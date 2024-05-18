package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.iso.IsoCell;
import zombie.iso.IsoWorld;
import zombie.iso.areas.BuildingScore;
import zombie.iso.areas.IsoBuilding;


public class LootMission extends OrderSequence {
	IsoBuilding b;
	IsoGameCharacter chr;

	public LootMission(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.chr = gameCharacter;
	}

	public void initOrder() {
		this.character = this.chr;
		Stack stack = IsoWorld.instance.CurrentCell.getBestBuildings(IsoCell.BuildingSearchCriteria.General, 8);
		float float1 = -1.0F;
		IsoBuilding building = null;
		for (int int1 = 0; int1 < stack.size(); ++int1) {
			if (((BuildingScore)stack.get(int1)).building == ((IsoSurvivor)this.chr).getDescriptor().getGroup().Safehouse) {
				stack.remove(int1);
				--int1;
			} else {
				float float2 = ((BuildingScore)stack.get(int1)).building.getNeedsScore(((IsoSurvivor)this.chr).getDescriptor().getGroup());
				if (float2 > float1) {
					float1 = float2;
					building = ((BuildingScore)stack.get(int1)).building;
				}
			}
		}

		if (building != null && !building.Exits.isEmpty()) {
			if (!this.character.IsInBuilding(((IsoSurvivor)this.chr).getDescriptor().getGroup().Safehouse)) {
				this.Orders.add(new GotoBuildingOrder(this.chr, ((IsoSurvivor)this.chr).getDescriptor().getGroup().Safehouse));
			}

			this.Orders.add(new CallMeetingOrder(this.chr));
			this.Orders.add(new ArrangeLootingTeamOrder(this.chr, "Base.ArrangeLooting"));
			this.Orders.add(new EndMeetingOrder(this.chr));
			this.Orders.add(new GotoBuildingOrder(this.chr, building));
			this.Orders.add(new LootBuilding(this.chr, building, LootBuilding.LootStyle.Extreme));
			building = ((IsoSurvivor)this.chr).getDescriptor().getGroup().Safehouse;
			this.Orders.add(new GotoSafehouseOrder(this.chr));
			this.Orders.add(new SecureSafehouse(this.chr));
			this.Orders.add(new ReturnToSafehouseConversation(this.chr, "Base.BackWithLoot"));
			this.Orders.add(new DumpLootOrder(this.chr, building));
		}
	}
}
