/*
TabbedView.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the code for the frame in Jsxe that contains all the gui
components.

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
import net.sourceforge.jsxe.action.FileCloseAction;
import net.sourceforge.jsxe.action.FileExitAction;
import net.sourceforge.jsxe.action.FileNewAction;
import net.sourceforge.jsxe.action.FileOpenAction;
import net.sourceforge.jsxe.action.FileSaveAction;
import net.sourceforge.jsxe.action.FileSaveAsAction;
import net.sourceforge.jsxe.action.ToolsOptionsAction;
import net.sourceforge.jsxe.action.ToolsViewSourceAction;
import net.sourceforge.jsxe.dom.DOMAdapter;
//}}}

//{{{ Swing components
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
//}}}

//{{{ AWT components
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;
//}}}

//{{{ JAXP classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ DOM uses SAX Exceptions
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
//}}}

//{{{ DOM classes
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
//}}}

//}}}

public class TabbedView extends JFrame {
    
    public TabbedView(int width, int height) {//{{{
        
        //{{{ Construct the Menu Bar
        JMenuBar menubar = new JMenuBar();
        
        //{{{ Add File Menu
        JMenu fileMenu = new JMenu("File");
            JMenuItem menuItem = new JMenuItem(new FileNewAction(this));
            fileMenu.add( menuItem );
            menuItem = new JMenuItem(new FileOpenAction(this));
            fileMenu.add( menuItem );
            fileMenu.addSeparator();
            menuItem = new JMenuItem(new FileSaveAction(this));
            fileMenu.add( menuItem );
            menuItem = new JMenuItem(new FileSaveAsAction(this));
            fileMenu.add( menuItem );
            fileMenu.addSeparator();
            menuItem = new JMenuItem(new FileCloseAction(this));
            fileMenu.add( menuItem );
            menuItem = new JMenuItem(new FileExitAction(this));
            fileMenu.add( menuItem );
        menubar.add(fileMenu);//}}}
        
        /*//{{{ Add Edit Menu
        JMenu editMenu = new JMenu("Edit");
            menuItem = new JMenuItem("Undo");
            menuItem.addActionListener( new EditUndoAction() );
            editMenu.add( menuItem );
            menuItem = new JMenuItem("Redo");
            menuItem.addActionListener( new EditRedoAction() );
            editMenu.add( menuItem );
            editMenu.addSeparator();
            menuItem = new JMenuItem("Cut");
            menuItem.addActionListener( new EditCutAction() );
            editMenu.add( menuItem );
            menuItem = new JMenuItem("Copy");
            menuItem.addActionListener( new EditCopyAction() );
            editMenu.add( menuItem );
            menuItem = new JMenuItem("Paste");
            menuItem.addActionListener( new EditPasteAction() );
            editMenu.add( menuItem );
        menubar.add(editMenu);//}}}*/
        
        //{{{ Add Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
            menuItem = new JMenuItem(new ToolsOptionsAction(this));
            toolsMenu.add( menuItem );
            menuItem = new JMenuItem(new ToolsViewSourceAction(this));
            toolsMenu.add( menuItem );
        menubar.add(toolsMenu);//}}}
        
        //Add Help Menu {{{
        JMenu helpMenu = new JMenu("Help");
            menuItem = new JMenuItem(new jsxeAboutDialog(this));
            helpMenu.add( menuItem );
        menubar.add(helpMenu);//}}}
        
        setJMenuBar(menubar);
        //}}}
        
        docpanel = DocumentPanel.getDocumentPanel(this, null);
        
        tabbedPane.addChangeListener(//{{{
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    //This could be called after removing the only tab
                    if (adapterList.size() != 0) {
                        setAdapter((DOMAdapter)adapterList.get(tabbedPane.getSelectedIndex()));
                        DOMAdapter adapter = docpanel.getDOMAdapter();
                        ((JPanel)tabbedPane.getSelectedComponent()).add(docpanel);
                        String name = "";
                        if (adapter != null) {
                            name = adapter.getName();
                        }
                        setTitle(AppTitle + " - " + name);
                    }
                }
        });//}}}
        
        tabbedPane.setPreferredSize(new Dimension(width, height));
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        pack();
        
        //Set window options
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        ImageIcon icon = new ImageIcon("icons"+System.getProperty("file.separator")+"jsxe.jpg", "Jsxe");
        setIconImage(icon.getImage());
    }//}}}
    
    public DocumentPanel getDocumentPanel() {//{{{
        return docpanel;
    }//}}}
    
    public void setAdapter(DOMAdapter adapter) {//{{{
        if (adapter != null) {
            docpanel = DocumentPanel.getDocumentPanel(this, adapter);
            if (!adapterList.contains(adapter)) {
                adapterList.add(adapter);
                JPanel dummypanel = new JPanel(new BorderLayout());
                dummypanel.add(docpanel, BorderLayout.CENTER);
                tabbedPane.add(adapter.getName(), dummypanel);
            }
        tabbedPane.setSelectedIndex(adapterList.indexOf(adapter));
        setTitle(AppTitle + " - " + docpanel.getDOMAdapter().getName());
        }
    }//}}}
    
    public void close(DOMAdapter adapter) {//{{{
        if (adapterList.contains(adapter)) {
            int index = adapterList.indexOf(adapter);
            adapterList.remove(adapter);
            tabbedPane.remove(index);
            //if the document is not the rightmost tab
            //stateChanged is not called.
            if (index != adapterList.size()) {
                setAdapter((DOMAdapter)adapterList.get(tabbedPane.getSelectedIndex()));
                adapter = docpanel.getDOMAdapter();
                ((JPanel)tabbedPane.getSelectedComponent()).add(docpanel);
                String name = "";
                if (adapter != null) {
                    name = adapter.getName();
                }
                setTitle(AppTitle + " - " + name);
            }
        }
    }//}}}
    
    public String getUntitledLabel() {//{{{
        int untitledNo = 0;
        for (int i=0; i < adapterList.size(); i++) {
            DOMAdapter adapter = (DOMAdapter)adapterList.elementAt(i);
            if ( adapter.getName().startsWith("Untitled-")) {
                // Kinda stolen from jEdit
                try {
					untitledNo = Math.max(untitledNo,Integer.parseInt(adapter.getName().substring(9)));
                }
				catch(NumberFormatException nf) {}
            }
        }
        return "Untitled-" + Integer.toString(untitledNo+1);
    }//}}}
    
    public int getDocumentCount() {//{{{
        return adapterList.size();
    }//}}}
    
    /*
    *************************************************
    Data Fields
    *************************************************
    *///{{{
    private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    private DocumentPanel docpanel;
    private JPanel panel;
    private Vector adapterList = new Vector(10,1);
    private static final String AppTitle = "jsXe";
    //}}}
}
