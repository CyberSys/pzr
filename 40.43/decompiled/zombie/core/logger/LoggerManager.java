package zombie.core.logger;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import zombie.GameWindow;
import zombie.characters.IsoPlayer;

public class LoggerManager {
   static HashMap _loggers = null;

   public static synchronized ZLogger getLogger(String var0) {
      if (!_loggers.containsKey(var0)) {
         createLogger(var0);
      }

      return (ZLogger)_loggers.get(var0);
   }

   public static void init() {
      if (_loggers == null) {
         _loggers = new HashMap();

         try {
            File var0 = new File(getLogsDir());
            String[] var1 = var0.list();
            if (var1 == null) {
               return;
            }

            String var2 = "logs_";
            File var3 = null;

            for(int var4 = 0; var4 < var1.length; ++var4) {
               File var5;
               if (var4 == 0) {
                  var5 = new File(getLogsDir() + File.separator + var1[var4]);
                  Calendar var6 = Calendar.getInstance();
                  var6.setTimeInMillis(var5.lastModified());
                  if (var6.get(5) < 9) {
                     var2 = var2 + "0" + var6.get(5);
                  } else {
                     var2 = var2 + var6.get(5);
                  }

                  if (var6.get(2) < 9) {
                     var2 = var2 + "-0" + (var6.get(2) + 1);
                  } else {
                     var2 = var2 + "-" + (var6.get(2) + 1);
                  }

                  var3 = new File(getLogsDir() + File.separator + var2);
                  if (!var3.exists()) {
                     var3.mkdir();
                  }
               }

               if (var3 != null) {
                  var5 = new File(getLogsDir() + File.separator + var1[var4]);
                  if (!var5.isDirectory()) {
                     var5.renameTo(new File(var3.getAbsolutePath() + File.separator + var5.getName()));
                     var5.delete();
                  }
               }
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }

      }
   }

   public static synchronized void createLogger(String var0) {
      _loggers.put(var0, new ZLogger(var0));
   }

   public static synchronized void createLogger(String var0, boolean var1) {
      _loggers.put(var0, new ZLogger(var0, var1));
   }

   public static String getLogsDir() {
      String var0 = GameWindow.getCacheDir() + File.separator + "Logs";
      File var1 = new File(var0);
      if (!var1.exists()) {
         var1.mkdir();
      }

      return var1.getAbsolutePath();
   }

   public static String getPlayerCoords(IsoPlayer var0) {
      return "(" + (int)var0.getX() + "," + (int)var0.getY() + "," + (int)var0.getZ() + ")";
   }
}
