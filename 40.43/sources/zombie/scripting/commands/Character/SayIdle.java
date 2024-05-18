package zombie.scripting.commands.Character;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.scripting.commands.BaseCommand;


public class SayIdle extends BaseCommand {
	String owner;
	Stack say = new Stack();
	IsoGameCharacter chr;

	public boolean IsFinished() {
		if (this.chr == null) {
			return true;
		} else {
			return !this.chr.isSpeaking();
		}
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		this.owner = string;
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			String string2 = stringArray[int1];
			string2 = this.module.getLanguage(string2);
			if (string2.indexOf("\"") == 0) {
				string2 = string2.substring(1);
				string2 = string2.substring(0, string2.length() - 1);
			}

			this.say.add(string2);
		}
	}

	public void begin() {
		String string;
		if (this.currentinstance.HasAlias(this.owner)) {
			this.chr = this.currentinstance.getAlias(this.owner);
			string = (String)this.say.get(Rand.Next(this.say.size()));
			string = StringFunctions.EscapeChar(this.chr, string);
			this.chr.Say(string);
		} else {
			this.chr = this.module.getCharacter(this.owner).Actual;
			if (this.chr != null) {
				string = (String)this.say.get(Rand.Next(this.say.size()));
				string = StringFunctions.EscapeChar(this.chr, string);
				this.chr.Say(string);
			}
		}
	}

	public boolean AllowCharacterBehaviour(String string) {
		return true;
	}

	public boolean DoesInstantly() {
		return false;
	}
}
