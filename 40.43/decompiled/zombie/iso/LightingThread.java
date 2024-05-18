package zombie.iso;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;
import zombie.GameApplet;
import zombie.GameTime;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.ui.FPSGraph;

public class LightingThread {
   public static LightingThread instance = new LightingThread();
   public Thread lightingThread;
   public boolean bFinished = false;
   public boolean bMovedMap = false;
   public static int skip = 0;
   private int[] cacheX = new int[4];
   private int[] cacheY = new int[4];
   private int[] cacheZ = new int[4];
   public volatile boolean Interrupted = false;
   public static boolean DebugLockTime = false;
   public boolean disable = false;
   public boolean UpdateDone = false;
   public int Stage = 0;
   public long UpdateTime = 0L;
   public volatile Boolean bHasLock = new Boolean(false);
   public boolean newLightingMethod = true;
   public boolean jniLighting = true;

   public boolean DoLightingUpdate(IsoCell var1, int var2) throws InterruptedException {
      if (var1 == null) {
         return false;
      } else {
         IsoPlayer var3 = IsoPlayer.players[var2];
         if (var3 != null && var3.getCurrentSquare() != null) {
            if (this.disable) {
               return false;
            } else if (skip < PerformanceSettings.LightingFrameSkip && PerformanceSettings.LightingFrameSkip != 3) {
               ++skip;
               return false;
            } else if (skip < PerformanceSettings.LightingFrameSkip - 1 && PerformanceSettings.LightingFrameSkip == 3) {
               ++skip;
               return false;
            } else if (GameServer.bServer) {
               return false;
            } else {
               skip = 0;
               byte var4 = 8;
               IsoGameCharacter.LightInfo var5 = var3.initLightInfo2();
               if (this.cacheX[var2] != (int)var5.x || this.cacheY[var2] != (int)var5.y || this.cacheZ[var2] != (int)var5.z) {
                  this.cacheX[var2] = (int)var5.x;
                  this.cacheY[var2] = (int)var5.y;
                  this.cacheZ[var2] = (int)var5.z;
                  LosUtil.cachecleared[var2] = true;
               }

               int var6;
               int var7;
               int var8;
               if (LosUtil.cachecleared[var2]) {
                  var6 = 0;

                  while(true) {
                     if (var6 >= LosUtil.XSIZE) {
                        LosUtil.cachecleared[var2] = false;
                        break;
                     }

                     for(var7 = 0; var7 < LosUtil.YSIZE; ++var7) {
                        for(var8 = 0; var8 < LosUtil.ZSIZE; ++var8) {
                           LosUtil.cachedresults[var6][var7][var8][var2] = 0;
                        }

                        if (Thread.interrupted() || this.Interrupted) {
                           return false;
                        }
                     }

                     ++var6;
                  }
               }

               IsoGridSquare.rmodLT = var5.rmod;
               IsoGridSquare.gmodLT = var5.gmod;
               IsoGridSquare.bmodLT = var5.bmod;

               IsoGridSquare var9;
               for(var6 = 0; var6 < var4; ++var6) {
                  for(var7 = 0; var7 < var1.getWidthInTiles(); ++var7) {
                     for(var8 = 0; var8 < var1.getWidthInTiles(); ++var8) {
                        if (Thread.interrupted() || this.Interrupted) {
                           return false;
                        }

                        if (this.bMovedMap) {
                           this.bMovedMap = false;
                           return false;
                        }

                        var9 = var1.getGridSquareDirect(var8, var7, var6, var2);
                        if (var9 != null) {
                           var9.CalcVisibility(var2);
                        }
                     }
                  }
               }

               for(var6 = 0; var6 < var4; ++var6) {
                  for(var7 = 0; var7 < var1.getWidthInTiles(); ++var7) {
                     for(var8 = 0; var8 < var1.getWidthInTiles(); ++var8) {
                        if (Thread.interrupted() || this.Interrupted) {
                           return false;
                        }

                        if (this.bMovedMap) {
                           this.bMovedMap = false;
                           return false;
                        }

                        var9 = var1.getGridSquareDirect(var8, var7, var6, var2);
                        if (var9 != null) {
                           var9.CalcLightInfo(var2);
                           if (PerformanceSettings.LightingFrameSkip >= 1 && PerformanceSettings.LightingFrameSkip < 3) {
                              var1.CalculateVertColoursForTile(var9, 0, 0, 0, var2);
                           }
                        }
                     }
                  }
               }

               if (PerformanceSettings.LightingFrameSkip == 0) {
                  for(var6 = 0; var6 < var4; ++var6) {
                     for(var7 = 0; var7 < var1.getWidthInTiles(); ++var7) {
                        for(var8 = 0; var8 < var1.getWidthInTiles(); ++var8) {
                           if (Thread.interrupted() || this.Interrupted) {
                              return false;
                           }

                           if (this.bMovedMap) {
                              this.bMovedMap = false;
                              return false;
                           }

                           var9 = var1.getGridSquareDirect(var8, var7, var6, var2);
                           if (var9 != null) {
                              var1.CalculateVertColoursForTile(var9, 0, 0, 0, var2);
                           }
                        }
                     }
                  }
               }

               if (Core.bDebug && FPSGraph.instance != null) {
                  FPSGraph.instance.addLighting(System.currentTimeMillis());
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   public void stop() {
      if (PerformanceSettings.LightingThread) {
         this.bFinished = true;

         while(this.lightingThread.isAlive()) {
         }

         if (this.jniLighting) {
            LightingJNI.stop();
         }

         this.lightingThread = null;
      }
   }

   public void create() {
      if (GameServer.bServer) {
         this.newLightingMethod = false;
         this.jniLighting = false;
      } else if (PerformanceSettings.LightingThread) {
         this.bFinished = false;
         if (this.jniLighting) {
            this.lightingThread = new Thread(new Runnable() {
               public void run() {
                  while(!LightingThread.this.bFinished) {
                     if (IsoWorld.instance.CurrentCell == null) {
                        return;
                     }

                     try {
                        Display.sync(PerformanceSettings.LightingFPS);
                        LightingJNI.DoLightingUpdateNew(System.nanoTime());

                        while(LightingJNI.WaitingForMain() && !LightingThread.this.bFinished) {
                           Thread.sleep(13L);
                        }

                        if (Core.bDebug && FPSGraph.instance != null) {
                           FPSGraph.instance.addLighting(System.currentTimeMillis());
                        }
                     } catch (Exception var2) {
                        var2.printStackTrace();
                     }
                  }

               }
            });
         } else {
            this.lightingThread = new Thread(new Runnable() {
               public void run() {
                  IsoCell var1 = IsoWorld.instance.CurrentCell;
                  if (var1 != null) {
                     if (!GameServer.bServer) {
                        while(!LightingThread.this.bFinished) {
                           if (IsoWorld.instance.CurrentCell == null) {
                              LightingThread.this.bFinished = true;
                           }

                           while(LightingThread.this.UpdateDone) {
                              if (LightingThread.this.bFinished) {
                                 return;
                              }

                              try {
                                 Thread.sleep((long)PerformanceSettings.LightingFPS);
                              } catch (InterruptedException var15) {
                              }
                           }

                           LightingThread.this.UpdateDone = false;

                           try {
                              if (!LightingThread.this.Interrupted && LightingThread.this.Stage == 0) {
                                 Display.sync(PerformanceSettings.LightingFPS);
                              }

                              long var2 = System.nanoTime();
                              double var4 = 0.0D;

                              try {
                                 LightingThread.this.Interrupted = false;
                                 synchronized(LightingThread.this.bHasLock) {
                                    IsoChunkMap.bSettingChunkLighting.lock();
                                    LightingThread.this.bHasLock = true;
                                 }

                                 long var6 = System.nanoTime();
                                 if (LightingThread.this.Stage == 0) {
                                    GameTime.getInstance().lightingUpdate();
                                    LightingThread.this.Stage = 1;
                                 } else {
                                    boolean var8 = LightingThread.this.DoLightingUpdate(var1, LightingThread.this.Stage - 1);
                                    if (var8 || IsoPlayer.players[LightingThread.this.Stage - 1] == null || IsoPlayer.players[LightingThread.this.Stage - 1].current == null) {
                                       ++LightingThread.this.Stage;
                                       if (LightingThread.this.Stage > IsoPlayer.numPlayers) {
                                          LightingThread.this.UpdateTime = (long)((double)System.nanoTime() / 1000000.0D);
                                          LightingThread.this.Stage = 0;
                                          LightingThread.this.UpdateDone = true;
                                       }
                                    }
                                 }

                                 var4 = (double)(System.nanoTime() - var6) / 1000000.0D;
                              } catch (InterruptedException var17) {
                                 LightingThread.this.Interrupted = true;
                              } finally {
                                 LightingThread.this.bHasLock = false;
                                 IsoChunkMap.bSettingChunkLighting.unlock();
                              }

                              double var20 = (double)(System.nanoTime() - var2) / 1000000.0D;
                              if (LightingThread.DebugLockTime && var20 > 10.0D) {
                                 DebugLog.log("LightingThread time " + var4 + "/" + var20 + " ms");
                              }
                           } catch (Exception var19) {
                              Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var19);
                           }
                        }

                     }
                  }
               }
            });
         }

         this.lightingThread.setPriority(5);
         this.lightingThread.setDaemon(true);
         this.lightingThread.setName("Lighting Thread");
         this.lightingThread.start();
      }
   }

   public void GameLoadingUpdate() {
      if (PerformanceSettings.LightingThread && !this.newLightingMethod) {
         while(!this.UpdateDone) {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var5) {
            }
         }

         GameTime var1 = GameTime.getInstance();
         var1.setAmbient(var1.TimeLerp(var1.getAmbientMin(), var1.getAmbientMax(), (float)var1.getDusk(), (float)var1.getDawn()));
         var1.setNight(var1.TimeLerp(var1.getNightMax(), var1.getNightMin(), (float)var1.getDusk(), (float)var1.getDawn()));
         float var2 = var1.Moon * 20.0F;
         var1.setViewDist(var1.TimeLerp(var1.getViewDistMin() + var2, var1.getViewDistMax(), (float)var1.getDusk(), (float)var1.getDawn()));
         var1.setNightTint(var1.TimeLerp(1.0F, 0.0F, (float)var1.getDusk(), (float)var1.getDawn()));
         if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
            var1.setNightTint(0.0F);
         }

         IsoPlayer.players[0].updateLightInfo();
         LosUtil.cachecleared[0] = true;
         GameTime.instance.lightSourceUpdate = 100.0F;
         IsoCell.bReadAltLight = !IsoCell.bReadAltLight;
         this.UpdateDone = false;

         while(!this.UpdateDone) {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var4) {
            }
         }

         IsoCell.bReadAltLight = !IsoCell.bReadAltLight;
         this.UpdateDone = false;
      }

   }

   public void update() {
      if (this.newLightingMethod) {
         if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
            if (this.jniLighting && LightingJNI.init) {
               LightingJNI.update();
            }

         }
      }
   }

   public void scrollLeft(int var1) {
      if (LightingJNI.init) {
         LightingJNI.scrollLeft(var1);
      }

   }

   public void scrollRight(int var1) {
      if (LightingJNI.init) {
         LightingJNI.scrollRight(var1);
      }

   }

   public void scrollUp(int var1) {
      if (LightingJNI.init) {
         LightingJNI.scrollUp(var1);
      }

   }

   public void scrollDown(int var1) {
      if (LightingJNI.init) {
         LightingJNI.scrollDown(var1);
      }

   }
}
