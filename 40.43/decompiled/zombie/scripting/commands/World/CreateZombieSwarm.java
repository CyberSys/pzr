package zombie.scripting.commands.World;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.iso.IsoWorld;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Zone;

public class CreateZombieSwarm extends BaseCommand {
   String position;
   int num = 1;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("World")) {
         this.num = Integer.parseInt(var2[0].trim());
         this.position = var2[1].trim().replace("\"", "");
      } else {
         JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + var1, "Error", 0);
      }
   }

   public void begin() {
      Zone var1 = this.module.getZone(this.position);
      if (var1 != null) {
         IsoWorld.instance.CreateSwarm(this.num, var1.x, var1.y, var1.x2, var1.y2);
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
