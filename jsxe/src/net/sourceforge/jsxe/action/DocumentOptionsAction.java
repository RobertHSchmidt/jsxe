/*
DocumentOptionsAction.java
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

package net.sourceforge.jsxe.action;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.LocalizedAction;
import net.sourceforge.jsxe.gui.DocumentOptionsDialog;
import net.sourceforge.jsxe.gui.TabbedView;
//}}}

//{{{ AWT components
import java.awt.event.ActionEvent;
//}}}

//}}}

/**
 * The action that is executed when the user selects 'Document Options' from
 * the tools menu.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class DocumentOptionsAction extends LocalizedAction {
    
    //{{{ DocumentOptionsAction constructor
    public DocumentOptionsAction() {
    	super("document-options");
    }//}}}
    
    //{{{ invoke()
    public void invoke(TabbedView view, ActionEvent evt) {
        new DocumentOptionsDialog(view);
    }//}}}
    
}
