/*
DefaultView.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains all the code for the default panel shows the xml document.
Since you don't want a panel for every document each with its own splitpane
dimensions this is a singleton. It may not be forever as jsXe may one day
may be multi-threaded and have multiple TabbedViews.

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
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.dom.AdapterNode;
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.gui.OptionsPanel;
import net.sourceforge.jsxe.gui.TabbedView;
//}}}

//{{{ Swing components
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
//}}}

//{{{ AWT components
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    
    protected DefaultView() {//{{{
        
        setLayout(new BorderLayout());
        
        JScrollPane treeView = new JScrollPane(tree);
        
        //{{{ Create html editor pane
        htmlPane.setEditable(true);
        JScrollPane htmlView = new JScrollPane(htmlPane);
        //}}}
        
        //create a table model
        JScrollPane attrView = new JScrollPane(attributesTable);
        
        tree.addTreeSelectionListener(new DefaultTreeSelectionListener(this));
        
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
        
        tree.addMouseListener(new TreePopupListener());
        attributesTable.addMouseListener(new TablePopupListener());

    } //}}}
    
    public void setDocument(TabbedView view, XMLDocument document) throws IOException {//{{{
        try {
            document.checkWellFormedness();
        } catch (Exception e) {
            String errormsg = "The tree view requires XML documents to be well-formed.\n\n"+
            e.toString();
            throw new IOException(errormsg);
        }
        
        close(view);
        
        AdapterNode adapter = new AdapterNode(document.getDocument());
        
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
        //Clear the right hand pane of previous values.
        htmlPane.setText("");
        
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
        currentDoc = document;
    } //}}}
    
    public JMenu[] getMenus() {//{{{
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
    
    public OptionsPanel getOptionsPanel() {//{{{
        return new DefaultViewOptionsPanel();
    }//}}}
    
    public XMLDocument getXMLDocument() {//{{{
        return currentDoc;
    }//}}}
    
    public void close(TabbedView view) {//{{{
        if (currentDoc != null) {
            String vert = Integer.toString(vertSplitPane.getDividerLocation());
            String horiz = Integer.toString(horizSplitPane.getDividerLocation());
            
            currentDoc.setProperty(viewname+".splitpane.vert.loc",vert);
            currentDoc.setProperty(viewname+".splitpane.horiz.loc",horiz);
        }
    }//}}}
    
    //{{{ Private Members
    
    private boolean canEditInJTree(AdapterNode node) {//{{{
        return (node.getNodeType() == Node.ELEMENT_NODE);
    }//}}}
    
    private boolean canEditInJEditorPane(AdapterNode node) {//{{{
        return (node.getNodeValue() != null);
    }//}}}
    
    private class DefaultViewOptionsPanel extends OptionsPanel {//{{{
        
        public DefaultViewOptionsPanel() {//{{{
            
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            
            setLayout(layout);
            
            int gridY = 0;
            
            boolean showCommentNodes = Boolean.valueOf(currentDoc.getProperty(viewname+".show.comment.nodes", "false")).booleanValue();
            boolean showEmptyNodes = Boolean.valueOf(currentDoc.getProperty(viewname+".show.empty.nodes", "false")).booleanValue();
            boolean continuousLayout = Boolean.valueOf(currentDoc.getProperty(viewname+".continuous.layout", "false")).booleanValue();
            
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
        
        public void saveOptions() {//{{{
            currentDoc.setProperty(viewname+".show.comment.nodes",(new Boolean(showCommentsCheckBox.isSelected())).toString());
            currentDoc.setProperty(viewname+".show.empty.nodes",(new Boolean(showEmptyNodesCheckBox.isSelected())).toString());
            
            boolean layout = ContinuousLayoutCheckBox.isSelected();
            currentDoc.setProperty(viewname+".continuous.layout",(new Boolean(layout)).toString());
            vertSplitPane.setContinuousLayout(layout);
            horizSplitPane.setContinuousLayout(layout);
            tree.updateUI();
        }//}}}
        
        public String getTitle() {//{{{
            return "Tree View Options";
        }//}}}
        
        private JCheckBox showCommentsCheckBox;
        private JCheckBox showEmptyNodesCheckBox;
        private JCheckBox ContinuousLayoutCheckBox;
        
    }//}}}
    
    private class DefaultTreeSelectionListener implements TreeSelectionListener {//{{{
        
        DefaultTreeSelectionListener(Component p) {
            parent=p;
        }
        
        public void valueChanged(TreeSelectionEvent e) {
            TreePath selPath = e.getPath();
            AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
            if ( selectedNode != null ) {
                
                //if the selected node can be edited in either the tree
                //or the text pane
                tree.setEditable(canEditInJTree(selectedNode));
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
        }
        
        private Component parent;
        
    }//}}}
    
    private class TreePopupListener extends MouseAdapter {//{{{
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
            if (e.isPopupTrigger() && selPath != null) {
                
                tree.setSelectionPath(selPath);
                
                AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
                JMenuItem popupMenuItem;
                JPopupMenu popup = new JPopupMenu();
                boolean showpopup = false;
                
                if (selectedNode.getNodeType() == Node.ELEMENT_NODE) {
                    popupMenuItem = new JMenuItem("Add Element Node");
                    popupMenuItem.addActionListener(new AddElementNodeAction());
                    popup.add(popupMenuItem);
                    popupMenuItem = new JMenuItem("Add Text Node");
                    popupMenuItem.addActionListener(new AddTextNodeAction());
                    popup.add(popupMenuItem);
                    showpopup = true;
                }
                //if the node is not the document or the document root.
                if (selectedNode.getNodeType() != Node.DOCUMENT_NODE && selectedNode.getParentNode().getNodeType() != Node.DOCUMENT_NODE) {
                    popupMenuItem = new JMenuItem("Remove Node");
                    popupMenuItem.addActionListener(new RemoveNodeAction());
                    popup.add(popupMenuItem);
                    showpopup = true;
                }
                if (showpopup)
                    popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }//}}}
    
    private class AddElementNodeAction implements ActionListener {//{{{
        
        public void actionPerformed(ActionEvent e) {
            try {
                TreePath selPath = tree.getLeadSelectionPath();
                if (selPath != null) {
                    AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
                    AdapterNode newNode = selectedNode.addNode("New_Element", "", Node.ELEMENT_NODE);
                    //The TreeModel doesn't automatically treeNodesInserted() yet
                    updateComponents();
                }
            } catch (DOMException dome) {
                JOptionPane.showMessageDialog(DefaultView.this, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
            }
        }
        
    }//}}}
    
    private class AddTextNodeAction implements ActionListener {//{{{
        
        public void actionPerformed(ActionEvent e) {
            try {
                TreePath selPath = tree.getLeadSelectionPath();
                if (selPath != null) {
                    AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
                    selectedNode.addNode("", "New Text Node", Node.TEXT_NODE);
                    //The TreeModel doesn't automatically treeNodesInserted() yet
                    updateComponents();
                }
            } catch (DOMException dome) {
                JOptionPane.showMessageDialog(DefaultView.this, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
            }
        }
        
    }//}}}
    
    private class RemoveNodeAction implements ActionListener {//{{{
        
        public void actionPerformed(ActionEvent e) {
            try {
                TreePath selPath = tree.getLeadSelectionPath();
                if (selPath != null) {
                    AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
                    selectedNode.remove();
                    //The TreeModel doesn't automatically treeNodesRemoved() yet
                    updateComponents();
                }
            } catch (DOMException dome) {
                JOptionPane.showMessageDialog(DefaultView.this, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
            }
        }
        
    }//}}}
    
    private class RemoveAttributeAction implements ActionListener {//{{{
        
        public RemoveAttributeAction(int r) {
            row = r;
        }
        
        public void actionPerformed(ActionEvent e) {
            DefaultViewTableModel model = (DefaultViewTableModel)attributesTable.getModel();
            AdapterNode node = model.getAdapterNode();
            node.removeAttributeAt(row);
            updateComponents();
        }
        
        private int row;
    }//}}}
    
    private class AddAttributeAction implements ActionListener {//{{{
        
        public void actionPerformed(ActionEvent e) {
            attributesTable.editCellAt(attributesTable.getRowCount()-1,0);
        }
        
    }//}}}
    
    private class TablePopupListener extends MouseAdapter {//{{{
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            int row = attributesTable.getSelectedRow();
            if (e.isPopupTrigger() && row != -1) {
                
                DefaultViewTableModel model = (DefaultViewTableModel)attributesTable.getModel();
                AdapterNode node = model.getAdapterNode();
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
        }
    }//}}}
    
    private void updateTree() {//{{{
        //We must settle for this but this doesn't
        //update the tree properly. When the text node
        //is changed the tree cell size doesn't change
        //resulting in ... characters in the tree.
        //Being able to update the tree the way we want
        //to is going to be require more complex use of
        //tree rendering I think.
        tree.treeDidChange();
    }//}}}
    
    /**
     * This is temporary. It causes GUI components to be updated but does
     * not retain their state.
     */
    private void updateComponents() {//{{{
        
        tree.updateUI();
        //Clear the right hand pane of previous values.
        htmlPane.setText("");
        
        //set the attributes table to the document itself
        AdapterNode adapter = new AdapterNode(currentDoc.getDocument());
        DefaultViewTableModel tableModel = new DefaultViewTableModel(this, adapter);
        attributesTable.setModel(tableModel);
        attributesTable.updateUI();
    }//}}}
    
    private JTree tree = new JTree();
    private JEditorPane htmlPane = new JEditorPane("text/plain","");
    private JTable attributesTable = new JTable();
    private JSplitPane vertSplitPane;
    private JSplitPane horizSplitPane;
    private XMLDocument currentDoc;
    
    private static final String viewname="documentview.default";
    private TableModelListener tableListener = new TableModelListener() {//{{{
        public void tableChanged(TableModelEvent e) {
           attributesTable.updateUI();
        }
    };//}}}
    private TreeModelListener treeListener = new TreeModelListener() {//{{{
        
        public void treeNodesChanged(TreeModelEvent e) {
            updateTree();
        }
        
        //These aren't called yet.
        public void treeNodesInserted(TreeModelEvent e) {
            updateComponents();
        }
        public void treeNodesRemoved(TreeModelEvent e) {
            updateComponents();
        }
        public void treeStructureChanged(TreeModelEvent e) {
            updateComponents();
        }
        
    };//}}}
    private DocumentListener docListener = new DocumentListener() {//{{{
        
        public void changedUpdate(DocumentEvent e) {
            updateTree();
        }
        public void insertUpdate(DocumentEvent e) {
            updateTree();
        }
        public void removeUpdate(DocumentEvent e) {
            updateTree();
        };
        
    };//}}}
    //}}}
}
