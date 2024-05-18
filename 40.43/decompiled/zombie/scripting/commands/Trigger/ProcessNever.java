package zombie.scripting.commands.Trigger;

import java.awt.Component;
import java.util.List;
import javax.swing.JOptionPane;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Trigger;

public class ProcessNever extends BaseCommand {
   String position;

   public void init(String var1, String[] var2) {
      if (var1 == null) {
         JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + var1, "Error", 0);
      } else {
         this.position = var1.toLowerCase();
      }
   }

   public void begin() {
      List var1 = (List)ScriptManager.instance.CustomTriggerMap.get(this.position);

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         ((Trigger)var1.get(var2)).Locked = true;
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
