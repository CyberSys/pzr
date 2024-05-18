package zombie.scripting.commands;

import zombie.scripting.objects.Conditional;


public class WaitCommand extends BaseCommand {
	int frames = 0;
	Conditional con = null;
	String obj = null;

	public boolean IsFinished() {
		if (this.con != null) {
			boolean boolean1 = this.con.ConditionPassed(this.currentinstance);
			if (boolean1) {
				boolean1 = boolean1;
			}

			return boolean1;
		} else {
			return this.frames <= 0;
		}
	}

	public void update() {
		--this.frames;
	}

	public void init(String string, String[] stringArray) {
		this.obj = string;
		try {
			this.frames = (int)(30.0F * Float.parseFloat(stringArray[0].trim()));
		} catch (Exception exception) {
			String string2 = stringArray[0].trim();
			for (int int1 = 1; int1 < stringArray.length; ++int1) {
				string2 = string2 + ", ";
				string2 = string2 + stringArray[int1];
			}

			this.con = new Conditional(string2.trim(), "");
		}
	}

	public boolean AllowCharacterBehaviour(String string) {
		return !string.equals(this.obj);
	}

	public void begin() {
	}

	public boolean DoesInstantly() {
		return false;
	}
}
