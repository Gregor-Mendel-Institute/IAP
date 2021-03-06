/*
 * ========================================================================
 * JCommon : a free general purpose class library for the Java(tm) platform
 * ========================================================================
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 * Project Info: http://www.jfree.org/jcommon/index.html
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 * ---------------
 * PadMessage.java
 * ---------------
 * (C) Copyright 2004, by Object Refinery Limited.
 * Original Author: Thomas Morgner;
 * Contributor(s): David Gilbert (for Object Refinery Limited);
 * $Id: PadMessage.java,v 1.1 2011-01-31 09:02:55 klukas Exp $
 * Changes
 * -------
 * 15-Jul-2004 : Added JCommon header (DG);
 */

package org.jfree.base.log;

import java.util.Arrays;

/**
 * A message object that pads the output if the text is shorter than the given length.
 */
public class PadMessage {

	/**
	 * The message.
	 */
	private final Object text;

	/**
	 * The padding size.
	 */
	private final int length;

	/**
	 * Creates a new message.
	 * 
	 * @param message
	 *           the message.
	 * @param length
	 *           the padding size.
	 */
	public PadMessage(final Object message, final int length) {
		this.text = message;
		this.length = length;
	}

	/**
	 * Returns a string representation of the message.
	 * 
	 * @return the string.
	 */
	public String toString() {
		final StringBuffer b = new StringBuffer();
		b.append(this.text);
		if (b.length() < this.length) {
			final char[] pad = new char[this.length - b.length()];
			Arrays.fill(pad, ' ');
			b.append(pad);
		}
		return b.toString();
	}

}
