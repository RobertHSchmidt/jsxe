/*
AttributesTableModel.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the implementation of the table model that displays and
allows the user to edit XML attributes.

This file written by ian Lewis (iml001@bridgewater.edu)
Copyright ? 2002 by ian Lewis

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

//{{{ imports

//{{{ Swing components
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
//}}}

//{{{ Java base classes
import java.util.Enumeration;
import java.util.Vector;
//}}}

//{{{ DOM classes
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
//}}}

//}}}

public class AttributesTableModel implements TableModel {

    // {{{ Implemented TableModel methods

    public void addTableModelListener(TableModelListener l) {//{{{
        if ( l != null && !listenerList.contains( l ) ) {
            listenerList.addElement( l );
        }
    }//}}}

    public Class getColumnClass(int columnIndex) {//{{{
        //the attributes table should contain strings only
        return data[0].get(0).getClass();
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
        //data[0] and data[1] are associated and will be equal in size
        return data[0].size();
    }//}}}

    public Object getValueAt(int rowIndex, int columnIndex) {//{{{
        return data[columnIndex].get(rowIndex);
    }//}}}

    public boolean isCellEditable(int rowIndex, int columnIndex) {//{{{
        //Do not allow editing of attribute values that have no
        //attribute defined yet.
        if (columnIndex==1 && (((String)getValueAt(rowIndex,0)).equals(""))) {
            return false;
        }
        return true;
    }//}}}

    public void removeTableModelListener(TableModelListener l) {//{{{
        if ( l != null ) {
            listenerList.removeElement( l );
        }
    }//}}}

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {//{{{
        //This algorithm makes sure that there is an empty table row
        //at the bottom of the table for users to add new attributes.

        if (!aValue.equals("")) {
            //If setting a value on the last row (greater shouldn't happen)
            if (rowIndex + 1 >= getRowCount()) {
                //add intermittent blank rows (this should never happen
                //more than once)
                while (rowIndex + 1 > getRowCount()) {
                    data[columnIndex].addElement("");
                    data[columnIndex+1%2].addElement("");
                }
                //We need to check if there really is a change.
                //If we don't the UI croaks NullPointerExceptions
                //when trying to update the UI for the table.
                if (!aValue.equals(getValueAt(rowIndex, columnIndex))) {
                    //Set the value in the table
                    data[columnIndex].set(rowIndex, aValue);
                    //If you aren't setting the final blank row
                    //it needs to be added.
                    if (!aValue.equals("")) {
                        data[columnIndex].addElement("");
                        data[columnIndex+1%2].addElement("");
                    }
                    fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE));
                }
            } else {
                //We're not on the last row so just set the value.
                if (!aValue.equals(getValueAt(rowIndex, columnIndex))) {
                    data[columnIndex].set(rowIndex, aValue);
                    fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE));
                }
            }
        }
    }//}}}

    //}}}

    public void removeRow(int rowIndex) {//{{{
        data[0].removeElementAt(rowIndex);
        data[1].removeElementAt(rowIndex);
    }//}}}

    public void update(AdapterNode selectedNode) {//{{{
        NamedNodeMap attrs = selectedNode.getAttributes();
        if (attrs!=null) {
            while(getRowCount()>0) {
                removeRow(0);
            }
            for(int i = 0; i < attrs.getLength(); i++) {
                setValueAt(attrs.item(i).getNodeName(),i,0);
                setValueAt(attrs.item(i).getNodeValue(),i,1);
            }
        } else {
            if (selectedNode.getNodeType() == Node.ELEMENT_NODE) {
                //One extra table entry for adding an attribute
                setValueAt("",attrs.getLength(),0);
                setValueAt("",attrs.getLength(),1);
            }
            while(getRowCount()>0) {
                removeRow(0);
            }
        }
    }//}}}

    private void fireTableChanged(TableModelEvent e) {//{{{
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TableModelListener listener = (TableModelListener)listeners.nextElement();
            listener.tableChanged(e);
        }
    }//}}}

    /*
    *************************************************
    Private Data Fields
    *************************************************
    *///{{{
    private Vector[] data={
        new Vector(),
        new Vector()
    };
    private Vector listenerList = new Vector();
    //}}}
}
