package net.miginfocom.swing.layout.snapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A plain text, diff friendly description of a laid out container: a header with environment information (not
 * compared), the container size and the bounds of every component.
 *
 * <pre>
 * # os: windows
 * # lookAndFeel: Metal
 * container 230 98
 * nameLabel     JLabel      7   7  38  16
 * nameField     JTextField 52   7 171  20
 * </pre>
 *
 * Shared snapshot infrastructure: keep identical in the swing and javafx modules (except the package).
 */
public final class LayoutSnapshot
{
	/** Lines starting with this prefix are informational only and ignored when comparing snapshots. */
	public static final String HEADER_PREFIX = "#";

	private final String text;

	private LayoutSnapshot(String text)
	{
		this.text = text;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	/** @return The full snapshot text, including the header. Lines end with '\n'. */
	public String text()
	{
		return text;
	}

	@Override
	public String toString()
	{
		return text;
	}

	public static final class Builder
	{
		private final Map<String, String> header = new LinkedHashMap<>();
		private String container;
		private final List<String[]> components = new ArrayList<>();

		private Builder()
		{
			header.put("os", TestPlatform.current().dirName());
			header.put("env", TestPlatform.current().environmentName());
			header.put("os.version", System.getProperty("os.name") + " " + System.getProperty("os.version"));
			header.put("java", System.getProperty("java.vendor") + " " + System.getProperty("java.version"));
		}

		/** Adds informational environment data (not compared). */
		public Builder header(String key, Object value)
		{
			header.put(key, String.valueOf(value));
			return this;
		}

		public Builder container(double width, double height)
		{
			container = "container " + format(width) + " " + format(height);
			return this;
		}

		/**
		 * @param name A stable, unique name of the component (Swing: {@code setName}, JavaFX: {@code setId}).
		 * @param type The component type, e.g. "JButton".
		 */
		public Builder component(String name, String type, double x, double y, double width, double height)
		{
			if (name == null || name.isEmpty())
				throw new IllegalArgumentException("Every component in a layout snapshot needs a name (" + type + " at " + format(x) + "," + format(y) + ")");
			components.add(new String[] {name, type, format(x), format(y), format(width), format(height)});
			return this;
		}

		public LayoutSnapshot build()
		{
			if (container == null)
				throw new IllegalStateException("container size not set");

			StringBuilder sb = new StringBuilder();
			header.forEach((k, v) -> sb.append(HEADER_PREFIX).append(' ').append(k).append(": ").append(v).append('\n'));
			sb.append(container).append('\n');

			// align the columns for readability
			int[] widths = new int[6];
			for (String[] c : components)
				for (int i = 0; i < c.length; i++)
					widths[i] = Math.max(widths[i], c[i].length());
			for (String[] c : components) {
				StringBuilder line = new StringBuilder();
				for (int i = 0; i < c.length; i++) {
					if (i > 0)
						line.append(' ');
					String pad = " ".repeat(widths[i] - c[i].length());
					line.append(i < 2 ? c[i] + pad : pad + c[i]); // text left, numbers right aligned
				}
				sb.append(line.toString().stripTrailing()).append('\n');
			}
			return new LayoutSnapshot(sb.toString());
		}
	}

	/** Formats with at most 2 decimals and without trailing zeros, independent of the locale: 7, 7.5, 7.33 */
	static String format(double v)
	{
		BigDecimal bd = BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
		if (bd.signum() == 0)
			return "0";
		return bd.scale() < 0 ? bd.setScale(0).toPlainString() : bd.toPlainString();
	}
}
