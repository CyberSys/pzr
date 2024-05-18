package zombie.scripting.commands.DayNight;

import zombie.GameTime;
import zombie.scripting.commands.BaseCommand;

public class IsNight extends BaseCommand {
   boolean invert = false;
   String character;

   public void begin() {
   }

   public boolean getValue() {
      boolean var1 = false;
      if (GameTime.getInstance().getTimeOfDay() > 20.0F || GameTime.getInstance().getTimeOfDay() < 6.0F) {
         var1 = true;
      }

      if (this.invert) {
         var1 = !var1;
      }

      return var1;
   }

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("!")) {
         this.invert = true;
      }

   }

   public boolean DoesInstantly() {
      return true;
   }
}
