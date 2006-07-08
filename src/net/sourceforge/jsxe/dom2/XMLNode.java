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

//}}}

/**
 * <p>The XMLNode class is meant to represent an XMLNode in a XMLDocument tree
 *    but also as a section of text within that document tree. It
 *    allows tree modifications only, however these modifications
 *    will correspond to text modifications to the XMLDocument. Direct text
 *    modifications are only allowed at the XMLDocument level.</p>
 *
 * <p>It implements a subset of the support offered by standard w3c DOM
 *    interfaces.</p>
 * 
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see XMLDocument
 * @since jsXe 0.5 pre2
 */
public abstract class XMLNode /* implements javax.swing.text.Element */ {
    
    protected static final String USER_DATA_KEY = "net.sourceforge.jsxe.dom.XMLNode";
    
    //{{{ Private members
    
    /**
     * The w3c Node that this node wraps. It must support the EventTarget
     * interface in org.w3c.dom.events
     */
    private Node m_domNode;
    
    //}}}
    
    //{{{ XMLNode constructor
    XMLNode(Node node) {
        m_domNode = node;
        //TODO: add UserDataHandler
        m_domNode.setUserData(USER_DATA_KEY, this, null);
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
        return (javax.swing.text.Element)m_domNode.getChildNodes().item(index).getUserData(USER_DATA_KEY);
    }//}}}
    
    //{{{ getElementCount()
    
    public int getElementCount() {
        return m_domNode.getChildNodes().getLength();
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
        
        m_domNode.appendChild(newChild.getNode());
        
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
        m_domNode.insertBefore(node.getNode(), m_domNode.getChildNodes().item(index));
        return node;
    }//}}}
    
    //{{{ lookupNamespaceURI()
    
    public String lookupNamespaceURI(String prefix) {
        return m_domNode.lookupNamespaceURI(prefix);
    }//}}}
    
    //{{{ removeNode()
    
    public XMLNode removeNode(XMLNode child) throws DOMException {
        Node childNode = child.getNode();
        m_domNode.removeChild(childNode);
        childNode.setUserData(USER_DATA_KEY, null, null);
        return child;
    }//}}}
    
    //{{{ setValue()
    
    public void setValue(String value) throws DOMException {
        m_domNode.setNodeValue(value);
    }//}}}
    
    //{{{ setNSPrefix()
    
    public void setNSPrefix(String prefix) throws DOMException {
        m_domNode.setPrefix(prefix);
    }//}}}
    
    //{{{ setName()
    /**
     * @param name the qualified name of the node.
     */
    public void setName(String name) {
        m_domNode = m_domNode.getOwnerDocument().renameNode(m_domNode, m_domNode.getNamespaceURI(), name);
    }//}}}
    
    //{{{ Protected Members
    
    //{{{ getNode()
    protected Node getNode() {
        return m_domNode;
    }//}}}
    
    //}}}
}
