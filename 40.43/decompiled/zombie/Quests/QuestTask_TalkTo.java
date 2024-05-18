package zombie.Quests;

import zombie.ui.UIManager;

public class QuestTask_TalkTo extends QuestTask {
   String CharacterName;

   public QuestTask_TalkTo(String var1, String var2, String var3) {
      super(QuestTaskType.TalkTo, var1, var2);
      this.CharacterName = var3;
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
