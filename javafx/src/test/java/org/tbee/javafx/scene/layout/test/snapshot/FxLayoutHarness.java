package org.tbee.javafx.scene.layout.test.snapshot;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.miginfocom.layout.PlatformDefaults;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * Lays out a JavaFX container the way an application would (in a shown {@link Stage}, default Modena skin, MigLayout
 * platform defaults of the real OS) and captures a {@link LayoutSnapshot} and {@link Screenshot}.
 *
 * <pre>
 * FxLayoutHarness.assertLayout(getClass(), "preferred", () -> {
 *     MigPane pane = new MigPane();
 *     pane.add(named(new Button("OK"), "ok"));
 *     return pane;
 * }, null);
 * </pre>
 *
 * Every node that should appear in the snapshot needs an id ({@link Node#setId}). Children of nested
 * {@link MigPane}s are included as well, with coordinates relative to the root container.
 */
public final class FxLayoutHarness
{
	private static final long TIMEOUT_SECONDS = 30;
	private static boolean started = false;

	private FxLayoutHarness()
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
	public static void assertLayout(Class<?> testClass, String caseName, Supplier<? extends Parent> content, Dimension2D size)
	{
		Result result = layout(content, size);
		SnapshotAssert.assertMatches(testClass, caseName, result.snapshot(), result.screenshot());
	}

	/**
	 * @param content Creates the container to lay out; called on the JavaFX application thread.
	 * @param size The size of the container, or null to use its preferred size (like {@link Stage#sizeToScene()}).
	 */
	public static Result layout(Supplier<? extends Parent> content, Dimension2D size)
	{
		startToolkit();
		return onFx(() -> {
			resetDefaults();

			Parent root = content.get();
			// The root is placed in a holder that gives it exactly the requested (or preferred) size, otherwise
			// the OS may enforce a minimum window size and stretch it.
			Holder holder = new Holder(root, size);
			Stage stage = new Stage();
			try {
				stage.setTitle("layout snapshot");
				stage.setScene(new Scene(holder));
				stage.sizeToScene();
				stage.show();
				holder.applyCss();
				holder.layout();

				double scale = Screen.getPrimary().getOutputScaleX();
				TestPlatform.current().assertUnscaled(scale);

				return new Result(snapshot(root, scale), screenshot(root));
			} finally {
				stage.hide();
			}
		});
	}

	/** Restores the global state that tests may have changed, so every layout starts like a fresh application. */
	private static void resetDefaults()
	{
		PlatformDefaults.setPlatform(PlatformDefaults.getCurrentPlatform());
		PlatformDefaults.setDefaultDPI(null);
		PlatformDefaults.setLogicalPixelBase(PlatformDefaults.BASE_SCALE_FACTOR);
		PlatformDefaults.setHorizontalScaleFactor(null);
		PlatformDefaults.setVerticalScaleFactor(null);
	}

	private static LayoutSnapshot snapshot(Parent root, double scale)
	{
		Bounds rootBounds = root.getLayoutBounds();
		LayoutSnapshot.Builder b = LayoutSnapshot.builder()
				.header("javafx", System.getProperty("javafx.runtime.version"))
				.header("font", Font.getDefault())
				.header("migPlatform", platformName(PlatformDefaults.getPlatform()))
				.header("screenDPI", Screen.getPrimary().getDpi())
				.header("uiScale", scale)
				.container(rootBounds.getWidth(), rootBounds.getHeight());
		addChildren(b, root, root);
		return b.build();
	}

	private static void addChildren(LayoutSnapshot.Builder b, Parent root, Parent parent)
	{
		for (Node n : parent.getChildrenUnmodifiable()) {
			if (isDebugOverlay(n))
				continue;
			Bounds layout = n.getLayoutBounds();
			Bounds inRoot = root.sceneToLocal(n.localToScene(layout));
			b.component(n.getId(), n.getClass().getSimpleName(), inRoot.getMinX(), inRoot.getMinY(), layout.getWidth(), layout.getHeight());
			if (n instanceof MigPane)
				addChildren(b, root, (Parent) n);
		}
	}

	/** MigPane adds its own rectangles ({@code MigPane.DebugRectangle}) in debug mode; they are not part of the layout. */
	private static boolean isDebugOverlay(Node n)
	{
		return n.getClass().getName().equals(MigPane.class.getName() + "$DebugRectangle");
	}

	private static Screenshot screenshot(Parent root)
	{
		WritableImage image = root.snapshot(new SnapshotParameters(), null);
		int w = (int) image.getWidth();
		int h = (int) image.getHeight();
		int[] argb = new int[w * h];
		image.getPixelReader().getPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), argb, 0, w);
		return new Screenshot(w, h, argb);
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

	/** Sets the id used in the snapshot and returns the node, for compact test code. */
	public static <T extends Node> T named(T node, String id)
	{
		node.setId(id);
		return node;
	}

	/** Places the single child at (0,0) with a fixed size, or its preferred size when no size is given. */
	private static final class Holder extends Region
	{
		private final Parent child;
		private final Dimension2D size;

		Holder(Parent child, Dimension2D size)
		{
			this.child = child;
			this.size = size;
			getChildren().add(child);
		}

		private double childWidth()
		{
			return size != null ? size.getWidth() : child.prefWidth(-1);
		}

		private double childHeight()
		{
			return size != null ? size.getHeight() : child.prefHeight(childWidth());
		}

		@Override
		protected void layoutChildren()
		{
			child.resizeRelocate(0, 0, childWidth(), childHeight());
		}

		@Override
		protected double computePrefWidth(double height)
		{
			return childWidth();
		}

		@Override
		protected double computePrefHeight(double width)
		{
			return childHeight();
		}
	}

	private static synchronized void startToolkit()
	{
		if (started)
			return;
		try {
			Platform.startup(() -> {});
		} catch (IllegalStateException alreadyStarted) {
			// fine, e.g. started by another test framework
		}
		Platform.setImplicitExit(false);
		started = true;
	}

	/** Runs the task on the JavaFX application thread and waits for the result. */
	public static <T> T onFx(Supplier<T> task)
	{
		if (Platform.isFxApplicationThread())
			return task.get();
		CompletableFuture<T> future = new CompletableFuture<>();
		Platform.runLater(() -> {
			try {
				future.complete(task.get());
			} catch (Throwable t) {
				future.completeExceptionally(t);
			}
		});
		try {
			return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException(e);
		} catch (TimeoutException e) {
			throw new IllegalStateException("JavaFX task did not finish within " + TIMEOUT_SECONDS + "s", e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;
			if (cause instanceof Error)
				throw (Error) cause;
			throw new IllegalStateException(cause);
		}
	}
}
