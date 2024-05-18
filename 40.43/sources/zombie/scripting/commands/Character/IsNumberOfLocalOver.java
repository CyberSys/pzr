package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class IsNumberOfLocalOver extends BaseCommand {
	String owner;
	String stat;
	int number = 0;
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

		this.number = Integer.parseInt(stringArray[0].trim());
		this.owner = string;
	}

	public boolean getValue() {
		if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
			this.chr = this.currentinstance.getAlias(this.owner);
		} else {
			this.chr = this.module.getCharacter(this.owner).Actual;
		}

		if (this.chr == null) {
			return false;
		} else if (this.number < this.chr.getLocalList().size()) {
			return !this.invert;
		} else {
			return this.invert;
		}
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
