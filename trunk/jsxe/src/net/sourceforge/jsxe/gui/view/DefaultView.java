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

This file written by ian Lewis (iml001@bridgewater.edu)
Copyright (C) 2002 by ian Lewis

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
import net.sourceforge.jsxe.gui.TabbedView;
//}}}

//{{{ Swing components
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
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
import java.awt.Component;
import java.awt.Dimension;
//}}}

//{{{ DOM classes
import org.w3c.dom.Node;
//}}}

//{{{ Java base classes
import java.util.Enumeration;
import java.util.Vector;
//}}}

//}}}

public class DefaultView extends DocumentView {
    
    protected DefaultView() {//{{{
        
        JScrollPane treeView = new JScrollPane(tree);
        
        //Create html editor pane
        htmlPane.setEditable(true);
        JScrollPane htmlView = new JScrollPane(htmlPane);
        
        //create a table model
        JScrollPane attrView = new JScrollPane(attributesTable);
        
        //layout of the panes is defined by splitpanes
        vertSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treeView, attrView);
        vertSplitPane.setContinuousLayout(true);
        
        horizSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vertSplitPane, htmlView ); 
        horizSplitPane.setContinuousLayout(true);
        
        tree.addTreeSelectionListener(new DefaultTreeSelectionListener(this));
        
        setLayout(new BorderLayout());
        add(horizSplitPane, BorderLayout.CENTER);
        
        vertSplitPane.setDividerLocation(200);
        horizSplitPane.setDividerLocation(200);
    } //}}}
    
    public void setDocument(TabbedView view, XMLDocument document) {//{{{
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
        
        //update the UI so that the components
        //are redrawn.
        attributesTable.updateUI();
        tree.updateUI();
        updateUI();
        currentdoc = document;
    } //}}}
    
    public JMenu[] getMenus() {//{{{
        JMenu[] menus = new JMenu[1];
        //{{{ Create Edit Menu
        JMenu editMenu = new JMenu("Edit");
            JMenuItem menuItem = new JMenuItem("Undo");
           // menuItem.addActionListener( new EditUndoAction() );
            editMenu.add( menuItem );
            menuItem = new JMenuItem("Redo");
           // menuItem.addActionListener( new EditRedoAction() );
            editMenu.add( menuItem );
            editMenu.addSeparator();
            menuItem = new JMenuItem("Cut");
           // menuItem.addActionListener( new EditCutAction() );
            editMenu.add( menuItem );
            menuItem = new JMenuItem("Copy");
           // menuItem.addActionListener( new EditCopyAction() );
            editMenu.add( menuItem );
            menuItem = new JMenuItem("Paste");
           // menuItem.addActionListener( new EditPasteAction() );
            editMenu.add( menuItem );
        //}}}
        menus[0] = editMenu;
        return menus;
    }//}}}
    
    public XMLDocument getXMLDocument() {//{{{
        return currentdoc;
    }//}}}
    
    //{{{ Private Members
    
    private boolean canEditInJTree(AdapterNode node) {//{{{
        return (node.getNodeType() == Node.ELEMENT_NODE);
    }//}}}
    
    private boolean canEditInJEditorPane(AdapterNode node) {//{{{
        return (node.getNodeType() == Node.TEXT_NODE);
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
                DefaultViewTableModel tableModel = new DefaultViewTableModel(parent, selectedNode);
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
    
    private JTree tree = new JTree();
    private JEditorPane htmlPane = new JEditorPane("text/plain","");
    private JTable attributesTable = new JTable();
    private JSplitPane vertSplitPane;
    private JSplitPane horizSplitPane;
    private XMLDocument currentdoc;
    private TableModelListener tableListener = new TableModelListener() {//{{{
        public void tableChanged(TableModelEvent e) {
           attributesTable.updateUI();
        }
    };//}}}
    private TreeModelListener treeListener = new TreeModelListener() {//{{{
        
        public void treeNodesChanged(TreeModelEvent e) {
            updateTree();
        }
        public void treeNodesInserted(TreeModelEvent e) {
            updateTree();
        }
        public void treeNodesRemoved(TreeModelEvent e) {
            updateTree();
        }
        public void treeStructureChanged(TreeModelEvent e) {
            updateTree();
        }
        
        private void updateTree() {
            int rowcount = tree.getRowCount();
            Vector allPaths = new Vector();
            for (int i=0; i<rowcount; i++) {
                TreePath root = tree.getPathForRow(i);
                Enumeration expandedPaths = tree.getExpandedDescendants(root);
                if (expandedPaths != null) {
                    allPaths.add(expandedPaths);
                }
            }
            Enumeration paths = allPaths.elements();
            while (paths.hasMoreElements()) {
                Enumeration nodepaths = (Enumeration) paths.nextElement();
                while (nodepaths.hasMoreElements()) {
                    TreePath path = (TreePath)nodepaths.nextElement();
                    tree.expandPath(path);
                }
            }
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
        
        private void updateTree() {
            int rowcount = tree.getRowCount();
            Vector allPaths = new Vector();
            for (int i=0; i<rowcount; i++) {
                TreePath root = tree.getPathForRow(i);
                Enumeration expandedPaths = tree.getExpandedDescendants(root);
                if (expandedPaths != null) {
                    allPaths.add(expandedPaths);
                }
            }
            Enumeration paths = allPaths.elements();
            while (paths.hasMoreElements()) {
                Enumeration nodepaths = (Enumeration) paths.nextElement();
                while (nodepaths.hasMoreElements()) {
                    TreePath path = (TreePath)nodepaths.nextElement();
                    tree.expandPath(path);
                }
            }
        }
        
    };//}}}
    //}}}
}
