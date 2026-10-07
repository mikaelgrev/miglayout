package net.miginfocom.swing.layout;

import java.awt.Dimension;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import net.miginfocom.swing.layout.snapshot.Sizing;
import net.miginfocom.swing.layout.snapshot.SwingLayoutHarness;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static net.miginfocom.swing.layout.snapshot.SwingLayoutHarness.named;

/**
 * A button bar ordered by MigLayout's {@code tag} mechanism. The tags are the same everywhere, but the resulting
 * order depends on the platform's button order ("L_E+U+YNBXOCAH_I_R" on Windows, "L_HE+U+NYBXCOA_I_R" on macOS,
 * "L_HE+UNYACBXO_I_R" on GNOME): on Windows OK comes before Cancel, on macOS/GNOME Cancel comes first.
 */
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

	static JComponent bar(Case c)
	{
		JPanel panel = new JPanel(new MigLayout(c.layout()));
		for (int i = 0; i < c.tags().length; i++)
			panel.add(named(button(Integer.toString(i + 1)), "b" + (i + 1)),
					(i == 0 ? "span, split " + c.tags().length + ", align right, " : "") + c.tags()[i]);
		return panel;
	}

	static JButton button(String text)
	{
		JButton button = new JButton(text);
		button.setPreferredSize(new Dimension(70, 26));
		button.setMinimumSize(button.getPreferredSize());
		button.setMaximumSize(button.getPreferredSize());
		return button;
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> SwingLayoutHarness.assertLayout(getClass(), c.name(), () -> bar(c), c.sizing())));
	}
}