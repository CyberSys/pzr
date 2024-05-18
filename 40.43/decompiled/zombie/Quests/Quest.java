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

   public Quest(String var1, String var2) {
      this.InternalQuestName = var1;
      this.QuestName = var2;
      this.NumQuestTasks = 0;
      this.Complete = false;
      this.Failed = false;
   }

   public QuestTask AddQuestTask_EquipItem(String var1, String var2, String var3) {
      QuestTask_EquipItem var4 = new QuestTask_EquipItem(var1, var2, var3);
      this.QuestTaskStack.add(var4);
      ++this.NumQuestTasks;
      return var4;
   }

   public QuestTask AddTask_FindItem(String var1, String var2, String var3, int var4) {
      QuestTask_FindItem var5 = new QuestTask_FindItem(var1, var2, var3, var4);
      this.QuestTaskStack.add(var5);
      ++this.NumQuestTasks;
      return var5;
   }

   public QuestTask AddTask_GiveItem(String var1, String var2, String var3, String var4) {
      QuestTask_GiveItem var5 = new QuestTask_GiveItem(var1, var2, var3, var4);
      this.QuestTaskStack.add(var5);
      ++this.NumQuestTasks;
      return var5;
   }

   public QuestTask AddTask_GotoLocation(String var1, String var2, int var3, int var4, int var5) {
      QuestTask_GotoLocation var6 = new QuestTask_GotoLocation(var1, var2, var3, var4, var5);
      this.QuestTaskStack.add(var6);
      ++this.NumQuestTasks;
      return var6;
   }

   public QuestTask AddTask_TalkTo(String var1, String var2, String var3) {
      QuestTask_TalkTo var4 = new QuestTask_TalkTo(var1, var2, var3);
      this.QuestTaskStack.add(var4);
      ++this.NumQuestTasks;
      return var4;
   }

   public QuestTask AddTask_UseItemOn(String var1, String var2, String var3, ScriptCharacter var4) {
      QuestTask_UseItemOn var5 = new QuestTask_UseItemOn(var1, var2, var3, var4);
      this.QuestTaskStack.add(var5);
      ++this.NumQuestTasks;
      return var5;
   }

   public QuestTask FindTask(String var1) {
      for(int var2 = 0; var2 < this.QuestTaskStack.size(); ++var2) {
         if (((QuestTask)this.QuestTaskStack.get(var2)).getInternalName().equals(var1)) {
            return (QuestTask)this.QuestTaskStack.get(var2);
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

   public String getTaskName(int var1) {
      if (var1 < 0) {
         return "Task does not exist.";
      } else {
         return var1 >= this.QuestTaskStack.size() ? "Task does not exist." : ((QuestTask)this.QuestTaskStack.get(var1)).getName();
      }
   }

   public boolean IsComplete() {
      return this.Complete;
   }

   public boolean IsFailed() {
      return this.Failed;
   }

   public boolean TaskComplete(int var1) {
      if (var1 < 0) {
         return false;
      } else {
         return var1 >= this.QuestTaskStack.size() ? false : ((QuestTask)this.QuestTaskStack.get(var1)).IsComplete();
      }
   }

   public boolean TaskFailed(int var1) {
      if (var1 < 0) {
         return false;
      } else {
         return var1 >= this.QuestTaskStack.size() ? false : ((QuestTask)this.QuestTaskStack.get(var1)).IsFailed();
      }
   }

   public void Update() {
      boolean var1;
      int var2;
      if (!this.Complete) {
         var1 = true;

         for(var2 = 0; var2 < this.QuestTaskStack.size(); ++var2) {
            if (!((QuestTask)this.QuestTaskStack.get(var2)).Unlocked) {
               var1 = false;
            } else if (!((QuestTask)this.QuestTaskStack.get(var2)).IsComplete()) {
               var1 = false;
            }
         }

         this.Complete = var1;
         if (this.Complete) {
            Iterator var5 = this.OnCompleteActions.iterator();

            while(var5.hasNext()) {
               QuestAction var3 = (QuestAction)var5.next();
               var3.Execute();
            }
         }
      }

      if (!this.Failed) {
         var1 = false;

         for(var2 = 0; var2 < this.QuestTaskStack.size(); ++var2) {
            if (((QuestTask)this.QuestTaskStack.get(var2)).IsFailed()) {
               var1 = true;
            }
         }

         this.Failed = var1;
      }

      for(int var4 = 0; var4 < this.QuestTaskStack.size(); ++var4) {
         ((QuestTask)this.QuestTaskStack.get(var4)).Update();
      }

   }

   QuestTask AddQuestTask_ArbitaryAction(String var1, String var2, String var3) {
      QuestTask_ArbitaryAction var4 = new QuestTask_ArbitaryAction(var1, var2, var3);
      this.QuestTaskStack.add(var4);
      ++this.NumQuestTasks;
      return var4;
   }

   QuestTask AddQuestTask_ScriptCondition(String var1, String var2, String var3) {
      QuestTask_ScriptCondition var4 = new QuestTask_ScriptCondition(var1, var2, var3);
      this.QuestTaskStack.add(var4);
      ++this.NumQuestTasks;
      return var4;
   }

   QuestTask AddQuestTask_LuaCondition(String var1, String var2, LuaClosure var3, KahluaTable var4) {
      QuestTask_LuaCondition var5 = new QuestTask_LuaCondition(var1, var2, var3, var4);
      this.QuestTaskStack.add(var5);
      ++this.NumQuestTasks;
      return var5;
   }
}
