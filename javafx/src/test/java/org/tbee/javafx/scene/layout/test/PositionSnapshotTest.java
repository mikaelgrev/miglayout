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

/** Absolute placement via {@code pos}; see the Swing {@code PositionSnapshotTest}. */
class PositionSnapshotTest
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
				new Case("pixel pos", "insets 0, gap 0, wrap", new String[] {"pos 100 50", "pos 20 80"}, Sizing.fixed(300, 200)),
				new Case("center align", "insets 0, gap 0, wrap", new String[] {"pos 0.5al 0.5al", "pos 0.25al 0.25al"}, Sizing.fixed(300, 200)),
				new Case("bottom right", "insets 0, gap 0, wrap", new String[] {"pos 1al 1al", "pos 1al 0al"}, Sizing.fixed(300, 200)),
				new Case("percent pos", "insets 0, gap 0, wrap", new String[] {"pos 25% 25%", "pos 50% 50%"}, Sizing.fixed(300, 200)));
	}

	static Parent panel(Case c)
	{
		MigPane pane = new MigPane(c.layout());
		for (int i = 0; i < c.cc().length; i++)
			pane.add(named(cell(), "c" + (i + 1)), c.cc()[i]);
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