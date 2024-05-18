package zombie.scripting.objects;

import java.util.Stack;
import zombie.scripting.commands.BaseCommand;

public class QuestTaskCondition extends Script {
   Stack Conditions = new Stack();

   public void Load(String var1, String[] var2) {
      String var3 = var2[0].trim();
      if (var3 != null) {
         String[] var4 = var3.split("&&");

         for(int var5 = 0; var5 < var4.length; ++var5) {
            if (!var4[var5].trim().isEmpty()) {
               this.Conditions.add(this.ReturnCommand(var4[var5].trim()));
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
}
