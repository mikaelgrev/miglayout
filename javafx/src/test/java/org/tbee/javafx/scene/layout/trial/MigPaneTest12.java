package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * The window should wrap the button perfectly (including the inset).
 * @author Tom Eugelink
 *
 */
public class MigPaneTest12 extends Application {

    public static void main(String[] args) {
    	launch(args);
    }

	@Override
    public void start (Stage stage) throws Exception {
		MigPane root = new MigPane();
//            MigPane root = new MigPane(new net.miginfocom.layout.LC().insets("0px"));
        //HBox root = new HBox();

        Button b = new Button("HelloHelloHelloHelloHelloHelloHello");

        root.getChildren().add(b);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("FX");
		stage.sizeToScene();
        stage.show();
    }
}
