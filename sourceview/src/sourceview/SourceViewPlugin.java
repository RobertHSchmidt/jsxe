/*
SourceViewPlugin.java
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

//{{{ Imports

import sourceview.action.*;

//{{{ jsXe classes

import net.sourceforge.jsxe.ViewPlugin;
import net.sourceforge.jsxe.DocumentBuffer;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.util.Log;
import net.sourceforge.jsxe.options.OptionPane;

//}}}

//{{{ Java classes
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
//}}}

//}}}

/**
 * A plugin for jsXe for viewing and editing an XML Document as text.
 * Supports syntax highlighting.
 * @author <a href="mailto:IanLewis at member dot fsf dot org">Ian Lewis</a>
 * @version $Id$
 * @see SourceView
 */
public class SourceViewPlugin extends ViewPlugin {
    
    public static final String PLUGIN_NAME = "sourceview";
    
    //{{{ SourceViewPlugin
    
    public SourceViewPlugin() {
    }//}}}
    
    //{{{ newDocumentView()
    
    public DocumentView newDocumentView(DocumentBuffer document) throws IOException {
        return new SourceView(document, this);
    }//}}}
    
    //{{{ getOptionPane()
    
    public OptionPane getOptionPane(DocumentBuffer buffer) {
        return new SourceViewOptionPane(buffer);
    }//}}}
    
    //{{{ getProperties()
    public Properties getProperties() {
        Properties props = new Properties();
        try {
            InputStream stream = SourceViewPlugin.class.getResourceAsStream("/sourceview/sourceview.props");
            props.load(stream);
        } catch (IOException ioe) {
            Log.log(Log.ERROR, this, "Source View: failed to load default properties.");
            Log.log(Log.ERROR, this, ioe.getMessage());
        }
        return props;
    }//}}}
}
