package zombie;

import java.util.ArrayList;
import java.util.Stack;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.popman.ZombiePopulationManager;

public class WorldSoundManager {
   public static WorldSoundManager instance = new WorldSoundManager();
   public ArrayList SoundList = new ArrayList();
   public Stack freeSounds = new Stack();
   private static final WorldSoundManager.ResultBiggestSound resultBiggestSound = new WorldSoundManager.ResultBiggestSound();

   public void init(IsoCell var1) {
   }

   public void initFrame() {
   }

   public void KillCell() {
      this.freeSounds.addAll(this.SoundList);
      this.SoundList.clear();
   }

   public WorldSoundManager.WorldSound getNew() {
      return this.freeSounds.isEmpty() ? new WorldSoundManager.WorldSound() : (WorldSoundManager.WorldSound)this.freeSounds.pop();
   }

   public void addSound(IsoObject var1, int var2, int var3, int var4, int var5, int var6) {
      if (var5 > 0) {
         if (GameClient.bClient) {
            GameClient.instance.sendWorldSound(var1, var2, var3, var4, var5, var6, false, 0.0F, 1.0F);
         } else {
            if (SandboxOptions.instance.Lore.Hearing.getValue() == 1) {
               var5 = (int)((float)var5 * 2.0F);
            }

            if (SandboxOptions.instance.Lore.Hearing.getValue() == 3) {
               var5 = (int)((float)var5 * 0.25F);
            }

            synchronized(this.SoundList) {
               WorldSoundManager.WorldSound var8 = this.getNew().init(var1, var2, var3, var4, var5, var6, false, 0.0F, 1.0F);
               if (!GameServer.bServer) {
                  int var9 = (var2 - var5) / 10;
                  int var10 = (var3 - var5) / 10;
                  int var11 = (int)Math.ceil((double)(((float)var2 + (float)var5) / 10.0F));
                  int var12 = (int)Math.ceil((double)(((float)var3 + (float)var5) / 10.0F));

                  for(int var13 = var9; var13 < var11; ++var13) {
                     for(int var14 = var10; var14 < var12; ++var14) {
                        IsoChunk var15 = IsoWorld.instance.CurrentCell.getChunk(var13, var14);
                        if (var15 != null) {
                           var15.SoundList.add(var8);
                        }
                     }
                  }
               }

               this.SoundList.add(var8);
               ZombiePopulationManager.instance.addWorldSound((WorldSoundManager.WorldSound)this.SoundList.get(this.SoundList.size() - 1));
            }
         }
      }
   }

   public void addSound(IsoObject var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      if (var5 > 0) {
         if (GameClient.bClient) {
            GameClient.instance.sendWorldSound(var1, var2, var3, var4, var5, var6, var7, 0.0F, 1.0F);
         } else {
            if (SandboxOptions.instance.Lore.Hearing.getValue() == 1) {
               var5 = (int)((float)var5 * 3.0F);
            }

            if (SandboxOptions.instance.Lore.Hearing.getValue() == 3) {
               var5 = (int)((float)var5 * 0.25F);
            }

            synchronized(this.SoundList) {
               WorldSoundManager.WorldSound var9 = this.getNew().init(var1, var2, var3, var4, var5, var6, var7, 0.0F, 1.0F);
               if (!GameServer.bServer) {
                  int var10 = (var2 - var5) / 10;
                  int var11 = (var3 - var5) / 10;
                  int var12 = (int)Math.ceil((double)(((float)var2 + (float)var5) / 10.0F));
                  int var13 = (int)Math.ceil((double)(((float)var3 + (float)var5) / 10.0F));

                  for(int var14 = var10; var14 < var12; ++var14) {
                     for(int var15 = var11; var15 < var13; ++var15) {
                        IsoChunk var16 = IsoWorld.instance.CurrentCell.getChunk(var14, var15);
                        if (var16 != null) {
                           var16.SoundList.add(var9);
                        }
                     }
                  }
               }

               this.SoundList.add(var9);
               ZombiePopulationManager.instance.addWorldSound((WorldSoundManager.WorldSound)this.SoundList.get(this.SoundList.size() - 1));
            }
         }
      }
   }

   public void addSound(IsoObject var1, int var2, int var3, int var4, int var5, int var6, boolean var7, float var8, float var9) {
      if (var5 > 0) {
         if (GameClient.bClient) {
            GameClient.instance.sendWorldSound(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         } else {
            if (SandboxOptions.instance.Lore.Hearing.getValue() == 1) {
               var5 = (int)((float)var5 * 3.0F);
            }

            if (SandboxOptions.instance.Lore.Hearing.getValue() == 3) {
               var5 = (int)((float)var5 * 0.25F);
            }

            synchronized(this.SoundList) {
               WorldSoundManager.WorldSound var11 = this.getNew().init(var1, var2, var3, var4, var5, var6, var7, var8, var9);
               if (!GameServer.bServer) {
                  int var12 = (var2 - var5) / 10;
                  int var13 = (var3 - var5) / 10;
                  int var14 = (int)Math.ceil((double)(((float)var2 + (float)var5) / 10.0F));
                  int var15 = (int)Math.ceil((double)(((float)var3 + (float)var5) / 10.0F));

                  for(int var16 = var12; var16 < var14; ++var16) {
                     for(int var17 = var13; var17 < var15; ++var17) {
                        IsoChunk var18 = IsoWorld.instance.CurrentCell.getChunk(var16, var17);
                        if (var18 != null) {
                           var18.SoundList.add(var11);
                        }
                     }
                  }
               }

               this.SoundList.add(var11);
               ZombiePopulationManager.instance.addWorldSound((WorldSoundManager.WorldSound)this.SoundList.get(this.SoundList.size() - 1));
            }
         }
      }
   }

   public void addSound(boolean var1, int var2, int var3, int var4, int var5, int var6, boolean var7, float var8, float var9) {
      if (SandboxOptions.instance.Lore.Hearing.getValue() == 1) {
         var5 = (int)((float)var5 * 3.0F);
      }

      if (SandboxOptions.instance.Lore.Hearing.getValue() == 3) {
         var5 = (int)((float)var5 * 0.25F);
      }

      synchronized(this.SoundList) {
         WorldSoundManager.WorldSound var11 = this.getNew().init(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         if (!GameServer.bServer) {
            int var12 = (var2 - var5) / 10;
            int var13 = (var3 - var5) / 10;
            int var14 = (int)Math.ceil((double)(((float)var2 + (float)var5) / 10.0F));
            int var15 = (int)Math.ceil((double)(((float)var3 + (float)var5) / 10.0F));

            for(int var16 = var12; var16 < var14; ++var16) {
               for(int var17 = var13; var17 < var15; ++var17) {
                  IsoChunk var18 = IsoWorld.instance.CurrentCell.getChunk(var16, var17);
                  if (var18 != null) {
                     var18.SoundList.add(var11);
                  }
               }
            }
         }

         this.SoundList.add(var11);
         ZombiePopulationManager.instance.addWorldSound((WorldSoundManager.WorldSound)this.SoundList.get(this.SoundList.size() - 1));
      }
   }

   public WorldSoundManager.WorldSound getSoundZomb(IsoZombie var1) {
      IsoChunk var2 = null;
      if (var1.soundSourceTarget == null) {
         return null;
      } else {
         if (var1 != null) {
            if (var1.getCurrentSquare() == null) {
               return null;
            }

            var2 = var1.getCurrentSquare().chunk;
         }

         ArrayList var3 = null;
         if (var2 != null && !GameServer.bServer) {
            var3 = var2.SoundList;
         } else {
            var3 = this.SoundList;
         }

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            WorldSoundManager.WorldSound var5 = (WorldSoundManager.WorldSound)var3.get(var4);
            if (var1.soundSourceTarget == var5.source) {
               return var5;
            }
         }

         return null;
      }
   }

   public WorldSoundManager.ResultBiggestSound getBiggestSoundZomb(int var1, int var2, int var3, boolean var4, IsoZombie var5) {
      float var6 = -1000000.0F;
      WorldSoundManager.WorldSound var7 = null;
      int var8 = IsoWorld.instance.getCell().getWidthInTiles();
      int var9 = IsoWorld.instance.getCell().getWidthInTiles();
      IsoChunk var10 = null;
      if (var5 != null) {
         if (var5.getCurrentSquare() == null) {
            return resultBiggestSound.init((WorldSoundManager.WorldSound)null, 0.0F);
         }

         var10 = var5.getCurrentSquare().chunk;
      }

      ArrayList var11 = null;
      boolean var12 = false;
      if (var10 != null && !GameServer.bServer) {
         var11 = var10.SoundList;
         var12 = true;
      } else {
         var11 = this.SoundList;
      }

      for(int var13 = 0; var13 < var11.size(); ++var13) {
         WorldSoundManager.WorldSound var14 = (WorldSoundManager.WorldSound)var11.get(var13);
         if (var14 != null && var14.radius != 0) {
            float var15 = IsoUtils.DistanceToSquared((float)var1, (float)var2, (float)var14.x, (float)var14.y);
            if (!(var15 > (float)(var14.radius * var14.radius)) && (!(var15 < var14.zombieIgnoreDist * var14.zombieIgnoreDist) || var3 != var14.z) && (!var4 || !var14.sourceIsZombie)) {
               IsoGridSquare var16 = IsoWorld.instance.CurrentCell.getGridSquare(var14.x, var14.y, var14.z);
               IsoGridSquare var17 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
               float var18 = var15 / (float)(var14.radius * var14.radius);
               if (var16 != null && var17 != null && var16.getRoom() != var17.getRoom()) {
                  var18 *= 1.2F;
                  if (var17.getRoom() == null || var16.getRoom() == null) {
                     var18 *= 1.4F;
                  }
               }

               var18 = 1.0F - var18;
               if (!(var18 <= 0.0F)) {
                  if (var18 > 1.0F) {
                     var18 = 1.0F;
                  }

                  float var19 = (float)var14.volume * var18;
                  if (var19 > var6) {
                     var6 = var19;
                     var7 = var14;
                  }
               }
            }
         }
      }

      return resultBiggestSound.init(var7, var6);
   }

   public float getSoundAttract(WorldSoundManager.WorldSound var1, IsoZombie var2) {
      if (var1 == null) {
         return 0.0F;
      } else if (var1.radius == 0) {
         return 0.0F;
      } else {
         float var3 = IsoUtils.DistanceToSquared(var2.x, var2.y, (float)var1.x, (float)var1.y);
         if (var3 > (float)(var1.radius * var1.radius)) {
            return 0.0F;
         } else if (var3 < var1.zombieIgnoreDist * var1.zombieIgnoreDist && var2.z == (float)var1.z) {
            return 0.0F;
         } else if (var1.sourceIsZombie) {
            return 0.0F;
         } else {
            IsoGridSquare var4 = IsoWorld.instance.CurrentCell.getGridSquare(var1.x, var1.y, var1.z);
            IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare((double)var2.x, (double)var2.y, (double)var2.z);
            float var6 = var3 / (float)(var1.radius * var1.radius);
            if (var4 != null && var5 != null && var4.getRoom() != var5.getRoom()) {
               var6 *= 1.2F;
               if (var5.getRoom() == null || var4.getRoom() == null) {
                  var6 *= 1.4F;
               }
            }

            var6 = 1.0F - var6;
            if (var6 <= 0.0F) {
               return 0.0F;
            } else {
               if (var6 > 1.0F) {
                  var6 = 1.0F;
               }

               float var7 = (float)var1.volume * var6;
               return var7;
            }
         }
      }
   }

   public float getStressFromSounds(int var1, int var2, int var3) {
      float var4 = 0.0F;

      for(int var5 = 0; var5 < this.SoundList.size(); ++var5) {
         WorldSoundManager.WorldSound var6 = (WorldSoundManager.WorldSound)this.SoundList.get(var5);
         if (var6.stresshumans && var6.radius != 0) {
            float var7 = IsoUtils.DistanceManhatten((float)var1, (float)var2, (float)var6.x, (float)var6.y);
            float var8 = var7 / (float)var6.radius;
            var8 = 1.0F - var8;
            if (!(var8 <= 0.0F)) {
               if (var8 > 1.0F) {
                  var8 = 1.0F;
               }

               float var9 = var8 * var6.stressMod;
               var4 += var9;
            }
         }
      }

      return var4;
   }

   public void update() {
      int var1;
      if (!GameClient.bClient && !GameServer.bServer) {
         for(var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
            IsoChunkMap var2 = IsoWorld.instance.CurrentCell.ChunkMap[var1];
            if (!var2.ignore) {
               for(int var3 = 0; var3 < IsoChunkMap.ChunkGridWidth; ++var3) {
                  for(int var4 = 0; var4 < IsoChunkMap.ChunkGridWidth; ++var4) {
                     IsoChunk var5 = var2.getChunk(var4, var3);
                     if (var5 != null) {
                        var5.updateSounds();
                     }
                  }
               }
            }
         }
      }

      var1 = this.SoundList.size();

      for(int var6 = 0; var6 < var1; ++var6) {
         WorldSoundManager.WorldSound var7 = (WorldSoundManager.WorldSound)this.SoundList.get(var6);
         if (var7 != null && var7.life > 0) {
            --var7.life;
         } else {
            this.SoundList.remove(var6);
            this.freeSounds.push(var7);
            --var6;
            --var1;
         }
      }

   }

   public void render() {
      if (Core.bDebug && DebugOptions.instance.WorldSoundRender.getValue()) {
         if (!GameClient.bClient) {
            if (!GameServer.bServer || ServerGUI.isCreated()) {
               for(int var3 = 0; var3 < this.SoundList.size(); ++var3) {
                  WorldSoundManager.WorldSound var4 = (WorldSoundManager.WorldSound)this.SoundList.get(var3);

                  for(double var5 = 0.0D; var5 < 6.283185307179586D; var5 += 0.15707963267948966D) {
                     this.DrawIsoLine((float)var4.x + (float)var4.radius * (float)Math.cos(var5), (float)var4.y + (float)var4.radius * (float)Math.sin(var5), (float)var4.x + (float)var4.radius * (float)Math.cos(var5 + 0.15707963267948966D), (float)var4.y + (float)var4.radius * (float)Math.sin(var5 + 0.15707963267948966D), (float)var4.z, 1.0F, 1.0F, 1.0F, 1.0F, 1);
                  }
               }

               if (!GameServer.bServer) {
                  IsoChunkMap var16 = IsoWorld.instance.CurrentCell.getChunkMap(0);
                  if (var16 != null && !var16.ignore) {
                     for(int var17 = 0; var17 < IsoChunkMap.ChunkGridWidth; ++var17) {
                        for(int var18 = 0; var18 < IsoChunkMap.ChunkGridWidth; ++var18) {
                           IsoChunk var6 = var16.getChunk(var18, var17);
                           if (var6 != null) {
                              for(int var7 = 0; var7 < var6.SoundList.size(); ++var7) {
                                 WorldSoundManager.WorldSound var8 = (WorldSoundManager.WorldSound)var6.SoundList.get(var7);

                                 for(double var9 = 0.0D; var9 < 6.283185307179586D; var9 += 0.15707963267948966D) {
                                    this.DrawIsoLine((float)var8.x + (float)var8.radius * (float)Math.cos(var9), (float)var8.y + (float)var8.radius * (float)Math.sin(var9), (float)var8.x + (float)var8.radius * (float)Math.cos(var9 + 0.15707963267948966D), (float)var8.y + (float)var8.radius * (float)Math.sin(var9 + 0.15707963267948966D), (float)var8.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
                                    float var12 = (float)(var6.wx * 10) + 0.1F;
                                    float var13 = (float)(var6.wy * 10) + 0.1F;
                                    float var14 = (float)((var6.wx + 1) * 10) - 0.1F;
                                    float var15 = (float)((var6.wy + 1) * 10) - 0.1F;
                                    this.DrawIsoLine(var12, var13, var14, var13, (float)var8.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
                                    this.DrawIsoLine(var14, var13, var14, var15, (float)var8.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
                                    this.DrawIsoLine(var14, var15, var12, var15, (float)var8.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
                                    this.DrawIsoLine(var12, var15, var12, var13, (float)var8.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
                                 }
                              }
                           }
                        }
                     }

                  }
               }
            }
         }
      }
   }

   private void DrawIsoLine(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10) {
      float var11 = IsoUtils.XToScreenExact(var1, var2, var5, 0);
      float var12 = IsoUtils.YToScreenExact(var1, var2, var5, 0);
      float var13 = IsoUtils.XToScreenExact(var3, var4, var5, 0);
      float var14 = IsoUtils.YToScreenExact(var3, var4, var5, 0);
      LineDrawer.drawLine(var11, var12, var13, var14, var6, var7, var8, var9, var10);
   }

   public class WorldSound {
      public IsoObject source = null;
      public int life = 1;
      public int radius;
      public boolean stresshumans;
      public int volume;
      public int x;
      public int y;
      public int z;
      public float zombieIgnoreDist = 0.0F;
      public boolean sourceIsZombie;
      private float stressMod = 1.0F;

      public WorldSoundManager.WorldSound init(IsoObject var1, int var2, int var3, int var4, int var5, int var6) {
         this.source = var1;
         this.life = 1;
         this.x = var2;
         this.y = var3;
         this.z = var4;
         this.radius = var5;
         this.volume = var6;
         this.stresshumans = false;
         this.zombieIgnoreDist = 0.0F;
         this.stressMod = 1.0F;
         this.sourceIsZombie = var1 != null && var1 instanceof IsoZombie;
         return this;
      }

      public WorldSoundManager.WorldSound init(IsoObject var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
         this.source = var1;
         this.life = 1;
         this.x = var2;
         this.y = var3;
         this.z = var4;
         this.radius = var5;
         this.volume = var6;
         this.stresshumans = var7;
         this.zombieIgnoreDist = 0.0F;
         this.stressMod = 1.0F;
         this.sourceIsZombie = var1 != null && var1 instanceof IsoZombie;
         return this;
      }

      public WorldSoundManager.WorldSound init(IsoObject var1, int var2, int var3, int var4, int var5, int var6, boolean var7, float var8, float var9) {
         this.source = var1;
         this.life = 1;
         this.x = var2;
         this.y = var3;
         this.z = var4;
         this.radius = var5;
         this.volume = var6;
         this.stresshumans = var7;
         this.zombieIgnoreDist = var8;
         this.stressMod = var9;
         this.sourceIsZombie = var1 != null && var1 instanceof IsoZombie;
         return this;
      }

      public WorldSoundManager.WorldSound init(boolean var1, int var2, int var3, int var4, int var5, int var6, boolean var7, float var8, float var9) {
         this.source = null;
         this.life = 1;
         this.x = var2;
         this.y = var3;
         this.z = var4;
         this.radius = var5;
         this.volume = var6;
         this.stresshumans = var7;
         this.zombieIgnoreDist = var8;
         this.stressMod = var9;
         this.sourceIsZombie = var1;
         return this;
      }
   }

   public static final class ResultBiggestSound {
      public WorldSoundManager.WorldSound sound;
      public float attract;

      public WorldSoundManager.ResultBiggestSound init(WorldSoundManager.WorldSound var1, float var2) {
         this.sound = var1;
         this.attract = var2;
         return this;
      }
   }
}
