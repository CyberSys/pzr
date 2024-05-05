package zombie.radio.script;



public enum OperatorType {

	NONE,
	AND,
	OR;

	private static OperatorType[] $values() {
		return new OperatorType[]{NONE, AND, OR};
	}
}
