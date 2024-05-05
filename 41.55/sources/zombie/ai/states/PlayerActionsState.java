package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;


public final class PlayerActionsState extends State {
	private static final PlayerActionsState _instance = new PlayerActionsState();

	public static PlayerActionsState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		InventoryItem inventoryItem = gameCharacter.getPrimaryHandItem();
		InventoryItem inventoryItem2 = gameCharacter.getSecondaryHandItem();
		if (!(inventoryItem instanceof HandWeapon) && !(inventoryItem2 instanceof HandWeapon)) {
			gameCharacter.setHideWeaponModel(true);
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
		gameCharacter.setHideWeaponModel(false);
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
	}
}
