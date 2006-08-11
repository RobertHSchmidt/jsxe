/*
AttributeChange.java
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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe.dom.undo;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.dom.AdapterNode;
//}}}

//{{{ Swing classes
import javax.swing.undo.*;
//}}}

//{{{ DOM classes
import org.w3c.dom.DOMException;
//}}}

//}}}

/**
 * An undoable edit sigifying a change to an attribute into an element 
 * AdapterNode..
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see net.sourceforge.jsxe.dom.XMLDocument
 * @see net.sourceforge.jsxe.dom.AdapterNode
 */
public class AttributeChange extends AbstractUndoableEdit {
    
    private AdapterNode m_node;
    private String m_name;
    private String m_oldValue;
    private String m_newValue;
    
    //{{{ AttributeChange constructor
    /**
     * @param name The qualified name of the attribute.
     * @param oldValue The old value of the attribute. If null then the attribute was added.
     * @param newValue The new value of the attribute. If null then the attribute was removed
     */
    public AttributeChange(AdapterNode node, String name, String oldValue, String newValue) {
        m_node = node;
        m_name = name;
        m_oldValue = oldValue;
        m_newValue = newValue;
    }//}}}
    
    //{{{ undo()
    
    public void undo() throws CannotUndoException {
        super.undo();
        try {
            if (m_oldValue != null) {
                m_node.setAttribute(m_name, m_oldValue);
            } else {
                //if the old value is null then the attribute was added.
                //we need to remove it here.
                m_node.removeAttribute(m_name);
            }
        } catch (DOMException e) {
            throw new CannotUndoException();
        }
    }//}}}
    
    //{{{ redo()
    
    public void redo() throws CannotRedoException {
        super.redo();
        try {
            if (m_newValue != null) {
                m_node.setAttribute(m_name, m_newValue);
            } else {
                //if the new value is null then the attribute was removed
                //we need to remove it here.
                m_node.removeAttribute(m_name);
            }
        } catch (DOMException e) {
            throw new CannotRedoException();
        }
    }//}}}
    
}