package net.miginfocom.layout;

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
 */

import java.io.Serializable;

/**
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 14-09-24
 *         Time: 17:05
 */
public class AnimSpec implements Serializable
{
//	public static final AnimSpec OFF = new AnimSpec(-1, 0, 0);
	public static final AnimSpec DEF = new AnimSpec(0, 0, 0.2f, 0.2f);

	private final int prio;
	private final int durMillis;
	private final float easeIn, easeOut;

	/**
	 * @param prio The animation priority. When added with the general animation priority of the layout the animation will
	 * be done if the resulting value is &gt; 0.
	 * @param durMillis Duration in milliseconds. <=0 means default value should be used and > 0 is the number of millis
	 * @param easeIn 0 is linear (no ease). 1 is max ease. Always clamped between these values.
	 * @param easeOut 0 is linear (no ease). 1 is max ease. Always clamped between these values.
	 */
	public AnimSpec(int prio, int durMillis, float easeIn, float easeOut)
	{
		this.prio = prio;
		this.durMillis = durMillis;
		this.easeIn = LayoutUtil.clamp(easeIn, 0, 1);
		this.easeOut = LayoutUtil.clamp(easeOut, 0, 1);
	}

	/**
	 * @return The animation priority. When added with the general animation priority of the layout the animation will
	 * be done if the resulting value is &gt; 0.
	 */
	public int getPriority()
	{
		return prio;
	}

	/**
	 * @param defMillis Default used if the millis in the spec is set to "default".
	 * @return Duration in milliseconds. <=0 means default value should be used and > 0 is the number of millis
	 */
	public int getDurationMillis(int defMillis)
	{
		return durMillis > 0 ? durMillis : defMillis;
	}

	/**
	 * @return Duration in milliseconds. <= 0 means default value should be used and > 0 is the number of millis
	 */
	public int getDurationMillis()
	{
		return durMillis;
	}

	/**
	 * @return A value between 0 and 1 where 0 is no ease in and 1 is maximum ease in.
	 */
	public float getEaseIn()
	{
		return easeIn;
	}

	/**
	 * @return A value between 0 and 1 where 0 is no ease out and 1 is maximum ease out.
	 */
	public float getEaseOut()
	{
		return easeOut;
	}
}
