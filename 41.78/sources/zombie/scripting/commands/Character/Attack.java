package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.scripting.commands.BaseCommand;


public class Attack extends BaseCommand {
	String owner;

	public void init(String string, String[] stringArray) {
		this.owner = string;
	}

	public void begin() {
		IsoGameCharacter gameCharacter = null;
		if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
			gameCharacter = this.currentinstance.getAlias(this.owner);
		} else {
			if (this.module.getCharacter(this.owner) == null) {
				return;
			}

			if (this.module.getCharacter(this.owner).Actual == null) {
				return;
			}

			gameCharacter = this.module.getCharacter(this.owner).Actual;
		}

		((IsoLivingCharacter)gameCharacter).AttemptAttack(1.0F);
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
