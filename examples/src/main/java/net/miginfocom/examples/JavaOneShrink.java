package net.miginfocom.examples;

import javax.swing.*;
import javax.swing.border.LineBorder;
import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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
 *         Date: Apr 20, 2008
 *         Time: 10:32:58 PM
 */
public class JavaOneShrink
{
	private static JComponent createPanel(String ... args)
	{
		JPanel panel = new JPanel(new MigLayout("nogrid"));

		panel.add(createTextArea(args[0].replace(", ", "\n    ")), args[0] + ", w 200");
		panel.add(createTextArea(args[1].replace(", ", "\n    ")), args[1] + ", w 200");
		panel.add(createTextArea(args[2].replace(", ", "\n    ")), args[2] + ", w 200");
		panel.add(createTextArea(args[3].replace(", ", "\n    ")), args[3] + ", w 200");

		JSplitPane gSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, panel, new JPanel());
		gSplitPane.setOpaque(true);
		gSplitPane.setBorder(null);

		return gSplitPane;
	}

	private static JComponent createTextArea(String s)
	{
		JTextArea ta = new JTextArea("\n\n    " + s, 6, 20);
		ta.setBorder(new LineBorder(new Color(200, 200, 200)));
		ta.setFont(new Font("Helvetica", Font.BOLD, 20));
		ta.setMinimumSize(new Dimension(20, 20));
		ta.setFocusable(false);
		return ta;
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				JFrame frame = new JFrame("JavaOne Shrink Demo");
				Container cp = frame.getContentPane();
				cp.setLayout(new MigLayout("wrap 1"));

				cp.add(createPanel("", "", "", ""));
				cp.add(createPanel("shrinkprio 1", "shrinkprio 1", "shrinkprio 2", "shrinkprio 3"));
				cp.add(createPanel("shrink 25", "shrink 50", "shrink 75", "shrink 100"));
				cp.add(createPanel("shrinkprio 1, shrink 50", "shrinkprio 1, shrink 100", "shrinkprio 2, shrink 50", "shrinkprio 2, shrink 100"));

				frame.pack();
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
