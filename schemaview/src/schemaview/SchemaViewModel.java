/*
SchemaViewModel.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

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

package schemaview;

//{{{ Imports

//{{{ jsXe imports
import net.sourceforge.jsxe.dom.*;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ Swing classes
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
//}}}

//{{{ AWT classes
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
//}}}

//{{{ Java base classes
import java.util.*;
import java.io.IOException;
//}}}

//{{{ DOM Classes
import org.w3c.dom.Element;
import org.w3c.dom.Node;
//}}}

//{{{ JGraph classes
import org.jgraph.graph.*;
import org.jgraph.event.*;
//}}}

//}}}

public class SchemaViewModel extends DefaultGraphModel {
    
    //{{{ SchemaViewModel constructor
    public SchemaViewModel(XMLDocument document) throws IOException {
        m_document = document;
        
        Element element = m_document.getDocumentCopy().getDocumentElement();
        
        String uri = element.getNamespaceURI();
        if (uri != null && uri.equals("http://www.w3.org/2001/XMLSchema")) {
            m_schemaPrefix = element.getPrefix();
           // m_rootNames.add(schemaPrefix+":"+"simpleType");
            m_rootNames.add(m_schemaPrefix+":"+"complexType");
            m_rootNames.add(m_schemaPrefix+":"+"element");
            m_rootNames.add(m_schemaPrefix+":"+"group");
            m_rootNames.add(m_schemaPrefix+":"+"sequence");
            m_rootNames.add(m_schemaPrefix+":"+"choice");
            
            parse();
            layout();
        } else {
            throw new IOException("Schema namespace is not defined");
        }
    }//}}}
    
    //{{{ getSchemaRootCount()
    
    public int getSchemaRootCount() {
        return m_schemaRoots.size();
    }//}}}
    
    //{{{ getSchemaRootAt()
    
    public SchemaGraphCell getSchemaRootAt(int index) {
        return (SchemaGraphCell)m_schemaRoots.get(index);
    }//}}}
    
    //{{{ SchemaGraphCell class
    
    public static class SchemaGraphCell extends DefaultGraphCell {
        
        //{{{ SchemaGraphCell constructor
        
        public SchemaGraphCell(Object userObj) {
            super(userObj);
        }//}}}
        
        //{{{ addSchemaCell()
        /**
         * Adds a node to the schema tree. This will probably throw exceptions in the future.
         */
        public void addSchemaCell(SchemaGraphCell child) {
            m_m_children.add(child);
            child.m_m_parent = this;
        }//}}}
        
        //{{{ getSchemaChildCount()
        
        public int getSchemaChildCount() {
            return m_m_children.size();
        }//}}}
        
        //{{{ getSchemaChildAt()
        
        public SchemaGraphCell getSchemaChildAt(int index) {
            return (SchemaGraphCell)m_m_children.get(index);
        }//}}}
        
        private ArrayList m_m_children = new ArrayList();
        protected SchemaGraphCell m_m_parent;
        
    }//}}}
    
    //{{{ Private Members
    
    //{{{ parse()
    
    public void parse() {
        m_cells = new ArrayList();
        m_cs = new ConnectionSet();
        m_attrib = new HashMap();
        m_schemaRoots = new ArrayList();
        parseNode(null, m_document.getAdapterNode(), 0);
        insert(m_cells.toArray(), m_attrib, m_cs, null, null);
    }//}}}
    
    //{{{ parseNode()
    /**
     * Creates the graph based on nodes below this node.
     * This is called whenever the schema changes to
     * reparse the changed node and nodes below the changed
     * node.
     * The roots below the changed node should be removed
     * from the model before calling this method.
     */
    private void parseNode(SchemaGraphCell parent, AdapterNode node, int level) {
        
        if (node.getNodeType() == Node.ELEMENT_NODE || node.getNodeType() == Node.DOCUMENT_NODE) {
        
            SchemaGraphCell newParent = parent;
            int newLevel = level;
            
            //hack: don't include local complexType nodes
            if (m_rootNames.contains(node.getNodeName()) && !isLocalComplexType(node)) {
                //add the cell
                SchemaGraphCell newCell = new SchemaGraphCell(node);
                DefaultPort      newPort = new DefaultPort();
                newCell.add(newPort);
                newPort.setParent(newCell);
                m_cells.add(newCell);
                m_cells.add(newPort);
                
                //Set the attributes
                m_attrib.put(newCell, getAttributes(node));
                
                newParent = newCell;
                
                //add it to the schema roots if it's a root
                if (level == 0) {
                    m_schemaRoots.add(newCell);
                }
                
                newLevel = level+1;
                
                //add the edge with the parent
                if (parent != null) {
                    //connect it to it's schema parent
                    parent.addSchemaCell(newCell);
                    
                    //Now add the edge
                    DefaultEdge newEdge = new DefaultEdge();
                    Map edgeAttrib = new Hashtable();
                    GraphConstants.setLineEnd(edgeAttrib, GraphConstants.ARROW_CLASSIC);
                    GraphConstants.setEndFill(edgeAttrib, true);
                    m_attrib.put(newEdge, edgeAttrib);
                    
                    //get the port for the parent
                    DefaultPort parentPort = (DefaultPort)parent.getChildAt(0);
                    m_cs.connect(newEdge, parentPort, newPort);
                    m_cells.add(newEdge);
                    
                }
            }
            int childCount = node.childCount();
            for (int i=0; i<childCount; i++) {
                parseNode(newParent, node.child(i), newLevel);
            }
        }
        
    }
    
    //}}}
    
    //{{{ layout()
    /**
     * Lays out the cells on the graph component by setting
     * their locations
     */
    protected void layout() {
        
        int startY = 0;
        int childCount = getSchemaRootCount();
        for (int i=0; i<childCount; i++) {
            startY = layoutNode(0,startY, (SchemaGraphCell)getSchemaRootAt(i));
        }
        
    }//}}}
    
    //{{{ layoutNode()
    
    private int layoutNode(int startX, int startY, SchemaGraphCell cell) {
        
        AttributeMap map = cell.getAttributes();
        
       // Rectangle2D size = GraphConstants.getBounds(map);
       // int width  = (int)size.getWidth();
       // int height = (int)size.getHeight();
       // int x      = (int)size.getX();
       // int y      = (int)size.getY();
        int width = 150;
        int height = 20;
        int padding = 20;
        
        int newYOffset = startY;
        int childCount = cell.getSchemaChildCount();
        for (int i=0; i<childCount; i++) {
            newYOffset = layoutNode(startX + width + padding, newYOffset,cell.getSchemaChildAt(i));
        }
        
        Rectangle bounds = new Rectangle(width, height);
        bounds.x = startX+padding/2;
        bounds.y =  (Math.max(newYOffset-startY, height+padding)/2) + startY - (height/2);
        //sets the bounds for the cell
        GraphConstants.setBounds(map, bounds);
        
        //just for good measure
        cell.setAttributes(map);
        
        return Math.max(newYOffset, startY+height+padding);
        
    }//}}}
    
    //{{{ setAttributes()
    /**
     * gets attributes that have to with the appearance of the type of
     * cell.
     */
    private HashMap getAttributes(AdapterNode node) {
        HashMap map = new HashMap();
        GraphConstants.setBorderColor(map, Color.black);
        GraphConstants.setOpaque(map, false);
       // GraphConstants.setGradientColor(map, Color.green);
       // GraphConstants.setBackground(map, Color.blue);
       // GraphConstants.setAutoSize(map, true);
        
        String nodeName = node.getLocalName();
        
        if (nodeName.equals("element")) {
            String name = node.getAttribute("name");
            if (name.equals("")) {
                GraphConstants.setValue(map, node.getAttribute("ref"));
            } else {
                GraphConstants.setValue(map, node.getAttribute("name"));
            }
        }
        if (nodeName.equals("complexType")) {
            GraphConstants.setValue(map, node.getAttribute("name"));
        }
        if (nodeName.equals("group")) {
            String name = node.getAttribute("name");
            if (name.equals("")) {
                GraphConstants.setValue(map, node.getAttribute("ref"));
            } else {
                GraphConstants.setValue(map, node.getAttribute("name"));
            }
        }
        if (nodeName.equals("sequence")) {
            GraphConstants.setValue(map, "sequence");
        }
        if (nodeName.equals("choice")) {
            GraphConstants.setValue(map, "choice");
        }
        
        return map;
    }//}}}
    
    //{{{ isLocalComplexType()
    
    private boolean isLocalComplexType(AdapterNode node) {
        /* For local complexTypes the name attribute is forbidden but
           for non-local complexTypes it's required */
        return node.getNodeName().equals(m_schemaPrefix+":"+"complexType") && node.getAttribute("name").equals("");
    }//}}}
    
    private XMLDocument m_document;
    
    private ArrayList m_schemaRoots = new ArrayList();
    
    /**
     * The root cells populated during parse
     */
    private ArrayList m_cells;
    /**
     * The connectionset populated during parse
     */
    private ConnectionSet m_cs;
    /**
     * The atribute set populated during parse
     */
    private HashMap m_attrib;
    
    //node names to check for.
    private final ArrayList m_rootNames = new ArrayList();
    private String m_schemaPrefix;
    //}}}
}
