package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.PacketValidator;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;


public class VehicleHitPlayerPacket extends VehicleHitPacket implements INetworkPacket {
	protected final Player target = new Player();
	protected final VehicleHit vehicleHit = new VehicleHit();
	protected final Fall fall = new Fall();

	public VehicleHitPlayerPacket() {
		super(HitCharacterPacket.HitType.VehicleHitPlayer);
	}

	public void set(IsoPlayer player, IsoPlayer player2, BaseVehicle baseVehicle, float float1, boolean boolean1, int int1, float float2, boolean boolean2) {
		super.set(player, baseVehicle, false);
		this.target.set(player2, false);
		this.vehicleHit.set(false, float1, player2.getHitForce(), player2.getHitDir().x, player2.getHitDir().y, int1, float2, boolean2, boolean1);
		this.fall.set(player2.getHitReactionNetworkAI());
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.target.parse(byteBuffer, udpConnection);
		this.target.parsePlayer(udpConnection);
		this.vehicleHit.parse(byteBuffer, udpConnection);
		this.fall.parse(byteBuffer, udpConnection);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		this.target.write(byteBufferWriter);
		this.vehicleHit.write(byteBufferWriter);
		this.fall.write(byteBufferWriter);
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.target.isConsistent() && this.vehicleHit.isConsistent();
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tTarget " + this.target.getDescription() + "\n\tVehicleHit " + this.vehicleHit.getDescription() + "\n\tFall " + this.fall.getDescription();
	}

	protected void preProcess() {
		super.preProcess();
		this.target.process();
	}

	protected void process() {
		this.vehicleHit.process(this.wielder.getCharacter(), this.target.getCharacter(), this.vehicle.getVehicle());
		this.fall.process(this.target.getCharacter());
	}

	protected void postProcess() {
		super.postProcess();
		this.target.process();
	}

	protected void react() {
		this.target.react();
	}

	protected void postpone() {
		this.target.getCharacter().getNetworkCharacterAI().setVehicleHit(this);
	}

	public boolean validate(UdpConnection udpConnection) {
		if (!PacketValidator.checkType1(udpConnection, this.wielder, this.target, VehicleHitPlayerPacket.class.getSimpleName())) {
			return false;
		} else if (!PacketValidator.checkType2(udpConnection, this.vehicleHit, VehicleHitPlayerPacket.class.getSimpleName())) {
			return false;
		} else if (!PacketValidator.checkType8(udpConnection, this.wielder, this.target, VehicleHitPlayerPacket.class.getSimpleName())) {
			return false;
		} else {
			return PacketValidator.checkType4(udpConnection, this.vehicleHit, VehicleHitPlayerPacket.class.getSimpleName());
		}
	}
}
