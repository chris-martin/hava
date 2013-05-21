package edu.gatech.hava.hdt.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * An extension to {@link BufferedReader} with a modified
 * {@link #readLine() method}.
 */
class CustomBufferedReader extends BufferedReader {

    int nextChar = -1;

    public CustomBufferedReader(final Reader reader) {

        super(reader);

    }

    public CustomBufferedReader(final InputStream stream) {

        this(new InputStreamReader(stream));

    }

    public CustomBufferedReader(final String contents) {

        this(new StringReader(contents));

    }

    /**
     * Reads a line, including any trailing carriage return
     * or newline.  This can return the final line in the
     * underlying stream, even if it is not terminated by a
     * carriage return or newline.
     *
     * {@inheritDoc}
     */
    @Override
    public String readLine() throws IOException {

        final StringBuilder str = new StringBuilder();

        while (true) {

            int c;

            if (nextChar != -1) {
                c = nextChar;
                nextChar = -1;
            } else {
                c = read();
                if (c == -1) {
                    break;
                }
            }

            str.append((char) c);

            if (c == '\n') {
                break;
            }

            if (c == '\r') {
                c = read();
                if (c == '\n') {
                    str.append((char) c);
                } else {
                    nextChar = c;
                }
                break;
            }

        }

        if (str.length() == 0) {
            return null;
        }

        return str.toString();

    }

}
