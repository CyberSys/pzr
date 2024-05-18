package zombie;

import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundBank;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import sun.nio.cs.ArrayDecoder;
import sun.nio.cs.ArrayEncoder;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.audio.BaseSoundBank;
import zombie.audio.DummySoundBank;
import zombie.characters.IsoPlayer;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.Core;
import zombie.core.Language;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.input.Input;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.ZipLogs;
import zombie.core.opengl.RenderThread;
import zombie.core.physics.Bullet;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.raknet.VoiceManager;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.textures.TexturePackPage;
import zombie.core.znet.ServerBrowser;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.gameStates.AlphaWarningState;
import zombie.gameStates.GameLoadingState;
import zombie.gameStates.GameStateMachine;
import zombie.gameStates.MainScreenState;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LightingThread;
import zombie.iso.SliceY;
import zombie.iso.Vector2;
import zombie.iso.WorldStreamer;
import zombie.network.CoopMaster;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.scripting.ScriptManager;
import zombie.spnetwork.SinglePlayerClient;
import zombie.spnetwork.SinglePlayerServer;
import zombie.ui.TextManager;
import zombie.ui.UIManager;
import zombie.util.PublicServerUtil;
import zombie.vehicles.PolygonalMap2;


public class GameWindow {
	public static Input GameInput = new Input(1);
	static int PauseKeyDebounce = 0;
	public static int ActiveController = -1;
	public static int XLAxis = 0;
	public static int YLAxis = 0;
	public static int XRAxis = 0;
	public static int YRAxis = 0;
	public static boolean flipX = false;
	public static boolean flipY = false;
	public static boolean bFlipXY = false;
	public static boolean DEBUG_SAVE = false;
	private static boolean doRenderEvent = false;
	public static boolean OkToSaveOnExit = false;
	private static final int FRAMERATE = 60;
	public static String lastU = null;
	public static String lastP = null;
	public static String lastK = null;
	public static final String GAME_TITLE = "Project Zomboid";
	public static GameStateMachine states = new GameStateMachine();
	public static LineDrawer debugLine = new LineDrawer();
	public static boolean bDrawMouse = true;
	static Texture MousePointer;
	private static AlphaWarningState AlphaWarningState = null;
	public static boolean bServerDisconnected;
	public static boolean bLoadedAsClient = false;
	public static String kickReason;
	public static boolean DrawReloadingLua = false;
	private static String CacheDir = null;
	private static boolean finished;
	static boolean keyDown;
	public static JoypadManager.Joypad ActivatedJoyPad = null;
	public static boolean DoFrame = true;
	public static String version = "RC3";
	public static String UpDownR = "Y Rotation";
	public static String LeftRightR = "X Rotation";
	public static String UpDown = "Y Axis";
	public static String LeftRight = "X Axis";
	public static String TriggerL = "Z Axis";
	static int q = 0;
	static long lastFrame = 0L;
	static long lastFPS = 0L;
	public static double average10 = 0.0;
	static float total10 = 0.0F;
	static long[] last10 = new long[200];
	static int last10index = 0;
	static long fps = 0L;
	static long last = 0L;
	private static int NUM_FPS = 10;
	public static volatile boolean closeRequested;
	private static int FPS = 60;
	private static long period;
	private static boolean running;
	private static int MAX_FRAME_SKIPS;
	private static final int NO_DELAYS_PER_YIELD = 20;
	public static float averageFPS;
	private static final ThreadLocal stringUTF;

	public static void initApplet() throws Exception {
		Vector2 vector2 = new Vector2();
		vector2.x = 0.5F;
		vector2.y = 1.0F;
		vector2.normalize();
		float float1 = vector2.getLength();
		float float2 = vector2.x / 2.0F;
		initShared();
	}

	public static void initShared() throws Exception {
		String string = getCacheDir() + File.separator;
		File file = new File(string);
		if (!file.exists()) {
			file.mkdirs();
		}

		string = string + "2133243254543.log";
		TextureID.bUseCompression = false;
		TexturePackPage.bIgnoreWorldItemTextures = true;
		LoadTexturePack("UI");
		LoadTexturePack("UI2");
		LoadTexturePack("IconsMoveables");
		LoadTexturePack("RadioIcons");
		LoadTexturePack("ApComUI");
		LoadTexturePack("Mechanics");
		LoadTexturePack("WeatherFx");
		ScriptManager.instance.Load();
		IsoGridSquare.TotalChecksum = IsoGridSquare.Checksum;
		IndieLogger.init();
		IndieLogger.Log("PZ Debug Logging started");
		TraitFactory.init();
		ProfessionFactory.init();
		PerkFactory.init();
		Rand.init();
		TexturePackPage.bIgnoreWorldItemTextures = false;
		TextureID.bUseCompression = TextureID.bUseCompressionOption;
		TextManager.instance.Init();
		IndieLogger.Log("Initialised TextManager");
		MousePointer = Texture.getSharedTexture("media/ui/mouseArrow.png");
		AlphaWarningState = new AlphaWarningState();
		if (!GameClient.bClient) {
			states.States.add(AlphaWarningState);
		}

		if (GameClient.bClient) {
			states.States.add(new GameLoadingState());
		} else {
			states.States.add(new MainScreenState());
		}

		states.LoopToState = 1;
		GameInput.initControllers();
		int int1 = GameInput.getControllerCount();
		file = new File(getCacheDir() + "\\joypad.ini");
		if (file.exists()) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
			try {
				String string2 = "";
				while (string2 != null) {
					string2 = bufferedReader.readLine();
					if (string2 != null && string2.trim().length() != 0) {
						String[] stringArray = string2.split("=");
						if (stringArray.length == 2) {
							stringArray[0] = stringArray[0].trim();
							stringArray[1] = stringArray[1].trim();
							if (stringArray[0].equals("LUpDown")) {
								UpDown = stringArray[1];
							}

							if (stringArray[0].equals("RLeftRight")) {
								LeftRightR = stringArray[1];
							}

							if (stringArray[0].equals("RUpDown")) {
								UpDownR = stringArray[1];
							}

							if (stringArray[0].equals("LLeftRight")) {
								LeftRight = stringArray[1];
							}

							if (stringArray[0].equals("Triggers")) {
								TriggerL = stringArray[1];
							}
						}
					}
				}
			} finally {
				bufferedReader.close();
			}
		}

		DebugLog.log("----------------------------------------------");
		DebugLog.log("--	Controller setup - use this info to	 ");
		DebugLog.log("--	edit joypad.ini in save directory	   ");
		DebugLog.log("----------------------------------------------");
		for (int int2 = 0; int2 < int1; ++int2) {
			DebugLog.log("----------------------------------------------");
			DebugLog.log("--  Joypad: " + GameInput.getController(int2).getName());
			DebugLog.log("----------------------------------------------");
			int int3 = GameInput.getAxisCount(int2);
			String string3;
			int int4;
			if (int3 > 1) {
				DebugLog.log("----------------------------------------------");
				DebugLog.log("--	Axis definitions for controller " + int2);
				DebugLog.log("----------------------------------------------");
				ActiveController = int2;
				for (int4 = 0; int4 < int3; ++int4) {
					string3 = GameInput.getAxisName(int2, int4);
					DebugLog.log("Testing for axis: " + string3);
					if (string3.equals(UpDown)) {
						YLAxis = int4;
					}

					if (string3.equals(LeftRight)) {
						XLAxis = int4;
					}

					if (string3.equals(LeftRightR)) {
						XRAxis = int4;
					}

					if (string3.equals(UpDownR)) {
						YRAxis = int4;
					}
				}
			}

			int3 = GameInput.getButtonCount(int2);
			if (int3 > 1) {
				GameInput.getController(int2).poll();
				DebugLog.log("----------------------------------------------");
				DebugLog.log("--	Button definitions for controller " + int2);
				DebugLog.log("----------------------------------------------");
				for (int4 = 0; int4 < int3; ++int4) {
					string3 = GameInput.getButtonName(int2, int4);
					DebugLog.log("Testing for button: " + string3);
				}
			}
		}

		GL11.glTexParameteri(3553, 10241, 9728);
		GL11.glTexParameteri(3553, 10240, 9728);
		TextureID.UseFiltering = false;
		Texture.getSharedTexture("media/white.png");
		TextureID.UseFiltering = true;
		SpriteRenderer.instance = new SpriteRenderer();
		SpriteRenderer.instance.create();
	}

	@Deprecated
	public static void initSharedServer() throws Exception {
		String string = getCacheDir() + File.separator;
		File file = new File(string);
		if (!file.exists()) {
			file.mkdirs();
		}

		string = string + "2133243254543.log";
		IndieLogger.init();
		IndieLogger.Log("PZ Debug Logging started");
		TraitFactory.init();
		ProfessionFactory.init();
		PerkFactory.init();
		Rand.init();
		MousePointer = Texture.getSharedTexture("media/ui/mouseArrow.png");
		states.LoopToState = 1;
	}

	public static void logic() {
		if (GameClient.bClient) {
			try {
				GameClient.instance.update();
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}

		try {
			SinglePlayerServer.update();
			SinglePlayerClient.update();
		} catch (Throwable throwable) {
			ExceptionLogger.logException(throwable);
		}

		SteamUtils.runLoop();
		Mouse.update();
		GameKeyboard.update();
		if (CoopMaster.instance != null) {
			CoopMaster.instance.update();
		}

		if (IsoPlayer.players[0] != null) {
			IsoPlayer.instance = IsoPlayer.players[0];
			IsoCamera.CamCharacter = IsoPlayer.players[0];
		}

		UIManager.update();
		VoiceManager.instance.update();
		LineDrawer lineDrawer = debugLine;
		LineDrawer.clear();
		if (!FrameLoader.bDedicated) {
			GameInput.poll(Core.getInstance().getOffscreenWidth(0), Core.getInstance().getOffscreenHeight(0));
		}

		if (!FrameLoader.bDedicated && JoypadManager.instance.isAPressed(-1)) {
			for (int int1 = 0; int1 < JoypadManager.instance.JoypadList.size(); ++int1) {
				JoypadManager.Joypad joypad = (JoypadManager.Joypad)JoypadManager.instance.JoypadList.get(int1);
				if (joypad.isAPressed()) {
					if (ActivatedJoyPad == null) {
						ActivatedJoyPad = joypad;
					}

					if (IsoPlayer.instance != null) {
						LuaEventManager.triggerEvent("OnJoypadActivate", joypad.getID());
					} else {
						AlphaWarningState.messageTime = 0.0F;
						LuaEventManager.triggerEvent("OnJoypadActivateUI", joypad.getID());
					}

					break;
				}
			}
		}

		if (!FrameLoader.bDedicated) {
			SoundManager.instance.Update();
		}

		boolean boolean1 = true;
		if (GameClient.IsClientPaused()) {
			boolean1 = false;
		}

		if (UIManager.getSpeedControls() != null && UIManager.getSpeedControls().getCurrentGameSpeed() == 0) {
			boolean1 = false;
		}

		MapCollisionData.instance.updateGameState();
		long long1 = System.nanoTime();
		if (boolean1) {
			states.update();
		}

		long long2 = System.nanoTime();
		if (!boolean1 && IsoPlayer.instance != null) {
			IsoCamera.update();
		}

		if (GameLoadingState.bDone) {
			LuaEventManager.triggerEvent("OnTickEvenPaused", 0.0);
		}

		UIManager.resize();
	}

	public static void render() {
		if (!PerformanceSettings.LightingThread) {
			try {
				GameTime.getInstance().lightingUpdate();
				boolean boolean1 = true;
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					if (IsoPlayer.players[int1] != null && !LightingThread.instance.DoLightingUpdate(IsoWorld.instance.CurrentCell, int1)) {
						boolean1 = false;
						break;
					}
				}

				if (boolean1) {
					IsoCell.bReadAltLight = !IsoCell.bReadAltLight;
				}
			} catch (InterruptedException interruptedException) {
			}
		}

		++IsoCamera.frameState.frameCount;
		IsoObjectPicker.Instance.StartRender();
		long long1 = System.nanoTime();
		states.render();
	}

	public static void mainServer() {
		LuaManager.init();
		try {
			LuaManager.LoadDirBase();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		ZomboidGlobals.Load();
		GameServer.bServer = true;
		FrameLoader.bDedicated = true;
		try {
			initserver();
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}

		runserver();
	}

	public static void mainaa(String[] stringArray) throws Exception {
		ZomboidFileSystem zomboidFileSystem = ZomboidFileSystem.instance;
		zomboidFileSystem.init();
		String string = System.getProperty("server");
		String string2 = System.getProperty("client");
		String string3 = System.getProperty("fullscreen");
		String string4 = System.getProperty("debug");
		String string5 = System.getProperty("xres");
		String string6 = System.getProperty("yres");
		if (string3 != null) {
			FrameLoader.bFullscreen = true;
		}

		if (string4 != null) {
			Core.bDebug = true;
		}

		if (string5 != null) {
			FrameLoader.FullX = Integer.parseInt(string5);
		}

		if (string6 != null) {
			FrameLoader.FullY = Integer.parseInt(string6);
		}

		String string7 = System.getProperty("graphiclevel");
		if (string7 != null) {
			Core.getInstance().nGraphicLevel = Integer.parseInt(string7);
		}

		if (string != null && string.equals("true")) {
			GameServer.bServer = true;
		}

		if (string2 != null) {
			FrameLoader.IP = string2;
		}

		if (GameServer.bServer) {
		}

		int int1 = Display.getDesktopDisplayMode().getWidth();
		int int2 = Display.getDesktopDisplayMode().getHeight();
		Preferences preferences = Preferences.userNodeForPackage(FrameLoader.class);
		Display.setResizable(true);
		init(false);
		run();
		System.exit(0);
	}

	public static void maina(boolean boolean1, int int1, int int2, int int3) throws Exception {
		String string = System.getProperty("debug");
		String string2 = System.getProperty("nosave");
		if (string2 != null) {
			Core.getInstance().setNoSave(true);
		}

		if (string != null) {
			Core.bDebug = true;
		}

		if (!Core.SoundDisabled) {
			FMODManager.instance.init();
		}

		DebugOptions.instance.load();
		SoundManager.instance = (BaseSoundManager)(Core.SoundDisabled ? new DummySoundManager() : new SoundManager());
		AmbientStreamManager.instance = (BaseAmbientStreamManager)(Core.SoundDisabled ? new DummyAmbientStreamManager() : new AmbientStreamManager());
		BaseSoundBank.instance = (BaseSoundBank)(Core.SoundDisabled ? new DummySoundBank() : new FMODSoundBank());
		Core.getInstance().nGraphicLevel = int3;
		int int4;
		if (!Core.getInstance().loadOptions()) {
			int4 = Runtime.getRuntime().availableProcessors();
			if (int4 == 1) {
				PerformanceSettings.LightingFrameSkip = 3;
			} else if (int4 == 2) {
				PerformanceSettings.LightingFrameSkip = 2;
			} else if (int4 <= 4) {
				PerformanceSettings.LightingFrameSkip = 1;
			}

			Display.setFullscreen(false);
			Display.setResizable(false);
			if (Display.getDesktopDisplayMode().getWidth() > 1280 && Display.getDesktopDisplayMode().getHeight() > 1080) {
				FrameLoader.FullX = 1280;
				FrameLoader.FullY = 720;
				Core.getInstance().init(1280, 720);
				Core.getInstance().saveOptions();
			} else {
				Core.getInstance().init(Core.width, Core.height);
			}

			if (!GLContext.getCapabilities().GL_ATI_meminfo && !GLContext.getCapabilities().GL_NVX_gpu_memory_info) {
				DebugLog.log("Unable to determine available GPU memory, texture compression defaults to on");
				TextureID.bUseCompressionOption = true;
				TextureID.bUseCompression = true;
			}

			DebugLog.log("Init language : " + System.getProperty("user.language"));
			Translator.setLanguage(Language.FromString(System.getProperty("user.language").toUpperCase()));
		} else {
			Core.getInstance().init(Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight());
		}

		VoiceManager.instance.loadConfig();
		TextureID.bUseCompressionOption = Core.SafeModeForced || Core.getInstance().getOptionTextureCompression();
		TextureID.bUseCompression = TextureID.bUseCompressionOption;
		if (GLContext.getCapabilities().GL_ATI_meminfo) {
			int4 = GL11.glGetInteger(34812);
			DebugLog.log("ATI: available texture memory is " + int4 / 1024 + " MB");
		}

		if (GLContext.getCapabilities().GL_NVX_gpu_memory_info) {
			int4 = GL11.glGetInteger(36937);
			DebugLog.log("NVIDIA: current available GPU memory is " + int4 / 1024 + " MB");
			int4 = GL11.glGetInteger(36935);
			DebugLog.log("NVIDIA: dedicated available GPU memory is " + int4 / 1024 + " MB");
			int4 = GL11.glGetInteger(36936);
			DebugLog.log("NVIDIA: total available GPU memory is " + int4 / 1024 + " MB");
		}

		SoundManager.instance.setSoundVolume((float)Core.getInstance().getOptionSoundVolume() / 10.0F);
		SoundManager.instance.setMusicVolume((float)Core.getInstance().getOptionMusicVolume() / 10.0F);
		SoundManager.instance.setAmbientVolume((float)Core.getInstance().getOptionAmbientVolume() / 10.0F);
		SoundManager.instance.setVehicleEngineVolume((float)Core.getInstance().getOptionVehicleEngineVolume() / 10.0F);
		ZomboidFileSystem.instance.init();
		String string3 = System.getProperty("server");
		String string4 = System.getProperty("client");
		FrameLoader.bFullscreen = boolean1;
		String string5 = System.getProperty("nozombies");
		if (string5 != null) {
			IsoWorld.NoZombies = true;
		}

		FrameLoader.FullX = int1;
		FrameLoader.FullY = int2;
		if (string3 != null && string3.equals("true")) {
			GameServer.bServer = true;
		}

		if (string4 != null) {
		}

		if (GameServer.bServer) {
		}

		int int5 = Display.getDesktopDisplayMode().getWidth();
		int int6 = Display.getDesktopDisplayMode().getHeight();
		Preferences preferences = Preferences.userNodeForPackage(FrameLoader.class);
		init(false);
		run();
		ZipLogs.addZipFile(true);
		System.exit(0);
	}

	public static long getTime() {
		return System.nanoTime() / 1000000L;
	}

	public int getDelta() {
		long long1 = getTime();
		int int1 = (int)(long1 - lastFrame);
		lastFrame = long1;
		return int1;
	}

	public static void start() {
		lastFPS = getTime();
	}

	public static void updateFPS(long long1) {
		last10[last10index++] = long1;
		if (last10index >= 5) {
			last10index = 0;
		}

		average10 = 0.0;
		long long2 = 0L;
		long long3 = 0L;
		float float1 = 11110.0F;
		float float2 = -11110.0F;
		for (int int1 = 0; int1 < 50; ++int1) {
			if (last10[int1] != 0L) {
				++long3;
				long2 += last10[int1];
				if ((float)last10[int1] < float1) {
					float1 = (float)last10[int1];
				}

				if ((float)last10[int1] > float2) {
					float2 = (float)last10[int1];
				}
			}
		}

		if (long3 > 0L) {
			average10 = (double)((float)(long2 / long3));
		} else {
			average10 = 0.01666666753590107;
		}

		GameTime.instance.FPSMultiplier2 = 1.0F;
		fps = 0L;
		++fps;
	}

	public static long readLong(DataInputStream dataInputStream) throws EOFException, IOException {
		int int1 = dataInputStream.read();
		int int2 = dataInputStream.read();
		int int3 = dataInputStream.read();
		int int4 = dataInputStream.read();
		int int5 = dataInputStream.read();
		int int6 = dataInputStream.read();
		int int7 = dataInputStream.read();
		int int8 = dataInputStream.read();
		if ((int1 | int2 | int3 | int4 | int5 | int6 | int7 | int8) < 0) {
			throw new EOFException();
		} else {
			return (long)((int1 << 0) + (int2 << 8) + (int3 << 16) + (int4 << 24) + (int5 << 32) + (int6 << 40) + (int7 << 48) + (int8 << 56));
		}
	}

	public static int readInt(DataInputStream dataInputStream) throws EOFException, IOException {
		int int1 = dataInputStream.read();
		int int2 = dataInputStream.read();
		int int3 = dataInputStream.read();
		int int4 = dataInputStream.read();
		if ((int1 | int2 | int3 | int4) < 0) {
			throw new EOFException();
		} else {
			return (int1 << 0) + (int2 << 8) + (int3 << 16) + (int4 << 24);
		}
	}

	public static void run() {
		long long1 = 0L;
		int int1 = 0;
		long long2 = 0L;
		Core.TileScale = Core.getInstance().getOptionTexture2x() ? 2 : 1;
		if (Core.SafeModeForced) {
			Core.TileScale = 1;
		}

		IsoCamera.init();
		if (Core.TileScale == 1) {
			LoadTexturePack("Tiles1x");
			LoadTexturePack("JumboTrees1x");
			TextureID.bUseCompression = false;
			LoadTexturePack("Tiles1x.floor");
			TextureID.bUseCompression = TextureID.bUseCompressionOption;
		}

		if (Core.TileScale == 2) {
			LoadTexturePack("Tiles2x");
			LoadTexturePack("JumboTrees2x");
			TextureID.bUseCompression = false;
			LoadTexturePack("Tiles2x.floor");
			TextureID.bUseCompression = TextureID.bUseCompressionOption;
		}

		LoadTexturePackDDS("Characters");
		if (Texture.getSharedTexture("TileIndieStoneTentFrontLeft") == null) {
			throw new RuntimeException("Rebuild Tiles.pack with \"1 Include This in .pack\" as individual images not tilesheets");
		} else {
			DebugLog.log("LOADED UP A TOTAL OF " + Texture.totalTextureID + " TEXTURES");
			if (!SteamUtils.isSteamModeEnabled()) {
				PublicServerUtil.init();
			}

			VoiceManager.instance.InitVMClient();
			LuaEventManager.triggerEvent("OnGameBoot");
			boolean boolean1 = false;
			start();
			RenderThread.init();
			float float1 = 0.0F;
			long long3 = System.nanoTime();
			float[] floatArray = new float[20];
			int int2 = 0;
			for (int int3 = 0; int3 < 20; ++int3) {
				floatArray[int3] = (float)PerformanceSettings.LockFPS;
			}

			while (!Display.isCloseRequested() && !closeRequested) {
				long long4 = System.nanoTime();
				period = 1000000000L / (long)PerformanceSettings.LockFPS;
				try {
					long2 = 0L;
					logic();
					long long5 = System.nanoTime();
					Core.getInstance().setScreenSize(Display.getWidth(), Display.getHeight());
					long long6 = System.nanoTime();
					render();
					if (doRenderEvent) {
						LuaEventManager.triggerEvent("OnRenderTick");
					}

					Core.getInstance().DoFrameReady();
					long long7 = System.nanoTime();
					LightingThread.instance.update();
					long long8 = System.nanoTime();
					if (Core.bDebug && Keyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger")) && !boolean1) {
						LuaManager.thread.bStep = true;
						LuaManager.thread.bStepInto = true;
						boolean1 = true;
					}

					if (!Keyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
						boolean1 = false;
					}

					long long9 = System.nanoTime();
					long long10 = long9 - long3;
					updateFPS(long10);
					long long11 = period - long10 - long1;
					if (long11 > 0L && PerformanceSettings.LockFPS != 61) {
						try {
							Thread.sleep(long11 / 1000000L);
						} catch (InterruptedException interruptedException) {
						}

						long1 = System.nanoTime() - long9 - long11;
					} else {
						long2 -= long11;
						long1 = 0L;
						++int1;
						if (int1 >= 20) {
							Thread.yield();
							int1 = 0;
						}
					}

					long3 = System.nanoTime();
					int int4 = 0;
					if (PerformanceSettings.AutomaticFrameSkipping) {
						while (long2 > period && int4 < PerformanceSettings.MaxAutomaticFrameSkips) {
							long2 -= period;
							logic();
							++int4;
						}
					} else if (PerformanceSettings.ManualFrameSkips > 0) {
						for (int int5 = 0; int5 < PerformanceSettings.ManualFrameSkips; ++int5) {
							long2 -= period;
							logic();
						}
					}

					long long12 = System.nanoTime();
					float float2 = (float)(long12 - long4) / 1000000.0F;
					if (float2 > 0.0F) {
						averageFPS = 0.0F;
						float float3 = 1000.0F / (float)PerformanceSettings.LockFPS / float2;
						float1 = float3 * (float)PerformanceSettings.LockFPS;
						floatArray[int2] = float1;
						++int2;
						if (int2 >= 5) {
							int2 = 0;
						}

						for (int int6 = 0; int6 < 5; ++int6) {
							averageFPS += floatArray[int6];
						}

						averageFPS /= 5.0F;
						GameTime.instance.FPSMultiplier = 60.0F / float1;
					}

					long long13 = System.nanoTime();
					long long14 = long13 - long4;
				} catch (Exception exception) {
					Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
				}
			}

			if (GameClient.bClient) {
				WorldStreamer.instance.stop();
				GameClient.instance.doDisconnect("Quitting");
				VoiceManager.instance.DeinitVMClient();
			}

			if (OkToSaveOnExit) {
				try {
					WorldStreamer.instance.quit();
				} catch (Exception exception2) {
					exception2.printStackTrace();
				}

				try {
					if (GameClient.bClient && GameClient.connection != null) {
						GameClient.connection.username = null;
					}

					save(true);
				} catch (Exception exception3) {
					exception3.printStackTrace();
				}

				try {
					if (IsoWorld.instance.CurrentCell != null) {
						LuaEventManager.triggerEvent("OnPostSave");
					}
				} catch (Exception exception4) {
					exception4.printStackTrace();
				}

				try {
					LightingThread.instance.stop();
					MapCollisionData.instance.stop();
					ZombiePopulationManager.instance.stop();
					PolygonalMap2.instance.stop();
				} catch (Exception exception5) {
					exception5.printStackTrace();
				}
			}

			if (GameClient.bClient) {
				WorldStreamer.instance.stop();
				GameClient.instance.doDisconnect("Quitting");
				try {
					Thread.sleep(500L);
				} catch (InterruptedException interruptedException2) {
					interruptedException2.printStackTrace();
				}
			}

			RenderThread.borrowContext();
			Display.destroy();
			GameClient.instance.Shutdown();
			SteamUtils.shutdown();
			ZipLogs.addZipFile(true);
			System.exit(0);
		}
	}

	public static void runserver() {
		doServer();
		LoadTexturePack("Tiles");
		LuaEventManager.triggerEvent("OnGameBoot");
		start();
		while (true) {
			try {
				logic();
				Thread.sleep(16L);
			} catch (Exception exception) {
				JOptionPane.showMessageDialog((Component)null, exception.getStackTrace(), "Error: " + exception.getMessage(), 0);
				Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
				try {
					save(true);
				} catch (FileNotFoundException fileNotFoundException) {
					Logger.getLogger(FrameLoader.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
				} catch (IOException ioException) {
					Logger.getLogger(FrameLoader.class.getName()).log(Level.SEVERE, (String)null, ioException);
				}

				Display.destroy();
				System.exit(0);
				return;
			}
		}
	}

	private static void doServer() {
	}

	public static TexturePackPage LoadTexturePack(String string) {
		DebugLog.log("texturepack: loading " + string);
		if (SpriteRenderer.instance != null && RenderThread.RenderThread == null) {
			Core.getInstance().StartFrame();
			Core.getInstance().EndFrame(0);
			Core.getInstance().StartFrameUI();
			SpriteRenderer.instance.render((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F);
			TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), Translator.getText("UI_Loading_Texturepack", string), 1.0, 1.0, 1.0, 1.0);
			Core.getInstance().EndFrameUI();
		}

		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(ZomboidFileSystem.instance.getString("media/texturepacks/" + string + ".pack"));
		} catch (FileNotFoundException fileNotFoundException) {
			Logger.getLogger(GameLoadingState.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
			return null;
		}

		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		try {
			RenderThread.borrowContext();
			int int1 = TexturePackPage.readInt(bufferedInputStream);
			TexturePackPage texturePackPage = null;
			for (int int2 = 0; int2 < int1; ++int2) {
				if (int2 == 3716) {
					boolean boolean1 = false;
				}

				texturePackPage = new TexturePackPage();
				if (int2 % 100 == 0 && SpriteRenderer.instance != null && RenderThread.RenderThread == null) {
					Core.getInstance().StartFrame();
					Core.getInstance().EndFrame();
					Core.getInstance().StartFrameUI();
					TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), Translator.getText("UI_Loading_Texturepack", string), 1.0, 1.0, 1.0, 1.0);
					Core.getInstance().EndFrameUI();
					Display.update();
				}

				texturePackPage.loadFromPackFile(bufferedInputStream);
				if (int2 % 5 == 0) {
					Display.processMessages();
				}
			}

			DebugLog.log("texturepack: finished loading " + string);
			TexturePackPage texturePackPage2 = texturePackPage;
			return texturePackPage2;
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				bufferedInputStream.close();
			} catch (Exception exception2) {
			}

			RenderThread.returnContext();
		}

		Texture.nullTextures.clear();
		return null;
	}

	public static void LoadTexturePackDDS(String string) {
		DebugLog.log("texturepack: loading " + string);
		if (SpriteRenderer.instance != null) {
			Core.getInstance().StartFrame();
			Core.getInstance().EndFrame(0);
			Core.getInstance().StartFrameUI();
			SpriteRenderer.instance.render((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F);
			TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), Translator.getText("UI_Loading_Texturepack", string), 1.0, 1.0, 1.0, 1.0);
			Core.getInstance().EndFrameUI();
		}

		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(ZomboidFileSystem.instance.getString("media/texturepacks/" + string + ".pack"));
		} catch (FileNotFoundException fileNotFoundException) {
			Logger.getLogger(GameLoadingState.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
		}

		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		try {
			int int1 = TexturePackPage.readInt(bufferedInputStream);
			for (int int2 = 0; int2 < int1; ++int2) {
				if (int2 == 3716) {
					boolean boolean1 = false;
				}

				TexturePackPage texturePackPage = new TexturePackPage();
				if (int2 % 100 == 0 && SpriteRenderer.instance != null) {
					Core.getInstance().StartFrame();
					Core.getInstance().EndFrame();
					Core.getInstance().StartFrameUI();
					TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), Translator.getText("UI_Loading_Texturepack", string), 1.0, 1.0, 1.0, 1.0);
					Core.getInstance().EndFrameUI();
					Display.update();
				}

				texturePackPage.loadFromPackFileDDS(bufferedInputStream);
			}

			DebugLog.log("texturepack: finished loading " + string);
		} catch (Exception exception) {
			DebugLog.log("media/texturepacks/" + string + ".pack");
			exception.printStackTrace();
		} finally {
			try {
				bufferedInputStream.close();
			} catch (Exception exception2) {
			}
		}

		Texture.nullTextures.clear();
	}

	private static void cleanup() {
		Display.destroy();
		AL.destroy();
	}

	private static void installRequiredLibrary(String string, String string2) {
		if ((new File(string)).exists()) {
			DebugLog.log("Attempting to install " + string2);
			DebugLog.log("Running " + string + ".");
			ProcessBuilder processBuilder = new ProcessBuilder(new String[]{string, "/quiet", "/norestart"});
			try {
				Process process = processBuilder.start();
				int int1 = process.waitFor();
				DebugLog.log("Process exited with code " + int1);
				return;
			} catch (IOException ioException) {
				ioException.printStackTrace();
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}

		DebugLog.log("Please install " + string2);
	}

	private static void checkRequiredLibraries() {
		if (System.getProperty("os.name").startsWith("Win")) {
			String string;
			String string2;
			String string3;
			String string4;
			if (System.getProperty("sun.arch.data.model").equals("64")) {
				string = "Lighting64";
				string2 = "_CommonRedist\\vcredist\\2010\\vcredist_x64.exe";
				string3 = "_CommonRedist\\vcredist\\2012\\vcredist_x64.exe";
				string4 = "_CommonRedist\\vcredist\\2013\\vcredist_x64.exe";
			} else {
				string = "Lighting32";
				string2 = "_CommonRedist\\vcredist\\2010\\vcredist_x86.exe";
				string3 = "_CommonRedist\\vcredist\\2012\\vcredist_x86.exe";
				string4 = "_CommonRedist\\vcredist\\2013\\vcredist_x86.exe";
			}

			try {
				System.loadLibrary(string);
			} catch (UnsatisfiedLinkError unsatisfiedLinkError) {
				DebugLog.log("Error loading " + string + ".dll.  Your system may be missing a required DLL.");
				installRequiredLibrary(string2, "the Microsoft Visual C++ 2010 Redistributable.");
				installRequiredLibrary(string3, "the Microsoft Visual C++ 2012 Redistributable.");
				installRequiredLibrary(string4, "the Microsoft Visual C++ 2013 Redistributable.");
			}
		}
	}

	private static void init(boolean boolean1) throws Exception {
		checkRequiredLibraries();
		SteamUtils.init();
		ServerBrowser.init();
		SteamFriends.init();
		SteamWorkshop.init();
		RakNetPeerInterface.init();
		LightingJNI.init();
		ZombiePopulationManager.init();
		Bullet.init();
		Display.setTitle("Project Zomboid");
		int int1 = Runtime.getRuntime().availableProcessors();
		if (Core.bMultithreadedRendering) {
			Core.bMultithreadedRendering = int1 > 1;
		}

		if (Core.SafeMode) {
		}

		String string = getCacheDir() + File.separator;
		File file = new File(string);
		if (!file.exists()) {
			file.mkdirs();
		}

		string = string + "2133243254543.log";
		new File(string);
		ZomboidFileSystem.instance.loadMods();
		Translator.loadFiles();
		initShared();
		DoLoadingText(Translator.getText("UI_Loading_Lua"));
		LuaManager.init();
		LuaManager.LoadDirBase();
		ZomboidGlobals.Load();
		LuaEventManager.triggerEvent("OnLoadSoundBanks");
	}

	private static void initserver() throws Exception {
		String string = getCacheDir() + File.separator;
		File file = new File(string);
		if (!file.exists()) {
			file.mkdirs();
		}

		string = string + "2133243254543.log";
		new File(string);
		initSharedServer();
	}

	public static void save(boolean boolean1) throws FileNotFoundException, IOException {
		save(boolean1, true);
	}

	public static void savePlayer() {
		try {
			IsoWorld.instance.CurrentCell.savePlayer();
			for (int int1 = 1; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && !player.isDead()) {
					String string = player.SaveFileName;
					if (string == null) {
						string = IsoPlayer.getUniqueFileName();
					}

					player.save(string);
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public static void save(boolean boolean1, boolean boolean2) throws FileNotFoundException, IOException {
		if (!Core.getInstance().isNoSave()) {
			if (IsoWorld.instance.CurrentCell != null && !"LastStand".equals(Core.getInstance().getGameMode()) && !"Tutorial".equals(Core.getInstance().getGameMode())) {
				File file = new File(getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_ver.bin");
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
				IsoWorld world = IsoWorld.instance;
				short short1 = 143;
				dataOutputStream.writeInt(short1);
				WriteString(dataOutputStream, Core.GameMap);
				WriteString(dataOutputStream, IsoWorld.instance.getDifficulty());
				dataOutputStream.flush();
				dataOutputStream.close();
				file = new File(getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_sand.bin");
				fileOutputStream = new FileOutputStream(file);
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				if (SliceY.SliceBuffer == null) {
					SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
				}

				SliceY.SliceBuffer.rewind();
				SandboxOptions.instance.save(SliceY.SliceBuffer);
				bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
				LuaEventManager.triggerEvent("OnSave");
				RenderThread.borrowContext();
				if (!FrameLoader.bDedicated) {
					Core.getInstance().TakeScreenshot();
				}

				file = new File(getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map.bin");
				fileOutputStream = null;
				fileOutputStream = new FileOutputStream(file);
				try {
					dataOutputStream = new DataOutputStream(fileOutputStream);
					IsoWorld.instance.CurrentCell.save(dataOutputStream, boolean1);
					fileOutputStream.close();
				} catch (Exception exception) {
					Logger.getLogger(FrameLoader.class.getName()).log(Level.SEVERE, (String)null, exception);
				} finally {
					fileOutputStream.close();
				}

				try {
					MapCollisionData.instance.save();
					if (!bLoadedAsClient) {
						SGlobalObjects.save();
					}
				} catch (Exception exception2) {
					exception2.printStackTrace();
				}

				ZomboidRadio.getInstance().Save();
				RenderThread.returnContext();
			}
		}
	}

	private static boolean validateUser(String string, String string2, String string3) throws MalformedURLException, IOException {
		String string4 = null;
		try {
			URL url = new URL("http://127.0.0.1/external/games/projectzomboid.php");
			URLConnection urlConnection = url.openConnection();
			InputStream inputStream = urlConnection.getInputStream();
			byte[] byteArray = new byte[7];
			inputStream.read(byteArray);
			if (byteArray[0] != 115 || byteArray[1] != 117 || byteArray[2] != 99 || byteArray[3] != 99 || byteArray[4] != 101) {
				throw new NullPointerException(string4);
			}

			inputStream.close();
		} catch (Exception exception) {
			if (string != null && !string.isEmpty()) {
				string4 = "http://www.desura.com/external/games/projectzomboid.php?username=" + string + "&password=" + string2;
			} else {
				string4 = "http://www.desura.com/external/games/projectzomboid.php?cdkey=" + string3;
			}

			URL url2 = new URL(string4);
			URLConnection urlConnection2 = url2.openConnection();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection2.getInputStream()));
			String string5;
			while ((string5 = bufferedReader.readLine()) != null) {
				if (string5.contains("success")) {
					return true;
				}
			}
		}

		return false;
	}

	public static void setCacheDir(String string) {
		string = string.replace("/", File.separator);
		DebugLog.log("cachedir set to \"" + string + "\"");
		CacheDir = string;
	}

	public static String getCacheDir() {
		if (CacheDir != null) {
			return CacheDir;
		} else {
			String string = System.getProperty("deployment.user.cachedir");
			if (string == null || System.getProperty("os.name").startsWith("Win")) {
				string = System.getProperty("user.home");
			}

			return string + File.separator + "Zomboid";
		}
	}

	public static String getSaveDir() {
		return getCacheDir() + File.separator + "Saves";
	}

	public static String getCoopServerHome() {
		File file = new File(getCacheDir());
		return file.getParent();
	}

	public static void WriteString(ByteBuffer byteBuffer, String string) {
		WriteStringUTF(byteBuffer, string);
	}

	public static void WriteStringUTF(ByteBuffer byteBuffer, String string) {
		try {
			((GameWindow.StringUTF)stringUTF.get()).save(byteBuffer, string);
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new RuntimeException("Bad encoding!");
		}
	}

	public static void WriteString(DataOutputStream dataOutputStream, String string) throws IOException {
		if (string == null) {
			dataOutputStream.writeInt(0);
		} else {
			dataOutputStream.writeInt(string.length());
			if (string != null && string.length() >= 0) {
				dataOutputStream.writeChars(string);
			}
		}
	}

	public static String ReadStringUTF(ByteBuffer byteBuffer) {
		try {
			return ((GameWindow.StringUTF)stringUTF.get()).load(byteBuffer);
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new RuntimeException("Bad encoding!");
		}
	}

	public static String ReadString(ByteBuffer byteBuffer) {
		return ReadStringUTF(byteBuffer);
	}

	public static String ReadString(DataInputStream dataInputStream) throws IOException {
		int int1 = dataInputStream.readInt();
		if (int1 == 0) {
			return "";
		} else if (int1 > 65536) {
			throw new RuntimeException("GameWindow.ReadString: string is too long, corrupted save?");
		} else {
			StringBuilder stringBuilder = new StringBuilder(int1);
			for (int int2 = 0; int2 < int1; ++int2) {
				stringBuilder.append(dataInputStream.readChar());
			}

			return stringBuilder.toString();
		}
	}

	public static String getGameModeCacheDir() {
		String string = getSaveDir();
		if (Core.GameMode == null) {
			Core.GameMode = "Sandbox";
		}

		return Core.GameMode != null ? string + File.separator + Core.GameMode + File.separator : string + File.separator;
	}

	public static String getScreenshotDir() {
		String string = getCacheDir() + File.separator + "Screenshots";
		File file = new File(string);
		if (!file.exists()) {
			file.mkdir();
		}

		return string;
	}

	public static void doRenderEvent(boolean boolean1) {
		doRenderEvent = boolean1;
	}

	public static void DoLoadingText(String string) {
		if (SpriteRenderer.instance != null) {
			Core.getInstance().StartFrame();
			Core.getInstance().EndFrame();
			Core.getInstance().StartFrameUI();
			SpriteRenderer.instance.render((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F);
			TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), string, 1.0, 1.0, 1.0, 1.0);
			Core.getInstance().EndFrameUI();
		}
	}

	public static void copyFolder(File file, File file2) throws IOException {
		String[] stringArray;
		String[] stringArray2;
		int int1;
		if (file.isDirectory()) {
			if (!file2.exists()) {
				file2.mkdirs();
			}

			stringArray = file.list();
			stringArray2 = stringArray;
			int int2 = stringArray.length;
			for (int1 = 0; int1 < int2; ++int1) {
				String string = stringArray2[int1];
				File file3 = new File(file, string);
				File file4 = new File(file2, string);
				copyFolder(file3, file4);
			}
		} else {
			stringArray = null;
			stringArray2 = null;
			FileInputStream fileInputStream = new FileInputStream(file);
			FileOutputStream fileOutputStream = new FileOutputStream(file2);
			byte[] byteArray = new byte[1024];
			while ((int1 = fileInputStream.read(byteArray)) > 0) {
				fileOutputStream.write(byteArray, 0, int1);
			}

			fileInputStream.close();
			fileOutputStream.close();
		}
	}

	public static String copySaveDir(String string) {
		File file = new File(getGameModeCacheDir() + File.separator + Core.GameSaveWorld);
		String string2 = GameClient.ip + "_" + GameClient.port + "_" + string;
		File file2 = new File(getGameModeCacheDir() + File.separator + string2);
		if (file.exists()) {
			if (file2.exists()) {
				return "The save " + file2.getPath() + " already exist.";
			} else {
				file2.mkdir();
				try {
					copyFolder(file, file2);
					Core.GameSaveWorld = string2;
					return "Copied your current save " + file.getPath() + " into " + file2.getPath();
				} catch (IOException ioException) {
					ioException.printStackTrace();
					return "An error occured while copying your save: " + file.getPath() + " into " + file2.getPath();
				}
			}
		} else {
			return "Can\'t change copy your save: " + getGameModeCacheDir() + File.separator + Core.GameSaveWorld;
		}
	}

	static  {
		period = 1000000000L / (long)FPS;
		running = true;
		MAX_FRAME_SKIPS = 24;
		averageFPS = (float)PerformanceSettings.LockFPS;
		stringUTF = new ThreadLocal(){
			
			protected GameWindow.StringUTF initialValue() {
				return new GameWindow.StringUTF();
			}
		};
	}

	private static class StringUTF {
		private char[] chars;
		private byte[] bytes;
		private CharsetEncoder ce;
		private CharsetDecoder cd;

		private StringUTF() {
		}

		private int encode(String string) throws UnsupportedEncodingException {
			int int1;
			if (this.chars == null || this.chars.length < string.length()) {
				int1 = (string.length() + 128 - 1) / 128 * 128;
				this.chars = new char[int1];
			}

			string.getChars(0, string.length(), this.chars, 0);
			if (this.ce == null) {
				this.ce = Charset.forName("UTF-8").newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
				assert this.ce instanceof ArrayEncoder;
			}

			this.ce.reset();
			int1 = (int)((double)string.length() * (double)this.ce.maxBytesPerChar());
			int1 = (int1 + 128 - 1) / 128 * 128;
			if (this.bytes == null || this.bytes.length < int1) {
				this.bytes = new byte[int1];
			}

			return ((ArrayEncoder)this.ce).encode(this.chars, 0, string.length(), this.bytes);
		}

		private String decode(int int1) {
			if (this.cd == null) {
				this.cd = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
				assert this.cd instanceof ArrayDecoder;
			}

			this.cd.reset();
			int int2 = (int)((double)int1 * (double)this.cd.maxCharsPerByte());
			int int3;
			if (this.chars == null || this.chars.length < int2) {
				int3 = (int2 + 128 - 1) / 128 * 128;
				this.chars = new char[int3];
			}

			int3 = ((ArrayDecoder)this.cd).decode(this.bytes, 0, int1, this.chars);
			return new String(this.chars, 0, int3);
		}

		public void save(ByteBuffer byteBuffer, String string) throws UnsupportedEncodingException {
			if (string != null && !string.isEmpty()) {
				int int1 = this.encode(string);
				byteBuffer.putShort((short)int1);
				byteBuffer.put(this.bytes, 0, int1);
			} else {
				byteBuffer.putShort((short)0);
			}
		}

		public String load(ByteBuffer byteBuffer) throws UnsupportedEncodingException {
			short short1 = byteBuffer.getShort();
			if (short1 <= 0) {
				return "";
			} else {
				int int1 = (short1 + 128 - 1) / 128 * 128;
				if (this.bytes == null || this.bytes.length < int1) {
					this.bytes = new byte[int1];
				}

				byteBuffer.get(this.bytes, 0, short1);
				return this.decode(short1);
			}
		}

		StringUTF(Object object) {
			this();
		}
	}

	public static class OSValidator {
		private static String OS = System.getProperty("os.name").toLowerCase();

		public static boolean isWindows() {
			return OS.indexOf("win") >= 0;
		}

		public static boolean isMac() {
			return OS.indexOf("mac") >= 0;
		}

		public static boolean isUnix() {
			return OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0;
		}

		public static boolean isSolaris() {
			return OS.indexOf("sunos") >= 0;
		}
	}
}
