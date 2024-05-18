package zombie.scripting.commands.Character;

import java.security.InvalidParameterException;
import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptTalker;


public class SayAt extends BaseCommand {
	String owner;
	Stack chrs = new Stack();
	Stack say = new Stack();
	IsoGameCharacter chr;
	ScriptTalker talkerobj = null;
	boolean talker = false;
	Stack chras = new Stack();

	public boolean IsFinished() {
		if (this.talker && this.talkerobj != null) {
			return !this.talkerobj.getActual().IsSpeaking();
		} else if (this.chr == null) {
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
			if (string2.contains("\"")) {
				string2 = this.module.getLanguage(string2);
				if (string2.indexOf("\"") == 0) {
					string2 = string2.substring(1);
					string2 = string2.substring(0, string2.length() - 1);
				}

				this.say.add(string2);
			} else {
				this.chrs.add(string2.trim());
			}
		}
	}

	public void begin() {
		if (!ScriptManager.instance.skipping) {
			String string;
			if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
				this.chr = this.currentinstance.getAlias(this.owner);
				for (int int1 = 0; int1 < this.chrs.size(); ++int1) {
					if (this.currentinstance.HasAlias((String)this.chrs.get(int1))) {
						this.chras.add(this.currentinstance.CharacterAliases.get(this.chrs.get(int1)));
					}
				}

				string = (String)this.say.get(Rand.Next(this.say.size()));
				string = StringFunctions.EscapeChar(this.chr, this.chras, string);
				this.chr.Say(string);
			} else if (this.module.getTalker(this.owner) != null) {
				this.talker = true;
				this.talkerobj = this.module.getTalker(this.owner);
				this.talkerobj.getActual().Say((String)this.say.get(Rand.Next(this.say.size())));
			} else if (this.module.getCharacter(this.owner).Actual == null) {
				throw new InvalidParameterException();
			} else {
				this.chr = this.module.getCharacter(this.owner).Actual;
				if (this.chr != null) {
					string = (String)this.say.get(Rand.Next(this.say.size()));
					string = StringFunctions.EscapeChar(this.chr, string);
					this.chr.Say(string);
				}
			}
		}
	}

	public boolean AllowCharacterBehaviour(String string) {
		return false;
	}

	public boolean DoesInstantly() {
		return false;
	}
}
