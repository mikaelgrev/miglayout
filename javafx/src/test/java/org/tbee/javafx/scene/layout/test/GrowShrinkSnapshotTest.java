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

/** grow/shrink/push; see the Swing {@code GrowShrinkSnapshotTest}. */
class GrowShrinkSnapshotTest
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
				new Case("growx fills", "insets 0, gap 0", "[60!][grow]", "[24!]", new String[] {"", "growx", ""}, Sizing.fixed(200, 24)),
				new Case("grow weight 0 vs 100", "insets 0, gap 0", "[60!][grow 0][grow 100]", "[24!]", new String[] {"", "growx", "growx"}, Sizing.fixed(300, 24)),
				new Case("growy", "insets 0, gap 0, wrap 1", "[60!]", "[24!][grow]", new String[] {"", "growy", "growy"}, Sizing.fixed(60, 100)),
				new Case("push center", "insets 0, gap 0", "push[60!][60!]push", "[24!]", new String[] {"", "", ""}, Sizing.fixed(200, 24)),
				new Case("push before", "insets 0, gap 0", "push[60!][60!]", "[24!]", new String[] {"", "", ""}, Sizing.fixed(200, 24)),
				new Case("shrinkx", "insets 0, gap 0", "[60!][shrink]", "[24!]", new String[] {"", "shrinkx", ""}, Sizing.fixed(100, 24)),
				new Case("size min:pref:max", "insets 0, gap 0", "[50:100:150]", "[24!]", new String[] {"growx", "", ""}, Sizing.fixed(120, 24)));
	}

	static Parent grid(Case c)
	{
		MigPane pane = new MigPane(c.layout(), c.cols(), c.rows());
		pane.add(named(cell(), "a"), c.cc()[0]);
		pane.add(named(cell(), "b"), c.cc()[1]);
		pane.add(named(cell(), "c"), c.cc()[2]);
		return pane;
	}

	static Region cell()
	{
		Region region = new Region();
		region.setPrefSize(60, 24);
		region.setMinSize(60, 24);
		region.setBackground(new Background(new BackgroundFill(Color.web("#d0e0ff"), CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
		return region; // max stays large, so grow/shrink/fill can change it
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> FxLayoutHarness.assertLayout(getClass(), c.name(), () -> grid(c), c.sizing())));
	}
}