/*
DefaultViewTreeNode.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the node class used by the DefaultViewTree

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
import net.sourceforge.jsxe.dom.AdapterNode;
import net.sourceforge.jsxe.dom.XMLDocument;
//}}}

//{{{ Swing classes
import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
//}}}

//{{{ Java classes
import java.util.ArrayList;
import java.util.Enumeration;
//}}}

//{{{ DOM classes
import org.w3c.dom.Node;
//}}}

//}}}

public class DefaultViewTreeNode implements MutableTreeNode {
    
    //{{{ DefaultViewTreeNode constructor
    
    /**
     * Creates a new DefaultViewTreeNode wrapping the AdapterNode
     * given
     * @param parent the parent node of this node
     * @param node the AdapterNode that this DefaultViewTreeNode wraps
     */
    public DefaultViewTreeNode(DefaultViewTreeNode parent, AdapterNode node) {
        m_node = node;
        m_parent = parent;
    }//}}}
    
    //{{{ DefaultViewTreeNode constructor
    
    /**
     * Creates a new root DefaultViewTreeNode wrapping the root
     * of the document given
     * @param  the AdapterNode that this DefaultViewTreeNode wraps
     */
    public DefaultViewTreeNode(XMLDocument document) {
        m_node = document.getAdapterNode();
        m_parent = null;
    }//}}}
    
    //{{{ TreeNode methods
    
    //{{{ children()
    
    public Enumeration children() {
        return new Enumeration() {//{{{
            
            public boolean hasMoreElements() {
                return (m_index < m_node.childCount());
            }
            
            public Object nextElement() {
                return m_node.child(m_index++);
            }
            
            private int m_index = 0;
            
        };//}}}
    }//}}}
    
    //{{{ getAdapterNode()
    
    /**
     * Gets the AdapterNode that this DefaultViewTreeNode wraps.
     * @return the AdapterNode that this node wraps.
     */
    public AdapterNode getAdapterNode() {
        return m_node;
    }//}}}
    
    //{{{ getAllowsChildren()
    
    public boolean getAllowsChildren() {
        return true; //ok for the most part
    }//}}}
    
    //{{{ getChildAt()
    
    public TreeNode getChildAt(int childIndex) {
        
        DefaultViewTreeNode child = null;
        if (childIndex < getChildCount()) {
            AdapterNode childAt = m_node.child(childIndex);
            
            if (childIndex < m_children.size()) {
                try {
                    child = (DefaultViewTreeNode)m_children.get(childIndex);
                    if (child == null) {
                        //the size was ok but no AdapterNode was at this index
                        child = new DefaultViewTreeNode(this, childAt);
                        m_children.set(childIndex, child);
                    }
                } catch (IndexOutOfBoundsException ioobe) {}
            } else {
                /*
                Populate the other elements with null until we
                have the correct size.
                */
                while (m_children.size() < childIndex + 1) {
                    m_children.add(null);
                }
                child = new DefaultViewTreeNode(this, childAt);
                m_children.set(childIndex, child);
            }
            
        }
        
        return child;
    }//}}}
    
    //{{{ getChildCount()
    
    public int getChildCount() {
        return m_node.childCount();
    }//}}}
    
    //{{{ getIndex()
    
    public int getIndex(TreeNode node) {
        try {
            DefaultViewTreeNode dNode = (DefaultViewTreeNode)node;
            AdapterNode aNode = dNode.getAdapterNode();
            return m_node.index(aNode);
        } catch (ClassCastException e) {
            return -1;
        }
    }//}}}
    
    //{{{ getParent()
    
    public TreeNode getParent() {
        return m_parent;
    }//}}}
    
    //{{{ isLeaf()
    
    public boolean isLeaf() {
        return m_node.childCount() <= 0;
    }//}}}
    
    //}}}
    
    //{{{ MutableTreeNode methods
    
    //{{{ insert()
    
    public void insert(MutableTreeNode child, int index) {
        DefaultViewTreeNode childNode = (DefaultViewTreeNode)child;
        AdapterNode adapter = childNode.getAdapterNode();
        child.setParent(this);
        m_node.addAdapterNodeAt(childNode.getAdapterNode(), index);
        m_children.add(index, child);
    }//}}}
    
    //{{{ insert()
    
    public void insert(String name, String value, short type, int index) {
        m_node.addAdapterNode(name, value, type, index);
        m_children.add(new DefaultViewTreeNode(this, m_node.child(index)));
    }//}}}
    
    //{{{ remove()
    
    public void remove(int index) {
        remove((DefaultViewTreeNode)m_children.get(index));
    }//}}}
    
    //{{{ remove()
    
    public void remove(MutableTreeNode node) {
        DefaultViewTreeNode treeNode = (DefaultViewTreeNode)node;
        m_node.remove(treeNode.getAdapterNode());
        m_children.remove(treeNode);
    }//}}}
    
    //{{{ removeFromParent()
    
    public void removeFromParent() {
        DefaultViewTreeNode parent = (DefaultViewTreeNode)getParent();
        parent.remove(this);
        m_parent=null;
    }//}}}
    
    //{{{ setParent()
    
    public void setParent(MutableTreeNode newParent) {
        m_node.setParent(m_node);
        removeFromParent();
        m_parent = (DefaultViewTreeNode)newParent;
    }//}}}
    
    //{{{ setUserObject
    
    public void setUserObject(Object object) {
        AdapterNode node = (AdapterNode)object;
        m_node = node;
    }//}}}
    
    //}}}
    
    //{{{ toString()
    
    public String toString() {
        String s = new String();
        if (m_node.getNodeType() == Node.DOCUMENT_NODE)
            return "Document Root";
        String nodeName = m_node.getNodeName();
        if (! nodeName.startsWith("#")) {   
            s += nodeName;
        }
        if (m_node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
            s += " ";
        }
        if (m_node.getNodeValue() != null) {
            String t = m_node.getNodeValue().trim();
            int x = t.indexOf("\n");
            if (x >= 0) {
                t = t.substring(0, x);
            }
            if (t.length() > 20) {
                t = t.substring(0, 20) + "...";
            }
            s += t;
        }
        return s;
    }//}}}
    
    //{{{ isExpanded()
    
    /**
     * Gets whether this tree node should be expanded or not
     * @return true if this node should be expanded in the JTree
     */
    public boolean isExpanded() {
        return m_expanded;
    }//}}}
    
    //{{{ setExpanded()
    
    /**
     * Sets whether the node should be expanded in the JTree
     * or not.
     * @param expanded true if this node should be expanded
     */
    public void setExpanded(boolean expanded) {
        m_expanded = expanded;
    }//}}}
    
    //{{{ Instance variables
    private AdapterNode m_node;
    private DefaultViewTreeNode m_parent = null;
    private ArrayList m_children = new ArrayList();
    private boolean m_expanded = false;
    //}}}
}
