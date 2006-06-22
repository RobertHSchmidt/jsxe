/*
EnhancedMenu.java
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
import net.sourceforge.jsxe.EBListener;
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
public class EnhancedMenu extends JMenu implements EBListener {
    
    //{{{ EnhancedMenu constructor
    /**
     * Constructs a EnhancedPopupMenu without an "invoker" and the default wrap count of 20.
     */
    public EnhancedMenu() {
        super();
        m_addToMenus.push(this);
    }//}}}
    
    //{{{ EnhancedMenu constructor
    /**
     * Constructs a EnhancedPopupMenu without an "invoker" and the wrap count
     * specified.
     * @param wrapCount the number of components that are added to menu before
     *                  it wraps.
     */
    public EnhancedMenu(int wrapCount) {
        super();
        m_wrapCount = wrapCount;
        m_addToMenus.push(this);
    }//}}}
    
    //{{{ EnhancedMenu constructor
    /**
     * Constructs an EnhancedMenu with the specified title.
     * @param label the string that a UI may use to display as a title for the
     *              popup menu.
     * @param wrapCount the number of components that are added to menu before
     *                  it wraps.
     */
    public EnhancedMenu(String label, int wrapCount) {
        super(label);
        m_wrapCount = wrapCount;
        m_addToMenus.push(this);
    }//}}}  
    
    //{{{ add()
    
    public JMenuItem add(Action a) {
        maybeAddMenu();
        JMenuItem r;
        JMenu menu = getCurrentMenu();
        if (menu == this) {
            r = super.add(a);
        } else {
            r = menu.add(a);
        }
        m_menuHash.put(r, getCurrentMenu());
        return r;
    }//}}}
    
    //{{{ add()
    
    public Component add(Component c) {
        maybeAddMenu();
        Component r;
        JMenu menu = getCurrentMenu();
        if (menu == this) {
            r = super.add(c);
        } else {
            r = menu.add(c);
        }
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
            if (menu == this) {
                r = super.add(c, addSubIndex);
            } else {
                r = menu.add(c, addSubIndex);
            }
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
        if (menu == this) {
            r = super.add(menuItem);
        } else {
            r = menu.add(menuItem);
        }
        return r;
    }//}}}
    
    //{{{ add()
    
    public JMenuItem add(String s) {
        maybeAddMenu();
        JMenuItem r;
        JMenu menu = getCurrentMenu();
        if (menu == this) {
            r = super.add(s);
        } else {
            r = menu.add(s);
        }
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
            if (menu == this) {
                r = super.insert(a, addSubIndex);
            } else {
                r = menu.insert(a, addSubIndex);
            }
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
            if (menu == this) {
                r = super.insert(mi, addSubIndex);
            } else {
                r = menu.insert(mi, addSubIndex);
            }
            updateSubMenus();
        }
        return r;
    }//}}}
    
    //{{{ insert
    
    public void insert(String s, int pos) {
        if (pos == -1) {
            JMenuItem r = add(s);
            m_menuHash.put(r, getCurrentMenu());
        } else {
            int addIndex = (int)(pos / m_wrapCount);
            int addSubIndex = (pos % m_wrapCount);
            JMenu menu = (JMenu)m_addToMenus.get(addIndex);
            if (menu == this) {
                super.insert(s, addSubIndex);
            } else {
                menu.insert(s, addSubIndex);
            }
            updateSubMenus();
        }
    }//}}}
    
    //{{{ remove()
    
    public void remove(Component c) {
        JMenu menu = (JMenu)m_menuHash.get(c);
        if (menu == this) {
            super.remove(c);
        } else {
            menu.remove(c);
        }
        
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
        if (menu == this) {
            super.remove(item);
        } else {
            menu.remove(item);
        }
        m_menuHash.remove(item);
        updateSubMenus();
    }//}}}
    
    //{{{ removeAll()
    
    public void removeAll() {
        m_menuHash = new HashMap();
        m_addToMenus = new Stack();
        m_addToMenus.push(this);
        super.removeAll();
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
        if (menu == this) {
            return super.getMenuComponent(addSubIndex);
        } else {
            return menu.getMenuComponent(addSubIndex);
        }
    }//}}}
    
    //{{{ getMenuComponentCount
    /**
     * Gets the total number of components in this menu
     * and submenus.
     * @return the total number of components.
     */
    public int getMenuComponentCount() {
        return m_menuHash.keySet().size();
    }//}}}
    
    //{{{ Private members
    
    //{{{ getTrueMenuItemCount()
    /**
     * Gets the true item count for a sub-menu
     */
    private int getTrueMenuItemCount(JMenu menu) {
        if (menu == this) {
            return super.getMenuComponentCount();
        } else {
            return menu.getMenuComponentCount();
        }
    }//}}}
    
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
            while (getTrueMenuItemCount(menu) > m_wrapCount + 1) {
                
                //If we need another menu then make one.
                JMenu nextMenu;
                try {
                    nextMenu = (JMenu)m_addToMenus.get(i+1);
                } catch (IndexOutOfBoundsException e) {
                    MoreMenu moreMenu = new MoreMenu();
                    menu.add(moreMenu);
                    m_addToMenus.push(moreMenu);
                    nextMenu = moreMenu;
                }
                
                int index = getTrueMenuItemCount(menu)-2;
                Component menuComponent = menu.getComponent(index);
                menu.remove(index);
                nextMenu.add(menuComponent, 0);
            }
            
            //while there are less than we want in the menu and it's not the last menu
            while (getTrueMenuItemCount(menu) < m_wrapCount + 1 && i+1 < m_addToMenus.size()) {
                JMenu nextMenu = (JMenu)m_addToMenus.get(i+1);
                
                Component menuComponent = nextMenu.getMenuComponent(0);
                nextMenu.remove(0);
                if (menu == this) {
                    super.add(menuComponent, getTrueMenuItemCount(this)-1);
                } else {
                    menu.add(menuComponent, getTrueMenuItemCount(menu)-1);
                }
                
                if (getTrueMenuItemCount(nextMenu) == 0) {
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
        int componentCount = getTrueMenuItemCount(getCurrentMenu());
        if (componentCount >= m_wrapCount) {
            MoreMenu menu = new MoreMenu();
            ((JMenu)m_addToMenus.peek()).add(menu);
            m_addToMenus.push(menu);
        }
    }//}}}
    
    //{{{ MoreMenu class
    /**
     * A submenu used by the EnhancedMenu
     */
    private static class MoreMenu extends JMenu {
        
        public MoreMenu() {
            super(Messages.getMessage("common.more"));
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
