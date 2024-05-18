package zombie.scripting.commands.Trigger;

import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;

public class TimeSinceLastRan extends BaseCommand {
   boolean invert = false;
   public String triggerInst;
   int frames = 0;

   public void begin() {
   }

   public boolean getValue() {
      boolean var1 = (Integer)ScriptManager.instance.CustomTriggerLastRan.get(this.triggerInst) > this.frames;
      if (this.invert) {
         return !var1;
      } else {
         return var1;
      }
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      if (var1 != null) {
         this.triggerInst = var1.toLowerCase();
         if (this.triggerInst.indexOf("!") == 0) {
            this.invert = true;
            this.triggerInst = this.triggerInst.substring(1);
         }
      }

      this.frames = (int)(30.0F * Float.parseFloat(var2[0].trim()));
   }

   public boolean DoesInstantly() {
      return true;
   }
}
