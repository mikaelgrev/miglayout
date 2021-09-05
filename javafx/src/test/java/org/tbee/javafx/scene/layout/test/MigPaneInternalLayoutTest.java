package org.tbee.javafx.scene.layout.test;

import java.util.List;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.PlatformDefaults;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * THESE TESTS ONLY RUN ON WINDOWS
 * ===============================
 *
 * TestFX is able to layout a single node per class.
 * Because we would be creating MigPane only once, this would result in one class with one test method per to-be-tested layout, and thus is a LOT of classes.
 * By placing MigPane in a presized Pane, it is possible to test different layouts each in a separate method, all in a single class.
 * The drawback is that MigPane is never tested stand alone, as the root node, so for each test it must be decided if we can put it in here, or if it needs a test class on its own.
 *
 * @author Tom Eugelink
 *
 */
public class MigPaneInternalLayoutTest extends org.testfx.framework.junit.ApplicationTest {
	
	@Override
	public void start(Stage stage) throws Exception {
		javafx.scene.text.Font.loadFont(this.getClass().getResource("/" + "Roboto-Medium.ttf").toExternalForm(), 12);
		
		this.stage = stage;
		Scene scene = new Scene(getRootNode());
        stage.setScene(scene);
        stage.show();
	}
	protected Stage stage;

	protected Parent getRootNode() {
		PlatformDefaults.setDefaultDPI(96);
		PlatformDefaults.setPlatform(PlatformDefaults.WINDOWS_XP);

		// use a pane to force the scene large enough, migpane is placed top-left
		pane = new Pane();
		pane.setMinSize(1000, 600);

		// just for readability; place a label
		label = new Label();
		label.setFont(new Font("Roboto Medium", 12));
		label.layoutYProperty().bind(pane.minHeightProperty().subtract(20));
		pane.getChildren().add(label);

		// done
		return pane;
	}
	private Pane pane = null;
	private Label label = null;

	@Before
	public void before() {
		Assume.assumeTrue(isUnix());
	}

	private boolean isUnix() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
	}

	@Test
	public void twoChildBasicLayout() {
		loadCSS();
		setLabel("twoChildBasicLayout");

		MigPane migPane = TestUtil.runThenWaitForPaintPulse( () -> {
			MigPane constructMigPane = new MigPane(new LC().debug(1000), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add nodes
	        constructMigPane.add(new TextField(), new CC());
	        constructMigPane.add(new Rectangle(30,30, Color.YELLOW), new CC());
	        return constructMigPane;
		});

		//generateSource(migPane);
		assertWH(migPane, 222.0, 44.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 10.0, 171.0, 24.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(185.0, 7.0, 30.0, 30.0, 0.01).assertClass(javafx.scene.shape.Rectangle.class);
	}
	
	@Test
	public void wrappingLabel() {
		loadCSS();
		setLabel("wrappingLabel");

		MigPane migPane = TestUtil.runThenWaitForPaintPulse( () -> {
			MigPane constructMigPane = new MigPane(new LC().width("400px").debug(1000), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add nodes
	        Label label = new Label("Test long label to see if the wrap works ok in a Migpane. I am going to have to keep writing because this may not be long enough yet!!");
			label.setFont(new Font("Roboto Medium", 12));
	        label.setWrapText(true);
	        constructMigPane.add(label, new CC().grow());
	        return constructMigPane;
		});

		//generateSource(migPane);
		assertWH(migPane, 400.0, 48.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 386.0, 34.0, 0.01).assertClass(javafx.scene.control.Label.class);
	}

	@Test
	public void size() {
		loadCSS();
		setLabel("size");

		MigPane migPane = TestUtil.runThenWaitForPaintPulse( () -> {
			MigPane constructMigPane = new MigPane(new LC().debug().fillX(), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add nodes
			constructMigPane.add(new Button("500 logical pixels (def)"), "w 500, wrap");
			constructMigPane.add(new Button("500 logical pixels"), "w 500lp, wrap");
			constructMigPane.add(new Button("500 pixels"), "w 500px, wrap");
			constructMigPane.add(new Button("10 centimeters"), "w 10cm, wrap");
			constructMigPane.add(new Button("4 inches"), "w 4in, wrap");
			constructMigPane.add(new Button("30% of screen"), "w 30sp, wrap");
			constructMigPane.add(new Button("30% of container"), "w 30%, wrap");
	        return constructMigPane;
		});

		// generateSource(migPane);
		assertWH(migPane, 1164.0, 231.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 495.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(7.0, 38.0, 495.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(2)).assertXYWH(7.0, 69.0, 500.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(3)).assertXYWH(7.0, 100.0, 374.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(4)).assertXYWH(7.0, 131.0, 380.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(5)).assertXYWH(7.0, 162.0, 1150.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(6)).assertXYWH(7.0, 193.0, 349.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);

	}

	@Test
	public void pack() {
		loadCSS();
		setLabel("pack");

		final Label label = new Label("Pack it up!");
		label.setFont(new Font("Roboto Medium", 12));
		final MigPane migPane = TestUtil.runThenWaitForPaintPulse( () -> {
			MigPane constructMigPane = new MigPane(new LC().pack().packAlign(0.5f, 1f), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add nodes
			constructMigPane.add(label, "alignx center, wrap unrel");
			Label wrapLabel = new Label("The only thing changed\nis the font size");
			wrapLabel.setFont(new Font("Roboto Medium", 12));
			wrapLabel.setTextAlignment(TextAlignment.CENTER);
			constructMigPane.add(wrapLabel, "alignx center");
	        return constructMigPane;
		});
		// generateSource(migPane);
		assertWH(migPane, 140.0, 76.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(42.0, 7.0, 57.0, 17.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(7.0, 35.0, 126.0, 34.0, 0.01).assertClass(javafx.scene.control.Label.class);

		// increase font size
		TestUtil.runThenWaitForPaintPulse( () -> {
			label.setFont(new Font("Roboto Medium", 50));
	        return null;
		});
		// generateSource(migPane);
		assertWH(migPane, 252.0, 129.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 238.0, 70.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(63.0, 88.0, 126.0, 34.0, 0.01).assertClass(javafx.scene.control.Label.class);

		// increase font size
		TestUtil.runThenWaitForPaintPulse( () -> {
			label.setFont(new Font("Roboto Medium", 100));
	        return null;
		});
		// generateSource(migPane);
		assertWH(migPane, 489.0, 198.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 475.0, 139.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(182.0, 157.0, 126.0, 34.0, 0.01).assertClass(javafx.scene.control.Label.class);
	}

	@Test
	public void wrap() {
		loadCSS();
		setLabel("wrap");

		MigPane migPane = TestUtil.runThenWaitForPaintPulse( () -> {
			MigPane constructMigPane = new MigPane(new LC().debug().fillX(), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add nodes
	        for (int i = 0; i < 10; i++)
	        {
	        	TextField lRectangle = new TextField();
		        CC lCC = new CC();
		        if ((i + 1) % 3 == 0) {
		        	lCC = lCC.growX().wrap(); // wrap every 3rd
		        }
		        constructMigPane.add(lRectangle, lCC);
	        }
	        return constructMigPane;
		});

		// generateSource(migPane);
		assertWH(migPane, 541.0, 131.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 171.0, 24.0, 0.01).assertClass(javafx.scene.control.TextField.class);
	}

	@Test
	public void external() {
		loadCSS();
		setLabel("external");

		MigPane migPane = TestUtil.runThenWaitForPaintPulse( () -> {
			MigPane constructMigPane = new MigPane(new LC().debug().fillX(), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add managed nodes
	        constructMigPane.add(new TextField(), new CC());
	        constructMigPane.add(new Rectangle(30,30, Color.RED), new CC());

			// add external (not unmanaged..) nodes
			Rectangle rectangle = new Rectangle(100, 50, 30, 30);
			constructMigPane.add(rectangle, new CC().external());
	        return constructMigPane;
		});

		// generateSource(migPane);
		assertWH(migPane, 222.0, 44.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 10.0, 171.0, 24.0, 0.01).assertClass(javafx.scene.control.TextField.class);
	}

	@Test
	public void defaultLayout() {
		loadCSS();
		setLabel("defaultLayout");

		MigPane migPane = TestUtil.runThenWaitForPaintPulse( () -> {
			MigPane constructMigPane = new MigPane(new LC().debug().fillX(), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add nodes
	        constructMigPane.add(new Label("First name"), "");
	        constructMigPane.add(new TextField(), "");
	        constructMigPane.add(new Label("Last name"), "gap unrelated");
	        constructMigPane.add(new TextField(), "wrap");
	        constructMigPane.add(new Label("Address"), "");
	        constructMigPane.add(new TextField(), "span, grow");
	        return constructMigPane;
		});

		// generateSource(migPane);
		assertWH(migPane, 527.0, 69.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 11.0, 70.0, 16.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(84.0, 7.0, 171.0, 24.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(2)).assertXYWH(273.0, 11.0, 69.0, 16.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(3)).assertXYWH(349.0, 7.0, 171.0, 24.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(4)).assertXYWH(7.0, 42.0, 53.0, 16.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(5)).assertXYWH(84.0, 38.0, 436.0, 24.0, 0.01).assertClass(javafx.scene.control.TextField.class);
	}

	@Test
	public void span() {
		loadCSS();
		setLabel("span");

		MigPane migPane = TestUtil.runThenWaitForPaintPulse( () -> {
			MigPane constructMigPane = new MigPane(new LC().debug().fillX(), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add nodes
	        for (int i = 0; i < 10; i++)
	        {
	        	constructMigPane.add(new Button("MMMMMMMMMMMMMMMMMMMMMMMMMMMM".substring(0, i + 1)), i < 9 ? new CC() : new CC().wrap());
	        }
	        for (int i = 1; i < 10; i++)
	        {
	        	constructMigPane.add(new Button("MMMMMMMMMMMMMMMMMMMMMMMMMMMM".substring(0, i + 1)), new CC().wrap().spanX());
	        }
	        return constructMigPane;
		});

		// generateSource(migPane);
		assertWH(migPane, 879.0, 324.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 30.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(44.0, 7.0, 41.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(2)).assertXYWH(92.0, 7.0, 52.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(3)).assertXYWH(151.0, 7.0, 63.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(4)).assertXYWH(221.0, 7.0, 75.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(5)).assertXYWH(303.0, 7.0, 86.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(6)).assertXYWH(396.0, 7.0, 97.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(7)).assertXYWH(500.0, 7.0, 108.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(8)).assertXYWH(615.0, 7.0, 119.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(9)).assertXYWH(741.0, 7.0, 131.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(10)).assertXYWH(7.0, 38.0, 41.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(11)).assertXYWH(7.0, 69.0, 52.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(12)).assertXYWH(7.0, 100.0, 63.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(13)).assertXYWH(7.0, 131.0, 75.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(14)).assertXYWH(7.0, 162.0, 86.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(15)).assertXYWH(7.0, 193.0, 97.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(16)).assertXYWH(7.0, 224.0, 108.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(17)).assertXYWH(7.0, 255.0, 119.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(18)).assertXYWH(7.0, 286.0, 131.0, 24.0, 0.01).assertClass(javafx.scene.control.Button.class);

	}

	// =============================================================================================================================================================================================================================
	// SUPPORT

	List<String> EXCLUDED_CLASSES = java.util.Arrays.asList(new String[]{"org.tbee.javafx.scene.layout.MigPane$DebugRectangle"});

	private void assertWH(MigPane migPane, double w, double h) {
		Assert.assertEquals(w, migPane.getWidth(), 0.01);
		Assert.assertEquals(h, migPane.getHeight(), 0.01);
	}

	private void setLabel(String s) {
		TestUtil.runAndWait( () -> {
			label.setText(s);
		});
	}

	private void generateSource(Pane pane) {
		System.out.println(label.getText());
		System.out.println("assertWH(migPane, " + pane.getWidth() + ", " + pane.getHeight() + ");");
		AssertNode.generateSource("migPane", pane.getChildren(), EXCLUDED_CLASSES, false, AssertNode.A.XYWH, AssertNode.A.CLASS);
		//TestUtil.sleep(3000);
	}

	private void loadCSS() {
		TestUtil.runThenWaitForPaintPulse( () -> {
			stage.getScene().getStylesheets().addAll(this.getClass().getResource("MigPaneInternalLayoutTest.css").toExternalForm());
		});
	}
}
