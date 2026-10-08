package org.tbee.javafx.scene.layout.test;

import java.util.stream.Stream;

import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.tbee.javafx.scene.layout.MigPane;
import org.tbee.javafx.scene.layout.test.snapshot.FxLayoutHarness;
import org.tbee.javafx.scene.layout.test.snapshot.Sizing;

import static org.tbee.javafx.scene.layout.test.snapshot.FxLayoutHarness.named;

/** {@code sizegroup}; see the Swing {@code SizeGroupSnapshotTest}. */
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

	static Parent panel(Case c)
	{
		int[] widths = {60, 100, 80, 40};
		MigPane pane = new MigPane(c.layout());
		for (int i = 0; i < c.cc().length; i++)
			pane.add(named(cell(widths[i], 24), "c" + (i + 1)), c.cc()[i]);
		return pane;
	}

	static Region cell(int w, int h)
	{
		Region region = new Region();
		region.setPrefSize(w, h);
		region.setMinSize(0, 0);
		region.setBackground(new Background(new BackgroundFill(Color.web("#d0e0ff"), CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
		return region;
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> FxLayoutHarness.assertLayout(getClass(), c.name(), () -> panel(c), c.sizing())));
	}
}