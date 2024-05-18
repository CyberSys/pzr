package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoObject;


public class UseItemOnIsoObject extends Order {
	IsoGameCharacter chr;
	String inv = null;
	IsoObject obj;

	public UseItemOnIsoObject(IsoGameCharacter gameCharacter, String string, IsoObject object) {
		super(gameCharacter);
		this.chr = gameCharacter;
		this.inv = string;
		this.obj = object;
	}

	public Behavior.BehaviorResult process() {
		return Behavior.BehaviorResult.Succeeded;
	}

	public void initOrder() {
		InventoryItem inventoryItem = this.chr.getInventory().FindAndReturn(this.inv);
		if (inventoryItem != null) {
			this.obj.useItemOn(inventoryItem);
			inventoryItem.Use();
		}
	}

	public boolean complete() {
		return true;
	}

	public void update() {
	}
}
