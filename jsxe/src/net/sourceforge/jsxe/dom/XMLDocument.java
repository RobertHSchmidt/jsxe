/*
XMLDocument.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the abstract class that can be used to create different
types of XMLDocuments. This class represents a document that is open and
contains attributes associated with the document.

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

//{{{ DOM classes
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
//}}}

//}}}

public abstract class XMLDocument {
    
    public abstract boolean checkWellFormedness() throws SAXParseException, SAXException, ParserConfigurationException, IOException;
    
    public boolean isUntitled() {//{{{
        return (getFile() == null);
    }//}}}
    
    public abstract AdapterNode getAdapterNode();
    
    public abstract AdapterNode newAdapterNode(AdapterNode parent, Node node);
    
    public abstract String getName();
    
    public abstract File getFile();
    
    public abstract String getSource() throws IOException;
    
    public abstract String setProperty(String key, String value);
    
    public abstract String getProperty(String key);
    
    public abstract String getProperty(String key, String defaultValue);
    
    public abstract void save() throws IOException, SAXParseException, SAXException, ParserConfigurationException;
    
    public abstract void saveAs(File file) throws IOException, SAXParseException, SAXException, ParserConfigurationException;
    
    public abstract void setModel(File file) throws FileNotFoundException, IOException;
    
    public abstract void setModel(Reader reader) throws IOException;
    
    public abstract void setModel(String string) throws IOException;
    
    public abstract boolean isWellFormed() throws IOException;

    public boolean equalsOnDisk(Object o) throws ClassCastException, IOException {//{{{
        if (getFile() != null && o != null) {
            boolean caseInsensitiveFilesystem = (File.separatorChar == '\\'
                || File.separatorChar == ':' /* Windows or MacOS */);
    
            File file;
    
            try {
                XMLDocument doc = (XMLDocument)o;
                file = doc.getFile();
            } catch (ClassCastException cce) {
                try {
                    file = (File)o;
                } catch (ClassCastException cce2) {
                    throw new ClassCastException("Could not cast to XMLDocument or File.");
                }
            }
            
            if (file != null) {
                if (caseInsensitiveFilesystem) {
                    
                    if (file.getCanonicalPath().equalsIgnoreCase(getFile().getCanonicalPath())) {
                        return true;
                    }
                    
                } else {
                    
                    if (file.getCanonicalPath().equals(getFile().getCanonicalPath())) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }//}}}
    
    public abstract void addXMLDocumentListener(XMLDocumentListener listener);
    
    public abstract void removeXMLDocumentListener(XMLDocumentListener listener);

}
