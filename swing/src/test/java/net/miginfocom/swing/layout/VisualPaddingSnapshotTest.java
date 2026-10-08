package net.miginfocom.swing.layout;

import java.awt.Dimension;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import net.miginfocom.swing.layout.snapshot.Sizing;
import net.miginfocom.swing.layout.snapshot.SwingLayoutHarness;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static net.miginfocom.swing.layout.snapshot.SwingLayoutHarness.named;

/**
 * Whether a component's visual bounds (decorations like a button boxing) are counted, controlled by the
 * {@code novisualpadding} layout constraint. On the Swing Metal look and feel this is mostly a no-op, but the toggle
 * is still pinned; the macOS-specific visual padding would be exercised when the component publishes one.
 */
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

	static JComponent panel(Case c)
	{
		JButton button = new JButton("OK");
		button.setPreferredSize(new Dimension(60, 24));
		button.setMinimumSize(button.getPreferredSize());
		button.setMaximumSize(button.getPreferredSize());

		JPanel panel = new JPanel(new MigLayout(c.layout()));
		panel.add(named(button, "b"));
		panel.add(named(button(), "b2"));
		return panel;
	}

	static JButton button()
	{
		JButton button = new JButton("Cancel");
		button.setPreferredSize(new Dimension(70, 24));
		button.setMinimumSize(button.getPreferredSize());
		button.setMaximumSize(button.getPreferredSize());
		return button;
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> SwingLayoutHarness.assertLayout(getClass(), c.name(), () -> panel(c), c.sizing())));
	}
}