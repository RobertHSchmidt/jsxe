/*
XMLDocumentFactory.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contians the factory that is used to create XMLDocuments of different
types. This can be used to create objects that implement the XMLDocument interface
in different ways.

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
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
//}}}

//{{{ DOM classes
import org.xml.sax.EntityResolver;
//}}}

//}}}

public class XMLDocumentFactory {
    
    private XMLDocumentFactory() {}
    
    public static XMLDocumentFactory newInstance() {//{{{
        return new XMLDocumentFactory();
    }//}}}
    
    public void setXMLDocumentType(String type) {//{{{
        docType = type;
    }//}}}
    
    public void setEntityResolver(EntityResolver resolver) {//{{{
        m_entityResolver = resolver;
    }//}}}
    
    public XMLDocument newXMLDocument(Reader reader) throws IOException, UnrecognizedDocTypeException {//{{{
        //Document type validation is pretty simple right now
        if (docType == "xmldocument.default") {
            if (m_entityResolver != null) {
                return new DefaultXMLDocument(reader, m_entityResolver);
            } else {
                return new DefaultXMLDocument(reader);
            }
        } else {
            throw new UnrecognizedDocTypeException();
        }
    }//}}}
    
    //{{{ Private members
    private String docType = "xmldocument.default";
    private EntityResolver m_entityResolver;
    //}}}

}