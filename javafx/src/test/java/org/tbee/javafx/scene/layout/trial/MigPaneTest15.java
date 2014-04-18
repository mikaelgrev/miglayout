package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.tbee.javafx.scene.layout.MigPane;

public class MigPaneTest15 extends Application {

	public static void main(String[] arguments) {
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		Scene scene = createScene();
		stage.setScene(scene);
		showStage(stage);
	}

	private Scene createScene() {
		final MigPane container = new MigPane();
		Button control = new Button("Add Content");
		control.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				showContent(container);
			}
		});

		MigPane parent = new MigPane("");
		parent.add(control);
		parent.add(container);
		return new Scene(parent);
	}

	private void showContent(MigPane container) {
		container.getChildren().clear();
		ComboBox<String> comboBox = new ComboBox<>();
		comboBox.getItems().add("There is a label to my left!");
		Label label = new Label("I should be visible!");
		container.add(label);
		container.add(comboBox);
	}


	private void showStage(Stage stage) {
		stage.setHeight(400);
		stage.setWidth(800);
		stage.show();
	}
}