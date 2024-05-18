package zombie.scripting.commands.Character;

import java.security.InvalidParameterException;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;


public class StopAction extends BaseCommand {
	String owner;
	IsoGameCharacter chr;
	float num = 1.0F;

	public boolean IsFinished() {
		return true;
	}

	public void update() {
	}

	public void init(String string, String[] stringArray) {
		this.owner = string;
	}

	public void begin() {
		if (this.module.getCharacter(this.owner).Actual == null) {
			throw new InvalidParameterException();
		} else {
			this.module.getCharacter(this.owner).Actual.StopAllActionQueue();
		}
	}

	public boolean DoesInstantly() {
		return true;
	}
}
