/*
PluginManagerDialog.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the class for the plugin manager dialog

This file written by Ian Lewis (IanLewis@member.fsf.org)
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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe.gui;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.ActionPlugin;
//}}}

//{{{ Swing classes
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelListener;
//}}}

//{{{ AWT classes
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
//}}}

//{{{ Java classes
import java.util.ArrayList;
//}}}

//}}}

public class PluginManagerDialog extends EnhancedDialog implements ActionListener {
   
    //{{{ PluginManagerDialog constructor
    
    public PluginManagerDialog(TabbedView parent) {
        super(parent, "Plugin Manager", true);
        setLocationRelativeTo(parent);
        
        JTable table = new JTable(new PluginManagerTableModel());
        JScrollPane tableView = new JScrollPane(table);
        
        JPanel content = new JPanel(new BorderLayout(12,12));
        content.setBorder(new EmptyBorder(12,12,12,12));
        setContentPane(content);
        
        Box buttons = new Box(BoxLayout.X_AXIS);
        buttons.add(Box.createGlue());
        
        m_ok = new JButton("Close");
        m_ok.addActionListener(this);
        buttons.add(m_ok);
       // buttons.add(Box.createHorizontalStrut(6));
       // getRootPane().setDefaultButton(m_ok);
       // cancel = new JButton("Cancel");
       // cancel.addActionListener(this);
       // buttons.add(cancel);
        
        buttons.add(Box.createGlue());
        
        content.add(tableView, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        
        loadGeometry(this, "pluginmgr");
        show();
    }//}}}

    //{{{ ok()
    
    public void ok() {
        cancel();
    }//}}}
    
    //{{{ cancel()
    
    public void cancel() {
        saveGeometry(this, "pluginmgr");
        dispose();
    }//}}}
    
    //{{{ actionPerformed() method
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if(source == m_ok) {
            ok();
        }
        else if(source == m_cancel) {
            cancel();
        }
    } //}}}
    
    //{{{ PluginManagerTableModel class
    
    private class PluginManagerTableModel implements TableModel {
        
        //{{{ PluginManagerTableModel constructor
        
        public PluginManagerTableModel() {
            m_m_plugins = jsXe.getPluginLoader().getAllPlugins();
        }//}}}
        
        //{{{ addTableModelListener()
        
        public void addTableModelListener(TableModelListener l) {
            //nothing
        }//}}}
        
        //{{{ getColumnClass()
        
        public Class getColumnClass(int columnIndex) {
            return (new String()).getClass();
        }//}}}
        
        //{{{ getColumnCount()
        
        public int getColumnCount() {
            return 3;
        }//}}}
        
        //{{{ getColumnName()
        
        public String getColumnName(int columnIndex) {
            String name = null;
            if (columnIndex == 0) {
                name = "Name";
            }
            if (columnIndex == 1) {
                name = "Description";
            }
            if (columnIndex == 2) {
                name = "Version";
            }
            return name;
        }//}}}
        
        //{{{ getRowCount()
        
        public int getRowCount() {
            return m_m_plugins.size();
        }//}}}
        
        //{{{ getValueAt()
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            String value = null;
            if (columnIndex == 0) {
                value = ((ActionPlugin)m_m_plugins.get(rowIndex)).getHumanReadableName();
            }
            if (columnIndex == 1) {
                value = ((ActionPlugin)m_m_plugins.get(rowIndex)).getDescription();
            }
            if (columnIndex == 2) {
                value = ((ActionPlugin)m_m_plugins.get(rowIndex)).getVersion();
            }
            return value;
        }//}}}
        
        //{{{ isCellEditable()
        
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }//}}}
        
        //{{{ removeTableModelListener()
        
        public void removeTableModelListener(TableModelListener l) {
            // nothing. not supported
        }//}}}
        
        //{{{ setValueAt()
        
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            // nothing. not supported.
        }//}}}
        
        //{{{ Private instance variables
        ArrayList m_m_plugins;
        //}}}
        
    }//}}}
    
    //{{{ Private Members
    JButton m_ok;
    JButton m_cancel;
    //}}}
}