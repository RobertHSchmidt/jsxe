/*
XMLDocumentType.java
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

//{{{ Swing classes
import javax.swing.text.*;
//}}}

//{{{ DOM classes
import org.w3c.dom.*;
import org.w3c.dom.events.*;
//}}}

//}}}

/**
 * XMLDocumentType represents a Document Type node in an XML Document.
 * It is generated from the DTD and contains element, attribute and entity
 * delarations..
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see XMLDocument
 */
public class XMLDocumentType extends XMLNode {
    
    //{{{ XMLDocumentType constructor
    /**
     * Creates a new Document Type node.
     * @param docType the DocumentType node that this node wraps
     */
    XMLDocumentType(DocumentType docType) {
        super(docType);
    }//}}}
    
    //{{{ getPublicId()
    /**
     * The system identifier of the external subset.
     * This may be an absolute URI or not.
     */
    public String getPublicId() {
        return ((DocumentType)getNode()).getPublicId();
    }//}}}
    
    //{{{ getPublicId()
    /**
     * The public identifier of the external subset.
     */
    public String getSystemId() {
        return ((DocumentType)getNode()).getSystemId();
    }//}}}
    
}
