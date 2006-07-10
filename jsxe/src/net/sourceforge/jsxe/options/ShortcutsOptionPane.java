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
import net.sourceforge.jsxe.gui.GrabKeyDialog;
import net.sourceforge.jsxe.util.MiscUtilities;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ AWT classes
import java.awt.BorderLayout;
import java.awt.Dimension;
//}}}

//{{{ Swing classes
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.*;
//}}}

//{{{ Java classses
import java.util.Comparator;
import java.util.Iterator;
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
        setLayout(new BorderLayout(12,12));
        
        initModels();
        
        JComboBox setsBox = new JComboBox(ActionManager.getActionSets().toArray());
        Box north = Box.createHorizontalBox();
        north.add(new JLabel(Messages.getMessage("Shortcuts.Options.Select.Label")));
        north.add(Box.createHorizontalStrut(6));
        north.add(setsBox);
        
        JTable keyTable = new JTable(m_currentModel);
        keyTable.getTableHeader().setReorderingAllowed(false);
       // keyTable.getTableHeader().addMouseListener(new HeaderMouseHandler());
       // keyTable.addMouseListener(new TableMouseHandler());
        Dimension d = keyTable.getPreferredSize();
        d.height = Math.min(d.height,200);
        JScrollPane scroller = new JScrollPane(keyTable);
        scroller.setPreferredSize(d);
        
        add(BorderLayout.NORTH,north);
        add(BorderLayout.CENTER,scroller);
        
    }//}}}
    
    //{{{ _save()
    protected void _save() {
        
    }//}}}
    
    //{{{ toString()
    public String toString() {
        return getTitle();
    }//}}}
    
    //{{{ Private Members
    
    private ActionSetTableModel m_currentModel;
    private Vector m_models;
    
    //{{{ ActionSetTableModel class
    private class ActionSetTableModel extends AbstractTableModel {
        
        //{{{ ActionSetTableModel constructor
        public ActionSetTableModel(ActionSet set) {
            String[] names = set.getActionNames();
            m_set = new Vector(names.length);
            
            for (int i = 0; i < names.length; i++) {
                String label = ActionManager.getLocalizedAction(names[i]).getLabel();
                if (label == null) {
                    Log.log(Log.WARNING, this, names[i]+" has a null label");
                } else {
                    String binding = jsXe.getProperty(names[i]+".shortcut");
                    m_set.add(new GrabKeyDialog.KeyBinding(names[i], label, binding));
                }
            }
            MiscUtilities.quicksort(m_set, new KeyCompare());
            
            m_name = set.getLabel();
        }//}}}
        
        //{{{ getColumnCount()
        public int getColumnCount() {
            return 2;
        }//}}}
        
        //{{{ getColumnName()
        
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case(0):
                    return Messages.getMessage("Shortcuts.Options.Command");
                case(1):
                    return Messages.getMessage("Shortcuts.Options.Shortcut");
                default:
                    return null;
            }
        }//}}}
        
        //{{{ getRowCount()
        public int getRowCount() {
            return m_set.size();
        }//}}}
        
        //{{{ getValueAt()
        public Object getValueAt(int row, int column) {
            switch (column) {
                case (0):
                    return ((GrabKeyDialog.KeyBinding)m_set.get(row)).label;
                case (1):
                    return ((GrabKeyDialog.KeyBinding)m_set.get(row)).shortcut;
                default:
                    return null;
            }
        }//}}}
        
        //{{{ setValueAt()
        
        public void setValueAt(Object value, int row, int col) {
            if (col == 0)
                return;

            ((GrabKeyDialog.KeyBinding)m_set.get(row)).shortcut = (String)value;

            // redraw the whole table because a second shortcut
            // might have changed, too
            fireTableDataChanged();
        }//}}}
        
        //{{{ toString()
        
        public String toString() {
            // needed for sorting of the models in initModels()
            return m_name;
        }//}}}
        
        //{{{ Private members
        
        //{{{ KeyCompare class
        
        private class KeyCompare implements Comparator {
            
            public int compare(Object obj1, Object obj2) {
                String label1 = ((GrabKeyDialog.KeyBinding)obj1).label;
                String label2 = ((GrabKeyDialog.KeyBinding)obj2).label;
                return MiscUtilities.compareStrings(label1,label2,true);
            }
            
        }//}}}
        
        private Vector m_set;
        private String m_name;
        //}}}
        
    }//}}}
    
    //{{{ initModels()
    
    private void initModels() {
        m_models = new Vector();
        Iterator itr = ActionManager.getActionSets().iterator();
        while(itr.hasNext()) {
            ActionSet actionSet = (ActionSet)itr.next();
            if (actionSet.getActionCount() != 0) {
                String modelLabel = actionSet.getLabel();
                m_models.addElement(new ActionSetTableModel(actionSet));
            }
        }
        MiscUtilities.quicksort(m_models,new MiscUtilities.StringICaseCompare());
        m_currentModel = (ActionSetTableModel)m_models.elementAt(0);
    }//}}}
    
    //}}}
    
}
