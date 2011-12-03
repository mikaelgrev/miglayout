package org.tbee.javafx.scene.layout.test;
import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.tbee.javafx.scene.layout.MigPane;

/**
 * Load a layout from FXML
 * 
 * @author Michael Paus and Tom Eugelink
 *
 */
public class MigPaneTest8 extends Application {
	
    public static void main(String[] args) {
    	launch(args);       
    }

	@Override
	public void start(Stage stage)
	throws IOException
	{
    	// load FXML
		String lName = this.getClass().getSimpleName() + ".xml";
		URL lURL = this.getClass().getResource(lName);
		System.out.println("loading FXML " + lName + " -> " + lURL);
    	MigPane lRoot = (MigPane)FXMLLoader.load(lURL);

        // create scene
        Scene scene = new Scene(lRoot, 800, 300);
        
        // create stage
        stage.setTitle("FXML Test");
        stage.setScene(scene);
        stage.show();
    }

}
