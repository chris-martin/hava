package edu.gatech.hava.parser.io;

/**
 * A {@link HSourceAddressFactory} can create a {@link HSourceAddress}
 * object from a String (the value of an 'import' statement).
 */
public class HSourceAddressFactory {

    private String defaultPath;

    public HSourceAddressFactory() {

    }

    public HSourceAddressFactory(final String defaultPath) {

        setDefaultPath(defaultPath);

    }

    /**
     * @param defaultPath can be a directory or file path
     */
    public void setDefaultPath(final String defaultPath) {

        if (defaultPath == null) {
            this.defaultPath = "";
        } else {
            this.defaultPath = breakOffFilename(defaultPath)[0];
        }

    }

    public HSourceAddress getAddress(String address) {

        if (address == null) {
            address = "";
        } else {
            address = applyExtension(address);
        }

        if (isRelative(address)) {
            return createRelativeAddress(address);
        } else {
            return createAbsoluteAddress(address);
        }

    }

    private String applyExtension(String a) {

        if (!a.endsWith(".hava")) {
            a += ".hava";
        }

        return a;

    }

    public HSourceAddress createRelativeAddress(final String address) {

        return new HSourceAddress(defaultPath, address);

    }

    public HSourceAddress createAbsoluteAddress(final String address) {

        String[] components = breakOffFilename(address);

        return new HSourceAddress(components[0], components[1]);

    }

    private boolean isRelative(final String address) {

        return lastSlash(address) < 0;

    }

    private String[] breakOffFilename(final String address) {

        int k = lastSlash(address);

        return new String[] {
            address.substring(0, k + 1),
            address.substring(k + 1)
        };

    }

    private int lastSlash(final String str) {

        return Math.max(str.lastIndexOf("/"),
                        str.lastIndexOf("\\"));

    }

}
