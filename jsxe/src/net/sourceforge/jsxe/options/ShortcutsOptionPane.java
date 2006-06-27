/*
ShortcutsOptionPane.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe.options;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.ActionSet;
import net.sourceforge.jsxe.ActionManager;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.util.MiscUtilities;
//}}}

//{{{ Swing classes
import javax.swing.table.*;
//}}}

//{{{ Java classses
import java.util.Vector;
//}}}

//}}}

/**
 * The OptionPane containing jsXe's shortcuts options in the Global Options
 * Dialog.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since 0.5 pre1
 * @see net.sourceforge.jsxe.gui.GlobalOptionsDialog
 */
public class ShortcutsOptionPane extends AbstractOptionPane {
    
    //{{{ ShortcutsOptionPane constructor
    public ShortcutsOptionPane() {
        super("shortcuts");
    }//}}}
    
    //{{{ getTitle()
    public String getTitle() {
        return Messages.getMessage("Shortcuts.Options.Title");
    }//}}}
    
    //{{{ _init()
    protected void _init() {
        
    }//}}}
    
    //{{{ _save()
    protected void _save() {
        
    }//}}}
    
    //{{{ toString()
    public String toString() {
        return getTitle();
    }//}}}
    
    //{{{ Private Members
    
    //{{{ ActionSetTableModel class
    private class ActionSetTableModel extends AbstractTableModel {
        
        //{{{ ActionSetTableModel constructor
        public ActionSetTableModel(ActionSet set) {
            //TODO: need to use key bindings from the GrabKeyDialog
            String[] names = set.getActionNames();
            m_set = new Vector(names.length);
            
            for (int i = 0; i < names.length; i++) {
                String label = ActionManager.getLocalizedAction(names[i]).getLabel();
                m_set.add(label);
            }
            MiscUtilities.quicksort(m_set, new MiscUtilities.StringCompare());
        }//}}}
        
        //{{{ getColumnCount()
        public int getColumnCount() {
            return 2;
        }//}}}
        
        //{{{ getRowCount()
        public int getRowCount() {
            return m_set.size();
        }//}}}
        
        //{{{ getValueAt()
        public Object getValueAt(int row, int column) {
            return null;
        }//}}}
        
        //{{{ Private members
        private Vector m_set;
        //}}}
        
    }//}}}
    
    //}}}
    
}
