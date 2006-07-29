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

package net.sourceforge.jsxe.dom2;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.dom.completion.*;
//}}}

//{{{ DOM classes
import org.w3c.dom.*;
import org.w3c.dom.events.*;
//}}}

//{{{ Swing classes
import javax.swing.text.*;
//}}}

import java.util.ArrayList;

//}}}

/**
 * <p>The XMLNode class is meant to represent an XMLNode in a XMLDocument tree
 *    but also as a section of text within that document tree. It
 *    allows tree modifications only, however these modifications
 *    will correspond to text modifications to the XMLDocument. Direct text
 *    modifications are only allowed at the XMLDocument level.</p>
 *
 * <p>It is implement in the spirit of the {@link javax.swing.text.Element}
 *    class as it represents a piece of text as well as a structural element
 *    in the document.</p>
 *
 * <p>It implements a subset of the support offered by standard w3c DOM
 *    interfaces.</p>
 * 
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see XMLDocument
 * @since jsXe 0.5 pre3
 */
public abstract class XMLNode {
    
    protected static final String USER_DATA_KEY = "net.sourceforge.jsxe.dom.XMLNode";
    
    /**
     * The node is an Attr.
     */
    public static short ATTRIBUTE_NODE = Node.ATTRIBUTE_NODE;
    /**
     * The node is a CDATASection.
     */
    public static short CDATA_SECTION_NODE = Node.CDATA_SECTION_NODE;
    /**
     * The node is a Comment.
     */
    public static short COMMENT_NODE = Node.COMMENT_NODE;
    /**
     * The node is a DocumentType.
     */
    public static short DOCUMENT_TYPE_NODE = Node.DOCUMENT_TYPE_NODE;
    /**
     * The node is an Element.
     */
    public static short ELEMENT_NODE = Node.ELEMENT_NODE;
    /**
     * The node is an Entity.
     */
    public static short ENTITY_NODE = Node.ENTITY_NODE;
    /**
     * The node is an EntityReference.
     */
    public static short ENTITY_REFERENCE_NODE = Node.ENTITY_REFERENCE_NODE;
    /**
     * The node is a Notation.
     */
    public static short NOTATION_NODE = Node.NOTATION_NODE;
    /**
     * The node is a ProcessingInstruction.
     */
    public static short PROCESSING_INSTRUCTION_NODE = Node.PROCESSING_INSTRUCTION_NODE;
    /**
     * The node is a Text node.
     */
    public static short TEXT_NODE = Node.TEXT_NODE;
    /**
     * The node is an Error node.
     */
    public static short ERROR_NODE = Short.MAX_VALUE;
    
    //{{{ Private members
    
    /**
     * The w3c Node that this node wraps. It must support the EventTarget
     * interface in org.w3c.dom.events
     */
    private Node m_domNode;
    
    /**
     * Used to keep track of children and XMLError nodes and keep them
     * in the correct order.
     */
    private ArrayList m_children = new ArrayList();
    
    //}}}
    
    //{{{ XMLNode constructor
    XMLNode(Node node) {
        m_domNode = node;
        //TODO: add UserDataHandler
        if (m_domNode != null) {
            m_domNode.setUserData(USER_DATA_KEY, this, null);
        }
    }//}}}
    
    //{{{ javax.swing.text.Element methods
    
    //{{{ getAttributes()
    /**
     * Attributes in an XMLNode are implemented as the attributes in a
     * XML Element node. All other node types will return null.
     * Attribute sets will never contain style data unless defined by a
     * DocumentView.
     * @return the attributes of the XMLElement node or null.
     */
    public AttributeSet getAttributes() {
        //no attributes by default. Subclasses may override this behavior
        return null;
    }//}}}
    
    //{{{ getDocument()
    public javax.swing.text.Document getDocument() {
        return (javax.swing.text.Document)m_domNode.getOwnerDocument().getUserData(USER_DATA_KEY);
    }//}}}
    
    //{{{ getElement()
    
    public javax.swing.text.Element getElement(int index) {
        return (javax.swing.text.Element)m_children.get(index);
    }//}}}
    
    //{{{ getElementCount()
    
    public int getElementCount() {
        return m_children.size();
    }//}}}
    
    //{{{ getElementIndex()
    
    public int getElementIndex(int offset) {
        //TODO: implement offsets
        return 0;
    }//}}}
    
    //{{{ getEndOffset()
    
    public int getEndOffset() {
        //TODO: implement offsets
        //TODO: should this be abstract?
        return 0;
    }//}}}
    
    //{{{ getName()
    /**
     * Gets the qualified name of the node. This will be the namespace
     * prefix + ":" + the locale name.
     * @return the qualified name
     */
    public String getName() {
        return m_domNode.getNodeName();
    }//}}}
    
    //{{{ getParentElement()
    
    public javax.swing.text.Element getParentElement() {
        return (javax.swing.text.Element)m_domNode.getParentNode().getUserData(USER_DATA_KEY);
    }//}}} 
    
    //{{{ getStartOffset() 
    
    public int getStartOffset() {
        //TODO: implement offsets
        //TODO: should this be abstract?
        return 0;
    }//}}}
    
    //{{{ isLeaf()
    
    public boolean isLeaf() {
        return m_domNode.hasChildNodes();
    }//}}}
    
    //}}}
    
    //{{{ addEventListener()
    public void addEventListener(java.lang.String type, EventListener listener, boolean useCapture) {
        ((EventTarget)m_domNode).addEventListener(type, listener, useCapture);
    }//}}}
    
    //{{{ removeEventListener()
    public void removeEventListener(java.lang.String type, EventListener listener, boolean useCapture) {
        ((EventTarget)m_domNode).removeEventListener(type, listener, useCapture);
    }//}}}
    
    //{{{ appendNode()
    public XMLNode appendNode(XMLNode newChild) throws DOMException {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        
        if (newChild != null) {
            if (!(newChild instanceof XMLError)) {
                XMLNode parent = (XMLNode)newChild.getParentElement();
                if (parent != null) {
                    parent.removeNode(newChild);
                }
                ((XMLError)newChild).setParent(this);
            }
            
            m_children.add(newChild);
            if (!(newChild instanceof XMLError)) {
                m_domNode.appendChild(newChild.getNode());
            }
        }
        
        return newChild;
    }//}}}
    
    //{{{ getBaseURI()
    
    public String getBaseURI() {
        return m_domNode.getBaseURI();
    }//}}}
    
    //{{{ getNamespaceURI()
    
    public String getNamespaceURI() {
        return m_domNode.getNamespaceURI();
    }//}}}
    
    //{{{ getNodeType()
    
    public short getNodeType() {
        return m_domNode.getNodeType();
    }//}}}
    
    //{{{ getValue()
    
    public String getValue() {
        return m_domNode.getNodeValue();
    }//}}}
    
    //{{{ getNSPrefix()
    
    public String getNSPrefix() {
        return m_domNode.getPrefix();
    }//}}}
    
    //{{{ insertNode()
    
    public XMLNode insertNode(XMLNode node, int index) throws DOMException {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        
        if (node != null) {
            if (!(node instanceof XMLError)) {
                XMLNode parent = (XMLNode)node.getParentElement();
                if (parent != null) {
                    parent.removeNode(node);
                }
                ((XMLError)node).setParent(this);
            }
            
            m_children.add(index, node);
            if (!(node instanceof XMLError)) {
                m_domNode.insertBefore(node.getNode(), m_domNode.getChildNodes().item(index));
            }
        }
        return node;
    }//}}}
    
    //{{{ lookupNamespaceURI()
    
    public String lookupNamespaceURI(String prefix) {
        return m_domNode.lookupNamespaceURI(prefix);
    }//}}}
    
    //{{{ removeNode()
    
    public XMLNode removeNode(XMLNode child) throws DOMException {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        
        if (child != null) {
            m_children.remove(child);
            if (!(child instanceof XMLError)) {
                Node childNode = child.getNode();
                m_domNode.removeChild(childNode);
                childNode.setUserData(USER_DATA_KEY, null, null);
            } else {
                ((XMLError)child).setParent(null);
            }
        }
        return child;
    }//}}}
    
    //{{{ setValue()
    
    public void setValue(String value) throws DOMException {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        
        m_domNode.setNodeValue(value);
    }//}}}
    
    //{{{ setNSPrefix()
    
    public void setNSPrefix(String prefix) throws DOMException {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        
        m_domNode.setPrefix(prefix);
    }//}}}
    
    //{{{ setName()
    /**
     * @param name the qualified name of the node.
     */
    public void setName(String name) {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        
        m_domNode = m_domNode.getOwnerDocument().renameNode(m_domNode, m_domNode.getNamespaceURI(), name);
    }//}}}
    
    //{{{ Protected Members
    
    //{{{ getNode()
    protected Node getNode() {
        return m_domNode;
    }//}}}
    
    //}}}
}
