package edu.gatech.hava.engine;

import java.util.Hashtable;

import edu.gatech.hava.engine.exception.ExceptionEnvironment;
import edu.gatech.hava.parser.HavaSourceConstants;
import edu.gatech.hava.parser.SimpleNode;
import edu.gatech.hava.parser.Token;
import edu.gatech.hava.parser.io.HSourceAddress;

/**
 * Java classes that hold definitions specified in the Hava source.
 */
public class HDefinition {

    /**
     * Collects definition attributes PRIVATE, FINAL, FUNCTION and TABLE.
     */
    static class StatementFlags {

        /**
         * Indicates that the definition is private.
         */
        static final int PRIVATE = 1;

        /**
         * Indicates that the definition is final.
         */
        static final int FINAL = 2;

        /**
         * Indicates that the definition is a function.
         */
        static final int FUNCTION = 4;

        /**
         * Indicates that the definition is a table.
         */
        static final int TABLE = 8;

        private int state = 0;

        /**
         * Adds a flag, if it is not already added.
         *
         * @param flag a flag to add
         */
        void set(final int flag) {

            state |= flag;

        }

        /**
         * Determines whether a particular flag is present.
         *
         * @param flag the flag to check
         * @return true if the given flag is present, false otherwise
         */
        boolean test(final int flag) {

            return (state & flag) != 0;

        }

    }

    private String identifier;
    private HDefImport source;
    private HParameters hdefIndex;
    private int[] defRank;

    HDefinition(final String identifier) {

        this.identifier = identifier;

    }

    HDefinition(final DefinitionEnvironment env) {

        source = env.getCurrentHDImport();

        if (source == env.getRootHDImport()) {
            defRank = new int[1];
        } else {
            int[] sourceDefRank = ((HDefinition) source).defRank;
            defRank = new int[sourceDefRank.length + 1];
            System.arraycopy(sourceDefRank, 0, defRank, 0,
                            sourceDefRank.length);
        }

        defRank[defRank.length - 1] = env.getAndIncrementDefCount();

    }

    HDefinition(final DefinitionEnvironment env,
                final String identifier) {

        this(env);

        this.identifier = identifier;

    }

    private HDefinition(final DefinitionEnvironment env,
                        final String identifier,
                        final HParameters indexDefinition) {

        this(env);

        this.identifier = identifier;
        this.hdefIndex = indexDefinition;

    }

    int compareRank(final HDefinition d) {

        int thisRank = defRank != null ? defRank.length : 0;
        int otherRank = d.defRank != null ? d.defRank.length : 0;

        int n = Math.min(thisRank, otherRank);
        for (int i = 0; i < n; i++) {
            int k = defRank[i] - d.defRank[i];
            if (k != 0) {
                return k;
            }
        }
        if (otherRank == n && otherRank == n) {
            return 0;
        }
        if (thisRank > n) {
            return -1;
        }

        return 1;

    }

    /**
     * @return the identifier by which this definition is known.
     */
    String getIdentifier() {

        return identifier;

    }

    /**
     * @return the definition of the source where this definition was read.
     */
    HDefImport getSource() {

        return source;

    }

    /**
     * @return the index for this identifier (if any).
     *         Indexed variables and structures are associated with index definitions.
     */
    HParameters getHDefIndex() {

        return hdefIndex;

    }

    /**
     * Definition of a single token.
     */
    static class HDefToken extends HDefinition {

        /**
         * Instantiates a token definition.
         */
        HDefToken(final String identifier) {

            super(identifier);

        }

        /**
         * Instantiates a token definition.
         */
        HDefToken(final DefinitionEnvironment env,
                  final String identifier) {

            super(env, identifier);

        }

        /**
         * The string representation of a token definition is simply its identifier.
         */
        public String toString() {

            return getIdentifier();

        }

    }

    /**
     * Definition of a single structure.
     */
    static class HDefStruct extends HDefinition {

        private SimpleNode defNode;

        /**
         * Instantiates a structure definition.
         */
        HDefStruct(final DefinitionEnvironment env,
                   final SimpleNode n,
                   final ExceptionEnvironment exEnv)
               throws HException {

            super(env, n.getToken(0).image, new HParameters(n.getChild(0), exEnv));

            defNode = n;

        }

        boolean match(final HDefStruct d) {

            Token t = defNode.jjtGetFirstToken();
            Token tf = defNode.jjtGetLastToken();

            Token dt = d.defNode.jjtGetFirstToken();
            Token dtf = d.defNode.jjtGetLastToken();

            while (true) {

                if (!(t.image.equals(dt.image))) {
                    return false;
                }

                if (t == tf && dt == dtf) {
                    break;
                }

                if (t == tf || dt == dtf) {
                    return false;
                }

                t = t.next;
                dt = dt.next;
            }

            return true;

        }

    }

    /**
     * Definition of a source file (root or imported).
     */
    public static class HDefImport extends HDefinition {

        final HSourceAddress address;

        private boolean isPrivate;
        private boolean isFinal;

        private HDefImport parent;

        private Hashtable<String, HDefVarIdentifier> identifierHashtable =
            new Hashtable<String, HDefVarIdentifier>();

        HDefImport(final DefinitionEnvironment env,
                   final HSourceAddress address,
                   final boolean isPrivate,
                   final boolean isFinal,
                   final HDefImport parent) {

            super(env, address.getIdentifier());

            this.address = address;
            this.isPrivate = isPrivate;
            this.isFinal = isFinal;
            this.parent = parent;

        }

        /**
         * Modifies the path part of this definition. Used to set a default path
         * for imports from the root before actually reading it.
         */
        void setPath(final String path) {

            address.setPath(path);

        }

        /**
         * @return the path part of this source file's address.
         */
        String getPath() {

            return address.getPath();

        }

        /**
         * @return the filename part of this source file's address.
         */
        String getFilename() {

            return address.getFilename();

        }

        /**
         * @return the full uri to this source file
         */
        String getAddress() {

            return address.getAddress();

        }

        /**
         * @return true if this import was designated as private.
         */
        boolean isPrivate() {

            return isPrivate;

        }

        /**
         * @return true if this import was designated as final.
         */
        boolean isFinal() {

            return isFinal;

        }

        /**
         * @return the source into which this source was imported.
         */
        HDefImport getParent() {

            return parent;

        }

        /**
         * Associates an overridden variable identifier with this source.
         */
        void addOverriddenVariableIdentifier(final HDefVarIdentifier vid) {

            identifierHashtable.put(vid.getIdentifier(), vid);

        }

        /**
         * @return an overridden variable identifier definition associated with this source.
         */
        HDefVarIdentifier getOverriddenVariableIdentifierDefinition(final String identifier) {

            return identifierHashtable.get(identifier);

        }

    }

    /**
     * Definition of a Hava function.
     */
    static class HDefFunction extends HDefinition {

        private HFunction function;

        /**
         * Defines a Java HFunction as a Hava function.
         */
        HDefFunction(final DefinitionEnvironment env,
                     final HFunction function) {

            super(env, function.getIdentifier());

            this.function = function;

        }

        /**
         * @return the Java HFunction associated with this Hava function definition.
         */
        HFunction getFunction() {

            return function;

        }

    }

    /**
     * Definition of a Hava iterator.
     */
    static class HDefIterator extends HDefinition {

        private HIterator iterator;

        /**
         * Defines a Java HIterator as a Hava iterator.
         */
        HDefIterator(final DefinitionEnvironment env,
                     final HIterator iterator) {

            super(env, iterator.getIdentifier());

            this.iterator = iterator;

        }

        /**
         * @return the Java HIterator associated with this Hava iterator definition.
         */
        HIterator getIterator() {

            return iterator;

        }

    }

    /**
     * Definition of a variable identifier. For example, the variable identifier
     * of <code>f(x)</code> is "f". More than one variable may be associated
     * with a variable identifier, for example, <code>f)</code>,
     * <code>f(x)</code> and <code>f(x,y)</code>.
     */
    static class HDefVarIdentifier extends HDefinition {

        private HDefVar[] variables;

        /**
         * Creates a variable identifier definition.
         */
        HDefVarIdentifier(final DefinitionEnvironment env,
                          final String identifier) {

            super(env, identifier);

        }

        /**
         * @return the variable definition associates with this variable identifier
         *         and the given number of arguments (or null if none exists).
         */
        HDefVar getHDefVar(final int i) {

            if ((variables == null) || (i >= variables.length)) {
                return null;
            }

            return variables[i];

        }

        /**
         * Associates a variable definition with this variable identifier definition.
         */
        void setHDefVar(final int i,
                        final HDefVar hdv) {

            if (variables == null) {
                variables = new HDefVar[i + 1];
            }

            if (variables.length <= i) {
                HDefVar[] b = new HDefVar[i + 1];
                System.arraycopy(variables, 0, b, 0, variables.length);
                variables = b;
            }

            variables[i] = hdv;

        }

    }

    /**
     * Defines a simple variable, indexed variable or Hava function.
     */
    static class HDefVar extends HDefinition {

        private StatementFlags flags;
        private boolean isOverridden;
        private SimpleNode rhsNode;
        private SymbolTable symbolTable;

        /**
         * Creates a variable definition.
         */
        HDefVar(final DefinitionEnvironment env,
                final ExceptionEnvironment exEnv,
                final SymbolTable symbolTable,
                final StatementFlags flags,
                final SimpleNode statementNode) throws HException {

            super(
                env,
                statementNode.getToken(
                    statementNode.getToken(0).kind == HavaSourceConstants.IDENTIFIER ? 0 : 1
                ).image,
                statementNode.getNumChildren() > 1
                    ? new HParameters(statementNode.getChild(0), exEnv)
                    : new HParameters()
            );

            this.flags = flags;
            this.symbolTable = symbolTable;

            if (getSource().isPrivate()) {
                flags.set(StatementFlags.PRIVATE);
            }

            if (getSource().isFinal()) {
                flags.set(StatementFlags.FINAL);
            }

            rhsNode = statementNode.getChild(statementNode.getNumChildren() - 1);

        }

        /**
         * @return true if this variable was designated as private.
         */
        boolean isPrivate() {

            return flags.test(StatementFlags.PRIVATE);

        }

        /**
         * @return true if this variable was designated as a Hava function.
         */
        boolean isFunction() {

            return flags.test(StatementFlags.FUNCTION);

        }

        /**
         * @return true if this variable was designated as final.
         */
        boolean isFinal() {

            return flags.test(StatementFlags.FINAL);

        }

        /**
         * @return true if this variable has been overridden.
         */
        boolean isOverridden() {

            return isOverridden;

        }

        /**
         * @return true if this variable has been designated a table.
         *         This affects default output formatting only.
         */
        boolean isTable() {

            return flags.test(StatementFlags.TABLE);

        }

        /**
         * Marks this variable as overridden.
         */
        void setOverridden() {

            isOverridden = true;

        }

        /**
         * Provided as a convenience to permit the report generator to look up
         * values associated with indexed variable instances.
         *
         * @return the SymbolTable instance where this variable was declared.
         */
        SymbolTable getSymbolTable() {

            return symbolTable;

        }

        /**
         * @return the parser node for this variable's definition, so it can be evaluated.
         */
        SimpleNode getRhsNode() {

            return rhsNode;

        }

    }

}
