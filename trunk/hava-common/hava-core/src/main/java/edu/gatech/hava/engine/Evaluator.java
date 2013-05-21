package edu.gatech.hava.engine;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import edu.gatech.hava.engine.debug.HDebugListener;
import edu.gatech.hava.engine.exception.ExceptionEnvironment;
import edu.gatech.hava.engine.exception.IncompleteHavaException;
import edu.gatech.hava.engine.progress.ProgressMonitors;
import edu.gatech.hava.parser.HavaSourceConstants;
import edu.gatech.hava.parser.HavaSourceTreeConstants;
import edu.gatech.hava.parser.SimpleNode;
import edu.gatech.hava.parser.Token;

/**
 * Recursive methods that evaluate Hava expressions.
 */
class Evaluator {

    private final SymbolTable symbolTable;
    private final ProgressMonitors progressMonitor;
    private final Set<HReference> activeIndexSet = new HashSet<HReference>();
    private final HDebugListener debug;
    private final ExceptionEnvironment exEnv;

    Evaluator(final SymbolTable symbolTable,
              final HDebugListener debug,
              final ProgressMonitors progressMonitor,
              final ExceptionEnvironment exEnv) throws HException {

        this.symbolTable = symbolTable;
        this.debug = debug;
        this.progressMonitor = progressMonitor;
        this.exEnv = exEnv;

    }

    /**
     * @throws HAbortException if the run was aborted
     */
    private void checkAbort() throws HAbortException {

        if (progressMonitor.checkAbort()) {
            final String message = progressMonitor.getAbortMessage();
            throw new HAbortException(message, exEnv);
        }

    }

    private Hashtable<String, HValue> localVariableHashtable =
        new Hashtable<String, HValue>();

    private void removeLocalVariable(final String identifier) {

        localVariableHashtable.remove(identifier);

    }

    private void addLocalVariable(final String identifier) throws HException {

        if (localVariableHashtable.get(identifier) != null) {
            SymbolTable.duplicateIdentifier(identifier, exEnv);
        }

    }

    private void setLocalVariable(final String identifier, final HValue value) {

        localVariableHashtable.put(identifier, value);

    }

    private HValue getLocalVariableValue(final String identifier) {

        return localVariableHashtable.get(identifier);

    }

    private HValue granulate(final HParameters definition, final HValue v)
            throws HException, IncompleteHavaException {

        int k = v.getNumElements();
        HValue[] a = new HValue[k];

        for (int i = 0; i < k; i++) {
            HValue vi = v.getElement(i);
            SimpleNode grainNode = definition.getFieldGranularityNode(i);
            if (grainNode != null) {
                HValue grainValue = definition.getFieldGranularityValue(i);
                if (grainValue == null) {
                    grainValue = evaluateExpression(grainNode);
                    definition.setFieldGranularityValue(grainValue, i);
                }

                exEnv.setErrorLocation(grainNode.jjtGetFirstToken());
                vi = granulate(vi, grainValue);
            }
            a[i] = vi;
        }

        return new HValue(a);

    }

    private static HValue granulate(HValue v,
                                    final HValue grainValue)
            throws IncompleteHavaException {

        if (v.isNumerical()) {

            v = HOperator.scaleTo(v, grainValue);

        } else if (v.isList()) {

            int k = v.getNumElements();
            HValue[] a = new HValue[k];

            for (int i = 0; i < k; i++) {
                HValue vi = v.getElement(i);
                vi = granulate(vi, grainValue);
                a[i] = vi;
            }

            v = new HValue(a);

        }

        return v;

    }

    HValue evaluateVariableReference(final HDefinition.HDefVar varDef,
                                     final HValue args)
            throws HException, IncompleteHavaException {

        HReference hi = new HReference(varDef, args,
                symbolTable.getDefinitionEnvironment());
        if (activeIndexSet.contains(hi)) {
            throw new HException("Circular reference.", exEnv);
        }

        activeIndexSet.add(hi);
        Hashtable<String, HValue> oldH = localVariableHashtable;
        localVariableHashtable = new Hashtable<String, HValue>();

        for (int i = 0; i < args.getNumElements(); i++) {
            setLocalVariable(
                    varDef.getHDefIndex().getFieldIdentifier(i),
                    args.getElement(i));
        }

        HValue value = evaluateExpression(varDef.getRhsNode());
        localVariableHashtable = oldH;
        activeIndexSet.remove(hi);

        return value;

    }

    private HValue evaluateOrExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        HValue v = evaluateExpression(n.getChild(0));

        for (int i = 1; i < n.getNumChildren(); i++) {

            if (v.isBoolean() && v.booleanValue()) {
                return v;
            }

            exEnv.setErrorLocation(n.getToken(2 * i - 1));
            v = HOperator.logicalOr(v, evaluateExpression(n.getChild(i)));

        }

        return v;

    }

    private HValue evaluateAndExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        HValue v = evaluateExpression(n.getChild(0));

        for (int i = 1; i < n.getNumChildren(); i++) {

            if (v.isBoolean() && !v.booleanValue()) {
                return v;
            }

            exEnv.setErrorLocation(n.getToken(2 * i - 1));
            v = HOperator.logicalAnd(v, evaluateExpression(n.getChild(i)));

        }

        return v;

    }

    private HValue evaluateCompareExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        boolean isIterator = false;
        SimpleNode np = n;

        while (np != null) {

            SimpleNode child = np;
            np = np.GetParent();

            switch (np.getId()) {

            default:
                continue;

            case HavaSourceTreeConstants.JJTIDENTIFIEREXPRESSION:
                isIterator = child == np.getChild(0)
                    && np.getToken(np.getNumElements() - 1).kind == HavaSourceConstants.RBRACE;
                break;

            case HavaSourceTreeConstants.JJTLISTEXPRESSION:
            case HavaSourceTreeConstants.JJTEXTRACTEXPRESSION:
            case HavaSourceTreeConstants.JJTFILTEREXPRESSION:
            case HavaSourceTreeConstants.JJTASSIGNMENTSTATEMENT:
                break;

            }

            break;

        }

        int k = n.getNumChildren();

        if (isIterator) {

            if (n.getChild(0).jjtGetFirstToken()
                    != n.getChild(0).jjtGetLastToken()) {

                exEnv.setErrorLocation(n);
                throw new HException("Internal error", exEnv);

            }

            String identifierName = n.getChild(0).jjtGetFirstToken().image;

            switch (k) {
            case 2: {
                HValue v;
                switch (n.getToken(1).kind) {
                case HavaSourceConstants.IN:
                    v = evaluateExpression(n.getChild(1));
                    debug.addIn(identifierName, v);
                    if (!v.isList()) {
                        exEnv.setErrorLocation(n.getChild(1));
                        throw new HException(HException.TYPE_MESSAGE, exEnv);
                    }
                    return new HValue(identifierName, v);
                case HavaSourceConstants.ASSIGN:
                    v = evaluateExpression(n.getChild(1));
                    debug.addAssignment(identifierName, v);
                    v = new HValue(new HValue[] {v});
                    return new HValue(identifierName, v);
                default:
                }
            }
            case 3:
                if (n.getToken(1).kind != HavaSourceConstants.ASSIGN) {
                    break;
                }
                if (n.getToken(3).kind != HavaSourceConstants.TO) {
                    break;
                }

                HValue v1 = evaluateExpression(n.getChild(1));
                HValue v2 = evaluateExpression(n.getChild(2));
                debug.addIterationRange(identifierName, v1, v2);
                exEnv.setErrorLocation(n.getToken(3));
                return new HValue(identifierName, HOperator.listRange(v1, v2));
            default:
                exEnv.setErrorLocation(n.getToken(1));
                throw new HException(HException.SYNTAX_MESSAGE, exEnv);
            }
        }

        HValue[] a = new HValue[k];
        for (int i = 0; i < k; i++) {
            a[i] = evaluateExpression(n.getChild(i));
        }

        if (n.getNumChildren() == 2) {

            exEnv.setErrorLocation(n.getToken(1));

            switch (n.getToken(1).kind) {

            case HavaSourceConstants.TO:
                return HOperator.listRange(a[0], a[1]);

            case HavaSourceConstants.IN:
                return HOperator.listContains(a[0], a[1]);

            case HavaSourceConstants.ASSIGN:
                throw new HException(
                        "Use double equal sign to compare values.", exEnv);

            }

        }

        for (int i = 1; i < k; i++) {

            Token t = n.getToken(2 * i - 1);
            exEnv.setErrorLocation(t);

            switch (t.kind) {

            case HavaSourceConstants.EQ:
                if (!HOperator.relationalEqual(a[i - 1], a[i]).booleanValue()) {
                    return HValue.FALSE;
                }
                break;

            case HavaSourceConstants.NE:
                if (!HOperator.relationalUnequal(a[i - 1], a[i]).booleanValue()) {
                    return HValue.FALSE;
                }
                break;

            case HavaSourceConstants.LE:
                if (!HOperator.relationalLessThanOrEqual(a[i - 1], a[i])
                        .booleanValue()) {
                    return HValue.FALSE;
                }
                break;

            case HavaSourceConstants.LT:
                if (!HOperator.relationalLessThan(a[i - 1], a[i]).booleanValue()) {
                    return HValue.FALSE;
                }
                break;

            case HavaSourceConstants.GT:
                if (!HOperator.relationalGreaterThan(a[i - 1], a[i])
                        .booleanValue()) {
                    return HValue.FALSE;
                }
                break;

            case HavaSourceConstants.GE:
                if (!HOperator.relationalGreaterThanOrEqual(a[i - 1], a[i])
                        .booleanValue()) {
                    return HValue.FALSE;
                }
                break;

            default:
                throw new HException(HException.SYNTAX_MESSAGE, exEnv);

            }

        }

        return HValue.TRUE;

    }

    private HValue evaluateUnaryExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        final HValue v = evaluateExpression(n.getChild(0));
        final Token t = n.getToken(0);
        exEnv.setErrorLocation(t);

        switch (t.kind) {

        case HavaSourceConstants.NOT:
            return HOperator.logicalNot(v);

        case HavaSourceConstants.SUB:
            return HOperator.negative(v);

        default:
            exEnv.setErrorLocation(n);
            throw new HException("Unknown run-time error.", exEnv);

        }

    }

    private HValue evaluateArithmeticExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        final HValue a = evaluateExpression(n.getChild(0));
        final HValue b = evaluateExpression(n.getChild(1));

        final Token t = n.getToken(1);
        exEnv.setErrorLocation(t);

        return HOperator.scaleTo(a, b);

    }

    private HValue evaluateAddExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        HValue v = evaluateExpression(n.getChild(0));

        for (int i = 1; i < n.getNumChildren(); i++) {

            HValue w = evaluateExpression(n.getChild(i));
            Token t = n.getToken(2 * i - 1);
            exEnv.setErrorLocation(t);

            switch (t.kind) {

            case HavaSourceConstants.ADD:
                v = HOperator.add(v, w);
                break;

            case HavaSourceConstants.SUB:
                v = HOperator.subtract(v, w);
                break;

            default:

            }

        }

        return v;

    }

    private HValue evaluateMultiplyExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        HValue v = evaluateExpression(n.getChild(0));

        for (int i = 1; i < n.getNumChildren(); i++) {

            HValue w = evaluateExpression(n.getChild(i));
            Token t = n.getToken(2 * i - 1);
            exEnv.setErrorLocation(t);

            switch (t.kind) {

            case HavaSourceConstants.MUL:
                v = HOperator.multiply(v, w);
                break;

            case HavaSourceConstants.DIV:
                v = HOperator.divide(v, w);
                break;

            default:

            }

        }

        return v;

    }

    private HValue evaluatePowerExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        HValue a = evaluateExpression(n.getChild(0));
        HValue b = evaluateExpression(n.getChild(1));
        Token t = n.getToken(1);
        exEnv.setErrorLocation(t);
        return HOperator.power(a, b);

    }

    private HValue evaluateExtractExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        HValue v;
        int i; // Next element number
        int j; // Next child number

        HDefinition symbolDef;
        if ((n.getChild(0).getId() == HavaSourceTreeConstants.JJTIDENTIFIEREXPRESSION)
                && ((symbolDef = symbolTable.getIdentifierDefinition(
                        n.getChild(0).getToken(0).image)) instanceof HDefinition.HDefImport)) {

            exEnv.setErrorLocation(n.getToken(1));
            if (n.getToken(1).kind != HavaSourceConstants.DOT) {
                throw new HException(HException.SYNTAX_MESSAGE, exEnv);
            }

            String importVariableIdentifier = n.getToken(2).image;
            exEnv.setErrorLocation(n.getToken(2));
            HValue args = (n.getNumElements() > 3)
                    && (n.getToken(3).kind == HavaSourceConstants.LPAREN)
                    ? evaluateExpression(n.getChild(1))
                    : HValue.EMPTYLIST;

            HDefinition.HDefVarIdentifier vid = ((HDefinition.HDefImport) symbolDef)
                    .getOverriddenVariableIdentifierDefinition(importVariableIdentifier);
            if (vid == null) {
                throw new HException("Variable \""
                        + importVariableIdentifier
                        + "\" has not been overridden.", exEnv);
            }
            HDefinition.HDefVar vdef = vid.getHDefVar(args.getNumElements());
            if (vdef == null) {
                throw new HException("Variable \""
                        + importVariableIdentifier
                        + "\" has not been overridden.", exEnv);
            }

            HReference x = new HReference(vdef, args, symbolTable.getDefinitionEnvironment());

            v = symbolTable.getValue(x);
            exEnv.setErrorLocation(n);

            if (v == null) {
                v = evaluateVariableReference(vdef, x);
                symbolTable.setValue(x, v);
            }

            i = args.getNumElements() > 0 ? 6 : 3;
            j = args.getNumElements() > 0 ? 1 : 0;

        } else {

            v = evaluateExpression(n.getChild(0));
            i = 1;
            j = 1;

        }

        while (i < n.getNumElements()) {

            Token t = n.getToken(i);
            exEnv.setErrorLocation(t);

            switch (t.kind) {
            case HavaSourceConstants.LBRACKET:
                v = HOperator.listExtract(v,
                        evaluateExpression(n.getChild(j)));
                i += 3;
                j++;
                continue;

            case HavaSourceConstants.DOT:
                v = HOperator.structExtract(v, n.getToken(i + 1).image);
                i += 2;

                if (i < n.getNumElements()
                        && t.kind == HavaSourceConstants.LPAREN) {
                    t = n.getToken(i);
                    exEnv.setErrorLocation(t);
                    throw new HException(HException.SYNTAX_MESSAGE, exEnv);
                }
                continue;

            default:
                t = n.getToken(1);
                exEnv.setErrorLocation(t);
                throw new HException(HException.SYNTAX_MESSAGE, exEnv);
            }

        }

        return v;

    }

    private HValue evaluateLiteralExpression(final SimpleNode n)
            throws HException {

        Token t = n.getToken(0);
        String s = t.image;

        switch (n.getToken(0).kind) {

        case HavaSourceConstants.INT:
            return new HValue(Integer.parseInt(s));

        case HavaSourceConstants.REAL:
            return new HValue(Double.valueOf(s));

        case HavaSourceConstants.TRUE:
            return HValue.TRUE;

        case HavaSourceConstants.FALSE:
            return HValue.FALSE;

        default:

        }

        t = n.getToken(1);
        exEnv.setErrorLocation(t);
        throw new HException(HException.SYNTAX_MESSAGE, exEnv);

    }

    HValue evaluateIdentifierExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        Token t = n.getToken(0);
        exEnv.setErrorLocation(t);
        String s = t.image;

        HValue v = getLocalVariableValue(s);
        if (v != null) {
            if (n.getNumChildren() > 0) {
                throw new HException(HException.SYNTAX_MESSAGE, exEnv);
            }
            return v;
        }

        HDefinition d = symbolTable.getIdentifierDefinition(s);

        if (d instanceof HDefinition.HDefToken) {
            if (n.getNumChildren() > 0) {
                throw new HException(HException.SYNTAX_MESSAGE, exEnv);
            }
            return new HValue((HDefinition.HDefToken) d);
        }

        if (d instanceof HDefinition.HDefStruct) {
            HDefinition.HDefStruct structDef = (HDefinition.HDefStruct) d;

            if (n.getNumChildren() == 0) {
                return new HValue(structDef, HValue.EMPTYLIST);
            }
            if (n.getNumChildren() > 1) {
                throw new HException(HException.SYNTAX_MESSAGE, exEnv);
            }

            v = evaluateExpression(n.getChild(0));
            if (v.getNumElements() > structDef.getHDefIndex().getNumFields()) {
                throw new HException(
                        "Too many arguments for this struct.", exEnv);
            }

            v = granulate(structDef.getHDefIndex(), v);
            return new HValue(structDef, v);
        }

        if (d instanceof HDefinition.HDefFunction) {
            HValue args;

            switch (n.getNumChildren()) {
            case 0:
                args = HValue.EMPTYLIST;
                break;
            case 1:
                args = evaluateExpression(n.getChild(0));
                break;
            default:
                throw new HException(HException.SYNTAX_MESSAGE, exEnv);
            }

            HFunction f = ((HDefinition.HDefFunction) d).getFunction();
            v = f.evaluate(args);

            if (args.getNumElements() > 1) {
                debug.addAssignment(f.getIdentifier() + args, v);
            } else if (args.getNumElements() == 1) {
                debug.addAssignment(f.getIdentifier() + "("
                        + args.getElement(0).toString() + ")", v);
            } else {
                debug.addAssignment(f.getIdentifier(), v);
            }
            return v;
        }

        if (d instanceof HDefinition.HDefVarIdentifier) {
            HDefinition.HDefVarIdentifier dvi = (HDefinition.HDefVarIdentifier) d;
            HDefinition.HDefVar vdef;

            switch (n.getNumChildren()) {
            case 0:
                vdef = dvi.getHDefVar(0);
                break;
            case 1:
                vdef = dvi.getHDefVar(n.getChild(0).getNumChildren());
                break;
            default:
                throw new HException(HException.SYNTAX_MESSAGE, exEnv);
            }

            if (vdef != null) {
                final HReference x;

                if (n.getNumChildren() > 0) {
                    HValue args = evaluateExpression(n.getChild(0));
                    args = granulate(vdef.getHDefIndex(), args);
                    x = new HReference(vdef, args,
                            symbolTable.getDefinitionEnvironment());
                } else {
                    x = new HReference(vdef, HValue.EMPTYLIST,
                            symbolTable.getDefinitionEnvironment());
                }

                HDebugListener.Function debugFunction = null;
                if (vdef.isFunction()) {
                    debugFunction = debug.evaluatingFunction(x);
                } else {
                    v = symbolTable.getValue(x);
                    if (v != null) {
                        return v;
                    }
                }

                exEnv.setErrorLocation(n);
                v = evaluateVariableReference(vdef, x);

                if (vdef.isFunction()) {
                    debug.evaluationCompleted(debugFunction, v);
                } else {
                    symbolTable.setValue(x, v);
                }

                return v;
            }
        }

        if (d instanceof HDefinition.HDefIterator) {

            if (n.getNumChildren() == 0) {
                throw new HException(HException.SYNTAX_MESSAGE, exEnv);
            }

            HIterator iteratorDefinition = ((HDefinition.HDefIterator) d)
                    .getIterator();
            HIterator.HIteratorInstance iteratorInstance = iteratorDefinition
                    .createInstance();
            HDebugListener.Node debugNode = debug.evaluating(n,
                    iteratorDefinition.getIdentifier());

            // Without braces
            if (n.getNumChildren() == 1) {

                HValue a = evaluateExpression(n.getChild(0));
                for (int i = 0; i < a.getNumElements(); i++) {
                    v = a.getElement(i);
                    if (v.equals(HValue.IGNORE)) {
                        continue;
                    }
                    iteratorInstance.addCase(new HValue(i + 1), v);
                    if (iteratorInstance.isComplete()) {
                        break;
                    }
                }

                final String b = iteratorDefinition.getIdentifier() + a;
                v = iteratorInstance.getValue();
                debug.evaluationCompleted(debugNode, b, v);

            } else {

                // Iterator requires index?
                if (n.getChild(0).getNumChildren() > 1
                        && iteratorDefinition.requiresIndex()) {
                    exEnv.setErrorLocation(n.getToken(1));
                    throw new HException(
                            "Chained iterator directive not permitted with "
                                    + iteratorDefinition.getIdentifier()
                                    + ".", exEnv);
                }

                new IteratorUtility(iteratorInstance, n.getChild(0), n.getChild(1));

                final String b = iteratorDefinition.getIdentifier();
                v = iteratorInstance.getValue();
                debug.evaluationCompleted(debugNode, b, v);

            }

            return v;

        }

        throw new HException("Unknown identifier reference.", exEnv);

    }

    private HValue evaluateListExpression(SimpleNode n)
            throws HException, IncompleteHavaException {

        n = n.getChild(0);

        switch (n.getNumChildren()) {

        case 0:
            return HValue.EMPTYLIST;

        case 1:
            return evaluateExpression(n.getChild(0));

        default:
            return evaluateExpression(n);

        }

    }

    private HValue evaluateConditionalExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        for (int i = 0; i < n.getNumChildren() - 1; i += 2) {
            SimpleNode ni = n.getChild(i);
            exEnv.setErrorLocation(ni);
            HValue c = evaluateExpression(ni);
            if (!c.isBoolean()) {
                throw new HException(HException.TYPE_MESSAGE, exEnv);
            }
            boolean b = c.booleanValue();
            if (b) {
                debug.evaluatingIf(ni, true);
                HValue v = evaluateExpression(n.getChild(i + 1));
                debug.ifEvaluationCompleted(v);
                return v;
            } else {
                debug.evaluatingIf(ni, false);
            }
        }

        debug.evaluatingElse();
        HValue v = evaluateExpression(n
                .getChild(n.getNumChildren() - 1));
        debug.elseEvaluationCompleted(v);

        return v;

    }

    private HValue evaluateArgListExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        int k = n.getNumChildren();
        HValue[] a = new HValue[k];

        for (int i = 0; i < k; i++) {
            a[i] = evaluateExpression(n.getChild(i));
        }

        return new HValue(a);

    }

    private HValue evaluateArgExpression(final SimpleNode n)
            throws HException, IncompleteHavaException {

        HValue v = evaluateExpression(n.getChild(0));
        v.iteratorDirectiveValue(exEnv).setFilterNode(n.getChild(1));

        return v;

    }

    HValue evaluateExpression(SimpleNode n)
            throws HException {

        checkAbort();

        while (true) {

            if (n == null) {
                throw new HException(exEnv);
            }

            if (n.getNumElements() > 1) {
                break;
            }

            if (n.getElement(0) instanceof Token) {
                break;
            }

            if (n.getId() == HavaSourceTreeConstants.JJTARGLISTEXPRESSION) {
                break;
            }

            n = n.getChild(0);

        }

        try {

            final int id = n.getId();

            switch (id) {

            case HavaSourceTreeConstants.JJTOREXPRESSION:
                return evaluateOrExpression(n);

            case HavaSourceTreeConstants.JJTANDEXPRESSION:
                return evaluateAndExpression(n);

            case HavaSourceTreeConstants.JJTCOMPAREEXPRESSION:
                return evaluateCompareExpression(n);

            case HavaSourceTreeConstants.JJTUNARYEXPRESSION:
                return evaluateUnaryExpression(n);

            case HavaSourceTreeConstants.JJTARITHMETICEXPRESSION:
                return evaluateArithmeticExpression(n);

            case HavaSourceTreeConstants.JJTADDEXPRESSION:
                return evaluateAddExpression(n);

            case HavaSourceTreeConstants.JJTMULTIPLYEXPRESSION:
                return evaluateMultiplyExpression(n);

            case HavaSourceTreeConstants.JJTPOWEREXPRESSION:
                return evaluatePowerExpression(n);

            case HavaSourceTreeConstants.JJTEXTRACTEXPRESSION:
                return evaluateExtractExpression(n);

            case HavaSourceTreeConstants.JJTLITERALEXPRESSION:
                return evaluateLiteralExpression(n);

            case HavaSourceTreeConstants.JJTIDENTIFIEREXPRESSION:
                return evaluateIdentifierExpression(n);

            case HavaSourceTreeConstants.JJTLISTEXPRESSION:
                return evaluateListExpression(n);

            case HavaSourceTreeConstants.JJTCONDITIONALEXPRESSION:
                return evaluateConditionalExpression(n);

            case HavaSourceTreeConstants.JJTARGLISTEXPRESSION:
                return evaluateArgListExpression(n);

            case HavaSourceTreeConstants.JJTARGEXPRESSION:
                return evaluateArgExpression(n);

            default:

            }

        } catch (final IncompleteHavaException x) {
            throw x.complete(exEnv);
        } catch (final ArrayIndexOutOfBoundsException x) {
            exEnv.setErrorLocation(n);
            throw new HException(
                    "Accessed element of compound value does not exist.", exEnv);
        } catch (final ArithmeticException x) {
            exEnv.setErrorLocation(n);
            throw new HException("Bad arithmetic: " + x.getMessage(), exEnv);
        } catch (final RuntimeException x) {
            exEnv.setErrorLocation(n);
            throw new HException(x.getClass().getSimpleName() + ": "
                    + x.getMessage(), exEnv);
        }

        exEnv.setErrorLocation(n);
        throw new HException("Unknown run-time error.", exEnv);

    }

    private class IteratorUtility {

        private HIterator.HIteratorInstance iteratorInstance;
        private SimpleNode argsNode, valueNode;
        private int nestCount;

        IteratorUtility(final HIterator.HIteratorInstance iteratorInstance,
                        final SimpleNode argsNode,
                        final SimpleNode managedValueNode)
                throws HException, IncompleteHavaException {

            this.iteratorInstance = iteratorInstance;
            this.argsNode = argsNode;
            this.valueNode = managedValueNode;

            nestCount = argsNode.getNumChildren();

            enumerate(0);

        }

        private void enumerate(final int level)
                throws HException, IncompleteHavaException {

            final SimpleNode iteratorNode = argsNode.getChild(level);

            HValue rawIteratorDirective = evaluateExpression(iteratorNode);
            exEnv.setErrorLocation(iteratorNode);
            HValue.HVIteratorDirective levelIteratorDirective =
                rawIteratorDirective.iteratorDirectiveValue(exEnv);

            exEnv.setErrorLocation(iteratorNode);
            String indexVariableName = levelIteratorDirective.getIndexVariableName();
            if (Character.isDigit(indexVariableName.charAt(0))) {
                throw new HException("Not an index identifier.", exEnv);
            }

            exEnv.setErrorLocation(argsNode.getChild(level).jjtGetFirstToken());
            addLocalVariable(indexVariableName);

            for (int j = 0; j < levelIteratorDirective.getNumElements(); j++) {

                checkAbort();

                HValue indexValue = levelIteratorDirective.getElement(j);
                setLocalVariable(indexVariableName, indexValue);

                debug.evaluatingIteration(indexVariableName, indexValue);

                SimpleNode filterNode = levelIteratorDirective.getFilterNode();
                HValue filterValue = filterNode != null ? evaluateExpression(filterNode)
                        : HValue.TRUE;

                if (!filterValue.equals(HValue.TRUE)) {
                    debug.addFilter(filterValue);
                } else if (level != nestCount - 1) {
                    enumerate(level + 1);
                } else {
                    HValue index = levelIteratorDirective.getElement(j);
                    if (!index.equals(HValue.IGNORE)) {
                        HValue value = evaluateExpression(valueNode);
                        debug.addBracket(value);
                        if (!value.equals(HValue.IGNORE)) {
                            iteratorInstance.addCase(index, value);
                        }
                    }
                }

                debug.evaluationCompleted();

                if (iteratorInstance.isComplete()) {
                    break;
                }

            }

            removeLocalVariable(indexVariableName);

        }

    }

}
