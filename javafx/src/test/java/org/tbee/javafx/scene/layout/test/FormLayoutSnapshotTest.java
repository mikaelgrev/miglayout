package org.tbee.javafx.scene.layout.test;

import java.util.stream.Stream;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.tbee.javafx.scene.layout.MigPane;
import org.tbee.javafx.scene.layout.test.snapshot.FxLayoutHarness;
import org.tbee.javafx.scene.layout.test.snapshot.Sizing;

import static org.tbee.javafx.scene.layout.test.snapshot.FxLayoutHarness.named;

/**
 * A typical form: right aligned labels, growing text fields, and an OK / Cancel button bar.
 * Covers the platform gaps (related / unrelated), panel insets, grow + fill, alignment and the platform button order.
 * The layout is compared with the golden snapshot of the OS the test runs on.
 * Same scenario as the Swing {@code FormLayoutSnapshotTest}.
 */
class FormLayoutSnapshotTest
{
	record Case(String name, Sizing sizing)
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
				new Case("preferred", Sizing.preferred()),
				new Case("wide", Sizing.preferredPlus(200, 50)));
	}

	static Parent form()
	{
		MigPane pane = new MigPane("", "[right][grow,fill]", "[]rel[]unrel[]");

		pane.add(named(new Label("Name:"), "nameLabel"));
		TextField nameField = named(new TextField(), "nameField");
		nameField.setPrefColumnCount(15);
		pane.add(nameField, "wrap");

		pane.add(named(new Label("E-mail:"), "emailLabel"));
		TextField emailField = named(new TextField(), "emailField");
		emailField.setPrefColumnCount(15);
		pane.add(emailField, "wrap");

		pane.add(named(new Button("OK"), "okButton"), "span 2, split 2, align right, tag ok");
		pane.add(named(new Button("Cancel"), "cancelButton"), "tag cancel");
		return pane;
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> FxLayoutHarness.assertLayout(getClass(), c.name(), FormLayoutSnapshotTest::form, c.sizing())));
	}
}
