package edu.gatech.hava.hdt.launch.config.task;

import edu.gatech.hava.parser.DefaultSourceTreeProvider;
import edu.gatech.hava.parser.HSourceTreeProvider;
import edu.gatech.hava.parser.io.HSourceProvider;

/**
 * {@link HSourceTreeProvider} for the Hava launch configuration.
 */
class HavaSourceTreeProvider extends DefaultSourceTreeProvider {

    /* This is a empty extension of {@link DefaultSourceTreeProvider}.
     * In the future, this could theoretically configured to retrieve
     * cached ASTs (probably from the project builder) to avoid
     * unnecessary re-parsing. */

    /** {@inheritDoc} */
    @Override
    protected HSourceProvider createSourceProvider() {

        return new HavaSourceProvider();

    }

}
