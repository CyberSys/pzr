package zombie.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientSoundManager;
import zombie.AmbientStreamManager;
import zombie.DebugFileWatcher;
import zombie.GameProfiler;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.asset.AssetManagers;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Safety;
import zombie.characters.SafetySystemManager;
import zombie.characters.Stats;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.skills.CustomPerks;
import zombie.characters.skills.PerkFactory;
import zombie.commands.CommandBase;
import zombie.commands.PlayerType;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Languages;
import zombie.core.PerformanceSettings;
import zombie.core.ProxyPrintStream;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.backup.ZipBackup;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferReader;
import zombie.core.network.ByteBufferWriter;
import zombie.core.profiling.PerformanceProfileFrameProbe;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.raknet.RakVoice;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNodeAssetManager;
import zombie.core.skinnedmodel.model.AiSceneAsset;
import zombie.core.skinnedmodel.model.AiSceneAssetManager;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.model.AnimationAssetManager;
import zombie.core.skinnedmodel.model.MeshAssetManager;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.core.skinnedmodel.population.BeardStyles;
import zombie.core.skinnedmodel.population.ClothingDecals;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.population.ClothingItemAssetManager;
import zombie.core.skinnedmodel.population.HairStyles;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.stash.StashSystem;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureAssetManager;
import zombie.core.textures.TextureID;
import zombie.core.textures.TextureIDAssetManager;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.PortMapper;
import zombie.core.znet.SteamGameServer;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.erosion.ErosionMain;
import zombie.gameStates.IngameState;
import zombie.globalObjects.SGlobalObjectNetwork;
import zombie.globalObjects.SGlobalObjects;
import zombie.inventory.CompressIdenticalItems;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemPickerJava;
import zombie.inventory.RecipeManager;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Radio;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.ObjectsSyncRequests;
import zombie.iso.RoomDef;
import zombie.iso.SpawnPoints;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.weather.ClimateManager;
import zombie.network.chat.ChatServer;
import zombie.network.packets.ActionPacket;
import zombie.network.packets.AddXp;
import zombie.network.packets.CleanBurn;
import zombie.network.packets.DeadPlayerPacket;
import zombie.network.packets.DeadZombiePacket;
import zombie.network.packets.Disinfect;
import zombie.network.packets.EventPacket;
import zombie.network.packets.PlaySoundPacket;
import zombie.network.packets.PlayWorldSoundPacket;
import zombie.network.packets.PlayerDataRequestPacket;
import zombie.network.packets.PlayerPacket;
import zombie.network.packets.RemoveBullet;
import zombie.network.packets.RemoveCorpseFromMap;
import zombie.network.packets.RemoveGlass;
import zombie.network.packets.RequestDataPacket;
import zombie.network.packets.SafetyPacket;
import zombie.network.packets.StartFire;
import zombie.network.packets.Stitch;
import zombie.network.packets.StopSoundPacket;
import zombie.network.packets.SyncClothingPacket;
import zombie.network.packets.SyncInjuriesPacket;
import zombie.network.packets.SyncNonPvpZonePacket;
import zombie.network.packets.SyncSafehousePacket;
import zombie.network.packets.ValidatePacket;
import zombie.network.packets.WaveSignal;
import zombie.network.packets.hit.HitCharacterPacket;
import zombie.popman.MPDebugInfo;
import zombie.popman.NetworkZombieManager;
import zombie.popman.NetworkZombiePacker;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.sandbox.CustomSandboxOptions;
import zombie.savefile.ServerPlayerDB;
import zombie.scripting.ScriptManager;
import zombie.util.PZSQLUtils;
import zombie.util.PublicServerUtil;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.Clipper;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehiclesDB2;
import zombie.world.moddata.GlobalModData;
import zombie.worldMap.WorldMapRemotePlayer;
import zombie.worldMap.WorldMapRemotePlayers;


public class GameServer {
	public static final int MAX_PLAYERS = 512;
	public static final int TimeLimitForProcessPackets = 70;
	public static final int PacketsUpdateRate = 200;
	public static final int FPS = 10;
	private static final HashMap ccFilters = new HashMap();
	public static int test = 432432;
	public static int DEFAULT_PORT = 16261;
	public static int UDPPort = 16262;
	public static String IPCommandline = null;
	public static int PortCommandline = -1;
	public static int UDPPortCommandline = -1;
	public static Boolean SteamVACCommandline;
	public static boolean GUICommandline;
	public static boolean bServer = false;
	public static boolean bCoop = false;
	public static boolean bDebug = false;
	public static boolean bSoftReset = false;
	public static UdpEngine udpEngine;
	public static final HashMap IDToAddressMap = new HashMap();
	public static final HashMap IDToPlayerMap = new HashMap();
	public static final ArrayList Players = new ArrayList();
	public static float timeSinceKeepAlive = 0.0F;
	public static int MaxTicksSinceKeepAliveBeforeStall = 60;
	public static final HashSet DebugPlayer = new HashSet();
	public static int ResetID = 0;
	public static final ArrayList ServerMods = new ArrayList();
	public static final ArrayList WorkshopItems = new ArrayList();
	public static String[] WorkshopInstallFolders;
	public static long[] WorkshopTimeStamps;
	public static String ServerName = "servertest";
	public static final DiscordBot discordBot;
	public static String checksum;
	public static String GameMap;
	public static boolean bFastForward;
	public static final HashMap transactionIDMap;
	public static final ObjectsSyncRequests worldObjectsServerSyncReq;
	public static String ip;
	static int count;
	private static final UdpConnection[] SlotToConnection;
	private static final HashMap PlayerToAddressMap;
	private static final ArrayList alreadyRemoved;
	private static boolean bDone;
	private static boolean launched;
	private static final ArrayList consoleCommands;
	private static final HashMap MainLoopPlayerUpdate;
	private static final ConcurrentLinkedQueue MainLoopPlayerUpdateQ;
	private static final ConcurrentLinkedQueue MainLoopNetDataHighPriorityQ;
	private static final ConcurrentLinkedQueue MainLoopNetDataQ;
	private static final ArrayList MainLoopNetData2;
	private static final HashMap playerToCoordsMap;
	private static final HashMap playerMovedToFastMap;
	private static ByteBuffer large_file_bb;
	private static long previousSave;
	private String poisonousBerry = null;
	private String poisonousMushroom = null;
	private String difficulty = "Hardcore";
	private static int droppedPackets;
	private static int countOfDroppedPackets;
	private static int countOfDroppedConnections;
	public static UdpConnection removeZombiesConnection;
	private static UpdateLimit calcCountPlayersInRelevantPositionLimiter;
	private static UpdateLimit sendWorldMapPlayerPositionLimiter;
	public static LoginQueue loginQueue;
	private static int mainCycleExceptionLogCount;
	public static Thread MainThread;
	public static final ArrayList tempPlayers;

	public static void PauseAllClients() {
		String string = "[SERVERMSG] Server saving...Please wait";
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.StartPause.doPacket(byteBufferWriter);
			byteBufferWriter.putUTF(string);
			PacketTypes.PacketType.StartPause.send(udpConnection);
		}
	}

	public static void UnPauseAllClients() {
		String string = "[SERVERMSG] Server saved game...enjoy :)";
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.StopPause.doPacket(byteBufferWriter);
			byteBufferWriter.putUTF(string);
			PacketTypes.PacketType.StopPause.send(udpConnection);
		}
	}

	private static String parseIPFromCommandline(String[] stringArray, int int1, String string) {
		if (int1 == stringArray.length - 1) {
			DebugLog.log("expected argument after \"" + string + "\"");
			System.exit(0);
		} else if (stringArray[int1 + 1].trim().isEmpty()) {
			DebugLog.log("empty argument given to \"\" + option + \"\"");
			System.exit(0);
		} else {
			String[] stringArray2 = stringArray[int1 + 1].trim().split("\\.");
			if (stringArray2.length == 4) {
				for (int int2 = 0; int2 < 4; ++int2) {
					try {
						int int3 = Integer.parseInt(stringArray2[int2]);
						if (int3 < 0 || int3 > 255) {
							DebugLog.log("expected IP address after \"" + string + "\", got \"" + stringArray[int1 + 1] + "\"");
							System.exit(0);
						}
					} catch (NumberFormatException numberFormatException) {
						DebugLog.log("expected IP address after \"" + string + "\", got \"" + stringArray[int1 + 1] + "\"");
						System.exit(0);
					}
				}
			} else {
				DebugLog.log("expected IP address after \"" + string + "\", got \"" + stringArray[int1 + 1] + "\"");
				System.exit(0);
			}
		}

		return stringArray[int1 + 1];
	}

	private static int parsePortFromCommandline(String[] stringArray, int int1, String string) {
		if (int1 == stringArray.length - 1) {
			DebugLog.log("expected argument after \"" + string + "\"");
			System.exit(0);
		} else if (stringArray[int1 + 1].trim().isEmpty()) {
			DebugLog.log("empty argument given to \"" + string + "\"");
			System.exit(0);
		} else {
			try {
				return Integer.parseInt(stringArray[int1 + 1].trim());
			} catch (NumberFormatException numberFormatException) {
				DebugLog.log("expected an integer after \"" + string + "\"");
				System.exit(0);
			}
		}

		return -1;
	}

	private static boolean parseBooleanFromCommandline(String[] stringArray, int int1, String string) {
		if (int1 == stringArray.length - 1) {
			DebugLog.log("expected argument after \"" + string + "\"");
			System.exit(0);
		} else if (stringArray[int1 + 1].trim().isEmpty()) {
			DebugLog.log("empty argument given to \"" + string + "\"");
			System.exit(0);
		} else {
			String string2 = stringArray[int1 + 1].trim();
			if ("true".equalsIgnoreCase(string2)) {
				return true;
			}

			if ("false".equalsIgnoreCase(string2)) {
				return false;
			}

			DebugLog.log("expected true or false after \"" + string + "\"");
			System.exit(0);
		}

		return false;
	}

	public static void setupCoop() throws FileNotFoundException {
		CoopSlave.init();
	}

	public static void main(String[] stringArray) {
		MainThread = Thread.currentThread();
		bServer = true;
		bSoftReset = System.getProperty("softreset") != null;
		int int1;
		for (int1 = 0; int1 < stringArray.length; ++int1) {
			if (stringArray[int1] != null) {
				if (stringArray[int1].startsWith("-cachedir=")) {
					ZomboidFileSystem.instance.setCacheDir(stringArray[int1].replace("-cachedir=", "").trim());
				} else if (stringArray[int1].equals("-coop")) {
					bCoop = true;
				}
			}
		}

		String string;
		if (bCoop) {
			try {
				CoopSlave.initStreams();
			} catch (FileNotFoundException fileNotFoundException) {
				fileNotFoundException.printStackTrace();
			}
		} else {
			try {
				String string2 = ZomboidFileSystem.instance.getCacheDir();
				string = string2 + File.separator + "server-console.txt";
				FileOutputStream fileOutputStream = new FileOutputStream(string);
				PrintStream printStream = new PrintStream(fileOutputStream, true);
				System.setOut(new ProxyPrintStream(System.out, printStream));
				System.setErr(new ProxyPrintStream(System.err, printStream));
			} catch (FileNotFoundException fileNotFoundException2) {
				fileNotFoundException2.printStackTrace();
			}
		}

		DebugLog.init();
		LoggerManager.init();
		DebugLog.log("cachedir set to \"" + ZomboidFileSystem.instance.getCacheDir() + "\"");
		if (bCoop) {
			try {
				setupCoop();
				CoopSlave.status("UI_ServerStatus_Initialising");
			} catch (FileNotFoundException fileNotFoundException3) {
				fileNotFoundException3.printStackTrace();
				SteamUtils.shutdown();
				System.exit(37);
				return;
			}
		}

		PZSQLUtils.init();
		Clipper.init();
		Rand.init();
		if (System.getProperty("debug") != null) {
			bDebug = true;
			Core.bDebug = true;
		}

		DebugLog.General.println("version=%s demo=%s", Core.getInstance().getVersion(), false);
		DebugLog.General.println("revision=%s date=%s time=%s", "", "", "");
		int int2;
		String string3;
		int int3;
		int int4;
		for (int1 = 0; int1 < stringArray.length; ++int1) {
			if (stringArray[int1] != null) {
				String[] stringArray2;
				int int5;
				if (!stringArray[int1].startsWith("-disablelog=")) {
					if (stringArray[int1].startsWith("-debuglog=")) {
						stringArray2 = stringArray[int1].replace("-debuglog=", "").split(",");
						int5 = stringArray2.length;
						for (int2 = 0; int2 < int5; ++int2) {
							string3 = stringArray2[int2];
							try {
								DebugLog.setLogEnabled(DebugType.valueOf(string3), true);
							} catch (IllegalArgumentException illegalArgumentException) {
							}
						}
					} else if (stringArray[int1].equals("-adminusername")) {
						if (int1 == stringArray.length - 1) {
							DebugLog.log("expected argument after \"-adminusername\"");
							System.exit(0);
						} else if (!ServerWorldDatabase.isValidUserName(stringArray[int1 + 1].trim())) {
							DebugLog.log("invalid username given to \"-adminusername\"");
							System.exit(0);
						} else {
							ServerWorldDatabase.instance.CommandLineAdminUsername = stringArray[int1 + 1].trim();
							++int1;
						}
					} else if (stringArray[int1].equals("-adminpassword")) {
						if (int1 == stringArray.length - 1) {
							DebugLog.log("expected argument after \"-adminpassword\"");
							System.exit(0);
						} else if (stringArray[int1 + 1].trim().isEmpty()) {
							DebugLog.log("empty argument given to \"-adminpassword\"");
							System.exit(0);
						} else {
							ServerWorldDatabase.instance.CommandLineAdminPassword = stringArray[int1 + 1].trim();
							++int1;
						}
					} else if (!stringArray[int1].startsWith("-cachedir=")) {
						if (stringArray[int1].equals("-ip")) {
							IPCommandline = parseIPFromCommandline(stringArray, int1, "-ip");
							++int1;
						} else if (stringArray[int1].equals("-gui")) {
							GUICommandline = true;
						} else if (stringArray[int1].equals("-nosteam")) {
							System.setProperty("zomboid.steam", "0");
						} else if (stringArray[int1].equals("-statistic")) {
							int int6 = parsePortFromCommandline(stringArray, int1, "-statistic");
							if (int6 >= 0) {
								MPStatistic.getInstance().setPeriod(int6);
								MPStatistic.getInstance().writeEnabled(true);
							}
						} else if (stringArray[int1].equals("-port")) {
							PortCommandline = parsePortFromCommandline(stringArray, int1, "-port");
							++int1;
						} else if (stringArray[int1].equals("-udpport")) {
							UDPPortCommandline = parsePortFromCommandline(stringArray, int1, "-udpport");
							++int1;
						} else if (stringArray[int1].equals("-steamvac")) {
							SteamVACCommandline = parseBooleanFromCommandline(stringArray, int1, "-steamvac");
							++int1;
						} else if (stringArray[int1].equals("-servername")) {
							if (int1 == stringArray.length - 1) {
								DebugLog.log("expected argument after \"-servername\"");
								System.exit(0);
							} else if (stringArray[int1 + 1].trim().isEmpty()) {
								DebugLog.log("empty argument given to \"-servername\"");
								System.exit(0);
							} else {
								ServerName = stringArray[int1 + 1].trim();
								++int1;
							}
						} else if (stringArray[int1].equals("-coop")) {
							ServerWorldDatabase.instance.doAdmin = false;
						} else {
							DebugLog.log("unknown option \"" + stringArray[int1] + "\"");
						}
					}
				} else {
					stringArray2 = stringArray[int1].replace("-disablelog=", "").split(",");
					int5 = stringArray2.length;
					for (int2 = 0; int2 < int5; ++int2) {
						string3 = stringArray2[int2];
						if ("All".equals(string3)) {
							DebugType[] debugTypeArray = DebugType.values();
							int3 = debugTypeArray.length;
							for (int4 = 0; int4 < int3; ++int4) {
								DebugType debugType = debugTypeArray[int4];
								DebugLog.setLogEnabled(debugType, false);
							}
						} else {
							try {
								DebugLog.setLogEnabled(DebugType.valueOf(string3), false);
							} catch (IllegalArgumentException illegalArgumentException2) {
							}
						}
					}
				}
			}
		}

		DebugLog.log("server name is \"" + ServerName + "\"");
		string = isWorldVersionUnsupported();
		if (string != null) {
			DebugLog.log(string);
			CoopSlave.status(string);
		} else {
			SteamUtils.init();
			RakNetPeerInterface.init();
			ZombiePopulationManager.init();
			try {
				ZomboidFileSystem.instance.init();
				Languages.instance.init();
				Translator.loadFiles();
			} catch (Exception exception) {
				DebugLog.General.printException(exception, "Exception Thrown", LogSeverity.Error);
				DebugLog.General.println("Server Terminated.");
			}

			ServerOptions.instance.init();
			initClientCommandFilter();
			if (PortCommandline != -1) {
				ServerOptions.instance.DefaultPort.setValue(PortCommandline);
			}

			if (UDPPortCommandline != -1) {
				ServerOptions.instance.UDPPort.setValue(UDPPortCommandline);
			}

			if (SteamVACCommandline != null) {
				ServerOptions.instance.SteamVAC.setValue(SteamVACCommandline);
			}

			DEFAULT_PORT = ServerOptions.instance.DefaultPort.getValue();
			UDPPort = ServerOptions.instance.UDPPort.getValue();
			if (CoopSlave.instance != null) {
				ServerOptions.instance.ServerPlayerID.setValue("");
			}

			String string4;
			if (SteamUtils.isSteamModeEnabled()) {
				string4 = ServerOptions.instance.PublicName.getValue();
				if (string4 == null || string4.isEmpty()) {
					ServerOptions.instance.PublicName.setValue("My PZ Server");
				}
			}

			string4 = ServerOptions.instance.Map.getValue();
			if (string4 != null && !string4.trim().isEmpty()) {
				GameMap = string4.trim();
				if (GameMap.contains(";")) {
					String[] stringArray3 = GameMap.split(";");
					string4 = stringArray3[0];
				}

				Core.GameMap = string4.trim();
			}

			String string5 = ServerOptions.instance.Mods.getValue();
			int int7;
			String string6;
			if (string5 != null) {
				String[] stringArray4 = string5.split(";");
				String[] stringArray5 = stringArray4;
				int7 = stringArray4.length;
				for (int3 = 0; int3 < int7; ++int3) {
					string6 = stringArray5[int3];
					if (!string6.trim().isEmpty()) {
						ServerMods.add(string6.trim());
					}
				}
			}

			int int8;
			if (SteamUtils.isSteamModeEnabled()) {
				int2 = ServerOptions.instance.SteamVAC.getValue() ? 3 : 2;
				if (!SteamGameServer.Init(IPCommandline, DEFAULT_PORT, UDPPort, int2, Core.getInstance().getSteamServerVersion())) {
					SteamUtils.shutdown();
					return;
				}

				SteamGameServer.SetProduct("zomboid");
				SteamGameServer.SetGameDescription("Project Zomboid");
				SteamGameServer.SetModDir("zomboid");
				SteamGameServer.SetDedicatedServer(true);
				SteamGameServer.SetMaxPlayerCount(ServerOptions.getInstance().getMaxPlayers());
				SteamGameServer.SetServerName(ServerOptions.instance.PublicName.getValue());
				SteamGameServer.SetMapName(ServerOptions.instance.Map.getValue());
				if (ServerOptions.instance.Public.getValue()) {
					SteamGameServer.SetGameTags(CoopSlave.instance != null ? "hosted" : "");
				} else {
					SteamGameServer.SetGameTags("hidden" + (CoopSlave.instance != null ? ";hosted" : ""));
				}

				SteamGameServer.SetKeyValue("description", ServerOptions.instance.PublicDescription.getValue());
				SteamGameServer.SetKeyValue("version", Core.getInstance().getVersion());
				SteamGameServer.SetKeyValue("open", ServerOptions.instance.Open.getValue() ? "1" : "0");
				SteamGameServer.SetKeyValue("public", ServerOptions.instance.Public.getValue() ? "1" : "0");
				string3 = ServerOptions.instance.Mods.getValue();
				int7 = 0;
				String[] stringArray6 = string3.split(";");
				String[] stringArray7 = stringArray6;
				int int9 = stringArray6.length;
				for (int8 = 0; int8 < int9; ++int8) {
					String string7 = stringArray7[int8];
					if (!StringUtils.isNullOrWhitespace(string7)) {
						++int7;
					}
				}

				int int10;
				String string8;
				String[] stringArray8;
				String[] stringArray9;
				int int11;
				if (string3.length() > 128) {
					StringBuilder stringBuilder = new StringBuilder();
					stringArray8 = string3.split(";");
					stringArray9 = stringArray8;
					int11 = stringArray8.length;
					for (int10 = 0; int10 < int11; ++int10) {
						string8 = stringArray9[int10];
						if (stringBuilder.length() + 1 + string8.length() > 128) {
							break;
						}

						if (stringBuilder.length() > 0) {
							stringBuilder.append(';');
						}

						stringBuilder.append(string8);
					}

					string3 = stringBuilder.toString();
				}

				SteamGameServer.SetKeyValue("mods", string3);
				SteamGameServer.SetKeyValue("modCount", String.valueOf(int7));
				SteamGameServer.SetKeyValue("pvp", ServerOptions.instance.PVP.getValue() ? "1" : "0");
				if (bDebug) {
				}

				string6 = ServerOptions.instance.WorkshopItems.getValue();
				if (string6 != null) {
					stringArray8 = string6.split(";");
					stringArray9 = stringArray8;
					int11 = stringArray8.length;
					for (int10 = 0; int10 < int11; ++int10) {
						string8 = stringArray9[int10];
						string8 = string8.trim();
						if (!string8.isEmpty() && SteamUtils.isValidSteamID(string8)) {
							WorkshopItems.add(SteamUtils.convertStringToSteamID(string8));
						}
					}
				}

				SteamWorkshop.init();
				SteamGameServer.LogOnAnonymous();
				SteamGameServer.EnableHeartBeats(true);
				DebugLog.log("Waiting for response from Steam servers");
				while (true) {
					SteamUtils.runLoop();
					int9 = SteamGameServer.GetSteamServersConnectState();
					if (int9 == SteamGameServer.STEAM_SERVERS_CONNECTED) {
						if (!GameServerWorkshopItems.Install(WorkshopItems)) {
							return;
						}

						break;
					}

					if (int9 == SteamGameServer.STEAM_SERVERS_CONNECTFAILURE) {
						DebugLog.log("Failed to connect to Steam servers");
						SteamUtils.shutdown();
						return;
					}

					try {
						Thread.sleep(100L);
					} catch (InterruptedException interruptedException) {
					}
				}
			}

			ZipBackup.onStartup();
			ZipBackup.onVersion();
			int2 = 0;
			try {
				ServerWorldDatabase.instance.create();
			} catch (ClassNotFoundException | SQLException error) {
				error.printStackTrace();
			}

			if (ServerOptions.instance.UPnP.getValue()) {
				DebugLog.log("Router detection/configuration starting.");
				DebugLog.log("If the server hangs here, set UPnP=false.");
				PortMapper.startup();
				if (PortMapper.discover()) {
					DebugLog.log("UPnP-enabled internet gateway found: " + PortMapper.getGatewayInfo());
					string3 = PortMapper.getExternalAddress();
					DebugLog.log("External IP address: " + string3);
					DebugLog.log("trying to setup port forwarding rules...");
					int7 = 86400;
					boolean boolean1 = true;
					if (PortMapper.addMapping(DEFAULT_PORT, DEFAULT_PORT, "PZ Server default port", "UDP", int7, boolean1)) {
						DebugLog.log(DebugType.Network, "Default port has been mapped successfully");
					} else {
						DebugLog.log(DebugType.Network, "Failed to map default port");
					}

					if (SteamUtils.isSteamModeEnabled()) {
						int4 = ServerOptions.instance.UDPPort.getValue();
						if (PortMapper.addMapping(int4, int4, "PZ Server UDPPort", "UDP", int7, boolean1)) {
							DebugLog.log(DebugType.Network, "AdditionUDPPort has been mapped successfully");
						} else {
							DebugLog.log(DebugType.Network, "Failed to map AdditionUDPPort");
						}
					}
				} else {
					DebugLog.log(DebugType.Network, "No UPnP-enabled Internet gateway found, you must configure port forwarding on your gateway manually in order to make your server accessible from the Internet.");
				}
			}

			Core.GameMode = "Multiplayer";
			bDone = false;
			DebugLog.log(DebugType.Network, "Initialising Server Systems...");
			CoopSlave.status("UI_ServerStatus_Initialising");
			try {
				doMinimumInit();
			} catch (Exception exception2) {
				DebugLog.General.printException(exception2, "Exception Thrown", LogSeverity.Error);
				DebugLog.General.println("Server Terminated.");
			}

			LosUtil.init(100, 100);
			ChatServer.getInstance().init();
			DebugLog.log(DebugType.Network, "Loading world...");
			CoopSlave.status("UI_ServerStatus_LoadingWorld");
			try {
				ClimateManager.setInstance(new ClimateManager());
				IsoWorld.instance.init();
			} catch (Exception exception3) {
				DebugLog.General.printException(exception3, "Exception Thrown", LogSeverity.Error);
				DebugLog.General.println("Server Terminated.");
				CoopSlave.status("UI_ServerStatus_Terminated");
				return;
			}

			File file = ZomboidFileSystem.instance.getFileInCurrentSave("z_outfits.bin");
			if (!file.exists()) {
				ServerOptions.instance.changeOption("ResetID", (new Integer(Rand.Next(100000000))).toString());
			}

			try {
				SpawnPoints.instance.initServer2();
			} catch (Exception exception4) {
				exception4.printStackTrace();
			}

			LuaEventManager.triggerEvent("OnGameTimeLoaded");
			SGlobalObjects.initSystems();
			SoundManager.instance = new SoundManager();
			AmbientStreamManager.instance = new AmbientSoundManager();
			AmbientStreamManager.instance.init();
			ServerMap.instance.LastSaved = System.currentTimeMillis();
			VehicleManager.instance = new VehicleManager();
			ServerPlayersVehicles.instance.init();
			DebugOptions.instance.init();
			GameProfiler.init();
			try {
				startServer();
			} catch (ConnectException connectException) {
				connectException.printStackTrace();
				SteamUtils.shutdown();
				return;
			}

			if (SteamUtils.isSteamModeEnabled()) {
				DebugLog.log("##########\nServer Steam ID " + SteamGameServer.GetSteamID() + "\n##########");
			}

			UpdateLimit updateLimit = new UpdateLimit(100L);
			PerformanceSettings.setLockFPS(10);
			IngameState ingameState = new IngameState();
			float float1 = 0.0F;
			float[] floatArray = new float[20];
			for (int8 = 0; int8 < 20; ++int8) {
				floatArray[int8] = (float)PerformanceSettings.getLockFPS();
			}

			float float2 = (float)PerformanceSettings.getLockFPS();
			long long1 = System.currentTimeMillis();
			long long2 = System.currentTimeMillis();
			if (!SteamUtils.isSteamModeEnabled()) {
				PublicServerUtil.init();
				PublicServerUtil.insertOrUpdate();
			}

			ServerLOS.init();
			NetworkAIParams.Init();
			int int12 = ServerOptions.instance.RCONPort.getValue();
			String string9 = ServerOptions.instance.RCONPassword.getValue();
			if (int12 != 0 && string9 != null && !string9.isEmpty()) {
				String string10 = System.getProperty("rconlo");
				RCONServer.init(int12, string9, string10 != null);
			}

			LuaManager.GlobalObject.refreshAnimSets(true);
			while (!bDone) {
				try {
					long long3 = System.nanoTime();
					MPStatistics.countServerNetworkingFPS();
					MainLoopNetData2.clear();
					IZomboidPacket iZomboidPacket;
					for (iZomboidPacket = (IZomboidPacket)MainLoopNetDataHighPriorityQ.poll(); iZomboidPacket != null; iZomboidPacket = (IZomboidPacket)MainLoopNetDataHighPriorityQ.poll()) {
						MainLoopNetData2.add(iZomboidPacket);
					}

					MPStatistic.getInstance().setPacketsLength((long)MainLoopNetData2.size());
					IZomboidPacket iZomboidPacket2;
					int int13;
					for (int13 = 0; int13 < MainLoopNetData2.size(); ++int13) {
						iZomboidPacket2 = (IZomboidPacket)MainLoopNetData2.get(int13);
						UdpConnection udpConnection;
						if (iZomboidPacket2.isConnect()) {
							udpConnection = ((GameServer.DelayedConnection)iZomboidPacket2).connection;
							LoggerManager.getLogger("user").write("added connection index=" + udpConnection.index + " " + ((GameServer.DelayedConnection)iZomboidPacket2).hostString);
							udpEngine.connections.add(udpConnection);
						} else if (iZomboidPacket2.isDisconnect()) {
							udpConnection = ((GameServer.DelayedConnection)iZomboidPacket2).connection;
							LoginQueue.disconnect(udpConnection);
							LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + udpConnection.username + "\" removed connection index=" + udpConnection.index);
							udpEngine.connections.remove(udpConnection);
							disconnect(udpConnection, "receive-disconnect");
						} else {
							mainLoopDealWithNetData((ZomboidNetData)iZomboidPacket2);
						}
					}

					MainLoopPlayerUpdate.clear();
					for (iZomboidPacket = (IZomboidPacket)MainLoopPlayerUpdateQ.poll(); iZomboidPacket != null; iZomboidPacket = (IZomboidPacket)MainLoopPlayerUpdateQ.poll()) {
						ZomboidNetData zomboidNetData = (ZomboidNetData)iZomboidPacket;
						long long4 = zomboidNetData.connection * 4L + (long)zomboidNetData.buffer.getShort(0);
						ZomboidNetData zomboidNetData2 = (ZomboidNetData)MainLoopPlayerUpdate.put(long4, zomboidNetData);
						if (zomboidNetData2 != null) {
							ZomboidNetDataPool.instance.discard(zomboidNetData2);
						}
					}

					MainLoopNetData2.clear();
					MainLoopNetData2.addAll(MainLoopPlayerUpdate.values());
					MainLoopPlayerUpdate.clear();
					MPStatistic.getInstance().setPacketsLength((long)MainLoopNetData2.size());
					for (int13 = 0; int13 < MainLoopNetData2.size(); ++int13) {
						iZomboidPacket2 = (IZomboidPacket)MainLoopNetData2.get(int13);
						GameServer.s_performance.mainLoopDealWithNetData.invokeAndMeasure((ZomboidNetData)iZomboidPacket2, GameServer::mainLoopDealWithNetData);
					}

					MainLoopNetData2.clear();
					for (iZomboidPacket = (IZomboidPacket)MainLoopNetDataQ.poll(); iZomboidPacket != null; iZomboidPacket = (IZomboidPacket)MainLoopNetDataQ.poll()) {
						MainLoopNetData2.add(iZomboidPacket);
					}

					for (int13 = 0; int13 < MainLoopNetData2.size(); ++int13) {
						if (int13 % 10 == 0 && (System.nanoTime() - long3) / 1000000L > 70L) {
							if (droppedPackets == 0) {
								DebugLog.log("Server is too busy. Server will drop updates of vehicle\'s physics. Server is closed for new connections.");
							}

							droppedPackets += 2;
							countOfDroppedPackets += MainLoopNetData2.size() - int13;
							break;
						}

						iZomboidPacket2 = (IZomboidPacket)MainLoopNetData2.get(int13);
						GameServer.s_performance.mainLoopDealWithNetData.invokeAndMeasure((ZomboidNetData)iZomboidPacket2, GameServer::mainLoopDealWithNetData);
					}

					MainLoopNetData2.clear();
					if (droppedPackets == 1) {
						DebugLog.log("Server is working normal. Server will not drop updates of vehicle\'s physics. The server is open for new connections. Server dropped " + countOfDroppedPackets + " packets and " + countOfDroppedConnections + " connections.");
						countOfDroppedPackets = 0;
						countOfDroppedConnections = 0;
					}

					droppedPackets = Math.max(0, Math.min(1000, droppedPackets - 1));
					if (!updateLimit.Check()) {
						long long5 = PZMath.clamp((5000000L - System.nanoTime() + long3) / 1000000L, 0L, 100L);
						if (long5 > 0L) {
							try {
								MPStatistic.getInstance().Main.StartSleep();
								Thread.sleep(long5);
								MPStatistic.getInstance().Main.EndSleep();
							} catch (InterruptedException interruptedException2) {
								interruptedException2.printStackTrace();
							}
						}
					} else {
						MPStatistic.getInstance().Main.Start();
						++IsoCamera.frameState.frameCount;
						GameServer.s_performance.frameStep.start();
						try {
							timeSinceKeepAlive += GameTime.getInstance().getMultiplier();
							MPStatistic.getInstance().ServerMapPreupdate.Start();
							ServerMap.instance.preupdate();
							MPStatistic.getInstance().ServerMapPreupdate.End();
							int int14;
							synchronized (consoleCommands) {
								for (int14 = 0; int14 < consoleCommands.size(); ++int14) {
									String string11 = (String)consoleCommands.get(int14);
									try {
										if (CoopSlave.instance == null || !CoopSlave.instance.handleCommand(string11)) {
											System.out.println(handleServerCommand(string11, (UdpConnection)null));
										}
									} catch (Exception exception5) {
										exception5.printStackTrace();
									}
								}

								consoleCommands.clear();
							}

							if (removeZombiesConnection != null) {
								NetworkZombieManager.removeZombies(removeZombiesConnection);
								removeZombiesConnection = null;
							}

							GameServer.s_performance.RCONServerUpdate.invokeAndMeasure(RCONServer::update);
							try {
								MapCollisionData.instance.updateGameState();
								MPStatistic.getInstance().IngameStateUpdate.Start();
								ingameState.update();
								MPStatistic.getInstance().IngameStateUpdate.End();
								VehicleManager.instance.serverUpdate();
							} catch (Exception exception6) {
								exception6.printStackTrace();
							}

							int13 = 0;
							int14 = 0;
							for (int int15 = 0; int15 < Players.size(); ++int15) {
								IsoPlayer player = (IsoPlayer)Players.get(int15);
								if (player.isAlive()) {
									if (!IsoWorld.instance.CurrentCell.getObjectList().contains(player)) {
										IsoWorld.instance.CurrentCell.getObjectList().add(player);
									}

									++int14;
									if (player.isAsleep()) {
										++int13;
									}
								}

								ServerMap.instance.characterIn(player);
							}

							setFastForward(ServerOptions.instance.SleepAllowed.getValue() && int14 > 0 && int13 == int14);
							boolean boolean2 = calcCountPlayersInRelevantPositionLimiter.Check();
							int int16;
							int int17;
							UdpConnection udpConnection2;
							for (int17 = 0; int17 < udpEngine.connections.size(); ++int17) {
								udpConnection2 = (UdpConnection)udpEngine.connections.get(int17);
								if (boolean2) {
									udpConnection2.calcCountPlayersInRelevantPosition();
								}

								for (int16 = 0; int16 < 4; ++int16) {
									Vector3 vector3 = udpConnection2.connectArea[int16];
									if (vector3 != null) {
										ServerMap.instance.characterIn((int)vector3.x, (int)vector3.y, (int)vector3.z);
									}

									ClientServerMap.characterIn(udpConnection2, int16);
								}

								if (udpConnection2.playerDownloadServer != null) {
									udpConnection2.playerDownloadServer.update();
								}
							}

							for (int17 = 0; int17 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++int17) {
								IsoMovingObject movingObject = (IsoMovingObject)IsoWorld.instance.CurrentCell.getObjectList().get(int17);
								if (movingObject instanceof IsoPlayer && !Players.contains(movingObject)) {
									DebugLog.log("Disconnected player in CurrentCell.getObjectList() removed");
									IsoWorld.instance.CurrentCell.getObjectList().remove(int17--);
								}
							}

							++int2;
							if (int2 > 150) {
								for (int17 = 0; int17 < udpEngine.connections.size(); ++int17) {
									udpConnection2 = (UdpConnection)udpEngine.connections.get(int17);
									try {
										if (udpConnection2.username == null && !udpConnection2.awaitingCoopApprove && !LoginQueue.isInTheQueue(udpConnection2) && udpConnection2.isConnectionAttemptTimeout()) {
											disconnect(udpConnection2, "connection-attempt-timeout");
											udpEngine.forceDisconnect(udpConnection2.getConnectedGUID(), "connection-attempt-timeout");
										}
									} catch (Exception exception7) {
										exception7.printStackTrace();
									}
								}

								int2 = 0;
							}

							worldObjectsServerSyncReq.serverSendRequests(udpEngine);
							MPStatistic.getInstance().ServerMapPostupdate.Start();
							ServerMap.instance.postupdate();
							MPStatistic.getInstance().ServerMapPostupdate.End();
							try {
								ServerGUI.update();
							} catch (Exception exception8) {
								exception8.printStackTrace();
							}

							long2 = long1;
							long1 = System.currentTimeMillis();
							long long6 = long1 - long2;
							float1 = 1000.0F / (float)long6;
							if (!Float.isNaN(float1)) {
								float2 = (float)((double)float2 + Math.min((double)(float1 - float2) * 0.05, 1.0));
							}

							GameTime.instance.FPSMultiplier = 60.0F / float2;
							launchCommandHandler();
							MPStatistic.getInstance().process(long6);
							if (!SteamUtils.isSteamModeEnabled()) {
								PublicServerUtil.update();
								PublicServerUtil.updatePlayerCountIfChanged();
							}

							for (int16 = 0; int16 < udpEngine.connections.size(); ++int16) {
								UdpConnection udpConnection3 = (UdpConnection)udpEngine.connections.get(int16);
								if (udpConnection3.checksumState == UdpConnection.ChecksumState.Different && udpConnection3.checksumTime + 8000L < System.currentTimeMillis()) {
									DebugLog.log("timed out connection because checksum was different");
									udpConnection3.checksumState = UdpConnection.ChecksumState.Init;
									udpConnection3.forceDisconnect("checksum-timeout");
								} else {
									udpConnection3.validator.update();
									if (!udpConnection3.chunkObjectState.isEmpty()) {
										for (int int18 = 0; int18 < udpConnection3.chunkObjectState.size(); int18 += 2) {
											short short1 = udpConnection3.chunkObjectState.get(int18);
											short short2 = udpConnection3.chunkObjectState.get(int18 + 1);
											if (!udpConnection3.RelevantTo((float)(short1 * 10 + 5), (float)(short2 * 10 + 5), (float)(udpConnection3.ChunkGridWidth * 4 * 10))) {
												udpConnection3.chunkObjectState.remove(int18, 2);
												int18 -= 2;
											}
										}
									}
								}
							}

							if (sendWorldMapPlayerPositionLimiter.Check()) {
								try {
									sendWorldMapPlayerPosition();
								} catch (Exception exception9) {
									boolean boolean3 = true;
								}
							}

							if (CoopSlave.instance != null) {
								CoopSlave.instance.update();
								if (CoopSlave.instance.masterLost()) {
									DebugLog.log("Coop master is not responding, terminating");
									ServerMap.instance.QueueQuit();
								}
							}

							LoginQueue.update();
							ZipBackup.onPeriod();
							SteamUtils.runLoop();
							GameWindow.fileSystem.updateAsyncTransactions();
						} catch (Exception exception10) {
							if (mainCycleExceptionLogCount-- > 0) {
								DebugLog.Multiplayer.printException(exception10, "Server processing error", LogSeverity.Error);
							}
						} finally {
							GameServer.s_performance.frameStep.end();
						}
					}
				} catch (Exception exception11) {
					if (mainCycleExceptionLogCount-- > 0) {
						DebugLog.Multiplayer.printException(exception11, "Server error", LogSeverity.Error);
					}
				}
			}

			CoopSlave.status("UI_ServerStatus_Terminated");
			DebugLog.log(DebugType.Network, "Server exited");
			ServerGUI.shutdown();
			ServerPlayerDB.getInstance().close();
			VehiclesDB2.instance.Reset();
			SteamUtils.shutdown();
			System.exit(0);
		}
	}

	private static void launchCommandHandler() {
		if (!launched) {
			launched = true;
			(new Thread(ThreadGroups.Workers, ()->{
				try {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
					while (true) {
						String string = bufferedReader.readLine();
						if (string == null) {
							consoleCommands.add("process-status@eof");
							break;
						}

						if (!string.isEmpty()) {
							System.out.println("command entered via server console (System.in): \"" + string + "\"");
							synchronized (consoleCommands) {
								consoleCommands.add(string);
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}, "command handler")).start();
		}
	}

	public static String rcon(String string) {
		try {
			return handleServerCommand(string, (UdpConnection)null);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return null;
		}
	}

	private static String handleServerCommand(String string, UdpConnection udpConnection) {
		if (string == null) {
			return null;
		} else {
			String string2 = "admin";
			String string3 = "admin";
			if (udpConnection != null) {
				string2 = udpConnection.username;
				string3 = PlayerType.toString(udpConnection.accessLevel);
			}

			if (udpConnection != null && udpConnection.isCoopHost) {
				string3 = "admin";
			}

			Class javaClass = CommandBase.findCommandCls(string);
			if (javaClass != null) {
				Constructor constructor = javaClass.getConstructors()[0];
				try {
					CommandBase commandBase = (CommandBase)constructor.newInstance(string2, string3, string, udpConnection);
					return commandBase.Execute();
				} catch (InvocationTargetException invocationTargetException) {
					invocationTargetException.printStackTrace();
					return "A InvocationTargetException error occured";
				} catch (IllegalAccessException illegalAccessException) {
					illegalAccessException.printStackTrace();
					return "A IllegalAccessException error occured";
				} catch (InstantiationException instantiationException) {
					instantiationException.printStackTrace();
					return "A InstantiationException error occured";
				} catch (SQLException sQLException) {
					sQLException.printStackTrace();
					return "A SQL error occured";
				}
			} else {
				return "Unknown command " + string;
			}
		}
	}

	public static void sendTeleport(IsoPlayer player, float float1, float float2, float float3) {
		UdpConnection udpConnection = getConnectionFromPlayer(player);
		if (udpConnection == null) {
			DebugLog.log("No connection found for user " + player.getUsername());
		} else {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.Teleport.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)0);
			byteBufferWriter.putFloat(float1);
			byteBufferWriter.putFloat(float2);
			byteBufferWriter.putFloat(float3);
			PacketTypes.PacketType.Teleport.send(udpConnection);
			if (udpConnection.players[0] != null && udpConnection.players[0].getNetworkCharacterAI() != null) {
				udpConnection.players[0].getNetworkCharacterAI().resetSpeedLimiter();
			}
		}
	}

	static void receiveTeleport(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		IsoPlayer player = getPlayerByRealUserName(string);
		if (player != null) {
			UdpConnection udpConnection2 = getConnectionFromPlayer(player);
			if (udpConnection2 != null) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.Teleport.doPacket(byteBufferWriter);
				byteBufferWriter.putByte((byte)player.PlayerIndex);
				byteBufferWriter.putFloat(float1);
				byteBufferWriter.putFloat(float2);
				byteBufferWriter.putFloat(float3);
				PacketTypes.PacketType.Teleport.send(udpConnection2);
				if (player.getNetworkCharacterAI() != null) {
					player.getNetworkCharacterAI().resetSpeedLimiter();
				}

				if (player.isAsleep()) {
					player.setAsleep(false);
					player.setAsleepTime(0.0F);
					sendWakeUpPlayer(player, (UdpConnection)null);
				}
			}
		}
	}

	public static void sendPlayerExtraInfo(IsoPlayer player, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
			PacketTypes.PacketType.ExtraInfo.doPacket(byteBufferWriter);
			byteBufferWriter.putShort(player.OnlineID);
			byteBufferWriter.putUTF(player.accessLevel);
			byteBufferWriter.putByte((byte)(player.isGodMod() ? 1 : 0));
			byteBufferWriter.putByte((byte)(player.isGhostMode() ? 1 : 0));
			byteBufferWriter.putByte((byte)(player.isInvisible() ? 1 : 0));
			byteBufferWriter.putByte((byte)(player.isNoClip() ? 1 : 0));
			byteBufferWriter.putByte((byte)(player.isShowAdminTag() ? 1 : 0));
			PacketTypes.PacketType.ExtraInfo.send(udpConnection2);
		}
	}

	static void receiveExtraInfo(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		boolean boolean1 = byteBuffer.get() == 1;
		boolean boolean2 = byteBuffer.get() == 1;
		boolean boolean3 = byteBuffer.get() == 1;
		boolean boolean4 = byteBuffer.get() == 1;
		boolean boolean5 = byteBuffer.get() == 1;
		boolean boolean6 = byteBuffer.get() == 1;
		IsoPlayer player = getPlayerFromConnection(udpConnection, short2);
		if (player != null) {
			player.setGodMod(boolean1);
			player.setGhostMode(boolean2);
			player.setInvisible(boolean3);
			player.setNoClip(boolean4);
			player.setShowAdminTag(boolean5);
			player.setCanHearAll(boolean6);
			sendPlayerExtraInfo(player, udpConnection);
		}
	}

	static void receiveAddXp(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		AddXp addXp = new AddXp();
		addXp.parse(byteBuffer, udpConnection);
		if (addXp.isConsistent() && addXp.validate(udpConnection)) {
			if (!canModifyPlayerStats(udpConnection, addXp.target.getCharacter())) {
				PacketTypes.PacketType.AddXP.onUnauthorized(udpConnection);
			} else {
				addXp.process();
				if (canModifyPlayerStats(udpConnection, (IsoPlayer)null)) {
					addXp.target.getCharacter().getXp().recalcSumm();
				}

				for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(addXp.target.getCharacter())) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.AddXP.doPacket(byteBufferWriter);
						addXp.write(byteBufferWriter);
						PacketTypes.PacketType.AddXP.send(udpConnection2);
					}
				}
			}
		}
	}

	private static boolean canSeePlayerStats(UdpConnection udpConnection) {
		return udpConnection.accessLevel != 1;
	}

	private static boolean canModifyPlayerStats(UdpConnection udpConnection, IsoPlayer player) {
		return (udpConnection.accessLevel & 56) != 0 || udpConnection.havePlayer(player);
	}

	static void receiveSyncXP(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getShort());
		if (player != null) {
			if (!canModifyPlayerStats(udpConnection, player)) {
				PacketTypes.PacketType.SyncXP.onUnauthorized(udpConnection);
			} else {
				if (player != null && !player.isDead()) {
					try {
						player.getXp().load(byteBuffer, 195);
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}

					for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
						UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
						if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
							ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
							PacketTypes.PacketType.SyncXP.doPacket(byteBufferWriter);
							byteBufferWriter.putShort(player.getOnlineID());
							try {
								player.getXp().save(byteBufferWriter.bb);
							} catch (IOException ioException2) {
								ioException2.printStackTrace();
							}

							PacketTypes.PacketType.SyncXP.send(udpConnection2);
						}
					}
				}
			}
		}
	}

	static void receiveChangePlayerStats(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			String string = GameWindow.ReadString(byteBuffer);
			player.setPlayerStats(byteBuffer, string);
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					if (udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(player)) {
						udpConnection2.allChatMuted = player.isAllChatMuted();
						udpConnection2.accessLevel = PlayerType.fromString(player.accessLevel);
					}

					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.ChangePlayerStats.doPacket(byteBufferWriter);
					player.createPlayerStats(byteBufferWriter, string);
					PacketTypes.PacketType.ChangePlayerStats.send(udpConnection2);
				}
			}
		}
	}

	public static void doMinimumInit() throws IOException {
		Rand.init();
		DebugFileWatcher.instance.init();
		ArrayList arrayList = new ArrayList(ServerMods);
		ZomboidFileSystem.instance.loadMods(arrayList);
		LuaManager.init();
		PerkFactory.init();
		CustomPerks.instance.init();
		CustomPerks.instance.initLua();
		AssetManagers assetManagers = GameWindow.assetManagers;
		AiSceneAssetManager.instance.create(AiSceneAsset.ASSET_TYPE, assetManagers);
		AnimationAssetManager.instance.create(AnimationAsset.ASSET_TYPE, assetManagers);
		AnimNodeAssetManager.instance.create(AnimationAsset.ASSET_TYPE, assetManagers);
		ClothingItemAssetManager.instance.create(ClothingItem.ASSET_TYPE, assetManagers);
		MeshAssetManager.instance.create(ModelMesh.ASSET_TYPE, assetManagers);
		ModelAssetManager.instance.create(Model.ASSET_TYPE, assetManagers);
		TextureIDAssetManager.instance.create(TextureID.ASSET_TYPE, assetManagers);
		TextureAssetManager.instance.create(Texture.ASSET_TYPE, assetManagers);
		if (GUICommandline && !bSoftReset) {
			ServerGUI.init();
		}

		CustomSandboxOptions.instance.init();
		CustomSandboxOptions.instance.initInstance(SandboxOptions.instance);
		ScriptManager.instance.Load();
		ClothingDecals.init();
		BeardStyles.init();
		HairStyles.init();
		OutfitManager.init();
		if (!bSoftReset) {
			JAssImpImporter.Init();
			ModelManager.NoOpenGL = !ServerGUI.isCreated();
			ModelManager.instance.create();
			System.out.println("LOADING ASSETS: START");
			while (GameWindow.fileSystem.hasWork()) {
				GameWindow.fileSystem.updateAsyncTransactions();
			}

			System.out.println("LOADING ASSETS: FINISH");
		}

		try {
			LuaManager.initChecksum();
			LuaManager.LoadDirBase("shared");
			LuaManager.LoadDirBase("client", true);
			LuaManager.LoadDirBase("server");
			LuaManager.finishChecksum();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		RecipeManager.LoadedAfterLua();
		String string = ZomboidFileSystem.instance.getCacheDir();
		File file = new File(string + File.separator + "Server" + File.separator + ServerName + "_SandboxVars.lua");
		if (file.exists()) {
			if (!SandboxOptions.instance.loadServerLuaFile(ServerName)) {
				System.out.println("Exiting due to errors loading " + file.getCanonicalPath());
				System.exit(1);
			}

			SandboxOptions.instance.handleOldServerZombiesFile();
			SandboxOptions.instance.saveServerLuaFile(ServerName);
			SandboxOptions.instance.toLua();
		} else {
			SandboxOptions.instance.handleOldServerZombiesFile();
			SandboxOptions.instance.saveServerLuaFile(ServerName);
			SandboxOptions.instance.toLua();
		}

		LuaEventManager.triggerEvent("OnGameBoot");
		ZomboidGlobals.Load();
		SpawnPoints.instance.initServer1();
		ServerGUI.init2();
	}

	public static void startServer() throws ConnectException {
		String string = ServerOptions.instance.Password.getValue();
		if (CoopSlave.instance != null && SteamUtils.isSteamModeEnabled()) {
			string = "";
		}

		udpEngine = new UdpEngine(DEFAULT_PORT, UDPPort, ServerOptions.getInstance().getMaxPlayers(), string, true);
		DebugLog.log(DebugType.Network, "*** SERVER STARTED ****");
		DebugLog.log(DebugType.Network, "*** Steam is " + (SteamUtils.isSteamModeEnabled() ? "enabled" : "not enabled"));
		if (SteamUtils.isSteamModeEnabled()) {
			DebugLog.log(DebugType.Network, "Server is listening on port " + DEFAULT_PORT + " (for Steam connection) and port " + UDPPort + " (for UDPRakNet connection)");
			DebugLog.log(DebugType.Network, "Clients should use " + DEFAULT_PORT + " port for connections");
		} else {
			DebugLog.log(DebugType.Network, "server is listening on port " + DEFAULT_PORT);
		}

		ResetID = ServerOptions.instance.ResetID.getValue();
		String string2;
		if (CoopSlave.instance != null) {
			if (SteamUtils.isSteamModeEnabled()) {
				RakNetPeerInterface rakNetPeerInterface = udpEngine.getPeer();
				CoopSlave coopSlave = CoopSlave.instance;
				String string3 = rakNetPeerInterface.GetServerIP();
				coopSlave.sendMessage("server-address", (String)null, string3 + ":" + DEFAULT_PORT);
				long long1 = SteamGameServer.GetSteamID();
				CoopSlave.instance.sendMessage("steam-id", (String)null, SteamUtils.convertSteamIDToString(long1));
			} else {
				string2 = "127.0.0.1";
				CoopSlave.instance.sendMessage("server-address", (String)null, string2 + ":" + DEFAULT_PORT);
			}
		}

		LuaEventManager.triggerEvent("OnServerStarted");
		if (SteamUtils.isSteamModeEnabled()) {
			CoopSlave.status("UI_ServerStatus_Started");
		} else {
			CoopSlave.status("UI_ServerStatus_Started");
		}

		string2 = ServerOptions.instance.DiscordChannel.getValue();
		String string4 = ServerOptions.instance.DiscordToken.getValue();
		boolean boolean1 = ServerOptions.instance.DiscordEnable.getValue();
		String string5 = ServerOptions.instance.DiscordChannelID.getValue();
		discordBot.connect(boolean1, string4, string2, string5);
	}

	private static void mainLoopDealWithNetData(ZomboidNetData zomboidNetData) {
		if (SystemDisabler.getDoMainLoopDealWithNetData()) {
			ByteBuffer byteBuffer = zomboidNetData.buffer;
			UdpConnection udpConnection = udpEngine.getActiveConnection(zomboidNetData.connection);
			if (zomboidNetData.type == null) {
				ZomboidNetDataPool.instance.discard(zomboidNetData);
			} else {
				++zomboidNetData.type.serverPacketCount;
				MPStatistic.getInstance().addIncomePacket(zomboidNetData.type, byteBuffer.limit());
				try {
					if (udpConnection == null) {
						DebugLog.log(DebugType.Network, "Received packet type=" + zomboidNetData.type.name() + " connection is null.");
						return;
					}

					if (udpConnection.username == null) {
						switch (zomboidNetData.type) {
						case Login: 
						
						case Ping: 
						
						case ScoreboardUpdate: 
							break;
						
						default: 
							String string = zomboidNetData.type.name();
							DebugLog.log("Received packet type=" + string + " before Login, disconnecting " + udpConnection.getInetSocketAddress().getHostString());
							udpConnection.forceDisconnect("unacceptable-packet");
							ZomboidNetDataPool.instance.discard(zomboidNetData);
							return;
						
						}
					}

					zomboidNetData.type.onServerPacket(byteBuffer, udpConnection);
				} catch (Exception exception) {
					if (udpConnection == null) {
						DebugLog.log(DebugType.Network, "Error with packet of type: " + zomboidNetData.type + " connection is null.");
					} else {
						DebugLog.General.error("Error with packet of type: " + zomboidNetData.type + " for " + udpConnection.username);
					}

					exception.printStackTrace();
				}

				ZomboidNetDataPool.instance.discard(zomboidNetData);
			}
		}
	}

	static void receiveInvMngRemoveItem(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(player)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.InvMngRemoveItem.doPacket(byteBufferWriter);
					byteBufferWriter.putInt(int1);
					PacketTypes.PacketType.InvMngRemoveItem.send(udpConnection2);
					break;
				}
			}
		}
	}

	static void receiveInvMngGetItem(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) throws IOException {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(player)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.InvMngGetItem.doPacket(byteBufferWriter);
					byteBuffer.rewind();
					byteBufferWriter.bb.put(byteBuffer);
					PacketTypes.PacketType.InvMngGetItem.send(udpConnection2);
					break;
				}
			}
		}
	}

	static void receiveInvMngReqItem(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = 0;
		String string = null;
		if (byteBuffer.get() == 1) {
			string = GameWindow.ReadString(byteBuffer);
		} else {
			int1 = byteBuffer.getInt();
		}

		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short3);
		if (player != null) {
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(player)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.InvMngReqItem.doPacket(byteBufferWriter);
					if (string != null) {
						byteBufferWriter.putByte((byte)1);
						byteBufferWriter.putUTF(string);
					} else {
						byteBufferWriter.putByte((byte)0);
						byteBufferWriter.putInt(int1);
					}

					byteBufferWriter.putShort(short2);
					PacketTypes.PacketType.InvMngReqItem.send(udpConnection2);
					break;
				}
			}
		}
	}

	static void receiveRequestZipList(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) throws Exception {
		if (!udpConnection.wasInLoadingQueue) {
			kick(udpConnection, "UI_Policy_Kick", "The server received an invalid request");
		}

		if (udpConnection.playerDownloadServer != null) {
			udpConnection.playerDownloadServer.receiveRequestArray(byteBuffer);
		}
	}

	static void receiveRequestLargeAreaZip(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		if (!udpConnection.wasInLoadingQueue) {
			kick(udpConnection, "UI_Policy_Kick", "The server received an invalid request");
		}

		if (udpConnection.playerDownloadServer != null) {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			udpConnection.connectArea[0] = new Vector3((float)int1, (float)int2, (float)int3);
			udpConnection.ChunkGridWidth = int3;
			ZombiePopulationManager.instance.updateLoadedAreas();
		}
	}

	static void receiveNotRequiredInZip(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		if (udpConnection.playerDownloadServer != null) {
			udpConnection.playerDownloadServer.receiveCancelRequest(byteBuffer);
		}
	}

	static void receiveLogin(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ConnectionManager.log("receive-packet", "login", udpConnection);
		String string = GameWindow.ReadString(byteBuffer).trim();
		String string2 = GameWindow.ReadString(byteBuffer).trim();
		String string3 = GameWindow.ReadString(byteBuffer).trim();
		ByteBufferWriter byteBufferWriter;
		if (!string3.equals(Core.getInstance().getVersion())) {
			byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter);
			LoggerManager.getLogger("user").write("access denied: user \"" + string + "\" client version (" + string3 + ") does not match server version (" + Core.getInstance().getVersion() + ")");
			byteBufferWriter.putUTF("ClientVersionMismatch##" + string3 + "##" + Core.getInstance().getVersion());
			PacketTypes.PacketType.AccessDenied.send(udpConnection);
			ConnectionManager.log("access-denied", "version-mismatch", udpConnection);
			udpConnection.forceDisconnect("access-denied-client-version");
		}

		udpConnection.wasInLoadingQueue = false;
		udpConnection.ip = udpConnection.getInetSocketAddress().getHostString();
		udpConnection.validator.reset();
		udpConnection.idStr = udpConnection.ip;
		if (SteamUtils.isSteamModeEnabled()) {
			udpConnection.steamID = udpEngine.getClientSteamID(udpConnection.getConnectedGUID());
			if (udpConnection.steamID == -1L) {
				byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter);
				LoggerManager.getLogger("user").write("access denied: The client \"" + string + "\" did not complete the connection and authorization procedure in zombienet");
				byteBufferWriter.putUTF("ClientIsNofFullyConnectedInZombienet");
				PacketTypes.PacketType.AccessDenied.send(udpConnection);
				ConnectionManager.log("access-denied", "znet-error", udpConnection);
				udpConnection.forceDisconnect("access-denied-zombienet-connect");
			}

			udpConnection.ownerID = udpEngine.getClientOwnerSteamID(udpConnection.getConnectedGUID());
			udpConnection.idStr = SteamUtils.convertSteamIDToString(udpConnection.steamID);
			if (udpConnection.steamID != udpConnection.ownerID) {
				String string4 = udpConnection.idStr;
				udpConnection.idStr = string4 + "(owner=" + SteamUtils.convertSteamIDToString(udpConnection.ownerID) + ")";
			}
		}

		udpConnection.password = string2;
		LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + string + "\" attempting to join");
		ServerWorldDatabase.LogonResult logonResult;
		ByteBufferWriter byteBufferWriter2;
		if (CoopSlave.instance != null && SteamUtils.isSteamModeEnabled()) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2 != udpConnection && udpConnection2.steamID == udpConnection.steamID) {
					LoggerManager.getLogger("user").write("access denied: user \"" + string + "\" already connected");
					byteBufferWriter2 = udpConnection.startPacket();
					PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter2);
					byteBufferWriter2.putUTF("AlreadyConnected");
					PacketTypes.PacketType.AccessDenied.send(udpConnection);
					ConnectionManager.log("access-denied", "already-connected-steamid", udpConnection);
					udpConnection.forceDisconnect("access-denied-already-connected-cs");
					return;
				}
			}

			udpConnection.username = string;
			udpConnection.usernames[0] = string;
			udpConnection.isCoopHost = udpEngine.connections.size() == 1;
			DebugLog.Multiplayer.debugln(udpConnection.idStr + " isCoopHost=" + udpConnection.isCoopHost);
			udpConnection.accessLevel = 1;
			if (!ServerOptions.instance.DoLuaChecksum.getValue()) {
				udpConnection.checksumState = UdpConnection.ChecksumState.Done;
			}

			if (getPlayerCount() >= ServerOptions.getInstance().getMaxPlayers()) {
				byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF("ServerFull");
				PacketTypes.PacketType.AccessDenied.send(udpConnection);
				ConnectionManager.log("access-denied", "server-full", udpConnection);
				udpConnection.forceDisconnect("access-denied-server-full-cs");
			} else {
				if (isServerDropPackets() && ServerOptions.instance.DenyLoginOnOverloadedServer.getValue()) {
					byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter);
					LoggerManager.getLogger("user").write("access denied: user \"" + string + "\" Server is too busy");
					byteBufferWriter.putUTF("Server is too busy.");
					PacketTypes.PacketType.AccessDenied.send(udpConnection);
					ConnectionManager.log("access-denied", "server-busy", udpConnection);
					udpConnection.forceDisconnect("access-denied-server-busy-cs");
					++countOfDroppedConnections;
				}

				LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + string + "\" allowed to join");
				ServerWorldDatabase serverWorldDatabase = ServerWorldDatabase.instance;
				Objects.requireNonNull(serverWorldDatabase);
				logonResult = serverWorldDatabase.new LogonResult();
				logonResult.accessLevel = PlayerType.toString(udpConnection.accessLevel);
				receiveClientConnect(udpConnection, logonResult);
			}
		} else {
			logonResult = ServerWorldDatabase.instance.authClient(string, string2, udpConnection.ip, udpConnection.steamID);
			ByteBufferWriter byteBufferWriter3;
			if (logonResult.bAuthorized) {
				int int2;
				for (int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
					UdpConnection udpConnection3 = (UdpConnection)udpEngine.connections.get(int2);
					for (int int3 = 0; int3 < 4; ++int3) {
						if (string.equals(udpConnection3.usernames[int3])) {
							LoggerManager.getLogger("user").write("access denied: user \"" + string + "\" already connected");
							ByteBufferWriter byteBufferWriter4 = udpConnection.startPacket();
							PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter4);
							byteBufferWriter4.putUTF("AlreadyConnected");
							PacketTypes.PacketType.AccessDenied.send(udpConnection);
							ConnectionManager.log("access-denied", "already-connected-username", udpConnection);
							udpConnection.forceDisconnect("access-denied-already-connected-username");
							return;
						}
					}
				}

				udpConnection.username = string;
				udpConnection.usernames[0] = string;
				transactionIDMap.put(string, logonResult.transactionID);
				if (CoopSlave.instance != null) {
					udpConnection.isCoopHost = udpEngine.connections.size() == 1;
					DebugLog.log(udpConnection.idStr + " isCoopHost=" + udpConnection.isCoopHost);
				}

				udpConnection.accessLevel = PlayerType.fromString(logonResult.accessLevel);
				udpConnection.preferredInQueue = logonResult.priority;
				if (!ServerOptions.instance.DoLuaChecksum.getValue() || logonResult.accessLevel.equals("admin")) {
					udpConnection.checksumState = UdpConnection.ChecksumState.Done;
				}

				if (!logonResult.accessLevel.equals("") && getPlayerCount() >= ServerOptions.getInstance().getMaxPlayers()) {
					byteBufferWriter3 = udpConnection.startPacket();
					PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter3);
					byteBufferWriter3.putUTF("ServerFull");
					PacketTypes.PacketType.AccessDenied.send(udpConnection);
					ConnectionManager.log("access-denied", "server-full-no-admin", udpConnection);
					udpConnection.forceDisconnect("access-denied-server-full");
					return;
				}

				if (!ServerWorldDatabase.instance.containsUser(string) && ServerWorldDatabase.instance.containsCaseinsensitiveUser(string)) {
					byteBufferWriter3 = udpConnection.startPacket();
					PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter3);
					byteBufferWriter3.putUTF("InvalidUsername");
					PacketTypes.PacketType.AccessDenied.send(udpConnection);
					ConnectionManager.log("access-denied", "invalid-username", udpConnection);
					udpConnection.forceDisconnect("access-denied-invalid-username");
					return;
				}

				int2 = udpConnection.getAveragePing();
				DebugLog.Multiplayer.debugln("User %s ping %d ms", udpConnection.username, int2);
				if (MPStatistics.doKickWhileLoading(udpConnection, (long)int2)) {
					byteBufferWriter2 = udpConnection.startPacket();
					PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter2);
					LoggerManager.getLogger("user").write("access denied: user \"" + string + "\" ping is too high");
					byteBufferWriter2.putUTF("Ping");
					PacketTypes.PacketType.AccessDenied.send(udpConnection);
					ConnectionManager.log("access-denied", "ping-limit", udpConnection);
					udpConnection.forceDisconnect("access-denied-ping-limit");
					return;
				}

				if (logonResult.newUser) {
					try {
						ServerWorldDatabase.instance.addUser(string, string2);
						LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + string + "\" was added");
					} catch (SQLException sQLException) {
						DebugLog.Multiplayer.printException(sQLException, "ServerWorldDatabase.addUser error", LogSeverity.Error);
					}
				}

				LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + string + "\" allowed to join");
				try {
					if (ServerOptions.instance.AutoCreateUserInWhiteList.getValue() && !ServerWorldDatabase.instance.containsUser(string)) {
						ServerWorldDatabase.instance.addUser(string, string2);
					} else {
						ServerWorldDatabase.instance.setPassword(string, string2);
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				ServerWorldDatabase.instance.updateLastConnectionDate(string, string2);
				if (SteamUtils.isSteamModeEnabled()) {
					String string5 = SteamUtils.convertSteamIDToString(udpConnection.steamID);
					ServerWorldDatabase.instance.setUserSteamID(string, string5);
				}

				receiveClientConnect(udpConnection, logonResult);
			} else {
				byteBufferWriter3 = udpConnection.startPacket();
				PacketTypes.PacketType.AccessDenied.doPacket(byteBufferWriter3);
				if (logonResult.banned) {
					LoggerManager.getLogger("user").write("access denied: user \"" + string + "\" is banned");
					if (logonResult.bannedReason != null && !logonResult.bannedReason.isEmpty()) {
						byteBufferWriter3.putUTF("BannedReason##" + logonResult.bannedReason);
					} else {
						byteBufferWriter3.putUTF("Banned");
					}
				} else if (!logonResult.bAuthorized) {
					LoggerManager.getLogger("user").write("access denied: user \"" + string + "\" reason \"" + logonResult.dcReason + "\"");
					byteBufferWriter3.putUTF(logonResult.dcReason != null ? logonResult.dcReason : "AccessDenied");
				}

				PacketTypes.PacketType.AccessDenied.send(udpConnection);
				ConnectionManager.log("access-denied", "unauthorized", udpConnection);
				udpConnection.forceDisconnect("access-denied-unauthorized");
			}
		}
	}

	static void receiveSendInventory(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		Long Long1 = (Long)IDToAddressMap.get(short2);
		if (Long1 != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() == Long1) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.SendInventory.doPacket(byteBufferWriter);
					byteBufferWriter.bb.put(byteBuffer);
					PacketTypes.PacketType.SendInventory.send(udpConnection2);
					break;
				}
			}
		}
	}

	static void receivePlayerStartPMChat(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ChatServer.getInstance().processPlayerStartWhisperChatPacket(byteBuffer);
	}

	static void receiveRequestInventory(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		Long Long1 = (Long)IDToAddressMap.get(short3);
		if (Long1 != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() == Long1) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.RequestInventory.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(short2);
					PacketTypes.PacketType.RequestInventory.send(udpConnection2);
					break;
				}
			}
		}
	}

	static void receiveStatistic(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			udpConnection.statistic.parse(byteBuffer);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static void receiveStatisticRequest(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		if (udpConnection.accessLevel != 32 && !Core.bDebug) {
			DebugLog.General.error("User " + udpConnection.username + " has no rights to access statistics.");
		} else {
			try {
				udpConnection.statistic.enable = byteBuffer.get();
				sendStatistic(udpConnection);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	static void receiveZombieSimulation(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		NetworkZombiePacker.getInstance().receivePacket(byteBuffer, udpConnection);
	}

	public static void sendShortStatistic() {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection.statistic.enable == 3) {
				sendShortStatistic(udpConnection);
			}
		}
	}

	public static void sendShortStatistic(UdpConnection udpConnection) {
		try {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.StatisticRequest.doPacket(byteBufferWriter);
			MPStatistic.getInstance().write(byteBufferWriter);
			PacketTypes.PacketType.StatisticRequest.send(udpConnection);
		} catch (Exception exception) {
			exception.printStackTrace();
			udpConnection.cancelPacket();
		}
	}

	public static void sendStatistic() {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection.statistic.enable == 1) {
				sendStatistic(udpConnection);
			}
		}
	}

	public static void sendStatistic(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.StatisticRequest.doPacket(byteBufferWriter);
		try {
			MPStatistic.getInstance().getStatisticTable(byteBufferWriter.bb);
			PacketTypes.PacketType.StatisticRequest.send(udpConnection);
		} catch (IOException ioException) {
			ioException.printStackTrace();
			udpConnection.cancelPacket();
		}
	}

	public static void getStatisticFromClients() {
		try {
			Iterator iterator = udpEngine.connections.iterator();
			while (iterator.hasNext()) {
				UdpConnection udpConnection = (UdpConnection)iterator.next();
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.Statistic.doPacket(byteBufferWriter);
				byteBufferWriter.putLong(System.currentTimeMillis());
				PacketTypes.PacketType.Statistic.send(udpConnection);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void updateZombieControl(IsoZombie zombie, short short1, int int1) {
		try {
			if (zombie.authOwner == null) {
				return;
			}

			ByteBufferWriter byteBufferWriter = zombie.authOwner.startPacket();
			PacketTypes.PacketType.ZombieControl.doPacket(byteBufferWriter);
			byteBufferWriter.putShort(zombie.OnlineID);
			byteBufferWriter.putShort(short1);
			byteBufferWriter.putInt(int1);
			PacketTypes.PacketType.ZombieControl.send(zombie.authOwner);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static void receivePlayerUpdate(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		if (udpConnection.checksumState != UdpConnection.ChecksumState.Done) {
			kick(udpConnection, "UI_Policy_Kick", (String)null);
			udpConnection.forceDisconnect("kick-checksum");
		} else {
			PlayerPacket playerPacket = PlayerPacket.l_receive.playerPacket;
			playerPacket.parse(byteBuffer, udpConnection);
			IsoPlayer player = getPlayerFromConnection(udpConnection, playerPacket.id);
			try {
				if (player == null) {
					DebugLog.General.error("receivePlayerUpdate: Server received position for unknown player (id:" + playerPacket.id + "). Server will ignore this data.");
				} else {
					if (udpConnection.accessLevel == 1 && player.networkAI.doCheckAccessLevel() && (playerPacket.booleanVariables & (!SystemDisabler.getAllowDebugConnections() && !SystemDisabler.getOverrideServerConnectDebugCheck() ? '' : '쀀')) != 0 && ServerOptions.instance.AntiCheatProtectionType12.getValue() && PacketValidator.checkUser(udpConnection)) {
						PacketValidator.doKickUser(udpConnection, playerPacket.getClass().getSimpleName(), "Type12", (String)null);
					}

					if (!player.networkAI.checkPosition(udpConnection, player, (float)PZMath.fastfloor(playerPacket.realx), (float)PZMath.fastfloor(playerPacket.realy))) {
						return;
					}

					if (!player.networkAI.isSetVehicleHit()) {
						player.networkAI.parse(playerPacket);
					}

					player.bleedingLevel = playerPacket.bleedingLevel;
					if (player.networkAI.distance.getLength() > (float)IsoChunkMap.ChunkWidthInTiles) {
						MPStatistic.getInstance().teleport();
					}

					udpConnection.ReleventPos[player.PlayerIndex].x = playerPacket.realx;
					udpConnection.ReleventPos[player.PlayerIndex].y = playerPacket.realy;
					udpConnection.ReleventPos[player.PlayerIndex].z = (float)playerPacket.realz;
					playerPacket.id = player.getOnlineID();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			if (ServerOptions.instance.KickFastPlayers.getValue()) {
				Vector2 vector2 = (Vector2)playerToCoordsMap.get(Integer.valueOf(playerPacket.id));
				if (vector2 == null) {
					vector2 = new Vector2();
					vector2.x = playerPacket.x;
					vector2.y = playerPacket.y;
					playerToCoordsMap.put(playerPacket.id, vector2);
				} else {
					if (!player.accessLevel.equals("") && !player.isGhostMode() && (Math.abs(playerPacket.x - vector2.x) > 4.0F || Math.abs(playerPacket.y - vector2.y) > 4.0F)) {
						if (playerMovedToFastMap.get(playerPacket.id) == null) {
							playerMovedToFastMap.put(playerPacket.id, 1);
						} else {
							playerMovedToFastMap.put(playerPacket.id, (Integer)playerMovedToFastMap.get(Integer.valueOf(playerPacket.id)) + 1);
						}

						ZLogger zLogger = LoggerManager.getLogger("admin");
						String string = player.getDisplayName();
						zLogger.write(string + " go too fast (" + playerMovedToFastMap.get(Integer.valueOf(playerPacket.id)) + " times)");
						if ((Integer)playerMovedToFastMap.get(playerPacket.id) == 10) {
							LoggerManager.getLogger("admin").write(player.getDisplayName() + " kicked for going too fast");
							kick(udpConnection, "UI_Policy_Kick", (String)null);
							udpConnection.forceDisconnect("kick-fast-player");
							return;
						}
					}

					vector2.x = playerPacket.x;
					vector2.y = playerPacket.y;
				}
			}

			if (player != null) {
				for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
					if (udpConnection.getConnectedGUID() != udpConnection2.getConnectedGUID() && udpConnection2.isFullyConnected() && (player.checkCanSeeClient(udpConnection2) && udpConnection2.RelevantTo(playerPacket.x, playerPacket.y) || short1 == PacketTypes.PacketType.PlayerUpdateReliable.getId() && (udpConnection2.accessLevel > udpConnection.accessLevel || udpConnection.accessLevel == 32))) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						((PacketTypes.PacketType)PacketTypes.packetTypes.get(short1)).doPacket(byteBufferWriter);
						byteBuffer.position(0);
						byteBuffer.position(2);
						byteBufferWriter.bb.putShort(player.getOnlineID());
						byteBufferWriter.bb.put(byteBuffer);
						((PacketTypes.PacketType)PacketTypes.packetTypes.get(short1)).send(udpConnection2);
					}
				}
			}
		}
	}

	static void receivePacketCounts(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.PacketCounts.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(PacketTypes.packetTypes.size());
		Iterator iterator = PacketTypes.packetTypes.values().iterator();
		while (iterator.hasNext()) {
			PacketTypes.PacketType packetType = (PacketTypes.PacketType)iterator.next();
			byteBufferWriter.putShort(packetType.getId());
			byteBufferWriter.putLong(packetType.serverPacketCount);
		}

		PacketTypes.PacketType.PacketCounts.send(udpConnection);
	}

	static void receiveSandboxOptions(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			SandboxOptions.instance.load(byteBuffer);
			SandboxOptions.instance.applySettings();
			SandboxOptions.instance.toLua();
			SandboxOptions.instance.saveServerLuaFile(ServerName);
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.SandboxOptions.doPacket(byteBufferWriter);
				byteBuffer.rewind();
				byteBufferWriter.bb.put(byteBuffer);
				PacketTypes.PacketType.SandboxOptions.send(udpConnection2);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static void receiveChunkObjectState(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		IsoChunk chunk = ServerMap.instance.getChunk(short2, short3);
		if (chunk == null) {
			udpConnection.chunkObjectState.add(short2);
			udpConnection.chunkObjectState.add(short3);
		} else {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.ChunkObjectState.doPacket(byteBufferWriter);
			byteBufferWriter.putShort(short2);
			byteBufferWriter.putShort(short3);
			try {
				if (chunk.saveObjectState(byteBufferWriter.bb)) {
					PacketTypes.PacketType.ChunkObjectState.send(udpConnection);
				} else {
					udpConnection.cancelPacket();
				}
			} catch (Throwable throwable) {
				throwable.printStackTrace();
				udpConnection.cancelPacket();
				return;
			}
		}
	}

	static void receiveReadAnnotedMap(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		StashSystem.prepareBuildingStash(string);
	}

	static void receiveTradingUIRemoveItem(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		Long Long1 = (Long)IDToAddressMap.get(short3);
		if (Long1 != null) {
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() == Long1) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.TradingUIRemoveItem.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(short2);
					byteBufferWriter.putInt(int1);
					PacketTypes.PacketType.TradingUIRemoveItem.send(udpConnection2);
					break;
				}
			}
		}
	}

	static void receiveTradingUIUpdateState(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		Long Long1 = (Long)IDToAddressMap.get(short3);
		if (Long1 != null) {
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() == Long1) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.TradingUIUpdateState.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(short2);
					byteBufferWriter.putInt(int1);
					PacketTypes.PacketType.TradingUIUpdateState.send(udpConnection2);
					break;
				}
			}
		}
	}

	static void receiveTradingUIAddItem(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		InventoryItem inventoryItem = null;
		try {
			inventoryItem = InventoryItem.loadItem(byteBuffer, 195);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (inventoryItem != null) {
			Long Long1 = (Long)IDToAddressMap.get(short3);
			if (Long1 != null) {
				for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
					if (udpConnection2.getConnectedGUID() == Long1) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.TradingUIAddItem.doPacket(byteBufferWriter);
						byteBufferWriter.putShort(short2);
						try {
							inventoryItem.saveWithSize(byteBufferWriter.bb, false);
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}

						PacketTypes.PacketType.TradingUIAddItem.send(udpConnection2);
						break;
					}
				}
			}
		}
	}

	static void receiveRequestTrading(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		byte byte1 = byteBuffer.get();
		Long Long1 = (Long)IDToAddressMap.get(short2);
		if (byte1 == 0) {
			Long1 = (Long)IDToAddressMap.get(short3);
		}

		if (Long1 != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() == Long1) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.RequestTrading.doPacket(byteBufferWriter);
					if (byte1 == 0) {
						byteBufferWriter.putShort(short2);
					} else {
						byteBufferWriter.putShort(short3);
					}

					byteBufferWriter.putByte(byte1);
					PacketTypes.PacketType.RequestTrading.send(udpConnection2);
					break;
				}
			}
		}
	}

	static void receiveSyncFaction(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		Faction faction = Faction.getFaction(string);
		boolean boolean1 = false;
		if (faction == null) {
			faction = new Faction(string, string2);
			boolean1 = true;
			Faction.getFactions().add(faction);
		}

		faction.getPlayers().clear();
		if (byteBuffer.get() == 1) {
			faction.setTag(GameWindow.ReadString(byteBuffer));
			faction.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
		}

		for (int int2 = 0; int2 < int1; ++int2) {
			String string3 = GameWindow.ReadString(byteBuffer);
			faction.getPlayers().add(string3);
		}

		if (!faction.getOwner().equals(string2)) {
			faction.setOwner(string2);
		}

		boolean boolean2 = byteBuffer.get() == 1;
		if (ChatServer.isInited()) {
			if (boolean1) {
				ChatServer.getInstance().createFactionChat(string);
			}

			if (boolean2) {
				ChatServer.getInstance().removeFactionChat(string);
			} else {
				ChatServer.getInstance().syncFactionChatMembers(string, string2, faction.getPlayers());
			}
		}

		if (boolean2) {
			Faction.getFactions().remove(faction);
			DebugLog.log("faction: removed " + string + " owner=" + faction.getOwner());
		}

		for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
			if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.SyncFaction.doPacket(byteBufferWriter);
				faction.writeToBuffer(byteBufferWriter, boolean2);
				PacketTypes.PacketType.SyncFaction.send(udpConnection2);
			}
		}
	}

	static void receiveSyncNonPvpZone(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			SyncNonPvpZonePacket syncNonPvpZonePacket = new SyncNonPvpZonePacket();
			syncNonPvpZonePacket.parse(byteBuffer, udpConnection);
			if (syncNonPvpZonePacket.isConsistent()) {
				sendNonPvpZone(syncNonPvpZonePacket.zone, syncNonPvpZonePacket.doRemove, udpConnection);
				syncNonPvpZonePacket.process();
				DebugLog.Multiplayer.debugln("ReceiveSyncNonPvpZone: %s", syncNonPvpZonePacket.getDescription());
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveSyncNonPvpZone: failed", LogSeverity.Error);
		}
	}

	public static void sendNonPvpZone(NonPvpZone nonPvpZone, boolean boolean1, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.SyncNonPvpZone.doPacket(byteBufferWriter);
				nonPvpZone.save(byteBufferWriter.bb);
				byteBufferWriter.putBoolean(boolean1);
				PacketTypes.PacketType.SyncNonPvpZone.send(udpConnection2);
			}
		}
	}

	static void receiveChangeTextColor(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = getPlayerFromConnection(udpConnection, short2);
		if (player != null) {
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			player.setSpeakColourInfo(new ColorInfo(float1, float2, float3, 1.0F));
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.ChangeTextColor.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(player.getOnlineID());
					byteBufferWriter.putFloat(float1);
					byteBufferWriter.putFloat(float2);
					byteBufferWriter.putFloat(float3);
					PacketTypes.PacketType.ChangeTextColor.send(udpConnection2);
				}
			}
		}
	}

	@Deprecated
	static void receiveTransactionID(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short1);
		if (player != null) {
			transactionIDMap.put(player.username, int1);
			player.setTransactionID(int1);
			ServerWorldDatabase.instance.saveTransactionID(player.username, int1);
		}
	}

	static void receiveSyncCompost(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			IsoCompost compost = square.getCompost();
			if (compost == null) {
				compost = new IsoCompost(square.getCell(), square);
				square.AddSpecialObject(compost);
			}

			float float1 = byteBuffer.getFloat();
			compost.setCompost(float1);
			sendCompost(compost, udpConnection);
		}
	}

	public static void sendCompost(IsoCompost compost, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.RelevantTo((float)compost.square.x, (float)compost.square.y) && (udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() || udpConnection == null)) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.SyncCompost.doPacket(byteBufferWriter);
				byteBufferWriter.putInt(compost.square.x);
				byteBufferWriter.putInt(compost.square.y);
				byteBufferWriter.putInt(compost.square.z);
				byteBufferWriter.putFloat(compost.getCompost());
				PacketTypes.PacketType.SyncCompost.send(udpConnection2);
			}
		}
	}

	static void receiveCataplasm(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			int int1 = byteBuffer.getInt();
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			if (float1 > 0.0F) {
				player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setPlantainFactor(float1);
			}

			if (float2 > 0.0F) {
				player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setComfreyFactor(float2);
			}

			if (float3 > 0.0F) {
				player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setGarlicFactor(float3);
			}

			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.Cataplasm.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(short2);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putFloat(float1);
					byteBufferWriter.putFloat(float2);
					byteBufferWriter.putFloat(float3);
					PacketTypes.PacketType.Cataplasm.send(udpConnection2);
				}
			}
		}
	}

	static void receiveSledgehammerDestroy(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		if (ServerOptions.instance.AllowDestructionBySledgehammer.getValue()) {
			receiveRemoveItemFromSquare(byteBuffer, udpConnection, short1);
		}
	}

	public static void AddExplosiveTrap(HandWeapon handWeapon, IsoGridSquare square, boolean boolean1) {
		IsoTrap trap = new IsoTrap(handWeapon, square.getCell(), square);
		int int1 = 0;
		if (handWeapon.getExplosionRange() > 0) {
			int1 = handWeapon.getExplosionRange();
		}

		if (handWeapon.getFireRange() > 0) {
			int1 = handWeapon.getFireRange();
		}

		if (handWeapon.getSmokeRange() > 0) {
			int1 = handWeapon.getSmokeRange();
		}

		square.AddTileObject(trap);
		for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int2);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.AddExplosiveTrap.doPacket(byteBufferWriter);
			byteBufferWriter.putInt(square.x);
			byteBufferWriter.putInt(square.y);
			byteBufferWriter.putInt(square.z);
			try {
				handWeapon.saveWithSize(byteBufferWriter.bb, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			byteBufferWriter.putInt(int1);
			byteBufferWriter.putBoolean(boolean1);
			byteBufferWriter.putBoolean(false);
			PacketTypes.PacketType.AddExplosiveTrap.send(udpConnection);
		}
	}

	static void receiveAddExplosiveTrap(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			InventoryItem inventoryItem = null;
			try {
				inventoryItem = InventoryItem.loadItem(byteBuffer, 195);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			if (inventoryItem == null) {
				return;
			}

			HandWeapon handWeapon = (HandWeapon)inventoryItem;
			String string = udpConnection.username;
			DebugLog.log("trap: user \"" + string + "\" added " + inventoryItem.getFullType() + " at " + int1 + "," + int2 + "," + int3);
			ZLogger zLogger = LoggerManager.getLogger("map");
			String string2 = udpConnection.idStr;
			zLogger.write(string2 + " \"" + udpConnection.username + "\" added " + inventoryItem.getFullType() + " at " + int1 + "," + int2 + "," + int3);
			if (handWeapon.isInstantExplosion()) {
				IsoTrap trap = new IsoTrap(handWeapon, square.getCell(), square);
				square.AddTileObject(trap);
				trap.triggerExplosion(false);
			}

			for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.AddExplosiveTrap.doPacket(byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					try {
						handWeapon.saveWithSize(byteBufferWriter.bb, false);
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}

					PacketTypes.PacketType.AddExplosiveTrap.send(udpConnection2);
				}
			}
		}
	}

	public static void sendHelicopter(float float1, float float2, boolean boolean1) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.Helicopter.doPacket(byteBufferWriter);
			byteBufferWriter.putFloat(float1);
			byteBufferWriter.putFloat(float2);
			byteBufferWriter.putBoolean(boolean1);
			PacketTypes.PacketType.Helicopter.send(udpConnection);
		}
	}

	static void receiveRegisterZone(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		int int5 = byteBuffer.getInt();
		int int6 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		ArrayList arrayList = IsoWorld.instance.getMetaGrid().getZonesAt(int1, int2, int3);
		boolean boolean2 = false;
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)iterator.next();
			if (string2.equals(zone.getType())) {
				boolean2 = true;
				zone.setName(string);
				zone.setLastActionTimestamp(int6);
			}
		}

		if (!boolean2) {
			IsoWorld.instance.getMetaGrid().registerZone(string, string2, int1, int2, int3, int4, int5);
		}

		if (boolean1) {
			for (int int7 = 0; int7 < udpEngine.connections.size(); ++int7) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int7);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.RegisterZone.doPacket(byteBufferWriter);
					byteBufferWriter.putUTF(string);
					byteBufferWriter.putUTF(string2);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					byteBufferWriter.putInt(int4);
					byteBufferWriter.putInt(int5);
					byteBufferWriter.putInt(int6);
					PacketTypes.PacketType.RegisterZone.send(udpConnection2);
				}
			}
		}
	}

	public static void sendZone(IsoMetaGrid.Zone zone, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.RegisterZone.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF(zone.name);
				byteBufferWriter.putUTF(zone.type);
				byteBufferWriter.putInt(zone.x);
				byteBufferWriter.putInt(zone.y);
				byteBufferWriter.putInt(zone.z);
				byteBufferWriter.putInt(zone.w);
				byteBufferWriter.putInt(zone.h);
				byteBufferWriter.putInt(zone.lastActionTimestamp);
				PacketTypes.PacketType.RegisterZone.send(udpConnection2);
			}
		}
	}

	static void receiveConstructedZone(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoMetaGrid.Zone zone = IsoWorld.instance.MetaGrid.getZoneAt(int1, int2, int3);
		if (zone != null) {
			zone.setHaveConstruction(true);
		}
	}

	public static void addXp(IsoPlayer player, PerkFactory.Perk perk, int int1) {
		if (PlayerToAddressMap.containsKey(player)) {
			long long1 = (Long)PlayerToAddressMap.get(player);
			UdpConnection udpConnection = udpEngine.getActiveConnection(long1);
			if (udpConnection == null) {
				return;
			}

			AddXp addXp = new AddXp();
			addXp.set(player, perk, int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.AddXP.doPacket(byteBufferWriter);
			addXp.write(byteBufferWriter);
			PacketTypes.PacketType.AddXP.send(udpConnection);
		}
	}

	static void receiveWriteLog(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
	}

	static void receiveChecksum(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		NetChecksum.comparer.serverPacket(byteBuffer, udpConnection);
	}

	private static void answerPing(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		String string = GameWindow.ReadString(byteBuffer);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.Ping.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putInt(udpEngine.connections.size());
				byteBufferWriter.putInt(512);
				PacketTypes.PacketType.Ping.send(udpConnection2);
			}
		}
	}

	static void receiveUpdateItemSprite(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		String string = GameWindow.ReadStringUTF(byteBuffer);
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		int int5 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, int4);
		if (square != null && int5 < square.getObjects().size()) {
			try {
				IsoObject object = (IsoObject)square.getObjects().get(int5);
				if (object != null) {
					object.sprite = IsoSpriteManager.instance.getSprite(int1);
					if (object.sprite == null && !string.isEmpty()) {
						object.setSprite(string);
					}

					object.RemoveAttachedAnims();
					int int6 = byteBuffer.get() & 255;
					for (int int7 = 0; int7 < int6; ++int7) {
						int int8 = byteBuffer.getInt();
						IsoSprite sprite = IsoSpriteManager.instance.getSprite(int8);
						if (sprite != null) {
							object.AttachExistingAnim(sprite, 0, 0, false, 0, false, 0.0F);
						}
					}

					object.transmitUpdatedSpriteToClients(udpConnection);
				}
			} catch (Exception exception) {
			}
		}
	}

	static void receiveWorldMessage(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		if (!udpConnection.allChatMuted) {
			String string = GameWindow.ReadString(byteBuffer);
			String string2 = GameWindow.ReadString(byteBuffer);
			if (string2.length() > 256) {
				string2 = string2.substring(0, 256);
			}

			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.WorldMessage.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putUTF(string2);
				PacketTypes.PacketType.WorldMessage.send(udpConnection2);
			}

			discordBot.sendMessage(string, string2);
			LoggerManager.getLogger("chat").write(udpConnection.index + " \"" + udpConnection.username + "\" A \"" + string2 + "\"");
		}
	}

	static void receiveGetModData(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		LuaEventManager.triggerEvent("SendCustomModData");
	}

	static void receiveStopFire(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		short short2;
		if (byte1 == 1) {
			short2 = byteBuffer.getShort();
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
			if (player != null) {
				player.sendObjectChange("StopBurning");
			}
		} else if (byte1 == 2) {
			short2 = byteBuffer.getShort();
			IsoZombie zombie = (IsoZombie)ServerMap.instance.ZombieMap.get(short2);
			if (zombie != null) {
				zombie.StopBurning();
			}
		} else {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
			if (square != null) {
				square.stopFire();
				for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
					if (udpConnection2.RelevantTo((float)int1, (float)int2) && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.StopFire.doPacket(byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						PacketTypes.PacketType.StopFire.send(udpConnection2);
					}
				}
			}
		}
	}

	@Deprecated
	static void receiveStartFire(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		StartFire startFire = new StartFire();
		startFire.parse(byteBuffer, udpConnection);
		if (startFire.isConsistent() && startFire.validate(udpConnection)) {
			startFire.process();
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.StartFire.doPacket(byteBufferWriter);
					startFire.write(byteBufferWriter);
					PacketTypes.PacketType.StartFire.send(udpConnection2);
				}
			}
		}
	}

	public static void startFireOnClient(IsoGridSquare square, int int1, boolean boolean1, int int2, boolean boolean2) {
		StartFire startFire = new StartFire();
		startFire.set(square, boolean1, int1, int2, boolean2);
		startFire.process();
		for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int3);
			if (udpConnection.RelevantTo((float)square.getX(), (float)square.getY())) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.StartFire.doPacket(byteBufferWriter);
				startFire.write(byteBufferWriter);
				PacketTypes.PacketType.StartFire.send(udpConnection);
			}
		}
	}

	public static void sendOptionsToClients() {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.ReloadOptions.doPacket(byteBufferWriter);
			byteBufferWriter.putInt(ServerOptions.instance.getPublicOptions().size());
			String string = null;
			Iterator iterator = ServerOptions.instance.getPublicOptions().iterator();
			while (iterator.hasNext()) {
				string = (String)iterator.next();
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putUTF(ServerOptions.instance.getOption(string));
			}

			PacketTypes.PacketType.ReloadOptions.send(udpConnection);
		}
	}

	public static void sendBecomeCorpse(IsoDeadBody deadBody) {
		IsoGridSquare square = deadBody.getSquare();
		if (square != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection.RelevantTo((float)square.x, (float)square.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.BecomeCorpse.doPacket(byteBufferWriter);
					try {
						byteBufferWriter.putShort(deadBody.getObjectID());
						byteBufferWriter.putShort(deadBody.getOnlineID());
						byteBufferWriter.putFloat(deadBody.getReanimateTime());
						if (deadBody.isPlayer()) {
							byteBufferWriter.putByte((byte)2);
						} else if (deadBody.isZombie()) {
							byteBufferWriter.putByte((byte)1);
						} else {
							byteBufferWriter.putByte((byte)0);
						}

						byteBufferWriter.putInt(square.x);
						byteBufferWriter.putInt(square.y);
						byteBufferWriter.putInt(square.z);
						PacketTypes.PacketType.BecomeCorpse.send(udpConnection);
					} catch (Exception exception) {
						udpConnection.cancelPacket();
						DebugLog.Multiplayer.printException(exception, "SendBecomeCorpse failed", LogSeverity.Error);
					}
				}
			}
		}
	}

	public static void sendCorpse(IsoDeadBody deadBody) {
		IsoGridSquare square = deadBody.getSquare();
		if (square != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection.RelevantTo((float)square.x, (float)square.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.AddCorpseToMap.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(deadBody.getObjectID());
					byteBufferWriter.putShort(deadBody.getOnlineID());
					byteBufferWriter.putInt(square.x);
					byteBufferWriter.putInt(square.y);
					byteBufferWriter.putInt(square.z);
					deadBody.writeToRemoteBuffer(byteBufferWriter);
					PacketTypes.PacketType.AddCorpseToMap.send(udpConnection);
				}
			}
		}
	}

	static void receiveAddCorpseToMap(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoObject object = WorldItemTypes.createFromBuffer(byteBuffer);
		if (object != null && object instanceof IsoDeadBody) {
			object.loadFromRemoteBuffer(byteBuffer, false);
			((IsoDeadBody)object).setObjectID(short2);
			IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
			if (square != null) {
				square.addCorpse((IsoDeadBody)object, true);
				for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.RelevantTo((float)int1, (float)int2)) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.AddCorpseToMap.doPacket(byteBufferWriter);
						byteBuffer.rewind();
						byteBufferWriter.bb.put(byteBuffer);
						PacketTypes.PacketType.AddCorpseToMap.send(udpConnection2);
					}
				}
			}

			LoggerManager.getLogger("item").write(udpConnection.idStr + " \"" + udpConnection.username + "\" corpse +1 " + int1 + "," + int2 + "," + int3);
		}
	}

	static void receiveSmashWindow(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		IsoObject object = IsoWorld.instance.getItemFromXYZIndexBuffer(byteBuffer);
		if (object != null && object instanceof IsoWindow) {
			byte byte1 = byteBuffer.get();
			if (byte1 == 1) {
				((IsoWindow)object).smashWindow(true);
				smashWindow((IsoWindow)object, 1);
			} else if (byte1 == 2) {
				((IsoWindow)object).setGlassRemoved(true);
				smashWindow((IsoWindow)object, 2);
			}
		}
	}

	public static void sendPlayerConnect(IsoPlayer player, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.PlayerConnect.doPacket(byteBufferWriter);
		if (udpConnection.getConnectedGUID() != (Long)PlayerToAddressMap.get(player)) {
			byteBufferWriter.putShort(player.OnlineID);
		} else {
			byteBufferWriter.putShort((short)-1);
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			byteBufferWriter.putShort(player.OnlineID);
			try {
				GameTime.getInstance().saveToPacket(byteBufferWriter.bb);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		byteBufferWriter.putFloat(player.x);
		byteBufferWriter.putFloat(player.y);
		byteBufferWriter.putFloat(player.z);
		byteBufferWriter.putUTF(player.username);
		if (udpConnection.getConnectedGUID() != (Long)PlayerToAddressMap.get(player)) {
			try {
				player.getDescriptor().save(byteBufferWriter.bb);
				player.getHumanVisual().save(byteBufferWriter.bb);
				ItemVisuals itemVisuals = new ItemVisuals();
				player.getItemVisuals(itemVisuals);
				itemVisuals.save(byteBufferWriter.bb);
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}
		}

		if (SteamUtils.isSteamModeEnabled()) {
			byteBufferWriter.putLong(player.getSteamID());
		}

		byteBufferWriter.putByte((byte)(player.isGodMod() ? 1 : 0));
		byteBufferWriter.putByte((byte)(player.isGhostMode() ? 1 : 0));
		player.getSafety().save(byteBufferWriter.bb);
		byteBufferWriter.putByte(PlayerType.fromString(player.accessLevel));
		byteBufferWriter.putByte((byte)(player.isInvisible() ? 1 : 0));
		if (udpConnection.getConnectedGUID() != (Long)PlayerToAddressMap.get(player)) {
			try {
				player.getXp().save(byteBufferWriter.bb);
			} catch (IOException ioException3) {
				ioException3.printStackTrace();
			}
		}

		byteBufferWriter.putUTF(player.getTagPrefix());
		byteBufferWriter.putFloat(player.getTagColor().r);
		byteBufferWriter.putFloat(player.getTagColor().g);
		byteBufferWriter.putFloat(player.getTagColor().b);
		byteBufferWriter.putDouble(player.getHoursSurvived());
		byteBufferWriter.putInt(player.getZombieKills());
		byteBufferWriter.putUTF(player.getDisplayName());
		byteBufferWriter.putFloat(player.getSpeakColour().r);
		byteBufferWriter.putFloat(player.getSpeakColour().g);
		byteBufferWriter.putFloat(player.getSpeakColour().b);
		byteBufferWriter.putBoolean(player.showTag);
		byteBufferWriter.putBoolean(player.factionPvp);
		byteBufferWriter.putInt(player.getAttachedItems().size());
		for (int int1 = 0; int1 < player.getAttachedItems().size(); ++int1) {
			byteBufferWriter.putUTF(player.getAttachedItems().get(int1).getLocation());
			byteBufferWriter.putUTF(player.getAttachedItems().get(int1).getItem().getFullType());
		}

		byteBufferWriter.putInt(player.remoteSneakLvl);
		byteBufferWriter.putInt(player.remoteStrLvl);
		byteBufferWriter.putInt(player.remoteFitLvl);
		PacketTypes.PacketType.PlayerConnect.send(udpConnection);
		if (udpConnection.getConnectedGUID() != (Long)PlayerToAddressMap.get(player)) {
			updateHandEquips(udpConnection, player);
		}
	}

	@Deprecated
	static void receiveRequestPlayerData(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getShort());
		if (player != null) {
			sendPlayerConnect(player, udpConnection);
		}
	}

	static void receiveChatMessageFromPlayer(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ChatServer.getInstance().processMessageFromPlayerPacket(byteBuffer);
	}

	public static void loadModData(IsoGridSquare square) {
		if (square.getModData().rawget("id") != null && square.getModData().rawget("id") != null && (square.getModData().rawget("remove") == null || ((String)square.getModData().rawget("remove")).equals("false"))) {
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":x", new Double((double)square.getX()));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":y", new Double((double)square.getY()));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":z", new Double((double)square.getZ()));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":typeOfSeed", square.getModData().rawget("typeOfSeed"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":nbOfGrow", (Double)square.getModData().rawget("nbOfGrow"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":id", square.getModData().rawget("id"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":waterLvl", square.getModData().rawget("waterLvl"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":lastWaterHour", square.getModData().rawget("lastWaterHour"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":waterNeeded", square.getModData().rawget("waterNeeded"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":waterNeededMax", square.getModData().rawget("waterNeededMax"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":mildewLvl", square.getModData().rawget("mildewLvl"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":aphidLvl", square.getModData().rawget("aphidLvl"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":fliesLvl", square.getModData().rawget("fliesLvl"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":fertilizer", square.getModData().rawget("fertilizer"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":nextGrowing", square.getModData().rawget("nextGrowing"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":hasVegetable", square.getModData().rawget("hasVegetable"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":hasSeed", square.getModData().rawget("hasSeed"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":health", square.getModData().rawget("health"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":badCare", square.getModData().rawget("badCare"));
			GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":state", square.getModData().rawget("state"));
			if (square.getModData().rawget("hoursElapsed") != null) {
				GameTime.getInstance().getModData().rawset("hoursElapsed", square.getModData().rawget("hoursElapsed"));
			}
		}

		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection.RelevantTo((float)square.getX(), (float)square.getY())) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.ReceiveModData.doPacket(byteBufferWriter);
				byteBufferWriter.putInt(square.getX());
				byteBufferWriter.putInt(square.getY());
				byteBufferWriter.putInt(square.getZ());
				try {
					square.getModData().save(byteBufferWriter.bb);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

				PacketTypes.PacketType.ReceiveModData.send(udpConnection);
			}
		}
	}

	static void receiveSendModData(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
		if (square != null) {
			try {
				square.getModData().load((ByteBuffer)byteBuffer, 195);
				if (square.getModData().rawget("id") != null && (square.getModData().rawget("remove") == null || ((String)square.getModData().rawget("remove")).equals("false"))) {
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":x", new Double((double)square.getX()));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":y", new Double((double)square.getY()));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":z", new Double((double)square.getZ()));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":typeOfSeed", square.getModData().rawget("typeOfSeed"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":nbOfGrow", (Double)square.getModData().rawget("nbOfGrow"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":id", square.getModData().rawget("id"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":waterLvl", square.getModData().rawget("waterLvl"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":lastWaterHour", square.getModData().rawget("lastWaterHour"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":waterNeeded", square.getModData().rawget("waterNeeded"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":waterNeededMax", square.getModData().rawget("waterNeededMax"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":mildewLvl", square.getModData().rawget("mildewLvl"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":aphidLvl", square.getModData().rawget("aphidLvl"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":fliesLvl", square.getModData().rawget("fliesLvl"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":fertilizer", square.getModData().rawget("fertilizer"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":nextGrowing", square.getModData().rawget("nextGrowing"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":hasVegetable", square.getModData().rawget("hasVegetable"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":hasSeed", square.getModData().rawget("hasSeed"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":health", square.getModData().rawget("health"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":badCare", square.getModData().rawget("badCare"));
					GameTime.getInstance().getModData().rawset("planting:" + ((Double)square.getModData().rawget("id")).intValue() + ":state", square.getModData().rawget("state"));
					if (square.getModData().rawget("hoursElapsed") != null) {
						GameTime.getInstance().getModData().rawset("hoursElapsed", square.getModData().rawget("hoursElapsed"));
					}
				}

				LuaEventManager.triggerEvent("onLoadModDataFromServer", square);
				for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
					if (udpConnection2.RelevantTo((float)square.getX(), (float)square.getY()) && (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID())) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.ReceiveModData.doPacket(byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						try {
							square.getModData().save(byteBufferWriter.bb);
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}

						PacketTypes.PacketType.ReceiveModData.send(udpConnection2);
					}
				}
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}
		}
	}

	static void receiveWeaponHit(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		IsoObject object = getIsoObjectRefFromByteBuffer(byteBuffer);
		short short2 = byteBuffer.getShort();
		String string = GameWindow.ReadStringUTF(byteBuffer);
		IsoPlayer player = getPlayerFromConnection(udpConnection, short2);
		if (object != null && player != null) {
			InventoryItem inventoryItem = null;
			if (!string.isEmpty()) {
				inventoryItem = InventoryItemFactory.CreateItem(string);
				if (!(inventoryItem instanceof HandWeapon)) {
					return;
				}
			}

			if (inventoryItem == null && !(object instanceof IsoWindow)) {
				return;
			}

			int int1 = (int)object.getX();
			int int2 = (int)object.getY();
			int int3 = (int)object.getZ();
			if (object instanceof IsoDoor) {
				((IsoDoor)object).WeaponHit(player, (HandWeapon)inventoryItem);
			} else if (object instanceof IsoThumpable) {
				((IsoThumpable)object).WeaponHit(player, (HandWeapon)inventoryItem);
			} else if (object instanceof IsoWindow) {
				((IsoWindow)object).WeaponHit(player, (HandWeapon)inventoryItem);
			} else if (object instanceof IsoBarricade) {
				((IsoBarricade)object).WeaponHit(player, (HandWeapon)inventoryItem);
			}

			if (object.getObjectIndex() == -1) {
				ZLogger zLogger = LoggerManager.getLogger("map");
				String string2 = udpConnection.idStr;
				zLogger.write(string2 + " \"" + udpConnection.username + "\" destroyed " + (object.getName() != null ? object.getName() : object.getObjectName()) + " with " + (string.isEmpty() ? "BareHands" : string) + " at " + int1 + "," + int2 + "," + int3);
			}
		}
	}

	private static void putIsoObjectRefToByteBuffer(IsoObject object, ByteBuffer byteBuffer) {
		byteBuffer.putInt(object.square.x);
		byteBuffer.putInt(object.square.y);
		byteBuffer.putInt(object.square.z);
		byteBuffer.put((byte)object.square.getObjects().indexOf(object));
	}

	private static IsoObject getIsoObjectRefFromByteBuffer(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
		return square != null && byte1 >= 0 && byte1 < square.getObjects().size() ? (IsoObject)square.getObjects().get(byte1) : null;
	}

	static void receiveDrink(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		float float1 = byteBuffer.getFloat();
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			Stats stats = player.getStats();
			stats.thirst -= float1;
			if (player.getStats().thirst < 0.0F) {
				player.getStats().thirst = 0.0F;
			}
		}
	}

	private static void process(ZomboidNetData zomboidNetData) {
		ByteBuffer byteBuffer = zomboidNetData.buffer;
		UdpConnection udpConnection = udpEngine.getActiveConnection(zomboidNetData.connection);
		try {
			switch (zomboidNetData.type) {
			default: 
				doZomboidDataInMainLoop(zomboidNetData);
			
			}
		} catch (Exception exception) {
			DebugLog.log(DebugType.Network, "Error with packet of type: " + zomboidNetData.type);
			exception.printStackTrace();
		}
	}

	static void receiveEatFood(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		float float1 = byteBuffer.getFloat();
		InventoryItem inventoryItem = null;
		try {
			inventoryItem = InventoryItem.loadItem(byteBuffer, 195);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (inventoryItem instanceof Food) {
			IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
			if (player != null) {
				player.Eat(inventoryItem, float1);
			}
		}
	}

	static void receivePingFromClient(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		long long1 = byteBuffer.getLong();
		if (long1 == -1L) {
			DebugLog.Multiplayer.warn("Player \"%s\" toggled lua debugger", udpConnection.username);
		} else {
			if (udpConnection.accessLevel != 32) {
				return;
			}

			PacketTypes.PacketType.PingFromClient.doPacket(byteBufferWriter);
			try {
				byteBufferWriter.putLong(long1);
				MPStatistics.write(udpConnection, byteBufferWriter.bb);
				PacketTypes.PacketType.PingFromClient.send(udpConnection);
				MPStatistics.requested();
			} catch (Exception exception) {
				udpConnection.cancelPacket();
			}
		}
	}

	static void receiveBandage(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			int int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			float float1 = byteBuffer.getFloat();
			boolean boolean2 = byteBuffer.get() == 1;
			String string = GameWindow.ReadStringUTF(byteBuffer);
			player.getBodyDamage().SetBandaged(int1, boolean1, float1, boolean2, string);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.Bandage.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(short2);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putBoolean(boolean1);
					byteBufferWriter.putFloat(float1);
					byteBufferWriter.putBoolean(boolean2);
					GameWindow.WriteStringUTF(byteBufferWriter.bb, string);
					PacketTypes.PacketType.Bandage.send(udpConnection2);
				}
			}
		}
	}

	static void receiveStitch(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		Stitch stitch = new Stitch();
		stitch.parse(byteBuffer, udpConnection);
		if (stitch.isConsistent() && stitch.validate(udpConnection)) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.Stitch.doPacket(byteBufferWriter);
					stitch.write(byteBufferWriter);
					PacketTypes.PacketType.Stitch.send(udpConnection2);
				}
			}
		}
	}

	@Deprecated
	static void receiveWoundInfection(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			int int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setInfectedWound(boolean1);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.WoundInfection.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(short2);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putBoolean(boolean1);
					PacketTypes.PacketType.WoundInfection.send(udpConnection2);
				}
			}
		}
	}

	static void receiveDisinfect(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		Disinfect disinfect = new Disinfect();
		disinfect.parse(byteBuffer, udpConnection);
		if (disinfect.isConsistent() && disinfect.validate(udpConnection)) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.Disinfect.doPacket(byteBufferWriter);
					disinfect.write(byteBufferWriter);
					PacketTypes.PacketType.Disinfect.send(udpConnection2);
				}
			}
		}
	}

	static void receiveSplint(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			int int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			String string = boolean1 ? GameWindow.ReadStringUTF(byteBuffer) : null;
			float float1 = boolean1 ? byteBuffer.getFloat() : 0.0F;
			BodyPart bodyPart = player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
			bodyPart.setSplint(boolean1, float1);
			bodyPart.setSplintItem(string);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.Splint.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(short2);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putBoolean(boolean1);
					if (boolean1) {
						byteBufferWriter.putUTF(string);
						byteBufferWriter.putFloat(float1);
					}

					PacketTypes.PacketType.Splint.send(udpConnection2);
				}
			}
		}
	}

	static void receiveAdditionalPain(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			int int1 = byteBuffer.getInt();
			float float1 = byteBuffer.getFloat();
			BodyPart bodyPart = player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
			bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + float1);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.AdditionalPain.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(short2);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putFloat(float1);
					PacketTypes.PacketType.AdditionalPain.send(udpConnection2);
				}
			}
		}
	}

	static void receiveRemoveGlass(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		RemoveGlass removeGlass = new RemoveGlass();
		removeGlass.parse(byteBuffer, udpConnection);
		if (removeGlass.isConsistent() && removeGlass.validate(udpConnection)) {
			removeGlass.process();
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.RemoveGlass.doPacket(byteBufferWriter);
					removeGlass.write(byteBufferWriter);
					PacketTypes.PacketType.RemoveGlass.send(udpConnection2);
				}
			}
		}
	}

	static void receiveRemoveBullet(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		RemoveBullet removeBullet = new RemoveBullet();
		removeBullet.parse(byteBuffer, udpConnection);
		if (removeBullet.isConsistent() && removeBullet.validate(udpConnection)) {
			removeBullet.process();
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.RemoveBullet.doPacket(byteBufferWriter);
					removeBullet.write(byteBufferWriter);
					PacketTypes.PacketType.RemoveBullet.send(udpConnection2);
				}
			}
		}
	}

	static void receiveCleanBurn(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		CleanBurn cleanBurn = new CleanBurn();
		cleanBurn.parse(byteBuffer, udpConnection);
		if (cleanBurn.isConsistent() && cleanBurn.validate(udpConnection)) {
			cleanBurn.process();
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.CleanBurn.doPacket(byteBufferWriter);
					cleanBurn.write(byteBufferWriter);
					PacketTypes.PacketType.CleanBurn.send(udpConnection2);
				}
			}
		}
	}

	static void receiveBodyDamageUpdate(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		BodyDamageSync.instance.serverPacket(byteBuffer);
	}

	static void receiveReceiveCommand(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = null;
		string2 = handleClientCommand(string.substring(1), udpConnection);
		if (string2 == null) {
			string2 = handleServerCommand(string.substring(1), udpConnection);
		}

		if (string2 == null) {
			string2 = "Unknown command " + string;
		}

		if (!string.substring(1).startsWith("roll") && !string.substring(1).startsWith("card")) {
			ChatServer.getInstance().sendMessageToServerChat(udpConnection, string2);
		} else {
			ChatServer.getInstance().sendMessageToServerChat(udpConnection, string2);
		}
	}

	private static String handleClientCommand(String string, UdpConnection udpConnection) {
		if (string == null) {
			return null;
		} else {
			ArrayList arrayList = new ArrayList();
			Matcher matcher = Pattern.compile("([^\"]\\S*|\".*?\")\\s*").matcher(string);
			while (matcher.find()) {
				arrayList.add(matcher.group(1).replace("\"", ""));
			}

			int int1 = arrayList.size();
			String[] stringArray = (String[])arrayList.toArray(new String[int1]);
			String string2 = int1 > 0 ? stringArray[0].toLowerCase() : "";
			String string3;
			if (string2.equals("card")) {
				PlayWorldSoundServer("ChatDrawCard", false, getAnyPlayerFromConnection(udpConnection).getCurrentSquare(), 0.0F, 3.0F, 1.0F, false);
				string3 = udpConnection.username;
				return string3 + " drew " + ServerOptions.getRandomCard();
			} else if (string2.equals("roll")) {
				if (int1 != 2) {
					return (String)ServerOptions.clientOptionsList.get("roll");
				} else {
					boolean boolean1 = false;
					try {
						int int2 = Integer.parseInt(stringArray[1]);
						PlayWorldSoundServer("ChatRollDice", false, getAnyPlayerFromConnection(udpConnection).getCurrentSquare(), 0.0F, 3.0F, 1.0F, false);
						string3 = udpConnection.username;
						return string3 + " rolls a " + int2 + "-sided dice and obtains " + Rand.Next(int2);
					} catch (Exception exception) {
						return (String)ServerOptions.clientOptionsList.get("roll");
					}
				}
			} else if (string2.equals("changepwd")) {
				if (int1 == 3) {
					String string4 = stringArray[1];
					String string5 = stringArray[2];
					try {
						return ServerWorldDatabase.instance.changePwd(udpConnection.username, string4.trim(), string5.trim());
					} catch (SQLException sQLException) {
						sQLException.printStackTrace();
						return "A SQL error occured";
					}
				} else {
					return (String)ServerOptions.clientOptionsList.get("changepwd");
				}
			} else if (string2.equals("dragons")) {
				return "Sorry, you don\'t have the required materials.";
			} else if (string2.equals("dance")) {
				return "Stop kidding me...";
			} else if (string2.equals("safehouse")) {
				if (int1 == 2 && udpConnection != null) {
					if (!ServerOptions.instance.PlayerSafehouse.getValue() && !ServerOptions.instance.AdminSafehouse.getValue()) {
						return "Safehouses are disabled on this server.";
					} else if ("release".equals(stringArray[1])) {
						SafeHouse safeHouse = SafeHouse.hasSafehouse(udpConnection.username);
						if (safeHouse == null) {
							return "You don\'t own a safehouse.";
						} else if (!ServerOptions.instance.PlayerSafehouse.getValue() && !"admin".equals(udpConnection.accessLevel) && !"moderator".equals(udpConnection.accessLevel)) {
							return "Only admin or moderator may release safehouses";
						} else {
							safeHouse.removeSafeHouse((IsoPlayer)null);
							return "Safehouse released";
						}
					} else {
						return (String)ServerOptions.clientOptionsList.get("safehouse");
					}
				} else {
					return (String)ServerOptions.clientOptionsList.get("safehouse");
				}
			} else {
				return null;
			}
		}
	}

	public static void doZomboidDataInMainLoop(ZomboidNetData zomboidNetData) {
		synchronized (MainLoopNetDataHighPriorityQ) {
			MainLoopNetDataHighPriorityQ.add(zomboidNetData);
		}
	}

	static void receiveEquip(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		byte byte3 = byteBuffer.get();
		InventoryItem inventoryItem = null;
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (byte3 == 1) {
			try {
				inventoryItem = InventoryItem.loadItem(byteBuffer, 195);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			if (inventoryItem == null) {
				LoggerManager.getLogger("user").write(udpConnection.idStr + " equipped unknown item type");
				return;
			}
		}

		if (player != null) {
			if (inventoryItem != null) {
				inventoryItem.setContainer(player.getInventory());
			}

			if (byte2 == 0) {
				player.setPrimaryHandItem(inventoryItem);
			} else {
				if (byte3 == 2) {
					inventoryItem = player.getPrimaryHandItem();
				}

				player.setSecondaryHandItem(inventoryItem);
			}

			try {
				if (byte3 == 1 && inventoryItem != null && byteBuffer.get() == 1) {
					inventoryItem.getVisual().load(byteBuffer, 195);
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		if (player != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection2);
					if (player2 != null) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.Equip.doPacket(byteBufferWriter);
						byteBufferWriter.putShort(player.OnlineID);
						byteBufferWriter.putByte(byte2);
						byteBufferWriter.putByte(byte3);
						if (byte3 == 1) {
							try {
								inventoryItem.saveWithSize(byteBufferWriter.bb, false);
								if (inventoryItem.getVisual() != null) {
									byteBufferWriter.bb.put((byte)1);
									inventoryItem.getVisual().save(byteBufferWriter.bb);
								} else {
									byteBufferWriter.bb.put((byte)0);
								}
							} catch (IOException ioException2) {
								ioException2.printStackTrace();
							}
						}

						PacketTypes.PacketType.Equip.send(udpConnection2);
					}
				}
			}
		}
	}

	static void receivePlayerConnect(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		receivePlayerConnect(byteBuffer, udpConnection, udpConnection.username);
		sendInitialWorldState(udpConnection);
	}

	static void receiveScoreboardUpdate(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.ScoreboardUpdate.doPacket(byteBufferWriter);
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = new ArrayList();
		ArrayList arrayList3 = new ArrayList();
		int int1;
		for (int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.isFullyConnected()) {
				for (int int2 = 0; int2 < 4; ++int2) {
					if (udpConnection2.usernames[int2] != null) {
						arrayList.add(udpConnection2.usernames[int2]);
						IsoPlayer player = getPlayerByRealUserName(udpConnection2.usernames[int2]);
						if (player != null) {
							arrayList2.add(player.getDisplayName());
						} else {
							String string = ServerWorldDatabase.instance.getDisplayName(udpConnection2.usernames[int2]);
							arrayList2.add(string == null ? udpConnection2.usernames[int2] : string);
						}

						if (SteamUtils.isSteamModeEnabled()) {
							arrayList3.add(udpConnection2.steamID);
						}
					}
				}
			}
		}

		byteBufferWriter.putInt(arrayList.size());
		for (int1 = 0; int1 < arrayList.size(); ++int1) {
			byteBufferWriter.putUTF((String)arrayList.get(int1));
			byteBufferWriter.putUTF((String)arrayList2.get(int1));
			if (SteamUtils.isSteamModeEnabled()) {
				byteBufferWriter.putLong((Long)arrayList3.get(int1));
			}
		}

		PacketTypes.PacketType.ScoreboardUpdate.send(udpConnection);
	}

	static void receiveStopSound(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		StopSoundPacket stopSoundPacket = new StopSoundPacket();
		stopSoundPacket.parse(byteBuffer, udpConnection);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.isFullyConnected()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.StopSound.doPacket(byteBufferWriter);
				stopSoundPacket.write(byteBufferWriter);
				PacketTypes.PacketType.StopSound.send(udpConnection2);
			}
		}
	}

	static void receivePlaySound(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		PlaySoundPacket playSoundPacket = new PlaySoundPacket();
		playSoundPacket.parse(byteBuffer, udpConnection);
		IsoMovingObject movingObject = playSoundPacket.getMovingObject();
		if (playSoundPacket.isConsistent()) {
			int int1 = 70;
			GameSound gameSound = GameSounds.getSound(playSoundPacket.getName());
			int int2;
			if (gameSound != null) {
				for (int2 = 0; int2 < gameSound.clips.size(); ++int2) {
					GameSoundClip gameSoundClip = (GameSoundClip)gameSound.clips.get(int2);
					if (gameSoundClip.hasMaxDistance()) {
						int1 = Math.max(int1, (int)gameSoundClip.distanceMax);
					}
				}
			}

			for (int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.isFullyConnected()) {
					IsoPlayer player = getAnyPlayerFromConnection(udpConnection2);
					if (player != null && (movingObject == null || udpConnection2.RelevantTo(movingObject.getX(), movingObject.getY(), (float)int1))) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.PlaySound.doPacket(byteBufferWriter);
						playSoundPacket.write(byteBufferWriter);
						PacketTypes.PacketType.PlaySound.send(udpConnection2);
					}
				}
			}
		}
	}

	static void receivePlayWorldSound(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		PlayWorldSoundPacket playWorldSoundPacket = new PlayWorldSoundPacket();
		playWorldSoundPacket.parse(byteBuffer, udpConnection);
		if (playWorldSoundPacket.isConsistent()) {
			int int1 = 70;
			GameSound gameSound = GameSounds.getSound(playWorldSoundPacket.getName());
			int int2;
			if (gameSound != null) {
				for (int2 = 0; int2 < gameSound.clips.size(); ++int2) {
					GameSoundClip gameSoundClip = (GameSoundClip)gameSound.clips.get(int2);
					if (gameSoundClip.hasMaxDistance()) {
						int1 = Math.max(int1, (int)gameSoundClip.distanceMax);
					}
				}
			}

			for (int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.isFullyConnected()) {
					IsoPlayer player = getAnyPlayerFromConnection(udpConnection2);
					if (player != null && udpConnection2.RelevantTo((float)playWorldSoundPacket.getX(), (float)playWorldSoundPacket.getY(), (float)int1)) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.PlayWorldSound.doPacket(byteBufferWriter);
						playWorldSoundPacket.write(byteBufferWriter);
						PacketTypes.PacketType.PlayWorldSound.send(udpConnection2);
					}
				}
			}
		}
	}

	private static void PlayWorldSound(String string, IsoGridSquare square, float float1) {
		if (bServer && square != null) {
			int int1 = square.getX();
			int int2 = square.getY();
			int int3 = square.getZ();
			PlayWorldSoundPacket playWorldSoundPacket = new PlayWorldSoundPacket();
			playWorldSoundPacket.set(string, int1, int2, (byte)int3);
			DebugType debugType = DebugType.Sound;
			String string2 = playWorldSoundPacket.getDescription();
			DebugLog.log(debugType, "sending " + string2 + " radius=" + float1);
			for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int4);
				IsoPlayer player = getAnyPlayerFromConnection(udpConnection);
				if (player != null && udpConnection.RelevantTo((float)int1, (float)int2, float1 * 2.0F)) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.PlayWorldSound.doPacket(byteBufferWriter);
					playWorldSoundPacket.write(byteBufferWriter);
					PacketTypes.PacketType.PlayWorldSound.send(udpConnection);
				}
			}
		}
	}

	public static void PlayWorldSoundServer(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2) {
		PlayWorldSound(string, square, float2);
	}

	public static void PlayWorldSoundServer(IsoGameCharacter gameCharacter, String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2) {
		if (gameCharacter == null || !gameCharacter.isInvisible() || DebugOptions.instance.Character.Debug.PlaySoundWhenInvisible.getValue()) {
			PlayWorldSound(string, square, float2);
		}
	}

	public static void PlayWorldSoundWavServer(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2) {
		PlayWorldSound(string, square, float2);
	}

	public static void PlaySoundAtEveryPlayer(String string, int int1, int int2, int int3) {
		PlaySoundAtEveryPlayer(string, int1, int2, int3, false);
	}

	public static void PlaySoundAtEveryPlayer(String string) {
		PlaySoundAtEveryPlayer(string, -1, -1, -1, true);
	}

	public static void PlaySoundAtEveryPlayer(String string, int int1, int int2, int int3, boolean boolean1) {
		if (bServer) {
			if (boolean1) {
				DebugLog.log(DebugType.Sound, "sound: sending " + string + " at every player (using player location)");
			} else {
				DebugLog.log(DebugType.Sound, "sound: sending " + string + " at every player location x=" + int1 + " y=" + int2);
			}

			for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int4);
				IsoPlayer player = getAnyPlayerFromConnection(udpConnection);
				if (player != null && !player.isDeaf()) {
					if (boolean1) {
						int1 = (int)player.getX();
						int2 = (int)player.getY();
						int3 = (int)player.getZ();
					}

					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.PlaySoundEveryPlayer.doPacket(byteBufferWriter);
					byteBufferWriter.putUTF(string);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					PacketTypes.PacketType.PlaySoundEveryPlayer.send(udpConnection);
				}
			}
		}
	}

	public static void sendZombieSound(IsoZombie.ZombieSound zombieSound, IsoZombie zombie) {
		float float1 = (float)zombieSound.radius();
		DebugLog.log(DebugType.Sound, "sound: sending zombie sound " + zombieSound);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection.isFullyConnected() && udpConnection.RelevantTo(zombie.getX(), zombie.getY(), float1)) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.ZombieSound.doPacket(byteBufferWriter);
				byteBufferWriter.putShort(zombie.OnlineID);
				byteBufferWriter.putByte((byte)zombieSound.ordinal());
				PacketTypes.PacketType.ZombieSound.send(udpConnection);
			}
		}
	}

	static void receiveZombieHelmetFalling(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		short short2 = byteBuffer.getShort();
		String string = GameWindow.ReadString(byteBuffer);
		IsoZombie zombie = (IsoZombie)ServerMap.instance.ZombieMap.get(short2);
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null && zombie != null) {
			zombie.serverRemoveItemFromZombie(string);
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						try {
							ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
							PacketTypes.PacketType.ZombieHelmetFalling.doPacket(byteBufferWriter);
							byteBufferWriter.putShort(short2);
							byteBufferWriter.putUTF(string);
							PacketTypes.PacketType.ZombieHelmetFalling.send(udpConnection2);
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	static void receivePlayerAttachedItem(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		String string = GameWindow.ReadString(byteBuffer);
		boolean boolean1 = byteBuffer.get() == 1;
		InventoryItem inventoryItem = null;
		if (boolean1) {
			String string2 = GameWindow.ReadString(byteBuffer);
			inventoryItem = InventoryItemFactory.CreateItem(string2);
			if (inventoryItem == null) {
				return;
			}
		}

		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			player.setAttachedItem(string, inventoryItem);
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						try {
							ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
							PacketTypes.PacketType.PlayerAttachedItem.doPacket(byteBufferWriter);
							byteBufferWriter.putShort(player.OnlineID);
							GameWindow.WriteString(byteBufferWriter.bb, string);
							byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
							if (boolean1) {
								GameWindow.WriteString(byteBufferWriter.bb, inventoryItem.getFullType());
							}

							PacketTypes.PacketType.PlayerAttachedItem.send(udpConnection2);
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	static void receiveSyncClothing(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		SyncClothingPacket syncClothingPacket = new SyncClothingPacket();
		syncClothingPacket.parse(byteBuffer, udpConnection);
		if (syncClothingPacket.isConsistent()) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player = getAnyPlayerFromConnection(udpConnection);
					if (player != null) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.SyncClothing.doPacket(byteBufferWriter);
						syncClothingPacket.write(byteBufferWriter);
						PacketTypes.PacketType.SyncClothing.send(udpConnection2);
					}
				}
			}
		}
	}

	static void receiveHumanVisual(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			if (!udpConnection.havePlayer(player)) {
				DebugLog.Network.warn("User " + udpConnection.username + " sent HumanVisual packet for non owned player #" + player.OnlineID);
			} else {
				try {
					player.getHumanVisual().load(byteBuffer, 195);
				} catch (Throwable throwable) {
					ExceptionLogger.logException(throwable);
					return;
				}

				for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
						IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection2);
						if (player2 != null) {
							ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
							PacketTypes.PacketType.HumanVisual.doPacket(byteBufferWriter);
							try {
								byteBufferWriter.putShort(player.OnlineID);
								player.getHumanVisual().save(byteBufferWriter.bb);
								PacketTypes.PacketType.HumanVisual.send(udpConnection2);
							} catch (Throwable throwable2) {
								udpConnection2.cancelPacket();
								ExceptionLogger.logException(throwable2);
							}
						}
					}
				}
			}
		}
	}

	public static void initClientCommandFilter() {
		String string = ServerOptions.getInstance().ClientCommandFilter.getValue();
		ccFilters.clear();
		String[] stringArray = string.split(";");
		String[] stringArray2 = stringArray;
		int int1 = stringArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = stringArray2[int2];
			if (!string2.isEmpty() && string2.contains(".") && (string2.startsWith("+") || string2.startsWith("-"))) {
				String[] stringArray3 = string2.split("\\.");
				if (stringArray3.length == 2) {
					String string3 = stringArray3[0].substring(1);
					String string4 = stringArray3[1];
					GameServer.CCFilter cCFilter = new GameServer.CCFilter();
					cCFilter.command = string4;
					cCFilter.allow = stringArray3[0].startsWith("+");
					cCFilter.next = (GameServer.CCFilter)ccFilters.get(string3);
					ccFilters.put(string3, cCFilter);
				}
			}
		}
	}

	static void receiveClientCommand(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		boolean boolean1 = byteBuffer.get() == 1;
		KahluaTable kahluaTable = null;
		if (boolean1) {
			kahluaTable = LuaManager.platform.newTable();
			try {
				TableNetworkUtils.load(kahluaTable, byteBuffer);
			} catch (Exception exception) {
				exception.printStackTrace();
				return;
			}
		}

		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (byte1 == -1) {
			player = getAnyPlayerFromConnection(udpConnection);
		}

		if (player == null) {
			DebugLog.log("receiveClientCommand: player is null");
		} else {
			GameServer.CCFilter cCFilter = (GameServer.CCFilter)ccFilters.get(string);
			if (cCFilter == null || cCFilter.passes(string2)) {
				ZLogger zLogger = LoggerManager.getLogger("cmd");
				String string3 = udpConnection.idStr;
				zLogger.write(string3 + " \"" + player.username + "\" " + string + "." + string2 + " @ " + (int)player.getX() + "," + (int)player.getY() + "," + (int)player.getZ());
			}

			if (!"vehicle".equals(string) || !"remove".equals(string2) || Core.bDebug || PlayerType.isPrivileged(udpConnection.accessLevel) || player.networkAI.isDismantleAllowed()) {
				LuaEventManager.triggerEvent("OnClientCommand", string, string2, player, kahluaTable);
			}
		}
	}

	static void receiveGlobalObjects(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (byte1 == -1) {
			player = getAnyPlayerFromConnection(udpConnection);
		}

		if (player == null) {
			DebugLog.log("receiveGlobalObjects: player is null");
		} else {
			SGlobalObjectNetwork.receive(byteBuffer, player);
		}
	}

	public static IsoPlayer getAnyPlayerFromConnection(UdpConnection udpConnection) {
		for (int int1 = 0; int1 < 4; ++int1) {
			if (udpConnection.players[int1] != null) {
				return udpConnection.players[int1];
			}
		}

		return null;
	}

	public static IsoPlayer getPlayerFromConnection(UdpConnection udpConnection, int int1) {
		return int1 >= 0 && int1 < 4 ? udpConnection.players[int1] : null;
	}

	public static IsoPlayer getPlayerByRealUserName(String string) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			for (int int2 = 0; int2 < 4; ++int2) {
				IsoPlayer player = udpConnection.players[int2];
				if (player != null && player.username.equals(string)) {
					return player;
				}
			}
		}

		return null;
	}

	public static IsoPlayer getPlayerByUserName(String string) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			for (int int2 = 0; int2 < 4; ++int2) {
				IsoPlayer player = udpConnection.players[int2];
				if (player != null && (player.getDisplayName().equals(string) || player.getUsername().equals(string))) {
					return player;
				}
			}
		}

		return null;
	}

	public static IsoPlayer getPlayerByUserNameForCommand(String string) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			for (int int2 = 0; int2 < 4; ++int2) {
				IsoPlayer player = udpConnection.players[int2];
				if (player != null && (player.getDisplayName().toLowerCase().equals(string.toLowerCase()) || player.getDisplayName().toLowerCase().startsWith(string.toLowerCase()))) {
					return player;
				}
			}
		}

		return null;
	}

	public static UdpConnection getConnectionByPlayerOnlineID(short short1) {
		return udpEngine.getActiveConnection((Long)IDToAddressMap.get(short1));
	}

	public static UdpConnection getConnectionFromPlayer(IsoPlayer player) {
		Long Long1 = (Long)PlayerToAddressMap.get(player);
		return Long1 == null ? null : udpEngine.getActiveConnection(Long1);
	}

	static void receiveRemoveBlood(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			square.removeBlood(false, boolean1);
			for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
				if (udpConnection2 != udpConnection && udpConnection2.RelevantTo((float)int1, (float)int2)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.RemoveBlood.doPacket(byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					byteBufferWriter.putBoolean(boolean1);
					PacketTypes.PacketType.RemoveBlood.send(udpConnection2);
				}
			}
		}
	}

	public static void sendAddItemToContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		Object object = itemContainer.getParent();
		if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
			object = itemContainer.getContainingItem().getWorldItem();
		}

		if (object == null) {
			DebugLog.General.error("container has no parent object");
		} else {
			IsoGridSquare square = ((IsoObject)object).getSquare();
			if (square == null) {
				DebugLog.General.error("container parent object has no square");
			} else {
				for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
					if (udpConnection.RelevantTo((float)square.x, (float)square.y)) {
						ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
						PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(byteBufferWriter);
						if (object instanceof IsoDeadBody) {
							byteBufferWriter.putShort((short)0);
							byteBufferWriter.putInt(((IsoObject)object).square.getX());
							byteBufferWriter.putInt(((IsoObject)object).square.getY());
							byteBufferWriter.putInt(((IsoObject)object).square.getZ());
							byteBufferWriter.putByte((byte)((IsoObject)object).getStaticMovingObjectIndex());
						} else if (object instanceof IsoWorldInventoryObject) {
							byteBufferWriter.putShort((short)1);
							byteBufferWriter.putInt(((IsoObject)object).square.getX());
							byteBufferWriter.putInt(((IsoObject)object).square.getY());
							byteBufferWriter.putInt(((IsoObject)object).square.getZ());
							byteBufferWriter.putInt(((IsoWorldInventoryObject)object).getItem().id);
						} else if (object instanceof BaseVehicle) {
							byteBufferWriter.putShort((short)3);
							byteBufferWriter.putInt(((IsoObject)object).square.getX());
							byteBufferWriter.putInt(((IsoObject)object).square.getY());
							byteBufferWriter.putInt(((IsoObject)object).square.getZ());
							byteBufferWriter.putShort(((BaseVehicle)object).VehicleID);
							byteBufferWriter.putByte((byte)itemContainer.vehiclePart.getIndex());
						} else {
							byteBufferWriter.putShort((short)2);
							byteBufferWriter.putInt(((IsoObject)object).square.getX());
							byteBufferWriter.putInt(((IsoObject)object).square.getY());
							byteBufferWriter.putInt(((IsoObject)object).square.getZ());
							byteBufferWriter.putByte((byte)((IsoObject)object).square.getObjects().indexOf(object));
							byteBufferWriter.putByte((byte)((IsoObject)object).getContainerIndex(itemContainer));
						}

						try {
							CompressIdenticalItems.save(byteBufferWriter.bb, inventoryItem);
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection);
					}
				}
			}
		}
	}

	public static void sendRemoveItemFromContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		Object object = itemContainer.getParent();
		if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
			object = itemContainer.getContainingItem().getWorldItem();
		}

		if (object == null) {
			DebugLog.log("sendRemoveItemFromContainer: o is null");
		} else {
			IsoGridSquare square = ((IsoObject)object).getSquare();
			if (square == null) {
				DebugLog.log("sendRemoveItemFromContainer: square is null");
			} else {
				for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
					if (udpConnection.RelevantTo((float)square.x, (float)square.y)) {
						ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
						PacketTypes.PacketType.RemoveInventoryItemFromContainer.doPacket(byteBufferWriter);
						if (object instanceof IsoDeadBody) {
							byteBufferWriter.putShort((short)0);
							byteBufferWriter.putInt(((IsoObject)object).square.getX());
							byteBufferWriter.putInt(((IsoObject)object).square.getY());
							byteBufferWriter.putInt(((IsoObject)object).square.getZ());
							byteBufferWriter.putByte((byte)((IsoObject)object).getStaticMovingObjectIndex());
							byteBufferWriter.putInt(1);
							byteBufferWriter.putInt(inventoryItem.id);
						} else if (object instanceof IsoWorldInventoryObject) {
							byteBufferWriter.putShort((short)1);
							byteBufferWriter.putInt(((IsoObject)object).square.getX());
							byteBufferWriter.putInt(((IsoObject)object).square.getY());
							byteBufferWriter.putInt(((IsoObject)object).square.getZ());
							byteBufferWriter.putInt(((IsoWorldInventoryObject)object).getItem().id);
							byteBufferWriter.putInt(1);
							byteBufferWriter.putInt(inventoryItem.id);
						} else if (object instanceof BaseVehicle) {
							byteBufferWriter.putShort((short)3);
							byteBufferWriter.putInt(((IsoObject)object).square.getX());
							byteBufferWriter.putInt(((IsoObject)object).square.getY());
							byteBufferWriter.putInt(((IsoObject)object).square.getZ());
							byteBufferWriter.putShort(((BaseVehicle)object).VehicleID);
							byteBufferWriter.putByte((byte)itemContainer.vehiclePart.getIndex());
							byteBufferWriter.putInt(1);
							byteBufferWriter.putInt(inventoryItem.id);
						} else {
							byteBufferWriter.putShort((short)2);
							byteBufferWriter.putInt(((IsoObject)object).square.getX());
							byteBufferWriter.putInt(((IsoObject)object).square.getY());
							byteBufferWriter.putInt(((IsoObject)object).square.getZ());
							byteBufferWriter.putByte((byte)((IsoObject)object).square.getObjects().indexOf(object));
							byteBufferWriter.putByte((byte)((IsoObject)object).getContainerIndex(itemContainer));
							byteBufferWriter.putInt(1);
							byteBufferWriter.putInt(inventoryItem.id);
						}

						PacketTypes.PacketType.RemoveInventoryItemFromContainer.send(udpConnection);
					}
				}
			}
		}
	}

	static void receiveRemoveInventoryItemFromContainer(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		alreadyRemoved.clear();
		ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
		short short2 = byteBufferReader.getShort();
		int int1 = byteBufferReader.getInt();
		int int2 = byteBufferReader.getInt();
		int int3 = byteBufferReader.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			square = ServerMap.instance.getGridSquare(int1, int2, int3);
		}

		HashSet hashSet = new HashSet();
		boolean boolean1 = false;
		int int4 = 0;
		byte byte1;
		int int5;
		int int6;
		InventoryItem inventoryItem;
		int int7;
		if (short2 == 0) {
			byte1 = byteBufferReader.getByte();
			int4 = byteBuffer.getInt();
			if (square != null && byte1 >= 0 && byte1 < square.getStaticMovingObjects().size()) {
				IsoObject object = (IsoObject)square.getStaticMovingObjects().get(byte1);
				if (object != null && object.getContainer() != null) {
					for (int5 = 0; int5 < int4; ++int5) {
						int6 = byteBufferReader.getInt();
						inventoryItem = object.getContainer().getItemWithID(int6);
						if (inventoryItem == null) {
							alreadyRemoved.add(int6);
						} else {
							object.getContainer().Remove(inventoryItem);
							boolean1 = true;
							hashSet.add(inventoryItem.getFullType());
						}
					}

					object.getContainer().setExplored(true);
					object.getContainer().setHasBeenLooted(true);
				}
			}
		} else if (short2 == 1) {
			if (square != null) {
				int7 = byteBufferReader.getInt();
				int4 = byteBuffer.getInt();
				ItemContainer itemContainer = null;
				for (int5 = 0; int5 < square.getWorldObjects().size(); ++int5) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int5);
					if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && worldInventoryObject.getItem().id == int7) {
						itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
						break;
					}
				}

				if (itemContainer != null) {
					for (int5 = 0; int5 < int4; ++int5) {
						int6 = byteBufferReader.getInt();
						inventoryItem = itemContainer.getItemWithID(int6);
						if (inventoryItem == null) {
							alreadyRemoved.add(int6);
						} else {
							itemContainer.Remove(inventoryItem);
							hashSet.add(inventoryItem.getFullType());
						}
					}

					itemContainer.setExplored(true);
					itemContainer.setHasBeenLooted(true);
				}
			}
		} else {
			int int8;
			byte byte2;
			if (short2 == 2) {
				byte1 = byteBufferReader.getByte();
				byte2 = byteBufferReader.getByte();
				int4 = byteBuffer.getInt();
				if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
					IsoObject object2 = (IsoObject)square.getObjects().get(byte1);
					ItemContainer itemContainer2 = object2 != null ? object2.getContainerByIndex(byte2) : null;
					if (itemContainer2 != null) {
						for (int int9 = 0; int9 < int4; ++int9) {
							int8 = byteBufferReader.getInt();
							InventoryItem inventoryItem2 = itemContainer2.getItemWithID(int8);
							if (inventoryItem2 == null) {
								alreadyRemoved.add(int8);
							} else {
								itemContainer2.Remove(inventoryItem2);
								itemContainer2.setExplored(true);
								itemContainer2.setHasBeenLooted(true);
								boolean1 = true;
								hashSet.add(inventoryItem2.getFullType());
							}
						}

						LuaManager.updateOverlaySprite(object2);
					}
				}
			} else if (short2 == 3) {
				short short3 = byteBufferReader.getShort();
				byte2 = byteBufferReader.getByte();
				int4 = byteBuffer.getInt();
				BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short3);
				if (baseVehicle != null) {
					VehiclePart vehiclePart = baseVehicle == null ? null : baseVehicle.getPartByIndex(byte2);
					ItemContainer itemContainer3 = vehiclePart == null ? null : vehiclePart.getItemContainer();
					if (itemContainer3 != null) {
						for (int8 = 0; int8 < int4; ++int8) {
							int int10 = byteBufferReader.getInt();
							InventoryItem inventoryItem3 = itemContainer3.getItemWithID(int10);
							if (inventoryItem3 == null) {
								alreadyRemoved.add(int10);
							} else {
								itemContainer3.Remove(inventoryItem3);
								itemContainer3.setExplored(true);
								itemContainer3.setHasBeenLooted(true);
								boolean1 = true;
								hashSet.add(inventoryItem3.getFullType());
							}
						}
					}
				}
			}
		}

		for (int7 = 0; int7 < udpEngine.connections.size(); ++int7) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int7);
			if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && square != null && udpConnection2.RelevantTo((float)square.x, (float)square.y)) {
				byteBuffer.rewind();
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.RemoveInventoryItemFromContainer.doPacket(byteBufferWriter);
				byteBufferWriter.bb.put(byteBuffer);
				PacketTypes.PacketType.RemoveInventoryItemFromContainer.send(udpConnection2);
			}
		}

		if (!alreadyRemoved.isEmpty()) {
			ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
			PacketTypes.PacketType.RemoveContestedItemsFromInventory.doPacket(byteBufferWriter2);
			byteBufferWriter2.putInt(alreadyRemoved.size());
			for (int int11 = 0; int11 < alreadyRemoved.size(); ++int11) {
				byteBufferWriter2.putInt((Integer)alreadyRemoved.get(int11));
			}

			PacketTypes.PacketType.RemoveContestedItemsFromInventory.send(udpConnection);
		}

		alreadyRemoved.clear();
		LoggerManager.getLogger("item").write(udpConnection.idStr + " \"" + udpConnection.username + "\" container -" + int4 + " " + int1 + "," + int2 + "," + int3 + " " + hashSet.toString());
	}

	private static void readItemStats(ByteBuffer byteBuffer, InventoryItem inventoryItem) {
		int int1 = byteBuffer.getInt();
		float float1 = byteBuffer.getFloat();
		boolean boolean1 = byteBuffer.get() == 1;
		inventoryItem.setUses(int1);
		if (inventoryItem instanceof DrainableComboItem) {
			((DrainableComboItem)inventoryItem).setDelta(float1);
			((DrainableComboItem)inventoryItem).updateWeight();
		}

		if (boolean1 && inventoryItem instanceof Food) {
			Food food = (Food)inventoryItem;
			food.setHungChange(byteBuffer.getFloat());
			food.setCalories(byteBuffer.getFloat());
			food.setCarbohydrates(byteBuffer.getFloat());
			food.setLipids(byteBuffer.getFloat());
			food.setProteins(byteBuffer.getFloat());
			food.setThirstChange(byteBuffer.getFloat());
			food.setFluReduction(byteBuffer.getInt());
			food.setPainReduction(byteBuffer.getFloat());
			food.setEndChange(byteBuffer.getFloat());
			food.setReduceFoodSickness(byteBuffer.getInt());
			food.setStressChange(byteBuffer.getFloat());
			food.setFatigueChange(byteBuffer.getFloat());
		}
	}

	static void receiveItemStats(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		byte byte1;
		int int4;
		byte byte2;
		int int5;
		int int6;
		ItemContainer itemContainer;
		InventoryItem inventoryItem;
		switch (short2) {
		case 0: 
			byte2 = byteBuffer.get();
			int6 = byteBuffer.getInt();
			if (square != null && byte2 >= 0 && byte2 < square.getStaticMovingObjects().size()) {
				IsoMovingObject movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(byte2);
				itemContainer = movingObject.getContainer();
				if (itemContainer != null) {
					inventoryItem = itemContainer.getItemWithID(int6);
					if (inventoryItem != null) {
						readItemStats(byteBuffer, inventoryItem);
					}
				}
			}

			break;
		
		case 1: 
			int5 = byteBuffer.getInt();
			if (square != null) {
				for (int6 = 0; int6 < square.getWorldObjects().size(); ++int6) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int6);
					if (worldInventoryObject.getItem() != null && worldInventoryObject.getItem().id == int5) {
						readItemStats(byteBuffer, worldInventoryObject.getItem());
						break;
					}

					if (worldInventoryObject.getItem() instanceof InventoryContainer) {
						itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
						inventoryItem = itemContainer.getItemWithID(int5);
						if (inventoryItem != null) {
							readItemStats(byteBuffer, inventoryItem);
							break;
						}
					}
				}
			}

			break;
		
		case 2: 
			byte2 = byteBuffer.get();
			byte1 = byteBuffer.get();
			int4 = byteBuffer.getInt();
			if (square != null && byte2 >= 0 && byte2 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(byte2);
				ItemContainer itemContainer2 = object.getContainerByIndex(byte1);
				if (itemContainer2 != null) {
					InventoryItem inventoryItem2 = itemContainer2.getItemWithID(int4);
					if (inventoryItem2 != null) {
						readItemStats(byteBuffer, inventoryItem2);
					}
				}
			}

			break;
		
		case 3: 
			short short3 = byteBuffer.getShort();
			byte1 = byteBuffer.get();
			int4 = byteBuffer.getInt();
			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short3);
			if (baseVehicle != null) {
				VehiclePart vehiclePart = baseVehicle.getPartByIndex(byte1);
				if (vehiclePart != null) {
					ItemContainer itemContainer3 = vehiclePart.getItemContainer();
					if (itemContainer3 != null) {
						InventoryItem inventoryItem3 = itemContainer3.getItemWithID(int4);
						if (inventoryItem3 != null) {
							readItemStats(byteBuffer, inventoryItem3);
						}
					}
				}
			}

		
		}
		for (int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
			if (udpConnection2 != udpConnection && udpConnection2.RelevantTo((float)int1, (float)int2)) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.ItemStats.doPacket(byteBufferWriter);
				byteBuffer.rewind();
				byteBufferWriter.bb.put(byteBuffer);
				PacketTypes.PacketType.ItemStats.send(udpConnection2);
			}
		}
	}

	static void receiveRequestItemsForContainer(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
		short short2 = byteBuffer.getShort();
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBufferReader.getInt();
		int int2 = byteBufferReader.getInt();
		int int3 = byteBufferReader.getInt();
		short short3 = byteBufferReader.getShort();
		byte byte1 = -1;
		byte byte2 = -1;
		int int4 = 0;
		short short4 = 0;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		IsoObject object = null;
		ItemContainer itemContainer = null;
		int int5;
		if (short3 == 2) {
			byte1 = byteBufferReader.getByte();
			byte2 = byteBufferReader.getByte();
			if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
				object = (IsoObject)square.getObjects().get(byte1);
				if (object != null) {
					itemContainer = object.getContainerByIndex(byte2);
					if (itemContainer == null || itemContainer.isExplored()) {
						return;
					}
				}
			}
		} else if (short3 == 3) {
			short4 = byteBufferReader.getShort();
			byte2 = byteBufferReader.getByte();
			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short4);
			if (baseVehicle != null) {
				VehiclePart vehiclePart = ((BaseVehicle)baseVehicle).getPartByIndex(byte2);
				itemContainer = vehiclePart == null ? null : vehiclePart.getItemContainer();
				if (itemContainer == null || itemContainer.isExplored()) {
					return;
				}
			}
		} else if (short3 == 1) {
			int4 = byteBufferReader.getInt();
			for (int5 = 0; int5 < square.getWorldObjects().size(); ++int5) {
				IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int5);
				if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && worldInventoryObject.getItem().id == int4) {
					itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
					break;
				}
			}
		} else if (short3 == 0) {
			byte1 = byteBufferReader.getByte();
			if (square != null && byte1 >= 0 && byte1 < square.getStaticMovingObjects().size()) {
				object = (IsoObject)square.getStaticMovingObjects().get(byte1);
				if (object != null && object.getContainer() != null) {
					if (object.getContainer().isExplored()) {
						return;
					}

					itemContainer = object.getContainer();
				}
			}
		}

		if (itemContainer != null && !itemContainer.isExplored()) {
			itemContainer.setExplored(true);
			int5 = itemContainer.Items.size();
			ItemPickerJava.fillContainer(itemContainer, (IsoPlayer)IDToPlayerMap.get(short2));
			if (int5 != itemContainer.Items.size()) {
				for (int int6 = 0; int6 < udpEngine.connections.size(); ++int6) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int6);
					if (udpConnection2.RelevantTo((float)square.x, (float)square.y)) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(byteBufferWriter);
						byteBufferWriter.putShort(short3);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						if (short3 == 0) {
							byteBufferWriter.putByte(byte1);
						} else if (short3 == 1) {
							byteBufferWriter.putInt(int4);
						} else if (short3 == 3) {
							byteBufferWriter.putShort(short4);
							byteBufferWriter.putByte(byte2);
						} else {
							byteBufferWriter.putByte(byte1);
							byteBufferWriter.putByte(byte2);
						}

						try {
							CompressIdenticalItems.save(byteBufferWriter.bb, itemContainer.getItems(), (IsoGameCharacter)null);
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection2);
					}
				}
			}
		}
	}

	public static void sendItemsInContainer(IsoObject object, ItemContainer itemContainer) {
		if (udpEngine != null) {
			if (itemContainer == null) {
				DebugLog.log("sendItemsInContainer: container is null");
			} else {
				if (object instanceof IsoWorldInventoryObject) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)object;
					if (!(worldInventoryObject.getItem() instanceof InventoryContainer)) {
						DebugLog.log("sendItemsInContainer: IsoWorldInventoryObject item isn\'t a container");
						return;
					}

					InventoryContainer inventoryContainer = (InventoryContainer)worldInventoryObject.getItem();
					if (inventoryContainer.getInventory() != itemContainer) {
						DebugLog.log("sendItemsInContainer: wrong container for IsoWorldInventoryObject");
						return;
					}
				} else if (object instanceof BaseVehicle) {
					if (itemContainer.vehiclePart == null || itemContainer.vehiclePart.getItemContainer() != itemContainer || itemContainer.vehiclePart.getVehicle() != object) {
						DebugLog.log("sendItemsInContainer: wrong container for BaseVehicle");
						return;
					}
				} else if (object instanceof IsoDeadBody) {
					if (itemContainer != object.getContainer()) {
						DebugLog.log("sendItemsInContainer: wrong container for IsoDeadBody");
						return;
					}
				} else if (object.getContainerIndex(itemContainer) == -1) {
					DebugLog.log("sendItemsInContainer: wrong container for IsoObject");
					return;
				}

				if (object != null && itemContainer != null && !itemContainer.getItems().isEmpty()) {
					for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
						UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
						if (udpConnection.RelevantTo((float)object.square.x, (float)object.square.y)) {
							ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
							PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(byteBufferWriter);
							if (object instanceof IsoDeadBody) {
								byteBufferWriter.putShort((short)0);
							} else if (object instanceof IsoWorldInventoryObject) {
								byteBufferWriter.putShort((short)1);
							} else if (object instanceof BaseVehicle) {
								byteBufferWriter.putShort((short)3);
							} else {
								byteBufferWriter.putShort((short)2);
							}

							byteBufferWriter.putInt(object.getSquare().getX());
							byteBufferWriter.putInt(object.getSquare().getY());
							byteBufferWriter.putInt(object.getSquare().getZ());
							if (object instanceof IsoDeadBody) {
								byteBufferWriter.putByte((byte)object.getStaticMovingObjectIndex());
							} else if (object instanceof IsoWorldInventoryObject) {
								byteBufferWriter.putInt(((IsoWorldInventoryObject)object).getItem().id);
							} else if (object instanceof BaseVehicle) {
								byteBufferWriter.putShort(((BaseVehicle)object).VehicleID);
								byteBufferWriter.putByte((byte)itemContainer.vehiclePart.getIndex());
							} else {
								byteBufferWriter.putByte((byte)object.getObjectIndex());
								byteBufferWriter.putByte((byte)object.getContainerIndex(itemContainer));
							}

							try {
								CompressIdenticalItems.save(byteBufferWriter.bb, itemContainer.getItems(), (IsoGameCharacter)null);
							} catch (Exception exception) {
								exception.printStackTrace();
							}

							PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection);
						}
					}
				}
			}
		}
	}

	private static void logDupeItem(UdpConnection udpConnection, String string) {
		IsoPlayer player = null;
		for (int int1 = 0; int1 < Players.size(); ++int1) {
			if (udpConnection.username.equals(((IsoPlayer)Players.get(int1)).username)) {
				player = (IsoPlayer)Players.get(int1);
				break;
			}
		}

		String string2 = "";
		if (player != null) {
			string2 = LoggerManager.getPlayerCoords(player);
			ZLogger zLogger = LoggerManager.getLogger("user");
			String string3 = player.getDisplayName();
			zLogger.write("Error: Dupe item ID for " + string3 + " " + string2);
		}

		ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.DupeItem, string, GameServer.class.getSimpleName(), 1);
	}

	static void receiveAddInventoryItemToContainer(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
		short short2 = byteBufferReader.getShort();
		int int1 = byteBufferReader.getInt();
		int int2 = byteBufferReader.getInt();
		int int3 = byteBufferReader.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		HashSet hashSet = new HashSet();
		byte byte1 = 0;
		if (square == null) {
			DebugLog.log("ERROR sendItemsToContainer square is null");
		} else {
			ItemContainer itemContainer = null;
			IsoObject object = null;
			byte byte2;
			int int4;
			if (short2 == 0) {
				byte2 = byteBufferReader.getByte();
				if (byte2 < 0 || byte2 >= square.getStaticMovingObjects().size()) {
					DebugLog.log("ERROR sendItemsToContainer invalid corpse index");
					return;
				}

				IsoObject object2 = (IsoObject)square.getStaticMovingObjects().get(byte2);
				if (object2 != null && object2.getContainer() != null) {
					itemContainer = object2.getContainer();
				}
			} else if (short2 == 1) {
				int int5 = byteBufferReader.getInt();
				for (int4 = 0; int4 < square.getWorldObjects().size(); ++int4) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int4);
					if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && worldInventoryObject.getItem().id == int5) {
						itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
						break;
					}
				}

				if (itemContainer == null) {
					DebugLog.log("ERROR sendItemsToContainer can\'t find world item with id=" + int5);
					return;
				}
			} else {
				byte byte3;
				if (short2 == 2) {
					byte2 = byteBufferReader.getByte();
					byte3 = byteBufferReader.getByte();
					if (byte2 < 0 || byte2 >= square.getObjects().size()) {
						DebugLog.log("ERROR sendItemsToContainer invalid object index");
						for (int int6 = 0; int6 < square.getObjects().size(); ++int6) {
							if (((IsoObject)square.getObjects().get(int6)).getContainer() != null) {
								byte2 = (byte)int6;
								byte3 = 0;
								break;
							}
						}

						if (byte2 == -1) {
							return;
						}
					}

					object = (IsoObject)square.getObjects().get(byte2);
					itemContainer = object != null ? object.getContainerByIndex(byte3) : null;
				} else if (short2 == 3) {
					short short3 = byteBufferReader.getShort();
					byte3 = byteBufferReader.getByte();
					BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short3);
					if (baseVehicle == null) {
						DebugLog.log("ERROR sendItemsToContainer invalid vehicle id");
						return;
					}

					VehiclePart vehiclePart = baseVehicle.getPartByIndex(byte3);
					itemContainer = vehiclePart == null ? null : vehiclePart.getItemContainer();
				}
			}

			if (itemContainer != null) {
				try {
					ArrayList arrayList = CompressIdenticalItems.load(byteBufferReader.bb, 195, (ArrayList)null, (ArrayList)null);
					for (int4 = 0; int4 < arrayList.size(); ++int4) {
						InventoryItem inventoryItem = (InventoryItem)arrayList.get(int4);
						if (inventoryItem != null) {
							if (itemContainer.containsID(inventoryItem.id)) {
								System.out.println("Error: Dupe item ID for " + udpConnection.username);
								logDupeItem(udpConnection, inventoryItem.getDisplayName());
							} else {
								itemContainer.addItem(inventoryItem);
								itemContainer.setExplored(true);
								hashSet.add(inventoryItem.getFullType());
								if (object instanceof IsoMannequin) {
									((IsoMannequin)object).wearItem(inventoryItem, (IsoGameCharacter)null);
								}
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				if (object != null) {
					LuaManager.updateOverlaySprite(object);
					if ("campfire".equals(itemContainer.getType())) {
						object.sendObjectChange("container.customTemperature");
					}
				}
			}
		}

		for (int int7 = 0; int7 < udpEngine.connections.size(); ++int7) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int7);
			if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.RelevantTo((float)square.x, (float)square.y)) {
				byteBuffer.rewind();
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(byteBufferWriter);
				byteBufferWriter.bb.put(byteBuffer);
				PacketTypes.PacketType.AddInventoryItemToContainer.send(udpConnection2);
			}
		}

		LoggerManager.getLogger("item").write(udpConnection.idStr + " \"" + udpConnection.username + "\" container +" + byte1 + " " + int1 + "," + int2 + "," + int3 + " " + hashSet.toString());
	}

	public static void addConnection(UdpConnection udpConnection) {
		synchronized (MainLoopNetDataHighPriorityQ) {
			MainLoopNetDataHighPriorityQ.add(new GameServer.DelayedConnection(udpConnection, true));
		}
	}

	public static void addDisconnect(UdpConnection udpConnection) {
		synchronized (MainLoopNetDataHighPriorityQ) {
			MainLoopNetDataHighPriorityQ.add(new GameServer.DelayedConnection(udpConnection, false));
		}
	}

	public static void disconnectPlayer(IsoPlayer player, UdpConnection udpConnection) {
		if (player != null) {
			SafetySystemManager.storeSafety(player);
			ChatServer.getInstance().disconnectPlayer(player.getOnlineID());
			if (player.getVehicle() != null) {
				VehiclesDB2.instance.updateVehicleAndTrailer(player.getVehicle());
				if (player.getVehicle().isDriver(player) && player.getVehicle().isNetPlayerId(player.getOnlineID())) {
					player.getVehicle().setNetPlayerAuthorization(BaseVehicle.Authorization.Server, -1);
					player.getVehicle().getController().clientForce = 0.0F;
					player.getVehicle().jniLinearVelocity.set(0.0F, 0.0F, 0.0F);
				}

				int int1 = player.getVehicle().getSeat(player);
				if (int1 != -1) {
					player.getVehicle().clearPassenger(int1);
				}
			}

			if (!player.isDead()) {
				ServerWorldDatabase.instance.saveTransactionID(player.username, player.getTransactionID());
			}

			NetworkZombieManager.getInstance().clearTargetAuth(udpConnection, player);
			player.removeFromWorld();
			player.removeFromSquare();
			PlayerToAddressMap.remove(player);
			IDToAddressMap.remove(player.OnlineID);
			IDToPlayerMap.remove(player.OnlineID);
			Players.remove(player);
			SafeHouse.updateSafehousePlayersConnected();
			SafeHouse safeHouse = SafeHouse.hasSafehouse(player);
			if (safeHouse != null && safeHouse.isOwner(player)) {
				Iterator iterator = IDToPlayerMap.values().iterator();
				while (iterator.hasNext()) {
					IsoPlayer player2 = (IsoPlayer)iterator.next();
					safeHouse.checkTrespass(player2);
				}
			}

			udpConnection.usernames[player.PlayerIndex] = null;
			udpConnection.players[player.PlayerIndex] = null;
			udpConnection.playerIDs[player.PlayerIndex] = -1;
			udpConnection.ReleventPos[player.PlayerIndex] = null;
			udpConnection.connectArea[player.PlayerIndex] = null;
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.PlayerTimeout.doPacket(byteBufferWriter);
				byteBufferWriter.putShort(player.OnlineID);
				PacketTypes.PacketType.PlayerTimeout.send(udpConnection2);
			}

			ServerLOS.instance.removePlayer(player);
			ZombiePopulationManager.instance.updateLoadedAreas();
			DebugType debugType = DebugType.Network;
			String string = player.getDisplayName();
			DebugLog.log(debugType, "Disconnected player \"" + string + "\" " + udpConnection.idStr);
			ZLogger zLogger = LoggerManager.getLogger("user");
			string = udpConnection.idStr;
			zLogger.write(string + " \"" + player.getUsername() + "\" disconnected player " + LoggerManager.getPlayerCoords(player));
		}
	}

	public static void heartBeat() {
		++count;
	}

	public static short getFreeSlot() {
		for (short short1 = 0; short1 < udpEngine.getMaxConnections(); ++short1) {
			if (SlotToConnection[short1] == null) {
				return short1;
			}
		}

		return -1;
	}

	public static void receiveClientConnect(UdpConnection udpConnection, ServerWorldDatabase.LogonResult logonResult) {
		ConnectionManager.log("receive-packet", "client-connect", udpConnection);
		short short1 = getFreeSlot();
		short short2 = (short)(short1 * 4);
		if (udpConnection.playerDownloadServer != null) {
			try {
				IDToAddressMap.put(short2, udpConnection.getConnectedGUID());
				udpConnection.playerDownloadServer.destroy();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		playerToCoordsMap.put(short2, new Vector2());
		playerMovedToFastMap.put(short2, 0);
		SlotToConnection[short1] = udpConnection;
		udpConnection.playerIDs[0] = short2;
		IDToAddressMap.put(short2, udpConnection.getConnectedGUID());
		udpConnection.playerDownloadServer = new PlayerDownloadServer(udpConnection);
		DebugLog.log(DebugType.Network, "Connected new client " + udpConnection.username + " ID # " + short2);
		udpConnection.playerDownloadServer.startConnectionTest();
		KahluaTable kahluaTable = SpawnPoints.instance.getSpawnRegions();
		for (int int1 = 1; int1 < kahluaTable.size() + 1; ++int1) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.SpawnRegion.doPacket(byteBufferWriter);
			byteBufferWriter.putInt(int1);
			try {
				((KahluaTable)kahluaTable.rawget(int1)).save(byteBufferWriter.bb);
				PacketTypes.PacketType.SpawnRegion.send(udpConnection);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		RequestDataPacket requestDataPacket = new RequestDataPacket();
		requestDataPacket.sendConnectingDetails(udpConnection, logonResult);
	}

	static void receiveReplaceOnCooked(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
		int int1 = byteBufferReader.getInt();
		int int2 = byteBufferReader.getInt();
		int int3 = byteBufferReader.getInt();
		byte byte1 = byteBufferReader.getByte();
		byte byte2 = byteBufferReader.getByte();
		int int4 = byteBufferReader.getInt();
		IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
		if (square != null) {
			if (byte1 >= 0 && byte1 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(byte1);
				if (object != null) {
					ItemContainer itemContainer = object.getContainerByIndex(byte2);
					if (itemContainer != null) {
						InventoryItem inventoryItem = itemContainer.getItemWithID(int4);
						if (inventoryItem != null) {
							Food food = (Food)Type.tryCastTo(inventoryItem, Food.class);
							if (food != null) {
								if (food.getReplaceOnCooked() != null && !food.isRotten()) {
									for (int int5 = 0; int5 < food.getReplaceOnCooked().size(); ++int5) {
										InventoryItem inventoryItem2 = itemContainer.AddItem((String)food.getReplaceOnCooked().get(int5));
										if (inventoryItem2 != null) {
											inventoryItem2.copyConditionModData(food);
											if (inventoryItem2 instanceof Food && food instanceof Food) {
											}

											if (inventoryItem2 instanceof Food && ((Food)inventoryItem2).isBadInMicrowave() && itemContainer.isMicrowave()) {
												inventoryItem2.setUnhappyChange(5.0F);
												inventoryItem2.setBoredomChange(5.0F);
												((Food)inventoryItem2).setCookedInMicrowave(true);
											}

											sendAddItemToContainer(itemContainer, inventoryItem2);
										}
									}

									sendRemoveItemFromContainer(itemContainer, food);
									itemContainer.Remove((InventoryItem)food);
									IsoWorld.instance.CurrentCell.addToProcessItemsRemove((InventoryItem)food);
								}
							}
						}
					}
				}
			}
		}
	}

	static void receivePlayerDataRequest(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		PlayerDataRequestPacket playerDataRequestPacket = new PlayerDataRequestPacket();
		playerDataRequestPacket.parse(byteBuffer, udpConnection);
		if (playerDataRequestPacket.isConsistent()) {
			playerDataRequestPacket.process(udpConnection);
		}
	}

	static void receiveRequestData(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		RequestDataPacket requestDataPacket = new RequestDataPacket();
		requestDataPacket.parse(byteBuffer, udpConnection);
		requestDataPacket.processServer(PacketTypes.PacketType.RequestData, udpConnection);
	}

	public static void sendMetaGrid(int int1, int int2, int int3, UdpConnection udpConnection) {
		IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
		if (int1 >= metaGrid.getMinX() && int1 <= metaGrid.getMaxX() && int2 >= metaGrid.getMinY() && int2 <= metaGrid.getMaxY()) {
			IsoMetaCell metaCell = metaGrid.getCellData(int1, int2);
			if (metaCell.info != null && int3 >= 0 && int3 < metaCell.info.RoomList.size()) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.MetaGrid.doPacket(byteBufferWriter);
				byteBufferWriter.putShort((short)int1);
				byteBufferWriter.putShort((short)int2);
				byteBufferWriter.putShort((short)int3);
				byteBufferWriter.putBoolean(metaCell.info.getRoom(int3).def.bLightsActive);
				PacketTypes.PacketType.MetaGrid.send(udpConnection);
			}
		}
	}

	public static void sendMetaGrid(int int1, int int2, int int3) {
		for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int4);
			sendMetaGrid(int1, int2, int3, udpConnection);
		}
	}

	private static void preventIndoorZombies(int int1, int int2, int int3) {
		RoomDef roomDef = IsoWorld.instance.MetaGrid.getRoomAt(int1, int2, int3);
		if (roomDef != null) {
			boolean boolean1 = isSpawnBuilding(roomDef.getBuilding());
			roomDef.getBuilding().setAllExplored(true);
			roomDef.getBuilding().setAlarmed(false);
			ArrayList arrayList = IsoWorld.instance.CurrentCell.getZombieList();
			for (int int4 = 0; int4 < arrayList.size(); ++int4) {
				IsoZombie zombie = (IsoZombie)arrayList.get(int4);
				if ((boolean1 || zombie.bIndoorZombie) && zombie.getSquare() != null && zombie.getSquare().getRoom() != null && zombie.getSquare().getRoom().def.building == roomDef.getBuilding()) {
					VirtualZombieManager.instance.removeZombieFromWorld(zombie);
					if (int4 >= arrayList.size() || arrayList.get(int4) != zombie) {
						--int4;
					}
				}
			}
		}
	}

	private static void receivePlayerConnect(ByteBuffer byteBuffer, UdpConnection udpConnection, String string) {
		ConnectionManager.log("receive-packet", "player-connect", udpConnection);
		DebugLog.General.println("User:\'" + string + "\' ip:" + udpConnection.ip + " is trying to connect");
		byte byte1 = byteBuffer.get();
		if (byte1 >= 0 && byte1 < 4 && udpConnection.players[byte1] == null) {
			byte byte2 = (byte)Math.min(20, byteBuffer.get());
			udpConnection.ReleventRange = (byte)(byte2 / 2 + 2);
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			udpConnection.ReleventPos[byte1].x = float1;
			udpConnection.ReleventPos[byte1].y = float2;
			udpConnection.ReleventPos[byte1].z = float3;
			udpConnection.connectArea[byte1] = null;
			udpConnection.ChunkGridWidth = byte2;
			udpConnection.loadedCells[byte1] = new ClientServerMap(byte1, (int)float1, (int)float2, byte2);
			SurvivorDesc survivorDesc = SurvivorFactory.CreateSurvivor();
			try {
				survivorDesc.load(byteBuffer, 195, (IsoGameCharacter)null);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			IsoPlayer player = new IsoPlayer((IsoCell)null, survivorDesc, (int)float1, (int)float2, (int)float3);
			player.realx = float1;
			player.realy = float2;
			player.realz = (byte)((int)float3);
			player.PlayerIndex = byte1;
			player.OnlineChunkGridWidth = byte2;
			Players.add(player);
			player.bRemote = true;
			try {
				player.getHumanVisual().load(byteBuffer, 195);
				player.getItemVisuals().load(byteBuffer, 195);
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}

			short short1 = udpConnection.playerIDs[byte1];
			IDToPlayerMap.put(short1, player);
			udpConnection.players[byte1] = player;
			PlayerToAddressMap.put(player, udpConnection.getConnectedGUID());
			player.setOnlineID(short1);
			try {
				player.getXp().load(byteBuffer, 195);
			} catch (IOException ioException3) {
				ioException3.printStackTrace();
			}

			player.setAllChatMuted(byteBuffer.get() == 1);
			udpConnection.allChatMuted = player.isAllChatMuted();
			player.setTagPrefix(GameWindow.ReadString(byteBuffer));
			player.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
			player.setTransactionID(byteBuffer.getInt());
			player.setHoursSurvived(byteBuffer.getDouble());
			player.setZombieKills(byteBuffer.getInt());
			player.setDisplayName(GameWindow.ReadString(byteBuffer));
			player.setSpeakColour(new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
			player.showTag = byteBuffer.get() == 1;
			player.factionPvp = byteBuffer.get() == 1;
			if (SteamUtils.isSteamModeEnabled()) {
				player.setSteamID(udpConnection.steamID);
				String string2 = GameWindow.ReadStringUTF(byteBuffer);
				SteamGameServer.BUpdateUserData(udpConnection.steamID, udpConnection.username, 0);
			}

			byte byte3 = byteBuffer.get();
			InventoryItem inventoryItem = null;
			if (byte3 == 1) {
				try {
					inventoryItem = InventoryItem.loadItem(byteBuffer, 195);
				} catch (IOException ioException4) {
					ioException4.printStackTrace();
					return;
				}

				if (inventoryItem == null) {
					LoggerManager.getLogger("user").write(udpConnection.idStr + " equipped unknown item");
					return;
				}

				player.setPrimaryHandItem(inventoryItem);
			}

			inventoryItem = null;
			byte byte4 = byteBuffer.get();
			if (byte4 == 2) {
				player.setSecondaryHandItem(player.getPrimaryHandItem());
			}

			if (byte4 == 1) {
				try {
					inventoryItem = InventoryItem.loadItem(byteBuffer, 195);
				} catch (IOException ioException5) {
					ioException5.printStackTrace();
					return;
				}

				if (inventoryItem == null) {
					LoggerManager.getLogger("user").write(udpConnection.idStr + " equipped unknown item");
					return;
				}

				player.setSecondaryHandItem(inventoryItem);
			}

			int int1 = byteBuffer.getInt();
			int int2;
			for (int2 = 0; int2 < int1; ++int2) {
				String string3 = GameWindow.ReadString(byteBuffer);
				InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer));
				if (inventoryItem2 != null) {
					player.setAttachedItem(string3, inventoryItem2);
				}
			}

			int2 = byteBuffer.getInt();
			player.remoteSneakLvl = int2;
			player.username = string;
			player.accessLevel = PlayerType.toString(udpConnection.accessLevel);
			if (!player.accessLevel.equals("") && CoopSlave.instance == null) {
				player.setGhostMode(true);
				player.setInvisible(true);
				player.setGodMod(true);
			}

			ChatServer.getInstance().initPlayer(player.OnlineID);
			udpConnection.setFullyConnected();
			sendWeather(udpConnection);
			SafetySystemManager.restoreSafety(player);
			for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
				sendPlayerConnect(player, udpConnection2);
			}

			SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
			Iterator iterator = IDToPlayerMap.values().iterator();
			while (iterator.hasNext()) {
				IsoPlayer player2 = (IsoPlayer)iterator.next();
				if (player2.getOnlineID() != player.getOnlineID() && player2.isAlive()) {
					sendPlayerConnect(player2, udpConnection);
					syncInjuriesPacket.set(player2);
					sendPlayerInjuries(udpConnection, syncInjuriesPacket);
				}
			}

			udpConnection.loadedCells[byte1].setLoaded();
			udpConnection.loadedCells[byte1].sendPacket(udpConnection);
			preventIndoorZombies((int)float1, (int)float2, (int)float3);
			ServerLOS.instance.addPlayer(player);
			ZLogger zLogger = LoggerManager.getLogger("user");
			String string4 = udpConnection.idStr;
			zLogger.write(string4 + " \"" + player.username + "\" fully connected " + LoggerManager.getPlayerCoords(player));
			try {
				iterator = NonPvpZone.getAllZones().iterator();
				while (iterator.hasNext()) {
					NonPvpZone nonPvpZone = (NonPvpZone)iterator.next();
					sendNonPvpZone(nonPvpZone, false, udpConnection);
				}
			} catch (Exception exception) {
				DebugLog.Multiplayer.printException(exception, "Send non PVP zones", LogSeverity.Error);
			}
		}
	}

	static void receivePlayerSave(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		if ((Calendar.getInstance().getTimeInMillis() - previousSave) / 60000L >= 0L) {
			byte byte1 = byteBuffer.get();
			if (byte1 >= 0 && byte1 < 4) {
				short short2 = byteBuffer.getShort();
				float float1 = byteBuffer.getFloat();
				float float2 = byteBuffer.getFloat();
				float float3 = byteBuffer.getFloat();
				ServerMap.instance.saveZoneInsidePlayerInfluence(short2);
			}
		}
	}

	static void receiveSendPlayerProfile(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ServerPlayerDB.getInstance().serverUpdateNetworkCharacter(byteBuffer, udpConnection);
	}

	static void receiveLoadPlayerProfile(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ServerPlayerDB.getInstance().serverLoadNetworkCharacter(byteBuffer, udpConnection);
	}

	private static void coopAccessGranted(int int1, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.AddCoopPlayer.doPacket(byteBufferWriter);
		byteBufferWriter.putBoolean(true);
		byteBufferWriter.putByte((byte)int1);
		PacketTypes.PacketType.AddCoopPlayer.send(udpConnection);
	}

	private static void coopAccessDenied(String string, int int1, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.AddCoopPlayer.doPacket(byteBufferWriter);
		byteBufferWriter.putBoolean(false);
		byteBufferWriter.putByte((byte)int1);
		byteBufferWriter.putUTF(string);
		PacketTypes.PacketType.AddCoopPlayer.send(udpConnection);
	}

	static void receiveAddCoopPlayer(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		if (!ServerOptions.instance.AllowCoop.getValue() && byte2 != 0) {
			coopAccessDenied("Coop players not allowed", byte2, udpConnection);
		} else if (byte2 >= 0 && byte2 < 4) {
			if (udpConnection.players[byte2] != null && !udpConnection.players[byte2].isDead()) {
				coopAccessDenied("Coop player " + (byte2 + 1) + "/4 already exists", byte2, udpConnection);
			} else {
				String string;
				if (byte1 != 1) {
					if (byte1 == 2) {
						string = udpConnection.usernames[byte2];
						if (string == null) {
							coopAccessDenied("Coop player login wasn\'t received", byte2, udpConnection);
						} else {
							DebugLog.log("coop player=" + (byte2 + 1) + "/4 username=\"" + string + "\" player info received");
							receivePlayerConnect(byteBuffer, udpConnection, string);
						}
					}
				} else {
					string = GameWindow.ReadStringUTF(byteBuffer);
					if (string.isEmpty()) {
						coopAccessDenied("No username given", byte2, udpConnection);
					} else {
						for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
							UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
							for (int int2 = 0; int2 < 4; ++int2) {
								if ((udpConnection2 != udpConnection || byte2 != int2) && string.equals(udpConnection2.usernames[int2])) {
									coopAccessDenied("User \"" + string + "\" already connected", byte2, udpConnection);
									return;
								}
							}
						}

						DebugLog.log("coop player=" + (byte2 + 1) + "/4 username=\"" + string + "\" is joining");
						short short2;
						float float1;
						if (udpConnection.players[byte2] != null) {
							DebugLog.log("coop player=" + (byte2 + 1) + "/4 username=\"" + string + "\" is replacing dead player");
							short2 = udpConnection.players[byte2].OnlineID;
							disconnectPlayer(udpConnection.players[byte2], udpConnection);
							float float2 = byteBuffer.getFloat();
							float1 = byteBuffer.getFloat();
							udpConnection.usernames[byte2] = string;
							udpConnection.ReleventPos[byte2] = new Vector3(float2, float1, 0.0F);
							udpConnection.connectArea[byte2] = new Vector3(float2 / 10.0F, float1 / 10.0F, (float)udpConnection.ChunkGridWidth);
							udpConnection.playerIDs[byte2] = short2;
							IDToAddressMap.put(short2, udpConnection.getConnectedGUID());
							coopAccessGranted(byte2, udpConnection);
							ZombiePopulationManager.instance.updateLoadedAreas();
							if (ChatServer.isInited()) {
								ChatServer.getInstance().initPlayer(short2);
							}
						} else if (getPlayerCount() >= ServerOptions.getInstance().getMaxPlayers()) {
							coopAccessDenied("Server is full", byte2, udpConnection);
						} else {
							short2 = -1;
							short short3;
							for (short3 = 0; short3 < udpEngine.getMaxConnections(); ++short3) {
								if (SlotToConnection[short3] == udpConnection) {
									short2 = short3;
									break;
								}
							}

							short3 = (short)(short2 * 4 + byte2);
							DebugLog.log("coop player=" + (byte2 + 1) + "/4 username=\"" + string + "\" assigned id=" + short3);
							float1 = byteBuffer.getFloat();
							float float3 = byteBuffer.getFloat();
							udpConnection.usernames[byte2] = string;
							udpConnection.ReleventPos[byte2] = new Vector3(float1, float3, 0.0F);
							udpConnection.playerIDs[byte2] = short3;
							udpConnection.connectArea[byte2] = new Vector3(float1 / 10.0F, float3 / 10.0F, (float)udpConnection.ChunkGridWidth);
							IDToAddressMap.put(short3, udpConnection.getConnectedGUID());
							coopAccessGranted(byte2, udpConnection);
							ZombiePopulationManager.instance.updateLoadedAreas();
						}
					}
				}
			}
		} else {
			coopAccessDenied("Invalid coop player index", byte2, udpConnection);
		}
	}

	private static void sendInitialWorldState(UdpConnection udpConnection) {
		if (RainManager.isRaining()) {
			sendStartRain(udpConnection);
		}

		VehicleManager.instance.serverSendInitialWorldState(udpConnection);
		try {
			if (!ClimateManager.getInstance().isUpdated()) {
				ClimateManager.getInstance().update();
			}

			ClimateManager.getInstance().sendInitialState(udpConnection);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static void receiveObjectModData(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null && int4 >= 0 && int4 < square.getObjects().size()) {
			IsoObject object = (IsoObject)square.getObjects().get(int4);
			int int5;
			if (boolean1) {
				int5 = object.getWaterAmount();
				try {
					object.getModData().load((ByteBuffer)byteBuffer, 195);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

				if (int5 != object.getWaterAmount()) {
					LuaEventManager.triggerEvent("OnWaterAmountChange", object, int5);
				}
			} else if (object.hasModData()) {
				object.getModData().wipe();
			}

			for (int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.RelevantTo((float)int1, (float)int2)) {
					sendObjectModData(object, udpConnection2);
				}
			}
		} else if (square != null) {
			DebugLog.log("receiveObjectModData: index=" + int4 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
		} else if (bDebug) {
			DebugLog.log("receiveObjectModData: sq is null x,y,z=" + int1 + "," + int2 + "," + int3);
		}
	}

	private static void sendObjectModData(IsoObject object, UdpConnection udpConnection) {
		if (object.getSquare() != null) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.ObjectModData.doPacket(byteBufferWriter);
			byteBufferWriter.putInt(object.getSquare().getX());
			byteBufferWriter.putInt(object.getSquare().getY());
			byteBufferWriter.putInt(object.getSquare().getZ());
			byteBufferWriter.putInt(object.getSquare().getObjects().indexOf(object));
			if (object.getModData().isEmpty()) {
				byteBufferWriter.putByte((byte)0);
			} else {
				byteBufferWriter.putByte((byte)1);
				try {
					object.getModData().save(byteBufferWriter.bb);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}

			PacketTypes.PacketType.ObjectModData.send(udpConnection);
		}
	}

	public static void sendObjectModData(IsoObject object) {
		if (!bSoftReset && !bFastForward) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection.RelevantTo(object.getX(), object.getY())) {
					sendObjectModData(object, udpConnection);
				}
			}
		}
	}

	public static void sendSlowFactor(IsoGameCharacter gameCharacter) {
		if (gameCharacter instanceof IsoPlayer) {
			if (PlayerToAddressMap.containsKey(gameCharacter)) {
				long long1 = (Long)PlayerToAddressMap.get((IsoPlayer)gameCharacter);
				UdpConnection udpConnection = udpEngine.getActiveConnection(long1);
				if (udpConnection != null) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.SlowFactor.doPacket(byteBufferWriter);
					byteBufferWriter.putByte((byte)((IsoPlayer)gameCharacter).PlayerIndex);
					byteBufferWriter.putFloat(gameCharacter.getSlowTimer());
					byteBufferWriter.putFloat(gameCharacter.getSlowFactor());
					PacketTypes.PacketType.SlowFactor.send(udpConnection);
				}
			}
		}
	}

	private static void sendObjectChange(IsoObject object, String string, KahluaTable kahluaTable, UdpConnection udpConnection) {
		if (object.getSquare() != null) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.ObjectChange.doPacket(byteBufferWriter);
			if (object instanceof IsoPlayer) {
				byteBufferWriter.putByte((byte)1);
				byteBufferWriter.putShort(((IsoPlayer)object).OnlineID);
			} else if (object instanceof BaseVehicle) {
				byteBufferWriter.putByte((byte)2);
				byteBufferWriter.putShort(((BaseVehicle)object).getId());
			} else if (object instanceof IsoWorldInventoryObject) {
				byteBufferWriter.putByte((byte)3);
				byteBufferWriter.putInt(object.getSquare().getX());
				byteBufferWriter.putInt(object.getSquare().getY());
				byteBufferWriter.putInt(object.getSquare().getZ());
				byteBufferWriter.putInt(((IsoWorldInventoryObject)object).getItem().getID());
			} else if (object instanceof IsoDeadBody) {
				byteBufferWriter.putByte((byte)4);
				byteBufferWriter.putInt(object.getSquare().getX());
				byteBufferWriter.putInt(object.getSquare().getY());
				byteBufferWriter.putInt(object.getSquare().getZ());
				byteBufferWriter.putInt(object.getStaticMovingObjectIndex());
			} else {
				byteBufferWriter.putByte((byte)0);
				byteBufferWriter.putInt(object.getSquare().getX());
				byteBufferWriter.putInt(object.getSquare().getY());
				byteBufferWriter.putInt(object.getSquare().getZ());
				byteBufferWriter.putInt(object.getSquare().getObjects().indexOf(object));
			}

			byteBufferWriter.putUTF(string);
			object.saveChange(string, kahluaTable, byteBufferWriter.bb);
			PacketTypes.PacketType.ObjectChange.send(udpConnection);
		}
	}

	public static void sendObjectChange(IsoObject object, String string, KahluaTable kahluaTable) {
		if (!bSoftReset) {
			if (object != null) {
				for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
					if (udpConnection.RelevantTo(object.getX(), object.getY())) {
						sendObjectChange(object, string, kahluaTable, udpConnection);
					}
				}
			}
		}
	}

	public static void sendObjectChange(IsoObject object, String string, Object[] objectArray) {
		if (!bSoftReset) {
			if (objectArray.length == 0) {
				sendObjectChange(object, string, (KahluaTable)null);
			} else if (objectArray.length % 2 == 0) {
				KahluaTable kahluaTable = LuaManager.platform.newTable();
				for (int int1 = 0; int1 < objectArray.length; int1 += 2) {
					Object object2 = objectArray[int1 + 1];
					if (object2 instanceof Float) {
						kahluaTable.rawset(objectArray[int1], ((Float)object2).doubleValue());
					} else if (object2 instanceof Integer) {
						kahluaTable.rawset(objectArray[int1], ((Integer)object2).doubleValue());
					} else if (object2 instanceof Short) {
						kahluaTable.rawset(objectArray[int1], ((Short)object2).doubleValue());
					} else {
						kahluaTable.rawset(objectArray[int1], object2);
					}
				}

				sendObjectChange(object, string, kahluaTable);
			}
		}
	}

	private static void updateHandEquips(UdpConnection udpConnection, IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Equip.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(player.OnlineID);
		byteBufferWriter.putByte((byte)0);
		byteBufferWriter.putByte((byte)(player.getPrimaryHandItem() != null ? 1 : 0));
		if (player.getPrimaryHandItem() != null) {
			try {
				player.getPrimaryHandItem().saveWithSize(byteBufferWriter.bb, false);
				if (player.getPrimaryHandItem().getVisual() != null) {
					byteBufferWriter.bb.put((byte)1);
					player.getPrimaryHandItem().getVisual().save(byteBufferWriter.bb);
				} else {
					byteBufferWriter.bb.put((byte)0);
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		PacketTypes.PacketType.Equip.send(udpConnection);
		byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Equip.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(player.OnlineID);
		byteBufferWriter.putByte((byte)1);
		if (player.getSecondaryHandItem() == player.getPrimaryHandItem() && player.getSecondaryHandItem() != null) {
			byteBufferWriter.putByte((byte)2);
		} else {
			byteBufferWriter.putByte((byte)(player.getSecondaryHandItem() != null ? 1 : 0));
		}

		if (player.getSecondaryHandItem() != null) {
			try {
				player.getSecondaryHandItem().saveWithSize(byteBufferWriter.bb, false);
				if (player.getSecondaryHandItem().getVisual() != null) {
					byteBufferWriter.bb.put((byte)1);
					player.getSecondaryHandItem().getVisual().save(byteBufferWriter.bb);
				} else {
					byteBufferWriter.bb.put((byte)0);
				}
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}
		}

		PacketTypes.PacketType.Equip.send(udpConnection);
	}

	public static void receiveSyncCustomLightSettings(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
		if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
			if (square.getObjects().get(byte1) instanceof IsoLightSwitch) {
				((IsoLightSwitch)square.getObjects().get(byte1)).receiveSyncCustomizedSettings(byteBuffer, udpConnection);
			} else {
				DebugLog.log("Sync Lightswitch custom settings: found object not a instance of IsoLightSwitch, x,y,z=" + int1 + "," + int2 + "," + int3);
			}
		} else if (square != null) {
			DebugLog.log("Sync Lightswitch custom settings: index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
		} else {
			DebugLog.log("Sync Lightswitch custom settings: sq is null x,y,z=" + int1 + "," + int2 + "," + int3);
		}
	}

	private static void sendAlarmClock_Player(short short1, int int1, boolean boolean1, int int2, int int3, boolean boolean2, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.SyncAlarmClock.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(AlarmClock.PacketPlayer);
		byteBufferWriter.putShort(short1);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
		if (!boolean1) {
			byteBufferWriter.putInt(int2);
			byteBufferWriter.putInt(int3);
			byteBufferWriter.putByte((byte)(boolean2 ? 1 : 0));
		}

		PacketTypes.PacketType.SyncAlarmClock.send(udpConnection);
	}

	private static void sendAlarmClock_World(int int1, int int2, int int3, int int4, boolean boolean1, int int5, int int6, boolean boolean2, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.SyncAlarmClock.doPacket(byteBufferWriter);
		byteBufferWriter.putShort(AlarmClock.PacketWorld);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putInt(int3);
		byteBufferWriter.putInt(int4);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
		if (!boolean1) {
			byteBufferWriter.putInt(int5);
			byteBufferWriter.putInt(int6);
			byteBufferWriter.putByte((byte)(boolean2 ? 1 : 0));
		}

		PacketTypes.PacketType.SyncAlarmClock.send(udpConnection);
	}

	static void receiveSyncAlarmClock(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		int int1;
		int int2;
		if (short2 == AlarmClock.PacketPlayer) {
			short short3 = byteBuffer.getShort();
			int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			int2 = 0;
			int int3 = 0;
			boolean boolean2 = false;
			if (!boolean1) {
				int2 = byteBuffer.getInt();
				int3 = byteBuffer.getInt();
				boolean2 = byteBuffer.get() == 1;
			}

			IsoPlayer player = getPlayerFromConnection(udpConnection, short3);
			if (player != null) {
				for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
					if (udpConnection2 != udpConnection) {
						sendAlarmClock_Player(player.getOnlineID(), int1, boolean1, int2, int3, boolean2, udpConnection2);
					}
				}
			}
		} else if (short2 == AlarmClock.PacketWorld) {
			int int5 = byteBuffer.getInt();
			int1 = byteBuffer.getInt();
			int int6 = byteBuffer.getInt();
			int2 = byteBuffer.getInt();
			boolean boolean3 = byteBuffer.get() == 1;
			int int7 = 0;
			int int8 = 0;
			boolean boolean4 = false;
			if (!boolean3) {
				int7 = byteBuffer.getInt();
				int8 = byteBuffer.getInt();
				boolean4 = byteBuffer.get() == 1;
			}

			IsoGridSquare square = ServerMap.instance.getGridSquare(int5, int1, int6);
			if (square == null) {
				DebugLog.log("SyncAlarmClock: sq is null x,y,z=" + int5 + "," + int1 + "," + int6);
			} else {
				AlarmClock alarmClock = null;
				int int9;
				for (int9 = 0; int9 < square.getWorldObjects().size(); ++int9) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int9);
					if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof AlarmClock && worldInventoryObject.getItem().id == int2) {
						alarmClock = (AlarmClock)worldInventoryObject.getItem();
						break;
					}
				}

				if (alarmClock == null) {
					DebugLog.log("SyncAlarmClock: AlarmClock is null x,y,z=" + int5 + "," + int1 + "," + int6);
				} else {
					if (boolean3) {
						alarmClock.stopRinging();
					} else {
						alarmClock.setHour(int7);
						alarmClock.setMinute(int8);
						alarmClock.setAlarmSet(boolean4);
					}

					for (int9 = 0; int9 < udpEngine.connections.size(); ++int9) {
						UdpConnection udpConnection3 = (UdpConnection)udpEngine.connections.get(int9);
						if (udpConnection3 != udpConnection) {
							sendAlarmClock_World(int5, int1, int6, int2, boolean3, int7, int8, boolean4, udpConnection3);
						}
					}
				}
			}
		}
	}

	static void receiveSyncIsoObject(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		if (DebugOptions.instance.Network.Server.SyncIsoObject.getValue()) {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			byte byte1 = byteBuffer.get();
			byte byte2 = byteBuffer.get();
			byte byte3 = byteBuffer.get();
			if (byte2 == 1) {
				IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
				if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
					((IsoObject)square.getObjects().get(byte1)).syncIsoObject(true, byte3, udpConnection, byteBuffer);
				} else if (square != null) {
					DebugLog.log("SyncIsoObject: index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
				} else {
					DebugLog.log("SyncIsoObject: sq is null x,y,z=" + int1 + "," + int2 + "," + int3);
				}
			}
		}
	}

	static void receiveSyncIsoObjectReq(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		if (short2 <= 50 && short2 > 0) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.SyncIsoObjectReq.doPacket(byteBufferWriter);
			byteBufferWriter.putShort(short2);
			for (int int1 = 0; int1 < short2; ++int1) {
				int int2 = byteBuffer.getInt();
				int int3 = byteBuffer.getInt();
				int int4 = byteBuffer.getInt();
				byte byte1 = byteBuffer.get();
				IsoGridSquare square = ServerMap.instance.getGridSquare(int2, int3, int4);
				if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
					((IsoObject)square.getObjects().get(byte1)).syncIsoObjectSend(byteBufferWriter);
				} else if (square != null) {
					byteBufferWriter.putInt(square.getX());
					byteBufferWriter.putInt(square.getY());
					byteBufferWriter.putInt(square.getZ());
					byteBufferWriter.putByte(byte1);
					byteBufferWriter.putByte((byte)0);
					byteBufferWriter.putByte((byte)0);
				} else {
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					byteBufferWriter.putInt(int4);
					byteBufferWriter.putByte(byte1);
					byteBufferWriter.putByte((byte)2);
					byteBufferWriter.putByte((byte)0);
				}
			}

			PacketTypes.PacketType.SyncIsoObjectReq.send(udpConnection);
		}
	}

	static void receiveSyncObjects(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		if (short2 == 1) {
			SyncObjectChunkHashes(byteBuffer, udpConnection);
		} else if (short2 == 3) {
			SyncObjectsGridSquareRequest(byteBuffer, udpConnection);
		} else if (short2 == 5) {
			SyncObjectsRequest(byteBuffer, udpConnection);
		}
	}

	public static void SyncObjectChunkHashes(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		if (short1 <= 10 && short1 > 0) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter);
			byteBufferWriter.putShort((short)2);
			int int1 = byteBufferWriter.bb.position();
			byteBufferWriter.putShort((short)0);
			int int2 = 0;
			int int3;
			for (int3 = 0; int3 < short1; ++int3) {
				int int4 = byteBuffer.getInt();
				int int5 = byteBuffer.getInt();
				long long1 = byteBuffer.getLong();
				IsoChunk chunk = ServerMap.instance.getChunk(int4, int5);
				if (chunk != null) {
					++int2;
					byteBufferWriter.putShort((short)chunk.wx);
					byteBufferWriter.putShort((short)chunk.wy);
					byteBufferWriter.putLong(chunk.getHashCodeObjects());
					int int6 = byteBufferWriter.bb.position();
					byteBufferWriter.putShort((short)0);
					int int7 = 0;
					int int8;
					for (int8 = int4 * 10; int8 < int4 * 10 + 10; ++int8) {
						for (int int9 = int5 * 10; int9 < int5 * 10 + 10; ++int9) {
							for (int int10 = 0; int10 <= 7; ++int10) {
								IsoGridSquare square = ServerMap.instance.getGridSquare(int8, int9, int10);
								if (square == null) {
									break;
								}

								byteBufferWriter.putByte((byte)(square.getX() - chunk.wx * 10));
								byteBufferWriter.putByte((byte)(square.getY() - chunk.wy * 10));
								byteBufferWriter.putByte((byte)square.getZ());
								byteBufferWriter.putInt((int)square.getHashCodeObjects());
								++int7;
							}
						}
					}

					int8 = byteBufferWriter.bb.position();
					byteBufferWriter.bb.position(int6);
					byteBufferWriter.putShort((short)int7);
					byteBufferWriter.bb.position(int8);
				}
			}

			int3 = byteBufferWriter.bb.position();
			byteBufferWriter.bb.position(int1);
			byteBufferWriter.putShort((short)int2);
			byteBufferWriter.bb.position(int3);
			PacketTypes.PacketType.SyncObjects.send(udpConnection);
		}
	}

	public static void SyncObjectChunkHashes(IsoChunk chunk, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter);
		byteBufferWriter.putShort((short)2);
		byteBufferWriter.putShort((short)1);
		byteBufferWriter.putShort((short)chunk.wx);
		byteBufferWriter.putShort((short)chunk.wy);
		byteBufferWriter.putLong(chunk.getHashCodeObjects());
		int int1 = byteBufferWriter.bb.position();
		byteBufferWriter.putShort((short)0);
		int int2 = 0;
		int int3;
		for (int3 = chunk.wx * 10; int3 < chunk.wx * 10 + 10; ++int3) {
			for (int int4 = chunk.wy * 10; int4 < chunk.wy * 10 + 10; ++int4) {
				for (int int5 = 0; int5 <= 7; ++int5) {
					IsoGridSquare square = ServerMap.instance.getGridSquare(int3, int4, int5);
					if (square == null) {
						break;
					}

					byteBufferWriter.putByte((byte)(square.getX() - chunk.wx * 10));
					byteBufferWriter.putByte((byte)(square.getY() - chunk.wy * 10));
					byteBufferWriter.putByte((byte)square.getZ());
					byteBufferWriter.putInt((int)square.getHashCodeObjects());
					++int2;
				}
			}
		}

		int3 = byteBufferWriter.bb.position();
		byteBufferWriter.bb.position(int1);
		byteBufferWriter.putShort((short)int2);
		byteBufferWriter.bb.position(int3);
		PacketTypes.PacketType.SyncObjects.send(udpConnection);
	}

	public static void SyncObjectsGridSquareRequest(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		if (short1 <= 100 && short1 > 0) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter);
			byteBufferWriter.putShort((short)4);
			int int1 = byteBufferWriter.bb.position();
			byteBufferWriter.putShort((short)0);
			int int2 = 0;
			int int3;
			for (int3 = 0; int3 < short1; ++int3) {
				int int4 = byteBuffer.getInt();
				int int5 = byteBuffer.getInt();
				byte byte1 = byteBuffer.get();
				IsoGridSquare square = ServerMap.instance.getGridSquare(int4, int5, byte1);
				if (square != null) {
					++int2;
					byteBufferWriter.putInt(int4);
					byteBufferWriter.putInt(int5);
					byteBufferWriter.putByte((byte)byte1);
					byteBufferWriter.putByte((byte)square.getObjects().size());
					byteBufferWriter.putInt(0);
					int int6 = byteBufferWriter.bb.position();
					int int7;
					for (int7 = 0; int7 < square.getObjects().size(); ++int7) {
						byteBufferWriter.putLong(((IsoObject)square.getObjects().get(int7)).customHashCode());
					}

					int7 = byteBufferWriter.bb.position();
					byteBufferWriter.bb.position(int6 - 4);
					byteBufferWriter.putInt(int7);
					byteBufferWriter.bb.position(int7);
				}
			}

			int3 = byteBufferWriter.bb.position();
			byteBufferWriter.bb.position(int1);
			byteBufferWriter.putShort((short)int2);
			byteBufferWriter.bb.position(int3);
			PacketTypes.PacketType.SyncObjects.send(udpConnection);
		}
	}

	public static void SyncObjectsRequest(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		if (short1 <= 100 && short1 > 0) {
			for (int int1 = 0; int1 < short1; ++int1) {
				int int2 = byteBuffer.getInt();
				int int3 = byteBuffer.getInt();
				byte byte1 = byteBuffer.get();
				long long1 = byteBuffer.getLong();
				IsoGridSquare square = ServerMap.instance.getGridSquare(int2, int3, byte1);
				if (square != null) {
					for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
						if (((IsoObject)square.getObjects().get(int4)).customHashCode() == long1) {
							ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
							PacketTypes.PacketType.SyncObjects.doPacket(byteBufferWriter);
							byteBufferWriter.putShort((short)6);
							byteBufferWriter.putInt(int2);
							byteBufferWriter.putInt(int3);
							byteBufferWriter.putByte((byte)byte1);
							byteBufferWriter.putLong(long1);
							byteBufferWriter.putByte((byte)square.getObjects().size());
							for (int int5 = 0; int5 < square.getObjects().size(); ++int5) {
								byteBufferWriter.putLong(((IsoObject)square.getObjects().get(int5)).customHashCode());
							}

							try {
								((IsoObject)square.getObjects().get(int4)).writeToRemoteBuffer(byteBufferWriter);
							} catch (Throwable throwable) {
								DebugLog.log("ERROR: GameServer.SyncObjectsRequest " + throwable.getMessage());
								udpConnection.cancelPacket();
								break;
							}

							PacketTypes.PacketType.SyncObjects.send(udpConnection);
							break;
						}
					}
				}
			}
		}
	}

	static void receiveSyncDoorKey(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		int int4 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
			IsoObject object = (IsoObject)square.getObjects().get(byte1);
			if (object instanceof IsoDoor) {
				IsoDoor door = (IsoDoor)object;
				door.keyId = int4;
				for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.SyncDoorKey.doPacket(byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						byteBufferWriter.putByte(byte1);
						byteBufferWriter.putInt(int4);
						PacketTypes.PacketType.SyncDoorKey.send(udpConnection2);
					}
				}
			} else {
				DebugLog.log("SyncDoorKey: expected IsoDoor index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
			}
		} else if (square != null) {
			DebugLog.log("SyncDoorKey: index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
		} else {
			DebugLog.log("SyncDoorKey: sq is null x,y,z=" + int1 + "," + int2 + "," + int3);
		}
	}

	static void receiveSyncThumpable(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		int int4 = byteBuffer.getInt();
		byte byte2 = byteBuffer.get();
		int int5 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
			IsoObject object = (IsoObject)square.getObjects().get(byte1);
			if (object instanceof IsoThumpable) {
				IsoThumpable thumpable = (IsoThumpable)object;
				thumpable.lockedByCode = int4;
				thumpable.lockedByPadlock = byte2 == 1;
				thumpable.keyId = int5;
				for (int int6 = 0; int6 < udpEngine.connections.size(); ++int6) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int6);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.SyncThumpable.doPacket(byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						byteBufferWriter.putByte(byte1);
						byteBufferWriter.putInt(int4);
						byteBufferWriter.putByte(byte2);
						byteBufferWriter.putInt(int5);
						PacketTypes.PacketType.SyncThumpable.send(udpConnection2);
					}
				}
			} else {
				DebugLog.log("SyncThumpable: expected IsoThumpable index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
			}
		} else if (square != null) {
			DebugLog.log("SyncThumpable: index=" + byte1 + " is invalid x,y,z=" + int1 + "," + int2 + "," + int3);
		} else {
			DebugLog.log("SyncThumpable: sq is null x,y,z=" + int1 + "," + int2 + "," + int3);
		}
	}

	static void receiveRemoveItemFromSquare(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null && int4 >= 0 && int4 < square.getObjects().size()) {
			IsoObject object = (IsoObject)square.getObjects().get(int4);
			if (!(object instanceof IsoWorldInventoryObject)) {
				IsoRegions.setPreviousFlags(square);
			}

			DebugLog.log(DebugType.Objects, "object: removing " + object + " index=" + int4 + " " + int1 + "," + int2 + "," + int3);
			if (object instanceof IsoWorldInventoryObject) {
				LoggerManager.getLogger("item").write(udpConnection.idStr + " \"" + udpConnection.username + "\" floor -1 " + int1 + "," + int2 + "," + int3 + " [" + ((IsoWorldInventoryObject)object).getItem().getFullType() + "]");
			} else {
				String string = object.getName() != null ? object.getName() : object.getObjectName();
				if (object.getSprite() != null && object.getSprite().getName() != null) {
					string = string + " (" + object.getSprite().getName() + ")";
				}

				LoggerManager.getLogger("map").write(udpConnection.idStr + " \"" + udpConnection.username + "\" removed " + string + " at " + int1 + "," + int2 + "," + int3);
			}

			int int5;
			if (object.isTableSurface()) {
				for (int5 = int4 + 1; int5 < square.getObjects().size(); ++int5) {
					IsoObject object2 = (IsoObject)square.getObjects().get(int5);
					if (object2.isTableTopObject() || object2.isTableSurface()) {
						object2.setRenderYOffset(object2.getRenderYOffset() - object.getSurfaceOffset());
					}
				}
			}

			if (!(object instanceof IsoWorldInventoryObject)) {
				LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", object);
			}

			if (!square.getObjects().contains(object)) {
				throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
			}

			object.removeFromWorld();
			object.removeFromSquare();
			square.RecalcAllWithNeighbours(true);
			if (!(object instanceof IsoWorldInventoryObject)) {
				IsoWorld.instance.CurrentCell.checkHaveRoof(int1, int2);
				MapCollisionData.instance.squareChanged(square);
				PolygonalMap2.instance.squareChanged(square);
				ServerMap.instance.physicsCheck(int1, int2);
				IsoRegions.squareChanged(square, true);
				IsoGenerator.updateGenerator(square);
			}

			for (int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.RemoveItemFromSquare.doPacket(byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					byteBufferWriter.putInt(int4);
					PacketTypes.PacketType.RemoveItemFromSquare.send(udpConnection2);
				}
			}
		}
	}

	public static int RemoveItemFromMap(IsoObject object) {
		int int1 = object.getSquare().getX();
		int int2 = object.getSquare().getY();
		int int3 = object.getSquare().getZ();
		int int4 = object.getSquare().getObjects().indexOf(object);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null && !(object instanceof IsoWorldInventoryObject)) {
			IsoRegions.setPreviousFlags(square);
		}

		LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", object);
		if (!object.getSquare().getObjects().contains(object)) {
			throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
		} else {
			object.removeFromWorld();
			object.removeFromSquare();
			if (square != null) {
				square.RecalcAllWithNeighbours(true);
			}

			if (!(object instanceof IsoWorldInventoryObject)) {
				IsoWorld.instance.CurrentCell.checkHaveRoof(int1, int2);
				MapCollisionData.instance.squareChanged(square);
				PolygonalMap2.instance.squareChanged(square);
				ServerMap.instance.physicsCheck(int1, int2);
				IsoRegions.squareChanged(square, true);
				IsoGenerator.updateGenerator(square);
			}

			for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int5);
				if (udpConnection.RelevantTo((float)int1, (float)int2)) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.RemoveItemFromSquare.doPacket(byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					byteBufferWriter.putInt(int4);
					PacketTypes.PacketType.RemoveItemFromSquare.send(udpConnection);
				}
			}

			return int4;
		}
	}

	public static void sendBloodSplatter(HandWeapon handWeapon, float float1, float float2, float float3, Vector2 vector2, boolean boolean1, boolean boolean2) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.BloodSplatter.doPacket(byteBufferWriter);
			byteBufferWriter.putUTF(handWeapon != null ? handWeapon.getType() : "");
			byteBufferWriter.putFloat(float1);
			byteBufferWriter.putFloat(float2);
			byteBufferWriter.putFloat(float3);
			byteBufferWriter.putFloat(vector2.getX());
			byteBufferWriter.putFloat(vector2.getY());
			byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
			byteBufferWriter.putByte((byte)(boolean2 ? 1 : 0));
			byte byte1 = 0;
			if (handWeapon != null) {
				byte1 = (byte)Math.max(handWeapon.getSplatNumber(), 1);
			}

			byteBufferWriter.putByte(byte1);
			PacketTypes.PacketType.BloodSplatter.send(udpConnection);
		}
	}

	static void receiveAddItemToMap(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		IsoObject object = WorldItemTypes.createFromBuffer(byteBuffer);
		if (object instanceof IsoFire && ServerOptions.instance.NoFire.getValue()) {
			DebugLog.log("user \"" + udpConnection.username + "\" tried to start a fire");
		} else {
			object.loadFromRemoteBuffer(byteBuffer);
			if (object.square != null) {
				DebugLog.log(DebugType.Objects, "object: added " + object + " index=" + object.getObjectIndex() + " " + object.getX() + "," + object.getY() + "," + object.getZ());
				ZLogger zLogger;
				String string;
				if (object instanceof IsoWorldInventoryObject) {
					zLogger = LoggerManager.getLogger("item");
					string = udpConnection.idStr;
					zLogger.write(string + " \"" + udpConnection.username + "\" floor +1 " + (int)object.getX() + "," + (int)object.getY() + "," + (int)object.getZ() + " [" + ((IsoWorldInventoryObject)object).getItem().getFullType() + "]");
				} else {
					String string2 = object.getName() != null ? object.getName() : object.getObjectName();
					if (object.getSprite() != null && object.getSprite().getName() != null) {
						string2 = string2 + " (" + object.getSprite().getName() + ")";
					}

					zLogger = LoggerManager.getLogger("map");
					string = udpConnection.idStr;
					zLogger.write(string + " \"" + udpConnection.username + "\" added " + string2 + " at " + object.getX() + "," + object.getY() + "," + object.getZ());
				}

				object.addToWorld();
				object.square.RecalcProperties();
				if (!(object instanceof IsoWorldInventoryObject)) {
					object.square.restackSheetRope();
					IsoWorld.instance.CurrentCell.checkHaveRoof(object.square.getX(), object.square.getY());
					MapCollisionData.instance.squareChanged(object.square);
					PolygonalMap2.instance.squareChanged(object.square);
					ServerMap.instance.physicsCheck(object.square.x, object.square.y);
					IsoRegions.squareChanged(object.square);
					IsoGenerator.updateGenerator(object.square);
				}

				for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.RelevantTo((float)object.square.x, (float)object.square.y)) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.PacketType.AddItemToMap.doPacket(byteBufferWriter);
						object.writeToRemoteBuffer(byteBufferWriter);
						PacketTypes.PacketType.AddItemToMap.send(udpConnection2);
					}
				}

				if (!(object instanceof IsoWorldInventoryObject)) {
					LuaEventManager.triggerEvent("OnObjectAdded", object);
				} else {
					((IsoWorldInventoryObject)object).dropTime = GameTime.getInstance().getWorldAgeHours();
				}
			} else if (bDebug) {
				DebugLog.log("AddItemToMap: sq is null");
			}
		}
	}

	public static void disconnect(UdpConnection udpConnection, String string) {
		if (udpConnection.playerDownloadServer != null) {
			try {
				udpConnection.playerDownloadServer.destroy();
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			udpConnection.playerDownloadServer = null;
		}

		RequestDataManager.getInstance().disconnect(udpConnection);
		int int1;
		for (int1 = 0; int1 < 4; ++int1) {
			IsoPlayer player = udpConnection.players[int1];
			if (player != null) {
				ChatServer.getInstance().disconnectPlayer(udpConnection.playerIDs[int1]);
				disconnectPlayer(player, udpConnection);
			}

			udpConnection.usernames[int1] = null;
			udpConnection.players[int1] = null;
			udpConnection.playerIDs[int1] = -1;
			udpConnection.ReleventPos[int1] = null;
			udpConnection.connectArea[int1] = null;
		}

		for (int1 = 0; int1 < udpEngine.getMaxConnections(); ++int1) {
			if (SlotToConnection[int1] == udpConnection) {
				SlotToConnection[int1] = null;
			}
		}

		Iterator iterator = IDToAddressMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			if ((Long)entry.getValue() == udpConnection.getConnectedGUID()) {
				iterator.remove();
			}
		}

		if (!SteamUtils.isSteamModeEnabled()) {
			PublicServerUtil.updatePlayers();
		}

		if (CoopSlave.instance != null && udpConnection.isCoopHost) {
			DebugLog.log("Host user disconnected, stopping the server");
			ServerMap.instance.QueueQuit();
		}

		if (bServer) {
			ConnectionManager.log("disconnect", string, udpConnection);
		}
	}

	public static void addIncoming(short short1, ByteBuffer byteBuffer, UdpConnection udpConnection) {
		ZomboidNetData zomboidNetData = null;
		if (byteBuffer.limit() > 2048) {
			zomboidNetData = ZomboidNetDataPool.instance.getLong(byteBuffer.limit());
		} else {
			zomboidNetData = ZomboidNetDataPool.instance.get();
		}

		zomboidNetData.read(short1, byteBuffer, udpConnection);
		if (zomboidNetData.type == null) {
			try {
				if (ServerOptions.instance.AntiCheatProtectionType13.getValue() && PacketValidator.checkUser(udpConnection)) {
					PacketValidator.doKickUser(udpConnection, String.valueOf(short1), "Type13", (String)null);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			zomboidNetData.time = System.currentTimeMillis();
			if (zomboidNetData.type != PacketTypes.PacketType.PlayerUpdate && zomboidNetData.type != PacketTypes.PacketType.PlayerUpdateReliable) {
				if (zomboidNetData.type != PacketTypes.PacketType.VehiclesUnreliable && zomboidNetData.type != PacketTypes.PacketType.Vehicles) {
					MainLoopNetDataHighPriorityQ.add(zomboidNetData);
				} else {
					byte byte1 = zomboidNetData.buffer.get(0);
					if (byte1 == 9) {
						MainLoopNetDataQ.add(zomboidNetData);
					} else {
						MainLoopNetDataHighPriorityQ.add(zomboidNetData);
					}
				}
			} else {
				MainLoopPlayerUpdateQ.add(zomboidNetData);
			}
		}
	}

	public static void smashWindow(IsoWindow window, int int1) {
		for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int2);
			if (udpConnection.RelevantTo(window.getX(), window.getY())) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.SmashWindow.doPacket(byteBufferWriter);
				byteBufferWriter.putInt(window.square.getX());
				byteBufferWriter.putInt(window.square.getY());
				byteBufferWriter.putInt(window.square.getZ());
				byteBufferWriter.putByte((byte)window.square.getObjects().indexOf(window));
				byteBufferWriter.putByte((byte)int1);
				PacketTypes.PacketType.SmashWindow.send(udpConnection);
			}
		}
	}

	static void receiveHitCharacter(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			HitCharacterPacket hitCharacterPacket = HitCharacterPacket.process(byteBuffer);
			if (hitCharacterPacket != null) {
				hitCharacterPacket.parse(byteBuffer, udpConnection);
				if (hitCharacterPacket.isConsistent() && hitCharacterPacket.validate(udpConnection)) {
					DebugLog.Damage.trace(hitCharacterPacket.getDescription());
					sendHitCharacter(hitCharacterPacket, udpConnection);
					hitCharacterPacket.tryProcess();
				}
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveHitCharacter: failed", LogSeverity.Error);
		}
	}

	private static void sendHitCharacter(HitCharacterPacket hitCharacterPacket, UdpConnection udpConnection) {
		DebugLog.Damage.trace(hitCharacterPacket.getDescription());
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && hitCharacterPacket.isRelevant(udpConnection2)) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.HitCharacter.doPacket(byteBufferWriter);
				hitCharacterPacket.write(byteBufferWriter);
				PacketTypes.PacketType.HitCharacter.send(udpConnection2);
			}
		}
	}

	static void receiveZombieDeath(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			DeadZombiePacket deadZombiePacket = new DeadZombiePacket();
			deadZombiePacket.parse(byteBuffer, udpConnection);
			if (Core.bDebug) {
				DebugLog.Multiplayer.debugln("ReceiveZombieDeath: %s", deadZombiePacket.getDescription());
			}

			if (deadZombiePacket.isConsistent()) {
				if (deadZombiePacket.getZombie().isReanimatedPlayer()) {
					sendZombieDeath(deadZombiePacket.getZombie());
				} else {
					sendZombieDeath(deadZombiePacket);
				}

				deadZombiePacket.process();
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveZombieDeath: failed", LogSeverity.Error);
		}
	}

	public static void sendZombieDeath(IsoZombie zombie) {
		try {
			DeadZombiePacket deadZombiePacket = new DeadZombiePacket();
			deadZombiePacket.set(zombie);
			sendZombieDeath(deadZombiePacket);
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "SendZombieDeath: failed", LogSeverity.Error);
		}
	}

	private static void sendZombieDeath(DeadZombiePacket deadZombiePacket) {
		try {
			if (Core.bDebug) {
				DebugLog.Multiplayer.debugln("SendZombieDeath: %s", deadZombiePacket.getDescription());
			}

			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection.RelevantTo(deadZombiePacket.getZombie().getX(), deadZombiePacket.getZombie().getY())) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.ZombieDeath.doPacket(byteBufferWriter);
					try {
						deadZombiePacket.write(byteBufferWriter);
						PacketTypes.PacketType.ZombieDeath.send(udpConnection);
					} catch (Exception exception) {
						udpConnection.cancelPacket();
						DebugLog.Multiplayer.printException(exception, "SendZombieDeath: failed", LogSeverity.Error);
					}
				}
			}
		} catch (Exception exception2) {
			DebugLog.Multiplayer.printException(exception2, "SendZombieDeath: failed", LogSeverity.Error);
		}
	}

	static void receivePlayerDeath(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			DeadPlayerPacket deadPlayerPacket = new DeadPlayerPacket();
			deadPlayerPacket.parse(byteBuffer, udpConnection);
			if (Core.bDebug) {
				DebugLog.Multiplayer.debugln("ReceivePlayerDeath: %s", deadPlayerPacket.getDescription());
			}

			String string = deadPlayerPacket.getPlayer().username;
			ChatServer.getInstance().disconnectPlayer(deadPlayerPacket.getPlayer().getOnlineID());
			ServerWorldDatabase.instance.saveTransactionID(string, 0);
			deadPlayerPacket.getPlayer().setTransactionID(0);
			transactionIDMap.put(string, 0);
			SafetySystemManager.clearSafety(deadPlayerPacket.getPlayer());
			if (deadPlayerPacket.getPlayer().accessLevel.equals("") && !ServerOptions.instance.Open.getValue() && ServerOptions.instance.DropOffWhiteListAfterDeath.getValue()) {
				try {
					ServerWorldDatabase.instance.removeUser(string);
				} catch (SQLException sQLException) {
					DebugLog.Multiplayer.printException(sQLException, "ReceivePlayerDeath: db failed", LogSeverity.Warning);
				}
			}

			if (deadPlayerPacket.isConsistent()) {
				deadPlayerPacket.id = deadPlayerPacket.getPlayer().getOnlineID();
				sendPlayerDeath(deadPlayerPacket, udpConnection);
				deadPlayerPacket.process();
			}

			deadPlayerPacket.getPlayer().setStateMachineLocked(true);
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceivePlayerDeath: failed", LogSeverity.Error);
		}
	}

	public static void sendPlayerDeath(DeadPlayerPacket deadPlayerPacket, UdpConnection udpConnection) {
		if (Core.bDebug) {
			DebugLog.Multiplayer.debugln("SendPlayerDeath: %s", deadPlayerPacket.getDescription());
		}

		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection == null || udpConnection.getConnectedGUID() != udpConnection2.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.PlayerDeath.doPacket(byteBufferWriter);
				deadPlayerPacket.write(byteBufferWriter);
				PacketTypes.PacketType.PlayerDeath.send(udpConnection2);
			}
		}
	}

	static void receivePlayerDamage(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			short short2 = byteBuffer.getShort();
			float float1 = byteBuffer.getFloat();
			IsoPlayer player = getPlayerFromConnection(udpConnection, short2);
			if (player != null) {
				player.getBodyDamage().load(byteBuffer, IsoWorld.getWorldVersion());
				player.getStats().setPain(float1);
				if (Core.bDebug) {
					DebugLog.Multiplayer.debugln("ReceivePlayerDamage: \"%s\" %f", player.getUsername(), player.getBodyDamage().getOverallBodyHealth());
				}

				sendPlayerDamage(player, udpConnection);
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceivePlayerDamage: failed", LogSeverity.Error);
		}
	}

	public static void sendPlayerDamage(IsoPlayer player, UdpConnection udpConnection) {
		if (Core.bDebug) {
			DebugLog.Multiplayer.debugln("SendPlayerDamage: \"%s\" %f", player.getUsername(), player.getBodyDamage().getOverallBodyHealth());
		}

		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection.getConnectedGUID() != udpConnection2.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.PlayerDamage.doPacket(byteBufferWriter);
				try {
					byteBufferWriter.putShort(player.getOnlineID());
					byteBufferWriter.putFloat(player.getStats().getPain());
					player.getBodyDamage().save(byteBufferWriter.bb);
					PacketTypes.PacketType.PlayerDamage.send(udpConnection2);
				} catch (Exception exception) {
					udpConnection2.cancelPacket();
					DebugLog.Multiplayer.printException(exception, "SendPlayerDamage: failed", LogSeverity.Error);
				}
			}
		}
	}

	static void receiveSyncInjuries(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			SyncInjuriesPacket syncInjuriesPacket = new SyncInjuriesPacket();
			syncInjuriesPacket.parse(byteBuffer, udpConnection);
			DebugLog.Damage.trace(syncInjuriesPacket.getDescription());
			if (syncInjuriesPacket.process()) {
				syncInjuriesPacket.id = syncInjuriesPacket.player.getOnlineID();
				sendPlayerInjuries(udpConnection, syncInjuriesPacket);
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceivePlayerInjuries: failed", LogSeverity.Error);
		}
	}

	private static void sendPlayerInjuries(UdpConnection udpConnection, SyncInjuriesPacket syncInjuriesPacket) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.SyncInjuries.doPacket(byteBufferWriter);
		syncInjuriesPacket.write(byteBufferWriter);
		PacketTypes.PacketType.SyncInjuries.send(udpConnection);
	}

	static void receiveKeepAlive(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		MPDebugInfo.instance.serverPacket(byteBuffer, udpConnection);
	}

	static void receiveRemoveCorpseFromMap(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		RemoveCorpseFromMap removeCorpseFromMap = new RemoveCorpseFromMap();
		removeCorpseFromMap.parse(byteBuffer, udpConnection);
		if (removeCorpseFromMap.isConsistent()) {
			removeCorpseFromMap.process();
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && removeCorpseFromMap.isRelevant(udpConnection2)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.RemoveCorpseFromMap.doPacket(byteBufferWriter);
					removeCorpseFromMap.write(byteBufferWriter);
					PacketTypes.PacketType.RemoveCorpseFromMap.send(udpConnection2);
				}
			}
		}
	}

	public static void sendRemoveCorpseFromMap(IsoDeadBody deadBody) {
		RemoveCorpseFromMap removeCorpseFromMap = new RemoveCorpseFromMap();
		removeCorpseFromMap.set(deadBody);
		DebugLog.Death.trace(removeCorpseFromMap.getDescription());
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.RemoveCorpseFromMap.doPacket(byteBufferWriter);
			removeCorpseFromMap.write(byteBufferWriter);
			PacketTypes.PacketType.RemoveCorpseFromMap.send(udpConnection);
		}
	}

	static void receiveEventPacket(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			EventPacket eventPacket = new EventPacket();
			eventPacket.parse(byteBuffer, udpConnection);
			Iterator iterator = udpEngine.connections.iterator();
			while (iterator.hasNext()) {
				UdpConnection udpConnection2 = (UdpConnection)iterator.next();
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && eventPacket.isRelevant(udpConnection2)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.EventPacket.doPacket(byteBufferWriter);
					eventPacket.write(byteBufferWriter);
					PacketTypes.PacketType.EventPacket.send(udpConnection2);
				}
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveEvent: failed", LogSeverity.Error);
		}
	}

	static void receiveActionPacket(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			ActionPacket actionPacket = new ActionPacket();
			actionPacket.parse(byteBuffer, udpConnection);
			Iterator iterator = udpEngine.connections.iterator();
			while (iterator.hasNext()) {
				UdpConnection udpConnection2 = (UdpConnection)iterator.next();
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && actionPacket.isRelevant(udpConnection2)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.ActionPacket.doPacket(byteBufferWriter);
					actionPacket.write(byteBufferWriter);
					PacketTypes.PacketType.ActionPacket.send(udpConnection2);
				}
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveAction: failed", LogSeverity.Error);
		}
	}

	static void receiveKillZombie(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			short short2 = byteBuffer.getShort();
			boolean boolean1 = byteBuffer.get() != 0;
			DebugLog.Death.trace("id=%d, isFallOnFront=%b", short2, boolean1);
			IsoZombie zombie = (IsoZombie)ServerMap.instance.ZombieMap.get(short2);
			if (zombie != null) {
				zombie.setFallOnFront(boolean1);
				zombie.becomeCorpse();
			} else {
				DebugLog.Multiplayer.error("ReceiveKillZombie: zombie not found");
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveKillZombie: failed", LogSeverity.Error);
		}
	}

	public static void receiveEatBody(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, "ReceiveEatBody");
			}

			short short2 = byteBuffer.getShort();
			IsoZombie zombie = (IsoZombie)ServerMap.instance.ZombieMap.get(short2);
			if (zombie == null) {
				DebugLog.Multiplayer.error("ReceiveEatBody: zombie " + short2 + " not found");
				return;
			}

			Iterator iterator = udpEngine.connections.iterator();
			while (iterator.hasNext()) {
				UdpConnection udpConnection2 = (UdpConnection)iterator.next();
				if (udpConnection2.RelevantTo(zombie.x, zombie.y)) {
					if (Core.bDebug) {
						DebugLog.log(DebugType.Multiplayer, "SendEatBody");
					}

					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.EatBody.doPacket(byteBufferWriter);
					byteBuffer.position(0);
					byteBufferWriter.bb.put(byteBuffer);
					PacketTypes.PacketType.EatBody.send(udpConnection2);
				}
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveEatBody: failed", LogSeverity.Error);
		}
	}

	public static void receiveSyncRadioData(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			boolean boolean1 = byteBuffer.get() == 1;
			int int1 = byteBuffer.getInt();
			int[] intArray = new int[int1];
			for (int int2 = 0; int2 < int1; ++int2) {
				intArray[int2] = byteBuffer.getInt();
			}

			RakVoice.SetChannelsRouting(udpConnection.getConnectedGUID(), boolean1, intArray, (short)int1);
			Iterator iterator = udpEngine.connections.iterator();
			while (iterator.hasNext()) {
				UdpConnection udpConnection2 = (UdpConnection)iterator.next();
				if (udpConnection2 != udpConnection && udpConnection.players[0] != null) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.SyncRadioData.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(udpConnection.players[0].OnlineID);
					byteBuffer.position(0);
					byteBufferWriter.bb.put(byteBuffer);
					PacketTypes.PacketType.SyncRadioData.send(udpConnection2);
				}
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "SyncRadioData: failed", LogSeverity.Error);
		}
	}

	public static void receiveThump(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, "ReceiveThump");
			}

			short short2 = byteBuffer.getShort();
			IsoZombie zombie = (IsoZombie)ServerMap.instance.ZombieMap.get(short2);
			if (zombie == null) {
				DebugLog.Multiplayer.error("ReceiveThump: zombie " + short2 + " not found");
				return;
			}

			Iterator iterator = udpEngine.connections.iterator();
			while (iterator.hasNext()) {
				UdpConnection udpConnection2 = (UdpConnection)iterator.next();
				if (udpConnection2.RelevantTo(zombie.x, zombie.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.Thump.doPacket(byteBufferWriter);
					byteBuffer.position(0);
					byteBufferWriter.bb.put(byteBuffer);
					PacketTypes.PacketType.Thump.send(udpConnection2);
				}
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveEatBody: failed", LogSeverity.Error);
		}
	}

	public static void sendWorldSound(UdpConnection udpConnection, WorldSoundManager.WorldSound worldSound) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.WorldSound.doPacket(byteBufferWriter);
		try {
			byteBufferWriter.putInt(worldSound.x);
			byteBufferWriter.putInt(worldSound.y);
			byteBufferWriter.putInt(worldSound.z);
			byteBufferWriter.putInt(worldSound.radius);
			byteBufferWriter.putInt(worldSound.volume);
			byteBufferWriter.putByte((byte)(worldSound.stresshumans ? 1 : 0));
			byteBufferWriter.putFloat(worldSound.zombieIgnoreDist);
			byteBufferWriter.putFloat(worldSound.stressMod);
			byteBufferWriter.putByte((byte)(worldSound.sourceIsZombie ? 1 : 0));
			PacketTypes.PacketType.WorldSound.send(udpConnection);
		} catch (Exception exception) {
			DebugLog.Sound.printException(exception, "SendWorldSound: failed", LogSeverity.Error);
			udpConnection.cancelPacket();
		}
	}

	public static void sendWorldSound(WorldSoundManager.WorldSound worldSound, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if ((udpConnection == null || udpConnection.getConnectedGUID() != udpConnection2.getConnectedGUID()) && udpConnection2.isFullyConnected()) {
				IsoPlayer player = getAnyPlayerFromConnection(udpConnection2);
				if (player != null && udpConnection2.RelevantTo((float)worldSound.x, (float)worldSound.y, (float)worldSound.radius)) {
					sendWorldSound(udpConnection2, worldSound);
				}
			}
		}
	}

	static void receiveWorldSound(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		int int5 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		boolean boolean2 = byteBuffer.get() == 1;
		DebugLog.Sound.noise("x=%d y=%d z=%d, radius=%d", int1, int2, int3, int4);
		WorldSoundManager.WorldSound worldSound = WorldSoundManager.instance.addSound((Object)null, int1, int2, int3, int4, int5, boolean1, float1, float2, boolean2, false, true);
		if (worldSound != null) {
			sendWorldSound(worldSound, udpConnection);
		}
	}

	public static void kick(UdpConnection udpConnection, String string, String string2) {
		DebugLog.General.warn("The player " + udpConnection.username + " was kicked. The reason was " + string + ", " + string2);
		ConnectionManager.log("kick", string2, udpConnection);
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		try {
			PacketTypes.PacketType.Kicked.doPacket(byteBufferWriter);
			byteBufferWriter.putUTF(string);
			byteBufferWriter.putUTF(string2);
			PacketTypes.PacketType.Kicked.send(udpConnection);
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "Kick: failed", LogSeverity.Error);
			udpConnection.cancelPacket();
		}
	}

	private static void sendStartRain(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.StartRain.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(RainManager.randRainMin);
		byteBufferWriter.putInt(RainManager.randRainMax);
		byteBufferWriter.putFloat(RainManager.RainDesiredIntensity);
		PacketTypes.PacketType.StartRain.send(udpConnection);
	}

	public static void startRain() {
		if (udpEngine != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
				sendStartRain(udpConnection);
			}
		}
	}

	private static void sendStopRain(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.StopRain.doPacket(byteBufferWriter);
		PacketTypes.PacketType.StopRain.send(udpConnection);
	}

	public static void stopRain() {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			sendStopRain(udpConnection);
		}
	}

	private static void sendWeather(UdpConnection udpConnection) {
		GameTime gameTime = GameTime.getInstance();
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.Weather.doPacket(byteBufferWriter);
		byteBufferWriter.putByte((byte)gameTime.getDawn());
		byteBufferWriter.putByte((byte)gameTime.getDusk());
		byteBufferWriter.putByte((byte)(gameTime.isThunderDay() ? 1 : 0));
		byteBufferWriter.putFloat(gameTime.Moon);
		byteBufferWriter.putFloat(gameTime.getAmbientMin());
		byteBufferWriter.putFloat(gameTime.getAmbientMax());
		byteBufferWriter.putFloat(gameTime.getViewDistMin());
		byteBufferWriter.putFloat(gameTime.getViewDistMax());
		byteBufferWriter.putFloat(IsoWorld.instance.getGlobalTemperature());
		byteBufferWriter.putUTF(IsoWorld.instance.getWeather());
		ErosionMain.getInstance().sendState(byteBufferWriter.bb);
		PacketTypes.PacketType.Weather.send(udpConnection);
	}

	public static void sendWeather() {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			sendWeather(udpConnection);
		}
	}

	private static boolean isInSameFaction(IsoPlayer player, IsoPlayer player2) {
		Faction faction = Faction.getPlayerFaction(player);
		Faction faction2 = Faction.getPlayerFaction(player2);
		return faction != null && faction == faction2;
	}

	private static boolean isInSameSafehouse(IsoPlayer player, IsoPlayer player2) {
		ArrayList arrayList = SafeHouse.getSafehouseList();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			SafeHouse safeHouse = (SafeHouse)arrayList.get(int1);
			if (safeHouse.playerAllowed(player.getUsername()) && safeHouse.playerAllowed(player2.getUsername())) {
				return true;
			}
		}

		return false;
	}

	private static boolean isAnyPlayerInSameFaction(UdpConnection udpConnection, IsoPlayer player) {
		for (int int1 = 0; int1 < 4; ++int1) {
			IsoPlayer player2 = udpConnection.players[int1];
			if (player2 != null && isInSameFaction(player2, player)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isAnyPlayerInSameSafehouse(UdpConnection udpConnection, IsoPlayer player) {
		for (int int1 = 0; int1 < 4; ++int1) {
			IsoPlayer player2 = udpConnection.players[int1];
			if (player2 != null && isInSameSafehouse(player2, player)) {
				return true;
			}
		}

		return false;
	}

	private static boolean shouldSendWorldMapPlayerPosition(UdpConnection udpConnection, IsoPlayer player) {
		if (player != null && !player.isDead()) {
			UdpConnection udpConnection2 = getConnectionFromPlayer(player);
			if (udpConnection2 != null && udpConnection2 != udpConnection && udpConnection2.isFullyConnected()) {
				if (udpConnection.accessLevel > 1) {
					return true;
				} else {
					int int1 = ServerOptions.getInstance().MapRemotePlayerVisibility.getValue();
					if (int1 == 2) {
						return isAnyPlayerInSameFaction(udpConnection, player) || isAnyPlayerInSameSafehouse(udpConnection, player);
					} else {
						return true;
					}
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private static void sendWorldMapPlayerPosition(UdpConnection udpConnection) {
		tempPlayers.clear();
		for (int int1 = 0; int1 < Players.size(); ++int1) {
			IsoPlayer player = (IsoPlayer)Players.get(int1);
			if (shouldSendWorldMapPlayerPosition(udpConnection, player)) {
				tempPlayers.add(player);
			}
		}

		if (!tempPlayers.isEmpty()) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.WorldMapPlayerPosition.doPacket(byteBufferWriter);
			byteBufferWriter.putBoolean(false);
			byteBufferWriter.putShort((short)tempPlayers.size());
			for (int int2 = 0; int2 < tempPlayers.size(); ++int2) {
				IsoPlayer player2 = (IsoPlayer)tempPlayers.get(int2);
				WorldMapRemotePlayer worldMapRemotePlayer = WorldMapRemotePlayers.instance.getOrCreatePlayer(player2);
				worldMapRemotePlayer.setPlayer(player2);
				byteBufferWriter.putShort(worldMapRemotePlayer.getOnlineID());
				byteBufferWriter.putShort(worldMapRemotePlayer.getChangeCount());
				byteBufferWriter.putFloat(worldMapRemotePlayer.getX());
				byteBufferWriter.putFloat(worldMapRemotePlayer.getY());
			}

			PacketTypes.PacketType.WorldMapPlayerPosition.send(udpConnection);
		}
	}

	public static void sendWorldMapPlayerPosition() {
		int int1 = ServerOptions.getInstance().MapRemotePlayerVisibility.getValue();
		for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int2);
			if (int1 != 1 || udpConnection.accessLevel != 1) {
				sendWorldMapPlayerPosition(udpConnection);
			}
		}
	}

	public static void receiveWorldMapPlayerPosition(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		tempPlayers.clear();
		IsoPlayer player;
		for (int int1 = 0; int1 < short2; ++int1) {
			short short3 = byteBuffer.getShort();
			player = (IsoPlayer)IDToPlayerMap.get(short3);
			if (player != null && shouldSendWorldMapPlayerPosition(udpConnection, player)) {
				tempPlayers.add(player);
			}
		}

		if (!tempPlayers.isEmpty()) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.WorldMapPlayerPosition.doPacket(byteBufferWriter);
			byteBufferWriter.putBoolean(true);
			byteBufferWriter.putShort((short)tempPlayers.size());
			for (int int2 = 0; int2 < tempPlayers.size(); ++int2) {
				player = (IsoPlayer)tempPlayers.get(int2);
				WorldMapRemotePlayer worldMapRemotePlayer = WorldMapRemotePlayers.instance.getOrCreatePlayer(player);
				worldMapRemotePlayer.setPlayer(player);
				byteBufferWriter.putShort(worldMapRemotePlayer.getOnlineID());
				byteBufferWriter.putShort(worldMapRemotePlayer.getChangeCount());
				byteBufferWriter.putUTF(worldMapRemotePlayer.getUsername());
				byteBufferWriter.putUTF(worldMapRemotePlayer.getForename());
				byteBufferWriter.putUTF(worldMapRemotePlayer.getSurname());
				byteBufferWriter.putUTF(worldMapRemotePlayer.getAccessLevel());
				byteBufferWriter.putFloat(worldMapRemotePlayer.getX());
				byteBufferWriter.putFloat(worldMapRemotePlayer.getY());
				byteBufferWriter.putBoolean(worldMapRemotePlayer.isInvisible());
			}

			PacketTypes.PacketType.WorldMapPlayerPosition.send(udpConnection);
		}
	}

	private static void syncClock(UdpConnection udpConnection) {
		GameTime gameTime = GameTime.getInstance();
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.SyncClock.doPacket(byteBufferWriter);
		byteBufferWriter.putBoolean(bFastForward);
		byteBufferWriter.putFloat(gameTime.getTimeOfDay());
		byteBufferWriter.putInt(gameTime.getNightsSurvived());
		PacketTypes.PacketType.SyncClock.send(udpConnection);
	}

	public static void syncClock() {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			syncClock(udpConnection);
		}
	}

	public static void sendServerCommand(String string, String string2, KahluaTable kahluaTable, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.ClientCommand.doPacket(byteBufferWriter);
		byteBufferWriter.putUTF(string);
		byteBufferWriter.putUTF(string2);
		if (kahluaTable != null && !kahluaTable.isEmpty()) {
			byteBufferWriter.putByte((byte)1);
			try {
				KahluaTableIterator kahluaTableIterator = kahluaTable.iterator();
				while (kahluaTableIterator.advance()) {
					if (!TableNetworkUtils.canSave(kahluaTableIterator.getKey(), kahluaTableIterator.getValue())) {
						Object object = kahluaTableIterator.getKey();
						DebugLog.log("ERROR: sendServerCommand: can\'t save key,value=" + object + "," + kahluaTableIterator.getValue());
					}
				}

				TableNetworkUtils.save(kahluaTable, byteBufferWriter.bb);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else {
			byteBufferWriter.putByte((byte)0);
		}

		PacketTypes.PacketType.ClientCommand.send(udpConnection);
	}

	public static void sendServerCommand(String string, String string2, KahluaTable kahluaTable) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			sendServerCommand(string, string2, kahluaTable, udpConnection);
		}
	}

	public static void sendServerCommandV(String string, String string2, Object[] objectArray) {
		if (objectArray.length == 0) {
			sendServerCommand(string, string2, (KahluaTable)null);
		} else if (objectArray.length % 2 != 0) {
			DebugLog.log("ERROR: sendServerCommand called with invalid number of arguments (" + string + " " + string2 + ")");
		} else {
			KahluaTable kahluaTable = LuaManager.platform.newTable();
			for (int int1 = 0; int1 < objectArray.length; int1 += 2) {
				Object object = objectArray[int1 + 1];
				if (object instanceof Float) {
					kahluaTable.rawset(objectArray[int1], ((Float)object).doubleValue());
				} else if (object instanceof Integer) {
					kahluaTable.rawset(objectArray[int1], ((Integer)object).doubleValue());
				} else if (object instanceof Short) {
					kahluaTable.rawset(objectArray[int1], ((Short)object).doubleValue());
				} else {
					kahluaTable.rawset(objectArray[int1], object);
				}
			}

			sendServerCommand(string, string2, kahluaTable);
		}
	}

	public static void sendServerCommand(IsoPlayer player, String string, String string2, KahluaTable kahluaTable) {
		if (PlayerToAddressMap.containsKey(player)) {
			long long1 = (Long)PlayerToAddressMap.get(player);
			UdpConnection udpConnection = udpEngine.getActiveConnection(long1);
			if (udpConnection != null) {
				sendServerCommand(string, string2, kahluaTable, udpConnection);
			}
		}
	}

	public static ArrayList getPlayers(ArrayList arrayList) {
		arrayList.clear();
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			for (int int2 = 0; int2 < 4; ++int2) {
				IsoPlayer player = udpConnection.players[int2];
				if (player != null && player.OnlineID != -1) {
					arrayList.add(player);
				}
			}
		}

		return arrayList;
	}

	public static ArrayList getPlayers() {
		ArrayList arrayList = new ArrayList();
		return getPlayers(arrayList);
	}

	public static int getPlayerCount() {
		int int1 = 0;
		for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int2);
			for (int int3 = 0; int3 < 4; ++int3) {
				if (udpConnection.playerIDs[int3] != -1) {
					++int1;
				}
			}
		}

		return int1;
	}

	public static void sendAmbient(String string, int int1, int int2, int int3, float float1) {
		DebugLog.log(DebugType.Sound, "ambient: sending " + string + " at " + int1 + "," + int2 + " radius=" + int3);
		for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int4);
			IsoPlayer player = getAnyPlayerFromConnection(udpConnection);
			if (player != null) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.AddAmbient.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putInt(int3);
				byteBufferWriter.putFloat(float1);
				PacketTypes.PacketType.AddAmbient.send(udpConnection);
			}
		}
	}

	static void receiveChangeSafety(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			SafetyPacket safetyPacket = new SafetyPacket();
			safetyPacket.parse(byteBuffer, udpConnection);
			safetyPacket.log(udpConnection, "ReceiveChangeSafety");
			safetyPacket.process();
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveZombieDeath: failed", LogSeverity.Error);
		}
	}

	public static void sendChangeSafety(Safety safety) {
		try {
			SafetyPacket safetyPacket = new SafetyPacket(safety);
			safetyPacket.log((UdpConnection)null, "SendChangeSafety");
			Iterator iterator = udpEngine.connections.iterator();
			while (iterator.hasNext()) {
				UdpConnection udpConnection = (UdpConnection)iterator.next();
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.ChangeSafety.doPacket(byteBufferWriter);
				safetyPacket.write(byteBufferWriter);
				PacketTypes.PacketType.ChangeSafety.send(udpConnection);
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "SendChangeSafety: failed", LogSeverity.Error);
		}
	}

	static void receivePing(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		udpConnection.ping = true;
		answerPing(byteBuffer, udpConnection);
	}

	public static void updateOverlayForClients(IsoObject object, String string, float float1, float float2, float float3, float float4, UdpConnection udpConnection) {
		if (udpEngine != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2 != null && object.square != null && udpConnection2.RelevantTo((float)object.square.x, (float)object.square.y) && (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID())) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.UpdateOverlaySprite.doPacket(byteBufferWriter);
					GameWindow.WriteStringUTF(byteBufferWriter.bb, string);
					byteBufferWriter.putInt(object.getSquare().getX());
					byteBufferWriter.putInt(object.getSquare().getY());
					byteBufferWriter.putInt(object.getSquare().getZ());
					byteBufferWriter.putFloat(float1);
					byteBufferWriter.putFloat(float2);
					byteBufferWriter.putFloat(float3);
					byteBufferWriter.putFloat(float4);
					byteBufferWriter.putInt(object.getSquare().getObjects().indexOf(object));
					PacketTypes.PacketType.UpdateOverlaySprite.send(udpConnection2);
				}
			}
		}
	}

	static void receiveUpdateOverlaySprite(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		String string = GameWindow.ReadStringUTF(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		float float4 = byteBuffer.getFloat();
		int int4 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null && int4 < square.getObjects().size()) {
			try {
				IsoObject object = (IsoObject)square.getObjects().get(int4);
				if (object != null && object.setOverlaySprite(string, float1, float2, float3, float4, false)) {
					updateOverlayForClients(object, string, float1, float2, float3, float4, udpConnection);
				}
			} catch (Exception exception) {
			}
		}
	}

	public static void sendReanimatedZombieID(IsoPlayer player, IsoZombie zombie) {
		if (PlayerToAddressMap.containsKey(player)) {
			sendObjectChange(player, "reanimatedID", new Object[]{"ID", (double)zombie.OnlineID});
		}
	}

	static void receiveSyncSafehouse(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		SyncSafehousePacket syncSafehousePacket = new SyncSafehousePacket();
		syncSafehousePacket.parse(byteBuffer, udpConnection);
		if (syncSafehousePacket.validate(udpConnection)) {
			syncSafehousePacket.process();
			sendSafehouse(syncSafehousePacket, udpConnection);
			if (ChatServer.isInited()) {
				if (syncSafehousePacket.shouldCreateChat) {
					ChatServer.getInstance().createSafehouseChat(syncSafehousePacket.safehouse.getId());
				}

				if (syncSafehousePacket.remove) {
					ChatServer.getInstance().removeSafehouseChat(syncSafehousePacket.safehouse.getId());
				} else {
					ChatServer.getInstance().syncSafehouseChatMembers(syncSafehousePacket.safehouse.getId(), syncSafehousePacket.ownerUsername, syncSafehousePacket.safehouse.getPlayers());
				}
			}
		}
	}

	public static void receiveKickOutOfSafehouse(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		try {
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getShort());
			if (player == null) {
				return;
			}

			IsoPlayer player2 = udpConnection.players[0];
			if (player2 == null) {
				return;
			}

			SafeHouse safeHouse = SafeHouse.hasSafehouse(player2);
			if (safeHouse == null) {
				return;
			}

			if (!safeHouse.isOwner(player2)) {
				return;
			}

			if (!safeHouse.playerAllowed(player)) {
				return;
			}

			UdpConnection udpConnection2 = getConnectionFromPlayer(player);
			if (udpConnection2 == null) {
				return;
			}

			ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
			PacketTypes.PacketType.KickOutOfSafehouse.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			byteBufferWriter.putFloat((float)(safeHouse.getX() - 1));
			byteBufferWriter.putFloat((float)(safeHouse.getY() - 1));
			byteBufferWriter.putFloat(0.0F);
			PacketTypes.PacketType.KickOutOfSafehouse.send(udpConnection2);
			if (player.getNetworkCharacterAI() != null) {
				player.getNetworkCharacterAI().resetSpeedLimiter();
			}

			if (player.isAsleep()) {
				player.setAsleep(false);
				player.setAsleepTime(0.0F);
				sendWakeUpPlayer(player, (UdpConnection)null);
			}
		} catch (Exception exception) {
			DebugLog.Multiplayer.printException(exception, "ReceiveKickOutOfSafehouse: failed", LogSeverity.Error);
		}
	}

	public static void sendSafehouse(SyncSafehousePacket syncSafehousePacket, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.SyncSafehouse.doPacket(byteBufferWriter);
				syncSafehousePacket.write(byteBufferWriter);
				PacketTypes.PacketType.SyncSafehouse.send(udpConnection2);
			}
		}
	}

	public static void receiveRadioServerData(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.RadioServerData.doPacket(byteBufferWriter);
		ZomboidRadio.getInstance().WriteRadioServerDataPacket(byteBufferWriter);
		PacketTypes.PacketType.RadioServerData.send(udpConnection);
	}

	public static void receiveRadioDeviceDataState(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		if (byte1 == 1) {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			int int4 = byteBuffer.getInt();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square != null && int4 >= 0 && int4 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(int4);
				if (object instanceof IsoWaveSignal) {
					DeviceData deviceData = ((IsoWaveSignal)object).getDeviceData();
					if (deviceData != null) {
						try {
							deviceData.receiveDeviceDataStatePacket(byteBuffer, (UdpConnection)null);
						} catch (Exception exception) {
							System.out.print(exception.getMessage());
						}
					}
				}
			}
		} else if (byte1 == 0) {
			byte byte2 = byteBuffer.get();
			IsoPlayer player = getPlayerFromConnection(udpConnection, byte2);
			byte byte3 = byteBuffer.get();
			if (player != null) {
				Radio radio = null;
				if (byte3 == 1 && player.getPrimaryHandItem() instanceof Radio) {
					radio = (Radio)player.getPrimaryHandItem();
				}

				if (byte3 == 2 && player.getSecondaryHandItem() instanceof Radio) {
					radio = (Radio)player.getSecondaryHandItem();
				}

				if (radio != null && radio.getDeviceData() != null) {
					try {
						radio.getDeviceData().receiveDeviceDataStatePacket(byteBuffer, udpConnection);
					} catch (Exception exception2) {
						System.out.print(exception2.getMessage());
					}
				}
			}
		} else if (byte1 == 2) {
			short short2 = byteBuffer.getShort();
			short short3 = byteBuffer.getShort();
			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
			if (baseVehicle != null) {
				VehiclePart vehiclePart = baseVehicle.getPartByIndex(short3);
				if (vehiclePart != null) {
					DeviceData deviceData2 = vehiclePart.getDeviceData();
					if (deviceData2 != null) {
						try {
							deviceData2.receiveDeviceDataStatePacket(byteBuffer, (UdpConnection)null);
						} catch (Exception exception3) {
							System.out.print(exception3.getMessage());
						}
					}
				}
			}
		}
	}

	public static void sendIsoWaveSignal(long long1, int int1, int int2, int int3, String string, String string2, String string3, float float1, float float2, float float3, int int4, boolean boolean1) {
		WaveSignal waveSignal = new WaveSignal();
		waveSignal.set(int1, int2, int3, string, string2, string3, float1, float2, float3, int4, boolean1);
		for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int5);
			if (long1 != udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.WaveSignal.doPacket(byteBufferWriter);
				waveSignal.write(byteBufferWriter);
				PacketTypes.PacketType.WaveSignal.send(udpConnection);
			}
		}
	}

	public static void receiveWaveSignal(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		WaveSignal waveSignal = new WaveSignal();
		waveSignal.parse(byteBuffer, udpConnection);
		waveSignal.process(udpConnection);
	}

	public static void receivePlayerListensChannel(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		boolean boolean2 = byteBuffer.get() == 1;
		ZomboidRadio.getInstance().PlayerListensChannel(int1, boolean1, boolean2);
	}

	public static void sendAlarm(int int1, int int2) {
		DebugLog.log(DebugType.Multiplayer, "SendAlarm at [ " + int1 + " , " + int2 + " ]");
		for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int3);
			IsoPlayer player = getAnyPlayerFromConnection(udpConnection);
			if (player != null) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.AddAlarm.doPacket(byteBufferWriter);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				PacketTypes.PacketType.AddAlarm.send(udpConnection);
			}
		}
	}

	public static boolean isSpawnBuilding(BuildingDef buildingDef) {
		return SpawnPoints.instance.isSpawnBuilding(buildingDef);
	}

	private static void setFastForward(boolean boolean1) {
		if (boolean1 != bFastForward) {
			bFastForward = boolean1;
			syncClock();
		}
	}

	static void receiveSendCustomColor(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		float float4 = byteBuffer.getFloat();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null && int4 < square.getObjects().size()) {
			IsoObject object = (IsoObject)square.getObjects().get(int4);
			if (object != null) {
				object.setCustomColor(float1, float2, float3, float4);
			}
		}

		for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
			if (udpConnection2.RelevantTo((float)int1, (float)int2) && (udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() || udpConnection == null)) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.SendCustomColor.doPacket(byteBufferWriter);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putInt(int3);
				byteBufferWriter.putInt(int4);
				byteBufferWriter.putFloat(float1);
				byteBufferWriter.putFloat(float2);
				byteBufferWriter.putFloat(float3);
				byteBufferWriter.putFloat(float4);
				PacketTypes.PacketType.SendCustomColor.send(udpConnection2);
			}
		}
	}

	static void receiveSyncFurnace(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square == null) {
			DebugLog.log("receiveFurnaceChange: square is null x,y,z=" + int1 + "," + int2 + "," + int3);
		} else {
			BSFurnace bSFurnace = null;
			for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
				if (square.getObjects().get(int4) instanceof BSFurnace) {
					bSFurnace = (BSFurnace)square.getObjects().get(int4);
					break;
				}
			}

			if (bSFurnace == null) {
				DebugLog.log("receiveFurnaceChange: furnace is null x,y,z=" + int1 + "," + int2 + "," + int3);
			} else {
				bSFurnace.fireStarted = byteBuffer.get() == 1;
				bSFurnace.fuelAmount = byteBuffer.getFloat();
				bSFurnace.fuelDecrease = byteBuffer.getFloat();
				bSFurnace.heat = byteBuffer.getFloat();
				bSFurnace.sSprite = GameWindow.ReadString(byteBuffer);
				bSFurnace.sLitSprite = GameWindow.ReadString(byteBuffer);
				sendFuranceChange(bSFurnace, udpConnection);
			}
		}
	}

	static void receiveVehicles(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		VehicleManager.instance.serverPacket(byteBuffer, udpConnection, short1);
	}

	static void receiveTimeSync(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		GameTime.receiveTimeSync(byteBuffer, udpConnection);
	}

	public static void sendFuranceChange(BSFurnace bSFurnace, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.RelevantTo((float)bSFurnace.square.x, (float)bSFurnace.square.y) && (udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() || udpConnection == null)) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.SyncFurnace.doPacket(byteBufferWriter);
				byteBufferWriter.putInt(bSFurnace.square.x);
				byteBufferWriter.putInt(bSFurnace.square.y);
				byteBufferWriter.putInt(bSFurnace.square.z);
				byteBufferWriter.putByte((byte)(bSFurnace.isFireStarted() ? 1 : 0));
				byteBufferWriter.putFloat(bSFurnace.getFuelAmount());
				byteBufferWriter.putFloat(bSFurnace.getFuelDecrease());
				byteBufferWriter.putFloat(bSFurnace.getHeat());
				GameWindow.WriteString(byteBufferWriter.bb, bSFurnace.sSprite);
				GameWindow.WriteString(byteBufferWriter.bb, bSFurnace.sLitSprite);
				PacketTypes.PacketType.SyncFurnace.send(udpConnection2);
			}
		}
	}

	static void receiveUserlog(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		ArrayList arrayList = ServerWorldDatabase.instance.getUserlog(string);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.Userlog.doPacket(byteBufferWriter);
				byteBufferWriter.putInt(arrayList.size());
				byteBufferWriter.putUTF(string);
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					Userlog userlog = (Userlog)arrayList.get(int2);
					byteBufferWriter.putInt(Userlog.UserlogType.FromString(userlog.getType()).index());
					byteBufferWriter.putUTF(userlog.getText());
					byteBufferWriter.putUTF(userlog.getIssuedBy());
					byteBufferWriter.putInt(userlog.getAmount());
					byteBufferWriter.putUTF(userlog.getLastUpdate());
				}

				PacketTypes.PacketType.Userlog.send(udpConnection2);
			}
		}
	}

	static void receiveAddUserlog(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) throws SQLException {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		String string3 = GameWindow.ReadString(byteBuffer);
		ServerWorldDatabase.instance.addUserlog(string, Userlog.UserlogType.FromString(string2), string3, udpConnection.username, 1);
		LoggerManager.getLogger("admin").write(udpConnection.username + " added log on user " + string + ", log: " + string3);
	}

	static void receiveRemoveUserlog(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) throws SQLException {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		String string3 = GameWindow.ReadString(byteBuffer);
		ServerWorldDatabase.instance.removeUserLog(string, string2, string3);
		LoggerManager.getLogger("admin").write(udpConnection.username + " removed log on user " + string + ", type:" + string2 + ", log: " + string3);
	}

	static void receiveAddWarningPoint(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) throws SQLException {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		ServerWorldDatabase.instance.addWarningPoint(string, string2, int1, udpConnection.username);
		LoggerManager.getLogger("admin").write(udpConnection.username + " added " + int1 + " warning point(s) on " + string + ", reason:" + string2);
		for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
			if (udpConnection2.username.equals(string)) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.WorldMessage.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF(udpConnection.username);
				byteBufferWriter.putUTF(" gave you " + int1 + " warning point(s), reason: " + string2 + " ");
				PacketTypes.PacketType.WorldMessage.send(udpConnection2);
			}
		}
	}

	public static void sendAdminMessage(String string, int int1, int int2, int int3) {
		for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int4);
			if (canSeePlayerStats(udpConnection)) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.PacketType.MessageForAdmin.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putInt(int3);
				PacketTypes.PacketType.MessageForAdmin.send(udpConnection);
			}
		}
	}

	static void receiveWakeUpPlayer(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		IsoPlayer player = getPlayerFromConnection(udpConnection, byteBuffer.getShort());
		if (player != null) {
			player.setAsleep(false);
			player.setAsleepTime(0.0F);
			sendWakeUpPlayer(player, udpConnection);
		}
	}

	public static void sendWakeUpPlayer(IsoPlayer player, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.WakeUpPlayer.doPacket(byteBufferWriter);
				byteBufferWriter.putShort(player.getOnlineID());
				PacketTypes.PacketType.WakeUpPlayer.send(udpConnection2);
			}
		}
	}

	static void receiveGetDBSchema(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		DBSchema dBSchema = ServerWorldDatabase.instance.getDBSchema();
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection != null && udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.GetDBSchema.doPacket(byteBufferWriter);
				HashMap hashMap = dBSchema.getSchema();
				byteBufferWriter.putInt(hashMap.size());
				Iterator iterator = hashMap.keySet().iterator();
				while (iterator.hasNext()) {
					String string = (String)iterator.next();
					HashMap hashMap2 = (HashMap)hashMap.get(string);
					byteBufferWriter.putUTF(string);
					byteBufferWriter.putInt(hashMap2.size());
					Iterator iterator2 = hashMap2.keySet().iterator();
					while (iterator2.hasNext()) {
						String string2 = (String)iterator2.next();
						byteBufferWriter.putUTF(string2);
						byteBufferWriter.putUTF((String)hashMap2.get(string2));
					}
				}

				PacketTypes.PacketType.GetDBSchema.send(udpConnection2);
			}
		}
	}

	static void receiveGetTableResult(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) throws SQLException {
		int int1 = byteBuffer.getInt();
		String string = GameWindow.ReadString(byteBuffer);
		ArrayList arrayList = ServerWorldDatabase.instance.getTableResult(string);
		for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
			if (udpConnection != null && udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
				doTableResult(udpConnection2, string, arrayList, 0, int1);
			}
		}
	}

	private static void doTableResult(UdpConnection udpConnection, String string, ArrayList arrayList, int int1, int int2) {
		int int3 = 0;
		boolean boolean1 = true;
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.PacketType.GetTableResult.doPacket(byteBufferWriter);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putUTF(string);
		if (arrayList.size() < int2) {
			byteBufferWriter.putInt(arrayList.size());
		} else if (arrayList.size() - int1 < int2) {
			byteBufferWriter.putInt(arrayList.size() - int1);
		} else {
			byteBufferWriter.putInt(int2);
		}

		for (int int4 = int1; int4 < arrayList.size(); ++int4) {
			DBResult dBResult = null;
			try {
				dBResult = (DBResult)arrayList.get(int4);
				byteBufferWriter.putInt(dBResult.getColumns().size());
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			Iterator iterator = dBResult.getColumns().iterator();
			while (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				byteBufferWriter.putUTF(string2);
				byteBufferWriter.putUTF((String)dBResult.getValues().get(string2));
			}

			++int3;
			if (int3 >= int2) {
				boolean1 = false;
				PacketTypes.PacketType.GetTableResult.send(udpConnection);
				doTableResult(udpConnection, string, arrayList, int1 + int3, int2);
				break;
			}
		}

		if (boolean1) {
			PacketTypes.PacketType.GetTableResult.send(udpConnection);
		}
	}

	static void receiveExecuteQuery(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) throws SQLException {
		if (udpConnection.accessLevel == 32) {
			try {
				String string = GameWindow.ReadString(byteBuffer);
				KahluaTable kahluaTable = LuaManager.platform.newTable();
				kahluaTable.load((ByteBuffer)byteBuffer, 195);
				ServerWorldDatabase.instance.executeQuery(string, kahluaTable);
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}

	static void receiveSendFactionInvite(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		String string3 = GameWindow.ReadString(byteBuffer);
		IsoPlayer player = getPlayerByUserName(string3);
		if (player != null) {
			Long Long1 = (Long)IDToAddressMap.get(player.getOnlineID());
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() == Long1) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.SendFactionInvite.doPacket(byteBufferWriter);
					byteBufferWriter.putUTF(string);
					byteBufferWriter.putUTF(string2);
					PacketTypes.PacketType.SendFactionInvite.send(udpConnection2);
					break;
				}
			}
		}
	}

	static void receiveAcceptedFactionInvite(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		IsoPlayer player = getPlayerByUserName(string2);
		Long Long1 = (Long)IDToAddressMap.get(player.getOnlineID());
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.getConnectedGUID() == Long1) {
				Faction faction = Faction.getPlayerFaction(udpConnection2.username);
				if (faction != null && faction.getName().equals(string)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.AcceptedFactionInvite.doPacket(byteBufferWriter);
					byteBufferWriter.putUTF(string);
					byteBufferWriter.putUTF(string2);
					PacketTypes.PacketType.AcceptedFactionInvite.send(udpConnection2);
				}
			}
		}
	}

	static void receiveViewTickets(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) throws SQLException {
		String string = GameWindow.ReadString(byteBuffer);
		if ("".equals(string)) {
			string = null;
		}

		sendTickets(string, udpConnection);
	}

	private static void sendTickets(String string, UdpConnection udpConnection) throws SQLException {
		ArrayList arrayList = ServerWorldDatabase.instance.getTickets(string);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.ViewTickets.doPacket(byteBufferWriter);
				byteBufferWriter.putInt(arrayList.size());
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					DBTicket dBTicket = (DBTicket)arrayList.get(int2);
					byteBufferWriter.putUTF(dBTicket.getAuthor());
					byteBufferWriter.putUTF(dBTicket.getMessage());
					byteBufferWriter.putInt(dBTicket.getTicketID());
					if (dBTicket.getAnswer() != null) {
						byteBufferWriter.putByte((byte)1);
						byteBufferWriter.putUTF(dBTicket.getAnswer().getAuthor());
						byteBufferWriter.putUTF(dBTicket.getAnswer().getMessage());
						byteBufferWriter.putInt(dBTicket.getAnswer().getTicketID());
					} else {
						byteBufferWriter.putByte((byte)0);
					}
				}

				PacketTypes.PacketType.ViewTickets.send(udpConnection2);
				break;
			}
		}
	}

	static void receiveAddTicket(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) throws SQLException {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		if (int1 == -1) {
			sendAdminMessage("user " + string + " added a ticket <LINE> <LINE> " + string2, -1, -1, -1);
		}

		ServerWorldDatabase.instance.addTicket(string, string2, int1);
		sendTickets(string, udpConnection);
	}

	static void receiveRemoveTicket(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) throws SQLException {
		int int1 = byteBuffer.getInt();
		ServerWorldDatabase.instance.removeTicket(int1);
		sendTickets((String)null, udpConnection);
	}

	public static boolean sendItemListNet(UdpConnection udpConnection, IsoPlayer player, ArrayList arrayList, IsoPlayer player2, String string, String string2) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection == null || udpConnection2 != udpConnection) {
				if (player2 != null) {
					boolean boolean1 = false;
					for (int int2 = 0; int2 < udpConnection2.players.length; ++int2) {
						IsoPlayer player3 = udpConnection2.players[int2];
						if (player3 != null && player3 == player2) {
							boolean1 = true;
							break;
						}
					}

					if (!boolean1) {
						continue;
					}
				}

				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.SendItemListNet.doPacket(byteBufferWriter);
				byteBufferWriter.putByte((byte)(player2 != null ? 1 : 0));
				if (player2 != null) {
					byteBufferWriter.putShort(player2.getOnlineID());
				}

				byteBufferWriter.putByte((byte)(player != null ? 1 : 0));
				if (player != null) {
					byteBufferWriter.putShort(player.getOnlineID());
				}

				GameWindow.WriteString(byteBufferWriter.bb, string);
				byteBufferWriter.putByte((byte)(string2 != null ? 1 : 0));
				if (string2 != null) {
					GameWindow.WriteString(byteBufferWriter.bb, string2);
				}

				try {
					CompressIdenticalItems.save(byteBufferWriter.bb, arrayList, (IsoGameCharacter)null);
				} catch (Exception exception) {
					exception.printStackTrace();
					udpConnection2.cancelPacket();
					return false;
				}

				PacketTypes.PacketType.SendItemListNet.send(udpConnection2);
			}
		}

		return true;
	}

	static void receiveSendItemListNet(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		IsoPlayer player = null;
		if (byteBuffer.get() == 1) {
			player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getShort());
		}

		IsoPlayer player2 = null;
		if (byteBuffer.get() == 1) {
			player2 = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getShort());
		}

		String string = GameWindow.ReadString(byteBuffer);
		String string2 = null;
		if (byteBuffer.get() == 1) {
			string2 = GameWindow.ReadString(byteBuffer);
		}

		ArrayList arrayList = new ArrayList();
		try {
			CompressIdenticalItems.load(byteBuffer, 195, arrayList, (ArrayList)null);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (player == null) {
			LuaEventManager.triggerEvent("OnReceiveItemListNet", player2, arrayList, player, string, string2);
		} else {
			sendItemListNet(udpConnection, player2, arrayList, player, string, string2);
		}
	}

	public static void sendPlayerDamagedByCarCrash(IsoPlayer player, float float1) {
		UdpConnection udpConnection = getConnectionFromPlayer(player);
		if (udpConnection != null) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.PlayerDamageFromCarCrash.doPacket(byteBufferWriter);
			byteBufferWriter.putFloat(float1);
			PacketTypes.PacketType.PlayerDamageFromCarCrash.send(udpConnection);
		}
	}

	static void receiveClimateManagerPacket(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ClimateManager climateManager = ClimateManager.getInstance();
		if (climateManager != null) {
			try {
				climateManager.receiveClimatePacket(byteBuffer, udpConnection);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	static void receivePassengerMap(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		PassengerMap.serverReceivePacket(byteBuffer, udpConnection);
	}

	static void receiveIsoRegionClientRequestFullUpdate(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		IsoRegions.receiveClientRequestFullDataChunks(byteBuffer, udpConnection);
	}

	private static String isWorldVersionUnsupported() {
		String string = ZomboidFileSystem.instance.getSaveDir();
		File file = new File(string + File.separator + "Multiplayer" + File.separator + ServerName + File.separator + "map_t.bin");
		if (file.exists()) {
			DebugLog.log("checking server WorldVersion in map_t.bin");
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				String string2;
				label93: {
					label92: {
						String string3;
						label112: {
							try {
								DataInputStream dataInputStream = new DataInputStream(fileInputStream);
								label87: {
									label86: {
										label85: {
											try {
												byte byte1 = dataInputStream.readByte();
												byte byte2 = dataInputStream.readByte();
												byte byte3 = dataInputStream.readByte();
												byte byte4 = dataInputStream.readByte();
												if (byte1 != 71 || byte2 != 77 || byte3 != 84 || byte4 != 77) {
													string3 = "The server savefile appears to be from an old version of the game and cannot be loaded.";
													break label85;
												}

												int int1 = dataInputStream.readInt();
												if (int1 <= 195) {
													if (int1 > 143) {
														break label87;
													}

													string2 = "The server savefile appears to be from a pre-animations version of the game and cannot be loaded.\nDue to the extent of changes required to implement animations, saves from earlier versions are not compatible.";
													break label86;
												}

												string2 = "The server savefile appears to be from a newer version of the game and cannot be loaded.";
											} catch (Throwable throwable) {
												try {
													dataInputStream.close();
												} catch (Throwable throwable2) {
													throwable.addSuppressed(throwable2);
												}

												throw throwable;
											}

											dataInputStream.close();
											break label93;
										}

										dataInputStream.close();
										break label112;
									}

									dataInputStream.close();
									break label92;
								}

								dataInputStream.close();
							} catch (Throwable throwable3) {
								try {
									fileInputStream.close();
								} catch (Throwable throwable4) {
									throwable3.addSuppressed(throwable4);
								}

								throw throwable3;
							}

							fileInputStream.close();
							return null;
						}

						fileInputStream.close();
						return string3;
					}

					fileInputStream.close();
					return string2;
				}

				fileInputStream.close();
				return string2;
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			DebugLog.log("map_t.bin does not exist, cannot determine the server\'s WorldVersion.  This is ok the first time a server is started.");
		}

		return null;
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

	public String getDifficulty() {
		return this.difficulty;
	}

	public void setDifficulty(String string) {
		this.difficulty = string;
	}

	public static void transmitBrokenGlass(IsoGridSquare square) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			try {
				if (udpConnection.RelevantTo((float)square.getX(), (float)square.getY())) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.PacketType.AddBrokenGlass.doPacket(byteBufferWriter);
					byteBufferWriter.putInt((short)square.getX());
					byteBufferWriter.putInt((short)square.getY());
					byteBufferWriter.putInt((short)square.getZ());
					PacketTypes.PacketType.AddBrokenGlass.send(udpConnection);
				}
			} catch (Throwable throwable) {
				udpConnection.cancelPacket();
				ExceptionLogger.logException(throwable);
			}
		}
	}

	public static boolean isServerDropPackets() {
		return droppedPackets > 0;
	}

	static void receiveSyncPerks(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			player.remoteSneakLvl = int1;
			player.remoteStrLvl = int2;
			player.remoteFitLvl = int3;
			for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						try {
							ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
							PacketTypes.PacketType.SyncPerks.doPacket(byteBufferWriter);
							byteBufferWriter.putShort(player.OnlineID);
							byteBufferWriter.putInt(int1);
							byteBufferWriter.putInt(int2);
							byteBufferWriter.putInt(int3);
							PacketTypes.PacketType.SyncPerks.send(udpConnection2);
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	static void receiveSyncWeight(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		double double1 = byteBuffer.getDouble();
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						try {
							ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
							PacketTypes.PacketType.SyncWeight.doPacket(byteBufferWriter);
							byteBufferWriter.putShort(player.OnlineID);
							byteBufferWriter.putDouble(double1);
							PacketTypes.PacketType.SyncWeight.send(udpConnection2);
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	static void receiveSyncEquippedRadioFreq(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		byte byte1 = byteBuffer.get();
		int int1 = byteBuffer.getInt();
		ArrayList arrayList = new ArrayList();
		for (int int2 = 0; int2 < int1; ++int2) {
			arrayList.add(byteBuffer.getInt());
		}

		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						try {
							ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
							PacketTypes.PacketType.SyncEquippedRadioFreq.doPacket(byteBufferWriter);
							byteBufferWriter.putShort(player.OnlineID);
							byteBufferWriter.putInt(int1);
							for (int int4 = 0; int4 < arrayList.size(); ++int4) {
								byteBufferWriter.putInt((Integer)arrayList.get(int4));
							}

							PacketTypes.PacketType.SyncEquippedRadioFreq.send(udpConnection2);
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	static void receiveGlobalModData(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		GlobalModData.instance.receive(byteBuffer);
	}

	static void receiveGlobalModDataRequest(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		GlobalModData.instance.receiveRequest(byteBuffer, udpConnection);
	}

	static void receiveSendSafehouseInvite(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		String string3 = GameWindow.ReadString(byteBuffer);
		IsoPlayer player = getPlayerByUserName(string3);
		Long Long1 = (Long)IDToAddressMap.get(player.getOnlineID());
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
			if (udpConnection2.getConnectedGUID() == Long1) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.PacketType.SendSafehouseInvite.doPacket(byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putUTF(string2);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putInt(int3);
				byteBufferWriter.putInt(int4);
				PacketTypes.PacketType.SendSafehouseInvite.send(udpConnection2);
				break;
			}
		}
	}

	static void receiveAcceptedSafehouseInvite(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		String string3 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		SafeHouse safeHouse = SafeHouse.getSafeHouse(int1, int2, int3, int4);
		if (safeHouse != null) {
			safeHouse.addPlayer(string3);
		} else {
			DebugLog.log("WARN: player \'" + string3 + "\' accepted the invitation, but the safehouse not found for x=" + int1 + " y=" + int2 + " w=" + int3 + " h=" + int4);
		}

		for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
			ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
			PacketTypes.PacketType.AcceptedSafehouseInvite.doPacket(byteBufferWriter);
			byteBufferWriter.putUTF(string);
			byteBufferWriter.putUTF(string2);
			byteBufferWriter.putUTF(string3);
			byteBufferWriter.putInt(int1);
			byteBufferWriter.putInt(int2);
			byteBufferWriter.putInt(int3);
			byteBufferWriter.putInt(int4);
			PacketTypes.PacketType.AcceptedSafehouseInvite.send(udpConnection2);
		}
	}

	public static void sendRadioPostSilence() {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection.statistic.enable == 3) {
				sendShortStatistic(udpConnection);
			}
		}
	}

	public static void sendRadioPostSilence(UdpConnection udpConnection) {
		try {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.RadioPostSilenceEvent.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)(ZomboidRadio.POST_RADIO_SILENCE ? 1 : 0));
			PacketTypes.PacketType.RadioPostSilenceEvent.send(udpConnection);
		} catch (Exception exception) {
			exception.printStackTrace();
			udpConnection.cancelPacket();
		}
	}

	static void receiveSneezeCough(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		byte byte1 = byteBuffer.get();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player != null) {
			float float1 = player.x;
			float float2 = player.y;
			int int1 = 0;
			for (int int2 = udpEngine.connections.size(); int1 < int2; ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection.getConnectedGUID() != udpConnection2.getConnectedGUID() && udpConnection2.RelevantTo(float1, float2)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.PacketType.SneezeCough.doPacket(byteBufferWriter);
					byteBufferWriter.putShort(short2);
					byteBufferWriter.putByte(byte1);
					PacketTypes.PacketType.SneezeCough.send(udpConnection2);
				}
			}
		}
	}

	static void receiveBurnCorpse(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		short short2 = byteBuffer.getShort();
		short short3 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(short2);
		if (player == null) {
			DebugLog.Network.warn("Player not found by id " + short2);
		} else {
			IsoDeadBody deadBody = IsoDeadBody.getDeadBody(short3);
			if (deadBody == null) {
				DebugLog.Network.warn("Corpse not found by id " + short3);
			} else {
				float float1 = IsoUtils.DistanceTo(player.x, player.y, deadBody.x, deadBody.y);
				if (float1 <= 1.8F) {
					IsoFireManager.StartFire(deadBody.getCell(), deadBody.getSquare(), true, 100);
				} else {
					DebugLog.Network.warn("Distance between player and corpse too big: " + float1);
				}
			}
		}
	}

	public static void sendValidatePacket(UdpConnection udpConnection, boolean boolean1, boolean boolean2, boolean boolean3) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		try {
			ValidatePacket validatePacket = new ValidatePacket();
			validatePacket.setSalt(udpConnection.validator.getSalt(), boolean1, boolean2, boolean3);
			PacketTypes.PacketType.Validate.doPacket(byteBufferWriter);
			validatePacket.write(byteBufferWriter);
			PacketTypes.PacketType.Validate.send(udpConnection);
			validatePacket.log(GameClient.connection, "send-packet");
		} catch (Exception exception) {
			udpConnection.cancelPacket();
			DebugLog.Multiplayer.printException(exception, "SendValidatePacket: failed", LogSeverity.Error);
		}
	}

	static void receiveValidatePacket(ByteBuffer byteBuffer, UdpConnection udpConnection, short short1) {
		ValidatePacket validatePacket = new ValidatePacket();
		validatePacket.parse(byteBuffer, udpConnection);
		validatePacket.log(GameClient.connection, "receive-packet");
		if (validatePacket.isConsistent()) {
			validatePacket.process(udpConnection);
		}
	}

	static  {
		discordBot = new DiscordBot(ServerName, (var0,var1)->{
			ChatServer.getInstance().sendMessageFromDiscordToGeneralChat(var0, var1);
		});
		checksum = "";
		GameMap = "Muldraugh, KY";
		transactionIDMap = new HashMap();
		worldObjectsServerSyncReq = new ObjectsSyncRequests(false);
		ip = "127.0.0.1";
		count = 0;
		SlotToConnection = new UdpConnection[512];
		PlayerToAddressMap = new HashMap();
		alreadyRemoved = new ArrayList();
		launched = false;
		consoleCommands = new ArrayList();
		MainLoopPlayerUpdate = new HashMap();
		MainLoopPlayerUpdateQ = new ConcurrentLinkedQueue();
		MainLoopNetDataHighPriorityQ = new ConcurrentLinkedQueue();
		MainLoopNetDataQ = new ConcurrentLinkedQueue();
		MainLoopNetData2 = new ArrayList();
		playerToCoordsMap = new HashMap();
		playerMovedToFastMap = new HashMap();
		large_file_bb = ByteBuffer.allocate(2097152);
		previousSave = Calendar.getInstance().getTimeInMillis();
		droppedPackets = 0;
		countOfDroppedPackets = 0;
		countOfDroppedConnections = 0;
		removeZombiesConnection = null;
		calcCountPlayersInRelevantPositionLimiter = new UpdateLimit(2000L);
		sendWorldMapPlayerPositionLimiter = new UpdateLimit(1000L);
		loginQueue = new LoginQueue();
		mainCycleExceptionLogCount = 25;
		tempPlayers = new ArrayList();
	}

	private static class DelayedConnection implements IZomboidPacket {
		public UdpConnection connection;
		public boolean connect;
		public String hostString;

		public DelayedConnection(UdpConnection udpConnection, boolean boolean1) {
			this.connection = udpConnection;
			this.connect = boolean1;
			if (boolean1) {
				try {
					this.hostString = udpConnection.getInetSocketAddress().getHostString();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		public boolean isConnect() {
			return this.connect;
		}

		public boolean isDisconnect() {
			return !this.connect;
		}
	}

	private static class s_performance {
		static final PerformanceProfileFrameProbe frameStep = new PerformanceProfileFrameProbe("GameServer.frameStep");
		static final PerformanceProfileProbe mainLoopDealWithNetData = new PerformanceProfileProbe("GameServer.mainLoopDealWithNetData");
		static final PerformanceProfileProbe RCONServerUpdate = new PerformanceProfileProbe("RCONServer.update");
	}

	private static final class CCFilter {
		String command;
		boolean allow;
		GameServer.CCFilter next;

		boolean matches(String string) {
			return this.command.equals(string) || "*".equals(this.command);
		}

		boolean passes(String string) {
			if (this.matches(string)) {
				return this.allow;
			} else {
				return this.next == null ? true : this.next.passes(string);
			}
		}
	}
}
