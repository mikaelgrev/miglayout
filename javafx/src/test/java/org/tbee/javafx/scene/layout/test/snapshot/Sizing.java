package org.tbee.javafx.scene.layout.test.snapshot;

/**
 * How large the laid out container is made: its preferred size (like a packed window), a fixed size, or the
 * preferred size plus a delta (e.g. a window the user made larger or smaller). The preferred size is the one the
 * container reports inside the real window, so it uses the real fonts and look and feel.
 *
 * Shared snapshot infrastructure: keep identical in the swing and javafx modules (except the package).
 */
public final class Sizing
{
	private final Double fixedWidth;
	private final Double fixedHeight;
	private final double deltaWidth;
	private final double deltaHeight;

	private Sizing(Double fixedWidth, Double fixedHeight, double deltaWidth, double deltaHeight)
	{
		this.fixedWidth = fixedWidth;
		this.fixedHeight = fixedHeight;
		this.deltaWidth = deltaWidth;
		this.deltaHeight = deltaHeight;
	}

	/** The preferred size, like {@code JFrame.pack()} / {@code Stage.sizeToScene()}. */
	public static Sizing preferred()
	{
		return new Sizing(null, null, 0, 0);
	}

	public static Sizing fixed(double width, double height)
	{
		return new Sizing(width, height, 0, 0);
	}

	/** The preferred size plus the deltas, which may be negative to make it smaller than preferred. */
	public static Sizing preferredPlus(double deltaWidth, double deltaHeight)
	{
		return new Sizing(null, null, deltaWidth, deltaHeight);
	}

	public double width(double preferredWidth)
	{
		return fixedWidth != null ? fixedWidth : Math.max(0, preferredWidth + deltaWidth);
	}

	public double height(double preferredHeight)
	{
		return fixedHeight != null ? fixedHeight : Math.max(0, preferredHeight + deltaHeight);
	}

	@Override
	public String toString()
	{
		if (fixedWidth != null)
			return "fixed " + LayoutSnapshot.format(fixedWidth) + "x" + LayoutSnapshot.format(fixedHeight);
		if (deltaWidth == 0 && deltaHeight == 0)
			return "preferred";
		return "preferred" + (deltaWidth >= 0 ? "+" : "") + LayoutSnapshot.format(deltaWidth) + (deltaHeight >= 0 ? "+" : "") + LayoutSnapshot.format(deltaHeight);
	}
}
