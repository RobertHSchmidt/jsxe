/*
TabbedView.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the code for the frame in jsXe that contains
the JTabbedPane handles the DocumentViews.

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

package net.sourceforge.jsxe.gui;

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
import net.sourceforge.jsxe.gui.view.DocumentView;
import net.sourceforge.jsxe.gui.view.DocumentViewFactory;
import net.sourceforge.jsxe.action.*;
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
import java.util.Enumeration;
import java.util.Vector;
//}}}

//}}}

/**
 * The view container that holds the JTabbedPane that holds
 * all open DocumentViews.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see DocumentView
 */
public class TabbedView extends JFrame {
    
    //{{{ TabbedView constructor
    /**
     * Constructs a new TabbedView
     */
    public TabbedView(DocumentBuffer buffer) throws IOException {
        
        //{{{ load global properties
        
        //Make sure user defined properties don't cause unwanted exceptions.
        int width = Integer.valueOf(jsXe.getDefaultProperty(_WIDTH)).intValue();
        try {
            width = Integer.valueOf(jsXe.getProperty(_WIDTH)).intValue();
        } catch (NumberFormatException e) {}
        
        int height = Integer.valueOf(jsXe.getDefaultProperty(_HEIGHT)).intValue();
        try {
            height = Integer.valueOf(jsXe.getProperty(_HEIGHT)).intValue();
        } catch (NumberFormatException e) {}
        
        int x = Integer.valueOf(jsXe.getDefaultProperty(_X)).intValue();
        try {
            x = Integer.valueOf(jsXe.getProperty(_X)).intValue();
        } catch (NumberFormatException e) {}
        
        int y = Integer.valueOf(jsXe.getDefaultProperty(_Y)).intValue();
        try {
            y = Integer.valueOf(jsXe.getProperty(_Y)).intValue();
        } catch (NumberFormatException e) {}
        
        //}}}
        
        createDefaultMenuItems();
        updateMenuBar();
        
        tabbedPane.addChangeListener(//{{{
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    //it's possible to change to another file
                    //that is using another view.
                    updateMenuBar();
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
        
        addDocumentBuffer(buffer);
    }//}}}
    
    //{{{ getDocumentBuffer()
    
    public DocumentBuffer getDocumentBuffer() {
        return jsXe.getDocumentBuffers()[tabbedPane.getSelectedIndex()];
    }//}}}
    
    //{{{ getDocumentView()
    /**
     * Gets the current DocumentView that is being displayed
     * by the JTabbedPane
     * @return the current DocumentView
     */
    public DocumentView getDocumentView() {
        return (DocumentView)tabbedPane.getSelectedComponent();
    }//}}}
    
    //{{{ addDocumentBuffer()
    /**
     * Adds a buffer to the main view. This is essentially opening
     * the document in jsXe.
     * @param doc The DocumentBuffer to add to the view
     */
    public void addDocumentBuffer(DocumentBuffer buffer) throws IOException {
        if (buffer != null) {
            DocumentViewFactory factory = DocumentViewFactory.newInstance();
            
            Enumeration types = factory.getAvailableViewTypes();
            
            String error = null;
            DocumentView newDocView;
            
            while (types.hasMoreElements()) {
                
                factory.setDocumentViewType((String)types.nextElement());
                
                try {
                    
                    newDocView = factory.newDocumentView(this, buffer.getXMLDocument());
                    
                   // newDocView.setDocumentBuffer(this, buffer);
                    
                    buffer.addDocumentBufferListener(new DocumentBufferListener() {//{{{
                
                        public void propertiesChanged(DocumentBuffer source, String propertyKey) {//{{{
                            if (propertyKey.equals("dirty")) {
                                //It's dirtyness has changed
                                //change the tab title
                                
                                DocumentBuffer[] buffers = jsXe.getDocumentBuffers();
                                for (int i=0; i < buffers.length; i++) {
                                    if (buffers[i] == source) {
                                        tabbedPane.setIconAt(i, getTabIcon(source));
                                        return;
                                    }
                                }
                            }
                        }//}}}
                        
                        public void nameChanged(DocumentBuffer source, String newName) {//{{{
                            DocumentBuffer[] buffers = jsXe.getDocumentBuffers();
                            for (int i=0; i < buffers.length; i++) {
                                if (buffers[i] == source) {
                                    tabbedPane.setTitleAt(i, source.getName());
                                }
                            }
                        };//}}}
                        
                        public void bufferSaved(DocumentBuffer source) {}
                        
                    });//}}}
                    
                    tabbedPane.addTab(buffer.getName(), getTabIcon(buffer), newDocView);
                    tabbedPane.setSelectedComponent(newDocView);
                    
                    updateTitle();
                    updateMenuBar();
                    
                    buffer.addDocumentBufferListener(m_bufferListener);
                    
                    if (error != null) {
                        String msg = "Could not open buffer in document views:\n\n"+error;
                        JOptionPane.showMessageDialog(this, msg, "I/O Error", JOptionPane.WARNING_MESSAGE);
                    }
                    
                    return;
                    
                } catch (IOException ioe) {
                    if (error == null) {
                        error = buffer.getName() + ": "+ioe.getMessage() + "\n";
                    } else {
                        error += buffer.getName() + ": "+ioe.getMessage() + "\n";
                    }
                }
            }
            
            throw new IOException("Could not open buffer in any installed document views\n\n" + error);
            
        }
    }//}}}
    
    //{{{ setDocumentBuffer()
    /**
     * Sets the current buffer and makes sure it is displayed. If
     * the document is not already open then this method does nothing.
     * @param buffer The buffer to set
     * @return true if the document was set and is visable
     */
    public boolean setDocumentBuffer(DocumentBuffer buffer) throws IOException {
        boolean success = true;
        if (buffer != null) {
            DocumentBuffer[] buffers = jsXe.getDocumentBuffers();
            for (int i=0; i < buffers.length; i++) {
                if (buffers[i] == buffer) {
                    tabbedPane.setSelectedIndex(i);
                    success = true;
                }
            }
            updateTitle();
            updateMenuBar();
        }
        return success;
    }//}}}
    
    //{{{ removeDocumentBuffer()
    /**
     * Removes a buffer from the view. If the buffer passed is not
     * already open this method does nothing.
     * @param buffer The document to remove
     * @return true if the document was removed
     */
    public boolean removeDocumentBuffer(DocumentBuffer buffer) {
        boolean success = false;
        if (buffer != null) {
            DocumentBuffer[] buffers = jsXe.getDocumentBuffers();
            for (int i=0; i < buffers.length; i++) {
                if (buffers[i] == buffer) {
                    
                    DocumentView docView = (DocumentView)tabbedPane.getComponentAt(i);
                    
                    //only close if we _mean_ it.
                    if (docView.close(this)) {
                        tabbedPane.remove(i);
                        //if the tab removed is not the rightmost tab
                        //stateChanged is not called for some
                        //reason so we must update the title.
                        updateTitle();
                        success = true;
                    }
                }
            }
        }
        return success;
    }//}}}
    
    //{{{ getBufferCount()
    /**
     * Gets the number of open buffers.
     * @return The number of documents open in this view.
     */
    public int getBufferCount() {
        return tabbedPane.getTabCount();
    }//}}}
    
    //{{{ close()
    /**
     * Closes the view.
     * @return true if you really want to close.
     * @throws IOException if all the DocumentBuffers could not be saved
     *                     due to an I/O error.
     */
    public boolean close() throws IOException {
        
        DocumentBuffer[] buffers = jsXe.getDocumentBuffers();
        DocumentView currentDocView = null;
        
        //sequentially close all the document views
        for (int i=0; i < buffers.length; i++) {
            if (!jsXe.closeDocumentBuffer(this, buffers[i])) {
                return false;
            }
        }
        
        //save properties
        Rectangle bounds = getBounds();
        
        jsXe.setProperty("tabbedview.width",Integer.toString((int)bounds.getWidth()));
        jsXe.setProperty("tabbedview.height",Integer.toString((int)bounds.getHeight()));
        jsXe.setProperty("tabbedview.x",Integer.toString((int)bounds.getX()));
        jsXe.setProperty("tabbedview.y",Integer.toString((int)bounds.getY()));
        
        return true;
    }//}}}
    
    //{{{ Private static members
    
    private static final String _WIDTH = "tabbedview.width";
    private static final String _HEIGHT = "tabbedview.height";
    private static final String _X = "tabbedview.x";
    private static final String _Y = "tabbedview.y";
    
    private static final ImageIcon m_cleanIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/clean.png"), "clean");
    private static final ImageIcon m_dirtyIcon = new ImageIcon(jsXe.class.getResource("/net/sourceforge/jsxe/icons/dirty.png"), "dirty");
    
    //}}}
    
    //{{{ Private members
    
    //{{{ createDefaultMenuItems()
    
    private void createDefaultMenuItems() {
        
        //{{{ Create File Menu
        m_fileMenu = new JMenu("File");
        m_fileMenu.setMnemonic('F');
            JMenuItem menuItem = new JMenuItem(new FileNewAction(this));
            m_fileMenu.add( menuItem );
            menuItem = new JMenuItem(new FileOpenAction(this));
            m_fileMenu.add( menuItem );
            m_fileMenu.addSeparator();
            menuItem = new JMenuItem(new FileSaveAction(this));
            m_fileMenu.add( menuItem );
            menuItem = new JMenuItem(new FileSaveAsAction(this));
            m_fileMenu.add( menuItem );
            m_fileMenu.addSeparator();
            menuItem = new JMenuItem(new FileReloadAction(this));
            m_fileMenu.add( menuItem );
            m_fileMenu.addSeparator();
            menuItem = new JMenuItem(new FileCloseAction(this));
            m_fileMenu.add( menuItem );
            menuItem = new JMenuItem(new FileCloseAllAction(this));
            m_fileMenu.add( menuItem );
            menuItem = new JMenuItem(new FileExitAction(this));
            m_fileMenu.add( menuItem );
        //}}}
        
        //{{{ Create View Menu
        m_viewMenu = new JMenu("View");
        m_viewMenu.setMnemonic('V');
            menuItem = new JMenuItem(new SetDefaultViewAction());
            m_viewMenu.add( menuItem );
            menuItem = new JMenuItem(new SetSourceViewAction());
            m_viewMenu.add( menuItem );
        //}}}
        
        //{{{ Create Tools Menu
        m_toolsMenu = new JMenu("Tools");
        m_toolsMenu.setMnemonic('T');
            menuItem = new JMenuItem(new ToolsOptionsAction(this));
            m_toolsMenu.add( menuItem );
        //}}}
        
        //{{{ Create Help Menu
        m_helpMenu = new JMenu("Help");
        m_helpMenu.setMnemonic('H');
            menuItem = new JMenuItem(new jsxeAboutDialog(this));
            m_helpMenu.add(menuItem);
        //}}}

    }//}}}
    
    //{{{ updateTitle()
    /**
     * Updates the frame title. Useful when the tab has changed to a
     * different open document..
     */
    private void updateTitle() {
        DocumentView currentDocView = getDocumentView();
        if (currentDocView != null) {
            DocumentBuffer buffer = getDocumentBuffer();
            String name = "";
            if (buffer != null) {
                name = buffer.getName();
            }
            setTitle(jsXe.getAppTitle() + " - " + name);
        } else {
            setTitle(jsXe.getAppTitle());
        }
    }//}}}
    
    //{{{ updateMenuBar()
    /**
     * Updates the menubar. Useful when the DocumentView has changed.
     */
    private void updateMenuBar() {
        
        JMenuBar menubar = new JMenuBar();
        DocumentView currentDocView = getDocumentView();
        
        if (currentDocView != null) {
            
            menubar.add(m_fileMenu);

            //Add View Specific Menus
            JMenu[] menus = currentDocView.getMenus();
            if (menus != null) {
                for (int i=0;i<menus.length;i++) {
                    menubar.add(menus[i]);
                }
            }

            menubar.add(m_viewMenu);
            
            menubar.add(m_toolsMenu);
            
            menubar.add(m_helpMenu);
            
            setJMenuBar(menubar);
            
            //Need to cause a repaint after menubar is changed.
            getRootPane().revalidate();
        }
    }//}}}
    
    //{{{ setDocumentView()
    /**
     * Sets the DocumentView.
     */
    private void setDocumentView(DocumentView newView) {
        
        DocumentView oldView = getDocumentView();
        
        int index = tabbedPane.getSelectedIndex();
        
        if (oldView != null) {
            
            //close the previous view
            oldView.close(this);
            
            DocumentBuffer currentBuffer = getDocumentBuffer();
            
            //no exceptions? cool. register the new view
            tabbedPane.remove(oldView);
            tabbedPane.add(newView, index);
            tabbedPane.setIconAt(index, getTabIcon(currentBuffer));
            tabbedPane.setTitleAt(index, currentBuffer.getName());
            tabbedPane.setSelectedIndex(index);
            updateMenuBar();
        }
    }//}}}
    
    //{{{ getTabIcon
    
    private ImageIcon getTabIcon(DocumentBuffer buffer) {
        
        if (buffer.isDirty()) {
            //Mark the tab title as dirty
            return m_dirtyIcon;
        }
        
        return m_cleanIcon;
    }//}}}
    
    //{{{ SetDefaultViewAction class
    /**
     * Temporary class to change views.
     */
    private class SetDefaultViewAction extends AbstractAction {
        
        //{{{ SetDefaultViewAction constructor
        
        public SetDefaultViewAction() {
            putValue(Action.NAME, "Tree View");
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            
            DocumentBuffer buffer = getDocumentBuffer();
            
            try {
                DocumentViewFactory factory = DocumentViewFactory.newInstance();
                DocumentView view = factory.newDocumentView(TabbedView.this, buffer.getXMLDocument());
                setDocumentView(view);
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(TabbedView.this, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
            }
        }//}}}
        
    }//}}}
    
    //{{{ SetSourceViewAction class
    /**
     * Temporary class to change views.
     */
    private class SetSourceViewAction extends AbstractAction {
        
        //{{{ SetSourceViewAction constructor
        
        public SetSourceViewAction() {
            putValue(Action.NAME, "Source View");
        }//}}}
        
        //{{{ actionPerformed()
        
        public void actionPerformed(ActionEvent e) {
            
            DocumentBuffer buffer = getDocumentBuffer();
            
            try {
                DocumentViewFactory factory = DocumentViewFactory.newInstance();
                factory.setDocumentViewType("documentview.sourceview");
                DocumentView view = factory.newDocumentView(TabbedView.this, buffer.getXMLDocument());
                setDocumentView(view);
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(TabbedView.this, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
            }
        }//}}}
        
    }//}}}
    
    //{{{ WindowHandler class
    
    private class WindowHandler extends WindowAdapter {
        
        //{{{ windowClosing()
        
        public void windowClosing(WindowEvent e) {
			jsXe.exit(TabbedView.this);
		}//}}}
        
    }//}}}
    
    private JMenu m_fileMenu;
    private JMenu m_viewMenu;
    private JMenu m_toolsMenu;
    private JMenu m_helpMenu;
    
    private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    //The current document
    private JPanel panel;
    private DocumentBufferListener m_bufferListener = new DocumentBufferListener() {//{{{
        
        public void nameChanged(DocumentBuffer source, String newName) {
            updateTitle();
           // tabbedPane.updateUI();
        }
        
        public void propertiesChanged(DocumentBuffer source, String key) {}
    
        public void bufferSaved(DocumentBuffer source) {}
        
    };//}}}
    
    //}}}
    
    //}}}
}
