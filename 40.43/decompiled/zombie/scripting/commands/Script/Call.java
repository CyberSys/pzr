package zombie.scripting.commands.Script;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.RandomSelector;

public class Call extends BaseCommand {
   String position;

   public void init(String var1, String[] var2) {
      if (var1 == null) {
         JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + var1, "Error", 0);
      } else {
         this.position = var1;
      }
   }

   public void begin() {
      if (this.module.RandomSelectorMap.containsKey(this.position)) {
         ((RandomSelector)this.module.RandomSelectorMap.get(this.position)).Process(this.currentinstance);
      } else {
         this.module.PlayScript(this.position, this.currentinstance);
      }

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
