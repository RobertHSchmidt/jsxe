/*
AdapterNode.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the Node class that will be used by the DomTreeAdapter
class to adapt a DOM into the model for a viewabel JTree.

This file written by ian Lewis (iml001@bridgewater.edu)
Copyright (C) 2002 ian Lewis

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

package net.sourceforge.jsxe.dom;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ DOM classes
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//}}}

//}}}

public class AdapterNode {
    
    public AdapterNode(Node node) {//{{{
        domNode = node;
    }//}}}
    
    public String toString() {//{{{
        String s = new String();
        if (typeName[domNode.getNodeType()].equals("Document"))
            return "Document Root";
        String nodeName = domNode.getNodeName();
        if (! nodeName.startsWith("#")) {	
            s += nodeName;
        }
        if (domNode.getNodeValue() != null) {
            String t = domNode.getNodeValue().trim();
            int x = t.indexOf("\n");
            if (x >= 0)
                t = t.substring(0, x);
            s += t;
        }
        return s;
    }//}}}
    
    public int index(AdapterNode child) {//{{{
        int count = childCount();
        for (int i=0; i<count; i++) {
            AdapterNode n = this.child(i);
            if (child.equals(n)) return i;
        }
        //Returns here when child not in tree
        return -1;
    }//}}}
    
    public AdapterNode child(int searchIndex) {//{{{
        Node node = domNode.getChildNodes().item(searchIndex);
        if (node == null)
            return null;
        else
            return new AdapterNode(node);
    }//}}}
    
    public int childCount() {//{{{
        return domNode.getChildNodes().getLength();
    }//}}}
    
    public String getNodeName() {//{{{
        return domNode.getNodeName();
    }//}}}
    
    public void setNodeName(String newValue) throws DOMException {//{{{
        //Verify that this really is a change
        if (typeName[domNode.getNodeType()].equals("Element") && !domNode.getNodeName().equals(newValue)) {
            //get the nodes needed
            Node parent = domNode.getParentNode();
            NodeList children = domNode.getChildNodes();
            Document document = domNode.getOwnerDocument();
            //replace the changed node
            Element newNode = document.createElement(newValue);
            NamedNodeMap attrs = domNode.getAttributes();
            int attrlength = attrs.getLength();
            
            for(int i = 0; i < attrlength; i++) {
                Node attr = attrs.item(i);
                newNode.setAttribute(attr.getNodeName(), attr.getNodeValue());
            }
            
            int length = children.getLength();
            for (int i = 0; i < length; i++ ) {
                Node child = children.item(0);
                domNode.removeChild(child);
                newNode.appendChild(child);
            }
            parent.replaceChild(newNode, domNode);
            domNode = newNode;
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element can be renamed at this time.");
        }
    }//}}}
    
    public String getNodeValue() {//{{{
        return domNode.getNodeValue();
    }//}}}
    
    public void setNodeValue(String str) {//{{{
        domNode.setNodeValue(str);
    }//}}}
    
    public short getNodeType() {//{{{
        return domNode.getNodeType();
    }//}}}
    
    public AdapterNode getParentNode() {//{{{
        return new AdapterNode(domNode.getParentNode());
    }//}}}
    
    public NamedNodeMap getAttributes() {//{{{
        return domNode.getAttributes();
    }//}}}
    
    public AdapterNode addNode(String name, String value, short type) throws DOMException {//{{{
        
        Node newNode = null;
        Document document = domNode.getOwnerDocument();
        
        switch(type) {
            case Node.ELEMENT_NODE:
                newNode = document.createElement(name);
                domNode.appendChild(newNode);
                break;
            case Node.TEXT_NODE:
                newNode = document.createTextNode(value);
                domNode.appendChild(newNode);
                break;
           // case Node.CDATA_SECTION_NODE:
           //     
           //     break;
           // case Node.COMMENT_NODE:
           //     
           //     break;
           // case Node.PROCESSING_INSTRUCTION_NODE:
           //     
           //     break;
           // case Node.ENTITY_REFERENCE_NODE:
           //     
           //     break;
           // case Node.DOCUMENT_TYPE_NODE:
           //     
           //     break;
            default:
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element and Text Nodes can be added at this time.");
        }
        
        return new AdapterNode(newNode);
    }//}}}
    
    public void remove() throws DOMException {//{{{
        Node parent = domNode.getParentNode();
        parent.removeChild(domNode);
    }//}}}
    
    public void remove(AdapterNode child) throws DOMException {//{{{
        domNode.removeChild(child.getNode());
    }//}}}
    
    public void addAttribute(String name, String value) throws DOMException {//{{{
        if (typeName[domNode.getNodeType()].equals("Element")) {
            Element element = (Element)domNode;
            element.setAttribute(name,value);
        }
    }//}}}
    
    public void removeAttributeAt(int index) throws DOMException {//{{{
        if (typeName[domNode.getNodeType()].equals("Element")) {
            Element element = (Element)domNode;
            NamedNodeMap attrs = element.getAttributes();
            Node attr = attrs.item(index);
            element.removeAttribute(attr.getNodeName());
        }
    }//}}}
    
    public boolean equals(AdapterNode node) {//{{{
        return node.getNode() == domNode;
    }//}}}
    
    //{{{ Protected members
    
    protected Node getNode() {//{{{
        return domNode;
    }//}}}
    
    //}}}
    
    //{{{ Private members
    
    private final String[] typeName = {
        "none",
        "Element",
        "Attr",
        "Text",
        "CDATA",
        "EntityRef",
        "Entity",
        "ProcInstr",
        "Comment",
        "Document",
        "DocType",
        "DocFragment",
        "Notation",
    };
    private Node domNode;
    //}}}
}
