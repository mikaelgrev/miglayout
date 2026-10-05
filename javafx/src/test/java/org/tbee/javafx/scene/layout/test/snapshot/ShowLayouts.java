package org.tbee.javafx.scene.layout.test.snapshot;

/**
 * Optionally shows every laid out window on screen for a while, so a developer can watch the layouts.
 * Enable with {@code -Dmiglayout.showTest=<milliseconds>}, e.g. {@code -Dmiglayout.showTest=2000};
 * off (0) by default, so Maven and CI runs are not slowed down. The snapshot is taken after the pause.
 *
 * Shared snapshot infrastructure: keep identical in the swing and javafx modules (except the package).
 */
public final class ShowLayouts
{
	public static final String PROPERTY = "miglayout.showTest";

	private ShowLayouts()
	{
	}

	/** @return The time each layout is shown, 0 when disabled. */
	public static long millis()
	{
		String value = System.getProperty(PROPERTY, "").trim();
		if (value.isEmpty())
			return 0;
		try {
			return Math.max(0, Long.parseLong(value));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("-D" + PROPERTY + " must be a number of milliseconds, got: " + value, e);
		}
	}

	public static boolean enabled()
	{
		return millis() > 0;
	}

	/** Waits while the window is shown; must not be called on the UI thread, otherwise nothing is painted. */
	static void pause(String title)
	{
		long millis = millis();
		if (millis <= 0)
			return;
		System.out.println("Showing layout for " + millis + " ms: " + title);
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
