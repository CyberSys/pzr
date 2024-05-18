package fmod.fmod;

import fmod.FMOD_STUDIO_EVENT_PROPERTY;
import fmod.javafmod;
import fmod.javafmodJNI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import zombie.GameSounds;
import zombie.SoundManager;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.IsoPlayer;
import zombie.core.PerformanceSettings;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class FMODSoundEmitter extends BaseSoundEmitter {
   private ArrayList ToStart = new ArrayList();
   private ArrayList Instances = new ArrayList();
   public float x;
   public float y;
   public float z;
   public EmitterType emitterType;
   public IsoObject parent;
   private ArrayDeque eventSoundPool = new ArrayDeque();
   private ArrayDeque fileSoundPool = new ArrayDeque();

   public FMODSoundEmitter() {
      SoundManager.instance.registerEmitter(this);
   }

   public void randomStart() {
   }

   public void setPos(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public int stopSound(long var1) {
      int var3;
      FMODSoundEmitter.Sound var4;
      for(var3 = 0; var3 < this.ToStart.size(); ++var3) {
         var4 = (FMODSoundEmitter.Sound)this.ToStart.get(var3);
         if (var4.getRef() == var1) {
            var4.release();
            this.ToStart.remove(var3--);
         }
      }

      for(var3 = 0; var3 < this.Instances.size(); ++var3) {
         var4 = (FMODSoundEmitter.Sound)this.Instances.get(var3);
         if (var4.getRef() == var1) {
            var4.stop();
            var4.release();
            this.Instances.remove(var3--);
         }
      }

      return 0;
   }

   public int stopSoundByName(String var1) {
      GameSound var2 = GameSounds.getSound(var1);
      if (var2 == null) {
         return 0;
      } else {
         int var3;
         FMODSoundEmitter.Sound var4;
         for(var3 = 0; var3 < this.ToStart.size(); ++var3) {
            var4 = (FMODSoundEmitter.Sound)this.ToStart.get(var3);
            if (var2.clips.contains(var4.clip)) {
               var4.release();
               this.ToStart.remove(var3--);
            }
         }

         for(var3 = 0; var3 < this.Instances.size(); ++var3) {
            var4 = (FMODSoundEmitter.Sound)this.Instances.get(var3);
            if (var2.clips.contains(var4.clip)) {
               var4.stop();
               var4.release();
               this.Instances.remove(var3--);
            }
         }

         return 0;
      }
   }

   public void setVolume(long var1, float var3) {
      int var4;
      FMODSoundEmitter.Sound var5;
      for(var4 = 0; var4 < this.ToStart.size(); ++var4) {
         var5 = (FMODSoundEmitter.Sound)this.ToStart.get(var4);
         if (var5.getRef() == var1) {
            var5.volume = var3;
         }
      }

      for(var4 = 0; var4 < this.Instances.size(); ++var4) {
         var5 = (FMODSoundEmitter.Sound)this.Instances.get(var4);
         if (var5.getRef() == var1) {
            var5.volume = var3;
         }
      }

   }

   public void setPitch(long var1, float var3) {
      int var4;
      FMODSoundEmitter.Sound var5;
      for(var4 = 0; var4 < this.ToStart.size(); ++var4) {
         var5 = (FMODSoundEmitter.Sound)this.ToStart.get(var4);
         if (var5.getRef() == var1) {
            var5.pitch = var3;
         }
      }

      for(var4 = 0; var4 < this.Instances.size(); ++var4) {
         var5 = (FMODSoundEmitter.Sound)this.Instances.get(var4);
         if (var5.getRef() == var1) {
            var5.pitch = var3;
         }
      }

   }

   public void setVolumeAll(float var1) {
      int var2;
      FMODSoundEmitter.Sound var3;
      for(var2 = 0; var2 < this.ToStart.size(); ++var2) {
         var3 = (FMODSoundEmitter.Sound)this.ToStart.get(var2);
         var3.volume = var1;
      }

      for(var2 = 0; var2 < this.Instances.size(); ++var2) {
         var3 = (FMODSoundEmitter.Sound)this.Instances.get(var2);
         var3.volume = var1;
      }

   }

   public void stopAll() {
      int var1;
      FMODSoundEmitter.Sound var2;
      for(var1 = 0; var1 < this.ToStart.size(); ++var1) {
         var2 = (FMODSoundEmitter.Sound)this.ToStart.get(var1);
         var2.release();
      }

      for(var1 = 0; var1 < this.Instances.size(); ++var1) {
         var2 = (FMODSoundEmitter.Sound)this.Instances.get(var1);
         var2.stop();
         var2.release();
      }

      this.ToStart.clear();
      this.Instances.clear();
   }

   public long playSound(String var1) {
      if (GameClient.bClient) {
         GameClient.instance.PlayWorldSound(var1, false, (int)this.x, (int)this.y, (int)this.z);
      }

      return GameServer.bServer ? 0L : this.playSoundImpl(var1, (IsoObject)null);
   }

   public long playSound(String var1, int var2, int var3, int var4) {
      this.x = (float)var2;
      this.y = (float)var3;
      this.z = (float)var4;
      return this.playSound(var1);
   }

   public long playSound(String var1, IsoGridSquare var2) {
      this.x = (float)var2.x + 0.5F;
      this.y = (float)var2.y + 0.5F;
      this.z = (float)var2.z;
      return this.playSound(var1);
   }

   public long playSoundImpl(String var1, IsoGridSquare var2) {
      this.x = (float)var2.x + 0.5F;
      this.y = (float)var2.y + 0.5F;
      this.z = (float)var2.z + 0.5F;
      return this.playSoundImpl(var1, (IsoObject)null);
   }

   public long playSound(String var1, boolean var2) {
      return this.playSound(var1);
   }

   public long playSoundImpl(String var1, boolean var2, IsoObject var3) {
      return this.playSoundImpl(var1, var3);
   }

   public long playSoundLooped(String var1) {
      if (GameClient.bClient) {
         GameClient.instance.PlayWorldSound(var1, true, (int)this.x, (int)this.y, (int)this.z);
      }

      return this.playSoundLoopedImpl(var1);
   }

   public long playSoundLoopedImpl(String var1) {
      return this.playSoundImpl(var1, false, (IsoObject)null);
   }

   public long playSound(String var1, IsoObject var2) {
      if (GameClient.bClient) {
         GameClient.instance.PlayWorldSound(var1, false, (int)this.x, (int)this.y, (int)this.z);
      }

      return GameServer.bServer ? 0L : this.playSoundImpl(var1, var2);
   }

   public long playSoundImpl(String var1, IsoObject var2) {
      if (FMODManager.instance.getNumListeners() == 0) {
         return 0L;
      } else {
         GameSound var3 = GameSounds.getSound(var1);
         if (var3 == null) {
            return 0L;
         } else {
            GameSoundClip var4 = var3.getRandomClip();
            return this.playClip(var4, var2);
         }
      }
   }

   public long playClip(GameSoundClip var1, IsoObject var2) {
      FMODSoundEmitter.Sound var3 = this.addSound(var1, 1.0F, var2);
      return var3 == null ? 0L : var3.getRef();
   }

   public long playAmbientSound(String var1) {
      if (FMODManager.instance.getNumListeners() == 0) {
         return 0L;
      } else if (GameServer.bServer) {
         return 0L;
      } else {
         GameSound var2 = GameSounds.getSound(var1);
         if (var2 == null) {
            return 0L;
         } else {
            GameSoundClip var3 = var2.getRandomClip();
            FMODSoundEmitter.Sound var4 = this.addSound(var3, 1.0F, (IsoObject)null);
            if (var4 instanceof FMODSoundEmitter.FileSound) {
               ((FMODSoundEmitter.FileSound)var4).ambient = true;
            }

            return var4 == null ? 0L : var4.getRef();
         }
      }
   }

   public long playAmbientLoopedImpl(String var1) {
      if (FMODManager.instance.getNumListeners() == 0) {
         return 0L;
      } else if (GameServer.bServer) {
         return 0L;
      } else {
         GameSound var2 = GameSounds.getSound(var1);
         if (var2 == null) {
            return 0L;
         } else {
            GameSoundClip var3 = var2.getRandomClip();
            FMODSoundEmitter.Sound var4 = this.addSound(var3, 1.0F, (IsoObject)null);
            return var4 == null ? 0L : var4.getRef();
         }
      }
   }

   public void set3D(long var1, boolean var3) {
      int var4;
      FMODSoundEmitter.Sound var5;
      for(var4 = 0; var4 < this.ToStart.size(); ++var4) {
         var5 = (FMODSoundEmitter.Sound)this.ToStart.get(var4);
         if (var5.getRef() == var1) {
            var5.set3D(var3);
         }
      }

      for(var4 = 0; var4 < this.Instances.size(); ++var4) {
         var5 = (FMODSoundEmitter.Sound)this.Instances.get(var4);
         if (var5.getRef() == var1) {
            var5.set3D(var3);
         }
      }

   }

   public void tick() {
      int var1;
      FMODSoundEmitter.Sound var2;
      for(var1 = 0; var1 < this.ToStart.size(); ++var1) {
         var2 = (FMODSoundEmitter.Sound)this.ToStart.get(var1);
         this.Instances.add(var2);
      }

      for(var1 = 0; var1 < this.Instances.size(); ++var1) {
         var2 = (FMODSoundEmitter.Sound)this.Instances.get(var1);
         boolean var3 = this.ToStart.contains(var2);
         if (var2.tick(var3)) {
            this.Instances.remove(var1--);
            var2.release();
         }
      }

      this.ToStart.clear();
   }

   public boolean hasSoundsToStart() {
      return !this.ToStart.isEmpty();
   }

   public boolean isEmpty() {
      return this.ToStart.isEmpty() && this.Instances.isEmpty();
   }

   public boolean isPlaying(long var1) {
      int var3;
      for(var3 = 0; var3 < this.ToStart.size(); ++var3) {
         if (((FMODSoundEmitter.Sound)this.ToStart.get(var3)).getRef() == var1) {
            return true;
         }
      }

      for(var3 = 0; var3 < this.Instances.size(); ++var3) {
         if (((FMODSoundEmitter.Sound)this.Instances.get(var3)).getRef() == var1) {
            return true;
         }
      }

      return false;
   }

   public boolean isPlaying(String var1) {
      int var2;
      for(var2 = 0; var2 < this.ToStart.size(); ++var2) {
         if (var1.equals(((FMODSoundEmitter.Sound)this.ToStart.get(var2)).name)) {
            return true;
         }
      }

      for(var2 = 0; var2 < this.Instances.size(); ++var2) {
         if (var1.equals(((FMODSoundEmitter.Sound)this.Instances.get(var2)).name)) {
            return true;
         }
      }

      return false;
   }

   private FMODSoundEmitter.EventSound allocEventSound() {
      return this.eventSoundPool.isEmpty() ? new FMODSoundEmitter.EventSound(this) : (FMODSoundEmitter.EventSound)this.eventSoundPool.pop();
   }

   private void releaseEventSound(FMODSoundEmitter.EventSound var1) {
      assert !this.eventSoundPool.contains(var1);

      this.eventSoundPool.push(var1);
   }

   private FMODSoundEmitter.FileSound allocFileSound() {
      return this.fileSoundPool.isEmpty() ? new FMODSoundEmitter.FileSound(this) : (FMODSoundEmitter.FileSound)this.fileSoundPool.pop();
   }

   private void releaseFileSound(FMODSoundEmitter.FileSound var1) {
      assert !this.fileSoundPool.contains(var1);

      this.fileSoundPool.push(var1);
   }

   private FMODSoundEmitter.Sound addSound(GameSoundClip var1, float var2, IsoObject var3) {
      if (var1 == null) {
         DebugLog.log("null sound passed to SoundEmitter.playSoundImpl");
         return null;
      } else {
         long var4;
         long var6;
         if (var1.event != null && !var1.event.isEmpty()) {
            var4 = javafmod.FMOD_Studio_System_GetEvent("event:/" + var1.getEvent());
            if (var4 < 0L) {
               return null;
            } else {
               var6 = javafmod.FMOD_Studio_System_CreateEventInstance(var4);
               if (var6 < 0L) {
                  return null;
               } else {
                  if (var1.hasMinDistance()) {
                     javafmodJNI.FMOD_Studio_EventInstance_SetProperty(var6, FMOD_STUDIO_EVENT_PROPERTY.FMOD_STUDIO_EVENT_PROPERTY_MINIMUM_DISTANCE.ordinal(), var1.getMinDistance());
                  }

                  if (var1.hasMaxDistance()) {
                     javafmodJNI.FMOD_Studio_EventInstance_SetProperty(var6, FMOD_STUDIO_EVENT_PROPERTY.FMOD_STUDIO_EVENT_PROPERTY_MAXIMUM_DISTANCE.ordinal(), var1.getMaxDistance());
                  }

                  FMODSoundEmitter.EventSound var9 = this.allocEventSound();
                  var9.clip = var1;
                  var9.name = var1.gameSound.getName();
                  var9.eventInstance = var6;
                  var9.volume = var2;
                  var9.parent = var3;
                  var9.setVolume = 1.0F;
                  var9.occlusion = -1.0F;
                  this.ToStart.add(var9);
                  return var9;
               }
            }
         } else if (var1.file != null && !var1.file.isEmpty()) {
            var4 = FMODManager.instance.loadSound(var1.file);
            if (var4 == 0L) {
               return null;
            } else {
               var6 = javafmod.FMOD_System_PlaySound(var4, true);
               javafmod.FMOD_Channel_SetVolume(var6, 0.0F);
               javafmod.FMOD_Channel_SetPriority(var6, 9 - var1.priority);
               javafmod.FMOD_Channel_SetChannelGroup(var6, FMODManager.instance.channelGroupInGameNonBankSounds);
               if (var1.distanceMax == 0.0F || this.x == 0.0F && this.y == 0.0F) {
                  javafmod.FMOD_Channel_SetMode(var6, (long)FMODManager.FMOD_2D);
               }

               FMODSoundEmitter.FileSound var8 = this.allocFileSound();
               var8.clip = var1;
               var8.name = var1.gameSound.getName();
               var8.sound = var4;
               var8.pitch = var1.pitch;
               var8.channel = var6;
               var8.parent = var3;
               var8.volume = var2;
               var8.setVolume = 1.0F;
               var8.is3D = -1;
               var8.ambient = false;
               this.ToStart.add(var8);
               return var8;
            }
         } else {
            return null;
         }
      }
   }

   private static final class FileSound extends FMODSoundEmitter.Sound {
      long sound;
      long channel;
      byte is3D = -1;
      boolean ambient;
      float lx;
      float ly;
      float lz;
      private static ArrayDeque pool = new ArrayDeque();

      FileSound(FMODSoundEmitter var1) {
         super(var1);
      }

      long getRef() {
         return this.channel;
      }

      void stop() {
         if (this.channel != 0L) {
            javafmod.FMOD_Channel_Stop(this.channel);
            this.sound = 0L;
            this.channel = 0L;
         }
      }

      void set3D(boolean var1) {
         if (this.is3D != (byte)(var1 ? 1 : 0)) {
            javafmod.FMOD_Channel_SetMode(this.channel, var1 ? (long)FMODManager.FMOD_3D : (long)FMODManager.FMOD_2D);
            if (var1) {
               javafmod.FMOD_Channel_Set3DAttributes(this.channel, this.emitter.x, this.emitter.y, this.emitter.z * 3.0F, 0.0F, 0.0F, 0.0F);
            }

            this.is3D = (byte)(var1 ? 1 : 0);
         }

      }

      void release() {
         this.stop();
         this.emitter.releaseFileSound(this);
      }

      boolean tick(boolean var1) {
         if (var1 && this.clip.gameSound.isLooped()) {
            javafmod.FMOD_Channel_SetMode(this.channel, (long)FMODManager.FMOD_LOOP_NORMAL);
         }

         float var2 = this.clip.distanceMin;
         if (!var1 && !javafmod.FMOD_Channel_IsPlaying(this.channel)) {
            return true;
         } else {
            float var3 = this.emitter.x;
            float var4 = this.emitter.y;
            float var5 = this.emitter.z;
            if (this.clip.gameSound.is3D && (var3 != 0.0F || var4 != 0.0F)) {
               this.lx = var3;
               this.ly = var4;
               this.lz = var5;
               javafmod.FMOD_Channel_Set3DAttributes(this.channel, var3, var4, var5 * 3.0F, var3 - this.lx, var4 - this.ly, var5 * 3.0F - this.lz * 3.0F);
               if (IsoPlayer.numPlayers > 1) {
                  if (var1) {
                     javafmod.FMOD_System_SetReverbDefault(0, FMODManager.FMOD_PRESET_OFF);
                     javafmod.FMOD_Channel_Set3DMinMaxDistance(this.channel, this.clip.distanceMin, this.clip.distanceMax);
                     javafmod.FMOD_Channel_Set3DOcclusion(this.channel, 0.0F, 0.0F);
                  }

                  javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
                  if (var1) {
                     javafmod.FMOD_Channel_SetPaused(this.channel, false);
                  }

                  javafmod.FMOD_Channel_SetReverbProperties(this.channel, 0, 0.0F);
                  javafmod.FMOD_Channel_SetReverbProperties(this.channel, 1, 0.0F);
                  javafmod.FMOD_System_SetReverbDefault(1, FMODManager.FMOD_PRESET_OFF);
                  javafmod.FMOD_Channel_Set3DOcclusion(this.channel, 0.0F, 0.0F);
                  return false;
               } else {
                  float var6 = this.clip.reverbMaxRange;
                  float var7 = IsoUtils.DistanceManhatten(var3, var4, IsoPlayer.instance.x, IsoPlayer.instance.y, var5, IsoPlayer.instance.z) / var6;
                  IsoGridSquare var8 = IsoPlayer.instance.getCurrentSquare();
                  if (var8 == null) {
                     javafmod.FMOD_Channel_Set3DMinMaxDistance(this.channel, var2, this.clip.distanceMax);
                     javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
                     if (var1) {
                        javafmod.FMOD_Channel_SetPaused(this.channel, false);
                     }

                     return false;
                  } else {
                     if (var8.getRoom() == null) {
                        if (!this.ambient) {
                           var7 += IsoPlayer.instance.numNearbyBuildingsRooms / 32.0F;
                        }

                        if (!this.ambient) {
                           var7 += 0.08F;
                        }
                     } else {
                        float var9 = (float)var8.getRoom().Squares.size();
                        if (!this.ambient) {
                           var7 += var9 / 500.0F;
                        }
                     }

                     if (var7 > 1.0F) {
                        var7 = 1.0F;
                     }

                     var7 *= var7;
                     var7 *= var7;
                     var7 *= this.clip.reverbFactor;
                     var7 *= 10.0F;
                     if (IsoPlayer.instance.getCurrentSquare().getRoom() == null && var7 < 0.1F) {
                        var7 = 0.1F;
                     }

                     byte var10;
                     byte var11;
                     byte var15;
                     if (!this.ambient) {
                        if (var8.getRoom() != null) {
                           var15 = 0;
                           var10 = 1;
                           var11 = 2;
                        } else {
                           var15 = 2;
                           var10 = 0;
                           var11 = 1;
                        }
                     } else {
                        var15 = 2;
                        var10 = 0;
                        var11 = 1;
                     }

                     IsoGridSquare var12 = IsoWorld.instance.CurrentCell.getGridSquare((double)var3, (double)var4, (double)var5);
                     if (var12 != null && var12.getZone() != null && (var12.getZone().getType().equals("Forest") || var12.getZone().getType().equals("DeepForest"))) {
                        var15 = 1;
                        var10 = 0;
                        var11 = 2;
                     }

                     javafmod.FMOD_Channel_SetReverbProperties(this.channel, var15, 0.0F);
                     javafmod.FMOD_Channel_SetReverbProperties(this.channel, var10, 0.0F);
                     javafmod.FMOD_Channel_SetReverbProperties(this.channel, var11, 0.0F);
                     javafmod.FMOD_Channel_Set3DMinMaxDistance(this.channel, var2, this.clip.distanceMax);
                     IsoGridSquare var16 = IsoWorld.instance.CurrentCell.getGridSquare((double)var3, (double)var4, (double)var5);
                     float var17 = 0.0F;
                     float var19 = 0.0F;
                     IsoRoom var18;
                     if (var16 != null) {
                        if (!(this.emitter.parent instanceof IsoWindow) && !(this.emitter.parent instanceof IsoDoor)) {
                           if (var16.getRoom() != null) {
                              var18 = IsoPlayer.instance.getCurrentSquare().getRoom();
                              if (var18 == null) {
                                 var17 = 0.33F;
                                 var19 = 0.23F;
                              } else if (var18 != var16.getRoom()) {
                                 var17 = 0.24F;
                                 var19 = 0.24F;
                              }

                              if (var18 != null && var16.getRoom().getBuilding() != var18.getBuilding()) {
                                 var17 = 1.0F;
                                 var19 = 0.8F;
                              }

                              if (var18 != null && var16.getRoom().def.level != (int)IsoPlayer.instance.z) {
                                 var17 = 0.6F;
                                 var19 = 0.6F;
                              }
                           } else {
                              var18 = IsoPlayer.instance.getCurrentSquare().getRoom();
                              if (var18 != null) {
                                 var17 = 0.79F;
                                 var19 = 0.59F;
                              }
                           }
                        } else {
                           var18 = IsoPlayer.instance.getCurrentSquare().getRoom();
                           if (var18 != this.emitter.parent.square.getRoom()) {
                              if (var18 != null && var18.getBuilding() == this.emitter.parent.square.getBuilding()) {
                                 var17 = 0.33F;
                                 var19 = 0.33F;
                              } else {
                                 IsoGridSquare var13 = null;
                                 if (this.emitter.parent instanceof IsoDoor) {
                                    IsoDoor var14 = (IsoDoor)this.emitter.parent;
                                    if (var14.north) {
                                       var13 = IsoWorld.instance.CurrentCell.getGridSquare((double)var14.getX(), (double)(var14.getY() - 1.0F), (double)var14.getZ());
                                    } else {
                                       var13 = IsoWorld.instance.CurrentCell.getGridSquare((double)(var14.getX() - 1.0F), (double)var14.getY(), (double)var14.getZ());
                                    }
                                 } else {
                                    IsoWindow var20 = (IsoWindow)this.emitter.parent;
                                    if (var20.north) {
                                       var13 = IsoWorld.instance.CurrentCell.getGridSquare((double)var20.getX(), (double)(var20.getY() - 1.0F), (double)var20.getZ());
                                    } else {
                                       var13 = IsoWorld.instance.CurrentCell.getGridSquare((double)(var20.getX() - 1.0F), (double)var20.getY(), (double)var20.getZ());
                                    }
                                 }

                                 if (var13 != null) {
                                    var18 = IsoPlayer.instance.getCurrentSquare().getRoom();
                                    if (var18 != null || var13.getRoom() == null) {
                                       if (var18 != null && var13.getRoom() != null && var18.building == var13.getBuilding()) {
                                          if (var18 != var13.getRoom()) {
                                             if (var18.def.level == var13.getZ()) {
                                                var17 = 0.33F;
                                                var19 = 0.33F;
                                             } else {
                                                var17 = 0.6F;
                                                var19 = 0.6F;
                                             }
                                          }
                                       } else {
                                          var17 = 0.33F;
                                          var19 = 0.33F;
                                       }
                                    }
                                 }
                              }
                           }
                        }

                        if (!var16.isCouldSee(IsoPlayer.getPlayerIndex()) && var16 != IsoPlayer.instance.getCurrentSquare()) {
                           var17 += 0.4F;
                        }
                     } else {
                        if (IsoWorld.instance.MetaGrid.getRoomAt((int)var3, (int)var4, (int)var5) != null) {
                           var17 = 1.0F;
                           var19 = 1.0F;
                        }

                        var18 = IsoPlayer.instance.getCurrentSquare().getRoom();
                        if (var18 != null) {
                           var17 += 0.94F;
                        } else {
                           var17 += 0.6F;
                        }
                     }

                     if (var16 != null && (int)IsoPlayer.instance.z != var16.getZ()) {
                        var17 *= 1.3F;
                     }

                     if (var17 > 0.9F) {
                        var17 = 0.9F;
                     }

                     if (var19 > 0.9F) {
                        var19 = 0.9F;
                     }

                     if (this.emitter.emitterType == EmitterType.Footstep && var5 > IsoPlayer.instance.z && var16.getBuilding() == IsoPlayer.instance.getBuilding()) {
                        var17 = 0.0F;
                        var19 = 0.0F;
                     }

                     if ("HouseAlarm".equals(this.name)) {
                        var17 = 0.0F;
                        var19 = 0.0F;
                     }

                     javafmod.FMOD_Channel_Set3DOcclusion(this.channel, var17, var19);
                     javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
                     javafmod.FMOD_Channel_SetPitch(this.channel, this.pitch);
                     if (var1) {
                        javafmod.FMOD_Channel_SetPaused(this.channel, false);
                     }

                     this.lx = var3;
                     this.ly = var4;
                     this.lz = var5;
                     return false;
                  }
               }
            } else {
               if ((var3 != 0.0F || var4 != 0.0F) && (var1 || var3 != this.lx || var4 != this.ly) && this.is3D == 1) {
                  javafmod.FMOD_Channel_Set3DAttributes(this.channel, var3, var4, var5 * 3.0F, 0.0F, 0.0F, 0.0F);
               }

               javafmod.FMOD_Channel_SetVolume(this.channel, this.getVolume());
               javafmod.FMOD_Channel_SetPitch(this.channel, this.pitch);
               if (var1) {
                  javafmod.FMOD_Channel_SetPaused(this.channel, false);
               }

               return false;
            }
         }
      }

      static FMODSoundEmitter.FileSound alloc(FMODSoundEmitter var0) {
         return pool.isEmpty() ? new FMODSoundEmitter.FileSound(var0) : (FMODSoundEmitter.FileSound)pool.pop();
      }
   }

   private static final class EventSound extends FMODSoundEmitter.Sound {
      long eventInstance;
      float occlusion;

      EventSound(FMODSoundEmitter var1) {
         super(var1);
      }

      long getRef() {
         return this.eventInstance;
      }

      void stop() {
         if (this.eventInstance != 0L) {
            javafmod.FMOD_Studio_StopInstance(this.eventInstance);
            javafmod.FMOD_Studio_ReleaseEventInstance(this.eventInstance);
            this.eventInstance = 0L;
         }
      }

      void set3D(boolean var1) {
      }

      void release() {
         this.stop();
         this.emitter.releaseEventSound(this);
      }

      boolean tick(boolean var1) {
         IsoPlayer var2 = IsoPlayer.instance;
         if (IsoPlayer.numPlayers > 1) {
            var2 = null;
         }

         boolean var3 = var2 != null && var2.HasTrait("HardOfHearing");
         if (!var1) {
            int var4 = javafmod.FMOD_Studio_GetPlaybackState(this.eventInstance);
            if (var4 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index) {
               return false;
            }

            if (var4 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index) {
               javafmod.FMOD_Studio_ReleaseEventInstance(this.eventInstance);
               this.eventInstance = 0L;
               return true;
            }
         }

         javafmod.FMOD_Studio_EventInstance3D(this.eventInstance, this.emitter.x, this.emitter.y, this.emitter.z * 3.0F);
         float var8 = this.getVolume() * (var3 ? 0.75F : 1.0F);
         if (var8 != this.setVolume) {
            this.setVolume = var8;
            javafmod.FMOD_Studio_SetVolume(this.eventInstance, var8);
         }

         if (var2 != null && this.emitter.x != 0.0F && this.emitter.y != 0.0F) {
            IsoGridSquare var5 = var2.getCurrentSquare();
            IsoGridSquare var6 = IsoWorld.instance.getCell().getGridSquare((double)this.emitter.x, (double)this.emitter.y, (double)this.emitter.z);
            if (var6 == null) {
               boolean var7 = true;
            }

            float var9 = 0.0F;
            if (var5 != null && var6 != null && !var6.isCouldSee(var2.PlayerIndex)) {
               var9 = 1.0F;
            }

            if (var9 < 0.8F && var3) {
               var9 = 0.8F;
            }

            if (var9 != this.occlusion) {
               if (var1) {
                  this.occlusion = var9;
               } else if (this.occlusion < var9) {
                  this.occlusion += 0.1F * (30.0F / (float)PerformanceSettings.LockFPS);
                  if (this.occlusion > var9) {
                     this.occlusion = var9;
                  }
               } else {
                  this.occlusion -= 0.1F * (30.0F / (float)PerformanceSettings.LockFPS);
                  if (this.occlusion < var9) {
                     this.occlusion = var9;
                  }
               }

               javafmod.FMOD_Studio_SetParameter(this.eventInstance, "Occlusion", this.occlusion);
            }
         }

         if (var1) {
            javafmod.FMOD_Studio_StartEvent(this.eventInstance);
         }

         return false;
      }
   }

   private abstract static class Sound {
      FMODSoundEmitter emitter;
      public GameSoundClip clip;
      public String name;
      public float volume = 1.0F;
      public float pitch = 1.0F;
      public IsoObject parent;
      public float setVolume = 1.0F;

      Sound(FMODSoundEmitter var1) {
         this.emitter = var1;
      }

      abstract long getRef();

      abstract void stop();

      abstract void set3D(boolean var1);

      abstract void release();

      abstract boolean tick(boolean var1);

      float getVolume() {
         this.clip = this.clip.checkReloaded();
         return this.volume * this.clip.getEffectiveVolume();
      }
   }
}
