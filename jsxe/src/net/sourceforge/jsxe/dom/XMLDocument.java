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
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ DOM classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xerces.dom3.DOMError;
import org.apache.xerces.dom3.DOMErrorHandler;
//}}}

//{{{ Java base classes
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Properties;
import javax.swing.text.Segment;
//}}}

//}}}

/**
 * The XMLDocument class represents an XML document as a  tree structure
 * that has nodes, implemented as AdapterNodes, as well as text. Methods are
 * provided to allow user objects to interact with the XML document as text
 * or as a tree structure seamlessly.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see AdapterNode
 */
public class XMLDocument {
    
    //{{{ XMLDocument defined properties
    
    /**
     * The property key for the encoding of this XML document
     */
    public static final String ENCODING = "encoding";
    /**
     * The property key for the boolean property specifying if whitespace
     * is allowed in element content.
     */
    public static final String WS_IN_ELEMENT_CONTENT = DOMSerializerConfiguration.WS_IN_ELEMENT_CONTENT;
    /**
     * The property key for the boolean property specifying that the XML text
     * should be formatted to look pleasing to the eye.
     */
    public static final String FORMAT_XML = DOMSerializerConfiguration.FORMAT_XML;
    /**
     * The property key for the property defining the size of a tab when the
     * document is displayed as text or otherwise.
     */
    public static final String INDENT = DOMSerializerConfiguration.INDENT;
    /**
     * The property key for the property defining whether to serialize
     * using soft tabs (tabs replaced by spaces). Has a value of "true" if
     * using soft tabs.
     */
    public static final String IS_USING_SOFT_TABS = DOMSerializerConfiguration.SOFT_TABS;
    /**
     * The property key for the property defining whether to validate the
     * document with a DTD or Schema
     */
    public static String IS_VALIDATING = "validating";
    
    //}}}
    
    //{{{ XMLDocument constructor
    /**
     * Creates a new XMLDocument for a document that can be read by the given
     * Reader.
     * @param reader the Reader object to read the XML document from.
     * @throws IOException if there was a problem reading the document
     */
    public XMLDocument(Reader reader) throws IOException {
        setDefaultProperties();
        setModel(reader);
        reader.close();
    }//}}}
    
    //{{{ XMLDocument constructor
    /**
     * Creates a new XMLDocument for a document that can be read by the given
     * Reader.
     * @param reader the Reader object to read the XML document from.
     * @param resolver the EntityResolver to use when resolving external
     *                 entities.
     * @throws IOException if there was a problem reading the document
     */
    public XMLDocument(Reader reader, EntityResolver resolver) throws IOException {
        m_entityResolver = resolver;
        setDefaultProperties();
        setModel(reader);
        reader.close();
    }//}}}
    
    //{{{ checkWellFormedness()
    /**
     * Checks the wellformedness of the document and throws appropriate
     * exceptions based on the errors encountered during parsing.
     * @return true if the document is well formed.
     * @throws SAXParseException if there was a SAX error when parsing.
     * @throws SAXException if there was a problem with the SAX parser.
     * @throws ParserConfigurationException if the parser is not configured properly
     * @throws IOException if there was a problem reading the document
     */
    public boolean checkWellFormedness() throws SAXParseException, SAXException, ParserConfigurationException, IOException {
        
        if (!m_parsedMode) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setExpandEntityReferences(false);
            factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", new Boolean(false));
            factory.setAttribute("http://xml.org/sax/features/external-general-entities", new Boolean(false));
            factory.setAttribute("http://xml.org/sax/features/external-parameter-entities", new Boolean(false));
            factory.setAttribute("http://xml.org/sax/features/namespaces",new Boolean(true));
           // factory.setAttribute("http://apache.org/xml/features/validation/dynamic",new Boolean(true));
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
                public void error(SAXParseException exception) {
                   // System.out.println(exception.getLineNumber());
                }
                public void fatalError(SAXParseException exception) {
                   // System.out.println(exception.getLineNumber());
                }
                public void warning(SAXParseException exception) {
                   // System.out.println(exception.getLineNumber());
                }
            });
            
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
    
    //{{{ setProperty()
    /**
     * Sets a property of the XMLDocument
     * @param key the key to the property
     * @param value the value of the property
     * @return the old value of the property
     */
    public String setProperty(String key, String value) {
        String oldValue = getProperty(key);
        
        if (oldValue == null || !oldValue.equals(value)) {
            // do this first so NullPointerExceptions are thrown
            oldValue = (String)props.setProperty(key, value);
            
            if (key.equals(ENCODING)) {
                m_syncedWithContent = false;
            }
            if (key.equals(FORMAT_XML)) {
                m_syncedWithContent = false;
                if (Boolean.valueOf(value).booleanValue()) {
                    setProperty(WS_IN_ELEMENT_CONTENT, "false");
                }
            }
            if (key.equals(WS_IN_ELEMENT_CONTENT)) {
                m_syncedWithContent = false;
                if (Boolean.valueOf(value).booleanValue()) {
                    setProperty(FORMAT_XML, "false");
                }
            }
            if (key.equals(IS_USING_SOFT_TABS)) {
                m_syncedWithContent = false;
            }
            firePropertyChanged(key, oldValue);
        }
        return oldValue;
    }//}}}
    
    //{{{ getDocumentCopy()
    /**
     * Gets a copy of the underlying Document objects.
     * @return a deep copy of the underlying document object
     */
    public Document getDocumentCopy() {
        //makes a deep copy of the document node
        try {
            checkWellFormedness();
        } catch (SAXParseException e) {
        } catch (SAXException e) {
        } catch (ParserConfigurationException e) {
        } catch (IOException e) {}
        if (m_document != null) {
            return (Document)m_document.cloneNode(true);
        } else {
            return null;
        }
    }//}}}
    
    //{{{ getProperties()
    
    public Properties getProperties() {
        return props;
    }//}}}
    
    //{{{ getProperty()
    /**
     * Gets a property for the key given.
     * @param key the key to the properties list
     * @return the value of the property for the given key.
     */
    public String getProperty(String key) {
        return props.getProperty(key);
    }//}}}
    
    //{{{ getProperty()
    /**
     * Gets a property for the key given or returns the default value
     * if there is no property for the given key.
     * @param key the key to the properties list
     * @param defaultValue the default value for the property requested
     * @return the value of the property for the given key.
     */
    public String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }//}}}
    
    //{{{ getRootElementNode()
    
    public AdapterNode getRootElementNode() {
        int childCount = m_adapterNode.childCount();
        AdapterNode rootElement = m_adapterNode.child(0);
        for (int i=1; i<childCount && rootElement.getNodeType() != Node.ELEMENT_NODE; i++) {
            rootElement = m_adapterNode.child(i);
        }
        return rootElement;
    }//}}}
    
    //{{{ getAdapterNode()
    /**
     * Returns the AdapterNode object for the root of the XML document.
     * @return the root node as an AdapterNode
     */
    public AdapterNode getAdapterNode() {
        try {
            checkWellFormedness();
        } catch (SAXParseException e) {
        } catch (SAXException e) {
        } catch (ParserConfigurationException e) {
        } catch (IOException e) {}
        return m_adapterNode;
    }//}}}
    
    //{{{ newAdapterNode()
    /**
     * Factory method that creates a new AdapterNode object wrapping the Node
     * object and AdapterNode object wrapping the Node's parent Node object.
     * @param parent the AdapterNode for the parent of the new node
     * @param node the Node object that the new AdapterNode should wrap
     * @return the new AdapterNode object
     */
    public AdapterNode newAdapterNode(AdapterNode parent, Node node) {
        AdapterNode newNode = null;
        if (node != null && parent != null) {
            newNode = new AdapterNode(this, parent, node);
            newNode.addAdapterNodeListener(docAdapterListener);
        }
        return newNode;
    }//}}}
    
    //{{{ newAdapterNode()
    /**
     * Creates a new AdapterNode in this document. This method is namespace aware.
     *
     * @param parent The parent of the node to create.
     * @param name the qualified name of the new node
     * @param value the value of the new node to create
     * @param type the type of node to create. Uses the types defined in the Node class.
     */
    public AdapterNode newAdapterNode(AdapterNode parent, String name, String value, short type) {
        Node newNode = null;
        
        //Only handle text and element nodes right now.
        switch(type) {
            case Node.ELEMENT_NODE:
                newNode = m_document.createElementNS("", name);
                break;
            case Node.TEXT_NODE:
                newNode = m_document.createTextNode(value);
                break;
            case Node.CDATA_SECTION_NODE:
                newNode = m_document.createCDATASection(value);     
                break;
            case Node.COMMENT_NODE:
                newNode = m_document.createComment(value);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                newNode = m_document.createProcessingInstruction(name, value);
                break;
            case Node.ENTITY_REFERENCE_NODE:
                if (m_document.getDoctype() ==  null) {
                    throw new DOMException(DOMException.NOT_FOUND_ERR, "No DTD defined");
                } else {
                    if (m_document.getDoctype().getEntities().getNamedItem(name) != null) {
                        newNode = m_document.createEntityReference(name);
                    } else {
                        throw new DOMException(DOMException.SYNTAX_ERR, "Entity "+"\""+name+"\""+" is not declared in the DTD");
                    }
                }
                break;
            case Node.DOCUMENT_TYPE_NODE:
                throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "DOM level 2 does not allow modification of the document type node");
            default:
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "An attempt was made to add a node that was not supported.");
        }
        
        return newAdapterNode(parent, newNode);
    }//}}}
    
    //{{{ getText()
    /**
     * Gets the text at a specified location in the document. This method
     * method should be used sparingly as changes to the properties of this
     * document or the tree structure could change the location of text
     * within the document.
     * @param start the starting index of the text to retrieve
     * @param length the length of the text needed
     * @return the text requested
     */
    public String getText(int start, int length) throws IOException {
        
        if (start < 0 || length < 0 || start + length > m_content.getLength()) {
            throw new ArrayIndexOutOfBoundsException(start + ":" + length);
        }
        
        //if the document is well formed we go by the DOM
        //if it's not we go by the source text.
        syncContentWithDOM();
        return m_content.getText(start,length);
    }//}}}
    
    //{{{ getSegment()
    /**
     * Gets the text at a specified location in the document. This method
     * method should be used sparingly as changes to the properties of this
     * document or the tree structure could change the location of text
     * within the document.
     * @param start the starting index of the text to retrieve
     * @param length the length of the text needed
     * @return the segment representing the text requested
     */
    public Segment getSegment(int start, int length) throws IOException {
        
        if (start < 0 || length < 0 || start + length > m_content.getLength()) {
            throw new ArrayIndexOutOfBoundsException(start + ":" + length);
        }
        
        //if the document is well formed we go by the DOM
        //if it's not we go by the source text.
        if (m_parsedMode) {
            syncContentWithDOM();
        }
        Segment seg = new Segment();
        m_content.getText(start, length, seg);
        return seg;
    }//}}}
    
    //{{{ getLength()
    /**
     * Gets the total number of characters in the document.
     * @return the length of the document
     */
    public int getLength() {
        
        syncContentWithDOM();
        
        return m_content.getLength();
    }//}}}
    
    //{{{ isWellFormed()
    /**
     * Indicates if the document is well formed.
     * @return true of the document is well formed
     * @throws IOException if there was a problem checking the wellformedness
     */
    public boolean isWellFormed() throws IOException {
        
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
    
    //{{{ serialize()
    /**
     * Writes the XML document to the output stream specified.
     * @param out the output stream to write the document to.
     * @throws IOException if there was a problem writing the document
     * @throws UnsupportedEncodingException if the encoding specified in the
     *                                      properties is not supported.
     */
    public void serialize(OutputStream out) throws IOException, UnsupportedEncodingException {
        
        boolean parsedBeforeSerialization = m_parsedMode;
        
        syncContentWithDOM();
        
        //now just write out the text.
        int length = m_content.getLength();
        int index = 0;
        java.io.BufferedWriter outbuf = new java.io.BufferedWriter(new java.io.OutputStreamWriter(out, getProperty(ENCODING)), IO_BUFFER_SIZE);
        Segment seg = new Segment();
        while (index < length) {
            int size = WRITE_SIZE;
            try {
                size = Math.min(length - index, WRITE_SIZE);
            } catch(NumberFormatException nf) {}
            
           // out.write(m_content.getText(index, size).getBytes(getProperty(ENCODING)), index, size);
            m_content.getText(index, size, seg);
            outbuf.write(seg.array, seg.offset, seg.count);
            index += size;
        }
        
        outbuf.close();
        
        //if something changed in the structure while serializing
        //basically we don't want serialize() to cause the XMLDocument
        //to go from parsed mode to non-parsed mode
        if (!m_parsedMode && parsedBeforeSerialization) {
            try {
                checkWellFormedness();
            } catch (SAXException saxe) {
                throw new IOException(saxe.getMessage());
            } catch (ParserConfigurationException pce) {
                throw new IOException(pce.getMessage());
            } finally {
                /*
                if there is an error parsing we want to be in parsed mode
                using the old DOM before serializing.
                if there is no error, we want to be in parsed mode with the
                new DOM.
                */
                m_parsedMode = true;
            }
        }
        
    }//}}}
    
    //{{{ serializeNodeToString()
    /**
     * Serializes a child node to a string using the properties specified in
     * this XMLDocument object.
     * @param node the node to serialize
     * @return the serialized version of the node given
     */
    public String serializeNodeToString(AdapterNode node) {
        String value = null;
        try {
            LSSerializer serializer = getSerializer();
            value = serializer.writeToString(node.getNode());
        } catch (DOMException e) {}
        return value;
    }//}}}
    
    //{{{ setEntityResolver()
    /**
     * Sets the EntityResolver object that is used when resolving external
     * entities.
     * @param resolver the entity resolver
     */
    public void setEntityResolver(EntityResolver resolver) {
        m_entityResolver = resolver;
    }//}}}
    
    //{{{ insertText()
    /**
     * Inserts text into the document at the specified location
     * @param offset the character offset where the text should be inserted
     * @param text the text to insert
     * @throws IOException if the text could not be inserted
     */
    public void insertText(int offset, String text) throws IOException {
        syncContentWithDOM();
        m_content.insert(offset, text);
        m_parsedMode = false;
        //may have some algorithm to determine the modified node(s) in the
        //future
        fireStructureChanged(null);
    }//}}}
    
    //{{{ removeText()
    /**
     * Removes text at the specifed character offset.
     * @param offset the character offset where the text is removed form
     * @param length the length of the text segment to remove
     * @throws IOException if the text could not be removed
     */
    public void removeText(int offset, int length) throws IOException {
        syncContentWithDOM();
        m_content.remove(offset, length);
        m_parsedMode = false;
        //may have some algorithm to determine the modified node(s) in the
        //future
        fireStructureChanged(null);
    }//}}}
    
    //{{{ setModel()
    /**
     * Sets up the XMLDocument given a Reader. The existing content is
     * thrown out and the document read in through the reader is
     * used.
     */
    public void setModel(Reader reader) throws IOException {
        
        char[] buffer = new char[READ_SIZE];
        
        ContentManager content = new ContentManager();
        
        //Read the document the content manager
        int bytesRead;
        int index = 0;
        do {
            bytesRead = reader.read(buffer, 0, READ_SIZE);
            if (bytesRead != -1) {
                content.insert(index, new String(buffer, 0, bytesRead));
                index+=bytesRead;
            }
        }
        while (bytesRead != -1);
        
        m_content = content;
        
        m_parsedMode = false;
        
       // try {
       //     checkWellFormedness();
       // } catch (SAXException saxe) {
       //     //nothing wrong here.
       //     //document is just not well-formed.
       // } catch (ParserConfigurationException pce) {
       //     throw new IOException(pce.getMessage());
       // } catch (IOException ioe) {
       //     /*
       //     do nothing since this can't happen
       //     unless the DTD couldn't be found or something
       //     Treat it as if it wasn't well-formed.
       //     */
       // }
    }//}}}
    
    //{{{ addXMLDocumentListener()
    /**
     * Registers a change listener with the XMLDocument
     * @param listener the listener to register with this document
     */
    public void addXMLDocumentListener(XMLDocumentListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }//}}}
    
    //{{{ removeXMLDocumentListener()
    /**
     * Unregisters a change listener from this document
     * @param listener the listener to unregister
     */
    public void removeXMLDocumentListener(XMLDocumentListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }//}}}
    
    //{{{ fireStructureChanged()
    
    protected void fireStructureChanged(AdapterNode location) {
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            XMLDocumentListener listener = (XMLDocumentListener)iterator.next();
            listener.structureChanged(this, location);
        }
        m_syncedWithContent = false;
    }//}}}
    
    //{{{ Private static members
    private static final int READ_SIZE = 5120;
    private static final int WRITE_SIZE = 5120;
    private static final int IO_BUFFER_SIZE = 32768;
    //}}}
    
    //{{{ Private members
    
    //{{{ setDefaultProperties()
    
    private void setDefaultProperties() {
        setProperty(FORMAT_XML, "false");
        setProperty(IS_USING_SOFT_TABS, "false");
        setProperty(WS_IN_ELEMENT_CONTENT, "true");
        setProperty(ENCODING, "UTF-8");
        setProperty(INDENT, "4");
    }//}}}
    
    //{{{ firePropertyChanged()
    
    private void firePropertyChanged(String key, String oldValue) {
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            XMLDocumentListener listener = (XMLDocumentListener)iterator.next();
            listener.propertyChanged(this, key, oldValue);
        }
    }//}}}
    
    //{{{ setDocument()
    
    private void setDocument(Document doc) {
        m_document=doc;
        m_adapterNode = new AdapterNode(this, m_document);
        m_adapterNode.addAdapterNodeListener(docAdapterListener);
        m_syncedWithContent = false;
    }//}}}
    
    //{{{ syncContentWithDOM()
    /**
     * Write the DOM to the content manager given the current serialization and
     * formatting options.
     */
    private void syncContentWithDOM() {
        if (m_parsedMode) {
            if (!m_syncedWithContent) {
                try {
                    //since we are in parsed mode let's serialize to the content
                    LSSerializer serializer = getSerializer();
                    //create a new content manager to be written to.
                    ContentManager content = new ContentManager();
                    //create the content manager's output stream
                    ContentManagerOutputStream output = new ContentManagerOutputStream(content);
                    DOMOutput out = new DOMOutput(output, getProperty(ENCODING));
                    if (!serializer.write(m_document, out)) {
                        throw new IOException("Could not serialize XML document.");
                    }
                    m_content = content;
                } catch (IOException ioe) {
                    System.err.println("jsXe: "+ioe.getMessage());
                }
            }
        }
        m_syncedWithContent = true;
    }//}}}
    
    //{{{ getSerializer()
    
    private LSSerializer getSerializer() {
        DOMSerializerConfiguration config = new DOMSerializerConfiguration();
        config.setFeature(FORMAT_XML, Boolean.valueOf(getProperty(FORMAT_XML)).booleanValue());
        config.setFeature(WS_IN_ELEMENT_CONTENT, Boolean.valueOf(getProperty(WS_IN_ELEMENT_CONTENT)).booleanValue());
        config.setParameter(INDENT, new Integer(getProperty(INDENT)));
        config.setParameter(DOMSerializerConfiguration.ERROR_HANDLER, new XMLDocErrorHandler());
        config.setFeature(IS_USING_SOFT_TABS, Boolean.valueOf(getProperty(IS_USING_SOFT_TABS)).booleanValue());
        
        return new DOMSerializer(config);
    }//}}}
    
    //{{{ ContentManager class
    /**
     * Text content manager based off of jEdit's ContentManager class.
     */
    private static class ContentManager {
        
        // {{{ ContentManager constructor
        public ContentManager() {
            text = new char[1024];
        } //}}}
    
        //{{{ getLength()
        
        public final int getLength() {
            return length;
        } //}}}
    
        //{{{ getText()
        public String getText(int start, int len) {
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
    
        //{{{ getText()
        public void getText(int start, int len, Segment seg) {
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
        
        //{{{ insert()
        public void insert(int start, String str) {
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
    
        //{{{ insert()
        public void insert(int start, Segment seg) {
            moveGapStart(start);
            
            if(gapEnd - gapStart < seg.count) {
                ensureCapacity(length + seg.count + 1024);
                moveGapEnd(start + seg.count + 1024);
            }
    
            System.arraycopy(seg.array,seg.offset,text,start,seg.count);
            gapStart += seg.count;
            length += seg.count;
        } //}}}
    
        //{{{ _setContent()
        public void _setContent(char[] text, int length) {
            this.text = text;
            this.gapStart = this.gapEnd = 0;
            this.length = length;
        } //}}}
    
        //{{{ remove()
        public void remove(int start, int len) {
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
    
    //{{{ ContentManagerOutputStream class
    /**
     * output stream to write to the content manager when the serialized. Used
     * when syncing the source with the current Document.
     */
    private static class ContentManagerOutputStream extends OutputStream {
        
        //{{{ ContentManagerOutputStream constructor
        public ContentManagerOutputStream(ContentManager content) {
            m_m_content = content;
        }//}}}
        
        //{{{ write()
        public void write(int b) throws IOException {
            byte []barray = { (byte)b };
            m_m_content.insert(m_m_content.getLength(), new String(barray));
        }//}}}
        
        //{{{ write()
        public void write(byte[] b) throws IOException {
            m_m_content.insert(m_m_content.getLength(), new String(b));
        }//}}}
        
        //{{{ write()
        public void write(byte[] b, int off, int len) {
            m_m_content.insert(m_m_content.getLength(), new String(b, off, len));
        }//}}}
        
        //{{{ Private members
        private ContentManager m_m_content;
        //}}}
    }//}}}
    
    //{{{ ContentManagerInputStream class
    /**
     * Input stream for parsing reading current text content.
     */
    private static class ContentManagerInputStream extends InputStream {
        
        //{{{ ContentManagerInputStream constructor
        public ContentManagerInputStream(ContentManager content) {
            m_m_content = content;
        }//}}}
        
        //{{{ available()
        public int available() {
            return m_m_content.getLength() - m_m_index;
        }//}}}
        
        //{{{ read()
        public int read() {
            if (m_m_index < m_m_content.getLength()) {
                char[] text = m_m_content.getText(m_m_index++, 1).toCharArray();
                return (int)text[0];
            } else {
                return -1;
            }
        }//}}}
        
        //{{{ read()
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }//}}}
        
        //{{{ read()
        public int read(byte[]b, int off, int len) {
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
        
        //{{{ skip()
        public long skip(long n) {
            m_m_index += (int)n;
            return n;
        }//}}}
        
        //{{{ Private members
        private int m_m_index = 0;
        private ContentManager m_m_content;
        //}}}
    }//}}}
    
    //{{{ XMLDocAdapterListener class
    private class XMLDocAdapterListener implements AdapterNodeListener {
        
        // {{{ nodeAdded()
        public void nodeAdded(AdapterNode source, AdapterNode added) {
            fireStructureChanged(source);
        }//}}}
        
        //{{{ nodeRemoved()
        public void nodeRemoved(AdapterNode source, AdapterNode removed) {
            fireStructureChanged(source);
        }//}}}
        
        //{{{ localNameChanged()
        public void localNameChanged(AdapterNode source) {
            fireStructureChanged(source);
        }//}}}
        
        //{{{ namespaceChanged()
        public void namespaceChanged(AdapterNode source) {
            fireStructureChanged(source);
        }//}}}
        
        //{{{ nodeValueChanged()
        public void nodeValueChanged(AdapterNode source) {
            fireStructureChanged(source);
        }//}}}
        
        //{{{ attributeChanged()
        public void attributeChanged(AdapterNode source, String attr) {
            fireStructureChanged(source);
        }//}}}
        
    }//}}}
    
    //{{{ XMLDocErrorHandler class
    
    private class XMLDocErrorHandler implements DOMErrorHandler {
        
        //{{{ handleError()
        
        public boolean handleError(DOMError error) {
            
            if (error.getType() == "cdata-sections-splitted") {
                /*
                make the source the valid model and
                force reparsing when DOM objects are
                requested.
                */
                m_syncedWithContent = true;
                m_parsedMode = false;
                fireStructureChanged(null);
                return true;
            }
            
            return false;
        }//}}}
        
    }//}}}
    
    private Document m_document;
    private AdapterNode m_adapterNode;
    private ContentManager m_content;
    
    /**
     * This flag is set to true if and only if the DOM model is the
     * model that contains the current version of the document. This
     * will be set to true when the text has been parsed into the DOM
     * but set to false when the text is altered.
     */
    private boolean m_parsedMode = false;
    
    /**
     * This flag is set to true if and only if the textual content held in
     * the ContentManager m_content is synced symantically with the DOM
     * and the AdapterNodes held in the tree structure.
     * This flag will be set to false when the tree or content are changed in
     * such a way that they become out of sync.
     */
    private boolean m_syncedWithContent = false;
    private EntityResolver m_entityResolver;
    private ArrayList listeners = new ArrayList();
    private Properties props = new Properties();
    
    private XMLDocAdapterListener docAdapterListener = new XMLDocAdapterListener();
    
    //}}}
}
