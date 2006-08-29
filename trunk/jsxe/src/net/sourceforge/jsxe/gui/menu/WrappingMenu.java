/*
WrappingMenu.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2005 Ian Lewis (IanLewis@member.fsf.org)

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

package net.sourceforge.jsxe.gui.menu;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.gui.Messages;
//}}}

//{{{ Java classes
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.Action;
import java.awt.Component;
import java.util.*;
//}}}

//}}}

/**
 * A Menu class that handles wrapping the menu items into sub-menus for you.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class WrappingMenu {
    
    //{{{ WrappingMenu constructor
    /**
     * Constructs a WrappingMenu without an "invoker" and the default wrap count of 20.
     */
    public WrappingMenu() {
        m_addToMenus.push(new JMenu());
    }//}}}
    
    //{{{ WrappingMenu constructor
    /**
     * Constructs a WrappingMenu without an "invoker" and the wrap count
     * specified.
     * @param wrapCount the number of components that are added to menu before
     *                  it wraps.
     */
    public WrappingMenu(int wrapCount) {
        m_wrapCount = wrapCount;
        m_addToMenus.push(new JMenu());
    }//}}}
    
    //{{{ WrappingMenu constructor
    /**
     * Constructs an WrappingMenu with the specified title.
     * @param label the string that a UI may use to display as a title for the
     *              popup menu.
     * @param wrapCount the number of components that are added to menu before
     *                  it wraps.
     */
    public WrappingMenu(String label, int wrapCount) {
        m_wrapCount = wrapCount;
        m_addToMenus.push(new JMenu(label));
    }//}}}  
    
    //{{{ add()
    
    public JMenuItem add(Action a) {
        maybeAddMenu();
        JMenuItem r;
        JMenu menu = getCurrentMenu();
        r = menu.add(a);
        m_menuHash.put(r, getCurrentMenu());
        return r;
    }//}}}
    
    //{{{ add()
    
    public Component add(Component c) {
        maybeAddMenu();
        Component r;
        JMenu menu = getCurrentMenu();
        r = menu.add(c);
        m_menuHash.put(r, getCurrentMenu());
        return r;
    }//}}}
    
    //{{{ add()
    
    public Component add(Component c, int index) {
        Component r;
        if (index == -1) {
            r = add(c);
            m_menuHash.put(r, getCurrentMenu());
        } else {
            int addIndex = (int)(index / m_wrapCount);
            int addSubIndex = (index % m_wrapCount);
            JMenu menu = (JMenu)m_addToMenus.get(addIndex);
            r = menu.add(c, addSubIndex);
            updateSubMenus();
        }
        return r;
    }//}}}
    
    //{{{ add()
    
    public JMenuItem add(JMenuItem menuItem) {
        if (!(menuItem instanceof MoreMenu)) {
            maybeAddMenu();
            m_menuHash.put(menuItem, getCurrentMenu());
        }
        JMenuItem r;
        JMenu menu = getCurrentMenu();
        r = menu.add(menuItem);
        return r;
    }//}}}
    
    //{{{ add()
    
    public JMenuItem add(String s) {
        maybeAddMenu();
        JMenuItem r;
        JMenu menu = getCurrentMenu();
        r = menu.add(s);
        m_menuHash.put(r, getCurrentMenu());
        return r;
    }//}}}
    
    //{{{ insert()
    
    public JMenuItem insert(Action a, int pos) {
        JMenuItem r;
        if (pos == -1) {
            r = add(a);
            m_menuHash.put(r, getCurrentMenu());
        } else {
            int addIndex = (int)(pos / m_wrapCount);
            int addSubIndex = (pos % m_wrapCount);
            JMenu menu = (JMenu)m_addToMenus.get(addIndex);
            r = menu.insert(a, addSubIndex);
            updateSubMenus();
        }
        return r;
    }//}}}
    
    //{{{ insert()
    
    public JMenuItem insert(JMenuItem mi, int pos) {
        JMenuItem r;
        if (pos == -1) {
            r = add(mi);
            m_menuHash.put(r, getCurrentMenu());
        } else {
            int addIndex = (int)(pos / m_wrapCount);
            int addSubIndex = (pos % m_wrapCount);
            JMenu menu = (JMenu)m_addToMenus.get(addIndex);
            r = menu.insert(mi, addSubIndex);
            updateSubMenus();
        }
        return r;
    }//}}}
    
    //{{{ insert()
    
    public void insert(String s, int pos) {
        if (pos == -1) {
            JMenuItem r = add(s);
            m_menuHash.put(r, getCurrentMenu());
        } else {
            int addIndex = (int)(pos / m_wrapCount);
            int addSubIndex = (pos % m_wrapCount);
            JMenu menu = (JMenu)m_addToMenus.get(addIndex);
            menu.insert(s, addSubIndex);
            updateSubMenus();
        }
    }//}}}
    
    //{{{ remove()
    
    public void remove(Component c) {
        JMenu menu = (JMenu)m_menuHash.get(c);
        menu.remove(c);
        m_menuHash.remove(c);
        updateSubMenus();
    }//}}}
    
    //{{{ remove()
    
    public void remove(int pos) {
        remove(getMenuComponent(pos));
    }//}}}
    
    //{{{ remove()
    
    public void remove(JMenuItem item) {
        JMenu menu = (JMenu)m_menuHash.get(item);
        menu.remove(item);
        m_menuHash.remove(item);
        updateSubMenus();
    }//}}}
    
    //{{{ removeAll()
    
    public void removeAll() {
        JMenu menu = getJMenu();
        menu.removeAll();
        m_menuHash = new HashMap();
        m_addToMenus = new Stack();
        m_addToMenus.push(menu);
    }//}}}
    
    //{{{ getMenuComponent()
    /**
     * Returns the the component in this menu or a submenu.
     * @param int the index into this menu and submenus.
     */
    public Component getMenuComponent(int n) {
        int addIndex = (int)(n / m_wrapCount);
        int addSubIndex = (n % m_wrapCount);
        JMenu menu = (JMenu)m_addToMenus.get(addIndex);
        return menu.getMenuComponent(addSubIndex);
    }//}}}
    
    //{{{ getMenuComponentCount()
    /**
     * Gets the total number of components in this menu
     * and submenus.
     * @return the total number of components.
     */
    public int getMenuComponentCount() {
        return m_menuHash.keySet().size();
    }//}}}
    
    //{{{ getJMenu()
    /**
     * Gets the JMenu that is used by Swing for this WrappingMenu.
     */
    public JMenu getJMenu() {
        return (JMenu)m_addToMenus.get(0);
    }//}}}
    
    //{{{ MoreMenu class
    /**
     * A submenu used by the <code>WrappingMenu</code>. Classes that extend 
     * WrappingMenu may wish to create an extension to <code>MoreMenu</code>
     * which implements new functionality, and override the
     * <code>createMoreMenu()</code> factory method.
     */
    public static class MoreMenu extends JMenu {
        
        public MoreMenu() {
            super(Messages.getMessage("common.more"));
        }
        
    }//}}}
    
    //{{{ Protected members
    
    //{{{ createMoreMenu()
    /**
     * Creates an internal menu that is used for wrapping this
     * WrappingMenu. This should be overridded by subclasses which
     * wish to implement new functionality.
     */
    protected MoreMenu createMoreMenu() {
        return new MoreMenu();
    }//}}}
    
    //}}}
    
    //{{{ Private members
    
    //{{{ getCurrentMenu()
    /**
     * Gets the current menu that we are adding to.
     */
    private JMenu getCurrentMenu() {
        return ((JMenu)m_addToMenus.peek());
    }//}}}
    
    //{{{ updateSubMenus()
    /**
     * Updates the submenus after adding or removing a component
     */
    private void updateSubMenus() {
        //must call m_addToMenus.size() here since we may remove menus in the loop.
        for (int i=0; i<m_addToMenus.size(); i++) {
            JMenu menu = (JMenu)m_addToMenus.get(i);
            
            //greater than wrap count + 1 because of the "More" menu item.
            while (menu.getMenuComponentCount() > m_wrapCount + 1) {
                
                //If we need another menu then make one.
                JMenu nextMenu;
                try {
                    nextMenu = (JMenu)m_addToMenus.get(i+1);
                } catch (IndexOutOfBoundsException e) {
                    MoreMenu moreMenu = createMoreMenu();
                    menu.add(moreMenu);
                    m_addToMenus.push(moreMenu);
                    nextMenu = moreMenu;
                }
                
                int index = menu.getMenuComponentCount()-2;
                Component menuComponent = menu.getComponent(index);
                menu.remove(index);
                nextMenu.add(menuComponent, 0);
            }
            
            //while there are less than we want in the menu and it's not the last menu
            while (menu.getMenuComponentCount() < m_wrapCount + 1 && i+1 < m_addToMenus.size()) {
                JMenu nextMenu = (JMenu)m_addToMenus.get(i+1);
                
                Component menuComponent = nextMenu.getMenuComponent(0);
                nextMenu.remove(0);
                menu.add(menuComponent, menu.getMenuComponentCount()-1);
                
                if (nextMenu.getMenuComponentCount() == 0) {
                    menu.remove(nextMenu);
                    m_addToMenus.pop(); //if it's empty it must be the last menu.
                }
            }
        }
    }//}}}
    
    //{{{ maybeAddMenu()
    /**
     * Updates the menu that we are truly adding to.
     */
    private void maybeAddMenu() {
        int componentCount = getCurrentMenu().getMenuComponentCount();
        if (componentCount >= m_wrapCount) {
            MoreMenu menu = createMoreMenu();
            ((JMenu)m_addToMenus.peek()).add(menu);
            m_addToMenus.push(menu);
        }
    }//}}}
    
    private int m_wrapCount = 20;
    private Stack m_addToMenus = new Stack();
    
    /**
     * An item to menu that contains it mapping.
     */
    private HashMap m_menuHash = new HashMap();
    //}}}
}
