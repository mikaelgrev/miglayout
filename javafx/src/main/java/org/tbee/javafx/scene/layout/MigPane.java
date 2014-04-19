package org.tbee.javafx.scene.layout;

import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import net.miginfocom.layout.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Manages nodes with MigLayout added via add(node, CC)
 *
 * @author Tom Eugelink
 *
 */
public class MigPane extends javafx.scene.layout.Pane
{
	protected final static String FXML_CC_KEY = "MigPane.cc";

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
	 * usee the string layout constraints
	 */
	public MigPane(String layoutConstraints) {
		super();
		setLayoutConstraints(ConstraintParser.parseLayoutConstraint(ConstraintParser.prepare(layoutConstraints)));
		construct();
	}

	/**
	 * usee the string layout constraints
	 */
	public MigPane(String layoutConstraints, String colConstraints) {
		super();
		setLayoutConstraints(ConstraintParser.parseLayoutConstraint(ConstraintParser.prepare(layoutConstraints)));
		setColumnConstraints(ConstraintParser.parseColumnConstraints(ConstraintParser.prepare(colConstraints)));
		construct();
	}

	/**
	 * usee the string layout constraints
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
		// defaults
		if (getLayoutConstraints() == null) setLayoutConstraints(new LC());
		if (getRowConstraints() == null) setRowConstraints(new AC());
		if (getColumnConstraints() == null) setColumnConstraints(new AC());

		// just in case when someone sneekly removes a child the JavaFX's way; prevent memory leaking
		getChildren().addListener(new ListChangeListener<Node>()
		{
			// as of JDK 1.6: @Override
			public void onChanged(Change<? extends Node> c)
			{
				while (c.next()) {
					for (Node node : c.getRemoved()) {
						// debug rectangles are not handled by miglayout, neither are not managed ones
						if (!node.isManaged())
							continue;

						// clean up. Iterate is fast enough and we don't have to have one more map.
						for (Map.Entry<FX2ComponentWrapper, CC> e : wrapperToCCMap.entrySet()) {
							if (e.getKey().getComponent() == node) {
								wrapperToCCMap.remove(e.getKey());
								break;
							}
						}
						invalidateGrid();
					}

					for (Node node : c.getAddedSubList()) {
						// debug rectangles are not handled by miglayout, neither are not managed ones
						if (!node.isManaged())
							continue;

						// get cc or use default
						CC cc = (CC) node.getProperties().remove(FXML_CC_KEY);

						// create wrapper information
						MigPane.this.wrapperToCCMap.put(new FX2ComponentWrapper(node), cc);

						invalidateGrid();
					}
				}
			}
		});
	}

	// ============================================================================================================
	// PANE
	//

	@Override
	protected double computeMinHeight(double width) {
		return computeHeight(width, LayoutUtil.MIN);
	}

	@Override
	protected double computeMinWidth(double height) {
		return computeWidth(height, LayoutUtil.MIN);
	}

	@Override
	protected double computePrefHeight(double width) {
		return computeHeight(width, LayoutUtil.PREF);
	}

	@Override
	protected double computePrefWidth(double height) {
		return computeWidth(height, LayoutUtil.PREF);
	}

	@Override
	protected double computeMaxHeight(double width) {
		return computeHeight(width, LayoutUtil.MAX);
	}

	@Override
	protected double computeMaxWidth(double height) {
		return computeWidth(height, LayoutUtil.MAX);
	}


	protected double computeWidth(double refHeight, int type) {
		int ins = getHorIns();
		int refSize = (int) Math.round(refHeight != -1 ? refHeight : getHeight()) - ins;
		return ins + LayoutUtil.getSizeSafe(getGrid().getWidth(refSize), type);
	}

	protected double computeHeight(double refWidth, int type) {
		int ins = getVerIns();
		int refSize = (int) Math.round(refWidth != -1 ? refWidth : getWidth()) - ins;
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
				if (ori != null)
					bias = ori;
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
	}
	public MigPane withLayoutConstraints(LC value) { setLayoutConstraints(value); return this; }
	private LC layoutConstraints = null;
	final static public String LAYOUTCONSTRAINTS_PROPERTY_ID = "layoutConstraints";
	//
	private boolean debug = false;

	/** ColumnConstraints: */
	public AC getColumnConstraints() { return this.columnConstraints; }
	public void setColumnConstraints(AC value) { this.columnConstraints = value; requestLayout();}
	public MigPane withColumnConstraints(AC value) { setColumnConstraints(value); return this; }
	private AC columnConstraints = null;
	final static public String COLUMNCONSTRAINTS_PROPERTY_ID = "columnConstraints";

	/** RowConstraints: */
	public AC getRowConstraints() { return this.rowConstraints; }
	public void setRowConstraints(AC value) { this.rowConstraints = value; requestLayout();}
	public MigPane withRowConstraints(AC value) { setRowConstraints(value); return this; }
	private AC rowConstraints = null;
	final static public String ROWCONSTRAINTS_PROPERTY_ID = "rowConstraints";


	// ============================================================================================================
	// CALLBACK

	private ArrayList<LayoutCallback> callbackList = null;

	/** Adds the callback function that will be called at different stages of the layout cylce.
	 * @param callback The callback. Not <code>null</code>.
	 */
	public void addLayoutCallback(LayoutCallback callback)
	{
		if (callback == null)
			throw new NullPointerException();

		if (callbackList == null)
			callbackList = new ArrayList<>(1);

		callbackList.add(callback);

		invalidateGrid();
	}

	/** Removes the callback if it exists.
	 * @param callback The callback. May be <code>null</code>.
	 */
	public void removeLayoutCallback(LayoutCallback callback)
	{
		if (callbackList != null)
			callbackList.remove(callback);
	}

	// ============================================================================================================
	// SCENE

	public void add(Node node, CC cc) {
		if (node.isManaged())
			wrapperToCCMap.put(new FX2ComponentWrapper(node), cc);
		getChildren().add(node);
	}

	public void add(Node node) {
		getChildren().add(node);
	}

	public void add(Node node, String sCc) {
		CC cc = ConstraintParser.parseComponentConstraint(ConstraintParser.prepare(sCc));
		add(node, cc);
	}


	// ============================================================================================================
	// LAYOUT

	// Store constraints. Key order important.
	final private LinkedHashMap<FX2ComponentWrapper, CC> wrapperToCCMap = new LinkedHashMap<FX2ComponentWrapper, CC>();

	/**
	 * This is where the actual layout happens
	 */
	protected void layoutChildren()	{
		performingLayout = true;

		// for debugging System.out.println("MigPane.layoutChildren");
		Grid lGrid = getGrid();

		// here the actual layout happens
		// this will use FX2ComponentWrapper.setBounds to actually place the components

		Insets ins = getInsets();
		int[] lBounds = new int[] {(int) Math.round(ins.getLeft()), (int) Math.round(ins.getTop()), (int) Math.ceil(getWidth() - getHorIns()), (int) Math.ceil(getHeight() - getVerIns())};
		lGrid.layout(lBounds, getLayoutConstraints().getAlignX(), getLayoutConstraints().getAlignY(), debug);

		// paint debug
		if (debug) {
			clearDebug();
			lGrid.paintDebug();
		}

		performingLayout = false;
	}

	@Override public void requestLayout() {
		if (performingLayout)
			return;

		biasDirty = true;
		if (_grid != null)
			_grid.invalidateContainerSize();

		super.requestLayout();
	}

	private Grid _grid;
	private boolean performingLayout = false;

	/*
	 * the _grid is valid if all hashcodes are unchanged
	 */
	private Grid getGrid() {

		if (_grid == null)
			_grid = new Grid(new FX2ContainerWrapper(this), getLayoutConstraints(), getRowConstraints(), getColumnConstraints(), wrapperToCCMap, callbackList);

		return _grid;
	}

	/** Removes the grid so it is recreated as needed next time. Should only be needed when the grid structure, or the interpretation of it,
	 * has changed.
	 */
	private void invalidateGrid()
	{
		_grid = null;
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
	class FX2ContainerWrapper extends FX2ComponentWrapper
		implements net.miginfocom.layout.ContainerWrapper {

		public FX2ContainerWrapper(Node node) {
			super(node);
		}

		// as of JDK 1.6: @Override
		public ComponentWrapper[] getComponents() {
			// for debugging System.out.println("MigPane.FX2ContainerWrapper.getComponents " + MigPane.this.componentWrapperList.size());
			return wrapperToCCMap.keySet().toArray(new FX2ComponentWrapper[wrapperToCCMap.size()]); // must be in the order of adding!
		}

		// as of JDK 1.6: @Override
		public int getComponentCount() {
			// for debugging System.out.println("MigPane.FX2ContainerWrapper.getComponentCount " + MigPane.this.wrapperToCCMap.size());
			return MigPane.this.wrapperToCCMap.size();
		}

		// as of JDK 1.6: @Override
		public Object getLayout() {
			return MigPane.this;
		}

		// as of JDK 1.6: @Override
		public boolean isLeftToRight() {
			NodeOrientation ori = getNodeOrientation();
			if (ori == NodeOrientation.INHERIT) {
				ContainerWrapper parent = getParent();
				if (parent != null)
					return parent.isLeftToRight();
			}
			return ori != NodeOrientation.RIGHT_TO_LEFT;
		}

		// as of JDK 1.6: @Override
		public void paintDebugCell(int x, int y, int w, int h) {
			addDebugRectangle((double)x, (double)y, (double)w, (double)h, DebugRectangleType.CELL);
		}

		// as of JDK 1.6: @Override
		public void paintDebugOutline(boolean useVisualPadding) {
			addDebugRectangle( 0, 0, getWidth(), getHeight(), DebugRectangleType.CONTAINER_OUTLINE);
		}
	}

	// ============================================================================================================
	// ComponentWrapper

	/*
	 * This class provides the data for MigLayout for a single component
	 */
	class FX2ComponentWrapper implements net.miginfocom.layout.ComponentWrapper {

		final protected Node node;

		// wrap this node
		public FX2ComponentWrapper(Node node) {
			this.node = node;
		}

		// get the wrapped node
		// as of JDK 1.6: @Override
		public Object getComponent() {
			return this.node;
		}

		// get the parent
		// as of JDK 1.6: @Override
		public ContainerWrapper getParent() {
			Parent parent = node.getParent();
			return parent != null ? new FX2ContainerWrapper(node.getParent()) : null;
		}

		// what type are we wrapping
		// as of JDK 1.6: @Override
		public int getComponentType(boolean arg0) {
			if (node instanceof TextField || node instanceof TextArea) {
				return TYPE_TEXT_FIELD;
			}
			else if (node instanceof Group) {
				return TYPE_CONTAINER;
			}
			else {
				return TYPE_UNKNOWN;
			}
		}

		// as of JDK 1.6: @Override
		public void setBounds(int x, int y, int width, int height) {
			// for debugging System.out.println(getComponent() + " FX2ComponentWrapper.setBound x="  + x + ",y=" + y + " / w=" + width + ",h=" + height + " / resizable=" + this.node.isResizable());
			this.node.resizeRelocate((double)x, (double)y, (double)width, (double)height);
		}

		// as of JDK 1.6: @Override
		public int getX() {
			int v = (int) node.getLayoutBounds().getMinX();
			return v;
		}

		// as of JDK 1.6: @Override
		public int getY() {
			int v = (int) node.getLayoutBounds().getMinY();
			return v;
		}

		// as of JDK 1.6: @Override
		public int getWidth() {
			// for debugging if (getComponent() instanceof MigLayoutFX2 == false) System.out.println(getComponent() + " getWidth " + node.getLayoutBounds().getWidth());
			int v = (int)Math.ceil(node.getLayoutBounds().getWidth());
			return v;
		}

		// as of JDK 1.6: @Override
		public int getMinimumWidth(int height) {
			int v = (int)Math.ceil(this.node.minWidth(height));
			// for debugging System.out.println(getComponent() + " getMinimumWidth " + v);
			return v;
		}

		// as of JDK 1.6: @Override
		public int getPreferredWidth(int height) {
			int v = (int)Math.ceil(this.node.prefWidth(height));
			// for debugging System.out.println(getComponent() + " getPreferredWidth " + v);
			return v;
		}

		// as of JDK 1.6: @Override
		public int getMaximumWidth(int height) {
			int v = (int)Math.ceil(this.node.maxWidth(height));
			if ( node instanceof javafx.scene.layout.Region
			     || node instanceof javafx.scene.control.Control // backwards compatibility with JavaFX2 (control does not extend Region there)
				) {
				v = Integer.MAX_VALUE;
				// for debugging System.out.println(getComponent() + " forced getMaximumWidth " + v); }
			}
			return v;
		}

		// as of JDK 1.6: @Override
		public int getHeight() {
			int v = (int)Math.ceil(node.getLayoutBounds().getHeight());
			return v;
		}

		// as of JDK 1.6: @Override
		public int getMinimumHeight(int width) {
			int v = (int)Math.ceil(this.node.minHeight(width));
			return v;
		}

		// as of JDK 1.6: @Override
		public int getPreferredHeight(int width) {
			int v = (int)Math.ceil(this.node.prefHeight(width));
			// for debugging System.out.println(getComponent() + " FX2ComponentWrapper.getPreferredHeight -> node.prefHeight(" + width + ")=" + this.node.prefHeight(width));
			return v;
		}

		// as of JDK 1.6: @Override
		public int getMaximumHeight(int width) {
			int v = (int)Math.ceil(this.node.maxHeight(width));
			if ( node instanceof javafx.scene.layout.Region
			     || node instanceof javafx.scene.control.Control // backwards compatibility with JavaFX2 (control does not extend Region there)
				) {
				v = Integer.MAX_VALUE;
				// for debugging System.out.println(getComponent() + " forced getMaximumHeight " + v); }
			}
			return v;
		}

		// as of JDK 1.6: @Override
		public int getBaseline(int width, int height) {
			return (int) Math.round(node.getBaselineOffset());
		}

		// as of JDK 1.6: @Override
		public int getScreenLocationX() {
			// this code is called when absolute layout is used
			Bounds lBoundsInSceneNode = node.localToScene(node.getBoundsInLocal());
			int v = (int) (node.getScene().getX() + node.getScene().getX() + lBoundsInSceneNode.getMinX());
			// for debugging System.out.println(getComponent() + " getScreenLocationX =" + v);
			return v;
		}

		// as of JDK 1.6: @Override
		public int getScreenLocationY() {
			// this code is called when absolute layout is used
			Bounds lBoundsInSceneNode = node.localToScene(node.getBoundsInLocal());
			int v = (int) (node.getScene().getY() + node.getScene().getY() + lBoundsInSceneNode.getMinY());
			// for debugging System.out.println(getComponent() + " getScreenLocationX =" + v);
			return v;
		}

		// as of JDK 1.6: @Override
		public int getScreenHeight() {
			// this code is never called?
			int v = (int) Math.ceil(Screen.getPrimary().getBounds().getHeight());
			// for debugging System.out.println(getComponent() + " getScreenHeight=" + v);
			return v;
		}

		// as of JDK 1.6: @Override
		public int getScreenWidth() {
			// this code is never called?
			int v = (int) Math.ceil(Screen.getPrimary().getBounds().getWidth());
			// for debugging System.out.println(getComponent() + " getScreenWidth=" + v);
			return v;
		}

		// as of JDK 1.6: @Override
		public int[] getVisualPadding() {
			return null;
		}

		// as of JDK 1.6: @Override
		public int getHorizontalScreenDPI() {
			return (int)Math.ceil(Screen.getPrimary().getDpi());
		}

		// as of JDK 1.6: @Override
		public int getVerticalScreenDPI() {
			return (int)Math.ceil(Screen.getPrimary().getDpi());
		}

		// as of JDK 1.6: @Override
		public float getPixelUnitFactor(boolean isHor) {
			switch (PlatformDefaults.getLogicalPixelBase()) {
				case PlatformDefaults.BASE_FONT_SIZE:
					return 1.0f; // todo

				case PlatformDefaults.BASE_SCALE_FACTOR:
					Float s = isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
					if (s == null)
						s = 1.0f;
					return s * (isHor ? getHorizontalScreenDPI() : getVerticalScreenDPI()) / (float) PlatformDefaults.getDefaultDPI();

				default:
					return 1f;
			}
		}

		// as of JDK 1.6: @Override
		public int getLayoutHashCode() {
			return 0; // Not used in MigPane.
		}

		// as of JDK 1.6: @Override
		public String getLinkId() {
			return node.getId();
		}

		// as of JDK 1.6: @Override
		public boolean hasBaseline() {
			// For some reason not resizable just return their height as the baseline, not BASELINE_OFFSET_SAME_AS_HEIGHT as logic would suggest.
			return node.isResizable() && node.getBaselineOffset() != BASELINE_OFFSET_SAME_AS_HEIGHT;
		}

		// as of JDK 1.6: @Override
		public boolean isVisible() {
			return node.isVisible();
		}

		public int getContentBias()
		{
			Orientation bias = node.getContentBias();
			return bias == null ? -1 : bias.ordinal(); // 0 == Orientation.HORIZONTAL and Orientation.HORIZONTAL, 1 = Orientation.VERTICAL and LayoutUtil.VERTICAL
		}

		// as of JDK 1.6: @Override
		public void paintDebugOutline(boolean useVisualPadding) {
			CC lCC = wrapperToCCMap.get(this);
			DebugRectangleType type = lCC != null && lCC.isExternal() ?  DebugRectangleType.EXTERNAL : DebugRectangleType.OUTLINE;
			addDebugRectangle(this.node.getLayoutX() + this.node.getLayoutBounds().getMinX(), (double)this.node.getLayoutY() + this.node.getLayoutBounds().getMinY(), getWidth(), getHeight(), type); // always draws node size, even if less is used
		}

		public int hashCode() {
			return node.hashCode();
		}

		/**
		 * This needs to be overridden so that different wrappers that hold the same component compare
		 * as equal.  Otherwise, Grid won't be able to layout the components correctly.
		 */
		public boolean equals(Object o) {
			if (!(o instanceof FX2ComponentWrapper)) {
				return false;
			}
			return getComponent().equals( ((FX2ComponentWrapper)o).getComponent() );
		}

	}
}
