package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.PacketValidator;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;


public class PlayerHitVehiclePacket extends PlayerHitPacket implements INetworkPacket {
	protected final Vehicle vehicle = new Vehicle();

	public PlayerHitVehiclePacket() {
		super(HitCharacterPacket.HitType.PlayerHitVehicle);
	}

	public void set(IsoPlayer player, BaseVehicle baseVehicle, HandWeapon handWeapon, boolean boolean1) {
		super.set(player, handWeapon, boolean1);
		this.vehicle.set(baseVehicle);
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.vehicle.parse(byteBuffer, udpConnection);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		this.vehicle.write(byteBufferWriter);
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.vehicle.isConsistent();
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tVehicle " + this.vehicle.getDescription();
	}

	protected void process() {
		this.vehicle.process(this.wielder.getCharacter(), this.weapon.getWeapon());
	}

	public boolean validate(UdpConnection udpConnection) {
		return PacketValidator.checkLongDistance(udpConnection, this.wielder, this.vehicle, PlayerHitVehiclePacket.class.getSimpleName());
	}
}
