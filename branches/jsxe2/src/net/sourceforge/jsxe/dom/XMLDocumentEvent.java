/*
XMLDocumentEvent.java
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

/**
 * An XMLDocumentEvent represents a change to an XMLDocument. It can occur
 * whenever any change is made to the document whether through normal text
 * insertion or removal, or by changing values or structure which maps to text
 * within the XMLDocument. All types of events occurring on an XMLDocument will
 * extend this class.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see XMLDocument
 * @since jsXe 0.5 pre1
 */
public abstract class XMLDocumentEvent {

    //{{{ XMLDocumentEvent constructor
    /**
     * Creates a new event signifying text being updated in the document
     * @param source the source document
     * @param offset the offset into the document where the insert occurred
     * @param text the text that was updated in the modification.
     */
    public XMLDocumentEvent(XMLDocument source, int offset, String text) {
        m_document = source;
        m_offset = offset;
        m_text = text;
    }//}}}
    
    //{{{ getXMLDocument()
    /**
     * Gets the document that is the source of this change event.
     */
    public XMLDocument getXMLDocument() {
        return m_document;
    }//}}}
    
    //{{{ getLength()
    /**
     * Gets the length of the change to the document
     */
    public int getLength() {
        return m_text.length();
    }//}}}
    
    //{{{ getOffset()
    /**
     * Gets the offset into the document where the change occurred
     */
    public int getOffset() {
        return m_offset;
    }//}}}
    
    //{{{ getText()
    /**
     * Gets the text associated with this event.
     */
    public String getText() {
        return m_text;
    }//}}}
    
    //{{{ Private Members
    private XMLDocument m_document;
    private String m_text;
    private int m_offset;
    //}}}
}
