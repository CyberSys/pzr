package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoWindow;
import zombie.network.packets.INetworkPacket;


public class HitInfo implements INetworkPacket {
	public MovingObject object = new MovingObject();
	public NetObject window = new NetObject();
	public float x;
	public float y;
	public float z;
	public float dot;
	public float distSq;
	public int chance = 0;

	public HitInfo init(IsoMovingObject movingObject, float float1, float float2, float float3, float float4, float float5) {
		this.object = new MovingObject();
		this.window = new NetObject();
		this.object.setMovingObject(movingObject);
		this.window.setObject((IsoObject)null);
		this.x = float3;
		this.y = float4;
		this.z = float5;
		this.dot = float1;
		this.distSq = float2;
		return this;
	}

	public HitInfo init(IsoWindow window, float float1, float float2) {
		this.object = new MovingObject();
		this.window = new NetObject();
		this.object.setMovingObject((IsoMovingObject)null);
		this.window.setObject(window);
		this.z = window.getZ();
		this.dot = float1;
		this.distSq = float2;
		return this;
	}

	public IsoMovingObject getObject() {
		return this.object.getMovingObject();
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.object.parse(byteBuffer, udpConnection);
		this.window.parse(byteBuffer, udpConnection);
		this.x = byteBuffer.getFloat();
		this.y = byteBuffer.getFloat();
		this.z = byteBuffer.getFloat();
		this.dot = byteBuffer.getFloat();
		this.distSq = byteBuffer.getFloat();
		this.chance = byteBuffer.getInt();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		this.object.write(byteBufferWriter);
		this.window.write(byteBufferWriter);
		byteBufferWriter.putFloat(this.x);
		byteBufferWriter.putFloat(this.y);
		byteBufferWriter.putFloat(this.z);
		byteBufferWriter.putFloat(this.dot);
		byteBufferWriter.putFloat(this.distSq);
		byteBufferWriter.putInt(this.chance);
	}

	public int getPacketSizeBytes() {
		return 24 + this.object.getPacketSizeBytes() + this.window.getPacketSizeBytes();
	}

	public String getDescription() {
		float float1 = this.x;
		return "\n\tHitInfo [ x=" + float1 + " y=" + this.y + " z=" + this.z + " dot=" + this.dot + " distSq=" + this.distSq + " chance=" + this.chance + "\n\t Object: " + this.object.getDescription() + "\n\t Window: " + this.window.getDescription() + " ]";
	}
}
