package edu.gatech.hava.report.simple;

import edu.gatech.hava.engine.HDocComment;
import edu.gatech.hava.engine.HReference;
import edu.gatech.hava.report.table.HReferenceTable;
import edu.gatech.hava.report.table.Table;

class HReportRow {

    boolean initialized = false;

    private HReferenceTable table;

    private String comment;
    private String importString;
    private String name;
    private String value;

    boolean isComment() {

        return comment != null;

    }

    void setComment(final String comment) {

        this.comment = comment;

    }

    void add(final HReference reference) {

        if (reference instanceof HDocComment) {
            comment = reference.toString();
            if (comment == null) {
                throw new AssertionError("comment is null");
            }
        } else if (reference.isTable()) {
            addTableCell(reference);
            if (table == null) {
                throw new AssertionError("table is null");
            }
        } else {
            value = reference.getValue().toString();
            if (value == null) {
                throw new AssertionError("value is null");
            }
        }

        if (!initialized) {
            initialize(reference);
            initialized = true;
        }

    }

    private void initialize(final HReference reference) {

        final StringBuilder importStringBuilder = new StringBuilder();
        importStringBuilder.append(reference.getImportIdentifier());

        name = reference.toString();

        // If overridden, remove the import filename from the index string,
        // and add an asterix to the file prefix.
        if (reference.isOverridden()) {
            importStringBuilder.append("*");
            name = name.substring(reference.getImportIdentifier().length() + 1);
        }

        importString = importStringBuilder.toString();

    }

    private void addTableCell(final HReference reference) {

        if (!getTable().accepts(reference)) {
            throw new IllegalArgumentException();
        }

        getTable().add(reference);

    }

    public int[] getColumnWidths() {

        final int[] a = getNonValueWidth();
        final int[] b = getContentWidth();

        final int[] widths = new int[a.length + b.length];
        System.arraycopy(a, 0, widths, 0, a.length);
        System.arraycopy(b, 0, widths, a.length, b.length);

        return widths;

    }

    private int[] getNonValueWidth() {

        final int[] width;

        if (isComment()) {
            width = new int[0];
        } else {
            width = new int[] {
                importString.length(),
                name.length()
            };
        }

        return width;

    }

    private int[] getContentWidth() {

        int[] contentWidth;

        if (table != null) {
            contentWidth = table.getColumnWidths();
        } else {
            /* Shouldn't influence the alignment of anything
             * else, so just don't report the column width. */
            contentWidth = new int[0];
        }

        return contentWidth;

    }

    Table getCells(final boolean showImport) {

        Table cells;

        if (isComment()) {

            final String[] commentLines = comment.split("\n");

            final String[] prependRows;
            if (showImport) {
                prependRows = new String[1];
            } else {
                prependRows = new String[0];
            }
            final String[][] prependStrings =
                new String[commentLines.length][];
            for (int i = 0; i < prependStrings.length; i++) {
                prependStrings[i] = prependRows;
            }

            final Table prependTable = new Table(prependStrings);
            final Table contentTable = new Table(
                    Table.VERTICAL, commentLines);

            cells = prependTable.concatHorizontal(contentTable);

        } else if (table != null) {

            final String[] prependStrings;
            if (showImport) {
                prependStrings = new String[]{importString, name};
            } else {
                prependStrings = new String[]{name};
            }

            final Table prependTable = new Table(
                    Table.HORIZONTAL, prependStrings);
            final Table contentTable = table.getStrings();

            cells = prependTable.concatHorizontal(contentTable);

        } else if (value != null) {

            if (showImport) {
                cells = new Table(
                    Table.HORIZONTAL, importString, name, value);
            } else {
                cells = new Table(
                    Table.HORIZONTAL, name, value);
            }

        } else {

            throw new AssertionError("initialized, but table and value are null");

        }

        return cells;

    }

    private HReferenceTable getTable() {

        if (table == null) {
            table = new HReferenceTable();
        }

        return table;

    }

}
