/*
SourceViewDocument.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This is the model for the source view.

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

package net.sourceforge.jsxe.gui.view;

//{{{ imports
/*
All classes are listed explicitly so
it is easy to see which package it
belongs to.
*/

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.dom.DOMSerializer;
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.gui.TabbedView;
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

    protected SourceViewDocument(TabbedView parent, XMLDocument doc) throws IOException {//{{{
        super(new GapContent(), new StyleContext());
        document = doc;
        view = parent;
        
        if (doc != null) {
            
            //formatting disabled because it doesn't work right
            DOMSerializer serializer = new DOMSerializer(false);
            try {
                
                super.insertString(0, document.getSource(), new SimpleAttributeSet());
                
            } catch (BadLocationException ble) {
                //This should never happen. If it does however jsXe will
                //act abnormally so crash.
                jsXe.exiterror(view, ble.toString(), 1);
            }
        }
    }//}}}

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {//{{{

        try {
            super.insertString(offs, str, a);
            document.setModel(super.getText(0,super.getLength()));
        } catch (DOMException dome) {
            Toolkit.getDefaultToolkit().beep();
        } catch (IOException ioe) {
            //This should never happen. If it does however jsXe will
            //act abnormally so crash.
            jsXe.exiterror(view, ioe.toString(), 1);
        }

    }//}}}

    public void remove(int offs, int len) throws BadLocationException {//{{{
        
        try {
            super.remove(offs, len);
            document.setModel(super.getText(0,super.getLength()));
        } catch (DOMException dome) {
            Toolkit.getDefaultToolkit().beep();
        } catch (IOException ioe) {
            //This should never happen. If it does however jsXe will
            //act abnormally so crash.
            jsXe.exiterror(view, ioe.toString(), 1);
        }
        
    }//}}}

    //{{{ Private members
    TabbedView view;
    XMLDocument document;
    //}}}

}
