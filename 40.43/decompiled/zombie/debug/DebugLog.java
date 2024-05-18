package zombie.debug;

import java.util.HashSet;

public class DebugLog {
   public static HashSet Types = new HashSet();

   public static void log(String var0) {
      long var1 = System.currentTimeMillis();
      log(DebugType.General, var1 + " " + var0);
   }

   public static void log(DebugType var0, String var1) {
      if (Types.contains(var0)) {
         if (var1 != null && var1.trim().length() != 0) {
            System.out.println(var1);
         }
      }
   }

   public static void enableLog(DebugType var0, boolean var1) {
      if (var1) {
         Types.add(var0);
      } else {
         Types.remove(var0);
      }

   }

   public static void log(Object var0) {
      if (var0 != null) {
         log(var0.toString());
      }
   }

   static {
      Types.add(DebugType.General);
      Types.add(DebugType.Lua);
   }
}
