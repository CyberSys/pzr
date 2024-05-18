package zombie.ai.astar;

import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;

public interface TileBasedMap {
   boolean blocked(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7);

   float getCost(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7);

   int getElevInTiles();

   int getHeightInTiles();

   int getWidthInTiles();

   void pathFinderVisited(int var1, int var2, int var3);

   boolean isNull(int var1, int var2, int var3);

   boolean IsStairsNode(IsoGridSquare var1, IsoGridSquare var2, IsoDirections var3);
}
