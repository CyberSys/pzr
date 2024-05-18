package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;


public class BandageOrder extends Order {
	IsoGameCharacter chr;

	public BandageOrder(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.chr = gameCharacter;
	}

	public Behavior.BehaviorResult process() {
		return Behavior.BehaviorResult.Succeeded;
	}

	public void initOrder() {
		InventoryItem inventoryItem = this.chr.getInventory().getBestBandage(this.chr.getDescriptor());
		if (inventoryItem != null) {
			this.chr.getBodyDamage().UseBandageOnMostNeededPart();
			inventoryItem.Use();
		}
	}

	public boolean complete() {
		return true;
	}

	public void update() {
	}
}
