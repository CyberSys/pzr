package zombie.scripting.commands.Script;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.RandomSelector;
import zombie.scripting.objects.Script;


public class CallAndWait extends BaseCommand {
	String position;
	String check;
	Script.ScriptInstance inst = null;

	public void init(String string, String[] stringArray) {
		if (string == null) {
			JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + string, "Error", 0);
		} else {
			this.position = string;
		}
	}

	public void begin() {
		if (this.module.RandomSelectorMap.containsKey(this.position)) {
			this.inst = ((RandomSelector)this.module.RandomSelectorMap.get(this.position)).Process(this.currentinstance);
		} else {
			this.inst = this.module.PlayScript(this.position, this.currentinstance);
			this.check = this.position;
		}
	}

	public boolean IsFinished() {
		return !ScriptManager.instance.IsScriptPlaying(this.inst);
	}

	public void update() {
	}

	public boolean DoesInstantly() {
		return false;
	}
}
