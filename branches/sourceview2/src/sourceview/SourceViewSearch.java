/*
SourceViewSearch.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2002 Ian Lewis (IanLewis@member.fsf.org)

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

package sourceview;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.gui.GUIUtilities;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.util.Log;
//}}}

//{{{ jEdit syntax classes
import org.syntax.jedit.JEditTextArea;
//}}}

//{{{ Swing classes
import javax.swing.JOptionPane;
import javax.swing.text.Segment;
//}}}

//}}}

/**
 * The SourceViewSearch class abstracts functions used by the source
 * view's searching mechanisms.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public class SourceViewSearch {
    
    private SourceView m_view;
    
    public static final String IGNORE_CASE = SourceViewPlugin.PLUGIN_NAME+".ignore.case";
    public static final String LAST_FIND_STRING = SourceViewPlugin.PLUGIN_NAME+".last.find.string";
    
    //{{{ SourceViewSearch constructor
    /**
     * Creates a new search on a specific SourceView
     * @param view the SourceView to search
     */
    public SourceViewSearch(SourceView view) {
        m_view = view;
    }//}}}
    
    //{{{ find()
    /**
     * Initiates a simple find on the text area where the document
     * is searched from the current caret position without replacing
     * any text.
     * @param search the search string to search for
     * @param ignoreCase true if you want to ignore the case of the characters
     *                   in the string when matching.
     */
    public void find(String search, boolean ignoreCase) {
        find(false, m_view.getTextArea().getCaretPosition(), search, "", ignoreCase);
    }//}}}
    
    //{{{ replaceAndFind()
    /**
     * Initiates a replace and find action on the text area where the current
     * matched text is replaced by the replacement and then the document is 
     * is searched from the current caret position for the next instance of
     * the search string.
     *
     * @param search the search string to search for
     * @param replace the replacement string
     * @param ignoreCase true if you want to ignore the case of the characters
     *                   in the string when matching.
     */
    public void replaceAndFind(String search, String replace, boolean ignoreCase) {
        find(true, m_view.getTextArea().getCaretPosition(), search, replace, ignoreCase);
    }//}}}
    
    //{{{ findNext()
    /**
     * Initiates a find using the last string that was entered in the search
     * dialog and value of the ignore case property. Text is not replaced.
     */
    public void findNext() {
        DocumentBuffer buffer = m_view.getDocumentBuffer();
        String search = buffer.getProperty(LAST_FIND_STRING);
        if (search != null) {
            boolean ignoreCase = jsXe.getBooleanProperty(IGNORE_CASE, false);
            find(false,  m_view.getTextArea().getCaretPosition(), search, "", ignoreCase);
        }
    }//}}}
    
    //{{{ Private members
    
    //{{{ find()
    
    private void find(boolean doReplace, int startIndex, String search, String replace, boolean ignoreCase) {
        try {
            
            RESearchMatcher matcher = new RESearchMatcher(search, replace, ignoreCase);
            
            JEditTextArea textArea = m_view.getTextArea();
            
            //replace previous text
            if (doReplace) {
                String selText = textArea.getSelectedText();
                if (selText != null && !selText.equals("")) { 
                    String replaceString = matcher.substitute(selText);
                    textArea.setSelectedText(replaceString);
                }
            }
            
            DocumentBuffer buffer = m_view.getDocumentBuffer();
            Segment seg = buffer.getSegment(0, buffer.getLength());
            int caretPosition = startIndex;
            CharIndexedSegment charSeg = new CharIndexedSegment(seg, caretPosition);
            
            int[] match = matcher.nextMatch(charSeg, false, true, true, false);
            
            buffer.setProperty(LAST_FIND_STRING, search);
            jsXe.setBooleanProperty(IGNORE_CASE, ignoreCase);
            
            if (match != null) {
                Log.log(Log.DEBUG, this, match[0] + " "+ match[1]);
                int start = match[0]+caretPosition;
                int end = match[1]+caretPosition;
               // textArea.requestFocus();
                textArea.select(start, end);
            } else {
                int again = GUIUtilities.confirm(m_view, "SourceView.No.More.Matches", null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (again == 0) {
                    find(doReplace, 0, search, replace, ignoreCase);
                }
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(m_view, ex, Messages.getMessage("SourceView.Search.Error.title"), JOptionPane.WARNING_MESSAGE);
        }
    }//}}}
    
    //}}}
}