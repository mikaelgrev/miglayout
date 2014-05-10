package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 14-04-29
 *         Time: 14:22
 */
public class MigPaneHidemodeTrial extends Application
{
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {


		// add managed nodes
		Button button = new Button("Change Visibility");

		TextField textField0 = new TextField("hidemode 0");
		TextField textField1 = new TextField("hidemode 1");
		TextField textField2 = new TextField("hidemode 2");
		TextField textField3 = new TextField("hidemode 3");

		MigPane pane = new MigPane(new LC().debug().pack(), new AC(), new AC());

		pane.add(button, "wrap");
		pane.add(new Separator(), "growx, wrap");
		pane.add(textField0, "hidemode 0, gap 10 10 10 10, wrap");
		pane.add(new Separator(), "growx, wrap");
		pane.add(textField1, "hidemode 1, gap 10 10 10 10, wrap");
		pane.add(new Separator(), "growx, wrap");
		pane.add(textField2, "hidemode 2, gap 10 10 10 10, wrap");
		pane.add(new Separator(), "growx, wrap");
		pane.add(textField3, "hidemode 3, gap 10 10 10 10, wrap");
		pane.add(new Separator(), "growx, wrap");

//		VBox pane = new VBox();
//		pane.getChildren().add(button);
//		pane.getChildren().add(new Separator());
//		pane.getChildren().add(textField0);
//		pane.getChildren().add(new Separator());
//		pane.getChildren().add(textField1);
//		pane.getChildren().add(new Separator());
//		pane.getChildren().add(textField2);q
//		pane.getChildren().add(new Separator());
//		pane.getChildren().add(textField3);
//		pane.getChildren().add(new Separator());

		button.setOnAction(event -> {
			textField0.setVisible(!textField0.isVisible());
			textField1.setVisible(!textField1.isVisible());
			textField2.setVisible(!textField2.isVisible());
			textField3.setVisible(!textField3.isVisible());
		});

		// create scene
		Scene scene = new Scene(pane, -1, -1);

		// create stage
		stage.setTitle("Test");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}
}
