package zombie.characters;

import zombie.iso.IsoObject;

public class DummyCharacterSoundEmitter extends BaseCharacterSoundEmitter {
   public DummyCharacterSoundEmitter(IsoGameCharacter var1) {
      super(var1);
   }

   public void register() {
   }

   public void unregister() {
   }

   public long playVocals(String var1) {
      return 0L;
   }

   public void playFootsteps(String var1) {
   }

   public long playSound(String var1) {
      return 0L;
   }

   public long playSound(String var1, boolean var2) {
      return 0L;
   }

   public long playSound(String var1, IsoObject var2) {
      return 0L;
   }

   public long playSoundImpl(String var1, IsoObject var2) {
      return 0L;
   }

   public void tick() {
   }

   public void set(float var1, float var2, float var3) {
   }

   public boolean isClear() {
      return false;
   }

   public void setVolume(long var1, float var3) {
   }

   public int stopSound(long var1) {
      return 0;
   }

   public boolean hasSoundsToStart() {
      return false;
   }

   public boolean isPlaying(long var1) {
      return false;
   }

   public boolean isPlaying(String var1) {
      return false;
   }
}
