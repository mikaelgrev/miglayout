package net.miginfocom.swt;
/*
 * License (BSD):
 * ==============
 *
 * Copyright (c) 2004, Mikael Grev, MiG InfoCom AB. (miglayout (at) miginfocom (dot) com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * Neither the name of the MiG InfoCom AB nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * @version 1.0
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 2006-sep-08
 */

import net.miginfocom.layout.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.*;

/** A very flexible layout manager.
 * <p>
 * Read the documentation that came with this layout manager for information on usage.
 */
public final class MigLayout extends Layout implements Externalizable
{
	// ******** Instance part ********

	/** The component to String or CC constraints mappings.
	 */
	private final Map<Control, Object> scrConstrMap = new IdentityHashMap<Control, Object>(8);

	/** Hold the serializable text representation of the constraints.
	 */
	private Object layoutConstraints = "", colConstraints = "", rowConstraints = "";    // Should never be null!

	// ******** Transient part ********

	private transient ContainerWrapper cacheParentW = null;

	private transient final Map<ComponentWrapper, CC> ccMap = new HashMap<ComponentWrapper, CC>(8);

	private transient LC lc = null;
	private transient AC colSpecs = null, rowSpecs = null;
	private transient Grid grid = null;

	private transient java.util.Timer debugTimer = null;
	private transient long curDelay = -1;
	private transient int lastModCount = PlatformDefaults.getModCount();
	private transient int lastHash = -1;

	private transient ArrayList<LayoutCallback> callbackList = null;

	/** Constructor with no constraints.
	 */
	public MigLayout()
	{
		this("", "", "");
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as "".
	 */
	public MigLayout(String layoutConstraints)
	{
		this(layoutConstraints, "", "");
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as "".
	 * @param colConstraints The constraints for the columns in the grid. <code>null</code> will be treated as "".
	 */
	public MigLayout(String layoutConstraints, String colConstraints)
	{
		this(layoutConstraints, colConstraints, "");
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as "".
	 * @param colConstraints The constraints for the columns in the grid. <code>null</code> will be treated as "".
	 * @param rowConstraints The constraints for the rows in the grid. <code>null</code> will be treated as "".
	 */
	public MigLayout(String layoutConstraints, String colConstraints, String rowConstraints)
	{
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		setRowConstraints(rowConstraints);
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as an empty constraint.
	 */
	public MigLayout(LC layoutConstraints)
	{
		this(layoutConstraints, null, null);
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as an empty constraint.
	 * @param colConstraints The constraints for the columns in the grid. <code>null</code> will be treated as an empty constraint.
	 */
	public MigLayout(LC layoutConstraints, AC colConstraints)
	{
		this(layoutConstraints, colConstraints, null);
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as an empty constraint.
	 * @param colConstraints The constraints for the columns in the grid. <code>null</code> will be treated as an empty constraint.
	 * @param rowConstraints The constraints for the rows in the grid. <code>null</code> will be treated as an empty constraint.
	 */
	public MigLayout(LC layoutConstraints, AC colConstraints, AC rowConstraints)
	{
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		setRowConstraints(rowConstraints);
	}

	/** Returns layout constraints either as a <code>String</code> or {@link net.miginfocom.layout.LC} depending what was sent in
	 * to the constructor or set with {@link #setLayoutConstraints(Object)}.
	 * @return The layout constraints either as a <code>String</code> or {@link net.miginfocom.layout.LC} depending what was sent in
	 * to the constructor or set with {@link #setLayoutConstraints(Object)}. Never <code>null</code>.
	 */
	public Object getLayoutConstraints()
	{
		return layoutConstraints;
	}

	/** Sets the layout constraints for the layout manager instance as a String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @param s The layout constraints as a String representation. <code>null</code> is converted to <code>""</code> for storage.
	 * @throws RuntimeException if the constraint was not valid.
	 */
	public void setLayoutConstraints(Object s)
	{
		if (s == null || s instanceof String) {
			s = ConstraintParser.prepare((String) s);
			lc = ConstraintParser.parseLayoutConstraint((String) s);
		} else if (s instanceof LC) {
			lc = (LC) s;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + s.getClass().toString());
		}
		layoutConstraints = s;
		grid = null;
	}

	/** Returns the column layout constraints either as a <code>String</code> or {@link net.miginfocom.layout.AC}.
	 * @return The column constraints either as a <code>String</code> or {@link net.miginfocom.layout.LC} depending what was sent in
	 * to the constructor or set with {@link #setLayoutConstraints(Object)}. Never <code>null</code>.
	 */
	public Object getColumnConstraints()
	{
		return colConstraints;
	}

	/** Sets the column layout constraints for the layout manager instance as a String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @param constr The column layout constraints as a String representation. <code>null</code> is converted to <code>""</code> for storage.
	 * @throws RuntimeException if the constraint was not valid.
	 */
	public void setColumnConstraints(Object constr)
	{
		if (constr == null || constr instanceof String) {
			constr = ConstraintParser.prepare((String) constr);
			colSpecs = ConstraintParser.parseColumnConstraints((String) constr);
		} else if (constr instanceof AC) {
			colSpecs = (AC) constr;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
		}
		colConstraints = constr;
		grid = null;
	}

	/** Returns the row layout constraints as a String representation. This string is the exact string as set with {@link #setRowConstraints(Object)}
	 * or sent into the constructor.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @return The row layout constraints as a String representation. Never <code>null</code>.
	 */
	public Object getRowConstraints()
	{
		return rowConstraints;
	}

	/** Sets the row layout constraints for the layout manager instance as a String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @param constr The row layout constraints as a String representation. <code>null</code> is converted to <code>""</code> for storage.
	 * @throws RuntimeException if the constraint was not valid.
	 */
	public void setRowConstraints(Object constr)
	{
		if (constr == null || constr instanceof String) {
			constr = ConstraintParser.prepare((String) constr);
			rowSpecs = ConstraintParser.parseRowConstraints((String) constr);
		} else if (constr instanceof AC) {
			rowSpecs = (AC) constr;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
		}
		rowConstraints = constr;
		grid = null;
	}

	/** Returns a shallow copy of the constraints map.
	 * @return A  shallow copy of the constraints map. Never <code>null</code>.
	 */
	public Map<Control, Object> getConstraintMap()
	{
		return new IdentityHashMap<Control, Object>(scrConstrMap);
	}

	/** Sets the constraints map.
	 * @param map The map. Will be copied.
	 */
	public void setConstraintMap(Map<Control, Object> map)
	{
		scrConstrMap.clear();
		ccMap.clear();
		for (Map.Entry<Control, Object> e : map.entrySet())
			setComponentConstraintsImpl(e.getKey(), e.getValue(), true);
	}

	/** Sets the component constraint for the component that already must be handled by this layout manager.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @param constr The component constraints as a String or {@link net.miginfocom.layout.CC}. <code>null</code> is ok.
	 * @param comp The component to set the constraints for.
	 * @param noCheck Doesn't check if control already is managed.
	 * @throws RuntimeException if the constraint was not valid.
	 * @throws IllegalArgumentException If the component is not handling the component.
	 */
	private void setComponentConstraintsImpl(Control comp, Object constr, boolean noCheck)
	{
		if (noCheck == false && scrConstrMap.containsKey(comp) == false)
			throw new IllegalArgumentException("Component must already be added to parent!");

		ComponentWrapper cw = new SwtComponentWrapper(comp);

		if (constr == null || constr instanceof String) {
			String cStr = ConstraintParser.prepare((String) constr);

			scrConstrMap.put(comp, constr);
			ccMap.put(cw, ConstraintParser.parseComponentConstraint(cStr));

		} else if (constr instanceof CC) {

			scrConstrMap.put(comp, constr);
			ccMap.put(cw, (CC) constr);

		} else {
			throw new IllegalArgumentException("Constraint must be String or ComponentConstraint: " + constr.getClass().toString());
		}
		grid = null;
	}

	/** Returns if this layout manager is currently managing this component.
	 * @param c The component to check. If <code>null</code> then <code>false</code> will be returned.
	 * @return If this layout manager is currently managing this component.
	 */
	public boolean isManagingComponent(Control c)
	{
		return scrConstrMap.containsKey(c);
	}

	/** Adds the callback function that will be called at different stages of the layout cycle.
	 * @param callback The callback. Not <code>null</code>.
	 */
	public void addLayoutCallback(LayoutCallback callback)
	{
		if (callback == null)
			throw new NullPointerException();

		if (callbackList == null)
			callbackList = new ArrayList<LayoutCallback>(1);

		callbackList.add(callback);
	}

	/** Removes the callback if it exists.
	 * @param callback The callback. May be <code>null</code>.
	 */
	public void removeLayoutCallback(LayoutCallback callback)
	{
		if (callbackList != null)
			callbackList.remove(callback);
	}

	/** Sets the debugging state for this layout manager instance. If debug is turned on a timer will repaint the last laid out parent
	 * with debug information on top.
	 * <p>
	 * Red fill and dashed dark red outline is used to indicate occupied cells in the grid. Blue dashed outline indicate
	 * component bounds set.
	 * <p>
	 * Note that debug can also be set on the layout constraints. There it will be persisted. The value set here will not. See the class
	 * JavaDocs for information.
	 * @param parentW The parent. Never <code>null</code>.
	 * @param b <code>true</code> means debug is turned on.
	 */
	private synchronized void setDebug(final ComponentWrapper parentW, boolean b)
	{
		if (b && (debugTimer == null || curDelay != getDebugMillis())) {
			if (debugTimer != null)
				debugTimer.cancel();

			debugTimer = new Timer(true);
			curDelay = getDebugMillis();
			debugTimer.schedule(new MyDebugRepaintTask(this), curDelay, curDelay);

			ContainerWrapper pCW = parentW.getParent();
			Composite parent = pCW != null ? (Composite) pCW.getComponent() : null;
			if (parent != null)
				parent.layout();

		} else if (!b && debugTimer != null) {
			debugTimer.cancel();
			debugTimer = null;
		}
	}

	/** Returns the current debugging state.
	 * @return The current debugging state.
	 */
	private boolean getDebug()
	{
		return debugTimer != null;
	}

	/** Returns the debug millis. Combines the value from {@link net.miginfocom.layout.LC#getDebugMillis()} and {@link net.miginfocom.layout.LayoutUtil#getGlobalDebugMillis()}
	 * @return The combined value.
	 */
	private int getDebugMillis()
	{
		int globalDebugMillis = LayoutUtil.getGlobalDebugMillis();
		return globalDebugMillis > 0 ? globalDebugMillis : lc.getDebugMillis();
	}

	/** Check if something has changed and if so recreate it to the cached objects.
	 * @param parent The parent that is the target for this layout manager.
	 */
	private void checkCache(Composite parent)
	{
		if (parent == null)
			return;

		checkConstrMap(parent);

		ContainerWrapper par = checkParent(parent);

		// Check if the grid is valid
		int mc = PlatformDefaults.getModCount();
		if (lastModCount != mc) {
			grid = null;
			lastModCount = mc;
		}

		int hash = parent.getSize().hashCode();
		for (ComponentWrapper cw : ccMap.keySet()) {
			hash ^= cw.getLayoutHashCode();
			hash += 285134905;
		}

		if (hash != lastHash) {
			grid = null;
			lastHash = hash;
		}

		setDebug(par, getDebugMillis() > 0);

		if (grid == null)
			grid = new Grid(par, lc, rowSpecs, colSpecs, ccMap, callbackList);
	}

	/** Checks so all components in ccMap actually exist in the parent's collection. Removes
	 * any references that don't.
	 * @param parent The parent to compare ccMap against. Never null.
	 */
	private void checkConstrMap(Composite parent)
	{
		Control[] children = parent.getChildren();
		HashSet<Control> parentCompSet = new HashSet<Control>(Arrays.asList(children));

		// Clear cached constraints
		Iterator<Map.Entry<ComponentWrapper, CC>> ccIt = ccMap.entrySet().iterator();
		while(ccIt.hasNext()) {
			Control c = (Control) ccIt.next().getKey().getComponent();
			if (c.isDisposed() || parentCompSet.contains(c) == false) {
				ccIt.remove();
				scrConstrMap.remove(c);
				grid = null; // To clear any references to a removed widget
			}
		}

		// Recreate constraints if changed.
		for (Control child : children) {
			if (scrConstrMap.get(child) != child.getLayoutData())
				setComponentConstraintsImpl(child, child.getLayoutData(), true);
		}
	}

	private ContainerWrapper checkParent(Composite parent)
	{
		if (parent == null)
			return null;

		if (cacheParentW == null || cacheParentW.getComponent() != parent)
			cacheParentW = new SwtContainerWrapper(parent);

		return cacheParentW;
	}

	public float getLayoutAlignmentX(Composite parent)
	{
		return lc != null && lc.getAlignX() != null ? lc.getAlignX().getPixels(1, checkParent(parent), null) : 0;
	}

	public float getLayoutAlignmentY(Composite parent)
	{
		return lc != null && lc.getAlignY() != null ? lc.getAlignY().getPixels(1, checkParent(parent), null) : 0;
	}

	@Override
	protected Point computeSize(Composite parent, int wHint, int hHint, boolean flushCache)
	{
		checkCache(parent);

		int w = LayoutUtil.getSizeSafe(grid != null ? grid.getWidth() : null, LayoutUtil.PREF);
		int h = LayoutUtil.getSizeSafe(grid != null ? grid.getHeight() : null, LayoutUtil.PREF);

		return new Point(w, h);
	}

	@Override
	protected void layout(Composite parent, boolean flushCache)
	{
		if (flushCache)
			grid = null;

		checkCache(parent);

		Rectangle r = parent.getClientArea();
		int[] b = new int[] {r.x, r.y, r.width, r.height};

		final boolean layoutAgain = grid.layout(b, lc.getAlignX(), lc.getAlignY(), getDebug());

		if (layoutAgain) {
			grid = null;
			checkCache(parent);
			grid.layout(b, lc.getAlignX(), lc.getAlignY(), getDebug());
		}
	}

	@Override
	protected boolean flushCache(Control control)
	{
		grid = null;
		return true;
	}

	// ************************************************
	// Persistence Delegate and Serializable combined.
	// ************************************************

	private Object readResolve() throws ObjectStreamException
	{
		return LayoutUtil.getSerializedObject(this);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(in));
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		if (getClass() == MigLayout.class)
			LayoutUtil.writeAsXML(out, this);
	}

	private static class MyDebugRepaintTask extends TimerTask
	{
		private final WeakReference<MigLayout> layoutRef;

		private MyDebugRepaintTask(MigLayout layout)
		{
			this.layoutRef = new WeakReference<MigLayout>(layout);
		}

		@Override
		public void run()
		{
			final MigLayout layout = layoutRef.get();
			if (layout != null && layout.grid != null) {
				Display.getDefault().asyncExec(new Runnable () {
					@Override
					public void run () {
						if (layout.grid != null)
							layout.grid.paintDebug();
					}
				});
			}
		}
	}
}