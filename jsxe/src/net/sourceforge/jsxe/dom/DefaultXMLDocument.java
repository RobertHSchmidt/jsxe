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
import org.xml.sax.EntityResolver;
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
import java.io.OutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;
import javax.swing.text.Segment;
//}}}

//}}}

public class DefaultXMLDocument extends XMLDocument {
    
    public static String ENCODING = "encoding";
    public static String WS_IN_ELEMENT_CONTENT = DOMSerializerConfiguration.WS_IN_ELEMENT_CONTENT;
    public static String FORMAT_XML = DOMSerializerConfiguration.FORMAT_XML;
    public static String INDENT = DOMSerializerConfiguration.INDENT;
    
    DefaultXMLDocument(Reader reader) throws IOException {//{{{
        setDefaultProperties();
        setModel(reader);
    }//}}}
    
    DefaultXMLDocument(Reader reader, EntityResolver resolver) throws IOException {//{{{
        m_entityResolver = resolver;
        setDefaultProperties();
        setModel(reader);
    }//}}}
    
    public boolean checkWellFormedness() throws SAXParseException, SAXException, ParserConfigurationException, IOException {//{{{
        
        if (!m_parsedMode) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            if (m_entityResolver != null) {
                builder.setEntityResolver(m_entityResolver);
            }
            
            Document doc = builder.parse(new ContentManagerInputStream(m_content));
            doc.getDocumentElement().normalize();
            setDocument(doc);
            
            m_parsedMode=true;
        }
        return m_parsedMode;
    }//}}}
    
    public String setProperty(String key, String value) {//{{{
        
        String oldValue = getProperty(key);
        String returnValue = oldValue;
        
        if (oldValue == null || !oldValue.equals(value)) {
            
            // do this first so NullPointerExceptions are thrown
            returnValue = (String)props.setProperty(key, value);
            
            if (key == "format-pretty-print") {
                m_syncedWithContent = false;
                if (Boolean.valueOf(value).booleanValue()) {
                    setProperty("element-content-whitespace", "false");
                }
            }
            if (key == "element-content-whitespace") {
                m_syncedWithContent = false;
                if (Boolean.valueOf(value).booleanValue()) {
                    setProperty("format-pretty-print", "false");
                }
            }
            firePropertiesChanged(key);
            return returnValue;
            
        }
        return returnValue;
    }//}}}
    
    public String getProperty(String key) {//{{{
        return props.getProperty(key);
    }//}}}
    
    public String getProperty(String key, String defaultValue) {//{{{
        return props.getProperty(key, defaultValue);
    }//}}}
    
    public AdapterNode getAdapterNode() {//{{{
        return m_adapterNode;
    }//}}}
    
    public AdapterNode newAdapterNode(AdapterNode parent, Node node) {//{{{
        AdapterNode newNode = null;
        if (node != null && parent != null) {
            newNode = new AdapterNode(this, parent, node);
            newNode.addAdapterNodeListener(docAdapterListener);
        }
        return newNode;
    }//}}}
    
    public String getText(int start, int length) throws IOException {//{{{
        
        if (start < 0 || length < 0 || start + length > m_content.getLength()) {
            throw new ArrayIndexOutOfBoundsException(start + ":" + length);
        }
        
        //if the document is well formed we go by the DOM
        //if it's not we go by the source text.
        if (m_parsedMode) {
            
            syncContentWithDOM();
            return m_content.getText(start, length);
            
        } else {
            
            return m_content.getText(start,length);
        }
    }//}}}
    
    public int getLength() {//{{{
        
        syncContentWithDOM();
        
        return m_content.getLength();
    }//}}}
    
    public boolean isWellFormed() throws IOException {//{{{
        
        if (!m_parsedMode) {
            try {
                checkWellFormedness();
            } catch (SAXException saxe) {
                //nothing wrong here.
                //document is just not well-formed.
            } catch (ParserConfigurationException pce) {
                throw new IOException(pce.getMessage());
            }
        }
        
        return m_parsedMode;
    }//}}}
    
   // public void save() throws IOException, SAXParseException, SAXException, ParserConfigurationException {//{{{
   //     if (getFile() != null) {
   //         saveAs(getFile());
   //     } else {
   //         //You shouldn't call this when the document is untitled but
   //         //if you do default to saving to the home directory.
   //         File newFile = new File(System.getProperty("user.home") + getName());
   //         //don't really need to do this.
   //        // setModel(newFile);
   //         //just set m and name instead.
   //         XMLFile = newFile;
   //         name = newFile.getName();
   //     }
   // }//}}}
    
    public void serialize(OutputStream out) throws IOException, UnsupportedEncodingException {//{{{
        if (m_parsedMode) {
            
            //since we are in parsed mode let's serialize.
            DOMOutput output = new DOMOutput(out, getProperty(ENCODING));
            
            DOMSerializerConfiguration config = new DOMSerializerConfiguration();
            config.setParameter(FORMAT_XML, getProperty(FORMAT_XML));
            config.setParameter(WS_IN_ELEMENT_CONTENT, getProperty(WS_IN_ELEMENT_CONTENT));
            config.setParameter(INDENT, new Integer(getProperty(INDENT)));
            
            DOMSerializer serializer = new DOMSerializer(config);
            
            if (!serializer.write(m_document, output)) {
                throw new IOException("Could not serialize XML document.");
            }
            
        } else {
            
            //not in parsed mode. just write out the text.
            int length = m_content.getLength();
            int index = 0;
            while (index < length) {
                
                int size = WRITE_SIZE;
                try {
                    size = Math.min(length - index, WRITE_SIZE);
				} catch(NumberFormatException nf) {}
                
                out.write(m_content.getText(index, size).getBytes(getProperty(ENCODING)), index, size);
                index += size;
            }
        }
        
    }//}}}
    
    public void setEntityResolver(EntityResolver resolver) {//{{{
        m_entityResolver = resolver;
    }//}}}
    
    public void insertText(int offset, String text) throws IOException {//{{{
        m_content.insert(offset, text);
        m_parsedMode = false;
        fireStructureChanged(null);
    }//}}}
    
    public void removeText(int offset, int length) throws IOException {//{{{
        m_content.remove(offset, length);
        m_parsedMode = false;
        fireStructureChanged(null);
    }//}}}
    
    public void addXMLDocumentListener(XMLDocumentListener listener) {//{{{
        if (listener != null) {
            listeners.add(listener);
        }
    }//}}}
    
    public void removeXMLDocumentListener(XMLDocumentListener listener) {//{{{
        if (listener != null) {
            listeners.remove(listeners.indexOf(listener));
        }
    }//}}}
    
    //{{{ Private members
    
   // private void setModel(File file) throws FileNotFoundException, IOException {//{{{
   //     if (file!=null) {
   //         int nextchar=0;
   //         name = file.getName();
   //         FileReader reader=new FileReader(file);
   //         
   //         
   //         StringBuffer text = new StringBuffer();
   //         char[] buffer = new char[READ_SIZE];
   //         
   //         //Save the document to a string
   //         int bytesRead;
   //         do {
   //             bytesRead = reader.read(buffer, 0, READ_SIZE);
   //             if (bytesRead != -1)
   //                 text.append(buffer, 0, bytesRead);
   //         }
   //         while (bytesRead != -1);
   //         m_text = text.toString();
   //         
   //         File oldFile = m_file;
   //         m_file = file;
   //         
   //         m_parsedMode = false;
   //         
   //         try {
   //             checkWellFormedness();
   //         } catch (SAXException saxe) {
   //             m_file = oldFile;
   //         } catch (ParserConfigurationException pce) {
   //             m_file = oldFile;
   //             throw new IOException(pce.getMessage());
   //         } catch (IOException ioe) {
   //             m_file = oldFile;
   //             throw ioe;
   //         }
   //         
   //         if (!file.equals(oldFile)) {
   //             fireFileChanged();
   //         }
   //         
   //         fireStructureChanged(m_adapterNode);
   //     } else {
   //         throw new FileNotFoundException("File Not Found: null");
   //     }
   // }//}}}
    
    /**
     * Sets up the DefaultXMLDocument given a Reader.
     * This should only be called in the constructor since if
     * an IOException is thrown in checkWellFormedness()
     * then the content manager will be messed up.
     * Also fireStructureChanged is not called since we
     * don't want to notify listeners that the struture
     * has changed when we are building the DOM for the first
     * time.
     */
    private void setModel(Reader reader) throws IOException {//{{{
        
        StringBuffer text = new StringBuffer();
        char[] buffer = new char[READ_SIZE];
        
        m_content = new ContentManager();
        
        //Read the document the content manager
        int bytesRead;
        int index = 0;
        do {
            bytesRead = reader.read(buffer, 0, READ_SIZE);
            if (bytesRead != -1) {
                m_content.insert(index, new String(buffer, 0, bytesRead));
                index+=bytesRead;
            }
        }
        while (bytesRead != -1);
        
        //check the wellformedness
        m_parsedMode = false;
        
        try {
            checkWellFormedness();
        } catch (SAXException saxe) {
            //nothing wrong here.
            //document is just not well-formed.
        } catch (ParserConfigurationException pce) {
            throw new IOException(pce.getMessage());
        }
    }//}}}
    
   // private void setModel(String string) throws IOException {//{{{
   //     String backupSource = source;
   //     m_text = string;
   //     m_parsedMode = false;
   //     
   //     try {
   //         checkWellFormedness();
   //     } catch (SAXException saxe) {
   //     } catch (ParserConfigurationException pce) {
   //         //resore the source text
   //         m_text = backupSource;
   //         throw new IOException(pce.getMessage());
   //     } catch (IOException ioe) {
   //         //restore the source text
   //         m_text = backupSource;
   //         throw ioe;
   //     }
   //     fireStructureChanged(m_adapterNode);
   // }//}}}
    
    private void setDefaultProperties() {//{{{
        setProperty(FORMAT_XML, "false");
        setProperty(WS_IN_ELEMENT_CONTENT, "true");
        setProperty(ENCODING, "UTF-8");
        setProperty(INDENT, "4");
    }//}}}
    
    private void firePropertiesChanged(String key) {//{{{
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            XMLDocumentListener listener = (XMLDocumentListener)iterator.next();
            listener.propertiesChanged(this, key);
        }
    }//}}}
    
    private void fireStructureChanged(AdapterNode location) {//{{{
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            XMLDocumentListener listener = (XMLDocumentListener)iterator.next();
            listener.structureChanged(this, location);
        }
        m_syncedWithContent = false;
    }//}}}
    
    private void setDocument(Document doc) {//{{{
        m_document=doc;
        m_adapterNode = new AdapterNode(this, m_document);
        m_adapterNode.addAdapterNodeListener(docAdapterListener);
        m_syncedWithContent = false;
    }//}}}
    
    /**
     * Write the DOM to the content manager given the current serialization and
     * formatting options.
     */
    private void syncContentWithDOM() {//{{{
        if (m_parsedMode) {
            if (!m_syncedWithContent) {
                //create a new content manager to be written to.
                ContentManager content = new ContentManager();
                //create the content manager's output stream
                ContentManagerOutputStream out = new ContentManagerOutputStream(content);
                try {
                    serialize(out);
                    m_content = content;
                } catch (IOException ioe) {
                    //Shouldn't happen.
                }
            }
        }
        m_syncedWithContent = true;
    }//}}}
    
    private class XMLDocAdapterListener implements AdapterNodeListener {//{{{
        
        public void nodeAdded(AdapterNode source, AdapterNode added) {
            fireStructureChanged(source);
        }
        
        public void nodeRemoved(AdapterNode source, AdapterNode removed) {
            fireStructureChanged(source);
        }
        
        public void localNameChanged(AdapterNode source) {
            fireStructureChanged(source);
        }
        
        public void namespaceChanged(AdapterNode source) {
            fireStructureChanged(source);
        }
        
        public void nodeValueChanged(AdapterNode source) {
            fireStructureChanged(source);
        }
        
        public void attributeChanged(AdapterNode source, String attr) {
            fireStructureChanged(source);
        }
        
    }//}}}
    
    /**
     * Text content manager based off of jEdit's ContentManager class.
     */
    private class ContentManager {//{{{
        
        public ContentManager() {//{{{
            text = new char[1024];
        } //}}}
    
        public final int getLength() {//{{{
            return length;
        } //}}}
    
        public String getText(int start, int len) {//{{{
            if(start >= gapStart) {
                return new String(text,start + gapEnd - gapStart,len);
            } else {
                if(start + len <= gapStart) {
                    return new String(text,start,len);
                } else {
                    return new String(text,start,gapStart - start).concat(new String(text,gapEnd,start + len - gapStart));
                }
            }
        } //}}}
    
        public void getText(int start, int len, Segment seg) {//{{{
            if(start >= gapStart) {
                seg.array = text;
                seg.offset = start + gapEnd - gapStart;
                seg.count = len;
            } else {
                if(start + len <= gapStart) {
                    seg.array = text;
                    seg.offset = start;
                    seg.count = len;
                } else {
                    seg.array = new char[len];
        
                    // copy text before gap
                    System.arraycopy(text,start,seg.array,0,gapStart - start);
        
                    // copy text after gap
                    System.arraycopy(text,gapEnd,seg.array,gapStart - start,
                        len + start - gapStart);
        
                    seg.offset = 0;
                    seg.count = len;
                }
            }
        } //}}}
        
        public void insert(int start, String str) {//{{{
            int len = str.length();
            moveGapStart(start);
            
            if(gapEnd - gapStart < len) {
                ensureCapacity(length + len + 1024);
                moveGapEnd(start + len + 1024);
            }
    
            str.getChars(0,len,text,start);
            gapStart += len;
            length += len;
        } //}}}
    
        public void insert(int start, Segment seg) {//{{{
            moveGapStart(start);
            
            if(gapEnd - gapStart < seg.count) {
                ensureCapacity(length + seg.count + 1024);
                moveGapEnd(start + seg.count + 1024);
            }
    
            System.arraycopy(seg.array,seg.offset,text,start,seg.count);
            gapStart += seg.count;
            length += seg.count;
        } //}}}
    
        public void _setContent(char[] text, int length) {//{{{
            this.text = text;
            this.gapStart = this.gapEnd = 0;
            this.length = length;
        } //}}}
    
        public void remove(int start, int len) {//{{{
            moveGapStart(start);
            gapEnd += len;
            length -= len;
        } //}}}
    
        //{{{ Private members
        private char[] text;
        private int gapStart = 0;
        private int gapEnd = 0;
        private int length = 0;
    
        private void moveGapStart(int newStart) {//{{{
            int newEnd = gapEnd + (newStart - gapStart);
    
            if(newStart == gapStart) {
                // nothing to do
            } else {
                if(newStart > gapStart) {
                    System.arraycopy(text,gapEnd,text,gapStart, newStart - gapStart);
                } else {
                    if(newStart < gapStart) {
                        System.arraycopy(text,newStart,text,newEnd, gapStart - newStart);
                    }
                }
            }
    
            gapStart = newStart;
            gapEnd = newEnd;
        } //}}}
    
        private void moveGapEnd(int newEnd) {//{{{
            System.arraycopy(text,gapEnd,text,newEnd,length - gapStart);
            gapEnd = newEnd;
        } //}}}
    
        private void ensureCapacity(int capacity) {//{{{
            
            if(capacity >= text.length) {
                char[] textN = new char[capacity * 2];
                System.arraycopy(text,0,textN,0,length + (gapEnd - gapStart));
                text = textN;
            }
            
        } //}}}
    
        //}}}
    }//}}}
    
    /**
     * output stream to write to the content manager when the serialized. Used
     * when syncing the source with the current Document.
     */
    private class ContentManagerOutputStream extends OutputStream {//{{{
        
        public ContentManagerOutputStream(ContentManager content) {//{{{
            m_m_content = content;
        }//}}}
        
        public void write(int b) throws IOException {//{{{
            byte []barray = { (byte)b };
            m_m_content.insert(m_m_content.getLength(), new String(barray));
        }//}}}
        
        public void write(byte[] b) throws IOException {//{{{
            m_m_content.insert(m_m_content.getLength(), new String(b));
        }//}}}
        
        public void write(byte[] b, int off, int len) {//{{{
            m_m_content.insert(m_m_content.getLength(), new String(b, off, len));
        }//}}}
        
        private ContentManager m_m_content;
    }//}}}
    /**
     * input stream for parsing reading current text content.
     */
    private class ContentManagerInputStream extends InputStream {//{{{
        
        public ContentManagerInputStream(ContentManager content) {//{{{
            m_m_content = content;
        }//}}}
        
        public int available() {//{{{
            return m_m_content.getLength() - m_m_index;
        }//}}}
        
        public int read() {//{{{
            if (m_m_index < m_m_content.getLength()) {
                char[] text = m_m_content.getText(m_m_index++, 1).toCharArray();
                return (int)text[0];
            } else {
                return -1;
            }
        }//}}}
        
        public int read(byte[] b) throws IOException {//{{{
            return read(b, 0, b.length);
        }//}}}
        
        public int read(byte[]b, int off, int len) {//{{{
            if (len == 0) {
                return 0;
            }
            
            int length = len;
            int contentLength = m_m_content.getLength();
            
            if (m_m_index < contentLength) {
                if (m_m_index + length >= contentLength) {
                    length = contentLength - m_m_index;
                }
            } else {
                length = -1;
            }
            
            //use UTF-8, want to esure it's UTF-8 instead of whatever
            //the system default is. We'll only use the specified encoding
            //when we write to disk.
            if (length != -1) {
                try {
                    
                    byte[] text = m_m_content.getText(m_m_index, length).getBytes("UTF-8");
                    System.arraycopy(text, 0, b, off, length);
                    m_m_index += length;
                    
                } catch (UnsupportedEncodingException uee) {
                    //UTF-8 is guaranteed to be supported.
                }
            }
            
            return length;
            
        }//}}}
        
        public long skip(long n) {//{{{
            m_m_index += (int)n;
            return n;
        }//}}}
        
        private int m_m_index = 0;
        private ContentManager m_m_content;
        
    }//}}}
    
    private Document m_document;
    private AdapterNode m_adapterNode;
    private ContentManager m_content;
    private boolean m_parsedMode = false;
    private boolean m_syncedWithContent = false;
    private EntityResolver m_entityResolver;
    private ArrayList listeners = new ArrayList();
    private Properties props = new Properties();
    private static final int READ_SIZE = 5120;
    private static final int WRITE_SIZE = 5120;
    
    private XMLDocAdapterListener docAdapterListener = new XMLDocAdapterListener();
    
    //}}}
}
