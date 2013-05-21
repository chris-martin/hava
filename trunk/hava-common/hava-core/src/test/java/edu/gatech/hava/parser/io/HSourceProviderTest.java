package edu.gatech.hava.parser.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

public abstract class HSourceProviderTest extends TestCase {

    protected abstract HSourceProvider getSourceProvider();

    @Test
    public void testFilesystemImport() throws Exception {

        final HSourceProvider sourceProvider = getSourceProvider();

        final File file = createTemporaryFile();

        try {

            final String expected = "a = 5;";

            final Writer w = new FileWriter(file);
            w.write(expected);
            w.close();

            final HSourceAddress address = new HSourceAddress();
            address.setPath("file://" + file.getAbsolutePath());

            final Reader r = sourceProvider.openStream(address);
            final BufferedReader br = new BufferedReader(r);

            final String actual = br.readLine();
            assertEquals(expected, actual);

        } finally {

            file.delete();

        }

    }

    @Test
    public void testWebImport() throws Exception {

        final HSourceProvider sourceProvider = getSourceProvider();

        final int port = 8098;
        final String expected = "answer = 42;";
        final SimpleHttpServer server =
            new SimpleHttpServer(port, expected);

        boolean success = false;

        try {

            server.start();

            final HSourceAddress address = new HSourceAddress();
            address.setPath("http://localhost:" + port + "/");
            final Reader r = sourceProvider.openStream(address);
            final BufferedReader br = new BufferedReader(r);
            final String actual = br.readLine();

            assertEquals(expected, actual);

            success = true;

        } finally {

            server.stop();

        }

        assertTrue(success);

    }

    protected File createTemporaryFile() throws Exception {

        final Random random = new Random();

        final String fileName = "test"
            + Long.toString(Math.abs(random.nextLong()), 36);


        final File file = File.createTempFile(fileName, ".hava");

        return file;

    }

}
