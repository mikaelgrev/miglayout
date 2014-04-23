package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

public class MigPaneTrial17 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		MigPane migPane = new MigPane(new LC().wrapAfter(1));
		migPane.getChildren().add(0, new Button("Test"));
		Scene scene = new Scene(migPane);
		stage.setScene(scene);
		stage.show();
	}
}