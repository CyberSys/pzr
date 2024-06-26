package zombie.scripting.commands.Character;

import java.security.InvalidParameterException;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.scripting.commands.BaseCommand;
import zombie.ui.UIManager;

public class Sleep extends BaseCommand {
   String owner;
   IsoGameCharacter chr;
   float num = 1.0F;

   public boolean IsFinished() {
      return true;
   }

   public void update() {
   }

   public void init(String var1, String[] var2) {
      this.owner = var1;
      this.num = Float.parseFloat(var2[0].trim());
   }

   public void begin() {
      if (this.module.getCharacter(this.owner).Actual == null) {
         throw new InvalidParameterException();
      } else {
         this.module.getCharacter(this.owner).Actual.setAsleep(true);
         if (this.module.getCharacter(this.owner).Actual == IsoPlayer.getInstance()) {
            IsoPlayer.instance.setAsleepTime(0.0F);
            this.module.getCharacter(this.owner).Actual.setForceWakeUpTime(this.num);
            UIManager.setbFadeBeforeUI(true);
            UIManager.FadeOut(4.0D);
         }

      }
   }

   public boolean DoesInstantly() {
      return true;
   }
}
