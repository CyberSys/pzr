package zombie.sandbox;

import zombie.scripting.ScriptParser;


public final class CustomStringSandboxOption extends CustomSandboxOption {
	public final String defaultValue;

	CustomStringSandboxOption(String string, String string2) {
		super(string);
		this.defaultValue = string2;
	}

	static CustomStringSandboxOption parse(ScriptParser.Block block) {
		ScriptParser.Value value = block.getValue("default");
		if (value == null) {
			return null;
		} else {
			CustomStringSandboxOption customStringSandboxOption = new CustomStringSandboxOption(block.id, value.getValue().trim());
			return !customStringSandboxOption.parseCommon(block) ? null : customStringSandboxOption;
		}
	}
}
