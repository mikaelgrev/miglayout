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

import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;
import net.miginfocom.layout.LayoutUtil;
import net.miginfocom.layout.PlatformDefaults;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 */
public class SwtComponentWrapper implements ComponentWrapper
{
	/** Debug color for component bounds outline.
	 */
	private static Color DB_COMP_OUTLINE = new Color(Display.getCurrent(), 0, 0, 200);

	private static boolean vp = false;
	private static boolean mz = false;

	private final Control c;
	private int compType = TYPE_UNSET;

	public SwtComponentWrapper(Control c)
	{
		this.c = c;
	}

	@Override
	public final int getBaseline(int width, int height)
	{
		return -1;
	}

	@Override
	public final Object getComponent()
	{
		return c;
	}

	@Override
	public final float getPixelUnitFactor(boolean isHor)
	{
		switch (PlatformDefaults.getLogicalPixelBase()) {
			case PlatformDefaults.BASE_FONT_SIZE:
				GC gc = new GC(c);
				FontMetrics fm = gc.getFontMetrics();
				float f = isHor ? fm.getAverageCharWidth() / 5f : fm.getHeight() / 13f;
				gc.dispose();
				return f;

			case PlatformDefaults.BASE_SCALE_FACTOR:
				Float s = isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
				if (s != null)
					return s.floatValue();
				return (isHor ? getHorizontalScreenDPI() : getVerticalScreenDPI()) / (float) PlatformDefaults.getDefaultDPI();

			default:
				return 1f;
		}
	}

//	/** Cache.
//	 */
//	private final static IdentityHashMap<FontMetrics, Point.Float> FM_MAP2 = new IdentityHashMap<FontMetrics, Point.Float>(4);
//	private final static Font SUBST_FONT2 = new Font("sansserif", Font.PLAIN, 11);
//
//	public float getDialogUnit(boolean isHor)
//	{
//		Font font = c.getFont();
//		FontMetrics fm = c.getFontMetrics(font != null ? font : SUBST_FONT2);
//		Point.Float dluP = FM_MAP2.get(fm);
//		if (dluP == null) {
//			float w = fm.charWidth('X') / 4f;
//			int ascent = fm.getAscent();
//			float h = (ascent > 14 ? ascent : ascent + (15 - ascent) / 3) / 8f;
//
//			dluP = new Point.Float(w, h);
//			FM_MAP2.put(fm, dluP);
//		}
//		return isHor ? dluP.x : dluP.y;
//	}

	@Override
	public final int getX()
	{
		return c.getLocation().x;
	}

	@Override
	public final int getY()
	{
		return c.getLocation().y;
	}

	@Override
	public final int getWidth()
	{
		return c.getSize().x;
	}

	@Override
	public final int getHeight()
	{
		return c.getSize().y;
	}

	@Override
	public final int getScreenLocationX()
	{
		return c.toDisplay(0, 0).x;
	}

	@Override
	public final int getScreenLocationY()
	{
		return c.toDisplay(0, 0).y;
	}

	@Override
	public final int getMinimumHeight(int sz)
	{
		return mz ? 0 : computeSize(false, sz).y;
	}

	@Override
	public final int getMinimumWidth(int sz)
	{
		return mz ? 0 : computeSize(true, sz).x;
	}

	@Override
	public final int getPreferredHeight(int sz)
	{
		return computeSize(false, sz).y;
	}

	@Override
	public final int getPreferredWidth(int sz)
	{
		return computeSize(true, sz).x;
	}

	@Override
	public final int getMaximumHeight(int sz)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public final int getMaximumWidth(int sz)
	{
		return Integer.MAX_VALUE;
	}

	private Point computeSize(boolean hor, int sz)
	{
		//Point p = c.getSize();
		int wHint = hor ? SWT.DEFAULT : sz;     //(hor || p.x <= 0) ? sz : p.x;
		int hHint = !hor ? SWT.DEFAULT : sz;    //(!hor || p.y <= 0) ? sz : p.y;

		if (wHint != SWT.DEFAULT || hHint != SWT.DEFAULT) {
			int trim = 0;
			if (c instanceof Scrollable) {
				Rectangle rect = ((Scrollable) c).computeTrim(0, 0, 0, 0);
				trim = hor ? rect.width : rect.height;
			} else {
				trim = (c.getBorderWidth() << 1);
			}
			if (wHint == SWT.DEFAULT) {
				hHint = Math.max(0, hHint - trim);
			} else {
				wHint = Math.max(0, wHint - trim);
			}
		}

		return c.computeSize(wHint, hHint);
	}

	@Override
	public final ContainerWrapper getParent()
	{
		return new SwtContainerWrapper(c.getParent());
	}

	@Override
	public int getHorizontalScreenDPI()
	{
		return PlatformDefaults.getDefaultDPI();
	}

	@Override
	public int getVerticalScreenDPI()
	{
		return PlatformDefaults.getDefaultDPI();
	}

	@Override
	public final int getScreenWidth()
	{
		return c.getDisplay().getBounds().width;
	}

	@Override
	public final int getScreenHeight()
	{
		return c.getDisplay().getBounds().height;
	}

	@Override
	public final boolean hasBaseline()
	{
		return false;
	}

	@Override
	public final String getLinkId()
	{
		return null;
	}

	@Override
	public final void setBounds(int x, int y, int width, int height)
	{
		c.setBounds(x, y, width, height);
	}

	@Override
	public boolean isVisible()
	{
		return c.getVisible();
	}

	@Override
	public final int[] getVisualPadding()
	{
		return null;
	}

	public static boolean isUseVisualPadding()
	{
		return vp;
	}

	public static void setUseVisualPadding(boolean b)
	{
		vp = b;
	}

	/** Sets if minimum size for SWT components should be preferred size (default, false) or 0.
	 * @return <code>true</code> means minimum size is 0.
	 */
	public static boolean isMinimumSizeZero()
	{
		return mz;
	}

	/** Sets if minimum size for SWT components should be preferred size (default, false) or 0.
	 * @param b <code>true</code> means minimum size is 0.
	 */
	public static void setMinimumSizeZero(boolean b)
	{
		mz = b;
	}

	@Override
	public int getLayoutHashCode()
	{
		if (c.isDisposed())
			return -1;

		Point sz = c.getSize();
		Point p = c.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		int h = p.x + (p.y << 12) + (sz.x << 22) + (sz.y << 16);

		if (c.isVisible())
			h += 1324511;

		String id = getLinkId();
		if (id != null)
			h += id.hashCode();
		return h;
	}

	@Override
	public final void paintDebugOutline(boolean useVisaualPadding)
	{
		if (c.isDisposed())
			return;

		GC gc = new GC(c);

		gc.setLineJoin(SWT.JOIN_MITER);
		gc.setLineCap(SWT.CAP_SQUARE);
		gc.setLineStyle(SWT.LINE_DOT);

		gc.setForeground(DB_COMP_OUTLINE);
		gc.drawRectangle(0, 0, getWidth() - 1, getHeight() - 1);

		gc.dispose();
	}

	@Override
	public int getComponentType(boolean disregardScrollPane)
	{
		if (compType == TYPE_UNSET)
			compType = checkType();

		return compType;
	}

	private int checkType()
	{
		int s = c.getStyle();

		if (c instanceof Text || c instanceof StyledText) {
			return (s & SWT.MULTI) > 0 ? TYPE_TEXT_AREA : TYPE_TEXT_FIELD;
		} else if (c instanceof Label) {
			return (s & SWT.SEPARATOR) > 0 ? TYPE_SEPARATOR : TYPE_LABEL;
		} else if (c instanceof Button) {
			if ((s & SWT.CHECK) > 0 || (s & SWT.RADIO) > 0)
				return TYPE_CHECK_BOX;
			return TYPE_BUTTON;
		} else if (c instanceof Canvas) {
			return TYPE_PANEL;
		} else if (c instanceof List) {
			return TYPE_LIST;
		} else if (c instanceof Table) {
			return TYPE_TABLE;
		} else if (c instanceof Spinner) {
			return TYPE_SPINNER;
		} else if (c instanceof ProgressBar) {
			return TYPE_PROGRESS_BAR;
		} else if (c instanceof Slider) {
			return TYPE_SLIDER;
		} else if (c instanceof Composite) {    // only AWT components is not containers.
			return TYPE_CONTAINER;
		}
		return TYPE_UNKNOWN;
	}

	@Override
	public final int hashCode()
	{
		return c.hashCode();
	}

	@Override
	public final boolean equals(Object o)
	{
		if (o == null || o instanceof ComponentWrapper == false)
			return false;

		return getComponent().equals(((ComponentWrapper) o).getComponent());
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "{" + c + "}";
	}

	@Override
	public int getContentBias()
	{
		return (c instanceof Text || c instanceof StyledText) && (c.getStyle() & SWT.MULTI) > 0 ? LayoutUtil.HORIZONTAL : -1;
	}
}
