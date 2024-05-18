package zombie;

import java.io.BufferedReader;
import java.io.FileReader;

public class SVNRevision {
   public static int REVISION = -1;

   public static int init() {
      try {
         FileReader var0 = new FileReader("SVNRevision.txt");
         BufferedReader var1 = new BufferedReader(var0);
         String var2 = var1.readLine();
         var1.close();
         REVISION = Integer.parseInt(var2);
      } catch (Exception var3) {
         System.out.println("Failed to read SVNRevision.txt");
      }

      return REVISION;
   }
}
