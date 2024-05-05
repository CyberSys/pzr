package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;

public class PlayerPacket implements INetworkPacket {
   public static final int PACKET_SIZE_BYTES = 43;
   public short id;
   public float x;
   public float y;
   public byte z;
   public int t;
   public float direction;
   public boolean usePathFinder;
   public short VehicleID;
   public short VehicleSeat;
   public int booleanVariables;
   public byte footstepSoundRadius;
   public float realx;
   public float realy;
   public byte realz;
   public byte realdir;
   public int realt;

   public void parse(ByteBuffer var1) {
      this.id = var1.getShort();
      this.x = var1.getFloat();
      this.y = var1.getFloat();
      this.z = var1.get();
      this.t = var1.getInt();
      this.direction = var1.getFloat();
      this.usePathFinder = var1.get() == 1;
      this.VehicleID = var1.getShort();
      this.VehicleSeat = var1.getShort();
      this.booleanVariables = var1.getInt();
      this.footstepSoundRadius = var1.get();
      this.realx = var1.getFloat();
      this.realy = var1.getFloat();
      this.realz = var1.get();
      this.realdir = var1.get();
      this.realt = var1.getInt();
   }

   public void write(ByteBufferWriter var1) {
      var1.putShort(this.id);
      var1.putFloat(this.x);
      var1.putFloat(this.y);
      var1.putByte(this.z);
      var1.putInt(this.t);
      var1.putFloat(this.direction);
      var1.putBoolean(this.usePathFinder);
      var1.putShort(this.VehicleID);
      var1.putShort(this.VehicleSeat);
      var1.putInt(this.booleanVariables);
      var1.putByte(this.footstepSoundRadius);
      var1.putFloat(this.realx);
      var1.putFloat(this.realy);
      var1.putByte(this.realz);
      var1.putByte(this.realdir);
      var1.putInt(this.realt);
   }

   public int getPacketSizeBytes() {
      return 43;
   }

   public boolean set(IsoPlayer var1) {
      this.id = (short)var1.OnlineID;
      return var1.networkAI.set(this);
   }

   public static class l_send {
      public static PlayerPacket playerPacket = new PlayerPacket();
   }

   public static class l_receive {
      public static PlayerPacket playerPacket = new PlayerPacket();
   }
}
