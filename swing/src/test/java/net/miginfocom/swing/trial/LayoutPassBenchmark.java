package net.miginfocom.swing.trial;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Compares what one layout pass costs with two or more builds of MigLayout, side by side in one JVM.
 * <p>
 * Each build is loaded by its own class loader and lays out its own copy of the same form, and the builds take
 * turns in random order, so that a machine getting slower or faster during the run affects all of them alike.
 * The form is resized on every pass, which is what dragging a window does.
 * <p>
 * Run it with the classpath of each build, the first one being the build to compare against:
 * <pre>
 * java LayoutPassBenchmark.java before=old/miglayout-core.jar:old/miglayout-swing.jar after=new/miglayout-core.jar:new/miglayout-swing.jar
 * </pre>
 * It needs a display, because a container that is not in a realized window never becomes valid and takes a path
 * that no application takes. <code>-Dbench.lafScaleFactor=1</code> publishes "laf.scaleFactor" like FlatLaf does.
 */
public class LayoutPassBenchmark
{
	private static final int ROUNDS = Integer.getInteger("bench.rounds", 40);
	private static final int PASSES_PER_ROUND = 500;
	private static final int WARM_UP_PASSES = 6000;

	private static boolean counting = false;
	private static int baselineCalls = 0;
	private static int scaleFactorLookups = 0;

	public static void main(String[] args) throws Exception
	{
		if (args.length < 2)
			throw new IllegalArgumentException("Give at least two builds as name=classpath, the first one to compare against.");

		UIManager.setLookAndFeel(new CountingMetal());
		final List<Build> builds = new ArrayList<Build>();
		for (String arg : args)
			builds.add(new Build(arg));

		final JFrame frame = new JFrame("LayoutPassBenchmark");
		SwingUtilities.invokeAndWait(() -> {
			JPanel holder = new JPanel(null);
			for (Build b : builds)
				holder.add(b.form);
			frame.setContentPane(holder);
			frame.setSize(1400, 900);
			frame.setVisible(true);
		});
		Thread.sleep(500);
		SwingUtilities.invokeAndWait(() -> measure(builds));
		frame.dispose();
		print(builds);
		System.exit(0);
	}

	private static void measure(List<Build> builds)
	{
		for (int i = 0; i < WARM_UP_PASSES; i++)
			for (Build b : builds)
				b.pass(widthOfPass(i));

		for (Build b : builds) {
			counting = true;
			baselineCalls = 0;
			scaleFactorLookups = 0;
			b.pass(1111);
			counting = false;
			b.baselineCalls = baselineCalls;
			b.scaleFactorLookups = scaleFactorLookups;
		}

		for (int i = 0; i < 250; i++) {
			int width = widthOfPass(i);
			for (Build b : builds) {
				b.pass(width);
				b.layouts.append(boundsOf(b.form));
			}
		}

		List<Build> order = new ArrayList<Build>(builds);
		Random random = new Random(42);
		for (int r = 0; r < ROUNDS; r++) {
			Collections.shuffle(order, random);
			for (Build b : order) {
				long[] nanos = new long[PASSES_PER_ROUND];
				for (int i = 0; i < nanos.length; i++)
					nanos[i] = b.pass(widthOfPass(i));
				Arrays.sort(nanos);
				b.medians.add(nanos[nanos.length / 2]);
			}
		}
	}

	private static void print(List<Build> builds)
	{
		Build reference = builds.get(0);
		System.out.printf("%nOne layout pass of a %d component form, Metal, laf.scaleFactor %s, %d rounds of %d passes%n%n",
				countComponents(reference.form), System.getProperty("bench.lafScaleFactor", "not published"), ROUNDS, PASSES_PER_ROUND);
		System.out.printf("%-10s %12s %22s %16s %10s %14s%n", "build", "getBaseline", "laf.scaleFactor reads", "median us/pass", "change", "faster rounds");
		for (Build b : builds) {
			List<Double> ratios = new ArrayList<Double>();
			int faster = 0;
			for (int r = 0; r < ROUNDS; r++) {
				double ratio = (double) b.medians.get(r) / reference.medians.get(r);
				ratios.add(ratio);
				if (ratio < 1)
					faster++;
			}
			boolean isReference = b == reference;
			System.out.printf("%-10s %12d %22d %16.1f %10s %14s%n", b.name, b.baselineCalls, b.scaleFactorLookups, median(b.medians) / 1000.0,
					isReference ? "" : String.format("%+.1f%%", (median(ratios) - 1) * 100), isReference ? "" : faster + "/" + ROUNDS);
		}
		System.out.println();
		for (Build b : builds.subList(1, builds.size()))
			System.out.println("Bounds of every component at 250 widths, " + b.name + " vs " + reference.name + ": "
					+ (b.layouts.toString().equals(reference.layouts.toString()) ? "identical" : "DIFFERENT"));
	}

	private static int widthOfPass(int i)
	{
		return 900 + (i % 250);
	}

	private static <N extends Number> double median(List<N> values)
	{
		double[] sorted = new double[values.size()];
		for (int i = 0; i < sorted.length; i++)
			sorted[i] = values.get(i).doubleValue();
		Arrays.sort(sorted);
		return sorted[sorted.length / 2];
	}

	private static String boundsOf(Component c)
	{
		StringBuilder sb = new StringBuilder(c.getBounds().toString());
		if (c instanceof Container)
			for (Component child : ((Container) c).getComponents())
				sb.append(boundsOf(child));
		return sb.toString();
	}

	private static int countComponents(Container c)
	{
		int total = c.getComponentCount();
		for (Component child : c.getComponents())
			if (child instanceof Container)
				total += countComponents((Container) child);
		return total;
	}

	/** One build of MigLayout, and the form it lays out. */
	private static final class Build
	{
		final String name;
		final JPanel form;
		final List<Long> medians = new ArrayList<Long>();
		final StringBuilder layouts = new StringBuilder();
		int baselineCalls;
		int scaleFactorLookups;
		private final ClassLoader loader;

		Build(String nameAndClasspath) throws Exception
		{
			int eq = nameAndClasspath.indexOf('=');
			name = eq < 0 ? "build " + nameAndClasspath.hashCode() : nameAndClasspath.substring(0, eq);
			String[] entries = nameAndClasspath.substring(eq + 1).split(File.pathSeparator);
			URL[] urls = new URL[entries.length];
			for (int i = 0; i < entries.length; i++)
				urls[i] = new File(entries[i]).toURI().toURL();
			loader = new URLClassLoader(urls, ClassLoader.getPlatformClassLoader());   // Never the MigLayout of the benchmark's own classpath.
			form = buildForm();
		}

		private LayoutManager migLayout(String layoutConstraints, String colConstraints, String rowConstraints) throws Exception
		{
			return (LayoutManager) Class.forName("net.miginfocom.swing.MigLayout", true, loader)
					.getConstructor(String.class, String.class, String.class)
					.newInstance(layoutConstraints, colConstraints, rowConstraints);
		}

		/** A form of the shape most applications have: labelled fields in a grid, a row of buttons and a few nested
		 * panels. Every size uses the default unit, because writing "px" switches off the scaling this is about. */
		private JPanel buildForm() throws Exception
		{
			JPanel root = new JPanel(migLayout("wrap 4", "[right][grow,fill][right][grow,fill]", ""));
			String[] items = {"alpha", "beta", "gamma", "delta"};
			for (int i = 0; i < 10; i++) {
				root.add(new Label("Field " + i + ":"));
				root.add(new TextField("value " + i, 12), "growx");
				root.add(new Label("Choice " + i + ":"));
				root.add(new ComboBox(items), "growx");
			}
			for (int i = 0; i < 4; i++)
				root.add(new Button("Action " + i));
			for (int n = 0; n < 3; n++) {
				JPanel nested = new JPanel(migLayout("insets 4", "[][grow,fill]", ""));
				for (int i = 0; i < 3; i++) {
					nested.add(new Label("Nested " + n + "." + i));
					nested.add(new TextField("n" + i, 8), "growx, wrap");
				}
				root.add(nested, "span 4, growx");
			}
			return root;
		}

		long pass(int width)
		{
			form.setBounds(0, 0, width, 700);
			long start = System.nanoTime();
			form.validate();
			return System.nanoTime() - start;
		}
	}

	private static int counted(int baseline)
	{
		if (counting)
			baselineCalls++;
		return baseline;
	}

	private static class Label extends JLabel
	{
		Label(String text) { super(text); }
		@Override public int getBaseline(int w, int h) { return counted(super.getBaseline(w, h)); }
	}

	private static class Button extends JButton
	{
		Button(String text) { super(text); }
		@Override public int getBaseline(int w, int h) { return counted(super.getBaseline(w, h)); }
	}

	private static class TextField extends JTextField
	{
		TextField(String text, int columns) { super(text, columns); }
		@Override public int getBaseline(int w, int h) { return counted(super.getBaseline(w, h)); }
	}

	private static class ComboBox extends JComboBox<String>
	{
		ComboBox(String[] items) { super(items); }
		@Override public int getBaseline(int w, int h) { return counted(super.getBaseline(w, h)); }
	}

	/** Metal, with a defaults table that counts how often "laf.scaleFactor" is looked up. */
	private static class CountingMetal extends MetalLookAndFeel
	{
		private UIDefaults defaults;

		@Override
		public UIDefaults getDefaults()
		{
			if (defaults == null) {
				defaults = new UIDefaults() {
					@Override
					public Object get(Object key)
					{
						if (counting && "laf.scaleFactor".equals(key))
							scaleFactorLookups++;
						return super.get(key);
					}
				};
				defaults.putAll(super.getDefaults());
				String published = System.getProperty("bench.lafScaleFactor");
				if (published != null)
					defaults.put("laf.scaleFactor", Float.valueOf(published));
			}
			return defaults;
		}
	}
}
