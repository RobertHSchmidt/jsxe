/*
DocumentBuffer.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the Buffer class that jsXe will use for editor/application
specific tasks. It uses the XMLDocument interface to model an XML document.

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

package net.sourceforge.jsxe;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.dom.AdapterNode;
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.dom.XMLDocumentFactory;
import net.sourceforge.jsxe.dom.XMLDocumentListener;
import net.sourceforge.jsxe.dom.UnrecognizedDocTypeException;
import net.sourceforge.jsxe.gui.OptionsPanel;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Vector;
//}}}

//{{{ AWT components
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
//}}}

//{{{ Swing classes
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
//}}}

//{{{ DOM classes
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
//}}}

//}}}

public class DocumentBuffer {
    
    DocumentBuffer(File file) throws IOException {//{{{
        m_file = file;
        m_name = file.getName();
        try {
            XMLDocumentFactory factory = XMLDocumentFactory.newInstance();
            factory.setEntityResolver(new DocumentBufferResolver());
            m_document = factory.newXMLDocument(new FileReader(file));
        } catch (UnrecognizedDocTypeException e) {}
        m_document.addXMLDocumentListener(m_bufferDocListener);
    }//}}}
    
    DocumentBuffer(Reader reader) throws IOException {//{{{
        m_file = null;
        m_name = getUntitledLabel();
        try {
            XMLDocumentFactory factory = XMLDocumentFactory.newInstance();
            factory.setEntityResolver(new DocumentBufferResolver());
            m_document = factory.newXMLDocument(reader);
        } catch (UnrecognizedDocTypeException e) {}
        m_document.addXMLDocumentListener(m_bufferDocListener);
    }//}}}
    
    public void addDocumentBufferListener(DocumentBufferListener listener) {//{{{
        if (listener != null) {
            if (!m_listeners.contains(listener)) {
                m_listeners.add(listener);
            }
        }
    }//}}}
    
    public void removeDocumentBufferListener(DocumentBufferListener listener) {//{{{
        if (listener != null) {
            m_listeners.remove(m_listeners.indexOf(listener));
        }
    }//}}}
    
    public String getName() {//{{{
        return m_name;
    }//}}}
    
    public File getFile() {//{{{
        return m_file;
    }//}}}
    
    public String setProperty(String key, String value) {//{{{
        return m_document.setProperty(key, value);
    }//}}}
    
    public String getProperty(String key) {//{{{
        return m_document.getProperty(key);
    }//}}}
    
    public String getProperty(String key, String defaultValue) {//{{{
        return m_document.getProperty(key, defaultValue);
    }//}}}
    
    public XMLDocument getXMLDocument() {//{{{
        return m_document;
    }//}}}
    
    public boolean isDirty() {//{{{
        return (Boolean.valueOf(getProperty("dirty"))).booleanValue();
    }//}}}
    
    public boolean isUntitled() {//{{{
        return (m_file == null);
    }//}}}
    
    public boolean save() throws IOException {//{{{
        return saveAs(getFile());
    }//}}}
    
    public boolean saveAs(File file) throws IOException {//{{{
        try {
            FileOutputStream out = new FileOutputStream(file);
            m_document.serialize(out);
            
            if (!getName().equals(file.getName())) {
                setName(file.getName());
                fireNameChanged();
            }
            
            m_file = file;
            setDirty(false);
            
            fireBufferSaved();
            
            return true;
            
        } catch (SecurityException se) {
            throw new IOException(se.getMessage());
        }
    }//}}}
    
    public boolean equalsOnDisk(File file) throws IOException {//{{{
        if (getFile() != null && file != null) {
            boolean caseInsensitiveFilesystem = (File.separatorChar == '\\'
                || File.separatorChar == ':' /* Windows or MacOS */);
    
            if (file != null) {
                if (caseInsensitiveFilesystem) {
                    
                    if (file.getCanonicalPath().equalsIgnoreCase(getFile().getCanonicalPath())) {
                        return true;
                    }
                    
                } else {
                    
                    if (file.getCanonicalPath().equals(getFile().getCanonicalPath())) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }//}}}
    
    public boolean equalsOnDisk(DocumentBuffer buffer) throws IOException {//{{{
        return equalsOnDisk(buffer.getFile());
    }//}}}
    
    public OptionsPanel getOptionsPanel() {//{{{
        return new DocumentBufferOptionsPanel();
    }//}}}
    
    //{{{ private members
    
    private void setDirty(boolean dirty) {//{{{
        setProperty("dirty", Boolean.toString(dirty));
    }//}}}
    
    private void setName(String name) {//{{{
        m_name = name;
        fireNameChanged();
    }//}}}
    
    private String getUntitledLabel() {//{{{
        DocumentBuffer[] buffers = jsXe.getDocumentBuffers();
        int untitledNo = 0;
        for (int i=0; i < buffers.length; i++) {
            if ( buffers[i].getName().startsWith("Untitled-")) {
                // Kinda stolen from jEdit
                try {
					untitledNo = Math.max(untitledNo,Integer.parseInt(buffers[i].getName().substring(9)));
                }
				catch(NumberFormatException nf) {}
            }
        }
        return "Untitled-" + Integer.toString(untitledNo+1);
    }//}}}
    
    private class DocumentBufferOptionsPanel extends OptionsPanel {//{{{
    
        public DocumentBufferOptionsPanel() {//{{{
            
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            
            //the grid y coordinate.
            int gridY = 0;
            
            setLayout(layout);
            
            //set up the encoding combo-box.
            supportedEncodings.add("US-ASCII");
            supportedEncodings.add("ISO-8859-1");
            supportedEncodings.add("UTF-8");
            supportedEncodings.add("UTF-16BE");
            supportedEncodings.add("UTF-16LE");
            supportedEncodings.add("UTF-16");
            
            JLabel encodingLabel = new JLabel("Encoding:");
            encodingComboBox = new JComboBox(supportedEncodings);
            encodingComboBox.setEditable(false);
            
            Enumeration encodings = supportedEncodings.elements();
            while (encodings.hasMoreElements()) {
                String nextEncoding = (String)encodings.nextElement();
                if (m_document.getProperty("encoding").equals(nextEncoding)) {
                    encodingComboBox.setSelectedItem(nextEncoding);
                }
            }
            
            JLabel indentLabel = new JLabel("Indent width:");
            
            Vector sizes = new Vector(3);
            sizes.add("2");
            sizes.add("4");
            sizes.add("8");
            indentComboBox = new JComboBox(sizes);
            indentComboBox.setEditable(true);
            indentComboBox.setSelectedItem(m_document.getProperty("indent"));
            
            //set up the whitespace and format output check-boxes.
            boolean whitespace    = Boolean.valueOf(m_document.getProperty("element-content-whitespace", "true")).booleanValue();
            boolean formatOutput = Boolean.valueOf(m_document.getProperty("format-pretty-print", "false")).booleanValue();
            
            whitespaceCheckBox = new JCheckBox("Whitespace in element content", whitespace);
            formatCheckBox     = new JCheckBox("Format XML output", formatOutput);
            
            whitespaceCheckBox.addChangeListener(new WhiteSpaceChangeListener());
            
            formatCheckBox.setEnabled(!whitespace);
            
            constraints.gridy      = gridY;
            constraints.gridx      = 0;
            constraints.gridheight = 1;
            constraints.gridwidth  = 1;
            constraints.weightx    = 1.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(encodingLabel, constraints);
            add(encodingLabel);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 1;
            constraints.gridheight = 1;
            constraints.gridwidth  = 1;
            constraints.weightx    = 1.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(encodingComboBox, constraints);
            add(encodingComboBox);
            
            constraints.gridy      = gridY;
            constraints.gridx      = 0;
            constraints.gridheight = 1;
            constraints.gridwidth  = 1;
            constraints.weightx    = 1.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(indentLabel, constraints);
            add(indentLabel);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 1;
            constraints.gridheight = 1;
            constraints.gridwidth  = 1;
            constraints.weightx    = 1.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(indentComboBox, constraints);
            add(indentComboBox);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 0;
            constraints.gridheight = 1;
            constraints.gridwidth  = GridBagConstraints.REMAINDER;
            constraints.weightx    = 0.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(whitespaceCheckBox, constraints);
            add(whitespaceCheckBox);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 0;
            constraints.gridheight = 1;
            constraints.gridwidth  = GridBagConstraints.REMAINDER;
            constraints.weightx    = 0.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(formatCheckBox, constraints);
            add(formatCheckBox);
            
        }//}}}
        
        public void saveOptions() {//{{{
            if (!String.valueOf(formatCheckBox.isSelected()).equals(m_document.getProperty(XMLDocument.FORMAT_XML))) {
                setDirty(true);
                m_document.setProperty(XMLDocument.FORMAT_XML, String.valueOf(formatCheckBox.isSelected()));
            }
            if (!String.valueOf(whitespaceCheckBox.isSelected()).equals(m_document.getProperty(XMLDocument.WS_IN_ELEMENT_CONTENT))) {
                setDirty(true);
                m_document.setProperty(XMLDocument.WS_IN_ELEMENT_CONTENT, String.valueOf(whitespaceCheckBox.isSelected()));
            }
            if (!encodingComboBox.getSelectedItem().toString().equals(m_document.getProperty(XMLDocument.ENCODING))) {
                setDirty(true);
                m_document.setProperty(XMLDocument.ENCODING, encodingComboBox.getSelectedItem().toString());
            }
            try {
                //don't need to set dirty, no change to text
                m_document.setProperty(XMLDocument.INDENT, (new Integer(indentComboBox.getSelectedItem().toString())).toString());
            } catch (NumberFormatException nfe) {
                //Bad input, don't save.
            }
        };//}}}
        
        public String getTitle() {//{{{
            return "XML Document Options";
        };//}}}
        
        private class WhiteSpaceChangeListener implements ChangeListener {//{{{
            
            public void stateChanged(ChangeEvent e) {//{{{
                boolean whitespace = whitespaceCheckBox.isSelected();
                if (whitespace) {
                    formatCheckBox.setSelected(false);
                }
                formatCheckBox.setEnabled(!whitespace);
            }//}}}
            
        }//}}}
        
        private DocumentBuffer m_buffer;
        private JComboBox encodingComboBox;
        private JComboBox indentComboBox;
        private JCheckBox whitespaceCheckBox;
        private final Vector supportedEncodings = new Vector(6);
        private JCheckBox formatCheckBox;
    
    }//}}}
    
    private void fireNameChanged() {//{{{
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            DocumentBufferListener listener = (DocumentBufferListener)iterator.next();
            listener.nameChanged(this, getName());
        }
    }//}}}
    
    private void firePropertiesChanged(String key) {//{{{
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            DocumentBufferListener listener = (DocumentBufferListener)iterator.next();
            listener.propertiesChanged(this, key);
        }
    }//}}}
    
    private void fireBufferSaved() {//{{{
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            DocumentBufferListener listener = (DocumentBufferListener)iterator.next();
            listener.bufferSaved(this);
        }
    }//}}}
    
    private class DocumentBufferResolver implements EntityResolver {//{{{
        
        public InputSource resolveEntity(String publicId, String systemId) {//{{{
            
            String entity = systemId;
            InputSource source = null;
            
            if (m_file != null) {
                try {
                    entity = entity.substring(entity.lastIndexOf("/")+1);
                    
                    String filePathURI = m_file.toURL().toExternalForm();
                    filePathURI = filePathURI.substring(0, filePathURI.lastIndexOf("/")+1);
                    
                    entity = filePathURI + entity;
                    
                    source = new InputSource(entity);
                    
                } catch (MalformedURLException e) {
                    //Do nothing and try to open this entity normally
                }
            }
            return source;
        }//}}}
        
    }//}}}

    private XMLDocumentListener m_bufferDocListener = new XMLDocumentListener() {//{{{
                
        public void propertiesChanged(XMLDocument source, String propertyKey) {//{{{
            firePropertiesChanged(propertyKey);
        }//}}}
        
        public void structureChanged(XMLDocument source, AdapterNode location) {//{{{
            setDirty(true);
        }//}}}
        
    };//}}}
    
    private XMLDocument m_document;
    private String m_name;
    private File m_file;
    private ArrayList m_listeners = new ArrayList();
    
    //}}}
}
