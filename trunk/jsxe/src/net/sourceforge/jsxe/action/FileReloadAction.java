/*
FileReloadAction.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the action taken when a user selects
reload from the file menu.

This file written by Ian Lewis (IanLewis@member.fsf.org)
Copyright (C) 2002 Ian Lewis

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

package net.sourceforge.jsxe.action;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.gui.view.DocumentView;
//}}}

//{{{ Java classes
import java.io.IOException;
//}}}

//{{{ Swing components
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.JOptionPane;
//}}}

//{{{ AWT components
import java.awt.event.ActionEvent;
//}}}

//}}}

public class FileReloadAction extends AbstractAction {
    
    //{{{ FileReloadAction constructor
    
    public FileReloadAction(TabbedView parent) {
        putValue(Action.NAME, "Reload");
        putValue(Action.MNEMONIC_KEY, new Integer(KeyStroke.getKeyStroke("R").getKeyCode()));
        m_view = parent;
    }//}}}
    
    //{{{ actionPerformed()
    
    public void actionPerformed(ActionEvent e) {
        try {
            DocumentView documentView = m_view.getDocumentView();
            DocumentBuffer buffer = documentView.getDocumentBuffer();
            buffer.reload(m_view);
            documentView.setDocumentBuffer(m_view, buffer); //reload the buffer in the documentView
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(m_view, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
        }
    }//}}}
    
    //{{{ Private members
    private TabbedView m_view;
    //}}}
}
