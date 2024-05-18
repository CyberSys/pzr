package zombie.scripting.commands.Trigger;

import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;


public class TimeSinceLastRan extends BaseCommand {
	boolean invert = false;
	public String triggerInst;
	int frames = 0;

	public void begin() {
	}

	public boolean getValue() {
		boolean boolean1 = (Integer)ScriptManager.instance.CustomTriggerLastRan.get(this.triggerInst) > this.frames;
		if (this.invert) {
			return !boolean1;
		} else {
			return boolean1;
		}
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		if (string != null) {
			this.triggerInst = string.toLowerCase();
			if (this.triggerInst.indexOf("!") == 0) {
				this.invert = true;
				this.triggerInst = this.triggerInst.substring(1);
			}
		}

		this.frames = (int)(30.0F * Float.parseFloat(stringArray[0].trim()));
	}

	public boolean DoesInstantly() {
		return true;
	}
}
