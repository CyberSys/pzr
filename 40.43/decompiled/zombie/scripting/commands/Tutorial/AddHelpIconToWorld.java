package zombie.scripting.commands.Tutorial;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.characters.IsoGameCharacter;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;
import zombie.ui.UIManager;

public class AddHelpIconToWorld extends BaseCommand {
   String title;
   String message;
   String location;
   int offset = 0;
   int x = 0;
   int y = 0;
   int z = 0;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("Tutorial")) {
         this.title = var2[0].trim().replace("\"", "");
         this.title = this.module.getLanguage(this.title);
         if (this.title.indexOf("\"") == 0) {
            this.title = this.title.substring(1);
            this.title = this.title.substring(0, this.title.length() - 1);
         }

         this.message = var2[1].trim().replace("\"", "");
         this.message = this.module.getLanguage(this.message);
         if (this.message.indexOf("\"") == 0) {
            this.message = this.message.substring(1);
            this.message = this.message.substring(0, this.message.length() - 1);
         }

         this.location = var2[2].trim().replace("\"", "");
         this.offset = Integer.parseInt(var2[3].trim());
      } else {
         JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + var1, "Error", 0);
      }
   }

   public void begin() {
      Waypoint var1 = this.module.getWaypoint(this.location.trim());
      if (var1 != null) {
         this.x = var1.x;
         this.y = var1.y;
         this.z = var1.z;
      } else {
         IsoGameCharacter var2 = this.module.getCharacterActual(this.location);
         if (var2 != null) {
            this.x = (int)var2.getX();
            this.y = (int)var2.getY();
            this.z = (int)var2.getZ();
         }
      }

      UIManager.AddTutorial((float)this.x, (float)this.y, (float)this.z, this.title, this.message, false, (float)this.offset);
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
