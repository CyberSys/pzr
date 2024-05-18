package zombie.iso.weather;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.opengl.RenderSettings;
import zombie.iso.weather.fx.SteppedUpdateFloat;

public class WorldFlares {
   private static ArrayList flares = new ArrayList();

   public static void launchFlare(float var0, int var1, int var2, float var3, float var4, float var5) {
      WorldFlares.Flare var6 = new WorldFlares.Flare();
      var6.color.r = var3;
      var6.color.g = var4;
      var6.color.b = var5;
      var6.hasLaunched = true;
      var6.maxLifeTime = var0;
      flares.add(var6);
   }

   public static void update() {
      for(int var0 = flares.size() - 1; var0 >= 0; --var0) {
      }

   }

   public static void applyFlaresForPlayer(RenderSettings.PlayerRenderSettings var0, int var1, IsoPlayer var2) {
   }

   private static class Flare {
      private Color color;
      private boolean hasLaunched;
      private SteppedUpdateFloat intensity;
      private float maxLifeTime;
      private float lifeTime;
      private int nextRandomTargetIntens;

      private Flare() {
         this.color = new Color(1.0F, 0.0F, 0.0F);
         this.hasLaunched = false;
         this.intensity = new SteppedUpdateFloat(0.0F, 0.01F, 0.0F, 1.0F);
         this.nextRandomTargetIntens = 10;
      }

      // $FF: synthetic method
      Flare(Object var1) {
         this();
      }
   }
}
