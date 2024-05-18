package zombie.scripting.commands.DayNight;

import zombie.GameTime;
import zombie.scripting.commands.BaseCommand;


public class IsNight extends BaseCommand {
	boolean invert = false;
	String character;

	public void begin() {
	}

	public boolean getValue() {
		boolean boolean1 = false;
		if (GameTime.getInstance().getTimeOfDay() > 20.0F || GameTime.getInstance().getTimeOfDay() < 6.0F) {
			boolean1 = true;
		}

		if (this.invert) {
			boolean1 = !boolean1;
		}

		return boolean1;
	}

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("!")) {
			this.invert = true;
		}
	}

	public boolean DoesInstantly() {
		return true;
	}
}
