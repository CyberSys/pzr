package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.characters.SurvivorDesc;
import zombie.core.Rand;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;


public class LootBuilding extends OrderSequence {
	IsoGameCharacter chr;
	IsoBuilding building;
	LootBuilding.LootStyle style;

	public LootBuilding(IsoGameCharacter gameCharacter, IsoBuilding building, LootBuilding.LootStyle lootStyle) {
		super(gameCharacter);
		this.building = building;
		this.style = lootStyle;
		this.chr = gameCharacter;
		Stack stack = new Stack();
		stack.addAll(building.Rooms);
		for (int int1 = 0; int1 < stack.size(); ++int1) {
			if (((IsoRoom)stack.get(int1)).Containers.isEmpty()) {
				stack.remove(int1);
				--int1;
			}
		}

		while (!stack.isEmpty()) {
			IsoRoom room = (IsoRoom)stack.get(Rand.Next(stack.size()));
			this.Orders.add(new LootRoom(gameCharacter, room, lootStyle));
			stack.remove(room);
		}
	}

	public boolean complete() {
		return this.chr.getInventoryWeight() + 10.0F >= (float)(this.chr.getMaxWeight() / 2) ? true : super.complete();
	}

	public void initOrder() {
		if (((IsoSurvivor)this.chr).getDescriptor().getGroup().Leader == this.chr.getDescriptor()) {
			for (int int1 = 0; int1 < this.chr.getDescriptor().getGroup().Members.size(); ++int1) {
				SurvivorDesc survivorDesc = (SurvivorDesc)this.chr.getDescriptor().getGroup().Members.get(int1);
				if (survivorDesc.getInstance() == null) {
					return;
				}

				if (survivorDesc.getInstance().getOrder() instanceof FollowOrder) {
					survivorDesc.getInstance().GiveOrder(new LootBuilding(survivorDesc.getInstance(), this.building, this.style), false);
				}
			}
		}
	}
	public static enum LootStyle {

		Safehouse,
		Medium,
		Extreme;
	}
}
