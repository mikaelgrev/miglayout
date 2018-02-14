package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

public class MigPaneTrial16 extends Application
{
	public static final String PATH_TO_IMAGE = "/MigPaneTrial16.png";

	public static void main(String[] arguments) {
//		URL url = new MigPaneTrial16().getClass().getResource(PATH_TO_IMAGE);
//		System.out.println(url);
				launch();
	}


	@Override
	public void start(Stage stage) throws Exception {
		Scene scene = createScene();
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}

	private Scene createScene() {
		MigPane parent = new MigPane(new LC().debug(300));
		addRowTo(parent);
		return new Scene(parent, 300, 100);
	}

	boolean b = true;
	private void addRowTo(MigPane parent) {
		ImageView mainIcon = new ImageView();
		Button toggle = new Button("Hello", mainIcon);
		sizeUpButton(toggle);

		toggle.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event)
			{
				if (b) {
					mainIcon.setImage(new Image(PATH_TO_IMAGE));
					//				toggle.getBaselineOffset();
					System.out.println("baseline img: " + toggle.getBaselineOffset());
				} else {
					mainIcon.setImage(null);
					System.out.println("baseline: " + toggle.getBaselineOffset());
				}
				b = !b;
			}
		});
		parent.add(toggle, "");
		parent.add(new Label("<-Click the Button"), new CC().growX().pushX());
	}

	private void sizeUpButton(ButtonBase button) {
//		button.setMinSize(20, 20);
//		button.setPrefSize(20, 20);
//		button.setMaxSize(20, 20);
	}
}