/*
XMLDocumentEvent.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.e

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

package net.sourceforge.jsxe.dom2.event;

import net.sourceforge.jsxe.dom2.*;
import java.util.EventObject;

/**
 * XMLDocumentEvents are modifications to the structure of the XMLDocument.
 * As the XMLDocument represents structured text the XMLDocumentEvent represents
 * a structured change to the XMLDocument.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see XMLDocument
 * @since jsXe 0.5 pre3
 */
public class XMLDocumentEvent extends EventObject {
    
    private int m_offset;
    private int m_length;
    private short m_type;
    
    public static short INSERT = 0;
    public static short REMOVE = 1;
    
    //{{{ XMLDocumentEvent constructor
    /**
     * Creates a new document event signifying an edit to the document.
     * 
     */
    public XMLDocumentEvent(XMLDocument doc, int offset, String text, short type) {
        super(doc);
        m_type = type;
        m_length = text.length();
        m_offset = offset;
    }//}}}
    
    //{{{ getDocument()
    /**
     * Gets the document that was updated.
     */
    public XMLDocument getDocument() {
        return (XMLDocument)getSource();
    }//}}}
    
    //{{{ getOffset()
    /**
     * Gets the offset into the document where the update occurred.
     */
    public int getOffset() {
        return m_offset;
    }//}}}
    
    //{{{ getLength()
    /**
     * Gets the length of the update.
     */
    public int getLength() {
        return m_length;
    }//}}}
    
    //{{{ getType()
    /**
     * Gets the type of edit done to the document.
     */
    public short getType() {
        return m_type;
    }//}}}
    
}