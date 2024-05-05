package zombie.config;

import zombie.debug.DebugLog;


public class IntegerConfigOption extends ConfigOption {
	protected int value;
	protected int defaultValue;
	protected int min;
	protected int max;

	public IntegerConfigOption(String string, int int1, int int2, int int3) {
		super(string);
		if (int3 >= int1 && int3 <= int2) {
			this.value = int3;
			this.defaultValue = int3;
			this.min = int1;
			this.max = int2;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public String getType() {
		return "integer";
	}

	public void resetToDefault() {
		this.setValue(this.defaultValue);
	}

	public double getMin() {
		return (double)this.min;
	}

	public double getMax() {
		return (double)this.max;
	}

	public void setDefaultToCurrentValue() {
		this.defaultValue = this.value;
	}

	public void parse(String string) {
		try {
			double double1 = Double.parseDouble(string);
			this.setValue((int)double1);
		} catch (NumberFormatException numberFormatException) {
			DebugLog.log("ERROR IntegerConfigOption.parse() \"" + this.name + "\" string=\"" + string + "\"");
		}
	}

	public String getValueAsString() {
		return String.valueOf(this.value);
	}

	public void setValueFromObject(Object object) {
		if (object instanceof Double) {
			this.setValue(((Double)object).intValue());
		} else if (object instanceof String) {
			this.parse((String)object);
		}
	}

	public Object getValueAsObject() {
		return (double)this.value;
	}

	public boolean isValidString(String string) {
		try {
			int int1 = Integer.parseInt(string);
			return int1 >= this.min && int1 <= this.max;
		} catch (NumberFormatException numberFormatException) {
			return false;
		}
	}

	public void setValue(int int1) {
		if (int1 < this.min) {
			DebugLog.log("ERROR: IntegerConfigOption.setValue() \"" + this.name + "\" " + int1 + " is less than min=" + this.min);
		} else if (int1 > this.max) {
			DebugLog.log("ERROR: IntegerConfigOption.setValue() \"" + this.name + "\" " + int1 + " is greater than max=" + this.max);
		} else {
			this.value = int1;
		}
	}

	public int getValue() {
		return this.value;
	}

	public int getDefaultValue() {
		return this.defaultValue;
	}

	public String getTooltip() {
		return String.valueOf(this.value);
	}
}
