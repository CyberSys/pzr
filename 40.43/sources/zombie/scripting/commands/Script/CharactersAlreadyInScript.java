package zombie.scripting.commands.Script;

import zombie.scripting.commands.BaseCommand;


public class CharactersAlreadyInScript extends BaseCommand {
	String position;
	String val;
	private boolean invert = false;

	public boolean getValue() {
		if (this.invert) {
			return !this.currentinstance.CharactersAlreadyInScript;
		} else {
			return this.currentinstance.CharactersAlreadyInScript;
		}
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		this.val = string;
		if (this.val != null && this.val.indexOf("!") == 0) {
			this.invert = true;
			this.val = this.val.substring(1);
		}
	}

	public boolean DoesInstantly() {
		return true;
	}

	public void begin() {
	}
}
