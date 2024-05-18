package zombie.scripting.commands.Script;

import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Conditional;
import zombie.scripting.objects.Script;


public class StopScript extends BaseCommand {
	String position;

	public void init(String string, String[] stringArray) {
		this.position = string;
	}

	public void begin() {
		if (this.position == null) {
			Script.ScriptInstance scriptInstance;
			for (scriptInstance = this.currentinstance; scriptInstance.parent != null && scriptInstance.theScript instanceof Conditional; scriptInstance = scriptInstance.parent) {
			}

			ScriptManager.instance.StopScript(scriptInstance);
		} else {
			ScriptManager.instance.StopScript(this.position);
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
