package zombie.scripting.commands;

import zombie.scripting.ScriptManager;

public class SetModuleAlias extends BaseCommand {
   String name;
   String a;
   String b;

   public void init(String var1, String[] var2) {
      this.a = var2[0].trim();
      this.b = var2[1].trim();
   }

   public void begin() {
      ScriptManager.instance.ModuleAliases.put(this.a, this.b);
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
