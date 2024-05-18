package zombie.Quests;

import zombie.scripting.ScriptManager;
import zombie.scripting.objects.QuestTaskCondition;

public class QuestTask_ScriptCondition extends QuestTask {
   String ArbAction;

   public QuestTask_ScriptCondition(String var1, String var2, String var3) {
      super(QuestTaskType.Custom, var1, var2);
      this.ArbAction = var3;
   }

   public void Update() {
      if (!this.Complete && this.ArbActionCheck()) {
         this.Complete = true;
      }

      super.Update();
   }

   private boolean ArbActionCheck() {
      QuestTaskCondition var1 = ScriptManager.instance.getQuestCondition(this.ArbAction);
      return var1 == null ? true : var1.ConditionPassed();
   }
}
