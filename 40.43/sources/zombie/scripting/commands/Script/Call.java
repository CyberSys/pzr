package zombie.scripting.commands.Script;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.RandomSelector;


public class Call extends BaseCommand {
	String position;

	public void init(String string, String[] stringArray) {
		if (string == null) {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		} else {
			this.position = string;
		}
	}

	public void begin() {
		if (this.module.RandomSelectorMap.containsKey(this.position)) {
			((RandomSelector)this.module.RandomSelectorMap.get(this.position)).Process(this.currentinstance);
		} else {
			this.module.PlayScript(this.position, this.currentinstance);
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
