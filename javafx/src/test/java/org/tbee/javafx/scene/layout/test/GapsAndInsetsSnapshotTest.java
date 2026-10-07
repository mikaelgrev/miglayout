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

/** Gaps and insets; see the Swing {@code GapsAndInsetsSnapshotTest}. */
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

	static Parent grid(Case c)
	{
		MigPane pane = new MigPane(c.layout(), c.cols(), c.rows());
		pane.add(named(cell(), "a"), c.cc()[0]);
		pane.add(named(cell(), "b"), c.cc()[1]);
		pane.add(named(cell(), "c"), c.cc()[2]);
		pane.add(named(cell(), "d"), c.cc()[3]);
		return pane;
	}

	static Region cell()
	{
		Region region = new Region();
		region.setPrefSize(60, 24);
		region.setMinSize(60, 24);
		region.setMaxSize(60, 24);
		region.setBackground(new Background(new BackgroundFill(Color.web("#d0e0ff"), CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
		return region;
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> FxLayoutHarness.assertLayout(getClass(), c.name(), () -> grid(c), c.sizing())));
	}
}