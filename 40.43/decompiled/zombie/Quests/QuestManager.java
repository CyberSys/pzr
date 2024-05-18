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

   public QuestTask AddQuestTask_ArbitaryAction(Quest var1, String var2, String var3, String var4) {
      this.LastAddedQuestTask = var1.AddQuestTask_ArbitaryAction(var2, var3, var4);
      return this.LastAddedQuestTask;
   }

   public QuestTask AddQuestTask_ScriptCondition(Quest var1, String var2, String var3, String var4) {
      this.LastAddedQuestTask = var1.AddQuestTask_ScriptCondition(var2, var3, var4);
      return this.LastAddedQuestTask;
   }

   public QuestTask AddQuestTask_LuaCondition(Quest var1, String var2, String var3, LuaClosure var4, KahluaTable var5) {
      this.LastAddedQuestTask = var1.AddQuestTask_LuaCondition(var2, var3, var4, var5);
      return this.LastAddedQuestTask;
   }

   public QuestTask AddQuestTask_EquipItem(Quest var1, String var2, String var3, String var4) {
      this.LastAddedQuestTask = var1.AddQuestTask_EquipItem(var2, var3, var4);
      return this.LastAddedQuestTask;
   }

   public QuestTask AddQuestTask_FindItem(Quest var1, String var2, String var3, String var4, int var5) {
      this.LastAddedQuestTask = var1.AddTask_FindItem(var2, var3, var4, var5);
      return this.LastAddedQuestTask;
   }

   public QuestTask AddQuestTask_GiveItem(Quest var1, String var2, String var3, String var4, String var5) {
      this.LastAddedQuestTask = var1.AddTask_GiveItem(var2, var3, var4, var5);
      return this.LastAddedQuestTask;
   }

   public QuestTask AddQuestTask_GotoLocation(Quest var1, String var2, String var3, int var4, int var5, int var6) {
      this.LastAddedQuestTask = var1.AddTask_GotoLocation(var2, var3, var4, var5, var6);
      return this.LastAddedQuestTask;
   }

   public QuestTask AddQuestTask_TalkTo(Quest var1, String var2, String var3, String var4) {
      this.LastAddedQuestTask = var1.AddTask_TalkTo(var2, var3, var4);
      return this.LastAddedQuestTask;
   }

   public QuestTask AddQuestTask_UseItemOn(Quest var1, String var2, String var3, String var4, ScriptCharacter var5) {
      this.LastAddedQuestTask = var1.AddTask_UseItemOn(var2, var3, var4, var5);
      return this.LastAddedQuestTask;
   }

   public Quest CreateQuest(String var1, String var2) {
      Quest var3 = new Quest(var1, var2);
      this.QuestStack.add(var3);
      ++this.NumActiveQuests;
      return var3;
   }

   public Quest FindQuest(String var1) {
      Iterator var2 = this.QuestStack.iterator();

      Quest var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Quest)var2.next();
      } while(!var3.getInternalName().trim().equalsIgnoreCase(var1));

      return var3;
   }

   public int getNumQuests() {
      return this.NumActiveQuests;
   }

   public Quest getQuest(int var1) {
      if (var1 < 0) {
         return null;
      } else {
         return var1 >= this.QuestStack.size() ? null : (Quest)this.QuestStack.get(var1);
      }
   }

   public String getQuestName(int var1) {
      if (var1 < 0) {
         return null;
      } else {
         return var1 >= this.QuestStack.size() ? null : ((Quest)this.QuestStack.get(var1)).getName();
      }
   }

   public boolean QuestComplete(int var1) {
      if (var1 < 0) {
         return false;
      } else {
         return var1 >= this.QuestStack.size() ? false : ((Quest)this.QuestStack.get(var1)).Complete;
      }
   }

   public void Update() {
      for(int var1 = 0; var1 < this.QuestStack.size(); ++var1) {
         if (((Quest)this.QuestStack.get(var1)).Unlocked) {
            ((Quest)this.QuestStack.get(var1)).Update();
         }
      }

   }

   public void ClearQuest(String var1) {
      for(int var2 = 0; var2 < this.QuestStack.size(); ++var2) {
         if (var1.equals(((Quest)this.QuestStack.get(var2)).getInternalName())) {
            this.QuestStack.remove(var2);
            return;
         }
      }

   }
}
