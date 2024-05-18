package zombie;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoBuilding;

public class FliesSound {
   public static FliesSound instance = new FliesSound();
   private static final IsoGridSquare[] tempSquares = new IsoGridSquare[100];
   private final FliesSound.PlayerData[] playerData = new FliesSound.PlayerData[4];
   private final ArrayList fadeEmitters = new ArrayList();
   private float fliesVolume = -1.0F;

   public FliesSound() {
      for(int var1 = 0; var1 < this.playerData.length; ++var1) {
         this.playerData[var1] = new FliesSound.PlayerData();
      }

   }

   public void Reset() {
      for(int var1 = 0; var1 < this.playerData.length; ++var1) {
         this.playerData[var1].Reset();
      }

   }

   public void update() {
      if (SandboxOptions.instance.DecayingCorpseHealthImpact.getValue() != 1) {
         int var1;
         for(var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
            IsoPlayer var2 = IsoPlayer.players[var1];
            if (var2 != null && var2.getCurrentSquare() != null) {
               this.playerData[var1].update(var2);
            }
         }

         for(var1 = 0; var1 < this.fadeEmitters.size(); ++var1) {
            FliesSound.FadeEmitter var3 = (FliesSound.FadeEmitter)this.fadeEmitters.get(var1);
            if (var3.update()) {
               this.fadeEmitters.remove(var1--);
            }
         }

      }
   }

   public void render() {
      IsoChunkMap var1 = IsoWorld.instance.CurrentCell.ChunkMap[0];

      for(int var2 = 0; var2 < IsoChunkMap.ChunkGridWidth; ++var2) {
         for(int var3 = 0; var3 < IsoChunkMap.ChunkGridWidth; ++var3) {
            IsoChunk var4 = var1.getChunk(var3, var2);
            if (var4 != null) {
               FliesSound.ChunkData var5 = var4.corpseData;
               if (var5 != null) {
                  int var6 = (int)IsoPlayer.players[0].z;
                  FliesSound.ChunkLevelData var7 = var5.levelData[var6];

                  for(int var8 = 0; var8 < var7.emitters.length; ++var8) {
                     if (var7.emitters[var8] != null && var7.emitters[var8].emitter != null) {
                        this.paintSquare(var7.emitters[var8].sq.x, var7.emitters[var8].sq.y, var7.emitters[var8].sq.z, 0.0F, 1.0F, 0.0F, 1.0F);
                     }

                     if (var7.refCount[var8] > 0) {
                        this.paintSquare(var4.wx * 10 + 5, var4.wy * 10 + 5, 0, 0.0F, 0.0F, 1.0F, 1.0F);
                     }
                  }

                  IsoBuilding var9 = IsoPlayer.players[0].getCurrentBuilding();
                  if (var9 != null && var7.buildingCorpseCount != null && var7.buildingCorpseCount.containsKey(var9)) {
                     this.paintSquare(var4.wx * 10 + 5, var4.wy * 10 + 5, var6, 1.0F, 0.0F, 0.0F, 1.0F);
                  }
               }
            }
         }
      }

   }

   private void paintSquare(int var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      int var8 = Core.TileScale;
      int var9 = (int)IsoUtils.XToScreenExact((float)var1, (float)(var2 + 1), (float)var3, 0);
      int var10 = (int)IsoUtils.YToScreenExact((float)var1, (float)(var2 + 1), (float)var3, 0);
      SpriteRenderer.instance.renderPoly(var9, var10, var9 + 32 * var8, var10 - 16 * var8, var9 + 64 * var8, var10, var9 + 32 * var8, var10 + 16 * var8, var4, var5, var6, var7);
   }

   public void chunkLoaded(IsoChunk var1) {
      if (var1.corpseData == null) {
         var1.corpseData = new FliesSound.ChunkData(var1.wx, var1.wy);
      }

      var1.corpseData.wx = var1.wx;
      var1.corpseData.wy = var1.wy;
      var1.corpseData.Reset();
   }

   public void corpseAdded(int var1, int var2, int var3) {
      FliesSound.ChunkData var4 = this.getChunkData(var1, var2);
      if (var4 != null) {
         var4.corpseAdded(var1, var2, var3);

         for(int var5 = 0; var5 < this.playerData.length; ++var5) {
            if (var4.levelData[var3].refCount[var5] > 0) {
               this.playerData[var5].forceUpdate = true;
            }
         }

      }
   }

   public void corpseRemoved(int var1, int var2, int var3) {
      FliesSound.ChunkData var4 = this.getChunkData(var1, var2);
      if (var4 != null) {
         var4.corpseRemoved(var1, var2, var3);

         for(int var5 = 0; var5 < this.playerData.length; ++var5) {
            if (var4.levelData[var3].refCount[var5] > 0) {
               this.playerData[var5].forceUpdate = true;
            }
         }

      }
   }

   public int getCorpseCount(IsoGameCharacter var1) {
      return var1 != null && var1.getCurrentSquare() != null ? this.getCorpseCount((int)var1.getX() / 10, (int)var1.getY() / 10, (int)var1.getZ(), var1.getBuilding()) : 0;
   }

   private int getCorpseCount(int var1, int var2, int var3, IsoBuilding var4) {
      int var5 = 0;

      for(int var7 = -1; var7 <= 1; ++var7) {
         for(int var8 = -1; var8 <= 1; ++var8) {
            FliesSound.ChunkData var9 = this.getChunkData((var1 + var8) * 10, (var2 + var7) * 10);
            if (var9 != null) {
               FliesSound.ChunkLevelData var10 = var9.levelData[var3];
               if (var4 == null) {
                  var5 += var10.corpseCount;
               } else if (var10.buildingCorpseCount != null) {
                  Integer var11 = (Integer)var10.buildingCorpseCount.get(var4);
                  if (var11 != null) {
                     var5 += var11;
                  }
               }
            }
         }
      }

      return var5;
   }

   private FliesSound.ChunkData getChunkData(int var1, int var2) {
      IsoChunk var3 = IsoWorld.instance.CurrentCell.getChunkForGridSquare(var1, var2, 0);
      return var3 != null ? var3.corpseData : null;
   }

   private class FadeEmitter {
      private static final float FADE_IN_RATE = 0.01F;
      private static final float FADE_OUT_RATE = -0.01F;
      BaseSoundEmitter emitter;
      float volume;
      float targetVolume;
      IsoGridSquare sq;

      private FadeEmitter() {
         this.emitter = null;
         this.volume = 1.0F;
         this.targetVolume = 1.0F;
         this.sq = null;
      }

      boolean update() {
         if (this.emitter == null) {
            return true;
         } else {
            if (this.volume < this.targetVolume) {
               this.volume += 0.01F * (30.0F / (float)PerformanceSettings.LockFPS);
               if (this.volume >= this.targetVolume) {
                  this.volume = this.targetVolume;
                  return true;
               }
            } else {
               this.volume += -0.01F * (30.0F / (float)PerformanceSettings.LockFPS);
               if (this.volume <= 0.0F) {
                  this.volume = 0.0F;
                  this.emitter.stopAll();
                  this.emitter = null;
                  return true;
               }
            }

            this.emitter.setVolumeAll(this.volume);
            return false;
         }
      }

      void Reset() {
         this.emitter = null;
         this.volume = 1.0F;
         this.targetVolume = 1.0F;
         this.sq = null;
      }

      // $FF: synthetic method
      FadeEmitter(Object var2) {
         this();
      }
   }

   private class PlayerData {
      int wx = -1;
      int wy = -1;
      int z = -1;
      IsoBuilding building = null;
      boolean forceUpdate = false;

      PlayerData() {
      }

      boolean isSameLocation(IsoPlayer var1) {
         IsoGridSquare var2 = var1.getCurrentSquare();
         if (var2 != null && var2.getBuilding() != this.building) {
            return false;
         } else {
            return (int)var1.getX() / 10 == this.wx && (int)var1.getY() / 10 == this.wy && (int)var1.getZ() == this.z;
         }
      }

      void update(IsoPlayer var1) {
         if (this.forceUpdate || !this.isSameLocation(var1)) {
            this.forceUpdate = false;
            int var2 = this.wx;
            int var3 = this.wy;
            int var4 = this.z;
            IsoGridSquare var6 = var1.getCurrentSquare();
            this.wx = var6.getX() / 10;
            this.wy = var6.getY() / 10;
            this.z = var6.getZ();
            this.building = var6.getBuilding();

            int var7;
            int var8;
            FliesSound.ChunkData var9;
            FliesSound.ChunkLevelData var10;
            for(var7 = -1; var7 <= 1; ++var7) {
               for(var8 = -1; var8 <= 1; ++var8) {
                  var9 = FliesSound.this.getChunkData((this.wx + var8) * 10, (this.wy + var7) * 10);
                  if (var9 != null) {
                     var10 = var9.levelData[this.z];
                     var10.update(this.wx + var8, this.wy + var7, this.z, var1);
                  }
               }
            }

            if (var4 != -1) {
               for(var7 = -1; var7 <= 1; ++var7) {
                  for(var8 = -1; var8 <= 1; ++var8) {
                     var9 = FliesSound.this.getChunkData((var2 + var8) * 10, (var3 + var7) * 10);
                     if (var9 != null) {
                        var10 = var9.levelData[var4];
                        var10.deref(var1);
                     }
                  }
               }

            }
         }
      }

      void Reset() {
         this.wx = this.wy = this.z = -1;
         this.building = null;
         this.forceUpdate = false;
      }
   }

   public class ChunkData {
      private int wx;
      private int wy;
      private final FliesSound.ChunkLevelData[] levelData;

      private ChunkData(int var2, int var3) {
         this.levelData = new FliesSound.ChunkLevelData[8];
         this.wx = var2;
         this.wy = var3;

         for(int var4 = 0; var4 < this.levelData.length; ++var4) {
            this.levelData[var4] = FliesSound.this.new ChunkLevelData();
         }

      }

      private void corpseAdded(int var1, int var2, int var3) {
         IsoGridSquare var4 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
         IsoBuilding var5 = var4 == null ? null : var4.getBuilding();
         int var6 = var1 - this.wx * 10;
         int var7 = var2 - this.wy * 10;
         this.levelData[var3].corpseAdded(var6, var7, var5);
      }

      private void corpseRemoved(int var1, int var2, int var3) {
         IsoGridSquare var4 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
         IsoBuilding var5 = var4 == null ? null : var4.getBuilding();
         int var6 = var1 - this.wx * 10;
         int var7 = var2 - this.wy * 10;
         this.levelData[var3].corpseRemoved(var6, var7, var5);
      }

      private void Reset() {
         for(int var1 = 0; var1 < this.levelData.length; ++var1) {
            this.levelData[var1].Reset();
         }

      }

      // $FF: synthetic method
      ChunkData(int var2, int var3, Object var4) {
         this(var2, var3);
      }
   }

   private class ChunkLevelData {
      int corpseCount = 0;
      HashMap buildingCorpseCount = null;
      final int[] refCount = new int[4];
      final FliesSound.FadeEmitter[] emitters = new FliesSound.FadeEmitter[4];

      ChunkLevelData() {
      }

      void corpseAdded(int var1, int var2, IsoBuilding var3) {
         if (var3 == null) {
            ++this.corpseCount;
         } else {
            if (this.buildingCorpseCount == null) {
               this.buildingCorpseCount = new HashMap();
            }

            Integer var4 = (Integer)this.buildingCorpseCount.get(var3);
            if (var4 == null) {
               this.buildingCorpseCount.put(var3, 1);
            } else {
               this.buildingCorpseCount.put(var3, var4 + 1);
            }
         }

      }

      void corpseRemoved(int var1, int var2, IsoBuilding var3) {
         if (var3 == null) {
            --this.corpseCount;
         } else if (this.buildingCorpseCount != null) {
            Integer var4 = (Integer)this.buildingCorpseCount.get(var3);
            if (var4 != null) {
               if (var4 > 1) {
                  this.buildingCorpseCount.put(var3, var4 - 1);
               } else {
                  this.buildingCorpseCount.remove(var3);
               }
            }
         }

      }

      IsoGridSquare calcSoundPos(int var1, int var2, int var3, IsoBuilding var4) {
         IsoChunk var5 = IsoWorld.instance.CurrentCell.getChunkForGridSquare(var1 * 10, var2 * 10, var3);
         if (var5 == null) {
            return null;
         } else {
            int var6 = 0;

            for(int var7 = 0; var7 < 10; ++var7) {
               for(int var8 = 0; var8 < 10; ++var8) {
                  IsoGridSquare var9 = var5.getGridSquare(var8, var7, var3);
                  if (var9 != null && !var9.getStaticMovingObjects().isEmpty() && var9.getBuilding() == var4) {
                     FliesSound.tempSquares[var6++] = var9;
                  }
               }
            }

            if (var6 > 0) {
               return FliesSound.tempSquares[var6 / 2];
            } else {
               return null;
            }
         }
      }

      void update(int var1, int var2, int var3, IsoPlayer var4) {
         int var10002 = this.refCount[var4.PlayerIndex]++;
         int var5 = FliesSound.this.getCorpseCount(var1, var2, var3, var4.getCurrentBuilding());
         if ((double)BodyDamage.getSicknessFromCorpsesRate(var5) > ZomboidGlobals.FoodSicknessDecrease) {
            IsoBuilding var6 = var4.getCurrentBuilding();
            IsoGridSquare var7 = this.calcSoundPos(var1, var2, var3, var6);
            if (var7 == null) {
               return;
            }

            if (this.emitters[var4.PlayerIndex] == null) {
               this.emitters[var4.PlayerIndex] = FliesSound.this.new FadeEmitter();
            }

            FliesSound.FadeEmitter var8 = this.emitters[var4.PlayerIndex];
            float var9 = Math.min(0.7692308F * (0.1F + (float)var5 * 0.002F), 1.0F);
            if (var8.emitter == null) {
               var8.emitter = IsoWorld.instance.getFreeEmitter((float)var7.x, (float)var7.y, (float)var3);
               var8.emitter.playSoundLoopedImpl("CorpseFlies");
               var8.emitter.setVolumeAll(0.0F);
               var8.volume = 0.0F;
               FliesSound.this.fadeEmitters.add(var8);
            } else {
               var8.emitter.setPos((float)var7.x, (float)var7.y, (float)var3);
               if (var8.targetVolume != var9 && !FliesSound.this.fadeEmitters.contains(var8)) {
                  FliesSound.this.fadeEmitters.add(var8);
               }
            }

            var8.targetVolume = var9;
            var8.sq = var7;
         } else {
            FliesSound.FadeEmitter var10 = this.emitters[var4.PlayerIndex];
            if (var10 != null && var10.emitter != null) {
               if (!FliesSound.this.fadeEmitters.contains(var10)) {
                  FliesSound.this.fadeEmitters.add(var10);
               }

               var10.targetVolume = 0.0F;
            }
         }

      }

      void deref(IsoPlayer var1) {
         int var2 = var1.PlayerIndex;
         int var10002 = this.refCount[var2]--;
         if (this.refCount[var2] <= 0) {
            if (this.emitters[var2] != null && this.emitters[var2].emitter != null) {
               if (!FliesSound.this.fadeEmitters.contains(this.emitters[var2])) {
                  FliesSound.this.fadeEmitters.add(this.emitters[var2]);
               }

               this.emitters[var2].targetVolume = 0.0F;
            }

         }
      }

      void Reset() {
         this.corpseCount = 0;
         if (this.buildingCorpseCount != null) {
            this.buildingCorpseCount.clear();
         }

         for(int var1 = 0; var1 < 4; ++var1) {
            this.refCount[var1] = 0;
            if (this.emitters[var1] != null) {
               this.emitters[var1].Reset();
            }
         }

      }
   }
}
