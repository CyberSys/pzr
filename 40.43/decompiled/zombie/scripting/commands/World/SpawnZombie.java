package zombie.scripting.commands.World;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.characters.IsoZombie;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;

public class SpawnZombie extends BaseCommand {
   String position;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("World")) {
         this.position = var2[0].trim().replace("\"", "");
      } else {
         JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + var1, "Error", 0);
      }
   }

   public void begin() {
      Waypoint var1 = this.module.getWaypoint(this.position);
      if (var1 != null) {
         IsoGridSquare var2 = IsoWorld.instance.CurrentCell.getGridSquare(var1.x, var1.y, var1.z);
         if (var2 != null) {
            IsoZombie var3 = new IsoZombie(IsoWorld.instance.CurrentCell);
            var3.KeepItReal = true;
            var3.setX((float)var1.x);
            var3.setY((float)var1.y);
            var3.setZ((float)var1.z);
            var3.setCurrent(var2);
            IsoWorld.instance.CurrentCell.getZombieList().add(var3);
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
