package org.tbee.javafx.scene.layout.test;

import java.util.concurrent.ExecutionException;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jfxtras.test.AssertNode;
import jfxtras.test.AssertNode.A;
import jfxtras.util.PlatformUtil;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;

import org.junit.Assert;
import org.junit.Test;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * TestFX is able to layout a single node per class.
 * Because we would be creating MigPane only once, this would result in one class with one test method per to-be-tested layout, and thus is a LOT of classes.
 * By placing MigPane in a presized Pane, it is possible to test multiple layouts in separate methods within a single class.
 * The drawback is that MigPane is never tests stand alone, so for each test it must be decided if we can put it as a test in here, or if it needs a class on its own.
 * 
 * @author Tom Eugelink
 *
 */
public class MigPaneInternalLayoutTest extends org.loadui.testfx.GuiTest {

	@Override
	protected Parent getRootNode() {
		// use a pane to force the scene large enough, migpane is placed top-left
		pane = new Pane();
		pane.setMinSize(800, 600);
		
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
	public void twoChildBasicLayout() throws InterruptedException, ExecutionException {
		setLabel("twoChildBasicLayout");
		
		MigPane migPane = PlatformUtil.runAndWait( () -> {
			MigPane constructMigPane = new MigPane(new LC().debug(1000), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add nodes
	        constructMigPane.add(new TextField(), new CC());
	        constructMigPane.add(new Rectangle(30,30, Color.YELLOW), new CC());
	        return constructMigPane;
		});
		PlatformUtil.waitForPaintPulse();

		assertWH(migPane, 200, 44);
		//AssertNode.generateSource("migPane", migPane.getChildren(), java.util.Arrays.asList(new String[]{"org.tbee.javafx.scene.layout.MigPane$DebugRectangle"}), false, A.XYWH, A.CLASS);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 10.0, 149.0, 25.0, 0.01).assertClass(javafx.scene.control.TextField.class);
		new AssertNode(migPane.getChildren().get(1)).assertXYWH(163.0, 7.0, 30.0, 30.0, 0.01).assertClass(javafx.scene.shape.Rectangle.class);	
	}


	@Test
	public void wrappingLabel() throws InterruptedException, ExecutionException {
		setLabel("wrappingLabel");
		
		MigPane migPane = PlatformUtil.runAndWait( () -> {
			MigPane constructMigPane = new MigPane(new LC().width("400px").debug(1000), new AC(), new AC());
	        pane.getChildren().add(constructMigPane);

	        // add nodes
	        Label label = new Label("Test long label to see if the wrap works ok in a Migpane. I am going to have to keep writing because this may not be long enough yet!!");
	        label.setWrapText(true);
	        constructMigPane.add(label, new CC().grow());
	        return constructMigPane;
		});
		PlatformUtil.waitForPaintPulse();

		assertWH(migPane, 400, 48);
		//AssertNode.generateSource("migPane", migPane.getChildren(), java.util.Arrays.asList(new String[]{"org.tbee.javafx.scene.layout.MigPane$DebugRectangle"}), false, A.XYWH, A.CLASS);
		new AssertNode(migPane.getChildren().get(0)).assertXYWH(7.0, 7.0, 386.0, 34.0, 0.01).assertClass(javafx.scene.control.Label.class);
	}
	
	// =============================================================================================================================================================================================================================
	// SUPPORT
	
	private void assertWH(MigPane migPane, double w, double h) {
		Assert.assertEquals(w, migPane.getWidth(), 0.01);
		Assert.assertEquals(h, migPane.getHeight(), 0.01);
	}
	
	private void setLabel(String s) {
		PlatformUtil.runAndWait( () -> {
			label.setText(s);
		});
	}
}
