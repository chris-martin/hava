package edu.gatech.hava.engine.exception;

import edu.gatech.hava.parser.SimpleNode;

public class ImportRec {

    String identifier;
    SimpleNode root;

    ImportRec(final String identifier, final SimpleNode root) {

        this.identifier = identifier;
        this.root = root;

    }

    public String getIdentifier() {

    	return identifier;

    }

    public SimpleNode getRoot() {

    	return root;

    }

}
