package sfix.msgcodec.message.property;

public class SimplePropertyType implements PropertyType {
    private final String typeName;

    public SimplePropertyType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public Class<?> getType() throws ClassNotFoundException {
        return Class.forName(typeName);
    }
}
