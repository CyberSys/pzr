package zombie.scripting.commands.Character;

import zombie.characters.IsoGameCharacter;
import zombie.iso.objects.IsoDeadBody;
import zombie.scripting.commands.BaseCommand;

public class Die extends BaseCommand {
   String owner;
   boolean bGory = false;

   public void init(String var1, String[] var2) {
      this.owner = var1;
      this.bGory = var2[0].equals("true");
   }

   public void begin() {
      IsoGameCharacter var1 = this.module.getCharacterActual(this.owner);
      if (var1 != null) {
         var1.setHealth(0.0F);
         new IsoDeadBody(var1);
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
