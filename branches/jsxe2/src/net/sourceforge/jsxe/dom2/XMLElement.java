/*
XMLElement.java
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

//{{{ Imports

//{{{ jsXe classes
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.util.MiscUtilities;
//}}}

//{{{ Swing classes
import javax.swing.text.*;
//}}}

//{{{ DOM classes
import org.w3c.dom.*;
import org.w3c.dom.events.*;
//}}}

//{{{ Java classes
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//}}}

//}}}

/**
 * The XMLElement class represents an element in the XMLDocument tree. It allows
 * attibute modifications which will translate to text modifications in the
 * XMLDocument.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see XMLDocument
 * @since jsXe 0.5 pre3
 */
public class XMLElement extends XMLNode {
    
    //{{{ XMLElement constructor
    /**
     * Creates a new XMLElement
     * @param element the element node that this node wraps.
     */
     XMLElement(org.w3c.dom.Element element) {
        super(element);
    }//}}}
    
    //{{{ getAttributes()
    /**
     * Returns a read only set of attributes in this node. The Map
     * maps the attribute name to the attribute's value.
     */
    public Map getAttributes() {
        //TODO: create resolving parent for the attributes for default node
        //      values from a DTD or schema?
        
        HashMap attrSet = new HashMap();
        NamedNodeMap attributes = getNode().getAttributes();
        
        int len = attributes.getLength();
        for (int i=0; i<len; i++) {
            Node attr = attributes.item(i);
            XMLAttribute xmlAttr = (XMLAttribute)attr.getUserData(USER_DATA_KEY);
            attrSet.put(xmlAttr.getName(), xmlAttr.getValue());
        }
        return attrSet;
    }//}}}
    
    //{{{ getAttributeNodes()
    /**
     * Returns a read only set of attributes in this node. This List
     * contains all the XMLAttributeNode objects for this element.
     */
    public List getAttributeNodes() {
        ArrayList attrSet = new ArrayList();
        
        NamedNodeMap attributes = getNode().getAttributes();
        
        int len = attributes.getLength();
        for (int i=0; i<len; i++) {
            Node attr = attributes.item(i);
            XMLAttribute xmlAttr = (XMLAttribute)attr.getUserData(USER_DATA_KEY);
            attrSet.add(xmlAttr);
        }
        
        return attrSet;
    }//}}}
    
    //{{{ getAttribute()
    /**
     * @param name the qualified name of the attribute
     */
    public XMLAttribute getAttribute(String name) {
        org.w3c.dom.Element element = (org.w3c.dom.Element)getNode();
        String localName = MiscUtilities.getLocalNameFromQualifiedName(name);
        String prefix    = MiscUtilities.getNSPrefixFromQualifiedName(name);
        
        if (prefix != null && !prefix.equals("")) {
            Attr attr = element.getAttributeNodeNS(lookupNamespaceURI(prefix),localName);
            return (XMLAttribute)attr.getUserData(USER_DATA_KEY);
        } else {
            Attr attr = element.getAttributeNode(name);
            return (XMLAttribute)attr.getUserData(USER_DATA_KEY);
        }
    }//}}}
    
    //{{{ setAttribute()
    /**
     * @param name the qualified name of the attribute
     * @param value the value of the attribute
     */
    public XMLAttribute setAttribute(String name, String value) throws DOMException {
        org.w3c.dom.Element element = (org.w3c.dom.Element)getNode();
        String prefix    = MiscUtilities.getNSPrefixFromQualifiedName(name);
        Attr newAttr;
        
        //check if we are setting a namespace declaration
        if ("xmlns".equals(prefix)) {
            //if so then make sure the value is valid
            if (value != null && value.equals("")) {
                //TODO: fix messages
                throw new DOMException(DOMException.NAMESPACE_ERR, "An attempt was made to create an empty namespace declaration");
            }
        }
        
        if (prefix != null && !prefix.equals("")) {
            String uri = lookupNamespaceURI(prefix);
            
            element.setAttributeNS(uri,name,value);
            newAttr = element.getAttributeNodeNS(uri, name);
        } else {
            /*
            setAttribute doesn't throw an error if the first character is
            a ":"
            */
            if (name != null && !name.equals("") && name.charAt(0)==':') {
                //TODO: fix messages
                throw new DOMException(DOMException.NAMESPACE_ERR, "An attribute name cannot have a ':' as the first character");
            }
            element.setAttribute(name, value);
            newAttr = element.getAttributeNode(name);
        }
        
        //create an XMLAttribute node for the new attribute
        if (newAttr != null) {
            XMLAttribute xmlAttr = new XMLAttribute(newAttr);
            newAttr.setUserData(USER_DATA_KEY, xmlAttr, null);
            return xmlAttr;
        }
        return null;
    }//}}}
    
    //{{{ setAttribute()
    
    public XMLAttribute setAttribute(XMLAttribute attr) throws DOMException {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        /*
        TODO: test how this works. Will I need to check for a namespace and
        call setAttributeNode() instead if there is none?
        */
        ((org.w3c.dom.Element)getNode()).setAttributeNodeNS((Attr)attr.getNode());
        return attr;
    }//}}}
    
    //{{{ removeAttributeAt()
    /**
     * @param index the index at which to remove an attribute.
     */
    public XMLAttribute removeAttributeAt(int index) throws DOMException {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        
        org.w3c.dom.Element element = (org.w3c.dom.Element)getNode();
        NamedNodeMap attrs = element.getAttributes();
        if (attrs != null) {
            return removeAttribute(attrs.item(index).getNodeName());
        }
        return null;
    }//}}}
    
    //{{{ removeAttribute()
    /**
     * @param name The qualified name of the attribute.
     */
    public XMLAttribute removeAttribute(String name) throws DOMException {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        
        org.w3c.dom.Element element = (org.w3c.dom.Element)getNode();
        
        String prefix = MiscUtilities.getNSPrefixFromQualifiedName(name);
        String localName = MiscUtilities.getLocalNameFromQualifiedName(name);
        
        //Check if we are removing a namespace declaration
        //This is a somewhat expensive operation, may need to
        //optimize somehow in the future
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
                        //TODO: fix messages
                        throw new DOMException(DOMException.NAMESPACE_ERR, "An attempt was made to remove a namespace declaration when nodes exist that use it");
                    }
                    //now check the attributes
                    NamedNodeMap attrs = ele.getAttributes();
                    for (int j=0; j<attrs.getLength(); j++) {
                        Node foundAttr = attrs.item(i);
                        if (uri.equals(foundAttr.getNamespaceURI())) {
                            //TODO: fix messages
                            throw new DOMException(DOMException.NAMESPACE_ERR, "An attempt was made to remove a namespace declaration when nodes exist that use it");
                        }
                    }
                }
            } else {
                throw new DOMException(DOMException.NAMESPACE_ERR, "An attempt was made to remove a namespace declaration when nodes exist that use it");
            }
        }
        
        XMLAttribute attr;
        
        if (prefix != null && !prefix.equals("")) {
            String uri = lookupNamespaceURI(prefix);
            attr = (XMLAttribute)element.getAttributeNodeNS(uri, localName).getUserData(USER_DATA_KEY);
            element.removeAttributeNS(uri,localName);
        } else {
            attr = (XMLAttribute)element.getAttributeNode(localName).getUserData(USER_DATA_KEY);
            element.removeAttribute(localName);
        }
        
        return attr;
    }//}}}
    
    //{{{ getNodeType()
    public short getNodeType() {
        return Node.ELEMENT_NODE;
    }//}}}
    
}
