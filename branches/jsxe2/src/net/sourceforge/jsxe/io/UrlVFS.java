/*
URLVFS.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2000 Slava Pestov
Portions Copyright (C) 2004 Ian Lewis (IanLewis@member.fsf.org)

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

package net.sourceforge.jsxe.io;

//{{{ Imports
import java.awt.Component;
import java.io.*;
import java.net.*;
import net.sourceforge.jsxe.util.Log;
//}}}

/**
 * URL VFS.
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @since jsXe 0.5 pre3
 * @version $Id: UrlVFS.java,v 1.6 2003/01/12 03:08:24 spestov Exp $
 */
public class UrlVFS extends VFS {
    
    //{{{ UrlVFS constructor
    public UrlVFS() {
        super("url",READ_CAP | WRITE_CAP);
    } //}}}

    //{{{ constructPath() method
    public String constructPath(String parent, String path) {
        if (parent.endsWith("/")) {
            return parent + path;
        } else {
            return parent + '/' + path;
        }
    } //}}}

    //{{{ _createInputStream() method
    public InputStream _createInputStream(Object session,
        String path, boolean ignoreErrors, Component comp)
        throws IOException
    {
        try {
            return new URL(path).openStream();
        } catch(MalformedURLException mu) {
            Log.log(Log.ERROR,this,mu);
            String[] args = { mu.getMessage() };
            VFSManager.error(comp,path,"ioerror.badurl",args);
            return null;
        }
    } //}}}

    //{{{ _createOutputStream() method
    public OutputStream _createOutputStream(Object session, String path,
        Component comp) throws IOException
    {
        try {
            return new URL(path).openConnection().getOutputStream();
        } catch(MalformedURLException mu) {
            Log.log(Log.ERROR,this,mu);
            String[] args = { mu.getMessage() };
            VFSManager.error(comp,path,"ioerror.badurl",args);
            return null;
        }
    } //}}}
}
