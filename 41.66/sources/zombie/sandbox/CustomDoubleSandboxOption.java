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
		float float1 = getValueFloat(block, "min", Float.NaN);
		float float2 = getValueFloat(block, "max", Float.NaN);
		float float3 = getValueFloat(block, "default", Float.NaN);
		if (!Float.isNaN(float1) && !Float.isNaN(float2) && !Float.isNaN(float3)) {
			CustomDoubleSandboxOption customDoubleSandboxOption = new CustomDoubleSandboxOption(block.id, (double)float1, (double)float2, (double)float3);
			return !customDoubleSandboxOption.parseCommon(block) ? null : customDoubleSandboxOption;
		} else {
			return null;
		}
	}
}
