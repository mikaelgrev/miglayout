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
 * The gaps between the components and the insets around them. The cells have a fixed size, so the recorded bounds
 * depend only on gaps/insets and on the platform (which resolves the {@code rel}/{@code panel} units).
 */
class GapsAndInsetsSnapshotTest
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
				new Case("default", "wrap 2", "[60!][60!]", "[24!]", new String[] {"", "", "", ""}, Sizing.preferred()),
				new Case("layout gap 0", "wrap 2, gap 0, insets 5", "[60!][60!]", "[24!]", new String[] {"", "", "", ""}, Sizing.preferred()),
				new Case("layout gap 15px 6px", "wrap 2, insets 5, gap 15px 6px", "[60!][60!]", "[24!]", new String[] {"", "", "", ""}, Sizing.preferred()),
				new Case("layout gapx gapy", "wrap 2, insets 5, gapx 20px, gapy 9px", "[60!][60!]", "[24!]", new String[] {"", "", "", ""}, Sizing.preferred()),
				new Case("column gap 12px", "wrap 2, insets 5, gap 0", "[60!][12px][60!]", "[24!]", new String[] {"", "", "", ""}, Sizing.preferred()),
				new Case("row gap 8px", "wrap 2, insets 5, gap 0", "[60!]", "[24!]8px[24!]", new String[] {"", "", "", ""}, Sizing.preferred()),
				new Case("component gapbefore", "wrap 2, insets 5, gap 0", "[60!][60!]", "[24!]", new String[] {"gapbefore 14px", "", "", ""}, Sizing.preferred()),
				new Case("component gaptop", "wrap 2, insets 5, gap 0", "[60!][60!]", "[24!]", new String[] {"", "gaptop 10px", "", ""}, Sizing.preferred()),
				new Case("insets 10 20 30 40", "wrap 2, gap 0, insets 10 20 30 40", "[60!][60!]", "[24!]", new String[] {"", "", "", ""}, Sizing.preferred()),
				new Case("panel insets", "wrap 2, gap 0, insets panel", "[60!][60!]", "[24!]", new String[] {"", "", "", ""}, Sizing.preferred()),
				new Case("dialog insets", "wrap 2, gap 0, insets dialog", "[60!][60!]", "[24!]", new String[] {"", "", "", ""}, Sizing.preferred()));
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