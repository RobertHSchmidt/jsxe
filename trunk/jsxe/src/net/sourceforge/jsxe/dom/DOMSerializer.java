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

This is a bad implementation of a serialization class. It will soon be replaced
by a DOM3 implementation.

This file written by Ian Lewis (IanLewis@member.fsf.org)
Copied extensively and modified from Java & XML by Brett McLaughlin

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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
//}}}

//}}}

public class DOMSerializer {
    
    public DOMSerializer(boolean formatting) {//{{{
        formatText = formatting;
        if (formatText) {
            indent = "    ";
            lineSeparator = System.getProperty("line.separator");
        } else {
            indent = "";
            lineSeparator = "";
        }
    }//}}}
    
    public void serialize(Document doc, OutputStream out) throws IOException {//{{{
        serialize(doc, new OutputStreamWriter(out));
    }//}}}
    
    public void serialize(Document doc, File file) throws IOException {//{{{
        serialize(doc, new FileWriter(file));
    }//}}}
    
    public void serialize(Document doc, Writer writer) throws IOException {//{{{
        serializeNode(doc, writer, "");
        writer.flush();
    }//}}}
    
    private void serializeNode(Node node, Writer writer, String indentLevel) throws IOException {//{{{
        switch (node.getNodeType()) {
            case Node.DOCUMENT_NODE:
                writer.write("<?xml version=\"1.0\"?>");
                writer.write(System.getProperty("line.separator"));
                
                NodeList nodes = node.getChildNodes();
                if (nodes != null) {
                    for (int i=0; i<nodes.getLength(); i++) {
                        serializeNode(nodes.item(i), writer, "");
                    }
                }
                /*
                Document doc = (Document)node;
                serializeNode(doc.getDocumentElement(), writer, "");
                */
                break;
            case Node.ELEMENT_NODE:
                String nodeName = node.getNodeName();
                writer.write(indentLevel + "<" + nodeName);
                NamedNodeMap attr = node.getAttributes();
                for (int i=0; i<attr.getLength(); i++) {
                    Node currentAttr = attr.item(i);
                    writer.write(" " + currentAttr.getNodeName() + "=\"" +
                        currentAttr.getNodeValue() + "\"");
                }
                writer.write(">");
                NodeList children = node.getChildNodes();
                if (children != null) {
                    if (children.item(0) != null) {
                        if (children.getLength() != 1 && children.item(0).getNodeType() == Node.TEXT_NODE)
                            writer.write(lineSeparator);
                    }
                    for(int i=0; i<children.getLength();i++) {
                        serializeNode(
                            children.item(i), writer, indentLevel + indent);
                    }
                    if (children.item(0) != null) {
                        if (children.getLength() != 1 && children.item(0).getNodeType() == Node.TEXT_NODE)
                            writer.write(indentLevel);
                    }
                }
                writer.write("</" + nodeName + ">");
                writer.write(lineSeparator);
                break;
            case Node.TEXT_NODE:
                String text = node.getNodeValue();
                if (formatText)
                    text = text.trim();
                if (!text.equals("")) {
                    writer.write(indentLevel + text);
                    writer.write(lineSeparator);
                }
                break;
            case Node.CDATA_SECTION_NODE:
                writer.write("<![CDATA[" + node.getNodeValue() + "]]>");
                break;
            case Node.COMMENT_NODE:
                writer.write(indentLevel+"<!--"+node.getNodeValue()+"-->");
                writer.write(lineSeparator);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                writer.write("<?" + node.getNodeName() + " " +
                    node.getNodeValue() + "?>");
                writer.write(lineSeparator);
                break;
            case Node.ENTITY_REFERENCE_NODE:
                writer.write("&" + node.getNodeName() + ";");
                break;
            case Node.DOCUMENT_TYPE_NODE:
                DocumentType docType = (DocumentType)node;
                writer.write("<!DOCTYPE " + docType.getName());
                if (docType.getPublicId() != null) {
                    writer.write(" PUBLIC \"" + docType.getPublicId() + "\" "); 
                } else {
                    writer.write(" SYSTEM ");
                }
                writer.write("\"" + docType.getSystemId() + "\">");
                writer.write(lineSeparator);
                break;
        }
    }//}}}
    
    /*
    *************************************************
    Private Data Fields
    *************************************************
    *///{{{
    private boolean formatText;
    private String indent;
    private String lineSeparator;
    //}}}
}
