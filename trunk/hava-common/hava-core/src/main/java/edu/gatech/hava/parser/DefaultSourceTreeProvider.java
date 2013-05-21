package edu.gatech.hava.parser;

import java.io.IOException;
import java.io.Reader;

import edu.gatech.hava.parser.io.DefaultSourceProvider;
import edu.gatech.hava.parser.io.HSourceAddress;
import edu.gatech.hava.parser.io.HSourceProvider;

public class DefaultSourceTreeProvider implements HSourceTreeProvider {

    private HSourceProvider sourceProvider;

    public SimpleNode getSourceTree(final HSourceAddress address)
            throws IOException, ParseException {

        final Reader reader = getSourceProvider().openStream(address);

        return getSourceTree(reader);

    }

    public SimpleNode getSourceTree(final Reader reader)
            throws IOException, ParseException {

        return new HavaSource(reader).Start();

    }

    protected HSourceProvider getSourceProvider() {

        if (sourceProvider == null) {
            sourceProvider = createSourceProvider();
        }

        return sourceProvider;

    }

    protected HSourceProvider createSourceProvider() {

        return new DefaultSourceProvider();

    }

}
