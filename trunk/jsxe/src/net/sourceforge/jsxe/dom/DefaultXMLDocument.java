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
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
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
        setDefaultProperties();
        setModel(file);
        wellFormed=false;
    }//}}}
    
    protected DefaultXMLDocument(Reader reader) throws IOException {//{{{
        setDefaultProperties();
        setModel(reader);
        name = getUntitledLabel();
        wellFormed=false;
    }//}}}
    
    protected DefaultXMLDocument(String string) throws IOException {//{{{
        setDefaultProperties();
        setModel(string);
        name = getUntitledLabel();
        wellFormed=false;
    }//}}}
    
    public void checkWellFormedness() throws SAXParseException, SAXException, ParserConfigurationException, IOException {//{{{
        if (!wellFormed) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(source)));
            doc.getDocumentElement().normalize();
            document=doc;
            wellFormed=true;
        }
    }//}}}
    
    public Document getDocument() {//{{{
        return document;
    }//}}}

    public String getName() {//{{{
        return name;
    }//}}}

    public String getSource() throws IOException {//{{{
        //if the document is well formed we go by the DOM
        //if it's not we go by the source.
        
        if (isWellFormed()) {
            DOMSerializer.DOMSerializerConfiguration config = new DOMSerializer.DOMSerializerConfiguration();
            config.setParameter("format-output", getProperty("format-output"));
            config.setParameter("whitespace-in-element-content", getProperty("whitespace-in-element-content"));
            config.setParameter("indent", new Integer(getProperty("indent")));
            DOMSerializer serializer = new DOMSerializer(config);
            serializer.setEncoding(getProperty("encoding"));
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
            name = file.getName();
            wellFormed=false;
            FileReader reader=new FileReader(file);
            
            StringBuffer text = new StringBuffer();
            char[] buffer = new char[READ_SIZE];

            int bytesRead;
            do {
                bytesRead = reader.read(buffer, 0, READ_SIZE);
                if (bytesRead != -1)
                    text.append(buffer, 0, bytesRead);
            }
            while (bytesRead != -1);
            source = text.toString();
            XMLFile = file;
        } else {
            throw new FileNotFoundException("File Not Found: null");
        }
    }//}}}
    
    public void setModel(Reader reader) throws IOException {//{{{
        wellFormed=false;
        
        StringBuffer text = new StringBuffer();
        char[] buffer = new char[READ_SIZE];

        int bytesRead;
        do {
            bytesRead = reader.read(buffer, 0, READ_SIZE);
            if (bytesRead != -1)
                text.append(buffer, 0, bytesRead);
        }
        while (bytesRead != -1);
        source = text.toString();
    }//}}}
    
    public void setModel(String string) {//{{{
        wellFormed = false;
        source=string;
    }//}}}
    
    public boolean isWellFormed() {//{{{
        try {
            checkWellFormedness();
            wellFormed = true;
        } catch (Exception e) {
            wellFormed=false;
        }
        
        return wellFormed;
    }//}}}

    public void save() throws IOException, SAXParseException, SAXException, ParserConfigurationException {//{{{
       if (getFile() != null) {
           saveAs(getFile());
       } else {
           //You shouldn't call this when the document is untitled but
           //if you do default to saving to the home directory.
           File newFile = new File(System.getProperty("user.home") + getName());
           setModel(newFile);
       }
    }//}}}
    
    public void saveAs(File file) throws IOException, SAXParseException, SAXException, ParserConfigurationException {//{{{
        checkWellFormedness();
        
        DOMSerializer.DOMSerializerConfiguration config = new DOMSerializer.DOMSerializerConfiguration();
        config.setParameter("format-output", getProperty("format-output"));
        config.setParameter("whitespace-in-element-content", getProperty("whitespace-in-element-content"));
        config.setParameter("indent", new Integer(getProperty("indent")));
        
        DOMSerializer serializer = new DOMSerializer(config);
        serializer.setEncoding(getProperty("encoding"));
        
        FileOutputStream out = new FileOutputStream(file);
        
        serializer.writeNode(out, getDocument());
        setModel(file);
    }//}}}
    
    public boolean equals(Object o) throws ClassCastException {//{{{
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
                try {
                    if (caseInsensitiveFilesystem) {
                        
                        if (file.getCanonicalPath().equalsIgnoreCase(getFile().getCanonicalPath())) {
                            return true;
                        }
                        
                    } else {
                        
                        if (file.getCanonicalPath().equals(getFile().getCanonicalPath())) {
                            return true;
                        }
                    }
                } catch (IOException ioe) {
                    jsXe.exiterror(null, ioe.getMessage(), 1);
                }
            }
        }
        
        return false;
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
    
    private void setDefaultProperties() {///{{{
        setProperty("format-output", "false");
        setProperty("whitespace-in-element-content", "true");
        setProperty("encoding", "UTF-8");
        setProperty("indent", "4");
    }//}}}
    
    private Document document;
    private File XMLFile;
    private String name;
    private String source=new String();
    private boolean wellFormed;
    private static final int READ_SIZE = 5120;
    //}}}
}
