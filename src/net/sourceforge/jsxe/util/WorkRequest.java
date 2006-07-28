/*
WorkRequest.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 2000 Slava Pestov

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

package net.sourceforge.jsxe.util;

/**
 * A subclass of the Runnable interface.
 * @author Slava Pestov
 * @version $Id$
 * @since jsXe 0.5 pre3
 */
public abstract class WorkRequest implements Runnable {
    
    /**
     * Sets if the request can be aborted.
     */
    public void setAbortable(boolean abortable) {
        Thread thread = Thread.currentThread();
        if (thread instanceof WorkThread) {
            ((WorkThread)thread).setAbortable(abortable);
        }
    }

    /**
     * Sets the status text.
     * @param status The status text
     */
    public void setStatus(String status) {
        Thread thread = Thread.currentThread();
        if (thread instanceof WorkThread) {
            ((WorkThread)thread).setStatus(status);
        }
    }

    /**
     * Sets the progress value.
     * @param value The progress value.
     */
    public void setProgressValue(int value) {
        Thread thread = Thread.currentThread();
        if (thread instanceof WorkThread) {
            ((WorkThread)thread).setProgressValue(value);
        }
    }

    /**
     * Sets the maximum progress value.
     * @param value The progress value.
     */
    public void setProgressMaximum(int value) {
        Thread thread = Thread.currentThread();
        if (thread instanceof WorkThread) {
            ((WorkThread)thread).setProgressMaximum(value);
        }
    }
}
