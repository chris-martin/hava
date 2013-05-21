package edu.gatech.hava.report.table;

import edu.gatech.hava.report.simple.StringUtil;

public class Table {

    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;

    String[][] cells;

    public Table(final String[][] cells) {

        init(cells);

    }

    /**
     * Constructs a table with a single row or column.
     *
     * @param layout HORIZONTAL or VERTICAL
     * @param cells all of the cells
     */
    public Table(final int layout,
                 final String ... cells) {

        if (layout == Table.HORIZONTAL) {

            init(new String[][]{cells});

        } else if (layout == Table.VERTICAL) {

            final String[][] c = new String[cells.length][];
            for (int i = 0; i < cells.length; i++) {
                c[i] = new String[]{cells[i]};
            }
            init(c);

        } else {

            throw new IllegalArgumentException();

        }

    }

    private void init(final String[][] cells) {

        this.cells = cells;

    }

    public static Table concatHorizontal(final Table ... tables) {

        int columnCount = 0;
        int rowCount = 0;
        for (final Table table : tables) {
            columnCount += table.getColumnCount();
            rowCount = Math.max(rowCount, table.getRowCount());
        }

        final String[][] cells = new String[rowCount][];
        for (int i = 0; i < rowCount; i++) {
            final String[] row = new String[columnCount];
            int columnBase = 0;
            for (final Table table : tables) {
                if (table.getRowCount() > i) {
                    final String[] rowPiece = table.getRow(i);
                    System.arraycopy(rowPiece, 0,
                            row, columnBase, rowPiece.length);
                }
                columnBase += table.getColumnCount();
            }
            cells[i] = row;
        }

        return new Table(cells);

    }

    public Table concatHorizontal(final Table other) {

        return concatHorizontal(this, other);

    }

    public int getRowCount() {

        return cells.length;

    }

    public int getColumnCount() {

        int cols = 0;

        for (final String[] row : cells) {
            if (row != null) {
                cols = Math.max(cols, row.length);
            }
        }

        return cols;

    }

    public int[] getColumnWidths() {

        int[] widths = new int[getColumnCount()];

        for (final String[] row : cells) {
            if (row != null) {
                for (int i = 0; i < row.length; i++) {
                    final String cell = row[i];
                    if (cell != null) {
                        widths[i] = Math.max(widths[i], row[i].length());
                    }
                }
            }
        }

        return widths;

    }

    public String[] getRow(final int i) {

        return cells[i];

    }

    public String getRow(final int i,
                         final int[] columnWidth) {

        final String[] row = cells[i];

        final StringBuilder str = new StringBuilder();

        for (int j = 0; j < row.length; j++) {

            final String cell = row[j];

            if (j != row.length - 1) {
                StringUtil.writePadded(str, cell, columnWidth[j]);
                str.append('\t');
            } else {
                StringUtil.write(str, cell);
            }

        }

        str.append('\n');

        return str.toString();

    }

    public String[] getRows(final int[] columnWidth) {

        final String[] rows = new String[getRowCount()];

        for (int i = 0; i < getRowCount(); i++) {
            rows[i] = getRow(i, columnWidth);
        }

        return rows;

    }

}
