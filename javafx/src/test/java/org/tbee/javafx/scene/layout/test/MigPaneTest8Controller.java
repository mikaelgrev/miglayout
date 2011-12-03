package org.tbee.javafx.scene.layout.test;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import org.tbee.javafx.scene.layout.MigPane;

/**
 * Handler class for the FXML
 * 
 * @author Michael Paus 
 * @author Tom Eugelink
 *
 */
public class MigPaneTest8Controller extends MigPane 
{
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private Label messageLabel;
    
    @SuppressWarnings("unused")
	@FXML private void handleButtonAction(ActionEvent event) 
    {
    	String fullName = firstNameField.getText() + " " + lastNameField.getText();
    	if (fullName.length() > 1) 
    	{
	        messageLabel.setText("Your name '" + fullName + "' was successfully entered into our spam database :-(");
    	} 
    	else 
    	{
	        messageLabel.setText("Sorry, but you have to provide your name!");
    	}
    }
}