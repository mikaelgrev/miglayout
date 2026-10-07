package net.miginfocom.swing.layout.snapshot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests the snapshot infrastructure itself (no UI needed). */
class SnapshotAssertTest
{
	@TempDir
	Path tmp;

	private Path golden() { return tmp.resolve("golden/SnapshotAssertTest/case_1.txt"); }
	private Path out(String ext) { return tmp.resolve("out/SnapshotAssertTest/case_1" + ext); }

	private SnapshotAssert snapshotAssert(boolean update)
	{
		return snapshotAssert(update, true);
	}

	private SnapshotAssert snapshotAssert(boolean update, boolean record)
	{
		return new SnapshotAssert(tmp.resolve("golden"), tmp.resolve("out"), update, record);
	}

	private static LayoutSnapshot snapshot(int buttonX)
	{
		return LayoutSnapshot.builder()
				.container(100, 50)
				.component("label", "JLabel", 7, 7, 30, 16)
				.component("button", "JButton", buttonX, 7, 60.5, 23)
				.build();
	}

	@Test
	void formatsAlignedColumnsWithoutLocaleDependentDecimals()
	{
		String text = snapshot(40).text();
		assertTrue(text.contains("\ncontainer 100 50\nlabel  JLabel   7 7   30 16\nbutton JButton 40 7 60.5 23\n"), text);
		assertTrue(text.startsWith("# os: "), text);
		assertEquals("0", LayoutSnapshot.format(-0.0));
		assertEquals("7.33", LayoutSnapshot.format(7.333));
		assertEquals("100", LayoutSnapshot.format(100.0));
	}

	@Test
	void aMissingGoldenFileIsSkippedByDefault()
	{
		snapshotAssert(false, false).check(SnapshotAssertTest.class, "case 1", snapshot(40), null);
		assertFalse(Files.exists(golden()));
		assertTrue(Files.exists(out(".txt")));
	}

	@Test
	void aMissingGoldenFileFailsWhenRecording()
	{
		AssertionFailedError e = assertThrows(AssertionFailedError.class,
				() -> snapshotAssert(false, true).check(SnapshotAssertTest.class, "case 1", snapshot(40), null));
		assertTrue(e.getMessage().contains("recorded a new one"), e.getMessage());
		assertTrue(Files.exists(golden()));
		assertTrue(Files.exists(out(".txt")));
	}

	@Test
	void headerAndLineEndingsAreIgnored() throws IOException
	{
		Files.createDirectories(golden().getParent());
		String other = "# os: some other machine\r\n" + snapshot(40).text().lines()
				.filter(l -> !l.startsWith("#")).reduce("", (a, l) -> a + l + "  \r\n");
		Files.writeString(golden(), other);

		snapshotAssert(false).check(SnapshotAssertTest.class, "case 1", snapshot(40), null);
		assertFalse(Files.exists(out(".diff.txt")));
	}

	@Test
	void aDifferenceFailsWithADiffAndKeepsTheGoldenFile() throws IOException
	{
		Files.createDirectories(golden().getParent());
		Files.writeString(golden(), snapshot(40).text());

		AssertionFailedError e = assertThrows(AssertionFailedError.class,
				() -> snapshotAssert(false).check(SnapshotAssertTest.class, "case 1", snapshot(41), null));
		assertTrue(e.getMessage().contains("- button JButton 40 7 60.5 23\n+ button JButton 41 7 60.5 23"), e.getMessage());
		assertTrue(e.getMessage().contains("  label  JLabel   7 7   30 16"), e.getMessage());
		assertTrue(Files.exists(out(".diff.txt")));
		assertEquals(snapshot(40).text(), Files.readString(golden()));
	}

	@Test
	void updateRewritesAChangedGoldenFileButStillFails() throws IOException
	{
		Files.createDirectories(golden().getParent());
		Files.writeString(golden(), snapshot(40).text());

		assertThrows(AssertionFailedError.class,
				() -> snapshotAssert(true).check(SnapshotAssertTest.class, "case 1", snapshot(41), null));
		assertEquals(snapshot(41).text(), Files.readString(golden(), StandardCharsets.UTF_8));
		snapshotAssert(true).check(SnapshotAssertTest.class, "case 1", snapshot(41), null);
	}

	@Test
	void writesAValidPngScreenshot() throws IOException
	{
		int[] pixels = {0xFFFF0000, 0x8000FF00, 0xFF0000FF, 0x00000000, 0xFFFFFFFF, 0xFF123456};
		assertThrows(AssertionFailedError.class, // fails because the golden file is recorded, but writes the screenshot
				() -> snapshotAssert(false).check(SnapshotAssertTest.class, "case 1", snapshot(40), new Screenshot(3, 2, pixels)));

		BufferedImage image = ImageIO.read(new ByteArrayInputStream(Files.readAllBytes(out(".png"))));
		assertEquals(3, image.getWidth());
		assertEquals(2, image.getHeight());
		for (int i = 0; i < pixels.length; i++)
			assertEquals(pixels[i], image.getRGB(i % 3, i / 3), "pixel " + i);
	}
}
