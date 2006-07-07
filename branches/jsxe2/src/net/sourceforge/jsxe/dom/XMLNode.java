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
import net.sourceforge.jsxe.dom.completion.*;
//}}}

//{{{ DOM classes
import org.w3c.dom.*;
import org.w3c.dom.events.*;
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
public abstract class XMLNode {
    
    private static final String USER_DATA_KEY = "net.sourceforge.jsxe.dom.XMLNode";
    
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
    }//}}}
    
    //{{{ addEventListener()
    public void addEventListener(java.lang.String type, EventListener listener, boolean useCapture) {
        ((EventTarget)m_domNode).addEventListener(type, listener, useCapture);
    }//}}}
    
    //{{{ removeEventListener()
    public void removeEventListener(java.lang.String type, EventListener listener, boolean useCapture) {
        ((EventTarget)m_domNode).removeEventListener(type, listener, useCapture);
    }//}}}
    
    //{{{ appendNode()
    public XMLNode appendChild(XMLNode newChild) {
        
        m_domNode.appendChild(newChild.getNode());
        
        return newChild;
    }//}}}
    
    //{{{ Protected Members
    
    //{{{ getNode()
    protected Node getNode() {
        return m_domNode;
    }//}}}
    
    //}}}
}
