/*
jsxeAboutDialog.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contains the code for the the jsXe about dialog.

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
//}}}

//{{{ Swing components
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
//}}}

//{{{ AWT components
import java.awt.Component;
import java.awt.event.ActionEvent;
//}}}

//}}}

public class jsxeAboutDialog extends AbstractAction {
    
    public jsxeAboutDialog(Component parent) {//{{{
        putValue(Action.NAME, "About jsXe...");
        view = parent;
    }//}}}
    
    public void actionPerformed(ActionEvent e) {//{{{
        String aboutMsg = 
        jsXe.getAppTitle() + " " + jsXe.getVersion()+"\n"+
        "Java Simple XML Editor\n"+
        "Copyright (C) 2002 Ian Lewis\n\n"+
        jsXe.getAppTitle() + " is an XML editor written using swing and JAXP 1.1\n\n"+
        "Authors: Ian Lewis <IanLewis@members.fsf.org>\n\n"+
        "Released under the terms of the GNU General Public License";
        
        Object[] okButton = {"Close"};
        JOptionPane.showOptionDialog(
            view,
            aboutMsg,
            "About jsXe",
            0,
            JOptionPane.INFORMATION_MESSAGE,
            jsXe.getIcon(),
            okButton,
            okButton[0]);
    }//}}}
    
    //{{{ Private Members
    private Component view;
    //}}}
}
