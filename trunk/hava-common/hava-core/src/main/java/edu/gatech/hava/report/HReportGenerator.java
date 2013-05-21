package edu.gatech.hava.report;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import edu.gatech.hava.engine.HReference;

/**
 * Generates the output shown in the demo, by sorting computed values, and
 * printing them, using toString() methods provided in the engine.
 */
public interface HReportGenerator {

    void setReferences(List<HReference> references);

    HReference[] getDisplayedReferences();

    String get(HReference reference);

    void write(Writer writer) throws IOException;

    void write(StringWriter writer,
               HReference reference);

}
