package org.tbee.javafx.scene.layout.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * Test initial layout
 * @author Tom Eugelink
 *
 */
public class MigPaneTest1 extends Application {

    public static void main(String[] args) {
    	launch(args);
    }

	@Override
	public void start(Stage stage) {

        // root
        MigPane lRoot = new MigPane(new LC().debug(1000), new AC(), new AC());

        // add nodes
        lRoot.add(new TextField(), new CC());
        lRoot.add(new Rectangle(30,30, Color.YELLOW), new CC());

        // create scene
        Scene scene = new Scene(lRoot, -1, -1);

        // create stage
        stage.setTitle("Test");
        stage.setScene(scene);
		stage.sizeToScene();
        stage.show();
    }
}
