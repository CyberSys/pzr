package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.Stack;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.commands.Trigger.TimeSinceLastRan;

public class Trigger extends Script {
   public static Integer tot = 0;
   public String name;
   Stack Conditions = new Stack();
   public boolean Locked = false;
   public ArrayList scriptsToCall = new ArrayList();
   public String TriggerParam = null;
   public String TriggerParam2 = null;
   public String TriggerParam3 = null;

   public void Load(String var1, String[] var2) {
      this.name = var1 + tot;
      Integer var3 = tot;
      tot = tot + 1;

      for(int var5 = 0; var5 < var2.length; ++var5) {
         this.DoLine(var2[var5].trim());
      }

   }

   private void DoLine(String var1) {
      if (!var1.isEmpty()) {
         if (var1.indexOf("call") == 0) {
            var1 = var1.replace("call", "").trim();
            this.scriptsToCall.add(var1);
         } else {
            String[] var2 = var1.split("&&");

            for(int var3 = 0; var3 < var2.length; ++var3) {
               if (!var2[var3].trim().isEmpty()) {
                  BaseCommand var4 = this.ReturnCommand(var2[var3].trim());
                  if (var4 instanceof TimeSinceLastRan) {
                     ((TimeSinceLastRan)var4).triggerInst = this.name;
                  }

                  this.Conditions.add(var4);
               }
            }
         }

      }
   }

   public boolean ConditionPassed() {
      for(int var1 = 0; var1 < this.Conditions.size(); ++var1) {
         if (!((BaseCommand)this.Conditions.get(var1)).getValue()) {
            return false;
         }
      }

      return true;
   }

   public void Process() {
      if (this.ConditionPassed()) {
         if (ScriptManager.instance.CustomTriggerLastRan.containsKey(this.name)) {
            ScriptManager.instance.CustomTriggerLastRan.put(this.name, 0);
         }

         for(int var1 = 0; var1 < this.scriptsToCall.size(); ++var1) {
            String var2 = (String)this.scriptsToCall.get(var1);
            this.module.PlayScript(var2);
         }

      }
   }
}
