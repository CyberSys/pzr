package zombie.radio.globals;


public enum CompareResult {

	True,
	False,
	Invalid;

	private static CompareResult[] $values() {
		return new CompareResult[]{True, False, Invalid};
	}
}
