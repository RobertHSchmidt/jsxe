/*
GeneralOptionPane.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)

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

package net.sourceforge.jsxe.options;

//{{{ imports
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.CatalogManager;
import net.sourceforge.jsxe.gui.Messages;
import javax.swing.JComboBox;
import java.util.Vector;
//}}}

/**
 * The OptionPane containing jsXe's general options in the Global Options
 * Dialog.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @see net.sourceforge.jsxe.gui.GlobalOptionsDialog
 */
public class GeneralOptionPane extends AbstractOptionPane {
    
    //{{{ GeneralOptionPane constructor
    public GeneralOptionPane() {
        super("jsxeoptions");
    }//}}}
    
    //{{{ getTitle()
    public String getTitle() {
        return Messages.getMessage("Global.Options.Title");
    }//}}}
    
    //{{{ _init()
    protected void _init() {
        
        //{{{ max recent files
        
        int maxRecentFiles = jsXe.getIntegerProperty("max.recent.files", 20);
        
        Vector sizes = new Vector(4);
        sizes.add("10");
        sizes.add("20");
        sizes.add("30");
        sizes.add("40");
        maxRecentFilesComboBox = new JComboBox(sizes);
        maxRecentFilesComboBox.setEditable(true);
        maxRecentFilesComboBox.setSelectedItem(Integer.toString(maxRecentFiles));
        
        addComponent(Messages.getMessage("Global.Options.Max.Recent.Files"),
                     maxRecentFilesComboBox,
                     Messages.getMessage("Global.Options.Max.Recent.Files.ToolTip"));
        
        //}}}
        
        //{{{ network
        
        String[] networkValues = {
            Messages.getMessage("Global.Options.network-always"),
            Messages.getMessage("Global.Options.network-cache"),
            Messages.getMessage("Global.Options.network-off")

        };
        
        network = new JComboBox(networkValues);
        network.setSelectedIndex(jsXe.getIntegerProperty("xml.cache", 1));
        
        addComponent(Messages.getMessage("Global.Options.network"),
                     network);
        
        //}}}
        
    }//}}}
    
    //{{{ _save()
    protected void _save() {
        try {
            //don't need to set dirty, no change to text
            jsXe.setProperty("max.recent.files", (new Integer(maxRecentFilesComboBox.getSelectedItem().toString())).toString());
        } catch (NumberFormatException nfe) {
            //Bad input, don't save.
        }
        jsXe.setIntegerProperty("xml.cache",network.getSelectedIndex());
        CatalogManager.propertiesChanged();
    }//}}}
    
    //{{{ toString()
    public String toString() {
        return getTitle();
    }//}}}
    
    //{{{ Private Members
    private JComboBox maxRecentFilesComboBox;
    private JComboBox network;
    //}}}
    
}
