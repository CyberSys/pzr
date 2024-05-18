package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class IsInside extends BaseCommand {
	String owner;
	boolean invert = false;

	public void init(String string, String[] stringArray) {
		this.owner = string;
		if (this.owner.indexOf("!") == 0) {
			this.invert = true;
			this.owner = this.owner.substring(1);
		}
	}

	public boolean getValue() {
		IsoGameCharacter gameCharacter = this.module.getCharacterActual(this.owner);
		if (gameCharacter == null) {
			return false;
		} else if (this.invert) {
			return gameCharacter.getCurrentSquare().getRoom() == null;
		} else {
			return gameCharacter.getCurrentSquare().getRoom() != null;
		}
	}

	public void begin() {
	}

	public void Finish() {
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
