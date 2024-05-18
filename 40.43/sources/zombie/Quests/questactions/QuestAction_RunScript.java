package zombie.Quests.questactions;

import zombie.scripting.ScriptManager;


public class QuestAction_RunScript implements QuestAction {
	String Script;

	public QuestAction_RunScript(String string) {
		this.Script = string;
	}

	public void Execute() {
		ScriptManager.instance.PlayScript(this.Script);
	}
}
