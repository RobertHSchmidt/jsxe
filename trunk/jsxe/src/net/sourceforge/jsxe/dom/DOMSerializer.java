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

This attempts to conform to the DOM3 implementation in Xerces. It conforms
to DOM3 as of Xerces 2.3.0. I'm not one to stay on the bleeding edge but
there is as close to a standard interface for load & save as you can get and I
didn't want to work around the fact that current serializers aren't very good.
This class name will have to changed because DOMWriter was changed to
DOMSerializer among other changes.

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
import java.util.Vector;
//}}}

//}}}

public class DOMSerializer implements DOMWriter {
    
    public DOMSerializer() {//{{{
        config = new DOMSerializerConfiguration();
        setEncoding("UTF-8");
        newLine = System.getProperty("line.separator");
    }//}}}
    
    public DOMSerializer(DOMSerializerConfiguration config) {//{{{
        this.config = config;
        setEncoding("UTF-8");
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
        //This method is my least favorite part of this class.
        //How the heck are we supposed to create error locations if
        //they can put anything in as a newLine?
        if (newLine == null)
            this.newLine = System.getProperty("line.separator");
        else
            this.newLine=newLine;
    }//}}}
    
    public boolean writeNode(OutputStream out, Node wnode) {//{{{
        if (filter == null || filter.acceptNode(wnode) == 1) {
            
            OutputStreamWriter writer = new OutputStreamWriter(out);
            
            if (writer.getEncoding() != "UTF-8") {
                try {
                    writer = new OutputStreamWriter(out, "UTF-8");
                } catch (UnsupportedEncodingException uee2) {}
            }
            
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
                        return false;
                    }
                } else {
                    return false;
                }
            }
            try {
                serializeNode(writer, wnode);
            } catch (DOMSerializerException dse) {
                
                return false;
            }
            
        }
        return true;
    }//}}}
    
    public String writeToString(Node wnode) throws DOMException {//{{{
        StringWriter writer = new StringWriter();
        try {
            serializeNode(writer, wnode);
        } catch (DOMSerializerException dse) {}
        return writer.toString();
    }//}}}
    
    //}}}
    
    public static class DOMSerializerConfiguration implements DOMConfiguration {//{{{
        
        public DOMSerializerConfiguration() {//{{{
            
            
            //create a vector of the supported parameters
            supportedParameters = new Vector(16);
            supportedParameters.add("error-handler");
            supportedParameters.add("canonical-form");
            supportedParameters.add("cdata-sections");
            supportedParameters.add("comments");
            supportedParameters.add("datatype-normalization");
            supportedParameters.add("discard-default-content");
            supportedParameters.add("entities");
            supportedParameters.add("infoset");
            supportedParameters.add("namespaces");
            supportedParameters.add("namespace-declarations");
            supportedParameters.add("normalize-characters");
            supportedParameters.add("split-cdata-sections");
            supportedParameters.add("validate");
            supportedParameters.add("validate-if-schema");
            supportedParameters.add("whitespace-in-element-content");
            supportedParameters.add("format-output");
            supportedParameters.add("indent");
            
            //set the default boolean parameters for a DOMConfiguration
            setFeature("canonical-form",                false);
            setFeature("cdata-sections",                true);
            setFeature("comments",                      true);
            setFeature("datatype-normalization",        false);
            setFeature("discard-default-content",       true);
            setFeature("entities",                      false);
            //infoset is not present because it is determined
            //by checking the values of other features.
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
            setParameter("error-handler",                 config.getParameter("error-handler"));
            
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
                    return true;
                }
                if (name == "format-output") {
                    return true;
                }
                return false;
            } else {
                if (name == "error-handler") {
                    if (value instanceof DOMErrorHandler || value == null)
                        return true;
                }
                if (name == "indent") {
                    if (value instanceof Integer || value == null)
                        return true;
                }
            }
            return false;
        }//}}}
        
        public Object getParameter(String name) throws DOMException {//{{{
            
            if (supportedParameters.indexOf(name) != -1) {
                
                if (name == "infoset") {
                    boolean namespaceDeclarations = getFeature("namespace-declarations");
                    boolean validateIfSchema      = getFeature("validate-if-schema");
                    boolean entities              = getFeature("entities");
                    boolean datatypeNormalization = getFeature("datatype-normalization");
                    boolean cdataSections         = getFeature("cdata-sections");
                    
                    boolean whitespace = getFeature("whitespace-in-element-content");
                    boolean comments   = getFeature("comments");
                    boolean namespaces = getFeature("namespaces");
                    
                    return (new Boolean(!namespaceDeclarations &&
                            !validateIfSchema &&
                            !entities &&
                            !datatypeNormalization &&
                            !cdataSections &&
                            whitespace &&
                            comments &&
                            namespaces));
                } else {
                    return parameters.get(name);
                }
                
            } else {
                
                throw new DOMException(DOMException.NOT_FOUND_ERR ,"NOT_FOUND_ERR: Parameter "+name+" not recognized");
                
            }
        }//}}}
        
        public void setParameter(String name, Object value) throws DOMException {//{{{
            
            //if a string, attempt to use it as a boolean value.
            if (value instanceof String) {
                value = new Boolean((String)value);
            }
            
            if (supportedParameters.indexOf(name) != -1) {
                if ( value != null ) {
                    if (canSetParameter(name, value)) {
                        //if the parameter is infoset
                        //then force the other parameters to
                        //values that the infoset option
                        //requires.
                        if (name == "infoset") {
                            setFeature("namespace-declarations",false);
                            setFeature("validate-if-schema",    false);
                            setFeature("entities",              false);
                            setFeature("datatype-normalization",false);
                            setFeature("cdata-sections",        false);
                            
                            setFeature("whitespace-in-element-content", true);
                            setFeature("comments",                      true);
                            setFeature("namespaces",                    true);
                            return;
                        }
                        if (name == "format-output" && ((Boolean)value).booleanValue()) {
                            setFeature("whitespace-in-element-content", false);
                        }
                        if (name == "whitespace-in-element-content" && ((Boolean)value).booleanValue()) {
                            setFeature("format-output", false);
                        }
                        
                        parameters.put(name, value);
                        
                    } else {
                        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Parameter "+name+" and value "+value.toString()+" not supported.");
                    }
                } else {
                    parameters.remove(name);
                }
            } else {
                throw new DOMException(DOMException.NOT_FOUND_ERR, "Parameter "+name+" is not recognized.");
            }
        }//}}}
        
        public boolean getFeature(String name) throws DOMException {//{{{
            if (name == "error-handler")
                throw new DOMException(DOMException.NOT_FOUND_ERR, "NOT_FOUND_ERR: "+name+" is not a feature.");
            return ((Boolean)getParameter(name)).booleanValue();
            
        }//}}}
        
        public void setFeature(String name, boolean value) throws DOMException {//{{{
            setParameter(name, new Boolean(value));
        }//}}}
        
        private Vector supportedParameters;
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
    
    private void serializeNode(Writer writer, Node node) throws DOMSerializerException {//{{{
        rSerializeNode(writer, node, "", 1, 1, 0);
    }//}}}
    
    private void rSerializeNode(Writer writer, Node node, String currentIndent, int line, int column, int offset) throws DOMSerializerException {//{{{
        
        boolean formatting = config.getFeature("format-output");
        boolean whitespace = config.getFeature("whitespace-in-element-content");
        
        //This is used many times below as a temporary variable.
        String str = "";
        
        if (filter == null || filter.acceptNode(node) == 1) {
            switch (node.getNodeType()) {
                case Node.DOCUMENT_NODE://{{{
                    String header = "<?xml version=\"1.0\" encoding=\""+encoding+"\"?>";
                    doWrite(writer, header, node, line, column, offset);
                    offset += header.length();
                    column += header.length();
                    
                    //if not formatting write newLine here.
                    if (!formatting) {
                        column = 0;
                        line += 1;
                        doWrite(writer, newLine, node, line, column, offset);
                        offset += newLine.length();
                    }
                    
                    NodeList nodes = node.getChildNodes();
                    if (nodes != null) {
                        for (int i=0; i<nodes.getLength(); i++) {
                            rSerializeNode(writer, nodes.item(i), currentIndent, line, column, offset);
                        }
                    }
                    
                    break;//}}}
                case Node.ELEMENT_NODE://{{{
                    String nodeName   = node.getLocalName();
                    String nodePrefix = node.getPrefix();
                    
                    if (formatting) {
                        //set to zero here for error handling (if doWrite throws exception).
                        column = 0;
                        str = newLine + currentIndent;
                        doWrite(writer, str, node, line, column, offset);
                        column += currentIndent.length();
                        offset += str.length();
                    }
                    
                    if (config.getFeature("namespaces") && nodePrefix != null) {
                        str = "<" + nodePrefix + ":" + nodeName;
                    } else {
                        str = "<" + nodeName;
                    }
                    doWrite(writer, str, node, line, column, offset);
                    column += str.length();
                    offset += str.length();
                    
                    NamedNodeMap attr = node.getAttributes();
                    for (int i=0; i<attr.getLength(); i++) {
                        Node currentAttr = attr.item(i);
                        str = " " + currentAttr.getNodeName() + "=\"" + currentAttr.getNodeValue() + "\"";
                        doWrite(writer, str, node, line, column, offset);
                        column += str.length();
                        offset += str.length();
                    }
                    NodeList children = node.getChildNodes();
                    if (children != null) {
                        
                        //check if element is empty or has
                        //only whitespace-only nodes
                        boolean elementEmpty = false;
                        if (children.getLength() <= 0) {
                            elementEmpty = true;
                        } else {
                            if (!config.getFeature("whitespace-in-element-content")) {
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
                                rSerializeNode(writer, children.item(i), currentIndent + indentUnit, line, column, offset);
                            }
                            
                            //don't add a new line if there is only one text node child.
                            if (formatting && !(children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE)) {
                                //set to zero here for error handling (if doWrite throws exception).
                                column = 0;
                                str = newLine + currentIndent;
                                doWrite(writer, str, node, line, column, offset);
                                column += currentIndent.length();
                                offset += str.length();
                            }
                            if (config.getFeature("namespaces") && nodePrefix != null) {
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
                                doWrite(writer, newLine, node, line, column, offset);
                                offset += newLine.length();
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
                            if (str.equals(newLine)) {
                                line++;
                                column=0;
                                doWrite(writer, newLine, node, line, column, offset);
                                offset += newLine.length();
                            } else {
                                doWrite(writer, str, node, line, column, offset);
                                column += str.length();
                                offset += str.length();
                            }
                        }
                    }
                    break;//}}}
                case Node.CDATA_SECTION_NODE://{{{
                    if (config.getFeature("cdata-sections")) {
                        if (formatting) {
                            //set to zero here for error handling (if doWrite throws exception)
                            column = 0;
                            str = newLine + currentIndent;
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
                            str = newLine + currentIndent;
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
                        str = newLine + currentIndent;
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
                        str = newLine + currentIndent;
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
                        str = " SYSTEM ";
                        doWrite(writer, str, node, line, column, offset);
                        column += str.length();
                        offset += str.length();
                    }
                    str = "\"" + docType.getSystemId() + "\">";
                    doWrite(writer, str, node, line, column, offset);
                    column += str.length();
                    offset += str.length();
                    break;//}}}
            }
        }
    }//}}}
    
    private void doWrite(Writer writer, String str, Node wnode, int line, int column, int offset) throws DOMSerializerException {//{{{
        try {
            writer.write(str);
            
        } catch (IOException ioe) {
            
            DefaultDOMLocator loc = new DefaultDOMLocator(wnode, line, column, offset, "");
            
            DOMSerializerError error = new DOMSerializerError(loc, ioe, DOMError.SEVERITY_FATAL_ERROR);
            Object rawHandler = config.getParameter("error-handler");
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
    
    private DOMSerializerConfiguration config;
    private DOMWriterFilter filter;
    private String newLine;
    private String encoding;
    //}}}
}
