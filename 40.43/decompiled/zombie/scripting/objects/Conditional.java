package zombie.scripting.objects;

import java.util.ArrayList;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.commands.ConditionalCommand;

public class Conditional extends Script {
   ArrayList Conditions = new ArrayList();
   public ConditionalCommand command;

   public Conditional(String var1, String var2) {
      this.DoScriptParsing("", var2);
      if (var1 != null) {
         String[] var3 = var1.split("&&");

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (!var3[var4].trim().isEmpty()) {
               this.Conditions.add(this.ReturnCommand(var3[var4].trim()));
            }
         }

      }
   }

   public Conditional(String var1, String var2, ConditionalCommand var3) {
      this.command = var3;
      this.DoScriptParsing("", var2);
      if (var1 != null) {
         String[] var4 = var1.split("&&");

         for(int var5 = 0; var5 < var4.length; ++var5) {
            if (!var4[var5].trim().isEmpty()) {
               this.Conditions.add(this.ReturnCommand(var4[var5].trim()));
            }
         }

      }
   }

   public boolean ConditionPassed(Script.ScriptInstance var1) {
      for(int var2 = 0; var2 < this.Conditions.size(); ++var2) {
         ((BaseCommand)this.Conditions.get(var2)).currentinstance = var1;
         if (!((BaseCommand)this.Conditions.get(var2)).getValue()) {
            return false;
         }
      }

      return true;
   }
}
