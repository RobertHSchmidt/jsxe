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
import net.sourceforge.jsxe.gui.GUIUtilities;
import net.sourceforge.jsxe.gui.KeyEventTranslator;
import net.sourceforge.jsxe.util.MiscUtilities;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ AWT classes
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
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
        
        m_setsComboBox = new JComboBox(m_models);
        m_setsComboBox.addActionListener(new ComboBoxHandler());
        Box north = Box.createHorizontalBox();
        north.add(new JLabel(Messages.getMessage("Shortcuts.Options.Select.Label")));
        north.add(Box.createHorizontalStrut(6));
        north.add(m_setsComboBox);
        
        m_keyTable = new JTable(m_currentModel);
        m_keyTable.getTableHeader().setReorderingAllowed(false);
       // keyTable.getTableHeader().addMouseListener(new HeaderMouseHandler());
        m_keyTable.addMouseListener(new TableMouseHandler());
        Dimension d = m_keyTable.getPreferredSize();
        d.height = Math.min(d.height,200);
        JScrollPane scroller = new JScrollPane(m_keyTable);
        scroller.setPreferredSize(d);
        
        add(BorderLayout.NORTH,north);
        add(BorderLayout.CENTER,scroller);
        
    }//}}}
    
    //{{{ _save()
    protected void _save() {
        if (m_keyTable.getCellEditor() != null) {
            m_keyTable.getCellEditor().stopCellEditing();
        }

        Iterator e = m_models.iterator();
        while (e.hasNext()) {
            ((ActionSetTableModel)e.next()).save();
        }
    }//}}}
    
    //{{{ toString()
    public String toString() {
        return getTitle();
    }//}}}
    
    //{{{ Private Members
    
    private ActionSetTableModel m_currentModel;
    private Vector m_models;
    private JComboBox m_setsComboBox;
    private JTable m_keyTable;
    
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
                    if (!ActionManager.isDocViewSpecific(names[i])) {
                        String binding = jsXe.getProperty(names[i]+".shortcut");
                        m_set.add(new GrabKeyDialog.KeyBinding(names[i], label, binding));
                    }
                }
            }
            MiscUtilities.quicksort(m_set, new KeyCompare());
            
            m_name = set.getLabel();
        }//}}}
        
        //{{{ getBindingAt()
        
        public GrabKeyDialog.KeyBinding getBindingAt(int row) {
            return (GrabKeyDialog.KeyBinding)m_set.elementAt(row);
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
        /**
         * Note: this returns a display value for the shortcut while
         * <code>setValueAt()</code> takes an internal value. Since we don't
         * use the default table editors, this works ok. 
         */
        public Object getValueAt(int row, int column) {
            switch (column) {
                case (0):
                    return ((GrabKeyDialog.KeyBinding)m_set.get(row)).label;
                case (1):
                    KeyEventTranslator.Key key = KeyEventTranslator.parseKey(((GrabKeyDialog.KeyBinding)m_set.get(row)).shortcut);
                    if (key != null) {
                        return key.toString();
                    } else {
                        return null;
                    }
                default:
                    return null;
            }
        }//}}}
        
        //{{{ setValueAt()
        /**
         * Note: this takes an internal shortcut value, while getValueAt()
         * will return a display value. Since we don't use the default
         * table editors this works ok.
         */
        public void setValueAt(Object value, int row, int col) {
            if (col == 0) {
                return;
            }
            
            Log.log(Log.DEBUG, this, "setting shortcut value: "+value);
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
        
        //{{{ save()
        /**
         * Saves the current config to the properties
         */
        public void save() {
            Iterator e = m_set.iterator();
            while(e.hasNext()) {
                GrabKeyDialog.KeyBinding binding = (GrabKeyDialog.KeyBinding)e.next();
                Log.log(Log.DEBUG, this, "saving "+binding.name+" shortcut value: "+binding.shortcut);
                jsXe.setProperty(binding.name + ".shortcut", binding.shortcut);
            }
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
        
        public Vector m_set;
        private String m_name;
        //}}}
        
    }//}}}
    
    //{{{ ComboBoxHandler class
    
    private class ComboBoxHandler implements ActionListener {
        
        public void actionPerformed(ActionEvent evt) {
            ActionSetTableModel newModel = (ActionSetTableModel)m_setsComboBox.getSelectedItem();

            if (m_currentModel != newModel) {
                m_currentModel = newModel;
                m_keyTable.setModel(m_currentModel);
            }
        }
    }//}}}
    
    //{{{ TableMouseHandler class
    
    private class TableMouseHandler extends MouseAdapter {
        
        public void mouseClicked(MouseEvent evt) {
            int row = m_keyTable.getSelectedRow();
            int col = m_keyTable.getSelectedColumn();
            if (col != 0 && row != -1) {
                Vector allBindings = createAllBindings();
                GrabKeyDialog gkd = new GrabKeyDialog(
                    GUIUtilities.getParentDialog(
                    ShortcutsOptionPane.this),
                    m_currentModel.getBindingAt(row),
                    allBindings);
                 if (gkd.isOK()) {
                    m_currentModel.setValueAt(gkd.getShortcut(),row,col);
                 }
            }
        }
    }//}}}
    
    //{{{ initModels()
    private void initModels() {
        m_models = new Vector();
        Iterator itr = ActionManager.getActionSets().iterator();
        while(itr.hasNext()) {
            ActionSet actionSet = (ActionSet)itr.next();
            if (actionSet.getActionCount() > 0) {
                String modelLabel = actionSet.getLabel();
                ActionSetTableModel model = new ActionSetTableModel(actionSet);
                //models may be empty due to having all view specific actions
                if (model.getRowCount() > 0) {
                    m_models.addElement(model);
                }
            }
        }
        MiscUtilities.quicksort(m_models,new MiscUtilities.StringICaseCompare());
        m_currentModel = (ActionSetTableModel)m_models.elementAt(0);
    }//}}}
    
    //{{{ createAllBindings()
    
    public Vector createAllBindings() {
        Vector set = new Vector();
        Iterator itr = m_models.iterator();
        while (itr.hasNext()) {
            ActionSetTableModel model = (ActionSetTableModel)itr.next();
            Iterator itr2 = model.m_set.iterator();
            while (itr2.hasNext()) {
                set.add((GrabKeyDialog.KeyBinding)itr2.next());
            }
        }
        return set;
    }//}}}
    
    //}}}
    
}