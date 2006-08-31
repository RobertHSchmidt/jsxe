/*
LocalizedAction.java
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

package net.sourceforge.jsxe;

//{{{ imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.util.MiscUtilities;
//}}}

/**
 * An action that can be used by jsXe. These actions are registered
 * with jsXe through ActionSets. These ActionSets can be registered with
 * jsXe by plugins. Once an LocalizedAction is included in an ActionSet and
 * registered with jsXe via the <code>addActionSet()</code> method,
 * key bindings can be associated to the action via the InputManager.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see jsXe
 * @see ActionSet
 * @see ActionManager
 * @since jsXe 0.5 pre1
 */
public abstract class LocalizedAction {
        
    // {{{ Public static members
    /**
     * The internal name of the action used for retrieval from
     * <code>jsXe.getAction(String)</code> and for storing properties related
     * to the action.
     */
    public static final String INTERNAL_NAME = "internal-name";
    /**
     * The message name of the action used to retrieve the localized
     * human readable text for the action. The text is retrieved from
     * the net.sourceforge.jsxe.Messages class. The text determines the
     * menu key mnemonic as well.
     */
    public static final String MESSAGE_NAME = "message-name";
    /**
     * As an alternative to the MESSAGE_NAME an actual name can be used.
     * This should only be used in the case that the action's name is
     * not locale specific, such as a file name or url.
     */
    public static final String NAME = "name";
    /**
     * The message name for the localized tooltip message that
     * is associated with this action.
     */
    public static final String TOOLTIP_MESSAGE_NAME = "tooltip-message-name";
    //}}}
    
    //{{{ LocalizedAction constructor
    /**
     * Creates a new LocalizedAction that can be used with jsXe.
     * @param name the internal name of the action.
     */
    public LocalizedAction(String name) {
        m_name = name;
    }//}}}
    
    //{{{ invoke()
    /** 
     * The method that is run when the action is invoked.
     * @param view the view that invoked the action.
     * @param evt the ActionEvent for this event.
     */
    public abstract void invoke(TabbedView view, ActionEvent evt);//}}}
    
    //{{{ getName()
    /**
     * Gets the internal name for this action.
     * @
     */
    public String getName() { 
        return m_name;
    }//}}}
    
    //{{{ getLabel()
    /**
     * Gets the human readable localized label for this action.
     * This returns the value of the property named by
     * {@link #getName()} suffixed with <code>.label</code>.
     * @see net.sourceforge.jsxe.gui.Messages
     * @return the localized label
     */
    public String getLabel() {
        return Messages.getMessage(m_name+".label");
    }//}}}
    
    //{{{ Private members
    private String m_name;
    //}}}
}
