/*
SourceViewPlugin.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This file contions the code source view plugin for jsXe.

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

package sourceview;

//{{{ Imports

//{{{ jsXe classes

import net.sourceforge.jsxe.ViewPlugin;
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.gui.OptionsPanel;

//}}}

//{{{ Java classes
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
//}}}

//}}}

public class SourceViewPlugin extends ViewPlugin {
    
    public static final String PLUGIN_NAME = "source";
    private static final String HUMAN_READABLE_NAME = "Source View";
    private static final String VERSION;
    private static final String DESCRIPTION = "Edit an XML document's source";
    
    static {
        InputStream propsStream = SourceViewPlugin.class.getResourceAsStream("/sourceview/build.properties");
        Properties buildProps = new Properties();
        try {
            buildProps.load(propsStream);
        } catch (IOException ioe) {}
        
        VERSION = buildProps.getProperty("major.version")+"."+buildProps.getProperty("minor.version")+" "+buildProps.getProperty("build.description");
    }
    
    //{{{ SourceViewPlugin
    
    public SourceViewPlugin() {
        super(PLUGIN_NAME, HUMAN_READABLE_NAME, VERSION, DESCRIPTION);
    }//}}}
    
    //{{{ newDocumentView()
    
    public DocumentView newDocumentView(DocumentBuffer document) throws IOException {
        return new SourceView(document, this);
    }//}}}
    
    //{{{ getOptionsPanel()
    
    public OptionsPanel getOptionsPanel(DocumentBuffer buffer) {
        return new SourceViewOptionsPanel(buffer);
    }//}}}
    
}
