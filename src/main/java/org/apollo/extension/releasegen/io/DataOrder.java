package org.apollo.extension.releasegen.io;

/**
 * Represents the order of bytes in a {@link DataType} when {@link DataType#getBytes()} {@code  > 1}.
 *
 * @author Graham
 */
public enum DataOrder {

    /**
     * Most significant byte to least significant byte.
     */
    BIG("be"),

    /**
     * Also known as the V2 order.
     */
    INVERSED_MIDDLE("v2"),

    /**
     * Least significant byte to most significant byte.
     */
    LITTLE("le"),

    /**
     * Also known as the V1 order.
     */
    MIDDLE("v1");

    private final String identifier;

    DataOrder(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Resolve a DataOrder from its identifier.
     *
     * @param value The identifier to lookup.
     * @return The DataOrder with the specified identifier.
     */
    public static DataOrder from(String value) {
        for (DataOrder order : values()) {
            if (value.equals(order.identifier)) {
                return order;
            }
        }

        return null;
    }

    /**
     * @return An array of identifiers for all given <code>DataOrder</code>s.
     */
    public static String[] identifiers() {
        DataOrder[] values = values();
        String[] identifiers = new String[values.length];

        for (int i = 0; i < identifiers.length; i++) {
            identifiers[i] = values[i].identifier;
        }

        return identifiers;
    }
}
