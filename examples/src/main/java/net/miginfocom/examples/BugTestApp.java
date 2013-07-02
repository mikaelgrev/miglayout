package net.miginfocom.examples;

import javax.swing.*;
import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.awt.LayoutManager;
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

/**
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: Dec 15, 2008
 *         Time: 7:04:50 PM
 */
public class BugTestApp
{
	private static JPanel createPanel()
	{
		JPanel c = new JPanel();
		c.setBackground(new Color(200, 255, 200));
		c.setLayout(new MigLayout("debug"));

		JLabel lab = new JLabel("Qwerty");
		lab.setFont(lab.getFont().deriveFont(30f));
		c.add(lab, "pos 0%+5 0%+5 50%-5  50%-5");
		c.add(new JTextField("Qwerty"));

		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(new MigLayout());
		f.add(c, "w 400, h 100");
		f.setLocationRelativeTo(null);
		f.pack();
		f.setVisible(true);
		return null;
	}

	private static JPanel createPanel2()
	{
		JFrame tst = new JFrame();
		tst.setLayout(new MigLayout("debug, fillx","",""));

		tst.add(new JTextField(),"span 2, grow, wrap");
		tst.add(new JTextField(10));
		tst.add(new JLabel("End"));

		tst.setSize(600, 400);
		tst.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tst.setVisible(true);
		return null;
	}

	public static void main2(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				createPanel();

//				JFrame frame = new JFrame("Bug Test App");
//				frame.getContentPane().add(createPanel2());
//				frame.pack();
//				frame.setLocationRelativeTo(null);
//				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//				frame.setVisible(true);
			}
		});
	}
	public static void main(String[] args) throws Exception
	{
//		createFrame(new GridLayout(1,1));
		createFrame(new MigLayout());
	}

	private static void createFrame(LayoutManager outerPanelLayout)
	{
		JPanel innerPanel = new JPanel(new MigLayout());
		for (int i = 0; i < 2000; i++)
			innerPanel.add(new JLabel("label nr "+i), "wrap");

		JPanel outerPanel = new JPanel(outerPanelLayout);
		outerPanel.add(innerPanel);

		JFrame f1 = new JFrame(outerPanelLayout.getClass().getSimpleName());
		f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f1.getContentPane().add(new JScrollPane(outerPanel));
		f1.pack();
		f1.setLocation((int)(Math.random() * 800.0), 0);
		f1.setVisible(true);
	}

}
