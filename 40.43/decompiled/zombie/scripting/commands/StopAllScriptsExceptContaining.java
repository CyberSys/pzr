package zombie.scripting.commands;

import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Script;

public class StopAllScriptsExceptContaining extends BaseCommand {
   String name;
   String scripts = null;

   public void init(String var1, String[] var2) {
      this.scripts = var2[0].trim().replace("\"", "");
   }

   public void begin() {
      for(int var1 = 0; var1 < ScriptManager.instance.PlayingScripts.size(); ++var1) {
         if (!((Script.ScriptInstance)ScriptManager.instance.PlayingScripts.get(var1)).theScript.name.contains(this.scripts)) {
            ScriptManager.instance.StopScript(((Script.ScriptInstance)ScriptManager.instance.PlayingScripts.get(var1)).theScript.name);
         }
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
