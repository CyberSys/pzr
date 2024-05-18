package zombie.console;

import java.security.InvalidParameterException;
import zombie.characters.IsoPlayer;
import zombie.ui.PZConsole;
import zombie.ui.TutorialManager;

public class Commands {
   public static void Log(String var0) {
      PZConsole.instance.Log(var0);
   }

   public static void ProcessCommand(String var0, String[] var1) {
      var0 = var0.toLowerCase();
      if (var0.equals("addinv")) {
         IsoPlayer.getInstance().getInventory().AddItem(var1[0]);
      } else if (var0.equals("debug")) {
         if (var1.length > 1) {
            throw new InvalidParameterException();
         }

         if (var1[0].equals("on")) {
            TutorialManager.Debug = true;
            Log("Debug mode activated");
         }

         if (var1[0].equals("off")) {
            TutorialManager.Debug = false;
            Log("Debug mode deactivated");
         }
      }

   }
}
