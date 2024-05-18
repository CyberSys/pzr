package zombie.core.raknet;

import gnu.trove.list.array.TShortArrayList;
import gnu.trove.set.hash.TShortHashSet;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import sun.misc.Lock;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.iso.Vector3;
import zombie.network.ClientServerMap;
import zombie.network.PlayerDownloadServer;

public class UdpConnection {
   Lock bufferLock = new Lock();
   private ByteBuffer bb = ByteBuffer.allocate(1000000);
   private ByteBufferWriter bbw;
   Lock bufferLockPing;
   private ByteBuffer bbPing;
   private ByteBufferWriter bbwPing;
   long connectedGUID;
   UdpEngine engine;
   public boolean connected;
   public int index;
   public boolean allChatMuted;
   public String username;
   public String[] usernames;
   public byte ReleventRange;
   public String accessLevel;
   public String ip;
   public String password;
   public boolean ping;
   public Vector3[] ReleventPos;
   public int[] playerIDs;
   public IsoPlayer[] players;
   public Vector3[] connectArea;
   public int ChunkGridWidth;
   public ClientServerMap[] loadedCells;
   public PlayerDownloadServer playerDownloadServer;
   public UdpConnection.ChecksumState checksumState;
   public long checksumTime;
   public boolean sendPulse;
   public boolean awaitingCoopApprove;
   public long steamID;
   public long ownerID;
   public String idStr;
   public boolean isCoopHost;
   public TShortHashSet vehicles;
   public final TShortArrayList chunkObjectState;
   public final long[] packetCounts;
   private boolean bFullyConnected;

   public UdpConnection(UdpEngine var1, long var2, int var4) {
      this.bbw = new ByteBufferWriter(this.bb);
      this.bufferLockPing = new Lock();
      this.bbPing = ByteBuffer.allocate(50);
      this.bbwPing = new ByteBufferWriter(this.bbPing);
      this.connectedGUID = 0L;
      this.connected = true;
      this.allChatMuted = false;
      this.usernames = new String[4];
      this.accessLevel = "";
      this.ping = false;
      this.ReleventPos = new Vector3[4];
      this.playerIDs = new int[4];
      this.players = new IsoPlayer[4];
      this.connectArea = new Vector3[4];
      this.loadedCells = new ClientServerMap[4];
      this.checksumState = UdpConnection.ChecksumState.Init;
      this.sendPulse = false;
      this.awaitingCoopApprove = false;
      this.vehicles = new TShortHashSet();
      this.chunkObjectState = new TShortArrayList();
      this.packetCounts = new long[256];
      this.bFullyConnected = false;
      this.engine = var1;
      this.connectedGUID = var2;
      this.index = var4;
      this.ReleventPos[0] = new Vector3();

      for(int var5 = 0; var5 < 4; ++var5) {
         this.playerIDs[var5] = -1;
      }

   }

   public RakNetPeerInterface getPeer() {
      return this.engine.peer;
   }

   public long getConnectedGUID() {
      return this.connectedGUID;
   }

   public String getServerIP() {
      return this.engine.getServerIP();
   }

   public ByteBufferWriter startPacket() {
      try {
         this.bufferLock.lock();
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }

      this.bb.clear();
      return this.bbw;
   }

   public ByteBufferWriter startPingPacket() {
      try {
         this.bufferLockPing.lock();
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }

      this.bbPing.clear();
      return this.bbwPing;
   }

   public boolean ReleventTo(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         if (this.connectArea[var3] != null) {
            int var4 = (int)this.connectArea[var3].z;
            int var5 = (int)(this.connectArea[var3].x - (float)(var4 / 2)) * 10;
            int var6 = (int)(this.connectArea[var3].y - (float)(var4 / 2)) * 10;
            int var7 = var5 + var4 * 10;
            int var8 = var6 + var4 * 10;
            if (var1 >= (float)var5 && var1 < (float)var7 && var2 >= (float)var6 && var2 < (float)var8) {
               return true;
            }
         }

         if (this.ReleventPos[var3] != null && Math.abs(this.ReleventPos[var3].x - var1) <= (float)(this.ReleventRange * 10) && Math.abs(this.ReleventPos[var3].y - var2) <= (float)(this.ReleventRange * 10)) {
            return true;
         }
      }

      return false;
   }

   public boolean ReleventToPlayers(double var1, double var3, double var5) {
      for(int var7 = 0; var7 < 4; ++var7) {
         if (this.players[var7] != null) {
            double var8 = (double)this.players[var7].x - var1;
            double var10 = (double)this.players[var7].y - var3;
            var8 *= var8;
            var10 *= var10;
            if (Math.sqrt(var8 + var10) < var5) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean ReleventToPlayerIndex(int var1, float var2, float var3) {
      if (this.connectArea[var1] != null) {
         int var4 = (int)this.connectArea[var1].z;
         int var5 = (int)(this.connectArea[var1].x - (float)(var4 / 2)) * 10;
         int var6 = (int)(this.connectArea[var1].y - (float)(var4 / 2)) * 10;
         int var7 = var5 + var4 * 10;
         int var8 = var6 + var4 * 10;
         if (var2 >= (float)var5 && var2 < (float)var7 && var3 >= (float)var6 && var3 < (float)var8) {
            return true;
         }
      }

      return this.ReleventPos[var1] != null && Math.abs(this.ReleventPos[var1].x - var2) <= (float)(this.ReleventRange * 10) && Math.abs(this.ReleventPos[var1].y - var3) <= (float)(this.ReleventRange * 10);
   }

   public boolean RelevantTo(float var1, float var2, float var3) {
      for(int var4 = 0; var4 < 4; ++var4) {
         if (this.connectArea[var4] != null) {
            int var5 = (int)this.connectArea[var4].z;
            int var6 = (int)(this.connectArea[var4].x - (float)(var5 / 2)) * 10;
            int var7 = (int)(this.connectArea[var4].y - (float)(var5 / 2)) * 10;
            int var8 = var6 + var5 * 10;
            int var9 = var7 + var5 * 10;
            if (var1 >= (float)var6 && var1 < (float)var8 && var2 >= (float)var7 && var2 < (float)var9) {
               return true;
            }
         }

         if (this.ReleventPos[var4] != null && Math.abs(this.ReleventPos[var4].x - var1) <= var3 && Math.abs(this.ReleventPos[var4].y - var2) <= var3) {
            return true;
         }
      }

      return false;
   }

   public void cancelPacket() {
      this.bufferLock.unlock();
   }

   public void endPacket(int var1, int var2) {
      this.bb.flip();
      this.engine.peer.Send(this.bb, var1, var2, (byte)0, this.connectedGUID, false);
      this.bufferLock.unlock();
   }

   public void endPacket() {
      this.bb.flip();
      int var1 = this.engine.peer.Send(this.bb, 1, 3, (byte)0, this.connectedGUID, false);
      this.bufferLock.unlock();
   }

   public void endPacketImmediate() {
      this.bb.flip();
      int var1 = this.engine.peer.Send(this.bb, 0, 3, (byte)0, this.connectedGUID, false);
      this.bufferLock.unlock();
   }

   public void endPacketUnordered() {
      this.bb.flip();
      int var1 = this.engine.peer.Send(this.bb, 2, 2, (byte)0, this.connectedGUID, false);
      this.bufferLock.unlock();
   }

   public void endPacketUnreliable() {
      this.bb.flip();
      int var1 = this.engine.peer.Send(this.bb, 2, 1, (byte)0, this.connectedGUID, false);
      this.bufferLock.unlock();
   }

   public void endPacketSuperHighUnreliable() {
      this.bb.flip();
      int var1 = this.engine.peer.Send(this.bb, 0, 1, (byte)0, this.connectedGUID, false);
      this.bufferLock.unlock();
   }

   public void endPingPacket() {
      this.bbPing.flip();
      this.engine.peer.Send(this.bbPing, 0, 1, (byte)0, this.connectedGUID, false);
      this.bufferLockPing.unlock();
   }

   public void close() {
   }

   public void disconnect(String var1) {
   }

   public InetSocketAddress getInetSocketAddress() {
      String var1 = this.engine.peer.getIPFromGUID(this.connectedGUID);
      if ("UNASSIGNED_SYSTEM_ADDRESS".equals(var1)) {
         return null;
      } else {
         var1 = var1.replace("|", "Â£");
         String[] var2 = var1.split("Â£");
         InetSocketAddress var3 = new InetSocketAddress(var2[0], Integer.parseInt(var2[1]));
         return var3;
      }
   }

   public void forceDisconnect() {
      this.engine.forceDisconnect(this.getConnectedGUID());
   }

   public void setFullyConnected() {
      this.bFullyConnected = true;
   }

   public boolean isFullyConnected() {
      return this.bFullyConnected;
   }

   public static enum ChecksumState {
      Init,
      Different,
      Done;
   }
}
