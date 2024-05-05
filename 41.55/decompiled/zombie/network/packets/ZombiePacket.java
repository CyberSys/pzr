package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;

public class ZombiePacket implements INetworkPacket {
   public static final int PACKET_SIZE_BYTES = 57;
   public short id;
   public float x;
   public float y;
   public byte z;
   public int t;
   public int descriptorID;
   public int owner;
   public byte type;
   public int booleanVariables;
   public int target;
   public int eatBodyTarget;
   public int smParamTargetAngle;
   public float speedMod;
   public String walkType;
   public float realx;
   public float realy;
   public byte realz;
   public byte realdir;
   public float realHealth;

   public void parse(ByteBuffer var1) {
      this.id = var1.getShort();
      this.x = var1.getFloat();
      this.y = var1.getFloat();
      this.z = var1.get();
      this.t = var1.getInt();
      this.descriptorID = var1.getInt();
      this.owner = var1.getInt();
      this.type = var1.get();
      this.booleanVariables = var1.getInt();
      this.target = var1.getInt();
      this.eatBodyTarget = var1.getInt();
      this.smParamTargetAngle = var1.getInt();
      this.speedMod = var1.getFloat();
      this.walkType = GameWindow.ReadString(var1);
      this.realx = var1.getFloat();
      this.realy = var1.getFloat();
      this.realz = var1.get();
      this.realdir = var1.get();
      this.realHealth = var1.getFloat();
   }

   public void write(ByteBufferWriter var1) {
      long var2 = (long)var1.bb.position();
      var1.putShort(this.id);
      var1.putFloat(this.x);
      var1.putFloat(this.y);
      var1.putByte(this.z);
      var1.putInt(this.t);
      var1.putInt(this.descriptorID);
      var1.putInt(this.owner);
      var1.putByte(this.type);
      var1.putInt(this.booleanVariables);
      var1.putInt(this.target);
      var1.putInt(this.eatBodyTarget);
      var1.putInt(this.smParamTargetAngle);
      var1.putFloat(this.speedMod);
      var1.putUTF(this.walkType);
      var1.putFloat(this.realx);
      var1.putFloat(this.realy);
      var1.putByte(this.realz);
      var1.putByte(this.realdir);
      var1.putFloat(this.realHealth);
   }

   public int getPacketSizeBytes() {
      return 57;
   }

   public void set(IsoZombie var1, int var2) {
      this.id = var1.OnlineID;
      this.descriptorID = var1.getPersistentOutfitID();
      var1.networkAI.set(this, var2);
      var1.thumpSent = true;
   }
}
