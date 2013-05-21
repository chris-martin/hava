package edu.gatech.hava.engine;

import java.io.StringWriter;
import java.util.Formatter;

import edu.gatech.hava.engine.exception.ExceptionEnvironment;
import edu.gatech.hava.parser.SimpleNode;

/**
 * A value that may be assigned to a Hava variable at run-time,
 * like 3, true, (1,2,3) or ERROR.
 *
 * It is implemented as a wrapper on what might be a standard Java class
 * (Integer, Double, Boolean) or a custom Hava class (not publicly visible).
 */
public class HValue {

    /** A built-in token, returned by getType(). */
    public static final HValue INTEGER = new HValue("INTEGER");

    /** A built-in token, returned by getType(). */
    public static final HValue REAL = new HValue("REAL");

    /** A built-in token, returned by getType(). */
    public static final HValue BOOLEAN = new HValue("BOOLEAN");

    /** A built-in token, returned by getType(). */
    public static final HValue TOKEN = new HValue("TOKEN");

    /** A built-in token, returned by getType(). */
    public static final HValue STRUCTURE = new HValue("STRUCTURE");

    /** A built-in token, returned by getType(). */
    public static final HValue LIST = new HValue("LIST");

    /** A built-in token. */
    public static final HValue ERROR = new HValue("ERROR");

    /** A built-in token. */
    public static final HValue IGNORE = new HValue("IGNORE");

    /** A built-in token. */
    public static final HValue BLANK = new HValue("BLANK");

    /** A built-in value. */
    public static final HValue ZERO = new HValue(0);

    /** A built-in value. */
    public static final HValue ONE = new HValue(1);

    /** A built-in value. */
    public static final HValue TWO = new HValue(2);

    /** A built-in value. */
    public static final HValue TRUE = new HValue(true);

    /** A built-in value. */
    public static final HValue FALSE = new HValue(false);

    /** A built-in value. */
    public static final HValue EMPTYLIST = new HValue(new HValue[0]);

    /**
     * The sequence of these tokens determines default ranking in report.
     */
    static HValue[] getPredefinedTokens() {

        return new HValue[] {INTEGER, REAL, BOOLEAN, TOKEN, STRUCTURE, LIST,
                ERROR, IGNORE, BLANK};

    }

    private static class ICompound {

        private HValue[] a;

        private ICompound(final HValue[] a) {

            this.a = a;

        }

        private ICompound(final HValue v) {

            this.a = ((ICompound) v.ivalue).a;

        }

        public int hashCode() {

            int k = 0;
            for (int i = 0; i < a.length; i++) {
                k <<= 1;
                k ^= a[i].hashCode();
            }
            return k;

        }

        public boolean equals(final Object x) {

            if (!this.getClass().equals(x.getClass())) {
                return false;
            }

            final HValue[] b = ((ICompound) x).a;

            if (a.length != b.length) {
                return false;
            }

            for (int i = 0; i < a.length; i++) {
                if (HOperator.relationalUnequal(a[i], b[i]).booleanValue()) {
                    return false;
                }
            }

            return true;

        }

        int getNumElements() {

            return a.length;

        }

        HValue getElement(final int i) {

            return a[i];

        }

        public String toString() {

            StringWriter sw = new StringWriter();
            if (getNumElements() == 1 && (this instanceof HVList)) {
                sw.append("collect");
            }
            sw.append('(');
            for (int i = 0; i < getNumElements(); i++) {
                if (i > 0) {
                    sw.append(", ");
                }
                sw.append(getElement(i).toString());
            }
            sw.append(')');
            return sw.toString();

        }

    }

    static final class HVList extends ICompound {

        private HVList(final HValue[] a) {
            super(a);
        }

    }

    static class HVTypedCompound extends ICompound {

        private HParameters definition;

        HVTypedCompound(final HParameters definition, final HValue v) {

            super(v);
            this.definition = definition;

        }

        @Override
        public int hashCode() {

            return definition.hashCode() ^ super.hashCode();

        }

        @Override
        public boolean equals(final Object x) {

            if (!(x instanceof HVTypedCompound)) {
                return false;
            }

            final HVTypedCompound tc = (HVTypedCompound) x;

            if (definition != tc.definition) {
                return false;
            }

            return super.equals(x);

        }

        HParameters getDefinition() {
            return definition;
        }

    }

    static class HVStructure extends HVTypedCompound {

        private HDefinition.HDefStruct structureDefinition;

        private HVStructure(final HDefinition.HDefStruct structureDefinition, final HValue v) {

            super(structureDefinition.getHDefIndex(), v);
            this.structureDefinition = structureDefinition;

        }

        HDefinition.HDefStruct getStructureDefinition() {

            return structureDefinition;

        }

        public String toString() {

            return structureDefinition.getIdentifier()
                + (getNumElements() > 0 ? super.toString() : "");

        }

    }

    static final class HVReference extends HVTypedCompound {

        private HDefinition.HDefVar variableDefinition;

        private HVReference(final HDefinition.HDefVar variableDefinition, final HValue v) {

            super(variableDefinition.getHDefIndex(), v);
            this.variableDefinition = variableDefinition;

        }

        HDefinition.HDefVar getVariableDefinition() {

            return variableDefinition;

        }

    }

    static final class HVIteratorDirective extends ICompound {

        private String indexVariableName;
        private SimpleNode filterNode;

        private HVIteratorDirective(final String indexVariableName, final HValue v) {

            super(v);
            this.indexVariableName = indexVariableName;

        }

        String getIndexVariableName() {

            return indexVariableName;

        }

        void setFilterNode(final SimpleNode filterNode) {

            this.filterNode = filterNode;

        }

        SimpleNode getFilterNode() {

            return filterNode;

        }

    }

    private Object ivalue;

    /**
     * Back door for DocComment.
     */
    HValue() {

    }

    /**
     * Creates an HValue wrapper for the given integer.
     */
    public HValue(final Integer ivalue) {

        this.ivalue = ivalue;

    }

    /**
     * Creates an HValue wrapper for the given double.
     */
    public HValue(final Double dvalue) {

        this.ivalue = dvalue;

    }

    /**
     * Creates an HValue wrapper for the given boolean.
     */
    public HValue(final Boolean dvalue) {

        this.ivalue = dvalue;

    }

    HValue(final HDefinition.HDefToken tdef) {

        this.ivalue = tdef;

    }

    private HValue(final String s) {

        this(new HDefinition.HDefToken(s));

    }

    /**
     * Creates an HValue wrapper for the given list.
     */
    public HValue(final HValue[] a) {

        ivalue = new HVList(a);

    }

    HValue(final HDefinition.HDefStruct definition, final HValue a) {

        ivalue = new HVStructure(definition, a);

    }

    HValue(final HDefinition.HDefVar definition, final HValue a) {

        ivalue = new HVReference(definition, a);

    }

    HValue(final String indexVariableName, final HValue a) {

        ivalue = new HVIteratorDirective(indexVariableName, a);

    }

    /**
     * @return a representation of this value in the same format that appears
     *         in the demo report.
     */
    public String toString() {

        if (!(ivalue instanceof Double)) {
            return ivalue.toString();
        }

        Formatter f = new Formatter();
        f.format("%10.6G", (double) (Double) ivalue);
        String s = f.out().toString().trim();

        if (s.contains("E")) {
            int k;
            while (((k = s.indexOf("0E")) > 0) && (s.charAt(k - 1) != '.')) {
                s = s.replace("0E", "E");
            }
            return s;
        }

        while (s.endsWith("0") && !s.endsWith(".0")) {
            s = s.substring(0, s.length() - 1);
        }

        return s;

    }

    public int hashCode() {

        if (isInteger()) {
            Double d = (double) intValue();
            return d.hashCode();
        }

        return ivalue.hashCode();

    }

    public boolean equals(Object x) {

        if (!(x instanceof HValue)) {
            return false;
        }

        return ivalue.equals(((HValue) x).ivalue);

    }

    /**
     * @return this value as an int (run-time failure if it is not numerical).
     *         May round or truncate if not integer.
     */
    public int intValue() {

        return ((Number) ivalue).intValue();

    }

    /**
     * @return this value as a double (run-time failure if it is not numerical).
     */
    public double doubleValue() {

        return ((Number) ivalue).doubleValue();

    }

    /**
     * @return this value as a boolean (run-time failure if it is not boolean).
     */
    public boolean booleanValue() {

        return (Boolean) ivalue;

    }

    HVList listValue() {

        return (HVList) ivalue;

    }

    HDefinition.HDefToken getTokenDefinition() {

        return (HDefinition.HDefToken) ivalue;

    }

    /**
     * @return the definition of the indexes or fields of this value
     *         (if it is an index or a structure).
     */
    public HParameters getParameters() {

        if (ivalue == null) {
            return null;
        }

        final HVTypedCompound vTypedCompound = ((HVTypedCompound) ivalue);

        return vTypedCompound.definition;

    }

    HVStructure structValue() {

        return (HVStructure) ivalue;

    }

    /**
     * @return the value of the field of given name (struct value only).
     */
    public HValue structField(String fieldName) {

        HValue.HVStructure as = structValue();
        HDefinition.HDefStruct asd = as.getStructureDefinition();
        int i = asd.getHDefIndex().getFieldIndex(fieldName);
        if (i < 0) {
            return null;
        }
        return as.getElement(i);

    }

    HVIteratorDirective iteratorDirectiveValue(final ExceptionEnvironment exEnv)
            throws HException {

        try {
            return (HVIteratorDirective) ivalue;
        } catch (final ClassCastException x) {
            throw new HException("Not an iterator directive.", exEnv);
        }

    }

    HVReference getIValue() {

        return (HVReference) ivalue;

    }

    /**
     * @return true if this value is an integer.
     */
    public boolean isInteger() {

        return ivalue instanceof Integer;

    }

    /**
     * @return true if this value is a double.
     */
    public boolean isDouble() {

        return ivalue instanceof Double;

    }

    /**
     * @return true if this value is a number (integer or double).
     */
    public boolean isNumerical() {

        return ivalue instanceof Number;

    }

    /**
     * @return true if this value is a boolean.
     */
    public boolean isBoolean() {

        return ivalue instanceof Boolean;

    }

    /**
     * @return true if this value is a token.
     */
    public boolean isToken() {

        return ivalue instanceof HDefinition.HDefToken;

    }

    /**
     * @return true if this value is a structure.
     */
    public boolean isStructure() {

        return ivalue instanceof HVStructure;

    }

    /**
     * @return true if this value is a list.
     */
    public boolean isList() {

        return ivalue instanceof HVList;

    }

    /**
     * @return true if this value is an index.
     */
    public boolean isIndex() {

        return this instanceof HReference;

    }

    /**
     * @return true if this value is a list, an index or a structure.
     */
    public boolean isCompound() {

        return isList() || isIndex() || isStructure();

    }

    /**
     * @return the type of this value, as a Hava token instance.
     */
    public HValue getType() {

        if (isInteger()) {
            return INTEGER;
        }
        if (isDouble()) {
            return REAL;
        }
        if (isBoolean()) {
            return BOOLEAN;
        }
        if (isToken()) {
            return TOKEN;
        }
        if (isList()) {
            return LIST;
        }
        if (isStructure()) {
            return STRUCTURE;
        }

        return ERROR;

    }

    /**
     * @return If compound, returns the number of elements it contains.
     */
    public int getNumElements() {

        return ((ICompound) ivalue).getNumElements();

    }

    /**
     * @return If compound, returns the i-th element.
     */
    public HValue getElement(final int i) {

        return ((ICompound) ivalue).getElement(i);

    }

    HDefinition getHDefinitionForRank() {

        if (isToken()) {
            return getTokenDefinition();
        }
        if (isStructure()) {
            return structValue().getStructureDefinition();
        }
        return null;

    }

    /**
     * Used by report generator to determine the sequence in which value types were defined.
     * Meaningful only values with defined types: token, structure, reference, import or
     * doc comment. In other words, not meaningful for integer, real, boolean or list types.
     *
     * @return 1 if the type of this value was defined before the type of the argument v,
     *         -1 if the opposite is true, or 0 if they have the same definition.
     */
    public int compareDefinitionRank(final HValue v) {

        return getHDefinitionForRank().compareRank(v.getHDefinitionForRank());

    }

}
