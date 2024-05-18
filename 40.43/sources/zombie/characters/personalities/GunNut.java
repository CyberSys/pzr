package zombie.characters.personalities;

import zombie.behaviors.survivor.MasterSurvivorBehavior;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorPersonality;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;


public class GunNut extends SurvivorPersonality {

	public void CreateBehaviours(IsoSurvivor survivor) {
		survivor.setMasterProper(new MasterSurvivorBehavior(survivor));
		survivor.getMasterBehaviorList().addChild(survivor.getMasterProper());
		if (survivor.getPrimaryHandItem() != null) {
			InventoryItem inventoryItem = survivor.getPrimaryHandItem();
			if (inventoryItem instanceof HandWeapon) {
				survivor.setUseHandWeapon((HandWeapon)inventoryItem);
			}
		}

		survivor.getMasterBehaviorList().addChild(survivor.behaviours);
	}

	public int getHuntZombieRange() {
		return 10;
	}
}
