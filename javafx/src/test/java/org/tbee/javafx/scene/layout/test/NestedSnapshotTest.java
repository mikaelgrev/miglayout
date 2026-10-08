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

/** A {@link MigPane} nested inside another MigPane; see the Swing {@code NestedSnapshotTest}. */
class NestedSnapshotTest
{
	record Case(String name, String outerLayout, String innerLayout, Sizing sizing)
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
				new Case("inner grid", "insets 5, gap 7", "insets 3, gap 5, wrap 2", Sizing.preferred()),
				new Case("inner align right", "insets 5, gap 7", "insets 3, gap 5, align right", Sizing.preferred()));
	}

	static Parent panel(Case c)
	{
		MigPane outer = new MigPane(c.outerLayout());
		outer.add(named(cell(80, 20), "o1"));
		MigPane inner = new MigPane(c.innerLayout());
		inner.add(named(cell(40, 24), "i1"));
		inner.add(named(cell(50, 24), "i2"));
		inner.add(named(cell(30, 24), "i3"));
		outer.add(named(inner, "inner"), "newline");
		return outer;
	}

	static Region cell(int w, int h)
	{
		Region region = new Region();
		region.setPrefSize(w, h);
		region.setMinSize(w, h);
		region.setMaxSize(w, h);
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