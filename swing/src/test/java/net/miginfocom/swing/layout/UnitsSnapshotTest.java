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
 * The size units MigLayout accepts. The default unit is {@code lp} (logical pixel), which depends on the platform's
 * DPI, so these baselines are per platform too.
 */
class UnitsSnapshotTest
{
	record Case(String name, String layout, String cols, String rows, Sizing sizing)
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
				new Case("pixel 80px", "insets 0, gap 0", "[80px!]", "[24!]", Sizing.preferred()),
				new Case("logical 80lp", "insets 0, gap 0", "[80lp!]", "[24!]", Sizing.preferred()),
				new Case("point 80pt", "insets 0, gap 0", "[80pt!]", "[24!]", Sizing.preferred()),
				new Case("millimeter 20mm", "insets 0, gap 0", "[20mm!]", "[24!]", Sizing.preferred()),
				new Case("percent 50%", "insets 0, gap 0", "[50%]", "[24!]", Sizing.fixed(200, 24)),
				new Case("row millimeter 10mm", "insets 0, gap 0", "[80px!]", "[10mm!]", Sizing.preferred()));
	}

	static JComponent panel(Case c)
	{
		JPanel panel = new JPanel(new MigLayout(c.layout(), c.cols(), c.rows()));
		panel.add(named(cell(), "a"), "grow");
		return panel;
	}

	static JLabel cell()
	{
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(10, 10));
		label.setMinimumSize(new Dimension(0, 0));
		return label; // max stays huge, so it fills its cell
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> SwingLayoutHarness.assertLayout(getClass(), c.name(), () -> panel(c), c.sizing())));
	}
}