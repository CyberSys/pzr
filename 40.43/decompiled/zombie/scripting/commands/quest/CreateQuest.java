package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;

public class CreateQuest extends BaseCommand {
   String name;
   String description;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("Quest")) {
         this.name = var2[0].trim().replace("\"", "");
         this.description = var2[1].replace("\"", "");
         this.description = this.module.getLanguage(this.description);
         if (this.description.indexOf("\"") == 0) {
            this.description = this.description.substring(1);
            this.description = this.description.substring(0, this.description.length() - 1);
         }

      }
   }

   public void begin() {
      QuestCreator.CreateQuest(this.name, this.description);
   }

   public boolean IsFinished() {
      return true;
   }

   public boolean getValue() {
      return false;
   }

   public void update() {
   }

   public boolean DoesInstantly() {
      return true;
   }
}
