package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;


public class UnlockLast extends BaseCommand {
	String quest = null;

	public void init(String string, String[] stringArray) {
		if (string == null || !string.equals("Quest")) {
			;
		}
	}

	public void begin() {
		QuestCreator.Unlock();
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
