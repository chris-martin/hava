package edu.gatech.hava.parser;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Custom Node implementation for use in Hava.
 */
public class SimpleNode implements Node, Iterable<Token> {

    private int id;
    private Node parent;
    private Token firstToken;
    private Token lastToken;
    protected Node[] children;
    private ArrayList<Object> elements;

    public SimpleNode(final int i) {

        id = i;

    }

    public int getId() {

        return id;

    }

    /** {@inheritDoc} */
    public void jjtSetParent(final Node n) {

        parent = n;

    }

    /** {@inheritDoc} */
    public Node jjtGetParent() {

        return parent;

    }

    public Token jjtGetFirstToken() {

        return firstToken;

    }

    public void jjtSetFirstToken(final Token token) {
        firstToken = token;
    }

    public Token jjtGetLastToken() {

        return lastToken;

    }

    public void jjtSetLastToken(final Token token) {

        lastToken = token;

    }

    /** {@inheritDoc} */
    public int jjtGetNumChildren() {

        return (children == null) ? 0 : children.length;

    }

    /** {@inheritDoc} */
    public Node jjtGetChild(final int i) {

        return children[i];

    }

    /** {@inheritDoc} */
    public void jjtAddChild(final Node n, final int i) {

        if (children == null) {
            children = new Node[i + 1];
        } else if (i >= children.length) {
            Node[] c = new Node[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }
        children[i] = n;

    }

    /** {@inheritDoc} */
    public void jjtOpen() {
    }

    /** {@inheritDoc} */
    public void jjtClose() {
    }

    private void initElements() {

        if (elements == null) {

            elements = new ArrayList<Object>();

            Token t = firstToken;

            int i = 0;
            Token ti = getChildFirstToken(0);

            while (t != null) {

                if (t != ti) {
                    elements.add(t);
                } else {
                    elements.add(jjtGetChild(i));
                    t = getChildLastToken(i);
                    i++;
                    ti = getChildFirstToken(i);
                }

                if (t == lastToken) {
                    break;
                }

                t = t.next;

            }

        }

    }

    public SimpleNode GetParent() {

        return (SimpleNode) parent;

    }

    public int getNumElements() {

        initElements();
        return elements.size();

    }

    private Token getChildFirstToken(final int i) {

        if (i >= jjtGetNumChildren()) {
            return null;
        }

        SimpleNode n = (SimpleNode) jjtGetChild(i);
        return n.jjtGetFirstToken();

    }

    private Token getChildLastToken(final int i) {

        if (i >= jjtGetNumChildren()) {
            return null;
        }

        SimpleNode n = (SimpleNode) jjtGetChild(i);
        return n.jjtGetLastToken();

    }

    public int getNumChildren() {

        return jjtGetNumChildren();

    }

    public SimpleNode getChild(final int i) {

        return (SimpleNode) jjtGetChild(i);

    }

    public Token getToken(final int i) {

        initElements();
        return (Token) elements.get(i);

    }

    public Object getElement(final int i) {

        initElements();
        return elements.get(i);

    }

    public Iterator<Token> iterator() {

        return new TokenIterator(jjtGetFirstToken(), jjtGetLastToken());

    }

}
