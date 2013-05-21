package edu.gatech.hava.hdt.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public final class HavaTextAttributes {

    private HavaTextAttributes() { }

    static final TextAttribute SINGLE_LINE_COMMENT = new TextAttribute(
            Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));

    static final TextAttribute MULTI_LINE_COMMENT = new TextAttribute(
            Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));

    static final TextAttribute RESERVED_WORD = new TextAttribute(
            Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED),
            null,
            SWT.BOLD);

    static final TextAttribute ITERATOR = new TextAttribute(
            Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA),
            null,
            SWT.BOLD);

    static final TextAttribute FUNCTION = new TextAttribute(
            Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA),
            null,
            SWT.BOLD);

    static final TextAttribute FIELD = new TextAttribute(
            Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));

    static final TextAttribute TOKEN = new TextAttribute(
            Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));

    static final TextAttribute LITERAL = new TextAttribute(
            Display.getCurrent().getSystemColor(SWT.COLOR_RED));

}
