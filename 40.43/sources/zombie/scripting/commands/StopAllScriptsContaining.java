package zombie.scripting.commands;

import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Script;


public class StopAllScriptsContaining extends BaseCommand {
	String name;
	String scripts = null;

	public void init(String string, String[] stringArray) {
		this.scripts = stringArray[0].trim().replace("\"", "");
	}

	public void begin() {
		for (int int1 = 0; int1 < ScriptManager.instance.PlayingScripts.size(); ++int1) {
			if (((Script.ScriptInstance)ScriptManager.instance.PlayingScripts.get(int1)).theScript.name.contains(this.scripts)) {
				ScriptManager.instance.StopScript(((Script.ScriptInstance)ScriptManager.instance.PlayingScripts.get(int1)).theScript.name);
			}
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
