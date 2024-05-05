package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.PacketValidator;
import zombie.network.packets.INetworkPacket;


public class PlayerHitZombiePacket extends PlayerHitPacket implements INetworkPacket {
	protected final Zombie target = new Zombie();
	protected final WeaponHit hit = new WeaponHit();
	protected final Fall fall = new Fall();

	public PlayerHitZombiePacket() {
		super(HitCharacterPacket.HitType.PlayerHitZombie);
	}

	public void set(IsoPlayer player, IsoZombie zombie, HandWeapon handWeapon, float float1, boolean boolean1, float float2, boolean boolean2, boolean boolean3, boolean boolean4) {
		super.set(player, handWeapon, boolean2);
		this.target.set(zombie, boolean3);
		this.hit.set(boolean1, float1, float2, zombie.getHitForce(), zombie.getHitDir().x, zombie.getHitDir().y, boolean4);
		this.fall.set(zombie.getHitReactionNetworkAI());
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.target.parse(byteBuffer, udpConnection);
		this.hit.parse(byteBuffer, udpConnection);
		this.fall.parse(byteBuffer, udpConnection);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		this.target.write(byteBufferWriter);
		this.hit.write(byteBufferWriter);
		this.fall.write(byteBufferWriter);
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.target.isConsistent() && this.hit.isConsistent();
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tTarget " + this.target.getDescription() + "\n\tHit " + this.hit.getDescription() + "\n\tFall " + this.fall.getDescription();
	}

	protected void preProcess() {
		super.preProcess();
		this.target.process();
	}

	protected void process() {
		this.hit.process(this.wielder.getCharacter(), this.target.getCharacter(), this.weapon.getWeapon());
		this.fall.process(this.target.getCharacter());
	}

	protected void postProcess() {
		super.postProcess();
		this.target.process();
	}

	protected void react() {
		this.target.react(this.weapon.getWeapon());
	}

	public boolean validate(UdpConnection udpConnection) {
		if (!PacketValidator.checkType3(udpConnection, this.wielder, this.target, PlayerHitZombiePacket.class.getSimpleName())) {
			return false;
		} else {
			return PacketValidator.checkType4(udpConnection, this.hit, PlayerHitZombiePacket.class.getSimpleName());
		}
	}
}
