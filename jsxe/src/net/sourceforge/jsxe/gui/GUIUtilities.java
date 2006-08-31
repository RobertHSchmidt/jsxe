/*
GUIUtilities.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 1999, 2004 Slava Pestov
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
import net.sourceforge.jsxe.OperatingSystem;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.util.MiscUtilities;
//}}}

//{{{ Swing classes
import javax.swing.*;
//}}}

//{{{ AWT classes
import java.awt.*;
import java.awt.event.*;
//}}}

//{{{ Java classes
import java.net.*;
import java.util.*;
//}}}

//}}}

/**
 * Various GUI functions.<p>
 *
 * The most frequently used members of this class are:
 *
 * <ul>
 * <li>{@link #loadIcon(String)}</li>
 * <li>{@link #confirm(Component,String,Object[],int,int)}</li>
 * <li>{@link #error(Component,String,Object[])}</li>
 * <li>{@link #message(Component,String,Object[])}</li>
 * <li>{@link #showPopupMenu(JPopupMenu,Component,int,int)}</li>
 * <li>{@link #showVFSFileDialog(View,String,int,boolean)}</li>
 * <li>{@link #loadGeometry(Window,String)}</li>
 * <li>{@link #saveGeometry(Window,String)}</li>
 * </ul>
 *
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class GUIUtilities {
    
    //{{{ Icon methods

    //{{{ setIconPath() method
    /**
     * Sets the path where jsXe looks for icons.
     */
    public static void setIconPath(String iconPath) {
        GUIUtilities.iconPath = iconPath;
        if (icons != null) {
            icons.clear();
        }
    } //}}}

    //{{{ loadIcon() method
    /**
     * Loads an icon.
     * @param iconName The icon name
     */
    public static Icon loadIcon(String iconName) {
        if (icons == null) {
            icons = new Hashtable();
        }
        // check if there is a cached version first
        ImageIcon icon = (ImageIcon)icons.get(iconName);
        if (icon != null) {
            return icon;
        }

        // get the icon
        if (MiscUtilities.isURL(iconName)) {
            icon = new ImageIcon(iconName.substring(5));
        } else {
            try {
                URL url = GUIUtilities.class.getResource(iconPath + iconName);
                icon = new ImageIcon(url);
            } catch(Exception e) {
                try {
                    URL url = GUIUtilities.class.getResource(defaultIconPath + iconName);
                    icon = new ImageIcon(url);
                } catch(Exception ex) {
                    Log.log(Log.ERROR,GUIUtilities.class, "Icon not found: " + iconName);
                    Log.log(Log.ERROR,GUIUtilities.class,ex);
                    return null;
                }
            }
        }

        icons.put(iconName,icon);
        return icon;
    } //}}}

    //}}}

    //{{{ Menus, tool bars

   // //{{{ loadMenuBar() method
   // /**
   //  * Creates a menubar. Plugins should not need to call this method.
   //  * @param name The menu bar name
   //  */
   // public static JMenuBar loadMenuBar(String name) {
   //     return loadMenuBar(jsXe.getActionContext(),name);
   // } //}}}

   // //{{{ loadMenuBar() method
   // /**
   //  * Creates a menubar. Plugins should not need to call this method.
   //  * @param context An action context
   //  * @param name The menu bar name
   //  */
   // public static JMenuBar loadMenuBar(ActionContext context, String name)  {
   //     String menus = jEdit.getProperty(name);
   //     StringTokenizer st = new StringTokenizer(menus);

   //     JMenuBar mbar = new JMenuBar();

   //     while(st.hasMoreTokens())
   //     {
   //         mbar.add(loadMenu(context,st.nextToken()));
   //     }

   //     return mbar;
   // } //}}}

   // //{{{ loadMenu() method
   // /**
   //  * Creates a menu. The menu label is set from the
   //  * <code><i>name</i>.label</code> property. The menu contents is taken
   //  * from the <code><i>name</i></code> property, which is a whitespace
   //  * separated list of action names. An action name of <code>-</code>
   //  * inserts a separator in the menu.
   //  * @param name The menu name
   //  * @see #loadMenuItem(String)
   //  */
   // public static JMenu loadMenu(String name)
   // {
   //     return loadMenu(jEdit.getActionContext(),name);
   // } //}}}

   // //{{{ loadMenu() method
   // /**
   //  * Creates a menu. The menu label is set from the
   //  * <code><i>name</i>.label</code> property. The menu contents is taken
   //  * from the <code><i>name</i></code> property, which is a whitespace
   //  * separated list of action names. An action name of <code>-</code>
   //  * inserts a separator in the menu.
   //  * @param context An action context; either
   //  * <code>jEdit.getActionContext()</code> or
   //  * <code>VFSBrowser.getActionContext()</code>.
   //  * @param name The menu name
   //  * @see #loadMenuItem(String)
   //  */
   // public static JMenu loadMenu(ActionContext context, String name)
   // {
   //     return new EnhancedMenu(name,
   //         jEdit.getProperty(name.concat(".label")),
   //         context);
   // } //}}}

   // //{{{ loadPopupMenu() method
   // /**
   //  * Creates a popup menu.
     
   //  * @param name The menu name
   //  */
   // public static JPopupMenu loadPopupMenu(String name)
   // {
   //     return loadPopupMenu(jEdit.getActionContext(),name);
   // } //}}}

   // //{{{ loadPopupMenu() method
   // /**
   //  * Creates a popup menu.
     
   //  * @param context An action context; either
   //  * <code>jEdit.getActionContext()</code> or
   //  * <code>VFSBrowser.getActionContext()</code>.
   //  * @param name The menu name
   //  */
   // public static JPopupMenu loadPopupMenu(ActionContext context, String name)
   // {
   //     JPopupMenu menu = new JPopupMenu();

   //     String menuItems = jEdit.getProperty(name);
   //     if(menuItems != null)
   //     {
   //         StringTokenizer st = new StringTokenizer(menuItems);
   //         while(st.hasMoreTokens())
   //         {
   //             String menuItemName = st.nextToken();
   //             if(menuItemName.equals("-"))
   //                 menu.addSeparator();
   //             else
   //                 menu.add(loadMenuItem(context,menuItemName,false));
   //         }
   //     }

   //     return menu;
   // } //}}}

   // //{{{ loadMenuItem() method
   // /**
   //  * Creates a menu item. The menu item is bound to the action named by
   //  * <code>name</code> with label taken from the return value of the
   //  * {@link EditAction#getLabel()} method.
   //  *
   //  * @param name The menu item name
   //  * @see #loadMenu(String)
   //  */
   // public static JMenuItem loadMenuItem(String name)
   // {
   //     return loadMenuItem(jEdit.getActionContext(),name,true);
   // } //}}}

   // //{{{ loadMenuItem() method
   // /**
   //  * Creates a menu item.
   //  * @param name The menu item name
   //  * @param setMnemonic True if the menu item should have a mnemonic
   //  */
   // public static JMenuItem loadMenuItem(String name, boolean setMnemonic)
   // {
   //     return loadMenuItem(jEdit.getActionContext(),name,setMnemonic);
   // } //}}}

   // //{{{ loadMenuItem() method
   // /**
   //  * Creates a menu item.
   //  * @param context An action context; either
   //  * <code>jEdit.getActionContext()</code> or
   //  * <code>VFSBrowser.getActionContext()</code>.
   //  * @param name The menu item name
   //  * @param setMnemonic True if the menu item should have a mnemonic
   //  */
   // public static JMenuItem loadMenuItem(ActionContext context, String name,
   //     boolean setMnemonic)
   // {
   //     if(name.startsWith("%"))
   //         return loadMenu(context,name.substring(1));

   //     String label = jEdit.getProperty(name + ".label");
   //     if(label == null)
   //         label = name;

   //     char mnemonic;
   //     int index = label.indexOf('$');
   //     if(index != -1 && label.length() - index > 1)
   //     {
   //         mnemonic = Character.toLowerCase(label.charAt(index + 1));
   //         label = label.substring(0,index).concat(label.substring(++index));
   //     }
   //     else
   //         mnemonic = '\0';

   //     JMenuItem mi;
   //     if(jEdit.getBooleanProperty(name + ".toggle"))
   //         mi = new EnhancedCheckBoxMenuItem(label,name,context);
   //     else
   //         mi = new EnhancedMenuItem(label,name,context);

   //     if(!OperatingSystem.isMacOS() && setMnemonic && mnemonic != '\0')
   //         mi.setMnemonic(mnemonic);

   //     return mi;
   // } //}}}

   // //{{{ loadToolBar() method
   // /**
   //  * Creates a toolbar.
   //  * @param name The toolbar name
   //  */
   // public static Box loadToolBar(String name)
   // {
   //     return loadToolBar(jEdit.getActionContext(),name);
   // } //}}}

   // //{{{ loadToolBar() method
   // /**
   //  * Creates a toolbar.
   //  * @param context An action context; either
   //  * <code>jEdit.getActionContext()</code> or
   //  * <code>VFSBrowser.getActionContext()</code>.
   //  * @param name The toolbar name
   //  */
   // public static Box loadToolBar(ActionContext context, String name)
   // {
   //     Box toolBar = new Box(BoxLayout.X_AXIS);

   //     String buttons = jEdit.getProperty(name);
   //     if(buttons != null)
   //     {
   //         StringTokenizer st = new StringTokenizer(buttons);
   //         while(st.hasMoreTokens())
   //         {
   //             String button = st.nextToken();
   //             if(button.equals("-"))
   //                 toolBar.add(Box.createHorizontalStrut(12));
   //             else
   //             {
   //                 JButton b = loadToolButton(context,button);
   //                 if(b != null)
   //                     toolBar.add(b);
   //             }
   //         }
   //     }

   //     toolBar.add(Box.createGlue());

   //     return toolBar;
   // } //}}}

   // //{{{ loadToolButton() method
   // /**
   //  * Loads a tool bar button. The tooltip is constructed from
   //  * the <code><i>name</i>.label</code> and
   //  * <code><i>name</i>.shortcut</code> properties and the icon is loaded
   //  * from the resource named '/org/gjt/sp/jedit/icons/' suffixed
   //  * with the value of the <code><i>name</i>.icon</code> property.
   //  * @param name The name of the button
   //  */
   // public static EnhancedButton loadToolButton(String name)
   // {
   //     return loadToolButton(jEdit.getActionContext(),name);
   // } //}}}

   // //{{{ loadToolButton() method
   // /**
   //  * Loads a tool bar button. The tooltip is constructed from
   //  * the <code><i>name</i>.label</code> and
   //  * <code><i>name</i>.shortcut</code> properties and the icon is loaded
   //  * from the resource named '/org/gjt/sp/jedit/icons/' suffixed
   //  * with the value of the <code><i>name</i>.icon</code> property.
   //  * @param context An action context; either
   //  * <code>jEdit.getActionContext()</code> or
   //  * <code>VFSBrowser.getActionContext()</code>.
   //  * @param name The name of the button
   //  */
   // public static EnhancedButton loadToolButton(ActionContext context,
   //     String name)
   // {
   //     String label = jEdit.getProperty(name + ".label");

   //     if(label == null)
   //         label = name;

   //     Icon icon;
   //     String iconName = jEdit.getProperty(name + ".icon");
   //     if(iconName == null)
   //         icon = loadIcon("BrokenImage.png");
   //     else
   //     {
   //         icon = loadIcon(iconName);
   //         if(icon == null)
   //             icon = loadIcon("BrokenImage.png");
   //     }

   //     String toolTip = prettifyMenuLabel(label);
   //     String shortcut1 = jEdit.getProperty(name + ".shortcut");
   //     String shortcut2 = jEdit.getProperty(name + ".shortcut2");
   //     if(shortcut1 != null || shortcut2 != null)
   //     {
   //         toolTip = toolTip + " ("
   //             + (shortcut1 != null
   //             ? shortcut1 : "")
   //             + ((shortcut1 != null && shortcut2 != null)
   //             ? " or " : "")
   //             + (shortcut2 != null
   //             ? shortcut2
   //             : "") + ")";
   //     }

   //     return new EnhancedButton(icon,toolTip,name,context);
   // } //}}}

   // //{{{ prettifyMenuLabel() method
   // /**
   //  * `Prettifies' a menu item label by removing the `$' sign. This
   //  * can be used to process the contents of an <i>action</i>.label
   //  * property.
   //  */
   // public static String prettifyMenuLabel(String label)
   // {
   //     int index = label.indexOf('$');
   //     if(index != -1)
   //     {
   //         label = label.substring(0,index)
   //             .concat(label.substring(index + 1));
   //     }
   //     return label;
   // } //}}}

    //}}}

    //{{{ Canned dialog boxes

    //{{{ message() method
    /**
     * Displays a dialog box.
     * The title of the dialog is fetched from
     * the <code><i>name</i>.title</code> property. The message is fetched
     * from the <code><i>name</i>.message</code> property. The message
     * is formatted by the property manager with <code>args</code> as
     * positional parameters.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param args Positional parameters to be substituted into the
     * message text
     */
    public static void message(Component comp, String name, Object[] args) {
        hideSplashScreen();

        JOptionPane.showMessageDialog(comp,
            Messages.getMessage(name.concat(".message"),args),
            Messages.getMessage(name.concat(".title"),args),
            JOptionPane.INFORMATION_MESSAGE);
    } //}}}

    //{{{ error() method
    /**
     * Displays an error dialog box.
     * The title of the dialog is fetched from
     * the <code><i>name</i>.title</code> property. The message is fetched
     * from the <code><i>name</i>.message</code> property. The message
     * is formatted by the property manager with <code>args</code> as
     * positional parameters.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param args Positional parameters to be substituted into the
     * message text
     */
    public static void error(Component comp, String name, Object[] args) {
        hideSplashScreen();

        JOptionPane.showMessageDialog(comp,
            Messages.getMessage(name.concat(".message"),args),
            Messages.getMessage(name.concat(".title"),args),
            JOptionPane.ERROR_MESSAGE);
    } //}}}

    //{{{ input() method
    /**
     * Displays an input dialog box and returns any text the user entered.
     * The title of the dialog is fetched from
     * the <code><i>name</i>.title</code> property. The message is fetched
     * from the <code><i>name</i>.message</code> property.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param def The text to display by default in the input field
     */
    public static String input(Component comp, String name, Object def)
    {
        return input(comp,name,null,def);
    } //}}}

    //{{{ inputProperty() method
    /**
     * Displays an input dialog box and returns any text the user entered.
     * The title of the dialog is fetched from
     * the <code><i>name</i>.title</code> property. The message is fetched
     * from the <code><i>name</i>.message</code> property.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param def The property whose text to display in the input field
     */
    public static String inputProperty(Component comp, String name,
        String def)
    {
        return inputProperty(comp,name,null,def);
    } //}}}

    //{{{ input() method
    /**
     * Displays an input dialog box and returns any text the user entered.
     * The title of the dialog is fetched from
     * the <code><i>name</i>.title</code> property. The message is fetched
     * from the <code><i>name</i>.message</code> property.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param def The text to display by default in the input field
     * @param args Positional parameters to be substituted into the
     * message text
     */
    public static String input(Component comp, String name,
        Object[] args, Object def)
    {
        hideSplashScreen();

        String retVal = (String)JOptionPane.showInputDialog(comp,
            Messages.getMessage(name.concat(".message"),args),
            Messages.getMessage(name.concat(".title")),
            JOptionPane.QUESTION_MESSAGE,null,null,def);
        return retVal;
    } //}}}

    //{{{ inputProperty() method
    /**
     * Displays an input dialog box and returns any text the user entered.
     * The title of the dialog is fetched from
     * the <code><i>name</i>.title</code> property. The message is fetched
     * from the <code><i>name</i>.message</code> property.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param args Positional parameters to be substituted into the
     * message text
     * @param def The property whose text to display in the input field
     */
    public static String inputProperty(Component comp, String name,
        Object[] args, String def)
    {
        hideSplashScreen();

        String retVal = (String)JOptionPane.showInputDialog(comp,
            Messages.getMessage(name.concat(".message"),args),
            Messages.getMessage(name.concat(".title")),
            JOptionPane.QUESTION_MESSAGE,
            null,null,jsXe.getProperty(def));
        if (retVal != null) {
            jsXe.setProperty(def,retVal);
        }
        return retVal;
    } //}}}

    //{{{ confirm() method
    /**
     * Displays a confirm dialog box and returns the button pushed by the
     * user. The title of the dialog is fetched from the
     * <code><i>name</i>.title</code> property. The message is fetched
     * from the <code><i>name</i>.message</code> property.
     * @param comp The component to display the dialog for
     * @param name The name of the dialog
     * @param args Positional parameters to be substituted into the
     * message text
     * @param buttons The buttons to display - for example,
     * JOptionPane.YES_NO_CANCEL_OPTION
     * @param type The dialog type - for example,
     * JOptionPane.WARNING_MESSAGE
     */
    public static int confirm(Component comp, String name,
        Object[] args, int buttons, int type)
    {
        hideSplashScreen();

        return JOptionPane.showConfirmDialog(comp,
            Messages.getMessage(name + ".message",args),
            Messages.getMessage(name + ".title"),buttons,type);
    } //}}}

   // //{{{ showVFSFileDialog() method
   // /**
   //  * Displays a VFS file selection dialog box.
   //  * @param view The view, should be non-null
   //  * @param path The initial directory to display. May be null
   //  * @param type The dialog type. One of
   //  * {@link org.gjt.sp.jedit.browser.VFSBrowser#OPEN_DIALOG},
   //  * {@link org.gjt.sp.jedit.browser.VFSBrowser#SAVE_DIALOG}, or
   //  * {@link org.gjt.sp.jedit.browser.VFSBrowser#CHOOSE_DIRECTORY_DIALOG}.
   //  * @param multipleSelection True if multiple selection should be allowed
   //  * @return The selected file(s)
   //  */
   // public static String[] showVFSFileDialog(View view, String path,
   //     int type, boolean multipleSelection)
   // {
   //     // the view should not be null, but some plugins might do this
   //     if(view == null)
   //     {
   //         Log.log(Log.WARNING,GUIUtilities.class,
   //         "showVFSFileDialog(): given null view, assuming jEdit.getActiveView()");
   //         view = jEdit.getActiveView();
   //     }

   //     hideSplashScreen();

   //     VFSFileChooserDialog fileChooser = new VFSFileChooserDialog(
   //         view,path,type,multipleSelection);
   //     String[] selectedFiles = fileChooser.getSelectedFiles();
   //     if(selectedFiles == null)
   //         return null;

   //     return selectedFiles;
   // } //}}}

    //}}}

    //{{{ Colors and styles

    //{{{ parseColor() method
    /**
     * Converts a color name to a color object. The name must either be
     * a known string, such as `red', `green', etc (complete list is in
     * the <code>java.awt.Color</code> class) or a hex color value
     * prefixed with `#', for example `#ff0088'.
     * @param name The color name
     */
    public static Color parseColor(String name)
    {
        return parseColor(name, Color.black);
    } //}}}

    //{{{ parseColor() method
    public static Color parseColor(String name, Color defaultColor)
    {
        if(name == null)
            return defaultColor;
        else if(name.startsWith("#"))
        {
            try
            {
                return Color.decode(name);
            }
            catch(NumberFormatException nf)
            {
                return defaultColor;
            }
        }
        else if("red".equals(name))
            return Color.red;
        else if("green".equals(name))
            return Color.green;
        else if("blue".equals(name))
            return Color.blue;
        else if("yellow".equals(name))
            return Color.yellow;
        else if("orange".equals(name))
            return Color.orange;
        else if("white".equals(name))
            return Color.white;
        else if("lightGray".equals(name))
            return Color.lightGray;
        else if("gray".equals(name))
            return Color.gray;
        else if("darkGray".equals(name))
            return Color.darkGray;
        else if("black".equals(name))
            return Color.black;
        else if("cyan".equals(name))
            return Color.cyan;
        else if("magenta".equals(name))
            return Color.magenta;
        else if("pink".equals(name))
            return Color.pink;
        else
            return defaultColor;
    } //}}}

    //{{{ getColorHexString() method
    /**
     * Converts a color object to its hex value. The hex value
     * prefixed is with `#', for example `#ff0088'.
     * @param c The color object
     */
    public static String getColorHexString(Color c)
    {
        String colString = Integer.toHexString(c.getRGB() & 0xffffff);
        return "#000000".substring(0,7 - colString.length()).concat(colString);
    } //}}}

   // //{{{ parseStyle() method
   // /**
   //  * Converts a style string to a style object.
   //  * @param str The style string
   //  * @param family Style strings only specify font style, not font family
   //  * @param size Style strings only specify font style, not font family
   //  * @exception IllegalArgumentException if the style is invalid
   //  */
   // public static SyntaxStyle parseStyle(String str, String family, int size)
   //     throws IllegalArgumentException
   // {
   //     return parseStyle(str,family,size,true);
   // } //}}}

   // //{{{ parseStyle() method
   // /**
   //  * Converts a style string to a style object.
   //  * @param str The style string
   //  * @param family Style strings only specify font style, not font family
   //  * @param size Style strings only specify font style, not font family
   //  * @param color If false, the styles will be monochrome
   //  * @exception IllegalArgumentException if the style is invalid
   //  */
   // public static SyntaxStyle parseStyle(String str, String family, int size,
   //     boolean color)
   //     throws IllegalArgumentException
   // {
   //     Color fgColor = Color.black;
   //     Color bgColor = null;
   //     boolean italic = false;
   //     boolean bold = false;
   //     StringTokenizer st = new StringTokenizer(str);
   //     while(st.hasMoreTokens())
   //     {
   //         String s = st.nextToken();
   //         if(s.startsWith("color:"))
   //         {
   //             if(color)
   //                 fgColor = GUIUtilities.parseColor(s.substring(6), Color.black);
   //         }
   //         else if(s.startsWith("bgColor:"))
   //         {
   //             if(color)
   //                 bgColor = GUIUtilities.parseColor(s.substring(8), null);
   //         }
   //         else if(s.startsWith("style:"))
   //         {
   //             for(int i = 6; i < s.length(); i++)
   //             {
   //                 if(s.charAt(i) == 'i')
   //                     italic = true;
   //                 else if(s.charAt(i) == 'b')
   //                     bold = true;
   //                 else
   //                     throw new IllegalArgumentException(
   //                         "Invalid style: " + s);
   //             }
   //         }
   //         else
   //             throw new IllegalArgumentException(
   //                 "Invalid directive: " + s);
   //     }
   //     return new SyntaxStyle(fgColor,bgColor,
   //         new Font(family,
   //         (italic ? Font.ITALIC : 0) | (bold ? Font.BOLD : 0),
   //         size));
   // } //}}}

   // //{{{ getStyleString() method
   // /**
   //  * Converts a style into it's string representation.
   //  * @param style The style
   //  */
   // public static String getStyleString(SyntaxStyle style)
   // {
   //     StringBuffer buf = new StringBuffer();

   //     if(style.getForegroundColor() != null)
   //     {
   //         buf.append("color:" + getColorHexString(style.getForegroundColor()));
   //     }

   //     if(style.getBackgroundColor() != null) 
   //     {
   //         buf.append(" bgColor:" + getColorHexString(style.getBackgroundColor()));
   //     }
   //     if(!style.getFont().isPlain())
   //     {
   //         buf.append(" style:" + (style.getFont().isItalic() ? "i" : "")
   //             + (style.getFont().isBold() ? "b" : ""));
   //     }

   //     return buf.toString();
   // } //}}}

   // //{{{ loadStyles() method
   // /**
   //  * Loads the syntax styles from the properties, giving them the specified
   //  * base font family and size.
   //  * @param family The font family
   //  * @param size The font size
   //  */
   // public static SyntaxStyle[] loadStyles(String family, int size)
   // {
   //     return loadStyles(family,size,true);
   // } //}}}

   // //{{{ loadStyles() method
   // /**
   //  * Loads the syntax styles from the properties, giving them the specified
   //  * base font family and size.
   //  * @param family The font family
   //  * @param size The font size
   //  * @param color If false, the styles will be monochrome
   //  */
   // public static SyntaxStyle[] loadStyles(String family, int size, boolean color)
   // {
   //     SyntaxStyle[] styles = new SyntaxStyle[Token.ID_COUNT];

   //     // start at 1 not 0 to skip Token.NULL
   //     for(int i = 1; i < styles.length; i++)
   //     {
   //         try
   //         {
   //             String styleName = "view.style."
   //                 + Token.tokenToString((byte)i)
   //                 .toLowerCase();
   //             styles[i] = GUIUtilities.parseStyle(
   //                 jsXe.getProperty(styleName),
   //                 family,size,color);
   //         }
   //         catch(Exception e)
   //         {
   //             Log.log(Log.ERROR,GUIUtilities.class,e);
   //         }
   //     }

   //     return styles;
   // } //}}}

    //}}}

    //{{{ Loading, saving window geometry

    //{{{ loadGeometry() method
    /**
     * Loads a windows's geometry from the properties.
     * The geometry is loaded from the <code><i>name</i>.x</code>,
     * <code><i>name</i>.y</code>, <code><i>name</i>.width</code> and
     * <code><i>name</i>.height</code> properties.
     *
     * @param win The window
     * @param name The window name
     */
    public static void loadGeometry(Window win, String name) {
        int x, y, width, height;

        Dimension size = win.getSize();
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle gcbounds = gd.getDefaultConfiguration().getBounds();

        x = gcbounds.x;
        y = gcbounds.y;

        width = jsXe.getIntegerProperty(name + ".width",size.width);
        height = jsXe.getIntegerProperty(name + ".height",size.height);

        Component parent = win.getParent();
        if (parent == null) {
            x += (gcbounds.width - width) / 2;
            y += (gcbounds.height - height) / 2;
        } else {
            Rectangle bounds = parent.getBounds();
            x += bounds.x + (bounds.width - width) / 2;
            y += bounds.y + (bounds.height - height) / 2;
        }

        x = jsXe.getIntegerProperty(name + ".x",x);
        y = jsXe.getIntegerProperty(name + ".y",y);

        int extState = jsXe.getIntegerProperty(name + ".extendedState", 0);

        Rectangle desired = new Rectangle(x,y,width,height);
        adjustForScreenBounds(desired);

        // if (OperatingSystem.isX11() && Debug.GEOMETRY_WORKAROUND) {
       //     new UnixWorkaround(win,name,desired,extState);
        // } else {
            win.setBounds(desired);
            if (win instanceof Frame)
                ((Frame)win).setExtendedState(extState);
       // }
    } //}}}

    //{{{ adjustForScreenBounds() method
    /**
     * Gives a rectangle the specified bounds, ensuring it is within the
     * screen bounds.
     */
    public static void adjustForScreenBounds(Rectangle desired) {
        // Make sure the window is displayed in visible region
        Rectangle osbounds = OperatingSystem.getScreenBounds(desired);

        if (desired.x < osbounds.x || desired.x+desired.width > desired.x + osbounds.width) {
            if (desired.width > osbounds.width) {
                desired.width = osbounds.width;
            }
            desired.x = (osbounds.width - desired.width) / 2;
        }
        if (desired.y < osbounds.y || desired.y+desired.height > osbounds.y + osbounds.height) {
            if (desired.height >= osbounds.height) {
                desired.height = osbounds.height;
            }
            desired.y = (osbounds.height - desired.height) / 2;
        }
    } //}}}

    //{{{ UnixWorkaround class
    static class UnixWorkaround
    {
        Window win;
        String name;
        Rectangle desired;
        Rectangle required;
        long start;
        boolean windowOpened;

        //{{{ UnixWorkaround constructor
        UnixWorkaround(Window win, String name, Rectangle desired,
            int extState)
        {
            this.win = win;
            this.name = name;
            this.desired = desired;

            int adjust_x, adjust_y, adjust_width, adjust_height;
            adjust_x = jsXe.getIntegerProperty(name + ".dx",0);
            adjust_y = jsXe.getIntegerProperty(name + ".dy",0);
            adjust_width = jsXe.getIntegerProperty(name + ".d-width",0);
            adjust_height = jsXe.getIntegerProperty(name + ".d-height",0);

            required = new Rectangle(
                desired.x - adjust_x,
                desired.y - adjust_y,
                desired.width - adjust_width,
                desired.height - adjust_height);

            Log.log(Log.DEBUG,GUIUtilities.class,"Window " + name
                + ": desired geometry is " + desired);
            Log.log(Log.DEBUG,GUIUtilities.class,"Window " + name
                + ": setting geometry to " + required);

            start = System.currentTimeMillis();

            win.setBounds(required);
            if (win instanceof Frame)
                ((Frame)win).setExtendedState(extState);

            win.addComponentListener(new ComponentHandler());
            win.addWindowListener(new WindowHandler());
        } //}}}

        //{{{ ComponentHandler class
        class ComponentHandler extends ComponentAdapter
        {
            //{{{ componentMoved() method
            public void componentMoved(ComponentEvent evt)
            {
                if(System.currentTimeMillis() - start < 1000)
                {
                    Rectangle r = win.getBounds();
                    if(!windowOpened && r.equals(required))
                        return;

                    if(!r.equals(desired))
                    {
                        Log.log(Log.DEBUG,GUIUtilities.class,
                            "Window resize blocked: " + win.getBounds());
                        win.setBounds(desired);
                    }
                }

                win.removeComponentListener(this);
            } //}}}

            //{{{ componentResized() method
            public void componentResized(ComponentEvent evt)
            {
                if(System.currentTimeMillis() - start < 1000)
                {
                    Rectangle r = win.getBounds();
                    if(!windowOpened && r.equals(required))
                        return;

                    if(!r.equals(desired))
                    {
                        Log.log(Log.DEBUG,GUIUtilities.class,
                            "Window resize blocked: " + win.getBounds());
                        win.setBounds(desired);
                    }
                }

                win.removeComponentListener(this);
            } //}}}
        } //}}}

        //{{{ WindowHandler class
        class WindowHandler extends WindowAdapter
        {
            //{{{ windowOpened() method
            public void windowOpened(WindowEvent evt)
            {
                windowOpened = true;

                Rectangle r = win.getBounds();
                Log.log(Log.DEBUG,GUIUtilities.class,"Window "
                    + name + ": bounds after opening: " + r);

                jsXe.setIntegerProperty(name + ".dx",
                    r.x - required.x);
                jsXe.setIntegerProperty(name + ".dy",
                    r.y - required.y);
                jsXe.setIntegerProperty(name + ".d-width",
                    r.width - required.width);
                jsXe.setIntegerProperty(name + ".d-height",
                    r.height - required.height);

                win.removeWindowListener(this);
            } //}}}
        } //}}}
    } //}}}

    //{{{ saveGeometry() method
    /**
     * Saves a window's geometry to the properties.
     * The geometry is saved to the <code><i>name</i>.x</code>,
     * <code><i>name</i>.y</code>, <code><i>name</i>.width</code> and
     * <code><i>name</i>.height</code> properties.
     * @param win The window
     * @param name The window name
     */
    public static void saveGeometry(Window win, String name) {
        if (win instanceof Frame)  {
            jsXe.setIntegerProperty(name + ".extendedState",
                ((Frame)win).getExtendedState());
        }

        Rectangle bounds = win.getBounds();
        jsXe.setIntegerProperty(name + ".x",bounds.x);
        jsXe.setIntegerProperty(name + ".y",bounds.y);
        jsXe.setIntegerProperty(name + ".width",bounds.width);
        jsXe.setIntegerProperty(name + ".height",bounds.height);
    } //}}}

    //}}}

    //{{{ hideSplashScreen() method
    /**
     * Ensures that the splash screen is not visible. This should be
     * called before displaying any dialog boxes or windows at
     * startup.
     */
    public static void hideSplashScreen() {
        // Nothing for now
       // if (splash != null) {
       //     splash.dispose();
       //     splash = null;
       // }
    } //}}}

    //{{{ createMultilineLabel() method
    /**
     * Creates a component that displays a multiple line message. This
     * is implemented by assembling a number of <code>JLabels</code> in
     * a <code>JPanel</code>.
     * @param str The string, with lines delimited by newline
     * (<code>\n</code>) characters.
     */
    public static JComponent createMultilineLabel(String str)
    {
        JPanel panel = new JPanel(new VariableGridLayout(
            VariableGridLayout.FIXED_NUM_COLUMNS,1,1,1));
        int lastOffset = 0;
        for(;;)
        {
            int index = str.indexOf('\n',lastOffset);
            if(index == -1)
                break;
            else
            {
                panel.add(new JLabel(str.substring(lastOffset,index)));
                lastOffset = index + 1;
            }
        }

        if(lastOffset != str.length())
            panel.add(new JLabel(str.substring(lastOffset)));

        return panel;
    } //}}}

    //{{{ requestFocus() method
    /**
     * Focuses on the specified component as soon as the window becomes
     * active.
     * @param win The window
     * @param comp The component
     */
    public static void requestFocus(final Window win, final Component comp)
    {
        win.addWindowListener(new WindowAdapter()
        {
            public void windowActivated(WindowEvent evt)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        comp.requestFocus();
                    }
                });
                win.removeWindowListener(this);
            }
        });
    } //}}}

    //{{{ isPopupTrigger() method
    /**
     * Returns if the specified event is the popup trigger event.
     * This implements precisely defined behavior, as opposed to
     * MouseEvent.isPopupTrigger().
     * @param evt The event
     */
    public static boolean isPopupTrigger(MouseEvent evt)
    {
        return isRightButton(evt.getModifiers());
    } //}}}

    //{{{ isMiddleButton() method
    /**
     * @param modifiers The modifiers flag from a mouse event
     */
    public static boolean isMiddleButton(int modifiers) {
        if (OperatingSystem.isMacOS()) {
            if ((modifiers & MouseEvent.BUTTON1_MASK) != 0) {
                return ((modifiers & MouseEvent.ALT_MASK) != 0);
            } else {
                return ((modifiers & MouseEvent.BUTTON3_MASK) != 0);
            }
        } else {
            return ((modifiers & MouseEvent.BUTTON2_MASK) != 0);
        }
    } //}}}

    //{{{ isRightButton() method
    /**
     * @param modifiers The modifiers flag from a mouse event
     */
    public static boolean isRightButton(int modifiers) {
        if (OperatingSystem.isMacOS()) {
            if ((modifiers & MouseEvent.BUTTON1_MASK) != 0) {
                return ((modifiers & MouseEvent.CTRL_MASK) != 0);
            } else {
                return ((modifiers & MouseEvent.BUTTON2_MASK) != 0);
            }
        } else {
            return ((modifiers & MouseEvent.BUTTON3_MASK) != 0);
        }
    } //}}}

    //{{{ showPopupMenu() method
    /**
     * Shows the specified popup menu, ensuring it is displayed within
     * the bounds of the screen.
     * @param popup The popup menu
     * @param comp The component to show it for
     * @param x The x co-ordinate
     * @param y The y co-ordinate
     */
    public static void showPopupMenu(JPopupMenu popup, Component comp, int x, int y) {
        showPopupMenu(popup,comp,x,y,true);
    } //}}}

    //{{{ showPopupMenu() method
    /**
     * Shows the specified popup menu, ensuring it is displayed within
     * the bounds of the screen.
     * @param popup The popup menu
     * @param comp The component to show it for
     * @param x The x co-ordinate
     * @param y The y co-ordinate
     * @param point If true, then the popup originates from a single point;
     * otherwise it will originate from the component itself. This affects
     * positioning in the case where the popup does not fit onscreen.
     *
     */
    public static void showPopupMenu(JPopupMenu popup, Component comp, int x, int y, boolean point) {
        int offsetX = 0;
        int offsetY = 0;

        int extraOffset = (point ? 1 : 0);

        Component win = comp;
        while (!(win instanceof Window || win == null)) {
            offsetX += win.getX();
            offsetY += win.getY();
            win = win.getParent();
        }

        if (win != null) {
            Dimension size = popup.getPreferredSize();

            Rectangle screenSize = win.getGraphicsConfiguration().getBounds();

            if (x + offsetX + size.width + win.getX() > screenSize.width
                && x + offsetX + win.getX() >= size.width)
            {
                if (point) {
                    x -= (size.width + extraOffset);
                } else {
                    x = (win.getWidth() - size.width - offsetX + extraOffset);
                }
            } else {
                x += extraOffset;
            }

            //Log.log(Log.DEBUG, GUIUtilities.class, "y=" + y + ",offsetY=" + offsetY
            //  + ",size.height=" + size.height
            //  + ",win.height=" + win.getHeight());
            if(y + offsetY + size.height + win.getY() > screenSize.height
                && y + offsetY + win.getY() >= size.height)
            {
                if (point) {
                    y = (win.getHeight() - size.height - offsetY + extraOffset);
                } else {
                    y = -size.height - 1;
                }
            } else {
                y += extraOffset;
            }

            popup.show(comp,x,y);
        } else {
            popup.show(comp,x + extraOffset,y + extraOffset);
        }

    } //}}}

    //{{{ isAncestorOf() method
    /**
     * Returns if the first component is an ancestor of the
     * second by traversing up the component hierarchy.
     *
     * @param comp1 The ancestor
     * @param comp2 The component to check
     */
    public static boolean isAncestorOf(Component comp1, Component comp2) {
        while(comp2 != null) {
            if (comp1 == comp2) {
                return true;
            } else {
                comp2 = comp2.getParent();
            }
        }

        return false;
    } //}}}

    //{{{ getParentDialog() method
    /**
     * Traverses the given component's parent tree looking for an
     * instance of JDialog, and return it. If not found, return null.
     * @param c The component
     */
    public static JDialog getParentDialog(Component c) {
        Component p = c.getParent();
        while (p != null && !(p instanceof JDialog)) {
            p = p.getParent();
        }

        return (p instanceof JDialog) ? (JDialog) p : null;
    } //}}}
    
    //{{{ isComponentParentOf() method
    /**
     * Returns true if the parent is a parent component of child.
     * @param parent the parent component
     * @param child the child component
     * @since jsXe 0.5 pre3
     */
    public static boolean isComponentParentOf(Component parent, Component child) {
        Component comp = child;
        for(;;) {
            if (comp == null) {
                break;
            }

            if (comp instanceof JComponent) {
                Component real = (Component)((JComponent)comp).getClientProperty("KORTE_REAL_FRAME");
                if (real != null) {
                    comp = real;
                }
            }

            if (comp.equals(parent)) {
                return true;
            } else {
                if (comp instanceof JPopupMenu) {
                    comp = ((JPopupMenu)comp).getInvoker();
                } else {
                    comp = comp.getParent();
                }
            }
        }
        return false;
    }//}}}
    
    //{{{ getComponentParent() method
    /**
     * Finds a parent of the specified component.
     * @param comp The component
     * @param clazz Looks for a parent with this class (exact match, not
     * derived).
     * @since jsXe 0.5 pre1
     */
    public static Component getComponentParent(Component comp, Class clazz) {
        for(;;) {
            if (comp == null) {
                break;
            }

            if (comp instanceof JComponent) {
                Component real = (Component)((JComponent)comp).getClientProperty("KORTE_REAL_FRAME");
                if (real != null) {
                    comp = real;
                }
            }

            if (comp.getClass().equals(clazz)) {
                return comp;
            } else {
                if (comp instanceof JPopupMenu) {
                    comp = ((JPopupMenu)comp).getInvoker();
                } else {
                    comp = comp.getParent();
                }
            }
        }
        return null;
    } //}}}
    
    //{{{ getView() method
    /**
     * Finds the view parent of the specified component.
     */
    public static TabbedView getView(Component comp)
    {
        return (TabbedView)getComponentParent(comp,TabbedView.class);
    } //}}}

    //{{{ Private members
    private static String defaultIconPath = "/net/sourceforge/jsxe/icons/";
    private static String iconPath = defaultIconPath;
    private static Hashtable icons;
    
    private GUIUtilities() {}
    //}}}
}
