package edu.gatech.hava.hdt.launch.event;

/**
 * Interface that defines a listener for Hava launches.
 * Created to support the abstraction of launch statistics.
 */
public interface IHavaLaunchListener {

    void fire(HavaStartEvent event);

    void fire(HavaStopEvent event);

}
