/*
RemoveEdit.java
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
import net.sourceforge.jsxe.dom.XMLDocument;
//}}}

//{{{ Swing classes
import javax.swing.undo.*;
//}}}

import java.io.IOException;

//}}}

/**
 * An undoable edit sigifying a removal from an XMLDocument.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see net.sourceforge.jsxe.dom.XMLDocument
 */
public class RemoveEdit extends AbstractUndoableEdit {
    
    private XMLDocument m_document;
    private String m_text;
    private int m_offset;
    
    //{{{ RemoveEdit constructor
    
    public RemoveEdit(XMLDocument document, int offset, String text) {
        m_document = document;
        m_offset = offset;
        m_text = text;
    }//}}}
    
    //{{{ undo()
    
    public void undo() throws CannotUndoException {
        super.undo();
        try {
            m_document.insertText(m_offset, m_text);
        } catch (IOException ioe) {
            throw new CannotUndoException();
        }
    }//}}}
    
    //{{{ redo()
    
    public void redo() throws CannotRedoException {
        super.redo();
        try {
            m_document.removeText(m_offset, m_text.length());
        } catch (IOException ioe) {
            throw new CannotUndoException();
        }
    }//}}}
    
}