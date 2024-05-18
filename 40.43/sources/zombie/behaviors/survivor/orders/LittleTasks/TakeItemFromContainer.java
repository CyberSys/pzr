package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;


public class TakeItemFromContainer extends Order {
	IsoGameCharacter chr;
	ItemContainer con;
	String type;

	public TakeItemFromContainer(IsoGameCharacter gameCharacter, ItemContainer itemContainer, String string) {
		super(gameCharacter);
		this.chr = gameCharacter;
		this.con = itemContainer;
		this.type = string;
	}

	public Behavior.BehaviorResult process() {
		if (this.type.contains("Type:")) {
			InventoryItem inventoryItem;
			if (this.type.contains("Food")) {
				inventoryItem = this.con.getBestFood(this.chr.getDescriptor());
				this.con.Remove(inventoryItem);
				this.chr.getInventory().AddItem(inventoryItem);
			}

			if (this.type.contains("Weapon")) {
				inventoryItem = this.con.getBestWeapon(this.chr.getDescriptor());
				this.con.Remove(inventoryItem);
				this.chr.getInventory().AddItem(inventoryItem);
			}
		}

		for (int int1 = 0; int1 < this.con.Items.size(); ++int1) {
			InventoryItem inventoryItem2 = (InventoryItem)this.con.Items.get(int1);
			if (inventoryItem2.getType().equals(this.type)) {
				this.con.Remove(inventoryItem2);
				this.chr.getInventory().AddItem(inventoryItem2);
			}
		}

		return Behavior.BehaviorResult.Succeeded;
	}

	public boolean complete() {
		return true;
	}

	public void update() {
	}
}
