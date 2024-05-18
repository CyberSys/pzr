package zombie.scripting.commands.Activatable;

import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptActivatable;


public class IsActivated extends BaseCommand {
	boolean invert = false;
	String character;

	public void begin() {
	}

	public boolean getValue() {
		ScriptActivatable scriptActivatable = this.module.getActivatable(this.character);
		if (scriptActivatable == null) {
			return false;
		} else if (this.invert) {
			return !scriptActivatable.IsActivated();
		} else {
			return scriptActivatable.IsActivated();
		}
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		this.character = string;
		if (this.character.indexOf("!") == 0) {
			this.invert = true;
			this.character = this.character.substring(1);
		}
	}

	public boolean DoesInstantly() {
		return true;
	}
}
