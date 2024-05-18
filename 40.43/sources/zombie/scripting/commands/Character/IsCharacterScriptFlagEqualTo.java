package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptFlag;


public class IsCharacterScriptFlagEqualTo extends BaseCommand {
	String owner;
	String stat;
	int modifier = 0;
	IsoGameCharacter chr;
	public String name;
	String value = "";
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
		this.name = stringArray[1].trim().replace("\"", "");
		this.value = stringArray[2].trim().replace("\"", "");
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
		} else if (this.module.getCharacter(this.Other) == null) {
			gameCharacter = null;
		} else if (this.module.getCharacter(this.Other).Actual == null) {
			gameCharacter = null;
		} else {
			gameCharacter = this.module.getCharacter(this.Other).Actual;
		}

		if (this.chr == null) {
			return false;
		} else {
			String string = "";
			if (gameCharacter != null) {
				(new StringBuilder()).append(this.chr.getDescriptor().getID()).append("_").append(gameCharacter.getDescriptor().getID()).append("_").append(this.name).toString();
			} else {
				(new StringBuilder()).append(this.chr.getDescriptor().getID()).append("_").append(this.name).toString();
			}

			ScriptFlag scriptFlag = this.module.getFlag(this.name);
			return scriptFlag == null ? false : scriptFlag.IsValue(this.value);
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
