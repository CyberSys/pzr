package zombie.scripting.commands;

import zombie.scripting.objects.Conditional;

public class WaitCommand extends BaseCommand {
   int frames = 0;
   Conditional con = null;
   String obj = null;

   public boolean IsFinished() {
      if (this.con != null) {
         boolean var1 = this.con.ConditionPassed(this.currentinstance);
         if (var1) {
            var1 = var1;
         }

         return var1;
      } else {
         return this.frames <= 0;
      }
   }

   public void update() {
      --this.frames;
   }

   public void init(String var1, String[] var2) {
      this.obj = var1;

      try {
         this.frames = (int)(30.0F * Float.parseFloat(var2[0].trim()));
      } catch (Exception var6) {
         String var4 = var2[0].trim();

         for(int var5 = 1; var5 < var2.length; ++var5) {
            var4 = var4 + ", ";
            var4 = var4 + var2[var5];
         }

         this.con = new Conditional(var4.trim(), "");
      }

   }

   public boolean AllowCharacterBehaviour(String var1) {
      return !var1.equals(this.obj);
   }

   public void begin() {
   }

   public boolean DoesInstantly() {
      return false;
   }
}
