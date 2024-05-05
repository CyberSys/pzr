package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.iso.IsoDirections;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public class DeadBodyPacket implements INetworkPacket {
   public static int DIED_UNDER_VEHICLE = 65536;
   short id;
   public float x;
   public float y;
   public float z;
   public float angle;
   public IsoDirections direction;
   public boolean isFallOnFront;
   public boolean isCrawling;
   public int lastPlayerHit;
   public boolean isServer;
   public IsoZombie zombie;

   public void set(IsoZombie var1) {
      this.zombie = var1;
      this.id = (short)var1.getOnlineID();
      this.x = var1.getX();
      this.y = var1.getY();
      this.z = var1.getZ();
      this.angle = var1.getAnimAngleRadians();
      this.direction = var1.getDir();
      this.isFallOnFront = var1.isFallOnFront();
      this.isCrawling = var1.isCrawling();
      this.lastPlayerHit = var1.lastPlayerHit;
      this.isServer = GameServer.bServer;
   }

   public void parse(ByteBuffer var1) {
      this.id = var1.getShort();
      this.x = var1.getFloat();
      this.y = var1.getFloat();
      this.z = var1.getFloat();
      this.angle = var1.getFloat();
      this.direction = IsoDirections.fromIndex(var1.get());
      this.isFallOnFront = var1.get() == 1;
      this.isCrawling = var1.get() == 1;
      this.lastPlayerHit = var1.getInt();
      this.isServer = var1.get() == 1;
      if (GameServer.bServer) {
         this.zombie = ServerMap.instance.ZombieMap.get(this.id);
      } else if (GameClient.bClient) {
         this.zombie = (IsoZombie)GameClient.IDToZombieMap.get(this.id);
      }

   }

   public void write(ByteBufferWriter var1) {
      var1.putShort(this.id);
      var1.putFloat(this.x);
      var1.putFloat(this.y);
      var1.putFloat(this.z);
      var1.putFloat(this.angle);
      var1.putByte((byte)this.direction.index());
      var1.putBoolean(this.isFallOnFront);
      var1.putBoolean(this.isCrawling);
      var1.putInt(this.lastPlayerHit);
      var1.putBoolean(this.isServer);
   }

   public int getPacketSizeBytes() {
      return 0;
   }

   public String getDescription() {
      String var1 = String.format("id=%d, dying=%b, server=%b, wielder=%d, weapon=%b, angle=%f, direction=%s, front=%b, crawling=%b, pos=( %f ; %f ; %f )", this.id, this.zombie == null ? "unknown" : this.zombie.networkAI.deadZombie != null, this.isServer, this.lastPlayerHit & ~DIED_UNDER_VEHICLE, (this.lastPlayerHit & DIED_UNDER_VEHICLE) == 0, this.angle, this.direction, this.isFallOnFront, this.isCrawling, this.x, this.y, this.z);
      if (this.zombie != null) {
         var1 = var1 + ", states=( " + this.zombie.getPreviousActionContextStateName() + " > " + this.zombie.getCurrentActionContextStateName() + " )";
      }

      return var1;
   }
}
