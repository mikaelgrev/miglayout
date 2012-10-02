package org.tbee.javafx.scene.layout.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * Test wrapping
 * @author Tom Eugelink
 *
 */
public class MigPaneTest2 extends Application {

    public static void main(String[] args) {
    	launch(args);
    }

	@Override
	public void start(Stage stage) {

        // root
        MigPane lRoot = new MigPane(new LC().fillX().debug(1000), new AC(), new AC());

        // add nodes
        for (int i = 0; i < 10; i++)
        {
        	TextField lRectangle = new TextField();
	        CC lCC = new CC();
	        if ((i + 1) % 3 == 0) {
	        	lCC = lCC.growX().wrap(); // wrap every 3rd
	        }
	        lRoot.add(lRectangle, lCC);
        }

        // create scene
        Scene scene = new Scene(lRoot, 600, 300);

        // create stage
        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();
	}

}
