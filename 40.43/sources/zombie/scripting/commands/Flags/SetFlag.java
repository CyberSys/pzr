package zombie.scripting.commands.Flags;

import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptFlag;


public class SetFlag extends BaseCommand {
	String name;
	String val;

	public void init(String string, String[] stringArray) {
		this.name = string.trim().replace("\"", "");
		this.val = stringArray[0].trim().replace("\"", "");
	}

	public void begin() {
		try {
			ScriptFlag scriptFlag = this.module.getFlag(this.name);
			if (scriptFlag == null) {
				return;
			}

			scriptFlag.SetValue(this.val);
		} catch (Exception exception) {
		}
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public boolean DoesInstantly() {
		return true;
	}
}
