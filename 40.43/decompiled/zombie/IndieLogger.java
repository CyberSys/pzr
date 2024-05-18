package zombie;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class IndieLogger {
   public static Logger logger;
   private static FileWriter fwrite;

   public static void init() throws IOException {
   }

   private static String getCacheDir() {
      String var0 = System.getProperty("deployment.user.cachedir");
      if (var0 == null || System.getProperty("os.name").startsWith("Win")) {
         var0 = System.getProperty("java.io.tmpdir");
      }

      return var0 + File.separator + "lwjglcache";
   }

   public static void Log(String var0) {
   }
}
