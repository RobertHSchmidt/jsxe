/*
DocumentViewFactory.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the factory that creates DocumentViews in jsXe. This can
be used to create different views that can display the XML document in different
ways each supporting the DocumentView interface that is used by the TabbedView.

This file written by ian Lewis (iml001@bridgewater.edu)
Copyright (C) 2002 by ian Lewis

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

package net.sourceforge.jsxe.gui.view;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.dom.XMLDocument;
//}}}

//{{{ Java base classes
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;
//}}}

//}}}

public class DocumentViewFactory {

    private DocumentViewFactory() {}
    
    public static DocumentViewFactory newInstance() {//{{{
        return new DocumentViewFactory();
    }//}}}
    
    public void setDocumentViewType(String type) {//{{{
        viewType = type;
    }//}}}
    
    public DocumentView newDocumentView() throws UnrecognizedDocViewException {//{{{
        //Document type validation is pretty simple right now
        if (viewType == "documentview.default") {
            return new DefaultView();
        } else {
            if (viewType == "documentview.sourceview") {
                return new SourceView();
            } else {
                throw new UnrecognizedDocViewException();
            }
        }
    }//}}}

    public static Enumeration getAvailableViewNames() {//{{{
        return new DocumentViews();
    }//}}}

    //{{{ Private members
    
    private static class DocumentViews implements Enumeration {//{{{
        
        DocumentViews() {
            elements.add("documentview.default");
            elements.add("documentview.sourceview");
        }
        
        public boolean hasMoreElements() {
            return (elements.size() != 0);
        }
        
        public Object nextElement() throws NoSuchElementException {
            if (elements.size() != 0)
                return elements.remove(0);
            else
                throw new NoSuchElementException();
        }
        
        private Vector elements = new Vector(2);
    }//}}}
    
    private String viewType = "documentview.default";
    
    //}}}
}
