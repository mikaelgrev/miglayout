package org.tbee.javafx.scene.layout.test;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import net.miginfocom.layout.CC;

import org.tbee.javafx.scene.layout.MigPane;

/**
 * Using string constraints
 * @author Tom Eugelink
 *
 */
public class MigPaneTest5 extends Application {
	
    public static void main(String[] args) {
    	launch(args);       
    }

	@Override
	public void start(Stage stage) {
		
        // root
        MigPane lRoot = new MigPane("debug", "[grow,fill]", "");

        // add managed nodes
        lRoot.add(new TextField(), "");
        
        // add unmanaged nodes
        lRoot.add(new Rectangle(100, 50, 30, 30), new CC().external());
        
        // create scene
        Scene scene = new Scene(lRoot, 200, 100);
        
        // create stage
        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();
    }

}
