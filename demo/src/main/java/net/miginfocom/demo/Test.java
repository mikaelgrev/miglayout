package net.miginfocom.demo;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.awt.HeadlessException;

public class Test extends JFrame
{
	public Test() throws HeadlessException
	{
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		setLayout(new MigLayout());
		add(panel, "w 100mm");

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] argv) {
		new Test();
	}
}
