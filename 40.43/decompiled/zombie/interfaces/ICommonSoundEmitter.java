package zombie.interfaces;

public interface ICommonSoundEmitter {
   void setPos(float var1, float var2, float var3);

   long playSound(String var1);

   /** @deprecated */
   @Deprecated
   long playSound(String var1, boolean var2);

   void tick();

   boolean isEmpty();

   void setVolume(long var1, float var3);

   int stopSound(long var1);

   boolean isPlaying(long var1);

   boolean isPlaying(String var1);
}
