package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;


public class UnlockTasksOnComplete extends BaseCommand {
	int count;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("Quest")) {
			this.count = Integer.parseInt(stringArray[0].trim());
		}
	}

	public void begin() {
		QuestCreator.SetToUnlockNext(this.count);
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
