package edu.gatech.hava.hdt.builder;

import java.io.BufferedReader;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test cases for {@link CustomBufferedReader}.
 */
public class CustomBufferedReaderTest extends TestCase {

    /**
     * Reads a single unterminated line.
     *
     * @throws Exception for any reason
     */
    @Test
    public void testSingleLine1() throws Exception {

        test("abcdef");

    }

    /**
     * Reads a single line, terminated with a carriage return.
     *
     * @throws Exception for any reason
     */
    @Test
    public void testSingleLine2() throws Exception {

        test("hijkl\r");

    }

    /**
     * Reads a single line, terminated with a newline.
     *
     * @throws Exception for any reason
     */
    @Test
    public void testSingleLine3() throws Exception {

        test("hijkl\n");

    }

    /**
     * Reads a single line, terminated by a carriage return
     * and a newline.
     *
     * @throws Exception for any reason
     */
    @Test
    public void testSingleLine4() throws Exception {

        test("hijkl\r\n");

    }

    /**
     * Reads two lines. The first is terminated by a carriage
     * return and a newline.  The second is unterminated.
     *
     * @throws Exception for any reason
     */
    @Test
    public void testMultiLine1() throws Exception {

        test("xyz\r\n", "mnopq");

    }

    /**
     * Reads two lines. The first is terminated by a carriage
     * return.  The second is unterminated by a newline.
     *
     * @throws Exception for any reason
     */
    @Test
    public void testMultiLine2() throws Exception {

        test("xyz\r", "mnopq\n");

    }

    /**
     * Reads two lines. The first is terminated by a carriage
     * return and a newline.  The second is terminated by a
     * newline.
     *
     * @throws Exception for any reason
     */
    @Test
    public void testMultiLine3() throws Exception {

        test("xyz\r\n", "mnopq\n");

    }

    /**
     * Reads three lines.  Each line is terminated by a newline.
     *
     * @throws Exception for any reason
     */
    @Test
    public void testMultiLine4() throws Exception {

        test("a\n", "b\n", "c\n");

    }

    private void test(final String ... testStrings)
            throws Exception {

        final BufferedReader reader =
            new CustomBufferedReader(join(testStrings));

        String line;

        for (final String s : testStrings) {
            line = reader.readLine();
            assertEquals(s, line);
        }

        line = reader.readLine();
        assertNull(line);

    }

    private String join(final String[] strings) {

        final StringBuilder builder = new StringBuilder();

        for (final String s : strings) {
            builder.append(s);
        }

        return builder.toString();

    }

}
