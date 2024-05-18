package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class AllowConversation extends BaseCommand {
	String owner;
	String say;
	String Other;
	IsoGameCharacter chr;
	boolean bAllow;

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		this.owner = string;
		String string2 = "";
		this.bAllow = stringArray[0].trim().equals("true");
	}

	public void begin() {
		if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
			this.chr = this.currentinstance.getAlias(this.owner);
		} else {
			if (this.module.getCharacter(this.owner) == null) {
				return;
			}

			if (this.module.getCharacter(this.owner).Actual == null) {
				return;
			}

			this.chr = this.module.getCharacter(this.owner).Actual;
		}

		if (this.bAllow) {
			this.chr.setAllowConversation(true);
		} else {
			this.chr.setAllowConversation(false);
		}
	}

	public boolean DoesInstantly() {
		return true;
	}
}
