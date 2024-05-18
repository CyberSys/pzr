package zombie.scripting.commands;

import zombie.scripting.objects.Script;
import zombie.scripting.objects.ScriptModule;


public abstract class BaseCommand {
	public ScriptModule module;
	public Script script = null;
	public Script.ScriptInstance currentinstance = null;

	public abstract void begin();

	public abstract boolean IsFinished();

	public abstract void update();

	public abstract void init(String string, String[] stringArray);

	public abstract boolean DoesInstantly();

	public boolean getValue() {
		return false;
	}

	public void Finish() {
	}

	public boolean AllowCharacterBehaviour(String string) {
		return true;
	}

	public void updateskip() {
	}
}
