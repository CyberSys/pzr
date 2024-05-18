package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Stack;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.commands.Trigger.TimeSinceLastRan;


public class Trigger extends Script {
	public static Integer tot = 0;
	public String name;
	Stack Conditions = new Stack();
	public boolean Locked = false;
	public ArrayList scriptsToCall = new ArrayList();
	public String TriggerParam = null;
	public String TriggerParam2 = null;
	public String TriggerParam3 = null;

	public void Load(String string, String[] stringArray) {
		this.name = string + tot;
		Integer integer = tot;
		tot = tot + 1;
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			this.DoLine(stringArray[int1].trim());
		}
	}

	private void DoLine(String string) {
		if (!string.isEmpty()) {
			if (string.indexOf("call") == 0) {
				string = string.replace("call", "").trim();
				this.scriptsToCall.add(string);
			} else {
				String[] stringArray = string.split("&&");
				for (int int1 = 0; int1 < stringArray.length; ++int1) {
					if (!stringArray[int1].trim().isEmpty()) {
						BaseCommand baseCommand = this.ReturnCommand(stringArray[int1].trim());
						if (baseCommand instanceof TimeSinceLastRan) {
							((TimeSinceLastRan)baseCommand).triggerInst = this.name;
						}

						this.Conditions.add(baseCommand);
					}
				}
			}
		}
	}

	public boolean ConditionPassed() {
		for (int int1 = 0; int1 < this.Conditions.size(); ++int1) {
			if (!((BaseCommand)this.Conditions.get(int1)).getValue()) {
				return false;
			}
		}

		return true;
	}

	public void Process() {
		if (this.ConditionPassed()) {
			if (ScriptManager.instance.CustomTriggerLastRan.containsKey(this.name)) {
				ScriptManager.instance.CustomTriggerLastRan.put(this.name, 0);
			}

			for (int int1 = 0; int1 < this.scriptsToCall.size(); ++int1) {
				String string = (String)this.scriptsToCall.get(int1);
				this.module.PlayScript(string);
			}
		}
	}
}
