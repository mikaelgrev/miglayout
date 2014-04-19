package net.miginfocom.demo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.LayoutCallback;
import net.miginfocom.layout.UnitValue;
import org.tbee.javafx.scene.layout.MigPane;

import java.util.IdentityHashMap;

/*
 * License (BSD):
 * ==============
 *
 * Copyright (c) 2004, Mikael Grev, MiG InfoCom AB. (miglayout (at) miginfocom (dot) com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * Neither the name of the MiG InfoCom AB nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * @version 1.0
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 2006-sep-08
 */

public class JavaFXCallbackDemo extends Application
{
	private AnimationTimer layoutTimer;

	private final IdentityHashMap<Object, Long> jumpingMap = new IdentityHashMap<>();
	private Point2D mousePos = null;
	private MigPane migPane = null;

	private Button createButton(int i)
	{
		Button button = new Button(String.valueOf("MIG LAYOUT".charAt(i)));

		button.setOnAction(event -> {
			jumpingMap.put(button, System.nanoTime());
			layoutTimer.start();
		});
		button.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent event) -> {
			mousePos = button.localToParent(event.getX(), event.getY());
			migPane.requestLayout();
		});

		button.widthProperty().addListener((observable, oldValue, newValue) -> {
			button.setFont(new Font(newValue.doubleValue() / 4));
		});
		button.setTextFill(Color.rgb(100, 100, 100));
		button.setFont(new Font(24));
		return button;
	}

	public void start(Stage stage) {

		migPane = new MigPane("align center bottom, insets 80");

		// This callback methods will be called for every layout cycle and let you make correction before and after the calculations.
		migPane.addLayoutCallback(new LayoutCallback() {

			// This is the size change part
			public BoundSize[] getSize(ComponentWrapper wrapper)
			{
				if (wrapper.getComponent() instanceof Button) {
					Button c = (Button) wrapper.getComponent();
					Point2D p = mousePos != null ? c.parentToLocal(mousePos) : new Point2D(-1000, -1000);

					double fact = Math.sqrt(Math.pow(Math.abs(p.getX() - c.getWidth() / 2f), 2) + Math.pow(Math.abs(p.getY() - c.getHeight() / 2f), 2));
					fact = Math.max(2 - (fact / 200), 1);

					return new BoundSize[] {new BoundSize(new UnitValue((float) (70 * fact)), ""), new BoundSize(new UnitValue((float) (70 * fact)), "")};
				}
				return null;
			}

			// This is the jumping part
			public void correctBounds(ComponentWrapper wrapper)
			{
				Long pressedNanos = jumpingMap.get(wrapper.getComponent());
				if (pressedNanos != null) {
					long duration = System.nanoTime() - pressedNanos;
					double maxHeight = 100.0 - (duration / 70000000.0);
					int deltaY = (int) Math.round(Math.abs(Math.sin((duration) / 300000000.0) * maxHeight));
					wrapper.setBounds(wrapper.getX(), wrapper.getY() - deltaY, wrapper.getWidth(), wrapper.getHeight());
					if (maxHeight < 0.5) {
						jumpingMap.remove(wrapper.getComponent());
						if (jumpingMap.isEmpty())
							layoutTimer.stop();
					}
				}
			}
		});

		for (int j = 0; j < 10; j++)
			migPane.add(createButton(j), "aligny 0.8al");

		Label label = new Label("Press one of those Buttons!");
		label.setFont(new Font(24));
		label.setTextFill(Color.rgb(150, 150, 150));
		migPane.add(label, "pos 0.5al 0al");

		migPane.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent event) -> {
			mousePos = new Point2D(event.getX(), event.getY());
			migPane.requestLayout();
		});

		migPane.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
			mousePos = null;
			migPane.requestLayout();
		});

		// create scene
		Scene scene = new Scene(migPane, -1, 450);
		scene.setFill(new LinearGradient(0, 0, 0, 500, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1, Color.rgb(240, 238, 235))));

		// create stage
		stage.setTitle("Callback Demo");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();

		layoutTimer = new AnimationTimer() {
			public void handle(long now) {
				migPane.requestLayout();
			}
		};
	}

	public static void main(String[] args) {
		launch(args);
	}
}