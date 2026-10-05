package net.miginfocom.swing.layout.snapshot;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import org.junit.jupiter.api.Test;

import static net.miginfocom.swing.layout.snapshot.SwingLayoutHarness.named;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Smoke test of the harness with fully explicit sizes, so the result is the same on every OS (no golden file). */
class SwingLayoutHarnessTest
{
	private static JComponent fixedLayout()
	{
		JPanel nested = named(new JPanel(new MigLayout("insets 0, gap 0", "[30!,fill]", "[10!,fill]")), "nested");
		nested.add(named(new JLabel("n"), "inNested"), "width 30!, height 10!");

		JPanel panel = new JPanel(new MigLayout("insets 5, gap 10", "[50!,fill][40!,fill]", "[20!,fill]"));
		panel.add(named(new JLabel("a"), "a"));
		panel.add(named(new JLabel("b"), "b"));
		panel.add(nested, "newline, width 30!, height 10!, aligny top");
		return panel;
	}

	@Test
	void packedUsesThePreferredSize()
	{
		List<String> lines = SnapshotAssert.significantLines(SwingLayoutHarness.layout(SwingLayoutHarnessTest::fixedLayout, null).snapshot().text());
		assertEquals(List.of(
				"container 110 60",
				"a        JLabel  5  5 50 20",
				"b        JLabel 65  5 40 20",
				"nested   JPanel  5 35 30 10",
				"inNested JLabel  5 35 30 10"), lines);
	}

	@Test
	void anExplicitSizeIsApplied()
	{
		SwingLayoutHarness.Result result = SwingLayoutHarness.layout(SwingLayoutHarnessTest::fixedLayout, new Dimension(300, 200));
		assertTrue(result.snapshot().text().contains("\ncontainer 300 200\n"), result.snapshot().text());
		assertEquals(300, result.screenshot().width());
		assertEquals(200, result.screenshot().height());
	}
}
