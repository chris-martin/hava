package edu.gatech.hava.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import edu.gatech.hava.engine.debug.HDebugListener;
import edu.gatech.hava.engine.exception.ExceptionEnvironment;
import edu.gatech.hava.lib.Functions;
import edu.gatech.hava.lib.iterator.Iterators;
import edu.gatech.hava.parser.HavaSourceConstants;
import edu.gatech.hava.parser.HavaSourceTreeConstants;
import edu.gatech.hava.parser.SimpleNode;
import edu.gatech.hava.parser.Token;
import edu.gatech.hava.parser.io.HSourceAddress;

/**
 * Manages the lists of defined variables and computed values.
 */
class SymbolTable {

    private Hashtable<String, HDefinition> definitionHashtable =
        new Hashtable<String, HDefinition>();

    private Hashtable<HReference, HValue> valueHashtable =
        new Hashtable<HReference, HValue>();

    private HDebugListener debug;

    private ArrayList<HDefinition.HDefImport> importQueue =
        new ArrayList<HDefinition.HDefImport>();

    private int numPathsAddedByCurrentImport;

    private final DefinitionEnvironment env;
    private final ExceptionEnvironment exEnv;

    DefinitionEnvironment getDefinitionEnvironment() {
        return env;
    }

    /**
     * Instantiates a new SymbolTable.
     */
    SymbolTable(final String givenAddress,
                final HDebugListener debug,
                final ExceptionEnvironment exEnv) {

        this.debug = debug;

        this.exEnv = exEnv;

        env = new DefinitionEnvironment(givenAddress);

        // Add predefined tokens (<code>INTEGER</code>, <code>REAL</code>, etc.).
        for (HValue v : HValue.getPredefinedTokens()) {
            definitionHashtable.put(v.toString(), v.getTokenDefinition());
        }

        // Add predefined functions (<code>round</code>, <code>random</code>, etc).
        for (HFunction f : Functions.getAll()) {
            registerFunction(f);
        }

        // Add predefined iterators (<code>max</code>, <code>collect</code>, etc).
        for (HIterator t : Iterators.getAll()) {
            registerIterator(t);
        }

    }

    /**
     * Adds a function provided by host application.
     */
    void registerFunction(final HFunction f) {

        definitionHashtable.put(f.getIdentifier(), new HDefinition.HDefFunction(env, f));

    }

    /**
     * Add an iterator provided by host application.
     */
    void registerIterator(final HIterator t) {

        definitionHashtable.put(t.getIdentifier(), new HDefinition.HDefIterator(env, t));

    }

    /**
     * Load definitions from a parsed source file.
     */
    void load(final SimpleNode root) throws HException {

        // Notify HException, so it can identify this source with errors that
        // may be detected in it.
        if (env.getCurrentHDImport() != env.getRootHDImport()) {
            exEnv.registerImport(
                    env.getCurrentHDImport().getIdentifier(), root);
        }

        // Traverse the source file, one definition at a time.
        for (int i = 0; i < root.getNumElements(); i++) {

            Object e = root.getElement(i);

            if (e instanceof Token) {
                Token t = (Token) e;
                if (t.kind == HavaSourceConstants.DOC_COMMENT) {
                  HDocComment hdc = new HDocComment(env, t);
                  valueHashtable.put(hdc, hdc);
                }
                continue;
            }

            SimpleNode statementNode = (SimpleNode) root.getElement(i);

            SimpleNode typedStatementNode = statementNode.getChild(0);

            HDefinition.StatementFlags permittedFlags = new HDefinition.StatementFlags();

            switch (typedStatementNode.getId()) {

            case HavaSourceTreeConstants.JJTASSIGNMENTSTATEMENT:
                permittedFlags.set(HDefinition.StatementFlags.FINAL
                        + HDefinition.StatementFlags.PRIVATE
                        + HDefinition.StatementFlags.FUNCTION
                        + HDefinition.StatementFlags.TABLE);
                break;

            case HavaSourceTreeConstants.JJTIMPORTSTATEMENT:
                permittedFlags.set(HDefinition.StatementFlags.FINAL
                        + HDefinition.StatementFlags.PRIVATE);
                break;

            default:

            }

            HDefinition.StatementFlags flags = new HDefinition.StatementFlags();

            for (int j = 0; j < statementNode.getNumElements() - 1; j++) {

                int flag = 0;

                switch (((Token) statementNode.getElement(j)).kind) {

                case HavaSourceConstants.PRIVATE:
                    flag = HDefinition.StatementFlags.PRIVATE;
                    break;

                case HavaSourceConstants.FINAL:
                    flag = HDefinition.StatementFlags.FINAL;
                    break;

                case HavaSourceConstants.FUNCTION:
                    flag = HDefinition.StatementFlags.FUNCTION;
                    break;

                case HavaSourceConstants.TABLE:
                    flag = HDefinition.StatementFlags.TABLE;
                    break;

                default:

                }

                if (!permittedFlags.test(flag)) {
                    exEnv.setErrorLocation((Token) typedStatementNode.getElement(j));
                    throw new HException(HException.SYNTAX_MESSAGE, exEnv);
                }

                flags.set(flag);

            }

            switch (typedStatementNode.getId()) {

            case HavaSourceTreeConstants.JJTTOKENSTATEMENT:
                for (int j = 1; j < typedStatementNode.getNumElements(); j += 2) {
                    Token token = typedStatementNode.getToken(j);
                    exEnv.setErrorLocation(token);
                    String tokenName = token.image;

                    HDefinition oldDef = definitionHashtable.get(tokenName);
                    HDefinition newDef = new HDefinition.HDefToken(env, tokenName);

                    if (oldDef instanceof HDefinition.HDefToken) {
                        continue;
                    }

                    if (oldDef != null) {
                        duplicateIdentifier(tokenName, exEnv);
                    }

                    definitionHashtable.put(tokenName, newDef);
                }
                break;

            case HavaSourceTreeConstants.JJTSTRUCTURESTATEMENT:
                for (int j = 0; j < typedStatementNode.getNumChildren(); j++) {
                    SimpleNode n = typedStatementNode.getChild(j);
                    Token token = n.getToken(0);
                    exEnv.setErrorLocation(token);
                    String structureName = token.image;

                    HDefinition oldDef = definitionHashtable.get(structureName);
                    HDefinition.HDefStruct newDef = new HDefinition.HDefStruct(env, n, exEnv);

                    if (oldDef instanceof HDefinition.HDefStruct) {
                        if (newDef.match((HDefinition.HDefStruct) oldDef)) {
                            continue;
                        }
                    }

                    if (oldDef != null) {
                        duplicateIdentifier(structureName, exEnv);
                    }

                    definitionHashtable.put(structureName, newDef);
                }
                break;

            case HavaSourceTreeConstants.JJTIMPORTSTATEMENT: {
                boolean isPrivate = env.getCurrentHDImport().isPrivate()
                        | flags.test(HDefinition.StatementFlags.PRIVATE);
                boolean isFinal = env.getCurrentHDImport().isFinal()
                        | flags.test(HDefinition.StatementFlags.FINAL);

                for (int j = 0; j<typedStatementNode.getNumChildren(); j++) {
                    SimpleNode importNode = typedStatementNode.getChild(j);
                    String path = importNode.getToken(0).image;
                    for (int k = 1; k<importNode.getNumElements(); k++) {
                        path += " " + importNode.getToken(k).image;
                    }
                    addToImportQueue(path, isPrivate, isFinal);
                }
                break;
            }

            case HavaSourceTreeConstants.JJTASSIGNMENTSTATEMENT: {
                HDefinition.HDefVar hdv = new HDefinition.HDefVar(env, exEnv, this, flags, typedStatementNode);

                Token token = typedStatementNode.getToken(0);
                exEnv.setErrorLocation(token);
                String variableIdentifier = token.image;

                HDefinition hdef = definitionHashtable.get(variableIdentifier);
                if (hdef == null) {
                    hdef = new HDefinition.HDefVarIdentifier(env, variableIdentifier);
                    definitionHashtable.put(variableIdentifier, hdef);
                }

                if (!(hdef instanceof HDefinition.HDefVarIdentifier)) {
                    duplicateIdentifier(variableIdentifier, exEnv);
                }

                int numFields = hdv.getHDefIndex().getNumFields();
                HDefinition.HDefVarIdentifier hdvi = (HDefinition.HDefVarIdentifier) hdef;
                if (hdvi.getHDefVar(numFields) == null) {
                    hdvi.setHDefVar(numFields, hdv);
                    break;
                }

                String variableRepresentationForError = variableIdentifier;
                if (numFields > 0) {
                    variableRepresentationForError += "(*";
                    for (int j = 1; j < numFields; j++) {
                        variableRepresentationForError += ",*";
                    }
                    variableRepresentationForError += ")";
                }

                if (hdv.isFinal()) {
                    throw new HException(
                            "Cannot override imported final variable \""
                                    + variableRepresentationForError + "\".", exEnv);
                }

                if (hdvi.getHDefVar(numFields).getSource() == env.getCurrentHDImport()) {
                    throw new HException("Duplicate definition of \""
                            + variableRepresentationForError + "\".", exEnv);
                }

                if (!overridesCurrentImportDefinition(hdvi.getHDefVar(numFields).getSource())) {
                    throw new HException("Duplicate imported definitions of \""
                            + variableRepresentationForError + "\".", exEnv);
                }

                hdv.setOverridden();

                hdvi = env.getCurrentHDImport()
                        .getOverriddenVariableIdentifierDefinition(variableIdentifier);

                if (hdvi == null) {
                    hdvi = new HDefinition.HDefVarIdentifier(env, variableIdentifier);
                    env.getCurrentHDImport().addOverriddenVariableIdentifier(hdvi);
                }

                hdvi.setHDefVar(numFields, hdv);
                break;
            }
            }
        }

    }

    /**
     * @throws HException announcing that this identifier is multiply defined.
     */
    static void duplicateIdentifier(final String identifier,
                                    final ExceptionEnvironment exEnv)
            throws HException {

        throw new HException(
                "Duplicate definition of \"" + identifier + "\".", exEnv);

    }

    /**
     * @return true if the given import may override the current import.
     */
    private boolean overridesCurrentImportDefinition(
            HDefinition.HDefImport hdimport) {

        for (HDefinition.HDefImport d = env.getCurrentHDImport();
                d != null; d = d.getParent()) {

            if (d.getParent() == hdimport) {
                return true;
            }

        }

        return false;

    }

    /**
     * Adds an address from an import statement to the import queue.
     * It is placed behind other paths added by current import,
     * but ahead of paths added by earlier imports.
     */
    private void addToImportQueue(final String givenAddress, final boolean isPrivate,
                                  final boolean isFinal) throws HException {

        HSourceAddress a = env.getAddress(givenAddress);

        HDefinition oldDef = getIdentifierDefinition(a.getIdentifier());
        if (oldDef != null) {

            if (!(oldDef instanceof HDefinition.HDefImport)) {
                throw new HException("Import identifier \""
                        + a.getIdentifier() + "\" is already defined.", exEnv);
            }

            HDefinition.HDefImport oldImportDef = (HDefinition.HDefImport) oldDef;

            if (oldImportDef.getPath().equals(a.getPath())
                    && oldImportDef.getFilename().equals(a.getFilename())) {
                return;
            }

            throw new HException("Import identifier \""
                    + a.getIdentifier()
                    + "\" is associated with a different import file.", exEnv);

        }

        HDefinition.HDefImport idef = new HDefinition.HDefImport(env, a,
                isPrivate, isFinal, env.getCurrentHDImport());

        definitionHashtable.put(a.getIdentifier(), idef);
        importQueue.add(numPathsAddedByCurrentImport++, idef);

    }

    /**
     * Pops the next import off the top of the import queue.
     */
    HSourceAddress getNextImportAddress() throws HException {

        if (importQueue.size() == 0) {
            return null;
        }

        HDefinition.HDefImport hdimport = importQueue.get(0);
        importQueue.remove(0);
        numPathsAddedByCurrentImport = 0;

        env.setCurrentHDImport(hdimport);
        return new HSourceAddress(hdimport.getPath(), hdimport.getFilename());

    }

    /**
     * Sets the path part of the root import definition.
     */
    void setDefaultImportPath(final String defaultImportPath) {

        env.getRootHDImport().setPath(defaultImportPath);

    }

    /**
     * Returns the definition corresponding to the given identifier string.
     */
    HDefinition getIdentifierDefinition(final String name) {

        return definitionHashtable.get(name);

    }

    /**
     * Returns a list of simple variables, so they may be evaluated by the engine.
     */
    ArrayList<String> getSimpleVariables() {

        ArrayList<String> a = new ArrayList<String>();

        for (String s : definitionHashtable.keySet()) {

            HDefinition d = getIdentifierDefinition(s);

            if (!(d instanceof HDefinition.HDefVarIdentifier)) {
                continue;
            }

            HDefinition.HDefVarIdentifier svd = (HDefinition.HDefVarIdentifier) d;
            HDefinition.HDefVar vd = svd.getHDefVar(0);

            if (vd == null) {
                continue;
            }

            if (vd.isFunction()) {
                continue;
            }

            a.add(s);

        }

        return a;

    }

    /**
     * Returns the number of stored calculated values.
     */
    int getValueCount() {

        return valueHashtable.size();

    }

    /**
     * Returns the set of indexes of stored calculated values.
     */
    Collection<HReference> getIndexSet() {

        return valueHashtable.keySet();

    }

    /**
     * Stores a stored calculated value.
     */
    void setValue(final HReference x,
                  final HValue v) {

        valueHashtable.put(x, v);

        debug.evaluationCompleted(x, v);

        // Make sure memory is not about to run out.
        if (valueHashtable.size() % 10 == 0) {
            int[] a = new int[10000];
        }

    }

    /**
     * Retrieves a stored calculated value.
     */
    HValue getValue(final HReference x) {

        HValue v = valueHashtable.get(x);

        if (v == null) {
            debug.evaluating(x);
        } else {
            debug.addAssignment(x.toString(), v);
        }

        return v;

    }

}
