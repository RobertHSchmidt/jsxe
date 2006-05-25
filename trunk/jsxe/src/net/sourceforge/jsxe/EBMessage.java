/*
EBMessage.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)
Copyright (C) 1999, 2002 Slava Pestov

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

package net.sourceforge.jsxe;

/**
 * The base class of all EditBus messages.<p>
 *
 * Message classes extending this class typically add
 * other data members and methods to provide subscribers with whatever is
 * needed to handle the message appropriately.<p>
 *
 * Message types sent by jEdit can be found in the
 * {@link net.sourceforge.jsxe.msg} package.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @author Slava Pestov
 * @author John Gellene (API documentation)
 * @version $Id$
 *
 * @since jsXe 0.5 pre1
 */
public abstract class EBMessage {
    
    //{{{ EBMessage constructor
    /**
     * Creates a new message.
     * @param source The message source
     */
    public EBMessage(Object source) {
        this.source = source;
    } //}}}

    //{{{ EBMessage constructor
    /**
     * Creates a new message.
     * @param source The message source
     */
    public EBMessage(EBListener source) {
        this.source = source;
    } //}}}

    //{{{ getSource() method
    /**
     * Returns the sender of this message.
     */
    public Object getSource() {
        return source;
    } //}}}

    //{{{ toString() method
    /**
     * Returns a string representation of this message.
     */
    public String toString() {
        String className = getClass().getName();
        int index = className.lastIndexOf('.');
        return className.substring(index + 1)
            + "[" + paramString() + "]";
    } //}}}

    //{{{ paramString() method
    /**
     * Returns a string representation of this message's parameters.
     */
    public String paramString() {
        return "source=" + source;
    } //}}}

    //{{{ Private members
    private Object source;
    //}}}

}
