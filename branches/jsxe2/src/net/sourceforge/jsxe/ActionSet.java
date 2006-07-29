/*
ActionSet.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2001 Slava Pestov
Portions Copyright (C) 2004 Ian Lewis

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

import java.util.*;

/**
 * A set of actions.
 *
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.4 beta
 */
public class ActionSet {
   
   //{{{ ActionSet constructor
   /**
    * Creates a new action set.
    */
   public ActionSet() {
      this(null);
   }//}}}

   //{{{ ActionSet constructor
   /**
    * Creates a new action set.
    * @param label The label for the action set
    */
   public ActionSet(String label) {
      this.label = label;
      actions = new Hashtable();
   }//}}}

   //{{{ getLabel()
   /**
    * Return the action source label.
    */
   public String getLabel() {
      return label;
   }//}}}

   //{{{ setLabel()
   /**
    * Sets the action source label.
    * @param label The label
    */
   public void setLabel(String label) {
      this.label = label;
   }//}}}

   //{{{ addAction()
   /**
    * Adds an action to the action set. The action can
    * be retrieved via the <code>getName()</code> method of
    * the LocalizedAction.
    * @param action The action
    */
   public void addAction(LocalizedAction action) {
      actions.put(action.getName(),action);
   }//}}}
   
   //{{{ removeAction()
   /**
    * Removes an action from the action set.
    * @param name The internal action name
    */
   public void removeAction(String name) {
      actions.remove(name);
   }//}}}

   //{{{ removeAllActions()
   /**
    * Removes all actions from the action set.
    */
   public void removeAllActions() {
      actions.clear();
   }//}}}

   //{{{ getAction()
   /**
    * Returns the action with the specified name.
    * @param name The action name
    */
   public LocalizedAction getAction(String name) {
      return (LocalizedAction)actions.get(name);
   }//}}}

   //{{{ getActionCount()
   /**
    * Returns the number of actions in the set.
    */
   public int getActionCount() {
      return actions.size();
   }//}}}
   
   //{{{ getActionNames() method
	/**
	 * Returns an array of all action names in this action set.
	 * @since jsXe 0.5 pre1
	 */
	public String[] getActionNames() {
		String[] retVal = new String[actions.size()];
		Enumeration e = actions.keys();
		int i = 0;
		while(e.hasMoreElements()) {
			retVal[i++] = (String)e.nextElement();
		}
		return retVal;
	} //}}}
   
   //{{{ getActions()
   /**
    * Returns an array of all actions in this action set.
    */
   public LocalizedAction[] getActions() {
      LocalizedAction[] retVal = new LocalizedAction[actions.size()];
      Enumeration elements = actions.elements();
      int i = 0;
      while(elements.hasMoreElements()) {
         retVal[i++] = (LocalizedAction)elements.nextElement();
      }
      return retVal;
   }//}}}

   //{{{ contains()
   /**
    * Returns if this action set contains the specified action.
    * @param action The action
    */
   public boolean contains(LocalizedAction action) {
      return actions.contains(action);
   }//}}}

   //{{{ toString()
   /**
    * Returns getLabel()
    * @see ActionSet#getLabel()
    */
   public String toString() {
      return getLabel();
   }//}}}

   // package-private members
  // void getActions(Vector vec) {
  //    Enumeration enum = actions.elements();
  //    while(enum.hasMoreElements()) {
  //       vec.addElement(enum.nextElement());
  //    }
  // }

   //{{{ Private Members
   private String label;
   private Hashtable actions;
   //}}}
}
