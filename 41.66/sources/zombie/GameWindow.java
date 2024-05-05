package zombie;

import fmod.fmod.FMODManager;
import fmod.fmod.FMODSoundBank;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjglx.LWJGLException;
import org.lwjglx.input.Controller;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.OpenGLException;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.asset.AssetManagers;
import zombie.audio.BaseSoundBank;
import zombie.audio.DummySoundBank;
import zombie.characters.IsoPlayer;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.CustomPerks;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.Core;
import zombie.core.Languages;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.input.Input;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.ZipLogs;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.particle.MuzzleFlash;
import zombie.core.physics.Bullet;
import zombie.core.profiling.PerformanceProfileFrameProbe;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.raknet.VoiceManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.OutfitManager;
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
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileSystemImpl;
import zombie.gameStates.GameLoadingState;
import zombie.gameStates.GameStateMachine;
import zombie.gameStates.IngameState;
import zombie.gameStates.MainScreenState;
import zombie.gameStates.TISLogoState;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.inventory.types.MapItem;
import zombie.iso.IsoCamera;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LightingThread;
import zombie.iso.SliceY;
import zombie.iso.WorldStreamer;
import zombie.network.CoopMaster;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.sandbox.CustomSandboxOptions;
import zombie.savefile.ClientPlayerDB;
import zombie.savefile.PlayerDB;
import zombie.savefile.SavefileThumbnail;
import zombie.scripting.ScriptManager;
import zombie.spnetwork.SinglePlayerClient;
import zombie.spnetwork.SinglePlayerServer;
import zombie.ui.TextManager;
import zombie.ui.UIDebugConsole;
import zombie.ui.UIManager;
import zombie.util.PZSQLUtils;
import zombie.util.PublicServerUtil;
import zombie.vehicles.Clipper;
import zombie.vehicles.PolygonalMap2;
import zombie.world.moddata.GlobalModData;
import zombie.worldMap.WorldMapJNI;
import zombie.worldMap.WorldMapVisited;


public final class GameWindow {
	private static final String GAME_TITLE = "Project Zomboid";
	private static final FPSTracking s_fpsTracking = new FPSTracking();
	private static final ThreadLocal stringUTF = ThreadLocal.withInitial(GameWindow.StringUTF::new);
	public static final Input GameInput = new Input();
	public static boolean DEBUG_SAVE = false;
	public static boolean OkToSaveOnExit = false;
	public static String lastP = null;
	public static GameStateMachine states = new GameStateMachine();
	public static boolean bServerDisconnected;
	public static boolean bLoadedAsClient = false;
	public static String kickReason;
	public static boolean DrawReloadingLua = false;
	public static JoypadManager.Joypad ActivatedJoyPad = null;
	public static String version = "RC3";
	public static volatile boolean closeRequested;
	public static float averageFPS = (float)PerformanceSettings.getLockFPS();
	private static boolean doRenderEvent = false;
	public static boolean bLuaDebuggerKeyDown = false;
	public static FileSystem fileSystem = new FileSystemImpl();
	public static AssetManagers assetManagers;
	public static boolean bGameThreadExited;
	public static Thread GameThread;
	public static final ArrayList texturePacks;
	public static final FileSystem.TexturePackTextures texturePackTextures;

	private static void initShared() throws Exception {
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator;
		File file = new File(string2);
		if (!file.exists()) {
			file.mkdirs();
		}

		TexturePackPage.bIgnoreWorldItemTextures = true;
		byte byte1 = 2;
		LoadTexturePack("UI", byte1);
		LoadTexturePack("UI2", byte1);
		LoadTexturePack("IconsMoveables", byte1);
		LoadTexturePack("RadioIcons", byte1);
		LoadTexturePack("ApComUI", byte1);
		LoadTexturePack("Mechanics", byte1);
		LoadTexturePack("WeatherFx", byte1);
		setTexturePackLookup();
		PerkFactory.init();
		CustomPerks.instance.init();
		DoLoadingText(Translator.getText("UI_Loading_Scripts"));
		ScriptManager.instance.Load();
		DoLoadingText(Translator.getText("UI_Loading_Clothing"));
		ClothingDecals.init();
		BeardStyles.init();
		HairStyles.init();
		OutfitManager.init();
		DoLoadingText("");
		TraitFactory.init();
		ProfessionFactory.init();
		Rand.init();
		TexturePackPage.bIgnoreWorldItemTextures = false;
		TextureID.bUseCompression = TextureID.bUseCompressionOption;
		MuzzleFlash.init();
		Mouse.initCustomCursor();
		if (!Core.bDebug) {
			states.States.add(new TISLogoState());
		}

		states.States.add(new MainScreenState());
		if (!Core.bDebug) {
			states.LoopToState = 1;
		}

		GameInput.initControllers();
		int int1 = GameInput.getControllerCount();
		DebugLog.Input.println("----------------------------------------------");
		DebugLog.Input.println("--	Controller setup - use this info to	 ");
		DebugLog.Input.println("--	edit joypad.ini in save directory	   ");
		DebugLog.Input.println("----------------------------------------------");
		for (int int2 = 0; int2 < int1; ++int2) {
			Controller controller = GameInput.getController(int2);
			if (controller != null) {
				DebugLog.Input.println("----------------------------------------------");
				DebugLog.Input.println("--  Joypad: " + controller.getGamepadName());
				DebugLog.Input.println("----------------------------------------------");
				int int3 = controller.getAxisCount();
				int int4;
				String string3;
				if (int3 > 1) {
					DebugLog.Input.println("----------------------------------------------");
					DebugLog.Input.println("--	Axis definitions for controller " + int2);
					DebugLog.Input.println("----------------------------------------------");
					for (int4 = 0; int4 < int3; ++int4) {
						string3 = controller.getAxisName(int4);
						DebugLog.Input.println("Axis: " + string3);
					}
				}

				int3 = controller.getButtonCount();
				if (int3 > 1) {
					DebugLog.Input.println("----------------------------------------------");
					DebugLog.Input.println("--	Button definitions for controller " + int2);
					DebugLog.Input.println("----------------------------------------------");
					for (int4 = 0; int4 < int3; ++int4) {
						string3 = controller.getButtonName(int4);
						DebugLog.Input.println("Button: " + string3);
					}
				}
			}
		}
	}

	private static void logic() {
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
		GameInput.updateGameThread();
		if (CoopMaster.instance != null) {
			CoopMaster.instance.update();
		}

		if (IsoPlayer.players[0] != null) {
			IsoPlayer.setInstance(IsoPlayer.players[0]);
			IsoCamera.CamCharacter = IsoPlayer.players[0];
		}

		UIManager.update();
		VoiceManager.instance.update();
		LineDrawer.clear();
		if (JoypadManager.instance.isAPressed(-1)) {
			for (int int1 = 0; int1 < JoypadManager.instance.JoypadList.size(); ++int1) {
				JoypadManager.Joypad joypad = (JoypadManager.Joypad)JoypadManager.instance.JoypadList.get(int1);
				if (joypad.isAPressed()) {
					if (ActivatedJoyPad == null) {
						ActivatedJoyPad = joypad;
					}

					if (IsoPlayer.getInstance() != null) {
						LuaEventManager.triggerEvent("OnJoypadActivate", joypad.getID());
					} else {
						LuaEventManager.triggerEvent("OnJoypadActivateUI", joypad.getID());
					}

					break;
				}
			}
		}

		SoundManager.instance.Update();
		boolean boolean1 = true;
		if (GameTime.isGamePaused()) {
			boolean1 = false;
		}

		MapCollisionData.instance.updateGameState();
		Mouse.setCursorVisible(true);
		if (boolean1) {
			states.update();
		} else {
			IsoCamera.updateAll();
			if (IngameState.instance != null && (states.current == IngameState.instance || states.States.contains(IngameState.instance))) {
				LuaEventManager.triggerEvent("OnTickEvenPaused", 0.0);
			}
		}

		UIManager.resize();
		fileSystem.updateAsyncTransactions();
		if (GameKeyboard.isKeyPressed(Core.getInstance().getKey("Take screenshot"))) {
			Core.getInstance().TakeFullScreenshot((String)null);
		}
	}

	public static void render() {
		++IsoCamera.frameState.frameCount;
		renderInternal();
	}

	protected static void renderInternal() {
		if (!PerformanceSettings.LightingThread && LightingJNI.init && !LightingJNI.WaitingForMain()) {
			LightingJNI.DoLightingUpdateNew(System.nanoTime());
		}

		IsoObjectPicker.Instance.StartRender();
		GameWindow.s_performance.statesRender.invokeAndMeasure(states, GameStateMachine::render);
	}

	public static void InitDisplay() throws IOException, LWJGLException {
		Display.setTitle("Project Zomboid");
		int int1;
		if (!Core.getInstance().loadOptions()) {
			int1 = Runtime.getRuntime().availableProcessors();
			if (int1 == 1) {
				PerformanceSettings.LightingFrameSkip = 3;
			} else if (int1 == 2) {
				PerformanceSettings.LightingFrameSkip = 2;
			} else if (int1 <= 4) {
				PerformanceSettings.LightingFrameSkip = 1;
			}

			Display.setFullscreen(false);
			Display.setResizable(false);
			if (Display.getDesktopDisplayMode().getWidth() > 1920 && Display.getDesktopDisplayMode().getHeight() > 1080) {
				Core.getInstance().init(1920, 1080);
				Core.getInstance().saveOptions();
			} else {
				Core.getInstance().init(Core.width, Core.height);
			}

			if (!GL.getCapabilities().GL_ATI_meminfo && !GL.getCapabilities().GL_NVX_gpu_memory_info) {
				DebugLog.General.warn("Unable to determine available GPU memory, texture compression defaults to on");
				TextureID.bUseCompressionOption = true;
				TextureID.bUseCompression = true;
			}

			DebugLog.log("Init language : " + System.getProperty("user.language"));
			Core.getInstance().setOptionLanguageName(System.getProperty("user.language").toUpperCase());
		} else {
			Core.getInstance().init(Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight());
		}

		if (GL.getCapabilities().GL_ATI_meminfo) {
			int1 = GL11.glGetInteger(34812);
			DebugLog.log("ATI: available texture memory is " + int1 / 1024 + " MB");
		}

		if (GL.getCapabilities().GL_NVX_gpu_memory_info) {
			int1 = GL11.glGetInteger(36937);
			DebugLog.log("NVIDIA: current available GPU memory is " + int1 / 1024 + " MB");
			int1 = GL11.glGetInteger(36935);
			DebugLog.log("NVIDIA: dedicated available GPU memory is " + int1 / 1024 + " MB");
			int1 = GL11.glGetInteger(36936);
			DebugLog.log("NVIDIA: total available GPU memory is " + int1 / 1024 + " MB");
		}

		SpriteRenderer.instance.create();
	}

	public static void InitGameThread() {
		Thread.setDefaultUncaughtExceptionHandler(GameWindow::uncaughtGlobalException);
		Thread thread = new Thread(ThreadGroups.Main, GameWindow::mainThread, "MainThread");
		thread.setUncaughtExceptionHandler(GameWindow::uncaughtExceptionMainThread);
		GameThread = thread;
		thread.start();
	}

	private static void uncaughtExceptionMainThread(Thread thread, Throwable throwable) {
		if (throwable instanceof ThreadDeath) {
			DebugLog.General.println("Game Thread exited: ", thread.getName());
		} else {
			try {
				uncaughtException(thread, throwable);
			} finally {
				onGameThreadExited();
			}
		}
	}

	private static void uncaughtGlobalException(Thread thread, Throwable throwable) {
		if (throwable instanceof ThreadDeath) {
			DebugLog.General.println("External Thread exited: ", thread.getName());
		} else {
			uncaughtException(thread, throwable);
		}
	}

	public static void uncaughtException(Thread thread, Throwable throwable) {
		if (throwable instanceof ThreadDeath) {
			DebugLog.General.println("Internal Thread exited: ", thread.getName());
		} else {
			String string = String.format("Unhandled %s thrown by thread %s.", throwable.getClass().getName(), thread.getName());
			DebugLog.General.error(string);
			ExceptionLogger.logException(throwable, string);
		}
	}

	private static void mainThread() {
		mainThreadInit();
		enter();
		RenderThread.setWaitForRenderState(true);
		run_ez();
	}

	private static void mainThreadInit() {
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

		DebugOptions.instance.init();
		GameProfiler.init();
		SoundManager.instance = (BaseSoundManager)(Core.SoundDisabled ? new DummySoundManager() : new SoundManager());
		AmbientStreamManager.instance = (BaseAmbientStreamManager)(Core.SoundDisabled ? new DummyAmbientStreamManager() : new AmbientStreamManager());
		BaseSoundBank.instance = (BaseSoundBank)(Core.SoundDisabled ? new DummySoundBank() : new FMODSoundBank());
		VoiceManager.instance.loadConfig();
		TextureID.bUseCompressionOption = Core.SafeModeForced || Core.getInstance().getOptionTextureCompression();
		TextureID.bUseCompression = TextureID.bUseCompressionOption;
		SoundManager.instance.setSoundVolume((float)Core.getInstance().getOptionSoundVolume() / 10.0F);
		SoundManager.instance.setMusicVolume((float)Core.getInstance().getOptionMusicVolume() / 10.0F);
		SoundManager.instance.setAmbientVolume((float)Core.getInstance().getOptionAmbientVolume() / 10.0F);
		SoundManager.instance.setVehicleEngineVolume((float)Core.getInstance().getOptionVehicleEngineVolume() / 10.0F);
		try {
			ZomboidFileSystem.instance.init();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		DebugFileWatcher.instance.init();
		String string3 = System.getProperty("server");
		String string4 = System.getProperty("client");
		String string5 = System.getProperty("nozombies");
		if (string5 != null) {
			IsoWorld.NoZombies = true;
		}

		if (string3 != null && string3.equals("true")) {
			GameServer.bServer = true;
		}

		try {
			renameSaveFolders();
			init();
		} catch (Exception exception2) {
			throw new RuntimeException(exception2);
		}
	}

	private static void renameSaveFolders() {
		String string = ZomboidFileSystem.instance.getSaveDir();
		File file = new File(string);
		if (file.exists() && file.isDirectory()) {
			File file2 = new File(file, "Fighter");
			File file3 = new File(file, "Survivor");
			if (file2.exists() && file2.isDirectory() && file3.exists() && file3.isDirectory()) {
				DebugLog.log("RENAMING Saves/Survivor to Saves/Apocalypse");
				DebugLog.log("RENAMING Saves/Fighter to Saves/Survivor");
				file3.renameTo(new File(file, "Apocalypse"));
				file2.renameTo(new File(file, "Survivor"));
				String string2 = ZomboidFileSystem.instance.getCacheDir();
				File file4 = new File(string2 + File.separator + "latestSave.ini");
				if (file4.exists()) {
					file4.delete();
				}
			}
		}
	}

	public static long readLong(DataInputStream dataInputStream) throws IOException {
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
			return (long)(int1 + (int2 << 8) + (int3 << 16) + (int4 << 24) + (int5 << 32) + (int6 << 40) + (int7 << 48) + (int8 << 56));
		}
	}

	public static int readInt(DataInputStream dataInputStream) throws IOException {
		int int1 = dataInputStream.read();
		int int2 = dataInputStream.read();
		int int3 = dataInputStream.read();
		int int4 = dataInputStream.read();
		if ((int1 | int2 | int3 | int4) < 0) {
			throw new EOFException();
		} else {
			return int1 + (int2 << 8) + (int3 << 16) + (int4 << 24);
		}
	}

	private static void run_ez() {
		long long1 = System.nanoTime();
		long long2 = 0L;
		while (!RenderThread.isCloseRequested() && !closeRequested) {
			long long3 = System.nanoTime();
			if (long3 < long1) {
				long1 = long3;
			} else {
				long long4 = long3 - long1;
				long1 = long3;
				if (PerformanceSettings.isUncappedFPS()) {
					frameStep();
				} else {
					long2 += long4;
					long long5 = PZMath.secondsToNanos / (long)PerformanceSettings.getLockFPS();
					if (long2 >= long5) {
						frameStep();
						long2 %= long5;
					}
				}

				if (Core.bDebug && DebugOptions.instance.ThreadCrash_Enabled.getValue()) {
					DebugOptions.testThreadCrash(0);
					RenderThread.invokeOnRenderContext(()->{
						DebugOptions.testThreadCrash(1);
					});
				}

				Thread.yield();
			}
		}

		exit();
	}

	private static void enter() {
		Core.TileScale = Core.getInstance().getOptionTexture2x() ? 2 : 1;
		if (Core.SafeModeForced) {
			Core.TileScale = 1;
		}

		IsoCamera.init();
		int int1 = TextureID.bUseCompression ? 4 : 0;
		int1 |= 64;
		if (Core.TileScale == 1) {
			LoadTexturePack("Tiles1x", int1);
			LoadTexturePack("Overlays1x", int1);
			LoadTexturePack("JumboTrees1x", int1);
			LoadTexturePack("Tiles1x.floor", int1 & -5);
		}

		if (Core.TileScale == 2) {
			LoadTexturePack("Tiles2x", int1);
			LoadTexturePack("Overlays2x", int1);
			LoadTexturePack("JumboTrees2x", int1);
			LoadTexturePack("Tiles2x.floor", int1 & -5);
		}

		setTexturePackLookup();
		if (Texture.getSharedTexture("TileIndieStoneTentFrontLeft") == null) {
			throw new RuntimeException("Rebuild Tiles.pack with \"1 Include This in .pack\" as individual images not tilesheets");
		} else {
			DebugLog.log("LOADED UP A TOTAL OF " + Texture.totalTextureID + " TEXTURES");
			s_fpsTracking.init();
			DoLoadingText(Translator.getText("UI_Loading_ModelsAnimations"));
			ModelManager.instance.create();
			if (!SteamUtils.isSteamModeEnabled()) {
				DoLoadingText(Translator.getText("UI_Loading_InitPublicServers"));
				PublicServerUtil.init();
			}

			VoiceManager.instance.InitVMClient();
			DoLoadingText(Translator.getText("UI_Loading_OnGameBoot"));
			LuaEventManager.triggerEvent("OnGameBoot");
		}
	}

	private static void frameStep() {
		try {
			++IsoCamera.frameState.frameCount;
			GameWindow.s_performance.frameStep.start();
			s_fpsTracking.frameStep();
			GameWindow.s_performance.logic.invokeAndMeasure(GameWindow::logic);
			Core.getInstance().setScreenSize(RenderThread.getDisplayWidth(), RenderThread.getDisplayHeight());
			renderInternal();
			if (doRenderEvent) {
				LuaEventManager.triggerEvent("OnRenderTick");
			}

			Core.getInstance().DoFrameReady();
			LightingThread.instance.update();
			if (Core.bDebug) {
				if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Lua Debugger"))) {
					if (!bLuaDebuggerKeyDown) {
						UIManager.setShowLuaDebuggerOnError(true);
						LuaManager.thread.bStep = true;
						LuaManager.thread.bStepInto = true;
						bLuaDebuggerKeyDown = true;
					}
				} else {
					bLuaDebuggerKeyDown = false;
				}

				if (GameKeyboard.isKeyPressed(Core.getInstance().getKey("ToggleLuaConsole"))) {
					UIDebugConsole uIDebugConsole = UIManager.getDebugConsole();
					if (uIDebugConsole != null) {
						uIDebugConsole.setVisible(!uIDebugConsole.isVisible());
					}
				}
			}
		} catch (OpenGLException openGLException) {
			RenderThread.logGLException(openGLException);
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		} finally {
			GameWindow.s_performance.frameStep.end();
		}
	}

	private static void exit() {
		DebugLog.log("EXITDEBUG: GameWindow.exit 1");
		if (GameClient.bClient) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null) {
					ClientPlayerDB.getInstance().clientSendNetworkPlayerInt(player);
				}
			}

			WorldStreamer.instance.stop();
			GameClient.instance.doDisconnect("exit");
			VoiceManager.instance.DeinitVMClient();
		}

		if (OkToSaveOnExit) {
			try {
				WorldStreamer.instance.quit();
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			if (PlayerDB.isAllow()) {
				PlayerDB.getInstance().saveLocalPlayersForce();
				PlayerDB.getInstance().m_canSavePlayers = false;
			}

			if (ClientPlayerDB.isAllow()) {
				ClientPlayerDB.getInstance().canSavePlayers = false;
			}

			try {
				if (GameClient.bClient && GameClient.connection != null) {
					GameClient.connection.username = null;
				}

				save(true);
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}

			try {
				if (IsoWorld.instance.CurrentCell != null) {
					LuaEventManager.triggerEvent("OnPostSave");
				}
			} catch (Exception exception2) {
				exception2.printStackTrace();
			}

			try {
				if (IsoWorld.instance.CurrentCell != null) {
					LuaEventManager.triggerEvent("OnPostSave");
				}
			} catch (Exception exception3) {
				exception3.printStackTrace();
			}

			try {
				LightingThread.instance.stop();
				MapCollisionData.instance.stop();
				ZombiePopulationManager.instance.stop();
				PolygonalMap2.instance.stop();
				ZombieSpawnRecorder.instance.quit();
			} catch (Exception exception4) {
				exception4.printStackTrace();
			}
		}

		DebugLog.log("EXITDEBUG: GameWindow.exit 2");
		if (GameClient.bClient) {
			WorldStreamer.instance.stop();
			GameClient.instance.doDisconnect("exit-saving");
			try {
				Thread.sleep(500L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}

		DebugLog.log("EXITDEBUG: GameWindow.exit 3");
		if (PlayerDB.isAvailable()) {
			PlayerDB.getInstance().close();
		}

		if (ClientPlayerDB.isAvailable()) {
			ClientPlayerDB.getInstance().close();
		}

		DebugLog.log("EXITDEBUG: GameWindow.exit 4");
		GameClient.instance.Shutdown();
		SteamUtils.shutdown();
		ZipLogs.addZipFile(true);
		onGameThreadExited();
		DebugLog.log("EXITDEBUG: GameWindow.exit 5");
	}

	private static void onGameThreadExited() {
		bGameThreadExited = true;
		RenderThread.onGameThreadExited();
	}

	public static void setTexturePackLookup() {
		texturePackTextures.clear();
		for (int int1 = texturePacks.size() - 1; int1 >= 0; --int1) {
			GameWindow.TexturePack texturePack = (GameWindow.TexturePack)texturePacks.get(int1);
			if (texturePack.modID == null) {
				texturePackTextures.putAll(texturePack.textures);
			}
		}

		ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();
		for (int int2 = texturePacks.size() - 1; int2 >= 0; --int2) {
			GameWindow.TexturePack texturePack2 = (GameWindow.TexturePack)texturePacks.get(int2);
			if (texturePack2.modID != null && arrayList.contains(texturePack2.modID)) {
				texturePackTextures.putAll(texturePack2.textures);
			}
		}

		Texture.onTexturePacksChanged();
	}

	public static void LoadTexturePack(String string, int int1) {
		LoadTexturePack(string, int1, (String)null);
	}

	public static void LoadTexturePack(String string, int int1, String string2) {
		DebugLog.General.println("texturepack: loading " + string);
		DoLoadingText(Translator.getText("UI_Loading_Texturepack", string));
		String string3 = ZomboidFileSystem.instance.getString("media/texturepacks/" + string + ".pack");
		GameWindow.TexturePack texturePack = new GameWindow.TexturePack();
		texturePack.packName = string;
		texturePack.fileName = string3;
		texturePack.modID = string2;
		fileSystem.mountTexturePack(string, texturePack.textures, int1);
		texturePacks.add(texturePack);
	}

	@Deprecated
	public static void LoadTexturePackDDS(String string) {
		DebugLog.log("texturepack: loading " + string);
		if (SpriteRenderer.instance != null) {
			Core.getInstance().StartFrame();
			Core.getInstance().EndFrame(0);
			Core.getInstance().StartFrameUI();
			SpriteRenderer.instance.renderi((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F, (Consumer)null);
			TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), Translator.getText("UI_Loading_Texturepack", string), 1.0, 1.0, 1.0, 1.0);
			Core.getInstance().EndFrameUI();
		}

		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(ZomboidFileSystem.instance.getString("media/texturepacks/" + string + ".pack"));
		} catch (FileNotFoundException fileNotFoundException) {
			Logger.getLogger(GameLoadingState.class.getName()).log(Level.SEVERE, (String)null, fileNotFoundException);
		}

		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			try {
				int int1 = TexturePackPage.readInt((InputStream)bufferedInputStream);
				int int2 = 0;
				while (true) {
					if (int2 >= int1) {
						DebugLog.log("texturepack: finished loading " + string);
						break;
					}

					TexturePackPage texturePackPage = new TexturePackPage();
					if (int2 % 100 == 0 && SpriteRenderer.instance != null) {
						Core.getInstance().StartFrame();
						Core.getInstance().EndFrame();
						Core.getInstance().StartFrameUI();
						TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), Translator.getText("UI_Loading_Texturepack", string), 1.0, 1.0, 1.0, 1.0);
						Core.getInstance().EndFrameUI();
						RenderThread.invokeOnRenderContext(Display::update);
					}

					texturePackPage.loadFromPackFileDDS(bufferedInputStream);
					++int2;
				}
			} catch (Throwable throwable) {
				try {
					bufferedInputStream.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			bufferedInputStream.close();
		} catch (Exception exception) {
			DebugLog.log("media/texturepacks/" + string + ".pack");
			exception.printStackTrace();
		}

		Texture.nullTextures.clear();
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
			} catch (InterruptedException | IOException error) {
				error.printStackTrace();
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

			if ("1".equals(System.getProperty("zomboid.debuglibs.lighting"))) {
				DebugLog.log("***** Loading debug version of Lighting");
				string = string + "d";
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

	private static void init() throws Exception {
		initFonts();
		checkRequiredLibraries();
		SteamUtils.init();
		ServerBrowser.init();
		SteamFriends.init();
		SteamWorkshop.init();
		RakNetPeerInterface.init();
		LightingJNI.init();
		ZombiePopulationManager.init();
		PZSQLUtils.init();
		Clipper.init();
		WorldMapJNI.init();
		Bullet.init();
		int int1 = Runtime.getRuntime().availableProcessors();
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator;
		File file = new File(string2);
		if (!file.exists()) {
			file.mkdirs();
		}

		DoLoadingText("Loading Mods");
		ZomboidFileSystem.instance.resetDefaultModsForNewRelease("41_51");
		ZomboidFileSystem.instance.loadMods("default");
		ZomboidFileSystem.instance.loadModPackFiles();
		if (Core.getInstance().isDefaultOptions() && SteamUtils.isSteamModeEnabled() && SteamUtils.isRunningOnSteamDeck()) {
			Core.getInstance().setOptionFontSize(2);
			Core.getInstance().setOptionSingleContextMenu(0, true);
			Core.getInstance().setOptionShoulderButtonContainerSwitch(3);
			Core.getInstance().setAutoZoom(0, true);
			Core.getInstance().setOptionZoomLevels2x("75;125;150;175;200;225");
			Core.getInstance().setOptionPanCameraWhileAiming(true);
			Core.getInstance().setOptionPanCameraWhileDriving(true);
			Core.getInstance().setOptionTextureCompression(true);
		}

		DoLoadingText("Loading Translations");
		Languages.instance.init();
		Translator.language = null;
		initFonts();
		Translator.loadFiles();
		initShared();
		DoLoadingText(Translator.getText("UI_Loading_Lua"));
		LuaManager.init();
		CustomPerks.instance.initLua();
		CustomSandboxOptions.instance.init();
		CustomSandboxOptions.instance.initInstance(SandboxOptions.instance);
		LuaManager.LoadDirBase();
		ZomboidGlobals.Load();
		LuaEventManager.triggerEvent("OnLoadSoundBanks");
	}

	private static void initFonts() throws FileNotFoundException {
		TextManager.instance.Init();
		while (TextManager.instance.font.isEmpty()) {
			fileSystem.updateAsyncTransactions();
			try {
				Thread.sleep(10L);
			} catch (InterruptedException interruptedException) {
			}
		}
	}

	public static void save(boolean boolean1) throws IOException {
		if (!Core.getInstance().isNoSave()) {
			if (IsoWorld.instance.CurrentCell != null && !"LastStand".equals(Core.getInstance().getGameMode()) && !"Tutorial".equals(Core.getInstance().getGameMode())) {
				File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin");
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				DataOutputStream dataOutputStream;
				try {
					dataOutputStream = new DataOutputStream(fileOutputStream);
					try {
						dataOutputStream.writeInt(186);
						WriteString(dataOutputStream, Core.GameMap);
						WriteString(dataOutputStream, IsoWorld.instance.getDifficulty());
					} catch (Throwable throwable) {
						try {
							dataOutputStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					dataOutputStream.close();
				} catch (Throwable throwable3) {
					try {
						fileOutputStream.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				fileOutputStream.close();
				file = ZomboidFileSystem.instance.getFileInCurrentSave("map_sand.bin");
				fileOutputStream = new FileOutputStream(file);
				try {
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					try {
						SliceY.SliceBuffer.clear();
						SandboxOptions.instance.save(SliceY.SliceBuffer);
						bufferedOutputStream.write(SliceY.SliceBuffer.array(), 0, SliceY.SliceBuffer.position());
					} catch (Throwable throwable5) {
						try {
							bufferedOutputStream.close();
						} catch (Throwable throwable6) {
							throwable5.addSuppressed(throwable6);
						}

						throw throwable5;
					}

					bufferedOutputStream.close();
				} catch (Throwable throwable7) {
					try {
						fileOutputStream.close();
					} catch (Throwable throwable8) {
						throwable7.addSuppressed(throwable8);
					}

					throw throwable7;
				}

				fileOutputStream.close();
				LuaEventManager.triggerEvent("OnSave");
				try {
					try {
						try {
							if (Thread.currentThread() == GameThread) {
								SavefileThumbnail.create();
							}
						} catch (Exception exception) {
							ExceptionLogger.logException(exception);
						}

						file = ZomboidFileSystem.instance.getFileInCurrentSave("map.bin");
						try {
							fileOutputStream = new FileOutputStream(file);
							try {
								dataOutputStream = new DataOutputStream(fileOutputStream);
								IsoWorld.instance.CurrentCell.save(dataOutputStream, boolean1);
							} catch (Throwable throwable9) {
								try {
									fileOutputStream.close();
								} catch (Throwable throwable10) {
									throwable9.addSuppressed(throwable10);
								}

								throw throwable9;
							}

							fileOutputStream.close();
						} catch (Exception exception2) {
							ExceptionLogger.logException(exception2);
						}

						try {
							MapCollisionData.instance.save();
							if (!bLoadedAsClient) {
								SGlobalObjects.save();
							}
						} catch (Exception exception3) {
							ExceptionLogger.logException(exception3);
						}

						ZomboidRadio.getInstance().Save();
						GlobalModData.instance.save();
						MapItem.SaveWorldMap();
						WorldMapVisited.SaveAll();
					} catch (IOException ioException) {
						throw new RuntimeException(ioException);
					}
				} catch (RuntimeException runtimeException) {
					Throwable throwable11 = runtimeException.getCause();
					if (throwable11 instanceof IOException) {
						throw (IOException)throwable11;
					} else {
						throw runtimeException;
					}
				}
			}
		}
	}

	public static String getCoopServerHome() {
		File file = new File(ZomboidFileSystem.instance.getCacheDir());
		return file.getParent();
	}

	public static void WriteString(ByteBuffer byteBuffer, String string) {
		WriteStringUTF(byteBuffer, string);
	}

	public static void WriteStringUTF(ByteBuffer byteBuffer, String string) {
		((GameWindow.StringUTF)stringUTF.get()).save(byteBuffer, string);
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
		return ((GameWindow.StringUTF)stringUTF.get()).load(byteBuffer);
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

	public static void doRenderEvent(boolean boolean1) {
		doRenderEvent = boolean1;
	}

	public static void DoLoadingText(String string) {
		if (SpriteRenderer.instance != null && TextManager.instance.font != null) {
			Core.getInstance().StartFrame();
			Core.getInstance().EndFrame();
			Core.getInstance().StartFrameUI();
			SpriteRenderer.instance.renderi((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F, (Consumer)null);
			TextManager.instance.DrawStringCentre((double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() / 2), string, 1.0, 1.0, 1.0, 1.0);
			Core.getInstance().EndFrameUI();
		}
	}

	static  {
		assetManagers = new AssetManagers(fileSystem);
		bGameThreadExited = false;
		texturePacks = new ArrayList();
		texturePackTextures = new FileSystem.TexturePackTextures();
	}

	private static class s_performance {
		static final PerformanceProfileFrameProbe frameStep = new PerformanceProfileFrameProbe("GameWindow.frameStep");
		static final PerformanceProfileProbe statesRender = new PerformanceProfileProbe("GameWindow.states.render");
		static final PerformanceProfileProbe logic = new PerformanceProfileProbe("GameWindow.logic");
	}

	private static final class TexturePack {
		String packName;
		String fileName;
		String modID;
		final FileSystem.TexturePackTextures textures = new FileSystem.TexturePackTextures();
	}

	private static class StringUTF {
		private char[] chars;
		private ByteBuffer byteBuffer;
		private CharBuffer charBuffer;
		private CharsetEncoder ce;
		private CharsetDecoder cd;

		private int encode(String string) {
			int int1;
			if (this.chars == null || this.chars.length < string.length()) {
				int1 = (string.length() + 128 - 1) / 128 * 128;
				this.chars = new char[int1];
				this.charBuffer = CharBuffer.wrap(this.chars);
			}

			string.getChars(0, string.length(), this.chars, 0);
			this.charBuffer.limit(string.length());
			this.charBuffer.position(0);
			if (this.ce == null) {
				this.ce = StandardCharsets.UTF_8.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
			}

			this.ce.reset();
			int1 = (int)((double)string.length() * (double)this.ce.maxBytesPerChar());
			int1 = (int1 + 128 - 1) / 128 * 128;
			if (this.byteBuffer == null || this.byteBuffer.capacity() < int1) {
				this.byteBuffer = ByteBuffer.allocate(int1);
			}

			this.byteBuffer.clear();
			CoderResult coderResult = this.ce.encode(this.charBuffer, this.byteBuffer, true);
			return this.byteBuffer.position();
		}

		private String decode(int int1) {
			if (this.cd == null) {
				this.cd = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
			}

			this.cd.reset();
			int int2 = (int)((double)int1 * (double)this.cd.maxCharsPerByte());
			if (this.chars == null || this.chars.length < int2) {
				int int3 = (int2 + 128 - 1) / 128 * 128;
				this.chars = new char[int3];
				this.charBuffer = CharBuffer.wrap(this.chars);
			}

			this.charBuffer.clear();
			CoderResult coderResult = this.cd.decode(this.byteBuffer, this.charBuffer, true);
			return new String(this.chars, 0, this.charBuffer.position());
		}

		void save(ByteBuffer byteBuffer, String string) {
			if (string != null && !string.isEmpty()) {
				int int1 = this.encode(string);
				byteBuffer.putShort((short)int1);
				this.byteBuffer.flip();
				byteBuffer.put(this.byteBuffer);
			} else {
				byteBuffer.putShort((short)0);
			}
		}

		String load(ByteBuffer byteBuffer) {
			short short1 = byteBuffer.getShort();
			if (short1 <= 0) {
				return "";
			} else {
				int int1 = (short1 + 128 - 1) / 128 * 128;
				if (this.byteBuffer == null || this.byteBuffer.capacity() < int1) {
					this.byteBuffer = ByteBuffer.allocate(int1);
				}

				this.byteBuffer.clear();
				if (byteBuffer.remaining() < short1) {
					DebugLog.General.error("GameWindow.StringUTF.load> numBytes:" + short1 + " is higher than the remaining bytes in the buffer:" + byteBuffer.remaining());
				}

				int int2 = byteBuffer.limit();
				byteBuffer.limit(byteBuffer.position() + short1);
				this.byteBuffer.put(byteBuffer);
				byteBuffer.limit(int2);
				this.byteBuffer.flip();
				return this.decode(short1);
			}
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
