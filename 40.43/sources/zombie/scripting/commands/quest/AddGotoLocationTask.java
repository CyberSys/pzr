package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;


public class AddGotoLocationTask extends BaseCommand {
	String name;
	String description;
	String location;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("Quest")) {
			this.name = stringArray[0].trim().replace("\"", "");
			this.description = stringArray[1].trim().replace("\"", "");
			this.description = this.module.getLanguage(this.description);
			if (this.description.indexOf("\"") == 0) {
				this.description = this.description.substring(1);
				this.description = this.description.substring(0, this.description.length() - 1);
			}

			this.location = stringArray[2].trim().replace("\"", "");
		}
	}

	public void begin() {
		Waypoint waypoint = this.module.getWaypoint(this.location);
		if (waypoint != null) {
			QuestCreator.AddQuestTask_GotoLocation(this.name, this.description, waypoint.x, waypoint.y, waypoint.z);
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
