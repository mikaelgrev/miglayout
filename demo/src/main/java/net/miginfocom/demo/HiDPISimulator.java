package net.miginfocom.demo;

import net.miginfocom.layout.PlatformDefaults;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.MigLayout;
//import org.jvnet.substance.SubstanceLookAndFeel;
//import org.jvnet.substance.fonts.SubstanceFontUtilities;
//import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.*;

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


// Since Substance Look & Fell is discontinued, so is this HiDPI Simulator.


/** A demo application that shows some components in a GUI and how they will look on a HiDPI screen.
 */
public class HiDPISimulator
{
//	static final String SYSTEM_LAF_NAME = "System";
//	static final String SUBSTANCE_LAF_NAME = "Substance";
//	static final String OCEAN_LAF_NAME = "Ocean";
//	static final String NUMBUS_LAF_NAME = "Nimbus (Soon..)";
//
//	static JFrame APP_GUI_FRAME;
//	static HiDPIDemoPanel HiDPIDEMO_PANEL;
//	static JPanel SIM_PANEL;
//	static JPanel MIRROR_PANEL;
//	static JScrollPane MAIN_SCROLL;
//	static JTextArea TEXT_AREA;
//
//	static boolean SCALE_LAF = false;
//	static boolean SCALE_FONTS = true;
//	static boolean SCALE_LAYOUT = true;
//
//	static boolean PAINT_GHOSTED = false;
//
//	static BufferedImage GUI_BUF = null;
//	static BufferedImage ORIG_GUI_BUF = null;
//
//	static int CUR_DPI = PlatformDefaults.getDefaultDPI();
//	static HashMap<String, Font> ORIG_DEFAULTS;
//
//	private static JPanel createScaleMirror()
//	{
//		return new JPanel(new MigLayout()) {
//			protected void paintComponent(Graphics g)
//			{
//				super.paintComponent(g);
//
//				if (GUI_BUF != null) {
//					Graphics2D g2 = (Graphics2D) g.create();
//
//					double dpi = getToolkit().getScreenResolution();
//
//					AffineTransform oldTrans = g2.getTransform();
//					g2.scale(dpi / CUR_DPI, dpi / CUR_DPI);
//
//					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//
//					g2.drawImage(GUI_BUF, 0, 0, null);
//
//					g2.setTransform(oldTrans);
//
//					if (ORIG_GUI_BUF != null && PAINT_GHOSTED) {
//						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
//						g2.drawImage(ORIG_GUI_BUF, 0, 0, null);
//					}
//
//					g2.dispose();
//				}
//			}
//
//			public Dimension getPreferredSize()
//			{
//				return ORIG_GUI_BUF != null ? new Dimension(ORIG_GUI_BUF.getWidth(), ORIG_GUI_BUF.getHeight()) : new Dimension(100, 100);
//			}
//
//			public Dimension getMinimumSize()
//			{
//				return getPreferredSize();
//			}
//		};
//	}
//
//	private static JPanel createSimulator()
//	{
//		final JRadioButton scaleCompsFonts = new JRadioButton("UIManager Font Substitution", true);
//		final JRadioButton scaleCompsLaf = new JRadioButton("Native Look&Feel Scaling", false);
//		final JRadioButton scaleCompsNone = new JRadioButton("No Scaling", false);
//		final JRadioButton scaleLayoutMig = new JRadioButton("Native MigLayout Gap Scaling", true);
//		final JRadioButton scaleLayoutNone = new JRadioButton("No Gap Scaling", false);
//		final JComboBox lafCombo = new JComboBox(new String[] {SYSTEM_LAF_NAME, SUBSTANCE_LAF_NAME, OCEAN_LAF_NAME, NUMBUS_LAF_NAME});
//
//		final ButtonGroup bg1 = new ButtonGroup();
//		final ButtonGroup bg2 = new ButtonGroup();
//		final JCheckBox ghostCheck = new JCheckBox("Overlay \"Optimal\" HiDPI Result");
//
//		scaleCompsLaf.setEnabled(false);
//
//		bg1.add(scaleCompsFonts);
//		bg1.add(scaleCompsLaf);
//		bg1.add(scaleCompsNone);
//
//		bg2.add(scaleLayoutMig);
//		bg2.add(scaleLayoutNone);
//
//		Vector<String> dpiStrings = new Vector<String>();
//
//		for (float f = 0.5f; f < 2.01f; f += 0.1f)
//			dpiStrings.add(Math.round(PlatformDefaults.getDefaultDPI() * f) + " DPI (" + Math.round(f * 100f + 0.499f) + "%)");
//
//		final JComboBox dpiCombo = new JComboBox(dpiStrings);
//		dpiCombo.setSelectedIndex(5);
//
//		JPanel panel = new JPanel(new MigLayout("alignx center, insets 10px, flowy", "[]", "[]3px[]0px[]"));
//
//		JLabel lafLabel = new JLabel("Look & Feel:");
//		JLabel sliderLabel = new JLabel("Simulated DPI:");
//		JLabel scaleLabel = new JLabel("Component/Text Scaling:");
//		JLabel layoutLabel = new JLabel("LayoutManager Scaling:");
//		JLabel visualsLabel = new JLabel("Visual Aids:");
//
//		panel.add(lafLabel,        "");
//		panel.add(lafCombo,        "wrap");
//
//		panel.add(sliderLabel,     "");
//		panel.add(dpiCombo,        "wrap");
//
//		panel.add(scaleLabel,      "");
//		panel.add(scaleCompsFonts, "");
//		panel.add(scaleCompsLaf,   "");
//		panel.add(scaleCompsNone,  "wrap");
//
//		panel.add(layoutLabel,     "");
//		panel.add(scaleLayoutMig,  "");
//		panel.add(scaleLayoutNone, "wrap");
//
//		panel.add(visualsLabel,    "");
//		panel.add(ghostCheck,      "");
//
//		lockFont(dpiCombo, scaleCompsFonts, scaleCompsLaf, scaleLayoutMig, scaleCompsNone, lafCombo, ghostCheck, panel, lafLabel, sliderLabel, scaleLayoutNone, scaleLabel, layoutLabel, visualsLabel);
//
//		lafCombo.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e)
//			{
//				GUI_BUF = null;
//				try {
//					Object s = lafCombo.getSelectedItem();
//
//					dpiCombo.setSelectedIndex(5);
//
//					if (s.equals(SYSTEM_LAF_NAME)) {
//						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//					} else if (s.equals(SUBSTANCE_LAF_NAME)) {
//						UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
//					} else if (s.equals(OCEAN_LAF_NAME)) {
//						UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//					} else {
//						JOptionPane.showMessageDialog(APP_GUI_FRAME, "Nimbus will be included as soon as it is ready!");
//					}
//
//					if (ORIG_DEFAULTS != null) {
//						for (String key : ORIG_DEFAULTS.keySet())
//							UIManager.put(key, null);
//					}
//					ORIG_DEFAULTS = null;
//
//					if (UIManager.getLookAndFeel().getName().toLowerCase().contains("windows")) {
//						UIManager.put("TextArea.font", UIManager.getFont("TextField.font"));
//					} else {
//						UIManager.put("TextArea.font", null);
//					}
//
//					SwingUtilities.updateComponentTreeUI(APP_GUI_FRAME);
//					MAIN_SCROLL.setBorder(null);
//
//					if (s.equals(SYSTEM_LAF_NAME)) {
//						if (scaleCompsLaf.isSelected())
//							scaleCompsFonts.setSelected(true);
//
//						scaleCompsLaf.setEnabled(false);
//
//					} else if (s.equals(SUBSTANCE_LAF_NAME)) {
//						scaleCompsLaf.setEnabled(true);
//
//					} else if (s.equals(OCEAN_LAF_NAME)) {
//						if (scaleCompsLaf.isSelected())
//							scaleCompsFonts.setSelected(true);
//						scaleCompsLaf.setEnabled(false);
//					}
//
//					setDPI(CUR_DPI);
//
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//			}
//		});
//
//		ghostCheck.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent actionEvent)
//			{
//				GUI_BUF = null;
//				PAINT_GHOSTED = ghostCheck.isSelected();
//				APP_GUI_FRAME.repaint();
//			}
//		});
//
//		scaleLayoutMig.addItemListener(new ItemListener() {
//			public void itemStateChanged(ItemEvent e)
//			{
//				GUI_BUF = null;
//				SCALE_LAYOUT = scaleLayoutMig.isSelected();
//				setDPI(CUR_DPI);
//			}
//		});
//
//		ItemListener il = new ItemListener() {
//			public void itemStateChanged(ItemEvent e)
//			{
//				if (e.getStateChange() == ItemEvent.SELECTED) {
//					GUI_BUF = null;
//					SCALE_LAF = scaleCompsLaf.isSelected();
//					SCALE_FONTS = scaleCompsFonts.isSelected();
//					setDPI(CUR_DPI);
//				}
//			}
//		};
//
//		scaleCompsLaf.addItemListener(il);
//		scaleCompsFonts.addItemListener(il);
//		scaleCompsNone.addItemListener(il);
//
//		dpiCombo.addItemListener(new ItemListener()	{
//			public void itemStateChanged(ItemEvent e)
//			{
//				if (e.getStateChange() == ItemEvent.SELECTED) {
//					GUI_BUF = null;
//					CUR_DPI = Integer.parseInt(dpiCombo.getSelectedItem().toString().substring(0, 3).trim());
//					setDPI(CUR_DPI);
//				}
//			}
//		});
//
//		return panel;
//	}
//
//	private static void lockFont(Component ... comps)
//	{
//		for (Component c : comps) {
//			Font f = c.getFont();
//			c.setFont(f.deriveFont((float) f.getSize()));
//		}
//	}
//
//	private static void revalidateGUI()
//	{
//		APP_GUI_FRAME.getContentPane().invalidate();
//		APP_GUI_FRAME.repaint();
//	}
//
//	private synchronized static void setDPI(int dpi)
//	{
//		float scaleFactor = dpi / (float) Toolkit.getDefaultToolkit().getScreenResolution();
//		TEXT_AREA.setSize(0, 0); // To reset for Swing TextArea horizontal size bug...
//		PlatformDefaults.setHorizontalScaleFactor(.1f);// Only so that the cache will be invalidated for sure
//		PlatformDefaults.setHorizontalScaleFactor(SCALE_LAYOUT ? scaleFactor : null);
//		PlatformDefaults.setVerticalScaleFactor(SCALE_LAYOUT ? scaleFactor : null);
//
//		float fontScale = SCALE_FONTS ? dpi / (float) Toolkit.getDefaultToolkit().getScreenResolution() : 1f;
//
//		if (ORIG_DEFAULTS == null) {
//			ORIG_DEFAULTS = new HashMap<String, Font>();
//
//			Set entries = new HashSet(UIManager.getLookAndFeelDefaults().keySet());
//			for (Iterator it = entries.iterator(); it.hasNext();) {
//				String key = it.next().toString();
//				Object value = UIManager.get(key);
//
//				if (value instanceof Font)
//					ORIG_DEFAULTS.put(key, (Font) value);
//			}
//		}
//
//		Set entries = ORIG_DEFAULTS.entrySet();
//		for (Iterator<Map.Entry<String, Font>> it = entries.iterator(); it.hasNext();) {
//			Map.Entry<String, Font> e = it.next();
//			Font origFont = e.getValue();
//
//			if (SCALE_LAF == false) {
//				UIManager.put(e.getKey(), new FontUIResource(origFont.deriveFont(origFont.getSize() * fontScale)));
//			} else {
//				UIManager.put(e.getKey(), null);
//			}
//		}
//
//		if (SCALE_LAF) {
//			scaleSubstanceLAF(scaleFactor);
//		} else if (UIManager.getLookAndFeel().getName().toLowerCase().contains("substance")) {
//			scaleSubstanceLAF(1);
//		}
//
//		SwingUtilities.updateComponentTreeUI(HiDPIDEMO_PANEL);
//		revalidateGUI();
//	}
//
//    private static void scaleSubstanceLAF(float factor)
//    {
//		SubstanceLookAndFeel.setFontPolicy(SubstanceFontUtilities.getScaledFontPolicy(factor));
//
//		try {
//			UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
//		} catch (Exception exc) {
//		}
//
//		SwingUtilities.updateComponentTreeUI(APP_GUI_FRAME);
//	    MAIN_SCROLL.setBorder(null);
//    }
//
//	public static void main(String[] args)
//	{
//		try {
//			System.setProperty("apple.laf.useScreenMenuBar", "true");
//			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HiDPI Simulator");
//		} catch(Exception ex) {}
//
//		PlatformDefaults.setDefaultHorizontalUnit(UnitValue.LPX);
//		PlatformDefaults.setDefaultVerticalUnit(UnitValue.LPY);
//
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				try {
////					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//
//				if (UIManager.getLookAndFeel().getName().toLowerCase().contains("windows"))
//					UIManager.put("TextArea.font", UIManager.getFont("TextField.font"));
//
//				APP_GUI_FRAME = new JFrame("Resolution Independence Simulator");
//
////				RepaintManager.currentManager(APP_GUI_FRAME).setDoubleBufferingEnabled(false);
//
//				JPanel uberPanel = new JPanel(new MigLayout("fill, insets 0px, nocache"));
//
//				JPanel mainPanel = new JPanel(new MigLayout("fill, insets 0px, nocache")) {
//					public void paintComponent(Graphics g)
//					{
//						Graphics2D g2 = (Graphics2D) g.create();
//
//						g2.setPaint(new GradientPaint(0, 0, new Color(20, 20, 30), 0, getHeight(), new Color(90, 90, 110), false));
//						g2.fillRect(0, 0, getWidth(), getHeight());
//
//						g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13));
//						g2.setPaint(Color.WHITE);
//						g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//
//						g2.drawString("Left panel shows the scaled version. Right side shows how this would look on a HiDPI screen. It should look the same as the original panel!", 10, 19);
//
//						g2.dispose();
//					}
//				};
//
//				HiDPIDEMO_PANEL = new HiDPIDemoPanel();
//				SIM_PANEL = createSimulator();
//				MIRROR_PANEL = createScaleMirror();
//
//				MAIN_SCROLL = new JScrollPane(mainPanel);
//				MAIN_SCROLL.setBorder(null);
//
//				mainPanel.add(HiDPIDEMO_PANEL, "align center center, split, span, width pref!");
//				mainPanel.add(MIRROR_PANEL, "id mirror, gap 20px!, width pref!");
//
//				uberPanel.add(SIM_PANEL,    "dock south");
//				uberPanel.add(MAIN_SCROLL,  "dock center");
//
//				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//				APP_GUI_FRAME.setContentPane(uberPanel);
//				APP_GUI_FRAME.setSize(Math.min(1240, screenSize.width), Math.min(950, screenSize.height - 30));
//				APP_GUI_FRAME.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//				APP_GUI_FRAME.setLocationRelativeTo(null);
//				APP_GUI_FRAME.setVisible(true);
//			}
//		});
//	}
//}
//
//class HiDPIDemoPanel extends JPanel {
//	public HiDPIDemoPanel()
//	{
//		super(new MigLayout());
//
//        JLabel jLabel1 = new JLabel("A Small Label:");
//        JTextField jTextField1 = new JTextField(10);
//        JButton jButton1 = new JButton("Cancel");
//        JButton jButton2 = new JButton("OK");
//        JButton jButton4 = new JButton("Help");
//        JList jList1 = new JList();
//        JLabel jLabel2 = new JLabel("Label:");
//        JTextField jTextField2 = new JTextField(10);
//        JLabel jLabel3 = new JLabel("This is another section");
//        JSeparator jSeparator1 = new JSeparator();
//        JTextArea jTextArea1 = new JTextArea("Some general text that takes place, doesn't offend anyone and fills some pixels.", 3, 30); // colums set always!
//        JLabel jLabel4 = new JLabel("Some Text Area");
//        JLabel jLabel6 = new JLabel("Some List:");
//        JComboBox jComboBox1 = new JComboBox();
//        JCheckBox jCheckBox1 = new JCheckBox("Orange");
//
//		JScrollBar scroll1 = new JScrollBar(JScrollBar.VERTICAL);
//		JScrollBar scroll2 = new JScrollBar(JScrollBar.HORIZONTAL, 30, 40, 0, 100);
//		JRadioButton radio = new JRadioButton("Apple");
//		JProgressBar prog = new JProgressBar();
//		prog.setValue(50);
//		JSpinner spinner = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
//		JTree tree = new JTree();
//		tree.setOpaque(false);
//		tree.setEnabled(false);
//
//        jList1.setModel(new AbstractListModel() {
//            String[] strings = { "Donald Duck", "Mickey Mouse", "Pluto", "Cartman" };
//            public int getSize() { return strings.length; }
//            public Object getElementAt(int i) { return strings[i]; }
//        });
//
//        jList1.setVisibleRowCount(4);
//        jList1.setBorder(new LineBorder(Color.GRAY));
//        jTextArea1.setLineWrap(true);
//		jTextArea1.setWrapStyleWord(true);
//		jTextArea1.setBorder(new LineBorder(Color.GRAY));
//        jComboBox1.setModel(new DefaultComboBoxModel(new String[] {"Text in ComboBox"}));
//        jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
//
//		add(jLabel1,       "split, span");
//		add(jTextField1,   "");
//		add(jLabel2,       "gap unrelated");
//		add(jTextField2,   "wrap");
//		add(jLabel3,       "split, span");
//		add(jSeparator1,   "growx, span, gap 2, wrap unrelated");
//		add(jLabel4,       "wrap 2");
//		add(jTextArea1,    "span, wmin 150, wrap unrelated");
//		add(jLabel6,       "wrap 2");
//		add(jList1,        "split, span");
//		add(scroll1,       "growy");
//		add(prog,          "width 80!");
//		add(tree,          "wrap unrelated");
//
//		add(scroll2,       "split, span, growx");
//		add(spinner,       "wrap unrelated");
//		add(jComboBox1,    "span, split");
//		add(radio,         "");
//		add(jCheckBox1,    "wrap unrelated");
//		add(jButton4,      "split, span, tag help2");
//		add(jButton2,      "tag ok");
//		add(jButton1,      "tag cancel");
//
//		HiDPISimulator.TEXT_AREA = jTextArea1;
//	}
//
//	public void paint(Graphics g)
//	{
//		if (HiDPISimulator.GUI_BUF == null) {
//			BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
//
//			Graphics2D g2 = bi.createGraphics();
//
//			super.paint(g2);
//
//			g2.dispose();
//
//			g.drawImage(bi, 0, 0, null);
//
//			HiDPISimulator.GUI_BUF = bi;
//
//			if (HiDPISimulator.CUR_DPI == PlatformDefaults.getDefaultDPI())
//				HiDPISimulator.ORIG_GUI_BUF = bi;
//
//			SwingUtilities.invokeLater(new Runnable() {
//				public void run() {
//					HiDPISimulator.MIRROR_PANEL.revalidate();
//					HiDPISimulator.MIRROR_PANEL.repaint();
//				}
//			});
//		} else {
//			super.paint(g);
//		}
//	}
}
