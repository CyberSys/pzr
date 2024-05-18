package zombie.scripting.commands.Activatable;

import zombie.iso.objects.interfaces.Activatable;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptActivatable;

public class ToggleActivatable extends BaseCommand {
   String owner;
   float num = 1.0F;

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      this.owner = var1;
   }

   public void begin() {
      ScriptActivatable var1 = this.module.getActivatable(this.owner);
      if (var1 != null) {
         Activatable var2 = var1.getActual();
         if (var2 != null) {
            var2.Toggle();
         }

      }
   }

   public boolean DoesInstantly() {
      return true;
   }
}
