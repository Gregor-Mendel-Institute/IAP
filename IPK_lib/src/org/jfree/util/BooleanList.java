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
 * ----------------
 * BooleanList.java
 * ----------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 * Original Author: David Gilbert (for Object Refinery Limited);
 * Contributor(s): -;
 * $Id: BooleanList.java,v 1.1 2011-01-31 09:01:41 klukas Exp $
 * Changes
 * -------
 * 11-Jun-2003 : Version 1 (DG);
 * 23-Jul-2003 : Renamed BooleanTable --> BooleanList and now extends ObjectList (DG);
 * 13-Aug-2003 : Now extends new class AbstractObjectList (DG);
 */

package org.jfree.util;

import java.io.Serializable;

/**
 * A list of <code>Boolean</code> objects.
 */
public class BooleanList extends AbstractObjectList implements Cloneable, Serializable {

	/**
	 * Creates a new list
	 */
	public BooleanList() {
		super();
	}

	/**
	 * Returns a {@link Boolean} from the list.
	 * 
	 * @param index
	 *           the index (zero-based).
	 * @return a {@link Boolean} from the list.
	 */
	public Boolean getBoolean(final int index) {
		return (Boolean) get(index);
	}

	/**
	 * Sets the value for an item in the list. The list is expanded if necessary.
	 * 
	 * @param index
	 *           the index (zero-based).
	 * @param b
	 *           the boolean.
	 */
	public void setBoolean(final int index, final Boolean b) {
		set(index, b);
	}

	/**
	 * Returns an independent copy of the list.
	 * 
	 * @return A clone.
	 * @throws CloneNotSupportedException
	 *            this shouldn't happen.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Tests the list for equality with another object (typically also a list).
	 * 
	 * @param o
	 *           the other object.
	 * @return A boolean.
	 */
	public boolean equals(final Object o) {

		if (o instanceof BooleanList) {
			return super.equals(o);
		}
		return false;
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return the hashcode
	 */
	public int hashCode() {
		return super.hashCode();
	}
}
