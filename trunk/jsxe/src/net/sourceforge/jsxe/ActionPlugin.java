/*
ActionPlugin.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that creates a tree view of an XML document.
The user can then edit this tree and the content in the tree.

This contains the 

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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe;

//{{{ imports

//{{{ jsXe classes
import net.sourceforge.jsxe.dom.XMLDocument;
import net.sourceforge.jsxe.gui.DocumentView;
import net.sourceforge.jsxe.gui.OptionsPanel;
//}}}

//{{{ Swing classes
import javax.swing.Action;
//}}}

//{{{ Java classes
import java.util.Properties;
//}}}

//}}}

/**
 * This defines the general interface that all plugins for jsXe must implement.
 * There are two types of plugins, ViewPlugins and ActionPlugins. ViewPlugins
 * specify a view that can be used to edit XML documents. ActionPlugins add actions
 * to jsXe allowing it to do extra tasks such as XSLT transformation.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public abstract class ActionPlugin {
    
    //{{{ Private members
    private String m_name;
    private String m_humanReadableName;
    private String m_version;
    private String m_description;
    private ActionSet m_actionSet;
    //}}}
    
    //{{{ ActionPlugin constructor
    /**
     * Constructs a new ActionPlugin with the values provided,
     * a version of "1.0", and an empty description
     * @param name a short unique name for the plugin. Contains alpha numeric characters, underscores, and dashes
     * @param humanReadableName a formatted name to be shown to users.
     */
    public ActionPlugin(String name, String humanReadableName) {
        this(name, humanReadableName, "1.0", "");
    }//}}}
    
    //{{{ ActionPlugin constructor
    /**
     * Constructs a new ActionPlugin with the values provided and an empty description.
     * @param name a short unique name for the plugin. Contains alpha numeric characters, underscores, and dashes
     * @param humanReadableName a formatted name to be shown to users.
     * @param version The formatted version for the plugin
     */
    public ActionPlugin(String name, String humanReadableName, String version) {
        this(name, humanReadableName, version, "");
    }//}}}
    
    //{{{ ActionPlugin constructor
    /**
     * Constructs an ActionPlugin with the supplied values.
     * @param name a short unique name for the plugin. Contains alpha numeric characters, underscores, and dashes
     * @param humanReadableName a formatted name to be shown to users.
     * @param version The formatted version for the plugin
     * @param description a description of the plugin.
     */
    public ActionPlugin(String name, String humanReadableName, String version, String description) {
        m_name = name;
        m_humanReadableName = humanReadableName;
        m_version = version;
        m_description = description;
        m_actionSet = new ActionSet(m_name);
    }//}}}
    
    //{{{ getName();
    /**
     * Gets the name of the plugin. This should be a unique name. It shouldn't have
     * spaces, only alpha numeric characters, underscores, and dashes. It is used for
     * properties and other fuctions within jsXe.
     * @return a unique name for the plugin
     */
    public String getName() {
        return m_name;
    }//}}}
    
    //{{{ getHumanReadableName()
    /**
     * The name that is shown to users by jsXe. This should be short but descriptive and
     * formatted.
     */
    public String getHumanReadableName() {
        return m_humanReadableName;
    }//}}}
    
    //{{{ getVersion()
    /**
     * Gets the version of this view. Ex. 1.4.2
     * @return a string containing the formatted version
     */
    public String getVersion() {
        return m_version;
    }//}}}
    
    //{{{ getDescription()
    /**
     * A description of the plugin. This should be a few short sentences. 
     * This is not used currently, but will be used in future.
     * @return the description of the plugin
     */
    public String getDescription() {
        return m_description;
    }//}}}
    
    //{{{ getOptionsPanel()
    /**
     * Gets the options panel for setting general plugin options. The default
     * implementation returns null.
     * @return an OptionsPanel for editing this plugin's options
     */
    public OptionsPanel getOptionsPanel(DocumentBuffer buffer) {
        return null;
    }//}}}
    
    //{{{ getProperties()
    /**
     * The properties to add to jsXe when the plugin is loaded. The default
     * iplementation returns an empty properties set.
     * @return the plugin's properties
     */
    public Properties getProperties() {
        return new Properties();
    }//}}}
    
    //{{{ addAction()
    /**
     * Allows subclasses to add actions to the ActionSet for the plugin. Normally
     * this would be done during initialization of the plugin.
     * @param name the name of the action
     * @param action the action itself
     */
    protected void addAction(String name, Action action) {
        m_actionSet.addAction(name, action);
    }//}}}
    
    //{{{ getActionSet()
    /**
     * Gets the ActionSet for this plugin. These actions can be executed
     * through menus and such.
     * @return the ActionSet containing actions added by this plugin.
     */
    public ActionSet getActionSet() {
        return m_actionSet;
    }//}}}
    
}
