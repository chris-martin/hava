package edu.gatech.hava.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ProblemPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final HPanel hPanel;

    /**
     * Text area in which hava code is entered.
     */
    public final JTextArea problemTextArea;

    private final JLabel editorPositionLabel;

    ProblemPanel(final HPanel hPanel) {

        this.hPanel = hPanel;

        JLabel problemLabel = new JLabel(" Problem:");

        problemTextArea = new JTextArea();

        editorPositionLabel = new JLabel() {

            private static final long serialVersionUID = 1L;

            /**
             * The editor position label needs to be same height as the checkbox.
             *
             * {@inheritDoc}
             */
            public Dimension getPreferredSize() {

                int width = super.getPreferredSize().width;
                int height = hPanel.getResultPanel().getBottomPanel().getPreferredSize().height;

                return new Dimension(width, height);

            }

        };

        JScrollPane leftScrollPane = new JScrollPane(problemTextArea);
        setLayout(new BorderLayout());
        add(problemLabel, BorderLayout.NORTH);
        add(leftScrollPane, BorderLayout.CENTER);
        add(editorPositionLabel, BorderLayout.SOUTH);

    }

    public JTextArea getTextArea() {

        return problemTextArea;

    }

    JLabel getEditorPositionLabel() {

        return editorPositionLabel;

    }

}
