package org.tbee.javafx.scene.layout;

import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.ContainerWrapper;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.LayoutUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Manages nodes with MigLayout added via add(node, CC)
 *
 * @author Tom Eugelink
 *
 */
public class MigPane extends javafx.scene.layout.Pane
{
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
		setLayoutConstraints( ConstraintParser.parseLayoutConstraint( ConstraintParser.prepare( layoutConstraints ) ) );
		construct();
	}

	/**
	 * usee the string layout constraints
	 */
	public MigPane(String layoutConstraints, String colConstraints) {
		super();
		setLayoutConstraints( ConstraintParser.parseLayoutConstraint( ConstraintParser.prepare( layoutConstraints ) ) );
		setColumnConstraints( ConstraintParser.parseColumnConstraints( ConstraintParser.prepare( colConstraints ) ) );
		construct();
	}

	/**
	 * usee the string layout constraints
	 */
	public MigPane(String layoutConstraints, String colConstraints, String rowConstraints) {
		super();
		setLayoutConstraints( ConstraintParser.parseLayoutConstraint( ConstraintParser.prepare( layoutConstraints ) ) );
		setColumnConstraints( ConstraintParser.parseColumnConstraints( ConstraintParser.prepare( colConstraints ) ) );
		setRowConstraints( ConstraintParser.parseRowConstraints( ConstraintParser.prepare( rowConstraints ) ) );
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

		// the container wrapper
		this.fx2ContainerWrapper = new FX2ContainerWrapper(this);

		// just in case when someone sneekly removes a child the JavaFX's way; prevent memory leaking
		getChildren().addListener(new ListChangeListener<Node>()
		{
			// as of JDK 1.6: @Override
			public void onChanged(Change<? extends Node> c)
			{
				while (c.next())
				{
					for (Node node : c.getRemoved())
					{
						// debug rectangles are not handled by miglayout
						if (node instanceof DebugRectangle) continue;

						// clean up
						FX2ComponentWrapper lFX2ComponentWrapper = MigPane.this.nodeToComponentWrapperMap.remove(node);
						componentWrapperList.remove(lFX2ComponentWrapper);
						fx2ComponentWrapperToCCMap.remove(lFX2ComponentWrapper);

						// grid is invalid
						invalidateMigLayoutGrid();
					}

					for (Node node : c.getAddedSubList())
					{
						// debug rectangles are not handled by miglayout
						if (node instanceof DebugRectangle) continue;

						// get cc or use default
						CC cc = cNodeToCC.remove(node);
						if (cc == null) cc = new CC();

						// create wrapper information
						FX2ComponentWrapper lFX2ComponentWrapper = new FX2ComponentWrapper(node);
						MigPane.this.componentWrapperList.add(lFX2ComponentWrapper);
						MigPane.this.nodeToComponentWrapperMap.put(node, lFX2ComponentWrapper);
						MigPane.this.fx2ComponentWrapperToCCMap.put(lFX2ComponentWrapper, cc );

						// grid is invalid
						invalidateMigLayoutGrid();
					}
				};
				
				// if invalid, create
				if (!isMiglayoutGridValid()) {
					createMigLayoutGrid();
				}
			}
		});

		// create the initial grid so it won't be null
		createMigLayoutGrid();
	}
	private FX2ContainerWrapper fx2ContainerWrapper;
	final static protected Map<Node, CC> cNodeToCC = new WeakHashMap<Node, CC>();


	// ============================================================================================================
	// SCENE

	/** LayoutConstraints: */
	public LC getLayoutConstraints() { return this.layoutConstraints; }
	public void setLayoutConstraints(LC value)
	{
		this.layoutConstraints = value;

		// if debug is set, do it
		if (value != null && value.getDebugMillis() > 0) {
			iDebug = true;
		}
	}
	public MigPane withLayoutConstraints(LC value) { setLayoutConstraints(value); return this; }
	volatile private LC layoutConstraints = null;
	final static public String LAYOUTCONSTRAINTS_PROPERTY_ID = "layoutConstraints";
	//
	volatile private boolean iDebug = false;

	/** ColumnConstraints: */
	public AC getColumnConstraints() { return this.columnConstraints; }
	public void setColumnConstraints(AC value) { this.columnConstraints = value; }
	public MigPane withColumnConstraints(AC value) { setColumnConstraints(value); return this; }
	volatile private AC columnConstraints = null;
	final static public String COLUMNCONSTRAINTS_PROPERTY_ID = "columnConstraints";

	/** RowConstraints: */
	public AC getRowConstraints() { return this.rowConstraints; }
	public void setRowConstraints(AC value) { this.rowConstraints = value; }
	public MigPane withRowConstraints(AC value) { setRowConstraints(value); return this; }
	volatile private AC rowConstraints = null;
	final static public String ROWCONSTRAINTS_PROPERTY_ID = "rowConstraints";

	// ============================================================================================================
	// SCENE

	/**
	 * @param node
	 * @param cc
	 */
	public void add(Node node, CC cc) {
		cNodeToCC.put(node, cc);
		getChildren().add(node);
	}

	/**
	 *
	 * @param node
	 */
	public void add(Node node) {
		add(node, new CC());
	}

	/**
	 *
	 * @param node
	 * @param cc
	 */
	public void add(Node node, String cc) {
		// parse as CC
		CC lCC = ConstraintParser.parseComponentConstraint( ConstraintParser.prepare( cc ) );

		// do regular add
		add(node, lCC);
	}


	// ============================================================================================================
	// LAYOUT

	// store of constraints
	final private List<FX2ComponentWrapper> componentWrapperList = new ArrayList<FX2ComponentWrapper>();
	final private Map<Node, FX2ComponentWrapper> nodeToComponentWrapperMap = new WeakHashMap<Node, FX2ComponentWrapper>();
	final private Map<ComponentWrapper, CC> fx2ComponentWrapperToCCMap = new WeakHashMap<ComponentWrapper, CC>();
	final private Map<Node, Integer> nodeToHashcodeMap = new WeakHashMap<Node, Integer>();

	/**
	 * This is where the actual layout happens
	 */
	protected void layoutChildren()	{
		// for debugging System.out.println("MigPane.layoutChildren");
		super.layoutChildren();

		// validate if the grid should be recreated
		validateMigLayoutGrid();

		// here the actual layout happens
		// this will use FX2ComponentWrapper.setBounds to actually place the components
		int[] lBounds = new int[]{ 0, 0, (int)Math.ceil(getWidth()), (int)Math.ceil(getHeight())};
		this.grid.layout( lBounds, getLayoutConstraints().getAlignX(), getLayoutConstraints().getAlignY(), iDebug, true );

        // paint debug
        if (iDebug) {
	        clearDebug();
	        this.grid.paintDebug();
        }
	}

	/*
	 *
	 */
	private void createMigLayoutGrid() {
		// for debugging System.out.println("createMigLayoutGrid");
        this.grid = new Grid( fx2ContainerWrapper, getLayoutConstraints(), getRowConstraints(), getColumnConstraints(), fx2ComponentWrapperToCCMap, null );
        // for debugging System.out.println("MigPane.createMigLayoutGrid height=" + Arrays.toString( grid.getHeight() ) );        
        this.valid = true;
        // for debugging System.out.println("MigPane.createMigLayoutGrid valid=" + valid );        

//        // -----------------------------------------
//        // set MigLayout's own size
//        setMinWidth(LayoutUtil.getSizeSafe(grid.getWidth(), LayoutUtil.MIN));
//        System.out.println("MigPane.setMinWidth " + getMinWidth());
//        setPrefWidth(LayoutUtil.getSizeSafe(grid.getWidth(), LayoutUtil.PREF));
//        System.out.println("MigPane.setPrefWidth " + getPrefWidth());
//        setMaxWidth(LayoutUtil.getSizeSafe(grid.getWidth(), LayoutUtil.MAX));
//        System.out.println("MigPane.setMaxWidth " + getMaxWidth());
//
//        setMinHeight(LayoutUtil.getSizeSafe(grid.getHeight(), LayoutUtil.MIN)); // 14 + button's height is 21, that is exactly what we need
//        System.out.println("MigPane.setMinHeight " + getMinHeight());
//        setPrefHeight(LayoutUtil.getSizeSafe(grid.getHeight(), LayoutUtil.PREF));
//setPrefHeight(35);
//        System.out.println("MigPane.setPrefHeight " + getPrefHeight());
//        setMaxHeight(LayoutUtil.getSizeSafe(grid.getHeight(), LayoutUtil.MAX));
//        System.out.println("MigPane.setMaxHeight " + getMaxHeight());
//        // -----------------------------------------
	}
	volatile private Grid grid;

	/*
	 * the grid is valid if all hashcodes are unchanged
	 */
	private void validateMigLayoutGrid() {

		// only needed if the grid is valid
		if (isMiglayoutGridValid()) {

			// scan all childeren
			for (Node lChild : getChildren()) {

				// if this child is managed by MigLayout
				if (nodeToComponentWrapperMap.containsKey(lChild)) {

					// get its previous hashcode
					Integer lPreviousHashcode = nodeToHashcodeMap.get(lChild);

					// calculate its current hashcode
					Integer lCurrentHashcode = calculateHashcode(lChild);

					// if it is not the same
					if (lPreviousHashcode == null || !lPreviousHashcode.equals(lCurrentHashcode)) {

						// invalidate the grid
						invalidateMigLayoutGrid();

						// remember the new hashcode
						nodeToHashcodeMap.put(lChild, Integer.valueOf(lCurrentHashcode));
					}
				}
			}
		}

		// if invalid, create
		if (!isMiglayoutGridValid()) {
			createMigLayoutGrid();
		}
	}

	/*
	 * mark the grid as invalid
	 */
	private void invalidateMigLayoutGrid() {
		this.valid = false;
        // for debugging System.out.println("MigPane.invalidateMigLayoutGrid valid=" + valid );        
	}

    /*
     * @returns true if the grid is valid.
     */
    private boolean isMiglayoutGridValid() {
        return this.valid;
    }
    volatile boolean valid = false;

    /**
     * use all kinds of properties to calculate a hash for the layout
     * @param node
     * @return
     */
    private Integer calculateHashcode(Node node) {
    	StringBuffer lStringBuffer = new StringBuffer();
    	lStringBuffer.append(node.minWidth(-1));
    	lStringBuffer.append("x");
    	lStringBuffer.append(node.minHeight(-1));
    	lStringBuffer.append("/");
    	lStringBuffer.append(node.prefWidth(-1));
    	lStringBuffer.append("x");
    	lStringBuffer.append(node.prefHeight(-1));
    	lStringBuffer.append("/");
    	lStringBuffer.append(node.maxWidth(-1));
    	lStringBuffer.append("x");
    	lStringBuffer.append(node.maxHeight(-1));
    	lStringBuffer.append("/");
    	lStringBuffer.append(node.getLayoutBounds().getWidth());
    	lStringBuffer.append("x");
    	lStringBuffer.append(node.getLayoutBounds().getHeight());
    	lStringBuffer.append("/");
    	lStringBuffer.append(node.isVisible());
    	return lStringBuffer.toString().hashCode();
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
	final private List<Node> debugRectangles = new ArrayList<Node>();

	/*
	 *
	 */
	private void addDebugRectangle(double x, double y, double w, double h, DebugRectangleType type)
	{
		DebugRectangle lRectangle = new DebugRectangle( x, y, w, h );
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
	enum DebugRectangleType { CELL, OUTLINE, CONTAINER_OUTLINE, EXTERNAL }

	class DebugRectangle extends Rectangle
	{
		public DebugRectangle(double x, double y, double w, double h)
		{
			super(x,y,w,h);
		}
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
		public net.miginfocom.layout.ComponentWrapper[] getComponents() {
			// for debugging System.out.println("MigPane.FX2ContainerWrapper.getComponents " + MigPane.this.componentWrapperList.size());
			return componentWrapperList.toArray(new FX2ComponentWrapper[]{}); // must be in the order of adding!
		}

		// as of JDK 1.6: @Override
		public int getComponentCount() {
			// for debugging System.out.println("MigPane.FX2ContainerWrapper.getComponentCount " + MigPane.this.fx2ComponentWrapperToCCMap.size());
			return MigPane.this.fx2ComponentWrapperToCCMap.size();
		}

		// as of JDK 1.6: @Override
		public java.lang.Object getLayout() {
			return MigPane.this;
		}

		// as of JDK 1.6: @Override
		public boolean isLeftToRight() {
			return true;
		}

		// as of JDK 1.6: @Override
		public void paintDebugCell(int x, int y, int w, int h) {
			addDebugRectangle((double)x, (double)y, (double)w, (double)h, DebugRectangleType.CELL);
		}

		// as of JDK 1.6: @Override
		public void paintDebugOutline(boolean useVisualPadding) {
			// to be frank: this is done via trail and error
			addDebugRectangle( 0
					         , 0
					         , getWidth()
					         , getHeight()
					         , DebugRectangleType.CONTAINER_OUTLINE
					         );
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
			return fx2ContainerWrapper;
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
			int v = (int)Math.ceil(node.getLayoutBounds().getMinX());
			return v;
		}

		// as of JDK 1.6: @Override
		public int getY() {
			int v = (int)Math.ceil(node.getLayoutBounds().getMinY());
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
			if (node instanceof javafx.scene.layout.Region) {
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
			// for debuggin System.out.println("MigPane.FX2ComponentWrapper.getPreferredHeight " + this.node.getClass().getSimpleName() + "=" + this.node.prefHeight(-1));
			return v;
		}

		// as of JDK 1.6: @Override
		public int getMaximumHeight(int width) {
			int v = (int)Math.ceil(this.node.maxHeight(width));
			if (node instanceof javafx.scene.layout.Region) {
				v = Integer.MAX_VALUE;
				// for debugging System.out.println(getComponent() + " forced getMaximumHeight " + v); }
			}
			return v;
		}

		// as of JDK 1.6: @Override
		public int getBaseline(int width, int height) {
			return -1; // TODO
		}

		// as of JDK 1.6: @Override
		public int getScreenLocationX() {
			// this code is never called?
			Bounds lBoundsInScenenode = node.localToScene(node.getBoundsInLocal());
			int v = (int)Math.ceil(node.getScene().getX() + node.getScene().getX() + lBoundsInScenenode.getMinX());
			// for debugging System.out.println(getComponent() + " getScreenLocationX =" + v);
			return v;
		}

		// as of JDK 1.6: @Override
		public int getScreenLocationY() {
			// this code is never called?
			Bounds lBoundsInScenenode = node.localToScene(node.getBoundsInLocal());
			int v = (int)Math.ceil(node.getScene().getY() + node.getScene().getY() + lBoundsInScenenode.getMinY());
			// for debugging System.out.println(getComponent() + " getScreenLocationX =" + v);
			return v;
		}

		// as of JDK 1.6: @Override
		public int getScreenHeight() {
			// this code is never called?
			int v = (int)Math.ceil(Screen.getPrimary().getBounds().getHeight());
			// for debugging System.out.println(getComponent() + " getScreenHeight=" + v);
			return v;
		}

		// as of JDK 1.6: @Override
		public int getScreenWidth() {
			// this code is never called?
			int v = (int)Math.ceil(Screen.getPrimary().getBounds().getWidth());
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
			int lHashCode = 0;
			lHashCode += ((int)this.node.getLayoutBounds().getWidth()) + (((int)this.node.getLayoutBounds().getHeight()) * 32); // << 0, << 5
		    if (this.node.isVisible()) {
		    	lHashCode += 1324511;
		    }
		    if (this.node.isManaged()) {
		    	lHashCode += 1324513;
		    }
		    if (this.node.getId().length() > 0) {
		        lHashCode += this.node.getId().hashCode();
		    }
			return lHashCode; // 0;
		}

		// as of JDK 1.6: @Override
		public String getLinkId() {
			return node.getId();
		}

		// as of JDK 1.6: @Override
		public boolean hasBaseline() {
			return false;
		}

		// as of JDK 1.6: @Override
		public boolean isVisible() {
			return node.isVisible();
		}

		// as of JDK 1.6: @Override
		public void paintDebugOutline(boolean useVisualPadding) {
			ComponentWrapper lComponentWrapper = nodeToComponentWrapperMap.get(node);
			CC lCC = fx2ComponentWrapperToCCMap.get(lComponentWrapper);
			if (lCC != null && lCC.isExternal())
			{
				addDebugRectangle(this.node.getLayoutX() + this.node.getLayoutBounds().getMinX(), (double)this.node.getLayoutY() + this.node.getLayoutBounds().getMinY(), getWidth(), getHeight(), DebugRectangleType.EXTERNAL); // always draws node size, even if less is used
			}
			else
			{
				addDebugRectangle(this.node.getLayoutX() + this.node.getLayoutBounds().getMinX(), (double)this.node.getLayoutY() + this.node.getLayoutBounds().getMinY(), getWidth(), getHeight(), DebugRectangleType.OUTLINE); // always draws node size, even if less is used
			}
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

    // -----------------------------------------
	// No need to recalculate MigLayout's size when resizing.
    // This is copied from MigLayout swing's code:
    //      maximumLayoutSize(); minimumLayoutSize(); preferredLayoutSize();

    @Override
    protected double computeMaxHeight(double width) {
    	validateMigLayoutGrid();
        int h = LayoutUtil.getSizeSafe(grid != null ? grid.getHeight() : null, LayoutUtil.MAX);
        return h;
    }

    @Override
    protected double computeMaxWidth(double height) {
    	validateMigLayoutGrid();
        int w = LayoutUtil.getSizeSafe(grid != null ? grid.getWidth() : null, LayoutUtil.MAX);
        return w;
    }

    @Override
    protected double computeMinHeight(double width) {
    	validateMigLayoutGrid();
        int h = LayoutUtil.getSizeSafe(grid != null ? grid.getHeight() : null, LayoutUtil.MIN);
        return h;
    }

    @Override
    protected double computeMinWidth(double height) {
    	validateMigLayoutGrid();
        int w = LayoutUtil.getSizeSafe(grid != null ? grid.getWidth() : null, LayoutUtil.MIN);
        return w;
    }

    @Override
    protected double computePrefHeight(double width) {
    	validateMigLayoutGrid();
        int h = LayoutUtil.getSizeSafe(grid != null ? grid.getHeight() : null, LayoutUtil.PREF);
        // for debugging System.out.println("MigPane.computePrefHeight " + h + " / " + LayoutUtil.getSizeSafe(grid.getHeight(), LayoutUtil.PREF));
        return h;
    }

    @Override
    protected double computePrefWidth(double height) {
    	validateMigLayoutGrid();
        int w = LayoutUtil.getSizeSafe(grid != null ? grid.getWidth() : null, LayoutUtil.PREF);
        return w;
    }
    // -----------------------------------------

}
