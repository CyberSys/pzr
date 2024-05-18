package zombie.config;

import zombie.debug.DebugLog;


public class DoubleConfigOption extends ConfigOption {
	protected double value;
	protected double defaultValue;
	protected double min;
	protected double max;

	public DoubleConfigOption(String string, double double1, double double2, double double3) {
		super(string);
		if (!(double3 < double1) && !(double3 > double2)) {
			this.value = double3;
			this.defaultValue = double3;
			this.min = double1;
			this.max = double2;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public String getType() {
		return "double";
	}

	public double getMin() {
		return this.min;
	}

	public double getMax() {
		return this.max;
	}

	public void resetToDefault() {
		this.setValue(this.defaultValue);
	}

	public void setDefaultToCurrentValue() {
		this.defaultValue = this.value;
	}

	public void parse(String string) {
		try {
			double double1 = Double.parseDouble(string);
			this.setValue(double1);
		} catch (NumberFormatException numberFormatException) {
			DebugLog.log("ERROR DoubleConfigOption.parse() \"" + this.name + "\" string=" + string + "\"");
		}
	}

	public String getValueAsString() {
		return String.valueOf(this.value);
	}

	public void setValueFromObject(Object object) {
		if (object instanceof Double) {
			this.setValue((Double)object);
		} else if (object instanceof String) {
			this.parse((String)object);
		}
	}

	public Object getValueAsObject() {
		return this.value;
	}

	public boolean isValidString(String string) {
		try {
			double double1 = Double.parseDouble(string);
			return double1 >= this.min && double1 <= this.max;
		} catch (NumberFormatException numberFormatException) {
			return false;
		}
	}

	public void setValue(double double1) {
		if (double1 < this.min) {
			DebugLog.log("ERROR: DoubleConfigOption.setValue() \"" + this.name + "\" " + double1 + " is less than min=" + this.min);
		} else if (double1 > this.max) {
			DebugLog.log("ERROR: DoubleConfigOption.setValue() \"" + this.name + "\" " + double1 + " is greater than max=" + this.max);
		} else {
			this.value = double1;
		}
	}

	public double getValue() {
		return this.value;
	}

	public double getDefaultValue() {
		return this.defaultValue;
	}
}
