package edu.gatech.hava.hdt.launch.event;

public abstract class HavaLaunchEvent {

    private final String name;

    public HavaLaunchEvent(final String name) {

        this.name = name;

    }

    public String getName() {

        return name;

    }

}
