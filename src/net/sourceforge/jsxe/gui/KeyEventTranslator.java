/*
KeyEventTranslator.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2003 Slava Pestov
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
import java.awt.event.*;
import java.awt.Toolkit;
import java.util.*;
import java.lang.reflect.Field;
import javax.swing.KeyStroke;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.util.MiscUtilities;
import net.sourceforge.jsxe.OperatingSystem;
//}}}

/**
 * In conjunction with the <code>KeyEventWorkaround</code>, hides some 
 * warts in the AWT key event API.
 *
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @since jsXe 0.5 pre1
 * @version $Id: KeyEventTranslator.java,v 1.23 2004/07/12 19:25:07 spestov Exp $
 */
public class KeyEventTranslator {
    
    //{{{ addTranslation()
    /**
     * Adds a keyboard translation.
     * @param key1 Translate this key
     * @param key2 Into this key
     */
    public static void addTranslation(Key key1, Key key2) {
        transMap.put(key1,key2);
    } //}}}

    //{{{ translateKeyEvent()
    /**
     * Pass this an event from {@link
     * KeyEventWorkaround#processKeyEvent(java.awt.event.KeyEvent)}.
     */
    public static Key translateKeyEvent(KeyEvent evt) {
        int modifiers = evt.getModifiers();
        Key returnValue = null;
        
        switch(evt.getID()) {
            case KeyEvent.KEY_PRESSED:
                int keyCode = evt.getKeyCode();
                if ((keyCode >= KeyEvent.VK_0
                    && keyCode <= KeyEvent.VK_9)
                    || (keyCode >= KeyEvent.VK_A
                    && keyCode <= KeyEvent.VK_Z))
                {
                    if (OperatingSystem.isMacOS()) {
                        return null;
                    } else {
                        returnValue = new Key(modifiers, '\0', Character.toLowerCase((char)keyCode));
                    }
                } else {
                    
                    if (keyCode == KeyEvent.VK_TAB) {
                        evt.consume();
                        returnValue = new Key(modifiers, keyCode,'\0');
                    } else if(keyCode == KeyEvent.VK_SPACE) {
                        // for SPACE or S+SPACE we pass the
                        // key typed since international
                        // keyboards sometimes produce a
                        // KEY_PRESSED SPACE but not a
                        // KEY_TYPED SPACE, eg if you have to
                        // do a "<space> to insert ".
                        if ((modifiers & ~InputEvent.SHIFT_MASK) == 0) {
                            returnValue = null;
                        } else {
                            returnValue = new Key(modifiers, 0,' ');
                        }
                    } else {
                        returnValue = new Key(modifiers, keyCode,'\0');
                    }
                }
                break;
            case KeyEvent.KEY_TYPED:
                char ch = evt.getKeyChar();
    
                switch(ch) {
                    case '\n':
                    case '\t':
                    case '\b':
                        return null;
                    case ' ':
                        if ((modifiers & ~InputEvent.SHIFT_MASK) != 0) {
                            return null;
                        }
                }
    
                int ignoreMods;
                if (OperatingSystem.isMacOS()) {
                    /* on MacOS, A+ can be user input */
                    ignoreMods = (InputEvent.SHIFT_MASK
                        | InputEvent.ALT_GRAPH_MASK
                        | InputEvent.ALT_MASK);
                } else {
                    /* on MacOS, A+ can be user input */
                    ignoreMods = (InputEvent.SHIFT_MASK
                        | InputEvent.ALT_GRAPH_MASK);
                }
    
                if((modifiers & InputEvent.ALT_GRAPH_MASK) == 0
                    && evt.getWhen()
                    -  KeyEventWorkaround.lastKeyTime < 750
                    && (KeyEventWorkaround.modifiers & ~ignoreMods)
                    != 0)
                {
                    if(OperatingSystem.isMacOS()) {
                        returnValue = new Key(modifiers, 0, ch);
                    } else {
                        return null;
                    }
                } else {
                    if (ch == ' ') {
                        returnValue = new Key(modifiers, 0, ch);
                    } else {
                        returnValue = new Key(0, 0, ch);
                    }
                }
                break;
            default:
                return null;
        }

        /* I guess translated events do not have the 'evt' field set
        so consuming won't work. I don't think this is a problem as
        nothing uses translation anyway */
        Key trans = (Key)transMap.get(returnValue);
        if(trans == null) {
            return returnValue;
        } else {
            return trans;
        }
    } //}}}
    
    //{{{ parseModifiers()
    /**
     * Parses the keyStroke and returns the InputEvent modifiers
     */
    public static int parseModifiers(String keyStroke) {
        int modifiers = 0;
        int index = keyStroke.indexOf('+');
        if (index != -1) {
            for(int i = 0; i < index; i++) {
                switch(Character.toUpperCase(keyStroke.charAt(i))) {
                    case 'A':
                        modifiers |= a;
                        break;
                    case 'C':
                        modifiers |= c;
                        break;
                    case 'M':
                        modifiers |= m;
                        break;
                    case 'S':
                        modifiers |= s;
                        break;
                }
            }
        }
        return modifiers;
    }//}}}
    
    //{{{ parseKey()
    /**
     * Converts a string to a keystroke. The string should be of the
     * form <i>modifiers</i>+<i>shortcut</i> where <i>modifiers</i>
     * is any combination of A for Alt, C for Control, S for Shift
     * or M for Meta, and <i>shortcut</i> is either a single character,
     * or a keycode name from the <code>KeyEvent</code> class, without
     * the <code>VK_</code> prefix.
     * @param keyStroke A string description of the key stroke
     */
    public static Key parseKey(String keyStroke) {
        if (keyStroke == null) {
            return null;
        }
        
        int modifiers = parseModifiers(keyStroke);
        
        int index = keyStroke.indexOf('+');
        String key = keyStroke.substring(index + 1);
        
        if (key.length() == 1) {
            return new Key(modifiers,0,key.charAt(0));
        
        } else {
            if(key.length() == 0) {
                Log.log(Log.ERROR, KeyEventTranslator.class, "Invalid key stroke: " + keyStroke);
                return null;
            
            } else {
                if (key.equals("SPACE")) {
                    return new Key(modifiers,0,' ');
                } else {
                    int ch;
    
                    try {
                        ch = KeyEvent.class.getField("VK_".concat(key))
                            .getInt(null);
                    } catch(Exception e) {
                        Log.log(Log.ERROR,KeyEventTranslator.class,
                            "Invalid key stroke: "
                            + keyStroke);
                        return null;
                    }
    
                    return new Key(modifiers,ch,'\0');
                }
            }
        }
    } //}}}
    
    //{{{ getKeyStroke()
    /**
     * Gets a Swing KeyStroke representing the internal key binding. 
     */
    public static KeyStroke getKeyStroke(String keyStroke) {
        
        Key key = parseKey(keyStroke);
        
        int keyCode;
        if (key.input != '\0') {
            return KeyStroke.getKeyStroke(new Character(key.input), key.modifiers);
        } else {
            return KeyStroke.getKeyStroke(key.key, key.modifiers);
        }
    }//}}}
    
    //{{{ setModifierMapping()
    /**
     * Changes the mapping between symbolic modifier key names
     * (<code>C</code>, <code>A</code>, <code>M</code>, <code>S</code>) and
     * Java modifier flags.
     *
     * You can map more than one Java modifier to a symobolic modifier, for 
     * example :
     * <p><code><pre>
     *  setModifierMapping(
     *      InputEvent.CTRL_MASK,
     *      InputEvent.ALT_MASK | InputEvent.META_MASK,
     *      0,
     *      InputEvent.SHIFT_MASK);
     *<pre></code></p>
     *
     * You cannot map a Java modifer to more than one symbolic modifier.
     *
     * @param c The modifier(s) to map the <code>C</code> modifier to
     * @param a The modifier(s) to map the <code>A</code> modifier to
     * @param m The modifier(s) to map the <code>M</code> modifier to
     * @param s The modifier(s) to map the <code>S</code> modifier to
     *
     */
    public static void setModifierMapping(int c, int a, int m, int s)
    {
    
        int duplicateMapping = 
            ((c & a) | (c & m) | (c & s) | (a & m) | (a & s) | (m & s)); 
        
        if((duplicateMapping & InputEvent.CTRL_MASK) != 0) {
            throw new IllegalArgumentException(
                "CTRL is mapped to more than one modifier");
        }
        if((duplicateMapping & InputEvent.ALT_MASK) != 0) {
            throw new IllegalArgumentException(
                "ALT is mapped to more than one modifier");
        }
        if((duplicateMapping & InputEvent.META_MASK) != 0) {
            throw new IllegalArgumentException(
                "META is mapped to more than one modifier");
        }
        if((duplicateMapping & InputEvent.SHIFT_MASK) != 0) {
            throw new IllegalArgumentException(
                "SHIFT is mapped to more than one modifier");
        }
            
        KeyEventTranslator.c = c;
        KeyEventTranslator.a = a;
        KeyEventTranslator.m = m;
        KeyEventTranslator.s = s;
    } //}}}

    //{{{ getSymbolicModifierName()
    /**
     * Returns a the symbolic modifier name for the specified Java modifier
     * flag.
     *
     * @param mod A modifier constant from <code>InputEvent</code>
     */
    public static char getSymbolicModifierName(int mod) {
        if ((mod & c) != 0)
            return 'C';
        else if ((mod & a) != 0)
            return 'A';
        else if ((mod & m) != 0)
            return 'M';
        else if ((mod & s) != 0)
            return 'S';
        else
            return '\0';
    } //}}}

    //{{{ modifiersToString()
    public static String modifiersToString(int mods) {
        StringBuffer buf = null;

        if ((mods & InputEvent.CTRL_MASK) != 0) {
            if (buf == null) {
                buf = new StringBuffer();
            }
            buf.append(getSymbolicModifierName(InputEvent.CTRL_MASK));
        }
        if ((mods & InputEvent.ALT_MASK) != 0) {
            if (buf == null) {
                buf = new StringBuffer();
            }
            buf.append(getSymbolicModifierName(InputEvent.ALT_MASK));
        }
        if ((mods & InputEvent.META_MASK) != 0) {
            if (buf == null) {
                buf = new StringBuffer();
            }
            buf.append(getSymbolicModifierName(InputEvent.META_MASK));
        }
        if ((mods & InputEvent.SHIFT_MASK) != 0) {
            if (buf == null) {
                buf = new StringBuffer();
            }
            buf.append(getSymbolicModifierName(InputEvent.SHIFT_MASK));
        }

        if (buf == null) {
            return null;
        } else {
            return buf.toString();
        }
    } //}}}
    
    //{{{ getModifierString()
    /**
     * Returns a string containing symbolic modifier names set in the
     * specified event.
     *
     * @param evt The event
     */
    public static String getModifierString(InputEvent evt) {
        StringBuffer buf = new StringBuffer();
        if(evt.isControlDown())
            buf.append(getSymbolicModifierName(InputEvent.CTRL_MASK));
        if(evt.isAltDown())
            buf.append(getSymbolicModifierName(InputEvent.ALT_MASK));
        if(evt.isMetaDown())
            buf.append(getSymbolicModifierName(InputEvent.META_MASK));
        if(evt.isShiftDown())
            buf.append(getSymbolicModifierName(InputEvent.SHIFT_MASK));
        return (buf.length() == 0 ? null : buf.toString());
    } //}}}
    
    static int c, a, m, s;

    //{{{ Private members
    
    private static Map transMap = new HashMap();

    static {
        if(OperatingSystem.isMacOS()) {
            setModifierMapping(
                InputEvent.META_MASK,  /* == ctrl */
                InputEvent.CTRL_MASK,  /* == alt */
                /* M+ discarded by key event workaround! */
                InputEvent.ALT_MASK,   /* == meta */
                InputEvent.SHIFT_MASK  /* == shift */);
        } else {
            setModifierMapping(
                InputEvent.CTRL_MASK,
                InputEvent.ALT_MASK,
                InputEvent.META_MASK,
                InputEvent.SHIFT_MASK);
        }
    } //}}}

    //{{{ Key class
    public static class Key {
        
        public int modifiers;
        public int key;
        public char input;

        //{{{ Key constructor
        
        public Key(int modifiers, int key, char input) {
            this.modifiers = modifiers;
            this.key = key;
            this.input = input;
        }//}}}
        
        //{{{ hashCode()
        
        public int hashCode() {
            return key + input;
        }//}}}
        
        //{{{ equals()
        
        public boolean equals(Object o) {
            if (o instanceof Key) {
                Key k = (Key)o;
                if (modifiers == k.modifiers &&
                    key == k.key &&
                    input == k.input)
                {
                    return true;
                }
            }

            return false;
        }//}}}
        
        //{{{ getModString()
        public String getModString() {
            return modifiersToString(modifiers);
        }//}}}
        
        //{{{ getInternalShortcut()
        
        public String getInternalShortcut() {
            StringBuffer keyString = new StringBuffer();

            String modString = modifiersToString(modifiers);
            if (modString != null) {
                keyString.append(modString).append('+');
            }
            
            if (input == ' ') {
                keyString.append("SPACE");
            } else {
                if (input != '\0') {
                    keyString.append(input);
                } else {
                    String symbolicName = getSymbolicName(key);

                    if (symbolicName == null) {
                        return null;
                    }

                    keyString.append(symbolicName);
                }
            }
            return keyString.toString();
        }//}}}
        
        //{{{ toString()
        
        public String toString() {
            StringBuffer str = new StringBuffer();
            String text = KeyEvent.getKeyModifiersText(modifiers);
            if (text != null && text.length() != 0) {
                str.append(text).append(" ");
            }
            
            if (input != '\0') {
                if (input == ' ') {
                    str.append(KeyEvent.getKeyText(KeyEvent.VK_SPACE));
                } else {
                    str.append(input);
                }   
            } else {
                str.append(KeyEvent.getKeyText(key));
            }
            
            return str.toString();
        }//}}}
        
        //{{{ getSymbolicName()
        private String getSymbolicName(int keyCode) {
            if (keyCode == KeyEvent.VK_UNDEFINED) {
                return null;
            }
            /* else if(keyCode == KeyEvent.VK_OPEN_BRACKET)
                return "[";
            else if(keyCode == KeyEvent.VK_CLOSE_BRACKET)
                return "]"; */
    
            if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
                return String.valueOf(Character.toLowerCase((char)keyCode));
            }
            
            try {
                Field[] fields = KeyEvent.class.getFields();
                for(int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    String name = field.getName();
                    if(name.startsWith("VK_") && field.getInt(null) == keyCode) {
                        return name.substring(3);
                    }
                }
            } catch(Exception e) {
                Log.log(Log.ERROR,this,e);
            }
    
            return null;
        } //}}}
        
    } //}}}
}
