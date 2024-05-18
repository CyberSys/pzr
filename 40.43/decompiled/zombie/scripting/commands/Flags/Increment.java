package zombie.scripting.commands.Flags;

import zombie.scripting.commands.BaseCommand;

public class Increment extends BaseCommand {
   String name;
   String val;

   public void init(String var1, String[] var2) {
      this.name = var1.trim().replace("\"", "");
   }

   public void begin() {
      try {
         this.val = this.module.getFlagValue(this.name);
         Integer var1 = Integer.parseInt(this.val);
         var1 = var1 + 1;
         this.module.getFlag(this.name).SetValue(var1.toString());
      } catch (Exception var4) {
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
