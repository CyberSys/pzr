package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoUtils;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.ScriptCharacter;
import zombie.scripting.objects.Waypoint;

public class InRange extends BaseCommand {
   String owner;
   String other;
   int min = 0;
   int x;
   int y;
   int z;
   boolean bChar = false;
   boolean invert = false;

   public void init(String var1, String[] var2) {
      this.owner = var1;
      if (this.owner.indexOf("!") == 0) {
         this.invert = true;
         this.owner = this.owner.substring(1);
      }

      if (var2.length == 2) {
         Waypoint var3 = this.module.getWaypoint(var2[0].trim());
         if (var3 != null) {
            this.x = var3.x;
            this.y = var3.y;
            this.z = var3.z;
         } else {
            this.bChar = true;
            this.other = var2[0].trim();
         }

         this.min = Integer.parseInt(var2[1].trim());
      }

   }

   public boolean getValue() {
      IsoGameCharacter var1 = null;
      if (this.currentinstance.HasAlias(this.owner)) {
         var1 = this.currentinstance.getAlias(this.owner);
      } else {
         var1 = this.module.getCharacterActual(this.owner);
         ScriptCharacter var2 = this.module.getCharacter(this.owner);
         if (var2.Actual == null) {
            return false;
         }
      }

      IsoGameCharacter var4 = var1;
      if (var1.isDead()) {
         return true;
      } else if (this.bChar) {
         if (this.currentinstance.HasAlias(this.other)) {
            var1 = this.currentinstance.getAlias(this.other);
         } else {
            var1 = this.module.getCharacterActual(this.other);
            ScriptCharacter var3 = this.module.getCharacter(this.other);
            if (var3.Actual == null) {
               return false;
            }
         }

         if (var1.isDead()) {
            return true;
         } else if (this.invert) {
            return !(IsoUtils.DistanceManhatten(var1.getX(), var1.getY(), var4.getX(), var4.getY()) <= (float)this.min) || var4.getZ() != var1.getZ();
         } else {
            return IsoUtils.DistanceManhatten(var1.getX(), var1.getY(), var4.getX(), var4.getY()) <= (float)this.min && var4.getZ() == var1.getZ();
         }
      } else if (this.invert) {
         return !(IsoUtils.DistanceManhatten((float)this.x, (float)this.y, var1.getX(), var1.getY()) <= (float)this.min) || var1.getZ() != (float)this.z;
      } else {
         return IsoUtils.DistanceManhatten((float)this.x, (float)this.y, var1.getX(), var1.getY()) <= (float)this.min && var1.getZ() == (float)this.z;
      }
   }

   public void begin() {
   }

   public void Finish() {
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
