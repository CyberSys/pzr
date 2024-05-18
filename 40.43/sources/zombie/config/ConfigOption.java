package zombie.config;


public abstract class ConfigOption {
	protected String name;

	public ConfigOption(String string) {
		if (string != null && !string.isEmpty() && !string.contains("=")) {
			this.name = string;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public String getName() {
		return this.name;
	}

	public abstract String getType();

	public abstract void resetToDefault();

	public abstract void setDefaultToCurrentValue();

	public abstract void parse(String string);

	public abstract String getValueAsString();

	public abstract void setValueFromObject(Object object);

	public abstract Object getValueAsObject();

	public abstract boolean isValidString(String string);
}
