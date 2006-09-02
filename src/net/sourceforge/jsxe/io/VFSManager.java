/*
VFSManager.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2000, 2003 Slava Pestov
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

package net.sourceforge.jsxe.io;

//{{{ Imports

//{{{ jsXe classes
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.JARClassLoader;
import net.sourceforge.jsxe.EditBus;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.gui.ErrorListDialog;
import net.sourceforge.jsxe.msg.VFSUpdate;
import net.sourceforge.jsxe.util.*;
//}}}

//{{{ Swing classes
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
//}}}

//{{{ AWT classes
import java.awt.Component;
import java.awt.Frame;
//}}}

//{{{ Java classes
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
//}}}

//}}}

/**
 * jsXe's virtual filesystem allows it to transparently edit files
 * stored elsewhere than the local filesystem, for example on an FTP
 * site. See the {@link VFS} class for implementation details.<p>
 *
 * Note that most of the jsXe API is not thread-safe, so special care
 * must be taken when making jsXe API calls. Also, it is not safe to
 * call <code>SwingUtilities.invokeAndWait()</code> from a work request;
 * it can cause a deadlock if the given runnable then later calls
 * {@link #waitForRequests()}.
 *
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre3
 */
public class VFSManager {

    //{{{ init() method
    /**
     * Do not call.
     */
    public static void init() {
        int count = jsXe.getIntegerProperty("ioThreadCount", 4);
        ioThreadPool = new WorkThreadPool("jsXe I/O", count);
       // JARClassLoader classLoader = new JARClassLoader();
        JARClassLoader classLoader = jsXe.getPluginLoader();
        for (int i = 0; i < ioThreadPool.getThreadCount(); i++) {
            ioThreadPool.getThread(i).setContextClassLoader(classLoader);
        }
        
    } //}}}

    //{{{ start() method
    /**
     * Do not call.
     */
    public static void start() {
        ioThreadPool.start();
    } //}}}

    //{{{ VFS methods

    //{{{ getFileVFS() method
    /**
     * Returns the local filesystem VFS.
     */
    public static VFS getFileVFS() {
        return fileVFS;
    } //}}}

    //{{{ getUrlVFS() method
    /**
     * Returns the URL VFS.
     */
    public static VFS getUrlVFS() {
        return urlVFS;
    } //}}}

    //{{{ getVFSForProtocol() method
    /**
     * Returns the VFS for the specified protocol.
     * @param protocol The protocol
     */
    public static VFS getVFSForProtocol(String protocol) {
        if (protocol.equals("file"))
            return fileVFS;
        else {
           // VFS vfs = (VFS)ServiceManager.getService(SERVICE,protocol);
           // if (vfs == null) {
           //     vfs = (VFS)protocolHash.get(protocol);
           // }
            VFS vfs = (VFS)protocolHash.get(protocol);

            if (vfs != null) {
                return vfs;
            } else {
                return urlVFS;
            }
        }
    } //}}}

    //{{{ getVFSForPath() method
    /**
     * Returns the VFS for the specified path.
     * @param path The path
     */
    public static VFS getVFSForPath(String path)
    {
        if(MiscUtilities.isURL(path))
            return getVFSForProtocol(MiscUtilities.getProtocolOfURL(path));
        else
            return fileVFS;
    } //}}}

    //{{{ registerVFS() method
    public static void registerVFS(String protocol, VFS vfs) {
        Log.log(Log.DEBUG,VFSManager.class,"Registered "
            + vfs.getName() + " filesystem for "
            + protocol + " protocol");
        vfsHash.put(vfs.getName(),vfs);
        protocolHash.put(protocol,vfs);
    } //}}}

    //{{{ getVFSs() method
    /**
     * Returns a list of all registered filesystems.
     */
    public static String[] getVFSs() {
        List returnValue = new LinkedList();
       // String[] newAPI = ServiceManager.getServiceNames(SERVICE);
       // if(newAPI != null)
       // {
       //     for(int i = 0; i < newAPI.length; i++)
       //     {
       //         returnValue.add(newAPI[i]);
       //     }
       // }
        Enumeration oldAPI = vfsHash.keys();
        while(oldAPI.hasMoreElements())
            returnValue.add(oldAPI.nextElement());
        return (String[])returnValue.toArray(new String[returnValue.size()]);
    } //}}}

    //}}}

    //{{{ I/O request methods

    //{{{ getIOThreadPool() method
    /**
     * Returns the I/O thread pool.
     */
    public static WorkThreadPool getIOThreadPool()
    {
        return ioThreadPool;
    } //}}}

    //{{{ waitForRequests() method
    /**
     * Returns when all pending requests are complete.
     */
    public static void waitForRequests()
    {
        ioThreadPool.waitForRequests();
    } //}}}

    //{{{ errorOccurred() method
    /**
     * Returns if the last request caused an error.
     */
    public static boolean errorOccurred()
    {
        return error;
    } //}}}

    //{{{ getRequestCount() method
    /**
     * Returns the number of pending I/O requests.
     */
    public static int getRequestCount()
    {
        return ioThreadPool.getRequestCount();
    } //}}}

    //{{{ runInAWTThread() method
    /**
     * Executes the specified runnable in the AWT thread once all
     * pending I/O requests are complete.
     */
    public static void runInAWTThread(Runnable run)
    {
        ioThreadPool.addWorkRequest(run,true);
    } //}}}

    //{{{ runInWorkThread() method
    /**
     * Executes the specified runnable in one of the I/O threads.
     */
    public static void runInWorkThread(Runnable run)
    {
        ioThreadPool.addWorkRequest(run,false);
    } //}}}

    //}}}

    //{{{ error() method
    /**
     * Reports an I/O error.
     *
     * @param comp The component
     * @param path The path name that caused the error
     * @param messageProp The error message property name
     * @param args Positional parameters
     */
    public static void error(Component comp,
        final String path,
        String messageProp,
        Object[] args)
    {
        final Frame frame = JOptionPane.getFrameForComponent(comp);

        synchronized(errorLock) {
            
            error = true;

            errors.addElement(new ErrorListDialog.ErrorEntry(path, messageProp, args));

            if (errors.size() == 1) {
                

                VFSManager.runInAWTThread(new Runnable() {
                    public void run() {
                        String caption = Messages.getMessage("IO.Error.caption" + (errors.size() == 1 ? "-1" : ""), 
                                                             new Integer[] { new Integer(errors.size()) });
                        
                        new ErrorListDialog(
                            frame.isShowing()
                            ? frame
                            : jsXe.getActiveView(),
                            Messages.getMessage("IO.Error.title"),
                            caption,errors,false);
                        
                        errors.removeAllElements();
                        error = false;
                    }
                });
            }
        }
    } //}}}

    //{{{ sendVFSUpdate() method
    /**
     * Sends a VFS update message.
     * @param vfs The VFS
     * @param path The path that changed
     * @param parent True if an update should be sent for the path's
     * parent too
     */
    public static void sendVFSUpdate(VFS vfs, String path, boolean parent) {
        if (parent) {
            sendVFSUpdate(vfs,vfs.getParentOfPath(path),false);
            sendVFSUpdate(vfs,path,false);
        } else {
            // have to do this hack until VFSPath class is written
            if (path.length() != 1 && (path.endsWith("/") || path.endsWith(java.io.File.separator))) {
                path = path.substring(0,path.length() - 1);
            }

            synchronized(vfsUpdateLock) {
                for (int i = 0; i < vfsUpdates.size(); i++) {
                    VFSUpdate msg = (VFSUpdate)vfsUpdates.get(i);
                    if (msg.getPath().equals(path)) {
                        // don't send two updates
                        // for the same path
                        return;
                    }
                }

                vfsUpdates.add(new VFSUpdate(path));

                if (vfsUpdates.size() == 1) {
                    // we were the first to add an update;
                    // add update sending runnable to AWT
                    // thread
                    VFSManager.runInAWTThread(new SendVFSUpdatesSafely());
                }
            }
        }
    } //}}}

    //{{{ SendVFSUpdatesSafely class
    static class SendVFSUpdatesSafely implements Runnable {
        
        public void run() {
            synchronized(vfsUpdateLock) {
                // the vfs browser has what you might call
                // a design flaw, it doesn't update properly
                // unless the vfs update for a parent arrives
                // before any updates for the children. sorting
                // the list alphanumerically guarantees this.
                Collections.sort(vfsUpdates, new MiscUtilities.StringCompare());
                for (int i = 0; i < vfsUpdates.size(); i++) {
                    EditBus.send((VFSUpdate)vfsUpdates.get(i));
                }

                vfsUpdates.clear();
            }
        }
    } //}}}

    //{{{ Private members

    //{{{ Static variables
    private static WorkThreadPool ioThreadPool;
    private static VFS fileVFS;
    private static VFS urlVFS;
    private static Hashtable vfsHash;
    private static Hashtable protocolHash;
    private static boolean error;
    private static Object errorLock;
    private static Vector errors;
    private static Object vfsUpdateLock;
    private static List vfsUpdates;
    //}}}

    //{{{ Class initializer
    static
    {
        errorLock = new Object();
        errors = new Vector();
        fileVFS = new FileVFS();
        urlVFS = new UrlVFS();
        vfsHash = new Hashtable();
        protocolHash = new Hashtable();
        vfsUpdateLock = new Object();
        vfsUpdates = new ArrayList(10);
    } //}}}

    private VFSManager() {}
    //}}}
}
