package net.miginfocom.examples;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.Color;

/**
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 14-06-03
 *         Time: 21:31
 */
public class MigLayoutBug
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel(new MigLayout("debug", "", ""));

		JPanel greyPanel = new JPanel();
		greyPanel.setBackground(Color.GRAY);

		JTextArea label = new JTextArea("text \n" +
		           "over \n" +
		           "two \n" +
		           "rows");

		mainPanel.add(label, "spany 2");
		mainPanel.add(new JLabel("First row"), "wrap");
		mainPanel.add(new JLabel("Second row"), "wrap");
		mainPanel.add(greyPanel, "spanx 2, pushy, grow");

		frame.setContentPane(mainPanel);
		frame.setVisible(true);
	}
}
