package zombie.scripting.commands.quest;

import zombie.Quests.QuestCreator;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;

public class AddGotoLocationTask extends BaseCommand {
   String name;
   String description;
   String location;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("Quest")) {
         this.name = var2[0].trim().replace("\"", "");
         this.description = var2[1].trim().replace("\"", "");
         this.description = this.module.getLanguage(this.description);
         if (this.description.indexOf("\"") == 0) {
            this.description = this.description.substring(1);
            this.description = this.description.substring(0, this.description.length() - 1);
         }

         this.location = var2[2].trim().replace("\"", "");
      }
   }

   public void begin() {
      Waypoint var1 = this.module.getWaypoint(this.location);
      if (var1 != null) {
         QuestCreator.AddQuestTask_GotoLocation(this.name, this.description, var1.x, var1.y, var1.z);
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
