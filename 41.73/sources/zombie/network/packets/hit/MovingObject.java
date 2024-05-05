package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;


public class MovingObject implements INetworkPacket {
	public final byte objectTypeNone = 0;
	public final byte objectTypePlayer = 1;
	public final byte objectTypeZombie = 2;
	public final byte objectTypeVehicle = 3;
	public final byte objectTypeObject = 4;
	private boolean isProcessed = false;
	private byte objectType = 0;
	private short objectId;
	private int squareX;
	private int squareY;
	private byte squareZ;
	private IsoMovingObject object;

	public void setMovingObject(IsoMovingObject movingObject) {
		this.object = movingObject;
		this.isProcessed = true;
		if (this.object == null) {
			this.objectType = 0;
			this.objectId = 0;
		} else if (this.object instanceof IsoPlayer) {
			this.objectType = 1;
			this.objectId = ((IsoPlayer)this.object).getOnlineID();
		} else if (this.object instanceof IsoZombie) {
			this.objectType = 2;
			this.objectId = ((IsoZombie)this.object).getOnlineID();
		} else if (this.object instanceof BaseVehicle) {
			this.objectType = 3;
			this.objectId = ((BaseVehicle)this.object).VehicleID;
		} else {
			IsoGridSquare square = this.object.getCurrentSquare();
			this.objectType = 4;
			this.objectId = (short)square.getMovingObjects().indexOf(this.object);
			this.squareX = square.getX();
			this.squareY = square.getY();
			this.squareZ = (byte)square.getZ();
		}
	}

	public IsoMovingObject getMovingObject() {
		if (!this.isProcessed) {
			if (this.objectType == 0) {
				this.object = null;
			}

			if (this.objectType == 1) {
				if (GameServer.bServer) {
					this.object = (IsoMovingObject)GameServer.IDToPlayerMap.get(this.objectId);
				} else if (GameClient.bClient) {
					this.object = (IsoMovingObject)GameClient.IDToPlayerMap.get(this.objectId);
				}
			}

			if (this.objectType == 2) {
				if (GameServer.bServer) {
					this.object = (IsoMovingObject)ServerMap.instance.ZombieMap.get(this.objectId);
				} else if (GameClient.bClient) {
					this.object = (IsoMovingObject)GameClient.IDToZombieMap.get(this.objectId);
				}
			}

			if (this.objectType == 3) {
				this.object = VehicleManager.instance.getVehicleByID(this.objectId);
			}

			if (this.objectType == 4) {
				IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(this.squareX, this.squareY, this.squareZ);
				if (square == null) {
					this.object = null;
				} else {
					this.object = (IsoMovingObject)square.getMovingObjects().get(this.objectId);
				}
			}

			this.isProcessed = true;
		}

		return this.object;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.objectType = byteBuffer.get();
		this.objectId = byteBuffer.getShort();
		if (this.objectType == 4) {
			this.squareX = byteBuffer.getInt();
			this.squareY = byteBuffer.getInt();
			this.squareZ = byteBuffer.get();
		}

		this.isProcessed = false;
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putByte(this.objectType);
		byteBufferWriter.putShort(this.objectId);
		if (this.objectType == 4) {
			byteBufferWriter.putInt(this.squareX);
			byteBufferWriter.putInt(this.squareY);
			byteBufferWriter.putByte(this.squareZ);
		}
	}

	public int getPacketSizeBytes() {
		return this.objectType == 4 ? 12 : 3;
	}

	public String getDescription() {
		String string = "";
		switch (this.objectType) {
		case 0: 
			string = "None";
			break;
		
		case 1: 
			string = "Player";
			break;
		
		case 2: 
			string = "Zombie";
			break;
		
		case 3: 
			string = "Vehicle";
			break;
		
		case 4: 
			string = "NetObject";
		
		}
		return this.objectType == 4 ? "\n\tMovingObject [type=" + string + "(" + this.objectType + ") | id=" + this.objectId + " | position=(" + this.squareX + ", " + this.squareY + ", " + this.squareZ + ") ]" : "\n\tMovingObject [type=" + string + "(" + this.objectType + ") | id=" + this.objectId + "]";
	}
}
