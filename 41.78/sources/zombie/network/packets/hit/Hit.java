package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.network.packets.INetworkPacket;


public abstract class Hit implements INetworkPacket {
	private static final float MAX_DAMAGE = 100.0F;
	protected boolean ignore;
	protected float damage;
	protected float hitForce;
	protected float hitDirectionX;
	protected float hitDirectionY;

	public void set(boolean boolean1, float float1, float float2, float float3, float float4) {
		this.ignore = boolean1;
		this.damage = Math.min(float1, 100.0F);
		this.hitForce = float2;
		this.hitDirectionX = float3;
		this.hitDirectionY = float4;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.ignore = byteBuffer.get() != 0;
		this.damage = byteBuffer.getFloat();
		this.hitForce = byteBuffer.getFloat();
		this.hitDirectionX = byteBuffer.getFloat();
		this.hitDirectionY = byteBuffer.getFloat();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putBoolean(this.ignore);
		byteBufferWriter.putFloat(this.damage);
		byteBufferWriter.putFloat(this.hitForce);
		byteBufferWriter.putFloat(this.hitDirectionX);
		byteBufferWriter.putFloat(this.hitDirectionY);
	}

	public String getDescription() {
		return "\n\tHit [ ignore=" + this.ignore + " | damage=" + this.damage + " | force=" + this.hitForce + " | dir=( " + this.hitDirectionX + " ; " + this.hitDirectionY + " ) ]";
	}

	void process(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2) {
		gameCharacter2.getHitDir().set(this.hitDirectionX, this.hitDirectionY);
		gameCharacter2.setHitForce(this.hitForce);
		if (GameServer.bServer && gameCharacter2 instanceof IsoZombie && gameCharacter instanceof IsoPlayer) {
			((IsoZombie)gameCharacter2).addAggro(gameCharacter, this.damage);
			DebugLog.Damage.noise("AddAggro zombie=%d player=%d ( \"%s\" ) damage=%f", gameCharacter2.getOnlineID(), gameCharacter.getOnlineID(), ((IsoPlayer)gameCharacter).getUsername(), this.damage);
		}

		gameCharacter2.setAttackedBy(gameCharacter);
	}

	public float getDamage() {
		return this.damage;
	}
}
