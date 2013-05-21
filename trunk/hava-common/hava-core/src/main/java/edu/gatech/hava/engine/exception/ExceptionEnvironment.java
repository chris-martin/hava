package edu.gatech.hava.engine.exception;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.hava.engine.HException;
import edu.gatech.hava.parser.SimpleNode;
import edu.gatech.hava.parser.Token;

/**
 * {@link ExceptionEnvironment} holds values that used to be
 * kept as static variables in {@link HException}.  There is one
 * {@link ExceptionEnvironment} per execution of an HEngine.
 */
public class ExceptionEnvironment {

    private List<ImportRec> importList;
    private Token errorLocation;

    public void setErrorLocation(final SimpleNode n) {

        errorLocation = n.jjtGetFirstToken();

    }

    public void setErrorLocation(final Token t) {

        errorLocation = t;

    }

    void clearErrorLocation() {

        errorLocation = null;

    }

    public void registerImport(final String identifier,
                               final SimpleNode root) {

        if (importList == null) {
            importList = new ArrayList<ImportRec>();
        }

        importList.add(new ImportRec(identifier, root));

    }

    public void reset() {

        importList = null;

    }

    public Token getErrorLocation() {

        return errorLocation;

    }

    public List<ImportRec> getImportList() {

        return importList;

    }

}
