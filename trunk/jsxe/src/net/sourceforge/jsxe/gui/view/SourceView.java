/*
SourceView.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contions the code source view in jsXe. This will eventually
be scrapped and a jEdit syntax highlighting view will be added.

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

package net.sourceforge.jsxe.gui.view;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.DocumentBufferListener;
import net.sourceforge.jsxe.gui.OptionsPanel;
import net.sourceforge.jsxe.dom.AdapterNode;
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.dom.XMLDocumentListener;
//}}}

//{{{ Swing components
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
//}}}

//{{{ AWT components
import java.awt.*;
import java.awt.event.*;
//}}}

//{{{ DOM Classes
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ Java base classes
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
//}}}

//}}}

/**
 * The SourceView class allows users to view and edit an XML document in raw
 * text form.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class SourceView extends JPanel implements DocumentView {
    
    //{{{ Private static members
    private static final String _VIEWNAME = "source";
    private static final Properties m_defaultProperties;
    //}}}
    
    //{{{ Public static members
    public static final String SOFT_TABS = _VIEWNAME+".soft.tabs";
    public static final String LAST_FIND_STRING = _VIEWNAME+".last.find.string";
    //}}}
    
    static {
       // InputStream viewinputstream = DefaultView.class.getResourceAsStream("/net/sourceforge/jsxe/gui/view/"+_VIEWNAME+".props");
        InputStream defaultPropsStream = SourceView.class.getResourceAsStream("/net/sourceforge/jsxe/gui/view/documentview.sourceview.props");
        m_defaultProperties = new Properties();
        try {
            m_defaultProperties.load(defaultPropsStream);
        } catch (IOException ioe) {}
    }
    
    //{{{ SourceView constructor
    /**
     * Creates a new SourceView for the XMLDocument specified.
     * Parent
     * @param document the document to open.
     * @throws IOException if the document cannot be viewed using this view
     */
    public SourceView(DocumentBuffer document) throws IOException {
        
        textarea = new SourceViewTextPane();
        textarea.setTabSize(4);
        textarea.setCaretPosition(0);
        textarea.setLineWrap(false);
        textarea.setWrapStyleWord(false);
        
        JScrollPane scrollPane = new JScrollPane(textarea);
        
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        
        setDocumentBuffer(document);
        
        //focus on the text area the first time the view is shown
        addComponentListener(new ComponentListener() {//{{{
            
            public void componentHidden(ComponentEvent e) {}
            
            public void componentMoved(ComponentEvent e) {}
            
            public void componentResized(ComponentEvent e) {}
            
            public void componentShown(ComponentEvent e) {
                textarea.requestFocus();
                removeComponentListener(this);
            }
            
        });//}}}
        
    }//}}}
    
    //{{{ getTextArea()
    
    public JTextArea getTextArea() {
        return textarea;
    }//}}}
    
    //{{{ DocumentView methods
    
    //{{{ close()
    
    public boolean close() {
        SourceViewSearchDialog dialog = SourceViewSearchDialog.getSearchDialog();
        if (dialog != null) {
            dialog.dispose();
        }
        m_document.removeDocumentBufferListener(docListener);
        return true;
    }//}}}
    
    //{{{ getDescription()
    
    public String getDescription() {
        return "View a document as text";
    }//}}}
    
    //{{{ getDocumentViewComponent
    
    public Component getDocumentViewComponent() {
        return this;
    }//}}}

    //{{{ getHumanReadableName()
    
    public String getHumanReadableName() {
        return "Source View";
    }//}}}
    
    //{{{ getMenus()
    
    public JMenu[] getMenus() {
        
        JMenu[] menus = new JMenu[1];
        
        //{{{ Construct Edit Menu
        JMenu menu = new JMenu("Edit");
        menu.setMnemonic('E');
           // These don't do anything yet.
           // JMenuItem menuItem = new JMenuItem("Undo");
           // menuItem.addActionListener( new EditUndoAction() );
           // menu.add( menuItem );
           // menuItem = new JMenuItem("Redo");
           // menuItem.addActionListener( new EditRedoAction() );
           // menu.add(menuItem);
           // menu.addSeparator();
            JMenuItem menuItem = new JMenuItem(new EditCutAction());
            menu.add(menuItem);
            menuItem = new JMenuItem(new EditCopyAction());
            menu.add(menuItem);
            menuItem = new JMenuItem(new EditPasteAction());
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem(new EditFindAction());
            menu.add(menuItem);
           // menuItem = new JMenuItem(new EditFindNextAction());
           // menu.add(menuItem);
        //}}}
        
        menus[0] = menu;
        return menus;
    }//}}}
    
    //{{{ getOptionsPanel()
    
    public OptionsPanel getOptionsPanel() {
        return new SourceViewOptionsPanel();
    }//}}}
    
    //{{{ getDocumentBuffer()
    
    public DocumentBuffer getDocumentBuffer() {
        return m_document;
    }//}}}
    
    //{{{ getViewName()
    
    public String getViewName() {
        return _VIEWNAME;
    }//}}}
    
    //{{{ setDocumentBuffer()
    
    public void setDocumentBuffer(DocumentBuffer document) throws IOException {
        
        if (m_document != null) {
            m_document.removeDocumentBufferListener(docListener);
        }
        
        ensureDefaultProps(document);
        
        m_document = document;
        textarea.setDocument(new SourceViewDocument(m_document));
        textarea.setTabSize((new Integer(m_document.getProperty(XMLDocument.INDENT, "4"))).intValue());
        m_document.addDocumentBufferListener(docListener);
        
    }//}}}
    
    //}}}
    
    //{{{ Private members
    
    //{{{ EditUndoAction class
    
    private class EditUndoAction implements ActionListener {
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            //undo does nothing for now
        }//}}}
        
    }//}}}
    
    //{{{ EditRedoAction class
    
    private class EditRedoAction implements ActionListener {
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            //redo action does nothing for now.
        }//}}}
        
    }//}}}
    
    //{{{ EditCutAction class
    
    private class EditCutAction extends AbstractAction {
        
        //{{{ EditCutAction constructor
        
        public EditCutAction() {
            putValue(Action.NAME, "Cut");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl X"));
            putValue(Action.MNEMONIC_KEY, new Integer(KeyStroke.getKeyStroke("C").getKeyCode()));
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            textarea.cut();
        }//}}}
        
    }//}}}
    
    //{{{ EditCopyAction class
    
    private class EditCopyAction extends AbstractAction {
        
        //{{{ EditCopyAction constructor
        
        public EditCopyAction() {
            putValue(Action.NAME, "Copy");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl C"));
            putValue(Action.MNEMONIC_KEY, new Integer(KeyStroke.getKeyStroke("O").getKeyCode()));
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            textarea.copy();
        }//}}}
        
    }//}}}
    
    //{{{ EditPasteAction class
    
    private class EditPasteAction extends AbstractAction {
        
        //{{{ EditPasteAction constructor
        
        public EditPasteAction() {
            putValue(Action.NAME, "Paste");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl V"));
            putValue(Action.MNEMONIC_KEY, new Integer(KeyStroke.getKeyStroke("P").getKeyCode()));
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            textarea.paste();
        }//}}}
        
    }//}}}
    
    //{{{ EditFindAction class
    
    private class EditFindAction extends AbstractAction {
        
        //{{{ EditFindAction constructor
        
        public EditFindAction() {
            putValue(Action.NAME, "Find...");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl F"));
            putValue(Action.MNEMONIC_KEY, new Integer(KeyStroke.getKeyStroke("F").getKeyCode()));
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            SourceViewSearchDialog.showSearchDialog(SourceView.this);
        }//}}}
        
    }//}}}
    
    //{{{ EditFindNextAction class
    
    private class EditFindNextAction extends AbstractAction {
        
        //{{{ EditFindNextAction constructor
        
        public EditFindNextAction() {
            putValue(Action.NAME, "Find Next");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl G"));
            putValue(Action.MNEMONIC_KEY, new Integer(KeyStroke.getKeyStroke("N").getKeyCode()));
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            //use previous find string
            
        }//}}}
        
    }//}}}
    
    //{{{ SourceViewXMLDocumentListener class
    
    private class SourceViewXMLDocumentListener implements DocumentBufferListener {
        
        //{{{ nameChanged()
        public void nameChanged(DocumentBuffer source, String newName) {}//}}}
        
        //{{{ propertiesChanged()
        
        public void propertiesChanged(DocumentBuffer source, String key) {
            if (key.equals(XMLDocument.INDENT)) {
                textarea.setTabSize((new Integer(source.getProperty(XMLDocument.INDENT, "4"))).intValue());
                textarea.updateUI();
            }
        }//}}}
        
        //{{{ bufferSaved()
        
        public void bufferSaved(DocumentBuffer source) {}//}}}
        
        //{{{ statusChanged
        
        public void statusChanged(DocumentBuffer source, int statusType, boolean newStatus) {
            
        }//}}}
        
    }//}}}
    
    //{{{ SourceViewOptionsPanel class
    
    private class SourceViewOptionsPanel extends OptionsPanel {
        
        //{{{ SourceViewOptionsPanel constructor
        
        public SourceViewOptionsPanel() {
            
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            
            setLayout(layout);
            
            int gridY = 0;
            
            boolean softTabs = Boolean.valueOf(m_document.getProperty(SOFT_TABS, "false")).booleanValue();
            
            m_m_softTabsCheckBox = new JCheckBox("Soft tabs (emulated with spaces)", softTabs);
            
            constraints.gridy      = gridY++;
            constraints.gridx      = 1;
            constraints.gridheight = 1;
            constraints.gridwidth  = 1;
            constraints.weightx    = 1.0f;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.insets     = new Insets(1,0,1,0);
            
            layout.setConstraints(m_m_softTabsCheckBox, constraints);
            add(m_m_softTabsCheckBox);
        }//}}}
        
        //{{{ saveOptions()
        
        public void saveOptions() {
            m_document.setProperty(SOFT_TABS,(new Boolean(m_m_softTabsCheckBox.isSelected())).toString());
        }//}}}
        
        //{{{ getTitle()
        
        public String getTitle() {
            return "Source View Options";
        }//}}}
        
        //{{{ Private Members
        private JCheckBox m_m_softTabsCheckBox;
        //}}}
        
    }//}}}
    
    //{{{ SourceViewTextPane class
    
    private class SourceViewTextPane extends JTextArea {
        
        //{{{ SourceViewTextPane constructor
        
        public SourceViewTextPane() {
            super("");
        }//}}}
        
        //{{{ processKeyEvent()
        
        protected void processComponentKeyEvent(KeyEvent e) {
            if (!e.isConsumed() && e.getKeyCode() == KeyEvent.VK_TAB && e.getID() == KeyEvent.KEY_PRESSED) {
                boolean softTabs = Boolean.valueOf(m_document.getProperty(SOFT_TABS, "false")).booleanValue();
                if (softTabs) {
                    try {
                        int indent = Integer.parseInt(m_document.getProperty(XMLDocument.INDENT));
                        StringBuffer tab = new StringBuffer();
                        for (int i=0; i<indent; i++) {
                            tab.append(" ");
                        }
                        getDocument().insertString(getCaretPosition(),tab.toString(),null);
                        e.consume();
                    } catch (NumberFormatException nfe) {}
                      catch (BadLocationException ble) {}
                }
            }
            super.processComponentKeyEvent(e);
        }//}}}
        
    }//}}}
    
    //{{{ ensureDefaultProps()
    
    private void ensureDefaultProps(DocumentBuffer document) {
        //get default properties
        document.setProperty(SOFT_TABS, document.getProperty(SOFT_TABS, m_defaultProperties.getProperty(SOFT_TABS)));
    }//}}}
    
    private SourceViewXMLDocumentListener docListener = new SourceViewXMLDocumentListener();
    
    private DocumentBuffer m_document;
    private SourceViewTextPane textarea;
    
    private String m_searchString;
    private String m_replaceString;
    
    //}}}
    
}
