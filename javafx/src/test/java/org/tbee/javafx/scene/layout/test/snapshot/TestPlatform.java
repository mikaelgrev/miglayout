package org.tbee.javafx.scene.layout.test.snapshot;

import java.util.Locale;

/**
 * The real operating system the tests run on. Layout snapshots are recorded per OS, because fonts, look and feel
 * metrics and MigLayout's platform defaults (gaps, insets, button order) differ per OS, and per developer machine,
 * see {@link #environmentName()}.
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

	/** Overrides the snapshot environment name, e.g. {@code -Dmiglayout.snapshotEnv=windows} to compare against the CI snapshots locally. */
	public static final String ENV_PROPERTY = "miglayout.snapshotEnv";

	/**
	 * The name of the environment the golden snapshots belong to, used as directory name:
	 * <ul>
	 * <li>On CI (GitHub Actions or another CI that sets {@code CI}): the OS, e.g. {@code windows}. These are the
	 *     reference snapshots, recorded on the pinned CI images.</li>
	 * <li>On a developer machine: OS and computer name, e.g. {@code windows-raelee}, because fonts, JDK, JavaFX
	 *     version and screen DPI differ from CI. These let a developer test locally before and after a change.</li>
	 * <li>{@code -Dmiglayout.snapshotEnv=<name>} overrides both.</li>
	 * </ul>
	 */
	public String environmentName()
	{
		String override = System.getProperty(ENV_PROPERTY, "").trim();
		if (!override.isEmpty())
			return sanitize(override);
		if (isCi())
			return dirName();
		return dirName() + "-" + sanitize(hostName());
	}

	public static boolean isCi()
	{
		return System.getenv("GITHUB_ACTIONS") != null || System.getenv("CI") != null;
	}

	private static String hostName()
	{
		String name = System.getenv("COMPUTERNAME"); // Windows
		if (name == null || name.isBlank())
			name = System.getenv("HOSTNAME"); // most Unix shells
		if (name == null || name.isBlank()) {
			try {
				name = java.net.InetAddress.getLocalHost().getHostName();
			} catch (java.net.UnknownHostException e) {
				name = "local";
			}
		}
		int dot = name.indexOf('.'); // macOS: "name.local"
		return dot > 0 ? name.substring(0, dot) : name;
	}

	private static String sanitize(String name)
	{
		return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]+", "_");
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
