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

//{{{ jsXe classes
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
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
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
public class DefaultView extends JPanel implements DocumentView {
    
    //{{{ Private static members
    private static final String _VIEWNAME="tree";
    
    private static final Properties m_defaultProperties;
    
    static {
       // InputStream viewinputstream = DefaultView.class.getResourceAsStream("/net/sourceforge/jsxe/gui/view/"+_VIEWNAME+".props");
        InputStream viewinputstream = DefaultView.class.getResourceAsStream("/net/sourceforge/jsxe/gui/view/documentview.default.props");
        m_defaultProperties = new Properties();
        try {
            m_defaultProperties.load(viewinputstream);
        } catch (IOException ioe) {}
    }
    //}}}
    
    public static final String CONTINUOUS_LAYOUT = _VIEWNAME+".continuous.layout";
    public static final String VERT_SPLIT_LOCATION = _VIEWNAME+".splitpane.vert.loc";
    public static final String HORIZ_SPLIT_LOCATION = _VIEWNAME+".splitpane.horiz.loc";
    public static final String SHOW_COMMENTS = _VIEWNAME+".show.comment.nodes";
    public static final String SHOW_EMPTY_NODES = _VIEWNAME+".show.empty.nodes";
    
    //{{{ DefaultView constructor
    /**
     * Constructs a new DefaultView for the provided document.
     * @param document the document that this view shows
     * @throws IOException if the document cannot be viewed using this view.
     */
    public DefaultView(TabbedView view, XMLDocument document) throws IOException {
        
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
        
        add(horizSplitPane, BorderLayout.CENTER);
        
        //}}}
        
        setXMLDocument(view, document);
    }//}}}
    
    //{{{ setVisible()
    /**
     * Initializes the size of the split panes and shows the component.
     * @param b If true, shows this component; otherwise, hides this component.
     */
    public void setVisible(boolean b) {
        if (b) {
            Container parent = getParent();
            if (parent != null) {
                Dimension size = parent.getSize();
                float vertPercent = Integer.valueOf(m_document.getProperty(VERT_SPLIT_LOCATION)).floatValue();
                float horizPercent = Integer.valueOf(m_document.getProperty(HORIZ_SPLIT_LOCATION)).floatValue();
                
                int vertLoc = (int)((vertPercent/100.0)*size.getHeight());
                int horizLoc = (int)((horizPercent/100.0)*size.getWidth());
                
                vertSplitPane.setDividerLocation(vertLoc);
                horizSplitPane.setDividerLocation(horizLoc);
            }
        }
        super.setVisible(b);
    }//}}}
    
    //{{{ DocumentView methods

    //{{{ close()
    
    public boolean close(TabbedView view) {
        
        //m_document should only be null if setXMLDocument was never called.
        if (m_document != null) {
            Dimension size = getSize();
            
            String vert = Integer.toString((int)(vertSplitPane.getDividerLocation()/size.getHeight()*100));
            String horiz = Integer.toString((int)(horizSplitPane.getDividerLocation()/size.getWidth()*100));
            
            m_document.setProperty(VERT_SPLIT_LOCATION,vert);
            m_document.setProperty(HORIZ_SPLIT_LOCATION,horiz);
        }
        
        return true;
    }//}}}

    //{{{ getDescription()
    
    public String getDescription() {
        return "View a document in as a tree";
    }//}}}
    
    //{{{ getDocumentViewComponent
    
    public Component getDocumentViewComponent() {
        return this;
    }//}}}

    //{{{ getHumanReadableName()
    
    public String getHumanReadableName() {
        return "Tree View";
    }//}}}
    
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
        return new JMenu[] {};
    }//}}}
    
    //{{{ getOptionsPanel()
    
    public OptionsPanel getOptionsPanel() {
        return new DefaultViewOptionsPanel();
    }//}}}
    
    //{{{ getViewName()
    
    public String getViewName() {
        return _VIEWNAME;
    }//}}}
    
    //{{{ getXMLDocument()
    
    public XMLDocument getXMLDocument() {
        return m_document;
    }//}}}
    
    //{{{ setXMLDocument()
    
    public void setXMLDocument(TabbedView view, XMLDocument document) throws IOException {
        
        try {
            document.checkWellFormedness();
        } catch (Exception e) {
            String errormsg = "The tree view requires XML documents to be well-formed.\n\n"+
            e.toString();
            throw new IOException(errormsg);
        }
        
        ensureDefaultProps(document);
        
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
        boolean layout = Boolean.valueOf(document.getProperty(CONTINUOUS_LAYOUT)).booleanValue();
        vertSplitPane.setContinuousLayout(layout);
        horizSplitPane.setContinuousLayout(layout);
        
        //update the UI so that the components
        //are redrawn.
        attributesTable.updateUI();
        tree.updateUI();
        updateUI();
        
        //Make root element node expanded.
       // TreePath path = new TreePath(new Object[] { , document.getRootElementNode() });
       // tree.expandPath(path);
        
        if (m_document != null) {
            m_document.removeXMLDocumentListener(m_documentListener);
        }
        m_document = document;
        m_document.addXMLDocumentListener(m_documentListener);
    } //}}}
    
    //}}}
    
    //{{{ Private Members
    
    //{{{ canEditInJEditorPane()
    
    private boolean canEditInJEditorPane(DefaultViewTreeNode node) {
        return (node.getAdapterNode().getNodeValue() != null);
    }//}}}
    
    //{{{ ensureDefaultProps()
    
    private void ensureDefaultProps(XMLDocument document) {
        //get default properties from jsXe
        document.setProperty(CONTINUOUS_LAYOUT, document.getProperty(CONTINUOUS_LAYOUT, m_defaultProperties.getProperty(CONTINUOUS_LAYOUT)));
        document.setProperty(HORIZ_SPLIT_LOCATION, document.getProperty(HORIZ_SPLIT_LOCATION, m_defaultProperties.getProperty(HORIZ_SPLIT_LOCATION)));
        document.setProperty(VERT_SPLIT_LOCATION, document.getProperty(VERT_SPLIT_LOCATION, m_defaultProperties.getProperty(VERT_SPLIT_LOCATION)));
        document.setProperty(SHOW_COMMENTS, document.getProperty(SHOW_COMMENTS, m_defaultProperties.getProperty(SHOW_COMMENTS)));
        document.setProperty(SHOW_EMPTY_NODES, document.getProperty(SHOW_EMPTY_NODES, m_defaultProperties.getProperty(SHOW_EMPTY_NODES)));
    }//}}}
    
    //{{{ DefaultViewOptionsPanel class
    
    private class DefaultViewOptionsPanel extends OptionsPanel {
        
        //{{{ DefaultViewOptionsPanel constructor
        
        public DefaultViewOptionsPanel() {
            
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            
            setLayout(layout);
            
            int gridY = 0;
            
            boolean showCommentNodes = Boolean.valueOf(m_document.getProperty(SHOW_COMMENTS, "false")).booleanValue();
            boolean showEmptyNodes = Boolean.valueOf(m_document.getProperty(SHOW_EMPTY_NODES, "false")).booleanValue();
            boolean continuousLayout = Boolean.valueOf(m_document.getProperty(CONTINUOUS_LAYOUT, "false")).booleanValue();
            
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
            m_document.setProperty(SHOW_COMMENTS,(new Boolean(showCommentsCheckBox.isSelected())).toString());
            m_document.setProperty(SHOW_EMPTY_NODES,(new Boolean(showEmptyNodesCheckBox.isSelected())).toString());
            
            boolean layout = ContinuousLayoutCheckBox.isSelected();
            m_document.setProperty(CONTINUOUS_LAYOUT,(new Boolean(layout)).toString());
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
               ((DefaultViewTableModel)attributesTable.getModel()).setAdapterNode(selectedNode.getAdapterNode());
                
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
    
    private DefaultViewTree tree = new DefaultViewTree();
    private JEditorPane htmlPane = new JEditorPane("text/plain","");
    private JTable attributesTable = new JTable();
    private JSplitPane vertSplitPane;
    private JSplitPane horizSplitPane;
    private XMLDocument m_document;
    
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
    private XMLDocumentListener m_documentListener = new XMLDocumentListener() {///{{{
        
        //{{{ propertiesChanged
        
        public void propertiesChanged(XMLDocument source, String key) {}//}}}
        
        //{{{ structureChanged()
        
        public void structureChanged(XMLDocument source, AdapterNode location) {
            /*
            need to reload since saving can change the structure,
            like when splitting cdata sections
            */
            
            tree.updateUI();
            
            //Make root element node expanded.
           // TreePath path = new TreePath(new Object[] { m_buffer.getXMLDocument().getAdapterNode(), m_buffer.getXMLDocument().getRootElementNode() });
           // tree.expandPath(path);
            
            //clear the html pane
            htmlPane.setDocument(new DefaultViewDocument(m_document.getAdapterNode()));
            htmlPane.setEditable(false);
        }//}}}
        
    };//}}}

    //}}}

}
