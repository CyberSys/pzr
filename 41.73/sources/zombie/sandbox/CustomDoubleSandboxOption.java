package zombie.sandbox;

import zombie.scripting.ScriptParser;


public final class CustomDoubleSandboxOption extends CustomSandboxOption {
	public final double min;
	public final double max;
	public final double defaultValue;

	CustomDoubleSandboxOption(String string, double double1, double double2, double double3) {
		super(string);
		this.min = double1;
		this.max = double2;
		this.defaultValue = double3;
	}

	static CustomDoubleSandboxOption parse(ScriptParser.Block block) {
		double double1 = getValueDouble(block, "min", Double.NaN);
		double double2 = getValueDouble(block, "max", Double.NaN);
		double double3 = getValueDouble(block, "default", Double.NaN);
		if (!Double.isNaN(double1) && !Double.isNaN(double2) && !Double.isNaN(double3)) {
			CustomDoubleSandboxOption customDoubleSandboxOption = new CustomDoubleSandboxOption(block.id, double1, double2, double3);
			return !customDoubleSandboxOption.parseCommon(block) ? null : customDoubleSandboxOption;
		} else {
			return null;
		}
	}
}
