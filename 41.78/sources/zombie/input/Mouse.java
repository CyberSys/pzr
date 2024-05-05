package zombie.input;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjglx.LWJGLException;
import org.lwjglx.input.Cursor;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;


public final class Mouse {
	protected static int x;
	protected static int y;
	public static boolean bLeftDown;
	public static boolean bLeftWasDown;
	public static boolean bRightDown;
	public static boolean bRightWasDown;
	public static boolean bMiddleDown;
	public static boolean bMiddleWasDown;
	public static boolean[] m_buttonDownStates;
	public static long lastActivity;
	public static int wheelDelta;
	private static final MouseStateCache s_mouseStateCache = new MouseStateCache();
	public static boolean[] UICaptured = new boolean[10];
	static Cursor blankCursor;
	static Cursor defaultCursor;
	private static boolean isCursorVisible = true;
	private static Texture mouseCursorTexture = null;

	public static int getWheelState() {
		return wheelDelta;
	}

	public static synchronized int getXA() {
		return x;
	}

	public static synchronized int getYA() {
		return y;
	}

	public static synchronized int getX() {
		return (int)((float)x * Core.getInstance().getZoom(0));
	}

	public static synchronized int getY() {
		return (int)((float)y * Core.getInstance().getZoom(0));
	}

	public static boolean isButtonDown(int int1) {
		return m_buttonDownStates != null ? m_buttonDownStates[int1] : false;
	}

	public static void UIBlockButtonDown(int int1) {
		UICaptured[int1] = true;
	}

	public static boolean isButtonDownUICheck(int int1) {
		if (m_buttonDownStates != null) {
			boolean boolean1 = m_buttonDownStates[int1];
			if (!boolean1) {
				UICaptured[int1] = false;
			} else if (UICaptured[int1]) {
				return false;
			}

			return boolean1;
		} else {
			return false;
		}
	}

	public static boolean isLeftDown() {
		return bLeftDown;
	}

	public static boolean isLeftPressed() {
		return !bLeftWasDown && bLeftDown;
	}

	public static boolean isLeftReleased() {
		return bLeftWasDown && !bLeftDown;
	}

	public static boolean isLeftUp() {
		return !bLeftDown;
	}

	public static boolean isMiddleDown() {
		return bMiddleDown;
	}

	public static boolean isMiddlePressed() {
		return !bMiddleWasDown && bMiddleDown;
	}

	public static boolean isMiddleReleased() {
		return bMiddleWasDown && !bMiddleDown;
	}

	public static boolean isMiddleUp() {
		return !bMiddleDown;
	}

	public static boolean isRightDown() {
		return bRightDown;
	}

	public static boolean isRightPressed() {
		return !bRightWasDown && bRightDown;
	}

	public static boolean isRightReleased() {
		return bRightWasDown && !bRightDown;
	}

	public static boolean isRightUp() {
		return !bRightDown;
	}

	public static synchronized void update() {
		MouseState mouseState = s_mouseStateCache.getState();
		if (!mouseState.isCreated()) {
			s_mouseStateCache.swap();
			try {
				org.lwjglx.input.Mouse.create();
			} catch (LWJGLException lWJGLException) {
				lWJGLException.printStackTrace();
			}
		} else {
			bLeftWasDown = bLeftDown;
			bRightWasDown = bRightDown;
			bMiddleWasDown = bMiddleDown;
			int int1 = x;
			int int2 = y;
			x = mouseState.getX();
			y = Core.getInstance().getScreenHeight() - mouseState.getY() - 1;
			bLeftDown = mouseState.isButtonDown(0);
			bRightDown = mouseState.isButtonDown(1);
			bMiddleDown = mouseState.isButtonDown(2);
			wheelDelta = mouseState.getDWheel();
			mouseState.resetDWheel();
			if (m_buttonDownStates == null) {
				m_buttonDownStates = new boolean[mouseState.getButtonCount()];
			}

			for (int int3 = 0; int3 < m_buttonDownStates.length; ++int3) {
				m_buttonDownStates[int3] = mouseState.isButtonDown(int3);
			}

			if (int1 != x || int2 != y || wheelDelta != 0 || bLeftWasDown != bLeftDown || bRightWasDown != bRightDown || bMiddleWasDown != bMiddleDown) {
				lastActivity = System.currentTimeMillis();
			}

			s_mouseStateCache.swap();
		}
	}

	public static void poll() {
		s_mouseStateCache.poll();
	}

	public static synchronized void setXY(int int1, int int2) {
		s_mouseStateCache.getState().setCursorPosition(int1, Core.getInstance().getOffscreenHeight(0) - 1 - int2);
	}

	public static Cursor loadCursor(String string) throws LWJGLException {
		File file = ZomboidFileSystem.instance.getMediaFile("ui/" + string);
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(file);
			int int1 = bufferedImage.getWidth();
			int int2 = bufferedImage.getHeight();
			int[] intArray = new int[int1 * int2];
			for (int int3 = 0; int3 < intArray.length; ++int3) {
				int int4 = int3 % int1;
				int int5 = int2 - 1 - int3 / int1;
				intArray[int3] = bufferedImage.getRGB(int4, int5);
			}

			IntBuffer intBuffer = BufferUtils.createIntBuffer(int1 * int2);
			intBuffer.put(intArray);
			intBuffer.rewind();
			byte byte1 = 1;
			byte byte2 = 1;
			Cursor cursor = new Cursor(int1, int2, byte1, byte2, 1, intBuffer, (IntBuffer)null);
			return cursor;
		} catch (Exception exception) {
			return null;
		}
	}

	public static void initCustomCursor() {
		if (blankCursor == null) {
			try {
				blankCursor = loadCursor("cursor_blank.png");
				defaultCursor = loadCursor("cursor_white.png");
			} catch (LWJGLException lWJGLException) {
				lWJGLException.printStackTrace();
			}
		}

		if (defaultCursor != null) {
			try {
				org.lwjglx.input.Mouse.setNativeCursor(defaultCursor);
			} catch (LWJGLException lWJGLException2) {
				lWJGLException2.printStackTrace();
			}
		}
	}

	public static void setCursorVisible(boolean boolean1) {
		isCursorVisible = boolean1;
	}

	public static boolean isCursorVisible() {
		return isCursorVisible;
	}

	public static void renderCursorTexture() {
		if (isCursorVisible()) {
			if (mouseCursorTexture == null) {
				mouseCursorTexture = Texture.getSharedTexture("media/ui/cursor_white.png");
			}

			if (mouseCursorTexture != null && mouseCursorTexture.isReady()) {
				int int1 = getXA();
				int int2 = getYA();
				byte byte1 = 1;
				byte byte2 = 1;
				SpriteRenderer.instance.render(mouseCursorTexture, (float)(int1 - byte1), (float)(int2 - byte2), (float)mouseCursorTexture.getWidth(), (float)mouseCursorTexture.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
			}
		}
	}
}
