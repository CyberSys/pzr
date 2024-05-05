package zombie.randomizedWorld.randomizedBuilding;

import java.io.PrintStream;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public final class RBSpiffo extends RandomizedBuildingBase {

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
								PrintStream printStream = System.out;
								String string = object.getSprite().getName();
								printStream.println("adding item on table " + string + " coords: " + square.x + "," + square.y);
								if (Rand.NextBool(2)) {
									this.addWorldItem("Burger", square, object);
								}

								if (Rand.NextBool(2)) {
									this.addWorldItem("Fries", square, object);
								}

								if (Rand.NextBool(2)) {
									this.addWorldItem("Ketchup", square, object);
								}

								if (Rand.NextBool(3)) {
									this.addWorldItem("Fork", square, object);
								}

								if (Rand.NextBool(3)) {
									this.addWorldItem("ButterKnife", square, object);
								}

								if (Rand.NextBool(30)) {
									this.addWorldItem("MugSpiffo", square, object);
								}

								break;
							}
						}
					}
				}
			}
		}
	}

	public boolean roomValid(IsoGridSquare square) {
		return square.getRoom() != null && ("spiffo_dining".equals(square.getRoom().getName()) || "burgerkitchen".equals(square.getRoom().getName()));
	}

	public boolean isValid(BuildingDef buildingDef, boolean boolean1) {
		return buildingDef.getRoom("spiffo_dining") != null || buildingDef.getRoom("burgerkitchen") != null || boolean1;
	}

	public RBSpiffo() {
		this.name = "Spiffo Restaurant";
		this.setAlwaysDo(true);
	}
}
