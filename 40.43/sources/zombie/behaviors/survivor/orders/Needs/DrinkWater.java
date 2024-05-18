package zombie.behaviors.survivor.orders.Needs;

import java.util.ArrayList;
import java.util.Iterator;
import zombie.behaviors.survivor.orders.GotoBuildingOrder;
import zombie.behaviors.survivor.orders.GotoNextTo;
import zombie.behaviors.survivor.orders.Order;
import zombie.behaviors.survivor.orders.OrderSequence;
import zombie.behaviors.survivor.orders.LittleTasks.GotoRoomOrder;
import zombie.behaviors.survivor.orders.LittleTasks.UseItemOnIsoObject;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.IsoRoomExit;


public class DrinkWater extends OrderSequence {
	static ArrayList choices = new ArrayList();

	public DrinkWater(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.type = "DrinkWater";
	}

	public void initOrder() {
		IsoBuilding building = null;
		if (this.character.getCurrentSquare().getRoom() != null && this.character.getCurrentSquare().getRoom().building.hasWater()) {
			building = this.character.getCurrentSquare().getRoom().building;
		}

		if (building == null && this.character.getDescriptor().getGroup().Safehouse != null && this.character.getDescriptor().getGroup().Safehouse.hasWater()) {
			building = this.character.getDescriptor().getGroup().Safehouse;
		}

		IsoBuilding building2;
		if (building == null) {
			Iterator iterator = IsoWorld.instance.getCell().getBuildingList().iterator();
			while (iterator != null && iterator.hasNext()) {
				building2 = (IsoBuilding)iterator.next();
				if (building2.hasWater()) {
					choices.add(building2);
				}
			}
		}

		float float1 = 1.0E7F;
		for (int int1 = 0; int1 < choices.size(); ++int1) {
			IsoBuilding building3 = (IsoBuilding)choices.get(int1);
			for (int int2 = 0; int2 < building3.Exits.size(); ++int2) {
				float float2 = IsoUtils.DistanceManhatten((float)((IsoRoomExit)building3.Exits.get(int2)).x, (float)((IsoRoomExit)building3.Exits.get(int2)).y, this.character.x, this.character.y);
				if (float2 < float1) {
					building = building3;
					break;
				}
			}
		}

		choices.clear();
		building2 = null;
		if (building != null) {
			if (this.character.getCurrentBuilding() != building) {
				this.Orders.add(new GotoBuildingOrder(this.character, building));
			}

			Iterator iterator2 = building.Rooms.iterator();
			while (iterator2 != null && iterator2.hasNext()) {
				IsoRoom room = (IsoRoom)iterator2.next();
				if (!room.WaterSources.isEmpty()) {
					IsoObject object = null;
					for (int int3 = 0; int3 < room.WaterSources.size(); ++int3) {
						if (((IsoObject)room.WaterSources.get(int3)).hasWater()) {
							object = (IsoObject)room.WaterSources.get(int3);
							break;
						}
					}

					if (object != null) {
						GotoRoomOrder gotoRoomOrder = new GotoRoomOrder(this.character, room);
						this.Orders.add(gotoRoomOrder);
						if (this.character.getInventory().getWaterContainerCount() > 0) {
							this.Orders.add(new GotoNextTo(this.character, object.square.getX(), object.square.getY(), object.square.getZ()));
							ArrayList arrayList = this.character.getInventory().getAllWaterFillables();
							for (int int4 = 0; int4 < arrayList.size(); ++int4) {
								this.Orders.add(new UseItemOnIsoObject(this.character, ((InventoryItem)arrayList.get(int4)).getType(), object));
							}
						}

						return;
					}
				}
			}
		}
	}

	public boolean ActedThisFrame() {
		return ((Order)this.Orders.get(this.ID)).ActedThisFrame();
	}
}
