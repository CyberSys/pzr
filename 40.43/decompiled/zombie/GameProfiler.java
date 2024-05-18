package zombie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;
import zombie.ui.TextManager;

public class GameProfiler {
   public static GameProfiler instance = new GameProfiler();
   public long StartTime = 0L;
   public HashMap Areas = new HashMap();
   Stack areaStack = new Stack();
   Stack usedAreaStack = new Stack();
   long TotalTime = 0L;

   public void StartFrame() {
      for(int var1 = 0; var1 < this.usedAreaStack.size(); ++var1) {
         this.areaStack.add(this.usedAreaStack.get(var1));
      }

      this.usedAreaStack.clear();
      this.Areas.clear();
      this.StartTime = System.nanoTime();
   }

   public void Start(String var1) {
      this.Start(var1, 1.0F, 1.0F, 1.0F);
   }

   public void Start(String var1, float var2, float var3, float var4) {
      if (var2 == 244.0F) {
         GameProfiler.ProfileArea var5 = null;
         if (this.Areas.containsKey(var1)) {
            var5 = (GameProfiler.ProfileArea)this.Areas.get(var1);
         } else {
            if (this.areaStack.isEmpty()) {
               var5 = new GameProfiler.ProfileArea();
            } else {
               var5 = (GameProfiler.ProfileArea)this.areaStack.pop();
            }

            var5.Total = 0L;
            this.usedAreaStack.add(var5);
         }

         var5.r = var2;
         var5.g = var3;
         var5.b = var4;
         var5.StartTime = System.nanoTime();
         this.Areas.put(var1, var5);
      }
   }

   public void End(String var1) {
      if (var1 == null) {
         GameProfiler.ProfileArea var2 = (GameProfiler.ProfileArea)this.Areas.get(var1);
         var2.EndTime = System.nanoTime();
         var2.Total += var2.EndTime - var2.StartTime;
      }
   }

   public void RenderTime(String var1, Long var2, int var3, int var4, float var5, float var6, float var7) {
      Float var8 = (float)var2 / 10000.0F;
      var8 = (float)((int)(var8 * 100.0F)) / 100.0F;
      TextManager.instance.DrawString((double)var3, (double)var4, var1, (double)var5, (double)var6, (double)var7, 1.0D);
      TextManager.instance.DrawStringRight((double)(var3 + 300), (double)var4, var8.toString(), (double)var5, (double)var6, (double)var7, 1.0D);
   }

   public void RenderPercent(String var1, Long var2, int var3, int var4, float var5, float var6, float var7) {
      Float var8 = (float)var2 / (float)this.TotalTime;
      var8 = var8 * 100.0F;
      var8 = (float)((int)(var8 * 10.0F)) / 10.0F;
      TextManager.instance.DrawString((double)var3, (double)var4, var1, (double)var5, (double)var6, (double)var7, 1.0D);
      TextManager.instance.DrawString((double)(var3 + 300), (double)var4, var8.toString() + "%", (double)var5, (double)var6, (double)var7, 1.0D);
   }

   public void render(int var1, int var2) {
      long var3 = System.nanoTime();
      this.TotalTime = var3 - this.StartTime;

      for(Iterator var5 = this.Areas.entrySet().iterator(); var5.hasNext(); var2 += 11) {
         Entry var6 = (Entry)var5.next();
         this.RenderPercent((String)var6.getKey(), ((GameProfiler.ProfileArea)var6.getValue()).Total, var1, var2, ((GameProfiler.ProfileArea)var6.getValue()).r, ((GameProfiler.ProfileArea)var6.getValue()).g, ((GameProfiler.ProfileArea)var6.getValue()).b);
      }

      this.StartFrame();
   }

   public class ProfileArea {
      public long Total;
      public long StartTime;
      public long EndTime;
      public float r;
      public float g;
      public float b;
   }
}
