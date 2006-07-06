/*
XMLNode.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)

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
import net.sourceforge.jsxe.dom.completion.*;
import net.sourceforge.jsxe.gui.Messages;
//}}}

//{{{ Java Base Classes
import java.util.*;
//}}}

//}}}

/**
 * <p>The XMLNode class is meant to represent an XMLNode in a XMLDocument tree
 *    but also as a section of text within that document tree. It
 *    allows tree modifications only, however these modifications
 *    will correspond to text modifications to the XMLDocument. Direct text
 *    modifications are only allowed at the XMLDocument level.</p>
 * 
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see XMLDocument
 * @since jsXe 0.5 pre2
 */
public abstract class XMLNode {
    
    //{{{ Public static properties
    /**
     * An attribute node.
     */
    public static final short ATTRIBUTE_NODE = 1;
    /**
     * A CDATA Section Node
     */
    public static final short CDATA_SECTION_NODE = 2;
    /**
     * A Comment Node
     */
    public static final short COMMENT_NODE = 3;
    /**
     * A Document Fragment
     */
    public static final short DOCUMENT_FRAGMENT_NODE = 4;
    /**
     * A Document Node
     */
    public static final short DOCUMENT_NODE = 5;
    /**
     * A DOCTYPE node
     */
    public static final short DOCUMENT_TYPE_NODE = 6;
    /**
     * An Element Node
     */
    public static final short ELEMENT_NODE = 7;
    /**
     * An Entity
     */
    public static final short ENTITY_NODE = 8;
    /**
     * An Entity Reference
     */
    public static final short ENTITY_REFERENCE_NODE = 9;
    /**
     * A Notation
     */
    public static final short NOTATION_NODE = 10;
    /**
     * A Processing Instruction
     */
    public static final short PROCESSING_INSTRUCTION_NODE = 11;
    /**
     * A Text Node
     */
    public static final short TEXT_NODE = 12;
    //}}}
    
    //{{{ XMLNode constructor
    /**
     * TODO
     * @param document the XMLDocument that owns this node.
     */
    XMLNode(XMLDocument document) {
        m_properties=new Properties();
        m_rootDocument=document;
    }//}}}
    
    //{{{ addXMLNodeListener()
    /**
     * <p>Adds an XMLNodeListener to be notified when this node changes</p>
     * @param listener the listener to add
     */
    public void addXMLNodeListener(XMLNodeListener listener) {
        m_listeners.add(listener);
    }//}}}
    
    //{{{ removeXMLNodeListener()
    /**
     * <p>Removes a listener from this node if it exists</p>
     * @param listener the listener to remove
     */
    public void removeXMLNodeListener(XMLNodeListener listener) {
        m_listeners.remove(m_listeners.indexOf(listener));
    }//}}}
    
    //{{{ getNodeType()
    /**
     * <p>Gets the type of the node.</p>
     * @return the node type
     */
    public abstract int getNodeType();//}}}
    
    //{{{ getOwnerDocument()
    /**
     * Gets the XMLDocument that owns this AdapterNode
     * @return The owning XMLDocument
     */
    public XMLDocument getOwnerDocument() {
        return m_rootDocument;
    }//}}}
    
    //{{{ indexOf()
    /**
     * <p>Returns the index of the given XMLNode if it is a child.</p>
     * @param child the child node of this node
     * @return the index where the child is located. -1 if the XMLNode is
     *         not a child
     */
    public int indexOf(AdapterNode child) {
        int count = getChildCount();
        for (int i=0; i<count; i++) {
            XMLNode n = this.getChildNode(i);
            if (child.equals(n)) return i;
        }
        //Returns here when child not in tree
        return -1;
    }//}}}
    
    //{{{ getChildCount()
    /**
     * <p>Gets the number of children that this node has.</p>
     * @return the number of children of this node
     */
    public int getChildCount() {
        return m_children.size();
    }//}}}
    
    //{{{ getChildNode()
    /**
     * <p>Gets the child node at the given index.</p>
     * @param index the index of the requested node
     * @return an XMLNode representing the node at the given index,
     *         null if the index is out of bounds
     */
    public XMLNode getChildNode(int index) {
        //TODO: create a way to instantiate nodes when they are requested.
        return (XMLNode)m_children.get(index);
    }//}}}
    
    //{{{ addChildNode()
    /**
     * <p>Adds a new child to this node given the node name, value, and type.</p>
     * @param name the name of the new child node
     * @param value the value of the new child node
     * @param type the type of the new child node
     * @param index the index where to add the new child node.
     * @return the new child that was created
     * @throws XMLException INVALID_CHARACTER_ERR: Raised if the specified name
     *                      or value contains an illegal character.
     * @throws XMLException NOT_SUPPORTED_ERR: Raised if the node type is not
     *                      supported.
     * @throws XMLException HIERARCHY_REQUEST_ERR: Raised if this node is of a
     *                      type that does not allow children of the type of the
     *                      newChild node, or if the node to append is one of this
     *                      node's ancestors or this node itself.
     * @throws XMLException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly or if the previous parent of the node being
     *                      inserted is readonly.
     */
    public XMLNode addChildNode(String name, String value, short type, int index) throws XMLException {
        //TODO: error checking
        //TODO: update the XMLDocument text
        //TODO: create node and add it to m_children.
        //TODO: notify listeners that the document has changed.
        return null;
    }//}}}
    
    //{{{ addChildNode()
    /**
     * Adds an already existing XMLNode to this node as a child. The node
     * is added after all child nodes that this node contains.
     * @param node the node to be added.
     * @return a reference to the node that was added.
     * @throws XMLException HIERARCHY_REQUEST_ERR: Raised if this node is of a
     *                      type that does not allow children of the type of the
     *                      newChild node, or if the node to append is one of this
     *                      node's ancestors or this node itself.
     * @throws XMLException WRONG_DOCUMENT_ERR: Raised if newChild was created
     *                      from a different document than the one that created
     *                      this node.
     * @throws XMLException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly or if the previous parent of the node being
     *                      inserted is readonly.
     */
    public XMLNode addChildNode(XMLNode node) throws XMLException {
        //TODO: error checking
        //TODO: updated XMLDocument text
        //TODO: update listeners that the doc has changed
        return addChildNodeAt(node, getChildCount());
    }//}}}
    
    //{{{ addChildNodeAt()
    /**
     * Adds an already existing XMLNode to this node at a specified
     * location. The index is zero indexed so it can be any number greater
     * than or equal to zero and less than or equal to the number of children
     * contained currently. Using a location that is one index greater than
     * the last child's index <code>(index == getChildCount())</code> then the
     * node is added at the end.
     * @param node the node to add to this parent node.
     * @param index the index to add it at.
     * @return the node added.
     * @throws XMLException HIERARCHY_REQUEST_ERR: Raised if this node is of a
     *                      type that does not allow children of the type of the
     *                      newChild node, or if the node to append is one of this
     *                      node's ancestors or this node itself.
     * @throws XMLException WRONG_DOCUMENT_ERR: Raised if newChild was created
     *                      from a different document than the one that created
     *                      this node.
     * @throws XMLException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly or if the previous parent of the node being
     *                      inserted is readonly.
     */
    public XMLNode addChildNodeAt(XMLNode node, int index) throws XMLException {
        //TODO: error checking
        //TODO: updated XMLDocument text
        //TODO: update listeners that the doc has changed
        if (index >= 0 && index < getChildCount()) {
            m_children.add(index, node);
        } else {
             if (index == getChildCount()) { 
                 m_children.add(node);
             } else {
                 throw new XMLException(XMLException.INDEX_SIZE_ERR, Messages.getMessage("Index.Out.Of.Bounds.Error"));
             }
        }
        return node;
    }//}}}
    
    //{{{ remove()
    /**
     * <p>Removes a child from this node.</p>
     * @param child the child node to remove from this node
     * @throws XMLException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly.
     * @throws XMLException NOT_FOUND_ERR: Raised if oldChild is not
     *                      a child of this node.
     */
    public void remove(XMLNode child) throws XMLException {
        //TODO: error checking
        //TODO: updated XMLDocument text
        //TODO: update listeners that the doc has changed
        m_children.remove(child);
    }//}}}
    
    //{{{ getNodeName()
    /**
     * Gets the full qualified name for this node including the local name
     * and namespace prefix.
     * @return the full qualified name of this node
     */
    public String getNodeName() {
        return getNSPrefix()+":"+getLocalName();
    }//}}}
    
    //{{{ getLocalName()
    /**
     * <p>Gets the local name of this node.</p>
     * @return the local name of the node
     */
    public String getLocalName() {
        return m_localName;
    }//}}} 
    
    //{{{ setLocalName()
    /**
     * <p>Sets the local name of the node.</p>
     * @param newValue the new local name for this node
     * @throws XMLException INVALID_CHARACTER_ERR: Raised if the specified name
     *                      contains an illegal character.
     */
    public void setLocalName(String localName) throws XMLException {
        //checking for illegal characters etc will be done by subclasses.
        //TODO: Notify listeners that the local name has changed
        //TODO: Update XMLDocument text with the change.
        m_localName = localName;
    }//}}}
    
    //{{{ getNSPrefix()
    /**
     * Gets the namespace prefix for this node. If this node is not a member
     * of a namespace then this method returns null.
     * @return the namespace prefix for this node. null if no namespace
     */
    public String getNSPrefix() {
        return m_namespacePrefix;
    }//}}}
    
    //{{{ setNSPrefix()
    /**
     * Sets the namespace prefix for this node. To remove this node from a
     * namespace this method should be passed null.
     * @param prefix The new prefix for this node
     * @throws XMLException INVALID_CHARACTER_ERR: Raised if the specified
     *                      prefix contains an illegal character, per the 
     *                      XML 1.0 specification .
     * @throws XMLException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is
     *                      readonly. 
     * @throws XMLException NAMESPACE_ERR: Raised if the specified prefix is
     *                      malformed per the Namespaces in XML specification,
     *                      if the namespaceURI of this node is null, if the
     *                      specified prefix is "xml" and the namespaceURI of
     *                      this node is different from
     *                      "http://www.w3.org/XML/1998/namespace", if this node
     *                      is an attribute and the specified prefix is "xmlns"
     *                      and the namespaceURI of this node is different from
     *                      " http://www.w3.org/2000/xmlns/", or if this node is
     *                      an attribute and the qualifiedName of this node is
     *                      "xmlns" .
     */
    public void setNSPrefix(String prefix) throws XMLException {
        //TODO: Error checking
        //TODO: Notify listeners that the namespaces has changed
        //TODO: Updated XMLDocument text with the change
        m_namespacePrefix = prefix;
    }//}}}
    
    //{{{ getProperty()
    /**
     * Gets a property for the key given.
     * @param key the key to the properties list
     * @return the value of the property for the given key.
     */
    public String getProperty(String key) {
        return m_properties.getProperty(key);
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
        return m_properties.getProperty(key, defaultValue);
    }//}}}
    
    //{{{ setProperty()
    /**
     * Sets a property of the AdapterNode
     * @param key the key to the property
     * @param value the value of the property
     * @return the old value of the property
     */
    public String setProperty(String key, String value) {
        Object oldValue = m_properties.setProperty(key, value);
        if (oldValue != null) {
            return oldValue.toString();
        } else {
            return null;
        }
        //TODO: notify listeners that the properties have changed?
    }//}}}
    
    //{{{ Private members
    /**
     * The node type
     */
    private int m_nodeType;
    /**
     * The local name property
     */
    private String m_localName;
    /**
     * The namespace prefix property
     */
    private String m_namespacePrefix;
    
    /**
     * The properties of this node. Some may be defined by plugins.
     */
    private Properties m_properties;
    /**
     * The document that owns this node.
     */
    private XMLDocument m_rootDocument;
    /**
     * The offset in the root document where this node starts
     */
    private int m_startOffset;
    /**
     * The offset in the root document where this node ends.
     */
    private int m_endOffset;
    /**
     * A list of listeners to be notified when the document changes.
     */
    private ArrayList m_listeners = new ArrayList();
    /**
     * A list of child nodes of this node.
     */
    private ArrayList m_children = new ArrayList();
    //}}}
}
