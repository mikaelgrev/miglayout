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
 * A {@link MigLayout} panel nested inside another MigLayout panel. The harness recurses into nested MigLayout
 * containers, so components at every level are captured with root-relative coordinates.
 */
class NestedSnapshotTest
{
	record Case(String name, String outerLayout, String innerLayout, Sizing sizing)
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
				new Case("inner grid", "insets 5, gap 7", "insets 3, gap 5, wrap 2", Sizing.preferred()),
				new Case("inner align right", "insets 5, gap 7", "insets 3, gap 5, align right", Sizing.preferred()));
	}

	static JComponent panel(Case c)
	{
		JPanel outer = new JPanel(new MigLayout(c.outerLayout()));
		outer.add(named(label(80, 20), "o1"));
		JPanel inner = new JPanel(new MigLayout(c.innerLayout()));
		inner.add(named(label(40, 24), "i1"));
		inner.add(named(label(50, 24), "i2"));
		inner.add(named(label(30, 24), "i3"));
		outer.add(named(inner, "inner"), "newline");
		return outer;
	}

	static JLabel label(int w, int h)
	{
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(w, h));
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