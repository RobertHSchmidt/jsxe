/*
 * CharIndexedSegment.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1998 Wes Biggs
 * Copyright (C) 2000, 2001 Slava Pestov
 * Copyright (C) 2004 Ian Lewis
 *
 * Originally used in jEdit. This file was modified slightly to set the
 * m_reverse variable
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as published
 * by the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package net.sourceforge.jsxe.gui.view;

//{{{ Imports
import java.io.Serializable;
import javax.swing.text.Segment;
import gnu.regexp.*;
//}}}

/**
 * Lets <code>gnu.regexp</code> search within <code>Segment</code> objects.
 */
public class CharIndexedSegment implements CharIndexed, Serializable
{
	//{{{ CharIndexedSegment constructor
	/**
	 * Creates a new <code>CharIndexedSegment</code>.
	 * @since jEdit 4.1pre3
	 */
	public CharIndexedSegment(Segment seg, int index)
	{
		this.seg = seg;
		m_index = index;
		m_reverse = false;
	} //}}}

	//{{{ CharIndexedSegment constructor
	/**
	 * Creates a new <code>CharIndexedSegment</code>.
	 * @since jEdit 4.1pre1
	 */
	public CharIndexedSegment(Segment seg, boolean reverse)
	{
		this.seg = seg;
		m_index = (reverse ? seg.count - 1 : 0);
		m_reverse = reverse;
	} //}}}

	//{{{ charAt() method
	public char charAt(int index)
	{
		if (m_reverse)
			index = -index;

		return ((m_index + index) < seg.count && m_index + index >= 0)
			? seg.array[seg.offset + m_index + index]
			: CharIndexed.OUT_OF_BOUNDS;
	} //}}}

	//{{{ isValid() method
	public boolean isValid()
	{
		return (m_index >=0 && m_index < seg.count);
	} //}}}

	//{{{ reset() method
	public void reset()
	{
		m_index = (m_reverse ? seg.count - 1 : 0);
	} //}}}

	//{{{ move() method
	public boolean move(int index)
	{
		if (m_reverse)
			index = -index;

		return ((m_index += index) < seg.count);
	} //}}}

	//{{{ Private members
	private Segment seg;
	private int m_index;
	private boolean m_reverse;
	//}}}
}