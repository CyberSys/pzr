package zombie.behaviors.survivor.orders;

import zombie.characters.IsoGameCharacter;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoRoom;


class LootRoom extends OrderSequence {
	IsoGameCharacter chr;
	IsoRoom room;
	LootBuilding.LootStyle style;

	public LootRoom(IsoGameCharacter gameCharacter, IsoRoom room, LootBuilding.LootStyle lootStyle) {
		super(gameCharacter);
		this.style = lootStyle;
		this.room = room;
		this.chr = gameCharacter;
		for (int int1 = 0; int1 < room.Containers.size(); ++int1) {
			ItemContainer itemContainer = (ItemContainer)room.Containers.get(int1);
			IsoGridSquare square = itemContainer.parent.square;
			this.Orders.add(new GotoNextTo(gameCharacter, square.getX(), square.getY(), square.getZ()));
			this.Orders.add(new LootContainer(gameCharacter, itemContainer, lootStyle));
		}
	}

	public void init() {
	}
}
