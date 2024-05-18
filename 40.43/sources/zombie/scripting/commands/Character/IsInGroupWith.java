package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class IsInGroupWith extends BaseCommand {
	String owner;
	String stat;
	int modifier = 0;
	IsoGameCharacter chr;
	String Other = "";
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
		this.Other = stringArray[0].trim();
	}

	public boolean getValue() {
		if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
			this.chr = this.currentinstance.getAlias(this.owner);
		} else {
			if (this.module.getCharacter(this.owner) == null) {
				return false;
			}

			if (this.module.getCharacter(this.owner).Actual == null) {
				return false;
			}

			this.chr = this.module.getCharacter(this.owner).Actual;
		}

		IsoGameCharacter gameCharacter;
		if (this.currentinstance != null && this.currentinstance.HasAlias(this.Other)) {
			gameCharacter = this.currentinstance.getAlias(this.Other);
		} else {
			if (this.module.getCharacter(this.Other) == null) {
				return false;
			}

			if (this.module.getCharacter(this.Other).Actual == null) {
				return false;
			}

			gameCharacter = this.module.getCharacter(this.Other).Actual;
		}

		if (this.chr == null) {
			return false;
		} else if (this.chr.getDescriptor().getGroup() != null && this.chr.getDescriptor().getGroup().isMember(gameCharacter.getDescriptor())) {
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
