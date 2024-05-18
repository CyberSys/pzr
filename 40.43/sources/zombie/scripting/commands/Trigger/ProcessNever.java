package zombie.scripting.commands.Trigger;

import java.awt.Component;
import java.util.List;
import javax.swing.JOptionPane;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Trigger;


public class ProcessNever extends BaseCommand {
	String position;

	public void init(String string, String[] stringArray) {
		if (string == null) {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		} else {
			this.position = string.toLowerCase();
		}
	}

	public void begin() {
		List list = (List)ScriptManager.instance.CustomTriggerMap.get(this.position);
		for (int int1 = 0; int1 < list.size(); ++int1) {
			((Trigger)list.get(int1)).Locked = true;
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
