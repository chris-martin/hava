package edu.gatech.hava.hdt.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

/**
 * The class that will supplant the current method of monopolizing the Eclipse status bar
 * to display elapsed time and memory usage.
 *
 */
public class TimeElapsedWorkbenchWindowContribution extends WorkbenchWindowControlContribution {

    private Label message = null;

    private boolean running = false;

    public TimeElapsedWorkbenchWindowContribution() {

        // TODO Auto-generated constructor stub

    }

    public TimeElapsedWorkbenchWindowContribution(final String id) {

        super(id);
        // TODO Auto-generated constructor stub

    }

    @Override
    protected Control createControl(final Composite parent) {

        Composite area = new Composite(parent, SWT.NONE);

        area.setLayout(new FillLayout());

        message = new Label(area, SWT.NONE);

        return area;

    }

    public void start() {

        start(true, 1000);

    }

    public void start(final int howOften) {

        start(true, howOften);

    }

    public void start(final boolean showMemoryUsage) {

        start(showMemoryUsage, 1000);

    }

    public void start(final boolean showMemoryUsage,
                      final int howOften) {

    }

    public void stop() {

    }

    public boolean running() {

        return running;

    }

    public void setTime(final long milliseconds) {

    }

    public void setMemoryUsage(final int percentUsed) {

        message.setText(percentUsed + "% used test 123455643");

    }

}
