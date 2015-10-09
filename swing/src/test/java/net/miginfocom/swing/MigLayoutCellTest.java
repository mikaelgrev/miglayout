package net.miginfocom.swing;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 15-10-09
 *         Time: 11:59
 */
public class MigLayoutCellTest
{
	public static void main(String[] args)
	{
		// The three panels below should all have the same end result.
		// See https://github.com/mikaelgrev/miglayout/issues/37

		JPanel panel1 = new JPanel(new MigLayout("debug"));
		panel1.add(new JLabel("cell 0 0"), "cell 0 0");
		panel1.add(new JLabel("cell 0 2"), "cell 0 2");

		JPanel panel2 = new JPanel(new MigLayout("debug", "", "[][][]"));
		panel2.add(new JLabel("cell 0 0"), "cell 0 0");
		panel2.add(new JLabel("cell 0 2"), "cell 0 2");

		JPanel panel3 = new JPanel(new MigLayout("debug"));
		panel3.add(new JLabel("cell 0 0"), "wrap");
		panel3.add(new JLabel("cell 0 2"), "newline");

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel1, BorderLayout.WEST );
		frame.getContentPane().add(panel2, BorderLayout.CENTER );
		frame.getContentPane().add(panel3, BorderLayout.EAST );
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}