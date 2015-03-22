package org.apollo.extension.releasegen.io;

public enum DataOrder {
    BIG_ENDIAN("be"), LITTLE_ENDIAN("le"), INVERSE_MIDDLE("m");

    private final String identifier;

    DataOrder(String identifier) {
        this.identifier = identifier;
    }

    public static DataOrder from(String value) {
        for(DataOrder order : values()) {
            if(value.equals(order.identifier)) {
                return order;
            }
        }

        return null;
    }

    public static String[] identifiers() {
        DataOrder[] values = values();
        String[] identifiers = new String[values.length];

        for(int i = 0; i < identifiers.length; i++) {
            identifiers[i] = values[i].identifier;
        }

        return identifiers;
    }
}
