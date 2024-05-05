package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;


public abstract class VehicleHitPacket extends HitCharacterPacket implements INetworkPacket {
	protected final Player wielder = new Player();
	protected final Vehicle vehicle = new Vehicle();

	public VehicleHitPacket(HitCharacterPacket.HitType hitType) {
		super(hitType);
	}

	public void set(IsoPlayer player, BaseVehicle baseVehicle, boolean boolean1) {
		this.wielder.set(player, boolean1);
		this.vehicle.set(baseVehicle);
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.wielder.parse(byteBuffer, udpConnection);
		this.wielder.parsePlayer((UdpConnection)null);
		this.vehicle.parse(byteBuffer, udpConnection);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		this.wielder.write(byteBufferWriter);
		this.vehicle.write(byteBufferWriter);
	}

	public boolean isRelevant(UdpConnection udpConnection) {
		return this.wielder.isRelevant(udpConnection);
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.wielder.isConsistent() && this.vehicle.isConsistent();
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tWielder " + this.wielder.getDescription() + "\n\tVehicle " + this.vehicle.getDescription();
	}

	protected void preProcess() {
		this.wielder.process();
	}

	protected void postProcess() {
		this.wielder.process();
	}

	protected void attack() {
	}
}
