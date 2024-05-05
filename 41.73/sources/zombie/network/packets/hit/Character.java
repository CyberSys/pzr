package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import java.util.Optional;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.packets.INetworkPacket;


public abstract class Character extends Instance implements IPositional,INetworkPacket {
	protected IsoGameCharacter character;
	protected short characterFlags;
	protected float positionX;
	protected float positionY;
	protected float positionZ;
	protected float directionX;
	protected float directionY;
	protected String characterReaction;
	protected String playerReaction;
	protected String zombieReaction;

	public void set(IsoGameCharacter gameCharacter) {
		super.set(gameCharacter.getOnlineID());
		this.characterFlags = 0;
		this.characterFlags |= (short)(gameCharacter.isDead() ? 1 : 0);
		this.characterFlags |= (short)(gameCharacter.isCloseKilled() ? 2 : 0);
		this.characterFlags |= (short)(gameCharacter.isHitFromBehind() ? 4 : 0);
		this.characterFlags |= (short)(gameCharacter.isFallOnFront() ? 8 : 0);
		this.characterFlags |= (short)(gameCharacter.isKnockedDown() ? 16 : 0);
		this.characterFlags |= (short)(gameCharacter.isOnFloor() ? 32 : 0);
		this.character = gameCharacter;
		this.positionX = gameCharacter.getX();
		this.positionY = gameCharacter.getY();
		this.positionZ = gameCharacter.getZ();
		this.directionX = gameCharacter.getForwardDirection().getX();
		this.directionY = gameCharacter.getForwardDirection().getY();
		this.characterReaction = (String)Optional.ofNullable(gameCharacter.getHitReaction()).orElse("");
		this.playerReaction = (String)Optional.ofNullable(gameCharacter.getVariableString("PlayerHitReaction")).orElse("");
		this.zombieReaction = (String)Optional.ofNullable(gameCharacter.getVariableString("ZombieHitReaction")).orElse("");
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.characterFlags = byteBuffer.getShort();
		this.positionX = byteBuffer.getFloat();
		this.positionY = byteBuffer.getFloat();
		this.positionZ = byteBuffer.getFloat();
		this.directionX = byteBuffer.getFloat();
		this.directionY = byteBuffer.getFloat();
		this.characterReaction = GameWindow.ReadString(byteBuffer);
		this.playerReaction = GameWindow.ReadString(byteBuffer);
		this.zombieReaction = GameWindow.ReadString(byteBuffer);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		byteBufferWriter.putShort(this.characterFlags);
		byteBufferWriter.putFloat(this.positionX);
		byteBufferWriter.putFloat(this.positionY);
		byteBufferWriter.putFloat(this.positionZ);
		byteBufferWriter.putFloat(this.directionX);
		byteBufferWriter.putFloat(this.directionY);
		byteBufferWriter.putUTF(this.characterReaction);
		byteBufferWriter.putUTF(this.playerReaction);
		byteBufferWriter.putUTF(this.zombieReaction);
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.character != null;
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tCharacter [ hit-reactions=( \"c=\"" + this.characterReaction + "\" ; p=\"" + this.playerReaction + "\" ; z=\"" + this.zombieReaction + "\" ) | " + this.getFlagsDescription() + " | pos=( " + this.positionX + " ; " + this.positionY + " ; " + this.positionZ + " ) | dir=( " + this.directionX + " ; " + this.directionY + " ) | health=" + (this.character == null ? "?" : this.character.getHealth()) + " | current=" + (this.character == null ? "?" : "\"" + this.character.getCurrentActionContextStateName() + "\"") + " | previous=" + (this.character == null ? "?" : "\"" + this.character.getPreviousActionContextStateName() + "\"") + " ]";
	}

	String getFlagsDescription() {
		boolean boolean1 = (this.characterFlags & 1) != 0;
		return " Flags [ isDead=" + boolean1 + " | isKnockedDown=" + ((this.characterFlags & 16) != 0) + " | isCloseKilled=" + ((this.characterFlags & 2) != 0) + " | isHitFromBehind=" + ((this.characterFlags & 4) != 0) + " | isFallOnFront=" + ((this.characterFlags & 8) != 0) + " | isOnFloor=" + ((this.characterFlags & 32) != 0) + " ]";
	}

	void process() {
		this.character.setHitReaction(this.characterReaction);
		this.character.setVariable("PlayerHitReaction", this.playerReaction);
		this.character.setVariable("ZombieHitReaction", this.zombieReaction);
		this.character.setCloseKilled((this.characterFlags & 2) != 0);
		this.character.setHitFromBehind((this.characterFlags & 4) != 0);
		this.character.setFallOnFront((this.characterFlags & 8) != 0);
		this.character.setKnockedDown((this.characterFlags & 16) != 0);
		this.character.setOnFloor((this.characterFlags & 32) != 0);
		if (GameServer.bServer && (this.characterFlags & 32) == 0 && (this.characterFlags & 4) != 0) {
			this.character.setFallOnFront(true);
		}
	}

	protected void react() {
	}

	public float getX() {
		return this.positionX;
	}

	public float getY() {
		return this.positionY;
	}

	public abstract IsoGameCharacter getCharacter();
}
