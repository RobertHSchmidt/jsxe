/*
InputManager.java
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
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.util.HashMap;
import java.util.Iterator;
//}}}

/**
 * The InputManager handles the key bindings for Actions within jsXe.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre1
 */
public class InputManager {
    
	//{{{ addKeyBinding() method
	/**
	 * Adds a key binding to jsXe.
	 * @param keyBinding The key binding
	 * @param action The action
	 */
     public static void addKeyBinding(String keyBinding, String action) {
         addKeyBinding(keyBinding, jsXe.getAction(action));
     }//}}}

	//{{{ addKeyBinding() method
	/**
	 * Adds a key binding to this input handler.
	 * @param keyBinding The key binding
	 * @param action The action
	 */
    public static void addKeyBinding(String keyBinding, Action action) {
        if (action != null) {
            m_keyBindingMap.put(keyBinding, action);
            action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(keyBinding));
            jsXe.setProperty(action.getValue(Action.NAME)+".shortcut", keyBinding);
        }
    }//}}}

	//{{{ removeKeyBinding() method
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

	//{{{ removeAllKeyBindings() method
	/**
	 * Removes all key bindings.
	 */
    public static void removeAllKeyBindings() {
        Iterator itr = m_keyBindingMap.keySet().iterator();
        while (itr.hasNext()) {
            removeKeyBinding(itr.next().toString());
        }
        m_keyBindingMap = new HashMap();
    }//}}}
    
    //{{{ Private Members
    
    //{{{ InputManager constructor
    private InputManager() {}//}}}
    
    private static HashMap m_keyBindingMap = new HashMap();
    
    //}}}
}
