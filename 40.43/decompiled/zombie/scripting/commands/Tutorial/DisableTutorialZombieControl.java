package zombie.scripting.commands.Tutorial;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.scripting.commands.BaseCommand;
import zombie.ui.TutorialManager;

public class DisableTutorialZombieControl extends BaseCommand {
   int limit;

   public void init(String var1, String[] var2) {
      if (var1 == null || !var1.equals("Tutorial")) {
         JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + var1, "Error", 0);
      }
   }

   public void begin() {
      TutorialManager.instance.ActiveControlZombies = false;
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
