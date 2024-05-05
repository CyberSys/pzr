package zombie.sandbox;

import zombie.scripting.ScriptParser;


public final class CustomBooleanSandboxOption extends CustomSandboxOption {
	public final boolean defaultValue;

	CustomBooleanSandboxOption(String string, boolean boolean1) {
		super(string);
		this.defaultValue = boolean1;
	}

	static CustomBooleanSandboxOption parse(ScriptParser.Block block) {
		ScriptParser.Value value = block.getValue("default");
		if (value == null) {
			return null;
		} else {
			boolean boolean1 = Boolean.parseBoolean(value.getValue().trim());
			CustomBooleanSandboxOption customBooleanSandboxOption = new CustomBooleanSandboxOption(block.id, boolean1);
			return !customBooleanSandboxOption.parseCommon(block) ? null : customBooleanSandboxOption;
		}
	}
}
