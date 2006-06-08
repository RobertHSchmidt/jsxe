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

package net.sourceforge.jsxe.options;

//{{{ Imports
import net.sourceforge.jsxe.gui.GridPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
//}}}

/**
 * The default implementation of the option pane interface.<p>
 *
 * Most option panes extend this implementation of {@link OptionPane}, instead
 * of implementing {@link OptionPane} directly. This class provides a convenient
 * default framework for laying out configuration options. It adds extra
 * <code>addComponent</code> implementations over the
 * {@link net.sourceforge.jsxe.gui.GridPanel} class.<p>
 * 
 * <ul>
 * <li>{@link #addComponent(Component,String)}</li>
 * <li>{@link #addComponent(String,Component)}</li>
 * <li>{@link #addComponent(String,Component,int)}</li>
 * <li>{@link #addComponent(Component,Component)}</li>
 * <li>{@link #addComponent(Component,Component,int)}</li>
 * <li>{@link #addComponent(String, Component, String)}</li>
 * <li>{@link #addComponent(Component, Component, String)}</li>
 * </ul>
 *
 * @author Slava Pestov
 * @author John Gellene (API documentation)
 * @author Ian Lewis (<a href="mailto:IanLewis@member.fsf.org">IanLewis@member.fsf.org</a>)
 * @version $Id$
 * @since jsXe 0.5 pre1
 */
public abstract class AbstractOptionPane extends GridPanel implements OptionPane {
	
    //{{{ AbstractOptionPane constructor
    /**
     * Creates a new option pane.
     * @param name The internal name.
     */
    public AbstractOptionPane(String name) {
        super();
        this.name = name;
    } //}}}

    //{{{ getName() method
    public String getName() {
        return name;
    } //}}}

    //{{{ getComponent() method
    /**
     * Returns the component that should be displayed for this option pane.
     * Because this class extends Component, it simply returns "this".
     */
    public Component getComponent() {
        return this;
    } //}}}

    //{{{ addComponent() method
    /**
     * Adds a tooltipped component to the option pane. Components are
     * added in a vertical fashion, one per row. The label is
     * displayed to the left of the component.
     * @param comp The component
     * @param toolTip the tooltip
     */
    public void addComponent(JComponent comp, String toolTip) {
        if (toolTip != null) {
            comp.setToolTipText(toolTip);
        }
        addComponent(comp);
    } //}}}
    
    //{{{ addComponent() method
    /**
     * Adds a labeled component to the option pane. Components are
     * added in a vertical fashion, one per row. The label is
     * displayed to the left of the component.
     * @param label The label
     * @param comp The component
     */
    public void addComponent(String label, Component comp) {
        JLabel l = new JLabel(label);
        l.setBorder(new EmptyBorder(0,0,0,12));
        addComponent(l,comp,GridBagConstraints.BOTH);
    } //}}}

    //{{{ addComponent() method
    /**
     * Adds a labeled component to the option pane. Components are
     * added in a vertical fashion, one per row. The label is
     * displayed to the left of the component.
     * @param label The label
     * @param comp The component
     * @param toolTip The toolTip for the component
     */
    public void addComponent(String label, JComponent comp, String toolTip) {
        JLabel l = new JLabel(label);
        l.setBorder(new EmptyBorder(0,0,0,12));
        if (toolTip != null) {
            l.setToolTipText(toolTip);
            comp.setToolTipText(toolTip);
        }
        addComponent(l,comp,GridBagConstraints.BOTH);
    } //}}}
    
    //{{{ addComponent() method
    /**
     * Adds a labeled component to the option pane. Components are
     * added in a vertical fashion, one per row. The label is
     * displayed to the left of the component.
     * @param label The label
     * @param comp The component
     * @param fill Fill parameter to GridBagConstraints for the right
     * component
     */
    public void addComponent(String label, Component comp, int fill) {
        JLabel l = new JLabel(label);
        l.setBorder(new EmptyBorder(0,0,0,12));
        addComponent(l,comp,fill);
    } //}}}

    //{{{ addComponent() method
    /**
     * Adds a labeled component to the option pane. Components are
     * added in a vertical fashion, one per row. The label is
     * displayed to the left of the component.
     * @param comp1 The label
     * @param comp2 The component
     * @param toolTip The toolTip text
     */
    public void addComponent(JComponent comp1, JComponent comp2, String toolTip) {
        if (toolTip != null) {
            comp1.setToolTipText(toolTip);
            comp2.setToolTipText(toolTip);
        }
        addComponent(comp1,comp2);
    } //}}}
    
    //{{{ addComponent() method
    /**
     * Adds a labeled component to the option pane. Components are
     * added in a vertical fashion, one per row. The label is
     * displayed to the left of the component.
     * @param comp1 The label
     * @param comp2 The component
     */
    public void addComponent(Component comp1, Component comp2) {
        addComponent(comp1,comp2,GridBagConstraints.BOTH);
    } //}}}

    //{{{ addComponent() method
    /**
     * Adds a labeled component to the option pane. Components are
     * added in a vertical fashion, one per row. The label is
     * displayed to the left of the component.
     * @param comp1 The label
     * @param comp2 The component
     * @param fill Fill parameter to GridBagConstraints for the right
     * component
     */
    public void addComponent(Component comp1, Component comp2, int fill) {
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridy = y++;
        cons.gridheight = 1;
        cons.gridwidth = 1;
        cons.weightx = 0.0f;
        cons.insets = new Insets(1,0,1,0);
        cons.fill = GridBagConstraints.BOTH;

        gridBag.setConstraints(comp1,cons);
        add(comp1);

        cons.fill = fill;
        cons.gridx = 1;
        cons.weightx = 1.0f;
        gridBag.setConstraints(comp2,cons);
        add(comp2);
    } //}}}
    
    //{{{ init() method
    /**
     * Do not override this method, override {@link #_init()} instead.
     */
    public void init() {
        if (!initialized) {
            initialized = true;
            _init();
        }
    } //}}}

    //{{{ save() method
    /**
     * Do not override this method, override {@link #_save()} instead.
     */
    public void save() {
        if (initialized) {
            _save();
		}
    } //}}}

    //{{{ Protected members
    /**
     * This method should create and arrange the components of the option pane
     * and initialize the option data displayed to the user. This method
     * is called when the option pane is first displayed, and is not
     * called again for the lifetime of the object.
     */
     protected abstract void _init();

    /**
     * Called when the options dialog's "ok" button is clicked.
     * This should save any properties being edited in this option
     * pane.
     */
    protected abstract void _save();
    //}}}

    //{{{ Private members
    private String name;
    //}}}
}
