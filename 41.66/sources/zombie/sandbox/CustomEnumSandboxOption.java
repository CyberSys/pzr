package zombie.sandbox;

import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;


public final class CustomEnumSandboxOption extends CustomSandboxOption {
	public final int numValues;
	public final int defaultValue;
	public String m_valueTranslation;

	CustomEnumSandboxOption(String string, int int1, int int2) {
		super(string);
		this.numValues = int1;
		this.defaultValue = int2;
	}

	static CustomEnumSandboxOption parse(ScriptParser.Block block) {
		int int1 = getValueInt(block, "numValues", -1);
		int int2 = getValueInt(block, "default", -1);
		if (int1 > 0 && int2 > 0) {
			CustomEnumSandboxOption customEnumSandboxOption = new CustomEnumSandboxOption(block.id, int1, int2);
			if (!customEnumSandboxOption.parseCommon(block)) {
				return null;
			} else {
				ScriptParser.Value value = block.getValue("valueTranslation");
				if (value != null) {
					customEnumSandboxOption.m_valueTranslation = StringUtils.discardNullOrWhitespace(value.getValue().trim());
				}

				return customEnumSandboxOption;
			}
		} else {
			return null;
		}
	}
}
