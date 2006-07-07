/*
FileSaveAsAction.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the action taken when a user selects
Save As... from the file menu.

Copyright (C) 2002 Ian Lewis (IanLewis@member.fsf.org)
Portions Copyright (C) 2005 Trish Harnett (trishah136@member.fsf.org)

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

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.LocalizedAction;
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.gui.Messages;
//}}}

//{{{ Swing components
import javax.swing.JOptionPane;
//}}}

//{{{ AWT components
import java.awt.event.ActionEvent;
//}}}

//{{{ Java base classes
import java.io.IOException;
//}}}

//}}}

/**
 * The action that is executed when the user selects 'save as' in the file menu.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @author Trish Hartnett (<a href="mailto:trishah136@member.fsf.org">trishah136@member.fsf.org</a>)
 * @version $Id$
 */
public class FileSaveAsAction extends LocalizedAction {
    
    //{{{ FileSaveAsAction constructor
    public FileSaveAsAction() {
        super("save-as");
    }//}}}
    
    //{{{ invoke()
    public void invoke(TabbedView view, ActionEvent evt) {
        try {
            view.getDocumentBuffer().saveAs(view);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(view, ioe, Messages.getMessage("IO.Error.title"), JOptionPane.WARNING_MESSAGE);
        }
    }//}}}
    
}
   
