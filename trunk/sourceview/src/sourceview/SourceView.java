/*
SourceView.java
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

package sourceview;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.*;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.gui.OptionsPanel;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.dom.AdapterNode;
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.dom.XMLDocumentListener;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ jEdit Syntax classes
import org.syntax.jedit.*;
import org.syntax.jedit.tokenmarker.XMLTokenMarker;
//}}}

//{{{ Swing components
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
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
        InputStream defaultPropsStream = SourceView.class.getResourceAsStream("/sourceview/sourceview.props");
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
    public SourceView(DocumentBuffer document, SourceViewPlugin plugin) throws IOException {
        
        m_plugin = plugin;
        
        textarea = new SourceViewTextPane();
        //use hard coded font for now
        textarea.getPainter().setStyles(
            new SyntaxStyle[] { new SyntaxStyle(Color.BLACK, false,false),      //NULL
                                new SyntaxStyle(Color.GREEN, true, false),      //COMMENT
                                new SyntaxStyle(Color.BLUE, true,true),         //DECLARATION
                                new SyntaxStyle(Color.GRAY, false,false),       //ATTRIBUTE VALUE
                                new SyntaxStyle(Color.ORANGE, false,true),      //CDATA
                                new SyntaxStyle(Color.BLACK, false,true),       //ENTITY REFERENCE
                                new SyntaxStyle(Color.GRAY, false,true),        //ELEMENT
                                new SyntaxStyle(Color.GRAY, false,true),        //ATTRIBUTE NAME
                                new SyntaxStyle(Color.CYAN, false,true),        //PROCESSING INSTRUCTION
                                new SyntaxStyle(Color.BLACK, false, true),      //OPERATOR
                                new SyntaxStyle(Color.RED, false, false),       //INVALID
                                });
        textarea.setFont(new Font("Monospaced", 0, 12));
        textarea.setCaretPosition(0);
        //for test scripts
        textarea.setName("SourceTextArea");
        
        setLayout(new BorderLayout());
        add(textarea, BorderLayout.CENTER);
        
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
    
    public JEditTextArea getTextArea() {
        return textarea;
    }//}}}
    
    //{{{ getHumanReadableName()
    
    public static String getHumanReadableName() {
        return "Source View";
    }//}}}
    
    //{{{ DocumentView methods
    
    //{{{ close()
    
    public boolean close() {
        SourceViewSearchDialog dialog = SourceViewSearchDialog.getSearchDialog();
        if (dialog != null) {
            dialog.dispose();
        }
        m_document.removeXMLDocumentListener(docListener);
        return true;
    }//}}}
    
    //{{{ getDocumentViewComponent
    
    public Component getDocumentViewComponent() {
        return this;
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
            JMenuItem menuItem = new JMenuItem(jsXe.getAction("sourceview.cut"));
            menu.add(menuItem);
            menuItem = new JMenuItem(jsXe.getAction("sourceview.copy"));
            menu.add(menuItem);
            menuItem = new JMenuItem(jsXe.getAction("sourceview.paste"));
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem(jsXe.getAction("sourceview.find"));
            menu.add(menuItem);
           // menuItem = new JMenuItem(new EditFindNextAction());
           // menu.add(menuItem);
        //}}}
        
        menus[0] = menu;
        return menus;
    }//}}}
    
    //{{{ getDocumentBuffer()
    
    public DocumentBuffer getDocumentBuffer() {
        return m_document;
    }//}}}
    
    //{{{ getViewName()
    
    public ViewPlugin getViewPlugin() {
        return m_plugin;
    }//}}}
    
    //{{{ setDocumentBuffer()
    
    public void setDocumentBuffer(DocumentBuffer document) throws IOException {
        
        if (m_document != null) {
            m_document.removeXMLDocumentListener(docListener);
        }
        
        ensureDefaultProps(document);
        
        m_document = document;
        textarea.setDocument(new SourceViewDocument(m_document));
        textarea.setTokenMarker(new XMLTokenMarker());
        try {
            textarea.getDocument().putProperty(PlainDocument.tabSizeAttribute, Integer.valueOf(document.getProperty(XMLDocument.INDENT, "4")));
        } catch (NumberFormatException e) {
            Log.log(Log.WARNING, this, e.getMessage());
        }
        m_document.addXMLDocumentListener(docListener);
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
    
    //{{{ EditFindNextAction class
    
    private class EditFindNextAction extends AbstractAction {
        
        //{{{ EditFindNextAction constructor
        
        public EditFindNextAction() {
            //putValue(Action.NAME, "Find Next");
        	putValue(Action.NAME,  Messages.getMessage("SourceView.FindNext"));
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl G"));
            putValue(Action.MNEMONIC_KEY, new Integer(KeyStroke.getKeyStroke("N").getKeyCode()));
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            //use previous find string
            
        }//}}}
        
    }//}}}
    
    //{{{ SourceViewXMLDocumentListener class
    
    private class SourceViewXMLDocumentListener implements XMLDocumentListener {
        
        //{{{ propertyChanged()
        
        public void propertyChanged(XMLDocument source, String key, String oldValue) {
            if (key.equals(XMLDocument.INDENT)) {
                try {
                    textarea.getDocument().putProperty(PlainDocument.tabSizeAttribute, Integer.valueOf(source.getProperty(XMLDocument.INDENT, "4")));
                    textarea.updateUI();
                } catch (NumberFormatException e) {
                    Log.log(Log.WARNING, this, e.getMessage());
                }
            }
        }//}}}
        
        //{{{ structureChanged
        
        public void structureChanged(XMLDocument source, AdapterNode location) {}//}}}
        
    }//}}}

    //{{{ SourceViewTextPane class
    
    private class SourceViewTextPane extends JEditTextArea {
        
        //{{{ SourceViewTextPane constructor
        
        public SourceViewTextPane() {
            super();
        }//}}}
        
        //{{{ processKeyEvent()
        
        public void processKeyEvent(KeyEvent e) {
            Log.log(Log.DEBUG,this,"processKeyEvent: "+KeyEvent.getKeyModifiersText(e.getModifiers())+" "+KeyEvent.getKeyText(e.getKeyCode()));
            if (!e.isConsumed() && e.getKeyCode() == KeyEvent.VK_TAB && e.getID() == KeyEvent.KEY_PRESSED) {
                boolean softTabs = Boolean.valueOf(m_document.getProperty(XMLDocument.IS_USING_SOFT_TABS, "false")).booleanValue();
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
            super.processKeyEvent(e);
        }//}}}
        
    }//}}}
    
    //{{{ ensureDefaultProps()
    
    private void ensureDefaultProps(DocumentBuffer document) {
        
    }//}}}
    
    private SourceViewXMLDocumentListener docListener = new SourceViewXMLDocumentListener();
    
    private DocumentBuffer m_document;
    private SourceViewTextPane textarea;
    
    private String m_searchString;
    private String m_replaceString;
    private SourceViewPlugin m_plugin;
    
    //}}}
    
}
