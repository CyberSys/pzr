package zombie.scripting.commands.Module;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptModule;


public class Enabled extends BaseCommand {
	String position;
	boolean b = false;

	public void init(String string, String[] stringArray) {
		if (string == null) {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		} else {
			this.position = string;
			this.b = stringArray[0].trim().equals("true");
		}
	}

	public void begin() {
		ScriptModule scriptModule = ScriptManager.instance.getModuleNoDisableCheck(this.position);
		if (scriptModule != null) {
			scriptModule.disabled = !this.b;
		}
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
