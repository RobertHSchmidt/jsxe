/*
FileSaveAsAction.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the action taken when a user selects
Save As... from the file menu.

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

package net.sourceforge.jsxe.action;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.CustomFileFilter;
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.gui.TabbedView;
//}}}

//{{{ DOM classes
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.ParserConfigurationException;
//}}}

//{{{ Swing components
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
//}}}

//{{{ AWT components
import java.awt.event.ActionEvent;
//}}}

//{{{ Java base classes
import java.io.File;
import java.io.IOException;
import java.util.Vector;
//}}}

//}}}

public class FileSaveAsAction extends AbstractAction {
    
    public FileSaveAsAction(TabbedView parent) {//{{{
        putValue(Action.NAME, "Save As...");
        view = parent;
    }//}}}
    
    public void actionPerformed(ActionEvent e) {//{{{
        
        DocumentBuffer currentBuffer = view.getDocumentView().getDocumentBuffer();
        //  if XMLFile is null, defaults to home directory
        JFileChooser saveDialog = new JFileChooser(currentBuffer.getFile());
        saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
        saveDialog.setDialogTitle("Save As");
        
        //Add a filter to display only XML files
        Vector extentionList = new Vector();
        extentionList.add(new String("xml"));
        CustomFileFilter firstFilter = new CustomFileFilter(extentionList, "XML Documents");
        saveDialog.addChoosableFileFilter(firstFilter);
        //Add a filter to display only XSL files
        extentionList = new Vector();
        extentionList.add(new String("xsl"));
        saveDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL Stylesheets"));
        //Add a filter to display only XSL:FO files
        extentionList = new Vector();
        extentionList.add(new String("fo"));
        saveDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL:FO Documents"));
        //Add a filter to display all formats
        extentionList = new Vector();
        extentionList.add(new String("xml"));
        extentionList.add(new String("xsl"));
        extentionList.add(new String("fo"));
        saveDialog.addChoosableFileFilter(new CustomFileFilter(extentionList, "All XML Documents"));
        
        //The "All Files" file filter is added to the dialog
        //by default. Put it at the end of the list.
        FileFilter all = saveDialog.getAcceptAllFileFilter();
        saveDialog.removeChoosableFileFilter(all);
        saveDialog.addChoosableFileFilter(all);
        saveDialog.setFileFilter(firstFilter);
        
        int returnVal = saveDialog.showSaveDialog(view);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                
                File selectedFile = saveDialog.getSelectedFile();
                boolean reallySave = true;
                if (selectedFile.exists()) {
                    //If it's dirty ask if you want to save.
                    String msg = "The file "+selectedFile.getName()+" already exists. Are you sure you want to overwrite it?";
                    String title = "File Exists";
                    int optionType = JOptionPane.YES_NO_OPTION;
                    int messageType = JOptionPane.WARNING_MESSAGE;
                    
                    returnVal = JOptionPane.showConfirmDialog(view,
                                        msg,
                                        title,
                                        optionType,
                                        messageType);
                    if (returnVal != JOptionPane.YES_OPTION) {
                        reallySave = false;
                    }
                }
                
                if (reallySave) {
                    
                    DocumentBuffer buffer = jsXe.getOpenBuffer(selectedFile);
                    
                    //If the document is already open and
                    //it isn't the current document
                    if (buffer != null && !buffer.equalsOnDisk(currentBuffer)) {
                        
                        //If the saved-to document is already open we
                        //need to close that tab and save this tab
                        //as that one.
                        
                        jsXe.closeDocumentBuffer(view, buffer);
                        currentBuffer.saveAs(selectedFile);
                        
                    } else {
                        currentBuffer.saveAs(selectedFile);
                    }
                    
                }
                
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(view, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//}}}
    
    //{{{ Private members
    private TabbedView view;
    //}}}
}
   
