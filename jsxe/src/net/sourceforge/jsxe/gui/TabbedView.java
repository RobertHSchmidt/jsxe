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
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
//}}}

//{{{ AWT components
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//}}}

//{{{ Java base classes
import java.io.IOException;
import java.util.Vector;
//}}}

//}}}

public class TabbedView extends JFrame {
    
    public TabbedView() {//{{{
        
        int width = Integer.valueOf(jsXe.getProperty("tabbedview.width")).intValue();
        int height = Integer.valueOf(jsXe.getProperty("tabbedview.height")).intValue();
        int x = Integer.valueOf(jsXe.getProperty("tabbedview.x")).intValue();
        int y = Integer.valueOf(jsXe.getProperty("tabbedview.y")).intValue();
        
        DocumentViewFactory factory = DocumentViewFactory.newInstance();
       // docView = factory.newDocumentView();
        
       // updateMenuBar();
        
        tabbedPane.addChangeListener(//{{{
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    updateTitle();
                }
           });//}}}
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        pack();
        
        //Set window options
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowHandler());
        
        setIconImage(jsXe.getIcon().getImage());
        
        setBounds(new Rectangle(x, y, width, height));
    }//}}}
    
    public DocumentView getDocumentView() {//{{{
        return (DocumentView)tabbedPane.getSelectedComponent();
    }//}}}
    
    public void addDocument(XMLDocument doc) throws IOException {//{{{
        if (doc != null) {
            DocumentViewFactory factory = DocumentViewFactory.newInstance();
            DocumentView newDocView = factory.newDocumentView();
            newDocView.setDocument(this,doc);
            tabbedPane.add(doc.getName(), newDocView);
            tabbedPane.setSelectedComponent(newDocView);
            updateTitle();
            updateMenuBar();
        }
    }//}}}
    
    public void setDocument(XMLDocument doc) throws IOException {//{{{
        if (doc != null) {
            XMLDocument[] docs = jsXe.getXMLDocuments();
            for (int i=0; i < docs.length; i++) {
                if (docs[i] == doc) {
                    tabbedPane.setSelectedIndex(i);
                }
            }
            updateTitle();
            updateMenuBar();
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
                    updateTitle();
                }
            }
        }
    }//}}}
    
    public int getDocumentCount() {//{{{
        return tabbedPane.getTabCount();
    }//}}}
    
    public void updateTitle() {//{{{
        DocumentView currentDocView = getDocumentView();
        if (currentDocView != null) {
            XMLDocument document = currentDocView.getXMLDocument();
            String name = "";
            if (document != null) {
                name = document.getName();
            }
            setTitle(jsXe.getAppTitle() + " - " + name);
        } else {
            setTitle(jsXe.getAppTitle());
        }
    }//}}}
    
    public void close() {//{{{
        
        XMLDocument[] docs = jsXe.getXMLDocuments();
        DocumentView currentDocView = null;
        
        //sequentially close all the document views
        for (int i=0; i < docs.length; i++) {
            currentDocView = (DocumentView)tabbedPane.getComponentAt(0);
            currentDocView.close(this);
            tabbedPane.remove(0);
        }
        
        //save properties
        Rectangle bounds = getBounds();
        
        jsXe.setProperty("tabbedview.width",Integer.toString((int)bounds.getWidth()));
        jsXe.setProperty("tabbedview.height",Integer.toString((int)bounds.getHeight()));
        jsXe.setProperty("tabbedview.x",Integer.toString((int)bounds.getX()));
        jsXe.setProperty("tabbedview.y",Integer.toString((int)bounds.getY()));
    }//}}}
    
    //{{{ Private members
    
    private void updateMenuBar() {//{{{
        JMenuBar menubar = new JMenuBar();
        DocumentView currentDocView = getDocumentView();
        
        if (currentDocView != null) {
            
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
                JMenu[] menus = currentDocView.getMenus();
                if (menus != null) {
                    for (int i=0;i<menus.length;i++) {
                        menubar.add(menus[i]);
                    }
                }
            //}}}
            
            //{{{ Add View Menu
            JMenu viewMenu = new JMenu("View");
                menuItem = new JMenuItem(new SetDefaultViewAction());
                viewMenu.add( menuItem );
                menuItem = new JMenuItem(new SetSourceViewAction());
                viewMenu.add( menuItem );
            menubar.add(viewMenu);
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
            
            //Need to cause a repaint after menubar is changed.
            getRootPane().revalidate();
        }
    }//}}}
    
    private void setDocumentView(DocumentView newView) {//{{{
        
        XMLDocument[] docs = jsXe.getXMLDocuments();
        DocumentView oldView = getDocumentView();
        int index = tabbedPane.getSelectedIndex();
        
        
        XMLDocument currentDoc = docs[index];
        
        if (oldView != null) {
            try {
                //close the previous view
                oldView.close(this);
                
                //try to open the document in the new view
                newView.setDocument(this, currentDoc);
                
                //no exceptions? cool. register the new view
                tabbedPane.remove(oldView);
                tabbedPane.add(newView, index);
                tabbedPane.setTitleAt(index, currentDoc.getName());
                tabbedPane.setSelectedIndex(index);
                updateMenuBar();
            } catch (IOException ioe) {
                //Some sort of error occured
                //We didn't register the new view yet.
                //So do nothing but display an error.
                JOptionPane.showMessageDialog(this, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//}}}
    
    //temporary classes to change views.
    private class SetDefaultViewAction extends AbstractAction {//{{{
        
        public SetDefaultViewAction() {
            putValue(Action.NAME, "Tree View");
        }
        
        public void actionPerformed(ActionEvent e) {
            DocumentViewFactory factory = DocumentViewFactory.newInstance();
            DocumentView view = factory.newDocumentView();
            setDocumentView(view);
        }
        
    }//}}}
    
    private class SetSourceViewAction extends AbstractAction {//{{{
        
        public SetSourceViewAction() {
            putValue(Action.NAME, "Source View");
        }
        
        public void actionPerformed(ActionEvent e) {
            DocumentViewFactory factory = DocumentViewFactory.newInstance();
            factory.setDocumentViewType("documentview.sourceview");
            DocumentView view = factory.newDocumentView();
            setDocumentView(view);
        }
        
    }//}}}
    
    private class WindowHandler extends WindowAdapter {//{{{
        public void windowClosing(WindowEvent e) {
			jsXe.exit(TabbedView.this);
		}
    }//}}}
    
    private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    //The current document
    private JPanel panel;
    //}}}
}
