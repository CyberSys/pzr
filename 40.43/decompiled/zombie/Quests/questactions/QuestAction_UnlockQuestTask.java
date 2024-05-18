package zombie.Quests.questactions;

import zombie.Quests.Quest;
import zombie.Quests.QuestTask;

public class QuestAction_UnlockQuestTask implements QuestAction {
   Quest Quest;
   String Task;

   public QuestAction_UnlockQuestTask(Quest var1, String var2) {
      this.Task = var2;
      this.Quest = var1;
   }

   public void Execute() {
      QuestTask var1 = this.Quest.FindTask(this.Task);
      var1.Unlocked = true;
   }
}
