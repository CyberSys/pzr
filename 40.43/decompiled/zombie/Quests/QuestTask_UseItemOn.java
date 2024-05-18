package zombie.Quests;

import zombie.scripting.objects.ScriptCharacter;
import zombie.ui.UIManager;

public class QuestTask_UseItemOn extends QuestTask {
   ScriptCharacter Character;
   String QuestItemType;

   public QuestTask_UseItemOn(String var1, String var2, String var3, ScriptCharacter var4) {
      super(QuestTaskType.UseItemOn, var1, var2);
      this.QuestItemType = var3;
      this.Character = var4;
   }

   public void Update() {
      if (!this.Unlocked) {
         this.Complete = false;
      } else {
         if (!this.Complete) {
            if (this.Character.Actual == null) {
               super.Update();
               return;
            }

            boolean var1 = false;

            for(int var2 = 0; var2 < this.Character.Actual.getUsedItemsOn().size(); ++var2) {
               if (((String)this.Character.Actual.getUsedItemsOn().get(var2)).equals(this.QuestItemType)) {
                  var1 = true;
                  break;
               }
            }

            if (var1) {
               this.Character.Actual.getUsedItemsOn().remove(this.QuestItemType);
               if (UIManager.getOnscreenQuest() != null) {
                  UIManager.getOnscreenQuest().TriggerQuestWiggle();
               }

               if (UIManager.getOnscreenQuest() != null) {
                  UIManager.getOnscreenQuest().TriggerQuestWiggle();
               }

               this.Complete = true;
            }
         }

         if (!this.Failed) {
            if (this.Character.Actual == null) {
               super.Update();
               return;
            }

            if (this.Character.Actual.getHealth() <= 0.0F) {
               this.Failed = true;
               if (UIManager.getOnscreenQuest() != null) {
                  UIManager.getOnscreenQuest().TriggerQuestWiggle();
               }
            }
         }

         super.Update();
      }
   }
}
