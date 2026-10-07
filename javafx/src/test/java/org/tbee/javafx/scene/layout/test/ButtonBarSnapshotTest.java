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

/** A button bar ordered by MigLayout's {@code tag} mechanism; see the Swing {@code ButtonBarSnapshotTest}. */
class ButtonBarSnapshotTest
{
	record Case(String name, String layout, String[] tags, Sizing sizing)
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
				new Case("ok cancel", "insets 0, gap 7, wrap", new String[] {"tag ok", "tag cancel"}, Sizing.preferred()),
				new Case("full bar", "insets 0, gap 7, wrap", new String[] {"tag yes", "tag no", "tag help", "tag other", "tag cancel", "tag ok"}, Sizing.preferred()));
	}

	static Parent bar(Case c)
	{
		MigPane pane = new MigPane(c.layout());
		for (int i = 0; i < c.tags().length; i++)
			pane.add(named(button(Integer.toString(i + 1)), "b" + (i + 1)),
					(i == 0 ? "span, split " + c.tags().length + ", align right, " : "") + c.tags()[i]);
		return pane;
	}

	static Button button(String text)
	{
		Button button = new Button(text);
		button.setPrefSize(70, 26);
		button.setMinSize(70, 26);
		button.setMaxSize(70, 26);
		return button;
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> FxLayoutHarness.assertLayout(getClass(), c.name(), () -> bar(c), c.sizing())));
	}
}