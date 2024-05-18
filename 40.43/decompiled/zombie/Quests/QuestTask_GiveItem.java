package zombie.Quests;

import zombie.ui.UIManager;

public class QuestTask_GiveItem extends QuestTask {
   String CharacterName;
   String ItemName;

   public QuestTask_GiveItem(String var1, String var2, String var3, String var4) {
      super(QuestTaskType.GiveItem, var1, var2);
      this.ItemName = var3;
      this.CharacterName = var4;
   }

   public void Update() {
      if (!this.Unlocked) {
         this.Complete = false;
      } else {
         if (!this.Complete) {
            this.Complete = true;
            if (UIManager.getOnscreenQuest() != null) {
               UIManager.getOnscreenQuest().TriggerQuestWiggle();
            }
         }

         if (!this.Failed) {
            this.Failed = true;
            if (UIManager.getOnscreenQuest() != null) {
               UIManager.getOnscreenQuest().TriggerQuestWiggle();
            }
         }

         super.Update();
      }
   }
}
