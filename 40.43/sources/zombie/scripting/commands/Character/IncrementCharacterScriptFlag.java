package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptFlag;


public class IncrementCharacterScriptFlag extends BaseCommand {
	String owner;
	String stat;
	int modifier = 0;
	IsoGameCharacter chr;
	public String name;
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
	}

	public boolean getValue() {
		return true;
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

		if (this.chr != null) {
			String string = "";
			if (gameCharacter != null) {
				(new StringBuilder()).append(this.chr.getDescriptor().getID()).append("_").append(gameCharacter.getDescriptor().getID()).append("_").append(this.name).toString();
			} else {
				(new StringBuilder()).append(this.chr.getDescriptor().getID()).append("_").append(this.name).toString();
			}

			ScriptFlag scriptFlag = this.module.getFlag(this.name);
			if (scriptFlag == null) {
				scriptFlag = new ScriptFlag();
				scriptFlag.module = this.module;
				scriptFlag.name = this.name;
				scriptFlag.value = "1";
				this.module.FlagMap.put(this.name, scriptFlag);
			} else {
				scriptFlag.value = (new Integer(Integer.parseInt(scriptFlag.value) + 1)).toString();
			}
		}
	}

	public boolean AllowCharacterBehaviour(String string) {
		return true;
	}

	public boolean DoesInstantly() {
		return true;
	}
}
