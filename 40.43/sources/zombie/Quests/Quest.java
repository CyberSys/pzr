package zombie.Quests;

import java.util.Iterator;
import java.util.Stack;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.Quests.questactions.QuestAction;
import zombie.scripting.objects.ScriptCharacter;


public class Quest implements Completable {
	public boolean Complete = false;
	public boolean Failed = false;
	public int NumQuestTasks = 0;
	public Stack OnCompleteActions = new Stack();
	public Stack QuestTaskStack = new Stack();
	public boolean Unlocked = false;
	String InternalQuestName = "Default Quest Name";
	String QuestName = "Default Quest Name";

	public Quest(String string, String string2) {
		this.InternalQuestName = string;
		this.QuestName = string2;
		this.NumQuestTasks = 0;
		this.Complete = false;
		this.Failed = false;
	}

	public QuestTask AddQuestTask_EquipItem(String string, String string2, String string3) {
		QuestTask_EquipItem questTask_EquipItem = new QuestTask_EquipItem(string, string2, string3);
		this.QuestTaskStack.add(questTask_EquipItem);
		++this.NumQuestTasks;
		return questTask_EquipItem;
	}

	public QuestTask AddTask_FindItem(String string, String string2, String string3, int int1) {
		QuestTask_FindItem questTask_FindItem = new QuestTask_FindItem(string, string2, string3, int1);
		this.QuestTaskStack.add(questTask_FindItem);
		++this.NumQuestTasks;
		return questTask_FindItem;
	}

	public QuestTask AddTask_GiveItem(String string, String string2, String string3, String string4) {
		QuestTask_GiveItem questTask_GiveItem = new QuestTask_GiveItem(string, string2, string3, string4);
		this.QuestTaskStack.add(questTask_GiveItem);
		++this.NumQuestTasks;
		return questTask_GiveItem;
	}

	public QuestTask AddTask_GotoLocation(String string, String string2, int int1, int int2, int int3) {
		QuestTask_GotoLocation questTask_GotoLocation = new QuestTask_GotoLocation(string, string2, int1, int2, int3);
		this.QuestTaskStack.add(questTask_GotoLocation);
		++this.NumQuestTasks;
		return questTask_GotoLocation;
	}

	public QuestTask AddTask_TalkTo(String string, String string2, String string3) {
		QuestTask_TalkTo questTask_TalkTo = new QuestTask_TalkTo(string, string2, string3);
		this.QuestTaskStack.add(questTask_TalkTo);
		++this.NumQuestTasks;
		return questTask_TalkTo;
	}

	public QuestTask AddTask_UseItemOn(String string, String string2, String string3, ScriptCharacter scriptCharacter) {
		QuestTask_UseItemOn questTask_UseItemOn = new QuestTask_UseItemOn(string, string2, string3, scriptCharacter);
		this.QuestTaskStack.add(questTask_UseItemOn);
		++this.NumQuestTasks;
		return questTask_UseItemOn;
	}

	public QuestTask FindTask(String string) {
		for (int int1 = 0; int1 < this.QuestTaskStack.size(); ++int1) {
			if (((QuestTask)this.QuestTaskStack.get(int1)).getInternalName().equals(string)) {
				return (QuestTask)this.QuestTaskStack.get(int1);
			}
		}

		return null;
	}

	public String getInternalName() {
		return this.InternalQuestName;
	}

	public String getName() {
		return this.QuestName;
	}

	public int getNumTasks() {
		return this.QuestTaskStack.size();
	}

	public String getTaskName(int int1) {
		if (int1 < 0) {
			return "Task does not exist.";
		} else {
			return int1 >= this.QuestTaskStack.size() ? "Task does not exist." : ((QuestTask)this.QuestTaskStack.get(int1)).getName();
		}
	}

	public boolean IsComplete() {
		return this.Complete;
	}

	public boolean IsFailed() {
		return this.Failed;
	}

	public boolean TaskComplete(int int1) {
		if (int1 < 0) {
			return false;
		} else {
			return int1 >= this.QuestTaskStack.size() ? false : ((QuestTask)this.QuestTaskStack.get(int1)).IsComplete();
		}
	}

	public boolean TaskFailed(int int1) {
		if (int1 < 0) {
			return false;
		} else {
			return int1 >= this.QuestTaskStack.size() ? false : ((QuestTask)this.QuestTaskStack.get(int1)).IsFailed();
		}
	}

	public void Update() {
		boolean boolean1;
		int int1;
		if (!this.Complete) {
			boolean1 = true;
			for (int1 = 0; int1 < this.QuestTaskStack.size(); ++int1) {
				if (!((QuestTask)this.QuestTaskStack.get(int1)).Unlocked) {
					boolean1 = false;
				} else if (!((QuestTask)this.QuestTaskStack.get(int1)).IsComplete()) {
					boolean1 = false;
				}
			}

			this.Complete = boolean1;
			if (this.Complete) {
				Iterator iterator = this.OnCompleteActions.iterator();
				while (iterator.hasNext()) {
					QuestAction questAction = (QuestAction)iterator.next();
					questAction.Execute();
				}
			}
		}

		if (!this.Failed) {
			boolean1 = false;
			for (int1 = 0; int1 < this.QuestTaskStack.size(); ++int1) {
				if (((QuestTask)this.QuestTaskStack.get(int1)).IsFailed()) {
					boolean1 = true;
				}
			}

			this.Failed = boolean1;
		}

		for (int int2 = 0; int2 < this.QuestTaskStack.size(); ++int2) {
			((QuestTask)this.QuestTaskStack.get(int2)).Update();
		}
	}

	QuestTask AddQuestTask_ArbitaryAction(String string, String string2, String string3) {
		QuestTask_ArbitaryAction questTask_ArbitaryAction = new QuestTask_ArbitaryAction(string, string2, string3);
		this.QuestTaskStack.add(questTask_ArbitaryAction);
		++this.NumQuestTasks;
		return questTask_ArbitaryAction;
	}

	QuestTask AddQuestTask_ScriptCondition(String string, String string2, String string3) {
		QuestTask_ScriptCondition questTask_ScriptCondition = new QuestTask_ScriptCondition(string, string2, string3);
		this.QuestTaskStack.add(questTask_ScriptCondition);
		++this.NumQuestTasks;
		return questTask_ScriptCondition;
	}

	QuestTask AddQuestTask_LuaCondition(String string, String string2, LuaClosure luaClosure, KahluaTable kahluaTable) {
		QuestTask_LuaCondition questTask_LuaCondition = new QuestTask_LuaCondition(string, string2, luaClosure, kahluaTable);
		this.QuestTaskStack.add(questTask_LuaCondition);
		++this.NumQuestTasks;
		return questTask_LuaCondition;
	}
}
