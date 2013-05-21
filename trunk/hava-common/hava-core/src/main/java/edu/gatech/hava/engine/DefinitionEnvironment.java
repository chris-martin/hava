package edu.gatech.hava.engine;

import edu.gatech.hava.engine.HDefinition.HDefImport;
import edu.gatech.hava.parser.io.HSourceAddress;
import edu.gatech.hava.parser.io.HSourceAddressFactory;

/**
 * {@link DefinitionEnvironment} holds values that used to be
 * kept as static variables in {@link HDefinition}.  There is one
 * {@link DefinitionEnvironment} per execution of an HEngine.
 */
class DefinitionEnvironment {

    private int defCount;

    private HDefImport rootImport;
    private HDefImport currentImport;

    private HSourceAddressFactory addressFactory = new HSourceAddressFactory();

    /**
     * Constructor.
     */
    public DefinitionEnvironment() {

        init(new HSourceAddress());

    }

    /**
     * Constructor.
     *
     * @param address the address of the root hava file
     */
    public DefinitionEnvironment(final String address) {

        init(addressFactory.getAddress(address));

    }

    /**
     * Constructor.
     *
     * @param address the address of the root hava file
     */
    public DefinitionEnvironment(final HSourceAddress address) {

        init(address);

    }

    private void init(final HSourceAddress address) {

        defCount = 0;

        final HDefImport imp = new HDefImport(
                this, address, false, false, null);

        setCurrentHDImport(imp);
        setRootHDImport(imp);

        addressFactory.setDefaultPath(imp.getPath());

    }

    /**
     * @return the root source definition.
     */
    HDefImport getRootHDImport() {

        return rootImport;

    }

    private void setRootHDImport(final HDefImport rootImport) {

        this.rootImport = rootImport;

    }

    /**
     * @return the current source definition.
     */
    HDefImport getCurrentHDImport() {

        return currentImport;

    }

    /**
     * @param currentImport the current source definition.
     */
    void setCurrentHDImport(final HDefImport currentImport) {

        this.currentImport = currentImport;

    }

    int getAndIncrementDefCount() {

        return defCount++;

    }

    /**
     * Constructs an {@link HSourceAddress} from a string,
     * using this definition environment's currentImport
     * as the base for relative paths.
     *
     * @param address the address string
     * @return the new source address
     */
    HSourceAddress getAddress(final String address) {

        return addressFactory.getAddress(address);

    }

}
