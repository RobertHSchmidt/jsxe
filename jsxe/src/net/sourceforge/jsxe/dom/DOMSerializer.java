/*
DOMSerializer.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the code for the DOMSerializer class that will write an XML
document to an output using serialization. Probobly the most complex and
nasty class in jsXe.

This attempts to conform to the DOM3 implementation in Xerces. It tries to
conform to DOM3 as of Xerces 2.6.0. I'm not one to stay on the bleeding edge
but it is as close to a standard interface for load & save as you can get
and I didn't want to work around the fact that current serializers aren't
very good.

This file written by Ian Lewis (IanLewis@member.fsf.org)

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

//{{{ DOM classes
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;
import org.w3c.dom.ls.LSOutput;
import org.apache.xerces.dom3.DOMConfiguration;
import org.apache.xerces.dom3.DOMLocator;
import org.apache.xerces.dom3.DOMError;
import org.apache.xerces.dom3.DOMErrorHandler;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
//}}}

//}}}

/**
 * <p>An implementation of the DOM3 LSSerializer interface. This class supports
 * everything that is supported by the DOMSerializerConfiguration class. Clients
 *  can check if a feature is supported by calling canSetParameter() on the
 * appropriate DOMSerializerConfiguration object.</p>
 *
 * @author <a href="mailto:IanLewis at member dot fsf dot org">Ian Lewis</a>
 * @version $Id$
 */
public class DOMSerializer implements LSSerializer {
    
    /**
     * <p>Creates a default DOMSerializer using the default options.</p>
     */
    public DOMSerializer() {//{{{
        config = new DOMSerializerConfiguration();
        m_newLine = System.getProperty("line.separator");
    }//}}}
    
    /**
     * <p>Creates a DOMSerializer that uses the configuration specified.</p>
     * @param config The configuration to be used by this DOMSerializer object
     */
    public DOMSerializer(DOMSerializerConfiguration config) {//{{{
        this.config = config;
        m_newLine = System.getProperty("line.separator");
    }//}}}
    
    //{{{ Implemented LSSerializer methods
    
    public DOMConfiguration getConfig() {//{{{
        return config;
    }//}}}
    
    public LSSerializerFilter getFilter() {//{{{
        return m_filter;
    }//}}}
    
    public String getNewLine() {//{{{
        return m_newLine;
    }//}}}
    
    public void setFilter(LSSerializerFilter filter) {//{{{
        m_filter=filter;
    }//}}}
    
    public void setNewLine(String newLine) {//{{{
        m_newLine=newLine;
    }//}}}
    
    public boolean write(Node nodeArg, LSOutput destination) {//{{{
        if (m_filter == null || m_filter.acceptNode(nodeArg) == 1) {
            
            //{{{ try to get the Writer object for our destination
            Writer writer = destination.getCharacterStream();
            String encoding = null;
            
            if (writer == null) {
                //no character stream specified, try the byte stream.
                OutputStream out = destination.getByteStream();
                if (out != null) {
                    
                    try {
                        writer = new OutputStreamWriter(out, destination.getEncoding());
                        encoding = destination.getEncoding();
                    } catch (UnsupportedEncodingException uee) {
                        DefaultDOMLocator loc = new DefaultDOMLocator(nodeArg, 1, 1, 0, "");
                        throwError(loc, "unsupported-encoding", uee);
                        //This is a fatal error, quit.
                        return false;
                    }
                } else {
                    //no char stream or byte stream, try the uri
                    String id = destination.getSystemId();
                    if (id != null) {
                        
                        try {
                            //We use URL since outputing to any other type of URI
                            //is not possible.
                            URL uri = new URL(id);
                            URLConnection con = uri.openConnection();
                            
                            try  {
                                //We want to try to output to the URI
                                con.setDoOutput(true);
                                //I don't see a problem with using caches
                                //do you?
                                con.setUseCaches(true);
                            } catch (IllegalStateException ise) {
                                //we are guaranteed to not be connected
                            }
                            
                            con.connect();
                            
                            writer = new OutputStreamWriter(con.getOutputStream(), destination.getEncoding());
                            
                        } catch (MalformedURLException mue) {
                            DefaultDOMLocator loc = new DefaultDOMLocator(nodeArg, 1, 1, 0, "");
                            throwError(loc, "bad-uri", mue);
                            //this is a fatal error
                            return false;
                        } catch (IOException ioe) {
                            DefaultDOMLocator loc = new DefaultDOMLocator(nodeArg, 1, 1, 0, "");
                            throwError(loc, "io-error", ioe);
                            //this is a fatal error
                            return false;
                        }
                        
                    } else {
                        DefaultDOMLocator loc = new DefaultDOMLocator(nodeArg, 1, 1, 0, "");
                        throwError(loc, "no-output-specified", null);
                        //this is a fatal error
                        return false;
                    }
                }
            }//}}}
            
            try {
                serializeNode(writer, nodeArg, encoding);
                return true;
            } catch (DOMSerializerException dse) {
                Object rawHandler = config.getParameter(DOMSerializerConfiguration.ERROR_HANDLER);
                if (rawHandler != null) {
                    DOMErrorHandler handler = (DOMErrorHandler)rawHandler;
                    DOMError error = dse.getError();
                    handler.handleError(error);
                }
                //This is a fatal error, quit.
            }
        }
        return false;
    }//}}}
    
    public String writeToString(Node nodeArg) throws DOMException {//{{{
        StringWriter writer = new StringWriter();
        try {
            serializeNode(writer, nodeArg);
            //flush the output-stream. Without this
            //files are sometimes not written at all.
            writer.flush();
        } catch (DOMSerializerException dse) {}
        return writer.toString();
    }//}}}

    public boolean writeToURI(Node nodeArg, java.lang.String uri) {//{{{
        return write(nodeArg, new DOMOutput(uri, "UTF-8"));
    }//}}}
    
    //}}}
    
    /**
     * A temprorary main method to test the serialization class
     */
    public static void main(String[] args) {//{{{
        
    }//}}}
    
    //{{{ Private members
    
    private class DOMSerializerError implements DOMError {//{{{
        
        public DOMSerializerError(DOMLocator locator, Exception e, short s, String type) {//{{{
            m_exception = e;
            m_location = locator;
            m_severity = s;
            m_type = type;
        }//}}}
        
        public DOMLocator getLocation() {//{{{
            return m_location;
        }//}}}
        
        public String getMessage() {//{{{
            return m_exception.getMessage();
        }//}}}
        
        public Object getRelatedData() {//{{{
            return m_location.getRelatedNode();
        }//}}}
        
        public Object getRelatedException() {//{{{
            return m_exception;
        }//}}}
        
        public short getSeverity() {//{{{
            return m_severity;
        }//}}}
        
        public String getType() {//{{{
            return m_type;
        }//}}}
        
        private Exception m_exception;
        private DOMLocator m_location;
        private short m_severity;
        private String m_type;
    }//}}}
    
    private void serializeNode(Writer writer, Node node) throws DOMSerializerException {//{{{
        serializeNode(writer, node, null);
    }//}}}
    
    /**
     * Serializes the node to the writer specified
     */
    private void serializeNode(Writer writer, Node node, String encoding) throws DOMSerializerException {//{{{
        rSerializeNode(writer, node, encoding, "", 1, 1, 0);
    }//}}}
    
    /**
     * Designed to be called recursively and maintain the state of the
     * serialization.
     */
    private void rSerializeNode(Writer writer, Node node, String encoding, String currentIndent, int line, int column, int offset) throws DOMSerializerException {//{{{
        
        boolean formatting = config.getFeature(DOMSerializerConfiguration.FORMAT_XML);
        boolean whitespace = config.getFeature(DOMSerializerConfiguration.WS_IN_ELEMENT_CONTENT);
        
        //This is used many times below as a temporary variable.
        String str = "";
        
        if (m_filter == null || m_filter.acceptNode(node) == 1) {
            switch (node.getNodeType()) {
                case Node.DOCUMENT_NODE://{{{
                    String header = "<?xml version=\"1.0\"";
                    if (encoding != null)
                        header += " encoding=\""+encoding+"\"";
                    header +="?>";
                    doWrite(writer, header, node, line, column, offset);
                    offset += header.length();
                    column += header.length();
                    
                    //if not formatting write newLine here.
                    if (!formatting) {
                        column = 0;
                        line += 1;
                        doWrite(writer, m_newLine, node, line, column, offset);
                        offset += m_newLine.length();
                    }
                    
                    NodeList nodes = node.getChildNodes();
                    if (nodes != null) {
                        for (int i=0; i<nodes.getLength(); i++) {
                            rSerializeNode(writer, nodes.item(i), encoding, currentIndent, line, column, offset);
                        }
                    }
                    
                    break;//}}}
                case Node.ELEMENT_NODE://{{{
                    String nodeName   = node.getLocalName();
                    String nodePrefix = node.getPrefix();
                    
                    if (formatting) {
                        //set to zero here for error handling (if doWrite throws exception).
                        column = 0;
                        str = m_newLine + currentIndent;
                        doWrite(writer, str, node, line, column, offset);
                        column += currentIndent.length();
                        offset += str.length();
                    }
                    
                    if (config.getFeature(DOMSerializerConfiguration.NAMESPACES) && nodePrefix != null) {
                        str = "<" + nodePrefix + ":" + nodeName;
                    } else {
                        str = "<" + nodeName;
                    }
                    
                    doWrite(writer, str, node, line, column, offset);
                    column += str.length();
                    offset += str.length();
                    
                    NamedNodeMap attr = node.getAttributes();
                    for (int i=0; i<attr.getLength(); i++) {
                        Attr currentAttr = (Attr)attr.item(i);
                        boolean writeAttr = false;
                        
                        /*
                        if we discard default content check if the attribute
                        was specified in the original document.
                        */
                        if (config.getFeature(DOMSerializerConfiguration.DISCARD_DEFAULT_CONTENT)) {
                            if (currentAttr.getSpecified()) {
                                writeAttr = true;
                            }
                        } else {
                            writeAttr = true;
                        }
                        
                        if (writeAttr) {
                            str = " " + currentAttr.getNodeName() + "=\"" + currentAttr.getNodeValue() + "\"";
                            doWrite(writer, str, node, line, column, offset);
                            column += str.length();
                            offset += str.length();
                        }
                    }
                    
                    NodeList children = node.getChildNodes();
                    if (children != null) {
                        
                        //check if element is empty or has
                        //only whitespace-only nodes
                        boolean elementEmpty = false;
                        if (children.getLength() <= 0) {
                            elementEmpty = true;
                        } else {
                            if (!config.getFeature(DOMSerializerConfiguration.WS_IN_ELEMENT_CONTENT)) {
                                boolean hasWSOnlyElements = true;
                                for(int i=0; i<children.getLength();i++) {
                                    hasWSOnlyElements = hasWSOnlyElements &&
                                        children.item(i).getNodeType()==Node.TEXT_NODE &&
                                        children.item(i).getNodeValue().trim().equals("");
                                }
                                elementEmpty = formatting && hasWSOnlyElements;
                            }
                        }
                        if (!elementEmpty) {
                            
                            str = ">";
                            doWrite(writer, str, node, line, column, offset);
                            column += str.length();
                            offset += str.length();
                            
                            String indentUnit = "";
                            
                            if (formatting) {
                                if (config.getFeature(DOMSerializerConfiguration.SOFT_TABS)) {
                                    //get the indent size and use it when serializing the children nodes.
                                    Integer indentSize = (Integer)config.getParameter("indent");
                                    if (indentSize != null) {
                                        int size = indentSize.intValue();
                                        for (int i=0; i < size; i++) {
                                            indentUnit += " ";
                                        }
                                    }
                                } else {
                                    indentUnit = "\t";
                                }
                            }
                            
                            
                            for(int i=0; i<children.getLength();i++) {
                                rSerializeNode(writer, children.item(i), encoding, currentIndent + indentUnit, line, column, offset);
                            }
                            
                            //don't add a new line if there is only one text node child.
                            if (formatting && !(children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE)) {
                                //set to zero here for error handling (if doWrite throws exception).
                                column = 0;
                                str = m_newLine + currentIndent;
                                doWrite(writer, str, node, line, column, offset);
                                column += currentIndent.length();
                                offset += str.length();
                            }
                            if (config.getFeature(DOMSerializerConfiguration.NAMESPACES) && nodePrefix != null) {
                                str = "</" + nodePrefix + ":" +nodeName + ">";
                            } else {
                                str = "</" + nodeName + ">";
                            }
                            doWrite(writer, str, node, line, column, offset);
                            column += str.length();
                            offset += str.length();
                            
                        } else {
                            str = "/>";
                            doWrite(writer, str, node, line, column, offset);
                            column += str.length();
                            offset += str.length();
                        }
                    }
                    break;//}}}
                case Node.TEXT_NODE://{{{
                    String text = node.getNodeValue();
                    //formatting implies no whitespace
                    //but to be explicit...
                    if (!whitespace || formatting) {
                        text = text.trim();
                    }
                    if (!text.equals("")) {
                        if (formatting) {
                            if (node.getNextSibling()!=null || node.getPreviousSibling()!=null) {
                                line++;
                                column=0;
                                doWrite(writer, m_newLine, node, line, column, offset);
                                offset += m_newLine.length();
                            }
                        }
                        //pass through the text and add entities where we find
                        // '>' or '<' characters
                        for (int i=0; i<text.length();i++) {
                            //this must be first or it picks up the other
                            //entities.
                            str = text.substring(i, i+1);
                            if (str.equals("&")) {
                                str = "&amp;";
                            }
                            if (str.equals(">")) {
                                str = "&gt;";
                            }
                            if (str.equals("<")) {
                                str = "&lt;";
                            }
                            if (str.equals("\'")) {
                                str = "&apos;";
                            }
                            if (str.equals("\"")) {
                                str = "&quot;";
                            }
                            if (str.equals(m_newLine)) {
                                line++;
                                column=0;
                                doWrite(writer, m_newLine, node, line, column, offset);
                                offset += m_newLine.length();
                            } else {
                                doWrite(writer, str, node, line, column, offset);
                                column += str.length();
                                offset += str.length();
                            }
                        }
                    }
                    break;//}}}
                case Node.CDATA_SECTION_NODE://{{{
                    if (config.getFeature(DOMSerializerConfiguration.CDATA_SECTIONS)) {
                        if (formatting) {
                            //set to zero here for error handling (if doWrite throws exception)
                            column = 0;
                            str = m_newLine + currentIndent;
                            doWrite(writer, str, node, line, column, offset);
                            column += currentIndent.length();
                            offset += str.length();
                        }
                        str = "<![CDATA[" + node.getNodeValue() + "]]>";
                        doWrite(writer, str, node, line, column, offset);
                        column += str.length();
                        offset += str.length();
                    }
                    break;//}}}
                case Node.COMMENT_NODE://{{{
                    if (config.getFeature("comments")) {
                        if (formatting) {
                            //set to zero here for error handling (if doWrite throws exception)
                            column = 0;
                            str = m_newLine + currentIndent;
                            doWrite(writer, str, node, line, column, offset);
                            column += currentIndent.length();
                            offset += str.length();
                        }
                        str = currentIndent+"<!--"+node.getNodeValue()+"-->";
                        doWrite(writer, str, node, line, column, offset);
                        column += str.length();
                        offset += str.length();
                    }
                    break;//}}}
                case Node.PROCESSING_INSTRUCTION_NODE://{{{
                    
                    if (formatting) {
                        //set to zero here for error handling (if doWrite throws exception)
                        column = 0;
                        str = m_newLine + currentIndent;
                        doWrite(writer, currentIndent, node, line, column, offset);
                        column += currentIndent.length();
                        offset += str.length();
                    }
                    
                    str = "<?" + node.getNodeName() + " " + node.getNodeValue() + "?>";
                    doWrite(writer, str, node, line, column, offset);
                    column += str.length();
                    offset += str.length();
                    
                    break;//}}}
                case Node.ENTITY_REFERENCE_NODE://{{{
                    str = "&" + node.getNodeName() + ";";
                    doWrite(writer, str, node, line, column, offset);
                    column += str.length();
                    offset += str.length();
                    break;//}}}
                case Node.DOCUMENT_TYPE_NODE://{{{
                    DocumentType docType = (DocumentType)node;
                    
                    if (formatting) {
                        //set to zero here for error handling (if doWrite throws exception).
                        column = 0;
                        str = m_newLine + currentIndent;
                        doWrite(writer, str, node, line, column, offset);
                        column += currentIndent.length();
                        offset += str.length();
                    }
                    
                    str = "<!DOCTYPE " + docType.getName();
                    doWrite(writer, str, node, line, column, offset);
                    column += str.length();
                    offset += str.length();
                    if (docType.getPublicId() != null) {
                        str = " PUBLIC \"" + docType.getPublicId() + "\" ";
                        doWrite(writer, str, node, line, column, offset);
                        column += str.length();
                        offset += str.length();
                    } else {
                        if (docType.getSystemId() != null) {
                            str = " SYSTEM \"" + docType.getSystemId() + "\"";
                            doWrite(writer, str, node, line, column, offset);
                            column += str.length();
                            offset += str.length();
                        }
                    }
                    
                    String internalSubset = docType.getInternalSubset();
                    if (internalSubset != null && !internalSubset.equals("")) {
                        str = " [ "+internalSubset+" ]";
                        doWrite(writer, str, node, line, column, offset);
                        column += str.length();
                        offset += str.length();
                    }
                    
                    str = ">";
                    doWrite(writer, str, node, line, column, offset);
                    column += str.length();
                    offset += str.length();
                    
                    break;//}}}
            }
        }
    }//}}}
    
    /**
     * Performs an actual write and implements error handling.
     */
    private void doWrite(Writer writer, String str, Node wnode, int line, int column, int offset) throws DOMSerializerException {//{{{
        try {
            writer.write(str, 0, str.length());
            //flush the output-stream. Without this
            //files are sometimes not written at all.
            writer.flush();
        } catch (IOException ioe) {
            
            DefaultDOMLocator loc = new DefaultDOMLocator(wnode, line, column, offset, "");
            
            DOMSerializerError error = new DOMSerializerError(loc, ioe, DOMError.SEVERITY_FATAL_ERROR, "io-error");
            Object rawHandler = config.getParameter(DOMSerializerConfiguration.ERROR_HANDLER);
            if (rawHandler != null) {
                
                DOMErrorHandler handler = (DOMErrorHandler)rawHandler;
                if (!handler.handleError(error)) {
                    //fatal error. Don't continue.
                    throw new DOMSerializerException(error);
                }
            } else {
                throw new DOMSerializerException(error);
            }
        }
    }//}}}
    
    /**
     * Throws an error, notifying the ErrorHandler object if necessary.
     */
    private void throwError(DOMLocator loc, String type, Exception e) {//{{{
        Object rawHandler = config.getParameter(DOMSerializerConfiguration.ERROR_HANDLER);
        if (rawHandler != null) {
            
            DOMErrorHandler handler = (DOMErrorHandler)rawHandler;
            DOMSerializerError error = new DOMSerializerError(loc, e, DOMError.SEVERITY_FATAL_ERROR, type);
            handler.handleError(error);
        }
        
    }//}}}
    
    private String normalizeCharacters(String text) {//{{{
        return text;
    }//}}}
    
    private DOMSerializerConfiguration config;
    private LSSerializerFilter m_filter;
    private String m_newLine;
    //}}}
}