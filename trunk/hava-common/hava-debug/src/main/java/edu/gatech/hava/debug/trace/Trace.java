package edu.gatech.hava.debug.trace;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

class Trace {

    static final int SPACE = 0;
    static final int BULLET = 1;
    static final int IF_TRUE = 2;
    static final int IF_FALSE = 3;
    static final int ELSE = 4;
    static final int IN = 5;
    static final int TO = 6;
    static final int LBRACE = 7;
    static final int RBRACE = 8;
    static final int ASSIGN = 9;
    static final int ELLIPSIS = 10;
    static final int FILTER = 11;

    private static final String[] LOG_TOKEN = new String[] {" ", "# ",
            "if (true) ", "if (false)", "else ", " in ", " to ", "{", "}",
            " = ", "...", "FILTER "};

    private static class Line {

        private final Object[] xArray;

        Line(final Object x1) {

            xArray = new Object[] {x1};

        }

        Line(final Object x1,
             final Object x2) {

            xArray = new Object[] {x1, x2};

        }

        Line(final Object x1,
             final Object x2,
             final Object x3) {

            xArray = new Object[] {x1, x2, x3};

        }

        Line(final Object x1,
             final Object x2,
             final Object x3,
             final Object x4) {

            xArray = new Object[] {x1, x2, x3, x4};

        }

        Line(final Object x1,
             final Object x2,
             final Object x3,
             final Object x4,
             final Object x5) {

            xArray = new Object[] {x1, x2, x3, x4, x5};

        }

        @Override
        public int hashCode() {

            int h = 0;

            for (int i = 0; i < xArray.length; i++) {
                h += xArray[i].hashCode();
            }

            return h;

        }

        @Override
        public boolean equals(final Object obj) {

            if (!(obj instanceof Line)) {
                return false;
            }

            Object[] aa = ((Line) obj).xArray;
            if (xArray.length != aa.length) {
                return false;
            }

            for (int i = 0; i < xArray.length; i++) {
                if (!xArray[i].equals(aa[i])) {
                    return false;
                }
            }

            return true;

        }

    }

    private PrintWriter pw;
    private int indent;
    private Set<Line> hs;

    Trace(final PrintWriter pw) {

        this.pw = pw;
        resetHashSet();

    }

    void resetHashSet() {

        hs = new HashSet<Line>();

    }

    void push() {

        indent++;
        resetHashSet();

    }

    void pop() {

        indent--;
        resetHashSet();

    }

    private void print(final Line line) {

        if ((line.xArray.length>1) && (line.xArray[1].equals(new Integer(ASSIGN)))) {

            if (hs.contains(line)) {
                return;
            }

            hs.add(line);

        } else {
            resetHashSet();
        }

        for (int i = 0; i < indent; i++) {
            pw.print("  ");
        }

        for (Object x : line.xArray) {
            if (x instanceof Integer) {
                x = LOG_TOKEN[(Integer) x];
            }
            pw.print(x);
        }

        pw.println();

    }

    void log(final Object x1) {

        print(new Line(x1));

    }

    void log(final Object x1,
             final Object x2) {

        print(new Line(x1, x2));

    }

    void log(final Object x1,
             final Object x2,
             final Object x3) {

        print(new Line(x1, x2, x3));

    }

    void log(final Object x1,
             final Object x2,
             final Object x3,
             final Object x4) {

        print(new Line(x1, x2, x3, x4));

    }

    void log(final Object x1,
             final Object x2,
             final Object x3,
             final Object x4,
             final Object x5) {

        print(new Line(x1, x2, x3, x4, x5));

    }

}
