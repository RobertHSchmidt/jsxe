/*
CutNodeAction.java
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

package treeview.action;

//{{{ imports

import treeview.*;

//{{{ AWT classes
import java.awt.event.ActionEvent;
import java.awt.Component;
//}}}

//{{{ Swing classes
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.JOptionPane;
//}}}

//{{{ DOM classes
import org.w3c.dom.DOMException;
//}}}

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.action.ContextSpecificAction;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.dom.AdapterNode;
import net.sourceforge.jsxe.util.Log;
//}}}

//}}}

/**
 * An action that cuts the currently selected node out of the tree and places
 * it into the clipboard.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class CutNodeAction implements ContextSpecificAction.ActionImplementation {
    
    //{{{ invoke()
    public void invoke(TabbedView view, Component comp, ActionEvent evt) {
        DocumentView docView = view.getDocumentView();
        if (docView instanceof DefaultView) {
            DefaultView defView = (DefaultView)docView;
            TreeViewTree tree = defView.getTree();
            try {
                tree.cut();
            } catch (DOMException dome) {
                JOptionPane.showMessageDialog(tree, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//}}}
    
}
