package zombie.scripting.commands.quest;

import zombie.Quests.Quest;
import zombie.Quests.QuestManager;
import zombie.scripting.commands.BaseCommand;
import zombie.ui.QuestPanel;


public class LockQuest extends BaseCommand {
	String quest = null;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("Quest")) {
			if (stringArray.length == 1) {
				this.quest = stringArray[0].trim().replace("\"", "");
			}
		}
	}

	public void begin() {
		Quest quest = QuestManager.instance.FindQuest(this.quest);
		if (quest != null) {
			quest.Unlocked = false;
			if (QuestPanel.instance.ActiveQuest == quest) {
				QuestPanel.instance.ActiveQuest = null;
			}
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
