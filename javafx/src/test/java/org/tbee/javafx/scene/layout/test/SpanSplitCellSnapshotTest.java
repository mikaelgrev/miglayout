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

/** wrap/span/split/cell; see the Swing {@code SpanSplitCellSnapshotTest}. */
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