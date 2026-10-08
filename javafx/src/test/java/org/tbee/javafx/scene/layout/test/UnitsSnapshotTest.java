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

/** The size units MigLayout accepts; see the Swing {@code UnitsSnapshotTest}. */
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

	static Parent panel(Case c)
	{
		MigPane pane = new MigPane(c.layout(), c.cols(), c.rows());
		pane.add(named(cell(), "a"), "grow");
		return pane;
	}

	static Region cell()
	{
		Region region = new Region();
		region.setPrefSize(10, 10);
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