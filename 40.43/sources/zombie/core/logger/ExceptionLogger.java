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

	public static synchronized void logException(Throwable throwable) {
		try {
			throwable.printStackTrace();
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
		} catch (Throwable throwable2) {
			throwable2.printStackTrace();
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
			byte byte1 = 100;
			byte byte2 = 40;
			int int1 = Core.getInstance().getScreenWidth() - byte1;
			int int2 = Core.getInstance().getScreenHeight() - (int)((float)byte2 * transition.fraction());
			SpriteRenderer.instance.render((Texture)null, int1, int2, byte1, byte2, 0.8F, 0.0F, 0.0F, 1.0F);
			SpriteRenderer.instance.render((Texture)null, int1 + 1, int2 + 1, byte1 - 2, 6, 0.0F, 0.0F, 0.0F, 1.0F);
			int int3 = TextManager.instance.getFontFromEnum(UIFont.Large).getLineHeight();
			TextManager.instance.DrawStringCentre(UIFont.Large, (double)(int1 + byte1 / 2), (double)(int2 + (byte2 - int3) / 2), Integer.toString(exceptionCount), 0.0, 0.0, 0.0, 1.0);
			if (popupFrameCount == 0 && !bHide) {
				popupFrameCount = PerformanceSettings.LockFPS / 2;
				transition.init(500.0F, true);
				bHide = true;
			}
		}
	}
}
