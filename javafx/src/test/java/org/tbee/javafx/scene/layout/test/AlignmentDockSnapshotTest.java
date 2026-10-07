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

/** alignment/dock/direction; see the Swing {@code AlignmentDockSnapshotTest}. */
class AlignmentDockSnapshotTest
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
				new Case("align center", "insets 0, gap 0, align center", "[60!]", "[24!]", new String[] {"wrap", "", "wrap", ""}, Sizing.fixed(200, 150)),
				new Case("alignx right", "insets 0, gap 0, alignx right", "[60!]", "[24!]", new String[] {"wrap", "", "wrap", ""}, Sizing.fixed(200, 150)),
				new Case("aligny bottom", "insets 0, gap 0, aligny bottom", "[60!]", "[24!]", new String[] {"wrap", "", "wrap", ""}, Sizing.fixed(200, 150)),
				new Case("col align right", "insets 0, gap 0", "[right]", "[24!]", new String[] {"", "wrap", "", "wrap"}, Sizing.fixed(200, 50)),
				new Case("comp align right", "insets 0, gap 0", "[200!]", "[24!]", new String[] {"alignx right", "wrap", "", "wrap"}, Sizing.fixed(200, 50)),
				new Case("dock north", "insets 0, gap 0", "", "", new String[] {"dock north", "", "", ""}, Sizing.preferred()),
				new Case("dock west/east", "insets 0, gap 0", "", "", new String[] {"dock west", "dock east", "", ""}, Sizing.preferred()),
				new Case("rtl", "insets 0, gap 0, rtl", "[60!]", "[24!]", new String[] {"wrap", "", "wrap", ""}, Sizing.preferred()),
				new Case("btt", "insets 0, gap 0, btt", "[60!]", "[24!]", new String[] {"wrap", "", "wrap", ""}, Sizing.preferred()));
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