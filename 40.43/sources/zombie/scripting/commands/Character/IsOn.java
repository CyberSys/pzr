package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class IsOn extends BaseCommand {
	String owner;
	String stat;
	int modifier = 0;
	IsoGameCharacter chr;
	boolean invert = false;

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		if (string.indexOf("!") == 0) {
			this.invert = true;
			string = string.substring(1);
		}

		this.owner = string;
	}

	public boolean getValue() {
		return !this.invert;
	}

	public void begin() {
	}

	public boolean AllowCharacterBehaviour(String string) {
		return true;
	}

	public boolean DoesInstantly() {
		return true;
	}
}
