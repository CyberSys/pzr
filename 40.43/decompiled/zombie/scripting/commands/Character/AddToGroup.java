package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class AddToGroup extends BaseCommand {
   String owner;
   String say;
   String Other;
   IsoGameCharacter chr;

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      this.owner = var1;
      String var3 = "";
      this.Other = var2[0].trim();
   }

   public void begin() {
      if (this.currentinstance != null && this.currentinstance.HasAlias(this.owner)) {
         this.chr = this.currentinstance.getAlias(this.owner);
      } else {
         if (this.module.getCharacter(this.owner) == null) {
            return;
         }

         if (this.module.getCharacter(this.owner).Actual == null) {
            return;
         }

         this.chr = this.module.getCharacter(this.owner).Actual;
      }

      IsoGameCharacter var1;
      if (this.currentinstance != null && this.currentinstance.HasAlias(this.Other)) {
         var1 = this.currentinstance.getAlias(this.Other);
      } else {
         if (this.module.getCharacter(this.Other) == null) {
            return;
         }

         if (this.module.getCharacter(this.Other).Actual == null) {
            return;
         }

         var1 = this.module.getCharacter(this.Other).Actual;
      }

      this.chr.getDescriptor().getGroup().addMember(var1.getDescriptor());
   }

   public boolean DoesInstantly() {
      return true;
   }
}
