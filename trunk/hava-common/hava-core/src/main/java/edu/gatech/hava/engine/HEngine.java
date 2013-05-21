package edu.gatech.hava.engine;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.gatech.hava.engine.HDefinition.HDefImport;
import edu.gatech.hava.engine.debug.DebugListeners;
import edu.gatech.hava.engine.debug.HDebugListener;
import edu.gatech.hava.engine.exception.ExceptionEnvironment;
import edu.gatech.hava.engine.progress.HProgressMonitor;
import edu.gatech.hava.engine.progress.ProgressMonitors;
import edu.gatech.hava.lib.Functions;
import edu.gatech.hava.parser.DefaultSourceTreeProvider;
import edu.gatech.hava.parser.HSourceTreeProvider;
import edu.gatech.hava.parser.ParseException;
import edu.gatech.hava.parser.SimpleNode;
import edu.gatech.hava.parser.io.HSourceAddress;
import edu.gatech.hava.report.HReportGenerator;
import edu.gatech.hava.report.simple.SimpleReportGenerator;

/**
 * Public API for the Hava calculator.
 */
public class HEngine {

    private SymbolTable symbolTable;
    private Evaluator evaluator;
    private String address;
    private HSourceTreeProvider sourceTreeProvider;
    private final DebugListeners debug = new DebugListeners();
    private final ProgressMonitors progressMonitor = new ProgressMonitors();
    private final ExceptionEnvironment exEnv = new ExceptionEnvironment();

    /**
     * Creates a new engine having no default import address.
     */
    public HEngine() {
        reset();
    }

    /**
     * Creates a new engine having the given default import address.
     */
    public HEngine(final String address) {

        this.address = address;
        reset();

    }

    /**
     * Clears all definitions and values from the engine, and sets a new default
     * import address.
     */
    public void reset(final String address) {

        this.address = address;
        reset();

    }

    /**
     * Clears all definitions and values from the engine, while retaining the
     * current default import address.
     */
    public void reset() {

        exEnv.reset();
        evaluator = null;
        symbolTable = new SymbolTable(address, debug, exEnv);

    }

    protected void finalize() throws Throwable {

        reset();
        symbolTable = null;
        super.finalize();

    }

    /**
     * Extends Hava to recognize a function written in Java.
     *
     * @param function a new function to introduce to Hava
     */
    public void registerFunction(final HFunction function) {

        symbolTable.registerFunction(function);

    }

    /**
     * Extends Hava to recognize am iterator written in Java.
     *
     * @param iterator a new iterator to introduce to Hava
     */
    public void registerIterator(final HIterator iterator) {

        symbolTable.registerIterator(iterator);

    }

    private void doLoad(final String source) throws HException {

        try {
            doLoad(new StringReader(source));
        } catch (final IOException x) {
            throw new HException("Import failure.", exEnv);
        }

    }

    /**
     * Loads the given Hava source.
     */
    public void load(final String source) throws HException {

        doLoad(source);

    }

    private void doLoad(final Reader reader) throws HException, IOException {

        try {
            load(getSourceTree(reader));
        } catch (final ParseException pe) {
            throw new HException(pe, exEnv, getCurrentFile());
        } catch (final HException he) {
            he.setFile(getCurrentFile());
            throw he;
        }

    }

    /**
     * Loads the given Hava source.
     */
    public void load(final Reader reader) throws HException, IOException {

        doLoad(reader);

    }

    private String getCurrentFile() {

        final DefinitionEnvironment de = symbolTable.getDefinitionEnvironment();
        final String file = de.getCurrentHDImport().getAddress();

        return file;

    }

    private SimpleNode getSourceTree(final Reader reader)
            throws HException, ParseException, IOException {

        return getSourceTreeProvider().getSourceTree(reader);

    }

    private SimpleNode getSourceTree(final HSourceAddress address) throws IOException, ParseException {

        return getSourceTreeProvider().getSourceTree(address);

    }

    public void load(final HSourceAddress address) throws HException, IOException, ParseException {

        load(getSourceTree(address));

    }

    /**
     * Loads the given Hava source.
     */
    private synchronized void load(SimpleNode havaSource) throws HException, IOException {

        try {

            symbolTable.load(havaSource);

            final Set<HSourceAddress> importPathSet = new HashSet<HSourceAddress>();

            HSourceAddress importAddress;
            while ((importAddress = symbolTable.getNextImportAddress()) != null) {

                if (importPathSet.contains(importAddress)) {
                    continue;
                }

                importPathSet.add(importAddress);

                havaSource = getSourceTree(importAddress);
                symbolTable.load(havaSource);

            }

        } catch (final HException x) {
            throw x;
        } catch (final IOException x) {
            throw x;
        } catch (final Throwable x) {
            throw new HException(x, exEnv);
        }

    }

    protected HSourceTreeProvider getSourceTreeProvider() {

        if (sourceTreeProvider == null) {
            sourceTreeProvider = createDefaultSourceTreeProvider();
        }

        return sourceTreeProvider;

    }

    protected HSourceTreeProvider createDefaultSourceTreeProvider() {

        return new DefaultSourceTreeProvider();

    }

    public void setSourceTreeProvider(final HSourceTreeProvider sourceTreeProvider) {

        this.sourceTreeProvider = sourceTreeProvider;

    }

    /**
     * Evaluates all non-indexed variables of the current Hava program.
     */
    public void run() throws HException {

        try {

            HDefinition rd = symbolTable.getIdentifierDefinition("REPLICATING");

            if (rd != null && (rd instanceof HDefinition.HDefToken)) {
                Functions.resetRandom(42);
            } else {
                Functions.resetRandom();
            }

            ArrayList<String> a = symbolTable.getSimpleVariables();

            Collections.sort(a, new Comparator<String>() {

                public int compare(final String a, final String b) {
                    return symbolTable.getIdentifierDefinition(a).compareRank(
                            symbolTable.getIdentifierDefinition(b));
                }

            });

            evaluator = new Evaluator(symbolTable, debug, progressMonitor, exEnv);

            debug.start();

            for (String s : a) {

                HDefinition d = symbolTable.getIdentifierDefinition(s);
                HDefinition.HDefVar vdef = ((HDefinition.HDefVarIdentifier) d).getHDefVar(0);
                HReference x = new HReference(vdef, HValue.EMPTYLIST,
                        symbolTable.getDefinitionEnvironment());

                if (symbolTable.getValue(x) != null) {
                    continue;
                }

                HValue v = evaluator.evaluateExpression(vdef.getRhsNode());
                symbolTable.setValue(x, v);

            }

            evaluator = null;

        } catch (final HException x) {
            debug.errorEncountered(x);
            throw x;
        } catch (final Throwable x) {
            HException h = new HException(x, exEnv);
            debug.errorEncountered(h);
            throw h;
        }
        debug.finish();

    }

    /**
     * Causes run to stop calculating and return.
     */
    public void abort() {

        progressMonitor.abort();

    }

    public void addProgressMonitor(final HProgressMonitor monitor) {

        progressMonitor.addMonitor(monitor);

    }

    public void removeProgressMonitor(final HProgressMonitor monitor) {

        progressMonitor.removeMonitor(monitor);

    }

    /**
     * @return the number of calculated values (for use by progress bar).
     */
    public int getValueCount() {

        return symbolTable.getValueCount();

    }

    /**
     * @return the list of non-private variable references for which values have
     *         been calculated (for use by custom report generator).
     */
    public List<HReference> getVariableReferenceSet() {

        List<HReference> a = new ArrayList<HReference>();

        if (symbolTable == null) {
            return a;
        }

        for (HReference x : symbolTable.getIndexSet()) {

            if (x instanceof HDocComment) {

                HDefinition d = x.getHDefinitionForRank();

                HDefImport source = d.getSource();
                if (source.isPrivate()) {
                    continue;
                }

            } else {

                HDefinition.HDefVar d = x.getIValue().getVariableDefinition();

                if (d.isPrivate()) {
                    continue;
                }

            }

            a.add(x);
        }

        return a;

    }

    /**
     * Generates a report, using the default generator.
     */
    public void generateReport(final Writer reportWriter)
            throws IOException {

        final HReportGenerator generator = getReportGenerator();

        generator.write(reportWriter);

    }

    public HReportGenerator getReportGenerator() {

        final HReportGenerator generator =
            new SimpleReportGenerator(getVariableReferenceSet());

        return generator;

    }

    /**
     * Creates an HValue of type token.
     *
     * @return the new HValue, or null (if the given identifier is not
     *         defined as a token).
     */
    public HValue createTokenValue(final String identifier) {

        HDefinition def = symbolTable.getIdentifierDefinition(identifier);

        if (def instanceof HDefinition.HDefToken) {
            return new HValue((HDefinition.HDefToken) def);
        }

        return null;

    }

    /**
     * Creates an HValue of type structure.
     *
     * @return the new HValue, or null (if the given identifier is not
     *         defined as a structure).
     */
    public HValue createStructValue(final String identifier,
                                    final HValue[] a) {

        HDefinition def = symbolTable.getIdentifierDefinition(identifier);

        if (def instanceof HDefinition.HDefStruct) {
            return new HValue((HDefinition.HDefStruct) def, new HValue(a));
        }

        return null;

    }

    /**
     * Creates a reference to a simple variable, from which its value may
     * be obtained.
     *
     * @return the new reference, or null (if the given identifier is not
     *         defined as a simple variable).
     */
    public HReference createReference(final String identifier) {

        return createReference(identifier, new HValue[0]);

    }

    /**
     * Creates a reference to an indexed variable, from which its value may
     * be obtained.
     *
     * @return the new reference, or null (if the given identifier is not
     *         defined as an indexed variable with the corresponding number
     *         of arguments).
     */
    public HReference createReference(String identifier,
                                      final HValue[] a) {

        HDefinition def;

        int k = identifier.indexOf(".");

        if (k > 0) {

            String importIdentifier = identifier.substring(0, k);
            identifier = identifier.substring(k + 1);

            def = symbolTable.getIdentifierDefinition(importIdentifier);

            if (!(def instanceof HDefinition.HDefImport)) {
                return null;
            }

            def = ((HDefinition.HDefImport) def)
                    .getOverriddenVariableIdentifierDefinition(identifier);

        } else {

            def = symbolTable.getIdentifierDefinition(identifier);

        }

        if (!(def instanceof HDefinition.HDefVarIdentifier)) {
            return null;
        }

        HDefinition.HDefVar hdv = ((HDefinition.HDefVarIdentifier) def).getHDefVar(a.length);

        if (hdv == null) {
            return null;
        }

        return new HReference(hdv, new HValue(a),
                symbolTable.getDefinitionEnvironment());

    }

    public void addDebugListener(final HDebugListener listener) {

        debug.add(listener);

    }

    public void removeDebugListener(final HDebugListener listener) {

        debug.remove(listener);

    }

}
