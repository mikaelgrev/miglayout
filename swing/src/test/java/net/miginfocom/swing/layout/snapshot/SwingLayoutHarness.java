package net.miginfocom.swing.layout.snapshot;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.miginfocom.layout.PlatformDefaults;
import net.miginfocom.swing.MigLayout;

/**
 * Lays out a Swing container the way an application would (in a packed or resized {@link JFrame}, Metal look and
 * feel, MigLayout platform defaults of the real OS) and captures a {@link LayoutSnapshot} and {@link Screenshot}.
 *
 * <pre>
 * SwingLayoutHarness.assertLayout(getClass(), "preferred", () -> {
 *     JPanel panel = new JPanel(new MigLayout());
 *     panel.add(named(new JButton("OK"), "ok"));
 *     return panel;
 * }, null);
 * </pre>
 *
 * Every component that should appear in the snapshot needs a name ({@link Component#setName}). Children of nested
 * containers that use MigLayout are included as well, with coordinates relative to the root container.
 */
public final class SwingLayoutHarness
{
	private SwingLayoutHarness()
	{
	}

	/** The result of laying out a container. */
	public static final class Result
	{
		private final LayoutSnapshot snapshot;
		private final Screenshot screenshot;

		Result(LayoutSnapshot snapshot, Screenshot screenshot)
		{
			this.snapshot = snapshot;
			this.screenshot = screenshot;
		}

		public LayoutSnapshot snapshot()
		{
			return snapshot;
		}

		public Screenshot screenshot()
		{
			return screenshot;
		}
	}

	/** Lays out the container and compares it with the golden snapshot, see {@link SnapshotAssert}. */
	public static void assertLayout(Class<?> testClass, String caseName, Supplier<? extends JComponent> content, Dimension size)
	{
		Result result = layout(testClass.getSimpleName() + " - " + caseName, content, size);
		SnapshotAssert.assertMatches(testClass, caseName, result.snapshot(), result.screenshot());
	}

	/**
	 * @param content Creates the container to lay out; called on the event dispatch thread.
	 * @param size The size of the container, or null to use its preferred size (like {@link JFrame#pack()}).
	 */
	public static Result layout(Supplier<? extends JComponent> content, Dimension size)
	{
		return layout("layout snapshot", content, size);
	}

	/**
	 * @param title The window title, shown when observing layouts ({@link ShowLayouts}).
	 * @param content Creates the container to lay out; called on the event dispatch thread.
	 * @param size The size of the container, or null to use its preferred size (like {@link JFrame#pack()}).
	 */
	public static Result layout(String title, Supplier<? extends JComponent> content, Dimension size)
	{
		if (GraphicsEnvironment.isHeadless())
			throw new IllegalStateException("Layout snapshot tests need a display (on Linux CI use xvfb)");

		JFrame[] frame = new JFrame[1];
		JComponent[] root = new JComponent[1];
		try {
			onEdt(() -> {
				resetDefaults();

				root[0] = content.get();
				frame[0] = new JFrame(title);
				frame[0].setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				// The root is placed in a holder that gives it exactly the requested (or preferred) size. Otherwise the
				// OS would stretch it, e.g. a packed JFrame on Windows is at least ~176px wide because of the title bar.
				JPanel holder = new JPanel(new FixedSizeLayout(size));
				holder.add(root[0]);
				frame[0].setContentPane(holder);
				frame[0].pack();
				if (ShowLayouts.enabled()) {
					frame[0].setLocationRelativeTo(null);
					frame[0].setVisible(true);
				}
				frame[0].validate();
				return null;
			});

			// wait outside the EDT, so the window is painted and can be looked at
			ShowLayouts.pause(title);

			return onEdt(() -> {
				GraphicsConfiguration gc = frame[0].getGraphicsConfiguration();
				double scale = gc.getDefaultTransform().getScaleX();
				TestPlatform.current().assertUnscaled(scale);
				return new Result(snapshot(root[0], scale), screenshot(root[0]));
			});
		} finally {
			if (frame[0] != null)
				onEdt(() -> {
					frame[0].dispose();
					return null;
				});
		}
	}

	/** Restores the global state that tests may have changed, so every layout starts like a fresh application. */
	private static void resetDefaults()
	{
		if (!(UIManager.getLookAndFeel() instanceof MetalLookAndFeel)) {
			try {
				UIManager.setLookAndFeel(new MetalLookAndFeel());
			} catch (UnsupportedLookAndFeelException e) {
				throw new IllegalStateException(e);
			}
		}
		UIManager.put("laf.scaleFactor", null);
		PlatformDefaults.setPlatform(PlatformDefaults.getCurrentPlatform());
		PlatformDefaults.setDefaultDPI(null);
		PlatformDefaults.setLogicalPixelBase(PlatformDefaults.BASE_SCALE_FACTOR);
		PlatformDefaults.setHorizontalScaleFactor(null);
		PlatformDefaults.setVerticalScaleFactor(null);
	}

	private static LayoutSnapshot snapshot(JComponent root, double scale)
	{
		LayoutSnapshot.Builder b = LayoutSnapshot.builder()
				.header("lookAndFeel", UIManager.getLookAndFeel().getName() + " (" + MetalLookAndFeel.getCurrentTheme().getName() + ")")
				.header("font", UIManager.getFont("Label.font"))
				.header("migPlatform", platformName(PlatformDefaults.getPlatform()))
				.header("screenDPI", root.getToolkit().getScreenResolution())
				.header("uiScale", scale)
				.container(root.getWidth(), root.getHeight());
		addChildren(b, root, root);
		return b.build();
	}

	private static void addChildren(LayoutSnapshot.Builder b, JComponent root, Container parent)
	{
		for (Component c : parent.getComponents()) {
			Point p = SwingUtilities.convertPoint(c.getParent(), c.getLocation(), root);
			b.component(c.getName(), c.getClass().getSimpleName(), p.x, p.y, c.getWidth(), c.getHeight());
			if (c instanceof Container && ((Container) c).getLayout() instanceof MigLayout)
				addChildren(b, root, (Container) c);
		}
	}

	private static Screenshot screenshot(JComponent root)
	{
		int w = Math.max(1, root.getWidth());
		int h = Math.max(1, root.getHeight());
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		try {
			root.printAll(g);
		} finally {
			g.dispose();
		}
		return new Screenshot(w, h, image.getRGB(0, 0, w, h, null, 0, w));
	}

	private static String platformName(int platform)
	{
		switch (platform) {
			case PlatformDefaults.WINDOWS_XP: return "WINDOWS_XP";
			case PlatformDefaults.MAC_OSX: return "MAC_OSX";
			case PlatformDefaults.GNOME: return "GNOME";
			default: return String.valueOf(platform);
		}
	}

	/** Places the single child at (0,0) with a fixed size, or its preferred size when no size is given. */
	private static final class FixedSizeLayout implements LayoutManager
	{
		private final Dimension size;

		FixedSizeLayout(Dimension size)
		{
			this.size = size;
		}

		private Dimension childSize(Container parent)
		{
			return size != null ? new Dimension(size) : parent.getComponent(0).getPreferredSize();
		}

		@Override
		public void layoutContainer(Container parent)
		{
			Dimension d = childSize(parent);
			parent.getComponent(0).setBounds(0, 0, d.width, d.height);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent)
		{
			return childSize(parent);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent)
		{
			return childSize(parent);
		}

		@Override
		public void addLayoutComponent(String name, Component comp)
		{
		}

		@Override
		public void removeLayoutComponent(Component comp)
		{
		}
	}

	/** Sets the name used in the snapshot and returns the component, for compact test code. */
	public static <T extends Component> T named(T component, String name)
	{
		component.setName(name);
		return component;
	}

	private static <T> T onEdt(Supplier<T> task)
	{
		if (SwingUtilities.isEventDispatchThread())
			return task.get();
		AtomicReference<T> result = new AtomicReference<>();
		try {
			SwingUtilities.invokeAndWait(() -> result.set(task.get()));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;
			if (cause instanceof Error)
				throw (Error) cause;
			throw new IllegalStateException(cause);
		}
		return result.get();
	}
}
