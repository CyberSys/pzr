package zombie.iso.objects.interfaces;

import zombie.characters.IsoGameCharacter;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoMovingObject;


public interface Thumpable {

	boolean isDestroyed();

	void Thump(IsoMovingObject movingObject);

	void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon);

	Thumpable getThumpableFor(IsoGameCharacter gameCharacter);

	float getThumpCondition();
}
