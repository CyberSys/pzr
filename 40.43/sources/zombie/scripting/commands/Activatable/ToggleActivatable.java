package zombie.scripting.commands.Activatable;

import zombie.iso.objects.interfaces.Activatable;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptActivatable;


public class ToggleActivatable extends BaseCommand {
	String owner;
	float num = 1.0F;

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		this.owner = string;
	}

	public void begin() {
		ScriptActivatable scriptActivatable = this.module.getActivatable(this.owner);
		if (scriptActivatable != null) {
			Activatable activatable = scriptActivatable.getActual();
			if (activatable != null) {
				activatable.Toggle();
			}
		}
	}

	public boolean DoesInstantly() {
		return true;
	}
}
