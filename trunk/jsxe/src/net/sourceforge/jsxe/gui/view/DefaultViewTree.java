/*
DefaultViewTree.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the tree class that is used in the default view.

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

public class DefaultViewTree extends JTree {
    
    //{{{ DefaultViewTree constructor
    
    public DefaultViewTree() {
        
        //{{{ intitalize Drag n Drop
       // m_dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, treeDGListener);
       // m_dropTarget = new DropTarget(this, DnDConstants.ACTION_MOVE, treeDTListener, true);
        //}}}
        
        addMouseListener(new TreePopupListener());
        
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        setCellRenderer(new DefaultViewTreeCellRenderer());
        
        //Since elements are the only editable nodes...
        setCellEditor(new DefaultTreeCellEditor(this, new ElementTreeCellRenderer()));
        ToolTipManager.sharedInstance().registerComponent(this);
        
    }//}}}
    
    //{{{ isEditable()
    
    public static boolean isEditable(AdapterNode node) {
        return (node.getNodeType() == Node.ELEMENT_NODE);
    }//}}}
    
    //{{{ TreePopupListener class
    
    private class TreePopupListener extends MouseAdapter {
        
        //{{{ mousePressed()
        
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }//}}}
        
        //{{{ mouseReleased()
        
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }//}}}
        
        //{{{ maybeShowPopup()
        
        private void maybeShowPopup(MouseEvent e) {
            TreePath selPath = getPathForLocation(e.getX(), e.getY());
            if (e.isPopupTrigger() && selPath != null) {
                
                setSelectionPath(selPath);
                
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
                    popupMenuItem = new JMenuItem("Rename Node");
                    popupMenuItem.addActionListener(new RenameElementAction());
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
        }//}}}
    }//}}}

    //{{{ Actions
    
    //{{{ AddElementNodeAction class
    
    private class AddElementNodeAction implements ActionListener {
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            try {
                TreePath selPath = getLeadSelectionPath();
                if (selPath != null) {
                    AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
                    AdapterNode newNode = selectedNode.addAdapterNode("New_Element", "", Node.ELEMENT_NODE);
                    //The TreeModel doesn't automatically treeNodesInserted() yet
                   // updateComponents();
                    updateUI();
                }
            } catch (DOMException dome) {
                JOptionPane.showMessageDialog(DefaultViewTree.this, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
            }
        }//}}}
        
    }//}}}
    
    //{{{ RenameElementAction class
    
    private class RenameElementAction implements ActionListener {
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            TreePath selPath = getLeadSelectionPath();
            if (selPath != null) {
                startEditingAtPath(selPath);
            }
        }//}}}
        
    }//}}}
    
    //{{{ AddTextNodeAction class
    
    private class AddTextNodeAction implements ActionListener {
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            try {
                TreePath selPath = getLeadSelectionPath();
                if (selPath != null) {
                    AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
                    selectedNode.addAdapterNode("", "New Text Node", Node.TEXT_NODE);
                    //The TreeModel doesn't automatically treeNodesInserted() yet
                   // updateComponents();
                    updateUI();
                }
            } catch (DOMException dome) {
                JOptionPane.showMessageDialog(DefaultViewTree.this, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
            }
        }//}}}
        
    }//}}}
    
    //{{{ RemoveNodeAction class
    
    private class RemoveNodeAction implements ActionListener {
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            try {
                TreePath selPath = getLeadSelectionPath();
                if (selPath != null) {
                    AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
                    selectedNode.getParentNode().remove(selectedNode);
                    //The TreeModel doesn't automatically treeNodesRemoved() yet
                   // updateComponents();
                    updateUI();
                }
            } catch (DOMException dome) {
                JOptionPane.showMessageDialog(DefaultViewTree.this, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
            }
        }//}}}
        
    }//}}}
    
    //}}}
    
    //{{{ DefaultViewTreeCellRenderer class
    
    private class DefaultViewTreeCellRenderer extends DefaultTreeCellRenderer {
        
        //{{{ DefaultViewTreeCellRenderer constructor
        
        DefaultViewTreeCellRenderer() {
            m_defaultLeafIcon = getLeafIcon();
            m_defaultOpenIcon = getOpenIcon();
            m_defaultClosedIcon = getClosedIcon();
        }//}}}
        
        //{{{ getTreeCellRendererComponent()
        
        public Component getTreeCellRendererComponent(JTree tree, 
            Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
            
            int type = -1;
            
            try {
                AdapterNode node = (AdapterNode)value;
                type = node.getNodeType();
            } catch (ClassCastException e) {}
            
            setText(value.toString());
            this.selected = selected;
            
            switch (type) {
                case Node.ELEMENT_NODE:
                    setIcon(m_elementIcon);
                    setLeafIcon(m_elementIcon);
                    setOpenIcon(m_elementIcon);
                    setClosedIcon(m_elementIcon);
                    setToolTipText("Element Node");
                    break;
                case Node.TEXT_NODE:
                    setIcon(m_textIcon);
                    setLeafIcon(m_textIcon);
                    setOpenIcon(m_textIcon);
                    setClosedIcon(m_textIcon);
                    setToolTipText("Text Node");
                    break;
                case Node.CDATA_SECTION_NODE:
                    setIcon(m_CDATAIcon);
                    setLeafIcon(m_CDATAIcon);
                    setOpenIcon(m_CDATAIcon);
                    setClosedIcon(m_CDATAIcon);
                    setToolTipText("CDATA Section");
                    break;
                case Node.COMMENT_NODE:
                    setIcon(m_commentIcon);
                    setLeafIcon(m_commentIcon);
                    setOpenIcon(m_commentIcon);
                    setClosedIcon(m_commentIcon);
                    setToolTipText("Comment Node");
                    break;
                case Node.ENTITY_REFERENCE_NODE:
                    setIcon(m_externalEntityIcon);
                    setLeafIcon(m_externalEntityIcon);
                    setOpenIcon(m_externalEntityIcon);
                    setClosedIcon(m_externalEntityIcon);
                    setToolTipText("Entity Reference");
                    break;
                case Node.DOCUMENT_NODE:
                    setIcon(m_defaultClosedIcon);
                    setLeafIcon(m_defaultLeafIcon);
                    setOpenIcon(m_defaultOpenIcon);
                    setClosedIcon(m_defaultClosedIcon);
                    setToolTipText("XML Document");
                    break;
                default:
                    if (leaf) {
                        setIcon(m_defaultLeafIcon);
                    } else {
                        if (expanded) {
                            setIcon(m_defaultOpenIcon);
                        } else {
                            setIcon(m_defaultClosedIcon);
                        }
                    }
                    
                    setLeafIcon(m_defaultLeafIcon);
                    setOpenIcon(m_defaultOpenIcon);
                    setClosedIcon(m_defaultClosedIcon);
                    
                    setToolTipText("Unknown Node Type");
                    
                    break;
            }
            
            return this;
        }//}}}
        
        //{{{ Private members
        private Icon m_defaultLeafIcon;
        private Icon m_defaultOpenIcon;
        private Icon m_defaultClosedIcon;
        //}}}
        
    }//}}}
    
    //{{{ ElementTreeCellRenderer class
    
    private class ElementTreeCellRenderer extends DefaultTreeCellRenderer {
        
        //{{{ ElementTreeCellRenderer constructor
        
        public ElementTreeCellRenderer() {
            //only element nodes can be edited in the tree
            setIcon(m_elementIcon);
            setLeafIcon(m_elementIcon);
            setOpenIcon(m_elementIcon);
            setClosedIcon(m_elementIcon);
            setToolTipText("Element Node");
        }//}}}
        
        //{{{ getTreeCellRendererComponent
        
        public Component getTreeCellRendererComponent(JTree tree, 
            Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
            
            return this;
            
        }//}}}
        
    }//}}}

    //{{{ Drag n Drop classes
//    
//    //{{{ TransferableNode class
//    
//    private class TransferableNode implements Transferable {
//        
//        //{{{ TransferableNode constructor
//        
//        public TransferableNode(AdapterNode node) {
//            m_node = node;
//        }//}}}
//        
//        //{{{ getTransferDataFlavors()
//        
//        public synchronized DataFlavor[] getTransferDataFlavors() {
//            return flavors;
//        }//}}}
//        
//        //{{{ isDataFlavorSupported()
//        
//        public boolean isDataFlavorSupported(DataFlavor flavor) {
//            return (flavorList.contains(flavor));
//        }//}}}
//        
//        //{{{ getTransferData()
//        
//        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
//            if (stringFlavor.equals(flavor)) {
//                return m_node.serializeToString();
//            } else {
//                if (plainTextFlavor.equals(flavor)) {
//                    //Maybe I'll make this more robust if needed.
//                    String charset = flavor.getParameter("charset").trim();
//                    if (charset.equalsIgnoreCase("unicode")) {
//                        return new ByteArrayInputStream(this.string.getBytes("Unicode"));
//                    } else {
//                        return new ByteArrayInputStream(this.string.getBytes("iso8859-1"));
//                    }
//                } else {
//                    if (nodeFlavor.equals(flavor)) {
//                        return m_node;
//                    } else {
//                        throw new UnsupportedFlavorException(flavor);
//                    }
//                }
//            }
//        }//}}}
//        
//        //{{{ Private Members
//        private static final DataFlavor stringFlavor = DataFlavor.stringFlavor;
//        private static final DataFlavor plainTextFlavor = DataFlavor.plainTextFlavor;
//        private static final DataFlavor nodeFlavor = new DataFlavor(Class.forName("net.sourceforge.jsxe.dom.AdapterNode"), "XML Node");
//        
//        private static final DataFlavor[] flavors = {
//            plainTextFlavor,
//            stringFlavor,
//            nodeFlavor
//        };
//
//        private static final List flavorList = Arrays.asList( flavors );
//        private AdapterNode m_node;
//        //}}}
//        
//    }//}}}
//    
//    //{{{ TreeDragGestureListener class
//    
//    private class TreeDragGestureListener implements DragGestureListener {
//        
//        //{{{ dragGestureRecognized()
//        
//        public void dragGestureRecognized(DragGestureEvent dge) {
//            try {
//                Point origin = e.getDragOrigin();
//                TreePath path = tree.getPathForLocation(origin.getX(), origin.getY());
//                AdapterNode node = (AdapterNode)path.getLastPathComponent();
//                Transferable transferable = new TransferableNode(AdapterNode);
//                dragSource.startDrag(e, DragSource.DefaultCopyNoDrop, transferable, m_treeDSListener);
//            } catch( InvalidDnDOperationException idoe ) {
//                System.err.println( idoe );
//            }
//        }//}}}
//        
//    }//}}}
//    
//    //{{{ DefaultViewDragSourceListener class
//    
//    private DefaultViewDragSourceListener implements DragSourceListener {
//        
//        //{{{ dragEnter()
//        
//        public void dragEnter(DragSourceDragEvent e) {
//            DragSourceContext context = e.getDragSourceContext();
//            
//            int myaction = e.getDropAction();
//            if ((myaction & DnDConstants.ACTION_MOVE) != 0) {
//                context.setCursor(DragSource.DefaultMoveDrop);   
//            } else {
//                context.setCursor(DragSource.DefaultMoveNoDrop);   
//            }
//        }//}}}
//        
//        //{{{ dragDropEnd()
//        
//        public void dragDropEnd(DragSourceDropEvent dsde) {
//            if ( e.getDropSuccess() == false ) {
//                return;
//            }
//            
//            int dropAction = e.getDropAction();
//            if ( dropAction == DnDConstants.ACTION_MOVE ) {
//                //***** Do stuff *****
//            }
//        }//}}}
//        
//        //{{{ dragExit()
//        
//        public void dragExit(DragSourceEvent dse) {
//            
//        }//}}}
//        
//        //{{{ dragOver()
//        
//        public void dragOver(DragSourceDragEvent dsde) {
//            
//        }//}}}
//        
//        //{{{ dropActionChanged()
//        
//        public void dropActionChanged(DragSourceDragEvent dsde) {
//            
//        }//}}}
//        
//    }//}}}
//    
//    //{{{ DefaultViewDropTargetListener class
//    
//    private class TreeDropTargetListener implements DropTargetListener {
//        
//        public void dragEnter(DropTargetDragEvent e) {
//            if (isDragOk(e) == false) {
//                e.rejectDrag();      
//                return;
//            }
//            
//            e.acceptDrag(DnDConstants.ACTION_MOVE);
//        }
//        
//        public void dragOver(DropTargetDragEvent e) {
//            if (isDragOk(e) == false) {
//                e.rejectDrag();      
//                return;
//            }
//            e.acceptDrag(DnDConstants.ACTION_MOVE);      
//        }
//    
//        public void dropActionChanged(DropTargetDragEvent e) {
//            if(isDragOk(e) == false) {
//                e.rejectDrag();      
//                return;
//            }
//            e.acceptDrag(DnDConstants.ACTION_MOVE);      
//        }
//        
//        public void dragExit(DropTargetEvent e) {
//            DropLabel.this.borderColor=Color.green;            
//            showBorder(false);
//        }
//        
//        private boolean isDragOk(DropTargetDragEvent e) {
//            DataFlavor nodeFlavor = new DataFlavor();
//            if (e.isDataFlavorSupported(nodeFlavor)) {
//                chosen = flavors[i];
//            }
//
//            // we're saying that these actions are necessary      
//            if ((e.getSourceActions() & DnDConstants.ACTION_MOVE) == 0) {
//                return false;
//            }
//            return true;
//        }
//        
//    }//}}}
//    
    //}}}

    //{{{ Instance variables
    
    //{{{ Drag and Drop instance variables
   // private DragSource m_dragSource = DragSource.getDefaultDragSource();
   // private DragGestureListener m_treeDGListener = new TreeDragGestureListener();
   // private DragSourceListener m_treeDSListener = new ;
   // private DropTarget m_dropTarget ;
   // private DropTargetListener m_treeDTListener = new TreeDropTargetListener();
    //}}}

    //{{{ Icons
    private static final ImageIcon m_elementIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/Element.png"), "Element");
    private static final ImageIcon m_textIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/Text.png"), "Text");
    private static final ImageIcon m_CDATAIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/CDATA.png"), "CDATA");
    private static final ImageIcon m_commentIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/Comment.png"), "Comment");
    private static final ImageIcon m_externalEntityIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/ExternalEntity.png"), "External Entity");
   // private static final ImageIcon m_internalEntityIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/InternalEntity.png"), "Internal Entity");
   //}}}

    //}}}
}