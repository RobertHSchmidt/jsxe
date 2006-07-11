/*
GrabKeyDialog.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2001, 2002 Slava Pestov
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

package net.sourceforge.jsxe.gui;

//{{{ Imports

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.util.Log;
//}}}

import javax.swing.border.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

//}}}

/**
 * A dialog for getting shortcut keys.
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre1
 */
public class GrabKeyDialog extends JDialog {
    
    //{{{ toString() method
    public static String toString(KeyEvent evt) {
        String id;
        switch (evt.getID()) {
            case KeyEvent.KEY_PRESSED:
                id = "KEY_PRESSED";
                break;
            case KeyEvent.KEY_RELEASED:
                id = "KEY_RELEASED";
                break;
            case KeyEvent.KEY_TYPED:
                id = "KEY_TYPED";
                break;
            default:
                id = "unknown type";
                break;
        }

        return id + ",keyCode=0x"
            + Integer.toString(evt.getKeyCode(),16)
            + ",keyChar=0x"
            + Integer.toString(evt.getKeyChar(),16)
            + ",modifiers=0x"
            + Integer.toString(evt.getModifiers(),16);
    } //}}}

    //{{{ GrabKeyDialog constructor
    /**
     * Create and show a new modal dialog.
     *
     * @param parent center dialog on this component.
     * @param binding the action/macro that should get a binding.
     * @param allBindings all other key bindings.
     * (may be null)
     */
    public GrabKeyDialog(Dialog parent, KeyBinding binding, Vector allBindings) {
        super(parent, Messages.getMessage("Grab.Key.title"),true);
        init(binding,allBindings);
    } //}}}

    //{{{ GrabKeyDialog constructor
    /**
     * Create and show a new modal dialog.
     *
     * @param parent center dialog on this component.
     * @param binding the action/macro that should get a binding.
     * @param allBindings all other key bindings.
     * (may be null)
     */
    public GrabKeyDialog(Frame parent, KeyBinding binding, Vector allBindings) {
        super(parent, Messages.getMessage("Grab.Key.title"),true);
        init(binding,allBindings);
    } //}}}

    //{{{ getShortcut() method
    /**
     * Returns the shortcut, or null if the current shortcut should be
     * removed or the dialog either has been cancelled. Use isOK()
     * to determine if the latter is true.
     */
    public String getShortcut() {
        if (isOK) {
            KeyEventTranslator.Key key = shortcut.getKey();
            if (key != null) {
                return key.getInternalShortcut();
            } else {
                return null;
            }
        } else {
            return null;
        }
    } //}}}

    //{{{ isOK() method
    /**
     * Returns true, if the dialog has not been cancelled.
     */
    public boolean isOK() {
        return isOK;
    } //}}}

    //{{{ isManagingFocus() method
    /**
     * Returns if this component can be traversed by pressing the
     * Tab key. This returns false.
     */
    public boolean isManagingFocus() {
        return false;
    } //}}}

    //{{{ getFocusTraversalKeysEnabled() method
    /**
     * Makes the tab key work in Java 1.4.
     */
    public boolean getFocusTraversalKeysEnabled() {
        return false;
    } //}}}

    //{{{ processKeyEvent() method
    protected void processKeyEvent(KeyEvent evt) {
        shortcut.processKeyEvent(evt);
    } //}}}

    //{{{ Private members

    //{{{ Instance variables
    private InputPane shortcut; // this is a bad hack
    private JLabel assignedTo;
    private JButton ok;
    private JButton remove;
    private JButton cancel;
    private JButton clear;
    private boolean isOK;
    private KeyBinding binding;
    private Vector allBindings;
    //}}}

    //{{{ init() method
    private void init(KeyBinding binding, Vector allBindings) {
        
        this.binding = binding;
        this.allBindings = allBindings;

        enableEvents(AWTEvent.KEY_EVENT_MASK);

        // create a panel with a BoxLayout. Can't use Box here
        // because Box doesn't have setBorder().
        JPanel content = new JPanel(new GridLayout(0,1,0,6)) {
            /**
             * Returns if this component can be traversed by pressing the
             * Tab key. This returns false.
             */
            public boolean isManagingFocus() {
                return false;
            }

            /**
             * Makes the tab key work in Java 1.4.
             */
            public boolean getFocusTraversalKeysEnabled() {
                return false;
            }
        };
        content.setBorder(new EmptyBorder(12,12,12,12));
        setContentPane(content);

        JLabel label = new JLabel(Messages.getMessage("Grab.Key.caption",new String[] { binding.label }));

        Box input = Box.createHorizontalBox();

        shortcut = new InputPane();
        input.add(shortcut);
        input.add(Box.createHorizontalStrut(12));

        clear = new JButton(Messages.getMessage("Grab.Key.clear"));
        clear.addActionListener(new ActionHandler());
        input.add(clear);

        assignedTo = new JLabel();
        updateAssignedTo(null);

        Box buttons = Box.createHorizontalBox();
        buttons.add(Box.createGlue());
        
        ok = new JButton(Messages.getMessage("common.ok"));
        ok.addActionListener(new ActionHandler());
        buttons.add(ok);
        buttons.add(Box.createHorizontalStrut(12));

        if (binding.isAssigned()) {
            // show "remove" button
            remove = new JButton(Messages.getMessage("Grab.Key.remove"));
            remove.addActionListener(new ActionHandler());
            buttons.add(remove);
            buttons.add(Box.createHorizontalStrut(12));
        }
        
        cancel = new JButton(Messages.getMessage("common.cancel"));
        cancel.addActionListener(new ActionHandler());
        buttons.add(cancel);
        buttons.add(Box.createGlue());

        content.add(label);
        content.add(input);
        content.add(assignedTo);
        content.add(buttons);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
        setVisible(true);
    } //}}}
    
    //{{{ updateAssignedTo() method
    private void updateAssignedTo(String shortcut) {
        
        String text = Messages.getMessage("Grab.Key.assigned-to.none");
        KeyBinding kb = getKeyBinding(shortcut);

        if (kb != null) {
           // if (kb.isPrefix) {
           //     text = Messages.getMessage("Grab.Key.assigned-to.prefix", new String[] { shortcut });
           // } else {
                text = kb.label;
           // }
        }
        
        if (ok != null) {
            ok.setEnabled(kb == null/* || !kb.isPrefix*/);
        }
        
        assignedTo.setText(Messages.getMessage("Grab.Key.assigned-to", new String[] { text }));
    } //}}}

    //{{{ getKeyBinding() method
    private KeyBinding getKeyBinding(String shortcut) {
        
        if (shortcut == null || shortcut.length() == 0) {
            return null;
        }
        
        Log.log(Log.DEBUG, this, "getting key binding for: "+shortcut);
        
       // String spacedShortcut = shortcut + " ";
        Enumeration e = allBindings.elements();

        while (e.hasMoreElements()) {
            
            KeyBinding kb = (KeyBinding)e.nextElement();
            
            if (!kb.isAssigned()) {
                continue;
            }
            
           // String spacedKbShortcut = kb.shortcut + " ";
            
            Log.log(Log.DEBUG, this, "searching "+kb.label+": "+kb.shortcut);
            
            // eg, trying to bind C+n C+p if C+n already bound
           // if (spacedShortcut.startsWith(spacedKbShortcut)) {
           //     return kb;
           // }
            
            if (shortcut.equals(kb.shortcut)) {
                return kb;
            }
            
            // eg, trying to bind C+e if C+e is a prefix
           // if (spacedKbShortcut.startsWith(spacedShortcut)) {
           //     // create a temporary (synthetic) prefix
           //     // KeyBinding, that won't be saved
           //     return new KeyBinding(kb.name,kb.label,shortcut);
           // }
        }

        return null;
    } //}}}

    //}}}

    //{{{ KeyBinding class
    /**
     * A jsXe key binding
     */
    public static class KeyBinding {
        
        /**
         * Creates a new key binding for use with the GrabKeyDialog.
         * @param name The internal name of the action.
         * @param label The human readable label for the action
         * @param shortcut The internal shortcut for the keybinding
         */
        public KeyBinding(String name, String label, String shortcut/*, boolean isPrefix*/) {
            this.name = name;
            this.label = label;
            this.shortcut = shortcut;
           // this.isPrefix = isPrefix;
        }
        
        public String name;
        public String label;
        public String shortcut;
       // public boolean isPrefix;

        public boolean isAssigned() {
            return shortcut != null && shortcut.length() > 0;
        }
    } //}}}

    //{{{ InputPane class
    private class InputPane extends JTextField {
        //{{{ getFocusTraversalKeysEnabled() method
        /**
         * Makes the tab key work in Java 1.4.
         */
        public boolean getFocusTraversalKeysEnabled() {
            return false;
        } //}}}

        //{{{ processKeyEvent() method
        protected void processKeyEvent(KeyEvent _evt) {
            KeyEvent evt = KeyEventWorkaround.processKeyEvent(_evt);
            Log.log(Log.DEBUG, this, "Event " + GrabKeyDialog.toString(_evt) + (evt == null ? " filtered\n" : " passed\n"));

            if (evt == null) {
                return;
            }

            evt.consume();
            
            KeyEventTranslator.Key key = KeyEventTranslator.translateKeyEvent(evt);
            
            if (key == null) {
                return;
            }
            
            Log.log(Log.DEBUG, this, "==> Translated to " + 
                    ((key.modifiers == 0 ? "" : KeyEventTranslator.modifiersToString(key.modifiers))
                    + "<"
                    + Integer.toString(key.key,16)
                    + ","
                    + Integer.toString(key.input,16)
                    + ">")
                + "\n");

            String internalShortcut = key.getInternalShortcut();
            if (internalShortcut == null) {
                return;
            }

            setText(key.toString());
            updateAssignedTo(internalShortcut);
            m_key = key;
        } //}}}
        
        //{{{ getKey()
        
        public KeyEventTranslator.Key getKey() {
            return m_key;
        }//}}}
        
        private KeyEventTranslator.Key m_key = null;
    } //}}}

    //{{{ ActionHandler class
    class ActionHandler implements ActionListener {
        
        //{{{ actionPerformed() method
        public void actionPerformed(ActionEvent evt) {
            
            if (evt.getSource() == ok) {
                if (canClose()) {
                    dispose();
                }
            } else if(evt.getSource() == remove) {
                shortcut.setText(null);
                isOK = true;
                dispose();
            } else if(evt.getSource() == cancel) {
                dispose();
            } else if(evt.getSource() == clear) {
                shortcut.setText(null);
                updateAssignedTo(null);
                shortcut.requestFocus();
            }
        } //}}}

        //{{{ canClose() method
        private boolean canClose() {
            
            String shortcutString = shortcut.getText();
            if (shortcutString.length() == 0 && binding.isAssigned()) {
                // ask whether to remove the old shortcut
                int answer = GUIUtilities.confirm(
                    GrabKeyDialog.this,
                    "Grab.Key.remove-ask",
                    null,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    shortcut.setText(null);
                    isOK = true;
                } else {
                    return false;
                }
            }

            // check whether this shortcut already exists
            KeyBinding other = getKeyBinding(shortcutString);
            if (other == null || other == binding) {
                isOK = true;
                return true;
            }

            // check whether the other shortcut is the alt. shortcut
            if (other.name == binding.name) {
                // we don't need two identical shortcuts
                GUIUtilities.error(GrabKeyDialog.this,
                    "Grab.Key.duplicate-alt-shortcut",
                    null);
                return false;
            }

            // check whether shortcut is a prefix to others
           // if (other.isPrefix) {
           //     // can't override prefix shortcuts
           //     GUIUtilities.error(GrabKeyDialog.this,
           //         "Grab.Key.prefix-shortcut",
           //         null);
           //     return false;
           // }

            // ask whether to override that other shortcut
            int answer = GUIUtilities.confirm(GrabKeyDialog.this,
                "Grab.Key.duplicate-shortcut",
                new Object[] { other.label },
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                if(other.shortcut != null && shortcutString.startsWith(other.shortcut)) {
                    other.shortcut = null;
                }
                isOK = true;
                return true;
            } else {
                return false;
            }
        } //}}}
        
    } //}}}
}
