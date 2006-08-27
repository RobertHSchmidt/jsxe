/*
PropertyChangedEvent.java
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
 * PropertyChangedEvents occur when a property in the XMLDocument is changed.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see XMLDocument
 * @since jsXe XX.XX
 */
public class PropertyChangeEvent extends EventObject {
    
    private XMLDocument m_document;
    private String m_oldValue;
    private String m_newValue;
    private String m_key;
    
    //{{{ PropertyChangeEvent constructor
    /**
     * Creates a new PropertyChangeEvent.
     */
    public PropertyChangeEvent(XMLDocument doc, String name, String oldValue, String newValue) {
        super(doc);
        m_key = name;
        m_oldValue = oldValue;
        m_newValue = newValue;
    }//}}}
    
    //{{{ getDocument()
    /**
     * Gets the document that was updated.
     */
    public XMLDocument getDocument() {
        return (XMLDocument)getSource();
    }//}}}
    
    //{{{ getOldValue()
    /**
     * Gets the old value of the property.
     */
    public String getOldValue() {
        return m_oldValue;
    }//}}}
    
    //{{{ getNewValue()
    /**
     * Gets the new value of the property.
     */
    public String getNewValue() {
        return m_newValue;
    }//}}}
    
    //{{{ getName()
    /**
     * Gets the name of the property that was changed.
     */
    public String getName() {
        return m_key;
    }//}}}
    
}