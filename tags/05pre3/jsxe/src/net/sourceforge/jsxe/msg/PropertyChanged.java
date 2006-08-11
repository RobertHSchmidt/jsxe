/*
PropertyChanged.java
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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe.msg;

import net.sourceforge.jsxe.*;

/**
 * Message sent when one of jsXe's properties have changed.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 *
 * @since jsXe 0.5 pre1
 */
public class PropertyChanged extends EBMessage {
    
    //{{{ PropertyChanged constructor
    /**
     * Creates a new PropertyChanged message.
     * @param 
     */
    public PropertyChanged(String key, String oldValue) {
        super(null);
        m_key = key;
        m_oldValue = oldValue;
    } //}}}

    //{{{ getKey()
    /**
     * Returns the property key that was changed.
     */
    public String getKey() {
        return m_key;
    } //}}}

    //{{{ getOldValue() method
    /**
     * Returns the old value of the property before it was changed
     */
    public String getOldValue() {
        return m_oldValue;
    } //}}}
    
    //{{{ paramString()
    public String paramString() {
        return "key=" + m_key + "," + "old.value=" + m_oldValue + "," + "new.value=" + jsXe.getProperty(m_key) + "," + super.paramString();
    } //}}}
    
    //{{{ Private members
    private String m_key;
    private String m_oldValue;
    //}}}
}
