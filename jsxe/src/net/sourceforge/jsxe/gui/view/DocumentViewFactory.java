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

package net.sourceforge.jsxe.gui.view;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.DocumentBuffer;
//}}}

//{{{ Java base classes
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.NoSuchElementException;
//}}}

//}}}

public class DocumentViewFactory {

    //{{{ DocumentViewFactory constructor
    
    private DocumentViewFactory() {}//}}}
    
    //{{{ newInstance()
    
    public static DocumentViewFactory newInstance() {
        return new DocumentViewFactory();
    }//}}}
    
    //{{{ setDocumentViewType()
    
    public void setDocumentViewType(String type) {
        viewType = type;
    }//}}}
    
    //{{{ newDocumentView()
    
    public DocumentView newDocumentView(DocumentBuffer document) throws IOException, UnrecognizedDocViewException {
        //Document type validation is pretty simple right now
        if (viewType.equals("tree")) {
            return new DefaultView(document);
        } else {
            if (viewType.equals("source")) {
                return new SourceView(document);
            } else {
                throw new UnrecognizedDocViewException(viewType);
            }
        }
    }//}}}

    //{{{ getAvailableViewTypes()
    
    public static Enumeration getAvailableViewTypes() {
        return new DocumentViews();
    }//}}}

    //{{{ Private members
    
    //{{{ DocumentViews class
    
    private static class DocumentViews implements Enumeration {
        
        //{{{ DocumentViews constructor
        
        DocumentViews() {
            elements.add("tree");
            elements.add("source");
        }//}}}
        
        //{{{ Enumeration methods
        
        //{{{ hasMoreElements()
        
        public boolean hasMoreElements() {
            return (elements.size() != 0);
        }//}}}
        
        //{{{ nextElement()
        
        public Object nextElement() throws NoSuchElementException {
            if (elements.size() != 0)
                return elements.remove(0);
            else
                throw new NoSuchElementException();
        }//}}}
        
        //}}}
        
        //{{{ Private members
        
        private ArrayList elements = new ArrayList(2);
        //}}}
    }//}}}
    
    private String viewType = "tree";
    
    //}}}
}
