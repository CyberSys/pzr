package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Food;


public class EatFoodOrder extends Order {
	IsoGameCharacter chr;

	public EatFoodOrder(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.chr = gameCharacter;
	}

	public Behavior.BehaviorResult process() {
		return Behavior.BehaviorResult.Succeeded;
	}

	public void initOrder() {
		InventoryItem inventoryItem = this.chr.getInventory().getBestFood(this.chr.getDescriptor());
		if (inventoryItem != null) {
			this.chr.Eat(inventoryItem);
			this.chr.getBodyDamage().JustAteFood((Food)inventoryItem);
			inventoryItem.Use();
		}
	}

	public boolean complete() {
		return true;
	}

	public void update() {
	}
}
