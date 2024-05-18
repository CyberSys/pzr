package zombie.scripting.commands.Trigger;

import java.util.List;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Trigger;


public class IsLastFiredParameter extends BaseCommand {
	boolean invert = false;
	String val;
	String paramval;
	int param = 0;

	public void begin() {
	}

	public boolean getValue() {
		List list = (List)ScriptManager.instance.TriggerMap.get(this.val);
		if (list == null) {
			return false;
		} else if (list.isEmpty()) {
			return false;
		} else {
			String string = null;
			switch (this.param) {
			case 0: 
				string = ((Trigger)list.get(0)).TriggerParam;
				break;
			
			case 1: 
				string = ((Trigger)list.get(0)).TriggerParam2;
				break;
			
			case 2: 
				string = ((Trigger)list.get(0)).TriggerParam3;
			
			}

			if (this.invert) {
				return !this.paramval.equals(string);
			} else {
				return this.paramval.equals(string);
			}
		}
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		this.val = string.toLowerCase();
		if (this.val.indexOf("!") == 0) {
			this.invert = true;
			this.val = this.val.substring(1);
		}

		if (stringArray.length == 1) {
			this.paramval = stringArray[0].trim().replace("\"", "");
		} else if (stringArray.length == 2) {
			this.param = Integer.parseInt(stringArray[0].trim());
			this.paramval = stringArray[1].trim().replace("\"", "");
		}
	}

	public boolean DoesInstantly() {
		return true;
	}
}
