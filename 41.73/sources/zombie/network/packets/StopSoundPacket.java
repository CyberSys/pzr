package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoMovingObject;
import zombie.network.packets.hit.MovingObject;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;


public class StopSoundPacket implements INetworkPacket {
	MovingObject object = new MovingObject();
	String name;
	boolean trigger;

	public void set(IsoMovingObject movingObject, String string, boolean boolean1) {
		this.object.setMovingObject(movingObject);
		this.name = string;
		this.trigger = boolean1;
	}

	public void process() {
		IsoMovingObject movingObject = this.object.getMovingObject();
		IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
		if (gameCharacter != null) {
			if (this.trigger) {
				gameCharacter.getEmitter().stopOrTriggerSoundByName(this.name);
			} else {
				gameCharacter.getEmitter().stopSoundByName(this.name);
			}
		} else {
			BaseVehicle baseVehicle = (BaseVehicle)Type.tryCastTo(movingObject, BaseVehicle.class);
			if (baseVehicle != null) {
				if (this.trigger) {
					baseVehicle.getEmitter().stopOrTriggerSoundByName(this.name);
				} else {
					baseVehicle.getEmitter().stopSoundByName(this.name);
				}
			}
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.trigger = byteBuffer.get() == 1;
		this.object.parse(byteBuffer, udpConnection);
		this.name = GameWindow.ReadString(byteBuffer);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putByte((byte)(this.trigger ? 1 : 0));
		this.object.write(byteBufferWriter);
		byteBufferWriter.putUTF(this.name);
	}

	public int getPacketSizeBytes() {
		return this.object.getPacketSizeBytes() + 2 + this.name.length();
	}

	public String getDescription() {
		String string = this.name;
		return "\n\tStopSoundPacket [name=" + string + " | object=" + this.object.getDescription() + "]";
	}
}
