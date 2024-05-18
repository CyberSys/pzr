package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;


public class UnlockTaskOnComplete extends BaseCommand {
	String script;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("Quest")) {
			this.script = stringArray[0].trim().replace("\"", "");
		}
	}

	public void begin() {
		QuestCreator.AddQuestAction_UnlockQuestTask(this.script);
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
