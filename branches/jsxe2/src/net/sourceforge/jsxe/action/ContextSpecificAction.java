/*
ContextSpecificAction.java
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
import net.sourceforge.jsxe.ActionManager;
import net.sourceforge.jsxe.LocalizedAction;
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.gui.GUIUtilities;
//}}}

//{{{ Java classes
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
//}}}

//{{{ AWT components
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
//}}}

//}}}

/**
 * The ContextSpecificAction is a class that defines actions that are
 * context specific. i.e. Actions that are defined by jsXe but whose
 * implementation is determined by the context or component that
 * currently has focus.
 *
 * The ContextSpecificAction class allows components to be assocatied
 * with a specific action implementation that is specific to that context 
 * (component). When the ContextSpecificAction is run, the action will
 * search the registered components. If the component that has focus is owned
 * by registered component then the action implementation associated with
 * that component is then invoked.
 *
 * The order of the search is not specified and the first match that is found
 * will be invoked.
 *
 * Examples where this class may be useful is cut, copy, paste, insert, or 
 * delete where the implementation may depend on the components implementation.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre3
 */
public abstract class ContextSpecificAction extends LocalizedAction {
    
    //{{{ ContextSpecificAction constructor
    public ContextSpecificAction(String name) {
        super(name);
    }//}}}
    
    //{{{ invoke()
    /**
     * Invokes the specific ActionImplementation for the component in
     * the current context. This method should generally not be overridden by
     * subclasses.
     */
    public void invoke(TabbedView view, ActionEvent evt) {
        /*
        Invoke the action registered for the current component named
        */
        Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        Iterator itr = m_actionMap.keySet().iterator();
        while (itr.hasNext()) {
            Component key = (Component)itr.next();
            if (GUIUtilities.isComponentParentOf(key, comp)) {
                ActionImplementation imp = (ActionImplementation)m_actionMap.get(key);
                imp.invoke(view, key, evt);
                return;
            }
        }
    }//}}}
    
    //{{{ registerComponent()
    /**
     * Adds a component and implementation to the action map for this
     * action.
     * @param comp The component that will recieve the action
     * @param imp the action implementation that implements the action for the
     *            given component.
     */
    public void registerComponent(Component comp, ActionImplementation imp) {
        m_actionMap.put(comp, imp);
    }//}}}
    
    //{{{ removeComponent()
    /**
     * Removes the component from the action map for this action
     */
     public void removeComponent(Component comp) {
         m_actionMap.remove(comp);
    }//}}}
    
    //{{{ interface ActionImplementation
    /**
     * An ActionImplementation is an implementation of an action that can be
     * registered with a ContextSpecificAction and run in a context.
     */
    public static interface ActionImplementation {
        
        /**
         * This method is run when the ContextSpecificAction is run and
         * the registered component has focus.
         * @param view the view that invoked the action
         * @param comp the registered component
         * @param evt the event that triggered the action
         */
        public void invoke(TabbedView view, Component comp, ActionEvent evt);
        
    }//}}}
    
    //{{{ Private Members
    
    private HashMap m_actionMap = new HashMap();
    
    //}}}
}
