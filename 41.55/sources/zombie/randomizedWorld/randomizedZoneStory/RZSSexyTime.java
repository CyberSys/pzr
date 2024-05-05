package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;


public class RZSSexyTime extends RandomizedZoneStoryBase {
	private final ArrayList pantsMaleItems = new ArrayList();
	private final ArrayList pantsFemaleItems = new ArrayList();
	private final ArrayList topItems = new ArrayList();
	private final ArrayList shoesItems = new ArrayList();

	public RZSSexyTime() {
		this.name = "Sexy Time";
		this.chance = 5;
		this.minZoneHeight = 5;
		this.minZoneWidth = 5;
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Beach.toString());
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Forest.toString());
		this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Lake.toString());
		this.shoesItems.add("Base.Shoes_Random");
		this.shoesItems.add("Base.Shoes_TrainerTINT");
		this.pantsMaleItems.add("Base.TrousersMesh_DenimLight");
		this.pantsMaleItems.add("Base.Trousers_DefaultTEXTURE_TINT");
		this.pantsMaleItems.add("Base.Trousers_Denim");
		this.pantsFemaleItems.add("Base.Skirt_Knees");
		this.pantsFemaleItems.add("Base.Skirt_Long");
		this.pantsFemaleItems.add("Base.Skirt_Short");
		this.pantsFemaleItems.add("Base.Skirt_Normal");
		this.topItems.add("Base.Shirt_FormalWhite");
		this.topItems.add("Base.Shirt_FormalWhite_ShortSleeve");
		this.topItems.add("Base.Tshirt_DefaultTEXTURE_TINT");
		this.topItems.add("Base.Tshirt_PoloTINT");
		this.topItems.add("Base.Tshirt_WhiteLongSleeveTINT");
		this.topItems.add("Base.Tshirt_WhiteTINT");
	}

	public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
		this.cleanAreaForStory(this, zone);
		BaseVehicle baseVehicle = this.addVehicle(zone, this.getSq(zone.pickedXForZoneStory, zone.pickedYForZoneStory, zone.z), (IsoChunk)null, (String)null, "Base.VanAmbulance", (Integer)null, (IsoDirections)null, (String)null);
		boolean boolean1 = Rand.Next(7) == 0;
		boolean boolean2 = Rand.Next(7) == 0;
		if (boolean1) {
			this.addItemsOnGround(zone, true, baseVehicle);
			this.addItemsOnGround(zone, true, baseVehicle);
		} else if (boolean2) {
			this.addItemsOnGround(zone, false, baseVehicle);
			this.addItemsOnGround(zone, false, baseVehicle);
		} else {
			this.addItemsOnGround(zone, true, baseVehicle);
			this.addItemsOnGround(zone, false, baseVehicle);
		}
	}

	private void addItemsOnGround(IsoMetaGrid.Zone zone, boolean boolean1, BaseVehicle baseVehicle) {
		byte byte1 = 100;
		if (!boolean1) {
			byte1 = 0;
		}

		ArrayList arrayList = this.addZombiesOnVehicle(1, "Naked", Integer.valueOf(byte1), baseVehicle);
		if (!arrayList.isEmpty()) {
			IsoZombie zombie = (IsoZombie)arrayList.get(0);
			this.addRandomItemOnGround(zombie.getSquare(), this.shoesItems);
			this.addRandomItemOnGround(zombie.getSquare(), this.topItems);
			this.addRandomItemOnGround(zombie.getSquare(), boolean1 ? this.pantsMaleItems : this.pantsFemaleItems);
		}
	}
}
