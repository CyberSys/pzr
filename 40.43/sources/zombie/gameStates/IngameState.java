package zombie.gameStates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import zombie.AmbientStreamManager;
import zombie.FliesSound;
import zombie.FrameLoader;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.LOSThread;
import zombie.LootRespawn;
import zombie.MapCollisionData;
import zombie.ReanimatedPlayers;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.TileAccessibilityWorker;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.Quests.QuestManager;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.Mover;
import zombie.ai.astar.Path;
import zombie.ai.astar.heuristics.ManhattanHeuristic;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorFactory;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.traits.TraitFactory;
import zombie.chat.ChatElement;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderSettings;
import zombie.core.physics.WorldSimulation;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.stash.StashSystem;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.utils.OnceEvery;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionGlobals;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.inventory.ItemSoundManager;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LightingThread;
import zombie.iso.LotHeader;
import zombie.iso.WorldStreamer;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.isoregion.IsoRegion;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.Temperature;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.meta.Meta;
import zombie.network.BodyDamageSync;
import zombie.network.ChunkChecksum;
import zombie.network.ClientServerMap;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PassengerMap;
import zombie.network.ServerGUI;
import zombie.network.ServerOptions;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.scripting.ScriptManager;
import zombie.spnetwork.SinglePlayerClient;
import zombie.spnetwork.SinglePlayerServer;
import zombie.ui.ActionProgressBar;
import zombie.ui.FPSGraph;
import zombie.ui.ServerPulseGraph;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIManager;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleCache;
import zombie.vehicles.VehicleIDMap;


public class IngameState extends GameState {
	public static boolean DebugPathfinding = false;
	public static boolean AlwaysDebugPathfinding = false;
	public static int WaitMul = 20;
	public static IngameState instance;
	static int last = -1;
	public long numberTicks = 0L;
	public boolean Paused = false;
	public float SaveDelay = 0.0F;
	boolean alt = false;
	static float xPos;
	static float yPos;
	static float offx;
	static float offy;
	static float zoom;
	public static float draww;
	public static float drawh;
	private float SadisticMusicDirectorTime;
	public static Long GameID = 0L;
	static HashMap ContainerTypes = new HashMap();
	int insanityScareCount = 5;
	boolean MDebounce = false;
	static int nSaveCycle = 1800;
	static boolean bDoChars = false;
	int insanitypic = -1;
	int timesincelastinsanity = 10000000;
	AStarPathFinder finder;
	boolean dbgChunkKeyDown = false;
	GameState RedirectState = null;
	boolean bDidServerDisconnectState = false;
	boolean fpsKeyDown = false;
	ArrayList debugTimes = new ArrayList();
	int tickCount = 0;
	public static ArrayList Frames = new ArrayList();
	OnceEvery replayUpdate = new OnceEvery(0.1F, false);
	int nReplay = 0;

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
		SpriteRenderer.instance.render((Texture)null, int3, int4, int2 * cell.getWidthInTiles(), int2 * cell.getHeightInTiles(), 0.7F, 0.7F, 0.7F, 1.0F);
		IsoGridSquare square = cell.getGridSquare(int5 + cell.ChunkMap[0].getWorldXMinTiles(), int6 + cell.ChunkMap[0].getWorldYMinTiles(), 0);
		int int7;
		if (square != null) {
			EnumSet enumSet = square.getProperties().getFlags();
			byte byte1 = 48;
			byte byte2 = 48;
			TextManager.instance.DrawString((double)byte2, (double)byte1, "SQUARE FLAGS", 1.0, 1.0, 1.0, 1.0);
			int7 = byte1 + 20;
			int int8 = byte2 + 8;
			int int9;
			for (int9 = 0; int9 < 64; ++int9) {
				if (enumSet.contains(int9)) {
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

		for (int int10 = 0; int10 < cell.getWidthInTiles(); ++int10) {
			for (int7 = 0; int7 < cell.getHeightInTiles(); ++int7) {
				IsoGridSquare square2 = cell.getGridSquare(int10 + cell.ChunkMap[0].getWorldXMinTiles(), int7 + cell.ChunkMap[0].getWorldYMinTiles(), int1);
				if (square2 != null) {
					if (!square2.getProperties().Is(IsoFlagType.solid) && !square2.getProperties().Is(IsoFlagType.solidtrans)) {
						if (!square2.getProperties().Is(IsoFlagType.exterior)) {
							SpriteRenderer.instance.render((Texture)null, int3 + int10 * int2, int4 + int7 * int2, int2, int2, 0.8F, 0.8F, 0.8F, 1.0F);
						}
					} else {
						SpriteRenderer.instance.render((Texture)null, int3 + int10 * int2, int4 + int7 * int2, int2, int2, 0.5F, 0.5F, 0.5F, 255.0F);
					}

					if (square2.Has(IsoObjectType.tree)) {
						SpriteRenderer.instance.render((Texture)null, int3 + int10 * int2, int4 + int7 * int2, int2, int2, 0.4F, 0.8F, 0.4F, 1.0F);
					}

					if (square2.getProperties().Is(IsoFlagType.collideN)) {
						SpriteRenderer.instance.render((Texture)null, int3 + int10 * int2, int4 + int7 * int2, int2, 1, 0.2F, 0.2F, 0.2F, 1.0F);
					}

					if (square2.getProperties().Is(IsoFlagType.collideW)) {
						SpriteRenderer.instance.render((Texture)null, int3 + int10 * int2, int4 + int7 * int2, 1, int2, 0.2F, 0.2F, 0.2F, 1.0F);
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

	public static float translatePointX(float float1) {
		return translatePointX(float1, xPos, zoom, offx);
	}

	public static float translatePointY(float float1) {
		return translatePointY(float1, yPos, zoom, offy);
	}

	public static void renderRect(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		float float9 = translatePointX(float1, xPos, zoom, offx);
		float float10 = translatePointY(float2, yPos, zoom, offy);
		float float11 = translatePointX(float1 + float3, xPos, zoom, offx);
		float float12 = translatePointY(float2 + float4, yPos, zoom, offy);
		float3 = float11 - float9;
		float4 = float12 - float10;
		if (!(float9 >= (float)Core.getInstance().getScreenWidth()) && !(float11 < 0.0F) && !(float10 >= (float)Core.getInstance().getScreenHeight()) && !(float12 < 0.0F)) {
			SpriteRenderer.instance.render((Texture)null, float9, float10, float3, float4, float5, float6, float7, float8);
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
				if (lotHeader != null) {
					for (int int10 = 0; int10 < lotHeader.Buildings.size(); ++int10) {
						BuildingDef buildingDef = (BuildingDef)lotHeader.Buildings.get(int10);
						if (!((BuildingDef)lotHeader.Buildings.get(int10)).isAllExplored() && buildingDef.bAlarmed) {
							renderRect((float)buildingDef.getX(), (float)buildingDef.getY(), (float)buildingDef.getW(), (float)buildingDef.getH(), 0.8F, 0.8F, 0.5F, 0.3F);
						} else {
							renderRect((float)buildingDef.getX(), (float)buildingDef.getY(), (float)buildingDef.getW(), (float)buildingDef.getH(), 0.5F, 0.5F, 0.8F, 0.3F);
						}
					}
				}
			}
		}
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
		OnceEvery.FPS = PerformanceSettings.LockFPS;
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
				if (!FrameLoader.bClient) {
					RainManager.Update();
				}
			} catch (Exception exception3) {
				ExceptionLogger.logException(exception3);
			}

			try {
				QuestManager.instance.Update();
			} catch (Exception exception4) {
				ExceptionLogger.logException(exception4);
			}

			Meta.instance.update();
			try {
				VirtualZombieManager.instance.update();
				MapCollisionData.instance.updateMain();
				ZombiePopulationManager.instance.updateMain();
				PolygonalMap2.instance.updateMain();
			} catch (Exception exception5) {
				ExceptionLogger.logException(exception5);
			} catch (Error error) {
				error.printStackTrace();
			}

			try {
				LootRespawn.update();
			} catch (Exception exception6) {
				ExceptionLogger.logException(exception6);
			}

			if (GameServer.bServer) {
				try {
					AmbientStreamManager.instance.update();
				} catch (Exception exception7) {
					ExceptionLogger.logException(exception7);
				}
			}

			if (GameClient.bClient) {
				try {
					BodyDamageSync.instance.update();
				} catch (Exception exception8) {
					ExceptionLogger.logException(exception8);
				}
			}

			if (!GameServer.bServer) {
				try {
					ItemSoundManager.update();
					FliesSound.instance.update();
					this.SadisticMusicDirectorTime += GameTime.getInstance().getGameWorldSecondsSinceLastUpdate();
					if (this.SadisticMusicDirectorTime > 20.0F) {
						LuaManager.call("SadisticMusicDirectorTick", (Object)null);
						this.SadisticMusicDirectorTime = 0.0F;
					}
				} catch (Exception exception9) {
					ExceptionLogger.logException(exception9);
				}
			}

			RenderSettings.getInstance().update();
			long long2 = System.nanoTime();
		}
	}

	public void enter() {
		boolean boolean1;
		label63: {
			if (Core.getInstance().supportsFBO()) {
				Core.getInstance();
				if (Core.OptionUIFBO) {
					boolean1 = true;
					break label63;
				}
			}

			boolean1 = false;
		}
		UIManager.useUIFBO = boolean1;
		if (!Core.getInstance().getUseShaders()) {
			Core.getInstance().RenderShader = null;
		}

		GameSounds.fix3DListenerPosition(false);
		IsoPlayer.instance.setModel(IsoPlayer.instance.isFemale() ? "kate" : "male");
		IsoPlayer.instance.updateUsername();
		IsoPlayer.instance.getInventory().addItemsToProcessItems();
		Core.getInstance().CalcCircle();
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
		try {
			MapCollisionData.instance.updateMain();
			ZombiePopulationManager.instance.updateMain();
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		} catch (Error error) {
			ExceptionLogger.logException(error);
		}

		if (!GameServer.bServer) {
			IsoWorld.instance.CurrentCell.ChunkMap[0].processAllLoadGridSquare();
			IsoWorld.instance.CurrentCell.ChunkMap[0].update();
			if (!GameClient.bClient) {
				LightingThread.instance.GameLoadingUpdate();
			}
		}

		IsoWorld.instance.CurrentCell.putInVehicle(IsoPlayer.instance);
		ClimateManager.getInstance().update();
		LuaEventManager.triggerEvent("OnGameStart");
		LuaEventManager.triggerEvent("OnLoad");
		if (GameClient.bClient) {
			GameClient.instance.sendPlayerConnect(IsoPlayer.instance);
			DebugLog.log("Waiting for player-connect response from server");
			for (; IsoPlayer.instance.OnlineID == -1; GameClient.instance.update()) {
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
		if (SteamUtils.isSteamModeEnabled()) {
			SteamFriends.UpdateRichPresenceConnectionInfo("", "");
		}

		UIManager.useUIFBO = false;
		if (ServerPulseGraph.instance != null) {
			ServerPulseGraph.instance.setVisible(false);
		}

		if (FPSGraph.instance != null) {
			FPSGraph.instance.setVisible(false);
		}

		UIManager.updateBeforeFadeOut();
		long long1 = Calendar.getInstance().getTimeInMillis();
		boolean boolean1 = UIManager.useUIFBO;
		UIManager.useUIFBO = false;
		while (true) {
			float float1 = Math.min(1.0F, (float)(Calendar.getInstance().getTimeInMillis() - long1) / 500.0F);
			boolean boolean2 = true;
			int int1;
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] != null) {
					IsoPlayer.instance = IsoPlayer.players[int1];
					IsoCamera.CamCharacter = IsoPlayer.players[int1];
					IsoSprite.globalOffsetX = -1;
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
			if (float1 >= 1.0F) {
				UIManager.useUIFBO = boolean1;
				while (WorldStreamer.instance.isBusy()) {
					try {
						Thread.sleep(1L);
					} catch (InterruptedException interruptedException) {
						interruptedException.printStackTrace();
					}
				}

				WorldStreamer.instance.stop();
				LightingThread.instance.stop();
				MapCollisionData.instance.stop();
				ZombiePopulationManager.instance.stop();
				PolygonalMap2.instance.stop();
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

				IsoPlayer.numPlayers = 1;
				Core.getInstance().OffscreenBuffer.destroy();
				WeatherFxMask.destroy();
				IsoRegion.reset();
				Temperature.reset();
				ZomboidRadio.getInstance().Reset();
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
				WorldStreamer.instance = new WorldStreamer();
				WorldSimulation.instance.destroy();
				WorldSimulation.instance = new WorldSimulation();
				VirtualZombieManager.instance = new VirtualZombieManager();
				ReanimatedPlayers.instance = new ReanimatedPlayers();
				ScriptManager.instance.Reset();
				GameSounds.Reset();
				LuaEventManager.Reset();
				MapObjects.Reset();
				CGlobalObjects.Reset();
				SGlobalObjects.Reset();
				AmbientStreamManager.instance.stop();
				SoundManager.instance.stop();
				IsoPlayer.instance = null;
				IsoCamera.CamCharacter = null;
				TutorialManager.instance.StealControl = false;
				UIManager.init();
				ScriptManager.instance.Reset();
				GameSounds.Reset();
				SurvivorFactory.Reset();
				ProfessionFactory.Reset();
				TraitFactory.Reset();
				ChooseGameInfo.Reset();
				LuaHookManager.Reset();
				LuaManager.init();
				JoypadManager.instance.Reset();
				GameKeyboard.doLuaKeyPressed = true;
				GameWindow.ActivatedJoyPad = null;
				GameWindow.OkToSaveOnExit = false;
				GameWindow.bLoadedAsClient = false;
				Core.bLastStand = false;
				Core.bTutorial = false;
				Core.getInstance().setChallenge(false);
				Core.getInstance().setForceSnow(false);
				Core.getInstance().setZombieGroupSound(true);
				SystemDisabler.Reset();
				Texture.nullTextures.clear();
				ZomboidFileSystem.instance.Reset();
				ZomboidFileSystem.instance.init();
				Core.OptionModsEnabled = true;
				ZomboidFileSystem.instance.loadMods();
				ScriptManager.instance.Load();
				try {
					LuaManager.LoadDirBase();
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
				}

				ZomboidGlobals.Load();
				LuaEventManager.triggerEvent("OnGameBoot");
				LOSThread.instance.finished = true;
				SoundManager.instance.resumeSoundAndMusic();
				IsoPlayer[] playerArray = IsoPlayer.players;
				int int3 = playerArray.length;
				for (int1 = 0; int1 < int3; ++int1) {
					IsoPlayer player = playerArray[int1];
					if (player != null) {
						player.dirtyRecalcGridStack = true;
					}
				}

				return;
			}

			try {
				Thread.sleep(33L);
			} catch (Exception exception2) {
			}
		}
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
	}

	public void FadeIn(int int1) {
		UIManager.FadeIn((double)int1);
	}

	public void FadeOut(int int1) {
		UIManager.FadeOut((double)int1);
	}

	public void renderframetext(int int1) {
		IndieGL.disableAlphaTest();
		IndieGL.glDisable(2929);
		ActionProgressBar actionProgressBar = UIManager.getProgressBar((double)int1);
		if (actionProgressBar != null && actionProgressBar.isVisible()) {
			actionProgressBar.render();
		}

		TextDrawObject.RenderBatch(int1);
		ChatElement.RenderBatch(int1);
		try {
			Core.getInstance().EndFrameText(int1);
		} catch (Exception exception) {
		}
	}

	public void renderframe(int int1) {
		if (IsoPlayer.instance == null) {
			IsoPlayer.instance = IsoPlayer.players[0];
			IsoCamera.CamCharacter = IsoPlayer.instance;
		}

		RenderSettings.getInstance().applyRenderSettings(int1);
		ActionProgressBar actionProgressBar = UIManager.getProgressBar((double)int1);
		if (actionProgressBar != null) {
			if (UIManager.getProgressBar((double)int1).getValue() > 0.0F && UIManager.getProgressBar((double)int1).getValue() < 1.0F) {
				float float1 = IsoUtils.XToScreen(IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY(), IsoPlayer.getInstance().getZ(), 0);
				float float2 = IsoUtils.YToScreen(IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY(), IsoPlayer.getInstance().getZ(), 0);
				float1 = float1 - IsoCamera.getOffX() - IsoPlayer.instance.offsetX;
				float2 = float2 - IsoCamera.getOffY() - IsoPlayer.instance.offsetY;
				float2 -= (float)(128 / (2 / Core.TileScale));
				float1 /= Core.getInstance().getZoom(int1);
				float2 /= Core.getInstance().getZoom(int1);
				float1 -= UIManager.getProgressBar((double)int1).width / 2.0F;
				float2 -= UIManager.getProgressBar((double)int1).height;
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player.getUserNameHeight() > 0) {
					float2 -= (float)(player.getUserNameHeight() + 2);
				}

				UIManager.getProgressBar((double)int1).setX((double)float1);
				UIManager.getProgressBar((double)int1).setY((double)float2);
				UIManager.getProgressBar((double)int1).setVisible(true);
				actionProgressBar.delayHide = 2;
			} else if (actionProgressBar.isVisible() && actionProgressBar.delayHide > 0 && --actionProgressBar.delayHide == 0) {
				actionProgressBar.setVisible(false);
			}
		}

		IndieGL.disableAlphaTest();
		IndieGL.glDisable(2929);
		if (IsoPlayer.instance != null && !IsoPlayer.instance.isAsleep() || UIManager.getFadeAlpha((double)int1) < 1.0F) {
			long long1 = System.nanoTime();
			IsoWorld.instance.render();
			RenderSettings.getInstance().legacyPostRender(int1);
			LuaEventManager.triggerEvent("OnPostRender");
		}

		WorldSoundManager.instance.render();
		if (GameClient.bClient) {
			ClientServerMap.render(int1);
			PassengerMap.render(int1);
		}

		LineDrawer.drawLines();
		try {
			Core.getInstance().EndFrame(int1);
		} catch (Exception exception) {
		}
	}

	public void renderframeui() {
		if (Core.getInstance().StartFrameUI()) {
			TextManager.instance.DrawTextFromGameWorld();
			UIManager.render();
			ZomboidRadio.getInstance().render();
			int int1;
			if (FrameLoader.bClient) {
				short short1 = 150;
				int int2 = Core.getInstance().getOffscreenWidth(0) - 20;
				TextManager.instance.DrawStringRight((double)int2, (double)short1, IsoPlayer.getInstance().getDescriptor().getForename() + " " + IsoPlayer.getInstance().getDescriptor().getSurname() + " - " + IsoPlayer.getInstance().getPing(), 1.0, 1.0, 1.0, 1.0);
				int1 = short1 + 12;
				for (int int3 = 0; int3 < IsoWorld.instance.CurrentCell.getRemoteSurvivorList().size(); ++int3) {
					IsoSurvivor survivor = (IsoSurvivor)IsoWorld.instance.CurrentCell.getRemoteSurvivorList().get(int3);
					TextManager.instance.DrawStringRight((double)int2, (double)int1, survivor.getDescriptor().getForename() + " " + survivor.getDescriptor().getSurname() + " - " + survivor.ping, 1.0, 1.0, 1.0, 1.0);
					int1 += 12;
				}
			}

			if (IsoWorld.instance.TotalSurvivorsDead > 0) {
			}

			if (Core.bDebug && IsoPlayer.instance != null && IsoPlayer.instance.GhostMode) {
				IsoWorld.instance.CurrentCell.ChunkMap[0].drawDebugChunkMap();
			}

			DeadBodyAtlas.instance.renderUI();
			if (GameClient.bClient && GameClient.accessLevel.equals("admin")) {
				if (ServerPulseGraph.instance == null) {
					ServerPulseGraph.instance = new ServerPulseGraph();
				}

				ServerPulseGraph.instance.update();
				ServerPulseGraph.instance.render();
			}

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
				for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					IsoPlayer player = IsoPlayer.players[int1];
					if (player != null && !player.isDead() && player.isAsleep()) {
						float float1 = GameClient.bFastForward ? GameTime.getInstance().ServerTimeOfDay : GameTime.getInstance().getTimeOfDay();
						float float2 = (float1 - (float)((int)float1)) * 60.0F;
						String string = "media/ui/SleepClock" + (int)float2 / 10 + ".png";
						Texture texture = Texture.getSharedTexture(string);
						if (texture == null) {
							break;
						}

						int int4 = IsoCamera.getScreenLeft(int1);
						int int5 = IsoCamera.getScreenTop(int1);
						int int6 = IsoCamera.getScreenWidth(int1);
						int int7 = IsoCamera.getScreenHeight(int1);
						SpriteRenderer.instance.render(texture, int4 + int6 / 2 - texture.getWidth() / 2, int5 + int7 / 2 - texture.getHeight() / 2, texture.getWidth(), texture.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F);
					}
				}
			}
		}

		Core.getInstance().EndFrameUI();
	}

	public void render() {
		IsoZombie.HighQualityZombiesDrawnThisFrame = 0;
		if (!AlwaysDebugPathfinding) {
			boolean boolean1 = true;
			int int1;
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] == null) {
					if (int1 == 0) {
						SpriteRenderer.instance.preRender();
					}
				} else {
					IsoPlayer.instance = IsoPlayer.players[int1];
					IsoCamera.CamCharacter = IsoPlayer.players[int1];
					Core.getInstance().StartFrame(int1, boolean1);
					IsoCamera.frameState.set(int1);
					boolean1 = false;
					IsoSprite.globalOffsetX = -1;
					this.renderframe(int1);
				}
			}

			Core.getInstance().RenderOffScreenBuffer();
			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] != null) {
					Core.getInstance().StartFrameText(int1);
					this.renderframetext(int1);
				}
			}

			UIManager.resize();
			this.renderframeui();
		}
	}

	public void StartMusic() {
	}

	public void StartMusic(String string) {
	}

	public GameStateMachine.StateAction update() {
		++this.tickCount;
		if (this.tickCount < 60) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] != null) {
					IsoPlayer.players[int1].dirtyRecalcGridStackTime = 20.0F;
				}
			}
		}

		long long1 = System.nanoTime();
		LuaEventManager.triggerEvent("OnTickEvenPaused", (double)this.numberTicks);
		if (Core.bDebug) {
			this.debugTimes.clear();
			this.debugTimes.add(System.nanoTime());
		}

		if (Core.bExiting) {
			Core.bExiting = false;
			if (GameClient.bClient) {
				WorldStreamer.instance.stop();
				GameClient.instance.doDisconnect("Quitting");
			}

			try {
				GameWindow.save(true);
			} catch (FileNotFoundException fileNotFoundException) {
				ExceptionLogger.logException(fileNotFoundException);
			} catch (IOException ioException) {
				ExceptionLogger.logException(ioException);
			}

			try {
				LuaEventManager.triggerEvent("OnPostSave");
			} catch (Exception exception) {
				exception.printStackTrace();
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
				if (GameKeyboard.isKeyDown(60)) {
					if (!this.dbgChunkKeyDown) {
						this.dbgChunkKeyDown = true;
						this.RedirectState = new DebugChunkState();
						return GameStateMachine.StateAction.Yield;
					}
				} else {
					this.dbgChunkKeyDown = false;
				}
			}

			if (Core.bDebug) {
				this.debugTimes.add(System.nanoTime());
			}

			if (this.finder == null && AlwaysDebugPathfinding) {
				this.finder = new AStarPathFinder((IsoGameCharacter)null, IsoWorld.instance.CurrentCell.getPathMap(), 800, true, new ManhattanHeuristic(1));
			}

			if (AlwaysDebugPathfinding) {
				this.finder.maxSearchDistance = 1000;
				Path path = this.finder.findPath(0, (Mover)null, 170, 110, 0, 85, 110, 0);
				return GameStateMachine.StateAction.Remain;
			} else {
				if (FrameLoader.bClient) {
				}

				if (IsoPlayer.DemoMode) {
					IsoCamera.updateDemo();
				}

				++this.timesincelastinsanity;
				if (!GameServer.bServer && GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Music")) && !this.MDebounce) {
					this.MDebounce = true;
					SoundManager.instance.AllowMusic = !SoundManager.instance.AllowMusic;
					if (!SoundManager.instance.AllowMusic) {
						SoundManager.instance.StopMusic();
						TutorialManager.instance.PrefMusic = null;
					}
				} else if (!GameServer.bServer && !GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Music"))) {
					this.MDebounce = false;
				}

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
								for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
									if (IsoPlayer.players[int2] != null && !IsoWorld.instance.CurrentCell.ChunkMap[int2].ignore) {
										if (!GameServer.bServer) {
											IsoCamera.CamCharacter = IsoPlayer.players[int2];
											IsoPlayer.instance = IsoPlayer.players[int2];
										}

										if (!GameServer.bServer) {
											IsoWorld.instance.CurrentCell.ChunkMap[int2].ProcessChunkPos(IsoCamera.CamCharacter);
										}
									}
								}
							}

							if (PerformanceSettings.LightingThread && !LightingThread.instance.newLightingMethod && LightingThread.instance.UpdateDone) {
								IsoCell.bReadAltLight = !IsoCell.bReadAltLight;
								LightingThread.instance.UpdateDone = false;
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
							++this.numberTicks;
							ScriptManager.instance.Trigger("OnTick");
						} catch (Exception exception2) {
							ExceptionLogger.logException(exception2);
							if (!GameServer.bServer) {
								if (GameClient.bClient) {
									WorldStreamer.instance.stop();
								}

								String string = Core.GameSaveWorld;
								createWorld(Core.GameSaveWorld + "_crash");
								copyWorld(string, Core.GameSaveWorld);
								GameWindow.save(true, false);
							}

							if (GameClient.bClient) {
								GameClient.instance.doDisconnect("Quitting");
							}

							return GameStateMachine.StateAction.Continue;
						}
					}
				} catch (Exception exception3) {
					ExceptionLogger.logException(exception3);
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

				return GameStateMachine.StateAction.Remain;
			}
		}
	}

	public static void copyWorld(String string, String string2) {
		String string3 = GameWindow.getGameModeCacheDir() + File.separator + string + File.separator;
		string3 = string3.replace("/", File.separator);
		string3 = string3.replace("\\", File.separator);
		String string4 = string3.substring(0, string3.lastIndexOf(File.separator));
		string4 = string4.replace("\\", "/");
		File file = new File(string4);
		string3 = GameWindow.getGameModeCacheDir() + File.separator + string2 + File.separator;
		string3 = string3.replace("/", File.separator);
		string3 = string3.replace("\\", File.separator);
		String string5 = string3.substring(0, string3.lastIndexOf(File.separator));
		string5 = string5.replace("\\", "/");
		File file2 = new File(string5);
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
		String string2 = GameWindow.getGameModeCacheDir() + File.separator + string + File.separator;
		string2 = string2.replace("/", File.separator);
		string2 = string2.replace("\\", File.separator);
		String string3 = string2.substring(0, string2.lastIndexOf(File.separator));
		string3 = string3.replace("\\", "/");
		File file = new File(string3);
		if (!file.exists()) {
			file.mkdirs();
		}

		Core.GameSaveWorld = string;
	}

	private void renderOverhead() {
		if (Core.bDebug) {
			if (Keyboard.isKeyDown(15)) {
				TextureID.UseFiltering = true;
				Texture.getSharedTexture("media/ui/white.png");
				IsoCell cell = IsoWorld.instance.CurrentCell;
				Texture texture = Texture.getSharedTexture("media/ui/white.png");
				byte byte1 = 0;
				byte byte2 = 2;
				int int1 = Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()) - cell.getWidthInTiles() * byte2;
				int int2 = Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()) - cell.getHeightInTiles() * byte2;
				texture.render(int1, int2, byte2 * cell.getWidthInTiles(), byte2 * cell.getHeightInTiles(), 0.7F, 0.7F, 0.7F, 1.0F);
				int int3;
				for (int3 = 0; int3 < cell.getWidthInTiles(); ++int3) {
					for (int int4 = 0; int4 < cell.getHeightInTiles(); ++int4) {
						IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()];
						IsoGridSquare square = cell.getGridSquare(int3 + chunkMap.getWorldXMinTiles(), int4 + chunkMap.getWorldYMinTiles(), byte1);
						if (square != null) {
							if (square.getProperties().Is(IsoFlagType.exterior)) {
								texture.render(int1 + int3 * byte2, int2 + int4 * byte2, byte2, byte2, 0.8F, 0.8F, 0.8F, 1.0F);
							}

							if (square.getProperties().Is(IsoFlagType.collideN)) {
							}

							if (square.getProperties().Is(IsoFlagType.collideW)) {
							}

							if (TileAccessibilityWorker.instance.current.getValue(int3, int4)) {
								texture.render(int1 + int3 * byte2, int2 + int4 * byte2, byte2, byte2, 0.0F, 1.0F, 0.0F, 0.3F);
							}
						}
					}
				}

				for (int3 = 0; int3 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++int3) {
					IsoMovingObject movingObject = (IsoMovingObject)IsoWorld.instance.CurrentCell.getObjectList().get(int3);
					if (movingObject.getZ() == (float)byte1) {
						if (movingObject instanceof IsoZombie) {
							texture.render(int1 + ((int)movingObject.getX() - IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles()) * byte2, int2 + ((int)movingObject.getY() - IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMinTiles()) * byte2, byte2, byte2, 1.0F, 0.0F, 0.0F, 1.0F);
							IsoZombie zombie = (IsoZombie)movingObject;
						}

						if (movingObject instanceof IsoSurvivor) {
							texture.render(int1 + (int)movingObject.getX() * byte2 + 1, int2 + (int)movingObject.getY() * byte2 + 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
							texture.render(int1 + (int)movingObject.getX() * byte2 - 1, int2 + (int)movingObject.getY() * byte2 - 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
							texture.render(int1 + (int)movingObject.getX() * byte2 - 1, int2 + (int)movingObject.getY() * byte2 + 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
							texture.render(int1 + (int)movingObject.getX() * byte2 + 1, int2 + (int)movingObject.getY() * byte2 - 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
							texture.render(int1 + (int)movingObject.getX() * byte2, int2 + (int)movingObject.getY() * byte2, byte2, byte2, 1.0F, 1.0F, 1.0F, 1.0F);
							IsoSurvivor survivor = (IsoSurvivor)movingObject;
							if (survivor.getPath() != null) {
								for (int int5 = 0; int5 < survivor.getPath().getLength(); ++int5) {
								}
							}
						}
					}
				}

				TextureID.UseFiltering = false;
			}
		}
	}

	private void updateOverheadReplay() {
		++this.nReplay;
		if (this.nReplay >= Frames.size()) {
			this.nReplay = 0;
		}

		if (!Keyboard.isKeyDown(15)) {
			this.nReplay = 0;
		}
	}

	private void renderOverheadReplay() {
		if (Keyboard.isKeyDown(15)) {
			if (!Frames.isEmpty()) {
				IsoWorld.Frame frame = (IsoWorld.Frame)Frames.get(this.nReplay);
				TextureID.UseFiltering = true;
				Texture.getSharedTexture("media/ui/white.png");
				IsoCell cell = IsoWorld.instance.CurrentCell;
				Texture texture = Texture.getSharedTexture("media/ui/white.png");
				byte byte1 = 0;
				byte byte2 = 4;
				for (int int1 = 0; int1 < cell.getWidthInTiles(); ++int1) {
					for (int int2 = 0; int2 < cell.getHeightInTiles(); ++int2) {
						IsoGridSquare square = cell.getGridSquare(int1, int2, byte1);
						if (square.getProperties().Is(IsoFlagType.exterior)) {
							texture.render(int1 * byte2, int2 * byte2, byte2, byte2, 0.8F, 0.8F, 0.8F, 1.0F);
						} else {
							texture.render(int1 * byte2, int2 * byte2, byte2, byte2, 0.7F, 0.7F, 0.7F, 1.0F);
						}

						if (square.getProperties().Is(IsoFlagType.solid) || square.getProperties().Is(IsoFlagType.solidtrans)) {
							texture.render(int1 * byte2, int2 * byte2, byte2, byte2, 0.5F, 0.5F, 0.5F, 255.0F);
						}

						if (square.getProperties().Is(IsoFlagType.collideN)) {
							texture.render(int1 * byte2, int2 * byte2, byte2, 1, 0.2F, 0.2F, 0.2F, 1.0F);
						}

						if (square.getProperties().Is(IsoFlagType.collideW)) {
							texture.render(int1 * byte2, int2 * byte2, 1, byte2, 0.2F, 0.2F, 0.2F, 1.0F);
						}
					}
				}

				Iterator iterator = frame.xPos.iterator();
				Iterator iterator2 = frame.yPos.iterator();
				Iterator iterator3 = frame.Type.iterator();
				while (iterator != null && iterator.hasNext()) {
					int int3 = (Integer)iterator.next();
					int int4 = (Integer)iterator2.next();
					int int5 = (Integer)iterator3.next();
					if (int5 == 0) {
						texture.render(int3 * byte2 + 1, int4 * byte2 + 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
						texture.render(int3 * byte2 - 1, int4 * byte2 - 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
						texture.render(int3 * byte2 - 1, int4 * byte2 + 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
						texture.render(int3 * byte2 + 1, int4 * byte2 - 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
						texture.render(int3 * byte2, int4 * byte2, byte2, byte2, 0.5F, 0.5F, 1.0F, 1.0F);
					}

					if (int5 == 1) {
						texture.render(int3 * byte2 + 1, int4 * byte2 + 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
						texture.render(int3 * byte2 - 1, int4 * byte2 - 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
						texture.render(int3 * byte2 - 1, int4 * byte2 + 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
						texture.render(int3 * byte2 + 1, int4 * byte2 - 1, byte2, byte2, 0.0F, 0.0F, 0.0F, 1.0F);
						texture.render(int3 * byte2, int4 * byte2, byte2, byte2, 1.0F, 1.0F, 1.0F, 1.0F);
					}

					if (int5 == 2) {
						texture.render(int3 * byte2, int4 * byte2, byte2, byte2, 1.0F, 0.0F, 0.0F, 1.0F);
					}
				}

				TextureID.UseFiltering = false;
			}
		}
	}
}
