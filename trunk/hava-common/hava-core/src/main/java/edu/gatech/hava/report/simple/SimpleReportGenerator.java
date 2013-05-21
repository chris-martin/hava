package edu.gatech.hava.report.simple;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.report.HReportGenerator;
import edu.gatech.hava.report.sort.HIndexComparator;
import edu.gatech.hava.report.table.Table;

/**
 * Generates the output shown in the demo, by sorting computed values, and
 * printing them, using toString() methods provided in the engine.
 */
public class SimpleReportGenerator implements HReportGenerator {

    int[] maxWidths;

    boolean showImport;

    /**
     * A map from each reference name to its displayed value.
     * Using {@link LinkedHashMap} to maintain the correct ordering.
     */
    private Map<RowKey, HReportRow> map = new LinkedHashMap<RowKey, HReportRow>();

    private RowKey createRefKey(final HReference ref) {

        if (ref.isTable()) {
            return new ParamRowKey(ref);
        } else {
            return new RefRowKey(ref);
        }

    }

    public SimpleReportGenerator(final List<HReference> references) {

        setReferences(references);

    }

    public void setReferences(final List<HReference> references) {

        map.clear();

        Collections.sort(references, new HIndexComparator());

        for (HReference reference : references) {
            final HReportRow row = getOrCreateRow(reference);
            row.add(reference);
        }

        final ArrayList<Integer> columnWidths =
            new ArrayList<Integer>();

        for (final HReportRow row : map.values()) {

            if (!row.isComment()) {

                final int[] widths = row.getColumnWidths();
                for (int i = 0; i < widths.length; i++) {
                    Integer currentMaxWidth;
                    if (columnWidths.size() <= i) {
                        columnWidths.add(widths[i]);
                    } else {
                        currentMaxWidth = columnWidths.get(i);
                        columnWidths.set(i,
                                Math.max(currentMaxWidth, widths[i]));
                    }
                }

            }

        }

        /* Copy the array list into an int array, truncating
         * the first entry if showImport is false. */
        if (columnWidths.size() != 0) {
            showImport = columnWidths.get(0) != 0;
            final int columnCount =
                showImport ? columnWidths.size() : columnWidths.size() - 1;
            maxWidths = new int[columnCount];
            final Integer[] columnWidthIntegers =
                columnWidths.toArray(new Integer[0]);
            for (int i = showImport ? 0 : 1;
                    i < columnWidthIntegers.length; i++) {
                maxWidths[showImport ? i : i - 1] =
                    columnWidthIntegers[i];
            }
        }

    }

    public String get(final HReference reference) {

        final StringWriter str = new StringWriter();

        write(str, reference);

        return str.toString();

    }

    public void write(final StringWriter writer,
                      final HReference reference) {

        try {
            write((Writer) writer, reference);
        } catch (final IOException ioe) {
            ;
        }

    }

    public void write(final Writer writer,
                      final HReference reference)
            throws IOException {

        final HReportRow row = getRow(reference);

        if (row == null) {
            throw new IllegalArgumentException(
                    "no row found for reference: " + reference.toString());
        }

        write(writer, row);

    }


    public void write(final Writer writer) throws IOException {

        // Write the padded report
        for (final HReportRow row : map.values()) {
            write(writer, row);
        }

    }

    public HReference[] getDisplayedReferences() {

        final HReference[] references = new HReference[map.size()];

        int i = 0;
        for (final RowKey key : map.keySet()) {
            references[i++] = key.getReference();
        }

        return references;

    }

    private void write(final Writer writer,
                       final HReportRow row) throws IOException {

        final Table table = row.getCells(showImport);

        final String[] rows = table.getRows(maxWidths);

        for (final String str : rows) {
            writer.write(str);
        }

    }

    private HReportRow getRow(final RowKey id) {

        return map.get(id);

    }

    private HReportRow getOrCreateRow(final RowKey id) {

        HReportRow row = getRow(id);

        if (row == null) {
            row = new HReportRow();
            map.put(id, row);
        }

        return row;

    }

    private HReportRow getOrCreateRow(final HReference reference) {

        return getOrCreateRow(createRefKey(reference));

    }

    private HReportRow getRow(final HReference reference) {

        return getRow(createRefKey(reference));

    }

}
