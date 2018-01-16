package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * Created by user mikaelgrev on 18-01-16.
 */
public class MigPanePosTrial extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage)
	{
		MigPane migPane = new MigPane("debug");

		FlowPane flowPane = new FlowPane();
		flowPane.getChildren().add(new Label("1"));
		flowPane.getChildren().add(new Label("2"));
		flowPane.getChildren().add(new Label("3"));

		migPane.add(new Label("3"), "pos container.x 0");
//		migPane.add(new Label("3"), "pos 0 0"); // This instead of above made it always work.
		migPane.add(flowPane, "");

		// Before fix to Grid.java in 2018-01-16 the flowpane became too large.

		Scene scene = new Scene(migPane);
		stage.setScene(scene);
		stage.show();
	}
}
