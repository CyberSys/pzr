package zombie.core.logger;

import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.network.GameServer;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UITransition;

public class ExceptionLogger {
   private static int exceptionCount;
   private static boolean bIgnore;
   private static boolean bExceptionPopup = true;
   private static int popupFrameCount = 0;
   private static UITransition transition = new UITransition();
   private static boolean bHide;

   public static synchronized void logException(Throwable var0) {
      try {
         var0.printStackTrace();
         if (bIgnore) {
            return;
         }

         bIgnore = true;
         ++exceptionCount;
         if (GameServer.bServer) {
            return;
         }

         if (bExceptionPopup) {
            showPopup();
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
      } finally {
         bIgnore = false;
      }

   }

   public static void showPopup() {
      popupFrameCount = PerformanceSettings.LockFPS * 3;
      transition.setIgnoreUpdateTime(true);
      transition.init(500.0F, false);
      bHide = false;
   }

   public static void render() {
      if (popupFrameCount > 0) {
         --popupFrameCount;
         transition.update();
         byte var0 = 100;
         byte var1 = 40;
         int var2 = Core.getInstance().getScreenWidth() - var0;
         int var3 = Core.getInstance().getScreenHeight() - (int)((float)var1 * transition.fraction());
         SpriteRenderer.instance.render((Texture)null, var2, var3, var0, var1, 0.8F, 0.0F, 0.0F, 1.0F);
         SpriteRenderer.instance.render((Texture)null, var2 + 1, var3 + 1, var0 - 2, 6, 0.0F, 0.0F, 0.0F, 1.0F);
         int var4 = TextManager.instance.getFontFromEnum(UIFont.Large).getLineHeight();
         TextManager.instance.DrawStringCentre(UIFont.Large, (double)(var2 + var0 / 2), (double)(var3 + (var1 - var4) / 2), Integer.toString(exceptionCount), 0.0D, 0.0D, 0.0D, 1.0D);
         if (popupFrameCount == 0 && !bHide) {
            popupFrameCount = PerformanceSettings.LockFPS / 2;
            transition.init(500.0F, true);
            bHide = true;
         }

      }
   }
}
