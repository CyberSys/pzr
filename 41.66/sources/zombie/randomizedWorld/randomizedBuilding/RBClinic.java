package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public final class RBClinic extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null && this.roomValid(square)) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (Rand.NextBool(2) && object.getSurfaceOffsetNoTable() > 0.0F && object.getContainer() == null && square.getProperties().Val("waterAmount") == null && !object.hasWater()) {
								int int5 = Rand.Next(1, 3);
								for (int int6 = 0; int6 < int5; ++int6) {
									int int7 = Rand.Next(12);
									switch (int7) {
									case 0: 
										this.addWorldItem("Scalpel", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
										break;
									
									case 1: 
										this.addWorldItem("Bandage", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
										break;
									
									case 2: 
										this.addWorldItem("Pills", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
										break;
									
									case 3: 
										this.addWorldItem("AlcoholWipes", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
										break;
									
									case 4: 
										this.addWorldItem("Bandaid", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
										break;
									
									case 5: 
										this.addWorldItem("CottonBalls", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
										break;
									
									case 6: 
										this.addWorldItem("Disinfectant", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
										break;
									
									case 7: 
										this.addWorldItem("SutureNeedle", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
										break;
									
									case 8: 
										this.addWorldItem("SutureNeedleHolder", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
										break;
									
									case 9: 
										this.addWorldItem("Tweezers", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
										break;
									
									case 10: 
										this.addWorldItem("Gloves_Surgical", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
										break;
									
									case 11: 
										this.addWorldItem("Hat_SurgicalMask_Blue", square, Rand.Next(0.4F, 0.6F), Rand.Next(0.4F, 0.6F), object.getSurfaceOffsetNoTable() / 96.0F);
									
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean roomValid(IsoGridSquare square) {
		return square.getRoom() != null && ("hospitalroom".equals(square.getRoom().getName()) || "clinic".equals(square.getRoom().getName()) || "medical".equals(square.getRoom().getName()));
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return buildingDef.getRoom("medical") != null || buildingDef.getRoom("clinic") != null || boolean1;
	}

	public RBClinic() {
		this.name = "Clinic (Vet, Doctor..)";
		this.setAlwaysDo(true);
	}
}
