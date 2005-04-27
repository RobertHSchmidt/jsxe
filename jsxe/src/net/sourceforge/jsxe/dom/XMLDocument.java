/*
XMLDocument.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2002 Ian Lewis (IanLewis@member.fsf.org)

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
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.util.MiscUtilities;
import net.sourceforge.jsxe.dom.completion.*;
//}}}

//{{{ DOM classes
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.*;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;
//}}}

//{{{ Xerces classes
import org.apache.xerces.dom3.DOMError;
import org.apache.xerces.dom3.DOMErrorHandler;
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.*;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XSGrammar;
//}}}

//{{{ Java base classes
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
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
     * document is displayed as text or otherwise. Used when serializing the
     * document using soft-tabs.
     */
    public static final String INDENT = DOMSerializerConfiguration.INDENT;
    /**
     * The property key for the property defining whether to serialize
     * using soft tabs (tabs replaced by the number of spaces specified in the
     * INDENT property). Has a value of "true" if using soft tabs.
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
        this(reader, null);
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
            parseDocument();
            m_adapterNode = new AdapterNode(this, m_document);
            m_adapterNode.addAdapterNodeListener(docAdapterListener);
            m_syncedWithContent = false;
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
            if (key.equals(IS_VALIDATING)) {
                syncContentWithDOM();
                //if we were in parsed mode we must make sure
                //the AdapterNodes in the tree have the correct
                //nodes internally.
                if (m_parsedMode) {
                    try {
                        parseDocument();
                        m_adapterNode.updateNode(m_document);
                    } catch (Exception e) {
                        //If an error occurs then we're in trouble
                        jsXe.exiterror(this, e, 1);
                    }
                }
            }
            firePropertyChanged(key, oldValue);
        }
        return oldValue;
    }//}}}
    
    //{{{ getDocumentCopy()
    /**
     * Gets a copy of the underlying Document object.
     * @return a deep copy of the underlying document object
     */
    public Document getDocumentCopy() {
        //makes a deep copy of the document node
        try {
            checkWellFormedness();
        } catch (SAXParseException e) {
        } catch (SAXException e) {
        } catch (ParserConfigurationException e) {
        } catch (IOException e) {
            //If an error occurs then we're in trouble
            jsXe.exiterror(this, e, 1);
        }
        if (m_document != null) {
            return (Document)m_document.cloneNode(true);
        } else {
            return null;
        }
    }//}}}
    
    //{{{ getProperties()
    /**
     * Gets all properties associated with this document.
     * @return the document's properties
     */
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
    /**
     * A convenience method that returns the root element node of the document.
     * @return the root element node.
     */
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
     * @param parent the AdapterNode for the parent of the new node. Can be null.
     * @param node the Node object that the new AdapterNode should wrap
     * @return the new AdapterNode object
     */
    public AdapterNode newAdapterNode(AdapterNode parent, Node node) {
        AdapterNode newNode = null;
        if (node != null) {
            if (parent != null) {
                newNode = new AdapterNode(parent, node);
            } else {
                newNode = new AdapterNode(node);
            }
        }
        return newNode;
    }//}}}
    
    //{{{ newAdapterNode()
    /**
     * Creates a new AdapterNode in this document. This method is namespace aware.
     *
     * @param parent The parent of the node to create. Can be null.
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
        
        try {
            checkWellFormedness();
        } catch (SAXException saxe) {
            //nothing wrong here.
            //document is just not well-formed.
        } catch (ParserConfigurationException pce) {
            throw new IOException(pce.getMessage());
        }
        
        return m_parsedMode;
    }//}}}
    
    //{{{ isValid()
    /**
     * Returns true if the document is valid i.e. The document structure
     * is well-formed and conforms to a DTD/Schema.
     * @return true if the IS_VALIDATING property is true and the document is valid. false otherwise.
     * @throws IOException if there was a problem checking the validity of the document
     * @since jsXe 0.4 pre1
     */
    public boolean isValid() throws IOException {
        if (Boolean.valueOf(getProperty(IS_VALIDATING)).booleanValue()) {
            try {
                checkWellFormedness();
            } catch (SAXException e) {
                return false;
            } catch (ParserConfigurationException e) {
                throw new IOException(e.getMessage());
            }
            
            return (m_parseErrors.size() == 0 && m_parseFatalErrors.size() == 0);
        } else {
            return false;
        }
    }//}}}
    
    //{{{ hasCompletionInfo()
    /**
     * Gets whether this document has completion info obtained from a
     * DTD/Schema. This effectively lets you know if the document has a valid
     * DTD/Schema declaration or not.
     * @return true if the document has completion info.
     */
    public boolean hasCompletionInfo() {
        return (m_mappings.size() != 0);
    }//}}}

    //{{{ entityDeclared()
    /**
     * Determines if the entity was declared by the DTD/Schema.
     * @param entityName the name of the entity
     * @return true if the entity was declared in this document
     * @since jsXe 0.4 pre1
     */
    public boolean entityDeclared(String entityName) {
        if(m_document.getDoctype() != null) {
            NamedNodeMap entities = m_document.getDoctype().getEntities();
            return (entities.getNamedItem(entityName) != null);
        } else {
            return false;
        }
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
    /**
     * Called when the structure of the document has changed. This may be called
     * in the event that specs that will alter how the document is serialized or
     * parsed are changed.
     * @param location the location of the change. null if unknown
     */
    protected void fireStructureChanged(AdapterNode location) {
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            XMLDocumentListener listener = (XMLDocumentListener)iterator.next();
            listener.structureChanged(this, location);
        }
        m_syncedWithContent = false;
    }//}}}
    
    //{{{ Protected members
    
    //{{{ getElementDecl()
    /**
     * Gets an element declaration for a qualified name.
     * @param the qualified name
     * @since jsXe 0.4 pre1
     */
    protected ElementDecl getElementDecl(String name) {
        String prefix = MiscUtilities.getNSPrefixFromQualifiedName(name);
        if (prefix == null) {
            prefix = "";
        }
        CompletionInfo info = (CompletionInfo)m_mappings.get(prefix);
        
        if(info == null) {
            return null;
        } else {
            String lName = MiscUtilities.getLocalNameFromQualifiedName(name);
            ElementDecl decl = (ElementDecl)info.elementHash.get(lName);
            if(decl == null) {
                return null;
            } else {
                return decl.withPrefix(prefix);
            }
        }
    } //}}}
    
    //{{{ getCompletionInfoMappings()
    /**
     * Gets the namespace uri to CompletionInfo Mappings for this document.
     */
    protected HashMap getCompletionInfoMappings() {
        return m_mappings;
    }//}}}
    
    //}}}
    
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
        setProperty(IS_VALIDATING, "false");
    }//}}}
    
    //{{{ firePropertyChanged()
    
    private void firePropertyChanged(String key, String oldValue) {
        ListIterator iterator = listeners.listIterator();
        while (iterator.hasNext()) {
            XMLDocumentListener listener = (XMLDocumentListener)iterator.next();
            listener.propertyChanged(this, key, oldValue);
        }
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
                    //If an error occurs then we're in trouble
                    jsXe.exiterror(this, ioe, 1);
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
        config.setParameter(DOMSerializerConfiguration.ERROR_HANDLER, new SerializeErrorHandler());
        config.setFeature(IS_USING_SOFT_TABS, Boolean.valueOf(getProperty(IS_USING_SOFT_TABS)).booleanValue());
        
        return new DOMSerializer(config);
    }//}}}
    
    //{{{ parseDocument()
    /**
     * Parses the document with the current options. After this is called m_adapterNode must
     * be updated.
     * @since jsXe 0.4 pre1
     */
    public void parseDocument() throws SAXParseException, SAXException, ParserConfigurationException, IOException {
        Boolean validating = new Boolean(getProperty(IS_VALIDATING));
        
        //{{{ Parse using DocumentBuilder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setExpandEntityReferences(false);
        factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", new Boolean(false));
        factory.setAttribute("http://xml.org/sax/features/external-general-entities", new Boolean(false));
        factory.setAttribute("http://xml.org/sax/features/external-parameter-entities", new Boolean(false));
        factory.setAttribute("http://xml.org/sax/features/namespaces",new Boolean(true));
       // factory.setAttribute("http://apache.org/xml/features/validation/dynamic",new Boolean(true));
        factory.setAttribute("http://xml.org/sax/features/validation",validating);
        factory.setAttribute("http://apache.org/xml/features/validation/schema",validating);
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        builder.setErrorHandler(new ParseErrorHandler());
        
        if (m_entityResolver != null) {
            builder.setEntityResolver(m_entityResolver);
        }
        
        Document doc = builder.parse(new ContentManagerInputStream(m_content));
        doc.getDocumentElement().normalize();
        //}}}
        
        /*
        Parsing the document twice stinks.
        Need to fix this in the somewhat near future.
        */
        
        //{{{ Parse using SAXParser to get Completion Info
        SymbolTable symbolTable = new SymbolTable();
        XMLGrammarPoolImpl grammarPool = new XMLGrammarPoolImpl();

        SchemaHandler handler = new SchemaHandler(grammarPool);

        org.apache.xerces.parsers.SAXParser reader = new org.apache.xerces.parsers.SAXParser(symbolTable,grammarPool);
        try {
            reader.setFeature("http://xml.org/sax/features/validation",validating.booleanValue());
            reader.setFeature("http://apache.org/xml/features/validation/schema",validating.booleanValue());
            reader.setFeature("http://xml.org/sax/features/namespaces",true);
            reader.setErrorHandler(handler);
            reader.setEntityResolver(handler);
            reader.setContentHandler(handler);
            reader.setProperty("http://xml.org/sax/properties/declaration-handler",handler);
        } catch(SAXException se) {
            Log.log(Log.ERROR,this,se);
        }
        
        if (m_entityResolver != null) {
            reader.setEntityResolver(m_entityResolver);
        }
        
        m_mappings = new HashMap();
        reader.parse(new InputSource(new ContentManagerInputStream(m_content)));
        //}}}
        
        m_document=doc;
    }//}}}
    
    //{{{ getNoNamespaceCompletionInfo() method
    /**
     * Gets the completion info for the null namespace.
     * @since jsXe 0.4 pre1
     */
    private CompletionInfo getNoNamespaceCompletionInfo() {
        CompletionInfo info = (CompletionInfo)m_mappings.get("");
        if(info == null) {
            info = new CompletionInfo();
            m_mappings.put("",info);
        }
        return info;
    } //}}}
    
    //{{{ xsElementToElementDecl() method
    /**
     * Converts an XSElementDeclaration into an internal ElementDecl object and
     * adds it to a CompletionInfo object
     * @since jsXe 0.4 pre1
     */
    private void xsElementToElementDecl(CompletionInfo info, XSElementDeclaration element, ElementDecl parent) {
        String name = element.getName();

        if (parent != null) {
            if (parent.content == null) {
                parent.content = new HashSet();
            }
            parent.content.add(name);
        }

        if (info.elementHash.get(name) != null) {
            return;
        }

        ElementDecl elementDecl = new ElementDecl(info,name,null);
        info.addElement(elementDecl);

        XSTypeDefinition typedef = element.getTypeDefinition();

        if (typedef.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
            XSComplexTypeDefinition complex = (XSComplexTypeDefinition)typedef;

            XSParticle particle = complex.getParticle();
            if (particle != null) {
                XSTerm particleTerm = particle.getTerm();
                if(particleTerm instanceof XSWildcard) {
                    elementDecl.any = true;
                } else {
                    xsTermToElementDecl(info,particleTerm,elementDecl);
                }
            }

            XSObjectList attributes = complex.getAttributeUses();
            for (int i = 0; i < attributes.getLength(); i++) {
                XSAttributeUse attr = (XSAttributeUse)attributes.item(i);
                boolean required = attr.getRequired();
                XSAttributeDeclaration decl = attr.getAttrDeclaration();
                String attrName = decl.getName();
                String value = decl.getConstraintValue();
                // TODO: possible values
                String type = decl.getTypeDefinition().getName();
                if(type == null) {
                    type = "CDATA";
                }
                elementDecl.addAttribute(new ElementDecl.AttributeDecl(attrName,value,null,type,required));
            }
        }
    } //}}}

    //{{{ xsTermToElementDecl() method
    /**
     * Converts an XSTerm object to internal ElementDecl objects and adds them
     * to a CompletionInfo object
     * @since jsXe 0.4 pre1
     */
    private void xsTermToElementDecl(CompletionInfo info, XSTerm term, ElementDecl parent) {
        if(term instanceof XSElementDeclaration) {
            xsElementToElementDecl(info, (XSElementDeclaration)term, parent);
        } else {
            if (term instanceof XSModelGroup) {
                XSObjectList content = ((XSModelGroup)term).getParticles();
                for(int i = 0; i < content.getLength(); i++) {
                    XSTerm childTerm = ((XSParticleDecl)content.item(i)).getTerm();
                    xsTermToElementDecl(info,childTerm,parent);
                }
            }
        }
    }
    //}}}
    
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
        
        //{{{ getCharAt()
        public char getCharAt(int start) {
            if(start >= gapStart) {
                return text[start + gapEnd - gapStart];
            } else {
                if(start + 1 <= gapStart) {
                    return text[start];
                } else {
                    if (gapStart - start > 0) {
                        return text[start];
                    } else {
                        return text[gapEnd + start - 1];
                    }
                }
            }
        }//}}}
        
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
                    Log.log(Log.ERROR, this, uee);
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
    /**
     * An AdapterNodeListener is added to each AdapterNode in the tree so that
     * the XMLDocument is notified when the structure changes.
     */
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
    
    //{{{ SerializeErrorHandler class
    /**
     * Handles when parsing causes changes to the structure of the document.
     * Sounds like that should never happen doesn't it? Read the source.
     * Splitting CDATA sections is an example.
     */
    private class SerializeErrorHandler implements DOMErrorHandler {
        
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
    
    //{{{ ParseErrorHandler class
    /**
     * Handles capturing of validation errors.
     * @since jsXe 0.4 pre1
     */
    private class ParseErrorHandler implements ErrorHandler {
        
        //{{{ error
        public void error(SAXParseException exception) {
            Log.log(Log.WARNING, this, "parse error: "+exception.getMessage());
            m_parseErrors.add(exception);
        }//}}}
        
        //{{{ fatalError
        public void fatalError(SAXParseException exception) {
            Log.log(Log.WARNING, this, "parse fatalError: "+exception.getMessage());
            m_parseFatalErrors.add(exception);
        }//}}}
        
        //{{{ warning
        public void warning(SAXParseException exception) {
            Log.log(Log.NOTICE, this, "parse warning: "+exception.getMessage());
        }//}}}
        
    }//}}}
    
    //{{{ SchemaHandler class
    /**
     * Handles building of CompletionInfo Objects to obtain schema
     * introspection.
     * @author Slava Pestov
     * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
     * @since jsXe 0.4 pre1
     */
    private class SchemaHandler extends DefaultHandler implements DeclHandler {
        
        //{{{ SchemaHandler constructor
        public SchemaHandler(XMLGrammarPoolImpl grammarPool) {
            m_m_grammarPool = grammarPool;
        }//}}}
        
        //{{{ endDocument()
        public void endDocument() throws SAXException {
            Grammar grammar = getGrammarForNamespace(null);

            if (grammar != null) {
                CompletionInfo info = grammarToCompletionInfo(grammar);
                if (info != null) {
                    m_mappings.put("",info);
                }
            }
        } //}}}
        
        //{{{ startPrefixMapping() method
        public void startPrefixMapping(String prefix, String uri) {
            m_m_activePrefixes.put(prefix,uri);
        } //}}}

        //{{{ endPrefixMapping() method
        public void endPrefixMapping(String prefix) {
            String uri = (String)m_m_activePrefixes.get(prefix);
            // check for built-in completion info for this URI
            // (eg, XSL, XSD, XHTML has this).
            if (uri != null) {
                CompletionInfo info = CompletionInfo.getCompletionInfoForNamespace(uri);
                if (info != null) {
                    m_mappings.put(prefix,info);
                    return;
                }
            }

            Grammar grammar = getGrammarForNamespace(uri);

            if (grammar != null) {
                CompletionInfo info = grammarToCompletionInfo(grammar);
                if (info != null) {
                    m_mappings.put(prefix,info);
                }
            }
        } //}}}
        
        //{{{ elementDecl() method
        public void elementDecl(String name, String model) {
            ElementDecl element = getElementDecl(name);
            if(element == null) {
                CompletionInfo info = getNoNamespaceCompletionInfo();
                element = new ElementDecl(info,name,model);
                info.addElement(element);
            } else {
                element.setContent(model);
            }
        } //}}}

        //{{{ attributeDecl() method
        public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) {
            ElementDecl element = getElementDecl(eName);
            if (element == null) {
                CompletionInfo info = getNoNamespaceCompletionInfo();
                element = new ElementDecl(info,eName,null);
                info.addElement(element);
            }

            // as per the XML spec
            if (element.getAttribute(aName) != null)
                return;

            ArrayList values;

            if (type.startsWith("(")) {
                values = new ArrayList();

                StringTokenizer st = new StringTokenizer(type.substring(1,type.length() - 1),"|");
                while(st.hasMoreTokens()) {
                    values.add(st.nextToken());
                }
            } else {
                values = null;
            }

            boolean required = "#REQUIRED".equals(valueDefault);

            element.addAttribute(new ElementDecl.AttributeDecl(aName,value,values,type,required));
        } //}}}

        //{{{ internalEntityDecl()
        public void internalEntityDecl(String name, String value) {
            // this is a bit of a hack
            if (name.startsWith("%")) {
                return;
            }
            getNoNamespaceCompletionInfo().addEntity(EntityDecl.INTERNAL, name, value);
        } //}}}

        //{{{ externalEntityDecl()
        public void externalEntityDecl(String name, String publicId, String systemId) {
            if (name.startsWith("%")) {
                return;
            }

            getNoNamespaceCompletionInfo().addEntity(EntityDecl.EXTERNAL,name, publicId, systemId);
        } //}}}
    
        //{{{ Private members
        
        //{{{ grammarToCompletionInfo()
        private CompletionInfo grammarToCompletionInfo(Grammar grammar) {
            if (!(grammar instanceof XSGrammar)) {
                return null;
            }

            CompletionInfo info = new CompletionInfo();

            XSModel model = ((XSGrammar)grammar).toXSModel();

            XSNamedMap elements = model.getComponents(XSConstants.ELEMENT_DECLARATION);
            
            for(int i = 0; i < elements.getLength(); i++) {
                XSElementDeclaration element = (XSElementDeclaration)
                    elements.item(i);

                xsElementToElementDecl(info,element,null);
            }

            XSNamedMap attributes = model.getComponents(XSConstants.ATTRIBUTE_DECLARATION);
            
            for(int i = 0; i < attributes.getLength(); i++) {
                XSObject attribute = attributes.item(i);
                System.err.println("look! " + attribute);
                /* String name = element.getName();
                boolean empty = true;
                boolean any = true;
                List attributes = new ArrayList();
                Map attributeHash = new HashMap();
                Set content = new HashSet();
                info.addElement(new ElementDecl(info,name,empty,any,
                    attributes,attributeHash,content)); */
            }

            return info;
        } //}}}
        
        //{{{ getGrammarForNamespace()
        private Grammar getGrammarForNamespace(String uri) {
            XSDDescription schemaDesc = new XSDDescription();
            schemaDesc.setTargetNamespace(uri);
            Grammar grammar = m_m_grammarPool.getGrammar(schemaDesc);
            return grammar;
        } //}}}
        
        private XMLGrammarPoolImpl m_m_grammarPool;
        private HashMap m_m_activePrefixes = new HashMap();
        
        //}}}
        
    } //}}}
    
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
    
    private ArrayList m_parseErrors = new ArrayList();
    private ArrayList m_parseFatalErrors = new ArrayList();
    private ArrayList m_parseWarnings = new ArrayList();
    
    private EntityResolver m_entityResolver;
    private ArrayList listeners = new ArrayList();
    private Properties props = new Properties();
    
    /**
     * A namespace uri to CompletionInfo map used to hold completion info
     * for active namespaces
     */
    private HashMap m_mappings;
    
    private XMLDocAdapterListener docAdapterListener = new XMLDocAdapterListener();
    
    //}}}
}
