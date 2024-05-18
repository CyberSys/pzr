package zombie.core.physics;

public class BulletGlobals {
   public static final boolean DEBUG = false;
   public static final float CONVEX_DISTANCE_MARGIN = 0.04F;
   public static final float FLT_EPSILON = 1.1920929E-7F;
   public static final float SIMD_EPSILON = 1.1920929E-7F;
   public static final float SIMD_2_PI = 6.2831855F;
   public static final float SIMD_PI = 3.1415927F;
   public static final float SIMD_HALF_PI = 1.5707964F;
   public static final float SIMD_RADS_PER_DEG = 0.017453292F;
   public static final float SIMD_DEGS_PER_RAD = 57.295776F;
   public static final float SIMD_INFINITY = Float.MAX_VALUE;
   private static ThreadLocal threadLocal = new ThreadLocal() {
      protected BulletGlobals initialValue() {
         return new BulletGlobals();
      }
   };

   public static void cleanCurrentThread() {
      threadLocal.remove();
      ObjectPool.cleanCurrentThread();
      ArrayPool.cleanCurrentThread();
   }
}
