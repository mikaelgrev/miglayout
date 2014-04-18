package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * Test miglayout managed and unmanaged nodes
 * @author Tom Eugelink
 *
 */
public class MigPaneBaselineTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {

		// root
		MigPane lRoot = new MigPane(new LC().debug(1000), new AC(), new AC());

		// add managed nodes
		Label bitLabel = new Label("We should");
		bitLabel.setFont(new Font(40));
		lRoot.add(bitLabel, "split 2");
		lRoot.add(new TextField("have the same baseline"));
//		lRoot.add(new Rectangle(30,30, Color.YELLOW), new CC());

		// create scene
		Scene scene = new Scene(lRoot, -1, -1);

		// create stage
		stage.setTitle("Test");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}

}