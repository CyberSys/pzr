package zombie.core.utils;

public class UpdateTimer {
   private long time = 0L;

   public UpdateTimer() {
      this.time = System.nanoTime() / 1000000L + 3800L;
   }

   public void reset(long var1) {
      this.time = System.nanoTime() / 1000000L + var1;
   }

   public boolean check() {
      return this.time != 0L && System.nanoTime() / 1000000L + 200L >= this.time;
   }

   public long getTime() {
      return this.time;
   }
}
