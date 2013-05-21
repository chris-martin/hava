package edu.gatech.hava.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ResultPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Text area which displays output from the hava engine.
     */
    private final JTextArea reportTextArea;

    private final JPanel rightBottomPanel;

    private final JCheckBox traceCheckbox;
    private final JCheckBox autoCheckbox;

    ResultPanel() {

        rightBottomPanel = new JPanel();
        rightBottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JLabel solutionLabel = new JLabel(" Solution:");
        reportTextArea = new JTextArea();
        traceCheckbox = new JCheckBox("Trace");
        traceCheckbox.setFocusable(false);
        traceCheckbox.setBackground(null);
        autoCheckbox = new JCheckBox("Auto");
        autoCheckbox.setFocusable(false);
        autoCheckbox.setBackground(null);
        autoCheckbox.setSelected(true);

        rightBottomPanel.add(traceCheckbox);
        rightBottomPanel.add(autoCheckbox);

        JScrollPane rightScrollPane = new JScrollPane(reportTextArea);

        setLayout(new BorderLayout());

        add(solutionLabel, BorderLayout.NORTH);
        add(rightScrollPane, BorderLayout.CENTER);
        add(rightBottomPanel, BorderLayout.SOUTH);

    }

    JPanel getBottomPanel() {

        return rightBottomPanel;

    }

    public JTextArea getTextArea() {

        return reportTextArea;

    }

    JCheckBox getAutoCheckbox() {

        return autoCheckbox;

    }

    JCheckBox getTraceCheckbox() {

        return traceCheckbox;

    }

}
