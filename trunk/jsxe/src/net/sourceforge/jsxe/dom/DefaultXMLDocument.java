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
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;
//}}}

//}}}

public class DefaultXMLDocument extends XMLDocument {
    
    DefaultXMLDocument(File file) throws FileNotFoundException, IOException {//{{{
        setDefaultProperties();
        setModel(file);
    }//}}}
    
    DefaultXMLDocument(Reader reader, String name) throws IOException {//{{{
        setDefaultProperties();
        setModel(reader);
        this.name = name;
    }//}}}
    
    DefaultXMLDocument(String string, String name) throws IOException {//{{{
        setDefaultProperties();
        setModel(string);
        this.name = name;
    }//}}}

    public boolean checkWellFormedness() throws SAXParseException, SAXException, ParserConfigurationException, IOException {//{{{
        wellFormed = false;
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(source)));
        doc.getDocumentElement().normalize();
        document=doc;
        
        wellFormed=true;
        return wellFormed;
    }//}}}
    
    public Document getDocument() {//{{{
        return document;
    }//}}}

    public String setProperty(String key, String value) {//{{{
        if (key == "format-output" && Boolean.valueOf(value).booleanValue()) {
            setProperty("whitespace-in-element-content", "false");
        }
        if (key == "whitespace-in-element-content" && Boolean.valueOf(value).booleanValue()) {
            setProperty("format-output", "false");
        }
        String returnValue = (String)props.setProperty(key, value);
        firePropertiesChanged(key);
        return returnValue;
    }//}}}
    
    public String getProperty(String key) {//{{{
        return props.getProperty(key);
    }//}}}
    
    public String getProperty(String key, String defaultValue) {//{{{
        return props.getProperty(key, defaultValue);
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
            
            try {
                checkWellFormedness();
            } catch (SAXException saxe) {
            } catch (ParserConfigurationException pce) {
                throw new IOException(pce.getMessage());
            }
            
            //set here so file is not set if there is an I/O Error.
            XMLFile = file;
            fireFileChanged();
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
    
    public void setModel(String string) throws IOException {//{{{
        String backupSource = source;
        source=string;
        try {
            checkWellFormedness();
        } catch (SAXException saxe) {
        } catch (ParserConfigurationException pce) {
            //resore the source variable.
            source = backupSource;
            throw new IOException(pce.getMessage());
        } catch (IOException ioe) {
            //restore the source variable.
            source = backupSource;
            throw ioe;
        }
    }//}}}
    
    public boolean isWellFormed() throws IOException {//{{{
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
    
    public void addXMLDocumentListener(XMLDocumentListener listener) {//{{{
        listeners.add(listener);
    }//}}}
    
    public void removeXMLDocumentListener(XMLDocumentListener listener) {//{{{
        listeners.remove(listeners.indexOf(listener));
    }//}}}
    
    //{{{ Private members
    
    private void setDefaultProperties() {///{{{
        setProperty("format-output", "false");
        setProperty("whitespace-in-element-content", "true");
        setProperty("encoding", "UTF-8");
        setProperty("indent", "4");
    }//}}}
    
    private void firePropertiesChanged(String key) {//{{{
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            XMLDocumentListener listener = (XMLDocumentListener)iterator.next();
            listener.propertiesChanged(this, key);
        }
    }//}}}
    
   // private void fireStructureChanged(AdapterNode location) {//{{{
   //     ListIterator iterator = listeners.listIterator();
   //     while (iterator.hasNext()) {
   //         XMLDocumentListener listener = (XMLDocumentListener)iterator.next();
   //         listener.structureChanged(this, location);
   //     }
   // }//}}}
    
    private void fireFileChanged() {//{{{
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            XMLDocumentListener listener = (XMLDocumentListener)iterator.next();
            listener.fileChanged(this);
        }
    }//}}}
    
    private Document document;
    private File XMLFile;
    private String name;
    private String source=new String();
    private boolean wellFormed;
    private ArrayList listeners = new ArrayList();
    private Properties props = new Properties();
    private static final int READ_SIZE = 5120;
    //}}}
}
