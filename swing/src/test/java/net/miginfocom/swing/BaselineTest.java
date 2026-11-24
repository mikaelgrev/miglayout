package net.miginfocom.swing;

import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.LayoutCallback;
import org.junit.jupiter.api.Test;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Baseline alignment, and which baselines a grid remembers for the sizes it has already asked about. */
public class BaselineTest
{
	@Test
	public void componentsInABaselineRowShareOneBaseline()
	{
		JPanel panel = new JPanel(new MigLayout("", "[][][][]", "[baseline]"));
		panel.add(sized(probe(new CountingLabelUI()), 11, 9, 55, 61));
		panel.add(sized(new JLabel("label"), 12, 7, 50, 33));
		panel.add(sized(probe(new CountingLabelUI()), 50, 10, 50, 40));
		panel.add(sized(probe(new CountingLabelUI()), 23, 31, 64, 31));

		layOut(panel, 400, 200);

		int expected = baselineInPanel(panel.getComponent(0));
		for (Component c : panel.getComponents())
			assertEquals(expected, baselineInPanel(c), "The baseline of " + c);
	}

	@Test
	public void aComponentIsAskedForItsBaselineOncePerSizeInALayoutPass()
	{
		CountingLabelUI ui = new CountingLabelUI();
		JPanel panel = new JPanel(new MigLayout("wrap 2", "[][grow,fill]", "[baseline][baseline]"));
		panel.add(new JLabel("first"));
		panel.add(sized(probe(ui), 20, 8, 60, 44));
		panel.add(new JLabel("second"));
		panel.add(sized(probe(new CountingLabelUI()), 30, 30, 30, 30));

		layOut(panel, 300, 200);

		assertFalse(ui.questions.isEmpty());
		assertEquals(new HashSet<String>(ui.questions).size(), ui.questions.size(), "Questions: " + ui.questions);
	}

	/** A change that only repaints, like a new vertical alignment, does not invalidate the layout, so a grid that is
	 * laid out again must ask again rather than remember the answer from its previous layout. */
	@Test
	public void aGridAsksForTheBaselineAgainInEveryLayout()
	{
		JLabel label = sized(new JLabel("label"), 50, 20, 50, 20);
		CountingLabelUI ui = new CountingLabelUI();
		JLabel probe = sized(probe(ui), 60, 40, 60, 40);
		JPanel panel = new JPanel(null);
		panel.add(label);
		panel.add(probe);

		Map<ComponentWrapper, CC> constraints = new LinkedHashMap<ComponentWrapper, CC>();
		constraints.put(new SwingComponentWrapper(label), new CC());
		constraints.put(new SwingComponentWrapper(probe), new CC());
		Grid grid = new Grid(new SwingContainerWrapper(panel), new LC(), ConstraintParser.parseRowConstraints("[baseline]"),
				ConstraintParser.parseColumnConstraints("[][]"), constraints, null);
		int[] bounds = {0, 0, 300, 200};

		grid.layout(bounds, null, null, false);
		assertEquals(baselineInPanel(label), baselineInPanel(probe));

		ui.lift = 5;
		grid.layout(bounds, null, null, false);
		assertEquals(baselineInPanel(label), baselineInPanel(probe));
	}

	/** Between two calls for sizes, too, a component can change without the layout being told. */
	@Test
	public void aGridAsksForTheBaselineAgainWhenItIsAskedForItsSizeAgain()
	{
		CountingLabelUI ui = new CountingLabelUI();
		JPanel panel = new JPanel(null);
		JLabel probe = sized(probe(ui), 60, 40, 60, 40);
		panel.add(new JLabel("label"));
		panel.add(probe);

		Map<ComponentWrapper, CC> constraints = new LinkedHashMap<ComponentWrapper, CC>();
		constraints.put(new SwingComponentWrapper(panel.getComponent(0)), new CC());
		constraints.put(new SwingComponentWrapper(probe), new CC());
		Grid grid = new Grid(new SwingContainerWrapper(panel), new LC(), ConstraintParser.parseRowConstraints("[baseline]"),
				ConstraintParser.parseColumnConstraints("[][]"), constraints, null);

		grid.getHeight(300);
		int asked = ui.questions.size();
		assertTrue(asked > 0);
		grid.getHeight(400);
		assertTrue(ui.questions.size() > asked, "Questions: " + ui.questions);
	}

	/** A layout callback can change a component in the middle of a layout. */
	@Test
	public void aComponentChangedByALayoutCallbackIsAskedAgain()
	{
		for (int n = 1; n <= 40; n++) {
			JPanel panel = changedByCallback(n), asked = changedByCallback(n);
			neverRemember(asked);
			panel.setSize(300, 60);
			asked.setSize(300, 60);
			assertEquals(asked.getPreferredSize(), panel.getPreferredSize(), "Preferred size, callback changing at " + n);
			panel.doLayout();
			asked.doLayout();
			for (int i = 0; i < panel.getComponentCount(); i++)
				assertEquals(asked.getComponent(i).getBounds(), panel.getComponent(i).getBounds(), "Callback changing at " + n);
		}
	}

	/** The UI positions HTML text using the text view's layout from the previous question, also when a combo box
	 * renders it, so these are asked every time, and nothing is placed differently than if nothing was remembered. */
	@Test
	public void componentsWhoseBaselineDependsOnEarlierQuestionsAreLaidOutAsBefore()
	{
		for (int width : new int[] {0, 150, 260, 600})
			assertSameLayoutAsWithoutRemembering(mixedForm(), mixedForm(), width, 0);
	}

	@Test
	public void plainTextIsRememberedAndHtmlTextIsNot()
	{
		assertTrue(cacheable(new JLabel("Name:")));
		assertTrue(cacheable(new JButton("OK")));
		assertTrue(cacheable(new JCheckBox("Remember me")));
		assertTrue(cacheable(new JTextField("Ada", 10)));
		assertFalse(cacheable(new JTextField("\u05e9\u05dc\u05d5\u05dd Ada", 10)));
		assertFalse(cacheable(new JLabel("<html>Name <b>here</b></html>")));
		assertFalse(cacheable(new JButton("<html>Run <i>now</i></html>")));
	}

	@Test
	public void aComponentWithItsOwnGetBaselineIsAskedEveryTime()
	{
		assertFalse(cacheable(new JLabel("own") {
			@Override
			public int getBaseline(int width, int height)
			{
				return height / 2;
			}
		}));
	}

	@Test
	public void aComponentWithAUIOfItsOwnIsAskedEveryTime()
	{
		JLabel label = new JLabel("own UI");
		label.setUI(new CountingLabelUI());
		assertFalse(cacheable(label));
	}

	@Test
	public void theClientPropertyDecides()
	{
		JLabel html = new JLabel("<html>Name <b>here</b></html>");
		html.putClientProperty(SwingComponentWrapper.BASELINE_CACHEABLE_PROPERTY, Boolean.TRUE);
		assertTrue(cacheable(html));

		JLabel plain = new JLabel("Name:");
		plain.putClientProperty(SwingComponentWrapper.BASELINE_CACHEABLE_PROPERTY, Boolean.FALSE);
		assertFalse(cacheable(plain));
	}

	@Test
	public void aComboBoxIsRememberedWhenItShowsPlainTextWithTheRendererOfTheLookAndFeel()
	{
		assertTrue(cacheable(new JComboBox<String>(new String[] {"Austria", "Belgium"})));
		assertTrue(cacheable(new JComboBox<String>()));
		assertFalse(cacheable(new JComboBox<String>(new String[] {"<html><b>Austria</b></html>", "Belgium"})));

		JComboBox<String> prototype = new JComboBox<String>(new String[] {"Austria"});
		prototype.setPrototypeDisplayValue("<html>A long <b>country</b></html>");
		assertFalse(cacheable(prototype));

		JComboBox<String> ownRenderer = new JComboBox<String>(new String[] {"Austria"});
		ownRenderer.setRenderer(new DefaultListCellRenderer());
		assertFalse(cacheable(ownRenderer));

		JComboBox<String> editable = new JComboBox<String>(new String[] {"<html><b>Austria</b></html>"});
		editable.setEditable(true);
		assertTrue(cacheable(editable));
	}

	@Test
	public void aSpinnerIsRememberedWithAStandardEditor()
	{
		assertTrue(cacheable(new JSpinner(new SpinnerNumberModel(1, 0, 9, 1))));

		JSpinner own = new JSpinner();
		own.setEditor(new JSpinner.DefaultEditor(own) {
			@Override
			public int getBaseline(int width, int height)
			{
				return 3;
			}
		});
		assertFalse(cacheable(own));
	}

	@Test
	public void containersAreAskedEveryTime()
	{
		assertFalse(cacheable(new JScrollPane(new JTextArea("text"))));
		assertFalse(cacheable(new JPanel()));
	}

	/** Lays out both panels in a window, the second with every baseline asked every time, as an application would:
	 * packed, then at the given size if any. Compares every component's bounds and the panels' sizes. */
	private static void assertSameLayoutAsWithoutRemembering(JPanel panel, JPanel asked, int width, int height)
	{
		neverRemember(asked);
		List<String> remembered = layOutInWindow(panel, width, height);
		List<String> askedEveryTime = layOutInWindow(asked, width, height);
		assertEquals(askedEveryTime, remembered);
	}

	private static List<String> layOutInWindow(final JPanel panel, final int width, final int height)
	{
		final List<String> result = new ArrayList<String>();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run()
				{
					JFrame frame = new JFrame();
					frame.setUndecorated(true);
					frame.add(panel);
					frame.pack();
					if (width > 0)
						frame.setSize(width, height > 0 ? height : frame.getHeight());
					frame.validate();
					result.add("minimum " + panel.getMinimumSize() + ", preferred " + panel.getPreferredSize());
					for (Component c : panel.getComponents())
						result.add(c.getClass().getSimpleName() + " " + c.getBounds());
					frame.dispose();
				}
			});
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return result;
	}

	private static void neverRemember(Component c)
	{
		if (c instanceof JComponent)
			((JComponent) c).putClientProperty(SwingComponentWrapper.BASELINE_CACHEABLE_PROPERTY, Boolean.FALSE);
		if (c instanceof java.awt.Container)
			for (Component child : ((java.awt.Container) c).getComponents())
				neverRemember(child);
	}

	private static JPanel mixedForm()
	{
		JPanel panel = new JPanel(new MigLayout("wrap 2", "[right][grow,fill]", ""));
		panel.add(new JLabel("Updates:"));
		panel.add(new JCheckBox("<html>Install updates <b>automatically</b></html>"));
		panel.add(new JLabel("Range:"));
		panel.add(new JRadioButton("<html>The last <b>7</b> days</html>"));
		panel.add(new JLabel("Action:"));
		panel.add(new JButton("<html>Run <i>now</i></html>"));
		panel.add(new JLabel("Language:"));
		panel.add(new JComboBox<String>(new String[] {"<html><b>English</b> (United Kingdom)</html>", "Deutsch"}), "wmin 60");
		panel.add(new JLabel("Country:"));
		panel.add(new JComboBox<String>(new String[] {"Austria", "Belgium"}));
		panel.add(new JLabel("Name:"));
		panel.add(new JTextField("Ada Lovelace", 12));
		panel.add(new JLabel("Copies:"));
		panel.add(new JSpinner(new SpinnerNumberModel(2, 1, 99, 1)));
		return panel;
	}

	/** A label next to a field, which a layout callback aligns to the top on its n-th question for the label's size. */
	private static JPanel changedByCallback(final int n)
	{
		MigLayout layout = new MigLayout();
		JPanel panel = new JPanel(layout);
		final JLabel label = new JLabel("Name");
		panel.add(label, "h 40!");
		panel.add(new JTextField("Ada", 10));
		layout.addLayoutCallback(new LayoutCallback() {
			private int calls = 0;

			@Override
			public BoundSize[] getSize(ComponentWrapper comp)
			{
				if (comp.getComponent() == label && ++calls == n)
					label.setVerticalAlignment(SwingConstants.TOP);
				return null;
			}
		});
		return panel;
	}

	private static boolean cacheable(Component c)
	{
		return new SwingComponentWrapper(c).isBaselineCacheable();
	}

	private static void layOut(JPanel panel, int width, int height)
	{
		panel.setSize(width, height);
		panel.doLayout();
	}

	private static int baselineInPanel(Component c)
	{
		return c.getY() + c.getBaseline(c.getWidth(), c.getHeight());
	}

	private static <C extends JComponent> C sized(C c, int minWidth, int minHeight, int prefWidth, int prefHeight)
	{
		c.setMinimumSize(new Dimension(minWidth, minHeight));
		c.setPreferredSize(new Dimension(prefWidth, prefHeight));
		return c;
	}

	/** A label whose baseline depends on both the width and the height it is asked about. */
	private static JLabel probe(CountingLabelUI ui)
	{
		JLabel label = new JLabel("probe");
		label.setUI(ui);
		label.putClientProperty(SwingComponentWrapper.BASELINE_CACHEABLE_PROPERTY, Boolean.TRUE);
		return label;
	}

	private static class CountingLabelUI extends BasicLabelUI
	{
		final List<String> questions = new ArrayList<String>();
		int lift = 0;

		@Override
		public int getBaseline(JComponent c, int width, int height)
		{
			questions.add(width + "x" + height);
			return (height * 2) / 3 + (width % 5) + lift;
		}

		@Override
		public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent c)
		{
			return Component.BaselineResizeBehavior.OTHER;
		}
	}
}
