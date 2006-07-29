/*
ActivityLogAction.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2005 Trish Harnett (trishah136@member.fsf.org)
Portions Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)

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
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.gui.ActivityLogDialog;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ AWT components
import java.awt.event.ActionEvent;
//}}}

//}}}

/**
 * The action that is executed when the clicks on the Activity Log
 * option under the Help Menu
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @author Trish Hartnett (<a href="mailto:trishah136@member.fsf.org">trishah136@member.fsf.org</a>)
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class ActivityLogAction extends LocalizedAction {
    
    //{{{ Private Members
    private ActivityLogDialog m_dialog;
    //}}}
    
    //{{{ ActivityLogAction constructor 
     /**
     * Constructor for the ActivityLogActionclass
     * @since jsXe 0.3pre15
     */
    public ActivityLogAction() {
        super("activity-log");
    }//}}}
    
    //{{{ invoke()
    public void invoke(TabbedView view, ActionEvent evt) {
        if (m_dialog == null) {
            m_dialog = new ActivityLogDialog(view);
        }
        m_dialog.refreshContents();
        m_dialog.setVisible(true);  
    }//}}}
}
