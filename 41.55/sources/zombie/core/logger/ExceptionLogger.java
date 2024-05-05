package zombie.core.logger;

import java.util.function.Consumer;
import org.lwjglx.opengl.OpenGLException;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.debug.LogSeverity;
import zombie.network.GameServer;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.ui.UITransition;
import zombie.util.Type;


public final class ExceptionLogger {
	private static int exceptionCount;
	private static boolean bIgnore;
	private static boolean bExceptionPopup = true;
	private static long popupFrameMS = 0L;
	private static UITransition transition = new UITransition();
	private static boolean bHide;

	public static synchronized void logException(Throwable throwable) {
		logException(throwable, (String)null);
	}

	public static synchronized void logException(Throwable throwable, String string) {
		logException(throwable, string, DebugLog.General, LogSeverity.Error);
	}

	public static synchronized void logException(Throwable throwable, String string, DebugLogStream debugLogStream, LogSeverity logSeverity) {
		OpenGLException openGLException = (OpenGLException)Type.tryCastTo(throwable, OpenGLException.class);
		if (openGLException != null) {
			RenderThread.logGLException(openGLException, false);
		}

		debugLogStream.printException(throwable, string, DebugLogStream.generateCallerPrefix(), logSeverity);
		try {
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
			debugLogStream.printException(throwable2, "Exception thrown while trying to logException.", LogSeverity.Error);
		} finally {
			bIgnore = false;
		}
	}

	public static void showPopup() {
		float float1 = popupFrameMS > 0L ? transition.getElapsed() : 0.0F;
		popupFrameMS = 3000L;
		transition.setIgnoreUpdateTime(true);
		transition.init(500.0F, false);
		transition.setElapsed(float1);
		bHide = false;
	}

	public static void render() {
		if (!UIManager.useUIFBO || Core.getInstance().UIRenderThisFrame) {
			boolean boolean1 = false;
			if (boolean1) {
				popupFrameMS = 3000L;
			}

			if (popupFrameMS > 0L) {
				popupFrameMS = (long)((double)popupFrameMS - UIManager.getMillisSinceLastRender());
				transition.update();
				int int1 = TextManager.instance.getFontHeight(UIFont.DebugConsole);
				byte byte1 = 100;
				int int2 = int1 * 2 + 4;
				int int3 = Core.getInstance().getScreenWidth() - byte1;
				int int4 = Core.getInstance().getScreenHeight() - (int)((float)int2 * transition.fraction());
				if (boolean1) {
					int4 = Core.getInstance().getScreenHeight() - int2;
				}

				SpriteRenderer.instance.renderi((Texture)null, int3, int4, byte1, int2, 0.8F, 0.0F, 0.0F, 1.0F, (Consumer)null);
				SpriteRenderer.instance.renderi((Texture)null, int3 + 1, int4 + 1, byte1 - 2, int1 - 1, 0.0F, 0.0F, 0.0F, 1.0F, (Consumer)null);
				TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)(int3 + byte1 / 2), (double)int4, "ERROR", 1.0, 0.0, 0.0, 1.0);
				TextManager.instance.DrawStringCentre(UIFont.DebugConsole, (double)(int3 + byte1 / 2), (double)(int4 + int1), boolean1 ? "999" : Integer.toString(exceptionCount), 0.0, 0.0, 0.0, 1.0);
				if (popupFrameMS <= 0L && !bHide) {
					popupFrameMS = 500L;
					transition.init(500.0F, true);
					bHide = true;
				}
			}
		}
	}
}
