package zombie.scripting.commands.Script;

import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;


public class IsPlaying extends BaseCommand {
	boolean invert = false;
	String val;

	public void begin() {
	}

	public boolean getValue() {
		boolean boolean1 = ScriptManager.instance.IsScriptPlaying(this.val);
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
		this.val = string;
		if (this.val.indexOf("!") == 0) {
			this.invert = true;
			this.val = this.val.substring(1);
		}
	}

	public boolean DoesInstantly() {
		return true;
	}
}
