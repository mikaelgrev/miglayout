package org.tbee.javafx.scene.layout.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * Test a nested MigPane
 *
 */
public class MigTestTest10 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws InterruptedException {

        MigPane lOuterMigPane = new MigPane("debug");
        lOuterMigPane.setId("outer");
        lOuterMigPane.setDebugCellColor(null);
        lOuterMigPane.setDebugOutlineColor(Color.BLUE);
        lOuterMigPane.setDebugContainerOutlineColor(Color.BLUE);
        lOuterMigPane.add(new Button("In outer MigPane"));

        MigPane lNestedMigPane = new MigPane("debug");
        lNestedMigPane.setId("nested");
        lNestedMigPane.setDebugCellColor(null);
        lNestedMigPane.setDebugOutlineColor(Color.RED);
        lNestedMigPane.setDebugContainerOutlineColor(Color.RED);
        lNestedMigPane.add(new Button("In nested MigPane"));
        lOuterMigPane.add(lNestedMigPane);

        Scene scene = new Scene(lOuterMigPane);
        stage.setScene(scene);
	    stage.sizeToScene();
        stage.show();
    }
}
