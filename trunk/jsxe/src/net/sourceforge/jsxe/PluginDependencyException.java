/*
PluginDependencyException.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2005 Ian Lewis (IanLewis@member.fsf.org)

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

package net.sourceforge.jsxe;

import net.sourceforge.jsxe.gui.Messages;

/**
 * Signals that a dependency required by a plugin has not been met.
 * @author <a href="mailto:IanLewis at member dot fsf dot org">Ian Lewis</a>
 * @version $Id$
 * @see JARClassLoader
 */
public class PluginDependencyException extends RuntimeException {

    //{{{ PluginDependencyException constructor
    /**
     * Creates a new PluginDependencyException for a plugin that was found
     * but an inadequate version was found.
     *
     * @param pluginName the name of the plugin
     * @param requiredName the name of the required component or plugin
     * @param versionRequired the required version of the required component or plugin
     * @param versionFound the version that was found.
     */
    public PluginDependencyException(String pluginName, String requiredName, String versionRequired, String versionFound) {
        super(Messages.getMessage("Plugin.Dependency.Message", new Object[] { pluginName, requiredName, versionRequired, versionFound }));
        m_pluginName = pluginName;
        m_requiredName = requiredName;
        m_versionRequired = versionRequired;
        m_versionFound = versionFound;
    }//}}}    
    
    //{{{ PluginDependencyException constructor
    /**
     * Creates a new PluginDependencyException for a component or plugin that
     * was not found.
     *
     * @param pluginName the name of the plugin
     * @param requiredName the name of the required component or plugin
     * @param versionRequired the required version of the required component or plugin
     */
    public PluginDependencyException(String pluginName, String requiredName, String versionRequired) {
        super(Messages.getMessage("Plugin.Dependency.Not.Found", new Object[] { pluginName, requiredName, versionRequired }));
        m_pluginName = pluginName;
        m_requiredName = requiredName;
        m_versionRequired = versionRequired;
        m_versionFound = null;
    }//}}}
    
    //{{{ PluginDependencyException constructor
    /**
     * Creates a new PluginDependencyException for a component or plugin that
     * was not found.
     *
     * @param pluginName the name of the plugin
     * @param requiredName the name of the required component or plugin
     * @param versionRequired the required version of the required component or plugin
     */
    public PluginDependencyException(String pluginName, String requiredName) {
        super(Messages.getMessage("Plugin.Dependency.Not.Found2", new Object[] { pluginName, requiredName }));
        m_pluginName = pluginName;
        m_requiredName = requiredName;
        m_versionRequired = null;
        m_versionFound = null;
    }//}}}
    
    //{{{ PluginDependencyException constructor
    /**
     * Creates a new PluginDependencyException.
     * @param pluginName the name of the plugin that requires.
     * @param messsage the message
     */
    public PluginDependencyException(String pluginName, String message) {
        super(message);
        m_pluginName = pluginName;
    }//}}}

    //{{{ Private Members
    private String m_pluginName;
    private String m_requiredName;
    private String m_versionRequired;
    private String m_versionFound;
    //}}}
}
