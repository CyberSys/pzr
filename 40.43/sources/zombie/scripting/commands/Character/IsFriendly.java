package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.scripting.commands.BaseCommand;


public class IsFriendly extends BaseCommand {
	String owner;
	String stat;
	int modifier = 0;
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
		} else if (this.chr instanceof IsoPlayer) {
			if (this.invert) {
				return ((IsoPlayer)this.chr).getDialogMood() != 2;
			} else {
				return ((IsoPlayer)this.chr).getDialogMood() == 2;
			}
		} else if (this.chr.getDescriptor().isFriendly()) {
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
