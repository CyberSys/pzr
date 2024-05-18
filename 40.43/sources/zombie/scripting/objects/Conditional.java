package zombie.scripting.objects;

import java.util.ArrayList;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.commands.ConditionalCommand;


public class Conditional extends Script {
	ArrayList Conditions = new ArrayList();
	public ConditionalCommand command;

	public Conditional(String string, String string2) {
		this.DoScriptParsing("", string2);
		if (string != null) {
			String[] stringArray = string.split("&&");
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				if (!stringArray[int1].trim().isEmpty()) {
					this.Conditions.add(this.ReturnCommand(stringArray[int1].trim()));
				}
			}
		}
	}

	public Conditional(String string, String string2, ConditionalCommand conditionalCommand) {
		this.command = conditionalCommand;
		this.DoScriptParsing("", string2);
		if (string != null) {
			String[] stringArray = string.split("&&");
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				if (!stringArray[int1].trim().isEmpty()) {
					this.Conditions.add(this.ReturnCommand(stringArray[int1].trim()));
				}
			}
		}
	}

	public boolean ConditionPassed(Script.ScriptInstance scriptInstance) {
		for (int int1 = 0; int1 < this.Conditions.size(); ++int1) {
			((BaseCommand)this.Conditions.get(int1)).currentinstance = scriptInstance;
			if (!((BaseCommand)this.Conditions.get(int1)).getValue()) {
				return false;
			}
		}

		return true;
	}
}
