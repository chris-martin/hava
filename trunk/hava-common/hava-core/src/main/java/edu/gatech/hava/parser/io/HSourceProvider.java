package edu.gatech.hava.parser.io;

import java.io.IOException;
import java.io.Reader;

public interface HSourceProvider {

    Reader openStream(HSourceAddress address) throws IOException;

}
