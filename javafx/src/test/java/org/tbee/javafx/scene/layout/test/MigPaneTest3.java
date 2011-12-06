package org.tbee.javafx.scene.layout.test;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;

import org.tbee.javafx.scene.layout.MigPane;

/**
 * Testing if grow and push actually grow stuff
 * @author Tom Eugelink
 *
 */
public class MigPaneTest3 extends Application {
	
    public static void main(String[] args) {
    	launch(args);       
    }
    
	@Override
	public void start(Stage stage) {
		
        // root
        MigPane lRoot = new MigPane(new LC().debug(1000), new AC(), new AC());

        // create 10 buttons
        for (int i = 0; i < 5*3; i++)
        {
        	int lRow = (int)(i / 3);
        	int lCol = (i + 1) % 3;
        	String lText = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX".substring(0, i + 1);
        	
	        // add a node
        	Control lControl = null;
        	if ( lRow == 0) lControl = new CheckBox(lText); 
        	else if ( lRow == 1) { lControl = new TextField(); ((TextField)lControl).setText(lText); } 
        	else if ( lRow == 2) { lControl = new ChoiceBox<String>(FXCollections.observableArrayList("X", lText, "XX")); ((ChoiceBox)lControl).getSelectionModel().select(1); }  
        	else if ( lRow == 3) { lControl = new ToggleButton(lText); } 
        	else lControl = new Button(lText); // wrong
        	
	        CC lCC = new CC();
	        if (lCol == 2) lCC = lCC.grow().push();
	        if (lCol == 0) lCC = lCC.wrap();
	        lRoot.add(lControl, lCC);
        }
	       
        // create scene
        Scene scene = new Scene(lRoot, 600, 200);
        
        // create stage
        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();
	}
}
