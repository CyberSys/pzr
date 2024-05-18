package zombie.scripting.commands.Hook;

import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;


public class RegisterOneTime extends BaseCommand {
	String event;
	String script;
	int num = 1;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("Hook")) {
			this.event = stringArray[0].trim().replace("\"", "");
			this.script = stringArray[1].trim().replace("\"", "");
		}
	}

	public void begin() {
		String string = this.script;
		if (!string.contains(".")) {
			string = this.module.name + "." + string;
		}

		ScriptManager.instance.AddOneTime(this.event, string);
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
