package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemType;


public class FindKnownItemBehavior extends Behavior {
	public ItemType FindItem;
	public boolean Found;
	public boolean LocationIsInventory;
	ItemContainer container;

	public FindKnownItemBehavior() {
		this.FindItem = ItemType.None;
		this.Found = false;
		this.LocationIsInventory = false;
	}

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		this.Found = false;
		if (gameCharacter.getInventory().HasType(this.FindItem)) {
			this.LocationIsInventory = true;
			this.Found = true;
			return Behavior.BehaviorResult.Succeeded;
		} else {
			this.LocationIsInventory = false;
			if (gameCharacter.getCurrentSquare().getRoom() != null) {
				this.container = gameCharacter.getCurrentSquare().getRoom().building.getContainerWith(this.FindItem);
				if (this.container != null) {
					this.Found = true;
					return Behavior.BehaviorResult.Succeeded;
				}
			}

			return Behavior.BehaviorResult.Failed;
		}
	}

	public void reset() {
	}

	public boolean valid() {
		return true;
	}
}
