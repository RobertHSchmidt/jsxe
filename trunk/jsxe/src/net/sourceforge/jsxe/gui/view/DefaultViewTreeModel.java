/*
DefaultViewTreeModel.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the adapter class that allows a XMLDocument class to serve
as the model for a JTree.

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
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.jsXe;
//}}}

//{{{ Swing components
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
//}}}

//{{{ AWT components
import java.awt.Component;
//}}}

//{{{ DOM classes
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//}}}

//{{{ DOM uses SAX Exceptions
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Enumeration;
//}}}

//}}}

/**
 * A tree model for the default view of jsXe that models
 * an XML document as a tree.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see DefaultView
 * @see DefaultViewTree
 */
public class DefaultViewTreeModel implements TreeModel {
    
    //{{{ DefaultViewTreeModel constructor
    
    /**
     * Constructs a new tree model for the default view for jsXe.
     * @param parent the parent gui component that is used to display errors
     *               when necessary.
     * @param doc the document that this tree model models
     */
    protected DefaultViewTreeModel(Component parent, XMLDocument doc) {
        m_document = doc;
        m_rootTreeNode = new DefaultViewTreeNode(m_document);
        view = parent;
    }//}}}

    //{{{ TreeModel methods
    
    //{{{ addTreeModelListener()
    
    public void addTreeModelListener( TreeModelListener listener ) {
        if ( listener != null && ! treeListenerList.contains( listener ) ) {
            treeListenerList.add( listener );
        }
    }//}}}
    
    //{{{ getChild()
    
    public Object getChild(Object parent, int index) {
        DefaultViewTreeNode node = (DefaultViewTreeNode)parent;
        int trueIndex = calculateIndex(node, index);
        javax.swing.tree.TreeNode child = node.getChildAt(trueIndex);
        return child;
    }//}}}
    
    //{{{ getChildCount()
    
    public int getChildCount(Object parent) {
        DefaultViewTreeNode node = (DefaultViewTreeNode)parent;
        int totalcount = node.getChildCount();
        int count = 0;
        
        /*
        We need to find out how many we actually want to display.
        */
        for (int i=0; i<totalcount; i++) {
            DefaultViewTreeNode child = (DefaultViewTreeNode)node.getChildAt(i);
            
            if (child != null) {
                if (displayNode(child)) {
                    count++;
                }
            }
        }
        return count;
    }//}}}
    
    //{{{ getIndexOfChild()
    
    public int getIndexOfChild(Object parent, Object child) {
        DefaultViewTreeNode node = (DefaultViewTreeNode)parent;
        DefaultViewTreeNode node2 = (DefaultViewTreeNode)child;
        int index = calculateIndex(node, node2);
        return index;
    }//}}}
    
    //{{{ getRoot()
    
    public Object getRoot() {
        return m_rootTreeNode;
    }//}}}
    
    //{{{ isLeaf()
    
    public boolean isLeaf(Object aNode) {
        // Return true for any node with no children
        return ((DefaultViewTreeNode)aNode).isLeaf();
    }//}}}
    
    //{{{ removeTreeModelListener()
    
    public void removeTreeModelListener(TreeModelListener listener) {
        if ( listener != null ) {
            treeListenerList.remove( listener );
        }
    }//}}}
    
    //{{{ valueForPathChanged()
    
    public void valueForPathChanged(TreePath path, Object newValue) {
        try {
            //get the nodes needed
            AdapterNode node = ((DefaultViewTreeNode)path.getLastPathComponent()).getAdapterNode();
            node.setNodeName(newValue.toString());
            //notify the listeners that tree nodes have changed
            fireTreeNodesChanged(new TreeModelEvent(this, path));
        } catch (DOMException dome) {
            JOptionPane.showMessageDialog(view, dome, "XML Error", JOptionPane.WARNING_MESSAGE);
        }
    }//}}}
    
    //}}}
    
    //{{{ Private members
    
    //{{{ calculateIndex()
    
    private int calculateIndex(DefaultViewTreeNode parent, int index) {
        boolean found = false;
        
        //massage the index so that it points returns
        //the correct child depending of if we are displaying
        //comments, empty nodes etc.
        
        //This should be changed later to make use of a node filter
        //or something similar.
        int newIndex = -1;
        int nodesFound = 0;
        int size = parent.getChildCount();
        for (int i=0; i<size && nodesFound<=index; i++) {
            DefaultViewTreeNode child = (DefaultViewTreeNode)parent.getChildAt(i);
            
            if (child != null) {
                if (displayNode(child)) {
                    nodesFound++;
                }
            }
            newIndex++;
            
        }
        return newIndex;
    }//}}}
    
    //{{{ calculateIndex()
    
    private int calculateIndex(DefaultViewTreeNode parent, DefaultViewTreeNode child) {
        int trueIndex = parent.getIndex(child);
        if (!displayNode(child)) {
            trueIndex = -1;
        }
        if (trueIndex != -1) {
            int index = -1;
            for (int i=0; i<=trueIndex; i++) {
                DefaultViewTreeNode otherChild = (DefaultViewTreeNode)parent.getChildAt(i);
                if (displayNode(otherChild)) {
                    index++;
                }
            }
            trueIndex = index;
        }
        return trueIndex;
    }//}}}
    
    //{{{ displayNode()
    
    private boolean displayNode(DefaultViewTreeNode node) {
        boolean showComments = Boolean.valueOf(m_document.getProperty(DefaultView.SHOW_COMMENTS, "false")).booleanValue();
        boolean showEmpty    = Boolean.valueOf(m_document.getProperty(DefaultView.SHOW_EMPTY_NODES, "false")).booleanValue();
        
        boolean displayNode = false;
        if (node != null) {
            displayNode = true;
            AdapterNode adapter = node.getAdapterNode();
            if (!showComments && adapter.getNodeType()==Node.COMMENT_NODE) {
                displayNode = false;
            }
            if (!showEmpty && adapter.getNodeType()==Node.TEXT_NODE && adapter.getNodeValue().trim().equals("")) {
                displayNode = false;
            }
            if (adapter.getNodeType()==Node.DOCUMENT_TYPE_NODE) {
                displayNode = false;
            }
        }
        return displayNode;
    }//}}}
    
    //{{{ fireTreeNodesChanged()
    
    private void fireTreeNodesChanged(TreeModelEvent e) {
        ListIterator listeners = treeListenerList.listIterator();
        while ( listeners.hasNext() ) {
            TreeModelListener listener = (TreeModelListener) listeners.next();
            listener.treeNodesChanged( e );
        }
    }//}}}
    
    //{{{ fireTreeNodesInserted()
    
    private void fireTreeNodesInserted(TreeModelEvent e) {
        ListIterator listeners = treeListenerList.listIterator();
        while ( listeners.hasNext() ) {
            TreeModelListener listener = (TreeModelListener) listeners.next();
            listener.treeNodesInserted( e );
        }
    }//}}}
    
    //{{{ fireTreeNodesRemoved()
    
    private void fireTreeNodesRemoved(TreeModelEvent e) {
        ListIterator listeners = treeListenerList.listIterator();
        while ( listeners.hasNext() ) {
            TreeModelListener listener = (TreeModelListener) listeners.next();
            listener.treeNodesRemoved( e );
        }
    }//}}}
    
    //{{{ fireTreeStructureChanged()
    
    private void fireTreeStructureChanged(TreeModelEvent e) {
        ListIterator listeners = treeListenerList.listIterator();
        while ( listeners.hasNext() ) {
            TreeModelListener listener = (TreeModelListener) listeners.next();
            listener.treeStructureChanged( e );
        }
    }//}}}

    Component view;
    
    private XMLDocument m_document;
    private DefaultViewTreeNode m_rootTreeNode;
    private ArrayList treeListenerList = new ArrayList();
    //}}}
}
