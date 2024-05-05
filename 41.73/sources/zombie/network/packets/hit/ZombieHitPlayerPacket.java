package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.PacketValidator;
import zombie.network.packets.INetworkPacket;


public class ZombieHitPlayerPacket extends HitCharacterPacket implements INetworkPacket {
	protected final Zombie wielder = new Zombie();
	protected final Player target = new Player();
	protected final Bite bite = new Bite();

	public ZombieHitPlayerPacket() {
		super(HitCharacterPacket.HitType.ZombieHitPlayer);
	}

	public void set(IsoZombie zombie, IsoPlayer player) {
		this.wielder.set(zombie, false);
		this.target.set(player, false);
		this.bite.set(zombie);
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.wielder.parse(byteBuffer, udpConnection);
		this.target.parse(byteBuffer, udpConnection);
		this.target.parsePlayer(udpConnection);
		this.bite.parse(byteBuffer, udpConnection);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		this.wielder.write(byteBufferWriter);
		this.target.write(byteBufferWriter);
		this.bite.write(byteBufferWriter);
	}

	public boolean isRelevant(UdpConnection udpConnection) {
		return this.target.isRelevant(udpConnection);
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.target.isConsistent() && this.wielder.isConsistent();
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tWielder " + this.wielder.getDescription() + "\n\tTarget " + this.target.getDescription() + "\n\tBite " + this.bite.getDescription();
	}

	protected void preProcess() {
		this.wielder.process();
		this.target.process();
	}

	protected void process() {
		this.bite.process((IsoZombie)this.wielder.getCharacter(), this.target.getCharacter());
	}

	protected void postProcess() {
		this.wielder.process();
		this.target.process();
	}

	protected void attack() {
	}

	protected void react() {
		this.wielder.react();
		this.target.react();
	}

	public boolean validate(UdpConnection udpConnection) {
		if (!PacketValidator.checkType8(udpConnection, this.wielder, this.target, ZombieHitPlayerPacket.class.getSimpleName())) {
			return false;
		} else if (!PacketValidator.checkType5(udpConnection, this.wielder, ZombieHitPlayerPacket.class.getSimpleName())) {
			return false;
		} else {
			return PacketValidator.checkType6(udpConnection, this.target, ZombieHitPlayerPacket.class.getSimpleName());
		}
	}
}
