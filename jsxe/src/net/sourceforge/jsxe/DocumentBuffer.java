/*
DocumentBuffer.java
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

package net.sourceforge.jsxe;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.dom.XMLDocumentListener;
import net.sourceforge.jsxe.dom.AdapterNode;
import net.sourceforge.jsxe.options.OptionPane;
import net.sourceforge.jsxe.gui.OptionsPanel;
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.gui.jsxeFileDialog;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ Java base classes
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.net.URL;
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

/**
 * The DocumentBuffer class implements application specific properties of an
 * XML document.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class DocumentBuffer extends XMLDocument {
    
    public static final int DIRTY = 1;
    
    //{{{ DocumentBuffer constructor
    /**
     * Creates a new DocumentBuffer for a jsXe's default document. The buffer
     * is initialized from the document and given and takes the name Untitled-X,
     * where X is the highest untitled document number plus 1.
     * @throws IOException if there was a problem reading the document
     */
    DocumentBuffer() throws IOException {
        super(new StringReader(jsXe.getDefaultDocument()));
        setEntityResolver(new DocumentBufferResolver());
        m_file = null;
        m_name = getUntitledLabel();
    }//}}}
    
    //{{{ DocumentBuffer constructor
    /**
     * Creates a new DocumentBuffer for a file on disk. The name of the
     * DocumentBuffer is taken from the filename.
     * @param file the file to read the XML document from
     * @throws IOException if there was a problem reading the file
     */
    DocumentBuffer(File file) throws IOException {
        super(new FileReader(file));
        setEntityResolver(new DocumentBufferResolver());
        m_file = file;
        m_name = file.getName();
    }//}}}
    
    //{{{ DocumentBuffer constructor
    /**
     * Creates a new untitled DocumentBuffer. The buffer is initialized from
     * the reader given and takes the name Untitled-X, where X is the highest
     * untitled document number plus 1.
     * @param reader the reader used to read the XML document.
     * @throws IOException if there is a problem using the reader
     */
    DocumentBuffer(Reader reader) throws IOException {
        super(reader);
        setEntityResolver(new DocumentBufferResolver());
        m_file = null;
        m_name = getUntitledLabel();
    }//}}}
    
    //{{{ DocumentBuffer constructor
    
    /** 
     * Creates a new DocumentBuffer for a file and with the provided
     * set of properties.
     * @param file the file to read the XML document from
     * @param properties the properties to set to this DocumentBuffer
     * @throws IOException if the there was a problem reading the file
     */
    DocumentBuffer(File file, Properties properties) throws IOException {
        this(file);
        
        //add properties one by one
        Enumeration propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String key = propertyNames.nextElement().toString();
            setProperty(key, properties.getProperty(key));
        }
    }//}}}
    
    //{{{ close()
    /**
     * Performs closing tasks. If the document is dirty then the user is
     * prompted if they want to save.
     * @return true if the close is requested
     * @throws IOException if the user chooses to save and the file could not be saved
     */
    public boolean close(TabbedView view) throws IOException {
        if (getStatus(DIRTY)) {
            
             //If it's dirty ask if you want to save.
            String msg = getName()+" unsaved! Save it now?";
            String title = "Unsaved Changes";
            int optionType = JOptionPane.YES_NO_CANCEL_OPTION;
            int messageType = JOptionPane.WARNING_MESSAGE;
            
            int returnVal = JOptionPane.showConfirmDialog(view,
                                msg,
                                title,
                                optionType,
                                messageType);
            
            if (returnVal == JOptionPane.YES_OPTION) {
                return save(view);
            } else {
                return !(returnVal == JOptionPane.CANCEL_OPTION);
            }
        } else {
            return true;
        }
    }//}}}
    
    //{{{ addDocumentBufferListener()
    
    /**
     * Adds a listener to this document buffer that is notified when the
     * buffer is changed.
     * @param listener the listener that is notified when the buffer is changed
     */
    public void addDocumentBufferListener(DocumentBufferListener listener) {
        if (listener != null) {
            if (!m_listeners.contains(listener)) {
                m_listeners.add(listener);
            }
        }
    }//}}}
    
    //{{{ removeDocumentBufferListener()
    
    /**
     * Removes a listener from this buffer. The listener will no longer recieve
     * notifications when the buffer changes.
     * @param listener the listener to remove
     */
    public void removeDocumentBufferListener(DocumentBufferListener listener) {
        if (listener != null) {
            m_listeners.remove(m_listeners.indexOf(listener));
        }
    }//}}}
    
    //{{{ getName()
    
    /**
     * Gets the name of the buffer.
     *
     * @return the name of the buffer
     */
    public String getName() {
        return m_name;
    }//}}}
    
    //{{{ getFile()
    
    /**
     * Gets the File for this DocumentBuffer.
     * @return the File where the document was stored on disk or null if the
     *         document is untitled and does not exist on disk.
     */
    public File getFile() {
        return m_file;
    }//}}}
    
    //{{{ getStatus()
    
    /**
     * Gets a status for the DocumentBuffer, such as if it is dirty.
     * @return true if the status for the type is set to true, false otherwise
     */
    public boolean getStatus(int statusType) {
        boolean status = false;
        if (statusType == DocumentBuffer.DIRTY) {
            status = m_dirty;
        }
        return status;
    }//}}}
    
    //{{{ isUntitled()
    
    /**
     * Gets whether this DocumentBuffer is untitled.
     * @return true if the document has no corresponding file on disk.
     */
    public boolean isUntitled() {
        return (m_file == null);
    }//}}}
    
    //{{{ reload()
    /**
     * Reloads the file for this DocumentBuffer. If the document
     * is dirty then the user is prompted if they want to continue.
     * If the user decides to cancel at that point then the reload
     * is aborted and this method returns false.
     * @param view the view that requested the reload operation
     * @return true if the document was reloaded sucessfully
     */ 
    public boolean reload(TabbedView view) throws IOException {
        boolean stillReload = true;
        if (getStatus(DIRTY)) {
            
             //If it's dirty ask if you want to save.
            String msg = getName()+" unsaved!\n You will lose all unsaved changes if you reload!\n\nContinue?";
            String title = "Document Modified";
            int optionType = JOptionPane.YES_NO_OPTION;
            int messageType = JOptionPane.WARNING_MESSAGE;
            
            int returnVal = JOptionPane.showConfirmDialog(view,
                                msg,
                                title,
                                optionType,
                                messageType);
            
            if (returnVal == JOptionPane.YES_OPTION) {
                stillReload=true;
            } else {
                stillReload=false;
            }
        }
        
        if (stillReload) {
            if (isUntitled()) {
                StringReader reader = new StringReader(jsXe.getDefaultDocument());
                setModel(reader);
                reader.close();
            } else {
                FileReader reader = new FileReader(m_file);
                setModel(reader);
                reader.close();
            }
            setDirty(false);
            return true;
        } else {
            return false;
        }
        
    }//}}}
    
    //{{{ save()
    
    /**
     * Saves the document buffer to disk in the current file. If the document
     * is untitled the user is prompted for a file to save to.
     *
     * @param view the TabbedView that made this save request
     * @return true if the save was successful and the used did not intervene.
     *         false if the user canceled the save.
     * @throws IOException if the document could not be written due to an I/O
     *                     error.
     */
    public boolean save(TabbedView view) throws IOException {
        return saveAs(view, getFile());
    }//}}}
    
    //{{{ saveAs()
    
    /**
     * Displays a save dialog that the user uses to chose a file to save to
     * and saves the document to it.
     * @param view the view that made the save request.
     * @throws IOException if the document could not be written due to an I/O
     *                     error.
     */
    public boolean saveAs(TabbedView view) throws IOException {
        
        //  if XMLFile is null, defaults to home directory
        JFileChooser saveDialog = new jsxeFileDialog(getFile());
        
        int returnVal = saveDialog.showSaveDialog(view);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            
            File selectedFile = saveDialog.getSelectedFile();
            boolean reallySave = true;
            if (selectedFile.exists()) {
                //If it's dirty ask if you want to save.
                String msg = "The file "+selectedFile.getName()+" already exists. Are you sure you want to overwrite it?";
                String title = "File Exists";
                int optionType = JOptionPane.YES_NO_OPTION;
                int messageType = JOptionPane.WARNING_MESSAGE;
                
                returnVal = JOptionPane.showConfirmDialog(view,
                                    msg,
                                    title,
                                    optionType,
                                    messageType);
                if (returnVal != JOptionPane.YES_OPTION) {
                    reallySave = false;
                }
            }
            
            if (reallySave) {
                
                DocumentBuffer buffer = jsXe.getOpenBuffer(selectedFile);
                
                //If the document is already open and
                //it isn't the current document
                if (buffer != null && !equalsOnDisk(buffer)) {
                    
                    //If the saved-to document is already open we
                    //need to close that tab and save this tab
                    //as that one.
                    
                    jsXe.closeDocumentBuffer(view, buffer);
                    return saveAs(view, selectedFile);
                    
                } else {
                    return saveAs(view, selectedFile);
                }
                
            }
        }
        return false;
    }//}}}
    
    //{{{ saveAs()
    
    /**
     * Saves the document to the given file. If the file is null then the user
     * is prompted for a file to save to.
     * 
     * @param view the TabbedView that requested this save
     * @param file the file to save the document to.
     * @return true if the document was saved successfully the user did not
     *         intervene
     * @throws IOException if the document could not be written due to an I/O
     *                     error.
     */
    public boolean saveAs(TabbedView view, File file) throws IOException {
        if (file != null) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                serialize(out);
                
                if (!getName().equals(file.getName())) {
                    setName(file.getName());
                }
                
                m_file = file;
                setDirty(false);
                
                fireBufferSaved();
                
                return true;
                
            } catch (SecurityException se) {
                throw new IOException(se.getMessage());
            }
        } else {
            return saveAs(view);
        }
    }//}}}
    
    //{{{ equalsOnDisk()
    
    /**
     * Indicates if the file given is the same file on disk as the file
     * used by this DocumentBuffer are the same file on disk.
     * @param file the File to compare this DocumentBuffer's file against
     * @return true if the file given is the same file on disk as the file used
     *         by this DocumentBuffer
     * @throws IOException if the files cannot be compared due to an I/O error
     */
    public boolean equalsOnDisk(File file) throws IOException {
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
    
    //{{{ equalsOnDisk()
    /**
     * Indicates if the DocumentBuffer given represents the same file on disk as
     * the file used by this DocumentBuffer.
     * @param the DocumentBuffer whose file to compare
     * @return true if the DocumentBuffer given uses the same file on disk as
     *         the file used by this DocumentBuffer
     * @throws IOException if the files cannot be compared due to an I/O error
     */
    public boolean equalsOnDisk(DocumentBuffer buffer) throws IOException {
        return equalsOnDisk(buffer.getFile());
    }//}}}
    
    //{{{ getOptionsPanel()
    
    /**
     * Gets the pane for editing options for this DocumentBuffer.
     * @return the option pane specific to this DocumentBuffer
     */
    public OptionsPanel getOptionsPanel() {
        return new DocumentBufferOptionsPanel();
    }//}}}
    
    //{{{ fireStructureChanged()
    
    protected void fireStructureChanged(AdapterNode location) {
        setDirty(true);
        super.fireStructureChanged(location);
    }//}}}
    
    //{{{ Private members
    
    //{{{ setDirty()
    
    private void setDirty(boolean dirty) {
        if (dirty != m_dirty) {
            boolean oldDirty = m_dirty;
            m_dirty=dirty;
            fireStatusChanged(DIRTY, oldDirty);
        }
    }//}}}
    
    //{{{ setName()
    
    private void setName(String name) {
        m_name = name;
        fireNameChanged();
    }//}}}
    
    //{{{ getUntitledLabel()
    
    private String getUntitledLabel() {
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
    
    //{{{ DocumentBufferOptionsPane class
    
    private class DocumentBufferOptionsPanel extends OptionsPanel {
    
        //{{{ DocumentBufferOptionsPanel()
        
        public DocumentBufferOptionsPanel() {
            
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            
            //the grid y coordinate.
            int gridY = 0;
            
            setLayout(layout);
            
            //set up the encoding combo-box.
            supportedEncodings.add("US-ASCII");
            supportedEncodings.add("ISO-8859-1");
            supportedEncodings.add("UTF-8");
           // supportedEncodings.add("UTF-16BE");
           // supportedEncodings.add("UTF-16LE");
           // supportedEncodings.add("UTF-16");
            
            JLabel encodingLabel = new JLabel("Encoding:");
            encodingComboBox = new JComboBox(supportedEncodings);
            encodingComboBox.setEditable(false);
            
            Enumeration encodings = supportedEncodings.elements();
            while (encodings.hasMoreElements()) {
                String nextEncoding = (String)encodings.nextElement();
                if (getProperty(ENCODING).equals(nextEncoding)) {
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
            indentComboBox.setSelectedItem(getProperty(INDENT));
            
            
           // boolean whitespace    = Boolean.valueOf(m_document.getProperty(XMLDocument.WS_IN_ELEMENT_CONTENT, "true")).booleanValue();
            boolean formatOutput = Boolean.valueOf(getProperty(XMLDocument.FORMAT_XML, "false")).booleanValue();
            
           // whitespaceCheckBox = new JCheckBox("Whitespace in element content", whitespace);
            formatCheckBox     = new JCheckBox("Format XML output", formatOutput);
            
           // whitespaceCheckBox.addChangeListener(new WhiteSpaceChangeListener());
            
            boolean validating = Boolean.valueOf(getProperty(XMLDocument.IS_VALIDATING, "false")).booleanValue();
            m_m_validatingCheckBox = new JCheckBox("Validate if DTD or Schema Available", validating);
            
           // formatCheckBox.setEnabled(!whitespace);
            
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
            
           // constraints.gridy      = gridY++;
           // constraints.gridx      = 0;
           // constraints.gridheight = 1;
           // constraints.gridwidth  = GridBagConstraints.REMAINDER;
           // constraints.weightx    = 0.0f;
           // constraints.fill       = GridBagConstraints.BOTH;
           // constraints.insets     = new Insets(1,0,1,0);
            
           // layout.setConstraints(whitespaceCheckBox, constraints);
           // add(whitespaceCheckBox);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 0;
            constraints.gridheight = 1;
            constraints.gridwidth  = GridBagConstraints.REMAINDER;
            constraints.weightx    = 0.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(formatCheckBox, constraints);
            add(formatCheckBox);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 0;
            constraints.gridheight = 1;
            constraints.gridwidth  = GridBagConstraints.REMAINDER;
            constraints.weightx    = 0.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(m_m_validatingCheckBox, constraints);
            add(m_m_validatingCheckBox);
            
            boolean softTabs = Boolean.valueOf(getProperty(XMLDocument.IS_USING_SOFT_TABS, "false")).booleanValue();
        
            m_m_softTabsCheckBox = new JCheckBox("Soft tabs (emulated with spaces)", softTabs);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 0;
            constraints.gridheight = 1;
            constraints.gridwidth  = GridBagConstraints.REMAINDER;
            constraints.weightx    = 0.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(m_m_softTabsCheckBox, constraints);
            add(m_m_softTabsCheckBox);
            
        }//}}}
        
        //{{{ saveOptions()
        
        public void saveOptions() {
            if (!String.valueOf(formatCheckBox.isSelected()).equals(getProperty(XMLDocument.FORMAT_XML))) {
                setDirty(true);
                setProperty(XMLDocument.FORMAT_XML, String.valueOf(formatCheckBox.isSelected()));
            }
            if (!String.valueOf(m_m_softTabsCheckBox.isSelected()).equals(getProperty(XMLDocument.IS_USING_SOFT_TABS))) {
                setDirty(true);
                setProperty(XMLDocument.IS_USING_SOFT_TABS, String.valueOf(m_m_softTabsCheckBox.isSelected()));
            }
            if (!String.valueOf(m_m_validatingCheckBox.isSelected()).equals(getProperty(XMLDocument.IS_VALIDATING))) {
                setProperty(XMLDocument.IS_VALIDATING, String.valueOf(m_m_validatingCheckBox.isSelected()));
            }
           // if (!String.valueOf(whitespaceCheckBox.isSelected()).equals(m_document.getProperty(XMLDocument.WS_IN_ELEMENT_CONTENT))) {
           //     setDirty(true);
           //     m_document.setProperty(XMLDocument.WS_IN_ELEMENT_CONTENT, String.valueOf(whitespaceCheckBox.isSelected()));
           // }
            if (!encodingComboBox.getSelectedItem().toString().equals(getProperty(XMLDocument.ENCODING))) {
                setDirty(true);
                setProperty(XMLDocument.ENCODING, encodingComboBox.getSelectedItem().toString());
            }
            try {
                //don't need to set dirty, no change to text
                setProperty(XMLDocument.INDENT, (new Integer(indentComboBox.getSelectedItem().toString())).toString());
            } catch (NumberFormatException nfe) {
                //Bad input, don't save.
            }
        };//}}}
        
        //{{{ getName()
        
        public String getName() {
            return "documentbuffer";
        }//}}}
        
        //{{{ getTitle()
        
        public String getTitle() {
            return "XML Document Options";
        };//}}}
        
       // //{{{ WhiteSpaceChangeListener class
        
       // private class WhiteSpaceChangeListener implements ChangeListener {
            
       //     //{{{ stateChanged()
            
       //     public void stateChanged(ChangeEvent e) {
       //         boolean whitespace = whitespaceCheckBox.isSelected();
       //         if (whitespace) {
       //             formatCheckBox.setSelected(false);
       //         }
       //         formatCheckBox.setEnabled(!whitespace);
       //     }//}}}
            
       // }//}}}
        
        //{{{ Private members
        
        private DocumentBuffer m_buffer;
        private JCheckBox m_m_softTabsCheckBox;
        private JComboBox encodingComboBox;
        private JComboBox indentComboBox;
        private JCheckBox whitespaceCheckBox;
        private JCheckBox m_m_validatingCheckBox;
        private final Vector supportedEncodings = new Vector(6);
        private JCheckBox formatCheckBox;
        //}}}
    
    }//}}}
    
    //{{{ fireNameChanged()
    
    private void fireNameChanged() {
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            DocumentBufferListener listener = (DocumentBufferListener)iterator.next();
            listener.nameChanged(this, getName());
        }
    }//}}}
    
    //{{{ fireBufferSaved()
    
    private void fireBufferSaved() {
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            DocumentBufferListener listener = (DocumentBufferListener)iterator.next();
            listener.bufferSaved(this);
        }
    }//}}}
    
    //{{{ fireStatusChanged()
    
    private void fireStatusChanged(int status, boolean oldStatus) {
        ListIterator iterator = m_listeners.listIterator();
        while (iterator.hasNext()) {
            DocumentBufferListener listener = (DocumentBufferListener)iterator.next();
            listener.statusChanged(this, status, oldStatus);
        }
    }//}}}
    
    //{{{ DocumentBufferResolver class
    
    private class DocumentBufferResolver implements EntityResolver {
        
        //{{{ resolveEntity()
        
        public InputSource resolveEntity(String publicId, String systemId) {
            
            String entity = systemId;
            InputSource source = null;
            
            if (m_file != null) {
                
                try {
                    String filePathURI = m_file.toURL().toExternalForm();
                    int index = filePathURI.lastIndexOf("/")+1;
                    if (index != -1) {
                        filePathURI = filePathURI.substring(0, index);
                    }
                    
                    index = entity.lastIndexOf("/")+1;
                    if (index != -1) {
                        entity = entity.substring(index);
                    }
                    
                    //create the path to the entity relative to the document
                    filePathURI += entity;
                    source = new InputSource((new URL(filePathURI)).openStream());
                    Log.log(Log.DEBUG, this, filePathURI);
                    
                } catch (MalformedURLException e) {
                    //Do nothing and try to open this entity normally
                } catch (IOException e) {
                    //Probobly file not found.
                    //Do nothing and try to open this entity normally
                }
            }
            return source;
        }//}}}
        
    }//}}}
    
    private String m_name;
    private File m_file;
    private ArrayList m_listeners = new ArrayList();
    private boolean m_dirty=false;
    
    //}}}
}
