package edu.gatech.hava.gui.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;

import javax.swing.JApplet;

import edu.gatech.hava.engine.HEngine;
import edu.gatech.hava.gui.HPanel;

/**
 * Applet version of the Hava editor.
 */
public class HApplet extends JApplet {

    private static final long serialVersionUID = 1;

    /**
     * The panel where the work appears.
     */
    private HPanel hPanel;

    /**
     * The class that does all the work.
     */
    private HEngine engine;

    /** {@inheritDoc} */
    public void init() {

        // Specify the location of imports for which a path is not provided
        String defaultImportPath = getDocumentBase().toString();

        // Instantiate an engine
        engine = new HEngine(defaultImportPath);

        // Instantiate a panel
        hPanel = new HPanel(engine);
        hPanel.setBackground(Color.LIGHT_GRAY);
        hPanel.getLeftComponent().setBackground(Color.WHITE);
        hPanel.getRightComponent().setBackground(Color.WHITE);

        // Add the panel to the applet
        setLayout(new BorderLayout());
        add(hPanel);

        /* The remainder of this method attempts to load a problem,
         * specified as a parameter. We use this feature in the online tutorial. */
        String filename = getParameter("source");
        if (filename == null) {
            return;
        }

        if (!filename.toLowerCase().endsWith(".hava")) {
            filename += ".hava";
        }

        try {
            URL url = new URL(getDocumentBase(), filename);
            BufferedReader r = new BufferedReader(new InputStreamReader(url
                    .openStream()));
            StringWriter w = new StringWriter();
            String line;
            while ((line = r.readLine()) != null) {
                w.append(line);
                w.append("\n");
            }
            r.close();
            hPanel.setProblem(w.toString());
        } catch (final Exception x) {
            hPanel.setProblem("HTML error:  Cannot find source file " + filename);
        }

    }

    /** {@inheritDoc} */
    public void start() {

        hPanel.setDividerLocation(0.6);
        hPanel.getProblemPanel().getTextArea().requestFocus();

    }

    /**
     * {@inheritDoc}
     *
     * This turns out to be an issue for certain browsers.
     * They don't like it when you leave a thread running.
     */
    public void stop() {

        engine.abort();
        engine.reset();

    }

    /** {@inheritDoc} */
    public String[][] getParameterInfo() {

        return new String[][] {{"source", "hava", "preloaded source filename (optional)"}};

    };

}
