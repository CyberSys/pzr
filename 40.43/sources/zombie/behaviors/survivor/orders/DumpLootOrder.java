package zombie.behaviors.survivor.orders;

import zombie.behaviors.survivor.orders.LittleTasks.DumpLootInContainer;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.ItemContainer;
import zombie.iso.areas.IsoBuilding;


class DumpLootOrder extends OrderSequence {

	public DumpLootOrder(IsoGameCharacter gameCharacter, IsoBuilding building) {
		super(gameCharacter);
		if (building.container.size() != 0) {
			ItemContainer itemContainer = (ItemContainer)building.container.get(Rand.Next(building.container.size()));
			this.Orders.add(new GotoNextTo(gameCharacter, itemContainer.parent.square.getX(), itemContainer.parent.square.getY(), itemContainer.parent.square.getZ()));
			this.Orders.add(new DumpLootInContainer(gameCharacter, itemContainer));
		}
	}
}
