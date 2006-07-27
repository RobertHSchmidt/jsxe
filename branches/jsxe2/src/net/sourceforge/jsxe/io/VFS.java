/*
VFS.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2000, 2003 Slava Pestov
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

package net.sourceforge.jsxe.io.io;

//{{{ Imports
import gnu.regexp.*;
import java.awt.Color;
import java.awt.Component;
import java.io.*;
import java.util.*;
import net.sourceforge.jsxe.msg.PropertiesChanged;
import net.sourceforge.jsxe.util.Log;
//}}}

/**
 * A virtual filesystem implementation.<p>
 *
 * Plugins can provide virtual file systems by defining entries in their
 * <code>services.xml</code> files like so:
 *
 * <pre>&lt;SERVICE CLASS="org.gjt.sp.jedit.io.VFS" NAME="<i>name</i>"&gt;
 *    new <i>MyVFS</i>();
 *&lt;/SERVICE&gt;</pre>
 *
 * URLs of the form <code><i>name</i>:<i>path</i></code> will then be handled
 * by the VFS named <code><i>name</i></code>.<p>
 *
 * See {@link org.gjt.sp.jedit.ServiceManager} for details.<p>
 *
 * <h3>Session objects:</h3>
 *
 * A session is used to persist things like login information, any network
 * sockets, etc. File system implementations that do not need this kind of
 * persistence return a dummy object as a session.<p>
 *
 * Methods whose names are prefixed with "_" expect to be given a
 * previously-obtained session object. A session must be obtained from the AWT
 * thread in one of two ways:
 *
 * <ul>
 * <li>{@link #createVFSSession(String,Component)}</li>
 * <li>{@link #showBrowseDialog(Object[],Component)}</li>
 * </ul>
 *
 * When done, the session must be disposed of using
 * {@link #_endVFSSession(Object,Component)}.<p>
 *
 * <h3>Thread safety:</h3>
 *
 * The following methods cannot be called from an I/O thread:
 *
 * <ul>
 * <li>{@link #createVFSSession(String,Component)}</li>
 * <li>{@link #insert(View,Buffer,String)}</li>
 * <li>{@link #load(View,Buffer,String)}</li>
 * <li>{@link #save(View,Buffer,String)}</li>
 * <li>{@link #showBrowseDialog(Object[],Component)}</li>
 * </ul>
 *
 * All remaining methods are required to be thread-safe in subclasses.
 *
 * <h3>Implementing a VFS</h3>
 *
 * You can override as many or as few methods as you want. Make sure
 * {@link #getCapabilities()} returns a value reflecting the functionality
 * implemented by your VFS.
 *
 * @see VFSManager#getVFSForPath(String)
 * @see VFSManager#getVFSForProtocol(String)
 *
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @since jsXe 0.5 pre3
 * @version $Id$
 */
public abstract class VFS {
    
    //{{{ Capabilities

    /**
     * Read capability.
     */
    public static final int READ_CAP = 1 << 0;

    /**
     * Write capability
     */
    public static final int WRITE_CAP = 1 << 1;

    /**
     * Delete file capability.
     */
    public static final int DELETE_CAP = 1 << 3;

    /**
     * Rename file capability.
     */
    public static final int RENAME_CAP = 1 << 4;

    /**
     * Make directory capability.
     */
    public static final int MKDIR_CAP = 1 << 5;

    /**
     * Low latency capability. If this is not set, then a confirm dialog
     * will be shown before doing a directory search in this VFS.
     */
    public static final int LOW_LATENCY_CAP = 1 << 6;

    /**
     * Case insensitive file system capability.
     */
    public static final int CASE_INSENSITIVE_CAP = 1 << 7;

    //}}}

    //{{{ Extended attributes
    /**
     * File type.
     */
    public static final String EA_TYPE = "type";

    /**
     * File status (read only, read write, etc).
     */
    public static final String EA_STATUS = "status";

    /**
     * File size.
     */
    public static final String EA_SIZE = "size";

    /**
     * File last modified date.
     */
    public static final String EA_MODIFIED = "modified";
    //}}}

    //{{{ VFS constructor
    /**
     * Creates a new virtual filesystem.
     * @param name The name
     * @param caps The capabilities
     */
    public VFS(String name, int caps) {
        this.name = name;
        this.caps = caps;
        // reasonable defaults (?)
        this.extAttrs = new String[] { EA_SIZE, EA_TYPE };
    } //}}}

    //{{{ VFS constructor
    /**
     * Creates a new virtual filesystem.
     * @param name The name
     * @param caps The capabilities
     * @param extAttrs The extended attributes
     */
    public VFS(String name, int caps, String[] extAttrs) {
        this.name = name;
        this.caps = caps;
        this.extAttrs = extAttrs;
    } //}}}

    //{{{ getName() method
    /**
     * Returns this VFS's name. The name is used to obtain the
     * label stored in the <code>vfs.<i>name</i>.label</code>
     * property.
     */
    public String getName() {
        return name;
    } //}}}

    //{{{ getCapabilities() method
    /**
     * Returns the capabilities of this VFS.
     */
    public int getCapabilities() {
        return caps;
    } //}}}

    //{{{ getExtendedAttributes() method
    /**
     * Returns the extended attributes supported by this VFS.
     */
    public String[] getExtendedAttributes() {
        return extAttrs;
    } //}}}

    //{{{ showBrowseDialog() method
    /**
     * Displays a dialog box that should set up a session and return
     * the initial URL to browse.
     * @param session Where the VFS session will be stored
     * @param comp The component that will parent error dialog boxes
     * @return The URL
     */
    public String showBrowseDialog(Object[] session, Component comp) {
        return null;
    } //}}}

    //{{{ getFileName() method
    /**
     * Returns the file name component of the specified path.
     * @param path The path
     */
    public String getFileName(String path) {
        if (path.equals("/")) {
            return path;
        }

        if (path.endsWith("/") || path.endsWith(File.separator)) {
            path = path.substring(0,path.length() - 1);
        }

        int index = Math.max(path.lastIndexOf('/'), path.lastIndexOf(File.separatorChar));
        if(index == -1) {
            index = path.indexOf(':');
        }

        // don't want getFileName("roots:") to return ""
        if (index == -1 || index == path.length() - 1) {
            return path;
        }

        return path.substring(index + 1);
    } //}}}

    //{{{ getParentOfPath() method
    /**
     * Returns the parent of the specified path. This must be
     * overridden to return a non-null value for browsing of this
     * filesystem to work.
     * @param path The path
     */
    public String getParentOfPath(String path) {
        // ignore last character of path to properly handle
        // paths like /foo/bar/
        int count = Math.max(0,path.length() - 2);
        int index = path.lastIndexOf(File.separatorChar,count);
        if (index == -1)
            index = path.lastIndexOf('/',count);
        if (index == -1) {
            // this ensures that getFileParent("protocol:"), for
            // example, is "protocol:" and not "".
            index = path.lastIndexOf(':');
        }

        return path.substring(0,index + 1);
    } //}}}

    //{{{ constructPath() method
    /**
     * Constructs a path from the specified directory and
     * file name component. This must be overridden to return a
     * non-null value, otherwise browsing this filesystem will
     * not work.<p>
     *
     * Unless you are writing a VFS, this method should not be called
     * directly. To ensure correct behavior, you <b>must</b> call
     * {@link org.gjt.sp.jedit.MiscUtilities#constructPath(String,String)}
     * instead.
     *
     * @param parent The parent directory
     * @param path The path
     */
    public String constructPath(String parent, String path) {
        return parent + path;
    } //}}}

    //{{{ getFileSeparator() method
    /**
     * Returns the file separator used by this VFS.
     */
    public char getFileSeparator() {
        return '/';
    } //}}}

    //{{{ getTwoStageSaveName() method
    /**
     * Returns a temporary file name based on the given path.
     *
     * By default jEdit first saves a file to <code>#<i>name</i>#save#</code>
     * and then renames it to the original file. However some virtual file
     * systems might not support the <code>#</code> character in filenames,
     * so this method permits the VFS to override this behavior.
     *
     * @param path The path name
     */
    public String getTwoStageSaveName(String path) {
        return MiscUtilities.constructPath(getParentOfPath(path),
            '#' + getFileName(path) + "#save#");
    } //}}}

    //{{{ reloadDirectory() method
    /**
     * Called before a directory is reloaded by the file system browser.
     * Can be used to flush a cache, etc.
     */
    public void reloadDirectory(String path) {} //}}}

    //{{{ createVFSSession() method
    /**
     * Creates a VFS session. This method is called from the AWT thread,
     * so it should not do any I/O. It could, however, prompt for
     * a login name and password, for example.
     * @param path The path in question
     * @param comp The component that will parent any dialog boxes shown
     * @return The session
     */
    public Object createVFSSession(String path, Component comp) {
        return new Object();
    } //}}}

    //{{{ load() method
    /**
     * Loads the specified buffer. The default implementation posts
     * an I/O request to the I/O thread.
     * @param view The view
     * @param buffer The buffer
     * @param path The path
     */
    public boolean load(View view, Buffer buffer, String path)
    {
        if((getCapabilities() & READ_CAP) == 0)
        {
            VFSManager.error(view,path,"vfs.not-supported.load",new String[] { name });
            return false;
        }

        Object session = createVFSSession(path,view);
        if(session == null)
            return false;

        if((getCapabilities() & WRITE_CAP) == 0)
            buffer.setReadOnly(true);

        BufferIORequest request = new BufferIORequest(
            BufferIORequest.LOAD,view,buffer,session,this,path);
        if(buffer.isTemporary())
            // this makes HyperSearch much faster
            request.run();
        else
            VFSManager.runInWorkThread(request);

        return true;
    } //}}}

    //{{{ save() method
    /**
     * Saves the specifies buffer. The default implementation posts
     * an I/O request to the I/O thread.
     * @param view The view
     * @param buffer The buffer
     * @param path The path
     */
    public boolean save(View view, Buffer buffer, String path)
    {
        if((getCapabilities() & WRITE_CAP) == 0)
        {
            VFSManager.error(view,path,"vfs.not-supported.save",new String[] { name });
            return false;
        }

        Object session = createVFSSession(path,view);
        if(session == null)
            return false;

        /* When doing a 'save as', the path to save to (path)
         * will not be the same as the buffer's previous path
         * (buffer.getPath()). In that case, we want to create
         * a backup of the new path, even if the old path was
         * backed up as well (BACKED_UP property set) */
        if(!path.equals(buffer.getPath()))
            buffer.unsetProperty(Buffer.BACKED_UP);

        VFSManager.runInWorkThread(new BufferIORequest(
            BufferIORequest.SAVE,view,buffer,session,this,path));
        return true;
    } //}}}

    //{{{ insert() method
    /**
     * Inserts a file into the specified buffer. The default implementation
     * posts an I/O request to the I/O thread.
     * @param view The view
     * @param buffer The buffer
     * @param path The path
     */
    public boolean insert(View view, Buffer buffer, String path)
    {
        if((getCapabilities() & READ_CAP) == 0)
        {
            VFSManager.error(view,path,"vfs.not-supported.load",new String[] { name });
            return false;
        }

        Object session = createVFSSession(path,view);
        if(session == null)
            return false;

        VFSManager.runInWorkThread(new BufferIORequest(
            BufferIORequest.INSERT,view,buffer,session,this,path));
        return true;
    } //}}}

    // A method name that starts with _ requires a session object

    //{{{ _canonPath() method
    /**
     * Returns the canonical form of the specified path name. For example,
     * <code>~</code> might be expanded to the user's home directory.
     * @param session The session
     * @param path The path
     * @param comp The component that will parent error dialog boxes
     * @exception IOException if an I/O error occurred
     */
    public String _canonPath(Object session, String path, Component comp) throws IOException {
        return path;
    } //}}}

    //{{{ _listDirectory() method
    /**
     * A convinience method that matches file names against globs, and can
     * optionally list the directory recursively.
     * @param session The session
     * @param directory The directory. Note that this must be a full
     * URL, including the host name, path name, and so on. The
     * username and password (if needed by the VFS) is obtained from the
     * session instance.
     * @param glob Only file names matching this glob will be returned
     * @param recursive If true, subdirectories will also be listed.
     * @param comp The component that will parent error dialog boxes
     * @exception IOException if an I/O error occurred
     */
    public String[] _listDirectory(Object session, String directory,
        String glob, boolean recursive, Component comp)
        throws IOException
    {
        Log.log(Log.DEBUG,this,"Listing " + directory);
        ArrayList files = new ArrayList(100);

        RE filter;
        try {
            filter = new RE(MiscUtilities.globToRE(glob), RE.REG_ICASE);
        } catch(REException e) {
            Log.log(Log.ERROR,this,e);
            return null;
        }

        _listDirectory(session,new ArrayList(),files,directory,filter,
            recursive,comp);

        String[] retVal = (String[])files.toArray(new String[files.size()]);

        Arrays.sort(retVal,new MiscUtilities.StringICaseCompare());

        return retVal;
    } //}}}

    //{{{ _listDirectory() method
    /**
     * Lists the specified directory. 
     * @param session The session
     * @param directory The directory. Note that this must be a full
     * URL, including the host name, path name, and so on. The
     * username and password (if needed by the VFS) is obtained from the
     * session instance.
     * @param comp The component that will parent error dialog boxes
     * @exception IOException if an I/O error occurred
     */
    public DirectoryEntry[] _listDirectory(Object session, String directory,
        Component comp)
        throws IOException
    {
        VFSManager.error(comp,directory,"vfs.not-supported.list",new String[] { name });
        return null;
    } //}}}

    //{{{ _getDirectoryEntry() method
    /**
     * Returns the specified directory entry.
     * @param session The session
     * @param path The path
     * @param comp The component that will parent error dialog boxes
     * @exception IOException if an I/O error occurred
     * @return The specified directory entry, or null if it doesn't exist.
     */
    public DirectoryEntry _getDirectoryEntry(Object session, String path,
        Component comp)
        throws IOException
    {
        return null;
    } //}}}

    //{{{ DirectoryEntry class
    /**
     * A directory entry.
     */
    public static class DirectoryEntry implements Serializable {
        
        //{{{ File types
        public static final int FILE = 0;
        public static final int DIRECTORY = 1;
        public static final int FILESYSTEM = 2;
        //}}}

        //{{{ Instance variables
        public String name;
        public String path;
        
        public String symlinkPath;

        public String deletePath;
        public int type;
        public long length;
        public boolean hidden;
        public boolean canRead;
        public boolean canWrite;
        //}}}

        //{{{ DirectoryEntry constructor
        public DirectoryEntry() {} //}}}

        //{{{ DirectoryEntry constructor
        public DirectoryEntry(String name, String path, String deletePath,
            int type, long length, boolean hidden)
        {
            this.name = name;
            this.path = path;
            this.deletePath = deletePath;
            this.symlinkPath = path;
            this.type = type;
            this.length = length;
            this.hidden = hidden;
            if (path != null) {
                // maintain backwards compatibility
                VFS vfs = VFSManager.getVFSForPath(path);
                canRead = ((vfs.getCapabilities() & READ_CAP) != 0);
                canWrite = ((vfs.getCapabilities() & WRITE_CAP) != 0);
            }
        } //}}}

        protected boolean colorCalculated;
        protected Color color;

        //{{{ getExtendedAttribute() method
        /**
         * Returns the value of an extended attribute. Note that this
         * returns formatted strings (eg, "10 Mb" for a file size of
         * 1048576 bytes). If you need access to the raw data, access
         * fields and methods of this class.
         * @param name The extended attribute name
         */
        public String getExtendedAttribute(String name) {
            
            if (name.equals(EA_TYPE)) {
                
                switch(type) {
                    case FILE:
                        return jEdit.getProperty("vfs.browser.type.file");
                    case DIRECTORY:
                        return jEdit.getProperty("vfs.browser.type.directory");
                    case FILESYSTEM:
                        return jEdit.getProperty("vfs.browser.type.filesystem");
                    default:
                        throw new IllegalArgumentException();
                }
            } else {
                if(name.equals(EA_STATUS)) {
                    if (canRead) {
                        if (canWrite) {
                            return jEdit.getProperty("vfs.browser.status.rw");
                        } else {
                            return jEdit.getProperty("vfs.browser.status.ro");
                        }
                    } else {
                        if (canWrite) {
                            return jEdit.getProperty("vfs.browser.status.append");
                        } else {
                            return jEdit.getProperty("vfs.browser.status.no");
                        }
                    }
                } else {
                    if(name.equals(EA_SIZE)) {
                        if (type != FILE) {
                            return null;
                        } else {
                            return MiscUtilities.formatFileSize(length);
                        }
                    } else {
                        return null;
                    }
                }
            }
        } //}}}

        //{{{ getColor() method
        public Color getColor() {
            if (!colorCalculated) {
                colorCalculated = true;
                color = getDefaultColorFor(name);
            }

            return color;
        } //}}}

        //{{{ toString() method
        public String toString() {
            return name;
        } //}}}
    } //}}}

    //{{{ _delete() method
    /**
     * Deletes the specified URL.
     * @param session The VFS session
     * @param path The path
     * @param comp The component that will parent error dialog boxes
     * @exception IOException if an I/O error occurs
     */
    public boolean _delete(Object session, String path, Component comp)
        throws IOException
    {
        return false;
    } //}}}

    //{{{ _rename() method
    /**
     * Renames the specified URL. Some filesystems might support moving
     * URLs between directories, however others may not. Do not rely on
     * this behavior.
     * @param session The VFS session
     * @param from The old path
     * @param to The new path
     * @param comp The component that will parent error dialog boxes
     * @exception IOException if an I/O error occurs
     */
    public boolean _rename(Object session, String from, String to,
        Component comp) throws IOException
    {
        return false;
    } //}}}

    //{{{ _mkdir() method
    /**
     * Creates a new directory with the specified URL.
     * @param session The VFS session
     * @param directory The directory
     * @param comp The component that will parent error dialog boxes
     * @exception IOException if an I/O error occurs
     */
    public boolean _mkdir(Object session, String directory, Component comp)
        throws IOException
    {
        return false;
    } //}}}

    //{{{ _backup() method
    /**
     * Backs up the specified file. This should only be overriden by
     * the local filesystem VFS.
     * @param session The VFS session
     * @param path The path
     * @param comp The component that will parent error dialog boxes
     * @exception IOException if an I/O error occurs
     */
    public void _backup(Object session, String path, Component comp)
        throws IOException
    {
    } //}}}

    //{{{ _createInputStream() method
    /**
     * Creates an input stream. This method is called from the I/O
     * thread.
     * @param session the VFS session
     * @param path The path
     * @param ignoreErrors If true, file not found errors should be
     * ignored
     * @param comp The component that will parent error dialog boxes
     * @exception IOException If an I/O error occurs
     */
    public InputStream _createInputStream(Object session,
        String path, boolean ignoreErrors, Component comp)
        throws IOException
    {
        VFSManager.error(comp,path,"vfs.not-supported.load",new String[] { name });
        return null;
    } //}}}

    //{{{ _createOutputStream() method
    /**
     * Creates an output stream. This method is called from the I/O
     * thread.
     * @param session the VFS session
     * @param path The path
     * @param comp The component that will parent error dialog boxes
     * @exception IOException If an I/O error occurs
     */
    public OutputStream _createOutputStream(Object session,
        String path, Component comp)
        throws IOException
    {
        VFSManager.error(comp,path,"vfs.not-supported.save",new String[] { name });
        return null;
    } //}}}

    //{{{ _saveComplete() method
    /**
     * Called after a file has been saved.
     * @param session The VFS session
     * @param buffer The buffer
     * @param path The path the buffer was saved to (can be different from
     * {@link org.gjt.sp.jedit.Buffer#getPath()} if the user invoked the
     * <b>Save a Copy As</b> command, for example).
     * @param comp The component that will parent error dialog boxes
     * @exception IOException If an I/O error occurs
     */
    public void _saveComplete(Object session, Buffer buffer, String path,
        Component comp) throws IOException {} //}}}

    //{{{ _endVFSSession() method
    /**
     * Finishes the specified VFS session. This must be called
     * after all I/O with this VFS is complete, to avoid leaving
     * stale network connections and such.
     * @param session The VFS session
     * @param comp The component that will parent error dialog boxes
     * @exception IOException if an I/O error occurred
     */
    public void _endVFSSession(Object session, Component comp)
        throws IOException
    {
    } //}}}

    //{{{ getDefaultColorFor() method
    /**
     * Returns color of the specified file name, by matching it against
     * user-specified regular expressions.
     */
    public static Color getDefaultColorFor(String name) {
        synchronized(lock) {
            if (colors == null) {
                loadColors();
            }

            for (int i = 0; i < colors.size(); i++) {
                ColorEntry entry = (ColorEntry)colors.elementAt(i);
                if (entry.re.isMatch(name)) {
                    return entry.color;
                }
            }

            return null;
        }
    } //}}}

    //{{{ DirectoryEntryCompare class
    /**
     * Implementation of {@link org.gjt.sp.jedit.MiscUtilities.Compare}
     * interface that compares {@link VFS.DirectoryEntry} instances.
     */
    public static class DirectoryEntryCompare implements MiscUtilities.Compare {
        
        private boolean sortIgnoreCase, sortMixFilesAndDirs;

        /**
         * Creates a new <code>DirectoryEntryCompare</code>.
         * @param sortMixFilesAndDirs If false, directories are
         * put at the top of the listing.
         * @param sortIgnoreCase If false, upper case comes before
         * lower case.
         */
         public DirectoryEntryCompare(boolean sortMixFilesAndDirs, boolean sortIgnoreCase) {
            this.sortMixFilesAndDirs = sortMixFilesAndDirs;
            this.sortIgnoreCase = sortIgnoreCase;
        }

        public int compare(Object obj1, Object obj2) {
            VFS.DirectoryEntry file1 = (VFS.DirectoryEntry)obj1;
            VFS.DirectoryEntry file2 = (VFS.DirectoryEntry)obj2;

            if (!sortMixFilesAndDirs) {
                if(file1.type != file2.type)
                    return file2.type - file1.type;
            }

            return MiscUtilities.compareStrings(file1.name, file2.name,sortIgnoreCase);
        }
    } //}}}

    //{{{ Private members
    private String name;
    private int caps;
    private String[] extAttrs;
    private static Vector colors;
    private static Object lock = new Object();

    //{{{ Class initializer
    static
    {
        EditBus.addToBus(new EBComponent()
        {
            public void handleMessage(EBMessage msg)
            {
                if(msg instanceof PropertiesChanged)
                {
                    synchronized(lock)
                    {
                        colors = null;
                    }
                }
            }
        });
    } //}}}

    //{{{ _listDirectory() method
    private void _listDirectory(Object session, ArrayList stack,
        ArrayList files, String directory, RE glob, boolean recursive,
        Component comp) throws IOException
    {
        if(stack.contains(directory))
        {
            Log.log(Log.ERROR,this,
                "Recursion in _listDirectory(): "
                + directory);
            return;
        }
        else
            stack.add(directory);

        VFS.DirectoryEntry[] _files = _listDirectory(session,directory,
            comp);
        if(_files == null || _files.length == 0)
            return;

        for(int i = 0; i < _files.length; i++)
        {
            VFS.DirectoryEntry file = _files[i];

            if(file.type == VFS.DirectoryEntry.DIRECTORY
                || file.type == VFS.DirectoryEntry.FILESYSTEM)
            {
                if(recursive)
                {
                    // resolve symlinks to avoid loops
                    String canonPath = _canonPath(session,file.path,comp);
                    if(!MiscUtilities.isURL(canonPath))
                        canonPath = MiscUtilities.resolveSymlinks(canonPath);

                    _listDirectory(session,stack,files,
                        canonPath,glob,recursive,
                        comp);
                }
            }
            else
            {
                if(!glob.isMatch(file.name))
                    continue;

                Log.log(Log.DEBUG,this,file.path);

                files.add(file.path);
            }
        }
    } //}}}

    //{{{ loadColors() method
    private static void loadColors()
    {
        synchronized(lock)
        {
            colors = new Vector();

            if(!jEdit.getBooleanProperty("vfs.browser.colorize"))
                return;

            String glob;
            int i = 0;
            while((glob = jEdit.getProperty("vfs.browser.colors." + i + ".glob")) != null)
            {
                try
                {
                    colors.addElement(new ColorEntry(
                        new RE(MiscUtilities.globToRE(glob)),
                        jEdit.getColorProperty(
                        "vfs.browser.colors." + i + ".color",
                        Color.black)));
                }
                catch(REException e)
                {
                    Log.log(Log.ERROR,VFS.class,"Invalid regular expression: "
                        + glob);
                    Log.log(Log.ERROR,VFS.class,e);
                }

                i++;
            }
        }
    } //}}}

    //{{{ ColorEntry class
    static class ColorEntry
    {
        RE re;
        Color color;

        ColorEntry(RE re, Color color)
        {
            this.re = re;
            this.color = color;
        }
    } //}}}

    //}}}
}
