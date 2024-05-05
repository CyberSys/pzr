package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoMovingObject;
import zombie.network.GameServer;
import zombie.network.packets.INetworkPacket;


public class Bite implements INetworkPacket {
	protected short flags;
	protected float hitDirection;

	public void set(IsoZombie zombie) {
		this.flags = 0;
		this.flags |= (short)(zombie.getEatBodyTarget() != null ? 1 : 0);
		this.flags |= (short)(zombie.getVariableBoolean("AttackDidDamage") ? 2 : 0);
		this.flags |= (short)("BiteDefended".equals(zombie.getHitReaction()) ? 4 : 0);
		this.flags |= (short)(zombie.scratch ? 8 : 0);
		this.flags |= (short)(zombie.laceration ? 16 : 0);
		this.hitDirection = zombie.getHitDir().getDirection();
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.flags = byteBuffer.getShort();
		this.hitDirection = byteBuffer.getFloat();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.flags);
		byteBufferWriter.putFloat(this.hitDirection);
	}

	public String getDescription() {
		boolean boolean1 = (this.flags & 1) != 0;
		return "\n\tBite [ eatBodyTarget=" + boolean1 + " | attackDidDamage=" + ((this.flags & 2) != 0) + " | biteDefended=" + ((this.flags & 4) != 0) + " | scratch=" + ((this.flags & 8) != 0) + " | laceration=" + ((this.flags & 16) != 0) + " | hitDirection=" + this.hitDirection + " ]";
	}

	void process(IsoZombie zombie, IsoGameCharacter gameCharacter) {
		if ((this.flags & 4) == 0) {
			gameCharacter.setAttackedBy(zombie);
			if ((this.flags & 1) != 0 || gameCharacter.isDead()) {
				zombie.setEatBodyTarget(gameCharacter, true);
				zombie.setTarget((IsoMovingObject)null);
			}

			if (gameCharacter.isAsleep()) {
				if (GameServer.bServer) {
					gameCharacter.sendObjectChange("wakeUp");
				} else {
					gameCharacter.forceAwake();
				}
			}

			if ((this.flags & 2) != 0) {
				gameCharacter.reportEvent("washit");
				gameCharacter.setVariable("hitpvp", false);
			}

			zombie.scratch = (this.flags & 8) != 0;
			zombie.laceration = (this.flags & 8) != 0;
		}

		zombie.getHitDir().setLengthAndDirection(this.hitDirection, 1.0F);
	}
}
