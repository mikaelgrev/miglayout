package net.miginfocom.swing.layout;

import java.awt.Dimension;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import net.miginfocom.swing.layout.snapshot.Sizing;
import net.miginfocom.swing.layout.snapshot.SwingLayoutHarness;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static net.miginfocom.swing.layout.snapshot.SwingLayoutHarness.named;

/**
 * How extra or missing space is handed out to columns/rows via grow, shrink and push. The cells have a fixed
 * preferred size of 60x24 but are resizable, so only grow/shrink/push changes the outcome.
 */
class GrowShrinkSnapshotTest
{
	record Case(String name, String layout, String cols, String rows, String[] cc, Sizing sizing)
	{
		@Override
		public String toString()
		{
			return name + " (" + sizing + ")";
		}
	}

	static Stream<Case> cases()
	{
		return Stream.of(
				new Case("growx fills", "insets 0, gap 0", "[60!][grow]", "[24!]", new String[] {"", "growx", ""}, Sizing.fixed(200, 24)),
				new Case("grow weight 0 vs 100", "insets 0, gap 0", "[60!][grow 0][grow 100]", "[24!]", new String[] {"", "growx", "growx"}, Sizing.fixed(300, 24)),
				new Case("growy", "insets 0, gap 0, wrap 1", "[60!]", "[24!][grow]", new String[] {"", "growy", "growy"}, Sizing.fixed(60, 100)),
				new Case("push center", "insets 0, gap 0", "push[60!][60!]push", "[24!]", new String[] {"", "", ""}, Sizing.fixed(200, 24)),
				new Case("push before", "insets 0, gap 0", "push[60!][60!]", "[24!]", new String[] {"", "", ""}, Sizing.fixed(200, 24)),
				new Case("shrinkx", "insets 0, gap 0", "[60!][shrink]", "[24!]", new String[] {"", "shrinkx", ""}, Sizing.fixed(100, 24)),
				new Case("size min:pref:max", "insets 0, gap 0", "[50:100:150]", "[24!]", new String[] {"growx", "", ""}, Sizing.fixed(120, 24)));
	}

	static JComponent grid(Case c)
	{
		JPanel panel = new JPanel(new MigLayout(c.layout(), c.cols(), c.rows()));
		panel.add(named(cell(), "a"), c.cc()[0]);
		panel.add(named(cell(), "b"), c.cc()[1]);
		panel.add(named(cell(), "c"), c.cc()[2]);
		return panel;
	}

	static JLabel cell()
	{
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(60, 24));
		label.setMinimumSize(label.getPreferredSize());
		return label; // max stays huge, so grow/shrink/fill can change it
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> SwingLayoutHarness.assertLayout(getClass(), c.name(), () -> grid(c), c.sizing())));
	}
}