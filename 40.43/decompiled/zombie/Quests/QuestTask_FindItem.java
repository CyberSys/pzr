package zombie.Quests;

import zombie.characters.IsoPlayer;
import zombie.inventory.InventoryItem;
import zombie.ui.UIManager;

public class QuestTask_FindItem extends QuestTask {
   int QuestItemRequiredAmmount;
   String QuestItemType;

   public QuestTask_FindItem(String var1, String var2, String var3, int var4) {
      super(QuestTaskType.FindItem, var1, var2);
      this.QuestItemType = var3;
      this.QuestItemRequiredAmmount = var4;
   }

   public void Update() {
      if (!this.Unlocked) {
         this.Complete = false;
      } else {
         if (!this.Complete) {
            int var1 = 0;

            for(int var2 = 0; var2 < IsoPlayer.getInstance().getInventory().Items.size(); ++var2) {
               if (((InventoryItem)IsoPlayer.getInstance().getInventory().Items.get(var2)).getType().equals(this.QuestItemType)) {
                  var1 += ((InventoryItem)IsoPlayer.getInstance().getInventory().Items.get(var2)).getUses();
               }
            }

            if (var1 >= this.QuestItemRequiredAmmount) {
               this.Complete = true;
               if (UIManager.getOnscreenQuest() != null) {
                  UIManager.getOnscreenQuest().TriggerQuestWiggle();
               }
            }
         }

         super.Update();
      }
   }
}
