package zombie.scripting.commands.Flags;

import zombie.scripting.commands.BaseCommand;


public class Increment extends BaseCommand {
	String name;
	String val;

	public void init(String string, String[] stringArray) {
		this.name = string.trim().replace("\"", "");
	}

	public void begin() {
		try {
			this.val = this.module.getFlagValue(this.name);
			Integer integer = Integer.parseInt(this.val);
			integer = integer + 1;
			this.module.getFlag(this.name).SetValue(integer.toString());
		} catch (Exception exception) {
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
