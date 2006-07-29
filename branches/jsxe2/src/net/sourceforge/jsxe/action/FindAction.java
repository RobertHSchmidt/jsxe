/*
FindAction.java
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
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.JARClassLoader;
import net.sourceforge.jsxe.ActionManager;
import net.sourceforge.jsxe.LocalizedAction;
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.gui.Messages;
//}}}

//{{{ Java classes
import java.io.IOException;
//}}}

//{{{ AWT components
import java.awt.event.ActionEvent;
//}}}

//}}}

/**
 * The find action invokes a DocumentView specific action defined for find.
 * The action should be defined by the view as <i>viewname</i>.find
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre1
 */
public class FindAction extends LocalizedAction {
    
    //{{{ FindAction constructor
    public FindAction() {
        super("find");
    }//}}}
    
    //{{{ invoke()
    public void invoke(TabbedView view, ActionEvent evt) {
        /*
        invoke the action registered for the current DocumentView named
        viewname.find if there is one.
        */
        ActionManager.invokeAction(jsXe.getPluginLoader().getPluginProperty(view.getDocumentView().getViewPlugin(), JARClassLoader.PLUGIN_NAME)+".find", evt);
    }//}}}

}
