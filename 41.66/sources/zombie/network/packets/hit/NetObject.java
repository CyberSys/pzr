package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.network.packets.INetworkPacket;


public class NetObject implements INetworkPacket {
	public final byte objectTypeNone = 0;
	public final byte objectTypeObject = 1;
	private boolean isProcessed = false;
	private byte objectType = 0;
	private short objectId;
	private int squareX;
	private int squareY;
	private byte squareZ;
	private IsoObject object;

	public void setObject(IsoObject object) {
		this.object = object;
		this.isProcessed = true;
		if (this.object == null) {
			this.objectType = 0;
			this.objectId = 0;
		} else {
			IsoGridSquare square = this.object.square;
			this.objectType = 1;
			this.objectId = (short)square.getObjects().indexOf(this.object);
			this.squareX = square.getX();
			this.squareY = square.getY();
			this.squareZ = (byte)square.getZ();
		}
	}

	public IsoObject getObject() {
		if (!this.isProcessed) {
			if (this.objectType == 0) {
				this.object = null;
			}

			if (this.objectType == 1) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(this.squareX, this.squareY, this.squareZ);
				if (square == null) {
					this.object = null;
				} else {
					this.object = (IsoObject)square.getObjects().get(this.objectId);
				}
			}

			this.isProcessed = true;
		}

		return this.object;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.objectType = byteBuffer.get();
		if (this.objectType == 1) {
			this.objectId = byteBuffer.getShort();
			this.squareX = byteBuffer.getInt();
			this.squareY = byteBuffer.getInt();
			this.squareZ = byteBuffer.get();
		}

		this.isProcessed = false;
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putByte(this.objectType);
		if (this.objectType == 1) {
			byteBufferWriter.putShort(this.objectId);
			byteBufferWriter.putInt(this.squareX);
			byteBufferWriter.putInt(this.squareY);
			byteBufferWriter.putByte(this.squareZ);
		}
	}

	public int getPacketSizeBytes() {
		return this.objectType == 1 ? 12 : 1;
	}

	public String getDescription() {
		String string = "";
		switch (this.objectType) {
		case 0: 
			string = "None";
			break;
		
		case 1: 
			string = "NetObject";
		
		}
		return "\n\tNetObject [type=" + string + "(" + this.objectType + ") | id=" + this.objectId + " | pos=(" + this.squareX + ", " + this.squareY + ", " + this.squareZ + ") ]";
	}
}
