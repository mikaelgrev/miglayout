package org.tbee.javafx.scene.layout.test;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
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
//		vBox.getChildren().add(vBoxLabel);
//		vBox.setPadding(new Insets(50, 50, 50, 50));
//		root.getChildren().add(vBox);

//		root.getChildren().add(new Separator());

		// MigPane
		final MigPane mPane = new MigPane("ins 0 , gap 0");
		final Label migLabel = new Label("Test long label to see if the wrap works ok in a Migpane. I am going to have to keep writing because this may not be long enough yet!!") {
			protected void setHeight(double h)
			{
				System.out.println("Set h: " + h);
				super.setHeight(h);
			}

			protected void setWidth(double w)
			{
				System.out.println("Set w: " + w);
				super.setWidth(w);
			}
		};
		migLabel.setWrapText(true);

		final Rectangle rect = new Rectangle(100, 100);
//		mPane.add(titleLB.text("MigPane").build(), "wrap");
//		mPane.add(rect, "");
		mPane.add(migLabel, "");


//		mPane.setPadding(new Insets(50, 50, 50, 50));

//		final Button butt = new Button();
//		mPane.add(butt, "hidemode 3");

//		butt.needsLayoutProperty().addListener(new ChangeListener<Boolean>()
//		{
//			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
//			{
//				System.out.println("needs more!!");
//			}
//		});

		Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				rect.setWidth(Math.random() * 100);
//				butt.setFont(new Font(Math.round(Math.random() * 10)));
//				System.out.println("bef: " + butt.isNeedsLayout());
//				butt.setVisible(!butt.isVisible());
//				stage.sizeToScene();
//				butt.setId("" + Math.random());
//				System.out.println("aft: " + butt.isNeedsLayout());
			}
		}));
		fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
		fiveSecondsWonder.play();
//
//		Timeline fiveSecondsWonder2 = new Timeline(new KeyFrame(Duration.seconds(0.5), new EventHandler<ActionEvent>() {
//			public void handle(ActionEvent event) {
////				System.out.println("need: " + butt.isNeedsLayout());
//			}
//		}));
//		fiveSecondsWonder2.setCycleCount(Timeline.INDEFINITE);
//		fiveSecondsWonder2.play();

//		butt.setText("sdfsdf");

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
