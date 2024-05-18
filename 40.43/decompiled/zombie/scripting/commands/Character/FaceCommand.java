package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoDirections;
import zombie.scripting.commands.BaseCommand;

public class FaceCommand extends BaseCommand {
   String owner;
   IsoDirections dir;
   String other = null;

   public void init(String var1, String[] var2) {
      this.owner = var1;

      try {
         this.dir = IsoDirections.valueOf(var2[0]);
      } catch (Exception var4) {
         this.other = var2[0];
      }

   }

   public void begin() {
      IsoGameCharacter var1 = null;
      IsoGameCharacter var2 = null;
      if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
         var1 = this.currentinstance.getAlias(this.owner);
      } else {
         var1 = this.module.getCharacter(this.owner).Actual;
      }

      if (var1 != null) {
         if (this.other == null) {
            var1.setDir(this.dir);
         } else {
            var2 = null;
            if (this.currentinstance != null && this.currentinstance.HasAlias(this.other)) {
               var2 = this.currentinstance.getAlias(this.other);
            } else {
               var2 = this.module.getCharacter(this.other).Actual;
            }

            if (var2 != null) {
               var1.faceDirection(var2);
            }
         }
      }
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
