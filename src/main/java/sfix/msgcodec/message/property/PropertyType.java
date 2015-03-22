package sfix.msgcodec.message.property;

public interface PropertyType {
    Class<?> getType() throws ClassNotFoundException;
}
