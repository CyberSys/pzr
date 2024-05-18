package zombie.Quests.questactions;

import zombie.Quests.Quest;
import zombie.Quests.QuestTask;


public class QuestAction_UnlockQuestTask implements QuestAction {
	Quest Quest;
	String Task;

	public QuestAction_UnlockQuestTask(Quest quest, String string) {
		this.Task = string;
		this.Quest = quest;
	}

	public void Execute() {
		QuestTask questTask = this.Quest.FindTask(this.Task);
		questTask.Unlocked = true;
	}
}
