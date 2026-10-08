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

/** Positioning relative to another component by {@code id}; see the Swing {@code ComponentLinkSnapshotTest}. */
class ComponentLinkSnapshotTest
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
				new Case("right of b1", "insets 0, gap 0", new String[] {"id b1, pos 20 40", "pos b1.x2+10 b1.y"}, Sizing.fixed(200, 100)),
				new Case("below b1", "insets 0, gap 0", new String[] {"id b1, pos 20 40", "pos b1.x b1.y2+10"}, Sizing.fixed(200, 100)),
				new Case("aligned right edge", "insets 0, gap 0", new String[] {"id b1, pos 20 40", "pos (b1.x2-pref) b1.y"}, Sizing.fixed(200, 100)));
	}

	static Parent panel(Case c)
	{
		MigPane pane = new MigPane(c.layout());
		pane.add(named(cell(), "b1"), c.cc()[0]);
		pane.add(named(cell(), "b2"), c.cc()[1]);
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