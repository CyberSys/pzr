package zombie.scripting.commands.Trigger;

import java.util.List;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Trigger;

public class IsLastFiredParameter extends BaseCommand {
   boolean invert = false;
   String val;
   String paramval;
   int param = 0;

   public void begin() {
   }

   public boolean getValue() {
      List var1 = (List)ScriptManager.instance.TriggerMap.get(this.val);
      if (var1 == null) {
         return false;
      } else if (var1.isEmpty()) {
         return false;
      } else {
         String var2 = null;
         switch(this.param) {
         case 0:
            var2 = ((Trigger)var1.get(0)).TriggerParam;
            break;
         case 1:
            var2 = ((Trigger)var1.get(0)).TriggerParam2;
            break;
         case 2:
            var2 = ((Trigger)var1.get(0)).TriggerParam3;
         }

         if (this.invert) {
            return !this.paramval.equals(var2);
         } else {
            return this.paramval.equals(var2);
         }
      }
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      this.val = var1.toLowerCase();
      if (this.val.indexOf("!") == 0) {
         this.invert = true;
         this.val = this.val.substring(1);
      }

      if (var2.length == 1) {
         this.paramval = var2[0].trim().replace("\"", "");
      } else if (var2.length == 2) {
         this.param = Integer.parseInt(var2[0].trim());
         this.paramval = var2[1].trim().replace("\"", "");
      }

   }

   public boolean DoesInstantly() {
      return true;
   }
}
