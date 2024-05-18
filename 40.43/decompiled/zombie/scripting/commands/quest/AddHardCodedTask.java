package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;

public class AddHardCodedTask extends BaseCommand {
   String name;
   String description;
   String task;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("Quest")) {
         this.name = var2[0].trim().replace("\"", "");
         this.description = var2[1].trim().replace("\"", "");
         this.description = this.module.getLanguage(this.description);
         if (this.description.indexOf("\"") == 0) {
            this.description = this.description.substring(1);
            this.description = this.description.substring(0, this.description.length() - 1);
         }

         this.task = var2[2].trim().replace("\"", "");
      }
   }

   public void begin() {
      QuestCreator.AddQuestTask_ArbitaryAction(this.name, this.description, this.task);
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
