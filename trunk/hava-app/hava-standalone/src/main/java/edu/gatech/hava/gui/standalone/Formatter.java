package edu.gatech.hava.gui.standalone;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.hava.parser.HavaSource;
import edu.gatech.hava.parser.HavaSourceConstants;
import edu.gatech.hava.parser.HavaSourceTreeConstants;
import edu.gatech.hava.parser.Node;
import edu.gatech.hava.parser.SimpleNode;
import edu.gatech.hava.parser.Token;

public class Formatter {

    private String s;

    private Token theAssign;
    private Token priorToken;
    private List<Token> theOperators;

    public Formatter(final String source, final int width) {

        s = source;

        try {
            StringReader sr = new StringReader(source);
            HavaSource havaSource = new HavaSource(sr);
            SimpleNode root = havaSource.Start();
            s = prettify(root);
        } catch (final Throwable x) {
            ;
        }

    }

    public String toString() {

        return s;

    }

    private String prettify(final SimpleNode n) {

        List<Token> savedOperators = null;

        if (isAssignment(n)) {

            // Context is assignment statement, so record which token is the ASSIGN.
            for (int i = 0; i < n.getNumElements(); i++) {
                if ((n.getElement(i) instanceof Token)
                        && (((Token) n.getElement(i)).kind == HavaSourceConstants.ASSIGN)) {
                    theAssign = (Token) n.getElement(i);
                    break;
                }
            }

        } else if (isExpression(n)) {

            // Context is arithmetic expression, so see if we must record the list of operators.
            savedOperators = theOperators;
            if (theOperators == null) {
                if (!isLonelyNode(n)) {
                    theOperators = new ArrayList<Token>();
                    for (int i = 0; i < n.getNumElements(); i++) {
                        if (n.getElement(i) instanceof Token) {
                            Token t = (Token) n.getElement(i);
                            if (isOperator(t)) {
                                theOperators.add(t);
                            }
                        }
                    }
                }
            }

        }

        // Build up result string.
        String result = "";
        if (n.getNumElements() != 0) {

            Object e = n.getElement(0);

            if ((n.getNumElements() == 1) && (e instanceof Node)) {

                // On Node with single Node element, recurse.
                return prettify((SimpleNode) e);

            } else {

                // Prettify Node and Token elements.
                for (int i = 0; i < n.getNumElements(); i++) {
                    if (n.getElement(i) instanceof Node) {
                        SimpleNode child = (SimpleNode) n.getElement(i);
                        result += prettify(child);
                    } else {
                        Token t = (Token) n.getElement(i);
                        if (t.specialToken != null) {
                            result += findLeadingComments(t);
                        }
                        result += prettify(t);
                        // Retrieve any comments that belong on this line.
                        result += findTrailingComments(t);
                        if (t.kind == HavaSourceConstants.SEMICOLON) {
                            result += "\n";
                        }
                        // Save as "prior" token for backward check on comments.
                        priorToken = t;
                    }
                }

            }

        }

        if (isAssignment(n)) {
            theAssign = null;
        } else if (isExpression(n)) {
            // End of arithmetic context.
            theOperators = savedOperators;
        }

        return result;

    }

    private boolean isAssignment(final SimpleNode n) {

        return n.getId() == HavaSourceTreeConstants.JJTASSIGNMENTSTATEMENT;

    }

    private boolean isExpression(final SimpleNode n) {

        return ((n.getId() == HavaSourceTreeConstants.JJTADDEXPRESSION)
                || (n.getId() == HavaSourceTreeConstants.JJTMULTIPLYEXPRESSION)
                || (n.getId() == HavaSourceTreeConstants.JJTOREXPRESSION)
                || (n.getId() == HavaSourceTreeConstants.JJTANDEXPRESSION)
                || (n.getId() == HavaSourceTreeConstants.JJTCOMPAREEXPRESSION));

    }

    // Return whether the passed Node has only one child - also a Node.
    private boolean isLonelyNode(final SimpleNode n) {

        return (n.getNumElements() == 1) && (n.getElement(0) instanceof Node);

    }

    // Return any trailing comments that may have been attached to
    // the following Node by the parser.
    private String findTrailingComments(final Token t) {

        String result = "";

        // If there are any, trailing comments are attached to the following
        // token as special tokens.
        if (t.next != null) {

            Token st = t.next.specialToken;

            // Walk the SpecialNode list, building up the comments in reverse of listed order.
            while (st != null) {
                // Such comments will have the same line number as the passed-in token.
                if ((st.beginLine == t.beginLine)
                            && (
                                (st.kind == HavaSourceConstants.ESCAPED_COMMENT)
                                    || (st.kind == HavaSourceConstants.ESCAPED_COMMENT)
                            )
                        ) {
                    result = st.image + result;
                }
                st = st.specialToken;
            }

            if (!result.equals("")) {
                // Hack: Do some formatting right here.
                result = " " + result + " ";
            }

        }

        return result;

    }

    // Return any leading comments, excluding anything in the Special Token list
    // that belongs to the previous node.
    private String findLeadingComments(final Token t) {

        String comments = "";
        Token st = t.specialToken;

        while (st != null) {

            // Skip any comments that belong on prior lines.
            if ((priorToken == null)
                    || (st.beginLine != priorToken.beginLine)) {
                if (st.kind == HavaSourceConstants.POSSIBLE_BLANK_LINE) {
                    if (st.beginColumn == 1) {
                        comments = "\n" + comments;
                    }
                } else if (st.kind == HavaSourceConstants.ESCAPED_COMMENT) {
                    comments = st.image + "\n" + comments;
                } else if (st.kind == HavaSourceConstants.SINGLE_LINE_COMMENT) {
                    comments = st.image + "\n" + comments;
                }
            }

            st = st.specialToken;

        }

        return comments;

    }

    // Prettify a token.
    // Frequently depends on looking ahead one token.
    private String prettify(final Token t) {

        // Very special tokens.
        switch (t.kind) {

        case HavaSourceConstants.EOF:
            return "";

        case HavaSourceConstants.SEMICOLON:
            String result = t.image;
            return result;

        case HavaSourceConstants.DOC_COMMENT:   // LKP
            return t.image + "\n";              // LKP

            // Tokens which by default are not surrounded by spaces.
        case HavaSourceConstants.INT:
        case HavaSourceConstants.REAL:
            if (inTheOperators(t.next)) {
                return t.image + " ";
            }
            if (isKeyWord(t.next)) {
                return t.image + " ";
            }
            if (t.next.kind == HavaSourceConstants.BAR) {
                return t.image + " ";
            }
            if (isLeftHugging(t.next)) {
                return t.image;
            }
            return t.image;

        case HavaSourceConstants.IDENTIFIER:
            if (isLeftHugging(t.next)) {
                return t.image;
            }
            if (isTheAssign(t.next)) {
                return t.image + " ";
            }
            if (isKeyWord(t.next)) {
                return t.image + " ";
            }
            if (inTheOperators(t.next)) {
                return t.image + " ";
            }
            if (t.next.kind == HavaSourceConstants.BAR) {
                return t.image + " ";
            }
            return t.image;

        case HavaSourceConstants.IMPORT_ADDRESS_SEGMENT:
            return t.image;

        case HavaSourceConstants.COLON:
        case HavaSourceConstants.DOT:
        case HavaSourceConstants.LBRACE:
        case HavaSourceConstants.LBRACKET:
        case HavaSourceConstants.LPAREN:
        case HavaSourceConstants.PWR:
            return t.image;

        case HavaSourceConstants.ADD:
        case HavaSourceConstants.SUB:
        case HavaSourceConstants.MUL:
        case HavaSourceConstants.DIV:
        case HavaSourceConstants.EQ:
        case HavaSourceConstants.NE:
        case HavaSourceConstants.GT:
        case HavaSourceConstants.LT:
        case HavaSourceConstants.GE:
        case HavaSourceConstants.LE:
        case HavaSourceConstants.AND:
        case HavaSourceConstants.OR:
        case HavaSourceConstants.NOT:
            if (inTheOperators(t)) {
                return t.image + " ";
            }
            return t.image;

        case HavaSourceConstants.ASSIGN:
            // Check for assignment operator context.
            if (isTheAssign(t)) {
                return t.image + " ";
            }
            return t.image;

            // Tokens which default to being surrounded by spaces
        case HavaSourceConstants.RBRACE:
        case HavaSourceConstants.RBRACKET:
        case HavaSourceConstants.RPAREN:
            if (isLeftHugging(t.next)) {
                return t.image;
            }
            if (isTheAssign(t.next)) {
                return t.image + " ";
            }
            if (isOperator(t.next)) {
                if (inTheOperators(t.next)) {
                    return t.image + " ";
                } else {
                    return t.image;
                }
            }
            if (t.next.kind == HavaSourceConstants.BAR) {
                return t.image + " ";
            }
            return t.image + " ";

        case HavaSourceConstants.BAR:
            return t.image + " ";

        case HavaSourceConstants.COMMA:
            return t.image + " ";

        case HavaSourceConstants.FALSE:
        case HavaSourceConstants.TRUE:
            if (isLeftHugging(t.next)) {
                return t.image;
            }
            return t.image + " ";

        default:
            if (isKeyWord(t)) {
                return t.image + " ";
            }
            return null;

        }

    }

    // Return whether or not the passed token is the assignment token for
    // an assignment statement.
    private boolean isTheAssign(final Token token) {

        return ((token != null) && (token == theAssign));

    }

    // Return whether or not the passed token is in the first-level list of operators.
    private boolean inTheOperators(final Token token) {

        if (theOperators != null) {
            for (int i = 0; i < theOperators.size(); i++) {
                if (theOperators.get(i) == token) {
                    return true;
                }
            }
        }

        return false;

    }

    // Return whether or not the passed token follows the previous token with no spacing.
    private boolean isLeftHugging(final Token t) {

        return ((t != null) && (t.kind == HavaSourceConstants.COLON)
                || (t.kind == HavaSourceConstants.COMMA)
                || (t.kind == HavaSourceConstants.DOT)
                || (t.kind == HavaSourceConstants.LPAREN)
                || (t.kind == HavaSourceConstants.LBRACKET)
                || (t.kind == HavaSourceConstants.PWR)
                || (t.kind == HavaSourceConstants.RPAREN)
                || (t.kind == HavaSourceConstants.RBRACKET)
                || (t.kind == HavaSourceConstants.RBRACE)
                || (t.kind == HavaSourceConstants.SEMICOLON));

    }

    // Return whether or not the passed token is an operator.
    private boolean isOperator(final Token t) {

        return ((t != null) && (t.kind == HavaSourceConstants.ADD)
                || (t.kind == HavaSourceConstants.SUB)
                || (t.kind == HavaSourceConstants.MUL)
                || (t.kind == HavaSourceConstants.DIV)
                || (t.kind == HavaSourceConstants.EQ)
                || (t.kind == HavaSourceConstants.NE)
                || (t.kind == HavaSourceConstants.GT)
                || (t.kind == HavaSourceConstants.LT)
                || (t.kind == HavaSourceConstants.GE)
                || (t.kind == HavaSourceConstants.LE)
                || (t.kind == HavaSourceConstants.AND)
                || (t.kind == HavaSourceConstants.OR));

    }

    // Return whether or not the passed token is a keyword.
    private boolean isKeyWord(final Token t) {

        return (t != null) && (t.kind == HavaSourceConstants.ELSE)
                || (t.kind == HavaSourceConstants.FINAL)
                || (t.kind == HavaSourceConstants.FUNCTION)
                || (t.kind == HavaSourceConstants.HTOKEN)
                || (t.kind == HavaSourceConstants.IF)
                || (t.kind == HavaSourceConstants.IN)
                || (t.kind == HavaSourceConstants.PRIVATE)
                || (t.kind == HavaSourceConstants.STRUCTURE)
                || (t.kind == HavaSourceConstants.TABLE)
                || (t.kind == HavaSourceConstants.TO)
                || (t.kind == HavaSourceConstants.IMPORT);

    }

}
