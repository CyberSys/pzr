package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class IsSpeaking extends BaseCommand {
	boolean invert = false;
	String character;

	public void begin() {
	}

	public boolean getValue() {
		IsoGameCharacter gameCharacter = this.module.getCharacterActual(this.character);
		if (gameCharacter == null) {
			return false;
		} else if (this.invert) {
			return !gameCharacter.IsSpeaking();
		} else {
			return gameCharacter.IsSpeaking();
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
