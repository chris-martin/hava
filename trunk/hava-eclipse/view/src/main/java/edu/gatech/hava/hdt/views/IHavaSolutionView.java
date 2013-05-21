package edu.gatech.hava.hdt.views;

import org.eclipse.ui.IViewPart;

import edu.gatech.hava.engine.HEngine;
import edu.gatech.hava.engine.HReference;

/**
 * The Hava "Debug" view.  This displays the results
 * from the execution of a Hava script.
 */
public interface IHavaSolutionView extends IViewPart {

    /**
     * The ID of the solution view part.
     */
    String VIEW_ID = "edu.gatech.hava.hdt.views.havaSolution";

    /**
     * Sets the data source, and refreshes the view.
     *
     * @param engine the engine, after it has been run
     * @param exception any exception thrown from the engine
     *                  (may be null)
     * @param reference the reference being evaluated as the
     *                  exception was thrown (may be null
     *                  only if <tt>exception</tt> is null)
     */
    void setContent(HEngine engine,
                    Exception exception,
                    HReference reference);

    /**
     * Sets the data source, and refreshes the view.
     *
     * @param engine the engine, after it has been run
     * @param exception any exception thrown from the engine
     *                  (may be null)
     * @param reference the reference being evaluated as the
     *                  exception was thrown (may be null
     *                  only if <tt>exception</tt> is null)
     */
    void setContent(String fileId,
                    HEngine engine,
                    Exception exception,
                    HReference reference);

}
