/*
SourceView.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contions the code source view in jsXe. This will eventually
be scrapped and a jEdit syntax highlighting view will be added.

This file written by ian Lewis (iml001@bridgewater.edu)
Copyright (C) 2002 ian Lewis

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
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.dom.DOMSerializer;
import net.sourceforge.jsxe.gui.TabbedView;
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

//}}}

public class SourceView extends DocumentView {
    
    protected SourceView() {//{{{
        
        panel = new JPanel();
        
        textarea = new JTextArea("");
        textarea.setTabSize(4);
        textarea.setCaretPosition(0);
        textarea.setLineWrap(false);
        textarea.setWrapStyleWord(false);
        
        JScrollPane scrollPane = new JScrollPane(textarea);
        
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        
    }//}}}
    
    public JMenu[] getMenus() {//{{{
        
        JMenu[] menus = new JMenu[1];
        
        //{{{ Construct Edit Menu
        JMenu menu = new JMenu("Edit");
            JMenuItem menuItem = new JMenuItem("Undo");
            menuItem.addActionListener( new EditUndoAction() );
            menu.add( menuItem );
            menuItem = new JMenuItem("Redo");
            menuItem.addActionListener( new EditRedoAction() );
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem( new EditCutAction() );
            menu.add( menuItem );
            menuItem = new JMenuItem( new EditCopyAction() );
            menu.add(menuItem);
            menuItem = new JMenuItem( new EditPasteAction() );
            menu.add( menuItem );
        //}}}
        
        menus[0] = menu;
        return menus;
    }//}}}
    
    public void setDocument(TabbedView view, XMLDocument document) {//{{{
        
        currentdoc = document;
        textarea.setDocument(new SourceViewDocument(view, document));
        
    }//}}}
    
    public XMLDocument getXMLDocument() {//{{{
        return currentdoc;
    }//}}}
    
    public void close(TabbedView view) {//{{{
        currentdoc.setModel(textarea.getText());
        try {
            currentdoc.validate();
        } catch (Exception e) {}
    }//}}}
    
    //{{{ Private members
    
    private class EditUndoAction implements ActionListener {//{{{
        public void actionPerformed(ActionEvent e) {
            //undo does nothing for now
        }
    }//}}}
    
    private class EditRedoAction implements ActionListener {//{{{
        public void actionPerformed(ActionEvent e) {
            //redo action does nothing for now.
        }
    }//}}}
    
    private class EditCutAction extends AbstractAction {//{{{
        public EditCutAction() {
            putValue(Action.NAME, "Cut");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl X"));
        }
        public void actionPerformed(ActionEvent e) {
            textarea.cut();
        }
    }//}}}
    
    private class EditCopyAction extends AbstractAction {//{{{
        public EditCopyAction() {
            putValue(Action.NAME, "Copy");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl C"));
        }
        public void actionPerformed(ActionEvent e) {
            textarea.copy();
        }
    }//}}}
    
    private class EditPasteAction extends AbstractAction {//{{{
        public EditPasteAction() {
            putValue(Action.NAME, "Paste");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl V"));
        }
        public void actionPerformed(ActionEvent e) {
            textarea.paste();
        }
    }//}}}
    
    private XMLDocument currentdoc;
    private JTextArea textarea;
    private JPanel panel;
    //}}}
    
}
