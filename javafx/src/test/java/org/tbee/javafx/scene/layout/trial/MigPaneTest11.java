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
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBoxBuilder;
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
			LabelBuilder<?> lb = LabelBuilder.create();
			Label title = lb.text("Element " + i).build();
			Label info = lb.text("element created at: " + date).build();

			getChildren().
			add(VBoxBuilder.create().children(title, info).build());

			ButtonBuilder<?> bb = ButtonBuilder.create().mnemonicParsing(false);
			Button b1 = bb.text("button-1").build();
			Button b2 = bb.text("button-2").build();
			Button b3 = bb.text("button-3").build();

			getChildren().
			add(HBoxBuilder.create().children(b1, b2, b3).build());
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

		final Button testButton = ButtonBuilder.create().text("Start test").maxWidth(Double.MAX_VALUE).build();

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

		return BorderPaneBuilder.create().center(listView).bottom(testButton).build();
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
