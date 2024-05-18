package zombie.Quests;

import zombie.characters.IsoPlayer;
import zombie.iso.IsoWorld;
import zombie.ui.UIManager;

public class QuestTask_GotoLocation extends QuestTask {
   int Task_x;
   int Task_y;
   int Task_z;

   public QuestTask_GotoLocation(String var1, String var2, int var3, int var4, int var5) {
      super(QuestTaskType.GotoLocation, var1, var2);
      this.Task_x = var3;
      this.Task_y = var4;
      this.Task_z = var5;
   }

   public void Update() {
      if (!this.Unlocked) {
         this.Complete = false;
      } else {
         if (!this.Complete && IsoPlayer.getInstance().getCurrentSquare().getX() > this.Task_x - 2 && IsoPlayer.getInstance().getCurrentSquare().getX() < this.Task_x + 2 && IsoPlayer.getInstance().getCurrentSquare().getY() > this.Task_y - 2 && IsoPlayer.getInstance().getCurrentSquare().getY() < this.Task_y + 2 && IsoPlayer.getInstance().getCurrentSquare().getZ() == this.Task_z && IsoPlayer.getInstance().getCurrentSquare().getRoom() == IsoWorld.instance.CurrentCell.getGridSquare(this.Task_x, this.Task_y, this.Task_z).getRoom()) {
            this.Complete = true;
            if (UIManager.getOnscreenQuest() != null) {
               UIManager.getOnscreenQuest().TriggerQuestWiggle();
            }
         }

         super.Update();
      }
   }
}
