package zombie.ai.astar;

import java.io.FileNotFoundException;
import java.io.IOException;
import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;

public class AStarPathMap {
   public TileBasedMap map;

   public AStarPathMap(TileBasedMap var1) {
      this.map = var1;
      int var2 = var1.getWidthInTiles();
      float var3 = (float)var2;
   }

   public void LoadPrecalcFlle() throws FileNotFoundException, IOException {
   }

   public void DoPrecalcFile(AStarPathFinder var1) {
   }

   public int getFreeIndex(IsoGameCharacter var1) {
      return 0;
   }

   public float getMovementCost(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return this.map.getCost(var1, var2, var3, var4, var5, var6, var7);
   }

   protected boolean isValidLocation(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      boolean var11 = false;
      if (var2 != var5 || var3 != var6 || var4 != var7) {
         var11 = this.map.blocked(var1, var5, var6, var7, var8, var9, var10);
      }

      return !var11;
   }

   public IsoGridSquare getNode(int var1, int var2, int var3) {
      return IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
   }
}
