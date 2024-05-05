package zombie.characters;

import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.BodyDamage.BodyDamage;
import zombie.inventory.types.HandWeapon;
import zombie.vehicles.BaseVehicle;


public interface ILuaGameCharacterDamage {

	BodyDamage getBodyDamage();

	BodyDamage getBodyDamageRemote();

	float getHealth();

	void setHealth(float float1);

	float Hit(BaseVehicle baseVehicle, float float1, boolean boolean1, float float2, float float3);

	float Hit(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2);

	float Hit(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2, boolean boolean2);

	boolean isOnFire();

	void StopBurning();

	void sendStopBurning();

	int getLastHitCount();

	void setLastHitCount(int int1);

	boolean addHole(BloodBodyPartType bloodBodyPartType);

	void addBlood(BloodBodyPartType bloodBodyPartType, boolean boolean1, boolean boolean2, boolean boolean3);

	boolean isBumped();

	String getBumpType();

	boolean isOnDeathDone();

	void setOnDeathDone(boolean boolean1);

	boolean isOnKillDone();

	void setOnKillDone(boolean boolean1);

	boolean isDeathDragDown();

	void setDeathDragDown(boolean boolean1);

	boolean isPlayingDeathSound();

	void setPlayingDeathSound(boolean boolean1);
}
