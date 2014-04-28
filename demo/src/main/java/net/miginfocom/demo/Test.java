package net.miginfocom.demo;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Toolkit;

public class Test extends JFrame
{
	public Test() throws HeadlessException
	{
		System.out.println("res " + Toolkit.getDefaultToolkit().getScreenResolution());

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
