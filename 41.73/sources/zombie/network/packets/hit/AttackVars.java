package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.characters.IsoLivingCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoMovingObject;
import zombie.network.packets.INetworkPacket;


public class AttackVars implements INetworkPacket {
	private boolean isBareHeadsWeapon;
	public MovingObject targetOnGround = new MovingObject();
	public boolean bAimAtFloor;
	public boolean bCloseKill;
	public boolean bDoShove;
	public float useChargeDelta;
	public int recoilDelay;
	public final ArrayList targetsStanding = new ArrayList();
	public final ArrayList targetsProne = new ArrayList();

	public void setWeapon(HandWeapon handWeapon) {
		this.isBareHeadsWeapon = "BareHands".equals(handWeapon.getType());
	}

	public HandWeapon getWeapon(IsoLivingCharacter livingCharacter) {
		return !this.isBareHeadsWeapon && livingCharacter.getUseHandWeapon() != null ? livingCharacter.getUseHandWeapon() : livingCharacter.bareHands;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		this.isBareHeadsWeapon = (byte1 & 1) != 0;
		this.bAimAtFloor = (byte1 & 2) != 0;
		this.bCloseKill = (byte1 & 4) != 0;
		this.bDoShove = (byte1 & 8) != 0;
		this.targetOnGround.parse(byteBuffer, udpConnection);
		this.useChargeDelta = byteBuffer.getFloat();
		this.recoilDelay = byteBuffer.getInt();
		byte byte2 = byteBuffer.get();
		this.targetsStanding.clear();
		int int1;
		HitInfo hitInfo;
		for (int1 = 0; int1 < byte2; ++int1) {
			hitInfo = new HitInfo();
			hitInfo.parse(byteBuffer, udpConnection);
			this.targetsStanding.add(hitInfo);
		}

		byte2 = byteBuffer.get();
		this.targetsProne.clear();
		for (int1 = 0; int1 < byte2; ++int1) {
			hitInfo = new HitInfo();
			hitInfo.parse(byteBuffer, udpConnection);
			this.targetsProne.add(hitInfo);
		}
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byte byte1 = 0;
		byte byte2 = (byte)(byte1 | (byte)(this.isBareHeadsWeapon ? 1 : 0));
		byte2 |= (byte)(this.bAimAtFloor ? 2 : 0);
		byte2 |= (byte)(this.bCloseKill ? 4 : 0);
		byte2 |= (byte)(this.bDoShove ? 8 : 0);
		byteBufferWriter.putByte(byte2);
		this.targetOnGround.write(byteBufferWriter);
		byteBufferWriter.putFloat(this.useChargeDelta);
		byteBufferWriter.putInt(this.recoilDelay);
		byte byte3 = (byte)Math.min(100, this.targetsStanding.size());
		byteBufferWriter.putByte(byte3);
		int int1;
		HitInfo hitInfo;
		for (int1 = 0; int1 < byte3; ++int1) {
			hitInfo = (HitInfo)this.targetsStanding.get(int1);
			hitInfo.write(byteBufferWriter);
		}

		byte3 = (byte)Math.min(100, this.targetsProne.size());
		byteBufferWriter.putByte(byte3);
		for (int1 = 0; int1 < byte3; ++int1) {
			hitInfo = (HitInfo)this.targetsProne.get(int1);
			hitInfo.write(byteBufferWriter);
		}
	}

	public int getPacketSizeBytes() {
		int int1 = 11 + this.targetOnGround.getPacketSizeBytes();
		byte byte1 = (byte)Math.min(100, this.targetsStanding.size());
		int int2;
		HitInfo hitInfo;
		for (int2 = 0; int2 < byte1; ++int2) {
			hitInfo = (HitInfo)this.targetsStanding.get(int2);
			int1 += hitInfo.getPacketSizeBytes();
		}

		byte1 = (byte)Math.min(100, this.targetsProne.size());
		for (int2 = 0; int2 < byte1; ++int2) {
			hitInfo = (HitInfo)this.targetsProne.get(int2);
			int1 += hitInfo.getPacketSizeBytes();
		}

		return int1;
	}

	public String getDescription() {
		String string = "";
		byte byte1 = (byte)Math.min(100, this.targetsStanding.size());
		for (int int1 = 0; int1 < byte1; ++int1) {
			HitInfo hitInfo = (HitInfo)this.targetsStanding.get(int1);
			string = string + hitInfo.getDescription();
		}

		String string2 = "";
		byte1 = (byte)Math.min(100, this.targetsProne.size());
		for (int int2 = 0; int2 < byte1; ++int2) {
			HitInfo hitInfo2 = (HitInfo)this.targetsProne.get(int2);
			string2 = string2 + hitInfo2.getDescription();
		}

		boolean boolean1 = this.isBareHeadsWeapon;
		return "\n\tHitInfo [ isBareHeadsWeapon=" + boolean1 + " bAimAtFloor=" + this.bAimAtFloor + " bCloseKill=" + this.bCloseKill + " bDoShove=" + this.bDoShove + " useChargeDelta=" + this.useChargeDelta + " recoilDelay=" + this.recoilDelay + "\n\t  targetOnGround:" + this.targetOnGround.getDescription() + "\n\t  targetsStanding=[" + string + "](size=" + this.targetsStanding.size() + ")\n\t  targetsProne=[" + string2 + "](size=" + this.targetsProne.size() + ")]";
	}

	public void copy(AttackVars attackVars) {
		this.isBareHeadsWeapon = attackVars.isBareHeadsWeapon;
		this.targetOnGround = attackVars.targetOnGround;
		this.bAimAtFloor = attackVars.bAimAtFloor;
		this.bCloseKill = attackVars.bCloseKill;
		this.bDoShove = attackVars.bDoShove;
		this.useChargeDelta = attackVars.useChargeDelta;
		this.recoilDelay = attackVars.recoilDelay;
		this.targetsStanding.clear();
		Iterator iterator = attackVars.targetsStanding.iterator();
		HitInfo hitInfo;
		while (iterator.hasNext()) {
			hitInfo = (HitInfo)iterator.next();
			this.targetsStanding.add(hitInfo);
		}

		this.targetsProne.clear();
		iterator = attackVars.targetsProne.iterator();
		while (iterator.hasNext()) {
			hitInfo = (HitInfo)iterator.next();
			this.targetsProne.add(hitInfo);
		}
	}

	public void clear() {
		this.targetOnGround.setMovingObject((IsoMovingObject)null);
		this.targetsStanding.clear();
		this.targetsProne.clear();
	}
}
