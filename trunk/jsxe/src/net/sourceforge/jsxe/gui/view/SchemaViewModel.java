/*
SchemaViewModel.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the model class for the schema view

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

//{{{ Imports

//{{{ jsXe imports
import net.sourceforge.jsxe.dom.*;
//}}}

//{{{ Swing classes
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
//}}}

//{{{ Java base classes
import java.util.*;
//}}}

//{{{ JGraph classes
import org.jgraph.graph.*;
import org.jgraph.event.*;
//}}}

//}}}

public class SchemaViewModel /*implements GraphModel*/ {
   
    //{{{ SchemaViewModel constructor
    
    public SchemaViewModel(XMLDocument document) {
        m_document = document;
    }//}}}
    
    //{{{ GraphModel methods
    
    //{{{ acceptsSource()
    
    public boolean acceptsSource(Object edge, Object port) {
        return true;
    }//}}}
    
    //{{{ acceptsTarget()
    
    public boolean acceptsTarget(Object edge, Object port) {
        return true;
    }//}}}
    
    //{{{ addGraphModelListener()
    
    public void addGraphModelListener(GraphModelListener l) {
        if ( l != null && ! m_graphModelListenerList.contains(l) ) {
            m_graphModelListenerList.add(l);
        }
    }//}}}
    
    //{{{ addUndoableEditListener()
    
    public void addUndoableEditListener(UndoableEditListener listener) {
        if ( listener != null && ! m_undoableEditListenerList.contains(listener) ) {
            m_undoableEditListenerList.add(listener);
        }
    }//}}}
    
    //{{{ cloneCells()
    
    public Map cloneCells(Object[] cells) {
        return new HashMap();
    }//}}}
    
    //{{{ contains()
    
    public boolean contains(Object node) {
        return true;
    }//}}}
    
    //{{{ edges()
    
    public Iterator edges(Object port) {
        return null;
    }//}}}
    
    //{{{ edit()
    
    public void edit(Map attributes, ConnectionSet cs, ParentMap pm, UndoableEdit[] e) {
        
    }//}}}
    
    //{{{ getAttributes()
    
    public AttributeMap getAttributes(Object node) {
        return new AttributeMap();
    }//}}}
    
    //{{{ getChild()
    
    public Object getChild(Object parent, int index) {
        if (parent != null) {
            AdapterNode parentNode = (AdapterNode)parent;
            
            return parentNode.child(index);
        }
        return null;
    }//}}}
    
    //{{{ getChildCount()
    
    public int getChildCount(Object parent) {
        if (parent != null) {
            AdapterNode parentNode = (AdapterNode)parent;
            return parentNode.childCount();
        }
        return -1;
    }//}}}
    
    //{{{ getIndexOfChild()
    
    public int getIndexOfChild(Object parent, Object child) {
        if (parent != null && child != null) {
            AdapterNode parentNode = (AdapterNode)parent;
            AdapterNode childNode = (AdapterNode)child;
            
            return parentNode.index(childNode);
        }
        return -1;
    }//}}}
    
    //{{{ getIndexOfRoot()
    
    public int getIndexOfRoot(Object root) {
        if (root != null) {
            AdapterNode node = (AdapterNode)root;
            if (node == m_document.getAdapterNode()) {
                return 0;
            }
        }
        return -1;
    }//}}}
    
    //{{{ getParent()
    
    public Object getParent(Object child) {
        if (child != null) {
            AdapterNode node = (AdapterNode)child;
            return node.getParentNode();
        }
        return null;
    }//}}}
    
    //{{{ getRootAt()
    
    public Object getRootAt(int index) {
        if (index == 1) {
            return m_document.getAdapterNode();
        }
        return null;
    }//}}}
    
    //{{{ getRootCount()
    
    public int getRootCount() {
        return 1;
    }//}}}
    
    //{{{ getSource()
    
    public Object getSource(Object edge) {
        return edge;
    }//}}}
    
    //{{{ getTarget()
    
    public Object getTarget(Object edge) {
        return edge;
    }//}}}
    
    //{{{ insert()
    
    public void insert(Object[] roots, Map attributes, ConnectionSet cs, ParentMap pm, UndoableEdit[] e) {
        
    }//}}}
    
    //{{{ isEdge()
    
    public boolean isEdge(Object edge) {
        return true;
    }//}}}
    
    //{{{ isLeaf()
    
    public boolean isLeaf(Object node) {
        if (node != null) {
            AdapterNode leaf = (AdapterNode)node;
            return (leaf.childCount() == 0);
        }
        return true;
    }//}}}
    
    //{{{ isPort()
    
    public boolean isPort(Object port) {
        return true;
    }//}}}
    
    //{{{ remove()
    
    public void remove(Object[] roots) {
        
    }//}}}
    
    //{{{ removeGraphModelListener()
    
    public void removeGraphModelListener(GraphModelListener l) {
        if ( l != null ) {
            m_graphModelListenerList.remove( l );
        }
    }//}}}
    
    //{{{ removeUndoableEditListener()
    
    public void removeUndoableEditListener(UndoableEditListener listener) {
        if ( listener != null ) {
            m_undoableEditListenerList.remove( listener );
        }
    }//}}}
    
    //{{{ toBack()
    
    public void toBack(Object[] cells) {
        
    }//}}}
    
    //{{{ toFront()
    
    public void toFront(Object[] cells) {
        
    }//}}}
    
    //}}}
    
    //{{{ Private Members
    
    private XMLDocument m_document;
    private ArrayList m_graphModelListenerList = new ArrayList();
    private ArrayList m_undoableEditListenerList = new ArrayList();
    
    //}}}
}
