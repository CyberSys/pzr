package zombie.radio.globals;



public enum RadioGlobalType {

	String,
	Integer,
	Boolean,
	Float,
	Invalid;

	private static RadioGlobalType[] $values() {
		return new RadioGlobalType[]{String, Integer, Boolean, Float, Invalid};
	}
}
