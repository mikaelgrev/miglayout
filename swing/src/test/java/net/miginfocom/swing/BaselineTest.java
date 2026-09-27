package net.miginfocom.swing;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.LC;
import org.junit.Test;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/** Baseline alignment, with components whose baseline depends on both the width and the height they are asked about. */
public class BaselineTest
{
	@Test
	public void componentsInABaselineRowShareOneBaseline()
	{
		JPanel panel = new JPanel(new MigLayout("", "[][][][]", "[baseline]"));
		panel.add(sized(new Probe(), 11, 9, 55, 61));
		panel.add(sized(new JLabel("label"), 12, 7, 50, 33));
		panel.add(sized(new Probe(), 50, 10, 50, 40));
		panel.add(sized(new Probe(), 23, 31, 64, 31));

		layOut(panel, 400, 200);

		int expected = baselineInPanel(panel.getComponent(0));
		for (Component c : panel.getComponents())
			assertEquals("The baseline of " + c, expected, baselineInPanel(c));
	}

	@Test
	public void aComponentIsAskedForItsBaselineOncePerSizeInALayoutPass()
	{
		Probe probe = sized(new Probe(), 20, 8, 60, 44);
		JPanel panel = new JPanel(new MigLayout("wrap 2", "[][grow,fill]", "[baseline][baseline]"));
		panel.add(new JLabel("first"));
		panel.add(probe);
		panel.add(new JLabel("second"));
		panel.add(sized(new Probe(), 30, 30, 30, 30));

		layOut(panel, 300, 200);

		assertEquals(new HashSet<String>(probe.questions).size(), probe.questions.size());
	}

	@Test
	public void aGridAsksForTheBaselineAgainAfterItsComponentSizesWereInvalidated()
	{
		JLabel label = sized(new JLabel("label"), 50, 20, 50, 20);
		Probe probe = sized(new Probe(), 60, 40, 60, 40);
		JPanel panel = new JPanel(null);
		panel.add(label);
		panel.add(probe);

		Map<ComponentWrapper, CC> constraints = new LinkedHashMap<ComponentWrapper, CC>();
		constraints.put(new SwingComponentWrapper(label), new CC());
		constraints.put(new SwingComponentWrapper(probe), new CC());
		Grid grid = new Grid(new SwingContainerWrapper(panel), new LC(), ConstraintParser.parseRowConstraints("[baseline]"),
				ConstraintParser.parseColumnConstraints("[][]"), constraints, null);
		int[] bounds = {0, 0, 300, 200};

		grid.layout(bounds, null, null, false);
		assertEquals(baselineInPanel(label), baselineInPanel(probe));

		probe.lift = 7;
		grid.invalidateContainerSize();
		grid.layout(bounds, null, null, false);
		assertEquals(baselineInPanel(label), baselineInPanel(probe));
	}

	private static void layOut(JPanel panel, int width, int height)
	{
		panel.setSize(width, height);
		panel.doLayout();
	}

	private static int baselineInPanel(Component c)
	{
		return c.getY() + c.getBaseline(c.getWidth(), c.getHeight());
	}

	private static <C extends JComponent> C sized(C c, int minWidth, int minHeight, int prefWidth, int prefHeight)
	{
		c.setMinimumSize(new Dimension(minWidth, minHeight));
		c.setPreferredSize(new Dimension(prefWidth, prefHeight));
		return c;
	}

	private static class Probe extends JComponent
	{
		final List<String> questions = new ArrayList<String>();
		int lift = 0;

		@Override
		public int getBaseline(int width, int height)
		{
			questions.add(width + "x" + height);
			return (height * 2) / 3 + (width % 5) + lift;
		}

		@Override
		public BaselineResizeBehavior getBaselineResizeBehavior()
		{
			return BaselineResizeBehavior.OTHER;
		}
	}
}
