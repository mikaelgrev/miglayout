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
 * Container/column/component alignment, dock, and text direction.
 */
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

	static JComponent grid(Case c)
	{
		JPanel panel = new JPanel(new MigLayout(c.layout(), c.cols(), c.rows()));
		panel.add(named(cell(), "a"), c.cc()[0]);
		panel.add(named(cell(), "b"), c.cc()[1]);
		panel.add(named(cell(), "c"), c.cc()[2]);
		panel.add(named(cell(), "d"), c.cc()[3]);
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
				() -> SwingLayoutHarness.assertLayout(getClass(), c.name(), () -> grid(c), c.sizing())));
	}
}