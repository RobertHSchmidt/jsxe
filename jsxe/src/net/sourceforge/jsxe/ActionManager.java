/*
ActionManager.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe;

//{{{ imports
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.util.MiscUtilities;
import net.sourceforge.jsxe.msg.PropertyChanged;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
//}}}

/**
 * The ActionManager handles key bindings within jsXe.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre1
 */
public class ActionManager {
    
    //{{{ addActionSet()
    /**
     * Adds a set of actions to the jsXe's pool of action sets.
     * This allows action sets from installed plugins to be added
     * and retrieved via jsXe's pool of actions.
     * @param set the action set to add
     */
    public static void addActionSet(ActionSet set) {
        m_actionSets.add(set);
    }//}}}
    
    //{{{ getLocalizedAction()
    /**
     * Gets the LocalizedAction set with the given name
     * @param the name of the action set.
     * @return the action set that matches the name, or null if none match.
     */
    public static LocalizedAction getLocalizedAction(String name) {
        for (int i = 0; i < m_actionSets.size(); i++) {
            LocalizedAction action = ((ActionSet)m_actionSets.get(i)).getAction(name);
            if (action != null) {
                return action;
            }
        }
        Log.log(Log.WARNING,ActionManager.class,"Unknown action: "+ name);
        return null;
    }//}}}
    
    //{{{ getAction()
    /**
     * Gets a true action for the LocalizedAction with the given name. This can be
     * used in menus and toobars etc.
     * @param name the name of the action.
     */
    public static Action getAction(String name) {
        Action action = (Action)m_actionMap.get(name);
        if (action == null) {
            LocalizedAction editAction = getLocalizedAction(name);
            if (editAction != null) {
                action = new Wrapper(name);
                
                if (editAction == null) {
                    String dispName = editAction.getLabel();
                    String keyBinding = jsXe.getProperty(name+".shortcut");
                    
                    action.putValue(Action.NAME, dispName);
                    
                    if (keyBinding != null) {
                        action.putValue(Action.ACCELERATOR_KEY, keyBinding);
                    }
                }
                
                m_actionMap.put(name, action);
            } else {
                Log.log(Log.WARNING,ActionManager.class,"Unknown action: "+ name);
            }
        }
        return action;
    }//}}}
    
    //{{{ getActionSets()
    /**
     * Gets all action sets that have been registered with jsXe
     * @return an ArrayList of ActionSet objects
     */
    public static ArrayList getActionSets() {
        return m_actionSets;
    }//}}}
    
    //{{{ invokeAction()
    /**
     * Invokes the action with the given name.
     * @param name the internal name of the action
     */
    public static void invokeAction(String name, ActionEvent evt) {
        invokeAction(getLocalizedAction(name), evt);
    }//}}}
    
    //{{{ initKeyBindings()
    /**
     * Initialized the key bindings for jsXe. This method is called
     * at startup after plugins are loaded. Subsequent calls will 
     * do nothing.
     */
    public static void initKeyBindings() {
        if (!initialized) {
            
            //Add EditBus Listener to update key bindings when properties are changed
            EditBus.addToBus(new EBListener() {

                //{{{ handleMessage()
                public void handleMessage(EBMessage message) {
                    if (message instanceof PropertyChanged) {
                        PropertyChanged msg = (PropertyChanged)message;
                        if (msg.getKey().endsWith(".shortcut")) {
                            String actionName = msg.getKey().substring(0, msg.getKey().lastIndexOf("."));
                            String keyBinding = jsXe.getProperty(msg.getKey());
                            addKeyBinding(keyBinding, actionName);
                        }
                    }
                }//}}}
                
            });
            
            Iterator itr = m_actionSets.iterator();
            while (itr.hasNext()) {
                ActionSet set = (ActionSet)itr.next();
                LocalizedAction[] actions = set.getActions();
                for (int i=0; i<actions.length; i++) {
                    String actionName = actions[i].getName();
                    String keyBinding = jsXe.getProperty(actionName+".shortcut");
                    addKeyBinding(keyBinding, actions[i]);
                }
            }
            
            initialized=true;
        }
    }//}}}
    
	//{{{ addKeyBinding()
	/**
	 * Adds a key binding to jsXe.
	 * @param keyBinding The key binding
	 * @param action The action name
	 */
     public static void addKeyBinding(String keyBinding, String action) {
         addKeyBinding(keyBinding, getLocalizedAction(action));
     }//}}}
     
	//{{{ addKeyBinding()
	/**
	 * Adds a key binding to this input handler.
	 * @param keyBinding The key binding
	 * @param action The action
	 */
    public static void addKeyBinding(String keyBinding, LocalizedAction action) {
        if (action != null && keyBinding != null) {
            Action wrapper = getAction(action.getName());
            m_keyBindingMap.put(keyBinding,wrapper);
            wrapper.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyBinding));
        }
    }//}}}
    
	//{{{ removeKeyBinding()
	/**
	 * Removes a key binding.
	 * @param keyBinding The key binding
	 */
    public static void removeKeyBinding(String keyBinding) {
        Action action = (Action)m_keyBindingMap.get(keyBinding);
        if (action != null) {
            action.putValue(Action.ACCELERATOR_KEY, null);
            m_keyBindingMap.remove(keyBinding);
        }
    }//}}}
    
	//{{{ removeAllKeyBindings()
	/**
	 * Removes all key bindings.
	 */
    public static void removeAllKeyBindings() {
        Iterator itr = m_keyBindingMap.keySet().iterator();
        while (itr.hasNext()) {
            removeKeyBinding(itr.next().toString());
        }
    }//}}}
    
    //{{{ Wrapper class
    /**
     * The Wrapper class wraps LocalizedActions so they can be invoked
     * through Swing via the ActionListener interface.
     */
    public static class Wrapper extends AbstractAction {
        
        //{{{ Wrapper constructor
        /**
         * Creates a new wrapper action.
         * @param name the name of the registered action to wrap
         */
        public Wrapper(String name) {
            this(getLocalizedAction(name));
        }//}}}
        
        //{{{ Wrapper constructor
        /**
         * Creates a new wrapper action.
         * @param name the name of the registered action to wrap
         */
        public Wrapper(LocalizedAction action) {
            m_action = action;
            putValue(AbstractAction.NAME, action.getLabel());
        }//}}}
        
        //{{{ actionPerformed()
        public void actionPerformed(ActionEvent evt) {
            invokeAction(m_action, evt);
        }//}}}
        
        //{{{ Private members
        private LocalizedAction m_action;
        //}}}
        
    }//}}}
    
    //{{{ Private Members
    /**
     * This is a key binding to Wrapper mapping.
     */
    private static HashMap m_keyBindingMap = new HashMap();
    /**
     * This is an name to Wrapper mapping.
     */
    private static HashMap m_actionMap = new HashMap();
    private static ArrayList m_actionSets = new ArrayList();
    
    private static boolean initialized = false;
    
    //{{{ invokeAction()
    private static void invokeAction(LocalizedAction action, ActionEvent evt) {
        if (action != null) {
            Log.log(Log.MESSAGE, ActionManager.class, "Invoking action "+action.getName());
            action.invoke((TabbedView)MiscUtilities.getComponentParent((java.awt.Component)evt.getSource(), TabbedView.class), evt);
        }
    }//}}}
    
    //{{{ ActionManager constructor
    private ActionManager() {}//}}}
    
    //}}}
}
