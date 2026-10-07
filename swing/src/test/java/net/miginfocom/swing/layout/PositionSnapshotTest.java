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
 * Absolute placement via {@code pos}. The container has a fixed size, so resolutions like {@code 0.5al} and
 * {@code visual.x2-pref} are well defined.
 */
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

	static JComponent panel(Case c)
	{
		JPanel panel = new JPanel(new MigLayout(c.layout()));
		for (int i = 0; i < c.cc().length; i++)
			panel.add(named(cell(), "c" + (i + 1)), c.cc()[i]);
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