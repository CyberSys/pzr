package zombie.scripting.commands.World;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoFireManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;

public class StartFire extends BaseCommand {
   String position;
   int Energy;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("World")) {
         this.position = var2[0].trim().replace("\"", "");
         this.Energy = Integer.parseInt(var2[1].trim());
      } else {
         JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + var1, "Error", 0);
      }
   }

   public void begin() {
      Waypoint var1 = this.module.getWaypoint(this.position);
      if (var1 != null) {
         IsoGridSquare var2 = IsoWorld.instance.CurrentCell.getGridSquare(var1.x, var1.y, var1.z);
         if (var2 != null) {
            IsoFireManager.StartFire(IsoWorld.instance.CurrentCell, var2, true, this.Energy);
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
