/*
SourceView.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contions the code for the source viewer in Jsxe.

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

package net.sourceforge.jsxe.gui;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.dom.DOMAdapter;
//}}}

//{{{ Swing components
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
//}}}

//{{{ AWT components
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
//}}}

//}}}

public class SourceView extends JDialog {
    
    public SourceView(TabbedView view, DOMAdapter adapter) {//{{{
        //make a modal dialog
        super(view, true);
        //{{{ Construct Menu Bar
        JMenuBar menuBar = new JMenuBar();
        
        //{{{ Construct File Menu
        JMenu menu = new JMenu("File");
            JMenuItem menuItem = new JMenuItem("Save & Close");
            menuItem.addActionListener( new FileSaveAction(this, view, adapter) );
            menu.add( menuItem );
            menuItem = new JMenuItem( new FileCloseAction(this) );
            menu.add( menuItem );
        menuBar.add( menu );//}}}
        
        //{{{ Construct Edit Menu
        menu = new JMenu("Edit");
            /*menuItem = new JMenuItem("Undo");
            menuItem.addActionListener( new EditUndoAction() );
            menu.add( menuItem );
            menuItem = new JMenuItem("Redo");
            menuItem.addActionListener( new EditRedoAction() );
            menu.add(menuItem);
            menu.addSeparator();*/
            menuItem = new JMenuItem( new EditCutAction() );
            menu.add( menuItem );
            menuItem = new JMenuItem( new EditCopyAction() );
            menu.add(menuItem);
            menuItem = new JMenuItem( new EditPasteAction() );
            menu.add( menuItem );
        menuBar.add( menu );//}}}
        
        setJMenuBar( menuBar );//}}}
        
        panel = new textPanel(adapter.getSource());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        
        //Set Dialog Options
        setSize(dialogWidth,dialogHeight);
        setLocationRelativeTo(view);
        
        setTitle("View Source");
    }//}}}
    
    private class textPanel extends JPanel {//{{{
        
        public textPanel(String text) {//{{{
            textPane = new JTextArea(text);
            textPane.setTabSize(4);
            textPane.setCaretPosition(0);
            textPane.setLineWrap(true);
	 	    textPane.setWrapStyleWord(true);
            scrollPane = new JScrollPane(textPane);
            addComponentListener(new ResizeListener());
            add(scrollPane);
        }//}}}
        
        public String getText() {//{{{
            return textPane.getText();
        }//}}}
        
        private class ResizeListener implements ComponentListener {//{{{
            
            public void componentHidden(ComponentEvent e) {}
            
            public void componentMoved(ComponentEvent e) {}
            
            public void componentResized(ComponentEvent e) {//{{{
                int textWidth=getWidth()-10;
                int textHeight=getHeight()-10;
                
                scrollPane.setPreferredSize(
                    new Dimension(textWidth, textHeight) );
                updateUI();
            }//}}}
            
            public void componentShown(ComponentEvent e) {}
            
        }//}}}
        
        private JScrollPane scrollPane;
        
    }//}}}
    
    private class FileSaveAction implements ActionListener {//{{{
        
        public FileSaveAction(Window p, TabbedView v, DOMAdapter a) {
            adapter = a;
            view = v;
            window = p;
        }
        public void actionPerformed(ActionEvent e) {
            adapter.setSource( view, panel.getText() );
            window.dispose();
        }
        private Window window;
        private TabbedView view;
        private DOMAdapter adapter;
    }//}}}
    
    private class FileCloseAction extends AbstractAction {//{{{
        public FileCloseAction(Window p) {
            putValue(Action.NAME, "Close");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl W"));
            parent=p;
        }
        public void actionPerformed(ActionEvent e) {
            parent.dispose();
        }
        private Window parent;
    }//}}}
    
    /*private class EditUndoAction implements ActionListener {//{{{
        public void actionPerformed(ActionEvent e) {
        
        }
    }//}}}
    
    private class EditRedoAction implements ActionListener {//{{{
        public void actionPerformed(ActionEvent e) {
        
        }
    }//}}}*/
    
    private class EditCutAction extends AbstractAction {//{{{
        public EditCutAction() {
            putValue(Action.NAME, "Cut");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl X"));
        }
        public void actionPerformed(ActionEvent e) {
            textPane.cut();
        }
    }//}}}
    
    private class EditCopyAction extends AbstractAction {//{{{
        public EditCopyAction() {
            putValue(Action.NAME, "Copy");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl C"));
        }
        public void actionPerformed(ActionEvent e) {
            textPane.copy();
        }
    }//}}}
    
    private class EditPasteAction extends AbstractAction {//{{{
        public EditPasteAction() {
            putValue(Action.NAME, "Paste");
            putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl V"));
        }
        public void actionPerformed(ActionEvent e) {
            textPane.paste();
        }
    }//}}}
    
    /*
    *************************************************
    Data Fields
    *************************************************
    *///{{{
    private int dialogWidth=550;
    private int dialogHeight=300;
    private JTextArea textPane;
    private textPanel panel;
    //}}}
}
