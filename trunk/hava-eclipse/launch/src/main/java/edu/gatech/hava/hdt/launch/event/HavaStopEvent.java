package edu.gatech.hava.hdt.launch.event;

public class HavaStopEvent extends HavaLaunchEvent {

    public enum Type {

        SUCCESS,

        FAIL,

        ABORT

    };

    private final Type type;

    public HavaStopEvent(final String name,
                         final Type type) {

        super(name);

        this.type = type;

    }

    public Type getType() {

        return type;

    }

}
