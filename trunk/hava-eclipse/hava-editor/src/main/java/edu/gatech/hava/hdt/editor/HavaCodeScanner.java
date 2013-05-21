package edu.gatech.hava.hdt.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/**
 * The home for all Hava non-partition-based syntax coloring.  Partition-based
 * coloring, like coloring for doc-comments and multi-line-comments, can be
 * found in HavaConfiguration and HavaPartitionScanner.
 *
 * @see HavaPartitionScanner, HavaConfiguration,HavaDecoumentSetupParticipant
 */
public class HavaCodeScanner extends RuleBasedScanner {

    /**
     * Stolen from HavaSourceConstants, needs to be retrieved legitimately (but
     * not right now).
     */
    private String[] sourceConstants = {
            "<EOF>",
            "\" \"",
            "\"\\t\"",
            "<POSSIBLE_BLANK_LINE>",
            "<SINGLE_LINE_COMMENT>",
            "<ESCAPED_COMMENT>",
            "\";\"",
            "\",\"",
            "<IMPORT_ADDRESS_SEGMENT>",
            "\"else\"",
            "\"false\"",
            "\"final\"",
            "\"function\"",
            "\"if\"",
            "\"in\"",
            "\"private\"",
            "\"struct\"",
            "\"table\"",
            "\"to\"",
            "\"token\"",
            "\"true\"",
            "\"import\"",
            "<IDENTIFIER>",
            "<INT>",
            "<REAL>",
            "<EXPONENT>",
            "<LETTER>",
            "<DIGIT>",
            "\"|\"",
            "\".\"",
            "\"{\"",
            "\"[\"",
            "\"(\"",
            "\"}\"",
            "\"]\"",
            "\")\"",
            "\"=\"",
            "\":\"",
            "\"+\"",
            "\"-\"",
            "\"*\"",
            "\"/\"",
            "\"^\"",
            "\">\"",
            "\"<\"",
            "\"<=\"",
            "\">=\"",
            "\"==\"",
            "\"!=\"",
            "\"||\"",
            "\"&&\"",
            "\"!\"",
            "<DOC_COMMENT>",
          };

    private String[] reserved;

    //private String[] operators;

    private String[] functions = {"ceiling", "exp", "floor",
                                  "ln", "random", "round"};

    private String[] iterators = {"argmax", "argmin", "collect",
                                  "first", "for", "join", "last",
                                  "max", "min", "sum"};

    private String[] fields = {"listSize", "structSize",
                               "structType", "valueType"};

    private String[] tokens = {"BLANK", "BOOLEAN", "ERROR", "IGNORE", "INTEGER",
                               "LIST", "REAL", "REPLICATING",
                               "STRUCTURE", "TOKEN"};

    private String[] literals = {"true", "false"};

    private Set<Character> reservedStarts;

    private Set<Character> reservedParts;

    /**
     * Constructor.
     */
    public HavaCodeScanner() {

        super();

        // data structures for isReservedPart and Start
        reservedParts = new HashSet<Character>();
        reservedStarts = new HashSet<Character>();

        // TODO HACKALERT, see whole class for more details =)
        // pulls reserved words and operators from our master array
        List<String> operatorsList = new ArrayList<String>();
        List<String> reservedList = new ArrayList<String>();

        for (String s : sourceConstants) {

            char first = s.charAt(0);
            if (first == '<') {
                continue;
            }

            String stripped = s.substring(1, s.length() - 1);
            char[] strArray = stripped.toCharArray();

            boolean rsvd = true, used = true;
            for (char c : strArray) {
                if (!Character.isLetter(c)) {
                    rsvd = false;
                }
                // removes all whitespace and non-operator special characters
                if (Character.isWhitespace(c) || c == ';' || c == ',') {
                    used = false;
                    break;
                }
            }

            if (used) {
                if (rsvd) {
                    reservedList.add(new String(stripped));
                    reservedStarts.add(strArray[0]);
                    for (int i = 1; i < strArray.length; i++) {
                        reservedParts.add(strArray[i]);
                    }
                } else {
                    operatorsList.add(new String(stripped));
                }
            }

        }

        this.reserved = reservedList.toArray(new String []{});
        /* Just in case, we'll see if these are necessary down the road -
         * I think they will be. */
        //this.operators = operatorsList.toArray(new String []{});

        // and on to syntax coloring!
        Token singleComment = new Token(HavaTextAttributes.MULTI_LINE_COMMENT);
        Token reservedWord = new Token(HavaTextAttributes.RESERVED_WORD);
        Token iterator = new Token(HavaTextAttributes.ITERATOR);
        Token function = new Token(HavaTextAttributes.FUNCTION);
        Token field = new Token(HavaTextAttributes.FIELD);
        Token token = new Token(HavaTextAttributes.TOKEN);
        Token literal = new Token(HavaTextAttributes.LITERAL);

        setRules(new IRule[] {
                // Add rule for single line comments.
                new SingleLineRule("http://", ";", new Token(IDocument.DEFAULT_CONTENT_TYPE)),
                new EndOfLineRule("//", singleComment),
                getWordRule(reserved, reservedWord),
                getWordRule(iterators, iterator),
                getWordRule(functions, function),
                getWordRule(fields, field),
                getWordRule(tokens, token),
                getWordRule(literals, literal)
        });

    }

    /**
     *
     * @param words A String array of all words to be added to this rule.
     *              IWordDetector.isWordPart(char c) and isWordStart(char c)
     *              are both derived from these strings.
     * @param t The token which all strings in the words list will return.
     * @return Returns a word rule with a valid IWordDetector for each
     *         string in the words array.
     */
    private WordRule getWordRule(final String[] words, final Token t) {

        final Set<Character> starts = new HashSet<Character>(),
                          parts = new HashSet<Character>();

        WordRule rule = new WordRule(new IWordDetector() {

            @Override
            public boolean isWordPart(final char c) {
                return parts.contains(c);
            }

            @Override
            public boolean isWordStart(final char c) {
                return starts.contains(c);
            }

        });

        for (String word : words) {
            char[] strArray = word.toCharArray();
            starts.add(strArray[0]);
            for (int i = 1; i < strArray.length; i++) {
                parts.add(strArray[i]);
            }
            rule.addWord(word, t);
        }

        return rule;

    }

}
