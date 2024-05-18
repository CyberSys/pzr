package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;


public class CreateQuest extends BaseCommand {
	String name;
	String description;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("Quest")) {
			this.name = stringArray[0].trim().replace("\"", "");
			this.description = stringArray[1].replace("\"", "");
			this.description = this.module.getLanguage(this.description);
			if (this.description.indexOf("\"") == 0) {
				this.description = this.description.substring(1);
				this.description = this.description.substring(0, this.description.length() - 1);
			}
		}
	}

	public void begin() {
		QuestCreator.CreateQuest(this.name, this.description);
	}

	public boolean IsFinished() {
		return true;
	}

	public boolean getValue() {
		return false;
	}

	public void update() {
	}

	public boolean DoesInstantly() {
		return true;
	}
}
