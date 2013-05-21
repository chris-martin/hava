package edu.gatech.hava.engine;

import edu.gatech.hava.parser.Token;

/**
 * An immutable Token wrapper, which returns only immutable wrappers.
 *
 * The class is meant to allow internal Token objects to be accessible through
 * the API.  It exposes token location in the just-parsed, whitespace agnostic
 * source, along with the token image, and the next parsed regular token.
 */
// TODO not sure if whitespace comment is actually true
public class HReferenceToken {

    private Token innerToken;

    public HReferenceToken(final Token inner) {
        innerToken = inner;
    }

    public int getBeginLine() {
        return innerToken.beginLine;
    }

    public int getBeginColumn() {
        return innerToken.beginColumn;
    }

    public int getEndLine() {
        return innerToken.endLine;
    }

    public int getEndColumn() {
        return innerToken.endColumn;
    }

    public int getKind() {
        return innerToken.kind;
    }

    public HReferenceToken getNext() {
        return new HReferenceToken(innerToken.next);
    }

    /**
     * @return A copy of the wrapped Token object's String image.
     */
    @Override
    public String toString() {
        return new String(innerToken.image);
    }
}
