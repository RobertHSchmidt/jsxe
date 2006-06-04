/*
XMLException.java
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
 * XMLExceptions represent any type of exception that may occur when editing
 * an XML document. This can include when the document would cause the
 * structure to be non-well-formed though a method call. Operations on
 * the structure of the XMLDocument should always result in a well-formed
 * document.
 * 
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see XMLDocument
 * @see XMLNode
 * @since jsXe 0.5 pre1
 */
public class XMLException extends RuntimeException {
    
    //{{{ Exception types
    /**
     * Indicates a Node is inserted somewhere it doesn't belong.
     */
    public static final short HIERARCHY_REQUEST_ERR = 1;
    
    /**
     * If index or size is negative, or greater than the allowed value.
     */
    public static final short INDEX_SIZE_ERR = 2;
    /**
     * If an invalid or illegal character is specified, such as in an XML name.
     */
    public static final short INVALID_CHARACTER_ERR = 3;
    /**
     * If an attempt is made to modify the type of the underlying object.
     */
    public static final short INVALID_MODIFICATION_ERR = 4;
    /**
     * If an attempt is made to use an object that is not, or is no longer, usable.
     */
    public static final short INVALID_STATE_ERR = 5;
    /**
     * If an attempt is made to create or change an object in a way which is incorrect with regard to namespaces.
     */
    public static final short NAMESPACE_ERR = 6;
    /**
     * If data is specified for a Node which does not support data.
     */
    public static final short NO_DATA_ALLOWED_ERR = 7;
    /**
     * If an attempt is made to modify an object where modifications are not allowed.
     */
    public static final short NO_MODIFICATION_ALLOWED_ERR = 8;
    /**
     * If an attempt is made to reference a Node in a context where it does not exist.
     */
    //public static final short NOT_FOUND_ERR = 9;
    /**
     * If an invalid or illegal string is specified.
     */ 
    //public static final short SYNTAX_ERR = 10;
    /**
     * If the type of an object is incompatible with the expected type of the parameter associated to the object.
     */
    public static final short TYPE_MISMATCH_ERR = 11;
    /**
     * If a call to a method such as insertBefore or removeChild would make the 
     * Node invalid with respect to "partial validity", this exception would be
     * raised and the operation would not be done.
     */
    //public static final short VALIDATION_ERR = 12;
    /**
     * If a Node is used in a different document than the one that created it (that doesn't support it).
     */
    public static final short WRONG_DOCUMENT_ERR = 13;
    //}}}
    
    //{{{ XMLException constructor
    /**
     * Creates a new XMLException with the given message.
     * @param type The exception type
     * @param message The message
     */
    public XMLException(short type, String message) {
        super(message);
        m_type = type;
    }//}}}
    
    //{{{ getType()
    /**
     * Returts the type of the exception
     */
    public short getType() {
        return m_type;
    }//}}}
    
    //{{{ Private Members
    private short m_type;
    //}}}
}
