package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class IsDead extends BaseCommand {
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
		IsoGameCharacter gameCharacter = null;
		if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
			gameCharacter = this.currentinstance.getAlias(this.owner);
		} else {
			gameCharacter = this.module.getCharacter(this.owner).Actual;
		}

		if (gameCharacter == null) {
			return false;
		} else if (this.invert) {
			return !(gameCharacter.getHealth() <= 0.0F) && !(gameCharacter.getBodyDamage().getHealth() <= 0.0F);
		} else {
			return gameCharacter.getHealth() <= 0.0F || gameCharacter.getBodyDamage().getHealth() <= 0.0F;
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
