package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;

public class AddFindItemTask extends BaseCommand {
   String name;
   String description;
   String item;
   int num = 1;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("Quest")) {
         this.name = var2[0].trim().replace("\"", "");
         this.description = var2[1].trim().replace("\"", "");
         this.description = this.module.getLanguage(this.description);
         if (this.description.indexOf("\"") == 0) {
            this.description = this.description.substring(1);
            this.description = this.description.substring(0, this.description.length() - 1);
         }

         this.item = var2[2].trim().replace("\"", "");
         this.num = Integer.parseInt(var2[3].trim());
      }
   }

   public void begin() {
      QuestCreator.AddQuestTask_FindItem(this.name, this.description, this.item, this.num);
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