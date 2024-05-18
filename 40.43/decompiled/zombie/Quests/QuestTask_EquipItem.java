package zombie.Quests;

import zombie.characters.IsoPlayer;
import zombie.ui.UIManager;

public class QuestTask_EquipItem extends QuestTask {
   String QuestItemType;

   public QuestTask_EquipItem(String var1, String var2, String var3) {
      super(QuestTaskType.FindItem, var1, var2);
      this.QuestItemType = var3;
   }

   public void Update() {
      if (!this.Complete && IsoPlayer.getInstance().getPrimaryHandItem() != null && IsoPlayer.getInstance().getPrimaryHandItem().getType().equals(this.QuestItemType)) {
         this.Complete = true;
         if (UIManager.getOnscreenQuest() != null) {
            UIManager.getOnscreenQuest().TriggerQuestWiggle();
         }
      }

      super.Update();
   }
}
