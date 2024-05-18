package zombie.Quests;

import zombie.scripting.ScriptManager;
import zombie.scripting.objects.QuestTaskCondition;


public class QuestTask_ScriptCondition extends QuestTask {
	String ArbAction;

	public QuestTask_ScriptCondition(String string, String string2, String string3) {
		super(QuestTaskType.Custom, string, string2);
		this.ArbAction = string3;
	}

	public void Update() {
		if (!this.Complete && this.ArbActionCheck()) {
			this.Complete = true;
		}

		super.Update();
	}

	private boolean ArbActionCheck() {
		QuestTaskCondition questTaskCondition = ScriptManager.instance.getQuestCondition(this.ArbAction);
		return questTaskCondition == null ? true : questTaskCondition.ConditionPassed();
	}
}
