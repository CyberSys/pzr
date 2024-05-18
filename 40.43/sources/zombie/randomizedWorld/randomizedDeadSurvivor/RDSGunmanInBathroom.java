package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;


public class RDSGunmanInBathroom extends RandomizedDeadSurvivorBase {
	private ArrayList weaponsList = new ArrayList();
	private ArrayList ammoList = new ArrayList();

	public void randomizeDeadSurvivor(BuildingDef buildingDef) {
		for (int int1 = 0; int1 < buildingDef.rooms.size(); ++int1) {
			RoomDef roomDef = (RoomDef)buildingDef.rooms.get(int1);
			if ("bathroom".equals(roomDef.name)) {
				IsoDeadBody deadBody = super.createRandomDeadBody(roomDef);
				if (deadBody != null) {
					int int2 = Rand.Next(5, 10);
					deadBody.getContainer().addItem(InventoryItemFactory.CreateItem((String)this.weaponsList.get(Rand.Next(0, this.weaponsList.size()))));
					for (int int3 = 0; int3 < int2; ++int3) {
						deadBody.getContainer().addItem(InventoryItemFactory.CreateItem((String)this.ammoList.get(Rand.Next(0, this.ammoList.size()))));
					}

					return;
				}
			}
		}
	}

	public RDSGunmanInBathroom() {
		this.weaponsList.add("Base.Shotgun");
		this.weaponsList.add("Base.Pistol");
		this.ammoList.add("Base.ShotgunShells");
		this.ammoList.add("Bullets9mm");
		this.ammoList.add("BulletsBox");
	}
}
