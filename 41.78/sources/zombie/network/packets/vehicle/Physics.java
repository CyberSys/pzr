package zombie.network.packets.vehicle;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.physics.Bullet;
import zombie.core.physics.WorldSimulation;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleInterpolationData;
import zombie.vehicles.VehicleManager;


public class Physics extends VehicleInterpolationData implements INetworkPacket {
	private static final float[] buffer = new float[27];
	protected short id;
	protected float force;
	private BaseVehicle vehicle;
	private boolean hasAuth;

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.id = byteBuffer.getShort();
		this.time = byteBuffer.getLong();
		this.force = byteBuffer.getFloat();
		this.x = byteBuffer.getFloat();
		this.y = byteBuffer.getFloat();
		this.z = byteBuffer.getFloat();
		this.qx = byteBuffer.getFloat();
		this.qy = byteBuffer.getFloat();
		this.qz = byteBuffer.getFloat();
		this.qw = byteBuffer.getFloat();
		this.vx = byteBuffer.getFloat();
		this.vy = byteBuffer.getFloat();
		this.vz = byteBuffer.getFloat();
		this.engineSpeed = byteBuffer.getFloat();
		this.throttle = byteBuffer.getFloat();
		this.setNumWheels(byteBuffer.getShort());
		for (int int1 = 0; int1 < this.wheelsCount; ++int1) {
			this.wheelSteering[int1] = byteBuffer.getFloat();
			this.wheelRotation[int1] = byteBuffer.getFloat();
			this.wheelSkidInfo[int1] = byteBuffer.getFloat();
			this.wheelSuspensionLength[int1] = byteBuffer.getFloat();
		}

		this.vehicle = VehicleManager.instance.getVehicleByID(this.id);
		if (this.vehicle != null) {
			this.hasAuth = this.vehicle.hasAuthorization(udpConnection);
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.id);
		byteBufferWriter.putLong(this.time);
		byteBufferWriter.putFloat(this.force);
		byteBufferWriter.putFloat(this.x);
		byteBufferWriter.putFloat(this.y);
		byteBufferWriter.putFloat(this.z);
		byteBufferWriter.putFloat(this.qx);
		byteBufferWriter.putFloat(this.qy);
		byteBufferWriter.putFloat(this.qz);
		byteBufferWriter.putFloat(this.qw);
		byteBufferWriter.putFloat(this.vx);
		byteBufferWriter.putFloat(this.vy);
		byteBufferWriter.putFloat(this.vz);
		byteBufferWriter.putFloat(this.engineSpeed);
		byteBufferWriter.putFloat(this.throttle);
		byteBufferWriter.putShort(this.wheelsCount);
		for (int int1 = 0; int1 < this.wheelsCount; ++int1) {
			byteBufferWriter.putFloat(this.wheelSteering[int1]);
			byteBufferWriter.putFloat(this.wheelRotation[int1]);
			byteBufferWriter.putFloat(this.wheelSkidInfo[int1]);
			byteBufferWriter.putFloat(this.wheelSuspensionLength[int1]);
		}
	}

	public boolean set(BaseVehicle baseVehicle) {
		if (Bullet.getOwnVehiclePhysics(baseVehicle.VehicleID, buffer) != 0) {
			return false;
		} else {
			this.id = baseVehicle.getId();
			this.time = WorldSimulation.instance.time;
			this.force = baseVehicle.getForce();
			byte byte1 = 0;
			int int1 = byte1 + 1;
			this.x = buffer[byte1];
			this.y = buffer[int1++];
			this.z = buffer[int1++];
			this.qx = buffer[int1++];
			this.qy = buffer[int1++];
			this.qz = buffer[int1++];
			this.qw = buffer[int1++];
			this.vx = buffer[int1++];
			this.vy = buffer[int1++];
			this.vz = buffer[int1++];
			this.engineSpeed = (float)baseVehicle.getEngineSpeed();
			this.throttle = baseVehicle.throttle;
			this.wheelsCount = (short)((int)buffer[int1++]);
			for (int int2 = 0; int2 < this.wheelsCount; ++int2) {
				this.wheelSteering[int2] = buffer[int1++];
				this.wheelRotation[int2] = buffer[int1++];
				this.wheelSkidInfo[int2] = buffer[int1++];
				this.wheelSuspensionLength[int2] = buffer[int1++];
			}

			return true;
		}
	}

	public boolean isConsistent() {
		return INetworkPacket.super.isConsistent() && this.vehicle != null && (GameClient.bClient && !this.hasAuth || GameServer.bServer && this.hasAuth);
	}

	public void process() {
		if (this.isConsistent()) {
			if (GameClient.bClient) {
				this.vehicle.interpolation.interpolationDataAdd(this.vehicle, this);
			} else if (GameServer.bServer) {
				this.vehicle.setClientForce(this.force);
				this.vehicle.setX(this.x);
				this.vehicle.setY(this.y);
				this.vehicle.setZ(this.z);
				this.vehicle.savedRot.x = this.qx;
				this.vehicle.savedRot.y = this.qy;
				this.vehicle.savedRot.z = this.qz;
				this.vehicle.savedRot.w = this.qw;
				this.vehicle.jniTransform.origin.set(this.vehicle.x - WorldSimulation.instance.offsetX, this.vehicle.z, this.vehicle.y - WorldSimulation.instance.offsetY);
				this.vehicle.jniTransform.setRotation(this.vehicle.savedRot);
				this.vehicle.jniLinearVelocity.x = this.vx;
				this.vehicle.jniLinearVelocity.y = this.vy;
				this.vehicle.jniLinearVelocity.z = this.vz;
				this.vehicle.engineSpeed = (double)this.engineSpeed;
				this.vehicle.throttle = this.throttle;
				this.setNumWheels(this.wheelsCount);
				for (int int1 = 0; int1 < this.wheelsCount; ++int1) {
					this.vehicle.wheelInfo[int1].steering = this.wheelSteering[int1];
					this.vehicle.wheelInfo[int1].rotation = this.wheelRotation[int1];
					this.vehicle.wheelInfo[int1].skidInfo = this.wheelSkidInfo[int1];
					this.vehicle.wheelInfo[int1].suspensionLength = this.wheelSuspensionLength[int1];
				}
			}
		} else if (GameClient.bClient) {
			VehicleManager.instance.sendRequestGetFull(this.id, PacketTypes.PacketType.Vehicles);
		}
	}

	public boolean isRelevant(UdpConnection udpConnection) {
		return udpConnection.RelevantTo(this.x, this.y);
	}
}
