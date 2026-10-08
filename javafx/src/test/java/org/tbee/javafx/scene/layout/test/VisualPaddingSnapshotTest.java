package org.tbee.javafx.scene.layout.test;

import java.util.stream.Stream;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.tbee.javafx.scene.layout.MigPane;
import org.tbee.javafx.scene.layout.test.snapshot.FxLayoutHarness;
import org.tbee.javafx.scene.layout.test.snapshot.Sizing;

import static org.tbee.javafx.scene.layout.test.snapshot.FxLayoutHarness.named;

/** {@code novisualpadding} toggle; see the Swing {@code VisualPaddingSnapshotTest}. */
class VisualPaddingSnapshotTest
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
				new Case("with visual padding", "insets 5, gap 7", Sizing.preferred()),
				new Case("novisualpadding", "insets 5, gap 7, novisualpadding", Sizing.preferred()));
	}

	static Parent panel(Case c)
	{
		Button ok = new Button("OK");
		ok.setPrefSize(60, 24);
		ok.setMinSize(60, 24);
		ok.setMaxSize(60, 24);
		Button cancel = new Button("Cancel");
		cancel.setPrefSize(70, 24);
		cancel.setMinSize(70, 24);
		cancel.setMaxSize(70, 24);

		MigPane pane = new MigPane(c.layout());
		pane.add(named(ok, "b"));
		pane.add(named(cancel, "b2"));
		return pane;
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> FxLayoutHarness.assertLayout(getClass(), c.name(), () -> panel(c), c.sizing())));
	}
}