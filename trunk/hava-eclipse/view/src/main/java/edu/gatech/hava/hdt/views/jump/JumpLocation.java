package edu.gatech.hava.hdt.views.jump;

/**
 * Specifies a location in a file to jump to.
 */
public interface JumpLocation {

    /**
     * @return the line number
     */
    int getLine();

    /**
     * @return a path to the file
     */
    String getFile();

}
