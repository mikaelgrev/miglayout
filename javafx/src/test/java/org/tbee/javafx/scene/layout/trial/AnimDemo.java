package org.tbee.javafx.scene.layout.trial;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.tbee.javafx.scene.layout.LayoutAnimator;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.Random;

import static javafx.animation.Timeline.INDEFINITE;

/**
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 14-09-25
 *         Time: 20:43
 */
public class AnimDemo extends Application
{
	private final Random random = new Random(1);
	private MigPane pane;

	public static void main(String[] args)
	{
		launch(args);
	}

	public void start(Stage stage)
	{
		pane = new MigPane("flowy, align center center");

		stage.setScene(new Scene(pane, 600, 800));
		stage.sizeToScene();
		stage.show();

		for (int i = 0; i++ < 3;)
			pane.add(getIx(), createRect());

		Timeline timer = new Timeline(new KeyFrame(new Duration(LayoutAnimator.ANIM_DURATION.toMillis() + 300), e -> {
			if (Math.random() > 0.6 || pane.getChildren().size() < 3) {
				pane.add(getIx(), createRect(), "");
			} else {
				pane.remove(getIx());
			}
		}));
		timer.setCycleCount(INDEFINITE);
		timer.play();
	}

	private int getIx()
	{
		int size = pane.getChildren().size();
		return size == 0 ? 0 : random.nextInt(size);
	}

	private Rectangle createRect()
	{
		Rectangle rect = new Rectangle(500, 100);
		rect.setFill(Color.color(Math.random(), Math.random(), Math.random()));
		rect.setArcHeight(15);
		rect.setArcWidth(15);
		return rect;
	}
}

