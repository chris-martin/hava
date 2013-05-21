package edu.gatech.hava.report.simple;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public final class StringUtil {

    private StringUtil() { }

    public static String getSpaces(final int n) {

        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < n; i++) {
            builder.append(' ');
        }

        return builder.toString();

    }

    public static void writeSpaces(final Writer writer,
                                   final int n) throws IOException {

        for (int i = 0; i < n; i++) {
            writer.write(' ');
        }

    }

    public static void writeSpaces(final PrintWriter writer,
                                   final int n) {

        for (int i = 0; i < n; i++) {
            writer.write(' ');
        }

    }

    public static void writeSpaces(final StringBuilder builder,
                                   final int n) {

        for (int i = 0; i < n; i++) {
            builder.append(' ');
        }

    }

    public static void writePadded(final StringBuilder builder,
                                   final String content,
                                   final int width) {

        final int pad;

        if (content == null) {
            pad = width;
        } else {
            builder.append(content);
            pad = width - content.length();
        }

        writeSpaces(builder, pad);

    }

    public static void write(final StringBuilder builder,
                             final String content) {

        if (content != null) {
            builder.append(content);
        }

    }

}
