package net.miginfocom.demo;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 * @author Gili Tzabari
 */
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;


import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
* @author Mikael Grev, MiG InfoCom AB
*         Date: 7/29/11
*         Time: 10:30 AM
*/
public class Test
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();

		JPanel panel = new JPanel(new MigLayout("debug, align right"));
		panel.add(new JLabel("Three"), "wrap");
		panel.add(new JLabel("Six"));

		frame.add(panel);
		frame.setBounds(500, 500, 500, 500);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
