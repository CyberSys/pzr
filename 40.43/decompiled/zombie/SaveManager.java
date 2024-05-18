package zombie;

import java.io.File;

public class SaveManager {
   public static SaveManager instance = new SaveManager();

   private static boolean deleteDirectory(File var0) {
      if (var0.exists()) {
         File[] var1 = var0.listFiles();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2].isDirectory()) {
               deleteDirectory(var1[var2]);
            } else {
               var1[var2].delete();
            }
         }
      }

      return var0.delete();
   }

   public void init(String var1) {
   }

   public void StartMassInsertion() {
   }

   public void EndMassInsertion() {
   }
}
