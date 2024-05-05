package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.packets.INetworkPacket;


public abstract class PlayerHitPacket extends HitCharacterPacket implements INetworkPacket {
	protected final Player wielder = new Player();
	protected final Weapon weapon = new Weapon();

	public PlayerHitPacket(HitCharacterPacket.HitType hitType) {
		super(hitType);
	}

	public void set(IsoPlayer player, HandWeapon handWeapon, boolean boolean1) {
		this.wielder.set(player, boolean1);
		this.weapon.set(handWeapon);
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.wielder.parse(byteBuffer, udpConnection);
		this.wielder.parsePlayer(udpConnection);
		this.weapon.parse(byteBuffer, (IsoLivingCharacter)this.wielder.getCharacter());
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		this.wielder.write(byteBufferWriter);
		this.weapon.write(byteBufferWriter);
	}

	public boolean isRelevant(UdpConnection udpConnection) {
		return this.wielder.isRelevant(udpConnection);
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.weapon.isConsistent() && this.wielder.isConsistent();
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tWielder " + this.wielder.getDescription() + "\n\tWeapon " + this.weapon.getDescription();
	}

	protected void preProcess() {
		this.wielder.process();
	}

	protected void postProcess() {
		this.wielder.process();
	}

	protected void attack() {
		this.wielder.attack(this.weapon.getWeapon(), false);
	}

	protected void react() {
	}
}
