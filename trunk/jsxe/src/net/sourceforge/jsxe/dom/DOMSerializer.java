/*
DOMSerializer.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the code for the DOMSerializer class that will write an XML
document to an output using serialization.

This attempts to conform to the DOM3 implementation in Xerces. It conforms
to DOM3 as of Xerces 2.3.0

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
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMWriter;
import org.w3c.dom.ls.DOMWriterFilter;
import org.apache.xerces.dom3.DOMConfiguration;
import org.apache.xerces.dom3.DOMError;
import org.apache.xerces.dom3.DOMErrorHandler;
import org.apache.xerces.dom3.DOMLocator;
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
import java.util.Hashtable;
//}}}

//}}}

public class DOMSerializer implements DOMWriter {
    
    public DOMSerializer() {//{{{
        DOMSerializerConfiguration config = new DOMSerializerConfiguration();
        newLine = System.getProperty("line.separator");
    }//}}}
    
    public DOMSerializer(DOMSerializerConfiguration config) {//{{{
        this.config = config;
        newLine = System.getProperty("line.separator");
    }//}}}
    
    //{{{ Implemented DOMWriter methods
    
    public DOMConfiguration getConfig() {//{{{
        return config;
    }//}}}
    
    public String getEncoding() {//{{{
        return encoding;
    }//}}}
    
    public DOMWriterFilter getFilter() {//{{{
        return filter;
    }//}}}
    
    public String getNewLine() {//{{{
        return newLine;
    }//}}}
    
    public void setEncoding(String encoding) {//{{{
        if (encoding == null)
            this.encoding = "UTF-8";
        else
            this.encoding = encoding;
    }//}}}
    
    public void setFilter(DOMWriterFilter filter) {//{{{
        this.filter=filter;
    }//}}}
    
    public void setNewLine(String newLine) {//{{{
        if (newLine == null)
            this.newLine = System.getProperty("line.separator");
        else
            this.newLine=newLine;
    }//}}}
    
    public boolean writeNode(OutputStream out, Node wnode) {//{{{
        if (filter == null || filter.acceptNode(wnode) == 1) {
            
            OutputStreamWriter writer;
            
            DefaultDOMLocator loc = new DefaultDOMLocator(wnode, 1, 1, 0, "");
            
            try {
                
                writer = new OutputStreamWriter(out, encoding);
                
            } catch (UnsupportedEncodingException uee) {
                Object rawHandler = config.getParameter("error-handler");
                if (rawHandler != null) {
                
                    DOMErrorHandler handler = (DOMErrorHandler)rawHandler;
                    
                    DOMSerializerError error = new DOMSerializerError(loc, uee, DOMError.SEVERITY_FATAL_ERROR);
                    
                    if (!handler.handleError(error)) {
                        //The handler should quit because this error
                        //is fatal but if it doesn't then use the
                        //default encoding. Java is guaranteed to support
                        //UTF-8
                        try {
                            writer = new OutputStreamWriter(out, "UTF-8");
                        } catch (UnsupportedEncodingException uee) {}
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            
            serializeNode(writer, wnode);
            
        }
        return true;
    }//}}}
    
    public String writeToString(Node wnode) throws DOMException {//{{{
        StringWriter writer = new StringWriter();
        serializeNode(writer, wnode);
        return writer.toString();
    }//}}}
    
    //}}}
    
    public class DOMSerializerConfiguration implements DOMConfiguration {//{{{
        
        public DOMSerializerConfiguration() {//{{{
            
            //set the default parameters for DOMConfiguration
            setParameter("error-handler", null);
            
            //set the default boolean parameters for a DOMConfiguration
            setFeature("canonical-form",                false);
            setFeature("cdata-sections",                true);
            setFeature("comments",                      true);
            setFeature("datatype-normalization",        false);
            setFeature("discard-default-content",       true);
            setFeature("entities",                      false);
            setFeature("infoset",                       false);
            setFeature("namespaces",                    true);
            setFeature("namespace-declarations",        true);
            setFeature("normalize-characters",          false);
            setFeature("split-cdata-sections",          true);
            setFeature("validate",                      false);
            setFeature("validate-if-schema",            false);
            setFeature("whitespace-in-element-content", true);
            
            //set DOMSerializer specific features
            setFeature("format-output",                 false);
            
        }//}}}
        
        public DOMSerializerConfiguration(DOMConfiguration config) throws DOMException {//{{{
            //set the default parameters for DOMConfiguration
            setParameter("error-handler", config.getParameter("error-handler"));
            
            //set the default boolean parameters for a DOMConfiguration
            setParameter("canonical-form",                config.getParameter("canonical-form"));
            setParameter("cdata-sections",                config.getParameter("cdata-sections"));
            setParameter("comments",                      config.getParameter("comments"));
            setParameter("datatype-normalization",        config.getParameter("datatype-normalization"));
            setParameter("discard-default-content",       config.getParameter("discard-default-content"));
            setParameter("entities",                      config.getParameter("entities"));
            setParameter("infoset",                       config.getParameter("infoset"));
            setParameter("namespaces",                    config.getParameter("namespaces"));
            setParameter("namespace-declarations",        config.getParameter("namespace-declarations"));
            setParameter("normalize-characters",          config.getParameter("normalize-characters"));
            setParameter("split-cdata-sections",          config.getParameter("split-cdata-sections"));
            setParameter("validate",                      config.getParameter("validate"));
            setParameter("validate-if-schema",            config.getParameter("validate-if-schema"));
            setParameter("whitespace-in-element-content", config.getParameter("whitespace-in-element-content"));
            
            //set DOMSerializer specific features
            setFeature("format-output",                 false);
        }//}}}
        
        public boolean canSetParameter(String name, Object value) {///{{{
            if (value instanceof Boolean) {
                boolean booleanValue = ((Boolean)value).booleanValue();
                
                //couldn't think of a slicker way to do this
                //that was worth the time to implement
                //and extra processing.
                if (name == "canonical-form") {
                    return !booleanValue;
                }
                if (name == "cdata-sections") {
                    return true;
                }
                if (name == "comments") {
                    return true;
                }
                if (name == "datatype-normalization") {
                    return true;
                }
                if (name == "discard-default-content") {
                    return true;
                }
                if (name == "entities") {
                    return true;
                }
                if (name == "infoset") {
                    return true;
                }
                if (name == "namespaces") {
                    return true;
                }
                if (name == "namespace-declarations") {
                    return true;
                }
                if (name == "normalize-characters") {
                    return !booleanValue;
                }
                if (name == "split-cdata-sections") {
                    return true;
                }
                if (name == "validate") {
                    return !booleanValue;
                }
                if (name == "validate-if-schema") {
                    return !booleanValue;
                }
                if (name == "whitespace-in-element-content") {
                    return !booleanValue;
                }
                if (name == "formatting") {
                    return true;
                }
                return false;
            } else {
                if (name == "error-handler" && value instanceof DOMErrorHandler) {
                    return true;
                }
                return (name == "indent" && value instanceof Integer);
            }
        }//}}}
        
        //This should be fixed to perform better validation of the parameter name
        public Object getParameter(String name) throws DOMException {//{{{
            //all parameters (except error-handler) have default values so
            //if its not set then its not recognized.
            Object value = parameters.get(name);
            if (value != null) {
                return value;
            } else {
                if (name == "error-handler")
                    return null;
                throw new DOMException(DOMException.NOT_FOUND_ERR ,"NOT_FOUND_ERR: Parameter "+name+" not recognized");
            }
        }//}}}
        
        //This should be fixed to perform better validation of the parameter name
        public void setParameter(String name, Object value) throws DOMException {//{{{
            //all parameters (except error-handler) have default values so
            //if its not set then its not recognized.
            Object valueTest = parameters.get(name);
            if (valueTest == null) {
                if (name != "error-handler")
                    throw new DOMException(DOMException.NOT_FOUND_ERR, "NOT_FOUND_ERR: Parameter "+name+" not recognized");
            }
            if (canSetParameter(name, value)) {
                parameters.put(name, value);
            } else {
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "NOT_SUPPORTED_ERR: Parameter "+name+" not supported.");
            }
        }//}}}
        
        public boolean getFeature(String name) throws DOMException {//{{{
            if (name == "error-handler")
                throw new DOMException(DOMException.NOT_FOUND_ERR, "NOT_FOUND_ERR: "+name+" is not a feature.");
            return ((Boolean)getParameter(name)).booleanValue();
            
        }//}}}
        
        public void setFeature(String name, boolean value) {//{{{
            setParameter(name, new Boolean(value));
        }//}}}
        
        private Hashtable parameters = new Hashtable(16);
        private DOMErrorHandler handler;
    }//}}}
    
    //{{{ Private members
    
    private class DOMSerializerError implements DOMError {//{{{
        
        public DOMSerializerError(DOMLocator locator, Exception e, short s) {//{{{
            exception = e;
            location = locator;
            severity = s;
        }//}}}
        
        public DOMLocator getLocation() {//{{{
            return location;
        }//}}}
        
        public String getMessage() {//{{{
            return exception.getMessage();
        }//}}}
        
        public Object getRelatedData() {//{{{
            // fix me
            return null;
        }//}}}
        
        public Object getRelatedException() {//{{{
            return exception;
        }//}}}
        
        public short getSeverity() {//{{{
            return severity;
        }//}}}
        
        public String getType() {//{{{
            // fix me
            return "";
        }//}}}
        
        private Exception exception;
        private DOMLocator location;
        private short severity;
    }//}}}
    
    private void serializeNode(Writer writer, Node node) {//{{{
        rSerializeNode(writer, node, "");
    }//}}}
    
    private void rSerializeNode(Writer writer, Node node, String currentIndent) {//{{{
        
        boolean formatting = config.getFeature("formatting");
        
        if (filter == null || filter.acceptNode(node) == 1) {
            switch (node.getNodeType()) {
                case Node.DOCUMENT_NODE://{{{
                    String header = "<?xml version=\"1.0\" encoding="+encoding+"?>";
                    writer.write(header);
                    //write newLine no matter what here.
                    writer.write(newLine);
                    
                    NodeList nodes = node.getChildNodes();
                    if (nodes != null) {
                        for (int i=0; i<nodes.getLength(); i++) {
                            rSerializeNode(writer, nodes.item(i), currentIndent);
                        }
                    }
                    
                    break;//}}}
                case Node.ELEMENT_NODE://{{{
                    String nodeName = node.getNodeName();
                    
                    if (formatting)
                        writer.write(currentIndent);
                    
                    writer.write("<" + nodeName);
                    NamedNodeMap attr = node.getAttributes();
                    for (int i=0; i<attr.getLength(); i++) {
                        Node currentAttr = attr.item(i);
                        writer.write(" " + currentAttr.getNodeName() + "=\"" +
                            currentAttr.getNodeValue() + "\"");
                    }
                    NodeList children = node.getChildNodes();
                    if (children != null) {
                        if (children.getLength() > 0) {
                            
                            writer.write(">");
                            
                           // if (children.getLength() != 1 && children.item(0).getNodeType() == Node.TEXT_NODE)
                           //     writer.write(newLine);
                            
                            String indentUnit = "";
                            
                            if (formatting) {
                                //get the indent size and use it when serializing the children nodes.
                                Integer indentSize = (Integer)config.getParameter("indent");
                                if (indentSize != null) {
                                    int size = indentSize.intValue();
                                    for (int i=0; i < size; i++) {
                                        indentUnit += " ";
                                    }
                                }
                            }
                            
                            
                            for(int i=0; i<children.getLength();i++) {
                                rSerializeNode(writer, children.item(i), currentIndent + indentUnit);
                            }
                            
                           // if (children.getLength() != 1 && children.item(0).getNodeType() == Node.TEXT_NODE)
                           //     writer.write(indentLevel);
                            
                            writer.write("</" + nodeName + ">");
                        } else {
                            writer.write("/>");
                        }
                    }
                    
                    if (formatting)
                        writer.write(newLine);
                    break;//}}}
                case Node.TEXT_NODE://{{{
                    String text = node.getNodeValue();
                    if (formatting)
                        text = text.trim();
                    if (!text.equals("")) {
                        //pass through the text and add entities where we find
                        // '>' or '<' characters
                        for (int i=0; i<text.length();i++) {
                            //this must be first or it picks up the other
                            //entities.
                            if (text.charAt(i)=='&') {
                                String before = text.substring(0,i);
                                String after = text.substring(i+1);
                                text = before + "&amp;" + after;
                            }
                            if (text.charAt(i)=='>') {
                                String before = text.substring(0,i);
                                String after = text.substring(i+1);
                                text = before + "&gt;" + after;
                            }
                            if (text.charAt(i)=='<') {
                                String before = text.substring(0,i);
                                String after = text.substring(i+1);
                                text = before + "&lt;" + after;
                            }
                            if (text.charAt(i)=='\'') {
                                String before = text.substring(0,i);
                                String after = text.substring(i+1);
                                text = before + "&apos;" + after;
                            }
                            if (text.charAt(i)=='\"') {
                                String before = text.substring(0,i);
                                String after = text.substring(i+1);
                                text = before + "&quot;" + after;
                            }
                        }
                        writer.write(text);
                        if (formatting)
                            writer.write(newLine);
                    }
                    break;//}}}
                case Node.CDATA_SECTION_NODE://{{{
                    if (formatting)
                        writer.write(currentIndent);
                    writer.write("<![CDATA[" + node.getNodeValue() + "]]>");
                    if (formatting)
                        writer.write(newLine);
                    break;//}}}
                case Node.COMMENT_NODE://{{{
                    if (formatting)
                        writer.write(currentIndent);
                    writer.write(currentIndent+"<!--"+node.getNodeValue()+"-->");
                    if (formatting)
                        writer.write(newLine);
                    break;//}}}
                case Node.PROCESSING_INSTRUCTION_NODE://{{{
                    
                    if (formatting)
                        writer.write(currentIndent);
                    
                    writer.write("<?" + node.getNodeName() + " " + node.getNodeValue() + "?>");
                    if (formatting)
                        writer.write(newLine);
                    break;//}}}
                case Node.ENTITY_REFERENCE_NODE://{{{
                    writer.write("&" + node.getNodeName() + ";");
                    break;//}}}
                case Node.DOCUMENT_TYPE_NODE://{{{
                    DocumentType docType = (DocumentType)node;
                    
                    if (formatting)
                        writer.write(currentIndent);
                    
                    writer.write("<!DOCTYPE " + docType.getName());
                    if (docType.getPublicId() != null) {
                        writer.write(" PUBLIC \"" + docType.getPublicId() + "\" "); 
                    } else {
                        writer.write(" SYSTEM ");
                    }
                    writer.write("\"" + docType.getSystemId() + "\">");
                    if (formatting)
                        writer.write(newLine);
                    break;//}}}
            }
        }
    }//}}}
    
    private DOMSerializerConfiguration config;
    private DOMWriterFilter filter;
    private String newLine;
    private String encoding;
    //}}}
}
