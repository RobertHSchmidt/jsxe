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
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.DocumentBufferListener;
import net.sourceforge.jsxe.gui.OptionsPanel;
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.dom.XMLDocument;
//}}}

//{{{ Swing components
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
//}}}

//{{{ AWT components
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
//}}}

//{{{ DOM Classes
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ Java base classes
import java.io.IOException;
//}}}

//}}}

/**
 * The SourceView class allows users to view and edit an XML document in raw
 * text form.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class SourceView extends DocumentView {
    
    //{{{ SourceView constructor
    /**
     * Creates a new SourceView for the DocumentBuffer specified.
     * @param buffer the buffer to open.
     * @throws IOException if the buffer cannot be viewed using this view
     */
    public SourceView(TabbedView view, DocumentBuffer buffer) throws IOException {
        
        panel = new JPanel();
        
        textarea = new JTextArea("");
        textarea.setTabSize(4);
        textarea.setCaretPosition(0);
        textarea.setLineWrap(false);
        textarea.setWrapStyleWord(false);
        
        JScrollPane scrollPane = new JScrollPane(textarea);
        
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        
        setDocumentBuffer(view, buffer);
    }//}}}
    
    //{{{ DocumentView methods
    
    //{{{ getMenus()
    
    public JMenu[] getMenus() {
        
        JMenu[] menus = new JMenu[1];
        
        //{{{ Construct Edit Menu
        JMenu menu = new JMenu("Edit");
           // These don't do anything yet.
           // JMenuItem menuItem = new JMenuItem("Undo");
           // menuItem.addActionListener( new EditUndoAction() );
           // menu.add( menuItem );
           // menuItem = new JMenuItem("Redo");
           // menuItem.addActionListener( new EditRedoAction() );
           // menu.add(menuItem);
           // menu.addSeparator();
            JMenuItem menuItem = new JMenuItem( new EditCutAction() );
            menu.add( menuItem );
            menuItem = new JMenuItem( new EditCopyAction() );
            menu.add(menuItem);
            menuItem = new JMenuItem( new EditPasteAction() );
            menu.add( menuItem );
        //}}}
        
        menus[0] = menu;
        return menus;
    }//}}}
    
    //{{{ getOptionsPanel()
    
    public OptionsPanel getOptionsPanel() {
        return null;
    }//}}}
    
    //{{{ setDocumentBuffer()
    
    public void setDocumentBuffer(TabbedView view, DocumentBuffer buffer) throws IOException {
        
        if (m_buffer != null) {
            m_buffer.removeDocumentBufferListener(docListener);
        }
        
        m_buffer = buffer;
        textarea.setDocument(new SourceViewDocument(m_buffer));
        textarea.setTabSize((new Integer(m_buffer.getProperty(XMLDocument.INDENT, "4"))).intValue());
        m_buffer.addDocumentBufferListener(docListener);
        
    }//}}}
    
    //{{{ getDocumentBuffer()
    
    public DocumentBuffer getDocumentBuffer() {
        return m_buffer;
    }//}}}
    
    //{{{ getName()
    
    public String getName() {
        return "Source View";
    }//}}}
    
    //{{{ close()
    
    public boolean close(TabbedView view) {
        m_buffer.removeDocumentBufferListener(docListener);
        return true;
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
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            textarea.paste();
        }//}}}
        
    }//}}}
    
    //{{{ SourceViewBufferListener class
    
    private class SourceViewBufferListener implements DocumentBufferListener {
        
        //{{{ propertiesChanged()
        
        public void propertiesChanged(DocumentBuffer source, String key) {
            if (key.equals(XMLDocument.INDENT)) {
                textarea.setTabSize((new Integer(source.getProperty(XMLDocument.INDENT, "4"))).intValue());
                textarea.updateUI();
            }
        }//}}}
        
        //{{{ nameChanged()
        
        public void nameChanged(DocumentBuffer source, String newName) {}
        //}}}
        
        //{{{ bufferSaved()
        
        public void bufferSaved(DocumentBuffer source) {}
        //}}}
        
    }//}}}
    
    private SourceViewBufferListener docListener = new SourceViewBufferListener();
    
    private DocumentBuffer m_buffer;
    private JTextArea textarea;
    private JPanel panel;
    //}}}
    
}
