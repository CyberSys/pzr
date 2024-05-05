package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.packets.INetworkPacket;


public abstract class HitCharacterPacket implements INetworkPacket {
	private final HitCharacterPacket.HitType hitType;

	public HitCharacterPacket(HitCharacterPacket.HitType hitType) {
		this.hitType = hitType;
	}

	public static HitCharacterPacket process(ByteBuffer byteBuffer) {
		byte byte1 = byteBuffer.get();
		if (byte1 > HitCharacterPacket.HitType.Min.ordinal() && byte1 < HitCharacterPacket.HitType.Max.ordinal()) {
			Object object;
			switch (HitCharacterPacket.HitType.values()[byte1]) {
			case PlayerHitSquare: 
				object = new PlayerHitSquarePacket();
				break;
			
			case PlayerHitVehicle: 
				object = new PlayerHitVehiclePacket();
				break;
			
			case PlayerHitZombie: 
				object = new PlayerHitZombiePacket();
				break;
			
			case PlayerHitPlayer: 
				object = new PlayerHitPlayerPacket();
				break;
			
			case ZombieHitPlayer: 
				object = new ZombieHitPlayerPacket();
				break;
			
			case VehicleHitZombie: 
				object = new VehicleHitZombiePacket();
				break;
			
			case VehicleHitPlayer: 
				object = new VehicleHitPlayerPacket();
				break;
			
			default: 
				object = null;
			
			}

			return (HitCharacterPacket)object;
		} else {
			return null;
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putByte((byte)this.hitType.ordinal());
	}

	public String getDescription() {
		String string = INetworkPacket.super.getDescription();
		return string + " (" + this.hitType.name() + ")";
	}

	public String getHitDescription() {
		String string = INetworkPacket.super.getDescription();
		return string + " (" + this.hitType.name() + ")";
	}

	public void tryProcess() {
		if (!GameClient.bClient || !HitCharacterPacket.HitType.VehicleHitZombie.equals(this.hitType) && !HitCharacterPacket.HitType.VehicleHitPlayer.equals(this.hitType)) {
			this.tryProcessInternal();
		} else {
			this.postpone();
		}
	}

	public void tryProcessInternal() {
		if (this.isConsistent()) {
			this.preProcess();
			this.process();
			this.postProcess();
			this.attack();
			this.react();
		} else {
			DebugLog.Multiplayer.warn("HitCharacter: check error");
		}
	}

	public abstract boolean isRelevant(UdpConnection udpConnection);

	protected abstract void attack();

	protected abstract void react();

	protected void preProcess() {
	}

	protected void process() {
	}

	protected void postProcess() {
	}

	protected void postpone() {
	}

	public abstract boolean validate(UdpConnection udpConnection);

	public static enum HitType {

		Min,
		PlayerHitSquare,
		PlayerHitVehicle,
		PlayerHitZombie,
		PlayerHitPlayer,
		ZombieHitPlayer,
		VehicleHitZombie,
		VehicleHitPlayer,
		Max;

		private static HitCharacterPacket.HitType[] $values() {
			return new HitCharacterPacket.HitType[]{Min, PlayerHitSquare, PlayerHitVehicle, PlayerHitZombie, PlayerHitPlayer, ZombieHitPlayer, VehicleHitZombie, VehicleHitPlayer, Max};
		}
	}
}
