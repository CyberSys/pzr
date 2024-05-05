package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;


public class VehicleHit extends Hit implements IMovable,INetworkPacket {
	public int vehicleDamage;
	public float vehicleSpeed;
	public boolean isVehicleHitFromBehind;
	public boolean isTargetHitFromBehind;

	public void set(boolean boolean1, float float1, float float2, float float3, float float4, int int1, float float5, boolean boolean2, boolean boolean3) {
		super.set(boolean1, float1, float2, float3, float4);
		this.vehicleDamage = int1;
		this.vehicleSpeed = float5;
		this.isVehicleHitFromBehind = boolean2;
		this.isTargetHitFromBehind = boolean3;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.vehicleDamage = byteBuffer.getInt();
		this.vehicleSpeed = byteBuffer.getFloat();
		this.isVehicleHitFromBehind = byteBuffer.get() != 0;
		this.isTargetHitFromBehind = byteBuffer.get() != 0;
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		byteBufferWriter.putInt(this.vehicleDamage);
		byteBufferWriter.putFloat(this.vehicleSpeed);
		byteBufferWriter.putBoolean(this.isVehicleHitFromBehind);
		byteBufferWriter.putBoolean(this.isTargetHitFromBehind);
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tVehicle [ speed=" + this.vehicleSpeed + " | damage=" + this.vehicleDamage + " | target-hit=" + (this.isTargetHitFromBehind ? "FRONT" : "BEHIND") + " | vehicle-hit=" + (this.isVehicleHitFromBehind ? "FRONT" : "REAR") + " ]";
	}

	void process(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, BaseVehicle baseVehicle) {
		super.process(gameCharacter, gameCharacter2);
		if (GameServer.bServer) {
			if (this.vehicleDamage != 0) {
				if (this.isVehicleHitFromBehind) {
					baseVehicle.addDamageFrontHitAChr(this.vehicleDamage);
				} else {
					baseVehicle.addDamageRearHitAChr(this.vehicleDamage);
				}

				baseVehicle.transmitBlood();
			}
		} else if (GameClient.bClient) {
			if (gameCharacter2 instanceof IsoZombie) {
				((IsoZombie)gameCharacter2).applyDamageFromVehicle(this.vehicleSpeed, this.damage);
			} else if (gameCharacter2 instanceof IsoPlayer) {
				((IsoPlayer)gameCharacter2).getDamageFromHitByACar(this.vehicleSpeed);
				((IsoPlayer)gameCharacter2).actionContext.reportEvent("washit");
				gameCharacter2.setVariable("hitpvp", false);
			}
		}
	}

	public float getSpeed() {
		return this.vehicleSpeed;
	}

	public boolean isVehicle() {
		return true;
	}
}
