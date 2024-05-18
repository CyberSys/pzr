package zombie.scripting.commands.Script;

import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Conditional;
import zombie.scripting.objects.Script;

public class StopScript extends BaseCommand {
   String position;

   public void init(String var1, String[] var2) {
      this.position = var1;
   }

   public void begin() {
      if (this.position == null) {
         Script.ScriptInstance var1;
         for(var1 = this.currentinstance; var1.parent != null && var1.theScript instanceof Conditional; var1 = var1.parent) {
         }

         ScriptManager.instance.StopScript(var1);
      } else {
         ScriptManager.instance.StopScript(this.position);
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
