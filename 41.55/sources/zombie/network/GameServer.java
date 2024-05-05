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
import java.io.UnsupportedEncodingException;
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
import java.util.Map.Entry;
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
import zombie.PersistentOutfits;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SoundManager;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.states.ZombieGetUpState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.asset.AssetManagers;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.WornItems.WornItem;
import zombie.characters.WornItems.WornItems;
import zombie.characters.skills.CustomPerks;
import zombie.characters.skills.PerkFactory;
import zombie.commands.CommandBase;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Languages;
import zombie.core.PerformanceSettings;
import zombie.core.ProxyPrintStream;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.network.ByteBufferReader;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.Bullet;
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
import zombie.core.znet.PortMapper;
import zombie.core.znet.SteamGameServer;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.erosion.ErosionMain;
import zombie.gameStates.ChooseGameInfo;
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
import zombie.network.packets.DeadBodyPacket;
import zombie.network.packets.EventUpdatePacket;
import zombie.network.packets.HitPacket;
import zombie.network.packets.PlayerPacket;
import zombie.network.packets.ZombieUpdateInfoPacket;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.sandbox.CustomSandboxOptions;
import zombie.savefile.ServerPlayerDB;
import zombie.scripting.ScriptManager;
import zombie.util.PZSQLUtils;
import zombie.util.PublicServerUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.Clipper;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehiclesDB2;
import zombie.world.WorldDictionary;
import zombie.world.moddata.GlobalModData;


public class GameServer {
	public static final int MAX_PLAYERS = 512;
	public static final int TimeLimitForProcessPackets = 70;
	public static final int FPS = 10;
	private static final long[] packetCounts = new long[256];
	private static final HashMap ccFilters = new HashMap();
	public static int test = 432432;
	public static int DEFAULT_PORT = 16261;
	public static String IPCommandline = null;
	public static int PortCommandline = -1;
	public static int SteamPortCommandline1 = -1;
	public static int SteamPortCommandline2 = -1;
	public static Boolean SteamVACCommandline;
	public static boolean GUICommandline;
	public static boolean bServer = false;
	public static boolean bCoop = false;
	public static boolean bDebug = false;
	public static UdpEngine udpEngine;
	public static final HashMap IDToAddressMap = new HashMap();
	public static final HashMap IDToPlayerMap = new HashMap();
	public static final ArrayList Players = new ArrayList();
	public static float timeSinceKeepAlive = 0.0F;
	public static int MaxTicksSinceKeepAliveBeforeStall = 60;
	public static final HashMap PlayerToBody = new HashMap();
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
	public static boolean UseTCPForMapDownloads;
	public static final HashMap transactionIDMap;
	public static final ObjectsSyncRequests worldObjectsServerSyncReq;
	public static String ip;
	static int count;
	private static final UdpConnection[] SlotToConnection;
	private static final HashMap PlayerToAddressMap;
	private static final ArrayList alreadyRemoved;
	private static int SendZombies;
	private static boolean bDone;
	private static boolean launched;
	private static final ArrayList consoleCommands;
	private static final HashMap MainLoopPlayerUpdate;
	private static final ArrayList MainLoopNetDataHighPrioritet;
	private static final ArrayList MainLoopNetData;
	private static final ArrayList MainLoopNetData2;
	private static final HashMap playerToCoordsMap;
	private static final HashMap playerMovedToFastMap;
	private static final ByteBuffer large_file_bb;
	private static long previousSave;
	private String poisonousBerry = null;
	private String poisonousMushroom = null;
	private String difficulty = "Hardcore";
	private static int droppedPackets;
	private static int countOfDroppedPackets;
	private static int countOfDroppedConnections;

	public static void PauseAllClients() {
		String string = "[SERVERMSG] Server saving...Please wait";
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)158, byteBufferWriter);
			byteBufferWriter.putUTF(string);
			udpConnection.endPacketImmediate();
		}
	}

	public static void UnPauseAllClients() {
		String string = "[SERVERMSG] Server saved game...enjoy :)";
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)159, byteBufferWriter);
			byteBufferWriter.putUTF(string);
			udpConnection.endPacketImmediate();
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
		bServer = true;
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
		String string2;
		if (bCoop) {
			try {
				CoopSlave.initStreams();
			} catch (FileNotFoundException fileNotFoundException) {
				fileNotFoundException.printStackTrace();
			}
		} else {
			try {
				string = ZomboidFileSystem.instance.getCacheDir();
				string2 = string + File.separator + "server-console.txt";
				FileOutputStream fileOutputStream = new FileOutputStream(string2);
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
				CoopSlave.status(Translator.getText("UI_ServerStatus_Initialising"));
			} catch (FileNotFoundException fileNotFoundException3) {
				fileNotFoundException3.printStackTrace();
				SteamUtils.shutdown();
				System.exit(37);
				return;
			}
		}

		PZSQLUtils.init();
		Clipper.init();
		Bullet.init();
		Rand.init();
		DebugLog.setLogEnabled(DebugType.General, true);
		DebugLog.setLogEnabled(DebugType.Network, true);
		DebugLog.setLogEnabled(DebugType.Lua, true);
		if (System.getProperty("debug") != null) {
			bDebug = true;
			Core.bDebug = true;
		}

		DebugLog.General.println("versionNumber=%s demo=%s", Core.getInstance().getVersionNumber(), false);
		int int2;
		String string3;
		for (int1 = 0; int1 < stringArray.length; ++int1) {
			if (stringArray[int1] != null) {
				if (stringArray[int1].startsWith("-debuglog=")) {
					String[] stringArray2 = stringArray[int1].replace("-debuglog=", "").split(",");
					int int3 = stringArray2.length;
					for (int2 = 0; int2 < int3; ++int2) {
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
					} else if (stringArray[int1].equals("-port")) {
						PortCommandline = parsePortFromCommandline(stringArray, int1, "-port");
						++int1;
					} else if (stringArray[int1].equals("-steamport1")) {
						SteamPortCommandline1 = parsePortFromCommandline(stringArray, int1, "-steamport1");
						++int1;
					} else if (stringArray[int1].equals("-steamport2")) {
						SteamPortCommandline2 = parsePortFromCommandline(stringArray, int1, "-steamport2");
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
			}
		}

		DebugLog.log("server name is \"" + ServerName + "\"");
		string2 = isWorldVersionUnsupported();
		if (string2 != null) {
			DebugLog.log(string2);
			CoopSlave.status(string2);
		} else {
			SteamUtils.init();
			RakNetPeerInterface.init();
			ZombiePopulationManager.init();
			ServerOptions.instance.init();
			initClientCommandFilter();
			if (PortCommandline != -1) {
				ServerOptions.instance.DefaultPort.setValue(PortCommandline);
			}

			if (SteamPortCommandline1 != -1) {
				ServerOptions.instance.SteamPort1.setValue(SteamPortCommandline1);
			}

			if (SteamPortCommandline2 != -1) {
				ServerOptions.instance.SteamPort2.setValue(SteamPortCommandline2);
			}

			if (SteamVACCommandline != null) {
				ServerOptions.instance.SteamVAC.setValue(SteamVACCommandline);
			}

			DEFAULT_PORT = ServerOptions.instance.DefaultPort.getValue();
			UseTCPForMapDownloads = ServerOptions.instance.UseTCPForMapDownloads.getValue();
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
			int int4;
			String string6;
			if (string5 != null) {
				String[] stringArray4 = string5.split(";");
				String[] stringArray5 = stringArray4;
				int4 = stringArray4.length;
				for (int int5 = 0; int5 < int4; ++int5) {
					string6 = stringArray5[int5];
					if (!string6.trim().isEmpty()) {
						ServerMods.add(string6.trim());
					}
				}
			}

			int int6;
			int int7;
			if (SteamUtils.isSteamModeEnabled()) {
				int2 = ServerOptions.instance.SteamPort1.getValue();
				int int8 = ServerOptions.instance.SteamPort2.getValue();
				int4 = ServerOptions.instance.SteamVAC.getValue() ? 3 : 2;
				if (!SteamGameServer.Init(IPCommandline, int2, int8, DEFAULT_PORT, int4, Core.getInstance().getSteamServerVersion())) {
					SteamUtils.shutdown();
					return;
				}

				SteamGameServer.SetProduct("zomboid");
				SteamGameServer.SetGameDescription("Project Zomboid");
				SteamGameServer.SetModDir("zomboid");
				SteamGameServer.SetDedicatedServer(true);
				SteamGameServer.SetMaxPlayerCount(ServerOptions.instance.MaxPlayers.getValue());
				SteamGameServer.SetServerName(ServerOptions.instance.PublicName.getValue());
				SteamGameServer.SetMapName(ServerOptions.instance.Map.getValue());
				if (ServerOptions.instance.Public.getValue()) {
					string = ServerOptions.instance.Mods.getValue();
					SteamGameServer.SetGameTags(string + (CoopSlave.instance != null ? ";hosted" : ""));
				} else {
					SteamGameServer.SetGameTags("hidden" + (CoopSlave.instance != null ? ";hosted" : ""));
				}

				SteamGameServer.SetKeyValue("description", ServerOptions.instance.PublicDescription.getValue());
				SteamGameServer.SetKeyValue("version", Core.getInstance().getVersionNumber());
				SteamGameServer.SetKeyValue("open", ServerOptions.instance.Open.getValue() ? "1" : "0");
				SteamGameServer.SetKeyValue("public", ServerOptions.instance.Public.getValue() ? "1" : "0");
				if (bDebug) {
					SteamGameServer.SetKeyValue("description", "0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789 0123456789\n");
					try {
						byte[] byteArray = new byte[]{-16, -96, -100, -114};
						string6 = new String(byteArray, "UTF-8");
						String string7 = "";
						for (int6 = 0; int6 < 128; ++int6) {
							string7 = string7 + string6;
						}

						SteamGameServer.SetKeyValue("test", string7);
					} catch (UnsupportedEncodingException unsupportedEncodingException) {
					}

					SteamGameServer.SetKeyValue("test2", "12345");
				}

				String string8 = ServerOptions.instance.WorkshopItems.getValue();
				if (string8 != null) {
					String[] stringArray6 = string8.split(";");
					String[] stringArray7 = stringArray6;
					int6 = stringArray6.length;
					for (int int9 = 0; int9 < int6; ++int9) {
						String string9 = stringArray7[int9];
						string9 = string9.trim();
						if (!string9.isEmpty() && SteamUtils.isValidSteamID(string9)) {
							WorkshopItems.add(SteamUtils.convertStringToSteamID(string9));
						}
					}
				}

				SteamWorkshop.init();
				SteamGameServer.LogOnAnonymous();
				SteamGameServer.EnableHeartBeats(true);
				DebugLog.log("Waiting for response from Steam servers");
				while (true) {
					SteamUtils.runLoop();
					int7 = SteamGameServer.GetSteamServersConnectState();
					if (int7 == SteamGameServer.STEAM_SERVERS_CONNECTED) {
						if (!GameServerWorkshopItems.Install(WorkshopItems)) {
							return;
						}

						break;
					}

					if (int7 == SteamGameServer.STEAM_SERVERS_CONNECTFAILURE) {
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
					int4 = ServerOptions.instance.UPnPLeaseTime.getValue();
					boolean boolean1 = ServerOptions.instance.UPnPForce.getValue();
					if (PortMapper.addMapping(DEFAULT_PORT, DEFAULT_PORT, "PZ Server default port", "UDP", int4, boolean1)) {
						DebugLog.log(DebugType.Network, "Default port has been mapped successfully");
					} else {
						DebugLog.log(DebugType.Network, "Failed to map default port");
					}

					int int10;
					if (SteamUtils.isSteamModeEnabled()) {
						int7 = ServerOptions.instance.SteamPort1.getValue();
						if (PortMapper.addMapping(int7, int7, "PZ Server SteamPort1", "UDP", int4, boolean1)) {
							DebugLog.log(DebugType.Network, "SteamPort1 has been mapped successfully");
						} else {
							DebugLog.log(DebugType.Network, "Failed to map SteamPort1");
						}

						int10 = ServerOptions.instance.SteamPort2.getValue();
						if (PortMapper.addMapping(int10, int10, "PZ Server SteamPort2", "UDP", int4, boolean1)) {
							DebugLog.log(DebugType.Network, "SteamPort2 has been mapped successfully");
						} else {
							DebugLog.log(DebugType.Network, "Failed to map SteamPort2");
						}
					}

					if (UseTCPForMapDownloads) {
						for (int7 = 1; int7 <= ServerOptions.instance.MaxPlayers.getValue(); ++int7) {
							int10 = DEFAULT_PORT + int7;
							if (PortMapper.addMapping(int10, int10, "PZ Server TCP Port " + int7, "TCP", int4, boolean1)) {
								DebugLog.log(DebugType.Network, int10 + " has been mapped successfully");
							} else {
								DebugLog.log(DebugType.Network, "Failed to map TCP port " + int10);
							}
						}
					}
				} else {
					DebugLog.log(DebugType.Network, "No UPnP-enabled Internet gateway found, you must configure port forwarding on your gateway manually in order to make your server accessible from the Internet.");
				}
			}

			Core.GameMode = "Multiplayer";
			bDone = false;
			DebugLog.log(DebugType.Network, "Initialising Server Systems...");
			CoopSlave.status(Translator.getText("UI_ServerStatus_Init"));
			try {
				doMinimumInit();
			} catch (Exception exception) {
				DebugLog.General.printException(exception, "Exception Thrown", LogSeverity.Error);
				DebugLog.General.println("Server Terminated.");
			}

			LosUtil.init(100, 100);
			ChatServer.getInstance().init();
			DebugLog.log(DebugType.Network, "Loading world...");
			CoopSlave.status(Translator.getText("UI_ServerStatus_LoadingWorld"));
			try {
				ClimateManager.setInstance(new ClimateManager());
				IsoWorld.instance.init();
			} catch (Exception exception2) {
				DebugLog.General.printException(exception2, "Exception Thrown", LogSeverity.Error);
				DebugLog.General.println("Server Terminated.");
				CoopSlave.status(Translator.getText("UI_ServerStatus_Terminated"));
				return;
			}

			File file = ZomboidFileSystem.instance.getFileInCurrentSave("z_outfits.bin");
			if (!file.exists()) {
				ServerOptions.instance.changeOption("ResetID", (new Integer(Rand.Next(100000000))).toString());
			}

			try {
				SpawnPoints.instance.initServer2();
			} catch (Exception exception3) {
				exception3.printStackTrace();
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

			PerformanceSettings.setLockFPS(10);
			IngameState ingameState = new IngameState();
			float float1 = 0.0F;
			float float2 = 0.0F;
			float[] floatArray = new float[20];
			for (int6 = 0; int6 < 20; ++int6) {
				floatArray[int6] = (float)PerformanceSettings.getLockFPS();
			}

			boolean boolean2 = false;
			float float3 = (float)PerformanceSettings.getLockFPS();
			long long1 = 0L;
			int int11 = 0;
			long long2 = 0L;
			long long3 = 1000000000L / (long)PerformanceSettings.getLockFPS();
			if (!SteamUtils.isSteamModeEnabled()) {
				PublicServerUtil.init();
				PublicServerUtil.insertOrUpdate();
			}

			ServerLOS.init();
			int int12 = ServerOptions.instance.RCONPort.getValue();
			String string10 = ServerOptions.instance.RCONPassword.getValue();
			if (int12 != 0 && string10 != null && !string10.isEmpty()) {
				RCONServer.init(int12, string10);
			}

			LuaManager.GlobalObject.refreshAnimSets(true);
			while (!bDone) {
				try {
					MPStatistic.getInstance().Main.Start();
					++IsoCamera.frameState.frameCount;
					GameServer.s_performance.frameStep.start();
					timeSinceKeepAlive += GameTime.getInstance().getMultiplier();
					long long4 = System.nanoTime();
					long long5 = System.currentTimeMillis();
					long3 = 1000000000L / (long)PerformanceSettings.getLockFPS();
					double double1 = ServerOptions.instance.ZombieUpdateDelta.getValue();
					++SendZombies;
					if ((double)((float)SendZombies / float3) > double1) {
						SendZombies = 0;
					}

					MPStatistic.getInstance().ServerMapPreupdate.Start();
					ServerMap.instance.preupdate();
					MPStatistic.getInstance().ServerMapPreupdate.End();
					long long6 = System.currentTimeMillis();
					synchronized (MainLoopNetDataHighPrioritet) {
						MainLoopNetData2.clear();
						MainLoopNetData2.addAll(MainLoopNetDataHighPrioritet);
						MainLoopNetDataHighPrioritet.clear();
					}

					MPStatistic.getInstance().setPacketsLength((long)MainLoopNetData2.size());
					int int13;
					IZomboidPacket iZomboidPacket;
					for (int13 = 0; int13 < MainLoopNetData2.size(); ++int13) {
						iZomboidPacket = (IZomboidPacket)MainLoopNetData2.get(int13);
						UdpConnection udpConnection;
						if (iZomboidPacket.isConnect()) {
							udpConnection = ((GameServer.DelayedConnection)iZomboidPacket).connection;
							LoggerManager.getLogger("user").write("added connection index=" + udpConnection.index + " " + ((GameServer.DelayedConnection)iZomboidPacket).hostString);
							udpEngine.connections.add(udpConnection);
						} else if (iZomboidPacket.isDisconnect()) {
							udpConnection = ((GameServer.DelayedConnection)iZomboidPacket).connection;
							LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + udpConnection.username + "\" removed connection index=" + udpConnection.index);
							udpEngine.connections.remove(udpConnection);
							disconnect(udpConnection);
						} else {
							short short1 = ((ZomboidNetData)iZomboidPacket).type;
							mainLoopDealWithNetData((ZomboidNetData)iZomboidPacket);
						}
					}

					synchronized (MainLoopPlayerUpdate) {
						MainLoopNetData2.clear();
						MainLoopNetData2.addAll(MainLoopPlayerUpdate.values());
						MainLoopPlayerUpdate.clear();
					}

					MPStatistic.getInstance().setPacketsLength((long)MainLoopNetData2.size());
					for (int13 = 0; int13 < MainLoopNetData2.size(); ++int13) {
						iZomboidPacket = (IZomboidPacket)MainLoopNetData2.get(int13);
						GameServer.s_performance.mainLoopDealWithNetData.invokeAndMeasure((ZomboidNetData)iZomboidPacket, GameServer::mainLoopDealWithNetData);
					}

					synchronized (MainLoopNetData) {
						MainLoopNetData2.clear();
						MainLoopNetData2.addAll(MainLoopNetData);
						MainLoopNetData.clear();
					}

					MPStatistic.getInstance().setPacketsLength((long)MainLoopNetData2.size());
					for (int13 = 0; int13 < MainLoopNetData2.size(); ++int13) {
						if (int13 % 10 == 0 && System.currentTimeMillis() - long6 > 70L) {
							if (droppedPackets == 0) {
								DebugLog.log("Server is too busy. Server will drop updates of vehicle\'s physics. Server is closed for new connections.");
							}

							droppedPackets += 2;
							countOfDroppedPackets += MainLoopNetData2.size() - int13;
							break;
						}

						iZomboidPacket = (IZomboidPacket)MainLoopNetData2.get(int13);
						GameServer.s_performance.mainLoopDealWithNetData.invokeAndMeasure((ZomboidNetData)iZomboidPacket, GameServer::mainLoopDealWithNetData);
					}

					MainLoopNetData2.clear();
					if (droppedPackets == 1) {
						DebugLog.log("Server is working normal. Server will not drop updates of vehicle\'s physics. The server is open for new connections. Server dropped " + countOfDroppedPackets + " packets and " + countOfDroppedConnections + " connections.");
						countOfDroppedPackets = 0;
						countOfDroppedConnections = 0;
					}

					droppedPackets = Math.max(0, Math.min(1000, droppedPackets - 1));
					int int14;
					synchronized (consoleCommands) {
						int14 = 0;
						while (true) {
							if (int14 >= consoleCommands.size()) {
								consoleCommands.clear();
								break;
							}

							String string11 = (String)consoleCommands.get(int14);
							try {
								if (CoopSlave.instance == null || !CoopSlave.instance.handleCommand(string11)) {
									System.out.println(handleServerCommand(string11, (UdpConnection)null));
								}
							} catch (Exception exception4) {
								exception4.printStackTrace();
							}

							++int14;
						}
					}

					GameServer.s_performance.RCONServerUpdate.invokeAndMeasure(RCONServer::update);
					try {
						MapCollisionData.instance.updateGameState();
						MPStatistic.getInstance().IngameStateUpdate.Start();
						ingameState.update();
						MPStatistic.getInstance().IngameStateUpdate.End();
						VehicleManager.instance.serverUpdate();
					} catch (Exception exception5) {
						exception5.printStackTrace();
					}

					int13 = 0;
					int14 = 0;
					int int15;
					for (int15 = 0; int15 < Players.size(); ++int15) {
						IsoPlayer player = (IsoPlayer)Players.get(int15);
						if (CheckPlayerStillValid(player)) {
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
					UdpConnection udpConnection2;
					for (int15 = 0; int15 < udpEngine.connections.size(); ++int15) {
						udpConnection2 = (UdpConnection)udpEngine.connections.get(int15);
						for (int int16 = 0; int16 < 4; ++int16) {
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

					for (int15 = 0; int15 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++int15) {
						IsoMovingObject movingObject = (IsoMovingObject)IsoWorld.instance.CurrentCell.getObjectList().get(int15);
						if (movingObject instanceof IsoPlayer && !Players.contains(movingObject)) {
							DebugLog.log("Disconnected player in CurrentCell.getObjectList() removed");
							IsoWorld.instance.CurrentCell.getObjectList().remove(int15--);
						}
					}

					++int2;
					if (int2 > 150) {
						for (int15 = 0; int15 < udpEngine.connections.size(); ++int15) {
							udpConnection2 = (UdpConnection)udpEngine.connections.get(int15);
							try {
								if (udpConnection2.username == null && !udpConnection2.awaitingCoopApprove) {
									disconnect(udpConnection2);
									udpEngine.forceDisconnect(udpConnection2.getConnectedGUID());
								}
							} catch (Exception exception6) {
								exception6.printStackTrace();
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
					} catch (Exception exception7) {
						exception7.printStackTrace();
					}

					long long7 = System.nanoTime();
					long long8 = long7 - long4;
					long long9 = long3 - long8 - long1;
					MPStatistic.getInstance().Main.StartSleep();
					if (long9 > 0L) {
						try {
							Thread.sleep(long9 / 1000000L);
						} catch (InterruptedException interruptedException2) {
						}

						long1 = System.nanoTime() - long7 - long9;
					} else {
						long2 -= long9;
						long1 = 0L;
						++int11;
						if (int11 >= 5) {
							Thread.yield();
							int11 = 0;
						}
					}

					MPStatistic.getInstance().Main.EndSleep();
					long4 = System.nanoTime();
					long long10 = System.currentTimeMillis();
					long long11 = long10 - long5;
					float2 = 1000.0F / (float)long11;
					if (!Float.isNaN(float2)) {
						float3 = (float)((double)float3 + Math.min((double)(float2 - float3) * 0.05, 1.0));
					}

					GameTime.instance.FPSMultiplier = 60.0F / float3;
					launchCommandHandler();
					MPStatistic.getInstance().process(long11);
					if (!SteamUtils.isSteamModeEnabled()) {
						PublicServerUtil.update();
						PublicServerUtil.updatePlayerCountIfChanged();
					}

					for (int int17 = 0; int17 < udpEngine.connections.size(); ++int17) {
						UdpConnection udpConnection3 = (UdpConnection)udpEngine.connections.get(int17);
						if (udpConnection3.accessLevel.equals("admin") && udpConnection3.sendPulse && udpConnection3.isFullyConnected()) {
							ByteBufferWriter byteBufferWriter = udpConnection3.startPacket();
							PacketTypes.doPacket((short)1, byteBufferWriter);
							byteBufferWriter.putLong(System.currentTimeMillis());
							udpConnection3.endPacket();
						}

						if (udpConnection3.checksumState == UdpConnection.ChecksumState.Different && udpConnection3.checksumTime + 8000L < System.currentTimeMillis()) {
							DebugLog.log("timed out connection because checksum was different");
							udpConnection3.checksumState = UdpConnection.ChecksumState.Init;
							udpConnection3.forceDisconnect();
						} else if (!udpConnection3.chunkObjectState.isEmpty()) {
							for (int int18 = 0; int18 < udpConnection3.chunkObjectState.size(); int18 += 2) {
								short short2 = udpConnection3.chunkObjectState.get(int18);
								short short3 = udpConnection3.chunkObjectState.get(int18 + 1);
								if (!udpConnection3.RelevantTo((float)(short2 * 10 + 5), (float)(short3 * 10 + 5), (float)(udpConnection3.ChunkGridWidth * 4 * 10))) {
									udpConnection3.chunkObjectState.remove(int18, 2);
									int18 -= 2;
								}
							}
						}
					}

					if (CoopSlave.instance != null) {
						CoopSlave.instance.update();
						if (CoopSlave.instance.masterLost()) {
							DebugLog.log("Coop master is not responding, terminating");
							ServerMap.instance.QueueQuit();
						}
					}

					SteamUtils.runLoop();
					GameWindow.fileSystem.updateAsyncTransactions();
				} finally {
					GameServer.s_performance.frameStep.end();
				}
			}

			CoopSlave.status(Translator.getText("UI_ServerStatus_Terminated"));
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
			System.out.println(string);
			String string2 = "admin";
			String string3 = "admin";
			if (udpConnection != null) {
				string2 = udpConnection.username;
				string3 = udpConnection.accessLevel;
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

	private static void teleport(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		String string = GameWindow.ReadString(byteBuffer);
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		IsoPlayer player = getPlayerByRealUserName(string);
		if (player != null) {
			UdpConnection udpConnection2 = getConnectionFromPlayer(player);
			if (udpConnection2 != null) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)108, byteBufferWriter);
				byteBufferWriter.putByte((byte)player.PlayerIndex);
				byteBufferWriter.putFloat(float1);
				byteBufferWriter.putFloat(float2);
				byteBufferWriter.putFloat(float3);
				udpConnection2.endPacketImmediate();
			}
		}
	}

	public static void sendPlayerExtraInfo(IsoPlayer player, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
			PacketTypes.doPacket((short)84, byteBufferWriter);
			byteBufferWriter.putShort((short)player.OnlineID);
			byteBufferWriter.putUTF(player.accessLevel);
			byteBufferWriter.putByte((byte)(player.isGodMod() ? 1 : 0));
			byteBufferWriter.putByte((byte)(player.isGhostMode() ? 1 : 0));
			byteBufferWriter.putByte((byte)(player.isInvisible() ? 1 : 0));
			byteBufferWriter.putByte((byte)(player.isNoClip() ? 1 : 0));
			byteBufferWriter.putByte((byte)(player.isShowAdminTag() ? 1 : 0));
			udpConnection2.endPacketImmediate();
		}
	}

	private static void receivePlayerExtraInfo(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		String string = GameWindow.ReadString(byteBuffer);
		boolean boolean1 = byteBuffer.get() == 1;
		boolean boolean2 = byteBuffer.get() == 1;
		boolean boolean3 = byteBuffer.get() == 1;
		boolean boolean4 = byteBuffer.get() == 1;
		boolean boolean5 = byteBuffer.get() == 1;
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			udpConnection.accessLevel = string;
			player.setGodMod(boolean1);
			player.setGhostMode(boolean2);
			player.setInvisible(boolean3);
			player.setNoClip(boolean4);
			player.setShowAdminTag(boolean5);
			sendPlayerExtraInfo(player, udpConnection);
		}
	}

	private static void addXpFromPlayerStatsUI(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (canModifyPlayerStats(udpConnection)) {
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getInt());
			int int1 = byteBuffer.getInt();
			int int2 = 0;
			int int3 = 0;
			boolean boolean1 = false;
			if (player != null && !player.isDead() && int1 == 0) {
				int3 = byteBuffer.getInt();
				int2 = byteBuffer.getInt();
				boolean1 = byteBuffer.get() == 1;
				player.getXp().AddXP(PerkFactory.Perks.fromIndex(int3), (float)int2, false, boolean1, false, true);
			}

			for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(player)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)124, byteBufferWriter);
					byteBufferWriter.putInt(player.getOnlineID());
					if (int1 == 0) {
						byteBufferWriter.putInt(0);
						byteBufferWriter.putInt(int3);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
					}

					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static boolean canSeePlayerStats(UdpConnection udpConnection) {
		return !udpConnection.accessLevel.equals("");
	}

	private static boolean canModifyPlayerStats(UdpConnection udpConnection) {
		return udpConnection.accessLevel.equals("admin") || udpConnection.accessLevel.equals("moderator") || udpConnection.accessLevel.equals("overseer");
	}

	private static void syncXp(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (canModifyPlayerStats(udpConnection)) {
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getInt());
			if (player != null && !player.isDead()) {
				try {
					player.getXp().load(byteBuffer, 184);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

				for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(player)) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)126, byteBufferWriter);
						byteBufferWriter.putInt(player.getOnlineID());
						try {
							player.getXp().save(byteBufferWriter.bb);
						} catch (IOException ioException2) {
							ioException2.printStackTrace();
						}

						udpConnection2.endPacketImmediate();
					}
				}
			}
		}
	}

	private static void receivePlayerStatsChanges(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			String string = GameWindow.ReadString(byteBuffer);
			player.setPlayerStats(byteBuffer, string);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					if (udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(player)) {
						udpConnection2.allChatMuted = player.isAllChatMuted();
						udpConnection2.accessLevel = player.accessLevel;
					}

					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					player.createPlayerStats(byteBufferWriter, string);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	public static void doMinimumInit() throws IOException {
		Rand.init();
		ZomboidFileSystem.instance.init();
		DebugFileWatcher.instance.init();
		ArrayList arrayList = new ArrayList(ServerMods);
		ZomboidFileSystem.instance.loadMods(arrayList);
		LuaManager.init();
		Languages.instance.init();
		Translator.loadFiles();
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
		if (GUICommandline && System.getProperty("softreset") == null) {
			ServerGUI.init();
		}

		CustomSandboxOptions.instance.init();
		CustomSandboxOptions.instance.initInstance(SandboxOptions.instance);
		ScriptManager.instance.Load();
		ClothingDecals.init();
		BeardStyles.init();
		HairStyles.init();
		OutfitManager.init();
		JAssImpImporter.Init();
		ModelManager.NoOpenGL = !ServerGUI.isCreated();
		ModelManager.instance.create();
		System.out.println("LOADING ASSETS: START");
		while (GameWindow.fileSystem.hasWork()) {
			GameWindow.fileSystem.updateAsyncTransactions();
		}

		System.out.println("LOADING ASSETS: FINISH");
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
			SandboxOptions.instance.loadServerLuaFile(ServerName);
			SandboxOptions.instance.handleOldServerZombiesFile();
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

		udpEngine = new UdpEngine(DEFAULT_PORT, ServerOptions.instance.MaxPlayers.getValue(), string, true);
		DebugLog.log(DebugType.Network, "*** SERVER STARTED ****");
		DebugLog.log(DebugType.Network, "*** Steam is " + (SteamUtils.isSteamModeEnabled() ? "enabled" : "not enabled"));
		DebugLog.log(DebugType.Network, "server is listening on port " + DEFAULT_PORT);
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
			CoopSlave.status("Server Started");
		} else {
			CoopSlave.status("Server Started");
		}

		string2 = ServerOptions.instance.DiscordChannel.getValue();
		String string4 = ServerOptions.instance.DiscordToken.getValue();
		boolean boolean1 = ServerOptions.instance.DiscordEnable.getValue();
		String string5 = ServerOptions.instance.DiscordChannelID.getValue();
		discordBot.connect(boolean1, string4, string2, string5);
	}

	private static void mainLoopDealWithNetData(ZomboidNetData zomboidNetData) {
	}

	private static void invMngRemoveItem(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		long long1 = byteBuffer.getLong();
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(player)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)157, byteBufferWriter);
					byteBufferWriter.putLong(long1);
					udpConnection2.endPacketImmediate();
					break;
				}
			}
		}
	}

	private static void invMngGotItem(ByteBuffer byteBuffer, UdpConnection udpConnection) throws IOException {
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(player)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)156, byteBufferWriter);
					byteBuffer.rewind();
					byteBufferWriter.bb.put(byteBuffer);
					udpConnection2.endPacketImmediate();
					break;
				}
			}
		}
	}

	private static void invMngSendItem(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		long long1 = 0L;
		String string = null;
		if (byteBuffer.get() == 1) {
			string = GameWindow.ReadString(byteBuffer);
		} else {
			long1 = byteBuffer.getLong();
		}

		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int2);
		if (player != null) {
			for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(player)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)155, byteBufferWriter);
					if (string != null) {
						byteBufferWriter.putByte((byte)1);
						byteBufferWriter.putUTF(string);
					} else {
						byteBufferWriter.putByte((byte)0);
						byteBufferWriter.putLong(long1);
					}

					byteBufferWriter.putLong((long)int1);
					udpConnection2.endPacketImmediate();
					break;
				}
			}
		}
	}

	private static void sendInventory(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		Long Long1 = (Long)IDToAddressMap.get(int1);
		if (Long1 != null) {
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() == Long1) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)154, byteBufferWriter);
					byteBufferWriter.bb.put(byteBuffer);
					udpConnection2.endPacketImmediate();
					break;
				}
			}
		}
	}

	private static void requestInventory(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		Long Long1 = (Long)IDToAddressMap.get(int2);
		if (Long1 != null) {
			for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
				if (udpConnection2.getConnectedGUID() == Long1) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)153, byteBufferWriter);
					byteBufferWriter.putInt(int1);
					udpConnection2.endPacketImmediate();
					break;
				}
			}
		}
	}

	public static void receiveZombieAttackTarget(ByteBuffer byteBuffer) {
		ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
		short short1 = byteBufferReader.getShort();
		short short2 = byteBufferReader.getShort();
		byte byte1 = byteBufferReader.getByte();
		String string = byteBufferReader.getUTF();
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)216, byteBufferWriter);
			byteBufferWriter.putShort(short1);
			byteBufferWriter.putShort(short2);
			byteBufferWriter.putByte(byte1);
			byteBufferWriter.putUTF(string);
			udpConnection.endPacketImmediate();
		}
	}

	public static void receiveEventUpdate(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		try {
			EventUpdatePacket eventUpdatePacket = EventUpdatePacket.l_receive.eventUpdatePacket;
			eventUpdatePacket.parse(byteBuffer);
			Iterator iterator = udpEngine.connections.iterator();
			while (iterator.hasNext()) {
				UdpConnection udpConnection2 = (UdpConnection)iterator.next();
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo(eventUpdatePacket.event.x, eventUpdatePacket.event.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)210, byteBufferWriter);
					eventUpdatePacket.write(byteBufferWriter);
					udpConnection2.endPacketSuperHighUnreliable();
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void receiveStatistic(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		try {
			udpConnection.statistic.parse(byteBuffer);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void receiveStatisticRequest(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (!udpConnection.accessLevel.equals("admin") && !Core.bDebug) {
			DebugLog.General.error("User " + udpConnection.username + " has no rights to access statistics.");
		} else {
			try {
				udpConnection.statistic.enable = byteBuffer.get();
				int int1 = byteBuffer.getInt();
				MPStatistic.getInstance().setPeriod(int1);
				sendStatistic(udpConnection);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
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
			PacketTypes.doPacket((short)212, byteBufferWriter);
			MPStatistic.getInstance().write(byteBufferWriter);
			udpConnection.endPacketImmediate();
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
		PacketTypes.doPacket((short)212, byteBufferWriter);
		try {
			MPStatistic.getInstance().getStatisticTable(byteBufferWriter.bb);
			udpConnection.endPacketImmediate();
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
				PacketTypes.doPacket((short)211, byteBufferWriter);
				byteBufferWriter.putLong(System.currentTimeMillis());
				udpConnection.endPacketSuperHighUnreliable();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void receivePlayerUpdate(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (udpConnection.checksumState != UdpConnection.ChecksumState.Done) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)83, byteBufferWriter);
			byteBufferWriter.putUTF("You have been kicked from this server.");
			udpConnection.endPacketImmediate();
			udpConnection.forceDisconnect();
		} else {
			PlayerPacket playerPacket = PlayerPacket.l_receive.playerPacket;
			playerPacket.parse(byteBuffer);
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(playerPacket.id));
			if (player.replay != null) {
				player.replay.recordPlayerPacket(playerPacket);
				if (player.replay.isPlay()) {
					return;
				}
			}

			try {
				if (player == null) {
					DebugLog.General.error("receivePlayerUpdate: Server received position for unknown player (id:" + playerPacket.id + "). Server will ignore this data.");
				} else {
					player.networkAI.parse(playerPacket);
					if (player.networkAI.distance.getLength() > (float)IsoChunkMap.ChunkWidthInTiles) {
						MPStatistic.getInstance().teleport();
					}

					RakVoice.SetPlayerCoordinate(udpConnection.getConnectedGUID(), playerPacket.realx, playerPacket.realy, (float)playerPacket.realz, player.isInvisible());
					udpConnection.ReleventPos[player.PlayerIndex].x = playerPacket.realx;
					udpConnection.ReleventPos[player.PlayerIndex].y = playerPacket.realy;
					udpConnection.ReleventPos[player.PlayerIndex].z = (float)playerPacket.realz;
					playerPacket.id = (short)player.getOnlineID();
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
					playerToCoordsMap.put(Integer.valueOf(playerPacket.id), vector2);
				} else {
					if (!player.accessLevel.equals("") && !player.isGhostMode() && (Math.abs(playerPacket.x - vector2.x) > 4.0F || Math.abs(playerPacket.y - vector2.y) > 4.0F)) {
						if (playerMovedToFastMap.get(Integer.valueOf(playerPacket.id)) == null) {
							playerMovedToFastMap.put(Integer.valueOf(playerPacket.id), 1);
						} else {
							playerMovedToFastMap.put(Integer.valueOf(playerPacket.id), (Integer)playerMovedToFastMap.get(Integer.valueOf(playerPacket.id)) + 1);
						}

						ZLogger zLogger = LoggerManager.getLogger("admin");
						String string = player.getDisplayName();
						zLogger.write(string + " go too fast (" + playerMovedToFastMap.get(Integer.valueOf(playerPacket.id)) + " times)");
						if ((Integer)playerMovedToFastMap.get(Integer.valueOf(playerPacket.id)) == 10) {
							LoggerManager.getLogger("admin").write(player.getDisplayName() + " kicked for going too fast");
							ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
							PacketTypes.doPacket((short)83, byteBufferWriter2);
							byteBufferWriter2.putUTF("You have been kicked from this server.");
							udpConnection.endPacketImmediate();
							udpConnection.forceDisconnect();
							return;
						}
					}

					vector2.x = playerPacket.x;
					vector2.y = playerPacket.y;
				}
			}

			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection.getConnectedGUID() != udpConnection2.getConnectedGUID() && (udpConnection2.ReleventTo(playerPacket.x, playerPacket.y) || !ServerOptions.instance.DoLuaChecksum.getValue() && !udpConnection2.accessLevel.isEmpty()) && udpConnection2.isFullyConnected()) {
					ByteBufferWriter byteBufferWriter3 = udpConnection2.startPacket();
					PacketTypes.doPacket((short)218, byteBufferWriter3);
					byteBuffer.position(0);
					byteBufferWriter3.bb.put(byteBuffer);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void receivePacketCounts(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (!udpConnection.accessLevel.isEmpty()) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)47, byteBufferWriter);
			for (int int1 = 0; int1 < 256; ++int1) {
				byteBufferWriter.putLong(packetCounts[int1]);
			}

			udpConnection.endPacket();
		}
	}

	private static void receiveSandboxOptions(ByteBuffer byteBuffer) {
		try {
			SandboxOptions.instance.load(byteBuffer);
			SandboxOptions.instance.applySettings();
			SandboxOptions.instance.toLua();
			SandboxOptions.instance.saveServerLuaFile(ServerName);
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)31, byteBufferWriter);
				byteBuffer.rewind();
				byteBufferWriter.bb.put(byteBuffer);
				udpConnection.endPacket();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static void receiveChunkObjectState(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		short short2 = byteBuffer.getShort();
		IsoChunk chunk = ServerMap.instance.getChunk(short1, short2);
		if (chunk == null) {
			udpConnection.chunkObjectState.add(short1);
			udpConnection.chunkObjectState.add(short2);
		} else {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)151, byteBufferWriter);
			byteBufferWriter.putShort(short1);
			byteBufferWriter.putShort(short2);
			try {
				if (chunk.saveObjectState(byteBufferWriter.bb)) {
					udpConnection.endPacket();
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

	private static void readAnnotedMap(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		String string = GameWindow.ReadString(byteBuffer);
		StashSystem.prepareBuildingStash(string);
	}

	private static void tradingUIRemoveItem(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		Long Long1 = (Long)IDToAddressMap.get(int2);
		if (Long1 != null) {
			for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
				if (udpConnection2.getConnectedGUID() == Long1) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)148, byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int3);
					udpConnection2.endPacketImmediate();
					break;
				}
			}
		}
	}

	private static void tradingUIUpdateState(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		Long Long1 = (Long)IDToAddressMap.get(int2);
		if (Long1 != null) {
			for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
				if (udpConnection2.getConnectedGUID() == Long1) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)149, byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int3);
					udpConnection2.endPacketImmediate();
					break;
				}
			}
		}
	}

	private static void tradingUIAddItem(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		InventoryItem inventoryItem = null;
		try {
			inventoryItem = InventoryItem.loadItem(byteBuffer, 184);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (inventoryItem != null) {
			Long Long1 = (Long)IDToAddressMap.get(int2);
			if (Long1 != null) {
				for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
					if (udpConnection2.getConnectedGUID() == Long1) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)147, byteBufferWriter);
						byteBufferWriter.putInt(int1);
						try {
							inventoryItem.saveWithSize(byteBufferWriter.bb, false);
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}

						udpConnection2.endPacketImmediate();
						break;
					}
				}
			}
		}
	}

	private static void requestTrading(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		byte byte1 = byteBuffer.get();
		Long Long1 = (Long)IDToAddressMap.get(int1);
		if (byte1 == 0) {
			Long1 = (Long)IDToAddressMap.get(int2);
		}

		if (Long1 != null) {
			for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
				if (udpConnection2.getConnectedGUID() == Long1) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)146, byteBufferWriter);
					if (byte1 == 0) {
						byteBufferWriter.putInt(int1);
					} else {
						byteBufferWriter.putInt(int2);
					}

					byteBufferWriter.putByte(byte1);
					udpConnection2.endPacketImmediate();
					break;
				}
			}
		}
	}

	private static void syncFaction(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
				PacketTypes.doPacket((short)140, byteBufferWriter);
				faction.writeToBuffer(byteBufferWriter, boolean2);
				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void syncNonPvpZone(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		String string = GameWindow.ReadString(byteBuffer);
		NonPvpZone nonPvpZone = NonPvpZone.getZoneByTitle(string);
		if (nonPvpZone == null) {
			nonPvpZone = NonPvpZone.addNonPvpZone(string, int1, int2, int3, int4);
		}

		if (nonPvpZone != null) {
			boolean boolean1 = byteBuffer.get() == 1;
			sendNonPvpZone(nonPvpZone, boolean1, udpConnection);
			if (boolean1) {
				NonPvpZone.removeNonPvpZone(string, true);
				DebugLog.log("non pvp zone: removed " + int1 + "," + int2 + ", ttle=" + nonPvpZone.getTitle());
			}
		}
	}

	public static void sendNonPvpZone(NonPvpZone nonPvpZone, boolean boolean1, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)139, byteBufferWriter);
				byteBufferWriter.putInt(nonPvpZone.getX());
				byteBufferWriter.putInt(nonPvpZone.getY());
				byteBufferWriter.putInt(nonPvpZone.getX2());
				byteBufferWriter.putInt(nonPvpZone.getY2());
				byteBufferWriter.putUTF(nonPvpZone.getTitle());
				byteBufferWriter.putBoolean(boolean1);
				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void receiveTextColor(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			float float1 = byteBuffer.getFloat();
			float float2 = byteBuffer.getFloat();
			float float3 = byteBuffer.getFloat();
			player.setSpeakColourInfo(new ColorInfo(float1, float2, float3, 1.0F));
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)138, byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putFloat(float1);
					byteBufferWriter.putFloat(float2);
					byteBufferWriter.putFloat(float3);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void receiveTransactionID(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int1);
		if (player != null) {
			transactionIDMap.put(player.username, int2);
			player.setTransactionID(int2);
			ServerWorldDatabase.instance.saveTransactionID(player.username, int2);
		}
	}

	private static void syncCompost(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
			if (udpConnection2.ReleventTo((float)compost.square.x, (float)compost.square.y) && (udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() || udpConnection == null)) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)122, byteBufferWriter);
				byteBufferWriter.putInt(compost.square.x);
				byteBufferWriter.putInt(compost.square.y);
				byteBufferWriter.putInt(compost.square.z);
				byteBufferWriter.putFloat(compost.getCompost());
				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void doCataplasm(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
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
					PacketTypes.doPacket((short)117, byteBufferWriter);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putFloat(float1);
					byteBufferWriter.putFloat(float2);
					byteBufferWriter.putFloat(float3);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void destroy(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (ServerOptions.instance.AllowDestructionBySledgehammer.getValue()) {
			RemoveItemFromMap(byteBuffer, udpConnection);
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
			PacketTypes.doPacket((short)110, byteBufferWriter);
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
			udpConnection.endPacketImmediate();
		}
	}

	private static void AddExplosiveTrap(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			InventoryItem inventoryItem = null;
			try {
				inventoryItem = InventoryItem.loadItem(byteBuffer, 184);
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
			IsoTrap trap = new IsoTrap(handWeapon, square.getCell(), square);
			if (handWeapon.getExplosionTimer() <= 0 && handWeapon.getSensorRange() <= 0 && handWeapon.getRemoteControlID() == -1) {
				trap.triggerExplosion(false);
				trap.removeFromWorld();
			} else {
				square.AddTileObject(trap);
				for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)110, byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						try {
							handWeapon.saveWithSize(byteBufferWriter.bb, false);
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}

						udpConnection2.endPacketImmediate();
					}
				}
			}
		}
	}

	public static void sendHelicopter(float float1, float float2, boolean boolean1) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)11, byteBufferWriter);
			byteBufferWriter.putFloat(float1);
			byteBufferWriter.putFloat(float2);
			byteBufferWriter.putBoolean(boolean1);
			udpConnection.endPacketImmediate();
		}
	}

	private static void registerZone(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
					PacketTypes.doPacket((short)94, byteBufferWriter);
					byteBufferWriter.putUTF(string);
					byteBufferWriter.putUTF(string2);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					byteBufferWriter.putInt(int4);
					byteBufferWriter.putInt(int5);
					byteBufferWriter.putInt(int6);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	public static void sendZone(IsoMetaGrid.Zone zone, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)94, byteBufferWriter);
				byteBufferWriter.putUTF(zone.name);
				byteBufferWriter.putUTF(zone.type);
				byteBufferWriter.putInt(zone.x);
				byteBufferWriter.putInt(zone.y);
				byteBufferWriter.putInt(zone.z);
				byteBufferWriter.putInt(zone.w);
				byteBufferWriter.putInt(zone.h);
				byteBufferWriter.putInt(zone.lastActionTimestamp);
				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void constructedZone(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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

			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)89, byteBufferWriter);
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			byteBufferWriter.putInt(perk.index());
			byteBufferWriter.putInt(int1);
			udpConnection.endPacketImmediate();
		}
	}

	private static void log(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		LoggerManager.getLogger(GameWindow.ReadString(byteBuffer)).write(GameWindow.ReadString(byteBuffer));
	}

	private static void answerPing(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		String string = GameWindow.ReadString(byteBuffer);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)87, byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putInt(udpEngine.connections.size());
				byteBufferWriter.putInt(512);
				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void updateItemSprite(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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

	private static void sendWorldMessage(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (!udpConnection.allChatMuted) {
			String string = GameWindow.ReadString(byteBuffer);
			String string2 = GameWindow.ReadString(byteBuffer);
			if (string2.length() > 256) {
				string2 = string2.substring(0, 256);
			}

			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)79, byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putUTF(string2);
				udpConnection2.endPacketImmediate();
			}

			discordBot.sendMessage(string, string2);
			LoggerManager.getLogger("chat").write(udpConnection.index + " \"" + udpConnection.username + "\" A \"" + string2 + "\"");
		}
	}

	private static void sendCustomModDataToClient(UdpConnection udpConnection) {
		LuaEventManager.triggerEvent("SendCustomModData");
	}

	public static void stopFire(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		short short1;
		if (byte1 == 1) {
			short1 = byteBuffer.getShort();
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
			if (player != null) {
				player.sendObjectChange("StopBurning");
			}
		} else if (byte1 == 2) {
			short1 = byteBuffer.getShort();
			IsoZombie zombie = ServerMap.instance.ZombieMap.get(short1);
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
					if (udpConnection2.ReleventTo((float)int1, (float)int2) && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)116, byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						udpConnection2.endPacketImmediate();
					}
				}
			}
		}
	}

	public static void startFireOnClient(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		int int5 = byteBuffer.getInt();
		boolean boolean2 = byteBuffer.get() == 1;
		if (!boolean2 && ServerOptions.instance.NoFire.getValue()) {
			DebugLog.log("user \"" + udpConnection.username + "\" tried to start a fire");
		} else {
			IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
			if (square != null) {
				IsoFire fire = boolean2 ? new IsoFire(square.getCell(), square, boolean1, int4, int5, true) : new IsoFire(square.getCell(), square, boolean1, int4, int5);
				IsoFireManager.Add(fire);
				square.getObjects().add(fire);
				for (int int6 = 0; int6 < udpEngine.connections.size(); ++int6) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int6);
					if (udpConnection2.ReleventTo((float)int1, (float)int2)) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)75, byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						byteBufferWriter.putInt(int4);
						byteBufferWriter.putBoolean(boolean1);
						byteBufferWriter.putInt(fire.SpreadDelay);
						byteBufferWriter.putInt(fire.Life);
						byteBufferWriter.putInt(fire.numFlameParticles);
						byteBufferWriter.putBoolean(boolean2);
						udpConnection2.endPacketImmediate();
					}
				}
			}
		}
	}

	public static void startFireOnClient(IsoGridSquare square, int int1, boolean boolean1, int int2, boolean boolean2) {
		IsoFire fire = boolean2 ? new IsoFire(square.getCell(), square, boolean1, int1, int2, true) : new IsoFire(square.getCell(), square, boolean1, int1, int2);
		IsoFireManager.Add(fire);
		square.getObjects().add(fire);
		for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int3);
			if (udpConnection.ReleventTo((float)square.getX(), (float)square.getY())) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)75, byteBufferWriter);
				byteBufferWriter.putInt(square.getX());
				byteBufferWriter.putInt(square.getY());
				byteBufferWriter.putInt(square.getZ());
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putBoolean(boolean1);
				byteBufferWriter.putInt(fire.SpreadDelay);
				byteBufferWriter.putInt(fire.Life);
				byteBufferWriter.putInt(fire.numFlameParticles);
				byteBufferWriter.putBoolean(boolean2);
				udpConnection.endPacketImmediate();
			}
		}
	}

	public static void sendOptionsToClients() {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)82, byteBufferWriter);
			byteBufferWriter.putInt(ServerOptions.instance.getPublicOptions().size());
			String string = null;
			Iterator iterator = ServerOptions.instance.getPublicOptions().iterator();
			while (iterator.hasNext()) {
				string = (String)iterator.next();
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putUTF(ServerOptions.instance.getOption(string));
			}

			udpConnection.endPacketImmediate();
		}
	}

	public static void sendCorpse(IsoDeadBody deadBody) {
		IsoGridSquare square = deadBody.getSquare();
		if (square != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection.ReleventTo((float)square.x, (float)square.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.doPacket((short)69, byteBufferWriter);
					byteBufferWriter.putInt(square.x);
					byteBufferWriter.putInt(square.y);
					byteBufferWriter.putInt(square.z);
					deadBody.writeToRemoteBuffer(byteBufferWriter);
					udpConnection.endPacketImmediate();
				}
			}
		}
	}

	private static void addCorpseToMap(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoObject object = WorldItemTypes.createFromBuffer(byteBuffer);
		if (object != null && object instanceof IsoDeadBody) {
			object.loadFromRemoteBuffer(byteBuffer, false);
			IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
			if (square != null) {
				square.addCorpse((IsoDeadBody)object, true);
				for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo((float)int1, (float)int2)) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)69, byteBufferWriter);
						byteBuffer.rewind();
						byteBufferWriter.bb.put(byteBuffer);
						udpConnection2.endPacketImmediate();
					}
				}
			}

			LoggerManager.getLogger("item").write(udpConnection.idStr + " \"" + udpConnection.username + "\" corpse +1 " + int1 + "," + int2 + "," + int3);
		}
	}

	public static void removeCorpseFromMap(IsoDeadBody deadBody) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			if (deadBody.getSquare() != null && udpConnection.ReleventTo(deadBody.getX(), deadBody.getY())) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)68, byteBufferWriter);
				byteBufferWriter.putInt(deadBody.getSquare().getX());
				byteBufferWriter.putInt(deadBody.getSquare().getY());
				byteBufferWriter.putInt(deadBody.getSquare().getZ());
				byteBufferWriter.putInt(deadBody.getSquare().getStaticMovingObjects().indexOf(deadBody));
				byteBufferWriter.putInt(deadBody.getOnlineId());
				udpConnection.endPacketImmediate();
			}
		}

		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(deadBody.getOnlineId());
		if (player != null) {
			PlayerToBody.remove(player);
		}
	}

	private static void removeCorpseFromMap(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		int int5 = byteBuffer.getInt();
		IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
		if (square != null && int4 >= 0 && int4 < square.getStaticMovingObjects().size()) {
			IsoObject object = (IsoObject)square.getStaticMovingObjects().get(int4);
			square.removeCorpse((IsoDeadBody)object, true);
			for (int int6 = 0; int6 < udpEngine.connections.size(); ++int6) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int6);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo((float)int1, (float)int2)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)68, byteBufferWriter);
					byteBuffer.rewind();
					byteBufferWriter.bb.put(byteBuffer);
					udpConnection2.endPacketImmediate();
				}
			}
		}

		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(int5);
		if (player != null) {
			PlayerToBody.remove(player);
		}

		LoggerManager.getLogger("item").write(udpConnection.idStr + " \"" + udpConnection.username + "\" corpse -1 " + int1 + "," + int2 + "," + int3);
	}

	private static void sendPlayerConnect(IsoPlayer player, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)6, byteBufferWriter);
		if (udpConnection.getConnectedGUID() != (Long)PlayerToAddressMap.get(player)) {
			byteBufferWriter.putShort((short)player.OnlineID);
		} else {
			byteBufferWriter.putShort((short)-1);
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			byteBufferWriter.putShort((short)player.OnlineID);
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
		byteBufferWriter.putByte((byte)(player.isSafety() ? 1 : 0));
		byteBufferWriter.putUTF(player.accessLevel);
		byteBufferWriter.putByte((byte)(player.isInvisible() ? 1 : 0));
		if (udpConnection.getConnectedGUID() != (Long)PlayerToAddressMap.get(player) && canSeePlayerStats(udpConnection)) {
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
		udpConnection.endPacketImmediate();
		if (udpConnection.getConnectedGUID() != (Long)PlayerToAddressMap.get(player)) {
			updateHandEquips(udpConnection, player);
		}
	}

	private static void RequestPlayerData(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(byteBuffer.getShort()));
		if (player != null) {
			sendPlayerConnect(player, udpConnection);
		}
	}

	private static void SyncPlayerInventory(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			DebugLog.log("SyncPlayerInventory " + player.username);
			player.setInventory(new ItemContainer());
			player.clearWornItems();
			int int1;
			try {
				ArrayList arrayList = player.getInventory().load(byteBuffer, 184);
				byte byte2 = byteBuffer.get();
				for (int1 = 0; int1 < byte2; ++int1) {
					String string = GameWindow.ReadString(byteBuffer);
					short short1 = byteBuffer.getShort();
					if (short1 >= 0 && short1 < arrayList.size() && player.getBodyLocationGroup().getLocation(string) != null) {
						player.setWornItem(string, (InventoryItem)arrayList.get(short1));
					}
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			IsoDeadBody deadBody = (IsoDeadBody)PlayerToBody.get(player);
			if (deadBody != null) {
				deadBody.setContainer(player.getInventory());
				deadBody.setWornItems(player.getWornItems());
				player.setInventory(new ItemContainer());
				player.clearWornItems();
				int int2 = (int)player.x;
				int1 = (int)player.y;
				for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo((float)int2, (float)int1)) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)65, byteBufferWriter);
						byteBuffer.rewind();
						byteBufferWriter.putShort((short)player.OnlineID);
						byteBufferWriter.bb.put(byteBuffer);
						udpConnection2.endPacketImmediate();
					}
				}

				PlayerToBody.remove(player);
			}
		}
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
			if (udpConnection.ReleventTo((float)square.getX(), (float)square.getY())) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)51, byteBufferWriter);
				byteBufferWriter.putInt(square.getX());
				byteBufferWriter.putInt(square.getY());
				byteBufferWriter.putInt(square.getZ());
				try {
					square.getModData().save(byteBufferWriter.bb);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

				udpConnection.endPacketImmediate();
			}
		}
	}

	private static void loadModData(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
		if (square != null) {
			try {
				square.getModData().load((ByteBuffer)byteBuffer, 184);
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
					if (udpConnection2.ReleventTo((float)square.getX(), (float)square.getY()) && (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID())) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)51, byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						try {
							square.getModData().save(byteBufferWriter.bb);
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}

						udpConnection2.endPacketImmediate();
					}
				}
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}
		}
	}

	private static void receiveWeaponHit(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		IsoObject object = getIsoObjectRefFromByteBuffer(byteBuffer);
		short short1 = byteBuffer.getShort();
		String string = GameWindow.ReadStringUTF(byteBuffer);
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (object != null && player != null) {
			InventoryItem inventoryItem = null;
			if (!string.isEmpty()) {
				inventoryItem = InventoryItemFactory.CreateItem(string);
				if (inventoryItem == null || !(inventoryItem instanceof HandWeapon)) {
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

	private static void drink(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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

	private static void receivePlayerDeath(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		int int1 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		float float4 = byteBuffer.getFloat();
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			ChatServer.getInstance().disconnectPlayer(player.getOnlineID());
			ServerWorldDatabase.instance.saveTransactionID(player.username, 0);
			player.setTransactionID(0);
			transactionIDMap.put(player.username, 0);
			player.setX(float1);
			player.setY(float2);
			player.setZ(float3);
			player.setDir(int1);
			IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare((double)float1, (double)float2, (double)float3);
			if (square != null) {
				player.setCurrent(square);
			}

			player.setStateMachineLocked(false);
			player.setHealth(0.0F);
			player.getBodyDamage().setOverallBodyHealth(0.0F);
			player.getBodyDamage().setInfected(boolean1);
			player.getBodyDamage().setInfectionLevel(float4);
			player.setStateMachineLocked(false);
			player.setStateMachineLocked(true);
			if (!ServerOptions.instance.Open.getValue() && ServerOptions.instance.DropOffWhiteListAfterDeath.getValue() && player.accessLevel.equals("")) {
				try {
					ServerWorldDatabase.instance.removeUser(player.getUsername());
				} catch (SQLException sQLException) {
				}
			}

			try {
				player.getInventory().clear();
				player.getWornItems().clear();
				ArrayList arrayList = player.getInventory().load(byteBuffer, IsoWorld.getWorldVersion());
				byte byte2 = byteBuffer.get();
				for (int int2 = 0; int2 < byte2; ++int2) {
					String string = GameWindow.ReadString(byteBuffer);
					short short1 = byteBuffer.getShort();
					if (short1 >= 0 && short1 < arrayList.size() && player.getWornItems().getBodyLocationGroup().getLocation(string) != null) {
						player.getWornItems().setItem(string, (InventoryItem)arrayList.get(short1));
					}
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			SendDeath(udpConnection, player);
			IsoDeadBody deadBody = new IsoDeadBody(player);
			if (player.shouldBecomeZombieAfterDeath()) {
				deadBody.reanimateLater();
			}
		}
	}

	private static void receivePlayerOnBeaten(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(byte1));
		if (player != null) {
			SendOnBeaten(player, float1, float2, float3);
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

	private static void eatFood(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		float float1 = byteBuffer.getFloat();
		InventoryItem inventoryItem = null;
		try {
			inventoryItem = InventoryItem.loadItem(byteBuffer, 184);
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

	private static void doBandage(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
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
					PacketTypes.doPacket((short)42, byteBufferWriter);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putBoolean(boolean1);
					byteBufferWriter.putFloat(float1);
					byteBufferWriter.putBoolean(boolean2);
					GameWindow.WriteStringUTF(byteBufferWriter.bb, string);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void doStitch(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			float float1 = byteBuffer.getFloat();
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setStitched(boolean1);
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setStitchTime(float1);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)98, byteBufferWriter);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putBoolean(boolean1);
					byteBufferWriter.putFloat(float1);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void doWoundInfection(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setInfectedWound(boolean1);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)97, byteBufferWriter);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putBoolean(boolean1);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void doDisinfect(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			float float1 = byteBuffer.getFloat();
			BodyPart bodyPart = player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
			bodyPart.setAlcoholLevel(bodyPart.getAlcoholLevel() + float1);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)99, byteBufferWriter);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putFloat(float1);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void doSplint(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
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
					PacketTypes.doPacket((short)102, byteBufferWriter);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putBoolean(boolean1);
					if (boolean1) {
						byteBufferWriter.putUTF(string);
						byteBufferWriter.putFloat(float1);
					}

					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void doAdditionalPain(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			float float1 = byteBuffer.getFloat();
			BodyPart bodyPart = player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1));
			bodyPart.setAdditionalPain(bodyPart.getAdditionalPain() + float1);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)100, byteBufferWriter);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putFloat(float1);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void doRemoveGlass(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setHaveGlass(false);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)101, byteBufferWriter);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putInt(int1);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void doRemoveBullet(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setHaveBullet(false, int2);
			for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)103, byteBufferWriter);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void doCleanBurn(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
		if (player != null) {
			int int1 = byteBuffer.getInt();
			player.getBodyDamage().getBodyPart(BodyPartType.FromIndex(int1)).setNeedBurnWash(false);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)104, byteBufferWriter);
					byteBufferWriter.putShort(short1);
					byteBufferWriter.putInt(int1);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void receiveBodyDamageUpdate(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		BodyDamageSync.instance.serverPacket(byteBuffer);
	}

	private static void ReceiveCommand(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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

	public static void Chat(String string, UdpConnection udpConnection, boolean boolean1) {
		Chat(string, udpConnection, boolean1, (byte)-1);
	}

	public static void Chat(String string, UdpConnection udpConnection, boolean boolean1, byte byte1) {
		IsoPlayer player = null;
		if (udpConnection != null) {
			player = getAnyPlayerFromConnection(udpConnection);
			if (player == null) {
				return;
			}
		}

		if (udpConnection == null || !udpConnection.accessLevel.equals("") && !udpConnection.accessLevel.equals("Observer") && !udpConnection.accessLevel.equals("GM") || !string.startsWith("[SERVERMSG]")) {
			if (player != null && !string.startsWith("[SERVERMSG]")) {
				if (byte1 == 0) {
					player.Say(string);
				} else if (byte1 == 1) {
					player.SayWhisper(string);
				} else if (byte1 == 2) {
					player.SayShout(string);
				}
			}

			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection == null || udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && player != null && udpConnection2.ReleventTo(player.x, player.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)186, byteBufferWriter);
					byteBufferWriter.putInt(player != null ? player.OnlineID : -1);
					byteBufferWriter.putByte(byte1);
					byteBufferWriter.putUTF(string);
					byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
					udpConnection2.endPacketImmediate();
				}
			}

			if (!string.equals("ZzzZZZzzzz") && !ZomboidRadio.isStaticSound(string)) {
				ZLogger zLogger = LoggerManager.getLogger("chat");
				String string2 = udpConnection == null ? "" : udpConnection.idStr + " \"" + udpConnection.username;
				zLogger.write(string2 + "\": \"" + string + "\"");
			}
		}
	}

	private static void Chat(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		String string = GameWindow.ReadString(byteBuffer);
		if (string.length() > 256) {
			string = string.substring(0, 256);
		}

		Chat(string, udpConnection, true, byte1);
	}

	public static void doZomboidDataInMainLoop(ZomboidNetData zomboidNetData) {
		synchronized (MainLoopNetDataHighPrioritet) {
			MainLoopNetDataHighPrioritet.add(zomboidNetData);
		}
	}

	private static void hitCharacter(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		HitPacket hitPacket = new HitPacket();
		hitPacket.parse(byteBuffer);
		if (hitPacket.check()) {
			DebugLog.log(DebugType.Combat, hitPacket.getDescription());
			if (hitPacket.objType == 1) {
				IsoZombie zombie = (IsoZombie)hitPacket.zom;
				zombie.bKnockedDown = (hitPacket.zombieFlags & 1) != 0;
				zombie.setFakeDead((hitPacket.zombieFlags & 2) != 0);
				zombie.setHitFromBehind((hitPacket.zombieFlags & 4) != 0);
				zombie.bStaggerBack = (hitPacket.zombieFlags & 8) != 0;
				zombie.setVariable("bKnifeDeath", (hitPacket.zombieFlags & 16) != 0);
				zombie.setFallOnFront((hitPacket.zombieFlags & 32) != 0);
				zombie.networkAI.extraUpdate();
				if (hitPacket.helmetFall && !PersistentOutfits.instance.isHatFallen(zombie)) {
					PersistentOutfits.instance.setFallenHat(zombie, true);
					if (ServerGUI.isCreated()) {
						PersistentOutfits.instance.removeFallenHat(zombie.getPersistentOutfitID(), zombie);
						ModelManager.instance.ResetNextFrame(zombie);
					}
				}

				if (hitPacket.jawStabAttach) {
					zombie.setAttachedItem("JawStab", hitPacket.item);
					zombie.setVariable("bKnifeDeath", true);
				}
			} else if (hitPacket.objType == 2) {
			}

			IsoPlayer player = hitPacket.player;
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo(player.x, player.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)26, byteBufferWriter);
					hitPacket.write(byteBufferWriter);
					udpConnection2.endPacket();
				}
			}

			hitPacket.player.useChargeDelta = hitPacket.charge;
			if (hitPacket.objType != 4) {
				IsoGameCharacter gameCharacter = hitPacket.zom;
				BaseVehicle baseVehicle = hitPacket.vehicle;
				if (hitPacket.objType == 1) {
					IsoZombie zombie2 = (IsoZombie)gameCharacter;
					if (zombie2.getStateMachine().getCurrent() == ZombieOnGroundState.instance()) {
						zombie2.setReanimateTimer((float)(Rand.Next(60) + 30));
					}

					if (zombie2.getStateMachine().getCurrent() == ZombieGetUpState.instance()) {
						float float1 = 15.0F - gameCharacter.def.Frame;
						if (float1 < 2.0F) {
							float1 = 2.0F;
						}

						gameCharacter.def.Frame = float1;
						zombie2.setReanimateTimer((float)(Rand.Next(60) + 30));
					}
				}

				if (hitPacket.objType == 2) {
					ZLogger zLogger = LoggerManager.getLogger("pvp");
					String string = player.username;
					zLogger.write("user " + string + " " + LoggerManager.getPlayerCoords(player) + " hit user " + ((IsoPlayer)gameCharacter).username + " " + LoggerManager.getPlayerCoords((IsoPlayer)gameCharacter) + " with " + hitPacket.typeAsString);
				}

				if (baseVehicle == null) {
					gameCharacter.setX(hitPacket.tx);
					gameCharacter.setY(hitPacket.ty);
					gameCharacter.setCloseKilled(hitPacket.bCloseKilled);
					player.isCrit = hitPacket.isCrit;
					player.bDoShove = hitPacket.doShove;
					player.setAimAtFloor(hitPacket.isAimAtFloor);
					HandWeapon handWeapon = player.bareHands;
					if (hitPacket.item instanceof HandWeapon) {
						handWeapon = (HandWeapon)hitPacket.item;
					}

					if (hitPacket.zombieHitReaction != null && !hitPacket.zombieHitReaction.isEmpty()) {
						player.setVariable("ZombieHitReaction", hitPacket.zombieHitReaction);
					}

					gameCharacter.Hit(handWeapon, player, hitPacket.damageSplit, hitPacket.bIgnoreDamage, hitPacket.rangeDel, true);
					gameCharacter.setHitForce(hitPacket.ohit);
					gameCharacter.getHitDir().x = hitPacket.ohitx;
					gameCharacter.getHitDir().y = hitPacket.ohity;
					if (gameCharacter instanceof IsoZombie) {
						((IsoZombie)gameCharacter).bKnockedDown = (hitPacket.zombieFlags & 1) != 0;
						((IsoZombie)gameCharacter).setFakeDead((hitPacket.zombieFlags & 2) != 0);
						((IsoZombie)gameCharacter).setHitFromBehind((hitPacket.zombieFlags & 4) != 0);
						((IsoZombie)gameCharacter).bStaggerBack = (hitPacket.zombieFlags & 8) != 0;
						((IsoZombie)gameCharacter).setVariable("bKnifeDeath", (hitPacket.zombieFlags & 16) != 0);
						((IsoZombie)gameCharacter).setFallOnFront((hitPacket.zombieFlags & 32) != 0);
					}

					if (gameCharacter.hasAnimationPlayer() && gameCharacter.getAnimationPlayer().isReady() && !gameCharacter.getAnimationPlayer().isBoneTransformsNeedFirstFrame()) {
						gameCharacter.getAnimationPlayer().setAngle(hitPacket.angle);
					} else {
						gameCharacter.getForwardDirection().setDirection(hitPacket.angle);
					}

					if (hitPacket.hitReaction != null && !hitPacket.hitReaction.isEmpty()) {
						gameCharacter.setHitReaction(hitPacket.hitReaction);
					}

					if (hitPacket.dead && gameCharacter.isAlive()) {
						gameCharacter.setOnDeathDone(true);
						if (gameCharacter instanceof IsoZombie) {
							((IsoZombie)gameCharacter).DoZombieInventory();
						}

						gameCharacter.setHealth(0.0F);
						LuaEventManager.triggerEvent("OnZombieDead", gameCharacter);
						gameCharacter.DoDeath((HandWeapon)null, (IsoGameCharacter)null, false);
					}
				} else {
					baseVehicle.hitVehicle(player, (HandWeapon)hitPacket.item);
				}
			}
		}
	}

	private static void equip(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		byte byte3 = byteBuffer.get();
		InventoryItem inventoryItem = null;
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (byte3 == 1) {
			try {
				inventoryItem = InventoryItem.loadItem(byteBuffer, 184);
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
					inventoryItem.getVisual().load(byteBuffer, 184);
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
						PacketTypes.doPacket((short)25, byteBufferWriter);
						byteBufferWriter.putShort((short)player.OnlineID);
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

						udpConnection2.endPacketImmediate();
					}
				}
			}
		}
	}

	private static void scoreboard(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)50, byteBufferWriter);
		ArrayList arrayList = new ArrayList();
		ArrayList arrayList2 = new ArrayList();
		ArrayList arrayList3 = new ArrayList();
		int int1;
		for (int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
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

		byteBufferWriter.putInt(arrayList.size());
		for (int1 = 0; int1 < arrayList.size(); ++int1) {
			byteBufferWriter.putUTF((String)arrayList.get(int1));
			byteBufferWriter.putUTF((String)arrayList2.get(int1));
			if (SteamUtils.isSteamModeEnabled()) {
				byteBufferWriter.putLong((Long)arrayList3.get(int1));
			}
		}

		udpConnection.endPacketImmediate();
	}

	private static void receiveWorldSound(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		int int5 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		boolean boolean2 = byteBuffer.get() == 1;
		DebugLog.log(DebugType.Sound, "worldsound: received at " + int1 + "," + int2 + "," + int3 + " radius=" + int4);
		WorldSoundManager.instance.addSound(boolean2, int1, int2, int3, int4, int5, boolean1, float1, float2);
	}

	private static void receiveSound(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		String string = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		if (string != null && !string.isEmpty()) {
			int int4 = 70;
			GameSound gameSound = GameSounds.getSound(string);
			int int5;
			if (gameSound != null) {
				for (int5 = 0; int5 < gameSound.clips.size(); ++int5) {
					GameSoundClip gameSoundClip = (GameSoundClip)gameSound.clips.get(int5);
					if (gameSoundClip.hasMaxDistance()) {
						int4 = Math.max(int4, (int)gameSoundClip.distanceMax);
					}
				}
			}

			for (int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.isFullyConnected()) {
					IsoPlayer player = getAnyPlayerFromConnection(udpConnection2);
					if (player != null && udpConnection2.RelevantTo((float)int1, (float)int2, (float)int4)) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)53, byteBufferWriter);
						byteBufferWriter.putUTF(string);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
						udpConnection2.endPacketImmediate();
					}
				}
			}
		}
	}

	private static void PlayWorldSound(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2, boolean boolean3) {
		if (bServer && square != null) {
			int int1 = square.getX();
			int int2 = square.getY();
			int int3 = square.getZ();
			DebugLog.log(DebugType.Sound, "sound: sending " + string + " at " + int1 + "," + int2 + "," + int3 + " radius=" + float2);
			for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int4);
				IsoPlayer player = getAnyPlayerFromConnection(udpConnection);
				if (player != null && udpConnection.RelevantTo((float)int1, (float)int2, float2 * 2.0F)) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.doPacket((short)53, byteBufferWriter);
					byteBufferWriter.putUTF(string);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
					byteBufferWriter.putByte((byte)1);
					udpConnection.endPacketImmediate();
				}
			}
		}
	}

	public static void PlayWorldSoundServer(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2) {
		PlayWorldSound(string, boolean1, square, float1, float2, float3, boolean2, false);
	}

	public static void PlayWorldSoundWavServer(String string, boolean boolean1, IsoGridSquare square, float float1, float float2, float float3, boolean boolean2) {
		PlayWorldSound(string, boolean1, square, float1, float2, float3, boolean2, true);
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
					PacketTypes.doPacket((short)119, byteBufferWriter);
					byteBufferWriter.putUTF(string);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					udpConnection.endPacketImmediate();
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
				PacketTypes.doPacket((short)61, byteBufferWriter);
				byteBufferWriter.putShort(zombie.OnlineID);
				byteBufferWriter.putByte((byte)zombieSound.ordinal());
				udpConnection.endPacketImmediate();
			}
		}
	}

	private static void receiveZombieHelmetFalling(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		short short1 = byteBuffer.getShort();
		String string = GameWindow.ReadString(byteBuffer);
		IsoZombie zombie = ServerMap.instance.ZombieMap.get(short1);
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
							PacketTypes.doPacket((short)174, byteBufferWriter);
							byteBufferWriter.putShort(short1);
							byteBufferWriter.putUTF(string);
							udpConnection2.endPacketImmediate();
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	private static void receiveAttachedItem(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
							PacketTypes.doPacket((short)173, byteBufferWriter);
							byteBufferWriter.putShort((short)player.OnlineID);
							GameWindow.WriteString(byteBufferWriter.bb, string);
							byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
							if (boolean1) {
								GameWindow.WriteString(byteBufferWriter.bb, inventoryItem.getFullType());
							}

							udpConnection2.endPacketImmediate();
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	private static void receiveClothing(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		String string = GameWindow.ReadString(byteBuffer);
		byte byte2 = byteBuffer.get();
		InventoryItem inventoryItem = null;
		if (byte2 == 1) {
			try {
				inventoryItem = InventoryItem.loadItem(byteBuffer, 184);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			if (inventoryItem == null) {
				return;
			}
		}

		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			try {
				player.getHumanVisual().load(byteBuffer, 184);
				player.getItemVisuals().load(byteBuffer, 184);
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
				return;
			}

			if (byte2 == 1) {
				player.getWornItems().setItem(string, inventoryItem);
			}

			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)56, byteBufferWriter);
						try {
							byteBufferWriter.putShort((short)player.OnlineID);
							byteBufferWriter.putUTF(string);
							byteBufferWriter.putByte(byte2);
							if (byte2 == 1) {
								try {
									inventoryItem.saveWithSize(byteBufferWriter.bb, false);
								} catch (IOException ioException) {
									ioException.printStackTrace();
								}
							}

							player.getHumanVisual().save(byteBufferWriter.bb);
							ItemVisuals itemVisuals = new ItemVisuals();
							player.getItemVisuals(itemVisuals);
							itemVisuals.save(byteBufferWriter.bb);
							udpConnection2.endPacketImmediate();
						} catch (Throwable throwable2) {
							udpConnection2.cancelPacket();
							ExceptionLogger.logException(throwable2);
						}
					}
				}
			}

			if (ServerGUI.isCreated()) {
				player.resetModelNextFrame();
			}
		}
	}

	private static void receiveVisual(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			try {
				player.getHumanVisual().load(byteBuffer, 184);
			} catch (Throwable throwable) {
				ExceptionLogger.logException(throwable);
				return;
			}

			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)3, byteBufferWriter);
						try {
							byteBufferWriter.putShort((short)player.OnlineID);
							player.getHumanVisual().save(byteBufferWriter.bb);
							udpConnection2.endPacketImmediate();
						} catch (Throwable throwable2) {
							udpConnection2.cancelPacket();
							ExceptionLogger.logException(throwable2);
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

	private static void receiveClientCommand(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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

			LuaEventManager.triggerEvent("OnClientCommand", string, string2, player, kahluaTable);
		}
	}

	private static void receiveGlobalObjects(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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

	private static IsoPlayer getPlayerFromConnection(UdpConnection udpConnection, int int1) {
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

	public static UdpConnection getConnectionByPlayerOnlineID(Integer integer) {
		return udpEngine.getActiveConnection((Long)IDToAddressMap.get(integer));
	}

	public static UdpConnection getConnectionFromPlayer(IsoPlayer player) {
		Long Long1 = (Long)PlayerToAddressMap.get(player);
		return Long1 == null ? null : udpEngine.getActiveConnection(Long1);
	}

	private static void removeBlood(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			square.removeBlood(false, boolean1);
			for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
				if (udpConnection2 != udpConnection && udpConnection2.ReleventTo((float)int1, (float)int2)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)109, byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					byteBufferWriter.putBoolean(boolean1);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	public static void sendAddItemToContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		Object object = itemContainer.getParent();
		if (itemContainer.getContainingItem() != null && itemContainer.getContainingItem().getWorldItem() != null) {
			object = itemContainer.getContainingItem().getWorldItem();
		}

		IsoGridSquare square = ((IsoObject)object).getSquare();
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection.ReleventTo((float)square.x, (float)square.y)) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)20, byteBufferWriter);
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

				udpConnection.endPacket();
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
					if (udpConnection.ReleventTo((float)square.x, (float)square.y)) {
						ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
						PacketTypes.doPacket((short)22, byteBufferWriter);
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

						udpConnection.endPacketImmediate();
					}
				}
			}
		}
	}

	private static void removeItemFromContainer(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		alreadyRemoved.clear();
		ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
		short short1 = byteBufferReader.getShort();
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
		if (short1 == 0) {
			byte1 = byteBufferReader.getByte();
			int4 = byteBuffer.getInt();
			if (square != null && byte1 >= 0 && byte1 < square.getStaticMovingObjects().size()) {
				IsoObject object = (IsoObject)square.getStaticMovingObjects().get(byte1);
				if (object != null && object.getContainer() != null) {
					for (int int6 = 0; int6 < int4; ++int6) {
						int5 = byteBufferReader.getInt();
						InventoryItem inventoryItem = object.getContainer().getItemWithID(int5);
						if (inventoryItem == null) {
							alreadyRemoved.add(int5);
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
		} else {
			int int7;
			if (short1 == 1) {
				if (square != null) {
					long long1 = byteBufferReader.getLong();
					int4 = byteBuffer.getInt();
					ItemContainer itemContainer = null;
					for (int5 = 0; int5 < square.getWorldObjects().size(); ++int5) {
						IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int5);
						if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && (long)worldInventoryObject.getItem().id == long1) {
							itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
							break;
						}
					}

					if (itemContainer != null) {
						for (int5 = 0; int5 < int4; ++int5) {
							int7 = byteBufferReader.getInt();
							InventoryItem inventoryItem2 = itemContainer.getItemWithID(int7);
							if (inventoryItem2 == null) {
								alreadyRemoved.add(int7);
							} else {
								itemContainer.Remove(inventoryItem2);
								hashSet.add(inventoryItem2.getFullType());
							}
						}

						itemContainer.setExplored(true);
						itemContainer.setHasBeenLooted(true);
					}
				}
			} else {
				byte byte2;
				int int8;
				if (short1 == 2) {
					byte1 = byteBufferReader.getByte();
					byte2 = byteBufferReader.getByte();
					int4 = byteBuffer.getInt();
					if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
						IsoObject object2 = (IsoObject)square.getObjects().get(byte1);
						ItemContainer itemContainer2 = object2 != null ? object2.getContainerByIndex(byte2) : null;
						if (itemContainer2 != null) {
							for (int7 = 0; int7 < int4; ++int7) {
								int8 = byteBufferReader.getInt();
								InventoryItem inventoryItem3 = itemContainer2.getItemWithID(int8);
								if (inventoryItem3 == null) {
									alreadyRemoved.add(int8);
								} else {
									itemContainer2.Remove(inventoryItem3);
									itemContainer2.setExplored(true);
									itemContainer2.setHasBeenLooted(true);
									boolean1 = true;
									hashSet.add(inventoryItem3.getFullType());
								}
							}

							LuaManager.updateOverlaySprite(object2);
						}
					}
				} else if (short1 == 3) {
					short short2 = byteBufferReader.getShort();
					byte2 = byteBufferReader.getByte();
					int4 = byteBuffer.getInt();
					BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
					if (baseVehicle != null) {
						VehiclePart vehiclePart = baseVehicle == null ? null : baseVehicle.getPartByIndex(byte2);
						ItemContainer itemContainer3 = vehiclePart == null ? null : vehiclePart.getItemContainer();
						if (itemContainer3 != null) {
							for (int8 = 0; int8 < int4; ++int8) {
								int int9 = byteBufferReader.getInt();
								InventoryItem inventoryItem4 = itemContainer3.getItemWithID(int9);
								if (inventoryItem4 == null) {
									alreadyRemoved.add(int9);
								} else {
									itemContainer3.Remove(inventoryItem4);
									itemContainer3.setExplored(true);
									itemContainer3.setHasBeenLooted(true);
									boolean1 = true;
									hashSet.add(inventoryItem4.getFullType());
								}
							}
						}
					}
				}
			}
		}

		for (int int10 = 0; int10 < udpEngine.connections.size(); ++int10) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int10);
			if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && square != null && udpConnection2.ReleventTo((float)square.x, (float)square.y)) {
				byteBuffer.rewind();
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)22, byteBufferWriter);
				byteBufferWriter.bb.put(byteBuffer);
				udpConnection2.endPacketUnordered();
			}
		}

		if (!alreadyRemoved.isEmpty()) {
			ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
			PacketTypes.doPacket((short)49, byteBufferWriter2);
			byteBufferWriter2.putInt(alreadyRemoved.size());
			for (int int11 = 0; int11 < alreadyRemoved.size(); ++int11) {
				byteBufferWriter2.putLong((long)(Integer)alreadyRemoved.get(int11));
			}

			udpConnection.endPacketUnordered();
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

	private static void receiveItemStats(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
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
		switch (short1) {
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
			short short2 = byteBuffer.getShort();
			byte1 = byteBuffer.get();
			int4 = byteBuffer.getInt();
			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
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
			if (udpConnection2 != udpConnection && udpConnection2.ReleventTo((float)int1, (float)int2)) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)35, byteBufferWriter);
				byteBuffer.rewind();
				byteBufferWriter.bb.put(byteBuffer);
				udpConnection2.endPacket();
			}
		}
	}

	private static void requestItemsForContainer(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
		short short1 = byteBuffer.getShort();
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBufferReader.getInt();
		int int2 = byteBufferReader.getInt();
		int int3 = byteBufferReader.getInt();
		short short2 = byteBufferReader.getShort();
		byte byte1 = -1;
		byte byte2 = -1;
		int int4 = 0;
		short short3 = 0;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		IsoObject object = null;
		ItemContainer itemContainer = null;
		int int5;
		if (short2 == 2) {
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
		} else if (short2 == 3) {
			short3 = byteBufferReader.getShort();
			byte2 = byteBufferReader.getByte();
			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short3);
			if (baseVehicle != null) {
				VehiclePart vehiclePart = ((BaseVehicle)baseVehicle).getPartByIndex(byte2);
				itemContainer = vehiclePart == null ? null : vehiclePart.getItemContainer();
				if (itemContainer == null || itemContainer.isExplored()) {
					return;
				}
			}
		} else if (short2 == 1) {
			int4 = byteBufferReader.getInt();
			for (int5 = 0; int5 < square.getWorldObjects().size(); ++int5) {
				IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int5);
				if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && worldInventoryObject.getItem().id == int4) {
					itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
					break;
				}
			}
		} else if (short2 == 0) {
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
			ItemPickerJava.fillContainer(itemContainer, (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1)));
			if (int5 != itemContainer.Items.size()) {
				for (int int6 = 0; int6 < udpEngine.connections.size(); ++int6) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int6);
					if (udpConnection2.ReleventTo((float)square.x, (float)square.y)) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)20, byteBufferWriter);
						byteBufferWriter.putShort(short2);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						if (short2 == 0) {
							byteBufferWriter.putByte(byte1);
						} else if (short2 == 1) {
							byteBufferWriter.putInt(int4);
						} else if (short2 == 3) {
							byteBufferWriter.putShort(short3);
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

						udpConnection2.endPacketUnordered();
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
						if (udpConnection.ReleventTo((float)object.square.x, (float)object.square.y)) {
							ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
							PacketTypes.doPacket((short)20, byteBufferWriter);
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
								byteBufferWriter.putLong((long)((IsoWorldInventoryObject)object).getItem().id);
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

							udpConnection.endPacketImmediate();
						}
					}
				}
			}
		}
	}

	private static void logDupeItem(UdpConnection udpConnection) {
		IsoPlayer player = null;
		for (int int1 = 0; int1 < Players.size(); ++int1) {
			if (udpConnection.username.equals(((IsoPlayer)Players.get(int1)).username)) {
				player = (IsoPlayer)Players.get(int1);
				break;
			}
		}

		String string = "";
		if (player != null) {
			string = LoggerManager.getPlayerCoords(player);
		}

		ZLogger zLogger = LoggerManager.getLogger("user");
		String string2 = player.getDisplayName();
		zLogger.write("Error: Dupe item ID for " + string2 + " " + string);
		ServerWorldDatabase.instance.addUserlog(udpConnection.username, Userlog.UserlogType.DupeItem, "", "server", 1);
	}

	private static void sendItemsToContainer(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		ByteBufferReader byteBufferReader = new ByteBufferReader(byteBuffer);
		short short1 = byteBufferReader.getShort();
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
			if (short1 == 0) {
				byte2 = byteBufferReader.getByte();
				if (byte2 < 0 || byte2 >= square.getStaticMovingObjects().size()) {
					DebugLog.log("ERROR sendItemsToContainer invalid corpse index");
					return;
				}

				IsoObject object2 = (IsoObject)square.getStaticMovingObjects().get(byte2);
				if (object2 != null && object2.getContainer() != null) {
					itemContainer = object2.getContainer();
				}
			} else if (short1 == 1) {
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
				if (short1 == 2) {
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
				} else if (short1 == 3) {
					short short2 = byteBufferReader.getShort();
					byte3 = byteBufferReader.getByte();
					BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
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
					ArrayList arrayList = CompressIdenticalItems.load(byteBufferReader.bb, 184, (ArrayList)null, (ArrayList)null);
					for (int4 = 0; int4 < arrayList.size(); ++int4) {
						InventoryItem inventoryItem = (InventoryItem)arrayList.get(int4);
						if (inventoryItem != null) {
							if (itemContainer.containsID(inventoryItem.id)) {
								System.out.println("Error: Dupe item ID for " + udpConnection.username);
								logDupeItem(udpConnection);
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
			if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo((float)square.x, (float)square.y)) {
				byteBuffer.rewind();
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)20, byteBufferWriter);
				byteBufferWriter.bb.put(byteBuffer);
				udpConnection2.endPacketUnordered();
			}
		}

		LoggerManager.getLogger("item").write(udpConnection.idStr + " \"" + udpConnection.username + "\" container +" + byte1 + " " + int1 + "," + int2 + "," + int3 + " " + hashSet.toString());
	}

	public static boolean CheckPlayerStillValid(IsoPlayer player) {
		long long1 = (Long)PlayerToAddressMap.get(player);
		return !(player.getBodyDamage().getHealth() < 0.0F);
	}

	public static void addConnection(UdpConnection udpConnection) {
		synchronized (MainLoopNetDataHighPrioritet) {
			MainLoopNetDataHighPrioritet.add(new GameServer.DelayedConnection(udpConnection, true));
		}
	}

	public static void addDisconnect(UdpConnection udpConnection) {
		synchronized (MainLoopNetDataHighPrioritet) {
			MainLoopNetDataHighPrioritet.add(new GameServer.DelayedConnection(udpConnection, false));
		}
	}

	public static void disconnectPlayer(IsoPlayer player, UdpConnection udpConnection) {
		if (player != null) {
			ChatServer.getInstance().disconnectPlayer(player.getOnlineID());
			int int1;
			if (player.getVehicle() != null) {
				VehiclesDB2.instance.updateVehicleAndTrailer(player.getVehicle());
				if (player.getVehicle().getDriver() == player) {
					player.getVehicle().netPlayerAuthorization = 0;
					player.getVehicle().netPlayerId = -1;
					player.getVehicle().getController().clientForce = 0.0F;
					player.getVehicle().netLinearVelocity.set(0.0F, 0.0F, 0.0F);
				}

				int1 = player.getVehicle().getSeat(player);
				if (int1 != -1) {
					player.getVehicle().clearPassenger(int1);
				}
			}

			if (!player.isDead()) {
				ServerWorldDatabase.instance.saveTransactionID(player.username, player.getTransactionID());
			}

			player.removeFromWorld();
			player.removeFromSquare();
			PlayerToAddressMap.remove(player);
			IDToAddressMap.remove(player.OnlineID);
			IDToPlayerMap.remove(player.OnlineID);
			Players.remove(player);
			udpConnection.usernames[player.PlayerIndex] = null;
			udpConnection.players[player.PlayerIndex] = null;
			udpConnection.playerIDs[player.PlayerIndex] = -1;
			udpConnection.ReleventPos[player.PlayerIndex] = null;
			udpConnection.connectArea[player.PlayerIndex] = null;
			for (int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)13, byteBufferWriter);
				byteBufferWriter.putInt(player.OnlineID);
				udpConnection2.endPacketImmediate();
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

	public static int getFreeSlot() {
		for (int int1 = 0; int1 < udpEngine.getMaxConnections(); ++int1) {
			if (SlotToConnection[int1] == null) {
				return int1;
			}
		}

		return -1;
	}

	public static void receiveClientConnect(UdpConnection udpConnection, ServerWorldDatabase.LogonResult logonResult) {
		int int1 = getFreeSlot();
		int int2 = int1 * 4;
		if (udpConnection.playerDownloadServer != null) {
			try {
				IDToAddressMap.put(int2, udpConnection.getConnectedGUID());
				udpConnection.playerDownloadServer.destroy();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		playerToCoordsMap.put(int2, new Vector2());
		playerMovedToFastMap.put(int2, 0);
		SlotToConnection[int1] = udpConnection;
		udpConnection.playerIDs[0] = int2;
		IDToAddressMap.put(int2, udpConnection.getConnectedGUID());
		udpConnection.playerDownloadServer = new PlayerDownloadServer(udpConnection, DEFAULT_PORT + int1 + 1);
		DebugLog.log(DebugType.Network, "Connected new client " + udpConnection.username + " ID # " + int2 + " and assigned DL port " + udpConnection.playerDownloadServer.port);
		udpConnection.playerDownloadServer.startConnectionTest();
		KahluaTable kahluaTable = SpawnPoints.instance.getSpawnRegions();
		for (int int3 = 1; int3 < kahluaTable.size() + 1; ++int3) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)171, byteBufferWriter);
			byteBufferWriter.putInt(int3);
			try {
				((KahluaTable)kahluaTable.rawget(int3)).save(byteBufferWriter.bb);
				udpConnection.endPacketImmediate();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		VehicleManager.serverSendVehiclesConfig(udpConnection);
		ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
		PacketTypes.doPacket((short)21, byteBufferWriter2);
		if (SteamUtils.isSteamModeEnabled() && CoopSlave.instance != null && !udpConnection.isCoopHost) {
			byteBufferWriter2.putByte((byte)1);
			byteBufferWriter2.putLong(CoopSlave.instance.hostSteamID);
			byteBufferWriter2.putUTF(ServerName);
		} else {
			byteBufferWriter2.putByte((byte)0);
		}

		byteBufferWriter2.putByte((byte)int1);
		byteBufferWriter2.putInt(udpConnection.playerDownloadServer.port);
		byteBufferWriter2.putBoolean(UseTCPForMapDownloads);
		byteBufferWriter2.putUTF(logonResult.accessLevel);
		byteBufferWriter2.putUTF(GameMap);
		if (SteamUtils.isSteamModeEnabled()) {
			byteBufferWriter2.putShort((short)WorkshopItems.size());
			for (int int4 = 0; int4 < WorkshopItems.size(); ++int4) {
				byteBufferWriter2.putLong((Long)WorkshopItems.get(int4));
				byteBufferWriter2.putLong(WorkshopTimeStamps[int4]);
			}
		}

		ArrayList arrayList = new ArrayList();
		ChooseGameInfo.Mod mod = null;
		Iterator iterator;
		for (iterator = ServerMods.iterator(); iterator.hasNext(); arrayList.add(mod)) {
			String string = (String)iterator.next();
			String string2 = ZomboidFileSystem.instance.getModDir(string);
			if (string2 != null) {
				try {
					mod = ChooseGameInfo.readModInfo(string2);
				} catch (Exception exception2) {
					ExceptionLogger.logException(exception2);
					mod = new ChooseGameInfo.Mod(string);
					mod.setId(string);
					mod.setName(string);
				}
			} else {
				mod = new ChooseGameInfo.Mod(string);
				mod.setId(string);
				mod.setName(string);
			}
		}

		byteBufferWriter2.putInt(arrayList.size());
		iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			ChooseGameInfo.Mod mod2 = (ChooseGameInfo.Mod)iterator.next();
			byteBufferWriter2.putUTF(mod2.getId());
			byteBufferWriter2.putUTF(mod2.getUrl());
			byteBufferWriter2.putUTF(mod2.getName());
		}

		Vector3 vector3 = ServerMap.instance.getStartLocation(logonResult);
		logonResult.x = (int)vector3.x;
		logonResult.y = (int)vector3.y;
		logonResult.z = (int)vector3.z;
		byteBufferWriter2.putInt(logonResult.x);
		byteBufferWriter2.putInt(logonResult.y);
		byteBufferWriter2.putInt(logonResult.z);
		byteBufferWriter2.putInt(ServerOptions.instance.getPublicOptions().size());
		iterator = null;
		Iterator iterator2 = ServerOptions.instance.getPublicOptions().iterator();
		while (iterator2.hasNext()) {
			String string3 = (String)iterator2.next();
			byteBufferWriter2.putUTF(string3);
			byteBufferWriter2.putUTF(ServerOptions.instance.getOption(string3));
		}

		try {
			SandboxOptions.instance.save(byteBufferWriter2.bb);
			GameTime.getInstance().saveToPacket(byteBufferWriter2.bb);
		} catch (IOException ioException2) {
			ioException2.printStackTrace();
		}

		ErosionMain.getInstance().getConfig().save(byteBufferWriter2.bb);
		try {
			SGlobalObjects.saveInitialStateForClient(byteBufferWriter2.bb);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}

		byteBufferWriter2.putInt(ResetID);
		GameWindow.WriteString(byteBufferWriter2.bb, Core.getInstance().getPoisonousBerry());
		GameWindow.WriteString(byteBufferWriter2.bb, Core.getInstance().getPoisonousMushroom());
		byteBufferWriter2.putBoolean(udpConnection.isCoopHost);
		try {
			WorldDictionary.saveDataForClient(byteBufferWriter2.bb);
		} catch (Exception exception3) {
			exception3.printStackTrace();
		}

		udpConnection.endPacketImmediate();
		if (!SteamUtils.isSteamModeEnabled()) {
			PublicServerUtil.updatePlayers();
		}
	}

	private static void sendLargeFile(UdpConnection udpConnection, String string) {
		int int1 = large_file_bb.position();
		int int2;
		for (int int3 = 0; int3 < int1; int3 += int2) {
			int2 = Math.min(1000, int1 - int3);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)37, byteBufferWriter);
			byteBufferWriter.putUTF(string);
			byteBufferWriter.putInt(int1);
			byteBufferWriter.putInt(int3);
			byteBufferWriter.putInt(int2);
			byteBufferWriter.bb.put(large_file_bb.array(), int3, int2);
			udpConnection.endPacketImmediate();
		}
	}

	private static void receiveRequestData(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		String string = GameWindow.ReadString(byteBuffer);
		ByteBufferWriter byteBufferWriter;
		if ("descriptors.bin".equals(string)) {
			byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)37, byteBufferWriter);
			byteBufferWriter.putUTF(string);
			try {
				PersistentOutfits.instance.save(byteBufferWriter.bb);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			udpConnection.endPacketImmediate();
		}

		if ("playerzombiedesc".equals(string)) {
			byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)37, byteBufferWriter);
			byteBufferWriter.putUTF(string);
			SharedDescriptors.Descriptor[] descriptorArray = SharedDescriptors.getPlayerZombieDescriptors();
			int int1 = 0;
			for (int int2 = 0; int2 < descriptorArray.length; ++int2) {
				if (descriptorArray[int2] != null) {
					++int1;
				}
			}

			try {
				byteBufferWriter.putShort((short)int1);
				SharedDescriptors.Descriptor[] descriptorArray2 = descriptorArray;
				int int3 = descriptorArray.length;
				for (int int4 = 0; int4 < int3; ++int4) {
					SharedDescriptors.Descriptor descriptor = descriptorArray2[int4];
					if (descriptor != null) {
						descriptor.save(byteBufferWriter.bb);
					}
				}
			} catch (Exception exception2) {
				exception2.printStackTrace();
			}

			udpConnection.endPacketImmediate();
		}

		if ("map_meta.bin".equals(string)) {
			try {
				large_file_bb.clear();
				IsoWorld.instance.MetaGrid.savePart(large_file_bb, 0, true);
				IsoWorld.instance.MetaGrid.savePart(large_file_bb, 1, true);
				sendLargeFile(udpConnection, string);
			} catch (Exception exception3) {
				exception3.printStackTrace();
				byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)83, byteBufferWriter);
				byteBufferWriter.putUTF("You have been kicked from this server because map_meta.bin could not be saved.");
				udpConnection.endPacketImmediate();
				udpConnection.forceDisconnect();
				addDisconnect(udpConnection);
			}
		}

		if ("map_zone.bin".equals(string)) {
			try {
				large_file_bb.clear();
				IsoWorld.instance.MetaGrid.saveZone(large_file_bb);
				sendLargeFile(udpConnection, string);
			} catch (Exception exception4) {
				exception4.printStackTrace();
				byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)83, byteBufferWriter);
				byteBufferWriter.putUTF("You have been kicked from this server because map_zone.bin could not be saved.");
				udpConnection.endPacketImmediate();
				udpConnection.forceDisconnect();
				addDisconnect(udpConnection);
			}
		}
	}

	public static void sendMetaGrid(int int1, int int2, int int3, UdpConnection udpConnection) {
		IsoMetaGrid metaGrid = IsoWorld.instance.MetaGrid;
		if (int1 >= metaGrid.getMinX() && int1 <= metaGrid.getMaxX() && int2 >= metaGrid.getMinY() && int2 <= metaGrid.getMaxY()) {
			IsoMetaCell metaCell = metaGrid.getCellData(int1, int2);
			if (metaCell.info != null && int3 >= 0 && int3 < metaCell.info.RoomList.size()) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)9, byteBufferWriter);
				byteBufferWriter.putShort((short)int1);
				byteBufferWriter.putShort((short)int2);
				byteBufferWriter.putShort((short)int3);
				byteBufferWriter.putBoolean(metaCell.info.getRoom(int3).def.bLightsActive);
				udpConnection.endPacketImmediate();
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
		byte byte1 = byteBuffer.get();
		if (byte1 >= 0 && byte1 < 4 && udpConnection.players[byte1] == null) {
			byte byte2 = byteBuffer.get();
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
				survivorDesc.load(byteBuffer, 184, (IsoGameCharacter)null);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			IsoPlayer player = new IsoPlayer((IsoCell)null, survivorDesc, (int)float1, (int)float2, (int)float3);
			player.PlayerIndex = byte1;
			player.OnlineChunkGridWidth = byte2;
			Players.add(player);
			player.bRemote = true;
			try {
				player.getHumanVisual().load(byteBuffer, 184);
				player.getItemVisuals().load(byteBuffer, 184);
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}

			int int1 = udpConnection.playerIDs[byte1];
			IDToPlayerMap.put(int1, player);
			udpConnection.players[byte1] = player;
			PlayerToAddressMap.put(player, udpConnection.getConnectedGUID());
			player.OnlineID = int1;
			try {
				player.getXp().load(byteBuffer, 184);
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
					inventoryItem = InventoryItem.loadItem(byteBuffer, 184);
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
					inventoryItem = InventoryItem.loadItem(byteBuffer, 184);
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

			int int2 = byteBuffer.getInt();
			int int3;
			for (int3 = 0; int3 < int2; ++int3) {
				String string3 = GameWindow.ReadString(byteBuffer);
				InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem(GameWindow.ReadString(byteBuffer));
				if (inventoryItem2 != null) {
					player.setAttachedItem(string3, inventoryItem2);
				}
			}

			int3 = byteBuffer.getInt();
			player.remoteSneakLvl = int3;
			player.username = string;
			player.accessLevel = udpConnection.accessLevel;
			if (!player.accessLevel.equals("") && CoopSlave.instance == null) {
				player.setGhostMode(true);
				player.setInvisible(true);
				player.setGodMod(true);
			}

			ChatServer.getInstance().initPlayer(player.OnlineID);
			udpConnection.setFullyConnected();
			sendWeather(udpConnection);
			for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int4);
				sendPlayerConnect(player, udpConnection2);
			}

			Iterator iterator = IDToPlayerMap.values().iterator();
			while (iterator.hasNext()) {
				IsoPlayer player2 = (IsoPlayer)iterator.next();
				if (player2.getOnlineID() != player.getOnlineID()) {
					sendPlayerConnect(player2, udpConnection);
				}
			}

			udpConnection.loadedCells[byte1].setLoaded();
			udpConnection.loadedCells[byte1].sendPacket(udpConnection);
			preventIndoorZombies((int)float1, (int)float2, (int)float3);
			ServerLOS.instance.addPlayer(player);
			ZLogger zLogger = LoggerManager.getLogger("user");
			String string4 = udpConnection.idStr;
			zLogger.write(string4 + " \"" + player.username + "\" fully connected " + LoggerManager.getPlayerCoords(player));
		}
	}

	private static void receivePlayerSave(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if ((Calendar.getInstance().getTimeInMillis() - previousSave) / 60000L >= 0L) {
			byte byte1 = byteBuffer.get();
			if (byte1 >= 0 && byte1 < 4) {
				int int1 = byteBuffer.getInt();
				float float1 = byteBuffer.getFloat();
				float float2 = byteBuffer.getFloat();
				float float3 = byteBuffer.getFloat();
				ServerMap.instance.saveZoneInsidePlayerInfluence(int1);
			}
		}
	}

	private static void receiveSendPlayerProfile(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		ServerPlayerDB.getInstance().serverUpdateNetworkCharacter(byteBuffer, udpConnection);
	}

	private static void receiveLoadPlayerProfile(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		ServerPlayerDB.getInstance().serverLoadNetworkCharacter(byteBuffer, udpConnection);
	}

	private static void coopAccessGranted(int int1, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)27, byteBufferWriter);
		byteBufferWriter.putBoolean(true);
		byteBufferWriter.putByte((byte)int1);
		udpConnection.endPacketImmediate();
	}

	private static void coopAccessDenied(String string, int int1, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)27, byteBufferWriter);
		byteBufferWriter.putBoolean(false);
		byteBufferWriter.putByte((byte)int1);
		byteBufferWriter.putUTF(string);
		udpConnection.endPacketImmediate();
	}

	private static void addCoopPlayer(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		if (byte2 >= 0 && byte2 < 4) {
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
						int int1;
						for (int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
							UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
							for (int int2 = 0; int2 < 4; ++int2) {
								if ((udpConnection2 != udpConnection || byte2 != int2) && string.equals(udpConnection2.usernames[int2])) {
									coopAccessDenied("User \"" + string + "\" already connected", byte2, udpConnection);
									return;
								}
							}
						}

						DebugLog.log("coop player=" + (byte2 + 1) + "/4 username=\"" + string + "\" is joining");
						float float1;
						if (udpConnection.players[byte2] != null) {
							DebugLog.log("coop player=" + (byte2 + 1) + "/4 username=\"" + string + "\" is replacing dead player");
							int1 = udpConnection.players[byte2].OnlineID;
							disconnectPlayer(udpConnection.players[byte2], udpConnection);
							float float2 = byteBuffer.getFloat();
							float1 = byteBuffer.getFloat();
							udpConnection.usernames[byte2] = string;
							udpConnection.ReleventPos[byte2] = new Vector3(float2, float1, 0.0F);
							udpConnection.connectArea[byte2] = new Vector3(float2 / 10.0F, float1 / 10.0F, (float)udpConnection.ChunkGridWidth);
							udpConnection.playerIDs[byte2] = int1;
							IDToAddressMap.put(int1, udpConnection.getConnectedGUID());
							coopAccessGranted(byte2, udpConnection);
							ZombiePopulationManager.instance.updateLoadedAreas();
							if (ChatServer.isInited()) {
								ChatServer.getInstance().initPlayer(int1);
							}
						} else if (getPlayerCount() >= ServerOptions.instance.MaxPlayers.getValue()) {
							coopAccessDenied("Server is full", byte2, udpConnection);
						} else {
							int1 = -1;
							int int3;
							for (int3 = 0; int3 < udpEngine.getMaxConnections(); ++int3) {
								if (SlotToConnection[int3] == udpConnection) {
									int1 = int3;
									break;
								}
							}

							int3 = int1 * 4 + byte2;
							DebugLog.log("coop player=" + (byte2 + 1) + "/4 username=\"" + string + "\" assigned id=" + int3);
							float1 = byteBuffer.getFloat();
							float float3 = byteBuffer.getFloat();
							udpConnection.usernames[byte2] = string;
							udpConnection.ReleventPos[byte2] = new Vector3(float1, float3, 0.0F);
							udpConnection.playerIDs[byte2] = int3;
							udpConnection.connectArea[byte2] = new Vector3(float1 / 10.0F, float3 / 10.0F, (float)udpConnection.ChunkGridWidth);
							IDToAddressMap.put(int3, udpConnection.getConnectedGUID());
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

		try {
			ClimateManager.getInstance().sendInitialState(udpConnection);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static void receiveObjectModData(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
					object.getModData().load((ByteBuffer)byteBuffer, 184);
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
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo((float)int1, (float)int2)) {
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
			PacketTypes.doPacket((short)58, byteBufferWriter);
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

			udpConnection.endPacketImmediate();
		}
	}

	public static void sendObjectModData(IsoObject object) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection.ReleventTo(object.getX(), object.getY())) {
				sendObjectModData(object, udpConnection);
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
					PacketTypes.doPacket((short)63, byteBufferWriter);
					byteBufferWriter.putByte((byte)((IsoPlayer)gameCharacter).PlayerIndex);
					byteBufferWriter.putFloat(gameCharacter.getSlowTimer());
					byteBufferWriter.putFloat(gameCharacter.getSlowFactor());
					udpConnection.endPacketImmediate();
				}
			}
		}
	}

	private static void sendObjectChange(IsoObject object, String string, KahluaTable kahluaTable, UdpConnection udpConnection) {
		if (object.getSquare() != null) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)59, byteBufferWriter);
			if (object instanceof IsoPlayer) {
				byteBufferWriter.putByte((byte)1);
				byteBufferWriter.putInt(((IsoPlayer)object).OnlineID);
			} else if (object instanceof BaseVehicle) {
				byteBufferWriter.putByte((byte)2);
				byteBufferWriter.putShort(((BaseVehicle)object).getId());
			} else if (object instanceof IsoWorldInventoryObject) {
				byteBufferWriter.putByte((byte)3);
				byteBufferWriter.putInt(object.getSquare().getX());
				byteBufferWriter.putInt(object.getSquare().getY());
				byteBufferWriter.putInt(object.getSquare().getZ());
				byteBufferWriter.putInt(((IsoWorldInventoryObject)object).getItem().getID());
			} else {
				byteBufferWriter.putByte((byte)0);
				byteBufferWriter.putInt(object.getSquare().getX());
				byteBufferWriter.putInt(object.getSquare().getY());
				byteBufferWriter.putInt(object.getSquare().getZ());
				byteBufferWriter.putInt(object.getSquare().getObjects().indexOf(object));
			}

			byteBufferWriter.putUTF(string);
			object.saveChange(string, kahluaTable, byteBufferWriter.bb);
			udpConnection.endPacketImmediate();
		}
	}

	public static void sendObjectChange(IsoObject object, String string, KahluaTable kahluaTable) {
		if (object != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection.ReleventTo(object.getX(), object.getY())) {
					sendObjectChange(object, string, kahluaTable, udpConnection);
				}
			}
		}
	}

	public static void sendObjectChange(IsoObject object, String string, Object[] objectArray) {
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

	private static void updateHandEquips(UdpConnection udpConnection, IsoPlayer player) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)25, byteBufferWriter);
		byteBufferWriter.putShort((short)player.OnlineID);
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

		udpConnection.endPacketImmediate();
		byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)25, byteBufferWriter);
		byteBufferWriter.putShort((short)player.OnlineID);
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

		udpConnection.endPacketImmediate();
	}

	public static void sendZombie(IsoZombie zombie) {
		if (!bFastForward) {
			;
		}
	}

	public static void sendZombieUpdate(IsoZombie zombie, UdpConnection udpConnection, int int1) {
		if (udpConnection != null && udpConnection.ReleventTo(zombie.x, zombie.y)) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)10, byteBufferWriter);
			byteBufferWriter.putShort((short)1);
			ZombieUpdateInfoPacket.writeZombie(byteBufferWriter, zombie, int1);
			udpConnection.endPacketImmediate();
		}
	}

	public static void sendZombieUpdate(IsoZombie zombie) {
		if (zombie != null && zombie.OnlineID != -1) {
			Iterator iterator = PlayerToAddressMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				if (entry.getValue() != null && entry.getKey() != null && ((IsoPlayer)entry.getKey()).getOnlineID() != -1) {
					UdpConnection udpConnection = udpEngine.getActiveConnection((Long)entry.getValue());
					sendZombieUpdate(zombie, udpConnection, ((IsoPlayer)entry.getKey()).getOnlineID());
				}
			}
		}
	}

	public static void SyncCustomLightSwitchSettings(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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

	private static void sendAlarmClock_Player(short short1, long long1, boolean boolean1, int int1, int int2, boolean boolean2, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)46, byteBufferWriter);
		byteBufferWriter.putShort(AlarmClock.PacketPlayer);
		byteBufferWriter.putShort(short1);
		byteBufferWriter.putLong(long1);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
		if (!boolean1) {
			byteBufferWriter.putInt(int1);
			byteBufferWriter.putInt(int2);
			byteBufferWriter.putByte((byte)(boolean2 ? 1 : 0));
		}

		udpConnection.endPacket();
	}

	private static void sendAlarmClock_World(int int1, int int2, int int3, long long1, boolean boolean1, int int4, int int5, boolean boolean2, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)46, byteBufferWriter);
		byteBufferWriter.putShort(AlarmClock.PacketWorld);
		byteBufferWriter.putInt(int1);
		byteBufferWriter.putInt(int2);
		byteBufferWriter.putInt(int3);
		byteBufferWriter.putLong(long1);
		byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
		if (!boolean1) {
			byteBufferWriter.putInt(int4);
			byteBufferWriter.putInt(int5);
			byteBufferWriter.putByte((byte)(boolean2 ? 1 : 0));
		}

		udpConnection.endPacket();
	}

	private static void SyncAlarmClock(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		int int1;
		int int2;
		int int3;
		if (short1 == AlarmClock.PacketPlayer) {
			short short2 = byteBuffer.getShort();
			int1 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			int2 = 0;
			int int4 = 0;
			boolean boolean2 = false;
			if (!boolean1) {
				int2 = byteBuffer.getInt();
				int4 = byteBuffer.getInt();
				boolean2 = byteBuffer.get() == 1;
			}

			for (int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
				if (udpConnection2 != udpConnection) {
					sendAlarmClock_Player(short2, (long)int1, boolean1, int2, int4, boolean2, udpConnection2);
				}
			}
		} else if (short1 == AlarmClock.PacketWorld) {
			int int5 = byteBuffer.getInt();
			int1 = byteBuffer.getInt();
			int int6 = byteBuffer.getInt();
			int2 = byteBuffer.getInt();
			boolean boolean3 = byteBuffer.get() == 1;
			int int7 = 0;
			int3 = 0;
			boolean boolean4 = false;
			if (!boolean3) {
				int7 = byteBuffer.getInt();
				int3 = byteBuffer.getInt();
				boolean4 = byteBuffer.get() == 1;
			}

			IsoGridSquare square = ServerMap.instance.getGridSquare(int5, int1, int6);
			if (square == null) {
				DebugLog.log("SyncAlarmClock: sq is null x,y,z=" + int5 + "," + int1 + "," + int6);
			} else {
				AlarmClock alarmClock = null;
				int int8;
				for (int8 = 0; int8 < square.getWorldObjects().size(); ++int8) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int8);
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
						alarmClock.setMinute(int3);
						alarmClock.setAlarmSet(boolean4);
					}

					for (int8 = 0; int8 < udpEngine.connections.size(); ++int8) {
						UdpConnection udpConnection3 = (UdpConnection)udpEngine.connections.get(int8);
						if (udpConnection3 != udpConnection) {
							sendAlarmClock_World(int5, int1, int6, (long)int2, boolean3, int7, int3, boolean4, udpConnection3);
						}
					}
				}
			}
		}
	}

	public static void SyncIsoObject(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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

	public static void SyncIsoObjectReq(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		if (short1 <= 50 && short1 > 0) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)161, byteBufferWriter);
			byteBufferWriter.putShort(short1);
			for (int int1 = 0; int1 < short1; ++int1) {
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

			udpConnection.endPacketImmediate();
		}
	}

	public static void SyncObjectChunkHashes(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		if (short1 <= 10 && short1 > 0) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)164, byteBufferWriter);
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
			udpConnection.endPacketImmediate();
		}
	}

	public static void SyncObjectChunkHashes(IsoChunk chunk, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)164, byteBufferWriter);
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
		udpConnection.endPacketImmediate();
	}

	public static void SyncObjectsGridSquareRequest(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		if (short1 <= 100 && short1 > 0) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)164, byteBufferWriter);
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
			udpConnection.endPacketImmediate();
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
							PacketTypes.doPacket((short)164, byteBufferWriter);
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

							udpConnection.endPacketImmediate();
							break;
						}
					}
				}
			}
		}
	}

	public static void SyncDoorKey(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
						PacketTypes.doPacket((short)106, byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						byteBufferWriter.putByte(byte1);
						byteBufferWriter.putInt(int4);
						udpConnection2.endPacketImmediate();
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

	public static void SyncThumpable(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
						PacketTypes.doPacket((short)105, byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						byteBufferWriter.putByte(byte1);
						byteBufferWriter.putInt(int4);
						byteBufferWriter.putByte(byte2);
						byteBufferWriter.putInt(int5);
						udpConnection2.endPacketImmediate();
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

	private static void RemoveItemFromMap(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
			}

			for (int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)23, byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					byteBufferWriter.putInt(int4);
					udpConnection2.endPacketImmediate();
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
			}

			for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int5);
				if (udpConnection.ReleventTo((float)int1, (float)int2)) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.doPacket((short)23, byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					byteBufferWriter.putInt(int4);
					udpConnection.endPacketImmediate();
				}
			}

			return int4;
		}
	}

	public static void doZombieDie(IsoZombie zombie, IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)29, byteBufferWriter);
			byteBufferWriter.putInt(zombie.OnlineID);
			byteBufferWriter.putShort((short)((IsoPlayer)gameCharacter).OnlineID);
			udpConnection.endPacketImmediate();
		}
	}

	public static void sendBloodSplatter(HandWeapon handWeapon, float float1, float float2, float float3, Vector2 vector2, boolean boolean1, boolean boolean2) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)60, byteBufferWriter);
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
			udpConnection.endPacketImmediate();
		}
	}

	public static void AddItemToMap(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
				}

				for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo((float)object.square.x, (float)object.square.y)) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)17, byteBufferWriter);
						object.writeToRemoteBuffer(byteBufferWriter);
						udpConnection2.endPacketImmediate();
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

	public static void sendDeleteZombie(IsoZombie zombie) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection.ReleventTo(zombie.x, zombie.y)) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)30, byteBufferWriter);
				byteBufferWriter.putShort(zombie.OnlineID);
				udpConnection.endPacketImmediate();
			}
		}
	}

	public static void disconnect(UdpConnection udpConnection) {
		if (udpConnection.playerDownloadServer != null) {
			try {
				udpConnection.playerDownloadServer.destroy();
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			udpConnection.playerDownloadServer = null;
		}

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
	}

	public static void addIncoming(short short1, ByteBuffer byteBuffer, UdpConnection udpConnection) {
		ZomboidNetData zomboidNetData = null;
		if (byteBuffer.limit() > 2048) {
			zomboidNetData = ZomboidNetDataPool.instance.getLong(byteBuffer.limit());
		} else {
			zomboidNetData = ZomboidNetDataPool.instance.get();
		}

		zomboidNetData.read(short1, byteBuffer, udpConnection);
		zomboidNetData.time = System.currentTimeMillis();
		if (zomboidNetData.type == 218) {
			short short2 = zomboidNetData.buffer.getShort();
			zomboidNetData.buffer.position(0);
			synchronized (MainLoopPlayerUpdate) {
				if (MainLoopPlayerUpdate.containsKey(Integer.valueOf(short2))) {
					MainLoopPlayerUpdate.replace(Integer.valueOf(short2), zomboidNetData);
				} else {
					MainLoopPlayerUpdate.put(Integer.valueOf(short2), zomboidNetData);
				}
			}
		} else if (zomboidNetData.type == 5) {
			byte byte1 = zomboidNetData.buffer.get();
			zomboidNetData.buffer.position(0);
			if (byte1 == 9) {
				synchronized (MainLoopNetData) {
					MainLoopNetData.add(zomboidNetData);
				}
			} else {
				synchronized (MainLoopNetDataHighPrioritet) {
					MainLoopNetDataHighPrioritet.add(zomboidNetData);
				}
			}
		} else {
			synchronized (MainLoopNetDataHighPrioritet) {
				MainLoopNetDataHighPrioritet.add(zomboidNetData);
			}
		}
	}

	public static void smashWindow(IsoWindow window, int int1) {
		for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int2);
			if (udpConnection.ReleventTo(window.getX(), window.getY())) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)32, byteBufferWriter);
				byteBufferWriter.putInt(window.square.getX());
				byteBufferWriter.putInt(window.square.getY());
				byteBufferWriter.putInt(window.square.getZ());
				byteBufferWriter.putByte((byte)window.square.getObjects().indexOf(window));
				byteBufferWriter.putByte((byte)int1);
				udpConnection.endPacketImmediate();
			}
		}
	}

	public static void SendDeath(UdpConnection udpConnection, IsoPlayer player) {
		player.getBodyDamage().setOverallBodyHealth(-1.0F);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2 != udpConnection) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)33, byteBufferWriter);
				byteBufferWriter.putInt(player.OnlineID);
				byteBufferWriter.putFloat(player.getX());
				byteBufferWriter.putFloat(player.getY());
				byteBufferWriter.putFloat(player.getZ());
				byteBufferWriter.putInt(player.getDir().index());
				try {
					ArrayList arrayList = player.getInventory().save(byteBufferWriter.bb);
					byteBufferWriter.putByte((byte)player.getWornItems().size());
					player.getWornItems().forEach((int1x)->{
						GameWindow.WriteString(udpConnection2.bb, int1x.getLocation());
						udpConnection2.putShort((short)byteBufferWriter.indexOf(int1x.getItem()));
					});
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

				udpConnection2.endPacketImmediate();
			}
		}
	}

	public static void SendDeath(IsoPlayer player) {
		player.getBodyDamage().setOverallBodyHealth(-1.0F);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)33, byteBufferWriter);
			byteBufferWriter.putInt(player.OnlineID);
			byteBufferWriter.putFloat(player.getX());
			byteBufferWriter.putFloat(player.getY());
			byteBufferWriter.putFloat(player.getZ());
			byteBufferWriter.putInt(player.getDir().index());
			try {
				ArrayList arrayList = player.getInventory().save(byteBufferWriter.bb);
				byteBufferWriter.putByte((byte)player.getWornItems().size());
				player.getWornItems().forEach((udpConnectionx)->{
					GameWindow.WriteString(byteBufferWriter.bb, udpConnectionx.getLocation());
					byteBufferWriter.putShort((short)arrayList.indexOf(udpConnectionx.getItem()));
				});
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			udpConnection.endPacketImmediate();
		}
	}

	public static void SendOnBeaten(IsoPlayer player, float float1, float float2, float float3) {
		player.getBodyDamage().setOverallBodyHealth(-1.0F);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)165, byteBufferWriter);
			byteBufferWriter.putInt(player.OnlineID);
			byteBufferWriter.putFloat(float1);
			byteBufferWriter.putFloat(float2);
			byteBufferWriter.putFloat(float3);
			udpConnection.endPacketImmediate();
		}
	}

	public static boolean doSendZombies() {
		return SendZombies == 0;
	}

	private static void receiveDeadZombie(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		try {
			DeadBodyPacket deadBodyPacket = new DeadBodyPacket();
			deadBodyPacket.parse(byteBuffer);
			DebugLog.log(DebugType.Multiplayer, "DeadBody receive: " + deadBodyPacket.getDescription());
			if (deadBodyPacket.zombie != null && deadBodyPacket.zombie.networkAI.deadZombie == null) {
				deadBodyPacket.zombie.networkAI.deadZombie = deadBodyPacket;
			}
		} catch (Exception exception) {
			DebugLog.log("DeadZombie receive: failed");
			exception.printStackTrace();
		}
	}

	public static void sendDeadZombie(IsoZombie zombie) {
		if (zombie.networkAI.deadZombie == null) {
			zombie.networkAI.deadZombie = new DeadBodyPacket();
			zombie.networkAI.deadZombie.set(zombie);
		}

		zombie.networkAI.deadZombie.isServer = true;
		DebugLog.log(DebugType.Multiplayer, "DeadBody send: " + zombie.networkAI.deadZombie.getDescription());
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection.ReleventTo(zombie.x, zombie.y)) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)39, byteBufferWriter);
				zombie.networkAI.deadZombie.write(byteBufferWriter);
				if (zombie.getInventory() != null) {
					byteBufferWriter.putByte((byte)1);
					try {
						int int2 = -1;
						Iterator iterator = zombie.getInventory().getItems().iterator();
						while (iterator.hasNext()) {
							InventoryItem inventoryItem = (InventoryItem)iterator.next();
							if (PersistentOutfits.instance.isHatFallen(zombie.getPersistentOutfitID()) && inventoryItem.getScriptItem() != null && inventoryItem.getScriptItem().getChanceToFall() > 0) {
								int2 = inventoryItem.id;
							}
						}

						if (int2 != -1) {
							zombie.getInventory().removeItemWithID(int2);
						}

						ArrayList arrayList = zombie.getInventory().save(byteBufferWriter.bb);
						WornItems wornItems = zombie.getWornItems();
						if (wornItems == null) {
							byteBufferWriter.bb.put((byte)0);
						} else {
							if (wornItems.size() > 127) {
								throw new RuntimeException("too many worn items");
							}

							byteBufferWriter.bb.put((byte)wornItems.size());
							for (int int3 = 0; int3 < wornItems.size(); ++int3) {
								WornItem wornItem = wornItems.get(int3);
								if (PersistentOutfits.instance.isHatFallen(zombie.getPersistentOutfitID()) && wornItem.getItem().getScriptItem() != null && wornItem.getItem().getScriptItem().getChanceToFall() > 0) {
									byteBufferWriter.putUTF("");
									byteBufferWriter.bb.putShort((short)-1);
								} else {
									byteBufferWriter.putUTF(wornItem.getLocation());
									byteBufferWriter.bb.putShort((short)arrayList.indexOf(wornItem.getItem()));
								}
							}
						}

						AttachedItems attachedItems = zombie.getAttachedItems();
						if (attachedItems == null) {
							byteBufferWriter.putByte((byte)0);
						} else {
							if (attachedItems.size() > 127) {
								throw new RuntimeException("too many attached items");
							}

							byteBufferWriter.putByte((byte)attachedItems.size());
							for (int int4 = 0; int4 < attachedItems.size(); ++int4) {
								AttachedItem attachedItem = attachedItems.get(int4);
								byteBufferWriter.putUTF(attachedItem.getLocation());
								byteBufferWriter.putShort((short)arrayList.indexOf(attachedItem.getItem()));
							}
						}
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				} else {
					byteBufferWriter.putByte((byte)0);
				}

				udpConnection.endPacketImmediate();
			}
		}
	}

	public static void doDamage(IsoGameCharacter gameCharacter, float float1) {
		if (gameCharacter != null) {
			if (PlayerToAddressMap.containsKey((IsoPlayer)gameCharacter)) {
				long long1 = (Long)PlayerToAddressMap.get((IsoPlayer)gameCharacter);
				UdpConnection udpConnection = udpEngine.getActiveConnection(long1);
				if (udpConnection != null) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					IsoPlayer player = (IsoPlayer)gameCharacter;
					PacketTypes.doPacket((short)41, byteBufferWriter);
					byteBufferWriter.putShort((short)player.OnlineID);
					try {
						player.getBodyDamage().save(byteBufferWriter.bb);
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}

					byteBufferWriter.putFloat(float1);
					udpConnection.endPacketImmediate();
				}
			}
		}
	}

	private static void sendStartRain(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)77, byteBufferWriter);
		byteBufferWriter.putInt(RainManager.randRainMin);
		byteBufferWriter.putInt(RainManager.randRainMax);
		byteBufferWriter.putFloat(RainManager.RainDesiredIntensity);
		udpConnection.endPacketImmediate();
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
		PacketTypes.doPacket((short)78, byteBufferWriter);
		udpConnection.endPacketImmediate();
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
		PacketTypes.doPacket((short)64, byteBufferWriter);
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
		udpConnection.endPacketImmediate();
	}

	public static void sendWeather() {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			sendWeather(udpConnection);
		}
	}

	private static void syncClock(UdpConnection udpConnection) {
		GameTime gameTime = GameTime.getInstance();
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)19, byteBufferWriter);
		byteBufferWriter.putBoolean(bFastForward);
		byteBufferWriter.putFloat(gameTime.getTimeOfDay());
		udpConnection.endPacketImmediate();
	}

	public static void syncClock() {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			syncClock(udpConnection);
		}
	}

	public static void sendServerCommand(String string, String string2, KahluaTable kahluaTable, UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)57, byteBufferWriter);
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

		udpConnection.endPacketImmediate();
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

	public static ArrayList getPlayers() {
		ArrayList arrayList = new ArrayList();
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
				PacketTypes.doPacket((short)55, byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putInt(int3);
				byteBufferWriter.putFloat(float1);
				udpConnection.endPacketImmediate();
			}
		}
	}

	public static void toggleSafety(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			player.setSafety(!player.isSafety());
			ZLogger zLogger;
			String string;
			if (player.isSafety()) {
				zLogger = LoggerManager.getLogger("pvp");
				string = player.username;
				zLogger.write("user " + string + " " + LoggerManager.getPlayerCoords(player) + " enabled safety");
			} else {
				zLogger = LoggerManager.getLogger("pvp");
				string = player.username;
				zLogger.write("user " + string + " " + LoggerManager.getPlayerCoords(player) + " disabled safety");
			}

			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)86, byteBufferWriter);
					byteBufferWriter.putInt(player.OnlineID);
					byteBufferWriter.putByte((byte)(player.isSafety() ? 1 : 0));
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	public static void updateOverlayForClients(IsoObject object, String string, float float1, float float2, float float3, float float4, UdpConnection udpConnection) {
		if (udpEngine != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2 != null && object.square != null && udpConnection2.ReleventTo((float)object.square.x, (float)object.square.y) && (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID())) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)90, byteBufferWriter);
					GameWindow.WriteStringUTF(byteBufferWriter.bb, string);
					byteBufferWriter.putInt(object.getSquare().getX());
					byteBufferWriter.putInt(object.getSquare().getY());
					byteBufferWriter.putInt(object.getSquare().getZ());
					byteBufferWriter.putFloat(float1);
					byteBufferWriter.putFloat(float2);
					byteBufferWriter.putFloat(float3);
					byteBufferWriter.putFloat(float4);
					byteBufferWriter.putInt(object.getSquare().getObjects().indexOf(object));
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void updateOverlayFromClient(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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

	private static void syncSafehouse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		String string = GameWindow.ReadString(byteBuffer);
		int int5 = byteBuffer.getInt();
		SafeHouse safeHouse = SafeHouse.getSafeHouse(int1, int2, int3, int4);
		boolean boolean1 = false;
		if (safeHouse == null) {
			safeHouse = SafeHouse.addSafeHouse(int1, int2, int3, int4, string, false);
			boolean1 = true;
		}

		if (safeHouse != null) {
			safeHouse.getPlayers().clear();
			int int6;
			for (int6 = 0; int6 < int5; ++int6) {
				String string2 = GameWindow.ReadString(byteBuffer);
				safeHouse.addPlayer(string2);
			}

			int6 = byteBuffer.getInt();
			safeHouse.playersRespawn.clear();
			for (int int7 = 0; int7 < int6; ++int7) {
				String string3 = GameWindow.ReadString(byteBuffer);
				safeHouse.playersRespawn.add(string3);
			}

			boolean boolean2 = byteBuffer.get() == 1;
			safeHouse.setTitle(GameWindow.ReadString(byteBuffer));
			safeHouse.setOwner(string);
			sendSafehouse(safeHouse, boolean2, udpConnection);
			if (ChatServer.isInited()) {
				if (boolean1) {
					ChatServer.getInstance().createSafehouseChat(safeHouse.getId());
				}

				if (boolean2) {
					ChatServer.getInstance().removeSafehouseChat(safeHouse.getId());
				} else {
					ChatServer.getInstance().syncSafehouseChatMembers(safeHouse.getId(), string, safeHouse.getPlayers());
				}
			}

			if (boolean2) {
				SafeHouse.getSafehouseList().remove(safeHouse);
				DebugLog.log("safehouse: removed " + int1 + "," + int2 + "," + int3 + "," + int4 + " owner=" + safeHouse.getOwner());
			}
		}
	}

	public static void sendSafehouse(SafeHouse safeHouse, boolean boolean1, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection == null || udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)114, byteBufferWriter);
				byteBufferWriter.putInt(safeHouse.getX());
				byteBufferWriter.putInt(safeHouse.getY());
				byteBufferWriter.putInt(safeHouse.getW());
				byteBufferWriter.putInt(safeHouse.getH());
				byteBufferWriter.putUTF(safeHouse.getOwner());
				byteBufferWriter.putInt(safeHouse.getPlayers().size());
				Iterator iterator = safeHouse.getPlayers().iterator();
				while (iterator.hasNext()) {
					String string = (String)iterator.next();
					byteBufferWriter.putUTF(string);
				}

				byteBufferWriter.putInt(safeHouse.playersRespawn.size());
				for (int int2 = 0; int2 < safeHouse.playersRespawn.size(); ++int2) {
					byteBufferWriter.putUTF((String)safeHouse.playersRespawn.get(int2));
				}

				byteBufferWriter.putBoolean(boolean1);
				byteBufferWriter.putUTF(safeHouse.getTitle());
				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void dealWithNetDataShort(ZomboidNetData zomboidNetData, ByteBuffer byteBuffer, UdpConnection udpConnection) {
		short short1 = byteBuffer.getShort();
		switch (short1) {
		case 1000: 
			receiveWaveSignal(byteBuffer);
			break;
		
		case 1001: 
			receivePlayerListensChannel(byteBuffer);
			break;
		
		case 1002: 
			sendRadioServerData(udpConnection);
			break;
		
		case 1004: 
			receiveRadioDeviceDataState(byteBuffer, udpConnection);
			break;
		
		case 1200: 
			SyncCustomLightSwitchSettings(byteBuffer, udpConnection);
		
		}
	}

	public static void receiveRadioDeviceDataState(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
			short short1 = byteBuffer.getShort();
			short short2 = byteBuffer.getShort();
			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short1);
			if (baseVehicle != null) {
				VehiclePart vehiclePart = baseVehicle.getPartByIndex(short2);
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

	private static void sendRadioServerData(UdpConnection udpConnection) {
		ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
		PacketTypesShort.doPacket((short)1002, byteBufferWriter);
		ZomboidRadio.getInstance().WriteRadioServerDataPacket(byteBufferWriter);
		udpConnection.endPacketImmediate();
	}

	public static void sendIsoWaveSignal(int int1, int int2, int int3, String string, String string2, float float1, float float2, float float3, int int4, boolean boolean1) {
		for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int5);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypesShort.doPacket((short)1000, byteBufferWriter);
			byteBufferWriter.putInt(int1);
			byteBufferWriter.putInt(int2);
			byteBufferWriter.putInt(int3);
			byteBufferWriter.putBoolean(string != null);
			if (string != null) {
				GameWindow.WriteString(byteBufferWriter.bb, string);
			}

			byteBufferWriter.putByte((byte)(string2 != null ? 1 : 0));
			if (string2 != null) {
				byteBufferWriter.putUTF(string2);
			}

			byteBufferWriter.putFloat(float1);
			byteBufferWriter.putFloat(float2);
			byteBufferWriter.putFloat(float3);
			byteBufferWriter.putInt(int4);
			byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
			udpConnection.endPacketImmediate();
		}
	}

	public static void receiveWaveSignal(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		String string = null;
		if (boolean1) {
			string = GameWindow.ReadString(byteBuffer);
		}

		String string2 = null;
		if (byteBuffer.get() == 1) {
			string2 = GameWindow.ReadString(byteBuffer);
		}

		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		int int4 = byteBuffer.getInt();
		boolean boolean2 = byteBuffer.get() == 1;
		ZomboidRadio.getInstance().ReceiveTransmission(int1, int2, int3, string, string2, float1, float2, float3, int4, boolean2);
	}

	public static void receivePlayerListensChannel(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		boolean boolean2 = byteBuffer.get() == 1;
		ZomboidRadio.getInstance().PlayerListensChannel(int1, boolean1, boolean2);
	}

	public static void sendAlarm(int int1, int int2) {
		for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int3);
			IsoPlayer player = getAnyPlayerFromConnection(udpConnection);
			if (player != null) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)118, byteBufferWriter);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				udpConnection.endPacketImmediate();
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
			if (!bFastForward) {
				SendZombies = 0;
			}
		}
	}

	private static void receiveCustomColor(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
			if (udpConnection2.ReleventTo((float)int1, (float)int2) && (udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() || udpConnection == null)) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)121, byteBufferWriter);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putInt(int3);
				byteBufferWriter.putInt(int4);
				byteBufferWriter.putFloat(float1);
				byteBufferWriter.putFloat(float2);
				byteBufferWriter.putFloat(float3);
				byteBufferWriter.putFloat(float4);
				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void receiveFurnaceChange(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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

	public static void sendFuranceChange(BSFurnace bSFurnace, UdpConnection udpConnection) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.ReleventTo((float)bSFurnace.square.x, (float)bSFurnace.square.y) && (udpConnection != null && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() || udpConnection == null)) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)120, byteBufferWriter);
				byteBufferWriter.putInt(bSFurnace.square.x);
				byteBufferWriter.putInt(bSFurnace.square.y);
				byteBufferWriter.putInt(bSFurnace.square.z);
				byteBufferWriter.putByte((byte)(bSFurnace.isFireStarted() ? 1 : 0));
				byteBufferWriter.putFloat(bSFurnace.getFuelAmount());
				byteBufferWriter.putFloat(bSFurnace.getFuelDecrease());
				byteBufferWriter.putFloat(bSFurnace.getHeat());
				GameWindow.WriteString(byteBufferWriter.bb, bSFurnace.sSprite);
				GameWindow.WriteString(byteBufferWriter.bb, bSFurnace.sLitSprite);
				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void sendUserlog(ByteBuffer byteBuffer, UdpConnection udpConnection, String string) {
		ArrayList arrayList = ServerWorldDatabase.instance.getUserlog(string);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)128, byteBufferWriter);
				byteBufferWriter.putInt(arrayList.size());
				byteBufferWriter.putUTF(string);
				for (int int2 = 0; int2 < arrayList.size(); ++int2) {
					Userlog userlog = (Userlog)arrayList.get(int2);
					byteBufferWriter.putInt(Userlog.UserlogType.FromString(userlog.getType()).index());
					byteBufferWriter.putUTF(userlog.getText());
					byteBufferWriter.putUTF(userlog.getIssuedBy());
					byteBufferWriter.putInt(userlog.getAmount());
				}

				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void addUserlog(ByteBuffer byteBuffer, UdpConnection udpConnection) throws SQLException {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		String string3 = GameWindow.ReadString(byteBuffer);
		ServerWorldDatabase.instance.addUserlog(string, Userlog.UserlogType.FromString(string2), string3, udpConnection.username, 1);
		LoggerManager.getLogger("admin").write(udpConnection.username + " added log on user " + string + ", log: " + string3);
	}

	private static void removeUserlog(ByteBuffer byteBuffer, UdpConnection udpConnection) throws SQLException {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		String string3 = GameWindow.ReadString(byteBuffer);
		ServerWorldDatabase.instance.removeUserLog(string, string2, string3);
		LoggerManager.getLogger("admin").write(udpConnection.username + " removed log on user " + string + ", type:" + string2 + ", log: " + string3);
	}

	private static void addWarningPoint(ByteBuffer byteBuffer, UdpConnection udpConnection) throws SQLException {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		ServerWorldDatabase.instance.addWarningPoint(string, string2, int1, udpConnection.username);
		LoggerManager.getLogger("admin").write(udpConnection.username + " added " + int1 + " warning point(s) on " + string + ", reason:" + string2);
		for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
			if (udpConnection2.username.equals(string)) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)79, byteBufferWriter);
				byteBufferWriter.putUTF(udpConnection.username);
				byteBufferWriter.putUTF(" gave you " + int1 + " warning point(s), reason: " + string2 + " ");
				udpConnection2.endPacketImmediate();
			}
		}
	}

	public static void sendAdminMessage(String string, int int1, int int2, int int3) {
		for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int4);
			if (canSeePlayerStats(udpConnection)) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)132, byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putInt(int3);
				udpConnection.endPacketImmediate();
			}
		}
	}

	private static void wakeUpPlayer(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getInt());
		player.setAsleep(false);
		player.setAsleepTime(0.0F);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)133, byteBufferWriter);
				byteBufferWriter.putInt(player.OnlineID);
				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void sendDBSchema(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		DBSchema dBSchema = ServerWorldDatabase.instance.getDBSchema();
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection != null && udpConnection2.getConnectedGUID() == udpConnection.getConnectedGUID()) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)135, byteBufferWriter);
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

				udpConnection2.endPacketImmediate();
			}
		}
	}

	private static void sendTableResult(ByteBuffer byteBuffer, UdpConnection udpConnection) throws SQLException {
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
		PacketTypes.doPacket((short)136, byteBufferWriter);
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
				udpConnection.endPacketImmediate();
				doTableResult(udpConnection, string, arrayList, int1 + int3, int2);
				break;
			}
		}

		if (boolean1) {
			udpConnection.endPacketImmediate();
		}
	}

	private static void executeQuery(ByteBuffer byteBuffer, UdpConnection udpConnection) throws SQLException {
		if (udpConnection.accessLevel != null && udpConnection.accessLevel.equals("admin")) {
			try {
				String string = GameWindow.ReadString(byteBuffer);
				KahluaTable kahluaTable = LuaManager.platform.newTable();
				kahluaTable.load((ByteBuffer)byteBuffer, 184);
				ServerWorldDatabase.instance.executeQuery(string, kahluaTable);
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}

	private static void sendFactionInvite(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		String string3 = GameWindow.ReadString(byteBuffer);
		IsoPlayer player = getPlayerByUserName(string3);
		Long Long1 = (Long)IDToAddressMap.get(player.getOnlineID());
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection2.getConnectedGUID() == Long1) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)141, byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putUTF(string2);
				udpConnection2.endPacketImmediate();
				break;
			}
		}
	}

	private static void acceptedFactionInvite(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
					PacketTypes.doPacket((short)142, byteBufferWriter);
					byteBufferWriter.putUTF(string);
					byteBufferWriter.putUTF(string2);
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void viewTickets(ByteBuffer byteBuffer, UdpConnection udpConnection) throws SQLException {
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
				PacketTypes.doPacket((short)144, byteBufferWriter);
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

				udpConnection2.endPacketImmediate();
				break;
			}
		}
	}

	private static void addTicket(ByteBuffer byteBuffer, UdpConnection udpConnection) throws SQLException {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		int int1 = byteBuffer.getInt();
		if (int1 == -1) {
			sendAdminMessage("user " + string + " added a ticket <LINE> <LINE> " + string2, -1, -1, -1);
		}

		ServerWorldDatabase.instance.addTicket(string, string2, int1);
		sendTickets(string, udpConnection);
	}

	private static void removeTicket(ByteBuffer byteBuffer, UdpConnection udpConnection) throws SQLException {
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
				PacketTypes.doPacket((short)150, byteBufferWriter);
				byteBufferWriter.putByte((byte)(player2 != null ? 1 : 0));
				if (player2 != null) {
					byteBufferWriter.putShort((short)player2.getOnlineID());
				}

				byteBufferWriter.putByte((byte)(player != null ? 1 : 0));
				if (player != null) {
					byteBufferWriter.putShort((short)player.getOnlineID());
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

				udpConnection2.endPacketImmediate();
			}
		}

		return true;
	}

	private static void receiveItemListNet(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
			CompressIdenticalItems.load(byteBuffer, 184, arrayList, (ArrayList)null);
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
			PacketTypes.doPacket((short)172, byteBufferWriter);
			byteBufferWriter.putFloat(float1);
			udpConnection.endPacketImmediate();
		}
	}

	private static void receiveClimateManagerPacket(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		ClimateManager climateManager = ClimateManager.getInstance();
		if (climateManager != null) {
			try {
				climateManager.receiveClimatePacket(byteBuffer, udpConnection);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
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
												if (int1 <= 184) {
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
			DebugLog.log("map_t.bin does not exist, cannot determine the server\'s WorldVersion.");
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

	public static void receiveHitVehicle(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		HitPacket.HitVehicle hitVehicle = new HitPacket.HitVehicle();
		hitVehicle.parse(byteBuffer);
		if (hitVehicle.check()) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo(hitVehicle.wielder.x, hitVehicle.wielder.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)213, byteBufferWriter);
					hitVehicle.write(byteBufferWriter);
					udpConnection2.endPacketImmediate();
				}
			}

			DebugLog.log(DebugType.Multiplayer, "HitVehicle receive: " + hitVehicle.getDescription());
			if (hitVehicle.targetType == 1) {
				((IsoZombie)hitVehicle.target).networkAI.hitVehicle = hitVehicle;
			}
		}
	}

	public static void transmitBrokenGlass(IsoGridSquare square) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			try {
				if (udpConnection.ReleventTo((float)square.getX(), (float)square.getY())) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.doPacket((short)175, byteBufferWriter);
					byteBufferWriter.putInt((short)square.getX());
					byteBufferWriter.putInt((short)square.getY());
					byteBufferWriter.putInt((short)square.getZ());
					udpConnection.endPacketImmediate();
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

	private static void receiveChrHitByVehicle(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		boolean boolean1 = byteBuffer.get() == 1;
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						try {
							ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
							PacketTypes.doPacket((short)176, byteBufferWriter);
							byteBufferWriter.putShort((short)player.OnlineID);
							byteBufferWriter.putByte((byte)(boolean1 ? 1 : 0));
							udpConnection2.endPacketImmediate();
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	private static void receiveSyncPerks(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
							PacketTypes.doPacket((short)177, byteBufferWriter);
							byteBufferWriter.putShort((short)player.OnlineID);
							byteBufferWriter.putInt(int1);
							byteBufferWriter.putInt(int2);
							byteBufferWriter.putInt(int3);
							udpConnection2.endPacketImmediate();
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	private static void receiveSyncWeight(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		float float1 = byteBuffer.getFloat();
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						try {
							ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
							PacketTypes.doPacket((short)178, byteBufferWriter);
							byteBufferWriter.putShort((short)player.OnlineID);
							byteBufferWriter.putFloat(float1);
							udpConnection2.endPacketImmediate();
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	private static void receiveSyncInjuries(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			player.setVariable("WalkSpeed", float1);
			player.setVariable("WalkInjury", float2);
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						try {
							ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
							PacketTypes.doPacket((short)179, byteBufferWriter);
							byteBufferWriter.putShort((short)player.OnlineID);
							byteBufferWriter.putFloat(float1);
							byteBufferWriter.putFloat(float2);
							udpConnection2.endPacketImmediate();
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	private static void receiveSyncEquippedRadioFreq(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
							PacketTypes.doPacket((short)181, byteBufferWriter);
							byteBufferWriter.putShort((short)player.OnlineID);
							byteBufferWriter.putInt(int1);
							for (int int4 = 0; int4 < arrayList.size(); ++int4) {
								byteBufferWriter.putInt((Integer)arrayList.get(int4));
							}

							udpConnection2.endPacketImmediate();
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	private static void receiveHitReactionFromZombie(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		String string = GameWindow.ReadStringUTF(byteBuffer);
		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						try {
							ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
							PacketTypes.doPacket((short)180, byteBufferWriter);
							byteBufferWriter.putShort((short)player.OnlineID);
							byteBufferWriter.putUTF(string);
							udpConnection2.endPacketImmediate();
						} catch (Throwable throwable) {
							udpConnection.cancelPacket();
							ExceptionLogger.logException(throwable);
						}
					}
				}
			}
		}
	}

	private static void receiveGlobalModData(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		GlobalModData.instance.receive(byteBuffer);
	}

	private static void receiveGlobalModDataRequest(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		GlobalModData.instance.receiveRequest(byteBuffer, udpConnection);
	}

	private static void sendSafehouseInvite(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
				PacketTypes.doPacket((short)193, byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putUTF(string2);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putInt(int3);
				byteBufferWriter.putInt(int4);
				udpConnection2.endPacketImmediate();
				break;
			}
		}
	}

	private static void acceptedSafehouseInvite(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		String string = GameWindow.ReadString(byteBuffer);
		String string2 = GameWindow.ReadString(byteBuffer);
		String string3 = GameWindow.ReadString(byteBuffer);
		IsoPlayer player = getPlayerByUserName(string2);
		Long Long1 = (Long)IDToAddressMap.get(player.getOnlineID());
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
			if (udpConnection2.getConnectedGUID() == Long1) {
				ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
				PacketTypes.doPacket((short)194, byteBufferWriter);
				byteBufferWriter.putUTF(string);
				byteBufferWriter.putUTF(string2);
				byteBufferWriter.putUTF(string3);
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putInt(int3);
				byteBufferWriter.putInt(int4);
				udpConnection2.endPacketImmediate();
			}
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
		SendZombies = 0;
		launched = false;
		consoleCommands = new ArrayList();
		MainLoopPlayerUpdate = new HashMap();
		MainLoopNetDataHighPrioritet = new ArrayList();
		MainLoopNetData = new ArrayList();
		MainLoopNetData2 = new ArrayList();
		playerToCoordsMap = new HashMap();
		playerMovedToFastMap = new HashMap();
		large_file_bb = ByteBuffer.allocate(3145728);
		previousSave = Calendar.getInstance().getTimeInMillis();
		droppedPackets = 0;
		countOfDroppedPackets = 0;
		countOfDroppedConnections = 0;
	}

	private static class s_performance {
		static final PerformanceProfileFrameProbe frameStep = new PerformanceProfileFrameProbe("GameServer.frameStep");
		static final PerformanceProfileProbe mainLoopDealWithNetData = new PerformanceProfileProbe("GameServer.mainLoopDealWithNetData");
		static final PerformanceProfileProbe RCONServerUpdate = new PerformanceProfileProbe("RCONServer.update");
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
					if (SteamUtils.isSteamModeEnabled()) {
						long long1 = GameServer.udpEngine.getClientSteamID(udpConnection.getConnectedGUID());
						this.hostString = SteamUtils.convertSteamIDToString(long1);
					} else {
						this.hostString = udpConnection.getInetSocketAddress().getHostString();
					}
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
