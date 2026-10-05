package org.tbee.javafx.scene.layout.test.snapshot;

import java.util.List;

import javafx.geometry.Dimension2D;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import org.junit.jupiter.api.Test;
import org.tbee.javafx.scene.layout.MigPane;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tbee.javafx.scene.layout.test.snapshot.FxLayoutHarness.named;

/**
 * Smoke test of the harness with fully explicit sizes, so the result is the same on every OS (no golden file).
 * Uses "px", because MigPane scales logical pixels (the default unit) by the physical screen DPI.
 */
class FxLayoutHarnessTest
{
	private static Parent fixedLayout()
	{
		MigPane nested = named(new MigPane(new LC().insetsAll("0px").gridGap("0px", "0px"), new AC().size("30px!").fill(), new AC().size("10px!").fill()), "nested");
		nested.add(named(new Label("n"), "inNested"), "width 30px!, height 10px!");

		MigPane pane = new MigPane(new LC().insetsAll("5px").gridGap("10px", "10px"), new AC().size("50px!").fill().gap().size("40px!").fill(), new AC().size("20px!").fill());
		pane.add(named(new Label("a"), "a"));
		pane.add(named(new Label("b"), "b"));
		pane.add(nested, "newline, width 30px!, height 10px!, aligny top");
		return pane;
	}

	@Test
	void packedUsesThePreferredSize()
	{
		List<String> lines = SnapshotAssert.significantLines(FxLayoutHarness.layout(FxLayoutHarnessTest::fixedLayout, null).snapshot().text());
		assertEquals(List.of(
				"container 110 60",
				"a        Label    5  5 50 20",
				"b        Label   65  5 40 20",
				"nested   MigPane  5 35 30 10",
				"inNested Label    5 35 30 10"), lines);
	}

	@Test
	void anExplicitSizeIsApplied()
	{
		FxLayoutHarness.Result result = FxLayoutHarness.layout(FxLayoutHarnessTest::fixedLayout, new Dimension2D(300, 200));
		assertTrue(result.snapshot().text().contains("\ncontainer 300 200\n"), result.snapshot().text());
		assertEquals(300, result.screenshot().width());
		assertEquals(200, result.screenshot().height());
	}
}
