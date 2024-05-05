package zombie.network;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.zip.CRC32;
import zombie.GameTime;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.ThreadGroups;
import zombie.core.raknet.RakNetPeerInterface;
import zombie.core.secure.PZcrypt;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;

public class FakeClient {
   private static final int PORT = 16261;
   private static final String IP = "localhost";
   private static final String PASSWORD = "";
   private static final String versionNumber = Core.getInstance().getVersionNumber();
   private static final Vector2 defaultSpawnPoint = new Vector2(9000.0F, 9000.0F);
   private static final int step = 300;
   private static final HashMap spawnPoints = new HashMap();
   private static final long stateWait = 60000L;
   private static int ID;
   private static long stateTime;
   private static long startTime;
   private static long connectionTime;
   private static boolean logNetwork;
   private static FakeClient.Player player;
   private static FakeClient.Networking networking;
   public static ArrayList startPositionsCircle = new ArrayList();
   public static Vector2[] startPositions;
   private static final DateFormat logDateFormat;
   private static final UpdateLimit logUpdateLimit;

   private static Vector2 getPlayerPositionCircle(float var0) {
      return new Vector2(3700.0F + (float)(10.0D * Math.sin((double)var0)), 5000.0F + (float)(10.0D * Math.cos((double)var0)));
   }

   private static void sleep(long var0) {
      try {
         Thread.sleep(var0);
      } catch (InterruptedException var3) {
         var3.printStackTrace();
      }

   }

   private static boolean isVerticalMovement() {
      return ID >= 1 && ID <= 37;
   }

   private static boolean isHorizontalMovement() {
      return ID >= 38 && ID <= 64;
   }

   private Vector2 initSpawnPoints() {
      spawnPoints.put(0, defaultSpawnPoint);

      int var1;
      int var2;
      int var3;
      for(var1 = 0; var1 < 37; ++var1) {
         var2 = var1 < 19 ? 3600 + var1 * 300 + 1 : 3600 + var1 * 300 - 1;
         var3 = (var1 + 1) % 2 == 0 ? 5101 : 12899;
         spawnPoints.put(var1 + 1, new Vector2((float)var2, (float)var3));
      }

      for(var1 = 0; var1 < 27; ++var1) {
         var2 = (var1 + 38) % 2 == 0 ? 3601 : 14399;
         var3 = var1 < 14 ? 5100 + var1 * 300 + 1 : 5100 + var1 * 300 - 1;
         spawnPoints.put(var1 + 38, new Vector2((float)var2, (float)var3));
      }

      for(var1 = 0; var1 < startPositions.length; ++var1) {
         spawnPoints.put(var1, startPositions[var1]);
      }

      for(var1 = 100; var1 < 112; ++var1) {
         spawnPoints.put(var1, (Vector2)startPositionsCircle.get(var1 - 100));
      }

      return (Vector2)spawnPoints.getOrDefault(ID, defaultSpawnPoint);
   }

   private void run() {
      DebugLog.disableLog(DebugType.General);
      log(String.format("FakeClient \"%s\"", versionNumber));
      Rand.init();
      Vector2 var1 = this.initSpawnPoints();
      player = new FakeClient.Player(var1.x, var1.y, ID);
      networking = new FakeClient.Networking();
      networking.state = FakeClient.Networking.State.START;
      startTime = System.currentTimeMillis();

      while(networking.state != FakeClient.Networking.State.QUIT) {
         networking.update();
         sleep(1L);
      }

      networking = null;
   }

   private static void log(String var0) {
      System.out.print(String.format("[%s] [client %d] %s\n", logDateFormat.format(Calendar.getInstance().getTime()), ID, var0));
   }

   public static void main(String[] var0) {
      System.loadLibrary("RakNet64");
      System.loadLibrary("ZNetNoSteam64");
      ID = var0.length == 1 ? Integer.parseInt(var0[0]) : -1;
      logNetwork = System.getProperty("zomboid.znetlog") != null;
      FakeClient var1 = new FakeClient();
      var1.run();
   }

   static {
      for(int var0 = 0; var0 < 12; ++var0) {
         startPositionsCircle.add(getPlayerPositionCircle((float)(var0 * 30)));
      }

      startPositions = new Vector2[]{new Vector2(5788.0F, 5299.0F), new Vector2(6099.0F, 5299.0F), new Vector2(6609.0F, 5299.0F), new Vector2(6797.0F, 5299.0F), new Vector2(7225.0F, 8217.0F), new Vector2(7225.0F, 8472.0F), new Vector2(8109.0F, 11308.0F), new Vector2(8109.0F, 11490.0F), new Vector2(8109.0F, 11660.0F), new Vector2(8229.0F, 11660.0F), new Vector2(8384.0F, 11660.0F), new Vector2(9914.0F, 13031.0F), new Vector2(9914.0F, 12750.0F), new Vector2(10186.0F, 12750.0F), new Vector2(11656.0F, 10390.0F), new Vector2(11656.0F, 10000.0F), new Vector2(11656.0F, 9700.0F), new Vector2(11656.0F, 9400.0F), new Vector2(12000.0F, 10390.0F), new Vector2(12000.0F, 10000.0F), new Vector2(12000.0F, 9700.0F), new Vector2(12000.0F, 9400.0F), new Vector2(11025.0F, 6735.0F), new Vector2(11325.0F, 6735.0F), new Vector2(11625.0F, 6735.0F), new Vector2(11925.0F, 6735.0F), new Vector2(11025.0F, 7035.0F), new Vector2(11325.0F, 7035.0F), new Vector2(11625.0F, 7035.0F), new Vector2(11925.0F, 7035.0F)};
      logDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
      logUpdateLimit = new UpdateLimit(1000L);
   }

   private static class Player {
      private static final int spawnMinX = 3600;
      private static final int spawnMaxX = 14400;
      private static final int spawnMinY = 5100;
      private static final int spawnMaxY = 12900;
      private static final int ChunkGridWidth = 13;
      private static final int ChunksPerWidth = 10;
      private static float speedKmph = 10.0F;
      private final UpdateLimit updateLimiter = new UpdateLimit(125L);
      private final int moveUpdateInterval = 250;
      private final UpdateLimit moveUpdateLimiter = new UpdateLimit(250L);
      private final int stopUpdateInterval = 4000;
      private final UpdateLimit stopUpdateLimiter = new UpdateLimit(4000L);
      private final ArrayList clothes;
      private final String username;
      private final int isFemale;
      private final Color tagColor;
      private final Color speakColor;
      private int OnlineID;
      private float x;
      private float y;
      private final float z;
      private IsoDirections dir;
      private int WorldX;
      private int WorldY;
      private float angleCircle;
      private boolean wasHit;

      private Player(float var1, float var2, int var3) {
         this.username = String.format("client_%d", var3);
         this.tagColor = Colors.SkyBlue;
         this.speakColor = Colors.GetRandomColor();
         this.isFemale = (int)Math.round(Math.random());
         this.OnlineID = -1;
         this.clothes = new ArrayList();
         this.clothes.add(new FakeClient.Player.Clothes((byte)11, (byte)0, "Shirt_FormalWhite"));
         this.clothes.add(new FakeClient.Player.Clothes((byte)13, (byte)3, "Tie_Full"));
         this.clothes.add(new FakeClient.Player.Clothes((byte)11, (byte)0, "Socks_Ankle"));
         this.clothes.add(new FakeClient.Player.Clothes((byte)13, (byte)0, "Trousers_Suit"));
         this.clothes.add(new FakeClient.Player.Clothes((byte)13, (byte)0, "Suit_Jacket"));
         this.clothes.add(new FakeClient.Player.Clothes((byte)11, (byte)0, "Shoes_Black"));
         this.clothes.add(new FakeClient.Player.Clothes((byte)11, (byte)0, "Glasses_Sun"));
         this.x = var1;
         this.y = var2;
         this.z = 0.0F;
         this.setDirection();
         this.WorldX = (int)this.x / 10;
         this.WorldY = (int)this.y / 10;
         speedKmph = (float)(Math.random() * 9.0D) + 1.0F;
         this.angleCircle = (float)(var3 * 30);
      }

      private void setDirection() {
         if (FakeClient.isHorizontalMovement()) {
            this.dir = FakeClient.ID % 2 == 0 ? IsoDirections.E : IsoDirections.W;
         } else if (FakeClient.isVerticalMovement()) {
            this.dir = FakeClient.ID % 2 == 0 ? IsoDirections.S : IsoDirections.N;
         } else {
            this.setRandomDirection();
         }

      }

      private void updateDirection() {
         this.dir = IsoDirections.fromIndex(this.dir.index() + 3);
      }

      private void setRandomDirection() {
         int var1 = (int)Math.round(Math.random() * 7.0D);
         if (this.dir == null || var1 != this.dir.index()) {
            this.dir = IsoDirections.fromIndex(var1);
         }

      }

      private float getDistance(float var1) {
         return var1 / 3.6F / 4.0F;
      }

      private void move() {
         if (FakeClient.ID >= 100 && FakeClient.ID < 112) {
            if (this.wasHit) {
               if (this.stopUpdateLimiter.Check()) {
                  this.wasHit = false;
               }
            } else if (this.moveUpdateLimiter.Check()) {
               this.angleCircle = (this.angleCircle + 0.04F) % 360.0F;
               Vector2 var5 = FakeClient.getPlayerPositionCircle(this.angleCircle);
               this.x = var5.x;
               this.y = var5.y;
               this.checkRequestChunks();
            }
         } else if (FakeClient.ID <= 64 && this.moveUpdateLimiter.Check()) {
            float var1 = this.getDistance(speedKmph);
            Vector2 var2 = this.dir.ToVector();
            float var3 = this.x + var1 * var2.x;
            float var4 = this.y + var1 * var2.y;
            if (var3 > 3600.0F && var3 < 14400.0F && var4 > 5100.0F && var4 < 12900.0F) {
               this.x = var3;
               this.y = var4;
               this.checkRequestChunks();
            } else {
               if (var3 < 3600.0F) {
                  var3 = 3600.0F;
               } else if (var3 > 14400.0F) {
                  var3 = 14400.0F;
               } else if (var4 < 5100.0F) {
                  var4 = 5100.0F;
               } else if (var4 > 12900.0F) {
                  var4 = 12900.0F;
               }

               this.x = var3;
               this.y = var4;
               this.updateDirection();
            }
         }

      }

      private void checkRequestChunks() {
         int var1 = (int)this.x / 10;
         int var2 = (int)this.y / 10;
         int var3;
         if (Math.abs(var1 - this.WorldX) < 13 && Math.abs(var2 - this.WorldY) < 13) {
            if (var1 != this.WorldX) {
               if (var1 < this.WorldX) {
                  for(var3 = -6; var3 <= 6; ++var3) {
                     FakeClient.networking.addChunkRequest(this.WorldX - 6, this.WorldY + var3, 0, var3 + 6);
                  }
               } else {
                  for(var3 = -6; var3 <= 6; ++var3) {
                     FakeClient.networking.addChunkRequest(this.WorldX + 6, this.WorldY + var3, 12, var3 + 6);
                  }
               }
            } else if (var2 != this.WorldY) {
               if (var2 < this.WorldY) {
                  for(var3 = -6; var3 <= 6; ++var3) {
                     FakeClient.networking.addChunkRequest(this.WorldX + var3, this.WorldY - 6, var3 + 6, 0);
                  }
               } else {
                  for(var3 = -6; var3 <= 6; ++var3) {
                     FakeClient.networking.addChunkRequest(this.WorldX + var3, this.WorldY + 6, var3 + 6, 12);
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
                  FakeClient.networking.addChunkRequest(var7, var8, var7 - var3, var8 - var4);
               }
            }
         }

         FakeClient.networking.requestChunks();
         this.WorldX = var1;
         this.WorldY = var2;
      }

      private void update() {
         if (this.updateLimiter.Check()) {
            this.move();
            FakeClient.networking.sendPlayer();
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

   private static class Networking {
      private final HashMap requests = new HashMap();
      private final ByteBuffer rb = ByteBuffer.allocate(1000000);
      private final ByteBuffer wb = ByteBuffer.allocate(1000000);
      private final RakNetPeerInterface peer = new RakNetPeerInterface();
      private long connectedGUID;
      private Thread thread;
      private FakeClient.Networking.State state;
      private static final HashMap systemPacketTypeNames = new HashMap();
      private static final HashMap userPacketTypeNames = new HashMap();

      private static void doPacket(short var0, ByteBuffer var1) {
         var1.put((byte)-122);
         var1.putShort(var0);
      }

      private static void putUTF(ByteBuffer var0, String var1) {
         if (var1 == null) {
            var0.putShort((short)0);
         } else {
            byte[] var2 = var1.getBytes();
            var0.putShort((short)var2.length);
            var0.put(var2);
         }

      }

      private static void putBoolean(ByteBuffer var0, boolean var1) {
         var0.put((byte)(var1 ? 1 : 0));
      }

      private Networking() {
         this.peer.Init(false);
         this.peer.SetMaximumIncomingConnections(1);
         this.peer.SetClientPort(17495 + FakeClient.ID);
         this.peer.SetOccasionalPing(true);
      }

      private void update() {
         switch(this.state) {
         case START:
            if (this.start()) {
               this.changeState(FakeClient.Networking.State.CONNECT);
            } else {
               this.changeState(FakeClient.Networking.State.RESTART);
            }
            break;
         case CONNECT:
            if (this.connect()) {
               this.changeState(FakeClient.Networking.State.WAIT_CONNECT);
            } else {
               this.changeState(FakeClient.Networking.State.RESTART);
            }
            break;
         case LOGIN:
            this.login();
            this.changeState(FakeClient.Networking.State.WAIT_LOGIN);
            break;
         case REGISTER:
            this.register();
            this.changeState(FakeClient.Networking.State.WAIT_REGISTER);
            break;
         case SETUP:
            this.setup();
            this.changeState(FakeClient.Networking.State.WAIT_SETUP);
            break;
         case LOAD:
            this.load();
            this.changeState(FakeClient.Networking.State.WAIT_LOAD);
            break;
         case RUN:
            FakeClient.player.update();
            break;
         case WAIT_CONNECT:
            if (this.checkStateTime()) {
               this.changeState(FakeClient.Networking.State.RESTART);
            }
            break;
         case WAIT_LOGIN:
            if (this.checkStateTime()) {
               FakeClient.log(String.format("%s timeout (%d)", this.state, 60L));
               this.changeState(FakeClient.Networking.State.DISCONNECT);
            }
         case WAIT_REGISTER:
            if (this.checkStateTime()) {
               FakeClient.log(String.format("%s timeout (%d)", this.state, 60L));
               this.changeState(FakeClient.Networking.State.DISCONNECT);
            }
         case WAIT_SETUP:
            if (this.checkStateTime()) {
               FakeClient.log(String.format("%s timeout (%d)", this.state, 60L));
               this.changeState(FakeClient.Networking.State.DISCONNECT);
            }
         case WAIT_LOAD:
            if (this.checkLoadTime()) {
               FakeClient.log(String.format("%s timeout (%d)", this.state, 60L));
               this.changeState(FakeClient.Networking.State.DISCONNECT);
            }
            break;
         case DISCONNECT:
            this.disconnect();
            this.changeState(FakeClient.Networking.State.WAIT_DISCONNECT);
            break;
         case WAIT_DISCONNECT:
            if (!this.thread.isAlive() || this.checkStateTime()) {
               this.changeState(FakeClient.Networking.State.RESTART);
            }
            break;
         case RESTART:
            this.stop();
            if (this.checkStateTime()) {
               this.changeState(FakeClient.Networking.State.START);
            }
         case QUIT:
         }

      }

      private void changeState(FakeClient.Networking.State var1) {
         long var2 = System.currentTimeMillis();
         if (FakeClient.logNetwork) {
            FakeClient.log(String.format("%s >> %s", this.state, var1));
         }

         FakeClient.stateTime = var2;
         this.state = var1;
         if (FakeClient.Networking.State.RUN.equals(var1)) {
            FakeClient.connectionTime = System.currentTimeMillis() - FakeClient.startTime;
            FakeClient.log(String.format("Connected in %.3f seconds", (float)FakeClient.connectionTime / 1000.0F));
         }

      }

      private void updateTime() {
         FakeClient.stateTime = System.currentTimeMillis();
      }

      private boolean checkStateTime() {
         long var1 = System.currentTimeMillis();
         return var1 - FakeClient.stateTime > 60000L;
      }

      private boolean checkLoadTime() {
         long var1 = System.currentTimeMillis();
         return var1 - FakeClient.stateTime > 120000L;
      }

      private boolean start() {
         boolean var1 = this.peer.Startup(1) == 0;
         if (var1) {
            this.thread = new Thread(ThreadGroups.Network, this::receiveThread, String.format("client_%d_receive", FakeClient.ID));
            this.thread.setDaemon(true);
            this.thread.start();
         }

         return var1;
      }

      private boolean connect() {
         boolean var1 = false;

         try {
            InetAddress var2 = InetAddress.getByName("localhost");
            String var3 = var2.getHostAddress();
            var1 = this.peer.Connect(var3, 16261, PZcrypt.hash("", true)) == 0;
         } catch (UnknownHostException var4) {
            var4.printStackTrace();
         }

         return var1;
      }

      private void login() {
         this.sendPlayerLogin();
      }

      private void register() {
         this.sendPlayerConnect();
      }

      private void setup() {
         boolean var1 = FakeClient.ID < 100 || FakeClient.ID >= 112;
         this.sendPlayerExtraInfo(var1);
      }

      private void load() {
         FakeClient.Networking.Request.count = 0;
         this.requests.clear();
         this.requestFullUpdate();
         this.requestLargeAreaZip();
      }

      private void disconnect() {
         if (this.connectedGUID != 0L) {
            this.peer.disconnect(this.connectedGUID);
         }

      }

      private void stop() {
         this.peer.Shutdown();
      }

      private void receiveThread() {
         while(true) {
            if (this.state != FakeClient.Networking.State.RESTART) {
               if (!this.peer.Receive(this.rb)) {
                  FakeClient.sleep(1L);
                  continue;
               }

               if (this.state != FakeClient.Networking.State.WAIT_DISCONNECT) {
                  this.decode(this.rb);
                  continue;
               }
            }

            return;
         }
      }

      private void logSystemPacket(int var1) {
         String var2 = (String)systemPacketTypeNames.getOrDefault(var1, "unknown system packet");
         FakeClient.log(String.format("# %s", var2));
      }

      private void decode(ByteBuffer var1) {
         int var2 = var1.get() & 255;
         switch(var2) {
         case 0:
         case 1:
         case 19:
         case 24:
         case 25:
         case 31:
         case 32:
         case 33:
         case 44:
         case 45:
         default:
            break;
         case 16:
            this.connectedGUID = this.peer.getGuidOfPacket();
            this.changeState(FakeClient.Networking.State.LOGIN);
            break;
         case 17:
         case 18:
         case 20:
         case 21:
         case 22:
         case 23:
            this.logSystemPacket(var2);
            this.changeState(FakeClient.Networking.State.RESTART);
            break;
         case 134:
            this.receive(var1.getShort(), var1);
         }

      }

      private void logUserPacket(short var1) {
         if (FakeClient.logNetwork) {
            String var2 = (String)userPacketTypeNames.getOrDefault(var1, "unknown user packet");
            FakeClient.log(String.format("## %s", var2));
         }

      }

      private void receive(short var1, ByteBuffer var2) {
         this.logUserPacket(var1);
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
         case 19:
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
         case 160:
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
         case 212:
         case 213:
         case 214:
         case 215:
         case 216:
         default:
            break;
         case 6:
            if (this.receivePlayerConnect(var2)) {
               this.changeState(FakeClient.Networking.State.SETUP);
            }
            break;
         case 10:
            this.receiveZombieUpdateInfo(var2);
            break;
         case 18:
            if (this.state == FakeClient.Networking.State.WAIT_LOAD && this.receiveChunkPart(var2)) {
               this.updateTime();
               if (this.allChunkPartsReceived()) {
                  this.changeState(FakeClient.Networking.State.REGISTER);
               }
            }
            break;
         case 21:
            this.changeState(FakeClient.Networking.State.LOAD);
            break;
         case 26:
            short var3 = var2.getShort();
            byte var4 = var2.get();
            short var5 = var2.getShort();
            if (var5 == FakeClient.player.OnlineID) {
               FakeClient.player.wasHit = true;
               UpdateLimit var10000 = FakeClient.player.stopUpdateLimiter;
               Objects.requireNonNull(FakeClient.player);
               var10000.Reset(4000L);
            }
            break;
         case 36:
            if (this.state == FakeClient.Networking.State.WAIT_LOAD && this.receiveNotRequired(var2)) {
               this.updateTime();
               if (this.allChunkPartsReceived()) {
                  this.changeState(FakeClient.Networking.State.REGISTER);
               }
            }
            break;
         case 84:
            if (this.receivePlayerExtraInfo(var2)) {
               this.changeState(FakeClient.Networking.State.RUN);
            }
         }

         var2.clear();
      }

      private ByteBuffer startPacket() {
         this.wb.clear();
         return this.wb;
      }

      private void cancelPacket() {
         this.wb.clear();
      }

      private void endPacket() {
         this.wb.flip();
         this.peer.Send(this.wb, 1, 3, (byte)0, this.connectedGUID, false);
      }

      private void endPacketImmediate() {
         this.wb.flip();
         this.peer.Send(this.wb, 0, 3, (byte)0, this.connectedGUID, false);
      }

      private void endPacketSuperHighUnreliable() {
         this.wb.flip();
         this.peer.Send(this.wb, 0, 1, (byte)0, this.connectedGUID, false);
      }

      private void sendPlayerLogin() {
         ByteBuffer var1 = this.startPacket();
         doPacket((short)2, var1);
         putUTF(var1, FakeClient.player.username);
         putUTF(var1, FakeClient.player.username);
         putUTF(var1, FakeClient.versionNumber);
         this.endPacketImmediate();
      }

      private void sendPlayerConnect() {
         ByteBuffer var1 = this.startPacket();
         doPacket((short)6, var1);
         this.writePlayerConnectData(var1);
         this.endPacketImmediate();
      }

      private void writePlayerConnectData(ByteBuffer var1) {
         var1.put((byte)0);
         var1.put((byte)13);
         var1.putFloat(FakeClient.player.x);
         var1.putFloat(FakeClient.player.y);
         var1.putFloat(FakeClient.player.z);
         var1.putInt(0);
         putUTF(var1, "fake");
         putUTF(var1, "fake");
         putUTF(var1, "fake");
         var1.putInt(FakeClient.player.isFemale);
         putUTF(var1, "fake");
         var1.putInt(0);
         var1.putInt(0);
         var1.put((byte)0);
         var1.put((byte)0);
         var1.put((byte)((int)Math.round(Math.random() * 5.0D)));
         var1.put((byte)0);
         var1.put((byte)0);
         var1.put((byte)0);
         var1.put((byte)0);
         int var2 = FakeClient.player.clothes.size();
         var1.put((byte)var2);
         Iterator var3 = FakeClient.player.clothes.iterator();

         while(var3.hasNext()) {
            FakeClient.Player.Clothes var4 = (FakeClient.Player.Clothes)var3.next();
            var1.put(var4.flags);
            putUTF(var1, "Base." + var4.name);
            putUTF(var1, (String)null);
            putUTF(var1, var4.name);
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

         putUTF(var1, "fake_str");
         var1.putShort((short)0);
         var1.putInt(0);
         var1.putFloat(0.0F);
         var1.putInt(0);
         var1.putInt(0);
         var1.putInt(0);
         var1.putInt(0);
         var1.putInt(0);
         putBoolean(var1, true);
         putUTF(var1, "fake");
         var1.putFloat(FakeClient.player.tagColor.r);
         var1.putFloat(FakeClient.player.tagColor.g);
         var1.putFloat(FakeClient.player.tagColor.b);
         var1.putInt(0);
         var1.putDouble(0.0D);
         var1.putInt(0);
         putUTF(var1, FakeClient.player.username);
         var1.putFloat(FakeClient.player.speakColor.r);
         var1.putFloat(FakeClient.player.speakColor.g);
         var1.putFloat(FakeClient.player.speakColor.b);
         putBoolean(var1, true);
         putBoolean(var1, false);
         var1.put((byte)0);
         var1.put((byte)0);
         var1.putInt(0);
         var1.putInt(0);
      }

      private void sendPlayerExtraInfo(boolean var1) {
         ByteBuffer var2 = this.startPacket();
         doPacket((short)84, var2);
         var2.putShort((short)FakeClient.player.OnlineID);
         putUTF(var2, "");
         var2.put((byte)(var1 ? 1 : 0));
         var2.put((byte)(var1 ? 1 : 0));
         var2.put((byte)0);
         var2.put((byte)0);
         var2.put((byte)0);
         this.endPacketImmediate();
      }

      private void sendPlayer() {
         int var1 = (int)(GameTime.getServerTime() / 1000000L);
         ByteBuffer var2 = this.startPacket();
         doPacket((short)218, var2);
         var2.putShort((short)FakeClient.player.OnlineID);
         var2.putFloat(FakeClient.player.x);
         var2.putFloat(FakeClient.player.y);
         var2.put((byte)((int)FakeClient.player.z));
         var2.putInt(var1 + 1000);
         var2.putFloat(FakeClient.player.dir.ToVector().getDirection());
         var2.put((byte)1);
         var2.putShort((short)-1);
         var2.putShort((short)-1);
         var2.putInt(0);
         var2.put((byte)1);
         var2.putFloat(FakeClient.player.x);
         var2.putFloat(FakeClient.player.y);
         var2.put((byte)((int)FakeClient.player.z));
         var2.put((byte)FakeClient.player.dir.index());
         var2.putInt(var1);
         var2.putShort((short)0);
         this.endPacketSuperHighUnreliable();
      }

      private boolean receivePlayerConnect(ByteBuffer var1) {
         short var2 = var1.getShort();
         if (var2 == -1) {
            byte var3 = var1.get();
            var2 = var1.getShort();
            FakeClient.player.OnlineID = var2;
            FakeClient.log(String.format("player:%s OnlineID:%d", FakeClient.player.username, var2));
            return true;
         } else {
            return false;
         }
      }

      private boolean receivePlayerExtraInfo(ByteBuffer var1) {
         short var2 = var1.getShort();
         return var2 == FakeClient.player.OnlineID;
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
         FakeClient.Networking.Request var5 = new FakeClient.Networking.Request(var1, var2);
         this.requests.put(var5.id, var5);
      }

      private void requestZipList() {
         ByteBuffer var1 = this.startPacket();
         doPacket((short)34, var1);
         var1.putInt(this.requests.size());
         Iterator var2 = this.requests.values().iterator();

         while(var2.hasNext()) {
            FakeClient.Networking.Request var3 = (FakeClient.Networking.Request)var2.next();
            var1.putInt(var3.id);
            var1.putInt(var3.wx);
            var1.putInt(var3.wy);
            var1.putLong(var3.crc);
         }

         this.endPacket();
      }

      private void requestLargeAreaZip() {
         ByteBuffer var1 = this.startPacket();
         doPacket((short)24, var1);
         var1.putInt(FakeClient.player.WorldX);
         var1.putInt(FakeClient.player.WorldY);
         var1.putInt(13);
         this.endPacketImmediate();
         int var2 = FakeClient.player.WorldX - 6 + 2;
         int var3 = FakeClient.player.WorldY - 6 + 2;
         int var4 = FakeClient.player.WorldX + 6 + 2;
         int var5 = FakeClient.player.WorldY + 6 + 2;

         for(int var6 = var3; var6 <= var5; ++var6) {
            for(int var7 = var2; var7 <= var4; ++var7) {
               FakeClient.Networking.Request var8 = new FakeClient.Networking.Request(var7, var6);
               this.requests.put(var8.id, var8);
            }
         }

         this.requestZipList();
      }

      private void requestFullUpdate() {
         ByteBuffer var1 = this.startPacket();
         doPacket((short)202, var1);
         this.endPacketImmediate();
      }

      private void requestChunkObjectState() {
         Iterator var1 = this.requests.values().iterator();

         while(var1.hasNext()) {
            FakeClient.Networking.Request var2 = (FakeClient.Networking.Request)var1.next();
            ByteBuffer var3 = this.startPacket();
            doPacket((short)151, var3);
            var3.putShort((short)var2.wx);
            var3.putShort((short)var2.wy);
            this.endPacket();
         }

      }

      private void requestChunks() {
         if (!this.requests.isEmpty()) {
            this.requestZipList();
            this.requestChunkObjectState();
            this.requests.clear();
         }

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

      private static enum State {
         START,
         CONNECT,
         WAIT_CONNECT,
         LOGIN,
         WAIT_LOGIN,
         LOAD,
         WAIT_LOAD,
         REGISTER,
         WAIT_REGISTER,
         SETUP,
         WAIT_SETUP,
         RUN,
         DISCONNECT,
         WAIT_DISCONNECT,
         RESTART,
         QUIT;

         // $FF: synthetic method
         private static FakeClient.Networking.State[] $values() {
            return new FakeClient.Networking.State[]{START, CONNECT, WAIT_CONNECT, LOGIN, WAIT_LOGIN, LOAD, WAIT_LOAD, REGISTER, WAIT_REGISTER, SETUP, WAIT_SETUP, RUN, DISCONNECT, WAIT_DISCONNECT, RESTART, QUIT};
         }
      }

      private static final class Request {
         private static final CRC32 crc32 = new CRC32();
         private static int count = 0;
         private final int id;
         private final int wx;
         private final int wy;
         private final long crc;

         private Request(int var1, int var2) {
            this.id = count++;
            this.wx = var1;
            this.wy = var2;
            crc32.reset();
            crc32.update(String.format("map_%d_%d.bin", var1, var2).getBytes());
            this.crc = crc32.getValue();
         }
      }
   }
}
