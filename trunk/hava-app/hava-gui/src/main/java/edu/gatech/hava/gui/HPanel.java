package edu.gatech.hava.gui;

import java.awt.Font;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DecimalFormat;

import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import edu.gatech.hava.debug.trace.TraceDebugListener;
import edu.gatech.hava.engine.HEngine;
import edu.gatech.hava.engine.HException;

/**
 * Swing demonstration panel that appears in both the applet and GUI demos.
 */
public class HPanel extends JSplitPane {

    private static final long serialVersionUID = 1L;

    /**
     * Whitespace border of the two JTextAreas.
     */
    private static final int GAP = 10;

    /**
     * Interval between monitor checks (in msec).
     */
    private static final int TICK = 250;

    /**
     * Delay before report or first progress update (in ticks).
     */
    private static final int HOLD = 6;

    /**
     * Delay between subsequent progress updates (in ticks).
     */
    private static final int UPDATE = 2;

    private ProblemPanel problemPanel;
    private ResultPanel resultPanel;

    /**
     * Current problem (compared to controls to determine if a new problem has been provided).
     */
    private String currentProblem;

    private boolean currentIsTrace;
    private int currentCaretPosition = -1;

    /**
     * The engine that does all the work.
     */
    private HEngine engine;

    private long startTime;

    // The two threads
    private MonitorThread monitorThread;
    private WorkThread workThread;

    /**
     * Interrupts and terminate both threads.
     */
    void abort() {

        if (monitorThread != null) {
            monitorThread.abort();
        }

        if (workThread != null) {
            workThread.abort();
        }

    }

    /**
     * Constructor.
     *
     * @param engine the engine used to interpret hava code
     */
    public HPanel(final HEngine engine) {

        // Instantiate the engine
        this.engine = engine;
        startTime = System.currentTimeMillis();

        // Create the right side
        resultPanel = new ResultPanel();
        setRightComponent(resultPanel);

        // Create the left side
        problemPanel = new ProblemPanel(this);
        setLeftComponent(problemPanel);

        // Use a slightly larger monospace font
        Font font = new Font("Courier New", Font.PLAIN, 12);
        problemPanel.getTextArea().setFont(font);
        resultPanel.getTextArea().setFont(font);

        // Configure the text areas
        setBorder(new EmptyBorder(2, 2, 2, 2));
        problemPanel.getTextArea().setBorder(new EmptyBorder(GAP, GAP, GAP, GAP));
        resultPanel.getTextArea().setBorder(new EmptyBorder(GAP, GAP, GAP, GAP));
        resultPanel.getTextArea().setEditable(false);

        monitorThread = new MonitorThread();
        monitorThread.start();

    }

    public ProblemPanel getProblemPanel() {

        return problemPanel;

    }

    public ResultPanel getResultPanel() {

        return resultPanel;

    }

    /**
     * Loads a problem into the left panel.
     *
     * @param s the hava code to load
     */
    public void setProblem(final String s) {

        problemPanel.getTextArea().setText(s);
        problemPanel.getTextArea().setCaretPosition(s.length());
        updateEditorPosition();

    }

    // Updates the line:column info at bottom left
    private void updateEditorPosition() {

        int caretPosition = problemPanel.getTextArea().getCaretPosition();
        if (caretPosition == currentCaretPosition) {
            return;
        }

        currentCaretPosition = caretPosition;
        String s = problemPanel.getTextArea().getText();

        int line = 1;
        int column = 1;
        for (int i = 0; i < caretPosition; i++) {
            switch (s.charAt(i)) {
            case '\n':
                line++;
                column = 1;
                break;
            case '\r':
                break;
            case '\t':
                column = ((column + 7) / 8) * 8;
            default:
                column++;
                break;
            }
        }

        problemPanel.getEditorPositionLabel().setText(
                " Line " + line + " : Column " + column);

    }

    /**
     * This thread updates problem position (line and column) and work-in-progress.
     * It also launches the work thread when a new problem appears.
     */
    private class MonitorThread extends Thread {

        private boolean isAborted;

        void abort() {

            isAborted = true;

        }

        public void run() {

            int clock = 0;

            while (!isAborted) {

                try {

                    sleep(TICK);

                    updateEditorPosition();

                    if (resultPanel.getAutoCheckbox().getSelectedObjects() == null) {
                        continue;
                    }

                    clock--;

                    String s = problemPanel.getTextArea().getText();

                    if ((currentIsTrace != resultPanel.getTraceCheckbox().isSelected())
                            || !s.equals(currentProblem)) {

                        resultPanel.getTextArea().setText("");

                        if (workThread != null) {
                            workThread.abort();
                        }

                        clock = HOLD;
                        currentProblem = s;
                        currentIsTrace = resultPanel.getTraceCheckbox().isSelected();

                        resultPanel.getTextArea().setTabSize(currentIsTrace ? 2 : 8);
                        continue;

                    }

                    if (clock == 0) {
                        startTime = System.currentTimeMillis();
                        workThread = new WorkThread();
                        workThread.start();
                    }

                    if ((clock == -UPDATE) && (workThread != null)) {
                        resultPanel.getTextArea().setText(getStatus());
                        clock = 0;
                    }

                } catch (final Throwable x) {
                    engine.reset();
                }

            }

        }

    }

    private String getTimeElapsed() {

        final long m = (System.currentTimeMillis() - startTime) / 1000;

        final int seconds = (int) m % 60;
        final int minutes = (int) (m / 60) % 60;
        final int hours = (int) m / 3600;

        return (hours > 0 ? hours + " hr, " : "")
            + (hours + minutes > 0 ? minutes + " min, " : "") + seconds + " sec";

    }

    private String getValueCount() {

        final DecimalFormat myFormatter = new DecimalFormat("###,###");

        return myFormatter.format(engine.getValueCount());

    }

    private int getMemoryUsagePercentage() {

        final Runtime runtime = Runtime.getRuntime();

        final long max = runtime.maxMemory();
        final long total = runtime.totalMemory();
        final long free = runtime.freeMemory();

        return 100 - (int) (100 * (max - total + free) / max);

    }

    private String getMemoryUsageString() {

        return getMemoryUsagePercentage() + "%";

    }

    private String getStatus() {

        final StringBuilder status = new StringBuilder();

        status.append("Time elapsed:    ").append(getTimeElapsed()).append("\n");
        status.append("Values computed: ").append(getValueCount()).append("\n");
        status.append("Memory used:     ").append(getMemoryUsageString()).append("\n\n");

        return status.toString();

    }

    /**
     * This thread runs the Hava engine.
     */
    private class WorkThread extends Thread {

        private boolean isAborted;

        void abort() {
            isAborted = true;
            engine.abort();
        }

        public void run() {

            if (getMemoryUsagePercentage() > 50) {
                System.gc();
            }

            StringWriter sw = new StringWriter();

            try {

                try {

                    StringReader sr = new StringReader(currentProblem);
                    engine.load(sr);
                    sr.close();

                    if (currentIsTrace) {

                        final PrintWriter writer = new PrintWriter(sw);
                        engine.addDebugListener(new TraceDebugListener(writer));

                        engine.run();

                        writer.close();

                        // This stops the progress reports from overwriting the report.
                        workThread = null;

                    } else {

                        engine.run();

                        // This stops the progress reports from overwriting the report.
                        workThread = null;

                        if (System.currentTimeMillis() - startTime > 10000) {
                            sw.write(getStatus());
                        }

                        if (!isAborted) {
                            engine.generateReport(sw);
                        }

                    }

                } catch (final HException x) {

                    engine.reset();

                    workThread = null;

                    if (!isAborted) {

                        sw.write("Error");

                        if (x.getFile() != null) {
                            sw.write(" in file " + x.getFile());
                        }

                        if (x.getLine() > 0) {
                            sw.write(" at line " + x.getLine() + ", col " + x.getColumn());
                        }

                        sw.write(":\n");
                        sw.write(x.getMessage());
                    }

                } catch (final IOException x) {

                    workThread = null;
                    sw.write(x.getMessage());

                }

            } catch (final OutOfMemoryError x) {

                workThread = null;

                // Memory sometimes runs out while processing other exceptions.
                // This is the catch all. Start by clearing sw, which may be huge.
                sw = new StringWriter();
                sw.write("Out of memory.");

            }

            sw.append('\n');

            try {
                sw.close();
            } catch (final IOException x) {
                ;
            }

            Thread.yield();

            // This frees up memory used by the engine.
            engine.reset();

            String report;
            try {

                if (isAborted) {
                    report = "";
                } else {
                    report = sw.toString();
                }

                resultPanel.getTextArea().setText(report);
                if (!currentIsTrace) {
                    resultPanel.getTextArea().setCaretPosition(report.length());
                }

            } catch (final OutOfMemoryError x) {

                report = null;
                resultPanel.getTextArea().setText("Out of memory.");

            }

        }

    }

}
