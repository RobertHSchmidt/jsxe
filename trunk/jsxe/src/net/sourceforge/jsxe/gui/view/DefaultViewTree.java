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
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
//}}}

//{{{ DOM classes
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
//}}}

//{{{ Java base classes
import java.io.IOException;
import java.util.*;
//}}}

//}}}

/**
 * The DefaultViewTree is the tree that is displayed in the upper-left of
 * the DefaultView in jsXe. This class defines methods specific to the tree
 * display.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class DefaultViewTree extends JTree {
    
    //{{{ DefaultViewTree constructor
    /**
     * Creates a new DefaultViewTree with the default TreeModel
     */
    public DefaultViewTree() {
        
        //{{{ intitalize Drag n Drop
        m_dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, m_treeDGListener);
        m_dropTarget = new DropTarget(this, m_acceptableActions, m_treeDTListener, true);
        //}}}
        
        addMouseListener(new TreePopupListener());
        setEditable(false);
       // addTreeSelectionListener(new TreeSelectionListener() {//{{{
       //     public void valueChanged(TreeSelectionEvent e) {
       //         TreePath selPath = e.getPath();
       //         AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
       //         if ( selectedNode != null ) {
       //             setEditable(isEditable(selectedNode));
       //         }
       //     }
       // });//}}}
        
        
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        setCellRenderer(new DefaultViewTreeCellRenderer());
        
        //Since elements are the only editable nodes...
        setCellEditor(new DefaultTreeCellEditor(this, new ElementTreeCellRenderer()));
        ToolTipManager.sharedInstance().registerComponent(this);
        
    }//}}}
    
    //{{{ Private members
    
    //{{{ isEditable()
    /**
     * Indicates if an AdapterNode is capable of being edited in
     * this tree.
     * @param node the node to check
     * @return true if the node can be edited in this tree
     */
    private static boolean isEditable(AdapterNode node) {
        if (node != null) {
            return (node.getNodeType() == Node.ELEMENT_NODE);
        } else {
            return false;
        }
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
                JMenu addNodeItem = new JMenu("Add");
                JPopupMenu popup = new JPopupMenu();
                boolean showpopup = false;
                boolean addNodeShown = false;
                
                if (selectedNode.getNodeType() == Node.ELEMENT_NODE) {
                    popupMenuItem = new JMenuItem(new AddNodeAction("New_Element", "", Node.ELEMENT_NODE));
                    addNodeItem.add(popupMenuItem);
                    popupMenuItem = new JMenuItem(new AddNodeAction("", "New Text Node", Node.TEXT_NODE));
                    addNodeItem.add(popupMenuItem);
                    popupMenuItem = new JMenuItem(new AddNodeAction("", "New CDATA Node", Node.CDATA_SECTION_NODE));
                    addNodeItem.add(popupMenuItem);
                    popupMenuItem = new JMenuItem(new AddNodeAction("", "New Comment Node", Node.COMMENT_NODE));
                    addNodeItem.add(popupMenuItem);
                   // popupMenuItem = new JMenuItem(new AddNodeAction("BLAH", "New Processing Instruction", Node.PROCESSING_INSTRUCTION_NODE));
                   // addNodeItem.add(popupMenuItem);
                   // popupMenuItem = new JMenuItem(new AddNodeAction("New_Entity", "", Node.ENTITY_REFERENCE_NODE));
                   // addNodeItem.add(popupMenuItem);
                    addNodeShown = true;
                    showpopup = true;
                }
                if (addNodeShown) {
                    popup.add(addNodeItem);
                }
                if (selectedNode.getNodeType() == Node.ELEMENT_NODE) {
                    popupMenuItem = new JMenuItem(new RenameElementAction());
                    popup.add(popupMenuItem);
                }
                //if the node is not the document or the document root.
                if (selectedNode.getNodeType() != Node.DOCUMENT_NODE && selectedNode.getParentNode().getNodeType() != Node.DOCUMENT_NODE) {
                    popupMenuItem = new JMenuItem(new RemoveNodeAction());
                    popup.add(popupMenuItem);
                    showpopup = true;
                }
                if (showpopup) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }//}}}
    }//}}}

    //{{{ Actions

    //{{{ AddNodeAction
    
    private class AddNodeAction extends AbstractAction {
        
        //{{{ AddNodeAction constructor
        
        public AddNodeAction(String name, String value, short nodeType) {
            m_name     = name;
            m_value    = value;
            m_nodeType = nodeType;
            String actionName;
            //{{{ set the Action.NAME
            switch (nodeType) {
                case Node.CDATA_SECTION_NODE:
                    actionName = "CDATA Section";
                    break;
                case Node.COMMENT_NODE:
                    actionName = "Comment";
                    break;
                case Node.ENTITY_NODE:
                    actionName = "Entity";
                    break;
                case Node.ENTITY_REFERENCE_NODE:
                    actionName = "Entity Reference";
                    break;
                case Node.ELEMENT_NODE:
                    actionName = "Element Node";
                    break;
                case Node.NOTATION_NODE:
                    actionName = "Notation Node";
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    actionName = "Processing Instruction";
                    break;
                case Node.TEXT_NODE:
                    actionName = "Text Node";
                    break;
                default:
                    actionName = "XML Node";
            }
            putValue(Action.NAME, actionName);//}}}
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            try {
                TreePath selPath = getLeadSelectionPath();
                if (selPath != null) {
                    AdapterNode selectedNode = (AdapterNode)selPath.getLastPathComponent();
                    selectedNode.addAdapterNode(m_name, m_value, m_nodeType);
                    setExpandedState(selPath, true);
                    //The TreeModel doesn't automatically treeNodesInserted() yet
                   // updateComponents();
                    updateUI();
                }
            } catch (DOMException dome) {
                JOptionPane.showMessageDialog(DefaultViewTree.this, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
            }
        }//}}}
        
        //{{{ Private members
        private short m_nodeType;
        private String m_name;
        private String m_value;
        //}}}
    }//}}}
    
    //{{{ RenameElementAction class
    
    private class RenameElementAction extends AbstractAction {
        
        //{{{ RenameElementAction constructor
        
        public RenameElementAction() {
            putValue(Action.NAME, "Rename Node");
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            TreePath selPath = getLeadSelectionPath();
            if (selPath != null) {
                //When editing is finished go back to uneditable
                getCellEditor().addCellEditorListener(new CellEditorListener() {//{{{
                    public void editingCanceled(ChangeEvent e) {
                        setEditable(false);
                        getCellEditor().removeCellEditorListener(this);
                    }
                    public void editingStopped(ChangeEvent e) {
                        setEditable(false);
                        getCellEditor().removeCellEditorListener(this);
                    }
                });//}}}
                setEditable(true);
                startEditingAtPath(selPath);
            }
        }//}}}
        
    }//}}}
    
    //{{{ RemoveNodeAction class
    
    private class RemoveNodeAction extends AbstractAction {
        
        //{{{ RemoveNodeAction constructor
        
        public RemoveNodeAction() {
            putValue(Action.NAME, "Remove Node");
        }//}}}
        
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
            m_defaultBackgroundSelectionColor = this.backgroundSelectionColor;
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
            if (value != null && m_dragOverTarget == value) {
                this.selected = true;
                backgroundSelectionColor = m_dragSelectionColor;
            } else {
                backgroundSelectionColor = m_defaultBackgroundSelectionColor;
            }
            
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
                    setIcon(m_internalEntityIcon);
                    setLeafIcon(m_internalEntityIcon);
                    setOpenIcon(m_internalEntityIcon);
                    setClosedIcon(m_internalEntityIcon);
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
        private Color m_defaultBackgroundSelectionColor;
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
    
    //{{{ TreeDragGestureListener class
    
    private class TreeDragGestureListener implements DragGestureListener {
        
        //{{{ dragGestureRecognized()
        
        public void dragGestureRecognized(DragGestureEvent dge) {
            try {
                Point origin = dge.getDragOrigin();
                TreePath path = getPathForLocation(origin.x, origin.y);
                //ignore dragging the Document root.
                if (path != null && !(isRootVisible() && getRowForPath(path) == 0)) {
                    AdapterNode node = (AdapterNode)path.getLastPathComponent();
                    Transferable transferable = new TransferableNode(node);
                    m_dragSource.startDrag(dge, DragSource.DefaultCopyNoDrop, transferable, m_treeDSListener);
                    
                    //Don't want to start editing if we are doing a drag
                    setEditable(false);
                }
            } catch( InvalidDnDOperationException idoe) {
                jsXe.exiterror(null, idoe.getMessage(), 1);
            }
        }//}}}
        
    }//}}}
    
    //{{{ DefaultViewDragSourceListener class
    
    private class DefaultViewDragSourceListener implements DragSourceListener {
        
        //{{{ dragEnter()
        
        public void dragEnter(DragSourceDragEvent dsde) {
            DragSourceContext context = dsde.getDragSourceContext();
            
            int myaction = dsde.getDropAction();
            if ((myaction & DnDConstants.ACTION_MOVE) != 0) {
                context.setCursor(DragSource.DefaultMoveDrop);   
            } else {
                context.setCursor(DragSource.DefaultMoveNoDrop);   
            }
        }//}}}
        
        //{{{ dragDropEnd()
        
        public void dragDropEnd(DragSourceDropEvent dsde) {
            //paint over the cue line no matter what.
            paintImmediately(m_cueLine);
            m_dragOverTarget = null;
            if ( dsde.getDropSuccess() == false ) {
                return;
            }
            
            int dropAction = dsde.getDropAction();
            if ( dropAction == DnDConstants.ACTION_MOVE ) {
                //***** Do stuff *****
            }
            
            //Set the editing stuff back to normal since drag is done.
            AdapterNode selectedNode = (AdapterNode)getLastSelectedPathComponent();
            if ( selectedNode != null ) {
                setEditable(isEditable(selectedNode));
            }
        }//}}}
        
        //{{{ dragExit()
        
        public void dragExit(DragSourceEvent dse) {
            paintImmediately(m_cueLine);
            m_dragOverTarget = null;
        }//}}}
        
        //{{{ dragOver()
        
        public void dragOver(DragSourceDragEvent dsde) {
            
        }//}}}
        
        //{{{ dropActionChanged()
        
        public void dropActionChanged(DragSourceDragEvent dsde) {
            
        }//}}}
        
    }//}}}
    
    //{{{ DefaultViewDropTargetListener class
    
    private class TreeDropTargetListener implements DropTargetListener {
        
        //{{{ dragEnter
        
        public void dragEnter(DropTargetDragEvent dtde) {
            if (isDragOk(dtde) == false) {
                dtde.rejectDrag();      
                return;
            }
            
            dtde.acceptDrag(DnDConstants.ACTION_MOVE);
        }//}}}
        
        //{{{ drop()
        
        public void drop(DropTargetDropEvent dtde) {
            if (!dtde.isDataFlavorSupported(TransferableNode.nodeFlavor)) {
                dtde.rejectDrop();
                return;
            }
            
            if ((dtde.getSourceActions() & m_acceptableActions ) == 0 ) {
                dtde.rejectDrop();
                return;
            }
            
            DataFlavor chosen = TransferableNode.nodeFlavor;
            
            Object data = null;
            try {
                data = dtde.getTransferable().getTransferData(chosen);
            } catch (UnsupportedFlavorException ufe) {
                jsXe.exiterror(null, ufe.getMessage(), 1);
            } catch (IOException ioe) {
                jsXe.exiterror(null, ioe.getMessage(), 1);
            }
            
            if (data == null)
                throw new NullPointerException();
            
            AdapterNode node = (AdapterNode)data;
            Point loc = dtde.getLocation();
            
            TreePath path = getPathForLocation(loc.x, loc.y);
            if (path == null) {
                dtde.rejectDrop();
                return;
            }
            AdapterNode parentNode = (AdapterNode)path.getLastPathComponent();
            TreePath dropPath = null;
            
            try {
                //Find out the relative location where I dropped.
                Rectangle bounds = getPathBounds(path);
                if (loc.y < bounds.y + (int)(bounds.height * 0.25)) {
                    //Insert before the node dropped on
                    if (parentNode != null) {
                        AdapterNode trueParent = parentNode.getParentNode();
                        if (trueParent != null) {
                            trueParent.addAdapterNodeAt(node, trueParent.index(parentNode));
                           // setExpandedState(path.getParentPath(), true);
                            dropPath = path;
                        } else {
                            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "HIERARCHY_REQUEST_ERR: An attempt was made to insert a node where it is not permitted");
                        }
                    } else {
                        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "HIERARCHY_REQUEST_ERR: An attempt was made to insert a node where it is not permitted");
                    }
                } else {
                    if (loc.y < bounds.y + (int)(bounds.height * 0.75)) {
                        //insert in the node
                        parentNode.addAdapterNode(node);
                        dropPath = path.pathByAddingChild(node);
                    } else {
                        if (parentNode != null) {
                            //insert after the node dropped on
                            AdapterNode trueParent = parentNode.getParentNode();
                            if (trueParent != null) {
                                trueParent.addAdapterNodeAt(node, trueParent.index(parentNode)+1);
                               // setExpandedState(path.getParentPath(), true);
                                dropPath = path;
                            } else {
                                throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "HIERARCHY_REQUEST_ERR: An attempt was made to insert a node where it is not permitted");
                            }
                        } else {
                            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "HIERARCHY_REQUEST_ERR: An attempt was made to insert a node where it is not permitted");
                        }
                    }
                }
                dtde.acceptDrop(m_acceptableActions);
            } catch (DOMException dome) {
                dtde.rejectDrop();
                JOptionPane.showMessageDialog(DefaultViewTree.this, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
            }
            
            m_dragOverTarget = null;
            paintImmediately(m_cueLine);
            dtde.dropComplete(true);
            updateUI();
           // makeVisible(dropPath);
        }//}}}
        
        //{{{ dragOver()
        
        public void dragOver(DropTargetDragEvent dtde) {
            if (isDragOk(dtde) == false) {
                dtde.rejectDrag();      
                return;
            }
            
            Point loc = dtde.getLocation();
            TreePath path = getPathForLocation(loc.x, loc.y);
            
            m_dragOverTarget = null;
            paintImmediately(m_cueLine);
            if (path != null) {
                Rectangle bounds = getPathBounds(path);
                //erase old cue line
                Graphics g = getGraphics();
                
                int x = bounds.x;
                int y = bounds.y;
                int width = bounds.width;
                int height = 2;
                
                g.setColor(m_dragSelectionColor);
                
                if (loc.y < bounds.y + (int)(bounds.height * 0.25)) {
                    //no change
                    g.fillRect(x, y, width, height);
                } else {
                    if (loc.y < bounds.y + (int)(bounds.height * 0.75)) {
                        //don't do anything right now
                        //Want to highlight the node we're dragging over
                        //in the future.
                       // x = 0;
                       // y = 0;
                       // width = 0;
                       // height = 0;
                        height=bounds.height;
                        /*
                        set the node that is being dragged over so that
                        the cell renderer knows that you are dragging over
                        */
                        m_dragOverTarget = path.getLastPathComponent();
                        paintImmediately(x,y,width,height);
                    } else {
                        y += bounds.height;
                        g.fillRect(x, y, width, height);
                    }
                }
                m_cueLine.setRect(x,y,width,height);
            }
            
            dtde.acceptDrag(DnDConstants.ACTION_MOVE);      
        }//}}}
        
        //{{{ dropActionChanged
        
        public void dropActionChanged(DropTargetDragEvent dtde) {
            if(isDragOk(dtde) == false) {
                dtde.rejectDrag();      
                return;
            }
            dtde.acceptDrag(DnDConstants.ACTION_MOVE);      
        }//}}}
        
        //{{{ dragExit
        
        public void dragExit(DropTargetEvent dte) {
            //Set the node that is dragged over to null
            m_dragOverTarget = null;
        }//}}}
        
        //{{{ Private Members
        
        //{{{ isDragOk()
        
        private boolean isDragOk(DropTargetDragEvent dtde) {
            //maybe someday I can accept text
            if (!dtde.isDataFlavorSupported(TransferableNode.nodeFlavor)) {
                return false;
            }

            // we're saying that these actions are necessary      
            if ((dtde.getSourceActions() & m_acceptableActions) == 0) {
                return false;
            }
            return true;
        }//}}}
        
        //}}}
        
    }//}}}
    
    //}}}

    //{{{ Instance variables
    
    //{{{ Drag and Drop instance variables
    private DragSource m_dragSource = DragSource.getDefaultDragSource();
    private DragGestureListener m_treeDGListener = new TreeDragGestureListener();
    private DragSourceListener m_treeDSListener = new DefaultViewDragSourceListener();
    private DropTarget m_dropTarget;
    private DropTargetListener m_treeDTListener = new TreeDropTargetListener();
    private int m_acceptableActions = DnDConstants.ACTION_MOVE;
    
    private Rectangle m_cueLine = new Rectangle();
    
    //the node that is being dragged over.
    private Object m_dragOverTarget = null;
    //the color used to highlight the drop target when dragging
    //Use RED for now
    private Color m_dragSelectionColor = Color.lightGray;
    //}}}

    //{{{ Icons
    private static final ImageIcon m_elementIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/Element.png"), "Element");
    private static final ImageIcon m_textIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/Text.png"), "Text");
    private static final ImageIcon m_CDATAIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/CDATA.png"), "CDATA");
    private static final ImageIcon m_commentIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/Comment.png"), "Comment");
   // private static final ImageIcon m_externalEntityIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/ExternalEntity.png"), "External Entity");
    private static final ImageIcon m_internalEntityIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/InternalEntity.png"), "Internal Entity");
   //}}}

    //}}}

    //}}}
}
