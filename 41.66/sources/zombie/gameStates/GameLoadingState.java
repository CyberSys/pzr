package zombie.gameStates;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import org.lwjglx.input.Keyboard;
import zombie.AmbientStreamManager;
import zombie.ChunkMapFilenames;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatManager;
import zombie.chat.ChatUtility;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.runtime.RuntimeAnimationScript;
import zombie.core.textures.Texture;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.inventory.RecipeManager;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoWater;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.WorldStreamer;
import zombie.iso.areas.SafeHouse;
import zombie.iso.sprite.SkyBox;
import zombie.iso.weather.ClimateManager;
import zombie.modding.ActiveMods;
import zombie.modding.ActiveModsFile;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetworkAIParams;
import zombie.network.ServerOptions;
import zombie.scripting.ScriptManager;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.vehicles.BaseVehicle;
import zombie.world.WorldDictionary;


public final class GameLoadingState extends GameState {
	public static Thread loader = null;
	public static boolean newGame = true;
	private static long startTime;
	public static boolean build23Stop = false;
	public static boolean unexpectedError = false;
	public static String GameLoadingString = "";
	public static boolean playerWrongIP = false;
	private static boolean bShowedUI = false;
	private static boolean bShowedClickToSkip = false;
	public static boolean mapDownloadFailed = false;
	private volatile boolean bWaitForAssetLoadingToFinish1 = false;
	private volatile boolean bWaitForAssetLoadingToFinish2 = false;
	private final Object assetLock1 = "Asset Lock 1";
	private final Object assetLock2 = "Asset Lock 2";
	public static boolean playerCreated = false;
	public static boolean bDone = false;
	public static boolean convertingWorld = false;
	public static int convertingFileCount = -1;
	public static int convertingFileMax = -1;
	public int Stage = 0;
	float TotalTime = 33.0F;
	float loadingDotTick = 0.0F;
	String loadingDot = "";
	private float clickToSkipAlpha = 1.0F;
	private boolean clickToSkipFadeIn = false;
	public float Time = 0.0F;
	public boolean bForceDone = false;

	public void enter() {
		GameWindow.bLoadedAsClient = GameClient.bClient;
		GameWindow.OkToSaveOnExit = false;
		bShowedUI = false;
		ChunkMapFilenames.instance.clear();
		DebugLog.log("Savefile name is \"" + Core.GameSaveWorld + "\"");
		GameLoadingString = "";
		try {
			LuaManager.LoadDirBase("server");
			LuaManager.finishChecksum();
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

		RecipeManager.LoadedAfterLua();
		Core.getInstance().initFBOs();
		Core.getInstance().initShaders();
		SkyBox.getInstance();
		IsoPuddles.getInstance();
		IsoWater.getInstance();
		GameWindow.bServerDisconnected = false;
		if (GameClient.bClient && !GameClient.instance.bConnected) {
			GameClient.instance.init();
			for (Core.GameMode = "Multiplayer"; GameClient.instance.ID == -1; GameClient.instance.update()) {
				try {
					Thread.sleep(10L);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}

			Core.GameSaveWorld = "clienttest" + GameClient.instance.ID;
			LuaManager.GlobalObject.deleteSave("clienttest" + GameClient.instance.ID);
			LuaManager.GlobalObject.createWorld("clienttest" + GameClient.instance.ID);
		}

		if (Core.GameSaveWorld.isEmpty()) {
			DebugLog.log("No savefile directory was specified.  It\'s a bug.");
			GameWindow.DoLoadingText("No savefile directory was specified.  The game will now close.  Sorry!");
			try {
				Thread.sleep(4000L);
			} catch (Exception exception2) {
			}

			System.exit(-1);
		}

		String string = ZomboidFileSystem.instance.getGameModeCacheDir();
		File file = new File(string + File.separator + Core.GameSaveWorld);
		if (!file.exists() && !Core.getInstance().isNoSave()) {
			DebugLog.log("The savefile directory doesn\'t exist.  It\'s a bug.");
			GameWindow.DoLoadingText("The savefile directory doesn\'t exist.  The game will now close.  Sorry!");
			try {
				Thread.sleep(4000L);
			} catch (Exception exception3) {
			}

			System.exit(-1);
		}

		try {
			if (!GameClient.bClient && !GameServer.bServer && !Core.bTutorial && !Core.isLastStand() && !"Multiplayer".equals(Core.GameMode)) {
				String string2 = ZomboidFileSystem.instance.getCacheDir();
				FileWriter fileWriter = new FileWriter(new File(string2 + File.separator + "latestSave.ini"));
				fileWriter.write(IsoWorld.instance.getWorld() + "\r\n");
				fileWriter.write(Core.getInstance().getGameMode() + "\r\n");
				fileWriter.write(IsoWorld.instance.getDifficulty() + "\r\n");
				fileWriter.flush();
				fileWriter.close();
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		bDone = false;
		this.bForceDone = false;
		IsoChunkMap.CalcChunkWidth();
		LosUtil.init(IsoChunkMap.ChunkGridWidth * 10, IsoChunkMap.ChunkGridWidth * 10);
		this.Time = 0.0F;
		this.Stage = 0;
		this.clickToSkipAlpha = 1.0F;
		this.clickToSkipFadeIn = false;
		startTime = System.currentTimeMillis();
		SoundManager.instance.Purge();
		SoundManager.instance.setMusicState("Loading");
		LuaEventManager.triggerEvent("OnPreMapLoad");
		newGame = true;
		build23Stop = false;
		unexpectedError = false;
		mapDownloadFailed = false;
		playerCreated = false;
		convertingWorld = false;
		convertingFileCount = 0;
		convertingFileMax = -1;
		File file2 = ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin");
		if (file2.exists()) {
			newGame = false;
		}

		if (GameClient.bClient) {
			newGame = false;
		}

		WorldDictionary.setIsNewGame(newGame);
		GameKeyboard.bNoEventsWhileLoading = true;
		loader = new Thread(ThreadGroups.Workers, new Runnable(){
			
			public void run() {
				try {
					this.runInner();
				} catch (Throwable fileWriter) {
					GameLoadingState.unexpectedError = true;
					ExceptionLogger.logException(fileWriter);
				}
			}

			
			private void runInner() throws Exception {
				GameLoadingState.this.bWaitForAssetLoadingToFinish1 = true;
				synchronized (GameLoadingState.this.assetLock1) {
					while (GameLoadingState.this.bWaitForAssetLoadingToFinish1) {
						try {
							GameLoadingState.this.assetLock1.wait();
						} catch (InterruptedException file2) {
						}
					}
				}

				String string = ZomboidFileSystem.instance.getGameModeCacheDir();
				boolean file = (new File(string + File.separator)).mkdir();
				BaseVehicle.LoadAllVehicleTextures();
				if (GameClient.bClient) {
					GameClient.instance.GameLoadingRequestData();
				}

				TutorialManager.instance = new TutorialManager();
				GameTime.setInstance(new GameTime());
				ClimateManager.setInstance(new ClimateManager());
				IsoWorld.instance = new IsoWorld();
				DebugOptions.testThreadCrash(0);
				IsoWorld.instance.init();
				if (GameWindow.bServerDisconnected) {
					GameLoadingState.bDone = true;
				} else if (!GameLoadingState.playerWrongIP) {
					if (!GameLoadingState.build23Stop) {
						LuaEventManager.triggerEvent("OnGameTimeLoaded");
						SGlobalObjects.initSystems();
						CGlobalObjects.initSystems();
						IsoObjectPicker.Instance.Init();
						TutorialManager.instance.init();
						TutorialManager.instance.CreateQuests();
						File fileWriter = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");
						if (fileWriter.exists()) {
						}

						if (!GameServer.bServer) {
							fileWriter = ZomboidFileSystem.instance.getFileInCurrentSave("map_ver.bin");
							boolean ioException = !fileWriter.exists();
							if (ioException || IsoWorld.SavedWorldVersion != 186) {
								if (!ioException && IsoWorld.SavedWorldVersion != 186) {
									GameLoadingState.GameLoadingString = "Saving converted world.";
								}

								try {
									GameWindow.save(true);
								} catch (Throwable exception) {
									ExceptionLogger.logException(exception);
								}
							}
						}

						ChatUtility.InitAllowedChatIcons();
						ChatManager.getInstance().init(true, IsoPlayer.getInstance());
						GameLoadingState.this.bWaitForAssetLoadingToFinish2 = true;
						synchronized (GameLoadingState.this.assetLock2) {
							while (GameLoadingState.this.bWaitForAssetLoadingToFinish2) {
								try {
									GameLoadingState.this.assetLock2.wait();
								} catch (InterruptedException interruptedException) {
								}
							}
						}

						UIManager.bSuspend = false;
						GameLoadingState.playerCreated = true;
						GameLoadingState.GameLoadingString = "";
						GameLoadingState.Done();
					}
				}
			}
		});
		UIManager.bSuspend = true;
		loader.setName("GameLoadingThread");
		loader.setUncaughtExceptionHandler(GameWindow::uncaughtException);
		loader.start();
	}

	public static void Done() {
		bDone = true;
		GameKeyboard.bNoEventsWhileLoading = false;
		long long1 = System.currentTimeMillis() - startTime + 999L;
		DebugLog.log("game loading took " + long1 / 1000L + " seconds");
		if (GameClient.bClient) {
			GameClient.instance.sendLoginQueueDone2(System.currentTimeMillis() - startTime);
		}
	}

	public GameState redirectState() {
		return new IngameState();
	}

	public void exit() {
		if (GameClient.bClient) {
			NetworkAIParams.Init();
		}

		UIManager.init();
		LuaEventManager.triggerEvent("OnCreatePlayer", 0, IsoPlayer.players[0]);
		loader = null;
		bDone = false;
		this.Stage = 0;
		IsoCamera.SetCharacterToFollow(IsoPlayer.getInstance());
		if (GameClient.bClient && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
			SafeHouse safeHouse = SafeHouse.isSafeHouse(IsoPlayer.getInstance().getCurrentSquare(), GameClient.username, true);
			if (safeHouse != null) {
				IsoPlayer.getInstance().setX((float)(safeHouse.getX() - 1));
				IsoPlayer.getInstance().setY((float)(safeHouse.getY() - 1));
			}
		}

		SoundManager.instance.stopMusic("");
		AmbientStreamManager.instance.init();
		if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().isAsleep()) {
			UIManager.setFadeBeforeUI(IsoPlayer.getInstance().getPlayerNum(), true);
			UIManager.FadeOut((double)IsoPlayer.getInstance().getPlayerNum(), 2.0);
			UIManager.setFadeTime((double)IsoPlayer.getInstance().getPlayerNum(), 0.0);
			UIManager.getSpeedControls().SetCurrentGameSpeed(3);
		}

		if (!GameClient.bClient) {
			ActiveMods activeMods = ActiveMods.getById("currentGame");
			activeMods.checkMissingMods();
			activeMods.checkMissingMaps();
			ActiveMods.setLoadedMods(activeMods);
			String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("mods.txt");
			ActiveModsFile activeModsFile = new ActiveModsFile();
			activeModsFile.write(string, activeMods);
		}

		GameWindow.OkToSaveOnExit = true;
	}

	public void render() {
		this.loadingDotTick += GameTime.getInstance().getMultiplier();
		if (this.loadingDotTick > 20.0F) {
			this.loadingDot = ".";
		}

		if (this.loadingDotTick > 40.0F) {
			this.loadingDot = "..";
		}

		if (this.loadingDotTick > 60.0F) {
			this.loadingDot = "...";
		}

		if (this.loadingDotTick > 80.0F) {
			this.loadingDot = "";
			this.loadingDotTick = 0.0F;
		}

		this.Time += GameTime.instance.getTimeDelta();
		float float1 = 0.0F;
		float float2 = 0.0F;
		float float3 = 0.0F;
		float float4;
		float float5;
		float float6;
		float float7;
		float float8;
		float float9;
		if (this.Stage == 0) {
			float4 = this.Time;
			float5 = 0.0F;
			float6 = 1.0F;
			float7 = 5.0F;
			float8 = 7.0F;
			float9 = 0.0F;
			if (float4 > float5 && float4 < float6) {
				float9 = (float4 - float5) / (float6 - float5);
			}

			if (float4 >= float6 && float4 <= float7) {
				float9 = 1.0F;
			}

			if (float4 > float7 && float4 < float8) {
				float9 = 1.0F - (float4 - float7) / (float8 - float7);
			}

			if (float4 >= float8) {
				++this.Stage;
			}

			float1 = float9;
		}

		if (this.Stage == 1) {
			float4 = this.Time;
			float5 = 7.0F;
			float6 = 8.0F;
			float7 = 13.0F;
			float8 = 15.0F;
			float9 = 0.0F;
			if (float4 > float5 && float4 < float6) {
				float9 = (float4 - float5) / (float6 - float5);
			}

			if (float4 >= float6 && float4 <= float7) {
				float9 = 1.0F;
			}

			if (float4 > float7 && float4 < float8) {
				float9 = 1.0F - (float4 - float7) / (float8 - float7);
			}

			if (float4 >= float8) {
				++this.Stage;
			}

			float2 = float9;
		}

		if (this.Stage == 2) {
			float4 = this.Time;
			float5 = 15.0F;
			float6 = 16.0F;
			float7 = 31.0F;
			float8 = this.TotalTime;
			float9 = 0.0F;
			if (float4 > float5 && float4 < float6) {
				float9 = (float4 - float5) / (float6 - float5);
			}

			if (float4 >= float6 && float4 <= float7) {
				float9 = 1.0F;
			}

			if (float4 > float7 && float4 < float8) {
				float9 = 1.0F - (float4 - float7) / (float8 - float7);
			}

			if (float4 >= float8) {
				++this.Stage;
			}

			float3 = float9;
		}

		Core.getInstance().StartFrame();
		Core.getInstance().EndFrame();
		boolean boolean1 = UIManager.useUIFBO;
		UIManager.useUIFBO = false;
		Core.getInstance().StartFrameUI();
		SpriteRenderer.instance.renderi((Texture)null, 0, 0, Core.getInstance().getScreenWidth(), Core.getInstance().getScreenHeight(), 0.0F, 0.0F, 0.0F, 1.0F, (Consumer)null);
		int int1;
		int int2;
		int int3;
		if (mapDownloadFailed) {
			int1 = Core.getInstance().getScreenWidth() / 2;
			int2 = Core.getInstance().getScreenHeight() / 2;
			int3 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
			int int4 = int2 - int3 / 2;
			String string = Translator.getText("UI_GameLoad_MapDownloadFailed");
			TextManager.instance.DrawStringCentre(UIFont.Medium, (double)int1, (double)int4, string, 0.8, 0.1, 0.1, 1.0);
			UIManager.render();
			Core.getInstance().EndFrameUI();
		} else {
			byte byte1;
			int int5;
			if (unexpectedError) {
				int1 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
				int2 = TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight();
				byte byte2 = 8;
				byte1 = 2;
				int5 = int1 + byte2 + int2 + byte1 + int2;
				int int6 = Core.getInstance().getScreenWidth() / 2;
				int int7 = Core.getInstance().getScreenHeight() / 2;
				int int8 = int7 - int5 / 2;
				TextManager.instance.DrawStringCentre(UIFont.Medium, (double)int6, (double)int8, Translator.getText("UI_GameLoad_UnexpectedError1"), 0.8, 0.1, 0.1, 1.0);
				TextManager.instance.DrawStringCentre(UIFont.Small, (double)int6, (double)(int8 + int1 + byte2), Translator.getText("UI_GameLoad_UnexpectedError2"), 1.0, 1.0, 1.0, 1.0);
				String string2 = ZomboidFileSystem.instance.getCacheDir();
				String string3 = string2 + File.separator + "console.txt";
				TextManager.instance.DrawStringCentre(UIFont.Small, (double)int6, (double)(int8 + int1 + byte2 + int2 + byte1), string3, 1.0, 1.0, 1.0, 1.0);
				UIManager.render();
				Core.getInstance().EndFrameUI();
			} else {
				String string4;
				if (GameWindow.bServerDisconnected) {
					int1 = Core.getInstance().getScreenWidth() / 2;
					int2 = Core.getInstance().getScreenHeight() / 2;
					int3 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
					byte1 = 2;
					int5 = int2 - (int3 + byte1 + int3) / 2;
					string4 = GameWindow.kickReason;
					if (string4 == null) {
						string4 = Translator.getText("UI_OnConnectFailed_ConnectionLost");
					}

					TextManager.instance.DrawStringCentre(UIFont.Medium, (double)int1, (double)int5, string4, 0.8, 0.1, 0.1, 1.0);
					UIManager.render();
					Core.getInstance().EndFrameUI();
				} else {
					if (build23Stop) {
						TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 100), "This save is incompatible. Please switch to Steam branch \"build23\" to continue this save.", 0.8, 0.1, 0.1, 1.0);
					} else if (convertingWorld) {
						TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 100), Translator.getText("UI_ConvertWorld"), 0.5, 0.5, 0.5, 1.0);
						if (convertingFileMax != -1) {
							TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 100 + TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight() + 8), convertingFileCount + " / " + convertingFileMax, 0.5, 0.5, 0.5, 1.0);
						}
					}

					if (playerWrongIP) {
						int1 = Core.getInstance().getScreenWidth() / 2;
						int2 = Core.getInstance().getScreenHeight() / 2;
						int3 = TextManager.instance.getFontFromEnum(UIFont.Medium).getLineHeight();
						byte1 = 2;
						int5 = int2 - (int3 + byte1 + int3) / 2;
						string4 = GameLoadingString;
						if (GameLoadingString == null) {
							string4 = "";
						}

						TextManager.instance.DrawStringCentre(UIFont.Medium, (double)int1, (double)int5, string4, 0.8, 0.1, 0.1, 1.0);
						UIManager.render();
						Core.getInstance().EndFrameUI();
					} else {
						if (GameClient.bClient) {
							String string5 = GameLoadingString;
							if (GameLoadingString == null) {
								string5 = "";
							}

							TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 60), string5, 0.5, 0.5, 0.5, 1.0);
						} else if (!playerCreated && newGame && !Core.isLastStand()) {
							TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 60), Translator.getText("UI_Loading").replace(".", ""), 0.5, 0.5, 0.5, 1.0);
							TextManager.instance.DrawString(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2 + TextManager.instance.MeasureStringX(UIFont.Small, Translator.getText("UI_Loading").replace(".", "")) / 2 + 1), (double)(Core.getInstance().getScreenHeight() - 60), this.loadingDot, 0.5, 0.5, 0.5, 1.0);
						}

						if (this.Stage == 0) {
							int1 = Core.getInstance().getScreenWidth() / 2;
							int2 = Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2;
							TextManager.instance.DrawStringCentre(UIFont.Intro, (double)int1, (double)int2, Translator.getText("UI_Intro1"), 1.0, 1.0, 1.0, (double)float1);
						}

						if (this.Stage == 1) {
							int1 = Core.getInstance().getScreenWidth() / 2;
							int2 = Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2;
							TextManager.instance.DrawStringCentre(UIFont.Intro, (double)int1, (double)int2, Translator.getText("UI_Intro2"), 1.0, 1.0, 1.0, (double)float2);
						}

						if (this.Stage == 2) {
							int1 = Core.getInstance().getScreenWidth() / 2;
							int2 = Core.getInstance().getScreenHeight() / 2 - TextManager.instance.getFontFromEnum(UIFont.Intro).getLineHeight() / 2;
							TextManager.instance.DrawStringCentre(UIFont.Intro, (double)int1, (double)int2, Translator.getText("UI_Intro3"), 1.0, 1.0, 1.0, (double)float3);
						}

						if (playerCreated && (!newGame || this.Time >= this.TotalTime || Core.isLastStand() || "Tutorial".equals(Core.GameMode))) {
							if (this.clickToSkipFadeIn) {
								this.clickToSkipAlpha += GameTime.getInstance().getMultiplier() / 1.6F / 30.0F;
								if (this.clickToSkipAlpha > 1.0F) {
									this.clickToSkipAlpha = 1.0F;
									this.clickToSkipFadeIn = false;
								}
							} else {
								bShowedClickToSkip = true;
								this.clickToSkipAlpha -= GameTime.getInstance().getMultiplier() / 1.6F / 30.0F;
								if (this.clickToSkipAlpha < 0.25F) {
									this.clickToSkipFadeIn = true;
								}
							}

							if (GameWindow.ActivatedJoyPad != null && !JoypadManager.instance.JoypadList.isEmpty()) {
								Texture texture = Texture.getSharedTexture("media/ui/xbox/XBOX_A.png");
								if (texture != null) {
									int2 = TextManager.instance.getFontFromEnum(UIFont.Small).getLineHeight();
									SpriteRenderer.instance.renderi(texture, Core.getInstance().getScreenWidth() / 2 - TextManager.instance.MeasureStringX(UIFont.Small, Translator.getText("UI_PressAToStart")) / 2 - 8 - texture.getWidth(), Core.getInstance().getScreenHeight() - 60 + int2 / 2 - texture.getHeight() / 2, texture.getWidth(), texture.getHeight(), 1.0F, 1.0F, 1.0F, this.clickToSkipAlpha, (Consumer)null);
								}

								TextManager.instance.DrawStringCentre(UIFont.Small, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 60), Translator.getText("UI_PressAToStart"), 1.0, 1.0, 1.0, (double)this.clickToSkipAlpha);
							} else {
								TextManager.instance.DrawStringCentre(UIFont.NewLarge, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 60), Translator.getText("UI_ClickToSkip"), 1.0, 1.0, 1.0, (double)this.clickToSkipAlpha);
							}
						}

						ActiveMods.renderUI();
						Core.getInstance().EndFrameUI();
						UIManager.useUIFBO = boolean1;
					}
				}
			}
		}
	}

	public GameStateMachine.StateAction update() {
		if (this.bWaitForAssetLoadingToFinish1 && !OutfitManager.instance.isLoadingClothingItems()) {
			if (Core.bDebug) {
				OutfitManager.instance.debugOutfits();
			}

			synchronized (this.assetLock1) {
				this.bWaitForAssetLoadingToFinish1 = false;
				this.assetLock1.notifyAll();
			}
		}

		if (this.bWaitForAssetLoadingToFinish2 && !ModelManager.instance.isLoadingAnimations() && !GameWindow.fileSystem.hasWork()) {
			synchronized (this.assetLock2) {
				this.bWaitForAssetLoadingToFinish2 = false;
				this.assetLock2.notifyAll();
				ArrayList arrayList = ScriptManager.instance.getAllRuntimeAnimationScripts();
				Iterator iterator = arrayList.iterator();
				while (iterator.hasNext()) {
					RuntimeAnimationScript runtimeAnimationScript = (RuntimeAnimationScript)iterator.next();
					runtimeAnimationScript.exec();
				}
			}
		}

		if (!unexpectedError && !GameWindow.bServerDisconnected && !playerWrongIP) {
			if (!bDone) {
				return GameStateMachine.StateAction.Remain;
			} else if (WorldStreamer.instance.isBusy()) {
				return GameStateMachine.StateAction.Remain;
			} else if (ModelManager.instance.isLoadingAnimations()) {
				return GameStateMachine.StateAction.Remain;
			} else if (!bShowedClickToSkip) {
				return GameStateMachine.StateAction.Remain;
			} else {
				if (Mouse.isButtonDown(0)) {
					this.bForceDone = true;
				}

				if (GameWindow.ActivatedJoyPad != null && GameWindow.ActivatedJoyPad.isAPressed()) {
					this.bForceDone = true;
				}

				if (this.bForceDone) {
					SoundManager.instance.playUISound("UIClickToStart");
					this.bForceDone = false;
					return GameStateMachine.StateAction.Continue;
				} else {
					return GameStateMachine.StateAction.Remain;
				}
			}
		} else {
			if (!bShowedUI) {
				bShowedUI = true;
				IsoPlayer.setInstance((IsoPlayer)null);
				IsoPlayer.players[0] = null;
				UIManager.UI.clear();
				LuaEventManager.Reset();
				LuaManager.call("ISGameLoadingUI_OnGameLoadingUI", "");
				UIManager.bSuspend = false;
			}

			if (Keyboard.isKeyDown(1)) {
				GameClient.instance.Shutdown();
				SteamUtils.shutdown();
				System.exit(1);
			}

			return GameStateMachine.StateAction.Remain;
		}
	}
}
