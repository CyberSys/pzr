package zombie.sandbox;

import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;


public class CustomSandboxOption {
	public final String m_id;
	public String m_page;
	public String m_translation;

	CustomSandboxOption(String string) {
		this.m_id = string;
	}

	static double getValueDouble(ScriptParser.Block block, String string, double double1) {
		ScriptParser.Value value = block.getValue(string);
		return value == null ? double1 : PZMath.tryParseDouble(value.getValue().trim(), double1);
	}

	static float getValueFloat(ScriptParser.Block block, String string, float float1) {
		ScriptParser.Value value = block.getValue(string);
		return value == null ? float1 : PZMath.tryParseFloat(value.getValue().trim(), float1);
	}

	static int getValueInt(ScriptParser.Block block, String string, int int1) {
		ScriptParser.Value value = block.getValue(string);
		return value == null ? int1 : PZMath.tryParseInt(value.getValue().trim(), int1);
	}

	boolean parseCommon(ScriptParser.Block block) {
		ScriptParser.Value value = block.getValue("page");
		if (value != null) {
			this.m_page = StringUtils.discardNullOrWhitespace(value.getValue().trim());
		}

		ScriptParser.Value value2 = block.getValue("translation");
		if (value2 != null) {
			this.m_translation = StringUtils.discardNullOrWhitespace(value2.getValue().trim());
		}

		return true;
	}
}
