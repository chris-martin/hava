package edu.gatech.hava.gui.standalone;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

import edu.gatech.hava.engine.HEngine;
import edu.gatech.hava.gui.HPanel;
import edu.gatech.hava.gui.standalone.icon.HIcon;

/**
 * Hava editor as a standalone GUI application.
 */
public class HGUI {

    /**
     * Main method - instantiates a new HGUI.
     *
     * @param args command-line arguments to the program - not used
     */
    public static void main(final String[] args) {

        new HGUI();

    }

    private static final String FRAME_TITLE = "Hava Demo - Beta 4";

    private String originalProblem = "";
    private HPanel hPanel;
    public JFrame frame;
    private JFileChooser fileChooser;
    private boolean isMac;
    private CustomUndoManager undoManager;
    private HEngine engine;

    private final Action newAction = new AbstractAction("New") {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            newFile();
        }

    };

    private final Action openAction = new AbstractAction("Open") {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            openFile();
        }

    };

    private final Action saveAction = new AbstractAction("Save") {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            save();
        }

    };

    private final Action saveAsAction = new AbstractAction("SaveAs") {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            saveAs();
        }

    };

    private final Action cutAction = new AbstractAction("Cut") {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            cut();
        }

    };

    private final Action copyAction = new AbstractAction("Copy") {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            copy();
        }

    };

    private final Action pasteAction = new AbstractAction("Paste") {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            paste();
        }

    };

    private final Action importAction = new AbstractAction("Import") {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            doImport();
        }

    };

    private final Action undoAction = new AbstractAction("Undo") {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            undoManager.undo();
        }

    };

    private final Action redoAction = new AbstractAction("Redo") {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            undoManager.redo();
        }

    };

    /*
    private final Action formatAction = new AbstractAction("Format") {

        public void actionPerformed(final ActionEvent e) {
            doFormat();
        }

    };
    */

    private final Action exportAction = new AbstractAction("Export") {

        private static final long serialVersionUID = 1L;

        public void actionPerformed(final ActionEvent e) {
            export();
        }

    };

    private static class HavaFileFilter extends FileFilter {

        @Override
        public boolean accept(final File f) {

            return f.isDirectory()
                || f.getName().toLowerCase().endsWith(".hava");

        }

        @Override
        public String getDescription() {

            return "Hava files";

        }

    }

    public HGUI() {

        init();

    }

    private void init() {

        String cwd = System.getProperty("user.dir") + File.separatorChar;
        engine = new HEngine(cwd);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception x) {
            ;
        }

        isMac = System.getProperty("os.name").startsWith("Mac ");

        hPanel = new HPanel(engine);

        undoManager = new CustomUndoManager(
                hPanel.getProblemPanel().getTextArea());

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileFilter(new HavaFileFilter());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(new LineBorder(Color.LIGHT_GRAY));

        toolBar.addSeparator(new java.awt.Dimension(5, 2));
        addButton(toolBar, "New", HIcon.NEW, KeyEvent.VK_N, newAction);
        addButton(toolBar, "Open...", HIcon.OPEN, KeyEvent.VK_O, openAction);
        toolBar.addSeparator();
        addButton(toolBar, "Save", HIcon.SAVE, KeyEvent.VK_S, saveAction);
        addButton(toolBar, "Save As...", HIcon.SAVE_AS, 0, saveAsAction);
        toolBar.addSeparator();
        addButton(toolBar, "Cut", HIcon.CUT, KeyEvent.VK_X, cutAction);
        addButton(toolBar, "Copy", HIcon.COPY, KeyEvent.VK_C, copyAction);
        addButton(toolBar, "Paste", HIcon.PASTE, KeyEvent.VK_V, pasteAction);
        addButton(toolBar, "Enter Data", HIcon.IMPORT, KeyEvent.VK_E, importAction);
        toolBar.addSeparator();
        addButton(toolBar, "Undo", HIcon.UNDO, KeyEvent.VK_Z, undoAction);
        addButton(toolBar, "Redo", HIcon.REDO, KeyEvent.VK_Y, redoAction);
        toolBar.addSeparator();
        //addButton(toolBar, "Format", "Format16.gif", KeyEvent.VK_Q, formatAction);
        //toolBar.addSeparator();
        addButton(toolBar, "Copy Solution to Clipboard", HIcon.REPORT, KeyEvent.VK_R, exportAction);

        toolBar.addSeparator();

        frame = new JFrame();
        frame.setTitle(FRAME_TITLE);
        frame.setIconImage(new ImageIcon(HIcon.HAVA).getImage());
        frame.setLayout(new BorderLayout());
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(hPanel);

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                if (okToDiscard()) {
                    System.exit(0);
                }
            }
        });

        frame.setVisible(true);
        hPanel.setDividerLocation(0.6);
        hPanel.getProblemPanel().getTextArea().requestFocus();

    }

    private void newFile() {

        if (okToDiscard()) {
            engine.reset("");
        }

    }

    private void openFile() {

        if (!okToDiscard()) {
            return;
        }
        if (fileChooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fileChooser.getSelectedFile();
        if (!(file.getName().toLowerCase().endsWith(".hava"))) {
            file = new File(file.getPath() + ".hava");
        }

        try {

            BufferedReader r = new BufferedReader(
                    new FileReader(file));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = r.readLine()) != null) {
                sb.append(line + "\n");
            }
            r.close();

            originalProblem = sb.toString();
            hPanel.getProblemPanel().getTextArea().setText(originalProblem);
            hPanel.getProblemPanel().getTextArea().setCaretPosition(0);
            frame.setTitle(FRAME_TITLE + " - " + file.getName());
            engine.reset(file.getAbsolutePath());
            undoManager.reset();

        } catch (final IOException x) {
            hPanel.getResultPanel().getTextArea().setText("Cannot open this file.");
        }

    }

    private void save() {

        File file = fileChooser.getSelectedFile();
        if (file == null) {
            if (fileChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            file = fileChooser.getSelectedFile();
        }

        if (!(file.getName().toLowerCase().endsWith(".hava"))) {
            file = new File(file.getPath() + ".hava");
        }

        try {

            Writer w = new FileWriter(file);
            originalProblem = hPanel.getProblemPanel().getTextArea().getText();
            w.write(originalProblem);
            w.close();

            frame.setTitle(FRAME_TITLE + " - " + file.getName());
            engine.reset(file.getAbsolutePath());

        } catch (final IOException x) {
            hPanel.getResultPanel().getTextArea().setText("Cannot save this file.");
        }

    }

    private void saveAs() {

        if (fileChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fileChooser.getSelectedFile();
        if (!(file.getName().toLowerCase().endsWith(".hava"))) {
            file = new File(file.getPath() + ".hava");
        }

        try {
            Writer w = new FileWriter(file);
            originalProblem = hPanel.getProblemPanel().getTextArea().getText();
            w.write(originalProblem);
            w.close();

            frame.setTitle(FRAME_TITLE + " - " + file.getName());
            engine.reset(file.getAbsolutePath());
        } catch (final IOException x) {
            hPanel.getResultPanel().getTextArea()
                    .setText("Cannot save this file.");
        }

    }

    private void cut() {

        Object x = frame.getFocusOwner();

        if (x instanceof JTextArea) {
            ((JTextArea) x).cut();
        }

    }

    private void copy() {

        Object x = frame.getFocusOwner();

        if (x instanceof JTextArea) {
            ((JTextArea) x).copy();
        }

    }

    private void paste() {

        Object x = frame.getFocusOwner();

        if (x instanceof JTextArea) {
            ((JTextArea) x).paste();
        }

    }

    private void export() {

        if (hPanel.getResultPanel().getTextArea().getText().length() > 0) {

            int i = hPanel.getResultPanel().getTextArea().getSelectionStart();
            int n = hPanel.getResultPanel().getTextArea().getSelectionEnd();

            hPanel.getResultPanel().getTextArea().selectAll();
            hPanel.getResultPanel().getTextArea().copy();
            hPanel.getResultPanel().getTextArea().setSelectionStart(i);
            hPanel.getResultPanel().getTextArea().setSelectionEnd(n);

        }

    }

    private void addButton(final JToolBar toolBar, final String tipString,
                           final URL imageFilename, final int acceleratorKeyEvent,
                           final Action action) {

        String tipPrefix = isMac ? "\u2318" : "Ctrl-";
        int mask = isMac ? InputEvent.META_MASK : InputEvent.CTRL_MASK;

        JButton button = new JButton();

        String s = tipString;
        if (acceleratorKeyEvent != 0) {
            s += " (" + tipPrefix
                    + ((char) ('A' + acceleratorKeyEvent - KeyEvent.VK_A))
                    + ")";
        }
        button.setToolTipText(s);

        button.setIcon(new ImageIcon(imageFilename, tipString));
        button.addActionListener(action);
        button.setFocusable(false);

        if (acceleratorKeyEvent != 0) {
            KeyStroke ks = KeyStroke.getKeyStroke(acceleratorKeyEvent, mask);

            InputMap imap = button
                    .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            imap.put(ks, action.getValue(Action.NAME));

            ActionMap amap = button.getActionMap();
            amap.put(action.getValue(Action.NAME), action);
        }

        toolBar.add(button);

    }

    private boolean okToDiscard() {

        boolean result;

        if (originalProblem.equals(hPanel.getProblemPanel().getTextArea().getText())) {
            result = true;
        } else {
            result = JOptionPane.showConfirmDialog(frame,
                    "Discard current problem?", "Hava",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        }

        if (result) {
            hPanel.getProblemPanel().getTextArea().setText("");
            hPanel.getResultPanel().getTextArea().setText("");
            originalProblem = "";

            frame.setTitle(FRAME_TITLE);
            File path = fileChooser.getCurrentDirectory();
            fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(path);
            fileChooser.setFileFilter(new HavaFileFilter());

            undoManager.reset();
        }

        return result;

    }

    private void doImport() {

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Transferable t = toolkit.getSystemClipboard().getContents(null);
        String s = null;

        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                s = (String) t.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (final UnsupportedFlavorException e) {
            ;
        } catch (final IOException e) {
            ;
        }

        if (s == null) {
            return;
        }

        if (s.indexOf('\t') < 0) {
            return;
        }

        try {
            BufferedReader r = new BufferedReader(new StringReader(s));
            ArrayList<String> a = new ArrayList<String>();
            String line;
            while ((line = r.readLine()) != null) {
                String[] b = line.split("\t");
                for (int i = 0; i < b.length; i++) {
                    b[i] = b[i].trim();
                    if (b[i].length() == 0) {
                        b[i] = "BLANK";
                    } else {
                        try {
                            Double.parseDouble(b[i]);
                        } catch (final NumberFormatException x) {
                            b[i] = "ERROR";
                        }
                    }
                }

                switch (b.length) {
                case 0:
                    line = "()";
                    break;
                case 1:
                    line = "collect(" + b[0] + ")";
                    break;
                default:
                    line = "(" + b[0];
                    for (int i = 1; i < b.length; i++) {
                        line += ", " + b[i];
                    }
                    line += ")";
                    break;
                }

                a.add(line);
            }

            StringWriter w = new StringWriter();
            w.append("\ntable data = ");
            if (a.size() == 1) {
                w.append("collect");
            }
            w.append("(\n");
            for (int i = 0; i < a.size() - 1; i++) {
                w.append("  " + a.get(i) + ",\n");
            }
            w.append("  " + a.get(a.size() - 1) + "\n);\n\n");
            s = w.toString();
        } catch (final IOException x) {
            ;
        }

        StringSelection ss = new StringSelection(s);
        toolkit.getSystemClipboard().setContents(ss, null);

        hPanel.getProblemPanel().getTextArea().paste();

    }

    /*
    private void doFormat() {

        JTextArea a = hPanel.problemTextArea;

        String s1 = a.getText();
        int p1a = a.getSelectionStart();
        int p1b = a.getSelectionEnd();

        int displayWidth = a.getWidth();
        int charWidth = a.getGraphics().getFontMetrics(a.getFont()).charWidth(' ');
        int n = Math.max(20, displayWidth / charWidth - 3);

        Formatter f = new Formatter(s1, n);
        String s2 = f.toString();
        if (s1.equals(s2)) {
            return;
        }

        int i1 = 0;
        int i2 = 0;
        while (i1 < p1a) {
            char c1 = s1.charAt(i1);
            char c2 = s2.charAt(i2);

            boolean b1 = c1 <= ' ';
            boolean b2 = c2 <= ' ';

            if (b1 || !b2) {
                i1++;
            }

            if (b2 || !b1) {
                i2++;
            }
        }
        int p2a = i2;

        i1 = s1.length();
        i2 = s2.length();
        while (i1 > p1b) {
            char c1 = s1.charAt(i1 - 1);
            char c2 = s2.charAt(i2 - 1);

            boolean b1 = c1 <= ' ';
            boolean b2 = c2 <= ' ';

            if (b1 || !b2) {
                i1--;
            }

            if (b2 || !b1) {
                i2--;
            }
        }
        int p2b = Math.max(i2, p2a);

        if (p1a != p1b) {
            s2 = s1.substring(0, p1a) + s2.substring(p2a, p2b)
                    + s1.substring(p1b);
            p2a = p1a;
            p2b = s2.length() - (s1.length() - p1b);
        } else {
            p2b = p2a;
        }

        a.setText(s2);
        a.setSelectionStart(p2a);
        a.setSelectionEnd(p2b);

    }
    */

    JFrame getFrame() {

        return frame;

    }

}
