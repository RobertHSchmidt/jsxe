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
import org.xml.sax.EntityResolver;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
//}}}

//}}}

public abstract class XMLDocument {
    
    public abstract boolean checkWellFormedness() throws SAXParseException, SAXException, ParserConfigurationException, IOException;
    
    public abstract AdapterNode getAdapterNode();
    
    public abstract AdapterNode newAdapterNode(AdapterNode parent, Node node);
    
   // public abstract String getSource() throws IOException;
    
    public abstract String setProperty(String key, String value);
    
    public abstract String getProperty(String key);
    
    public abstract String getProperty(String key, String defaultValue);
    
    public abstract void serialize(OutputStream out) throws IOException, UnsupportedEncodingException;
    
    public abstract void setEntityResolver(EntityResolver resolver);
    
    public abstract String getText(int start, int length) throws IOException;
    
    public abstract int getLength();
    
   // public abstract void setModel(File file) throws FileNotFoundException, IOException;
   // 
   // public abstract void setModel(Reader reader) throws IOException;
   // 
   // public abstract void setModel(String string) throws IOException;
    
    public abstract void insertText(int offset, String text) throws IOException;
    
    public abstract void removeText(int offset, int length) throws IOException;
    
    public abstract boolean isWellFormed() throws IOException;
    
    public abstract void addXMLDocumentListener(XMLDocumentListener listener);
    
    public abstract void removeXMLDocumentListener(XMLDocumentListener listener);

}