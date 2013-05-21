package edu.gatech.hava.parser.io;


public class HSourceAddress {

    private String path;
    private String filename;
    private String identifier;

    public HSourceAddress() {

        this("", "");

    }

    public HSourceAddress(final String path, final String filename) {

        setAddress(path, filename);

    }

    public void setAddress(final String path, final String filename) {

        this.path = path;
        this.filename = filename;
        this.identifier = createIdentifier(filename);

    }

    private String createIdentifier(final String filename) {

        if (filename.toLowerCase().endsWith(".hava")) {

            return filename
                .substring(0, filename.length() - ".hava".length())
                .replace(' ', '_')
                .replace('.', '_');

        } else {

            return "";

        }

    }

    public String getPath() {

        return path;

    }

    public void setPath(final String path) {

        this.path = path;

    }

    public String getFilename() {

        return filename;

    }

    public String getIdentifier() {

        return identifier;

    }

    public String getAddress() {

        return emptyIfNull(path) + emptyIfNull(filename);

    }

    private String emptyIfNull(final String str) {

        if (str == null) {
            return "";
        } else {
            return str;
        }

    }

    public String toString() {

        return getAddress();

    }

    public int hashCode() {

        return getIdentifier().hashCode();

    }

    public boolean equals(final Object o) {

        if (!(o instanceof HSourceAddress)) {
            return false;
        }

        HSourceAddress otherAddress = (HSourceAddress) o;

        return otherAddress.getIdentifier().equals(getIdentifier());

    }

}
