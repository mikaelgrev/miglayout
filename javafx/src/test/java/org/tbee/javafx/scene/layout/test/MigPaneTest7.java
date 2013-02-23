package org.tbee.javafx.scene.layout.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * A copy of the example dialog on www.miglayout.com
 *
 * @author Tom Eugelink
 *
 */
public class MigPaneTest7 extends Application {

    public static void main(String[] args) {
    	launch(args);
    }

	@Override
	public void start(Stage stage) {

        // root
        MigPane lRoot = new MigPane(new LC().debug(300), new AC(), new AC());

        // add nodes
        for (int i = 0; i < 10; i++)
        {
        	lRoot.add(new Button("MMMMMMMMMMMMMMMMMMMMMMMMMMMM".substring(0, i + 1)), i < 9 ? new CC() : new CC().wrap());
        }
        for (int i = 1; i < 10; i++)
        {
        	lRoot.add(new Button("MMMMMMMMMMMMMMMMMMMMMMMMMMMM".substring(0, i + 1)), new CC().wrap().spanX());
        }

        // create scene
        Scene scene = new Scene(lRoot, 800, 300);

        // create stage
        stage.setTitle("ButtonTest");
        stage.setScene(scene);
        stage.show();
    }

}
