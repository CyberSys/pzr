package zombie.sandbox;

import zombie.scripting.ScriptParser;


public final class CustomIntegerSandboxOption extends CustomSandboxOption {
	public final int min;
	public final int max;
	public final int defaultValue;

	CustomIntegerSandboxOption(String string, int int1, int int2, int int3) {
		super(string);
		this.min = int1;
		this.max = int2;
		this.defaultValue = int3;
	}

	static CustomIntegerSandboxOption parse(ScriptParser.Block block) {
		int int1 = getValueInt(block, "min", Integer.MIN_VALUE);
		int int2 = getValueInt(block, "max", Integer.MIN_VALUE);
		int int3 = getValueInt(block, "default", Integer.MIN_VALUE);
		if (int1 != Integer.MIN_VALUE && int2 != Integer.MIN_VALUE && int3 != Integer.MIN_VALUE) {
			CustomIntegerSandboxOption customIntegerSandboxOption = new CustomIntegerSandboxOption(block.id, int1, int2, int3);
			return !customIntegerSandboxOption.parseCommon(block) ? null : customIntegerSandboxOption;
		} else {
			return null;
		}
	}
}
