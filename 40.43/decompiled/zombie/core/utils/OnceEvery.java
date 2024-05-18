package zombie.core.utils;

import java.util.Stack;
import zombie.GameTime;
import zombie.core.Rand;

public class OnceEvery {
   public static int FPS = 60;
   int millioff = 0;
   int millimax = 0;
   private float seconds;
   static float comp = 0.0F;
   static float last = 0.0F;
   static Stack list = new Stack();

   public OnceEvery(float var1, boolean var2) {
      this.seconds = var1;
      this.millimax = (int)((float)FPS * var1);
      if (var2) {
         this.millioff = Rand.Next(this.millimax);
      }

   }

   public OnceEvery(float var1) {
      this.seconds = var1;
      this.millimax = (int)((float)FPS * var1);
   }

   public void SetFrequency(float var1) {
      this.millimax = (int)((float)FPS * var1);
   }

   public boolean Check() {
      this.millimax = (int)((float)FPS * this.seconds);
      if (this.millimax == 0) {
         return true;
      } else {
         long var1 = ((long)last - (long)this.millioff) % (long)this.millimax;
         long var3 = ((long)comp - (long)this.millioff) % (long)this.millimax;
         return var1 > var3 || (float)this.millimax < comp - last;
      }
   }

   public static void update() {
      last = comp;
      comp += 1.0F * GameTime.instance.getMultiplier();
   }
}
