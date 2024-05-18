package zombie.scripting.objects;

import java.util.Stack;
import zombie.scripting.commands.BaseCommand;


public class QuestTaskCondition extends Script {
	Stack Conditions = new Stack();

	public void Load(String string, String[] stringArray) {
		String string2 = stringArray[0].trim();
		if (string2 != null) {
			String[] stringArray2 = string2.split("&&");
			for (int int1 = 0; int1 < stringArray2.length; ++int1) {
				if (!stringArray2[int1].trim().isEmpty()) {
					this.Conditions.add(this.ReturnCommand(stringArray2[int1].trim()));
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
}
