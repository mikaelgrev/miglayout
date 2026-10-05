package org.tbee.javafx.scene.layout.test.snapshot;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.opentest4j.AssertionFailedError;

/**
 * Compares a {@link LayoutSnapshot} with the golden file recorded for the current OS.
 * <ul>
 * <li>Golden files: {@code src/test/resources/layout-snapshots/<os>/<TestClass>/<case>.txt} (commit these).</li>
 * <li>Every run writes the actual snapshot, a screenshot and, on mismatch, a diff to
 *     {@code target/layout-snapshots/<os>/<TestClass>/} (uploaded as CI artifact).</li>
 * <li>A missing golden file is recorded and the test fails, so new baselines are always reviewed.</li>
 * <li>{@code -Dmiglayout.updateSnapshots=true} rewrites changed golden files (the test still fails if one changed).</li>
 * </ul>
 * Header lines ({@link LayoutSnapshot#HEADER_PREFIX}) are not compared.
 *
 * Shared snapshot infrastructure: keep identical in the swing and javafx modules (except the package).
 */
public final class SnapshotAssert
{
	public static final String UPDATE_PROPERTY = "miglayout.updateSnapshots";

	private final Path goldenRoot;
	private final Path outputRoot;
	private final boolean update;

	public SnapshotAssert(Path goldenRoot, Path outputRoot, boolean update)
	{
		this.goldenRoot = goldenRoot;
		this.outputRoot = outputRoot;
		this.update = update;
	}

	/** @return The instance for the module the tests run in (Surefire and IntelliJ use the module directory). */
	public static SnapshotAssert forCurrentModule()
	{
		Path baseDir = Paths.get(System.getProperty("basedir", System.getProperty("user.dir")));
		String os = TestPlatform.current().dirName();
		return new SnapshotAssert(
				baseDir.resolve("src/test/resources/layout-snapshots").resolve(os),
				baseDir.resolve("target/layout-snapshots").resolve(os),
				Boolean.getBoolean(UPDATE_PROPERTY));
	}

	public static void assertMatches(Class<?> testClass, String caseName, LayoutSnapshot actual, Screenshot screenshot)
	{
		forCurrentModule().check(testClass, caseName, actual, screenshot);
	}

	public void check(Class<?> testClass, String caseName, LayoutSnapshot actual, Screenshot screenshot)
	{
		String dir = testClass.getSimpleName();
		String file = sanitize(caseName);
		Path golden = goldenRoot.resolve(dir).resolve(file + ".txt");
		Path outDir = outputRoot.resolve(dir);

		write(outDir.resolve(file + ".txt"), actual.text().getBytes(StandardCharsets.UTF_8));
		if (screenshot != null)
			write(outDir.resolve(file + ".png"), screenshot.toPng());
		Path diffFile = outDir.resolve(file + ".diff.txt");
		delete(diffFile);

		if (!Files.exists(golden)) {
			write(golden, actual.text().getBytes(StandardCharsets.UTF_8));
			throw new AssertionFailedError("No layout snapshot existed, recorded a new one: " + golden.toAbsolutePath()
					+ "\nReview it (and the screenshot in " + outDir.toAbsolutePath() + ") and commit it.\n\n" + actual.text());
		}

		String expected = read(golden);
		List<String> expectedLines = significantLines(expected);
		List<String> actualLines = significantLines(actual.text());
		if (expectedLines.equals(actualLines))
			return;

		String diff = diff(expectedLines, actualLines);
		write(diffFile, diff.getBytes(StandardCharsets.UTF_8));
		if (update) {
			write(golden, actual.text().getBytes(StandardCharsets.UTF_8));
			throw new AssertionFailedError("Layout snapshot updated: " + golden.toAbsolutePath() + "\nReview the change and commit it.\n\n" + diff);
		}
		throw new AssertionFailedError("Layout differs from " + golden.toAbsolutePath()
				+ "\nIf the change is intended, rerun with -D" + UPDATE_PROPERTY + "=true (or delete the file) and commit it."
				+ "\nScreenshot: " + outDir.resolve(file + ".png").toAbsolutePath() + "\n\n" + diff,
				String.join("\n", expectedLines), String.join("\n", actualLines));
	}

	/** Removes header lines and line ending / trailing whitespace differences. */
	static List<String> significantLines(String text)
	{
		return text.lines()
				.map(String::stripTrailing)
				.filter(l -> !l.isEmpty() && !l.startsWith(LayoutSnapshot.HEADER_PREFIX))
				.collect(Collectors.toList());
	}

	/** A simple line based diff: lines are matched by their first token (the component name), otherwise by position. */
	static String diff(List<String> expected, List<String> actual)
	{
		StringBuilder sb = new StringBuilder("--- expected\n+++ actual\n");
		List<String> remainingActual = new ArrayList<>(actual);
		for (String e : expected) {
			String key = firstToken(e);
			String a = remainingActual.stream().filter(l -> firstToken(l).equals(key)).findFirst().orElse(null);
			if (a == null) {
				sb.append("- ").append(e).append('\n');
			} else {
				remainingActual.remove(a);
				if (a.equals(e))
					sb.append("  ").append(e).append('\n');
				else
					sb.append("- ").append(e).append('\n').append("+ ").append(a).append('\n');
			}
		}
		for (String a : remainingActual)
			sb.append("+ ").append(a).append('\n');
		return sb.toString();
	}

	private static String firstToken(String line)
	{
		int i = line.indexOf(' ');
		return i < 0 ? line : line.substring(0, i);
	}

	static String sanitize(String caseName)
	{
		return caseName.replaceAll("[^A-Za-z0-9._-]+", "_");
	}

	private static String read(Path p)
	{
		try {
			return Files.readString(p, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static void write(Path p, byte[] content)
	{
		try {
			Files.createDirectories(p.getParent());
			Files.write(p, content);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static void delete(Path p)
	{
		try {
			Files.deleteIfExists(p);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
