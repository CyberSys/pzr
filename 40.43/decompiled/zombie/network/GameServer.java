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
      String var0 = "[SERVERMSG] Server saving...Please wait";

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         ByteBufferWriter var3 = var2.startPacket();
         PacketTypes.doPacket((short)158, var3);
         var3.putUTF(var0);
         var2.endPacketImmediate();
      }

   }

   public static void UnPauseAllClients() {
      String var0 = "[SERVERMSG] Server saved game...enjoy :)";

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         ByteBufferWriter var3 = var2.startPacket();
         PacketTypes.doPacket((short)159, var3);
         var3.putUTF(var0);
         var2.endPacketImmediate();
      }

   }

   private static void testTCPListen(int var0) {
      try {
         ServerSocket var1 = new ServerSocket();
         var1.setSoTimeout(8000);
         var1.setReuseAddress(true);
         var1.bind(new InetSocketAddress(var0));
         Socket var2 = var1.accept();
         var2.close();
         var1.close();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   private static void prevent32BitServer() {
      String var0 = System.getProperty("sun.arch.data.model");
      String var1 = System.getProperty("zomboid.force32");
      boolean var2 = var1 != null && var1.equals("1");
      if (!var0.equals("64") && !var2) {
         System.err.println("32-bit server is not supported, please restart in 64-bit mode");
         System.exit(-1);
      }

   }

   private static String parseIPFromCommandline(String[] var0, int var1, String var2) {
      if (var1 == var0.length - 1) {
         DebugLog.log("expected argument after \"" + var2 + "\"");
         System.exit(0);
      } else if (var0[var1 + 1].trim().isEmpty()) {
         DebugLog.log("empty argument given to \"\" + option + \"\"");
         System.exit(0);
      } else {
         String[] var3 = var0[var1 + 1].trim().split("\\.");
         if (var3.length == 4) {
            for(int var4 = 0; var4 < 4; ++var4) {
               try {
                  int var5 = Integer.parseInt(var3[var4]);
                  if (var5 < 0 || var5 > 255) {
                     DebugLog.log("expected IP address after \"" + var2 + "\", got \"" + var0[var1 + 1] + "\"");
                     System.exit(0);
                  }
               } catch (NumberFormatException var6) {
                  DebugLog.log("expected IP address after \"" + var2 + "\", got \"" + var0[var1 + 1] + "\"");
                  System.exit(0);
               }
            }
         } else {
            DebugLog.log("expected IP address after \"" + var2 + "\", got \"" + var0[var1 + 1] + "\"");
            System.exit(0);
         }
      }

      return var0[var1 + 1];
   }

   private static int parsePortFromCommandline(String[] var0, int var1, String var2) {
      if (var1 == var0.length - 1) {
         DebugLog.log("expected argument after \"" + var2 + "\"");
         System.exit(0);
      } else if (var0[var1 + 1].trim().isEmpty()) {
         DebugLog.log("empty argument given to \"" + var2 + "\"");
         System.exit(0);
      } else {
         try {
            return Integer.parseInt(var0[var1 + 1].trim());
         } catch (NumberFormatException var4) {
            DebugLog.log("expected an integer after \"" + var2 + "\"");
            System.exit(0);
         }
      }

      return -1;
   }

   private static boolean parseBooleanFromCommandline(String[] var0, int var1, String var2) {
      if (var1 == var0.length - 1) {
         DebugLog.log("expected argument after \"" + var2 + "\"");
         System.exit(0);
      } else if (var0[var1 + 1].trim().isEmpty()) {
         DebugLog.log("empty argument given to \"" + var2 + "\"");
         System.exit(0);
      } else {
         String var3 = var0[var1 + 1].trim();
         if ("true".equalsIgnoreCase(var3)) {
            return true;
         }

         if ("false".equalsIgnoreCase(var3)) {
            return false;
         }

         DebugLog.log("expected true or false after \"" + var2 + "\"");
         System.exit(0);
      }

      return false;
   }

   public static void setupCoop() throws FileNotFoundException {
      CoopSlave.init();
   }

   public static void main(String[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         if (var0[var1] != null && var0[var1].startsWith("-cachedir=")) {
            GameWindow.setCacheDir(var0[var1].replace("-cachedir=", "").trim());
         }
      }

      boolean var63 = false;

      int var2;
      for(var2 = 0; var2 < var0.length; ++var2) {
         if (var0[var2].equals("-coop")) {
            try {
               var63 = true;
               setupCoop();
               CoopSlave.status(Translator.getText("UI_ServerStatus_Initialising"));
               break;
            } catch (FileNotFoundException var59) {
               var59.printStackTrace();
               SteamUtils.shutdown();
               System.exit(37);
               return;
            }
         }
      }

      String var64;
      if (!var63) {
         var64 = GameWindow.getCacheDir() + File.separator + "server-console.txt";

         try {
            FileOutputStream var3 = new FileOutputStream(var64);
            PrintStream var4 = new PrintStream(var3, true);
            System.setOut(new ProxyPrintStream(System.out, var4));
            System.setErr(new ProxyPrintStream(System.err, var4));
         } catch (FileNotFoundException var58) {
            var58.printStackTrace();
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

      int var5;
      String var6;
      for(var2 = 0; var2 < var0.length; ++var2) {
         if (var0[var2] != null) {
            if (var0[var2].startsWith("-debuglog=")) {
               String[] var65 = var0[var2].replace("-debuglog=", "").split(",");
               int var67 = var65.length;

               for(var5 = 0; var5 < var67; ++var5) {
                  var6 = var65[var5];

                  try {
                     DebugLog.enableLog(DebugType.valueOf(var6), true);
                  } catch (IllegalArgumentException var57) {
                  }
               }
            } else if (var0[var2].equals("-adminusername")) {
               if (var2 == var0.length - 1) {
                  DebugLog.log("expected argument after \"-adminusername\"");
                  System.exit(0);
               } else if (!ServerWorldDatabase.isValidUserName(var0[var2 + 1].trim())) {
                  DebugLog.log("invalid username given to \"-adminusername\"");
                  System.exit(0);
               } else {
                  ServerWorldDatabase.instance.CommandLineAdminUsername = var0[var2 + 1].trim();
                  ++var2;
               }
            } else if (var0[var2].equals("-adminpassword")) {
               if (var2 == var0.length - 1) {
                  DebugLog.log("expected argument after \"-adminpassword\"");
                  System.exit(0);
               } else if (var0[var2 + 1].trim().isEmpty()) {
                  DebugLog.log("empty argument given to \"-adminpassword\"");
                  System.exit(0);
               } else {
                  ServerWorldDatabase.instance.CommandLineAdminPassword = var0[var2 + 1].trim();
                  ++var2;
               }
            } else if (var0[var2].startsWith("-cachedir=")) {
               GameWindow.setCacheDir(var0[var2].replace("-cachedir=", "").trim());
            } else if (var0[var2].equals("-ip")) {
               IPCommandline = parseIPFromCommandline(var0, var2, "-ip");
               ++var2;
            } else if (var0[var2].equals("-gui")) {
               GUICommandline = true;
            } else if (var0[var2].equals("-nosteam")) {
               System.setProperty("zomboid.steam", "0");
            } else if (var0[var2].equals("-port")) {
               PortCommandline = parsePortFromCommandline(var0, var2, "-port");
               ++var2;
            } else if (var0[var2].equals("-steamport1")) {
               SteamPortCommandline1 = parsePortFromCommandline(var0, var2, "-steamport1");
               ++var2;
            } else if (var0[var2].equals("-steamport2")) {
               SteamPortCommandline2 = parsePortFromCommandline(var0, var2, "-steamport2");
               ++var2;
            } else if (var0[var2].equals("-steamvac")) {
               SteamVACCommandline = parseBooleanFromCommandline(var0, var2, "-steamvac");
               ++var2;
            } else if (var0[var2].equals("-servername")) {
               if (var2 == var0.length - 1) {
                  DebugLog.log("expected argument after \"-servername\"");
                  System.exit(0);
               } else if (var0[var2 + 1].trim().isEmpty()) {
                  DebugLog.log("empty argument given to \"-servername\"");
                  System.exit(0);
               } else {
                  ServerName = var0[var2 + 1].trim();
                  ++var2;
               }
            } else if (var0[var2].equals("-coop")) {
               ServerWorldDatabase.instance.doAdmin = false;
            } else {
               DebugLog.log("unknown option \"" + var0[var2] + "\"");
            }
         }
      }

      DebugLog.log("server name is \"" + ServerName + "\"");
      var64 = isWorldVersionUnsupported();
      if (var64 != null) {
         DebugLog.log(var64);
         CoopSlave.status(var64);
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

         String var66;
         if (SteamUtils.isSteamModeEnabled()) {
            var66 = ServerOptions.instance.PublicName.getValue();
            if (var66 == null || var66.isEmpty()) {
               ServerOptions.instance.PublicName.setValue("My PZ Server");
            }
         }

         var66 = ServerOptions.instance.Map.getValue();
         if (var66 != null && !var66.trim().isEmpty()) {
            GameMap = var66.trim();
            if (GameMap.contains(";")) {
               String[] var68 = GameMap.split(";");
               var66 = var68[0];
            }

            Core.GameMap = var66.trim();
         }

         String var69 = ServerOptions.instance.Mods.getValue();
         int var7;
         String var9;
         if (var69 != null) {
            String[] var70 = var69.split(";");
            String[] var71 = var70;
            var7 = var70.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               var9 = var71[var8];
               if (!var9.trim().isEmpty()) {
                  ServerMods.add(var9.trim());
               }
            }
         }

         int var11;
         int var80;
         if (SteamUtils.isSteamModeEnabled()) {
            var5 = ServerOptions.instance.SteamPort1.getValue();
            int var72 = ServerOptions.instance.SteamPort2.getValue();
            var7 = ServerOptions.instance.SteamVAC.getValue() ? 3 : 2;
            if (!SteamGameServer.Init(IPCommandline, var5, var72, DEFAULT_PORT, var7, Core.getInstance().getSteamServerVersion())) {
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
                  byte[] var75 = new byte[]{-16, -96, -100, -114};
                  var9 = new String(var75, "UTF-8");
                  String var10 = "";

                  for(var11 = 0; var11 < 128; ++var11) {
                     var10 = var10 + var9;
                  }

                  SteamGameServer.SetKeyValue("test", var10);
               } catch (UnsupportedEncodingException var62) {
               }

               SteamGameServer.SetKeyValue("test2", "12345");
            }

            String var76 = ServerOptions.instance.WorkshopItems.getValue();
            if (var76 != null) {
               String[] var78 = var76.split(";");
               String[] var81 = var78;
               var11 = var78.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  String var13 = var81[var12];
                  var13 = var13.trim();
                  if (!var13.isEmpty() && SteamUtils.isValidSteamID(var13)) {
                     WorkshopItems.add(SteamUtils.convertStringToSteamID(var13));
                  }
               }
            }

            SteamWorkshop.init();
            SteamGameServer.LogOnAnonymous();
            SteamGameServer.EnableHeartBeats(true);
            DebugLog.log("Waiting for response from Steam servers");

            while(true) {
               SteamUtils.runLoop();
               var80 = SteamGameServer.GetSteamServersConnectState();
               if (var80 == SteamGameServer.STEAM_SERVERS_CONNECTED) {
                  if (!GameServerWorkshopItems.Install(WorkshopItems)) {
                     return;
                  }
                  break;
               }

               if (var80 == SteamGameServer.STEAM_SERVERS_CONNECTFAILURE) {
                  DebugLog.log("Failed to connect to Steam servers");
                  SteamUtils.shutdown();
                  return;
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var56) {
               }
            }
         }

         var5 = 0;

         try {
            ServerWorldDatabase.instance.create();
         } catch (SQLException var54) {
            var54.printStackTrace();
         } catch (ClassNotFoundException var55) {
            var55.printStackTrace();
         }

         if (ServerOptions.instance.UPnP.getValue()) {
            DebugLog.log("Router detection/configuration starting.");
            DebugLog.log("If the server hangs here, set UPnP=false.");
            PortMapper.startup();
            if (PortMapper.discover()) {
               DebugLog.log("UPnP-enabled internet gateway found: " + PortMapper.getGatewayInfo());
               var6 = PortMapper.getExternalAddress();
               DebugLog.log("External IP address: " + var6);
               DebugLog.log("trying to setup port forwarding rules...");
               var7 = ServerOptions.instance.UPnPLeaseTime.getValue();
               boolean var77 = ServerOptions.instance.UPnPForce.getValue();
               if (PortMapper.addMapping(DEFAULT_PORT, DEFAULT_PORT, "PZ Server default port", "UDP", var7, var77)) {
                  DebugLog.log(DebugType.Network, "Default port has been mapped successfully");
               } else {
                  DebugLog.log(DebugType.Network, "Failed to map default port");
               }

               int var82;
               if (SteamUtils.isSteamModeEnabled()) {
                  var80 = ServerOptions.instance.SteamPort1.getValue();
                  if (PortMapper.addMapping(var80, var80, "PZ Server SteamPort1", "UDP", var7, var77)) {
                     DebugLog.log(DebugType.Network, "SteamPort1 has been mapped successfully");
                  } else {
                     DebugLog.log(DebugType.Network, "Failed to map SteamPort1");
                  }

                  var82 = ServerOptions.instance.SteamPort2.getValue();
                  if (PortMapper.addMapping(var82, var82, "PZ Server SteamPort2", "UDP", var7, var77)) {
                     DebugLog.log(DebugType.Network, "SteamPort2 has been mapped successfully");
                  } else {
                     DebugLog.log(DebugType.Network, "Failed to map SteamPort2");
                  }
               }

               if (UseTCPForMapDownloads) {
                  for(var80 = 1; var80 <= ServerOptions.instance.MaxPlayers.getValue(); ++var80) {
                     var82 = DEFAULT_PORT + var80;
                     if (PortMapper.addMapping(var82, var82, "PZ Server TCP Port " + var80, "TCP", var7, var77)) {
                        DebugLog.log(DebugType.Network, var82 + " has been mapped successfully");
                     } else {
                        DebugLog.log(DebugType.Network, "Failed to map TCP port " + var82);
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
         } catch (Exception var53) {
            var53.printStackTrace();
            DebugLog.log("server terminated");
            CoopSlave.status(Translator.getText("UI_ServerStatus_Terminated"));
            return;
         }

         File var73 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "descriptors.bin");
         if (!var73.exists()) {
            ServerOptions.instance.changeOption("ResetID", (new Integer(Rand.Next(100000000))).toString());
         }

         try {
            parseSpawnRegions();
            initSpawnBuildings();
         } catch (Exception var52) {
            var52.printStackTrace();
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
         } catch (ConnectException var51) {
            var51.printStackTrace();
            SteamUtils.shutdown();
            return;
         }

         if (SteamUtils.isSteamModeEnabled()) {
            DebugLog.log("##########\nServer Steam ID " + SteamGameServer.GetSteamID() + "\n##########");
         }

         PerformanceSettings.LockFPS = 10;
         IngameState var74 = new IngameState();
         float var79 = 0.0F;
         float var83 = 0.0F;
         float[] var84 = new float[20];

         for(var11 = 0; var11 < 20; ++var11) {
            var84[var11] = (float)PerformanceSettings.LockFPS;
         }

         boolean var85 = false;
         float var86 = (float)PerformanceSettings.LockFPS;
         long var21 = 0L;
         int var23 = 0;
         long var24 = 0L;
         long var26 = 1000000000L / (long)PerformanceSettings.LockFPS;
         if (!SteamUtils.isSteamModeEnabled()) {
            PublicServerUtil.init();
            PublicServerUtil.insertOrUpdate();
         }

         ServerLOS.init();
         int var28 = ServerOptions.instance.RCONPort.getValue();
         String var29 = ServerOptions.instance.RCONPassword.getValue();
         if (var28 != 0 && var29 != null && !var29.isEmpty()) {
            RCONServer.init(var28, var29);
         }

         for(; !bDone; SteamUtils.runLoop()) {
            timeSinceKeepAlive += GameTime.getInstance().getMultiplier();
            long var87 = System.nanoTime();
            long var30 = System.currentTimeMillis();
            var26 = 1000000000L / (long)PerformanceSettings.LockFPS;
            double var32 = ServerOptions.instance.ZombieUpdateDelta.getValue();
            ++SendZombies;
            if ((double)((float)SendZombies / var86) > var32) {
               SendZombies = 0;
            }

            ServerMap.instance.preupdate();
            synchronized(MainLoopNetData) {
               MainLoopNetData2.clear();
               MainLoopNetData2.addAll(MainLoopNetData);
               MainLoopNetData.clear();
            }

            int var34;
            for(var34 = 0; var34 < MainLoopNetData2.size(); ++var34) {
               IZomboidPacket var35 = (IZomboidPacket)MainLoopNetData2.get(var34);
               UdpConnection var36;
               if (var35.isConnect()) {
                  var36 = ((GameServer.DelayedConnection)var35).connection;
                  LoggerManager.getLogger("user").write("added connection index=" + var36.index + " " + ((GameServer.DelayedConnection)var35).hostString);
                  udpEngine.connections.add(var36);
               } else if (var35.isDisconnect()) {
                  var36 = ((GameServer.DelayedConnection)var35).connection;
                  LoggerManager.getLogger("user").write(var36.idStr + " \"" + var36.username + "\" removed connection index=" + var36.index);
                  udpEngine.connections.remove(var36);
                  disconnect(var36);
               } else {
                  mainLoopDealWithNetData((ZomboidNetData)var35);
               }
            }

            MainLoopNetData2.clear();
            int var88;
            synchronized(consoleCommands) {
               for(var88 = 0; var88 < consoleCommands.size(); ++var88) {
                  String var89 = (String)consoleCommands.get(var88);

                  try {
                     if (CoopSlave.instance == null || !CoopSlave.instance.handleCommand(var89)) {
                        System.out.println(handleServerCommand(var89, (UdpConnection)null));
                     }
                  } catch (Exception var60) {
                     var60.printStackTrace();
                  }
               }

               consoleCommands.clear();
            }

            RCONServer.update();
            MapCollisionData.instance.updateGameState();
            var74.update();

            try {
               VehicleManager.instance.serverUpdate();
            } catch (Exception var49) {
               var49.printStackTrace();
            }

            var34 = 0;
            var88 = 0;

            int var90;
            for(var90 = 0; var90 < Players.size(); ++var90) {
               IsoPlayer var37 = (IsoPlayer)Players.get(var90);
               if (CheckPlayerStillValid(var37)) {
                  if (!IsoWorld.instance.CurrentCell.getObjectList().contains(var37)) {
                     IsoWorld.instance.CurrentCell.getObjectList().add(var37);
                  }

                  ++var88;
                  if (var37.isAsleep()) {
                     ++var34;
                  }
               }

               ServerMap.instance.characterIn(var37);
            }

            setFastForward(ServerOptions.instance.SleepAllowed.getValue() && var88 > 0 && var34 == var88);

            UdpConnection var91;
            for(var90 = 0; var90 < udpEngine.connections.size(); ++var90) {
               var91 = (UdpConnection)udpEngine.connections.get(var90);

               for(int var38 = 0; var38 < 4; ++var38) {
                  Vector3 var39 = var91.connectArea[var38];
                  if (var39 != null) {
                     ServerMap.instance.characterIn((int)var39.x, (int)var39.y, (int)var39.z);
                  }

                  ClientServerMap.characterIn(var91, var38);
               }

               if (var91.playerDownloadServer != null) {
                  var91.playerDownloadServer.update();
               }
            }

            for(var90 = 0; var90 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++var90) {
               IsoMovingObject var92 = (IsoMovingObject)IsoWorld.instance.CurrentCell.getObjectList().get(var90);
               if (var92 instanceof IsoPlayer && !Players.contains(var92)) {
                  DebugLog.log("Disconnected player in CurrentCell.getObjectList() removed");
                  IsoWorld.instance.CurrentCell.getObjectList().remove(var90--);
               }
            }

            ++var5;
            if (var5 > 150) {
               for(var90 = 0; var90 < udpEngine.connections.size(); ++var90) {
                  var91 = (UdpConnection)udpEngine.connections.get(var90);

                  try {
                     if (var91.username == null && !var91.awaitingCoopApprove) {
                        disconnect(var91);
                        udpEngine.forceDisconnect(var91.getConnectedGUID());
                     }
                  } catch (Exception var48) {
                     var48.printStackTrace();
                  }
               }

               var5 = 0;
            }

            worldObjectsServerSyncReq.serverSendRequests(udpEngine);
            ServerMap.instance.postupdate();
            if (ChunkRevisions.USE_CHUNK_REVISIONS) {
               ChunkRevisions.instance.updateServer();
            }

            try {
               ServerGUI.update();
            } catch (Exception var47) {
               var47.printStackTrace();
            }

            long var15 = System.nanoTime();
            long var17 = var15 - var87;
            long var19 = var26 - var17 - var21;
            if (var19 > 0L) {
               try {
                  Thread.sleep(var19 / 1000000L);
               } catch (InterruptedException var46) {
               }

               var21 = System.nanoTime() - var15 - var19;
            } else {
               var24 -= var19;
               var21 = 0L;
               ++var23;
               if (var23 >= 5) {
                  Thread.yield();
                  var23 = 0;
               }
            }

            var87 = System.nanoTime();
            long var94 = System.currentTimeMillis();
            long var93 = var94 - var30;
            var83 = 1000.0F / (float)var93;
            if (!Float.isNaN(var83)) {
               var86 = (float)((double)var86 + Math.min((double)(var83 - var86) * 0.05D, 1.0D));
            }

            GameTime.instance.FPSMultiplier = 60.0F / var86;
            launchCommandHandler();
            if (!SteamUtils.isSteamModeEnabled()) {
               PublicServerUtil.update();
               PublicServerUtil.updatePlayerCountIfChanged();
            }

            for(int var40 = 0; var40 < udpEngine.connections.size(); ++var40) {
               UdpConnection var41 = (UdpConnection)udpEngine.connections.get(var40);
               if (var41.accessLevel.equals("admin") && var41.sendPulse && var41.isFullyConnected()) {
                  ByteBufferWriter var42 = var41.startPacket();
                  PacketTypes.doPacket((short)1, var42);
                  var42.putLong(System.currentTimeMillis());
                  var41.endPacket();
               }

               if (var41.checksumState == UdpConnection.ChecksumState.Different && var41.checksumTime + 8000L < System.currentTimeMillis()) {
                  DebugLog.log("timed out connection because checksum was different");
                  var41.checksumState = UdpConnection.ChecksumState.Init;
                  var41.forceDisconnect();
               } else if (!var41.chunkObjectState.isEmpty()) {
                  for(int var43 = 0; var43 < var41.chunkObjectState.size(); var43 += 2) {
                     short var44 = var41.chunkObjectState.get(var43);
                     short var45 = var41.chunkObjectState.get(var43 + 1);
                     if (!var41.RelevantTo((float)(var44 * 10 + 5), (float)(var45 * 10 + 5), (float)(var41.ChunkGridWidth * 4 * 10))) {
                        var41.chunkObjectState.remove(var43, 2);
                        var43 -= 2;
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
         (new Thread("command handler") {
            public void run() {
               try {
                  BufferedReader var1 = new BufferedReader(new InputStreamReader(System.in));

                  while(true) {
                     String var2 = var1.readLine();
                     if (var2 == null) {
                        GameServer.consoleCommands.add("process-status@eof");
                        break;
                     }

                     if (!var2.isEmpty()) {
                        synchronized(GameServer.consoleCommands) {
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

   public static String rcon(String var0) {
      try {
         return handleServerCommand(var0, (UdpConnection)null);
      } catch (Throwable var2) {
         var2.printStackTrace();
         return null;
      }
   }

   private static String handleServerCommand(String var0, UdpConnection var1) {
      if (var0 == null) {
         return null;
      } else {
         System.out.println(var0);
         String var2 = "admin";
         String var3 = "admin";
         if (var1 != null) {
            var2 = var1.username;
            var3 = var1.accessLevel;
         }

         if (var1 != null && var1.isCoopHost) {
            var3 = "admin";
         }

         Class var4 = CommandBase.findCommandCls(var0);
         if (var4 != null) {
            Constructor var5 = var4.getConstructors()[0];

            try {
               CommandBase var6 = (CommandBase)var5.newInstance(var2, var3, var0, var1);
               return var6.Execute();
            } catch (InvocationTargetException var7) {
               var7.printStackTrace();
               return "A InvocationTargetException error occured";
            } catch (IllegalAccessException var8) {
               var8.printStackTrace();
               return "A IllegalAccessException error occured";
            } catch (InstantiationException var9) {
               var9.printStackTrace();
               return "A InstantiationException error occured";
            } catch (SQLException var10) {
               var10.printStackTrace();
               return "A SQL error occured";
            }
         } else {
            return "Unknown command " + var0;
         }
      }
   }

   private static void teleport(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      float var3 = var0.getFloat();
      float var4 = var0.getFloat();
      float var5 = var0.getFloat();
      IsoPlayer var6 = getPlayerByRealUserName(var2);
      if (var6 != null) {
         UdpConnection var7 = getConnectionFromPlayer(var6);
         if (var7 != null) {
            ByteBufferWriter var8 = var7.startPacket();
            PacketTypes.doPacket((short)108, var8);
            var8.putByte((byte)var6.PlayerIndex);
            var8.putFloat(var3);
            var8.putFloat(var4);
            var8.putFloat(var5);
            var7.endPacketImmediate();
         }
      }
   }

   public static void sendPlayerExtraInfo(IsoPlayer var0, UdpConnection var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         ByteBufferWriter var4 = var3.startPacket();
         PacketTypes.doPacket((short)84, var4);
         var4.putShort((short)var0.OnlineID);
         var4.putUTF(var0.accessLevel);
         var4.putByte((byte)(var0.godMod ? 1 : 0));
         var4.putByte((byte)(var0.invisible ? 1 : 0));
         var4.putByte((byte)(var0.isNoClip() ? 1 : 0));
         var4.putByte((byte)(var0.isShowAdminTag() ? 1 : 0));
         var3.endPacketImmediate();
      }

   }

   private static void receivePlayerExtraInfo(ByteBuffer var0, UdpConnection var1) {
      IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var0.getShort()));
      if (var2 != null) {
         var1.accessLevel = GameWindow.ReadString(var0);
         var2.godMod = var0.get() == 1;
         var2.invisible = var0.get() == 1;
         var2.GhostMode = var2.invisible;
         var2.setNoClip(var0.get() == 1);
         var2.setShowAdminTag(var0.get() == 1);
         sendPlayerExtraInfo(var2, var1);
      }

   }

   private static void addLevelUpPoint(ByteBuffer var0, UdpConnection var1) {
      if (canModifyPlayerStats(var1)) {
         IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(var0.getInt());
         if (var2 != null && !var2.isDead()) {
            var2.setNumberOfPerksToPick(var2.getNumberOfPerksToPick() + 1);
         }

         for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
            UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
            if (var4.getConnectedGUID() != var1.getConnectedGUID() && var4.getConnectedGUID() == (Long)PlayerToAddressMap.get(var2)) {
               ByteBufferWriter var5 = var4.startPacket();
               PacketTypes.doPacket((short)125, var5);
               var5.putInt(var2.getOnlineID());
               var4.endPacketImmediate();
            }
         }

      }
   }

   private static void addXpFromPlayerStatsUI(ByteBuffer var0, UdpConnection var1) {
      if (canModifyPlayerStats(var1)) {
         IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(var0.getInt());
         int var3 = var0.getInt();
         int var4 = 0;
         int var5 = 0;
         boolean var6 = false;
         if (var2 != null && !var2.isDead()) {
            if (var3 == 0) {
               var5 = var0.getInt();
               var4 = var0.getInt();
               var6 = var0.get() == 1;
               var2.getXp().AddXP(PerkFactory.Perks.fromIndex(var5), (float)var4, false, var6, false, true);
            } else if (var3 == 1) {
               var4 = var0.getInt();
               var2.getXp().addGlobalXP((float)var4);
            }
         }

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID() && var8.getConnectedGUID() == (Long)PlayerToAddressMap.get(var2)) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)124, var9);
               var9.putInt(var2.getOnlineID());
               if (var3 == 0) {
                  var9.putInt(0);
                  var9.putInt(var5);
                  var9.putInt(var4);
                  var9.putByte((byte)(var6 ? 1 : 0));
               } else {
                  var9.putInt(1);
                  var9.putInt(var4);
               }

               var8.endPacketImmediate();
            }
         }

      }
   }

   private static boolean canSeePlayerStats(UdpConnection var0) {
      return !var0.accessLevel.equals("");
   }

   private static boolean canModifyPlayerStats(UdpConnection var0) {
      return var0.accessLevel.equals("admin") || var0.accessLevel.equals("moderator") || var0.accessLevel.equals("overseer");
   }

   private static void syncXp(ByteBuffer var0, UdpConnection var1) {
      if (canModifyPlayerStats(var1)) {
         IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(var0.getInt());
         if (var2 != null && !var2.isDead()) {
            try {
               var2.getXp().load(var0, 143);
            } catch (IOException var8) {
               var8.printStackTrace();
            }

            var2.setNumberOfPerksToPick(var0.getInt());

            for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
               UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
               if (var4.getConnectedGUID() != var1.getConnectedGUID() && var4.getConnectedGUID() == (Long)PlayerToAddressMap.get(var2)) {
                  ByteBufferWriter var5 = var4.startPacket();
                  PacketTypes.doPacket((short)126, var5);
                  var5.putInt(var2.getOnlineID());

                  try {
                     var2.getXp().save(var5.bb);
                  } catch (IOException var7) {
                     var7.printStackTrace();
                  }

                  var5.putInt(var2.getNumberOfPerksToPick());
                  var4.endPacketImmediate();
               }
            }
         }

      }
   }

   private static void receivePlayerStatsChanges(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var3 != null) {
         String var4 = GameWindow.ReadString(var0);
         var3.setPlayerStats(var0, var4);

         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var6.getConnectedGUID() != var1.getConnectedGUID()) {
               if (var6.getConnectedGUID() == (Long)PlayerToAddressMap.get(var3)) {
                  var6.allChatMuted = var3.isAllChatMuted();
                  var6.accessLevel = var3.accessLevel;
               }

               ByteBufferWriter var7 = var6.startPacket();
               var3.createPlayerStats(var7, var4);
               var6.endPacketImmediate();
            }
         }

      }
   }

   public static void doMinimumInit() {
      Rand.init();
      ZomboidFileSystem.instance.init();
      ArrayList var0 = new ArrayList(ServerMods);
      ZomboidFileSystem.instance.loadMods(var0);
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
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      File var1 = new File(GameWindow.getCacheDir() + File.separator + "Server" + File.separator + ServerName + "_SandboxVars.lua");
      if (var1.exists()) {
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
         KahluaTable var2 = (KahluaTable)LuaManager.env.rawget("SpawnRegionMgr");
         if (var2 != null) {
            Object[] var3 = LuaManager.caller.pcall(LuaManager.thread, var2.rawget("getSpawnRegions"));
            if (var3.length > 1 && var3[1] instanceof KahluaTable) {
               SpawnRegions = (KahluaTable)var3[1];
            }
         } else {
            DebugLog.log("ERROR: SpawnRegionMgr is undefined");
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      ServerGUI.init2();
   }

   public static void startServer() throws ConnectException {
      String var0 = ServerOptions.instance.Password.getValue();
      if (CoopSlave.instance != null && SteamUtils.isSteamModeEnabled()) {
         var0 = "";
      }

      udpEngine = new UdpEngine(DEFAULT_PORT, ServerOptions.instance.MaxPlayers.getValue(), var0, true);
      DebugLog.log(DebugType.Network, "*** SERVER STARTED ****");
      DebugLog.log(DebugType.Network, "*** Steam is " + (SteamUtils.isSteamModeEnabled() ? "enabled" : "not enabled"));
      DebugLog.log(DebugType.Network, "server is listening on port " + DEFAULT_PORT);
      ResetID = ServerOptions.instance.ResetID.getValue();
      String var5;
      if (CoopSlave.instance != null) {
         if (SteamUtils.isSteamModeEnabled()) {
            RakNetPeerInterface var1 = udpEngine.getPeer();
            CoopSlave.instance.sendMessage("server-address", (String)null, var1.GetServerIP() + ":" + DEFAULT_PORT);
            long var2 = SteamGameServer.GetSteamID();
            CoopSlave.instance.sendMessage("steam-id", (String)null, SteamUtils.convertSteamIDToString(var2));
         } else {
            var5 = "127.0.0.1";
            CoopSlave.instance.sendMessage("server-address", (String)null, var5 + ":" + DEFAULT_PORT);
         }
      }

      LuaEventManager.triggerEvent("OnServerStarted");
      if (SteamUtils.isSteamModeEnabled()) {
         CoopSlave.status("Server Started");
      } else {
         CoopSlave.status("Server Started");
      }

      var5 = ServerOptions.instance.DiscordChannel.getValue();
      String var6 = ServerOptions.instance.DiscordToken.getValue();
      boolean var3 = ServerOptions.instance.DiscordEnable.getValue();
      String var4 = ServerOptions.instance.DiscordChannelID.getValue();
      discordBot.connect(var3, var6, var5, var4);
   }

   private static void printPacket(ZomboidNetData var0) {
      if (var0.type != 7) {
         DebugLog.log("# " + PacketTypes.packetTypeToString(var0.type));
      }

   }

   private static void mainLoopDealWithNetData(ZomboidNetData var0) {
      ByteBuffer var1 = var0.buffer;
      UdpConnection var2 = udpEngine.getActiveConnection(var0.connection);
      if (var0.type >= 0 && var0.type < packetCounts.length) {
         int var10002 = packetCounts[var0.type]++;
         if (var2 != null) {
            var10002 = var2.packetCounts[var0.type]++;
         }
      }

      try {
         if (var2 == null) {
            DebugLog.log(DebugType.Network, "Received packet type=" + PacketTypes.packetTypeToString(var0.type) + " connection is null.");
            return;
         }

         if (var2.username == null) {
            switch(var0.type) {
            case 2:
            case 50:
            case 87:
               break;
            default:
               DebugLog.log("Received packet type=" + PacketTypes.packetTypeToString(var0.type) + " before Login, disconnecting " + var2.getInetSocketAddress().getHostString());
               var2.forceDisconnect();
               ZomboidNetDataPool.instance.discard(var0);
               return;
            }
         }

         String var21;
         label345:
         switch(var0.type) {
         case 2:
            String var18 = GameWindow.ReadString(var0.buffer).trim();
            String var20 = GameWindow.ReadString(var0.buffer).trim();
            var21 = GameWindow.ReadString(var0.buffer).trim();
            if (!var21.equals(Core.getInstance().getVersionNumber())) {
               ByteBufferWriter var22 = var2.startPacket();
               PacketTypes.doPacket((short)40, var22);
               LoggerManager.getLogger("user").write("access denied: user \"" + var18 + "\" client version (" + var21 + ") does not match server version (" + Core.getInstance().getVersionNumber() + ")");
               var22.putUTF("ClientVersionMismatch##" + var21 + "##" + Core.getInstance().getVersionNumber());
               var2.endPacketImmediate();
               var2.forceDisconnect();
            }

            boolean var23 = true;

            try {
               int var24 = var1.getInt();
            } catch (Exception var14) {
            }

            var2.ip = var2.getInetSocketAddress().getHostString();
            var2.idStr = var2.ip;
            if (SteamUtils.isSteamModeEnabled()) {
               var2.steamID = udpEngine.getClientSteamID(var2.getConnectedGUID());
               var2.ownerID = udpEngine.getClientOwnerSteamID(var2.getConnectedGUID());
               var2.idStr = SteamUtils.convertSteamIDToString(var2.steamID);
               if (var2.steamID != var2.ownerID) {
                  var2.idStr = var2.idStr + "(owner=" + SteamUtils.convertSteamIDToString(var2.ownerID) + ")";
               }
            }

            var2.password = var20;
            LoggerManager.getLogger("user").write(var2.idStr + " \"" + var18 + "\" attempting to join");
            ServerWorldDatabase.LogonResult var9;
            if (CoopSlave.instance != null && SteamUtils.isSteamModeEnabled()) {
               for(int var25 = 0; var25 < udpEngine.connections.size(); ++var25) {
                  UdpConnection var30 = (UdpConnection)udpEngine.connections.get(var25);
                  if (var30 != var2 && var30.steamID == var2.steamID) {
                     LoggerManager.getLogger("user").write("access denied: user \"" + var18 + "\" already connected");
                     ByteBufferWriter var29 = var2.startPacket();
                     PacketTypes.doPacket((short)40, var29);
                     var29.putUTF("AlreadyConnected");
                     var2.endPacketImmediate();
                     var2.forceDisconnect();
                     return;
                  }
               }

               var2.username = var18;
               var2.usernames[0] = var18;
               var2.isCoopHost = udpEngine.connections.size() == 1;
               DebugLog.log(var2.idStr + " isCoopHost=" + var2.isCoopHost);
               var2.accessLevel = "";
               if (!ServerOptions.instance.DoLuaChecksum.getValue() || var2.accessLevel.equals("admin")) {
                  var2.checksumState = UdpConnection.ChecksumState.Done;
               }

               if (getPlayerCount() >= ServerOptions.instance.MaxPlayers.getValue()) {
                  ByteBufferWriter var26 = var2.startPacket();
                  PacketTypes.doPacket((short)40, var26);
                  var26.putUTF("ServerFull");
                  var2.endPacketImmediate();
                  var2.forceDisconnect();
                  return;
               }

               LoggerManager.getLogger("user").write(var2.idStr + " \"" + var18 + "\" allowed to join");
               var9 = ServerWorldDatabase.instance.new LogonResult();
               var9.accessLevel = var2.accessLevel;
               receiveClientConnect(var2, var9);
            } else {
               var9 = ServerWorldDatabase.instance.authClient(var18, var20, var2.ip, var2.steamID);
               ByteBufferWriter var28;
               if (var9.bAuthorized) {
                  for(int var10 = 0; var10 < udpEngine.connections.size(); ++var10) {
                     UdpConnection var11 = (UdpConnection)udpEngine.connections.get(var10);

                     for(int var12 = 0; var12 < 4; ++var12) {
                        if (var18.equals(var11.usernames[var12])) {
                           LoggerManager.getLogger("user").write("access denied: user \"" + var18 + "\" already connected");
                           ByteBufferWriter var13 = var2.startPacket();
                           PacketTypes.doPacket((short)40, var13);
                           var13.putUTF("AlreadyConnected");
                           var2.endPacketImmediate();
                           var2.forceDisconnect();
                           return;
                        }
                     }
                  }

                  var2.username = var18;
                  var2.usernames[0] = var18;
                  transactionIDMap.put(var18, var9.transactionID);
                  if (CoopSlave.instance != null) {
                     var2.isCoopHost = udpEngine.connections.size() == 1;
                     DebugLog.log(var2.idStr + " isCoopHost=" + var2.isCoopHost);
                  }

                  var2.accessLevel = var9.accessLevel;
                  if (!ServerOptions.instance.DoLuaChecksum.getValue() || var9.accessLevel.equals("admin")) {
                     var2.checksumState = UdpConnection.ChecksumState.Done;
                  }

                  if (!var9.accessLevel.equals("") && getPlayerCount() >= ServerOptions.instance.MaxPlayers.getValue()) {
                     var28 = var2.startPacket();
                     PacketTypes.doPacket((short)40, var28);
                     var28.putUTF("ServerFull");
                     var2.endPacketImmediate();
                     var2.forceDisconnect();
                     return;
                  }

                  if (!ServerWorldDatabase.instance.containsUser(var18) && ServerWorldDatabase.instance.containsCaseinsensitiveUser(var18)) {
                     var28 = var2.startPacket();
                     PacketTypes.doPacket((short)40, var28);
                     var28.putUTF("InvalidUsername");
                     var2.endPacketImmediate();
                     var2.forceDisconnect();
                     return;
                  }

                  LoggerManager.getLogger("user").write(var2.idStr + " \"" + var18 + "\" allowed to join");
                  if (ServerOptions.instance.AutoCreateUserInWhiteList.getValue() && !ServerWorldDatabase.instance.containsUser(var18)) {
                     ServerWorldDatabase.instance.addUser(var18, var20);
                  } else {
                     ServerWorldDatabase.instance.setPassword(var18, var20);
                  }

                  ServerWorldDatabase.instance.updateLastConnectionDate(var18, var20);
                  if (SteamUtils.isSteamModeEnabled()) {
                     String var27 = SteamUtils.convertSteamIDToString(var2.steamID);
                     ServerWorldDatabase.instance.setUserSteamID(var18, var27);
                  }

                  receiveClientConnect(var2, var9);
               } else {
                  var28 = var2.startPacket();
                  PacketTypes.doPacket((short)40, var28);
                  if (var9.banned) {
                     LoggerManager.getLogger("user").write("access denied: user \"" + var18 + "\" is banned");
                     if (var9.bannedReason != null && !var9.bannedReason.isEmpty()) {
                        var28.putUTF("BannedReason##" + var9.bannedReason);
                     } else {
                        var28.putUTF("Banned");
                     }
                  } else if (!var9.bAuthorized) {
                     LoggerManager.getLogger("user").write("access denied: user \"" + var18 + "\" reason \"" + var9.dcReason + "\"");
                     var28.putUTF(var9.dcReason != null ? var9.dcReason : "AccessDenied");
                  }

                  var2.endPacketImmediate();
                  var2.forceDisconnect();
               }
            }
            break;
         case 3:
            ChunkRevisions.instance.serverPacket(var0.type, var1, var2);
            break;
         case 4:
            MPDebugInfo.instance.serverPacket(var1, var2);
            break;
         case 5:
            VehicleManager.instance.serverPacket(var1, var2);
            break;
         case 6:
            receivePlayerConnect(var1, var2, var2.username);
            sendInitialWorldState(var2);
            break;
         case 7:
            receivePlayerInfo(var1, var2);
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
            SyncIsoObject(var1, var2);
            break;
         case 14:
            byte var17 = var1.get();
            switch(var17) {
            case 0:
               byte var19 = var1.get();
               var21 = GameWindow.ReadStringUTF(var1);
               IsoPlayer var8 = getPlayerFromConnection(var2, var19);
               if (var8 != null) {
                  SteamGameServer.BUpdateUserData(var8.getSteamID(), var8.username, 0);
               }
            default:
               break label345;
            }
         case 16:
            PassengerMap.serverReceivePacket(var1, var2);
            break;
         case 17:
            AddItemToMap(var1, var2);
            break;
         case 20:
            sendItemsToContainer(var1, var2);
            break;
         case 22:
            removeItemFromContainer(var1, var2);
            break;
         case 23:
            RemoveItemFromMap(var1, var2);
            break;
         case 24:
            if (var2.playerDownloadServer != null) {
               int var5 = var1.getInt();
               int var6 = var1.getInt();
               int var7 = var1.getInt();
               var2.connectArea[0] = new Vector3((float)var5, (float)var6, (float)var7);
               var2.ChunkGridWidth = var7;
               ZombiePopulationManager.instance.updateLoadedAreas();
            }
            break;
         case 25:
            equip(var1, var2);
            break;
         case 26:
            hitZombie(var1, var2);
            break;
         case 27:
            addCoopPlayer(var1, var2);
            break;
         case 28:
            receiveWeaponHit(var1, var2);
            break;
         case 31:
            receiveSandboxOptions(var1);
            break;
         case 32:
            IsoObject var3 = IsoWorld.instance.getItemFromXYZIndexBuffer(var1);
            if (var3 != null && var3 instanceof IsoWindow) {
               byte var16 = var1.get();
               if (var16 == 1) {
                  ((IsoWindow)var3).smashWindow(true);
                  smashWindow((IsoWindow)var3, 1);
               } else if (var16 == 2) {
                  ((IsoWindow)var3).setGlassRemoved(true);
                  smashWindow((IsoWindow)var3, 2);
               }
            }
            break;
         case 33:
            receivePlayerDeath(var1, var2);
            break;
         case 34:
            if (var2.playerDownloadServer != null) {
               var2.playerDownloadServer.receiveRequestArray(var1);
            }
            break;
         case 35:
            receiveItemStats(var1, var2);
            break;
         case 36:
            if (var2.playerDownloadServer != null) {
               var2.playerDownloadServer.receiveCancelRequest(var1);
            }
            break;
         case 37:
            receiveRequestData(var1, var2);
            break;
         case 42:
            doBandage(var1, var2);
            break;
         case 43:
            eatFood(var1, var2);
            break;
         case 44:
            requestItemsForContainer(var1, var2);
            break;
         case 45:
            drink(var1, var2);
            break;
         case 46:
            SyncAlarmClock(var1, var2);
            break;
         case 47:
            receivePacketCounts(var1, var2);
            break;
         case 48:
            loadModData(var1, var2);
            break;
         case 50:
            scoreboard(var2);
            break;
         case 53:
            receiveSound(var1, var2);
            break;
         case 54:
            receiveWorldSound(var1);
            break;
         case 56:
            receiveClothing(var1, var2);
            break;
         case 57:
            receiveClientCommand(var1, var2);
            break;
         case 58:
            receiveObjectModData(var1, var2);
            break;
         case 65:
            SyncPlayerInventory(var1, var2);
            break;
         case 67:
            RequestPlayerData(var1, var2);
            break;
         case 68:
            removeCorpseFromMap(var1, var2);
            break;
         case 69:
            addCorpseToMap(var1, var2);
            break;
         case 75:
            startFireOnClient(var1, var2);
            break;
         case 76:
            updateItemSprite(var1, var2);
            break;
         case 79:
            sendWorldMessage(var1, var2);
            break;
         case 80:
            sendCustomModDataToClient(var2);
            break;
         case 81:
            ReceiveCommand(var1, var2);
            break;
         case 84:
            receivePlayerExtraInfo(var1, var2);
            break;
         case 86:
            toggleSafety(var1, var2);
            break;
         case 87:
            var2.ping = true;
            answerPing(var1, var2);
            break;
         case 88:
            log(var1, var2);
            break;
         case 90:
            updateOverlayFromClient(var1, var2);
            break;
         case 91:
            NetChecksum.comparer.serverPacket(var1, var2);
            break;
         case 92:
            constructedZone(var1, var2);
            break;
         case 94:
            registerZone(var1, var2);
            break;
         case 97:
            doWoundInfection(var1, var2);
            break;
         case 98:
            doStitch(var1, var2);
            break;
         case 99:
            doDisinfect(var1, var2);
            break;
         case 100:
            doAdditionalPain(var1, var2);
            break;
         case 101:
            doRemoveGlass(var1, var2);
            break;
         case 102:
            doSplint(var1, var2);
            break;
         case 103:
            doRemoveBullet(var1, var2);
            break;
         case 104:
            doCleanBurn(var1, var2);
            break;
         case 105:
            SyncThumpable(var1, var2);
            break;
         case 106:
            SyncDoorKey(var1, var2);
            break;
         case 108:
            teleport(var1, var2);
            break;
         case 109:
            removeBlood(var1, var2);
            break;
         case 110:
            AddExplosiveTrap(var1, var2);
            break;
         case 111:
            removeSpecialObject(var1, var2);
            break;
         case 112:
            receiveBodyDamageUpdate(var1, var2);
            break;
         case 114:
            syncSafehouse(var1, var2);
            break;
         case 115:
            destroy(var1, var2);
            break;
         case 116:
            stopFire(var1, var2);
            break;
         case 117:
            doCataplasm(var1, var2);
            break;
         case 120:
            receiveFurnaceChange(var1, var2);
            break;
         case 121:
            receiveCustomColor(var1, var2);
            break;
         case 122:
            syncCompost(var1, var2);
            break;
         case 123:
            receivePlayerStatsChanges(var1, var2);
            break;
         case 124:
            addXpFromPlayerStatsUI(var1, var2);
            break;
         case 125:
            addLevelUpPoint(var1, var2);
            break;
         case 126:
            syncXp(var1, var2);
            break;
         case 127:
            dealWithNetDataShort(var0, var1, var2);
            break;
         case 128:
            sendUserlog(var1, var2, GameWindow.ReadString(var1));
            break;
         case 129:
            addUserlog(var1, var2);
            break;
         case 130:
            removeUserlog(var1, var2);
            break;
         case 131:
            addWarningPoint(var1, var2);
            break;
         case 133:
            wakeUpPlayer(var1, var2);
            break;
         case 134:
            receiveTransactionID(var1, var2);
            break;
         case 135:
            sendDBSchema(var1, var2);
            break;
         case 136:
            sendTableResult(var1, var2);
            break;
         case 137:
            executeQuery(var1, var2);
            break;
         case 138:
            receiveTextColor(var1, var2);
            break;
         case 139:
            syncNonPvpZone(var1, var2);
            break;
         case 140:
            syncFaction(var1, var2);
            break;
         case 141:
            sendFactionInvite(var1, var2);
            break;
         case 142:
            acceptedFactionInvite(var1, var2);
            break;
         case 143:
            addTicket(var1, var2);
            break;
         case 144:
            viewTickets(var1, var2);
            break;
         case 145:
            removeTicket(var1, var2);
            break;
         case 146:
            requestTrading(var1, var2);
            break;
         case 147:
            tradingUIAddItem(var1, var2);
            break;
         case 148:
            tradingUIRemoveItem(var1, var2);
            break;
         case 149:
            tradingUIUpdateState(var1, var2);
            break;
         case 150:
            receiveItemListNet(var1, var2);
            break;
         case 151:
            receiveChunkObjectState(var1, var2);
            break;
         case 152:
            readAnnotedMap(var1, var2);
            break;
         case 153:
            requestInventory(var1, var2);
            break;
         case 154:
            sendInventory(var1, var2);
            break;
         case 155:
            invMngSendItem(var1, var2);
            break;
         case 156:
            invMngGotItem(var1, var2);
            break;
         case 157:
            invMngRemoveItem(var1, var2);
            break;
         case 160:
            GameTime.getInstance();
            GameTime.receiveTimeSync(var1, var2);
            break;
         case 161:
            SyncIsoObjectReq(var1, var2);
            break;
         case 162:
            receivePlayerSave(var1, var2);
            break;
         case 164:
            short var4 = var1.getShort();
            if (var4 == 1) {
               SyncObjectChunkHashes(var1, var2);
            } else if (var4 == 3) {
               SyncObjectsGridSquareRequest(var1, var2);
            } else if (var4 == 5) {
               SyncObjectsRequest(var1, var2);
            }
            break;
         case 165:
            receivePlayerOnBeaten(var1, var2);
            break;
         case 185:
            ChatServer.getInstance().processMessageFromPlayerPacket(var1);
            break;
         case 187:
            ChatServer.getInstance().processPlayerStartWhisperChatPacket(var1);
            break;
         case 200:
            receiveClimateManagerPacket(var1, var2);
            break;
         case 202:
            IsoRegion.receiveClientRequestFullDataChunks(var1, var2);
         }
      } catch (Exception var15) {
         if (var2 == null) {
            DebugLog.log(DebugType.Network, "Error with packet of type: " + var0.type + " connection is null.");
         } else {
            DebugLog.log(DebugType.Network, "Error with packet of type: " + var0.type + " for " + var2.username);
         }

         var15.printStackTrace();
      }

      ZomboidNetDataPool.instance.discard(var0);
   }

   private static void invMngRemoveItem(ByteBuffer var0, UdpConnection var1) {
      long var2 = var0.getLong();
      int var4 = var0.getInt();
      IsoPlayer var5 = (IsoPlayer)IDToPlayerMap.get(var4);
      if (var5 != null) {
         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() != var1.getConnectedGUID() && var7.getConnectedGUID() == (Long)PlayerToAddressMap.get(var5)) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)157, var8);
               var8.putLong(var2);
               var7.endPacketImmediate();
               break;
            }
         }

      }
   }

   private static void invMngGotItem(ByteBuffer var0, UdpConnection var1) throws IOException {
      int var2 = var0.getInt();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var3 != null) {
         for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
            UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
            if (var5.getConnectedGUID() != var1.getConnectedGUID() && var5.getConnectedGUID() == (Long)PlayerToAddressMap.get(var3)) {
               ByteBufferWriter var6 = var5.startPacket();
               PacketTypes.doPacket((short)156, var6);
               var0.rewind();
               var6.bb.put(var0);
               var5.endPacketImmediate();
               break;
            }
         }

      }
   }

   private static void invMngSendItem(ByteBuffer var0, UdpConnection var1) {
      long var2 = 0L;
      String var4 = null;
      if (var0.get() == 1) {
         var4 = GameWindow.ReadString(var0);
      } else {
         var2 = var0.getLong();
      }

      int var5 = var0.getInt();
      int var6 = var0.getInt();
      IsoPlayer var7 = (IsoPlayer)IDToPlayerMap.get(var6);
      if (var7 != null) {
         for(int var8 = 0; var8 < udpEngine.connections.size(); ++var8) {
            UdpConnection var9 = (UdpConnection)udpEngine.connections.get(var8);
            if (var9.getConnectedGUID() != var1.getConnectedGUID() && var9.getConnectedGUID() == (Long)PlayerToAddressMap.get(var7)) {
               ByteBufferWriter var10 = var9.startPacket();
               PacketTypes.doPacket((short)155, var10);
               if (var4 != null) {
                  var10.putByte((byte)1);
                  var10.putUTF(var4);
               } else {
                  var10.putByte((byte)0);
                  var10.putLong(var2);
               }

               var10.putLong((long)var5);
               var9.endPacketImmediate();
               break;
            }
         }

      }
   }

   private static void sendInventory(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      Long var3 = (Long)IDToAddressMap.get(var2);
      if (var3 != null) {
         for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
            UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
            if (var5.getConnectedGUID() == var3) {
               ByteBufferWriter var6 = var5.startPacket();
               PacketTypes.doPacket((short)154, var6);
               var6.bb.put(var0);
               var5.endPacketImmediate();
               break;
            }
         }
      }

   }

   private static void requestInventory(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      Long var4 = (Long)IDToAddressMap.get(var3);
      if (var4 != null) {
         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var6.getConnectedGUID() == var4) {
               ByteBufferWriter var7 = var6.startPacket();
               PacketTypes.doPacket((short)153, var7);
               var7.putInt(var2);
               var6.endPacketImmediate();
               break;
            }
         }
      }

   }

   private static void receivePacketCounts(ByteBuffer var0, UdpConnection var1) {
      if (!var1.accessLevel.isEmpty()) {
         ByteBufferWriter var2 = var1.startPacket();
         PacketTypes.doPacket((short)47, var2);

         for(int var3 = 0; var3 < 256; ++var3) {
            var2.putLong(packetCounts[var3]);
         }

         var1.endPacket();
      }
   }

   private static void receiveSandboxOptions(ByteBuffer var0) {
      try {
         SandboxOptions.instance.load(var0);
         SandboxOptions.instance.applySettings();
         SandboxOptions.instance.toLua();
         SandboxOptions.instance.saveServerLuaFile(ServerName);

         for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
            UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
            ByteBufferWriter var3 = var2.startPacket();
            PacketTypes.doPacket((short)31, var3);
            var0.rewind();
            var3.bb.put(var0);
            var2.endPacket();
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   private static void receiveChunkObjectState(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      short var3 = var0.getShort();
      IsoChunk var4 = ServerMap.instance.getChunk(var2, var3);
      if (var4 == null) {
         var1.chunkObjectState.add(var2);
         var1.chunkObjectState.add(var3);
      } else {
         ByteBufferWriter var5 = var1.startPacket();
         PacketTypes.doPacket((short)151, var5);
         var5.putShort(var2);
         var5.putShort(var3);

         try {
            if (var4.saveObjectState(var5.bb)) {
               var1.endPacket();
            } else {
               var1.cancelPacket();
            }
         } catch (Throwable var7) {
            var7.printStackTrace();
            var1.cancelPacket();
            return;
         }
      }

   }

   private static void readAnnotedMap(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      StashSystem.prepareBuildingStash(var2);
   }

   private static void tradingUIRemoveItem(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      Long var5 = (Long)IDToAddressMap.get(var3);
      if (var5 != null) {
         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() == var5) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)148, var8);
               var8.putInt(var2);
               var8.putInt(var4);
               var7.endPacketImmediate();
               break;
            }
         }
      }

   }

   private static void tradingUIUpdateState(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      Long var5 = (Long)IDToAddressMap.get(var3);
      if (var5 != null) {
         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() == var5) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)149, var8);
               var8.putInt(var2);
               var8.putInt(var4);
               var7.endPacketImmediate();
               break;
            }
         }
      }

   }

   private static void tradingUIAddItem(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      String var4 = GameWindow.ReadString(var0);
      InventoryItem var5 = InventoryItemFactory.CreateItem(var4);
      if (var5 != null) {
         byte var6 = var0.get();

         try {
            var5.load(var0, 143, false);
         } catch (IOException var13) {
            var13.printStackTrace();
            return;
         }

         Long var7 = (Long)IDToAddressMap.get(var3);
         if (var7 != null) {
            for(int var8 = 0; var8 < udpEngine.connections.size(); ++var8) {
               UdpConnection var9 = (UdpConnection)udpEngine.connections.get(var8);
               if (var9.getConnectedGUID() == var7) {
                  ByteBufferWriter var10 = var9.startPacket();
                  PacketTypes.doPacket((short)147, var10);
                  var10.putInt(var2);

                  try {
                     var5.save(var10.bb, false);
                  } catch (IOException var12) {
                     var12.printStackTrace();
                  }

                  var9.endPacketImmediate();
                  break;
               }
            }
         }

      }
   }

   private static void requestTrading(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      byte var4 = var0.get();
      Long var5 = (Long)IDToAddressMap.get(var2);
      if (var4 == 0) {
         var5 = (Long)IDToAddressMap.get(var3);
      }

      if (var5 != null) {
         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() == var5) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)146, var8);
               if (var4 == 0) {
                  var8.putInt(var2);
               } else {
                  var8.putInt(var3);
               }

               var8.putByte(var4);
               var7.endPacketImmediate();
               break;
            }
         }
      }

   }

   private static void syncFaction(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      int var4 = var0.getInt();
      Faction var5 = Faction.getFaction(var2);
      boolean var6 = false;
      if (var5 == null) {
         var5 = new Faction(var2, var3);
         var6 = true;
         Faction.getFactions().add(var5);
      }

      var5.getPlayers().clear();
      if (var0.get() == 1) {
         var5.setTag(GameWindow.ReadString(var0));
         var5.setTagColor(new ColorInfo(var0.getFloat(), var0.getFloat(), var0.getFloat(), 1.0F));
      }

      for(int var7 = 0; var7 < var4; ++var7) {
         String var8 = GameWindow.ReadString(var0);
         var5.getPlayers().add(var8);
      }

      if (!var5.getOwner().equals(var3)) {
         var5.setOwner(var3);
      }

      boolean var11 = var0.get() == 1;
      if (ChatServer.isInited()) {
         if (var6) {
            ChatServer.getInstance().createFactionChat(var2);
         }

         if (var11) {
            ChatServer.getInstance().removeFactionChat(var2);
         } else {
            ChatServer.getInstance().syncFactionChatMembers(var2, var3, var5.getPlayers());
         }
      }

      if (var11) {
         Faction.getFactions().remove(var5);
         DebugLog.log("faction: removed " + var2 + " owner=" + var5.getOwner());
      }

      for(int var12 = 0; var12 < udpEngine.connections.size(); ++var12) {
         UdpConnection var9 = (UdpConnection)udpEngine.connections.get(var12);
         if (var1 == null || var9.getConnectedGUID() != var1.getConnectedGUID()) {
            ByteBufferWriter var10 = var9.startPacket();
            PacketTypes.doPacket((short)140, var10);
            var5.writeToBuffer(var10, var11);
            var9.endPacketImmediate();
         }
      }

   }

   private static void syncNonPvpZone(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      String var6 = GameWindow.ReadString(var0);
      NonPvpZone var7 = NonPvpZone.getZoneByTitle(var6);
      if (var7 == null) {
         var7 = NonPvpZone.addNonPvpZone(var6, var2, var3, var4, var5);
      }

      if (var7 != null) {
         boolean var8 = var0.get() == 1;
         sendNonPvpZone(var7, var8, var1);
         if (var8) {
            NonPvpZone.removeNonPvpZone(var6, true);
            DebugLog.log("non pvp zone: removed " + var2 + "," + var3 + ", ttle=" + var7.getTitle());
         }

      }
   }

   public static void sendNonPvpZone(NonPvpZone var0, boolean var1, UdpConnection var2) {
      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var2 == null || var4.getConnectedGUID() != var2.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)139, var5);
            var5.putInt(var0.getX());
            var5.putInt(var0.getY());
            var5.putInt(var0.getX2());
            var5.putInt(var0.getY2());
            var5.putUTF(var0.getTitle());
            var5.putBoolean(var1);
            var4.endPacketImmediate();
         }
      }

   }

   private static void receiveTextColor(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var3 != null) {
         float var4 = var0.getFloat();
         float var5 = var0.getFloat();
         float var6 = var0.getFloat();
         var3.setSpeakColourInfo(new ColorInfo(var4, var5, var6, 1.0F));

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)138, var9);
               var9.putInt(var2);
               var9.putFloat(var4);
               var9.putFloat(var5);
               var9.putFloat(var6);
               var8.endPacketImmediate();
            }
         }

      }
   }

   private static void receiveTransactionID(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      IsoPlayer var4 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var4 != null) {
         transactionIDMap.put(var4.username, var3);
         var4.setTransactionID(var3);
         ServerWorldDatabase.instance.saveTransactionID(var4.username, var3);
      }

   }

   private static void syncCompost(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 != null) {
         IsoCompost var6 = var5.getCompost();
         if (var6 == null) {
            var6 = new IsoCompost(var5.getCell(), var5);
            var5.AddSpecialObject(var6);
         }

         float var7 = var0.getFloat();
         var6.setCompost(var7);
         sendCompost(var6, var1);
      }

   }

   public static void sendCompost(IsoCompost var0, UdpConnection var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         if (var3.ReleventTo((float)var0.square.x, (float)var0.square.y) && (var1 != null && var3.getConnectedGUID() != var1.getConnectedGUID() || var1 == null)) {
            ByteBufferWriter var4 = var3.startPacket();
            PacketTypes.doPacket((short)122, var4);
            var4.putInt(var0.square.x);
            var4.putInt(var0.square.y);
            var4.putInt(var0.square.z);
            var4.putFloat(var0.getCompost());
            var3.endPacketImmediate();
         }
      }

   }

   private static void doCataplasm(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         float var5 = var0.getFloat();
         float var6 = var0.getFloat();
         float var7 = var0.getFloat();
         if (var5 > 0.0F) {
            var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setPlantainFactor(var5);
         }

         if (var6 > 0.0F) {
            var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setComfreyFactor(var6);
         }

         if (var7 > 0.0F) {
            var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setGarlicFactor(var7);
         }

         for(int var8 = 0; var8 < udpEngine.connections.size(); ++var8) {
            UdpConnection var9 = (UdpConnection)udpEngine.connections.get(var8);
            if (var9.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var10 = var9.startPacket();
               PacketTypes.doPacket((short)117, var10);
               var10.putShort(var2);
               var10.putInt(var4);
               var10.putFloat(var5);
               var10.putFloat(var6);
               var10.putFloat(var7);
               var9.endPacketImmediate();
            }
         }
      }

   }

   private static void destroy(ByteBuffer var0, UdpConnection var1) {
      if (ServerOptions.instance.AllowDestructionBySledgehammer.getValue()) {
         RemoveItemFromMap(var0, var1);
      }

   }

   public static void AddExplosiveTrap(HandWeapon var0, IsoGridSquare var1, boolean var2) {
      IsoTrap var3 = new IsoTrap(var0, var1.getCell(), var1);
      int var4 = 0;
      if (var0.getExplosionRange() > 0) {
         var4 = var0.getExplosionRange();
      }

      if (var0.getFireRange() > 0) {
         var4 = var0.getFireRange();
      }

      if (var0.getSmokeRange() > 0) {
         var4 = var0.getSmokeRange();
      }

      var1.AddTileObject(var3);

      for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
         UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
         ByteBufferWriter var7 = var6.startPacket();
         PacketTypes.doPacket((short)110, var7);
         var7.putInt(var1.x);
         var7.putInt(var1.y);
         var7.putInt(var1.z);

         try {
            var0.save(var7.bb, false);
         } catch (IOException var9) {
            var9.printStackTrace();
         }

         var7.putInt(var4);
         var7.putBoolean(var2);
         var7.putBoolean(false);
         var6.endPacketImmediate();
      }

   }

   private static void AddExplosiveTrap(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 != null) {
         String var6 = GameWindow.ReadString(var0);
         byte var7 = var0.get();
         InventoryItem var8 = InventoryItemFactory.CreateItem(var6);
         HandWeapon var9 = (HandWeapon)var8;

         try {
            var9.load(var0, 143, false);
         } catch (IOException var19) {
            var19.printStackTrace();
            return;
         }

         DebugLog.log("trap: user \"" + var1.username + "\" added " + var6 + " at " + var2 + "," + var3 + "," + var4);
         LoggerManager.getLogger("map").write(var1.idStr + " \"" + var1.username + "\" added " + var6 + " at " + var2 + "," + var3 + "," + var4);
         IsoTrap var10 = new IsoTrap(var9, var5.getCell(), var5);
         int var11 = var0.getInt();
         boolean var12 = var0.get() == 1;
         boolean var13 = var0.get() == 1;
         if (var13) {
            var5.drawCircleExplosion(var11, var10, var12);
            var10.removeFromWorld();
         } else {
            var5.AddTileObject(var10);

            for(int var14 = 0; var14 < udpEngine.connections.size(); ++var14) {
               UdpConnection var15 = (UdpConnection)udpEngine.connections.get(var14);
               if (var15.getConnectedGUID() != var1.getConnectedGUID()) {
                  ByteBufferWriter var16 = var15.startPacket();
                  PacketTypes.doPacket((short)110, var16);
                  var16.putInt(var2);
                  var16.putInt(var3);
                  var16.putInt(var4);

                  try {
                     var9.save(var16.bb, false);
                  } catch (IOException var18) {
                     var18.printStackTrace();
                  }

                  var16.putInt(var11);
                  var16.putBoolean(var12);
                  var16.putBoolean(var13);
                  var15.endPacketImmediate();
               }
            }
         }
      }

   }

   public static void sendHelicopter(float var0, float var1, boolean var2) {
      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         ByteBufferWriter var5 = var4.startPacket();
         PacketTypes.doPacket((short)11, var5);
         var5.putFloat(var0);
         var5.putFloat(var1);
         var5.putBoolean(var2);
         var4.endPacketImmediate();
      }

   }

   private static void registerZone(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      int var6 = var0.getInt();
      int var7 = var0.getInt();
      int var8 = var0.getInt();
      int var9 = var0.getInt();
      boolean var10 = var0.get() == 1;
      ArrayList var11 = IsoWorld.instance.getMetaGrid().getZonesAt(var4, var5, var6);
      boolean var12 = false;
      Iterator var13 = var11.iterator();

      while(var13.hasNext()) {
         IsoMetaGrid.Zone var14 = (IsoMetaGrid.Zone)var13.next();
         if (var3.equals(var14.getType())) {
            var12 = true;
            var14.setName(var2);
            var14.setLastActionTimestamp(var9);
         }
      }

      if (!var12) {
         IsoWorld.instance.getMetaGrid().registerZone(var2, var3, var4, var5, var6, var7, var8);
      }

      if (var10) {
         for(int var16 = 0; var16 < udpEngine.connections.size(); ++var16) {
            UdpConnection var17 = (UdpConnection)udpEngine.connections.get(var16);
            if (var17.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var15 = var17.startPacket();
               PacketTypes.doPacket((short)94, var15);
               var15.putUTF(var2);
               var15.putUTF(var3);
               var15.putInt(var4);
               var15.putInt(var5);
               var15.putInt(var6);
               var15.putInt(var7);
               var15.putInt(var8);
               var15.putInt(var9);
               var17.endPacketImmediate();
            }
         }
      }

   }

   public static void sendZone(IsoMetaGrid.Zone var0, UdpConnection var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         if (var1 == null || var3.getConnectedGUID() != var1.getConnectedGUID()) {
            ByteBufferWriter var4 = var3.startPacket();
            PacketTypes.doPacket((short)94, var4);
            var4.putUTF(var0.name);
            var4.putUTF(var0.type);
            var4.putInt(var0.x);
            var4.putInt(var0.y);
            var4.putInt(var0.z);
            var4.putInt(var0.w);
            var4.putInt(var0.h);
            var4.putInt(var0.lastActionTimestamp);
            var3.endPacketImmediate();
         }
      }

   }

   private static void constructedZone(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      IsoMetaGrid.Zone var5 = IsoWorld.instance.MetaGrid.getZoneAt(var2, var3, var4);
      if (var5 != null) {
         var5.setHaveConstruction(true);
      }

   }

   public static void addXp(IsoPlayer var0, PerkFactory.Perks var1, int var2) {
      if (PlayerToAddressMap.containsKey(var0)) {
         long var3 = (Long)PlayerToAddressMap.get(var0);
         UdpConnection var5 = udpEngine.getActiveConnection(var3);
         if (var5 == null) {
            return;
         }

         ByteBufferWriter var6 = var5.startPacket();
         PacketTypes.doPacket((short)89, var6);
         var6.putByte((byte)var0.PlayerIndex);
         var6.putInt(var1.index());
         var6.putInt(var2);
         var5.endPacketImmediate();
      }

   }

   private static void log(ByteBuffer var0, UdpConnection var1) {
      LoggerManager.getLogger(GameWindow.ReadString(var0)).write(GameWindow.ReadString(var0));
   }

   private static void answerPing(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);

      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var4.getConnectedGUID() == var1.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)87, var5);
            var5.putUTF(var2);
            var5.putInt(udpEngine.connections.size());
            var5.putInt(512);
            var4.endPacketImmediate();
         }
      }

   }

   private static void updateItemSprite(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      String var3 = GameWindow.ReadStringUTF(var0);
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      int var6 = var0.getInt();
      int var7 = var0.getInt();
      IsoGridSquare var8 = IsoWorld.instance.CurrentCell.getGridSquare(var4, var5, var6);
      if (var8 != null && var7 < var8.getObjects().size()) {
         try {
            IsoObject var9 = (IsoObject)var8.getObjects().get(var7);
            if (var9 != null) {
               var9.sprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(var2);
               if (var9.sprite == null && !var3.isEmpty()) {
                  var9.setSprite(var3);
               }

               var9.RemoveAttachedAnims();
               int var10 = var0.get() & 255;

               for(int var11 = 0; var11 < var10; ++var11) {
                  int var12 = var0.getInt();
                  IsoSprite var13 = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(var12);
                  if (var13 != null) {
                     var9.AttachExistingAnim(var13, 0, 0, false, 0, false, 0.0F);
                  }
               }

               var9.transmitUpdatedSpriteToClients(var1);
            }
         } catch (Exception var14) {
         }
      }

   }

   private static void sendWorldMessage(ByteBuffer var0, UdpConnection var1) {
      if (!var1.allChatMuted) {
         String var2 = GameWindow.ReadString(var0);
         String var3 = GameWindow.ReadString(var0);
         if (var3.length() > 256) {
            var3 = var3.substring(0, 256);
         }

         for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
            UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
            ByteBufferWriter var6 = var5.startPacket();
            PacketTypes.doPacket((short)79, var6);
            var6.putUTF(var2);
            var6.putUTF(var3);
            var5.endPacketImmediate();
         }

         discordBot.sendMessage(var2, var3);
         LoggerManager.getLogger("chat").write(var1.index + " \"" + var1.username + "\" A \"" + var3 + "\"");
      }
   }

   private static void sendCustomModDataToClient(UdpConnection var0) {
      LuaEventManager.triggerEvent("SendCustomModData");
   }

   public static void stopFire(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      short var10;
      if (var2 == 1) {
         var10 = var0.getShort();
         IsoPlayer var12 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var10));
         if (var12 != null) {
            var12.sendObjectChange("StopBurning");
         }

      } else if (var2 == 2) {
         var10 = var0.getShort();
         IsoZombie var11 = ServerMap.instance.ZombieMap.get(var10);
         if (var11 != null) {
            var11.StopBurning();
         }

      } else {
         int var3 = var0.getInt();
         int var4 = var0.getInt();
         int var5 = var0.getInt();
         IsoGridSquare var6 = ServerMap.instance.getGridSquare(var3, var4, var5);
         if (var6 != null) {
            var6.stopFire();

            for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
               UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
               if (var8.ReleventTo((float)var3, (float)var4) && var8.getConnectedGUID() != var1.getConnectedGUID()) {
                  ByteBufferWriter var9 = var8.startPacket();
                  PacketTypes.doPacket((short)116, var9);
                  var9.putInt(var3);
                  var9.putInt(var4);
                  var9.putInt(var5);
                  var8.endPacketImmediate();
               }
            }

         }
      }
   }

   public static void startFireOnClient(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      boolean var6 = var0.get() == 1;
      int var7 = var0.getInt();
      boolean var8 = var0.get() == 1;
      if (!var8 && ServerOptions.instance.NoFire.getValue()) {
         DebugLog.log("user \"" + var1.username + "\" tried to start a fire");
      } else {
         IsoGridSquare var9 = ServerMap.instance.getGridSquare(var2, var3, var4);
         if (var9 != null) {
            IsoFire var10 = var8 ? new IsoFire(var9.getCell(), var9, var6, var5, var7, true) : new IsoFire(var9.getCell(), var9, var6, var5, var7);
            IsoFireManager.Add(var10);
            var9.getObjects().add(var10);

            for(int var11 = 0; var11 < udpEngine.connections.size(); ++var11) {
               UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var11);
               if (var12.ReleventTo((float)var2, (float)var3)) {
                  ByteBufferWriter var13 = var12.startPacket();
                  PacketTypes.doPacket((short)75, var13);
                  var13.putInt(var2);
                  var13.putInt(var3);
                  var13.putInt(var4);
                  var13.putInt(var5);
                  var13.putBoolean(var6);
                  var13.putInt(var10.SpreadDelay);
                  var13.putInt(var10.Life);
                  var13.putInt(var10.numFlameParticles);
                  var13.putBoolean(var8);
                  var12.endPacketImmediate();
               }
            }

         }
      }
   }

   public static void startFireOnClient(IsoGridSquare var0, int var1, boolean var2, int var3, boolean var4) {
      IsoFire var5 = var4 ? new IsoFire(var0.getCell(), var0, var2, var1, var3, true) : new IsoFire(var0.getCell(), var0, var2, var1, var3);
      IsoFireManager.Add(var5);
      var0.getObjects().add(var5);

      for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
         UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
         if (var7.ReleventTo((float)var0.getX(), (float)var0.getY())) {
            ByteBufferWriter var8 = var7.startPacket();
            PacketTypes.doPacket((short)75, var8);
            var8.putInt(var0.getX());
            var8.putInt(var0.getY());
            var8.putInt(var0.getZ());
            var8.putInt(var1);
            var8.putBoolean(var2);
            var8.putInt(var5.SpreadDelay);
            var8.putInt(var5.Life);
            var8.putInt(var5.numFlameParticles);
            var8.putBoolean(var4);
            var7.endPacketImmediate();
         }
      }

   }

   public static void sendOptionsToClients() {
      for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
         UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
         ByteBufferWriter var2 = var1.startPacket();
         PacketTypes.doPacket((short)82, var2);
         var2.putInt(ServerOptions.instance.getPublicOptions().size());
         String var3 = null;
         Iterator var4 = ServerOptions.instance.getPublicOptions().iterator();

         while(var4.hasNext()) {
            var3 = (String)var4.next();
            var2.putUTF(var3);
            var2.putUTF(ServerOptions.instance.getOption(var3));
         }

         var1.endPacketImmediate();
      }

   }

   public static void sendCorpse(IsoDeadBody var0) {
      IsoGridSquare var1 = var0.getSquare();
      if (var1 != null) {
         for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
            UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
            if (var3.ReleventTo((float)var1.x, (float)var1.y)) {
               ByteBufferWriter var4 = var3.startPacket();
               PacketTypes.doPacket((short)69, var4);
               var4.putInt(var1.x);
               var4.putInt(var1.y);
               var4.putInt(var1.z);
               var0.writeToRemoteBuffer(var4);
               var3.endPacketImmediate();
            }
         }

      }
   }

   private static void addCorpseToMap(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      IsoObject var5 = WorldItemTypes.createFromBuffer(var0);
      if (var5 != null && var5 instanceof IsoDeadBody) {
         var5.loadFromRemoteBuffer(var0, false);
         IsoGridSquare var6 = ServerMap.instance.getGridSquare(var2, var3, var4);
         if (var6 != null) {
            var6.addCorpse((IsoDeadBody)var5, true);
            var6.revisionUp();

            for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
               UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
               if (var8.getConnectedGUID() != var1.getConnectedGUID() && var8.ReleventTo((float)var2, (float)var3)) {
                  ByteBufferWriter var9 = var8.startPacket();
                  PacketTypes.doPacket((short)69, var9);
                  var0.rewind();
                  var9.bb.put(var0);
                  var8.endPacketImmediate();
               }
            }
         }

         LoggerManager.getLogger("item").write(var1.idStr + " \"" + var1.username + "\" corpse +1 " + var2 + "," + var3 + "," + var4);
      }
   }

   public static void removeCorpseFromMap(IsoDeadBody var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         if (var0.getSquare() != null && var2.ReleventTo(var0.getX(), var0.getY())) {
            ByteBufferWriter var3 = var2.startPacket();
            PacketTypes.doPacket((short)68, var3);
            var3.putInt(var0.getSquare().getX());
            var3.putInt(var0.getSquare().getY());
            var3.putInt(var0.getSquare().getZ());
            var3.putInt(var0.getSquare().getStaticMovingObjects().indexOf(var0));
            var2.endPacketImmediate();
         }
      }

   }

   private static void removeCorpseFromMap(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      IsoGridSquare var6 = ServerMap.instance.getGridSquare(var2, var3, var4);
      if (var6 != null && var5 >= 0 && var5 < var6.getStaticMovingObjects().size()) {
         IsoObject var7 = (IsoObject)var6.getStaticMovingObjects().get(var5);
         var6.removeCorpse((IsoDeadBody)var7, true);
         var6.revisionUp();

         for(int var8 = 0; var8 < udpEngine.connections.size(); ++var8) {
            UdpConnection var9 = (UdpConnection)udpEngine.connections.get(var8);
            if (var9.getConnectedGUID() != var1.getConnectedGUID() && var9.ReleventTo((float)var2, (float)var3)) {
               ByteBufferWriter var10 = var9.startPacket();
               PacketTypes.doPacket((short)68, var10);
               var0.rewind();
               var10.bb.put(var0);
               var9.endPacketImmediate();
            }
         }
      }

      LoggerManager.getLogger("item").write(var1.idStr + " \"" + var1.username + "\" corpse -1 " + var2 + "," + var3 + "," + var4);
   }

   private static void sendPlayerConnect(IsoPlayer var0, UdpConnection var1) {
      ByteBufferWriter var2 = var1.startPacket();
      PacketTypes.doPacket((short)6, var2);
      if (var1.getConnectedGUID() != (Long)PlayerToAddressMap.get(var0)) {
         var2.putShort((short)var0.OnlineID);
      } else {
         var2.putShort((short)-1);
         var2.putByte((byte)var0.PlayerIndex);
         var2.putShort((short)var0.OnlineID);

         try {
            GameTime.getInstance().saveToPacket(var2.bb);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

      var2.putFloat(var0.x);
      var2.putFloat(var0.y);
      var2.putFloat(var0.z);
      if (var1.getConnectedGUID() != (Long)PlayerToAddressMap.get(var0)) {
         try {
            var0.getDescriptor().save(var2.bb);
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      }

      var2.putUTF(var0.username);
      if (SteamUtils.isSteamModeEnabled()) {
         var2.putLong(var0.getSteamID());
      }

      var2.putByte((byte)(var0.godMod ? 1 : 0));
      var2.putByte((byte)(var0.isSafety() ? 1 : 0));
      var2.putUTF(var0.accessLevel);
      var2.putByte((byte)(var0.invisible ? 1 : 0));
      if (var1.getConnectedGUID() != (Long)PlayerToAddressMap.get(var0) && canSeePlayerStats(var1)) {
         try {
            var0.getXp().save(var2.bb);
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      }

      var2.putUTF(var0.getTagPrefix());
      var2.putFloat(var0.getTagColor().r);
      var2.putFloat(var0.getTagColor().g);
      var2.putFloat(var0.getTagColor().b);
      var2.putDouble(var0.getHoursSurvived());
      var2.putInt(var0.getZombieKills());
      var2.putUTF(var0.getDisplayName());
      var2.putFloat(var0.getSpeakColour().r);
      var2.putFloat(var0.getSpeakColour().g);
      var2.putFloat(var0.getSpeakColour().b);
      var2.putBoolean(var0.showTag);
      var2.putBoolean(var0.factionPvp);
      var1.endPacketImmediate();
      if (var1.getConnectedGUID() != (Long)PlayerToAddressMap.get(var0)) {
         updateHandEquips(var1, var0);
      }

   }

   private static void RequestPlayerData(ByteBuffer var0, UdpConnection var1) {
      IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var0.getShort()));
      if (var2 != null) {
         sendPlayerConnect(var2, var1);
      }

   }

   private static void SyncPlayerInventory(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      IsoPlayer var3 = getPlayerFromConnection(var1, var2);
      if (var3 != null) {
         DebugLog.log("SyncPlayerInventory " + var3.username);
         var3.setInventory(new ItemContainer());

         try {
            ArrayList var4 = var3.getInventory().load(var0, 143, false);
            short var5 = var0.getShort();
            if (var5 >= 0 && var5 < var4.size()) {
               var3.setClothingItem_Torso((InventoryItem)var4.get(var5));
            }

            var5 = var0.getShort();
            if (var5 >= 0 && var5 < var4.size()) {
               var3.setClothingItem_Legs((InventoryItem)var4.get(var5));
            }

            var5 = var0.getShort();
            if (var5 >= 0 && var5 < var4.size()) {
               var3.setClothingItem_Feet((InventoryItem)var4.get(var5));
            }
         } catch (IOException var9) {
            var9.printStackTrace();
         }

         if (PlayerToBody.containsKey(var3)) {
            ((IsoDeadBody)PlayerToBody.get(var3)).container = var3.getInventory();
            ((IsoDeadBody)PlayerToBody.get(var3)).ClothingItem_Torso = var3.getClothingItem_Torso();
            ((IsoDeadBody)PlayerToBody.get(var3)).ClothingItem_Legs = var3.getClothingItem_Legs();
            ((IsoDeadBody)PlayerToBody.get(var3)).ClothingItem_Feet = var3.getClothingItem_Feet();
            var3.setInventory(new ItemContainer());
            int var10 = (int)var3.x;
            int var11 = (int)var3.y;

            for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
               UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
               if (var7.getConnectedGUID() != var1.getConnectedGUID() && var7.ReleventTo((float)var10, (float)var11)) {
                  ByteBufferWriter var8 = var7.startPacket();
                  PacketTypes.doPacket((short)65, var8);
                  var0.rewind();
                  var8.putShort((short)var3.OnlineID);
                  var8.bb.put(var0);
                  var7.endPacketImmediate();
               }
            }

            PlayerToBody.remove(var3);
         }
      }

   }

   public static void loadModData(IsoGridSquare var0) {
      if (var0.getModData().rawget("id") != null && var0.getModData().rawget("id") != null && (var0.getModData().rawget("remove") == null || ((String)var0.getModData().rawget("remove")).equals("false"))) {
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":x", new Double((double)var0.getX()));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":y", new Double((double)var0.getY()));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":z", new Double((double)var0.getZ()));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":typeOfSeed", var0.getModData().rawget("typeOfSeed"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":nbOfGrow", (Double)var0.getModData().rawget("nbOfGrow"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":id", var0.getModData().rawget("id"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":waterLvl", var0.getModData().rawget("waterLvl"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":lastWaterHour", var0.getModData().rawget("lastWaterHour"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":waterNeeded", var0.getModData().rawget("waterNeeded"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":waterNeededMax", var0.getModData().rawget("waterNeededMax"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":mildewLvl", var0.getModData().rawget("mildewLvl"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":aphidLvl", var0.getModData().rawget("aphidLvl"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":fliesLvl", var0.getModData().rawget("fliesLvl"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":fertilizer", var0.getModData().rawget("fertilizer"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":nextGrowing", var0.getModData().rawget("nextGrowing"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":hasVegetable", var0.getModData().rawget("hasVegetable"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":hasSeed", var0.getModData().rawget("hasSeed"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":health", var0.getModData().rawget("health"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":badCare", var0.getModData().rawget("badCare"));
         GameTime.getInstance().getModData().rawset("planting:" + ((Double)var0.getModData().rawget("id")).intValue() + ":state", var0.getModData().rawget("state"));
         if (var0.getModData().rawget("hoursElapsed") != null) {
            GameTime.getInstance().getModData().rawset("hoursElapsed", var0.getModData().rawget("hoursElapsed"));
         }
      }

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         if (var2.ReleventTo((float)var0.getX(), (float)var0.getY())) {
            ByteBufferWriter var3 = var2.startPacket();
            PacketTypes.doPacket((short)51, var3);
            var3.putInt(var0.getX());
            var3.putInt(var0.getY());
            var3.putInt(var0.getZ());

            try {
               var0.getModData().save(var3.bb);
            } catch (IOException var5) {
               var5.printStackTrace();
            }

            var2.endPacketImmediate();
         }
      }

   }

   private static void loadModData(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      IsoGridSquare var5 = ServerMap.instance.getGridSquare(var2, var3, var4);
      if (var5 != null) {
         try {
            var5.getModData().load((ByteBuffer)var0, 143);
            if (var5.getModData().rawget("id") != null && (var5.getModData().rawget("remove") == null || ((String)var5.getModData().rawget("remove")).equals("false"))) {
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":x", new Double((double)var5.getX()));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":y", new Double((double)var5.getY()));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":z", new Double((double)var5.getZ()));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":typeOfSeed", var5.getModData().rawget("typeOfSeed"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":nbOfGrow", (Double)var5.getModData().rawget("nbOfGrow"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":id", var5.getModData().rawget("id"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":waterLvl", var5.getModData().rawget("waterLvl"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":lastWaterHour", var5.getModData().rawget("lastWaterHour"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":waterNeeded", var5.getModData().rawget("waterNeeded"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":waterNeededMax", var5.getModData().rawget("waterNeededMax"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":mildewLvl", var5.getModData().rawget("mildewLvl"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":aphidLvl", var5.getModData().rawget("aphidLvl"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":fliesLvl", var5.getModData().rawget("fliesLvl"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":fertilizer", var5.getModData().rawget("fertilizer"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":nextGrowing", var5.getModData().rawget("nextGrowing"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":hasVegetable", var5.getModData().rawget("hasVegetable"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":hasSeed", var5.getModData().rawget("hasSeed"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":health", var5.getModData().rawget("health"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":badCare", var5.getModData().rawget("badCare"));
               GameTime.getInstance().getModData().rawset("planting:" + ((Double)var5.getModData().rawget("id")).intValue() + ":state", var5.getModData().rawget("state"));
               if (var5.getModData().rawget("hoursElapsed") != null) {
                  GameTime.getInstance().getModData().rawset("hoursElapsed", var5.getModData().rawget("hoursElapsed"));
               }
            }

            LuaEventManager.triggerEvent("onLoadModDataFromServer", var5);

            for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
               UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
               if (var7.ReleventTo((float)var5.getX(), (float)var5.getY()) && (var1 == null || var7.getConnectedGUID() != var1.getConnectedGUID())) {
                  ByteBufferWriter var8 = var7.startPacket();
                  PacketTypes.doPacket((short)51, var8);
                  var8.putInt(var2);
                  var8.putInt(var3);
                  var8.putInt(var4);

                  try {
                     var5.getModData().save(var8.bb);
                  } catch (IOException var10) {
                     var10.printStackTrace();
                  }

                  var7.endPacketImmediate();
               }
            }
         } catch (IOException var11) {
            var11.printStackTrace();
         }

      }
   }

   private static void receiveWeaponHit(ByteBuffer var0, UdpConnection var1) {
      IsoObject var2 = getIsoObjectRefFromByteBuffer(var0);
      short var3 = var0.getShort();
      String var4 = GameWindow.ReadStringUTF(var0);
      IsoPlayer var5 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var3));
      if (var2 != null && var5 != null) {
         InventoryItem var6 = null;
         if (!var4.isEmpty()) {
            var6 = InventoryItemFactory.CreateItem(var4);
            if (var6 == null || !(var6 instanceof HandWeapon)) {
               return;
            }
         }

         if (var6 == null && !(var2 instanceof IsoWindow)) {
            return;
         }

         int var7 = (int)var2.getX();
         int var8 = (int)var2.getY();
         int var9 = (int)var2.getZ();
         if (var2 instanceof IsoDoor) {
            ((IsoDoor)var2).WeaponHit(var5, (HandWeapon)var6);
         } else if (var2 instanceof IsoThumpable) {
            ((IsoThumpable)var2).WeaponHit(var5, (HandWeapon)var6);
         } else if (var2 instanceof IsoWindow) {
            ((IsoWindow)var2).WeaponHit(var5, (HandWeapon)var6);
         } else if (var2 instanceof IsoWoodenWall) {
            ((IsoWoodenWall)var2).WeaponHit(var5, (HandWeapon)var6);
         } else if (var2 instanceof IsoBarricade) {
            ((IsoBarricade)var2).WeaponHit(var5, (HandWeapon)var6);
         }

         if (var2.getObjectIndex() == -1) {
            LoggerManager.getLogger("map").write(var1.idStr + " \"" + var1.username + "\" destroyed " + (var2.getName() != null ? var2.getName() : var2.getObjectName()) + " with " + (var4.isEmpty() ? "BareHands" : var4) + " at " + var7 + "," + var8 + "," + var9);
         }
      }

   }

   private static void putIsoObjectRefToByteBuffer(IsoObject var0, ByteBuffer var1) {
      var1.putInt(var0.square.x);
      var1.putInt(var0.square.y);
      var1.putInt(var0.square.z);
      var1.put((byte)var0.square.getObjects().indexOf(var0));
   }

   private static IsoObject getIsoObjectRefFromByteBuffer(ByteBuffer var0) {
      int var1 = var0.getInt();
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      byte var4 = var0.get();
      IsoGridSquare var5 = ServerMap.instance.getGridSquare(var1, var2, var3);
      return var5 != null && var4 >= 0 && var4 < var5.getObjects().size() ? (IsoObject)var5.getObjects().get(var4) : null;
   }

   private static void drink(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      float var3 = var0.getFloat();
      IsoPlayer var4 = getPlayerFromConnection(var1, var2);
      if (var4 != null) {
         Stats var10000 = var4.getStats();
         var10000.thirst -= var3;
         if (var4.getStats().thirst < 0.0F) {
            var4.getStats().thirst = 0.0F;
         }
      }

   }

   private static void receivePlayerDeath(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      float var3 = var0.getFloat();
      float var4 = var0.getFloat();
      float var5 = var0.getFloat();
      boolean var6 = var0.get() == 1;
      float var7 = var0.getFloat();
      IsoPlayer var8 = getPlayerFromConnection(var1, var2);
      if (var8 != null) {
         ChatServer.getInstance().disconnectPlayer(var8.getOnlineID());
         ServerWorldDatabase.instance.saveTransactionID(var8.username, 0);
         var8.setTransactionID(0);
         transactionIDMap.put(var8.username, 0);
         var8.setX(var3);
         var8.setY(var4);
         var8.setZ(var5);
         var8.getStateMachine().Lock = false;
         var8.setHealth(0.0F);
         var8.getBodyDamage().setOverallBodyHealth(0.0F);
         var8.getBodyDamage().setInfected(var6);
         var8.getBodyDamage().setInfectionLevel(var7);
         var8.getStateMachine().Lock = false;
         var8.getStateMachine().changeState(DieState.instance());
         var8.getStateMachine().Lock = true;
         if (!ServerOptions.instance.Open.getValue() && ServerOptions.instance.DropOffWhiteListAfterDeath.getValue() && var8.accessLevel.equals("")) {
            try {
               ServerWorldDatabase.instance.removeUser(var8.getUsername());
            } catch (SQLException var10) {
            }
         }
      }

   }

   private static void receivePlayerOnBeaten(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      float var3 = var0.getFloat();
      float var4 = var0.getFloat();
      float var5 = var0.getFloat();
      IsoPlayer var6 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var6 != null) {
         SendOnBeaten(var6, var3, var4, var5);
      }

   }

   private static void process(ZomboidNetData var0) {
      ByteBuffer var1 = var0.buffer;
      UdpConnection var2 = udpEngine.getActiveConnection(var0.connection);

      try {
         switch(var0.type) {
         default:
            doZomboidDataInMainLoop(var0);
         }
      } catch (Exception var4) {
         DebugLog.log(DebugType.Network, "Error with packet of type: " + var0.type);
         var4.printStackTrace();
      }
   }

   private static void eatFood(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      float var3 = var0.getFloat();
      String var4 = GameWindow.ReadString(var0);
      byte var5 = var0.get();
      InventoryItem var6 = InventoryItemFactory.CreateItem(var4);

      try {
         var6.load(var0, 143, false);
      } catch (IOException var8) {
         var8.printStackTrace();
      }

      if (var6 instanceof Food) {
         IsoPlayer var7 = getPlayerFromConnection(var1, var2);
         if (var7 != null) {
            var7.Eat(var6, var3);
         }
      }

   }

   private static void doBandage(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         boolean var5 = var0.get() == 1;
         float var6 = var0.getFloat();
         boolean var7 = var0.get() == 1;
         String var8 = GameWindow.ReadStringUTF(var0);
         var3.getBodyDamage().SetBandaged(var4, var5, var6, var7, var8);

         for(int var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
            UdpConnection var10 = (UdpConnection)udpEngine.connections.get(var9);
            if (var10.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var11 = var10.startPacket();
               PacketTypes.doPacket((short)42, var11);
               var11.putShort(var2);
               var11.putInt(var4);
               var11.putBoolean(var5);
               var11.putFloat(var6);
               var11.putBoolean(var7);
               GameWindow.WriteStringUTF(var11.bb, var8);
               var10.endPacketImmediate();
            }
         }
      }

   }

   private static void doStitch(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         boolean var5 = var0.get() == 1;
         float var6 = var0.getFloat();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setStitched(var5);
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setStitchTime(var6);

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)98, var9);
               var9.putShort(var2);
               var9.putInt(var4);
               var9.putBoolean(var5);
               var9.putFloat(var6);
               var8.endPacketImmediate();
            }
         }
      }

   }

   private static void doWoundInfection(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         boolean var5 = var0.get() == 1;
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setInfectedWound(var5);

         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)97, var8);
               var8.putShort(var2);
               var8.putInt(var4);
               var8.putBoolean(var5);
               var7.endPacketImmediate();
            }
         }
      }

   }

   private static void doDisinfect(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         float var5 = var0.getFloat();
         BodyPart var6 = var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4));
         var6.setAlcoholLevel(var6.getAlcoholLevel() + var5);

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)99, var9);
               var9.putShort(var2);
               var9.putInt(var4);
               var9.putFloat(var5);
               var8.endPacketImmediate();
            }
         }
      }

   }

   private static void doSplint(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         boolean var5 = var0.get() == 1;
         String var6 = var5 ? GameWindow.ReadStringUTF(var0) : null;
         float var7 = var5 ? var0.getFloat() : 0.0F;
         BodyPart var8 = var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4));
         var8.setSplint(var5, var7);
         var8.setSplintItem(var6);

         for(int var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
            UdpConnection var10 = (UdpConnection)udpEngine.connections.get(var9);
            if (var10.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var11 = var10.startPacket();
               PacketTypes.doPacket((short)102, var11);
               var11.putShort(var2);
               var11.putInt(var4);
               var11.putBoolean(var5);
               if (var5) {
                  var11.putUTF(var6);
                  var11.putFloat(var7);
               }

               var10.endPacketImmediate();
            }
         }
      }

   }

   private static void doAdditionalPain(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         float var5 = var0.getFloat();
         BodyPart var6 = var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4));
         var6.setAdditionalPain(var6.getAdditionalPain() + var5);

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)100, var9);
               var9.putShort(var2);
               var9.putInt(var4);
               var9.putFloat(var5);
               var8.endPacketImmediate();
            }
         }
      }

   }

   private static void doRemoveGlass(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setHaveGlass(false);

         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var6.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var7 = var6.startPacket();
               PacketTypes.doPacket((short)101, var7);
               var7.putShort(var2);
               var7.putInt(var4);
               var6.endPacketImmediate();
            }
         }
      }

   }

   private static void doRemoveBullet(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         int var5 = var0.getInt();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setHaveBullet(false, var5);

         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)103, var8);
               var8.putShort(var2);
               var8.putInt(var4);
               var8.putInt(var5);
               var7.endPacketImmediate();
            }
         }
      }

   }

   private static void doCleanBurn(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var0.getInt();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setNeedBurnWash(false);

         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var6.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var7 = var6.startPacket();
               PacketTypes.doPacket((short)104, var7);
               var7.putShort(var2);
               var7.putInt(var4);
               var6.endPacketImmediate();
            }
         }
      }

   }

   private static void receiveBodyDamageUpdate(ByteBuffer var0, UdpConnection var1) {
      BodyDamageSync.instance.serverPacket(var0);
   }

   private static void ReceiveCommand(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = null;
      var3 = handleClientCommand(var2.substring(1), var1);
      if (var3 == null) {
         var3 = handleServerCommand(var2.substring(1), var1);
      }

      if (var3 == null) {
         var3 = "Unknown command " + var2;
      }

      if (!var2.substring(1).startsWith("roll") && !var2.substring(1).startsWith("card")) {
         ChatServer.getInstance().sendMessageToServerChat(var1, var3);
      } else {
         ChatServer.getInstance().sendMessageToServerChat(var1, var3);
      }

   }

   private static String handleClientCommand(String var0, UdpConnection var1) {
      if (var0 == null) {
         return null;
      } else {
         ArrayList var2 = new ArrayList();
         Matcher var3 = Pattern.compile("([^\"]\\S*|\".*?\")\\s*").matcher(var0);

         while(var3.find()) {
            var2.add(var3.group(1).replace("\"", ""));
         }

         int var4 = var2.size();
         String[] var5 = (String[])var2.toArray(new String[var4]);
         String var6 = var4 > 0 ? var5[0].toLowerCase() : "";
         if (var6.equals("card")) {
            PlayWorldSoundServer("ChatDrawCard", false, getAnyPlayerFromConnection(var1).getCurrentSquare(), 0.0F, 3.0F, 1.0F, false);
            return var1.username + " drew " + ServerOptions.getRandomCard();
         } else if (var6.equals("roll")) {
            if (var4 != 2) {
               return (String)ServerOptions.clientOptionsList.get("roll");
            } else {
               boolean var13 = false;

               try {
                  int var14 = Integer.parseInt(var5[1]);
                  PlayWorldSoundServer("ChatRollDice", false, getAnyPlayerFromConnection(var1).getCurrentSquare(), 0.0F, 3.0F, 1.0F, false);
                  return var1.username + " rolls a " + var14 + "-sided dice and obtains " + Rand.Next(var14);
               } catch (Exception var10) {
                  return (String)ServerOptions.clientOptionsList.get("roll");
               }
            }
         } else if (var6.equals("changepwd")) {
            if (var4 == 3) {
               String var12 = var5[1];
               String var8 = var5[2];

               try {
                  return ServerWorldDatabase.instance.changePwd(var1.username, var12.trim(), var8.trim());
               } catch (SQLException var11) {
                  var11.printStackTrace();
                  return "A SQL error occured";
               }
            } else {
               return (String)ServerOptions.clientOptionsList.get("changepwd");
            }
         } else if (var6.equals("dragons")) {
            return "Sorry, you don't have the required materials.";
         } else if (var6.equals("tooks")) {
            return "No pants hug !";
         } else if (var6.equals("robomat")) {
            return "This guy is not really a dentist...";
         } else if (var6.equals("sirtwiggy")) {
            return "http://www.twitch.tv/sirtwiggy BEST STREAM EVER !";
         } else if (var6.equals("eckyman")) {
            return "http://www.youtube.com/user/MrEckyman Cool cat !";
         } else if (var6.equals("dance")) {
            return "Stop kidding me...";
         } else if (var6.equals("safehouse")) {
            if (var4 == 2 && var1 != null) {
               if (!ServerOptions.instance.PlayerSafehouse.getValue() && !ServerOptions.instance.AdminSafehouse.getValue()) {
                  return "Safehouses are disabled on this server.";
               } else if ("release".equals(var5[1])) {
                  SafeHouse var7 = SafeHouse.hasSafehouse(var1.username);
                  if (var7 == null) {
                     return "You don't own a safehouse.";
                  } else if (!ServerOptions.instance.PlayerSafehouse.getValue() && !"admin".equals(var1.accessLevel) && !"moderator".equals(var1.accessLevel)) {
                     return "Only admin or moderator may release safehouses";
                  } else {
                     var7.removeSafeHouse((IsoPlayer)null);
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

   public static void Chat(String var0, UdpConnection var1, boolean var2) {
      Chat(var0, var1, var2, (byte)-1);
   }

   public static void Chat(String var0, UdpConnection var1, boolean var2, byte var3) {
      IsoPlayer var4 = null;
      if (var1 != null) {
         var4 = getAnyPlayerFromConnection(var1);
         if (var4 == null) {
            return;
         }
      }

      if (var1 == null || !var1.accessLevel.equals("") && !var1.accessLevel.equals("Observer") && !var1.accessLevel.equals("GM") || !var0.startsWith("[SERVERMSG]")) {
         if (var4 != null && !var0.startsWith("[SERVERMSG]")) {
            if (var3 == 0) {
               var4.Say(var0);
            } else if (var3 == 1) {
               var4.SayWhisper(var0);
            } else if (var3 == 2) {
               var4.SayShout(var0);
            }
         }

         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            if (var1 == null || var1 != null && var6.getConnectedGUID() != var1.getConnectedGUID() && var4 != null && var6.ReleventTo(var4.x, var4.y)) {
               ByteBufferWriter var7 = var6.startPacket();
               PacketTypes.doPacket((short)186, var7);
               var7.putInt(var4 != null ? var4.OnlineID : -1);
               var7.putByte(var3);
               var7.putUTF(var0);
               var7.putByte((byte)(var2 ? 1 : 0));
               var6.endPacketImmediate();
            }
         }

         if (!var0.equals("ZzzZZZzzzz") && !ZomboidRadio.isStaticSound(var0)) {
            LoggerManager.getLogger("chat").write((var1 == null ? "" : var1.idStr + " \"" + var1.username) + "\": \"" + var0 + "\"");
         }

      }
   }

   private static void Chat(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      String var3 = GameWindow.ReadString(var0);
      if (var3.length() > 256) {
         var3 = var3.substring(0, 256);
      }

      Chat(var3, var1, true, var2);
   }

   public static void doZomboidDataInMainLoop(ZomboidNetData var0) {
      synchronized(MainLoopNetData) {
         MainLoopNetData.add(var0);
      }
   }

   private static void hitZombie(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      int var3 = var0.getInt();
      short var4 = var0.getShort();
      String var5 = GameWindow.ReadString(var0);
      float var6 = var0.getFloat();
      boolean var7 = var0.get() == 1;
      boolean var8 = var0.get() == 1;
      float var9 = var0.getFloat();
      float var10 = var0.getFloat();
      float var11 = var0.getFloat();
      float var12 = var0.getFloat();
      float var13 = var0.getFloat();
      float var14 = var0.getFloat();
      float var15 = var0.getFloat();
      float var16 = var0.getFloat();
      float var17 = var0.getFloat();
      float var18 = var0.getFloat();
      float var19 = var0.getFloat();
      InventoryItem var20 = InventoryItemFactory.CreateItem(var5);
      Object var21 = null;
      BaseVehicle var22 = null;
      if (var3 == 1) {
         var21 = ServerMap.instance.ZombieMap.get(var4);
      } else if (var3 == 2) {
         var21 = (IsoGameCharacter)IDToPlayerMap.get(Integer.valueOf(var4));
      } else if (var3 == 3) {
         var22 = VehicleManager.instance.getVehicleByID(var4);
      }

      IsoPlayer var23 = getPlayerFromConnection(var1, var2);
      var23.useChargeDelta = var19;
      if (var21 == null && var22 == null) {
         DebugLog.log(var23.username + " hit non-existent " + (var3 == 1 ? "zombie" : (var3 == 2 ? "player" : "vehicle")) + " id=" + var4);
      } else {
         if (var3 == 1) {
            IsoZombie var24 = (IsoZombie)var21;
            if (var24.getStateMachine().getCurrent() == StaggerBackDieState.instance() && var24.getReanimPhase() == 1) {
               var24.setReanimateTimer((float)(Rand.Next(60) + 30));
            }

            if (var24.getStateMachine().getCurrent() == StaggerBackDieState.instance() && var24.getReanimPhase() == 2 && !var24.isFakeDead()) {
               float var25 = 15.0F - ((IsoGameCharacter)var21).def.Frame;
               if (var25 < 2.0F) {
                  var25 = 2.0F;
               }

               var24.setReanimPhase(1);
               var24.PlayAnimUnlooped("ZombieDeath");
               ((IsoGameCharacter)var21).def.Frame = var25;
               var24.setReanimateTimer((float)(Rand.Next(60) + 30));
            }
         }

         if (var3 == 2) {
            LoggerManager.getLogger("pvp").write("user " + var23.username + " " + LoggerManager.getPlayerCoords(var23) + " hit user " + ((IsoPlayer)var21).username + " " + LoggerManager.getPlayerCoords((IsoPlayer)var21) + " with " + var5);
         }

         if (var22 == null) {
            DebugLog.log(DebugType.Combat, "player " + var23.username + " hit " + (var3 == 1 ? "zombie " + var4 : "player " + ((IsoPlayer)var21).username) + " health=" + ((IsoGameCharacter)var21).getHealth() + (var7 ? " for no dmg" : " for dmg " + var6));
            ((IsoGameCharacter)var21).setHitForce(var16);
            ((IsoGameCharacter)var21).getHitDir().x = var17;
            ((IsoGameCharacter)var21).getHitDir().y = var18;
            ((IsoGameCharacter)var21).setX(var10);
            ((IsoGameCharacter)var21).setY(var11);
            ((IsoGameCharacter)var21).setCloseKilled(var8);
            ((IsoGameCharacter)var21).Hit((HandWeapon)var20, var23, var6, var7, var9);
         } else {
            var22.hitVehicle(var23, (HandWeapon)var20);
         }

      }
   }

   private static void equip(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      byte var3 = var0.get();
      byte var4 = var0.get();
      InventoryItem var5 = null;
      if (var4 == 1) {
         String var6 = GameWindow.ReadString(var0);
         var5 = InventoryItemFactory.CreateItem(var6);
         if (var5 == null) {
            LoggerManager.getLogger("user").write(var1.idStr + " equipped unknown item type \"" + var6 + "\"");
            return;
         }

         byte var7 = var0.get();

         try {
            var5.load(var0, 143, false);
         } catch (IOException var13) {
            var13.printStackTrace();
            return;
         }
      }

      IsoPlayer var14 = getPlayerFromConnection(var1, var2);
      if (var14 != null) {
         if (var3 == 0) {
            var14.setPrimaryHandItem(var5);
         } else {
            var14.setSecondaryHandItem(var5);
         }
      }

      if (var14 != null) {
         for(int var15 = 0; var15 < udpEngine.connections.size(); ++var15) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var15);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var9 = getAnyPlayerFromConnection(var8);
               if (var9 != null) {
                  ByteBufferWriter var10 = var8.startPacket();
                  PacketTypes.doPacket((short)25, var10);
                  var10.putByte(var3);
                  var10.putByte(var4);
                  var10.putInt(var14.OnlineID);
                  if (var4 == 1) {
                     try {
                        var5.save(var10.bb, false);
                     } catch (IOException var12) {
                        var12.printStackTrace();
                     }
                  }

                  var8.endPacketImmediate();
               }
            }
         }

      }
   }

   private static void scoreboard(UdpConnection var0) {
      ByteBufferWriter var1 = var0.startPacket();
      PacketTypes.doPacket((short)50, var1);
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();

      int var5;
      for(var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
         UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);

         for(int var7 = 0; var7 < 4; ++var7) {
            if (var6.usernames[var7] != null) {
               var2.add(var6.usernames[var7]);
               IsoPlayer var8 = getPlayerByRealUserName(var6.usernames[var7]);
               if (var8 != null) {
                  var3.add(var8.getDisplayName());
               } else {
                  String var9 = ServerWorldDatabase.instance.getDisplayName(var6.usernames[var7]);
                  var3.add(var9 == null ? var6.usernames[var7] : var9);
               }

               if (SteamUtils.isSteamModeEnabled()) {
                  var4.add(var6.steamID);
               }
            }
         }
      }

      var1.putInt(var2.size());

      for(var5 = 0; var5 < var2.size(); ++var5) {
         var1.putUTF((String)var2.get(var5));
         var1.putUTF((String)var3.get(var5));
         if (SteamUtils.isSteamModeEnabled()) {
            var1.putLong((Long)var4.get(var5));
         }
      }

      var0.endPacketImmediate();
   }

   private static void receiveWorldSound(ByteBuffer var0) {
      int var1 = var0.getInt();
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      boolean var6 = var0.get() == 1;
      float var7 = var0.getFloat();
      float var8 = var0.getFloat();
      boolean var9 = var0.get() == 1;
      DebugLog.log(DebugType.Sound, "worldsound: received at " + var1 + "," + var2 + "," + var3 + " radius=" + var4);
      WorldSoundManager.instance.addSound(var9, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private static void receiveSound(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      boolean var6 = var0.get() == 1;
      if (var2 != null && !var2.isEmpty()) {
         int var7 = 70;
         GameSound var8 = GameSounds.getSound(var2);
         int var9;
         if (var8 != null) {
            for(var9 = 0; var9 < var8.clips.size(); ++var9) {
               GameSoundClip var10 = (GameSoundClip)var8.clips.get(var9);
               if (var10.hasMaxDistance()) {
                  var7 = Math.max(var7, (int)var10.distanceMax);
               }
            }
         }

         for(var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
            UdpConnection var13 = (UdpConnection)udpEngine.connections.get(var9);
            if (var13.getConnectedGUID() != var1.getConnectedGUID() && var13.isFullyConnected()) {
               IsoPlayer var11 = getAnyPlayerFromConnection(var13);
               if (var11 != null && var13.RelevantTo((float)var3, (float)var4, (float)var7)) {
                  ByteBufferWriter var12 = var13.startPacket();
                  PacketTypes.doPacket((short)53, var12);
                  var12.putUTF(var2);
                  var12.putInt(var3);
                  var12.putInt(var4);
                  var12.putInt(var5);
                  var12.putByte((byte)(var6 ? 1 : 0));
                  var13.endPacketImmediate();
               }
            }
         }

      }
   }

   private static void PlayWorldSound(String var0, boolean var1, IsoGridSquare var2, float var3, float var4, float var5, boolean var6, boolean var7) {
      if (bServer && var2 != null) {
         int var8 = var2.getX();
         int var9 = var2.getY();
         int var10 = var2.getZ();
         DebugLog.log(DebugType.Sound, "sound: sending " + var0 + " at " + var8 + "," + var9 + "," + var10 + " radius=" + var4);

         for(int var11 = 0; var11 < udpEngine.connections.size(); ++var11) {
            UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var11);
            IsoPlayer var13 = getAnyPlayerFromConnection(var12);
            if (var13 != null && var12.RelevantTo((float)var8, (float)var9, var4 * 2.0F)) {
               ByteBufferWriter var14 = var12.startPacket();
               PacketTypes.doPacket((short)53, var14);
               var14.putUTF(var0);
               var14.putInt(var8);
               var14.putInt(var9);
               var14.putInt(var10);
               var14.putByte((byte)(var1 ? 1 : 0));
               var14.putByte((byte)1);
               var12.endPacketImmediate();
            }
         }

      }
   }

   public static void PlayWorldSoundServer(String var0, boolean var1, IsoGridSquare var2, float var3, float var4, float var5, boolean var6) {
      PlayWorldSound(var0, var1, var2, var3, var4, var5, var6, false);
   }

   public static void PlayWorldSoundWavServer(String var0, boolean var1, IsoGridSquare var2, float var3, float var4, float var5, boolean var6) {
      PlayWorldSound(var0, var1, var2, var3, var4, var5, var6, true);
   }

   public static void PlaySoundAtEveryPlayer(String var0, int var1, int var2, int var3) {
      PlaySoundAtEveryPlayer(var0, var1, var2, var3, false);
   }

   public static void PlaySoundAtEveryPlayer(String var0) {
      PlaySoundAtEveryPlayer(var0, -1, -1, -1, true);
   }

   public static void PlaySoundAtEveryPlayer(String var0, int var1, int var2, int var3, boolean var4) {
      if (bServer) {
         if (var4) {
            DebugLog.log(DebugType.Sound, "sound: sending " + var0 + " at every player (using player location)");
         } else {
            DebugLog.log(DebugType.Sound, "sound: sending " + var0 + " at every player location x=" + var1 + " y=" + var2);
         }

         for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
            UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
            IsoPlayer var7 = getAnyPlayerFromConnection(var6);
            if (var7 != null && !var7.isDeaf()) {
               if (var4) {
                  var1 = (int)var7.getX();
                  var2 = (int)var7.getY();
                  var3 = (int)var7.getZ();
               }

               ByteBufferWriter var8 = var6.startPacket();
               PacketTypes.doPacket((short)119, var8);
               var8.putUTF(var0);
               var8.putInt(var1);
               var8.putInt(var2);
               var8.putInt(var3);
               var6.endPacketImmediate();
            }
         }

      }
   }

   public static void sendZombieSound(IsoZombie.ZombieSound var0, IsoZombie var1) {
      float var2 = (float)var0.radius();
      DebugLog.log(DebugType.Sound, "sound: sending zombie sound " + var0);

      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var4.isFullyConnected() && var4.RelevantTo(var1.getX(), var1.getY(), var2)) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)61, var5);
            var5.putShort(var1.OnlineID);
            var5.putByte((byte)var0.ordinal());
            var4.endPacketImmediate();
         }
      }

   }

   private static void receiveClothing(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      byte var3 = var0.get();
      byte var4 = var0.get();
      InventoryItem var5 = null;
      if (var4 == 1) {
         String var6 = GameWindow.ReadString(var0);
         byte var7 = var0.get();
         var5 = InventoryItemFactory.CreateItem(var6);
         if (var5 == null) {
            return;
         }

         try {
            var5.load(var0, 143, false);
         } catch (IOException var13) {
            var13.printStackTrace();
            return;
         }
      }

      IsoPlayer var14 = getPlayerFromConnection(var1, var2);
      if (var14 != null) {
         if (var3 == Item.ClothingBodyLocation.Bottoms.ordinal()) {
            var14.setClothingItem_Legs(var5);
         }

         if (var3 == Item.ClothingBodyLocation.Shoes.ordinal()) {
            var14.setClothingItem_Feet(var5);
         }

         if (var3 == Item.ClothingBodyLocation.Top.ordinal()) {
            var14.setClothingItem_Torso(var5);
         }

         for(int var15 = 0; var15 < udpEngine.connections.size(); ++var15) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var15);
            if (var8.getConnectedGUID() != var1.getConnectedGUID()) {
               IsoPlayer var9 = getAnyPlayerFromConnection(var1);
               if (var9 != null) {
                  ByteBufferWriter var10 = var8.startPacket();
                  PacketTypes.doPacket((short)56, var10);
                  var10.putShort((short)var14.OnlineID);
                  var10.putByte(var3);
                  var10.putByte(var4);
                  if (var4 == 1) {
                     try {
                        var5.save(var10.bb, false);
                     } catch (IOException var12) {
                        var12.printStackTrace();
                     }
                  }

                  var8.endPacketImmediate();
               }
            }
         }

      }
   }

   public static void initClientCommandFilter() {
      String var0 = ServerOptions.getInstance().ClientCommandFilter.getValue();
      ccFilters.clear();
      String[] var1 = var0.split(";");
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (!var5.isEmpty() && var5.contains(".") && (var5.startsWith("+") || var5.startsWith("-"))) {
            String[] var6 = var5.split("\\.");
            if (var6.length == 2) {
               String var7 = var6[0].substring(1);
               String var8 = var6[1];
               GameServer.CCFilter var9 = new GameServer.CCFilter();
               var9.command = var8;
               var9.allow = var6[0].startsWith("+");
               var9.next = (GameServer.CCFilter)ccFilters.get(var7);
               ccFilters.put(var7, var9);
            }
         }
      }

   }

   private static void receiveClientCommand(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      String var3 = GameWindow.ReadString(var0);
      String var4 = GameWindow.ReadString(var0);
      boolean var5 = var0.get() == 1;
      KahluaTable var6 = null;
      if (var5) {
         var6 = LuaManager.platform.newTable();

         try {
            TableNetworkUtils.load(var6, var0);
         } catch (Exception var9) {
            var9.printStackTrace();
            return;
         }
      }

      IsoPlayer var7 = getPlayerFromConnection(var1, var2);
      if (var2 == -1) {
         var7 = getAnyPlayerFromConnection(var1);
      }

      if (var7 == null) {
         DebugLog.log("receiveClientCommand: player is null");
      } else {
         GameServer.CCFilter var8 = (GameServer.CCFilter)ccFilters.get(var3);
         if (var8 == null || var8.passes(var4)) {
            LoggerManager.getLogger("cmd").write(var1.idStr + " \"" + var7.username + "\" " + var3 + "." + var4 + " @ " + (int)var7.getX() + "," + (int)var7.getY() + "," + (int)var7.getZ());
         }

         if (!SGlobalObjects.receiveClientCommand(var3, var4, var7, var6)) {
            LuaEventManager.triggerEvent("OnClientCommand", var3, var4, var7, var6);
         }
      }
   }

   public static IsoPlayer getAnyPlayerFromConnection(UdpConnection var0) {
      for(int var1 = 0; var1 < 4; ++var1) {
         if (var0.players[var1] != null) {
            return var0.players[var1];
         }
      }

      return null;
   }

   private static IsoPlayer getPlayerFromConnection(UdpConnection var0, int var1) {
      return var1 >= 0 && var1 < 4 ? var0.players[var1] : null;
   }

   public static IsoPlayer getPlayerByRealUserName(String var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);

         for(int var3 = 0; var3 < 4; ++var3) {
            IsoPlayer var4 = var2.players[var3];
            if (var4 != null && var4.username.equals(var0)) {
               return var4;
            }
         }
      }

      return null;
   }

   public static IsoPlayer getPlayerByUserName(String var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);

         for(int var3 = 0; var3 < 4; ++var3) {
            IsoPlayer var4 = var2.players[var3];
            if (var4 != null && (var4.getDisplayName().equals(var0) || var4.getUsername().equals(var0))) {
               return var4;
            }
         }
      }

      return null;
   }

   public static IsoPlayer getPlayerByUserNameForCommand(String var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);

         for(int var3 = 0; var3 < 4; ++var3) {
            IsoPlayer var4 = var2.players[var3];
            if (var4 != null && (var4.getDisplayName().toLowerCase().equals(var0.toLowerCase()) || var4.getDisplayName().toLowerCase().startsWith(var0.toLowerCase()))) {
               return var4;
            }
         }
      }

      return null;
   }

   public static UdpConnection getConnectionByPlayerOnlineID(Integer var0) {
      return udpEngine.getActiveConnection((Long)IDToAddressMap.get(var0));
   }

   public static UdpConnection getConnectionFromPlayer(IsoPlayer var0) {
      Long var1 = (Long)PlayerToAddressMap.get(var0);
      return var1 == null ? null : udpEngine.getActiveConnection(var1);
   }

   private static void removeBlood(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      boolean var5 = var0.get() == 1;
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var6 != null) {
         var6.removeBlood(false, var5);

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8 != var1 && var8.ReleventTo((float)var2, (float)var3)) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)109, var9);
               var9.putInt(var2);
               var9.putInt(var3);
               var9.putInt(var4);
               var9.putBoolean(var5);
               var8.endPacketImmediate();
            }
         }

      }
   }

   public static void sendAddItemToContainer(ItemContainer var0, InventoryItem var1) {
      Object var2 = var0.getParent();
      if (var0.getContainingItem() != null && var0.getContainingItem().getWorldItem() != null) {
         var2 = var0.getContainingItem().getWorldItem();
      }

      IsoGridSquare var3 = ((IsoObject)var2).getSquare();

      for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
         UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
         if (var5.ReleventTo((float)var3.x, (float)var3.y)) {
            ByteBufferWriter var6 = var5.startPacket();
            PacketTypes.doPacket((short)20, var6);
            if (var2 instanceof IsoDeadBody) {
               var6.putShort((short)0);
               var6.putInt(((IsoObject)var2).square.getX());
               var6.putInt(((IsoObject)var2).square.getY());
               var6.putInt(((IsoObject)var2).square.getZ());
               var6.putByte((byte)((IsoObject)var2).getStaticMovingObjectIndex());
            } else if (var2 instanceof IsoWorldInventoryObject) {
               var6.putShort((short)1);
               var6.putInt(((IsoObject)var2).square.getX());
               var6.putInt(((IsoObject)var2).square.getY());
               var6.putInt(((IsoObject)var2).square.getZ());
               var6.putLong(((IsoWorldInventoryObject)var2).getItem().id);
            } else if (var2 instanceof BaseVehicle) {
               var6.putShort((short)3);
               var6.putInt(((IsoObject)var2).square.getX());
               var6.putInt(((IsoObject)var2).square.getY());
               var6.putInt(((IsoObject)var2).square.getZ());
               var6.putShort(((BaseVehicle)var2).VehicleID);
               var6.putByte((byte)var0.vehiclePart.getIndex());
            } else {
               var6.putShort((short)2);
               var6.putInt(((IsoObject)var2).square.getX());
               var6.putInt(((IsoObject)var2).square.getY());
               var6.putInt(((IsoObject)var2).square.getZ());
               var6.putByte((byte)((IsoObject)var2).square.getObjects().indexOf(var2));
               var6.putByte((byte)((IsoObject)var2).getContainerIndex(var0));
            }

            try {
               CompressIdenticalItems.save(var6.bb, var1);
            } catch (Exception var8) {
               var8.printStackTrace();
            }

            var5.endPacket();
         }
      }

   }

   public static void sendRemoveItemFromContainer(ItemContainer var0, InventoryItem var1) {
      Object var2 = var0.getParent();
      if (var0.getContainingItem() != null && var0.getContainingItem().getWorldItem() != null) {
         var2 = var0.getContainingItem().getWorldItem();
      }

      if (var2 == null) {
         DebugLog.log("sendRemoveItemFromContainer: o is null");
      } else {
         IsoGridSquare var3 = ((IsoObject)var2).getSquare();
         if (var3 == null) {
            DebugLog.log("sendRemoveItemFromContainer: square is null");
         } else {
            for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
               UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
               if (var5.ReleventTo((float)var3.x, (float)var3.y)) {
                  ByteBufferWriter var6 = var5.startPacket();
                  PacketTypes.doPacket((short)22, var6);
                  if (var2 instanceof IsoDeadBody) {
                     var6.putShort((short)0);
                     var6.putInt(((IsoObject)var2).square.getX());
                     var6.putInt(((IsoObject)var2).square.getY());
                     var6.putInt(((IsoObject)var2).square.getZ());
                     var6.putByte((byte)((IsoObject)var2).getStaticMovingObjectIndex());
                     var6.putInt(1);
                     var6.putLong(var1.id);
                  } else if (var2 instanceof IsoWorldInventoryObject) {
                     var6.putShort((short)1);
                     var6.putInt(((IsoObject)var2).square.getX());
                     var6.putInt(((IsoObject)var2).square.getY());
                     var6.putInt(((IsoObject)var2).square.getZ());
                     var6.putLong(((IsoWorldInventoryObject)var2).getItem().id);
                     var6.putInt(1);
                     var6.putLong(var1.id);
                  } else if (var2 instanceof BaseVehicle) {
                     var6.putShort((short)3);
                     var6.putInt(((IsoObject)var2).square.getX());
                     var6.putInt(((IsoObject)var2).square.getY());
                     var6.putInt(((IsoObject)var2).square.getZ());
                     var6.putShort(((BaseVehicle)var2).VehicleID);
                     var6.putByte((byte)var0.vehiclePart.getIndex());
                     var6.putInt(1);
                     var6.putLong(var1.id);
                  } else {
                     var6.putShort((short)2);
                     var6.putInt(((IsoObject)var2).square.getX());
                     var6.putInt(((IsoObject)var2).square.getY());
                     var6.putInt(((IsoObject)var2).square.getZ());
                     var6.putByte((byte)((IsoObject)var2).square.getObjects().indexOf(var2));
                     var6.putByte((byte)((IsoObject)var2).getContainerIndex(var0));
                     var6.putInt(1);
                     var6.putLong(var1.id);
                  }

                  var5.endPacketImmediate();
               }
            }

         }
      }
   }

   private static void removeItemFromContainer(ByteBuffer var0, UdpConnection var1) {
      alreadyRemoved.clear();
      ByteBufferReader var2 = new ByteBufferReader(var0);
      short var3 = var2.getShort();
      int var4 = var2.getInt();
      int var5 = var2.getInt();
      int var6 = var2.getInt();
      IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var4, var5, var6);
      HashSet var8 = new HashSet();
      boolean var9 = false;
      int var10 = 0;
      byte var11;
      if (var3 == 0) {
         var11 = var2.getByte();
         var10 = var0.getInt();
         if (var7 != null && var11 >= 0 && var11 < var7.getStaticMovingObjects().size()) {
            IsoObject var12 = (IsoObject)var7.getStaticMovingObjects().get(var11);
            if (var12 != null && var12.getContainer() != null) {
               for(int var13 = 0; var13 < var10; ++var13) {
                  long var14 = var2.getLong();
                  InventoryItem var16 = var12.getContainer().getItemWithID(var14);
                  if (var16 == null) {
                     alreadyRemoved.add(var14);
                  } else {
                     var12.getContainer().Remove(var16);
                     var9 = true;
                     var8.add(var16.getFullType());
                  }
               }

               var12.getContainer().setExplored(true);
               var12.getContainer().setHasBeenLooted(true);
            }
         }
      } else if (var3 == 1) {
         if (var7 != null) {
            long var20 = var2.getLong();
            var10 = var0.getInt();
            ItemContainer var27 = null;

            int var28;
            for(var28 = 0; var28 < var7.getWorldObjects().size(); ++var28) {
               IsoWorldInventoryObject var15 = (IsoWorldInventoryObject)var7.getWorldObjects().get(var28);
               if (var15 != null && var15.getItem() instanceof InventoryContainer && var15.getItem().id == var20) {
                  var27 = ((InventoryContainer)var15.getItem()).getInventory();
                  break;
               }
            }

            if (var27 != null) {
               for(var28 = 0; var28 < var10; ++var28) {
                  long var32 = var2.getLong();
                  InventoryItem var17 = var27.getItemWithID(var32);
                  if (var17 == null) {
                     alreadyRemoved.add(var32);
                  } else {
                     var27.Remove(var17);
                     var8.add(var17.getFullType());
                  }
               }

               var27.setExplored(true);
               var27.setHasBeenLooted(true);
            }
         }
      } else {
         byte var22;
         if (var3 == 2) {
            var11 = var2.getByte();
            var22 = var2.getByte();
            var10 = var0.getInt();
            if (var7 != null && var11 >= 0 && var11 < var7.getObjects().size()) {
               IsoObject var29 = (IsoObject)var7.getObjects().get(var11);
               ItemContainer var33 = var29 != null ? var29.getContainerByIndex(var22) : null;
               if (var33 != null) {
                  for(int var34 = 0; var34 < var10; ++var34) {
                     long var37 = var2.getLong();
                     InventoryItem var18 = var33.getItemWithID(var37);
                     if (var18 == null) {
                        alreadyRemoved.add(var37);
                     } else {
                        var33.Remove(var18);
                        var33.setExplored(true);
                        var33.setHasBeenLooted(true);
                        var9 = true;
                        var8.add(var18.getFullType());
                     }
                  }

                  LuaManager.updateOverlaySprite(var29);
               }
            }
         } else if (var3 == 3) {
            short var21 = var2.getShort();
            var22 = var2.getByte();
            var10 = var0.getInt();
            BaseVehicle var30 = VehicleManager.instance.getVehicleByID(var21);
            if (var30 != null) {
               VehiclePart var35 = var30 == null ? null : var30.getPartByIndex(var22);
               ItemContainer var36 = var35 == null ? null : var35.getItemContainer();
               if (var36 != null) {
                  for(int var38 = 0; var38 < var10; ++var38) {
                     long var39 = var2.getLong();
                     InventoryItem var19 = var36.getItemWithID(var39);
                     if (var19 == null) {
                        alreadyRemoved.add(var39);
                     } else {
                        var36.Remove(var19);
                        var36.setExplored(true);
                        var36.setHasBeenLooted(true);
                        var9 = true;
                        var8.add(var19.getFullType());
                     }
                  }
               }
            }
         }
      }

      if (var9) {
         var7.revisionUp();
      }

      for(int var23 = 0; var23 < udpEngine.connections.size(); ++var23) {
         UdpConnection var24 = (UdpConnection)udpEngine.connections.get(var23);
         if (var24.getConnectedGUID() != var1.getConnectedGUID() && var24.ReleventTo((float)var7.x, (float)var7.y)) {
            var0.rewind();
            ByteBufferWriter var31 = var24.startPacket();
            PacketTypes.doPacket((short)22, var31);
            var31.bb.put(var0);
            var24.endPacketUnordered();
         }
      }

      if (!alreadyRemoved.isEmpty()) {
         ByteBufferWriter var25 = var1.startPacket();
         PacketTypes.doPacket((short)49, var25);
         var25.putInt(alreadyRemoved.size());

         for(int var26 = 0; var26 < alreadyRemoved.size(); ++var26) {
            var25.putLong((Long)alreadyRemoved.get(var26));
         }

         var1.endPacketUnordered();
      }

      alreadyRemoved.clear();
      LoggerManager.getLogger("item").write(var1.idStr + " \"" + var1.username + "\" container -" + var10 + " " + var4 + "," + var5 + "," + var6 + " " + var8.toString());
   }

   private static void readItemStats(ByteBuffer var0, InventoryItem var1) {
      int var2 = var0.getInt();
      float var3 = var0.getFloat();
      boolean var4 = var0.get() == 1;
      var1.setUses(var2);
      if (var1 instanceof DrainableComboItem) {
         ((DrainableComboItem)var1).setDelta(var3);
         ((DrainableComboItem)var1).updateWeight();
      }

      if (var4 && var1 instanceof Food) {
         Food var5 = (Food)var1;
         var5.setHungChange(var0.getFloat());
         var5.setCalories(var0.getFloat());
         var5.setCarbohydrates(var0.getFloat());
         var5.setLipids(var0.getFloat());
         var5.setProteins(var0.getFloat());
         var5.setThirstChange(var0.getFloat());
         var5.setFluReduction(var0.getInt());
         var5.setPainReduction(var0.getFloat());
         var5.setEndChange(var0.getFloat());
         var5.setReduceFoodSickness(var0.getInt());
         var5.setStressChange(var0.getFloat());
         var5.setFatigueChange(var0.getFloat());
      }

   }

   private static void receiveItemStats(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, var5);
      byte var8;
      long var9;
      byte var15;
      ItemContainer var24;
      InventoryItem var26;
      switch(var2) {
      case 0:
         var15 = var0.get();
         long var18 = var0.getLong();
         if (var6 != null && var15 >= 0 && var15 < var6.getStaticMovingObjects().size()) {
            IsoMovingObject var21 = (IsoMovingObject)var6.getStaticMovingObjects().get(var15);
            var24 = var21.getContainer();
            if (var24 != null) {
               var26 = var24.getItemWithID(var18);
               if (var26 != null) {
                  readItemStats(var0, var26);
               }
            }
         }
         break;
      case 1:
         long var16 = var0.getLong();
         if (var6 != null) {
            for(int var20 = 0; var20 < var6.getWorldObjects().size(); ++var20) {
               IsoWorldInventoryObject var10 = (IsoWorldInventoryObject)var6.getWorldObjects().get(var20);
               if (var10.getItem() != null && var10.getItem().id == var16) {
                  readItemStats(var0, var10.getItem());
                  break;
               }

               if (var10.getItem() instanceof InventoryContainer) {
                  var24 = ((InventoryContainer)var10.getItem()).getInventory();
                  var26 = var24.getItemWithID(var16);
                  if (var26 != null) {
                     readItemStats(var0, var26);
                     break;
                  }
               }
            }
         }
         break;
      case 2:
         var15 = var0.get();
         var8 = var0.get();
         var9 = var0.getLong();
         if (var6 != null && var15 >= 0 && var15 < var6.getObjects().size()) {
            IsoObject var23 = (IsoObject)var6.getObjects().get(var15);
            ItemContainer var25 = var23.getContainerByIndex(var8);
            if (var25 != null) {
               InventoryItem var27 = var25.getItemWithID(var9);
               if (var27 != null) {
                  readItemStats(var0, var27);
               }
            }
         }
         break;
      case 3:
         short var7 = var0.getShort();
         var8 = var0.get();
         var9 = var0.getLong();
         BaseVehicle var11 = VehicleManager.instance.getVehicleByID(var7);
         if (var11 != null) {
            VehiclePart var12 = var11.getPartByIndex(var8);
            if (var12 != null) {
               ItemContainer var13 = var12.getItemContainer();
               if (var13 != null) {
                  InventoryItem var14 = var13.getItemWithID(var9);
                  if (var14 != null) {
                     readItemStats(var0, var14);
                  }
               }
            }
         }
      }

      for(int var17 = 0; var17 < udpEngine.connections.size(); ++var17) {
         UdpConnection var19 = (UdpConnection)udpEngine.connections.get(var17);
         if (var19 != var1 && var19.ReleventTo((float)var3, (float)var4)) {
            ByteBufferWriter var22 = var19.startPacket();
            PacketTypes.doPacket((short)35, var22);
            var0.rewind();
            var22.bb.put(var0);
            var19.endPacket();
         }
      }

   }

   private static void requestItemsForContainer(ByteBuffer var0, UdpConnection var1) {
      ByteBufferReader var2 = new ByteBufferReader(var0);
      short var3 = var0.getShort();
      String var4 = GameWindow.ReadString(var0);
      String var5 = GameWindow.ReadString(var0);
      int var6 = var2.getInt();
      int var7 = var2.getInt();
      int var8 = var2.getInt();
      short var9 = var2.getShort();
      byte var10 = -1;
      byte var11 = -1;
      long var12 = 0L;
      short var14 = 0;
      IsoGridSquare var15 = IsoWorld.instance.CurrentCell.getGridSquare(var6, var7, var8);
      Object var16 = null;
      ItemContainer var17 = null;
      int var24;
      if (var9 == 2) {
         var10 = var2.getByte();
         var11 = var2.getByte();
         if (var15 != null && var10 >= 0 && var10 < var15.getObjects().size()) {
            var16 = (IsoObject)var15.getObjects().get(var10);
            if (var16 != null) {
               var17 = ((IsoObject)var16).getContainerByIndex(var11);
               if (var17 == null || var17.isExplored()) {
                  return;
               }
            }
         }
      } else if (var9 == 3) {
         var14 = var2.getShort();
         var11 = var2.getByte();
         var16 = VehicleManager.instance.getVehicleByID(var14);
         if (var16 != null) {
            VehiclePart var18 = ((BaseVehicle)var16).getPartByIndex(var11);
            var17 = var18 == null ? null : var18.getItemContainer();
            if (var17 == null || var17.isExplored()) {
               return;
            }
         }
      } else if (var9 == 1) {
         var12 = var2.getLong();

         for(var24 = 0; var24 < var15.getWorldObjects().size(); ++var24) {
            IsoWorldInventoryObject var19 = (IsoWorldInventoryObject)var15.getWorldObjects().get(var24);
            if (var19 != null && var19.getItem() instanceof InventoryContainer && var19.getItem().id == var12) {
               var17 = ((InventoryContainer)var19.getItem()).getInventory();
               break;
            }
         }
      } else if (var9 == 0) {
         var10 = var2.getByte();
         if (var15 != null && var10 >= 0 && var10 < var15.getStaticMovingObjects().size()) {
            var16 = (IsoObject)var15.getStaticMovingObjects().get(var10);
            if (var16 != null && ((IsoObject)var16).getContainer() != null) {
               if (((IsoObject)var16).getContainer().isExplored()) {
                  return;
               }

               var17 = ((IsoObject)var16).getContainer();
            }
         }
      }

      if (var17 != null && !var17.isExplored()) {
         var17.setExplored(true);
         var24 = var17.Items.size();
         LuaManager.fillContainer(var17, (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var3)));
         if (var24 != var17.Items.size()) {
            ((IsoObject)var16).revisionUp();

            for(int var25 = 0; var25 < udpEngine.connections.size(); ++var25) {
               UdpConnection var20 = (UdpConnection)udpEngine.connections.get(var25);
               if (var20.ReleventTo((float)var15.x, (float)var15.y)) {
                  ByteBufferWriter var21 = var20.startPacket();
                  PacketTypes.doPacket((short)20, var21);
                  var21.putShort(var9);
                  var21.putInt(var6);
                  var21.putInt(var7);
                  var21.putInt(var8);
                  if (var9 == 0) {
                     var21.putByte(var10);
                  } else if (var9 == 1) {
                     var21.putLong(var12);
                  } else if (var9 == 3) {
                     var21.putShort(var14);
                     var21.putByte(var11);
                  } else {
                     var21.putByte(var10);
                     var21.putByte(var11);
                  }

                  try {
                     CompressIdenticalItems.save(var21.bb, var17.getItems(), (IsoGameCharacter)null);
                  } catch (Exception var23) {
                     var23.printStackTrace();
                  }

                  var20.endPacketUnordered();
               }
            }

         }
      }
   }

   public static void sendItemsInContainer(IsoObject var0, ItemContainer var1) {
      if (udpEngine != null) {
         if (var1 == null) {
            DebugLog.log("sendItemsInContainer: container is null");
         } else {
            if (var0 instanceof IsoWorldInventoryObject) {
               IsoWorldInventoryObject var2 = (IsoWorldInventoryObject)var0;
               if (!(var2.getItem() instanceof InventoryContainer)) {
                  DebugLog.log("sendItemsInContainer: IsoWorldInventoryObject item isn't a container");
                  return;
               }

               InventoryContainer var3 = (InventoryContainer)var2.getItem();
               if (var3.getInventory() != var1) {
                  DebugLog.log("sendItemsInContainer: wrong container for IsoWorldInventoryObject");
                  return;
               }
            } else if (var0 instanceof BaseVehicle) {
               if (var1.vehiclePart == null || var1.vehiclePart.getItemContainer() != var1 || var1.vehiclePart.getVehicle() != var0) {
                  DebugLog.log("sendItemsInContainer: wrong container for BaseVehicle");
                  return;
               }
            } else if (var0 instanceof IsoDeadBody) {
               if (var1 != var0.getContainer()) {
                  DebugLog.log("sendItemsInContainer: wrong container for IsoDeadBody");
                  return;
               }
            } else if (var0.getContainerIndex(var1) == -1) {
               DebugLog.log("sendItemsInContainer: wrong container for IsoObject");
               return;
            }

            if (var0 != null && var1 != null && !var1.getItems().isEmpty()) {
               var0.revisionUp();

               for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
                  UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
                  if (var8.ReleventTo((float)var0.square.x, (float)var0.square.y)) {
                     ByteBufferWriter var4 = var8.startPacket();
                     PacketTypes.doPacket((short)20, var4);
                     if (var0 instanceof IsoDeadBody) {
                        var4.putShort((short)0);
                     } else if (var0 instanceof IsoWorldInventoryObject) {
                        var4.putShort((short)1);
                     } else if (var0 instanceof BaseVehicle) {
                        var4.putShort((short)3);
                     } else {
                        var4.putShort((short)2);
                     }

                     var4.putInt(var0.getSquare().getX());
                     var4.putInt(var0.getSquare().getY());
                     var4.putInt(var0.getSquare().getZ());
                     if (var0 instanceof IsoDeadBody) {
                        var4.putByte((byte)var0.getStaticMovingObjectIndex());
                     } else if (var0 instanceof IsoWorldInventoryObject) {
                        var4.putLong(((IsoWorldInventoryObject)var0).getItem().id);
                     } else if (var0 instanceof BaseVehicle) {
                        var4.putShort(((BaseVehicle)var0).VehicleID);
                        var4.putByte((byte)var1.vehiclePart.getIndex());
                     } else {
                        var4.putByte((byte)var0.getObjectIndex());
                        var4.putByte((byte)var0.getContainerIndex(var1));
                     }

                     try {
                        CompressIdenticalItems.save(var4.bb, var1.getItems(), (IsoGameCharacter)null);
                     } catch (Exception var6) {
                        var6.printStackTrace();
                     }

                     var8.endPacketImmediate();
                  }
               }

            }
         }
      }
   }

   private static void logDupeItem(UdpConnection var0) {
      IsoPlayer var1 = null;

      for(int var2 = 0; var2 < Players.size(); ++var2) {
         if (var0.username.equals(((IsoPlayer)Players.get(var2)).username)) {
            var1 = (IsoPlayer)Players.get(var2);
            break;
         }
      }

      String var3 = "";
      if (var1 != null) {
         var3 = LoggerManager.getPlayerCoords(var1);
      }

      LoggerManager.getLogger("user").write("Error: Dupe item ID for " + var1.getDisplayName() + " " + var3);
      ServerWorldDatabase.instance.addUserlog(var0.username, Userlog.UserlogType.DupeItem, "", "server", 1);
   }

   private static void sendItemsToContainer(ByteBuffer var0, UdpConnection var1) {
      ByteBufferReader var2 = new ByteBufferReader(var0);
      short var3 = var2.getShort();
      int var4 = var2.getInt();
      int var5 = var2.getInt();
      int var6 = var2.getInt();
      IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var4, var5, var6);
      HashSet var8 = new HashSet();
      byte var9 = 0;
      if (var7 == null) {
         DebugLog.log("ERROR sendItemsToContainer square is null");
      } else {
         ItemContainer var10 = null;
         IsoObject var11 = null;
         byte var12;
         if (var3 == 0) {
            var12 = var2.getByte();
            if (var12 < 0 || var12 >= var7.getStaticMovingObjects().size()) {
               DebugLog.log("ERROR sendItemsToContainer invalid corpse index");
               return;
            }

            IsoObject var13 = (IsoObject)var7.getStaticMovingObjects().get(var12);
            if (var13 != null && var13.getContainer() != null) {
               var10 = var13.getContainer();
            }
         } else {
            int var14;
            if (var3 == 1) {
               long var19 = var2.getLong();

               for(var14 = 0; var14 < var7.getWorldObjects().size(); ++var14) {
                  IsoWorldInventoryObject var15 = (IsoWorldInventoryObject)var7.getWorldObjects().get(var14);
                  if (var15 != null && var15.getItem() instanceof InventoryContainer && var15.getItem().id == var19) {
                     var10 = ((InventoryContainer)var15.getItem()).getInventory();
                     break;
                  }
               }

               if (var10 == null) {
                  DebugLog.log("ERROR sendItemsToContainer can't find world item with id=" + var19);
                  return;
               }
            } else {
               byte var20;
               if (var3 == 2) {
                  var12 = var2.getByte();
                  var20 = var2.getByte();
                  if (var12 < 0 || var12 >= var7.getObjects().size()) {
                     DebugLog.log("ERROR sendItemsToContainer invalid object index");

                     for(var14 = 0; var14 < var7.getObjects().size(); ++var14) {
                        if (((IsoObject)var7.getObjects().get(var14)).getContainer() != null) {
                           var12 = (byte)var14;
                           var20 = 0;
                           break;
                        }
                     }

                     if (var12 == -1) {
                        return;
                     }
                  }

                  var11 = (IsoObject)var7.getObjects().get(var12);
                  var10 = var11 != null ? var11.getContainerByIndex(var20) : null;
               } else if (var3 == 3) {
                  short var21 = var2.getShort();
                  var20 = var2.getByte();
                  BaseVehicle var25 = VehicleManager.instance.getVehicleByID(var21);
                  if (var25 == null) {
                     DebugLog.log("ERROR sendItemsToContainer invalid vehicle id");
                     return;
                  }

                  VehiclePart var26 = var25.getPartByIndex(var20);
                  var10 = var26 == null ? null : var26.getItemContainer();
               }
            }
         }

         if (var10 != null) {
            try {
               ArrayList var22 = CompressIdenticalItems.load(var2.bb, 143, (ArrayList)null, (ArrayList)null);

               for(int var24 = 0; var24 < var22.size(); ++var24) {
                  InventoryItem var27 = (InventoryItem)var22.get(var24);
                  if (var27 != null) {
                     if (var10.containsID(var27.id)) {
                        System.out.println("Error: Dupe item ID for " + var1.username);
                        logDupeItem(var1);
                     } else {
                        var10.addItem(var27);
                        var10.setExplored(true);
                        var8.add(var27.getFullType());
                     }
                  }
               }
            } catch (Exception var16) {
               var16.printStackTrace();
            }

            if (var11 != null) {
               LuaManager.updateOverlaySprite(var11);
               if ("campfire".equals(var10.getType())) {
                  var11.sendObjectChange("container.customTemperature");
               }
            }
         }

         var7.revisionUp();
      }

      for(int var18 = 0; var18 < udpEngine.connections.size(); ++var18) {
         UdpConnection var17 = (UdpConnection)udpEngine.connections.get(var18);
         if (var17.getConnectedGUID() != var1.getConnectedGUID() && var17.ReleventTo((float)var7.x, (float)var7.y)) {
            var0.rewind();
            ByteBufferWriter var23 = var17.startPacket();
            PacketTypes.doPacket((short)20, var23);
            var23.bb.put(var0);
            var17.endPacketUnordered();
         }
      }

      LoggerManager.getLogger("item").write(var1.idStr + " \"" + var1.username + "\" container +" + var9 + " " + var4 + "," + var5 + "," + var6 + " " + var8.toString());
   }

   public static boolean CheckPlayerStillValid(IsoPlayer var0) {
      long var1 = (Long)PlayerToAddressMap.get(var0);
      return !(var0.getBodyDamage().getHealth() < 0.0F);
   }

   public static void addConnection(UdpConnection var0) {
      synchronized(MainLoopNetData) {
         MainLoopNetData.add(new GameServer.DelayedConnection(var0, true));
      }
   }

   public static void addDisconnect(UdpConnection var0) {
      synchronized(MainLoopNetData) {
         MainLoopNetData.add(new GameServer.DelayedConnection(var0, false));
      }
   }

   public static void disconnectPlayer(IsoPlayer var0, UdpConnection var1) {
      if (var0 != null) {
         ChatServer.getInstance().disconnectPlayer(var0.getOnlineID());
         int var2;
         if (var0.getVehicle() != null) {
            if (var0.getVehicle().getDriver() != null) {
               var0.getVehicle().netPlayerAuthorization = 0;
               var0.getVehicle().netPlayerId = -1;
            }

            var2 = var0.getVehicle().getSeat(var0);
            if (var2 != -1) {
               var0.getVehicle().clearPassenger(var2);
            }
         }

         if (!var0.isDead()) {
            ServerWorldDatabase.instance.saveTransactionID(var0.username, var0.getTransactionID());
         }

         var0.removeFromWorld();
         var0.removeFromSquare();
         PlayerToAddressMap.remove(var0);
         IDToAddressMap.remove(var0.OnlineID);
         IDToPlayerMap.remove(var0.OnlineID);
         Players.remove(var0);
         var1.usernames[var0.PlayerIndex] = null;
         var1.players[var0.PlayerIndex] = null;
         var1.playerIDs[var0.PlayerIndex] = -1;
         var1.ReleventPos[var0.PlayerIndex] = null;
         var1.connectArea[var0.PlayerIndex] = null;

         for(var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
            UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
            ByteBufferWriter var4 = var3.startPacket();
            PacketTypes.doPacket((short)13, var4);
            var4.putInt(var0.OnlineID);
            var3.endPacketImmediate();
         }

         ServerLOS.instance.removePlayer(var0);
         ZombiePopulationManager.instance.updateLoadedAreas();
         DebugLog.log(DebugType.Network, "Disconnected player \"" + var0.getDisplayName() + "\" " + var1.idStr);
         LoggerManager.getLogger("user").write(var1.idStr + " \"" + var0.getDisplayName() + "\" disconnected player " + LoggerManager.getPlayerCoords(var0));
      }
   }

   public static void heartBeat() {
      ++count;
   }

   public static void receivePlayerInfo(ByteBuffer var0, UdpConnection var1) {
      if (var1 != null) {
         boolean var2 = DebugPlayer.contains(var1);
         if (var2) {
            DebugLog.log("DBGPLR: received player update for " + var1.username + " checksumState=" + var1.checksumState);
         }

         if (var1.checksumState != UdpConnection.ChecksumState.Done) {
            ByteBufferWriter var37 = var1.startPacket();
            PacketTypes.doPacket((short)83, var37);
            var37.putUTF("You have been kicked from this server.");
            var1.endPacketImmediate();
            var1.forceDisconnect();
         } else {
            short var3 = var0.getShort();
            byte var4 = var0.get();
            float var5 = var0.getFloat();
            float var6 = var0.getFloat();
            float var7 = var0.getFloat();
            float var8 = var0.getFloat();
            float var9 = var0.getFloat();
            byte var10 = var0.get();
            byte var11 = var0.get();
            byte var12 = var0.get();
            float var13 = var0.getFloat();
            float var14 = var0.getFloat();
            float var15 = var0.getFloat();
            float var16 = var0.getFloat();
            short var17 = var0.getShort();
            short var18 = var0.getShort();
            BaseVehicle var19 = VehicleManager.instance.getVehicleByID(var17);
            byte var20 = var0.get();
            boolean var21 = (var20 & 1) != 0;
            boolean var22 = (var20 & 2) != 0;
            boolean var23 = (var20 & 4) != 0;
            boolean var24 = (var20 & 8) != 0;
            boolean var25 = (var20 & 16) != 0;
            boolean var26 = (var20 & 32) != 0;
            boolean var27 = (var20 & 64) != 0;
            boolean var28 = (var20 & 128) != 0;
            IsoPlayer var29 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var3));
            if (var2 && var29 == null) {
               DebugLog.log("DBGPLR: IDToPlayerMap is null for id=" + var3 + " user=" + var1.username);
            }

            if (var29 != null) {
               RakVoice.SetPlayerCoordinate(var1.getConnectedGUID(), var5, var6, var7, var29.invisible);
               if (ServerOptions.instance.KickFastPlayers.getValue()) {
                  Vector2 var30 = (Vector2)playerToCoordsMap.get(Integer.valueOf(var3));
                  if (var30 == null) {
                     var30 = new Vector2();
                     var30.x = var5;
                     var30.y = var6;
                     playerToCoordsMap.put(Integer.valueOf(var3), var30);
                  } else {
                     if (!var29.accessLevel.equals("") && !var29.GhostMode && (Math.abs(var5 - var30.x) > 4.0F || Math.abs(var6 - var30.y) > 4.0F)) {
                        if (playerMovedToFastMap.get(Integer.valueOf(var3)) == null) {
                           playerMovedToFastMap.put(Integer.valueOf(var3), 1);
                        } else {
                           playerMovedToFastMap.put(Integer.valueOf(var3), (Integer)playerMovedToFastMap.get(Integer.valueOf(var3)) + 1);
                        }

                        LoggerManager.getLogger("admin").write(var29.getDisplayName() + " go too fast (" + playerMovedToFastMap.get(Integer.valueOf(var3)) + " times)");
                        if ((Integer)playerMovedToFastMap.get(Integer.valueOf(var3)) == 10) {
                           LoggerManager.getLogger("admin").write(var29.getDisplayName() + " kicked for going too fast");
                           ByteBufferWriter var40 = var1.startPacket();
                           PacketTypes.doPacket((short)83, var40);
                           var40.putUTF("You have been kicked from this server.");
                           var1.endPacketImmediate();
                           var1.forceDisconnect();
                           return;
                        }
                     }

                     var30.x = var5;
                     var30.y = var6;
                  }
               }

               var1.ReleventPos[var29.PlayerIndex].x = var5;
               var1.ReleventPos[var29.PlayerIndex].y = var6;
               var1.ReleventPos[var29.PlayerIndex].z = var7;
               if (var29.getStateMachine().getCurrent() == DieState.instance()) {
                  if (var2) {
                     DebugLog.log("DBGPLR: player in DieState id=" + var3 + " user=" + var1.username);
                  }

               } else {
                  var29.setbSeenThisFrame(false);
                  var29.setbCouldBeSeenThisFrame(false);
                  var29.bSneaking = var24;
                  var29.CurrentSpeed = var14;
                  IsoAnim var38 = (IsoAnim)var29.legsSprite.AnimStack.get(var11);
                  var29.TimeSinceLastNetData = 0;
                  var29.setDir(var4);
                  var29.angle.set(var29.dir.ToVector());
                  if (var29.getX() == var5 && var29.getY() == var6 && var29.getZ() == var7) {
                     var29.JustMoved = false;
                  } else {
                     var29.JustMoved = true;
                  }

                  if (var2) {
                     DebugLog.log("DBGPLR: x,y,z now " + (int)var5 + "," + (int)var6 + "," + (int)var7 + " for id=" + var3 + " user=" + var1.username);
                  }

                  var29.removeFromSquare();
                  var29.setX(var5);
                  var29.setY(var6);
                  var29.setZ(var7);
                  var29.setLx(var5);
                  var29.setLy(var6);
                  var29.setLz(var7);
                  var29.setRemoteState(var10);
                  var29.setRemoteMoveX(var8);
                  var29.setRemoteMoveY(var9);
                  var29.def.AnimFrameIncrease = var13;
                  var29.PlayAnim(var38.name);
                  var29.def.Frame = (float)var12;
                  var29.def.Finished = var21;
                  var29.def.Looped = var22;
                  if (var29.legsSprite != null && var29.legsSprite.CurrentAnim != null && var23) {
                     var29.legsSprite.CurrentAnim.FinishUnloopedOnFrame = 0;
                  }

                  var29.mpTorchDist = var15;
                  var29.mpTorchCone = var25;
                  var29.mpTorchStrength = var16;
                  var29.setAsleep(var27);
                  var29.setbClimbing(var28);
                  var29.ensureOnTile();
                  IsoGameCharacter var31;
                  if (var29.getVehicle() == null) {
                     if (var19 != null) {
                        if (var18 >= 0 && var18 < var19.getMaxPassengers()) {
                           var31 = var19.getCharacter(var18);
                           if (var31 == null) {
                              if (bDebug) {
                                 DebugLog.log(var29.getUsername() + " got in vehicle " + var19.VehicleID + " seat " + var18);
                              }

                              var19.enterRSync(var18, var29, var19);
                           } else if (var31 != var29) {
                              DebugLog.log(var29.getUsername() + " got in same seat as " + ((IsoPlayer)var31).getUsername());
                              var29.sendObjectChange("exitVehicle");
                           }
                        } else {
                           DebugLog.log(var29.getUsername() + " invalid seat vehicle " + var19.VehicleID + " seat " + var18);
                        }
                     }
                  } else if (var19 != null) {
                     if (var19 == var29.getVehicle() && var29.getVehicle().getSeat(var29) != -1) {
                        var31 = var19.getCharacter(var18);
                        if (var31 == null) {
                           if (var19.getSeat(var29) != var18) {
                              var19.switchSeatRSync(var29, var18);
                           }
                        } else if (var31 != var29) {
                           DebugLog.log(var29.getUsername() + " switched to same seat as " + ((IsoPlayer)var31).getUsername());
                           var29.sendObjectChange("exitVehicle");
                        }
                     } else {
                        DebugLog.log(var29.getUsername() + " vehicle/seat remote " + var19.VehicleID + "/" + var18 + " local " + var29.getVehicle().VehicleID + "/" + var29.getVehicle().getSeat(var29));
                        var29.sendObjectChange("exitVehicle");
                     }
                  } else {
                     var29.getVehicle().exitRSync(var29);
                     var29.setVehicle((BaseVehicle)null);
                  }

                  for(int var39 = 0; var39 < udpEngine.connections.size(); ++var39) {
                     UdpConnection var32 = (UdpConnection)udpEngine.connections.get(var39);
                     if (var32 != var1 && var32.ReleventTo(var5, var6) && var32.isFullyConnected()) {
                        boolean var33 = false;

                        for(int var34 = 0; var34 < 4; ++var34) {
                           IsoPlayer var35 = var32.players[var34];
                           if (var35 != null && (!var29.invisible || !var35.accessLevel.equals(""))) {
                              var33 = true;
                              break;
                           }
                        }

                        if (var33) {
                           long var41 = System.currentTimeMillis();
                           ByteBufferWriter var36 = var32.startPacket();
                           PacketTypes.doPacket((short)7, var36);
                           var36.putShort((short)var29.OnlineID);
                           var36.putLong(var41);
                           var36.putByte((byte)var29.dir.index());
                           var36.putFloat(var29.getX());
                           var36.putFloat(var29.getY());
                           var36.putFloat(var29.getZ());
                           var36.putFloat(var8);
                           var36.putFloat(var9);
                           var36.putByte(var29.NetRemoteState);
                           if (var29.sprite != null) {
                              var36.putByte((byte)var29.sprite.AnimStack.indexOf(var29.sprite.CurrentAnim));
                           } else {
                              var36.putByte((byte)0);
                           }

                           var36.putByte((byte)((int)var29.def.Frame));
                           var36.putFloat(var29.def.AnimFrameIncrease);
                           var36.putFloat(var15);
                           var36.putFloat(var16);
                           if (var19 == null) {
                              var36.putShort((short)-1);
                              var36.putShort((short)-1);
                           } else {
                              var36.putShort(var19.VehicleID);
                              var36.putShort((short)var19.getSeat(var29));
                           }

                           var20 = 0;
                           if (var29.def.Finished) {
                              var20 = (byte)(var20 | 1);
                           }

                           if (var29.def.Looped) {
                              var20 = (byte)(var20 | 2);
                           }

                           if (var29.legsSprite != null && var29.legsSprite.CurrentAnim != null && var29.legsSprite.CurrentAnim.FinishUnloopedOnFrame == 0) {
                              var20 = (byte)(var20 | 4);
                           }

                           if (var29.bSneaking) {
                              var20 = (byte)(var20 | 8);
                           }

                           if (var25) {
                              var20 = (byte)(var20 | 16);
                           }

                           if (var26) {
                              var20 = (byte)(var20 | 32);
                           }

                           if (var27) {
                              var20 = (byte)(var20 | 64);
                           }

                           if (var28) {
                              var20 = (byte)(var20 | 128);
                           }

                           var36.putByte(var20);
                           var32.endPacketSuperHighUnreliable();
                        }
                     }
                  }

               }
            }
         }
      }
   }

   public static int getFreeSlot() {
      for(int var0 = 0; var0 < udpEngine.getMaxConnections(); ++var0) {
         if (SlotToConnection[var0] == null) {
            return var0;
         }
      }

      return -1;
   }

   public static void receiveClientConnect(UdpConnection var0, ServerWorldDatabase.LogonResult var1) {
      int var2 = getFreeSlot();
      int var3 = var2 * 4;
      if (var0.playerDownloadServer != null) {
         try {
            IDToAddressMap.put(var3, var0.getConnectedGUID());
            var0.playerDownloadServer.destroy();
         } catch (Exception var16) {
            var16.printStackTrace();
         }
      }

      playerToCoordsMap.put(var3, new Vector2());
      playerMovedToFastMap.put(var3, 0);
      SlotToConnection[var2] = var0;
      var0.playerIDs[0] = var3;
      IDToAddressMap.put(var3, var0.getConnectedGUID());
      var0.playerDownloadServer = new PlayerDownloadServer(var0, DEFAULT_PORT + var2 + 1);
      DebugLog.log(DebugType.Network, "Connected new client " + var0.username + " ID # " + var3 + " and assigned DL port " + var0.playerDownloadServer.port);
      var0.playerDownloadServer.startConnectionTest();

      for(int var4 = 1; var4 < SpawnRegions.size() + 1; ++var4) {
         ByteBufferWriter var5 = var0.startPacket();
         PacketTypes.doPacket((short)171, var5);
         var5.putInt(var4);

         try {
            ((KahluaTable)SpawnRegions.rawget(var4)).save(var5.bb);
            var0.endPacketImmediate();
         } catch (IOException var15) {
            var15.printStackTrace();
         }
      }

      VehicleManager.serverSendVehiclesConfig(var0);
      ByteBufferWriter var17 = var0.startPacket();
      PacketTypes.doPacket((short)21, var17);
      if (SteamUtils.isSteamModeEnabled() && CoopSlave.instance != null && !var0.isCoopHost) {
         var17.putByte((byte)1);
         var17.putLong(CoopSlave.instance.hostSteamID);
         var17.putUTF(ServerName);
      } else {
         var17.putByte((byte)0);
      }

      var17.putByte((byte)var2);
      var17.putInt(var0.playerDownloadServer.port);
      var17.putBoolean(UseTCPForMapDownloads);
      var17.putUTF(var1.accessLevel);
      var17.putUTF(GameMap);
      if (SteamUtils.isSteamModeEnabled()) {
         var17.putShort((short)WorkshopItems.size());

         for(int var18 = 0; var18 < WorkshopItems.size(); ++var18) {
            var17.putLong((Long)WorkshopItems.get(var18));
            var17.putLong(WorkshopTimeStamps[var18]);
         }
      }

      ArrayList var19 = new ArrayList();
      ChooseGameInfo.Mod var6 = null;

      Iterator var7;
      for(var7 = ServerMods.iterator(); var7.hasNext(); var19.add(var6)) {
         String var8 = (String)var7.next();
         String var9 = ZomboidFileSystem.instance.getModDir(var8);
         ChooseGameInfo var10 = new ChooseGameInfo();
         if (var9 != null) {
            try {
               var6 = var10.readModInfo(var9);
            } catch (Exception var14) {
               Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, (String)null, var14);
               var6 = var10.new Mod(var8);
               var6.setId(var8);
            }
         } else {
            var6 = var10.new Mod(var8);
            var6.setId(var8);
            var6.setName(var8);
         }
      }

      var17.putInt(var19.size());
      var7 = var19.iterator();

      while(var7.hasNext()) {
         ChooseGameInfo.Mod var21 = (ChooseGameInfo.Mod)var7.next();
         var17.putUTF(var21.getId());
         var17.putUTF(var21.getUrl());
         var17.putUTF(var21.getName());
      }

      Vector3 var20 = ServerMap.instance.getStartLocation(var1);
      var1.x = (int)var20.x;
      var1.y = (int)var20.y;
      var1.z = (int)var20.z;
      var17.putInt(var1.x);
      var17.putInt(var1.y);
      var17.putInt(var1.z);
      var17.putInt(ServerOptions.instance.getPublicOptions().size());
      var7 = null;
      Iterator var23 = ServerOptions.instance.getPublicOptions().iterator();

      while(var23.hasNext()) {
         String var22 = (String)var23.next();
         var17.putUTF(var22);
         var17.putUTF(ServerOptions.instance.getOption(var22));
      }

      try {
         SandboxOptions.instance.save(var17.bb);
         GameTime.getInstance().saveToPacket(var17.bb);
      } catch (IOException var13) {
         var13.printStackTrace();
      }

      ErosionMain.getInstance().getConfig().save(var17.bb);

      try {
         SGlobalObjects.saveInitialStateForClient(var17.bb);
      } catch (Throwable var12) {
         var12.printStackTrace();
      }

      var17.putInt(ResetID);
      GameWindow.WriteString(var17.bb, Core.getInstance().getPoisonousBerry());
      GameWindow.WriteString(var17.bb, Core.getInstance().getPoisonousMushroom());
      var17.putBoolean(var0.isCoopHost);
      var0.endPacketImmediate();
      if (!SteamUtils.isSteamModeEnabled()) {
         PublicServerUtil.updatePlayers();
      }

   }

   private static void sendLargeFile(UdpConnection var0, String var1) {
      int var2 = large_file_bb.position();

      int var5;
      for(int var4 = 0; var4 < var2; var4 += var5) {
         var5 = Math.min(1000, var2 - var4);
         ByteBufferWriter var6 = var0.startPacket();
         PacketTypes.doPacket((short)37, var6);
         var6.putUTF(var1);
         var6.putInt(var2);
         var6.putInt(var4);
         var6.putInt(var5);
         var6.bb.put(large_file_bb.array(), var4, var5);
         var0.endPacketImmediate();
      }

   }

   private static void receiveRequestData(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      ByteBufferWriter var3;
      SharedDescriptors.Descriptor[] var4;
      int var6;
      int var7;
      if ("descriptors.bin".equals(var2)) {
         var3 = var1.startPacket();
         PacketTypes.doPacket((short)37, var3);
         var3.putUTF(var2);
         var4 = SharedDescriptors.getSharedDescriptors();

         try {
            var3.putShort((short)var4.length);
            SharedDescriptors.Descriptor[] var5 = var4;
            var6 = var4.length;

            for(var7 = 0; var7 < var6; ++var7) {
               SharedDescriptors.Descriptor var8 = var5[var7];
               var8.desc.saveCompact(var3.bb);
               var3.bb.put((byte)var8.palette);
            }
         } catch (Exception var13) {
            var13.printStackTrace();
         }

         var1.endPacketImmediate();
      }

      if ("playerzombiedesc".equals(var2)) {
         var3 = var1.startPacket();
         PacketTypes.doPacket((short)37, var3);
         var3.putUTF(var2);
         var4 = SharedDescriptors.getPlayerZombieDescriptors();
         int var14 = 0;

         for(var6 = 0; var6 < var4.length; ++var6) {
            if (var4[var6] != null) {
               ++var14;
            }
         }

         try {
            var3.putShort((short)var14);
            SharedDescriptors.Descriptor[] var15 = var4;
            var7 = var4.length;

            for(int var16 = 0; var16 < var7; ++var16) {
               SharedDescriptors.Descriptor var9 = var15[var16];
               if (var9 != null) {
                  var3.putShort((short)var9.desc.getID());
                  var9.desc.saveCompact(var3.bb);
                  var3.bb.put((byte)var9.palette);
               }
            }
         } catch (Exception var12) {
            var12.printStackTrace();
         }

         var1.endPacketImmediate();
      }

      if ("map_meta.bin".equals(var2)) {
         try {
            large_file_bb.clear();
            IsoWorld.instance.MetaGrid.savePart(large_file_bb, 0);
            IsoWorld.instance.MetaGrid.savePart(large_file_bb, 1);
            sendLargeFile(var1, var2);
         } catch (Exception var11) {
            var11.printStackTrace();
            var3 = var1.startPacket();
            PacketTypes.doPacket((short)83, var3);
            var3.putUTF("You have been kicked from this server because map_meta.bin could not be saved.");
            var1.endPacketImmediate();
            var1.forceDisconnect();
            addDisconnect(var1);
         }
      }

      if ("map_zone.bin".equals(var2)) {
         try {
            large_file_bb.clear();
            IsoWorld.instance.MetaGrid.saveZone(large_file_bb);
            sendLargeFile(var1, var2);
         } catch (Exception var10) {
            var10.printStackTrace();
            var3 = var1.startPacket();
            PacketTypes.doPacket((short)83, var3);
            var3.putUTF("You have been kicked from this server because map_zone.bin could not be saved.");
            var1.endPacketImmediate();
            var1.forceDisconnect();
            addDisconnect(var1);
         }
      }

   }

   public static void sendMetaGrid(int var0, int var1, int var2, UdpConnection var3) {
      IsoMetaGrid var4 = IsoWorld.instance.MetaGrid;
      if (var0 >= var4.getMinX() && var0 <= var4.getMaxX() && var1 >= var4.getMinY() && var1 <= var4.getMaxY()) {
         IsoMetaCell var5 = var4.getCellData(var0, var1);
         if (var5.info != null && var2 >= 0 && var2 < var5.info.RoomList.size()) {
            ByteBufferWriter var6 = var3.startPacket();
            PacketTypes.doPacket((short)9, var6);
            var6.putShort((short)var0);
            var6.putShort((short)var1);
            var6.putShort((short)var2);
            var6.putBoolean(var5.info.getRoom(var2).def.bLightsActive);
            var3.endPacketImmediate();
         }
      }
   }

   public static void sendMetaGrid(int var0, int var1, int var2) {
      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         sendMetaGrid(var0, var1, var2, var4);
      }

   }

   private static void preventIndoorZombies(int var0, int var1, int var2) {
      RoomDef var3 = IsoWorld.instance.MetaGrid.getRoomAt(var0, var1, var2);
      if (var3 != null) {
         boolean var4 = false;
         if (SpawnBuildings != null) {
            var4 = SpawnBuildings.contains(var3.getBuilding());
         }

         var3.getBuilding().setAllExplored(true);
         ArrayList var5 = IsoWorld.instance.CurrentCell.getZombieList();

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            IsoZombie var7 = (IsoZombie)var5.get(var6);
            if ((var4 || var7.bIndoorZombie) && var7.getSquare() != null && var7.getSquare().getRoom() != null && var7.getSquare().getRoom().def.building == var3.getBuilding()) {
               VirtualZombieManager.instance.removeZombieFromWorld(var7);
               if (var6 >= var5.size() || var5.get(var6) != var7) {
                  --var6;
               }
            }
         }

      }
   }

   private static void receivePlayerConnect(ByteBuffer var0, UdpConnection var1, String var2) {
      byte var3 = var0.get();
      if (var3 >= 0 && var3 < 4 && var1.players[var3] == null) {
         byte var4 = var0.get();
         var1.ReleventRange = (byte)(var4 / 2 + 2);
         float var5 = var0.getFloat();
         float var6 = var0.getFloat();
         float var7 = var0.getFloat();
         var1.ReleventPos[var3].x = var5;
         var1.ReleventPos[var3].y = var6;
         var1.ReleventPos[var3].z = var7;
         var1.connectArea[var3] = null;
         var1.ChunkGridWidth = var4;
         var1.loadedCells[var3] = new ClientServerMap(var3, (int)var5, (int)var6, var4);
         SurvivorDesc var8 = SurvivorFactory.CreateSurvivor();

         try {
            var8.load(var0, 143, (IsoGameCharacter)null);
         } catch (IOException var14) {
            var14.printStackTrace();
         }

         IsoPlayer var9 = new IsoPlayer((IsoCell)null, var8, (int)var5, (int)var6, (int)var7);
         var9.PlayerIndex = var3;
         var9.OnlineChunkGridWidth = var4;
         Players.add(var9);
         var9.bRemote = true;
         int var10 = var1.playerIDs[var3];
         IDToPlayerMap.put(var10, var9);
         var1.players[var3] = var9;
         PlayerToAddressMap.put(var9, var1.getConnectedGUID());
         var9.OnlineID = var10;

         try {
            var9.getXp().load(var0, 143);
         } catch (IOException var13) {
            var13.printStackTrace();
         }

         var9.setAllChatMuted(var0.get() == 1);
         var1.allChatMuted = var9.isAllChatMuted();
         var9.setTagPrefix(GameWindow.ReadString(var0));
         var9.setTagColor(new ColorInfo(var0.getFloat(), var0.getFloat(), var0.getFloat(), 1.0F));
         var9.setTransactionID(var0.getInt());
         var9.setHoursSurvived(var0.getDouble());
         var9.setZombieKills(var0.getInt());
         var9.setDisplayName(GameWindow.ReadString(var0));
         var9.setSpeakColour(new Color(var0.getFloat(), var0.getFloat(), var0.getFloat(), 1.0F));
         var9.showTag = var0.get() == 1;
         var9.factionPvp = var0.get() == 1;
         if (SteamUtils.isSteamModeEnabled()) {
            var9.setSteamID(var1.steamID);
            String var11 = GameWindow.ReadStringUTF(var0);
            SteamGameServer.BUpdateUserData(var1.steamID, var1.username, 0);
         }

         var9.username = var2;
         var9.accessLevel = var1.accessLevel;
         if (!var9.accessLevel.equals("") && CoopSlave.instance == null) {
            var9.GhostMode = true;
            var9.invisible = true;
            var9.godMod = true;
         }

         ChatServer.getInstance().initPlayer(var9.OnlineID);
         var1.setFullyConnected();
         sendWeather(var1);

         for(int var15 = 0; var15 < udpEngine.connections.size(); ++var15) {
            UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var15);
            sendPlayerConnect(var9, var12);
         }

         var1.loadedCells[var3].setLoaded();
         var1.loadedCells[var3].sendPacket(var1);
         preventIndoorZombies((int)var5, (int)var6, (int)var7);
         ServerLOS.instance.addPlayer(var9);
         LoggerManager.getLogger("user").write(var1.idStr + " \"" + var9.username + "\" fully connected " + LoggerManager.getPlayerCoords(var9));
      }
   }

   private static void receivePlayerSave(ByteBuffer var0, UdpConnection var1) {
      if ((Calendar.getInstance().getTimeInMillis() - previousSave) / 60000L >= 0L) {
         byte var2 = var0.get();
         if (var2 >= 0 && var2 < 4) {
            int var3 = var0.getInt();
            float var4 = var0.getFloat();
            float var5 = var0.getFloat();
            float var6 = var0.getFloat();
            ServerMap.instance.saveZoneInsidePlayerInfluence(var3);
         }
      }
   }

   private static void coopAccessGranted(int var0, UdpConnection var1) {
      ByteBufferWriter var2 = var1.startPacket();
      PacketTypes.doPacket((short)27, var2);
      var2.putBoolean(true);
      var2.putByte((byte)var0);
      var1.endPacketImmediate();
   }

   private static void coopAccessDenied(String var0, int var1, UdpConnection var2) {
      ByteBufferWriter var3 = var2.startPacket();
      PacketTypes.doPacket((short)27, var3);
      var3.putBoolean(false);
      var3.putByte((byte)var1);
      var3.putUTF(var0);
      var2.endPacketImmediate();
   }

   private static void addCoopPlayer(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      byte var3 = var0.get();
      if (var3 >= 0 && var3 < 4) {
         if (var1.players[var3] != null && !var1.players[var3].isDead()) {
            coopAccessDenied("Coop player " + (var3 + 1) + "/" + 4 + " already exists", var3, var1);
         } else {
            String var4;
            if (var2 != 1) {
               if (var2 == 2) {
                  var4 = var1.usernames[var3];
                  if (var4 == null) {
                     coopAccessDenied("Coop player login wasn't received", var3, var1);
                  } else {
                     DebugLog.log("coop player=" + (var3 + 1) + "/" + 4 + " username=\"" + var4 + "\" player info received");
                     receivePlayerConnect(var0, var1, var4);
                  }
               }
            } else {
               var4 = GameWindow.ReadStringUTF(var0);
               if (var4.isEmpty()) {
                  coopAccessDenied("No username given", var3, var1);
               } else {
                  int var5;
                  for(var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
                     UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);

                     for(int var7 = 0; var7 < 4; ++var7) {
                        if ((var6 != var1 || var3 != var7) && var4.equals(var6.usernames[var7])) {
                           coopAccessDenied("User \"" + var4 + "\" already connected", var3, var1);
                           return;
                        }
                     }
                  }

                  DebugLog.log("coop player=" + (var3 + 1) + "/" + 4 + " username=\"" + var4 + "\" is joining");
                  float var11;
                  if (var1.players[var3] != null) {
                     DebugLog.log("coop player=" + (var3 + 1) + "/" + 4 + " username=\"" + var4 + "\" is replacing dead player");
                     var5 = var1.players[var3].OnlineID;
                     disconnectPlayer(var1.players[var3], var1);
                     float var10 = var0.getFloat();
                     var11 = var0.getFloat();
                     var1.usernames[var3] = var4;
                     var1.ReleventPos[var3] = new Vector3(var10, var11, 0.0F);
                     var1.connectArea[var3] = new Vector3(var10 / 10.0F, var11 / 10.0F, (float)var1.ChunkGridWidth);
                     var1.playerIDs[var3] = var5;
                     IDToAddressMap.put(var5, var1.getConnectedGUID());
                     coopAccessGranted(var3, var1);
                     ZombiePopulationManager.instance.updateLoadedAreas();
                     if (ChatServer.isInited()) {
                        ChatServer.getInstance().initPlayer(var5);
                     }

                  } else if (getPlayerCount() >= ServerOptions.instance.MaxPlayers.getValue()) {
                     coopAccessDenied("Server is full", var3, var1);
                  } else {
                     var5 = -1;

                     int var9;
                     for(var9 = 0; var9 < udpEngine.getMaxConnections(); ++var9) {
                        if (SlotToConnection[var9] == var1) {
                           var5 = var9;
                           break;
                        }
                     }

                     var9 = var5 * 4 + var3;
                     DebugLog.log("coop player=" + (var3 + 1) + "/" + 4 + " username=\"" + var4 + "\" assigned id=" + var9);
                     var11 = var0.getFloat();
                     float var8 = var0.getFloat();
                     var1.usernames[var3] = var4;
                     var1.ReleventPos[var3] = new Vector3(var11, var8, 0.0F);
                     var1.playerIDs[var3] = var9;
                     var1.connectArea[var3] = new Vector3(var11 / 10.0F, var8 / 10.0F, (float)var1.ChunkGridWidth);
                     IDToAddressMap.put(var9, var1.getConnectedGUID());
                     coopAccessGranted(var3, var1);
                     ZombiePopulationManager.instance.updateLoadedAreas();
                  }
               }
            }
         }
      } else {
         coopAccessDenied("Invalid coop player index", var3, var1);
      }
   }

   private static void sendInitialWorldState(UdpConnection var0) {
      if (RainManager.isRaining()) {
         sendStartRain(var0);
      }

   }

   private static void receiveObjectModData(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      boolean var6 = var0.get() == 1;
      IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var7 != null && var5 >= 0 && var5 < var7.getObjects().size()) {
         IsoObject var8 = (IsoObject)var7.getObjects().get(var5);
         int var9;
         if (var6) {
            var9 = var8.getWaterAmount();

            try {
               var8.getModData().load((ByteBuffer)var0, 143);
            } catch (IOException var11) {
               var11.printStackTrace();
            }

            if (var9 != var8.getWaterAmount()) {
               LuaEventManager.triggerEvent("OnWaterAmountChange", var8, var9);
            }
         } else if (var8.hasModData()) {
            var8.getModData().wipe();
         }

         for(var9 = 0; var9 < udpEngine.connections.size(); ++var9) {
            UdpConnection var10 = (UdpConnection)udpEngine.connections.get(var9);
            if (var10.getConnectedGUID() != var1.getConnectedGUID() && var10.ReleventTo((float)var2, (float)var3)) {
               sendObjectModData(var8, var10);
            }
         }
      } else if (var7 != null) {
         DebugLog.log("receiveObjectModData: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
      } else if (bDebug) {
         DebugLog.log("receiveObjectModData: sq is null x,y,z=" + var2 + "," + var3 + "," + var4);
      }

   }

   private static void sendObjectModData(IsoObject var0, UdpConnection var1) {
      if (var0.getSquare() != null) {
         ByteBufferWriter var2 = var1.startPacket();
         PacketTypes.doPacket((short)58, var2);
         var2.putInt(var0.getSquare().getX());
         var2.putInt(var0.getSquare().getY());
         var2.putInt(var0.getSquare().getZ());
         var2.putInt(var0.getSquare().getObjects().indexOf(var0));
         if (var0.getModData().isEmpty()) {
            var2.putByte((byte)0);
         } else {
            var2.putByte((byte)1);

            try {
               var0.getModData().save(var2.bb);
            } catch (IOException var4) {
               var4.printStackTrace();
            }
         }

         var1.endPacketImmediate();
      }
   }

   public static void sendObjectModData(IsoObject var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         if (var2.ReleventTo(var0.getX(), var0.getY())) {
            sendObjectModData(var0, var2);
         }
      }

   }

   public static void sendSlowFactor(IsoGameCharacter var0) {
      if (var0 instanceof IsoPlayer) {
         if (PlayerToAddressMap.containsKey(var0)) {
            long var1 = (Long)PlayerToAddressMap.get((IsoPlayer)var0);
            UdpConnection var3 = udpEngine.getActiveConnection(var1);
            if (var3 != null) {
               ByteBufferWriter var4 = var3.startPacket();
               PacketTypes.doPacket((short)63, var4);
               var4.putByte((byte)((IsoPlayer)var0).PlayerIndex);
               var4.putFloat(var0.getSlowTimer());
               var4.putFloat(var0.getSlowFactor());
               var3.endPacketImmediate();
            }
         }
      }
   }

   private static void sendObjectChange(IsoObject var0, String var1, KahluaTable var2, UdpConnection var3) {
      if (var0.getSquare() != null) {
         ByteBufferWriter var4 = var3.startPacket();
         PacketTypes.doPacket((short)59, var4);
         if (var0 instanceof IsoPlayer) {
            var4.putByte((byte)1);
            var4.putInt(((IsoPlayer)var0).OnlineID);
         } else if (var0 instanceof BaseVehicle) {
            var4.putByte((byte)2);
            var4.putShort(((BaseVehicle)var0).getId());
         } else {
            var4.putByte((byte)0);
            var4.putInt(var0.getSquare().getX());
            var4.putInt(var0.getSquare().getY());
            var4.putInt(var0.getSquare().getZ());
            var4.putInt(var0.getSquare().getObjects().indexOf(var0));
         }

         var4.putUTF(var1);
         var0.saveChange(var1, var2, var4.bb);
         var3.endPacketImmediate();
      }
   }

   public static void sendObjectChange(IsoObject var0, String var1, KahluaTable var2) {
      if (var0 != null) {
         for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
            UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
            if (var4.ReleventTo(var0.getX(), var0.getY())) {
               sendObjectChange(var0, var1, var2, var4);
            }
         }

      }
   }

   public static void sendObjectChange(IsoObject var0, String var1, Object... var2) {
      if (var2.length == 0) {
         sendObjectChange(var0, var1, (KahluaTable)null);
      } else if (var2.length % 2 == 0) {
         KahluaTable var3 = LuaManager.platform.newTable();

         for(int var4 = 0; var4 < var2.length; var4 += 2) {
            Object var5 = var2[var4 + 1];
            if (var5 instanceof Float) {
               var3.rawset(var2[var4], ((Float)var5).doubleValue());
            } else if (var5 instanceof Integer) {
               var3.rawset(var2[var4], ((Integer)var5).doubleValue());
            } else if (var5 instanceof Short) {
               var3.rawset(var2[var4], ((Short)var5).doubleValue());
            } else {
               var3.rawset(var2[var4], var5);
            }
         }

         sendObjectChange(var0, var1, var3);
      }
   }

   private static void updateHandEquips(UdpConnection var0, IsoPlayer var1) {
      ByteBufferWriter var2 = var0.startPacket();
      PacketTypes.doPacket((short)25, var2);
      var2.putByte((byte)0);
      var2.putByte((byte)(var1.getPrimaryHandItem() != null ? 1 : 0));
      var2.putInt(var1.OnlineID);
      if (var1.getPrimaryHandItem() != null) {
         try {
            var1.getPrimaryHandItem().save(var2.bb, false);
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      }

      var0.endPacketImmediate();
      var2 = var0.startPacket();
      PacketTypes.doPacket((short)25, var2);
      var2.putByte((byte)1);
      var2.putByte((byte)(var1.getSecondaryHandItem() != null ? 1 : 0));
      var2.putInt(var1.OnlineID);
      if (var1.getSecondaryHandItem() != null) {
         try {
            var1.getSecondaryHandItem().save(var2.bb, false);
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      }

      var0.endPacketImmediate();
   }

   public static void sendZombie(IsoZombie var0) {
      if (!bFastForward) {
         ZombieUpdatePacker.instance.addZombieToPacker(var0);
      }
   }

   public static void createZombie() {
   }

   public static void requestingChunk(ByteBufferReader var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      toCreateOnClient.clear();
      IsoChunk var4 = ServerMap.instance.getChunk(var2, var3);
      if (var4 != null) {
         for(int var5 = 0; var5 < 10; ++var5) {
            for(int var6 = 0; var6 < 10; ++var6) {
               for(int var7 = 0; var7 < 6; ++var7) {
                  IsoGridSquare var8 = var4.getGridSquare(var5, var6, var7);
                  if (var8 != null && !var8.getMovingObjects().isEmpty()) {
                     for(int var9 = 0; var9 < var8.getMovingObjects().size(); ++var9) {
                        IsoMovingObject var10 = (IsoMovingObject)var8.getMovingObjects().get(var9);
                        if (var10 instanceof IsoZombie) {
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public static void SyncCustomLightSwitchSettings(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      byte var5 = var0.get();
      IsoGridSquare var6 = ServerMap.instance.getGridSquare(var2, var3, var4);
      if (var6 != null && var5 >= 0 && var5 < var6.getObjects().size()) {
         if (var6.getObjects().get(var5) instanceof IsoLightSwitch) {
            ((IsoLightSwitch)var6.getObjects().get(var5)).receiveSyncCustomizedSettings(var0, var1);
         } else {
            DebugLog.log("Sync Lightswitch custom settings: found object not a instance of IsoLightSwitch, x,y,z=" + var2 + "," + var3 + "," + var4);
         }
      } else if (var6 != null) {
         DebugLog.log("Sync Lightswitch custom settings: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
      } else {
         DebugLog.log("Sync Lightswitch custom settings: sq is null x,y,z=" + var2 + "," + var3 + "," + var4);
      }

   }

   private static void sendAlarmClock_Player(short var0, long var1, boolean var3, int var4, int var5, boolean var6, UdpConnection var7) {
      ByteBufferWriter var8 = var7.startPacket();
      PacketTypes.doPacket((short)46, var8);
      var8.putShort(AlarmClock.PacketPlayer);
      var8.putShort(var0);
      var8.putLong(var1);
      var8.putByte((byte)(var3 ? 1 : 0));
      if (!var3) {
         var8.putInt(var4);
         var8.putInt(var5);
         var8.putByte((byte)(var6 ? 1 : 0));
      }

      var7.endPacket();
   }

   private static void sendAlarmClock_World(int var0, int var1, int var2, long var3, boolean var5, int var6, int var7, boolean var8, UdpConnection var9) {
      ByteBufferWriter var10 = var9.startPacket();
      PacketTypes.doPacket((short)46, var10);
      var10.putShort(AlarmClock.PacketWorld);
      var10.putInt(var0);
      var10.putInt(var1);
      var10.putInt(var2);
      var10.putLong(var3);
      var10.putByte((byte)(var5 ? 1 : 0));
      if (!var5) {
         var10.putInt(var6);
         var10.putInt(var7);
         var10.putByte((byte)(var8 ? 1 : 0));
      }

      var9.endPacket();
   }

   private static void SyncAlarmClock(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      int var10;
      if (var2 == AlarmClock.PacketPlayer) {
         short var16 = var0.getShort();
         long var17 = var0.getLong();
         boolean var18 = var0.get() == 1;
         int var7 = 0;
         int var19 = 0;
         boolean var20 = false;
         if (!var18) {
            var7 = var0.getInt();
            var19 = var0.getInt();
            var20 = var0.get() == 1;
         }

         for(var10 = 0; var10 < udpEngine.connections.size(); ++var10) {
            UdpConnection var21 = (UdpConnection)udpEngine.connections.get(var10);
            if (var21 != var1) {
               sendAlarmClock_Player(var16, var17, var18, var7, var19, var20, var21);
            }
         }

      } else if (var2 == AlarmClock.PacketWorld) {
         int var3 = var0.getInt();
         int var4 = var0.getInt();
         int var5 = var0.getInt();
         long var6 = var0.getLong();
         boolean var8 = var0.get() == 1;
         int var9 = 0;
         var10 = 0;
         boolean var11 = false;
         if (!var8) {
            var9 = var0.getInt();
            var10 = var0.getInt();
            var11 = var0.get() == 1;
         }

         IsoGridSquare var12 = ServerMap.instance.getGridSquare(var3, var4, var5);
         if (var12 == null) {
            DebugLog.log("SyncAlarmClock: sq is null x,y,z=" + var3 + "," + var4 + "," + var5);
         } else {
            AlarmClock var13 = null;

            int var14;
            for(var14 = 0; var14 < var12.getWorldObjects().size(); ++var14) {
               IsoWorldInventoryObject var15 = (IsoWorldInventoryObject)var12.getWorldObjects().get(var14);
               if (var15 != null && var15.getItem() instanceof AlarmClock && var15.getItem().id == var6) {
                  var13 = (AlarmClock)var15.getItem();
                  break;
               }
            }

            if (var13 == null) {
               DebugLog.log("SyncAlarmClock: AlarmClock is null x,y,z=" + var3 + "," + var4 + "," + var5);
            } else {
               if (var8) {
                  var13.stopRinging();
               } else {
                  var13.setHour(var9);
                  var13.setMinute(var10);
                  var13.setAlarmSet(var11);
               }

               for(var14 = 0; var14 < udpEngine.connections.size(); ++var14) {
                  UdpConnection var22 = (UdpConnection)udpEngine.connections.get(var14);
                  if (var22 != var1) {
                     sendAlarmClock_World(var3, var4, var5, var6, var8, var9, var10, var11, var22);
                  }
               }
            }

         }
      }
   }

   public static void SyncIsoObject(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      byte var5 = var0.get();
      byte var6 = var0.get();
      byte var7 = var0.get();
      if (var6 == 1) {
         IsoGridSquare var8 = ServerMap.instance.getGridSquare(var2, var3, var4);
         if (var8 != null && var5 >= 0 && var5 < var8.getObjects().size()) {
            ((IsoObject)var8.getObjects().get(var5)).syncIsoObject(true, var7, var1, var0);
         } else if (var8 != null) {
            DebugLog.log("SyncIsoObject: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
         } else {
            DebugLog.log("SyncIsoObject: sq is null x,y,z=" + var2 + "," + var3 + "," + var4);
         }

      }
   }

   public static void SyncIsoObjectReq(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      if (var2 <= 50 && var2 > 0) {
         ByteBufferWriter var3 = var1.startPacket();
         PacketTypes.doPacket((short)161, var3);
         var3.putShort(var2);

         for(int var4 = 0; var4 < var2; ++var4) {
            int var5 = var0.getInt();
            int var6 = var0.getInt();
            int var7 = var0.getInt();
            byte var8 = var0.get();
            IsoGridSquare var9 = ServerMap.instance.getGridSquare(var5, var6, var7);
            if (var9 != null && var8 >= 0 && var8 < var9.getObjects().size()) {
               ((IsoObject)var9.getObjects().get(var8)).syncIsoObjectSend(var3);
            } else if (var9 != null) {
               var3.putInt(var9.getX());
               var3.putInt(var9.getY());
               var3.putInt(var9.getZ());
               var3.putByte(var8);
               var3.putByte((byte)0);
               var3.putByte((byte)0);
            } else {
               var3.putInt(var5);
               var3.putInt(var6);
               var3.putInt(var7);
               var3.putByte(var8);
               var3.putByte((byte)2);
               var3.putByte((byte)0);
            }
         }

         var1.endPacketImmediate();
      }
   }

   public static void SyncObjectChunkHashes(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      if (var2 <= 10 && var2 > 0) {
         ByteBufferWriter var3 = var1.startPacket();
         PacketTypes.doPacket((short)164, var3);
         var3.putShort((short)2);
         int var4 = var3.bb.position();
         var3.putShort((short)0);
         int var5 = 0;

         int var6;
         for(var6 = 0; var6 < var2; ++var6) {
            int var7 = var0.getInt();
            int var8 = var0.getInt();
            long var9 = var0.getLong();
            IsoChunk var11 = ServerMap.instance.getChunk(var7, var8);
            if (var11 != null) {
               ++var5;
               var3.putShort((short)var11.wx);
               var3.putShort((short)var11.wy);
               var3.putLong(var11.getHashCodeObjects());
               int var12 = var3.bb.position();
               var3.putShort((short)0);
               int var13 = 0;

               int var14;
               for(var14 = var7 * 10; var14 < var7 * 10 + 10; ++var14) {
                  for(int var15 = var8 * 10; var15 < var8 * 10 + 10; ++var15) {
                     for(int var16 = 0; var16 <= 7; ++var16) {
                        IsoGridSquare var17 = ServerMap.instance.getGridSquare(var14, var15, var16);
                        if (var17 == null) {
                           break;
                        }

                        var3.putByte((byte)(var17.getX() - var11.wx * 10));
                        var3.putByte((byte)(var17.getY() - var11.wy * 10));
                        var3.putByte((byte)var17.getZ());
                        var3.putInt((int)var17.getHashCodeObjects());
                        ++var13;
                     }
                  }
               }

               var14 = var3.bb.position();
               var3.bb.position(var12);
               var3.putShort((short)var13);
               var3.bb.position(var14);
            }
         }

         var6 = var3.bb.position();
         var3.bb.position(var4);
         var3.putShort((short)var5);
         var3.bb.position(var6);
         var1.endPacketImmediate();
      }
   }

   public static void SyncObjectChunkHashes(IsoChunk var0, UdpConnection var1) {
      ByteBufferWriter var2 = var1.startPacket();
      PacketTypes.doPacket((short)164, var2);
      var2.putShort((short)2);
      var2.putShort((short)1);
      var2.putShort((short)var0.wx);
      var2.putShort((short)var0.wy);
      var2.putLong(var0.getHashCodeObjects());
      int var3 = var2.bb.position();
      var2.putShort((short)0);
      int var4 = 0;

      int var5;
      for(var5 = var0.wx * 10; var5 < var0.wx * 10 + 10; ++var5) {
         for(int var6 = var0.wy * 10; var6 < var0.wy * 10 + 10; ++var6) {
            for(int var7 = 0; var7 <= 7; ++var7) {
               IsoGridSquare var8 = ServerMap.instance.getGridSquare(var5, var6, var7);
               if (var8 == null) {
                  break;
               }

               var2.putByte((byte)(var8.getX() - var0.wx * 10));
               var2.putByte((byte)(var8.getY() - var0.wy * 10));
               var2.putByte((byte)var8.getZ());
               var2.putInt((int)var8.getHashCodeObjects());
               ++var4;
            }
         }
      }

      var5 = var2.bb.position();
      var2.bb.position(var3);
      var2.putShort((short)var4);
      var2.bb.position(var5);
      var1.endPacketImmediate();
   }

   public static void SyncObjectsGridSquareRequest(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      if (var2 <= 100 && var2 > 0) {
         ByteBufferWriter var3 = var1.startPacket();
         PacketTypes.doPacket((short)164, var3);
         var3.putShort((short)4);
         int var4 = var3.bb.position();
         var3.putShort((short)0);
         int var5 = 0;

         int var6;
         for(var6 = 0; var6 < var2; ++var6) {
            int var7 = var0.getInt();
            int var8 = var0.getInt();
            byte var9 = var0.get();
            IsoGridSquare var10 = ServerMap.instance.getGridSquare(var7, var8, var9);
            if (var10 != null) {
               ++var5;
               var3.putInt(var7);
               var3.putInt(var8);
               var3.putByte((byte)var9);
               var3.putByte((byte)var10.getObjects().size());
               var3.putInt(0);
               int var11 = var3.bb.position();

               int var12;
               for(var12 = 0; var12 < var10.getObjects().size(); ++var12) {
                  var3.putLong(((IsoObject)var10.getObjects().get(var12)).customHashCode());
               }

               var12 = var3.bb.position();
               var3.bb.position(var11 - 4);
               var3.putInt(var12);
               var3.bb.position(var12);
            }
         }

         var6 = var3.bb.position();
         var3.bb.position(var4);
         var3.putShort((short)var5);
         var3.bb.position(var6);
         var1.endPacketImmediate();
      }
   }

   public static void SyncObjectsRequest(ByteBuffer var0, UdpConnection var1) {
      short var2 = var0.getShort();
      if (var2 <= 100 && var2 > 0) {
         for(int var3 = 0; var3 < var2; ++var3) {
            int var4 = var0.getInt();
            int var5 = var0.getInt();
            byte var6 = var0.get();
            long var7 = var0.getLong();
            IsoGridSquare var9 = ServerMap.instance.getGridSquare(var4, var5, var6);
            if (var9 != null) {
               for(int var10 = 0; var10 < var9.getObjects().size(); ++var10) {
                  if (((IsoObject)var9.getObjects().get(var10)).customHashCode() == var7) {
                     ByteBufferWriter var11 = var1.startPacket();
                     PacketTypes.doPacket((short)164, var11);
                     var11.putShort((short)6);
                     var11.putInt(var4);
                     var11.putInt(var5);
                     var11.putByte((byte)var6);
                     var11.putLong(var7);
                     var11.putByte((byte)var9.getObjects().size());

                     for(int var12 = 0; var12 < var9.getObjects().size(); ++var12) {
                        var11.putLong(((IsoObject)var9.getObjects().get(var12)).customHashCode());
                     }

                     try {
                        ((IsoObject)var9.getObjects().get(var10)).writeToRemoteBuffer(var11);
                     } catch (Throwable var13) {
                        DebugLog.log("ERROR: GameServer.SyncObjectsRequest " + var13.getMessage());
                        var1.cancelPacket();
                        break;
                     }

                     var1.endPacketImmediate();
                     break;
                  }
               }
            }
         }

      }
   }

   public static void SyncDoorKey(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      byte var5 = var0.get();
      int var6 = var0.getInt();
      IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var7 != null && var5 >= 0 && var5 < var7.getObjects().size()) {
         IsoObject var8 = (IsoObject)var7.getObjects().get(var5);
         if (var8 instanceof IsoDoor) {
            IsoDoor var9 = (IsoDoor)var8;
            var9.keyId = var6;

            for(int var11 = 0; var11 < udpEngine.connections.size(); ++var11) {
               UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var11);
               if (var12.getConnectedGUID() != var1.getConnectedGUID()) {
                  ByteBufferWriter var10 = var12.startPacket();
                  PacketTypes.doPacket((short)106, var10);
                  var10.putInt(var2);
                  var10.putInt(var3);
                  var10.putInt(var4);
                  var10.putByte(var5);
                  var10.putInt(var6);
                  var12.endPacketImmediate();
               }
            }

         } else {
            DebugLog.log("SyncDoorKey: expected IsoDoor index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
         }
      } else if (var7 != null) {
         DebugLog.log("SyncDoorKey: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
      } else {
         DebugLog.log("SyncDoorKey: sq is null x,y,z=" + var2 + "," + var3 + "," + var4);
      }
   }

   public static void SyncThumpable(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      byte var5 = var0.get();
      int var6 = var0.getInt();
      byte var7 = var0.get();
      int var8 = var0.getInt();
      IsoGridSquare var9 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var9 != null && var5 >= 0 && var5 < var9.getObjects().size()) {
         IsoObject var10 = (IsoObject)var9.getObjects().get(var5);
         if (var10 instanceof IsoThumpable) {
            IsoThumpable var11 = (IsoThumpable)var10;
            var11.lockedByCode = var6;
            var11.lockedByPadlock = var7 == 1;
            var11.keyId = var8;

            for(int var13 = 0; var13 < udpEngine.connections.size(); ++var13) {
               UdpConnection var14 = (UdpConnection)udpEngine.connections.get(var13);
               if (var14.getConnectedGUID() != var1.getConnectedGUID()) {
                  ByteBufferWriter var12 = var14.startPacket();
                  PacketTypes.doPacket((short)105, var12);
                  var12.putInt(var2);
                  var12.putInt(var3);
                  var12.putInt(var4);
                  var12.putByte(var5);
                  var12.putInt(var6);
                  var12.putByte(var7);
                  var12.putInt(var8);
                  var14.endPacketImmediate();
               }
            }

         } else {
            DebugLog.log("SyncThumpable: expected IsoThumpable index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
         }
      } else if (var9 != null) {
         DebugLog.log("SyncThumpable: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
      } else {
         DebugLog.log("SyncThumpable: sq is null x,y,z=" + var2 + "," + var3 + "," + var4);
      }
   }

   private static void RemoveItemFromMap(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var6 != null && var5 >= 0 && var5 < var6.getObjects().size()) {
         IsoObject var7 = (IsoObject)var6.getObjects().get(var5);
         if (!(var7 instanceof IsoWorldInventoryObject)) {
            IsoRegion.setPreviousFlags(var6);
         }

         DebugLog.log(DebugType.Objects, "object: removing " + var7 + " index=" + var5 + " " + var2 + "," + var3 + "," + var4);
         if (var7 instanceof IsoWorldInventoryObject) {
            LoggerManager.getLogger("item").write(var1.idStr + " \"" + var1.username + "\" floor -1 " + var2 + "," + var3 + "," + var4 + " [" + ((IsoWorldInventoryObject)var7).getItem().getFullType() + "]");
         } else {
            String var8 = var7.getName() != null ? var7.getName() : var7.getObjectName();
            if (var7.getSprite() != null && var7.getSprite().getName() != null) {
               var8 = var8 + " (" + var7.getSprite().getName() + ")";
            }

            LoggerManager.getLogger("map").write(var1.idStr + " \"" + var1.username + "\" removed " + var8 + " at " + var2 + "," + var3 + "," + var4);
         }

         int var11;
         if (var7.isTableSurface()) {
            for(var11 = var5 + 1; var11 < var6.getObjects().size(); ++var11) {
               IsoObject var9 = (IsoObject)var6.getObjects().get(var11);
               if (var9.isTableTopObject() || var9.isTableSurface()) {
                  var9.setRenderYOffset(var9.getRenderYOffset() - var7.getSurfaceOffset());
               }
            }
         }

         if (!(var7 instanceof IsoWorldInventoryObject)) {
            LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", var7);
         }

         if (!var6.getObjects().contains(var7)) {
            throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
         }

         var7.removeFromWorld();
         var7.removeFromSquare();
         var6.RecalcAllWithNeighbours(true);
         if (!(var7 instanceof IsoWorldInventoryObject)) {
            IsoWorld.instance.CurrentCell.checkHaveRoof(var2, var3);
            MapCollisionData.instance.squareChanged(var6);
            PolygonalMap2.instance.squareChanged(var6);
            ServerMap.instance.physicsCheck(var2, var3);
            IsoRegion.squareChanged(var6, true);
         }

         for(var11 = 0; var11 < udpEngine.connections.size(); ++var11) {
            UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var11);
            if (var12.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var10 = var12.startPacket();
               PacketTypes.doPacket((short)23, var10);
               var10.putInt(var2);
               var10.putInt(var3);
               var10.putInt(var4);
               var10.putInt(var5);
               var12.endPacketImmediate();
            }
         }

         var6.revisionUp();
      }

   }

   public static int RemoveItemFromMap(IsoObject var0) {
      int var1 = var0.getSquare().getX();
      int var2 = var0.getSquare().getY();
      int var3 = var0.getSquare().getZ();
      int var4 = var0.getSquare().getObjects().indexOf(var0);
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
      if (var5 != null && !(var0 instanceof IsoWorldInventoryObject)) {
         IsoRegion.setPreviousFlags(var5);
      }

      LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", var0);
      if (!var0.getSquare().getObjects().contains(var0)) {
         throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
      } else {
         var0.removeFromWorld();
         var0.removeFromSquare();
         if (var5 != null) {
            var5.RecalcAllWithNeighbours(true);
         }

         if (!(var0 instanceof IsoWorldInventoryObject)) {
            IsoWorld.instance.CurrentCell.checkHaveRoof(var1, var2);
            MapCollisionData.instance.squareChanged(var5);
            PolygonalMap2.instance.squareChanged(var5);
            ServerMap.instance.physicsCheck(var1, var2);
            IsoRegion.squareChanged(var5, true);
         }

         var5.revisionUp();

         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.ReleventTo((float)var1, (float)var2)) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)23, var8);
               var8.putInt(var1);
               var8.putInt(var2);
               var8.putInt(var3);
               var8.putInt(var4);
               var7.endPacketImmediate();
            }
         }

         return var4;
      }
   }

   public static void doZombieDie(IsoZombie var0, IsoGameCharacter var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         ByteBufferWriter var4 = var3.startPacket();
         PacketTypes.doPacket((short)29, var4);
         var4.putInt(var0.OnlineID);
         var4.putShort((short)((IsoPlayer)var1).OnlineID);
         var3.endPacketImmediate();
      }

   }

   public static void sendBloodSplatter(HandWeapon var0, float var1, float var2, float var3, Vector2 var4, boolean var5, boolean var6) {
      for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
         UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
         ByteBufferWriter var9 = var8.startPacket();
         PacketTypes.doPacket((short)60, var9);
         var9.putUTF(var0 != null ? var0.getType() : "");
         var9.putFloat(var1);
         var9.putFloat(var2);
         var9.putFloat(var3);
         var9.putFloat(var4.getX());
         var9.putFloat(var4.getY());
         var9.putByte((byte)(var5 ? 1 : 0));
         var9.putByte((byte)(var6 ? 1 : 0));
         byte var10 = 0;
         if (var0 != null) {
            var10 = (byte)Math.max(var0.getSplatNumber(), 1);
         }

         var9.putByte(var10);
         var8.endPacketImmediate();
      }

   }

   public static void AddItemToMap(ByteBuffer var0, UdpConnection var1) {
      IsoObject var2 = WorldItemTypes.createFromBuffer(var0);
      if (var2 instanceof IsoFire && ServerOptions.instance.NoFire.getValue()) {
         DebugLog.log("user \"" + var1.username + "\" tried to start a fire");
      } else {
         var2.loadFromRemoteBuffer(var0);
         if (var2.square != null) {
            DebugLog.log(DebugType.Objects, "object: added " + var2 + " index=" + var2.getObjectIndex() + " " + var2.getX() + "," + var2.getY() + "," + var2.getZ());
            if (var2 instanceof IsoWorldInventoryObject) {
               LoggerManager.getLogger("item").write(var1.idStr + " \"" + var1.username + "\" floor +1 " + (int)var2.getX() + "," + (int)var2.getY() + "," + (int)var2.getZ() + " [" + ((IsoWorldInventoryObject)var2).getItem().getFullType() + "]");
            } else {
               String var3 = var2.getName() != null ? var2.getName() : var2.getObjectName();
               if (var2.getSprite() != null && var2.getSprite().getName() != null) {
                  var3 = var3 + " (" + var2.getSprite().getName() + ")";
               }

               LoggerManager.getLogger("map").write(var1.idStr + " \"" + var1.username + "\" added " + var3 + " at " + var2.getX() + "," + var2.getY() + "," + var2.getZ());
            }

            var2.addToWorld();
            var2.square.RecalcProperties();
            if (!(var2 instanceof IsoWorldInventoryObject)) {
               var2.square.restackSheetRope();
               IsoWorld.instance.CurrentCell.checkHaveRoof(var2.square.getX(), var2.square.getY());
               MapCollisionData.instance.squareChanged(var2.square);
               PolygonalMap2.instance.squareChanged(var2.square);
               ServerMap.instance.physicsCheck(var2.square.x, var2.square.y);
               IsoRegion.squareChanged(var2.square);
            }

            for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
               UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var6);
               if (var4.getConnectedGUID() != var1.getConnectedGUID() && var4.ReleventTo((float)var2.square.x, (float)var2.square.y)) {
                  ByteBufferWriter var5 = var4.startPacket();
                  PacketTypes.doPacket((short)17, var5);
                  var2.writeToRemoteBuffer(var5);
                  var4.endPacketImmediate();
               }
            }

            if (!(var2 instanceof IsoWorldInventoryObject)) {
               LuaEventManager.triggerEvent("OnObjectAdded", var2);
            } else {
               ((IsoWorldInventoryObject)var2).dropTime = GameTime.getInstance().getWorldAgeHours();
            }
         } else if (bDebug) {
            DebugLog.log("AddItemToMap: sq is null");
         }

      }
   }

   public static void sendDeleteZombie(IsoZombie var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         if (var2.ReleventTo(var0.x, var0.y)) {
            ByteBufferWriter var3 = var2.startPacket();
            PacketTypes.doPacket((short)30, var3);
            var3.putShort(var0.OnlineID);
            var2.endPacketImmediate();
         }
      }

   }

   public static void disconnect(UdpConnection var0) {
      if (var0.playerDownloadServer != null) {
         try {
            var0.playerDownloadServer.destroy();
         } catch (Exception var3) {
            var3.printStackTrace();
         }

         var0.playerDownloadServer = null;
      }

      int var1;
      for(var1 = 0; var1 < 4; ++var1) {
         IsoPlayer var2 = var0.players[var1];
         if (var2 != null) {
            ChatServer.getInstance().disconnectPlayer(var0.playerIDs[var1]);
            disconnectPlayer(var2, var0);
         }

         var0.usernames[var1] = null;
         var0.players[var1] = null;
         var0.playerIDs[var1] = -1;
         var0.ReleventPos[var1] = null;
         var0.connectArea[var1] = null;
      }

      for(var1 = 0; var1 < udpEngine.getMaxConnections(); ++var1) {
         if (SlotToConnection[var1] == var0) {
            SlotToConnection[var1] = null;
         }
      }

      Iterator var4 = IDToAddressMap.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         if ((Long)var5.getValue() == var0.getConnectedGUID()) {
            var4.remove();
         }
      }

      if (!SteamUtils.isSteamModeEnabled()) {
         PublicServerUtil.updatePlayers();
      }

      if (CoopSlave.instance != null && var0.isCoopHost) {
         DebugLog.log("Host user disconnected, stopping the server");
         ServerMap.instance.QueueQuit();
      }

   }

   public static void addIncoming(short var0, ByteBuffer var1, UdpConnection var2) {
      ZomboidNetData var3 = null;
      if (var1.limit() > 2048) {
         var3 = ZomboidNetDataPool.instance.getLong(var1.limit());
      } else {
         var3 = ZomboidNetDataPool.instance.get();
      }

      var3.read(var0, var1, var2);
      var3.time = System.currentTimeMillis();
      synchronized(MainLoopNetData) {
         MainLoopNetData.add(var3);
      }
   }

   public static void smashWindow(IsoWindow var0, int var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         if (var3.ReleventTo(var0.getX(), var0.getY())) {
            ByteBufferWriter var4 = var3.startPacket();
            PacketTypes.doPacket((short)32, var4);
            var4.putInt(var0.square.getX());
            var4.putInt(var0.square.getY());
            var4.putInt(var0.square.getZ());
            var4.putByte((byte)var0.square.getObjects().indexOf(var0));
            var4.putByte((byte)var1);
            var3.endPacketImmediate();
         }
      }

   }

   public static void SendDeath(IsoPlayer var0) {
      var0.getBodyDamage().setOverallBodyHealth(-1.0F);

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         ByteBufferWriter var3 = var2.startPacket();
         PacketTypes.doPacket((short)33, var3);
         var3.putInt(var0.OnlineID);
         var2.endPacketImmediate();
      }

   }

   public static void SendOnBeaten(IsoPlayer var0, float var1, float var2, float var3) {
      var0.getBodyDamage().setOverallBodyHealth(-1.0F);

      for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
         UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
         ByteBufferWriter var6 = var5.startPacket();
         PacketTypes.doPacket((short)165, var6);
         var6.putInt(var0.OnlineID);
         var6.putFloat(var1);
         var6.putFloat(var2);
         var6.putFloat(var3);
         var5.endPacketImmediate();
      }

   }

   public static boolean doSendZombies() {
      return SendZombies == 0;
   }

   public static void sendDeadZombie(IsoZombie var0) {
      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);
         if (var2.ReleventTo(var0.x, var0.y)) {
            ByteBufferWriter var3 = var2.startPacket();
            PacketTypes.doPacket((short)39, var3);
            var3.putShort(var0.OnlineID);
            var3.putFloat(var0.getX());
            var3.putFloat(var0.getY());
            var3.putFloat(var0.getZ());
            if (var0.getInventory() != null) {
               var3.putByte((byte)1);

               try {
                  ArrayList var4 = var0.getInventory().save(var3.bb, false);
                  var3.bb.putShort((short)var4.indexOf(var0.getClothingItem_Torso()));
                  var3.bb.putShort((short)var4.indexOf(var0.getClothingItem_Legs()));
                  var3.bb.putShort((short)var4.indexOf(var0.getClothingItem_Feet()));
               } catch (IOException var5) {
                  var5.printStackTrace();
               }
            } else {
               var3.putByte((byte)0);
            }

            var2.endPacketImmediate();
         }
      }

   }

   public static void doDamage(IsoGameCharacter var0, float var1) {
      if (var0 != null) {
         if (PlayerToAddressMap.containsKey((IsoPlayer)var0)) {
            long var2 = (Long)PlayerToAddressMap.get((IsoPlayer)var0);
            UdpConnection var4 = udpEngine.getActiveConnection(var2);
            if (var4 != null) {
               ByteBufferWriter var5 = var4.startPacket();
               IsoPlayer var6 = (IsoPlayer)var0;
               PacketTypes.doPacket((short)41, var5);
               var5.putShort((short)var6.OnlineID);

               try {
                  var6.getBodyDamage().save(var5.bb);
               } catch (IOException var8) {
                  var8.printStackTrace();
               }

               var5.putFloat(var1);
               var4.endPacketImmediate();
            }
         }
      }
   }

   private static void sendStartRain(UdpConnection var0) {
      ByteBufferWriter var1 = var0.startPacket();
      PacketTypes.doPacket((short)77, var1);
      var1.putInt(RainManager.randRainMin);
      var1.putInt(RainManager.randRainMax);
      var1.putFloat(RainManager.RainDesiredIntensity);
      var0.endPacketImmediate();
   }

   public static void startRain() {
      if (udpEngine != null) {
         for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
            UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
            sendStartRain(var1);
         }

      }
   }

   private static void sendStopRain(UdpConnection var0) {
      ByteBufferWriter var1 = var0.startPacket();
      PacketTypes.doPacket((short)78, var1);
      var0.endPacketImmediate();
   }

   public static void stopRain() {
      for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
         UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
         sendStopRain(var1);
      }

   }

   private static void sendWeather(UdpConnection var0) {
      GameTime var1 = GameTime.getInstance();
      ByteBufferWriter var2 = var0.startPacket();
      PacketTypes.doPacket((short)64, var2);
      var2.putByte((byte)var1.getDawn());
      var2.putByte((byte)var1.getDusk());
      var2.putByte((byte)(var1.isThunderDay() ? 1 : 0));
      var2.putFloat(var1.Moon);
      var2.putFloat(var1.getAmbientMin());
      var2.putFloat(var1.getAmbientMax());
      var2.putFloat(var1.getViewDistMin());
      var2.putFloat(var1.getViewDistMax());
      var2.putFloat(IsoWorld.instance.getGlobalTemperature());
      var2.putUTF(IsoWorld.instance.getWeather());
      ErosionMain.getInstance().sendState(var2.bb);
      var0.endPacketImmediate();
   }

   public static void sendWeather() {
      for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
         UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
         sendWeather(var1);
      }

   }

   private static void syncClock(UdpConnection var0) {
      GameTime var1 = GameTime.getInstance();
      ByteBufferWriter var2 = var0.startPacket();
      PacketTypes.doPacket((short)19, var2);
      var2.putBoolean(bFastForward);
      var2.putFloat(var1.getTimeOfDay());
      var0.endPacketImmediate();
   }

   public static void syncClock() {
      for(int var0 = 0; var0 < udpEngine.connections.size(); ++var0) {
         UdpConnection var1 = (UdpConnection)udpEngine.connections.get(var0);
         syncClock(var1);
      }

   }

   public static void sendServerCommand(String var0, String var1, KahluaTable var2, UdpConnection var3) {
      ByteBufferWriter var4 = var3.startPacket();
      PacketTypes.doPacket((short)57, var4);
      var4.putUTF(var0);
      var4.putUTF(var1);
      if (var2 != null && !var2.isEmpty()) {
         var4.putByte((byte)1);

         try {
            KahluaTableIterator var5 = var2.iterator();

            while(var5.advance()) {
               if (!TableNetworkUtils.canSave(var5.getKey(), var5.getValue())) {
                  DebugLog.log("ERROR: sendServerCommand: can't save key,value=" + var5.getKey() + "," + var5.getValue());
               }
            }

            TableNetworkUtils.save(var2, var4.bb);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      } else {
         var4.putByte((byte)0);
      }

      var3.endPacketImmediate();
   }

   public static void sendServerCommand(String var0, String var1, KahluaTable var2) {
      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         sendServerCommand(var0, var1, var2, var4);
      }

   }

   public static void sendServerCommandV(String var0, String var1, Object... var2) {
      if (var2.length == 0) {
         sendServerCommand(var0, var1, (KahluaTable)null);
      } else if (var2.length % 2 != 0) {
         DebugLog.log("ERROR: sendServerCommand called with invalid number of arguments (" + var0 + " " + var1 + ")");
      } else {
         KahluaTable var3 = LuaManager.platform.newTable();

         for(int var4 = 0; var4 < var2.length; var4 += 2) {
            Object var5 = var2[var4 + 1];
            if (var5 instanceof Float) {
               var3.rawset(var2[var4], ((Float)var5).doubleValue());
            } else if (var5 instanceof Integer) {
               var3.rawset(var2[var4], ((Integer)var5).doubleValue());
            } else if (var5 instanceof Short) {
               var3.rawset(var2[var4], ((Short)var5).doubleValue());
            } else {
               var3.rawset(var2[var4], var5);
            }
         }

         sendServerCommand(var0, var1, var3);
      }
   }

   public static void sendServerCommand(IsoPlayer var0, String var1, String var2, KahluaTable var3) {
      if (PlayerToAddressMap.containsKey(var0)) {
         long var4 = (Long)PlayerToAddressMap.get(var0);
         UdpConnection var6 = udpEngine.getActiveConnection(var4);
         if (var6 != null) {
            sendServerCommand(var1, var2, var3, var6);
         }
      }
   }

   public static ArrayList getPlayers() {
      ArrayList var0 = new ArrayList();

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);

         for(int var3 = 0; var3 < 4; ++var3) {
            IsoPlayer var4 = var2.players[var3];
            if (var4 != null && var4.OnlineID != -1) {
               var0.add(var4);
            }
         }
      }

      return var0;
   }

   public static int getPlayerCount() {
      int var0 = 0;

      for(int var1 = 0; var1 < udpEngine.connections.size(); ++var1) {
         UdpConnection var2 = (UdpConnection)udpEngine.connections.get(var1);

         for(int var3 = 0; var3 < 4; ++var3) {
            if (var2.playerIDs[var3] != -1) {
               ++var0;
            }
         }
      }

      return var0;
   }

   public static void sendAmbient(String var0, int var1, int var2, int var3, float var4) {
      DebugLog.log(DebugType.Sound, "ambient: sending " + var0 + " at " + var1 + "," + var2 + " radius=" + var3);

      for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
         UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
         IsoPlayer var7 = getAnyPlayerFromConnection(var6);
         if (var7 != null) {
            ByteBufferWriter var8 = var6.startPacket();
            PacketTypes.doPacket((short)55, var8);
            var8.putUTF(var0);
            var8.putInt(var1);
            var8.putInt(var2);
            var8.putInt(var3);
            var8.putFloat(var4);
            var6.endPacketImmediate();
         }
      }

   }

   public static void toggleSafety(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      IsoPlayer var3 = getPlayerFromConnection(var1, var2);
      if (var3 != null) {
         var3.setSafety(!var3.isSafety());
         if (var3.isSafety()) {
            LoggerManager.getLogger("pvp").write("user " + var3.username + " " + LoggerManager.getPlayerCoords(var3) + " enabled safety");
         } else {
            LoggerManager.getLogger("pvp").write("user " + var3.username + " " + LoggerManager.getPlayerCoords(var3) + " disabled safety");
         }

         for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
            UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
            if (var5.getConnectedGUID() != var1.getConnectedGUID()) {
               ByteBufferWriter var6 = var5.startPacket();
               PacketTypes.doPacket((short)86, var6);
               var6.putInt(var3.OnlineID);
               var6.putByte((byte)(var3.isSafety() ? 1 : 0));
               var5.endPacketImmediate();
            }
         }

      }
   }

   public static void updateOverlayForClients(IsoObject var0, String var1, float var2, float var3, float var4, float var5, UdpConnection var6) {
      if (udpEngine != null) {
         var0.revisionUp();

         for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
            UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
            if (var8 != null && var0.square != null && var8.ReleventTo((float)var0.square.x, (float)var0.square.y) && (var6 == null || var8.getConnectedGUID() != var6.getConnectedGUID())) {
               ByteBufferWriter var9 = var8.startPacket();
               PacketTypes.doPacket((short)90, var9);
               GameWindow.WriteStringUTF(var9.bb, var1);
               var9.putInt(var0.getSquare().getX());
               var9.putInt(var0.getSquare().getY());
               var9.putInt(var0.getSquare().getZ());
               var9.putFloat(var2);
               var9.putFloat(var3);
               var9.putFloat(var4);
               var9.putFloat(var5);
               var9.putInt(var0.getSquare().getObjects().indexOf(var0));
               var8.endPacketImmediate();
            }
         }

      }
   }

   private static void updateOverlayFromClient(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadStringUTF(var0);
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      float var6 = var0.getFloat();
      float var7 = var0.getFloat();
      float var8 = var0.getFloat();
      float var9 = var0.getFloat();
      int var10 = var0.getInt();
      IsoGridSquare var11 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, var5);
      if (var11 != null && var10 < var11.getObjects().size()) {
         try {
            IsoObject var12 = (IsoObject)var11.getObjects().get(var10);
            if (var12 != null && var12.setOverlaySprite(var2, var6, var7, var8, var9, false)) {
               updateOverlayForClients(var12, var2, var6, var7, var8, var9, var1);
            }
         } catch (Exception var13) {
         }
      }

   }

   private static void copyFile(File var0, File var1) throws IOException {
      if (!var1.exists()) {
         var1.createNewFile();
      }

      FileChannel var2 = null;
      FileChannel var3 = null;

      try {
         var2 = (new FileInputStream(var0)).getChannel();
         var3 = (new FileOutputStream(var1)).getChannel();
         var3.transferFrom(var2, 0L, var2.size());
      } finally {
         if (var2 != null) {
            var2.close();
         }

         if (var3 != null) {
            var3.close();
         }

      }

   }

   public static void sendReanimatedZombieID(IsoPlayer var0, IsoZombie var1) {
      if (PlayerToAddressMap.containsKey(var0)) {
         sendObjectChange(var0, "reanimatedID", (Object[])("ID", (double)var1.OnlineID));
      }

   }

   public static void RemoveSpecialObjectFromSquare(IsoObject var0) {
      int var1 = var0.getSquare().getX();
      int var2 = var0.getSquare().getY();
      int var3 = var0.getSquare().getZ();
      int var4 = var0.getSquare().getSpecialObjects().indexOf(var0);
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
      if (var5 != null) {
         if (var5.getCell().getProcessIsoObjects().contains(var0)) {
            var5.getCell().getProcessIsoObjectRemove().add(var0);
         }

         var0.removeFromWorld();
         var0.removeFromSquare();

         for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
            UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
            if (var7.ReleventTo((float)var0.square.x, (float)var0.square.y)) {
               ByteBufferWriter var8 = var7.startPacket();
               PacketTypes.doPacket((short)111, var8);
               var8.putInt(var1);
               var8.putInt(var2);
               var8.putInt(var3);
               var8.putInt(var4);
               var7.endPacketImmediate();
            }
         }
      }

   }

   private static void removeSpecialObject(ByteBuffer var0, UdpConnection var1) {
      if (IsoWorld.instance.CurrentCell != null) {
         int var2 = var0.getInt();
         int var3 = var0.getInt();
         int var4 = var0.getInt();
         int var5 = var0.getInt();
         IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
         if (var6 != null && var5 >= 0 && var5 < var6.getSpecialObjects().size()) {
            IsoObject var7 = (IsoObject)var6.getSpecialObjects().get(var5);
            var7.removeFromWorld();
            var7.removeFromSquare();
            if (var6.getCell().getProcessIsoObjects().contains(var7)) {
               var6.getCell().getProcessIsoObjectRemove().add(var7);
            }

            for(int var8 = 0; var8 < udpEngine.connections.size(); ++var8) {
               UdpConnection var9 = (UdpConnection)udpEngine.connections.get(var8);
               if (var9.ReleventTo((float)var7.square.x, (float)var7.square.y) && var9.getConnectedGUID() != var1.getConnectedGUID()) {
                  ByteBufferWriter var10 = var9.startPacket();
                  PacketTypes.doPacket((short)111, var10);
                  var10.putInt(var2);
                  var10.putInt(var3);
                  var10.putInt(var4);
                  var10.putInt(var5);
                  var9.endPacketImmediate();
               }
            }
         } else if (bDebug) {
            DebugLog.log("RemoveSpecialObject: sq is null or index is invalid");
         }

      }
   }

   private static void syncSafehouse(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      String var6 = GameWindow.ReadString(var0);
      int var7 = var0.getInt();
      SafeHouse var8 = SafeHouse.getSafeHouse(var2, var3, var4, var5);
      boolean var9 = false;
      if (var8 == null) {
         var8 = SafeHouse.addSafeHouse(var2, var3, var4, var5, var6, false);
         var9 = true;
      }

      if (var8 != null) {
         var8.getPlayers().clear();

         for(int var10 = 0; var10 < var7; ++var10) {
            String var11 = GameWindow.ReadString(var0);
            var8.addPlayer(var11);
         }

         boolean var12 = var0.get() == 1;
         var8.setTitle(GameWindow.ReadString(var0));
         var8.setOwner(var6);
         sendSafehouse(var8, var12, var1);
         if (ChatServer.isInited()) {
            if (var9) {
               ChatServer.getInstance().createSafehouseChat(var8.getId());
            }

            if (var12) {
               ChatServer.getInstance().removeSafehouseChat(var8.getId());
            } else {
               ChatServer.getInstance().syncSafehouseChatMembers(var8.getId(), var6, var8.getPlayers());
            }
         }

         if (var12) {
            SafeHouse.getSafehouseList().remove(var8);
            DebugLog.log("safehouse: removed " + var2 + "," + var3 + "," + var4 + "," + var5 + " owner=" + var8.getOwner());
         }

      }
   }

   public static void sendSafehouse(SafeHouse var0, boolean var1, UdpConnection var2) {
      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var2 == null || var4.getConnectedGUID() != var2.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)114, var5);
            var5.putInt(var0.getX());
            var5.putInt(var0.getY());
            var5.putInt(var0.getW());
            var5.putInt(var0.getH());
            var5.putUTF(var0.getOwner());
            var5.putInt(var0.getPlayers().size());
            Iterator var6 = var0.getPlayers().iterator();

            while(var6.hasNext()) {
               String var7 = (String)var6.next();
               var5.putUTF(var7);
            }

            var5.putBoolean(var1);
            var5.putUTF(var0.getTitle());
            var4.endPacketImmediate();
         }
      }

   }

   private static void dealWithNetDataShort(ZomboidNetData var0, ByteBuffer var1, UdpConnection var2) {
      short var3 = var1.getShort();
      switch(var3) {
      case 1000:
         receiveWaveSignal(var1);
         break;
      case 1001:
         receivePlayerListensChannel(var1);
         break;
      case 1002:
         sendRadioServerData(var2);
         break;
      case 1004:
         receiveRadioDeviceDataState(var1, var2);
         break;
      case 1200:
         SyncCustomLightSwitchSettings(var1, var2);
      }

   }

   public static void receiveRadioDeviceDataState(ByteBuffer var0, UdpConnection var1) {
      byte var2 = var0.get();
      if (var2 == 1) {
         int var3 = var0.getInt();
         int var4 = var0.getInt();
         int var5 = var0.getInt();
         int var6 = var0.getInt();
         IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, var5);
         if (var7 != null && var6 >= 0 && var6 < var7.getObjects().size()) {
            IsoObject var8 = (IsoObject)var7.getObjects().get(var6);
            if (var8 instanceof IsoWaveSignal) {
               DeviceData var9 = ((IsoWaveSignal)var8).getDeviceData();
               if (var9 != null) {
                  try {
                     var9.receiveDeviceDataStatePacket(var0, (UdpConnection)null);
                  } catch (Exception var13) {
                     System.out.print(var13.getMessage());
                  }
               }
            }
         }
      } else if (var2 == 0) {
         byte var14 = var0.get();
         IsoPlayer var16 = getPlayerFromConnection(var1, var14);
         byte var18 = var0.get();
         if (var16 != null) {
            Radio var20 = null;
            if (var18 == 1 && var16.getPrimaryHandItem() instanceof Radio) {
               var20 = (Radio)var16.getPrimaryHandItem();
            }

            if (var18 == 2 && var16.getSecondaryHandItem() instanceof Radio) {
               var20 = (Radio)var16.getSecondaryHandItem();
            }

            if (var20 != null && var20.getDeviceData() != null) {
               try {
                  var20.getDeviceData().receiveDeviceDataStatePacket(var0, var1);
               } catch (Exception var12) {
                  System.out.print(var12.getMessage());
               }
            }
         }
      } else if (var2 == 2) {
         short var15 = var0.getShort();
         short var17 = var0.getShort();
         BaseVehicle var19 = VehicleManager.instance.getVehicleByID(var15);
         if (var19 != null) {
            VehiclePart var22 = var19.getPartByIndex(var17);
            if (var22 != null) {
               DeviceData var21 = var22.getDeviceData();
               if (var21 != null) {
                  try {
                     var21.receiveDeviceDataStatePacket(var0, (UdpConnection)null);
                  } catch (Exception var11) {
                     System.out.print(var11.getMessage());
                  }
               }
            }
         }
      }

   }

   private static void sendRadioServerData(UdpConnection var0) {
      ByteBufferWriter var1 = var0.startPacket();
      PacketTypesShort.doPacket((short)1002, var1);
      ZomboidRadio.getInstance().WriteRadioServerDataPacket(var1);
      var0.endPacketImmediate();
   }

   public static void sendIsoWaveSignal(int var0, int var1, int var2, ChatMessage var3, String var4, float var5, float var6, float var7, int var8, boolean var9) {
      for(int var10 = 0; var10 < udpEngine.connections.size(); ++var10) {
         UdpConnection var11 = (UdpConnection)udpEngine.connections.get(var10);
         ByteBufferWriter var12 = var11.startPacket();
         PacketTypesShort.doPacket((short)1000, var12);
         var12.putInt(var0);
         var12.putInt(var1);
         var12.putInt(var2);
         var12.putBoolean(var3 != null);
         if (var3 != null) {
            var3.pack(var12);
         }

         var12.putByte((byte)(var4 != null ? 1 : 0));
         if (var4 != null) {
            var12.putUTF(var4);
         }

         var12.putFloat(var5);
         var12.putFloat(var6);
         var12.putFloat(var7);
         var12.putInt(var8);
         var12.putByte((byte)(var9 ? 1 : 0));
         var11.endPacketImmediate();
      }

   }

   public static void receiveWaveSignal(ByteBuffer var0) {
      int var1 = var0.getInt();
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      boolean var4 = var0.get() == 1;
      ChatMessage var5 = null;
      if (var4) {
         var5 = ChatServer.getInstance().unpackChatMessage(var0);
      }

      String var6 = null;
      if (var0.get() == 1) {
         var6 = GameWindow.ReadString(var0);
      }

      float var7 = var0.getFloat();
      float var8 = var0.getFloat();
      float var9 = var0.getFloat();
      int var10 = var0.getInt();
      boolean var11 = var0.get() == 1;
      ZomboidRadio.getInstance().ReceiveTransmission(var1, var2, var3, var5, var6, var7, var8, var9, var10, var11);
   }

   public static void receivePlayerListensChannel(ByteBuffer var0) {
      int var1 = var0.getInt();
      boolean var2 = var0.get() == 1;
      boolean var3 = var0.get() == 1;
      ZomboidRadio.getInstance().PlayerListensChannel(var1, var2, var3);
   }

   public String getPoisonousBerry() {
      return this.poisonousBerry;
   }

   public void setPoisonousBerry(String var1) {
      this.poisonousBerry = var1;
   }

   public String getPoisonousMushroom() {
      return this.poisonousMushroom;
   }

   public void setPoisonousMushroom(String var1) {
      this.poisonousMushroom = var1;
   }

   public String getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(String var1) {
      this.difficulty = var1;
   }

   public static void sendAlarm(int var0, int var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         IsoPlayer var4 = getAnyPlayerFromConnection(var3);
         if (var4 != null) {
            ByteBufferWriter var5 = var3.startPacket();
            PacketTypes.doPacket((short)118, var5);
            var5.putInt(var0);
            var5.putInt(var1);
            var3.endPacketImmediate();
         }
      }

   }

   private static void parseSpawnRegions() {
      if (SpawnRegions != null) {
         SpawnPoints = new ArrayList();
         if (!ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
            String[] var0 = ServerOptions.instance.SpawnPoint.getValue().split(",");
            if (var0.length == 3) {
               try {
                  int var1 = Integer.parseInt(var0[0].trim());
                  int var2 = Integer.parseInt(var0[1].trim());
                  int var3 = Integer.parseInt(var0[2].trim());
                  if (var1 != 0 || var2 != 0) {
                     SpawnPoints.add(new IsoGameCharacter.Location(var1, var2, var3));
                     return;
                  }
               } catch (NumberFormatException var15) {
                  DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
               }
            } else {
               DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
            }
         }

         KahluaTableIterator var16 = SpawnRegions.iterator();

         label73:
         while(true) {
            Object var18;
            do {
               Object var17;
               do {
                  if (!var16.advance()) {
                     return;
                  }

                  var17 = var16.getValue();
               } while(!(var17 instanceof KahluaTable));

               var18 = ((KahluaTable)var17).rawget("points");
            } while(!(var18 instanceof KahluaTable));

            KahluaTableIterator var19 = ((KahluaTable)var18).iterator();

            while(true) {
               do {
                  if (!var19.advance()) {
                     continue label73;
                  }
               } while(!(var19.getValue() instanceof KahluaTable));

               KahluaTableIterator var4 = ((KahluaTable)var19.getValue()).iterator();

               while(var4.advance()) {
                  Object var5 = var4.getValue();
                  if (var5 instanceof KahluaTable) {
                     Object var6 = ((KahluaTable)var5).rawget("worldX");
                     Object var7 = ((KahluaTable)var5).rawget("worldY");
                     Object var8 = ((KahluaTable)var5).rawget("posX");
                     Object var9 = ((KahluaTable)var5).rawget("posY");
                     Object var10 = ((KahluaTable)var5).rawget("posZ");
                     if (var6 instanceof Double && var7 instanceof Double && var8 instanceof Double && var9 instanceof Double) {
                        int var11 = ((Double)var6).intValue() * 300 + ((Double)var8).intValue();
                        int var12 = ((Double)var7).intValue() * 300 + ((Double)var9).intValue();
                        int var13 = var10 == null ? 0 : ((Double)var10).intValue();
                        IsoGameCharacter.Location var14 = new IsoGameCharacter.Location(var11, var12, var13);
                        if (!SpawnPoints.contains(var14)) {
                           SpawnPoints.add(var14);
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

         for(int var0 = 0; var0 < SpawnPoints.size(); ++var0) {
            IsoGameCharacter.Location var1 = (IsoGameCharacter.Location)SpawnPoints.get(var0);
            RoomDef var2 = IsoWorld.instance.MetaGrid.getRoomAt(var1.x, var1.y, var1.z);
            if (var2 != null && var2.getBuilding() != null) {
               SpawnBuildings.add(var2.getBuilding());
            }
         }

      }
   }

   public static boolean isSpawnBuilding(BuildingDef var0) {
      return SpawnBuildings != null && SpawnBuildings.contains(var0);
   }

   private static void setFastForward(boolean var0) {
      if (var0 != bFastForward) {
         bFastForward = var0;
         syncClock();
         if (!bFastForward) {
            SendZombies = 0;
         }

      }
   }

   private static void receiveCustomColor(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      float var6 = var0.getFloat();
      float var7 = var0.getFloat();
      float var8 = var0.getFloat();
      float var9 = var0.getFloat();
      IsoGridSquare var10 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var10 != null && var5 < var10.getObjects().size()) {
         IsoObject var11 = (IsoObject)var10.getObjects().get(var5);
         if (var11 != null) {
            var11.setCustomColor(var6, var7, var8, var9);
         }
      }

      for(int var14 = 0; var14 < udpEngine.connections.size(); ++var14) {
         UdpConnection var12 = (UdpConnection)udpEngine.connections.get(var14);
         if (var12.ReleventTo((float)var2, (float)var3) && (var1 != null && var12.getConnectedGUID() != var1.getConnectedGUID() || var1 == null)) {
            ByteBufferWriter var13 = var12.startPacket();
            PacketTypes.doPacket((short)121, var13);
            var13.putInt(var2);
            var13.putInt(var3);
            var13.putInt(var4);
            var13.putInt(var5);
            var13.putFloat(var6);
            var13.putFloat(var7);
            var13.putFloat(var8);
            var13.putFloat(var9);
            var12.endPacketImmediate();
         }
      }

   }

   private static void receiveFurnaceChange(ByteBuffer var0, UdpConnection var1) {
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 == null) {
         DebugLog.log("receiveFurnaceChange: square is null x,y,z=" + var2 + "," + var3 + "," + var4);
      } else {
         BSFurnace var6 = null;

         for(int var7 = 0; var7 < var5.getObjects().size(); ++var7) {
            if (var5.getObjects().get(var7) instanceof BSFurnace) {
               var6 = (BSFurnace)var5.getObjects().get(var7);
               break;
            }
         }

         if (var6 == null) {
            DebugLog.log("receiveFurnaceChange: furnace is null x,y,z=" + var2 + "," + var3 + "," + var4);
         } else {
            var6.fireStarted = var0.get() == 1;
            var6.fuelAmount = var0.getFloat();
            var6.fuelDecrease = var0.getFloat();
            var6.heat = var0.getFloat();
            var6.sSprite = GameWindow.ReadString(var0);
            var6.sLitSprite = GameWindow.ReadString(var0);
            sendFuranceChange(var6, var1);
         }
      }
   }

   public static void sendFuranceChange(BSFurnace var0, UdpConnection var1) {
      for(int var2 = 0; var2 < udpEngine.connections.size(); ++var2) {
         UdpConnection var3 = (UdpConnection)udpEngine.connections.get(var2);
         if (var3.ReleventTo((float)var0.square.x, (float)var0.square.y) && (var1 != null && var3.getConnectedGUID() != var1.getConnectedGUID() || var1 == null)) {
            ByteBufferWriter var4 = var3.startPacket();
            PacketTypes.doPacket((short)120, var4);
            var4.putInt(var0.square.x);
            var4.putInt(var0.square.y);
            var4.putInt(var0.square.z);
            var4.putByte((byte)(var0.isFireStarted() ? 1 : 0));
            var4.putFloat(var0.getFuelAmount());
            var4.putFloat(var0.getFuelDecrease());
            var4.putFloat(var0.getHeat());
            GameWindow.WriteString(var4.bb, var0.sSprite);
            GameWindow.WriteString(var4.bb, var0.sLitSprite);
            var3.endPacketImmediate();
         }
      }

   }

   private static void sendUserlog(ByteBuffer var0, UdpConnection var1, String var2) {
      ArrayList var3 = ServerWorldDatabase.instance.getUserlog(var2);

      for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
         UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
         if (var5.getConnectedGUID() == var1.getConnectedGUID()) {
            ByteBufferWriter var6 = var5.startPacket();
            PacketTypes.doPacket((short)128, var6);
            var6.putInt(var3.size());
            var6.putUTF(var2);

            for(int var7 = 0; var7 < var3.size(); ++var7) {
               Userlog var8 = (Userlog)var3.get(var7);
               var6.putInt(Userlog.UserlogType.FromString(var8.getType()).index());
               var6.putUTF(var8.getText());
               var6.putUTF(var8.getIssuedBy());
               var6.putInt(var8.getAmount());
            }

            var5.endPacketImmediate();
         }
      }

   }

   private static void addUserlog(ByteBuffer var0, UdpConnection var1) throws SQLException {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      String var4 = GameWindow.ReadString(var0);
      ServerWorldDatabase.instance.addUserlog(var2, Userlog.UserlogType.FromString(var3), var4, var1.username, 1);
      LoggerManager.getLogger("admin").write(var1.username + " added log on user " + var2 + ", log: " + var4);
   }

   private static void removeUserlog(ByteBuffer var0, UdpConnection var1) throws SQLException {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      String var4 = GameWindow.ReadString(var0);
      ServerWorldDatabase.instance.removeUserLog(var2, var3, var4);
      LoggerManager.getLogger("admin").write(var1.username + " removed log on user " + var2 + ", type:" + var3 + ", log: " + var4);
   }

   private static void addWarningPoint(ByteBuffer var0, UdpConnection var1) throws SQLException {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      int var4 = var0.getInt();
      ServerWorldDatabase.instance.addWarningPoint(var2, var3, var4, var1.username);
      LoggerManager.getLogger("admin").write(var1.username + " added " + var4 + " warning point(s) on " + var2 + ", reason:" + var3);

      for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
         UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
         if (var6.username.equals(var2)) {
            ByteBufferWriter var7 = var6.startPacket();
            PacketTypes.doPacket((short)79, var7);
            var7.putUTF(var1.username);
            var7.putUTF(" gave you " + var4 + " warning point(s), reason: " + var3 + " ");
            var6.endPacketImmediate();
         }
      }

   }

   public static void sendAdminMessage(String var0, int var1, int var2, int var3) {
      for(int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
         UdpConnection var5 = (UdpConnection)udpEngine.connections.get(var4);
         if (canSeePlayerStats(var5)) {
            ByteBufferWriter var6 = var5.startPacket();
            PacketTypes.doPacket((short)132, var6);
            var6.putUTF(var0);
            var6.putInt(var1);
            var6.putInt(var2);
            var6.putInt(var3);
            var5.endPacketImmediate();
         }
      }

   }

   private static void wakeUpPlayer(ByteBuffer var0, UdpConnection var1) {
      IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(var0.getInt());
      var2.setAsleep(false);
      var2.setAsleepTime(0.0F);

      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var4.getConnectedGUID() != var1.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)133, var5);
            var5.putInt(var2.OnlineID);
            var4.endPacketImmediate();
         }
      }

   }

   private static void sendDBSchema(ByteBuffer var0, UdpConnection var1) {
      DBSchema var2 = ServerWorldDatabase.instance.getDBSchema();

      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var1 != null && var4.getConnectedGUID() == var1.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)135, var5);
            HashMap var6 = var2.getSchema();
            var5.putInt(var6.size());
            Iterator var7 = var6.keySet().iterator();

            while(var7.hasNext()) {
               String var8 = (String)var7.next();
               HashMap var9 = (HashMap)var6.get(var8);
               var5.putUTF(var8);
               var5.putInt(var9.size());
               Iterator var10 = var9.keySet().iterator();

               while(var10.hasNext()) {
                  String var11 = (String)var10.next();
                  var5.putUTF(var11);
                  var5.putUTF((String)var9.get(var11));
               }
            }

            var4.endPacketImmediate();
         }
      }

   }

   private static void sendTableResult(ByteBuffer var0, UdpConnection var1) throws SQLException {
      int var2 = var0.getInt();
      String var3 = GameWindow.ReadString(var0);
      ArrayList var4 = ServerWorldDatabase.instance.getTableResult(var3);

      for(int var5 = 0; var5 < udpEngine.connections.size(); ++var5) {
         UdpConnection var6 = (UdpConnection)udpEngine.connections.get(var5);
         if (var1 != null && var6.getConnectedGUID() == var1.getConnectedGUID()) {
            doTableResult(var6, var3, var4, 0, var2);
         }
      }

   }

   private static void doTableResult(UdpConnection var0, String var1, ArrayList var2, int var3, int var4) {
      int var5 = 0;
      boolean var6 = true;
      ByteBufferWriter var7 = var0.startPacket();
      PacketTypes.doPacket((short)136, var7);
      var7.putInt(var3);
      var7.putUTF(var1);
      if (var2.size() < var4) {
         var7.putInt(var2.size());
      } else if (var2.size() - var3 < var4) {
         var7.putInt(var2.size() - var3);
      } else {
         var7.putInt(var4);
      }

      for(int var8 = var3; var8 < var2.size(); ++var8) {
         DBResult var9 = null;

         try {
            var9 = (DBResult)var2.get(var8);
            var7.putInt(var9.getColumns().size());
         } catch (Exception var12) {
            var12.printStackTrace();
         }

         Iterator var10 = var9.getColumns().iterator();

         while(var10.hasNext()) {
            String var11 = (String)var10.next();
            var7.putUTF(var11);
            var7.putUTF((String)var9.getValues().get(var11));
         }

         ++var5;
         if (var5 >= var4) {
            var6 = false;
            var0.endPacketImmediate();
            doTableResult(var0, var1, var2, var3 + var5, var4);
            break;
         }
      }

      if (var6) {
         var0.endPacketImmediate();
      }

   }

   private static void executeQuery(ByteBuffer var0, UdpConnection var1) throws SQLException {
      if (var1.accessLevel != null && var1.accessLevel.equals("admin")) {
         try {
            String var2 = GameWindow.ReadString(var0);
            KahluaTable var3 = LuaManager.platform.newTable();
            var3.load((ByteBuffer)var0, 143);
            ServerWorldDatabase.instance.executeQuery(var2, var3);
         } catch (Throwable var4) {
            var4.printStackTrace();
         }

      }
   }

   private static void sendFactionInvite(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      String var4 = GameWindow.ReadString(var0);
      IsoPlayer var5 = getPlayerByUserName(var4);
      Long var6 = (Long)IDToAddressMap.get(var5.getOnlineID());

      for(int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
         UdpConnection var8 = (UdpConnection)udpEngine.connections.get(var7);
         if (var8.getConnectedGUID() == var6) {
            ByteBufferWriter var9 = var8.startPacket();
            PacketTypes.doPacket((short)141, var9);
            var9.putUTF(var2);
            var9.putUTF(var3);
            var8.endPacketImmediate();
            break;
         }
      }

   }

   private static void acceptedFactionInvite(ByteBuffer var0, UdpConnection var1) {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      IsoPlayer var4 = getPlayerByUserName(var3);
      Long var5 = (Long)IDToAddressMap.get(var4.getOnlineID());

      for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
         UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
         if (var7.getConnectedGUID() == var5) {
            Faction var8 = Faction.getPlayerFaction(var7.username);
            if (var8 != null && var8.getName().equals(var2)) {
               ByteBufferWriter var9 = var7.startPacket();
               PacketTypes.doPacket((short)142, var9);
               var9.putUTF(var2);
               var9.putUTF(var3);
               var7.endPacketImmediate();
            }
         }
      }

   }

   private static void viewTickets(ByteBuffer var0, UdpConnection var1) throws SQLException {
      String var2 = GameWindow.ReadString(var0);
      if ("".equals(var2)) {
         var2 = null;
      }

      sendTickets(var2, var1);
   }

   private static void sendTickets(String var0, UdpConnection var1) throws SQLException {
      ArrayList var2 = ServerWorldDatabase.instance.getTickets(var0);

      for(int var3 = 0; var3 < udpEngine.connections.size(); ++var3) {
         UdpConnection var4 = (UdpConnection)udpEngine.connections.get(var3);
         if (var4.getConnectedGUID() == var1.getConnectedGUID()) {
            ByteBufferWriter var5 = var4.startPacket();
            PacketTypes.doPacket((short)144, var5);
            var5.putInt(var2.size());

            for(int var6 = 0; var6 < var2.size(); ++var6) {
               DBTicket var7 = (DBTicket)var2.get(var6);
               var5.putUTF(var7.getAuthor());
               var5.putUTF(var7.getMessage());
               var5.putInt(var7.getTicketID());
               if (var7.getAnswer() != null) {
                  var5.putByte((byte)1);
                  var5.putUTF(var7.getAnswer().getAuthor());
                  var5.putUTF(var7.getAnswer().getMessage());
                  var5.putInt(var7.getAnswer().getTicketID());
               } else {
                  var5.putByte((byte)0);
               }
            }

            var4.endPacketImmediate();
            break;
         }
      }

   }

   private static void addTicket(ByteBuffer var0, UdpConnection var1) throws SQLException {
      String var2 = GameWindow.ReadString(var0);
      String var3 = GameWindow.ReadString(var0);
      int var4 = var0.getInt();
      if (var4 == -1) {
         sendAdminMessage("user " + var2 + " added a ticket <LINE> <LINE> " + var3, -1, -1, -1);
      }

      ServerWorldDatabase.instance.addTicket(var2, var3, var4);
      sendTickets(var2, var1);
   }

   private static void removeTicket(ByteBuffer var0, UdpConnection var1) throws SQLException {
      int var2 = var0.getInt();
      ServerWorldDatabase.instance.removeTicket(var2);
      sendTickets((String)null, var1);
   }

   public static boolean sendItemListNet(UdpConnection var0, IsoPlayer var1, ArrayList var2, IsoPlayer var3, String var4, String var5) {
      for(int var6 = 0; var6 < udpEngine.connections.size(); ++var6) {
         UdpConnection var7 = (UdpConnection)udpEngine.connections.get(var6);
         if (var0 == null || var7 != var0) {
            if (var3 != null) {
               boolean var8 = false;

               for(int var9 = 0; var9 < var7.players.length; ++var9) {
                  IsoPlayer var10 = var7.players[var9];
                  if (var10 != null && var10 == var3) {
                     var8 = true;
                     break;
                  }
               }

               if (!var8) {
                  continue;
               }
            }

            ByteBufferWriter var12 = var7.startPacket();
            PacketTypes.doPacket((short)150, var12);
            var12.putByte((byte)(var3 != null ? 1 : 0));
            if (var3 != null) {
               var12.putShort((short)var3.getOnlineID());
            }

            var12.putByte((byte)(var1 != null ? 1 : 0));
            if (var1 != null) {
               var12.putShort((short)var1.getOnlineID());
            }

            GameWindow.WriteString(var12.bb, var4);
            var12.putByte((byte)(var5 != null ? 1 : 0));
            if (var5 != null) {
               GameWindow.WriteString(var12.bb, var5);
            }

            try {
               CompressIdenticalItems.save(var12.bb, var2, (IsoGameCharacter)null);
            } catch (Exception var11) {
               var11.printStackTrace();
               var7.cancelPacket();
               return false;
            }

            var7.endPacketImmediate();
         }
      }

      return true;
   }

   private static void receiveItemListNet(ByteBuffer var0, UdpConnection var1) {
      IsoPlayer var2 = null;
      if (var0.get() == 1) {
         var2 = (IsoPlayer)IDToPlayerMap.get(var0.getShort());
      }

      IsoPlayer var3 = null;
      if (var0.get() == 1) {
         var3 = (IsoPlayer)IDToPlayerMap.get(var0.getShort());
      }

      String var4 = GameWindow.ReadString(var0);
      String var5 = null;
      if (var0.get() == 1) {
         var5 = GameWindow.ReadString(var0);
      }

      ArrayList var6 = new ArrayList();

      try {
         CompressIdenticalItems.load(var0, 143, var6, (ArrayList)null);
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      if (var2 == null) {
         LuaEventManager.triggerEvent("OnReceiveItemListNet", var3, var6, var2, var4, var5);
      } else {
         sendItemListNet(var1, var3, var6, var2, var4, var5);
      }

   }

   public static void sendPlayerDamagedByCarCrash(IsoPlayer var0, float var1) {
      UdpConnection var2 = getConnectionFromPlayer(var0);
      if (var2 != null) {
         ByteBufferWriter var3 = var2.startPacket();
         PacketTypes.doPacket((short)172, var3);
         var3.putFloat(var1);
         var2.endPacketImmediate();
      }
   }

   private static void receiveClimateManagerPacket(ByteBuffer var0, UdpConnection var1) {
      ClimateManager var2 = ClimateManager.getInstance();
      if (var2 != null) {
         try {
            var2.receiveClimatePacket(var0, var1);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

   }

   private static String isWorldVersionUnsupported() {
      File var0 = new File(GameWindow.getSaveDir() + File.separator + "Multiplayer" + File.separator + ServerName + File.separator + "map_t.bin");
      if (var0.exists()) {
         DebugLog.log("checking server WorldVersion in map_t.bin");

         try {
            FileInputStream var1 = new FileInputStream(var0);
            Throwable var2 = null;

            try {
               DataInputStream var3 = new DataInputStream(var1);
               Throwable var4 = null;

               try {
                  byte var5 = var3.readByte();
                  byte var6 = var3.readByte();
                  byte var7 = var3.readByte();
                  byte var8 = var3.readByte();
                  String var9;
                  if (var5 == 71 && var6 == 77 && var7 == 84 && var8 == 77) {
                     var9 = var3.readInt();
                     String var10;
                     if (var9 > 143) {
                        var10 = "The server savefile appears to be from a newer version of the game and cannot be loaded.";
                        return var10;
                     }

                     if (!(var9 <= 115)) {
                        return null;
                     }

                     var10 = "The server savefile appears to be from a pre-vehicles version of the game and cannot be loaded.\nDue to the extent of changes required to implement vehicles, saves from earlier versions are not compatible.\nTo continue this game, use the 38.30(pre-vehicles) branch.";
                     return var10;
                  }

                  var9 = "The server savefile appears to be from an old version of the game and cannot be loaded.";
                  return var9;
               } catch (Throwable var44) {
                  var4 = var44;
                  throw var44;
               } finally {
                  if (var3 != null) {
                     if (var4 != null) {
                        try {
                           var3.close();
                        } catch (Throwable var43) {
                           var4.addSuppressed(var43);
                        }
                     } else {
                        var3.close();
                     }
                  }

               }
            } catch (Throwable var46) {
               var2 = var46;
               throw var46;
            } finally {
               if (var1 != null) {
                  if (var2 != null) {
                     try {
                        var1.close();
                     } catch (Throwable var42) {
                        var2.addSuppressed(var42);
                     }
                  } else {
                     var1.close();
                  }
               }

            }
         } catch (Exception var48) {
            var48.printStackTrace();
         }
      } else {
         DebugLog.log("map_t.bin does not exist, cannot determine the server's WorldVersion.");
      }

      return null;
   }

   static {
      discordBot = new DiscordBot(ServerName, new DiscordSender() {
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

      public DelayedConnection(UdpConnection var1, boolean var2) {
         this.connection = var1;
         this.connect = var2;
         if (var2) {
            try {
               if (SteamUtils.isSteamModeEnabled()) {
                  long var3 = GameServer.udpEngine.getClientSteamID(var1.getConnectedGUID());
                  this.hostString = SteamUtils.convertSteamIDToString(var3);
               } else {
                  this.hostString = var1.getInetSocketAddress().getHostString();
               }
            } catch (Exception var5) {
               var5.printStackTrace();
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

      boolean matches(String var1) {
         return this.command.equals(var1) || "*".equals(this.command);
      }

      boolean passes(String var1) {
         if (this.matches(var1)) {
            return this.allow;
         } else {
            return this.next == null ? true : this.next.passes(var1);
         }
      }

      // $FF: synthetic method
      CCFilter(Object var1) {
         this();
      }
   }
}
