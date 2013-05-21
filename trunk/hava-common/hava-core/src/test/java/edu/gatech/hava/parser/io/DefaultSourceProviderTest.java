package edu.gatech.hava.parser.io;

public class DefaultSourceProviderTest extends HSourceProviderTest {

    @Override
    protected HSourceProvider getSourceProvider() {

        return new DefaultSourceProvider();

    }

}
