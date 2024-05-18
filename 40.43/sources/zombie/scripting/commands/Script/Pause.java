package zombie.scripting.commands.Script;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;


public class Pause extends BaseCommand {
	String position;

	public void init(String string, String[] stringArray) {
		if (string == null) {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		} else {
			this.position = string;
		}
	}

	public void begin() {
		ScriptManager.instance.PauseScript(this.position);
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
