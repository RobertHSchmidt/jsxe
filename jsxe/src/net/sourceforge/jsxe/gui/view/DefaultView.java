/*
DefaultView.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains all the code for the default panel shows the xml document.

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
import net.sourceforge.jsxe.*;
import net.sourceforge.jsxe.dom.*;
import net.sourceforge.jsxe.gui.*;
//}}}

//{{{ Swing components
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
//}}}

//{{{ AWT components
import java.awt.*;
import java.awt.event.*;
//}}}

//{{{ DOM classes
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
//}}}

//{{{ Java base classes
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
//}}}

//}}}

public class DefaultView extends DocumentView {
    
    //{{{ DefaultView constructor
    
    public DefaultView(DocumentBuffer buffer) throws IOException {
        
        setLayout(new BorderLayout());
        
        //{{{ init html editor pane
        htmlPane.setEditable(false);
        JScrollPane htmlView = new JScrollPane(htmlPane);
        //}}}
        
        //{{{ init attributes table
        JScrollPane attrView = new JScrollPane(attributesTable);
        
        attributesTable.setColumnSelectionAllowed(false);
        attributesTable.setRowSelectionAllowed(false);
        attributesTable.addMouseListener(new TablePopupListener());
        //}}}
        
        //{{{ init tree
        JScrollPane treeView = new JScrollPane(tree);
        tree.addTreeSelectionListener(new DefaultTreeSelectionListener(this));
        //}}}
        
        //{{{ Create and set up the splitpanes
        vertSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treeView, attrView);
        vertSplitPane.setContinuousLayout(false);
                
        horizSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vertSplitPane, htmlView);
        horizSplitPane.setContinuousLayout(false);
        
        //set to arbitrary size.
        vertSplitPane.setDividerLocation(200);
        horizSplitPane.setDividerLocation(200);
        
        add(horizSplitPane, BorderLayout.CENTER);
        
        //}}}
        
        setDocumentBuffer(buffer);
    }//}}}
    
    //{{{ setDocumentBuffer()
    
    public void setDocumentBuffer(DocumentBuffer buffer) throws IOException {
        
        XMLDocument document = buffer.getXMLDocument();
        
        try {
            document.checkWellFormedness();
        } catch (Exception e) {
            String errormsg = "The tree view requires XML documents to be well-formed.\n\n"+
            e.toString();
            throw new IOException(errormsg);
        }
        
        AdapterNode adapter = document.getAdapterNode();
        
        DefaultViewTreeModel treeModel = new DefaultViewTreeModel(this, document);
        DefaultViewTableModel tableModel = new DefaultViewTableModel(this, adapter);
        DefaultViewDocument styledDoc = new DefaultViewDocument(adapter);
        
        //This adapter may have the listener already.
        //addTreeModelListener does not add the listener
        //again if it is already added.
        tree.setModel(treeModel);
        attributesTable.setModel(tableModel);
        treeModel.addTreeModelListener(treeListener);
        tableModel.addTableModelListener(tableListener);
        
        htmlPane.setDocument(styledDoc);
        styledDoc.addDocumentListener(docListener);
        
        //get the splitpane layout options
        boolean layout = Boolean.valueOf(document.getProperty(viewname+".continuous.layout", "false")).booleanValue();
        vertSplitPane.setContinuousLayout(layout);
        horizSplitPane.setContinuousLayout(layout);
        vertSplitPane.setDividerLocation(Integer.valueOf(document.getProperty(viewname+".splitpane.vert.loc", "200")).intValue());
        horizSplitPane.setDividerLocation(Integer.valueOf(document.getProperty(viewname+".splitpane.horiz.loc", "200")).intValue());
        
        //update the UI so that the components
        //are redrawn.
        attributesTable.updateUI();
        tree.updateUI();
        updateUI();
        
        m_buffer = buffer;
    } //}}}
    
    //{{{ getMenus()
    
    public JMenu[] getMenus() {
        //Edit Menu doesn't work yet.
       // JMenu[] menus = new JMenu[1];
       // //{{{ Create Edit Menu
       // JMenu editMenu = new JMenu("Edit");
       //     JMenuItem menuItem = new JMenuItem("Undo");
       //    // menuItem.addActionListener( new EditUndoAction() );
       //     editMenu.add( menuItem );
       //     menuItem = new JMenuItem("Redo");
       //    // menuItem.addActionListener( new EditRedoAction() );
       //     editMenu.add( menuItem );
       //     editMenu.addSeparator();
       //     menuItem = new JMenuItem("Cut");
       //    // menuItem.addActionListener( new EditCutAction() );
       //     editMenu.add( menuItem );
       //     menuItem = new JMenuItem("Copy");
       //    // menuItem.addActionListener( new EditCopyAction() );
       //     editMenu.add( menuItem );
       //     menuItem = new JMenuItem("Paste");
       //    // menuItem.addActionListener( new EditPasteAction() );
       //     editMenu.add( menuItem );
       // //}}}
       // menus[0] = editMenu;
       // return menus;
        return null;
    }//}}}
    
    //{{{ getOptionsPanel()
    
    public OptionsPanel getOptionsPanel() {
        return new DefaultViewOptionsPanel();
    }//}}}
    
    //{{{ getDocumentBuffer()
    
    public DocumentBuffer getDocumentBuffer() {
        return m_buffer;
    }//}}}
    
    //{{{ getName()
    
    public String getName() {
        return "Tree View";
    }//}}}
    
    //{{{ close()
    
    public boolean close(TabbedView view) {
        
        //m_buffer should only be null if setBuffer was never called.
        if (m_buffer != null) {
            String vert = Integer.toString(vertSplitPane.getDividerLocation());
            String horiz = Integer.toString(horizSplitPane.getDividerLocation());
            
            m_buffer.setProperty(viewname+".splitpane.vert.loc",vert);
            m_buffer.setProperty(viewname+".splitpane.horiz.loc",horiz);
        }
        
        return true;
    }//}}}
    
    //{{{ Private Members
    
    //{{{ canEditInJEditorPane()
    
    private boolean canEditInJEditorPane(AdapterNode node) {
        return (node.getNodeValue() != null);
    }//}}}
    
    //{{{ DefaultViewOptionsPanel class
    
    private class DefaultViewOptionsPanel extends OptionsPanel {
        
        //{{{ DefaultViewOptionsPanel constructor
        
        public DefaultViewOptionsPanel() {
            
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            
            setLayout(layout);
            
            int gridY = 0;
            
            boolean showCommentNodes = Boolean.valueOf(m_buffer.getProperty(viewname+".show.comment.nodes", "false")).booleanValue();
            boolean showEmptyNodes = Boolean.valueOf(m_buffer.getProperty(viewname+".show.empty.nodes", "false")).booleanValue();
            boolean continuousLayout = Boolean.valueOf(m_buffer.getProperty(viewname+".continuous.layout", "false")).booleanValue();
            
            showCommentsCheckBox = new JCheckBox("Show comment nodes",showCommentNodes);
            showEmptyNodesCheckBox = new JCheckBox("Show whitespace-only nodes",showEmptyNodes);
            ContinuousLayoutCheckBox = new JCheckBox("Continuous layout for split-panes",continuousLayout);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 1;
            constraints.gridheight = 1;
            constraints.gridwidth  = 1;
            constraints.weightx    = 1.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(showCommentsCheckBox, constraints);
            add(showCommentsCheckBox);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 1;
            constraints.gridheight = 1;
            constraints.gridwidth  = 1;
            constraints.weightx    = 1.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(showEmptyNodesCheckBox, constraints);
            add(showEmptyNodesCheckBox);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 1;
            constraints.gridheight = 1;
            constraints.gridwidth  = 1;
            constraints.weightx    = 1.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(ContinuousLayoutCheckBox, constraints);
            add(ContinuousLayoutCheckBox);
        }//}}}
        
        //{{{ saveOptions()
        
        public void saveOptions() {
            m_buffer.setProperty(viewname+".show.comment.nodes",(new Boolean(showCommentsCheckBox.isSelected())).toString());
            m_buffer.setProperty(viewname+".show.empty.nodes",(new Boolean(showEmptyNodesCheckBox.isSelected())).toString());
            
            boolean layout = ContinuousLayoutCheckBox.isSelected();
            m_buffer.setProperty(viewname+".continuous.layout",(new Boolean(layout)).toString());
            vertSplitPane.setContinuousLayout(layout);
            horizSplitPane.setContinuousLayout(layout);
            tree.updateUI();
        }//}}}
        
        //{{{ getTitle()
        
        public String getTitle() {
            return "Tree View Options";
        }//}}}
        
        //{{{ Private Members
        
        private JCheckBox showCommentsCheckBox;
        private JCheckBox showEmptyNodesCheckBox;
        private JCheckBox ContinuousLayoutCheckBox;
        
        //}}}
        
    }//}}}
    
    //{{{ TablePopupListener class
    
    private class TablePopupListener extends MouseAdapter {
        
        //{{{ mousePressed()
        
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }//}}}

        //{{{ mouseReleased()
        
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }//}}}

        //{{{ maybeShowPopup
        
        private void maybeShowPopup(MouseEvent e) {
            Point point = new Point(e.getX(), e.getY());
            int row = attributesTable.rowAtPoint(point);
            int column = attributesTable.columnAtPoint(point);
            
            attributesTable.setColumnSelectionInterval(column, column);
            attributesTable.setRowSelectionInterval(row, row);
            
            if (e.isPopupTrigger() && row != -1) {
                
                DefaultViewTableModel model = (DefaultViewTableModel)attributesTable.getModel();
                JPopupMenu popup = new JPopupMenu();
                JMenuItem popupMenuItem;
                
                popupMenuItem = new JMenuItem("Add Attribute");
                popupMenuItem.addActionListener(new AddAttributeAction());
                popup.add(popupMenuItem);
                
                if (row != attributesTable.getRowCount()-1) {
                    popupMenuItem = new JMenuItem("Remove Attribute");
                    popupMenuItem.addActionListener(new RemoveAttributeAction(row));
                    popup.add(popupMenuItem);
                }
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }//}}}
        
    }//}}}

    //{{{ DefaultTreeSelectionListener class
    
    private class DefaultTreeSelectionListener implements TreeSelectionListener {
        
        //{{{ DefaultTreeSelectionListener constructor
        
        DefaultTreeSelectionListener(Component p) {
            parent=p;
        }//}}}
        
        //{{{ valueChanged()
        
        public void valueChanged(TreeSelectionEvent e) {
            TreePath selPath = e.getPath();
            AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
            if ( selectedNode != null ) {
                
                //if the selected node can be edited in either the tree
                //or the text pane
                tree.setEditable(DefaultViewTree.isEditable(selectedNode));
                htmlPane.setEditable(canEditInJEditorPane(selectedNode));
                
                //update the attributes table with the current info.
                DefaultViewTableModel tableModel = new DefaultViewTableModel(DefaultView.this, selectedNode);
                attributesTable.setModel(tableModel);
                tableModel.addTableModelListener(tableListener);
                attributesTable.updateUI();
                
                //update the text pane with the current info
                DefaultViewDocument styledDoc = new DefaultViewDocument(selectedNode);
                htmlPane.setDocument(styledDoc);
                styledDoc.addDocumentListener(docListener);
                htmlPane.updateUI();
                
            } else {
                htmlPane.setDocument(null);
            }
        }//}}}
        
        //{{{ Private members
        private Component parent;
        //}}}
        
    }//}}}
    
    //{{{ Actions
    
    //{{{ RemoveAttributeAction class
    
    private class RemoveAttributeAction implements ActionListener {
        
        //{{{ RemoveAttributeAction constructor
        
        public RemoveAttributeAction(int r) {
            row = r;
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            DefaultViewTableModel model = (DefaultViewTableModel)attributesTable.getModel();
            model.removeRow(row);
           // updateComponents();
            attributesTable.updateUI();
        }//}}}
        
        //{{{ Private members
        private int row;
        //}}}
    }//}}}
    
    //{{{ AddAttributeAction class
    
    private class AddAttributeAction implements ActionListener {
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            attributesTable.editCellAt(attributesTable.getRowCount()-1,0);
        }//}}}
        
    }//}}}
    
    //}}}

    //{{{ Instance variables
    
    private DefaultViewTree tree = new DefaultViewTree();
    private JEditorPane htmlPane = new JEditorPane("text/plain","");
    private JTable attributesTable = new JTable();
    private JSplitPane vertSplitPane;
    private JSplitPane horizSplitPane;
    private DocumentBuffer m_buffer;
    
    private static final String viewname="documentview.default";
    private TableModelListener tableListener = new TableModelListener() {//{{{
        public void tableChanged(TableModelEvent e) {
           attributesTable.updateUI();
        }
    };//}}}
    private TreeModelListener treeListener = new TreeModelListener() {//{{{
        
        public void treeNodesChanged(TreeModelEvent e) {
           // updateTree();
        }
        
        //These aren't called yet.
        public void treeNodesInserted(TreeModelEvent e) {
           // updateComponents();
            tree.updateUI();
        }
        public void treeNodesRemoved(TreeModelEvent e) {
           // updateComponents();
            tree.updateUI();
        }
        public void treeStructureChanged(TreeModelEvent e) {
           // updateComponents();
            tree.updateUI();
        }
        
    };//}}}
    private DocumentListener docListener = new DocumentListener() {//{{{
        
        public void changedUpdate(DocumentEvent e) {
           // updateTree();
        }
        public void insertUpdate(DocumentEvent e) {
           // updateTree();
        }
        public void removeUpdate(DocumentEvent e) {
           // updateTree();
        };
        
    };//}}}
    //}}}

    //}}}
}
