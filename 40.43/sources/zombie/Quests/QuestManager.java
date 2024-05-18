package zombie.Quests;

import java.util.Iterator;
import java.util.Stack;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.scripting.objects.ScriptCharacter;


public class QuestManager {
	public static QuestManager instance = new QuestManager();
	public int NumActiveQuests = 0;
	public Stack QuestStack = new Stack();
	QuestTask LastAddedQuestTask;

	public QuestTask AddQuestTask_ArbitaryAction(Quest quest, String string, String string2, String string3) {
		this.LastAddedQuestTask = quest.AddQuestTask_ArbitaryAction(string, string2, string3);
		return this.LastAddedQuestTask;
	}

	public QuestTask AddQuestTask_ScriptCondition(Quest quest, String string, String string2, String string3) {
		this.LastAddedQuestTask = quest.AddQuestTask_ScriptCondition(string, string2, string3);
		return this.LastAddedQuestTask;
	}

	public QuestTask AddQuestTask_LuaCondition(Quest quest, String string, String string2, LuaClosure luaClosure, KahluaTable kahluaTable) {
		this.LastAddedQuestTask = quest.AddQuestTask_LuaCondition(string, string2, luaClosure, kahluaTable);
		return this.LastAddedQuestTask;
	}

	public QuestTask AddQuestTask_EquipItem(Quest quest, String string, String string2, String string3) {
		this.LastAddedQuestTask = quest.AddQuestTask_EquipItem(string, string2, string3);
		return this.LastAddedQuestTask;
	}

	public QuestTask AddQuestTask_FindItem(Quest quest, String string, String string2, String string3, int int1) {
		this.LastAddedQuestTask = quest.AddTask_FindItem(string, string2, string3, int1);
		return this.LastAddedQuestTask;
	}

	public QuestTask AddQuestTask_GiveItem(Quest quest, String string, String string2, String string3, String string4) {
		this.LastAddedQuestTask = quest.AddTask_GiveItem(string, string2, string3, string4);
		return this.LastAddedQuestTask;
	}

	public QuestTask AddQuestTask_GotoLocation(Quest quest, String string, String string2, int int1, int int2, int int3) {
		this.LastAddedQuestTask = quest.AddTask_GotoLocation(string, string2, int1, int2, int3);
		return this.LastAddedQuestTask;
	}

	public QuestTask AddQuestTask_TalkTo(Quest quest, String string, String string2, String string3) {
		this.LastAddedQuestTask = quest.AddTask_TalkTo(string, string2, string3);
		return this.LastAddedQuestTask;
	}

	public QuestTask AddQuestTask_UseItemOn(Quest quest, String string, String string2, String string3, ScriptCharacter scriptCharacter) {
		this.LastAddedQuestTask = quest.AddTask_UseItemOn(string, string2, string3, scriptCharacter);
		return this.LastAddedQuestTask;
	}

	public Quest CreateQuest(String string, String string2) {
		Quest quest = new Quest(string, string2);
		this.QuestStack.add(quest);
		++this.NumActiveQuests;
		return quest;
	}

	public Quest FindQuest(String string) {
		Iterator iterator = this.QuestStack.iterator();
		Quest quest;
		do {
			if (!iterator.hasNext()) {
				return null;
			}

			quest = (Quest)iterator.next();
		} while (!quest.getInternalName().trim().equalsIgnoreCase(string));

		return quest;
	}

	public int getNumQuests() {
		return this.NumActiveQuests;
	}

	public Quest getQuest(int int1) {
		if (int1 < 0) {
			return null;
		} else {
			return int1 >= this.QuestStack.size() ? null : (Quest)this.QuestStack.get(int1);
		}
	}

	public String getQuestName(int int1) {
		if (int1 < 0) {
			return null;
		} else {
			return int1 >= this.QuestStack.size() ? null : ((Quest)this.QuestStack.get(int1)).getName();
		}
	}

	public boolean QuestComplete(int int1) {
		if (int1 < 0) {
			return false;
		} else {
			return int1 >= this.QuestStack.size() ? false : ((Quest)this.QuestStack.get(int1)).Complete;
		}
	}

	public void Update() {
		for (int int1 = 0; int1 < this.QuestStack.size(); ++int1) {
			if (((Quest)this.QuestStack.get(int1)).Unlocked) {
				((Quest)this.QuestStack.get(int1)).Update();
			}
		}
	}

	public void ClearQuest(String string) {
		for (int int1 = 0; int1 < this.QuestStack.size(); ++int1) {
			if (string.equals(((Quest)this.QuestStack.get(int1)).getInternalName())) {
				this.QuestStack.remove(int1);
				return;
			}
		}
	}
}
