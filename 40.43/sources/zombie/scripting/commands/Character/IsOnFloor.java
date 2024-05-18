package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class IsOnFloor extends BaseCommand {
	String owner;
	int min = 0;
	int max = 0;
	boolean invert = false;

	public void init(String string, String[] stringArray) {
		this.owner = string;
		if (this.owner.indexOf("!") == 0) {
			this.invert = true;
			this.owner = this.owner.substring(1);
		}

		if (stringArray.length == 1) {
			this.min = this.max = Integer.parseInt(stringArray[0].trim());
		}

		if (stringArray.length == 2) {
			this.min = Integer.parseInt(stringArray[0].trim());
			this.max = Integer.parseInt(stringArray[1].trim());
		}
	}

	public boolean getValue() {
		IsoGameCharacter gameCharacter = this.module.getCharacterActual(this.owner);
		if (gameCharacter == null) {
			return false;
		} else if (this.invert) {
			return !(gameCharacter.getZ() >= (float)this.min) || !(gameCharacter.getZ() <= (float)this.max);
		} else {
			return gameCharacter.getZ() >= (float)this.min && gameCharacter.getZ() <= (float)this.max;
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
