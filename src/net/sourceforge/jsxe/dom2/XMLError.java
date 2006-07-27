/*
XMLError.java
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

public class XMLError extends XMLNode {
    
    private XMLDocument m_document;
    private String m_error;
    private XMLNode m_parent;
    private boolean m_warning;
    
    //{{{ XMLError constructor
    XMLError(XMLDocument document, XMLNode parent, String message, boolean warning) {
        super(null);
        m_document = document;
        m_parent = parent;
        m_error = message;
        m_warning = warning;
    }//}}}
    
    //{{{ getDocument()
    public javax.swing.text.Document getDocument() {
        return m_document;
    }//}}}
    
    //{{{ getElement()
    public javax.swing.text.Element getElement(int index) {
        return null;
    }//}}}
    
    //{{{ getElementCount()
    public int getElementCount() {
        return 0;
    }//}}}
    
    //{{{ getName()
    public String getName() {
        //TODO
        if (m_warning) {
            return Messages.getMessage("common.warning");
        } else {
            return Messages.getMessage("common.error");
        }
    }//}}}
    
    //{{{ getParentElement()
    public javax.swing.text.Element getParentElement() {
        return m_parent;
    }//}}} 
    
    //{{{ isLeaf()
    public boolean isLeaf() {
        return true;
    }//}}}
    
    //{{{ addEventListener()
    public void addEventListener(java.lang.String type, EventListener listener, boolean useCapture) {}//}}}
    
    //{{{ removeEventListener()
    public void removeEventListener(java.lang.String type, EventListener listener, boolean useCapture) {}//}}}
    
    //{{{ appendNode()
    public XMLNode appendNode(XMLNode newChild) throws DOMException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
    }//}}}
    
    //{{{ getBaseURI()
    public String getBaseURI() {
        return m_parent.getBaseURI();
    }//}}}
    
    //{{{ getNamespaceURI()
    public String getNamespaceURI() {
        return m_parent.getNamespaceURI();
    }//}}}
    
    //{{{ getNodeType()
    public short getNodeType() {
        return ERROR_NODE;
    }//}}}
    
    //{{{ getValue()
    public String getValue() {
        //TODO
        return null;
    }//}}}
    
    //{{{ getNSPrefix()
    public String getNSPrefix() {
        return null;
    }//}}}
    
    //{{{ insertNode()
    
    public XMLNode insertNode(XMLNode node, int index) throws DOMException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
    }//}}}
    
    //{{{ lookupNamespaceURI()
    public String lookupNamespaceURI(String prefix) {
        return m_parent.lookupNamespaceURI(prefix);
    }//}}}
    
    //{{{ removeNode()
    public XMLNode removeNode(XMLNode child) throws DOMException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
    }//}}}
    
    //{{{ setValue()
    public void setValue(String value) throws DOMException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
    }//}}}
    
    //{{{ setNSPrefix()
    public void setNSPrefix(String prefix) throws DOMException {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
    }//}}}
    
    //{{{ setName()
    public void setName(String name) {
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
    }//}}}
    
    //{{{ isWarning()
    /**
     * Returns whether this node represents a warning message
     * or an Error message.
     * @return true if a warning message, false if an error message
     */
    public boolean isWarning() {
        return m_warning;
    }//}}}
    
    //{{{ setParent()
    protected void setParent(XMLNode node) {
        m_parent = node;
    }//}}}
}