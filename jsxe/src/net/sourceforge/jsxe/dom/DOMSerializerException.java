/*
DOMSerializerException.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the code for the serialization error handling class.

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
import org.apache.xerces.dom3.DOMError;
//}}}

//}}}

public class DOMSerializerException extends Exception {
    
    //{{{ DOMSerializerException constructor
    
    public DOMSerializerException(DOMError err) {
        super(((Throwable)err.getRelatedException()).getMessage());
        error = err;
    }//}}}
    
    //{{{ getError()
    
    public DOMError getError() {
        return error;
    }//}}}

    //{{{ Private members
    private DOMError error;
    //}}}

}
