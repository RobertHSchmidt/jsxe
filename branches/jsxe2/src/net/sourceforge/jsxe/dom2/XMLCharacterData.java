/*
XMLCharacterData.java
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

import net.sourceforge.jsxe.gui.Messages;

import org.w3c.dom.*;

public abstract class XMLCharacterData extends XMLNode {
    
    //{{{ XMLCharacterData constructor
    XMLCharacterData(CharacterData data) {
        super(data);
    }//}}}
    
    //{{{ appendData()
    public void append(String data) throws DOMException {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        ((CharacterData)getNode()).appendData(data);
    }//}}}
    
    //{{{ deleteData()
    public void deleteData(int offset, int len) throws DOMException {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        ((CharacterData)getNode()).deleteData(offset, len);
    }//}}}
    
    //{{{ insertData()
    public void insertData(int offset, String data) {
        if (((XMLDocument)getDocument()).isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, Messages.getMessage("XML.Read.Only.Node"));
        }
        ((CharacterData)getNode()).insertData(offset, data);
    }//}}}
    
    //{{{ getLength()
    public int getLength() {
        return ((CharacterData)getNode()).getLength();
    }//}}}
    
}