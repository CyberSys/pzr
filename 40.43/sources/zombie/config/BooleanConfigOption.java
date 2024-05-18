package zombie.config;

import zombie.debug.DebugLog;


public class BooleanConfigOption extends ConfigOption {
	protected boolean value;
	protected boolean defaultValue;

	public BooleanConfigOption(String string, boolean boolean1) {
		super(string);
		this.value = boolean1;
		this.defaultValue = boolean1;
	}

	public String getType() {
		return "boolean";
	}

	public void resetToDefault() {
		this.setValue(this.defaultValue);
	}

	public void setDefaultToCurrentValue() {
		this.defaultValue = this.value;
	}

	public void parse(String string) {
		if (this.isValidString(string)) {
			this.setValue(string.equalsIgnoreCase("true"));
		} else {
			DebugLog.log("ERROR BooleanConfigOption.parse() \"" + this.name + "\" string=" + string + "\"");
		}
	}

	public String getValueAsString() {
		return String.valueOf(this.value);
	}

	public void setValueFromObject(Object object) {
		if (object instanceof Boolean) {
			this.setValue((Boolean)object);
		} else if (object instanceof Double) {
			this.setValue((Double)object != 0.0);
		} else if (object instanceof String) {
			this.parse((String)object);
		}
	}

	public Object getValueAsObject() {
		return this.value;
	}

	public boolean isValidString(String string) {
		return string != null && string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
	}

	public void setValue(boolean boolean1) {
		this.value = boolean1;
	}

	public boolean getValue() {
		return this.value;
	}

	public boolean getDefaultValue() {
		return this.defaultValue;
	}
}
