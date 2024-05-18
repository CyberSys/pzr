package zombie.scripting.commands.Module;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptModule;

public class Enabled extends BaseCommand {
   String position;
   boolean b = false;

   public void init(String var1, String[] var2) {
      if (var1 == null) {
         JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + var1, "Error", 0);
      } else {
         this.position = var1;
         this.b = var2[0].trim().equals("true");
      }
   }

   public void begin() {
      ScriptModule var1 = ScriptManager.instance.getModuleNoDisableCheck(this.position);
      if (var1 != null) {
         var1.disabled = !this.b;
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
