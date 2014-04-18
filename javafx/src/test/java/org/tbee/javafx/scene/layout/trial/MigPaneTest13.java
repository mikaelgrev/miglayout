package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.geometry.Insets;
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
	public void start(final Stage stage) throws Exception {

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
		vBox.setPadding(new Insets(10, 10, 10, 10));
		root.getChildren().add(vBox);

		root.getChildren().add(new Separator());

		// MigPane
		final MigPane mPane = new MigPane("ins 0");
		mPane.setPadding(new Insets(10, 10, 10, 10));
		final Label migLabel = new Label("Test long label to see if the wrap works ok in a Migpane. I am going to have to keep writing because this may not be long enough yet!!");
		migLabel.setWrapText(true);

//		mPane.add(titleLB.text("MigPane").build(), "wrap 0");
		mPane.add(migLabel, "");

//		AtomicReference<Float> size = new AtomicReference<>(10f);
//		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
//			public void handle(ActionEvent event) {
//				migLabel.setFont(new Font(size.get()));
//				stage.sizeToScene();
//				size.set(size.get() + 0.5f);
//			}
//		}));
//		timeline.setCycleCount(Timeline.INDEFINITE);
//		timeline.play();

		root.getChildren().add(mPane);

		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();

		// The label correctly calculetes its pref and max height based on the
		// number of lines.
		// Eg setting wrapText to false returns its minWidth (height for 1 line)
		// as prefWidth and cuts the rest of the
		// line with ellipsis, setting it to true returns a prefHeight so that
		// every line is visible
//		double sceneWidth = scene.getWidth();
//
//		System.out.println("\nlabel heights for a scene width of " + sceneWidth
//				+ ":\nmin: " + vBoxLabel.minHeight(sceneWidth) + ", pref: "
//				+ vBoxLabel.prefHeight(sceneWidth) + ", max:"
//				+ vBoxLabel.maxHeight(sceneWidth));
	}
}
