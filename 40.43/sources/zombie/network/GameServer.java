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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientSoundManager;
import zombie.AmbientStreamManager;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SoundManager;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.states.DieState;
import zombie.ai.states.StaggerBackDieState;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.skills.PerkFactory;
import zombie.chat.ChatMessage;
import zombie.commands.CommandBase;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.ProxyPrintStream;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferReader;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.Bullet;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.raknet.RakVoice;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;
import zombie.core.stash.StashSystem;
import zombie.core.textures.ColorInfo;
import zombie.core.znet.PortMapper;
import zombie.core.znet.SteamGameServer;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.erosion.ErosionMain;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.IngameState;
import zombie.globalObjects.SGlobalObjects;
import zombie.inventory.CompressIdenticalItems;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Radio;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.ObjectsSyncRequests;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegion;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWoodenWall;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoAnim;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.weather.ClimateManager;
import zombie.network.chat.ChatServer;
import zombie.popman.MPDebugInfo;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.PublicServerUtil;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;


public class GameServer {
	public static final int MAX_PLAYERS = 512;
	public static int test = 432432;
	public static int DEFAULT_PORT = 16261;
	public static String IPCommandline = null;
	public static int PortCommandline = -1;
	public static int SteamPortCommandline1 = -1;
	public static int SteamPortCommandline2 = -1;
	public static Boolean SteamVACCommandline;
	public static boolean GUICommandline;
	public static final int FPS = 10;
	public static boolean bServer = false;
	public static boolean bDebug = false;
	public static UdpEngine udpEngine;
	static int count = 0;
	static UdpConnection[] SlotToConnection = new UdpConnection[512];
	public static HashMap IDToAddressMap = new HashMap();
	public static HashMap IDToPlayerMap = new HashMap();
	static HashMap PlayerToAddressMap = new HashMap();
	public static ArrayList Players = new ArrayList();
	static int ClientIDMax = 0;
	public static float timeSinceKeepAlive = 0.0F;
	public static int MaxTicksSinceKeepAliveBeforeStall = 60;
	private static int SendZombies = 0;
	public static HashMap PlayerToBody = new HashMap();
	public static HashSet DebugPlayer = new HashSet();
	public static int ResetID = 0;
	public static ArrayList ServerMods = new ArrayList();
	public static ArrayList WorkshopItems = new ArrayList();
	public static String[] WorkshopInstallFolders;
	public static long[] WorkshopTimeStamps;
	public static KahluaTable SpawnRegions;
	private static ArrayList SpawnPoints;
	private static ArrayList SpawnBuildings;
	public static String ServerName = "servertest";
	public static String checksum = "";
	private static boolean bDone;
	private String poisonousBerry = null;
	private String poisonousMushroom = null;
	private String difficulty = "Hardcore";
	public static String GameMap = "Muldraugh, KY";
	public static boolean bFastForward;
	public static float FastForwardMultiplier = 40.0F;
	public static boolean UseTCPForMapDownloads;
	public static final long[] packetCounts = new long[256];
	public static HashMap transactionIDMap = new HashMap();
	public static ObjectsSyncRequests worldObjectsServerSyncReq = new ObjectsSyncRequests(false);
	public static String discordToken = null;
	public static String discordChannel = null;
	public static final DiscordBot discordBot;
	private static boolean launched;
	private static ArrayList consoleCommands;
	private static ArrayList MainLoopNetData;
	private static ArrayList MainLoopNetData2;
	private static final HashMap ccFilters;
	static ArrayList alreadyRemoved;
	static int CellLoaderX;
	static int CellLoaderY;
	private static HashMap playerToCoordsMap;
	private static HashMap playerMovedToFastMap;
	private static ByteBuffer large_file_bb;
	private static long previousSave;
	static ArrayList toCreateOnClient;
	public static String ip;
	static final ArrayList incomingNetData;

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

	private static void testTCPListen(int int1) {
		try {
			ServerSocket serverSocket = new ServerSocket();
			serverSocket.setSoTimeout(8000);
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(int1));
			Socket socket = serverSocket.accept();
			socket.close();
			serverSocket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private static void prevent32BitServer() {
		String string = System.getProperty("sun.arch.data.model");
		String string2 = System.getProperty("zomboid.force32");
		boolean boolean1 = string2 != null && string2.equals("1");
		if (!string.equals("64") && !boolean1) {
			System.err.println("32-bit server is not supported, please restart in 64-bit mode");
			System.exit(-1);
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
		for (int int1 = 0; int1 < stringArray.length; ++int1) {
			if (stringArray[int1] != null && stringArray[int1].startsWith("-cachedir=")) {
				GameWindow.setCacheDir(stringArray[int1].replace("-cachedir=", "").trim());
			}
		}

		boolean boolean1 = false;
		int int2;
		for (int2 = 0; int2 < stringArray.length; ++int2) {
			if (stringArray[int2].equals("-coop")) {
				try {
					boolean1 = true;
					setupCoop();
					CoopSlave.status(Translator.getText("UI_ServerStatus_Initialising"));
					break;
				} catch (FileNotFoundException fileNotFoundException) {
					fileNotFoundException.printStackTrace();
					SteamUtils.shutdown();
					System.exit(37);
					return;
				}
			}
		}

		String string;
		if (!boolean1) {
			string = GameWindow.getCacheDir() + File.separator + "server-console.txt";
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(string);
				PrintStream printStream = new PrintStream(fileOutputStream, true);
				System.setOut(new ProxyPrintStream(System.out, printStream));
				System.setErr(new ProxyPrintStream(System.err, printStream));
			} catch (FileNotFoundException fileNotFoundException2) {
				fileNotFoundException2.printStackTrace();
			}
		}

		bServer = true;
		Bullet.init();
		Rand.init();
		DebugLog.enableLog(DebugType.General, true);
		DebugLog.enableLog(DebugType.Network, true);
		DebugLog.enableLog(DebugType.Lua, true);
		if (System.getProperty("debug") != null) {
			bDebug = true;
			Core.bDebug = true;
		}

		DebugLog.log("versionNumber=" + Core.getInstance().getVersionNumber() + " demo=false");
		int int3;
		String string2;
		for (int2 = 0; int2 < stringArray.length; ++int2) {
			if (stringArray[int2] != null) {
				if (stringArray[int2].startsWith("-debuglog=")) {
					String[] stringArray2 = stringArray[int2].replace("-debuglog=", "").split(",");
					int int4 = stringArray2.length;
					for (int3 = 0; int3 < int4; ++int3) {
						string2 = stringArray2[int3];
						try {
							DebugLog.enableLog(DebugType.valueOf(string2), true);
						} catch (IllegalArgumentException illegalArgumentException) {
						}
					}
				} else if (stringArray[int2].equals("-adminusername")) {
					if (int2 == stringArray.length - 1) {
						DebugLog.log("expected argument after \"-adminusername\"");
						System.exit(0);
					} else if (!ServerWorldDatabase.isValidUserName(stringArray[int2 + 1].trim())) {
						DebugLog.log("invalid username given to \"-adminusername\"");
						System.exit(0);
					} else {
						ServerWorldDatabase.instance.CommandLineAdminUsername = stringArray[int2 + 1].trim();
						++int2;
					}
				} else if (stringArray[int2].equals("-adminpassword")) {
					if (int2 == stringArray.length - 1) {
						DebugLog.log("expected argument after \"-adminpassword\"");
						System.exit(0);
					} else if (stringArray[int2 + 1].trim().isEmpty()) {
						DebugLog.log("empty argument given to \"-adminpassword\"");
						System.exit(0);
					} else {
						ServerWorldDatabase.instance.CommandLineAdminPassword = stringArray[int2 + 1].trim();
						++int2;
					}
				} else if (stringArray[int2].startsWith("-cachedir=")) {
					GameWindow.setCacheDir(stringArray[int2].replace("-cachedir=", "").trim());
				} else if (stringArray[int2].equals("-ip")) {
					IPCommandline = parseIPFromCommandline(stringArray, int2, "-ip");
					++int2;
				} else if (stringArray[int2].equals("-gui")) {
					GUICommandline = true;
				} else if (stringArray[int2].equals("-nosteam")) {
					System.setProperty("zomboid.steam", "0");
				} else if (stringArray[int2].equals("-port")) {
					PortCommandline = parsePortFromCommandline(stringArray, int2, "-port");
					++int2;
				} else if (stringArray[int2].equals("-steamport1")) {
					SteamPortCommandline1 = parsePortFromCommandline(stringArray, int2, "-steamport1");
					++int2;
				} else if (stringArray[int2].equals("-steamport2")) {
					SteamPortCommandline2 = parsePortFromCommandline(stringArray, int2, "-steamport2");
					++int2;
				} else if (stringArray[int2].equals("-steamvac")) {
					SteamVACCommandline = parseBooleanFromCommandline(stringArray, int2, "-steamvac");
					++int2;
				} else if (stringArray[int2].equals("-servername")) {
					if (int2 == stringArray.length - 1) {
						DebugLog.log("expected argument after \"-servername\"");
						System.exit(0);
					} else if (stringArray[int2 + 1].trim().isEmpty()) {
						DebugLog.log("empty argument given to \"-servername\"");
						System.exit(0);
					} else {
						ServerName = stringArray[int2 + 1].trim();
						++int2;
					}
				} else if (stringArray[int2].equals("-coop")) {
					ServerWorldDatabase.instance.doAdmin = false;
				} else {
					DebugLog.log("unknown option \"" + stringArray[int2] + "\"");
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

			String string3;
			if (SteamUtils.isSteamModeEnabled()) {
				string3 = ServerOptions.instance.PublicName.getValue();
				if (string3 == null || string3.isEmpty()) {
					ServerOptions.instance.PublicName.setValue("My PZ Server");
				}
			}

			string3 = ServerOptions.instance.Map.getValue();
			if (string3 != null && !string3.trim().isEmpty()) {
				GameMap = string3.trim();
				if (GameMap.contains(";")) {
					String[] stringArray3 = GameMap.split(";");
					string3 = stringArray3[0];
				}

				Core.GameMap = string3.trim();
			}

			String string4 = ServerOptions.instance.Mods.getValue();
			int int5;
			String string5;
			if (string4 != null) {
				String[] stringArray4 = string4.split(";");
				String[] stringArray5 = stringArray4;
				int5 = stringArray4.length;
				for (int int6 = 0; int6 < int5; ++int6) {
					string5 = stringArray5[int6];
					if (!string5.trim().isEmpty()) {
						ServerMods.add(string5.trim());
					}
				}
			}

			int int7;
			int int8;
			if (SteamUtils.isSteamModeEnabled()) {
				int3 = ServerOptions.instance.SteamPort1.getValue();
				int int9 = ServerOptions.instance.SteamPort2.getValue();
				int5 = ServerOptions.instance.SteamVAC.getValue() ? 3 : 2;
				if (!SteamGameServer.Init(IPCommandline, int3, int9, DEFAULT_PORT, int5, Core.getInstance().getSteamServerVersion())) {
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
					SteamGameServer.SetGameTags(ServerOptions.instance.Mods.getValue() + (CoopSlave.instance != null ? ";hosted" : ""));
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
						string5 = new String(byteArray, "UTF-8");
						String string6 = "";
						for (int7 = 0; int7 < 128; ++int7) {
							string6 = string6 + string5;
						}

						SteamGameServer.SetKeyValue("test", string6);
					} catch (UnsupportedEncodingException unsupportedEncodingException) {
					}

					SteamGameServer.SetKeyValue("test2", "12345");
				}

				String string7 = ServerOptions.instance.WorkshopItems.getValue();
				if (string7 != null) {
					String[] stringArray6 = string7.split(";");
					String[] stringArray7 = stringArray6;
					int7 = stringArray6.length;
					for (int int10 = 0; int10 < int7; ++int10) {
						String string8 = stringArray7[int10];
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
					int8 = SteamGameServer.GetSteamServersConnectState();
					if (int8 == SteamGameServer.STEAM_SERVERS_CONNECTED) {
						if (!GameServerWorkshopItems.Install(WorkshopItems)) {
							return;
						}

						break;
					}

					if (int8 == SteamGameServer.STEAM_SERVERS_CONNECTFAILURE) {
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

			int3 = 0;
			try {
				ServerWorldDatabase.instance.create();
			} catch (SQLException sQLException) {
				sQLException.printStackTrace();
			} catch (ClassNotFoundException classNotFoundException) {
				classNotFoundException.printStackTrace();
			}

			if (ServerOptions.instance.UPnP.getValue()) {
				DebugLog.log("Router detection/configuration starting.");
				DebugLog.log("If the server hangs here, set UPnP=false.");
				PortMapper.startup();
				if (PortMapper.discover()) {
					DebugLog.log("UPnP-enabled internet gateway found: " + PortMapper.getGatewayInfo());
					string2 = PortMapper.getExternalAddress();
					DebugLog.log("External IP address: " + string2);
					DebugLog.log("trying to setup port forwarding rules...");
					int5 = ServerOptions.instance.UPnPLeaseTime.getValue();
					boolean boolean2 = ServerOptions.instance.UPnPForce.getValue();
					if (PortMapper.addMapping(DEFAULT_PORT, DEFAULT_PORT, "PZ Server default port", "UDP", int5, boolean2)) {
						DebugLog.log(DebugType.Network, "Default port has been mapped successfully");
					} else {
						DebugLog.log(DebugType.Network, "Failed to map default port");
					}

					int int11;
					if (SteamUtils.isSteamModeEnabled()) {
						int8 = ServerOptions.instance.SteamPort1.getValue();
						if (PortMapper.addMapping(int8, int8, "PZ Server SteamPort1", "UDP", int5, boolean2)) {
							DebugLog.log(DebugType.Network, "SteamPort1 has been mapped successfully");
						} else {
							DebugLog.log(DebugType.Network, "Failed to map SteamPort1");
						}

						int11 = ServerOptions.instance.SteamPort2.getValue();
						if (PortMapper.addMapping(int11, int11, "PZ Server SteamPort2", "UDP", int5, boolean2)) {
							DebugLog.log(DebugType.Network, "SteamPort2 has been mapped successfully");
						} else {
							DebugLog.log(DebugType.Network, "Failed to map SteamPort2");
						}
					}

					if (UseTCPForMapDownloads) {
						for (int8 = 1; int8 <= ServerOptions.instance.MaxPlayers.getValue(); ++int8) {
							int11 = DEFAULT_PORT + int8;
							if (PortMapper.addMapping(int11, int11, "PZ Server TCP Port " + int8, "TCP", int5, boolean2)) {
								DebugLog.log(DebugType.Network, int11 + " has been mapped successfully");
							} else {
								DebugLog.log(DebugType.Network, "Failed to map TCP port " + int11);
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
			doMinimumInit();
			LosUtil.init(100, 100);
			ChatServer.getInstance().init();
			DebugLog.log(DebugType.Network, "Loading world...");
			CoopSlave.status(Translator.getText("UI_ServerStatus_LoadingWorld"));
			try {
				ClimateManager.setInstance(new ClimateManager());
				IsoWorld.instance.init();
			} catch (Exception exception) {
				exception.printStackTrace();
				DebugLog.log("server terminated");
				CoopSlave.status(Translator.getText("UI_ServerStatus_Terminated"));
				return;
			}

			File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "descriptors.bin");
			if (!file.exists()) {
				ServerOptions.instance.changeOption("ResetID", (new Integer(Rand.Next(100000000))).toString());
			}

			try {
				parseSpawnRegions();
				initSpawnBuildings();
			} catch (Exception exception2) {
				exception2.printStackTrace();
			}

			LuaEventManager.triggerEvent("OnGameTimeLoaded");
			SGlobalObjects.initSystems();
			SoundManager.instance = new SoundManager();
			AmbientStreamManager.instance = new AmbientSoundManager();
			AmbientStreamManager.instance.init();
			ServerMap.instance.LastSaved = System.currentTimeMillis();
			VehicleManager.instance = new VehicleManager();
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

			PerformanceSettings.LockFPS = 10;
			IngameState ingameState = new IngameState();
			float float1 = 0.0F;
			float float2 = 0.0F;
			float[] floatArray = new float[20];
			for (int7 = 0; int7 < 20; ++int7) {
				floatArray[int7] = (float)PerformanceSettings.LockFPS;
			}

			boolean boolean3 = false;
			float float3 = (float)PerformanceSettings.LockFPS;
			long long1 = 0L;
			int int12 = 0;
			long long2 = 0L;
			long long3 = 1000000000L / (long)PerformanceSettings.LockFPS;
			if (!SteamUtils.isSteamModeEnabled()) {
				PublicServerUtil.init();
				PublicServerUtil.insertOrUpdate();
			}

			ServerLOS.init();
			int int13 = ServerOptions.instance.RCONPort.getValue();
			String string9 = ServerOptions.instance.RCONPassword.getValue();
			if (int13 != 0 && string9 != null && !string9.isEmpty()) {
				RCONServer.init(int13, string9);
			}

			for (; !bDone; SteamUtils.runLoop()) {
				timeSinceKeepAlive += GameTime.getInstance().getMultiplier();
				long long4 = System.nanoTime();
				long long5 = System.currentTimeMillis();
				long3 = 1000000000L / (long)PerformanceSettings.LockFPS;
				double double1 = ServerOptions.instance.ZombieUpdateDelta.getValue();
				++SendZombies;
				if ((double)((float)SendZombies / float3) > double1) {
					SendZombies = 0;
				}

				ServerMap.instance.preupdate();
				synchronized (MainLoopNetData) {
					MainLoopNetData2.clear();
					MainLoopNetData2.addAll(MainLoopNetData);
					MainLoopNetData.clear();
				}

				int int14;
				for (int14 = 0; int14 < MainLoopNetData2.size(); ++int14) {
					IZomboidPacket iZomboidPacket = (IZomboidPacket)MainLoopNetData2.get(int14);
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
						mainLoopDealWithNetData((ZomboidNetData)iZomboidPacket);
					}
				}

				MainLoopNetData2.clear();
				int int15;
				synchronized (consoleCommands) {
					for (int15 = 0; int15 < consoleCommands.size(); ++int15) {
						String string10 = (String)consoleCommands.get(int15);
						try {
							if (CoopSlave.instance == null || !CoopSlave.instance.handleCommand(string10)) {
								System.out.println(handleServerCommand(string10, (UdpConnection)null));
							}
						} catch (Exception exception3) {
							exception3.printStackTrace();
						}
					}

					consoleCommands.clear();
				}

				RCONServer.update();
				MapCollisionData.instance.updateGameState();
				ingameState.update();
				try {
					VehicleManager.instance.serverUpdate();
				} catch (Exception exception4) {
					exception4.printStackTrace();
				}

				int14 = 0;
				int15 = 0;
				int int16;
				for (int16 = 0; int16 < Players.size(); ++int16) {
					IsoPlayer player = (IsoPlayer)Players.get(int16);
					if (CheckPlayerStillValid(player)) {
						if (!IsoWorld.instance.CurrentCell.getObjectList().contains(player)) {
							IsoWorld.instance.CurrentCell.getObjectList().add(player);
						}

						++int15;
						if (player.isAsleep()) {
							++int14;
						}
					}

					ServerMap.instance.characterIn(player);
				}

				setFastForward(ServerOptions.instance.SleepAllowed.getValue() && int15 > 0 && int14 == int15);
				UdpConnection udpConnection2;
				for (int16 = 0; int16 < udpEngine.connections.size(); ++int16) {
					udpConnection2 = (UdpConnection)udpEngine.connections.get(int16);
					for (int int17 = 0; int17 < 4; ++int17) {
						Vector3 vector3 = udpConnection2.connectArea[int17];
						if (vector3 != null) {
							ServerMap.instance.characterIn((int)vector3.x, (int)vector3.y, (int)vector3.z);
						}

						ClientServerMap.characterIn(udpConnection2, int17);
					}

					if (udpConnection2.playerDownloadServer != null) {
						udpConnection2.playerDownloadServer.update();
					}
				}

				for (int16 = 0; int16 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++int16) {
					IsoMovingObject movingObject = (IsoMovingObject)IsoWorld.instance.CurrentCell.getObjectList().get(int16);
					if (movingObject instanceof IsoPlayer && !Players.contains(movingObject)) {
						DebugLog.log("Disconnected player in CurrentCell.getObjectList() removed");
						IsoWorld.instance.CurrentCell.getObjectList().remove(int16--);
					}
				}

				++int3;
				if (int3 > 150) {
					for (int16 = 0; int16 < udpEngine.connections.size(); ++int16) {
						udpConnection2 = (UdpConnection)udpEngine.connections.get(int16);
						try {
							if (udpConnection2.username == null && !udpConnection2.awaitingCoopApprove) {
								disconnect(udpConnection2);
								udpEngine.forceDisconnect(udpConnection2.getConnectedGUID());
							}
						} catch (Exception exception5) {
							exception5.printStackTrace();
						}
					}

					int3 = 0;
				}

				worldObjectsServerSyncReq.serverSendRequests(udpEngine);
				ServerMap.instance.postupdate();
				if (ChunkRevisions.USE_CHUNK_REVISIONS) {
					ChunkRevisions.instance.updateServer();
				}

				try {
					ServerGUI.update();
				} catch (Exception exception6) {
					exception6.printStackTrace();
				}

				long long6 = System.nanoTime();
				long long7 = long6 - long4;
				long long8 = long3 - long7 - long1;
				if (long8 > 0L) {
					try {
						Thread.sleep(long8 / 1000000L);
					} catch (InterruptedException interruptedException2) {
					}

					long1 = System.nanoTime() - long6 - long8;
				} else {
					long2 -= long8;
					long1 = 0L;
					++int12;
					if (int12 >= 5) {
						Thread.yield();
						int12 = 0;
					}
				}

				long4 = System.nanoTime();
				long long9 = System.currentTimeMillis();
				long long10 = long9 - long5;
				float2 = 1000.0F / (float)long10;
				if (!Float.isNaN(float2)) {
					float3 = (float)((double)float3 + Math.min((double)(float2 - float3) * 0.05, 1.0));
				}

				GameTime.instance.FPSMultiplier = 60.0F / float3;
				launchCommandHandler();
				if (!SteamUtils.isSteamModeEnabled()) {
					PublicServerUtil.update();
					PublicServerUtil.updatePlayerCountIfChanged();
				}

				for (int int18 = 0; int18 < udpEngine.connections.size(); ++int18) {
					UdpConnection udpConnection3 = (UdpConnection)udpEngine.connections.get(int18);
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
						for (int int19 = 0; int19 < udpConnection3.chunkObjectState.size(); int19 += 2) {
							short short1 = udpConnection3.chunkObjectState.get(int19);
							short short2 = udpConnection3.chunkObjectState.get(int19 + 1);
							if (!udpConnection3.RelevantTo((float)(short1 * 10 + 5), (float)(short2 * 10 + 5), (float)(udpConnection3.ChunkGridWidth * 4 * 10))) {
								udpConnection3.chunkObjectState.remove(int19, 2);
								int19 -= 2;
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
			}

			CoopSlave.status(Translator.getText("UI_ServerStatus_Terminated"));
			DebugLog.log(DebugType.Network, "Server exited");
			ServerGUI.shutdown();
			SteamUtils.shutdown();
			System.exit(0);
		}
	}

	private static void launchCommandHandler() {
		if (!launched) {
			launched = true;
			(new Thread("command handler"){
				
				public void run() {
					try {
						BufferedReader var1 = new BufferedReader(new InputStreamReader(System.in));
						while (true) {
							String var2 = var1.readLine();
							if (var2 == null) {
								GameServer.consoleCommands.add("process-status@eof");
								break;
							}

							if (!var2.isEmpty()) {
								synchronized (GameServer.consoleCommands) {
									GameServer.consoleCommands.add(var2);
								}
							}
						}
					} catch (Exception var6) {
						var6.printStackTrace();
					}
				}
			}).start();
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
			byteBufferWriter.putByte((byte)(player.godMod ? 1 : 0));
			byteBufferWriter.putByte((byte)(player.invisible ? 1 : 0));
			byteBufferWriter.putByte((byte)(player.isNoClip() ? 1 : 0));
			byteBufferWriter.putByte((byte)(player.isShowAdminTag() ? 1 : 0));
			udpConnection2.endPacketImmediate();
		}
	}

	private static void receivePlayerExtraInfo(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(byteBuffer.getShort()));
		if (player != null) {
			udpConnection.accessLevel = GameWindow.ReadString(byteBuffer);
			player.godMod = byteBuffer.get() == 1;
			player.invisible = byteBuffer.get() == 1;
			player.GhostMode = player.invisible;
			player.setNoClip(byteBuffer.get() == 1);
			player.setShowAdminTag(byteBuffer.get() == 1);
			sendPlayerExtraInfo(player, udpConnection);
		}
	}

	private static void addLevelUpPoint(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (canModifyPlayerStats(udpConnection)) {
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getInt());
			if (player != null && !player.isDead()) {
				player.setNumberOfPerksToPick(player.getNumberOfPerksToPick() + 1);
			}

			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.getConnectedGUID() == (Long)PlayerToAddressMap.get(player)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)125, byteBufferWriter);
					byteBufferWriter.putInt(player.getOnlineID());
					udpConnection2.endPacketImmediate();
				}
			}
		}
	}

	private static void addXpFromPlayerStatsUI(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (canModifyPlayerStats(udpConnection)) {
			IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(byteBuffer.getInt());
			int int1 = byteBuffer.getInt();
			int int2 = 0;
			int int3 = 0;
			boolean boolean1 = false;
			if (player != null && !player.isDead()) {
				if (int1 == 0) {
					int3 = byteBuffer.getInt();
					int2 = byteBuffer.getInt();
					boolean1 = byteBuffer.get() == 1;
					player.getXp().AddXP(PerkFactory.Perks.fromIndex(int3), (float)int2, false, boolean1, false, true);
				} else if (int1 == 1) {
					int2 = byteBuffer.getInt();
					player.getXp().addGlobalXP((float)int2);
				}
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
					} else {
						byteBufferWriter.putInt(1);
						byteBufferWriter.putInt(int2);
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
					player.getXp().load(byteBuffer, 143);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}

				player.setNumberOfPerksToPick(byteBuffer.getInt());
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

						byteBufferWriter.putInt(player.getNumberOfPerksToPick());
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

	public static void doMinimumInit() {
		Rand.init();
		ZomboidFileSystem.instance.init();
		ArrayList arrayList = new ArrayList(ServerMods);
		ZomboidFileSystem.instance.loadMods(arrayList);
		LuaManager.init();
		Translator.loadFiles();
		PerkFactory.init();
		if (GUICommandline && System.getProperty("softreset") == null) {
			ServerGUI.init();
		}

		ScriptManager.instance.Load();
		try {
			LuaManager.initChecksum();
			LuaManager.LoadDirBase("shared");
			LuaManager.LoadDirBase("client", true);
			LuaManager.LoadDirBase("server");
			LuaManager.finishChecksum();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		File file = new File(GameWindow.getCacheDir() + File.separator + "Server" + File.separator + ServerName + "_SandboxVars.lua");
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
		SpawnRegions = LuaManager.platform.newTable();
		try {
			KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("SpawnRegionMgr");
			if (kahluaTable != null) {
				Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget("getSpawnRegions"));
				if (objectArray.length > 1 && objectArray[1] instanceof KahluaTable) {
					SpawnRegions = (KahluaTable)objectArray[1];
				}
			} else {
				DebugLog.log("ERROR: SpawnRegionMgr is undefined");
			}
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}

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
				CoopSlave.instance.sendMessage("server-address", (String)null, rakNetPeerInterface.GetServerIP() + ":" + DEFAULT_PORT);
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
		String string3 = ServerOptions.instance.DiscordToken.getValue();
		boolean boolean1 = ServerOptions.instance.DiscordEnable.getValue();
		String string4 = ServerOptions.instance.DiscordChannelID.getValue();
		discordBot.connect(boolean1, string3, string2, string4);
	}

	private static void printPacket(ZomboidNetData zomboidNetData) {
		if (zomboidNetData.type != 7) {
			DebugLog.log("# " + PacketTypes.packetTypeToString(zomboidNetData.type));
		}
	}

	private static void mainLoopDealWithNetData(ZomboidNetData zomboidNetData) {
		ByteBuffer byteBuffer = zomboidNetData.buffer;
		UdpConnection udpConnection = udpEngine.getActiveConnection(zomboidNetData.connection);
		if (zomboidNetData.type >= 0 && zomboidNetData.type < packetCounts.length) {
			int int1 = packetCounts[zomboidNetData.type]++;
			if (udpConnection != null) {
				int1 = udpConnection.packetCounts[zomboidNetData.type]++;
			}
		}

		try {
			if (udpConnection == null) {
				DebugLog.log(DebugType.Network, "Received packet type=" + PacketTypes.packetTypeToString(zomboidNetData.type) + " connection is null.");
				return;
			}

			if (udpConnection.username == null) {
				switch (zomboidNetData.type) {
				case 2: 
				
				case 50: 
				
				case 87: 
					break;
				
				default: 
					DebugLog.log("Received packet type=" + PacketTypes.packetTypeToString(zomboidNetData.type) + " before Login, disconnecting " + udpConnection.getInetSocketAddress().getHostString());
					udpConnection.forceDisconnect();
					ZomboidNetDataPool.instance.discard(zomboidNetData);
					return;
				
				}
			}

			String string;
			label345: switch (zomboidNetData.type) {
			case 2: 
				String string2 = GameWindow.ReadString(zomboidNetData.buffer).trim();
				String string3 = GameWindow.ReadString(zomboidNetData.buffer).trim();
				string = GameWindow.ReadString(zomboidNetData.buffer).trim();
				if (!string.equals(Core.getInstance().getVersionNumber())) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.doPacket((short)40, byteBufferWriter);
					LoggerManager.getLogger("user").write("access denied: user \"" + string2 + "\" client version (" + string + ") does not match server version (" + Core.getInstance().getVersionNumber() + ")");
					byteBufferWriter.putUTF("ClientVersionMismatch##" + string + "##" + Core.getInstance().getVersionNumber());
					udpConnection.endPacketImmediate();
					udpConnection.forceDisconnect();
				}

				boolean boolean1 = true;
				try {
					int int2 = byteBuffer.getInt();
				} catch (Exception exception) {
				}

				udpConnection.ip = udpConnection.getInetSocketAddress().getHostString();
				udpConnection.idStr = udpConnection.ip;
				if (SteamUtils.isSteamModeEnabled()) {
					udpConnection.steamID = udpEngine.getClientSteamID(udpConnection.getConnectedGUID());
					udpConnection.ownerID = udpEngine.getClientOwnerSteamID(udpConnection.getConnectedGUID());
					udpConnection.idStr = SteamUtils.convertSteamIDToString(udpConnection.steamID);
					if (udpConnection.steamID != udpConnection.ownerID) {
						udpConnection.idStr = udpConnection.idStr + "(owner=" + SteamUtils.convertSteamIDToString(udpConnection.ownerID) + ")";
					}
				}

				udpConnection.password = string3;
				LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + string2 + "\" attempting to join");
				ServerWorldDatabase.LogonResult logonResult;
				if (CoopSlave.instance != null && SteamUtils.isSteamModeEnabled()) {
					for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
						UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
						if (udpConnection2 != udpConnection && udpConnection2.steamID == udpConnection.steamID) {
							LoggerManager.getLogger("user").write("access denied: user \"" + string2 + "\" already connected");
							ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
							PacketTypes.doPacket((short)40, byteBufferWriter2);
							byteBufferWriter2.putUTF("AlreadyConnected");
							udpConnection.endPacketImmediate();
							udpConnection.forceDisconnect();
							return;
						}
					}

					udpConnection.username = string2;
					udpConnection.usernames[0] = string2;
					udpConnection.isCoopHost = udpEngine.connections.size() == 1;
					DebugLog.log(udpConnection.idStr + " isCoopHost=" + udpConnection.isCoopHost);
					udpConnection.accessLevel = "";
					if (!ServerOptions.instance.DoLuaChecksum.getValue() || udpConnection.accessLevel.equals("admin")) {
						udpConnection.checksumState = UdpConnection.ChecksumState.Done;
					}

					if (getPlayerCount() >= ServerOptions.instance.MaxPlayers.getValue()) {
						ByteBufferWriter byteBufferWriter3 = udpConnection.startPacket();
						PacketTypes.doPacket((short)40, byteBufferWriter3);
						byteBufferWriter3.putUTF("ServerFull");
						udpConnection.endPacketImmediate();
						udpConnection.forceDisconnect();
						return;
					}

					LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + string2 + "\" allowed to join");
					logonResult = ServerWorldDatabase.instance.new LogonResult();
					logonResult.accessLevel = udpConnection.accessLevel;
					receiveClientConnect(udpConnection, logonResult);
				} else {
					logonResult = ServerWorldDatabase.instance.authClient(string2, string3, udpConnection.ip, udpConnection.steamID);
					ByteBufferWriter byteBufferWriter4;
					if (logonResult.bAuthorized) {
						for (int int4 = 0; int4 < udpEngine.connections.size(); ++int4) {
							UdpConnection udpConnection3 = (UdpConnection)udpEngine.connections.get(int4);
							for (int int5 = 0; int5 < 4; ++int5) {
								if (string2.equals(udpConnection3.usernames[int5])) {
									LoggerManager.getLogger("user").write("access denied: user \"" + string2 + "\" already connected");
									ByteBufferWriter byteBufferWriter5 = udpConnection.startPacket();
									PacketTypes.doPacket((short)40, byteBufferWriter5);
									byteBufferWriter5.putUTF("AlreadyConnected");
									udpConnection.endPacketImmediate();
									udpConnection.forceDisconnect();
									return;
								}
							}
						}

						udpConnection.username = string2;
						udpConnection.usernames[0] = string2;
						transactionIDMap.put(string2, logonResult.transactionID);
						if (CoopSlave.instance != null) {
							udpConnection.isCoopHost = udpEngine.connections.size() == 1;
							DebugLog.log(udpConnection.idStr + " isCoopHost=" + udpConnection.isCoopHost);
						}

						udpConnection.accessLevel = logonResult.accessLevel;
						if (!ServerOptions.instance.DoLuaChecksum.getValue() || logonResult.accessLevel.equals("admin")) {
							udpConnection.checksumState = UdpConnection.ChecksumState.Done;
						}

						if (!logonResult.accessLevel.equals("") && getPlayerCount() >= ServerOptions.instance.MaxPlayers.getValue()) {
							byteBufferWriter4 = udpConnection.startPacket();
							PacketTypes.doPacket((short)40, byteBufferWriter4);
							byteBufferWriter4.putUTF("ServerFull");
							udpConnection.endPacketImmediate();
							udpConnection.forceDisconnect();
							return;
						}

						if (!ServerWorldDatabase.instance.containsUser(string2) && ServerWorldDatabase.instance.containsCaseinsensitiveUser(string2)) {
							byteBufferWriter4 = udpConnection.startPacket();
							PacketTypes.doPacket((short)40, byteBufferWriter4);
							byteBufferWriter4.putUTF("InvalidUsername");
							udpConnection.endPacketImmediate();
							udpConnection.forceDisconnect();
							return;
						}

						LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + string2 + "\" allowed to join");
						if (ServerOptions.instance.AutoCreateUserInWhiteList.getValue() && !ServerWorldDatabase.instance.containsUser(string2)) {
							ServerWorldDatabase.instance.addUser(string2, string3);
						} else {
							ServerWorldDatabase.instance.setPassword(string2, string3);
						}

						ServerWorldDatabase.instance.updateLastConnectionDate(string2, string3);
						if (SteamUtils.isSteamModeEnabled()) {
							String string4 = SteamUtils.convertSteamIDToString(udpConnection.steamID);
							ServerWorldDatabase.instance.setUserSteamID(string2, string4);
						}

						receiveClientConnect(udpConnection, logonResult);
					} else {
						byteBufferWriter4 = udpConnection.startPacket();
						PacketTypes.doPacket((short)40, byteBufferWriter4);
						if (logonResult.banned) {
							LoggerManager.getLogger("user").write("access denied: user \"" + string2 + "\" is banned");
							if (logonResult.bannedReason != null && !logonResult.bannedReason.isEmpty()) {
								byteBufferWriter4.putUTF("BannedReason##" + logonResult.bannedReason);
							} else {
								byteBufferWriter4.putUTF("Banned");
							}
						} else if (!logonResult.bAuthorized) {
							LoggerManager.getLogger("user").write("access denied: user \"" + string2 + "\" reason \"" + logonResult.dcReason + "\"");
							byteBufferWriter4.putUTF(logonResult.dcReason != null ? logonResult.dcReason : "AccessDenied");
						}

						udpConnection.endPacketImmediate();
						udpConnection.forceDisconnect();
					}
				}

				break;
			
			case 3: 
				ChunkRevisions.instance.serverPacket(zomboidNetData.type, byteBuffer, udpConnection);
				break;
			
			case 4: 
				MPDebugInfo.instance.serverPacket(byteBuffer, udpConnection);
				break;
			
			case 5: 
				VehicleManager.instance.serverPacket(byteBuffer, udpConnection);
				break;
			
			case 6: 
				receivePlayerConnect(byteBuffer, udpConnection, udpConnection.username);
				sendInitialWorldState(udpConnection);
				break;
			
			case 7: 
				receivePlayerInfo(byteBuffer, udpConnection);
			
			case 8: 
			
			case 9: 
			
			case 10: 
			
			case 11: 
			
			case 13: 
			
			case 15: 
			
			case 18: 
			
			case 19: 
			
			case 21: 
			
			case 29: 
			
			case 30: 
			
			case 38: 
			
			case 39: 
			
			case 40: 
			
			case 41: 
			
			case 49: 
			
			case 51: 
			
			case 52: 
			
			case 55: 
			
			case 59: 
			
			case 60: 
			
			case 61: 
			
			case 62: 
			
			case 63: 
			
			case 64: 
			
			case 66: 
			
			case 70: 
			
			case 71: 
			
			case 72: 
			
			case 73: 
			
			case 74: 
			
			case 77: 
			
			case 78: 
			
			case 82: 
			
			case 83: 
			
			case 85: 
			
			case 89: 
			
			case 93: 
			
			case 95: 
			
			case 96: 
			
			case 107: 
			
			case 113: 
			
			case 118: 
			
			case 119: 
			
			case 132: 
			
			case 158: 
			
			case 159: 
			
			case 163: 
			
			case 166: 
			
			case 167: 
			
			case 168: 
			
			case 169: 
			
			case 170: 
			
			case 171: 
			
			case 172: 
			
			case 173: 
			
			case 174: 
			
			case 175: 
			
			case 176: 
			
			case 177: 
			
			case 178: 
			
			case 179: 
			
			case 180: 
			
			case 181: 
			
			case 182: 
			
			case 183: 
			
			case 184: 
			
			case 186: 
			
			case 188: 
			
			case 189: 
			
			case 190: 
			
			case 191: 
			
			case 192: 
			
			case 193: 
			
			case 194: 
			
			case 195: 
			
			case 196: 
			
			case 197: 
			
			case 198: 
			
			case 199: 
			
			case 201: 
			
			default: 
				break;
			
			case 12: 
				SyncIsoObject(byteBuffer, udpConnection);
				break;
			
			case 14: 
				byte byte1 = byteBuffer.get();
				switch (byte1) {
				case 0: 
					byte byte2 = byteBuffer.get();
					string = GameWindow.ReadStringUTF(byteBuffer);
					IsoPlayer player = getPlayerFromConnection(udpConnection, byte2);
					if (player != null) {
						SteamGameServer.BUpdateUserData(player.getSteamID(), player.username, 0);
					}

				
				default: 
					break label345;
				
				}

			
			case 16: 
				PassengerMap.serverReceivePacket(byteBuffer, udpConnection);
				break;
			
			case 17: 
				AddItemToMap(byteBuffer, udpConnection);
				break;
			
			case 20: 
				sendItemsToContainer(byteBuffer, udpConnection);
				break;
			
			case 22: 
				removeItemFromContainer(byteBuffer, udpConnection);
				break;
			
			case 23: 
				RemoveItemFromMap(byteBuffer, udpConnection);
				break;
			
			case 24: 
				if (udpConnection.playerDownloadServer != null) {
					int int6 = byteBuffer.getInt();
					int int7 = byteBuffer.getInt();
					int int8 = byteBuffer.getInt();
					udpConnection.connectArea[0] = new Vector3((float)int6, (float)int7, (float)int8);
					udpConnection.ChunkGridWidth = int8;
					ZombiePopulationManager.instance.updateLoadedAreas();
				}

				break;
			
			case 25: 
				equip(byteBuffer, udpConnection);
				break;
			
			case 26: 
				hitZombie(byteBuffer, udpConnection);
				break;
			
			case 27: 
				addCoopPlayer(byteBuffer, udpConnection);
				break;
			
			case 28: 
				receiveWeaponHit(byteBuffer, udpConnection);
				break;
			
			case 31: 
				receiveSandboxOptions(byteBuffer);
				break;
			
			case 32: 
				IsoObject object = IsoWorld.instance.getItemFromXYZIndexBuffer(byteBuffer);
				if (object != null && object instanceof IsoWindow) {
					byte byte3 = byteBuffer.get();
					if (byte3 == 1) {
						((IsoWindow)object).smashWindow(true);
						smashWindow((IsoWindow)object, 1);
					} else if (byte3 == 2) {
						((IsoWindow)object).setGlassRemoved(true);
						smashWindow((IsoWindow)object, 2);
					}
				}

				break;
			
			case 33: 
				receivePlayerDeath(byteBuffer, udpConnection);
				break;
			
			case 34: 
				if (udpConnection.playerDownloadServer != null) {
					udpConnection.playerDownloadServer.receiveRequestArray(byteBuffer);
				}

				break;
			
			case 35: 
				receiveItemStats(byteBuffer, udpConnection);
				break;
			
			case 36: 
				if (udpConnection.playerDownloadServer != null) {
					udpConnection.playerDownloadServer.receiveCancelRequest(byteBuffer);
				}

				break;
			
			case 37: 
				receiveRequestData(byteBuffer, udpConnection);
				break;
			
			case 42: 
				doBandage(byteBuffer, udpConnection);
				break;
			
			case 43: 
				eatFood(byteBuffer, udpConnection);
				break;
			
			case 44: 
				requestItemsForContainer(byteBuffer, udpConnection);
				break;
			
			case 45: 
				drink(byteBuffer, udpConnection);
				break;
			
			case 46: 
				SyncAlarmClock(byteBuffer, udpConnection);
				break;
			
			case 47: 
				receivePacketCounts(byteBuffer, udpConnection);
				break;
			
			case 48: 
				loadModData(byteBuffer, udpConnection);
				break;
			
			case 50: 
				scoreboard(udpConnection);
				break;
			
			case 53: 
				receiveSound(byteBuffer, udpConnection);
				break;
			
			case 54: 
				receiveWorldSound(byteBuffer);
				break;
			
			case 56: 
				receiveClothing(byteBuffer, udpConnection);
				break;
			
			case 57: 
				receiveClientCommand(byteBuffer, udpConnection);
				break;
			
			case 58: 
				receiveObjectModData(byteBuffer, udpConnection);
				break;
			
			case 65: 
				SyncPlayerInventory(byteBuffer, udpConnection);
				break;
			
			case 67: 
				RequestPlayerData(byteBuffer, udpConnection);
				break;
			
			case 68: 
				removeCorpseFromMap(byteBuffer, udpConnection);
				break;
			
			case 69: 
				addCorpseToMap(byteBuffer, udpConnection);
				break;
			
			case 75: 
				startFireOnClient(byteBuffer, udpConnection);
				break;
			
			case 76: 
				updateItemSprite(byteBuffer, udpConnection);
				break;
			
			case 79: 
				sendWorldMessage(byteBuffer, udpConnection);
				break;
			
			case 80: 
				sendCustomModDataToClient(udpConnection);
				break;
			
			case 81: 
				ReceiveCommand(byteBuffer, udpConnection);
				break;
			
			case 84: 
				receivePlayerExtraInfo(byteBuffer, udpConnection);
				break;
			
			case 86: 
				toggleSafety(byteBuffer, udpConnection);
				break;
			
			case 87: 
				udpConnection.ping = true;
				answerPing(byteBuffer, udpConnection);
				break;
			
			case 88: 
				log(byteBuffer, udpConnection);
				break;
			
			case 90: 
				updateOverlayFromClient(byteBuffer, udpConnection);
				break;
			
			case 91: 
				NetChecksum.comparer.serverPacket(byteBuffer, udpConnection);
				break;
			
			case 92: 
				constructedZone(byteBuffer, udpConnection);
				break;
			
			case 94: 
				registerZone(byteBuffer, udpConnection);
				break;
			
			case 97: 
				doWoundInfection(byteBuffer, udpConnection);
				break;
			
			case 98: 
				doStitch(byteBuffer, udpConnection);
				break;
			
			case 99: 
				doDisinfect(byteBuffer, udpConnection);
				break;
			
			case 100: 
				doAdditionalPain(byteBuffer, udpConnection);
				break;
			
			case 101: 
				doRemoveGlass(byteBuffer, udpConnection);
				break;
			
			case 102: 
				doSplint(byteBuffer, udpConnection);
				break;
			
			case 103: 
				doRemoveBullet(byteBuffer, udpConnection);
				break;
			
			case 104: 
				doCleanBurn(byteBuffer, udpConnection);
				break;
			
			case 105: 
				SyncThumpable(byteBuffer, udpConnection);
				break;
			
			case 106: 
				SyncDoorKey(byteBuffer, udpConnection);
				break;
			
			case 108: 
				teleport(byteBuffer, udpConnection);
				break;
			
			case 109: 
				removeBlood(byteBuffer, udpConnection);
				break;
			
			case 110: 
				AddExplosiveTrap(byteBuffer, udpConnection);
				break;
			
			case 111: 
				removeSpecialObject(byteBuffer, udpConnection);
				break;
			
			case 112: 
				receiveBodyDamageUpdate(byteBuffer, udpConnection);
				break;
			
			case 114: 
				syncSafehouse(byteBuffer, udpConnection);
				break;
			
			case 115: 
				destroy(byteBuffer, udpConnection);
				break;
			
			case 116: 
				stopFire(byteBuffer, udpConnection);
				break;
			
			case 117: 
				doCataplasm(byteBuffer, udpConnection);
				break;
			
			case 120: 
				receiveFurnaceChange(byteBuffer, udpConnection);
				break;
			
			case 121: 
				receiveCustomColor(byteBuffer, udpConnection);
				break;
			
			case 122: 
				syncCompost(byteBuffer, udpConnection);
				break;
			
			case 123: 
				receivePlayerStatsChanges(byteBuffer, udpConnection);
				break;
			
			case 124: 
				addXpFromPlayerStatsUI(byteBuffer, udpConnection);
				break;
			
			case 125: 
				addLevelUpPoint(byteBuffer, udpConnection);
				break;
			
			case 126: 
				syncXp(byteBuffer, udpConnection);
				break;
			
			case 127: 
				dealWithNetDataShort(zomboidNetData, byteBuffer, udpConnection);
				break;
			
			case 128: 
				sendUserlog(byteBuffer, udpConnection, GameWindow.ReadString(byteBuffer));
				break;
			
			case 129: 
				addUserlog(byteBuffer, udpConnection);
				break;
			
			case 130: 
				removeUserlog(byteBuffer, udpConnection);
				break;
			
			case 131: 
				addWarningPoint(byteBuffer, udpConnection);
				break;
			
			case 133: 
				wakeUpPlayer(byteBuffer, udpConnection);
				break;
			
			case 134: 
				receiveTransactionID(byteBuffer, udpConnection);
				break;
			
			case 135: 
				sendDBSchema(byteBuffer, udpConnection);
				break;
			
			case 136: 
				sendTableResult(byteBuffer, udpConnection);
				break;
			
			case 137: 
				executeQuery(byteBuffer, udpConnection);
				break;
			
			case 138: 
				receiveTextColor(byteBuffer, udpConnection);
				break;
			
			case 139: 
				syncNonPvpZone(byteBuffer, udpConnection);
				break;
			
			case 140: 
				syncFaction(byteBuffer, udpConnection);
				break;
			
			case 141: 
				sendFactionInvite(byteBuffer, udpConnection);
				break;
			
			case 142: 
				acceptedFactionInvite(byteBuffer, udpConnection);
				break;
			
			case 143: 
				addTicket(byteBuffer, udpConnection);
				break;
			
			case 144: 
				viewTickets(byteBuffer, udpConnection);
				break;
			
			case 145: 
				removeTicket(byteBuffer, udpConnection);
				break;
			
			case 146: 
				requestTrading(byteBuffer, udpConnection);
				break;
			
			case 147: 
				tradingUIAddItem(byteBuffer, udpConnection);
				break;
			
			case 148: 
				tradingUIRemoveItem(byteBuffer, udpConnection);
				break;
			
			case 149: 
				tradingUIUpdateState(byteBuffer, udpConnection);
				break;
			
			case 150: 
				receiveItemListNet(byteBuffer, udpConnection);
				break;
			
			case 151: 
				receiveChunkObjectState(byteBuffer, udpConnection);
				break;
			
			case 152: 
				readAnnotedMap(byteBuffer, udpConnection);
				break;
			
			case 153: 
				requestInventory(byteBuffer, udpConnection);
				break;
			
			case 154: 
				sendInventory(byteBuffer, udpConnection);
				break;
			
			case 155: 
				invMngSendItem(byteBuffer, udpConnection);
				break;
			
			case 156: 
				invMngGotItem(byteBuffer, udpConnection);
				break;
			
			case 157: 
				invMngRemoveItem(byteBuffer, udpConnection);
				break;
			
			case 160: 
				GameTime.getInstance();
				GameTime.receiveTimeSync(byteBuffer, udpConnection);
				break;
			
			case 161: 
				SyncIsoObjectReq(byteBuffer, udpConnection);
				break;
			
			case 162: 
				receivePlayerSave(byteBuffer, udpConnection);
				break;
			
			case 164: 
				short short1 = byteBuffer.getShort();
				if (short1 == 1) {
					SyncObjectChunkHashes(byteBuffer, udpConnection);
				} else if (short1 == 3) {
					SyncObjectsGridSquareRequest(byteBuffer, udpConnection);
				} else if (short1 == 5) {
					SyncObjectsRequest(byteBuffer, udpConnection);
				}

				break;
			
			case 165: 
				receivePlayerOnBeaten(byteBuffer, udpConnection);
				break;
			
			case 185: 
				ChatServer.getInstance().processMessageFromPlayerPacket(byteBuffer);
				break;
			
			case 187: 
				ChatServer.getInstance().processPlayerStartWhisperChatPacket(byteBuffer);
				break;
			
			case 200: 
				receiveClimateManagerPacket(byteBuffer, udpConnection);
				break;
			
			case 202: 
				IsoRegion.receiveClientRequestFullDataChunks(byteBuffer, udpConnection);
			
			}
		} catch (Exception exception2) {
			if (udpConnection == null) {
				DebugLog.log(DebugType.Network, "Error with packet of type: " + zomboidNetData.type + " connection is null.");
			} else {
				DebugLog.log(DebugType.Network, "Error with packet of type: " + zomboidNetData.type + " for " + udpConnection.username);
			}

			exception2.printStackTrace();
		}

		ZomboidNetDataPool.instance.discard(zomboidNetData);
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
		String string = GameWindow.ReadString(byteBuffer);
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
		if (inventoryItem != null) {
			byte byte1 = byteBuffer.get();
			try {
				inventoryItem.load(byteBuffer, 143, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return;
			}

			Long Long1 = (Long)IDToAddressMap.get(int2);
			if (Long1 != null) {
				for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
					if (udpConnection2.getConnectedGUID() == Long1) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)147, byteBufferWriter);
						byteBufferWriter.putInt(int1);
						try {
							inventoryItem.save(byteBufferWriter.bb, false);
						} catch (IOException ioException2) {
							ioException2.printStackTrace();
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
				handWeapon.save(byteBufferWriter.bb, false);
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
			String string = GameWindow.ReadString(byteBuffer);
			byte byte1 = byteBuffer.get();
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
			HandWeapon handWeapon = (HandWeapon)inventoryItem;
			try {
				handWeapon.load(byteBuffer, 143, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return;
			}

			DebugLog.log("trap: user \"" + udpConnection.username + "\" added " + string + " at " + int1 + "," + int2 + "," + int3);
			LoggerManager.getLogger("map").write(udpConnection.idStr + " \"" + udpConnection.username + "\" added " + string + " at " + int1 + "," + int2 + "," + int3);
			IsoTrap trap = new IsoTrap(handWeapon, square.getCell(), square);
			int int4 = byteBuffer.getInt();
			boolean boolean1 = byteBuffer.get() == 1;
			boolean boolean2 = byteBuffer.get() == 1;
			if (boolean2) {
				square.drawCircleExplosion(int4, trap, boolean1);
				trap.removeFromWorld();
			} else {
				square.AddTileObject(trap);
				for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)110, byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						try {
							handWeapon.save(byteBufferWriter.bb, false);
						} catch (IOException ioException2) {
							ioException2.printStackTrace();
						}

						byteBufferWriter.putInt(int4);
						byteBufferWriter.putBoolean(boolean1);
						byteBufferWriter.putBoolean(boolean2);
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

	public static void addXp(IsoPlayer player, PerkFactory.Perks perks, int int1) {
		if (PlayerToAddressMap.containsKey(player)) {
			long long1 = (Long)PlayerToAddressMap.get(player);
			UdpConnection udpConnection = udpEngine.getActiveConnection(long1);
			if (udpConnection == null) {
				return;
			}

			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)89, byteBufferWriter);
			byteBufferWriter.putByte((byte)player.PlayerIndex);
			byteBufferWriter.putInt(perks.index());
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
					object.sprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(int1);
					if (object.sprite == null && !string.isEmpty()) {
						object.setSprite(string);
					}

					object.RemoveAttachedAnims();
					int int6 = byteBuffer.get() & 255;
					for (int int7 = 0; int7 < int6; ++int7) {
						int int8 = byteBuffer.getInt();
						IsoSprite sprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(int8);
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
				square.revisionUp();
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
				udpConnection.endPacketImmediate();
			}
		}
	}

	private static void removeCorpseFromMap(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		int int3 = byteBuffer.getInt();
		int int4 = byteBuffer.getInt();
		IsoGridSquare square = ServerMap.instance.getGridSquare(int1, int2, int3);
		if (square != null && int4 >= 0 && int4 < square.getStaticMovingObjects().size()) {
			IsoObject object = (IsoObject)square.getStaticMovingObjects().get(int4);
			square.removeCorpse((IsoDeadBody)object, true);
			square.revisionUp();
			for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo((float)int1, (float)int2)) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)68, byteBufferWriter);
					byteBuffer.rewind();
					byteBufferWriter.bb.put(byteBuffer);
					udpConnection2.endPacketImmediate();
				}
			}
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
		if (udpConnection.getConnectedGUID() != (Long)PlayerToAddressMap.get(player)) {
			try {
				player.getDescriptor().save(byteBufferWriter.bb);
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}
		}

		byteBufferWriter.putUTF(player.username);
		if (SteamUtils.isSteamModeEnabled()) {
			byteBufferWriter.putLong(player.getSteamID());
		}

		byteBufferWriter.putByte((byte)(player.godMod ? 1 : 0));
		byteBufferWriter.putByte((byte)(player.isSafety() ? 1 : 0));
		byteBufferWriter.putUTF(player.accessLevel);
		byteBufferWriter.putByte((byte)(player.invisible ? 1 : 0));
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
			try {
				ArrayList arrayList = player.getInventory().load(byteBuffer, 143, false);
				short short1 = byteBuffer.getShort();
				if (short1 >= 0 && short1 < arrayList.size()) {
					player.setClothingItem_Torso((InventoryItem)arrayList.get(short1));
				}

				short1 = byteBuffer.getShort();
				if (short1 >= 0 && short1 < arrayList.size()) {
					player.setClothingItem_Legs((InventoryItem)arrayList.get(short1));
				}

				short1 = byteBuffer.getShort();
				if (short1 >= 0 && short1 < arrayList.size()) {
					player.setClothingItem_Feet((InventoryItem)arrayList.get(short1));
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			if (PlayerToBody.containsKey(player)) {
				((IsoDeadBody)PlayerToBody.get(player)).container = player.getInventory();
				((IsoDeadBody)PlayerToBody.get(player)).ClothingItem_Torso = player.getClothingItem_Torso();
				((IsoDeadBody)PlayerToBody.get(player)).ClothingItem_Legs = player.getClothingItem_Legs();
				((IsoDeadBody)PlayerToBody.get(player)).ClothingItem_Feet = player.getClothingItem_Feet();
				player.setInventory(new ItemContainer());
				int int1 = (int)player.x;
				int int2 = (int)player.y;
				for (int int3 = 0; int3 < udpEngine.connections.size(); ++int3) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int3);
					if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo((float)int1, (float)int2)) {
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
				square.getModData().load((ByteBuffer)byteBuffer, 143);
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
			} else if (object instanceof IsoWoodenWall) {
				((IsoWoodenWall)object).WeaponHit(player, (HandWeapon)inventoryItem);
			} else if (object instanceof IsoBarricade) {
				((IsoBarricade)object).WeaponHit(player, (HandWeapon)inventoryItem);
			}

			if (object.getObjectIndex() == -1) {
				LoggerManager.getLogger("map").write(udpConnection.idStr + " \"" + udpConnection.username + "\" destroyed " + (object.getName() != null ? object.getName() : object.getObjectName()) + " with " + (string.isEmpty() ? "BareHands" : string) + " at " + int1 + "," + int2 + "," + int3);
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
			player.getStateMachine().Lock = false;
			player.setHealth(0.0F);
			player.getBodyDamage().setOverallBodyHealth(0.0F);
			player.getBodyDamage().setInfected(boolean1);
			player.getBodyDamage().setInfectionLevel(float4);
			player.getStateMachine().Lock = false;
			player.getStateMachine().changeState(DieState.instance());
			player.getStateMachine().Lock = true;
			if (!ServerOptions.instance.Open.getValue() && ServerOptions.instance.DropOffWhiteListAfterDeath.getValue() && player.accessLevel.equals("")) {
				try {
					ServerWorldDatabase.instance.removeUser(player.getUsername());
				} catch (SQLException sQLException) {
				}
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
		String string = GameWindow.ReadString(byteBuffer);
		byte byte2 = byteBuffer.get();
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
		try {
			inventoryItem.load(byteBuffer, 143, false);
		} catch (IOException ioException) {
			ioException.printStackTrace();
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
			if (string2.equals("card")) {
				PlayWorldSoundServer("ChatDrawCard", false, getAnyPlayerFromConnection(udpConnection).getCurrentSquare(), 0.0F, 3.0F, 1.0F, false);
				return udpConnection.username + " drew " + ServerOptions.getRandomCard();
			} else if (string2.equals("roll")) {
				if (int1 != 2) {
					return (String)ServerOptions.clientOptionsList.get("roll");
				} else {
					boolean boolean1 = false;
					try {
						int int2 = Integer.parseInt(stringArray[1]);
						PlayWorldSoundServer("ChatRollDice", false, getAnyPlayerFromConnection(udpConnection).getCurrentSquare(), 0.0F, 3.0F, 1.0F, false);
						return udpConnection.username + " rolls a " + int2 + "-sided dice and obtains " + Rand.Next(int2);
					} catch (Exception exception) {
						return (String)ServerOptions.clientOptionsList.get("roll");
					}
				}
			} else if (string2.equals("changepwd")) {
				if (int1 == 3) {
					String string3 = stringArray[1];
					String string4 = stringArray[2];
					try {
						return ServerWorldDatabase.instance.changePwd(udpConnection.username, string3.trim(), string4.trim());
					} catch (SQLException sQLException) {
						sQLException.printStackTrace();
						return "A SQL error occured";
					}
				} else {
					return (String)ServerOptions.clientOptionsList.get("changepwd");
				}
			} else if (string2.equals("dragons")) {
				return "Sorry, you don\'t have the required materials.";
			} else if (string2.equals("tooks")) {
				return "No pants hug !";
			} else if (string2.equals("robomat")) {
				return "This guy is not really a dentist...";
			} else if (string2.equals("sirtwiggy")) {
				return "http://www.twitch.tv/sirtwiggy BEST STREAM EVER !";
			} else if (string2.equals("eckyman")) {
				return "http://www.youtube.com/user/MrEckyman Cool cat !";
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
				LoggerManager.getLogger("chat").write((udpConnection == null ? "" : udpConnection.idStr + " \"" + udpConnection.username) + "\": \"" + string + "\"");
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
		synchronized (MainLoopNetData) {
			MainLoopNetData.add(zomboidNetData);
		}
	}

	private static void hitZombie(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		int int1 = byteBuffer.getInt();
		short short1 = byteBuffer.getShort();
		String string = GameWindow.ReadString(byteBuffer);
		float float1 = byteBuffer.getFloat();
		boolean boolean1 = byteBuffer.get() == 1;
		boolean boolean2 = byteBuffer.get() == 1;
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		float float4 = byteBuffer.getFloat();
		float float5 = byteBuffer.getFloat();
		float float6 = byteBuffer.getFloat();
		float float7 = byteBuffer.getFloat();
		float float8 = byteBuffer.getFloat();
		float float9 = byteBuffer.getFloat();
		float float10 = byteBuffer.getFloat();
		float float11 = byteBuffer.getFloat();
		float float12 = byteBuffer.getFloat();
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
		Object object = null;
		BaseVehicle baseVehicle = null;
		if (int1 == 1) {
			object = ServerMap.instance.ZombieMap.get(short1);
		} else if (int1 == 2) {
			object = (IsoGameCharacter)IDToPlayerMap.get(Integer.valueOf(short1));
		} else if (int1 == 3) {
			baseVehicle = VehicleManager.instance.getVehicleByID(short1);
		}

		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		player.useChargeDelta = float12;
		if (object == null && baseVehicle == null) {
			DebugLog.log(player.username + " hit non-existent " + (int1 == 1 ? "zombie" : (int1 == 2 ? "player" : "vehicle")) + " id=" + short1);
		} else {
			if (int1 == 1) {
				IsoZombie zombie = (IsoZombie)object;
				if (zombie.getStateMachine().getCurrent() == StaggerBackDieState.instance() && zombie.getReanimPhase() == 1) {
					zombie.setReanimateTimer((float)(Rand.Next(60) + 30));
				}

				if (zombie.getStateMachine().getCurrent() == StaggerBackDieState.instance() && zombie.getReanimPhase() == 2 && !zombie.isFakeDead()) {
					float float13 = 15.0F - ((IsoGameCharacter)object).def.Frame;
					if (float13 < 2.0F) {
						float13 = 2.0F;
					}

					zombie.setReanimPhase(1);
					zombie.PlayAnimUnlooped("ZombieDeath");
					((IsoGameCharacter)object).def.Frame = float13;
					zombie.setReanimateTimer((float)(Rand.Next(60) + 30));
				}
			}

			if (int1 == 2) {
				LoggerManager.getLogger("pvp").write("user " + player.username + " " + LoggerManager.getPlayerCoords(player) + " hit user " + ((IsoPlayer)object).username + " " + LoggerManager.getPlayerCoords((IsoPlayer)object) + " with " + string);
			}

			if (baseVehicle == null) {
				DebugLog.log(DebugType.Combat, "player " + player.username + " hit " + (int1 == 1 ? "zombie " + short1 : "player " + ((IsoPlayer)object).username) + " health=" + ((IsoGameCharacter)object).getHealth() + (boolean1 ? " for no dmg" : " for dmg " + float1));
				((IsoGameCharacter)object).setHitForce(float9);
				((IsoGameCharacter)object).getHitDir().x = float10;
				((IsoGameCharacter)object).getHitDir().y = float11;
				((IsoGameCharacter)object).setX(float3);
				((IsoGameCharacter)object).setY(float4);
				((IsoGameCharacter)object).setCloseKilled(boolean2);
				((IsoGameCharacter)object).Hit((HandWeapon)inventoryItem, player, float1, boolean1, float2);
			} else {
				baseVehicle.hitVehicle(player, (HandWeapon)inventoryItem);
			}
		}
	}

	private static void equip(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		byte byte3 = byteBuffer.get();
		InventoryItem inventoryItem = null;
		if (byte3 == 1) {
			String string = GameWindow.ReadString(byteBuffer);
			inventoryItem = InventoryItemFactory.CreateItem(string);
			if (inventoryItem == null) {
				LoggerManager.getLogger("user").write(udpConnection.idStr + " equipped unknown item type \"" + string + "\"");
				return;
			}

			byte byte4 = byteBuffer.get();
			try {
				inventoryItem.load(byteBuffer, 143, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return;
			}
		}

		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			if (byte2 == 0) {
				player.setPrimaryHandItem(inventoryItem);
			} else {
				player.setSecondaryHandItem(inventoryItem);
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
						byteBufferWriter.putByte(byte2);
						byteBufferWriter.putByte(byte3);
						byteBufferWriter.putInt(player.OnlineID);
						if (byte3 == 1) {
							try {
								inventoryItem.save(byteBufferWriter.bb, false);
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

	private static void receiveClothing(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		byte byte2 = byteBuffer.get();
		byte byte3 = byteBuffer.get();
		InventoryItem inventoryItem = null;
		if (byte3 == 1) {
			String string = GameWindow.ReadString(byteBuffer);
			byte byte4 = byteBuffer.get();
			inventoryItem = InventoryItemFactory.CreateItem(string);
			if (inventoryItem == null) {
				return;
			}

			try {
				inventoryItem.load(byteBuffer, 143, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return;
			}
		}

		IsoPlayer player = getPlayerFromConnection(udpConnection, byte1);
		if (player != null) {
			if (byte2 == Item.ClothingBodyLocation.Bottoms.ordinal()) {
				player.setClothingItem_Legs(inventoryItem);
			}

			if (byte2 == Item.ClothingBodyLocation.Shoes.ordinal()) {
				player.setClothingItem_Feet(inventoryItem);
			}

			if (byte2 == Item.ClothingBodyLocation.Top.ordinal()) {
				player.setClothingItem_Torso(inventoryItem);
			}

			for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
					IsoPlayer player2 = getAnyPlayerFromConnection(udpConnection);
					if (player2 != null) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)56, byteBufferWriter);
						byteBufferWriter.putShort((short)player.OnlineID);
						byteBufferWriter.putByte(byte2);
						byteBufferWriter.putByte(byte3);
						if (byte3 == 1) {
							try {
								inventoryItem.save(byteBufferWriter.bb, false);
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
				LoggerManager.getLogger("cmd").write(udpConnection.idStr + " \"" + player.username + "\" " + string + "." + string2 + " @ " + (int)player.getX() + "," + (int)player.getY() + "," + (int)player.getZ());
			}

			if (!SGlobalObjects.receiveClientCommand(string, string2, player, kahluaTable)) {
				LuaEventManager.triggerEvent("OnClientCommand", string, string2, player, kahluaTable);
			}
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
					byteBufferWriter.putLong(((IsoWorldInventoryObject)object).getItem().id);
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
							byteBufferWriter.putLong(inventoryItem.id);
						} else if (object instanceof IsoWorldInventoryObject) {
							byteBufferWriter.putShort((short)1);
							byteBufferWriter.putInt(((IsoObject)object).square.getX());
							byteBufferWriter.putInt(((IsoObject)object).square.getY());
							byteBufferWriter.putInt(((IsoObject)object).square.getZ());
							byteBufferWriter.putLong(((IsoWorldInventoryObject)object).getItem().id);
							byteBufferWriter.putInt(1);
							byteBufferWriter.putLong(inventoryItem.id);
						} else if (object instanceof BaseVehicle) {
							byteBufferWriter.putShort((short)3);
							byteBufferWriter.putInt(((IsoObject)object).square.getX());
							byteBufferWriter.putInt(((IsoObject)object).square.getY());
							byteBufferWriter.putInt(((IsoObject)object).square.getZ());
							byteBufferWriter.putShort(((BaseVehicle)object).VehicleID);
							byteBufferWriter.putByte((byte)itemContainer.vehiclePart.getIndex());
							byteBufferWriter.putInt(1);
							byteBufferWriter.putLong(inventoryItem.id);
						} else {
							byteBufferWriter.putShort((short)2);
							byteBufferWriter.putInt(((IsoObject)object).square.getX());
							byteBufferWriter.putInt(((IsoObject)object).square.getY());
							byteBufferWriter.putInt(((IsoObject)object).square.getZ());
							byteBufferWriter.putByte((byte)((IsoObject)object).square.getObjects().indexOf(object));
							byteBufferWriter.putByte((byte)((IsoObject)object).getContainerIndex(itemContainer));
							byteBufferWriter.putInt(1);
							byteBufferWriter.putLong(inventoryItem.id);
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
		HashSet hashSet = new HashSet();
		boolean boolean1 = false;
		int int4 = 0;
		byte byte1;
		if (short1 == 0) {
			byte1 = byteBufferReader.getByte();
			int4 = byteBuffer.getInt();
			if (square != null && byte1 >= 0 && byte1 < square.getStaticMovingObjects().size()) {
				IsoObject object = (IsoObject)square.getStaticMovingObjects().get(byte1);
				if (object != null && object.getContainer() != null) {
					for (int int5 = 0; int5 < int4; ++int5) {
						long long1 = byteBufferReader.getLong();
						InventoryItem inventoryItem = object.getContainer().getItemWithID(long1);
						if (inventoryItem == null) {
							alreadyRemoved.add(long1);
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
		} else if (short1 == 1) {
			if (square != null) {
				long long2 = byteBufferReader.getLong();
				int4 = byteBuffer.getInt();
				ItemContainer itemContainer = null;
				int int6;
				for (int6 = 0; int6 < square.getWorldObjects().size(); ++int6) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int6);
					if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && worldInventoryObject.getItem().id == long2) {
						itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
						break;
					}
				}

				if (itemContainer != null) {
					for (int6 = 0; int6 < int4; ++int6) {
						long long3 = byteBufferReader.getLong();
						InventoryItem inventoryItem2 = itemContainer.getItemWithID(long3);
						if (inventoryItem2 == null) {
							alreadyRemoved.add(long3);
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
			if (short1 == 2) {
				byte1 = byteBufferReader.getByte();
				byte2 = byteBufferReader.getByte();
				int4 = byteBuffer.getInt();
				if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
					IsoObject object2 = (IsoObject)square.getObjects().get(byte1);
					ItemContainer itemContainer2 = object2 != null ? object2.getContainerByIndex(byte2) : null;
					if (itemContainer2 != null) {
						for (int int7 = 0; int7 < int4; ++int7) {
							long long4 = byteBufferReader.getLong();
							InventoryItem inventoryItem3 = itemContainer2.getItemWithID(long4);
							if (inventoryItem3 == null) {
								alreadyRemoved.add(long4);
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
						for (int int8 = 0; int8 < int4; ++int8) {
							long long5 = byteBufferReader.getLong();
							InventoryItem inventoryItem4 = itemContainer3.getItemWithID(long5);
							if (inventoryItem4 == null) {
								alreadyRemoved.add(long5);
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

		if (boolean1) {
			square.revisionUp();
		}

		for (int int9 = 0; int9 < udpEngine.connections.size(); ++int9) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int9);
			if (udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID() && udpConnection2.ReleventTo((float)square.x, (float)square.y)) {
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
			for (int int10 = 0; int10 < alreadyRemoved.size(); ++int10) {
				byteBufferWriter2.putLong((Long)alreadyRemoved.get(int10));
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
		long long1;
		byte byte2;
		ItemContainer itemContainer;
		InventoryItem inventoryItem;
		switch (short1) {
		case 0: 
			byte2 = byteBuffer.get();
			long long2 = byteBuffer.getLong();
			if (square != null && byte2 >= 0 && byte2 < square.getStaticMovingObjects().size()) {
				IsoMovingObject movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(byte2);
				itemContainer = movingObject.getContainer();
				if (itemContainer != null) {
					inventoryItem = itemContainer.getItemWithID(long2);
					if (inventoryItem != null) {
						readItemStats(byteBuffer, inventoryItem);
					}
				}
			}

			break;
		
		case 1: 
			long long3 = byteBuffer.getLong();
			if (square != null) {
				for (int int4 = 0; int4 < square.getWorldObjects().size(); ++int4) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int4);
					if (worldInventoryObject.getItem() != null && worldInventoryObject.getItem().id == long3) {
						readItemStats(byteBuffer, worldInventoryObject.getItem());
						break;
					}

					if (worldInventoryObject.getItem() instanceof InventoryContainer) {
						itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
						inventoryItem = itemContainer.getItemWithID(long3);
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
			long1 = byteBuffer.getLong();
			if (square != null && byte2 >= 0 && byte2 < square.getObjects().size()) {
				IsoObject object = (IsoObject)square.getObjects().get(byte2);
				ItemContainer itemContainer2 = object.getContainerByIndex(byte1);
				if (itemContainer2 != null) {
					InventoryItem inventoryItem2 = itemContainer2.getItemWithID(long1);
					if (inventoryItem2 != null) {
						readItemStats(byteBuffer, inventoryItem2);
					}
				}
			}

			break;
		
		case 3: 
			short short2 = byteBuffer.getShort();
			byte1 = byteBuffer.get();
			long1 = byteBuffer.getLong();
			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
			if (baseVehicle != null) {
				VehiclePart vehiclePart = baseVehicle.getPartByIndex(byte1);
				if (vehiclePart != null) {
					ItemContainer itemContainer3 = vehiclePart.getItemContainer();
					if (itemContainer3 != null) {
						InventoryItem inventoryItem3 = itemContainer3.getItemWithID(long1);
						if (inventoryItem3 != null) {
							readItemStats(byteBuffer, inventoryItem3);
						}
					}
				}
			}

		
		}
		for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
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
		long long1 = 0L;
		short short3 = 0;
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		Object object = null;
		ItemContainer itemContainer = null;
		int int4;
		if (short2 == 2) {
			byte1 = byteBufferReader.getByte();
			byte2 = byteBufferReader.getByte();
			if (square != null && byte1 >= 0 && byte1 < square.getObjects().size()) {
				object = (IsoObject)square.getObjects().get(byte1);
				if (object != null) {
					itemContainer = ((IsoObject)object).getContainerByIndex(byte2);
					if (itemContainer == null || itemContainer.isExplored()) {
						return;
					}
				}
			}
		} else if (short2 == 3) {
			short3 = byteBufferReader.getShort();
			byte2 = byteBufferReader.getByte();
			object = VehicleManager.instance.getVehicleByID(short3);
			if (object != null) {
				VehiclePart vehiclePart = ((BaseVehicle)object).getPartByIndex(byte2);
				itemContainer = vehiclePart == null ? null : vehiclePart.getItemContainer();
				if (itemContainer == null || itemContainer.isExplored()) {
					return;
				}
			}
		} else if (short2 == 1) {
			long1 = byteBufferReader.getLong();
			for (int4 = 0; int4 < square.getWorldObjects().size(); ++int4) {
				IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int4);
				if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && worldInventoryObject.getItem().id == long1) {
					itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
					break;
				}
			}
		} else if (short2 == 0) {
			byte1 = byteBufferReader.getByte();
			if (square != null && byte1 >= 0 && byte1 < square.getStaticMovingObjects().size()) {
				object = (IsoObject)square.getStaticMovingObjects().get(byte1);
				if (object != null && ((IsoObject)object).getContainer() != null) {
					if (((IsoObject)object).getContainer().isExplored()) {
						return;
					}

					itemContainer = ((IsoObject)object).getContainer();
				}
			}
		}

		if (itemContainer != null && !itemContainer.isExplored()) {
			itemContainer.setExplored(true);
			int4 = itemContainer.Items.size();
			LuaManager.fillContainer(itemContainer, (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1)));
			if (int4 != itemContainer.Items.size()) {
				((IsoObject)object).revisionUp();
				for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
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
							byteBufferWriter.putLong(long1);
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
					object.revisionUp();
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
								byteBufferWriter.putLong(((IsoWorldInventoryObject)object).getItem().id);
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

		LoggerManager.getLogger("user").write("Error: Dupe item ID for " + player.getDisplayName() + " " + string);
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
			} else {
				int int4;
				if (short1 == 1) {
					long long1 = byteBufferReader.getLong();
					for (int4 = 0; int4 < square.getWorldObjects().size(); ++int4) {
						IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int4);
						if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof InventoryContainer && worldInventoryObject.getItem().id == long1) {
							itemContainer = ((InventoryContainer)worldInventoryObject.getItem()).getInventory();
							break;
						}
					}

					if (itemContainer == null) {
						DebugLog.log("ERROR sendItemsToContainer can\'t find world item with id=" + long1);
						return;
					}
				} else {
					byte byte3;
					if (short1 == 2) {
						byte2 = byteBufferReader.getByte();
						byte3 = byteBufferReader.getByte();
						if (byte2 < 0 || byte2 >= square.getObjects().size()) {
							DebugLog.log("ERROR sendItemsToContainer invalid object index");
							for (int4 = 0; int4 < square.getObjects().size(); ++int4) {
								if (((IsoObject)square.getObjects().get(int4)).getContainer() != null) {
									byte2 = (byte)int4;
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
			}

			if (itemContainer != null) {
				try {
					ArrayList arrayList = CompressIdenticalItems.load(byteBufferReader.bb, 143, (ArrayList)null, (ArrayList)null);
					for (int int5 = 0; int5 < arrayList.size(); ++int5) {
						InventoryItem inventoryItem = (InventoryItem)arrayList.get(int5);
						if (inventoryItem != null) {
							if (itemContainer.containsID(inventoryItem.id)) {
								System.out.println("Error: Dupe item ID for " + udpConnection.username);
								logDupeItem(udpConnection);
							} else {
								itemContainer.addItem(inventoryItem);
								itemContainer.setExplored(true);
								hashSet.add(inventoryItem.getFullType());
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

			square.revisionUp();
		}

		for (int int6 = 0; int6 < udpEngine.connections.size(); ++int6) {
			UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int6);
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
		synchronized (MainLoopNetData) {
			MainLoopNetData.add(new GameServer.DelayedConnection(udpConnection, true));
		}
	}

	public static void addDisconnect(UdpConnection udpConnection) {
		synchronized (MainLoopNetData) {
			MainLoopNetData.add(new GameServer.DelayedConnection(udpConnection, false));
		}
	}

	public static void disconnectPlayer(IsoPlayer player, UdpConnection udpConnection) {
		if (player != null) {
			ChatServer.getInstance().disconnectPlayer(player.getOnlineID());
			int int1;
			if (player.getVehicle() != null) {
				if (player.getVehicle().getDriver() != null) {
					player.getVehicle().netPlayerAuthorization = 0;
					player.getVehicle().netPlayerId = -1;
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
			DebugLog.log(DebugType.Network, "Disconnected player \"" + player.getDisplayName() + "\" " + udpConnection.idStr);
			LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + player.getDisplayName() + "\" disconnected player " + LoggerManager.getPlayerCoords(player));
		}
	}

	public static void heartBeat() {
		++count;
	}

	public static void receivePlayerInfo(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (udpConnection != null) {
			boolean boolean1 = DebugPlayer.contains(udpConnection);
			if (boolean1) {
				DebugLog.log("DBGPLR: received player update for " + udpConnection.username + " checksumState=" + udpConnection.checksumState);
			}

			if (udpConnection.checksumState != UdpConnection.ChecksumState.Done) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)83, byteBufferWriter);
				byteBufferWriter.putUTF("You have been kicked from this server.");
				udpConnection.endPacketImmediate();
				udpConnection.forceDisconnect();
			} else {
				short short1 = byteBuffer.getShort();
				byte byte1 = byteBuffer.get();
				float float1 = byteBuffer.getFloat();
				float float2 = byteBuffer.getFloat();
				float float3 = byteBuffer.getFloat();
				float float4 = byteBuffer.getFloat();
				float float5 = byteBuffer.getFloat();
				byte byte2 = byteBuffer.get();
				byte byte3 = byteBuffer.get();
				byte byte4 = byteBuffer.get();
				float float6 = byteBuffer.getFloat();
				float float7 = byteBuffer.getFloat();
				float float8 = byteBuffer.getFloat();
				float float9 = byteBuffer.getFloat();
				short short2 = byteBuffer.getShort();
				short short3 = byteBuffer.getShort();
				BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(short2);
				byte byte5 = byteBuffer.get();
				boolean boolean2 = (byte5 & 1) != 0;
				boolean boolean3 = (byte5 & 2) != 0;
				boolean boolean4 = (byte5 & 4) != 0;
				boolean boolean5 = (byte5 & 8) != 0;
				boolean boolean6 = (byte5 & 16) != 0;
				boolean boolean7 = (byte5 & 32) != 0;
				boolean boolean8 = (byte5 & 64) != 0;
				boolean boolean9 = (byte5 & 128) != 0;
				IsoPlayer player = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(short1));
				if (boolean1 && player == null) {
					DebugLog.log("DBGPLR: IDToPlayerMap is null for id=" + short1 + " user=" + udpConnection.username);
				}

				if (player != null) {
					RakVoice.SetPlayerCoordinate(udpConnection.getConnectedGUID(), float1, float2, float3, player.invisible);
					if (ServerOptions.instance.KickFastPlayers.getValue()) {
						Vector2 vector2 = (Vector2)playerToCoordsMap.get(Integer.valueOf(short1));
						if (vector2 == null) {
							vector2 = new Vector2();
							vector2.x = float1;
							vector2.y = float2;
							playerToCoordsMap.put(Integer.valueOf(short1), vector2);
						} else {
							if (!player.accessLevel.equals("") && !player.GhostMode && (Math.abs(float1 - vector2.x) > 4.0F || Math.abs(float2 - vector2.y) > 4.0F)) {
								if (playerMovedToFastMap.get(Integer.valueOf(short1)) == null) {
									playerMovedToFastMap.put(Integer.valueOf(short1), 1);
								} else {
									playerMovedToFastMap.put(Integer.valueOf(short1), (Integer)playerMovedToFastMap.get(Integer.valueOf(short1)) + 1);
								}

								LoggerManager.getLogger("admin").write(player.getDisplayName() + " go too fast (" + playerMovedToFastMap.get(Integer.valueOf(short1)) + " times)");
								if ((Integer)playerMovedToFastMap.get(Integer.valueOf(short1)) == 10) {
									LoggerManager.getLogger("admin").write(player.getDisplayName() + " kicked for going too fast");
									ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
									PacketTypes.doPacket((short)83, byteBufferWriter2);
									byteBufferWriter2.putUTF("You have been kicked from this server.");
									udpConnection.endPacketImmediate();
									udpConnection.forceDisconnect();
									return;
								}
							}

							vector2.x = float1;
							vector2.y = float2;
						}
					}

					udpConnection.ReleventPos[player.PlayerIndex].x = float1;
					udpConnection.ReleventPos[player.PlayerIndex].y = float2;
					udpConnection.ReleventPos[player.PlayerIndex].z = float3;
					if (player.getStateMachine().getCurrent() == DieState.instance()) {
						if (boolean1) {
							DebugLog.log("DBGPLR: player in DieState id=" + short1 + " user=" + udpConnection.username);
						}
					} else {
						player.setbSeenThisFrame(false);
						player.setbCouldBeSeenThisFrame(false);
						player.bSneaking = boolean5;
						player.CurrentSpeed = float7;
						IsoAnim anim = (IsoAnim)player.legsSprite.AnimStack.get(byte3);
						player.TimeSinceLastNetData = 0;
						player.setDir(byte1);
						player.angle.set(player.dir.ToVector());
						if (player.getX() == float1 && player.getY() == float2 && player.getZ() == float3) {
							player.JustMoved = false;
						} else {
							player.JustMoved = true;
						}

						if (boolean1) {
							DebugLog.log("DBGPLR: x,y,z now " + (int)float1 + "," + (int)float2 + "," + (int)float3 + " for id=" + short1 + " user=" + udpConnection.username);
						}

						player.removeFromSquare();
						player.setX(float1);
						player.setY(float2);
						player.setZ(float3);
						player.setLx(float1);
						player.setLy(float2);
						player.setLz(float3);
						player.setRemoteState(byte2);
						player.setRemoteMoveX(float4);
						player.setRemoteMoveY(float5);
						player.def.AnimFrameIncrease = float6;
						player.PlayAnim(anim.name);
						player.def.Frame = (float)byte4;
						player.def.Finished = boolean2;
						player.def.Looped = boolean3;
						if (player.legsSprite != null && player.legsSprite.CurrentAnim != null && boolean4) {
							player.legsSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
						}

						player.mpTorchDist = float8;
						player.mpTorchCone = boolean6;
						player.mpTorchStrength = float9;
						player.setAsleep(boolean8);
						player.setbClimbing(boolean9);
						player.ensureOnTile();
						IsoGameCharacter gameCharacter;
						if (player.getVehicle() == null) {
							if (baseVehicle != null) {
								if (short3 >= 0 && short3 < baseVehicle.getMaxPassengers()) {
									gameCharacter = baseVehicle.getCharacter(short3);
									if (gameCharacter == null) {
										if (bDebug) {
											DebugLog.log(player.getUsername() + " got in vehicle " + baseVehicle.VehicleID + " seat " + short3);
										}

										baseVehicle.enterRSync(short3, player, baseVehicle);
									} else if (gameCharacter != player) {
										DebugLog.log(player.getUsername() + " got in same seat as " + ((IsoPlayer)gameCharacter).getUsername());
										player.sendObjectChange("exitVehicle");
									}
								} else {
									DebugLog.log(player.getUsername() + " invalid seat vehicle " + baseVehicle.VehicleID + " seat " + short3);
								}
							}
						} else if (baseVehicle != null) {
							if (baseVehicle == player.getVehicle() && player.getVehicle().getSeat(player) != -1) {
								gameCharacter = baseVehicle.getCharacter(short3);
								if (gameCharacter == null) {
									if (baseVehicle.getSeat(player) != short3) {
										baseVehicle.switchSeatRSync(player, short3);
									}
								} else if (gameCharacter != player) {
									DebugLog.log(player.getUsername() + " switched to same seat as " + ((IsoPlayer)gameCharacter).getUsername());
									player.sendObjectChange("exitVehicle");
								}
							} else {
								DebugLog.log(player.getUsername() + " vehicle/seat remote " + baseVehicle.VehicleID + "/" + short3 + " local " + player.getVehicle().VehicleID + "/" + player.getVehicle().getSeat(player));
								player.sendObjectChange("exitVehicle");
							}
						} else {
							player.getVehicle().exitRSync(player);
							player.setVehicle((BaseVehicle)null);
						}

						for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
							UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
							if (udpConnection2 != udpConnection && udpConnection2.ReleventTo(float1, float2) && udpConnection2.isFullyConnected()) {
								boolean boolean10 = false;
								for (int int2 = 0; int2 < 4; ++int2) {
									IsoPlayer player2 = udpConnection2.players[int2];
									if (player2 != null && (!player.invisible || !player2.accessLevel.equals(""))) {
										boolean10 = true;
										break;
									}
								}

								if (boolean10) {
									long long1 = System.currentTimeMillis();
									ByteBufferWriter byteBufferWriter3 = udpConnection2.startPacket();
									PacketTypes.doPacket((short)7, byteBufferWriter3);
									byteBufferWriter3.putShort((short)player.OnlineID);
									byteBufferWriter3.putLong(long1);
									byteBufferWriter3.putByte((byte)player.dir.index());
									byteBufferWriter3.putFloat(player.getX());
									byteBufferWriter3.putFloat(player.getY());
									byteBufferWriter3.putFloat(player.getZ());
									byteBufferWriter3.putFloat(float4);
									byteBufferWriter3.putFloat(float5);
									byteBufferWriter3.putByte(player.NetRemoteState);
									if (player.sprite != null) {
										byteBufferWriter3.putByte((byte)player.sprite.AnimStack.indexOf(player.sprite.CurrentAnim));
									} else {
										byteBufferWriter3.putByte((byte)0);
									}

									byteBufferWriter3.putByte((byte)((int)player.def.Frame));
									byteBufferWriter3.putFloat(player.def.AnimFrameIncrease);
									byteBufferWriter3.putFloat(float8);
									byteBufferWriter3.putFloat(float9);
									if (baseVehicle == null) {
										byteBufferWriter3.putShort((short)-1);
										byteBufferWriter3.putShort((short)-1);
									} else {
										byteBufferWriter3.putShort(baseVehicle.VehicleID);
										byteBufferWriter3.putShort((short)baseVehicle.getSeat(player));
									}

									byte5 = 0;
									if (player.def.Finished) {
										byte5 = (byte)(byte5 | 1);
									}

									if (player.def.Looped) {
										byte5 = (byte)(byte5 | 2);
									}

									if (player.legsSprite != null && player.legsSprite.CurrentAnim != null && player.legsSprite.CurrentAnim.FinishUnloopedOnFrame == 0) {
										byte5 = (byte)(byte5 | 4);
									}

									if (player.bSneaking) {
										byte5 = (byte)(byte5 | 8);
									}

									if (boolean6) {
										byte5 = (byte)(byte5 | 16);
									}

									if (boolean7) {
										byte5 = (byte)(byte5 | 32);
									}

									if (boolean8) {
										byte5 = (byte)(byte5 | 64);
									}

									if (boolean9) {
										byte5 = (byte)(byte5 | 128);
									}

									byteBufferWriter3.putByte(byte5);
									udpConnection2.endPacketSuperHighUnreliable();
								}
							}
						}
					}
				}
			}
		}
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
		for (int int3 = 1; int3 < SpawnRegions.size() + 1; ++int3) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)171, byteBufferWriter);
			byteBufferWriter.putInt(int3);
			try {
				((KahluaTable)SpawnRegions.rawget(int3)).save(byteBufferWriter.bb);
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
			ChooseGameInfo chooseGameInfo = new ChooseGameInfo();
			if (string2 != null) {
				try {
					mod = chooseGameInfo.readModInfo(string2);
				} catch (Exception exception2) {
					Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, exception2);
					mod = chooseGameInfo.new Mod(string);
					mod.setId(string);
				}
			} else {
				mod = chooseGameInfo.new Mod(string);
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
		SharedDescriptors.Descriptor[] descriptorArray;
		int int1;
		int int2;
		if ("descriptors.bin".equals(string)) {
			byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)37, byteBufferWriter);
			byteBufferWriter.putUTF(string);
			descriptorArray = SharedDescriptors.getSharedDescriptors();
			try {
				byteBufferWriter.putShort((short)descriptorArray.length);
				SharedDescriptors.Descriptor[] descriptorArray2 = descriptorArray;
				int1 = descriptorArray.length;
				for (int2 = 0; int2 < int1; ++int2) {
					SharedDescriptors.Descriptor descriptor = descriptorArray2[int2];
					descriptor.desc.saveCompact(byteBufferWriter.bb);
					byteBufferWriter.bb.put((byte)descriptor.palette);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			udpConnection.endPacketImmediate();
		}

		if ("playerzombiedesc".equals(string)) {
			byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)37, byteBufferWriter);
			byteBufferWriter.putUTF(string);
			descriptorArray = SharedDescriptors.getPlayerZombieDescriptors();
			int int3 = 0;
			for (int1 = 0; int1 < descriptorArray.length; ++int1) {
				if (descriptorArray[int1] != null) {
					++int3;
				}
			}

			try {
				byteBufferWriter.putShort((short)int3);
				SharedDescriptors.Descriptor[] descriptorArray3 = descriptorArray;
				int2 = descriptorArray.length;
				for (int int4 = 0; int4 < int2; ++int4) {
					SharedDescriptors.Descriptor descriptor2 = descriptorArray3[int4];
					if (descriptor2 != null) {
						byteBufferWriter.putShort((short)descriptor2.desc.getID());
						descriptor2.desc.saveCompact(byteBufferWriter.bb);
						byteBufferWriter.bb.put((byte)descriptor2.palette);
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
				IsoWorld.instance.MetaGrid.savePart(large_file_bb, 0);
				IsoWorld.instance.MetaGrid.savePart(large_file_bb, 1);
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
			boolean boolean1 = false;
			if (SpawnBuildings != null) {
				boolean1 = SpawnBuildings.contains(roomDef.getBuilding());
			}

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
				survivorDesc.load(byteBuffer, 143, (IsoGameCharacter)null);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			IsoPlayer player = new IsoPlayer((IsoCell)null, survivorDesc, (int)float1, (int)float2, (int)float3);
			player.PlayerIndex = byte1;
			player.OnlineChunkGridWidth = byte2;
			Players.add(player);
			player.bRemote = true;
			int int1 = udpConnection.playerIDs[byte1];
			IDToPlayerMap.put(int1, player);
			udpConnection.players[byte1] = player;
			PlayerToAddressMap.put(player, udpConnection.getConnectedGUID());
			player.OnlineID = int1;
			try {
				player.getXp().load(byteBuffer, 143);
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
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

			player.username = string;
			player.accessLevel = udpConnection.accessLevel;
			if (!player.accessLevel.equals("") && CoopSlave.instance == null) {
				player.GhostMode = true;
				player.invisible = true;
				player.godMod = true;
			}

			ChatServer.getInstance().initPlayer(player.OnlineID);
			udpConnection.setFullyConnected();
			sendWeather(udpConnection);
			for (int int2 = 0; int2 < udpEngine.connections.size(); ++int2) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int2);
				sendPlayerConnect(player, udpConnection2);
			}

			udpConnection.loadedCells[byte1].setLoaded();
			udpConnection.loadedCells[byte1].sendPacket(udpConnection);
			preventIndoorZombies((int)float1, (int)float2, (int)float3);
			ServerLOS.instance.addPlayer(player);
			LoggerManager.getLogger("user").write(udpConnection.idStr + " \"" + player.username + "\" fully connected " + LoggerManager.getPlayerCoords(player));
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
				coopAccessDenied("Coop player " + (byte2 + 1) + "/" + 4 + " already exists", byte2, udpConnection);
			} else {
				String string;
				if (byte1 != 1) {
					if (byte1 == 2) {
						string = udpConnection.usernames[byte2];
						if (string == null) {
							coopAccessDenied("Coop player login wasn\'t received", byte2, udpConnection);
						} else {
							DebugLog.log("coop player=" + (byte2 + 1) + "/" + 4 + " username=\"" + string + "\" player info received");
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

						DebugLog.log("coop player=" + (byte2 + 1) + "/" + 4 + " username=\"" + string + "\" is joining");
						float float1;
						if (udpConnection.players[byte2] != null) {
							DebugLog.log("coop player=" + (byte2 + 1) + "/" + 4 + " username=\"" + string + "\" is replacing dead player");
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
							DebugLog.log("coop player=" + (byte2 + 1) + "/" + 4 + " username=\"" + string + "\" assigned id=" + int3);
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
					object.getModData().load((ByteBuffer)byteBuffer, 143);
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
		byteBufferWriter.putByte((byte)0);
		byteBufferWriter.putByte((byte)(player.getPrimaryHandItem() != null ? 1 : 0));
		byteBufferWriter.putInt(player.OnlineID);
		if (player.getPrimaryHandItem() != null) {
			try {
				player.getPrimaryHandItem().save(byteBufferWriter.bb, false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		udpConnection.endPacketImmediate();
		byteBufferWriter = udpConnection.startPacket();
		PacketTypes.doPacket((short)25, byteBufferWriter);
		byteBufferWriter.putByte((byte)1);
		byteBufferWriter.putByte((byte)(player.getSecondaryHandItem() != null ? 1 : 0));
		byteBufferWriter.putInt(player.OnlineID);
		if (player.getSecondaryHandItem() != null) {
			try {
				player.getSecondaryHandItem().save(byteBufferWriter.bb, false);
			} catch (IOException ioException2) {
				ioException2.printStackTrace();
			}
		}

		udpConnection.endPacketImmediate();
	}

	public static void sendZombie(IsoZombie zombie) {
		if (!bFastForward) {
			ZombieUpdatePacker.instance.addZombieToPacker(zombie);
		}
	}

	public static void createZombie() {
	}

	public static void requestingChunk(ByteBufferReader byteBufferReader, UdpConnection udpConnection) {
		int int1 = byteBufferReader.getInt();
		int int2 = byteBufferReader.getInt();
		toCreateOnClient.clear();
		IsoChunk chunk = ServerMap.instance.getChunk(int1, int2);
		if (chunk != null) {
			for (int int3 = 0; int3 < 10; ++int3) {
				for (int int4 = 0; int4 < 10; ++int4) {
					for (int int5 = 0; int5 < 6; ++int5) {
						IsoGridSquare square = chunk.getGridSquare(int3, int4, int5);
						if (square != null && !square.getMovingObjects().isEmpty()) {
							for (int int6 = 0; int6 < square.getMovingObjects().size(); ++int6) {
								IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int6);
								if (movingObject instanceof IsoZombie) {
								}
							}
						}
					}
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
		if (short1 == AlarmClock.PacketPlayer) {
			short short2 = byteBuffer.getShort();
			long long1 = byteBuffer.getLong();
			boolean boolean1 = byteBuffer.get() == 1;
			int int2 = 0;
			int int3 = 0;
			boolean boolean2 = false;
			if (!boolean1) {
				int2 = byteBuffer.getInt();
				int3 = byteBuffer.getInt();
				boolean2 = byteBuffer.get() == 1;
			}

			for (int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
				UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int1);
				if (udpConnection2 != udpConnection) {
					sendAlarmClock_Player(short2, long1, boolean1, int2, int3, boolean2, udpConnection2);
				}
			}
		} else if (short1 == AlarmClock.PacketWorld) {
			int int4 = byteBuffer.getInt();
			int int5 = byteBuffer.getInt();
			int int6 = byteBuffer.getInt();
			long long2 = byteBuffer.getLong();
			boolean boolean3 = byteBuffer.get() == 1;
			int int7 = 0;
			int1 = 0;
			boolean boolean4 = false;
			if (!boolean3) {
				int7 = byteBuffer.getInt();
				int1 = byteBuffer.getInt();
				boolean4 = byteBuffer.get() == 1;
			}

			IsoGridSquare square = ServerMap.instance.getGridSquare(int4, int5, int6);
			if (square == null) {
				DebugLog.log("SyncAlarmClock: sq is null x,y,z=" + int4 + "," + int5 + "," + int6);
			} else {
				AlarmClock alarmClock = null;
				int int8;
				for (int8 = 0; int8 < square.getWorldObjects().size(); ++int8) {
					IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)square.getWorldObjects().get(int8);
					if (worldInventoryObject != null && worldInventoryObject.getItem() instanceof AlarmClock && worldInventoryObject.getItem().id == long2) {
						alarmClock = (AlarmClock)worldInventoryObject.getItem();
						break;
					}
				}

				if (alarmClock == null) {
					DebugLog.log("SyncAlarmClock: AlarmClock is null x,y,z=" + int4 + "," + int5 + "," + int6);
				} else {
					if (boolean3) {
						alarmClock.stopRinging();
					} else {
						alarmClock.setHour(int7);
						alarmClock.setMinute(int1);
						alarmClock.setAlarmSet(boolean4);
					}

					for (int8 = 0; int8 < udpEngine.connections.size(); ++int8) {
						UdpConnection udpConnection3 = (UdpConnection)udpEngine.connections.get(int8);
						if (udpConnection3 != udpConnection) {
							sendAlarmClock_World(int4, int5, int6, long2, boolean3, int7, int1, boolean4, udpConnection3);
						}
					}
				}
			}
		}
	}

	public static void SyncIsoObject(ByteBuffer byteBuffer, UdpConnection udpConnection) {
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
				IsoRegion.setPreviousFlags(square);
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
				IsoRegion.squareChanged(square, true);
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

			square.revisionUp();
		}
	}

	public static int RemoveItemFromMap(IsoObject object) {
		int int1 = object.getSquare().getX();
		int int2 = object.getSquare().getY();
		int int3 = object.getSquare().getZ();
		int int4 = object.getSquare().getObjects().indexOf(object);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null && !(object instanceof IsoWorldInventoryObject)) {
			IsoRegion.setPreviousFlags(square);
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
				IsoRegion.squareChanged(square, true);
			}

			square.revisionUp();
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
				if (object instanceof IsoWorldInventoryObject) {
					LoggerManager.getLogger("item").write(udpConnection.idStr + " \"" + udpConnection.username + "\" floor +1 " + (int)object.getX() + "," + (int)object.getY() + "," + (int)object.getZ() + " [" + ((IsoWorldInventoryObject)object).getItem().getFullType() + "]");
				} else {
					String string = object.getName() != null ? object.getName() : object.getObjectName();
					if (object.getSprite() != null && object.getSprite().getName() != null) {
						string = string + " (" + object.getSprite().getName() + ")";
					}

					LoggerManager.getLogger("map").write(udpConnection.idStr + " \"" + udpConnection.username + "\" added " + string + " at " + object.getX() + "," + object.getY() + "," + object.getZ());
				}

				object.addToWorld();
				object.square.RecalcProperties();
				if (!(object instanceof IsoWorldInventoryObject)) {
					object.square.restackSheetRope();
					IsoWorld.instance.CurrentCell.checkHaveRoof(object.square.getX(), object.square.getY());
					MapCollisionData.instance.squareChanged(object.square);
					PolygonalMap2.instance.squareChanged(object.square);
					ServerMap.instance.physicsCheck(object.square.x, object.square.y);
					IsoRegion.squareChanged(object.square);
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
		synchronized (MainLoopNetData) {
			MainLoopNetData.add(zomboidNetData);
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

	public static void SendDeath(IsoPlayer player) {
		player.getBodyDamage().setOverallBodyHealth(-1.0F);
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)33, byteBufferWriter);
			byteBufferWriter.putInt(player.OnlineID);
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

	public static void sendDeadZombie(IsoZombie zombie) {
		for (int int1 = 0; int1 < udpEngine.connections.size(); ++int1) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int1);
			if (udpConnection.ReleventTo(zombie.x, zombie.y)) {
				ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
				PacketTypes.doPacket((short)39, byteBufferWriter);
				byteBufferWriter.putShort(zombie.OnlineID);
				byteBufferWriter.putFloat(zombie.getX());
				byteBufferWriter.putFloat(zombie.getY());
				byteBufferWriter.putFloat(zombie.getZ());
				if (zombie.getInventory() != null) {
					byteBufferWriter.putByte((byte)1);
					try {
						ArrayList arrayList = zombie.getInventory().save(byteBufferWriter.bb, false);
						byteBufferWriter.bb.putShort((short)arrayList.indexOf(zombie.getClothingItem_Torso()));
						byteBufferWriter.bb.putShort((short)arrayList.indexOf(zombie.getClothingItem_Legs()));
						byteBufferWriter.bb.putShort((short)arrayList.indexOf(zombie.getClothingItem_Feet()));
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
						DebugLog.log("ERROR: sendServerCommand: can\'t save key,value=" + kahluaTableIterator.getKey() + "," + kahluaTableIterator.getValue());
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
			if (player.isSafety()) {
				LoggerManager.getLogger("pvp").write("user " + player.username + " " + LoggerManager.getPlayerCoords(player) + " enabled safety");
			} else {
				LoggerManager.getLogger("pvp").write("user " + player.username + " " + LoggerManager.getPlayerCoords(player) + " disabled safety");
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
			object.revisionUp();
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

	private static void copyFile(File file, File file2) throws IOException {
		if (!file2.exists()) {
			file2.createNewFile();
		}

		FileChannel fileChannel = null;
		FileChannel fileChannel2 = null;
		try {
			fileChannel = (new FileInputStream(file)).getChannel();
			fileChannel2 = (new FileOutputStream(file2)).getChannel();
			fileChannel2.transferFrom(fileChannel, 0L, fileChannel.size());
		} finally {
			if (fileChannel != null) {
				fileChannel.close();
			}

			if (fileChannel2 != null) {
				fileChannel2.close();
			}
		}
	}

	public static void sendReanimatedZombieID(IsoPlayer player, IsoZombie zombie) {
		if (PlayerToAddressMap.containsKey(player)) {
			sendObjectChange(player, "reanimatedID", new Object[]{"ID", (double)zombie.OnlineID});
		}
	}

	public static void RemoveSpecialObjectFromSquare(IsoObject object) {
		int int1 = object.getSquare().getX();
		int int2 = object.getSquare().getY();
		int int3 = object.getSquare().getZ();
		int int4 = object.getSquare().getSpecialObjects().indexOf(object);
		IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		if (square != null) {
			if (square.getCell().getProcessIsoObjects().contains(object)) {
				square.getCell().getProcessIsoObjectRemove().add(object);
			}

			object.removeFromWorld();
			object.removeFromSquare();
			for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
				UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int5);
				if (udpConnection.ReleventTo((float)object.square.x, (float)object.square.y)) {
					ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
					PacketTypes.doPacket((short)111, byteBufferWriter);
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putInt(int3);
					byteBufferWriter.putInt(int4);
					udpConnection.endPacketImmediate();
				}
			}
		}
	}

	private static void removeSpecialObject(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (IsoWorld.instance.CurrentCell != null) {
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			int int3 = byteBuffer.getInt();
			int int4 = byteBuffer.getInt();
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
			if (square != null && int4 >= 0 && int4 < square.getSpecialObjects().size()) {
				IsoObject object = (IsoObject)square.getSpecialObjects().get(int4);
				object.removeFromWorld();
				object.removeFromSquare();
				if (square.getCell().getProcessIsoObjects().contains(object)) {
					square.getCell().getProcessIsoObjectRemove().add(object);
				}

				for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
					UdpConnection udpConnection2 = (UdpConnection)udpEngine.connections.get(int5);
					if (udpConnection2.ReleventTo((float)object.square.x, (float)object.square.y) && udpConnection2.getConnectedGUID() != udpConnection.getConnectedGUID()) {
						ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
						PacketTypes.doPacket((short)111, byteBufferWriter);
						byteBufferWriter.putInt(int1);
						byteBufferWriter.putInt(int2);
						byteBufferWriter.putInt(int3);
						byteBufferWriter.putInt(int4);
						udpConnection2.endPacketImmediate();
					}
				}
			} else if (bDebug) {
				DebugLog.log("RemoveSpecialObject: sq is null or index is invalid");
			}
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
			for (int int6 = 0; int6 < int5; ++int6) {
				String string2 = GameWindow.ReadString(byteBuffer);
				safeHouse.addPlayer(string2);
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

	public static void sendIsoWaveSignal(int int1, int int2, int int3, ChatMessage chatMessage, String string, float float1, float float2, float float3, int int4, boolean boolean1) {
		for (int int5 = 0; int5 < udpEngine.connections.size(); ++int5) {
			UdpConnection udpConnection = (UdpConnection)udpEngine.connections.get(int5);
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypesShort.doPacket((short)1000, byteBufferWriter);
			byteBufferWriter.putInt(int1);
			byteBufferWriter.putInt(int2);
			byteBufferWriter.putInt(int3);
			byteBufferWriter.putBoolean(chatMessage != null);
			if (chatMessage != null) {
				chatMessage.pack(byteBufferWriter);
			}

			byteBufferWriter.putByte((byte)(string != null ? 1 : 0));
			if (string != null) {
				byteBufferWriter.putUTF(string);
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
		ChatMessage chatMessage = null;
		if (boolean1) {
			chatMessage = ChatServer.getInstance().unpackChatMessage(byteBuffer);
		}

		String string = null;
		if (byteBuffer.get() == 1) {
			string = GameWindow.ReadString(byteBuffer);
		}

		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		float float3 = byteBuffer.getFloat();
		int int4 = byteBuffer.getInt();
		boolean boolean2 = byteBuffer.get() == 1;
		ZomboidRadio.getInstance().ReceiveTransmission(int1, int2, int3, chatMessage, string, float1, float2, float3, int4, boolean2);
	}

	public static void receivePlayerListensChannel(ByteBuffer byteBuffer) {
		int int1 = byteBuffer.getInt();
		boolean boolean1 = byteBuffer.get() == 1;
		boolean boolean2 = byteBuffer.get() == 1;
		ZomboidRadio.getInstance().PlayerListensChannel(int1, boolean1, boolean2);
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

	private static void parseSpawnRegions() {
		if (SpawnRegions != null) {
			SpawnPoints = new ArrayList();
			if (!ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
				String[] stringArray = ServerOptions.instance.SpawnPoint.getValue().split(",");
				if (stringArray.length == 3) {
					try {
						int int1 = Integer.parseInt(stringArray[0].trim());
						int int2 = Integer.parseInt(stringArray[1].trim());
						int int3 = Integer.parseInt(stringArray[2].trim());
						if (int1 != 0 || int2 != 0) {
							SpawnPoints.add(new IsoGameCharacter.Location(int1, int2, int3));
							return;
						}
					} catch (NumberFormatException numberFormatException) {
						DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
					}
				} else {
					DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
				}
			}

			KahluaTableIterator kahluaTableIterator = SpawnRegions.iterator();
			label73: while (true) {
				Object object;
				do {
					Object object2;
					do {
						if (!kahluaTableIterator.advance()) {
							return;
						}

						object2 = kahluaTableIterator.getValue();
					}			 while (!(object2 instanceof KahluaTable));

					object = ((KahluaTable)object2).rawget("points");
				}		 while (!(object instanceof KahluaTable));

				KahluaTableIterator kahluaTableIterator2 = ((KahluaTable)object).iterator();
				while (true) {
					do {
						if (!kahluaTableIterator2.advance()) {
							continue label73;
						}
					}			 while (!(kahluaTableIterator2.getValue() instanceof KahluaTable));

					KahluaTableIterator kahluaTableIterator3 = ((KahluaTable)kahluaTableIterator2.getValue()).iterator();
					while (kahluaTableIterator3.advance()) {
						Object object3 = kahluaTableIterator3.getValue();
						if (object3 instanceof KahluaTable) {
							Object object4 = ((KahluaTable)object3).rawget("worldX");
							Object object5 = ((KahluaTable)object3).rawget("worldY");
							Object object6 = ((KahluaTable)object3).rawget("posX");
							Object object7 = ((KahluaTable)object3).rawget("posY");
							Object object8 = ((KahluaTable)object3).rawget("posZ");
							if (object4 instanceof Double && object5 instanceof Double && object6 instanceof Double && object7 instanceof Double) {
								int int4 = ((Double)object4).intValue() * 300 + ((Double)object6).intValue();
								int int5 = ((Double)object5).intValue() * 300 + ((Double)object7).intValue();
								int int6 = object8 == null ? 0 : ((Double)object8).intValue();
								IsoGameCharacter.Location location = new IsoGameCharacter.Location(int4, int5, int6);
								if (!SpawnPoints.contains(location)) {
									SpawnPoints.add(location);
								}
							}
						}
					}
				}
			}
		}
	}

	private static void initSpawnBuildings() {
		if (SpawnPoints != null && !SpawnPoints.isEmpty()) {
			SpawnBuildings = new ArrayList();
			for (int int1 = 0; int1 < SpawnPoints.size(); ++int1) {
				IsoGameCharacter.Location location = (IsoGameCharacter.Location)SpawnPoints.get(int1);
				RoomDef roomDef = IsoWorld.instance.MetaGrid.getRoomAt(location.x, location.y, location.z);
				if (roomDef != null && roomDef.getBuilding() != null) {
					SpawnBuildings.add(roomDef.getBuilding());
				}
			}
		}
	}

	public static boolean isSpawnBuilding(BuildingDef buildingDef) {
		return SpawnBuildings != null && SpawnBuildings.contains(buildingDef);
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
				kahluaTable.load((ByteBuffer)byteBuffer, 143);
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
			CompressIdenticalItems.load(byteBuffer, 143, arrayList, (ArrayList)null);
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
		File file = new File(GameWindow.getSaveDir() + File.separator + "Multiplayer" + File.separator + ServerName + File.separator + "map_t.bin");
		if (file.exists()) {
			DebugLog.log("checking server WorldVersion in map_t.bin");
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				Throwable throwable = null;
				try {
					DataInputStream dataInputStream = new DataInputStream(fileInputStream);
					Throwable throwable2 = null;
					try {
						byte byte1 = dataInputStream.readByte();
						byte byte2 = dataInputStream.readByte();
						byte byte3 = dataInputStream.readByte();
						byte byte4 = dataInputStream.readByte();
						String string;
						if (byte1 == 71 && byte2 == 77 && byte3 == 84 && byte4 == 77) {
							string = dataInputStream.readInt();
							String string2;
							if (string > 143) {
								string2 = "The server savefile appears to be from a newer version of the game and cannot be loaded.";
								return string2;
							}

							if (!(string <= 115)) {
								return null;
							}

							string2 = "The server savefile appears to be from a pre-vehicles version of the game and cannot be loaded.\nDue to the extent of changes required to implement vehicles, saves from earlier versions are not compatible.\nTo continue this game, use the 38.30(pre-vehicles) branch.";
							return string2;
						}

						string = "The server savefile appears to be from an old version of the game and cannot be loaded.";
						return string;
					} catch (Throwable throwable3) {
						throwable2 = throwable3;
						throw throwable3;
					} finally {
						if (dataInputStream != null) {
							if (throwable2 != null) {
								try {
									dataInputStream.close();
								} catch (Throwable throwable4) {
									throwable2.addSuppressed(throwable4);
								}
							} else {
								dataInputStream.close();
							}
						}
					}
				} catch (Throwable throwable5) {
					throwable = throwable5;
					throw throwable5;
				} finally {
					if (fileInputStream != null) {
						if (throwable != null) {
							try {
								fileInputStream.close();
							} catch (Throwable throwable6) {
								throwable.addSuppressed(throwable6);
							}
						} else {
							fileInputStream.close();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			DebugLog.log("map_t.bin does not exist, cannot determine the server\'s WorldVersion.");
		}

		return null;
	}

	static  {
		discordBot = new DiscordBot(ServerName, new DiscordSender(){
			
			public void sendMessageFromDiscord(String var1, String var2) {
				ChatServer.getInstance().sendMessageFromDiscordToGeneralChat(var1, var2);
			}
		});
		launched = false;
		consoleCommands = new ArrayList();
		MainLoopNetData = new ArrayList();
		MainLoopNetData2 = new ArrayList();
		ccFilters = new HashMap();
		alreadyRemoved = new ArrayList();
		CellLoaderX = 0;
		CellLoaderY = 0;
		playerToCoordsMap = new HashMap();
		playerMovedToFastMap = new HashMap();
		large_file_bb = ByteBuffer.allocate(3145728);
		previousSave = Calendar.getInstance().getTimeInMillis();
		toCreateOnClient = new ArrayList();
		ip = "127.0.0.1";
		incomingNetData = new ArrayList();
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

		private CCFilter() {
		}

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

		CCFilter(Object object) {
			this();
		}
	}
}
