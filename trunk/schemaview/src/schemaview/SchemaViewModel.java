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

package schemaview;

//{{{ Imports

//{{{ jsXe imports
import net.sourceforge.jsxe.dom.*;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ Swing classes
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
//}}}

//{{{ JGraph classes
import org.jgraph.graph.*;
import org.jgraph.event.*;
//}}}

//}}}

public class SchemaViewModel extends DefaultGraphModel {
    
    //{{{ Private static members
    private static final ArrayList m_rootNames = new ArrayList();
    
    static {
        m_rootNames.add("xsd:simpleType");
        m_rootNames.add("xsd:complexType");
        m_rootNames.add("xsd:element");
        m_rootNames.add("xsd:group");
        m_rootNames.add("xsd:sequence");
        m_rootNames.add("xsd:choice");
    }
    //}}}
    
    //{{{ SchemaViewModel constructor
    public SchemaViewModel(XMLDocument document) throws IOException {
        m_document = document;
        parse(m_document);
    }//}}}
    
    //{{{ Private Members
    
    //{{{ parse()
    /**
     * Parses the Schema into a model.
     */
    private void parse(XMLDocument doc) throws IOException {
        Log.log(Log.DEBUG,this,"Starting parse");
        Element element = doc.getDocumentCopy().getDocumentElement();
        roots = new ArrayList();
        String uri = element.getNamespaceURI();
        if (uri != null && uri.equals("http://www.w3.org/2001/XMLSchema")) {
            parseNode(null, doc.getAdapterNode(), 0);
        } else {
            throw new IOException("Schema namespace is not defined");
        }
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
    private void parseNode(DefaultGraphCell parent, AdapterNode node, int level) {
        Log.log(Log.DEBUG,this,"Parsing node: "+node.getNodeName());
        DefaultGraphCell newParent = parent;
        int newLevel = level;
        if (m_rootNames.contains(node.getNodeName())) {
            //add the cell
            DefaultGraphCell newCell = new DefaultGraphCell(node);
            DefaultPort      newPort = new DefaultPort(node);
            newCell.add(newPort);
            setAttributes(node.getNodeName(), newCell);
           // roots.add(newCell);
            Object[] insert = new Object[]{newCell};
            insert(insert, null, null, null, null);
            toFront(insert);
            newParent = newCell;
            newLevel = level+1;
            //add the edge with the parent
            if (parent != null) {
                DefaultEdge newEdge = new DefaultEdge();
                ConnectionSet conSet = new ConnectionSet(newEdge, parent, newCell);
                insert = new Object[]{newEdge};
                insert(insert, null, conSet, null, null);
                toBack(insert);
            }
        }
        int childCount = node.childCount();
        for (int i=0; i<childCount; i++) {
            parseNode(newParent, node.child(i), newLevel);
        }
    }
    
    //}}}
    
    
    //{{{ setAttributes()
    
    public void setAttributes(String type, DefaultGraphCell cell) {
        AttributeMap map = cell.getAttributes();
        GraphConstants.setBorderColor(map, Color.black);
        GraphConstants.setOpaque(map, false);
       // GraphConstants.setGradientColor(map, Color.green);
       // GraphConstants.setBackground(map, Color.blue);
        GraphConstants.setAutoSize(map, true);
        cell.setAttributes(map);
       // if (type.equals("xsd:simpleType") {
       //     
       // }
        
        
        
    }//}}}
    
    private XMLDocument m_document;
    
    //}}}
}
