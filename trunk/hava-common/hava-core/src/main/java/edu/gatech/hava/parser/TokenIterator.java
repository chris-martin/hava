package edu.gatech.hava.parser;

import java.util.Iterator;
import java.util.NoSuchElementException;

class TokenIterator implements Iterator<Token> {

    private Token next;
    private final Token last;

    TokenIterator(final Token first, final Token last) {

        next = first;

        this.last = last;

    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {

        return next != null;

    }

    /**
     * {@inheritDoc}
     */
    public Token next() {

        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        final Token current = next;

        next = next == last ? null : next.next;

        return current;

    }

    /**
     * @throws UnsupportedOperationException
     */
    public void remove() {

        throw new UnsupportedOperationException();

    }

}
