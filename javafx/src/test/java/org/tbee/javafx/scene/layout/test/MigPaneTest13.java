package org.tbee.javafx.scene.layout.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.tbee.javafx.scene.layout.MigPane;

/**
 * The window should wrap the button perfectly (including the inset).
 * 
 * @author Tom Eugelink
 *
 */
public class MigPaneTest13 extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		final LabelBuilder<?> lBuilder = LabelBuilder
				.create()
				.text("Test long label to see if the wrap works ok in a Migpane. I am going to have to keep writing because this may not be long enough yet!!")
				.wrapText(true);

		final VBox root = new VBox();
		// we give a fixed width of 400 and a height of -1. The latter one means
		// that the height of the Scene should be
		// calculated by the bounds of its root
		final Scene scene = new Scene(root, 400, -1);

		// VBOX
		final VBox vBox = new VBox();
		LabelBuilder<?> titleLB = LabelBuilder.create().style("-fx-font-weight: bold;");
		vBox.getChildren().add(titleLB.text("VBox").build());
		final Label vBoxLabel = lBuilder.build();
		vBox.getChildren().add(vBoxLabel);
		root.getChildren().add(vBox);

		root.getChildren().add(new Separator());

		// MigPane
		final MigPane mPane = new MigPane("fill, ins 0 , gap 0");
		final Label migLabel = lBuilder.build();
		// doesn't even work when min height == pref height
		mPane.add(titleLB.text("MigPane").build(), "wrap");
		mPane.add(migLabel, "grow");
		root.getChildren().add(mPane);

		stage.setScene(scene);
		stage.show();

		// The label correctly calculetes its pref and max height based on the
		// number of lines.
		// Eg setting wrapText to false returns its minWidth (height for 1 line)
		// as prefWidth and cuts the rest of the
		// line with ellipsis, setting it to true returns a prefHeight so that
		// every line is visible
		double sceneWidth = scene.getWidth();
		System.out.println("label heights for a scene width of " + sceneWidth
				+ ":\nmin: " + vBoxLabel.minHeight(sceneWidth) + ", pref: "
				+ vBoxLabel.prefHeight(sceneWidth) + ", max:"
				+ vBoxLabel.maxHeight(sceneWidth));
		stage.sizeToScene();
	}
}
