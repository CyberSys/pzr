package zombie.scripting.commands.Flags;

import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptFlag;


public class IsGreaterThanEqualTo extends BaseCommand {
	boolean invert = false;
	String name;
	String value;

	public void begin() {
	}

	public boolean getValue() {
		ScriptFlag scriptFlag = this.module.getFlag(this.name);
		if (scriptFlag == null) {
			return false;
		} else {
			int int1 = Integer.parseInt(scriptFlag.value);
			int int2 = Integer.parseInt(this.value);
			boolean boolean1 = int1 >= int2;
			if (this.invert) {
				return !boolean1;
			} else {
				return boolean1;
			}
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
