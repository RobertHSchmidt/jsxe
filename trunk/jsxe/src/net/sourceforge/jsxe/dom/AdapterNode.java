/*
AdapterNode.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the Node class that will be used by jsXe
to adapt a DOM into the model for a viewable JTree. This class also
provides persistent objects for editing a DOM.

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

package net.sourceforge.jsxe.dom;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ Java Base Classes
import java.util.ArrayList;
import java.util.ListIterator;
//}}}

//{{{ DOM classes
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//}}}

//}}}

/**
 * <p>The AdapterNode class is meant to provide extensions to the W3C Node
 * interface by wrapping around existing nodes created after parsing a document.
 * It provides some event functionality and some methods for editing nodes in
 * a DOM tree.</p>
 * 
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class AdapterNode {
    
    /**
     * <p>Creates an AdapterNode for a root document node. This is normally used
     * by an implementation of the XMLDocument interface when it is created.</p>
     * @param xmlDocument the XMLDocument object that wraps the Document object
     * @param document the document object that this AdapterNode is to
     *                 represent
     */
    AdapterNode(XMLDocument xmlDocument, Document document) {//{{{
        domNode = document;
        rootDocument = xmlDocument;
    }//}}}
    
    /**
     * <p>Creates a new AdapterNode for a node in a DOM tree. This is normally used
     * by an implementation of the XMLDocument interface. Use the
     * <code>newAdapterNode()</code> method in the XMLDocument interface to
     * create AdapterNodes.</p>
     * @param xmlDocument the XMLDocument that owns this node
     * @param parent the parent AdapterNode object for the parent DOM node
     * @param node the Node object that this AdapterNode represents. This node
     *             should be a child of the Node that is wrapped by the parent
     *             AdapterNode
     */
    AdapterNode(XMLDocument xmlDocument, AdapterNode parent, Node node) {//{{{
        domNode = node;
        setParent(parent);
        rootDocument = xmlDocument;
    }//}}}
    
    public String toString() {//{{{
        String s = new String();
        if (domNode.getNodeType() == Node.DOCUMENT_NODE)
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
    
    /**
     * <p>Returns the index of the given AdapterNode if it is a child.</p>
     * @param child the child node of this node
     * @return the index where the child is located. -1 if the AdapterNode is
     *         not a child
     */
    public int index(AdapterNode child) {//{{{
        int count = childCount();
        for (int i=0; i<count; i++) {
            AdapterNode n = this.child(i);
            if (child.equals(n)) return i;
        }
        //Returns here when child not in tree
        return -1;
    }//}}}
    
    /**
     * <p>Gets the child node at the given index.</p>
     * @param index the index of the requested node
     * @return an AdapterNode representing the node at the given index,
     *         null if the index is out of bounds
     */
    public AdapterNode child(int index) {//{{{
        /*
        Only populate the children list if asked for the
        Adapter. Once asked for however the object should
        be persistent.
        */
        AdapterNode child = null;
        if (index < domNode.getChildNodes().getLength()) {
            if (index < children.size()) {
                try {
                    child = (AdapterNode)children.get(index);
                    if (child == null) {
                        //the size was ok but no AdapterNode was at this index
                        child = rootDocument.newAdapterNode(this, domNode.getChildNodes().item(index));
                        children.set(index, child);
                    }
                } catch (IndexOutOfBoundsException ioobe) {}
            } else {
                /*
                Populate the other elements with null until we
                have the correct size.
                */
                while (children.size() < index) {
                    children.add(null);
                }
                child = rootDocument.newAdapterNode(this, domNode.getChildNodes().item(index));
                children.add(child);
            }
        }
       return child;
    }//}}}
    
    /**
     * <p>Gets the number of children that this node has.</p>
     * @return the number of children of this node
     */
    public int childCount() {//{{{
        return domNode.getChildNodes().getLength();
    }//}}}
    
    /**
     * <p>Gets the name of this node.</p>
     * @return the name of the node
     */
    public String getNodeName() {//{{{
        return domNode.getNodeName();
    }//}}}
    
    /**
     * <p>Sets the name of the node. Only element nodes are currently
     * supported.</p>
     * @param newValue the new name for this node
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified name
     *                      contains an illegal character.
     */
    public void setNodeName(String newValue) throws DOMException {//{{{
        if (domNode.getNodeType() == Node.ELEMENT_NODE) {
            //Verify that this really is a change
            if (!domNode.getNodeName().equals(newValue)) {
                //get the nodes needed
                Node parent = domNode.getParentNode();
                NodeList children = domNode.getChildNodes();
                Document document = domNode.getOwnerDocument();
                //replace the changed node
                Element newNode = document.createElementNS("", newValue);
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
                fireLocalNameChanged(this);
            }
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element can be renamed at this time.");
        }
    }//}}}
    
    /**
     * <p>Gets the current value if this node.</p>
     * @return the current value associated with this node
     */
    public String getNodeValue() {//{{{
        return domNode.getNodeValue();
    }//}}}
    
    /**
     * <p>Sets the value of the node.</p>
     * @param str the new value of the node
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised when the node is
     *                      readonly.
     * @throws DOMException DOMSTRING_SIZE_ERR: Raised when it would return
     *                      more characters than fit in a DOMString variable on
     *                      the implementation platform.
     */
    public void setNodeValue(String str) throws DOMException {//{{{
        // Make sure there is a change.
        if (str != null && !str.equals(domNode.getNodeValue())) {
            domNode.setNodeValue(str);
            fireNodeValueChanged(this);
        }
    }//}}}
    
    /**
     * <p>Gets the type of the node specified in the W3C Node interface.</p>
     * @return the node type
     */
    public short getNodeType() {//{{{
        return domNode.getNodeType();
    }//}}}
    
    /**
     * <p>Gets the parent AdapterNode object.</p>
     * @return the AdapterNode that is the parent of this node
     */
    public AdapterNode getParentNode() {//{{{
        return parentNode;
    }//}}}
    
    /**
     * <p>Gets the attributes associated with this node.</p>
     * @return a map of the attributes associated with this node.
     *         <code>null</code> if this is not an element node
     */
    public NamedNodeMap getAttributes() {//{{{
        return domNode.getAttributes();
    }//}}}
    
    /**
     * <p>Adds a new child to this node given the node name, value, and type.</p>
     * @param name the name of the new child node
     * @param value the value of the new child node
     * @param type the type of the new child node as specified by the W3C Node
     *             interface
     * @return the new child that was created
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified name
     *                      or value contains an illegal character.
     *                      NOT_SUPPORTED_ERR: Raised if the node type is not
     *                      supported.
     */
    public AdapterNode addAdapterNode(String name, String value, short type) throws DOMException {//{{{
        
        Node newNode = null;
        Document document = domNode.getOwnerDocument();
        
        //Only handle text and element nodes right now.
        switch(type) {
            case Node.ELEMENT_NODE:
                newNode = document.createElementNS("", name);
                break;
            case Node.TEXT_NODE:
                newNode = document.createTextNode(value);
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
        
        AdapterNode newAdapterNode = rootDocument.newAdapterNode(this, newNode);
        //add to this AdapterNode and the DOM.
        domNode.appendChild(newNode);
        children.add(newAdapterNode);
        
        fireNodeAdded(this, newAdapterNode);
        
        return newAdapterNode;
    }//}}}
    
    /**
     * <p>Adds an already existing AdapterNode to this node as a child.</p>
     * @param node the node to be added.
     * @return a reference to the node that was added.
     */
    public AdapterNode addAdapterNode(AdapterNode node) {//{{{
        //add to this AdapterNode and the DOM.
        if (node != null) {
            domNode.appendChild(node.getNode());
            children.add(node);
            node.setParent(this);
            
            fireNodeAdded(this, node);
        }
        
        return node;
    }//}}}
    
   // public void remove() throws DOMException {//{{{
   //     Node parent = domNode.getParentNode();
   //     parent.removeChild(domNode);
   //     parentNode.remove(this);
   //     fireNodeRemoved(this, this);
   // }//}}}
    
    /**
     * <p>Removes a child from this node.</p>
     * @param child the child node to remove from this node
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly.
     * @throws DOMException NOT_FOUND_ERR: Raised if oldChild is not
     *                      a child of this node.
     */
    public void remove(AdapterNode child) throws DOMException {//{{{
        if (child != null) {
            domNode.removeChild(child.getNode());
            children.remove(child);
            fireNodeRemoved(this, child);
        }
    }//}}}
    
    /**
     * <p>Sets an attribute of this node. If the specified attribute does not
     * exist it is created.</p>
     * @param name the name of the attribute
     * @param value the new value of the attribute
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified
     *                      qualified name contains an illegal character, per
     *                      the XML 1.0 specification
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly
     */
    public void setAttribute(String name, String value) throws DOMException {//{{{
        if (domNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)domNode;
            element.setAttribute(name,value);
            fireAttributeChanged(this, name);
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
        }
    }//}}}
    
    /**
     * <p>Gets the value of an attribute associated with this node.</p>
     * @param name the name of the attribute
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     */
    public String getAttribute(String name) throws DOMException {//{{{
        if (domNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)domNode;
            return element.getAttribute(name);
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
        }
    }//}}}
    
    /**
     * <p>Removes an attribute at the given index.</p>
     * <p><b>Note:</b> Attributes are sorted alphabetically.</p>
     * @param index the index of the node to remove
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly
     */
    public void removeAttributeAt(int index) throws DOMException {//{{{
        if (domNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)domNode;
            NamedNodeMap attrs = element.getAttributes();
            Node attr = attrs.item(index);
            element.removeAttribute(attr.getNodeName());
            fireAttributeChanged(this, attr.getNodeName());
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
        }
    }//}}}
    
    /**
     * <p>Removes an attribute by name.</p>
     * @param attr the name of the attribute to remove
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly
     */
    public void removeAttribute(String attr) throws DOMException {//{{{
        if (domNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)domNode;
            element.removeAttribute(attr);
            fireAttributeChanged(this, attr);
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
        }
    }//}}}
    
    /**
     * <p>Gets the value of an attribute at the given index</p>
     * <p><b>Note:</b> Attributes are sorted alphabetically.</p>
     * @param index the index of the attribute to get
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     */
    public String getAttributeAt(int index) throws DOMException {//{{{
        if (domNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)domNode;
            NamedNodeMap attrs = element.getAttributes();
            Node attr = attrs.item(index);
            return attr.getNodeName();
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
        }
    }//}}}
    
    /**
     * <p>Compares this AdapterNode with another.</p>
     * @param node the AdapterNode to compare to
     * @return true if the underlying Node object for the two AdapterNodes is
     *         is the same.
     */
    public boolean equals(AdapterNode node) {//{{{
        return node.getNode() == domNode;
    }//}}}
    
    /**
     * <p>Adds an AdapterNodeListener to be notified when this node changes</p>
     * @param listener the listener to add
     */
    public void addAdapterNodeListener(AdapterNodeListener listener) {//{{{
        listeners.add(listener);
    }//}}}
    
    /**
     * <p>Removes a listener from this node if it exists</p>
     * @param listener the listener to remove
     */
    public void removeAdapterNodeListener(AdapterNodeListener listener) {//{{{
        listeners.remove(listeners.indexOf(listener));
    }//}}}
    
    //{{{ Protected members
    
    /**
     * <p>Gets the underlying Node object that this AdapterNode wraps.</p>
     * @return the underlying Node object for this AdapterNode object
     */
    protected Node getNode() {//{{{
        return domNode;
    }//}}}
    
    /**
     * <p>Sets the parent node of this AdapterNode.</p>
     * @param parent the new parent for this AdapterNode
     */
    protected void setParent(AdapterNode parent) {//{{{
        parentNode = parent;
    }//}}}
    
    //}}}
    
    //{{{ Private members
    
    private void fireNodeAdded(AdapterNode source, AdapterNode child) {//{{{
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.nodeAdded(source, child);
        }
    }//}}}
    
    private void fireNodeRemoved(AdapterNode source, AdapterNode child) {//{{{
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.nodeRemoved(source, child);
        }
    }//}}}
    
    private void fireLocalNameChanged(AdapterNode source) {//{{{
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.localNameChanged(source);
        }
    }//}}}
    
    private void fireNamespaceChanged(AdapterNode source) {//{{{
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.namespaceChanged(source);
        }
    }//}}}
    
    private void fireNodeValueChanged(AdapterNode source) {//{{{
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.nodeValueChanged(source);
        }
    }//}}}
    
    private void fireAttributeChanged(AdapterNode source, String attr) {//{{{
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.attributeChanged(source, attr);
        }
    }//}}}
    
    private AdapterNode parentNode;
    private XMLDocument rootDocument;
    private ArrayList children = new ArrayList();
    
    private Node domNode;
    private ArrayList listeners = new ArrayList();
    //}}}
}
