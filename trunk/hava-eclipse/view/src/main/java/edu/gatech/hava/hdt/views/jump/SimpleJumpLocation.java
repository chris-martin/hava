package edu.gatech.hava.hdt.views.jump;

/**
 * An basic bean implementation of {@link JumpLocation}.
 */
public class SimpleJumpLocation implements JumpLocation {

    private int line;
    private String file;

    /**
     * Constructor.
     *
     * @param line the line number
     * @param file the file path
     */
    public SimpleJumpLocation(final int line,
                              final String file) {

        this.line = line;
        this.file = file;

    }

    @Override
    public int getLine() {

        return line;

    }

    @Override
    public String getFile() {

        return file;

    }

}
