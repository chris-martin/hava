package edu.gatech.hava.hdt.editor;

import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * Contains the configuration for syntax coloring, text hover capabilities, and
 * content assist for the Hava language.  Currently, the configuration, and
 * thus, the editor, only supports syntax coloring.
 */
public class HavaConfiguration extends SourceViewerConfiguration {

    /**
     * @param sourceViewer The ISourceViewer to be configured.
     * @return Returns the IPresentationReconciler configured to color Hava
     *         syntax.
     */
    @Override
    public final IPresentationReconciler getPresentationReconciler(
            final ISourceViewer sourceViewer) {

        final PresentationReconciler reconciler = new PresentationReconciler();

        ITokenScanner scanner;

        // Present non-partitioned text as Hava code.
        scanner = new HavaCodeScanner();
        setDamagerRepairer(reconciler, scanner, IDocument.DEFAULT_CONTENT_TYPE);

        /* Add a single token scanner to the doc comment partition,
         * which will re-color all partition contents on the fly. */
        scanner = new SingleTokenScanner(HavaTextAttributes.SINGLE_LINE_COMMENT);
        setDamagerRepairer(reconciler, scanner, HavaPartitionScanner.DOC_COMMENT);

        // Do the same for multi-line comments.
        scanner = new SingleTokenScanner(HavaTextAttributes.MULTI_LINE_COMMENT);
        setDamagerRepairer(reconciler, scanner, HavaPartitionScanner.MULTI_LINE_COMMENT);

        return reconciler;

    }

    @Override
    public ITextHover getTextHover(final ISourceViewer sourceViewer,
                                   final String contentType) {

        return new DefaultTextHover(sourceViewer);

    }

    private static void setDamagerRepairer(final PresentationReconciler reconciler,
                                           final ITokenScanner scanner,
                                           final String contentType) {

        final DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);

        setDamagerRepairer(reconciler, dr, contentType);

    }

    private static void setDamagerRepairer(final PresentationReconciler reconciler,
                                           final DefaultDamagerRepairer dr,
                                           final String contentType) {

        reconciler.setDamager(dr, contentType);
        reconciler.setRepairer(dr, contentType);

    }

    /**
     * A scanner that simply changes the TextAttribute of any token that it
     * finds.  Used to color and style entire partitions.
     */
    static class SingleTokenScanner extends RuleBasedScanner {

        /**
         * @param attribute The TextAttribute used to define the default token.
         */
        public SingleTokenScanner(final TextAttribute attribute) {
            setDefaultReturnToken(new Token(attribute));
        }

    }

}
