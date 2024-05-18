package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoDeadBody;
import zombie.scripting.commands.BaseCommand;


public class Die extends BaseCommand {
	String owner;
	boolean bGory = false;

	public void init(String string, String[] stringArray) {
		this.owner = string;
		this.bGory = stringArray[0].equals("true");
	}

	public void begin() {
		IsoGameCharacter gameCharacter = this.module.getCharacterActual(this.owner);
		if (gameCharacter != null) {
			gameCharacter.setHealth(0.0F);
			new IsoDeadBody(gameCharacter);
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
