/*
DOMOutput.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains a simple implementation of the LSOutput interface.

This file written by Ian Lewis (IanLewis@member.fsf.org)
Copyright (C) 2002 Ian Lewis

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

//{{{ DOM classes
import org.w3c.dom.ls.LSOutput;
//}}}

//{{{ Java classes
import java.io.OutputStream;
import java.io.Writer;
//}}}

//}}}

public class DOMOutput implements LSOutput {
    
    public DOMOutput(OutputStream byteStream, String encoding) {//{{{
        m_byteStream = byteStream;
        m_encoding = encoding;
    }//}}}
    
    public DOMOutput(String systemId, String encoding) {//{{{
        m_systemId = systemId;
        m_encoding = encoding;
    }//}}}
    
    public DOMOutput(Writer characterStream) {//{{{
        m_characterStream = characterStream;
    }//}}}
    
    public OutputStream getByteStream() {//{{{
        return m_byteStream;
    }//}}}
    
    public Writer getCharacterStream() {//{{{
        return m_characterStream;
    }//}}}
    
    public String getEncoding() {//{{{
        return m_encoding;
    }//}}}
    
    public String getSystemId() {//{{{
        return m_systemId;
    }//}}}
    
    public void setByteStream(OutputStream byteStream) {//{{{
        m_byteStream = byteStream;
    }//}}}
    
    public void setCharacterStream(Writer characterStream) {//{{{
        m_characterStream = characterStream;
    }//}}}
    
    public void setEncoding(String encoding) {//{{{
        m_encoding = encoding;
    }//}}}
    
    public void setSystemId(String systemId) {//{{{
        m_systemId = systemId;
    }//}}}
    
    private OutputStream m_byteStream;
    private Writer m_characterStream;
    private String m_systemId;
    private String m_encoding;
}