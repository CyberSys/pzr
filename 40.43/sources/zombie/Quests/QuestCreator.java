package zombie.Quests;

import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.Quests.questactions.QuestAction;
import zombie.Quests.questactions.QuestAction_CallLua;
import zombie.Quests.questactions.QuestAction_RunScript;
import zombie.Quests.questactions.QuestAction_StartConversation;
import zombie.Quests.questactions.QuestAction_TutorialIcon;
import zombie.Quests.questactions.QuestAction_UnlockQuest;
import zombie.Quests.questactions.QuestAction_UnlockQuestTask;
import zombie.characters.Talker;
import zombie.iso.IsoObject;
import zombie.scripting.objects.ScriptCharacter;
import zombie.ui.QuestPanel;
import zombie.ui.UIElement;


public class QuestCreator {
	static Quest CurrentQuest;
	static QuestTask CurrentQuestTask;
	static int UnlockNext = 0;
	static QuestTask UnlockNextQuest = null;

	public static void AddQuestAction_StartConversation(Talker talker, Talker talker2, String string) {
		QuestAction_StartConversation questAction_StartConversation = new QuestAction_StartConversation(string, talker, talker2);
		AddAction(questAction_StartConversation);
	}

	public static void AddQuestAction_TutorialIcon(String string, String string2, IsoObject object, boolean boolean1, float float1) {
		QuestAction_TutorialIcon questAction_TutorialIcon = new QuestAction_TutorialIcon(string, string2, object, boolean1, float1);
		AddAction(questAction_TutorialIcon);
	}

	public static void AddQuestAction_TutorialIcon(UIElement uIElement, int int1, int int2, String string, String string2, boolean boolean1) {
		QuestAction_TutorialIcon questAction_TutorialIcon = new QuestAction_TutorialIcon(uIElement, int1, int2, string, string2, boolean1);
		AddAction(questAction_TutorialIcon);
	}

	public static void AddQuestAction_UnlockQuest(String string) {
		QuestAction_UnlockQuest questAction_UnlockQuest = new QuestAction_UnlockQuest(string);
		AddAction(questAction_UnlockQuest);
	}

	public static void AddQuestAction_RunScript(String string) {
		QuestAction_RunScript questAction_RunScript = new QuestAction_RunScript(string);
		AddAction(questAction_RunScript);
	}

	public static void AddQuestAction_CallLua(LuaClosure luaClosure, KahluaTable kahluaTable) {
		QuestAction_CallLua questAction_CallLua = new QuestAction_CallLua(luaClosure, kahluaTable);
		AddAction(questAction_CallLua);
	}

	public static void AddQuestAction_UnlockQuestTask(String string) {
		QuestAction_UnlockQuestTask questAction_UnlockQuestTask = new QuestAction_UnlockQuestTask(CurrentQuest, string);
		AddAction(questAction_UnlockQuestTask);
	}

	public static void AddQuestTask_ArbitaryAction(String string, String string2, String string3) {
		QuestTask questTask = QuestManager.instance.AddQuestTask_ArbitaryAction(CurrentQuest, string, string2, string3);
		HandleUnlockNext(questTask);
		CurrentQuestTask = questTask;
	}

	public static void AddQuestTask_LuaCondition(String string, String string2, LuaClosure luaClosure, KahluaTable kahluaTable) {
		QuestTask questTask = QuestManager.instance.AddQuestTask_LuaCondition(CurrentQuest, string, string2, luaClosure, kahluaTable);
		HandleUnlockNext(questTask);
		CurrentQuestTask = questTask;
	}

	public static void AddQuestTask_ScriptCondition(String string, String string2, String string3) {
		QuestTask questTask = QuestManager.instance.AddQuestTask_ScriptCondition(CurrentQuest, string, string2, string3);
		HandleUnlockNext(questTask);
		CurrentQuestTask = questTask;
	}

	public static void AddQuestTask_EquipItem(String string, String string2, String string3) {
		QuestTask questTask = QuestManager.instance.AddQuestTask_EquipItem(CurrentQuest, string, string2, string3);
		HandleUnlockNext(questTask);
		CurrentQuestTask = questTask;
	}

	public static void AddQuestTask_FindItem(String string, String string2, String string3, int int1) {
		QuestTask questTask = QuestManager.instance.AddQuestTask_FindItem(CurrentQuest, string, string2, string3, int1);
		HandleUnlockNext(questTask);
		CurrentQuestTask = questTask;
	}

	public static void AddQuestTask_GiveItem(String string, String string2, String string3, String string4) {
		QuestTask questTask = QuestManager.instance.AddQuestTask_GiveItem(CurrentQuest, string, string2, string3, string4);
		HandleUnlockNext(questTask);
		CurrentQuestTask = questTask;
	}

	public static void AddQuestTask_GotoLocation(String string, String string2, int int1, int int2, int int3) {
		QuestTask questTask = CurrentQuest.AddTask_GotoLocation(string, string2, int1, int2, int3);
		HandleUnlockNext(questTask);
		CurrentQuestTask = questTask;
	}

	public static void AddQuestTask_TalkTo(String string, String string2, String string3) {
		QuestTask questTask = QuestManager.instance.AddQuestTask_TalkTo(CurrentQuest, string, string2, string3);
		HandleUnlockNext(questTask);
		CurrentQuestTask = questTask;
	}

	public static void AddQuestTask_UseItemOn(String string, String string2, String string3, ScriptCharacter scriptCharacter) {
		QuestTask questTask = QuestManager.instance.AddQuestTask_UseItemOn(CurrentQuest, string, string2, string3, scriptCharacter);
		HandleUnlockNext(questTask);
		CurrentQuestTask = questTask;
	}

	public static void ClearQuest(String string) {
		QuestManager.instance.ClearQuest(string);
	}

	public static void CreateQuest(String string, String string2) {
		Quest quest = QuestManager.instance.CreateQuest(string, string2);
		HandleUnlockNext(quest);
		CurrentQuest = quest;
		CurrentQuestTask = null;
	}

	public static void LockLast() {
		if (CurrentQuestTask == null) {
			CurrentQuest.Unlocked = false;
		} else {
			CurrentQuestTask.Unlocked = false;
		}
	}

	public static void SetToUnlockNext() {
		UnlockNextQuest = CurrentQuestTask;
		UnlockNext = 1;
	}

	public static void SetToUnlockNext(int int1) {
		UnlockNextQuest = CurrentQuestTask;
		UnlockNext = int1;
	}

	public static void Unlock() {
		if (CurrentQuestTask == null) {
			CurrentQuest.Unlocked = true;
		} else {
			CurrentQuestTask.Unlocked = true;
		}
	}

	public static void UnlockButHide() {
		CurrentQuestTask.Unlocked = true;
		CurrentQuestTask.Hidden = true;
	}

	public static void UnlockQuest(String string) {
		Quest quest = QuestManager.instance.FindQuest(string);
		if (quest != null) {
			quest.Unlocked = true;
			QuestPanel.instance.ActiveQuest = quest;
		}
	}

	public static void AddAction(QuestAction questAction) {
		if (CurrentQuestTask == null) {
			CurrentQuest.OnCompleteActions.add(questAction);
		} else {
			CurrentQuestTask.OnCompleteActions.add(questAction);
		}
	}

	static void HandleUnlockNext(QuestTask questTask) {
		if (UnlockNext > 0) {
			if (UnlockNextQuest != null) {
				UnlockNextQuest.OnCompleteActions.add(new QuestAction_UnlockQuestTask(CurrentQuest, questTask.InternalTaskName));
			} else {
				CurrentQuest.OnCompleteActions.add(new QuestAction_UnlockQuestTask(CurrentQuest, questTask.InternalTaskName));
			}

			--UnlockNext;
			if (UnlockNext == 0) {
				UnlockNextQuest = null;
			}
		}
	}

	static void HandleUnlockNext(Quest quest) {
		if (UnlockNext > 0) {
			if (UnlockNextQuest != null) {
				UnlockNextQuest.OnCompleteActions.add(new QuestAction_UnlockQuest(quest.InternalQuestName));
			} else {
				CurrentQuest.OnCompleteActions.add(new QuestAction_UnlockQuest(quest.InternalQuestName));
			}

			--UnlockNext;
			if (UnlockNext == 0) {
				UnlockNextQuest = null;
			}
		}
	}
}
