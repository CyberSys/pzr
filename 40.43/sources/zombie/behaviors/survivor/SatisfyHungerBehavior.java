package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;


public class SatisfyHungerBehavior extends Behavior {
	ObtainItemBehavior obtain = new ObtainItemBehavior();

	public Behavior.BehaviorResult process(DecisionPath decisionPath, IsoGameCharacter gameCharacter) {
		this.obtain.FindItem = ItemType.Food;
		this.obtain.Found = false;
		Behavior.BehaviorResult behaviorResult = this.obtain.process(decisionPath, gameCharacter);
		if (behaviorResult == Behavior.BehaviorResult.Succeeded) {
			InventoryItem inventoryItem = gameCharacter.getInventory().Remove(ItemType.Food);
			gameCharacter.Eat(inventoryItem);
			this.reset();
		}

		return behaviorResult;
	}

	public void reset() {
		this.obtain.reset();
		this.obtain.FindItem = ItemType.Food;
		this.obtain.Found = false;
		this.obtain.HaveLocation = false;
		this.obtain.DoneFindItem = false;
		this.obtain.container = null;
	}

	public boolean valid() {
		return true;
	}
}
