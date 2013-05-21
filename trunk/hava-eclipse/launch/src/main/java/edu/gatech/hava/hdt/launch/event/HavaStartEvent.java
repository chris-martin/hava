package edu.gatech.hava.hdt.launch.event;

public class HavaStartEvent extends HavaLaunchEvent {

    public enum Mode {

        RUN,

        DEBUG

    }

    private final Mode mode;

    public HavaStartEvent(final String name,
                          final Mode mode) {

        super(name);

        this.mode = mode;

    }

    public Mode getMode() {

        return mode;

    }

}
