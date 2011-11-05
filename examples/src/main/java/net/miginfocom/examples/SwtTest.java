package net.miginfocom.examples;

import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

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
public class SwtTest
{

	public static void main(final String[] args)
	{
		final Display display = new Display();
		final Shell shell = new Shell(display);

		shell.setLayout(new FillLayout(SWT.VERTICAL));

		final Composite cmpLabels = new Composite(shell, SWT.BORDER);
		cmpLabels.setLayout(new MigLayout("wrap 5"));

		final Label l0 = new Label(cmpLabels, SWT.NONE);
		l0.setText("L 0");
		final Label l1 = new Label(cmpLabels, SWT.NONE);
		final Label l2 = new Label(cmpLabels, SWT.NONE);
		l2.setText("L 2");
		final Label l3 = new Label(cmpLabels, SWT.NONE);
		final Label l4 = new Label(cmpLabels, SWT.NONE);
		l4.setText("L 4");

		final Composite cmpButtons = new Composite(shell, SWT.NONE);
		cmpButtons.setLayout(new FillLayout());

		final Button btn1 = new Button(cmpButtons, SWT.PUSH);
		btn1.setText("Set 1");
		btn1.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				l3.setText("");
				l1.setText("Some Text");
				cmpLabels.layout();
				cmpLabels.redraw();
			}
		});

		final Button btn3 = new Button(cmpButtons, SWT.PUSH);
		btn3.setText("Set 3");
		btn3.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				l1.setText("");
				l3.setText("Some Text");
				cmpLabels.layout();
				cmpLabels.redraw();
			}
		});

		shell.setSize(300, 100);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}