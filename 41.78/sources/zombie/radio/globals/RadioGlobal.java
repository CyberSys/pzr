package zombie.radio.globals;


public abstract class RadioGlobal {
	protected String name;
	protected Object value;
	protected RadioGlobalType type;

	protected RadioGlobal(Object object, RadioGlobalType radioGlobalType) {
		this((String)null, object, radioGlobalType);
	}

	protected RadioGlobal(String string, Object object, RadioGlobalType radioGlobalType) {
		this.type = RadioGlobalType.Invalid;
		this.name = string;
		this.value = object;
		this.type = radioGlobalType;
	}

	public final RadioGlobalType getType() {
		return this.type;
	}

	public final String getName() {
		return this.name;
	}

	public abstract String getString();

	public abstract CompareResult compare(RadioGlobal radioGlobal, CompareMethod compareMethod);

	public abstract boolean setValue(RadioGlobal radioGlobal, EditGlobalOps editGlobalOps);
}
