/*
UndoEvent.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe.msg;

import net.sourceforge.jsxe.*;
import net.sourceforge.jsxe.dom.XMLDocument;

/**
 * Message send when an undo is performed on an XMLDocument.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 *
 * @since jsXe 0.5 pre3
 */
public class UndoEvent extends EBMessage {
    
    //{{{ UndoEvent constructor
    /**
     * Creates a new UndoEvent.
     */
    public UndoEvent(XMLDocument document) {
        super(document);
    } //}}}
    
}
