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
 * How an invisible component affects the layout, configurable with {@code hidemode}:
 * 0 = keeps its full size (the gap stays), 1 = keeps the gap, 2 = no size and no gap, 3 = removed from the layout.
 */
class HideModeSnapshotTest
{
	record Case(String name, String layout, Sizing sizing)
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
				new Case("hidemode 0", "insets 0, gap 7, hidemode 0", Sizing.preferred()),
				new Case("hidemode 1", "insets 0, gap 7, hidemode 1", Sizing.preferred()),
				new Case("hidemode 2", "insets 0, gap 7, hidemode 2", Sizing.preferred()),
				new Case("hidemode 3", "insets 0, gap 7, hidemode 3", Sizing.preferred()));
	}

	static JComponent panel(Case c)
	{
		JPanel panel = new JPanel(new MigLayout(c.layout()));
		panel.add(named(cell(), "a"));
		JLabel hidden = cell();
		hidden.setVisible(false);
		panel.add(named(hidden, "b"));
		panel.add(named(cell(), "c"));
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
				() -> SwingLayoutHarness.assertLayout(getClass(), c.name(), () -> panel(c), c.sizing())));
	}
}