package zombie.scripting.commands;

import java.util.ArrayList;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Script;

public class ResumeAllScriptsExcept extends BaseCommand {
   String name;
   ArrayList scripts = new ArrayList();

   public void init(String var1, String[] var2) {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         this.scripts.add(var2[var3].trim());
      }

   }

   public void begin() {
      for(int var1 = 0; var1 < ScriptManager.instance.PlayingScripts.size(); ++var1) {
         boolean var2 = false;

         for(int var3 = 0; var3 < this.scripts.size(); ++var3) {
            if (((String)this.scripts.get(var3)).equals(((Script.ScriptInstance)ScriptManager.instance.PlayingScripts.get(var1)).theScript.name)) {
               var2 = true;
            }
         }

         if (!var2) {
            ScriptManager.instance.UnPauseScript(((Script.ScriptInstance)ScriptManager.instance.PlayingScripts.get(var1)).theScript.name);
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
