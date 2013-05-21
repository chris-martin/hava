package edu.gatech.hava.hdt.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

public class HavaDocumentSetupParticipant implements IDocumentSetupParticipant {

    /**
     * Connects Hava documents to the HavaPartitionScanner.
     *
     * @param document An IDocument representing Hava source.
     */
    @Override
    public final void setup(final IDocument document) {

        connectPartitioner(document, createHavaPartitioner());

    }

    private static void connectPartitioner(final IDocument document,
                                           final IDocumentPartitioner partitioner) {

        partitioner.connect(document);
        document.setDocumentPartitioner(partitioner);

    }

    private static IDocumentPartitioner createHavaPartitioner() {

        final IPartitionTokenScanner partitionScanner = new HavaPartitionScanner();
        final String[] legalContentTypes = HavaPartitionScanner.HAVA_PARTITION_TYPES;

        return new FastPartitioner(partitionScanner, legalContentTypes);

    }

}
