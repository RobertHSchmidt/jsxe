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

//{{{ jsXe classes
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.gui.GUIUtilities;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.util.MiscUtilities;
import net.sourceforge.jsxe.msg.PropertyChanged;
//}}}

//{{{ Java classes
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
//}}}

//{{{ Swing classes
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
//}}}

//{{{ AWT classes
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
//}}}

//}}}

/**
 * <p>The ActionManager handles key bindings within jsXe. Key bindings can be
 * set editor wide via the shortcuts option pane in the global options dialog.</p>
 *
 * <p>While most actions are editor wide, some are editor wide but the
 * implementations are view specific. These include Cut/Copy/Paste/Find which
 * are common among different views thought their implementation will be
 * provided by the view. These actions will be named using the format
 * <i>viewname</i>.<i>actionname</i> (ex. <i>treeview</i>.<i>cut</i>).
 * these special actions will have a single key binding associated with them
 * but when invoked will activate view specific code provided by the plugin.</p>
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre1
 */
public class ActionManager {
    
    //{{{ Public static identifiers
    
    public static final String CUT_SUFFIX = ".cut";
    public static final String COPY_SUFFIX = ".copy";
    public static final String PASTE_SUFFIX = ".paste";
    public static final String FIND_SUFFIX = ".find";
    
    //}}}
    
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
                
                String dispName = editAction.getLabel();
                //TODO: add method for setting menu mnemonic from label
                
                String keyBinding = jsXe.getProperty(name+".shortcut");
                
                action.putValue(Action.NAME, dispName);
                
                if (keyBinding != null) {
                    action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyBinding));
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
        Log.log(Log.NOTICE, ActionManager.class, "Loading key bindings.");
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
                            if (keyBinding != null) {
                                addKeyBinding(keyBinding, actionName);
                            } else {
                                removeKeyBinding(msg.getOldValue());
                            }
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
                    if (keyBinding != null) {
                        addKeyBinding(keyBinding, actions[i]);
                        Log.log(Log.NOTICE, ActionManager.class, "Loaded key binding for "+actionName+": "+keyBinding);
                    }
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
            KeyStroke key = KeyStroke.getKeyStroke(keyBinding);
            m_keyBindingMap.put(key,wrapper);
            
            //need to do this so that the accelerator key is rendered on menu items
            wrapper.putValue(Action.ACCELERATOR_KEY, key);
        }
    }//}}}
    
	//{{{ removeKeyBinding()
	/**
	 * Removes a key binding.
	 * @param keyBinding The key binding
	 */
    public static void removeKeyBinding(String keyBinding) {
        removeKeyBinding(KeyStroke.getKeyStroke(keyBinding));
    }//}}}
    
	//{{{ removeAllKeyBindings()
	/**
	 * Removes all key bindings.
	 */
    public static void removeAllKeyBindings() {
        Iterator itr = m_keyBindingMap.keySet().iterator();
        while (itr.hasNext()) {
            removeKeyBinding((KeyStroke)itr.next());
        }
    }//}}}
    
    //{{{ handleKey()
    /**
     * Handles a key event. If the event matches any key bindings the
     * associated action is invoked.
     */
    public static void handleKey(KeyEvent event) {
        KeyStroke key = KeyStroke.getKeyStrokeForEvent(event);
        
        //Gets the action for the KeyStroke.
        Action action = (Action)m_keyBindingMap.get(key);
        if (action != null) {
            Log.log(Log.DEBUG, ActionManager.class, "Key mapping match for "+((Wrapper)action).getName());
            action.actionPerformed(translateKeyEvent(event));
            event.consume();
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
        
        //{{{ getName()
        public String getName() {
            return m_action.getName();
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
            action.invoke(GUIUtilities.getView((java.awt.Component)evt.getSource()), evt);
        }
    }//}}}
    
    //{{{ ActionManager constructor
    private ActionManager() {}//}}}
    
    //{{{ removeKeyBinding()
	/**
	 * Removes a key binding.
	 * @param keyBinding The key binding
	 */
    public static void removeKeyBinding(KeyStroke key) {
        Action action = (Action)m_keyBindingMap.get(key);
        if (action != null) {
            action.putValue(Action.ACCELERATOR_KEY, null);
            m_keyBindingMap.remove(key);
        }
    }//}}}
    
    //{{{ translateKeyEvent()
    public static ActionEvent translateKeyEvent(KeyEvent evt) {
        ActionEvent event = new ActionEvent(evt.getSource(),
                                            (int)ActionEvent.KEY_EVENT_MASK,
                                            "",
                                            evt.getModifiers());
        return event;
    }//}}}
    
    //{{{ isDocviewSpecific()
    public boolean isDocViewSpecific(String actionName) {
        return (actionName.endsWith(CUT_SUFFIX) ||
                actionName.endsWith(COPY_SUFFIX) ||
                actionName.endsWith(PASTE_SUFFIX) ||
                actionName.endsWith(FIND_SUFFIX));
    }//}}}
    
    //}}}
}
