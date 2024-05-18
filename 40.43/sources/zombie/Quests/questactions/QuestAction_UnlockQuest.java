package zombie.Quests.questactions;

import zombie.Quests.Quest;
import zombie.Quests.QuestManager;


public class QuestAction_UnlockQuest implements QuestAction {
	String Quest;

	public QuestAction_UnlockQuest(String string) {
		this.Quest = string;
	}

	public void Execute() {
		Quest quest = QuestManager.instance.FindQuest(this.Quest);
		if (quest != null) {
			quest.Unlocked = true;
		}
	}
}
