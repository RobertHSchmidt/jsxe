/*
DefaultViewTableModel.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the adapter class that allows an AdapterNode to serve
as the model for a JTable. This adapter class implements the model for the
JTable for viewing of node attributes.

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

package net.sourceforge.jsxe.gui.view;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.dom.AdapterNode;
//}}}

//{{{ Swing components
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
//}}}

//{{{ AWT components
import java.awt.Component;
//}}}

//{{{ DOM classes
import org.w3c.dom.DOMException;
//import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//}}}

//{{{ Java base classes
import java.util.Vector;
import java.util.Enumeration;
//}}}

//}}}

public class DefaultViewTableModel implements TableModel {
    
    protected DefaultViewTableModel(Component parent, AdapterNode adapterNode) {//{{{
        m_currentNode = adapterNode;
        m_view = parent;
        updateTable(m_currentNode);
    }//}}}

    // {{{ Implemented TableModel methods
    
    public void addTableModelListener(TableModelListener l) {//{{{
        if (l != null && !m_tableListenerList.contains(l) ) {
            m_tableListenerList.addElement(l);
        }
    }//}}}
    
    public Class getColumnClass(int columnIndex) {//{{{
        //the attributes table should contain strings only
        return (new String()).getClass();
    }//}}}
    
    public int getColumnCount() {//{{{
        //the attributes table will always contain 2 columns
        //an attribute and value
        return 2;
    }//}}}
    
    public String getColumnName(int columnIndex) {//{{{
        if (columnIndex==0)
            return "Attribute";
        else
            return "Value";
    }//}}}
    
    public int getRowCount() {//{{{
        return m_data[0].size();
    }//}}}

    public Object getValueAt(int rowIndex, int columnIndex) {//{{{
        return m_data[columnIndex].get(rowIndex);
    }//}}}
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {//{{{
        //Do not allow editing of attribute values that have no
        //attribute defined yet.
        if (columnIndex==1 && (((String)getValueAt(rowIndex,0)).equals(""))) {
            return false;
        }
        return true;
    }//}}}
    
    public void removeTableModelListener(TableModelListener listener) {//{{{
        if (listener!=null) {
            m_tableListenerList.removeElement(listener);
        }
    }//}}}
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {//{{{
        //pad with empty values if necessary (this shouldn't really happen)
        while (rowIndex+1 > getRowCount()) {
            m_data[columnIndex].add("");
        }
        try {
            //If setting a value on the last row
            if (rowIndex+1 == getRowCount()) {
                //we must be editing an attribute name.
                if (!aValue.equals("")) {
                    m_currentNode.setAttribute(aValue.toString(), "");
                   // data[columnIndex].setElementAt(aValue.toString(),rowIndex);
                   // data[0].add("");
                   // data[1].add("");
                    updateTable(m_currentNode);
                    
                    fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE));
                }
            
            //Otherwise we are editing an existing attribute.
            } else {
                //We don't want to allow the user to set an attribute
                //name to an empty string.
                if (columnIndex==1 || !aValue.equals("")) {
                    //We need to check if there really is a change.
                    //If we don't the UI croaks NullPointerExceptions
                    //when trying to update the UI for the table.
                    if (!aValue.equals(getValueAt(rowIndex, columnIndex))) {
                        if (columnIndex == 0) {
                            //we are renaming the attribute, remove the old one first
                            m_currentNode.removeAttribute((String)getValueAt(rowIndex, 0));
                            m_currentNode.setAttribute(aValue.toString(), (String)getValueAt(rowIndex, 1));
                        } else {
                            m_currentNode.setAttribute((String)getValueAt(rowIndex, 0), aValue.toString());
                        }
                       // data[columnIndex].setElementAt(aValue,rowIndex);
                        updateTable(m_currentNode);
                        fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE));
                    }
                }
            }
        //We need to catch this here unfortunately because this method is called by
        //a default table editor. Maybe this will change.
        } catch (DOMException dome) {
            JOptionPane.showMessageDialog(m_view, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
        }
    }//}}}
    
    //}}}
    
    public void removeRow(int row) {//{{{
        m_currentNode.removeAttributeAt(row);
        updateTable(m_currentNode);
        fireTableChanged(new TableModelEvent(this, row));
    }//}}}
    
    //{{{ Private members
    
    // {{{ Event notification methods
    private void fireTableChanged(TableModelEvent e) {//{{{
        Enumeration listeners = m_tableListenerList.elements();
        while (listeners.hasMoreElements()) {
            TableModelListener listener = (TableModelListener)listeners.nextElement();
            listener.tableChanged(e);
        }
    }//}}}
    // }}}
    
    private void updateTable(AdapterNode selectedNode) {//{{{
        m_currentNode = selectedNode;
        m_data[0].removeAllElements();
        m_data[1].removeAllElements();
        if (selectedNode!=null) {
            NamedNodeMap attrs = selectedNode.getAttributes();
            if (selectedNode.getNodeType() == Node.ELEMENT_NODE) {
                if (attrs!=null) {
                    for(int i = 0; i < attrs.getLength(); i++) {
                        m_data[0].add(attrs.item(i).getNodeName());
                        m_data[1].add(attrs.item(i).getNodeValue());
                    }
                    //One extra table entry for adding an attribute
                    m_data[0].add("");
                    m_data[1].add("");
                }
            }
        }
    }//}}}
    
    private Component m_view;
    
    private AdapterNode m_currentNode;
    private Vector m_tableListenerList = new Vector();
    private Vector[] m_data={
        new Vector(),
        new Vector()
    };
    //}}}

}
