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
import org.w3c.dom.*;
import org.w3c.dom.events.*;
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
     * @param element the element node that this node wraps.
     */
     XMLElement(Element element) {
        super(element);
    }//}}}
    
    //{{{ getNodeType()
    public int getNodeType() {
        return Node.ELEMENT_NODE;
    }//}}}
    
}
