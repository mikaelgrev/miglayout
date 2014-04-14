package org.tbee.javafx.scene.layout.test;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * Wrap MigPane in a number of other containers and set a padding.
 *
 * @author Tom Eugelink
 *
 */
public class MigPaneTest9 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        // root
        MigPane lMigPane = new MigPane(new LC().debug(1000).align("center", "center").gridGap("0px", "0px"), new AC(), new AC());
        for (int i = 0; i < 9; i++)
        {
            CC lCC = new CC();
            if ((i + 1) % 3 == 0)
            {
                lCC = lCC.wrap();
            }
            final ButtonBase btn = new ToggleButton("MMMMMMMMMMMMMMMMMMMMMMMMMMMM".substring(0, i + 1));
            if (i == 0)
            {
            	btn.setStyle("-fx-padding: 10; ");
            }
            lMigPane.add(btn, lCC);
        }

        // include in stackpane and set a padding
        StackPane lStackPane = new StackPane();
        lStackPane.getChildren().add(lMigPane);
        lStackPane.setStyle("-fx-background-color: yellow; -fx-padding: 10; ");

        // create scene
        Scene scene = new Scene(new Group(lStackPane), -1, -1);

        // create stage
        stage.setTitle("ButtonTest");
        stage.setScene(scene);
	    stage.sizeToScene();
        stage.show();
    }
}