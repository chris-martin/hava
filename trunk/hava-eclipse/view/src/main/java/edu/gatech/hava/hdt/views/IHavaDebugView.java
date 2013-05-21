package edu.gatech.hava.hdt.views;

import org.eclipse.ui.IViewPart;

import edu.gatech.hava.debug.IDebugNodeProvider;

/**
 * The Hava "Debug" view.  This displays information
 * about how a Hava script was executed.
 */
public interface IHavaDebugView extends IViewPart {

    /**
     * The ID of the debug view part.
     */
    String VIEW_ID = "edu.gatech.hava.hdt.views.debug";

    /**
     * Sets the source of this view's content, and refreshes the view.
     *
     * @param debugListener a debug listener which has captured all
     *                      of a Hava engine's debug events needed
     *                      for this view
     * @param loadException an exception generated while loading
     *                      and parsing Hava code, if one exists
     */
    void setContent(IDebugNodeProvider debugListener,
                    Exception loadException);

    /**
     * Sets the source of this view's content, and refreshes the view.
     *
     * @param fileID a String identifying the Hava code displayed
     * @param debugListener a debug listener which has captured all
     *                      of a Hava engine's debug events needed
     *                      for this view
     * @param loadException an exception generated while loading
     *                      and parsing Hava code, if one exists
     */
    void setContent(String fileID,
                    IDebugNodeProvider debugListener,
                    Exception loadException);

}
