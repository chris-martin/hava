package edu.gatech.hava.gui.standalone;

import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CustomUndoManager implements DocumentListener, CaretListener {

    private static final int N = 100;
    private static final long T = 50;

    private JTextArea jTextArea;
    private ArrayList<State> stack;
    private int stackPos;
    private boolean ignoreUpdates;
    private long t;

    private class State {

        private String text;

        private int selectionStart;
        private int selectionEnd;

        State() {

            text = jTextArea.getText();

            selectionStart = jTextArea.getSelectionStart();
            selectionEnd = jTextArea.getSelectionEnd();

        }

        void apply() {

            ignoreUpdates = true;

            jTextArea.setText(text);
            jTextArea.setSelectionStart(selectionStart);
            jTextArea.setSelectionEnd(selectionEnd);

            ignoreUpdates = false;

        }

    }

    public CustomUndoManager(final JTextArea jTextArea) {

        this.jTextArea = jTextArea;

        jTextArea.addCaretListener(this);
        jTextArea.getDocument().addDocumentListener(this);

        reset();

    }

    public void changedUpdate(final DocumentEvent e) {

        update();

    }

    public void insertUpdate(final DocumentEvent e) {

        update();

    }

    public void removeUpdate(final DocumentEvent e) {

        update();

    }

    public void caretUpdate(final CaretEvent e) {

        update();

    }

    public void reset() {

        stack = new ArrayList<State>();
        stackPos = -1;
        update();

    }

    public void undo() {

        if (stackPos <= 0) {
            return;
        }

        stackPos--;
        stack.get(stackPos).apply();

    }

    public void redo() {

        if (stackPos >= stack.size() - 1) {
            return;
        }

        stackPos++;
        stack.get(stackPos).apply();

    }

    private void update() {

        if (ignoreUpdates) {
            return;
        }

        while (stackPos < stack.size() - 1) {
            stack.remove(stackPos + 1);
        }

        long tt = System.currentTimeMillis();
        if (stackPos >= 0 && (tt - t) < T) {
            stack.remove(stackPos);
        }

        if (stack.size() >= N) {
            stack.remove(0);
        }

        stackPos = stack.size();
        stack.add(new State());
        t = tt;

    }

}
