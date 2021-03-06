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

import sourceview.action.*;

//{{{ jsXe classes
import net.sourceforge.jsxe.*;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.dom.AdapterNode;
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.dom.XMLDocumentListener;
import net.sourceforge.jsxe.msg.PropertyChanged;
import net.sourceforge.jsxe.msg.UndoEvent;
import net.sourceforge.jsxe.msg.RedoEvent;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.util.MiscUtilities;
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
import java.util.ArrayList;
//}}}

//}}}

/**
 * The SourceView class allows users to view and edit an XML document in raw
 * text form.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class SourceView extends JPanel implements DocumentView, EBListener {
    
    //{{{ Public static members
    public static final String SOFT_TABS = SourceViewPlugin.PLUGIN_NAME+".soft.tabs";
    public static final String END_OF_LINE_MARKS = SourceViewPlugin.PLUGIN_NAME+".end-of-line-markers";
    //}}}
    
    //{{{ SourceView constructor
    /**
     * Creates a new SourceView for the XMLDocument specified.
     * Parent
     * @param document the document to open.
     * @throws IOException if the document cannot be viewed using this view
     */
    public SourceView(DocumentBuffer document, SourceViewPlugin plugin) throws IOException {
        
        m_plugin = plugin;
        
        m_textarea = new JEditTextArea();
        
        InputHandler handler = m_textarea.getInputHandler();
        handler.addKeyBinding("ENTER",new SourceViewEnter());
        handler.addKeyBinding("TAB",new SourceViewTab());
        m_textarea.setInputHandler(handler);
        
        TextAreaPainter painter = m_textarea.getPainter();
        painter.setEOLMarkersPainted(jsXe.getBooleanProperty(SourceView.END_OF_LINE_MARKS, true));
        painter.setStyles(
            new SyntaxStyle[] { SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.text.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.comment.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.doctype.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.attribute.value.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.attribute.value.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.cdata.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.entity.reference.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.element.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.attribute.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.processing.instruction.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.namespace.prefix.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.markup.color")),
                                SourceViewOptionPane.parseStyle(jsXe.getProperty("sourceview.invalid.color")),
                                });
       // textarea.setFont(new Font("Monospaced", 0, 12));
        m_textarea.setCaretPosition(0);
        //for test scripts
        m_textarea.setName("SourceTextArea");
        
        m_textarea.putClientProperty(InputHandler.SMART_HOME_END_PROPERTY, Boolean.TRUE);
        
        ActionManager.addActionImplementation("cut", m_textarea, new EditCutAction());
        ActionManager.addActionImplementation("copy", m_textarea, new EditCopyAction());
        ActionManager.addActionImplementation("paste", m_textarea, new EditPasteAction());
        ActionManager.addActionImplementation("find", m_textarea, new EditFindAction());
        ActionManager.addActionImplementation("findnext", m_textarea, new EditFindNextAction());
        
        //{{{ create popup menu
        
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem(ActionManager.getAction("cut"));
        popup.add(menuItem);
        menuItem = new JMenuItem(ActionManager.getAction("copy"));
        popup.add(menuItem);
        menuItem = new JMenuItem(ActionManager.getAction("paste"));
        popup.add(menuItem);
        popup.addSeparator();
        menuItem = new JMenuItem(ActionManager.getAction("find"));
        popup.add(menuItem);
        menuItem = new JMenuItem(ActionManager.getAction("findnext"));
        popup.add(menuItem);
        
        m_textarea.setRightClickPopup(popup);
        //}}}
        
        setLayout(new BorderLayout());
        add(m_textarea, BorderLayout.CENTER);
        
        setDocumentBuffer(document);
        
        //focus on the text area the first time the view is shown
        addComponentListener(new ComponentListener() {//{{{
            
            public void componentHidden(ComponentEvent e) {}
            
            public void componentMoved(ComponentEvent e) {}
            
            public void componentResized(ComponentEvent e) {}
            
            public void componentShown(ComponentEvent e) {
                m_textarea.requestFocus();
                removeComponentListener(this);
            }
            
        });//}}}
        
        EditBus.addToBus(this);
    }//}}}
    
    //{{{ handleMessage()
    public void handleMessage(EBMessage message) {
        if (message instanceof PropertyChanged) {
            String key = ((PropertyChanged)message).getKey();
            TextAreaPainter painter = getTextArea().getPainter();
            if (key.equals("source.end-of-line-markers")) {
                painter.setEOLMarkersPainted(jsXe.getBooleanProperty("source.end-of-line-markers", false));
            }
            
            if (key.startsWith("source.") && key.endsWith(".color")) {
                painter.setStyles(
                    new SyntaxStyle[] {
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.text.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.comment.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.doctype.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.attribute.value.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.attribute.value.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.cdata.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.entity.reference.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.element.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.attribute.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.processing.instruction.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.namespace.prefix.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.markup.color")),
                                        SourceViewOptionPane.parseStyle(jsXe.getProperty("source.invalid.color")),
                                      });
            }
        } else {
            if ((message instanceof UndoEvent) || (message instanceof RedoEvent)) {
                //hack to get undo to work properly
                try {
                    int caret = m_textarea.getCaretPosition();
                    m_textarea.setDocument(new SourceViewDocument(m_document));
                    m_textarea.setTokenMarker(new XMLTokenMarker());
                    m_textarea.setCaretPosition(caret);
                } catch (IOException ioe) {
                    Log.log(Log.ERROR, this, ioe);
                }
            }
        }
    }//}}}
    
    //{{{ getTextArea()
    
    public JEditTextArea getTextArea() {
        return m_textarea;
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
        
        ActionManager.removeActionImplementation("cut", m_textarea);
        ActionManager.removeActionImplementation("copy", m_textarea);
        ActionManager.removeActionImplementation("paste", m_textarea);
        ActionManager.removeActionImplementation("find", m_textarea);
        ActionManager.removeActionImplementation("findNext", m_textarea);
        
        return true;
    }//}}}
    
    //{{{ getDocumentViewComponent
    
    public Component getDocumentViewComponent() {
        return this;
    }//}}}

    //{{{ getMenus()
    
    public JMenu[] getMenus() {
        return null;
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
        m_textarea.setDocument(new SourceViewDocument(m_document));
        m_textarea.setTokenMarker(new XMLTokenMarker());
        try {
            m_textarea.getDocument().putProperty(PlainDocument.tabSizeAttribute, Integer.valueOf(document.getProperty(XMLDocument.INDENT, "4")));
        } catch (NumberFormatException e) {
            Log.log(Log.WARNING, this, e.getMessage());
        }
        m_document.addXMLDocumentListener(docListener);
    }//}}}
    
    //{{{ goToLine()
    public boolean goToLine(int lineNo) {
        //not supported yet.
        return false;
    }//}}}
    
    //}}}
    
    //{{{ Private members
    
    //{{{ SourceViewXMLDocumentListener class
    
    private class SourceViewXMLDocumentListener implements XMLDocumentListener {
        
        //{{{ propertyChanged()
        
        public void propertyChanged(XMLDocument source, String key, String oldValue) {
            if (key.equals(XMLDocument.INDENT)) {
                try {
                    m_textarea.getDocument().putProperty(PlainDocument.tabSizeAttribute, Integer.valueOf(source.getProperty(XMLDocument.INDENT, "4")));
                    m_textarea.updateUI();
                } catch (NumberFormatException e) {
                    Log.log(Log.WARNING, this, e.getMessage());
                }
            }
            if (key.equals(XMLDocument.ENCODING)) {
                try {
                    //reload the document
                    m_textarea.setDocument(new SourceViewDocument(m_document));
                    m_textarea.setTokenMarker(new XMLTokenMarker());
                } catch (IOException e) {
                    Log.log(Log.ERROR, this, e);
                }
            }
        }//}}}
        
        //{{{ structureChanged
        
        public void structureChanged(XMLDocument source, AdapterNode location) {}//}}}
        
    }//}}}
    
    //{{{ SourceViewEnter
    
    private class SourceViewEnter implements ActionListener {
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent evt) {
            
            if (!m_textarea.isEditable()) {
                m_textarea.getToolkit().beep();
                return;
            }
            
            m_textarea.setSelectedText("\n"+getLastIndent());
        }//}}}
        
        //{{{ getLastIndent()
        
        private String getLastIndent() {
            boolean softTabs = Boolean.valueOf(m_document.getProperty(XMLDocument.IS_USING_SOFT_TABS, "false")).booleanValue();
            int tabWidth = Integer.parseInt(m_document.getProperty(XMLDocument.INDENT));
            
            int line = m_textarea.getCaretLine();
			StringBuffer indent = new StringBuffer();
            while (line >= 1) {
                String text = m_textarea.getLineText(line);
                for (int i=0; i<text.length(); i++) {
                    char current = text.charAt(i);
                    if (!(current == ' ' ||
                          current == '\t' ||
                          current == '\n'))
                    {
                        int ws = MiscUtilities.getLeadingWhiteSpaceWidth(text, tabWidth);
                        return MiscUtilities.createWhiteSpace(ws, softTabs ? 0 : tabWidth);
                    }
                }
                line--;
            }
            return "";
        }//}}}
        
    }//}}}
    
    //{{{ SourceViewTab
    
    private class SourceViewTab implements ActionListener {
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent evt) {
            
            if (!m_textarea.isEditable())  {
                m_textarea.getToolkit().beep();
                return;
            }
            
            
            boolean softTabs = Boolean.valueOf(m_document.getProperty(XMLDocument.IS_USING_SOFT_TABS, "false")).booleanValue();
            if (softTabs) {
                try {
                    int indent = Integer.parseInt(m_document.getProperty(XMLDocument.INDENT));
                    StringBuffer tab = new StringBuffer();
                    for (int i=0; i<indent; i++) {
                        tab.append(" ");
                    }
                    m_textarea.overwriteSetSelectedText(tab.toString());
                } catch (NumberFormatException nfe) {
                    Log.log(Log.ERROR, this, nfe);
                }
            } else {
                m_textarea.overwriteSetSelectedText("\t");
            }
        }//}}}
        
    }//}}}
    
    //{{{ ensureDefaultProps()
    
    private void ensureDefaultProps(DocumentBuffer document) {
        
    }//}}}
    
    private SourceViewXMLDocumentListener docListener = new SourceViewXMLDocumentListener();
    
    private DocumentBuffer m_document;
    private JEditTextArea m_textarea;
    
    private String m_searchString;
    private String m_replaceString;
    private SourceViewPlugin m_plugin;
    
    //}}}
    
}
