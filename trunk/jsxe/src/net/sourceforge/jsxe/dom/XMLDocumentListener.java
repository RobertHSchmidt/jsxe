/*
XMLDocumentListener.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

jsXe is the Java Simple XML Editor
jsXe is a gui application that can edit an XML document and create a tree view.
The user can then edit this tree and the content in the tree and save the
document.

This file contains the interface that defines listeners used with
the XMLDocument interface. Listeners allow other components to be
notified when something pertaining to the XMLDocument changes.

This file written by Ian Lewis (IanLewis@member.fsf.org)
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
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
Optionally, you may find a copy of the GNU General Public License
from http://www.fsf.org/copyleft/gpl.txt
*/


package net.sourceforge.jsxe.dom;
/**
 * XMLDocumentListener is used to notify objects of a change to the XMLDocument.
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 */
public interface XMLDocumentListener {
    
    /**
     * Called when the properties associated with the XMLDocument have changed.
     * @param source The source XMLDocument
     * @param propertyKey The key to the property that has changed.
     */
    public void propertiesChanged(XMLDocument source, String propertyKey);
    
    /**
     * Called when the structure of the XMLDocument has changed.
     * @param source The source XMLDocument
     * @param location The AdapterNode location where the change occurred.
     *                 If a node was removed then this is the parent of the
     *                 node that was removed.
     */
   // public void structureChanged(XMLDocument source, AdapterNode location);
    
    /**
     * Called when the file on disk that the XMLDocument represents is changed.
     * @param source The source XMLDocument.
     */
    public void fileChanged(XMLDocument source);
    
}