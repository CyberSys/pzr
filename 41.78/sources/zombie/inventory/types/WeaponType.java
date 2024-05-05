package zombie.inventory.types;

import java.util.Arrays;
import java.util.List;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;



public enum WeaponType {

	barehand,
	twohanded,
	onehanded,
	heavy,
	knife,
	spear,
	handgun,
	firearm,
	throwing,
	chainsaw,
	type,
	possibleAttack,
	canMiss,
	isRanged;

	private WeaponType(String string, List list, boolean boolean1, boolean boolean2) {
		this.type = string;
		this.possibleAttack = list;
		this.canMiss = boolean1;
		this.isRanged = boolean2;
	}
	public static WeaponType getWeaponType(HandWeapon handWeapon) {
		WeaponType weaponType = null;
		if (handWeapon.getSwingAnim().equalsIgnoreCase("Stab")) {
			return knife;
		} else if (handWeapon.getSwingAnim().equalsIgnoreCase("Heavy")) {
			return heavy;
		} else if (handWeapon.getSwingAnim().equalsIgnoreCase("Throw")) {
			return throwing;
		} else {
			if (!handWeapon.isRanged()) {
				weaponType = onehanded;
				if (handWeapon.isTwoHandWeapon()) {
					weaponType = twohanded;
					if (handWeapon.getSwingAnim().equalsIgnoreCase("Spear")) {
						return spear;
					}

					if ("Chainsaw".equals(handWeapon.getType())) {
						return chainsaw;
					}
				}
			} else {
				weaponType = handgun;
				if (handWeapon.isTwoHandWeapon()) {
					weaponType = firearm;
				}
			}

			if (weaponType == null) {
				weaponType = barehand;
			}

			return weaponType;
		}
	}
	public static WeaponType getWeaponType(IsoGameCharacter gameCharacter) {
		if (gameCharacter == null) {
			return null;
		} else {
			WeaponType weaponType = null;
			gameCharacter.setVariable("rangedWeapon", false);
			InventoryItem inventoryItem = gameCharacter.getPrimaryHandItem();
			InventoryItem inventoryItem2 = gameCharacter.getSecondaryHandItem();
			if (inventoryItem != null && inventoryItem instanceof HandWeapon) {
				if (inventoryItem.getSwingAnim().equalsIgnoreCase("Stab")) {
					return knife;
				}

				if (inventoryItem.getSwingAnim().equalsIgnoreCase("Heavy")) {
					return heavy;
				}

				if (inventoryItem.getSwingAnim().equalsIgnoreCase("Throw")) {
					gameCharacter.setVariable("rangedWeapon", true);
					return throwing;
				}

				if (!((HandWeapon)inventoryItem).isRanged()) {
					weaponType = onehanded;
					if (inventoryItem == inventoryItem2 && inventoryItem.isTwoHandWeapon()) {
						weaponType = twohanded;
						if (inventoryItem.getSwingAnim().equalsIgnoreCase("Spear")) {
							return spear;
						}

						if ("Chainsaw".equals(inventoryItem.getType())) {
							return chainsaw;
						}
					}
				} else {
					weaponType = handgun;
					if (inventoryItem == inventoryItem2 && inventoryItem.isTwoHandWeapon()) {
						weaponType = firearm;
					}
				}
			}

			if (weaponType == null) {
				weaponType = barehand;
			}

			gameCharacter.setVariable("rangedWeapon", weaponType == handgun || weaponType == firearm);
			return weaponType;
		}
	}
	public String getType() {
		return this.type;
	}
	private static WeaponType[] $values() {
		return new WeaponType[]{barehand, twohanded, onehanded, heavy, knife, spear, handgun, firearm, throwing, chainsaw};
	}
}
