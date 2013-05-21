package edu.gatech.hava.engine;

import edu.gatech.hava.engine.HDefinition.HDefImport;
import edu.gatech.hava.parser.Token;

/**
 * A doc comment masquerading as an HReference so it can be
 * included in list passed to report generator.
 */
public class HDocComment extends HReference {

    private String commentText;
    private HDefinition def;
    private HReferenceToken token;

    HDocComment(final DefinitionEnvironment env,
                final Token t) {

        super();

        token = new HReferenceToken(t);

        String s = t.image;

        if (s.startsWith("/** ")) {
            s = s.substring(4);
        } else if (s.startsWith("/**")) {
            s = s.substring(3);
        }

        if (s.endsWith(" */")) {
            s = s.substring(0, s.length() - 3);
        } else if (s.endsWith("*/")) {
            s = s.substring(0, s.length() - 2);
        }

        this.commentText = s;
        def = new HDefinition(env);

    }

    /**
     * @return the text content of this document comment.
     */
    @Override
    public String toString() {

        return commentText;

    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {

        return commentText.hashCode();

    }

    /**
     * {@inheritDoc}
     *
     * @return false, because a comment cannot be overridden
     */
    @Override
    public boolean isOverridden() {

        return false;

    }

    /**
     * {@inheritDoc}
     *
     * @return false, because a comment cannot be a table
     */
    @Override
    public boolean isTable() {

        return false;

    }

    /**
     * {@inheritDoc}
     *
     * @return true if x is this object, false otherwise
     */
    @Override
    public boolean equals(final Object x) {

        return x == this;

    }

    /**
     * {@inheritDoc}
     *
     * @return an empty string
     */
    @Override
    public String getImportIdentifier() {

        return "";

    }

    /** {@inheritDoc} */
    @Override
    HDefinition getHDefinitionForRank() {

        return def;

    }

    /** {@inheritDoc} */
    @Override
    public HReferenceToken getReferenceToken() {

        return token;

    }

    /** {@inheritDoc} */
    @Override
    protected HDefImport getSource() {

        return def.getSource();

    }

}
