package net.miginfocom.demo;

import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.LayoutCallback;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

public class CallbackDemo extends JFrame implements ActionListener, MouseMotionListener, MouseListener
{
	private final Timer repaintTimer = new Timer(20, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			((JPanel) getContentPane()).revalidate();
		}
	});
	private final IdentityHashMap<Object, Long> pressMap = new IdentityHashMap<Object, Long>();
	private Point mousePos = null;

	public CallbackDemo()
	{
		super("MiG Layout Callback Demo");

		MigLayout migLayout = new MigLayout("align center bottom, insets 30");
		final JPanel panel = new JPanel(migLayout) {
			protected void paintComponent(Graphics g)
			{
				((Graphics2D) g).setPaint(new GradientPaint(0, getHeight() / 2, Color.WHITE, 0, getHeight(), new Color(240, 238, 235)));
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		setContentPane(panel);

		// This callback methods will be called for every layout cycle and let you make correction before and after the calculations.
		migLayout.addLayoutCallback(new LayoutCallback() {

			// This is the size change part
			public BoundSize[] getSize(ComponentWrapper comp)
			{
				if (comp.getComponent() instanceof JButton) {
					Component c = (Component) comp.getComponent();
					Point p = mousePos != null ? SwingUtilities.convertPoint(panel, mousePos, c) : new Point(-1000, -1000);

					float fact = (float) Math.sqrt(Math.pow(Math.abs(p.x - c.getWidth() / 2f), 2) + Math.pow(Math.abs(p.y - c.getHeight() / 2f), 2));
					fact = Math.max(2 - (fact / 200), 1);

					return new BoundSize[] {new BoundSize(new UnitValue(70 * fact), ""), new BoundSize(new UnitValue(70 * fact), "")};
				}
				return null;
			}

			// This is the jumping part
			public void correctBounds(ComponentWrapper c)
			{
				Long pressedNanos = pressMap.get(c.getComponent());
				if (pressedNanos != null) {
					long duration = System.nanoTime() - pressedNanos;
					double maxHeight = 100.0 - (duration / 100000000.0);
					int deltaY = (int) Math.round(Math.abs(Math.sin((duration) / 300000000.0) * maxHeight));
					c.setBounds(c.getX(), c.getY() - deltaY, c.getWidth(), c.getHeight());
					if (maxHeight < 0.5) {
						pressMap.remove(c.getComponent());
						if (pressMap.size() == 0)
							repaintTimer.stop();
					}
				}
			}
		});

		for (int j = 0; j < 10; j++)
			panel.add(createButton(j), "aligny 0.8al");

		JLabel label = new JLabel("Press one of those Swing JButtons!");
		label.setFont(new Font("verdana", Font.PLAIN, 24));
		label.setForeground(new Color(150, 150, 150));
		panel.add(label, "pos 0.5al 0.2al");

		panel.addMouseMotionListener(this);
		panel.addMouseListener(this);
	}

	private static Font[] FONTS = new Font[120];
	private JButton createButton(int i)
	{
		JButton button = new JButton(String.valueOf("MIG LAYOUT".charAt(i))) {
			public Font getFont()
			{
				if (FONTS[0] == null) {
					for (int i = 0; i < FONTS.length; i++)
						FONTS[i] = new Font("tahoma", Font.PLAIN, i);
				}

				return FONTS[getWidth() >> 1];
			}
		};

		button.setForeground(new Color(100, 100, 100));
		button.setFocusPainted(false);
		button.addMouseMotionListener(this);
		button.addActionListener(this);
		button.setMargin(new Insets(0, 0, 0, 0));
		return button;
	}

	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e)
	{
		if (e.getSource() instanceof JButton) {
			mousePos = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getContentPane());
		} else {
			mousePos = e.getPoint();
		}
		((JPanel) getContentPane()).revalidate();
	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e)
	{
		mousePos = null;
		((JPanel) getContentPane()).revalidate();
	}

	public void actionPerformed(ActionEvent e)
	{
		pressMap.put(e.getSource(), System.nanoTime());
		repaintTimer.start();
	}

	public static void main(String args[])
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {}

		CallbackDemo demoFrame = new CallbackDemo();
		demoFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		demoFrame.setSize(970, 500);
		demoFrame.setLocationRelativeTo(null);
		demoFrame.setVisible(true);
	}
}