package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.BaseCharacterSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.network.packets.hit.MovingObject;


public class PlaySoundPacket implements INetworkPacket {
	String name;
	MovingObject object = new MovingObject();
	boolean loop;

	public void set(String string, boolean boolean1, IsoMovingObject movingObject) {
		this.name = string;
		this.loop = boolean1;
		this.object.setMovingObject(movingObject);
	}

	public void process() {
		IsoMovingObject movingObject = this.object.getMovingObject();
		if (movingObject instanceof IsoGameCharacter) {
			BaseCharacterSoundEmitter baseCharacterSoundEmitter = ((IsoGameCharacter)movingObject).getEmitter();
			if (!this.loop) {
				baseCharacterSoundEmitter.playSoundImpl(this.name, (IsoObject)null);
			}
		} else if (movingObject != null) {
			BaseSoundEmitter baseSoundEmitter = movingObject.emitter;
			if (baseSoundEmitter == null) {
				baseSoundEmitter = IsoWorld.instance.getFreeEmitter(movingObject.x, movingObject.y, movingObject.z);
				IsoWorld.instance.takeOwnershipOfEmitter(baseSoundEmitter);
				movingObject.emitter = baseSoundEmitter;
			}

			if (!this.loop) {
				baseSoundEmitter.playSoundImpl(this.name, (IsoObject)null);
			} else {
				baseSoundEmitter.playSoundLoopedImpl(this.name);
			}

			baseSoundEmitter.tick();
		}
	}

	public String getName() {
		return this.name;
	}

	public IsoMovingObject getMovingObject() {
		return this.object.getMovingObject();
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.object.parse(byteBuffer, udpConnection);
		this.name = GameWindow.ReadString(byteBuffer);
		this.loop = byteBuffer.get() == 1;
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		this.object.write(byteBufferWriter);
		byteBufferWriter.putUTF(this.name);
		byteBufferWriter.putByte((byte)(this.loop ? 1 : 0));
	}

	public boolean isConsistent() {
		return this.name != null && !this.name.isEmpty();
	}

	public int getPacketSizeBytes() {
		return 12 + this.name.length();
	}

	public String getDescription() {
		String string = this.name;
		return "\n\tPlaySoundPacket [name=" + string + " | object=" + this.object.getDescription() + " | loop=" + this.loop + " ]";
	}
}
