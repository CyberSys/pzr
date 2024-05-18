package zombie.scripting.commands.Flags;

import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptFlag;


public class IsFlagValue extends BaseCommand {
	boolean invert = false;
	String name;
	String value;

	public void begin() {
	}

	public boolean getValue() {
		ScriptFlag scriptFlag = this.module.getFlag(this.name);
		if (scriptFlag == null) {
			return false;
		} else if (this.invert) {
			return !scriptFlag.IsValue(this.value);
		} else {
			return scriptFlag.IsValue(this.value);
		}
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		this.name = string;
		if (this.name != null && this.name.indexOf("!") == 0) {
			this.invert = true;
			this.name = this.name.substring(1);
		}

		this.value = stringArray[0].trim().replace("\"", "");
	}

	public boolean DoesInstantly() {
		return true;
	}
}
