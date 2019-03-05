package net.miginfocom.demo;

import net.miginfocom.layout.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

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
public class SwingDemo extends JFrame
{
	public static final int INITIAL_INDEX = 0;
	private static final boolean DEBUG = false;
	private static final boolean OPAQUE = false;

	private static final String[][] panels = new String[][] {
//		{"Test", "BugTestApp, disregard"},
		{"Welcome", "\n\n         \"MigLayout makes complex layouts easy and normal layouts one-liners.\""},
		{"Quick Start", "This is an example of how to build a common dialog type. Note that there are no special components, nested panels or absolute references to cell positions. If you look at the source code you will see that the layout code is very simple to understand."},
		{"Plain", "A simple example on how simple it is to create normal forms. No builders needed since the whole layout manager works like a builder."},
		{"Alignments", "Shows how the alignment of components are specified. At the top/left is the alignment for the column/row. The components have no alignments specified.\n\nNote that baseline alignment will be interpreted as 'center' before JDK 6."},
		{"Cell Alignments", "Shows how components are aligned when both column/row alignments and component constraints are specified. At the top/left are the alignment for the column/row and the text on the buttons is the component constraint that will override the column/row alignment if it is an alignment.\n\nNote that baseline alignment will be interpreted as 'center' before JDK 6."},
		{"Basic Sizes", "A simple example that shows how to use the column or row min/preferred/max size to set the sizes of the contained components and also an example that shows how to do this directly in the component constraints."},
		{"Growing", "A simple example that shows how to use the growx and growy constraint to set the sizes and how they should grow to fit the available size. Both the column/row and the component grow/shrink constraints can be set, but the components will always be confined to the space given by its column/row."},
		{"Grow Shrink", "Demonstrates the very flexible grow and shrink constraints that can be set on a component.\nComponents can be divided into grow/shrink groups and also have grow/shrink weight within each of those groups.\n\nBy default " +
						"components shrink to their inherent (or specified) minimum size, but they don't grow."},
		{"Span", "This example shows the powerful spanning and splitting that can be specified in the component constraints. With spanning any number of cells can be merged with the additional option to split that space for more than one component. This makes layouts very flexible and reduces the number of times you will need nested panels to very few."},
		{"Flow Direction", "Shows the different flow directions. Flow direction for the layout specifies if the next cell will be in the x or y dimension. Note that it can be a different flow direction in the slit cell (the middle cell is slit in two). Wrap is set to 3 for all panels."},
		{"Grouping", "Sizes for both components and columns/rows can be grouped so they get the same size. For instance buttons in a button bar can be given a size-group so that they will all get " +
					 "the same minimum and preferred size (the largest within the group). Size-groups can be set for the width, height or both."},
		{"Units", "Demonstrates the basic units that are understood by MigLayout. These units can be extended by the user by adding one or more UnitConverter(s)."},
		{"Component Sizes", "Minimum, preferred and maximum component sizes can be overridden in the component constraints using any unit type. The format to do this is short and simple to understand. You simply specify the " +
							"min, preferred and max sizes with a colon between.\n\nAbove are some examples of this. An exclamation mark means that the value will be used for all sizes."},
		{"Bound Sizes", "Shows how to create columns that are stable between tabs using minimum sizes."},
		{"Cell Position", "Even though MigLayout has automatic grid flow you can still specify the cell position explicitly. You can even combine absolute (x, y) and flow (skip, wrap and newline) constraints to build your layout."},
		{"Orientation", "MigLayout supports not only right-to-left orientation, but also bottom-to-top. You can even set the flow direction so that the flow is vertical instead of horizontal. It will automatically " +
						"pick up if right-to-left is to be used depending on the ComponentWrapper, but it can also be manually set for every layout."},
		{"Absolute Position", "Demonstrates the option to place any number of components using absolute coordinates. This can be just the position (if min/preferred size) using \"x y p p\" format or" +
							  "the bounds using the \"x1 y1 x2 y2\" format. Any unit can be used and percent is relative to the parent.\nAbsolute components will not disturb the flow or occupy cells in the grid. " +
							  "Absolute positioned components will be taken into account when calculating the container's preferred size."},
		{"Component Links", "Components can be linked to any side of any other component. It can be a forward, backward or cyclic link references, as long as it is stable and won't continue to change value over many iterations." +
							"Links are referencing the ID of another component. The ID can be overridden by the component's constrains or is provided by the ComponentWrapper. For instance it will use the component's 'name' on Swing.\n" +
							"Since the links can be combined with any expression (such as 'butt1.x+10' or 'max(button.x, 200)' the links are very customizable."},
		{"Docking", "Docking components can be added around the grid. The docked component will get the whole width/height on the docked side by default, however this can be overridden. When all docked components are laid out, whatever space " +
					"is left will be available for the normal grid laid out components. Docked components does not in any way affect the flow in the grid.\n\nSince the docking runs in the same code path " +
					"as the normal layout code the same properties can be specified for the docking components. You can for instance set the sizes and alignment or link other components to their docked component's bounds."},
		{"Button Bars", "Button order is very customizable and are by default different on the supported platforms. E.g. Gaps, button order and minimum button size are properties that are 'per platform'. MigLayout picks up the current platform automatically and adjusts the button order and minimum button size accordingly, all without using a button builder or any other special code construct."},
		{"Visual Bounds", "Human perceptible bounds may not be the same as the mathematical bounds for the component. This is for instance the case if there is a drop shadow painted by the component's border. MigLayout can compensate " +
						  "for this in a simple way. Note the top middle tab-component, it is not aligned visually correct on Windows XP. For the second tab the bounds are corrected automatically on Windows XP."},
		{"Debug", "Demonstrates the non-intrusive way to get visual debugging aid. There is no need to use a special DebugPanel or anything that will need code changes. The user can simply turn on debug on the layout manager by using the \"debug\" constraint and it will " +
				  "continuously repaint the panel with debug information on top. This means you don't have to change your code to debug!"},
		{"Layout Showdown", "This is an implementation of the Layout Showdown posted on java.net by John O'Conner. The first tab is a pure implementation of the showdown that follows all the rules. The second tab is a slightly fixed version that follows some improved layout guidelines." +
		                    "The source code is for both the first and for the fixed version. Note the simplification of the code for the fixed version. Writing better layouts with MiG Layout is easier that writing bad.\n\nReference: http://weblogs.java.net/blog/joconner/archive/2006/10/more_informatio.html"},
		{"API Constraints1", "This dialog shows the constraint API added to v2.0. It works the same way as the string constraints but with chained method calls. See the source code for details."},
		{"API Constraints2", "This dialog shows the constraint API added to v2.0. It works the same way as the string constraints but with chained method calls. See the source code for details."}
	};

	private int lastIndex = -10;

	private JPanel contentPanel = DEBUG ? new JPanel(new BorderLayout()) : new JPanel(new MigLayout("wrap", "[]unrel[grow]", "[grow][pref]"));

	private JTabbedPane layoutPickerTabPane = new JTabbedPane();
		private JList pickerList = new JList(new DefaultListModel());

	private JTabbedPane southTabPane = new JTabbedPane();
		private JScrollPane descrTextAreaScroll = createTextAreaScroll("", 5, 80, true);
		private JTextArea descrTextArea = (JTextArea) descrTextAreaScroll.getViewport().getView();

		private JScrollPane sourceTextAreaScroll = null;
		private JTextArea sourceTextArea = null;

	private JPanel layoutDisplayPanel = new JPanel(new BorderLayout(0, 0));
	private static boolean buttonOpaque = true;
	private static boolean contentAreaFilled = true;
	private JFrame sourceFrame = null;
	private JTextArea sourceFrameTextArea = null;

	private static int benchRuns = 0;
	private static long startupMillis = 0;
	private static long timeToShowMillis = 0;
	private static long benchRunTime = 0;
	private static String benchOutFileName = null;
	private static boolean append = false;
	private static long lastRunTimeStart = 0;
	private static StringBuffer runTimeSB = null;

	public static void main(String args[])
	{
		try {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MiGLayout Swing Demo");
		} catch(Throwable ex) {
			// Beacuse we did not have permissions.
		}

		startupMillis = System.currentTimeMillis();

		String laf = UIManager.getSystemLookAndFeelClassName();

		if (args.length > 0) {
			for (int i = 0; i <args.length; i++) {
				String arg = args[i].trim();
				if (arg.startsWith("-laf")) {
					laf = arg.substring(4);
				} else if (arg.startsWith("-bench")) {
					benchRuns = 10;
					try {
						benchRuns = Integer.parseInt(arg.substring(6));
					} catch(Exception ex) {}
				} else if (arg.startsWith("-bout")) {
					benchOutFileName = arg.substring(5);
				} else if (arg.startsWith("-append")) {
					append = true;
				} else if (arg.startsWith("-verbose")) {
					runTimeSB = new StringBuffer(256);
				} else if (arg.equals("-steel")) {
					laf = "javax.swing.plaf.metal.MetalLookAndFeel";
					System.setProperty("swing.metalTheme", "steel");
				} else if (arg.equals("-ocean")) {
					laf = "javax.swing.plaf.metal.MetalLookAndFeel";
				} else {
					System.out.println("Usage: [-laf[look_&_feel_class_name]] [-bench[#_of_runs]] [-bout[benchmark_results_filename]] [-append] [-steel] [-ocean]\n" +
					                   " -laf    Set the Application Look&Feel. (Look and feel must be in Classpath)\n" +
					                   " -bench  Run demo as benchmark. Run count can be appended. 10 is default.\n" +
					                   " -bout   Benchmark results output filename.\n" +
					                   " -append Appends the result to the -bout file.\n" +
					                   " -verbose Print the times of every run.\n" +
					                   " -steel  Sets the old Steel theme for Sun's Metal look&feel.\n" +
					                   " -ocean  Sets the Ocean theme for Sun's Metal look&feel.\n" +
					                   "\nExamples:\n" +
					                   " java -jar swingdemoapp.jar -bench -boutC:/bench.txt -append\n" +
					                   " java -jar swingdemoapp.jar -ocean -bench20\n" +
					                   " java -cp c:\\looks-2.0.4.jar;.\\swingdemoapp.jar net.miginfocom.demo.SwingDemo -lafcom.jgoodies.looks.plastic.PlasticLookAndFeel -bench20 -boutC:/bench.txt");
					System.exit(0);
				}
			}
		}

		if (benchRuns == 0)
			LayoutUtil.setDesignTime(null, true); // So that the string constraints to create the UnitValues is saved and can be recreated exactly.

//		laf = "net.sourceforge.napkinlaf.NapkinLookAndFeel";
//		laf = "javax.swing.plaf.metal.MetalLookAndFeel";
//		System.setProperty("swing.metalTheme", "steel");
//		laf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
//		laf = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
//		laf = "com.jgoodies.looks.plastic.PlasticLookAndFeel";
//		laf = "com.jgoodies.looks.plastic.Plastic3DLookAndFeel";
//		laf = "com.jgoodies.looks.plastic.PlasticXPLookAndFeel";
//		laf = "com.jgoodies.looks.windows.WindowsLookAndFeel";

		if (laf.endsWith("WindowsLookAndFeel"))
			buttonOpaque = false;

		if (laf.endsWith("AquaLookAndFeel"))
			contentAreaFilled = false;

		final String laff = laf;

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try {
					UIManager.setLookAndFeel(laff);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				new SwingDemo();
			}
		});
	}

	public SwingDemo()
	{
		super("MigLayout Swing Demo v2.5 - MigLayout v" + LayoutUtil.getVersion());

//		if (benchRuns > 0)
//			RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);

		if (benchRuns == 0) {
			sourceTextAreaScroll = createTextAreaScroll("", 5, 80, true);
			sourceTextArea = (JTextArea) sourceTextAreaScroll.getViewport().getView();
		}

		if (DEBUG) {
			contentPanel.add(layoutDisplayPanel, BorderLayout.CENTER);
//		    contentPanel.add(layoutPickerTabPane, BorderLayout.WEST);
//		    contentPanel.add(descriptionTabPane, BorderLayout.SOUTH);
		} else {
			contentPanel.add(layoutPickerTabPane, "spany,grow");
			contentPanel.add(layoutDisplayPanel, "grow");
			contentPanel.add(southTabPane, "growx");
		}

		setContentPane(contentPanel);

			pickerList.setOpaque(OPAQUE);
			((DefaultListCellRenderer) pickerList.getCellRenderer()).setOpaque(OPAQUE);
			pickerList.setSelectionForeground(new Color(0, 0, 220));
			pickerList.setBackground(null);
			pickerList.setBorder(new EmptyBorder(2, 5, 0, 4));
			pickerList.setFont(pickerList.getFont().deriveFont(Font.BOLD));
		layoutPickerTabPane.addTab("Example Browser", pickerList);

			descrTextAreaScroll.setBorder(null);
			descrTextAreaScroll.setOpaque(OPAQUE);
			descrTextAreaScroll.getViewport().setOpaque(OPAQUE);
			descrTextArea.setOpaque(OPAQUE);
			descrTextArea.setEditable(false);
			descrTextArea.setBorder(new EmptyBorder(0, 4, 0, 4));
		southTabPane.addTab("Description", descrTextAreaScroll);


		if (sourceTextArea != null) {
			sourceTextAreaScroll.setBorder(null);
			sourceTextAreaScroll.setOpaque(OPAQUE);
			sourceTextAreaScroll.getViewport().setOpaque(OPAQUE);
			sourceTextAreaScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			sourceTextArea.setOpaque(OPAQUE);
			sourceTextArea.setLineWrap(false);
			sourceTextArea.setWrapStyleWord(false);
			sourceTextArea.setEditable(false);
			sourceTextArea.setBorder(new EmptyBorder(0, 4, 0, 4));
			sourceTextArea.setFont(new Font("monospaced", Font.PLAIN, 11));
			southTabPane.addTab("Source Code", sourceTextAreaScroll);

			southTabPane.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e)
				{
					if (e.getClickCount() == 2)
						showSourceInFrame();
				}
			});
		}

		for (int i = 0; i < panels.length; i++)
			((DefaultListModel) pickerList.getModel()).addElement(panels[i][0]);

		try {
			if (UIManager.getLookAndFeel().getID().equals("Aqua")) {
				setSize(1000, 750);
			} else {
				setSize(900, 650);
			}
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
		} catch(Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}

		pickerList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e)
			{
				int ix = pickerList.getSelectedIndex();
				if (ix == -1 || lastIndex == ix)
					return;

				lastIndex = ix;

				String methodName = "create" + panels[ix][0].replace(' ', '_');
				layoutDisplayPanel.removeAll();
				try {
					pickerList.requestFocusInWindow();
					final JComponent panel = (JComponent) SwingDemo.class.getMethod(methodName, new Class[0]).invoke(SwingDemo.this, new Object[0]);
					layoutDisplayPanel.add(panel);
					descrTextArea.setText(panels[ix][1]);
					descrTextArea.setCaretPosition(0);
					contentPanel.revalidate();
				} catch (Exception e1) {
					e1.printStackTrace();   // Should never happen...
				}
				southTabPane.setSelectedIndex(0);
			}
		});

		pickerList.requestFocusInWindow();
		Toolkit.getDefaultToolkit().setDynamicLayout(true);

		if (benchRuns > 0) {
			doBenchmark();
		} else {
			pickerList.setSelectedIndex(INITIAL_INDEX);

			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
				public boolean dispatchKeyEvent(KeyEvent e)
				{
					if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_B && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0) {
						startupMillis = System.currentTimeMillis();
						timeToShowMillis = System.currentTimeMillis() - startupMillis;
						benchRuns = 1;
						doBenchmark();
						return true;
					}
					return false;
				}
			});
		}
	}

	private void doBenchmark()
	{
		Thread benchThread = new Thread() {
			public void run()
			{
				for (int j = 0; j < benchRuns; j++) {
					lastRunTimeStart = System.currentTimeMillis();
					for (int i = 0, iCnt = pickerList.getModel().getSize(); i < iCnt; i++) {

						if (benchRuns > 0 && panels[i][0].equals("Visual Bounds"))
							continue;   // the SWT version does not have Visual bounds...

						final int ii = i;
						try {
							SwingUtilities.invokeAndWait(new Runnable() {
								public void run() {
									pickerList.setSelectedIndex(ii);
									Toolkit.getDefaultToolkit().sync();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}

						Component[] comps = layoutDisplayPanel.getComponents();
						for (int cIx = 0; cIx < comps.length; cIx++) {
							if (comps[cIx] instanceof JTabbedPane) {
								final JTabbedPane tp = (JTabbedPane) comps[cIx];

								for (int k = 0, kCnt = tp.getTabCount(); k < kCnt; k++) {
									final int kk = k;
									try {
										SwingUtilities.invokeAndWait(new Runnable() {
											public void run() {
												tp.setSelectedIndex(kk);
												Toolkit.getDefaultToolkit().sync();

												if (timeToShowMillis == 0)
													timeToShowMillis = System.currentTimeMillis() - startupMillis;
											}
										});
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
					if (runTimeSB != null) {
					   runTimeSB.append("Run ").append(j).append(": ");
					   runTimeSB.append(System.currentTimeMillis() - lastRunTimeStart).append(" millis.\n");
					}
				}

				benchRunTime = System.currentTimeMillis() - startupMillis - timeToShowMillis;

				final String message = "Java Version:       " + System.getProperty("java.version") + "\n" +
				                 "Look & Feel:        " + UIManager.getLookAndFeel().getDescription() + "\n" +
				                 "Time to Show:       " + timeToShowMillis + " millis.\n" +
				                 (runTimeSB != null ? runTimeSB.toString() : "") +
				                 "Benchmark Run Time: " + benchRunTime + " millis.\n" +
				                 "Average Run Time:   " + (benchRunTime / benchRuns) + " millis (" + benchRuns + " runs).\n\n";

				if (benchOutFileName == null) {
					JOptionPane.showMessageDialog(SwingDemo.this, message, "Results", JOptionPane.INFORMATION_MESSAGE);
				} else {
					FileWriter fw = null;
					try {
						fw = new FileWriter(benchOutFileName, append);
						fw.write(message);
					} catch(IOException ex) {
						ex.printStackTrace();
					} finally {
						if (fw != null)
							try {fw.close();} catch(IOException ex) {}
					}
				}
				System.out.println(message);
			}
		};
		benchThread.start();
	}

	private void setSource(String source)
	{
		if (benchRuns > 0 || sourceTextArea == null)
			return;

		if (source.length() > 0) {
			source = source.replaceAll("\t\t", "");
			source = "DOUBLE CLICK TAB TO SHOW SOURCE IN SEPARATE WINDOW!\n===================================================\n\n" + source;
		}
		sourceTextArea.setText(source);
		sourceTextArea.setCaretPosition(0);

		if (sourceFrame != null && sourceFrame.isVisible()) {
			sourceFrameTextArea.setText(source.length() > 105 ? source.substring(105) : "No Source Code Available!");
			sourceFrameTextArea.setCaretPosition(0);
		}
	}

	private void showSourceInFrame()
	{
		if (sourceTextArea == null)
			return;

		JScrollPane sourceFrameTextAreaScroll = createTextAreaScroll("", 5, 80, true);
		sourceFrameTextArea = (JTextArea) sourceFrameTextAreaScroll.getViewport().getView();

		sourceFrameTextAreaScroll.setBorder(null);
		sourceFrameTextAreaScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sourceFrameTextArea.setLineWrap(false);
		sourceFrameTextArea.setWrapStyleWord(false);
		sourceFrameTextArea.setEditable(false);
		sourceFrameTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
		sourceFrameTextArea.setFont(new Font("monospaced", Font.PLAIN, 12));

		String source = this.sourceTextArea.getText();
		sourceFrameTextArea.setText(source.length() > 105 ? source.substring(105) : "No Source Code Available!");
		sourceFrameTextArea.setCaretPosition(0);

		sourceFrame = new JFrame("Source Code");
		sourceFrame.getContentPane().add(sourceFrameTextAreaScroll, BorderLayout.CENTER);
		sourceFrame.setSize(700, 800);
		sourceFrame.setLocationRelativeTo(this);
		sourceFrame.setVisible(true);
	}

	public JComponent createTest()
	{
		JPanel thisp = new JPanel();

		MigLayout layout = new MigLayout();
		thisp.setLayout(layout);

//		thisp.add("wrap, span", new JButton());
//		thisp.add("newline, span", new JButton());
//		thisp.add("newline, span", new JButton());
//		thisp.add("newline, span", new JButton());

//		thisp.add("newline 100", new JButton("should have 40 pixels before"));

//		thisp.add("newline", new JButton());
//		thisp.add("newline", new JButton());
//		thisp.add("newline", new JButton());

		return thisp;
	}

	public JComponent createAPI_Constraints1()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 1).fill(2, 4).grow(100, 2, 4).align("right", 3).gap("15", 2);
		AC rowC = new AC().align("top", 7).gap("15!", 6).grow(100, 8);

		JPanel panel = createTabPanel(new MigLayout(layC, colC, rowC));    // Makes the background gradient

		// References to text fields not stored to reduce code clutter.

		JScrollPane list2 = new JScrollPane(new JList(new String[] {"Mouse, Mickey"}));
		panel.add(list2,                     new CC().spanY().growY().minWidth("150").gapX(null, "10"));

		panel.add(new JLabel("Last Name"));
		panel.add(new JTextField());
		panel.add(new JLabel("First Name"));
		panel.add(new JTextField(),          new CC().wrap().alignX("right"));
		panel.add(new JLabel("Phone"));
		panel.add(new JTextField());
		panel.add(new JLabel("Email"));
		panel.add(new JTextField());
		panel.add(new JLabel("Address 1"));
		panel.add(new JTextField(),          new CC().spanX().growX());
		panel.add(new JLabel("Address 2"));
		panel.add(new JTextField(),          new CC().spanX().growX());
		panel.add(new JLabel("City"));
		panel.add(new JTextField(),          new CC().wrap());
		panel.add(new JLabel("State"));
		panel.add(new JTextField());
		panel.add(new JLabel("Postal Code"));
		panel.add(new JTextField(10),        new CC().spanX(2).growX(0));
		panel.add(new JLabel("Country"));
		panel.add(new JTextField(),          new CC().wrap());

		panel.add(new JButton("New"),        new CC().spanX(5).split(5).tag("other"));
		panel.add(new JButton("Delete"),     new CC().tag("other"));
		panel.add(new JButton("Edit"),       new CC().tag("other"));
		panel.add(new JButton("Save"),       new CC().tag("other"));
		panel.add(new JButton("Cancel"),     new CC().tag("cancel"));

		tabbedPane.addTab("Layout Showdown (improved)", panel);

		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "LC layC = new LC().fill().wrap();\n" +
		          "AC colC = new AC().align(\"right\", 1).fill(2, 4).grow(100, 2, 4).align(\"right\", 3).gap(\"15\", 2);\n" +
		          "AC rowC = new AC().align(\"top\", 7).gap(\"15!\", 6).grow(100, 8);\n" +
		          "\n" +
		          "JPanel panel = createTabPanel(new MigLayout(layC, colC, rowC));    // Makes the background gradient\n" +
		          "\n" +
		          "// References to text fields not stored to reduce code clutter.\n" +
		          "\n" +
		          "JScrollPane list2 = new JScrollPane(new JList(new String[] {\"Mouse, Mickey\"}));\n" +
		          "panel.add(list2,                     new CC().spanY().growY().minWidth(\"150\").gapX(null, \"10\"));\n" +
		          "\n" +
		          "panel.add(new JLabel(\"Last Name\"));\n" +
		          "panel.add(new JTextField());\n" +
		          "panel.add(new JLabel(\"First Name\"));\n" +
		          "panel.add(new JTextField(),          new CC().wrap().alignX(\"right\"));\n" +
		          "panel.add(new JLabel(\"Phone\"));\n" +
		          "panel.add(new JTextField());\n" +
		          "panel.add(new JLabel(\"Email\"));\n" +
		          "panel.add(new JTextField());\n" +
		          "panel.add(new JLabel(\"Address 1\"));\n" +
		          "panel.add(new JTextField(),          new CC().spanX().growX());\n" +
		          "panel.add(new JLabel(\"Address 2\"));\n" +
		          "panel.add(new JTextField(),          new CC().spanX().growX());\n" +
		          "panel.add(new JLabel(\"City\"));\n" +
		          "panel.add(new JTextField(),          new CC().wrap());\n" +
		          "panel.add(new JLabel(\"State\"));\n" +
		          "panel.add(new JTextField());\n" +
		          "panel.add(new JLabel(\"Postal Code\"));\n" +
		          "panel.add(new JTextField(10),        new CC().spanX(2).growX(0));\n" +
		          "panel.add(new JLabel(\"Country\"));\n" +
		          "panel.add(new JTextField(),          new CC().wrap());\n" +
		          "\n" +
		          "panel.add(new JButton(\"New\"),        new CC().spanX(5).split(5).tag(\"other\"));\n" +
		          "panel.add(new JButton(\"Delete\"),     new CC().tag(\"other\"));\n" +
		          "panel.add(new JButton(\"Edit\"),       new CC().tag(\"other\"));\n" +
		          "panel.add(new JButton(\"Save\"),       new CC().tag(\"other\"));\n" +
		          "panel.add(new JButton(\"Cancel\"),     new CC().tag(\"cancel\"));\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Layout Showdown (improved)\", panel);");

		return tabbedPane;
	}

	public JComponent createAPI_Constraints2()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		LC layC = new LC().fill().wrap();
		AC colC = new AC().align("right", 0).fill(1, 3).grow(100, 1, 3).align("right", 2).gap("15", 1);
		AC rowC = new AC().index(6).gap("15!").align("top").grow(100, 8);

		JPanel panel = createTabPanel(new MigLayout(layC, colC, rowC));    // Makes the background gradient

		// References to text fields not stored to reduce code clutter.

		panel.add(new JLabel("Last Name"));
		panel.add(new JTextField());
		panel.add(new JLabel("First Name"));
		panel.add(new JTextField(),          new CC().wrap());
		panel.add(new JLabel("Phone"));
		panel.add(new JTextField());
		panel.add(new JLabel("Email"));
		panel.add(new JTextField());
		panel.add(new JLabel("Address 1"));
		panel.add(new JTextField(),          new CC().spanX().growX());
		panel.add(new JLabel("Address 2"));
		panel.add(new JTextField(),          new CC().spanX().growX());
		panel.add(new JLabel("City"));
		panel.add(new JTextField(),          new CC().wrap());
		panel.add(new JLabel("State"));
		panel.add(new JTextField());
		panel.add(new JLabel("Postal Code"));
		panel.add(new JTextField(10),        new CC().spanX(2).growX(0));
		panel.add(new JLabel("Country"));
		panel.add(new JTextField(),          new CC().wrap());

		panel.add(createButton("New"),        new CC().spanX(5).split(5).tag("other"));
		panel.add(createButton("Delete"),     new CC().tag("other"));
		panel.add(createButton("Edit"),       new CC().tag("other"));
		panel.add(createButton("Save"),       new CC().tag("other"));
		panel.add(createButton("Cancel"),     new CC().tag("cancel"));

		tabbedPane.addTab("Layout Showdown (improved)", panel);

		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "LC layC = new LC().fill().wrap();\n" +
		          "AC colC = new AC().align(\"right\", 0).fill(1, 3).grow(100, 1, 3).align(\"right\", 2).gap(\"15\", 1);\n" +
		          "AC rowC = new AC().index(6).gap(\"15!\").align(\"top\").grow(100, 8);\n" +
		          "\n" +
		          "JPanel panel = createTabPanel(new MigLayout(layC, colC, rowC));    // Makes the background gradient\n" +
		          "\n" +
		          "// References to text fields not stored to reduce code clutter.\n" +
		          "\n" +
		          "panel.add(new JLabel(\"Last Name\"));\n" +
		          "panel.add(new JTextField());\n" +
		          "panel.add(new JLabel(\"First Name\"));\n" +
		          "panel.add(new JTextField(),          new CC().wrap());\n" +
		          "panel.add(new JLabel(\"Phone\"));\n" +
		          "panel.add(new JTextField());\n" +
		          "panel.add(new JLabel(\"Email\"));\n" +
		          "panel.add(new JTextField());\n" +
		          "panel.add(new JLabel(\"Address 1\"));\n" +
		          "panel.add(new JTextField(),          new CC().spanX().growX());\n" +
		          "panel.add(new JLabel(\"Address 2\"));\n" +
		          "panel.add(new JTextField(),          new CC().spanX().growX());\n" +
		          "panel.add(new JLabel(\"City\"));\n" +
		          "panel.add(new JTextField(),          new CC().wrap());\n" +
		          "panel.add(new JLabel(\"State\"));\n" +
		          "panel.add(new JTextField());\n" +
		          "panel.add(new JLabel(\"Postal Code\"));\n" +
		          "panel.add(new JTextField(10),        new CC().spanX(2).growX(0));\n" +
		          "panel.add(new JLabel(\"Country\"));\n" +
		          "panel.add(new JTextField(),          new CC().wrap());\n" +
		          "\n" +
		          "panel.add(createButton(\"New\"),        new CC().spanX(5).split(5).tag(\"other\"));\n" +
		          "panel.add(createButton(\"Delete\"),     new CC().tag(\"other\"));\n" +
		          "panel.add(createButton(\"Edit\"),       new CC().tag(\"other\"));\n" +
		          "panel.add(createButton(\"Save\"),       new CC().tag(\"other\"));\n" +
		          "panel.add(createButton(\"Cancel\"),     new CC().tag(\"cancel\"));\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Layout Showdown (improved)\", panel);");

		return tabbedPane;
	}

	public JComponent createLayout_Showdown()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel p = createTabPanel(new MigLayout("", "[]15[][grow,fill]15[grow]"));

		JScrollPane list1 = new JScrollPane(new JList(new String[] {"Mouse, Mickey"}));

		p.add(list1,                     "spany, growy, wmin 150");
		p.add(new JLabel("Last Name"));
		p.add(new JTextField());
		p.add(new JLabel("First Name"),  "split");  // split divides the cell
		p.add(new JTextField(),          "growx, wrap");
		p.add(new JLabel("Phone"));
		p.add(new JTextField());
		p.add(new JLabel("Email"),       "split");
		p.add(new JTextField(),          "growx, wrap");
		p.add(new JLabel("Address 1"));
		p.add(new JTextField(),          "span, growx"); // span merges cells
		p.add(new JLabel("Address 2"));
		p.add(new JTextField(),          "span, growx");
		p.add(new JLabel("City"));
		p.add(new JTextField(),          "wrap"); // wrap continues on next line
		p.add(new JLabel("State"));
		p.add(new JTextField());
		p.add(new JLabel("Postal Code"), "split");
		p.add(new JTextField(),          "growx, wrap");
		p.add(new JLabel("Country"));
		p.add(new JTextField(),          "wrap 15");

		p.add(createButton("New"),        "span, split, align left");
		p.add(createButton("Delete"),     "");
		p.add(createButton("Edit"),       "");
		p.add(createButton("Save"),       "");
		p.add(createButton("Cancel"),     "wrap push");

		tabbedPane.addTab("Layout Showdown (pure)", p);


		// Fixed version *******************************************

		JPanel p2 = createTabPanel(new MigLayout("", "[]15[][grow,fill]15[][grow,fill]"));    // Makes the background gradient

		// References to text fields not stored to reduce code clutter.

		JScrollPane list2 = new JScrollPane(new JList(new String[] {"Mouse, Mickey"}));
		p2.add(list2,                     "spany, growy, wmin 150");
		p2.add(new JLabel("Last Name"));
		p2.add(new JTextField());
		p2.add(new JLabel("First Name"));
		p2.add(new JTextField(),          "wrap");
		p2.add(new JLabel("Phone"));
		p2.add(new JTextField());
		p2.add(new JLabel("Email"));
		p2.add(new JTextField(),          "wrap");
		p2.add(new JLabel("Address 1"));
		p2.add(new JTextField(),          "span");
		p2.add(new JLabel("Address 2"));
		p2.add(new JTextField(),          "span");
		p2.add(new JLabel("City"));
		p2.add(new JTextField(),          "wrap");
		p2.add(new JLabel("State"));
		p2.add(new JTextField());
		p2.add(new JLabel("Postal Code"));
		p2.add(new JTextField(10),        "growx 0, wrap");
		p2.add(new JLabel("Country"));
		p2.add(new JTextField(),          "wrap 15");

		p2.add(createButton("New"),        "tag other, span, split");
		p2.add(createButton("Delete"),     "tag other");
		p2.add(createButton("Edit"),       "tag other");
		p2.add(createButton("Save"),       "tag other");
		p2.add(createButton("Cancel"),     "tag cancel, wrap push");

		tabbedPane.addTab("Layout Showdown (improved)", p2);


		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "JPanel p = createTabPanel(new MigLayout(\"\", \"[]15[][grow,fill]15[grow]\"));\n" +
		          "\n" +
		          "JScrollPane list1 = new JScrollPane(new JList(new String[] {\"Mouse, Mickey\"}));\n" +
		          "\n" +
		          "p.add(list1,                     \"spany, growy, wmin 150\");\n" +
		          "p.add(new JLabel(\"Last Name\"));\n" +
		          "p.add(new JTextField());\n" +
		          "p.add(new JLabel(\"First Name\"),  \"split\");  // split divides the cell\n" +
		          "p.add(new JTextField(),          \"growx, wrap\");\n" +
		          "p.add(new JLabel(\"Phone\"));\n" +
		          "p.add(new JTextField());\n" +
		          "p.add(new JLabel(\"Email\"),       \"split\");\n" +
		          "p.add(new JTextField(),          \"growx, wrap\");\n" +
		          "p.add(new JLabel(\"Address 1\"));\n" +
		          "p.add(new JTextField(),          \"span, growx\"); // span merges cells\n" +
		          "p.add(new JLabel(\"Address 2\"));\n" +
		          "p.add(new JTextField(),          \"span, growx\");\n" +
		          "p.add(new JLabel(\"City\"));\n" +
		          "p.add(new JTextField(),          \"wrap\"); // wrap continues on next line\n" +
		          "p.add(new JLabel(\"State\"));\n" +
		          "p.add(new JTextField());\n" +
		          "p.add(new JLabel(\"Postal Code\"), \"split\");\n" +
		          "p.add(new JTextField(),          \"growx, wrap\");\n" +
		          "p.add(new JLabel(\"Country\"));\n" +
		          "p.add(new JTextField(),          \"wrap 15\");\n" +
		          "\n" +
		          "p.add(createButton(\"New\"),        \"span, split, align left\");\n" +
		          "p.add(createButton(\"Delete\"),     \"\");\n" +
		          "p.add(createButton(\"Edit\"),       \"\");\n" +
		          "p.add(createButton(\"Save\"),       \"\");\n" +
		          "p.add(createButton(\"Cancel\"),     \"wrap push\");\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Layout Showdown (pure)\", p);" +
		          "\n" +
		          "\t\t// Fixed version *******************************************\n" +
		          "JPanel p2 = createTabPanel(new MigLayout(\"\", \"[]15[][grow,fill]15[][grow,fill]\"));    // Makes the background gradient\n" +
		          "\n" +
		          "// References to text fields not stored to reduce code clutter.\n" +
		          "\n" +
		          "JScrollPane list2 = new JScrollPane(new JList(new String[] {\"Mouse, Mickey\"}));\n" +
		          "p2.add(list2,                     \"spany, growy, wmin 150\");\n" +
		          "p2.add(new JLabel(\"Last Name\"));\n" +
		          "p2.add(new JTextField());\n" +
		          "p2.add(new JLabel(\"First Name\"));\n" +
		          "p2.add(new JTextField(),          \"wrap\");\n" +
		          "p2.add(new JLabel(\"Phone\"));\n" +
		          "p2.add(new JTextField());\n" +
		          "p2.add(new JLabel(\"Email\"));\n" +
		          "p2.add(new JTextField(),          \"wrap\");\n" +
		          "p2.add(new JLabel(\"Address 1\"));\n" +
		          "p2.add(new JTextField(),          \"span\");\n" +
		          "p2.add(new JLabel(\"Address 2\"));\n" +
		          "p2.add(new JTextField(),          \"span\");\n" +
		          "p2.add(new JLabel(\"City\"));\n" +
		          "p2.add(new JTextField(),          \"wrap\");\n" +
		          "p2.add(new JLabel(\"State\"));\n" +
		          "p2.add(new JTextField());\n" +
		          "p2.add(new JLabel(\"Postal Code\"));\n" +
		          "p2.add(new JTextField(10),        \"growx 0, wrap\");\n" +
		          "p2.add(new JLabel(\"Country\"));\n" +
		          "p2.add(new JTextField(),          \"wrap 15\");\n" +
		          "\n" +
		          "p2.add(createButton(\"New\"),        \"tag other, span, split\");\n" +
		          "p2.add(createButton(\"Delete\"),     \"tag other\");\n" +
		          "p2.add(createButton(\"Edit\"),       \"tag other\");\n" +
		          "p2.add(createButton(\"Save\"),       \"tag other\");\n" +
		          "p2.add(createButton(\"Cancel\"),     \"tag cancel, wrap push\");\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Layout Showdown (improved)\", p2);");

		return tabbedPane;
	}

	public JComponent createWelcome()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		MigLayout lm = new MigLayout("ins 20, fill", "", "[grow]unrel[]");

		JPanel mainPanel = createTabPanel(lm);

		String s = "MigLayout's main purpose is to make layouts for SWT and Swing, and possibly other frameworks, much more powerful and a lot easier to create, especially for manual coding.\n\n" +
		           "The motto is: \"MigLayout makes complex layouts easy and normal layouts one-liners.\"\n\n" +
		           "The layout engine is very flexible and advanced, something that is needed to make it simple to use yet handle almost all layout use-cases.\n\n" +
		           "MigLayout can handle all layouts that the commonly used Swing Layout Managers can handle and this with a lot of extra features. " +
		           "It also incorporates most, if not all, of the open source alternatives FormLayout's and TableLayout's functionality." +
		           "\n\n\nThanks to Karsten Lentzsch of JGoodies.com for allowing the reuse of the main demo application layout and for his inspiring talks that led to this layout manager." +
		           "\n\n\nMikael Grev\n" +
		           "MiG InfoCom AB\n" +
		           "miglayout@miginfocom.com";

		JTextArea textArea = new JTextArea(s);
		textArea.setEditable(false);
		textArea.setSize(400, 400);

		if (PlatformDefaults.getCurrentPlatform() == PlatformDefaults.WINDOWS_XP)
			textArea.setFont(new Font("tahoma", Font.BOLD, 11));

		textArea.setOpaque(OPAQUE);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);

		JLabel label = new JLabel("You can Right Click any Component or Container to change the constraints for it!");
		label.setForeground(new Color(200, 0, 0));

		mainPanel.add(textArea, "wmin 500, ay top, grow, push, wrap");
		mainPanel.add(label, "growx");
		label.setFont(label.getFont().deriveFont(Font.BOLD));

		tabbedPane.addTab("Welcome", mainPanel);

		setSource("");

		return tabbedPane;
	}

	public JComponent createVisual_Bounds()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// "NON"-corrected bounds
		JPanel p1 = createTabPanel(new MigLayout("fill, ins 3, novisualpadding"));
		p1.setBorder(new LineBorder(Color.BLACK));

		JTabbedPane demoPane2 = new JTabbedPane();
		JPanel demoPanel2 = new JPanel();
		demoPanel2.setBackground(Color.WHITE);
		demoPane2.addTab("Demo Tab", demoPanel2);

		p1.add(createTextArea("A JTextArea", 1, 100), "grow, aligny bottom, wmin 100");
		p1.add(demoPane2, "grow, aligny bottom");
		p1.add(createTextField("A JTextField", 100), "grow, aligny bottom, wmin 100");
		p1.add(createTextArea("A JTextArea", 1, 100), "newline,grow, aligny bottom, wmin 100");
		p1.add(createTextArea("A JTextArea", 1, 100), "grow, aligny bottom, wmin 100");
		p1.add(createTextArea("A JTextArea", 1, 100), "grow, aligny bottom, wmin 100");

		JPanel p2 = createTabPanel(new MigLayout("center,center,fill,ins 3"));
		p2.setBorder(new LineBorder(Color.BLACK));

		JTabbedPane demoPane = new JTabbedPane();
		JPanel demoPanel = new JPanel();
		demoPanel.setBackground(Color.WHITE);
		demoPane.addTab("Demo Tab", demoPanel);

		p2.add(createTextArea("A JTextArea", 1, 100), "grow, aligny bottom, wmin 100");
		p2.add(demoPane, "grow, aligny bottom");
		p2.add(createTextField("A JTextField", 100), "grow, aligny bottom, wmin 100");
		p2.add(createTextArea("A JTextArea", 1, 100), "newline,grow, aligny bottom, wmin 100");
		p2.add(createTextArea("A JTextArea", 1, 100), "grow, aligny bottom, wmin 100");
		p2.add(createTextArea("A JTextArea", 1, 100), "grow, aligny bottom, wmin 100");

		tabbedPane.addTab("Visual Bounds (Not Corrected)", p1);
		tabbedPane.addTab("Visual Bounds (Corrected)", p2);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "// \"NON\"-corrected bounds\n" +
		          "JPanel p1 = createTabPanel(new MigLayout(\"fill, ins 3, novisualpadding\"));\n" +
		          "p1.setBorder(new LineBorder(Color.BLACK));\n" +
		          "\n" +
		          "JTabbedPane demoPane2 = new JTabbedPane();\n" +
		          "JPanel demoPanel2 = new JPanel();\n" +
		          "demoPanel2.setBackground(Color.WHITE);\n" +
		          "demoPane2.addTab(\"Demo Tab\", demoPanel2);\n" +
		          "\n" +
		          "p1.add(createTextArea(\"A JTextArea\", 1, 100), \"grow, aligny bottom, wmin 100\");\n" +
		          "p1.add(demoPane2, \"grow, aligny bottom\");\n" +
		          "p1.add(createTextField(\"A JTextField\", 100), \"grow, aligny bottom, wmin 100\");\n" +
		          "p1.add(createTextArea(\"A JTextArea\", 1, 100), \"newline,grow, aligny bottom, wmin 100\");\n" +
		          "p1.add(createTextArea(\"A JTextArea\", 1, 100), \"grow, aligny bottom, wmin 100\");\n" +
		          "p1.add(createTextArea(\"A JTextArea\", 1, 100), \"grow, aligny bottom, wmin 100\");\n" +
		          "\n" +
		          "JPanel p2 = createTabPanel(new MigLayout(\"center,center,fill,ins 3\"));\n" +
		          "p2.setBorder(new LineBorder(Color.BLACK));\n" +
		          "\n" +
		          "JTabbedPane demoPane = new JTabbedPane();\n" +
		          "JPanel demoPanel = new JPanel();\n" +
		          "demoPanel.setBackground(Color.WHITE);\n" +
		          "demoPane.addTab(\"Demo Tab\", demoPanel);\n" +
		          "\n" +
		          "p2.add(createTextArea(\"A JTextArea\", 1, 100), \"grow, aligny bottom, wmin 100\");\n" +
		          "p2.add(demoPane, \"grow, aligny bottom\");\n" +
		          "p2.add(createTextField(\"A JTextField\", 100), \"grow, aligny bottom, wmin 100\");\n" +
		          "p2.add(createTextArea(\"A JTextArea\", 1, 100), \"newline,grow, aligny bottom, wmin 100\");\n" +
		          "p2.add(createTextArea(\"A JTextArea\", 1, 100), \"grow, aligny bottom, wmin 100\");\n" +
		          "p2.add(createTextArea(\"A JTextArea\", 1, 100), \"grow, aligny bottom, wmin 100\");\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Visual Bounds (Not Corrected)\", p1);\n" +
		          "tabbedPane.addTab(\"Visual Bounds (Corrected)\", p2);");

		return tabbedPane;
	}

	public JComponent createDocking()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel p1 = createTabPanel(new MigLayout("fill"));

		p1.add(createPanel("1. North"), "north");
		p1.add(createPanel("2. West"), "west");
		p1.add(createPanel("3. East"), "east");
		p1.add(createPanel("4. South"), "south");

		String[][] data = new String[20][6];
		for (int r = 0; r < data.length; r++) {
			data[r] = new String[6];
			for (int c = 0; c < data[r].length; c++)
				data[r][c] = "Cell " + (r + 1) + ", " + (c + 1);
		}
		JTable table = new JTable(data, new String[] {"Column 1", "Column 2", "Column 3", "Column 4", "Column 5", "Column 6"});
		p1.add(new JScrollPane(table), "grow");

		JPanel p2 = createTabPanel(new MigLayout("fill", "[c]", ""));

		p2.add(createPanel("1. North"), "north");
		p2.add(createPanel("2. North"), "north");
		p2.add(createPanel("3. West"), "west");
		p2.add(createPanel("4. West"), "west");
		p2.add(createPanel("5. South"), "south");
		p2.add(createPanel("6. East"), "east");
		p2.add(createButton("7. Normal"));
		p2.add(createButton("8. Normal"));
		p2.add(createButton("9. Normal"));

		JPanel p3 = createTabPanel(new MigLayout());

		p3.add(createPanel("1. North"), "north");
		p3.add(createPanel("2. South"), "south");
		p3.add(createPanel("3. West"), "west");
		p3.add(createPanel("4. East"), "east");
		p3.add(createButton("5. Normal"));

		JPanel p4 = createTabPanel(new MigLayout());

		p4.add(createPanel("1. North"), "north");
		p4.add(createPanel("2. North"), "north");
		p4.add(createPanel("3. West"), "west");
		p4.add(createPanel("4. West"), "west");
		p4.add(createPanel("5. South"), "south");
		p4.add(createPanel("6. East"), "east");
		p4.add(createButton("7. Normal"));
		p4.add(createButton("8. Normal"));
		p4.add(createButton("9. Normal"));

		JPanel p5 = createTabPanel(new MigLayout("fillx", "[c]", ""));

		p5.add(createPanel("1. North"), "north");
		p5.add(createPanel("2. North"), "north");
		p5.add(createPanel("3. West"), "west");
		p5.add(createPanel("4. West"), "west");
		p5.add(createPanel("5. South"), "south");
		p5.add(createPanel("6. East"), "east");
		p5.add(createButton("7. Normal"));
		p5.add(createButton("8. Normal"));
		p5.add(createButton("9. Normal"));

		JPanel p6 = createTabPanel(new MigLayout("fill", "", ""));

		Random rand = new Random();
		String[] sides = {"north", "east", "south", "west"};
		for (int i = 0; i < 20; i++) {
			int side = rand.nextInt(4);
			p6.add(createPanel((i + 1) + " " + sides[side]), sides[side]);
		}
		p6.add(createPanel("I'm in the Center!"), "dock center");

		tabbedPane.addTab("Docking 1 (fill)", p1);
		tabbedPane.addTab("Docking 2 (fill)", p2);
		tabbedPane.addTab("Docking 3", p3);
		tabbedPane.addTab("Docking 4", p4);
		tabbedPane.addTab("Docking 5 (fillx)", p5);
		tabbedPane.addTab("Random Docking", new JScrollPane(p6));

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "JPanel p1 = createTabPanel(new MigLayout(\"fill\"));\n" +
		          "\n" +
		          "p1.add(createPanel(\"1. North\"), \"north\");\n" +
		          "p1.add(createPanel(\"2. West\"), \"west\");\n" +
		          "p1.add(createPanel(\"3. East\"), \"east\");\n" +
		          "p1.add(createPanel(\"4. South\"), \"south\");\n" +
		          "\n" +
		          "String[][] data = new String[20][6];\n" +
		          "for (int r = 0; r < data.length; r++) {\n" +
		          "\tdata[r] = new String[6];\n" +
		          "\tfor (int c = 0; c < data[r].length; c++)\n" +
		          "\t\tdata[r][c] = \"Cell \" + (r + 1) + \", \" + (c + 1);\n" +
		          "}\n" +
		          "JTable table = new JTable(data, new String[] {\"Column 1\", \"Column 2\", \"Column 3\", \"Column 4\", \"Column 5\", \"Column 6\"});\n" +
		          "p1.add(new JScrollPane(table), \"grow\");\n" +
		          "\n" +
		          "JPanel p2 = createTabPanel(new MigLayout(\"fill\", \"[c]\", \"\"));\n" +
		          "\n" +
		          "p2.add(createPanel(\"1. North\"), \"north\");\n" +
		          "p2.add(createPanel(\"2. North\"), \"north\");\n" +
		          "p2.add(createPanel(\"3. West\"), \"west\");\n" +
		          "p2.add(createPanel(\"4. West\"), \"west\");\n" +
		          "p2.add(createPanel(\"5. South\"), \"south\");\n" +
		          "p2.add(createPanel(\"6. East\"), \"east\");\n" +
		          "p2.add(createButton(\"7. Normal\"));\n" +
		          "p2.add(createButton(\"8. Normal\"));\n" +
		          "p2.add(createButton(\"9. Normal\"));\n" +
		          "\n" +
		          "JPanel p3 = createTabPanel(new MigLayout());\n" +
		          "\n" +
		          "p3.add(createPanel(\"1. North\"), \"north\");\n" +
		          "p3.add(createPanel(\"2. South\"), \"south\");\n" +
		          "p3.add(createPanel(\"3. West\"), \"west\");\n" +
		          "p3.add(createPanel(\"4. East\"), \"east\");\n" +
		          "p3.add(createButton(\"5. Normal\"));\n" +
		          "\n" +
		          "JPanel p4 = createTabPanel(new MigLayout());\n" +
		          "\n" +
		          "p4.add(createPanel(\"1. North\"), \"north\");\n" +
		          "p4.add(createPanel(\"2. North\"), \"north\");\n" +
		          "p4.add(createPanel(\"3. West\"), \"west\");\n" +
		          "p4.add(createPanel(\"4. West\"), \"west\");\n" +
		          "p4.add(createPanel(\"5. South\"), \"south\");\n" +
		          "p4.add(createPanel(\"6. East\"), \"east\");\n" +
		          "p4.add(createButton(\"7. Normal\"));\n" +
		          "p4.add(createButton(\"8. Normal\"));\n" +
		          "p4.add(createButton(\"9. Normal\"));\n" +
		          "\n" +
		          "JPanel p5 = createTabPanel(new MigLayout(\"fillx\", \"[c]\", \"\"));\n" +
		          "\n" +
		          "p5.add(createPanel(\"1. North\"), \"north\");\n" +
		          "p5.add(createPanel(\"2. North\"), \"north\");\n" +
		          "p5.add(createPanel(\"3. West\"), \"west\");\n" +
		          "p5.add(createPanel(\"4. West\"), \"west\");\n" +
		          "p5.add(createPanel(\"5. South\"), \"south\");\n" +
		          "p5.add(createPanel(\"6. East\"), \"east\");\n" +
		          "p5.add(createButton(\"7. Normal\"));\n" +
		          "p5.add(createButton(\"8. Normal\"));\n" +
		          "p5.add(createButton(\"9. Normal\"));\n" +
		          "\n" +
		          "JPanel p6 = createTabPanel(new MigLayout(\"fill\", \"\", \"\"));\n" +
		          "\n" +
		          "Random rand = new Random();\n" +
		          "String[] sides = {\"north\", \"east\", \"south\", \"west\"};\n" +
		          "for (int i = 0; i < 20; i++) {\n" +
		          "\tint side = rand.nextInt(4);\n" +
		          "\tp6.add(createPanel((i + 1) + \" \" + sides[side]), sides[side]);\n" +
		          "}\n" +
		          "p6.add(createButton(\"I'm in the middle!\"), \"grow\");\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Docking 1 (fill)\", p1);\n" +
		          "tabbedPane.addTab(\"Docking 2 (fill)\", p2);\n" +
		          "tabbedPane.addTab(\"Docking 3\", p3);\n" +
		          "tabbedPane.addTab(\"Docking 4\", p4);\n" +
		          "tabbedPane.addTab(\"Docking 5 (fillx)\", p5);\n" +
		          "tabbedPane.addTab(\"Docking Spiral\", new JScrollPane(p6));");

		return tabbedPane;
	}

	public JComponent createAbsolute_Position()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// Pos tab
		final JPanel posPanel = createTabPanel(new MigLayout());

		posPanel.add(createButton(), "pos 0.5al 0al");
		posPanel.add(createButton(), "pos 1al 0al");
		posPanel.add(createButton(), "pos 0.5al 0.5al");
		posPanel.add(createButton(), "pos 5in 45lp");
		posPanel.add(createButton(), "pos 0.5al 0.5al");
		posPanel.add(createButton(), "pos 0.5al 1.0al");
		posPanel.add(createButton(), "pos 1al .25al");
		posPanel.add(createButton(), "pos visual.x2-pref visual.y2-pref");
		posPanel.add(createButton(), "pos 1al -1in");
		posPanel.add(createButton(), "pos 100 100");
		posPanel.add(createButton(), "pos (10+(20*3lp)) 200");
		posPanel.add(createButton("Drag Window! (pos 500-container.xpos 500-container.ypos)"),
		                            "pos 500-container.xpos 500-container.ypos");

		// Bounds tab
		JPanel boundsPanel = createTabPanel(new MigLayout());

		String constr = "pos (visual.x+visual.w*0.1) visual.y2-40 (visual.x2-visual.w*0.1) visual.y2";
		JLabel southLabel = createLabel(constr, SwingConstants.CENTER);
		southLabel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
		southLabel.setBackground(new Color(200, 200, 255, benchRuns == 0 ? 70 : 255));
		southLabel.setOpaque(true);
		southLabel.setFont(southLabel.getFont().deriveFont(Font.BOLD));
		boundsPanel.add(southLabel, constr);

		boundsPanel.add(createButton(), "pos 0 0 container.x2 n");
		boundsPanel.add(createButton(), "pos visual.x 40 visual.x2 70");
		boundsPanel.add(createButton(), "pos visual.x 100 visual.x2 p");
		boundsPanel.add(createButton(), "pos 0.1al 0.4al n (visual.y2 - 10)");
		boundsPanel.add(createButton(), "pos 0.9al 0.4al n visual.y2-10");
		boundsPanel.add(createButton(), "pos 0.5al 0.5al, pad 3 0 -3 0");
		boundsPanel.add(createButton(), "pos n n 50% 50%");
		boundsPanel.add(createButton(), "pos 50% 50% n n");
		boundsPanel.add(createButton(), "pos 50% n n 50%");
		boundsPanel.add(createButton(), "pos n 50% 50% n");

		tabbedPane.addTab("X Y Positions", posPanel);
		tabbedPane.addTab("X1 Y1 X2 Y2 Bounds", boundsPanel);

		// Glass pane tab
		if (benchRuns == 0) {
			final JPanel glassPanel = createTabPanel(new MigLayout("align c c, ins 0"));
			final JButton butt = createButton("Press me!!");
			glassPanel.add(butt);

			butt.addActionListener(new ActionListener()		{
				public void actionPerformed(ActionEvent e)
				{
					butt.setEnabled(false);
					final JPanel bg = new JPanel(new MigLayout("align c c,fill")) {
						public void paint(Graphics g)
						{
							g.setColor(getBackground());
							g.fillRect(0, 0, getWidth(), getHeight());
							super.paint(g);
						}
					};
					bg.setOpaque(OPAQUE);
					configureActiveComponet(bg);

					final JLabel label = createLabel("You don't need a GlassPane to be cool!");
					label.setFont(label.getFont().deriveFont(30f));
					label.setForeground(new Color(255, 255, 255, 0));
					bg.add(label, "align 50% 30%");

					glassPanel.add(bg, "pos visual.x visual.y visual.x2 visual.y2", 0);
					final long startTime = System.nanoTime();
					final long endTime = startTime + 500000000L;

					glassPanel.revalidate();

					final javax.swing.Timer timer = new Timer(25, null);

					timer.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e)
						{
							long now = System.nanoTime();
							int alpha = (int) (((now - startTime) / (double) (endTime - startTime)) * 300);
							if (alpha < 150)
								bg.setBackground(new Color(100, 100, 100, alpha));

							if (alpha > 150 && alpha < 405) {
								label.setForeground(new Color(255, 255, 255, (alpha - 150)));
								bg.repaint();
							}
							if (alpha > 405)
								timer.stop();
						}
					});
					timer.start();
				}
			});
			tabbedPane.addTab("GlassPane Substitute", glassPanel);
			addComponentListener(new ComponentAdapter() {
				public void componentMoved(ComponentEvent e) {
					if (posPanel.isDisplayable()) {
						posPanel.revalidate();
					} else {
						removeComponentListener(this);
					}
				}
			});
		}

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "// Pos tab\n" +
		          "final JPanel posPanel = createTabPanel(new MigLayout());\n" +
		          "\n" +
		          "posPanel.add(createButton(), \"pos 0.5al 0al\");\n" +
		          "posPanel.add(createButton(), \"pos 1al 0al\");\n" +
		          "posPanel.add(createButton(), \"pos 0.5al 0.5al\");\n" +
		          "posPanel.add(createButton(), \"pos 5in 45lp\");\n" +
		          "posPanel.add(createButton(), \"pos 0.5al 0.5al\");\n" +
		          "posPanel.add(createButton(), \"pos 0.5al 1.0al\");\n" +
		          "posPanel.add(createButton(), \"pos 1al .25al\");\n" +
		          "posPanel.add(createButton(), \"pos visual.x2-pref visual.y2-pref\");\n" +
		          "posPanel.add(createButton(), \"pos 1al -1in\");\n" +
		          "posPanel.add(createButton(), \"pos 100 100\");\n" +
		          "posPanel.add(createButton(), \"pos (10+(20*3lp)) 200\");\n" +
		          "posPanel.add(createButton(\"Drag Window! (pos 500-container.xpos 500-container.ypos)\"),\n" +
		          "                            \"pos 500-container.xpos 500-container.ypos\");\n" +
		          "\n" +
		          "// Bounds tab\n" +
		          "JPanel boundsPanel = createTabPanel(new MigLayout());\n" +
		          "\n" +
		          "String constr = \"pos (visual.x+visual.w*0.1) visual.y2-40 (visual.x2-visual.w*0.1) visual.y2\";\n" +
		          "JLabel southLabel = createLabel(constr, SwingConstants.CENTER);\n" +
		          "southLabel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));\n" +
		          "southLabel.setBackground(new Color(200, 200, 255, 70));\n" +
		          "southLabel.setOpaque(true);\n" +
		          "southLabel.setFont(southLabel.getFont().deriveFont(Font.BOLD));\n" +
		          "boundsPanel.add(southLabel, constr);\n" +
		          "\n" +
		          "boundsPanel.add(createButton(), \"pos 0 0 container.x2 n\");\n" +
		          "boundsPanel.add(createButton(), \"pos visual.x 40 visual.x2 70\");\n" +
		          "boundsPanel.add(createButton(), \"pos visual.x 100 visual.x2 p\");\n" +
		          "boundsPanel.add(createButton(), \"pos 0.1al 0.4al n visual.y2-10\");\n" +
		          "boundsPanel.add(createButton(), \"pos 0.9al 0.4al n visual.y2-10\");\n" +
		          "boundsPanel.add(createButton(), \"pos 0.5al 0.5al, pad 3 0 -3 0\");\n" +
		          "boundsPanel.add(createButton(), \"pos n n 50% 50%\");\n" +
		          "boundsPanel.add(createButton(), \"pos 50% 50% n n\");\n" +
		          "boundsPanel.add(createButton(), \"pos 50% n n 50%\");\n" +
		          "boundsPanel.add(createButton(), \"pos n 50% 50% n\");\n" +
		          "\n" +
		          "// Glass pane tab\n" +
		          "final JPanel glassPanel = createTabPanel(new MigLayout(\"align c c, ins 0\"));\n" +
		          "final JButton butt = createButton(\"Press me!!\");\n" +
		          "glassPanel.add(butt);\n" +
		          "\n" +
		          "butt.addActionListener(new ActionListener()\t\t{\n" +
		          "\tpublic void actionPerformed(ActionEvent e)\n" +
		          "\t{\n" +
		          "\t\tbutt.setEnabled(false);\n" +
		          "\t\tfinal JPanel bg = new JPanel(new MigLayout(\"align c c,fill\")) {\n" +
		          "\t\t\tpublic void paint(Graphics g)\n" +
		          "\t\t\t{\n" +
		          "\t\t\t\tg.setColor(getBackground());\n" +
		          "\t\t\t\tg.fillRect(0, 0, getWidth(), getHeight());\n" +
		          "\t\t\t\tsuper.paint(g);\n" +
		          "\t\t\t}\n" +
		          "\t\t};\n" +
		          "\t\tbg.setOpaque(OPAQUE);\n" +
		          "\t\tconfigureActiveComponet(bg);\n" +
		          "\n" +
		          "\t\tfinal JLabel label = createLabel(\"You don't need a GlassPane to be cool!\");\n" +
		          "\t\tlabel.setFont(label.getFont().deriveFont(30f));\n" +
		          "\t\tlabel.setForeground(new Color(255, 255, 255, 0));\n" +
		          "\t\tbg.add(label, \"align 50% 30%\");\n" +
		          "\n" +
		          "\t\tglassPanel.add(bg, \"pos visual.x visual.y visual.x2 visual.y2\", 0);\n" +
		          "\t\tfinal long startTime = System.nanoTime();\n" +
		          "\t\tfinal long endTime = startTime + 500000000L;\n" +
		          "\n" +
		          "\t\tglassPanel.revalidate();\n" +
		          "\n" +
		          "\t\tfinal javax.swing.Timer timer = new Timer(25, null);\n" +
		          "\n" +
		          "\t\ttimer.addActionListener(new ActionListener() {\n" +
		          "\t\t\tpublic void actionPerformed(ActionEvent e)\n" +
		          "\t\t\t{\n" +
		          "\t\t\t\tlong now = System.nanoTime();\n" +
		          "\t\t\t\tint alpha = (int) (((now - startTime) / (double) (endTime - startTime)) * 300);\n" +
		          "\t\t\t\tif (alpha < 150)\n" +
		          "\t\t\t\t\tbg.setBackground(new Color(100, 100, 100, alpha));\n" +
		          "\n" +
		          "\t\t\t\tif (alpha > 150 && alpha < 405) {\n" +
		          "\t\t\t\t\tlabel.setForeground(new Color(255, 255, 255, (alpha - 150)));\n" +
		          "\t\t\t\t\tbg.repaint();\n" +
		          "\t\t\t\t}\n" +
		          "\t\t\t\tif (alpha > 405)\n" +
		          "\t\t\t\t\ttimer.stop();\n" +
		          "\t\t\t}\n" +
		          "\t\t});\n" +
		          "\t\ttimer.start();\n" +
		          "\t}\n" +
		          "});\n" +
		          "\n" +
		          "tabbedPane.addTab(\"X Y Positions\", posPanel);\n" +
		          "tabbedPane.addTab(\"X1 Y1 X2 Y2 Bounds\", boundsPanel);\n" +
		          "tabbedPane.addTab(\"GlassPane Substitute\", glassPanel);\n" +
		          "\n" +
		          "addComponentListener(new ComponentAdapter() {\n" +
		          "\tpublic void componentMoved(ComponentEvent e) {\n" +
		          "\t\tif (posPanel.isDisplayable()) {\n" +
		          "\t\t\tposPanel.revalidate();\n" +
		          "\t\t} else {\n" +
		          "\t\t\tremoveComponentListener(this);\n" +
		          "\t\t}\n" +
		          "\t}\n" +
		          "});");

		return tabbedPane;
	}

	public JComponent createComponent_Links()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel linksPanel = createTabPanel(new MigLayout());

		// Links tab
		JButton mini = createButton("Mini");
		mini.setMargin(new Insets(0, 1, 0, 1));
		linksPanel.add(mini, "pos null ta.y ta.x2 null");
		linksPanel.add(createTextArea("Components, Please Link to Me!\nMy ID is: 'ta'", 3, 30), "id ta, pos 0.5al 0.5al");
		linksPanel.add(createButton(), "id b1,pos ta.x2 ta.y2");
		linksPanel.add(createButton(), "pos b1.x2+rel b1.y visual.x2 null");
		linksPanel.add(createButton(), "pos ta.x2+rel ta.y visual.x2 null");
		linksPanel.add(createButton(), "pos null ta.y+(ta.h-pref)/2 ta.x-rel null");
		linksPanel.add(createButton(), "pos ta.x ta.y2+100 ta.x2 null");
		linksPanel.add(createCheck("pos (ta.x+indent) (ta.y2+rel)"),
		                           "pos (ta.x+indent) (ta.y2+rel)");

		// External tab
		JPanel externalPanel = createTabPanel(new MigLayout());

		JButton extButt = createButton("Bounds Externally Set!");
		extButt.setBounds(250, 130, 200, 40);

		externalPanel.add(extButt, "id ext, external");
		externalPanel.add(createButton(), "pos ext.x2 ext.y2");
		externalPanel.add(createButton(), "pos null null ext.x ext.y");

		// Start/End Group tab
		JPanel egPanel = createTabPanel(new MigLayout());

		egPanel.add(createButton(), "id b1, endgroupx g1, pos 200 200");
		egPanel.add(createButton(), "id b2, endgroupx g1, pos (b1.x+2ind) (b1.y2+rel)");
		egPanel.add(createButton(), "id b3, endgroupx g1, pos (b1.x+4ind) (b2.y2+rel)");
		egPanel.add(createButton(), "id b4, endgroupx g1, pos (b1.x+6ind) (b3.y2+rel)");

		// Group Bounds tab
		JPanel gpPanel = createTabPanel(new MigLayout());

		gpPanel.add(createButton(), "id grp1.b1, pos n 0.5al 50% n");
		gpPanel.add(createButton(), "id grp1.b2, pos 50% 0.5al n n");
		gpPanel.add(createButton(), "id grp1.b3, pos 0.5al n n b1.y");
		gpPanel.add(createButton(), "id grp1.b4, pos 0.5al b1.y2 n n");

		gpPanel.add(createButton(), "pos n grp1.y2 grp1.x n");
		gpPanel.add(createButton(), "pos n n grp1.x grp1.y");
		gpPanel.add(createButton(), "pos grp1.x2 n n grp1.y");
		gpPanel.add(createButton(), "pos grp1.x2 grp1.y2");

		JPanel boundsPanel = new JPanel(null);
		boundsPanel.setBackground(new Color(200, 200, 255));
		gpPanel.add(boundsPanel, "pos grp1.x grp1.y grp1.x2 grp1.y2");


		tabbedPane.addTab("Component Links", linksPanel);
		tabbedPane.addTab("External Components", externalPanel);
		tabbedPane.addTab("End Grouping", egPanel);
		tabbedPane.addTab("Group Bounds", gpPanel);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "JPanel linksPanel = createTabPanel(new MigLayout());\n" +
		          "\n" +
		          "// Links tab\n" +
		          "JButton mini = createButton(\"Mini\");\n" +
		          "mini.setMargin(new Insets(0, 1, 0, 1));\n" +
		          "linksPanel.add(mini, \"pos null ta.y ta.x2 null\");\n" +
		          "linksPanel.add(createTextArea(\"Components, Please Link to Me!\\nMy ID is: 'ta'\", 3, 30), \"id ta, pos 0.5al 0.5al\");\n" +
		          "linksPanel.add(createButton(), \"id b1,pos ta.x2 ta.y2\");\n" +
		          "linksPanel.add(createButton(), \"pos b1.x2+rel b1.y visual.x2 null\");\n" +
		          "linksPanel.add(createButton(), \"pos ta.x2+rel ta.y visual.x2 null\");\n" +
		          "linksPanel.add(createButton(), \"pos null ta.y+(ta.h-pref)/2 ta.x-rel null\");\n" +
		          "linksPanel.add(createButton(), \"pos ta.x ta.y2+100 ta.x2 null\");\n" +
		          "linksPanel.add(createCheck(\"pos (ta.x+indent) (ta.y2+rel)\"),\n" +
		          "                           \"pos (ta.x+indent) (ta.y2+rel)\");\n" +
		          "\n" +
		          "// External tab\n" +
		          "JPanel externalPanel = createTabPanel(new MigLayout());\n" +
		          "\n" +
		          "JButton extButt = createButton(\"Bounds Externally Set!\");\n" +
		          "extButt.setBounds(250, 130, 200, 40);\n" +
		          "externalPanel.add(extButt, \"id ext, external\");\n" +
		          "externalPanel.add(createButton(), \"pos ext.x2 ext.y2\");\n" +
		          "externalPanel.add(createButton(), \"pos null null ext.x ext.y\");\n" +
		          "\n" +
		          "// Start/End Group tab\n" +
		          "JPanel egPanel = createTabPanel(new MigLayout());\n" +
		          "\n" +
		          "egPanel.add(createButton(), \"id b1, endgroupx g1, pos 200 200\");\n" +
		          "egPanel.add(createButton(), \"id b2, endgroupx g1, pos (b1.x+2ind) (b1.y2+rel)\");\n" +
		          "egPanel.add(createButton(), \"id b3, endgroupx g1, pos (b1.x+4ind) (b2.y2+rel)\");\n" +
		          "egPanel.add(createButton(), \"id b4, endgroupx g1, pos (b1.x+6ind) (b3.y2+rel)\");\n" +
		          "\n" +
		          "// Group Bounds tab\n" +
		          "JPanel gpPanel = createTabPanel(new MigLayout());\n" +
		          "\n" +
		          "gpPanel.add(createButton(), \"id grp1.b1, pos n 0.5al 50% n\");\n" +
		          "gpPanel.add(createButton(), \"id grp1.b2, pos 50% 0.5al n n\");\n" +
		          "gpPanel.add(createButton(), \"id grp1.b3, pos 0.5al n n b1.y\");\n" +
		          "gpPanel.add(createButton(), \"id grp1.b4, pos 0.5al b1.y2 n n\");\n" +
		          "\n" +
		          "gpPanel.add(createButton(), \"pos n grp1.y2 grp1.x n\");\n" +
		          "gpPanel.add(createButton(), \"pos n n grp1.x grp1.y\");\n" +
		          "gpPanel.add(createButton(), \"pos grp1.x2 n n grp1.y\");\n" +
		          "gpPanel.add(createButton(), \"pos grp1.x2 grp1.y2\");\n" +
		          "\n" +
		          "JPanel boundsPanel = new JPanel(null);\n" +
		          "boundsPanel.setBackground(new Color(200, 200, 255));\n" +
		          "gpPanel.add(boundsPanel, \"pos grp1.x grp1.y grp1.x2 grp1.y2\");\n" +
		          "\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Component Links\", linksPanel);\n" +
		          "tabbedPane.addTab(\"External Components\", externalPanel);\n" +
		          "tabbedPane.addTab(\"End Grouping\", egPanel);\n" +
		          "tabbedPane.addTab(\"Group Bounds\", gpPanel);");

		return tabbedPane;
	}

	public JComponent createFlow_Direction()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Layout: flowx, Cell: flowx", createFlowPanel("", "flowx"));
		tabbedPane.addTab("Layout: flowx, Cell: flowy", createFlowPanel("", "flowy"));
		tabbedPane.addTab("Layout: flowy, Cell: flowx", createFlowPanel("flowy", "flowx"));
		tabbedPane.addTab("Layout: flowy, Cell: flowy", createFlowPanel("flowy", "flowy"));

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Layout: flowx, Cell: flowx\", createFlowPanel(\"\", \"flowx\"));\n" +
		          "tabbedPane.addTab(\"Layout: flowx, Cell: flowy\", createFlowPanel(\"\", \"flowy\"));\n" +
		          "tabbedPane.addTab(\"Layout: flowy, Cell: flowx\", createFlowPanel(\"flowy\", \"flowx\"));\n" +
		          "tabbedPane.addTab(\"Layout: flowy, Cell: flowy\", createFlowPanel(\"flowy\", \"flowy\"));" +
				  "\n\tpublic JPanel createFlowPanel(String gridFlow, String cellFlow)\n" +
				  "\t{\n" +
				  "MigLayout lm = new MigLayout(\"center, wrap 3,\" + gridFlow,\n" +
				  "                             \"[110,fill]\",\n" +
				  "                             \"[110,fill]\");\n" +
				  "JPanel panel = createTabPanel(lm);\n" +
				  "\n" +
				  "for (int i = 0; i < 9; i++) {\n" +
				  "\tJButton b = createButton(\"\" + (i + 1));\n" +
				  "\tb.setFont(b.getFont().deriveFont(20f));\n" +
				  "\tpanel.add(b, cellFlow);\n" +
				  "}\n" +
				  "\n" +
				  "JButton b = createButton(\"5:2\");\n" +
				  "b.setFont(b.getFont().deriveFont(20f));\n" +
				  "panel.add(b, cellFlow + \",cell 1 1\");\n" +
				  "\n" +
				  "return panel;\n" +
				  "\t}");

		return tabbedPane;
	}

	public JPanel createFlowPanel(String gridFlow, String cellFlow)
	{
		MigLayout lm = new MigLayout("center, wrap 3," + gridFlow,
		                             "[110,fill]",
		                             "[110,fill]");
		JPanel panel = createTabPanel(lm);

		Font f = panel.getFont().deriveFont(20f);
		for (int i = 0; i < 9; i++) {
			JComponent b = createPanel("" + (i + 1));
			b.setFont(f);
			panel.add(b, cellFlow);
		}

		JComponent b = createPanel("5:2");
		b.setFont(f);
		panel.add(b, cellFlow + ",cell 1 1");

		return panel;
	}

	public JComponent createDebug()
	{
		return createPlainImpl(true);
	}

	public JComponent createButton_Bars()
	{
		MigLayout lm = new MigLayout("ins 0 0 15lp 0",
		                                  "[grow]",
		                                  "[grow][baseline,nogrid]");

		final JPanel mainPanel = new JPanel(lm);
		final JLabel formatLabel = createLabel("");
		formatLabel.setFont(formatLabel.getFont().deriveFont(Font.BOLD));
		JTabbedPane tabbedPane = new JTabbedPane();

		JToggleButton winButt = createToggleButton("Windows");
		JToggleButton macButt = createToggleButton("Mac OS X");
		JButton helpButt = createButton("Help");

		if (benchRuns == 0) {
			winButt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PlatformDefaults.setPlatform(PlatformDefaults.WINDOWS_XP);
					formatLabel.setText("'" + PlatformDefaults.getButtonOrder() + "'");
					((JPanel) ((JFrame) Frame.getFrames()[0]).getContentPane()).revalidate();
				}
			});

			macButt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PlatformDefaults.setPlatform(PlatformDefaults.MAC_OSX);
					formatLabel.setText(PlatformDefaults.getButtonOrder());
					((JPanel) ((JFrame) Frame.getFrames()[0]).getContentPane()).revalidate();
				}
			});

			helpButt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(mainPanel, "See JavaDoc for PlatformDefaults.setButtonOrder(..) for details on the format string.");
				}
			});
		}

		ButtonGroup bg = new ButtonGroup();
		bg.add(winButt);
		bg.add(macButt);
		if (benchRuns == 0) {
			if (PlatformDefaults.getCurrentPlatform() == PlatformDefaults.MAC_OSX) {
				macButt.doClick();
			} else {
				winButt.doClick();
			}
		}

		tabbedPane.addTab("Buttons", createButtonBarsPanel("help", false));
		tabbedPane.addTab("Buttons with Help2", createButtonBarsPanel("help2", false));
		tabbedPane.addTab("Buttons (Same width)", createButtonBarsPanel("help", true));

		mainPanel.add(tabbedPane, "grow,wrap");

		mainPanel.add(createLabel("Button Order:"));
		mainPanel.add(formatLabel, "growx");
		mainPanel.add(winButt);
		mainPanel.add(macButt);
		mainPanel.add(helpButt, "gapbefore unrel");

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("MigLayout lm = new MigLayout(\"ins 0 0 15lp 0\",\n" +
		          "                                  \"[grow]\",\n" +
		          "                                  \"[grow][baseline,nogrid,gap unrelated]\");\n" +
		          "\n" +
		          "final JPanel mainPanel = new JPanel(lm);\n" +
		          "final JLabel formatLabel = createLabel(\"\");\n" +
		          "formatLabel.setFont(formatLabel.getFont().deriveFont(Font.BOLD));\n" +
		          "JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "JToggleButton winButt = createToggleButton(\"Windows\");\n" +
		          "\n" +
		          "winButt.addActionListener(new ActionListener() {\n" +
		          "\tpublic void actionPerformed(ActionEvent e) {\n" +
		          "\t\tPlatformDefaults.setPlatform(PlatformDefaults.WINDOWS_XP);\n" +
		          "\t\tformatLabel.setText(\"'\" + PlatformDefaults.getButtonOrder() + \"'\");\n" +
		          "\t\tSwingUtilities.updateComponentTreeUI(mainPanel);\n" +
		          "\t}\n" +
		          "});\n" +
		          "\n" +
		          "JToggleButton macButt = createToggleButton(\"Mac OS X\");\n" +
		          "macButt.addActionListener(new ActionListener() {\n" +
		          "\tpublic void actionPerformed(ActionEvent e) {\n" +
		          "\t\tPlatformDefaults.setPlatform(PlatformDefaults.MAC_OSX);\n" +
		          "\t\tformatLabel.setText(PlatformDefaults.getButtonOrder());\n" +
		          "\t\tSwingUtilities.updateComponentTreeUI(mainPanel);\n" +
		          "\t}\n" +
		          "});\n" +
		          "\n" +
		          "JButton helpButt = createButton(\"Help\");\n" +
		          "helpButt.addActionListener(new ActionListener() {\n" +
		          "\tpublic void actionPerformed(ActionEvent e) {\n" +
		          "\t\tJOptionPane.showMessageDialog(mainPanel, \"See JavaDoc for PlatformDefaults.setButtonOrder(..) for details on the format string.\");\n" +
		          "\t}\n" +
		          "});\n" +
		          "\n" +
		          "ButtonGroup bg = new ButtonGroup();\n" +
		          "bg.add(winButt);\n" +
		          "bg.add(macButt);\n" +
		          "winButt.doClick();\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Buttons\", createButtonBarsPanel(\"help\", false));\n" +
		          "tabbedPane.addTab(\"Buttons with Help2\", createButtonBarsPanel(\"help2\", false));\n" +
		          "tabbedPane.addTab(\"Buttons (Same width)\", createButtonBarsPanel(\"help\", true));\n" +
		          "\n" +
		          "mainPanel.add(tabbedPane, \"grow,wrap\");\n" +
		          "\n" +
		          "mainPanel.add(createLabel(\"Button Order:\"));\n" +
		          "mainPanel.add(formatLabel, \"growx\");\n" +
		          "mainPanel.add(winButt);\n" +
		          "mainPanel.add(macButt);\n" +
		          "mainPanel.add(helpButt, \"gapbefore unrel\");");

		return mainPanel;
	}

	private JComponent createButtonBarsPanel(String helpTag, boolean sizeLocked)
	{
		MigLayout lm = new MigLayout("nogrid, fillx, aligny 100%, gapy unrel");
		JPanel panel = createTabPanel(lm);

		// Notice that the order in the rows below does not matter...
		String[][] buttons = new String[][] {
				{"OK"},
				{"No", "Yes"},
				{"Help", "Close"},
				{"OK", "Help"},
				{"OK", "Cancel", "Help"},
				{"OK", "Cancel", "Apply", "Help"},
				{"No", "Yes", "Cancel"},
				{"Help", "< Back", "Forward >", "Cancel"},
				{"Print...", "Cancel", "Help"}
		};

		for (int r = 0; r < buttons.length; r++) {
			for (int i = 0; i < buttons[r].length; i++) {
				String txt = buttons[r][i];
				String tag = txt;

				if (txt.equals("Help")) {
					tag = helpTag;
				} else if (txt.equals("< Back")) {
					tag = "back";
				} else if (txt.equals("Close")) {
					tag = "cancel";
				} else if (txt.equals("Forward >")) {
					tag = "next";
				} else if (txt.equals("Print...")) {
					tag = "other";
				}
				String wrap = (i == buttons[r].length - 1) ? ",wrap" : "";
				String sizeGroup = sizeLocked ? ("sgx " + r + ",") : "";
				panel.add(createButton(txt), sizeGroup + "tag " + tag + wrap);
			}
		}
		return panel;
	}

	public JComponent createOrientation()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		MigLayout lm = new MigLayout("flowy", "[grow,fill]", "[]20[]20[]20[]");
		JPanel mainPanel = createTabPanel(lm);

		// Default orientation
		MigLayout defLM = new MigLayout("", "[trailing][grow,fill]", "");

		JPanel defPanel = createTabPanel(defLM);
		addSeparator(defPanel, "Default Orientation");
		defPanel.add(createLabel("Level of Trust"));
		defPanel.add(createTextField(""), "span,growx");
		defPanel.add(createLabel("Radar Presentation"));
		defPanel.add(createTextField(""));
		defPanel.add(createTextField(""));

		// Right-to-left, Top-to-bottom
		MigLayout rtlLM = new MigLayout("rtl,ttb",
		                                "[trailing][grow,fill]",
		                                "");
		JPanel rtlPanel = createTabPanel(rtlLM);
		addSeparator(rtlPanel, "Right to Left");
		rtlPanel.add(createLabel("Level of Trust"));
		rtlPanel.add(createTextField(""), "span,growx");
		rtlPanel.add(createLabel("Radar Presentation"));
		rtlPanel.add(createTextField(""));
		rtlPanel.add(createTextField(""));

		// Right-to-left, Bottom-to-top
		MigLayout rtlbLM = new MigLayout("rtl,btt",
		                                      "[trailing][grow,fill]",
		                                      "");
		JPanel rtlbPanel = createTabPanel(rtlbLM);
		addSeparator(rtlbPanel, "Right to Left, Bottom to Top");
		rtlbPanel.add(createLabel("Level of Trust"));
		rtlbPanel.add(createTextField(""), "span,growx");
		rtlbPanel.add(createLabel("Radar Presentation"));
		rtlbPanel.add(createTextField(""));
		rtlbPanel.add(createTextField(""));

		// Left-to-right, Bottom-to-top
		MigLayout ltrbLM = new MigLayout("ltr,btt",
		                                      "[trailing][grow,fill]",
		                                      "");
		JPanel ltrbPanel = createTabPanel(ltrbLM);
		addSeparator(ltrbPanel, "Left to Right, Bottom to Top");
		ltrbPanel.add(createLabel("Level of Trust"));
		ltrbPanel.add(createTextField(""), "span,growx");
		ltrbPanel.add(createLabel("Radar Presentation"));
		ltrbPanel.add(createTextField(""));
		ltrbPanel.add(createTextField(""));

		mainPanel.add(defPanel);
		mainPanel.add(rtlPanel);
		mainPanel.add(rtlbPanel);
		mainPanel.add(ltrbPanel);

		tabbedPane.addTab("Orientation", mainPanel);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "MigLayout lm = new MigLayout(\"flowy\", \"[grow,fill]\", \"[]0[]15lp[]0[]\");\n" +
		          "JPanel mainPanel = createTabPanel(lm);\n" +
		          "\n" +
		          "// Default orientation\n" +
		          "MigLayout defLM = new MigLayout(\"\", \"[][grow,fill]\", \"\");\n" +
		          "\n" +
		          "JPanel defPanel = createTabPanel(defLM);\n" +
		          "addSeparator(defPanel, \"Default Orientation\");\n" +
		          "defPanel.add(createLabel(\"Level\"));\n" +
		          "defPanel.add(createTextField(\"\"), \"span,growx\");\n" +
		          "defPanel.add(createLabel(\"Radar\"));\n" +
		          "defPanel.add(createTextField(\"\"));\n" +
		          "defPanel.add(createTextField(\"\"));\n" +
		          "\n" +
		          "// Right-to-left, Top-to-bottom\n" +
		          "MigLayout rtlLM = new MigLayout(\"rtl,ttb\",\n" +
		          "                                \"[][grow,fill]\",\n" +
		          "                                \"\");\n" +
		          "JPanel rtlPanel = createTabPanel(rtlLM);\n" +
		          "addSeparator(rtlPanel, \"Right to Left\");\n" +
		          "rtlPanel.add(createLabel(\"Level\"));\n" +
		          "rtlPanel.add(createTextField(\"\"), \"span,growx\");\n" +
		          "rtlPanel.add(createLabel(\"Radar\"));\n" +
		          "rtlPanel.add(createTextField(\"\"));\n" +
		          "rtlPanel.add(createTextField(\"\"));\n" +
		          "\n" +
		          "// Right-to-left, Bottom-to-top\n" +
		          "MigLayout rtlbLM = new MigLayout(\"rtl,btt\",\n" +
		          "                                      \"[][grow,fill]\",\n" +
		          "                                      \"\");\n" +
		          "JPanel rtlbPanel = createTabPanel(rtlbLM);\n" +
		          "addSeparator(rtlbPanel, \"Right to Left, Bottom to Top\");\n" +
		          "rtlbPanel.add(createLabel(\"Level\"));\n" +
		          "rtlbPanel.add(createTextField(\"\"), \"span,growx\");\n" +
		          "rtlbPanel.add(createLabel(\"Radar\"));\n" +
		          "rtlbPanel.add(createTextField(\"\"));\n" +
		          "rtlbPanel.add(createTextField(\"\"));\n" +
		          "\n" +
		          "// Left-to-right, Bottom-to-top\n" +
		          "MigLayout ltrbLM = new MigLayout(\"ltr,btt\",\n" +
		          "                                      \"[][grow,fill]\",\n" +
		          "                                      \"\");\n" +
		          "JPanel ltrbPanel = createTabPanel(ltrbLM);\n" +
		          "addSeparator(ltrbPanel, \"Left to Right, Bottom to Top\");\n" +
		          "ltrbPanel.add(createLabel(\"Level\"));\n" +
		          "ltrbPanel.add(createTextField(\"\"), \"span,growx\");\n" +
		          "ltrbPanel.add(createLabel(\"Radar\"));\n" +
		          "ltrbPanel.add(createTextField(\"\"));\n" +
		          "ltrbPanel.add(createTextField(\"\"));\n" +
		          "\n" +
		          "mainPanel.add(defPanel);\n" +
		          "mainPanel.add(rtlPanel);\n" +
		          "mainPanel.add(rtlbPanel);\n" +
		          "mainPanel.add(ltrbPanel);\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Orientation\", mainPanel);");

		return tabbedPane;
	}

	public JComponent createCell_Position()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// Absolute grid position
		MigLayout absLM = new MigLayout("",
		                                "[100:pref,fill]",
		                                "[100:pref,fill]");
		JPanel absPanel = createTabPanel(absLM);
		absPanel.add(createButton(), "cell 0 0");
		absPanel.add(createButton(), "cell 2 0");
		absPanel.add(createButton(), "cell 3 0");
		absPanel.add(createButton(), "cell 1 1");
		absPanel.add(createButton(), "cell 0 2");
		absPanel.add(createButton(), "cell 2 2");
		absPanel.add(createButton(), "cell 2 2");


		// Relative grid position with wrap
		MigLayout relAwLM = new MigLayout("wrap",
		                                       "[100:pref,fill][100:pref,fill][100:pref,fill][100:pref,fill]",
		                                       "[100:pref,fill]");
		JPanel relAwPanel = createTabPanel(relAwLM);
		relAwPanel.add(createButton());
		relAwPanel.add(createButton(), "skip");
		relAwPanel.add(createButton());
		relAwPanel.add(createButton(), "skip,wrap");
		relAwPanel.add(createButton());
		relAwPanel.add(createButton(), "skip,split");
		relAwPanel.add(createButton());


		// Relative grid position with manual wrap
		MigLayout relWLM = new MigLayout("",
		                                      "[100:pref,fill]",
		                                      "[100:pref,fill]");
		JPanel relWPanel = createTabPanel(relWLM);
		relWPanel.add(createButton());
		relWPanel.add(createButton(), "skip");
		relWPanel.add(createButton(), "wrap");
		relWPanel.add(createButton(), "skip,wrap");
		relWPanel.add(createButton());
		relWPanel.add(createButton(), "skip,split");

		relWPanel.add(createButton());


		// Mixed relative and absolute grid position
		MigLayout mixLM = new MigLayout("",
		                                "[100:pref,fill]",
		                                "[100:pref,fill]");
		JPanel mixPanel = createTabPanel(mixLM);
		mixPanel.add(createButton());
		mixPanel.add(createButton(), "cell 2 0");
		mixPanel.add(createButton());
		mixPanel.add(createButton(), "cell 1 1,wrap");
		mixPanel.add(createButton());
		mixPanel.add(createButton(), "cell 2 2,split");
		mixPanel.add(createButton());

		tabbedPane.addTab("Absolute", absPanel);
		tabbedPane.addTab("Relative + Wrap", relAwPanel);
		tabbedPane.addTab("Relative", relWPanel);
		tabbedPane.addTab("Mixed", mixPanel);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("\t\tJTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "\t\t// Absolute grid position\n" +
		          "\t\tMigLayout absLM = new MigLayout(\"\",\n" +
		          "\t\t                                \"[100:pref,fill]\",\n" +
		          "\t\t                                \"[100:pref,fill]\");\n" +
		          "\t\tJPanel absPanel = createTabPanel(absLM);\n" +
		          "\t\tabsPanel.add(createPanel(), \"cell 0 0\");\n" +
		          "\t\tabsPanel.add(createPanel(), \"cell 2 0\");\n" +
		          "\t\tabsPanel.add(createPanel(), \"cell 3 0\");\n" +
		          "\t\tabsPanel.add(createPanel(), \"cell 1 1\");\n" +
		          "\t\tabsPanel.add(createPanel(), \"cell 0 2\");\n" +
		          "\t\tabsPanel.add(createPanel(), \"cell 2 2\");\n" +
		          "\t\tabsPanel.add(createPanel(), \"cell 2 2\");\n" +
		          "\n" +
		          "\n" +
		          "\t\t// Relative grid position with wrap\n" +
		          "\t\tMigLayout relAwLM = new MigLayout(\"wrap\",\n" +
		          "\t\t                                       \"[100:pref,fill][100:pref,fill][100:pref,fill][100:pref,fill]\",\n" +
		          "\t\t                                       \"[100:pref,fill]\");\n" +
		          "\t\tJPanel relAwPanel = createTabPanel(relAwLM);\n" +
		          "\t\trelAwPanel.add(createPanel());\n" +
		          "\t\trelAwPanel.add(createPanel(), \"skip\");\n" +
		          "\t\trelAwPanel.add(createPanel());\n" +
		          "\t\trelAwPanel.add(createPanel(), \"skip,wrap\");\n" +
		          "\t\trelAwPanel.add(createPanel());\n" +
		          "\t\trelAwPanel.add(createPanel(), \"skip,split\");\n" +
		          "\t\trelAwPanel.add(createPanel());\n" +
		          "\n" +
		          "\n" +
		          "\t\t// Relative grid position with manual wrap\n" +
		          "\t\tMigLayout relWLM = new MigLayout(\"\",\n" +
		          "\t\t                                      \"[100:pref,fill]\",\n" +
		          "\t\t                                      \"[100:pref,fill]\");\n" +
		          "\t\tJPanel relWPanel = createTabPanel(relWLM);\n" +
		          "\t\trelWPanel.add(createPanel());\n" +
		          "\t\trelWPanel.add(createPanel(), \"skip\");\n" +
		          "\t\trelWPanel.add(createPanel(), \"wrap\");\n" +
		          "\t\trelWPanel.add(createPanel(), \"skip,wrap\");\n" +
		          "\t\trelWPanel.add(createPanel());\n" +
		          "\t\trelWPanel.add(createPanel(), \"skip,split\");\n" +
		          "\n" +
		          "\t\trelWPanel.add(createPanel());\n" +
		          "\n" +
		          "\n" +
		          "\t\t// Mixed relative and absolute grid position\n" +
		          "\t\tMigLayout mixLM = new MigLayout(\"\",\n" +
		          "\t\t                                \"[100:pref,fill]\",\n" +
		          "\t\t                                \"[100:pref,fill]\");\n" +
		          "\t\tJPanel mixPanel = createTabPanel(mixLM);\n" +
		          "\t\tmixPanel.add(createPanel());\n" +
		          "\t\tmixPanel.add(createPanel(), \"cell 2 0\");\n" +
		          "\t\tmixPanel.add(createPanel());\n" +
		          "\t\tmixPanel.add(createPanel(), \"cell 1 1,wrap\");\n" +
		          "\t\tmixPanel.add(createPanel());\n" +
		          "\t\tmixPanel.add(createPanel(), \"cell 2 2,split\");\n" +
		          "\t\tmixPanel.add(createPanel());\n" +
		          "\n" +
		          "\t\ttabbedPane.addTab(\"Absolute\", absPanel);\n" +
		          "\t\ttabbedPane.addTab(\"Relative + Wrap\", relAwPanel);\n" +
		          "\t\ttabbedPane.addTab(\"Relative\", relWPanel);\n" +
		          "\t\ttabbedPane.addTab(\"Mixed\", mixPanel);");

		return tabbedPane;
	}

	public JComponent createPlain()
	{
		return createPlainImpl(false);
	}

	private JComponent createPlainImpl(boolean debug)
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		MigLayout lm = new MigLayout((debug && benchRuns == 0 ? "debug, inset 20" : "ins 20"), "[para]0[][100lp, fill][60lp][95lp, fill]", "");
		JPanel panel = createTabPanel(lm);

		addSeparator(panel, "Manufacturer");

		panel.add(createLabel("Company"),      "skip");
		panel.add(createTextField(""),         "span, growx");
		panel.add(createLabel("Contact"),      "skip");
		panel.add(createTextField(""),         "span, growx");
		panel.add(createLabel("Order No"),     "skip");
		panel.add(createTextField(15),         "wrap para");

		addSeparator(panel, "Inspector");

		panel.add(createLabel("Name"),         "skip");
		panel.add(createTextField(""),         "span, growx");
		panel.add(createLabel("Reference No"), "skip");
		panel.add(createTextField(""),         "wrap");
		panel.add(createLabel("Status"),       "skip");
		panel.add(createCombo(new String[] {"In Progress", "Finnished", "Released"}), "wrap para");

		addSeparator(panel, "Ship");

		panel.add(createLabel("Shipyard"),     "skip");
		panel.add(createTextField(""),         "span, growx");
		panel.add(createLabel("Register No"),  "skip");
		panel.add(createTextField(""));
		panel.add(createLabel("Hull No"),      "right");
		panel.add(createTextField(15), "wrap");
		panel.add(createLabel("Project StructureType"), "skip");
		panel.add(createCombo(new String[] {"New Building", "Convention", "Repair"}));

		if (debug)
			panel.add(createLabel("Red is cell bounds. Blue is component bounds."), "newline,ax left,span,gaptop 40,");

		tabbedPane.addTab("Plain", panel);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "MigLayout lm = new MigLayout((debug && benchRuns == 0 ? \"debug, inset 20\" : \"ins 20\"), \"[para]0[][100lp, fill][60lp][95lp, fill]\", \"\");\n" +
		          "JPanel panel = createTabPanel(lm);\n" +
		          "\n" +
		          "addSeparator(panel, \"Manufacturer\");\n" +
		          "\n" +
		          "panel.add(createLabel(\"Company\"),   \"skip\");\n" +
		          "panel.add(createTextField(\"\"),      \"span, growx\");\n" +
		          "panel.add(createLabel(\"Contact\"),   \"skip\");\n" +
		          "panel.add(createTextField(\"\"),      \"span, growx\");\n" +
		          "panel.add(createLabel(\"Order No\"),  \"skip\");\n" +
		          "panel.add(createTextField(15),      \"wrap para\");\n" +
		          "\n" +
		          "addSeparator(panel, \"Inspector\");\n" +
		          "\n" +
		          "panel.add(createLabel(\"Name\"),         \"skip\");\n" +
		          "panel.add(createTextField(\"\"),         \"span, growx\");\n" +
		          "panel.add(createLabel(\"Reference No\"), \"skip\");\n" +
		          "panel.add(createTextField(\"\"),         \"wrap\");\n" +
		          "panel.add(createLabel(\"Status\"),       \"skip\");\n" +
		          "panel.add(createCombo(new String[] {\"In Progress\", \"Finnished\", \"Released\"}), \"wrap para\");\n" +
		          "\n" +
		          "addSeparator(panel, \"Ship\");\n" +
		          "\n" +
		          "panel.add(createLabel(\"Shipyard\"),     \"skip\");\n" +
		          "panel.add(createTextField(\"\"),         \"span, growx\");\n" +
		          "panel.add(createLabel(\"Register No\"),  \"skip\");\n" +
		          "panel.add(createTextField(\"\"));\n" +
		          "panel.add(createLabel(\"Hull No\"),      \"right\");\n" +
		          "panel.add(createTextField(15), \"wrap\");\n" +
		          "panel.add(createLabel(\"Project StructureType\"), \"skip\");\n" +
		          "panel.add(createCombo(new String[] {\"New Building\", \"Convention\", \"Repair\"}));\n" +
		          "\n" +
		          "if (debug)\n" +
		          "\tpanel.add(createLabel(\"Red is cell bounds. Blue is component bounds.\"), \"newline,ax left,span,gaptop 40,\");\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Plain\", panel);");

		return tabbedPane;
	}

	public JComponent createBound_Sizes()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		for (int i = 0; i < 2; i++) {   // Jumping for 0 and Stable for 1
			String colConstr = i == 0 ? "[right][300]" : "[right, 100lp:pref][300]";

			MigLayout LM1 = new MigLayout("wrap", colConstr, "");
			JPanel panel1 = createTabPanel(LM1);
			panel1.add(createLabel("File Number:"));
			panel1.add(createTextField(""), "growx");
			panel1.add(createLabel("RFQ Number:"));
			panel1.add(createTextField(""), "growx");
			panel1.add(createLabel("Entry Date:"));
			panel1.add(createTextField(6));
			panel1.add(createLabel("Sales Person:"));
			panel1.add(createTextField(""), "growx");

			MigLayout LM2 = new MigLayout("wrap", colConstr, "");
			JPanel panel2 = createTabPanel(LM2);
			panel2.add(createLabel("Shipper:"));
			panel2.add(createTextField(6), "split 2");
			panel2.add(createTextField(""), "growx");
			panel2.add(createLabel("Consignee:"));
			panel2.add(createTextField(6), "split 2");
			panel2.add(createTextField(""), "growx");
			panel2.add(createLabel("Departure:"));
			panel2.add(createTextField(6), "split 2");
			panel2.add(createTextField(""), "growx");
			panel2.add(createLabel("Destination:"));
			panel2.add(createTextField(6), "split 2");
			panel2.add(createTextField(""), "growx");

			tabbedPane.addTab(i == 0 ? "Jumping 1" : "Stable 1", panel1);
			tabbedPane.addTab(i == 0 ? "Jumping 2" : "Stable 2", panel2);
		}

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "for (int i = 0; i < 2; i++) {   // Jumping for 0 and Stable for 1\n" +
		          "\tString colConstr = i == 0 ? \"[right][300]\" : \"[right, 100lp:pref][300]\";\n" +
		          "\n" +
		          "\tMigLayout LM1 = new MigLayout(\"wrap\", colConstr, \"\");\n" +
		          "\tJPanel panel1 = createTabPanel(LM1);\n" +
		          "\tpanel1.add(createLabel(\"File Number:\"));\n" +
		          "\tpanel1.add(createTextField(\"\"), \"growx\");\n" +
		          "\tpanel1.add(createLabel(\"RFQ Number:\"));\n" +
		          "\tpanel1.add(createTextField(\"\"), \"growx\");\n" +
		          "\tpanel1.add(createLabel(\"Entry Date:\"));\n" +
		          "\tpanel1.add(createTextField(6));\n" +
		          "\tpanel1.add(createLabel(\"Sales Person:\"));\n" +
		          "\tpanel1.add(createTextField(\"\"), \"growx\");\n" +
		          "\n" +
		          "\tMigLayout LM2 = new MigLayout(\"wrap\", colConstr, \"\");\n" +
		          "\tJPanel panel2 = createTabPanel(LM2);\n" +
		          "\tpanel2.add(createLabel(\"Shipper:\"));\n" +
		          "\tpanel2.add(createTextField(6), \"split 2\");\n" +
		          "\tpanel2.add(createTextField(\"\"), \"growx\");\n" +
		          "\tpanel2.add(createLabel(\"Consignee:\"));\n" +
		          "\tpanel2.add(createTextField(6), \"split 2\");\n" +
		          "\tpanel2.add(createTextField(\"\"), \"growx\");\n" +
		          "\tpanel2.add(createLabel(\"Departure:\"));\n" +
		          "\tpanel2.add(createTextField(6), \"split 2\");\n" +
		          "\tpanel2.add(createTextField(\"\"), \"growx\");\n" +
		          "\tpanel2.add(createLabel(\"Destination:\"));\n" +
		          "\tpanel2.add(createTextField(6), \"split 2\");\n" +
		          "\tpanel2.add(createTextField(\"\"), \"growx\");\n" +
		          "\n" +
		          "\ttabbedPane.addTab(i == 0 ? \"Jumping 1\" : \"Stable 2\", panel1);\n" +
		          "\ttabbedPane.addTab(i == 0 ? \"Jumping 2\" : \"Stable 2\", panel2);\n" +
		          "}");

		return tabbedPane;
	}

	public JComponent createComponent_Sizes()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		MigLayout LM = new MigLayout("wrap", "[right][0:pref,grow]", "");

		JPanel panel = createTabPanel(LM);
		JScrollPane descrText = createTextAreaScroll("Use slider to see how the components grow and shrink depending on the constraints set on them.", 0, 0, false);

		descrText.setOpaque(OPAQUE);
		descrText.setBorder(new EmptyBorder(10, 10, 10, 10));
		((JTextArea) descrText.getViewport().getView()).setOpaque(OPAQUE);
		descrText.getViewport().setOpaque(OPAQUE);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, panel, descrText);
		splitPane.setOpaque(OPAQUE);
		splitPane.setBorder(null);

		panel.add(createLabel("\"\""));
		panel.add(createTextField(""));
		panel.add(createLabel("\"min!\""));
		panel.add(createTextField("3", 3), "width min!");
		panel.add(createLabel("\"pref!\""));
		panel.add(createTextField("3", 3), "width pref!");
		panel.add(createLabel("\"min:pref\""));
		panel.add(createTextField("8", 8), "width min:pref");
		panel.add(createLabel("\"min:100:150\""));
		panel.add(createTextField("8", 8), "width min:100:150");
		panel.add(createLabel("\"min:100:150, growx\""));
		panel.add(createTextField("8", 8), "width min:100:150, growx");
		panel.add(createLabel("\"min:100, growx\""));
		panel.add(createTextField("8", 8), "width min:100, growx");
		panel.add(createLabel("\"40!\""));
		panel.add(createTextField("8", 8), "width 40!");
		panel.add(createLabel("\"40:40:40\""));
		panel.add(createTextField("8", 8), "width 40:40:40");

		tabbedPane.addTab("Component Sizes", splitPane);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "\t\tMigLayout LM = new MigLayout(\"wrap\", \"[right][0:pref,grow]\", \"\");\n" +
		          "\n" +
		          "\t\tJPanel panel = createTabPanel(LM);\n" +
		          "\t\tJScrollPane descrText = createTextAreaScroll(\"Use slider to see how the components grow and shrink depending on the constraints set on them.\", 0, 0, false);\n" +
		          "\n" +
		          "\t\tdescrText.setOpaque(OPAQUE);\n" +
		          "\t\tdescrText.setBorder(new EmptyBorder(10, 10, 10, 10));\n" +
		          "\t\t((JTextArea) descrText.getViewport().getView()).setOpaque(OPAQUE);\n" +
		          "\t\tdescrText.getViewport().setOpaque(OPAQUE);\n" +
		          "\n" +
		          "\t\tJSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, panel, descrText);\n" +
		          "\t\tsplitPane.setOpaque(OPAQUE);\n" +
		          "\t\tsplitPane.setBorder(null);\n" +
		          "\n" +
		          "\t\tpanel.add(createLabel(\"\\\"\\\"\"));\n" +
		          "\t\tpanel.add(createTextField(\"\"));\n" +
		          "\t\tpanel.add(createLabel(\"\\\"min!\\\"\"));\n" +
		          "\t\tpanel.add(createTextField(\"3\", 3), \"width min!\");\n" +
		          "\t\tpanel.add(createLabel(\"\\\"pref!\\\"\"));\n" +
		          "\t\tpanel.add(createTextField(\"3\", 3), \"width pref!\");\n" +
		          "\t\tpanel.add(createLabel(\"\\\"min:pref\\\"\"));\n" +
		          "\t\tpanel.add(createTextField(\"8\", 8), \"width min:pref\");\n" +
		          "\t\tpanel.add(createLabel(\"\\\"min:100:150\\\"\"));\n" +
		          "\t\tpanel.add(createTextField(\"8\", 8), \"width min:100:150\");\n" +
		          "\t\tpanel.add(createLabel(\"\\\"min:100:150, growx\\\"\"));\n" +
		          "\t\tpanel.add(createTextField(\"8\", 8), \"width min:100:150, growx\");\n" +
		          "\t\tpanel.add(createLabel(\"\\\"min:100, growx\\\"\"));\n" +
		          "\t\tpanel.add(createTextField(\"8\", 8), \"width min:100, growx\");\n" +
		          "\t\tpanel.add(createLabel(\"\\\"40!\\\"\"));\n" +
		          "\t\tpanel.add(createTextField(\"8\", 8), \"width 40!\");\n" +
		          "\t\tpanel.add(createLabel(\"\\\"40:40:40\\\"\"));\n" +
		          "\t\tpanel.add(createTextField(\"8\", 8), \"width 40:40:40\");\n" +
		          "\n" +
		          "\t\ttabbedPane.addTab(\"Component Sizes\", splitPane);");

		return tabbedPane;
	}

	public JComponent createCell_Alignments()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// Horizontal
		MigLayout hLM = new MigLayout("wrap",
		                              "[grow,left][grow,center][grow,right][grow,fill,center]",
		                              "[]unrel[][]");
		JPanel hPanel = createTabPanel(hLM);
		String[] sizes = new String[] {"", "growx", "growx 0", "left", "center", "right", "leading", "trailing"};
		hPanel.add(createLabel("[left]"), "c");
		hPanel.add(createLabel("[center]"), "c");
		hPanel.add(createLabel("[right]"), "c");
		hPanel.add(createLabel("[fill,center]"), "c, growx 0");

		for (int r = 0; r < sizes.length; r++) {
			for (int c = 0; c < 4; c++) {
				String text = sizes[r].length() > 0 ? sizes[r] : "default";
				hPanel.add(createButton(text), sizes[r]);
			}
		}

		// Vertical
		MigLayout vLM = new MigLayout("wrap,flowy",
		                                   "[right][]",
		                                   "[grow,top][grow,center][grow,bottom][grow,fill,bottom][grow,fill,baseline]");
		JPanel vPanel = createTabPanel(vLM);
		String[] vSizes = new String[] {"", "growy", "growy 0", "top", "aligny center", "bottom"};
		vPanel.add(createLabel("[top]"), "aligny center");
		vPanel.add(createLabel("[center]"), "aligny center");
		vPanel.add(createLabel("[bottom]"), "aligny center");
		vPanel.add(createLabel("[fill, bottom]"), "aligny center, growy 0");
		vPanel.add(createLabel("[fill, baseline]"), "aligny center");

		for (int c = 0; c < vSizes.length; c++) {
			for (int r = 0; r < 5; r++) {
				String text = vSizes[c].length() > 0 ? vSizes[c] : "default";
				JButton b = createButton(text);
				if (r == 4 && c <= 1)
					b.setFont(new Font("sansserif", Font.PLAIN, 16 + c * 5));
				vPanel.add(b, vSizes[c]);
			}
		}

		tabbedPane.addTab("Horizontal", hPanel);
		tabbedPane.addTab("Vertical", vPanel);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "// Horizontal\n" +
		          "MigLayout hLM = new MigLayout(\"wrap\",\n" +
		          "                              \"[grow,left][grow,center][grow,right][grow,fill,center]\",\n" +
		          "                              \"[]unrel[][]\");\n" +
		          "JPanel hPanel = createTabPanel(hLM);\n" +
		          "String[] sizes = new String[] {\"\", \"growx\", \"growx 0\", \"left\", \"center\", \"right\", \"leading\", \"trailing\"};\n" +
		          "hPanel.add(createLabel(\"[left]\"), \"c\");\n" +
		          "hPanel.add(createLabel(\"[center]\"), \"c\");\n" +
		          "hPanel.add(createLabel(\"[right]\"), \"c\");\n" +
		          "hPanel.add(createLabel(\"[fill,center]\"), \"c, growx 0\");\n" +
		          "\n" +
		          "for (int r = 0; r < sizes.length; r++) {\n" +
		          "\tfor (int c = 0; c < 4; c++) {\n" +
		          "\t\tString text = sizes[r].length() > 0 ? sizes[r] : \"default\";\n" +
		          "\t\thPanel.add(createButton(text), sizes[r]);\n" +
		          "\t}\n" +
		          "}\n" +
		          "\n" +
		          "// Vertical\n" +
		          "MigLayout vLM = new MigLayout(\"wrap,flowy\",\n" +
		          "                                   \"[right][]\",\n" +
		          "                                   \"[grow,top][grow,center][grow,bottom][grow,fill,bottom][grow,fill,baseline]\");\n" +
		          "JPanel vPanel = createTabPanel(vLM);\n" +
		          "String[] vSizes = new String[] {\"\", \"growy\", \"growy 0\", \"top\", \"center\", \"bottom\"};\n" +
		          "vPanel.add(createLabel(\"[top]\"), \"center\");\n" +
		          "vPanel.add(createLabel(\"[center]\"), \"center\");\n" +
		          "vPanel.add(createLabel(\"[bottom]\"), \"center\");\n" +
		          "vPanel.add(createLabel(\"[fill, bottom]\"), \"center, growy 0\");\n" +
		          "vPanel.add(createLabel(\"[fill, baseline]\"), \"center\");\n" +
		          "\n" +
		          "for (int c = 0; c < vSizes.length; c++) {\n" +
		          "\tfor (int r = 0; r < 5; r++) {\n" +
		          "\t\tString text = vSizes[c].length() > 0 ? vSizes[c] : \"default\";\n" +
		          "\t\tJButton b = createButton(text);\n" +
		          "\t\tif (r == 4 && c <= 1)\n" +
		          "\t\t\tb.setFont(new Font(\"sansserif\", Font.PLAIN, 16 + c * 5));\n" +
		          "\t\tvPanel.add(b, vSizes[c]);\n" +
		          "\t}\n" +
		          "}\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Horizontal\", hPanel);\n" +
		          "tabbedPane.addTab(\"Vertical\", vPanel);");

		return tabbedPane;
	}

	public JComponent createUnits()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// Horizontal
		MigLayout hLM = new MigLayout("wrap,nocache",
									  "[right][]",
									  "");
		JPanel hPanel = createTabPanel(hLM);
		String[] sizes = new String[] {"72pt", "25.4mm", "2.54cm", "1in", "72px", "96px", "120px", "25%", "20sp"};
		for (int i = 0; i < sizes.length; i++) {
			hPanel.add(createLabel(sizes[i]));
			hPanel.add(createTextField(""), "width " + sizes[i] + "!");
		}

		// Horizontal lp
		MigLayout hlpLM = new MigLayout("nocache", "[right][][]", "");
		JPanel hlpPanel = createTabPanel(hlpLM);
		hlpPanel.add(createLabel("9 cols"));
		hlpPanel.add(createTextField(9));
		String[] lpSizes = new String[] {"75lp", "75px", "88px", "100px"};
		hlpPanel.add(createLabel("Width of createTextField(9)"), "wrap");
		for (int i = 0; i < lpSizes.length; i++) {
			hlpPanel.add(createLabel(lpSizes[i]));
			hlpPanel.add(createTextField(""), "width " + lpSizes[i] + "!, wrap");
		}

		// Vertical
		MigLayout vLM = new MigLayout("wrap,flowy,nocache",
									  "[c]",
									  "[top][top]");
		JPanel vPanel = createTabPanel(vLM);
		String[] vSizes = new String[] {"72pt", "25.4mm", "2.54cm", "1in", "72px", "96px", "120px", "25%", "20sp"};
		for (int i = 0; i < sizes.length; i++) {
			vPanel.add(createLabel(vSizes[i]));
			vPanel.add(createTextArea("", 0, 5), "width 50!, height " + vSizes[i] + "!");
		}

		// Vertical lp
		MigLayout vlpLM = new MigLayout("wrap,flowy,nocache",
										"[c]",
										"[top][top]40px[top][top]");
		JPanel vlpPanel = createTabPanel(vlpLM);
		vlpPanel.add(createLabel("4 rows"));
		vlpPanel.add(createTextArea("", 4, 5), "width 50!");
		vlpPanel.add(createLabel("field"));
		vlpPanel.add(createTextField(5));

		String[] vlpSizes1 = new String[] {"63lp", "57px", "63px", "68px", "25%"};
		String[] vlpSizes2 = new String[] {"21lp", "21px", "23px", "24px", "10%"};
		for (int i = 0; i < vlpSizes1.length; i++) {
			vlpPanel.add(createLabel(vlpSizes1[i]));
			vlpPanel.add(createTextArea("", 1, 5), "width 50!, height " + vlpSizes1[i] + "!");
			vlpPanel.add(createLabel(vlpSizes2[i]));
			vlpPanel.add(createTextField(5), "height " + vlpSizes2[i] + "!");
		}

		vlpPanel.add(createLabel("button"), "skip 2");
		vlpPanel.add(createButton("..."));

		tabbedPane.addTab("Horizontal", hPanel);
		tabbedPane.addTab("Horizontal LP", hlpPanel);
		tabbedPane.addTab("Vertical", vPanel);
		tabbedPane.addTab("Vertical LP", vlpPanel);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "// Horizontal\n" +
		          "MigLayout hLM = new MigLayout(\"wrap,nocache\",\n" +
		          "\t\t\t\t\t\t\t  \"[right][]\",\n" +
		          "\t\t\t\t\t\t\t  \"\");\n" +
		          "JPanel hPanel = createTabPanel(hLM);\n" +
		          "String[] sizes = new String[] {\"72pt\", \"25.4mm\", \"2.54cm\", \"1in\", \"72px\", \"96px\", \"120px\", \"25%\", \"20sp\"};\n" +
		          "for (int i = 0; i < sizes.length; i++) {\n" +
		          "\thPanel.add(createLabel(sizes[i]));\n" +
		          "\thPanel.add(createTextField(\"\"), \"width \" + sizes[i] + \"!\");\n" +
		          "}\n" +
		          "\n" +
		          "// Horizontal lp\n" +
		          "MigLayout hlpLM = new MigLayout(\"nocache\", \"[right][][]\", \"\");\n" +
		          "JPanel hlpPanel = createTabPanel(hlpLM);\n" +
		          "hlpPanel.add(createLabel(\"9 cols\"));\n" +
		          "hlpPanel.add(createTextField(9));\n" +
		          "String[] lpSizes = new String[] {\"75lp\", \"75px\", \"88px\", \"100px\"};\n" +
		          "hlpPanel.add(createLabel(\"Width of createTextField(9)\"), \"wrap\");\n" +
		          "for (int i = 0; i < lpSizes.length; i++) {\n" +
		          "\thlpPanel.add(createLabel(lpSizes[i]));\n" +
		          "\thlpPanel.add(createTextField(\"\"), \"width \" + lpSizes[i] + \"!, wrap\");\n" +
		          "}\n" +
		          "\n" +
		          "// Vertical\n" +
		          "MigLayout vLM = new MigLayout(\"wrap,flowy,nocache\",\n" +
		          "\t\t\t\t\t\t\t  \"[c]\",\n" +
		          "\t\t\t\t\t\t\t  \"[top][top]\");\n" +
		          "JPanel vPanel = createTabPanel(vLM);\n" +
		          "String[] vSizes = new String[] {\"72pt\", \"25.4mm\", \"2.54cm\", \"1in\", \"72px\", \"96px\", \"120px\", \"25%\", \"20sp\"};\n" +
		          "for (int i = 0; i < sizes.length; i++) {\n" +
		          "\tvPanel.add(createLabel(vSizes[i]));\n" +
		          "\tvPanel.add(createTextArea(\"\", 0, 5), \"width 50!, height \" + vSizes[i] + \"!\");\n" +
		          "}\n" +
		          "\n" +
		          "// Vertical lp\n" +
		          "MigLayout vlpLM = new MigLayout(\"wrap,flowy,nocache\",\n" +
		          "\t\t\t\t\t\t\t\t\"[c]\",\n" +
		          "\t\t\t\t\t\t\t\t\"[top][top]40px[top][top]\");\n" +
		          "JPanel vlpPanel = createTabPanel(vlpLM);\n" +
		          "vlpPanel.add(createLabel(\"4 rows\"));\n" +
		          "vlpPanel.add(createTextArea(\"\", 4, 5), \"width 50!\");\n" +
		          "vlpPanel.add(createLabel(\"field\"));\n" +
		          "vlpPanel.add(createTextField(5));\n" +
		          "\n" +
		          "String[] vlpSizes1 = new String[] {\"63lp\", \"57px\", \"63px\", \"68px\", \"25%\"};\n" +
		          "String[] vlpSizes2 = new String[] {\"21lp\", \"21px\", \"23px\", \"24px\", \"10%\"};\n" +
		          "for (int i = 0; i < vlpSizes1.length; i++) {\n" +
		          "\tvlpPanel.add(createLabel(vlpSizes1[i]));\n" +
		          "\tvlpPanel.add(createTextArea(\"\", 1, 5), \"width 50!, height \" + vlpSizes1[i] + \"!\");\n" +
		          "\tvlpPanel.add(createLabel(vlpSizes2[i]));\n" +
		          "\tvlpPanel.add(createTextField(5), \"height \" + vlpSizes2[i] + \"!\");\n" +
		          "}\n" +
		          "\n" +
		          "vlpPanel.add(createLabel(\"button\"), \"skip 2\");\n" +
		          "vlpPanel.add(createButton(\"...\"));\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Horizontal\", hPanel);\n" +
		          "tabbedPane.addTab(\"Horizontal LP\", hlpPanel);\n" +
		          "tabbedPane.addTab(\"Vertical\", vPanel);\n" +
		          "tabbedPane.addTab(\"Vertical LP\", vlpPanel);");

		return tabbedPane;
	}

	public JComponent createGrouping()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// Ungrouped
		MigLayout ugM = new MigLayout("", "[]push[][][]", "");
		JPanel ugPanel = createTabPanel(ugM);
		ugPanel.add(createButton("Help"));
		ugPanel.add(createButton("< Back"), "");
		ugPanel.add(createButton("Forward >"), "gap push");
		ugPanel.add(createButton("Apply"), "gap unrel");
		ugPanel.add(createButton("Cancel"), "gap unrel");

		// Grouped Components
		MigLayout gM = new MigLayout("nogrid, fillx");
		JPanel gPanel = createTabPanel(gM);
		gPanel.add(createButton("Help"), "sg");
		gPanel.add(createButton("< Back"), "sg,gap push");
		gPanel.add(createButton("Forward >"), "sg");
		gPanel.add(createButton("Apply"), "sg,gap unrel");
		gPanel.add(createButton("Cancel"), "sg,gap unrel");

		// Grouped Columns
		MigLayout gcM = new MigLayout("", "[sg,fill]push[sg,fill][sg,fill]unrel[sg,fill]unrel[sg,fill]", "");
		JPanel gcPanel = createTabPanel(gcM);
		gcPanel.add(createButton("Help"));
		gcPanel.add(createButton("< Back"));
		gcPanel.add(createButton("Forward >"));
		gcPanel.add(createButton("Apply"));
		gcPanel.add(createButton("Cancel"));

		// Ungrouped Rows
		MigLayout ugrM = new MigLayout();     // no "sg" is the only difference to next panel
		JPanel ugrPanel = createTabPanel(ugrM);
		ugrPanel.add(createLabel("File Number:"));
		ugrPanel.add(createTextField(30), "wrap");
		ugrPanel.add(createLabel("BL/MBL number:"));
		ugrPanel.add(createTextField(7), "split 2");
		ugrPanel.add(createTextField(7), "wrap");
		ugrPanel.add(createLabel("Entry Date:"));
		ugrPanel.add(createTextField(7), "wrap");
		ugrPanel.add(createLabel("RFQ Number:"));
		ugrPanel.add(createTextField(30), "wrap");
		ugrPanel.add(createLabel("Goods:"));
		ugrPanel.add(createCheck("Dangerous"), "wrap");
		ugrPanel.add(createLabel("Shipper:"));
		ugrPanel.add(createTextField(30), "wrap");
		ugrPanel.add(createLabel("Customer:"));
		ugrPanel.add(createTextField(""), "split 2,growx");
		ugrPanel.add(createButton("..."), "width 60px:pref,wrap");
		ugrPanel.add(createLabel("Port of Loading:"));
		ugrPanel.add(createTextField(30), "wrap");
		ugrPanel.add(createLabel("Destination:"));
		ugrPanel.add(createTextField(30), "wrap");

		// Grouped Rows
		MigLayout grM = new MigLayout("", "[]", "[sg]");    // "sg" is the only difference to previous panel
		JPanel grPanel = createTabPanel(grM);
		grPanel.add(createLabel("File Number:"));
		grPanel.add(createTextField(30),"wrap");
		grPanel.add(createLabel("BL/MBL number:"));
		grPanel.add(createTextField(7),"split 2");
		grPanel.add(createTextField(7), "wrap");
		grPanel.add(createLabel("Entry Date:"));
		grPanel.add(createTextField(7), "wrap");
		grPanel.add(createLabel("RFQ Number:"));
		grPanel.add(createTextField(30), "wrap");
		grPanel.add(createLabel("Goods:"));
		grPanel.add(createCheck("Dangerous"), "wrap");
		grPanel.add(createLabel("Shipper:"));
		grPanel.add(createTextField(30), "wrap");
		grPanel.add(createLabel("Customer:"));
		grPanel.add(createTextField(""), "split 2,growx");
		grPanel.add(createButton("..."), "width 50px:pref,wrap");
		grPanel.add(createLabel("Port of Loading:"));
		grPanel.add(createTextField(30), "wrap");
		grPanel.add(createLabel("Destination:"));
		grPanel.add(createTextField(30), "wrap");

		tabbedPane.addTab("Ungrouped", ugPanel);
		tabbedPane.addTab("Grouped (Components)", gPanel);
		tabbedPane.addTab("Grouped (Columns)", gcPanel);
		tabbedPane.addTab("Ungrouped Rows", ugrPanel);
		tabbedPane.addTab("Grouped Rows", grPanel);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "// Ungrouped\n" +
		          "MigLayout ugM = new MigLayout(\"\", \"[]push[][][]\", \"\");\n" +
		          "JPanel ugPanel = createTabPanel(ugM);\n" +
		          "ugPanel.add(createButton(\"Help\"));\n" +
		          "ugPanel.add(createButton(\"< Back\"), \"\");\n" +
		          "ugPanel.add(createButton(\"Forward >\"), \"gap push\");\n" +
		          "ugPanel.add(createButton(\"Apply\"), \"gap unrel\");\n" +
		          "ugPanel.add(createButton(\"Cancel\"), \"gap unrel\");\n" +
		          "\n" +
		          "// Grouped Components\n" +
		          "MigLayout gM = new MigLayout(\"nogrid, fillx\");\n" +
		          "JPanel gPanel = createTabPanel(gM);\n" +
		          "gPanel.add(createButton(\"Help\"), \"sg\");\n" +
		          "gPanel.add(createButton(\"< Back\"), \"sg,gap push\");\n" +
		          "gPanel.add(createButton(\"Forward >\"), \"sg\");\n" +
		          "gPanel.add(createButton(\"Apply\"), \"sg,gap unrel\");\n" +
		          "gPanel.add(createButton(\"Cancel\"), \"sg,gap unrel\");\n" +
		          "\n" +
		          "// Grouped Columns\n" +
		          "MigLayout gcM = new MigLayout(\"\", \"[sg,fill]push[sg,fill][sg,fill]unrel[sg,fill]unrel[sg,fill]\", \"\");\n" +
		          "JPanel gcPanel = createTabPanel(gcM);\n" +
		          "gcPanel.add(createButton(\"Help\"));\n" +
		          "gcPanel.add(createButton(\"< Back\"));\n" +
		          "gcPanel.add(createButton(\"Forward >\"));\n" +
		          "gcPanel.add(createButton(\"Apply\"));\n" +
		          "gcPanel.add(createButton(\"Cancel\"));\n" +
		          "\n" +
		          "// Ungrouped Rows\n" +
		          "MigLayout ugrM = new MigLayout();     // no \"sg\" is the only difference to next panel\n" +
		          "JPanel ugrPanel = createTabPanel(ugrM);\n" +
		          "ugrPanel.add(createLabel(\"File Number:\"));\n" +
		          "ugrPanel.add(createTextField(30), \"wrap\");\n" +
		          "ugrPanel.add(createLabel(\"BL/MBL number:\"));\n" +
		          "ugrPanel.add(createTextField(7), \"split 2\");\n" +
		          "ugrPanel.add(createTextField(7), \"wrap\");\n" +
		          "ugrPanel.add(createLabel(\"Entry Date:\"));\n" +
		          "ugrPanel.add(createTextField(7), \"wrap\");\n" +
		          "ugrPanel.add(createLabel(\"RFQ Number:\"));\n" +
		          "ugrPanel.add(createTextField(30), \"wrap\");\n" +
		          "ugrPanel.add(createLabel(\"Goods:\"));\n" +
		          "ugrPanel.add(createCheck(\"Dangerous\"), \"wrap\");\n" +
		          "ugrPanel.add(createLabel(\"Shipper:\"));\n" +
		          "ugrPanel.add(createTextField(30), \"wrap\");\n" +
		          "ugrPanel.add(createLabel(\"Customer:\"));\n" +
		          "ugrPanel.add(createTextField(\"\"), \"split 2,growx\");\n" +
		          "ugrPanel.add(createButton(\"...\"), \"width 60px:pref,wrap\");\n" +
		          "ugrPanel.add(createLabel(\"Port of Loading:\"));\n" +
		          "ugrPanel.add(createTextField(30), \"wrap\");\n" +
		          "ugrPanel.add(createLabel(\"Destination:\"));\n" +
		          "ugrPanel.add(createTextField(30), \"wrap\");\n" +
		          "\n" +
		          "// Grouped Rows\n" +
		          "MigLayout grM = new MigLayout(\"\", \"[]\", \"[sg]\");    // \"sg\" is the only difference to previous panel\n" +
		          "JPanel grPanel = createTabPanel(grM);\n" +
		          "grPanel.add(createLabel(\"File Number:\"));\n" +
		          "grPanel.add(createTextField(30),\"wrap\");\n" +
		          "grPanel.add(createLabel(\"BL/MBL number:\"));\n" +
		          "grPanel.add(createTextField(7),\"split 2\");\n" +
		          "grPanel.add(createTextField(7), \"wrap\");\n" +
		          "grPanel.add(createLabel(\"Entry Date:\"));\n" +
		          "grPanel.add(createTextField(7), \"wrap\");\n" +
		          "grPanel.add(createLabel(\"RFQ Number:\"));\n" +
		          "grPanel.add(createTextField(30), \"wrap\");\n" +
		          "grPanel.add(createLabel(\"Goods:\"));\n" +
		          "grPanel.add(createCheck(\"Dangerous\"), \"wrap\");\n" +
		          "grPanel.add(createLabel(\"Shipper:\"));\n" +
		          "grPanel.add(createTextField(30), \"wrap\");\n" +
		          "grPanel.add(createLabel(\"Customer:\"));\n" +
		          "grPanel.add(createTextField(\"\"), \"split 2,growx\");\n" +
		          "grPanel.add(createButton(\"...\"), \"width 50px:pref,wrap\");\n" +
		          "grPanel.add(createLabel(\"Port of Loading:\"));\n" +
		          "grPanel.add(createTextField(30), \"wrap\");\n" +
		          "grPanel.add(createLabel(\"Destination:\"));\n" +
		          "grPanel.add(createTextField(30), \"wrap\");\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Ungrouped\", ugPanel);\n" +
		          "tabbedPane.addTab(\"Grouped (Components)\", gPanel);\n" +
		          "tabbedPane.addTab(\"Grouped (Columns)\", gcPanel);\n" +
		          "tabbedPane.addTab(\"Ungrouped Rows\", ugrPanel);\n" +
		          "tabbedPane.addTab(\"Grouped Rows\", grPanel);");

		return tabbedPane;
	}

	public JComponent createSpan()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// Horizontal span
		MigLayout colLM = new MigLayout("nocache",
		                                     "[fill][25%!,fill][105lp!,fill][100px!,fill]",
		                                     "[]15[][]");
		JPanel colPanel = createTabPanel(colLM);
		colPanel.add(createTextField("Col1 [ ]"));
		colPanel.add(createTextField("Col2 [25%!]"));
		colPanel.add(createTextField("Col3 [105lp!]"));
		colPanel.add(createTextField("Col4 [100px!]"), "wrap");

		colPanel.add(createLabel("Full Name:"));
		colPanel.add(createTextField("span, growx", 40), "span,growx");

		colPanel.add(createLabel("Phone:"));
		colPanel.add(createTextField(5), "span 3, split 5");
		colPanel.add(createTextField(7));
		colPanel.add(createTextField(7));
		colPanel.add(createTextField(9));
		colPanel.add(createLabel("(span 3, split 4)"), "wrap");

		colPanel.add(createLabel("Zip/City:"));
		colPanel.add(createTextField(5));
		colPanel.add(createTextField("span 2, growx", 5), "span 2,growx");

		// Vertical span
		MigLayout rowLM = new MigLayout("wrap",
		                                     "[225lp]para[225lp]",
		                                     "[]3[]unrel[]3[]unrel[]3[]");
		JPanel rowPanel = createTabPanel(rowLM);
		rowPanel.add(createLabel("Name"));
		rowPanel.add(createLabel("Notes"));
		rowPanel.add(createTextField("growx"), "growx");
		rowPanel.add(createTextArea("spany,grow", 5, 20), "spany,grow");
		rowPanel.add(createLabel("Phone"));
		rowPanel.add(createTextField("growx"), "growx");
		rowPanel.add(createLabel("Fax"));
		rowPanel.add(createTextField("growx"), "growx");

		tabbedPane.addTab("Column Span/Split", colPanel);
		tabbedPane.addTab("Row Span", rowPanel);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("\t\tJTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "\t\t// Horizontal span\n" +
		          "\t\tMigLayout colLM = new MigLayout(\"\",\n" +
		          "\t\t                                     \"[fill][25%!,fill][105lp!,fill][100px!,fill]\",\n" +
		          "\t\t                                     \"[]15[][]\");\n" +
		          "\t\tJPanel colPanel = createTabPanel(colLM);\n" +
		          "\t\tcolPanel.add(createTextField(\"Col1 [ ]\"));\n" +
		          "\t\tcolPanel.add(createTextField(\"Col2 [25%!]\"));\n" +
		          "\t\tcolPanel.add(createTextField(\"Col3 [105lp!]\"));\n" +
		          "\t\tcolPanel.add(createTextField(\"Col4 [100px!]\"), \"wrap\");\n" +
		          "\n" +
		          "\t\tcolPanel.add(createLabel(\"Full Name:\"));\n" +
		          "\t\tcolPanel.add(createTextField(\"span, growx\", 40), \"span,growx\");\n" +
		          "\n" +
		          "\t\tcolPanel.add(createLabel(\"Phone:\"));\n" +
		          "\t\tcolPanel.add(createTextField(5), \"span 3, split 5\");\n" +
		          "\t\tcolPanel.add(createTextField(7));\n" +
		          "\t\tcolPanel.add(createTextField(7));\n" +
		          "\t\tcolPanel.add(createTextField(9));\n" +
		          "\t\tcolPanel.add(createLabel(\"(span 3, split 4)\"), \"wrap\");\n" +
		          "\n" +
		          "\t\tcolPanel.add(createLabel(\"Zip/City:\"));\n" +
		          "\t\tcolPanel.add(createTextField(5));\n" +
		          "\t\tcolPanel.add(createTextField(\"span 2, growx\", 5), \"span 2,growx\");\n" +
		          "\n" +
		          "\t\t// Vertical span\n" +
		          "\t\tMigLayout rowLM = new MigLayout(\"wrap\",\n" +
		          "\t\t                                     \"[225lp]para[225lp]\",\n" +
		          "\t\t                                     \"[]3[]unrel[]3[]unrel[]3[]\");\n" +
		          "\t\tJPanel rowPanel = createTabPanel(rowLM);\n" +
		          "\t\trowPanel.add(createLabel(\"Name\"));\n" +
		          "\t\trowPanel.add(createLabel(\"Notes\"));\n" +
		          "\t\trowPanel.add(createTextField(\"growx\"), \"growx\");\n" +
		          "\t\trowPanel.add(createTextArea(\"spany,grow\", 5, 20), \"spany,grow\");\n" +
		          "\t\trowPanel.add(createLabel(\"Phone\"));\n" +
		          "\t\trowPanel.add(createTextField(\"growx\"), \"growx\");\n" +
		          "\t\trowPanel.add(createLabel(\"Fax\"));\n" +
		          "\t\trowPanel.add(createTextField(\"growx\"), \"growx\");\n" +
		          "\n" +
		          "\t\ttabbedPane.addTab(\"Column Span/Split\", colPanel);\n" +
		          "\t\ttabbedPane.addTab(\"Row Span\", rowPanel);");

		return tabbedPane;
	}

	public JComponent createGrowing()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// All tab
		MigLayout allLM = new MigLayout("",
		                                "[pref!][grow,fill]",
		                                "[]15[]");
		JPanel allTab = createTabPanel(allLM);
		allTab.add(createLabel("Fixed"));
		allTab.add(createLabel("Gets all extra space"), "wrap");
		allTab.add(createTextField(5));
		allTab.add(createTextField(5));

		// Half tab
		MigLayout halfLM = new MigLayout("",
		                                 "[pref!][grow,fill]",
		                                 "[]15[]");
		JPanel halfTab = createTabPanel(halfLM);
		halfTab.add(createLabel("Fixed"));
		halfTab.add(createLabel("Gets half of extra space"));
		halfTab.add(createLabel("Gets half of extra space"), "wrap");
		halfTab.add(createTextField(5));
		halfTab.add(createTextField(5));
		halfTab.add(createTextField(5));

		// Percent 1 tab
		MigLayout p1LM = new MigLayout("",
		                               "[pref!][0:0,grow 25,fill][0:0,grow 75,fill]",
		                               "[]15[]");
		JPanel p1Tab = createTabPanel(p1LM);
		p1Tab.add(createLabel("Fixed"));
		p1Tab.add(createLabel("Gets 25% of extra space"), "");
		p1Tab.add(createLabel("Gets 75% of extra space"), "wrap");
		p1Tab.add(createTextField(5));
		p1Tab.add(createTextField(5));
		p1Tab.add(createTextField(5));

		// Percent 2 tab
		MigLayout p2LM = new MigLayout("",
		                               "[0:0,grow 33,fill][0:0,grow 67,fill]",
		                               "[]15[]");
		JPanel p2Tab = createTabPanel(p2LM);
		p2Tab.add(createLabel("Gets 33% of extra space"), "");
		p2Tab.add(createLabel("Gets 67% of extra space"), "wrap");
		p2Tab.add(createTextField(5));
		p2Tab.add(createTextField(5));

		// Vertical 1 tab
		MigLayout v1LM = new MigLayout("flowy",
		                               "[]15[]",
		                               "[][c,pref!][c,grow 25,fill][c,grow 75,fill]");
		JPanel v1Tab = createTabPanel(v1LM);
		v1Tab.add(createLabel("Fixed"), "skip");
		v1Tab.add(createLabel("Gets 25% of extra space"));
		v1Tab.add(createLabel("Gets 75% of extra space"), "wrap");
		v1Tab.add(createLabel("new JTextArea(4, 30)"));
		v1Tab.add(createTextAreaScroll("", 4, 30, false));
		v1Tab.add(createTextAreaScroll("", 4, 30, false));
		v1Tab.add(createTextAreaScroll("", 4, 30, false));

		// Vertical 2 tab
		MigLayout v2LM = new MigLayout("flowy",
		                               "[]15[]",
		                               "[][c,grow 33,fill][c,grow 67,fill]");
		JPanel v2Tab = createTabPanel(v2LM);
		v2Tab.add(createLabel("Gets 33% of extra space"), "skip");
		v2Tab.add(createLabel("Gets 67% of extra space"), "wrap");
		v2Tab.add(createLabel("new JTextArea(4, 30)"));
		v2Tab.add(createTextAreaScroll("", 4, 30, false));
		v2Tab.add(createTextAreaScroll("", 4, 30, false));

		tabbedPane.addTab("All", allTab);
		tabbedPane.addTab("Half", halfTab);
		tabbedPane.addTab("Percent 1", p1Tab);
		tabbedPane.addTab("Percent 2", p2Tab);
		tabbedPane.addTab("Vertical 1", v1Tab);
		tabbedPane.addTab("Vertical 2", v2Tab);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "// All tab\n" +
		          "MigLayout allLM = new MigLayout(\"\",\n" +
		          "                                \"[pref!][grow,fill]\",\n" +
		          "                                \"[]15[]\");\n" +
		          "JPanel allTab = createTabPanel(allLM);\n" +
		          "allTab.add(createLabel(\"Fixed\"));\n" +
		          "allTab.add(createLabel(\"Gets all extra space\"), \"wrap\");\n" +
		          "allTab.add(createTextField(5));\n" +
		          "allTab.add(createTextField(5));\n" +
		          "\n" +
		          "// Half tab\n" +
		          "MigLayout halfLM = new MigLayout(\"\",\n" +
		          "                                 \"[pref!][grow,fill]\",\n" +
		          "                                 \"[]15[]\");\n" +
		          "JPanel halfTab = createTabPanel(halfLM);\n" +
		          "halfTab.add(createLabel(\"Fixed\"));\n" +
		          "halfTab.add(createLabel(\"Gets half of extra space\"));\n" +
		          "halfTab.add(createLabel(\"Gets half of extra space\"), \"wrap\");\n" +
		          "halfTab.add(createTextField(5));\n" +
		          "halfTab.add(createTextField(5));\n" +
		          "halfTab.add(createTextField(5));\n" +
		          "\n" +
		          "// Percent 1 tab\n" +
		          "MigLayout p1LM = new MigLayout(\"\",\n" +
		          "                               \"[pref!][0:0,grow 25,fill][0:0,grow 75,fill]\",\n" +
		          "                               \"[]15[]\");\n" +
		          "JPanel p1Tab = createTabPanel(p1LM);\n" +
		          "p1Tab.add(createLabel(\"Fixed\"));\n" +
		          "p1Tab.add(createLabel(\"Gets 25% of extra space\"), \"\");\n" +
		          "p1Tab.add(createLabel(\"Gets 75% of extra space\"), \"wrap\");\n" +
		          "p1Tab.add(createTextField(5));\n" +
		          "p1Tab.add(createTextField(5));\n" +
		          "p1Tab.add(createTextField(5));\n" +
		          "\n" +
		          "// Percent 2 tab\n" +
		          "MigLayout p2LM = new MigLayout(\"\",\n" +
		          "                               \"[0:0,grow 33,fill][0:0,grow 67,fill]\",\n" +
		          "                               \"[]15[]\");\n" +
		          "JPanel p2Tab = createTabPanel(p2LM);\n" +
		          "p2Tab.add(createLabel(\"Gets 33% of extra space\"), \"\");\n" +
		          "p2Tab.add(createLabel(\"Gets 67% of extra space\"), \"wrap\");\n" +
		          "p2Tab.add(createTextField(5));\n" +
		          "p2Tab.add(createTextField(5));\n" +
		          "\n" +
		          "// Vertical 1 tab\n" +
		          "MigLayout v1LM = new MigLayout(\"flowy\",\n" +
		          "                               \"[]15[]\",\n" +
		          "                               \"[][c,pref!][c,grow 25,fill][c,grow 75,fill]\");\n" +
		          "JPanel v1Tab = createTabPanel(v1LM);\n" +
		          "v1Tab.add(createLabel(\"Fixed\"), \"skip\");\n" +
		          "v1Tab.add(createLabel(\"Gets 25% of extra space\"));\n" +
		          "v1Tab.add(createLabel(\"Gets 75% of extra space\"), \"wrap\");\n" +
		          "v1Tab.add(createLabel(\"new JTextArea(4, 30)\"));\n" +
		          "v1Tab.add(createTextAreaScroll(\"\", 4, 30, false));\n" +
		          "v1Tab.add(createTextAreaScroll(\"\", 4, 30, false));\n" +
		          "v1Tab.add(createTextAreaScroll(\"\", 4, 30, false));\n" +
		          "\n" +
		          "// Vertical 2 tab\n" +
		          "MigLayout v2LM = new MigLayout(\"flowy\",\n" +
		          "                               \"[]15[]\",\n" +
		          "                               \"[][c,grow 33,fill][c,grow 67,fill]\");\n" +
		          "JPanel v2Tab = createTabPanel(v2LM);\n" +
		          "v2Tab.add(createLabel(\"Gets 33% of extra space\"), \"skip\");\n" +
		          "v2Tab.add(createLabel(\"Gets 67% of extra space\"), \"wrap\");\n" +
		          "v2Tab.add(createLabel(\"new JTextArea(4, 30)\"));\n" +
		          "v2Tab.add(createTextAreaScroll(\"\", 4, 30, false));\n" +
		          "v2Tab.add(createTextAreaScroll(\"\", 4, 30, false));\n" +
		          "\n" +
		          "tabbedPane.addTab(\"All\", allTab);\n" +
		          "tabbedPane.addTab(\"Half\", halfTab);\n" +
		          "tabbedPane.addTab(\"Percent 1\", p1Tab);\n" +
		          "tabbedPane.addTab(\"Percent 2\", p2Tab);\n" +
		          "tabbedPane.addTab(\"Vertical 1\", v1Tab);\n" +
		          "tabbedPane.addTab(\"Vertical 2\", v2Tab);");

		return tabbedPane;
	}

	public JComponent createBasic_Sizes()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// Horizontal tab
		MigLayout horLM = new MigLayout("",
		                                     "[]15[75px]25[min]25[]",
		                                     "[]15");
		JPanel horTab = createTabPanel(horLM);
		horTab.add(createLabel("75px"), "skip");
		horTab.add(createLabel("Min"));
		horTab.add(createLabel("Pref"), "wrap");

		horTab.add(createLabel("new TextField(15)"));
		horTab.add(createTextField(15));
		horTab.add(createTextField(15));
		horTab.add(createTextField(15));

		// Vertical tab 1
		MigLayout verLM = new MigLayout("flowy,wrap",
		                                     "[]15[]",
		                                     "[]15[c,45px]15[c,min]15[c,pref]");
		JPanel verTab = createTabPanel(verLM);
		verTab.add(createLabel("45px"), "skip");
		verTab.add(createLabel("Min"));
		verTab.add(createLabel("Pref"));

		verTab.add(createLabel("new JTextArea(10, 40)"));
		verTab.add(createTextArea("", 10, 40));
		verTab.add(createTextArea("", 10, 40));
		verTab.add(createTextArea("", 10, 40));

		// Componentsized/Baseline 2
		MigLayout verLM2 = new MigLayout("flowy,wrap",
		                                 "[]15[]",
		                                 "[]15[baseline]15[baseline]15[baseline]");
		JPanel verTab2 = createTabPanel(verLM2);
		verTab2.add(createLabel("45px"), "skip");
		verTab2.add(createLabel("Min"));
		verTab2.add(createLabel("Pref"));

		verTab2.add(createLabel("new JTextArea(10, 40)"));
		verTab2.add(createTextArea("", 10, 40), "height 45");
		verTab2.add(createTextArea("", 10, 40), "height min");
		verTab2.add(createTextArea("", 10, 40), "height pref");

		tabbedPane.addTab("Horizontal - Column size set", horTab);
		tabbedPane.addTab("Vertical - Row sized", verTab);
		tabbedPane.addTab("Vertical - Component sized + Baseline", verTab2);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "// Horizontal tab\n" +
		          "MigLayout horLM = new MigLayout(\"\",\n" +
		          "                                     \"[]15[75px]25[min]25[]\",\n" +
		          "                                     \"[]15\");\n" +
		          "JPanel horTab = createTabPanel(horLM);\n" +
		          "horTab.add(createLabel(\"75px\"), \"skip\");\n" +
		          "horTab.add(createLabel(\"Min\"));\n" +
		          "horTab.add(createLabel(\"Pref\"), \"wrap\");\n" +
		          "\n" +
		          "horTab.add(createLabel(\"new TextField(15)\"));\n" +
		          "horTab.add(createTextField(15));\n" +
		          "horTab.add(createTextField(15));\n" +
		          "horTab.add(createTextField(15));\n" +
		          "\n" +
		          "// Vertical tab 1\n" +
		          "MigLayout verLM = new MigLayout(\"flowy,wrap\",\n" +
		          "                                     \"[]15[]\",\n" +
		          "                                     \"[]15[c,45px]15[c,min]15[c,pref]\");\n" +
		          "JPanel verTab = createTabPanel(verLM);\n" +
		          "verTab.add(createLabel(\"45px\"), \"skip\");\n" +
		          "verTab.add(createLabel(\"Min\"));\n" +
		          "verTab.add(createLabel(\"Pref\"));\n" +
		          "\n" +
		          "verTab.add(createLabel(\"new JTextArea(10, 40)\"));\n" +
		          "verTab.add(createTextArea(\"\", 10, 40));\n" +
		          "verTab.add(createTextArea(\"\", 10, 40));\n" +
		          "verTab.add(createTextArea(\"\", 10, 40));\n" +
		          "\n" +
		          "// Componentsized/Baseline 2\n" +
		          "MigLayout verLM2 = new MigLayout(\"flowy,wrap\",\n" +
		          "                                 \"[]15[]\",\n" +
		          "                                 \"[]15[baseline]15[baseline]15[baseline]\");\n" +
		          "JPanel verTab2 = createTabPanel(verLM2);\n" +
		          "verTab2.add(createLabel(\"45px\"), \"skip\");\n" +
		          "verTab2.add(createLabel(\"Min\"));\n" +
		          "verTab2.add(createLabel(\"Pref\"));\n" +
		          "\n" +
		          "verTab2.add(createLabel(\"new JTextArea(10, 40)\"));\n" +
		          "verTab2.add(createTextArea(\"\", 10, 40), \"height 45\");\n" +
		          "verTab2.add(createTextArea(\"\", 10, 40), \"height min\");\n" +
		          "verTab2.add(createTextArea(\"\", 10, 40), \"height pref\");\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Horizontal - Column size set\", horTab);\n" +
		          "tabbedPane.addTab(\"Vertical - Row sized\", verTab);\n" +
		          "tabbedPane.addTab(\"Vertical - Component sized + Baseline\", verTab2);");

		return tabbedPane;
	}

	public JComponent createAlignments()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// Horizontal tab
		MigLayout horLM = new MigLayout("wrap",
		                                     "[label]15[left]15[center]15[right]15[fill]15[]",
		                                     "[]15[]");

		String[] horLabels = new String[] {"[label]", "[left]", "[center]", "[right]", "[fill]", "[] (Default)"};
		JPanel horTab = createTabPanel(horLM);
		String[] horNames = new String[] {"First Name", "Phone Number", "Facsmile", "Email", "Address", "Other"};
		for (int c = 0; c < horLabels.length; c++)
			horTab.add(createLabel(horLabels[c]));

		for (int r = 0; r < horLabels.length; r++) {
			for (int c = 0; c < horNames.length; c++)
				horTab.add(c == 0 ? createLabel(horNames[r] + ":") : createButton(horNames[r]));
		}

		// Vertical tab
		MigLayout verLM = new MigLayout("wrap,flowy",
		                                "[]unrel[]rel[]",
		                                "[top]15[center]15[bottom]15[fill]15[fill,baseline]15[baseline]15[]");

		String[] verLabels = new String[] {"[top]", "[center]", "[bottom]", "[fill]", "[fill,baseline]", "[baseline]", "[] (Default)"};
		JPanel verTab = createTabPanel(verLM);

		String[] verNames = benchRuns == 0 ? new String[] {"<html>One</html>", "<html>One<br>Two</html>"} : new String[] {"One", "One/Two"};
		for (int c = 0; c < verLabels.length; c++)
			verTab.add(createLabel(verLabels[c]));

		for (int r = 0; r < verNames.length; r++) {
			for (int c = 0; c < verLabels.length; c++)
				verTab.add(createButton(verNames[r]));
		}

		for (int c = 0; c < verLabels.length; c++)
			verTab.add(createTextField("JTextFied"));

		for (int c = 0; c < verLabels.length; c++)
			verTab.add(createTextArea("JTextArea", 1, 8));

		for (int c = 0; c < verLabels.length; c++)
			verTab.add(createTextArea("JTextArea\nwith two lines", 1, 10));

		for (int c = 0; c < verLabels.length; c++)
			verTab.add(createTextAreaScroll("Scrolling JTextArea\nwith two lines", 1, 15, true));

		tabbedPane.addTab("Horizontal", horTab);
		tabbedPane.addTab("Vertical", verTab);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "// Horizontal tab\n" +
		          "MigLayout horLM = new MigLayout(\"wrap\",\n" +
		          "                                     \"[left]15[center]15[right]15[fill]15[]\",\n" +
		          "                                     \"rel[]rel\");\n" +
		          "\n" +
		          "String[] horLabels = new String[] {\"[left]\", \"[center]\", \"[right]\", \"[fill]\", \"[] (Default)\"};\n" +
		          "JPanel horTab = createTabPanel(horLM);\n" +
		          "String[] horNames = new String[] {\"First Name\", \"Phone Number\", \"Facsmile\", \"Email\", \"Address\"};\n" +
		          "for (int c = 0; c < horLabels.length; c++)\n" +
		          "\thorTab.add(createLabel(horLabels[c]));\n" +
		          "\n" +
		          "for (int r = 0; r < horLabels.length; r++) {\n" +
		          "\tfor (int c = 0; c < horNames.length; c++)\n" +
		          "\t\thorTab.add(createButton(horNames[r]));\n" +
		          "}\n" +
		          "\n" +
		          "// Vertical tab\n" +
		          "MigLayout verLM = new MigLayout(\"wrap,flowy\",\n" +
		          "                                \"[]unrel[]rel[]\",\n" +
		          "                                \"[top]15[center]15[bottom]15[fill]15[fill,baseline]15[baseline]15[]\");\n" +
		          "\n" +
		          "String[] verLabels = new String[] {\"[top]\", \"[center]\", \"[bottom]\", \"[fill]\", \"[fill,baseline]\", \"[baseline]\", \"[] (Default)\"};\n" +
		          "JPanel verTab = createTabPanel(verLM);\n" +
		          "\n" +
		          "String[] verNames = new String[] {\"<html>One</html>\", \"<html>One<br>Two</html>\"};\n" +
		          "for (int c = 0; c < verLabels.length; c++)\n" +
		          "\tverTab.add(createLabel(verLabels[c]));\n" +
		          "\n" +
		          "for (int r = 0; r < verNames.length; r++) {\n" +
		          "\tfor (int c = 0; c < verLabels.length; c++)\n" +
		          "\t\tverTab.add(createButton(verNames[r]));\n" +
		          "}\n" +
		          "\n" +
		          "for (int c = 0; c < verLabels.length; c++)\n" +
		          "\tverTab.add(createTextField(\"JTextFied\"));\n" +
		          "\n" +
		          "for (int c = 0; c < verLabels.length; c++)\n" +
		          "\tverTab.add(createTextArea(\"JTextArea\", 1, 8));\n" +
		          "\n" +
		          "for (int c = 0; c < verLabels.length; c++)\n" +
		          "\tverTab.add(createTextArea(\"JTextArea\\nwith two lines\", 1, 10));\n" +
		          "\n" +
		          "for (int c = 0; c < verLabels.length; c++)\n" +
		          "\tverTab.add(createTextAreaScroll(\"Scrolling JTextArea\\nwith two lines\", 1, 15, true));\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Horizontal\", horTab);\n" +
		          "tabbedPane.addTab(\"Vertical\", verTab);");

		return tabbedPane;
	}

	public JComponent createQuick_Start()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel p = createTabPanel(new MigLayout("inset 20"));

		addSeparator(p, "General");

		p.add(createLabel("Company"), "gap para");
		p.add(createTextField(""),    "span, growx");
		p.add(createLabel("Contact"), "gap para");
		p.add(createTextField(""),    "span, growx, wrap para");

		addSeparator(p, "Propeller");

		p.add(createLabel("PTI/kW"),  "gap para");
		p.add(createTextField(10));
		p.add(createLabel("Power/kW"),"gap para");
		p.add(createTextField(10),    "wrap");
		p.add(createLabel("R/mm"),    "gap para");
		p.add(createTextField(10));
		p.add(createLabel("D/mm"),    "gap para");
		p.add(createTextField(10));

		tabbedPane.addTab("Quick Start", p);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "JPanel p = createTabPanel(new MigLayout());\n" +
		          "\n" +
		          "addSeparator(p, \"General\");\n" +
		          "\n" +
		          "p.add(createLabel(\"Company\"), \"gap para\");\n" +
		          "p.add(createTextField(\"\"),    \"span, growx, wrap\");\n" +
		          "p.add(createLabel(\"Contact\"), \"gap para\");\n" +
		          "p.add(createTextField(\"\"),    \"span, growx, wrap para\");\n" +
		          "\n" +
		          "addSeparator(p, \"Propeller\");\n" +
		          "\n" +
		          "p.add(createLabel(\"PTI/kW\"),  \"gap para\");\n" +
		          "p.add(createTextField(10));\n" +
		          "p.add(createLabel(\"Power/kW\"),\"gap para\");\n" +
		          "p.add(createTextField(10),    \"wrap\");\n" +
		          "p.add(createLabel(\"R/mm\"),    \"gap para\");\n" +
		          "p.add(createTextField(10));\n" +
		          "p.add(createLabel(\"D/mm\"),    \"gap para\");\n" +
		          "p.add(createTextField(10));\n" +
		          "\n" +
		          "tabbedPane.addTab(\"Quick Start\", p);");

		return tabbedPane;
	}

	public JComponent createGrow_Shrink()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		// shrink tab
		MigLayout slm = new MigLayout("nogrid");
		JPanel sPanel = createTabPanel(slm);

		JScrollPane sDescrText = createTextAreaScroll("Use the slider to see how the components shrink depending on the constraints set on them.\n\n'shp' means Shrink Priority. " +
		                                              "Higher values will be shrunk before lower ones and the default value is 100.\n\n'shrink' means Shrink Weight. " +
		                                              "Lower values relative to other's means they will shrink less when space is scarce. " +
		                                              "Shrink Weight is only relative to components with the same Shrink Priority. Default Shrink Weight is 100.\n\n" +
		                                              "The component's minimum size will always be honored.", 0, 0, true);

		sDescrText.setOpaque(OPAQUE);
		sDescrText.setBorder(new EmptyBorder(10, 10, 10, 10));
		((JTextArea) sDescrText.getViewport().getView()).setOpaque(OPAQUE);
		sDescrText.getViewport().setOpaque(OPAQUE);

		JSplitPane sSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, sPanel, sDescrText);
		sSplitPane.setOpaque(OPAQUE);
		sSplitPane.setBorder(null);

		sPanel.add(createTextField("shp 110", 12), "shp 110");
		sPanel.add(createTextField("Default (100)", 12), "");
		sPanel.add(createTextField("shp 90", 12), "shp 90");

		sPanel.add(createTextField("shrink 25", 20), "newline,shrink 25");
		sPanel.add(createTextField("shrink 75", 20), "shrink 75");

		sPanel.add(createTextField("Default", 20), "newline");
		sPanel.add(createTextField("Default", 20), "");

		sPanel.add(createTextField("shrink 0", 40), "newline,shrink 0");

		sPanel.add(createTextField("shp 110", 12), "newline,shp 110");
		sPanel.add(createTextField("shp 100,shrink 25", 12), "shp 100,shrink 25");
		sPanel.add(createTextField("shp 100,shrink 75", 12), "shp 100,shrink 75");
		tabbedPane.addTab("Shrink", sSplitPane);

		// Grow tab
		MigLayout glm = new MigLayout("nogrid", "[grow]", "");
		JPanel gPanel = createTabPanel(glm);

		JScrollPane gDescrText = createTextAreaScroll("'gp' means Grow Priority. " +
		                                              "Higher values will be grown before lower ones and the default value is 100.\n\n'grow' means Grow Weight. " +
		                                              "Higher values relative to other's means they will grow more when space is up for takes. " +
		                                              "Grow Weight is only relative to components with the same Grow Priority. Default Grow Weight is 0 which means " +
		                                              "components will normally not grow. \n\nNote that the buttons in the first and last row have max width set to 170 to " +
		                                              "emphasize Grow Priority.\n\nThe component's maximum size will always be honored.", 0, 0, true);

		gDescrText.setOpaque(OPAQUE);
		gDescrText.setBorder(new EmptyBorder(10, 10, 10, 10));
		((JTextArea) gDescrText.getViewport().getView()).setOpaque(OPAQUE);
		gDescrText.getViewport().setOpaque(OPAQUE);

		JSplitPane gSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, gPanel, gDescrText);
		gSplitPane.setOpaque(OPAQUE);
		gSplitPane.setBorder(null);

		gPanel.add(createButton("gp 110,grow"), "gp 110,grow,wmax 170");
		gPanel.add(createButton("Default (100),grow"), "grow,wmax 170");
		gPanel.add(createButton("gp 90,grow"), "gp 90,grow,wmax 170");

		gPanel.add(createButton("Default Button"), "newline");

		gPanel.add(createButton("growx"), "newline,growx,wrap");

		gPanel.add(createButton("gp 110,grow"), "gp 110,grow,wmax 170");
		gPanel.add(createButton("gp 100,grow 25"), "gp 100,grow 25,wmax 170");
		gPanel.add(createButton("gp 100,grow 75"), "gp 100,grow 75,wmax 170");
		tabbedPane.addTab("Grow", gSplitPane);

		// Disregard. Just forgetting the source code text close to the source code.
		setSource("JTabbedPane tabbedPane = new JTabbedPane();\n" +
		          "\n" +
		          "// shrink tab\n" +
		          "MigLayout slm = new MigLayout(\"nogrid\");\n" +
		          "JPanel sPanel = createTabPanel(slm);\n" +
		          "\n" +
		          "JScrollPane sDescrText = createTextAreaScroll(\"Use the slider to see how the components shrink depending on the constraints set on them.\\n\\n'shp' means Shrink Priority. \" +\n" +
		          "                                              \"Higher values will be shrunk before lower ones and the default value is 100.\\n\\n'shrink' means Shrink Weight. \" +\n" +
		          "                                              \"Lower values relative to other's means they will shrink less when space is scarce. \" +\n" +
		          "                                              \"Shrink Weight is only relative to components with the same Shrink Priority. Default Shrink Weight is 100.\\n\\n\" +\n" +
		          "                                              \"The component's minimum size will always be honored.\", 0, 0, true);\n" +
		          "\n" +
		          "sDescrText.setOpaque(OPAQUE);\n" +
		          "sDescrText.setBorder(new EmptyBorder(10, 10, 10, 10));\n" +
		          "((JTextArea) sDescrText.getViewport().getView()).setOpaque(OPAQUE);\n" +
		          "sDescrText.getViewport().setOpaque(OPAQUE);\n" +
		          "\n" +
		          "JSplitPane sSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, sPanel, sDescrText);\n" +
		          "sSplitPane.setOpaque(OPAQUE);\n" +
		          "sSplitPane.setBorder(null);\n" +
		          "\n" +
		          "sPanel.add(createTextField(\"shp 110\", 12), \"shp 110\");\n" +
		          "sPanel.add(createTextField(\"Default (100)\", 12), \"\");\n" +
		          "sPanel.add(createTextField(\"shp 90\", 12), \"shp 90\");\n" +
		          "\n" +
		          "sPanel.add(createTextField(\"shrink 25\", 20), \"newline,shrink 25\");\n" +
		          "sPanel.add(createTextField(\"shrink 75\", 20), \"shrink 75\");\n" +
		          "\n" +
		          "sPanel.add(createTextField(\"Default\", 20), \"newline\");\n" +
		          "sPanel.add(createTextField(\"Default\", 20), \"\");\n" +
		          "\n" +
		          "sPanel.add(createTextField(\"shrink 0\", 40), \"newline,shrink 0\");\n" +
		          "\n" +
		          "sPanel.add(createTextField(\"shp 110\", 12), \"newline,shp 110\");\n" +
		          "sPanel.add(createTextField(\"shp 100,shrink 25\", 12), \"shp 100,shrink 25\");\n" +
		          "sPanel.add(createTextField(\"shp 100,shrink 75\", 12), \"shp 100,shrink 75\");\n" +
		          "tabbedPane.addTab(\"Shrink\", sSplitPane);\n" +
		          "\n" +
		          "// Grow tab\n" +
		          "MigLayout glm = new MigLayout(\"nogrid\", \"[grow]\", \"\");\n" +
		          "JPanel gPanel = createTabPanel(glm);\n" +
		          "\n" +
		          "JScrollPane gDescrText = createTextAreaScroll(\"'gp' means Grow Priority. \" +\n" +
		          "                                              \"Higher values will be grown before lower ones and the default value is 100.\\n\\n'grow' means Grow Weight. \" +\n" +
		          "                                              \"Higher values relative to other's means they will grow more when space is up for takes. \" +\n" +
		          "                                              \"Grow Weight is only relative to components with the same Grow Priority. Default Grow Weight is 0 which means \" +\n" +
		          "                                              \"components will normally not grow. \\n\\nNote that the buttons in the first and last row have max width set to 170 to \" +\n" +
		          "                                              \"emphasize Grow Priority.\\n\\nThe component's maximum size will always be honored.\", 0, 0, true);\n" +
		          "\n" +
		          "gDescrText.setOpaque(OPAQUE);\n" +
		          "gDescrText.setBorder(new EmptyBorder(10, 10, 10, 10));\n" +
		          "((JTextArea) gDescrText.getViewport().getView()).setOpaque(OPAQUE);\n" +
		          "gDescrText.getViewport().setOpaque(OPAQUE);\n" +
		          "\n" +
		          "JSplitPane gSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, gPanel, gDescrText);\n" +
		          "gSplitPane.setOpaque(OPAQUE);\n" +
		          "gSplitPane.setBorder(null);\n" +
		          "\n" +
		          "gPanel.add(createButton(\"gp 110,grow\"), \"gp 110,grow,wmax 170\");\n" +
		          "gPanel.add(createButton(\"Default (100),grow\"), \"grow,wmax 170\");\n" +
		          "gPanel.add(createButton(\"gp 90,grow\"), \"gp 90,grow,wmax 170\");\n" +
		          "\n" +
		          "gPanel.add(createButton(\"Default Button\"), \"newline\");\n" +
		          "\n" +
		          "gPanel.add(createButton(\"growx\"), \"newline,growx,wrap\");\n" +
		          "\n" +
		          "gPanel.add(createButton(\"gp 110,grow\"), \"gp 110,grow,wmax 170\");\n" +
		          "gPanel.add(createButton(\"gp 100,grow 25\"), \"gp 100,grow 25,wmax 170\");\n" +
		          "gPanel.add(createButton(\"gp 100,grow 75\"), \"gp 100,grow 75,wmax 170\");\n" +
		          "tabbedPane.addTab(\"Grow\", gSplitPane);");

		return tabbedPane;
	}

	public JComponent createPlainApi()
	{
		JTabbedPane tabbedPane = new JTabbedPane();

		MigLayout lm = new MigLayout(new LC(), null, null);
		JPanel panel = createTabPanel(lm);

		addSeparator(panel, "Manufacturer");
		panel.add(createLabel("Company"));
		panel.add(createTextField(""), "span,growx");
		panel.add(createLabel("Contact"));
		panel.add(createTextField(""), "span,growx");
		panel.add(createLabel("Order No"));
		panel.add(createTextField(15), "wrap");

		addSeparator(panel, "Inspector");
		panel.add(createLabel("Name"));
		panel.add(createTextField(""), "span,growx");
		panel.add(createLabel("Reference No"));
		panel.add(createTextField(""), "wrap");
		panel.add(createLabel("Status"));
		panel.add(createCombo(new String[] {"In Progress", "Finnished", "Released"}), "wrap");

		addSeparator(panel, "Ship");
		panel.add(createLabel("Shipyard"));
		panel.add(createTextField(""), "span,growx");
		panel.add(createLabel("Register No"));
		panel.add(createTextField(""));
		panel.add(createLabel("Hull No"), "right");
		panel.add(createTextField(15), "wrap");
		panel.add(createLabel("Project StructureType"));
		panel.add(createCombo(new String[] {"New Building", "Convention", "Repair"}));

		tabbedPane.addTab("Plain", panel);

		return tabbedPane;
	}

	// **********************************************************
	// * Helper Methods
	// **********************************************************

	private final ToolTipListener toolTipListener = new ToolTipListener();
	private final ConstraintListener constraintListener = new ConstraintListener();

	private JLabel createLabel(String text)
	{
		return createLabel(text, SwingConstants.LEADING);
	}

	private JLabel createLabel(String text, int align)
	{
		final JLabel b = new JLabel(text, align);
		configureActiveComponet(b);
		return b;
	}

	public JComboBox createCombo(String[] items)
	{
		JComboBox combo = new JComboBox(items);

		if (PlatformDefaults.getCurrentPlatform() == PlatformDefaults.MAC_OSX)
			combo.setOpaque(false);

		return combo;
	}

	private JTextField createTextField(int cols)
	{
		return createTextField("", cols);
	}

	private JTextField createTextField(String text)
	{
		return createTextField(text, 0);
	}

	private JTextField createTextField(String text, int cols)
	{
		final JTextField b = new JTextField(text, cols);

		configureActiveComponet(b);

		return b;
	}

	private static final Font BUTT_FONT = new Font("monospaced", Font.PLAIN, 12);
	private JButton createButton()
	{
		return createButton("");
	}

	private JButton createButton(String text)
	{
		return createButton(text, false);
	}

	private JButton createButton(String text, boolean bold)
	{
		JButton b = new JButton(text) {
			public void addNotify()
			{
				super.addNotify();
				if (benchRuns == 0) {   // Since this does not exist in the SWT version
					if (getText().length() == 0) {
						String lText = (String) ((MigLayout) getParent().getLayout()).getComponentConstraints(this);
						setText(lText != null && lText.length() > 0 ? lText : "<Empty>");
					}
				} else {
					setText("Benchmark Version");
				}
			}
		};

		if (bold)
			b.setFont(b.getFont().deriveFont(Font.BOLD));

		configureActiveComponet(b);

		b.setOpaque(buttonOpaque); // Or window's buttons will have strange border
		b.setContentAreaFilled(contentAreaFilled);

		return b;
	}

	private JToggleButton createToggleButton(String text)
	{
		JToggleButton b = new JToggleButton(text);
//		configureActiveComponet(b);
		b.setOpaque(buttonOpaque); // Or window's buttons will have strange border
		return b;
	}

	private JCheckBox createCheck(String text)
	{
		JCheckBox b = new JCheckBox(text);

		configureActiveComponet(b);

		b.setOpaque(OPAQUE); // Or window's checkboxes will have strange border
		return b;
	}

	private JPanel createTabPanel(LayoutManager lm)
	{
		JPanel panel = new JPanel(lm);
		configureActiveComponet(panel);
		panel.setOpaque(OPAQUE);
		return panel;
	}

	private JComponent createPanel()
	{
		return createPanel("");
	}

	private JComponent createPanel(String s)
	{
		JLabel panel = new JLabel(s, SwingConstants.CENTER) {
			public void addNotify()
			{
				super.addNotify();
				if (benchRuns == 0) {   // Since this does not exist in the SWT version
					if (getText().length() == 0) {
						String lText = (String) ((MigLayout) getParent().getLayout()).getComponentConstraints(this);
						setText(lText != null && lText.length() > 0 ? lText : "<Empty>");
					}
				}
			}
		};
		panel.setBorder(new EtchedBorder());
		panel.setOpaque(true);
		configureActiveComponet(panel);

		return panel;
	}

	private JTextArea createTextArea(String text, int rows, int cols)
	{
		JTextArea ta = new JTextArea(text, rows, cols);
		ta.setBorder(UIManager.getBorder("TextField.border"));
		ta.setFont(UIManager.getFont("TextField.font"));
		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);

		configureActiveComponet(ta);

		return ta;
	}

	private JScrollPane createTextAreaScroll(String text, int rows, int cols, boolean hasVerScroll)
	{
		JTextArea ta = new JTextArea(text, rows, cols);
		ta.setFont(UIManager.getFont("TextField.font"));
		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);

		JScrollPane scroll = new JScrollPane(
			    ta,
			    hasVerScroll ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
			    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		return scroll;
	}

	private JComponent configureActiveComponet(JComponent c)
	{
		if (benchRuns == 0) {
			c.addMouseMotionListener(toolTipListener);
			c.addMouseListener(constraintListener);
		}
		return c;
	}

	static final Color LABEL_COLOR = new Color(0, 70, 213);
	private void addSeparator(JPanel panel, String text)
	{
		JLabel l = createLabel(text);
		l.setForeground(LABEL_COLOR);

		panel.add(l, "gapbottom 1, span, split 2, aligny center");
		panel.add(configureActiveComponet(new JSeparator()), "gapleft rel, growx");
	}

	private class ConstraintListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
				react(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
				react(e);
		}

		public void react(MouseEvent e)
		{
			JComponent c = (JComponent) e.getSource();
			LayoutManager lm = c.getParent().getLayout();
			if (lm instanceof MigLayout == false)
				lm = c.getLayout();

			if (lm instanceof MigLayout) {
				MigLayout layout = (MigLayout) lm;
				boolean isComp = layout.isManagingComponent(c);

				Object compConstr = isComp ? layout.getComponentConstraints(c) : null;
				if (isComp && compConstr == null)
					compConstr = "";

				Object rowsConstr = isComp ? null : layout.getRowConstraints();
				Object colsConstr = isComp ? null : layout.getColumnConstraints();
				Object layoutConstr = isComp ? null : layout.getLayoutConstraints();

				ConstraintsDialog cDlg = new ConstraintsDialog(SwingDemo.this,
					   layoutConstr instanceof LC ? IDEUtil.getConstraintString((LC) layoutConstr, false) : (String) layoutConstr,
					   rowsConstr instanceof AC ? IDEUtil.getConstraintString((AC) rowsConstr, false, false) : (String) rowsConstr,
					   colsConstr instanceof AC ? IDEUtil.getConstraintString((AC) colsConstr, false, false) : (String) colsConstr,
					   compConstr instanceof CC ? IDEUtil.getConstraintString((CC) compConstr, false) : (String) compConstr);

				cDlg.pack();
				cDlg.setLocationRelativeTo(c);

				if (cDlg.showDialog()) {
					try {
						if (isComp) {
							String constrStr = cDlg.componentConstrTF.getText().trim();
							layout.setComponentConstraints(c, constrStr);
							if (c instanceof JButton) {
								c.setFont(BUTT_FONT);
								((JButton) c).setText(constrStr.length() == 0 ? "<Empty>" : constrStr);
							}
						} else {
							layout.setLayoutConstraints(cDlg.layoutConstrTF.getText());
							layout.setRowConstraints(cDlg.rowsConstrTF.getText());
							layout.setColumnConstraints(cDlg.colsConstrTF.getText());
						}
					} catch(Exception ex) {
						StringWriter sw = new StringWriter();
						ex.printStackTrace(new PrintWriter(sw));
						JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(c), sw.toString(), "Error parsing Constraint!", JOptionPane.ERROR_MESSAGE);
						return;
					}

					c.invalidate();
					c.getParent().validate();
				}
			}
		}
	}

	private static class ToolTipListener extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent e)
		{
			JComponent c = (JComponent) e.getSource();
			LayoutManager lm = c.getParent().getLayout();
			if (lm instanceof MigLayout) {
				Object constr = ((MigLayout) lm).getComponentConstraints(c);
				if (constr instanceof String)
					c.setToolTipText((constr != null ? ("\"" + constr + "\"") : "null"));
			}
		}
	}

	private static class ConstraintsDialog extends JDialog implements ActionListener, KeyEventDispatcher
	{
		private static final Color ERROR_COLOR = new Color(255, 180, 180);
		private final JPanel mainPanel = new JPanel(new MigLayout("fillx,flowy,ins dialog",
		                                                          "[fill]",
		                                                          "2[]2"));
		final JTextField layoutConstrTF;
		final JTextField rowsConstrTF;
		final JTextField colsConstrTF;
		final JTextField componentConstrTF;

		private final JButton okButt = new JButton("OK");
		private final JButton cancelButt = new JButton("Cancel");

		private boolean okPressed = false;

		public ConstraintsDialog(Frame owner, String layoutConstr, String rowsConstr, String colsConstr, String compConstr)
		{
			super(owner, (compConstr != null ? "Edit Component Constraints" : "Edit Container Constraints"), true);

			layoutConstrTF = createConstraintField(layoutConstr);
			rowsConstrTF = createConstraintField(rowsConstr);
			colsConstrTF = createConstraintField(colsConstr);
			componentConstrTF = createConstraintField(compConstr);

			if (componentConstrTF != null) {
				mainPanel.add(new JLabel("Component Constraints"));
				mainPanel.add(componentConstrTF);
			}

			if (layoutConstrTF != null) {
				mainPanel.add(new JLabel("Layout Constraints"));
				mainPanel.add(layoutConstrTF);
			}

			if (colsConstrTF != null) {
				mainPanel.add(new JLabel("Column Constraints"), "gaptop unrel");
				mainPanel.add(colsConstrTF);
			}

			if (rowsConstrTF != null) {
				mainPanel.add(new JLabel("Row Constraints"), "gaptop unrel");
				mainPanel.add(rowsConstrTF);
			}

			mainPanel.add(okButt, "tag ok,split,flowx,gaptop 15");
			mainPanel.add(cancelButt, "tag cancel,gaptop 15");

			setContentPane(mainPanel);

			okButt.addActionListener(this);
			cancelButt.addActionListener(this);
		}

		public void addNotify()
		{
			super.addNotify();
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
		}

		public void removeNotify()
		{
			KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
			super.removeNotify();
		}

		public boolean dispatchKeyEvent(KeyEvent e)
		{
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				dispose();
			return false;
		}

		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == okButt)
				okPressed = true;
			dispose();
		}

		private JTextField createConstraintField(String text)
		{
			if (text == null)
				return null;

			final JTextField tf = new JTextField(text, 50);
			tf.setFont(new Font("monospaced", Font.PLAIN, 12));

			tf.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e)
				{
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						okButt.doClick();
						return;
					}

					javax.swing.Timer timer = new Timer(50, new ActionListener() {
						public void actionPerformed(ActionEvent e)
						{
							String constr = tf.getText();
							try {
								if (tf == layoutConstrTF) {
									ConstraintParser.parseLayoutConstraint(constr);
								} else if (tf == rowsConstrTF) {
									ConstraintParser.parseRowConstraints(constr);
								} else if (tf == colsConstrTF) {
									ConstraintParser.parseColumnConstraints(constr);
								} else if (tf == componentConstrTF) {
									ConstraintParser.parseComponentConstraint(constr);
								}

								tf.setBackground(Color.WHITE);
								okButt.setEnabled(true);
							} catch(Exception ex) {
								tf.setBackground(ERROR_COLOR);
								okButt.setEnabled(false);
							}
						}
					});
					timer.setRepeats(false);
					timer.start();
				}
			});

			return tf;
		}

		private boolean showDialog()
		{
			setVisible(true);
			return okPressed;
		}
	}

}