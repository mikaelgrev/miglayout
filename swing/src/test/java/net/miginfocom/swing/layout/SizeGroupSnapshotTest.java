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
 * {@code sizegroup} makes components of a named group share one size (the maximum), and {@code endgroupx} links their
 * trailing edges.
 */
class SizeGroupSnapshotTest
{
	record Case(String name, String layout, String[] cc, Sizing sizing)
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
				new Case("sizegroup", "insets 0, gap 7", new String[] {"sizegroup g1", "sizegroup g1", "sizegroup g1"}, Sizing.preferred()),
				new Case("two groups", "insets 0, gap 7", new String[] {"sizegroup a", "sizegroup b", "sizegroup a", "sizegroup b"}, Sizing.preferred()));
	}

	static JComponent panel(Case c)
	{
		int[] widths = {60, 100, 80, 40};
		JPanel panel = new JPanel(new MigLayout(c.layout()));
		for (int i = 0; i < c.cc().length; i++)
			panel.add(named(cell(widths[i], 24), "c" + (i + 1)), c.cc()[i]);
		return panel;
	}

	static JLabel cell(int w, int h)
	{
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(w, h));
		label.setMinimumSize(new Dimension(0, 0));
		return label;
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> SwingLayoutHarness.assertLayout(getClass(), c.name(), () -> panel(c), c.sizing())));
	}
}