package net.miginfocom.swing.layout;

import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import net.miginfocom.swing.layout.snapshot.Sizing;
import net.miginfocom.swing.layout.snapshot.SwingLayoutHarness;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static net.miginfocom.swing.layout.snapshot.SwingLayoutHarness.named;

/**
 * A typical form: right aligned labels, growing text fields, and an OK / Cancel button bar.
 * Covers the platform gaps (related / unrelated), panel insets, grow + fill, alignment and the platform button order.
 * The layout is compared with the golden snapshot of the OS the test runs on.
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

	static JComponent form()
	{
		JPanel panel = new JPanel(new MigLayout("", "[right][grow,fill]", "[]rel[]unrel[]"));

		panel.add(named(new JLabel("Name:"), "nameLabel"));
		panel.add(named(new JTextField(15), "nameField"), "wrap");

		panel.add(named(new JLabel("E-mail:"), "emailLabel"));
		panel.add(named(new JTextField(15), "emailField"), "wrap");

		panel.add(named(new JButton("OK"), "okButton"), "span 2, split 2, align right, tag ok");
		panel.add(named(new JButton("Cancel"), "cancelButton"), "tag cancel");
		return panel;
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> SwingLayoutHarness.assertLayout(getClass(), c.name(), FormLayoutSnapshotTest::form, c.sizing())));
	}
}
