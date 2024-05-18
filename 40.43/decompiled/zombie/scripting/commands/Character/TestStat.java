package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.scripting.commands.BaseCommand;

public class TestStat extends BaseCommand {
   String owner;
   String stat;
   int modifier = 0;
   IsoGameCharacter chr;
   boolean invert = false;

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      if (var1.indexOf("!") == 0) {
         this.invert = true;
         var1 = var1.substring(1);
      }

      this.owner = var1;
      this.stat = var2[0].trim();
      if (var2.length > 1) {
         this.modifier = Integer.parseInt(var2[1].trim());
      }

   }

   public boolean getValue() {
      float var1 = 0.0F;
      if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
         this.chr = this.currentinstance.getAlias(this.owner);
      } else {
         this.chr = this.module.getCharacter(this.owner).Actual;
      }

      if (this.chr == null) {
         return false;
      } else {
         if (this.stat.contains("Compassion")) {
            var1 = this.chr.getDescriptor().getCompassion();
         }

         if (this.stat.contains("Bravery")) {
            var1 = this.chr.getDescriptor().getBravery() * 2.0F;
         }

         if (this.stat.contains("Loner")) {
            var1 = this.chr.getDescriptor().getLoner();
         }

         if (this.stat.contains("Temper")) {
            var1 = this.chr.getDescriptor().getTemper();
         }

         var1 *= 10.0F;
         if (this.invert) {
            return !((float)Rand.Next(100) < var1 + (float)this.modifier);
         } else {
            return (float)Rand.Next(100) < var1 + (float)this.modifier;
         }
      }
   }

   public void begin() {
   }

   public boolean AllowCharacterBehaviour(String var1) {
      return true;
   }

   public boolean DoesInstantly() {
      return true;
   }
}
