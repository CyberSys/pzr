package zombie.scripting.commands.World;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.WorldSoundManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;

public class PlayWorldSoundEffect extends BaseCommand {
   String position;
   int radius;
   int volume;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("World")) {
         this.position = var2[0].trim().replace("\"", "");
         this.radius = Integer.parseInt(var2[1].trim().replace("\"", ""));
         this.volume = Integer.parseInt(var2[2].trim().replace("\"", ""));
      } else {
         JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + var1, "Error", 0);
      }
   }

   public void begin() {
      Waypoint var1 = this.module.getWaypoint(this.position);
      if (var1 != null) {
         IsoGridSquare var2 = IsoWorld.instance.CurrentCell.getGridSquare(var1.x, var1.y, var1.z);
         if (var2 != null) {
            WorldSoundManager.instance.addSound((IsoObject)null, var1.x, var1.y, var1.z, this.radius, this.volume);
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
