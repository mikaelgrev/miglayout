package org.tbee.javafx.scene.layout.test.snapshot;

import java.util.Locale;

/**
 * The real operating system the tests run on. Layout snapshots are recorded per OS, because fonts, look and feel
 * metrics and MigLayout's platform defaults (gaps, insets, button order) differ per OS.
 *
 * Shared snapshot infrastructure: keep identical in the swing and javafx modules (except the package).
 */
public enum TestPlatform
{
	WINDOWS, LINUX, MACOS;

	public static TestPlatform current()
	{
		String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
		if (os.startsWith("windows"))
			return WINDOWS;
		if (os.startsWith("mac"))
			return MACOS;
		if (os.contains("linux") || os.contains("nix") || os.contains("nux"))
			return LINUX;
		throw new IllegalStateException("Unsupported OS for layout snapshot tests: " + os);
	}

	/** @return The directory name used for the snapshots of this platform. */
	public String dirName()
	{
		return name().toLowerCase(Locale.ROOT);
	}

	/**
	 * Verifies that the UI is rendered at 100% scaling, otherwise the recorded positions are not comparable between
	 * machines. Surefire forces this with -Dsun.java2d.uiScale=1 / -Dglass.*.uiScale=1. macOS ignores these on
	 * Retina screens, but there all positions are logical (unscaled) anyway; only the screenshot is larger.
	 *
	 * @param scale The effective scale reported by the UI toolkit.
	 */
	public void assertUnscaled(double scale)
	{
		if (this != MACOS && Math.abs(scale - 1.0) > 0.001)
			throw new IllegalStateException("UI scale is " + scale + " but must be 1 for layout snapshot tests. "
					+ "Run with: -Dsun.java2d.uiScale=1 -Dglass.win.uiScale=1 -Dglass.gtk.uiScale=1 "
					+ "(Maven does this automatically; in IntelliJ re-import the Maven project or add them to the run configuration)");
	}
}
