package org.tbee.javafx.scene.layout.test.snapshot;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;

/**
 * An ARGB image of a laid out container, written as PNG for visual review. Screenshots are never compared, because
 * font anti-aliasing differs too much between machines.
 *
 * Contains its own minimal PNG encoder, because the javafx module does not read java.desktop (ImageIO).
 *
 * Shared snapshot infrastructure: keep identical in the swing and javafx modules (except the package).
 */
public final class Screenshot
{
	private final int width;
	private final int height;
	private final int[] argb;

	/** @param argb The pixels, row by row, in 0xAARRGGBB format. */
	public Screenshot(int width, int height, int[] argb)
	{
		if (argb.length != width * height)
			throw new IllegalArgumentException("Expected " + (width * height) + " pixels, got " + argb.length);
		this.width = width;
		this.height = height;
		this.argb = argb;
	}

	public int width()
	{
		return width;
	}

	public int height()
	{
		return height;
	}

	public byte[] toPng()
	{
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(new byte[] {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'});

			ByteArrayOutputStream ihdr = new ByteArrayOutputStream();
			DataOutputStream d = new DataOutputStream(ihdr);
			d.writeInt(width);
			d.writeInt(height);
			d.writeByte(8);   // bit depth
			d.writeByte(6);   // color type RGBA
			d.writeByte(0);   // compression
			d.writeByte(0);   // filter
			d.writeByte(0);   // interlace
			writeChunk(out, "IHDR", ihdr.toByteArray());

			ByteArrayOutputStream raw = new ByteArrayOutputStream();
			try (DeflaterOutputStream z = new DeflaterOutputStream(raw)) {
				byte[] row = new byte[1 + width * 4];
				for (int y = 0; y < height; y++) {
					row[0] = 0; // filter: none
					for (int x = 0; x < width; x++) {
						int p = argb[y * width + x];
						int i = 1 + x * 4;
						row[i] = (byte) (p >> 16);
						row[i + 1] = (byte) (p >> 8);
						row[i + 2] = (byte) p;
						row[i + 3] = (byte) (p >>> 24);
					}
					z.write(row);
				}
			}
			writeChunk(out, "IDAT", raw.toByteArray());
			writeChunk(out, "IEND", new byte[0]);
			return out.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException(e); // cannot happen for in-memory streams
		}
	}

	private static void writeChunk(ByteArrayOutputStream out, String type, byte[] data) throws IOException
	{
		DataOutputStream d = new DataOutputStream(out);
		byte[] typeBytes = type.getBytes(StandardCharsets.US_ASCII);
		d.writeInt(data.length);
		d.write(typeBytes);
		d.write(data);
		CRC32 crc = new CRC32();
		crc.update(typeBytes);
		crc.update(data);
		d.writeInt((int) crc.getValue());
	}
}
