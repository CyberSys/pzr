package zombie.scripting.commands;

import zombie.scripting.ScriptManager;
import zombie.ui.TutorialManager;


public class LockHud extends BaseCommand {
	boolean doIt = false;

	public void init(String string, String[] stringArray) {
		if (stringArray.length == 1) {
			this.doIt = stringArray[0].trim().equalsIgnoreCase("true");
		}
	}

	public void begin() {
		TutorialManager.instance.StealControl = this.doIt;
		if (!this.doIt) {
			ScriptManager.instance.skipping = false;
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
