/*
PluginManagerDialog.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2002 Ian Lewis (IanLewis@member.fsf.org)

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
import net.sourceforge.jsxe.JARClassLoader;
import net.sourceforge.jsxe.ActionPlugin;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ Swing classes
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
//}}}

//{{{ AWT classes
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;
//}}}

//{{{ Java classes
import java.util.ArrayList;
//}}}

//}}}

/**
 * The plugin manager dialog for jsXe.
 *
 * @author <a href="mailto:IanLewis at member dot fsf dot org">Ian Lewis</a>
 * @version $Id$
 * @since jsXe 0.4 beta
 */
public class PluginManagerDialog extends EnhancedDialog implements ActionListener {
   
    //{{{ PluginManagerDialog constructor
    
    public PluginManagerDialog(TabbedView parent) {
        super(parent, Messages.getMessage("Plugin.Manager.Title"), true);
        setLocationRelativeTo(parent);
        
        final JTable table = new JTable(new PluginManagerTableModel());
        final JScrollPane tableView = new JScrollPane(table);
        
        final JTextArea descArea = new JTextArea();
        final JScrollPane textView = new JScrollPane(descArea);
        
        final JSplitPane centerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, tableView, textView);
        
        JPanel content = new JPanel(new BorderLayout(12,12));
        content.setBorder(new EmptyBorder(12,12,12,12));
        setContentPane(content);
        
        Box buttons = new Box(BoxLayout.X_AXIS);
        buttons.add(Box.createGlue());
        
        m_ok = new JButton(Messages.getMessage("common.close"));
        m_ok.addActionListener(this);
        buttons.add(m_ok);
       // buttons.add(Box.createHorizontalStrut(6));
       // getRootPane().setDefaultButton(m_ok);
       // cancel = new JButton("Cancel");
       // cancel.addActionListener(this);
       // buttons.add(cancel);
        
        buttons.add(Box.createGlue());
        
        //set up the plugin table
        ListSelectionModel model = table.getSelectionModel();
        model.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model.addListSelectionListener(new ListSelectionListener() {//{{{
            public void valueChanged(ListSelectionEvent e) {
                DefaultListSelectionModel model = (DefaultListSelectionModel)e.getSource();
                for (int i=0;i<m_pluginNames.size();i++) {
                    if (model.isSelectedIndex(i)) {
                        JARClassLoader loader = jsXe.getPluginLoader();
                        String releaseDate = loader.getPluginProperty(m_pluginNames.get(i).toString(), JARClassLoader.PLUGIN_RELEASE_DATE);
                        String author = loader.getPluginProperty(m_pluginNames.get(i).toString(), JARClassLoader.PLUGIN_AUTHOR);
                        String url = loader.getPluginProperty(m_pluginNames.get(i).toString(), JARClassLoader.PLUGIN_URL);
                        String desc = loader.getPluginProperty(m_pluginNames.get(i).toString(), JARClassLoader.PLUGIN_DESCRIPTION);
                        
                        StringBuffer text = new StringBuffer();
                        if (author != null && !author.equals("")) {
                            text.append("Author: ");
                            text.append(author);
                            text.append("\n");
                        }
                        if (releaseDate != null && !releaseDate.equals("")) {
                            text.append("Release Date: ");
                            text.append(releaseDate);
                            text.append("\n");
                        }
                        if (url != null && !url.equals("")) {
                            text.append("URL: ");
                            text.append(url);
                            text.append("\n");
                        }
                        if (desc != null) {
                            text.append(desc);
                        }
                        
                        descArea.setText(text.toString());
                    }
                }
            }
        });//}}}
        
        //resize the splitpane the first time shown
        addComponentListener(new ComponentListener() {//{{{
            
            public void componentHidden(ComponentEvent e) {}
            
            public void componentMoved(ComponentEvent e) {}
            
            public void componentResized(ComponentEvent e) {}
            
            public void componentShown(ComponentEvent e) {
                Container parent = getParent();
                if (parent != null) {
                    double splitLoc = 0.75;
                    centerPane.setDividerLocation(splitLoc);
                    removeComponentListener(this);
                }
            }
            
        });//}}}
        
        content.add(centerPane, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        
        loadGeometry(this, "pluginmgr");
        
        setVisible(true);
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
        
        //{{{ addTableModelListener()
        public void addTableModelListener(TableModelListener l) {
            //nothing
        }//}}}
        
        //{{{ getColumnClass()
        public Class getColumnClass(int columnIndex) {
            return "".getClass();
        }//}}}
        
        //{{{ getColumnCount()
        public int getColumnCount() {
            return 3;
        }//}}}
        
        //{{{ getColumnName()
        public String getColumnName(int columnIndex) {
            String name = null;
            switch (columnIndex) {
                case 0:
                    return Messages.getMessage("Plugin.Manager.Name.Column.Header");
                case 1:
                    return Messages.getMessage("Plugin.Manager.Version.Column.Header");
                case 2:
                    return Messages.getMessage("Plugin.Manager.Status.Column.Header");
                default:
                    throw new Error("Column out of range");
            }
        }//}}}
        
        //{{{ getRowCount()
        public int getRowCount() {
            return m_pluginNames.size();
        }//}}}
        
        //{{{ getValueAt()
        public Object getValueAt(int rowIndex, int columnIndex) {
            String value = null;
            JARClassLoader loader = jsXe.getPluginLoader();
            switch (columnIndex) {
                case 0:
                    return loader.getPluginProperty(m_pluginNames.get(rowIndex).toString(), JARClassLoader.PLUGIN_HUMAN_READABLE_NAME);
                case 1:
                    return loader.getPluginProperty(m_pluginNames.get(rowIndex).toString(), JARClassLoader.PLUGIN_VERSION);
                case 2:
                    ActionPlugin plugin = loader.getPlugin(m_pluginNames.get(rowIndex).toString());
                    if (plugin instanceof ActionPlugin.Broken) {
                        return Messages.getMessage("Plugin.Manager.Broken.Status");
                    } else {
                        return Messages.getMessage("Plugin.Manager.Loaded.Status");
                    }
                default:
                    throw new Error("Column out of range");
            }
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
        
    }//}}}
    
    //{{{ Private Members
    private JButton m_ok;
    private JButton m_cancel;
    private ArrayList m_pluginNames = jsXe.getPluginLoader().getAllPluginNames();
    //}}}
}
