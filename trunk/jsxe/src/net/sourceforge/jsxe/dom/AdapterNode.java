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
import java.util.Properties;
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
 * interface by wrapping around existing nodes created after a document is
 * parsed. It provides some extra event functionality and some methods for
 * editing nodes in a DOM tree.</p>
 * <p>Because AdapterNodes are part of an XMLDocument they must be created
 * by their owning XMLDocument object via the newAdapterNode methods</p>
 * 
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @author Bilel Remmache (<a href="mailto:rbilel@users.sourceforge.net">rbilel@users.sourceforge.net</a>)
 * @version $Id$
 * @see XMLDocument
 */
public class AdapterNode {
    
    //{{{ AdapterNode constructor
    /**
     * <p>Creates an AdapterNode for a root document node. This is normally used
     * by an implementation of the XMLDocument interface when it is created.</p>
     * @param xmlDocument the XMLDocument object that wraps the Document object
     * @param document the document object that this AdapterNode is to
     *                 represent
     */
    AdapterNode(XMLDocument xmlDocument, Document document) {
        m_domNode = document;
        m_rootDocument = xmlDocument;
    }//}}}
    
    //{{{ AdapterNode constructor
    /**
     * Creates a new AdapterNode for a node in a DOM tree. This is normally used
     * by an implementation of the XMLDocument interface. Use the
     * <code>newAdapterNode()</code> method in the XMLDocument interface to
     * create AdapterNodes.
     * @param xmlDocument the XMLDocument that owns this node
     * @param parent the parent AdapterNode object for the parent DOM node
     * @param node the Node object that this AdapterNode represents. This node
     *             should be a child of the Node that is wrapped by the parent
     *             AdapterNode
     */
    AdapterNode(XMLDocument xmlDocument, AdapterNode parent, Node node) {
        m_domNode = node;
        setParent(parent);
        m_rootDocument = xmlDocument;
    }//}}}
    
    //{{{ getProperty()
    /**
     * Gets a property for the key given.
     * @param key the key to the properties list
     * @return the value of the property for the given key.
     */
    public String getProperty(String key) {
        return m_props.getProperty(key);
    }//}}}
    
    //{{{ getProperty()
    /**
     * Gets a property for the key given or returns the default value
     * if there is no property for the given key.
     * @param key the key to the properties list
     * @param defaultValue the default value for the property requested
     * @return the value of the property for the given key.
     */
    public String getProperty(String key, String defaultValue) {
        return m_props.getProperty(key, defaultValue);
    }//}}}
    
    //{{{ setProperty()
    /**
     * Sets a property of the AdapterNode
     * @param key the key to the property
     * @param value the value of the property
     * @return the old value of the property
     */
    public String setProperty(String key, String value) {
        Object oldValue = m_props.setProperty(key, value);
        if (oldValue != null) {
            return oldValue.toString();
        } else {
            return null;
        }
    }//}}}
    
    //{{{ index()
    /**
     * <p>Returns the index of the given AdapterNode if it is a child.</p>
     * @param child the child node of this node
     * @return the index where the child is located. -1 if the AdapterNode is
     *         not a child
     */
    public int index(AdapterNode child) {
        int count = childCount();
        for (int i=0; i<count; i++) {
            AdapterNode n = this.child(i);
            if (child.equals(n)) return i;
        }
        //Returns here when child not in tree
        return -1;
    }//}}}
    
    //{{{ child()
    /**
     * <p>Gets the child node at the given index.</p>
     * @param index the index of the requested node
     * @return an AdapterNode representing the node at the given index,
     *         null if the index is out of bounds
     */
    public AdapterNode child(int index) {
        /*
        Only populate the children list if asked for the
        Adapter. Once asked for however the object should
        be persistent.
        */
        AdapterNode child = null;
        if (index < m_domNode.getChildNodes().getLength()) {
            if (index < m_children.size()) {
                try {
                    child = (AdapterNode)m_children.get(index);
                    if (child == null) {
                        //the size was ok but no AdapterNode was at this index
                        child = m_rootDocument.newAdapterNode(this, m_domNode.getChildNodes().item(index));
                        m_children.set(index, child);
                    }
                } catch (IndexOutOfBoundsException ioobe) {}
            } else {
                /*
                Populate the other elements with null until we
                have the correct size.
                */
                ensureChildrenSize(index+1);
                child = m_rootDocument.newAdapterNode(this, m_domNode.getChildNodes().item(index));
                m_children.set(index, child);
            }
        }
       return child;
    }//}}}
    
    //{{{ childCount()
    /**
     * <p>Gets the number of children that this node has.</p>
     * @return the number of children of this node
     */
    public int childCount() {
        return m_domNode.getChildNodes().getLength();
    }//}}}
    
    //{{{ getNSPrefix()
    /**
     * Gets the namespace prefix for this node.
     * @return the namespace prefix for this node.
     */
    public String getNSPrefix() {
        return m_domNode.getPrefix();
    }//}}}
    
    //{{{ setNSPrefix()
    /**
     * Sets the namespace prefix for this node.
     * @param prefix The new prefix for this node
     * @throws DOMException if this namespace prefix is not valid.
     * INVALID_CHARACTER_ERR: Raised if the specified prefix contains an illegal character, per the XML 1.0 specification .
     * NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     * NAMESPACE_ERR: Raised if the specified prefix is malformed per the Namespaces in XML specification, if the namespaceURI of this node is null, if the specified prefix is "xml" and the namespaceURI of this node is different from "http://www.w3.org/XML/1998/namespace", if this node is an attribute and the specified prefix is "xmlns" and the namespaceURI of this node is different from " http://www.w3.org/2000/xmlns/", or if this node is an attribute and the qualifiedName of this node is "xmlns" .
     */
    public void setNSPrefix(String prefix) throws DOMException {
        m_domNode.setPrefix(prefix);
    }//}}}
    
    //{{{ getNodeName()
    /**
     * Gets the full name for this node including the local name and namespace
     * prefix.
     * @return the full qualified name of this node
     */
    public String getNodeName() {
        return m_domNode.getNodeName();
    }//}}}
    
    //{{{ getLocalName()
    /**
     * <p>Gets the local name of this node.</p>
     * @return the local name of the node
     */
    public String getLocalName() {
        return m_domNode.getLocalName();
    }//}}}
    
    //{{{ setLocalName()
    /**
     * <p>Sets the local name of the node. Only element nodes are currently
     * supported.</p>
     * @param newValue the new local name for this node
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified name
     *                      contains an illegal character.
     */
    public void setLocalName(String newValue) throws DOMException {
        if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
            //Verify that this really is a change
            if (!m_domNode.getLocalName().equals(newValue)) {
                
                //get the nodes needed
                Node parent = m_domNode.getParentNode();
                NodeList children = m_domNode.getChildNodes();
                Document document = m_domNode.getOwnerDocument();
                
                //replace the changed node; maintain the namespace URI;
                //newValue is a qualified name for now
                Element newNode = document.createElementNS(m_domNode.getNamespaceURI(), newValue);
                NamedNodeMap attrs = m_domNode.getAttributes();
                int attrlength = attrs.getLength();
                
                for(int i = 0; i < attrlength; i++) {
                    Node attr = attrs.item(i);
                    newNode.setAttributeNS(attr.getNamespaceURI(), attr.getNodeName(), attr.getNodeValue());
                }
                
                int length = children.getLength();
                for (int i = 0; i < length; i++ ) {
                    Node child = children.item(0);
                    m_domNode.removeChild(child);
                    newNode.appendChild(child);
                }
                parent.replaceChild(newNode, m_domNode);
                m_domNode = newNode;
                fireLocalNameChanged(this);
            }
        } else {
            if (m_domNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                if (m_domNode.getNodeName() != newValue) {
                    Node newNode = m_domNode.getOwnerDocument().createProcessingInstruction(newValue, m_domNode.getNodeValue());
                    m_domNode.getParentNode().replaceChild(newNode, m_domNode);
                    m_domNode = newNode;
                }
            } else {
                if (m_domNode.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
                    if (m_domNode.getNodeName() != newValue) {
                        if (entityDeclared(newValue)) {
                            Node newNode = m_domNode.getOwnerDocument().createEntityReference(newValue);
                            m_domNode.getParentNode().replaceChild(newNode, m_domNode);
                            m_domNode = newNode;
                        } else {
                            throw new DOMException(DOMException.SYNTAX_ERR, "Entity "+"\""+newValue+"\""+" is not declared in the DTD");
                        }
                    }
                } else {
                    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Renaming this node type is not supported.");
                }
            }
        }
    }//}}}
    
    //{{{ getNodeValue()
    /**
     * <p>Gets the current value if this node.</p>
     * @return the current value associated with this node
     */
    public String getNodeValue() {
        return m_domNode.getNodeValue();
    }//}}}
    
    //{{{ setNodeValue()
    /**
     * <p>Sets the value of the node.</p>
     * @param str the new value of the node
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised when the node is
     *                      readonly.
     * @throws DOMException DOMSTRING_SIZE_ERR: Raised when it would return
     *                      more characters than fit in a DOMString variable on
     *                      the implementation platform.
     */
    public void setNodeValue(String str) throws DOMException {
        // Make sure there is a change.
        if (str != null && !str.equals(m_domNode.getNodeValue())) {
            m_domNode.setNodeValue(str);
            fireNodeValueChanged(this);
        }
    }//}}}
    
    //{{{ getNodeType()
    /**
     * <p>Gets the type of the node specified in the W3C Node interface.</p>
     * @return the node type
     */
    public short getNodeType() {
        return m_domNode.getNodeType();
    }//}}}
    
    //{{{ getParentNode()
    /**
     * <p>Gets the parent AdapterNode object.</p>
     * @return the AdapterNode that is the parent of this node
     */
    public AdapterNode getParentNode() {
        return m_parentNode;
    }//}}}
    
    //{{{ getAttributes()
    /**
     * <p>Gets the attributes associated with this node.</p>
     * @return a map of the attributes associated with this node.
     *         <code>null</code> if this is not an element node
     */
    public NamedNodeMap getAttributes() {
        return m_domNode.getAttributes();
    }//}}}
    
    //{{{ addAdapterNode()
    /**
     * <p>Adds a new child to this node given the node name, value, and type.</p>
     * @param name the name of the new child node
     * @param value the value of the new child node
     * @param type the type of the new child node as specified by the W3C Node
     *             interface
     * @return the new child that was created
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified name
     *                      or value contains an illegal character.
     * @throws DOMException NOT_SUPPORTED_ERR: Raised if the node type is not
     *                      supported.
     * @throws DOMException HIERARCHY_REQUEST_ERR: Raised if this node is of a
     *                      type that does not allow children of the type of the
     *                      newChild node, or if the node to append is one of this
     *                      node's ancestors or this node itself.
     * @throws DOMException WRONG_DOCUMENT_ERR: Raised if newChild was created
     *                      from a different document than the one that created
     *                      this node.
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly or if the previous parent of the node being
     *                      inserted is readonly.
     */
    public AdapterNode addAdapterNode(String name, String value, short type, int index) throws DOMException {
        return addAdapterNodeAt(m_rootDocument.newAdapterNode(this, name, value, type), index);
    }//}}}
    
    //{{{ addAdapterNode()
    
    /**
     * Adds an already existing AdapterNode to this node as a child. The node
     * is added after all child nodes that this node contains.
     * @param node the node to be added.
     * @return a reference to the node that was added.
     * @throws DOMException HIERARCHY_REQUEST_ERR: Raised if this node is of a
     *                      type that does not allow children of the type of the
     *                      newChild node, or if the node to append is one of this
     *                      node's ancestors or this node itself.
     * @throws DOMException WRONG_DOCUMENT_ERR: Raised if newChild was created
     *                      from a different document than the one that created
     *                      this node.
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly or if the previous parent of the node being
     *                      inserted is readonly.
     */
    public AdapterNode addAdapterNode(AdapterNode node) throws DOMException {
        return addAdapterNodeAt(node, childCount());
    }//}}}
    
    //{{{ addAdapterNodeAt()
    /**
     * Adds an already existing AdapterNode to this node at a specified
     * location. The location is zero indexed so it can be any number greater
     * than or equal to zero and less than or equal to the number of children
     * contained currently. Using a location that is one index greater than
     * the last child's index <code>(location == childCount())</code> then the
     * node is added at the end.
     * @param node the node to add to this parent node.
     * @param location the location to add it at.
     * @return the node added.
     * @throws DOMException if the addition of the node is not allowed or the
     *                      location is invalid.
     */
    public AdapterNode addAdapterNodeAt(AdapterNode node, int location) throws DOMException {
        if (node != null) {
            if (location >= 0 && location <= childCount()) {
                if (m_children.indexOf(node) == location) {
                    //node is already in the location specified
                    return node;
                }
                //add to this AdapterNode and the DOM.
                if (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
                    //Add all children of the document fragment
                    for(int i=0; i<node.childCount(); i++) {
                        addAdapterNodeAt(node.child(i), location+i);
                    }
                } else {
                    /*
                    if the node is already contained in this node
                    then we are effectively moving the node.
                    */
                    if (m_children.contains(node)) {
                        if (location > m_children.indexOf(node)) {
                            location -= 1;
                        }
                        m_children.remove(node);
                    }
                    if (location >= m_children.size()) {
                        m_domNode.appendChild(node.getNode());
                        ensureChildrenSize(location);
                        m_children.add(node);
                    } else {
                        m_domNode.insertBefore(node.getNode(), child(location).getNode());
                        m_children.add(location, node);
                    }
                    
                    //Remove from previous parent
                    AdapterNode previousParent = node.getParentNode();
                    if (previousParent != this) {
                        if (previousParent != null) {
                            previousParent.removeChild(node);
                        }
                    }
                    node.setParent(this);
                    fireNodeAdded(this, node);
                }
            } else {
                throw new DOMException(DOMException.INDEX_SIZE_ERR, "The location to insert this node is invalid.");
            }
        }
        return node;
    }//}}}
    
    //{{{ remove()
    /**
     * <p>Removes a child from this node.</p>
     * @param child the child node to remove from this node
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly.
     * @throws DOMException NOT_FOUND_ERR: Raised if oldChild is not
     *                      a child of this node.
     */
    public void remove(AdapterNode child) throws DOMException {
        if (child != null) {
            m_domNode.removeChild(child.getNode());
            m_children.remove(child);
            child.setParent(null);
            fireNodeRemoved(this, child);
        }
    }//}}}
    
    //{{{ entityDeclared()
    /**
     * Determines if the entity was declared by the DTD.
     * @param entityName the name of the entity
     * @return true if the entity was declared in this document
     */
    public boolean entityDeclared(String entityName) {
        if(m_domNode.getOwnerDocument().getDoctype() != null) {
            NamedNodeMap entities = m_domNode.getOwnerDocument().getDoctype().getEntities();
            
            return (entities.getNamedItem(entityName) != null);
        } else {
            return false;
        }
    }//}}}
    
    //{{{ setAttribute()
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
    public void setAttribute(String name, String value) throws DOMException {
        if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)m_domNode;
            element.setAttribute(name,value);
            fireAttributeChanged(this, name);
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
        }
    }//}}}
    
    //{{{ getAttribute()
    /**
     * <p>Gets the value of an attribute associated with this node.</p>
     * @param name the name of the attribute
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     */
    public String getAttribute(String name) throws DOMException {
        if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)m_domNode;
            return element.getAttribute(name);
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
        }
    }//}}}
    
    //{{{ removeAttributeAt()
    /**
     * <p>Removes an attribute at the given index.</p>
     * <p><b>Note:</b> Attributes are sorted alphabetically.</p>
     * @param index the index of the node to remove
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly
     */
    public void removeAttributeAt(int index) throws DOMException {
        if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)m_domNode;
            NamedNodeMap attrs = element.getAttributes();
            Node attr = attrs.item(index);
            element.removeAttribute(attr.getNodeName());
            fireAttributeChanged(this, attr.getNodeName());
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
        }
    }//}}}
    
    //{{{ removeAttribute()
    /**
     * <p>Removes an attribute by name.</p>
     * @param attr the name of the attribute to remove
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly
     */
    public void removeAttribute(String attr) throws DOMException {
        if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)m_domNode;
            element.removeAttribute(attr);
            fireAttributeChanged(this, attr);
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
        }
    }//}}}
    
    //{{{ getAttributeAt()
    /**
     * <p>Gets the value of an attribute at the given index</p>
     * <p><b>Note:</b> Attributes are sorted alphabetically.</p>
     * @param index the index of the attribute to get
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     */
    public String getAttributeAt(int index) throws DOMException {
        if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element)m_domNode;
            NamedNodeMap attrs = element.getAttributes();
            Node attr = attrs.item(index);
            return attr.getNodeName();
        } else {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
        }
    }//}}}
    
    //{{{ equals()
    /**
     * <p>Compares this AdapterNode with another.</p>
     * @param node the AdapterNode to compare to
     * @return true if the underlying Node object for the two AdapterNodes is
     *         is the same.
     */
    public boolean equals(Object node) {
        return (node instanceof AdapterNode) && (((AdapterNode)node).getNode() == m_domNode);
    }//}}}
    
    //{{{ addAdapterNodeListener()
    /**
     * <p>Adds an AdapterNodeListener to be notified when this node changes</p>
     * @param listener the listener to add
     */
    public void addAdapterNodeListener(AdapterNodeListener listener) {
        m_listeners.add(listener);
    }//}}}
    
    //{{{ removeAdapterNodeListener()
    /**
     * <p>Removes a listener from this node if it exists</p>
     * @param listener the listener to remove
     */
    public void removeAdapterNodeListener(AdapterNodeListener listener) {
        m_listeners.remove(m_listeners.indexOf(listener));
    }//}}}
    
    //{{{ serializeToString()
    /**
     * Serializes this Node to a string based on the owning XMLDocument's
     * properties.
     * @return the string representation of this node.
     */
    public String serializeToString() {
        return m_rootDocument.serializeNodeToString(this);
    }//}}}
    
    //{{{ toString()
        
    public String toString() {
        String s = "";
        if (getNodeType() == Node.DOCUMENT_NODE)
            return "Document Root";
        String nodeName = getNodeName();
        if (! nodeName.startsWith("#")) {   
            s += nodeName;
        }
        if (s.equals("")) {
            if (getNodeValue() != null) {
                String t = getNodeValue().trim();
                s += t;
            }
        }
        return s;
    }//}}}
    
    //{{{ Protected members
    
    //{{{ getNode()
    /**
     * <p>Gets the underlying Node object that this AdapterNode wraps.</p>
     * @return the underlying Node object for this AdapterNode object
     */
    Node getNode() {
        return m_domNode;
    }//}}}
    
    //{{{ removeChild()
    /**
     * <p>Ensures an AdapterNode is not in the list of children
     * children 
     */
    /*
    This is required to help maintain sync between the AdapterNode tree
    */
    void removeChild(AdapterNode node) {
        if (node != null) {
            m_children.remove(node);
        }
    }//}}}
    
    //{{{ setParent()
    /**
     * <p>Sets the parent node of this AdapterNode.</p>
     * @param parent the new parent for this AdapterNode
     */
    void setParent(AdapterNode parent) {
        m_parentNode = parent;
    }//}}}
    
    //}}}
    
    //{{{ Private members
    
    //{{{ fireNodeAdded()
    private void fireNodeAdded(AdapterNode source, AdapterNode child) {
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.nodeAdded(source, child);
        }
    }//}}}
    
    //{{{ fireNodeRemoved()
    private void fireNodeRemoved(AdapterNode source, AdapterNode child) {
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.nodeRemoved(source, child);
        }
    }//}}}
    
    //{{{ fireLocalNameChanged()
    private void fireLocalNameChanged(AdapterNode source) {
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.localNameChanged(source);
        }
    }//}}}
    
    //{{{ fireNamespaceChanged()
    private void fireNamespaceChanged(AdapterNode source) {
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.namespaceChanged(source);
        }
    }//}}}
    
    //{{{ fireNodeValueChanged()
    private void fireNodeValueChanged(AdapterNode source) {
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.nodeValueChanged(source);
        }
    }//}}}
    
    //{{{ fireAttributeChanged()
    private void fireAttributeChanged(AdapterNode source, String attr) {
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.attributeChanged(source, attr);
        }
    }//}}}
    
    //{{{ ensureChildrenSize()
    private void ensureChildrenSize(int size) {
        while (m_children.size() < size) {
            m_children.add(null);
        }
    }//}}}
    
    private AdapterNode m_parentNode;
    private XMLDocument m_rootDocument;
    private ArrayList m_children = new ArrayList();
    
    private Node m_domNode;
    private ArrayList m_listeners = new ArrayList();
    private Properties m_props = new Properties();
    //}}}
}
