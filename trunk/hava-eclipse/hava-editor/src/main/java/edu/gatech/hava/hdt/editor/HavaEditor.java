package edu.gatech.hava.hdt.editor;

import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * Extension for a custom Hava source editor.
 */
public class HavaEditor extends AbstractDecoratedTextEditor {

    /**
     * Default constructor which sets the SourceViewerConfiguration and
     * IDocumentProvider.
     *
     * @see IDocumentProvider.SourceViewerConfiguration
     */
    public HavaEditor() {

        super();

        // Install the source configuration
        final HavaConfiguration configuration = new HavaConfiguration();
        setSourceViewerConfiguration(configuration);

        // Install the document provider
        final TextFileDocumentProvider documentProvider =
            new TextFileDocumentProvider();
        setDocumentProvider(documentProvider);

    }

}
