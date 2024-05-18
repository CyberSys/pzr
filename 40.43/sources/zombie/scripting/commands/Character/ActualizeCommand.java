package zombie.scripting.commands.Character;

import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;


public class ActualizeCommand extends BaseCommand {
	public String command;
	public String chr;
	public String[] params;
	String owner;
	int x;
	int y;
	int z;

	public void init(String string, String[] stringArray) {
		if (stringArray.length == 1) {
			Waypoint waypoint = this.module.getWaypoint(stringArray[0]);
			this.x = waypoint.x;
			this.y = waypoint.y;
			this.z = waypoint.z;
			this.owner = string;
		}
	}

	public void begin() {
		this.module.getCharacter(this.owner).Actualise(this.x, this.y, this.z);
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
