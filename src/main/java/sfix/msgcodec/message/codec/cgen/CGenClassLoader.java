package sfix.msgcodec.message.codec.cgen;

/**
 * Class loader proxy for exposing the {@link ClassLoader#defineClass} method to define classes on the fly.
 */
public class CGenClassLoader extends ClassLoader {

    /**
     * Initialize this ClassLoader with a parent ClassLoader.
     *
     * @param parent The parent class loader.
     */
    public CGenClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * Proxy to defineClass.
     *
     * @see ClassLoader#defineClass(String, byte[], int, int)
     */
    public Class<?> defineClassProxy(String name, byte[] b, int off, int len) {
        return defineClass(name, b, off, len);
    }
}
