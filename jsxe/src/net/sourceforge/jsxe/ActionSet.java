/*
ActionSet.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the class that manages the storing of actions. This file
was originally written by Slava Pestov for use with jEdit but
was modified for use with jsXe.

Copyright (C) 2001 Slava Pestov
Copyright (C) 2002 Ian Lewis

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
import javax.swing.Action;

/**
 * A set of actions.
 *
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class ActionSet {
   /**
    * Creates a new action set.
    */
   public ActionSet() {
      this(null);
   }

   /**
    * Creates a new action set.
    * @param label The label for the action set
    */
   public ActionSet(String label) {
      this.label = label;
      actions = new Hashtable();
   }

   /**
    * Return the action source label.
    */
   public String getLabel() {
      return label;
   }

   /**
    * Sets the action source label.
    * @param label The label
    */
   public void setLabel(String label) {
      this.label = label;
   }

   /**
    * Adds an action to the action set.
    * @param name the internal name for the action
    * @param action The action
    */
   public void addAction(String name, Action action) {
      actions.put(name,action);
   }

   /**
    * Removes an action from the action set.
    * @param name The internal action name
    */
   public void removeAction(String name) {
      actions.remove(name);
   }

   /**
    * Removes all actions from the action set.
    */
   public void removeAllActions() {
      actions.clear();
   }

   /**
    * Returns an action with the specified name.
    * @param name The action name
    */
   public Action getAction(String name) {
      return (Action)actions.get(name);
   }

   /**
    * Returns the number of actions in the set.
    */
   public int getActionCount() {
      return actions.size();
   }

   /**
    * Returns an array of all actions in this action set.
    */
   public Action[] getActions() {
      Action[] retVal = new Action[actions.size()];
      Enumeration enum = actions.elements();
      int i = 0;
      while(enum.hasMoreElements()) {
         retVal[i++] = (Action)enum.nextElement();
      }
      return retVal;
   }

   /**
    * Returns if this action set contains the specified action.
    * @param action The action
    */
   public boolean contains(Action action) {
      return actions.contains(action);
   }

   public String toString() {
      return label;
   }

   // package-private members
  // void getActions(Vector vec) {
  //    Enumeration enum = actions.elements();
  //    while(enum.hasMoreElements()) {
  //       vec.addElement(enum.nextElement());
  //    }
  // }

   // private members
   private String label;
   private Hashtable actions;
}
