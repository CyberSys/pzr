package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;


public class RunScriptOnComplete extends BaseCommand {
	String script;

	public void init(String string, String[] stringArray) {
		if (string != null && string.equals("Quest")) {
			this.script = stringArray[0].trim().replace("\"", "");
		}
	}

	public void begin() {
		if (this.script.contains(".")) {
			QuestCreator.AddQuestAction_RunScript(this.script);
		} else {
			QuestCreator.AddQuestAction_RunScript(this.module.name + "." + this.script);
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
