/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.*;

/**
 * This class makes it easy to drag and drop files from the operating system to
 * a Java program. Any <tt>Component</tt> can be dropped onto, but only
 * <tt>JComponent</tt>s will indicate the drop event with a changed
 * border.
 * <p>
 * To use this class, construct a new <tt>FileDrop</tt> by passing it the target
 * component and a <tt>Listener</tt> to receive notification when file(s) have
 * been dropped. Here is an example:
 * <p>
 * JPanel myPanel = new JPanel();
 * new FileDrop( myPanel, new FileDrop.Listener()
 * {   public void filesDropped( File[] files )
 * {
 * // handle file drop
 * ...
 * }
 * });
 * <p>
 * You can specify the border that will appear when files are being dragged by
 * calling the constructor with a <tt>Border</tt>. Only
 * <tt>JComponent</tt>s will show any indication with a border.
 * <p>
 * You can turn on some debugging features by passing a <tt>PrintStream</tt>
 * object (such as <tt>System.out</tt>) into the full constructor. A
 * <tt>null</tt> value will result in no extra debugging information being
 * output.
 * <p>
 * I'm releasing this code into the Public Domain. Enjoy.
 *
 * <em>Original author: Robert Harder, rharder@usa.net</em>
 * <p>
 * 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
 *
 * @author Robert Harder
 * @author rharder@users.sf.net
 * @version 1.0.1
 */
@SuppressWarnings({"rawtypes", "unused", "unchecked"})
public class FileDrop
{
    private transient Border normalBorder;
    private transient DropTargetListener dropListener;

    /**
     * Discover if the running JVM is modern enough to have drag and drop.
     */
    private static Boolean supportsDnD;

    // Default border color
    private static final Color defaultBorderColor = new Color(0f, 0f, 1f, 0.25f);

    /**
     * Constructs a {@link FileDrop} with a default light-blue border and, if
     * <var>c</var> is a {@link Container}, recursively sets all
     * elements contained within as drop targets, though only the top level
     * container will change borders.
     *
     * @param dropTarget        Component on which files will be dropped.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public FileDrop(Component dropTarget, Listener listener)
    {
        this(null, // Logging stream
            dropTarget, // Drop target
            BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), // Drag border
            true, // Recursive
            listener);
    }

    /**
     * Constructor with a default border and the option to recursively set drop
     * targets. If your component is a <tt>Container</tt>, then each of
     * its children components will also listen for drops, though only the
     * parent will change borders.
     *
     * @param dropTarget         Component on which files will be dropped.
     * @param recursive Recursively set children as drop targets.
     * @param listener  Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public FileDrop(Component dropTarget, boolean recursive, Listener listener)
    {
        this(null, // Logging stream
            dropTarget, // Drop target
            BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), // Drag border
            recursive, // Recursive
            listener);
    }

    /**
     * Constructor with a default border and debugging optionally turned on.
     * With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for the
     * parameter <tt>out</tt> will result in no debugging output.
     *
     * @param loggingStream      PrintStream to record debugging info or null for no debugging.
     * @param dropTarget        Component on which files will be dropped.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public FileDrop(PrintStream loggingStream, Component dropTarget, Listener listener)
    {
        this(loggingStream, // Logging stream
            dropTarget, // Drop target
            BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), false, // Recursive
            listener);
    }

    /**
     * Constructor with a default border, debugging optionally turned on and the
     * option to recursively set drop targets. If your component is a
     * <tt>Container</tt>, then each of its children components will
     * also listen for drops, though only the parent will change borders. With
     * Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for the
     * parameter <tt>out</tt> will result in no debugging output.
     *
     * @param loggingStream       PrintStream to record debugging info or null for no debugging.
     * @param dropTarget         Component on which files will be dropped.
     * @param recursive Recursively set children as drop targets.
     * @param listener  Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public FileDrop(PrintStream loggingStream, Component dropTarget, boolean recursive, Listener listener)
    {
        this(loggingStream, // Logging stream
            dropTarget, // Drop target
            BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), // Drag border
            recursive, // Recursive
            listener);
    }

    /**
     * Constructor with a specified border
     *
     * @param dropTarget          Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param listener   Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public FileDrop(Component dropTarget, Border dragBorder, Listener listener)
    {
        this(null, // Logging stream
            dropTarget, // Drop target
            dragBorder, // Drag border
            false, // Recursive
            listener);
    }

    /**
     * Constructor with a specified border and the option to recursively set
     * drop targets. If your component is a <tt>Container</tt>, then
     * each of its children components will also listen for drops, though only
     * the parent will change borders.
     *
     * @param dropTarget          Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param recursive  Recursively set children as drop targets.
     * @param listener   Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public FileDrop(Component dropTarget, Border dragBorder, boolean recursive, Listener listener)
    {
        this(null, dropTarget, dragBorder, recursive, listener);
    } // end constructor

    /**
     * Constructor with a specified border and debugging optionally turned on.
     * With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for the
     * parameter <tt>out</tt> will result in no debugging output.
     *
     * @param loggingStream        PrintStream to record debugging info or null for no debugging.
     * @param dropTarget          Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param listener   Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public FileDrop(PrintStream loggingStream, Component dropTarget, Border dragBorder, Listener listener)
    {
        this(loggingStream, // Logging stream
            dropTarget, // Drop target
            dragBorder, // Drag border
            false, // Recursive
            listener);
    }

    /**
     * Full constructor with a specified border and debugging optionally turned
     * on. With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for the
     * parameter <tt>out</tt> will result in no debugging output.
     *
     * @param loggingStream        PrintStream to record debugging info or null for no debugging.
     * @param dropTarget          Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param recursive  Recursively set children as drop targets.
     * @param listener   Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public FileDrop(PrintStream loggingStream, Component dropTarget, Border dragBorder, boolean recursive, Listener listener)
    {
        // Make a drop listener
        if (supportsDnD())
        {
            dropListener = new DropTargetListener()
            {
                @Override
                public void dragEnter(DropTargetDragEvent evt)
                {
                    log(loggingStream, "FileDrop: dragEnter event.");

                    // Is this an acceptable drag event?
                    if (isDragOk(loggingStream, evt))
                    {
                        // If it's a Swing component, set its border
                        if (dropTarget instanceof JComponent)
                        {
                            final JComponent jc = (JComponent) dropTarget;
                            normalBorder = jc.getBorder();
                            log(loggingStream, "FileDrop: normal border saved.");
                            jc.setBorder(dragBorder);
                            log(loggingStream, "FileDrop: drag border set.");
                        }

                        // Acknowledge that it's okay to enter
                        //evt.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                        evt.acceptDrag(DnDConstants.ACTION_COPY);
                        log(loggingStream, "FileDrop: event accepted.");
                    }
                    else
                    {
                        // Reject the drag event
                        evt.rejectDrag();
                        log(loggingStream, "FileDrop: event rejected.");
                    }
                }

                @Override
                public void dragOver(DropTargetDragEvent evt)
                { // This is called continually as long as the mouse is over the drag target.
                }

                @Override
                public void drop(DropTargetDropEvent evt)
                {
                    log(loggingStream, "FileDrop: drop event.");
                    try
                    { // Get whatever was dropped
                        final Transferable tr = evt.getTransferable();

                        // Is it a file list?
                        if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
                        {
                            // Say we'll take it. evt.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
                            evt.acceptDrop(DnDConstants.ACTION_COPY);
                            log(loggingStream, "FileDrop: file list accepted.");

                            // Get a useful list
                            final java.util.List fileList = (java.util.List) tr.getTransferData(DataFlavor.javaFileListFlavor);
                            final java.util.Iterator iterator = fileList.iterator();

                            // Convert list to array
                            final File[] filesTemp = new File[fileList.size()];
                            fileList.toArray(filesTemp);

                            // Alert listener to drop.
                            if (listener != null)
                                listener.filesDropped(filesTemp);

                            // Mark that drop is completed.
                            evt.getDropTargetContext().dropComplete(true);
                            log(loggingStream, "FileDrop: drop complete.");
                        }
                        else
                        {
                            final DataFlavor[] flavors = tr.getTransferDataFlavors();
                            boolean handled = false;

                            for (DataFlavor flavor : flavors)
                            {
                                if (flavor.isRepresentationClassReader())
                                {
                                    // Say we'll take it. evt.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
                                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                                    log(loggingStream, "FileDrop: reader accepted.");

                                    final Reader reader = flavor.getReaderForText(tr);

                                    final BufferedReader br = new BufferedReader(reader);

                                    if (listener != null)
                                        listener.filesDropped(createFileArray(br, loggingStream));

                                    // Mark that drop is completed.
                                    evt.getDropTargetContext().dropComplete(true);
                                    log(loggingStream, "FileDrop: drop complete.");
                                    handled = true;
                                    break;
                                }
                            }

                            if (!handled)
                            {
                                log(loggingStream, "FileDrop: not a file list or reader - abort.");
                                evt.rejectDrop();
                            }
                        }
                    }
                    catch (IOException io)
                    {
                        log(loggingStream, "FileDrop: IOException - abort:");
                        BytecodeViewer.handleException(io);
                        evt.rejectDrop();
                    }
                    catch (UnsupportedFlavorException ufe)
                    {
                        log(loggingStream, "FileDrop: UnsupportedFlavorException - abort:");
                        BytecodeViewer.handleException(ufe);
                        evt.rejectDrop();
                    }
                    finally
                    {
                        // If it's a Swing component, reset its border
                        if (dropTarget instanceof JComponent)
                        {
                            final JComponent jc = (JComponent) dropTarget;
                            jc.setBorder(normalBorder);
                            log(loggingStream, "FileDrop: normal border restored.");
                        }
                    }
                }

                @Override
                public void dragExit(DropTargetEvent evt)
                {
                    log(loggingStream, "FileDrop: dragExit event.");

                    // If it's a Swing component, reset its border
                    if (dropTarget instanceof JComponent)
                    {
                        final JComponent jc = (JComponent) dropTarget;
                        jc.setBorder(normalBorder);
                        log(loggingStream, "FileDrop: normal border restored.");
                    }
                }

                @Override
                public void dropActionChanged(final DropTargetDragEvent evt)
                {
                    log(loggingStream, "FileDrop: dropActionChanged event.");

                    // Is this an acceptable drag event?
                    if (isDragOk(loggingStream, evt))
                    {
                        //evt.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                        evt.acceptDrag(DnDConstants.ACTION_COPY);
                        log(loggingStream, "FileDrop: event accepted.");
                    }
                    else
                    {
                        evt.rejectDrag();
                        log(loggingStream, "FileDrop: event rejected.");
                    }
                }
            };

            // Make the component (and possibly children) drop targets
            makeDropTarget(loggingStream, dropTarget, recursive);
        }
        else
        {
            log(loggingStream, "FileDrop: Drag and drop is not supported with this JVM");
        }
    }

    private static boolean supportsDnD()
    {
        if (supportsDnD == null)
        {
            boolean support;

            try
            {
                final Class arbitraryDndClass = Class.forName("java.awt.dnd.DnDConstants");
                support = true;
            }
            catch (Throwable t)
            {
                support = false;
            }

            supportsDnD = support;
        }
        return supportsDnD;
    }

    private static final String ZERO_CHAR_STRING = "" + (char) 0;

    private static File[] createFileArray(BufferedReader bReader, PrintStream out)
    {
        try
        {
            final java.util.List list = new java.util.ArrayList();
            java.lang.String line;
            while ((line = bReader.readLine()) != null)
            {
                try
                {
                    // kde seems to append a 0 char to the end of the reader
                    if (ZERO_CHAR_STRING.equals(line))
                        continue;

                    final File file = new File(new java.net.URI(line));
                    list.add(file);
                }
                catch (Exception ex)
                {
                    log(out, "Error with " + line + ": " + ex.getMessage());
                }
            }

            return (File[]) list.toArray(new File[0]);
        }
        catch (IOException ex)
        {
            log(out, "FileDrop: IOException");
        }
        return new File[0];
    }

    private void makeDropTarget(PrintStream out, Component c, boolean recursive)
    {
        final DropTarget dt = new DropTarget();

        try
        {
            dt.addDropTargetListener(dropListener);
        }
        catch (java.util.TooManyListenersException e)
        {
            BytecodeViewer.handleException(e);
            log(out, "FileDrop: Drop will not work due to previous error. Do you have another listener attached?");
        }

        // Listen for hierarchy changes and remove the drop target when the parent gets cleared out.
        c.addHierarchyListener(evt ->
        {
            log(out, "FileDrop: Hierarchy changed.");
            final Component parent = c.getParent();

            if (parent == null)
            {
                c.setDropTarget(null);
                log(out, "FileDrop: Drop target cleared from component.");
            }
            else
            {
                new DropTarget(c, dropListener);
                log(out, "FileDrop: Drop target added to component.");
            }
        });

        if (c.getParent() != null)
            new DropTarget(c, dropListener);

        if (recursive && (c instanceof Container))
        {
            // Get the container
            final Container cont = (Container) c;

            // Get its components
            final Component[] comps = cont.getComponents();

            // Set its components as listeners also
            for (Component comp : comps)
            {
                makeDropTarget(out, comp, true);
            }
        }
    }

    /**
     * Determine if the dragged data is a file list.
     */
    private boolean isDragOk(PrintStream out, DropTargetDragEvent evt)
    {
        boolean ok = false;

        // Get data flavors being dragged
        final DataFlavor[] flavors = evt.getCurrentDataFlavors();

        // See if any of the flavors are a file list
        int i = 0;
        while (!ok && i < flavors.length)
        {
            // Is the flavor a file list?
            final DataFlavor curFlavor = flavors[i];

            if (curFlavor.equals(DataFlavor.javaFileListFlavor) || curFlavor.isRepresentationClassReader())
                ok = true;
            i++;
        }

        // If logging is enabled, show data flavors
        if (out != null)
        {
            if (flavors.length == 0)
                log(out, "FileDrop: no data flavors.");

            for (i = 0; i < flavors.length; i++)
            {
                log(out, flavors[i].toString());
            }
        }

        return ok;
    }

    /**
     * Outputs <tt>message</tt> to <tt>out</tt> if it's not null.
     */
    private static void log(PrintStream out, String message)
    {
        // Log message if requested
        if (out != null)
            out.println(message);
    }

    /**
     * Removes the drag-and-drop hooks from the component and optionally from
     * the all children. You should call this if you add and remove components
     * after you've set up the drag-and-drop. This will recursively unregister
     * all components contained within <var>c</var> if <var>c</var> is a
     * {@link Container}.
     *
     * @param c The component to unregister as a drop target
     * @since 1.0
     */
    public static boolean remove(Component c)
    {
        return remove(null, c, true);
    }

    /**
     * Removes the drag-and-drop hooks from the component and optionally from
     * the all children. You should call this if you add and remove components
     * after you've set up the drag-and-drop.
     *
     * @param out       Optional {@link PrintStream} for logging drag and drop
     *                  messages
     * @param c         The component to unregister
     * @param recursive Recursively unregister components within a container
     * @since 1.0
     */
    public static boolean remove(PrintStream out, Component c, boolean recursive)
    {
        // Make sure we support
        if (supportsDnD())
        {
            log(out, "FileDrop: Removing drag-and-drop hooks.");
            c.setDropTarget(null);

            if (recursive && (c instanceof Container))
            {
                final Component[] comps = ((Container) c).getComponents();

                for (Component comp : comps)
                {
                    remove(out, comp, true);
                }

                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    /* ******** I N N E R I N T E R F A C E L I S T E N E R ******** */

    /**
     * Implement this inner interface to listen for when files are dropped. For
     * example your class declaration may begin like this: <code><pre>
     *      public class MyClass implements FileDrop.Listener
     *      ...
     *      public void filesDropped( File[] files )
     *      {
     *          ...
     *      }
     *      ...
     * </pre></code>
     *
     * @since 1.1
     */
    public interface Listener
    {

        /**
         * This method is called when files have been successfully dropped.
         *
         * @param files An array of <tt>File</tt>s that were dropped.
         * @since 1.0
         */
        void filesDropped(File[] files);

    }

    /* ******** I N N E R C L A S S ******** */

    /**
     * This is the event that is passed to the
     * FileDropListener#filesDropped filesDropped(...) method in your
     * FileDropListener when files are dropped onto a registered drop
     * target.
     * <p>
     * <p>
     * I'm releasing this code into the Public Domain. Enjoy.
     * </p>
     *
     * @author Robert Harder
     * @author rob@iharder.net
     * @version 1.2
     */
    public static class Event extends java.util.EventObject
    {

        private static final long serialVersionUID = -2175361562828864378L;
        private final File[] files;

        /**
         * Constructs an {@link Event} with the array of files that were dropped
         * and the {@link FileDrop} that initiated the event.
         *
         * @param files  The array of files that were dropped
         * @param source The event source
         * @since 1.1
         */
        public Event(File[] files, Object source)
        {
            super(source);
            this.files = files;
        }

        /**
         * Returns an array of files that were dropped on a registered drop
         * target.
         *
         * @return array of files that were dropped
         * @since 1.1
         */
        public File[] getFiles()
        {
            return files;
        }

    }

    /* ******** I N N E R C L A S S ******** */

    /**
     * At last an easy way to encapsulate your custom objects for dragging and
     * dropping in your Java programs! When you need to create a
     * {@link Transferable} object, use this class to wrap
     * your object. For example:
     * <p>
     * <pre>
     * <code>
     *      ...
     *      MyCoolClass myObj = new MyCoolClass();
     *      Transferable xfer = new TransferableObject( myObj );
     *      ...
     * </code>
     * </pre>
     * <p>
     * Or if you need to know when the data was actually dropped, like when
     * you're moving data out of a list, say, you can use the
     * {@link TransferableObject.Fetcher} inner class to return your object Just
     * in Time. For example:
     * <p>
     * <pre>
     * <code>
     *      ...
     *      final MyCoolClass myObj = new MyCoolClass();
     *
     *      TransferableObject.Fetcher fetcher = new TransferableObject.Fetcher()
     *      {   public Object getObject(){ return myObj; }
     *      };
     *
     *      Transferable xfer = new TransferableObject( fetcher );
     *      ...
     * </code>
     * </pre>
     * <p>
     * The {@link DataFlavor} associated with
     * {@link TransferableObject} has the representation class
     * <tt>net.iharder.TransferableObject.class</tt> and MIME type
     * <tt>application/x-net.iharder.TransferableObject</tt>. This data
     * flavor is accessible via the static {@link #DATA_FLAVOR} property.
     * <p>
     * <p>
     * <p>
     * I'm releasing this code into the Public Domain. Enjoy.
     * </p>
     *
     * @author Robert Harder
     * @author rob@iharder.net
     * @version 1.2
     */
    public static class TransferableObject implements Transferable
    {
        /**
         * The MIME type for {@link #DATA_FLAVOR} is
         * <tt>application/x-net.iharder.TransferableObject</tt>.
         *
         * @since 1.1
         */
        public final static String MIME_TYPE = "application/x-net.iharder.dnd.TransferableObject";

        /**
         * The default {@link DataFlavor} for
         * {@link TransferableObject} has the representation class
         * <tt>net.iharder.TransferableObject.class</tt> and the MIME type
         * <tt>application/x-net.iharder.TransferableObject</tt>.
         *
         * @since 1.1
         */
        public final static DataFlavor DATA_FLAVOR = new DataFlavor(FileDrop.TransferableObject.class, MIME_TYPE);

        private Fetcher fetcher;
        private Object data;

        private DataFlavor customFlavor;

        /**
         * Creates a new {@link TransferableObject} that wraps <var>data</var>.
         * Along with the {@link #DATA_FLAVOR} associated with this class, this
         * creates a custom data flavor with a representation class determined
         * from <code>data.getClass()</code> and the MIME type
         * <tt>application/x-net.iharder.TransferableObject</tt>.
         *
         * @param data The data to transfer
         * @since 1.1
         */
        public TransferableObject(Object data)
        {
            this.data = data;
            this.customFlavor = new DataFlavor(data.getClass(), MIME_TYPE);
        }

        /**
         * Creates a new {@link TransferableObject} that will return the object
         * that is returned by <var>fetcher</var>. No custom data flavor is set
         * other than the default {@link #DATA_FLAVOR}.
         *
         * @param fetcher The {@link Fetcher} that will return the data object
         * @see Fetcher
         * @since 1.1
         */
        public TransferableObject(Fetcher fetcher)
        {
            this.fetcher = fetcher;
        }

        /**
         * Creates a new {@link TransferableObject} that will return the object
         * that is returned by <var>fetcher</var>. Along with the
         * {@link #DATA_FLAVOR} associated with this class, this creates a
         * custom data flavor with a representation class <var>dataClass</var>
         * and the MIME type
         * <tt>application/x-net.iharder.TransferableObject</tt>.
         *
         * @param dataClass The {@link java.lang.Class} to use in the custom data
         *                  flavor
         * @param fetcher   The {@link Fetcher} that will return the data object
         * @see Fetcher
         * @since 1.1
         */
        public TransferableObject(Class dataClass, Fetcher fetcher)
        {
            this.fetcher = fetcher;
            this.customFlavor = new DataFlavor(dataClass, MIME_TYPE);
        }

        /**
         * Returns the custom {@link DataFlavor}
         * associated with the encapsulated object or <tt>null</tt> if the
         * {@link Fetcher} constructor was used without passing a
         * {@link java.lang.Class}.
         *
         * @return The custom data flavor for the encapsulated object
         * @since 1.1
         */
        public DataFlavor getCustomDataFlavor()
        {
            return customFlavor;
        }

        /* ******** T R A N S F E R A B L E M E T H O D S ******** */

        /**
         * Returns a two- or three-element array containing first the custom
         * data flavor, if one was created in the constructors, second the
         * default {@link #DATA_FLAVOR} associated with
         * {@link TransferableObject}, and third the
         * DataFlavor.stringFlavor.
         *
         * @return An array of supported data flavors
         * @since 1.1
         */
        @Override
        public DataFlavor[] getTransferDataFlavors()
        {
            if (customFlavor != null)
                return new DataFlavor[]{customFlavor, DATA_FLAVOR, DataFlavor.stringFlavor}; // end flavors array
            else
                return new DataFlavor[]{DATA_FLAVOR, DataFlavor.stringFlavor}; // end flavors array
        }

        /**
         * Returns the data encapsulated in this {@link TransferableObject}. If
         * the {@link Fetcher} constructor was used, then this is when the
         * {@link Fetcher#getObject getObject()} method will be called. If the
         * requested data flavor is not supported, then the
         * {@link Fetcher#getObject getObject()} method will not be called.
         *
         * @param flavor The data flavor for the data to return
         * @return The dropped data
         * @since 1.1
         */
        @Override
        public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException
        {
            // Native object
            if (flavor.equals(DATA_FLAVOR))
                return fetcher == null ? data : fetcher.getObject();

            // String
            if (flavor.equals(DataFlavor.stringFlavor))
                return fetcher == null ? data.toString() : fetcher.getObject().toString();

            // We can't do anything else
            throw new UnsupportedFlavorException(flavor);
        }

        /**
         * Returns <tt>true</tt> if <var>flavor</var> is one of the supported
         * flavors. Flavors are supported using the <code>equals(...)</code>
         * method.
         *
         * @param flavor The data flavor to check
         * @return Whether or not the flavor is supported
         * @since 1.1
         */
        @Override
        public boolean isDataFlavorSupported(final DataFlavor flavor)
        {
            // Native object
            if (flavor.equals(DATA_FLAVOR))
                return true;

            // String
            return flavor.equals(DataFlavor.stringFlavor);

            // We can't do anything else
        }

        /* ******** I N N E R I N T E R F A C E F E T C H E R ******** */

        /**
         * Instead of passing your data directly to the
         * {@link TransferableObject} constructor, you may want to know exactly
         * when your data was received in case you need to remove it from its
         * source (or do anyting else to it). When the {@link #getTransferData
         * getTransferData(...)} method is called on the
         * {@link TransferableObject}, the {@link Fetcher}'s {@link #getObject
         * getObject()} method will be called.
         *
         * @author Robert Harder
         * @version 1.1
         * @copyright 2001
         * @since 1.1
         */
        public interface Fetcher
        {
            /**
             * Return the object being encapsulated in the
             * {@link TransferableObject}.
             *
             * @return The dropped object
             * @since 1.1
             */
            Object getObject();
        }
    }
}
