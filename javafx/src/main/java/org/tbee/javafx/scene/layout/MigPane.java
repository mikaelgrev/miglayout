package org.tbee.javafx.scene.layout;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Window;
import net.miginfocom.layout.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * Manages nodes with MigLayout added via add(node, CC)
 *
 * @author Tom Eugelink
 *
 */
public class MigPane extends javafx.scene.layout.Pane
{
	protected final static String FXML_CC_KEY = "MigPane.cc";

	// We need to invalidate the grid since we can have a component with hidemode 3
	// We need to request layout since JavaFX doesn't do this.
	private final ChangeListener<Boolean> gridInvalidator = (observable, oldValue, newValue) -> {
		invalidateGrid();
		requestLayout();
	};

	// ============================================================================================================
	// CONSTRUCTOR

	/**
	 *
	 */
	public MigPane() {
		super();
		construct();
	}

	/**
	 * use the class layout constraints
	 */
	public MigPane(LC layoutConstraints) {
		super();
		setLayoutConstraints(layoutConstraints);
		construct();
	}

	/**
	 * use the class layout constraints
	 */
	public MigPane(LC layoutConstraints, AC colConstraints) {
		super();
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		construct();
	}

	/**
	 * use the class layout constraints
	 */
	public MigPane(LC layoutConstraints, AC colConstraints, AC rowConstraints) {
		super();
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		setRowConstraints(rowConstraints);
		construct();
	}

	/**
	 * use the string layout constraints
	 */
	public MigPane(String layoutConstraints) {
		super();
		setLayoutConstraints(ConstraintParser.parseLayoutConstraint(ConstraintParser.prepare(layoutConstraints)));
		construct();
	}

	/**
	 * use the string layout constraints
	 */
	public MigPane(String layoutConstraints, String colConstraints) {
		super();
		setLayoutConstraints(ConstraintParser.parseLayoutConstraint(ConstraintParser.prepare(layoutConstraints)));
		setColumnConstraints(ConstraintParser.parseColumnConstraints(ConstraintParser.prepare(colConstraints)));
		construct();
	}

	/**
	 * use the string layout constraints
	 */
	public MigPane(String layoutConstraints, String colConstraints, String rowConstraints) {
		super();
		setLayoutConstraints(ConstraintParser.parseLayoutConstraint(ConstraintParser.prepare(layoutConstraints)));
		setColumnConstraints(ConstraintParser.parseColumnConstraints(ConstraintParser.prepare(colConstraints)));
		setRowConstraints(ConstraintParser.parseRowConstraints(ConstraintParser.prepare(rowConstraints)));
		construct();
	}

	/*
	 *
	 */
	private void construct() {
		// When Scene changes the grid needs to be cleared
		sceneProperty().addListener(e -> invalidateGrid());

		// invalidate grid and request layout when node orientation changes
		nodeOrientationProperty().addListener(observable -> {
			invalidateGrid();
			requestLayout();
		});

		// defaults
		if (layoutConstraints == null) setLayoutConstraints(new LC());
		if (rowConstraints == null) setRowConstraints(new AC());
		if (columnConstraints == null) setColumnConstraints(new AC());

		// In case when someone sneakily removes a child the JavaFX way; prevent memory leaking
		getChildren().addListener((ListChangeListener<Node>) c -> {
			while (c.next()) {
				for (Node node : c.getRemoved()) {
					node.visibleProperty().removeListener(gridInvalidator);

					animateRemoved(node);

					int sizeBef = wrapperToCCMap.size();
					wrapperToCCMap.remove(new FXComponentWrapper(node));
					if (wrapperToCCMap.size() != sizeBef) { // Can't use the return from wrapperToCCMap since it might be null anyway if no CC.
						invalidateGrid();
					}
				}

				for (Node node : c.getAddedSubList()) {
					// debug rectangles are not handled by miglayout, neither are not managed ones
					if (!node.isManaged()) {
						continue;
					}
						
					// get cc or use default
					CC cc = (CC) node.getProperties().remove(FXML_CC_KEY);
					FXComponentWrapper wrapper = new FXComponentWrapper(node);

					// Only put the value if this comes from FXML or from direct list manipulation (not in wrapperToCCMap yet)
					if (cc != null || !wrapperToCCMap.containsKey(wrapper)) {
						wrapperToCCMap.put(wrapper, cc);
					}

					animateAdded(node);

					node.visibleProperty().addListener(gridInvalidator);

					invalidateGrid();
				}
			}
		});
	}

	// ============================================================================================================
	// PANE
	//

	@Override
	protected double computeMinWidth(double height) {
		return computeWidth(height, LayoutUtil.MIN);
	}

	@Override
	protected double computeMinHeight(double width) {
		return computeHeight(width, LayoutUtil.MIN);
	}

	@Override
	protected double computePrefWidth(double height) {
		return computeWidth(height, LayoutUtil.PREF);
	}

	@Override
	protected double computePrefHeight(double width) {
		return computeHeight(width, LayoutUtil.PREF);
	}

	@Override
	protected double computeMaxWidth(double height) {
		return computeWidth(height, LayoutUtil.MAX);
	}

	@Override
	protected double computeMaxHeight(double width) {
		return computeHeight(width, LayoutUtil.MAX);
	}


	protected double computeWidth(double refHeight, int type) {
		int ins = getHorIns();
		int refSize = (int) Math.ceil(refHeight != -1 ? refHeight : getHeight()) - ins;
		return ins + LayoutUtil.getSizeSafe(getGrid().getWidth(refSize), type);
	}

	protected double computeHeight(double refWidth, int type) {
		int ins = getVerIns();
		int refSize = (int) Math.ceil(refWidth != -1 ? refWidth : getWidth()) - ins;
		return ins + LayoutUtil.getSizeSafe(getGrid().getHeight(refSize), type);
	}

	private int getHorIns()
	{
		Insets insets = getInsets();
		return (int) Math.ceil(snapSpace(insets.getLeft()) + snapSpace(insets.getRight()));
	}

	private int getVerIns()
	{
		Insets insets = getInsets();
		return (int) Math.ceil(snapSpace(insets.getTop()) + snapSpace(insets.getBottom()));
	}

	private Orientation bias = null;
	private boolean biasDirty = true;
	private boolean debug = false;

	@Override
	public Orientation getContentBias() {
		if (biasDirty) {
			bias = null;
			for (Node child : getManagedChildren()) {
				Orientation ori = child.getContentBias();
				if (ori == Orientation.HORIZONTAL) {
					bias = Orientation.HORIZONTAL;
					break;
				}
				if (ori != null) {
					bias = ori;
				}
			}
			biasDirty = false;
		}
		return bias;
	}

	// ============================================================================================================
	// CONSTRAINTS

	/** LayoutConstraints: */
	public LC getLayoutConstraints() { return this.layoutConstraints; }
	public void setLayoutConstraints(LC lc)
	{
		this.layoutConstraints = lc;

		// Set debug. Clear it if LC is null.
		debug = lc != null && lc.getDebugMillis() > 0;
		invalidateGrid();
		requestLayout();
	}
	public MigPane withLayoutConstraints(LC value) { setLayoutConstraints(value); return this; }
	private LC layoutConstraints = null;
	final static public String LAYOUTCONSTRAINTS_PROPERTY_ID = "layoutConstraints";

	/** ColumnConstraints: */
	public AC getColumnConstraints() { return this.columnConstraints; }
	public void setColumnConstraints(AC value) { this.columnConstraints = value; invalidateGrid(); requestLayout();}
	public MigPane withColumnConstraints(AC value) { setColumnConstraints(value); return this; }
	private AC columnConstraints = null;
	final static public String COLUMNCONSTRAINTS_PROPERTY_ID = "columnConstraints";

	/** RowConstraints: */
	public AC getRowConstraints() { return this.rowConstraints; }
	public void setRowConstraints(AC value) { this.rowConstraints = value; invalidateGrid(); requestLayout();}
	public MigPane withRowConstraints(AC value) { setRowConstraints(value); return this; }
	private AC rowConstraints = null;
	final static public String ROWCONSTRAINTS_PROPERTY_ID = "rowConstraints";


	/** Returns the constraints for the node
	 * @return May be null which means all default constraints.
	 */
	public CC getComponentConstraints(Node node)
	{
		return wrapperToCCMap.get(new FXComponentWrapper(node));
	}

	/** Sets the constraints for the node
	 * @param node The node. Must already be in the pane.
	 * @param ccs The component constraints. 
	 */
	public void setComponentConstraints(Node node, String ccs)
	{
		CC cc = ConstraintParser.parseComponentConstraint(ConstraintParser.prepare(ccs));
		setComponentConstraints(node, cc);
	}

	/** Sets the constraints for the node
	 * @param node The node. Must already be in the pane.
	 * @param cc The component constraints. Can be null.
	 */
	public void setComponentConstraints(Node node, CC cc)
	{
		FXComponentWrapper wrapper = new FXComponentWrapper(node);
		if (!wrapperToCCMap.containsKey(wrapper)) {
			throw new IllegalArgumentException("Node not in pane: " + node);
		}
		
		wrapperToCCMap.put(wrapper, cc);

		invalidateGrid();
		requestLayout();
	}

	private LayoutAnimator anim = null;

	// ============================================================================================================
	// Animation

	private int animPrio = 0;

	/**
	 * @return If there is a current animator, that is returned. Otherwise a new one is created and returned. Never null.
	 */
	private LayoutAnimator getAnimator()
	{
		if (anim == null) {
			anim = new LayoutAnimator(this);
		}
		return anim;
	}

	/** Starts animation if there is one.
	 */
	private void startQueuedAnimations()
	{
		if (anim != null) {
			anim.start();
		}
	}

	public void animateAdded(Node node)
	{
		if (isNodeAnimated(node)) {
			getAnimator().nodeAdded(node);
		}
	}

	public void animateRemoved(Node node)
	{
		if (isNodeAnimated(node)) {
			getAnimator().nodeRemoved(node);
		}
	}

	public boolean animateBoundsChange(Node node, int x, int y, int width, int height)
	{
		if (!isNodeAnimated(node)) {
			return false;
		}

		getAnimator().animate(node, new Rectangle2D(x, y, width, height));
		return true;
	}

	private boolean isNodeAnimated(Node node)
	{
		if (!isVisible()) {
			return false;
		}

		CC cc = wrapperToCCMap.get(new FXComponentWrapper(node));
		int compPrio = cc != null ? cc.getAnimSpec().getPriority() : 0;

		return compPrio + (long) animPrio > 0;
	}

	// ============================================================================================================
	// CALLBACK

	private ArrayList<LayoutCallback> callbackList = null;

	/** Adds the callback function that will be called at different stages of the layout cycle.
	 * @param callback The callback. Not <code>null</code>.
	 */
	public void addLayoutCallback(LayoutCallback callback)
	{
		if (callback == null) {
			throw new NullPointerException();
		}

		if (callbackList == null) {
			callbackList = new ArrayList<>(1);
		}
		
		callbackList.add(callback);

		invalidateGrid();
	}

	/** Removes the callback if it exists.
	 * @param callback The callback. May be <code>null</code>.
	 */
	public void removeLayoutCallback(LayoutCallback callback)
	{
		if (callbackList != null) {
			callbackList.remove(callback);
		}
	}

	// ============================================================================================================
	// SCENE

	public MigPane add(Node node, CC cc) {
		if (node.isManaged()) {
			wrapperToCCMap.put(new FXComponentWrapper(node), cc);
		}
		getChildren().add(node);
		return this;
	}

	public MigPane add(Node node) {
		add(node, (CC) null);
		return this;
	}

	public MigPane add(Node node, String sCc) {
		CC cc = ConstraintParser.parseComponentConstraint(ConstraintParser.prepare(sCc));
		add(node, cc);
		return this;
	}

	public MigPane add(int index, Node node) {
		add(index, node, (CC) null);
		return this;
	}

	public MigPane add(int index, Node node, String sCc) {
		CC cc = ConstraintParser.parseComponentConstraint(ConstraintParser.prepare(sCc));
		add(index, node, cc);
		return this;
	}

	public MigPane add(int index, Node node, CC cc) {
		if (node.isManaged()) {
			wrapperToCCMap.put(new FXComponentWrapper(node), cc);
		}
		getChildren().add(index, node);
		return this;
	}

	public boolean remove(Node node)
	{
		return getChildren().remove(node);
	}

	public Node remove(int ix)
	{
		return getChildren().remove(ix);
	}


	// ============================================================================================================
	// LAYOUT

	// Store constraints. Key order important. Can have null values but all components that MigPane handles must be a key.
	final private LinkedHashMap<FXComponentWrapper, CC> wrapperToCCMap = new LinkedHashMap<>();

	private long lastSize = 0;

	/**
	 * This is where the actual layout happens
	 */
	@Override
	protected void layoutChildren()	{
		incLayoutInhibit();

		try {
			if (layoutConstraints.isNoCache()) {
				_grid = null;
			}

			// for debugging System.out.println("MigPane.layoutChildren");
			Grid lGrid = getGrid();

			// here the actual layout happens
			// this will use FXComponentWrapper.setBounds to actually place the components

			Insets ins = getInsets();
			int[] lBounds = new int[]{(int) ins.getLeft(), (int) ins.getTop(), (int) Math.ceil(getWidth() - getHorIns()), (int) Math.ceil(getHeight() - getVerIns())};
			lGrid.layout(lBounds, getLayoutConstraints().getAlignX(), getLayoutConstraints().getAlignY(), debug);

			// paint debug
			if (debug) {
				clearDebug();
				lGrid.paintDebug();
			}

			// Handle the "pack" keyword
			long newSize = lGrid.getHeight()[1] + (((long) lGrid.getWidth()[1]) << 32);
			if (lastSize != newSize) {
				lastSize = newSize;
				Platform.runLater(this::adjustWindowSize);
			}

			startQueuedAnimations();

		} finally {
			decLayoutInhibit();
		}
	}

	@Override
	protected void setWidth(double newWidth)
	{
		if (newWidth != getWidth()) {
			super.setWidth(newWidth);
			if (_grid != null) {
				_grid.invalidateContainerSize();
			}
		}
	}

	@Override
	protected void setHeight(double newHeight)
	{
		if (newHeight != getHeight()) {
			super.setHeight(newHeight);
			if (_grid != null) {
				_grid.invalidateContainerSize();
			}
		}
	}

	@Override
	public void requestLayout() {
		if (layoutInhibits > 0) {
			return;
		}

		biasDirty = true;
		if (_grid != null) {
			_grid.invalidateContainerSize();
		}

		super.requestLayout();
	}

	private Grid _grid;
	private int layoutInhibits = 0; // When > 0 request layouts should be inhibited

	void incLayoutInhibit()
	{
		layoutInhibits++;
	}

	void decLayoutInhibit()
	{
		layoutInhibits--;
	}

	/*
	 * the _grid is valid if all hash codes are unchanged
	 */
	private Grid getGrid() {

		if (_grid == null) {
			_grid = new Grid(new FXContainerWrapper(this), getLayoutConstraints(), getRowConstraints(), getColumnConstraints(), wrapperToCCMap, callbackList);
		}	
		return _grid;
	}

	/** Removes the grid so it is recreated as needed next time. Should only be needed when the grid structure, or the interpretation of it,
	 * has changed. Should normally don't have to be called by client code since this should be fully handled by MigPane.
	 */
	public void invalidateGrid()
	{
		_grid = null;
		biasDirty = true;
	}

	/** Checks the parent window/popup if its size is within parameters as set by the LC.
	 */
	private void adjustWindowSize()
	{
		BoundSize wBounds = layoutConstraints.getPackWidth();
		BoundSize hBounds = layoutConstraints.getPackHeight();
		Scene scene = getScene();
		Window window = scene != null ? scene.getWindow() : null;

		if (window == null || wBounds == BoundSize.NULL_SIZE && hBounds == BoundSize.NULL_SIZE) {
			return;
		}

		Parent root = scene.getRoot();

		double winWidth = window.getWidth();
		double winHeight = window.getHeight();

		double prefWidth = root.prefWidth(-1);
		double prefHeight = root.prefHeight(-1);
		FXContainerWrapper container = new FXContainerWrapper(root);

		double horIns = winWidth - scene.getWidth();
		double verIns = winHeight - scene.getHeight();

		double targetW = constrain(container, winWidth, prefWidth, wBounds) + horIns;
		double targetH = constrain(container, winHeight, prefHeight, hBounds) + verIns;

		double x = window.getX() - ((targetW - winWidth) * (1 - layoutConstraints.getPackWidthAlign()));
		double y = window.getY() - ((targetH - winHeight) * (1 - layoutConstraints.getPackHeightAlign()));

		window.setX(x);
		window.setY(y);
		window.setWidth(targetW);
		window.setHeight(targetH);
	}

	private double constrain(ContainerWrapper parent, double winSize, double prefSize, BoundSize constrain)
	{
		if (constrain == null) {
			return winSize;
		}

		double retSize = winSize;
		UnitValue wUV = constrain.getPreferred();
		if (wUV != null) {
			retSize = wUV.getPixels((float) prefSize, parent, parent);
		}

		retSize = constrain.constrain((int) Math.ceil(retSize), (float) prefSize, parent);

		return constrain.getGapPush() ? Math.max(winSize, retSize) : retSize;
	}

	@Override
	public boolean usesMirroring() {
		// do not use mirroring transformation for right-to-left node orientation
		return false;
	}

	// ============================================================================================================
	// DEBUG

	public void clearDebug() {
		// for debugging System.out.println("clearDebug");
		MigPane.this.getChildren().removeAll(this.debugRectangles);
		this.debugRectangles.clear();
	}
	final private List<Node> debugRectangles = new ArrayList<Node>();

	private void addDebugRectangle(double x, double y, double w, double h, DebugRectangleType type)
	{
		DebugRectangle lRectangle = new DebugRectangle( snap(x), snap(y), snap(x + w - 1) - snap(x), snap(y + h - 1) - snap(y) );

		if (type == DebugRectangleType.CELL) {
			//System.out.print(getId() + ": " + "paintDebugCell ");
			lRectangle.setStroke(getDebugCellColor());
			lRectangle.getStrokeDashArray().addAll(3d,3d);
		}
		else if (type == DebugRectangleType.EXTERNAL) {
			//System.out.print(getId() + ": " + "paintDebugExternal ");
			lRectangle.setStroke(getDebugExternalColor());
			lRectangle.getStrokeDashArray().addAll(5d,5d);
		}
		else if (type == DebugRectangleType.OUTLINE) {
			//System.out.print(getId() + ": " + "paintDebugOutline ");
			lRectangle.setStroke(getDebugOutlineColor());
			lRectangle.getStrokeDashArray().addAll(4d,4d);
		}
		else if (type == DebugRectangleType.CONTAINER_OUTLINE) {
			//System.out.print(getId() + ": " + "paintDebugContainerOutline ");
			lRectangle.setStroke(getDebugContainerOutlineColor());
			lRectangle.getStrokeDashArray().addAll(7d,7d);
		}
		else {
			throw new IllegalStateException("Unknown debug rectangle type");
		}
		// for debugging System.out.println(lRectangle.getX() + "," + lRectangle.getY() + "/" + lRectangle.getWidth() + "x" + lRectangle.getHeight());
		//lRectangle.setStrokeWidth(0.5f);
		lRectangle.setFill(null);
		lRectangle.mouseTransparentProperty().set(true); // just to be sure

		// add as child
		MigPane.this.getChildren().add(lRectangle);
		this.debugRectangles.add(lRectangle);
	}
	private enum DebugRectangleType { CELL, OUTLINE, CONTAINER_OUTLINE, EXTERNAL }

	class DebugRectangle extends Rectangle
	{
		public DebugRectangle(double x, double y, double w, double h)
		{
			super(x,y,w,h);
			setManaged(false);
		}
	}

	private double snap(double v) {
		return ((int) v) + .5;
	}


	/** debugCellColor */
	public Color getDebugCellColor() { return this.debugCellColor; }
	public void setDebugCellColor(Color value) { this.debugCellColor = value; }
	private Color debugCellColor = Color.RED;

	/** debugExternalColor */
	public Color getDebugExternalColor() { return this.debugExternalColor; }
	public void setDebugExternalColor(Color value) { this.debugExternalColor = value; }
	private Color debugExternalColor = Color.BLUE;

	/** debugOutlineColor */
	public Color getDebugOutlineColor() { return this.debugOutlineColor; }
	public void setDebugOutlineColor(Color value) { this.debugOutlineColor = value; }
	private Color debugOutlineColor = Color.GREEN;

	/** debugContainerOutlineColor */
	public Color getDebugContainerOutlineColor() { return this.debugContainerOutlineColor; }
	public void setDebugContainerOutlineColor(Color value) { this.debugContainerOutlineColor = value; }
	private Color debugContainerOutlineColor = Color.PURPLE;

	// ============================================================================================================
	// ContainerWrapper

	/*
	 * This class provides the data for MigLayout for the container
	 */
	class FXContainerWrapper extends FXComponentWrapper
		implements net.miginfocom.layout.ContainerWrapper {

		public FXContainerWrapper(Parent node) {
			super(node);
		}

		@Override
		public FXComponentWrapper[] getComponents() {
			// for debugging System.out.println("MigPane.FXContainerWrapper.getComponents " + MigPane.this.componentWrapperList.size());
//			return getManagedChildren().stream().map(node -> new FXComponentWrapper(node)).toArray(FXComponentWrapper[]::new);
			List<FXComponentWrapper> lFXComponentWrappers = new ArrayList<>();
			for (Node node : getManagedChildren()) {
				lFXComponentWrappers.add(new FXComponentWrapper(node));
			}
			return lFXComponentWrappers.toArray(new FXComponentWrapper[]{});
		}

		@Override
		public int getComponentCount() {
			// for debugging System.out.println("MigPane.FXContainerWrapper.getComponentCount " + MigPane.this.wrapperToCCMap.size());
			return MigPane.this.wrapperToCCMap.size();
		}

		@Override
		public Object getLayout() {
			return MigPane.this;
		}

		@Override
		public boolean isLeftToRight() {
			return getEffectiveNodeOrientation() != NodeOrientation.RIGHT_TO_LEFT;
		}

		@Override
		public void paintDebugCell(int x, int y, int w, int h) {
			addDebugRectangle((double)x, (double)y, (double)w, (double)h, DebugRectangleType.CELL);
		}

		@Override
		public void paintDebugOutline(boolean useVisualPadding) {
			addDebugRectangle( 0, 0, getWidth(), getHeight(), DebugRectangleType.CONTAINER_OUTLINE);
		}
	}

	// ============================================================================================================
	// ComponentWrapper

	/*
	 * This class provides the data for MigLayout for a single component
	 */
	class FXComponentWrapper implements net.miginfocom.layout.ComponentWrapper
	{

		final protected Node node;

		// wrap this node
		public FXComponentWrapper(Node node)
		{
			this.node = node;
		}

		// get the wrapped node
		@Override
		public Object getComponent()
		{
			return this.node;
		}

		// get the parent
		@Override
		public ContainerWrapper getParent()
		{
			Parent parent = node.getParent();
			return parent != null ? new FXContainerWrapper(node.getParent()) : null;
		}

		// what type are we wrapping
		@Override
		public int getComponentType(boolean arg0)
		{
			if (node instanceof TextField || node instanceof TextArea) {
				return TYPE_TEXT_FIELD;
			} else if (node instanceof Group) {
				return TYPE_CONTAINER;
			} else {
				return TYPE_UNKNOWN;
			}
		}

		@Override
		public int getX()
		{
			int v = (int) node.getLayoutX();
			return v;
		}

		@Override
		public int getY()
		{
			int v = (int) node.getLayoutY();
			return v;
		}

		@Override
		public int getWidth()
		{
			// for debugging if (getComponent() instanceof MigPane == false) System.out.println(getComponent() + " getWidth " + node.getLayoutBounds().getWidth());
			int v = (int) Math.ceil(node.getLayoutBounds().getWidth());
			return v;
		}

		@Override
		public int getMinimumWidth(int height)
		{
			int v = (int) Math.ceil(this.node.minWidth(height));
			// for debugging System.out.println(getComponent() + " getMinimumWidth " + v);
			return v;
		}

		@Override
		public int getPreferredWidth(int height)
		{
			int v = (int) Math.ceil(this.node.prefWidth(height));
			// for debugging System.out.println(getComponent() + " getPreferredWidth " + v);
			return v;
		}

		@Override
		public int getMaximumWidth(int height)
		{
			// backwards compatibility with JavaFX2 (control does not extend Region there)
			if (node instanceof Region || node instanceof Control) {
				double prefWidth = node instanceof Region ? ((Region) node).getMaxWidth() : ((Control) node).getMaxWidth();
				if (prefWidth == USE_COMPUTED_SIZE || prefWidth == USE_PREF_SIZE) {
					return LayoutUtil.INF;
				}
			}
			return (int) Math.ceil(node.maxWidth(height));
		}

		@Override
		public int getHeight()
		{
			int v = (int) Math.ceil(node.getLayoutBounds().getHeight());
			return v;
		}

		@Override
		public int getMinimumHeight(int width)
		{
			int v = (int) Math.ceil(this.node.minHeight(width));
			return v;
		}

		@Override
		public int getPreferredHeight(int width)
		{
			int v = (int) Math.ceil(this.node.prefHeight(width));
			// for debugging System.out.println(getComponent() + " FXComponentWrapper.getPreferredHeight -> node.prefHeight(" + width + ")=" + this.node.prefHeight(width));
			return v;
		}

		@Override
		public int getMaximumHeight(int width)
		{
			// backwards compatibility with JavaFX2 (control does not extend Region there)
			if (node instanceof Region || node instanceof Control) {
				double prefWidth = node instanceof Region ? ((Region) node).getMaxHeight() : ((Control) node).getMaxHeight();
				if (prefWidth == USE_COMPUTED_SIZE || prefWidth == USE_PREF_SIZE) {
					return LayoutUtil.INF;
				}
			}
			return (int) Math.ceil(node.maxHeight(width));
		}

		@Override
		public int getBaseline(int width, int height)
		{
			return (int) Math.round(node.getBaselineOffset());
		}

		@Override
		public boolean hasBaseline()
		{
			// For some reason not resizable just return their height as the baseline, not BASELINE_OFFSET_SAME_AS_HEIGHT as logic would suggest.
			// For more info : https://bugs.openjdk.java.net/browse/JDK-8091288
			return node.isResizable() && node.getBaselineOffset() != BASELINE_OFFSET_SAME_AS_HEIGHT;
		}

		@Override
		public int getScreenLocationX()
		{
			// this code is called when absolute layout is used
			Bounds lBoundsInSceneNode = node.localToScene(node.getBoundsInLocal());
			int v = (int) (node.getScene().getX() + node.getScene().getX() + lBoundsInSceneNode.getMinX());
			// for debugging System.out.println(getComponent() + " getScreenLocationX =" + v);
			return v;
		}

		@Override
		public int getScreenLocationY()
		{
			// this code is called when absolute layout is used
			Bounds lBoundsInSceneNode = node.localToScene(node.getBoundsInLocal());
			int v = (int) (node.getScene().getY() + node.getScene().getY() + lBoundsInSceneNode.getMinY());
			// for debugging System.out.println(getComponent() + " getScreenLocationX =" + v);
			return v;
		}

		@Override
		public int getScreenHeight()
		{
			// this code is never called?
			int v = (int) Math.ceil(Screen.getPrimary().getBounds().getHeight());
			// for debugging System.out.println(getComponent() + " getScreenHeight=" + v);
			return v;
		}

		@Override
		public int getScreenWidth()
		{
			// this code is never called?
			int v = (int) Math.ceil(Screen.getPrimary().getBounds().getWidth());
			// for debugging System.out.println(getComponent() + " getScreenWidth=" + v);
			return v;
		}

		@Override
		public int[] getVisualPadding()
		{
			return null;
		}

		@Override
		public int getHorizontalScreenDPI()
		{
			// todo All references to Screen.getPrimary() should be replaced with getting the actual screen the Node is on.
			return (int) Math.ceil(Screen.getPrimary().getDpi());
		}

		@Override
		public int getVerticalScreenDPI()
		{
			// todo All references to Screen.getPrimary() should be replaced with getting the actual screen the Node is on.
			return (int) Math.ceil(Screen.getPrimary().getDpi());
		}

		@Override
		public float getPixelUnitFactor(boolean isHor)
		{
			switch (PlatformDefaults.getLogicalPixelBase()) {
				case PlatformDefaults.BASE_FONT_SIZE:
					return 1.0f; // todo

				case PlatformDefaults.BASE_SCALE_FACTOR:
					Float s = isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
					if (s == null) {
						s = 1.0f;
					}

					return s * (isHor ? getHorizontalScreenDPI() : getVerticalScreenDPI()) / (float) PlatformDefaults.getDefaultDPI();

				default:
					return 1f;
			}
		}

		@Override
		public int getLayoutHashCode()
		{
			return 0; // Not used in MigPane.
		}

		@Override
		public String getLinkId()
		{
			return node.getId();
		}

		@Override
		public boolean isVisible()
		{
			return node.isVisible();
		}

		@Override
		public int getContentBias()
		{
			Orientation bias = node.getContentBias();
			return bias == null ? -1 : bias.ordinal(); // 0 == Orientation.HORIZONTAL and Orientation.HORIZONTAL, 1 = Orientation.VERTICAL and LayoutUtil.VERTICAL
		}

		@Override
		public void paintDebugOutline(boolean useVisualPadding)
		{
			CC lCC = wrapperToCCMap.get(this);
			DebugRectangleType type = lCC != null && lCC.isExternal() ? DebugRectangleType.EXTERNAL : DebugRectangleType.OUTLINE;
			addDebugRectangle(this.node.getLayoutX() + this.node.getLayoutBounds().getMinX(), (double) this.node.getLayoutY() + this.node.getLayoutBounds().getMinY(), getWidth(), getHeight(), type); // always draws node size, even if less is used
		}

		@Override
		public int hashCode()
		{
			return node.hashCode();
		}

		/**
		 * This needs to be overridden so that different wrappers that hold the same component compare
		 * as equal.  Otherwise, Grid won't be able to layout the components correctly.
		 */
		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof FXComponentWrapper)) {
				return false;
			}

			return getComponent().equals(((FXComponentWrapper) o).getComponent());
		}

		@Override
		public void setBounds(int x, int y, int width, int height)
		{
			//			System.out.println(getComponent() + " FXComponentWrapper.setBound x="  + x + ",y=" + y + " / w=" + width + ",h=" + height + " / resizable=" + this.node.isResizable());
			//			System.out.println("x: " + x + ", y: " + y);
			//			CC cc = wrapperToCCMap.get(this);

			if (!animateBoundsChange(node, x, y, width, height)) {
				node.resizeRelocate((double) x, (double) y, (double) width, (double) height);
			}
		}
	}
}
