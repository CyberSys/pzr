package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;

public class UnlockTaskOnComplete extends BaseCommand {
   String script;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("Quest")) {
         this.script = var2[0].trim().replace("\"", "");
      }
   }

   public void begin() {
      QuestCreator.AddQuestAction_UnlockQuestTask(this.script);
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
