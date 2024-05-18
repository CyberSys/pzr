package zombie.scripting.commands.Character;

import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;

public class ActualizeCommand extends BaseCommand {
   public String command;
   public String chr;
   public String[] params;
   String owner;
   int x;
   int y;
   int z;

   public void init(String var1, String[] var2) {
      if (var2.length == 1) {
         Waypoint var3 = this.module.getWaypoint(var2[0]);
         this.x = var3.x;
         this.y = var3.y;
         this.z = var3.z;
         this.owner = var1;
      }

   }

   public void begin() {
      this.module.getCharacter(this.owner).Actualise(this.x, this.y, this.z);
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
