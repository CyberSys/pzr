package zombie.scripting.commands.Hook;

import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;

public class RegisterOneTime extends BaseCommand {
   String event;
   String script;
   int num = 1;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("Hook")) {
         this.event = var2[0].trim().replace("\"", "");
         this.script = var2[1].trim().replace("\"", "");
      }
   }

   public void begin() {
      String var1 = this.script;
      if (!var1.contains(".")) {
         var1 = this.module.name + "." + var1;
      }

      ScriptManager.instance.AddOneTime(this.event, var1);
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
