package net.miginfocom.swt;
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

import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 */
public final class SwtContainerWrapper extends SwtComponentWrapper implements ContainerWrapper
{
	public SwtContainerWrapper(Composite c)
	{
		super(c);
	}

	public ComponentWrapper[] getComponents()
	{
		Composite c = (Composite) getComponent();
		Control[] cons = c.getChildren();
		ComponentWrapper[] cws = new ComponentWrapper[cons.length];
		for (int i = 0; i < cws.length; i++)
			cws[i] = new SwtComponentWrapper(cons[i]);
		return cws;
	}

	public int getComponentCount()
	{
		return ((Composite) getComponent()).getChildren().length;
	}

	public Object getLayout()
	{
		return ((Composite) getComponent()).getLayout();
	}

	public final boolean isLeftToRight()
	{
		return (((Composite) getComponent()).getStyle() & SWT.LEFT_TO_RIGHT) > 0;
	}

	public final void paintDebugCell(int x, int y, int width, int height)
	{
		// A Composite can not draw above its children, so the cells can not be painted.

//		if (c.isDisposed())
//			return;
//		GC gc = new GC(c);
//
//		gc.setLineStyle(SWT.LINE_DASHDOTDOT);
//		gc.setLineJoin(SWT.JOIN_MITER);
//		gc.setLineCap(SWT.CAP_SQUARE);
//
//		gc.setBackground(DB_CELL_BG);
//		gc.fillRectangle(x, y, width, height);
//
//		gc.setForeground(DB_CELL_OUTLINE);
//		gc.drawRectangle(x, y, width - 1, height - 1);
//
//		gc.dispose();
	}

	public int getComponetType(boolean disregardScrollPane)
	{
		return TYPE_CONTAINER;
	}


	public int getLayoutHashCode()
	{
		int h = super.getLayoutHashCode();

		if (isLeftToRight())
			h |= (1 << 26);

		return h;
	}
}
