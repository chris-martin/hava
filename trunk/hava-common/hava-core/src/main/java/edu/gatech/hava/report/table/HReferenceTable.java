package edu.gatech.hava.report.table;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.gatech.hava.engine.HDocComment;
import edu.gatech.hava.engine.HParameters;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.engine.HValue;

public class HReferenceTable {

    private int headerWidth;
    private int headerDepth;

    private List<HReference> referenceList = new ArrayList<HReference>();
    private List<Key> keyList = new ArrayList<Key>();
    private Map<LabelLocator, String> labelList = new HashMap<LabelLocator, String>();

    public HReferenceTable() {

    }

    public void add(final HReference reference) {

        referenceList.add(reference);

    }

    public boolean accepts(final HReference reference) {

        if (reference instanceof HDocComment) {
            return false;
        } else if (referenceList.isEmpty()) {
            return true;
        } else {
            return reference.getParameters() == referenceList.get(0).getParameters();
        }

    }

    private boolean firstRun = true;

    public Table getStrings() {

        final String[][] cells;

        // The first reference of one or more from the same definition
        HReference x0 = referenceList.get(0);
        HValue v0 = x0.getValue();

        // Indexed variable
        if (x0.getParameters().getNumFields() > 0) {

            // Build header and identify columns
            if (firstRun) {
                for (int i = 0; i < x0.getParameters().getNumFields(); i++) {
                    generateReferenceKeys(new Key(i));
                }
                generateReferenceKeys(new Key());
            }

            cells = new String[headerDepth + referenceList.size()][];

            // Print header
            for (int j = 0; j < headerDepth; j++) {

                final String[] row = new String[headerWidth];
                cells[j] = row;

                for (int i = 0; i < headerWidth; i++) {
                    String label = labelList.get(new LabelLocator(i, j));
                    row[i] = label;
                }

            }

            int j = 0;
            for (final HReference x : referenceList) {

                final String[] row = new String[keyList.size()];
                cells[headerDepth + j] = row;

                int i = 0;
                for (Key k : keyList) {
                    final String str = valueToString(k.extractFromReference(x));
                    row[i++] = str;
                }

                j++;

            }

        // Simple structure
        } else if (v0.isStructure()) {

            // Build header
            if (firstRun) {
                generateReferenceKeys(new Key());
            }

            cells = new String[keyList.size()][];

            // One line for each field
            for (int i = 0; i < keyList.size(); i++) {

                final String[] row = new String[headerDepth];
                cells[i] = row;

                final Key k = keyList.get(i);

                for (int j = 1; j < headerDepth; j++) {
                    final String label = labelList.get(new LabelLocator(i, j));
                    row[j - 1] = label;
                }

                final String value = valueToString(k.extractFromReference(x0));
                row[row.length - 1] = value;

            }

        // Simple list
        } else if (v0.isList()) {

            // Build header
            if (firstRun) {
                generateListKeys(v0, new Key());
            }

            int headers = Math.max(1, headerDepth - 1);

            cells = new String[headers + v0.getNumElements()][];

            if (headerDepth <= 1) {
                cells[0] = new String[headerWidth];
            } else {
                for (int j = 1; j < headerDepth; j++) {
                    final String[] row = new String[headerWidth];
                    cells[j - 1] = row;
                    for (int i = 0; i < headerWidth; i++) {
                        final String label = labelList.get(new LabelLocator(i, j));
                        row[i] = label;
                    }
                }
            }

            for (int i = 0; i < v0.getNumElements(); i++) {

                final HValue v = v0.getElement(i);

                final String[] row;

                if (v.isList() && keyList.size() == 1) {

                    row = new String[v.getNumElements()];

                    for (int j = 0; j < v.getNumElements(); j++) {
                        row[j] = valueToString(v.getElement(j));
                    }

                } else if (!v.equals(HValue.BLANK)) {

                    row = new String[keyList.size()];

                    int j = 0;

                    for (Key k : keyList) {
                        final String str = valueToString(k.extractFromValue(v));
                        row[j++] = str;
                    }

                } else {

                    row = new String[0];

                }

                cells[headers + i] = row;

            }

        } else {

            // Other
            final String value = valueToString(x0.getValue());
            cells = new String[][]{new String[]{value}};

        }

        firstRun = false;

        return new Table(cells);

    }

    public int[] getColumnWidths() {

        return getStrings().getColumnWidths();

    }

    /**
     * Formats the output, and prints it.
     */
    public void generateOutput(final PrintWriter printWriter,
                               final int[] columnWidth,
                               final String rowPrefix) {

        final Table table = getStrings();

        final String[] rows = table.getRows(columnWidth);

        boolean firstRow = true;

        for (final String row : rows) {

            if (firstRow) {
                firstRow = false;
            } else {
                printWriter.print(rowPrefix);
            }

            printWriter.print(row);

        }

    }

    private void generateReferenceKeys(final Key key) {

        int i = keyList.size();
        int j = key.depth() - 1;

        for (HReference reference : referenceList) {

            HValue v = key.extractFromReference(reference);

            if (v == null) {
                continue;
            }

            final String label = key.getLabelFromReference(reference);
            if (label != null) {
                LabelLocator ll = new LabelLocator(i, j);
                labelList.put(ll, label);
                headerWidth = Math.max(headerWidth, i + 1);
                headerDepth = Math.max(headerDepth, j + 1);
                break;
            }

        }

        HReference reference0 = null;
        HParameters parameters0 = null;

        for (HReference reference : referenceList) {

            HValue value = key.extractFromReference(reference);

            if (value == null) {
                continue;
            }

            if (value.equals(HValue.BLANK)) {
                continue;
            }

            if (value.isStructure()) {
                HParameters parameters = value.getParameters();

                if (reference0 == null) {
                    reference0 = reference;
                    parameters0 = parameters;
                    continue;
                }

                if (parameters != parameters0) {
                    reference0 = null;
                    parameters0 = null;
                }
            }

            break;

        }

        if (reference0 == null) {
            keyList.add(key);
        } else {
            for (int m = 0; m < parameters0.getNumFields(); m++) {
                generateReferenceKeys(new Key(key, m));
            }
        }

    }

    private void generateListKeys(final HValue valueList, final Key key) {

        int i = keyList.size();
        int j = key.depth() - 1;

        if (j > 0) {

            for (int n = 0; n < valueList.getNumElements(); n++) {

                final HValue valueN = valueList.getElement(n);

                final HValue value = key.extractFromValue(valueN);
                if (value == null) {
                    continue;
                }

                String label = key.getLabelFromValue(valueN);
                if (label == null) {
                    break;
                }

                headerWidth = Math.max(headerWidth, i + 1);
                headerDepth = Math.max(headerDepth, j + 1);
                LabelLocator labelLocator = new LabelLocator(i, j);
                labelList.put(labelLocator, label);
                break;

            }

        }

        HParameters parameters0 = null;
        HValue value0 = null;

        for (int n = 0; n < valueList.getNumElements(); n++) {

            final HValue valueN = valueList.getElement(n);
            final HValue value = key.extractFromValue(valueN);

            if (value == null) {
                continue;
            }

            if (value.equals(HValue.BLANK)) {
                continue;
            }

            if (value.isStructure()) {
                HParameters parameters = value.getParameters();

                if (value0 == null) {
                    value0 = value;
                    parameters0 = parameters;
                    continue;
                }

                if (parameters != parameters0) {
                    value0 = null;
                    parameters0 = null;
                    break;
                }
            }

        }

        if (value0 == null) {
            keyList.add(key);
            return;
        }

        for (int m = 0; m < parameters0.getNumFields(); m++) {
            generateListKeys(valueList, new Key(key, m));
        }

    }

    private static String valueToString(final HValue value) {

        final String string;

        if (value == null) {
            string = "";
        } else if (value.equals(HValue.BLANK)) {
            string = "";
        } else {
            string = value.toString();
        }

        return string;

    }

}
