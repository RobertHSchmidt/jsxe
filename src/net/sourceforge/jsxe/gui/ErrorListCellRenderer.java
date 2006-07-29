/*
ErrorListCellRenderer.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2001 Slava Pestov
Portions Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)

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

//{{{ Imports
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
//}}}

/**
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre3
 */
public class ErrorListCellRenderer extends JComponent implements ListCellRenderer {
	
	//{{{ ErrorListCellRenderer constructor
	ErrorListCellRenderer() {
		// GTK look and feel issues
		plainFont = new JLabel().getFont();
		//UIManager.getFont("Label.font");
		boldFont = new Font(plainFont.getName(),Font.BOLD,plainFont.getSize());
		plainFM = getFontMetrics(plainFont);
		boldFM = getFontMetrics(boldFont);

		setBorder(new EmptyBorder(2,2,2,2));
	} //}}}

	//{{{ getListCellRendererComponent() method
	public Component getListCellRendererComponent(JList list, Object value,
		int index, boolean isSelected, boolean cellHasFocus)
	{
		if (value instanceof ErrorListDialog.ErrorEntry) {
            ErrorListDialog.ErrorEntry entry = (ErrorListDialog.ErrorEntry)value;
            this.path = entry.path + ":";
            this.messages = entry.messages;
        } else {
            this.path = null;
            this.messages = new String[] { value.toString() };
        }
		
        
        return this;
	} //}}}

	//{{{ getPreferredSize() method
	public Dimension getPreferredSize() {
		
		int width = 0;
        if (path != null) {
            width = boldFM.stringWidth(path);
        }
		int height = boldFM.getHeight();
		for (int i = 0; i < messages.length; i++) {
			width = Math.max(plainFM.stringWidth(messages[i]),width);
			height += plainFM.getHeight();
		}

		Insets insets = getBorder().getBorderInsets(this);
		width += insets.left + insets.right;
		height += insets.top + insets.bottom;

		return new Dimension(width,height);
	} //}}}

	//{{{ paintComponent() method
	public void paintComponent(Graphics g) {
		
		Insets insets = getBorder().getBorderInsets(this);
		int y = insets.top;
        if (path != null) {
            g.setFont(boldFont);
            g.drawString(path,insets.left,insets.top + boldFM.getAscent());
            y = insets.top + boldFM.getHeight() + 2;
        }
		
		g.setFont(plainFont);
		for (int i = 0; i < messages.length; i++) {
			g.drawString(messages[i],insets.left,y + plainFM.getAscent());
			y += plainFM.getHeight();
		}
	} //}}}

	//{{{ Instance variables
	private String path;
	private String[] messages;
	private Font plainFont;
	private Font boldFont;
	private FontMetrics plainFM;
	private FontMetrics boldFM;
	//}}}
}
