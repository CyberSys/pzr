package zombie.scripting.commands;

import java.util.ArrayList;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Script;


public class StopAllScriptsExcept extends BaseCommand {
	String name;
	ArrayList scripts = new ArrayList();

	public void init(String string, String[] stringArray) {
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			this.scripts.add(stringArray[int1].trim());
		}
	}

	public void begin() {
		for (int int1 = 0; int1 < ScriptManager.instance.PlayingScripts.size(); ++int1) {
			boolean boolean1 = false;
			for (int int2 = 0; int2 < this.scripts.size(); ++int2) {
				if (((String)this.scripts.get(int2)).equals(((Script.ScriptInstance)ScriptManager.instance.PlayingScripts.get(int1)).theScript.name)) {
					boolean1 = true;
				}
			}

			if (!boolean1) {
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
