package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;


public class AddUseItemOnTask extends BaseCommand {
	String name;
	String description;
	String item;
	String character;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("Quest")) {
			this.name = stringArray[0].trim().replace("\"", "");
			this.description = stringArray[1].trim().replace("\"", "");
			this.description = this.module.getLanguage(this.description);
			if (this.description.indexOf("\"") == 0) {
				this.description = this.description.substring(1);
				this.description = this.description.substring(0, this.description.length() - 1);
			}

			this.item = stringArray[2].trim().replace("\"", "");
			this.character = stringArray[3].trim();
		}
	}

	public void begin() {
		QuestCreator.AddQuestTask_UseItemOn(this.name, this.description, this.item, this.module.getCharacter(this.character));
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
