/*
SourceViewDocument.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This is the model for the source view.

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
import net.sourceforge.jsxe.dom.DOMSerializer;
import net.sourceforge.jsxe.dom.XMLDocument;
//}}}

//{{{ AWT components
import java.awt.Component;
import java.awt.Toolkit;
//}}}

//{{{ Swing components
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.GapContent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleContext;
//}}}

//{{{ DOM classes
import org.w3c.dom.DOMException;
//}}}

//{{{ Java base classes
import java.io.IOException;
import java.io.StringWriter;
//}}}

import java.util.StringTokenizer;

//}}}

public class SourceViewDocument extends DefaultStyledDocument {

    protected SourceViewDocument(Component view, XMLDocument doc) {//{{{
        super(new GapContent(), new StyleContext());
        document = doc;
        
        if (doc != null) {
            StringWriter writer = new StringWriter();
            
            //formatting disabled because it doesn't work right
            DOMSerializer serializer = new DOMSerializer(false);
            try {
                serializer.serialize(document.getDocument(), writer);
                super.insertString(0, writer.toString(), new SimpleAttributeSet());
                
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(view, ioe, "I/O Error", JOptionPane.WARNING_MESSAGE);
            } catch (BadLocationException ble) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//}}}

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {//{{{

        try {
            super.insertString(offs, str, a);
            document.setModel(super.getText(0,super.getLength()));
        } catch (DOMException dome) {
            Toolkit.getDefaultToolkit().beep();
        }

    }//}}}

    public void remove(int offs, int len) throws BadLocationException {//{{{
        
        try {
            super.remove(offs, len);
            document.setModel(super.getText(0,super.getLength()));
        } catch (DOMException dome) {
            Toolkit.getDefaultToolkit().beep();
        }
        
    }//}}}

    //{{{ Private members
    XMLDocument document;
    //}}}

}
