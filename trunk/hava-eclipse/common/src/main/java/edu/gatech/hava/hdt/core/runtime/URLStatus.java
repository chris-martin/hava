package edu.gatech.hava.hdt.core.runtime;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;

public class URLStatus implements IStatus {

    private IOException exception;
    private String message;
    private String pluginID;

    public URLStatus(final IOException e,
                     final String pluginID) {

        this(e, "", pluginID);

    }

    public URLStatus(final IOException e,
                     final String message,
                     final String pluginID) {

        this.exception = e;
        this.message = message;
        this.pluginID = pluginID;

    }

    @Override
    public IStatus[] getChildren() {

        return new IStatus[0];

    }

    @Override
    public int getCode() {

        // TODO Auto-generated method stub
        return 0;

    }

    @Override
    public Throwable getException() {

        return exception;

    }

    @Override
    public String getMessage() {

        return message;

    }

    @Override
    public String getPlugin() {

        return pluginID;

    }

    @Override
    public int getSeverity() {

        return IStatus.ERROR;

    }

    @Override
    public boolean isMultiStatus() {

        return false;

    }

    @Override
    public boolean isOK() {

        return false;

    }

    @Override
    public boolean matches(final int severityMask) {

        return (severityMask & getSeverity()) != 0;

    }

}
