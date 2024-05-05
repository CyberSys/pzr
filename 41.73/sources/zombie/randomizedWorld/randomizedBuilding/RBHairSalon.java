package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public final class RBHairSalon extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null && this.roomValid(square)) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (Rand.NextBool(3) && object.getSurfaceOffsetNoTable() > 0.0F && square.getProperties().Val("waterAmount") == null && !object.hasWater() && object.getProperties().Val("BedType") == null) {
								int int5 = Rand.Next(12);
								switch (int5) {
								case 0: 
									this.addWorldItem("Comb", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 1: 
									this.addWorldItem("HairDyeBlonde", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 2: 
									this.addWorldItem("HairDyeBlack", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 3: 
									this.addWorldItem("HairDyeWhite", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
								
								case 4: 
								
								default: 
									break;
								
								case 5: 
									this.addWorldItem("HairDyePink", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 6: 
									this.addWorldItem("HairDyeYellow", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 7: 
									this.addWorldItem("HairDyeRed", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 8: 
									this.addWorldItem("HairDyeGinger", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 9: 
									this.addWorldItem("Hairgel", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 10: 
									this.addWorldItem("Hairspray", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
									break;
								
								case 11: 
									this.addWorldItem("Razor", square, 0.5F, 0.5F, object.getSurfaceOffsetNoTable() / 96.0F);
								
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean roomValid(IsoGridSquare square) {
		return square.getRoom() != null && "aesthetic".equals(square.getRoom().getName());
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return buildingDef.getRoom("aesthetic") != null || boolean1;
	}

	public RBHairSalon() {
		this.name = "Hair Salon";
		this.setAlwaysDo(true);
	}
}
