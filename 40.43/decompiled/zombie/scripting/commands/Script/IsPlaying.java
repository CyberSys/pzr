package zombie.scripting.commands.Script;

import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;

public class IsPlaying extends BaseCommand {
   boolean invert = false;
   String val;

   public void begin() {
   }

   public boolean getValue() {
      boolean var1 = ScriptManager.instance.IsScriptPlaying(this.val);
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
      this.val = var1;
      if (this.val.indexOf("!") == 0) {
         this.invert = true;
         this.val = this.val.substring(1);
      }

   }

   public boolean DoesInstantly() {
      return true;
   }
}
