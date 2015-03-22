package sfix.msgcodec.io;

/**
 * Represents the different simple data types.
 * 
 * @author Graham <grahamedgecombe.com>
 */
public enum DataType {

	/**
	 * A byte.
	 */
	BYTE(1),

	/**
	 * A short.
	 */
	SHORT(2),

	/**
	 * A 'tri byte' - a group of three bytes.
	 */
	TRI_BYTE(3),

	/**
	 * An integer.
	 */
	INT(4),

	/**
	 * A long.
	 */
	LONG(8);

	/**
	 * The number of bytes this type occupies.
	 */
	private final int bytes;

	/**
	 * Creates a data type.
	 * 
	 * @param bytes The number of bytes it occupies.
	 */
	private DataType(int bytes) {
		this.bytes = bytes;
	}

	/**
	 * Gets the number of bytes the data type occupies.
	 * 
	 * @return The number of bytes.
	 */
	public int getBytes() {
		return bytes;
	}

	/**
	 * Lookup a DataType by the bit width of the integer it represents.
	 *
	 * @param bits The number of bits to lookup
	 * @return A DataType with a width of <code>bits</code> or null
	 */
	public static DataType fromBits(int bits) {
		int bytes = bits / 8;

		for(DataType type : values()) {
			if (type.bytes == bytes) {
				return type;
			}
		}

		return null;
	}

}