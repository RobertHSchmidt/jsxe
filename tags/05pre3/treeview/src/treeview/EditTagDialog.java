/*
EditTagDialog.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2000, 2003 Slava Pestov
Portions Copyright (C) 2005 Ian Lewis (IanLewis@member.fsf.org)

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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/


package treeview;

//{{{ Imports
import org.w3c.dom.Node;
import java.awt.Frame;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.*;
import net.sourceforge.jsxe.dom.*;
import net.sourceforge.jsxe.dom.completion.*;
import net.sourceforge.jsxe.gui.EnhancedDialog;
import net.sourceforge.jsxe.util.MiscUtilities;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.util.Log;
//}}}

public class EditTagDialog extends EnhancedDialog {

    //{{{ EditTagDialog constructor
    /**
     * Allows you to edit an existing node that has an element declaration.
     */
    public EditTagDialog(Frame view, ElementDecl element,
        Map attributeValues, boolean elementEmpty, Map entityHash,
        List ids, XMLDocument document, AdapterNode node)
    {
        this(view, element, attributeValues, elementEmpty, entityHash, ids, document);
        m_newNode = node;
        org.w3c.dom.NamedNodeMap attributes = m_newNode.getAttributes();
        for (int i = 0; i < attributeModel.size(); i++) {
            Attribute attr = (Attribute)attributeModel.get(i);
            if (attributes.getNamedItem(attr.name) != null) {
                attr.value.value = m_newNode.getAttribute(attr.name);
                attr.set = true;
            }
        }
        updateTag();
    }//}}}
    
    //{{{ EditTagDialog constructor
    
    public EditTagDialog(Frame view, ElementDecl element,
        Map attributeValues, boolean elementEmpty, Map entityHash,
        List ids, XMLDocument document)
    {
        super(view,Messages.getMessage("Edit.Node.Dialog.Title"),true);

        m_document = document;
        this.element = element;
        this.entityHash = entityHash;

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(12,12,12,12));
        setContentPane(content);

        //{{{ Top panel with element name
        JPanel top = new JPanel(new BorderLayout(6,0));
        top.setBorder(new EmptyBorder(0,0,12,0));

        top.add(BorderLayout.WEST,new JLabel("Element name:"));

        top.add(BorderLayout.CENTER,new JLabel(element.name));

       // empty = new JCheckBox("Empty");
       // if (element.empty) {
       //     empty.setSelected(true);
       //     empty.setEnabled(false);
       // } else {
       //     empty.setSelected(elementEmpty);
       // }
       // empty.addActionListener(new ActionHandler());

       // top.add(BorderLayout.EAST,empty);
        content.add(BorderLayout.NORTH,top);
        //}}}

        //{{{ Attribute table
        JPanel center = new JPanel(new BorderLayout());
        attributeModel = createAttributeModel(element.getAttributes(), attributeValues,ids);
        attributes = new AttributeTable();
        attributes.setModel(new AttributeTableModel());
        attributes.setRowHeight(new JComboBox(new String[] { "template" })
                .getPreferredSize().height);

        attributes.getTableHeader().setReorderingAllowed(false);
        attributes.getColumnModel().getColumn(0).setPreferredWidth(30);
        attributes.setColumnSelectionAllowed(false);
        attributes.setRowSelectionAllowed(false);
        attributes.setCellSelectionEnabled(false);

        JScrollPane scroller = new JScrollPane(attributes);
        Dimension size = scroller.getPreferredSize();
        size.height = Math.min(size.width,200);
        scroller.setPreferredSize(size);
        center.add(BorderLayout.CENTER,scroller);
        //}}}

        //{{{ Preview field
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(new EmptyBorder(12,0,0,0));
        previewPanel.add(BorderLayout.NORTH,new JLabel("Preview:"));

        preview = new JTextArea(5,5);
        preview.setLineWrap(true);
        preview.setWrapStyleWord(true);
        preview.setEditable(false);
        previewPanel.add(BorderLayout.CENTER,new JScrollPane(preview));

        center.add(BorderLayout.SOUTH,previewPanel);
        //}}}

        content.add(BorderLayout.CENTER,center);

        //{{{ Buttons
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
        buttons.setBorder(new EmptyBorder(12,0,0,0));

        buttons.add(Box.createGlue());
        buttons.add(ok = new JButton(Messages.getMessage("common.ok")));
        ok.addActionListener(new ActionHandler());
        getRootPane().setDefaultButton(ok);

        buttons.add(Box.createHorizontalStrut(6));

        buttons.add(cancel = new JButton(Messages.getMessage("common.cancel")));
        cancel.addActionListener(new ActionHandler());
        buttons.add(cancel);

        buttons.add(Box.createGlue());

        content.add(BorderLayout.SOUTH,buttons);
        //}}}

        updateTag();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(view);
    } //}}}

    //{{{ ok() method
    public void ok() {
        int row = attributes.getSelectedRow();
        int column = attributes.getSelectedColumn();

        if (row != -1 && column != -1) {
            if (attributes.getCellEditor(row,column) != null) {
                attributes.getCellEditor(row,column).stopCellEditing();
            }
        }

        //update the m_newNode object
        if (m_newNode == null) {
            //node will be added to parent later
            m_newNode = m_document.newAdapterNode(null, element.name, "", Node.ELEMENT_NODE);
        }
        //remove all attributes
        org.w3c.dom.NamedNodeMap map = m_newNode.getAttributes();
        int len = map.getLength();
        for (int i=0; i<len; i++) {
            m_newNode.removeAttribute(map.item(0).getNodeName());
        }
        for (int i = 0; i < attributeModel.size(); i++) {
            Attribute attr = (Attribute)attributeModel.get(i);
            if (attr.set) {
                m_newNode.setAttribute(attr.name, attr.value.value);
            }
        }
        
        isOK = true;
        dispose();
    } //}}}

    //{{{ cancel() method
    public void cancel() {
        isOK = false;
        dispose();
    } //}}}

    //{{{ getNewTag() method
    public String getNewTag() {
        return (isOK ? newTag : null);
    } //}}}
    
    //{{{ getNewNode()
    
    public AdapterNode getNewNode() {
        return (isOK ? m_newNode : null);
    }//}}}

   // //{{{ isEmpty() method
   // public boolean isEmpty() {
   //     return empty.isSelected();
   // } //}}}

    //{{{ Private members

    //{{{ Instance variables
    private ElementDecl element;
    private Map entityHash;
   // private JCheckBox empty;
    private List attributeModel;
    private JTable attributes;
    private JTextArea preview;
    private JButton ok;
    private JButton cancel;
    private String newTag;
    private boolean isOK;
    private AdapterNode m_newNode;
    private XMLDocument m_document;
    //}}}

    //{{{ createAttributeModel() method
    private ArrayList createAttributeModel(List declaredAttributes, Map attributeValues, List ids) {
        ArrayList stringIDs = new ArrayList(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            stringIDs.add(((IDDecl)ids.get(i)).id);
        }

        ArrayList attributeModel = new ArrayList();
        for (int i = 0; i < declaredAttributes.size(); i++) {
            ElementDecl.AttributeDecl attr = (ElementDecl.AttributeDecl)declaredAttributes.get(i);

            boolean set;
            String value = (String)attributeValues.get(attr.name);
            if (value == null) {
                set = false;
                value = attr.value;
            } else {
                set = true;
            }

            if (attr.required) {
                set = true;
            }

            ArrayList values;
            if (attr.type.equals("IDREF") && stringIDs.size() > 0) {
                values = stringIDs;
                if (value == null) {
                    value = (String)stringIDs.get(0);
                }
            } else {
                values = attr.values;
                if (value == null && values != null && values.size() > 0) {
                    value = (String)values.get(0);
                }
            }

            attributeModel.add(new Attribute(set,attr.name, value,values,attr.type,attr.required));
        }

        Collections.sort(attributeModel,new AttributeCompare());

        return attributeModel;
    } //}}}

    //{{{ updateTag() method
    private void updateTag() {
        
        StringBuffer buf = new StringBuffer("<");
        buf.append(element.name);
        
        for (int i = 0; i < attributeModel.size(); i++) {
            Attribute attr = (Attribute)attributeModel.get(i);
            if (!attr.set) {
                continue;
            }
            
            buf.append(' ');
            String attrName = attr.name;
            
            buf.append(attr.name);
            
            buf.append("=\"");
            if (attr.value.value != null) {
                buf.append(MiscUtilities.charactersToEntities(attr.value.value,entityHash));
            }
            buf.append("\"");
        }
        
       // if (empty.isSelected()) {
       //     buf.append("/");
       // }
        
        buf.append(">");
        
        newTag = buf.toString();
        
        preview.setText(newTag);
    } //}}}

    //}}}

    //{{{ Inner classes

    //{{{ ActionHandler class
    private class ActionHandler implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
           // if(evt.getSource() == empty)
           //         updateTag();
           if (evt.getSource() == ok) {
               ok();
           } else {
               if (evt.getSource() == cancel) {
                    cancel();
               }
           }
        }
    } //}}}

    //{{{ Attribute class
    static class Attribute {
        //{{{ Instance variables
        boolean set;

        String name;
        Value value;
        String type;
        boolean required;
        //}}}

        //{{{ Attribute constructor
        Attribute(boolean set, String name, String value, ArrayList values, String type, boolean required) {
            this.set = set;
            this.name = name;
            this.value = new Value(value,values);
            this.type = type;
            this.required = required;
        } //}}}

        //{{{ Value class
        static class Value {
            String value;
            ArrayList values;

            Value(String value, ArrayList values) {
                this.value = value;
                this.values = values;
            }

            public String toString() {
                return value;
            }
        } //}}}
    } //}}}

    //{{{ AttributeCompare class
    static class AttributeCompare implements Comparator {
        public int compare(Object obj1, Object obj2) {
            Attribute attr1 = (Attribute)obj1;
            Attribute attr2 = (Attribute)obj2;

            // put required attributes at the top
            if (attr1.required && !attr2.required) {
                return -1;
            } else {
                if (!attr1.required && attr2.required) {
                    return 1;
                } else {
                    return MiscUtilities.compareStrings(attr1.name,attr2.name,true);
                }
            }
        }
    } //}}}

    private static ComboValueRenderer comboRenderer = new ComboValueRenderer();

    //{{{ AttributeTable class
    private class AttributeTable extends JTable {
        //{{{ getCellEditor() method
        public TableCellEditor getCellEditor(int row, int column) {
            Object value = getModel().getValueAt(row,column);
            if (value instanceof Attribute.Value) {
                return comboRenderer;
            }

            return super.getCellEditor(row,column);
        } //}}}

        //{{{ getCellRenderer() method
        public TableCellRenderer getCellRenderer(int row, int column) {
            Object value = getModel().getValueAt(row,column);
            if (value instanceof Attribute.Value) {
                return comboRenderer;
            }

            return super.getCellRenderer(row,column);
        } //}}}
    } //}}}

    //{{{ AttributeTableModel class
    private class AttributeTableModel extends AbstractTableModel {
        //{{{ getColumnCount() method
        public int getColumnCount() {
            return 4;
        } //}}}

        //{{{ getRowCount() method
        public int getRowCount() {
                return attributeModel.size();
        } //}}}

        //{{{ getColumnClass() method
        public Class getColumnClass(int col) {
            if (col == 0) {
                return Boolean.class;
            } else {
                return String.class;
            }
        } //}}}

        //{{{ getColumnName() method
        public String getColumnName(int col) {
            switch(col) {
                case 0:
                    return "Set";
                case 1:
                    return "Attribute";
                case 2:
                    return "Type";
                case 3:
                    return "Value";
                default:
                    throw new InternalError();
            }
        } //}}}

        //{{{ isCellEditable() method
        public boolean isCellEditable(int row, int col) {
            if (col != 1 && col != 2) {
                return true;
            } else {
                return false;
            }
        } //}}}

        //{{{ getValueAt() method
        public Object getValueAt(int row, int col) {
            Attribute attr = (Attribute)attributeModel.get(row);
            switch(col) {
                case 0:
                    return new Boolean(attr.set);
                case 1:
                    return attr.name;
                case 2:
                    if (attr.required) {
                        if (attr.type.startsWith("(")) {
                            return "#REQUIRED";
                        } else {
                            return attr.type + ", " + "#REQUIRED";
                        }
                    } else {
                        if (attr.type.startsWith("(")) {
                            return "";
                        } else {
                            return attr.type;
                        }
                    }
                case 3:
                    if (attr.value.values != null) {
                        return attr.value;
                    } else {
                        return attr.value.value;
                    }
                default:
                    throw new InternalError();
            }
        } //}}}

        //{{{ setValueAt() method
        public void setValueAt(Object value, int row, int col) {
            Attribute attr = (Attribute)attributeModel.get(row);
            switch(col) {
                case 0:
                    if (attr.required) {
                        return;
                    }

                    attr.set = ((Boolean)value).booleanValue();
                    break;
                case 3:
                    String sValue;
                    if (value instanceof IDDecl) {
                        sValue = ((IDDecl)value).id;
                    } else {
                        sValue = value.toString();
                    }
                    if (equal(attr.value.value,sValue)) {
                        return;
                    }

                    attr.set = true;
                    attr.value.value = sValue;
                    break;
            }

            fireTableRowsUpdated(row,row);

            updateTag();
        } //}}}

        //{{{ equal() method
        private boolean equal(String str1, String str2) {
            if (str1 == null || str1.length() == 0) {
                if (str2 == null || str2.length() == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (str2 == null) {
                    return false;
                } else {
                    return str1.equals(str2);
                }
            }
        } //}}}
    } //}}}

    //{{{ ComboValueRenderer class
    static class ComboValueRenderer extends DefaultCellEditor implements TableCellRenderer {
        JComboBox editorCombo;
        JComboBox renderCombo;

        //{{{ ComboValueRenderer constructor
        ComboValueRenderer() {
            this(new JComboBox());
        } //}}}

        //{{{ ComboValueRenderer constructor
        // this is stupid. why can't you reference instance vars
        // in a super() invocation?
        ComboValueRenderer(JComboBox comboBox) {
            super(comboBox);
            this.editorCombo = comboBox;
            editorCombo.setEditable(true);
            this.renderCombo = new JComboBox();
            renderCombo.setEditable(true);
        } //}}}

        //{{{ getTableCellEditorComponent() method
        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column)
        {
            Attribute.Value _value = (Attribute.Value)value;
            editorCombo.setModel(new DefaultComboBoxModel(_value.values.toArray()));
            return super.getTableCellEditorComponent(table, _value.value,isSelected,row,column);
        } //}}}

        //{{{ getTableCellRendererComponent() method
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            Attribute.Value _value = (Attribute.Value)value;
            renderCombo.setModel(new DefaultComboBoxModel(_value.values.toArray()));
            renderCombo.setSelectedItem(_value.value);
            return renderCombo;
        } //}}}
    } //}}}

    //}}}
}
