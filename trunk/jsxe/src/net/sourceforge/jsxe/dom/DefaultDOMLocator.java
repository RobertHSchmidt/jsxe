/*
DefaultDOMLocator.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the source for a default DOMLocator that is used to
contain information about a location within an XML document.

This attempts to conform to the DOM3 implementation in Xerces. It conforms
to DOM3 as of Xerces 2.3.0

This file written by Ian Lewis (IanLewis@member.fsf.org)

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
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ DOM classes
import org.w3c.dom.Node;
import org.apache.xerces.dom3.DOMLocator;
//}}}

//}}}

public class DefaultDOMLocator implements DOMLocator {
    
    public DefaultDOMLocator() {//{{{
        column = -1;
        line   = -1;
        offset = -1;
    }//}}}
    
    public DefaultDOMLocator(Node node, int lineno, int col, int charOffset, String u) {//{{{
        line = lineno;
        column = col;
        errorNode = node;
        offset = charOffset;
        uri = u;
    }//}}}
    
    public int getColumnNumber() {//{{{
        return column;
    }//}}}
    
    public Node getErrorNode() {//{{{
        return errorNode;
    }//}}}
    
    public int getLineNumber() {//{{{
        return line;
    }//}}}
    
    public int getOffset() {//{{{
        return offset;
    }//}}}
    
    public String getUri() {//{{{
        return uri;
    }//}}}
    
    private int column;
    private int line;
    private int offset;
    private Node errorNode;
    private String uri;
    
}
