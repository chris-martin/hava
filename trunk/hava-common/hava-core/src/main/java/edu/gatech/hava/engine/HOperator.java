package edu.gatech.hava.engine;

import edu.gatech.hava.engine.exception.IncompleteHavaException;

/**
 * Hava operator implementations.
 */
public final class HOperator {

    private HOperator() { }

    /**
     * @return true if both values are numerical, false otherwise
     */
    private static boolean bothNumerical(final HValue a,
                                         final HValue b) {

        return a.isNumerical() && b.isNumerical();

    }

    /**
     * @return true if both values are integers, false otherwise
     */
    private static boolean bothInteger(final HValue a,
                                       final HValue b) {

        return a.isInteger() && b.isInteger();

    }

    /**
     * @return true if both values are booleans, false otherwise
     */
    private static boolean bothBoolean(final HValue a,
                                       final HValue b) {

        return a.isBoolean() && b.isBoolean();

    }

    /**
     * Computes the logical AND of two boolean values.
     *
     * @param a a boolean value
     * @param b a boolean value
     * @return the boolean AND of <tt>a</tt> and <tt>b</tt>
     * @throws IncompleteHavaException if one of the values is not a boolean
     */
    public static HValue logicalAnd(final HValue a,
                                    final HValue b)
            throws IncompleteHavaException {

        if (bothBoolean(a, b)) {
            return new HValue(a.booleanValue() && b.booleanValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue logicalOr(final HValue a,
                                   final HValue b)
            throws IncompleteHavaException {

        if (bothBoolean(a, b)) {
            return new HValue(a.booleanValue() || b.booleanValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue logicalNot(final HValue a)
            throws IncompleteHavaException {

        if (a.isBoolean()) {
            return new HValue(!a.booleanValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue relationalEqual(final HValue a,
                                         final HValue b) {

        if (bothNumerical(a, b)) {
            return new HValue(a.doubleValue() == b.doubleValue());
        }

        return a.equals(b) ? HValue.TRUE : HValue.FALSE;

    }

    public static HValue relationalUnequal(final HValue a,
                                           final HValue b) {

        if (bothNumerical(a, b)) {
            return new HValue(a.doubleValue() != b.doubleValue());
        }

        return a.equals(b) ? HValue.FALSE : HValue.TRUE;

    }

    public static HValue relationalLessThan(final HValue a,
                                            final HValue b)
            throws IncompleteHavaException {

        if (bothInteger(a, b)) {
            return new HValue(a.intValue() < b.intValue());
        }

        if (bothNumerical(a, b)) {
            return new HValue(a.doubleValue() < b.doubleValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue relationalLessThanOrEqual(final HValue a,
                                                   final HValue b)
            throws IncompleteHavaException {

        if (bothInteger(a, b)) {
            return new HValue(a.intValue() <= b.intValue());
        }

        if (bothNumerical(a, b)) {
            return new HValue(a.doubleValue() <= b.doubleValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue relationalGreaterThan(final HValue a,
                                               final HValue b)
            throws IncompleteHavaException {

        if (bothInteger(a, b)) {
            return new HValue(a.intValue() > b.intValue());
        }

        if (bothNumerical(a, b)) {
            return new HValue(a.doubleValue() > b.doubleValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue relationalGreaterThanOrEqual(final HValue a,
                                                      final HValue b)
            throws IncompleteHavaException {

        if (bothInteger(a, b)) {
            return new HValue(a.intValue() >= b.intValue());
        }

        if (bothNumerical(a, b)) {
            return new HValue(a.doubleValue() >= b.doubleValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue add(final HValue a,
                             final HValue b)
            throws IncompleteHavaException {

        if (bothInteger(a, b)) {
            long la = a.intValue();
            long lb = b.intValue();
            long lv = la + lb;
            int iv = (int) lv;
            if (lv == iv) {
                return new HValue(iv);
            }
        }

        if (bothNumerical(a, b)) {
            return new HValue(a.doubleValue() + b.doubleValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue subtract(final HValue a,
                                  final HValue b)
            throws IncompleteHavaException {

        if (bothInteger(a, b)) {
            long la = a.intValue();
            long lb = b.intValue();
            long lv = la - lb;
            int iv = (int) lv;
            if (lv == iv) {
                return new HValue(iv);
            }
        }

        if (bothNumerical(a, b)) {
            return new HValue(a.doubleValue() - b.doubleValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue negative(final HValue a)
            throws IncompleteHavaException {

        if (a.isInteger()) {
            long la = a.intValue();
            long lv = -la;
            int iv = (int) lv;
            if (iv == lv) {
                return new HValue(iv);
            }
        }

        if (a.isNumerical()) {
            return new HValue(-a.doubleValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue multiply(final HValue a,
                                  final HValue b)
            throws IncompleteHavaException {

        if (bothInteger(a, b)) {
            long la = a.intValue();
            long lb = b.intValue();
            long lv = la * lb;
            int iv = (int) lv;
            if (iv == lv) {
                return new HValue(iv);
            }
        }

        if (bothNumerical(a, b)) {
            return new HValue(a.doubleValue() * b.doubleValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue divide(final HValue a,
                                final HValue b)
            throws IncompleteHavaException {

        if (b.isInteger() && b.intValue() == 0) {
            throw new IncompleteHavaException(HException.DIVIDE_BY_ZERO_MESSAGE);
        }

        if (bothInteger(a, b)) {
            int ia = a.intValue();
            int ib = b.intValue();
            if (ia % ib == 0) {
                return new HValue(ia / ib);
            }
        }

        if (bothNumerical(a, b)) {
            return new HValue(a.doubleValue() / b.doubleValue());
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue power(final HValue a,
                               final HValue b)
            throws IncompleteHavaException {

        if (bothInteger(a, b)) {

            int ia = a.intValue();
            int ib = b.intValue();

            if (ia == 1) {
                return new HValue(1);
            }

            if (ib >= 0) {

                long lp = 1;
                int ip = 1;

                while (ib-- > 0) {
                    lp *= ia;
                    ip = (int) lp;
                    if (ip != lp) {
                        ip = 0;
                        break;
                    }
                }

                if (ip != 0) {
                    return new HValue(ip);
                }

            }

        }

        if (bothNumerical(a, b)) {
            return new HValue(Math.pow(a.doubleValue(), b.doubleValue()));
        }

        if (a.equals(HValue.TWO) && b.isList()) {

            if (b.equals(HValue.EMPTYLIST)) {
                return b;
            }

            int n = Math.min(b.getNumElements(), 30);
            int pn = 1 << n;
            HValue[] outerList = new HValue[pn];

            for (int i = 0; i < pn; i++) {
                int k = 0;
                for (int j = 0; j < n; j++) {
                    if ((i & (1 << j)) != 0) {
                        k++;
                    }
                }
                HValue[] innerList = new HValue[k];
                k = 0;
                for (int j = 0; j < n; j++) {
                    if ((i & (1 << j)) != 0) {
                        innerList[k++] = b.getElement(j);
                    }
                }
                outerList[i] = new HValue(innerList);
            }

            return new HValue(outerList);

        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue round(final HValue a)
            throws IncompleteHavaException {

        if (a.isInteger()) {
            return a;
        }

        if (a.isDouble()) {
            return new HValue((int) Math.round(a.doubleValue()));
        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue listRange(final HValue a,
                                   final HValue b)
            throws IncompleteHavaException {

        if (bothInteger(a, b)) {

            int ia = a.intValue();
            int ib = b.intValue();

            HValue[] va = new HValue[1 + ib - ia];

            for (int i = ia; i <= ib; i++) {
                va[i - ia] = new HValue(i);
            }

            return new HValue(va);

        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    static HValue structExtract(final HValue a,
                                final String fieldName)
            throws IncompleteHavaException {

        if (fieldName.equals("valueType")) {
            return a.getType();
        }

        if (fieldName.equals("structType")) {
            if (a.isStructure()) {
                return new HValue(a.structValue().getStructureDefinition(), HValue.EMPTYLIST);
            } else {
                return HValue.ERROR;
            }
        }

        if (fieldName.equals("listSize")) {
            if (a.isList()) {
                return new HValue(a.getNumElements());
            } else {
                return HValue.ERROR;
            }
        }

        if (fieldName.equals("structSize")) {
            if (a.isStructure()) {
                return new HValue(a.structValue().getNumElements());
            } else {
                return HValue.ERROR;
            }
        }

        if (a.isStructure()) {

            HValue v = a.structField(fieldName);

            if (v == null) {
                throw new IncompleteHavaException(fieldName + " is not a field of "
                        + a.structValue().getStructureDefinition().getIdentifier());
            }

            return v;

        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    static HValue listExtract(HValue a,
                              final HValue b)
            throws IncompleteHavaException {

        if (a.isList()) {

            HValue.HVList bl = b.listValue();

            for (int i = 0; i < bl.getNumElements(); i++) {
                if (!a.isList()) {
                    break;
                }
                HValue.HVList al = a.listValue();
                HValue bli = bl.getElement(i);
                if (!bli.isInteger()) {
                    break;
                }
                a = al.getElement(bli.intValue() - 1);
            }

            return a;

        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    public static HValue listContains(final HValue a,
                                      final HValue b)
            throws IncompleteHavaException {

        if (b.isList()) {

            HValue.HVList bl = b.listValue();

            for (int i = 0; i < bl.getNumElements(); i++) {
                if (relationalEqual(a, bl.getElement(i)).booleanValue()) {
                    return HValue.TRUE;
                }
            }

            return HValue.FALSE;

        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

    static HValue scaleTo(final HValue a,
                          final HValue b)
            throws IncompleteHavaException {

        if (b.isNumerical()) {

            HValue v = HOperator.divide(a, b);
            v = HOperator.round(v);
            v = HOperator.multiply(v, b);

            return v;

        } else if (b.isList()) {

            HValue.HVList bl = b.listValue();
            for (int i = 0; i < bl.getNumElements(); i++) {
                if (relationalEqual(a, bl.getElement(i)).booleanValue()) {
                    return new HValue(i + 1);
                }
            }

            throw new IncompleteHavaException("Not an element of the list.");

        }

        throw new IncompleteHavaException(HException.TYPE_MESSAGE);

    }

}
