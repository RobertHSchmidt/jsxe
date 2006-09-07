/*
AdapterNode.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2002 Ian Lewis (IanLewis@member.fsf.org)
Portions Copyright (C) 2003 Bilel Remmache

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

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.util.MiscUtilities;
import net.sourceforge.jsxe.dom.undo.*;
import net.sourceforge.jsxe.dom.completion.*;
import net.sourceforge.jsxe.dom.ls.DOMSerializer;
//}}}

//{{{ Java Base Classes
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ListIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.undo.UndoableEdit;
//}}}

//{{{ DOM classes
import org.w3c.dom.*;
import org.w3c.dom.events.*;
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
 * @see XMLDocument#addAdapterNode(AdapterNode, String, String, short)
 */
public class AdapterNode {
    
    //{{{ Public static properties
    public static final short ATTRIBUTE_NODE = Node.ATTRIBUTE_NODE;
    public static final short CDATA_SECTION_NODE = Node.CDATA_SECTION_NODE;
    public static final short COMMENT_NODE = Node.COMMENT_NODE;
    public static final short DOCUMENT_FRAGMENT_NODE = Node.DOCUMENT_FRAGMENT_NODE;
    public static final short DOCUMENT_NODE = Node.DOCUMENT_NODE;
    public static final short DOCUMENT_TYPE_NODE = Node.DOCUMENT_TYPE_NODE;
    public static final short ELEMENT_NODE = Node.ELEMENT_NODE;
    public static final short ENTITY_NODE = Node.ENTITY_NODE;
    public static final short ENTITY_REFERENCE_NODE = Node.ENTITY_REFERENCE_NODE;
    public static final short NOTATION_NODE = Node.NOTATION_NODE;
    public static final short PROCESSING_INSTRUCTION_NODE = Node.PROCESSING_INSTRUCTION_NODE;
    public static final short TEXT_NODE = Node.TEXT_NODE;
    //}}}
    
    //{{{ AdapterNode constructor
    /**
     * Creates a new AdapterNode for a node in a DOM tree. This is normally used
     * by an implementation of the XMLDocument interface. This node will not be
     * part of an XMLDocument or the child of any element. Use the
     * <code>newAdapterNode()</code> method in the XMLDocument interface to
     * create AdapterNodes.
     * @param node the Node object that this AdapterNode represents.
     */
    AdapterNode(Node node) {
        if (node == null) {
            throw new NullPointerException();
        }
        m_domNode = node;
    }//}}}
    
    //{{{ AdapterNode constructor
    /**
     * <p>Creates an AdapterNode for a root document node. This is normally used
     * by an implementation of the XMLDocument interface when it is created.</p>
     * @param xmlDocument the XMLDocument object that wraps the Document object
     * @param document the document object that this AdapterNode is to
     *                 represent
     */
    AdapterNode(XMLDocument xmlDocument, Document document) {
        if (xmlDocument == null || document == null) {
            throw new NullPointerException();
        }
        m_domNode = document;
        ((EventTarget)m_domNode).addEventListener("DOMSubtreeModified", 
            new EventListener() {
                public void handleEvent(Event evt) {
                    Log.log(Log.DEBUG, this, evt.toString());
                }
            }, true);
        m_rootDocument = xmlDocument;
    }//}}}
    
    //{{{ AdapterNode constructor
    /**
     * Creates a new AdapterNode for a node in a DOM tree. This is normally used
     * by an implementation of the XMLDocument interface. Use the
     * <code>newAdapterNode()</code> method in the XMLDocument interface to
     * create AdapterNodes.
     * @param parent the parent AdapterNode object for the parent DOM node
     * @param node the Node object that this AdapterNode represents. This node
     *             should be a child of the Node that is wrapped by the parent
     *             AdapterNode
     */
    AdapterNode(AdapterNode parent, Node node) {
        if (node == null) {
            throw new NullPointerException();
        }
        m_domNode = node;
        setParent(parent);
    }//}}}
    
    //{{{ getOwnerDocument()
    /**
     * Gets the XMLDocument that owns this AdapterNode
     * @return The owning XMLDocument
     */
    public XMLDocument getOwnerDocument() {
        return m_rootDocument;
    }//}}}
    
    //{{{ getProperty()
    /**
     * Gets a property for the key given.
     * @param key the key to the properties list
     * @return the value of the property for the given key.
     */
    public String getProperty(String key) {
        synchronized(propertyLock) {
            return m_props.getProperty(key);
        }
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
        synchronized(propertyLock) {
            return m_props.getProperty(key, defaultValue);
        }
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
    
    //{{{ copy()
    /**
     * Copies this node into a new AdapterNode. The copy will have no parent
     * node.
     * @param deep if true then the subtree is copied as well.
     * @since jsXe 0.4 pre3
     */
    public AdapterNode copy(boolean deep) {
        Node newNode = m_domNode.cloneNode(deep);
        AdapterNode node = new AdapterNode(newNode);
        return node;
    }//}}}
    
    //{{{ child()
    /**
     * <p>Gets the child node at the given index. This method is thread safe.</p>
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
        XMLDocument rootDocument = getOwnerDocument();
        AdapterNode child = null;
        
        synchronized(childrenLock) {
        
            
            if (index < childCount()) {
                if (index < m_children.size()) {
                    try {
                        child = (AdapterNode)m_children.get(index);
                        if (child == null) {
                            //the size was ok but no AdapterNode was at this index
                            child = rootDocument.newAdapterNode(this, m_domNode.getChildNodes().item(index));
                            m_children.set(index, child);
                        }
                    } catch (IndexOutOfBoundsException ioobe) {}
                } else {
                    /*
                    Populate the other elements with null until we
                    have the correct size.
                    */
                    ensureChildrenSize(index+1);
                    child = rootDocument.newAdapterNode(this, m_domNode.getChildNodes().item(index));
                    m_children.set(index, child);
                }
            }
           return child;
        }
    }//}}}
    
    //{{{ childCount()
    /**
     * <p>Gets the number of children that this node has. This method is thread
     *    safe.</p>
     * @return the number of children of this node
     */
    public int childCount() {
        Object lock = getLock();
        
        /*
        Accesses to the underlying DOM should be synchronized on
        the root XMLDocument
        */
        synchronized (lock) {
            NodeList childNodes = m_domNode.getChildNodes();
            if (childNodes != null) {
                return childNodes.getLength();
            } else {
                return 0;
            }
        }
    }//}}}
    
    //{{{ getNSPrefix()
    /**
     * Gets the namespace prefix for this node. If this node is not a member
     * of a namespace then this method returns null.
     * @return the namespace prefix for this node. null if no namespace
     */
    public String getNSPrefix() {
        Object lock = getLock();
        /*
        Accesses to the underlying DOM should be synchronized on
        the root XMLDocument
        */
        synchronized (lock) {
            return m_domNode.getPrefix();
        }
    }//}}}
    
    //{{{ setNSPrefix()
    /**
     * Sets the namespace prefix for this node. To remove this node from a
     * namespace this method should be passed null.
     * @param prefix The new prefix for this node
     * @throws DOMException if this namespace prefix is not valid.
     * INVALID_CHARACTER_ERR: Raised if the specified prefix contains an illegal character, per the XML 1.0 specification .
     * NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     * NAMESPACE_ERR: Raised if the specified prefix is malformed per the Namespaces in XML specification, if the namespaceURI of this node is null, if the specified prefix is "xml" and the namespaceURI of this node is different from "http://www.w3.org/XML/1998/namespace", if this node is an attribute and the specified prefix is "xmlns" and the namespaceURI of this node is different from " http://www.w3.org/2000/xmlns/", or if this node is an attribute and the qualifiedName of this node is "xmlns" .
     */
    public void setNSPrefix(String prefix) throws DOMException {
        Object lock = getLock();
        synchronized(lock) {
            try {
                beginCompoundEdit();
                String oldPrefix = getNSPrefix();
                /*
                for whatever reason if I set a prefix on an node with no prefix
                you get DOMException.NAMESPACE_ERRs. If we are adding a NS then
                just rename the node.
                */
                if (oldPrefix != null && !oldPrefix.equals("")) {
                    m_domNode.setPrefix(prefix);
                } else {
                    renameElementNode(prefix, getLocalName());
                }
                fireNamespaceChanged(this, oldPrefix, prefix);
            } finally {
                endCompoundEdit();
            }
        }
    }//}}}
    
    //{{{ getNodeName()
    /**
     * Gets the full qualified name for this node including the local name
     * and namespace prefix.
     * @return the full qualified name of this node
     */
    public String getNodeName() {
        Object lock = getLock();
        synchronized(lock) {
            return m_domNode.getNodeName();
        }
    }//}}}
    
    //{{{ setNodeName()
    /**
     * Sets the full qualified name of this node. This method is namespace aware
     * @param qualifiedName the new qualified name
     */
    public void setNodeName(String qualifiedName) throws DOMException {
        Object lock = getLock();
        
        synchronized(lock) {
            
            String oldPrefix = getNSPrefix();
            String oldLocalName = getLocalName();
            
            String prefix = MiscUtilities.getNSPrefixFromQualifiedName(qualifiedName);
            String localName = MiscUtilities.getLocalNameFromQualifiedName(qualifiedName);
            
            beginCompoundEdit();
            
            if (getNodeType() == ELEMENT_NODE) {
                renameElementNode(prefix, localName);
            } else {
                if (getNodeType() == PROCESSING_INSTRUCTION_NODE) {
                    renamePINode(localName);
                } else {
                    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "An attempt was made to rename a node that is not supported.");
                }
            }
            
            if (!MiscUtilities.equals(oldPrefix, prefix)) {
                fireNamespaceChanged(this, oldPrefix, prefix);
            }
            if (!MiscUtilities.equals(oldLocalName, localName)) {
                fireLocalNameChanged(this, oldLocalName, localName);
            }
            
            endCompoundEdit();
        }
    }//}}}
    
    //{{{ getLocalName()
    /**
     * <p>Gets the local name of this node.</p>
     * @return the local name of the node
     */
    public String getLocalName() {
        Object lock = getLock();
        synchronized(lock) {
            return m_domNode.getLocalName();
        }
    }//}}}
    
    //{{{ setLocalName()
    /**
     * <p>Sets the local name of the node.</p>
     * @param newValue the new local name for this node
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified name
     *                      contains an illegal character.
     */
    public void setLocalName(String localName) throws DOMException {
        Object lock = getLock();
        
        synchronized(lock) {
        
            if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
                //Verify that this really is a change
                String oldLocalName = m_domNode.getLocalName();
                if (!oldLocalName.equals(localName)) {
                    
                    renameElementNode(getNSPrefix(), localName);
                    
                    fireLocalNameChanged(this, oldLocalName, localName);
                }
            } else {
                if (m_domNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                    if (m_domNode.getNodeName() != localName) {
                        Node newNode = m_domNode.getOwnerDocument().createProcessingInstruction(localName, m_domNode.getNodeValue());
                        m_domNode.getParentNode().replaceChild(newNode, m_domNode);
                        m_domNode = newNode;
                    }
                } else {
                    if (m_domNode.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
                        if (m_domNode.getNodeName() != localName) {
                            XMLDocument doc = getOwnerDocument();
                            if (doc != null && doc.entityDeclared(localName)) {
                                Node newNode = m_domNode.getOwnerDocument().createEntityReference(localName);
                                m_domNode.getParentNode().replaceChild(newNode, m_domNode);
                                m_domNode = newNode;
                            } else {
                                throw new DOMException(DOMException.SYNTAX_ERR, "Entity "+"\""+localName+"\""+" is not declared in the Schema");
                            }
                        }
                    } else {
                        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Renaming this node type is not supported.");
                    }
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
        Object lock = getLock();
        synchronized(lock) {
            return m_domNode.getNodeValue();
        }
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
        Object lock = getLock();
        
        synchronized(lock) {
            // Make sure there is a change.
            if (!MiscUtilities.equals(str, m_domNode.getNodeValue())) {
                String oldValue = getNodeValue();
                m_domNode.setNodeValue(str);
                fireNodeValueChanged(this, oldValue, str);
            }
        }
    }//}}}
    
    //{{{ getNodeType()
    /**
     * <p>Gets the type of the node specified in the W3C Node interface.</p>
     * @return the node type
     */
    public short getNodeType() {
        Object lock = getLock();
        
        synchronized(lock) {
            return m_domNode.getNodeType();
        }
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
        Object lock = getLock();
        
        synchronized(lock) {
            return m_domNode.getAttributes();
        }
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
        return addAdapterNodeAt(getOwnerDocument().newAdapterNode(this, name, value, type), index);
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
        
        synchronized(childrenLock) {
            if (node != null) {
                if (location >= 0 && location <= childCount()) {
                    if (m_children.indexOf(node) == location) {
                        //node is already in the location specified
                        return node;
                    }
                    
                        
                    Object lock = getLock();
                    synchronized(lock) {
                        
                        try {
                            beginCompoundEdit();
                            //add to this AdapterNode and the DOM.
                            if (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
                                //Add all children of the document fragment
                                for (int i=0; i<node.childCount(); i++) {
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
                                    int index = index(node);
                                    m_children.remove(node);
                                    addUndoableEdit(new RemoveNodeChange(this, node, index));
                                } else {
                                    //Remove from previous parent
                                    AdapterNode previousParent = node.getParentNode();
                                    if (previousParent != this) {
                                        if (previousParent != null) {
                                            previousParent.removeChild(node);
                                        }
                                    }
                                }
                                if (location >= m_children.size()) {
                                    m_domNode.appendChild(node.getNode());
                                    ensureChildrenSize(location);
                                    m_children.add(node);
                                } else {
                                    m_domNode.insertBefore(node.getNode(), child(location).getNode());
                                    m_children.add(location, node);
                                }
                                
                                node.setParent(this);
                                fireNodeAdded(this, node, location);
                            }
                        } finally {
                            endCompoundEdit();
                        }
                    }
                } else {
                    throw new DOMException(DOMException.INDEX_SIZE_ERR, "The location to insert this node is invalid.");
                }
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
        
        synchronized(childrenLock) {
            Object lock = getLock();
            synchronized(lock) {
                
                if (child != null) {
                    if (getNodeType() == Node.DOCUMENT_NODE && child.getNodeType() == Node.ELEMENT_NODE) {
                        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "You cannot remove the root element node.");
                    }
                    if (child.getNodeType() != Node.DOCUMENT_TYPE_NODE) {
                        int index = index(child);
                        m_domNode.removeChild(child.getNode());
                        m_children.remove(child);
                        child.setParent(null);
                        fireNodeRemoved(this, child, index);
                    } else {
                        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Cannot remove Document Type Nodes");
                    }
                }
            }
        }
    }//}}}
    
    //{{{ setAttribute()
    /**
     * <p>Sets an attribute of this node. If the specified attribute does not
     * exist it is created.</p>
     * @param name the qualified name of the attribute
     * @param value the new value of the attribute
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     * @throws DOMException INVALID_CHARACTER_ERR: Raised if the specified
     *                      qualified name contains an illegal character, per
     *                      the XML 1.0 specification
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly
     */
    public void setAttribute(String name, String value) throws DOMException {
        Object lock = getLock();
        
        synchronized(lock) {
        
            if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
                
                Element element = (Element)m_domNode;
                String prefix    = MiscUtilities.getNSPrefixFromQualifiedName(name);
                
                String oldValue = getAttribute(name);
                
                if (!MiscUtilities.equals(oldValue, value)) {
                    //check if we are setting a namespace declaration
                    if ("xmlns".equals(prefix)) {
                        //if so then make sure the value is valid
                        if (value != null && value.equals("")) {
                            throw new DOMException(DOMException.NAMESPACE_ERR, "An attempt was made to create an empty namespace declaration");
                        }
                    }
                    
                    /*
                    If the attribute did not have a prefix to begin with then
                    using setAttributeNS may add a new attribute node to the element
                    even though the attribute already exists.
                    */
                    if (prefix != null && !prefix.equals("")) {
                        element.setAttributeNS(lookupNamespaceURI(prefix),name,value);
                    } else {
                        /*
                        setAttribute doesn't throw an error if the first character
                        is a ":"
                        */
                        if (name != null && !name.equals("") && name.charAt(0)==':') {
                            throw new DOMException(DOMException.NAMESPACE_ERR, "An attribute name cannot have a ':' as the first character");
                        }
                        element.setAttribute(name, value);
                    }
                    fireAttributeChanged(this, name, oldValue, value);
                }
            } else {
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
            }
        }
    }//}}}
    
    //{{{ getAttribute()
    /**
     * <p>Gets the value of an attribute associated with this node.</p>
     * @param name the qualified name of the attribute
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     */
    public String getAttribute(String name) throws DOMException {
        
        Object lock = getLock();
        
        synchronized(lock) {
            if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
                String localName = MiscUtilities.getLocalNameFromQualifiedName(name);
                String prefix    = MiscUtilities.getNSPrefixFromQualifiedName(name);
                
                Element element = (Element)m_domNode;
                if (prefix != null && !prefix.equals("")) {
                    //getAttributeNS returns "" even if the attribute isn't in the
                    //element.
                    if (element.getAttributeNodeNS(lookupNamespaceURI(prefix),localName) == null) {
                        return null;
                    }
                    return element.getAttributeNS(lookupNamespaceURI(prefix),localName);
                } else {
                    //getAttribute returns "" even if the attribute isn't in the
                    //element.
                    if (element.getAttributeNode(name) == null) {
                        return null;
                    }
                    return element.getAttribute(name);
                }
            } else {
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
            }
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
        Object lock = getLock();
        
        synchronized(lock) {
            
            if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)m_domNode;
                NamedNodeMap attrs = element.getAttributes();
                if (attrs != null) {
                    removeAttribute(attrs.item(index).getNodeName());
                }
            } else {
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
            }
        }
    }//}}}
    
    //{{{ removeAttribute()
    /**
     * <p>Removes an attribute by name.</p>
     * @param attr the qualified name of the attribute to remove
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     * @throws DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly
     */
    public void removeAttribute(String attr) throws DOMException {
        Object lock = getLock();
        
        synchronized(lock) {
        
            if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)m_domNode;
                String prefix = MiscUtilities.getNSPrefixFromQualifiedName(attr);
                String localName = MiscUtilities.getLocalNameFromQualifiedName(attr);
                String oldValue = getAttribute(attr);
                
                //make sure we are actually removing an attribute.
                if (oldValue != null) {
                    //Check if we are removing a namespace declaration
                    //This is a somewhat expensive operation, may need to
                    //optimize in the future
                    if ("xmlns".equals(prefix)) {
                        //check if there are nodes using the namespace
                        String uri = lookupNamespaceURI(localName);
                        //check this element's namespace
                        if (!uri.equals(element.getNamespaceURI())) {
                            //check for decendent elements with this namespace
                            NodeList list = element.getElementsByTagName("*");
                            //check if an attribute with this NS is used
                            for (int i=0; i<list.getLength(); i++) {
                                Node ele = list.item(i);
                                if (uri.equals(ele.getNamespaceURI())) {
                                    throw new DOMException(DOMException.NAMESPACE_ERR, "An attempt was made to remove a namespace declaration when nodes exist that use it");
                                }
                                //now check the attributes
                                NamedNodeMap attrs = ele.getAttributes();
                                for (int j=0; j<attrs.getLength(); j++) {
                                    Node foundAttr = attrs.item(i);
                                    if (uri.equals(foundAttr.getNamespaceURI())) {
                                        throw new DOMException(DOMException.NAMESPACE_ERR, "An attempt was made to remove a namespace declaration when nodes exist that use it");
                                    }
                                }
                            }
                        } else {
                            throw new DOMException(DOMException.NAMESPACE_ERR, "An attempt was made to remove a namespace declaration when nodes exist that use it");
                        }
                    }
                    if (prefix != null && !prefix.equals("")) {
                        element.removeAttributeNS(lookupNamespaceURI(prefix),localName);
                    } else {
                        element.removeAttribute(localName);
                    }
                    fireAttributeChanged(this, attr, oldValue, null);
                }
            } else {
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
            }
        }
    }//}}}
    
    //{{{ getAttributeNameAt()
    /**
     * <p>Gets the qualified name of an attribute at the given index</p>
     * <p><b>Note:</b> Attributes are sorted alphabetically.</p>
     * @param index the index of the attribute to get
     * @return the qualified name of the attribute at the index
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     */
    public String getAttributeNameAt(int index) throws DOMException {
        Object lock = getLock();
        
        synchronized(lock) {
        
            if (m_domNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)m_domNode;
                NamedNodeMap attrs = element.getAttributes();
                Node attr = attrs.item(index);
                return attr.getNodeName();
            } else {
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Only Element Nodes can have attributes");
            }
        }
    }//}}}
    
    //{{{ getAttributeAt()
    /**
     * <p>Gets the value of an attribute at the given index</p>
     * <p><b>Note:</b> Attributes are sorted alphabetically.</p>
     * @param index the index of the attribute to get
     * @return the value of the attribute at the index
     * @throws DOMException NOT_SUPPORTED_ERR: if this is not an element node
     */
    public String getAttributeAt(int index) throws DOMException {
        Object lock = getLock();
        
        synchronized(lock) {
            return getAttribute(getAttributeNameAt(index));
        }
    }//}}}
    
    //{{{ getAllowedElements()
    /**
     * Gets a sorted list of all the elements (ElementDecl objects) allowed as
     * children of this node as defined in the DTD or Schema.
     * @return a list of ElementDecl objects.
     * @since jsXe 0.4 pre1
     */
    public List getAllowedElements() {
        
        XMLDocument rootDocument = getOwnerDocument();
        if (rootDocument != null) {
            Object lock = getLock();
            synchronized(lock) {
                HashMap mappings = rootDocument.getCompletionInfoMappings();
                ElementDecl thisDecl = rootDocument.getElementDecl(getNodeName());
                
                ArrayList allowedElements = new ArrayList();
                
                if (thisDecl != null) {
                    allowedElements.addAll(thisDecl.getChildElements(getNSPrefix()));
                }
                
                // add everything but the parent's prefix now
                Iterator iter = mappings.entrySet().iterator();
                while(iter.hasNext()) {
                    Map.Entry entry = (Map.Entry)iter.next();
                    String prefix = entry.getKey().toString();
                    String myprefix = getNSPrefix();
                    if (myprefix == null) {
                        myprefix = "";
                    }
                    if (!prefix.equals(myprefix)) {
                        CompletionInfo info = (CompletionInfo)entry.getValue();
                        info.getAllElements(prefix, allowedElements);
                    }
                }
                MiscUtilities.quicksort(allowedElements, new ElementDecl.Compare());
                return allowedElements;
            }
        } else {
            return new ArrayList();
        }
    }//}}}
    
    //{{{ getElementDecl()
    /**
     * Gets the Element declaration that defines this element
     */
    public ElementDecl getElementDecl() {
        Object lock = getLock();
        
        synchronized(lock) {
            XMLDocument doc = getOwnerDocument();
            if (doc != null) {
                return doc.getElementDecl(getNodeName());
            } else {
                return null;
            }
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
        if (listener != null) {
            m_listeners.add(listener);
        }
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
     * Serializes this Node to a string based on the last owning
     * XMLDocument's properties.
     * @return the string representation of this node.
     */
    public String serializeToString() {
        
        Object lock = getLock();
        
        synchronized(lock) {
            XMLDocument owner = getOwnerDocument();
            if (owner != null) {
                return owner.serializeNodeToString(this);
            } else {
                if (m_lastRootDocument != null) {
                    return m_lastRootDocument.serializeNodeToString(this);
                } else {
                    //node was never owned? write it out using the default config
                    String value = null;
                    try {
                        DOMSerializer serializer = new DOMSerializer();
                        serializer.setNewLine("\n");
                        value = serializer.writeToString(m_domNode);
                    } catch (DOMException e) {
                        Log.log(Log.WARNING, this, "Could not write node to string");
                    }
                    return value;
                }
            }
        }
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
     * <p>Ensures an AdapterNode is not in the list of children</p>
     */
    /*
    This is required to help maintain sync between the AdapterNode tree
    */
    void removeChild(AdapterNode node) {
        synchronized(childrenLock) {
            Object lock = getLock();
            synchronized(lock) {
        
                if (node != null) {
                    int index = index(node);
                    m_children.remove(node);
                    if (index != -1) {
                        addUndoableEdit(new RemoveNodeChange(this, node, index));
                    }
                }
            }
        }
    }//}}}
    
    //{{{ setParent()
    /**
     * <p>Sets the parent node of this AdapterNode.</p>
     * @param parent the new parent for this AdapterNode
     */
    void setParent(AdapterNode parent) {
        Object lock = getLock();
        
        synchronized(lock) {
            m_parentNode = parent;
            if (parent != null) {
                m_lastRootDocument = m_rootDocument;
                m_rootDocument = m_parentNode.getOwnerDocument();
            } else {
                m_lastRootDocument = m_rootDocument;
                m_rootDocument = null;
            }
        }
    }//}}}
    
    //}}}
    
    //{{{ Private members
    
    //{{{ fireNodeAdded()
    private void fireNodeAdded(AdapterNode source, AdapterNode child, int index) {
        addUndoableEdit(new AddNodeChange(source, child, index));
        
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.nodeAdded(source, child);
        }
        
        Node doc = getOwnerDocument().getAdapterNode().getNode();
        fireStructureChanged();
    }//}}}
    
    //{{{ fireNodeRemoved()
    private void fireNodeRemoved(AdapterNode source, AdapterNode child, int index) {
        addUndoableEdit(new RemoveNodeChange(source, child, index));
        
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.nodeRemoved(source, child);
        }
        fireStructureChanged();
    }//}}}
    
    //{{{ fireLocalNameChanged()
    private void fireLocalNameChanged(AdapterNode source, String oldValue, String newValue) {
        addUndoableEdit(new NodeNameChange(this, oldValue, newValue));
        
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.localNameChanged(source);
        }
        fireStructureChanged();
    }//}}}
    
    //{{{ fireNamespaceChanged()
    private void fireNamespaceChanged(AdapterNode source, String oldValue, String newValue) {
        addUndoableEdit(new NodePrefixChange(this, oldValue, newValue));
        
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.namespaceChanged(source);
        }
        fireStructureChanged();
    }//}}}
    
    //{{{ fireNodeValueChanged()
    private void fireNodeValueChanged(AdapterNode source, String oldValue, String newValue) {
        addUndoableEdit(new NodeValueChange(this, oldValue, newValue));
        
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.nodeValueChanged(source);
        }
        fireStructureChanged();
    }//}}}
    
    //{{{ fireAttributeChanged()
    private void fireAttributeChanged(AdapterNode source, String attr, String oldValue, String newValue) {
        addUndoableEdit(new AttributeChange(this, attr, oldValue, newValue));
        
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            AdapterNodeListener listener = (AdapterNodeListener)iterator.next();
            listener.attributeChanged(source, attr);
        }
        fireStructureChanged();
    }//}}}
    
    //{{{ fireStructureChanged()
    public void fireStructureChanged() {
        XMLDocument doc = getOwnerDocument();
        if (doc != null) {
            doc.fireStructureChanged(this);
        }
    }//}}}
    
    //{{{ ensureChildrenSize()
    private void ensureChildrenSize(int size) {
        while (m_children.size() < size) {
            m_children.add(null);
        }
    }//}}}
    
    //{{{ renameElementNode()
    /**
     * Renames this element node to the prefix and local name given. This
     * should only be called on an element node.
     */
    private void renameElementNode(String prefix, String localName) throws DOMException {
        
        Document document = m_domNode.getOwnerDocument();
        
        //replace the changed node; maintain the namespace URI;
        String qualifiedName = localName;
        if (prefix != null) {
            qualifiedName = prefix+":"+localName;
        }
        
        String nsURI = lookupNamespaceURI(prefix);;
        
        m_domNode = document.renameNode(m_domNode, nsURI, qualifiedName);
    }//}}}
    
    //{{{ renamePINode()
    /**
     * Renames this processing instruction node with the target given. This
     * should only be called on an processing instruction node.
     */
    private void renamePINode(String target) {
        //get the nodes needed
        Node parent = m_domNode.getParentNode();
        Document document = m_domNode.getOwnerDocument();
        
        ProcessingInstruction newNode = document.createProcessingInstruction(target, m_domNode.getNodeValue());
        
        parent.replaceChild(newNode, m_domNode);
        m_domNode = newNode;
    }//}}}
    
    //{{{ lookupNamespaceURI()
    
    private String lookupNamespaceURI(String prefix) {
        //temporary xerces dependent solution
        if ("xmlns".equals(prefix)) {
            return "http://www.w3.org/2000/xmlns/";
        } else {
            return ((org.apache.xerces.dom.NodeImpl)m_domNode).lookupNamespaceURI(prefix);
        }
    }//}}}
    
    //{{{ addUndoableEdit()
    private void addUndoableEdit(UndoableEdit edit) {
        XMLDocument doc = getOwnerDocument();
        
        //edits are not added if the document is null. The
        //edits are just not undoable.
        if (doc != null) {
            doc.addUndoableEdit(edit);
        }
    }//}}}
    
    //{{{ getLock()
    
    private synchronized Object getLock() {
        XMLDocument doc = getOwnerDocument();
        if (doc != null) {
            return doc;
        } else {
            return this;
        }
    }//}}}
    
    //{{{ beginCompoundEdit()
    
    private void beginCompoundEdit() {
        XMLDocument doc = getOwnerDocument();
        if (doc != null) {
            doc.beginCompoundEdit();
        }
    }//}}}
    
    //{{{ endCompoundEdit()
    
    private void endCompoundEdit() {
        XMLDocument doc = getOwnerDocument();
        if (doc != null) {
            doc.endCompoundEdit();
        }
    }//}}}
    
    private AdapterNode m_parentNode;
    private XMLDocument m_rootDocument;
    private XMLDocument m_lastRootDocument;
    
    private ArrayList m_children = new ArrayList();
    
    private Node m_domNode;
    private ArrayList m_listeners = new ArrayList();
    private Properties m_props = new Properties();
    
    private Object propertyLock = new Object();
    /**
     * Children lock for when adding AdapterNodes to the children.
     */
    private Object childrenLock = new Object();
    //}}}
}
