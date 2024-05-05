package zombie.config;


public class StringConfigOption extends ConfigOption {
	protected String value;
	protected String defaultValue;

	public StringConfigOption(String string, String string2) {
		super(string);
		if (string2 == null) {
			string2 = "";
		}

		this.value = string2;
		this.defaultValue = string2;
	}

	public String getType() {
		return "string";
	}

	public void resetToDefault() {
		this.value = this.defaultValue;
	}

	public void setDefaultToCurrentValue() {
		this.defaultValue = this.value;
	}

	public void parse(String string) {
		this.setValueFromObject(string);
	}

	public String getValueAsString() {
		return this.value;
	}

	public String getValueAsLuaString() {
		return String.format("\"%s\"", this.value.replace("\\", "\\\\").replace("\"", "\\\""));
	}

	public void setValueFromObject(Object object) {
		if (object == null) {
			this.value = "";
		} else if (object instanceof String) {
			this.value = (String)object;
		} else {
			this.value = object.toString();
		}
	}

	public Object getValueAsObject() {
		return this.value;
	}

	public boolean isValidString(String string) {
		return true;
	}

	public void setValue(String string) {
		if (string == null) {
			string = "";
		}

		this.value = string;
	}

	public String getValue() {
		return this.value;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}
}
