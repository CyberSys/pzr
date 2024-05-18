package zombie.scripting.commands.Flags;

import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptFlag;

public class IsFlagValue extends BaseCommand {
   boolean invert = false;
   String name;
   String value;

   public void begin() {
   }

   public boolean getValue() {
      ScriptFlag var1 = this.module.getFlag(this.name);
      if (var1 == null) {
         return false;
      } else if (this.invert) {
         return !var1.IsValue(this.value);
      } else {
         return var1.IsValue(this.value);
      }
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      this.name = var1;
      if (this.name != null && this.name.indexOf("!") == 0) {
         this.invert = true;
         this.name = this.name.substring(1);
      }

      this.value = var2[0].trim().replace("\"", "");
   }

   public boolean DoesInstantly() {
      return true;
   }
}
