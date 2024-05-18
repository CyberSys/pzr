package zombie.core.input;

import zombie.debug.DebugLog;

public class XInput {
   public static void init() {
      String var0 = "";
      if ("1".equals(System.getProperty("zomboid.debuglibs"))) {
         var0 = "d";
      }

      try {
         if (System.getProperty("os.name").startsWith("Win")) {
            if (System.getProperty("sun.arch.data.model").equals("64")) {
               System.loadLibrary("PZ_XInput64" + var0);
            } else {
               System.loadLibrary("PZ_XInput32" + var0);
            }

            System.setProperty("jinput.plugins", "zombie.core.input.XInputEnvironmentPlugin");
         }
      } catch (UnsatisfiedLinkError var2) {
         DebugLog.log("Failed to load XInput library");
      }

   }
}
