package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;


public class VehicleAuthorizationPacket implements INetworkPacket {
	short vehicleId = -1;
	BaseVehicle.Authorization authorization;
	short authorizationPlayer;

	public VehicleAuthorizationPacket() {
		this.authorization = BaseVehicle.Authorization.Server;
		this.authorizationPlayer = -1;
	}

	public void set(BaseVehicle baseVehicle, UdpConnection udpConnection) {
		BaseVehicle.ServerVehicleState serverVehicleState = baseVehicle.connectionState[udpConnection.index];
		serverVehicleState.setAuthorization(baseVehicle);
		this.authorization = baseVehicle.netPlayerAuthorization;
		this.authorizationPlayer = baseVehicle.netPlayerId;
		this.vehicleId = baseVehicle.getId();
	}

	public void process() {
		BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(this.vehicleId);
		if (baseVehicle != null) {
			DebugLog.Vehicle.trace("vehicle=%d netPlayerAuthorization=%s netPlayerId=%d", baseVehicle.getId(), this.authorization.name(), this.authorizationPlayer);
			baseVehicle.netPlayerFromServerUpdate(this.authorization, this.authorizationPlayer);
		}
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.vehicleId = byteBuffer.getShort();
		this.authorization = BaseVehicle.Authorization.valueOf(byteBuffer.get());
		this.authorizationPlayer = byteBuffer.getShort();
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.vehicleId);
		byteBufferWriter.putByte(this.authorization.index());
		byteBufferWriter.putShort(this.authorizationPlayer);
	}

	public String getDescription() {
		return null;
	}
}
