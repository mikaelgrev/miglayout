package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.miginfocom.layout.LC;
import org.tbee.javafx.scene.layout.MigPane;

/**
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 14-04-24
 *         Time: 16:33
 */
public class MigPaneSizeTrial extends Application
{
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {

		// root
		MigPane lRoot = new MigPane(new LC().debug().fillX());

//		System.out.println(Screen.getPrimary().getDpi());
//		System.out.println(Toolkit.getDefaultToolkit().getScreenResolution());

		// add nodes
		lRoot.add(new Button("500 logical pixels (def)"), "w 500, wrap");
		lRoot.add(new Button("500 logical pixels"), "w 500lp, wrap");
		lRoot.add(new Button("500 pixels"), "w 500px, wrap");
		lRoot.add(new Button("10 centimeters"), "w 10cm, wrap");
		lRoot.add(new Button("4 inches"), "w 4in, wrap");
		lRoot.add(new Button("30% of screen"), "w 30sp, wrap");
		lRoot.add(new Button("30% of container"), "w 30%, wrap");

		// create scene
		Scene scene = new Scene(lRoot);

		// create stage
		stage.setTitle("Test - resize to check container percentage");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
	}
}
