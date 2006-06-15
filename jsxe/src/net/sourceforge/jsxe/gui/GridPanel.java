/*
AbstractOptionPane.java
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 1998, 1999, 2000, 2001, 2002 Slava Pestov
Portions Copyright (C) 2005 Ian Lewis (IanLewis@member.fsf.org)

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

package net.sourceforge.jsxe.gui;

//{{{ Imports
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
//}}}

/**
 * A convenient class for laying out components in a form or dialog.
 *
 * It is derived from Java's <code>JPanel</code> class and uses a
 * <code>GridBagLayout</code> object for component management. Since
 * <code>GridBagLayout</code> can be a bit cumbersome to use, this class
 * contains shortcut methods to simplify layout:
 *
 * <ul>
 * <li>{@link #addComponent(Component)}</li>
 * <li>{@link #addComponent(Component,int)}</li>
 * <li>{@link #addSeparator()}</li>
 * <li>{@link #addSeparator(String)}</li>
 * </ul>
 *
 * @
 * @author Slava Pestov
 * @author John Gellene (API documentation)
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre1
 */
public class GridPanel extends JPanel {
	
    //{{{ GridPanel constructor
    /**
     * Creates a new panel.
     */
    public GridPanel() {
        setLayout(gridBag = new GridBagLayout());
    } //}}}

    //{{{ addComponent() method
    /**
     * Adds a component to the option pane. Components are
     * added in a vertical fashion, one per row.
     * @param comp The component
     */
    public void addComponent(Component comp) {
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridy = y++;
        cons.gridheight = 1;
        cons.gridwidth = cons.REMAINDER;
        cons.fill = GridBagConstraints.NONE;
        cons.anchor = GridBagConstraints.WEST;
        cons.weightx = 1.0f;
        cons.insets = new Insets(1,0,1,0);

        gridBag.setConstraints(comp,cons);
        add(comp);
    } //}}}

    //{{{ addComponent() method
    /**
     * Adds a component to the option pane. Components are
     * added in a vertical fashion, one per row.
     * @param comp The component
     * @param fill Fill parameter to GridBagConstraints
     */
    public void addComponent(Component comp, int fill) {
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridy = y++;
        cons.gridheight = 1;
        cons.gridwidth = cons.REMAINDER;
        cons.fill = fill;
        cons.anchor = GridBagConstraints.WEST;
        cons.weightx = 1.0f;
        cons.insets = new Insets(1,0,1,0);

        gridBag.setConstraints(comp,cons);
        add(comp);
    } //}}}

    //{{{ addSeparator() method
    /**
     * Adds a separator component.
     */
    public void addSeparator() {
        addComponent(Box.createVerticalStrut(6));

        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);

        GridBagConstraints cons = new GridBagConstraints();
        cons.gridy = y++;
        cons.gridheight = 1;
        cons.gridwidth = cons.REMAINDER;
        cons.fill = GridBagConstraints.BOTH;
        cons.anchor = GridBagConstraints.WEST;
        cons.weightx = 1.0f;
        //cons.insets = new Insets(1,0,1,0);

        gridBag.setConstraints(sep,cons);
        add(sep);

        addComponent(Box.createVerticalStrut(6));
    } //}}}

    //{{{ addSeparator() method
    /**
     * Adds a separator component.
     * @param label The separator label property
     */
    public void addSeparator(String label) {
        if (y != 0) {
            addComponent(Box.createVerticalStrut(6));
		}

        Box box = new Box(BoxLayout.X_AXIS);
        Box box2 = new Box(BoxLayout.Y_AXIS);
        box2.add(Box.createGlue());
        box2.add(new JSeparator(JSeparator.HORIZONTAL));
        box2.add(Box.createGlue());
        box.add(box2);
        JLabel l = new JLabel(label);
        l.setMaximumSize(l.getPreferredSize());
        box.add(l);
        Box box3 = new Box(BoxLayout.Y_AXIS);
        box3.add(Box.createGlue());
        box3.add(new JSeparator(JSeparator.HORIZONTAL));
        box3.add(Box.createGlue());
        box.add(box3);

        GridBagConstraints cons = new GridBagConstraints();
        cons.gridy = y++;
        cons.gridheight = 1;
        cons.gridwidth = cons.REMAINDER;
        cons.fill = GridBagConstraints.BOTH;
        cons.anchor = GridBagConstraints.WEST;
        cons.weightx = 1.0f;
        cons.insets = new Insets(1,0,1,0);

        gridBag.setConstraints(box,cons);
        add(box);
    } //}}}

    //{{{ Protected members
    /**
     * Has the option pane been initialized?
     */
    protected boolean initialized;

    /**
     * The layout manager.
     */
    protected GridBagLayout gridBag;

    /**
     * The number of components already added to the layout manager.
     */
    protected int y;
    //}}}

}
