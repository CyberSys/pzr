package zombie.scripting.commands.Character;

import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptCharacter;
import zombie.scripting.objects.ScriptTalker;


public class SayCommand extends BaseCommand {
	String owner;
	Stack say = new Stack();
	IsoGameCharacter chr;
	ScriptTalker talkerobj = null;
	boolean talker = false;

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
			string2 = this.module.getLanguage(string2);
			if (string2.indexOf("\"") == 0) {
				string2 = string2.substring(1);
				string2 = string2.substring(0, string2.length() - 1);
			}

			this.say.add(string2);
		}
	}

	public void begin() {
		if (!ScriptManager.instance.skipping) {
			if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
				this.chr = this.currentinstance.getAlias(this.owner);
				String string = (String)this.say.get(Rand.Next(this.say.size()));
				string = StringFunctions.EscapeChar(this.chr, string);
				Entry entry;
				String string2;
				for (Iterator iterator = this.currentinstance.luaMap.entrySet().iterator(); iterator.hasNext(); string = string.replace(string2, (CharSequence)entry.getValue())) {
					entry = (Entry)iterator.next();
					string2 = "$" + ((String)entry.getKey()).toUpperCase() + "$";
				}

				this.chr.Say(string);
			} else if (this.module.getTalker(this.owner) != null) {
				this.talker = true;
				this.talkerobj = this.module.getTalker(this.owner);
				this.talkerobj.getActual().Say((String)this.say.get(Rand.Next(this.say.size())));
			} else {
				ScriptCharacter scriptCharacter = this.module.getCharacter(this.owner);
				if (scriptCharacter != null) {
					this.chr = scriptCharacter.Actual;
					if (this.chr != null) {
						String string3 = (String)this.say.get(Rand.Next(this.say.size()));
						string3 = StringFunctions.EscapeChar(this.chr, string3);
						this.chr.Say(string3);
					}
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
