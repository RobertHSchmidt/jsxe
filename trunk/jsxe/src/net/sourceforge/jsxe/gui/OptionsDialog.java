/*
OptionsDialog.java - Tree options dialog
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:

Copyright (C) 1998, 2003 Slava Pestov
Portions copyright (C) 1999 mike dillon

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package net.sourceforge.jsxe.gui;

//{{{ Imports
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import net.sourceforge.jsxe.options.*;
import net.sourceforge.jsxe.jsXe;
import net.sourceforge.jsxe.OperatingSystem;
//}}}

/**
 * An abstract tabbed options dialog box.
 * @author Slava Pestov
 * @author Ian Lewis
 * @version $Id$
 */
public abstract class OptionsDialog extends EnhancedDialog
    implements ActionListener, TreeSelectionListener
{
    //{{{ OptionsDialog constructor
    public OptionsDialog(Frame frame, String name, String title, String pane)
    {
        super(frame, title, true);
        init(name,pane);
    } //}}}

    //{{{ OptionsDialog constructor
    public OptionsDialog(Dialog dialog, String name, String title, String pane)
    {
        super(dialog, title, true);
        init(name,pane);
    } //}}}

    //{{{ addOptionGroup() method
    public void addOptionGroup(OptionGroup group)
    {
        addOptionGroup(group, getDefaultGroup());
    } //}}}

    //{{{ addOptionPane() method
    public void addOptionPane(OptionPane pane)
    {
        addOptionPane(pane, getDefaultGroup());
    } //}}}

    //{{{ ok() method
    public void ok()
    {
        jsXe.setProperty(name + ".last",currentPane);
        ok(true);
    } //}}}

    //{{{ cancel() method
    public void cancel()
    {
        jsXe.setProperty(name + ".last",currentPane);
        saveGeometry(this, name);
        jsXe.setIntegerProperty(name + ".splitter", splitter.getDividerLocation());
        dispose();
    } //}}}

    //{{{ ok() method
    public void ok(boolean dispose)
    {
        OptionTreeModel m = (OptionTreeModel) paneTree.getModel();
        ((OptionGroup) m.getRoot()).save();
        saveGeometry(this, name);

        jsXe.setIntegerProperty(name + ".splitter", splitter.getDividerLocation());
        
        // get rid of this dialog if necessary
        if(dispose) {
            dispose();
        }
    } //}}}

    //{{{ actionPerformed() method
    public void actionPerformed(ActionEvent evt)
    {
        Object source = evt.getSource();

        if(source == ok)
        {
            ok();
        }
        else if(source == cancel)
        {
            cancel();
        }
        else if(source == apply)
        {
            ok(false);
        }
    } //}}}

    //{{{ valueChanged() method
    public void valueChanged(TreeSelectionEvent evt)
    {
        TreePath path = evt.getPath();

        if (path == null || !(path.getLastPathComponent() instanceof
            OptionPane)) return;

        Object[] nodes = path.getPath();

        StringBuffer buf = new StringBuffer();

        OptionPane optionPane = null;
        String name = null;

        int lastIdx = nodes.length - 1;

        for (int i = paneTree.isRootVisible() ? 0 : 1;
            i <= lastIdx; i++)
        {
            String label = null;
            if (nodes[i] instanceof OptionPane)
            {
                optionPane = (OptionPane)nodes[i];
                name = optionPane.getName();
                label = optionPane.getTitle();
            }
            else if (nodes[i] instanceof OptionGroup)
            {
                OptionGroup group = (OptionGroup)nodes[i];
                name = group.getName();
                label = group.getTitle();
            }
            else
            {
                continue;
            }

            if (name != null) {
                if (label == null) {
                    buf.append(name);
                } else {
                    buf.append(label);
                }
            }

            if (i != lastIdx) buf.append(": ");
        }

        currentLabel.setText(buf.toString());

        optionPane.init();

        ((CardLayout)cardPanel.getLayout()).show(cardPanel, name);

        // workaround...
        addNotify();

        updateSize();

        currentPane = name;
    } //}}}

    //{{{ Protected members
    protected abstract OptionTreeModel createOptionTreeModel();
    protected abstract OptionGroup getDefaultGroup();

    //{{{ addOptionGroup() method
    protected void addOptionGroup(OptionGroup child, OptionGroup parent)
    {
        Enumeration enum = child.getMembers();

        while (enum.hasMoreElements())
        {
            Object elem = enum.nextElement();

            if (elem instanceof OptionPane)
            {
                addOptionPane((OptionPane) elem, child);
            }
            else if (elem instanceof OptionGroup)
            {
                addOptionGroup((OptionGroup) elem, child);
            }
        }

        parent.addOptionGroup(child);
    } //}}}

    //{{{ addOptionPane() method
    protected void addOptionPane(OptionPane pane, OptionGroup parent)
    {
        String name = pane.getName();
        if(firstPane == null)
            firstPane = name;

        cardPanel.add(pane.getComponent(), name);

        parent.addOptionPane(pane);
    } //}}}

    //}}}

    //{{{ Private members

    //{{{ Instance variables
    private String name;
    private JSplitPane splitter;
    private JTree paneTree;
    private JPanel cardPanel;
    private JLabel currentLabel;
    private JButton ok;
    private JButton cancel;
    private JButton apply;
    private String currentPane;
    private String firstPane;
    //}}}

    //{{{ init() method
    private void init(String name, String pane)
    {
        this.name = name;

        JPanel content = new JPanel(new BorderLayout(12,12));
        content.setBorder(new EmptyBorder(12,12,12,12));
        setContentPane(content);

        JPanel stage = new JPanel(new BorderLayout(6,6));

        // currentLabel displays the path of the currently selected
        // OptionPane at the top of the stage area
        currentLabel = new JLabel();
        currentLabel.setHorizontalAlignment(JLabel.LEFT);
        stage.add(currentLabel, BorderLayout.NORTH);

        cardPanel = new JPanel(new CardLayout());
        stage.add(cardPanel, BorderLayout.CENTER);

        paneTree = new JTree(createOptionTreeModel());
        paneTree.setVisibleRowCount(1);
        paneTree.setCellRenderer(new PaneNameRenderer());

        paneTree.setShowsRootHandles(true);
        paneTree.setRootVisible(false);

        JScrollPane scroller = new JScrollPane(paneTree,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            scroller,stage);
        splitter.setDividerLocation(scroller.getPreferredSize().width);
        content.add(splitter, BorderLayout.CENTER);

        Box buttons = new Box(BoxLayout.X_AXIS);
        buttons.add(Box.createGlue());

        ok = new JButton("OK");
        ok.addActionListener(this);
        buttons.add(ok);
        buttons.add(Box.createHorizontalStrut(6));
        getRootPane().setDefaultButton(ok);
        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        buttons.add(cancel);
        buttons.add(Box.createHorizontalStrut(6));
        apply = new JButton("Apply");
        apply.addActionListener(this);
        buttons.add(apply);

        buttons.add(Box.createGlue());

        content.add(buttons, BorderLayout.SOUTH);

        // register the Options dialog as a TreeSelectionListener.
        // this is done before the initial selection to ensure that the
        // first selected OptionPane is displayed on startup.
        paneTree.getSelectionModel().addTreeSelectionListener(this);

        OptionGroup rootNode = (OptionGroup)paneTree.getModel().getRoot();
        for(int i = 0; i < rootNode.getMemberCount(); i++)
        {
            paneTree.expandPath(new TreePath(
            new Object[] { rootNode, rootNode.getMember(i) }));
        }

        if(pane == null || !selectPane(rootNode,pane))
            selectPane(rootNode,firstPane);

        loadGeometry(this, name);
        int dividerLocation = jsXe.getIntegerProperty(name + ".splitter",300);
        if(dividerLocation != -1)
            splitter.setDividerLocation(dividerLocation);

        // in case saved geometry is too small
        updateSize();

        show();
    } //}}}

    //{{{ selectPane() method
    private boolean selectPane(OptionGroup node, String name)
    {
        return selectPane(node,name,new ArrayList());
    } //}}}

    //{{{ selectPane() method
    private boolean selectPane(OptionGroup node, String name, ArrayList path)
    {
        path.add(node);

        Enumeration enum = node.getMembers();
        while(enum.hasMoreElements())
        {
            Object obj = enum.nextElement();
            if(obj instanceof OptionGroup)
            {
                OptionGroup grp = (OptionGroup)obj;
                if(grp.getName().equals(name))
                {
                    path.add(grp);
                    path.add(grp.getMember(0));
                    TreePath treePath = new TreePath(
                        path.toArray());
                    paneTree.scrollPathToVisible(treePath);
                    paneTree.setSelectionPath(treePath);
                    return true;
                }
                else if(selectPane((OptionGroup)obj,name,path))
                    return true;
            }
            else
            {
                OptionPane pane = (OptionPane)obj;
                if(pane.getName().equals(name))
                {
                    path.add(pane);
                    TreePath treePath = new TreePath(
                        path.toArray());
                    paneTree.scrollPathToVisible(treePath);
                    paneTree.setSelectionPath(treePath);
                    return true;
                }
            }
        }

        path.remove(node);

        return false;
    } //}}}

    //{{{ updateSize() method
    private void updateSize()
    {
        Dimension currentSize = getSize();
        Dimension requestedSize = getPreferredSize();
        Dimension newSize = new Dimension(
            Math.max(currentSize.width,requestedSize.width),
            Math.max(currentSize.height,requestedSize.height)
        );
        if(newSize.width < 300)
            newSize.width = 300;
        if(newSize.height < 200)
            newSize.height = 200;
        setSize(newSize);
        validate();
    } //}}}

    //{{{ loadGeometry()
    /**
     * Loads a windows's geometry from the properties.
     * The geometry is loaded from the <code><i>name</i>.x</code>,
     * <code><i>name</i>.y</code>, <code><i>name</i>.width</code> and
     * <code><i>name</i>.height</code> properties.
     *
     * @param win The window
     * @param name The window name
     */
    public static void loadGeometry(Window win, String name)
    {
        int x, y, width, height;

        Dimension size = win.getSize();
        Dimension screen = win.getToolkit().getScreenSize();

        width = jsXe.getIntegerProperty(name + ".width",size.width);
        height = jsXe.getIntegerProperty(name + ".height",size.height);

        Component parent = win.getParent();
        if(parent == null)
        {
            x = (screen.width - width) / 2;
            y = (screen.height - height) / 2;
        }
        else
        {
            Rectangle bounds = parent.getBounds();
            x = bounds.x + (bounds.width - width) / 2;
            y = bounds.y + (bounds.height - height) / 2;
        }

        x = jsXe.getIntegerProperty(name + ".x",x);
        y = jsXe.getIntegerProperty(name + ".y",y);

        // Make sure the window is displayed in visible region
        Rectangle osbounds = OperatingSystem.getScreenBounds();
        
        if(x < osbounds.x || x+width > osbounds.width)
        {
            if (width > osbounds.width)
                width = osbounds.width;
            x = (osbounds.width - width) / 2;
        }
        if(y < osbounds.y || y+height > osbounds.height)
        {
            if (height >= osbounds.height)
                height = osbounds.height;
            y = (osbounds.height - height) / 2;
        }

        Rectangle desired = new Rectangle(x,y,width,height);
        win.setBounds(desired);

        if((win instanceof Frame) && OperatingSystem.hasJava14())
        {
            int extState = jsXe.getIntegerProperty(name +   ".extendedState", Frame.NORMAL);

            try
            {
                java.lang.reflect.Method meth = Frame.class.getMethod("setExtendedState", new Class[] {int.class});

                meth.invoke(win, new Object[] {new Integer(extState)});
            }
            catch(Exception e) {}
        }
    } //}}}
    
    //{{{ saveGeometry() method
    /**
     * Saves a window's geometry to the properties.
     * The geometry is saved to the <code><i>name</i>.x</code>,
     * <code><i>name</i>.y</code>, <code><i>name</i>.width</code> and
     * <code><i>name</i>.height</code> properties.
     * @param win The window
     * @param name The window name
     */
    public static void saveGeometry(Window win, String name)
    {
        if ((win instanceof Frame) && OperatingSystem.hasJava14()) {
            try {
                java.lang.reflect.Method meth = Frame.class.getMethod("getExtendedState",   new Class[0]);

                Integer extState = (Integer)meth.invoke(win, new Object[0]);

                jsXe.setIntegerProperty(name + ".extendedState", extState.intValue());

                if (extState.intValue() != Frame.NORMAL) {
                    return;
                }
            }
            catch(Exception e) {}
        }

        Rectangle bounds = win.getBounds();
        jsXe.setIntegerProperty(name + ".x",bounds.x);
        jsXe.setIntegerProperty(name + ".y",bounds.y);
        jsXe.setIntegerProperty(name + ".width",bounds.width);
        jsXe.setIntegerProperty(name + ".height",bounds.height);
    } //}}}
    
    //}}}

    //{{{ PaneNameRenderer class
    class PaneNameRenderer extends DefaultTreeCellRenderer
    {
        public PaneNameRenderer()
        {
            paneFont = UIManager.getFont("Tree.font");
            groupFont = paneFont.deriveFont(Font.BOLD);
        }

        public Component getTreeCellRendererComponent(JTree tree,
            Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus)
        {
            super.getTreeCellRendererComponent(tree,value,
                selected,expanded,leaf,row,hasFocus);

            String name = null;
            String label = null;
            if (value instanceof OptionGroup)
            {
                OptionGroup group = ((OptionGroup)value);
                name = group.getName();
                label = group.getTitle();
                this.setFont(groupFont);
            }
            else if (value instanceof OptionPane)
            {
                OptionPane pane = ((OptionPane)value);
                label = pane.getTitle();
                name = pane.getName();
                this.setFont(paneFont);
            }

            if (name == null)
            {
                setText(null);
            }
            else
            {
                if (value instanceof OptionPane) {
                    
                } else {
                    if (value instanceof OptionGroup) {
                        label = ((OptionGroup)value).getTitle();
                    } else {
                        label = value.toString();
                    }
                }

                if (label == null)
                {
                    setText("NO LABEL PROPERTY: " + name);
                }
                else
                {
                    setText(label);
                }
            }

            setIcon(null);

            return this;
        }

        private Font paneFont;
        private Font groupFont;
    } //}}}

    //{{{ OptionTreeModel class
    public class OptionTreeModel implements TreeModel
    {
        public void addTreeModelListener(TreeModelListener l)
        {
            listenerList.add(TreeModelListener.class, l);
        }

        public void removeTreeModelListener(TreeModelListener l)
        {
            listenerList.remove(TreeModelListener.class, l);
        }

        public Object getChild(Object parent, int index)
        {
            if (parent instanceof OptionGroup)
            {
                return ((OptionGroup)parent).getMember(index);
            }
            else
            {
                return null;
            }
        }

        public int getChildCount(Object parent)
        {
            if (parent instanceof OptionGroup)
            {
                return ((OptionGroup)parent).getMemberCount();
            }
            else
            {
                return 0;
            }
        }

        public int getIndexOfChild(Object parent, Object child)
        {
            if (parent instanceof OptionGroup)
            {
                return ((OptionGroup)parent)
                    .getMemberIndex(child);
            }
            else
            {
                return -1;
            }
        }

        public Object getRoot()
        {
            return root;
        }

        public boolean isLeaf(Object node)
        {
            return node instanceof OptionPane;
        }

        public void valueForPathChanged(TreePath path, Object newValue)
        {
            // this model may not be changed by the TableCellEditor
        }

        protected void fireNodesChanged(Object source, Object[] path,
            int[] childIndices, Object[] children)
        {
            Object[] listeners = listenerList.getListenerList();

            TreeModelEvent modelEvent = null;
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
                if (listeners[i] != TreeModelListener.class)
                    continue;

                if (modelEvent == null)
                {
                    modelEvent = new TreeModelEvent(source,
                        path, childIndices, children);
                }

                ((TreeModelListener)listeners[i + 1])
                    .treeNodesChanged(modelEvent);
            }
        }

        protected void fireNodesInserted(Object source, Object[] path,
            int[] childIndices, Object[] children)
        {
            Object[] listeners = listenerList.getListenerList();

            TreeModelEvent modelEvent = null;
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
                if (listeners[i] != TreeModelListener.class)
                    continue;

                if (modelEvent == null)
                {
                    modelEvent = new TreeModelEvent(source,
                        path, childIndices, children);
                }

                ((TreeModelListener)listeners[i + 1])
                    .treeNodesInserted(modelEvent);
            }
        }

        protected void fireNodesRemoved(Object source, Object[] path,
            int[] childIndices, Object[] children)
        {
            Object[] listeners = listenerList.getListenerList();

            TreeModelEvent modelEvent = null;
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
                if (listeners[i] != TreeModelListener.class)
                    continue;

                if (modelEvent == null)
                {
                    modelEvent = new TreeModelEvent(source,
                        path, childIndices, children);
                }

                ((TreeModelListener)listeners[i + 1])
                    .treeNodesRemoved(modelEvent);
            }
        }

        protected void fireTreeStructureChanged(Object source,
            Object[] path, int[] childIndices, Object[] children)
        {
            Object[] listeners = listenerList.getListenerList();

            TreeModelEvent modelEvent = null;
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
                if (listeners[i] != TreeModelListener.class)
                    continue;

                if (modelEvent == null)
                {
                    modelEvent = new TreeModelEvent(source,
                        path, childIndices, children);
                }

                ((TreeModelListener)listeners[i + 1])
                    .treeStructureChanged(modelEvent);
            }
        }

        private OptionGroup root = new OptionGroup(null, null);
        private EventListenerList listenerList = new EventListenerList();
    } //}}}
}
