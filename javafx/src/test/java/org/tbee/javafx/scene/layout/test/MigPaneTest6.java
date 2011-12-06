package org.tbee.javafx.scene.layout.test;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.tbee.javafx.scene.layout.MigPane;

/**
 * A copy of the example dialog on www.miglayout.com
 * 
 * @author Tom Eugelink
 *
 */
public class MigPaneTest6 extends Application {
	
    public static void main(String[] args) {
    	launch(args);       
    }

	@Override
	public void start(Stage stage) {
		
        // root
        MigPane lRoot = new MigPane();

        // add managed nodes
        lRoot.add(new Label("First name"), "");
        lRoot.add(new TextField(), "");
        lRoot.add(new Label("Last name"), "gap unrelated");
        lRoot.add(new TextField(), "wrap");
        lRoot.add(new Label("Address"), "");
        lRoot.add(new TextField(), "span, grow");
        
        // create scene
        Scene scene = new Scene(lRoot, 600, 100);
        
        // create stage
        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();
    }

}
