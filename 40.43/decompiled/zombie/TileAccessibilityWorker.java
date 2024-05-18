package zombie;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import zombie.characters.IsoPlayer;
import zombie.core.utils.BooleanGrid;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;

public class TileAccessibilityWorker {
   public static TileAccessibilityWorker instance = new TileAccessibilityWorker();
   public int CurrentWorldXStart = 0;
   public int CurrentWorldYStart = 0;
   public BooleanGrid current = null;
   BooleanGrid working = null;
   public boolean startingNew = true;
   public boolean first = true;
   Queue queuex = new ArrayDeque(512);
   Queue queuey = new ArrayDeque(512);

   public void update() {
      int var1 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
      int var2 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
      if (this.working == null) {
         this.working = new BooleanGrid(var1, var2);
         this.current = new BooleanGrid(var1, var2);
         this.first = true;
      }

      int var4;
      int var5;
      int var6;
      int var7;
      int var8;
      int var9;
      if (this.startingNew) {
         ArrayList var3 = IsoWorld.instance.getCell().getZoneStack();
         var4 = var3.size();
         var5 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldXMinTiles();
         var6 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldYMinTiles();
         var7 = var5 + IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
         var8 = var6 + IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
         var9 = var5 + IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles() / 2;
         int var10 = var6 + IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles() / 2;
         this.queuex.add(var5);
         this.queuey.add(var10);
         this.queuex.add(var7);
         this.queuey.add(var10);
         this.queuex.add(var9);
         this.queuey.add(var6);
         this.queuex.add(var9);
         this.queuey.add(var8);
         this.startingNew = false;
         this.working.clear();
      }

      int var17 = 5000;
      var4 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldXMinTiles();
      var5 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldXMaxTiles();
      var6 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldYMinTiles();
      var7 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldYMaxTiles();

      while(!this.queuex.isEmpty() && (var17 > 0 || this.first)) {
         --var17;
         var8 = (Integer)this.queuex.remove();
         var9 = (Integer)this.queuey.remove();
         IsoChunk var18 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].getChunkForGridSquare(var8, var9);
         if (var18 != null) {
            IsoGridSquare var11 = IsoWorld.instance.CurrentCell.getGridSquare(var8, var9, 0);
            if (var11 != null) {
               this.working.setValue(var8 - var4, var9 - var6, true);

               for(int var12 = -1; var12 <= 1; ++var12) {
                  for(int var13 = -1; var13 <= 1; ++var13) {
                     if ((var12 != 0 || var13 != 0) && (var13 == 0 || var12 == 0)) {
                        int var14 = var8 + var12 - var4;
                        int var15 = var9 + var13 - var6;
                        if (var8 + var12 >= var4 && var8 + var12 < var5 && var9 + var13 >= var6 && var9 + var13 < var7 && !this.working.getValue(var14, var15)) {
                           if (!var11.testCollideAdjacentAdvanced(var12, var13, 0, false)) {
                              this.queuex.add(var8 + var12);
                              this.queuey.add(var9 + var13);
                              this.working.setValue(var14, var15, true);
                           } else {
                              IsoGridSquare var16 = IsoWorld.instance.CurrentCell.getGridSquare(var8 + var12, var9 + var13, 0);
                              if (var16 != null && (var16.getProperties().Is(IsoFlagType.solid) || var16.getProperties().Is(IsoFlagType.solidtrans))) {
                                 this.working.setValue(var14, var15, true);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (this.queuex.isEmpty()) {
         this.current.copy(this.working);
         this.startingNew = true;
         this.CurrentWorldXStart = var4;
         this.CurrentWorldYStart = var6;
         if (this.first) {
         }

         this.first = false;
      }

   }
}
