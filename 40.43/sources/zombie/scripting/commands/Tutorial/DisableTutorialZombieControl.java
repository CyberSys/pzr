package zombie.scripting.commands.Tutorial;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.scripting.commands.BaseCommand;
import zombie.ui.TutorialManager;


public class DisableTutorialZombieControl extends BaseCommand {
	int limit;

	public void init(String string, String[] stringArray) {
		if (string == null || !string.equals("Tutorial")) {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		}
	}

	public void begin() {
		TutorialManager.instance.ActiveControlZombies = false;
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
