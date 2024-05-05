package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.packets.INetworkPacket;


public class PlayerBodyPart implements INetworkPacket {
	protected byte bodyPartIndex;
	protected BodyPart bodyPart;

	public void set(BodyPart bodyPart) {
		if (bodyPart == null) {
			this.bodyPartIndex = -1;
		} else {
			this.bodyPartIndex = (byte)bodyPart.getIndex();
		}

		this.bodyPart = bodyPart;
	}

	public void parse(ByteBuffer byteBuffer, IsoGameCharacter gameCharacter) {
		boolean boolean1 = byteBuffer.get() == 1;
		if (boolean1) {
			this.bodyPartIndex = byteBuffer.get();
			if (gameCharacter == null) {
				this.bodyPart = null;
			} else {
				this.bodyPart = gameCharacter.getBodyDamage().getBodyPart(BodyPartType.FromIndex(this.bodyPartIndex));
			}
		} else {
			this.bodyPart = null;
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		DebugLog.Multiplayer.error("PlayerBodyPart.parse is not implemented");
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		if (this.bodyPart == null) {
			byteBufferWriter.putByte((byte)0);
		} else {
			byteBufferWriter.putByte((byte)1);
			byteBufferWriter.putByte((byte)this.bodyPart.getIndex());
		}
	}

	public String getDescription() {
		String string = this.bodyPart == null ? "?" : "\"" + this.bodyPart.getType().name() + "\"";
		return "\n\tPlayerBodyPart [ Item=" + string + " ]";
	}

	public BodyPart getBodyPart() {
		return this.bodyPart;
	}
}
