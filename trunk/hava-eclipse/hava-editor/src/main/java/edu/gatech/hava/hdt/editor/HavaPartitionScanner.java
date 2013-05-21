package edu.gatech.hava.hdt.editor;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public class HavaPartitionScanner extends RuleBasedPartitionScanner {

    // not worth using an enum, following example set by jdt
    public static final String MULTI_LINE_COMMENT = "__multi_line_comment";
    public static final String DOC_COMMENT = "__doc_comment";

    public static final String[] HAVA_PARTITION_TYPES = new String[] {
            MULTI_LINE_COMMENT,
            DOC_COMMENT
    };

    public HavaPartitionScanner() {

        super();

        Token multiComment = new Token(MULTI_LINE_COMMENT),
              docComment = new Token(DOC_COMMENT);

        setPredicateRules(new IPredicateRule[] {
                // Add special case word rule.
                new WordPredicateRule(multiComment),
                new MultiLineRule("/**", "*/", docComment, (char) 0, true),
                new MultiLineRule("/*", "*/", multiComment, (char) 0, true)
        });

    }

    static class WordPredicateRule extends WordRule implements IPredicateRule {

        private IToken fSuccessToken;

        public WordPredicateRule(final IToken successToken) {

            super(new IWordDetector() {

                @Override
                public boolean isWordPart(final char c) {
                    return (c == '*' || c == '/');
                }

                @Override
                public boolean isWordStart(final char c) {
                    return (c == '/');
                }

            });

            fSuccessToken = successToken;
            addWord("/**/", fSuccessToken);

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IToken evaluate(final ICharacterScanner scanner,
                               final boolean resume) {

            return super.evaluate(scanner);

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IToken getSuccessToken() {

            return fSuccessToken;

        }

    }

}
