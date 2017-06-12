package net.miginfocom.swing;
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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.IdentityHashMap;
import java.util.StringTokenizer;

/**
 */
public class SwingComponentWrapper implements ComponentWrapper
{
	private static boolean maxSet = false;

	private static boolean vp = true;

	/** Debug color for component bounds outline.
	 */
	private static final Color DB_COMP_OUTLINE = new Color(0, 0, 200);

	/** Property to use in LAF settings and as JComponent client property
	 * to specify the visual padding.
	 * <p>
	 */
	private static final String VISUAL_PADDING_PROPERTY = net.miginfocom.layout.PlatformDefaults.VISUAL_PADDING_PROPERTY;

	private final Component c;
	private int compType = TYPE_UNSET;
	private Boolean bl = null;
	private boolean prefCalled = false;

	public SwingComponentWrapper(Component c)
	{
		this.c = c;
	}

	@Override
	public final int getBaseline(int width, int height)
	{
		int baseLine = c.getBaseline(width < 0 ? c.getWidth() : width, height < 0 ? c.getHeight() : height);
		if (baseLine != -1) {
			int[] visPad = getVisualPadding();
			if (visPad != null)
				baseLine += (visPad[2] - visPad[0] + 1) / 2;
		}
		return baseLine;
	}

	@Override
	public final Object getComponent()
	{
		return c;
	}

	/** Cache.
	 */
	private final static IdentityHashMap<FontMetrics, Point.Float> FM_MAP = new IdentityHashMap<FontMetrics, Point.Float>(4);
	private final static Font SUBST_FONT = new Font("sansserif", Font.PLAIN, 11);

	@Override
	public final float getPixelUnitFactor(boolean isHor)
	{
		switch (PlatformDefaults.getLogicalPixelBase()) {
			case PlatformDefaults.BASE_FONT_SIZE:
				Font font = c.getFont();
				FontMetrics fm = c.getFontMetrics(font != null ? font : SUBST_FONT);
				Point.Float p = FM_MAP.get(fm);
				if (p == null) {
					Rectangle2D r = fm.getStringBounds("X", c.getGraphics());
					p = new Point.Float(((float) r.getWidth()) / 6f, ((float) r.getHeight()) / 13.27734375f);
					FM_MAP.put(fm, p);
				}
				return isHor ? p.x : p.y;

			case PlatformDefaults.BASE_SCALE_FACTOR:

				Float s = isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
				float scaleFactor = (s != null) ? s : 1f;

				// Swing in Java 9 scales automatically using the system scale factor(s) that the
				// user can change in the system settings (Windows: Control Panel; Mac: System Preferences).
				// Each connected screen may use its own scale factor
				// (e.g. 1.5 for primary 4K 40inch screen and 1.0 for secondary HD screen).
				float screenScale = isJava9orLater
					? 1f // use system scale factor(s)
					: (float) (isHor ? getHorizontalScreenDPI() : getVerticalScreenDPI()) / (float) PlatformDefaults.getDefaultDPI();
				return scaleFactor * screenScale;

			default:
				return 1f;
		}
	}

	private static boolean isJava9orLater;
	static {
		try {
			// Java 9 version-String Scheme: http://openjdk.java.net/jeps/223
			StringTokenizer st = new StringTokenizer(System.getProperty("java.version"), "._-+");
			int majorVersion = Integer.parseInt(st.nextToken());
			isJava9orLater = majorVersion >= 9;
		} catch (Exception e) {
			// Java 8 or older
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
		return c.getX();
	}

	@Override
	public final int getY()
	{
		return c.getY();
	}

	@Override
	public final int getHeight()
	{
		return c.getHeight();
	}

	@Override
	public final int getWidth()
	{
		return c.getWidth();
	}

	@Override
	public final int getScreenLocationX()
	{
		Point p = new Point();
		SwingUtilities.convertPointToScreen(p, c);
		return p.x;
	}

	@Override
	public final int getScreenLocationY()
	{
		Point p = new Point();
		SwingUtilities.convertPointToScreen(p, c);
		return p.y;
	}

	@Override
	public final int getMinimumHeight(int sz)
	{
		if (prefCalled == false) {
			c.getPreferredSize(); // To defeat a bug where the minimum size is different before and after the first call to getPreferredSize();
			prefCalled = true;
		}
		return c.getMinimumSize().height;
	}

	@Override
	public final int getMinimumWidth(int sz)
	{
		if (prefCalled == false) {
			c.getPreferredSize(); // To defeat a bug where the minimum size is different before and after the first call to getPreferredSize();
			prefCalled = true;
		}
		return c.getMinimumSize().width;
	}
	@Override
	public final int getPreferredHeight(int sz)
	{
		// If the component has not gotten size yet and there is a size hint, trick Swing to return a better height.
		if (c.getWidth() == 0 && c.getHeight() == 0 && sz != -1)
			c.setBounds(c.getX(), c.getY(), sz, 1);

		return c.getPreferredSize().height;
	}

	@Override
	public final int getPreferredWidth(int sz)
	{
		// If the component has not gotten size yet and there is a size hint, trick Swing to return a better height.
		if (c.getWidth() == 0 && c.getHeight() == 0 && sz != -1)
			c.setBounds(c.getX(), c.getY(), 1, sz);

		return c.getPreferredSize().width;
	}

	@Override
	public final int getMaximumHeight(int sz)
	{
		if (!isMaxSet(c))
			return Integer.MAX_VALUE;

		return c.getMaximumSize().height;
	}

	@Override
	public final int getMaximumWidth(int sz)
	{
		if (!isMaxSet(c))
			return Integer.MAX_VALUE;

		return c.getMaximumSize().width;
	}


	private boolean isMaxSet(Component c)
	{
		return c.isMaximumSizeSet();
	}

	@Override
	public final ContainerWrapper getParent()
	{
		Container p = c.getParent();
		return p != null ? new SwingContainerWrapper(p) : null;
	}

    @Override
    public final int getHorizontalScreenDPI() {
        try {
            return c.getToolkit().getScreenResolution();
        } catch (HeadlessException ex) {
            return PlatformDefaults.getDefaultDPI();
        }
    }

	@Override
	public final int getVerticalScreenDPI()
	{
        try {
            return c.getToolkit().getScreenResolution();
        } catch (HeadlessException ex) {
            return PlatformDefaults.getDefaultDPI();
        }
	}

	@Override
	public final int getScreenWidth()
	{
		try {
			return c.getToolkit().getScreenSize().width;
		} catch (HeadlessException ex) {
			return 1024;
		}
	}

	@Override
	public final int getScreenHeight()
	{
		try {
			return c.getToolkit().getScreenSize().height;
		} catch (HeadlessException ex) {
			return 768;
		}
	}

	@Override
	public final boolean hasBaseline()
	{
		if (bl == null) {
			try {
				// Removed since OTHER is sometimes returned even though there is a valid baseline (e.g. an empty JComboBox)
//				if (c.getBaselineResizeBehavior() == Component.BaselineResizeBehavior.OTHER) {
//					bl = Boolean.FALSE;
//				} else {
					Dimension d = c.getMinimumSize();
					bl = getBaseline(d.width, d.height) > -1;
//				}
			} catch (Throwable ex) {
				bl = Boolean.FALSE;
			}
		}
		return bl;
	}

	@Override
	public final String getLinkId()
	{
		return c.getName();
	}

	@Override
	public final void setBounds(int x, int y, int width, int height)
	{
		c.setBounds(x, y, width, height);
	}

	@Override
	public boolean isVisible()
	{
		return c.isVisible();
	}

	@Override
	public final int[] getVisualPadding()
	{
		int[] padding = null;
		if (isVisualPaddingEnabled()) {
			//First try "visualPadding" client property
			if (c instanceof JComponent) {
				JComponent component = (JComponent) c;
				Object padValue = component.getClientProperty(VISUAL_PADDING_PROPERTY);

				if (padValue instanceof int[] ) {
					//client property value could be an int[]
					padding = (int[]) padValue;
				} else if (padValue instanceof Insets) {
					//OR client property value could be an Insets
					Insets padInsets = (Insets) padValue;
					padding = new int[] { padInsets.top, padInsets.left, padInsets.bottom, padInsets.right };
				}

				if (padding == null) {
					//No client property set on the individual JComponent,
					//	so check for a LAF setting for the component type.
					String classID;
					switch (getComponentType(false)) {
						case TYPE_BUTTON:
							Border border = component.getBorder();
							if (border != null && border.getClass().getName().startsWith("com.apple.laf.AquaButtonBorder")) {
								if (PlatformDefaults.getPlatform() == PlatformDefaults.MAC_OSX) {
									Object buttonType = component.getClientProperty("JButton.buttonType");
									if (buttonType == null) {
										classID = component.getHeight() < 33 ? "Button" : "Button.bevel";
									} else {
										classID = "Button." + buttonType;
									}
									if (((AbstractButton) component).getIcon() != null)
										classID += ".icon";
								} else {
									classID = "Button";
								}
							} else {
								classID = "";
							}
							break;

						case TYPE_CHECK_BOX:
							border = component.getBorder();
							if (border != null && border.getClass().getName().startsWith("com.apple.laf.AquaButtonBorder")) {
								Object size = component.getClientProperty("JComponent.sizeVariant");
								if (size != null && size.toString().equals("regular") == false) {
									size = "." + size;
								} else {
									size = "";
								}

								if (component instanceof JRadioButton) {
									classID = "RadioButton" + size;
								} else if (component instanceof JCheckBox) {
									classID = "CheckBox" + size;
								} else {
									classID = "ToggleButton" + size;
								}
							} else {
								classID = "";
							}
							break;

						case TYPE_COMBO_BOX:
							if (PlatformDefaults.getPlatform() == PlatformDefaults.MAC_OSX) {
								if (((JComboBox) component).isEditable()) {
									Object isSquare = component.getClientProperty("JComboBox.isSquare");
									if (isSquare != null && isSquare.toString().equals("true")) {
										classID = "ComboBox.editable.isSquare";
									} else {
										classID = "ComboBox.editable";
									}

								} else {
									Object isSquare = component.getClientProperty("JComboBox.isSquare");
									Object isPopDown = component.getClientProperty("JComboBox.isPopDown");

									if (isSquare != null && isSquare.toString().equals("true")) {
										classID = "ComboBox.isSquare";
									} else if (isPopDown != null && isPopDown.toString().equals("true")) {
										classID = "ComboBox.isPopDown";
									} else {
										classID = "ComboBox";
									}
								}
							} else {
								classID = "ComboBox";
							}
							break;
						case TYPE_CONTAINER:
							classID = "Container";
							break;
						case TYPE_IMAGE:
							classID = "Image";
							break;
						case TYPE_LABEL:
							classID = "Label";
							break;
						case TYPE_LIST:
							classID = "List";
							break;
						case TYPE_PANEL:
							classID = "Panel";
							break;
						case TYPE_PROGRESS_BAR:
							classID = "ProgressBar";
							break;
						case TYPE_SCROLL_BAR:
							classID = "ScrollBar";
							break;
						case TYPE_SCROLL_PANE:
							classID = "ScrollPane";
							break;
						case TYPE_SEPARATOR:
							classID = "Separator";
							break;
						case TYPE_SLIDER:
							classID = "Slider";
							break;
						case TYPE_SPINNER:
							classID = "Spinner";
							break;
						case TYPE_TABLE:
							classID = "Table";
							break;
						case TYPE_TABBED_PANE:
							classID = "TabbedPane";
							break;
						case TYPE_TEXT_AREA:
							classID = "TextArea";
							break;
						case TYPE_TEXT_FIELD:
							border = component.getBorder();
							if (!component.isOpaque() && border != null && border.getClass().getSimpleName().equals("AquaTextFieldBorder")) {
								classID = "TextField";
							} else {
								classID = "";
							}
							break;
						case TYPE_TREE:
							classID = "Tree";
							break;
						case TYPE_UNKNOWN:
							classID = "Other";
							break;
						case TYPE_UNSET:
						default:
							classID = "";
							break;
					}

					padValue = PlatformDefaults.getDefaultVisualPadding(classID + "." + VISUAL_PADDING_PROPERTY);
					if (padValue instanceof int[]) {
						//client property value could be an int[]
						padding = (int[]) padValue;
					} else if (padValue instanceof Insets) {
						//OR client property value could be an Insets
						Insets padInsets = (Insets) padValue;
						padding = new int[] { padInsets.top, padInsets.left, padInsets.bottom, padInsets.right };
					}
				}
			}
		}
		return padding;
	}

	/**
	 * @deprecated Java 1.4 is not supported anymore
	 */
	public static boolean isMaxSizeSetOn1_4()
	{
		return maxSet;
	}

	/**
	 * @deprecated Java 1.4 is not supported anymore
	 */
	public static void setMaxSizeSetOn1_4(boolean b)
	{
		maxSet = b;
	}

	public static boolean isVisualPaddingEnabled()
	{
		return vp;
	}

	public static void setVisualPaddingEnabled(boolean b)
	{
		vp = b;
	}

	@Override
	public final void paintDebugOutline(boolean showVisualPadding)
	{
		if (c.isShowing() == false)
			return;

		Graphics2D g = (Graphics2D) c.getGraphics();
		if (g == null)
			return;

		g.setPaint(DB_COMP_OUTLINE);
		g.setStroke(new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10f, new float[] {2f, 4f}, 0));
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		if (showVisualPadding && isVisualPaddingEnabled()) {
			int[] padding = getVisualPadding();
			if (padding != null) {
				g.setColor(Color.GREEN);
				g.drawRect(padding[1], padding[0], (getWidth() - 1) - (padding[1] + padding[3]), (getHeight() - 1) - (padding[0] + padding[2]));
			}
		}
	}

	@Override
	public int getComponentType(boolean disregardScrollPane)
	{
		if (compType == TYPE_UNSET)
			compType = checkType(disregardScrollPane);

		return compType;
	}

	@Override
	public int getLayoutHashCode()
	{
		Dimension d = c.getMaximumSize();
		int hash = d.width + (d.height << 5);

		d = c.getPreferredSize();
		hash += (d.width << 10) + (d.height << 15);

		d = c.getMinimumSize();
		hash += (d.width << 20) + (d.height << 25);

		if (c.isVisible())
			hash += 1324511;

		String id = getLinkId();
		if (id != null)
			hash += id.hashCode();

		return hash;
	}

	private int checkType(boolean disregardScrollPane)
	{
		Component c = this.c;

		if (disregardScrollPane) {
			if (c instanceof JScrollPane) {
				c = ((JScrollPane) c).getViewport().getView();
			} else if (c instanceof ScrollPane) {
				c = ((ScrollPane) c).getComponent(0);
			}
		}

		if (c instanceof JTextField || c instanceof TextField) {
			return TYPE_TEXT_FIELD;
		} else if (c instanceof JLabel || c instanceof Label) {
			return TYPE_LABEL;
		} else if (c instanceof JCheckBox || c instanceof JRadioButton || c instanceof Checkbox) {
			return TYPE_CHECK_BOX;
		} else if (c instanceof AbstractButton || c instanceof Button) {
			return TYPE_BUTTON;
		} else if (c instanceof JComboBox || c instanceof Choice) {
			return TYPE_COMBO_BOX;
		} else if (c instanceof JTextComponent || c instanceof TextComponent) {
			return TYPE_TEXT_AREA;
		} else if (c instanceof JPanel || c instanceof Canvas) {
			return TYPE_PANEL;
		} else if (c instanceof JList || c instanceof List) {
			return TYPE_LIST;
		} else if (c instanceof JTable) {
			return TYPE_TABLE;
		} else if (c instanceof JSeparator) {
			return TYPE_SEPARATOR;
		} else if (c instanceof JSpinner) {
			return TYPE_SPINNER;
		} else if (c instanceof JTabbedPane) {
			return TYPE_TABBED_PANE;
		} else if (c instanceof JProgressBar) {
			return TYPE_PROGRESS_BAR;
		} else if (c instanceof JSlider) {
			return TYPE_SLIDER;
		} else if (c instanceof JScrollPane) {
			return TYPE_SCROLL_PANE;
		} else if (c instanceof JScrollBar || c instanceof Scrollbar) {
			return TYPE_SCROLL_BAR;
		} else if (c instanceof Container) {    // only AWT components is not containers.
			return TYPE_CONTAINER;
		}
		return TYPE_UNKNOWN;
	}

	@Override
	public final int hashCode()
	{
		return getComponent().hashCode();
	}

	@Override
	public final boolean equals(Object o)
	{
		if (o instanceof ComponentWrapper == false)
			return false;

		return c.equals(((ComponentWrapper) o).getComponent());
	}

	@Override
	public int getContentBias()
	{
		return c instanceof JTextArea || c instanceof JEditorPane || (c instanceof JComponent && Boolean.TRUE.equals(((JComponent)c).getClientProperty("migLayout.dynamicAspectRatio"))) ? LayoutUtil.HORIZONTAL : -1;
	}
}
