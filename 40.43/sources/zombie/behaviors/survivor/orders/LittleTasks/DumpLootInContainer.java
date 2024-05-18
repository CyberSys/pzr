package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.LootBuilding;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;


public class DumpLootInContainer extends Order {
	IsoGameCharacter chr;
	ItemContainer con;

	public DumpLootInContainer(IsoGameCharacter gameCharacter, ItemContainer itemContainer) {
		super(gameCharacter);
		this.chr = gameCharacter;
		this.con = itemContainer;
	}

	public Behavior.BehaviorResult process() {
		for (int int1 = 0; int1 < this.chr.getInventory().Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.chr.getInventory().Items.get(int1);
			boolean boolean1 = ((IsoSurvivor)this.chr).SatisfiedWithInventory(LootBuilding.LootStyle.Safehouse, IsoSurvivor.SatisfiedBy.Food);
			boolean boolean2 = ((IsoSurvivor)this.chr).SatisfiedWithInventory(LootBuilding.LootStyle.Safehouse, IsoSurvivor.SatisfiedBy.Weapons);
			int int2 = this.chr.getInventory().getWaterContainerCount();
			if (inventoryItem.CanStoreWater) {
				if (int2 > 2) {
					this.chr.getInventory().Remove(inventoryItem);
					this.con.AddItem(inventoryItem);
				}
			} else if (boolean1 && inventoryItem instanceof Food) {
				this.chr.getInventory().Remove(inventoryItem);
				this.con.AddItem(inventoryItem);
			} else if (boolean2 && inventoryItem instanceof HandWeapon) {
				this.chr.getInventory().Remove(inventoryItem);
				this.con.AddItem(inventoryItem);
			} else if (!(inventoryItem instanceof HandWeapon) && !(inventoryItem instanceof Food)) {
				this.chr.getInventory().Remove(inventoryItem);
				this.con.AddItem(inventoryItem);
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
