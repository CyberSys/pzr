package zombie.gameStates;

import fmod.javafmod;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import zombie.AmbientStreamManager;
import zombie.DebugFileWatcher;
import zombie.FliesSound;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.LootRespawn;
import zombie.MapCollisionData;
import zombie.ReanimatedPlayers;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZombieSpawnRecorder;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.audio.ObjectAmbientEmitters;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorFactory;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.CustomPerks;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitFactory;
import zombie.chat.ChatElement;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.Languages;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderSettings;
import zombie.core.opengl.RenderThread;
import zombie.core.physics.WorldSimulation;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.model.ModelOutlines;
import zombie.core.skinnedmodel.model.WorldItemAtlas;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.stash.StashSystem;
import zombie.core.textures.Texture;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionGlobals;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.inventory.ItemSoundManager;
import zombie.iso.BentFences;
import zombie.iso.BrokenFences;
import zombie.iso.BuildingDef;
import zombie.iso.ContainerOverlays;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMarkers;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.LightingThread;
import zombie.iso.LotHeader;
import zombie.iso.SearchMode;
import zombie.iso.TileOverlays;
import zombie.iso.WorldMarkers;
import zombie.iso.WorldStreamer;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.CorpseFlies;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.SkyBox;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.Temperature;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.meta.Meta;
import zombie.modding.ActiveMods;
import zombie.network.BodyDamageSync;
import zombie.network.ChunkChecksum;
import zombie.network.ClientServerMap;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ItemTransactionManager;
import zombie.network.MPStatistics;
import zombie.network.PassengerMap;
import zombie.network.ServerGUI;
import zombie.network.ServerOptions;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.sandbox.CustomSandboxOptions;
import zombie.savefile.ClientPlayerDB;
import zombie.savefile.PlayerDB;
import zombie.scripting.ScriptManager;
import zombie.spnetwork.SinglePlayerClient;
import zombie.spnetwork.SinglePlayerServer;
import zombie.text.templating.TemplateText;
import zombie.ui.ActionProgressBar;
import zombie.ui.FPSGraph;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIElement;
import zombie.ui.UIManager;
import zombie.util.StringUtils;
import zombie.vehicles.EditVehicleState;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleCache;
import zombie.vehicles.VehicleIDMap;
import zombie.vehicles.VehicleType;
import zombie.vehicles.VehiclesDB2;
import zombie.worldMap.WorldMap;
import zombie.worldMap.WorldMapVisited;
import zombie.worldMap.editor.WorldMapEditorState;


public final class IngameState extends GameState {
	public static int WaitMul = 20;
	public static IngameState instance;
	public static float draww;
	public static float drawh;
	public static Long GameID = 0L;
	static int last = -1;
	static float xPos;
	static float yPos;
	static float offx;
	static float offy;
	static float zoom;
	static HashMap ContainerTypes = new HashMap();
	static int nSaveCycle = 1800;
	static boolean bDoChars = false;
	static boolean keySpacePreviousFrame = false;
	public long numberTicks = 0L;
	public boolean Paused = false;
	public float SaveDelay = 0.0F;
	boolean alt = false;
	int insanityScareCount = 5;
	int insanitypic = -1;
	int timesincelastinsanity = 10000000;
	GameState RedirectState = null;
	boolean bDidServerDisconnectState = false;
	boolean fpsKeyDown = false;
	private final ArrayList debugTimes = new ArrayList();
	private int tickCount = 0;
	private float SadisticMusicDirectorTime;
	public boolean showAnimationViewer = false;
	public boolean showAttachmentEditor = false;
	public boolean showChunkDebugger = false;
	public boolean showGlobalObjectDebugger = false;
	public String showVehicleEditor = null;
	public String showWorldMapEditor = null;

	public IngameState() {
		instance = this;
	}

	public static void renderDebugOverhead(IsoCell cell, int int1, int int2, int int3, int int4) {
		Mouse.update();
		int int5 = Mouse.getX();
		int int6 = Mouse.getY();
		int5 -= int3;
		int6 -= int4;
		int5 /= int2;
		int6 /= int2;
		SpriteRenderer.instance.renderi((Texture)null, int3, int4, int2 * cell.getWidthInTiles(), int2 * cell.getHeightInTiles(), 0.7F, 0.7F, 0.7F, 1.0F, (Consumer)null);
		IsoGridSquare square = cell.getGridSquare(int5 + cell.ChunkMap[0].getWorldXMinTiles(), int6 + cell.ChunkMap[0].getWorldYMinTiles(), 0);
		int int7;
		int int8;
		if (square != null) {
			byte byte1 = 48;
			byte byte2 = 48;
			TextManager.instance.DrawString((double)byte2, (double)byte1, "SQUARE FLAGS", 1.0, 1.0, 1.0, 1.0);
			int7 = byte1 + 20;
			int8 = byte2 + 8;
			int int9;
			for (int9 = 0; int9 < IsoFlagType.MAX.index(); ++int9) {
				if (square.Is(IsoFlagType.fromIndex(int9))) {
					TextManager.instance.DrawString((double)int8, (double)int7, IsoFlagType.fromIndex(int9).toString(), 0.6, 0.6, 0.8, 1.0);
					int7 += 18;
				}
			}

			byte2 = 48;
			int7 += 16;
			TextManager.instance.DrawString((double)byte2, (double)int7, "SQUARE OBJECT TYPES", 1.0, 1.0, 1.0, 1.0);
			int7 += 20;
			int8 = byte2 + 8;
			for (int9 = 0; int9 < 64; ++int9) {
				if (square.getHasTypes().isSet(int9)) {
					TextManager.instance.DrawString((double)int8, (double)int7, IsoObjectType.fromIndex(int9).toString(), 0.6, 0.6, 0.8, 1.0);
					int7 += 18;
				}
			}
		}

		for (int7 = 0; int7 < cell.getWidthInTiles(); ++int7) {
			for (int8 = 0; int8 < cell.getHeightInTiles(); ++int8) {
				IsoGridSquare square2 = cell.getGridSquare(int7 + cell.ChunkMap[0].getWorldXMinTiles(), int8 + cell.ChunkMap[0].getWorldYMinTiles(), int1);
				if (square2 != null) {
					if (!square2.getProperties().Is(IsoFlagType.solid) && !square2.getProperties().Is(IsoFlagType.solidtrans)) {
						if (!square2.getProperties().Is(IsoFlagType.exterior)) {
							SpriteRenderer.instance.renderi((Texture)null, int3 + int7 * int2, int4 + int8 * int2, int2, int2, 0.8F, 0.8F, 0.8F, 1.0F, (Consumer)null);
						}
					} else {
						SpriteRenderer.instance.renderi((Texture)null, int3 + int7 * int2, int4 + int8 * int2, int2, int2, 0.5F, 0.5F, 0.5F, 255.0F, (Consumer)null);
					}

					if (square2.Has(IsoObjectType.tree)) {
						SpriteRenderer.instance.renderi((Texture)null, int3 + int7 * int2, int4 + int8 * int2, int2, int2, 0.4F, 0.8F, 0.4F, 1.0F, (Consumer)null);
					}

					if (square2.getProperties().Is(IsoFlagType.collideN)) {
						SpriteRenderer.instance.renderi((Texture)null, int3 + int7 * int2, int4 + int8 * int2, int2, 1, 0.2F, 0.2F, 0.2F, 1.0F, (Consumer)null);
					}

					if (square2.getProperties().Is(IsoFlagType.collideW)) {
						SpriteRenderer.instance.renderi((Texture)null, int3 + int7 * int2, int4 + int8 * int2, 1, int2, 0.2F, 0.2F, 0.2F, 1.0F, (Consumer)null);
					}
				}
			}
		}
	}

	public static float translatePointX(float float1, float float2, float float3, float float4) {
		float1 -= float2;
		float1 *= float3;
		float1 += float4;
		float1 += draww / 2.0F;
		return float1;
	}

	public static float invTranslatePointX(float float1, float float2, float float3, float float4) {
		float1 -= draww / 2.0F;
		float1 -= float4;
		float1 /= float3;
		float1 += float2;
		return float1;
	}

	public static float invTranslatePointY(float float1, float float2, float float3, float float4) {
		float1 -= drawh / 2.0F;
		float1 -= float4;
		float1 /= float3;
		float1 += float2;
		return float1;
	}

	public static float translatePointY(float float1, float float2, float float3, float float4) {
		float1 -= float2;
		float1 *= float3;
		float1 += float4;
		float1 += drawh / 2.0F;
		return float1;
	}

	public static void renderRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = translatePointX(float1, xPos, zoom, offx);
		float float10 = translatePointY(float2, yPos, zoom, offy);
		float float11 = translatePointX(float1 + float3, xPos, zoom, offx);
		float float12 = translatePointY(float2 + float4, yPos, zoom, offy);
		float3 = float11 - float9;
		float4 = float12 - float10;
		if (!(float9 >= (float)Core.getInstance().getScreenWidth()) && !(float11 < 0.0F) && !(float10 >= (float)Core.getInstance().getScreenHeight()) && !(float12 < 0.0F)) {
			SpriteRenderer.instance.render((Texture)null, float9, float10, float3, float4, float5, float6, float7, float8, (Consumer)null);
		}
	}

	public static void renderLine(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = translatePointX(float1, xPos, zoom, offx);
		float float10 = translatePointY(float2, yPos, zoom, offy);
		float float11 = translatePointX(float3, xPos, zoom, offx);
		float float12 = translatePointY(float4, yPos, zoom, offy);
		if ((!(float9 >= (float)Core.getInstance().getScreenWidth()) || !(float11 >= (float)Core.getInstance().getScreenWidth())) && (!(float10 >= (float)Core.getInstance().getScreenHeight()) || !(float12 >= (float)Core.getInstance().getScreenHeight())) && (!(float9 < 0.0F) || !(float11 < 0.0F)) && (!(float10 < 0.0F) || !(float12 < 0.0F))) {
			SpriteRenderer.instance.renderline((Texture)null, (int)float9, (int)float10, (int)float11, (int)float12, float5, float6, float7, float8);
		}
	}

	public static void renderDebugOverhead2(IsoCell cell, int int1, float float1, int int2, int int3, float float2, float float3, int int4, int int5) {
		draww = (float)int4;
		drawh = (float)int5;
		xPos = float2;
		yPos = float3;
		offx = (float)int2;
		offy = (float)int3;
		zoom = float1;
		float float4 = (float)cell.ChunkMap[0].getWorldXMinTiles();
		float float5 = (float)cell.ChunkMap[0].getWorldYMinTiles();
		float float6 = (float)cell.ChunkMap[0].getWorldXMaxTiles();
		float float7 = (float)cell.ChunkMap[0].getWorldYMaxTiles();
		renderRect(float4, float5, (float)cell.getWidthInTiles(), (float)cell.getWidthInTiles(), 0.7F, 0.7F, 0.7F, 1.0F);
		int int6;
		for (int int7 = 0; int7 < cell.getWidthInTiles(); ++int7) {
			for (int6 = 0; int6 < cell.getHeightInTiles(); ++int6) {
				IsoGridSquare square = cell.getGridSquare(int7 + cell.ChunkMap[0].getWorldXMinTiles(), int6 + cell.ChunkMap[0].getWorldYMinTiles(), int1);
				float float8 = (float)int7 + float4;
				float float9 = (float)int6 + float5;
				if (square != null) {
					if (!square.getProperties().Is(IsoFlagType.solid) && !square.getProperties().Is(IsoFlagType.solidtrans)) {
						if (!square.getProperties().Is(IsoFlagType.exterior)) {
							renderRect(float8, float9, 1.0F, 1.0F, 0.8F, 0.8F, 0.8F, 1.0F);
						}
					} else {
						renderRect(float8, float9, 1.0F, 1.0F, 0.5F, 0.5F, 0.5F, 1.0F);
					}

					if (square.Has(IsoObjectType.tree)) {
						renderRect(float8, float9, 1.0F, 1.0F, 0.4F, 0.8F, 0.4F, 1.0F);
					}

					if (square.getProperties().Is(IsoFlagType.collideN)) {
						renderRect(float8, float9, 1.0F, 0.2F, 0.2F, 0.2F, 0.2F, 1.0F);
					}

					if (square.getProperties().Is(IsoFlagType.collideW)) {
						renderRect(float8, float9, 0.2F, 1.0F, 0.2F, 0.2F, 0.2F, 1.0F);
					}
				}
			}
		}

		IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
		renderRect((float)(metaGrid.minX * 300), (float)(metaGrid.minY * 300), (float)(metaGrid.getWidth() * 300), (float)(metaGrid.getHeight() * 300), 1.0F, 1.0F, 1.0F, 0.05F);
		if ((double)float1 > 0.1) {
			for (int6 = metaGrid.minY; int6 <= metaGrid.maxY; ++int6) {
				renderLine((float)(metaGrid.minX * 300), (float)(int6 * 300), (float)((metaGrid.maxX + 1) * 300), (float)(int6 * 300), 1.0F, 1.0F, 1.0F, 0.15F);
			}

			for (int6 = metaGrid.minX; int6 <= metaGrid.maxX; ++int6) {
				renderLine((float)(int6 * 300), (float)(metaGrid.minY * 300), (float)(int6 * 300), (float)((metaGrid.maxY + 1) * 300), 1.0F, 1.0F, 1.0F, 0.15F);
			}
		}

		IsoMetaCell[][] metaCellArrayArray = IsoWorld.instance.MetaGrid.Grid;
		for (int int8 = 0; int8 < metaCellArrayArray.length; ++int8) {
			for (int int9 = 0; int9 < metaCellArrayArray[0].length; ++int9) {
				LotHeader lotHeader = metaCellArrayArray[int8][int9].info;
				if (lotHeader == null) {
					renderRect((float)((metaGrid.minX + int8) * 300 + 1), (float)((metaGrid.minY + int9) * 300 + 1), 298.0F, 298.0F, 0.2F, 0.0F, 0.0F, 0.3F);
				} else {
					for (int int10 = 0; int10 < lotHeader.Buildings.size(); ++int10) {
						BuildingDef buildingDef = (BuildingDef)lotHeader.Buildings.get(int10);
						if (buildingDef.bAlarmed) {
							renderRect((float)buildingDef.getX(), (float)buildingDef.getY(), (float)buildingDef.getW(), (float)buildingDef.getH(), 0.8F, 0.8F, 0.5F, 0.3F);
						} else {
							renderRect((float)buildingDef.getX(), (float)buildingDef.getY(), (float)buildingDef.getW(), (float)buildingDef.getH(), 0.5F, 0.5F, 0.8F, 0.3F);
						}
					}
				}
			}
		}
	}

	public static void copyWorld(String string, String string2) {
		String string3 = ZomboidFileSystem.instance.getGameModeCacheDir();
		String string4 = string3 + File.separator + string + File.separator;
		string4 = string4.replace("/", File.separator);
		string4 = string4.replace("\\", File.separator);
		String string5 = string4.substring(0, string4.lastIndexOf(File.separator));
		string5 = string5.replace("\\", "/");
		File file = new File(string5);
		string3 = ZomboidFileSystem.instance.getGameModeCacheDir();
		string4 = string3 + File.separator + string2 + File.separator;
		string4 = string4.replace("/", File.separator);
		string4 = string4.replace("\\", File.separator);
		String string6 = string4.substring(0, string4.lastIndexOf(File.separator));
		string6 = string6.replace("\\", "/");
		File file2 = new File(string6);
		try {
			copyDirectory(file, file2);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public static void copyDirectory(File file, File file2) throws IOException {
		if (file.isDirectory()) {
			if (!file2.exists()) {
				file2.mkdir();
			}

			String[] stringArray = file.list();
			boolean boolean1 = GameLoadingState.convertingFileMax == -1;
			if (boolean1) {
				GameLoadingState.convertingFileMax = stringArray.length;
			}

			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				if (boolean1) {
					++GameLoadingState.convertingFileCount;
				}

				copyDirectory(new File(file, stringArray[int1]), new File(file2, stringArray[int1]));
			}
		} else {
			FileInputStream fileInputStream = new FileInputStream(file);
			FileOutputStream fileOutputStream = new FileOutputStream(file2);
			fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0L, file.length());
			fileInputStream.close();
			fileOutputStream.close();
		}
	}

	public static void createWorld(String string) {
		string = string.replace(" ", "_").trim();
		String string2 = ZomboidFileSystem.instance.getGameModeCacheDir();
		String string3 = string2 + File.separator + string + File.separator;
		string3 = string3.replace("/", File.separator);
		string3 = string3.replace("\\", File.separator);
		String string4 = string3.substring(0, string3.lastIndexOf(File.separator));
		string4 = string4.replace("\\", "/");
		File file = new File(string4);
		if (!file.exists()) {
			file.mkdirs();
		}

		Core.GameSaveWorld = string;
	}

	public void debugFullyStreamedIn(int int1, int int2) {
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, 0);
		if (square != null) {
			if (square.getBuilding() != null) {
				BuildingDef buildingDef = square.getBuilding().getDef();
				if (buildingDef != null) {
					boolean boolean1 = buildingDef.isFullyStreamedIn();
					for (int int3 = 0; int3 < buildingDef.overlappedChunks.size(); int3 += 2) {
						short short1 = buildingDef.overlappedChunks.get(int3);
						short short2 = buildingDef.overlappedChunks.get(int3 + 1);
						if (boolean1) {
							renderRect((float)(short1 * 10), (float)(short2 * 10), 10.0F, 10.0F, 0.0F, 1.0F, 0.0F, 0.5F);
						} else {
							renderRect((float)(short1 * 10), (float)(short2 * 10), 10.0F, 10.0F, 1.0F, 0.0F, 0.0F, 0.5F);
						}
					}
				}
			}
		}
	}

	public void UpdateStuff() {
		GameClient.bIngame = true;
		this.SaveDelay += GameTime.instance.getMultiplier();
		if (this.SaveDelay / 60.0F > 30.0F) {
			this.SaveDelay = 0.0F;
		}

		GameTime.instance.LastLastTimeOfDay = GameTime.instance.getLastTimeOfDay();
		GameTime.instance.setLastTimeOfDay(GameTime.getInstance().getTimeOfDay());
		boolean boolean1 = false;
		if (!GameServer.bServer && IsoPlayer.getInstance() != null) {
			boolean1 = IsoPlayer.allPlayersAsleep();
		}

		GameTime.getInstance().update(boolean1 && UIManager.getFadeAlpha() == 1.0);
		if (!this.Paused) {
			ScriptManager.instance.update();
		}

		if (!this.Paused) {
			long long1 = System.nanoTime();
			try {
				WorldSoundManager.instance.update();
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}

			try {
				IsoFireManager.Update();
			} catch (Exception exception2) {
				ExceptionLogger.logException(exception2);
			}

			try {
				RainManager.Update();
			} catch (Exception exception3) {
				ExceptionLogger.logException(exception3);
			}

			Meta.instance.update();
			try {
				VirtualZombieManager.instance.update();
				MapCollisionData.instance.updateMain();
				ZombiePopulationManager.instance.updateMain();
				PolygonalMap2.instance.updateMain();
			} catch (Exception exception4) {
				ExceptionLogger.logException(exception4);
			} catch (Error error) {
				error.printStackTrace();
			}

			try {
				LootRespawn.update();
			} catch (Exception exception5) {
				ExceptionLogger.logException(exception5);
			}

			if (GameServer.bServer) {
				try {
					AmbientStreamManager.instance.update();
				} catch (Exception exception6) {
					ExceptionLogger.logException(exception6);
				}
			} else {
				ObjectAmbientEmitters.getInstance().update();
			}

			if (GameClient.bClient) {
				try {
					BodyDamageSync.instance.update();
				} catch (Exception exception7) {
					ExceptionLogger.logException(exception7);
				}
			}

			if (!GameServer.bServer) {
				try {
					ItemSoundManager.update();
					FliesSound.instance.update();
					CorpseFlies.update();
					LuaManager.call("SadisticMusicDirectorTick", (Object)null);
					WorldMapVisited.update();
				} catch (Exception exception8) {
					ExceptionLogger.logException(exception8);
				}
			}

			SearchMode.getInstance().update();
			RenderSettings.getInstance().update();
			long long2 = System.nanoTime();
		}
	}

	public void enter() {
		UIManager.useUIFBO = Core.getInstance().supportsFBO() && Core.OptionUIFBO;
		if (!Core.getInstance().getUseShaders()) {
			Core.getInstance().RenderShader = null;
		}

		GameSounds.fix3DListenerPosition(false);
		IsoPlayer.getInstance().updateUsername();
		IsoPlayer.getInstance().setSceneCulled(false);
		IsoPlayer.getInstance().getInventory().addItemsToProcessItems();
		GameID = (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		GameID = GameID + (long)Rand.Next(10000000);
		ZombieSpawnRecorder.instance.init();
		if (!GameServer.bServer) {
			IsoWorld.instance.CurrentCell.ChunkMap[0].processAllLoadGridSquare();
			IsoWorld.instance.CurrentCell.ChunkMap[0].update();
			if (!GameClient.bClient) {
				LightingThread.instance.GameLoadingUpdate();
			}
		}

		try {
			MapCollisionData.instance.startGame();
		} catch (Throwable throwable) {
			ExceptionLogger.logException(throwable);
		}

		IsoWorld.instance.CurrentCell.putInVehicle(IsoPlayer.getInstance());
		SoundManager.instance.setMusicState("Tutorial".equals(Core.GameMode) ? "Tutorial" : "InGame");
		ClimateManager.getInstance().update();
		LuaEventManager.triggerEvent("OnGameStart");
		LuaEventManager.triggerEvent("OnLoad");
		if (GameClient.bClient) {
			GameClient.instance.sendPlayerConnect(IsoPlayer.getInstance());
			DebugLog.log("Waiting for player-connect response from server");
			for (; IsoPlayer.getInstance().OnlineID == -1; GameClient.instance.update()) {
				try {
					Thread.sleep(10L);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}

			ClimateManager.getInstance().update();
			LightingThread.instance.GameLoadingUpdate();
		}

		if (GameClient.bClient && SteamUtils.isSteamModeEnabled()) {
			SteamFriends.UpdateRichPresenceConnectionInfo("In game", "+connect " + GameClient.ip + ":" + GameClient.port);
		}
	}

	public void exit() {
		DebugLog.log("EXITDEBUG: IngameState.exit 1");
		if (SteamUtils.isSteamModeEnabled()) {
			SteamFriends.UpdateRichPresenceConnectionInfo("", "");
		}

		UIManager.useUIFBO = false;
		if (FPSGraph.instance != null) {
			FPSGraph.instance.setVisible(false);
		}

		UIManager.updateBeforeFadeOut();
		SoundManager.instance.setMusicState("MainMenu");
		long long1 = System.currentTimeMillis();
		boolean boolean1 = UIManager.useUIFBO;
		UIManager.useUIFBO = false;
		DebugLog.log("EXITDEBUG: IngameState.exit 2");
		while (true) {
			float float1 = Math.min(1.0F, (float)(System.currentTimeMillis() - long1) / 500.0F);
			boolean boolean2 = true;
			int int1;
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] != null) {
					IsoPlayer.setInstance(IsoPlayer.players[int1]);
					IsoCamera.CamCharacter = IsoPlayer.players[int1];
					IsoSprite.globalOffsetX = -1.0F;
					Core.getInstance().StartFrame(int1, boolean2);
					IsoCamera.frameState.set(int1);
					IsoWorld.instance.render();
					Core.getInstance().EndFrame(int1);
					boolean2 = false;
				}
			}

			Core.getInstance().RenderOffScreenBuffer();
			Core.getInstance().StartFrameUI();
			UIManager.render();
			UIManager.DrawTexture(UIManager.getBlack(), 0.0, 0.0, (double)Core.getInstance().getScreenWidth(), (double)Core.getInstance().getScreenHeight(), (double)float1);
			Core.getInstance().EndFrameUI();
			DebugLog.log("EXITDEBUG: IngameState.exit 3 (alpha=" + float1 + ")");
			if (float1 >= 1.0F) {
				UIManager.useUIFBO = boolean1;
				DebugLog.log("EXITDEBUG: IngameState.exit 4");
				RenderThread.setWaitForRenderState(false);
				SpriteRenderer.instance.notifyRenderStateQueue();
				while (WorldStreamer.instance.isBusy()) {
					try {
						Thread.sleep(1L);
					} catch (InterruptedException interruptedException) {
						interruptedException.printStackTrace();
					}
				}

				DebugLog.log("EXITDEBUG: IngameState.exit 5");
				WorldStreamer.instance.stop();
				LightingThread.instance.stop();
				MapCollisionData.instance.stop();
				ZombiePopulationManager.instance.stop();
				PolygonalMap2.instance.stop();
				DebugLog.log("EXITDEBUG: IngameState.exit 6");
				int int2;
				for (int2 = 0; int2 < IsoWorld.instance.CurrentCell.ChunkMap.length; ++int2) {
					IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int2];
					for (int1 = 0; int1 < IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth; ++int1) {
						IsoChunk chunk = chunkMap.getChunk(int1 % IsoChunkMap.ChunkGridWidth, int1 / IsoChunkMap.ChunkGridWidth);
						if (chunk != null && chunk.refs.contains(chunkMap)) {
							chunk.refs.remove(chunkMap);
							if (chunk.refs.isEmpty()) {
								chunk.removeFromWorld();
								chunk.doReuseGridsquares();
							}
						}
					}
				}

				ModelManager.instance.Reset();
				for (int2 = 0; int2 < 4; ++int2) {
					IsoPlayer.players[int2] = null;
				}

				IsoPlayer.Reset();
				ZombieSpawnRecorder.instance.quit();
				DebugLog.log("EXITDEBUG: IngameState.exit 7");
				IsoPlayer.numPlayers = 1;
				Core.getInstance().OffscreenBuffer.destroy();
				WeatherFxMask.destroy();
				IsoRegions.reset();
				Temperature.reset();
				WorldMarkers.instance.reset();
				IsoMarkers.instance.reset();
				SearchMode.reset();
				ZomboidRadio.getInstance().Reset();
				IsoWaveSignal.Reset();
				ErosionGlobals.Reset();
				IsoGenerator.Reset();
				StashSystem.Reset();
				LootRespawn.Reset();
				VehicleCache.Reset();
				VehicleIDMap.instance.Reset();
				IsoWorld.instance.KillCell();
				ItemSoundManager.Reset();
				IsoChunk.Reset();
				ChunkChecksum.Reset();
				ClientServerMap.Reset();
				SinglePlayerClient.Reset();
				SinglePlayerServer.Reset();
				PassengerMap.Reset();
				DeadBodyAtlas.instance.Reset();
				WorldItemAtlas.instance.Reset();
				CorpseFlies.Reset();
				if (PlayerDB.isAvailable()) {
					PlayerDB.getInstance().close();
				}

				VehiclesDB2.instance.Reset();
				WorldMap.Reset();
				WorldStreamer.instance = new WorldStreamer();
				WorldSimulation.instance.destroy();
				WorldSimulation.instance = new WorldSimulation();
				DebugLog.log("EXITDEBUG: IngameState.exit 8");
				VirtualZombieManager.instance.Reset();
				VirtualZombieManager.instance = new VirtualZombieManager();
				ReanimatedPlayers.instance = new ReanimatedPlayers();
				ScriptManager.instance.Reset();
				GameSounds.Reset();
				VehicleType.Reset();
				TemplateText.Reset();
				LuaEventManager.Reset();
				MapObjects.Reset();
				CGlobalObjects.Reset();
				SGlobalObjects.Reset();
				AmbientStreamManager.instance.stop();
				SoundManager.instance.stop();
				IsoPlayer.setInstance((IsoPlayer)null);
				IsoCamera.CamCharacter = null;
				TutorialManager.instance.StealControl = false;
				UIManager.init();
				ScriptManager.instance.Reset();
				ClothingDecals.Reset();
				BeardStyles.Reset();
				HairStyles.Reset();
				OutfitManager.Reset();
				AnimationSet.Reset();
				GameSounds.Reset();
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
				GameWindow.ActivatedJoyPad = null;
				GameWindow.OkToSaveOnExit = false;
				GameWindow.bLoadedAsClient = false;
				Core.bLastStand = false;
				Core.ChallengeID = null;
				Core.bTutorial = false;
				Core.getInstance().setChallenge(false);
				Core.getInstance().setForceSnow(false);
				Core.getInstance().setZombieGroupSound(true);
				Core.getInstance().setFlashIsoCursor(false);
				SystemDisabler.Reset();
				Texture.nullTextures.clear();
				DebugLog.log("EXITDEBUG: IngameState.exit 9");
				ZomboidFileSystem.instance.Reset();
				if (!Core.SoundDisabled && !GameServer.bServer) {
					javafmod.FMOD_System_Update();
				}

				try {
					ZomboidFileSystem.instance.init();
				} catch (IOException ioException) {
					ExceptionLogger.logException(ioException);
				}

				Core.OptionModsEnabled = true;
				DebugLog.log("EXITDEBUG: IngameState.exit 10");
				ZomboidFileSystem.instance.loadMods("default");
				ZomboidFileSystem.instance.loadModPackFiles();
				Languages.instance.init();
				Translator.loadFiles();
				DebugLog.log("EXITDEBUG: IngameState.exit 11");
				CustomPerks.instance.init();
				CustomPerks.instance.initLua();
				CustomSandboxOptions.instance.init();
				CustomSandboxOptions.instance.initInstance(SandboxOptions.instance);
				ScriptManager.instance.Load();
				ModelManager.instance.initAnimationMeshes(true);
				ModelManager.instance.loadModAnimations();
				ClothingDecals.init();
				BeardStyles.init();
				HairStyles.init();
				OutfitManager.init();
				DebugLog.log("EXITDEBUG: IngameState.exit 12");
				try {
					TextManager.instance.Init();
					LuaManager.LoadDirBase();
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}

				ZomboidGlobals.Load();
				DebugLog.log("EXITDEBUG: IngameState.exit 13");
				LuaEventManager.triggerEvent("OnGameBoot");
				SoundManager.instance.resumeSoundAndMusic();
				IsoPlayer[] playerArray = IsoPlayer.players;
				int int3 = playerArray.length;
				for (int1 = 0; int1 < int3; ++int1) {
					IsoPlayer player = playerArray[int1];
					if (player != null) {
						player.dirtyRecalcGridStack = true;
					}
				}

				RenderThread.setWaitForRenderState(true);
				DebugLog.log("EXITDEBUG: IngameState.exit 14");
				return;
			}

			try {
				Thread.sleep(33L);
			} catch (Exception exception2) {
			}
		}
	}

	public void yield() {
		SoundManager.instance.setMusicState("PauseMenu");
	}

	public GameState redirectState() {
		if (this.RedirectState != null) {
			GameState gameState = this.RedirectState;
			this.RedirectState = null;
			return gameState;
		} else {
			return new MainScreenState();
		}
	}

	public void reenter() {
		SoundManager.instance.setMusicState("InGame");
	}

	public void renderframetext(int int1) {
		IngameState.s_performance.renderFrameText.invokeAndMeasure(this, int1, IngameState::renderFrameTextInternal);
	}

	private void renderFrameTextInternal(int int1) {
		IndieGL.disableAlphaTest();
		IndieGL.glDisable(2929);
		ArrayList arrayList = UIManager.getUI();
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			UIElement uIElement = (UIElement)arrayList.get(int2);
			if (!(uIElement instanceof ActionProgressBar) && uIElement.isVisible() && uIElement.isFollowGameWorld() && (uIElement.getRenderThisPlayerOnly() == -1 || uIElement.getRenderThisPlayerOnly() == int1)) {
				uIElement.render();
			}
		}

		ActionProgressBar actionProgressBar = UIManager.getProgressBar((double)int1);
		if (actionProgressBar != null && actionProgressBar.isVisible()) {
			actionProgressBar.render();
		}

		WorldMarkers.instance.render();
		IsoMarkers.instance.render();
		TextDrawObject.RenderBatch(int1);
		ChatElement.RenderBatch(int1);
		try {
			Core.getInstance().EndFrameText(int1);
		} catch (Exception exception) {
		}
	}

	public void renderframe(int int1) {
		IngameState.s_performance.renderFrame.invokeAndMeasure(this, int1, IngameState::renderFrameInternal);
	}

	private void renderFrameInternal(int int1) {
		if (IsoPlayer.getInstance() == null) {
			IsoPlayer.setInstance(IsoPlayer.players[0]);
			IsoCamera.CamCharacter = IsoPlayer.getInstance();
		}

		RenderSettings.getInstance().applyRenderSettings(int1);
		ActionProgressBar actionProgressBar = UIManager.getProgressBar((double)int1);
		if (actionProgressBar != null) {
			actionProgressBar.update(int1);
		}

		IndieGL.disableAlphaTest();
		IndieGL.glDisable(2929);
		if (IsoPlayer.getInstance() != null && !IsoPlayer.getInstance().isAsleep() || UIManager.getFadeAlpha((double)int1) < 1.0F) {
			ModelOutlines.instance.startFrameMain(int1);
			IsoWorld.instance.render();
			ModelOutlines.instance.endFrameMain(int1);
			RenderSettings.getInstance().legacyPostRender(int1);
			LuaEventManager.triggerEvent("OnPostRender");
		}

		LineDrawer.clear();
		if (Core.bDebug && GameKeyboard.isKeyPressed(Core.getInstance().getKey("ToggleAnimationText"))) {
			DebugOptions.instance.Animation.Debug.setValue(!DebugOptions.instance.Animation.Debug.getValue());
		}

		try {
			Core.getInstance().EndFrame(int1);
		} catch (Exception exception) {
		}
	}

	public void renderframeui() {
		IngameState.s_performance.renderFrameUI.invokeAndMeasure(this, IngameState::renderFrameUI);
	}

	private void renderFrameUI() {
		if (Core.getInstance().StartFrameUI()) {
			TextManager.instance.DrawTextFromGameWorld();
			SkyBox.getInstance().draw();
			UIManager.render();
			ZomboidRadio.getInstance().render();
			if (Core.bDebug && IsoPlayer.getInstance() != null && IsoPlayer.getInstance().isGhostMode()) {
				IsoWorld.instance.CurrentCell.ChunkMap[0].drawDebugChunkMap();
			}

			DeadBodyAtlas.instance.renderUI();
			WorldItemAtlas.instance.renderUI();
			if (Core.bDebug) {
				if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Display FPS"))) {
					if (!this.fpsKeyDown) {
						this.fpsKeyDown = true;
						if (FPSGraph.instance == null) {
							FPSGraph.instance = new FPSGraph();
						}

						FPSGraph.instance.setVisible(!FPSGraph.instance.isVisible());
					}
				} else {
					this.fpsKeyDown = false;
				}

				if (FPSGraph.instance != null) {
					FPSGraph.instance.render();
				}
			}

			if (!GameServer.bServer) {
				for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null && !player.isDead() && player.isAsleep()) {
						float float1 = GameClient.bFastForward ? GameTime.getInstance().ServerTimeOfDay : GameTime.getInstance().getTimeOfDay();
						float float2 = (float1 - (float)((int)float1)) * 60.0F;
						String string = "media/ui/SleepClock" + (int)float2 / 10 + ".png";
						Texture texture = Texture.getSharedTexture(string);
						if (texture == null) {
							break;
						}

						int int2 = IsoCamera.getScreenLeft(int1);
						int int3 = IsoCamera.getScreenTop(int1);
						int int4 = IsoCamera.getScreenWidth(int1);
						int int5 = IsoCamera.getScreenHeight(int1);
						SpriteRenderer.instance.renderi(texture, int2 + int4 / 2 - texture.getWidth() / 2, int3 + int5 / 2 - texture.getHeight() / 2, texture.getWidth(), texture.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
					}
				}
			}

			ActiveMods.renderUI();
			JoypadManager.instance.renderUI();
		}

		if (Core.bDebug && DebugOptions.instance.Animation.AnimRenderPicker.getValue() && IsoPlayer.players[0] != null) {
			IsoPlayer.players[0].advancedAnimator.render();
		}

		if (Core.bDebug) {
			ModelOutlines.instance.renderDebug();
		}

		Core.getInstance().EndFrameUI();
	}

	public void render() {
		IngameState.s_performance.render.invokeAndMeasure(this, IngameState::renderInternal);
	}

	private void renderInternal() {
		boolean boolean1 = true;
		int int1;
		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			if (IsoPlayer.players[int1] == null) {
				if (int1 == 0) {
					SpriteRenderer.instance.prePopulating();
				}
			} else {
				IsoPlayer.setInstance(IsoPlayer.players[int1]);
				IsoCamera.CamCharacter = IsoPlayer.players[int1];
				Core.getInstance().StartFrame(int1, boolean1);
				IsoCamera.frameState.set(int1);
				boolean1 = false;
				IsoSprite.globalOffsetX = -1.0F;
				this.renderframe(int1);
			}
		}

		if (DebugOptions.instance.OffscreenBuffer.Render.getValue()) {
			Core.getInstance().RenderOffScreenBuffer();
		}

		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			if (IsoPlayer.players[int1] != null) {
				IsoPlayer.setInstance(IsoPlayer.players[int1]);
				IsoCamera.CamCharacter = IsoPlayer.players[int1];
				IsoCamera.frameState.set(int1);
				Core.getInstance().StartFrameText(int1);
				this.renderframetext(int1);
			}
		}

		UIManager.resize();
		this.renderframeui();
	}

	public GameStateMachine.StateAction update() {
		GameStateMachine.StateAction stateAction;
		try {
			IngameState.s_performance.update.start();
			stateAction = this.updateInternal();
		} finally {
			IngameState.s_performance.update.end();
		}

		return stateAction;
	}

	private GameStateMachine.StateAction updateInternal() {
		++this.tickCount;
		int int1;
		if (this.tickCount < 60) {
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] != null) {
					IsoPlayer.players[int1].dirtyRecalcGridStackTime = 20.0F;
				}
			}
		}

		LuaEventManager.triggerEvent("OnTickEvenPaused", BoxedStaticValues.toDouble((double)this.numberTicks));
		DebugFileWatcher.instance.update();
		AdvancedAnimator.checkModifiedFiles();
		if (Core.bDebug) {
			this.debugTimes.clear();
			this.debugTimes.add(System.nanoTime());
		}

		if (Core.bExiting) {
			DebugLog.log("EXITDEBUG: IngameState.updateInternal 1");
			Core.bExiting = false;
			if (GameClient.bClient) {
				for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null) {
						ClientPlayerDB.getInstance().clientSendNetworkPlayerInt(player);
					}
				}

				try {
					Thread.sleep(500L);
				} catch (InterruptedException interruptedException) {
				}

				WorldStreamer.instance.stop();
				GameClient.instance.doDisconnect("exiting");
			}

			DebugLog.log("EXITDEBUG: IngameState.updateInternal 2");
			if (PlayerDB.isAllow()) {
				PlayerDB.getInstance().saveLocalPlayersForce();
				PlayerDB.getInstance().m_canSavePlayers = false;
			}

			if (ClientPlayerDB.isAllow()) {
				ClientPlayerDB.getInstance().canSavePlayers = false;
			}

			try {
				GameWindow.save(true);
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
			}

			DebugLog.log("EXITDEBUG: IngameState.updateInternal 3");
			try {
				LuaEventManager.triggerEvent("OnPostSave");
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}

			if (ClientPlayerDB.isAllow()) {
				ClientPlayerDB.getInstance().close();
			}

			return GameStateMachine.StateAction.Continue;
		} else if (GameWindow.bServerDisconnected) {
			TutorialManager.instance.StealControl = true;
			if (!this.bDidServerDisconnectState) {
				this.bDidServerDisconnectState = true;
				this.RedirectState = new ServerDisconnectState();
				return GameStateMachine.StateAction.Yield;
			} else {
				GameClient.connection = null;
				GameClient.instance.bConnected = false;
				GameClient.bClient = false;
				GameWindow.bServerDisconnected = false;
				return GameStateMachine.StateAction.Continue;
			}
		} else {
			if (Core.bDebug) {
				label368: {
					if (!this.showGlobalObjectDebugger && (!GameKeyboard.isKeyPressed(60) || !GameKeyboard.isKeyDown(29))) {
						if (!this.showChunkDebugger && !GameKeyboard.isKeyPressed(60)) {
							if (this.showAnimationViewer || GameKeyboard.isKeyPressed(65) && GameKeyboard.isKeyDown(29)) {
								this.showAnimationViewer = false;
								DebugLog.General.debugln("Activating AnimationViewerState.");
								AnimationViewerState animationViewerState = AnimationViewerState.checkInstance();
								this.RedirectState = animationViewerState;
								return GameStateMachine.StateAction.Yield;
							}

							if (!this.showAttachmentEditor && (!GameKeyboard.isKeyPressed(65) || !GameKeyboard.isKeyDown(42))) {
								if (this.showVehicleEditor == null && !GameKeyboard.isKeyPressed(65)) {
									if (this.showWorldMapEditor == null && !GameKeyboard.isKeyPressed(66)) {
										break label368;
									}

									WorldMapEditorState worldMapEditorState = WorldMapEditorState.checkInstance();
									this.showWorldMapEditor = null;
									this.RedirectState = worldMapEditorState;
									return GameStateMachine.StateAction.Yield;
								}

								DebugLog.General.debugln("Activating EditVehicleState.");
								EditVehicleState editVehicleState = EditVehicleState.checkInstance();
								if (!StringUtils.isNullOrWhitespace(this.showVehicleEditor)) {
									editVehicleState.setScript(this.showVehicleEditor);
								}

								this.showVehicleEditor = null;
								this.RedirectState = editVehicleState;
								return GameStateMachine.StateAction.Yield;
							}

							this.showAttachmentEditor = false;
							DebugLog.General.debugln("Activating AttachmentEditorState.");
							AttachmentEditorState attachmentEditorState = AttachmentEditorState.checkInstance();
							this.RedirectState = attachmentEditorState;
							return GameStateMachine.StateAction.Yield;
						}

						this.showChunkDebugger = false;
						DebugLog.General.debugln("Activating DebugChunkState.");
						this.RedirectState = DebugChunkState.checkInstance();
						return GameStateMachine.StateAction.Yield;
					}

					this.showGlobalObjectDebugger = false;
					DebugLog.General.debugln("Activating DebugGlobalObjectState.");
					this.RedirectState = new DebugGlobalObjectState();
					return GameStateMachine.StateAction.Yield;
				}
			}

			if (Core.bDebug) {
				this.debugTimes.add(System.nanoTime());
			}

			++this.timesincelastinsanity;
			if (Core.bDebug) {
				this.debugTimes.add(System.nanoTime());
			}

			try {
				if (!GameServer.bServer && IsoPlayer.getInstance() != null && IsoPlayer.allPlayersDead()) {
					if (IsoPlayer.getInstance() != null) {
						UIManager.getSpeedControls().SetCurrentGameSpeed(1);
					}

					IsoCamera.update();
				}

				this.alt = !this.alt;
				if (!GameServer.bServer) {
					WaitMul = 1;
					if (UIManager.getSpeedControls() != null) {
						if (UIManager.getSpeedControls().getCurrentGameSpeed() == 2) {
							WaitMul = 15;
						}

						if (UIManager.getSpeedControls().getCurrentGameSpeed() == 3) {
							WaitMul = 30;
						}
					}
				}

				if (Core.bDebug) {
					this.debugTimes.add(System.nanoTime());
				}

				if (GameServer.bServer) {
					if (GameServer.Players.isEmpty() && ServerOptions.instance.PauseEmpty.getValue()) {
						this.Paused = true;
					} else {
						this.Paused = false;
					}
				}

				if (!this.Paused || GameClient.bClient) {
					try {
						if (IsoCamera.CamCharacter != null && IsoWorld.instance.bDoChunkMapUpdate) {
							for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
								if (IsoPlayer.players[int1] != null && !IsoWorld.instance.CurrentCell.ChunkMap[int1].ignore) {
									if (!GameServer.bServer) {
										IsoCamera.CamCharacter = IsoPlayer.players[int1];
										IsoPlayer.setInstance(IsoPlayer.players[int1]);
									}

									if (!GameServer.bServer) {
										IsoWorld.instance.CurrentCell.ChunkMap[int1].ProcessChunkPos(IsoCamera.CamCharacter);
									}
								}
							}
						}

						if (Core.bDebug) {
							this.debugTimes.add(System.nanoTime());
						}

						IsoWorld.instance.update();
						if (Core.bDebug) {
							this.debugTimes.add(System.nanoTime());
						}

						ZomboidRadio.getInstance().update();
						this.UpdateStuff();
						LuaEventManager.triggerEvent("OnTick", (double)this.numberTicks);
						this.numberTicks = Math.max(this.numberTicks + 1L, 0L);
					} catch (Exception exception2) {
						ExceptionLogger.logException(exception2);
						if (!GameServer.bServer) {
							if (GameClient.bClient) {
								for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
									IsoPlayer player2 = IsoPlayer.players[int2];
									if (player2 != null) {
										ClientPlayerDB.getInstance().clientSendNetworkPlayerInt(player2);
									}
								}

								WorldStreamer.instance.stop();
							}

							String string = Core.GameSaveWorld;
							createWorld(Core.GameSaveWorld + "_crash");
							copyWorld(string, Core.GameSaveWorld);
							if (GameClient.bClient) {
								if (PlayerDB.isAllow()) {
									PlayerDB.getInstance().saveLocalPlayersForce();
									PlayerDB.getInstance().m_canSavePlayers = false;
								}

								if (ClientPlayerDB.isAllow()) {
									ClientPlayerDB.getInstance().canSavePlayers = false;
								}
							}

							try {
								GameWindow.save(true);
							} catch (Throwable throwable2) {
								ExceptionLogger.logException(throwable2);
							}

							if (GameClient.bClient) {
								try {
									LuaEventManager.triggerEvent("OnPostSave");
								} catch (Exception exception3) {
									ExceptionLogger.logException(exception3);
								}

								if (ClientPlayerDB.isAllow()) {
									ClientPlayerDB.getInstance().close();
								}
							}
						}

						if (GameClient.bClient) {
							GameClient.instance.doDisconnect("crash");
						}

						return GameStateMachine.StateAction.Continue;
					}
				}
			} catch (Exception exception4) {
				System.err.println("IngameState.update caught an exception.");
				ExceptionLogger.logException(exception4);
			}

			if (Core.bDebug) {
				this.debugTimes.add(System.nanoTime());
			}

			if (!GameServer.bServer || ServerGUI.isCreated()) {
				ModelManager.instance.update();
			}

			if (Core.bDebug && FPSGraph.instance != null) {
				FPSGraph.instance.addUpdate(System.currentTimeMillis());
				FPSGraph.instance.update();
			}

			if (GameClient.bClient || GameServer.bServer) {
				ItemTransactionManager.update();
				MPStatistics.Update();
			}

			return GameStateMachine.StateAction.Remain;
		}
	}

	private static class s_performance {
		static final PerformanceProfileProbe render = new PerformanceProfileProbe("IngameState.render");
		static final PerformanceProfileProbe renderFrame = new PerformanceProfileProbe("IngameState.renderFrame");
		static final PerformanceProfileProbe renderFrameText = new PerformanceProfileProbe("IngameState.renderFrameText");
		static final PerformanceProfileProbe renderFrameUI = new PerformanceProfileProbe("IngameState.renderFrameUI");
		static final PerformanceProfileProbe update = new PerformanceProfileProbe("IngameState.update");
	}
}
