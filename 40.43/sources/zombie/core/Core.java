package zombie.core;

import fmod.FMOD_DriverInfo;
import fmod.javafmod;
import java.awt.Canvas;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import se.krka.kahlua.vm.KahluaTable;
import zombie.FrameLoader;
import zombie.GameSounds;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.SVNRevision;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorFactory;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.raknet.VoiceManager;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.MultiTextureFBO2;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.core.textures.TextureID;
import zombie.core.utils.OnceEvery;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.IngameState;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.iso.IsoCamera;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.ui.ObjectTooltip;
import zombie.ui.UIManager;
import zombie.ui.UITextBox2;


public class Core {
	public static final boolean bDemo = false;
	public static boolean bTutorial;
	private static boolean fakefullscreen = false;
	public String versionNumber = "40.43";
	public String steamServerVersion = "1.0.0.0";
	public static int SVN_REVISION = SVNRevision.init();
	static Canvas canvas;
	static Canvas fullscreencanvas;
	public static boolean bMultithreadedRendering = true;
	public static boolean bDoubleSize = false;
	public static boolean bAltMoveMethod = false;
	public boolean rosewoodSpawnDone = false;
	private ColorInfo objectHighlitedColor = new ColorInfo(0.98F, 0.56F, 0.11F, 1.0F);
	public MultiTextureFBO2 OffscreenBuffer = new MultiTextureFBO2();
	public static float Zoom = 0.5F;
	public static boolean useLwjgl = true;
	public static boolean DoFiltering = false;
	private String saveFolder = null;
	public static boolean OptionZoom = true;
	public static boolean OptionModsEnabled = true;
	public static String OptionContextMenuFont = "Medium";
	public static String OptionInventoryFont = "Medium";
	public static String OptionTooltipFont = "Small";
	public static String OptionMeasurementFormat = "Metric";
	public static int OptionClockFormat = 1;
	public static boolean OptionClock24Hour = true;
	public static boolean OptionVSync = false;
	public static int OptionSoundVolume = 8;
	public static int OptionMusicVolume = 6;
	public static int OptionAmbientVolume = 5;
	public static int OptionMusicLibrary = 1;
	public static boolean OptionVoiceEnable = true;
	public static int OptionVoiceMode = 3;
	public static int OptionVoiceVADMode = 3;
	public static String OptionVoiceRecordDeviceName = "";
	public static int OptionVoiceVolumeMic = 10;
	public static int OptionVoiceVolumePlayers = 5;
	public static int OptionVehicleEngineVolume = 5;
	public static int OptionReloadDifficulty = 1;
	public static boolean OptionRackProgress = true;
	public static int OptionBloodDecals = 10;
	public static boolean OptionBorderlessWindow = false;
	public static boolean OptionTextureCompression = false;
	public static boolean OptionTexture2x = true;
	private static String OptionZoomLevels1x = "";
	private static String OptionZoomLevels2x = "";
	public static boolean OptionEnableContentTranslations = true;
	public static boolean OptionUIFBO = true;
	public static int OptionUIRenderFPS = 20;
	public static boolean OptionRadialMenuKeyToggle = true;
	public static boolean OptionPanCameraWhileAiming = true;
	public static boolean OptionShowChatTimestamp = false;
	public static boolean OptionShowChatTitle = false;
	public static String OptionChatFontSize = "medium";
	public static float OptionMinChatOpaque = 1.0F;
	public static float OptionMaxChatOpaque = 1.0F;
	public static float OptionChatFadeTime = 0.0F;
	public static boolean OptionChatOpaqueOnFocus = true;
	public static boolean OptionTemperatureDisplayCelsius = false;
	public static boolean OptionDoWindSpriteEffects = true;
	public static boolean OptionDoDoorSpriteEffects = true;
	private boolean showPing = true;
	private boolean forceSnow = false;
	private boolean zombieGroupSound = true;
	private String blinkingMoodle = null;
	private boolean tutorialDone = false;
	private boolean vehiclesWarningShow = false;
	private String poisonousBerry = null;
	private String poisonousMushroom = null;
	private boolean doneNewSaveFolder = false;
	private static String difficulty = "Hardcore";
	public static int TileScale = 2;
	private boolean isSelectingAll = false;
	private boolean showYourUsername = true;
	private ColorInfo mpTextColor = null;
	private boolean isAzerty = false;
	private String seenUpdateText = "";
	private boolean toggleToRun = false;
	private boolean celsius = false;
	private boolean warnMapConflict = false;
	private LinkedList mapOrder = null;
	private boolean riversideDone = false;
	private boolean noSave = false;
	private boolean showFirstTimeVehicleTutorial = false;
	private boolean showFirstTimeWeatherTutorial = true;
	boolean bChallenge;
	public static int width = 1280;
	public static int height = 720;
	public static int MaxJukeBoxesActive = 10;
	public static int NumJukeBoxesActive = 0;
	public static String GameMode = "Sandbox";
	private static String glVersion;
	private static int glMajorVersion = -1;
	private static Core core = new Core();
	public static boolean bDebug = false;
	public static boolean bHighSqualityShadows = true;
	public int lastHeight = 0;
	public int lastWidth = 0;
	public static UITextBox2 CurrentTextEntryBox = null;
	public static String storyDirectory = "mods/";
	public static String modRootDirectory = "mods/media/";
	public Shader RenderShader;
	private Map keyMaps = null;
	public boolean bUseShaders = true;
	private boolean bPerfSkybox = true;
	private boolean bPerfReflections = true;
	private boolean bPerfReflections_new = true;
	public int vidMem = 3;
	public int nGraphicLevel = 5;
	public boolean bSupportsFBO = true;
	public float maxZoom = 1.0F;
	int lviewwid = -1;
	int lviewhei = -1;
	public Vector2[] CircleVecs = new Vector2[32];
	public int UIRenderTick = 0;
	ByteBuffer buffer;
	BufferedImage image;
	Texture buffertexture;
	public boolean bInFrame = false;
	public int version = 1;
	public int fileversion = 7;
	public static final String SUN_JAVA_COMMAND = "sun.java.command";
	public static boolean fullScreen = false;
	static boolean[] bAutoZoom = new boolean[4];
	public static String GameMap = "DEFAULT";
	public static String GameSaveWorld = "";
	public static boolean SafeMode = false;
	public static boolean SafeModeForced = false;
	public static boolean SoundDisabled = false;
	public static boolean bIsSteam = true;
	public int frameStage = 0;
	int stack = 0;
	public static boolean bLastStand = false;
	public static boolean bLoadedWithMultithreaded = false;
	public static boolean bExiting = false;

	public boolean isMultiThread() {
		return bMultithreadedRendering;
	}

	public void setChallenge(boolean boolean1) {
		this.bChallenge = boolean1;
	}

	public boolean isChallenge() {
		return this.bChallenge;
	}

	public void setFramerate(int int1) {
		if (int1 == 1) {
			PerformanceSettings.LockFPS = 61;
		} else if (int1 == 2) {
			PerformanceSettings.LockFPS = 244;
		} else if (int1 == 3) {
			PerformanceSettings.LockFPS = 240;
		} else if (int1 == 4) {
			PerformanceSettings.LockFPS = 165;
		} else if (int1 == 5) {
			PerformanceSettings.LockFPS = 120;
		} else if (int1 == 6) {
			PerformanceSettings.LockFPS = 95;
		} else if (int1 == 7) {
			PerformanceSettings.LockFPS = 90;
		} else if (int1 == 8) {
			PerformanceSettings.LockFPS = 75;
		} else if (int1 == 9) {
			PerformanceSettings.LockFPS = 60;
		} else if (int1 == 10) {
			PerformanceSettings.LockFPS = 55;
		} else if (int1 == 11) {
			PerformanceSettings.LockFPS = 45;
		} else if (int1 == 12) {
			PerformanceSettings.LockFPS = 30;
		} else if (int1 == 13) {
			PerformanceSettings.LockFPS = 24;
		}
	}

	public void setMultiThread(boolean boolean1) {
		bMultithreadedRendering = boolean1;
		try {
			this.saveOptions();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public boolean loadedShader() {
		return this.RenderShader != null;
	}

	public static int getGLMajorVersion() {
		if (glMajorVersion == -1) {
			getOpenGLVersions();
		}

		return glMajorVersion;
	}

	public boolean isDedicated() {
		return FrameLoader.bDedicated;
	}

	public boolean getUseShaders() {
		return this.bUseShaders;
	}

	public boolean getPerfSkybox() {
		return this.bPerfSkybox;
	}

	public void setPerfSkybox(boolean boolean1) {
		this.bPerfSkybox = boolean1;
	}

	public boolean getPerfReflections() {
		return this.bPerfReflections_new;
	}

	public boolean getPerfReflectionsOnLoad() {
		return this.bPerfReflections;
	}

	public void setPerfReflections(boolean boolean1) {
		this.bPerfReflections_new = boolean1;
	}

	public int getVidMem() {
		return SafeMode ? 5 : this.vidMem;
	}

	public void setVidMem(int int1) {
		if (SafeMode) {
			this.vidMem = 5;
		}

		this.vidMem = int1;
		try {
			this.saveOptions();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void setUseShaders(boolean boolean1) {
		this.bUseShaders = boolean1;
		try {
			this.saveOptions();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void shadersOptionChanged() {
		RenderThread.borrowContext();
		if (this.bUseShaders && !SafeModeForced) {
			try {
				if (!FrameLoader.bDedicated && this.RenderShader == null) {
					this.RenderShader = new Shader("screen");
				}

				if (this.RenderShader != null && (this.RenderShader.FragID == 0 || this.RenderShader.VertID == 0)) {
					this.RenderShader = null;
				}
			} catch (Exception exception) {
				this.RenderShader = null;
			}
		} else if (this.RenderShader != null) {
			try {
				this.RenderShader.destroy();
			} catch (Exception exception2) {
				exception2.printStackTrace();
			}

			this.RenderShader = null;
		}

		RenderThread.returnContext();
	}

	public void initShaders() {
		try {
			if (!FrameLoader.bDedicated && this.RenderShader == null && !SafeMode && !SafeModeForced) {
				RenderThread.borrowContext();
				this.RenderShader = new Shader("screen");
				RenderThread.returnContext();
			}

			if (this.RenderShader == null || this.RenderShader.FragID == 0 || this.RenderShader.VertID == 0) {
				this.RenderShader = null;
			}
		} catch (Exception exception) {
			this.RenderShader = null;
			exception.printStackTrace();
		}
	}

	public static String getGLVersion() {
		if (glVersion == null) {
			getOpenGLVersions();
		}

		return glVersion;
	}

	public String getGameMode() {
		return GameMode;
	}

	public static Core getInstance() {
		return core;
	}

	public static void getOpenGLVersions() {
		glVersion = GL11.glGetString(7938);
		glMajorVersion = glVersion.charAt(0) - 48;
	}

	public boolean getDebug() {
		return bDebug;
	}

	public static void setFullScreen(boolean boolean1) throws LWJGLException {
		fullScreen = boolean1;
	}

	public static int[] flipPixels(int[] intArray, int int1, int int2) {
		int[] intArray2 = null;
		if (intArray != null) {
			intArray2 = new int[int1 * int2];
			for (int int3 = 0; int3 < int2; ++int3) {
				for (int int4 = 0; int4 < int1; ++int4) {
					intArray2[(int2 - int3 - 1) * int1 + int4] = intArray[int3 * int1 + int4];
				}
			}
		}

		return intArray2;
	}

	public void TakeScreenshot() {
		GL11.glPixelStorei(3333, 1);
		GL11.glReadBuffer(1028);
		int int1 = Display.getDisplayMode().getWidth();
		int int2 = Display.getDisplayMode().getHeight();
		int int3 = 0;
		int int4 = 0;
		if (int1 > 256) {
			int3 = int1 / 2 - 128;
			int1 = 256;
		}

		if (int2 > 256) {
			int4 = int2 / 2 - 128;
			int2 = 256;
		}

		int4 += 32;
		int3 += 10;
		if (int4 < 0) {
			int4 = 0;
		}

		byte byte1 = 3;
		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(int1 * int2 * byte1);
		GL11.glReadPixels(int3, int4, int1, int2, 6407, 5121, byteBuffer);
		int[] intArray = new int[int1 * int2];
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + GameSaveWorld + File.separator + "thumb.png");
		String string = "png";
		for (int int5 = 0; int5 < intArray.length; ++int5) {
			int int6 = int5 * 3;
			intArray[int5] = -16777216 | (byteBuffer.get(int6) & 255) << 16 | (byteBuffer.get(int6 + 1) & 255) << 8 | (byteBuffer.get(int6 + 2) & 255) << 0;
		}

		byteBuffer = null;
		intArray = flipPixels(intArray, int1, int2);
		BufferedImage bufferedImage = new BufferedImage(int1, int2, 2);
		bufferedImage.setRGB(0, 0, int1, int2, intArray, 0, int1);
		try {
			ImageIO.write(bufferedImage, "png", file);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		Texture.forgetTexture(GameWindow.getGameModeCacheDir() + File.separator + GameSaveWorld + File.separator + "thumb.png");
	}

	public void TakeFullScreenshot(String string) {
		try {
			RenderThread.borrowContext();
			GL11.glPixelStorei(3333, 1);
			GL11.glReadBuffer(1028);
			int int1 = Display.getDisplayMode().getWidth();
			int int2 = Display.getDisplayMode().getHeight();
			byte byte1 = 0;
			byte byte2 = 0;
			byte byte3 = 3;
			ByteBuffer byteBuffer = BufferUtils.createByteBuffer(int1 * int2 * byte3);
			GL11.glReadPixels(byte1, byte2, int1, int2, 6407, 5121, byteBuffer);
			int[] intArray = new int[int1 * int2];
			if (string == null) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
				string = "screenshot_" + simpleDateFormat.format(Calendar.getInstance().getTime()) + ".png";
			}

			File file = new File(GameWindow.getScreenshotDir() + File.separator + string);
			int int3 = 0;
			while (true) {
				if (int3 >= intArray.length) {
					byteBuffer = null;
					intArray = flipPixels(intArray, int1, int2);
					BufferedImage bufferedImage = new BufferedImage(int1, int2, 2);
					bufferedImage.setRGB(0, 0, int1, int2, intArray, 0, int1);
					try {
						ImageIO.write(bufferedImage, "png", file);
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}

					break;
				}

				int int4 = int3 * 3;
				intArray[int3] = -16777216 | (byteBuffer.get(int4) & 255) << 16 | (byteBuffer.get(int4 + 1) & 255) << 8 | (byteBuffer.get(int4 + 2) & 255) << 0;
				++int3;
			}
		} finally {
			RenderThread.returnContext();
		}
	}

	public static boolean supportNPTTexture() {
		return false;
	}

	public boolean supportsFBO() {
		if (FrameLoader.bDedicated) {
			return false;
		} else if (SafeMode) {
			this.OffscreenBuffer.bZoomEnabled = false;
			return false;
		} else if (!this.bSupportsFBO) {
			return false;
		} else if (this.OffscreenBuffer.Current != null) {
			return true;
		} else {
			try {
				if (TextureFBO.checkFBOSupport() && this.setupMultiFBO()) {
					return true;
				} else {
					this.bSupportsFBO = false;
					getInstance();
					SafeMode = true;
					this.OffscreenBuffer.bZoomEnabled = false;
					return false;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				this.bSupportsFBO = false;
				getInstance();
				SafeMode = true;
				this.OffscreenBuffer.bZoomEnabled = false;
				return false;
			}
		}
	}

	void sharedInit() {
		if (this.supportsFBO()) {
		}
	}

	public void MoveMethodToggle() {
		bAltMoveMethod = !bAltMoveMethod;
	}

	public void doubleSizeToggle() {
		bDoubleSize = !bDoubleSize;
		UIManager.resize();
	}

	public void EndFrameText(int int1) {
		if (!LuaManager.thread.bStep) {
			if (this.OffscreenBuffer.Current != null) {
			}

			IndieGL.glDoEndFrame();
			this.frameStage = 2;
		}
	}

	public void EndFrame(int int1) {
		if (!LuaManager.thread.bStep) {
			if (this.OffscreenBuffer.Current != null) {
				SpriteRenderer.instance.glBuffer(0, int1);
			}

			IndieGL.glDoEndFrame();
			this.frameStage = 2;
		}
	}

	public void EndFrame() {
		IndieGL.glDoEndFrame();
		if (this.OffscreenBuffer.Current != null) {
			SpriteRenderer.instance.glBuffer(0, 0);
		}
	}

	public void CalcCircle() {
		Vector2 vector2 = new Vector2(0.0F, -1.0F);
		for (int int1 = 0; int1 < 32; ++int1) {
			this.CircleVecs[int1] = new Vector2(vector2.x, vector2.y);
			vector2.rotate(0.19634955F);
		}
	}

	public void DrawCircle(float float1, float float2, float float3) {
	}

	public void EndFrameUI() {
		if (UIManager.useUIFBO && UIManager.UIFBO == null) {
			UIManager.CreateFBO(width, height);
		}

		if (LuaManager.thread != null && LuaManager.thread.bStep) {
			SpriteRenderer.instance.clearSprites();
			SpriteRenderer.instance.numSprites = 0;
		} else {
			ExceptionLogger.render();
			if (UIManager.useUIFBO && this.UIRenderTick <= 0) {
				SpriteRenderer.instance.glBuffer(3, 0);
				this.UIRenderTick = (int)Math.ceil((double)((float)PerformanceSettings.LockFPS / (float)OptionUIRenderFPS));
			}

			if (UIManager.useUIFBO) {
				SpriteRenderer.instance.bDoAdditive = true;
				SpriteRenderer.instance.render((Texture)UIManager.UIFBO.getTexture(), 0, height, width, -height, 1.0F, 1.0F, 1.0F, 1.0F);
				SpriteRenderer.instance.bDoAdditive = false;
			}

			IndieGL.glDoEndFrame();
			RenderThread.Ready();
			this.frameStage = 0;
		}
	}

	public static void UnfocusActiveTextEntryBox() {
		if (CurrentTextEntryBox != null && !CurrentTextEntryBox.getUIName().contains("chat text entry")) {
			CurrentTextEntryBox.DoingTextEntry = false;
			if (CurrentTextEntryBox.Frame != null) {
				CurrentTextEntryBox.Frame.Colour = CurrentTextEntryBox.StandardFrameColour;
			}

			CurrentTextEntryBox = null;
		}
	}

	public int getOffscreenWidth(int int1) {
		if (this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null) {
			return this.OffscreenBuffer.getWidth(int1);
		} else {
			return IsoPlayer.numPlayers > 1 ? this.getScreenWidth() / 2 : this.getScreenWidth();
		}
	}

	public int getOffscreenHeight(int int1) {
		if (this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null) {
			return this.OffscreenBuffer.getHeight(int1);
		} else {
			return IsoPlayer.numPlayers > 2 ? this.getScreenHeight() / 2 : this.getScreenHeight();
		}
	}

	public int getOffscreenTrueWidth() {
		return this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null ? this.OffscreenBuffer.getTexture(0).getWidth() : this.getScreenWidth();
	}

	public int getOffscreenTrueHeight() {
		return this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null ? this.OffscreenBuffer.getTexture(0).getHeight() : this.getScreenHeight();
	}

	public int getScreenHeight() {
		return height;
	}

	public int getScreenWidth() {
		return width;
	}

	public void setResolutionAndFullScreen(int int1, int int2, boolean boolean1) {
		setDisplayMode(int1, int2, boolean1);
		this.setScreenSize(Display.getWidth(), Display.getHeight());
	}

	public void setResolution(String string) {
		String[] stringArray = string.split("x");
		int int1 = Integer.parseInt(stringArray[0].trim());
		int int2 = Integer.parseInt(stringArray[1].trim());
		if (fullScreen) {
			setDisplayMode(int1, int2, true);
		} else {
			setDisplayMode(int1, int2, false);
		}

		this.setScreenSize(Display.getWidth(), Display.getHeight());
		try {
			this.saveOptions();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void changeCursor(String string) {
	}

	public boolean loadOptions() throws IOException {
		File file = new File(GameWindow.getCacheDir() + File.separator + "options.bin");
		File file2 = new File(GameWindow.getCacheDir() + File.separator + "options.ini");
		int int1;
		int int2;
		if (!file2.exists() && !file.exists()) {
			this.saveFolder = getMyDocumentFolder();
			File file3 = new File(this.saveFolder);
			file3.mkdir();
			this.copyPasteFolders("mods");
			Translator.setLanguage(Language.FromString(System.getProperty("user.language").toUpperCase()));
			if (Translator.getAzertyMap().contains(Translator.getLanguage().name())) {
				this.setAzerty(true);
			}

			JoypadManager.instance.setControllerActive("XInputController0", true);
			JoypadManager.instance.setControllerActive("XInputController1", true);
			JoypadManager.instance.setControllerActive("XInputController2", true);
			JoypadManager.instance.setControllerActive("XInputController3", true);
			if (!GameServer.bServer) {
				try {
					int int3 = 0;
					int2 = 0;
					DisplayMode[] displayModeArray = Display.getAvailableDisplayModes();
					for (int1 = 0; int1 < displayModeArray.length; ++int1) {
						if (displayModeArray[int1].getWidth() > int3 && displayModeArray[int1].getWidth() <= 1920) {
							int3 = displayModeArray[int1].getWidth();
							int2 = displayModeArray[int1].getHeight();
						}
					}

					width = int3;
					height = int2;
				} catch (LWJGLException lWJGLException) {
					lWJGLException.printStackTrace();
				}
			}

			this.setOptionZoomLevels2x("50;75;125;150;175;200");
			this.setOptionZoomLevels1x("50;75;125;150;175;200");
			this.saveOptions();
			return false;
		} else {
			if (!file2.exists() && file.exists()) {
				file2.createNewFile();
				FileWriter fileWriter = new FileWriter(file2);
				FileInputStream fileInputStream = new FileInputStream(file);
				DataInputStream dataInputStream = new DataInputStream(fileInputStream);
				this.version = dataInputStream.readInt();
				fileWriter.write("version=" + this.version + "\r\n");
				width = dataInputStream.readInt();
				fileWriter.write("width=" + width + "\r\n");
				height = dataInputStream.readInt();
				fileWriter.write("height=" + height + "\r\n");
				fullScreen = dataInputStream.readBoolean();
				fileWriter.write("fullScreen=" + fullScreen + "\r\n");
				this.bUseShaders = dataInputStream.readBoolean();
				fileWriter.write("bUseShaders=" + this.bUseShaders + "\r\n");
				if (this.fileversion > 1) {
					this.vidMem = dataInputStream.readInt();
				}

				if (this.fileversion > 2) {
					bMultithreadedRendering = dataInputStream.readBoolean();
				}

				try {
					if (this.version > 3) {
						Translator.setLanguage(dataInputStream.readInt());
					}

					this.saveFolder = getMyDocumentFolder();
				} catch (Exception exception) {
				}

				fileWriter.write("bMultithreadedRendering=" + bMultithreadedRendering + "\r\n");
				if (this.version < 6) {
					this.copyPasteFolders("mods");
				}

				if (Translator.language == null) {
					Translator.setLanguage(Language.FromString(System.getProperty("user.language").toUpperCase()));
				}

				if (Translator.language == null) {
					Translator.setLanguage(Translator.getDefaultLanguage().index());
				}

				fileInputStream.close();
				fileWriter.write("language=" + Translator.language + "\r\n");
				fileWriter.close();
			}

			if (file.exists()) {
				file.delete();
			}

			for (int int4 = 0; int4 < 4; ++int4) {
				this.setAutoZoom(int4, false);
			}

			BufferedReader bufferedReader = new BufferedReader(new FileReader(file2));
			try {
				String string;
				while ((string = bufferedReader.readLine()) != null) {
					if (string.startsWith("version=")) {
						this.version = new Integer(string.replaceFirst("version=", ""));
					} else if (string.startsWith("width=")) {
						width = new Integer(string.replaceFirst("width=", ""));
					} else if (string.startsWith("height=")) {
						height = new Integer(string.replaceFirst("height=", ""));
					} else if (string.startsWith("fullScreen=")) {
						fullScreen = new Boolean(string.replaceFirst("fullScreen=", ""));
					} else if (string.startsWith("frameRate=")) {
						PerformanceSettings.LockFPS = Integer.parseInt(string.replaceFirst("frameRate=", ""));
					} else if (string.startsWith("lighting=")) {
						PerformanceSettings.LightingFrameSkip = Integer.parseInt(string.replaceFirst("lighting=", ""));
					} else if (string.startsWith("lightFPS=")) {
						PerformanceSettings.instance.setLightingFPS(Integer.parseInt(string.replaceFirst("lightFPS=", "")));
					} else if (string.startsWith("newRoofHiding=")) {
						PerformanceSettings.instance.setNewRoofHiding(Boolean.parseBoolean(string.replaceFirst("newRoofHiding=", "")));
					} else if (string.startsWith("bUseShaders=")) {
						this.bUseShaders = new Boolean(string.replaceFirst("bUseShaders=", ""));
					} else if (string.startsWith("bPerfSkybox=")) {
						this.bPerfSkybox = new Boolean(string.replaceFirst("bPerfSkybox=", ""));
					} else if (string.startsWith("bPerfReflections=")) {
						this.bPerfReflections = new Boolean(string.replaceFirst("bPerfReflections=", ""));
						this.bPerfReflections_new = this.bPerfReflections;
					} else if (string.startsWith("bMultithreadedRendering=")) {
						bMultithreadedRendering = new Boolean(string.replaceFirst("bMultithreadedRendering=", ""));
					} else if (string.startsWith("language=")) {
						Translator.setLanguage(Language.FromString(string.replaceFirst("language=", "")));
					} else if (string.startsWith("zoom=")) {
						OptionZoom = Boolean.parseBoolean(string.replaceFirst("zoom=", ""));
					} else {
						String[] stringArray;
						if (string.startsWith("autozoom=")) {
							stringArray = string.replaceFirst("autozoom=", "").split(",");
							for (int2 = 0; int2 < stringArray.length; ++int2) {
								if (!stringArray[int2].isEmpty()) {
									int1 = Integer.parseInt(stringArray[int2]);
									if (int1 >= 1 && int1 <= 4) {
										this.setAutoZoom(int1 - 1, true);
									}
								}
							}
						} else if (string.startsWith("contextMenuFont=")) {
							OptionContextMenuFont = string.replaceFirst("contextMenuFont=", "").trim();
						} else if (string.startsWith("inventoryFont=")) {
							OptionInventoryFont = string.replaceFirst("inventoryFont=", "").trim();
						} else if (string.startsWith("tooltipFont=")) {
							OptionTooltipFont = string.replaceFirst("tooltipFont=", "").trim();
						} else if (string.startsWith("measurementsFormat=")) {
							OptionMeasurementFormat = string.replaceFirst("measurementsFormat=", "").trim();
						} else if (string.startsWith("clockFormat=")) {
							OptionClockFormat = Integer.parseInt(string.replaceFirst("clockFormat=", ""));
						} else if (string.startsWith("clock24Hour=")) {
							OptionClock24Hour = Boolean.parseBoolean(string.replaceFirst("clock24Hour=", ""));
						} else if (string.startsWith("vsync=")) {
							OptionVSync = Boolean.parseBoolean(string.replaceFirst("vsync=", ""));
						} else if (string.startsWith("voiceEnable=")) {
							OptionVoiceEnable = Boolean.parseBoolean(string.replaceFirst("voiceEnable=", ""));
						} else if (string.startsWith("voiceMode=")) {
							OptionVoiceMode = Integer.parseInt(string.replaceFirst("voiceMode=", ""));
						} else if (string.startsWith("voiceVADMode=")) {
							OptionVoiceVADMode = Integer.parseInt(string.replaceFirst("voiceVADMode=", ""));
						} else if (string.startsWith("voiceVolumeMic=")) {
							OptionVoiceVolumeMic = Integer.parseInt(string.replaceFirst("voiceVolumeMic=", ""));
						} else if (string.startsWith("voiceVolumePlayers=")) {
							OptionVoiceVolumePlayers = Integer.parseInt(string.replaceFirst("voiceVolumePlayers=", ""));
						} else if (string.startsWith("voiceRecordDeviceName=")) {
							OptionVoiceRecordDeviceName = string.replaceFirst("voiceRecordDeviceName=", "");
						} else if (string.startsWith("soundVolume=")) {
							OptionSoundVolume = Integer.parseInt(string.replaceFirst("soundVolume=", ""));
						} else if (string.startsWith("musicVolume=")) {
							OptionMusicVolume = Integer.parseInt(string.replaceFirst("musicVolume=", ""));
						} else if (string.startsWith("ambientVolume=")) {
							OptionAmbientVolume = Integer.parseInt(string.replaceFirst("ambientVolume=", ""));
						} else if (string.startsWith("musicLibrary=")) {
							OptionMusicLibrary = Integer.parseInt(string.replaceFirst("musicLibrary=", ""));
						} else if (string.startsWith("vehicleEngineVolume=")) {
							OptionVehicleEngineVolume = Integer.parseInt(string.replaceFirst("vehicleEngineVolume=", ""));
						} else if (string.startsWith("reloadDifficulty=")) {
							OptionReloadDifficulty = Integer.parseInt(string.replaceFirst("reloadDifficulty=", ""));
						} else if (string.startsWith("rackProgress=")) {
							OptionRackProgress = Boolean.parseBoolean(string.replaceFirst("rackProgress=", ""));
						} else if (string.startsWith("controller=")) {
							String string2 = string.replaceFirst("controller=", "");
							if (!string2.isEmpty()) {
								JoypadManager.instance.setControllerActive(string2, true);
							}
						} else if (string.startsWith("numberOf3D=")) {
							PerformanceSettings.numberOf3D = Integer.parseInt(string.replaceFirst("numberOf3D=", ""));
							if (PerformanceSettings.numberOf3D < 0) {
								PerformanceSettings.numberOf3D = 0;
							}

							if (PerformanceSettings.numberOf3D > 9) {
								PerformanceSettings.numberOf3D = 9;
							}
						} else if (string.startsWith("modelsEnabled")) {
							PerformanceSettings.modelsEnabled = Boolean.parseBoolean(string.replaceFirst("modelsEnabled=", ""));
						} else if (string.startsWith("corpses3D")) {
							PerformanceSettings.corpses3D = Boolean.parseBoolean(string.replaceFirst("corpses3D=", ""));
						} else if (string.startsWith("tutorialDone=")) {
							this.tutorialDone = Boolean.parseBoolean(string.replaceFirst("tutorialDone=", ""));
						} else if (string.startsWith("vehiclesWarningShow=")) {
							this.vehiclesWarningShow = Boolean.parseBoolean(string.replaceFirst("vehiclesWarningShow=", ""));
						} else if (string.startsWith("bloodDecals=")) {
							this.setOptionBloodDecals(Integer.parseInt(string.replaceFirst("bloodDecals=", "")));
						} else if (string.startsWith("borderless=")) {
							OptionBorderlessWindow = Boolean.parseBoolean(string.replaceFirst("borderless=", ""));
						} else if (string.startsWith("textureCompression=")) {
							OptionTextureCompression = Boolean.parseBoolean(string.replaceFirst("textureCompression=", ""));
						} else if (string.startsWith("texture2x=")) {
							OptionTexture2x = Boolean.parseBoolean(string.replaceFirst("texture2x=", ""));
						} else if (string.startsWith("zoomLevels1x=")) {
							OptionZoomLevels1x = string.replaceFirst("zoomLevels1x=", "");
						} else if (string.startsWith("zoomLevels2x=")) {
							OptionZoomLevels2x = string.replaceFirst("zoomLevels2x=", "");
						} else if (string.startsWith("showChatTimestamp=")) {
							OptionShowChatTimestamp = Boolean.parseBoolean(string.replaceFirst("showChatTimestamp=", ""));
						} else if (string.startsWith("showChatTitle=")) {
							OptionShowChatTitle = Boolean.parseBoolean(string.replaceFirst("showChatTitle=", ""));
						} else if (string.startsWith("chatFontSize=")) {
							OptionChatFontSize = string.replaceFirst("chatFontSize=", "");
						} else if (string.startsWith("minChatOpaque=")) {
							OptionMinChatOpaque = Float.parseFloat(string.replaceFirst("minChatOpaque=", ""));
						} else if (string.startsWith("maxChatOpaque=")) {
							OptionMaxChatOpaque = Float.parseFloat(string.replaceFirst("maxChatOpaque=", ""));
						} else if (string.startsWith("chatFadeTime=")) {
							OptionChatFadeTime = Float.parseFloat(string.replaceFirst("chatFadeTime=", ""));
						} else if (string.startsWith("chatOpaqueOnFocus=")) {
							OptionChatOpaqueOnFocus = Boolean.parseBoolean(string.replaceFirst("chatOpaqueOnFocus=", ""));
						} else if (string.startsWith("doneNewSaveFolder=")) {
							this.doneNewSaveFolder = Boolean.parseBoolean(string.replaceFirst("doneNewSaveFolder=", ""));
						} else if (string.startsWith("contentTranslationsEnabled=")) {
							OptionEnableContentTranslations = Boolean.parseBoolean(string.replaceFirst("contentTranslationsEnabled=", ""));
						} else if (string.startsWith("showYourUsername=")) {
							this.showYourUsername = Boolean.parseBoolean(string.replaceFirst("showYourUsername=", ""));
						} else if (string.startsWith("riversideDone=")) {
							this.riversideDone = Boolean.parseBoolean(string.replaceFirst("riversideDone=", ""));
						} else if (string.startsWith("rosewoodSpawnDone=")) {
							this.rosewoodSpawnDone = Boolean.parseBoolean(string.replaceFirst("rosewoodSpawnDone=", ""));
						} else {
							float float1;
							float float2;
							float float3;
							if (string.startsWith("mpTextColor=")) {
								stringArray = string.replaceFirst("mpTextColor=", "").split(",");
								float1 = Float.parseFloat(stringArray[0]);
								float2 = Float.parseFloat(stringArray[1]);
								float3 = Float.parseFloat(stringArray[2]);
								if (float1 < 0.19F) {
									float1 = 0.19F;
								}

								if (float2 < 0.19F) {
									float2 = 0.19F;
								}

								if (float3 < 0.19F) {
									float3 = 0.19F;
								}

								this.mpTextColor = new ColorInfo(float1, float2, float3, 1.0F);
							} else if (string.startsWith("objHighlightColor=")) {
								stringArray = string.replaceFirst("objHighlightColor=", "").split(",");
								float1 = Float.parseFloat(stringArray[0]);
								float2 = Float.parseFloat(stringArray[1]);
								float3 = Float.parseFloat(stringArray[2]);
								if (float1 < 0.19F) {
									float1 = 0.19F;
								}

								if (float2 < 0.19F) {
									float2 = 0.19F;
								}

								if (float3 < 0.19F) {
									float3 = 0.19F;
								}

								this.objectHighlitedColor = new ColorInfo(float1, float2, float3, 1.0F);
							} else if (string.startsWith("seenNews=")) {
								this.setSeenUpdateText(string.replaceFirst("seenNews=", ""));
							} else if (string.startsWith("toggleToRun=")) {
								this.setToggleToRun(Boolean.parseBoolean(string.replaceFirst("toggleToRun=", "")));
							} else if (string.startsWith("celsius=")) {
								this.setCelsius(Boolean.parseBoolean(string.replaceFirst("celsius=", "")));
							} else if (string.startsWith("mapOrder=")) {
								this.mapOrder = new LinkedList();
								if (this.version < 7) {
									string = "mapOrder=";
								}

								stringArray = string.replaceFirst("mapOrder=", "").split(";");
								String[] stringArray2 = stringArray;
								int1 = stringArray.length;
								for (int int5 = 0; int5 < int1; ++int5) {
									String string3 = stringArray2[int5];
									if (!string3.isEmpty()) {
										this.mapOrder.add(string3);
									}
								}
							} else if (string.startsWith("showFirstTimeWeatherTutorial=")) {
								this.setShowFirstTimeWeatherTutorial(Boolean.parseBoolean(string.replaceFirst("showFirstTimeWeatherTutorial=", "")));
							} else if (string.startsWith("uiRenderOffscreen=")) {
								OptionUIFBO = Boolean.parseBoolean(string.replaceFirst("uiRenderOffscreen=", ""));
							} else if (string.startsWith("uiRenderFPS=")) {
								OptionUIRenderFPS = Integer.parseInt(string.replaceFirst("uiRenderFPS=", ""));
							} else if (string.startsWith("radialMenuKeyToggle=")) {
								OptionRadialMenuKeyToggle = Boolean.parseBoolean(string.replaceFirst("radialMenuKeyToggle=", ""));
							} else if (string.startsWith("panCameraWhileAiming=")) {
								OptionPanCameraWhileAiming = Boolean.parseBoolean(string.replaceFirst("panCameraWhileAiming=", ""));
							} else if (string.startsWith("temperatureDisplayCelsius=")) {
								OptionTemperatureDisplayCelsius = Boolean.parseBoolean(string.replaceFirst("temperatureDisplayCelsius=", ""));
							} else if (string.startsWith("doWindSpriteEffects=")) {
								OptionDoWindSpriteEffects = Boolean.parseBoolean(string.replaceFirst("doWindSpriteEffects=", ""));
							} else if (string.startsWith("doDoorSpriteEffects=")) {
								OptionDoDoorSpriteEffects = Boolean.parseBoolean(string.replaceFirst("doDoorSpriteEffects=", ""));
							}
						}
					}
				}

				OnceEvery.FPS = PerformanceSettings.LockFPS;
				if (Translator.language == null) {
					Translator.setLanguage(Language.FromString(System.getProperty("user.language").toUpperCase()));
				}

				if (Translator.language == null) {
					Translator.setLanguage(Translator.getDefaultLanguage().index());
				}

				if (!this.doneNewSaveFolder) {
					File file4 = new File(GameWindow.getSaveDir());
					file4.mkdir();
					ArrayList arrayList = new ArrayList();
					arrayList.add("Beginner");
					arrayList.add("Survival");
					arrayList.add("A Really CD DA");
					arrayList.add("LastStand");
					arrayList.add("Opening Hours");
					arrayList.add("Sandbox");
					arrayList.add("Tutorial");
					arrayList.add("Winter is Coming");
					arrayList.add("You Have One Day");
					File file5 = null;
					File file6 = null;
					try {
						Iterator iterator = arrayList.iterator();
						while (iterator.hasNext()) {
							String string4 = (String)iterator.next();
							file5 = new File(GameWindow.getCacheDir() + File.separator + string4);
							file6 = new File(GameWindow.getSaveDir() + File.separator + string4);
							if (file5.exists()) {
								file6.mkdir();
								Files.move(file5.toPath(), file6.toPath(), StandardCopyOption.REPLACE_EXISTING);
							}
						}
					} catch (Exception exception2) {
					}

					this.doneNewSaveFolder = true;
				}
			} catch (Exception exception3) {
				exception3.printStackTrace();
			} finally {
				bufferedReader.close();
			}

			this.saveOptions();
			return true;
		}
	}

	private void copyPasteFolders(String string) {
		File file = (new File(string)).getAbsoluteFile();
		if (file.exists()) {
			this.searchFolders(file, string);
		}
	}

	private void searchFolders(File file, String string) {
		if (file.isDirectory()) {
			File file2 = new File(this.saveFolder + File.separator + string);
			file2.mkdir();
			String[] stringArray = file.list();
			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				this.searchFolders(new File(file.getAbsolutePath() + File.separator + stringArray[int1]), string + File.separator + stringArray[int1]);
			}
		} else {
			this.copyPasteFile(file, string);
		}
	}

	private void copyPasteFile(File file, String string) {
		FileOutputStream fileOutputStream = null;
		FileInputStream fileInputStream = null;
		try {
			File file2 = new File(this.saveFolder + File.separator + string);
			file2.createNewFile();
			fileOutputStream = new FileOutputStream(file2);
			fileInputStream = new FileInputStream(file);
			fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0L, file.length());
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}

				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public static String getMyDocumentFolder() {
		return GameWindow.getCacheDir();
	}

	public void saveOptions() throws IOException {
		File file = new File(GameWindow.getCacheDir() + File.separator + "options.ini");
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fileWriter = new FileWriter(file);
		try {
			fileWriter.write("version=" + this.fileversion + "\r\n");
			fileWriter.write("width=" + this.getScreenWidth() + "\r\n");
			fileWriter.write("height=" + this.getScreenHeight() + "\r\n");
			fileWriter.write("fullScreen=" + fullScreen + "\r\n");
			fileWriter.write("frameRate=" + PerformanceSettings.LockFPS + "\r\n");
			fileWriter.write("lighting=" + PerformanceSettings.LightingFrameSkip + "\r\n");
			fileWriter.write("lightFPS=" + PerformanceSettings.LightingFPS + "\r\n");
			fileWriter.write("newRoofHiding=" + PerformanceSettings.NewRoofHiding + "\r\n");
			fileWriter.write("bUseShaders=" + this.bUseShaders + "\r\n");
			fileWriter.write("bPerfSkybox=" + this.bPerfSkybox + "\r\n");
			fileWriter.write("bPerfReflections=" + this.bPerfReflections_new + "\r\n");
			fileWriter.write("vidMem=" + this.vidMem + "\r\n");
			fileWriter.write("bMultithreadedRendering=" + bMultithreadedRendering + "\r\n");
			fileWriter.write("language=" + Translator.getLanguage() + "\r\n");
			fileWriter.write("zoom=" + OptionZoom + "\r\n");
			fileWriter.write("contextMenuFont=" + OptionContextMenuFont + "\r\n");
			fileWriter.write("inventoryFont=" + OptionInventoryFont + "\r\n");
			fileWriter.write("tooltipFont=" + OptionTooltipFont + "\r\n");
			fileWriter.write("clockFormat=" + OptionClockFormat + "\r\n");
			fileWriter.write("clock24Hour=" + OptionClock24Hour + "\r\n");
			fileWriter.write("measurementsFormat=" + OptionMeasurementFormat + "\r\n");
			String string = "";
			for (int int1 = 0; int1 < 4; ++int1) {
				if (bAutoZoom[int1]) {
					if (!string.isEmpty()) {
						string = string + ",";
					}

					string = string + (int1 + 1);
				}
			}

			fileWriter.write("autozoom=" + string + "\r\n");
			fileWriter.write("vsync=" + OptionVSync + "\r\n");
			fileWriter.write("soundVolume=" + OptionSoundVolume + "\r\n");
			fileWriter.write("ambientVolume=" + OptionAmbientVolume + "\r\n");
			fileWriter.write("musicVolume=" + OptionMusicVolume + "\r\n");
			fileWriter.write("musicLibrary=" + OptionMusicLibrary + "\r\n");
			fileWriter.write("vehicleEngineVolume=" + OptionVehicleEngineVolume + "\r\n");
			fileWriter.write("voiceEnable=" + OptionVoiceEnable + "\r\n");
			fileWriter.write("voiceMode=" + OptionVoiceMode + "\r\n");
			fileWriter.write("voiceVADMode=" + OptionVoiceVADMode + "\r\n");
			fileWriter.write("voiceVolumeMic=" + OptionVoiceVolumeMic + "\r\n");
			fileWriter.write("voiceVolumePlayerse=" + OptionVoiceVolumePlayers + "\r\n");
			fileWriter.write("voiceRecordDeviceName=" + OptionVoiceRecordDeviceName + "\r\n");
			fileWriter.write("reloadDifficulty=" + OptionReloadDifficulty + "\r\n");
			fileWriter.write("rackProgress=" + OptionRackProgress + "\r\n");
			Iterator iterator = JoypadManager.instance.ActiveControllerNames.iterator();
			while (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				fileWriter.write("controller=" + string2 + "\r\n");
			}

			fileWriter.write("numberOf3D=" + PerformanceSettings.numberOf3D + "\r\n");
			fileWriter.write("modelsEnabled=" + PerformanceSettings.modelsEnabled + "\r\n");
			fileWriter.write("corpses3D=" + PerformanceSettings.corpses3D + "\r\n");
			fileWriter.write("tutorialDone=" + this.isTutorialDone() + "\r\n");
			fileWriter.write("vehiclesWarningShow=" + this.isVehiclesWarningShow() + "\r\n");
			fileWriter.write("bloodDecals=" + OptionBloodDecals + "\r\n");
			fileWriter.write("borderless=" + OptionBorderlessWindow + "\r\n");
			fileWriter.write("textureCompression=" + OptionTextureCompression + "\r\n");
			fileWriter.write("texture2x=" + OptionTexture2x + "\r\n");
			fileWriter.write("zoomLevels1x=" + OptionZoomLevels1x + "\r\n");
			fileWriter.write("zoomLevels2x=" + OptionZoomLevels2x + "\r\n");
			fileWriter.write("showChatTimestamp=" + OptionShowChatTimestamp + "\r\n");
			fileWriter.write("showChatTitle=" + OptionShowChatTitle + "\r\n");
			fileWriter.write("chatFontSize=" + OptionChatFontSize + "\r\n");
			fileWriter.write("minChatOpaque=" + OptionMinChatOpaque + "\r\n");
			fileWriter.write("maxChatOpaque=" + OptionMaxChatOpaque + "\r\n");
			fileWriter.write("chatFadeTime=" + OptionChatFadeTime + "\r\n");
			fileWriter.write("chatOpaqueOnFocus=" + OptionChatOpaqueOnFocus + "\r\n");
			fileWriter.write("doneNewSaveFolder=" + this.doneNewSaveFolder + "\r\n");
			fileWriter.write("contentTranslationsEnabled=" + OptionEnableContentTranslations + "\r\n");
			fileWriter.write("showYourUsername=" + this.showYourUsername + "\r\n");
			fileWriter.write("rosewoodSpawnDone=" + this.rosewoodSpawnDone + "\r\n");
			if (this.mpTextColor != null) {
				fileWriter.write("mpTextColor=" + this.mpTextColor.r + "," + this.mpTextColor.g + "," + this.mpTextColor.b + "\r\n");
			}

			fileWriter.write("objHighlightColor=" + this.objectHighlitedColor.r + "," + this.objectHighlitedColor.g + "," + this.objectHighlitedColor.b + "\r\n");
			fileWriter.write("seenNews=" + this.getSeenUpdateText() + "\r\n");
			fileWriter.write("toggleToRun=" + this.isToggleToRun() + "\r\n");
			fileWriter.write("celsius=" + this.isCelsius() + "\r\n");
			fileWriter.write("riversideDone=" + this.isRiversideDone() + "\r\n");
			fileWriter.write("mapOrder=");
			String string3 = "";
			if (this.mapOrder != null) {
				for (int int2 = 0; int2 < this.mapOrder.size(); ++int2) {
					string3 = string3 + (String)this.mapOrder.get(int2) + (int2 < this.mapOrder.size() - 1 ? ";" : "");
				}

				fileWriter.write(string3 + "\r\n");
			}

			fileWriter.write("showFirstTimeWeatherTutorial=" + this.isShowFirstTimeWeatherTutorial() + "\r\n");
			fileWriter.write("uiRenderOffscreen=" + OptionUIFBO + "\r\n");
			fileWriter.write("uiRenderFPS=" + OptionUIRenderFPS + "\r\n");
			fileWriter.write("radialMenuKeyToggle=" + OptionRadialMenuKeyToggle + "\r\n");
			fileWriter.write("panCameraWhileAiming=" + OptionPanCameraWhileAiming + "\r\n");
			fileWriter.write("temperatureDisplayCelsius=" + OptionTemperatureDisplayCelsius + "\r\n");
			fileWriter.write("doWindSpriteEffects=" + OptionDoWindSpriteEffects + "\r\n");
			fileWriter.write("doDoorSpriteEffects=" + OptionDoDoorSpriteEffects + "\r\n");
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			fileWriter.close();
		}

		OnceEvery.FPS = PerformanceSettings.LockFPS;
	}

	public void setWindowed(boolean boolean1) {
		RenderThread.borrowContext();
		if (boolean1 != fullScreen) {
			setDisplayMode(this.getScreenWidth(), this.getScreenHeight(), boolean1);
		}

		fullScreen = boolean1;
		if (fakefullscreen) {
			Display.setResizable(false);
		} else {
			Display.setResizable(!boolean1);
		}

		try {
			this.saveOptions();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		RenderThread.returnContext();
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public static void restartApplication(Runnable runnable) throws IOException {
		try {
			String string = System.getProperty("java.home") + "/bin/java";
			List list = ManagementFactory.getRuntimeMXBean().getInputArguments();
			StringBuffer stringBuffer = new StringBuffer();
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				if (!string2.contains("-agentlib")) {
					stringBuffer.append(string2);
					stringBuffer.append(" ");
				}
			}

			final StringBuffer stringBuffer2 = new StringBuffer(string + " " + stringBuffer);
			String[] stringArray = System.getProperty("sun.java.command").split(" ");
			if (stringArray[0].endsWith(".jar")) {
				stringBuffer2.append("-jar " + (new File(stringArray[0])).getPath());
			} else {
				stringBuffer2.append("-cp " + System.getProperty("java.class.path") + " " + stringArray[0]);
			}

			for (int int1 = 1; int1 < stringArray.length; ++int1) {
				stringBuffer2.append(" ");
				stringBuffer2.append(stringArray[int1]);
			}

			Runtime.getRuntime().addShutdownHook(new Thread(){
				
				public void run() {
					try {
						DebugLog.log("Relaunching: " + stringBuffer2.toString());
						Runtime.getRuntime().exec(stringBuffer2.toString());
					} catch (IOException list) {
						list.printStackTrace();
					}
				}
			});

			if (runnable != null) {
				runnable.run();
			}

			System.exit(0);
		} catch (Exception exception) {
			throw new IOException("Error while trying to restart the application", exception);
		}
	}

	public KahluaTable getScreenModes() {
		ArrayList arrayList = new ArrayList();
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		File file = new File(LuaManager.getLuaCacheDir() + File.separator + "screenresolution.ini");
		int int1 = 1;
		try {
			Integer integer;
			if (!file.exists()) {
				file.createNewFile();
				FileWriter fileWriter = new FileWriter(file);
				integer = 0;
				Integer integer2 = 0;
				DisplayMode[] displayModeArray = Display.getAvailableDisplayModes();
				for (int int2 = 0; int2 < displayModeArray.length; ++int2) {
					integer = displayModeArray[int2].getWidth();
					integer2 = displayModeArray[int2].getHeight();
					if (!arrayList.contains(integer + " x " + integer2)) {
						kahluaTable.rawset(int1, integer + " x " + integer2);
						fileWriter.write(integer + " x " + integer2 + " \r\n");
						arrayList.add(integer + " x " + integer2);
						++int1;
					}
				}

				fileWriter.close();
			} else {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String string;
				for (integer = null; (string = bufferedReader.readLine()) != null; ++int1) {
					kahluaTable.rawset(int1, string.trim());
				}

				bufferedReader.close();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return kahluaTable;
	}

	public static void setDisplayMode(int int1, int int2, boolean boolean1) {
		if (Display.getWidth() != int1 || Display.getHeight() != int2 || Display.isFullscreen() != boolean1) {
			RenderThread.borrowContext();
			fullScreen = boolean1;
			try {
				DisplayMode displayMode = null;
				if (!boolean1) {
					displayMode = new DisplayMode(int1, int2);
				} else {
					DisplayMode[] displayModeArray = Display.getAvailableDisplayModes();
					int int3 = 0;
					DisplayMode displayMode2 = null;
					for (int int4 = 0; int4 < displayModeArray.length; ++int4) {
						DisplayMode displayMode3 = displayModeArray[int4];
						if (displayMode3.getWidth() == int1 && displayMode3.getHeight() == int2 && displayMode3.isFullscreenCapable()) {
							if ((displayMode == null || displayMode3.getFrequency() >= int3) && (displayMode == null || displayMode3.getBitsPerPixel() > displayMode.getBitsPerPixel())) {
								displayMode = displayMode3;
								int3 = displayMode3.getFrequency();
							}

							if (displayMode3.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel() && displayMode3.getFrequency() == Display.getDesktopDisplayMode().getFrequency()) {
								displayMode = displayMode3;
								break;
							}
						}

						if (displayMode3.isFullscreenCapable() && (displayMode2 == null || Math.abs(displayMode3.getWidth() - int1) < Math.abs(displayMode2.getWidth() - int1) || displayMode3.getWidth() == displayMode2.getWidth() && displayMode3.getFrequency() > int3)) {
							displayMode2 = displayMode3;
							int3 = displayMode3.getFrequency();
							System.out.println("closest width=" + displayMode3.getWidth() + " freq=" + displayMode3.getFrequency());
						}
					}

					if (displayMode == null && displayMode2 != null) {
						displayMode = displayMode2;
					}
				}

				if (displayMode == null) {
					RenderThread.returnContext();
					DebugLog.log("Failed to find value mode: " + int1 + "x" + int2 + " fs=" + boolean1);
					return;
				}

				if (boolean1) {
					Display.setDisplayModeAndFullscreen(displayMode);
				} else {
					Display.setDisplayMode(displayMode);
					Display.setFullscreen(boolean1);
				}

				if (!boolean1 && OptionBorderlessWindow) {
					Display.setResizable(false);
				} else if (!boolean1 && !fakefullscreen) {
					Display.setResizable(false);
					Display.setResizable(true);
				}

				if (Display.isCreated()) {
					DebugLog.log("Display mode changed to " + Display.getWidth() + "x" + Display.getHeight() + " freq=" + Display.getDisplayMode().getFrequency() + " fullScreen=" + Display.isFullscreen());
				}
			} catch (LWJGLException lWJGLException) {
				DebugLog.log("Unable to setup mode " + int1 + "x" + int2 + " fullscreen=" + boolean1 + lWJGLException);
			}

			RenderThread.returnContext();
		}
	}

	public void updateKeyboard() {
		if (CurrentTextEntryBox == null) {
			while (true) {
				if (Keyboard.next()) {
					continue;
				}
			}
		} else if (CurrentTextEntryBox.IsEditable && CurrentTextEntryBox.DoingTextEntry) {
			while (true) {
				boolean boolean1;
				do {
					do {
						while (true) {
							do {
								if (!Keyboard.next()) {
									if (CurrentTextEntryBox != null && CurrentTextEntryBox.ignoreFirst) {
										CurrentTextEntryBox.ignoreFirst = false;
									}

									return;
								}
							}					 while (!Keyboard.getEventKeyState());

							int int1;
							if (Keyboard.getEventKey() == 28) {
								boolean1 = false;
								if (UIManager.getDebugConsole() != null && CurrentTextEntryBox == UIManager.getDebugConsole().CommandLine) {
									boolean1 = true;
								}

								if (CurrentTextEntryBox != null) {
									UITextBox2 uITextBox2 = CurrentTextEntryBox;
									if (uITextBox2.multipleLine) {
										if (uITextBox2.Lines.size() < uITextBox2.getMaxLines()) {
											if (uITextBox2.TextEntryCursorPos != uITextBox2.ToSelectionIndex) {
												int1 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
												int int2 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
												if (uITextBox2.internalText.length() > 0) {
													uITextBox2.internalText = uITextBox2.internalText.substring(0, int1) + "\n" + uITextBox2.internalText.substring(int2);
												} else {
													uITextBox2.internalText = "\n";
												}

												uITextBox2.TextEntryCursorPos = int1 + 1;
											} else {
												int1 = uITextBox2.TextEntryCursorPos;
												String string = uITextBox2.internalText.substring(0, int1) + "\n" + uITextBox2.internalText.substring(int1);
												uITextBox2.SetText(string);
												uITextBox2.TextEntryCursorPos = int1 + 1;
											}

											uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
											uITextBox2.CursorLine = uITextBox2.toDisplayLine(uITextBox2.TextEntryCursorPos);
										}
									} else {
										CurrentTextEntryBox.onCommandEntered();
									}
								}

								break;
							}

							if (Keyboard.getEventKey() == 1) {
								if (CurrentTextEntryBox != null) {
									CurrentTextEntryBox.onOtherKey(1);
									GameKeyboard.eatKeyPress(1);
								}
							} else if (Keyboard.getEventKey() == 15) {
								if (CurrentTextEntryBox != null) {
									CurrentTextEntryBox.onOtherKey(15);
								}

								LuaEventManager.triggerEvent("SwitchChatStream");
							} else if (Keyboard.getEventKey() != 58) {
								if (Keyboard.getEventKey() == 199) {
									CurrentTextEntryBox.TextEntryCursorPos = 0;
									if (!CurrentTextEntryBox.Lines.isEmpty()) {
										CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine);
									}

									if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
										CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
									}

									CurrentTextEntryBox.resetBlink();
								} else if (Keyboard.getEventKey() == 207) {
									CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.internalText.length();
									if (!CurrentTextEntryBox.Lines.isEmpty()) {
										CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine) + ((String)CurrentTextEntryBox.Lines.get(CurrentTextEntryBox.CursorLine)).length();
									}

									if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
										CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
									}

									CurrentTextEntryBox.resetBlink();
								} else {
									int int3;
									if (Keyboard.getEventKey() == 200) {
										if (CurrentTextEntryBox.CursorLine > 0) {
											int3 = CurrentTextEntryBox.TextEntryCursorPos - CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine);
											--CurrentTextEntryBox.CursorLine;
											if (int3 > ((String)CurrentTextEntryBox.Lines.get(CurrentTextEntryBox.CursorLine)).length()) {
												int3 = ((String)CurrentTextEntryBox.Lines.get(CurrentTextEntryBox.CursorLine)).length();
											}

											CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine) + int3;
											if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
												CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
											}
										}

										CurrentTextEntryBox.onPressUp();
									} else if (Keyboard.getEventKey() == 208) {
										if (CurrentTextEntryBox.Lines.size() - 1 > CurrentTextEntryBox.CursorLine && CurrentTextEntryBox.CursorLine + 1 < CurrentTextEntryBox.getMaxLines()) {
											int3 = CurrentTextEntryBox.TextEntryCursorPos - CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine);
											++CurrentTextEntryBox.CursorLine;
											if (int3 > ((String)CurrentTextEntryBox.Lines.get(CurrentTextEntryBox.CursorLine)).length()) {
												int3 = ((String)CurrentTextEntryBox.Lines.get(CurrentTextEntryBox.CursorLine)).length();
											}

											CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.TextOffsetOfLineStart.get(CurrentTextEntryBox.CursorLine) + int3;
											if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
												CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
											}
										}

										CurrentTextEntryBox.onPressDown();
									} else if (Keyboard.getEventKey() != 29 && Keyboard.getEventKey() != 157 && Keyboard.getEventKey() != 56 && Keyboard.getEventKey() != 184) {
										if (Keyboard.getEventKey() == 203) {
											--CurrentTextEntryBox.TextEntryCursorPos;
											if (CurrentTextEntryBox.TextEntryCursorPos < 0) {
												CurrentTextEntryBox.TextEntryCursorPos = 0;
											}

											if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
												CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
											}

											CurrentTextEntryBox.resetBlink();
										} else if (Keyboard.getEventKey() == 205) {
											++CurrentTextEntryBox.TextEntryCursorPos;
											if (CurrentTextEntryBox.TextEntryCursorPos > CurrentTextEntryBox.internalText.length()) {
												CurrentTextEntryBox.TextEntryCursorPos = CurrentTextEntryBox.internalText.length();
											}

											if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
												CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
											}

											CurrentTextEntryBox.resetBlink();
										} else if (CurrentTextEntryBox != null && CurrentTextEntryBox.DoingTextEntry) {
											int int4;
											if ((Keyboard.getEventKey() == 211 || Keyboard.getEventKey() == 14) && CurrentTextEntryBox.TextEntryCursorPos != CurrentTextEntryBox.ToSelectionIndex) {
												int3 = Math.min(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
												int4 = Math.max(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
												CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, int3) + CurrentTextEntryBox.internalText.substring(int4);
												CurrentTextEntryBox.CursorLine = CurrentTextEntryBox.toDisplayLine(int3);
												CurrentTextEntryBox.ToSelectionIndex = int3;
												CurrentTextEntryBox.TextEntryCursorPos = int3;
												CurrentTextEntryBox.onTextChange();
											} else if (Keyboard.getEventKey() == 211) {
												if (CurrentTextEntryBox.internalText.length() > 0 && CurrentTextEntryBox.TextEntryCursorPos < CurrentTextEntryBox.internalText.length()) {
													if (CurrentTextEntryBox.TextEntryCursorPos > 0) {
														CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, CurrentTextEntryBox.TextEntryCursorPos) + CurrentTextEntryBox.internalText.substring(CurrentTextEntryBox.TextEntryCursorPos + 1);
													} else {
														CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(1);
													}

													CurrentTextEntryBox.onTextChange();
												}
											} else if (Keyboard.getEventKey() == 14) {
												if (CurrentTextEntryBox.internalText.length() > 0 && CurrentTextEntryBox.TextEntryCursorPos > 0) {
													if (CurrentTextEntryBox.TextEntryCursorPos > CurrentTextEntryBox.internalText.length()) {
														CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, CurrentTextEntryBox.internalText.length() - 1);
													} else {
														int3 = CurrentTextEntryBox.TextEntryCursorPos;
														CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, int3 - 1) + CurrentTextEntryBox.internalText.substring(int3);
													}

													if (CurrentTextEntryBox.TextEntryCursorPos > 0) {
														--CurrentTextEntryBox.TextEntryCursorPos;
														CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.TextEntryCursorPos;
													}

													CurrentTextEntryBox.onTextChange();
												}
											} else if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.getEventKey() == 47) {
												String string2 = Clipboard.getClipboard();
												if (string2 != null) {
													if (CurrentTextEntryBox.TextEntryCursorPos != CurrentTextEntryBox.ToSelectionIndex) {
														int4 = Math.min(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
														int1 = Math.max(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
														CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, int4) + string2 + CurrentTextEntryBox.internalText.substring(int1);
														CurrentTextEntryBox.ToSelectionIndex = int4 + string2.length();
														CurrentTextEntryBox.TextEntryCursorPos = int4 + string2.length();
													} else {
														if (CurrentTextEntryBox.TextEntryCursorPos < CurrentTextEntryBox.internalText.length()) {
															CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, CurrentTextEntryBox.TextEntryCursorPos) + string2 + CurrentTextEntryBox.internalText.substring(CurrentTextEntryBox.TextEntryCursorPos);
														} else {
															CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText + string2;
														}

														UITextBox2 uITextBox22 = CurrentTextEntryBox;
														uITextBox22.TextEntryCursorPos += string2.length();
														uITextBox22 = CurrentTextEntryBox;
														uITextBox22.ToSelectionIndex += string2.length();
													}

													CurrentTextEntryBox.onTextChange();
												}
											} else {
												String string3;
												if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.getEventKey() == 46) {
													if (CurrentTextEntryBox.TextEntryCursorPos != CurrentTextEntryBox.ToSelectionIndex) {
														int3 = Math.min(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
														int4 = Math.max(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
														CurrentTextEntryBox.updateText();
														string3 = CurrentTextEntryBox.Text.substring(int3, int4);
														if (string3 != null && string3.length() > 0) {
															Clipboard.setClipboard(string3);
														}
													}
												} else if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.getEventKey() == 45) {
													if (CurrentTextEntryBox.TextEntryCursorPos != CurrentTextEntryBox.ToSelectionIndex) {
														int3 = Math.min(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
														int4 = Math.max(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
														CurrentTextEntryBox.updateText();
														string3 = CurrentTextEntryBox.Text.substring(int3, int4);
														if (string3 != null && string3.length() > 0) {
															Clipboard.setClipboard(string3);
														}

														CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, int3) + CurrentTextEntryBox.internalText.substring(int4);
														CurrentTextEntryBox.ToSelectionIndex = int3;
														CurrentTextEntryBox.TextEntryCursorPos = int3;
													}
												} else if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.getEventKey() == 30) {
													CurrentTextEntryBox.TextEntryCursorPos = 0;
													CurrentTextEntryBox.ToSelectionIndex = CurrentTextEntryBox.internalText.length();
												} else if (!CurrentTextEntryBox.ignoreFirst && Keyboard.getEventKey() != 42 && Keyboard.getEventKey() != 54 && CurrentTextEntryBox.internalText.length() <= CurrentTextEntryBox.TextEntryMaxLength) {
													if (CurrentTextEntryBox.isOnlyNumbers() && Keyboard.getEventCharacter() != '.' && Keyboard.getEventCharacter() != '-') {
														try {
															Double.parseDouble(String.valueOf(Keyboard.getEventCharacter()));
														} catch (Exception exception) {
															return;
														}
													}

													if (CurrentTextEntryBox.TextEntryCursorPos != CurrentTextEntryBox.ToSelectionIndex) {
														int3 = Math.min(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
														int4 = Math.max(CurrentTextEntryBox.TextEntryCursorPos, CurrentTextEntryBox.ToSelectionIndex);
														if (CurrentTextEntryBox.internalText.length() > 0) {
															CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, int3) + Keyboard.getEventCharacter() + CurrentTextEntryBox.internalText.substring(int4);
														} else {
															CurrentTextEntryBox.internalText = "" + Keyboard.getEventCharacter();
														}

														CurrentTextEntryBox.ToSelectionIndex = int3 + 1;
														CurrentTextEntryBox.TextEntryCursorPos = int3 + 1;
													} else {
														int3 = CurrentTextEntryBox.TextEntryCursorPos;
														if (int3 < CurrentTextEntryBox.internalText.length()) {
															CurrentTextEntryBox.internalText = CurrentTextEntryBox.internalText.substring(0, int3) + Keyboard.getEventCharacter() + CurrentTextEntryBox.internalText.substring(int3);
														} else {
															StringBuilder stringBuilder = new StringBuilder();
															UITextBox2 uITextBox23 = CurrentTextEntryBox;
															uITextBox23.internalText = stringBuilder.append(uITextBox23.internalText).append(Keyboard.getEventCharacter()).toString();
														}

														++CurrentTextEntryBox.TextEntryCursorPos;
														++CurrentTextEntryBox.ToSelectionIndex;
														CurrentTextEntryBox.onTextChange();
													}
												}
											}
										}
									}
								}
							}
						}
					}			 while (!boolean1);
				}		 while (GameClient.bClient && GameClient.accessLevel.equals("") && (GameClient.connection == null || !GameClient.connection.isCoopHost));

				UIManager.getDebugConsole().ProcessCommand();
			}
		}
	}

	public void quit() {
		if (IsoPlayer.instance != null) {
			bExiting = true;
		} else {
			try {
				this.saveOptions();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			GameClient.instance.Shutdown();
			SteamUtils.shutdown();
			System.exit(0);
		}
	}

	public void exitToMenu() {
		bExiting = true;
	}

	public void quitToDesktop() {
		GameWindow.closeRequested = true;
	}

	public boolean supportRes(int int1, int int2) throws LWJGLException {
		DisplayMode[] displayModeArray = Display.getAvailableDisplayModes();
		boolean boolean1 = false;
		for (int int3 = 0; int3 < displayModeArray.length; ++int3) {
			if (displayModeArray[int3].getWidth() == int1 && displayModeArray[int3].getHeight() == int2 && displayModeArray[int3].isFullscreenCapable()) {
				return true;
			}
		}

		return false;
	}

	public void init(int int1, int int2) throws LWJGLException {
		System.setProperty("org.lwjgl.opengl.Window.undecorated", OptionBorderlessWindow ? "true" : "false");
		Display.setVSyncEnabled(OptionVSync);
		if (!System.getProperty("os.name").contains("OS X") && !System.getProperty("os.name").startsWith("Win")) {
			DebugLog.log("Creating display. If this fails, you may need to install xrandr.");
		}

		setDisplayMode(int1, int2, fullScreen);
		try {
			Display.create(new PixelFormat(32, 0, 24, 8, 0));
		} catch (LWJGLException lWJGLException) {
			Display.destroy();
			Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
			Display.create(new PixelFormat(32, 0, 24, 8, 0));
		}

		fullScreen = Display.isFullscreen();
		DebugLog.log("OpenGL version: " + GL11.glGetString(7938));
		DebugLog.log("Desktop resolution " + Display.getDesktopDisplayMode().getWidth() + "x" + Display.getDesktopDisplayMode().getHeight());
		DebugLog.log("Initial resolution " + width + "x" + height + " fullScreen=" + fullScreen);
		GLVertexBufferObject.init();
		if (bIsSteam && GameWindow.OSValidator.isWindows()) {
		}

		Display.setVSyncEnabled(OptionVSync);
		GL11.glEnable(3553);
		IndieGL.glBlendFunc(770, 771);
		GL11.glTexEnvf(8960, 8704, 8448.0F);
		GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
	}

	private boolean setupMultiFBO() {
		try {
			if (!this.OffscreenBuffer.test()) {
				return false;
			} else {
				this.OffscreenBuffer.setZoomLevelsFromOption(TileScale == 2 ? OptionZoomLevels2x : OptionZoomLevels1x);
				this.OffscreenBuffer.create(Display.getWidth(), Display.getHeight());
				return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	public void init(int int1, int int2, int int3, int int4, Canvas canvas, Canvas canvas2) throws LWJGLException {
		width = int1;
		height = int2;
		if (int2 > 768 && this.supportsFBO() && this.OffscreenBuffer == null) {
			bDoubleSize = true;
		}

		canvas = canvas;
		fullscreencanvas = canvas2;
		Display.setVSyncEnabled(false);
		GL11.glEnable(3553);
		GL11.glTexEnvf(8960, 8704, 8448.0F);
		IndieGL.glBlendFunc(770, 771);
		GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
		this.sharedInit();
	}

	public void setScreenSize(int int1, int int2) {
		if (width != int1 || int2 != height) {
			int int3 = width;
			int int4 = height;
			DebugLog.log("Screen resolution changed from " + int3 + "x" + int4 + " to " + int1 + "x" + int2 + " fullScreen=" + fullScreen);
			width = int1;
			height = int2;
			if (this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null) {
				this.OffscreenBuffer.destroy();
				try {
					this.OffscreenBuffer.setZoomLevelsFromOption(TileScale == 2 ? OptionZoomLevels2x : OptionZoomLevels1x);
					this.OffscreenBuffer.create(int1, int2);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

			try {
				LuaEventManager.triggerEvent("OnResolutionChange", int3, int4, int1, int2);
			} catch (Exception exception2) {
				exception2.printStackTrace();
			}

			for (int int5 = 0; int5 < IsoPlayer.numPlayers; ++int5) {
				IsoPlayer player = IsoPlayer.players[int5];
				if (player != null) {
					player.dirtyRecalcGridStackTime = 2.0F;
				}
			}
		}
	}

	public void setForceScreenSize() {
	}

	public static boolean supportCompressedTextures() {
		return GLContext.getCapabilities().GL_EXT_texture_compression_latc;
	}

	public void refreshOffscreen() {
	}

	public void StartFrame() {
		if (LuaManager.thread == null || !LuaManager.thread.bStep) {
			if (this.RenderShader != null && this.OffscreenBuffer.Current != null) {
				this.RenderShader.setTexture(this.OffscreenBuffer.getTexture(0));
			}

			SpriteRenderer.instance.preRender();
			UIManager.resize();
			boolean boolean1 = false;
			TextureID.TextureIDStack.clear();
			Texture.BindCount = 0;
			if (!boolean1) {
				IndieGL.glClear(18176);
			}

			if (this.OffscreenBuffer.Current != null) {
				SpriteRenderer.instance.glBuffer(1, 0);
			}

			IndieGL.glDoStartFrame(this.getOffscreenWidth(0), this.getOffscreenHeight(0), 0);
			if (DoFiltering) {
			}

			this.frameStage = 1;
		}
	}

	public void StartFrame(int int1, boolean boolean1) {
		if (!LuaManager.thread.bStep) {
			this.OffscreenBuffer.update();
			if (this.RenderShader != null && this.OffscreenBuffer.Current != null) {
				this.RenderShader.setTexture(this.OffscreenBuffer.getTexture(int1));
			}

			if (boolean1) {
				SpriteRenderer.instance.preRender();
			}

			boolean boolean2 = false;
			TextureID.TextureIDStack.clear();
			Texture.BindCount = 0;
			IndieGL.glLoadIdentity();
			if (this.OffscreenBuffer.Current != null) {
				SpriteRenderer.instance.glBuffer(1, int1);
			}

			IndieGL.glDoStartFrame(this.getOffscreenWidth(int1), this.getOffscreenHeight(int1), int1);
			IndieGL.glClear(17664);
			this.frameStage = 1;
		}
	}

	public TextureFBO getOffscreenBuffer() {
		return this.OffscreenBuffer.getCurrent(0);
	}

	public TextureFBO getOffscreenBuffer(int int1) {
		return this.OffscreenBuffer.getCurrent(int1);
	}

	public void setLastRenderedFBO(TextureFBO textureFBO) {
		this.OffscreenBuffer.FBOrendered = textureFBO;
	}

	public void DoStartFrameStuff(int int1, int int2, int int3) {
		this.DoStartFrameStuff(int1, int2, int3, false);
	}

	public void DoStartFrameStuff(int int1, int int2, int int3, boolean boolean1) {
		GL11.glEnable(3042);
		GL11.glDepthFunc(519);
		while (this.stack > 0) {
			GL11.glPopMatrix();
			GL11.glPopAttrib();
			this.stack -= 2;
		}

		GL11.glAlphaFunc(516, 0.0F);
		GL11.glPushAttrib(2048);
		++this.stack;
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		++this.stack;
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0.0F, (float)int1, (float)int2, 0.0F);
		GL11.glMatrixMode(5888);
		if (int3 != -1) {
			int int4;
			int int5;
			if (boolean1) {
				int4 = int1;
				int5 = int2;
			} else {
				int4 = this.getOffscreenTrueWidth();
				int5 = this.getOffscreenTrueHeight();
				if (IsoPlayer.numPlayers > 1) {
					int4 /= 2;
				}

				if (IsoPlayer.numPlayers > 2) {
					int5 /= 2;
				}
			}

			float float1 = 0.0F;
			float float2 = (float)(int4 * (int3 % 2));
			if (int3 < 2 && IsoPlayer.numPlayers > 2) {
				float1 += (float)int5;
			}

			GL11.glViewport((int)float2, (int)float1, int1, int2);
			GL11.glEnable(3089);
			GL11.glScissor((int)float2, (int)float1, int1, int2);
		} else {
			GL11.glViewport(0, 0, int1, int2);
		}

		GL11.glLoadIdentity();
		SpriteRenderer.instance.states[2].playerIndex = int3;
	}

	public void DoEndFrameStuffFx(int int1, int int2, int int3) {
		GL11.glPopAttrib();
		--this.stack;
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		--this.stack;
		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
	}

	public void DoStartFrameStuffFx(int int1, int int2, int int3) {
		GL11.glEnable(3042);
		GL11.glDepthFunc(519);
		GL11.glAlphaFunc(516, 0.0F);
		GL11.glPushAttrib(2048);
		++this.stack;
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		++this.stack;
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0.0F, (float)int1, (float)int2, 0.0F);
		GL11.glMatrixMode(5888);
		if (int3 != -1) {
			int int4 = this.getOffscreenTrueWidth();
			int int5 = this.getOffscreenTrueHeight();
			if (IsoPlayer.numPlayers > 1) {
				int4 /= 2;
			}

			if (IsoPlayer.numPlayers > 2) {
				int5 /= 2;
			}

			float float1 = 0.0F;
			float float2 = (float)(int4 * (int3 % 2));
			if (int3 < 2 && IsoPlayer.numPlayers > 2) {
				float1 += (float)int5;
			}

			GL11.glViewport((int)float2, (int)float1, int1, int2);
			GL11.glEnable(3089);
			GL11.glScissor((int)float2, (int)float1, int1, int2);
		} else {
			GL11.glViewport(0, 0, int1, int2);
		}

		GL11.glLoadIdentity();
		SpriteRenderer.instance.states[2].playerIndex = int3;
	}

	public void PushIso() {
		SpriteRenderer.instance.PushIso();
	}

	public void PopIso() {
		SpriteRenderer.instance.PopIso();
	}

	public void DoPushIsoStuff() {
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		float float1 = 10.0F;
		float float2 = IsoCamera.CamCharacter.getX();
		float float3 = IsoCamera.CamCharacter.getY();
		float float4 = IsoCamera.CamCharacter.getZ();
		GL11.glOrtho(100.0, -100.0, -100.0, 100.0, -500.0, 500.0);
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glRotatef(25.264F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(225.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(80.0F, 0.0F, -80.0F);
		GL11.glScalef(110.0F, 110.0F, -110.0F);
	}

	public void DoPopIsoStuff() {
		GL11.glDepthFunc(519);
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
	}

	public void DoEndFrameStuff(int int1, int int2) {
		GL11.glPopAttrib();
		--this.stack;
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		--this.stack;
		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
		GL11.glDisable(3089);
	}

	public void RenderOffScreenBuffer() {
		if (LuaManager.thread == null || !LuaManager.thread.bStep) {
			if (this.OffscreenBuffer.Current != null) {
				IndieGL.disableStencilTest();
				IndieGL.glDoStartFrame(width, height, -1);
				IndieGL.glDisable(3042);
				this.OffscreenBuffer.render();
				IndieGL.glDoEndFrame();
			}
		}
	}

	public void StartFrameText(int int1) {
		if (LuaManager.thread == null || !LuaManager.thread.bStep) {
			IndieGL.glDoStartFrame(IsoCamera.getScreenWidth(int1), IsoCamera.getScreenHeight(int1), int1, true);
			IndieGL.glTexParameteri(3553, 10241, 9729);
			IndieGL.glTexParameteri(3553, 10240, 9728);
			this.frameStage = 2;
		}
	}

	public boolean StartFrameUI() {
		if (LuaManager.thread != null && LuaManager.thread.bStep) {
			return false;
		} else {
			boolean boolean1 = true;
			if (UIManager.useUIFBO) {
				--this.UIRenderTick;
				if (this.UIRenderTick <= 0) {
					SpriteRenderer.instance.glBuffer(2, 0);
				} else {
					boolean1 = false;
				}
			}

			IndieGL.glDoStartFrame(width, height, -1);
			IndieGL.glClear(1024);
			UIManager.resize();
			IndieGL.glTexParameteri(3553, 10241, 9729);
			IndieGL.glTexParameteri(3553, 10240, 9728);
			this.frameStage = 3;
			return boolean1;
		}
	}

	public void StartFrameUIOld() {
		if (LuaManager.thread == null || !LuaManager.thread.bStep) {
			if (this.OffscreenBuffer.Current != null) {
				IndieGL.disableStencilTest();
			}

			if (this.OffscreenBuffer.Current != null) {
				IndieGL.glClear(17664);
			}

			IndieGL.glTexParameteri(3553, 10241, 9729);
			IndieGL.glTexParameteri(3553, 10240, 9728);
			IndieGL.glDoStartFrame(width, height, -1);
			if (DoFiltering) {
			}

			UIManager.resize();
			this.OffscreenBuffer.render();
			IndieGL.glTexParameteri(3553, 10241, 9729);
			IndieGL.glTexParameteri(3553, 10240, 9728);
			this.frameStage = 3;
		}
	}

	public Map getKeyMaps() {
		return this.keyMaps;
	}

	public void setKeyMaps(Map map) {
		this.keyMaps = map;
	}

	public void reinitKeyMaps() {
		this.keyMaps = new HashMap();
	}

	public int getKey(String string) {
		if (this.keyMaps == null) {
			return 0;
		} else {
			return this.keyMaps.get(string) != null ? (Integer)this.keyMaps.get(string) : 0;
		}
	}

	public void addKeyBinding(String string, Integer integer) {
		if (this.keyMaps == null) {
			this.keyMaps = new HashMap();
		}

		this.keyMaps.put(string, integer);
	}

	public static boolean isLastStand() {
		return bLastStand;
	}

	public String getVersionNumber() {
		return this.versionNumber;
	}

	public String getSteamServerVersion() {
		return this.steamServerVersion;
	}

	public void DoFrameReady() {
		if (GameWindow.OSValidator.isMac()) {
			if (Mouse.isCreated()) {
				Mouse.poll();
				Mouse.updateCursor();
			}

			if (Keyboard.isCreated()) {
				Keyboard.poll();
			}

			if (Controllers.isCreated()) {
				Controllers.poll();
			}
		} else {
			Display.processMessages();
		}

		getInstance().updateKeyboard();
	}

	public float getZoom(int int1) {
		return this.OffscreenBuffer != null && this.OffscreenBuffer.Current != null ? this.OffscreenBuffer.zoom[int1] : 1.0F;
	}

	public float getNextZoom(int int1, int int2) {
		return this.OffscreenBuffer != null ? this.OffscreenBuffer.getNextZoom(int1, int2) : 1.0F;
	}

	public float getMinZoom() {
		return this.OffscreenBuffer != null ? this.OffscreenBuffer.getMinZoom() : 1.0F;
	}

	public float getMaxZoom() {
		return this.OffscreenBuffer != null ? this.OffscreenBuffer.getMaxZoom() : 1.0F;
	}

	public void doZoomScroll(int int1, int int2) {
		if (this.OffscreenBuffer != null) {
			this.OffscreenBuffer.doZoomScroll(int1, int2);
		}
	}

	public String getSaveFolder() {
		return this.saveFolder;
	}

	public void setSaveFolder(String string) {
		if (!this.saveFolder.equals(string)) {
			File file = new File(string);
			if (!file.exists()) {
				file.mkdir();
			}

			file = new File(string + File.separator + "mods");
			if (!file.exists()) {
				file.mkdir();
			}

			String string2 = this.saveFolder + File.separator;
			this.saveFolder = string;
			this.copyPasteFolders(string2 + "mods");
			this.deleteDirectoryRecusrively(string2);
		}
	}

	public void deleteDirectoryRecusrively(String string) {
		File file = new File(string);
		String[] stringArray = file.list();
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			File file2 = new File(string + File.separator + stringArray[int1]);
			if (file2.isDirectory()) {
				this.deleteDirectoryRecusrively(string + File.separator + stringArray[int1]);
			} else {
				file2.delete();
			}
		}

		file.delete();
	}

	public boolean getOptionZoom() {
		return OptionZoom;
	}

	public void setOptionZoom(boolean boolean1) {
		OptionZoom = boolean1;
	}

	public void zoomOptionChanged(boolean boolean1) {
		if (!boolean1) {
			SafeMode = SafeModeForced;
			this.OffscreenBuffer.bZoomEnabled = OptionZoom && !SafeModeForced;
		} else {
			RenderThread.borrowContext();
			if (OptionZoom && !SafeModeForced) {
				SafeMode = false;
				this.bSupportsFBO = true;
				this.OffscreenBuffer.bZoomEnabled = true;
				this.supportsFBO();
			} else {
				this.OffscreenBuffer.destroy();
				SafeMode = true;
				this.bSupportsFBO = false;
				this.OffscreenBuffer.bZoomEnabled = false;
			}

			RenderThread.returnContext();
			DebugLog.log("SafeMode is " + (SafeMode ? "on" : "off"));
		}
	}

	public void zoomLevelsChanged() {
		if (this.OffscreenBuffer.Current != null) {
			RenderThread.borrowContext();
			this.OffscreenBuffer.destroy();
			this.zoomOptionChanged(true);
			RenderThread.returnContext();
		}
	}

	public boolean isZoomEnabled() {
		return this.OffscreenBuffer.bZoomEnabled;
	}

	public void initFBOs() {
		if (OptionZoom && !SafeModeForced) {
			RenderThread.borrowContext();
			this.supportsFBO();
			RenderThread.returnContext();
		} else {
			SafeMode = true;
			this.OffscreenBuffer.bZoomEnabled = false;
		}

		DebugLog.log("SafeMode is " + (SafeMode ? "on" : "off"));
	}

	public boolean getAutoZoom(int int1) {
		return bAutoZoom[int1];
	}

	public void setAutoZoom(int int1, boolean boolean1) {
		bAutoZoom[int1] = boolean1;
		if (this.OffscreenBuffer != null) {
			this.OffscreenBuffer.bAutoZoom[int1] = boolean1;
		}
	}

	public boolean getOptionVSync() {
		return OptionVSync;
	}

	public void setOptionVSync(boolean boolean1) {
		OptionVSync = boolean1;
		RenderThread.borrowContext();
		try {
			Display.setVSyncEnabled(boolean1);
		} finally {
			RenderThread.returnContext();
		}
	}

	public int getOptionSoundVolume() {
		return OptionSoundVolume;
	}

	public float getRealOptionSoundVolume() {
		return (float)OptionSoundVolume / 10.0F;
	}

	public void setOptionSoundVolume(int int1) {
		OptionSoundVolume = Math.max(0, Math.min(10, int1));
		if (!GameClient.bClient || !GameSounds.soundIsPaused) {
			if (SoundManager.instance != null) {
				SoundManager.instance.setSoundVolume((float)int1 / 10.0F);
			}
		}
	}

	public int getOptionMusicVolume() {
		return OptionMusicVolume;
	}

	public void setOptionMusicVolume(int int1) {
		OptionMusicVolume = Math.max(0, Math.min(10, int1));
		if (!GameClient.bClient || !GameSounds.soundIsPaused) {
			if (SoundManager.instance != null) {
				SoundManager.instance.setMusicVolume((float)int1 / 10.0F);
			}
		}
	}

	public int getOptionAmbientVolume() {
		return OptionAmbientVolume;
	}

	public void setOptionAmbientVolume(int int1) {
		OptionAmbientVolume = Math.max(0, Math.min(10, int1));
		if (!GameClient.bClient || !GameSounds.soundIsPaused) {
			if (SoundManager.instance != null) {
				SoundManager.instance.setAmbientVolume((float)int1 / 10.0F);
			}
		}
	}

	public int getOptionMusicLibrary() {
		return OptionMusicLibrary;
	}

	public void setOptionMusicLibrary(int int1) {
		if (int1 < 1) {
			int1 = 1;
		}

		if (int1 > 3) {
			int1 = 3;
		}

		OptionMusicLibrary = int1;
	}

	public int getOptionVehicleEngineVolume() {
		return OptionVehicleEngineVolume;
	}

	public void setOptionVehicleEngineVolume(int int1) {
		OptionVehicleEngineVolume = Math.max(0, Math.min(10, int1));
		if (!GameClient.bClient || !GameSounds.soundIsPaused) {
			if (SoundManager.instance != null) {
				SoundManager.instance.setVehicleEngineVolume((float)OptionVehicleEngineVolume / 10.0F);
			}
		}
	}

	public boolean getOptionVoiceEnable() {
		return OptionVoiceEnable;
	}

	public void setOptionVoiceEnable(boolean boolean1) {
		OptionVoiceEnable = boolean1;
	}

	public int getOptionVoiceMode() {
		return OptionVoiceMode;
	}

	public void setOptionVoiceMode(int int1) {
		OptionVoiceMode = int1;
		VoiceManager.instance.setMode(int1);
	}

	public int getOptionVoiceVADMode() {
		return OptionVoiceVADMode;
	}

	public void setOptionVoiceVADMode(int int1) {
		OptionVoiceVADMode = int1;
		VoiceManager.instance.setVADMode(int1);
	}

	public int getOptionVoiceVolumeMic() {
		return OptionVoiceVolumeMic;
	}

	public void setOptionVoiceVolumeMic(int int1) {
		OptionVoiceVolumeMic = int1;
		VoiceManager.instance.setVolumeMic(int1);
	}

	public int getOptionVoiceVolumePlayers() {
		return OptionVoiceVolumePlayers;
	}

	public void setOptionVoiceVolumePlayers(int int1) {
		OptionVoiceVolumePlayers = int1;
		VoiceManager.instance.setVolumePlayers(int1);
	}

	public String getOptionVoiceRecordDeviceName() {
		return OptionVoiceRecordDeviceName;
	}

	public void setOptionVoiceRecordDeviceName(String string) {
		OptionVoiceRecordDeviceName = string;
		VoiceManager.instance.UpdateRecordDevice();
	}

	public int getOptionVoiceRecordDevice() {
		if (!SoundDisabled && !VoiceManager.VoipDisabled) {
			int int1 = javafmod.FMOD_System_GetRecordNumDrivers();
			for (int int2 = 0; int2 < int1; ++int2) {
				FMOD_DriverInfo fMOD_DriverInfo = new FMOD_DriverInfo();
				javafmod.FMOD_System_GetRecordDriverInfo(int2, fMOD_DriverInfo);
				if (fMOD_DriverInfo.name.equals(OptionVoiceRecordDeviceName)) {
					return int2 + 1;
				}
			}

			return 0;
		} else {
			return 0;
		}
	}

	public void setOptionVoiceRecordDevice(int int1) {
		if (!SoundDisabled && !VoiceManager.VoipDisabled) {
			if (int1 >= 1) {
				FMOD_DriverInfo fMOD_DriverInfo = new FMOD_DriverInfo();
				javafmod.FMOD_System_GetRecordDriverInfo(int1 - 1, fMOD_DriverInfo);
				OptionVoiceRecordDeviceName = fMOD_DriverInfo.name;
				VoiceManager.instance.UpdateRecordDevice();
			}
		}
	}

	public int getMicVolumeIndicator() {
		return VoiceManager.instance.getMicVolumeIndicator();
	}

	public boolean getMicVolumeError() {
		return VoiceManager.instance.getMicVolumeError();
	}

	public boolean getServerVOIPEnable() {
		return VoiceManager.instance.getServerVOIPEnable();
	}

	public void setTestingMicrophone(boolean boolean1) {
		VoiceManager.instance.setTestingMicrophone(boolean1);
	}

	public int getOptionReloadDifficulty() {
		return OptionReloadDifficulty;
	}

	public void setOptionReloadDifficulty(int int1) {
		OptionReloadDifficulty = Math.max(1, Math.min(3, int1));
	}

	public boolean getOptionRackProgress() {
		return OptionRackProgress;
	}

	public void setOptionRackProgress(boolean boolean1) {
		OptionRackProgress = boolean1;
	}

	public String getOptionContextMenuFont() {
		return OptionContextMenuFont;
	}

	public void setOptionContextMenuFont(String string) {
		OptionContextMenuFont = string;
	}

	public String getOptionInventoryFont() {
		return OptionInventoryFont;
	}

	public void setOptionInventoryFont(String string) {
		OptionInventoryFont = string;
	}

	public String getOptionTooltipFont() {
		return OptionTooltipFont;
	}

	public void setOptionTooltipFont(String string) {
		OptionTooltipFont = string;
		ObjectTooltip.checkFont();
	}

	public String getOptionMeasurementFormat() {
		return OptionMeasurementFormat;
	}

	public void setOptionMeasurementFormat(String string) {
		OptionMeasurementFormat = string;
	}

	public int getOptionClockFormat() {
		return OptionClockFormat;
	}

	public void setOptionClockFormat(int int1) {
		if (int1 < 1) {
			int1 = 1;
		}

		if (int1 > 2) {
			int1 = 2;
		}

		OptionClockFormat = int1;
	}

	public boolean getOptionClock24Hour() {
		return OptionClock24Hour;
	}

	public void setOptionClock24Hour(boolean boolean1) {
		OptionClock24Hour = boolean1;
	}

	public boolean getOptionModsEnabled() {
		return OptionModsEnabled;
	}

	public void setOptionModsEnabled(boolean boolean1) {
		OptionModsEnabled = boolean1;
	}

	public int getOptionBloodDecals() {
		return OptionBloodDecals;
	}

	public void setOptionBloodDecals(int int1) {
		if (int1 < 0) {
			int1 = 0;
		}

		if (int1 > 10) {
			int1 = 10;
		}

		OptionBloodDecals = int1;
	}

	public boolean getOptionBorderlessWindow() {
		return OptionBorderlessWindow;
	}

	public void setOptionBorderlessWindow(boolean boolean1) {
		OptionBorderlessWindow = boolean1;
	}

	public boolean getOptionTextureCompression() {
		return OptionTextureCompression;
	}

	public void setOptionTextureCompression(boolean boolean1) {
		OptionTextureCompression = boolean1;
	}

	public boolean getOptionTexture2x() {
		return OptionTexture2x;
	}

	public void setOptionTexture2x(boolean boolean1) {
		OptionTexture2x = boolean1;
	}

	public String getOptionZoomLevels1x() {
		return OptionZoomLevels1x;
	}

	public void setOptionZoomLevels1x(String string) {
		OptionZoomLevels1x = string == null ? "" : string;
	}

	public String getOptionZoomLevels2x() {
		return OptionZoomLevels2x;
	}

	public void setOptionZoomLevels2x(String string) {
		OptionZoomLevels2x = string == null ? "" : string;
	}

	public ArrayList getDefaultZoomLevels() {
		return this.OffscreenBuffer.getDefaultZoomLevels();
	}

	public void setOptionActiveController(int int1, boolean boolean1) {
		if (int1 >= 0 && int1 < GameWindow.GameInput.getControllerCount()) {
			Controller controller = GameWindow.GameInput.getController(int1);
			if (controller != null) {
				JoypadManager.instance.setControllerActive(controller.getName(), boolean1);
			}
		}
	}

	public boolean getOptionActiveController(String string) {
		return JoypadManager.instance.ActiveControllerNames.contains(string);
	}

	public boolean isOptionShowChatTimestamp() {
		return OptionShowChatTimestamp;
	}

	public void setOptionShowChatTimestamp(boolean boolean1) {
		OptionShowChatTimestamp = boolean1;
	}

	public boolean isOptionShowChatTitle() {
		return OptionShowChatTitle;
	}

	public String getOptionChatFontSize() {
		return OptionChatFontSize;
	}

	public void setOptionChatFontSize(String string) {
		OptionChatFontSize = string;
	}

	public void setOptionShowChatTitle(boolean boolean1) {
		OptionShowChatTitle = boolean1;
	}

	public float getOptionMinChatOpaque() {
		return OptionMinChatOpaque;
	}

	public void setOptionMinChatOpaque(float float1) {
		OptionMinChatOpaque = float1;
	}

	public float getOptionMaxChatOpaque() {
		return OptionMaxChatOpaque;
	}

	public void setOptionMaxChatOpaque(float float1) {
		OptionMaxChatOpaque = float1;
	}

	public float getOptionChatFadeTime() {
		return OptionChatFadeTime;
	}

	public void setOptionChatFadeTime(float float1) {
		OptionChatFadeTime = float1;
	}

	public boolean getOptionChatOpaqueOnFocus() {
		return OptionChatOpaqueOnFocus;
	}

	public void setOptionChatOpaqueOnFocus(boolean boolean1) {
		OptionChatOpaqueOnFocus = boolean1;
	}

	public boolean getOptionUIFBO() {
		return OptionUIFBO;
	}

	public void setOptionUIFBO(boolean boolean1) {
		OptionUIFBO = boolean1;
		if (GameWindow.states.current == IngameState.instance) {
			UIManager.useUIFBO = getInstance().supportsFBO() && OptionUIFBO;
		}
	}

	public int getOptionUIRenderFPS() {
		return OptionUIRenderFPS;
	}

	public void setOptionUIRenderFPS(int int1) {
		OptionUIRenderFPS = int1;
	}

	public void setOptionRadialMenuKeyToggle(boolean boolean1) {
		OptionRadialMenuKeyToggle = boolean1;
	}

	public boolean getOptionRadialMenuKeyToggle() {
		return OptionRadialMenuKeyToggle;
	}

	public void setOptionPanCameraWhileAiming(boolean boolean1) {
		OptionPanCameraWhileAiming = boolean1;
	}

	public boolean getOptionPanCameraWhileAiming() {
		return OptionPanCameraWhileAiming;
	}

	public void ResetLua(boolean boolean1, String string) {
		if (SpriteRenderer.instance != null) {
			GameWindow.DrawReloadingLua = true;
			GameWindow.render();
			GameWindow.DrawReloadingLua = false;
		}

		ScriptManager.instance.Reset();
		GameSounds.Reset();
		LuaEventManager.Reset();
		MapObjects.Reset();
		UIManager.init();
		SurvivorFactory.Reset();
		ProfessionFactory.Reset();
		TraitFactory.Reset();
		ChooseGameInfo.Reset();
		LuaHookManager.Reset();
		LuaManager.init();
		JoypadManager.instance.Reset();
		GameKeyboard.doLuaKeyPressed = true;
		Texture.nullTextures.clear();
		ZomboidFileSystem.instance.Reset();
		ZomboidFileSystem.instance.init();
		ZomboidFileSystem.instance.loadMods();
		Translator.loadFiles();
		ScriptManager.instance.Load();
		try {
			LuaManager.LoadDirBase();
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
			GameWindow.DoLoadingText("Reloading Lua - ERRORS!");
			try {
				Thread.sleep(2000L);
			} catch (InterruptedException interruptedException) {
			}
		}

		ZomboidGlobals.Load();
		LuaEventManager.triggerEvent("OnGameBoot");
		LuaEventManager.triggerEvent("OnMainMenuEnter");
		LuaEventManager.triggerEvent("OnResetLua", string);
	}

	public boolean isShowPing() {
		return this.showPing;
	}

	public void setShowPing(boolean boolean1) {
		this.showPing = boolean1;
	}

	public boolean isForceSnow() {
		return this.forceSnow;
	}

	public void setForceSnow(boolean boolean1) {
		this.forceSnow = boolean1;
	}

	public boolean isZombieGroupSound() {
		return this.zombieGroupSound;
	}

	public void setZombieGroupSound(boolean boolean1) {
		this.zombieGroupSound = boolean1;
	}

	public String getBlinkingMoodle() {
		return this.blinkingMoodle;
	}

	public void setBlinkingMoodle(String string) {
		this.blinkingMoodle = string;
	}

	public boolean isTutorialDone() {
		return this.tutorialDone;
	}

	public void setTutorialDone(boolean boolean1) {
		this.tutorialDone = boolean1;
	}

	public boolean isVehiclesWarningShow() {
		return this.vehiclesWarningShow;
	}

	public void setVehiclesWarningShow(boolean boolean1) {
		this.vehiclesWarningShow = boolean1;
	}

	public void initPoisonousBerry() {
		ArrayList arrayList = new ArrayList();
		arrayList.add("Base.BerryGeneric1");
		arrayList.add("Base.BerryGeneric2");
		arrayList.add("Base.BerryGeneric3");
		arrayList.add("Base.BerryGeneric4");
		arrayList.add("Base.BerryGeneric5");
		arrayList.add("Base.BerryPoisonIvy");
		this.setPoisonousBerry((String)arrayList.get(Rand.Next(0, arrayList.size() - 1)));
	}

	public void initPoisonousMushroom() {
		ArrayList arrayList = new ArrayList();
		arrayList.add("Base.MushroomGeneric1");
		arrayList.add("Base.MushroomGeneric2");
		arrayList.add("Base.MushroomGeneric3");
		arrayList.add("Base.MushroomGeneric4");
		arrayList.add("Base.MushroomGeneric5");
		arrayList.add("Base.MushroomGeneric6");
		arrayList.add("Base.MushroomGeneric7");
		this.setPoisonousMushroom((String)arrayList.get(Rand.Next(0, arrayList.size() - 1)));
	}

	public String getPoisonousBerry() {
		return this.poisonousBerry;
	}

	public void setPoisonousBerry(String string) {
		this.poisonousBerry = string;
	}

	public String getPoisonousMushroom() {
		return this.poisonousMushroom;
	}

	public void setPoisonousMushroom(String string) {
		this.poisonousMushroom = string;
	}

	public static String getDifficulty() {
		return difficulty;
	}

	public static void setDifficulty(String string) {
		difficulty = string;
	}

	public boolean isDoneNewSaveFolder() {
		return this.doneNewSaveFolder;
	}

	public void setDoneNewSaveFolder(boolean boolean1) {
		this.doneNewSaveFolder = boolean1;
	}

	public static int getTileScale() {
		return TileScale;
	}

	public boolean isSelectingAll() {
		return this.isSelectingAll;
	}

	public void setIsSelectingAll(boolean boolean1) {
		this.isSelectingAll = boolean1;
	}

	public boolean getContentTranslationsEnabled() {
		return OptionEnableContentTranslations;
	}

	public void setContentTranslationsEnabled(boolean boolean1) {
		OptionEnableContentTranslations = boolean1;
	}

	public boolean isShowYourUsername() {
		return this.showYourUsername;
	}

	public void setShowYourUsername(boolean boolean1) {
		this.showYourUsername = boolean1;
	}

	public ColorInfo getMpTextColor() {
		if (this.mpTextColor == null) {
			this.mpTextColor = new ColorInfo((float)(Rand.Next(135) + 120) / 255.0F, (float)(Rand.Next(135) + 120) / 255.0F, (float)(Rand.Next(135) + 120) / 255.0F, 1.0F);
		}

		return this.mpTextColor;
	}

	public void setMpTextColor(ColorInfo colorInfo) {
		if (colorInfo.r < 0.19F) {
			colorInfo.r = 0.19F;
		}

		if (colorInfo.g < 0.19F) {
			colorInfo.g = 0.19F;
		}

		if (colorInfo.b < 0.19F) {
			colorInfo.b = 0.19F;
		}

		this.mpTextColor = colorInfo;
	}

	public boolean isAzerty() {
		return this.isAzerty;
	}

	public void setAzerty(boolean boolean1) {
		this.isAzerty = boolean1;
	}

	public ColorInfo getObjectHighlitedColor() {
		return this.objectHighlitedColor;
	}

	public void setObjectHighlitedColor(ColorInfo colorInfo) {
		this.objectHighlitedColor = colorInfo;
	}

	public String getSeenUpdateText() {
		return this.seenUpdateText;
	}

	public void setSeenUpdateText(String string) {
		this.seenUpdateText = string;
	}

	public boolean isToggleToRun() {
		return this.toggleToRun;
	}

	public void setToggleToRun(boolean boolean1) {
		this.toggleToRun = boolean1;
	}

	public int getXAngle(int int1, float float1) {
		double double1 = Math.toRadians((double)(225.0F + float1));
		int int2 = (new Long(Math.round((Math.sqrt(2.0) * Math.cos(double1) + 1.0) * (double)(int1 / 2)))).intValue();
		return int2;
	}

	public int getYAngle(int int1, float float1) {
		double double1 = Math.toRadians((double)(225.0F + float1));
		int int2 = (new Long(Math.round((Math.sqrt(2.0) * Math.sin(double1) + 1.0) * (double)(int1 / 2)))).intValue();
		return int2;
	}

	public boolean isCelsius() {
		return this.celsius;
	}

	public void setCelsius(boolean boolean1) {
		this.celsius = boolean1;
	}

	public boolean isInDebug() {
		return bDebug;
	}

	public boolean doWarnMapConflict() {
		return this.warnMapConflict;
	}

	public void setWarnMapConflict(boolean boolean1) {
		this.warnMapConflict = boolean1;
	}

	public void setMapOrder(LinkedList linkedList) {
		this.mapOrder = linkedList;
	}

	public LinkedList getMapOrder() {
		return this.mapOrder;
	}

	public boolean isRiversideDone() {
		return this.riversideDone;
	}

	public void setRiversideDone(boolean boolean1) {
		this.riversideDone = boolean1;
	}

	public boolean isNoSave() {
		return this.noSave;
	}

	public void setNoSave(boolean boolean1) {
		this.noSave = boolean1;
	}

	public boolean isShowFirstTimeVehicleTutorial() {
		return this.showFirstTimeVehicleTutorial;
	}

	public void setShowFirstTimeVehicleTutorial(boolean boolean1) {
		this.showFirstTimeVehicleTutorial = boolean1;
	}

	public boolean getOptionDisplayAsCelsius() {
		return OptionTemperatureDisplayCelsius;
	}

	public void setOptionDisplayAsCelsius(boolean boolean1) {
		OptionTemperatureDisplayCelsius = boolean1;
	}

	public boolean isShowFirstTimeWeatherTutorial() {
		return this.showFirstTimeWeatherTutorial;
	}

	public void setShowFirstTimeWeatherTutorial(boolean boolean1) {
		this.showFirstTimeWeatherTutorial = boolean1;
	}

	public boolean getOptionDoWindSpriteEffects() {
		return OptionDoWindSpriteEffects;
	}

	public void setOptionDoWindSpriteEffects(boolean boolean1) {
		OptionDoWindSpriteEffects = boolean1;
	}

	public boolean getOptionDoDoorSpriteEffects() {
		return OptionDoDoorSpriteEffects;
	}

	public void setOptionDoDoorSpriteEffects(boolean boolean1) {
		OptionDoDoorSpriteEffects = boolean1;
	}
}
