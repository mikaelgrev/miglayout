package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 15-10-07
 *         Time: 21:11
 */
public class MigPaneSizeGroupTrial18 extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage)
	{
		// root
		MigPane pane = new MigPane("debug");

		// add managed nodes
		pane.add(new Label("Should have same sizes as ->"), "sgx");
		pane.add(new Label("Short"), "sgx");

		// With this line the layout will not be correct pre 2015-10-07 fix since the Grid is created
		// with the Scene set to null
		pane.prefHeight(-1);

		// Add this and it till work again since it clears the grid. Adding "nocache"
		// to the LC in the constructor also works.
//		pane.invalidateGrid();

		// create scene
		Scene scene = new Scene(pane, -1, -1);

		// create stage
		stage.setTitle("Test");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}
}

