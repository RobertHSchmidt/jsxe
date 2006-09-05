/*
NodeNameChange.java
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
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ Swing classes
import javax.swing.undo.*;
//}}}

//{{{ DOM classes
import org.w3c.dom.DOMException;
//}}}

//}}}

/**
 * An undoable edit sigifying a change of an AdapterNode's local name.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see net.sourceforge.jsxe.dom.XMLDocument
 * @see net.sourceforge.jsxe.dom.AdapterNode
 */
public class NodeNameChange extends AbstractUndoableEdit {
    
    private AdapterNode m_node;
    private String m_oldValue;
    private String m_newValue;
    
    //{{{ NodeNameChange constructor
    public NodeNameChange(AdapterNode node, String oldValue, String newValue) {
        m_node = node;
        m_oldValue = oldValue;
        m_newValue = newValue;
    }//}}}
    
    //{{{ undo()
    
    public void undo() throws CannotUndoException {
        super.undo();
        try {
            m_node.setLocalName(m_oldValue);
        } catch (DOMException e) {
            Log.log(Log.ERROR, this, e);
            throw new CannotUndoException();
        }
    }//}}}
    
    //{{{ redo()
    
    public void redo() throws CannotRedoException {
        super.redo();
        try {
            m_node.setLocalName(m_newValue);
        } catch (DOMException e) {
            Log.log(Log.ERROR, this, e);
            throw new CannotRedoException();
        }
    }//}}}
    
}