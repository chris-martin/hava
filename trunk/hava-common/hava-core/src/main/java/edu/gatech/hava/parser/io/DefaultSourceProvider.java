package edu.gatech.hava.parser.io;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class DefaultSourceProvider implements HSourceProvider {

    private static final String[] URL_PREFIXES = {"http:", "file:"};

    public Reader openStream(final HSourceAddress address) throws IOException {

        final Reader reader;

        if (isUrl(address)) {

            try {
                reader = getUrlReader(address);
            } catch (final Exception x) {
                throw new IOException("Import failed: " + address, x);
            }

        } else {

            try {
                reader = getFileReader(address);
            } catch (final Exception x) {
                throw new IOException("Import failed: " + address, x);
            }

        }

        return reader;

    }

    protected boolean isUrl(final HSourceAddress address) {

        final String addressString = address.getAddress();

        for (final String prefix : URL_PREFIXES) {
            if (addressString.startsWith(prefix)) {
                return true;
            }
        }

        return false;

    }

    protected Reader getUrlReader(final HSourceAddress address) throws IOException {

        Reader reader = new InputStreamReader((new URL(address.getAddress())).openStream());
        char[] a = new char[3];
        int n = reader.read(a);
        reader.close();

        if ((n != 3) || (a[0] == '<')) {
            throw new IOException();
        }

        reader = new InputStreamReader((new URL(address.getAddress())).openStream());
        return reader;

    }

    protected Reader getFileReader(final HSourceAddress address) throws IOException {

        return new FileReader(address.getAddress());

    }

}
