package org.tbee.javafx.scene.layout.trial;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.tbee.javafx.scene.layout.fxml.MigPane;

import java.util.Date;

/**
 * This test is ran out of memory because of a memory leak.
 *
 */
public class MigPaneTest11 extends Application
{

	public static class ListElement extends MigPane
	{

		public ListElement(int i, Date date)
		{
			Label title = new Label("Element " + i);
			Label info = new Label("element created at: " + date);

			getChildren().
			add(new VBox(title, info));

			Button b1 = new Button("button-1");
			Button b2 = new Button("button-2");
			Button b3 = new Button("button-3");
			b1.setMnemonicParsing(false);
			b2.setMnemonicParsing(false);
			b3.setMnemonicParsing(false);

			getChildren().
			add(new HBox(b1, b2, b3));
		}

	}

	public MigPaneTest11()
	{
		super();
//		this.items = FXCollections.observableArrayList();
	}
//	private ObservableList<ListElement> items;

	private Parent createRoot()
	{

		final ListView<MigPaneTest11.ListElement> listView = new ListView<MigPaneTest11.ListElement>();
//		listView.setItems(this.items);

		Button testButton = new Button("Start test");
		testButton.maxWidth(Double.MAX_VALUE);

		testButton.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(final ActionEvent ev)
			{
				final Thread t = new Thread()
				{
					@Override
					public void run()
					{
						for (int loop = 0; loop < Integer.MAX_VALUE; loop++)
						{
							System.out.println("loop " + loop);
							Platform.runLater(new Runnable()
							{
								public void run()
								{
//									for (ListElement le : items)
//									{
//										le.getChildren().clear();
//									}
//									items.clear();
									ObservableList<ListElement> items = FXCollections.observableArrayList();
									for (int i = 0; i < 10; i++)
									{
										items.add(new ListElement(i, new Date()));
									}
									listView.setItems(items);
								}
							});
							try
							{
								Thread.sleep(10);
							}
							catch (final InterruptedException e)
							{
								// do nothing
							}
						}
						System.out.println("done");
					}

				};
				t.setDaemon(true);
				t.start();
			}
		});

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(listView);
		borderPane.setBottom(testButton);
		return borderPane;
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		Parent root = createRoot();

		scene = new Scene(root);
		stage.setScene(scene);
		stage.setWidth(800);
		stage.setHeight(600);
		stage.show();
	}
	static Scene scene = null;

	public static void main(String[] args)
	{
		launch(args);
	}

}
