package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoObject;


public final class PlayerStrafeState extends State {
	private static final PlayerStrafeState _instance = new PlayerStrafeState();

	public static PlayerStrafeState instance() {
		return _instance;
	}

	public void enter(IsoGameCharacter gameCharacter) {
		if (!"aim".equals(gameCharacter.getPreviousActionContextStateName())) {
			InventoryItem inventoryItem = gameCharacter.getPrimaryHandItem();
			if (inventoryItem != null && inventoryItem.getBringToBearSound() != null) {
				gameCharacter.getEmitter().playSoundImpl(inventoryItem.getBringToBearSound(), (IsoObject)null);
			}
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
	}

	public void exit(IsoGameCharacter gameCharacter) {
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
	}
}
