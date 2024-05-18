package zombie.behaviors.survivor.orders;

import zombie.behaviors.Behavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;


class LootContainer extends Order {
	IsoGameCharacter chr;
	ItemContainer con;
	LootBuilding.LootStyle style;

	public LootContainer(IsoGameCharacter gameCharacter, ItemContainer itemContainer, LootBuilding.LootStyle lootStyle) {
		super(gameCharacter);
		this.chr = gameCharacter;
		this.con = itemContainer;
		this.style = lootStyle;
	}

	public Behavior.BehaviorResult process() {
		for (int int1 = 0; int1 < this.con.Items.size(); ++int1) {
			InventoryItem inventoryItem = (InventoryItem)this.con.Items.get(int1);
			if (inventoryItem.CanStoreWater) {
				this.con.Remove(inventoryItem);
				this.chr.getInventory().AddItem(inventoryItem);
			} else if ((!(inventoryItem instanceof Food) || ((IsoSurvivor)this.chr).SatisfiedWithInventory(this.style, IsoSurvivor.SatisfiedBy.Food)) && this.style != LootBuilding.LootStyle.Extreme && !this.chr.getDescriptor().getGroup().HasNeed("Type:Food")) {
				if ((!(inventoryItem instanceof HandWeapon) || ((IsoSurvivor)this.chr).SatisfiedWithInventory(this.style, IsoSurvivor.SatisfiedBy.Weapons)) && this.style != LootBuilding.LootStyle.Extreme && !this.chr.getDescriptor().getGroup().HasNeed("Type:Weapon")) {
					if (this.chr.getDescriptor().getGroup().HasNeed(inventoryItem.getType())) {
						this.con.Remove(inventoryItem);
						this.chr.getInventory().AddItem(inventoryItem);
					}
				} else {
					this.con.Remove(inventoryItem);
					this.chr.getInventory().AddItem(inventoryItem);
				}
			} else {
				this.con.Remove(inventoryItem);
				this.chr.getInventory().AddItem(inventoryItem);
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
