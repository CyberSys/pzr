package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.PacketValidator;
import zombie.network.packets.INetworkPacket;


public class PlayerHitPlayerPacket extends PlayerHitPacket implements INetworkPacket {
	protected final Player target = new Player();
	protected final WeaponHit hit = new WeaponHit();
	protected final Fall fall = new Fall();

	public PlayerHitPlayerPacket() {
		super(HitCharacterPacket.HitType.PlayerHitPlayer);
	}

	public void set(IsoPlayer player, IsoPlayer player2, HandWeapon handWeapon, float float1, boolean boolean1, float float2, boolean boolean2, boolean boolean3) {
		super.set(player, handWeapon, boolean2);
		this.target.set(player2, false);
		this.hit.set(boolean1, float1, float2, player2.getHitForce(), player2.getHitDir().x, player2.getHitDir().y, boolean3);
		this.fall.set(player2.getHitReactionNetworkAI());
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.target.parse(byteBuffer, udpConnection);
		this.target.parsePlayer((UdpConnection)null);
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

	public String getHitDescription() {
		String string = this.getClass().getSimpleName();
		return string + this.fall.getDescription() + this.target.getFlagsDescription();
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

	public boolean validate(UdpConnection udpConnection) {
		if (!PacketValidator.checkType1(udpConnection, this.wielder, this.target, PlayerHitPlayerPacket.class.getSimpleName())) {
			return false;
		} else if (!PacketValidator.checkType3(udpConnection, this.wielder, this.target, PlayerHitPlayerPacket.class.getSimpleName())) {
			return false;
		} else {
			return PacketValidator.checkType4(udpConnection, this.hit, PlayerHitPlayerPacket.class.getSimpleName());
		}
	}

	protected void attack() {
		this.wielder.attack(this.weapon.getWeapon(), true);
	}

	protected void react() {
		this.target.react();
	}
}
