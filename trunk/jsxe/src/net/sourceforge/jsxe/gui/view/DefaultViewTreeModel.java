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
import net.sourceforge.jsxe.DocumentBuffer;
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
import java.util.Vector;
import java.util.Properties;
import java.util.Enumeration;
//}}}

//}}}

public class DefaultViewTreeModel implements TreeModel {
    
    protected DefaultViewTreeModel(Component parent, DocumentBuffer doc) {//{{{
        m_buffer = doc;
        m_rootTreeNode = new DefaultViewTreeNode(m_buffer.getXMLDocument());
        view = parent;
    }//}}}

    // {{{ Implemented TreeModel methods
    
    public void addTreeModelListener( TreeModelListener listener ) {//{{{
        if ( listener != null && ! treeListenerList.contains( listener ) ) {
            treeListenerList.addElement( listener );
        }
    }//}}}
    
    public Object getChild(Object parent, int index) {//{{{
        DefaultViewTreeNode node = (DefaultViewTreeNode) parent;
        
        boolean showComments = Boolean.valueOf(m_buffer.getProperty("documentview.default.show.comment.nodes", "false")).booleanValue();
        boolean showEmpty    = Boolean.valueOf(m_buffer.getProperty("documentview.default.show.empty.nodes", "false")).booleanValue();
        
        boolean found = false;
        
        //massage the index so that it points returns
        //the correct child depending of if we are displaying
        //comments, empty nodes etc.
        
        //This should be changed later to make use of a node filter
        //or something similar.
        for (int i=0; i<=index; i++) {
            DefaultViewTreeNode child = (DefaultViewTreeNode)node.getChildAt(i);
            
            if (child != null) {
                AdapterNode adapter = child.getAdapterNode();
                if (!showComments && adapter.getNodeType()==Node.COMMENT_NODE) {
                    index++;
                }
                if (!showEmpty && adapter.getNodeType()==Node.TEXT_NODE && adapter.getNodeValue().trim().equals("")) {
                    index++;
                }
                if (adapter.getNodeType()==Node.DOCUMENT_TYPE_NODE) {
                    index++;
                }
            }
            
        }
        return node.getChildAt(index);
    }//}}}
    
    public int getChildCount(Object parent) {//{{{
        DefaultViewTreeNode node = (DefaultViewTreeNode)parent;
        int totalcount = node.getChildCount();
        int count = 0;
        
        boolean showComments = Boolean.valueOf(m_buffer.getProperty("documentview.default.show.comment.nodes", "false")).booleanValue();
        boolean showEmpty    = Boolean.valueOf(m_buffer.getProperty("documentview.default.show.empty.nodes", "false")).booleanValue();
        /*
        We need to find out how many we actually want to display.
        */
        for (int i=0; i<totalcount; i++) {
            DefaultViewTreeNode child = (DefaultViewTreeNode)node.getChildAt(i);
            
            if (child != null) {
                System.out.println(i+".) "+child.toString());
                boolean displayNode = true;
                AdapterNode adapter = child.getAdapterNode();
                if (!showComments && adapter.getNodeType()==Node.COMMENT_NODE) {
                    displayNode = false;
                }
                if (!showEmpty && adapter.getNodeType()==Node.TEXT_NODE && adapter.getNodeValue().trim().equals("")) {
                    displayNode = false;
                }
                if (adapter.getNodeType()==Node.DOCUMENT_TYPE_NODE) {
                    displayNode = false;
                }
                if (displayNode) {
                    count++;
                }
            }
        }
        return count;
    }//}}}
    
    public int getIndexOfChild(Object parent, Object child) {//{{{
        DefaultViewTreeNode node = (DefaultViewTreeNode) parent;
        AdapterNode adapter = node.getAdapterNode();
        
        boolean showComments = Boolean.valueOf(m_buffer.getProperty("documentview.default.show.comment.nodes", "false")).booleanValue();
        boolean showEmpty    = Boolean.valueOf(m_buffer.getProperty("documentview.default.show.empty.nodes", "false")).booleanValue();
        
        if (!showComments && adapter.getNodeType()==Node.COMMENT_NODE)
            return -1;
        if (!showEmpty && adapter.getNodeType()==Node.TEXT_NODE && adapter.getNodeValue().trim()=="")
            return -1;
        
        return node.getIndex((DefaultViewTreeNode)child);
    }//}}}
    
    public Object getRoot() {//{{{
        return m_rootTreeNode;
    }//}}}
    
    public boolean isLeaf(Object aNode) {//{{{
        // Return true for any node with no children
        return ((DefaultViewTreeNode)aNode).isLeaf();
    }//}}}
    
    public void removeTreeModelListener(TreeModelListener listener) {//{{{
        if ( listener != null ) {
            treeListenerList.removeElement( listener );
        }
    }//}}}
    
    public void valueForPathChanged(TreePath path, Object newValue) {//{{{
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
    
    // {{{ Event notification methods
    
    private void fireTreeNodesChanged(TreeModelEvent e) {//{{{
        Enumeration listeners = treeListenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TreeModelListener listener = (TreeModelListener) listeners.nextElement();
            listener.treeNodesChanged( e );
        }
    }//}}}
    
    private void fireTreeNodesInserted(TreeModelEvent e) {//{{{
        Enumeration listeners = treeListenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TreeModelListener listener = (TreeModelListener) listeners.nextElement();
            listener.treeNodesInserted( e );
        }
    }//}}}
    
    private void fireTreeNodesRemoved(TreeModelEvent e) {//{{{
        Enumeration listeners = treeListenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TreeModelListener listener = (TreeModelListener) listeners.nextElement();
            listener.treeNodesRemoved( e );
        }
    }//}}}
    
    private void fireTreeStructureChanged(TreeModelEvent e) {//{{{
        Enumeration listeners = treeListenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TreeModelListener listener = (TreeModelListener) listeners.nextElement();
            listener.treeStructureChanged( e );
        }
    }//}}}

    // }}}
    
    Component view;
    
    private DocumentBuffer m_buffer;
    private DefaultViewTreeNode m_rootTreeNode;
    private Vector treeListenerList = new Vector();
    //}}}
}
