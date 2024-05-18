package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.scripting.commands.BaseCommand;


public class TryToTeamUp extends BaseCommand {
	String owner;
	boolean tryToTeamUp = true;

	public void init(String string, String[] stringArray) {
		this.owner = string;
		this.tryToTeamUp = new Boolean(stringArray[0]);
	}

	public void begin() {
		IsoGameCharacter gameCharacter = this.module.getCharacterActual(this.owner);
		if (gameCharacter != null) {
			((IsoSurvivor)gameCharacter).setTryToTeamUp(this.tryToTeamUp);
		}
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
