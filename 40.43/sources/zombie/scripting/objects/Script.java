package zombie.scripting.objects;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;
import javax.swing.JOptionPane;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParsingUtils;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.commands.CommandFactory;
import zombie.scripting.commands.ConditionalCommand;


public class Script extends BaseScriptObject {
	public boolean Instancable = false;
	public String name;
	public ArrayList CommandList = new ArrayList();
	ConditionalCommand LastConditional = null;

	public void Load(String string, String[] stringArray) {
		this.name = string;
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			this.ParseCommand(stringArray[int1].trim());
		}
	}

	public void begin(Script.ScriptInstance scriptInstance) {
		scriptInstance.CommandIndex = 0;
		if (scriptInstance.CommandIndex < this.CommandList.size()) {
			BaseCommand baseCommand = (BaseCommand)this.CommandList.get(scriptInstance.CommandIndex);
			baseCommand.currentinstance = scriptInstance;
			baseCommand.begin();
			while (baseCommand.DoesInstantly()) {
				baseCommand.currentinstance = scriptInstance;
				baseCommand.update();
				baseCommand.Finish();
				++scriptInstance.CommandIndex;
				if (scriptInstance.CommandIndex >= this.CommandList.size()) {
					return;
				}

				baseCommand = (BaseCommand)this.CommandList.get(scriptInstance.CommandIndex);
				baseCommand.currentinstance = scriptInstance;
				baseCommand.begin();
			}
		}
	}

	public boolean finished(Script.ScriptInstance scriptInstance) {
		return scriptInstance.CommandIndex >= this.CommandList.size();
	}

	public void reset(Script.ScriptInstance scriptInstance) {
		scriptInstance.CommandIndex = 0;
		scriptInstance.Paused = false;
	}

	public void update(Script.ScriptInstance scriptInstance) {
		if (scriptInstance.CommandIndex < this.CommandList.size()) {
			if (!scriptInstance.Paused) {
				BaseCommand baseCommand = (BaseCommand)this.CommandList.get(scriptInstance.CommandIndex);
				baseCommand.currentinstance = scriptInstance;
				if (ScriptManager.instance.skipping) {
					baseCommand.updateskip();
				} else {
					baseCommand.update();
				}

				if (ScriptManager.instance.skipping || baseCommand.IsFinished()) {
					baseCommand.Finish();
					++scriptInstance.CommandIndex;
					if (scriptInstance.CommandIndex >= this.CommandList.size()) {
						return;
					}

					BaseCommand baseCommand2 = (BaseCommand)this.CommandList.get(scriptInstance.CommandIndex);
					baseCommand2.currentinstance = scriptInstance;
					baseCommand2.begin();
					while (baseCommand2.DoesInstantly()) {
						baseCommand2.update();
						baseCommand2.Finish();
						++scriptInstance.CommandIndex;
						if (scriptInstance.CommandIndex >= this.CommandList.size()) {
							return;
						}

						baseCommand2 = (BaseCommand)this.CommandList.get(scriptInstance.CommandIndex);
						baseCommand2.currentinstance = scriptInstance;
						baseCommand2.begin();
					}
				}
			}
		}
	}

	protected void ParseCommand(String string) {
		if (string.trim().length() != 0) {
			BaseCommand baseCommand = this.ReturnCommand(string);
			if (baseCommand != null) {
				baseCommand.script = this;
				this.CommandList.add(baseCommand);
			}
		}
	}

	protected BaseCommand ReturnCommand(String string) {
		if (string.indexOf("callwait") == 0) {
			string = string.replace("callwait", "");
			string = string.trim() + ".CallWait()";
		}

		if (string.indexOf("call") == 0) {
			string = string.replace("call", "");
			string = string.trim() + ".Call()";
		}

		int int1;
		int int2;
		if (string.indexOf("else") == 0) {
			int1 = string.indexOf("{");
			int2 = string.lastIndexOf("}");
			String string2 = string.substring(int1 + 1, int2);
			this.LastConditional.AddElse(string2);
			this.LastConditional = null;
			return null;
		} else {
			int int3;
			if (string.indexOf("if") == 0) {
				int1 = string.indexOf("{");
				int2 = string.lastIndexOf("}");
				int int4 = string.indexOf("(");
				int3 = string.indexOf(")");
				String string3 = string.substring(int1 + 1, int2);
				String string4 = string.substring(int4 + 1, int1).trim();
				string4 = string4.substring(0, string4.length() - 1);
				this.LastConditional = new ConditionalCommand(string4, string3, this);
				return this.LastConditional;
			} else {
				String string5 = null;
				String string6 = null;
				if (string.indexOf(".") != -1 && string.indexOf(".") < string.indexOf("(")) {
					String[] stringArray = new String[2];
					int3 = string.indexOf(".");
					int int5 = string.indexOf("(");
					int int6;
					for (int6 = string.indexOf("."); int3 < int5 && int3 != -1; int3 = string.indexOf(".", int3 + 1)) {
						int6 = int3;
					}

					stringArray[0] = string.substring(0, int6);
					stringArray[1] = string.substring(int6 + 1);
					string5 = stringArray[0];
					string6 = stringArray[1];
				} else {
					string6 = string;
				}

				return string6.trim().length() > 0 ? this.DoActual(string6, string5) : null;
			}
		}
	}

	protected BaseCommand DoActual(String string, String string2) {
		if (string.contains("Wait")) {
			string = string;
		}

		String string3 = null;
		try {
			string3 = new String(string.substring(0, string.indexOf("(")));
		} catch (Exception exception) {
		}

		string = string.replace(string3, "");
		string = string.trim().substring(1);
		string = string.trim().substring(0, string.trim().lastIndexOf(")"));
		String[] stringArray = ScriptParsingUtils.SplitExceptInbetween(string, ",", "\"");
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			stringArray[int1] = new String(stringArray[int1].trim());
		}

		boolean boolean1 = false;
		if (string3.indexOf("!") == 0) {
			string3 = string3.replace("!", "");
			boolean1 = true;
		}

		BaseCommand baseCommand = CommandFactory.CreateCommand(string3);
		if (baseCommand == null) {
			JOptionPane.showMessageDialog((Component)null, "Command: " + string3 + " not found", "Error", 0);
		}

		baseCommand.module = ScriptManager.instance.CurrentLoadingModule;
		try {
			if (boolean1) {
				baseCommand.init("!", stringArray);
			} else if (string2 != null) {
				baseCommand.init(new String(string2), stringArray);
			} else {
				baseCommand.init((String)null, stringArray);
			}
		} catch (Exception exception2) {
			String string4 = ": [";
			if (stringArray.length <= 0) {
				string4 = ".";
			} else {
				for (int int2 = 0; int2 < stringArray.length; ++int2) {
					string4 = string4 + stringArray[int2] + ", ";
				}

				string4 = string4.substring(0, string4.length() - 2) + "]";
			}

			JOptionPane.showMessageDialog((Component)null, "Command: " + string3 + " parameters incorrect" + string4, "Error", 0);
		}

		return baseCommand;
	}

	public String[] DoScriptParsing(String string, String string2) {
		Stack stack = new Stack();
		boolean boolean1 = false;
		boolean boolean2 = false;
		boolean boolean3 = false;
		boolean boolean4 = false;
		boolean boolean5 = false;
		int int1 = 0;
		int int2 = 0;
		int int3 = 0;
		int int4 = 0;
		if (string2.indexOf("}", int2) == -1) {
			boolean1 = true;
			this.Load(string, string2.split(";"));
			return string2.split(";");
		} else {
			do {
				int2 = string2.indexOf("{", int2 + 1);
				int3 = string2.indexOf("}", int3 + 1);
				int4 = string2.indexOf(";", int4 + 1);
				if ((int4 < int2 || int2 == -1 && int4 != -1) && int1 == 0) {
					stack.add(string2.substring(0, int4));
					string2 = string2.substring(int4 + 1);
					int2 = 0;
					int3 = 0;
					int4 = 0;
				} else if ((int3 >= int2 || int3 == -1) && int2 != -1) {
					if (int2 != -1) {
						int3 = int2;
						++int1;
					}
				} else {
					int2 = int3;
					--int1;
					if (int1 == 0) {
						stack.add(string2.substring(0, int3 + 1));
						string2 = string2.substring(int3 + 1);
						int2 = 0;
						int3 = 0;
						int4 = 0;
					}
				}
			}	 while (string2.trim().length() > 0);

			String[] stringArray = new String[stack.size()];
			for (int int5 = 0; int5 < stack.size(); ++int5) {
				stringArray[int5] = (String)stack.get(int5);
			}

			this.Load(string, stringArray);
			return stringArray;
		}
	}

	public boolean AllowCharacterBehaviour(String string, Script.ScriptInstance scriptInstance) {
		if (scriptInstance.CommandIndex >= this.CommandList.size()) {
			return true;
		} else {
			return scriptInstance.Paused ? true : ((BaseCommand)this.CommandList.get(scriptInstance.CommandIndex)).AllowCharacterBehaviour(string);
		}
	}

	public static class ScriptInstance {
		public HashMap luaMap = new HashMap();
		public Script.ScriptInstance parent = null;
		public Script theScript;
		public int CommandIndex = 0;
		public boolean Paused = false;
		public HashMap CharacterAliases = new HashMap();
		public HashMap CharacterAliasesR = new HashMap();
		public String ID = "";
		public boolean CharactersAlreadyInScript = false;

		public void update() {
			this.theScript.update(this);
		}

		public void addPair(String string, String string2) {
			this.luaMap.put(string.toUpperCase(), string2);
		}

		public boolean HasAlias(String string) {
			return this.CharacterAliases.containsKey(string);
		}

		public IsoGameCharacter getAlias(String string) {
			return (IsoGameCharacter)this.CharacterAliases.get(string);
		}

		public boolean finished() {
			return this.theScript.finished(this);
		}

		public void begin() {
			this.theScript.begin(this);
		}

		public boolean AllowBehaviours(IsoGameCharacter gameCharacter) {
			return this.theScript.AllowCharacterBehaviour((String)this.CharacterAliasesR.get(gameCharacter), this);
		}

		public void CopyAliases(Script.ScriptInstance scriptInstance) {
			if (scriptInstance != this) {
				this.parent = scriptInstance;
				Iterator iterator = scriptInstance.CharacterAliases.entrySet().iterator();
				this.CharacterAliases.clear();
				this.CharacterAliasesR.clear();
				this.luaMap = scriptInstance.luaMap;
				while (iterator != null && iterator.hasNext()) {
					Entry entry = (Entry)iterator.next();
					this.CharacterAliases.put(entry.getKey(), entry.getValue());
					this.CharacterAliasesR.put(entry.getValue(), entry.getKey());
				}
			}
		}
	}
}
