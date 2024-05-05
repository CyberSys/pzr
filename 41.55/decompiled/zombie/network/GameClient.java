package zombie.network;

import fmod.javafmod;
import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;
import gnu.trove.map.hash.TShortObjectHashMap;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientStreamManager;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.PersistentOutfits;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SystemDisabler;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkPlayerAI;
import zombie.characters.NetworkStrikeAI;
import zombie.characters.NetworkTeleport;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.skills.PerkFactory;
import zombie.chat.ChatManager;
import zombie.core.BoxedStaticValues;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferReader;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.RakVoice;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.UdpEngine;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUser;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.erosion.ErosionConfig;
import zombie.erosion.ErosionMain;
import zombie.gameStates.ConnectToServerState;
import zombie.gameStates.MainScreenState;
import zombie.globalObjects.CGlobalObjectNetwork;
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
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridOcclusionData;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectSyncRequests;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.ObjectsSyncRequests;
import zombie.iso.SliceY;
import zombie.iso.Vector2;
import zombie.iso.WorldStreamer;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.BSFurnace;
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
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.weather.ClimateManager;
import zombie.network.packets.DeadBodyPacket;
import zombie.network.packets.EventUpdatePacket;
import zombie.network.packets.HitPacket;
import zombie.network.packets.PlayerPacket;
import zombie.popman.MPDebugInfo;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.savefile.ClientPlayerDB;
import zombie.scripting.ScriptManager;
import zombie.ui.ServerPulseGraph;
import zombie.util.AddCoopPlayer;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.world.moddata.GlobalModData;

public class GameClient {
   public static final GameClient instance = new GameClient();
   public static final int DEFAULT_PORT = 16361;
   public static boolean bClient = false;
   public static UdpConnection connection;
   public static int count = 0;
   public static String ip = "localhost";
   public static String localIP = "";
   public static String password = "testpass";
   public static boolean allChatMuted = false;
   public static String username = "lemmy101";
   public static String serverPassword = "";
   public UdpEngine udpEngine;
   public static String accessLevel = "";
   public byte ID = -1;
   public float timeSinceKeepAlive = 0.0F;
   UpdateLimit itemSendFrequency = new UpdateLimit(3000L);
   public static int port;
   public HashMap PlayerToBody = new HashMap();
   public boolean bPlayerConnectSent = false;
   private boolean bClientStarted = false;
   private int ResetID = 0;
   private boolean bConnectionLost = false;
   public static String checksum;
   public static boolean checksumValid;
   public static List pingsList;
   public static String GameMap;
   public static boolean bFastForward;
   public static final ClientServerMap[] loadedCells;
   public int DEBUG_PING = 5;
   public IsoObjectSyncRequests objectSyncReq = new IsoObjectSyncRequests();
   public ObjectsSyncRequests worldObjectsSyncReq = new ObjectsSyncRequests(true);
   public static boolean bCoopInvite;
   private ArrayList connectedPlayers = new ArrayList();
   private static boolean isPaused;
   private final ArrayList players = new ArrayList();
   private boolean idMapDirty = true;
   public static Map positions;
   private int safehouseUpdateTimer = 0;
   private boolean delayPacket = false;
   private final long[] packetCountsFromAllClients = new long[256];
   private final long[] packetCountsFromServer = new long[256];
   private final KahluaTable packetCountsTable;
   private final ArrayList delayedDisconnect;
   private volatile GameClient.RequestState request;
   public KahluaTable ServerSpawnRegions;
   static final ArrayList MainLoopNetData;
   static final ArrayList LoadingMainLoopNetData;
   static final ArrayList DelayedCoopNetData;
   public boolean bConnected;
   public int TimeSinceLastUpdate;
   ByteBuffer staticTest;
   ByteBufferWriter wr;
   long StartHeartMilli;
   long EndHeartMilli;
   public int ping;
   public static float ServerPredictedAhead;
   public static final HashMap IDToPlayerMap;
   public static final TShortObjectHashMap IDToZombieMap;
   public static boolean bIngame;
   public static boolean askPing;
   public final ArrayList ServerMods;
   public ErosionConfig erosionConfig;
   public static Calendar startAuth;
   public static String poisonousBerry;
   public static String poisonousMushroom;
   final ArrayList incomingNetData;
   private final HashMap itemsToSend;
   private final HashMap itemsToSendRemove;
   KahluaTable dbSchema;

   public GameClient() {
      this.packetCountsTable = LuaManager.platform.newTable();
      this.delayedDisconnect = new ArrayList();
      this.ServerSpawnRegions = null;
      this.bConnected = false;
      this.TimeSinceLastUpdate = 0;
      this.staticTest = ByteBuffer.allocate(20000);
      this.wr = new ByteBufferWriter(this.staticTest);
      this.StartHeartMilli = 0L;
      this.EndHeartMilli = 0L;
      this.ping = 0;
      this.ServerMods = new ArrayList();
      this.incomingNetData = new ArrayList();
      this.itemsToSend = new HashMap();
      this.itemsToSendRemove = new HashMap();
   }

   public IsoPlayer getPlayerByOnlineID(int var1) {
      Iterator var2 = IDToPlayerMap.values().iterator();

      IsoPlayer var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (IsoPlayer)var2.next();
      } while(var3.getOnlineID() != var1);

      return var3;
   }

   public void init() {
      DebugLog.setLogEnabled(DebugType.Network, true);
      LoadingMainLoopNetData.clear();
      MainLoopNetData.clear();
      DelayedCoopNetData.clear();
      bIngame = false;
      IDToPlayerMap.clear();
      IDToZombieMap.clear();
      pingsList.clear();
      IDToZombieMap.setAutoCompactionFactor(0.0F);
      this.bPlayerConnectSent = false;
      this.bConnectionLost = false;
      this.delayedDisconnect.clear();
      GameWindow.bServerDisconnected = false;
      this.ServerSpawnRegions = null;
      this.startClient();
   }

   public void startClient() {
      if (this.bClientStarted) {
         this.udpEngine.Connect(ip, port, serverPassword);
      } else {
         try {
            this.udpEngine = new UdpEngine(Rand.Next(10000) + 12345, 1, (String)null, false);
            this.udpEngine.Connect(ip, port, serverPassword);
            this.bClientStarted = true;
         } catch (Exception var2) {
            DebugLog.Network.printException(var2, "Exception thrown during GameClient.startClient.", LogSeverity.Error);
         }

      }
   }

   public static void sendZombieAttackTarget(IsoZombie var0, IsoPlayer var1, String var2, String var3) {
      try {
         ByteBufferWriter var4 = connection.startPacket();
         PacketTypes.doPacket((short)216, var4);
         var4.putShort((short)var0.getOnlineID());
         var4.putShort((short)var1.getOnlineID());
         byte var6 = -1;
         switch(var2.hashCode()) {
         case -1947652542:
            if (var2.equals("interrupted")) {
               var6 = 2;
            }
            break;
         case -1867169789:
            if (var2.equals("success")) {
               var6 = 0;
            }
            break;
         case 3135262:
            if (var2.equals("fail")) {
               var6 = 1;
            }
         }

         switch(var6) {
         case 0:
            var4.putByte((byte)1);
            break;
         case 1:
            var4.putByte((byte)2);
            break;
         case 2:
            var4.putByte((byte)2);
            break;
         default:
            var4.putByte((byte)0);
         }

         var4.putUTF(var3);
         connection.endPacketImmediate();
      } catch (Exception var7) {
         connection.cancelPacket();
         ExceptionLogger.logException(var7);
      }

   }

   public static void sendEventUpdate(IsoPlayer var0, String var1, boolean var2) {
      try {
         EventUpdatePacket var3 = EventUpdatePacket.l_send.eventUpdatePacket;
         if (var3.set(var0, var1, var2)) {
            ByteBufferWriter var4 = connection.startPacket();
            PacketTypes.doPacket((short)210, var4);
            var3.write(var4);
            connection.endPacketSuperHighUnreliable();
            DebugLog.log(DebugType.Multiplayer, "Event sent: " + var3.event.getDescription());
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public static void receiveEventUpdate(ByteBuffer var0) {
      try {
         EventUpdatePacket var1 = EventUpdatePacket.l_receive.eventUpdatePacket;
         var1.parse(var0);
         IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var1.event.id));
         DebugLog.log(DebugType.Multiplayer, "Event received: " + var1.event.getDescription());
         if (var1.event.name == -2 && var2 != null) {
            var2.clearNetworkEvents();
         } else if (var2 == null || !var2.networkAI.events.isEmpty() && ((NetworkPlayerAI.Event)var2.networkAI.events.get(0)).name == var1.event.name) {
            DebugLog.log(DebugType.Multiplayer, "Event skipped: " + var1.event.getDescription());
         } else {
            var2.networkAI.events.add(new NetworkPlayerAI.Event(var1.event));
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static void receiveStatistic(ByteBuffer var0) {
      try {
         long var1 = var0.getLong();
         ByteBufferWriter var3 = connection.startPacket();
         PacketTypes.doPacket((short)211, var3);
         var3.putLong(var1);
         MPStatisticClient.getInstance().send(var3);
         connection.endPacketSuperHighUnreliable();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public static void receiveStatisticRequest(ByteBuffer var0) {
      try {
         MPStatistic.getInstance().setStatisticTable(var0);
      } catch (IOException var2) {
         var2.printStackTrace();
      }

      LuaEventManager.triggerEvent("OnServerStatisticReceived");
   }

   public static void receivePlayerUpdate(ByteBuffer var0) {
      PlayerPacket var1 = PlayerPacket.l_receive.playerPacket;
      var1.parse(var0);

      try {
         IsoPlayer var2 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var1.id));
         if (var2 == null) {
            DebugLog.General.error("receivePlayerUpdate: Client received position for unknown player (id:" + var1.id + "). Client will ignore this data.");
         } else {
            if (DebugOptions.instance.MultiplayerShowPosition.getValue()) {
               if (positions.containsKey(var2.getOnlineID())) {
                  ((Vector2)positions.get(var2.getOnlineID())).set(var1.realx, var1.realy);
               } else {
                  positions.put(var2.getOnlineID(), new Vector2(var1.realx, var1.realy));
               }
            }

            label55: {
               var2.networkAI.parse(var1);
               RakVoice.SetPlayerCoordinate(connection.getConnectedGUID(), var1.x, var1.y, (float)var1.z, var2.isInvisible());
               int var3 = (int)(GameTime.getServerTime() / 1000000L);
               if (Math.abs(var1.t - var3) < 5000) {
                  NetworkPlayerAI var10000 = var2.networkAI;
                  if (NetworkPlayerAI.controlQuality > 0.5F) {
                     if (var2.getVehicle() != null || var1.t <= var3 + 10 || !(var2.networkAI.distance.getLength() > 7.0F) && (!(var2.networkAI.distance.getLength() > 4.0F) || (int)var2.z == var1.z)) {
                        break label55;
                     }

                     NetworkTeleport.update(var2, var1);
                     if (NetworkTeleport.teleport(var2, var1, 1.0F)) {
                        DebugLog.General.warn(String.format("Player %d teleport from (%.2f, %.2f, %.2f) to (%.2f, %.2f %d)", var2.getOnlineID(), var2.x, var2.y, var2.z, var1.x, var1.y, var1.z));
                     }
                     break label55;
                  }
               }

               if (var2.networkAI.distance.getLength() > 30.0F) {
                  NetworkTeleport.update(var2, var1);
                  if (NetworkTeleport.teleport(var2, var1, 1.0F)) {
                     DebugLog.General.warn(String.format("Player %d teleport from (%.2f, %.2f, %.2f) to (%.2f, %.2f %d)", var2.getOnlineID(), var2.x, var2.y, var2.z, var1.x, var1.y, var1.z));
                  }
               }
            }

            IsoGridSquare var4 = IsoWorld.instance.CurrentCell.getGridSquare((double)var1.x, (double)var1.y, (double)var1.z);
            if (var4 != null) {
               if (var2.isAlive() && !IsoWorld.instance.CurrentCell.getObjectList().contains(var2)) {
                  IsoWorld.instance.CurrentCell.getObjectList().add(var2);
                  var2.setCurrent(var4);
               }
            } else if (IsoWorld.instance.CurrentCell.getObjectList().contains(var2)) {
               var2.removeFromWorld();
               var2.removeFromSquare();
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public static void receiveZombieAttackTarget(ByteBuffer var0) {
      ByteBufferReader var1 = new ByteBufferReader(var0);
      IsoZombie.AttackNetworkEvent var2 = new IsoZombie.AttackNetworkEvent();
      var2.load(var1);
      IsoZombie var3 = (IsoZombie)IDToZombieMap.get(var2.zombieId);
      if (var3 != null) {
         var3.attackNetworkEvents.add(var2);
      }

   }

   public void Shutdown() {
      if (this.bClientStarted) {
         this.udpEngine.Shutdown();
      }

   }

   public void update() {
      if (this.safehouseUpdateTimer == 0 && ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue()) {
         this.safehouseUpdateTimer = 3000;
         SafeHouse.updateSafehousePlayersConnected();
      }

      if (this.safehouseUpdateTimer > 0) {
         --this.safehouseUpdateTimer;
      }

      int var2;
      ZomboidNetData var3;
      if (this.bConnectionLost) {
         if (!this.bPlayerConnectSent) {
            synchronized(MainLoopNetData) {
               for(var2 = 0; var2 < MainLoopNetData.size(); ++var2) {
                  var3 = (ZomboidNetData)MainLoopNetData.get(var2);
                  this.gameLoadingDealWithNetData(var3);
               }

               MainLoopNetData.clear();
            }
         } else {
            synchronized(MainLoopNetData) {
               for(var2 = 0; var2 < MainLoopNetData.size(); ++var2) {
                  var3 = (ZomboidNetData)MainLoopNetData.get(var2);
                  if (var3.type == 83) {
                     GameWindow.kickReason = GameWindow.ReadStringUTF(var3.buffer);
                  }
               }

               MainLoopNetData.clear();
            }
         }

         GameWindow.bServerDisconnected = true;
      } else {
         synchronized(this.delayedDisconnect) {
            while(!this.delayedDisconnect.isEmpty()) {
               var2 = (Integer)this.delayedDisconnect.remove(0);
               switch(var2) {
               case 17:
                  LuaEventManager.triggerEvent("OnConnectFailed", (Object)null);
                  break;
               case 18:
                  LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_AlreadyConnected"));
               case 19:
               case 20:
               case 22:
               case 25:
               case 26:
               case 27:
               case 28:
               case 29:
               case 30:
               case 31:
               default:
                  break;
               case 21:
                  LuaEventManager.triggerEvent("OnDisconnect");
                  break;
               case 23:
                  LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_Banned"));
                  break;
               case 24:
                  LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_InvalidServerPassword"));
                  break;
               case 32:
                  LuaEventManager.triggerEvent("OnConnectFailed", Translator.getText("UI_OnConnectFailed_ConnectionLost"));
               }
            }
         }

         if (!this.bPlayerConnectSent) {
            synchronized(MainLoopNetData) {
               for(var2 = 0; var2 < MainLoopNetData.size(); ++var2) {
                  var3 = (ZomboidNetData)MainLoopNetData.get(var2);
                  if (!this.gameLoadingDealWithNetData(var3)) {
                     LoadingMainLoopNetData.add(var3);
                  }
               }

               MainLoopNetData.clear();
               WorldStreamer.instance.updateMain();
            }
         } else {
            if (!LoadingMainLoopNetData.isEmpty()) {
               DebugLog.log(DebugType.Network, "Processing delayed packets...");
               synchronized(MainLoopNetData) {
                  MainLoopNetData.addAll(0, LoadingMainLoopNetData);
                  LoadingMainLoopNetData.clear();
               }
            }

            if (!DelayedCoopNetData.isEmpty() && IsoWorld.instance.AddCoopPlayers.isEmpty()) {
               DebugLog.log(DebugType.Network, "Processing delayed coop packets...");
               synchronized(MainLoopNetData) {
                  MainLoopNetData.addAll(0, DelayedCoopNetData);
                  DelayedCoopNetData.clear();
               }
            }

            synchronized(MainLoopNetData) {
               long var20 = System.currentTimeMillis();
               int var4 = 0;

               while(true) {
                  if (var4 >= MainLoopNetData.size()) {
                     break;
                  }

                  ZomboidNetData var5 = (ZomboidNetData)MainLoopNetData.get(var4);
                  if (var5.time + (long)this.DEBUG_PING <= var20) {
                     this.mainLoopDealWithNetData(var5);
                     MainLoopNetData.remove(var4--);
                  }

                  ++var4;
               }
            }

            for(int var1 = 0; var1 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++var1) {
               IsoMovingObject var21 = (IsoMovingObject)IsoWorld.instance.CurrentCell.getObjectList().get(var1);
               if (var21 instanceof IsoPlayer && !((IsoPlayer)var21).isLocalPlayer() && !this.getPlayers().contains(var21)) {
                  if (Core.bDebug) {
                     DebugLog.log("Disconnected/Distant player " + ((IsoPlayer)var21).username + " in CurrentCell.getObjectList() removed");
                  }

                  IsoWorld.instance.CurrentCell.getObjectList().remove(var1--);
               }
            }

            try {
               this.sendAddedRemovedItems(false);
            } catch (Exception var13) {
               connection.cancelPacket();
               ExceptionLogger.logException(var13);
            }

            try {
               VehicleManager.instance.clientUpdate();
            } catch (Exception var12) {
               var12.printStackTrace();
            }

            this.objectSyncReq.sendRequests(connection);
            this.worldObjectsSyncReq.sendRequests(connection);
            WorldStreamer.instance.updateMain();
            MPStatisticClient.getInstance().update();
            this.timeSinceKeepAlive += GameTime.getInstance().getMultiplier();
         }
      }
   }

   public void smashWindow(IsoWindow var1, int var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)32, var3);
      var3.putInt(var1.square.getX());
      var3.putInt(var1.square.getY());
      var3.putInt(var1.square.getZ());
      var3.putByte((byte)var1.square.getObjects().indexOf(var1));
      var3.putByte((byte)var2);
      connection.endPacketImmediate();
   }

   public static void getCustomModData() {
      ByteBufferWriter var0 = connection.startPacket();
      PacketTypes.doPacket((short)80, var0);
      connection.endPacketImmediate();
   }

   private void doStitch(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var1.getInt();
         boolean var5 = var1.get() == 1;
         float var6 = var1.getFloat();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setStitched(var5);
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setStitchTime(var6);
      }

   }

   private void doBandage(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var1.getInt();
         boolean var5 = var1.get() == 1;
         float var6 = var1.getFloat();
         boolean var7 = var1.get() == 1;
         String var8 = GameWindow.ReadStringUTF(var1);
         var3.getBodyDamage().SetBandaged(var4, var5, var6, var7, var8);
      }

   }

   private void doWoundInfection(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var1.getInt();
         boolean var5 = var1.get() == 1;
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setInfectedWound(var5);
      }

   }

   private void doDisinfect(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var1.getInt();
         float var5 = var1.getFloat();
         BodyPart var6 = var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4));
         var6.setAlcoholLevel(var6.getAlcoholLevel() + var5);
      }

   }

   private void doSplint(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var1.getInt();
         boolean var5 = var1.get() == 1;
         String var6 = var5 ? GameWindow.ReadStringUTF(var1) : null;
         float var7 = var5 ? var1.getFloat() : 0.0F;
         BodyPart var8 = var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4));
         var8.setSplint(var5, var7);
         var8.setSplintItem(var6);
      }

   }

   private void doRemoveGlass(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var1.getInt();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setHaveGlass(false);
      }

   }

   private void doRemoveBullet(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var1.getInt();
         int var5 = var1.getInt();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setHaveBullet(false, var5);
      }

   }

   private void doCleanBurn(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var1.getInt();
         var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setNeedBurnWash(false);
      }

   }

   private void doAdditionalPain(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var1.getInt();
         float var5 = var1.getFloat();
         BodyPart var6 = var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4));
         var6.setAdditionalPain(var6.getAdditionalPain() + var5);
      }

   }

   private void applyDamage(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null && var3 == IsoPlayer.getInstance()) {
         try {
            var3.getBodyDamage().load(var1, 184);
         } catch (IOException var5) {
            var5.printStackTrace();
         }

         float var4 = var1.getFloat();
         var3.getStats().Pain = var4;
      }

      if (ServerOptions.instance.PlayerSaveOnDamage.getValue()) {
         GameWindow.savePlayer();
      }

   }

   private void delayPacket(int var1, int var2, int var3) {
      if (IsoWorld.instance != null) {
         for(int var4 = 0; var4 < IsoWorld.instance.AddCoopPlayers.size(); ++var4) {
            AddCoopPlayer var5 = (AddCoopPlayer)IsoWorld.instance.AddCoopPlayers.get(var4);
            if (var5.isLoadingThisSquare(var1, var2)) {
               this.delayPacket = true;
               return;
            }
         }

      }
   }

   private void mainLoopDealWithNetData(ZomboidNetData var1) {
      ByteBuffer var2 = var1.buffer;
      int var3 = var2.position();
      this.delayPacket = false;
      if (var1.type >= 0 && var1.type < 256) {
         int var10002 = this.packetCountsFromServer[var1.type]++;
      }

      try {
         this.mainLoopHandlePacketInternal(var1, var2);
         if (this.delayPacket) {
            var2.position(var3);
            DelayedCoopNetData.add(var1);
            return;
         }
      } catch (Exception var5) {
         DebugLog.Network.printException(var5, "Error with packet of type: " + var1.type, LogSeverity.Error);
      }

      ZomboidNetDataPool.instance.discard(var1);
   }

   private void mainLoopHandlePacketInternal(ZomboidNetData var1, ByteBuffer var2) throws IOException {
      if (DebugOptions.instance.Network.Client.MainLoop.getValue()) {
         int var8;
         switch(var1.type) {
         case 1:
            if (ServerPulseGraph.instance != null) {
               ServerPulseGraph.instance.add(var2.getLong());
            }
            break;
         case 3:
            this.receiveVisual(var2);
            break;
         case 4:
            MPDebugInfo.instance.clientPacket(var2);
            break;
         case 5:
            VehicleManager.instance.clientPacket(var2);
            break;
         case 6:
            this.receivePlayerConnect(var2);
            break;
         case 9:
            this.receiveMetaGrid(var2);
            break;
         case 10:
            this.receiveZombieUpdateInfoPacket(var2);
            break;
         case 11:
            this.receiveHelicopter(var2);
            break;
         case 12:
            this.SyncIsoObject(var2);
            break;
         case 13:
            this.playerTimeout(var2);
            break;
         case 15:
            ClientServerMap.receivePacket(var2);
            break;
         case 16:
            PassengerMap.clientReceivePacket(var2);
            break;
         case 17:
            this.AddItemToMap(var2);
            break;
         case 18:
            assert false;
            break;
         case 19:
            this.receiveSyncClock(var2);
            break;
         case 20:
            this.sendItemsToContainer(var2);
            break;
         case 22:
            this.removeItemFromContainer(var2);
            break;
         case 23:
            this.RemoveItemFromMap(var2);
            break;
         case 25:
            this.receiveEquip(var2);
            break;
         case 26:
            hitCharacter(var2);
            break;
         case 27:
            this.receiveAddCoopPlayer(var2);
            break;
         case 29:
            this.onZombieDie(var2);
            break;
         case 30:
            this.removeZombieCompletely(var2);
            break;
         case 31:
            this.receiveSandboxOptions(var2);
            break;
         case 32:
            IsoObject var3 = this.getIsoObjectRefFromByteBuffer(var2);
            if (var3 instanceof IsoWindow) {
               byte var12 = var2.get();
               if (var12 == 1) {
                  ((IsoWindow)var3).smashWindow(true);
               } else if (var12 == 2) {
                  ((IsoWindow)var3).setGlassRemoved(true);
               }
            } else if (Core.bDebug) {
               DebugLog.log("SmashWindow not a window!");
            }
            break;
         case 33:
            this.onPlayerDeath(var2);
            break;
         case 35:
            this.receiveItemStats(var2);
            break;
         case 36:
            assert false;
            break;
         case 37:
            this.receiveRequestData(var2);
            break;
         case 38:
            CGlobalObjectNetwork.receive(var2);
            break;
         case 39:
            this.doDeadZombie(var2);
            break;
         case 41:
            this.applyDamage(var2);
            break;
         case 42:
            this.doBandage(var2);
            break;
         case 46:
            SyncAlarmClock(var2);
            break;
         case 47:
            this.receivePacketCounts(var2);
            break;
         case 49:
            this.RemoveContestedItemsFromInventory(var2);
            break;
         case 50:
            int var4 = var2.getInt();
            this.connectedPlayers = new ArrayList();
            ArrayList var5 = new ArrayList();
            ArrayList var6 = new ArrayList();
            ArrayList var7 = new ArrayList();

            for(var8 = 0; var8 < var4; ++var8) {
               String var13 = GameWindow.ReadString(var2);
               String var10 = GameWindow.ReadString(var2);
               var5.add(var13);
               var6.add(var10);
               this.connectedPlayers.add(this.getPlayerFromUsername(var13));
               if (SteamUtils.isSteamModeEnabled()) {
                  String var11 = SteamUtils.convertSteamIDToString(var2.getLong());
                  var7.add(var11);
               }
            }

            LuaEventManager.triggerEvent("OnScoreboardUpdate", var5, var6, var7);
            break;
         case 51:
            this.receiveModData(var2);
            break;
         case 52:
            GameWindow.savePlayer();
            GameWindow.kickReason = "Server shut down safely. Players and map data saved.";
            GameWindow.bServerDisconnected = true;
            break;
         case 53:
            this.receiveSound(var2);
            break;
         case 55:
            this.receiveAmbient(var2);
            break;
         case 56:
            this.receiveClothing(var2);
            break;
         case 57:
            this.receiveServerCommand(var2);
            break;
         case 58:
            this.receiveObjectModData(var2);
            break;
         case 59:
            this.receiveObjectChange(var2);
            break;
         case 60:
            this.receiveBloodSplatter(var2);
            break;
         case 61:
            this.receiveZombieSound(var2);
            break;
         case 62:
            this.receivePlayerZombieDescriptor(var2);
            break;
         case 63:
            receiveSlowFactor(var2);
            break;
         case 64:
            this.receiveWeather(var2);
            break;
         case 65:
            this.SyncPlayerInventory(var2);
            break;
         case 68:
            this.RemoveCorpseFromMap(var2);
            break;
         case 69:
            this.AddCorpseToMap(var2);
            break;
         case 75:
            this.startFire(var2);
            break;
         case 76:
            this.updateItemSprite(var2);
            break;
         case 77:
            this.startRain(var2);
            break;
         case 78:
            RainManager.stopRaining();
            break;
         case 79:
            this.receiveWorldMessage(var2);
            break;
         case 82:
            var8 = var2.getInt();

            for(int var9 = 0; var9 < var8; ++var9) {
               ServerOptions.instance.putOption(GameWindow.ReadString(var2), GameWindow.ReadString(var2));
            }

            return;
         case 83:
            kick(var2);
            break;
         case 84:
            receivePlayerExtraInfo(var2);
            break;
         case 85:
            AddItemInInventory(var2);
            break;
         case 86:
            playerChangeSafety(var2);
            break;
         case 87:
            getInfoFromPing(var2);
            break;
         case 89:
            addXp(var2);
            break;
         case 90:
            this.updateOverlay(var2);
            break;
         case 91:
            NetChecksum.comparer.clientPacket(var2);
            break;
         case 92:
            constructedZone(var2);
            break;
         case 94:
            registerZone(var2);
            break;
         case 97:
            this.doWoundInfection(var2);
            break;
         case 98:
            this.doStitch(var2);
            break;
         case 99:
            this.doDisinfect(var2);
            break;
         case 100:
            this.doAdditionalPain(var2);
            break;
         case 101:
            this.doRemoveGlass(var2);
            break;
         case 102:
            this.doSplint(var2);
            break;
         case 103:
            this.doRemoveBullet(var2);
            break;
         case 104:
            this.doCleanBurn(var2);
            break;
         case 105:
            this.syncThumpable(var2);
            break;
         case 106:
            this.SyncDoorKey(var2);
            break;
         case 107:
            addXpFromCommand(var2);
            break;
         case 108:
            this.teleport(var2);
            break;
         case 109:
            this.removeBlood(var2);
            break;
         case 110:
            this.addExplosiveTrap(var2);
            break;
         case 112:
            this.receiveBodyDamageUpdate(var2);
            break;
         case 114:
            this.syncSafehouse(var2);
            break;
         case 116:
            this.stopFire(var2);
            break;
         case 117:
            this.doCataplasm(var2);
            break;
         case 118:
            this.addAlarm(var2);
            break;
         case 119:
            this.receiveSoundEveryPlayer(var2);
            break;
         case 120:
            this.receiveFurnaceChange(var2);
            break;
         case 121:
            this.receiveCustomColor(var2);
            break;
         case 122:
            this.syncCompost(var2);
            break;
         case 123:
            this.receivePlayerStatsChanges(var2);
            break;
         case 124:
            addXpFromPlayerStatsUI(var2);
            break;
         case 126:
            syncXp(var2);
            break;
         case 127:
            dealWithNetDataShort(var1, var2);
            break;
         case 128:
            this.receiveUserlog(var2);
            break;
         case 132:
            receiveAdminMessage(var2);
            break;
         case 133:
            receiveWakeUpOrder(var2);
            break;
         case 135:
            this.receiveDBSchema(var2);
            break;
         case 136:
            this.receiveTableResult(var2);
            break;
         case 138:
            this.receiveNewTxtColor(var2);
            break;
         case 139:
            this.syncNonPvpZone(var2);
            break;
         case 140:
            this.syncFaction(var2);
            break;
         case 141:
            this.receiveFactionInvite(var2);
            break;
         case 142:
            this.AcceptedFactionInvite(var2);
            break;
         case 144:
            gotTickets(var2);
            break;
         case 146:
            isRequestedToTrade(var2);
            break;
         case 147:
            tradingUIAddItem(var2);
            break;
         case 148:
            tradingUIRemoveItem(var2);
            break;
         case 149:
            tradingUIUpdateState(var2);
            break;
         case 150:
            receiveItemListNet(var2);
            break;
         case 151:
            this.receiveChunkObjectState(var2);
            break;
         case 153:
            this.sendInventory(var2);
            break;
         case 154:
            this.receiveInventory(var2);
            break;
         case 155:
            this.invMngSendItem(var2);
            break;
         case 156:
            this.invMngGotItem(var2);
            break;
         case 157:
            this.invMngRemoveItem(var2);
            break;
         case 158:
            this.PauseGame(var2);
            break;
         case 159:
            this.UnpauseGame(var2);
            break;
         case 160:
            GameTime.receiveTimeSync(var2, connection);
            break;
         case 161:
            this.SyncIsoObjectReq(var2);
            break;
         case 163:
            this.SyncWorldObjectsReq(var2);
            break;
         case 164:
            this.SyncObjectsReq(var2);
            break;
         case 165:
            this.onPlayerOnBeaten(var2);
            break;
         case 167:
            ClientPlayerDB.getInstance().clientLoadNetworkCharacter(var2, connection);
            break;
         case 172:
            this.receiveDamageFromCarCrash(var2);
            break;
         case 173:
            this.receiveAttachedItem(var2);
            break;
         case 174:
            this.receiveZombieHelmetFalling(var2);
            break;
         case 175:
            this.receiveBrokenGlass(var2);
            break;
         case 176:
            this.receiveChrHitByVehicle(var2);
            break;
         case 177:
            this.receivePerks(var2);
            break;
         case 178:
            this.receiveWeight(var2);
            break;
         case 179:
            this.receiveInjuries(var2);
            break;
         case 180:
            this.receiveHitReactionFromZombie(var2);
            break;
         case 181:
            this.syncEquippedRadioFreq(var2);
            break;
         case 182:
            ChatManager.getInstance().processInitPlayerChatPacket(var2);
            break;
         case 183:
            ChatManager.getInstance().processJoinChatPacket(var2);
            break;
         case 184:
            ChatManager.getInstance().processLeaveChatPacket(var2);
            break;
         case 186:
            ChatManager.getInstance().processChatMessagePacket(var2);
            break;
         case 189:
            ChatManager.getInstance().processAddTabPacket(var2);
            break;
         case 190:
            ChatManager.getInstance().processRemoveTabPacket(var2);
            break;
         case 191:
            ChatManager.getInstance().setFullyConnected();
            break;
         case 192:
            ChatManager.getInstance().processPlayerNotFound();
            break;
         case 193:
            this.receiveSafehouseInvite(var2);
            break;
         case 194:
            this.AcceptedSafehouseInvite(var2);
            break;
         case 200:
            receiveClimateManagerPacket(var2);
            break;
         case 201:
            IsoRegions.receiveServerUpdatePacket(var2);
            break;
         case 210:
            receiveEventUpdate(var2);
            break;
         case 211:
            receiveStatistic(var2);
            break;
         case 212:
            receiveStatisticRequest(var2);
            break;
         case 213:
            receiveHitVehicle(var2);
            break;
         case 216:
            receiveZombieAttackTarget(var2);
            break;
         case 218:
            receivePlayerUpdate(var2);
            break;
         case 32000:
            receiveGlobalModData(var2);
         }

      }
   }

   private void receiveBrokenGlass(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 != null) {
         var5.addBrokenGlass();
      }

   }

   private void receiveDamageFromCarCrash(ByteBuffer var1) {
      float var2 = var1.getFloat();
      if (IsoPlayer.getInstance().getVehicle() == null) {
         DebugLog.log(DebugType.Network, "Receive damage from car crash, can't find vehicle");
      } else {
         IsoPlayer.getInstance().getVehicle().addRandomDamageFromCrash(IsoPlayer.getInstance(), var2);
      }
   }

   private void receivePacketCounts(ByteBuffer var1) {
      for(int var2 = 0; var2 < 256; ++var2) {
         this.packetCountsFromAllClients[var2] = var1.getLong();
      }

   }

   public void requestPacketCounts() {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)47, var1);
      connection.endPacket();
   }

   public KahluaTable getPacketCounts(int var1) {
      long[] var2 = var1 == 1 ? this.packetCountsFromAllClients : this.packetCountsFromServer;

      for(int var3 = 0; var3 < 256; ++var3) {
         this.packetCountsTable.rawset(var3 + 1, BoxedStaticValues.toDouble((double)var2[var3]));
      }

      return this.packetCountsTable;
   }

   public static boolean IsClientPaused() {
      return isPaused;
   }

   private void PauseGame(ByteBuffer var1) {
      isPaused = true;
      LuaEventManager.triggerEvent("OnServerStartSaving");
   }

   private void UnpauseGame(ByteBuffer var1) {
      isPaused = false;
      LuaEventManager.triggerEvent("OnServerFinishSaving");
   }

   private void invMngRemoveItem(ByteBuffer var1) {
      int var2 = var1.getInt();
      InventoryItem var3 = IsoPlayer.getInstance().getInventory().getItemWithIDRecursiv(var2);
      if (var3 == null) {
         DebugLog.log("ERROR: invMngRemoveItem can not find " + var2 + " item.");
      } else {
         IsoPlayer.getInstance().removeWornItem(var3);
         if (var3.getCategory().equals("Clothing")) {
            LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
         }

         if (var3 == IsoPlayer.getInstance().getPrimaryHandItem()) {
            IsoPlayer.getInstance().setPrimaryHandItem((InventoryItem)null);
            LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
         } else if (var3 == IsoPlayer.getInstance().getSecondaryHandItem()) {
            IsoPlayer.getInstance().setSecondaryHandItem((InventoryItem)null);
            LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
         }

         boolean var4 = IsoPlayer.getInstance().getInventory().removeItemWithIDRecurse(var2);
         if (!var4) {
            DebugLog.log("ERROR: GameClient.invMngRemoveItem can not remove item " + var2);
         }

      }
   }

   private void invMngGotItem(ByteBuffer var1) throws IOException {
      int var2 = var1.getInt();
      InventoryItem var3 = null;

      try {
         var3 = InventoryItem.loadItem(var1, 184);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      if (var3 != null) {
         IsoPlayer.getInstance().getInventory().addItem(var3);
      }

   }

   private void invMngSendItem(ByteBuffer var1) throws IOException {
      int var2 = 0;
      String var3 = null;
      if (var1.get() == 1) {
         var3 = GameWindow.ReadString(var1);
      } else {
         var2 = var1.getInt();
      }

      int var4 = var1.getInt();
      InventoryItem var5 = null;
      if (var3 == null) {
         var5 = IsoPlayer.getInstance().getInventory().getItemWithIDRecursiv(var2);
         if (var5 == null) {
            DebugLog.log("ERROR: invMngRemoveItem can not find " + var2 + " item.");
            return;
         }
      } else {
         var5 = InventoryItemFactory.CreateItem(var3);
      }

      if (var5 != null) {
         if (var3 == null) {
            IsoPlayer.getInstance().removeWornItem(var5);
            if (var5.getCategory().equals("Clothing")) {
               LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
            }

            if (var5 == IsoPlayer.getInstance().getPrimaryHandItem()) {
               IsoPlayer.getInstance().setPrimaryHandItem((InventoryItem)null);
               LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
            } else if (var5 == IsoPlayer.getInstance().getSecondaryHandItem()) {
               IsoPlayer.getInstance().setSecondaryHandItem((InventoryItem)null);
               LuaEventManager.triggerEvent("OnClothingUpdated", IsoPlayer.getInstance());
            }

            IsoPlayer.getInstance().getInventory().removeItemWithIDRecurse(var5.getID());
         } else {
            IsoPlayer.getInstance().getInventory().RemoveOneOf(var3.split("\\.")[1]);
         }

         ByteBufferWriter var6 = connection.startPacket();
         PacketTypes.doPacket((short)156, var6);
         var6.putInt(var4);
         var5.saveWithSize(var6.bb, false);
         connection.endPacketImmediate();
      }

   }

   public static void invMngRequestItem(long var0, String var2, IsoPlayer var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)155, var4);
      if (var2 != null) {
         var4.putByte((byte)1);
         var4.putUTF(var2);
      } else {
         var4.putByte((byte)0);
         var4.putLong(var0);
      }

      var4.putInt(IsoPlayer.getInstance().getOnlineID());
      var4.putInt(var3.getOnlineID());
      connection.endPacketImmediate();
   }

   public static void invMngRequestRemoveItem(long var0, IsoPlayer var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)157, var3);
      var3.putLong(var0);
      var3.putInt(var2.getOnlineID());
      connection.endPacketImmediate();
   }

   private void syncFaction(ByteBuffer var1) {
      String var2 = GameWindow.ReadString(var1);
      String var3 = GameWindow.ReadString(var1);
      int var4 = var1.getInt();
      Faction var5 = Faction.getFaction(var2);
      if (var5 == null) {
         var5 = new Faction(var2, var3);
         Faction.getFactions().add(var5);
      }

      var5.getPlayers().clear();
      if (var1.get() == 1) {
         var5.setTag(GameWindow.ReadString(var1));
         var5.setTagColor(new ColorInfo(var1.getFloat(), var1.getFloat(), var1.getFloat(), 1.0F));
      }

      for(int var6 = 0; var6 < var4; ++var6) {
         var5.getPlayers().add(GameWindow.ReadString(var1));
      }

      var5.setOwner(var3);
      boolean var7 = var1.get() == 1;
      if (var7) {
         Faction.getFactions().remove(var5);
         DebugLog.log("faction: removed " + var2 + " owner=" + var5.getOwner());
      }

      LuaEventManager.triggerEvent("SyncFaction", var2);
   }

   private void syncNonPvpZone(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      String var6 = GameWindow.ReadString(var1);
      NonPvpZone var7 = NonPvpZone.getZoneByTitle(var6);
      if (var7 == null) {
         var7 = NonPvpZone.addNonPvpZone(var6, var2, var3, var4, var5);
      }

      if (var7 != null) {
         boolean var8 = var1.get() == 1;
         if (var8) {
            NonPvpZone.removeNonPvpZone(var6, true);
         }

      }
   }

   private void receiveNewTxtColor(ByteBuffer var1) {
      int var2 = var1.getInt();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var3 != null) {
         float var4 = var1.getFloat();
         float var5 = var1.getFloat();
         float var6 = var1.getFloat();
         var3.setSpeakColourInfo(new ColorInfo(var4, var5, var6, 1.0F));
      }
   }

   private void receiveSoundEveryPlayer(ByteBuffer var1) {
      String var2 = GameWindow.ReadString(var1);
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      DebugLog.log(DebugType.Sound, "sound: received " + var2 + " at " + var3 + "," + var4 + "," + var5);
      if (!Core.SoundDisabled) {
         FMOD_STUDIO_EVENT_DESCRIPTION var6 = FMODManager.instance.getEventDescription(var2);
         if (var6 == null) {
            return;
         }

         long var7 = javafmod.FMOD_Studio_System_CreateEventInstance(var6.address);
         if (var7 <= 0L) {
            return;
         }

         javafmod.FMOD_Studio_EventInstance_SetVolume(var7, (float)Core.getInstance().getOptionAmbientVolume() / 20.0F);
         javafmod.FMOD_Studio_EventInstance3D(var7, (float)var3, (float)var4, (float)var5);
         javafmod.FMOD_Studio_StartEvent(var7);
         javafmod.FMOD_Studio_ReleaseEventInstance(var7);
      }

   }

   private void doCataplasm(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null) {
         int var4 = var1.getInt();
         float var5 = var1.getFloat();
         float var6 = var1.getFloat();
         float var7 = var1.getFloat();
         if (var5 > 0.0F) {
            var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setPlantainFactor(var5);
         }

         if (var6 > 0.0F) {
            var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setComfreyFactor(var6);
         }

         if (var7 > 0.0F) {
            var3.getBodyDamage().getBodyPart(BodyPartType.FromIndex(var4)).setGarlicFactor(var7);
         }
      }

   }

   private void stopFire(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 != null) {
         var5.stopFire();
      }
   }

   private void addAlarm(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      IsoGridSquare var4 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, 0);
      if (var4 != null && var4.getBuilding() != null && var4.getBuilding().getDef() != null) {
         var4.getBuilding().getDef().bAlarmed = true;
         AmbientStreamManager.instance.doAlarm(var4.room.def);
      }
   }

   private void addExplosiveTrap(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 != null) {
         InventoryItem var6 = null;

         try {
            var6 = InventoryItem.loadItem(var1, 184);
         } catch (Exception var12) {
            var12.printStackTrace();
         }

         HandWeapon var7 = var6 != null ? (HandWeapon)var6 : null;
         IsoTrap var8 = new IsoTrap(var7, var5.getCell(), var5);
         int var9 = var1.getInt();
         boolean var10 = var1.get() == 1;
         boolean var11 = var1.get() == 1;
         if (!var11) {
            var5.AddTileObject(var8);
         }
      }

   }

   private void teleport(ByteBuffer var1) {
      byte var2 = var1.get();
      IsoPlayer var3 = IsoPlayer.players[var2];
      if (var3 != null && !var3.isDead()) {
         if (var3.getVehicle() != null) {
            var3.getVehicle().exit(var3);
            LuaEventManager.triggerEvent("OnExitVehicle", var3);
         }

         var3.setX(var1.getFloat());
         var3.setY(var1.getFloat());
         var3.setZ(var1.getFloat());
         var3.setLx(var3.getX());
         var3.setLy(var3.getY());
         var3.setLz(var3.getZ());
      }
   }

   private void removeBlood(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      boolean var5 = var1.get() == 1;
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var6 != null) {
         var6.removeBlood(true, var5);
      }

   }

   private void syncThumpable(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      byte var5 = var1.get();
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var6 == null) {
         this.delayPacket(var2, var3, var4);
      } else {
         if (var5 >= 0 && var5 < var6.getObjects().size()) {
            IsoObject var7 = (IsoObject)var6.getObjects().get(var5);
            if (var7 instanceof IsoThumpable) {
               IsoThumpable var8 = (IsoThumpable)var7;
               var8.lockedByCode = var1.getInt();
               var8.lockedByPadlock = var1.get() == 1;
               var8.keyId = var1.getInt();
            } else {
               DebugLog.log("syncThumpable: expected IsoThumpable index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
            }
         } else {
            DebugLog.log("syncThumpable: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
         }

      }
   }

   private void SyncDoorKey(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      byte var5 = var1.get();
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var6 == null) {
         this.delayPacket(var2, var3, var4);
      } else {
         if (var5 >= 0 && var5 < var6.getObjects().size()) {
            IsoObject var7 = (IsoObject)var6.getObjects().get(var5);
            if (var7 instanceof IsoDoor) {
               IsoDoor var8 = (IsoDoor)var7;
               var8.keyId = var1.getInt();
            } else {
               DebugLog.log("SyncDoorKey: expected IsoDoor index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
            }
         } else {
            DebugLog.log("SyncDoorKey: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
         }

      }
   }

   private static void constructedZone(ByteBuffer var0) {
      int var1 = var0.getInt();
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      IsoMetaGrid.Zone var4 = IsoWorld.instance.MetaGrid.getZoneAt(var1, var2, var3);
      if (var4 != null) {
         var4.setHaveConstruction(true);
      }

   }

   private void receiveAddCoopPlayer(ByteBuffer var1) {
      boolean var2 = var1.get() == 1;
      byte var3 = var1.get();
      if (var2) {
         for(int var4 = 0; var4 < IsoWorld.instance.AddCoopPlayers.size(); ++var4) {
            ((AddCoopPlayer)IsoWorld.instance.AddCoopPlayers.get(var4)).accessGranted(var3);
         }
      } else {
         String var6 = GameWindow.ReadStringUTF(var1);

         for(int var5 = 0; var5 < IsoWorld.instance.AddCoopPlayers.size(); ++var5) {
            ((AddCoopPlayer)IsoWorld.instance.AddCoopPlayers.get(var5)).accessDenied(var3, var6);
         }
      }

   }

   private void receivePlayerZombieDescriptor(ByteBuffer var1) {
      try {
         SharedDescriptors.Descriptor var2 = new SharedDescriptors.Descriptor();
         var2.load(var1, 184);
         SharedDescriptors.registerPlayerZombieDescriptor(var2);
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public static void checksumServer() {
      ByteBufferWriter var0 = connection.startPacket();
      PacketTypes.doPacket((short)91, var0);
      String var10001 = checksum;
      var0.putUTF(var10001 + ScriptManager.instance.getChecksum());
      connection.endPacketImmediate();
   }

   private static void registerZone(ByteBuffer var0) {
      String var1 = GameWindow.ReadString(var0);
      String var2 = GameWindow.ReadString(var0);
      int var3 = var0.getInt();
      int var4 = var0.getInt();
      int var5 = var0.getInt();
      int var6 = var0.getInt();
      int var7 = var0.getInt();
      int var8 = var0.getInt();
      ArrayList var9 = IsoWorld.instance.getMetaGrid().getZonesAt(var3, var4, var5);
      boolean var10 = false;
      Iterator var11 = var9.iterator();

      while(var11.hasNext()) {
         IsoMetaGrid.Zone var12 = (IsoMetaGrid.Zone)var11.next();
         if (var2.equals(var12.getType())) {
            var10 = true;
            var12.setName(var1);
            var12.setLastActionTimestamp(var8);
         }
      }

      if (!var10) {
         IsoWorld.instance.getMetaGrid().registerZone(var1, var2, var3, var4, var5, var6, var7);
      }

   }

   private static void addXp(ByteBuffer var0) {
      byte var1 = var0.get();
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      IsoPlayer var4 = IsoPlayer.players[var1];
      if (var4 != null && !var4.isDead()) {
         PerkFactory.Perk var5 = PerkFactory.Perks.fromIndex(var2);
         var4.getXp().AddXP(var5, (float)var3);
      }
   }

   private static void addXpFromCommand(ByteBuffer var0) {
      IsoPlayer var1 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var0.getShort()));
      PerkFactory.Perk var2 = PerkFactory.Perks.fromIndex(var0.getInt());
      if (var1 != null && !var1.isDead()) {
         var1.getXp().AddXP(var2, (float)var0.getInt());
      }

   }

   public void sendAddXpFromPlayerStatsUI(IsoPlayer var1, PerkFactory.Perk var2, int var3, boolean var4, boolean var5) {
      if (canModifyPlayerStats()) {
         ByteBufferWriter var6 = connection.startPacket();
         PacketTypes.doPacket((short)124, var6);
         var6.putInt(var1.getOnlineID());
         if (!var5) {
            var6.putInt(0);
            var6.putInt(var2.index());
            var6.putInt(var3);
            var6.putByte((byte)(var4 ? 1 : 0));
         }

         connection.endPacketImmediate();
      }
   }

   private static void syncXp(ByteBuffer var0) {
      IsoPlayer var1 = (IsoPlayer)IDToPlayerMap.get(var0.getInt());
      if (var1 != null && !var1.isDead()) {
         try {
            var1.getXp().load(var0, 184);
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

   }

   public void sendSyncXp(IsoPlayer var1) {
      if (canModifyPlayerStats()) {
         ByteBufferWriter var2 = connection.startPacket();
         PacketTypes.doPacket((short)126, var2);
         var2.putInt(var1.getOnlineID());

         try {
            var1.getXp().save(var2.bb);
         } catch (IOException var4) {
            var4.printStackTrace();
         }

         connection.endPacketImmediate();
      }
   }

   public void sendTransactionID(IsoPlayer var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)134, var2);
      var2.putInt(var1.getOnlineID());
      var2.putInt(var1.getTransactionID());
      connection.endPacketImmediate();
   }

   private void receiveUserlog(ByteBuffer var1) {
      ArrayList var2 = new ArrayList();
      int var3 = var1.getInt();
      String var4 = GameWindow.ReadString(var1);

      for(int var5 = 0; var5 < var3; ++var5) {
         var2.add(new Userlog(var4, Userlog.UserlogType.fromIndex(var1.getInt()).toString(), GameWindow.ReadString(var1), GameWindow.ReadString(var1), var1.getInt()));
      }

      LuaEventManager.triggerEvent("OnReceiveUserlog", var4, var2);
   }

   private static void addXpFromPlayerStatsUI(ByteBuffer var0) {
      IsoPlayer var1 = (IsoPlayer)IDToPlayerMap.get(var0.getInt());
      int var2 = var0.getInt();
      if (var1 != null && !var1.isDead() && var2 == 0) {
         PerkFactory.Perk var3 = PerkFactory.Perks.fromIndex(var0.getInt());
         var1.getXp().AddXP(var3, (float)var0.getInt(), false, var0.get() == 1, false, true);
      }

   }

   private static void getInfoFromPing(ByteBuffer var0) {
      String var1 = GameWindow.ReadString(var0);
      int var10000 = var0.getInt() - 1;
      String var2 = var10000 + "/" + var0.getInt();
      LuaEventManager.triggerEvent("ServerPinged", var1, var2);
      connection.forceDisconnect();
      askPing = false;
   }

   private static void playerChangeSafety(ByteBuffer var0) {
      IsoPlayer var1 = (IsoPlayer)IDToPlayerMap.get(var0.getInt());
      if (var1 != null) {
         var1.setSafety(var0.get() == 1);
      }

   }

   private static void AddItemInInventory(ByteBuffer var0) {
      short var1 = var0.getShort();
      String var2 = GameWindow.ReadString(var0);
      int var3 = var0.getInt();
      IsoPlayer var4 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var1));
      if (var4 != null && !var4.isDead()) {
         var4.getInventory().AddItems(var2, var3);
      }

   }

   private static void kick(ByteBuffer var0) {
      String var1 = GameWindow.ReadString(var0);
      if (var1 != null && !var1.equals("")) {
         ChatManager.getInstance().showServerChatMessage(var1);
      }

      connection.username = null;
      GameWindow.savePlayer();
      GameWindow.bServerDisconnected = true;
      GameWindow.kickReason = var1;
      connection.forceDisconnect();
      connection.close();
   }

   public void addDisconnectPacket(int var1) {
      synchronized(this.delayedDisconnect) {
         this.delayedDisconnect.add(var1);
      }
   }

   public void connectionLost() {
      this.bConnectionLost = true;
      positions.clear();
   }

   public static void SendCommandToServer(String var0) {
      if (ServerOptions.clientOptionsList == null) {
         ServerOptions.initClientCommandsHelp();
      }

      if (var0.startsWith("/roll")) {
         try {
            int var1 = Integer.parseInt(var0.split(" ")[1]);
            if (var1 > 100) {
               ChatManager.getInstance().showServerChatMessage((String)ServerOptions.clientOptionsList.get("roll"));
               return;
            }
         } catch (Exception var2) {
            ChatManager.getInstance().showServerChatMessage((String)ServerOptions.clientOptionsList.get("roll"));
            return;
         }

         if (!IsoPlayer.getInstance().getInventory().contains("Dice") && accessLevel.equals("")) {
            ChatManager.getInstance().showServerChatMessage((String)ServerOptions.clientOptionsList.get("roll"));
            return;
         }
      }

      if (var0.startsWith("/card") && !IsoPlayer.getInstance().getInventory().contains("CardDeck") && accessLevel.equals("")) {
         ChatManager.getInstance().showServerChatMessage((String)ServerOptions.clientOptionsList.get("card"));
      } else {
         ByteBufferWriter var3 = connection.startPacket();
         PacketTypes.doPacket((short)81, var3);
         var3.putUTF(var0);
         connection.endPacketImmediate();
      }
   }

   private boolean gameLoadingDealWithNetData(ZomboidNetData var1) {
      ByteBuffer var2 = var1.buffer;

      try {
         switch(var1.type) {
         case 4:
            break;
         case 5:
            VehicleManager.loadingClientPacket(var2);
            return false;
         case 6:
            int var5 = var2.position();
            if (!this.receivePlayerConnectWhileLoading(var2)) {
               var2.position(var5);
               return false;
            }
            break;
         case 13:
            this.playerTimeout(var2);
            break;
         case 15:
            ClientServerMap.receivePacket(var2);
            break;
         case 18:
            assert false;
            break;
         case 21:
            this.receiveConnectionDetails(var2);
            break;
         case 36:
            assert false;
            break;
         case 37:
            this.receiveRequestData(var2);
            break;
         case 40:
            String var3 = GameWindow.ReadString(var2);
            String[] var4 = var3.split("##");
            LuaEventManager.triggerEvent("OnConnectFailed", var4.length > 0 ? Translator.getText("UI_OnConnectFailed_" + var4[0], var4.length > 1 ? var4[1] : null, var4.length > 2 ? var4[2] : null, var4.length > 3 ? var4[3] : null) : null);
            break;
         case 83:
            GameWindow.kickReason = GameWindow.ReadStringUTF(var2);
            GameWindow.bServerDisconnected = true;
            break;
         case 87:
            getInfoFromPing(var2);
            break;
         case 91:
            NetChecksum.comparer.clientPacket(var2);
            break;
         case 171:
            this.receiveSpawnRegion(var2);
            break;
         default:
            if (Core.bDebug) {
               DebugLog.log(DebugType.Network, "Delay processing packet of type " + var1.type + " while loading game");
            }

            return false;
         }
      } catch (Exception var6) {
         DebugLog.log(DebugType.Network, "Error with packet of type: " + var1.type);
         var6.printStackTrace();
      }

      ZomboidNetDataPool.instance.discard(var1);
      return true;
   }

   private void receiveWorldMessage(ByteBuffer var1) {
      String var2 = GameWindow.ReadStringUTF(var1);
      String var3 = GameWindow.ReadString(var1);
      var3 = var3.replaceAll("<", "&lt;");
      var3 = var3.replaceAll(">", "&gt;");
      ChatManager.getInstance().addMessage(var2, var3);
   }

   private void startRain(ByteBuffer var1) {
      RainManager.setRandRainMin(var1.getInt());
      RainManager.setRandRainMax(var1.getInt());
      RainManager.startRaining();
      RainManager.RainDesiredIntensity = var1.getFloat();
   }

   private void receiveWeather(ByteBuffer var1) {
      GameTime var2 = GameTime.getInstance();
      var2.setDawn(var1.get() & 255);
      var2.setDusk(var1.get() & 255);
      var2.setThunderDay(var1.get() == 1);
      var2.setMoon(var1.getFloat());
      var2.setAmbientMin(var1.getFloat());
      var2.setAmbientMax(var1.getFloat());
      var2.setViewDistMin(var1.getFloat());
      var2.setViewDistMax(var1.getFloat());
      IsoWorld.instance.setGlobalTemperature(var1.getFloat());
      IsoWorld.instance.setWeather(GameWindow.ReadStringUTF(var1));
      ErosionMain.getInstance().receiveState(var1);
   }

   private void receiveSyncClock(ByteBuffer var1) {
      GameTime var2 = GameTime.getInstance();
      boolean var3 = bFastForward;
      bFastForward = var1.get() == 1;
      float var4 = var1.getFloat();
      float var5 = var2.getTimeOfDay() - var2.getLastTimeOfDay();
      var2.setTimeOfDay(var4);
      var2.setLastTimeOfDay(var4 - var5);
      if (var2.getLastTimeOfDay() < 0.0F) {
         var2.setLastTimeOfDay(var4 - var5 + 24.0F);
      }

      var2.ServerLastTimeOfDay = var2.ServerTimeOfDay;
      var2.ServerTimeOfDay = var4;
      if (var2.ServerLastTimeOfDay <= 7.0F && var2.ServerTimeOfDay > 7.0F) {
         var2.setNightsSurvived(var2.getNightsSurvived() + 1);
      }

      if (var2.ServerLastTimeOfDay > var2.ServerTimeOfDay) {
         ++var2.ServerNewDays;
      }

   }

   private void receiveServerCommand(ByteBuffer var1) {
      String var2 = GameWindow.ReadString(var1);
      String var3 = GameWindow.ReadString(var1);
      boolean var4 = var1.get() == 1;
      KahluaTable var5 = null;
      if (var4) {
         var5 = LuaManager.platform.newTable();

         try {
            TableNetworkUtils.load(var5, var1);
         } catch (Exception var7) {
            var7.printStackTrace();
            return;
         }
      }

      LuaEventManager.triggerEvent("OnServerCommand", var2, var3, var5);
   }

   private boolean receiveLargeFilePart(ByteBuffer var1, String var2) {
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      File var6 = new File(var2);

      try {
         FileOutputStream var7 = new FileOutputStream(var6, var4 > 0);

         try {
            BufferedOutputStream var8 = new BufferedOutputStream(var7);

            try {
               var8.write(var1.array(), var1.position(), var1.limit() - var1.position());
            } catch (Throwable var13) {
               try {
                  var8.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }

               throw var13;
            }

            var8.close();
         } catch (Throwable var14) {
            try {
               var7.close();
            } catch (Throwable var11) {
               var14.addSuppressed(var11);
            }

            throw var14;
         }

         var7.close();
      } catch (IOException var15) {
         var15.printStackTrace();
      }

      return var4 + var5 >= var3;
   }

   private void receiveRequestData(ByteBuffer var1) {
      String var2 = GameWindow.ReadStringUTF(var1);
      if ("descriptors.bin".equals(var2)) {
         try {
            this.receiveZombieDescriptors(var1);
         } catch (IOException var5) {
            var5.printStackTrace();
         }

         this.request = GameClient.RequestState.ReceivedDescriptors;
      }

      if ("playerzombiedesc".equals(var2)) {
         try {
            this.receivePlayerZombieDescriptors(var1);
         } catch (IOException var4) {
            var4.printStackTrace();
         }

         this.request = GameClient.RequestState.ReceivedPlayerZombieDescriptors;
      }

      boolean var3;
      if ("map_meta.bin".equals(var2)) {
         var3 = this.receiveLargeFilePart(var1, ZomboidFileSystem.instance.getFileNameInCurrentSave("map_meta.bin"));
         if (var3) {
            this.request = GameClient.RequestState.ReceivedMetaGrid;
         }
      }

      if ("map_zone.bin".equals(var2)) {
         var3 = this.receiveLargeFilePart(var1, ZomboidFileSystem.instance.getFileNameInCurrentSave("map_zone.bin"));
         if (var3) {
            this.request = GameClient.RequestState.ReceivedMapZone;
         }
      }

   }

   public void GameLoadingRequestData() {
      this.request = GameClient.RequestState.Start;

      while(this.request != GameClient.RequestState.Complete) {
         ByteBufferWriter var1;
         switch(this.request) {
         case Start:
            var1 = connection.startPacket();
            PacketTypes.doPacket((short)37, var1);
            var1.putUTF("descriptors.bin");
            connection.endPacketImmediate();
            this.request = GameClient.RequestState.RequestDescriptors;
         case RequestDescriptors:
         case RequestMetaGrid:
         case RequestMapZone:
         case RequestPlayerZombieDescriptors:
         case Complete:
         default:
            break;
         case ReceivedDescriptors:
            var1 = connection.startPacket();
            PacketTypes.doPacket((short)37, var1);
            var1.putUTF("map_meta.bin");
            connection.endPacketImmediate();
            this.request = GameClient.RequestState.RequestMetaGrid;
            break;
         case ReceivedMetaGrid:
            var1 = connection.startPacket();
            PacketTypes.doPacket((short)37, var1);
            var1.putUTF("map_zone.bin");
            connection.endPacketImmediate();
            this.request = GameClient.RequestState.RequestMapZone;
            break;
         case ReceivedMapZone:
            var1 = connection.startPacket();
            PacketTypes.doPacket((short)37, var1);
            var1.putUTF("playerzombiedesc");
            connection.endPacketImmediate();
            this.request = GameClient.RequestState.RequestPlayerZombieDescriptors;
            break;
         case ReceivedPlayerZombieDescriptors:
            this.request = GameClient.RequestState.Complete;
         }

         try {
            Thread.sleep(30L);
         } catch (InterruptedException var3) {
            var3.printStackTrace();
         }
      }

   }

   private void receiveMetaGrid(ByteBuffer var1) {
      short var2 = var1.getShort();
      short var3 = var1.getShort();
      short var4 = var1.getShort();
      IsoMetaGrid var5 = IsoWorld.instance.MetaGrid;
      if (var2 >= var5.getMinX() && var2 <= var5.getMaxX() && var3 >= var5.getMinY() && var3 <= var5.getMaxY()) {
         IsoMetaCell var6 = var5.getCellData(var2, var3);
         if (var6.info != null && var4 >= 0 && var4 < var6.info.RoomList.size()) {
            var6.info.getRoom(var4).def.bLightsActive = var1.get() == 1;
         }
      }
   }

   private void receiveCustomColor(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var6 == null) {
         this.delayPacket(var2, var3, var4);
      } else {
         if (var6 != null && var5 < var6.getObjects().size()) {
            IsoObject var7 = (IsoObject)var6.getObjects().get(var5);
            if (var7 != null) {
               var7.setCustomColor(new ColorInfo(var1.getFloat(), var1.getFloat(), var1.getFloat(), var1.getFloat()));
            }
         }

      }
   }

   private void updateItemSprite(ByteBuffer var1) {
      int var2 = var1.getInt();
      String var3 = GameWindow.ReadStringUTF(var1);
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      int var6 = var1.getInt();
      int var7 = var1.getInt();
      IsoGridSquare var8 = IsoWorld.instance.CurrentCell.getGridSquare(var4, var5, var6);
      if (var8 == null) {
         this.delayPacket(var4, var5, var6);
      } else {
         if (var8 != null && var7 < var8.getObjects().size()) {
            try {
               IsoObject var9 = (IsoObject)var8.getObjects().get(var7);
               if (var9 != null) {
                  boolean var10 = var9.sprite != null && var9.sprite.getProperties().Is("HitByCar") && var9.sprite.getProperties().Val("DamagedSprite") != null && !var9.sprite.getProperties().Val("DamagedSprite").isEmpty();
                  var9.sprite = IsoSpriteManager.instance.getSprite(var2);
                  if (var9.sprite == null && !var3.isEmpty()) {
                     var9.setSprite(var3);
                  }

                  var9.RemoveAttachedAnims();
                  int var11 = var1.get() & 255;

                  for(int var12 = 0; var12 < var11; ++var12) {
                     int var13 = var1.getInt();
                     IsoSprite var14 = IsoSpriteManager.instance.getSprite(var13);
                     if (var14 != null) {
                        var9.AttachExistingAnim(var14, 0, 0, false, 0, false, 0.0F);
                     }
                  }

                  if (var9 instanceof IsoThumpable && var10 && (var9.sprite == null || !var9.sprite.getProperties().Is("HitByCar"))) {
                     ((IsoThumpable)var9).setBlockAllTheSquare(false);
                  }

                  var8.RecalcAllWithNeighbours(true);
               }
            } catch (Exception var15) {
            }
         }

      }
   }

   private void updateOverlay(ByteBuffer var1) {
      String var2 = GameWindow.ReadStringUTF(var1);
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      float var6 = var1.getFloat();
      float var7 = var1.getFloat();
      float var8 = var1.getFloat();
      float var9 = var1.getFloat();
      int var10 = var1.getInt();
      IsoGridSquare var11 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, var5);
      if (var11 == null) {
         this.delayPacket(var3, var4, var5);
      } else {
         if (var11 != null && var10 < var11.getObjects().size()) {
            try {
               IsoObject var12 = (IsoObject)var11.getObjects().get(var10);
               if (var12 != null) {
                  var12.setOverlaySprite(var2, var6, var7, var8, var9, false);
               }
            } catch (Exception var13) {
            }
         }

      }
   }

   private KahluaTable copyTable(KahluaTable var1) {
      KahluaTable var2 = LuaManager.platform.newTable();
      KahluaTableIterator var3 = var1.iterator();

      while(var3.advance()) {
         Object var4 = var3.getKey();
         Object var5 = var3.getValue();
         if (var5 instanceof KahluaTable) {
            var2.rawset(var4, this.copyTable((KahluaTable)var5));
         } else {
            var2.rawset(var4, var5);
         }
      }

      return var2;
   }

   public KahluaTable getServerSpawnRegions() {
      return this.copyTable(this.ServerSpawnRegions);
   }

   public static void toggleSafety(IsoPlayer var0) {
      if (var0 != null) {
         ByteBufferWriter var1 = connection.startPacket();
         PacketTypes.doPacket((short)86, var1);
         var1.putByte((byte)var0.PlayerIndex);
         connection.endPacketImmediate();
      }
   }

   private void startFire(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      boolean var6 = var1.get() == 1;
      int var7 = var1.getInt();
      int var8 = var1.getInt();
      int var9 = var1.getInt();
      boolean var10 = var1.get() == 1;
      IsoGridSquare var11 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var11 == null) {
         this.delayPacket(var2, var3, var4);
      } else if (!IsoFire.CanAddFire(var11, var6, var10)) {
         DebugLog.log("not adding fire that is on the server " + var2 + "," + var3);
      } else {
         IsoFire var12 = var10 ? new IsoFire(IsoWorld.instance.CurrentCell, var11, var6, var5, var8, true) : new IsoFire(IsoWorld.instance.CurrentCell, var11, var6, var5, var8);
         var12.SpreadDelay = var7;
         var12.numFlameParticles = var9;
         IsoFireManager.Add(var12);
         var11.getObjects().add(var12);
      }
   }

   private void AddCorpseToMap(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      IsoObject var5 = WorldItemTypes.createFromBuffer(var1);
      var5.loadFromRemoteBuffer(var1, false);
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var6 == null) {
         this.delayPacket(var2, var3, var4);
      } else {
         if (var6 != null) {
            var6.addCorpse((IsoDeadBody)var5, true);
         }

      }
   }

   private void RemoveCorpseFromMap(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      int var6 = var1.getInt();
      IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var7 == null) {
         this.delayPacket(var2, var3, var4);
      } else {
         IsoPlayer var8 = null;
         IsoDeadBody var9 = null;
         if (var6 != -1) {
            var8 = (IsoPlayer)IDToPlayerMap.get(var6);
         }

         if (var5 >= 0 && var5 < var7.getStaticMovingObjects().size()) {
            var9 = (IsoDeadBody)var7.getStaticMovingObjects().get(var5);
            var7.removeCorpse(var9, true);
         } else {
            DebugLog.log(DebugType.Multiplayer, "IsoDeadBody was not found on square");
            if (var8 != null) {
               var9 = (IsoDeadBody)instance.PlayerToBody.get(var8);
               if (var9 != null) {
                  var9.removeFromWorld();
                  var9.removeFromSquare();
               }
            }
         }

         if (var8 != null) {
            instance.PlayerToBody.remove(var8);
         }

         if (var5 < 0) {
            DebugLog.log("Remove corpse index <||> 0, index = " + var5 + " X:" + var2 + " Y:" + var3 + " Z:" + var4);
         }

      }
   }

   private void SyncPlayerInventory(ByteBuffer var1) {
      short var2 = var1.getShort();
      byte var3 = var1.get();
      IsoPlayer var4 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var4 != null) {
         DebugLog.log("SyncPlayerInventory " + var4.username);
         var4.setInventory(new ItemContainer());

         try {
            ArrayList var5 = var4.getInventory().load(var1, 184);
            byte var6 = var1.get();

            for(int var7 = 0; var7 < var6; ++var7) {
               String var8 = GameWindow.ReadString(var1);
               short var9 = var1.getShort();
               if (var9 >= 0 && var9 < var5.size() && var4.getBodyLocationGroup().getLocation(var8) != null) {
                  var4.setWornItem(var8, (InventoryItem)var5.get(var9));
               }
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         }

         IsoDeadBody var11 = (IsoDeadBody)this.PlayerToBody.get(var4);
         if (var11 != null) {
            var11.setContainer(var4.getInventory());
            var11.setWornItems(var4.getWornItems());
            var4.setInventory(new ItemContainer());
            var4.clearWornItems();
            this.PlayerToBody.remove(var4);
         }
      }

   }

   private void receiveModData(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 == null && IsoWorld.instance.isValidSquare(var2, var3, var4) && IsoWorld.instance.CurrentCell.getChunkForGridSquare(var2, var3, var4) != null) {
         var5 = IsoGridSquare.getNew(IsoWorld.instance.getCell(), (SliceY)null, var2, var3, var4);
      }

      if (var5 == null) {
         this.delayPacket(var2, var3, var4);
      } else {
         try {
            var5.getModData().load((ByteBuffer)var1, 184);
         } catch (IOException var7) {
            var7.printStackTrace();
         }

         LuaEventManager.triggerEvent("onLoadModDataFromServer", var5);
      }
   }

   private void receiveObjectModData(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      boolean var6 = var1.get() == 1;
      IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var7 == null) {
         this.delayPacket(var2, var3, var4);
      } else {
         if (var7 != null && var5 >= 0 && var5 < var7.getObjects().size()) {
            IsoObject var8 = (IsoObject)var7.getObjects().get(var5);
            if (var6) {
               try {
                  var8.getModData().load((ByteBuffer)var1, 184);
               } catch (IOException var10) {
                  var10.printStackTrace();
               }
            } else {
               var8.getModData().wipe();
            }
         } else if (var7 != null) {
            DebugLog.log("receiveObjectModData: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
         } else if (Core.bDebug) {
            DebugLog.log("receiveObjectModData: sq is null x,y,z=" + var2 + "," + var3 + "," + var4);
         }

      }
   }

   private void receiveObjectChange(ByteBuffer var1) {
      byte var2 = var1.get();
      int var3;
      String var4;
      if (var2 == 1) {
         var3 = var1.getInt();
         var4 = GameWindow.ReadString(var1);
         if (Core.bDebug) {
            DebugLog.log("receiveObjectChange " + var4);
         }

         IsoPlayer var5 = (IsoPlayer)IDToPlayerMap.get(var3);
         if (var5 != null) {
            var5.loadChange(var4, var1);
         }
      } else if (var2 == 2) {
         short var11 = var1.getShort();
         var4 = GameWindow.ReadString(var1);
         if (Core.bDebug) {
            DebugLog.log("receiveObjectChange " + var4);
         }

         BaseVehicle var13 = VehicleManager.instance.getVehicleByID(var11);
         if (var13 != null) {
            var13.loadChange(var4, var1);
         } else if (Core.bDebug) {
            DebugLog.log("receiveObjectChange: unknown vehicle id=" + var11);
         }
      } else {
         int var6;
         String var7;
         IsoGridSquare var8;
         int var12;
         int var14;
         if (var2 == 3) {
            var3 = var1.getInt();
            var12 = var1.getInt();
            var14 = var1.getInt();
            var6 = var1.getInt();
            var7 = GameWindow.ReadString(var1);
            if (Core.bDebug) {
               DebugLog.log("receiveObjectChange " + var7);
            }

            var8 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var12, var14);
            if (var8 == null) {
               this.delayPacket(var3, var12, var14);
               return;
            }

            for(int var9 = 0; var9 < var8.getWorldObjects().size(); ++var9) {
               IsoWorldInventoryObject var10 = (IsoWorldInventoryObject)var8.getWorldObjects().get(var9);
               if (var10.getItem() != null && var10.getItem().getID() == var6) {
                  var10.loadChange(var7, var1);
                  return;
               }
            }

            if (Core.bDebug) {
               DebugLog.log("receiveObjectChange: itemID=" + var6 + " is invalid x,y,z=" + var3 + "," + var12 + "," + var14);
            }
         } else {
            var3 = var1.getInt();
            var12 = var1.getInt();
            var14 = var1.getInt();
            var6 = var1.getInt();
            var7 = GameWindow.ReadString(var1);
            if (Core.bDebug) {
               DebugLog.log("receiveObjectChange " + var7);
            }

            var8 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var12, var14);
            if (var8 == null) {
               this.delayPacket(var3, var12, var14);
               return;
            }

            if (var8 != null && var6 >= 0 && var6 < var8.getObjects().size()) {
               IsoObject var15 = (IsoObject)var8.getObjects().get(var6);
               var15.loadChange(var7, var1);
            } else if (var8 != null) {
               if (Core.bDebug) {
                  DebugLog.log("receiveObjectChange: index=" + var6 + " is invalid x,y,z=" + var3 + "," + var12 + "," + var14);
               }
            } else if (Core.bDebug) {
               DebugLog.log("receiveObjectChange: sq is null x,y,z=" + var3 + "," + var12 + "," + var14);
            }
         }
      }

   }

   private void RemoveContestedItemsFromInventory(ByteBuffer var1) {
      int var2 = var1.getInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getInt();

         for(int var5 = 0; var5 < IsoPlayer.numPlayers; ++var5) {
            IsoPlayer var6 = IsoPlayer.players[var5];
            if (var6 != null && !var6.isDead()) {
               var6.getInventory().removeItemWithIDRecurse(var4);
            }
         }
      }

   }

   public static void sendDeadZombie(IsoZombie var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)39, var1);

      try {
         DeadBodyPacket var2 = new DeadBodyPacket();
         var2.set(var0);
         var2.write(var1);
         connection.endPacketImmediate();
         DebugLog.log(DebugType.Multiplayer, "DeadBody send: " + var2.getDescription());
         var0.networkAI.deadZombie = var2;
      } catch (Exception var3) {
         connection.cancelPacket();
         DebugLog.log("DeadZombie send: failed");
         var3.printStackTrace();
      }

   }

   private void doDeadZombie(ByteBuffer var1) {
      DeadBodyPacket var2 = new DeadBodyPacket();
      var2.parse(var1);
      boolean var3 = var1.get() == 1;
      DebugLog.log(DebugType.Multiplayer, "DeadBody receive: " + var2.getDescription());
      IsoZombie var4 = var2.zombie;
      if (var4 != null) {
         IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare((double)var2.x, (double)var2.y, (double)var2.z);
         if (var4.getCurrentSquare() != var5 || var4.getAnimAngleRadians() != var2.angle) {
            DebugLog.log(DebugType.Multiplayer, String.format("Teleport dead zombie %d: ( %f ; %f ; %f ) => ( %f ; %f ; %f ) with angle %.6f => %.6f", var4.getOnlineID(), var4.x, var4.y, var4.z, var2.x, var2.y, var2.z, var4.getAnimAngleRadians(), var2.angle));
            var4.setX(var2.x);
            var4.setY(var2.y);
            var4.setZ(var2.z);
            if (var4.hasAnimationPlayer() && var4.getAnimationPlayer().isReady() && !var4.getAnimationPlayer().isBoneTransformsNeedFirstFrame()) {
               var4.getAnimationPlayer().setAngle(var2.angle);
            } else {
               var4.getForwardDirection().setDirection(var2.angle);
            }
         }

         var4.setFallOnFront(var2.isFallOnFront);
         var4.bCrawling = var2.isCrawling;
         var4.setCurrent(var5);
         var4.dir = var2.direction;
         var4.getInventory().removeAllItems();
         var4.getInventory().setSourceGrid(var4.getCurrentSquare());
         var4.getWornItems().clear();
         var4.getAttachedItems().clear();
         if (var3) {
            try {
               ArrayList var6 = var4.getInventory().load(var1, 184);
               byte var7 = var1.get();

               int var8;
               String var9;
               short var10;
               for(var8 = 0; var8 < var7; ++var8) {
                  var9 = GameWindow.ReadStringUTF(var1);
                  var10 = var1.getShort();
                  if (var10 >= 0 && var10 < var6.size() && var4.getBodyLocationGroup().getLocation(var9) != null) {
                     var4.getWornItems().setItem(var9, (InventoryItem)var6.get(var10));
                  }
               }

               var7 = var1.get();

               for(var8 = 0; var8 < var7; ++var8) {
                  var9 = GameWindow.ReadStringUTF(var1);
                  var10 = var1.getShort();
                  if (var10 >= 0 && var10 < var6.size() && var4.getAttachedLocationGroup().getLocation(var9) != null) {
                     var4.getAttachedItems().setItem(var9, (InventoryItem)var6.get(var10));
                  }
               }
            } catch (IOException var11) {
               var11.printStackTrace();
            }
         }

         if (var4.networkAI.deadZombie == null) {
            new IsoDeadBody(var4);
         }

         var4.networkAI.deadZombie = var2;
      }

      if (var2.lastPlayerHit == IsoPlayer.getInstance().getOnlineID()) {
         IsoPlayer.getInstance().setZombieKills(IsoPlayer.getInstance().getZombieKills() + 1);
      }

   }

   private static void doZomboidDataInMainLoop(ZomboidNetData var0) {
   }

   private void onPlayerDeath(ByteBuffer var1) {
      int var2 = var1.getInt();
      float var3 = var1.getFloat();
      float var4 = var1.getFloat();
      float var5 = var1.getFloat();
      int var6 = var1.getInt();
      IsoPlayer var7 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var7 != null) {
         try {
            var7.getInventory().clear();
            var7.getWornItems().clear();
            ArrayList var8 = var7.getInventory().load(var1, IsoWorld.getWorldVersion());
            byte var9 = var1.get();

            for(int var10 = 0; var10 < var9; ++var10) {
               String var11 = GameWindow.ReadString(var1);
               short var12 = var1.getShort();
               if (var12 >= 0 && var12 < var8.size() && var7.getWornItems().getBodyLocationGroup().getLocation(var11) != null) {
                  var7.getWornItems().setItem(var11, (InventoryItem)var8.get(var12));
               }
            }

            var7.setX(var3);
            var7.setY(var4);
            var7.setZ(var5);
            var7.setDir(var6);
            IsoGridSquare var14 = IsoWorld.instance.getCell().getGridSquare((double)var3, (double)var4, (double)var5);
            if (var14 != null) {
               var7.setCurrent(var14);
            }
         } catch (IOException var13) {
            var13.printStackTrace();
         }

         if (var7.getCurrentSquare() != null) {
            var7.setHealth(-1.0F);
            var7.getBodyDamage().setOverallBodyHealth(-1.0F);
            var7.setStateMachineLocked(false);
            var7.setRemoteMoveX(0.0F);
            var7.setRemoteMoveY(0.0F);
         }

      }
   }

   private void onPlayerOnBeaten(ByteBuffer var1) {
      int var2 = var1.getInt();
      float var3 = var1.getFloat();
      float var4 = var1.getFloat();
      float var5 = var1.getFloat();
      IsoPlayer var6 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var6 != null) {
         var6.doBeatenVehicle(var3, var4, var5, true);
      }
   }

   private void removeZombieCompletely(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoZombie var3 = (IsoZombie)IDToZombieMap.get(var2);
      if (var3 != null) {
         if (var3.getCurrentSquare() == null) {
            return;
         }

         if (var3.networkAI.deadZombie != null) {
            return;
         }

         new IsoDeadBody(var3);

         assert !IDToZombieMap.containsKey(var2);
      }

   }

   private void RemoveItemFromMap(ByteBuffer var1) {
      if (IsoWorld.instance.CurrentCell != null) {
         int var2 = var1.getInt();
         int var3 = var1.getInt();
         int var4 = var1.getInt();
         int var5 = var1.getInt();
         IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
         if (var6 == null) {
            this.delayPacket(var2, var3, var4);
         } else {
            if (var6 != null && var5 >= 0 && var5 < var6.getObjects().size()) {
               IsoObject var7 = (IsoObject)var6.getObjects().get(var5);
               var6.RemoveTileObject(var7);
               if (var7 instanceof IsoWorldInventoryObject || var7.getContainer() != null) {
                  LuaEventManager.triggerEvent("OnContainerUpdate", var7);
               }
            } else if (Core.bDebug) {
               DebugLog.log("RemoveItemFromMap: sq is null or index is invalid");
            }

         }
      }
   }

   private void removeItemFromContainer(ByteBuffer var1) {
      if (IsoWorld.instance.CurrentCell != null) {
         ByteBufferReader var2 = new ByteBufferReader(var1);
         short var3 = var1.getShort();
         int var4 = var2.getInt();
         int var5 = var2.getInt();
         int var6 = var2.getInt();
         IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var4, var5, var6);
         if (var7 != null) {
            byte var16;
            int var18;
            int var22;
            int var25;
            if (var3 == 0) {
               var16 = var2.getByte();
               var18 = var1.getInt();
               if (var16 < 0 || var16 >= var7.getStaticMovingObjects().size()) {
                  DebugLog.log("ERROR: removeItemFromContainer: invalid corpse index");
                  return;
               }

               IsoObject var21 = (IsoObject)var7.getStaticMovingObjects().get(var16);
               if (var21 != null && var21.getContainer() != null) {
                  for(var22 = 0; var22 < var18; ++var22) {
                     var25 = var2.getInt();
                     var21.getContainer().removeItemWithID(var25);
                     var21.getContainer().setExplored(true);
                  }
               }
            } else if (var3 == 1) {
               int var17 = var2.getInt();
               var18 = var1.getInt();
               ItemContainer var19 = null;

               for(var22 = 0; var22 < var7.getWorldObjects().size(); ++var22) {
                  IsoWorldInventoryObject var24 = (IsoWorldInventoryObject)var7.getWorldObjects().get(var22);
                  if (var24 != null && var24.getItem() instanceof InventoryContainer && var24.getItem().id == var17) {
                     var19 = ((InventoryContainer)var24.getItem()).getInventory();
                     break;
                  }
               }

               if (var19 == null) {
                  DebugLog.log("ERROR removeItemFromContainer can't find world item with id=" + var17);
                  return;
               }

               for(var22 = 0; var22 < var18; ++var22) {
                  var25 = var2.getInt();
                  var19.removeItemWithID(var25);
                  var19.setExplored(true);
               }
            } else {
               byte var9;
               int var10;
               int var14;
               if (var3 == 2) {
                  var16 = var2.getByte();
                  var9 = var2.getByte();
                  var10 = var1.getInt();
                  if (var16 < 0 || var16 >= var7.getObjects().size()) {
                     DebugLog.log("ERROR: removeItemFromContainer: invalid object index");
                     return;
                  }

                  IsoObject var20 = (IsoObject)var7.getObjects().get(var16);
                  ItemContainer var23 = var20 != null ? var20.getContainerByIndex(var9) : null;
                  if (var23 != null) {
                     for(int var26 = 0; var26 < var10; ++var26) {
                        var14 = var2.getInt();
                        var23.removeItemWithID(var14);
                        var23.setExplored(true);
                     }
                  }
               } else if (var3 == 3) {
                  short var8 = var2.getShort();
                  var9 = var2.getByte();
                  var10 = var1.getInt();
                  BaseVehicle var11 = VehicleManager.instance.getVehicleByID(var8);
                  if (var11 == null) {
                     DebugLog.log("ERROR: removeItemFromContainer: invalid vehicle id");
                     return;
                  }

                  VehiclePart var12 = var11.getPartByIndex(var9);
                  if (var12 == null) {
                     DebugLog.log("ERROR: removeItemFromContainer: invalid part index");
                     return;
                  }

                  ItemContainer var13 = var12.getItemContainer();
                  if (var13 == null) {
                     DebugLog.log("ERROR: removeItemFromContainer: part " + var12.getId() + " has no container");
                     return;
                  }

                  if (var13 != null) {
                     for(var14 = 0; var14 < var10; ++var14) {
                        int var15 = var2.getInt();
                        var13.removeItemWithID(var15);
                        var13.setExplored(true);
                     }

                     var12.setContainerContentAmount(var13.getCapacityWeight());
                  }
               } else {
                  DebugLog.log("ERROR: removeItemFromContainer: invalid object index");
               }
            }
         } else {
            this.delayPacket(var4, var5, var6);
         }

      }
   }

   private void sendItemsToContainer(ByteBuffer var1) {
      if (IsoWorld.instance.CurrentCell != null) {
         ByteBufferReader var2 = new ByteBufferReader(var1);
         short var3 = var1.getShort();
         int var4 = var2.getInt();
         int var5 = var2.getInt();
         int var6 = var2.getInt();
         IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var4, var5, var6);
         if (var7 == null) {
            this.delayPacket(var4, var5, var6);
         } else {
            ItemContainer var8 = null;
            VehiclePart var9 = null;
            byte var10;
            int var16;
            if (var3 == 0) {
               var10 = var2.getByte();
               if (var10 < 0 || var10 >= var7.getStaticMovingObjects().size()) {
                  DebugLog.log("ERROR: sendItemsToContainer: invalid corpse index");
                  return;
               }

               IsoObject var11 = (IsoObject)var7.getStaticMovingObjects().get(var10);
               if (var11 != null && var11.getContainer() != null) {
                  var8 = var11.getContainer();
               }
            } else if (var3 == 1) {
               int var14 = var2.getInt();

               for(var16 = 0; var16 < var7.getWorldObjects().size(); ++var16) {
                  IsoWorldInventoryObject var12 = (IsoWorldInventoryObject)var7.getWorldObjects().get(var16);
                  if (var12 != null && var12.getItem() instanceof InventoryContainer && var12.getItem().id == var14) {
                     var8 = ((InventoryContainer)var12.getItem()).getInventory();
                     break;
                  }
               }

               if (var8 == null) {
                  DebugLog.log("ERROR: sendItemsToContainer: can't find world item with id=" + var14);
                  return;
               }
            } else {
               byte var18;
               if (var3 == 2) {
                  var10 = var2.getByte();
                  var18 = var2.getByte();
                  if (var10 < 0 || var10 >= var7.getObjects().size()) {
                     DebugLog.log("ERROR: sendItemsToContainer: invalid object index");
                     return;
                  }

                  IsoObject var19 = (IsoObject)var7.getObjects().get(var10);
                  var8 = var19 != null ? var19.getContainerByIndex(var18) : null;
               } else if (var3 == 3) {
                  short var15 = var2.getShort();
                  var18 = var2.getByte();
                  BaseVehicle var20 = VehicleManager.instance.getVehicleByID(var15);
                  if (var20 == null) {
                     DebugLog.log("ERROR: sendItemsToContainer: invalid vehicle id");
                     return;
                  }

                  var9 = var20.getPartByIndex(var18);
                  if (var9 == null) {
                     DebugLog.log("ERROR: sendItemsToContainer: invalid part index");
                     return;
                  }

                  var8 = var9.getItemContainer();
                  if (var8 == null) {
                     DebugLog.log("ERROR: sendItemsToContainer: part " + var9.getId() + " has no container");
                     return;
                  }
               } else {
                  DebugLog.log("ERROR: sendItemsToContainer: unknown container type");
               }
            }

            if (var8 != null) {
               try {
                  ArrayList var17 = CompressIdenticalItems.load(var2.bb, 184, (ArrayList)null, (ArrayList)null);

                  for(var16 = 0; var16 < var17.size(); ++var16) {
                     InventoryItem var21 = (InventoryItem)var17.get(var16);
                     if (var21 != null) {
                        if (var8.containsID(var21.id)) {
                           if (var3 != 0) {
                              System.out.println("Error: Dupe item ID. id = " + var21.id);
                           }
                        } else {
                           var8.addItem(var21);
                           var8.setExplored(true);
                           if (var8.getParent() instanceof IsoMannequin) {
                              ((IsoMannequin)var8.getParent()).wearItem(var21, (IsoGameCharacter)null);
                           }
                        }
                     }
                  }
               } catch (Exception var13) {
                  var13.printStackTrace();
               }

               if (var9 != null) {
                  var9.setContainerContentAmount(var8.getCapacityWeight());
               }
            }
         }

      }
   }

   private void readItemStats(ByteBuffer var1, InventoryItem var2) {
      int var3 = var1.getInt();
      float var4 = var1.getFloat();
      boolean var5 = var1.get() == 1;
      var2.setUses(var3);
      if (var2 instanceof DrainableComboItem) {
         ((DrainableComboItem)var2).setDelta(var4);
         ((DrainableComboItem)var2).updateWeight();
      }

      if (var5 && var2 instanceof Food) {
         Food var6 = (Food)var2;
         var6.setHungChange(var1.getFloat());
         var6.setCalories(var1.getFloat());
         var6.setCarbohydrates(var1.getFloat());
         var6.setLipids(var1.getFloat());
         var6.setProteins(var1.getFloat());
         var6.setThirstChange(var1.getFloat());
         var6.setFluReduction(var1.getInt());
         var6.setPainReduction(var1.getFloat());
         var6.setEndChange(var1.getFloat());
         var6.setReduceFoodSickness(var1.getInt());
         var6.setStressChange(var1.getFloat());
         var6.setFatigueChange(var1.getFloat());
      }

   }

   private void receiveItemStats(ByteBuffer var1) {
      short var2 = var1.getShort();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, var5);
      byte var8;
      int var9;
      byte var14;
      int var16;
      ItemContainer var20;
      InventoryItem var22;
      switch(var2) {
      case 0:
         var14 = var1.get();
         var16 = var1.getInt();
         if (var6 != null && var14 >= 0 && var14 < var6.getStaticMovingObjects().size()) {
            IsoMovingObject var18 = (IsoMovingObject)var6.getStaticMovingObjects().get(var14);
            var20 = var18.getContainer();
            if (var20 != null) {
               var22 = var20.getItemWithID(var16);
               if (var22 != null) {
                  this.readItemStats(var1, var22);
               }
            }
         }
         break;
      case 1:
         int var15 = var1.getInt();
         if (var6 != null) {
            for(var16 = 0; var16 < var6.getWorldObjects().size(); ++var16) {
               IsoWorldInventoryObject var17 = (IsoWorldInventoryObject)var6.getWorldObjects().get(var16);
               if (var17.getItem() != null && var17.getItem().id == var15) {
                  this.readItemStats(var1, var17.getItem());
                  break;
               }

               if (var17.getItem() instanceof InventoryContainer) {
                  var20 = ((InventoryContainer)var17.getItem()).getInventory();
                  var22 = var20.getItemWithID(var15);
                  if (var22 != null) {
                     this.readItemStats(var1, var22);
                     break;
                  }
               }
            }
         }
         break;
      case 2:
         var14 = var1.get();
         var8 = var1.get();
         var9 = var1.getInt();
         if (var6 != null && var14 >= 0 && var14 < var6.getObjects().size()) {
            IsoObject var19 = (IsoObject)var6.getObjects().get(var14);
            ItemContainer var21 = var19.getContainerByIndex(var8);
            if (var21 != null) {
               InventoryItem var23 = var21.getItemWithID(var9);
               if (var23 != null) {
                  this.readItemStats(var1, var23);
               }
            }
         }
         break;
      case 3:
         short var7 = var1.getShort();
         var8 = var1.get();
         var9 = var1.getInt();
         BaseVehicle var10 = VehicleManager.instance.getVehicleByID(var7);
         if (var10 != null) {
            VehiclePart var11 = var10.getPartByIndex(var8);
            if (var11 != null) {
               ItemContainer var12 = var11.getItemContainer();
               if (var12 != null) {
                  InventoryItem var13 = var12.getItemWithID(var9);
                  if (var13 != null) {
                     this.readItemStats(var1, var13);
                  }
               }
            }
         }
      }

   }

   public static boolean canSeePlayerStats() {
      return !accessLevel.equals("");
   }

   public static boolean canModifyPlayerStats() {
      return accessLevel.equals("admin") || accessLevel.equals("moderator") || accessLevel.equals("overseer");
   }

   public void sendPersonalColor(IsoPlayer var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)138, var2);
      var2.putInt(var1.getOnlineID());
      var2.putFloat(Core.getInstance().getMpTextColor().r);
      var2.putFloat(Core.getInstance().getMpTextColor().g);
      var2.putFloat(Core.getInstance().getMpTextColor().b);
      connection.endPacketImmediate();
   }

   public void sendChangedPlayerStats(IsoPlayer var1) {
      ByteBufferWriter var2 = connection.startPacket();
      var1.createPlayerStats(var2, username);
      connection.endPacketImmediate();
   }

   private void receivePlayerStatsChanges(ByteBuffer var1) {
      int var2 = var1.getInt();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var3 != null) {
         String var4 = GameWindow.ReadString(var1);
         var3.setPlayerStats(var1, var4);
         allChatMuted = var3.isAllChatMuted();
      }
   }

   public void writePlayerConnectData(ByteBufferWriter var1, IsoPlayer var2) {
      var1.putByte((byte)var2.PlayerIndex);
      var1.putByte((byte)IsoChunkMap.ChunkGridWidth);
      var1.putFloat(var2.x);
      var1.putFloat(var2.y);
      var1.putFloat(var2.z);

      try {
         var2.getDescriptor().save(var1.bb);
         var2.getHumanVisual().save(var1.bb);
         ItemVisuals var3 = new ItemVisuals();
         var2.getItemVisuals(var3);
         var3.save(var1.bb);
         var2.getXp().save(var1.bb);
      } catch (IOException var8) {
         var8.printStackTrace();
      }

      var1.putBoolean(var2.isAllChatMuted());
      var1.putUTF(var2.getTagPrefix());
      var1.putFloat(var2.getTagColor().r);
      var1.putFloat(var2.getTagColor().g);
      var1.putFloat(var2.getTagColor().b);
      var1.putInt(var2.getTransactionID());
      var1.putDouble(var2.getHoursSurvived());
      var1.putInt(var2.getZombieKills());
      var1.putUTF(var2.getDisplayName());
      var1.putFloat(var2.getSpeakColour().r);
      var1.putFloat(var2.getSpeakColour().g);
      var1.putFloat(var2.getSpeakColour().b);
      var1.putBoolean(var2.showTag);
      var1.putBoolean(var2.factionPvp);
      if (SteamUtils.isSteamModeEnabled()) {
         var1.putUTF(SteamFriends.GetFriendPersonaName(SteamUser.GetSteamID()));
      }

      InventoryItem var9 = var2.getPrimaryHandItem();
      if (var9 == null) {
         var1.putByte((byte)0);
      } else {
         var1.putByte((byte)1);

         try {
            var9.saveWithSize(var1.bb, false);
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }

      InventoryItem var4 = var2.getSecondaryHandItem();
      if (var4 == null) {
         var1.putByte((byte)0);
      } else if (var4 == var9) {
         var1.putByte((byte)2);
      } else {
         var1.putByte((byte)1);

         try {
            var4.saveWithSize(var1.bb, false);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

      var1.putInt(var2.getAttachedItems().size());

      for(int var5 = 0; var5 < var2.getAttachedItems().size(); ++var5) {
         var1.putUTF(var2.getAttachedItems().get(var5).getLocation());
         var1.putUTF(var2.getAttachedItems().get(var5).getItem().getFullType());
      }

      var1.putInt(var2.getPerkLevel(PerkFactory.Perks.Sneak));
      connection.username = var2.username;
   }

   public void sendPlayerConnect(IsoPlayer var1) {
      var1.OnlineID = -1;
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)6, var2);
      this.writePlayerConnectData(var2, var1);
      connection.endPacketImmediate();
      allChatMuted = var1.isAllChatMuted();
      sendPerks(var1);
      var1.updateEquippedRadioFreq();
      this.bPlayerConnectSent = true;
   }

   public void sendPlayerSave(IsoPlayer var1) {
      if (connection != null) {
         ByteBufferWriter var2 = connection.startPacket();
         PacketTypes.doPacket((short)162, var2);
         var2.putByte((byte)var1.PlayerIndex);
         var2.putInt(var1.OnlineID);
         var2.putFloat(var1.x);
         var2.putFloat(var1.y);
         var2.putFloat(var1.z);
         connection.endPacketImmediate();
      }
   }

   public void sendPlayer2(IsoPlayer var1) {
      if (bClient && var1.isLocalPlayer() && var1.networkAI.isNeedToUpdate()) {
         if (PlayerPacket.l_send.playerPacket.set(var1)) {
            ByteBufferWriter var2 = connection.startPacket();
            PacketTypes.doPacket((short)218, var2);
            PlayerPacket.l_send.playerPacket.write(var2);
            connection.endPacketSuperHighUnreliable();
         }

      }
   }

   public void sendPlayer(IsoPlayer var1) {
      var1.networkAI.needToUpdate();
   }

   public void sendSteamProfileName(long var1) {
      if (SteamUtils.isSteamModeEnabled()) {
         for(int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
            IsoPlayer var4 = IsoPlayer.players[var3];
            if (var4 != null && var4.getSteamID() == var1) {
               ByteBufferWriter var5 = connection.startPacket();
               PacketTypes.doPacket((short)14, var5);
               var5.putShort((short)0);
               var5.putByte((byte)var4.getPlayerNum());
               var5.putUTF(SteamFriends.GetFriendPersonaName(var1));
               connection.endPacketUnordered();
               return;
            }
         }

      }
   }

   public void heartBeat() {
      ++count;
   }

   public static IsoZombie getZombie(short var0) {
      return (IsoZombie)IDToZombieMap.get(var0);
   }

   private void onZombieDie(ByteBuffer var1) {
      int var2 = var1.getInt();
      short var3 = var1.getShort();
      IsoZombie var4 = (IsoZombie)IDToZombieMap.get((short)var2);
      IsoPlayer var5 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var3));
      if (var4 != null) {
         var4.setHealth(-2.0F);
         var4.DoDeath((HandWeapon)var5.getPrimaryHandItem(), var5);
      }

   }

   public void receiveZombieUpdateInfoPacket(ByteBuffer var1) {
      ZombieUpdatePacker.instance.receivePacket(var1);
   }

   public static void sendPlayerExtraInfo(IsoPlayer var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)84, var1);
      var1.putShort((short)var0.OnlineID);
      var1.putUTF(var0.accessLevel);
      var1.putByte((byte)(var0.isGodMod() ? 1 : 0));
      var1.putByte((byte)(var0.isGhostMode() ? 1 : 0));
      var1.putByte((byte)(var0.isInvisible() ? 1 : 0));
      var1.putByte((byte)(var0.isNoClip() ? 1 : 0));
      var1.putByte((byte)(var0.isShowAdminTag() ? 1 : 0));
      connection.endPacketImmediate();
   }

   private static void receivePlayerExtraInfo(ByteBuffer var0) {
      short var1 = var0.getShort();
      String var2 = GameWindow.ReadString(var0);
      boolean var3 = var0.get() == 1;
      boolean var4 = var0.get() == 1;
      boolean var5 = var0.get() == 1;
      boolean var6 = var0.get() == 1;
      boolean var7 = var0.get() == 1;
      IsoPlayer var8 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var1));
      if (var8 != null) {
         var8.accessLevel = var2;
         var8.setGodMod(var3);
         var8.setInvisible(var5);
         var8.setGhostMode(var4);
         var8.setNoClip(var6);
         var8.setShowAdminTag(var7);
         if (!var8.bRemote) {
            accessLevel = var2;

            for(int var9 = 0; var9 < IsoPlayer.numPlayers; ++var9) {
               IsoPlayer var10 = IsoPlayer.players[var9];
               if (var10 != null && !var10.accessLevel.equals("")) {
                  accessLevel = var10.accessLevel;
                  break;
               }
            }
         }
      }

   }

   public void receiveConnectionDetails(ByteBuffer var1) {
      Calendar var2 = Calendar.getInstance();
      PrintStream var10000 = System.out;
      long var10001 = var2.getTimeInMillis();
      var10000.println("LOGGED INTO : " + (var10001 - startAuth.getTimeInMillis()) + " millisecond");
      ConnectToServerState var3 = new ConnectToServerState(var1);
      var3.enter();
      MainScreenState.getInstance().setConnectToServerState(var3);
   }

   public void setResetID(int var1) {
      this.loadResetID();
      if (this.ResetID != var1) {
         boolean var2 = true;
         ArrayList var3 = IsoPlayer.getAllFileNames();
         var3.add("map_p.bin");
         String var10002;
         int var4;
         File var5;
         File var6;
         if (var2) {
            for(var4 = 0; var4 < var3.size(); ++var4) {
               try {
                  var5 = ZomboidFileSystem.instance.getFileInCurrentSave((String)var3.get(var4));
                  if (var5.exists()) {
                     var10002 = ZomboidFileSystem.instance.getCacheDir();
                     var6 = new File(var10002 + File.separator + (String)var3.get(var4));
                     if (var6.exists()) {
                        var6.delete();
                     }

                     var5.renameTo(var6);
                  }
               } catch (Exception var8) {
                  var8.printStackTrace();
               }
            }
         }

         DebugLog.log("server was reset, deleting " + Core.GameSaveWorld);
         LuaManager.GlobalObject.deleteSave(Core.GameSaveWorld);
         LuaManager.GlobalObject.createWorld(Core.GameSaveWorld);
         if (var2) {
            for(var4 = 0; var4 < var3.size(); ++var4) {
               try {
                  var5 = ZomboidFileSystem.instance.getFileInCurrentSave((String)var3.get(var4));
                  var10002 = ZomboidFileSystem.instance.getCacheDir();
                  var6 = new File(var10002 + File.separator + (String)var3.get(var4));
                  if (var6 != null) {
                     var6.renameTo(var5);
                  }
               } catch (Exception var7) {
                  var7.printStackTrace();
               }
            }
         }
      }

      this.ResetID = var1;
      this.saveResetID();
   }

   public void loadResetID() {
      File var1 = ZomboidFileSystem.instance.getFileInCurrentSave("serverid.dat");
      if (var1.exists()) {
         FileInputStream var2 = null;

         try {
            var2 = new FileInputStream(var1);
         } catch (FileNotFoundException var7) {
            var7.printStackTrace();
         }

         DataInputStream var3 = new DataInputStream(var2);

         try {
            this.ResetID = var3.readInt();
         } catch (IOException var6) {
            var6.printStackTrace();
         }

         try {
            var2.close();
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      }

   }

   private void saveResetID() {
      File var1 = ZomboidFileSystem.instance.getFileInCurrentSave("serverid.dat");
      FileOutputStream var2 = null;

      try {
         var2 = new FileOutputStream(var1);
      } catch (FileNotFoundException var7) {
         var7.printStackTrace();
      }

      DataOutputStream var3 = new DataOutputStream(var2);

      try {
         var3.writeInt(this.ResetID);
      } catch (IOException var6) {
         var6.printStackTrace();
      }

      try {
         var2.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public void receivePlayerConnect(ByteBuffer var1) {
      boolean var2 = false;
      short var3 = var1.getShort();
      byte var4 = -1;
      if (var3 == -1) {
         var2 = true;
         var4 = var1.get();
         var3 = var1.getShort();

         try {
            GameTime.getInstance().load(var1);
            GameTime.getInstance().ServerTimeOfDay = GameTime.getInstance().getTimeOfDay();
            GameTime.getInstance().ServerNewDays = 0;
            GameTime.getInstance().setMinutesPerDay((float)SandboxOptions.instance.getDayLengthMinutes());
            LuaEventManager.triggerEvent("OnGameTimeLoaded");
         } catch (IOException var17) {
            var17.printStackTrace();
         }
      } else if (IDToPlayerMap.containsKey(Integer.valueOf(var3))) {
         return;
      }

      float var5 = var1.getFloat();
      float var6 = var1.getFloat();
      float var7 = var1.getFloat();
      IsoPlayer var8 = null;
      String var9;
      int var10;
      if (var2) {
         var9 = GameWindow.ReadString(var1);

         for(var10 = 0; var10 < IsoWorld.instance.AddCoopPlayers.size(); ++var10) {
            ((AddCoopPlayer)IsoWorld.instance.AddCoopPlayers.get(var10)).receivePlayerConnect(var4);
         }

         var8 = IsoPlayer.players[var4];
         var8.username = var9;
         var8.OnlineID = var3;
         connection.ReleventPos[var4].x = var5;
         connection.ReleventPos[var4].y = var6;
         connection.ReleventPos[var4].z = var7;
      } else {
         var9 = GameWindow.ReadString(var1);
         SurvivorDesc var19 = SurvivorFactory.CreateSurvivor();

         try {
            var19.load(var1, 184, (IsoGameCharacter)null);
         } catch (IOException var16) {
            var16.printStackTrace();
         }

         try {
            var8 = new IsoPlayer(IsoWorld.instance.CurrentCell, var19, (int)var5, (int)var6, (int)var7);
            var8.bRemote = true;
            var8.getHumanVisual().load(var1, 184);
            var8.getItemVisuals().load(var1, 184);
            var8.username = var9;
            var8.updateUsername();
            var8.setSceneCulled(false);
         } catch (Exception var15) {
            var15.printStackTrace();
         }

         var8.setX(var5);
         var8.setY(var6);
         var8.setZ(var7);
      }

      var8.OnlineID = var3;
      if (SteamUtils.isSteamModeEnabled()) {
         var8.setSteamID(var1.getLong());
      }

      var8.setGodMod(var1.get() == 1);
      var8.setGhostMode(var1.get() == 1);
      var8.setSafety(var1.get() == 1);
      var8.accessLevel = GameWindow.ReadString(var1);
      var8.setInvisible(var1.get() == 1);
      if (!var2 && canSeePlayerStats()) {
         try {
            var8.getXp().load(var1, 184);
         } catch (IOException var14) {
            var14.printStackTrace();
         }
      }

      var8.setTagPrefix(GameWindow.ReadString(var1));
      var8.setTagColor(new ColorInfo(var1.getFloat(), var1.getFloat(), var1.getFloat(), 1.0F));
      var8.setHoursSurvived(var1.getDouble());
      var8.setZombieKills(var1.getInt());
      var8.setDisplayName(GameWindow.ReadString(var1));
      var8.setSpeakColour(new Color(var1.getFloat(), var1.getFloat(), var1.getFloat(), 1.0F));
      var8.showTag = var1.get() == 1;
      var8.factionPvp = var1.get() == 1;
      int var18 = var1.getInt();

      for(var10 = 0; var10 < var18; ++var10) {
         String var11 = GameWindow.ReadString(var1);
         InventoryItem var12 = InventoryItemFactory.CreateItem(GameWindow.ReadString(var1));
         if (var12 != null) {
            var8.setAttachedItem(var11, var12);
         }
      }

      var10 = var1.getInt();
      int var20 = var1.getInt();
      int var21 = var1.getInt();
      var8.remoteSneakLvl = var10;
      var8.remoteStrLvl = var20;
      var8.remoteFitLvl = var21;
      if (Core.bDebug) {
         DebugLog.log(DebugType.Network, "Player Connect received for player " + username + " id " + var3 + (var2 ? " (local)" : " (remote)"));
      }

      if (DebugOptions.instance.MultiplayerShowPosition.getValue() && !var2) {
         if (positions.containsKey(var8.getOnlineID())) {
            ((Vector2)positions.get(var8.getOnlineID())).set(var5, var6);
         } else {
            positions.put(var8.getOnlineID(), new Vector2(var5, var6));
         }
      }

      IDToPlayerMap.put(Integer.valueOf(var3), var8);
      this.idMapDirty = true;
      LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
      if (var2) {
         getCustomModData();
      }

      if (!var2 && ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue()) {
         SafeHouse var13 = SafeHouse.hasSafehouse(var8);
         if (var13 != null) {
            var13.setPlayerConnected(var13.getPlayerConnected() + 1);
         }
      }

      String var22 = ServerOptions.getInstance().getOption("ServerWelcomeMessage");
      if (var2 && var22 != null && !var22.equals("")) {
         ChatManager.getInstance().showServerChatMessage(var22);
      }

   }

   public boolean receivePlayerConnectWhileLoading(ByteBuffer var1) {
      boolean var2 = false;
      short var3 = var1.getShort();
      byte var4 = -1;
      if (var3 != -1) {
         return false;
      } else {
         if (var3 == -1) {
            var2 = true;
            var4 = var1.get();
            var3 = var1.getShort();

            try {
               GameTime.getInstance().load(var1);
               LuaEventManager.triggerEvent("OnGameTimeLoaded");
            } catch (IOException var14) {
               var14.printStackTrace();
            }
         }

         float var5 = var1.getFloat();
         float var6 = var1.getFloat();
         float var7 = var1.getFloat();
         IsoPlayer var8 = null;
         String var9;
         if (var2) {
            var9 = GameWindow.ReadString(var1);
            var8 = IsoPlayer.players[var4];
            var8.username = var9;
            var8.OnlineID = var3;
         } else {
            var9 = GameWindow.ReadString(var1);
            SurvivorDesc var10 = SurvivorFactory.CreateSurvivor();

            try {
               var10.load(var1, 184, (IsoGameCharacter)null);
            } catch (IOException var13) {
               var13.printStackTrace();
            }

            try {
               var8 = new IsoPlayer(IsoWorld.instance.CurrentCell, var10, (int)var5, (int)var6, (int)var7);
               var8.getHumanVisual().load(var1, 184);
               var8.getItemVisuals().load(var1, 184);
               var8.username = var9;
               var8.updateUsername();
               var8.setSceneCulled(false);
            } catch (Exception var12) {
               var12.printStackTrace();
            }

            var8.bRemote = true;
            var8.setX(var5);
            var8.setY(var6);
            var8.setZ(var7);
         }

         var8.OnlineID = var3;
         if (Core.bDebug) {
            DebugLog.log(DebugType.Network, "Player Connect received for player " + username + " id " + var3 + (var2 ? " (me)" : " (not me)"));
         }

         int var15 = var1.getInt();

         for(int var16 = 0; var16 < var15; ++var16) {
            ServerOptions.instance.putOption(GameWindow.ReadString(var1), GameWindow.ReadString(var1));
         }

         var8.setGodMod(var1.get() == 1);
         var8.setGhostMode(var1.get() == 1);
         var8.setSafety(var1.get() == 1);
         var8.accessLevel = GameWindow.ReadString(var1);
         var8.setInvisible(var1.get() == 1);
         IDToPlayerMap.put(Integer.valueOf(var3), var8);
         this.idMapDirty = true;
         getCustomModData();
         String var17 = ServerOptions.getInstance().getOption("ServerWelcomeMessage");
         if (var2 && var17 != null && !var17.equals("")) {
            ChatManager.getInstance().showServerChatMessage(var17);
         }

         return true;
      }
   }

   public ArrayList getPlayers() {
      if (!this.idMapDirty) {
         return this.players;
      } else {
         this.players.clear();
         this.players.addAll(IDToPlayerMap.values());
         this.idMapDirty = false;
         return this.players;
      }
   }

   private IsoObject getIsoObjectRefFromByteBuffer(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      byte var5 = var1.get();
      IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var6 == null) {
         this.delayPacket(var2, var3, var4);
         return null;
      } else {
         return var5 >= 0 && var5 < var6.getObjects().size() ? (IsoObject)var6.getObjects().get(var5) : null;
      }
   }

   public void sendWeaponHit(IsoPlayer var1, HandWeapon var2, IsoObject var3) {
      if (var1 != null && var3 != null && var1.isLocalPlayer()) {
         ByteBufferWriter var4 = connection.startPacket();
         PacketTypes.doPacket((short)28, var4);
         var4.putInt(var3.square.x);
         var4.putInt(var3.square.y);
         var4.putInt(var3.square.z);
         var4.putByte((byte)var3.getObjectIndex());
         var4.putShort((short)var1.OnlineID);
         var4.putUTF(var2 != null ? var2.getFullType() : "");
         connection.endPacketImmediate();
      }
   }

   public static void SyncCustomLightSwitchSettings(ByteBuffer var0) {
      int var1 = var0.getInt();
      int var2 = var0.getInt();
      int var3 = var0.getInt();
      byte var4 = var0.get();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
      if (var5 != null && var4 >= 0 && var4 < var5.getObjects().size()) {
         if (var5.getObjects().get(var4) instanceof IsoLightSwitch) {
            ((IsoLightSwitch)var5.getObjects().get(var4)).receiveSyncCustomizedSettings(var0, (UdpConnection)null);
         } else {
            DebugLog.log("Sync Lightswitch custom settings: found object not a instance of IsoLightSwitch, x,y,z=" + var1 + "," + var2 + "," + var3);
         }
      } else if (var5 != null) {
         DebugLog.log("Sync Lightswitch custom settings: index=" + var4 + " is invalid x,y,z=" + var1 + "," + var2 + "," + var3);
      } else if (Core.bDebug) {
         DebugLog.log("Sync Lightswitch custom settings: sq is null x,y,z=" + var1 + "," + var2 + "," + var3);
      }

   }

   public void SyncIsoObjectReq(ByteBuffer var1) {
      if (SystemDisabler.doObjectStateSyncEnable) {
         short var2 = var1.getShort();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.SyncIsoObject(var1);
         }

      }
   }

   public void SyncWorldObjectsReq(ByteBuffer var1) {
      DebugLog.log("SyncWorldObjectsReq client : ");
      short var2 = var1.getShort();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getInt();
         int var5 = var1.getInt();
         instance.worldObjectsSyncReq.receiveSyncIsoChunk(var4, var5);
         short var6 = var1.getShort();
         DebugLog.log("[" + var4 + "," + var5 + "]:" + var6 + " ");
         IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var4 * 10, var5 * 10, 0);
         if (var7 == null) {
            return;
         }

         IsoChunk var8 = var7.getChunk();
         ++var8.ObjectsSyncCount;
         var8.recalcHashCodeObjects();
      }

      DebugLog.log(";\n");
   }

   public void SyncObjectsReq(ByteBuffer var1) {
      if (SystemDisabler.doWorldSyncEnable) {
         short var2 = var1.getShort();
         if (var2 == 2) {
            instance.worldObjectsSyncReq.receiveGridSquareHashes(var1);
         }

         if (var2 == 4) {
            instance.worldObjectsSyncReq.receiveGridSquareObjectHashes(var1);
         }

         if (var2 == 6) {
            instance.worldObjectsSyncReq.receiveObject(var1);
         }

      }
   }

   public void SyncIsoObject(ByteBuffer var1) {
      if (DebugOptions.instance.Network.Client.SyncIsoObject.getValue()) {
         int var2 = var1.getInt();
         int var3 = var1.getInt();
         int var4 = var1.getInt();
         byte var5 = var1.get();
         byte var6 = var1.get();
         byte var7 = var1.get();
         if (var6 != 2) {
            this.objectSyncReq.receiveIsoSync(var2, var3, var4, var5);
         }

         if (var6 == 1) {
            IsoGridSquare var8 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
            if (var8 == null) {
               return;
            }

            if (var5 >= 0 && var5 < var8.getObjects().size()) {
               ((IsoObject)var8.getObjects().get(var5)).syncIsoObject(true, var7, (UdpConnection)null, var1);
            } else {
               DebugLog.Network.warn("SyncIsoObject: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var3 + "," + var4);
            }
         }

      }
   }

   public static void SyncAlarmClock(ByteBuffer var0) {
      short var1 = var0.getShort();
      int var2;
      int var7;
      if (var1 == AlarmClock.PacketPlayer) {
         var2 = var0.getInt();
         long var14 = var0.getLong();
         boolean var15 = var0.get() == 1;
         int var16 = var15 ? 0 : var0.getInt();
         var7 = var15 ? 0 : var0.getInt();
         byte var17 = var15 ? 0 : var0.get();
         IsoPlayer var18 = (IsoPlayer)IDToPlayerMap.get(var2);
         if (var18 != null) {
            for(int var19 = 0; var19 < var18.getInventory().getItems().size(); ++var19) {
               InventoryItem var20 = (InventoryItem)var18.getInventory().getItems().get(var19);
               if (var20 instanceof AlarmClock && (long)var20.getID() == var14) {
                  if (var15) {
                     ((AlarmClock)var20).stopRinging();
                  } else {
                     ((AlarmClock)var20).setAlarmSet(var17 == 1);
                     ((AlarmClock)var20).setHour(var16);
                     ((AlarmClock)var20).setMinute(var7);
                  }
                  break;
               }
            }

         }
      } else if (var1 == AlarmClock.PacketWorld) {
         var2 = var0.getInt();
         int var3 = var0.getInt();
         int var4 = var0.getInt();
         int var5 = var0.getInt();
         boolean var6 = var0.get() == 1;
         var7 = var6 ? 0 : var0.getInt();
         int var8 = var6 ? 0 : var0.getInt();
         byte var9 = var6 ? 0 : var0.get();
         IsoGridSquare var10 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
         if (var10 != null) {
            for(int var11 = 0; var11 < var10.getWorldObjects().size(); ++var11) {
               IsoWorldInventoryObject var12 = (IsoWorldInventoryObject)var10.getWorldObjects().get(var11);
               if (var12 != null && var12.getItem() instanceof AlarmClock && var12.getItem().id == var5) {
                  AlarmClock var13 = (AlarmClock)var12.getItem();
                  if (var6) {
                     var13.stopRinging();
                  } else {
                     var13.setAlarmSet(var9 == 1);
                     var13.setHour(var7);
                     var13.setMinute(var8);
                  }
                  break;
               }
            }

         }
      }
   }

   public void AddItemToMap(ByteBuffer var1) {
      if (IsoWorld.instance.CurrentCell != null) {
         IsoObject var2 = WorldItemTypes.createFromBuffer(var1);
         var2.loadFromRemoteBuffer(var1);
         if (var2.square != null) {
            if (var2 instanceof IsoLightSwitch) {
               ((IsoLightSwitch)var2).addLightSourceFromSprite();
            }

            var2.addToWorld();
            IsoWorld.instance.CurrentCell.checkHaveRoof(var2.square.getX(), var2.square.getY());
            if (!(var2 instanceof IsoWorldInventoryObject)) {
               for(int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
                  LosUtil.cachecleared[var3] = true;
               }

               IsoGridSquare.setRecalcLightTime(-1);
               GameTime.instance.lightSourceUpdate = 100.0F;
               MapCollisionData.instance.squareChanged(var2.square);
               PolygonalMap2.instance.squareChanged(var2.square);
               if (var2 == var2.square.getPlayerBuiltFloor()) {
                  IsoGridOcclusionData.SquareChanged();
               }
            }

            if (var2 instanceof IsoWorldInventoryObject || var2.getContainer() != null) {
               LuaEventManager.triggerEvent("OnContainerUpdate", var2);
            }
         }

      }
   }

   public void playerTimeout(ByteBuffer var1) {
      int var2 = var1.getInt();
      if (DebugOptions.instance.MultiplayerShowPosition.getValue()) {
         positions.remove(var2);
      }

      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var2);
      if (var3 != null) {
         DebugLog.log("Received timeout for player " + var3.username + " id " + var3.OnlineID);
         if (var3.getVehicle() != null) {
            int var4 = var3.getVehicle().getSeat(var3);
            if (var4 != -1) {
               var3.getVehicle().clearPassenger(var4);
            }

            VehicleManager.instance.sendReqestGetPosition(var3.getVehicle().VehicleID);
         }

         var3.removeFromWorld();
         var3.removeFromSquare();
         IDToPlayerMap.remove(var3.OnlineID);
         this.idMapDirty = true;
         LuaEventManager.triggerEvent("OnMiniScoreboardUpdate");
      }
   }

   public void disconnect() {
      this.bConnected = false;
      if (IsoPlayer.getInstance() != null) {
         IsoPlayer.getInstance().OnlineID = -1;
      }

   }

   public void addIncoming(short var1, ByteBuffer var2) {
      if (connection != null) {
         if (var1 == 18) {
            WorldStreamer.instance.receiveChunkPart(var2);
         } else if (var1 == 36) {
            WorldStreamer.instance.receiveNotRequired(var2);
         } else if (var1 == 167) {
            ClientPlayerDB.getInstance().clientLoadNetworkCharacter(var2, connection);
         } else {
            ZomboidNetData var3 = null;
            if (var2.remaining() > 2048) {
               var3 = ZomboidNetDataPool.instance.getLong(var2.remaining());
            } else {
               var3 = ZomboidNetDataPool.instance.get();
            }

            var3.read(var1, var2, connection);
            var3.time = System.currentTimeMillis();
            synchronized(MainLoopNetData) {
               MainLoopNetData.add(var3);
            }
         }
      }
   }

   public void doDisconnect(String var1) {
      if (this.bConnected && connection != null && connection.connected) {
         connection.forceDisconnect();
         this.bConnected = false;
         connection = null;
         bClient = false;
      }

   }

   public void removeZombieFromCache(IsoZombie var1) {
      if (IDToZombieMap.containsKey(var1.OnlineID)) {
         IDToZombieMap.remove(var1.OnlineID);
      }

   }

   private void receiveEquip(ByteBuffer var1) {
      short var2 = var1.getShort();
      byte var3 = var1.get();
      byte var4 = var1.get();
      IsoPlayer var5 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var5 != IsoPlayer.getInstance()) {
         InventoryItem var6 = null;
         if (var4 == 1) {
            try {
               var6 = InventoryItem.loadItem(var1, 184);
            } catch (Exception var9) {
               var9.printStackTrace();
            }
         }

         if (var5 != null && var3 == 1 && var4 == 2) {
            var6 = var5.getPrimaryHandItem();
         }

         if (var5 != null) {
            if (var3 == 0) {
               var5.setPrimaryHandItem(var6);
            } else {
               var5.setSecondaryHandItem(var6);
            }

            try {
               if (var6 != null) {
                  var6.setContainer(var5.getInventory());
                  if (var4 == 1 && var1.get() == 1) {
                     var6.getVisual().load(var1, 184);
                  }
               }
            } catch (IOException var8) {
               var8.printStackTrace();
            }
         }

      }
   }

   private static void hitCharacter(ByteBuffer var0) {
      HitPacket var1 = new HitPacket();
      var1.parse(var0);
      if (var1.check()) {
         if (NetworkStrikeAI.canStrike(var1)) {
            NetworkStrikeAI.executeStrike(var1);
         } else {
            NetworkStrikeAI.addStrike(var1);
         }

      }
   }

   public void equip(IsoPlayer var1, int var2) {
      InventoryItem var3 = null;
      if (var2 == 0) {
         var3 = var1.getPrimaryHandItem();
      } else {
         var3 = var1.getSecondaryHandItem();
      }

      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)25, var4);
      var4.putByte((byte)var1.PlayerIndex);
      var4.putByte((byte)var2);
      if (var3 == null) {
         var4.putByte((byte)0);
      } else if (var2 == 1 && var1.getPrimaryHandItem() == var1.getSecondaryHandItem()) {
         var4.putByte((byte)2);
      } else {
         var4.putByte((byte)1);

         try {
            var3.saveWithSize(var4.bb, false);
            if (var3.getVisual() != null) {
               var4.bb.put((byte)1);
               var3.getVisual().save(var4.bb);
            } else {
               var4.bb.put((byte)0);
            }
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

      connection.endPacketImmediate();
   }

   public void sendWorldMessage(String var1) {
      ChatManager.getInstance().showInfoMessage(var1);
   }

   private void convertGameSaveWorldDirectory(String var1, String var2) {
      File var3 = new File(var1);
      if (var3.isDirectory()) {
         File var4 = new File(var2);
         boolean var5 = var3.renameTo(var4);
         if (var5) {
            DebugLog.log("CONVERT: The GameSaveWorld directory was renamed from " + var1 + " to " + var2);
         } else {
            DebugLog.log("ERROR: The GameSaveWorld directory cannot rename from " + var1 + " to " + var2);
         }

      }
   }

   public void doConnect(String var1, String var2, String var3, String var4, String var5, String var6) {
      username = var1.trim();
      password = var2.trim();
      ip = var3.trim();
      localIP = var4.trim();
      port = Integer.parseInt(var5.trim());
      serverPassword = var6.trim();
      instance.init();
      String var10000 = ip;
      Core.GameSaveWorld = var10000 + "_" + port + "_" + ServerWorldDatabase.encrypt(var1);
      String var10001 = ZomboidFileSystem.instance.getGameModeCacheDir() + File.separator + ip + "_" + port + "_" + var1;
      String var10002 = ZomboidFileSystem.instance.getGameModeCacheDir();
      this.convertGameSaveWorldDirectory(var10001, var10002 + File.separator + Core.GameSaveWorld);
      if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
         Core.GameSaveWorld = CoopMaster.instance.getPlayerSaveFolder(CoopMaster.instance.getServerName());
      }

   }

   public void doConnectCoop(String var1) {
      username = SteamFriends.GetPersonaName();
      password = "";
      ip = var1;
      localIP = "";
      port = 0;
      serverPassword = "";
      this.init();
      if (CoopMaster.instance != null && CoopMaster.instance.isRunning()) {
         Core.GameSaveWorld = CoopMaster.instance.getPlayerSaveFolder(CoopMaster.instance.getServerName());
      }

   }

   public void scoreboardUpdate() {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)50, var1);
      connection.endPacketImmediate();
   }

   public void sendWorldSound(Object var1, int var2, int var3, int var4, int var5, int var6, boolean var7, float var8, float var9) {
      ByteBufferWriter var10 = connection.startPacket();
      PacketTypes.doPacket((short)54, var10);
      var10.putInt(var2);
      var10.putInt(var3);
      var10.putInt(var4);
      var10.putInt(var5);
      var10.putInt(var6);
      var10.putByte((byte)(var7 ? 1 : 0));
      var10.putFloat(var8);
      var10.putFloat(var9);
      var10.putByte((byte)(var1 instanceof IsoZombie ? 1 : 0));
      connection.endPacketImmediate();
   }

   private void receiveSound(ByteBuffer var1) {
      String var2 = GameWindow.ReadString(var1);
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      boolean var6 = var1.get() == 1;
      DebugLog.log(DebugType.Sound, "sound: received " + var2 + " at " + var3 + "," + var4 + "," + var5);
      BaseSoundEmitter var7 = IsoWorld.instance.getFreeEmitter((float)var3 + 0.5F, (float)var4 + 0.5F, (float)var5 + 0.5F);
      if (!var6) {
         var7.playSoundImpl(var2, (IsoObject)null);
      } else {
         var7.playSoundLoopedImpl(var2);
      }

   }

   private void receiveZombieDescriptors(ByteBuffer var1) throws IOException {
      DebugLog.log(DebugType.NetworkFileDebug, "received zombie descriptors");
      PersistentOutfits.instance.load(var1);
   }

   private void receivePlayerZombieDescriptors(ByteBuffer var1) throws IOException {
      short var2 = var1.getShort();
      DebugLog.log(DebugType.NetworkFileDebug, "received " + var2 + " player-zombie descriptors");

      for(short var3 = 0; var3 < var2; ++var3) {
         SharedDescriptors.Descriptor var4 = new SharedDescriptors.Descriptor();
         var4.load(var1, 184);
         SharedDescriptors.registerPlayerZombieDescriptor(var4);
      }

   }

   private void receiveAmbient(ByteBuffer var1) {
      String var2 = GameWindow.ReadString(var1);
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      float var6 = var1.getFloat();
      DebugLog.log(DebugType.Sound, "ambient: received " + var2 + " at " + var3 + "," + var4 + " radius=" + var5);
      AmbientStreamManager.instance.addAmbient(var2, var3, var4, var5, var6);
   }

   public void sendClientCommand(IsoPlayer var1, String var2, String var3, KahluaTable var4) {
      ByteBufferWriter var5 = connection.startPacket();
      PacketTypes.doPacket((short)57, var5);
      var5.putByte((byte)(var1 != null ? var1.PlayerIndex : -1));
      var5.putUTF(var2);
      var5.putUTF(var3);
      if (var4 != null && !var4.isEmpty()) {
         var5.putByte((byte)1);

         try {
            KahluaTableIterator var6 = var4.iterator();

            while(var6.advance()) {
               if (!TableNetworkUtils.canSave(var6.getKey(), var6.getValue())) {
                  Object var10000 = var6.getKey();
                  DebugLog.log("ERROR: sendClientCommand: can't save key,value=" + var10000 + "," + var6.getValue());
               }
            }

            TableNetworkUtils.save(var4, var5.bb);
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      } else {
         var5.putByte((byte)0);
      }

      connection.endPacketImmediate();
   }

   public void sendClientCommandV(IsoPlayer var1, String var2, String var3, Object... var4) {
      if (var4.length == 0) {
         this.sendClientCommand(var1, var2, var3, (KahluaTable)null);
      } else if (var4.length % 2 != 0) {
         DebugLog.log("ERROR: sendClientCommand called with wrong number of arguments (" + var2 + " " + var3 + ")");
      } else {
         KahluaTable var5 = LuaManager.platform.newTable();

         for(int var6 = 0; var6 < var4.length; var6 += 2) {
            Object var7 = var4[var6 + 1];
            if (var7 instanceof Float) {
               var5.rawset(var4[var6], ((Float)var7).doubleValue());
            } else if (var7 instanceof Integer) {
               var5.rawset(var4[var6], ((Integer)var7).doubleValue());
            } else if (var7 instanceof Short) {
               var5.rawset(var4[var6], ((Short)var7).doubleValue());
            } else {
               var5.rawset(var4[var6], var7);
            }
         }

         this.sendClientCommand(var1, var2, var3, var5);
      }
   }

   public void sendClothing(IsoPlayer var1, String var2, InventoryItem var3) {
      if (var1 != null && var1.OnlineID != -1) {
         ByteBufferWriter var4 = connection.startPacket();
         PacketTypes.doPacket((short)56, var4);

         try {
            var4.putByte((byte)var1.PlayerIndex);
            var4.putUTF(var2);
            if (var3 == null) {
               var4.putByte((byte)0);
            } else {
               var4.putByte((byte)1);

               try {
                  var3.saveWithSize(var4.bb, false);
               } catch (IOException var6) {
                  var6.printStackTrace();
               }
            }

            var1.getHumanVisual().save(var4.bb);
            ItemVisuals var5 = new ItemVisuals();
            var1.getItemVisuals(var5);
            var5.save(var4.bb);
            connection.endPacketImmediate();
         } catch (Throwable var7) {
            connection.cancelPacket();
            ExceptionLogger.logException(var7);
         }

      }
   }

   private void receiveClothing(ByteBuffer var1) {
      short var2 = var1.getShort();
      String var3 = GameWindow.ReadString(var1);
      byte var4 = var1.get();
      IsoPlayer var5 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var5 != null && var5 != IsoPlayer.getInstance()) {
         InventoryItem var6 = null;
         if (var4 == 1) {
            try {
               var6 = InventoryItem.loadItem(var1, 184);
            } catch (Exception var9) {
               var9.printStackTrace();
            }

            if (var6 == null) {
               return;
            }
         } else if (Core.bDebug) {
            DebugLog.log(DebugType.General, "player " + var5.username + " removing location " + var3);
         }

         try {
            var5.getHumanVisual().load(var1, 184);
            var5.getItemVisuals().load(var1, 184);
            var5.resetModelNextFrame();
         } catch (Throwable var8) {
            ExceptionLogger.logException(var8);
         }

      }
   }

   public void sendAttachedItem(IsoPlayer var1, String var2, InventoryItem var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)173, var4);

      try {
         var4.putByte((byte)var1.PlayerIndex);
         GameWindow.WriteString(var4.bb, var2);
         if (var3 != null) {
            var4.putByte((byte)1);
            GameWindow.WriteString(var4.bb, var3.getFullType());
         } else {
            var4.putByte((byte)0);
         }

         connection.endPacketImmediate();
      } catch (Throwable var6) {
         connection.cancelPacket();
         ExceptionLogger.logException(var6);
      }

   }

   private void receiveAttachedItem(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null && !var3.isLocalPlayer()) {
         String var4 = GameWindow.ReadString(var1);
         boolean var5 = var1.get() == 1;
         if (var5) {
            String var6 = GameWindow.ReadString(var1);
            InventoryItem var7 = InventoryItemFactory.CreateItem(var6);
            if (var7 == null) {
               return;
            }

            var3.setAttachedItem(var4, var7);
         } else {
            var3.setAttachedItem(var4, (InventoryItem)null);
         }

      }
   }

   public void sendVisual(IsoPlayer var1) {
      if (var1 != null && var1.OnlineID != -1) {
         ByteBufferWriter var2 = connection.startPacket();
         PacketTypes.doPacket((short)3, var2);

         try {
            var2.putByte((byte)var1.PlayerIndex);
            var1.getHumanVisual().save(var2.bb);
            connection.endPacketImmediate();
         } catch (Throwable var4) {
            connection.cancelPacket();
            ExceptionLogger.logException(var4);
         }

      }
   }

   private void receiveVisual(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var3 != null && !var3.isLocalPlayer()) {
         try {
            var3.getHumanVisual().load(var1, 184);
            var3.resetModelNextFrame();
         } catch (Throwable var5) {
            ExceptionLogger.logException(var5);
         }

      }
   }

   private void receiveBloodSplatter(ByteBuffer var1) {
      String var2 = GameWindow.ReadString(var1);
      float var3 = var1.getFloat();
      float var4 = var1.getFloat();
      float var5 = var1.getFloat();
      float var6 = var1.getFloat();
      float var7 = var1.getFloat();
      boolean var8 = var1.get() == 1;
      boolean var9 = var1.get() == 1;
      byte var10 = var1.get();
      IsoCell var11 = IsoWorld.instance.CurrentCell;
      IsoGridSquare var12 = var11.getGridSquare((double)var3, (double)var4, (double)var5);
      if (var12 == null) {
         this.delayPacket((int)var3, (int)var4, (int)var5);
      } else {
         int var13;
         if (var9 && SandboxOptions.instance.BloodLevel.getValue() > 1) {
            for(var13 = -1; var13 <= 1; ++var13) {
               for(int var18 = -1; var18 <= 1; ++var18) {
                  if (var13 != 0 || var18 != 0) {
                     new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, (float)var13 * Rand.Next(0.25F, 0.5F), (float)var18 * Rand.Next(0.25F, 0.5F));
                  }
               }
            }

            new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, var11, var3, var4, var5, var6 * 0.8F, var7 * 0.8F);
         } else {
            if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
               for(var13 = 0; var13 < var10; ++var13) {
                  var12.splatBlood(3, 0.3F);
               }

               var12.getChunk().addBloodSplat(var3, var4, (float)((int)var5), Rand.Next(20));
               new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, var6 * 1.5F, var7 * 1.5F);
            }

            byte var17 = 3;
            byte var14 = 0;
            byte var15 = 1;
            switch(SandboxOptions.instance.BloodLevel.getValue()) {
            case 1:
               var15 = 0;
               break;
            case 2:
               var15 = 1;
               var17 = 5;
               var14 = 2;
            case 3:
            default:
               break;
            case 4:
               var15 = 3;
               var17 = 2;
               break;
            case 5:
               var15 = 10;
               var17 = 0;
            }

            for(int var16 = 0; var16 < var15; ++var16) {
               if (Rand.Next(var8 ? 8 : var17) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, var6 * 1.5F, var7 * 1.5F);
               }

               if (Rand.Next(var8 ? 8 : var17) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, var6 * 1.5F, var7 * 1.5F);
               }

               if (Rand.Next(var8 ? 8 : var17) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, var6 * 1.8F, var7 * 1.8F);
               }

               if (Rand.Next(var8 ? 8 : var17) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, var6 * 1.9F, var7 * 1.9F);
               }

               if (Rand.Next(var8 ? 4 : var14) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, var6 * 3.5F, var7 * 3.5F);
               }

               if (Rand.Next(var8 ? 4 : var14) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, var6 * 3.8F, var7 * 3.8F);
               }

               if (Rand.Next(var8 ? 4 : var14) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, var6 * 3.9F, var7 * 3.9F);
               }

               if (Rand.Next(var8 ? 4 : var14) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, var6 * 1.5F, var7 * 1.5F);
               }

               if (Rand.Next(var8 ? 4 : var14) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, var6 * 3.8F, var7 * 3.8F);
               }

               if (Rand.Next(var8 ? 4 : var14) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, var11, var3, var4, var5, var6 * 3.9F, var7 * 3.9F);
               }

               if (Rand.Next(var8 ? 9 : 6) == 0) {
                  new IsoZombieGiblets(IsoZombieGiblets.GibletType.Eye, var11, var3, var4, var5, var6 * 0.8F, var7 * 0.8F);
               }
            }

         }
      }
   }

   private void receiveZombieSound(ByteBuffer var1) {
      short var2 = var1.getShort();
      byte var3 = var1.get();
      IsoZombie.ZombieSound var4 = IsoZombie.ZombieSound.fromIndex(var3);
      DebugLog.log(DebugType.Sound, "sound: received " + var3 + " for zombie " + var2);
      IsoZombie var5 = (IsoZombie)IDToZombieMap.get(var2);
      if (var5 != null && var5.getCurrentSquare() != null) {
         float var6 = (float)var4.radius();
         String var7;
         switch(var4) {
         case Burned:
            var7 = var5.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
            var5.getEmitter().playVocals(var7);
            break;
         case DeadCloseKilled:
            var5.getEmitter().playSoundImpl("HeadStab", (IsoObject)null);
            var7 = var5.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
            var5.getEmitter().playVocals(var7);
            var5.getEmitter().tick();
            break;
         case DeadNotCloseKilled:
            var5.getEmitter().playSoundImpl("HeadSmash", (IsoObject)null);
            var7 = var5.isFemale() ? "FemaleZombieDeath" : "MaleZombieDeath";
            var5.getEmitter().playVocals(var7);
            var5.getEmitter().tick();
            break;
         case Hurt:
            var5.playHurtSound();
            break;
         case Idle:
            var7 = var5.isFemale() ? "FemaleZombieIdle" : "MaleZombieIdle";
            var5.getEmitter().playVocals(var7);
            break;
         case Lunge:
            var7 = var5.isFemale() ? "FemaleZombieAttack" : "MaleZombieAttack";
            var5.getEmitter().playVocals(var7);
            break;
         default:
            DebugLog.log("unhandled zombie sound " + var4);
         }
      }

   }

   private static void receiveSlowFactor(ByteBuffer var0) {
      byte var1 = var0.get();
      float var2 = var0.getFloat();
      float var3 = var0.getFloat();
      IsoPlayer var4 = IsoPlayer.players[var1];
      if (var4 != null && !var4.isDead()) {
         var4.setSlowTimer(var2);
         var4.setSlowFactor(var3);
         DebugLog.log(DebugType.Combat, "slowTimer=" + var2 + " slowFactor=" + var3);
      }
   }

   public void sendCustomColor(IsoObject var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)121, var2);
      var2.putInt(var1.getSquare().getX());
      var2.putInt(var1.getSquare().getY());
      var2.putInt(var1.getSquare().getZ());
      var2.putInt(var1.getSquare().getObjects().indexOf(var1));
      var2.putFloat(var1.getCustomColor().r);
      var2.putFloat(var1.getCustomColor().g);
      var2.putFloat(var1.getCustomColor().b);
      var2.putFloat(var1.getCustomColor().a);
      connection.endPacketImmediate();
   }

   public void sendBandage(int var1, int var2, boolean var3, float var4, boolean var5, String var6) {
      ByteBufferWriter var7 = connection.startPacket();
      PacketTypes.doPacket((short)42, var7);
      var7.putShort((short)var1);
      var7.putInt(var2);
      var7.putBoolean(var3);
      var7.putFloat(var4);
      var7.putBoolean(var5);
      GameWindow.WriteStringUTF(var7.bb, var6);
      connection.endPacketImmediate();
   }

   public void sendStitch(int var1, int var2, boolean var3, float var4) {
      ByteBufferWriter var5 = connection.startPacket();
      PacketTypes.doPacket((short)98, var5);
      var5.putShort((short)var1);
      var5.putInt(var2);
      var5.putBoolean(var3);
      var5.putFloat(var4);
      connection.endPacketImmediate();
   }

   public void sendWoundInfection(int var1, int var2, boolean var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)97, var4);
      var4.putShort((short)var1);
      var4.putInt(var2);
      var4.putBoolean(var3);
      connection.endPacketImmediate();
   }

   public void sendDisinfect(int var1, int var2, float var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)99, var4);
      var4.putShort((short)var1);
      var4.putInt(var2);
      var4.putFloat(var3);
      connection.endPacketImmediate();
   }

   public void sendSplint(int var1, int var2, boolean var3, float var4, String var5) {
      ByteBufferWriter var6 = connection.startPacket();
      PacketTypes.doPacket((short)102, var6);
      var6.putShort((short)var1);
      var6.putInt(var2);
      var6.putBoolean(var3);
      if (var3) {
         if (var5 == null) {
            var5 = "";
         }

         var6.putUTF(var5);
         var6.putFloat(var4);
      }

      connection.endPacketImmediate();
   }

   public void sendAdditionalPain(int var1, int var2, float var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)100, var4);
      var4.putShort((short)var1);
      var4.putInt(var2);
      var4.putFloat(var3);
      connection.endPacketImmediate();
   }

   public void sendRemoveGlass(int var1, int var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)101, var3);
      var3.putShort((short)var1);
      var3.putInt(var2);
      connection.endPacketImmediate();
   }

   public void sendRemoveBullet(int var1, int var2, int var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)103, var4);
      var4.putShort((short)((byte)var1));
      var4.putInt(var2);
      var4.putInt(var3);
      connection.endPacketImmediate();
   }

   public void sendCleanBurn(int var1, int var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)104, var3);
      var3.putShort((short)((byte)var1));
      var3.putInt(var2);
      connection.endPacketImmediate();
   }

   public void eatFood(IsoPlayer var1, Food var2, float var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)43, var4);

      try {
         var4.putByte((byte)var1.PlayerIndex);
         var4.putFloat(var3);
         var2.saveWithSize(var4.bb, false);
      } catch (IOException var6) {
         var6.printStackTrace();
      }

      connection.endPacketImmediate();
   }

   public void drink(IsoPlayer var1, float var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)45, var3);
      var3.putByte((byte)var1.PlayerIndex);
      var3.putFloat(var2);
      connection.endPacketImmediate();
   }

   public void sendDeath(IsoPlayer var1) {
      if (var1 != null && var1.OnlineID != -1) {
         var1.setTransactionID(0);
         ByteBufferWriter var2 = connection.startPacket();
         PacketTypes.doPacket((short)33, var2);
         var2.putByte((byte)var1.PlayerIndex);
         var2.bb.putFloat(var1.getX());
         var2.bb.putFloat(var1.getY());
         var2.bb.putFloat(var1.getZ());
         var2.bb.putInt(var1.getDir().index());
         var2.putBoolean(var1.getBodyDamage().isInfected());
         var2.putFloat(var1.getBodyDamage().getInfectionLevel());

         try {
            ArrayList var3 = var1.getInventory().save(var2.bb);
            var2.putByte((byte)var1.getWornItems().size());
            var1.getWornItems().forEach((var2x) -> {
               GameWindow.WriteString(var2.bb, var2x.getLocation());
               var2.putShort((short)var3.indexOf(var2x.getItem()));
            });
         } catch (IOException var4) {
            var4.printStackTrace();
         }

         connection.endPacketImmediate();
      }
   }

   public void sendOnBeaten(IsoPlayer var1, float var2, float var3, float var4) {
      if (var1 != null && var1.OnlineID != -1) {
         var1.setTransactionID(0);
         ByteBufferWriter var5 = connection.startPacket();
         PacketTypes.doPacket((short)165, var5);
         var5.putByte((byte)var1.OnlineID);
         var5.putFloat(var2);
         var5.putFloat(var3);
         var5.putFloat(var4);
         connection.endPacketImmediate();
      }
   }

   public void addToItemSendBuffer(IsoObject var1, ItemContainer var2, InventoryItem var3) {
      if (var1 instanceof IsoWorldInventoryObject) {
         InventoryItem var4 = ((IsoWorldInventoryObject)var1).getItem();
         if (var3 == null || var4 == null || !(var4 instanceof InventoryContainer) || var2 != ((InventoryContainer)var4).getInventory()) {
            DebugLog.log("ERROR: addToItemSendBuffer parent=" + var1 + " item=" + var3);
            if (Core.bDebug) {
               throw new IllegalStateException();
            } else {
               return;
            }
         }
      } else if (var1 instanceof BaseVehicle) {
         if (var2.vehiclePart == null || var2.vehiclePart.getItemContainer() != var2 || var2.vehiclePart.getVehicle() != var1) {
            DebugLog.log("ERROR: addToItemSendBuffer parent=" + var1 + " item=" + var3);
            if (Core.bDebug) {
               throw new IllegalStateException();
            }

            return;
         }
      } else if (var1 == null || var3 == null || var1.getContainerIndex(var2) == -1) {
         DebugLog.log("ERROR: addToItemSendBuffer parent=" + var1 + " item=" + var3);
         if (Core.bDebug) {
            throw new IllegalStateException();
         }

         return;
      }

      ArrayList var5;
      if (this.itemsToSendRemove.containsKey(var2)) {
         var5 = (ArrayList)this.itemsToSendRemove.get(var2);
         if (var5.remove(var3)) {
            if (var5.isEmpty()) {
               this.itemsToSendRemove.remove(var2);
            }

            return;
         }
      }

      if (this.itemsToSend.containsKey(var2)) {
         ((ArrayList)this.itemsToSend.get(var2)).add(var3);
      } else {
         var5 = new ArrayList();
         this.itemsToSend.put(var2, var5);
         var5.add(var3);
      }

   }

   public void addToItemRemoveSendBuffer(IsoObject var1, ItemContainer var2, InventoryItem var3) {
      if (var1 instanceof IsoWorldInventoryObject) {
         InventoryItem var4 = ((IsoWorldInventoryObject)var1).getItem();
         if (var3 == null || var4 == null || !(var4 instanceof InventoryContainer) || var2 != ((InventoryContainer)var4).getInventory()) {
            DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + var1 + " item=" + var3);
            if (Core.bDebug) {
               throw new IllegalStateException();
            } else {
               return;
            }
         }
      } else if (var1 instanceof BaseVehicle) {
         if (var2.vehiclePart == null || var2.vehiclePart.getItemContainer() != var2 || var2.vehiclePart.getVehicle() != var1) {
            DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + var1 + " item=" + var3);
            if (Core.bDebug) {
               throw new IllegalStateException();
            }

            return;
         }
      } else if (var1 instanceof IsoDeadBody) {
         if (var3 == null || var2 != var1.getContainer()) {
            DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + var1 + " item=" + var3);
            if (Core.bDebug) {
               throw new IllegalStateException();
            }

            return;
         }
      } else if (var1 == null || var3 == null || var1.getContainerIndex(var2) == -1) {
         DebugLog.log("ERROR: addToItemRemoveSendBuffer parent=" + var1 + " item=" + var3);
         if (Core.bDebug) {
            throw new IllegalStateException();
         }

         return;
      }

      if (!SystemDisabler.doWorldSyncEnable) {
         ArrayList var8;
         if (this.itemsToSend.containsKey(var2)) {
            var8 = (ArrayList)this.itemsToSend.get(var2);
            if (var8.remove(var3)) {
               if (var8.isEmpty()) {
                  this.itemsToSend.remove(var2);
               }

               return;
            }
         }

         if (this.itemsToSendRemove.containsKey(var2)) {
            ((ArrayList)this.itemsToSendRemove.get(var2)).add(var3);
         } else {
            var8 = new ArrayList();
            var8.add(var3);
            this.itemsToSendRemove.put(var2, var8);
         }

      } else {
         Object var7 = var2.getParent();
         if (var2.getContainingItem() != null && var2.getContainingItem().getWorldItem() != null) {
            var7 = var2.getContainingItem().getWorldItem();
         }

         GameClient var10000 = instance;
         UdpConnection var5 = connection;
         ByteBufferWriter var6 = var5.startPacket();
         PacketTypes.doPacket((short)22, var6);
         if (var7 instanceof IsoDeadBody) {
            var6.putShort((short)0);
            var6.putInt(((IsoObject)var7).square.getX());
            var6.putInt(((IsoObject)var7).square.getY());
            var6.putInt(((IsoObject)var7).square.getZ());
            var6.putByte((byte)((IsoObject)var7).getStaticMovingObjectIndex());
            var6.putInt(1);
            var6.putInt(var3.id);
         } else if (var7 instanceof IsoWorldInventoryObject) {
            var6.putShort((short)1);
            var6.putInt(((IsoObject)var7).square.getX());
            var6.putInt(((IsoObject)var7).square.getY());
            var6.putInt(((IsoObject)var7).square.getZ());
            var6.putInt(((IsoWorldInventoryObject)var7).getItem().id);
            var6.putInt(1);
            var6.putInt(var3.id);
         } else if (var7 instanceof BaseVehicle) {
            var6.putShort((short)3);
            var6.putInt(((IsoObject)var7).square.getX());
            var6.putInt(((IsoObject)var7).square.getY());
            var6.putInt(((IsoObject)var7).square.getZ());
            var6.putShort(((BaseVehicle)var7).VehicleID);
            var6.putByte((byte)var2.vehiclePart.getIndex());
            var6.putInt(1);
            var6.putInt(var3.id);
         } else {
            var6.putShort((short)2);
            var6.putInt(((IsoObject)var7).square.getX());
            var6.putInt(((IsoObject)var7).square.getY());
            var6.putInt(((IsoObject)var7).square.getZ());
            var6.putByte((byte)((IsoObject)var7).square.getObjects().indexOf(var7));
            var6.putByte((byte)((IsoObject)var7).getContainerIndex(var2));
            var6.putInt(1);
            var6.putInt(var3.id);
         }

         var5.endPacketUnordered();
      }
   }

   public void sendAddedRemovedItems(boolean var1) {
      boolean var2 = var1 || this.itemSendFrequency.Check();
      Iterator var3;
      Entry var4;
      ItemContainer var5;
      ArrayList var6;
      Object var7;
      ByteBufferWriter var8;
      if (!SystemDisabler.doWorldSyncEnable && !this.itemsToSendRemove.isEmpty() && var2) {
         var3 = this.itemsToSendRemove.entrySet().iterator();

         label119:
         while(true) {
            do {
               if (!var3.hasNext()) {
                  this.itemsToSendRemove.clear();
                  break label119;
               }

               var4 = (Entry)var3.next();
               var5 = (ItemContainer)var4.getKey();
               var6 = (ArrayList)var4.getValue();
               var7 = var5.getParent();
               if (var5.getContainingItem() != null && var5.getContainingItem().getWorldItem() != null) {
                  var7 = var5.getContainingItem().getWorldItem();
               }
            } while(((IsoObject)var7).square == null);

            var8 = connection.startPacket();
            PacketTypes.doPacket((short)22, var8);
            if (var7 instanceof IsoDeadBody) {
               var8.putShort((short)0);
               var8.putInt(((IsoObject)var7).square.getX());
               var8.putInt(((IsoObject)var7).square.getY());
               var8.putInt(((IsoObject)var7).square.getZ());
               var8.putByte((byte)((IsoObject)var7).getStaticMovingObjectIndex());
            } else if (var7 instanceof IsoWorldInventoryObject) {
               var8.putShort((short)1);
               var8.putInt(((IsoObject)var7).square.getX());
               var8.putInt(((IsoObject)var7).square.getY());
               var8.putInt(((IsoObject)var7).square.getZ());
               var8.putInt(((IsoWorldInventoryObject)var7).getItem().id);
            } else if (var7 instanceof BaseVehicle) {
               var8.putShort((short)3);
               var8.putInt(((IsoObject)var7).square.getX());
               var8.putInt(((IsoObject)var7).square.getY());
               var8.putInt(((IsoObject)var7).square.getZ());
               var8.putShort(((BaseVehicle)var7).VehicleID);
               var8.putByte((byte)var5.vehiclePart.getIndex());
            } else {
               var8.putShort((short)2);
               var8.putInt(((IsoObject)var7).square.getX());
               var8.putInt(((IsoObject)var7).square.getY());
               var8.putInt(((IsoObject)var7).square.getZ());
               var8.putByte((byte)((IsoObject)var7).square.getObjects().indexOf(var7));
               var8.putByte((byte)((IsoObject)var7).getContainerIndex(var5));
            }

            var8.putInt(var6.size());

            for(int var9 = 0; var9 < var6.size(); ++var9) {
               InventoryItem var10 = (InventoryItem)var6.get(var9);
               var8.putInt(var10.id);
            }

            if (var1) {
               connection.endPacket();
            } else {
               connection.endPacketUnordered();
            }
         }
      }

      if (!this.itemsToSend.isEmpty() && var2) {
         var3 = this.itemsToSend.entrySet().iterator();

         while(var3.hasNext()) {
            var4 = (Entry)var3.next();
            var5 = (ItemContainer)var4.getKey();
            var6 = (ArrayList)var4.getValue();
            var7 = var5.getParent();
            if (var5.getContainingItem() != null && var5.getContainingItem().getWorldItem() != null) {
               var7 = var5.getContainingItem().getWorldItem();
            }

            if (((IsoObject)var7).square != null) {
               var8 = connection.startPacket();
               PacketTypes.doPacket((short)20, var8);
               if (var7 instanceof IsoDeadBody) {
                  var8.putShort((short)0);
                  var8.putInt(((IsoObject)var7).square.getX());
                  var8.putInt(((IsoObject)var7).square.getY());
                  var8.putInt(((IsoObject)var7).square.getZ());
                  var8.putByte((byte)((IsoObject)var7).getStaticMovingObjectIndex());

                  try {
                     CompressIdenticalItems.save(var8.bb, var6, (IsoGameCharacter)null);
                  } catch (Exception var14) {
                     var14.printStackTrace();
                  }
               } else if (var7 instanceof IsoWorldInventoryObject) {
                  var8.putShort((short)1);
                  var8.putInt(((IsoObject)var7).square.getX());
                  var8.putInt(((IsoObject)var7).square.getY());
                  var8.putInt(((IsoObject)var7).square.getZ());
                  var8.putInt(((IsoWorldInventoryObject)var7).getItem().id);

                  try {
                     CompressIdenticalItems.save(var8.bb, var6, (IsoGameCharacter)null);
                  } catch (Exception var13) {
                     var13.printStackTrace();
                  }
               } else if (var7 instanceof BaseVehicle) {
                  var8.putShort((short)3);
                  var8.putInt(((IsoObject)var7).square.getX());
                  var8.putInt(((IsoObject)var7).square.getY());
                  var8.putInt(((IsoObject)var7).square.getZ());
                  var8.putShort(((BaseVehicle)var7).VehicleID);
                  var8.putByte((byte)var5.vehiclePart.getIndex());

                  try {
                     CompressIdenticalItems.save(var8.bb, var6, (IsoGameCharacter)null);
                  } catch (Exception var12) {
                     var12.printStackTrace();
                  }
               } else {
                  var8.putShort((short)2);
                  var8.putInt(((IsoObject)var7).square.getX());
                  var8.putInt(((IsoObject)var7).square.getY());
                  var8.putInt(((IsoObject)var7).square.getZ());
                  var8.putByte((byte)((IsoObject)var7).square.getObjects().indexOf(var7));
                  var8.putByte((byte)((IsoObject)var7).getContainerIndex(var5));

                  try {
                     CompressIdenticalItems.save(var8.bb, var6, (IsoGameCharacter)null);
                  } catch (Exception var11) {
                     var11.printStackTrace();
                  }
               }

               if (var1) {
                  connection.endPacket();
               } else {
                  connection.endPacketUnordered();
               }
            }
         }

         this.itemsToSend.clear();
      }

   }

   public void checkAddedRemovedItems(IsoObject var1) {
      if (var1 != null) {
         if (!this.itemsToSend.isEmpty() || !this.itemsToSendRemove.isEmpty()) {
            if (var1 instanceof IsoDeadBody) {
               if (this.itemsToSend.containsKey(var1.getContainer()) || this.itemsToSendRemove.containsKey(var1.getContainer())) {
                  this.sendAddedRemovedItems(true);
               }

            } else {
               ItemContainer var3;
               if (var1 instanceof IsoWorldInventoryObject) {
                  InventoryItem var4 = ((IsoWorldInventoryObject)var1).getItem();
                  if (var4 instanceof InventoryContainer) {
                     var3 = ((InventoryContainer)var4).getInventory();
                     if (this.itemsToSend.containsKey(var3) || this.itemsToSendRemove.containsKey(var3)) {
                        this.sendAddedRemovedItems(true);
                     }
                  }

               } else if (!(var1 instanceof BaseVehicle)) {
                  for(int var2 = 0; var2 < var1.getContainerCount(); ++var2) {
                     var3 = var1.getContainerByIndex(var2);
                     if (this.itemsToSend.containsKey(var3) || this.itemsToSendRemove.containsKey(var3)) {
                        this.sendAddedRemovedItems(true);
                        return;
                     }
                  }

               }
            }
         }
      }
   }

   private void writeItemStats(ByteBufferWriter var1, InventoryItem var2) {
      var1.putInt(var2.id);
      var1.putInt(var2.getUses());
      var1.putFloat(var2 instanceof DrainableComboItem ? ((DrainableComboItem)var2).getUsedDelta() : 0.0F);
      if (var2 instanceof Food) {
         Food var3 = (Food)var2;
         var1.putBoolean(true);
         var1.putFloat(var3.getHungChange());
         var1.putFloat(var3.getCalories());
         var1.putFloat(var3.getCarbohydrates());
         var1.putFloat(var3.getLipids());
         var1.putFloat(var3.getProteins());
         var1.putFloat(var3.getThirstChange());
         var1.putInt(var3.getFluReduction());
         var1.putFloat(var3.getPainReduction());
         var1.putFloat(var3.getEndChange());
         var1.putInt(var3.getReduceFoodSickness());
         var1.putFloat(var3.getStressChange());
         var1.putFloat(var3.getFatigueChange());
      } else {
         var1.putBoolean(false);
      }

   }

   public void sendItemStats(InventoryItem var1) {
      if (var1 != null) {
         if (var1.getWorldItem() != null && var1.getWorldItem().getWorldObjectIndex() != -1) {
            IsoWorldInventoryObject var6 = var1.getWorldItem();
            ByteBufferWriter var7 = connection.startPacket();
            PacketTypes.doPacket((short)35, var7);
            var7.putShort((short)1);
            var7.putInt(var6.square.getX());
            var7.putInt(var6.square.getY());
            var7.putInt(var6.square.getZ());
            this.writeItemStats(var7, var1);
            connection.endPacket();
         } else if (var1.getContainer() == null) {
            DebugLog.log("ERROR: sendItemStats(): item is neither in a container nor on the ground");
            if (Core.bDebug) {
               throw new IllegalStateException();
            }
         } else {
            ItemContainer var2 = var1.getContainer();
            Object var3 = var2.getParent();
            if (var2.getContainingItem() != null && var2.getContainingItem().getWorldItem() != null) {
               var3 = var2.getContainingItem().getWorldItem();
            }

            if (var3 instanceof IsoWorldInventoryObject) {
               InventoryItem var5 = ((IsoWorldInventoryObject)var3).getItem();
               if (!(var5 instanceof InventoryContainer) || var2 != ((InventoryContainer)var5).getInventory()) {
                  DebugLog.log("ERROR: sendItemStats() parent=" + var3 + " item=" + var1);
                  if (Core.bDebug) {
                     throw new IllegalStateException();
                  } else {
                     return;
                  }
               }
            } else if (var3 instanceof BaseVehicle) {
               if (var2.vehiclePart == null || var2.vehiclePart.getItemContainer() != var2 || var2.vehiclePart.getVehicle() != var3) {
                  DebugLog.log("ERROR: sendItemStats() parent=" + var3 + " item=" + var1);
                  if (Core.bDebug) {
                     throw new IllegalStateException();
                  }

                  return;
               }
            } else if (var3 instanceof IsoDeadBody) {
               if (var2 != ((IsoObject)var3).getContainer()) {
                  DebugLog.log("ERROR: sendItemStats() parent=" + var3 + " item=" + var1);
                  if (Core.bDebug) {
                     throw new IllegalStateException();
                  }

                  return;
               }
            } else if (var3 == null || ((IsoObject)var3).getContainerIndex(var2) == -1) {
               DebugLog.log("ERROR: sendItemStats() parent=" + var3 + " item=" + var1);
               if (Core.bDebug) {
                  throw new IllegalStateException();
               }

               return;
            }

            ByteBufferWriter var8 = connection.startPacket();
            PacketTypes.doPacket((short)35, var8);
            if (var3 instanceof IsoDeadBody) {
               var8.putShort((short)0);
               var8.putInt(((IsoObject)var3).square.getX());
               var8.putInt(((IsoObject)var3).square.getY());
               var8.putInt(((IsoObject)var3).square.getZ());
               var8.putByte((byte)((IsoObject)var3).getStaticMovingObjectIndex());
            } else if (var3 instanceof IsoWorldInventoryObject) {
               var8.putShort((short)1);
               var8.putInt(((IsoObject)var3).square.getX());
               var8.putInt(((IsoObject)var3).square.getY());
               var8.putInt(((IsoObject)var3).square.getZ());
            } else if (var3 instanceof BaseVehicle) {
               var8.putShort((short)3);
               var8.putInt(((IsoObject)var3).square.getX());
               var8.putInt(((IsoObject)var3).square.getY());
               var8.putInt(((IsoObject)var3).square.getZ());
               var8.putShort(((BaseVehicle)var3).VehicleID);
               var8.putByte((byte)var2.vehiclePart.getIndex());
            } else {
               var8.putShort((short)2);
               var8.putInt(((IsoObject)var3).square.getX());
               var8.putInt(((IsoObject)var3).square.getY());
               var8.putInt(((IsoObject)var3).square.getZ());
               var8.putByte((byte)((IsoObject)var3).getObjectIndex());
               var8.putByte((byte)((IsoObject)var3).getContainerIndex(var2));
            }

            this.writeItemStats(var8, var1);
            connection.endPacket();
         }
      }
   }

   public void PlayWorldSound(String var1, boolean var2, int var3, int var4, int var5) {
      ByteBufferWriter var6 = connection.startPacket();
      PacketTypes.doPacket((short)53, var6);
      var6.putUTF(var1);
      var6.putInt(var3);
      var6.putInt(var4);
      var6.putInt(var5);
      var6.putByte((byte)(var2 ? 1 : 0));
      connection.endPacketImmediate();
   }

   public void startLocalServer() throws Exception {
      bClient = true;
      ip = "127.0.0.1";
      Thread var1 = new Thread(ThreadGroups.Workers, () -> {
         String var0 = System.getProperty("file.separator");
         String var1 = System.getProperty("java.class.path");
         String var10000 = System.getProperty("java.home");
         String var2 = var10000 + var0 + "bin" + var0 + "java";
         ProcessBuilder var3 = new ProcessBuilder(new String[]{var2, "-Xms2048m", "-Xmx2048m", "-Djava.library.path=../natives/", "-cp", "lwjgl.jar;lwjgl_util.jar;sqlitejdbc-v056.jar;../bin/", "zombie.network.GameServer"});
         var3.redirectErrorStream(true);
         Process var4 = null;

         try {
            var4 = var3.start();
         } catch (IOException var10) {
            var10.printStackTrace();
         }

         InputStreamReader var5 = new InputStreamReader(var4.getInputStream());
         boolean var6 = false;

         try {
            while(!var5.ready()) {
               int var7;
               try {
                  while((var7 = var5.read()) != -1) {
                     System.out.print((char)var7);
                  }
               } catch (IOException var11) {
                  var11.printStackTrace();
               }

               try {
                  var5.close();
               } catch (IOException var9) {
                  var9.printStackTrace();
               }
            }
         } catch (IOException var12) {
            var12.printStackTrace();
         }

      });
      var1.setUncaughtExceptionHandler(GameWindow::uncaughtException);
      var1.start();
   }

   public static void sendPing() {
      if (bClient) {
         ByteBufferWriter var0 = connection.startPingPacket();
         PacketTypes.doPingPacket(var0);
         var0.putLong(System.currentTimeMillis());
         var0.putLong(0L);
         connection.endPingPacket();
      }

   }

   public static void registerZone(IsoMetaGrid.Zone var0, boolean var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)94, var2);
      var2.putUTF(var0.name);
      var2.putUTF(var0.type);
      var2.putInt(var0.x);
      var2.putInt(var0.y);
      var2.putInt(var0.z);
      var2.putInt(var0.w);
      var2.putInt(var0.h);
      var2.putInt(var0.getLastActionTimestamp());
      var2.putBoolean(var1);
      connection.endPacketImmediate();
   }

   private void receiveHelicopter(ByteBuffer var1) {
      float var2 = var1.getFloat();
      float var3 = var1.getFloat();
      boolean var4 = var1.get() == 1;
      if (IsoWorld.instance != null && IsoWorld.instance.helicopter != null) {
         IsoWorld.instance.helicopter.clientSync(var2, var3, var4);
      }

   }

   public static void sendSafehouse(SafeHouse var0, boolean var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)114, var2);
      var2.putInt(var0.getX());
      var2.putInt(var0.getY());
      var2.putInt(var0.getW());
      var2.putInt(var0.getH());
      var2.putUTF(var0.getOwner());
      var2.putInt(var0.getPlayers().size());
      Iterator var3 = var0.getPlayers().iterator();

      String var4;
      while(var3.hasNext()) {
         var4 = (String)var3.next();
         var2.putUTF(var4);
      }

      var2.putInt(var0.playersRespawn.size());
      var3 = var0.playersRespawn.iterator();

      while(var3.hasNext()) {
         var4 = (String)var3.next();
         var2.putUTF(var4);
      }

      var2.putBoolean(var1);
      var2.putUTF(var0.getTitle());
      connection.endPacketImmediate();
   }

   private void syncSafehouse(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      String var6 = GameWindow.ReadString(var1);
      int var7 = var1.getInt();
      SafeHouse var8 = SafeHouse.getSafeHouse(var2, var3, var4, var5);
      if (var8 == null) {
         var8 = SafeHouse.addSafeHouse(var2, var3, var4, var5, var6, true);
      }

      if (var8 != null) {
         var8.getPlayers().clear();

         int var9;
         for(var9 = 0; var9 < var7; ++var9) {
            var8.getPlayers().add(GameWindow.ReadString(var1));
         }

         var9 = var1.getInt();
         var8.playersRespawn.clear();

         for(int var10 = 0; var10 < var9; ++var10) {
            var8.playersRespawn.add(GameWindow.ReadString(var1));
         }

         if (var1.get() == 1) {
            SafeHouse.getSafehouseList().remove(var8);
         }

         var8.setTitle(GameWindow.ReadString(var1));
         var8.setOwner(var6);
         LuaEventManager.triggerEvent("OnSafehousesChanged");
      }
   }

   public IsoPlayer getPlayerFromUsername(String var1) {
      ArrayList var2 = this.getPlayers();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         IsoPlayer var4 = (IsoPlayer)var2.get(var3);
         if (var4.getUsername().equals(var1)) {
            return var4;
         }
      }

      return null;
   }

   public static void destroy(IsoObject var0) {
      if (ServerOptions.instance.AllowDestructionBySledgehammer.getValue()) {
         ByteBufferWriter var1 = connection.startPacket();
         PacketTypes.doPacket((short)115, var1);
         IsoGridSquare var2 = var0.getSquare();
         var1.putInt(var2.getX());
         var1.putInt(var2.getY());
         var1.putInt(var2.getZ());
         var1.putInt(var2.getObjects().indexOf(var0));
         connection.endPacketImmediate();
         var2.RemoveTileObject(var0);
      }

   }

   public static void sendTeleport(IsoPlayer var0, float var1, float var2, float var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)108, var4);
      GameWindow.WriteString(var4.bb, var0.getUsername());
      var4.putFloat(var1);
      var4.putFloat(var2);
      var4.putFloat(var3);
      connection.endPacketImmediate();
   }

   public static void sendStopFire(IsoGridSquare var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)116, var1);
      var1.putByte((byte)0);
      var1.putInt(var0.getX());
      var1.putInt(var0.getY());
      var1.putInt(var0.getZ());
      connection.endPacketImmediate();
   }

   public static void sendStopFire(IsoGameCharacter var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)116, var1);
      if (var0 instanceof IsoPlayer) {
         var1.putByte((byte)1);
         var1.putShort((short)((IsoPlayer)var0).getOnlineID());
      }

      if (var0 instanceof IsoZombie) {
         var1.putByte((byte)2);
         var1.putShort(((IsoZombie)var0).OnlineID);
      }

      connection.endPacketImmediate();
   }

   public void sendCataplasm(int var1, int var2, float var3, float var4, float var5) {
      ByteBufferWriter var6 = connection.startPacket();
      PacketTypes.doPacket((short)117, var6);
      var6.putShort((short)var1);
      var6.putInt(var2);
      var6.putFloat(var3);
      var6.putFloat(var4);
      var6.putFloat(var5);
      connection.endPacketImmediate();
   }

   private void receiveBodyDamageUpdate(ByteBuffer var1) {
      BodyDamageSync.instance.clientPacket(var1);
   }

   private static void dealWithNetDataShort(ZomboidNetData var0, ByteBuffer var1) {
      short var2 = var1.getShort();
      switch(var2) {
      case 1000:
         receiveWaveSignal(var1);
         break;
      case 1002:
         receiveRadioServerData(var1);
         break;
      case 1004:
         receiveRadioDeviceDataState(var1);
         break;
      case 1200:
         SyncCustomLightSwitchSettings(var1);
      }

   }

   public static void receiveRadioDeviceDataState(ByteBuffer var0) {
      byte var1 = var0.get();
      int var2;
      if (var1 == 1) {
         var2 = var0.getInt();
         int var3 = var0.getInt();
         int var4 = var0.getInt();
         int var5 = var0.getInt();
         IsoGridSquare var6 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
         if (var6 != null && var5 >= 0 && var5 < var6.getObjects().size()) {
            IsoObject var7 = (IsoObject)var6.getObjects().get(var5);
            if (var7 instanceof IsoWaveSignal) {
               DeviceData var8 = ((IsoWaveSignal)var7).getDeviceData();
               if (var8 != null) {
                  try {
                     var8.receiveDeviceDataStatePacket(var0, (UdpConnection)null);
                  } catch (Exception var12) {
                     System.out.print(var12.getMessage());
                  }
               }
            }
         }
      } else if (var1 == 0) {
         var2 = var0.getInt();
         IsoPlayer var14 = (IsoPlayer)IDToPlayerMap.get(var2);
         byte var16 = var0.get();
         if (var14 != null) {
            Radio var18 = null;
            if (var16 == 1 && var14.getPrimaryHandItem() instanceof Radio) {
               var18 = (Radio)var14.getPrimaryHandItem();
            }

            if (var16 == 2 && var14.getSecondaryHandItem() instanceof Radio) {
               var18 = (Radio)var14.getSecondaryHandItem();
            }

            if (var18 != null && var18.getDeviceData() != null) {
               try {
                  var18.getDeviceData().receiveDeviceDataStatePacket(var0, connection);
               } catch (Exception var11) {
                  System.out.print(var11.getMessage());
               }
            }
         }
      } else if (var1 == 2) {
         short var13 = var0.getShort();
         short var15 = var0.getShort();
         BaseVehicle var17 = VehicleManager.instance.getVehicleByID(var13);
         if (var17 != null) {
            VehiclePart var20 = var17.getPartByIndex(var15);
            if (var20 != null) {
               DeviceData var19 = var20.getDeviceData();
               if (var19 != null) {
                  try {
                     var19.receiveDeviceDataStatePacket(var0, (UdpConnection)null);
                  } catch (Exception var10) {
                     System.out.print(var10.getMessage());
                  }
               }
            }
         }
      }

   }

   public static void sendRadioServerDataRequest() {
      ByteBufferWriter var0 = connection.startPacket();
      PacketTypesShort.doPacket((short)1002, var0);
      connection.endPacketImmediate();
   }

   private static void receiveRadioServerData(ByteBuffer var0) {
      ZomboidRadio var1 = ZomboidRadio.getInstance();
      int var2 = var0.getInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = GameWindow.ReadString(var0);
         int var5 = var0.getInt();

         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var0.getInt();
            String var8 = GameWindow.ReadString(var0);
            var1.addChannelName(var8, var7, var4);
         }
      }

      var1.setHasRecievedServerData(true);
   }

   public static void sendIsoWaveSignal(int var0, int var1, int var2, String var3, String var4, float var5, float var6, float var7, int var8, boolean var9) {
      ByteBufferWriter var10 = connection.startPacket();
      PacketTypesShort.doPacket((short)1000, var10);
      var10.putInt(var0);
      var10.putInt(var1);
      var10.putInt(var2);
      var10.putBoolean(var3 != null);
      if (var3 != null) {
         GameWindow.WriteString(var10.bb, var3);
      }

      var10.putByte((byte)(var4 != null ? 1 : 0));
      if (var4 != null) {
         var10.putUTF(var4);
      }

      var10.putFloat(var5);
      var10.putFloat(var6);
      var10.putFloat(var7);
      var10.putInt(var8);
      var10.putByte((byte)(var9 ? 1 : 0));
      connection.endPacketImmediate();
   }

   private static void receiveWaveSignal(ByteBuffer var0) {
      if (ChatManager.getInstance().isWorking()) {
         int var1 = var0.getInt();
         int var2 = var0.getInt();
         int var3 = var0.getInt();
         String var4 = null;
         byte var5 = var0.get();
         if (var5 == 1) {
            var4 = GameWindow.ReadString(var0);
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
         ZomboidRadio.getInstance().ReceiveTransmission(var1, var2, var3, var4, var6, var7, var8, var9, var10, var11);
      }
   }

   public static void sendPlayerListensChannel(int var0, boolean var1, boolean var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypesShort.doPacket((short)1001, var3);
      var3.putInt(var0);
      var3.putByte((byte)(var1 ? 1 : 0));
      var3.putByte((byte)(var2 ? 1 : 0));
      connection.endPacketImmediate();
   }

   private void receiveFurnaceChange(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 == null) {
         this.delayPacket(var2, var3, var4);
      } else {
         if (var5 != null) {
            BSFurnace var6 = null;

            for(int var7 = 0; var7 < var5.getObjects().size(); ++var7) {
               if (var5.getObjects().get(var7) instanceof BSFurnace) {
                  var6 = (BSFurnace)var5.getObjects().get(var7);
                  break;
               }
            }

            if (var6 == null) {
               DebugLog.log("receiveFurnaceChange: furnace is null x,y,z=" + var2 + "," + var3 + "," + var4);
               return;
            }

            var6.fireStarted = var1.get() == 1;
            var6.fuelAmount = var1.getFloat();
            var6.fuelDecrease = var1.getFloat();
            var6.heat = var1.getFloat();
            var6.sSprite = GameWindow.ReadString(var1);
            var6.sLitSprite = GameWindow.ReadString(var1);
            var6.updateLight();
         }

      }
   }

   public static void sendFurnaceChange(BSFurnace var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)120, var1);
      var1.putInt(var0.getSquare().getX());
      var1.putInt(var0.getSquare().getY());
      var1.putInt(var0.getSquare().getZ());
      var1.putByte((byte)(var0.isFireStarted() ? 1 : 0));
      var1.putFloat(var0.getFuelAmount());
      var1.putFloat(var0.getFuelDecrease());
      var1.putFloat(var0.getHeat());
      GameWindow.WriteString(var1.bb, var0.sSprite);
      GameWindow.WriteString(var1.bb, var0.sLitSprite);
      connection.endPacketImmediate();
   }

   public static void sendCompost(IsoCompost var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)122, var1);
      var1.putInt(var0.getSquare().getX());
      var1.putInt(var0.getSquare().getY());
      var1.putInt(var0.getSquare().getZ());
      var1.putFloat(var0.getCompost());
      connection.endPacketImmediate();
   }

   private void syncCompost(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 != null) {
         IsoCompost var6 = var5.getCompost();
         if (var6 == null) {
            var6 = new IsoCompost(var5.getCell(), var5);
            var5.AddSpecialObject(var6);
         }

         var6.setCompost(var1.getFloat());
         var6.updateSprite();
      }

   }

   public void requestUserlog(String var1) {
      if (canSeePlayerStats()) {
         ByteBufferWriter var2 = connection.startPacket();
         PacketTypes.doPacket((short)128, var2);
         GameWindow.WriteString(var2.bb, var1);
         connection.endPacketImmediate();
      }
   }

   public void addUserlog(String var1, String var2, String var3) {
      if (canSeePlayerStats()) {
         ByteBufferWriter var4 = connection.startPacket();
         PacketTypes.doPacket((short)129, var4);
         GameWindow.WriteString(var4.bb, var1);
         GameWindow.WriteString(var4.bb, var2);
         GameWindow.WriteString(var4.bb, var3);
         connection.endPacketImmediate();
      }
   }

   public void removeUserlog(String var1, String var2, String var3) {
      if (canModifyPlayerStats()) {
         ByteBufferWriter var4 = connection.startPacket();
         PacketTypes.doPacket((short)130, var4);
         GameWindow.WriteString(var4.bb, var1);
         GameWindow.WriteString(var4.bb, var2);
         GameWindow.WriteString(var4.bb, var3);
         connection.endPacketImmediate();
      }
   }

   public void addWarningPoint(String var1, String var2, int var3) {
      if (canModifyPlayerStats()) {
         ByteBufferWriter var4 = connection.startPacket();
         PacketTypes.doPacket((short)131, var4);
         GameWindow.WriteString(var4.bb, var1);
         GameWindow.WriteString(var4.bb, var2);
         var4.putInt(var3);
         connection.endPacketImmediate();
      }
   }

   private static void receiveAdminMessage(ByteBuffer var0) {
      if (canSeePlayerStats()) {
         String var1 = GameWindow.ReadString(var0);
         int var2 = var0.getInt();
         int var3 = var0.getInt();
         int var4 = var0.getInt();
         LuaEventManager.triggerEvent("OnAdminMessage", var1, var2, var3, var4);
      }

   }

   public void wakeUpPlayer(IsoPlayer var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)133, var2);
      var2.putInt(var1.OnlineID);
      connection.endPacketImmediate();
   }

   private static void receiveWakeUpOrder(ByteBuffer var0) {
      IsoPlayer var1 = (IsoPlayer)IDToPlayerMap.get(var0.getInt());
      if (var1 != null) {
         SleepingEvent.instance.wakeUp(var1, true);
      }

   }

   public void getDBSchema() {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)135, var1);
      connection.endPacketImmediate();
   }

   private void receiveDBSchema(ByteBuffer var1) {
      if (!accessLevel.equals("Observer") && !accessLevel.equals("")) {
         this.dbSchema = LuaManager.platform.newTable();
         int var2 = var1.getInt();

         for(int var3 = 0; var3 < var2; ++var3) {
            KahluaTable var4 = LuaManager.platform.newTable();
            String var5 = GameWindow.ReadString(var1);
            int var6 = var1.getInt();

            for(int var7 = 0; var7 < var6; ++var7) {
               KahluaTable var8 = LuaManager.platform.newTable();
               String var9 = GameWindow.ReadString(var1);
               String var10 = GameWindow.ReadString(var1);
               var8.rawset("name", var9);
               var8.rawset("type", var10);
               var4.rawset(var7, var8);
            }

            this.dbSchema.rawset(var5, var4);
         }

         LuaEventManager.triggerEvent("OnGetDBSchema", this.dbSchema);
      }
   }

   public void getTableResult(String var1, int var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)136, var3);
      var3.putInt(var2);
      var3.putUTF(var1);
      connection.endPacketImmediate();
   }

   private void receiveTableResult(ByteBuffer var1) {
      ArrayList var2 = new ArrayList();
      int var3 = var1.getInt();
      String var4 = GameWindow.ReadString(var1);
      int var5 = var1.getInt();
      ArrayList var6 = new ArrayList();

      for(int var7 = 0; var7 < var5; ++var7) {
         DBResult var8 = new DBResult();
         var8.setTableName(var4);
         int var9 = var1.getInt();

         for(int var10 = 0; var10 < var9; ++var10) {
            String var11 = GameWindow.ReadString(var1);
            String var12 = GameWindow.ReadString(var1);
            var8.getValues().put(var11, var12);
            if (var7 == 0) {
               var6.add(var11);
            }
         }

         var8.setColumns(var6);
         var2.add(var8);
      }

      LuaEventManager.triggerEvent("OnGetTableResult", var2, var3, var4);
   }

   public void executeQuery(String var1, KahluaTable var2) {
      if (accessLevel.equals("admin")) {
         ByteBufferWriter var3 = connection.startPacket();
         PacketTypes.doPacket((short)137, var3);

         try {
            var3.putUTF(var1);
            var2.save(var3.bb);
         } catch (Throwable var8) {
            ExceptionLogger.logException(var8);
         } finally {
            connection.endPacketImmediate();
         }

      }
   }

   public ArrayList getConnectedPlayers() {
      return this.connectedPlayers;
   }

   public static void sendNonPvpZone(NonPvpZone var0, boolean var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)139, var2);
      var2.putInt(var0.getX());
      var2.putInt(var0.getY());
      var2.putInt(var0.getX2());
      var2.putInt(var0.getY2());
      var2.putUTF(var0.getTitle());
      var2.putBoolean(var1);
      connection.endPacketImmediate();
   }

   public static void sendFaction(Faction var0, boolean var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)140, var2);
      var0.writeToBuffer(var2, var1);
      connection.endPacketImmediate();
   }

   public static void sendFactionInvite(Faction var0, IsoPlayer var1, String var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)141, var3);
      var3.putUTF(var0.getName());
      var3.putUTF(var1.getUsername());
      var3.putUTF(var2);
      connection.endPacketImmediate();
   }

   private void receiveFactionInvite(ByteBuffer var1) {
      String var2 = GameWindow.ReadString(var1);
      String var3 = GameWindow.ReadString(var1);
      LuaEventManager.triggerEvent("ReceiveFactionInvite", var2, var3);
   }

   public static void acceptFactionInvite(Faction var0, String var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)142, var2);
      var2.putUTF(var0.getName());
      var2.putUTF(var1);
      connection.endPacketImmediate();
   }

   private void AcceptedFactionInvite(ByteBuffer var1) {
      String var2 = GameWindow.ReadString(var1);
      String var3 = GameWindow.ReadString(var1);
      Faction var4 = Faction.getFaction(var2);
      if (var4 != null) {
         var4.addPlayer(var3);
      }

      LuaEventManager.triggerEvent("AcceptedFactionInvite", var2, var3);
   }

   public static void addTicket(String var0, String var1, int var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)143, var3);
      var3.putUTF(var0);
      var3.putUTF(var1);
      var3.putInt(var2);
      connection.endPacketImmediate();
   }

   public static void getTickets(String var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)144, var1);
      var1.putUTF(var0);
      connection.endPacketImmediate();
   }

   private static void gotTickets(ByteBuffer var0) {
      ArrayList var1 = new ArrayList();
      int var2 = var0.getInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         DBTicket var4 = new DBTicket(GameWindow.ReadString(var0), GameWindow.ReadString(var0), var0.getInt());
         var1.add(var4);
         if (var0.get() == 1) {
            DBTicket var5 = new DBTicket(GameWindow.ReadString(var0), GameWindow.ReadString(var0), var0.getInt());
            var5.setIsAnswer(true);
            var4.setAnswer(var5);
         }
      }

      LuaEventManager.triggerEvent("ViewTickets", var1);
   }

   public static void removeTicket(int var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)145, var1);
      var1.putInt(var0);
      connection.endPacketImmediate();
   }

   public static boolean sendItemListNet(IsoPlayer var0, ArrayList var1, IsoPlayer var2, String var3, String var4) {
      ByteBufferWriter var5 = connection.startPacket();
      PacketTypes.doPacket((short)150, var5);
      var5.putByte((byte)(var2 != null ? 1 : 0));
      if (var2 != null) {
         var5.putShort((short)var2.getOnlineID());
      }

      var5.putByte((byte)(var0 != null ? 1 : 0));
      if (var0 != null) {
         var5.putShort((short)var0.getOnlineID());
      }

      GameWindow.WriteString(var5.bb, var3);
      var5.putByte((byte)(var4 != null ? 1 : 0));
      if (var4 != null) {
         GameWindow.WriteString(var5.bb, var4);
      }

      try {
         CompressIdenticalItems.save(var5.bb, var1, (IsoGameCharacter)null);
      } catch (Exception var7) {
         var7.printStackTrace();
         connection.cancelPacket();
         return false;
      }

      connection.endPacketImmediate();
      return true;
   }

   private static void receiveItemListNet(ByteBuffer var0) {
      IsoPlayer var1 = null;
      if (var0.get() != 1) {
         var1 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var0.getShort()));
      }

      IsoPlayer var2 = null;
      if (var0.get() == 1) {
         var2 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var0.getShort()));
      }

      String var3 = GameWindow.ReadString(var0);
      String var4 = null;
      if (var0.get() == 1) {
         var4 = GameWindow.ReadString(var0);
      }

      short var5 = var0.getShort();
      ArrayList var6 = new ArrayList(var5);

      try {
         for(int var7 = 0; var7 < var5; ++var7) {
            InventoryItem var8 = InventoryItem.loadItem(var0, 184);
            if (var8 != null) {
               var6.add(var8);
            }
         }
      } catch (IOException var9) {
         var9.printStackTrace();
      }

      LuaEventManager.triggerEvent("OnReceiveItemListNet", var2, var6, var1, var3, var4);
   }

   public void requestTrading(IsoPlayer var1, IsoPlayer var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)146, var3);
      var3.putInt(var1.OnlineID);
      var3.putInt(var2.OnlineID);
      var3.putByte((byte)0);
      connection.endPacketImmediate();
   }

   public void acceptTrading(IsoPlayer var1, IsoPlayer var2, boolean var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)146, var4);
      var4.putInt(var2.OnlineID);
      var4.putInt(var1.OnlineID);
      var4.putByte((byte)(var3 ? 1 : 2));
      connection.endPacketImmediate();
   }

   private static void isRequestedToTrade(ByteBuffer var0) {
      int var1 = var0.getInt();
      byte var2 = var0.get();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var1);
      if (var3 != null) {
         if (var2 == 0) {
            LuaEventManager.triggerEvent("RequestTrade", var3);
         } else {
            LuaEventManager.triggerEvent("AcceptedTrade", var2 == 1);
         }
      }

   }

   public void tradingUISendAddItem(IsoPlayer var1, IsoPlayer var2, InventoryItem var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)147, var4);
      var4.putInt(var1.OnlineID);
      var4.putInt(var2.OnlineID);

      try {
         var3.saveWithSize(var4.bb, false);
      } catch (IOException var6) {
         var6.printStackTrace();
      }

      connection.endPacketImmediate();
   }

   private static void tradingUIAddItem(ByteBuffer var0) {
      int var1 = var0.getInt();
      InventoryItem var2 = null;

      try {
         var2 = InventoryItem.loadItem(var0, 184);
      } catch (Exception var4) {
         var4.printStackTrace();
         return;
      }

      if (var2 != null) {
         IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var1);
         if (var3 != null) {
            LuaEventManager.triggerEvent("TradingUIAddItem", var3, var2);
         }

      }
   }

   public void tradingUISendRemoveItem(IsoPlayer var1, IsoPlayer var2, int var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)148, var4);
      var4.putInt(var1.OnlineID);
      var4.putInt(var2.OnlineID);
      var4.putInt(var3);
      connection.endPacketImmediate();
   }

   private static void tradingUIRemoveItem(ByteBuffer var0) {
      int var1 = var0.getInt();
      int var2 = var0.getInt();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var1);
      if (var3 != null) {
         LuaEventManager.triggerEvent("TradingUIRemoveItem", var3, var2);
      }

   }

   public void tradingUISendUpdateState(IsoPlayer var1, IsoPlayer var2, int var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)149, var4);
      var4.putInt(var1.OnlineID);
      var4.putInt(var2.OnlineID);
      var4.putInt(var3);
      connection.endPacketImmediate();
   }

   private static void tradingUIUpdateState(ByteBuffer var0) {
      int var1 = var0.getInt();
      int var2 = var0.getInt();
      IsoPlayer var3 = (IsoPlayer)IDToPlayerMap.get(var1);
      if (var3 != null) {
         LuaEventManager.triggerEvent("TradingUIUpdateState", var3, var2);
      }

   }

   public static void sendBuildingStashToDo(String var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)152, var1);
      var1.putUTF(var0);
      connection.endPacketImmediate();
   }

   public static void setServerStatisticEnable(boolean var0, int var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)212, var2);
      var2.putBoolean(var0);
      var2.putInt(var1);
      connection.endPacketImmediate();
      MPStatistic.clientStatisticEnable = var0;
   }

   public static boolean getServerStatisticEnable() {
      return MPStatistic.clientStatisticEnable;
   }

   public static void sendRequestInventory(IsoPlayer var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)153, var1);
      var1.putInt(IsoPlayer.getInstance().getOnlineID());
      var1.putInt(var0.getOnlineID());
      connection.endPacketImmediate();
   }

   private int sendInventoryPutItems(ByteBufferWriter var1, LinkedHashMap var2, long var3) {
      int var5 = var2.size();
      Iterator var6 = var2.keySet().iterator();

      while(var6.hasNext()) {
         InventoryItem var7 = (InventoryItem)var2.get(var6.next());
         var1.putUTF(var7.getModule());
         var1.putUTF(var7.getType());
         var1.putLong((long)var7.getID());
         var1.putLong(var3);
         var1.putBoolean(IsoPlayer.getInstance().isEquipped(var7));
         if (var7 instanceof DrainableComboItem) {
            var1.putFloat(((DrainableComboItem)var7).getUsedDelta());
         } else {
            var1.putFloat((float)var7.getCondition());
         }

         var1.putInt(var7.getCount());
         if (var7 instanceof DrainableComboItem) {
            var1.putUTF(Translator.getText("IGUI_ItemCat_Drainable"));
         } else {
            var1.putUTF(var7.getCategory());
         }

         var1.putUTF(var7.getContainer().getType());
         var1.putBoolean(var7.getWorker() != null && var7.getWorker().equals("inInv"));
         if (var7 instanceof InventoryContainer && ((InventoryContainer)var7).getItemContainer() != null && !((InventoryContainer)var7).getItemContainer().getItems().isEmpty()) {
            LinkedHashMap var8 = ((InventoryContainer)var7).getItemContainer().getItems4Admin();
            var5 += var8.size();
            this.sendInventoryPutItems(var1, var8, (long)var7.getID());
         }
      }

      return var5;
   }

   private void sendInventory(ByteBuffer var1) {
      int var2 = var1.getInt();
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)154, var3);
      var3.putInt(var2);
      int var4 = var3.bb.position();
      var3.putInt(0);
      LinkedHashMap var5 = IsoPlayer.getInstance().getInventory().getItems4Admin();
      int var6 = this.sendInventoryPutItems(var3, var5, -1L);
      int var7 = var3.bb.position();
      var3.bb.position(var4);
      var3.putInt(var6);
      var3.bb.position(var7);
      connection.endPacketImmediate();
   }

   private void receiveInventory(ByteBuffer var1) {
      int var2 = var1.getInt();
      KahluaTable var3 = LuaManager.platform.newTable();

      for(int var4 = 0; var4 < var2; ++var4) {
         KahluaTable var5 = LuaManager.platform.newTable();
         String var10000 = GameWindow.ReadStringUTF(var1);
         String var6 = var10000 + "." + GameWindow.ReadStringUTF(var1);
         long var7 = var1.getLong();
         long var9 = var1.getLong();
         boolean var11 = var1.get() == 1;
         float var12 = var1.getFloat();
         int var13 = var1.getInt();
         String var14 = GameWindow.ReadStringUTF(var1);
         String var15 = GameWindow.ReadStringUTF(var1);
         boolean var16 = var1.get() == 1;
         var5.rawset("fullType", var6);
         var5.rawset("itemId", var7);
         var5.rawset("isEquip", var11);
         var5.rawset("var", (double)Math.round((double)var12 * 100.0D) / 100.0D);
         var5.rawset("count", var13.makeConcatWithConstants<invokedynamic>(var13));
         var5.rawset("cat", var14);
         var5.rawset("parrentId", var9);
         var5.rawset("hasParrent", var9 != -1L);
         var5.rawset("container", var15);
         var5.rawset("inInv", var16);
         var3.rawset(var3.size() + 1, var5);
      }

      LuaEventManager.triggerEvent("MngInvReceiveItems", var3);
   }

   public static void sendGetItemInvMng(long var0) {
   }

   private void receiveSpawnRegion(ByteBuffer var1) {
      if (instance.ServerSpawnRegions == null) {
         instance.ServerSpawnRegions = LuaManager.platform.newTable();
      }

      int var2 = var1.getInt();
      KahluaTable var3 = LuaManager.platform.newTable();

      try {
         var3.load((ByteBuffer)var1, 184);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      instance.ServerSpawnRegions.rawset(var2, var3);
   }

   private static void receiveClimateManagerPacket(ByteBuffer var0) {
      ClimateManager var1 = ClimateManager.getInstance();
      if (var1 != null) {
         try {
            var1.receiveClimatePacket(var0, (UdpConnection)null);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

   }

   public static void sendIsoRegionDataRequest() {
      ByteBufferWriter var0 = connection.startPacket();
      PacketTypes.doPacket((short)202, var0);
      connection.endPacketImmediate();
   }

   public void sendSandboxOptionsToServer(SandboxOptions var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)31, var2);

      try {
         var1.save(var2.bb);
      } catch (IOException var7) {
         ExceptionLogger.logException(var7);
      } finally {
         connection.endPacket();
      }

   }

   private void receiveSandboxOptions(ByteBuffer var1) {
      try {
         SandboxOptions.instance.load(var1);
         SandboxOptions.instance.applySettings();
         SandboxOptions.instance.toLua();
      } catch (Exception var3) {
         ExceptionLogger.logException(var3);
      }

   }

   private void receiveChunkObjectState(ByteBuffer var1) {
      short var2 = var1.getShort();
      short var3 = var1.getShort();
      IsoChunk var4 = IsoWorld.instance.CurrentCell.getChunk(var2, var3);
      if (var4 != null) {
         try {
            var4.loadObjectState(var1);
         } catch (Throwable var6) {
            ExceptionLogger.logException(var6);
         }

      }
   }

   public static void sendZombieHelmetFall(IsoPlayer var0, IsoGameCharacter var1, InventoryItem var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)174, var3);
      var3.putByte((byte)var0.PlayerIndex);
      var3.putShort((short)var1.getOnlineID());
      var3.putUTF(var2.getType());
      connection.endPacketImmediate();
   }

   private void receiveZombieHelmetFalling(ByteBuffer var1) {
      short var2 = var1.getShort();
      IsoZombie var3 = (IsoZombie)IDToZombieMap.get(var2);
      String var4 = GameWindow.ReadString(var1);
      if (var3 != null && !StringUtils.isNullOrEmpty(var4)) {
         var3.helmetFall(true, var4);
      }
   }

   public static void receiveHitVehicle(ByteBuffer var0) {
      HitPacket.HitVehicle var1 = new HitPacket.HitVehicle();
      var1.parse(var0);
      if (var1.check()) {
         DebugLog.log(DebugType.Multiplayer, "HitVehicle receive: " + var1.getDescription());
         if (var1.targetType == 1) {
            ((IsoZombie)var1.target).networkAI.hitVehicle = var1;
         }

      }
   }

   public static void sendHitVehicle(IsoGameCharacter var0, IsoGameCharacter var1, BaseVehicle var2, float var3, float var4, float var5, float var6) {
      ByteBufferWriter var7 = connection.startPacket();
      PacketTypes.doPacket((short)213, var7);

      try {
         HitPacket.HitVehicle var8 = new HitPacket.HitVehicle();
         var8.set(var0, var1, var2, var3, var4, var5, var6);
         var8.write(var7);
         connection.endPacketImmediate();
         DebugLog.log(DebugType.Multiplayer, "HitVehicle send: " + var8.getDescription());
      } catch (Exception var9) {
         connection.cancelPacket();
         DebugLog.log("HitVehicle send: failed");
         var9.printStackTrace();
      }

   }

   public static void sendGotHitByVehicle(IsoPlayer var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)176, var1);
      var1.putByte((byte)var0.PlayerIndex);
      var1.putByte((byte)(var0.m_bKnockedDown ? 1 : 0));
      connection.endPacketImmediate();
   }

   private void receiveChrHitByVehicle(ByteBuffer var1) {
      short var2 = var1.getShort();
      boolean var3 = var1.get() == 1;
      IsoPlayer var4 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var4 != null && !var4.isLocalPlayer()) {
         var4.setHitReaction("HitReaction");
         var4.setM_bKnockedDown(var3);
         var4.reportEvent("washit");
      }
   }

   public static void sendPerks(IsoPlayer var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)177, var1);
      var1.putByte((byte)var0.PlayerIndex);
      var1.putInt(var0.getPerkLevel(PerkFactory.Perks.Sneak));
      var1.putInt(var0.getPerkLevel(PerkFactory.Perks.Strength));
      var1.putInt(var0.getPerkLevel(PerkFactory.Perks.Fitness));
      connection.endPacketImmediate();
   }

   private void receivePerks(ByteBuffer var1) {
      short var2 = var1.getShort();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      IsoPlayer var6 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var6 != null && !var6.isLocalPlayer()) {
         var6.remoteSneakLvl = var3;
         var6.remoteStrLvl = var4;
         var6.remoteFitLvl = var5;
      }
   }

   public static void sendWeight(IsoPlayer var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)178, var1);
      var1.putByte((byte)var0.PlayerIndex);
      var1.putFloat(var0.getNutrition().getWeight());
      connection.endPacketImmediate();
   }

   private void receiveWeight(ByteBuffer var1) {
      short var2 = var1.getShort();
      float var3 = var1.getFloat();
      IsoPlayer var4 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var4 != null && !var4.isLocalPlayer()) {
         var4.getNutrition().setWeight(var3);
      }
   }

   public static void sendInjuries(IsoPlayer var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)179, var1);
      var1.putByte((byte)var0.PlayerIndex);
      var1.putFloat(var0.getVariableFloat("WalkSpeed", 1.0F));
      var1.putFloat(var0.getVariableFloat("WalkInjury", 0.0F));
      connection.endPacketImmediate();
   }

   private void receiveInjuries(ByteBuffer var1) {
      short var2 = var1.getShort();
      float var3 = var1.getFloat();
      float var4 = var1.getFloat();
      IsoPlayer var5 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var5 != null && !var5.isLocalPlayer()) {
         var5.setVariable("WalkSpeed", var3);
         var5.setVariable("WalkInjury", var4);
      }
   }

   public static void sendHitReactionFromZombie(IsoPlayer var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)180, var1);
      var1.putByte((byte)var0.PlayerIndex);
      var1.putUTF(var0.getHitReaction());
      connection.endPacketImmediate();
   }

   private void receiveHitReactionFromZombie(ByteBuffer var1) {
      short var2 = var1.getShort();
      String var3 = GameWindow.ReadStringUTF(var1);
      IsoPlayer var4 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var4 != null && !var4.isLocalPlayer()) {
         var4.setVariable("hitpvp", false);
         var4.setHitReaction(var3);
         var4.reportEvent("washit");
      }
   }

   private static void receiveGlobalModData(ByteBuffer var0) {
      GlobalModData.instance.receive(var0);
   }

   public static void sendSafehouseInvite(SafeHouse var0, IsoPlayer var1, String var2) {
      ByteBufferWriter var3 = connection.startPacket();
      PacketTypes.doPacket((short)193, var3);
      var3.putUTF(var0.getTitle());
      var3.putUTF(var1.getUsername());
      var3.putUTF(var2);
      var3.putInt(var0.getX());
      var3.putInt(var0.getY());
      var3.putInt(var0.getW());
      var3.putInt(var0.getH());
      connection.endPacketImmediate();
   }

   private void receiveSafehouseInvite(ByteBuffer var1) {
      String var2 = GameWindow.ReadString(var1);
      String var3 = GameWindow.ReadString(var1);
      int var4 = var1.getInt();
      int var5 = var1.getInt();
      int var6 = var1.getInt();
      int var7 = var1.getInt();
      SafeHouse var8 = SafeHouse.getSafeHouse(var4, var5, var6, var7);
      LuaEventManager.triggerEvent("ReceiveSafehouseInvite", var8, var3);
   }

   public static void acceptSafehouseInvite(SafeHouse var0, String var1) {
      ByteBufferWriter var2 = connection.startPacket();
      PacketTypes.doPacket((short)194, var2);
      var2.putUTF(var0.getTitle());
      var2.putUTF(var1);
      var2.putUTF(username);
      var2.putInt(var0.getX());
      var2.putInt(var0.getY());
      var2.putInt(var0.getW());
      var2.putInt(var0.getH());
      connection.endPacketImmediate();
   }

   private void AcceptedSafehouseInvite(ByteBuffer var1) {
      String var2 = GameWindow.ReadString(var1);
      String var3 = GameWindow.ReadString(var1);
      String var4 = GameWindow.ReadString(var1);
      int var5 = var1.getInt();
      int var6 = var1.getInt();
      int var7 = var1.getInt();
      int var8 = var1.getInt();
      SafeHouse var9 = SafeHouse.getSafeHouse(var5, var6, var7, var8);
      if (var9 != null) {
         var9.addPlayer(var4);
      }

      LuaEventManager.triggerEvent("AcceptedSafehouseInvite", var9.getTitle(), var3);
   }

   public static void sendEquippedRadioFreq(IsoPlayer var0) {
      ByteBufferWriter var1 = connection.startPacket();
      PacketTypes.doPacket((short)181, var1);
      var1.putByte((byte)var0.PlayerIndex);
      var1.putInt(var0.invRadioFreq.size());

      for(int var2 = 0; var2 < var0.invRadioFreq.size(); ++var2) {
         var1.putInt((Integer)var0.invRadioFreq.get(var2));
      }

      connection.endPacketImmediate();
   }

   public void syncEquippedRadioFreq(ByteBuffer var1) {
      short var2 = var1.getShort();
      int var3 = var1.getInt();
      IsoPlayer var4 = (IsoPlayer)IDToPlayerMap.get(Integer.valueOf(var2));
      if (var4 != null) {
         var4.invRadioFreq.clear();

         int var5;
         for(var5 = 0; var5 < var3; ++var5) {
            var4.invRadioFreq.add(var1.getInt());
         }

         System.out.println("SYNC EQUIPPED RADIO FOR PLAYER: " + var4.getDisplayName());

         for(var5 = 0; var5 < var4.invRadioFreq.size(); ++var5) {
            System.out.println(var4.invRadioFreq.get(var5));
         }
      }

   }

   static {
      port = GameServer.DEFAULT_PORT;
      checksum = "";
      checksumValid = false;
      pingsList = new ArrayList();
      loadedCells = new ClientServerMap[4];
      isPaused = false;
      positions = new HashMap(ServerOptions.getInstance().MaxPlayers.getValue());
      MainLoopNetData = new ArrayList();
      LoadingMainLoopNetData = new ArrayList();
      DelayedCoopNetData = new ArrayList();
      ServerPredictedAhead = 0.0F;
      IDToPlayerMap = new HashMap();
      IDToZombieMap = new TShortObjectHashMap();
      askPing = false;
      startAuth = null;
      poisonousBerry = null;
      poisonousMushroom = null;
   }

   private static enum RequestState {
      Start,
      RequestDescriptors,
      ReceivedDescriptors,
      RequestMetaGrid,
      ReceivedMetaGrid,
      RequestMapZone,
      ReceivedMapZone,
      RequestPlayerZombieDescriptors,
      ReceivedPlayerZombieDescriptors,
      Complete;

      // $FF: synthetic method
      private static GameClient.RequestState[] $values() {
         return new GameClient.RequestState[]{Start, RequestDescriptors, ReceivedDescriptors, RequestMetaGrid, ReceivedMetaGrid, RequestMapZone, ReceivedMapZone, RequestPlayerZombieDescriptors, ReceivedPlayerZombieDescriptors, Complete};
      }
   }
}
