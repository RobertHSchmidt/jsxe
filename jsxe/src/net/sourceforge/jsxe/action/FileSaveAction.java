/*
FileSaveAction.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the action taken when the users selects
Save from the file menu.

This file written by ian Lewis (iml001@bridgewater.edu)
Copyright (C) 2002 ian Lewis

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
import net.sourceforge.jsxe.gui.DocumentPanel;
import net.sourceforge.jsxe.gui.TabbedView;
//}}}

//{{{ Swing components
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
//}}}

//{{{ AWT components
import java.awt.event.ActionEvent;
//}}}

//}}}

public class FileSaveAction extends AbstractAction {
    
    public FileSaveAction(TabbedView parent) {//{{{
        putValue(Action.NAME, "Save");
        putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl S"));
        view = parent;
    }//}}}
    
    public void actionPerformed(ActionEvent e) {//{{{
        DocumentPanel docpanel = view.getDocumentPanel();
        docpanel.getDOMAdapter().save();
    }//}}}
    
    /*
    *************************************************
    Data Fields
    *************************************************
    *///{{{
    private TabbedView view;
    //}}}
}
