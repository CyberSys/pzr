package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;

public class UnlockTasksOnComplete extends BaseCommand {
   int count;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("Quest")) {
         this.count = Integer.parseInt(var2[0].trim());
      }
   }

   public void begin() {
      QuestCreator.SetToUnlockNext(this.count);
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
