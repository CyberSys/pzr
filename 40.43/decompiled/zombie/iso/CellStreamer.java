package zombie.iso;

public class CellStreamer {
   public static int WidthTiles = 50;
   public static int HeightTiles = 50;

   public static void LoadInitialStream(int var0, int var1, int var2, int var3) {
      int var4 = var2 / WidthTiles;
      int var5 = var3 / HeightTiles;
      int var6 = var0;
      int var7 = var1;
      --var4;
      --var5;
      int var10 = var4 + 2;
      int var11 = var5 + 2;
      if (var4 < 0) {
         var4 += 3;
         var6 = var0 - 1;
      }

      if (var5 < 0) {
         var5 += 3;
         var7 = var1 - 1;
      }

      if (var4 > 2) {
         var4 -= 3;
         ++var6;
      }

      if (var5 > 2) {
         var5 -= 3;
         ++var7;
      }

   }
}
