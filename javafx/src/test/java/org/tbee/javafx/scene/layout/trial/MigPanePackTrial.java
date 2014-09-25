package org.tbee.javafx.scene.layout.trial;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Test miglayout managed and unmanaged nodes
 * @author Tom Eugelink
 *
 */
public class MigPanePackTrial extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {

		// root
		MigPane rootMP = new MigPane(new LC().pack().packAlign(0.5f, 1f));

		// add managed nodes
		Label label = new Label("Pack it up!");

		rootMP.add(label, "alignx center, wrap unrel");

		Label wrapLabel = new Label("The only thing changed\nis the font size");
		wrapLabel.setTextAlignment(TextAlignment.CENTER);
		rootMP.add(wrapLabel, "alignx center");

		// create scene
		Scene scene = new Scene(rootMP);

		// create stage
		stage.setTitle("Pack Trial");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();

		AtomicBoolean up = new AtomicBoolean(true);
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(40), event -> {
				double oldSize = label.getFont().getSize();
				if (oldSize > 100) {
					up.set(false);
				} else if (oldSize < 10) {
					up.set(true);
					stage.centerOnScreen();
				}
				label.setFont(new Font(oldSize + (up.get() ? 2 : -2)));
		}));
		timeline.setCycleCount(-1);
		timeline.play();
	}
}