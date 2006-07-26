/*
XMLDocument.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)

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

package net.sourceforge.jsxe.dom2;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.dom2.ls.*;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.util.MiscUtilities;
//}}}

//{{{ DOM classes
import org.w3c.dom.*;
import org.w3c.dom.events.*;
import org.xml.sax.EntityResolver;
//}}}

//{{{ Java base classes
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.net.URI;
//}}}

//{{{ Swing classes
import javax.swing.text.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
//}}}

//}}}

/**
 * The XMLDocument class represents an XML document as a  tree structure
 * that has nodes, implemented as XMLNodes, as well as text. Methods are
 * provided to allow user objects to interact with the XML document as text
 * or as a tree structure seamlessly.
 *
 * Properties of XMLDocuments are saved by jsXe as string values. And are loaded
 * later when if the document was loaded recently.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id: XMLDocument.java 999 2006-07-07 20:59:23Z ian_lewis $
 * @see XMLNode
 */
public class XMLDocument implements javax.swing.text.Document {
    
    //{{{ XMLDocument defined properties
    /**
     * The property key for the encoding of this XML document
     */
    public static final String ENCODING = DOMSerializerConfiguration.XML_ENCODING;
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
    public static final String IS_VALIDATING = "validating";
    /**
     * The property key for the property defining what line separator
     * to use when serializing a document. \n is always used for the internal
     * text but this line separator is used when serializing using the
     * serialize method.
     */
    public static final String LINE_SEPARATOR = "line-separator";
    //}}}
    
    //{{{ XMLDocument constructor
    /**
     * Creates a new XMLDocument for a document that can be read by the given
     * Reader.
     * @param uri the uri for the location of this document. Can be null.
     * @param reader the Reader object to read the XML document from.
     * @throws IOException if there was a problem reading the document
     */
    public XMLDocument(String uri, InputStream reader) throws IOException {
        this(uri, reader, null);
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
    public XMLDocument(String uri, InputStream reader, EntityResolver resolver) throws IOException {
        this(uri, reader, resolver, null);
    }//}}}
    
    //{{{ XMLDocument constructor
    /**
     * Creates a new XMLDocument for a document that can be read by the given
     * Reader.
     * @param uri the document uri
     * @param reader the Reader object to read the XML document from.
     * @param resolver the EntityResolver to use when resolving external
     *                 entities.
     * @param properties the properties to assign this XMLDocument on creation.
     * @throws IOException if there was a problem reading the document
     */
    public XMLDocument(String uri, InputStream reader, EntityResolver resolver, Map properties) throws IOException {
        m_entityResolver = resolver;
        setDefaultProperties();
        setURI(uri);
        
        //add properties one by one
        if (properties != null) {
            Iterator propertyNames = properties.keySet().iterator();
            while (propertyNames.hasNext()) {
                Object key = propertyNames.next();
                putProperty(key, properties.get(key));
            }
        }
        
        //TODO: Read the document from the input stream
        reader.close();
    }//}}}
    
    //{{{ swing.text.Document methods
    
    //{{{ addDocumentListener
    /**
     * Registers a change listener with the XMLDocument
     * @param listener the listener to register with this document
     */
    public void addDocumentListener(DocumentListener listener) {
        if (listener != null) {
            m_docListeners.add(listener);
        }
    }//}}}
    
    //{{{ addUndoableEditListener()
    
    public void addUndoableEditListener(UndoableEditListener listener) {
        if (listener != null) {
            m_undoListeners.add(listener);
        }
    }//}}}
    
    //{{{ createPosition()
    
    public Position createPosition(int offs) throws BadLocationException {
        if (offs < 0 || offs > m_content.getLength()) {
            //TODO: fix error messages
            throw new BadLocationException("createPosition(): Bad offset", offs);
        }
        
        final int offset = offs;
        return new Position() {
            public int getOffset() {
                return offset;
            }
        };
    }//}}}
    
    //{{{ getDefaultRootElement()
    
    public javax.swing.text.Element getDefaultRootElement() {
        //TODO: return root element
        return null;
    }//}}}
    
    //{{{ getEndPosition()
    
    public Position getEndPosition() {
        try {
            return createPosition(getLength()-1);
        } catch (BadLocationException e) {
            //Guaranteed to be good.
            return null;
        }
    }//}}}
    
    //{{{ getLength()
    /**
     * Gets the total number of characters in the document.
     * @return the length of the document
     */
    public int getLength() {
        return m_content.getLength();
    }//}}}
    
    //{{{ getProperty()
    
    public Object getProperty(Object key) {
        return m_properties.get(key);
    }//}}}
    
    //{{{ getRootElements()
    
    public javax.swing.text.Element[] getRootElements() {
        //TODO: return root nodes
        return null;
    }//}}}
    
    //{{{ getStartPosition()
    
    public Position getStartPosition() {
        try {
            return createPosition(0);
        } catch (BadLocationException e) {
            //Guaranteed to be good.
            return null;
        }
    }//}}}
    
    //{{{ getText()
    public String getText(int offset, int length) throws BadLocationException {
        if (offset < 0 || length < 0 || offset + length > m_content.getLength()) {
            //TODO: fix error messages
            throw new BadLocationException("insertString(): Bad Offset", offset);
        }
        
        return m_content.getText(offset,length);
    }//}}}
    
    //{{{ getText()
    
    public void getText(int offset, int length, Segment txt) throws BadLocationException {
        
        if (offset < 0 || length < 0 || offset + length > m_content.getLength()) {
            //TODO: fix error messages
            throw new BadLocationException("insertString(): Bad Offset", offset);
        }
        
        m_content.getText(offset, length, txt);
    }//}}}
    
    //{{{ insertString()
    /**
     * <p>Inserts a string of content. This will cause a DocumentEvent of type
     *    DocumentEvent.EventType.INSERT to be sent to the registered
     *    DocumentListers, unless an exception is thrown. The DocumentEvent will
     *    be delivered by calling the insertUpdate method on the
     *    DocumentListener. The offset and length of the generated DocumentEvent
     *    will indicate what change was actually made to the Document.</p>
     *
     * <p>If the Document structure changed as result of the insertion, the
     *    details of what Elements were inserted and removed in response to the 
     *    change will also be contained in the generated DocumentEvent. It is up 
     *    to the implementation of a Document to decide how the structure should 
     *    change in response to an insertion.</p>
     * 
     * <p>If the Document supports undo/redo, an UndoableEditEvent will also be
     *    generated.</p>
     * @param offset the offset into the document to insert the content >= 0.
     *               All positions that track change at or after the given
     *               location will move.
     * @param str the string to insert
     * @param a the attributes to associate with the inserted content.
     *          This attribute is ignored by XMLDocuments.
     */
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        if (offset < 0 || str.length() < 0 || offset + str.length() > m_content.getLength()) {
            //TODO: fix error messages
            throw new BadLocationException("insertString(): Bad Offset", offset);
        }
        
        m_content.insert(offset, str);
        
        //TODO: document structure must change
        //TODO: notify listeners
        //TODO: implement undo
    }//}}}
    
    //{{{ putProperty()
    /**
     * Add a property to the XMLDocument.
     * They are saved in memory as objects however, jsXe saves properties as
     * Strings when saving the Document to the recent buffers file, and when
     * the document is opened later the property will be loaded as a String.
     * Generally String properties are stored here.
     */
    public void putProperty(Object key, Object value) {
        m_properties.put(key, value);
    }//}}}
    
    //{{{ remove()
    
    public void remove(int offs, int len) throws BadLocationException {
        if (offs < 0 || len < 0 || offs + len > m_content.getLength()) {
            //TODO: fix error messages
            throw new BadLocationException("insertString(): Bad Offset", offs);
        }
        
        m_content.remove(offs, len);
        
        //TODO: document structure must change
        //TODO: notify listeners
        //TODO: implement undo
    }//}}}
    
    //{{{ removeDocumentListener()
    
    public void removeDocumentListener(DocumentListener listener) {
        if (listener != null) {
            m_docListeners.remove(listener);
        }
    }//}}}
    
    //{{{ removeUndoableEditListener()
    
    public void removeUndoableEditListener(UndoableEditListener listener) {
        if (listener != null) {
            m_undoListeners.remove(listener);
        }
    }//}}}
    
    //{{{ render()
    
    public void render(Runnable r) {
        //TODO: implement concurrency support.
    }//}}}
    
    //}}}
    
    //{{{ getDocumentType()
    
    public XMLDocumentType getDocumentType() {
        DocumentType docType = m_document.getDoctype();
        if (docType != null) {
            return (XMLDocumentType)docType.getUserData(XMLNode.USER_DATA_KEY);
        } else {
            return null;
        }
    }//}}}
    
    //{{{ getProperties()
    
    public Map getProperties() {
        return m_properties;
    }//}}}
    
    //{{{ setURI()
    /**
     * Sets the URI for the location of this document.
     * @param uri the uri specifying the location of this document. Can be null.
     */
    public void setURI(String uri) {
        m_document.setDocumentURI(uri);
    }//}}}
    
    //{{{ getURI()
    /**
     * Gets the URI for the location of this document.
     * @return the uri specifying the location of this document. Can be null.
     */
    public String getURI() {
        return m_document.getDocumentURI();
    }//}}}
    
    //{{{ setStandalone()
    /**
     * An attribute specifying, as part of the XML declaration, whether this
     * document is standalone. This is false when unspecified.
     */
    public void setStandalone(boolean standalone) throws DOMException {
        m_document.setXmlStandalone(standalone);
    }//}}}
    
    //{{{ isStandalone()
    /**
     * An attribute specifying, as part of the XML declaration, whether this
     * document is standalone. This is false when unspecified.
     */
    public boolean isStandalone() {
        return m_document.getXmlStandalone();
    }//}}}
    
    //{{{ isReadOnly()
    /**
     * Returns whether this document is currently read only.
     */
    public boolean isReadOnly() {
        return m_readOnly;
    }//}}}
    
    //{{{ setReadOnly()
    /**
     * Sets this document to read only. Any attempts
     * to change any attribute of this document or
     * child nodes of this document will result in
     * DOMExceptions being thrown.
     * @param readOnly the value of the readOnly property
     */
    public void setReadOnly(boolean readOnly) {
        m_readOnly = readOnly;
    }//}}}
    
    //{{{ Node Factory methods
    
    //{{{ newElementNode()
    /**
     * Create a new XMLElement node with the given name.
     * @param name the qualified name of the new node.
     */
    public XMLElement newElementNode(String name) throws DOMException {
        return new XMLElement(m_document.createElementNS("", name));
    }//}}}
    
    //{{{ newAttributeNode()
    /**
     * Create a new XMLAttribute node with the given name.
     * @param name the qualified name of the new node.
     */
    public XMLAttribute newAttributeNode(String name) throws DOMException {
        return new XMLAttribute(m_document.createAttributeNS("", name));
    }//}}}
    
    //{{{ newErrorNode()
    /**
     * Creates a new error node.
     * @param message the error message
     * @param warning true if the node is a warning, false if it's an error
     */
    public XMLError newErrorNode(String message, boolean warning) {
        return new XMLError(this, null, message, warning);
    }//}}}
    
    //}}}
    
    //{{{ Private members
    
    //{{{ setDefaultProperties()
    private void setDefaultProperties() {
        putProperty(FORMAT_XML, "false");
        putProperty(IS_USING_SOFT_TABS, "false");
        putProperty(WS_IN_ELEMENT_CONTENT, "true");
        putProperty(ENCODING, "UTF-8");
        putProperty(INDENT, "4");
        putProperty(IS_VALIDATING, "false");
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
    
    //{{{ ContentManagerWriter class
    /**
     * Character Stream used to write to the content manager when
     * serialized. Used when syncing the source with the current Document.
     */
    private static class ContentManagerWriter extends Writer {
        
        //{{{ ContentManagerWriter constructor
        public ContentManagerWriter(ContentManager content) {
            m_m_content = content;
        }//}}}
        
        //{{{ write()
        public void write(char[] cbuf) throws IOException {
            if (m_m_closed) {
                throw new IOException("ContentManagerWriter is closed");
            }
            m_m_content.insert(m_m_content.getLength(), new String(cbuf));
        }//}}}
        
        //{{{ write()
        public void write(char[] cbuf, int off, int len) throws IOException {
            if (m_m_closed) {
                throw new IOException("ContentManagerWriter is closed");
            }
            m_m_content.insert(m_m_content.getLength(), new String(cbuf, off, len));
        }//}}}
        
        //{{{ write()
        public void write(int b) throws IOException {
            if (m_m_closed) {
                throw new IOException("ContentManagerWriter is closed");
            }
            char []carray = { (char)b };
            m_m_content.insert(m_m_content.getLength(), new String(carray));
        }//}}}
        
        //{{{ write()
        public void write(String str) throws IOException {
            if (m_m_closed) {
                throw new IOException("ContentManagerWriter is closed");
            }
            m_m_content.insert(m_m_content.getLength(), str);
        }//}}}
        
        //{{{ flush()
        public void flush() throws IOException {
            if (m_m_closed) {
                throw new IOException("ContentManagerWriter is closed");
            }
            //writes happen immediately so this does nothing.
        }//}}}
        
        //{{{ close()
        public void close() {
            //honoring the contract for Writer class
            m_m_closed = true;
        }//}}}
        
        //{{{ Private members
        private ContentManager m_m_content;
        private boolean m_m_closed = false;
        //}}}
    }//}}}
    
    private org.w3c.dom.Document m_document;
    
    private ContentManager m_content;
    
    private HashMap m_properties = new HashMap();
    
    private ArrayList m_docListeners = new ArrayList();
    private ArrayList m_undoListeners = new ArrayList();
    
    /**
     * The entity resolver to use when resolving external entities
     * in this document.
     */
    private EntityResolver m_entityResolver;
    
    private boolean m_readOnly;
    //}}}
    
}
