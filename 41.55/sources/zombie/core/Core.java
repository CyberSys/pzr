package zombie.core;

import fmod.FMOD_DriverInfo;
import fmod.javafmod;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.glu.GLU;
import org.lwjglx.LWJGLException;
import org.lwjglx.input.Controller;
import org.lwjglx.input.Keyboard;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.DisplayMode;
import org.lwjglx.opengl.OpenGLException;
import org.lwjglx.opengl.PixelFormat;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.MovingObjectUpdateScheduler;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorFactory;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.CustomPerks;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.VBO.GLVertexBufferObject;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.raknet.VoiceManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.sprite.SpriteRenderState;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.MultiTextureFBO2;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureFBO;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.IngameState;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.iso.BentFences;
import zombie.iso.BrokenFences;
import zombie.iso.ContainerOverlays;
import zombie.iso.IsoCamera;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoWater;
import zombie.iso.PlayerCamera;
import zombie.iso.TileOverlays;
import zombie.iso.weather.WeatherShader;
import zombie.modding.ActiveMods;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.sandbox.CustomSandboxOptions;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.FPSGraph;
import zombie.ui.ObjectTooltip;
import zombie.ui.TextManager;
import zombie.ui.UIManager;
import zombie.ui.UITextBox2;
import zombie.util.StringUtils;
import zombie.vehicles.VehicleType;


public final class Core {
	public static final boolean bDemo = false;
	public static boolean bTutorial;
	private static boolean fakefullscreen = false;
	private static final GameVersion gameVersion = new GameVersion(41, 55, " - IWBUMS");
	public String steamServerVersion = "1.0.0.0";
	public static boolean bMultithreadedRendering = true;
	public static boolean bAltMoveMethod = false;
	private boolean rosewoodSpawnDone = false;
	private final ColorInfo objectHighlitedColor = new ColorInfo(0.98F, 0.56F, 0.11F, 1.0F);
	private boolean flashIsoCursor = false;
	private int isoCursorVisibility = 5;
	public static boolean OptionShowCursorWhileAiming = false;
	private boolean collideZombies = true;
	public final MultiTextureFBO2 OffscreenBuffer = new MultiTextureFBO2();
	private String saveFolder = null;
	public static boolean OptionZoom = true;
	public static boolean OptionModsEnabled = true;
	public static int OptionFontSize = 1;
	public static String OptionContextMenuFont = "Medium";
	public static String OptionInventoryFont = "Medium";
	public static String OptionTooltipFont = "Small";
	public static String OptionMeasurementFormat = "Metric";
	public static int OptionClockFormat = 1;
	public static int OptionClockSize = 2;
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
	public static int OptionReloadDifficulty = 2;
	public static boolean OptionRackProgress = true;
	public static int OptionBloodDecals = 10;
	public static boolean OptionBorderlessWindow = false;
	public static boolean OptionLockCursorToWindow = false;
	public static boolean OptionTextureCompression = false;
	public static boolean OptionModelTextureMipmaps = false;
	public static boolean OptionTexture2x = true;
	private static String OptionZoomLevels1x = "";
	private static String OptionZoomLevels2x = "";
	public static boolean OptionEnableContentTranslations = true;
	public static boolean OptionUIFBO = true;
	public static int OptionUIRenderFPS = 20;
	public static boolean OptionRadialMenuKeyToggle = true;
	public static boolean OptionReloadRadialInstant = false;
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
	public static boolean OptionRenderPrecipIndoors = true;
	public static boolean OptionAutoProneAtk = true;
	public static boolean Option3DGroundItem = true;
	public static int OptionRenderPrecipitation = 1;
	public static boolean OptionUpdateSneakButton = true;
	public static boolean OptiondblTapJogToSprint = false;
	private static int OptionAimOutline = 1;
	private static String OptionCycleContainerKey = "shift";
	private static boolean OptionDropItemsOnSquareCenter = false;
	private static boolean OptionTimedActionGameSpeedReset = false;
	private static int OptionShoulderButtonContainerSwitch = 1;
	private static boolean OptionProgressBar = false;
	private static String OptionLanguageName = null;
	private static final boolean[] OptionSingleContextMenu = new boolean[4];
	private static boolean OptionCorpseShadows = true;
	private static int OptionSimpleClothingTextures = 1;
	private static boolean OptionSimpleWeaponTextures = false;
	private static boolean OptionAutoDrink = true;
	private static boolean OptionLeaveKeyInIgnition = false;
	private static int OptionIgnoreProneZombieRange = 2;
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
	private boolean toggleToAim = false;
	private boolean toggleToRun = false;
	private boolean toggleToSprint = true;
	private boolean celsius = false;
	private boolean riversideDone = false;
	private boolean noSave = false;
	private boolean showFirstTimeVehicleTutorial = false;
	private boolean showFirstTimeWeatherTutorial = false;
	private boolean showFirstTimeSneakTutorial = true;
	private boolean newReloading = true;
	private boolean gotNewBelt = false;
	private boolean bAnimPopupDone = false;
	private boolean bModsPopupDone = false;
	public static float blinkAlpha = 1.0F;
	public static boolean blinkAlphaIncrease = false;
	private static HashMap optionsOnStartup = new HashMap();
	private boolean bChallenge;
	public static int width = 1280;
	public static int height = 720;
	public static int MaxJukeBoxesActive = 10;
	public static int NumJukeBoxesActive = 0;
	public static String GameMode = "Sandbox";
	private static String glVersion;
	private static int glMajorVersion = -1;
	private static Core core = new Core();
	public static boolean bDebug = false;
	public static UITextBox2 CurrentTextEntryBox = null;
	public Shader RenderShader;
	private Map keyMaps = null;
	public final boolean bUseShaders = true;
	private int iPerfSkybox = 1;
	private int iPerfSkybox_new = 1;
	public static final int iPerfSkybox_High = 0;
	public static final int iPerfSkybox_Medium = 1;
	public static final int iPerfSkybox_Static = 2;
	private int iPerfPuddles = 0;
	private int iPerfPuddles_new = 0;
	public static final int iPerfPuddles_None = 3;
	public static final int iPerfPuddles_GroundOnly = 2;
	public static final int iPerfPuddles_GroundWithRuts = 1;
	public static final int iPerfPuddles_All = 0;
	private boolean bPerfReflections = true;
	private boolean bPerfReflections_new = true;
	public int vidMem = 3;
	private boolean bSupportsFBO = true;
	public float UIRenderAccumulator = 0.0F;
	public boolean UIRenderThisFrame = true;
	public int version = 1;
	public int fileversion = 7;
	private static boolean fullScreen = false;
	private static final boolean[] bAutoZoom = new boolean[4];
	public static String GameMap = "DEFAULT";
	public static String GameSaveWorld = "";
	public static boolean SafeMode = false;
	public static boolean SafeModeForced = false;
	public static boolean SoundDisabled = false;
	public int frameStage = 0;
	private int stack = 0;
	public static int xx = 0;
	public static int yy = 0;
	public static int zz = 0;
	public final HashMap FloatParamMap = new HashMap();
	private final Matrix4f tempMatrix4f = new Matrix4f();
	private static final float isoAngle = 62.65607F;
	private static final float scale = 0.047085002F;
	public static boolean bLastStand = false;
	public static String ChallengeID = null;
	public static boolean bLoadedWithMultithreaded = false;
	public static boolean bExiting = false;
	private String m_delayResetLua_activeMods = null;
	private String m_delayResetLua_reason = null;

	public boolean isMultiThread() {
		return bMultithreadedRendering;
	}

	public void setChallenge(boolean boolean1) {
		this.bChallenge = boolean1;
	}

	public boolean isChallenge() {
		return this.bChallenge;
	}

	public String getChallengeID() {
		return ChallengeID;
	}

	public boolean getOptionTieredZombieUpdates() {
		return MovingObjectUpdateScheduler.instance.isEnabled();
	}

	public void setOptionTieredZombieUpdates(boolean boolean1) {
		MovingObjectUpdateScheduler.instance.setEnabled(boolean1);
	}

	public void setFramerate(int int1) {
		PerformanceSettings.setUncappedFPS(int1 == 1);
		switch (int1) {
		case 1: 
			PerformanceSettings.setLockFPS(60);
			break;
		
		case 2: 
			PerformanceSettings.setLockFPS(244);
			break;
		
		case 3: 
			PerformanceSettings.setLockFPS(240);
			break;
		
		case 4: 
			PerformanceSettings.setLockFPS(165);
			break;
		
		case 5: 
			PerformanceSettings.setLockFPS(120);
			break;
		
		case 6: 
			PerformanceSettings.setLockFPS(95);
			break;
		
		case 7: 
			PerformanceSettings.setLockFPS(90);
			break;
		
		case 8: 
			PerformanceSettings.setLockFPS(75);
			break;
		
		case 9: 
			PerformanceSettings.setLockFPS(60);
			break;
		
		case 10: 
			PerformanceSettings.setLockFPS(55);
			break;
		
		case 11: 
			PerformanceSettings.setLockFPS(45);
			break;
		
		case 12: 
			PerformanceSettings.setLockFPS(30);
			break;
		
		case 13: 
			PerformanceSettings.setLockFPS(24);
		
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

	public boolean getUseShaders() {
		return true;
	}

	public int getPerfSkybox() {
		return this.iPerfSkybox_new;
	}

	public int getPerfSkyboxOnLoad() {
		return this.iPerfSkybox;
	}

	public void setPerfSkybox(int int1) {
		this.iPerfSkybox_new = int1;
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

	public int getPerfPuddles() {
		return this.iPerfPuddles_new;
	}

	public int getPerfPuddlesOnLoad() {
		return this.iPerfPuddles;
	}

	public void setPerfPuddles(int int1) {
		this.iPerfPuddles_new = int1;
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
	}

	public void shadersOptionChanged() {
		RenderThread.invokeOnRenderContext(()->{
			if (!SafeModeForced) {
				try {
					if (this.RenderShader == null) {
						this.RenderShader = new WeatherShader("screen");
					}

					if (this.RenderShader != null && !this.RenderShader.isCompiled()) {
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
		});
	}

	public void initShaders() {
		try {
			if (this.RenderShader == null && !SafeMode && !SafeModeForced) {
				RenderThread.invokeOnRenderContext(()->{
					this.RenderShader = new WeatherShader("screen");
				});
			}

			if (this.RenderShader == null || !this.RenderShader.isCompiled()) {
				this.RenderShader = null;
			}
		} catch (Exception exception) {
			this.RenderShader = null;
			exception.printStackTrace();
		}

		IsoPuddles.getInstance();
		IsoWater.getInstance();
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

	public static void setFullScreen(boolean boolean1) {
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
		this.TakeScreenshot(256, 256, 1028);
	}

	public void TakeScreenshot(int int1, int int2, int int3) {
		byte byte1 = 0;
		int int4 = IsoCamera.getScreenWidth(byte1);
		int int5 = IsoCamera.getScreenHeight(byte1);
		int1 = PZMath.min(int1, int4);
		int2 = PZMath.min(int2, int5);
		int int6 = IsoCamera.getScreenLeft(byte1) + int4 / 2 - int1 / 2;
		int int7 = IsoCamera.getScreenTop(byte1) + int5 / 2 - int2 / 2;
		this.TakeScreenshot(int6, int7, int1, int2, int3);
	}

	public void TakeScreenshot(int int1, int int2, int int3, int int4, int int5) {
		GL11.glPixelStorei(3333, 1);
		GL11.glReadBuffer(int5);
		byte byte1 = 3;
		ByteBuffer byteBuffer = MemoryUtil.memAlloc(int3 * int4 * byte1);
		GL11.glReadPixels(int1, int2, int3, int4, 6407, 5121, byteBuffer);
		int[] intArray = new int[int3 * int4];
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("thumb.png");
		String string = "png";
		for (int int6 = 0; int6 < intArray.length; ++int6) {
			int int7 = int6 * 3;
			intArray[int6] = -16777216 | (byteBuffer.get(int7) & 255) << 16 | (byteBuffer.get(int7 + 1) & 255) << 8 | (byteBuffer.get(int7 + 2) & 255) << 0;
		}

		MemoryUtil.memFree(byteBuffer);
		intArray = flipPixels(intArray, int3, int4);
		BufferedImage bufferedImage = new BufferedImage(int3, int4, 2);
		bufferedImage.setRGB(0, 0, int3, int4, intArray, 0, int3);
		try {
			ImageIO.write(bufferedImage, "png", file);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		String string2 = ZomboidFileSystem.instance.getGameModeCacheDir();
		Texture.reload(string2 + GameSaveWorld + File.separator + "thumb.png");
	}

	public void TakeFullScreenshot(String string) {
		RenderThread.invokeOnRenderContext(string, (var0)->{
			GL11.glPixelStorei(3333, 1);
			GL11.glReadBuffer(1028);
			int string = Display.getDisplayMode().getWidth();
			int int1 = Display.getDisplayMode().getHeight();
			byte byte1 = 0;
			byte byte2 = 0;
			byte byte3 = 3;
			ByteBuffer byteBuffer = MemoryUtil.memAlloc(string * int1 * byte3);
			GL11.glReadPixels(byte1, byte2, string, int1, 6407, 5121, byteBuffer);
			int[] intArray = new int[string * int1];
			if (var0 == null) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
				var0 = "screenshot_" + simpleDateFormat.format(Calendar.getInstance().getTime()) + ".png";
			}

			String string2 = ZomboidFileSystem.instance.getScreenshotDir();
			File file = new File(string2 + File.separator + var0);
			for (int int2 = 0; int2 < intArray.length; ++int2) {
				int int3 = int2 * 3;
				intArray[int2] = -16777216 | (byteBuffer.get(int3) & 255) << 16 | (byteBuffer.get(int3 + 1) & 255) << 8 | (byteBuffer.get(int3 + 2) & 255) << 0;
			}

			MemoryUtil.memFree(byteBuffer);
			intArray = flipPixels(intArray, string, int1);
			BufferedImage bufferedImage = new BufferedImage(string, int1, 2);
			bufferedImage.setRGB(0, 0, string, int1, intArray, 0, string);
			try {
				ImageIO.write(bufferedImage, "png", file);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		});
	}

	public static boolean supportNPTTexture() {
		return false;
	}

	public boolean supportsFBO() {
		if (SafeMode) {
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
					SafeMode = true;
					this.OffscreenBuffer.bZoomEnabled = false;
					return false;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				this.bSupportsFBO = false;
				SafeMode = true;
				this.OffscreenBuffer.bZoomEnabled = false;
				return false;
			}
		}
	}

	private void sharedInit() {
		this.supportsFBO();
	}

	public void MoveMethodToggle() {
		bAltMoveMethod = !bAltMoveMethod;
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

	public void EndFrameUI() {
		if (!blinkAlphaIncrease) {
			blinkAlpha -= 0.07F * (GameTime.getInstance().getMultiplier() / 1.6F);
			if (blinkAlpha < 0.15F) {
				blinkAlpha = 0.15F;
				blinkAlphaIncrease = true;
			}
		} else {
			blinkAlpha += 0.07F * (GameTime.getInstance().getMultiplier() / 1.6F);
			if (blinkAlpha > 1.0F) {
				blinkAlpha = 1.0F;
				blinkAlphaIncrease = false;
			}
		}

		if (UIManager.useUIFBO && UIManager.UIFBO == null) {
			UIManager.CreateFBO(width, height);
		}

		if (LuaManager.thread != null && LuaManager.thread.bStep) {
			SpriteRenderer.instance.clearSprites();
		} else {
			ExceptionLogger.render();
			if (UIManager.useUIFBO && this.UIRenderThisFrame) {
				SpriteRenderer.instance.glBuffer(3, 0);
				IndieGL.glDoEndFrame();
				SpriteRenderer.instance.stopOffscreenUI();
				IndieGL.glDoStartFrame(width, height, 1.0F, -1);
				float float1 = (float)((int)(1.0F / (float)OptionUIRenderFPS * 100.0F)) / 100.0F;
				int int1 = (int)(this.UIRenderAccumulator / float1);
				this.UIRenderAccumulator -= (float)int1 * float1;
				if (FPSGraph.instance != null) {
					FPSGraph.instance.addUI(System.currentTimeMillis());
				}
			}

			if (UIManager.useUIFBO) {
				SpriteRenderer.instance.setDoAdditive(true);
				SpriteRenderer.instance.renderi((Texture)UIManager.UIFBO.getTexture(), 0, height, width, -height, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
				SpriteRenderer.instance.setDoAdditive(false);
			}

			if (getInstance().getOptionLockCursorToWindow()) {
				Mouse.renderCursorTexture();
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
		if (this.OffscreenBuffer == null) {
			return IsoPlayer.numPlayers > 1 ? this.getScreenWidth() / 2 : this.getScreenWidth();
		} else {
			return this.OffscreenBuffer.getWidth(int1);
		}
	}

	public int getOffscreenHeight(int int1) {
		if (this.OffscreenBuffer == null) {
			return IsoPlayer.numPlayers > 2 ? this.getScreenHeight() / 2 : this.getScreenHeight();
		} else {
			return this.OffscreenBuffer.getHeight(int1);
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

	public boolean loadOptions() throws IOException {
		String string = ZomboidFileSystem.instance.getCacheDir();
		File file = new File(string + File.separator + "options.ini");
		int int1;
		int int2;
		if (!file.exists()) {
			this.saveFolder = getMyDocumentFolder();
			File file2 = new File(this.saveFolder);
			file2.mkdir();
			this.copyPasteFolders("mods");
			this.setOptionLanguageName(System.getProperty("user.language").toUpperCase());
			if (Translator.getAzertyMap().contains(Translator.getLanguage().name())) {
				this.setAzerty(true);
			}

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
			for (int int4 = 0; int4 < 4; ++int4) {
				this.setAutoZoom(int4, false);
			}

			OptionLanguageName = null;
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			try {
				String string2;
				while ((string2 = bufferedReader.readLine()) != null) {
					if (string2.startsWith("version=")) {
						this.version = new Integer(string2.replaceFirst("version=", ""));
					} else if (string2.startsWith("width=")) {
						width = new Integer(string2.replaceFirst("width=", ""));
					} else if (string2.startsWith("height=")) {
						height = new Integer(string2.replaceFirst("height=", ""));
					} else if (string2.startsWith("fullScreen=")) {
						fullScreen = Boolean.parseBoolean(string2.replaceFirst("fullScreen=", ""));
					} else if (string2.startsWith("frameRate=")) {
						PerformanceSettings.setLockFPS(Integer.parseInt(string2.replaceFirst("frameRate=", "")));
					} else if (string2.startsWith("uncappedFPS=")) {
						PerformanceSettings.setUncappedFPS(Boolean.parseBoolean(string2.replaceFirst("uncappedFPS=", "")));
					} else if (string2.startsWith("iso_cursor=")) {
						getInstance().setIsoCursorVisibility(Integer.parseInt(string2.replaceFirst("iso_cursor=", "")));
					} else if (string2.startsWith("showCursorWhileAiming=")) {
						OptionShowCursorWhileAiming = Boolean.parseBoolean(string2.replaceFirst("showCursorWhileAiming=", ""));
					} else if (string2.startsWith("water=")) {
						PerformanceSettings.WaterQuality = Integer.parseInt(string2.replaceFirst("water=", ""));
					} else if (string2.startsWith("puddles=")) {
						PerformanceSettings.PuddlesQuality = Integer.parseInt(string2.replaceFirst("puddles=", ""));
					} else if (string2.startsWith("lighting=")) {
						PerformanceSettings.LightingFrameSkip = Integer.parseInt(string2.replaceFirst("lighting=", ""));
					} else if (string2.startsWith("lightFPS=")) {
						PerformanceSettings.instance.setLightingFPS(Integer.parseInt(string2.replaceFirst("lightFPS=", "")));
					} else if (string2.startsWith("perfSkybox=")) {
						this.iPerfSkybox = Integer.parseInt(string2.replaceFirst("perfSkybox=", ""));
						this.iPerfSkybox_new = this.iPerfSkybox;
					} else if (string2.startsWith("perfPuddles=")) {
						this.iPerfPuddles = Integer.parseInt(string2.replaceFirst("perfPuddles=", ""));
						this.iPerfPuddles_new = this.iPerfPuddles;
					} else if (string2.startsWith("bPerfReflections=")) {
						this.bPerfReflections = Boolean.parseBoolean(string2.replaceFirst("bPerfReflections=", ""));
						this.bPerfReflections_new = this.bPerfReflections;
					} else if (string2.startsWith("bMultithreadedRendering=")) {
						bMultithreadedRendering = Boolean.parseBoolean(string2.replaceFirst("bMultithreadedRendering=", ""));
					} else if (string2.startsWith("language=")) {
						OptionLanguageName = string2.replaceFirst("language=", "").trim();
					} else if (string2.startsWith("zoom=")) {
						OptionZoom = Boolean.parseBoolean(string2.replaceFirst("zoom=", ""));
					} else {
						String[] stringArray;
						if (string2.startsWith("autozoom=")) {
							stringArray = string2.replaceFirst("autozoom=", "").split(",");
							for (int2 = 0; int2 < stringArray.length; ++int2) {
								if (!stringArray[int2].isEmpty()) {
									int1 = Integer.parseInt(stringArray[int2]);
									if (int1 >= 1 && int1 <= 4) {
										this.setAutoZoom(int1 - 1, true);
									}
								}
							}
						} else if (string2.startsWith("fontSize=")) {
							this.setOptionFontSize(Integer.parseInt(string2.replaceFirst("fontSize=", "").trim()));
						} else if (string2.startsWith("contextMenuFont=")) {
							OptionContextMenuFont = string2.replaceFirst("contextMenuFont=", "").trim();
						} else if (string2.startsWith("inventoryFont=")) {
							OptionInventoryFont = string2.replaceFirst("inventoryFont=", "").trim();
						} else if (string2.startsWith("tooltipFont=")) {
							OptionTooltipFont = string2.replaceFirst("tooltipFont=", "").trim();
						} else if (string2.startsWith("measurementsFormat=")) {
							OptionMeasurementFormat = string2.replaceFirst("measurementsFormat=", "").trim();
						} else if (string2.startsWith("clockFormat=")) {
							OptionClockFormat = Integer.parseInt(string2.replaceFirst("clockFormat=", ""));
						} else if (string2.startsWith("clockSize=")) {
							OptionClockSize = Integer.parseInt(string2.replaceFirst("clockSize=", ""));
						} else if (string2.startsWith("clock24Hour=")) {
							OptionClock24Hour = Boolean.parseBoolean(string2.replaceFirst("clock24Hour=", ""));
						} else if (string2.startsWith("vsync=")) {
							OptionVSync = Boolean.parseBoolean(string2.replaceFirst("vsync=", ""));
						} else if (string2.startsWith("voiceEnable=")) {
							OptionVoiceEnable = Boolean.parseBoolean(string2.replaceFirst("voiceEnable=", ""));
						} else if (string2.startsWith("voiceMode=")) {
							OptionVoiceMode = Integer.parseInt(string2.replaceFirst("voiceMode=", ""));
						} else if (string2.startsWith("voiceVADMode=")) {
							OptionVoiceVADMode = Integer.parseInt(string2.replaceFirst("voiceVADMode=", ""));
						} else if (string2.startsWith("voiceVolumeMic=")) {
							OptionVoiceVolumeMic = Integer.parseInt(string2.replaceFirst("voiceVolumeMic=", ""));
						} else if (string2.startsWith("voiceVolumePlayers=")) {
							OptionVoiceVolumePlayers = Integer.parseInt(string2.replaceFirst("voiceVolumePlayers=", ""));
						} else if (string2.startsWith("voiceRecordDeviceName=")) {
							OptionVoiceRecordDeviceName = string2.replaceFirst("voiceRecordDeviceName=", "");
						} else if (string2.startsWith("soundVolume=")) {
							OptionSoundVolume = Integer.parseInt(string2.replaceFirst("soundVolume=", ""));
						} else if (string2.startsWith("musicVolume=")) {
							OptionMusicVolume = Integer.parseInt(string2.replaceFirst("musicVolume=", ""));
						} else if (string2.startsWith("ambientVolume=")) {
							OptionAmbientVolume = Integer.parseInt(string2.replaceFirst("ambientVolume=", ""));
						} else if (string2.startsWith("musicLibrary=")) {
							OptionMusicLibrary = Integer.parseInt(string2.replaceFirst("musicLibrary=", ""));
						} else if (string2.startsWith("vehicleEngineVolume=")) {
							OptionVehicleEngineVolume = Integer.parseInt(string2.replaceFirst("vehicleEngineVolume=", ""));
						} else if (string2.startsWith("reloadDifficulty=")) {
							OptionReloadDifficulty = Integer.parseInt(string2.replaceFirst("reloadDifficulty=", ""));
						} else if (string2.startsWith("rackProgress=")) {
							OptionRackProgress = Boolean.parseBoolean(string2.replaceFirst("rackProgress=", ""));
						} else if (string2.startsWith("controller=")) {
							String string3 = string2.replaceFirst("controller=", "");
							if (!string3.isEmpty()) {
								JoypadManager.instance.setControllerActive(string3, true);
							}
						} else if (string2.startsWith("tutorialDone=")) {
							this.tutorialDone = Boolean.parseBoolean(string2.replaceFirst("tutorialDone=", ""));
						} else if (string2.startsWith("vehiclesWarningShow=")) {
							this.vehiclesWarningShow = Boolean.parseBoolean(string2.replaceFirst("vehiclesWarningShow=", ""));
						} else if (string2.startsWith("bloodDecals=")) {
							this.setOptionBloodDecals(Integer.parseInt(string2.replaceFirst("bloodDecals=", "")));
						} else if (string2.startsWith("borderless=")) {
							OptionBorderlessWindow = Boolean.parseBoolean(string2.replaceFirst("borderless=", ""));
						} else if (string2.startsWith("lockCursorToWindow=")) {
							OptionLockCursorToWindow = Boolean.parseBoolean(string2.replaceFirst("lockCursorToWindow=", ""));
						} else if (string2.startsWith("textureCompression=")) {
							OptionTextureCompression = Boolean.parseBoolean(string2.replaceFirst("textureCompression=", ""));
						} else if (string2.startsWith("modelTextureMipmaps=")) {
							OptionModelTextureMipmaps = Boolean.parseBoolean(string2.replaceFirst("modelTextureMipmaps=", ""));
						} else if (string2.startsWith("texture2x=")) {
							OptionTexture2x = Boolean.parseBoolean(string2.replaceFirst("texture2x=", ""));
						} else if (string2.startsWith("zoomLevels1x=")) {
							OptionZoomLevels1x = string2.replaceFirst("zoomLevels1x=", "");
						} else if (string2.startsWith("zoomLevels2x=")) {
							OptionZoomLevels2x = string2.replaceFirst("zoomLevels2x=", "");
						} else if (string2.startsWith("showChatTimestamp=")) {
							OptionShowChatTimestamp = Boolean.parseBoolean(string2.replaceFirst("showChatTimestamp=", ""));
						} else if (string2.startsWith("showChatTitle=")) {
							OptionShowChatTitle = Boolean.parseBoolean(string2.replaceFirst("showChatTitle=", ""));
						} else if (string2.startsWith("chatFontSize=")) {
							OptionChatFontSize = string2.replaceFirst("chatFontSize=", "");
						} else if (string2.startsWith("minChatOpaque=")) {
							OptionMinChatOpaque = Float.parseFloat(string2.replaceFirst("minChatOpaque=", ""));
						} else if (string2.startsWith("maxChatOpaque=")) {
							OptionMaxChatOpaque = Float.parseFloat(string2.replaceFirst("maxChatOpaque=", ""));
						} else if (string2.startsWith("chatFadeTime=")) {
							OptionChatFadeTime = Float.parseFloat(string2.replaceFirst("chatFadeTime=", ""));
						} else if (string2.startsWith("chatOpaqueOnFocus=")) {
							OptionChatOpaqueOnFocus = Boolean.parseBoolean(string2.replaceFirst("chatOpaqueOnFocus=", ""));
						} else if (string2.startsWith("doneNewSaveFolder=")) {
							this.doneNewSaveFolder = Boolean.parseBoolean(string2.replaceFirst("doneNewSaveFolder=", ""));
						} else if (string2.startsWith("contentTranslationsEnabled=")) {
							OptionEnableContentTranslations = Boolean.parseBoolean(string2.replaceFirst("contentTranslationsEnabled=", ""));
						} else if (string2.startsWith("showYourUsername=")) {
							this.showYourUsername = Boolean.parseBoolean(string2.replaceFirst("showYourUsername=", ""));
						} else if (string2.startsWith("riversideDone=")) {
							this.riversideDone = Boolean.parseBoolean(string2.replaceFirst("riversideDone=", ""));
						} else if (string2.startsWith("rosewoodSpawnDone=")) {
							this.rosewoodSpawnDone = Boolean.parseBoolean(string2.replaceFirst("rosewoodSpawnDone=", ""));
						} else if (string2.startsWith("gotNewBelt=")) {
							this.gotNewBelt = Boolean.parseBoolean(string2.replaceFirst("gotNewBelt=", ""));
						} else {
							float float1;
							float float2;
							float float3;
							if (string2.startsWith("mpTextColor=")) {
								stringArray = string2.replaceFirst("mpTextColor=", "").split(",");
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
							} else if (string2.startsWith("objHighlightColor=")) {
								stringArray = string2.replaceFirst("objHighlightColor=", "").split(",");
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

								this.objectHighlitedColor.set(float1, float2, float3, 1.0F);
							} else if (string2.startsWith("seenNews=")) {
								this.setSeenUpdateText(string2.replaceFirst("seenNews=", ""));
							} else if (string2.startsWith("toggleToAim=")) {
								this.setToggleToAim(Boolean.parseBoolean(string2.replaceFirst("toggleToAim=", "")));
							} else if (string2.startsWith("toggleToRun=")) {
								this.setToggleToRun(Boolean.parseBoolean(string2.replaceFirst("toggleToRun=", "")));
							} else if (string2.startsWith("toggleToSprint=")) {
								this.setToggleToSprint(Boolean.parseBoolean(string2.replaceFirst("toggleToSprint=", "")));
							} else if (string2.startsWith("celsius=")) {
								this.setCelsius(Boolean.parseBoolean(string2.replaceFirst("celsius=", "")));
							} else if (!string2.startsWith("mapOrder=")) {
								if (string2.startsWith("showFirstTimeSneakTutorial=")) {
									this.setShowFirstTimeSneakTutorial(Boolean.parseBoolean(string2.replaceFirst("showFirstTimeSneakTutorial=", "")));
								} else if (string2.startsWith("uiRenderOffscreen=")) {
									OptionUIFBO = Boolean.parseBoolean(string2.replaceFirst("uiRenderOffscreen=", ""));
								} else if (string2.startsWith("uiRenderFPS=")) {
									OptionUIRenderFPS = Integer.parseInt(string2.replaceFirst("uiRenderFPS=", ""));
								} else if (string2.startsWith("radialMenuKeyToggle=")) {
									OptionRadialMenuKeyToggle = Boolean.parseBoolean(string2.replaceFirst("radialMenuKeyToggle=", ""));
								} else if (string2.startsWith("reloadRadialInstant=")) {
									OptionReloadRadialInstant = Boolean.parseBoolean(string2.replaceFirst("reloadRadialInstant=", ""));
								} else if (string2.startsWith("panCameraWhileAiming=")) {
									OptionPanCameraWhileAiming = Boolean.parseBoolean(string2.replaceFirst("panCameraWhileAiming=", ""));
								} else if (string2.startsWith("temperatureDisplayCelsius=")) {
									OptionTemperatureDisplayCelsius = Boolean.parseBoolean(string2.replaceFirst("temperatureDisplayCelsius=", ""));
								} else if (string2.startsWith("doWindSpriteEffects=")) {
									OptionDoWindSpriteEffects = Boolean.parseBoolean(string2.replaceFirst("doWindSpriteEffects=", ""));
								} else if (string2.startsWith("doDoorSpriteEffects=")) {
									OptionDoDoorSpriteEffects = Boolean.parseBoolean(string2.replaceFirst("doDoorSpriteEffects=", ""));
								} else if (string2.startsWith("updateSneakButton2=")) {
									OptionUpdateSneakButton = true;
								} else if (string2.startsWith("updateSneakButton=")) {
									OptionUpdateSneakButton = Boolean.parseBoolean(string2.replaceFirst("updateSneakButton=", ""));
								} else if (string2.startsWith("dblTapJogToSprint=")) {
									OptiondblTapJogToSprint = Boolean.parseBoolean(string2.replaceFirst("dblTapJogToSprint=", ""));
								} else if (string2.startsWith("aimOutline=")) {
									this.setOptionAimOutline(PZMath.tryParseInt(string2.replaceFirst("aimOutline=", ""), 2));
								} else if (string2.startsWith("cycleContainerKey=")) {
									OptionCycleContainerKey = string2.replaceFirst("cycleContainerKey=", "");
								} else if (string2.startsWith("dropItemsOnSquareCenter=")) {
									OptionDropItemsOnSquareCenter = Boolean.parseBoolean(string2.replaceFirst("dropItemsOnSquareCenter=", ""));
								} else if (string2.startsWith("timedActionGameSpeedReset=")) {
									OptionTimedActionGameSpeedReset = Boolean.parseBoolean(string2.replaceFirst("timedActionGameSpeedReset=", ""));
								} else if (string2.startsWith("shoulderButtonContainerSwitch=")) {
									OptionShoulderButtonContainerSwitch = Integer.parseInt(string2.replaceFirst("shoulderButtonContainerSwitch=", ""));
								} else if (string2.startsWith("singleContextMenu=")) {
									this.readPerPlayerBoolean(string2.replaceFirst("singleContextMenu=", ""), OptionSingleContextMenu);
								} else if (string2.startsWith("renderPrecipIndoors=")) {
									OptionRenderPrecipIndoors = Boolean.parseBoolean(string2.replaceFirst("renderPrecipIndoors=", ""));
								} else if (string2.startsWith("autoProneAtk=")) {
									OptionAutoProneAtk = Boolean.parseBoolean(string2.replaceFirst("autoProneAtk=", ""));
								} else if (string2.startsWith("3DGroundItem=")) {
									Option3DGroundItem = Boolean.parseBoolean(string2.replaceFirst("3DGroundItem=", ""));
								} else if (string2.startsWith("tieredZombieUpdates=")) {
									this.setOptionTieredZombieUpdates(Boolean.parseBoolean(string2.replaceFirst("tieredZombieUpdates=", "")));
								} else if (string2.startsWith("progressBar=")) {
									this.setOptionProgressBar(Boolean.parseBoolean(string2.replaceFirst("progressBar=", "")));
								} else if (string2.startsWith("corpseShadows=")) {
									OptionCorpseShadows = Boolean.parseBoolean(string2.replaceFirst("corpseShadows=", ""));
								} else if (string2.startsWith("simpleClothingTextures=")) {
									this.setOptionSimpleClothingTextures(PZMath.tryParseInt(string2.replaceFirst("simpleClothingTextures=", ""), 1));
								} else if (string2.startsWith("simpleWeaponTextures=")) {
									OptionSimpleWeaponTextures = Boolean.parseBoolean(string2.replaceFirst("simpleWeaponTextures=", ""));
								} else if (string2.startsWith("autoDrink=")) {
									OptionAutoDrink = Boolean.parseBoolean(string2.replaceFirst("autoDrink=", ""));
								} else if (string2.startsWith("leaveKeyInIgnition=")) {
									OptionLeaveKeyInIgnition = Boolean.parseBoolean(string2.replaceFirst("leaveKeyInIgnition=", ""));
								} else if (string2.startsWith("ignoreProneZombieRange=")) {
									this.setOptionIgnoreProneZombieRange(PZMath.tryParseInt(string2.replaceFirst("ignoreProneZombieRange=", ""), 1));
								} else if (string2.startsWith("fogQuality=")) {
									PerformanceSettings.FogQuality = Integer.parseInt(string2.replaceFirst("fogQuality=", ""));
								} else if (string2.startsWith("renderPrecipitation=")) {
									OptionRenderPrecipitation = Integer.parseInt(string2.replaceFirst("renderPrecipitation=", ""));
								}
							} else {
								if (this.version < 7) {
									string2 = "mapOrder=";
								}

								stringArray = string2.replaceFirst("mapOrder=", "").split(";");
								String[] stringArray2 = stringArray;
								int1 = stringArray.length;
								for (int int5 = 0; int5 < int1; ++int5) {
									String string4 = stringArray2[int5];
									string4 = string4.trim();
									if (!string4.isEmpty()) {
										ActiveMods.getById("default").getMapOrder().add(string4);
									}
								}

								ZomboidFileSystem.instance.saveModsFile();
							}
						}
					}
				}

				if (OptionLanguageName == null) {
					OptionLanguageName = System.getProperty("user.language").toUpperCase();
				}

				if (!this.doneNewSaveFolder) {
					File file3 = new File(ZomboidFileSystem.instance.getSaveDir());
					file3.mkdir();
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
					File file4 = null;
					File file5 = null;
					try {
						Iterator iterator = arrayList.iterator();
						while (iterator.hasNext()) {
							String string5 = (String)iterator.next();
							string = ZomboidFileSystem.instance.getCacheDir();
							file4 = new File(string + File.separator + string5);
							string = ZomboidFileSystem.instance.getSaveDir();
							file5 = new File(string + File.separator + string5);
							if (file4.exists()) {
								file5.mkdir();
								Files.move(file4.toPath(), file5.toPath(), StandardCopyOption.REPLACE_EXISTING);
							}
						}
					} catch (Exception exception) {
					}

					this.doneNewSaveFolder = true;
				}
			} catch (Exception exception2) {
				exception2.printStackTrace();
			} finally {
				bufferedReader.close();
			}

			this.saveOptions();
			return true;
		}
	}

	public boolean isDedicated() {
		return GameServer.bServer;
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
				String string2 = file.getAbsolutePath();
				this.searchFolders(new File(string2 + File.separator + stringArray[int1]), string + File.separator + stringArray[int1]);
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
		return ZomboidFileSystem.instance.getCacheDir();
	}

	public void saveOptions() throws IOException {
		String string = ZomboidFileSystem.instance.getCacheDir();
		File file = new File(string + File.separator + "options.ini");
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fileWriter = new FileWriter(file);
		try {
			fileWriter.write("version=" + this.fileversion + "\r\n");
			fileWriter.write("width=" + this.getScreenWidth() + "\r\n");
			fileWriter.write("height=" + this.getScreenHeight() + "\r\n");
			fileWriter.write("fullScreen=" + fullScreen + "\r\n");
			fileWriter.write("frameRate=" + PerformanceSettings.getLockFPS() + "\r\n");
			fileWriter.write("uncappedFPS=" + PerformanceSettings.isUncappedFPS() + "\r\n");
			fileWriter.write("iso_cursor=" + getInstance().getIsoCursorVisibility() + "\r\n");
			fileWriter.write("showCursorWhileAiming=" + OptionShowCursorWhileAiming + "\r\n");
			fileWriter.write("water=" + PerformanceSettings.WaterQuality + "\r\n");
			fileWriter.write("puddles=" + PerformanceSettings.PuddlesQuality + "\r\n");
			fileWriter.write("lighting=" + PerformanceSettings.LightingFrameSkip + "\r\n");
			fileWriter.write("lightFPS=" + PerformanceSettings.LightingFPS + "\r\n");
			fileWriter.write("perfSkybox=" + this.iPerfSkybox_new + "\r\n");
			fileWriter.write("perfPuddles=" + this.iPerfPuddles_new + "\r\n");
			fileWriter.write("bPerfReflections=" + this.bPerfReflections_new + "\r\n");
			fileWriter.write("vidMem=" + this.vidMem + "\r\n");
			fileWriter.write("bMultithreadedRendering=" + bMultithreadedRendering + "\r\n");
			fileWriter.write("language=" + this.getOptionLanguageName() + "\r\n");
			fileWriter.write("zoom=" + OptionZoom + "\r\n");
			fileWriter.write("fontSize=" + OptionFontSize + "\r\n");
			fileWriter.write("contextMenuFont=" + OptionContextMenuFont + "\r\n");
			fileWriter.write("inventoryFont=" + OptionInventoryFont + "\r\n");
			fileWriter.write("tooltipFont=" + OptionTooltipFont + "\r\n");
			fileWriter.write("clockFormat=" + OptionClockFormat + "\r\n");
			fileWriter.write("clockSize=" + OptionClockSize + "\r\n");
			fileWriter.write("clock24Hour=" + OptionClock24Hour + "\r\n");
			fileWriter.write("measurementsFormat=" + OptionMeasurementFormat + "\r\n");
			String string2 = "";
			for (int int1 = 0; int1 < 4; ++int1) {
				if (bAutoZoom[int1]) {
					if (!string2.isEmpty()) {
						string2 = string2 + ",";
					}

					string2 = string2 + (int1 + 1);
				}
			}

			fileWriter.write("autozoom=" + string2 + "\r\n");
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
			Iterator iterator = JoypadManager.instance.ActiveControllerGUIDs.iterator();
			while (iterator.hasNext()) {
				String string3 = (String)iterator.next();
				fileWriter.write("controller=" + string3 + "\r\n");
			}

			fileWriter.write("tutorialDone=" + this.isTutorialDone() + "\r\n");
			fileWriter.write("vehiclesWarningShow=" + this.isVehiclesWarningShow() + "\r\n");
			fileWriter.write("bloodDecals=" + OptionBloodDecals + "\r\n");
			fileWriter.write("borderless=" + OptionBorderlessWindow + "\r\n");
			fileWriter.write("lockCursorToWindow=" + OptionLockCursorToWindow + "\r\n");
			fileWriter.write("textureCompression=" + OptionTextureCompression + "\r\n");
			fileWriter.write("modelTextureMipmaps=" + OptionModelTextureMipmaps + "\r\n");
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
			fileWriter.write("toggleToAim=" + this.isToggleToAim() + "\r\n");
			fileWriter.write("toggleToRun=" + this.isToggleToRun() + "\r\n");
			fileWriter.write("toggleToSprint=" + this.isToggleToSprint() + "\r\n");
			fileWriter.write("celsius=" + this.isCelsius() + "\r\n");
			fileWriter.write("riversideDone=" + this.isRiversideDone() + "\r\n");
			fileWriter.write("showFirstTimeSneakTutorial=" + this.isShowFirstTimeSneakTutorial() + "\r\n");
			fileWriter.write("uiRenderOffscreen=" + OptionUIFBO + "\r\n");
			fileWriter.write("uiRenderFPS=" + OptionUIRenderFPS + "\r\n");
			fileWriter.write("radialMenuKeyToggle=" + OptionRadialMenuKeyToggle + "\r\n");
			fileWriter.write("reloadRadialInstant=" + OptionReloadRadialInstant + "\r\n");
			fileWriter.write("panCameraWhileAiming=" + OptionPanCameraWhileAiming + "\r\n");
			fileWriter.write("temperatureDisplayCelsius=" + OptionTemperatureDisplayCelsius + "\r\n");
			fileWriter.write("doWindSpriteEffects=" + OptionDoWindSpriteEffects + "\r\n");
			fileWriter.write("doDoorSpriteEffects=" + OptionDoDoorSpriteEffects + "\r\n");
			fileWriter.write("updateSneakButton=" + OptionUpdateSneakButton + "\r\n");
			fileWriter.write("dblTapJogToSprint=" + OptiondblTapJogToSprint + "\r\n");
			fileWriter.write("gotNewBelt=" + this.gotNewBelt + "\r\n");
			fileWriter.write("aimOutline=" + OptionAimOutline + "\r\n");
			fileWriter.write("cycleContainerKey=" + OptionCycleContainerKey + "\r\n");
			fileWriter.write("dropItemsOnSquareCenter=" + OptionDropItemsOnSquareCenter + "\r\n");
			fileWriter.write("timedActionGameSpeedReset=" + OptionTimedActionGameSpeedReset + "\r\n");
			fileWriter.write("shoulderButtonContainerSwitch=" + OptionShoulderButtonContainerSwitch + "\r\n");
			fileWriter.write("singleContextMenu=" + this.getPerPlayerBooleanString(OptionSingleContextMenu) + "\r\n");
			fileWriter.write("renderPrecipIndoors=" + OptionRenderPrecipIndoors + "\r\n");
			fileWriter.write("autoProneAtk=" + OptionAutoProneAtk + "\r\n");
			fileWriter.write("3DGroundItem=" + Option3DGroundItem + "\r\n");
			fileWriter.write("tieredZombieUpdates=" + this.getOptionTieredZombieUpdates() + "\r\n");
			fileWriter.write("progressBar=" + this.isOptionProgressBar() + "\r\n");
			fileWriter.write("corpseShadows=" + this.getOptionCorpseShadows() + "\r\n");
			fileWriter.write("simpleClothingTextures=" + this.getOptionSimpleClothingTextures() + "\r\n");
			fileWriter.write("simpleWeaponTextures=" + this.getOptionSimpleWeaponTextures() + "\r\n");
			fileWriter.write("autoDrink=" + this.getOptionAutoDrink() + "\r\n");
			fileWriter.write("leaveKeyInIgnition=" + this.getOptionLeaveKeyInIgnition() + "\r\n");
			fileWriter.write("ignoreProneZombieRange=" + this.getOptionIgnoreProneZombieRange() + "\r\n");
			fileWriter.write("fogQuality=" + PerformanceSettings.FogQuality + "\r\n");
			fileWriter.write("renderPrecipitation=" + OptionRenderPrecipitation + "\r\n");
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			fileWriter.close();
		}
	}

	public void setWindowed(boolean boolean1) {
		RenderThread.invokeOnRenderContext(()->{
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
		});
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public KahluaTable getScreenModes() {
		ArrayList arrayList = new ArrayList();
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		String string = LuaManager.getLuaCacheDir();
		File file = new File(string + File.separator + "screenresolution.ini");
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
				String string2;
				for (integer = null; (string2 = bufferedReader.readLine()) != null; ++int1) {
					kahluaTable.rawset(int1, string2.trim());
				}

				bufferedReader.close();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return kahluaTable;
	}

	public static void setDisplayMode(int int1, int int2, boolean boolean1) {
		RenderThread.invokeOnRenderContext(()->{
			if (Display.getWidth() != int1 || Display.getHeight() != int2 || Display.isFullscreen() != boolean1 || Display.isBorderlessWindow() != OptionBorderlessWindow) {
				fullScreen = boolean1;
				try {
					DisplayMode displayMode = null;
					if (!boolean1) {
						if (OptionBorderlessWindow) {
							if (Display.getWindow() != 0L && Display.isFullscreen()) {
								Display.setFullscreen(false);
							}

							long long1 = GLFW.glfwGetPrimaryMonitor();
							GLFWVidMode gLFWVidMode = GLFW.glfwGetVideoMode(long1);
							displayMode = new DisplayMode(gLFWVidMode.width(), gLFWVidMode.height());
						} else {
							displayMode = new DisplayMode(int1, int2);
						}
					} else {
						DisplayMode[] displayModeArray = Display.getAvailableDisplayModes();
						int int3 = 0;
						DisplayMode displayMode2 = null;
						DisplayMode[] displayModeArray2 = displayModeArray;
						int int4 = displayModeArray.length;
						for (int int5 = 0; int5 < int4; ++int5) {
							DisplayMode displayMode3 = displayModeArray2[int5];
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
								PrintStream printStream = System.out;
								int int6 = displayMode3.getWidth();
								printStream.println("closest width=" + int6 + " freq=" + displayMode3.getFrequency());
							}
						}

						if (displayMode == null && displayMode2 != null) {
							displayMode = displayMode2;
						}
					}

					if (displayMode == null) {
						DebugLog.log("Failed to find value mode: " + int1 + "x" + int2 + " fs=" + boolean1);
						return;
					}

					Display.setBorderlessWindow(OptionBorderlessWindow);
					if (boolean1) {
						Display.setDisplayModeAndFullscreen(displayMode);
					} else {
						Display.setDisplayMode(displayMode);
						Display.setFullscreen(false);
					}

					if (!boolean1 && OptionBorderlessWindow) {
						Display.setResizable(false);
					} else if (!boolean1 && !fakefullscreen) {
						Display.setResizable(false);
						Display.setResizable(true);
					}

					if (Display.isCreated()) {
						int int7 = Display.getWidth();
						DebugLog.log("Display mode changed to " + int7 + "x" + Display.getHeight() + " freq=" + Display.getDisplayMode().getFrequency() + " fullScreen=" + Display.isFullscreen());
					}
				} catch (LWJGLException lWJGLException) {
					DebugLog.log("Unable to setup mode " + int1 + "x" + int2 + " fullscreen=" + boolean1 + lWJGLException);
				}
			}
		});
	}

	private boolean isFunctionKey(int int1) {
		return int1 >= 59 && int1 <= 68 || int1 >= 87 && int1 <= 105 || int1 == 113;
	}

	public boolean isDoingTextEntry() {
		if (CurrentTextEntryBox == null) {
			return false;
		} else if (!CurrentTextEntryBox.IsEditable) {
			return false;
		} else {
			return CurrentTextEntryBox.DoingTextEntry;
		}
	}

	private void updateKeyboardAux(UITextBox2 uITextBox2, int int1) {
		boolean boolean1 = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
		boolean boolean2 = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
		int int2;
		if (int1 != 28 && int1 != 156) {
			if (int1 == 1) {
				uITextBox2.onOtherKey(1);
				GameKeyboard.eatKeyPress(1);
			} else if (int1 == 15) {
				uITextBox2.onOtherKey(15);
				LuaEventManager.triggerEvent("SwitchChatStream");
			} else if (int1 != 58) {
				if (int1 == 199) {
					uITextBox2.TextEntryCursorPos = 0;
					if (!uITextBox2.Lines.isEmpty()) {
						uITextBox2.TextEntryCursorPos = uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine);
					}

					if (!boolean2) {
						uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
					}

					uITextBox2.resetBlink();
				} else if (int1 == 207) {
					uITextBox2.TextEntryCursorPos = uITextBox2.internalText.length();
					if (!uITextBox2.Lines.isEmpty()) {
						uITextBox2.TextEntryCursorPos = uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine) + ((String)uITextBox2.Lines.get(uITextBox2.CursorLine)).length();
					}

					if (!boolean2) {
						uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
					}

					uITextBox2.resetBlink();
				} else {
					int int3;
					if (int1 == 200) {
						if (uITextBox2.CursorLine > 0) {
							int3 = uITextBox2.TextEntryCursorPos - uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine);
							--uITextBox2.CursorLine;
							if (int3 > ((String)uITextBox2.Lines.get(uITextBox2.CursorLine)).length()) {
								int3 = ((String)uITextBox2.Lines.get(uITextBox2.CursorLine)).length();
							}

							uITextBox2.TextEntryCursorPos = uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine) + int3;
							if (!boolean2) {
								uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
							}
						}

						uITextBox2.onPressUp();
					} else if (int1 == 208) {
						if (uITextBox2.Lines.size() - 1 > uITextBox2.CursorLine && uITextBox2.CursorLine + 1 < uITextBox2.getMaxLines()) {
							int3 = uITextBox2.TextEntryCursorPos - uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine);
							++uITextBox2.CursorLine;
							if (int3 > ((String)uITextBox2.Lines.get(uITextBox2.CursorLine)).length()) {
								int3 = ((String)uITextBox2.Lines.get(uITextBox2.CursorLine)).length();
							}

							uITextBox2.TextEntryCursorPos = uITextBox2.TextOffsetOfLineStart.get(uITextBox2.CursorLine) + int3;
							if (!boolean2) {
								uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
							}
						}

						uITextBox2.onPressDown();
					} else if (int1 != 29) {
						if (int1 != 157) {
							if (int1 != 42) {
								if (int1 != 54) {
									if (int1 != 56) {
										if (int1 != 184) {
											if (int1 == 203) {
												--uITextBox2.TextEntryCursorPos;
												if (uITextBox2.TextEntryCursorPos < 0) {
													uITextBox2.TextEntryCursorPos = 0;
												}

												if (!boolean2) {
													uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
												}

												uITextBox2.resetBlink();
											} else if (int1 == 205) {
												++uITextBox2.TextEntryCursorPos;
												if (uITextBox2.TextEntryCursorPos > uITextBox2.internalText.length()) {
													uITextBox2.TextEntryCursorPos = uITextBox2.internalText.length();
												}

												if (!boolean2) {
													uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
												}

												uITextBox2.resetBlink();
											} else if (!this.isFunctionKey(int1)) {
												int int4;
												String string;
												if ((int1 == 211 || int1 == 14) && uITextBox2.TextEntryCursorPos != uITextBox2.ToSelectionIndex) {
													int3 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
													int4 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
													string = uITextBox2.internalText.substring(0, int3);
													uITextBox2.internalText = string + uITextBox2.internalText.substring(int4);
													uITextBox2.CursorLine = uITextBox2.toDisplayLine(int3);
													uITextBox2.ToSelectionIndex = int3;
													uITextBox2.TextEntryCursorPos = int3;
													uITextBox2.onTextChange();
												} else if (int1 == 211) {
													if (uITextBox2.internalText.length() != 0 && uITextBox2.TextEntryCursorPos < uITextBox2.internalText.length()) {
														if (uITextBox2.TextEntryCursorPos > 0) {
															string = uITextBox2.internalText.substring(0, uITextBox2.TextEntryCursorPos);
															uITextBox2.internalText = string + uITextBox2.internalText.substring(uITextBox2.TextEntryCursorPos + 1);
														} else {
															uITextBox2.internalText = uITextBox2.internalText.substring(1);
														}

														uITextBox2.onTextChange();
													}
												} else if (int1 == 14) {
													if (uITextBox2.internalText.length() != 0 && uITextBox2.TextEntryCursorPos > 0) {
														if (uITextBox2.TextEntryCursorPos > uITextBox2.internalText.length()) {
															uITextBox2.internalText = uITextBox2.internalText.substring(0, uITextBox2.internalText.length() - 1);
														} else {
															int3 = uITextBox2.TextEntryCursorPos;
															string = uITextBox2.internalText.substring(0, int3 - 1);
															uITextBox2.internalText = string + uITextBox2.internalText.substring(int3);
														}

														--uITextBox2.TextEntryCursorPos;
														uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
														uITextBox2.onTextChange();
													}
												} else if (boolean1 && int1 == 47) {
													String string2 = Clipboard.getClipboard();
													if (string2 != null) {
														if (uITextBox2.TextEntryCursorPos != uITextBox2.ToSelectionIndex) {
															int4 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
															int2 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
															uITextBox2.internalText = uITextBox2.internalText.substring(0, int4) + string2 + uITextBox2.internalText.substring(int2);
															uITextBox2.ToSelectionIndex = int4 + string2.length();
															uITextBox2.TextEntryCursorPos = int4 + string2.length();
														} else {
															if (uITextBox2.TextEntryCursorPos < uITextBox2.internalText.length()) {
																uITextBox2.internalText = uITextBox2.internalText.substring(0, uITextBox2.TextEntryCursorPos) + string2 + uITextBox2.internalText.substring(uITextBox2.TextEntryCursorPos);
															} else {
																uITextBox2.internalText = uITextBox2.internalText + string2;
															}

															uITextBox2.TextEntryCursorPos += string2.length();
															uITextBox2.ToSelectionIndex += string2.length();
														}

														uITextBox2.onTextChange();
													}
												} else {
													String string3;
													if (boolean1 && int1 == 46) {
														if (uITextBox2.TextEntryCursorPos != uITextBox2.ToSelectionIndex) {
															uITextBox2.updateText();
															int3 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
															int4 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
															string3 = uITextBox2.Text.substring(int3, int4);
															if (string3 != null && string3.length() > 0) {
																Clipboard.setClipboard(string3);
															}
														}
													} else if (boolean1 && int1 == 45) {
														if (uITextBox2.TextEntryCursorPos != uITextBox2.ToSelectionIndex) {
															uITextBox2.updateText();
															int3 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
															int4 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
															string3 = uITextBox2.Text.substring(int3, int4);
															if (string3 != null && string3.length() > 0) {
																Clipboard.setClipboard(string3);
															}

															string = uITextBox2.internalText.substring(0, int3);
															uITextBox2.internalText = string + uITextBox2.internalText.substring(int4);
															uITextBox2.ToSelectionIndex = int3;
															uITextBox2.TextEntryCursorPos = int3;
														}
													} else if (boolean1 && int1 == 30) {
														uITextBox2.selectAll();
													} else if (!uITextBox2.ignoreFirst) {
														if (uITextBox2.internalText.length() < uITextBox2.TextEntryMaxLength) {
															char char1 = Keyboard.getEventCharacter();
															if (char1 != 0) {
																if (uITextBox2.isOnlyNumbers() && char1 != '.' && char1 != '-') {
																	try {
																		Double.parseDouble(String.valueOf(char1));
																	} catch (Exception exception) {
																		return;
																	}
																}

																if (uITextBox2.TextEntryCursorPos == uITextBox2.ToSelectionIndex) {
																	int4 = uITextBox2.TextEntryCursorPos;
																	if (int4 < uITextBox2.internalText.length()) {
																		uITextBox2.internalText = uITextBox2.internalText.substring(0, int4) + char1 + uITextBox2.internalText.substring(int4);
																	} else {
																		uITextBox2.internalText = uITextBox2.internalText + char1;
																	}

																	++uITextBox2.TextEntryCursorPos;
																	++uITextBox2.ToSelectionIndex;
																	uITextBox2.onTextChange();
																} else {
																	int4 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
																	int2 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
																	if (uITextBox2.internalText.length() > 0) {
																		uITextBox2.internalText = uITextBox2.internalText.substring(0, int4) + char1 + uITextBox2.internalText.substring(int2);
																	} else {
																		uITextBox2.internalText = char1.makeConcatWithConstants < invokedynamic > (char1);
																	}

																	uITextBox2.ToSelectionIndex = int4 + 1;
																	uITextBox2.TextEntryCursorPos = int4 + 1;
																	uITextBox2.onTextChange();
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			boolean boolean3 = false;
			if (UIManager.getDebugConsole() != null && uITextBox2 == UIManager.getDebugConsole().CommandLine) {
				boolean3 = true;
			}

			if (uITextBox2.multipleLine) {
				if (uITextBox2.Lines.size() < uITextBox2.getMaxLines()) {
					if (uITextBox2.TextEntryCursorPos != uITextBox2.ToSelectionIndex) {
						int2 = Math.min(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
						int int5 = Math.max(uITextBox2.TextEntryCursorPos, uITextBox2.ToSelectionIndex);
						if (uITextBox2.internalText.length() > 0) {
							uITextBox2.internalText = uITextBox2.internalText.substring(0, int2) + "\n" + uITextBox2.internalText.substring(int5);
						} else {
							uITextBox2.internalText = "\n";
						}

						uITextBox2.TextEntryCursorPos = int2 + 1;
					} else {
						int2 = uITextBox2.TextEntryCursorPos;
						String string4 = uITextBox2.internalText.substring(0, int2) + "\n" + uITextBox2.internalText.substring(int2);
						uITextBox2.SetText(string4);
						uITextBox2.TextEntryCursorPos = int2 + 1;
					}

					uITextBox2.ToSelectionIndex = uITextBox2.TextEntryCursorPos;
					uITextBox2.CursorLine = uITextBox2.toDisplayLine(uITextBox2.TextEntryCursorPos);
				}
			} else {
				uITextBox2.onCommandEntered();
			}

			if (boolean3 && (!GameClient.bClient || !GameClient.accessLevel.equals("") || GameClient.connection != null && GameClient.connection.isCoopHost)) {
				UIManager.getDebugConsole().ProcessCommand();
			}
		}
	}

	public void updateKeyboard() {
		if (this.isDoingTextEntry()) {
			while (Keyboard.next()) {
				if (this.isDoingTextEntry() && Keyboard.getEventKeyState()) {
					int int1 = Keyboard.getEventKey();
					this.updateKeyboardAux(CurrentTextEntryBox, int1);
				}
			}

			if (CurrentTextEntryBox != null && CurrentTextEntryBox.ignoreFirst) {
				CurrentTextEntryBox.ignoreFirst = false;
			}
		}
	}

	public void quit() {
		DebugLog.log("EXITDEBUG: Core.quit 1");
		if (IsoPlayer.getInstance() != null) {
			DebugLog.log("EXITDEBUG: Core.quit 2");
			bExiting = true;
		} else {
			DebugLog.log("EXITDEBUG: Core.quit 3");
			try {
				this.saveOptions();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			GameClient.instance.Shutdown();
			SteamUtils.shutdown();
			DebugLog.log("EXITDEBUG: Core.quit 4");
			System.exit(0);
		}
	}

	public void exitToMenu() {
		DebugLog.log("EXITDEBUG: Core.exitToMenu");
		bExiting = true;
	}

	public void quitToDesktop() {
		DebugLog.log("EXITDEBUG: Core.quitToDesktop");
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
		String string = GL11.glGetString(7936);
		DebugLog.log("GraphicsCard: " + string + " " + GL11.glGetString(7937));
		string = GL11.glGetString(7938);
		DebugLog.log("OpenGL version: " + string);
		int int3 = Display.getDesktopDisplayMode().getWidth();
		DebugLog.log("Desktop resolution " + int3 + "x" + Display.getDesktopDisplayMode().getHeight());
		int3 = width;
		DebugLog.log("Initial resolution " + int3 + "x" + height + " fullScreen=" + fullScreen);
		GLVertexBufferObject.init();
		DebugLog.General.println("VSync: %s", OptionVSync ? "ON" : "OFF");
		Display.setVSyncEnabled(OptionVSync);
		GL11.glEnable(3553);
		IndieGL.glBlendFunc(770, 771);
		GL32.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
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

	public static boolean supportCompressedTextures() {
		return GL.getCapabilities().GL_EXT_texture_compression_latc;
	}

	public void StartFrame() {
		if (LuaManager.thread == null || !LuaManager.thread.bStep) {
			if (this.RenderShader != null && this.OffscreenBuffer.Current != null) {
				this.RenderShader.setTexture(this.OffscreenBuffer.getTexture(0));
			}

			SpriteRenderer.instance.prePopulating();
			UIManager.resize();
			boolean boolean1 = false;
			Texture.BindCount = 0;
			if (!boolean1) {
				IndieGL.glClear(18176);
				if (DebugOptions.instance.Terrain.RenderTiles.HighContrastBg.getValue()) {
					SpriteRenderer.instance.glClearColor(255, 0, 255, 255);
					SpriteRenderer.instance.glClear(16384);
				}
			}

			if (this.OffscreenBuffer.Current != null) {
				SpriteRenderer.instance.glBuffer(1, 0);
			}

			IndieGL.glDoStartFrame(this.getScreenWidth(), this.getScreenWidth(), this.getCurrentPlayerZoom(), 0);
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
				SpriteRenderer.instance.prePopulating();
			}

			if (!boolean1) {
				SpriteRenderer.instance.initFromIsoCamera(int1);
			}

			Texture.BindCount = 0;
			IndieGL.glLoadIdentity();
			if (this.OffscreenBuffer.Current != null) {
				SpriteRenderer.instance.glBuffer(1, int1);
			}

			IndieGL.glDoStartFrame(this.getScreenWidth(), this.getScreenHeight(), this.getZoom(int1), int1);
			IndieGL.glClear(17664);
			if (DebugOptions.instance.Terrain.RenderTiles.HighContrastBg.getValue()) {
				SpriteRenderer.instance.glClearColor(255, 0, 255, 255);
				SpriteRenderer.instance.glClear(16384);
			}

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

	public void DoStartFrameStuff(int int1, int int2, float float1, int int3) {
		this.DoStartFrameStuff(int1, int2, float1, int3, false);
	}

	public void DoStartFrameStuff(int int1, int int2, float float1, int int3, boolean boolean1) {
		this.DoStartFrameStuffInternal(int1, int2, float1, int3, boolean1, false, false);
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

	public void DoStartFrameStuffSmartTextureFx(int int1, int int2, int int3) {
		this.DoStartFrameStuffInternal(int1, int2, 1.0F, int3, false, true, true);
	}

	private void DoStartFrameStuffInternal(int int1, int int2, float float1, int int3, boolean boolean1, boolean boolean2, boolean boolean3) {
		GL32.glEnable(3042);
		GL32.glDepthFunc(519);
		int int4 = this.getScreenWidth();
		int int5 = this.getScreenHeight();
		if (!boolean3 && !boolean2) {
			int1 = int4;
		}

		if (!boolean3 && !boolean2) {
			int2 = int5;
		}

		if (!boolean3 && int3 != -1) {
			int1 /= IsoPlayer.numPlayers > 1 ? 2 : 1;
			int2 /= IsoPlayer.numPlayers > 2 ? 2 : 1;
		}

		GL32.glMatrixMode(5889);
		int int6;
		int int7;
		if (!boolean2) {
			while (this.stack > 0) {
				try {
					GL11.glPopMatrix();
					GL11.glPopAttrib();
					this.stack -= 2;
				} catch (OpenGLException openGLException) {
					int6 = GL11.glGetInteger(2992);
					while (int6-- > 0) {
						GL11.glPopAttrib();
					}

					int7 = GL11.glGetInteger(2980);
					while (int7-- > 1) {
						GL11.glPopMatrix();
					}

					this.stack = 0;
				}
			}
		}

		GL11.glAlphaFunc(516, 0.0F);
		GL11.glPushAttrib(2048);
		++this.stack;
		GL11.glPushMatrix();
		++this.stack;
		GL11.glLoadIdentity();
		if (!boolean3 && !boolean1) {
			GLU.gluOrtho2D(0.0F, (float)int1 * float1, (float)int2 * float1, 0.0F);
		} else {
			GLU.gluOrtho2D(0.0F, (float)int1, (float)int2, 0.0F);
		}

		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
		if (int3 != -1) {
			int7 = int1;
			int int8 = int2;
			int int9;
			if (boolean1) {
				int9 = int1;
				int6 = int2;
			} else {
				int9 = int4;
				int6 = int5;
				if (IsoPlayer.numPlayers > 1) {
					int9 = int4 / 2;
				}

				if (IsoPlayer.numPlayers > 2) {
					int6 = int5 / 2;
				}
			}

			if (boolean2) {
				int7 = int9;
				int8 = int6;
			}

			float float2 = 0.0F;
			float float3 = (float)(int9 * (int3 % 2));
			if (int3 >= 2) {
				float2 += (float)int6;
			}

			if (boolean1) {
				float2 = (float)(getInstance().getScreenHeight() - int8) - float2;
			}

			GL11.glViewport((int)float3, (int)float2, int7, int8);
			GL11.glEnable(3089);
			GL11.glScissor((int)float3, (int)float2, int7, int8);
			SpriteRenderer.instance.setRenderingPlayerIndex(int3);
		} else {
			GL11.glViewport(0, 0, int1, int2);
		}
	}

	public void DoPushIsoStuff(float float1, float float2, float float3, float float4, boolean boolean1) {
		float float5 = (Float)getInstance().FloatParamMap.get(0);
		float float6 = (Float)getInstance().FloatParamMap.get(1);
		float float7 = (Float)getInstance().FloatParamMap.get(2);
		double double1 = (double)float5;
		double double2 = (double)float6;
		double double3 = (double)float7;
		SpriteRenderState spriteRenderState = SpriteRenderer.instance.getRenderingState();
		int int1 = spriteRenderState.playerIndex;
		PlayerCamera playerCamera = spriteRenderState.playerCamera[int1];
		float float8 = playerCamera.RightClickX;
		float float9 = playerCamera.RightClickY;
		float float10 = playerCamera.getTOffX();
		float float11 = playerCamera.getTOffY();
		float float12 = playerCamera.DeferedX;
		float float13 = playerCamera.DeferedY;
		double1 -= (double)playerCamera.XToIso(-float10 - float8, -float11 - float9, 0.0F);
		double2 -= (double)playerCamera.YToIso(-float10 - float8, -float11 - float9, 0.0F);
		double1 += (double)float12;
		double2 += (double)float13;
		double double4 = (double)((float)playerCamera.OffscreenWidth / 1920.0F);
		double double5 = (double)((float)playerCamera.OffscreenHeight / 1920.0F);
		Matrix4f matrix4f = this.tempMatrix4f;
		matrix4f.setOrtho(-((float)double4) / 2.0F, (float)double4 / 2.0F, -((float)double5) / 2.0F, (float)double5 / 2.0F, -10.0F, 10.0F);
		PZGLUtil.pushAndLoadMatrix(5889, matrix4f);
		Matrix4f matrix4f2 = this.tempMatrix4f;
		float float14 = (float)(2.0 / Math.sqrt(2048.0));
		matrix4f2.scaling(0.047085002F);
		matrix4f2.scale((float)TileScale / 2.0F);
		matrix4f2.rotate(0.5235988F, 1.0F, 0.0F, 0.0F);
		matrix4f2.rotate(2.3561945F, 0.0F, 1.0F, 0.0F);
		double double6 = (double)float1 - double1;
		double double7 = (double)float2 - double2;
		matrix4f2.translate(-((float)double6), (float)((double)float3 - double3) * 2.5F, -((float)double7));
		if (boolean1) {
			matrix4f2.scale(-1.0F, 1.0F, 1.0F);
		} else {
			matrix4f2.scale(-1.5F, 1.5F, 1.5F);
		}

		matrix4f2.rotate(float4 + 3.1415927F, 0.0F, 1.0F, 0.0F);
		if (!boolean1) {
			matrix4f2.translate(0.0F, -0.48F, 0.0F);
		}

		PZGLUtil.pushAndLoadMatrix(5888, matrix4f2);
		GL11.glDepthRange(0.0, 1.0);
	}

	public void DoPushIsoParticleStuff(float float1, float float2, float float3) {
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		float float4 = (Float)getInstance().FloatParamMap.get(0);
		float float5 = (Float)getInstance().FloatParamMap.get(1);
		float float6 = (Float)getInstance().FloatParamMap.get(2);
		GL11.glLoadIdentity();
		double double1 = (double)float4;
		double double2 = (double)float5;
		double double3 = (double)float6;
		double double4 = (double)((float)Math.abs(getInstance().getOffscreenWidth(0)) / 1920.0F);
		double double5 = (double)((float)Math.abs(getInstance().getOffscreenHeight(0)) / 1080.0F);
		GL11.glLoadIdentity();
		GL11.glOrtho(-double4 / 2.0, double4 / 2.0, -double5 / 2.0, double5 / 2.0, -10.0, 10.0);
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glScaled(0.047085002064704895, 0.047085002064704895, 0.047085002064704895);
		GL11.glRotatef(62.65607F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslated(0.0, -2.7200000286102295, 0.0);
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glScalef(1.7099999F, 14.193F, 1.7099999F);
		GL11.glScalef(0.59F, 0.59F, 0.59F);
		GL11.glTranslated(-((double)float1 - double1), (double)float3 - double3, -((double)float2 - double2));
		GL11.glDepthRange(0.0, 1.0);
	}

	public void DoPopIsoStuff() {
		GL11.glEnable(3008);
		GL11.glDepthFunc(519);
		GL11.glDepthMask(false);
		GL11.glMatrixMode(5889);
		GL11.glPopMatrix();
		GL11.glMatrixMode(5888);
		GL11.glPopMatrix();
	}

	public void DoEndFrameStuff(int int1, int int2) {
		try {
			GL11.glPopAttrib();
			--this.stack;
			GL11.glMatrixMode(5889);
			GL11.glPopMatrix();
			--this.stack;
		} catch (Exception exception) {
			int int3 = GL11.glGetInteger(2992);
			while (int3-- > 0) {
				GL11.glPopAttrib();
			}

			GL11.glMatrixMode(5889);
			int int4 = GL11.glGetInteger(2980);
			while (int4-- > 1) {
				GL11.glPopMatrix();
			}

			this.stack = 0;
		}

		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
		GL11.glDisable(3089);
	}

	public void RenderOffScreenBuffer() {
		if (LuaManager.thread == null || !LuaManager.thread.bStep) {
			if (this.OffscreenBuffer.Current != null) {
				IndieGL.disableStencilTest();
				IndieGL.glDoStartFrame(width, height, 1.0F, -1);
				IndieGL.glDisable(3042);
				this.OffscreenBuffer.render();
				IndieGL.glDoEndFrame();
			}
		}
	}

	public void StartFrameText(int int1) {
		if (LuaManager.thread == null || !LuaManager.thread.bStep) {
			IndieGL.glDoStartFrame(IsoCamera.getScreenWidth(int1), IsoCamera.getScreenHeight(int1), 1.0F, int1, true);
			this.frameStage = 2;
		}
	}

	public boolean StartFrameUI() {
		if (LuaManager.thread != null && LuaManager.thread.bStep) {
			return false;
		} else {
			boolean boolean1 = true;
			if (UIManager.useUIFBO) {
				if (UIManager.defaultthread == LuaManager.debugthread) {
					this.UIRenderThisFrame = true;
				} else {
					this.UIRenderAccumulator += GameTime.getInstance().getMultiplier() / 1.6F;
					this.UIRenderThisFrame = this.UIRenderAccumulator >= 30.0F / (float)OptionUIRenderFPS;
				}

				if (this.UIRenderThisFrame) {
					SpriteRenderer.instance.startOffscreenUI();
					SpriteRenderer.instance.glBuffer(2, 0);
				} else {
					boolean1 = false;
				}
			}

			IndieGL.glDoStartFrame(width, height, 1.0F, -1);
			IndieGL.glClear(1024);
			UIManager.resize();
			this.frameStage = 3;
			return boolean1;
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
		return gameVersion.toString();
	}

	public GameVersion getGameVersion() {
		return gameVersion;
	}

	public String getSteamServerVersion() {
		return this.steamServerVersion;
	}

	public void DoFrameReady() {
		this.updateKeyboard();
	}

	public float getCurrentPlayerZoom() {
		int int1 = IsoCamera.frameState.playerIndex;
		return this.getZoom(int1);
	}

	public float getZoom(int int1) {
		return this.OffscreenBuffer != null ? this.OffscreenBuffer.zoom[int1] * ((float)TileScale / 2.0F) : 1.0F;
	}

	public float getNextZoom(int int1, int int2) {
		return this.OffscreenBuffer != null ? this.OffscreenBuffer.getNextZoom(int1, int2) : 1.0F;
	}

	public float getMinZoom() {
		return this.OffscreenBuffer != null ? this.OffscreenBuffer.getMinZoom() * ((float)TileScale / 2.0F) : 1.0F;
	}

	public float getMaxZoom() {
		return this.OffscreenBuffer != null ? this.OffscreenBuffer.getMaxZoom() * ((float)TileScale / 2.0F) : 1.0F;
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
			File file = (new File(string)).getAbsoluteFile();
			if (!file.exists()) {
				file.mkdir();
			}

			file = new File(file, "mods");
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
		if (boolean1) {
			RenderThread.invokeOnRenderContext(()->{
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
			});

			DebugLog.log("SafeMode is " + (SafeMode ? "on" : "off"));
		} else {
			SafeMode = SafeModeForced;
			this.OffscreenBuffer.bZoomEnabled = OptionZoom && !SafeModeForced;
		}
	}

	public void zoomLevelsChanged() {
		if (this.OffscreenBuffer.Current != null) {
			RenderThread.invokeOnRenderContext(()->{
				this.OffscreenBuffer.destroy();
				this.zoomOptionChanged(true);
			});
		}
	}

	public boolean isZoomEnabled() {
		return this.OffscreenBuffer.bZoomEnabled;
	}

	public void initFBOs() {
		if (OptionZoom && !SafeModeForced) {
			RenderThread.invokeOnRenderContext(this::supportsFBO);
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
		RenderThread.invokeOnRenderContext(()->{
			Display.setVSyncEnabled(boolean1);
		});
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
		return 2;
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

	public int getOptionFontSize() {
		return OptionFontSize;
	}

	public void setOptionFontSize(int int1) {
		OptionFontSize = PZMath.clamp(int1, 1, 5);
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

	public int getOptionClockSize() {
		return 2;
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

	public void setOptionClockSize(int int1) {
		if (int1 < 1) {
			int1 = 1;
		}

		if (int1 > 2) {
			int1 = 2;
		}

		OptionClockSize = int1;
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

	public boolean getOptionLockCursorToWindow() {
		return OptionLockCursorToWindow;
	}

	public void setOptionLockCursorToWindow(boolean boolean1) {
		OptionLockCursorToWindow = boolean1;
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

	public boolean getOptionModelTextureMipmaps() {
		return OptionModelTextureMipmaps;
	}

	public void setOptionModelTextureMipmaps(boolean boolean1) {
		OptionModelTextureMipmaps = boolean1;
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
				JoypadManager.instance.setControllerActive(controller.getGUID(), boolean1);
			}
		}
	}

	public boolean getOptionActiveController(String string) {
		return JoypadManager.instance.ActiveControllerGUIDs.contains(string);
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

	public int getOptionAimOutline() {
		return OptionAimOutline;
	}

	public void setOptionAimOutline(int int1) {
		OptionAimOutline = PZMath.clamp(int1, 1, 3);
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

	public void setOptionReloadRadialInstant(boolean boolean1) {
		OptionReloadRadialInstant = boolean1;
	}

	public boolean getOptionReloadRadialInstant() {
		return OptionReloadRadialInstant;
	}

	public void setOptionPanCameraWhileAiming(boolean boolean1) {
		OptionPanCameraWhileAiming = boolean1;
	}

	public boolean getOptionPanCameraWhileAiming() {
		return OptionPanCameraWhileAiming;
	}

	public String getOptionCycleContainerKey() {
		return OptionCycleContainerKey;
	}

	public void setOptionCycleContainerKey(String string) {
		OptionCycleContainerKey = string;
	}

	public boolean getOptionDropItemsOnSquareCenter() {
		return OptionDropItemsOnSquareCenter;
	}

	public void setOptionDropItemsOnSquareCenter(boolean boolean1) {
		OptionDropItemsOnSquareCenter = boolean1;
	}

	public boolean getOptionTimedActionGameSpeedReset() {
		return OptionTimedActionGameSpeedReset;
	}

	public void setOptionTimedActionGameSpeedReset(boolean boolean1) {
		OptionTimedActionGameSpeedReset = boolean1;
	}

	public int getOptionShoulderButtonContainerSwitch() {
		return OptionShoulderButtonContainerSwitch;
	}

	public void setOptionShoulderButtonContainerSwitch(int int1) {
		OptionShoulderButtonContainerSwitch = int1;
	}

	public boolean getOptionSingleContextMenu(int int1) {
		return OptionSingleContextMenu[int1];
	}

	public void setOptionSingleContextMenu(int int1, boolean boolean1) {
		OptionSingleContextMenu[int1] = boolean1;
	}

	public boolean getOptionAutoDrink() {
		return OptionAutoDrink;
	}

	public void setOptionAutoDrink(boolean boolean1) {
		OptionAutoDrink = boolean1;
	}

	public boolean getOptionCorpseShadows() {
		return OptionCorpseShadows;
	}

	public void setOptionCorpseShadows(boolean boolean1) {
		OptionCorpseShadows = boolean1;
	}

	public boolean getOptionLeaveKeyInIgnition() {
		return OptionLeaveKeyInIgnition;
	}

	public void setOptionLeaveKeyInIgnition(boolean boolean1) {
		OptionLeaveKeyInIgnition = boolean1;
	}

	public int getOptionSimpleClothingTextures() {
		return OptionSimpleClothingTextures;
	}

	public void setOptionSimpleClothingTextures(int int1) {
		OptionSimpleClothingTextures = PZMath.clamp(int1, 1, 3);
	}

	public boolean isOptionSimpleClothingTextures(boolean boolean1) {
		switch (OptionSimpleClothingTextures) {
		case 1: 
			return false;
		
		case 2: 
			return boolean1;
		
		default: 
			return true;
		
		}
	}

	public boolean getOptionSimpleWeaponTextures() {
		return OptionSimpleWeaponTextures;
	}

	public void setOptionSimpleWeaponTextures(boolean boolean1) {
		OptionSimpleWeaponTextures = boolean1;
	}

	public int getOptionIgnoreProneZombieRange() {
		return OptionIgnoreProneZombieRange;
	}

	public void setOptionIgnoreProneZombieRange(int int1) {
		OptionIgnoreProneZombieRange = PZMath.clamp(int1, 1, 5);
	}

	public float getIgnoreProneZombieRange() {
		switch (OptionIgnoreProneZombieRange) {
		case 1: 
			return -1.0F;
		
		case 2: 
			return 1.5F;
		
		case 3: 
			return 2.0F;
		
		case 4: 
			return 2.5F;
		
		case 5: 
			return 3.0F;
		
		default: 
			return -1.0F;
		
		}
	}

	private void readPerPlayerBoolean(String string, boolean[] booleanArray) {
		Arrays.fill(booleanArray, false);
		String[] stringArray = string.split(",");
		for (int int1 = 0; int1 < stringArray.length && int1 != 4; ++int1) {
			booleanArray[int1] = StringUtils.tryParseBoolean(stringArray[int1]);
		}
	}

	private String getPerPlayerBooleanString(boolean[] booleanArray) {
		return String.format("%b,%b,%b,%b", booleanArray[0], booleanArray[1], booleanArray[2], booleanArray[3]);
	}

	@Deprecated
	public void ResetLua(boolean boolean1, String string) throws IOException {
		this.ResetLua("default", string);
	}

	public void ResetLua(String string, String string2) throws IOException {
		if (SpriteRenderer.instance != null) {
			GameWindow.DrawReloadingLua = true;
			GameWindow.render();
			GameWindow.DrawReloadingLua = false;
		}

		RenderThread.setWaitForRenderState(false);
		SpriteRenderer.instance.notifyRenderStateQueue();
		ScriptManager.instance.Reset();
		ClothingDecals.Reset();
		BeardStyles.Reset();
		HairStyles.Reset();
		OutfitManager.Reset();
		AnimationSet.Reset();
		GameSounds.Reset();
		VehicleType.Reset();
		LuaEventManager.Reset();
		MapObjects.Reset();
		UIManager.init();
		SurvivorFactory.Reset();
		ProfessionFactory.Reset();
		TraitFactory.Reset();
		ChooseGameInfo.Reset();
		AttachedLocations.Reset();
		BodyLocations.Reset();
		ContainerOverlays.instance.Reset();
		BentFences.getInstance().Reset();
		BrokenFences.getInstance().Reset();
		TileOverlays.instance.Reset();
		LuaHookManager.Reset();
		CustomPerks.Reset();
		PerkFactory.Reset();
		CustomSandboxOptions.Reset();
		SandboxOptions.Reset();
		LuaManager.init();
		JoypadManager.instance.Reset();
		GameKeyboard.doLuaKeyPressed = true;
		Texture.nullTextures.clear();
		ZomboidFileSystem.instance.Reset();
		ZomboidFileSystem.instance.init();
		ZomboidFileSystem.instance.loadMods(string);
		ZomboidFileSystem.instance.loadModPackFiles();
		ModelManager.instance.loadModAnimations();
		Languages.instance.init();
		Translator.loadFiles();
		CustomPerks.instance.init();
		CustomPerks.instance.initLua();
		CustomSandboxOptions.instance.init();
		CustomSandboxOptions.instance.initInstance(SandboxOptions.instance);
		ScriptManager.instance.Load();
		ClothingDecals.init();
		BeardStyles.init();
		HairStyles.init();
		OutfitManager.init();
		try {
			TextManager.instance.Init();
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
		RenderThread.setWaitForRenderState(true);
		LuaEventManager.triggerEvent("OnGameBoot");
		LuaEventManager.triggerEvent("OnMainMenuEnter");
		LuaEventManager.triggerEvent("OnResetLua", string2);
	}

	public void DelayResetLua(String string, String string2) {
		this.m_delayResetLua_activeMods = string;
		this.m_delayResetLua_reason = string2;
	}

	public void CheckDelayResetLua() throws IOException {
		if (this.m_delayResetLua_activeMods != null) {
			String string = this.m_delayResetLua_activeMods;
			String string2 = this.m_delayResetLua_reason;
			this.m_delayResetLua_activeMods = null;
			this.m_delayResetLua_reason = null;
			this.ResetLua(string, string2);
		}
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
		this.objectHighlitedColor.set(colorInfo);
	}

	public String getSeenUpdateText() {
		return this.seenUpdateText;
	}

	public void setSeenUpdateText(String string) {
		this.seenUpdateText = string;
	}

	public boolean isToggleToAim() {
		return this.toggleToAim;
	}

	public void setToggleToAim(boolean boolean1) {
		this.toggleToAim = boolean1;
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

	public void setOptionUpdateSneakButton(boolean boolean1) {
		OptionUpdateSneakButton = boolean1;
	}

	public boolean getOptionUpdateSneakButton() {
		return OptionUpdateSneakButton;
	}

	public boolean isNewReloading() {
		return this.newReloading;
	}

	public void setNewReloading(boolean boolean1) {
		this.newReloading = boolean1;
	}

	public boolean isShowFirstTimeSneakTutorial() {
		return this.showFirstTimeSneakTutorial;
	}

	public void setShowFirstTimeSneakTutorial(boolean boolean1) {
		this.showFirstTimeSneakTutorial = boolean1;
	}

	public void setOptiondblTapJogToSprint(boolean boolean1) {
		OptiondblTapJogToSprint = boolean1;
	}

	public boolean isOptiondblTapJogToSprint() {
		return OptiondblTapJogToSprint;
	}

	public boolean isToggleToSprint() {
		return this.toggleToSprint;
	}

	public void setToggleToSprint(boolean boolean1) {
		this.toggleToSprint = boolean1;
	}

	public int getIsoCursorVisibility() {
		return this.isoCursorVisibility;
	}

	public void setIsoCursorVisibility(int int1) {
		this.isoCursorVisibility = int1;
	}

	public boolean getOptionShowCursorWhileAiming() {
		return OptionShowCursorWhileAiming;
	}

	public void setOptionShowCursorWhileAiming(boolean boolean1) {
		OptionShowCursorWhileAiming = boolean1;
	}

	public boolean gotNewBelt() {
		return this.gotNewBelt;
	}

	public void setGotNewBelt(boolean boolean1) {
		this.gotNewBelt = boolean1;
	}

	public void setAnimPopupDone(boolean boolean1) {
		this.bAnimPopupDone = boolean1;
	}

	public boolean isAnimPopupDone() {
		return this.bAnimPopupDone;
	}

	public void setModsPopupDone(boolean boolean1) {
		this.bModsPopupDone = boolean1;
	}

	public boolean isModsPopupDone() {
		return this.bModsPopupDone;
	}

	public boolean isRenderPrecipIndoors() {
		return OptionRenderPrecipIndoors;
	}

	public void setRenderPrecipIndoors(boolean boolean1) {
		OptionRenderPrecipIndoors = boolean1;
	}

	public boolean isCollideZombies() {
		return this.collideZombies;
	}

	public void setCollideZombies(boolean boolean1) {
		this.collideZombies = boolean1;
	}

	public boolean isFlashIsoCursor() {
		return this.flashIsoCursor;
	}

	public void setFlashIsoCursor(boolean boolean1) {
		this.flashIsoCursor = boolean1;
	}

	public boolean isOptionProgressBar() {
		return true;
	}

	public void setOptionProgressBar(boolean boolean1) {
		OptionProgressBar = boolean1;
	}

	public void setOptionLanguageName(String string) {
		OptionLanguageName = string;
	}

	public String getOptionLanguageName() {
		return OptionLanguageName;
	}

	public int getOptionRenderPrecipitation() {
		return OptionRenderPrecipitation;
	}

	public void setOptionRenderPrecipitation(int int1) {
		OptionRenderPrecipitation = int1;
	}

	public void setOptionAutoProneAtk(boolean boolean1) {
		OptionAutoProneAtk = boolean1;
	}

	public boolean isOptionAutoProneAtk() {
		return OptionAutoProneAtk;
	}

	public void setOption3DGroundItem(boolean boolean1) {
		Option3DGroundItem = boolean1;
	}

	public boolean isOption3DGroundItem() {
		return Option3DGroundItem;
	}

	public Object getOptionOnStartup(String string) {
		return optionsOnStartup.get(string);
	}

	public void setOptionOnStartup(String string, Object object) {
		optionsOnStartup.put(string, object);
	}

	public void countMissing3DItems() {
		ArrayList arrayList = ScriptManager.instance.getAllItems();
		int int1 = 0;
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			Item item = (Item)iterator.next();
			if (item.type != Item.Type.Weapon && item.type != Item.Type.Moveable && !item.name.contains("ZedDmg") && !item.name.contains("Wound") && !item.name.contains("MakeUp") && !item.name.contains("Bandage") && !item.name.contains("Hat") && !item.getObsolete() && StringUtils.isNullOrEmpty(item.worldObjectSprite) && StringUtils.isNullOrEmpty(item.worldStaticModel)) {
				System.out.println("Missing: " + item.name);
				++int1;
			}
		}

		System.out.println("total missing: " + int1 + "/" + arrayList.size());
	}
}
