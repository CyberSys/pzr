package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.SwipeStatePlayer;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.packets.INetworkPacket;


public class WeaponHit extends Hit implements INetworkPacket {
	protected float range;
	protected boolean hitHead;

	public void set(boolean boolean1, float float1, float float2, float float3, float float4, float float5, boolean boolean2) {
		super.set(boolean1, float1, float3, float4, float5);
		this.range = float2;
		this.hitHead = boolean2;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		super.parse(byteBuffer, udpConnection);
		this.range = byteBuffer.getFloat();
		this.hitHead = byteBuffer.get() != 0;
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		super.write(byteBufferWriter);
		byteBufferWriter.putFloat(this.range);
		byteBufferWriter.putBoolean(this.hitHead);
	}

	public String getDescription() {
		String string = super.getDescription();
		return string + "\n\tWeapon [ range=" + this.range + " | hitHead=" + this.hitHead + " ]";
	}

	void process(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, HandWeapon handWeapon) {
		gameCharacter2.Hit(handWeapon, gameCharacter, this.damage, this.ignore, this.range, true);
		super.process(gameCharacter, gameCharacter2);
		LuaEventManager.triggerEvent("OnWeaponHitXp", gameCharacter, handWeapon, gameCharacter2, this.damage);
		if (gameCharacter.isAimAtFloor() && !handWeapon.isRanged() && gameCharacter.isNPC()) {
			SwipeStatePlayer.splash(gameCharacter2, handWeapon, gameCharacter);
		}

		if (this.hitHead) {
			SwipeStatePlayer.splash(gameCharacter2, handWeapon, gameCharacter);
			SwipeStatePlayer.splash(gameCharacter2, handWeapon, gameCharacter);
			gameCharacter2.addBlood(BloodBodyPartType.Head, true, true, true);
			gameCharacter2.addBlood(BloodBodyPartType.Torso_Upper, true, false, false);
			gameCharacter2.addBlood(BloodBodyPartType.UpperArm_L, true, false, false);
			gameCharacter2.addBlood(BloodBodyPartType.UpperArm_R, true, false, false);
		}

		if ((!((IsoLivingCharacter)gameCharacter).bDoShove || gameCharacter.isAimAtFloor()) && gameCharacter.DistToSquared(gameCharacter2) < 2.0F && Math.abs(gameCharacter.z - gameCharacter2.z) < 0.5F) {
			gameCharacter.addBlood((BloodBodyPartType)null, false, false, false);
		}

		if (!gameCharacter2.isDead() && !(gameCharacter2 instanceof IsoPlayer) && (!((IsoLivingCharacter)gameCharacter).bDoShove || gameCharacter.isAimAtFloor())) {
			SwipeStatePlayer.splash(gameCharacter2, handWeapon, gameCharacter);
		}
	}
}
