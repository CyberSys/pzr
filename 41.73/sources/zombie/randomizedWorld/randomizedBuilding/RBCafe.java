package zombie.randomizedWorld.randomizedBuilding;

import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public final class RBCafe extends RandomizedBuildingBase {

	public void randomizeBuilding(BuildingDef buildingDef) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int1 = buildingDef.x - 1; int1 < buildingDef.x2 + 1; ++int1) {
			for (int int2 = buildingDef.y - 1; int2 < buildingDef.y2 + 1; ++int2) {
				for (int int3 = 0; int3 < 8; ++int3) {
					IsoGridSquare square = cell.getGridSquare(int1, int2, int3);
					if (square != null && this.roomValid(square)) {
						for (int int4 = 0; int4 < square.getObjects().size(); ++int4) {
							IsoObject object = (IsoObject)square.getObjects().get(int4);
							if (Rand.NextBool(2) && this.isTableFor3DItems(object, square)) {
								if (Rand.NextBool(2)) {
									int int5 = Rand.Next(3);
									switch (int5) {
									case 0: 
										this.addWorldItem("Mugl", square, object);
										break;
									
									case 1: 
										this.addWorldItem("MugWhite", square, object);
										break;
									
									case 2: 
										this.addWorldItem("MugRed", square, object);
									
									}
								}

								if (Rand.NextBool(4)) {
									this.addWorldItem("Cupcake", square, object);
								}

								if (Rand.NextBool(4)) {
									this.addWorldItem("CookieJelly", square, object);
								}

								if (Rand.NextBool(4)) {
									this.addWorldItem("CookieChocolateChip", square, object);
								}

								if (Rand.NextBool(4)) {
									this.addWorldItem("Kettle", square, object);
								}

								if (Rand.NextBool(3)) {
									this.addWorldItem("Sugar", square, object);
								}

								if (Rand.NextBool(2)) {
									this.addWorldItem("Teabag2", square, object);
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean roomValid(IsoGridSquare square) {
		return square.getRoom() != null && "cafe".equals(square.getRoom().getName());
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return buildingDef.getRoom("cafe") != null || boolean1;
	}

	public RBCafe() {
		this.name = "Cafe (Seahorse..)";
		this.setAlwaysDo(true);
	}
}
