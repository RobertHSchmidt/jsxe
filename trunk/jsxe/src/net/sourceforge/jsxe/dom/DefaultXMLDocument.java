/*
DefaultXMLDocument.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the default implementation of the XMLDocument abstract class.
It represents a generic XML document.

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
import net.sourceforge.jsxe.gui.TabbedView;
//}}}

//{{{ DOM classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;
//}}}

//}}}

public class DefaultXMLDocument extends XMLDocument {
    
    protected DefaultXMLDocument(File file) throws FileNotFoundException, IOException {//{{{
        setProperty("format-output",                 "false");
        setProperty("whitespace-in-element-content", "true");
        setModel(file);
        validated=false;
    }//}}}
    
    protected DefaultXMLDocument(Reader reader) throws IOException {//{{{
        setProperty("format-output",                 "false");
        setProperty("whitespace-in-element-content", "true");
        setModel(reader);
        name = getUntitledLabel();
        validated=false;
    }//}}}
    
    protected DefaultXMLDocument(String string) throws IOException {//{{{
        setProperty("format-output", "false");
        setProperty("whitespace-in-element-content", "true");
        setModel(string);
        name = getUntitledLabel();
        validated=false;
    }//}}}
    
    public void validate() throws SAXParseException, SAXException, ParserConfigurationException, IOException {//{{{
        if (!isValidated()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(source)));
            doc.getDocumentElement().normalize();
            document=doc;
            validated=true;
        }
    }//}}}
    
    public Document getDocument() {//{{{
        return document;
    }//}}}

    public String getName() {//{{{
        return name;
    }//}}}

    public String getSource() throws IOException {//{{{
        //if the document is validated we go by the DOM
        //if it's not we go by the source.
        
        if (isValidated()) {
            DOMSerializer.DOMSerializerConfiguration config = new DOMSerializer.DOMSerializerConfiguration();
            config.setParameter("format-output", getProperty("format-output"));
            config.setParameter("whitespace-in-element-content", getProperty("whitespace-in-element-content"));
            DOMSerializer serializer = new DOMSerializer(config);
            return serializer.writeToString(getDocument());
        } else {
            return source;
        }
        
    }//}}}
    
    public File getFile() {//{{{
        return XMLFile;
    }//}}}
    
    public void setModel(File file) throws FileNotFoundException, IOException {//{{{
        if (file!=null) {
            int nextchar=0;
            source = "";
            name = file.getName();
            FileReader reader=new FileReader(file);
            validated=false;
            //This is _very_ inefficient
            //Change this very soon.
            while (nextchar != -1) {
               nextchar = reader.read();
               if (nextchar != -1)
                   source+=(char)nextchar;
            }
            XMLFile = file;
        } else {
            throw new FileNotFoundException("File Not Found: null");
        }
    }//}}}
    
    public void setModel(Reader reader) throws IOException {//{{{
        validated=false;
        source="";
        int nextchar=0;
        while (nextchar != -1) {
           nextchar = reader.read();
           if (nextchar != -1)
               source+=(char)nextchar;
        }
    }//}}}
    
    public void setModel(String string) {//{{{
        validated = false;
        source=string;
    }//}}}
    
    public boolean isValidated() {//{{{
        return validated;
    }//}}}

    public void save() throws IOException, SAXParseException, SAXException, ParserConfigurationException {//{{{
       if (XMLFile != null) {
           saveAs(XMLFile);
       } else {
           //You shouldn't call this when the document is untitled but
           //if you do default to saving to the home directory.
           File newFile = new File(System.getProperty("user.home") + getName());
           setModel(newFile);
       }
    }//}}}
    
    public void saveAs(File file) throws IOException, SAXParseException, SAXException, ParserConfigurationException {//{{{
        validate();
        //formatting the document is disabled because it doesn't work right
        DOMSerializer serializer = new DOMSerializer();
        FileOutputStream out = new FileOutputStream(file);
        serializer.writeNode(out, getDocument());
        setModel(file);
    }//}}}
    
    //{{{ Private members
    
    private String getUntitledLabel() {//{{{
        XMLDocument[] docs = jsXe.getXMLDocuments();
        int untitledNo = 0;
        for (int i=0; i < docs.length; i++) {
            if ( docs[i].getName().startsWith("Untitled-")) {
                // Kinda stolen from jEdit
                try {
					untitledNo = Math.max(untitledNo,Integer.parseInt(docs[i].getName().substring(9)));
                }
				catch(NumberFormatException nf) {}
            }
        }
        return "Untitled-" + Integer.toString(untitledNo+1);
    }//}}}
    
    private Document document;
    private File XMLFile;
    private String name;
    private String source=new String();
    private boolean validated;
    //}}}
}
