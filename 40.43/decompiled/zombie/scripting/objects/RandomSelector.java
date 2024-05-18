package zombie.scripting.objects;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.scripting.ScriptManager;

public class RandomSelector extends Script {
   public String name;
   public ArrayList scriptsToCall = new ArrayList();

   public void Load(String var1, String[] var2) {
      this.name = new String(var1);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         this.DoLine(new String(var2[var3].trim()));
      }

   }

   private void DoLine(String var1) {
      if (!var1.isEmpty()) {
         this.scriptsToCall.add(var1);
      }
   }

   public String Process() {
      int var1 = Rand.Next(this.scriptsToCall.size());
      if (((String)this.scriptsToCall.get(var1)).contains(".")) {
         ScriptManager.instance.PlayScript((String)this.scriptsToCall.get(var1));
      } else {
         ScriptManager.instance.PlayScript(this.module.name + "." + (String)this.scriptsToCall.get(var1));
      }

      return (String)this.scriptsToCall.get(var1);
   }

   public Script.ScriptInstance Process(Script.ScriptInstance var1) {
      int var2 = Rand.Next(this.scriptsToCall.size());
      return ((String)this.scriptsToCall.get(var2)).contains(".") ? ScriptManager.instance.PlayScript((String)this.scriptsToCall.get(var2), var1) : ScriptManager.instance.PlayScript(this.module.name + "." + (String)this.scriptsToCall.get(var2), var1);
   }
}
