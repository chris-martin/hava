package edu.gatech.hava.engine;

import edu.gatech.hava.engine.HDefinition.HDefImport;
import edu.gatech.hava.engine.HDefinition.HDefVar;
import edu.gatech.hava.parser.SimpleNode;
import edu.gatech.hava.parser.Token;

/**
 * Label associated with an assigned value, for example, "pi" or "f(x, y)".
 *
 * It consists of the variable identifier (a String), along with an
 * index (an inherited HValue of type list), which may be empty.
 *
 * HReference is a subclass of HValue only because it is implemented as a
 * list of arguments to which the identifier name has been added.
 */
public class HReference extends HValue {

    private Boolean rootImport;

    HReference(final HDefinition.HDefVar hdv,
               final HValue v,
               final DefinitionEnvironment env) {

        super(hdv, v);

        rootImport = getHDefVar().getSource() == env.getRootHDImport();

    }

    /**
     * Back door for DocComment.
     */
    HReference() {
    }

    /**
     * Returns the variable identifier. For example, the variable reference
     * <code>f(1, 2)</code> has identifier <code>"f"</code>.
     */
    public String getVariableIdentifier() {

        return getHDefVar().getIdentifier();

    }

    /**
     * Exposes an immutable token representation.  For example, if this
     * HReference points to an <code>if</code> block, the HReferenceToken
     * returned will have a String representation of <code>"if"</code> and
     * contain the whitespace-agnostic source code location of the referenced
     * <code>"if"</code>.
     *
     * @see HReferenceToken
     * @return The first HReferenceToken parsed in the original source
     * represented by HReference.
     */
    //TODO TODO TODO not sure if whitespace comment is true
    public HReferenceToken getReferenceToken() {

        final HDefVar defVar = getHDefVar();
        final SimpleNode simpleNode = defVar.getRhsNode();
        final Token token = simpleNode.jjtGetFirstToken();

        return new HReferenceToken(token);

    }

    /**
     * Returns the name of the import file where this variable is defined.
     */
    public String getImportIdentifier() {

        if (Boolean.TRUE.equals(rootImport)) {
            return "";
        }

        return getHDefVar().getSource().getIdentifier();

    }

    protected HDefImport getSource() {

        return getHDefVar().getSource();

    }

    public String getAddress() {

        return getSource().getAddress();

    }

    /**
     * Returns the value assigned to this variable reference.
     */
    public HValue getValue() {

        final HDefVar defVar = getHDefVar();

        return defVar != null ? defVar.getSymbolTable().getValue(this) : null;

    }

    /**
     * Returns true if this variable reference is overridden.
     */
    public boolean isOverridden() {

        return getHDefVar().isOverridden();

    }

    /**
     * Returns true if this variable reference was defined with a
     * <code>table</code> prefix.
     */
    public boolean isTable() {

        return getHDefVar().isTable();

    }

    /**
     * Provides a representation of this variable reference in the same format
     * that appears in the demo report. For example, the variable reference
     * <code>f(1, 2)</code> has string representation <code>"f(1, 2)"</code>.
     */
    public String toString() {

        HDefinition.HDefVar hdv = getHDefVar();

        return (hdv.isOverridden() ? hdv.getSource().getIdentifier() + "." : "")
                + hdv.getIdentifier()
                + (getNumElements() > 0 ? super.toString() : "");

    }

    HDefinition.HDefVar getHDefVar() {

        final HVReference vReference = getIValue();

        return vReference != null ? vReference.getVariableDefinition() : null;

    }

    HDefinition getHDefinitionForRank() {

        return getHDefVar();

    }

    public boolean isPrivate() {

        return getHDefVar().isPrivate();

    }

}
