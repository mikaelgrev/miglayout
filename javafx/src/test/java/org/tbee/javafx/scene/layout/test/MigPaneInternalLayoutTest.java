package org.tbee.javafx.scene.layout.test;

import java.util.List;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import jfxtras.test.AssertNode;
import jfxtras.test.TestUtil;
import jfxtras.util.PlatformUtil;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.PlatformDefaults;

import org.junit.Assert;
import org.junit.Test;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * TestFX is able to layout a single node per class.
 * Because we would be creating MigPane only once, this would result in one class with one test method per to-be-tested layout, and thus is a LOT of classes.
 * By placing MigPane in a presized Pane, it is possible to test different layouts each in a separate method, all in a single class.
 * The drawback is that MigPane is never tested stand alone, as the root node, so for each test it must be decided if we can put it in here, or if it needs a test class on its own.
 *
 * @author Tom Eugelink
 *
 */
public class MigPaneInternalLayoutTest extends org.loadui.testfx.GuiTest {

	@Override
	protected Parent getRootNode() {
		PlatformDefaults.setDefaultDPI(96);
		PlatformDefaults.setPlatform(PlatformDefaults.WINDOWS_XP);

		// use a pane to force the scene large enough, migpane is placed top-left
		pane = new Pane();
		pane.setMinSize(1000, 600);

		// just for readability; place a label
		label = new Label();
		label.layoutYProperty().bind(pane.minHeightProperty().subtract(20));
		pane.getChildren().add(label);

		// done
		return pane;
	}
	private Pane pane = null;
	private Label label = null;

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
		assertWH(migPane, 187.0, 45.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(150.0, 8.0, 30.0, 30.0, 0.01).assertClass(javafx.scene.shape.Rectangle.class);	}

	@Test
	public void wrappingLabel() {
		loadCSS();
		setLabel("wrappingLabel");

		MigPane migPane = TestUtil.runThenWaitForPaintPulse( () -> {
			MigPane constructMigPane = new MigPane(new LC().width("400px").debug(1000), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add nodes
	        Label label = new Label("Test long label to see if the wrap works ok in a Migpane. I am going to have to keep writing because this may not be long enough yet!!");
	        label.setWrapText(true);
	        constructMigPane.add(label, new CC().grow());
	        return constructMigPane;
		});

		//generateSource(migPane);
		assertWH(migPane, 400.0, 90.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 386.0, 76.0, 0.01).assertClass(javafx.scene.control.Label.class);
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

		//generateSource(migPane);
		assertWH(migPane, 781.0, 280.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 500.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(7.0, 45.0, 500.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(2)).assertXYWH(7.0, 83.0, 500.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(3)).assertXYWH(7.0, 121.0, 378.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(4)).assertXYWH(7.0, 159.0, 384.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(5)).assertXYWH(7.0, 197.0, 767.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(6)).assertXYWH(7.0, 235.0, 234.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
	}

	@Test
	public void pack() {
		loadCSS();
		setLabel("pack");

		final Label label = new Label("Pack it up!");
		final MigPane migPane = TestUtil.runThenWaitForPaintPulse( () -> {
			MigPane constructMigPane = new MigPane(new LC().pack().packAlign(0.5f, 1f), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add nodes
			constructMigPane.add(label, "alignx center, wrap unrel");
			Label wrapLabel = new Label("The only thing changed\nis the font size");
			wrapLabel.setTextAlignment(TextAlignment.CENTER);
			constructMigPane.add(wrapLabel, "alignx center");
	        return constructMigPane;
		});

		//generateSource(migPane);
		assertWH(migPane, 226.0, 82.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(60.0, 7.0, 106.0, 19.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(7.0, 37.0, 212.0, 38.0, 0.01).assertClass(javafx.scene.control.Label.class);
		
		// increase font size
		TestUtil.runThenWaitForPaintPulse( () -> {
			label.setFont(new Font(50));
	        return null;
		});
		//generateSource(migPane);
		assertWH(migPane, 244.0, 136.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 230.0, 73.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(16.0, 91.0, 212.0, 38.0, 0.01).assertClass(javafx.scene.control.Label.class);
		
		// increase font size
		TestUtil.runThenWaitForPaintPulse( () -> {
			label.setFont(new Font(100));
	        return null;
		});
		//generateSource(migPane);
		assertWH(migPane, 474.0, 209.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 460.0, 146.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(131.0, 164.0, 212.0, 38.0, 0.01).assertClass(javafx.scene.control.Label.class);
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

		//generateSource(migPane);
		assertWH(migPane, 436.0, 159.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(150.0, 7.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(2)).assertXYWH(293.0, 7.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(3)).assertXYWH(7.0, 45.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(4)).assertXYWH(150.0, 45.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(5)).assertXYWH(293.0, 45.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(6)).assertXYWH(7.0, 83.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(7)).assertXYWH(150.0, 83.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(8)).assertXYWH(293.0, 83.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(9)).assertXYWH(7.0, 121.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
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

		//generateSource(migPane);
		assertWH(migPane, 187.0, 45.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(150.0, 8.0, 30.0, 30.0, 0.01).assertClass(javafx.scene.shape.Rectangle.class);
		new AssertNode(migPane.getChildren().get(2)).assertXYWH(0.0, 0.0, 130.0, 80.0, 0.01).assertClass(javafx.scene.shape.Rectangle.class);
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

		//generateSource(migPane);
		assertWH(migPane, 502.0, 83.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 13.0, 97.0, 19.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(111.0, 7.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(2)).assertXYWH(265.0, 13.0, 87.0, 19.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(3)).assertXYWH(359.0, 7.0, 136.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(4)).assertXYWH(7.0, 51.0, 68.0, 19.0, 0.01).assertClass(javafx.scene.control.Label.class);
		new AssertNode(migPane.getChildren().get(5)).assertXYWH(111.0, 45.0, 384.0, 31.0, 0.01).assertClass(javafx.scene.control.TextField.class);
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

		//generateSource(migPane);
		assertWH(migPane, 831.0, 394.0);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 32.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(46.0, 7.0, 42.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(2)).assertXYWH(95.0, 7.0, 51.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(3)).assertXYWH(153.0, 7.0, 61.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(4)).assertXYWH(221.0, 7.0, 71.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(5)).assertXYWH(299.0, 7.0, 80.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(6)).assertXYWH(386.0, 7.0, 90.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(7)).assertXYWH(483.0, 7.0, 99.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(8)).assertXYWH(589.0, 7.0, 109.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(9)).assertXYWH(705.0, 7.0, 119.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(10)).assertXYWH(7.0, 45.0, 42.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(11)).assertXYWH(7.0, 83.0, 51.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(12)).assertXYWH(7.0, 121.0, 61.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(13)).assertXYWH(7.0, 159.0, 71.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(14)).assertXYWH(7.0, 197.0, 80.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(15)).assertXYWH(7.0, 235.0, 90.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(16)).assertXYWH(7.0, 273.0, 99.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(17)).assertXYWH(7.0, 311.0, 109.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);
		new AssertNode(migPane.getChildren().get(18)).assertXYWH(7.0, 349.0, 119.0, 31.0, 0.01).assertClass(javafx.scene.control.Button.class);

	}

	// =============================================================================================================================================================================================================================
	// SUPPORT

	List<String> EXCLUDED_CLASSES = java.util.Arrays.asList(new String[]{"org.tbee.javafx.scene.layout.MigPane$DebugRectangle"});

	private void assertWH(MigPane migPane, double w, double h) {
		Assert.assertEquals(w, migPane.getWidth(), 0.01);
		Assert.assertEquals(h, migPane.getHeight(), 0.01);
	}

	private void setLabel(String s) {
		PlatformUtil.runAndWait( () -> {
			label.setText(s);
		});
	}

	private void generateSource(Pane pane) {
		System.out.println(label.getText());
		System.out.println("assertWH(migPane, " + pane.getWidth() + ", " + pane.getHeight() + ");");
		AssertNode.generateSource("migPane", pane.getChildren(), EXCLUDED_CLASSES, false, AssertNode.A.XYWH, AssertNode.A.CLASS);
		TestUtil.sleep(3000);
	}

	private void loadCSS() {
		TestUtil.runThenWaitForPaintPulse( () -> {
			Stage lStage = (Stage)getWindows().get(0);
			lStage.getScene().getStylesheets().addAll(this.getClass().getResource("MigPaneInternalLayoutTest.css").toExternalForm());
		});
	}
}
