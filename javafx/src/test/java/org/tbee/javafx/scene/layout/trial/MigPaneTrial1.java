package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

public class MigPaneTrial1 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		MigPane migPane = new MigPane(new LC());
		migPane.add(new Label("Label"), new CC().wrap());
		migPane.add(new Label("Label"), new CC().wrap().push().grow());
		migPane.add(new Label("Label"), new CC().wrap());
		Button button = new Button("Button");
//		migPane.add(button, new CC().dockWest().grow());
		migPane.add(button, new CC().wrap().grow().push());
		button.setRotate(90);
		Scene scene = new Scene(migPane);
		stage.setScene(scene);
		stage.show();
	}
}