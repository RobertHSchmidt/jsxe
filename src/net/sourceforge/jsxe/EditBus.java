/*
EditBus.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)
Copyright (C) 1999 Slava Pestov

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
import net.sourceforge.jsxe.util.Log;

/**
 * jsXe's global event notification mechanism.<p>
 *
 * Plugins register with the EditBus to receive messages reflecting
 * changes in the application's state, including changes in buffers,
 * views and edit panes, changes in the set of properties maintained
 * by the application, and the closing of the application.<p>
 *
 * The EditBus maintains a list of objects that have requested to receive
 * messages. When a message is sent using this class, all registered
 * components receive it in turn. Classes for objects that subscribe to
 * the EditBus must implement the {@link EBListener} interface, which
 * defines the single method {@link EBListener#handleMessage(EBMessage)}.<p>
 *
 * Any plugin core class whose name ends with <code>Plugin</code> (for
 * identification purposes) will automatically be
 * added to the EditBus during jsXe's startup routine.  Any other
 * class - for example, a dockable window that needs to receive
 * notification of buffer changes - must perform its own registration by calling
 * {@link #addToBus(EBListener)} during its initialization.
 * A convenient place to register in a class derived from
 * <code>JComponent</code> would be in an implementation of the
 * <code>JComponent</code> method<code>addNotify()</code>.<p>
 *
 * Message types sent by jsXe can be found in the
 * {@link net.sourceforge.jsxe.msg} package.<p>
 *
 * Plugins can also send their own messages - any object can send a message to
 * the EditBus by calling the static method {@link #send(EBMessage)}.
 * Most plugins, however, only concern themselves with receiving, not
 * sending, messages.
 *
 * @see net.sourceforge.jsxe.EBListener
 * @see net.sourceforge.jsxe.EBMessage
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @author Slava Pestov
 * @author John Gellene (API documentation)
 * @version $Id$
 *
 * @since jsXe 0.5 pre1
 */
public class EditBus {
    
    //{{{ addToBus() method
    /**
     * Adds a component to the bus. It will receive all messages sent
     * on the bus.
     *
     * @param comp The component to add
     */
    public static void addToBus(EBListener comp) {
        synchronized(components) {
            components.add(comp);
            copyComponents = null;
        }
    } //}}}

    //{{{ removeFromBus() method
    /**
     * Removes a component from the bus.
     * @param comp The component to remove
     */
    public static void removeFromBus(EBListener comp) {
        synchronized(components) {
            components.remove(comp);
            copyComponents = null;
        }
    } //}}}

    //{{{ getComponents() method
    /**
     * Returns an array of all components connected to the bus.
     */
    public static EBListener[] getComponents() {
        synchronized(components) {
            if (copyComponents == null) {
                copyComponents = (EBListener[])components.toArray(
                    new EBListener[components.size()]);
            }
            return copyComponents;
        }
    } //}}}

    //{{{ send() method
    /**
     * Sends a message to all components on the bus in turn.
     * @param message The message
     */
    public static void send(EBMessage message) {
        Log.log(Log.MESSAGE,EditBus.class,message.toString());

        // To avoid any problems if components are added or removed
        // while the message is being sent
        EBListener[] comps = getComponents();

        for(int i = 0; i < comps.length; i++) {
            try {
                EBListener comp = comps[i];
                comps[i].handleMessage(message);
               // if (Debug.EB_TIMER) {
               //     long start = System.currentTimeMillis();
               //     comp.handleMessage(message);
               //     long time = (System.currentTimeMillis() - start);
               //     if (time != 0) {
               //         Log.log(Log.DEBUG,EditBus.class,comp + ": " + time + " ms");
               //     }
               // } else {
               //     comps[i].handleMessage(message);
               // }
            } catch(Throwable t) {
                Log.log(Log.ERROR,EditBus.class,"Exception while sending message on EditBus:");
                Log.log(Log.ERROR,EditBus.class, t);
            }
        }
    } //}}}

    //{{{ Private members
    private static ArrayList components = new ArrayList();
    private static EBListener[] copyComponents;

    // can't create new instances
    private EditBus() {}
    //}}}
}
