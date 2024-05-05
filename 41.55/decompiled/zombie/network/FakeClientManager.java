package zombie.network;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.CRC32;
import org.json.JSONArray;
import org.json.JSONObject;
import zombie.characters.NetworkCharacter;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.secure.PZcrypt;
import zombie.core.utils.UpdateLimit;
import zombie.core.znet.ZNet;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;

public class FakeClientManager {
   private static final int SERVER_PORT = 16261;
   private static final int CLIENT_PORT = 17500;
   private static final String CLIENT_ADDRESS = "0.0.0.0";
   private static final String versionNumber = Core.getInstance().getVersionNumber();
   private static final DateFormat logDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
   private static int logLevel = 0;

   private static void sleep(long var0) {
      try {
         Thread.sleep(var0);
      } catch (InterruptedException var3) {
         var3.printStackTrace();
      }

   }

   private static HashMap load(String var0) {
      HashMap var1 = new HashMap();

      try {
         String var2 = new String(Files.readAllBytes(Paths.get(var0)));
         JSONObject var3 = new JSONObject(var2);
         FakeClientManager.Movement.version = var3.getString("version");
         JSONObject var4 = var3.getJSONObject("config");
         JSONObject var5 = var4.getJSONObject("client");
         JSONObject var6 = var5.getJSONObject("connection");
         FakeClientManager.Client.connectionInterval = var6.getLong("interval");
         FakeClientManager.Client.connectionTimeout = var6.getLong("timeout");
         FakeClientManager.Client.connectionDelay = var6.getLong("delay");
         JSONObject var7 = var5.getJSONObject("statistics");
         FakeClientManager.Client.statisticsPeriod = var7.getInt("period");
         FakeClientManager.Client.statisticsClientID = Math.max(var7.getInt("id"), -1);
         var6 = var4.getJSONObject("player");
         FakeClientManager.Player.fps = var6.getInt("fps");
         FakeClientManager.Player.predictInterval = var6.getInt("predict");
         var7 = var4.getJSONObject("movement");
         FakeClientManager.Movement.defaultRadius = var7.getInt("radius");
         JSONObject var8 = var7.getJSONObject("motion");
         FakeClientManager.Movement.aimSpeed = var8.getInt("aim");
         FakeClientManager.Movement.sneakSpeed = var8.getInt("sneak");
         FakeClientManager.Movement.sneakRunSpeed = var8.getInt("sneakrun");
         FakeClientManager.Movement.walkSpeed = var8.getInt("walk");
         FakeClientManager.Movement.runSpeed = var8.getInt("run");
         FakeClientManager.Movement.sprintSpeed = var8.getInt("sprint");
         JSONObject var9 = var8.getJSONObject("pedestrian");
         FakeClientManager.Movement.pedestrianSpeedMin = var9.getInt("min");
         FakeClientManager.Movement.pedestrianSpeedMax = var9.getInt("max");
         JSONObject var10 = var8.getJSONObject("vehicle");
         FakeClientManager.Movement.vehicleSpeedMin = var10.getInt("min");
         FakeClientManager.Movement.vehicleSpeedMax = var10.getInt("max");
         JSONArray var34 = var3.getJSONArray("movements");

         for(int var35 = 0; var35 < var34.length(); ++var35) {
            var7 = var34.getJSONObject(var35);
            int var36 = var7.getInt("id");
            String var37 = null;
            if (var7.has("description")) {
               var37 = var7.getString("description");
            }

            int var38 = (int)Math.round(Math.random() * 6000.0D + 6000.0D);
            int var11 = (int)Math.round(Math.random() * 6000.0D + 6000.0D);
            if (var7.has("spawn")) {
               JSONObject var12 = var7.getJSONObject("spawn");
               var38 = var12.getInt("x");
               var11 = var12.getInt("y");
            }

            FakeClientManager.Movement.Motion var39 = Math.random() > 0.800000011920929D ? FakeClientManager.Movement.Motion.Vehicle : FakeClientManager.Movement.Motion.Pedestrian;
            if (var7.has("motion")) {
               var39 = FakeClientManager.Movement.Motion.valueOf(var7.getString("motion"));
            }

            int var13 = 0;
            if (var7.has("speed")) {
               var13 = var7.getInt("speed");
            } else {
               switch(var39) {
               case Aim:
                  var13 = FakeClientManager.Movement.aimSpeed;
                  break;
               case Sneak:
                  var13 = FakeClientManager.Movement.sneakSpeed;
                  break;
               case SneakRun:
                  var13 = FakeClientManager.Movement.sneakRunSpeed;
                  break;
               case Run:
                  var13 = FakeClientManager.Movement.runSpeed;
                  break;
               case Sprint:
                  var13 = FakeClientManager.Movement.sprintSpeed;
                  break;
               case Walk:
                  var13 = FakeClientManager.Movement.walkSpeed;
                  break;
               case Pedestrian:
                  var13 = (int)Math.round(Math.random() * (double)(FakeClientManager.Movement.pedestrianSpeedMax - FakeClientManager.Movement.pedestrianSpeedMin) + (double)FakeClientManager.Movement.pedestrianSpeedMin);
                  break;
               case Vehicle:
                  var13 = (int)Math.round(Math.random() * (double)(FakeClientManager.Movement.vehicleSpeedMax - FakeClientManager.Movement.vehicleSpeedMin) + (double)FakeClientManager.Movement.vehicleSpeedMin);
               }
            }

            FakeClientManager.Movement.Type var14 = FakeClientManager.Movement.Type.Line;
            if (var7.has("type")) {
               var14 = FakeClientManager.Movement.Type.valueOf(var7.getString("type"));
            }

            int var15 = FakeClientManager.Movement.defaultRadius;
            if (var7.has("radius")) {
               var15 = var7.getInt("radius");
            }

            IsoDirections var16 = IsoDirections.fromIndex((int)Math.round(Math.random() * 7.0D));
            if (var7.has("direction")) {
               var16 = IsoDirections.valueOf(var7.getString("direction"));
            }

            boolean var17 = false;
            if (var7.has("ghost")) {
               var17 = var7.getBoolean("ghost");
            }

            long var18 = (long)var36 * FakeClientManager.Client.connectionInterval;
            if (var7.has("connect")) {
               var18 = var7.getLong("connect");
            }

            long var20 = 0L;
            if (var7.has("disconnect")) {
               var20 = var7.getLong("disconnect");
            }

            long var22 = 0L;
            if (var7.has("reconnect")) {
               var22 = var7.getLong("reconnect");
            }

            long var24 = 0L;
            if (var7.has("teleport")) {
               var24 = var7.getLong("teleport");
            }

            int var26 = (int)Math.round(Math.random() * 6000.0D + 6000.0D);
            int var27 = (int)Math.round(Math.random() * 6000.0D + 6000.0D);
            if (var7.has("destination")) {
               JSONObject var28 = var7.getJSONObject("destination");
               var26 = var28.getInt("x");
               var27 = var28.getInt("y");
            }

            FakeClientManager.Movement var40 = new FakeClientManager.Movement(var36, var37, var38, var11, var39, var13, var14, var15, var26, var27, var16, var17, var18, var20, var22, var24);
            if (var1.containsKey(var36)) {
               error(var36, String.format("Client %d already exists", var40.id));
            } else {
               var1.put(var36, var40);
            }
         }

         return var1;
      } catch (Exception var32) {
         error(-1, "Scenarios file load failed");
         var32.printStackTrace();
         return var1;
      } finally {
         ;
      }
   }

   private static void error(int var0, String var1) {
      System.out.print(String.format("%5s : %s , [%2d] > %s\n", "ERROR", logDateFormat.format(Calendar.getInstance().getTime()), var0, var1));
   }

   private static void info(int var0, String var1) {
      if (logLevel >= 0) {
         System.out.print(String.format("%5s : %s , [%2d] > %s\n", "INFO", logDateFormat.format(Calendar.getInstance().getTime()), var0, var1));
      }

   }

   private static void log(int var0, String var1) {
      if (logLevel >= 1) {
         System.out.print(String.format("%5s : %s , [%2d] > %s\n", "LOG", logDateFormat.format(Calendar.getInstance().getTime()), var0, var1));
      }

   }

   private static void trace(int var0, String var1) {
      if (logLevel >= 2) {
         System.out.print(String.format("%5s : %s , [%2d] > %s\n", "TRACE", logDateFormat.format(Calendar.getInstance().getTime()), var0, var1));
      }

   }

   public static void main(String[] var0) {
      String var1 = null;
      int var2 = -1;

      for(int var3 = 0; var3 < var0.length; ++var3) {
         if (var0[var3].startsWith("-scenarios=")) {
            var1 = var0[var3].replace("-scenarios=", "").trim();
         } else if (var0[var3].startsWith("-id=")) {
            var2 = Integer.parseInt(var0[var3].replace("-id=", "").trim());
         }
      }

      if (var1 == null || var1.isBlank()) {
         error(-1, "Invalid scenarios file name");
         System.exit(0);
      }

      Rand.init();
      System.loadLibrary("RakNet64");
      System.loadLibrary("ZNetNoSteam64");

      try {
         String var11 = System.getProperty("zomboid.znetlog");
         if (var11 != null) {
            logLevel = Integer.parseInt(var11);
            ZNet.init();
            ZNet.setLogLevel(logLevel);
         }
      } catch (NumberFormatException var10) {
         error(-1, "Invalid log arguments");
      }

      DebugLog.disableLog(DebugType.General);
      HashMap var12 = load(var1);
      FakeClientManager.Network var4;
      int var5;
      if (var2 != -1) {
         var5 = 17500 + var2;
         var4 = new FakeClientManager.Network(var12.size(), var5);
      } else {
         var5 = 17500;
         var4 = new FakeClientManager.Network(var12.size(), var5);
      }

      if (var4.isStarted()) {
         HashSet var6 = new HashSet();
         int var7 = 0;
         if (var2 != -1) {
            FakeClientManager.Movement var13 = (FakeClientManager.Movement)var12.get(var2);
            if (var13 != null) {
               var6.add(new FakeClientManager.Player(var13, var4, var7, var5));
            } else {
               error(var2, "Client movement not found");
            }
         } else {
            Iterator var8 = var12.values().iterator();

            while(var8.hasNext()) {
               FakeClientManager.Movement var9 = (FakeClientManager.Movement)var8.next();
               var6.add(new FakeClientManager.Player(var9, var4, var7++, var5));
            }
         }

         while(!var6.isEmpty()) {
            sleep(1000L);
         }
      }

   }

   private static class Movement {
      static String version;
      static int defaultRadius = 150;
      static int aimSpeed = 4;
      static int sneakSpeed = 6;
      static int walkSpeed = 7;
      static int sneakRunSpeed = 10;
      static int runSpeed = 13;
      static int sprintSpeed = 19;
      static int pedestrianSpeedMin = 5;
      static int pedestrianSpeedMax = 20;
      static int vehicleSpeedMin = 40;
      static int vehicleSpeedMax = 80;
      final int id;
      final String description;
      final Vector2 spawn;
      final FakeClientManager.Movement.Motion motion;
      final float speed;
      final FakeClientManager.Movement.Type type;
      final int radius;
      final IsoDirections direction;
      final Vector2 destination;
      final boolean ghost;
      final long connectDelay;
      final long disconnectDelay;
      final long reconnectDelay;
      final long teleportDelay;
      long timestamp;

      public Movement(int var1, String var2, int var3, int var4, FakeClientManager.Movement.Motion var5, int var6, FakeClientManager.Movement.Type var7, int var8, int var9, int var10, IsoDirections var11, boolean var12, long var13, long var15, long var17, long var19) {
         this.id = var1;
         this.description = var2;
         this.spawn = new Vector2((float)var3, (float)var4);
         this.motion = var5;
         this.speed = (float)var6;
         this.type = var7;
         this.radius = var8;
         this.direction = var11;
         this.destination = new Vector2((float)var9, (float)var10);
         this.ghost = var12;
         this.connectDelay = var13;
         this.disconnectDelay = var15;
         this.reconnectDelay = var17;
         this.teleportDelay = var19;
      }

      public void connect(int var1) {
         long var2 = System.currentTimeMillis();
         if (this.disconnectDelay != 0L) {
            FakeClientManager.info(this.id, String.format("Player %3d connect in %.3fs, disconnect in %.3fs", var1, (float)(var2 - this.timestamp) / 1000.0F, (float)this.disconnectDelay / 1000.0F));
         } else {
            FakeClientManager.info(this.id, String.format("Player %3d connect in %.3fs", var1, (float)(var2 - this.timestamp) / 1000.0F));
         }

         this.timestamp = var2;
      }

      public void disconnect(int var1) {
         long var2 = System.currentTimeMillis();
         if (this.reconnectDelay != 0L) {
            FakeClientManager.info(this.id, String.format("Player %3d disconnect in %.3fs, reconnect in %.3fs", var1, (float)(var2 - this.timestamp) / 1000.0F, (float)this.reconnectDelay / 1000.0F));
         } else {
            FakeClientManager.info(this.id, String.format("Player %3d disconnect in %.3fs", var1, (float)(var2 - this.timestamp) / 1000.0F));
         }

         this.timestamp = var2;
      }

      public boolean doTeleport() {
         return this.teleportDelay != 0L;
      }

      public boolean doDisconnect() {
         return this.disconnectDelay != 0L;
      }

      public boolean checkDisconnect() {
         return System.currentTimeMillis() - this.timestamp > this.disconnectDelay;
      }

      public boolean doReconnect() {
         return this.reconnectDelay != 0L;
      }

      public boolean checkReconnect() {
         return System.currentTimeMillis() - this.timestamp > this.reconnectDelay;
      }

      private static enum Motion {
         Aim,
         Sneak,
         Walk,
         SneakRun,
         Run,
         Sprint,
         Pedestrian,
         Vehicle;

         // $FF: synthetic method
         private static FakeClientManager.Movement.Motion[] $values() {
            return new FakeClientManager.Movement.Motion[]{Aim, Sneak, Walk, SneakRun, Run, Sprint, Pedestrian, Vehicle};
         }
      }

      private static enum Type {
         Line,
         Circle;

         // $FF: synthetic method
         private static FakeClientManager.Movement.Type[] $values() {
            return new FakeClientManager.Movement.Type[]{Line, Circle};
         }
      }
   }

   private static class Client {
      private static long connectionInterval = 1500L;
      private static long connectionTimeout = 10000L;
      private static long connectionDelay = 15000L;
      private static int statisticsClientID = -1;
      private static int statisticsPeriod = 1;
      private static long serverTimeShift = 0L;
      private static boolean serverTimeShiftIsSet = false;
      private final HashMap requests = new HashMap();
      private final FakeClientManager.Player player;
      private final FakeClientManager.Network network;
      private final int connectionIndex;
      private final int port;
      private long connectionGUID = -1L;
      private int requestId = 0;
      private long stateTime;
      private FakeClientManager.Client.State state;
      private String host;

      private Client(FakeClientManager.Player var1, FakeClientManager.Network var2, int var3, int var4) {
         this.connectionIndex = var3;
         this.network = var2;
         this.player = var1;
         this.port = var4;

         try {
            this.host = InetAddress.getByName("127.0." + var3 + ".1").getHostAddress();
            this.state = FakeClientManager.Client.State.CONNECT;
            Thread var5 = new Thread(ThreadGroups.Workers, this::updateThread, var1.username);
            var5.setDaemon(true);
            var5.start();
         } catch (UnknownHostException var6) {
            this.state = FakeClientManager.Client.State.QUIT;
            var6.printStackTrace();
         }

      }

      private void updateThread() {
         FakeClientManager.info(this.player.movement.id, String.format("Start client (%d) %s:%d => %s:%d / \"%s\"", this.connectionIndex, "0.0.0.0", this.port, this.host, 16261, this.player.movement.description));
         FakeClientManager.sleep(this.player.movement.connectDelay);
         switch(this.player.movement.type) {
         case Circle:
            this.player.circleMovement();
            break;
         case Line:
            this.player.lineMovement();
         }

         while(this.state != FakeClientManager.Client.State.QUIT) {
            this.update();
            FakeClientManager.sleep(1L);
         }

         FakeClientManager.info(this.player.movement.id, String.format("Stop client (%d) %s:%d => %s:%d / \"%s\"", this.connectionIndex, "0.0.0.0", this.port, this.host, 16261, this.player.movement.description));
      }

      private void updateTime() {
         this.stateTime = System.currentTimeMillis();
      }

      private long getServerTime() {
         return serverTimeShiftIsSet ? System.nanoTime() + serverTimeShift : 0L;
      }

      private boolean checkConnectionTimeout() {
         return System.currentTimeMillis() - this.stateTime > connectionTimeout;
      }

      private boolean checkConnectionDelay() {
         return System.currentTimeMillis() - this.stateTime > connectionDelay;
      }

      private void changeState(FakeClientManager.Client.State var1) {
         this.updateTime();
         FakeClientManager.log(this.player.movement.id, String.format("%s >> %s", this.state, var1));
         if (FakeClientManager.Client.State.RUN.equals(var1)) {
            this.player.movement.connect(this.player.OnlineID);
            if (this.player.teleportLimiter == null) {
               this.player.teleportLimiter = new UpdateLimit(this.player.movement.teleportDelay);
            }

            if (this.player.movement.id == statisticsClientID) {
               this.sendTimeSync();
               this.sendInjuries();
               this.sendStatisticsEnable(statisticsPeriod);
            }
         } else if (FakeClientManager.Client.State.DISCONNECT.equals(var1) && !FakeClientManager.Client.State.DISCONNECT.equals(this.state)) {
            this.player.movement.disconnect(this.player.OnlineID);
         }

         this.state = var1;
      }

      private void update() {
         switch(this.state) {
         case CONNECT:
            this.player.movement.timestamp = System.currentTimeMillis();
            this.network.connect(this.player.movement.id, this.host);
            this.changeState(FakeClientManager.Client.State.WAIT);
            break;
         case LOGIN:
            this.sendPlayerLogin();
            this.changeState(FakeClientManager.Client.State.WAIT);
            break;
         case PLAYER_CONNECT:
            this.sendPlayerConnect();
            this.changeState(FakeClientManager.Client.State.WAIT);
            break;
         case PLAYER_EXTRA_INFO:
            this.sendPlayerExtraInfo(this.player.movement.ghost, this.player.movement.id == statisticsClientID);
            this.changeState(FakeClientManager.Client.State.WAIT);
            break;
         case LOAD:
            this.requestId = 0;
            this.requests.clear();
            this.requestFullUpdate();
            this.requestLargeAreaZip();
            this.changeState(FakeClientManager.Client.State.WAIT);
            break;
         case RUN:
            if (this.player.movement.doDisconnect() && this.player.movement.checkDisconnect()) {
               this.changeState(FakeClientManager.Client.State.DISCONNECT);
            } else {
               this.player.run();
            }
            break;
         case WAIT:
            if (this.checkConnectionTimeout()) {
               this.changeState(FakeClientManager.Client.State.DISCONNECT);
            }
            break;
         case DISCONNECT:
            if (this.network.isConnected()) {
               this.player.movement.timestamp = System.currentTimeMillis();
               this.network.disconnect(this.connectionGUID, this.player.movement.id, this.host);
            }

            if (this.player.movement.doReconnect() && this.player.movement.checkReconnect() || !this.player.movement.doReconnect() && this.checkConnectionDelay()) {
               this.changeState(FakeClientManager.Client.State.CONNECT);
            }
         case QUIT:
         }

      }

      private void receive(short var1, ByteBuffer var2) {
         FakeClientManager.Network.logUserPacket(this.player.movement.id, var1);
         switch(var1) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 7:
         case 8:
         case 9:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 20:
         case 22:
         case 23:
         case 24:
         case 25:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
         case 137:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 144:
         case 145:
         case 146:
         case 147:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 159:
         case 161:
         case 162:
         case 163:
         case 164:
         case 165:
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
         case 185:
         case 186:
         case 187:
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
         case 200:
         case 201:
         case 202:
         case 203:
         case 204:
         case 205:
         case 206:
         case 207:
         case 208:
         case 209:
         case 210:
         case 211:
         case 213:
         case 214:
         case 215:
         case 216:
         default:
            break;
         case 6:
            if (this.receivePlayerConnect(var2)) {
               this.changeState(FakeClientManager.Client.State.PLAYER_EXTRA_INFO);
            }
            break;
         case 10:
            this.receiveZombieUpdateInfo(var2);
            break;
         case 18:
            if (this.state == FakeClientManager.Client.State.WAIT && this.receiveChunkPart(var2)) {
               this.updateTime();
               if (this.allChunkPartsReceived()) {
                  this.changeState(FakeClientManager.Client.State.PLAYER_CONNECT);
               }
            }
            break;
         case 19:
            this.receiveSyncClock(var2);
            break;
         case 21:
            this.changeState(FakeClientManager.Client.State.LOAD);
            break;
         case 26:
            short var3 = var2.getShort();
            byte var4 = var2.get();
            short var5 = var2.getShort();
            if (var5 == this.player.OnlineID) {
               this.player.hit();
            }
            break;
         case 36:
            if (this.state == FakeClientManager.Client.State.WAIT && this.receiveNotRequired(var2)) {
               this.updateTime();
               if (this.allChunkPartsReceived()) {
                  this.changeState(FakeClientManager.Client.State.PLAYER_CONNECT);
               }
            }
            break;
         case 84:
            if (this.receivePlayerExtraInfo(var2)) {
               this.changeState(FakeClientManager.Client.State.RUN);
            }
            break;
         case 160:
            this.receiveTimeSync(var2);
            break;
         case 212:
            this.receiveStatistics(var2);
         }

         var2.clear();
      }

      private void doPacket(short var1, ByteBuffer var2) {
         var2.put((byte)-122);
         var2.putShort(var1);
      }

      private void putUTF(ByteBuffer var1, String var2) {
         if (var2 == null) {
            var1.putShort((short)0);
         } else {
            byte[] var3 = var2.getBytes();
            var1.putShort((short)var3.length);
            var1.put(var3);
         }

      }

      private void putBoolean(ByteBuffer var1, boolean var2) {
         var1.put((byte)(var2 ? 1 : 0));
      }

      private void sendPlayerLogin() {
         ByteBuffer var1 = this.network.startPacket();
         this.doPacket((short)2, var1);
         this.putUTF(var1, this.player.username);
         this.putUTF(var1, this.player.username);
         this.putUTF(var1, FakeClientManager.versionNumber);
         this.network.endPacketImmediate(this.connectionGUID);
      }

      private void sendPlayerConnect() {
         ByteBuffer var1 = this.network.startPacket();
         this.doPacket((short)6, var1);
         this.writePlayerConnectData(var1);
         this.network.endPacketImmediate(this.connectionGUID);
      }

      private void writePlayerConnectData(ByteBuffer var1) {
         var1.put((byte)0);
         var1.put((byte)13);
         var1.putFloat(this.player.x);
         var1.putFloat(this.player.y);
         var1.putFloat(this.player.z);
         var1.putInt(0);
         this.putUTF(var1, this.player.username);
         this.putUTF(var1, this.player.username);
         this.putUTF(var1, this.player.isFemale == 0 ? "Kate" : "Male");
         var1.putInt(this.player.isFemale);
         this.putUTF(var1, "fireofficer");
         var1.putInt(0);
         var1.putInt(4);
         this.putUTF(var1, "Sprinting");
         var1.putInt(1);
         this.putUTF(var1, "Fitness");
         var1.putInt(6);
         this.putUTF(var1, "Strength");
         var1.putInt(6);
         this.putUTF(var1, "Axe");
         var1.putInt(1);
         var1.put((byte)0);
         var1.put((byte)0);
         var1.put((byte)((int)Math.round(Math.random() * 5.0D)));
         var1.put((byte)0);
         var1.put((byte)0);
         var1.put((byte)0);
         var1.put((byte)0);
         int var2 = this.player.clothes.size();
         var1.put((byte)var2);
         Iterator var3 = this.player.clothes.iterator();

         while(var3.hasNext()) {
            FakeClientManager.Player.Clothes var4 = (FakeClientManager.Player.Clothes)var3.next();
            var1.put(var4.flags);
            this.putUTF(var1, "Base." + var4.name);
            this.putUTF(var1, (String)null);
            this.putUTF(var1, var4.name);
            var1.put((byte)-1);
            var1.put((byte)-1);
            var1.put((byte)-1);
            var1.put(var4.text);
            var1.putFloat(0.0F);
            var1.put((byte)0);
            var1.put((byte)0);
            var1.put((byte)0);
            var1.put((byte)0);
            var1.put((byte)0);
            var1.put((byte)0);
         }

         this.putUTF(var1, "fake_str");
         var1.putShort((short)0);
         var1.putInt(2);
         this.putUTF(var1, "Fit");
         this.putUTF(var1, "Stout");
         var1.putFloat(0.0F);
         var1.putInt(0);
         var1.putInt(0);
         var1.putInt(4);
         this.putUTF(var1, "Sprinting");
         var1.putFloat(75.0F);
         this.putUTF(var1, "Fitness");
         var1.putFloat(67500.0F);
         this.putUTF(var1, "Strength");
         var1.putFloat(67500.0F);
         this.putUTF(var1, "Axe");
         var1.putFloat(75.0F);
         var1.putInt(4);
         this.putUTF(var1, "Sprinting");
         var1.putInt(1);
         this.putUTF(var1, "Fitness");
         var1.putInt(6);
         this.putUTF(var1, "Strength");
         var1.putInt(6);
         this.putUTF(var1, "Axe");
         var1.putInt(1);
         var1.putInt(0);
         this.putBoolean(var1, true);
         this.putUTF(var1, "fake");
         var1.putFloat(this.player.tagColor.r);
         var1.putFloat(this.player.tagColor.g);
         var1.putFloat(this.player.tagColor.b);
         var1.putInt(0);
         var1.putDouble(0.0D);
         var1.putInt(0);
         this.putUTF(var1, this.player.username);
         var1.putFloat(this.player.speakColor.r);
         var1.putFloat(this.player.speakColor.g);
         var1.putFloat(this.player.speakColor.b);
         this.putBoolean(var1, true);
         this.putBoolean(var1, false);
         var1.put((byte)0);
         var1.put((byte)0);
         var1.putInt(0);
         var1.putInt(0);
      }

      private void sendPlayerExtraInfo(boolean var1, boolean var2) {
         ByteBuffer var3 = this.network.startPacket();
         this.doPacket((short)84, var3);
         var3.putShort((short)this.player.OnlineID);
         this.putUTF(var3, var2 ? "admin" : "");
         var3.put((byte)0);
         var3.put((byte)(var1 ? 1 : 0));
         var3.put((byte)0);
         var3.put((byte)0);
         var3.put((byte)0);
         this.network.endPacketImmediate(this.connectionGUID);
      }

      private int getBooleanVariables() {
         int var1 = 0;
         if (this.player.movement.speed > 0.0F) {
            switch(this.player.movement.motion) {
            case Aim:
               var1 |= 64;
               break;
            case Sneak:
               var1 |= 1;
               break;
            case SneakRun:
               var1 |= 17;
               break;
            case Run:
               var1 |= 16;
               break;
            case Sprint:
               var1 |= 32;
            }

            var1 |= 17408;
         }

         return var1;
      }

      private void sendPlayer(NetworkCharacter.Transform var1, int var2) {
         ByteBuffer var3 = this.network.startPacket();
         this.doPacket((short)218, var3);
         var3.putShort((short)this.player.OnlineID);
         var3.putFloat(var1.position.x);
         var3.putFloat(var1.position.y);
         var3.put((byte)((int)this.player.z));
         var3.putInt(var1.time);
         var3.putFloat(var1.rotation.getDirection());
         var3.put((byte)0);
         var3.putShort((short)-1);
         var3.putShort((short)-1);
         var3.putInt(this.getBooleanVariables());
         var3.put((byte)((int)Math.min(this.player.movement.speed, 20.0F)));
         var3.putFloat(this.player.x);
         var3.putFloat(this.player.y);
         var3.put((byte)((int)this.player.z));
         var3.put((byte)IsoDirections.fromAngleActual(this.player.direction).index());
         var3.putInt(var2);
         var3.putShort((short)0);
         this.network.endPacketSuperHighUnreliable(this.connectionGUID);
      }

      private boolean receivePlayerConnect(ByteBuffer var1) {
         short var2 = var1.getShort();
         if (var2 == -1) {
            byte var3 = var1.get();
            var2 = var1.getShort();
            this.player.OnlineID = var2;
            return true;
         } else {
            return false;
         }
      }

      private boolean receivePlayerExtraInfo(ByteBuffer var1) {
         short var2 = var1.getShort();
         return var2 == this.player.OnlineID;
      }

      private void receiveZombieUpdateInfo(ByteBuffer var1) {
         short var2 = var1.getShort();

         for(short var3 = 0; var3 < var2; ++var3) {
            short var4 = var1.getShort();
            float var5 = var1.getFloat();
            float var6 = var1.getFloat();
            byte var7 = var1.get();
            int var8 = var1.getInt();
            short var9 = var1.getShort();
            int var10 = var1.getInt();
            byte var11 = var1.get();
            int var12 = var1.getInt();
            int var13 = var1.getInt();
            int var14 = var1.getInt();
            float var15 = var1.getFloat();
            float var16 = var1.getFloat();
            byte var17 = var1.get();
            byte var18 = var1.get();
         }

      }

      private boolean receiveChunkPart(ByteBuffer var1) {
         boolean var2 = false;
         int var3 = var1.getInt();
         int var4 = var1.getInt();
         int var5 = var1.getInt();
         int var6 = var1.getInt();
         int var7 = var1.getInt();
         int var8 = var1.getInt();
         if (this.requests.remove(var3) != null) {
            var2 = true;
         }

         return var2;
      }

      private boolean receiveNotRequired(ByteBuffer var1) {
         boolean var2 = false;
         int var3 = var1.getInt();

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var1.getInt();
            boolean var6 = var1.get() == 1;
            if (this.requests.remove(var5) != null) {
               var2 = true;
            }
         }

         return var2;
      }

      private boolean allChunkPartsReceived() {
         return this.requests.size() == 0;
      }

      private void addChunkRequest(int var1, int var2, int var3, int var4) {
         FakeClientManager.Client.Request var5 = new FakeClientManager.Client.Request(var1, var2, this.requestId);
         ++this.requestId;
         this.requests.put(var5.id, var5);
      }

      private void requestZipList() {
         ByteBuffer var1 = this.network.startPacket();
         this.doPacket((short)34, var1);
         var1.putInt(this.requests.size());
         Iterator var2 = this.requests.values().iterator();

         while(var2.hasNext()) {
            FakeClientManager.Client.Request var3 = (FakeClientManager.Client.Request)var2.next();
            var1.putInt(var3.id);
            var1.putInt(var3.wx);
            var1.putInt(var3.wy);
            var1.putLong(var3.crc);
         }

         this.network.endPacket(this.connectionGUID);
      }

      private void requestLargeAreaZip() {
         ByteBuffer var1 = this.network.startPacket();
         this.doPacket((short)24, var1);
         var1.putInt(this.player.WorldX);
         var1.putInt(this.player.WorldY);
         var1.putInt(13);
         this.network.endPacketImmediate(this.connectionGUID);
         int var2 = this.player.WorldX - 6 + 2;
         int var3 = this.player.WorldY - 6 + 2;
         int var4 = this.player.WorldX + 6 + 2;
         int var5 = this.player.WorldY + 6 + 2;

         for(int var6 = var3; var6 <= var5; ++var6) {
            for(int var7 = var2; var7 <= var4; ++var7) {
               FakeClientManager.Client.Request var8 = new FakeClientManager.Client.Request(var7, var6, this.requestId);
               ++this.requestId;
               this.requests.put(var8.id, var8);
            }
         }

         this.requestZipList();
      }

      private void requestFullUpdate() {
         ByteBuffer var1 = this.network.startPacket();
         this.doPacket((short)202, var1);
         this.network.endPacketImmediate(this.connectionGUID);
      }

      private void requestChunkObjectState() {
         Iterator var1 = this.requests.values().iterator();

         while(var1.hasNext()) {
            FakeClientManager.Client.Request var2 = (FakeClientManager.Client.Request)var1.next();
            ByteBuffer var3 = this.network.startPacket();
            this.doPacket((short)151, var3);
            var3.putShort((short)var2.wx);
            var3.putShort((short)var2.wy);
            this.network.endPacket(this.connectionGUID);
         }

      }

      private void requestChunks() {
         if (!this.requests.isEmpty()) {
            this.requestZipList();
            this.requestChunkObjectState();
            this.requests.clear();
         }

      }

      private void sendStatisticsEnable(int var1) {
         ByteBuffer var2 = this.network.startPacket();
         this.doPacket((short)212, var2);
         var2.put((byte)3);
         var2.putInt(var1);
         this.network.endPacketImmediate(this.connectionGUID);
      }

      private void receiveStatistics(ByteBuffer var1) {
         long var2 = var1.getLong();
         long var4 = var1.getLong();
         long var6 = var1.getLong();
         long var8 = var1.getLong();
         long var10 = var1.getLong();
         long var12 = var1.getLong();
         long var14 = var1.getLong();
         long var16 = var1.getLong();
         long var18 = var1.getLong();
         FakeClientManager.info(this.player.movement.id, String.format("ServerStats: con=[%2d] fps=[%2d] tps=[%2d] upt=[%4d-%4d/%4d], c1=[%d] c2=[%d] c3=[%d]", var12, var8, var10, var2, var4, var6, var14, var16, var18));
      }

      private void sendTimeSync() {
         ByteBuffer var1 = this.network.startPacket();
         this.doPacket((short)160, var1);
         long var2 = System.nanoTime();
         var1.putLong(var2);
         var1.putLong(0L);
         this.network.endPacketImmediate(this.connectionGUID);
      }

      private void receiveTimeSync(ByteBuffer var1) {
         long var2 = var1.getLong();
         long var4 = var1.getLong();
         long var6 = System.nanoTime();
         long var8 = var6 - var2;
         long var10 = var4 - var6 + var8 / 2L;
         long var12 = serverTimeShift;
         if (!serverTimeShiftIsSet) {
            serverTimeShift = var10;
         } else {
            serverTimeShift = (long)((float)serverTimeShift + (float)(var10 - serverTimeShift) * 0.05F);
         }

         long var14 = 10000000L;
         if (Math.abs(serverTimeShift - var12) > var14) {
            this.sendTimeSync();
         } else {
            serverTimeShiftIsSet = true;
         }

      }

      private void receiveSyncClock(ByteBuffer var1) {
         FakeClientManager.trace(this.player.movement.id, String.format("Player %3d sync clock", this.player.OnlineID));
      }

      private void sendInjuries() {
         ByteBuffer var1 = this.network.startPacket();
         this.doPacket((short)179, var1);
         var1.put((byte)0);
         var1.putFloat(1.0F);
         var1.putFloat(0.0F);
         this.network.endPacketImmediate(this.connectionGUID);
      }

      private static enum State {
         CONNECT,
         LOGIN,
         PLAYER_CONNECT,
         PLAYER_EXTRA_INFO,
         LOAD,
         RUN,
         WAIT,
         DISCONNECT,
         QUIT;

         // $FF: synthetic method
         private static FakeClientManager.Client.State[] $values() {
            return new FakeClientManager.Client.State[]{CONNECT, LOGIN, PLAYER_CONNECT, PLAYER_EXTRA_INFO, LOAD, RUN, WAIT, DISCONNECT, QUIT};
         }
      }

      private static final class Request {
         private final int id;
         private final int wx;
         private final int wy;
         private final long crc;

         private Request(int var1, int var2, int var3) {
            this.id = var3;
            this.wx = var1;
            this.wy = var2;
            CRC32 var4 = new CRC32();
            var4.reset();
            var4.update(String.format("map_%d_%d.bin", var1, var2).getBytes());
            this.crc = var4.getValue();
         }
      }
   }

   private static class Player {
      private static final int cellSize = 50;
      private static final int spawnMinX = 3550;
      private static final int spawnMaxX = 14450;
      private static final int spawnMinY = 5050;
      private static final int spawnMaxY = 12950;
      private static final int ChunkGridWidth = 13;
      private static final int ChunksPerWidth = 10;
      private static int fps = 60;
      private static int predictInterval = 1000;
      private final NetworkCharacter networkCharacter;
      private final UpdateLimit updateLimiter;
      private final UpdateLimit predictLimiter;
      private final UpdateLimit timeSyncLimiter;
      private final FakeClientManager.Client client;
      private final FakeClientManager.Movement movement;
      private final ArrayList clothes;
      private final String username;
      private final int isFemale;
      private final Color tagColor;
      private final Color speakColor;
      private UpdateLimit teleportLimiter;
      private int OnlineID;
      private float x;
      private float y;
      private final float z;
      private Vector2 direction;
      private int WorldX;
      private int WorldY;
      private float angle;

      private Player(FakeClientManager.Movement var1, FakeClientManager.Network var2, int var3, int var4) {
         this.username = String.format("Client%d", var1.id);
         this.tagColor = Colors.SkyBlue;
         this.speakColor = Colors.GetRandomColor();
         this.isFemale = (int)Math.round(Math.random());
         this.OnlineID = -1;
         this.clothes = new ArrayList();
         this.clothes.add(new FakeClientManager.Player.Clothes((byte)11, (byte)0, "Shirt_FormalWhite"));
         this.clothes.add(new FakeClientManager.Player.Clothes((byte)13, (byte)3, "Tie_Full"));
         this.clothes.add(new FakeClientManager.Player.Clothes((byte)11, (byte)0, "Socks_Ankle"));
         this.clothes.add(new FakeClientManager.Player.Clothes((byte)13, (byte)0, "Trousers_Suit"));
         this.clothes.add(new FakeClientManager.Player.Clothes((byte)13, (byte)0, "Suit_Jacket"));
         this.clothes.add(new FakeClientManager.Player.Clothes((byte)11, (byte)0, "Shoes_Black"));
         this.clothes.add(new FakeClientManager.Player.Clothes((byte)11, (byte)0, "Glasses_Sun"));
         this.WorldX = (int)this.x / 10;
         this.WorldY = (int)this.y / 10;
         this.movement = var1;
         this.z = 0.0F;
         this.angle = 0.0F;
         this.x = var1.spawn.x;
         this.y = var1.spawn.y;
         this.direction = var1.direction.ToVector();
         this.networkCharacter = new NetworkCharacter();
         this.client = new FakeClientManager.Client(this, var2, var3, var4);
         var2.createdClients.put(var3, this.client);
         this.updateLimiter = new UpdateLimit((long)(1000 / fps));
         this.predictLimiter = new UpdateLimit((long)((float)predictInterval * 0.6F));
         this.timeSyncLimiter = new UpdateLimit(10000L);
      }

      private float getDistance(float var1) {
         return var1 / 3.6F / (float)fps;
      }

      private void teleportMovement() {
         float var1 = this.movement.destination.x;
         float var2 = this.movement.destination.y;
         FakeClientManager.info(this.movement.id, String.format("Player %3d teleport (%9.3f,%9.3f) => (%9.3f,%9.3f) / %9.3f, next in %.3fs", this.OnlineID, this.x, this.y, var1, var2, Math.sqrt(Math.pow((double)(var1 - this.x), 2.0D) + Math.pow((double)(var2 - this.y), 2.0D)), (float)this.movement.teleportDelay / 1000.0F));
         this.x = var1;
         this.y = var2;
         this.angle = 0.0F;
         this.teleportLimiter.Reset(this.movement.teleportDelay);
      }

      private void lineMovement() {
         float var1 = this.getDistance(this.movement.speed);
         float var2 = this.x + var1 * this.direction.x;
         float var3 = this.y + var1 * this.direction.y;
         if (var2 <= 3550.0F || var2 >= 14450.0F || var3 <= 5050.0F || var3 >= 12950.0F) {
            if (var2 < 3550.0F) {
               var2 = 3550.0F;
            }

            if (var2 > 14450.0F) {
               var2 = 14450.0F;
            }

            if (var3 < 5050.0F) {
               var3 = 5050.0F;
            }

            if (var3 > 12950.0F) {
               var3 = 12950.0F;
            }

            int var4 = IsoDirections.fromAngleActual(this.direction).index();
            this.direction = IsoDirections.fromIndex(var4 + 3).ToVector();
         }

         this.x = var2;
         this.y = var3;
      }

      private void circleMovement() {
         this.angle = (this.angle + (float)(2.0D * Math.asin((double)(this.getDistance(this.movement.speed) / 2.0F / (float)this.movement.radius)))) % 360.0F;
         float var1 = this.movement.spawn.x + (float)((double)this.movement.radius * Math.sin((double)this.angle));
         float var2 = this.movement.spawn.y + (float)((double)this.movement.radius * Math.cos((double)this.angle));
         this.x = var1;
         this.y = var2;
      }

      private void checkRequestChunks() {
         int var1 = (int)this.x / 10;
         int var2 = (int)this.y / 10;
         int var3;
         if (Math.abs(var1 - this.WorldX) < 13 && Math.abs(var2 - this.WorldY) < 13) {
            if (var1 != this.WorldX) {
               if (var1 < this.WorldX) {
                  for(var3 = -6; var3 <= 6; ++var3) {
                     this.client.addChunkRequest(this.WorldX - 6, this.WorldY + var3, 0, var3 + 6);
                  }
               } else {
                  for(var3 = -6; var3 <= 6; ++var3) {
                     this.client.addChunkRequest(this.WorldX + 6, this.WorldY + var3, 12, var3 + 6);
                  }
               }
            } else if (var2 != this.WorldY) {
               if (var2 < this.WorldY) {
                  for(var3 = -6; var3 <= 6; ++var3) {
                     this.client.addChunkRequest(this.WorldX + var3, this.WorldY - 6, var3 + 6, 0);
                  }
               } else {
                  for(var3 = -6; var3 <= 6; ++var3) {
                     this.client.addChunkRequest(this.WorldX + var3, this.WorldY + 6, var3 + 6, 12);
                  }
               }
            }
         } else {
            var3 = this.WorldX - 6;
            int var4 = this.WorldY - 6;
            int var5 = this.WorldX + 6;
            int var6 = this.WorldY + 6;

            for(int var7 = var3; var7 <= var5; ++var7) {
               for(int var8 = var4; var8 <= var6; ++var8) {
                  this.client.addChunkRequest(var7, var8, var7 - var3, var8 - var4);
               }
            }
         }

         this.client.requestChunks();
         this.WorldX = var1;
         this.WorldY = var2;
      }

      private void hit() {
         FakeClientManager.info(this.movement.id, String.format("Player %3d hit", this.OnlineID));
      }

      private void run() {
         if (this.updateLimiter.Check()) {
            if (this.movement.doTeleport() && this.teleportLimiter.Check()) {
               this.teleportMovement();
            }

            switch(this.movement.type) {
            case Circle:
               this.circleMovement();
               break;
            case Line:
               this.lineMovement();
            }

            this.checkRequestChunks();
            int var1 = (int)(this.client.getServerTime() / 1000000L);
            this.networkCharacter.checkResetPlayer(var1);
            NetworkCharacter.Transform var2 = this.networkCharacter.predict(predictInterval, var1, this.x, this.y, this.direction.x, this.direction.y);
            if (this.predictLimiter.Check()) {
               this.client.sendPlayer(var2, var1);
            }

            if (this.timeSyncLimiter.Check()) {
               this.client.sendTimeSync();
            }
         }

      }

      private static class Clothes {
         private final byte flags;
         private final byte text;
         private final String name;

         Clothes(byte var1, byte var2, String var3) {
            this.flags = var1;
            this.text = var2;
            this.name = var3;
         }
      }
   }

   private static class Network {
      private final HashMap createdClients = new HashMap();
      private final HashMap connectedClients = new HashMap();
      private final ByteBuffer rb = ByteBuffer.allocate(1000000);
      private final ByteBuffer wb = ByteBuffer.allocate(1000000);
      private final RakNetPeerInterface peer = new RakNetPeerInterface();
      private final int started;
      private int connected = -1;
      private static final HashMap systemPacketTypeNames = new HashMap();
      private static final HashMap userPacketTypeNames = new HashMap();

      boolean isConnected() {
         return this.connected == 0;
      }

      boolean isStarted() {
         return this.started == 0;
      }

      private Network(int var1, int var2) {
         this.peer.Init(false);
         this.peer.SetMaximumIncomingConnections(0);
         this.peer.SetClientPort(var2);
         this.peer.SetOccasionalPing(true);
         this.started = this.peer.Startup(var1);
         if (this.started == 0) {
            Thread var3 = new Thread(ThreadGroups.Network, this::receiveThread, "PeerInterfaceReceive");
            var3.setDaemon(true);
            var3.start();
            FakeClientManager.log(-1, "Network start ok");
         } else {
            FakeClientManager.error(-1, String.format("Network start failed: %d", this.started));
         }

      }

      private void connect(int var1, String var2) {
         this.connected = this.peer.Connect(var2, 16261, PZcrypt.hash("", true));
         if (this.connected == 0) {
            FakeClientManager.log(var1, String.format("Client connected to %s:%d", var2, 16261));
         } else {
            FakeClientManager.error(var1, String.format("Client connection to %s:%d failed: %d", var2, 16261, this.connected));
         }

      }

      private void disconnect(long var1, int var3, String var4) {
         if (var1 != 0L) {
            this.peer.disconnect(var1);
            this.connected = -1;
         }

         if (this.connected == -1) {
            FakeClientManager.log(var3, String.format("Client disconnected from %s:%d", var4, 16261));
         } else {
            FakeClientManager.log(var3, String.format("Client disconnection from %s:%d failed: %d", var4, 16261, var1));
         }

      }

      private ByteBuffer startPacket() {
         this.wb.clear();
         return this.wb;
      }

      private void cancelPacket() {
         this.wb.clear();
      }

      private void endPacket(long var1) {
         this.wb.flip();
         this.peer.Send(this.wb, 1, 3, (byte)0, var1, false);
      }

      private void endPacketImmediate(long var1) {
         this.wb.flip();
         this.peer.Send(this.wb, 0, 3, (byte)0, var1, false);
      }

      private void endPacketSuperHighUnreliable(long var1) {
         this.wb.flip();
         this.peer.Send(this.wb, 0, 1, (byte)0, var1, false);
      }

      private void receiveThread() {
         while(true) {
            if (this.peer.Receive(this.rb)) {
               this.decode(this.rb);
            } else {
               FakeClientManager.sleep(1L);
            }
         }
      }

      private static void logUserPacket(int var0, short var1) {
         String var2 = (String)userPacketTypeNames.getOrDefault(var1, "unknown user packet");
         FakeClientManager.trace(var0, String.format("## %s", var2));
      }

      private static void logSystemPacket(int var0, int var1) {
         String var2 = (String)systemPacketTypeNames.getOrDefault(var1, "unknown system packet");
         FakeClientManager.trace(var0, String.format("# %s", var2));
      }

      private void decode(ByteBuffer var1) {
         int var2 = var1.get() & 255;
         int var3 = -1;
         long var4 = -1L;
         FakeClientManager.Client var6;
         switch(var2) {
         case 0:
         case 1:
         case 20:
         case 25:
         case 31:
         case 33:
         default:
            break;
         case 16:
            var3 = var1.get() & 255;
            var4 = this.peer.getGuidOfPacket();
            var6 = (FakeClientManager.Client)this.createdClients.get(var3);
            if (var6 != null) {
               var6.connectionGUID = var4;
               this.connectedClients.put(var4, var6);
               var6.changeState(FakeClientManager.Client.State.LOGIN);
            }

            FakeClientManager.log(-1, String.format("Connected clients: %d (connection index %d)", this.connectedClients.size(), var3));
            break;
         case 17:
         case 18:
         case 23:
         case 24:
         case 32:
            FakeClientManager.error(-1, "Connection failed: " + var2);
            break;
         case 19:
            var3 = var1.get() & 255;
         case 44:
         case 45:
            var4 = this.peer.getGuidOfPacket();
            break;
         case 21:
            var3 = var1.get() & 255;
            var4 = this.peer.getGuidOfPacket();
            var6 = (FakeClientManager.Client)this.connectedClients.get(var4);
            if (var6 != null) {
               this.connectedClients.remove(var4);
               var6.changeState(FakeClientManager.Client.State.DISCONNECT);
            }

            FakeClientManager.log(-1, String.format("Connected clients: %d (connection index %d)", this.connectedClients.size(), var3));
            break;
         case 22:
            var3 = var1.get() & 255;
            var6 = (FakeClientManager.Client)this.createdClients.get(var3);
            if (var6 != null) {
               var6.changeState(FakeClientManager.Client.State.DISCONNECT);
            }
            break;
         case 134:
            short var7 = var1.getShort();
            var4 = this.peer.getGuidOfPacket();
            var6 = (FakeClientManager.Client)this.connectedClients.get(var4);
            if (var6 != null) {
               var6.receive((short)var7, var1);
               var3 = var6.connectionIndex;
            }
         }

         logSystemPacket(var3, var2);
      }

      static {
         systemPacketTypeNames.put(22, "connection lost");
         systemPacketTypeNames.put(21, "disconnected");
         systemPacketTypeNames.put(23, "connection banned");
         systemPacketTypeNames.put(17, "connection failed");
         systemPacketTypeNames.put(20, "no free connections");
         systemPacketTypeNames.put(16, "connection accepted");
         systemPacketTypeNames.put(18, "already connected");
         systemPacketTypeNames.put(44, "voice request");
         systemPacketTypeNames.put(45, "voice reply");
         systemPacketTypeNames.put(25, "wrong protocol version");
         systemPacketTypeNames.put(0, "connected ping");
         systemPacketTypeNames.put(1, "unconnected ping");
         systemPacketTypeNames.put(33, "new remote connection");
         systemPacketTypeNames.put(31, "remote disconnection");
         systemPacketTypeNames.put(32, "remote connection lost");
         systemPacketTypeNames.put(24, "invalid password");
         systemPacketTypeNames.put(19, "new connection");
         systemPacketTypeNames.put(134, "user packet");
         Field[] var0 = PacketTypes.class.getFields();
         Field[] var1 = var0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Field var4 = var1[var3];
            if (var4.getType().equals(Short.TYPE) && Modifier.isStatic(var4.getModifiers())) {
               try {
                  userPacketTypeNames.put(var4.getShort((Object)null), var4.getName());
               } catch (IllegalAccessException var6) {
                  var6.printStackTrace();
               }
            }
         }

      }
   }
}
