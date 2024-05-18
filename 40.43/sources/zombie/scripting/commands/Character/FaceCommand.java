package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoDirections;
import zombie.scripting.commands.BaseCommand;


public class FaceCommand extends BaseCommand {
	String owner;
	IsoDirections dir;
	String other = null;

	public void init(String string, String[] stringArray) {
		this.owner = string;
		try {
			this.dir = IsoDirections.valueOf(stringArray[0]);
		} catch (Exception exception) {
			this.other = stringArray[0];
		}
	}

	public void begin() {
		IsoGameCharacter gameCharacter = null;
		IsoGameCharacter gameCharacter2 = null;
		if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
			gameCharacter = this.currentinstance.getAlias(this.owner);
		} else {
			gameCharacter = this.module.getCharacter(this.owner).Actual;
		}

		if (gameCharacter != null) {
			if (this.other == null) {
				gameCharacter.setDir(this.dir);
			} else {
				gameCharacter2 = null;
				if (this.currentinstance != null && this.currentinstance.HasAlias(this.other)) {
					gameCharacter2 = this.currentinstance.getAlias(this.other);
				} else {
					gameCharacter2 = this.module.getCharacter(this.other).Actual;
				}

				if (gameCharacter2 != null) {
					gameCharacter.faceDirection(gameCharacter2);
				}
			}
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
