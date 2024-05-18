package zombie.scripting.commands.Script;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.scripting.ScriptManager;
import zombie.scripting.commands.BaseCommand;

public class Resume extends BaseCommand {
   String position;

   public void init(String var1, String[] var2) {
      if (var1 == null) {
         JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + var1, "Error", 0);
      } else {
         this.position = var1;
      }
   }

   public void begin() {
      ScriptManager.instance.UnPauseScript(this.position);
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
