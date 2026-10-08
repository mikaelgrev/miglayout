package net.miginfocom.swing.layout;

import java.awt.Dimension;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import net.miginfocom.swing.layout.snapshot.Sizing;
import net.miginfocom.swing.layout.snapshot.SwingLayoutHarness;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static net.miginfocom.swing.layout.snapshot.SwingLayoutHarness.named;

/**
 * Positioning a component relative to another component by its {@code id}, e.g. {@code pos b1.x2 b1.y}, and
 * container/visual links in {@code pos}.
 */
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

	static JComponent panel(Case c)
	{
		JPanel panel = new JPanel(new MigLayout(c.layout()));
		panel.add(named(cell(), "b1"), c.cc()[0]);
		panel.add(named(cell(), "b2"), c.cc()[1]);
		return panel;
	}

	static JLabel cell()
	{
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(60, 24));
		label.setMinimumSize(label.getPreferredSize());
		label.setMaximumSize(label.getPreferredSize());
		return label;
	}

	@TestFactory
	Stream<DynamicTest> layout()
	{
		return cases().map(c -> DynamicTest.dynamicTest(c.toString(),
				() -> SwingLayoutHarness.assertLayout(getClass(), c.name(), () -> panel(c), c.sizing())));
	}
}