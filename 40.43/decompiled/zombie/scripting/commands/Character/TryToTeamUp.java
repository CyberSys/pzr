package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.scripting.commands.BaseCommand;

public class TryToTeamUp extends BaseCommand {
   String owner;
   boolean tryToTeamUp = true;

   public void init(String var1, String[] var2) {
      this.owner = var1;
      this.tryToTeamUp = new Boolean(var2[0]);
   }

   public void begin() {
      IsoGameCharacter var1 = this.module.getCharacterActual(this.owner);
      if (var1 != null) {
         ((IsoSurvivor)var1).setTryToTeamUp(this.tryToTeamUp);
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
