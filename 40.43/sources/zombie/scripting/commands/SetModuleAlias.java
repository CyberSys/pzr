package zombie.scripting.commands;

import zombie.scripting.ScriptManager;


public class SetModuleAlias extends BaseCommand {
	String name;
	String a;
	String b;

	public void init(String string, String[] stringArray) {
		this.a = stringArray[0].trim();
		this.b = stringArray[1].trim();
	}

	public void begin() {
		ScriptManager.instance.ModuleAliases.put(this.a, this.b);
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
