/*
jsxeAboutDialog.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the code for the the jsXe about dialog.

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
import net.sourceforge.jsxe.CustomFileFilter;
//}}}

//{{{ Swing components
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
//}}}

//{{{ AWT Components
import java.awt.Component;
//}}}

//{{{ Java components
import java.io.File;
import java.util.ArrayList;
//}}}

//}}}

public class jsxeFileDialog extends JFileChooser {
    
    //{{{ jsxeFileDialog constructor
    
    public jsxeFileDialog(File file) {
        super(file);
        init();
    }//}}}
    
    //{{{ jsxeFileDialog constructor
    
    public jsxeFileDialog(String path) {
        super(path);
        init();
    }//}}}
    
    //{{{ showOpenDialog()
    
    public int showOpenDialog(Component parent) {
        setDialogTitle("Open");
        return super.showOpenDialog(parent);
    }//}}}
    
    //{{{ showSaveDialog()
    
    public int showSaveDialog(Component parent) {
        setDialogTitle("Save As");
        return super.showSaveDialog(parent);
    }//}}}
    
    //{{{ Private members
    
    //{{{ init()
    
    private void init() {
        //Add a filter to display only XML files
        ArrayList extentionList = new ArrayList();
        extentionList.add("xml");
        CustomFileFilter firstFilter = new CustomFileFilter(extentionList, "XML Documents");
        addChoosableFileFilter(firstFilter);
        //Add a filter to display only XSL files
        extentionList = new ArrayList();
        extentionList.add("xsl");
        addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL Stylesheets"));
        //Add a filter to display only XSL:FO files
        extentionList = new ArrayList();
        extentionList.add(new String("fo"));
        addChoosableFileFilter(new CustomFileFilter(extentionList, "XSL:FO Documents"));
        //Add a filter to display only Schema files
        extentionList = new ArrayList();
        extentionList.add(new String("xsd"));
        addChoosableFileFilter(new CustomFileFilter(extentionList, "XML Schema"));
        //Add a filter to display all formats
        extentionList = new ArrayList();
        extentionList.add(new String("xml"));
        extentionList.add(new String("xsl"));
        extentionList.add(new String("fo"));
        extentionList.add(new String("xsd"));
        addChoosableFileFilter(new CustomFileFilter(extentionList, "All XML Documents"));
        
        //The "All Files" file filter is added to the dialog
        //by default. Put it at the end of the list.
        FileFilter all = getAcceptAllFileFilter();
        removeChoosableFileFilter(all);
        addChoosableFileFilter(all);
        setFileFilter(firstFilter);
    }//}}}
    
    //}}}

}
