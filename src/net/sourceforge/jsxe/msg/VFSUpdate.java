/*
VFSUpdate.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2000 Slava Pestov
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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/

package net.sourceforge.jsxe.msg;

import net.sourceforge.jsxe.EBMessage;

/**
 * Message sent when a file or directory changes.
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id: VFSUpdate.java,v 1.3 2002/05/14 07:34:55 spestov Exp $
 * @since jsXe XX.XX
 */
public class VFSUpdate extends EBMessage {
    
    //{{{ VFSUpdate constructor
	/**
	 * Creates a VFS update message.
	 * @param path The path in question
	 */
	public VFSUpdate(String path) {
		super(null);
        
		if (path == null) {
			throw new NullPointerException("Path must be non-null");
        }
        
		this.path = path;
    }//}}}
    
    //{{{ getPath()
	/**
	 * Returns the path that changed.
	 */
	public String getPath() {
		return path;
    }//}}}
    
    //{{{ paramString()
	public String paramString() {
		return "path=" + path + "," + super.paramString();
    }//}}}

	//{{{ Private members
	private String path;
    //}}}
}
