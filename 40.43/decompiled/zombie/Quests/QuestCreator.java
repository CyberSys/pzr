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

   public static void AddQuestAction_StartConversation(Talker var0, Talker var1, String var2) {
      QuestAction_StartConversation var3 = new QuestAction_StartConversation(var2, var0, var1);
      AddAction(var3);
   }

   public static void AddQuestAction_TutorialIcon(String var0, String var1, IsoObject var2, boolean var3, float var4) {
      QuestAction_TutorialIcon var5 = new QuestAction_TutorialIcon(var0, var1, var2, var3, var4);
      AddAction(var5);
   }

   public static void AddQuestAction_TutorialIcon(UIElement var0, int var1, int var2, String var3, String var4, boolean var5) {
      QuestAction_TutorialIcon var6 = new QuestAction_TutorialIcon(var0, var1, var2, var3, var4, var5);
      AddAction(var6);
   }

   public static void AddQuestAction_UnlockQuest(String var0) {
      QuestAction_UnlockQuest var1 = new QuestAction_UnlockQuest(var0);
      AddAction(var1);
   }

   public static void AddQuestAction_RunScript(String var0) {
      QuestAction_RunScript var1 = new QuestAction_RunScript(var0);
      AddAction(var1);
   }

   public static void AddQuestAction_CallLua(LuaClosure var0, KahluaTable var1) {
      QuestAction_CallLua var2 = new QuestAction_CallLua(var0, var1);
      AddAction(var2);
   }

   public static void AddQuestAction_UnlockQuestTask(String var0) {
      QuestAction_UnlockQuestTask var1 = new QuestAction_UnlockQuestTask(CurrentQuest, var0);
      AddAction(var1);
   }

   public static void AddQuestTask_ArbitaryAction(String var0, String var1, String var2) {
      QuestTask var3 = QuestManager.instance.AddQuestTask_ArbitaryAction(CurrentQuest, var0, var1, var2);
      HandleUnlockNext(var3);
      CurrentQuestTask = var3;
   }

   public static void AddQuestTask_LuaCondition(String var0, String var1, LuaClosure var2, KahluaTable var3) {
      QuestTask var4 = QuestManager.instance.AddQuestTask_LuaCondition(CurrentQuest, var0, var1, var2, var3);
      HandleUnlockNext(var4);
      CurrentQuestTask = var4;
   }

   public static void AddQuestTask_ScriptCondition(String var0, String var1, String var2) {
      QuestTask var3 = QuestManager.instance.AddQuestTask_ScriptCondition(CurrentQuest, var0, var1, var2);
      HandleUnlockNext(var3);
      CurrentQuestTask = var3;
   }

   public static void AddQuestTask_EquipItem(String var0, String var1, String var2) {
      QuestTask var3 = QuestManager.instance.AddQuestTask_EquipItem(CurrentQuest, var0, var1, var2);
      HandleUnlockNext(var3);
      CurrentQuestTask = var3;
   }

   public static void AddQuestTask_FindItem(String var0, String var1, String var2, int var3) {
      QuestTask var4 = QuestManager.instance.AddQuestTask_FindItem(CurrentQuest, var0, var1, var2, var3);
      HandleUnlockNext(var4);
      CurrentQuestTask = var4;
   }

   public static void AddQuestTask_GiveItem(String var0, String var1, String var2, String var3) {
      QuestTask var4 = QuestManager.instance.AddQuestTask_GiveItem(CurrentQuest, var0, var1, var2, var3);
      HandleUnlockNext(var4);
      CurrentQuestTask = var4;
   }

   public static void AddQuestTask_GotoLocation(String var0, String var1, int var2, int var3, int var4) {
      QuestTask var5 = CurrentQuest.AddTask_GotoLocation(var0, var1, var2, var3, var4);
      HandleUnlockNext(var5);
      CurrentQuestTask = var5;
   }

   public static void AddQuestTask_TalkTo(String var0, String var1, String var2) {
      QuestTask var3 = QuestManager.instance.AddQuestTask_TalkTo(CurrentQuest, var0, var1, var2);
      HandleUnlockNext(var3);
      CurrentQuestTask = var3;
   }

   public static void AddQuestTask_UseItemOn(String var0, String var1, String var2, ScriptCharacter var3) {
      QuestTask var4 = QuestManager.instance.AddQuestTask_UseItemOn(CurrentQuest, var0, var1, var2, var3);
      HandleUnlockNext(var4);
      CurrentQuestTask = var4;
   }

   public static void ClearQuest(String var0) {
      QuestManager.instance.ClearQuest(var0);
   }

   public static void CreateQuest(String var0, String var1) {
      Quest var2 = QuestManager.instance.CreateQuest(var0, var1);
      HandleUnlockNext(var2);
      CurrentQuest = var2;
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

   public static void SetToUnlockNext(int var0) {
      UnlockNextQuest = CurrentQuestTask;
      UnlockNext = var0;
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

   public static void UnlockQuest(String var0) {
      Quest var1 = QuestManager.instance.FindQuest(var0);
      if (var1 != null) {
         var1.Unlocked = true;
         QuestPanel.instance.ActiveQuest = var1;
      }
   }

   public static void AddAction(QuestAction var0) {
      if (CurrentQuestTask == null) {
         CurrentQuest.OnCompleteActions.add(var0);
      } else {
         CurrentQuestTask.OnCompleteActions.add(var0);
      }

   }

   static void HandleUnlockNext(QuestTask var0) {
      if (UnlockNext > 0) {
         if (UnlockNextQuest != null) {
            UnlockNextQuest.OnCompleteActions.add(new QuestAction_UnlockQuestTask(CurrentQuest, var0.InternalTaskName));
         } else {
            CurrentQuest.OnCompleteActions.add(new QuestAction_UnlockQuestTask(CurrentQuest, var0.InternalTaskName));
         }

         --UnlockNext;
         if (UnlockNext == 0) {
            UnlockNextQuest = null;
         }

      }
   }

   static void HandleUnlockNext(Quest var0) {
      if (UnlockNext > 0) {
         if (UnlockNextQuest != null) {
            UnlockNextQuest.OnCompleteActions.add(new QuestAction_UnlockQuest(var0.InternalQuestName));
         } else {
            CurrentQuest.OnCompleteActions.add(new QuestAction_UnlockQuest(var0.InternalQuestName));
         }

         --UnlockNext;
         if (UnlockNext == 0) {
            UnlockNextQuest = null;
         }

      }
   }
}
