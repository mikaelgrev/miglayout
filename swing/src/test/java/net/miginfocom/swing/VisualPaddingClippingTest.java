package net.miginfocom.swing;

import org.junit.jupiter.api.Test;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Insets;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** How a component's visual padding (the "visualPadding" client property, which FlatLaf sets on buttons, text
 * fields, combo boxes, scroll panes and more) turns into its bounds, and where those bounds are cut.
 * <p>
 * MigLayout lays out a padded component by its <i>visual</i> box: the cell gets the component's bounds minus the
 * padding. When it finally sets the bounds, it adds the padding back on every side, so the component reaches out
 * of its cell by exactly its padding. Since #94 the result is cut where it would leave the parent, so that the
 * parent does not clip what the component paints there, such as a focus ring.
 * <p>
 * Every test lays out one component in a parent of 200 x 100 pixels with the "fill" constraint, so that the
 * component's cell is the parent's whole content area: the parent minus its insets.
 * <pre>
 *     0                                    200
 *   0 +--------------------------------------+
 *     |   insets                             |
 *     |   +------------------------------+   |
 *     |   |  cell = content area         |   |
 *     |   +------------------------------+   |
 *     |                                      |
 * 100 +--------------------------------------+
 * </pre>
 * Which insets matter: the "insets" layout constraint is applied inside the grid, but the parent's own Swing
 * insets, those of its border, are taken out of the bounds the grid is laid out in. The regression these tests
 * pin came from those bounds: the cut compared the component's far edges, which are relative to the parent, with
 * the width and height of the bounds, which leave out the border. So a component in a parent with a border lost
 * exactly the border's left and top insets on its far side, even with a visual padding of zero, which is what
 * FlatLaf's Light and Dark themes set.
 */
public class VisualPaddingClippingTest
{
	private static final int PARENT_WIDTH = 200;
	private static final int PARENT_HEIGHT = 100;

	/** Lays out a single component in a parent of 200 x 100 pixels and returns the bounds it was given.
	 * @param layoutInsets the "insets" layout constraint of the parent
	 * @param border the insets of the parent's border: top, left, bottom, right
	 * @param visualPadding the component's visual padding, or {@code null} for none */
	private static Rectangle boundsOfTheOnlyComponent(String layoutInsets, Insets border, Insets visualPadding)
	{
		JPanel parent = new JPanel(new MigLayout("fill, insets " + layoutInsets));
		parent.setBorder(new EmptyBorder(border));
		JComponent component = new JPanel();
		if (visualPadding != null)
			component.putClientProperty("visualPadding", visualPadding);
		parent.add(component, "grow");

		parent.setSize(PARENT_WIDTH, PARENT_HEIGHT);
		parent.doLayout();
		return component.getBounds();
	}

	/** The reference for the tests below: a component without visual padding fills its cell, which is the
	 * parent minus its border. */
	@Test
	public void aComponentWithoutVisualPaddingFillsTheContentArea()
	{
		Rectangle bounds = boundsOfTheOnlyComponent("0", new Insets(12, 12, 12, 12), null);

		assertEquals(new Rectangle(12, 12, 176, 76), bounds);
	}

	/** A padding of zero adds nothing and has nothing to cut, so the component fills its cell just as one without
	 * padding does. This is what FlatLaf sets on most components.
	 * <p>
	 * Regression: since #94 the component was cut by the border's left and top insets, to 164 x 64 here: its far
	 * edge, 12 + 176 = 188, was compared with the width of the content area, 176, instead of the parent's, 200. */
	@Test
	public void aZeroVisualPaddingFillsTheContentAreaOfAParentWithABorder()
	{
		Rectangle bounds = boundsOfTheOnlyComponent("0", new Insets(12, 12, 12, 12), new Insets(0, 0, 0, 0));

		assertEquals(new Rectangle(12, 12, 176, 76), bounds);
	}

	/** The "insets" layout constraint was never affected, because it is applied inside the grid, which is laid
	 * out in the parent's whole bounds here. Pinned so that the difference between the two kinds of insets stays
	 * visible. */
	@Test
	public void aZeroVisualPaddingFillsTheContentAreaOfAParentWithLayoutInsets()
	{
		Rectangle bounds = boundsOfTheOnlyComponent("12px", new Insets(0, 0, 0, 0), new Insets(0, 0, 0, 0));

		assertEquals(new Rectangle(12, 12, 176, 76), bounds);
	}

	/** Only the left and top insets of the border moved the far edges, so each is pinned alone: a left inset alone
	 * must not take anything from the component's width.
	 * <p>
	 * Regression: since #94 the width came out as 176, the content width minus the left inset once more. */
	@Test
	public void aLeftBorderInsetAloneDoesNotNarrowAComponentWithVisualPadding()
	{
		Rectangle bounds = boundsOfTheOnlyComponent("0", new Insets(0, 12, 0, 0), new Insets(0, 0, 0, 0));

		assertEquals(new Rectangle(12, 0, 188, 100), bounds);
	}

	/** And a top inset alone must not take anything from the component's height.
	 * <p>
	 * Regression: since #94 the height came out as 76, the content height minus the top inset once more. */
	@Test
	public void aTopBorderInsetAloneDoesNotShortenAComponentWithVisualPadding()
	{
		Rectangle bounds = boundsOfTheOnlyComponent("0", new Insets(12, 0, 0, 0), new Insets(0, 0, 0, 0));

		assertEquals(new Rectangle(0, 12, 200, 88), bounds);
	}

	/** A padding may reach into the parent's border: that is still inside the parent, so nothing is cut. Here the
	 * cell is 12, 12, 176 x 76 and the padding of 3 on each side adds 3 around it.
	 * <p>
	 * Regression: since #94 the component was cut to 9, 9, 167 x 67 here. Cutting it at the far edges of the
	 * content area instead, which would fix the case of a zero padding, would still cut it to 9, 9, 179 x 79. */
	@Test
	public void aVisualPaddingMayReachIntoTheBorderOfTheParent()
	{
		Rectangle bounds = boundsOfTheOnlyComponent("0", new Insets(12, 12, 12, 12), new Insets(3, 3, 3, 3));

		assertEquals(new Rectangle(9, 9, 182, 82), bounds);
	}

	/** What #94 is for: without insets, a padding of 5 would make the component 210 x 110 at -5, -5, outside the
	 * parent on every side. It is cut to the parent instead. */
	@Test
	public void aVisualPaddingIsCutWhereItWouldLeaveTheParent()
	{
		Rectangle bounds = boundsOfTheOnlyComponent("0", new Insets(0, 0, 0, 0), new Insets(5, 5, 5, 5));

		assertEquals(new Rectangle(0, 0, PARENT_WIDTH, PARENT_HEIGHT), bounds);
	}

	/** The cut is made on each side by itself: a padding that leaves the parent only on its right and bottom is
	 * cut only there, and keeps what it adds on its left and top, where the border leaves room for it. */
	@Test
	public void aVisualPaddingIsCutOnlyOnTheSidesWhereItLeavesTheParent()
	{
		Rectangle bounds = boundsOfTheOnlyComponent("0", new Insets(12, 12, 0, 0), new Insets(4, 4, 4, 4));

		assertEquals(new Rectangle(8, 8, 192, 92), bounds);
	}
}
