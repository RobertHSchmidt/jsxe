/*
TabbedView.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the code for the frame in jsXe that contains
the JTabbedPane handles the DocumentViews.

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
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.gui.view.DocumentView;
import net.sourceforge.jsxe.gui.view.DocumentViewFactory;
import net.sourceforge.jsxe.action.FileCloseAction;
import net.sourceforge.jsxe.action.FileExitAction;
import net.sourceforge.jsxe.action.FileNewAction;
import net.sourceforge.jsxe.action.FileOpenAction;
import net.sourceforge.jsxe.action.FileSaveAction;
import net.sourceforge.jsxe.action.FileSaveAsAction;
import net.sourceforge.jsxe.action.ToolsOptionsAction;
//import net.sourceforge.jsxe.action.ToolsViewSourceAction;
//}}}

//{{{ Swing components
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
//}}}

//{{{ AWT components
import java.awt.BorderLayout;
import java.awt.Dimension;
//}}}

//{{{ Java base classes
//import java.io.File;
import java.util.Vector;
//}}}

//}}}

public class TabbedView extends JFrame {
    
    public TabbedView(int width, int height) {//{{{
        
        DocumentViewFactory factory = DocumentViewFactory.newInstance();
        docview = factory.newDocumentView();
        
        updateMenuBar();
        
        tabbedPane.addChangeListener(//{{{
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    //This could be called after removing the only tab
                    //if there is only 1 or 0 tabs then we don't need to
                    //do this stuff.
                    if (tabbedPane.getTabCount() > 1) {
                        XMLDocument[] docs = jsXe.getXMLDocuments();
                        setDocument(docs[tabbedPane.getSelectedIndex()]);
                        ((JPanel)tabbedPane.getSelectedComponent()).add(docview);
                        updateTitle();
                    }
                }
           });//}}}
        
        tabbedPane.setPreferredSize(new Dimension(width, height));
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        pack();
        
        //Set window options
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setIconImage(jsXe.getIcon().getImage());
    }//}}}
    
    public DocumentView getDocumentView() {//{{{
        return docview;
    }//}}}
    
    public void addDocument(XMLDocument doc) {//{{{
        if (doc != null) {
            docview.setDocument(this,doc);
            JPanel dummypanel = new JPanel(new BorderLayout());
            dummypanel.add(docview, BorderLayout.CENTER);
            tabbedPane.add(doc.getName(), dummypanel);
            tabbedPane.setSelectedComponent(dummypanel);
            updateTitle();
        }
    }//}}}
    
    public void setDocument(XMLDocument doc) {//{{{
        if (doc != null) {
            XMLDocument[] docs = jsXe.getXMLDocuments();
            for (int i=0; i < docs.length; i++) {
                if (docs[i] == doc) {
                    docview.setDocument(this,doc);
                    tabbedPane.setSelectedIndex(i);
                }
            }
            updateTitle();
        }
    }//}}}
    
    public void removeDocument(XMLDocument doc) {//{{{
        if (doc != null) {
            XMLDocument[] docs = jsXe.getXMLDocuments();
            for (int i=0; i < docs.length; i++) {
                if (docs[i] == doc) {
                    tabbedPane.remove(i);
                    //if the tab removed is not the rightmost tab
                    //stateChanged is not called for some
                    //reason.
                    if (i != tabbedPane.getTabCount()) {
                        docview.setDocument(this,docs[tabbedPane.getSelectedIndex()+1]);
                        tabbedPane.setSelectedIndex(i);
                        ((JPanel)tabbedPane.getSelectedComponent()).add(docview);
                        updateTitle();
                    }
                }
            }
        }
    }//}}}
    
    public int getDocumentCount() {//{{{
        return tabbedPane.getTabCount();
    }//}}}
    
    public void updateTitle() {//{{{
        XMLDocument document = docview.getXMLDocument();
        String name = "";
        if (document != null) {
            name = document.getName();
        }
        setTitle(jsXe.getAppTitle() + " - " + name);;
    }//}}}
    
    //{{{ Private members
    
    private void updateMenuBar() {//{{{
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
        
        //{{{ Add View Specific Menus
            JMenu[] menus = docview.getMenus();
            for (int i=0;i<menus.length;i++) {
                menubar.add(menus[i]);
            }
        //}}}
        
        //{{{ Add Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
            menuItem = new JMenuItem(new ToolsOptionsAction(this));
            toolsMenu.add( menuItem );
        menubar.add(toolsMenu);//}}}
        
        //{{{ Add Help Menu
        JMenu helpMenu = new JMenu("Help");
            menuItem = new JMenuItem(new jsxeAboutDialog(this));
            helpMenu.add( menuItem );
        menubar.add(helpMenu);//}}}
        
        setJMenuBar(menubar);
    }//}}}
    
    private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    private DocumentView docview;
    private JPanel panel;
    //}}}
}
