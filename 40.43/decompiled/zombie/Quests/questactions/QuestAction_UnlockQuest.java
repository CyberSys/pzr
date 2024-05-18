package zombie.Quests.questactions;

import zombie.Quests.Quest;
import zombie.Quests.QuestManager;

public class QuestAction_UnlockQuest implements QuestAction {
   String Quest;

   public QuestAction_UnlockQuest(String var1) {
      this.Quest = var1;
   }

   public void Execute() {
      Quest var1 = QuestManager.instance.FindQuest(this.Quest);
      if (var1 != null) {
         var1.Unlocked = true;
      }
   }
}
