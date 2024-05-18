package zombie.scripting.commands.Flags;

import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptFlag;

public class SetFlag extends BaseCommand {
   String name;
   String val;

   public void init(String var1, String[] var2) {
      this.name = var1.trim().replace("\"", "");
      this.val = var2[0].trim().replace("\"", "");
   }

   public void begin() {
      try {
         ScriptFlag var1 = this.module.getFlag(this.name);
         if (var1 == null) {
            return;
         }

         var1.SetValue(this.val);
      } catch (Exception var2) {
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
