package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;

public class AllowBehaviours extends BaseCommand {
   public String command;
   public IsoGameCharacter chr;
   public String[] params;
   String owner;
   boolean allow = false;

   public void init(String var1, String[] var2) {
      if (var2.length == 1) {
         this.allow = var2[0].trim().equals("true");
         this.owner = var1;
      }

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

      this.chr.setAllowBehaviours(this.allow);
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
