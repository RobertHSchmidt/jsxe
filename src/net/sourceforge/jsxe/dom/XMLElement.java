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

package net.sourceforge.jsxe.dom;

//{{{ Imports
import java.util.HashMap;
//}}}

/**
 * The XMLElement class represents an element in the XMLDocument tree. It allows
 * attibute modifications which will translate to text modifications in the
 * XMLDocument.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see XMLDocument
 * @since jsXe 0.5 pre2
 */
public class XMLElement extends XMLNode {
    
    //{{{ XMLElement constructor
    /**
     * Creates a new XMLElement
     * @param document the document that owns this element
     * @param localName the local name of the element.
     */
    XMLElement(XMLDocument document, String localName) throws XMLException {
        super(document);
        setLocalName(localName);
        m_attributes = new HashMap();
    }//}}}
    
    //{{{ XMLElement constructor
    /**
     * Creates a new XMLElement
     * @param document the document that owns this element
     * @param prefix the namespace prefix for this element
     * @param localName the local name of the element.
     */
    XMLElement(XMLDocument document, String prefix, String localName) throws XMLException {
        super(document);
        setNSPrefix(prefix);
        setLocalName(localName);
        m_attributes = new HashMap();
    }//}}}
    
    //{{{ getAttributes()
    /**
     * Returns a HashMap of attribute names to XMLAttribute objects.
     */
    public HashMap getAttributes() {
        return m_attributes;
    }//}}}
    
    //{{{ getNodeType()
    public int getNodeType() {
        return ELEMENT_NODE;
    }//}}}
    
    //{{{ setLocalName()
    public void setLocalName(String localName) throws XMLException {
        //TODO: error checking for invalid name
        super.setLocalName(localName);
    }//}}}
    
    //{{{ Private Members
    private HashMap m_attributes;
    //}}}
}
