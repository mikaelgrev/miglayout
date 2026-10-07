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
 * wrap/newline, span, split and explicit cell placement.
 */
class SpanSplitCellSnapshotTest
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
				new Case("wrap after each", "insets 0, gap 0", "[60!][60!]", "[24!]", new String[] {"wrap", "wrap", "wrap", ""}, Sizing.preferred()),
				new Case("newline", "insets 0, gap 0", "[60!][60!]", "[24!]", new String[] {"newline", "", "", ""}, Sizing.preferred()),
				new Case("spanx 2", "insets 0, gap 0", "[60!][60!]", "[24!]", new String[] {"spanx 2, wrap", "wrap", "wrap", ""}, Sizing.preferred()),
				new Case("spany 2", "insets 0, gap 0", "[60!][60!]", "[24!]", new String[] {"spany 2, growy", "wrap", "wrap", ""}, Sizing.preferred()),
				new Case("split 2", "insets 0, gap 0", "[120!][60!]", "[24!]", new String[] {"split 2", "", "wrap", ""}, Sizing.preferred()),
				new Case("cell 1 1", "insets 0, gap 0", "[60!][60!]", "[24!]", new String[] {"cell 1 1", "cell 0 0", "cell 1 0", "cell 0 0, wrap"}, Sizing.preferred()),
				new Case("skip 1", "insets 0, gap 0", "[60!][60!]", "[24!]", new String[] {"", "skip 1", "wrap", ""}, Sizing.preferred()),
				new Case("wrap 3", "insets 0, gap 0", "[60!][60!]", "[24!]", new String[] {"wrap 3", "", "", ""}, Sizing.preferred()),
				new Case("flowy", "insets 0, gap 0, flowy", "[60!][60!]", "[24!]", new String[] {"", "", "", ""}, Sizing.preferred()));
	}

	static JComponent grid(Case c)
	{
		JPanel panel = new JPanel(new MigLayout(c.layout(), c.cols(), c.rows()));
		panel.add(named(cell(), "a"), c.cc()[0]);
		panel.add(named(cell(), "b"), c.cc()[1]);
		panel.add(named(cell(), "c"), c.cc()[2]);
		panel.add(named(cell(), "d"), c.cc()[3]);
		return panel;
	}

	static JLabel cell()
	{
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(60, 24));
		label.setMinimumSize(label.getPreferredSize());
		label.setMaximumSize(label.getPreferredSize());
		return label;
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> SwingLayoutHarness.assertLayout(getClass(), c.name(), () -> grid(c), c.sizing())));
	}
}