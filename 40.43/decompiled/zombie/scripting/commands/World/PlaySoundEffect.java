package zombie.scripting.commands.World;

import java.awt.Component;
import javax.swing.JOptionPane;
import zombie.SoundManager;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.scripting.commands.BaseCommand;
import zombie.scripting.objects.Waypoint;

public class PlaySoundEffect extends BaseCommand {
   String position;
   String sound;
   float pitchVar;
   int radius;
   float volume;
   boolean ignoreOutside;
   public String format;

   public void init(String var1, String[] var2) {
      if (var1 != null && var1.equals("World")) {
         this.format = var2[0].trim().replace("\"", "");
         this.sound = var2[1].trim().replace("\"", "");
         this.position = var2[2].trim().replace("\"", "");
         this.pitchVar = Float.parseFloat(var2[3].trim().replace("\"", ""));
         this.radius = Integer.parseInt(var2[4].trim().replace("\"", ""));
         this.volume = Float.parseFloat(var2[5].trim().replace("\"", ""));
         this.ignoreOutside = var2[6].trim().replace("\"", "").equals("true");
      } else {
         JOptionPane.showMessageDialog((Component)null, "Command: " + this.getClass().getName() + " is not part of " + var1, "Error", 0);
      }
   }

   public void begin() {
      Waypoint var1 = this.module.getWaypoint(this.position);
      if (var1 != null) {
         IsoGridSquare var2 = IsoWorld.instance.CurrentCell.getGridSquare(var1.x, var1.y, var1.z);
         if (var2 != null) {
            if (this.format.equals("WAV")) {
               SoundManager.instance.PlayWorldSoundWav(this.sound, var2, this.pitchVar, (float)this.radius, this.volume, this.ignoreOutside);
            } else if (this.format.equals("OGG")) {
               SoundManager.instance.PlayWorldSound(this.sound, var2, this.pitchVar, (float)this.radius, this.volume, this.ignoreOutside);
            }
         }

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
