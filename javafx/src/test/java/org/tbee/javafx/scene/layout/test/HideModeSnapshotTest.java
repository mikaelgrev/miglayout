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

/** How an invisible component affects the layout via {@code hidemode}; see the Swing {@code HideModeSnapshotTest}. */
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

	static Parent panel(Case c)
	{
		MigPane pane = new MigPane(c.layout());
		pane.add(named(cell(), "a"));
		Region hidden = cell();
		hidden.setVisible(false);
		pane.add(named(hidden, "b"));
		pane.add(named(cell(), "c"));
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
				() -> FxLayoutHarness.assertLayout(getClass(), c.name(), () -> panel(c), c.sizing())));
	}
}