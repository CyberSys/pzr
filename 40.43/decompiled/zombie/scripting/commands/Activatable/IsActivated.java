package zombie.scripting.commands.Activatable;

import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptActivatable;

public class IsActivated extends BaseCommand {
   boolean invert = false;
   String character;

   public void begin() {
   }

   public boolean getValue() {
      ScriptActivatable var1 = this.module.getActivatable(this.character);
      if (var1 == null) {
         return false;
      } else if (this.invert) {
         return !var1.IsActivated();
      } else {
         return var1.IsActivated();
      }
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      this.character = var1;
      if (this.character.indexOf("!") == 0) {
         this.invert = true;
         this.character = this.character.substring(1);
      }

   }

   public boolean DoesInstantly() {
      return true;
   }
}
