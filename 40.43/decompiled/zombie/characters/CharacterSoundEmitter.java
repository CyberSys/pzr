package zombie.characters;

import fmod.fmod.EmitterType;
import fmod.fmod.FMODFootstep;
import fmod.fmod.FMODSoundBank;
import fmod.fmod.FMODSoundEmitter;
import fmod.fmod.FMODVoice;
import zombie.SoundManager;
import zombie.interfaces.ICommonSoundEmitter;
import zombie.iso.IsoObject;
import zombie.network.GameServer;

public class CharacterSoundEmitter extends BaseCharacterSoundEmitter implements ICommonSoundEmitter {
   float currentPriority;
   FMODSoundEmitter vocals = new FMODSoundEmitter();
   FMODSoundEmitter footsteps = new FMODSoundEmitter();
   FMODSoundEmitter extra = new FMODSoundEmitter();

   public CharacterSoundEmitter(IsoGameCharacter var1) {
      super(var1);
      this.vocals.emitterType = EmitterType.Voice;
      this.vocals.parent = this.character;
      this.footsteps.emitterType = EmitterType.Footstep;
      this.footsteps.parent = this.character;
      this.extra.emitterType = EmitterType.Extra;
      this.extra.parent = this.character;
   }

   public void register() {
      SoundManager.instance.registerEmitter(this.vocals);
      SoundManager.instance.registerEmitter(this.footsteps);
      SoundManager.instance.registerEmitter(this.extra);
   }

   public void unregister() {
      SoundManager.instance.unregisterEmitter(this.vocals);
      SoundManager.instance.unregisterEmitter(this.footsteps);
      SoundManager.instance.unregisterEmitter(this.extra);
   }

   public long playVocals(String var1) {
      if (GameServer.bServer) {
         return 0L;
      } else {
         FMODVoice var2 = FMODSoundBank.instance.getVoice(var1);
         float var3 = var2.priority;
         long var4 = this.vocals.playSoundImpl(var2.sound, false, (IsoObject)null);
         this.currentPriority = var3;
         return var4;
      }
   }

   public void playFootsteps(String var1) {
      if (!GameServer.bServer) {
         FMODFootstep var2 = FMODSoundBank.instance.getFootstep(var1);
         String var3 = var2.getSoundToPlay(this.character);
         if (var3.equals(var2.wood) && this.character.getCurrentSquare() != null) {
            for(int var4 = 0; var4 < this.character.getCurrentSquare().getSpecialObjects().size(); ++var4) {
               IsoObject var5 = (IsoObject)this.character.getCurrentSquare().getSpecialObjects().get(var4);
               if (var5 != null && var5.getContainer() != null && var5.getSprite() != null && var5.getSprite().getName().startsWith("floors_interior_tilesandwood")) {
                  var3 = var2.woodCreak;
                  break;
               }
            }
         }

         this.footsteps.playSoundImpl(var3, false, (IsoObject)null);
      }
   }

   public long playSound(String var1) {
      return this.character.invisible ? 0L : this.extra.playSound(var1);
   }

   public long playSound(String var1, boolean var2) {
      return this.extra.playSound(var1, var2);
   }

   public long playSound(String var1, IsoObject var2) {
      return GameServer.bServer ? 0L : this.extra.playSound(var1, var2);
   }

   public long playSoundImpl(String var1, IsoObject var2) {
      return this.extra.playSoundImpl(var1, false, var2);
   }

   public void tick() {
      this.vocals.tick();
      this.footsteps.tick();
      this.extra.tick();
   }

   public void setPos(float var1, float var2, float var3) {
      this.set(var1, var2, var3);
   }

   public void set(float var1, float var2, float var3) {
      this.vocals.x = this.footsteps.x = this.extra.x = var1;
      this.vocals.y = this.footsteps.y = this.extra.y = var2;
      this.vocals.z = this.footsteps.z = this.extra.z = var3;
   }

   public boolean isEmpty() {
      return this.isClear();
   }

   public boolean isClear() {
      return this.vocals.isEmpty() && this.footsteps.isEmpty() && this.extra.isEmpty();
   }

   public void setVolume(long var1, float var3) {
      this.extra.setVolume(var1, var3);
   }

   public int stopSound(long var1) {
      this.extra.stopSound(var1);
      return 0;
   }

   public boolean hasSoundsToStart() {
      return this.extra.hasSoundsToStart() || this.footsteps.hasSoundsToStart() || this.vocals.hasSoundsToStart();
   }

   public boolean isPlaying(long var1) {
      return this.extra.isPlaying(var1);
   }

   public boolean isPlaying(String var1) {
      return this.extra.isPlaying(var1) || this.vocals.isPlaying(var1);
   }
}
