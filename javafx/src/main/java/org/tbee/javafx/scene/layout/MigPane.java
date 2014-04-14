package org.tbee.javafx.scene.layout;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import java.util.*;


/**
 * Manages nodes with MigLayout added via add(node, CC)
 *
 * @author Tom Eugelink
 *
 */
public class MigPane extends javafx.scene.layout.Pane implements ChangeListener<Boolean>
{
	// Listens for child component layout is needed changes.
	public void changed(ObservableValue observable, Boolean oldIsInvalid, Boolean newIsInvalid)
	{
		if (newIsInvalid) {
			// TODO Possible optimization to only invalidate if new FX2ComponentWrapper(node).getLayoutCahsCode() has changed and that one asks min/pref/max sizes and some more.
//			System.out.println("invalidate: layout invalidated in child");
			invalidateGrid();
		}
	}

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

						if (node instanceof Parent)
							((Parent) node).needsLayoutProperty().removeListener(MigPane.this);

						// clean up. Iterate is fast enough and we don't have to have one more map.
						for (Map.Entry<FX2ComponentWrapper, CC> e : wrapperToCCMap.entrySet()) {
							if (e.getKey().getComponent() == node) {
								wrapperToCCMap.remove(e.getKey());
								break;
							}
						}

						// _grid is invalid
//						System.out.println("invalidate: Node removed");
						invalidateGrid();
						biasDirty = true;
					}

					for (Node node : c.getAddedSubList()) {
						// debug rectangles are not handled by miglayout, neither are not managed ones
						if (!node.isManaged())
							continue;

						if (node instanceof Parent)
							((Parent) node).needsLayoutProperty().addListener(MigPane.this);

						// get cc or use default
						CC cc = cNodeToCC.remove(node);

						// create wrapper information
						FX2ComponentWrapper lFX2ComponentWrapper = new FX2ComponentWrapper(node);
						MigPane.this.wrapperToCCMap.put(lFX2ComponentWrapper, cc);

						// _grid is invalid
//						System.out.println("invalidate: Node added");
						invalidateGrid();
						biasDirty = true;
					}
				}
			}
		});
	}

	final static protected Map<Node, CC> cNodeToCC = new WeakHashMap<>();


	// ============================================================================================================
	// PANE
	//

	@Override
	protected double computeMaxHeight(double width) {
		return computeHeight(width, LayoutUtil.MAX);
	}

	@Override
	protected double computeMaxWidth(double height) {
		return computeWidth(height, LayoutUtil.MAX);
	}

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

	protected double computeWidth(double refHeight, int type) {
		int ins = getHorIns();
		return ins + LayoutUtil.getSizeSafe(getValidGrid(gridWidth, refHeight - ins).getWidth(), type);
	}

	protected double computeHeight(double refWidth, int type) {
		int ins = getVerIns();
		return ins + LayoutUtil.getSizeSafe(getValidGrid(refWidth - ins, gridHeight).getHeight(), type);
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
		invalidateGrid();

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
	public void setColumnConstraints(AC value) { this.columnConstraints = value; invalidateGrid();}
	public MigPane withColumnConstraints(AC value) { setColumnConstraints(value); return this; }
	private AC columnConstraints = null;
	final static public String COLUMNCONSTRAINTS_PROPERTY_ID = "columnConstraints";

	/** RowConstraints: */
	public AC getRowConstraints() { return this.rowConstraints; }
	public void setRowConstraints(AC value) { this.rowConstraints = value; invalidateGrid();}
	public MigPane withRowConstraints(AC value) { setRowConstraints(value); return this; }
	private AC rowConstraints = null;
	final static public String ROWCONSTRAINTS_PROPERTY_ID = "rowConstraints";

	// ============================================================================================================
	// SCENE

	public void add(Node node, CC cc) {
		if (node.isManaged())
			cNodeToCC.put(node, cc);
		getChildren().add(node);
	}

	public void add(Node node) {
		getChildren().add(node);
	}

	public void add(Node node, String cc) {
		if (node.isManaged()) {
			CC lCC = ConstraintParser.parseComponentConstraint(ConstraintParser.prepare(cc));
			// do regular add
			add(node, lCC);
		} else {
			getChildren().add(node);
		}
	}


	// ============================================================================================================
	// LAYOUT

	// store of constraints
	final private LinkedHashMap<FX2ComponentWrapper, CC> wrapperToCCMap = new LinkedHashMap<>();

	/**
	 * This is where the actual layout happens
	 */
	protected void layoutChildren()	{
		// for debugging System.out.println("MigPane.layoutChildren");
		Insets insets = getInsets();
		double horIns = getHorIns();
		double verIns = getVerIns();
		Grid lGrid = getValidGrid(getWidth() - horIns, getHeight() - verIns);

		// here the actual layout happens
		// this will use FX2ComponentWrapper.setBounds to actually place the components

		int[] lBounds = new int[]{ (int) Math.round(insets.getLeft()), (int) Math.round(insets.getTop()), (int) Math.ceil(getWidth() - horIns), (int) Math.ceil(getHeight() - verIns)};
		lGrid.layout(lBounds, getLayoutConstraints().getAlignX(), getLayoutConstraints().getAlignY(), debug, false);

		// paint debug
		if (debug) {
			clearDebug();
			lGrid.paintDebug();
		}
	}

	private void invalidateGrid()
	{
		_grid = null;
	}

	private Grid _grid;
	private int gridModCount = 0;
	private double gridWidth = -1, gridHeight = -1;

	/*
	 * the _grid is valid if all hashcodes are unchanged
	 */
	private Grid getValidGrid(double refWidth, double refHeight) {

		if (PlatformDefaults.getModCount() != gridModCount) {
			gridModCount = PlatformDefaults.getModCount();
			invalidateGrid();
		}

		Orientation bias = getContentBias();
		if (bias != null && (refWidth != gridWidth || refHeight != gridHeight)) {
//			System.out.println("invalidate: new width & height.  w: " + refWidth + ", gridW: " + gridWidth + " h: " + refHeight + ", gridH: " + gridHeight);
			invalidateGrid();
		}

		if (_grid == null) {
			gridWidth = refWidth;
			gridHeight = refHeight;
			_grid = new Grid(new FX2ContainerWrapper(this), getLayoutConstraints(), getRowConstraints(), getColumnConstraints(), wrapperToCCMap, null);
		}

		return _grid;
	}

	// ============================================================================================================
	// DEBUG

	/*
	 *
	 */
	public void clearDebug() {
		// for debugging System.out.println("clearDebug");
		MigPane.this.getChildren().removeAll(this.debugRectangles);
		this.debugRectangles.clear();
	}
	final private List<Node> debugRectangles = new ArrayList<>();

	/*
	 *
	 */
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

		// wrap this node
		public FX2ComponentWrapper(Node node) {
			this.node = node;
		}
		final protected Node node;

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
		public float getPixelUnitFactor(boolean arg0) {
			return 1.0f; // TODO
		}

		// as of JDK 1.6: @Override
		public int getLayoutHashCode() {
			// Not used in MigPane.
			return 0;
//			int lHashCode = 0;
//			lHashCode += ((int)this.node.getLayoutBounds().getWidth()) + (((int)this.node.getLayoutBounds().getHeight()) * 32); // << 0, << 5
//			if (this.node.isVisible()) {
//				lHashCode += 1324511;
//			}
//
//			String id = node.getId();
//			if (id != null) {
//				lHashCode += this.node.getId().hashCode();
//			}
//			return lHashCode; // 0;
		}

		// as of JDK 1.6: @Override
		public String getLinkId() {
			return node.getId();
		}

		// as of JDK 1.6: @Override
		public boolean hasBaseline() {
			return node.getBaselineOffset() != BASELINE_OFFSET_SAME_AS_HEIGHT;
		}

		// as of JDK 1.6: @Override
		public boolean isVisible() {
			return node.isVisible();
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
