package zombie;

import fmod.javafmod;
import fmod.javafmodJNI;
import fmod.fmod.FMODFootstep;
import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundBank;
import fmod.fmod.FMODVoice;
import fmod.fmod.FMOD_STUDIO_PLAYBACK_STATE;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import zombie.audio.BaseSoundBank;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.IsoPlayer;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.GameSoundScript;

public final class GameSounds {
   protected static final HashMap soundByName = new HashMap();
   protected static final ArrayList sounds = new ArrayList();
   public static final int VERSION = 1;
   private static final GameSounds.BankPreviewSound previewBank = new GameSounds.BankPreviewSound();
   private static final GameSounds.FilePreviewSound previewFile = new GameSounds.FilePreviewSound();
   private static GameSounds.IPreviewSound previewSound;
   public static boolean soundIsPaused = false;

   public static void addSound(GameSound var0) {
      assert !sounds.contains(var0);

      int var1 = sounds.size();
      if (soundByName.containsKey(var0.getName())) {
         for(var1 = 0; var1 < sounds.size() && !((GameSound)sounds.get(var1)).getName().equals(var0.getName()); ++var1) {
         }

         sounds.remove(var1);
      }

      sounds.add(var1, var0);
      soundByName.put(var0.getName(), var0);
   }

   public static boolean isKnownSound(String var0) {
      return soundByName.containsKey(var0);
   }

   public static GameSound getSound(String var0) {
      return getOrCreateSound(var0);
   }

   public static GameSound getOrCreateSound(String var0) {
      GameSound var1 = (GameSound)soundByName.get(var0);
      if (var1 == null) {
         DebugLog.log("WARNING: no GameSound called \"" + var0 + "\", adding a new one");
         var1 = new GameSound();
         var1.name = var0;
         var1.category = "AUTO";
         GameSoundClip var2 = new GameSoundClip(var1);
         var1.clips.add(var2);
         sounds.add(var1);
         soundByName.put(var0.replace(".wav", "").replace(".ogg", ""), var1);
         if (BaseSoundBank.instance instanceof FMODSoundBank) {
            long var3 = javafmodJNI.FMOD_Studio_GetEvent(var0);
            if (var3 > 0L) {
               var2.event = var0;
            } else {
               String var5 = null;
               if (ZomboidFileSystem.instance.getAbsolutePath("media/sound/" + var0 + ".ogg") != null) {
                  var5 = "media/sound/" + var0 + ".ogg";
               } else if (ZomboidFileSystem.instance.getAbsolutePath("media/sound/" + var0 + ".wav") != null) {
                  var5 = "media/sound/" + var0 + ".wav";
               }

               if (var5 != null) {
                  long var6 = FMODManager.instance.loadSound(var5);
                  if (var6 != 0L) {
                     var2.file = var5;
                  }
               }
            }

            if (var2.event == null && var2.file == null) {
               DebugLog.log("WARNING: couldn't find an FMOD event or .ogg or .wav file for sound \"" + var0 + "\"");
            }
         }
      }

      return var1;
   }

   private static void loadNonBankSounds() {
      if (BaseSoundBank.instance instanceof FMODSoundBank) {
         Iterator var0 = sounds.iterator();

         while(var0.hasNext()) {
            GameSound var1 = (GameSound)var0.next();
            Iterator var2 = var1.clips.iterator();

            while(var2.hasNext()) {
               GameSoundClip var3 = (GameSoundClip)var2.next();
               if (var3.getFile() != null && var3.getFile().isEmpty()) {
               }
            }
         }

      }
   }

   public static void ScriptsLoaded() {
      ArrayList var0 = ScriptManager.instance.getAllGameSounds();

      for(int var1 = 0; var1 < var0.size(); ++var1) {
         GameSoundScript var2 = (GameSoundScript)var0.get(var1);
         if (!var2.gameSound.clips.isEmpty()) {
            addSound(var2.gameSound);
         }
      }

      var0.clear();
      loadNonBankSounds();
      loadINI();
      if (Core.bDebug && BaseSoundBank.instance instanceof FMODSoundBank) {
         HashSet var11 = new HashSet();
         Iterator var12 = sounds.iterator();

         while(var12.hasNext()) {
            GameSound var3 = (GameSound)var12.next();
            Iterator var4 = var3.clips.iterator();

            while(var4.hasNext()) {
               GameSoundClip var5 = (GameSoundClip)var4.next();
               if (var5.getEvent() != null && !var5.getEvent().isEmpty()) {
                  var11.add(var5.getEvent());
               }
            }
         }

         FMODSoundBank var13 = (FMODSoundBank)BaseSoundBank.instance;
         Iterator var14 = var13.footstepMap.values().iterator();

         while(var14.hasNext()) {
            FMODFootstep var16 = (FMODFootstep)var14.next();
            var11.add(var16.wood);
            var11.add(var16.concrete);
            var11.add(var16.grass);
            var11.add(var16.upstairs);
            var11.add(var16.woodCreak);
         }

         var14 = var13.voiceMap.values().iterator();

         while(var14.hasNext()) {
            FMODVoice var17 = (FMODVoice)var14.next();
            var11.add(var17.sound);
         }

         long[] var15 = new long[32];
         long[] var18 = new long[512];
         int var19 = javafmodJNI.FMOD_Studio_System_GetBankList(var15);

         for(int var6 = 0; var6 < var19; ++var6) {
            int var7 = javafmodJNI.FMOD_Studio_Bank_GetEventList(var15[var6], var18);

            for(int var8 = 0; var8 < var7; ++var8) {
               try {
                  String var9 = javafmodJNI.FMOD_Studio_EventDescription_GetPath(var18[var8]);
                  var9 = var9.replace("event:/", "");
                  if (!var11.contains(var9)) {
                     DebugLog.log("WARNING: FMOD event \"" + var9 + "\" not used by any GameSound");
                  }
               } catch (Exception var10) {
                  DebugLog.log("ERROR: FMOD cannot get path for " + var18[var8] + " event");
               }
            }
         }
      }

   }

   public static void ReloadFile(String var0) {
      try {
         ScriptManager.instance.LoadFile(var0, true);
         ArrayList var1 = ScriptManager.instance.getAllGameSounds();

         for(int var2 = 0; var2 < var1.size(); ++var2) {
            GameSoundScript var3 = (GameSoundScript)var1.get(var2);
            if (!sounds.contains(var3.gameSound) && !var3.gameSound.clips.isEmpty()) {
               addSound(var3.gameSound);
            }
         }
      } catch (Throwable var4) {
         ExceptionLogger.logException(var4);
      }

   }

   public static ArrayList getCategories() {
      HashSet var0 = new HashSet();
      Iterator var1 = sounds.iterator();

      while(var1.hasNext()) {
         GameSound var2 = (GameSound)var1.next();
         var0.add(var2.getCategory());
      }

      ArrayList var3 = new ArrayList(var0);
      Collections.sort(var3);
      return var3;
   }

   public static ArrayList getSoundsInCategory(String var0) {
      ArrayList var1 = new ArrayList();
      Iterator var2 = sounds.iterator();

      while(var2.hasNext()) {
         GameSound var3 = (GameSound)var2.next();
         if (var3.getCategory().equals(var0)) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public static void loadINI() {
      String var0 = GameWindow.getCacheDir() + File.separator + "sounds.ini";
      ConfigFile var1 = new ConfigFile();
      if (var1.read(var0)) {
         if (var1.getVersion() <= 1) {
            Iterator var2 = var1.getOptions().iterator();

            while(var2.hasNext()) {
               ConfigOption var3 = (ConfigOption)var2.next();
               GameSound var4 = (GameSound)soundByName.get(var3.getName());
               if (var4 != null) {
                  var4.userVolume = Float.parseFloat(var3.getValueAsString());
               }
            }

         }
      }
   }

   public static void saveINI() {
      ArrayList var0 = new ArrayList();
      Iterator var1 = sounds.iterator();

      while(var1.hasNext()) {
         GameSound var2 = (GameSound)var1.next();
         DoubleConfigOption var3 = new DoubleConfigOption(var2.getName(), 0.0D, 2.0D, 0.0D);
         var3.setValue((double)var2.userVolume);
         var0.add(var3);
      }

      String var4 = GameWindow.getCacheDir() + File.separator + "sounds.ini";
      ConfigFile var5 = new ConfigFile();
      if (var5.write(var4, 1, var0)) {
         var0.clear();
      }
   }

   public static void previewSound(String var0) {
      if (!Core.SoundDisabled) {
         if (isKnownSound(var0)) {
            GameSound var1 = getSound(var0);
            if (var1 == null) {
               DebugLog.log("no such GameSound " + var0);
            } else {
               GameSoundClip var2 = var1.getRandomClip();
               if (var2 == null) {
                  DebugLog.log("GameSound.clips is empty");
               } else {
                  if (soundIsPaused) {
                     if (!GameClient.bClient) {
                        long var3 = javafmod.FMOD_System_GetMasterChannelGroup();
                        javafmod.FMOD_ChannelGroup_SetVolume(var3, 1.0F);
                     }

                     soundIsPaused = false;
                  }

                  if (previewSound != null) {
                     previewSound.stop();
                  }

                  if (var2.getEvent() != null) {
                     if (previewBank.play(var2)) {
                        previewSound = previewBank;
                     }
                  } else if (var2.getFile() != null && previewFile.play(var2)) {
                     previewSound = previewFile;
                  }

               }
            }
         }
      }
   }

   public static void stopPreview() {
      if (previewSound != null) {
         previewSound.stop();
         previewSound = null;
      }
   }

   public static boolean isPreviewPlaying() {
      if (previewSound == null) {
         return false;
      } else if (previewSound.update()) {
         previewSound = null;
         return false;
      } else {
         return previewSound.isPlaying();
      }
   }

   public static void fix3DListenerPosition(boolean var0) {
      if (!Core.SoundDisabled) {
         if (var0) {
            javafmod.FMOD_Studio_Listener3D(0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
         } else {
            for(int var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
               IsoPlayer var2 = IsoPlayer.players[var1];
               if (var2 != null && !var2.HasTrait("Deaf")) {
                  javafmod.FMOD_Studio_Listener3D(var1, var2.x, var2.y, var2.z * 3.0F, 0.0F, 0.0F, 0.0F, -1.0F / (float)Math.sqrt(2.0D), -1.0F / (float)Math.sqrt(2.0D), 0.0F, 0.0F, 0.0F, 1.0F);
               }
            }
         }

      }
   }

   public static void Reset() {
      sounds.clear();
      soundByName.clear();
      if (previewSound != null) {
         previewSound.stop();
         previewSound = null;
      }

   }

   private static final class FilePreviewSound implements GameSounds.IPreviewSound {
      long channel;
      GameSoundClip clip;
      float effectiveGain;

      private FilePreviewSound() {
      }

      public boolean play(GameSoundClip var1) {
         GameSound var2 = var1.gameSound;
         long var3 = FMODManager.instance.loadSound(var1.getFile(), var2.isLooped());
         if (var3 == 0L) {
            return false;
         } else {
            this.channel = javafmod.FMOD_System_PlaySound(var3, true);
            this.clip = var1;
            this.effectiveGain = var1.getEffectiveVolumeInMenu();
            javafmod.FMOD_Channel_SetVolume(this.channel, this.effectiveGain);
            javafmod.FMOD_Channel_SetPitch(this.channel, var1.pitch);
            if (var2.isLooped()) {
               javafmod.FMOD_Channel_SetMode(this.channel, (long)FMODManager.FMOD_LOOP_NORMAL);
            }

            javafmod.FMOD_Channel_SetPaused(this.channel, false);
            return true;
         }
      }

      public boolean isPlaying() {
         return this.channel == 0L ? false : javafmod.FMOD_Channel_IsPlaying(this.channel);
      }

      public boolean update() {
         if (this.channel == 0L) {
            return false;
         } else if (!javafmod.FMOD_Channel_IsPlaying(this.channel)) {
            this.channel = 0L;
            this.clip = null;
            return true;
         } else {
            float var1 = this.clip.getEffectiveVolumeInMenu();
            if (this.effectiveGain != var1) {
               this.effectiveGain = var1;
               javafmod.FMOD_Channel_SetVolume(this.channel, this.effectiveGain);
            }

            return false;
         }
      }

      public void stop() {
         if (this.channel != 0L) {
            javafmod.FMOD_Channel_Stop(this.channel);
            this.channel = 0L;
            this.clip = null;
         }
      }

      // $FF: synthetic method
      FilePreviewSound(Object var1) {
         this();
      }
   }

   private static final class BankPreviewSound implements GameSounds.IPreviewSound {
      long instance;
      GameSoundClip clip;
      float effectiveGain;

      private BankPreviewSound() {
      }

      public boolean play(GameSoundClip var1) {
         long var2 = javafmod.FMOD_Studio_System_GetEvent("event:/" + var1.getEvent());
         if (var2 < 0L) {
            DebugLog.log("failed to get event " + var1.getEvent() + ": error=" + var2);
            return false;
         } else {
            this.instance = javafmod.FMOD_Studio_System_CreateEventInstance(var2);
            if (this.instance < 0L) {
               DebugLog.log("failed to create EventInstance: error=" + this.instance);
               this.instance = 0L;
               return false;
            } else {
               this.clip = var1;
               this.effectiveGain = var1.getEffectiveVolumeInMenu();
               javafmod.FMOD_Studio_SetVolume(this.instance, this.effectiveGain);
               javafmod.FMOD_Studio_SetParameter(this.instance, "Occlusion", 0.0F);
               javafmod.FMOD_Studio_StartEvent(this.instance);
               if (var1.gameSound.master == GameSound.MasterVolume.Music) {
                  javafmod.FMOD_Studio_SetParameter(this.instance, "Volume", 10.0F);
               }

               return true;
            }
         }
      }

      public boolean isPlaying() {
         if (this.instance == 0L) {
            return false;
         } else {
            int var1 = javafmod.FMOD_Studio_GetPlaybackState(this.instance);
            if (var1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index) {
               return true;
            } else {
               return var1 != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index;
            }
         }
      }

      public boolean update() {
         if (this.instance == 0L) {
            return false;
         } else {
            int var1 = javafmod.FMOD_Studio_GetPlaybackState(this.instance);
            if (var1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index) {
               return false;
            } else if (var1 == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPED.index) {
               javafmod.FMOD_Studio_ReleaseEventInstance(this.instance);
               this.instance = 0L;
               this.clip = null;
               return true;
            } else {
               float var2 = this.clip.getEffectiveVolumeInMenu();
               if (this.effectiveGain != var2) {
                  this.effectiveGain = var2;
                  javafmod.FMOD_Studio_SetVolume(this.instance, this.effectiveGain);
               }

               return false;
            }
         }
      }

      public void stop() {
         if (this.instance != 0L) {
            javafmod.FMOD_Studio_StopInstance(this.instance);
            javafmod.FMOD_Studio_ReleaseEventInstance(this.instance);
            this.instance = 0L;
            this.clip = null;
         }
      }

      // $FF: synthetic method
      BankPreviewSound(Object var1) {
         this();
      }
   }

   private interface IPreviewSound {
      boolean play(GameSoundClip var1);

      boolean isPlaying();

      boolean update();

      void stop();
   }
}
