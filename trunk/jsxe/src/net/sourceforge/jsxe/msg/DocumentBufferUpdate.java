/*
DocumentBufferUpdate.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2006 Ian Lewis (IanLewis@member.fsf.org)
Copyright (C) 1999, 2001 Slava Pestov

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
 * Message sent when a document-related change occurs.
 *
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @author Slava Pestov
 * @version $Id$
 *
 * @since jsXe 0.5 pre1
 */
public class DocumentBufferUpdate extends EBMessage {
    
    //{{{ Message types
    /**
     * Buffer created.
     */
   // public static final Object CREATED = "CREATED";

    /**
     * Buffer load started.
     */
   // public static final Object LOAD_STARTED = "LOAD_STARTED";

    /**
     * Buffer loaded.
     */
    public static final Object LOADED = "LOADED";

    /**
     * Buffer closed.
     */
    public static final Object CLOSED = "CLOSED";

    /**
     * Buffer dirty changed.
     */
    public static final Object DIRTY_CHANGED = "DIRTY_CHANGED";

    /**
     * Buffer saving.
     */
    public static final Object SAVING = "SAVING";

    /**
     * Buffer saved.
     */
    public static final Object SAVED = "SAVED";

    /**
     * Properties changed.
     */
   // public static final Object PROPERTIES_CHANGED = "PROPERTIES_CHANGED";
    //}}}

    //{{{ DocumentBufferUpdate constructor
    /**
     * Creates a new buffer update message.
     * @param buffer The document buffer
     * @param what the update type
     */
    public DocumentBufferUpdate(DocumentBuffer buffer, Object what) {
        super(buffer);

        if (what == null) {
            throw new NullPointerException("What must be non-null");
        }
            
        this.what = what;
    } //}}}

    //{{{ getWhat()
    /**
     * Returns what caused this buffer update.
     */
    public Object getWhat() {
        return what;
    } //}}}

    //{{{ getBuffer()
    /**
     * Returns the buffer involved.
     */
    public DocumentBuffer getDocumentBuffer() {
        return (DocumentBuffer)getSource();
    } //}}}

    //{{{ paramString()
    public String paramString() {
        return "what=" + what + "," + super.paramString();
    } //}}}

    //{{{ Private members
    private Object what;
    //}}}
}
