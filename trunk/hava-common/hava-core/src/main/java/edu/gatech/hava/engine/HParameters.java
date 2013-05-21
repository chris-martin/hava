package edu.gatech.hava.engine;

import java.util.HashSet;

import edu.gatech.hava.engine.exception.ExceptionEnvironment;
import edu.gatech.hava.parser.HavaSourceTreeConstants;
import edu.gatech.hava.parser.SimpleNode;

/**
 * Definition of a variable's indexes or a structure's fields.
 */
public class HParameters {

    private String[] fieldIdentifiers;
    private SimpleNode[] ganularityExpressions;
    private HValue[] granularityValues;

    HParameters() {

        fieldIdentifiers = new String[0];

    }

    HParameters(final SimpleNode paramListNode,
                final ExceptionEnvironment exEnv)
            throws HException {

        int k = paramListNode.getNumChildren();
        fieldIdentifiers = new String[k];
        ganularityExpressions = new SimpleNode[k];
        granularityValues = new HValue[k];

        for (int i = 0; i < k; i++) {

            SimpleNode paramNode = paramListNode.getChild(i);

            SimpleNode n = paramNode.getChild(0);
            exEnv.setErrorLocation(n);

            while (n.getNumChildren() == 1) {
                n = n.getChild(0);
            }

            if (n.getId() != HavaSourceTreeConstants.JJTIDENTIFIEREXPRESSION) {
                throw new HException("Expected identifier, not value.", exEnv);
            }

            fieldIdentifiers[i] = n.getToken(0).image;

            if (paramNode.getNumChildren() > 1) {
                ganularityExpressions[i] = paramNode.getChild(1);
            }

        }

        HashSet<String> hs = new HashSet<String>(k);

        for (int i = 0; i < k; i++) {

            String s = fieldIdentifiers[i];

            if (hs.contains(s)) {
                exEnv.setErrorLocation(paramListNode.getChild(i));
                throw new HException("Multiple definition.", exEnv);
            }

            hs.add(s);

        }

    }

    /**
     * Returns the number of parameters defined for this symbol.
     */
    public int getNumFields() {

        return fieldIdentifiers.length;

    }

    /**
     * Returns the identifier for the i-th parameters of this symbol.
     */
    public String getFieldIdentifier(final int i) {

        return fieldIdentifiers[i];

    }

    public String[] getFieldIdentifiers() {

        String[] copy = new String[fieldIdentifiers.length];

        System.arraycopy(fieldIdentifiers, 0, copy, 0, fieldIdentifiers.length);

        return copy;

    }

    /**
     * @param name the name of a field
     * @return the relative position of the field whose name is provided.
     */
    public int getFieldIndex(final String name) {

        int k = getNumFields();

        for (int i = 0; i < k; i++) {
            if (fieldIdentifiers[i].equals(name)) {
                return i;
            }
        }

        return -1;

    }

    final SimpleNode getFieldGranularityNode(final int i) {

        return ganularityExpressions[i];

    }

    void setFieldGranularityValue(final HValue v,
                                  final int i) {

        granularityValues[i] = v;

    }

    /**
     * Returns the grain associated with this field (or <code>null</code>, if none).
     */
    public HValue getFieldGranularityValue(int i) {

        return granularityValues[i];

    }

}
