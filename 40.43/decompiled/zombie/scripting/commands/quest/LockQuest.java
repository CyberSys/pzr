package zombie.scripting.commands.quest;

import zombie.Quests.Quest;
import zombie.Quests.QuestManager;
import zombie.scripting.commands.BaseCommand;
import zombie.ui.QuestPanel;

public class LockQuest extends BaseCommand {
   String quest = null;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("Quest")) {
         if (var2.length == 1) {
            this.quest = var2[0].trim().replace("\"", "");
         }

      }
   }

   public void begin() {
      Quest var1 = QuestManager.instance.FindQuest(this.quest);
      if (var1 != null) {
         var1.Unlocked = false;
         if (QuestPanel.instance.ActiveQuest == var1) {
            QuestPanel.instance.ActiveQuest = null;
         }

      }
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public boolean DoesInstantly() {
      return true;
   }
}
