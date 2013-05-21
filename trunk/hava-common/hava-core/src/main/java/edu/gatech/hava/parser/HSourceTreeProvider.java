package edu.gatech.hava.parser;

import java.io.IOException;
import java.io.Reader;

import edu.gatech.hava.parser.io.HSourceAddress;

/**
 * A {@link HSourceTreeProvider} provides parsed Hava source
 * to the Hava evaluator.
 */
public interface HSourceTreeProvider {

    /**
     * Retrieves and parses Hava code from a particular location.
     *
     * @param address the location of the Hava source
     * @return the root node of the syntax tree
     *
     * @throws IOException if the source code cannot be
     *                     obtained due to an io error
     * @throws ParseException if the source code cannot be parsed
     *                        because it is improperly formatted
     */
    SimpleNode getSourceTree(HSourceAddress address)
        throws IOException, ParseException;

    /**
     * Reads and parses hava code from a {@link Reader}.
     *
     * @param reader a reader which provides Hava source
     * @return the root node of the syntax tree
     *
     * @throws IOException if the source code cannot be
     *                     obtained due to an io error
     * @throws ParseException if the source code cannot be parsed
     *                        because it is improperly formatted
     */
    SimpleNode getSourceTree(Reader reader)
        throws IOException, ParseException;

}
