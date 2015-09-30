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
 */

package net.miginfocom.layout;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

/**
 * Unit tests for {@link IDEUtil}.
 *
 * @author Karl Tauber
 */
public class IDEUtilTest
{
	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@BeforeClass
	public static void initialize() {
		// MigLayout: enable design time for LayoutUtil.putCCString()
		LayoutUtil.setDesignTime( null, true );
	}

	@Test
	public void testMigComponentConstraints() {
		// wrap
		testCC( "wrap", null, new CC().wrap(), ".wrap()" );
		testCC( "wrap 15px", null, new CC().wrap("15px"), ".wrap(\"15px\")" );
		testCC( "wrap push", null, new CC().wrap("push"), ".wrap(\"push\")" );
		testCC( "wrap 15:push", null, new CC().wrap("15:push"), ".wrap(\"15:push\")" );

		// newline
		testCC( "newline", null, new CC().newline(), ".newline()" );
		testCC( "newline 15px", null, new CC().newline("15px"), ".newline(\"15px\")" );

		// push
		testCC( "push", null, new CC().push(), ".push()" );
		testCC( "push 200", "push 200 100", new CC().push(200f, 100f), ".push(200f, 100f)" );
		testCC( "push 200 300", null, new CC().push(200f, 300f), ".push(200f, 300f)" );
		testCC( "pushx", null, new CC().pushX(), ".pushX()" );
		testCC( "pushx 200", null, new CC().pushX(200f), ".pushX(200f)" );
		testCC( "pushy", null, new CC().pushY(), ".pushY()" );
		testCC( "pushy 200", null, new CC().pushY(200f), ".pushY(200f)" );

		// skip
		testCC( "skip", "skip 1", new CC().skip(), ".skip(1)" );
		testCC( "skip 3", null, new CC().skip(3), ".skip(3)" );

		// span
		testCC( "span", "spanx", new CC().span(), ".spanX()" );
		testCC( "span 4", "spanx 4", new CC().span(4), ".spanX(4)" );
		testCC( "span 2 3", null, new CC().span(2, 3), ".span(2, 3)" );
		testCC( "spanx", null, new CC().spanX(), ".spanX()" );
		testCC( "spanx 10", null, new CC().spanX(10), ".spanX(10)" );
		testCC( "spany", null, new CC().spanY(), ".spanY()" );
		testCC( "spany 2", null, new CC().spanY(2), ".spanY(2)" );

		// split
		testCC( "split", null, new CC().split(), ".split()" );
		testCC( "split 4", null, new CC().split(4), ".split(4)" );

		// cell
		testCC( "cell 2 2", null, new CC().cell(2, 2), ".cell(2, 2)" );
		testCC( "cell 0 1 2", "cell 0 1 2 1", new CC().cell(0, 1, 2), ".cell(0, 1, 2, 1)" );
		testCC( "cell 0 1 2 3", null, new CC().cell(0, 1, 2, 3), ".cell(0, 1, 2, 3)" );

		// flowx, flowy
		testCC( "flowx", null, new CC().flowX(), ".flowX()" );
		testCC( "flowy", null, new CC().flowY(), ".flowY()" );

		// width, height
		testCC( "width 10", null, new CC().width("10"), ".width(\"10\")" );
		testCC( "height pref!", null, new CC().height("pref!"), ".height(\"pref!\")" );

		// wmin, wmax, hmin, hmax
		testCC( "wmin 10", null, new CC().minWidth("10"), ".minWidth(\"10\")" );
		testCC( "wmax 10", null, new CC().maxWidth("10"), ".maxWidth(\"10\")" );
		testCC( "hmin 10", null, new CC().minHeight("10"), ".minHeight(\"10\")" );
		testCC( "hmax 10", null, new CC().maxHeight("10"), ".maxHeight(\"10\")" );

		// grow
		testCC( "grow", null, new CC().grow(), ".grow()" );
		testCC( "grow 50", "growx 50,growy", new CC().grow(50, 100), ".growX(50).growY()" );
		testCC( "grow 50 20", "growx 50,growy 20", new CC().grow(50, 20), ".growX(50).growY(20)" );
		testCC( "growx", null, new CC().growX(), ".growX()" );
		testCC( "growx 50", null, new CC().growX(50), ".growX(50)" );
		testCC( "growy", null, new CC().growY(), ".growY()" );
		testCC( "growy 0", null, new CC().growY(0), ".growY(0)" );

		// growprio
		testCC( "growprio 50", "growpriox 50", new CC().growPrio(50), ".growPrioX(50)" );
		testCC( "growprio 50 80", "growpriox 50,growprioy 80", new CC().growPrio(50, 80), ".growPrioX(50).growPrioY(80)" );
		testCC( "growpriox 50", null, new CC().growPrioX(50), ".growPrioX(50)" );
		testCC( "growprioy 80", null, new CC().growPrioY(80), ".growPrioY(80)" );

		// shrink
		testCC( "shrink 50", "shrinkx 50", new CC().shrink(50, 100), ".shrinkX(50)" );
		testCC( "shrink 50 20", "shrinkx 50,shrinky 20", new CC().shrink(50, 20), ".shrinkX(50).shrinkY(20)" );
		testCC( "shrink 100 20", "shrinky 20", new CC().shrink(100, 20), ".shrinkY(20)" );
		testCC( "shrinkx 50", null, new CC().shrinkX(50), ".shrinkX(50)" );
		testCC( "shrinky 20", null, new CC().shrinkY(20), ".shrinkY(20)" );

		// shrinkprio
		testCC( "shrinkprio 50", "shrinkpriox 50", new CC().shrinkPrio(50), ".shrinkPrioX(50)" );
		testCC( "shrinkprio 50 80", "shrinkpriox 50,shrinkprioy 80", new CC().shrinkPrio(50, 80), ".shrinkPrioX(50).shrinkPrioY(80)" );
		testCC( "shrinkpriox 50", null, new CC().shrinkPrioX(50), ".shrinkPrioX(50)" );
		testCC( "shrinkprioy 80", null, new CC().shrinkPrioY(80), ".shrinkPrioY(80)" );

		// sizegroup
		testCC( "sizegroup", "sizegroupx,sizegroupy", new CC().sizeGroup("", ""), ".sizeGroupX(\"\").sizeGroupY(\"\")" );
		testCC( "sizegroup g1", "sizegroupx g1,sizegroupy g1", new CC().sizeGroup("g1", "g1"), ".sizeGroupX(\"g1\").sizeGroupY(\"g1\")" );
		testCC( "sizegroupx", null, new CC().sizeGroupX(""), ".sizeGroupX(\"\")" );
		testCC( "sizegroupx g1", null, new CC().sizeGroupX("g1"), ".sizeGroupX(\"g1\")" );
		testCC( "sizegroupy", null, new CC().sizeGroupY(""), ".sizeGroupY(\"\")" );
		testCC( "sizegroupy g1", null, new CC().sizeGroupY("g1"), ".sizeGroupY(\"g1\")" );

		// endgroup
		testCC( "endgroupx", null, new CC().endGroupX(""), ".endGroupX(\"\")" );
		testCC( "endgroupx g1", null, new CC().endGroupX("g1"), ".endGroupX(\"g1\")" );
		testCC( "endgroupy", null, new CC().endGroupY(""), ".endGroupY(\"\")" );
		testCC( "endgroupy g1", null, new CC().endGroupY("g1"), ".endGroupY(\"g1\")" );

		// gap, gaptop, gapleft, gapbottom, gapright, gapbefore, gapafter
		testCC( "gap 5", "gapx 5", new CC().gap("5"), ".gapX(\"5\", null)" );
		testCC( "gap 5 6", "gapx 5 6", new CC().gap("5", "6"), ".gapX(\"5\", \"6\")" );
		testCC( "gap 5 6 7", "gapx 5 6,gapy 7", new CC().gap("5", "6", "7"), ".gapX(\"5\", \"6\").gapY(\"7\", null)" );
		testCC( "gap 5 6 7 8", "gapx 5 6,gapy 7 8", new CC().gap("5", "6", "7", "8"), ".gapX(\"5\", \"6\").gapY(\"7\", \"8\")" );
		testCC( "gaptop 5", "gapy 5", new CC().gapTop("5"), ".gapY(\"5\", null)" );
		testCC( "gapleft 5", "gapx 5", new CC().gapLeft("5"), ".gapX(\"5\", null)" );
		testCC( "gapbottom 5", "gapy null 5", new CC().gapBottom("5"), ".gapY(null, \"5\")" );
		testCC( "gapright 5", "gapx null 5", new CC().gapRight("5"), ".gapX(null, \"5\")" );
		testCC( "gapbefore 5", "gapx 5", new CC().gapBefore("5"), ".gapX(\"5\", null)" );
		testCC( "gapafter 5", "gapx null 5", new CC().gapAfter("5"), ".gapX(null, \"5\")" );

		// gapx, gapy
		testCC( "gapx 5", null, new CC().gapX("5", null), ".gapX(\"5\", null)" );
		testCC( "gapx 5 10", null, new CC().gapX("5", "10"), ".gapX(\"5\", \"10\")" );
		testCC( "gapy unrel", null, new CC().gapY("unrel", null), ".gapY(\"unrel\", null)" );
		testCC( "gapy unrel rel", null, new CC().gapY("unrel", "rel"), ".gapY(\"unrel\", \"rel\")" );

		// id
		testCC( "id button1", null, new CC().id("button1"), ".id(\"button1\")" );

		// pos
		testCC( "pos 50% 80%", null, new CC().pos("50%", "80%"), ".pos(\"50%\", \"80%\")" );
		testCC( "pos 50% 80%", null, new CC().pos("50%", "80%"), ".pos(\"50%\", \"80%\")" );
		testCC( "pos 50% 80% 200 100", null, new CC().pos("50%", "80%", "200", "100"), ".pos(\"50%\", \"80%\", \"200\", \"100\")" );
		testCC( "pos null null 200 100", null, new CC().pos(null, null, "200", "100"), ".pos(null, null, \"200\", \"100\")" );
		testCC( "pos (b1.x+b1.w/2) (b1.y2+rel)", null, new CC().pos("(b1.x+b1.w/2)", "(b1.y2+rel)"), ".pos(\"(b1.x+b1.w/2)\", \"(b1.y2+rel)\")" );
		testCC( "pos (visual.x2 - pref) 200", null, new CC().pos("(visual.x2 - pref)", "200"), ".pos(\"(visual.x2 - pref)\", \"200\")" );
		testCC( "pos null b1.y b1.x-rel b1.y2", null, new CC().pos(null, "b1.y", "b1.x-rel", "b1.y2"), ".pos(null, \"b1.y\", \"b1.x-rel\", \"b1.y2\")" );

		// x, x2, y, y2
		testCC( "x 10", null, new CC().x("10"), ".x(\"10\")" );
		testCC( "x button1.x", null, new CC().x("button1.x"), ".x(\"button1.x\")" );
		testCC( "x2 (visual.x2-50)", null, new CC().x2("(visual.x2-50)"), ".x2(\"(visual.x2-50)\")" );
		testCC( "y 10", null, new CC().y("10"), ".y(\"10\")" );
		testCC( "y2 10", null, new CC().y2("10"), ".y2(\"10\")" );

		// dock
		testCC( "dock north", "north", new CC().dockNorth(), ".dockNorth()" );
		testCC( "dock west", "west", new CC().dockWest(), ".dockWest()" );
		testCC( "dock south", "south", new CC().dockSouth(), ".dockSouth()" );
		testCC( "dock east", "east", new CC().dockEast(), ".dockEast()" );
		testCC( "dock center", "push,grow", new CC().push().grow(), ".push().grow()" );
		testCC( "north", null, new CC().dockNorth(), ".dockNorth()" );
		testCC( "west", null, new CC().dockWest(), ".dockWest()" );
		testCC( "south", null, new CC().dockSouth(), ".dockSouth()" );
		testCC( "east", null, new CC().dockEast(), ".dockEast()" );

		// pad
		testCC( "pad 5", "pad 5 5 5 5", new CC().pad("5"), ".pad(\"5 5 5 5\")" );
		testCC( "pad 5 6", "pad 5 6 6 6", new CC().pad("5 6"), ".pad(\"5 6 6 6\")" );
		testCC( "pad 5 6 7", "pad 5 6 7 7", new CC().pad("5 6 7"), ".pad(\"5 6 7 7\")" );
		testCC( "pad 5 6 7 8", null, new CC().pad("5 6 7 8"), ".pad(\"5 6 7 8\")" );
		testCC( "pad 5.0px 6.0px 7.0px 8.0px", null, new CC().pad(5, 6, 7, 8), ".pad(\"5.0px 6.0px 7.0px 8.0px\")" );

		// align
		testCC( "align left", "alignx left", new CC().alignX("left"), ".alignX(\"left\")" );
		testCC( "align left top", "alignx left,aligny top", new CC().alignX("left").alignY("top"), ".alignX(\"left\").alignY(\"top\")" );
		testCC( "align null top", "aligny top", new CC().alignY("top"), ".alignY(\"top\")" );
		testCC( "alignx left", null, new CC().alignX("left"), ".alignX(\"left\")" );
		testCC( "aligny top", null, new CC().alignY("top"), ".alignY(\"top\")" );

		// external
		testCC( "external", null, new CC().external(), ".external()" );

		// hidemode
		testCC( "hidemode 1", null, new CC().hideMode(1), ".hideMode(1)" );

		// tag
		testCC( "tag ok", null, new CC().tag("ok"), ".tag(\"ok\")" );
	}

	private void testCC( String input, String expected, CC inputAPI, String expectedAPI ) {
		CC cc = ConstraintParser.parseComponentConstraint( input );
		String actual = IDEUtil.getConstraintString( cc, false );
		String actualAPI = IDEUtil.getConstraintString( inputAPI, true );
		String actualAPI2 = IDEUtil.getConstraintString( cc, true );

		myAssertEquals( input, (expected != null) ? expected : input, actual );
		myAssertEquals( input, expectedAPI, actualAPI );
		myAssertEquals( input, actualAPI2, actualAPI );
	}

	private void myAssertEquals( String message, Object expected, Object actual ) {
		try {
			assertEquals( message, expected, actual );
		} catch( Throwable ex ) {
			errorCollector.addError( ex );
		}
	}
}
