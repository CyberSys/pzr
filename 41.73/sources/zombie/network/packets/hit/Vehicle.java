package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.GameServer;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;


public class Vehicle extends Instance implements IPositional,INetworkPacket {
	protected BaseVehicle vehicle;

	public void set(BaseVehicle baseVehicle) {
		super.set(baseVehicle.getId());
		this.vehicle = baseVehicle;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.vehicle = VehicleManager.instance.getVehicleByID(this.ID);
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
	}

	public boolean isConsistent() {
		return super.isConsistent() && this.vehicle != null;
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tVehicle [ vehicle=" + (this.vehicle == null ? "?" : "\"" + this.vehicle.getScriptName() + "\"") + " ]";
	}

	void process(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		if (GameServer.bServer) {
			this.vehicle.hitVehicle(gameCharacter, handWeapon);
		}
	}

	BaseVehicle getVehicle() {
		return this.vehicle;
	}

	public float getX() {
		return this.vehicle.getX();
	}

	public float getY() {
		return this.vehicle.getY();
	}
}
