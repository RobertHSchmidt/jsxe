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
import javax.swing.text.PlainDocument;
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

/**
 * This is the default tree-like view in jsXe. It has a tree panel in the
 * upper-left, a table in the lower-left to display attributes of the currently
 * selected node, and a text panel on the right to display the value of the
 * currently selected node.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class DefaultView extends DocumentView {
    
    //{{{ DefaultView constructor
    /**
     * Constructs a new DefaultView for the provided document buffer.
     * @param buffer the buffer owning the document that this view shows
     * @throws IOException if the buffer cannot be viewed using this view.
     */
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
        vertSplitPane.setOneTouchExpandable(true);
        
        horizSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vertSplitPane, htmlView);
        horizSplitPane.setContinuousLayout(false);
        horizSplitPane.setOneTouchExpandable(true);
        
       // horizSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new java.beans.PropertyChangeListener() {
       //     public void propertyChange(java.beans.PropertyChangeEvent evt) {
       //         JSplitPane pane = (JSplitPane)evt.getSource();
       //         System.out.println("Packing");
       //         pane.resetToPreferredSizes();
       //     }
       // });
        
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
        
        ensureDefaultProps(buffer);
        
        AdapterNode adapter = document.getAdapterNode();
        
        DefaultViewTreeModel treeModel = new DefaultViewTreeModel(this, buffer);
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
        boolean layout = Boolean.valueOf(buffer.getProperty(_CONTINUOUS_LAYOUT)).booleanValue();
        vertSplitPane.setContinuousLayout(layout);
        horizSplitPane.setContinuousLayout(layout);
        vertSplitPane.setDividerLocation(Integer.valueOf(buffer.getProperty(_HORIZ_SPLIT_LOCATION)).intValue());
        horizSplitPane.setDividerLocation(Integer.valueOf(buffer.getProperty(_HORIZ_SPLIT_LOCATION)).intValue());
        
        //update the UI so that the components
        //are redrawn.
        attributesTable.updateUI();
        tree.updateUI();
        updateUI();
        
        //Make root element node expanded.
       // TreePath path = new TreePath(new Object[] { , document.getRootElementNode() });
       // tree.expandPath(path);
        
        if (m_buffer != null) {
            m_buffer.removeDocumentBufferListener(m_bufferListener);
        }
        m_buffer = buffer;
        m_buffer.addDocumentBufferListener(m_bufferListener);
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
            
            m_buffer.setProperty(_VERT_SPLIT_LOCATION,vert);
            m_buffer.setProperty(_HORIZ_SPLIT_LOCATION,horiz);
        }
        
        return true;
    }//}}}
    
    //{{{ Private Members
    
    //{{{ canEditInJEditorPane()
    
    private boolean canEditInJEditorPane(DefaultViewTreeNode node) {
        return (node.getAdapterNode().getNodeValue() != null);
    }//}}}
    
    //{{{ ensureDefaultProps()
    
    private void ensureDefaultProps(DocumentBuffer buffer) {
        //get default properties from jsXe
        buffer.setProperty(_CONTINUOUS_LAYOUT, buffer.getProperty(_CONTINUOUS_LAYOUT, jsXe.getProperty(_CONTINUOUS_LAYOUT)));
        buffer.setProperty(_HORIZ_SPLIT_LOCATION, buffer.getProperty(_HORIZ_SPLIT_LOCATION, jsXe.getProperty(_HORIZ_SPLIT_LOCATION)));
        buffer.setProperty(_HORIZ_SPLIT_LOCATION, buffer.getProperty(_HORIZ_SPLIT_LOCATION, jsXe.getProperty(_HORIZ_SPLIT_LOCATION)));
        buffer.setProperty(_SHOW_COMMENTS, buffer.getProperty(_SHOW_COMMENTS, jsXe.getProperty(_SHOW_COMMENTS)));
        buffer.setProperty(_SHOW_EMPTY_NODES, buffer.getProperty(_SHOW_EMPTY_NODES, jsXe.getProperty(_SHOW_EMPTY_NODES)));
    }//}}}
    
    //{{{ DefaultViewOptionsPanel class
    
    private class DefaultViewOptionsPanel extends OptionsPanel {
        
        //{{{ DefaultViewOptionsPanel constructor
        
        public DefaultViewOptionsPanel() {
            
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            
            setLayout(layout);
            
            int gridY = 0;
            
            boolean showCommentNodes = Boolean.valueOf(m_buffer.getProperty(_SHOW_COMMENTS, "false")).booleanValue();
            boolean showEmptyNodes = Boolean.valueOf(m_buffer.getProperty(_SHOW_EMPTY_NODES, "false")).booleanValue();
            boolean continuousLayout = Boolean.valueOf(m_buffer.getProperty(_CONTINUOUS_LAYOUT, "false")).booleanValue();
            
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
            m_buffer.setProperty(_SHOW_COMMENTS,(new Boolean(showCommentsCheckBox.isSelected())).toString());
            m_buffer.setProperty(_SHOW_EMPTY_NODES,(new Boolean(showEmptyNodesCheckBox.isSelected())).toString());
            
            boolean layout = ContinuousLayoutCheckBox.isSelected();
            m_buffer.setProperty(_CONTINUOUS_LAYOUT,(new Boolean(layout)).toString());
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
            DefaultViewTreeNode selectedNode = (DefaultViewTreeNode)selPath.getLastPathComponent();
            if ( selectedNode != null ) {
                
                //if the selected node can be edited in the text pane
                htmlPane.setEditable(canEditInJEditorPane(selectedNode));
                
                //update the attributes table with the current info.
                DefaultViewTableModel tableModel = new DefaultViewTableModel(DefaultView.this, selectedNode.getAdapterNode());
                attributesTable.setModel(tableModel);
                tableModel.addTableModelListener(tableListener);
                attributesTable.updateUI();
                
                //update the text pane with the current info
                DefaultViewDocument styledDoc = new DefaultViewDocument(selectedNode.getAdapterNode());
                htmlPane.setDocument(styledDoc);
                styledDoc.addDocumentListener(docListener);
                htmlPane.updateUI();
                
            } else {
                htmlPane.setDocument(new PlainDocument());
                htmlPane.setEditable(false);
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
    
    private static final String _VIEWNAME="documentview.default";
    private static final String _CONTINUOUS_LAYOUT = _VIEWNAME+".continuous.layout";
    private static final String _VERT_SPLIT_LOCATION = _VIEWNAME+".splitpane.vert.loc";
    private static final String _HORIZ_SPLIT_LOCATION = _VIEWNAME+".splitpane.horiz.loc";
    private static final String _SHOW_COMMENTS = _VIEWNAME+".show.comment.nodes";
    private static final String _SHOW_EMPTY_NODES = _VIEWNAME+".show.empty.nodes";
    
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
    private DocumentBufferListener m_bufferListener = new DocumentBufferListener() {///{{{
        
        //{{{ nameChanged
        
        public void nameChanged(DocumentBuffer source, String newName) {}//}}}
        
        //{{{ propertiesChanged
        
        public void propertiesChanged(DocumentBuffer source, String key) {}//}}}
        
        //{{{ bufferSaved()
        
        public void bufferSaved(DocumentBuffer source) {
            /*
            need to reload since saving can change the structure,
            like when splitting cdata sections
            */
            
            tree.updateUI();
            //Make root element node expanded.
            TreePath path = new TreePath(new Object[] { m_buffer.getXMLDocument().getAdapterNode(), m_buffer.getXMLDocument().getRootElementNode() });
            tree.expandPath(path);
            
            //clear the html pane
            htmlPane.setDocument(new DefaultViewDocument(m_buffer.getXMLDocument().getAdapterNode()));
            htmlPane.setEditable(false);
        }//}}}
        
    };//}}}
    //}}}

    //}}}
}
