package edu.gatech.hava.debug;

import edu.gatech.hava.parser.Token;

class SimpleNodeStringBuilder {

    private StringBuilder stringBuilder = new StringBuilder();

    private TokenType previousType;

    public SimpleNodeStringBuilder() {

    }

    public SimpleNodeStringBuilder(final Iterable<Token> tokens) {

        appendAll(tokens);

    }

    public void appendAll(final Iterable<Token> tokens) {

        for (final Token token : tokens) {
            append(token);
        }

    }

    public void append(final Token token) {

        final TokenType type = TokenType.get(token.kind);

        if (prependSpace(type)) {
            stringBuilder.append(" ");
        }

        stringBuilder.append(token.image);

        previousType = type;

    }

    private boolean prependSpace(final TokenType type) {

        if (previousType == null) {
            return false;
        }

        switch (type) {

        case SPACE_BOTH:
            return true;

        default:
            switch (previousType) {

            case SPACE_AFTER:
            case SPACE_BOTH:
                return true;

            default:
                return false;

            }

        }

    }

    @Override
    public String toString() {

        return stringBuilder.toString();

    }

    @Override
    public int hashCode() {

        return stringBuilder.hashCode();

    }

    @Override
    public boolean equals(final Object obj) {

        return stringBuilder.equals(obj);

    }

}
