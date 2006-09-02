/*
XMLDocumentIORequest.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2000, 2003 Slava Pestov
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

package net.sourceforge.jsxe.dom2.ls;

//{{{ Imports
import javax.swing.text.Segment;
import java.io.*;
import java.util.zip.*;
import java.util.Vector;
import net.sourceforge.jsxe.io.*;
import net.sourceforge.jsxe.*;
import net.sourceforge.jsxe.gui.TabbedView;
import net.sourceforge.jsxe.gui.Messages;
import net.sourceforge.jsxe.dom2.XMLDocument;
import net.sourceforge.jsxe.util.*;
//}}}

/**
 * A document I/O request.
 * @author Slava Pestov
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre3
 */
public class XMLDocumentIORequest extends WorkRequest {
    
    //{{{ Constants
    /**
     * Size of I/O buffers.
     */
    public static final int IOBUFSIZE = 32768;

    /**
     * Number of lines per progress increment.
     */
    public static final int PROGRESS_INTERVAL = 300;

    public static final String LOAD_DATA = "XMLDocumentIORequest__loadData";
    public static final String END_OFFSETS = "XMLDocumentIORequest__endOffsets";
    public static final String NEW_PATH = "XMLDocumentIORequest__newPath";

    /**
     * Boolean property set when an error occurs.
     */
    public static final String ERROR_OCCURRED = "XMLDocumentIORequest__error";

    /**
     * A file load request.
     */
    public static final int LOAD = 0;

    /**
     * A file save request.
     */
    public static final int SAVE = 1;

    /**
     * An autosave request. Only supported for local files.
     */
    public static final int AUTOSAVE = 2;

    /**
     * An insert file request.
     */
   // public static final int INSERT = 3;

    /**
     * Magic numbers used for auto-detecting Unicode and GZIP files.
     */
    public static final int GZIP_MAGIC_1 = 0x1f;
    public static final int GZIP_MAGIC_2 = 0x8b;
    public static final int UNICODE_MAGIC_1 = 0xfe;
    public static final int UNICODE_MAGIC_2 = 0xff;
    public static final int UTF8_MAGIC_1 = 0xef;
    public static final int UTF8_MAGIC_2 = 0xbb;
    public static final int UTF8_MAGIC_3 = 0xbf;

    /**
     * Length of longest XML PI used for encoding detection.<p>
     * &lt;?xml version="1.0" encoding="................"?&gt;
     */
    public static final int XML_PI_LENGTH = 50;
    //}}}

    //{{{ XMLDocumentIORequest constructor
    /**
     * Creates a new buffer I/O request.
     * @param type The request type
     * @param view The view
     * @param buffer The document
     * @param session The VFS session
     * @param vfs The VFS
     * @param path The path
     */
    public XMLDocumentIORequest(int type, TabbedView view, XMLDocument buffer,
        Object session, VFS vfs, String path)
    {
        this.type = type;
        this.view = view;
        this.buffer = buffer;
        this.session = session;
        this.vfs = vfs;
        this.path = path;
    } //}}}

    //{{{ run() method
    public void run() {
        
        switch(type) {
            case LOAD:
                load();
                break;
            case SAVE:
                save();
                break;
            case AUTOSAVE:
                autosave();
                break;
           // case INSERT:
           //     insert();
           //     break;
            default:
                throw new InternalError();
        }
    } //}}}
    
    //{{{ getLoadData()
    /**
     * Gets the data loaded from a load request.
     */
    public SegmentBuffer getLoadData() {
        return m_loadData;
    }//}}}
    
    //{{{ toString() method
    public String toString() {
        
        String typeString;
        switch(type) {
            case LOAD:
                typeString = "LOAD";
                break;
            case SAVE:
                typeString = "SAVE";
                break;
            case AUTOSAVE:
                typeString = "AUTOSAVE";
                break;
            default:
                typeString = "UNKNOWN!!!";
        }

        return getClass().getName() + "[type=" + typeString
            + ",buffer=" + buffer + "]";
    } //}}}

    //{{{ Private members

    //{{{ Instance variables
    private int type;
    private TabbedView view;
    private XMLDocument buffer;
    private Object session;
    private VFS vfs;
    private String path;
    private SegmentBuffer m_loadData;
    //}}}

    //{{{ load() method
    private void load() {
        
        InputStream in = null;

        try {
            try {
                
                String[] args = { vfs.getFileName(path) };
                setAbortable(true);
                
               // if (!buffer.isTemporary()) {
               //     setStatus(Messages.getMessage("DocumentBuffer.Loading.Message",args));
               //     setProgressValue(0);
               // }

                path = vfs._canonPath(session,path,view);

                VFS.DirectoryEntry entry = vfs._getDirectoryEntry(session,path,view);
                
                long length;
                if (entry != null) {
                    length = entry.length;
                } else {
                    length = 0L;
                }

                in = vfs._createInputStream(session,path,false,view);
                if (in == null) {
                    return;
                }

                read(autodetect(in),length,false);
               // buffer.setNewFile(false);
                
            } catch(CharConversionException ch) {
                Log.log(Log.ERROR,this,ch);
                
                Object[] pp = { buffer.getProperty(XMLDocument.ENCODING), ch.toString() };
                
                VFSManager.error(view,path,"IO.Error.Encoding.Error",pp);

                buffer.setBooleanProperty(ERROR_OCCURRED,true);
            
            } catch(UnsupportedEncodingException uu) {
                Log.log(Log.ERROR,this,uu);
                Object[] pp = { buffer.getProperty(XMLDocument.ENCODING),
                    uu.toString() };
                VFSManager.error(view,path,"IO.Error.Encoding.Error",pp);

                buffer.setBooleanProperty(ERROR_OCCURRED,true);
            
            } catch(IOException io) {
                Log.log(Log.ERROR,this,io);
                Object[] pp = { io.toString() };
                VFSManager.error(view,path,"IO.Error.Read.Error",pp);

                buffer.setBooleanProperty(ERROR_OCCURRED,true);
            
            } catch(OutOfMemoryError oom) {
                Log.log(Log.ERROR,this,oom);
                VFSManager.error(view,path,"Out.Of.Memory.Error",null);

                buffer.setBooleanProperty(ERROR_OCCURRED,true);
            }
        } catch(WorkThread.Abort a) {
            if (in != null) {
                try {
                    in.close();
                }
                catch(IOException io) {}
            }

            buffer.setBooleanProperty(ERROR_OCCURRED,true);
        } finally {
            try {
                vfs._endVFSSession(session,view);
            } catch (IOException io) {
                Log.log(Log.ERROR,this,io);
                String[] pp = { io.toString() };
                VFSManager.error(view,path,"IO.Error.Read.Error",pp);

                buffer.setBooleanProperty(ERROR_OCCURRED,true);
            } catch (WorkThread.Abort a) {
                buffer.setBooleanProperty(ERROR_OCCURRED,true);
            }
        }
    } //}}}

    //{{{ autodetect() method
    /**
     * Tries to detect if the stream is gzipped, and if it has an encoding
     * specified with an XML PI.
     */
    private Reader autodetect(InputStream in) throws IOException {
        in = new BufferedInputStream(in);

        String encoding = buffer.getProperty(XMLDocument.ENCODING);
        if (!in.markSupported()) {
            Log.log(Log.WARNING,this,"Mark not supported: " + in);
        } else {
            if (buffer.getBooleanProperty(XMLDocument.ENCODING_AUTODETECT, true)) {
                in.mark(XML_PI_LENGTH);
                int b1 = in.read();
                int b2 = in.read();
                int b3 = in.read();
                
                if (encoding.equals(MiscUtilities.UTF_8_Y)) {
                    // Java does not support this encoding so
                    // we have to handle it manually.
                    if (b1 != UTF8_MAGIC_1 || b2 != UTF8_MAGIC_2 || b3 != UTF8_MAGIC_3) {
                        // file does not begin with UTF-8-Y
                        // signature. reset stream, read as
                        // UTF-8.
                        in.reset();
                    } else {
                        // file begins with UTF-8-Y signature.
                        // discard the signature, and read
                        // the remainder as UTF-8.
                    }
                    
                    encoding = "UTF-8";
                } else { 
                   // if (b1 == GZIP_MAGIC_1 && b2 == GZIP_MAGIC_2) {
                   //     in.reset();
                   //     in = new GZIPInputStream(in);
                   //     buffer.setBooleanProperty(Buffer.GZIPPED,true);
                   //     // auto-detect encoding within the gzip stream.
                   //     return autodetect(in);
                   // } else {
                        if ((b1 == UNICODE_MAGIC_1
                            && b2 == UNICODE_MAGIC_2)
                            || (b1 == UNICODE_MAGIC_2
                            && b2 == UNICODE_MAGIC_1))
                        {
                            in.reset();
                            encoding = "UTF-16";
                            buffer.setProperty(XMLDocument.ENCODING,encoding);
                        } else {
                            if (b1 == UTF8_MAGIC_1 && b2 == UTF8_MAGIC_2
                                && b3 == UTF8_MAGIC_3)
                            {
                                // do not reset the stream and just treat it
                                // like a normal UTF-8 file.
                                buffer.setProperty(XMLDocument.ENCODING, MiscUtilities.UTF_8_Y);
                                
                                encoding = "UTF-8";
                            } else {
                                in.reset();
                                
                                byte[] _xmlPI = new byte[XML_PI_LENGTH];
                                int offset = 0;
                                int count;
                                while((count = in.read(_xmlPI,offset,
                                    XML_PI_LENGTH - offset)) != -1)
                                {
                                    offset += count;
                                    if(offset == XML_PI_LENGTH)
                                        break;
                                }
                                
                                String xmlPI = new String(_xmlPI,0,offset, "ASCII");
                                
                                if (xmlPI.startsWith("<?xml")) {
                                    int index = xmlPI.indexOf("encoding=");
                                    if (index != -1 && index + 9 != xmlPI.length()) {
                                        char ch = xmlPI.charAt(index + 9);
                                        int endIndex = xmlPI.indexOf(ch, index + 10);
                                        encoding = xmlPI.substring(index + 10,endIndex);
                                        
                                        if (MiscUtilities.isSupportedEncoding(encoding)) {
                                            buffer.setProperty(XMLDocument.ENCODING, encoding);
                                        } else {
                                            Log.log(Log.WARNING,this,"XML PI specifies unsupported encoding: " + encoding);
                                        }
                                    }
                                }
                                
                                in.reset();
                            }
                        }
                   // }
                }
            }
        }

        return new InputStreamReader(in,encoding);
    } //}}}

    //{{{ read() method
    private SegmentBuffer read(Reader in, long length, boolean insert) throws IOException {
    
        /* we guess an initial size for the array */
       // IntegerArray endOffsets = new IntegerArray(
       //     Math.max(1,(int)(length / 50)));

        // only true if the file size is known
        boolean trackProgress = (length != 0);

        if (trackProgress) {
            setProgressValue(0);
            setProgressMaximum((int)length);
        }

        // if the file size is not known, start with a resonable
        // default buffer size
        if(length == 0)
            length = IOBUFSIZE;

        SegmentBuffer seg = new SegmentBuffer((int)length + 1);

        char[] buf = new char[IOBUFSIZE];

        // Number of characters in 'buf' array.
        // InputStream.read() doesn't always fill the
        // array (eg, the file size is not a multiple of
        // IOBUFSIZE, or it is a GZipped file, etc)
        int len;

        // True if a \n was read after a \r. Usually
        // means this is a DOS/Windows file
        boolean CRLF = false;

        // A \r was read, hence a MacOS file
        boolean CROnly = false;

        // Was the previous read character a \r?
        // If we read a \n and this is true, we assume
        // we have a DOS/Windows file
        boolean lastWasCR = false;

        // Number of lines read. Every 100 lines, we update the
        // progress bar
        int lineCount = 0;

        while((len = in.read(buf,0,buf.length)) != -1)
        {
            // Offset of previous line, relative to
            // the start of the I/O buffer (NOT
            // relative to the start of the document)
            int lastLine = 0;

            for(int i = 0; i < len; i++)
            {
                // Look for line endings.
                switch(buf[i])
                {
                case '\r':
                    // If we read a \r and
                    // lastWasCR is also true,
                    // it is probably a Mac file
                    // (\r\r in stream)
                    if(lastWasCR)
                    {
                        CROnly = true;
                        CRLF = false;
                    }
                    // Otherwise set a flag,
                    // so that \n knows that last
                    // was a \r
                    else
                    {
                        lastWasCR = true;
                    }

                    // Insert a line
                    seg.append(buf,lastLine,i -
                        lastLine);
                    seg.append('\n');
                   // endOffsets.add(seg.count);
                    if(trackProgress && lineCount++ % PROGRESS_INTERVAL == 0)
                        setProgressValue(seg.count);

                    // This is i+1 to take the
                    // trailing \n into account
                    lastLine = i + 1;
                    break;
                case '\n':
                    // If lastWasCR is true,
                    // we just read a \r followed
                    // by a \n. We specify that
                    // this is a Windows file,
                    // but take no further
                    // action and just ignore
                    // the \r.
                    if(lastWasCR)
                    {
                        CROnly = false;
                        CRLF = true;
                        lastWasCR = false;
                        // Bump lastLine so
                        // that the next line
                        // doesn't erronously
                        // pick up the \r
                        lastLine = i + 1;
                    }
                    // Otherwise, we found a \n
                    // that follows some other
                    // character, hence we have
                    // a Unix file
                    else
                    {
                        CROnly = false;
                        CRLF = false;
                        seg.append(buf,lastLine,
                            i - lastLine);
                        seg.append('\n');
                       // endOffsets.add(seg.count);
                        if(trackProgress && lineCount++ % PROGRESS_INTERVAL == 0)
                            setProgressValue(seg.count);
                        lastLine = i + 1;
                    }
                    break;
                default:
                    // If we find some other
                    // character that follows
                    // a \r, so it is not a
                    // Windows file, and probably
                    // a Mac file
                    if(lastWasCR)
                    {
                        CROnly = true;
                        CRLF = false;
                        lastWasCR = false;
                    }
                    break;
                }
            }

            if(trackProgress)
                setProgressValue(seg.count);

            // Add remaining stuff from buffer
            seg.append(buf,lastLine,len - lastLine);
        }

        setAbortable(false);

        String lineSeparator;
        if(seg.count == 0) {
            // 0-byte files should open using
            // the default line seperator"
            lineSeparator = jsXe.getProperty(
                "xml.document."+XMLDocument.LINE_SEPARATOR,
                System.getProperty("line.separator"));
        } else {
            if (CRLF) {
                lineSeparator = "\r\n";
            } else {
                if(CROnly) {
                    lineSeparator = "\r";
                } else {
                    lineSeparator = "\n";
                }
            }
        }

        in.close();

        // Chop trailing newline and/or ^Z (if any)
        int bufferLength = seg.count;
        if (bufferLength != 0) {
            char ch = seg.array[bufferLength - 1];
            if(ch == 0x1a /* DOS ^Z */)
                seg.count--;
        }

       // buffer.setBooleanProperty(Buffer.TRAILING_EOL,false);
       // if (bufferLength != 0 && jEdit.getBooleanProperty("stripTrailingEOL")) {
       //     char ch = seg.array[bufferLength - 1];
       //     if (ch == '\n') {
       //         buffer.setBooleanProperty(Buffer.TRAILING_EOL,true);
       //         seg.count--;
       //        // endOffsets.setSize(endOffsets.getSize() - 1);
       //     }
       // }

        // add a line marker at the end for proper offset manager
        // operation
       // endOffsets.add(seg.count + 1);

        // to avoid having to deal with read/write locks and such,
        // we insert the loaded data into the buffer in the
        // post-load cleanup runnable, which runs in the AWT thread.
        if (!insert) {
            m_loadData = seg;
           // buffer.setProperty(END_OFFSETS,endOffsets);
           // buffer.setProperty(NEW_PATH,path);
            if (lineSeparator != null) {
                buffer.setProperty(XMLDocument.LINE_SEPARATOR, lineSeparator);
            }
        }

        // used in insert()
        return seg;
    } //}}}

    //{{{ save() method
    private void save()
    {
        OutputStream out = null;

        try
        {
            String[] args = { vfs.getFileName(path) };
            setStatus(Messages.getMessage("vfs.status.save",args));

            // the entire save operation can be aborted...
            setAbortable(true);

            path = vfs._canonPath(session,path,view);           if(!MiscUtilities.isURL(path))
                path = MiscUtilities.resolveSymlinks(path);

            // Only backup once per session
           // if(buffer.getProperty(Buffer.BACKED_UP) == null
           //     || jEdit.getBooleanProperty("backupEverySave"))
           // {
           //     vfs._backup(session,path,view);
           //     buffer.setBooleanProperty(Buffer.BACKED_UP,true);
           // }

            /* if the VFS supports renaming files, we first
             * save to #<filename>#save#, then rename that
             * to <filename>, so that if the save fails,
             * data will not be lost.
             *
             * as of 4.1pre7 we now call vfs.getTwoStageSaveName()
             * instead of constructing the path directly
             * since some VFS's might not allow # in filenames.
             */
            String savePath;

            boolean twoStageSave = (vfs.getCapabilities() & VFS.RENAME_CAP) != 0
                && jsXe.getBooleanProperty("twoStageSave", false);
            if(twoStageSave)
                savePath = vfs.getTwoStageSaveName(path);
            else
                savePath = path;

            out = vfs._createOutputStream(session,savePath,view);

            try
            {
                // this must be after the stream is created or
                // we deadlock with SSHTools.
                buffer.readLock();
                if(out != null)
                {
                    // Can't use buffer.getName() here because
                    // it is not changed until the save is
                    // complete
                   // if(savePath.endsWith(".gz"))
                   //     buffer.setBooleanProperty(Buffer.GZIPPED,true);

                   // if(buffer.getBooleanProperty(Buffer.GZIPPED))
                   //     out = new GZIPOutputStream(out);

                    write(buffer,out);

                    if(twoStageSave)
                    {
                        if(!vfs._rename(session,savePath,path,view))
                            throw new IOException("Rename failed: " + savePath);
                    }
                }
                else
                    buffer.setBooleanProperty(ERROR_OCCURRED,true);

                if(!twoStageSave)
                    VFSManager.sendVFSUpdate(vfs,path,true);
            }
            finally
            {
                buffer.readUnlock();
            }
        }
        catch(IOException io)
        {
            Log.log(Log.ERROR,this,io);
            String[] pp = { io.toString() };
            VFSManager.error(view,path,"ioerror.write-error",pp);

            buffer.setBooleanProperty(ERROR_OCCURRED,true);
        }
        catch(WorkThread.Abort a)
        {
            if(out != null)
            {
                try
                {
                    out.close();
                }
                catch(IOException io)
                {
                }
            }

            buffer.setBooleanProperty(ERROR_OCCURRED,true);
        }
        finally
        {
            try
            {
                vfs._saveComplete(session,buffer,path,view);
                vfs._endVFSSession(session,view);
            }
            catch(IOException io)
            {
                Log.log(Log.ERROR,this,io);
                String[] pp = { io.toString() };
                VFSManager.error(view,path,"ioerror.write-error",pp);

                buffer.setBooleanProperty(ERROR_OCCURRED,true);
            }
            catch(WorkThread.Abort a)
            {
                buffer.setBooleanProperty(ERROR_OCCURRED,true);
            }
        }
    } //}}}

    //{{{ autosave() method
    private void autosave()
    {
        OutputStream out = null;

        try
        {
            String[] args = { vfs.getFileName(path) };
            setStatus(Messages.getMessage("vfs.status.autosave",args));

            // the entire save operation can be aborted...
            setAbortable(true);

            try
            {
                //buffer.readLock();

                if(!buffer.getBooleanProperty("xmldocument.dirty", false)) {
                    // buffer has been saved while we
                    // were waiting.
                    return;
                }

                out = vfs._createOutputStream(session,path,view);
                if(out == null)
                    return;

                write(buffer,out);
            }
            catch(Exception e)
            {
            }
            finally
            {
                //buffer.readUnlock();
            }
        }
        catch(WorkThread.Abort a)
        {
            if(out != null)
            {
                try
                {
                    out.close();
                }
                catch(IOException io)
                {
                }
            }
        }
    } //}}}

    //{{{ write() method
   // private void write(XMLDocument buffer, OutputStream _out) throws IOException {
   //     BufferedWriter out = null;

   //     try {
   //         String encoding = buffer.getStringProperty(Buffer.ENCODING);
   //         if (encoding.equals(MiscUtilities.UTF_8_Y)) {
   //             // not supported by Java...
   //             _out.write(UTF8_MAGIC_1);
   //             _out.write(UTF8_MAGIC_2);
   //             _out.write(UTF8_MAGIC_3);
   //             _out.flush();
   //             encoding = "UTF-8";
   //         }

   //         out = new BufferedWriter(new OutputStreamWriter(_out,encoding), IOBUFSIZE);

   //         Segment lineSegment = new Segment();
   //         String newline = buffer.getStringProperty(Buffer.LINESEP);
   //         if (newline == null) {
   //             newline = System.getProperty("line.separator");
   //         }

   //         setProgressMaximum(buffer.getLineCount() / PROGRESS_INTERVAL);
   //         setProgressValue(0);

   //         int i = 0;
   //         while (i < buffer.getLineCount()) {
   //             buffer.getLineText(i,lineSegment);
   //             out.write(lineSegment.array, lineSegment.offset, lineSegment.count);

   //             if (i != buffer.getLineCount() - 1) {
   //                 out.write(newline);
   //             }

   //             if (++i % PROGRESS_INTERVAL == 0) {
   //                 setProgressValue(i / PROGRESS_INTERVAL);
   //             }
   //         }

   //        // if(jEdit.getBooleanProperty("stripTrailingEOL")
   //        //     && buffer.getBooleanProperty(Buffer.TRAILING_EOL))
   //        // {
   //        //     out.write(newline);
   //        // }
   //     } finally {
   //         if (out != null) {
   //             out.close();
   //         } else {
   //             _out.close();
   //         }
   //     }
   // } //}}}
    
    //{{{ write()
    private void write(XMLDocument buffer, OutputStream out) throws IOException {
        
        String newLine = buffer.getProperty(XMLDocument.LINE_SEPARATOR);
        
        String encoding = buffer.getProperty(XMLDocument.ENCODING);
        if (encoding.equals(MiscUtilities.UTF_8_Y)) {
            // not supported by Java...
            out.write(UTF8_MAGIC_1);
            out.write(UTF8_MAGIC_2);
            out.write(UTF8_MAGIC_3);
            out.flush();
            encoding = "UTF-8";
        }
        
        //now just write out the text.
        int length = buffer.getLength();
        int index = 0;
        BufferedWriter outbuf = new BufferedWriter(new OutputStreamWriter(out, encoding), IO_BUFFER_SIZE);
        Segment seg = new Segment();
        
        while (index < length) {
            int size = WRITE_SIZE;
            try {
                size = Math.min(length - index, WRITE_SIZE);
            } catch(NumberFormatException nf) {
                Log.log(Log.ERROR, this, nf);
            }
            
           // out.write(m_content.getText(index, size).getBytes(getProperty(ENCODING)), index, size);
            buffer.getText(index, size, seg);
            
            int startOffset = seg.offset;
            int endOffset = size + seg.offset;
            
            for (int i=startOffset; i<endOffset; i++) {
                if (seg.array[i]=='\n') {
                    outbuf.write(seg.array, seg.offset, i - seg.offset);
                    outbuf.write(newLine.toCharArray(), 0, newLine.length());
                    
                    //add 1 because of \n character,
                    seg.count -= i-seg.offset+1;
                    seg.offset += i-seg.offset+1;
                }
            }
            
            //write the rest
            outbuf.write(seg.array, seg.offset, seg.count);
            index += size;
        }
        
        outbuf.close();
    }//}}}
    
   // //{{{ insert() method
   // private void insert()
   // {
   //     InputStream in = null;

   //     try
   //     {
   //         try
   //         {
   //             String[] args = { vfs.getFileName(path) };
   //             setStatus(jEdit.getProperty("vfs.status.load",args));
   //             setAbortable(true);

   //             path = vfs._canonPath(session,path,view);

   //             VFS.DirectoryEntry entry = vfs._getDirectoryEntry(
   //                 session,path,view);
   //             long length;
   //             if(entry != null)
   //                 length = entry.length;
   //             else
   //                 length = 0L;

   //             in = vfs._createInputStream(session,path,false,view);
   //             if(in == null)
   //                 return;

   //             final SegmentBuffer seg = read(
   //                 autodetect(in),length,true);

   //             /* we don't do this in Buffer.insert() so that
   //                we can insert multiple files at once */
   //             VFSManager.runInAWTThread(new Runnable()
   //             {
   //                 public void run()
   //                 {
   //                     view.getTextArea().setSelectedText(
   //                         seg.toString());
   //                 }
   //             });
   //         }
   //         catch(IOException io)
   //         {
   //             Log.log(Log.ERROR,this,io);
   //             String[] pp = { io.toString() };
   //             VFSManager.error(view,path,"ioerror.read-error",pp);

   //             buffer.setBooleanProperty(ERROR_OCCURRED,true);
   //         }
   //     }
   //     catch(WorkThread.Abort a)
   //     {
   //         if(in != null)
   //         {
   //             try
   //             {
   //                 in.close();
   //             }
   //             catch(IOException io)
   //             {
   //             }
   //         }

   //         buffer.setBooleanProperty(ERROR_OCCURRED,true);
   //     }
   //     finally
   //     {
   //         try
   //         {
   //             vfs._endVFSSession(session,view);
   //         }
   //         catch(IOException io)
   //         {
   //             Log.log(Log.ERROR,this,io);
   //             String[] pp = { io.toString() };
   //             VFSManager.error(view,path,"ioerror.read-error",pp);

   //             buffer.setBooleanProperty(ERROR_OCCURRED,true);
   //         }
   //         catch(WorkThread.Abort a)
   //         {
   //             buffer.setBooleanProperty(ERROR_OCCURRED,true);
   //         }
   //     }
   // } //}}}

    //{{{ Private static members
    private static final int READ_SIZE = 5120;
    private static final int WRITE_SIZE = 5120;
    private static final int IO_BUFFER_SIZE = 32768;
    //}}}
    
    //}}}
}
